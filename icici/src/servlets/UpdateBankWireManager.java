import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.BankManager;
import com.manager.GatewayManager;
import com.manager.dao.BankDao;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.ActionVO;
import com.manager.vo.BankWireManagerVO;
import com.manager.vo.gatewayVOs.GatewayAccountVO;
import com.manager.vo.gatewayVOs.GatewayTypeVO;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 10/10/14
 * Time: 3:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateBankWireManager extends HttpServlet
{
    private static Logger logger = new Logger(UpdateBankWireManager.class.getName());
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        HttpSession session = request.getSession();
        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        BankManager bankManager = new BankManager();
        Functions functions=new Functions();
        BankWireManagerVO bankWireManagerVO = null;
        ActionVO actionVO = new ActionVO();
        ValidationErrorList validationErrorList=null;
        CommonFunctionUtil commonFunctionUtil=new CommonFunctionUtil();
        BankDao bankDao=new BankDao();
        String parent_bankwireId = Functions.checkStringNull(request.getParameter("parent_bankwireId")) == null ? "" : request.getParameter("parent_bankwireId");
        RequestDispatcher rdError = request.getRequestDispatcher("/bankWireManager.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        RequestDispatcher rdSuccess = request.getRequestDispatcher("/bankWireManager.jsp?UPDATE=Success&ctoken="+user.getCSRFToken());

        try
        {
            String accountID =request.getParameter("accountid");
            //validation for the parameters
            if("getinfo".equalsIgnoreCase(request.getParameter("action")))
            {
                validationErrorList = new ValidationErrorList();
                if (functions.isValueNull(accountID))
                {
                    if (/*!ESAPI.validator().isValidInput("accountid", accountID, "Numbers", 10, false) || */functions.hasHTMLTags(accountID))
                    {
                        validationErrorList.addError("Generic Exception", new ValidationException("Please provide valid account ID", "Due to internal Issue"));
                        request.setAttribute("error", validationErrorList);
                        rdError.forward(request, response);
                        return;
                    }
                }

                if (functions.isValueNull(request.getParameter("pgtypeid"))){
                    if (functions.hasHTMLTags(request.getParameter("pgtypeid")))
                    {
                        validationErrorList.addError("Generic Exception", new ValidationException("Please provide valid Gateway", "Due to internal Issue"));
                        request.setAttribute("error", validationErrorList);
                        rdError.forward(request, response);
                        return;
                    }
                }

                if (functions.isValueNull(parent_bankwireId)){
                    bankWireManagerVO = bankDao.getBankWireDetailsWithParentBankID(parent_bankwireId);
                    if (bankWireManagerVO == null)
                    {
                        validationErrorList.addError("Generic Exception", new ValidationException("Please provide valid parent_bankwire Id", "Due to internal Issue"));
                        request.setAttribute("error", validationErrorList);
                        rdError.forward(request, response);
                    }
                } else {
                    bankWireManagerVO = bankDao.getPreviousWireDetails(accountID);
                }
                if (bankWireManagerVO != null)
                {
                    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Calendar cal = Calendar.getInstance();
                    try
                    {
                        if (!functions.isValueNull(parent_bankwireId)){
                            cal.setTime(targetFormat.parse(bankWireManagerVO.getDeclinedcoveredupto()));
                            cal.add(Calendar.SECOND, 1);
                            String DeclinedCoveredUpto = targetFormat.format(cal.getTime());
                            cal.setTime(targetFormat.parse(bankWireManagerVO.getReversedCoveredUpto()));
                            cal.add(Calendar.SECOND, 1);
                            String ReversedCoveredUpto = targetFormat.format(cal.getTime());

                            cal.setTime(targetFormat.parse(bankWireManagerVO.getChargebackcoveredupto()));
                            cal.add(Calendar.SECOND, 1);
                            String ChargebackCoveredUpto = targetFormat.format(cal.getTime());

                            cal.setTime(targetFormat.parse(bankWireManagerVO.getRollingreservereleasedateupto()));
                            cal.add(Calendar.SECOND, 1);
                            String RollingReserveReleasedateUpto = targetFormat.format(cal.getTime());

                            String[] decline_timestampStart = commonFunctionUtil.convertTimestampToDateTimePicker(DeclinedCoveredUpto);
                            String[] reverse_timestampStart = commonFunctionUtil.convertTimestampToDateTimePicker(ReversedCoveredUpto);
                            String[] chargeback_timestampStart = commonFunctionUtil.convertTimestampToDateTimePicker(ChargebackCoveredUpto);
                            String[] rollingreserve_timestampStart = commonFunctionUtil.convertTimestampToDateTimePicker(RollingReserveReleasedateUpto);

                            bankWireManagerVO.setDeclinedcoveredStartdate(decline_timestampStart[0]);
                            bankWireManagerVO.setChargebackcoveredStartdate(chargeback_timestampStart[0]);
                            bankWireManagerVO.setReversedCoveredStartdate(reverse_timestampStart[0]);
                            bankWireManagerVO.setRollingreservereleaseStartdate(rollingreserve_timestampStart[0]);

                            bankWireManagerVO.setDeclinedcoveredtimeStarttime(decline_timestampStart[1]);
                            bankWireManagerVO.setChargebackcoveredtimeStarttime(chargeback_timestampStart[1]);
                            bankWireManagerVO.setReversedcoveredtimeStarttime(reverse_timestampStart[1]);
                            bankWireManagerVO.setRollingreservereleaseStarttime(rollingreserve_timestampStart[1]);
                        }
                        // changes as per parent_bankwireId
                        else if (functions.isValueNull(parent_bankwireId) && (parent_bankwireId.equals(bankWireManagerVO.getParent_bankwireid()) || parent_bankwireId.equals(bankWireManagerVO.getBankwiremanagerId())))
                        {
                            if (functions.isValueNull(bankWireManagerVO.getParent_bankwireid())){
                                bankWireManagerVO.setParent_bankwireid(bankWireManagerVO.getParent_bankwireid());
                            }
                            else if (functions.isValueNull(bankWireManagerVO.getBankwiremanagerId()) && parent_bankwireId.equals(bankWireManagerVO.getBankwiremanagerId()))
                            {
                                bankWireManagerVO.setParent_bankwireid(bankWireManagerVO.getBankwiremanagerId());
                            }
                            if (functions.isValueNull(accountID)){
                                BankWireManagerVO bankWireManagerVO1 = bankDao.getPreviousWireDetails(accountID);
                                if (bankWireManagerVO1!=null){
                                    if (functions.isValueNull(bankWireManagerVO1.getCurrency()) && functions.isValueNull(bankWireManagerVO.getCurrency())){
                                        if (!bankWireManagerVO1.getCurrency().equalsIgnoreCase(bankWireManagerVO.getCurrency())){
                                            validationErrorList.addError("Generic Exception", new ValidationException("Kindly Select Same Currency Account ID", "Due to internal Issue"));
                                            request.setAttribute("error", validationErrorList);
                                            rdError.forward(request,response);
                                        }
                                    }
                                }
                            }
                            cal.setTime(targetFormat.parse(bankWireManagerVO.getDeclinedcoveredupto()));
                            String DeclinedCoveredUpto = targetFormat.format(cal.getTime());
                            cal.setTime(targetFormat.parse(bankWireManagerVO.getReversedCoveredUpto()));
                            String ReversedCoveredUpto = targetFormat.format(cal.getTime());

                            cal.setTime(targetFormat.parse(bankWireManagerVO.getChargebackcoveredupto()));
                            String ChargebackCoveredUpto = targetFormat.format(cal.getTime());

                            cal.setTime(targetFormat.parse(bankWireManagerVO.getRollingreservereleasedateupto()));
                            String RollingReserveReleasedateUpto = targetFormat.format(cal.getTime());

                            /*cal.setTime(targetFormat.parse(bankWireManagerVO.getBank_start_date()));
                            String BankStartDate = targetFormat.format(cal.getTime());

                            cal.setTime(targetFormat.parse(bankWireManagerVO.getBank_end_date()));
                            cal.add(Calendar.SECOND, 1);
                            String BankEndDate = targetFormat.format(cal.getTime());*/

                            cal.setTime(targetFormat.parse(bankWireManagerVO.getDeclinedcoveredStartdate()));
                            String DeclinedcoveredStartdate = targetFormat.format(cal.getTime());

                            cal.setTime(targetFormat.parse(bankWireManagerVO.getReversedCoveredStartdate()));
                            String ReversedCoveredStartdate = targetFormat.format(cal.getTime());


                            cal.setTime(targetFormat.parse(bankWireManagerVO.getChargebackcoveredStartdate()));
                            String ChargebackcoveredStartdate = targetFormat.format(cal.getTime());

                            cal.setTime(targetFormat.parse(bankWireManagerVO.getRollingreservereleaseStartdate()));
                            String RollingreservereleaseStartdate = targetFormat.format(cal.getTime());

                            cal.setTime(targetFormat.parse(bankWireManagerVO.getBank_start_date()));
                            String Bank_Start_Date = targetFormat.format(cal.getTime());

                            cal.setTime(targetFormat.parse(bankWireManagerVO.getBank_end_date()));
                            String Bank_End_Date = targetFormat.format(cal.getTime());

                            String[] bankStartTime = commonFunctionUtil.convertTimestampToDateTimePicker(Bank_Start_Date);

                            String[] decline_timestampStarttime = commonFunctionUtil.convertTimestampToDateTimePicker(DeclinedcoveredStartdate);
                            String[] reverse_timestampStarttime = commonFunctionUtil.convertTimestampToDateTimePicker(ReversedCoveredStartdate);
                            String[] chargeback_timestampStarttime = commonFunctionUtil.convertTimestampToDateTimePicker(ChargebackcoveredStartdate);
                            String[] rollingreserve_timestampStarttime = commonFunctionUtil.convertTimestampToDateTimePicker(RollingreservereleaseStartdate);

                            String[] banEndTime = commonFunctionUtil.convertTimestampToDateTimePicker(Bank_End_Date);

                            String[] decline_timestampend = commonFunctionUtil.convertTimestampToDateTimePicker(DeclinedCoveredUpto);
                            String[] reverse_timestampend = commonFunctionUtil.convertTimestampToDateTimePicker(ReversedCoveredUpto);
                            String[] chargeback_timestampend = commonFunctionUtil.convertTimestampToDateTimePicker(ChargebackCoveredUpto);
                            String[] rollingreserve_timestampent = commonFunctionUtil.convertTimestampToDateTimePicker(RollingReserveReleasedateUpto);

                            bankWireManagerVO.setBank_start_date(bankStartTime[0]);
                            bankWireManagerVO.setDeclinedcoveredStartdate(decline_timestampStarttime[0]);
                            bankWireManagerVO.setChargebackcoveredStartdate(chargeback_timestampStarttime[0]);
                            bankWireManagerVO.setReversedCoveredStartdate(reverse_timestampStarttime[0]);
                            bankWireManagerVO.setRollingreservereleaseStartdate(rollingreserve_timestampStarttime[0]);

                            bankWireManagerVO.setBank_start_timestamp(bankStartTime[1]);
                            bankWireManagerVO.setDeclinedcoveredtimeStarttime(decline_timestampStarttime[1]);
                            bankWireManagerVO.setChargebackcoveredtimeStarttime(chargeback_timestampStarttime[1]);
                            bankWireManagerVO.setReversedcoveredtimeStarttime(reverse_timestampStarttime[1]);
                            bankWireManagerVO.setRollingreservereleaseStarttime(rollingreserve_timestampStarttime[1]);

                            bankWireManagerVO.setBank_end_date(banEndTime[0]);
                            bankWireManagerVO.setDeclinedcoveredupto(decline_timestampend[0]);
                            bankWireManagerVO.setChargebackcoveredupto(chargeback_timestampend[0]);
                            bankWireManagerVO.setReversedCoveredUpto(reverse_timestampend[0]);
                            bankWireManagerVO.setRollingreservereleasedateupto(rollingreserve_timestampent[0]);

                            bankWireManagerVO.setBank_end_timestamp(banEndTime[1]);
                            bankWireManagerVO.setDeclinedcoveredtime(decline_timestampend[1]);
                            bankWireManagerVO.setChargebackcoveredtime(chargeback_timestampend[1]);
                            bankWireManagerVO.setReversedcoveredtime(reverse_timestampend[1]);
                            bankWireManagerVO.setRollingreservetime(rollingreserve_timestampent[1]);

                            bankWireManagerVO.setProcessing_amount(bankWireManagerVO.getProcessing_amount());
                            bankWireManagerVO.setGrossAmount(bankWireManagerVO.getGrossAmount());
                            bankWireManagerVO.setNetfinal_amount(bankWireManagerVO.getNetfinal_amount());
                            bankWireManagerVO.setUnpaid_amount(bankWireManagerVO.getUnpaid_amount());
                            bankWireManagerVO.setIsrollingreservereleasewire(bankWireManagerVO.getIsrollingreservereleasewire());
                            bankWireManagerVO.setIspaid(bankWireManagerVO.getIspaid());
                        }
                    }
                    catch (Exception e){
                        logger.error("Exception----",e);
                        request.setAttribute("bankWireManagerVO", bankWireManagerVO);
                        rdError = request.getRequestDispatcher("/bankWireManager.jsp?ctoken=" + user.getCSRFToken());
                        rdError.forward(request, response);
                        return;
                    }
                    request.setAttribute("bankWireManagerVO", bankWireManagerVO);
                    rdError = request.getRequestDispatcher("/bankWireManager.jsp?ctoken=" + user.getCSRFToken());
                    rdError.forward(request, response);
                    return;
                }
                else
                {
                    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String firstTransactionDate = bankDao.getFirstTransactionDate(accountID);
                    bankWireManagerVO=new BankWireManagerVO();
                    try
                    {
                        if (functions.isValueNull(firstTransactionDate))
                        {
                            firstTransactionDate = targetFormat.format(targetFormat.parse(firstTransactionDate));

                            String[] decline_timestampStart = commonFunctionUtil.convertTimestampToDateTimePicker(firstTransactionDate);
                            String[] reverse_timestampStart = commonFunctionUtil.convertTimestampToDateTimePicker(firstTransactionDate);
                            String[] chargeback_timestampStart = commonFunctionUtil.convertTimestampToDateTimePicker(firstTransactionDate);
                            String[] rollingreserve_timestampStart = commonFunctionUtil.convertTimestampToDateTimePicker(firstTransactionDate);

                            bankWireManagerVO.setDeclinedcoveredStartdate(decline_timestampStart[0]);
                            bankWireManagerVO.setChargebackcoveredStartdate(chargeback_timestampStart[0]);
                            bankWireManagerVO.setReversedCoveredStartdate(reverse_timestampStart[0]);
                            bankWireManagerVO.setRollingreservereleaseStartdate(rollingreserve_timestampStart[0]);

                            bankWireManagerVO.setDeclinedcoveredtimeStarttime(decline_timestampStart[1]);
                            bankWireManagerVO.setChargebackcoveredtimeStarttime(chargeback_timestampStart[1]);
                            bankWireManagerVO.setReversedcoveredtimeStarttime(reverse_timestampStart[1]);
                            bankWireManagerVO.setRollingreservereleaseStarttime(rollingreserve_timestampStart[1]);

                            bankWireManagerVO.setDeclinedcoveredStartdate(decline_timestampStart[0]);
                            bankWireManagerVO.setChargebackcoveredStartdate(chargeback_timestampStart[0]);
                            bankWireManagerVO.setReversedCoveredStartdate(reverse_timestampStart[0]);
                            bankWireManagerVO.setRollingreservereleaseStartdate(rollingreserve_timestampStart[0]);

                            bankWireManagerVO.setDeclinedcoveredtimeStarttime(decline_timestampStart[1]);
                            bankWireManagerVO.setChargebackcoveredtimeStarttime(chargeback_timestampStart[1]);
                            bankWireManagerVO.setReversedcoveredtimeStarttime(reverse_timestampStart[1]);
                            bankWireManagerVO.setRollingreservereleaseStarttime(rollingreserve_timestampStart[1]);
                        }
                    }catch (Exception e){
                        logger.error("Exception----", e);
                        request.setAttribute("bankWireManagerVO", bankWireManagerVO);
                        rdError = request.getRequestDispatcher("/bankWireManager.jsp?ctoken=" + user.getCSRFToken());
                        rdError.forward(request, response);
                        return;
                    }
                }
                request.setAttribute("bankWireManagerVO",bankWireManagerVO);
                rdError = request.getRequestDispatcher("/bankWireManager.jsp?ctoken=" + user.getCSRFToken());
                rdError.forward(request, response);
                return;
            }

            validationErrorList=validateMandatoryParameter(request);
            if(request.getParameter("action").contains("_Add"))
            {
                validateAddParameters(request,validationErrorList);
            }
            else
            {
                logger.debug("inside else condition of add action");
                validateUpdateParameter(request,validationErrorList);
            }
            if(!validationErrorList.isEmpty())
            {
                if(session.getAttribute("singleBankWireManagerVO")!=null)
                {
                    actionVO.setEdit();
                    request.setAttribute("actionVO",actionVO);
                    bankWireManagerVO= (BankWireManagerVO) session.getAttribute("singleBankWireManagerVO");
                    request.setAttribute("singleBankWireManagerVO",bankWireManagerVO);
                    request.setAttribute("bankWireManagerVO",bankWireManagerVO);
                }
                request.setAttribute("error", validationErrorList);
                rdError.forward(request,response);
                return;
            }
            try
            {
                String settledate = request.getParameter("settlementdate");
                if((functions.isValueNull(request.getParameter("action")) && "_Update".contains(request.getParameter("action"))) && !functions.isValueNull(settledate) && functions.isValueNull(parent_bankwireId))
                {
                    request.setAttribute("error", "Please update the Parent BankWire before proceeding with this update");
                }

                String rollingreserveStartdate = request.getParameter("rollingreserveStartdate") + " " + request.getParameter("rollingReleaseTimeStart");
                String rollingreservedateupto = request.getParameter("rollingreservedateupto") + " " + request.getParameter("rollingRelease_Time");
                Timestamp timestamp1 = convertStringToTimestamp(rollingreserveStartdate);
                Timestamp timestamp2 = convertStringToTimestamp(rollingreservedateupto);
               /* String settledate = request.getParameter("settlementdate");
                System.out.println("parent_bankwireId :::"+parent_bankwireId);
*/
                if(!functions.isValueNull(settledate) && functions.isValueNull(parent_bankwireId))
                {
                    System.out.println("inside first if updateBankWireManager ");
//                    validationErrorList.addError("Generic Exception", new ValidationException("Please update the Parent BankWire before proceeding with this update","Please update the Parent BankWire before proceeding with this update"));
//                    System.out.println("validationErrorList::::::"+validationErrorList.toString());
                    request.setAttribute("error", "Please update the Parent BankWire before proceeding with this update");
                   /* rdError.forward(request,response);
                    return;*/
                }

                logger.error("rollingreserve Startdate---->"+timestamp1+"----- End Date"+timestamp2);
                if (timestamp1.after(timestamp2))
                {
                    validationErrorList.addError("rollingreserve", new ValidationException("Rolling Reserve Start Date is greater than Rolling Reserve Date Upto ", "Rolling Reserve Start Date is greater than Rolling Reserve Date Upto "));
                }
                String declinedcoveredStartdate = request.getParameter("declinedcoveredStartdate") + " " + request.getParameter("declinedcoveredtimeStart");
                String declinedcoveredupto = request.getParameter("declinedcoveredupto") + "  " + request.getParameter("declinedcoveredtime");
                timestamp1 = convertStringToTimestamp(declinedcoveredStartdate);
                timestamp2 = convertStringToTimestamp(declinedcoveredupto);
                logger.error("declinedcovered Startdate---->"+timestamp1+"----- End Date"+timestamp2);
                if (timestamp1.after(timestamp2))
                {
                    validationErrorList.addError("declinedcovered", new ValidationException("Declined Covered Start Date is greater thean Declined Covered Date Upto ", "Declined Covered Start Date is greater than Declined Covered Date Upto "));
                }
                String chargebackcovereduptoStartdate = request.getParameter("chargebackcovereduptoStartdate") + " " + request.getParameter("chargebackcoveredtimeStart");
                String chargebackcoveredupto = request.getParameter("chargebackcoveredupto") + " " + request.getParameter("chargebackcoveredtime");
                timestamp1 = convertStringToTimestamp(chargebackcovereduptoStartdate);
                timestamp2 = convertStringToTimestamp(chargebackcoveredupto);
                logger.error("chargebackcovered Startdate---->"+timestamp1+"----- End Date"+timestamp2);
                if (timestamp1.after(timestamp2))
                {
                    validationErrorList.addError("chargebackcovered", new ValidationException("Chargeback Covered Start Date is greater thean Chargeback Covered Date Upto ", "Chargeback Covered Start Date is greater than Chargeback Covered Date Upto "));
                }
                String  reversedcovereduptoStartdate = request.getParameter("reversedcovereduptoStartdate") + " " + request.getParameter("reversedcoveredtimeStart");
                String reversedcoveredupto = request.getParameter("reversedcoveredupto") + " " + request.getParameter("reversedcoveredtime");
                timestamp1 = convertStringToTimestamp(reversedcovereduptoStartdate);
                timestamp2 = convertStringToTimestamp(reversedcoveredupto);
                logger.error("reversedcovered Startdate---->"+timestamp1+"----- End Date"+timestamp2);
                if (timestamp1.after(timestamp2))
                {
                    validationErrorList.addError("reversedcovered", new ValidationException("Reversed Covered Start Date is greater than Reversed Covered Date Upto ", "Reversed Covered Start Date is greater than Reversed Covered Date Upto "));
                }
            }
            catch (Exception e)
            {
                logger.error("Exception----->"+e.getMessage());
            }
            BankWireManagerVO bankWireManagerVO1= null;
            bankWireManagerVO=new BankWireManagerVO();

            String settleddate ="";
            if (functions.isValueNull(request.getParameter("action")) && request.getParameter("action").contains("_Update") && functions.isValueNull(parent_bankwireId))
            {
                settleddate = bankDao.getparentSettleDate(parent_bankwireId);
                if (!functions.isValueNull(settleddate))
                {
                    validationErrorList.addError("Error", new ValidationException("Please update the Parent BankWire before proceeding with this update","Please update the Parent BankWire before proceeding with this update"));
                    request.setAttribute("error", validationErrorList);
                }
            }

            bankWireManagerVO.setDeclinedcoveredStartdate(request.getParameter("declinedcoveredStartdate"));
            bankWireManagerVO.setChargebackcoveredStartdate(request.getParameter("chargebackcovereduptoStartdate"));
            bankWireManagerVO.setReversedCoveredStartdate(request.getParameter("reversedcovereduptoStartdate"));
            bankWireManagerVO.setRollingreservereleaseStartdate(request.getParameter("rollingreserveStartdate"));

            bankWireManagerVO.setDeclinedcoveredtimeStarttime(request.getParameter("declinedcoveredtimeStart"));
            bankWireManagerVO.setChargebackcoveredtimeStarttime(request.getParameter("chargebackcoveredtimeStart"));
            bankWireManagerVO.setReversedcoveredtimeStarttime(request.getParameter("reversedcoveredtimeStart"));
            bankWireManagerVO.setRollingreservereleaseStarttime(request.getParameter("rollingReleaseTimeStart"));
            if(!validationErrorList.isEmpty())
            {
                if(session.getAttribute("singleBankWireManagerVO")!=null)
                {
                    actionVO.setEdit();
                    request.setAttribute("actionVO",actionVO);
                    bankWireManagerVO= (BankWireManagerVO) session.getAttribute("singleBankWireManagerVO");
                    request.setAttribute("singleBankWireManagerVO",bankWireManagerVO);
                    request.setAttribute("bankWireManagerVO",bankWireManagerVO);
                }
                request.setAttribute("error", validationErrorList);
                rdError.forward(request,response);
                return;
            }

            actionVO.setActionCriteria(request.getParameter("action"));
            actionVO.setYesOrNoCriteria(request.getParameter("isdaylight"));
            bankWireManagerVO.setBankwiremanagerId(request.getParameter("bankwiremangerid"));
            bankWireManagerVO.setSettleddate(request.getParameter("settlementdate"));
//            bankWireManagerVO.setParent_bankwireid(request.getParameter("parent_bankwireId"));
            if (!"0".equalsIgnoreCase(request.getParameter("pgtypeid")))
                bankWireManagerVO.setPgtypeId(request.getParameter("pgtypeid"));

            if (!"0".equalsIgnoreCase(accountID))
                bankWireManagerVO.setAccountId(accountID);
            bankWireManagerVO.setBank_start_date(request.getParameter("expected_startDate"));
            bankWireManagerVO.setBank_end_date(request.getParameter("expected_endDate"));
            bankWireManagerVO.setServer_start_date(request.getParameter("actual_startDate"));
            bankWireManagerVO.setServer_end_date(request.getParameter("actual_endDate"));
            bankWireManagerVO.setProcessing_amount(request.getParameter("processingamount"));
            bankWireManagerVO.setGrossAmount(request.getParameter("grossamount"));
            bankWireManagerVO.setNetfinal_amount(request.getParameter("netfinalamout"));
            bankWireManagerVO.setUnpaid_amount(request.getParameter("unpaidamount"));
            bankWireManagerVO.setIsrollingreservereleasewire(request.getParameter("isrollingreservereleasewire"));
            bankWireManagerVO.setRollingreservereleasedateupto(request.getParameter("rollingreservedateupto")) ;
            bankWireManagerVO.setDeclinedcoveredupto(request.getParameter("declinedcoveredupto"));
            bankWireManagerVO.setChargebackcoveredupto(request.getParameter("chargebackcoveredupto"));
            bankWireManagerVO.setReversedCoveredUpto(request.getParameter("reversedcoveredupto"));
            bankWireManagerVO.setBanksettlement_report_file(request.getParameter("settlement_report_file"));
            bankWireManagerVO.setBanksettlement_transaction_file(request.getParameter("settlement_transaction_file"));
            bankWireManagerVO.setSettlementCronExceuted(request.getParameter("issettlementcron"));
            bankWireManagerVO.setPayoutCronExcuted(request.getParameter("ispayoutcron"));
            bankWireManagerVO.setIsPartnerCommCronExecuted(request.getParameter("ispartnercommcronexecuted"));
            bankWireManagerVO.setIsAgentCommCronExecuted(request.getParameter("isagentcommcronexecuted"));
            bankWireManagerVO.setIspaid(request.getParameter("isPaid"));
            bankWireManagerVO.setBank_start_timestamp(request.getParameter("expected_startTime"));
            bankWireManagerVO.setBank_end_timestamp(request.getParameter("expected_endTime"));
            bankWireManagerVO.setServer_start_timestamp(request.getParameter("actual_startTime"));
            bankWireManagerVO.setServer_end_timestamp(request.getParameter("actual_endTime"));
            bankWireManagerVO.setSettled_timestamp(request.getParameter("settlementtime"));
            bankWireManagerVO.setDeclinedcoveredtime(request.getParameter("declinedcoveredtime"));
            bankWireManagerVO.setChargebackcoveredtime(request.getParameter("chargebackcoveredtime"));
            bankWireManagerVO.setReversedcoveredtime(request.getParameter("reversedcoveredtime"));
            bankWireManagerVO.setRollingreservetime(request.getParameter("rollingRelease_Time"));

//-----------------need to add inside account id loop
           /* GatewayAccountVO gatewayAccountVO=gatewayManager.getGatewayAccountForAccountId(bankWireManagerVO.getAccountId());
            GatewayTypeVO gatewayTypeVO=gatewayManager.getGatewayTypeForPgTypeId(gatewayAccountVO.getGatewayAccount().getPgTypeId());
            System.out.println("gatewayTypeVo::::::"+gatewayTypeVO);

            bankWireManagerVO.setPgtypeId(gatewayAccountVO.getGatewayAccount().getPgTypeId());
            bankWireManagerVO.setCurrency(gatewayAccountVO.getGatewayAccount().getCurrency());
            bankWireManagerVO.setMid(gatewayAccountVO.getGatewayAccount().getMerchantId());*/
            if (functions.isValueNull(parent_bankwireId))
            {
                bankWireManagerVO1 = bankDao.getBankWireDetailsWithParentBankID(parent_bankwireId);
                if (bankWireManagerVO1 == null){
                    validationErrorList.addError("Generic Exception", new ValidationException("Please provide valid parent_bankwire Id", "Due to internal Issue"));
                    request.setAttribute("error", validationErrorList);
                    rdError.forward(request,response);
                }
                if (bankWireManagerVO1!=null){
                    if (functions.isValueNull(accountID)){
                        BankWireManagerVO bankWireManagerVO2 = bankDao.getPreviousWireDetails(accountID);
                        if (bankWireManagerVO2!=null){
                            if (functions.isValueNull(bankWireManagerVO2.getCurrency()) && functions.isValueNull(bankWireManagerVO1.getCurrency())){
                                if (!bankWireManagerVO2.getCurrency().equalsIgnoreCase(bankWireManagerVO1.getCurrency())){
                                    validationErrorList.addError("Generic Exception", new ValidationException("Kindly Select Same Currency Account ID", "Due to internal Issue"));
                                    request.setAttribute("error", validationErrorList);
                                    rdError.forward(request,response);
                                }
                            }
                        }
                    }
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    if (!functions.isValueNull(request.getParameter("actual_startDate"))){
                        cal.setTime(targetFormat.parse(bankWireManagerVO1.getServer_start_date()));
                        String Server_Start_Date = targetFormat.format(cal.getTime());

                        cal.setTime(targetFormat.parse(bankWireManagerVO1.getServer_end_date()));
                        String Server_End_Date = targetFormat.format(cal.getTime());
                        String[] serverStartTime = commonFunctionUtil.convertTimestampToDateTimePicker(Server_Start_Date);
                        String[] serverEndTime = commonFunctionUtil.convertTimestampToDateTimePicker(Server_End_Date);
                        bankWireManagerVO.setServer_start_date(serverStartTime[0]);
                        bankWireManagerVO.setServer_start_timestamp(serverStartTime[1]);
                        bankWireManagerVO.setServer_end_date(serverEndTime[0]);
                        bankWireManagerVO.setServer_end_timestamp(serverEndTime[1]);
                    }
                    if (!functions.isValueNull(request.getParameter("settlementdate")) && functions.isValueNull(settleddate)){
                       /* Calendar cal = Calendar.getInstance();
                        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");*/

                            cal.setTime(targetFormat.parse(settleddate));
                            String setteleDate = targetFormat.format(cal.getTime());
                            String[] setteleTime = commonFunctionUtil.convertTimestampToDateTimePicker(setteleDate);
                            bankWireManagerVO.setSettleddate(setteleTime[0]);
                            bankWireManagerVO.setSettled_timestamp(setteleTime[1]);

                    }
                }
            }
            else if (functions.isValueNull(bankWireManagerVO.getAccountId()))
            {
                bankWireManagerVO1 = bankDao.getPreviousWireDetails(bankWireManagerVO.getAccountId());
            }
            if (bankWireManagerVO1!=null){
                if (functions.isValueNull(parent_bankwireId) && !"0".equalsIgnoreCase(parent_bankwireId))
                {
                    if (parent_bankwireId.equals(bankWireManagerVO1.getParent_bankwireid())){
                        bankWireManagerVO.setParent_bankwireid(bankWireManagerVO1.getParent_bankwireid());}
                    else if (parent_bankwireId.equals(bankWireManagerVO1.getBankwiremanagerId())){
                        bankWireManagerVO.setParent_bankwireid(bankWireManagerVO1.getBankwiremanagerId());
                    }
                }/*else {
                    bankWireManagerVO.setParent_bankwireid(bankWireManagerVO.getBankwiremanagerId());
                }*/
            }

            if (functions.isValueNull(bankWireManagerVO.getAccountId()) && bankWireManagerVO.getAccountId().contains(",")){
                String[] accIds = bankWireManagerVO.getAccountId().split(",");
                if (accIds.length!=0 && accIds.length>=50){
                    validationErrorList.addError("Generic Exception", new ValidationException("Kindly Select Maximum 50 Accounts At a Time","Maximum Account Length Issue"));
                    request.setAttribute("error", validationErrorList);
                    rdError.forward(request,response);
                    return;
                }
            }
            String b=bankManager.updateBankWireManager(bankWireManagerVO, actionVO/*, gatewayTypeVO*/);
            if(functions.isValueNull(b))
            {
                if (b.equals("true") || b.equals("false")){
                    b = "";
                }
                request.setAttribute("result",b);
                request.setAttribute("actionVO",actionVO);
                request.setAttribute("singleBankWireManagerVO",bankWireManagerVO);
                request.setAttribute("bankWireManagerVO",bankWireManagerVO);
                rdSuccess.forward(request,response);
            }
            else
            {
                validationErrorList = new ValidationErrorList();
                validationErrorList.addError("System Error",new ValidationException("System Error","Due to internal Issue"));
                request.setAttribute("singleBankWireManagerVO",bankWireManagerVO);
                request.setAttribute("bankWireManagerVO",bankWireManagerVO);
                request.setAttribute("error",validationErrorList);
                rdError.forward(request,response);
            }
        }
        catch (Exception e)
        {
            validationErrorList = new ValidationErrorList();
            validationErrorList.addError("Generic Exception",new ValidationException("Please enter appropriate parameter","Due to internal Issue"));
            request.setAttribute("error",validationErrorList);
            request.setAttribute("actionVO",actionVO);
            request.setAttribute("singleBankWireManagerVO",bankWireManagerVO);
            request.setAttribute("bankWireManagerVO",bankWireManagerVO);
            rdError.forward(request,response);
            logger.error("main class exception ::",e);
        }

    }
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request, response);
    }

    private ValidationErrorList validateMandatoryParameter(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        /*inputFieldsListMandatory.add(InputFields.PGTYPEID);*/
        //inputFieldsListMandatory.add(InputFields.ACCOUNTID_SMALL);
        inputFieldsListMandatory.add(InputFields.EXPECTED_STARTDATE);
        inputFieldsListMandatory.add(InputFields.EXPECTED_ENDDATE);
        inputFieldsListMandatory.add(InputFields.PROCESSINGAMOUNT);
        inputFieldsListMandatory.add(InputFields.GROSSAMOUNT);
        inputFieldsListMandatory.add(InputFields.NETFINALAMOUNT);
        inputFieldsListMandatory.add(InputFields.UNPAIDAMOUNT);
        inputFieldsListMandatory.add(InputFields.ISROLLINGRESERVERELAEASEWIRE);
        inputFieldsListMandatory.add(InputFields.ROLLINGRESERVEDATEUPTO);
        inputFieldsListMandatory.add(InputFields.ROLLINGRELEASETIME);
        inputFieldsListMandatory.add(InputFields.DECLINEDCOVEREDDATEUPTO);
        inputFieldsListMandatory.add(InputFields.DECLINECOVERTIME);
        inputFieldsListMandatory.add(InputFields.CHARGEBACKCOVEREDDATEUPTO);
        inputFieldsListMandatory.add(InputFields.CHARGEBACKCOVEREDTIME);
        inputFieldsListMandatory.add(InputFields.REVERSEDCOVEREDDATEUPTO);
        inputFieldsListMandatory.add(InputFields.REVERSECOVEREDTIME);
        inputFieldsListMandatory.add(InputFields.ISPAID);
        //temporary details
        inputFieldsListMandatory.add(InputFields.EXPECTED_STARTTIME);
        inputFieldsListMandatory.add(InputFields.EXPECTED_ENDTIME);
        //till here
        inputFieldsListMandatory.add(InputFields.SMALL_ACTION);

        ValidationErrorList validationErrorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListMandatory,validationErrorList,false);

        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.SETTLEMENTREPORTFILE);
        inputFieldsListOptional.add(InputFields.SETTLEMENTTRANSACTIONFILE);
        inputValidator.InputValidations(req,inputFieldsListOptional,validationErrorList,true);
        return validationErrorList;
    }
    private void validateUpdateParameter(HttpServletRequest request,ValidationErrorList validationErrorList)
    {
        InputValidator inputValidator = new InputValidator();
        Functions functions = new Functions();

        List<InputFields> inputFieldsListCondition = new ArrayList<InputFields>();
        if (!functions.isValueNull(request.getParameter("parent_bankwireId"))) {
            inputFieldsListCondition.add(InputFields.SETTLEMENTDATE);
            inputFieldsListCondition.add(InputFields.SETTLEMENTIME);
        }
        inputFieldsListCondition.add(InputFields.BANKWIREMANGERID);
        inputFieldsListCondition.add(InputFields.ACTUAL_SARTDATE);
        inputFieldsListCondition.add(InputFields.ACTUAL_ENDDATE);
        inputFieldsListCondition.add(InputFields.ISSETTLEMENTCRONEXECCUTED);
        inputFieldsListCondition.add(InputFields.ISPAYOUCRONEXECUTED);
        //temporary has to be removed
        inputFieldsListCondition.add(InputFields.ACTUAL_STARTTIME);
        inputFieldsListCondition.add(InputFields.ACTUAL_ENDTIME);

        inputValidator.InputValidations(request,inputFieldsListCondition,validationErrorList,false);
        //return validationErrorList;
    }
    private void validateAddParameters(HttpServletRequest request,ValidationErrorList validationErrorList)
    {
        InputValidator inputValidator = new InputValidator();

        List<InputFields> inputFieldsListCondition = new ArrayList<InputFields>();
        inputFieldsListCondition.add(InputFields.ISDAYLIGHT);


        inputValidator.InputValidations(request,inputFieldsListCondition,validationErrorList,false);
        //return validationErrorList;
    }
    private Timestamp convertStringToTimestamp(String date) throws ParseException
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        Date parsedDate = null;
        parsedDate = dateFormat.parse(date);
        Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
        return timestamp;
    }
}