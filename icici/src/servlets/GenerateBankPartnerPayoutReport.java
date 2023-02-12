import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.manager.ChargeManager;
import com.manager.GatewayManager;
import com.manager.PayoutManager;
import com.manager.dao.PartnerDAO;
import com.manager.dao.PayoutDAO;
import com.manager.enums.*;
import com.manager.vo.*;
import com.manager.vo.gatewayVOs.GatewayTypeVO;
import com.manager.vo.payoutVOs.*;

import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
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

import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 2/10/15
 * Time: 6:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class GenerateBankPartnerPayoutReport extends HttpServlet
{
    Logger logger=new Logger(GenerateBankPartnerPayoutReport.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        processRequest(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {

        processRequest(request,response);
    }
    public void processRequest(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        logger.debug("Entering into GenerateBankPartnerPayoutReport Report ");
        HttpSession session = request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        Functions functions = new Functions();


        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
        }

        RequestDispatcher rd=request.getRequestDispatcher("/bankPartnerPayoutReport.jsp?ctoken=" + user.getCSRFToken());
        StringBuffer  sb=new StringBuffer();
        ValidationErrorList validationErrorList=validateMandatoryParameter(request);
        validateOptionalParameter(request,validationErrorList);
        if(!validationErrorList.isEmpty())
        {
            logger.debug("Invalid Input Data Value");
            for(ValidationException validationException:validationErrorList.errors())
            {
                sb.append(validationException.getMessage());
                sb.append("<BR>");
            }
            request.setAttribute("errormsg",sb.toString());
            rd.forward(request, response);
        }
        String partnerId=request.getParameter("partnerid");
        String pgTypeId="";
        String accountId=request.getParameter("accountid");
        String reportType=request.getParameter("reporttype");
        String errormsg=null;
        if (functions.isValueNull(request.getParameter("gateway")))
        {
            String gateway[] = request.getParameter("gateway").split(" ");
            pgTypeId = gateway[2];
        }

        double totalChargesAmount=0.00;
        long vCntTotal = 0;
        long totalPaidWireCount=0;

        double vDblTotalAmount = 0.00;
        double vDblTotalBalanceAmount=0.00;
        double vDblWirePaidAmount=0.00;
        double vDblWireUnPaidAmount=0.00;
        double vDblWireBalanceAmount=0.00;
        double cDblTotalFundedToBank=0.00;
        double vDblWireAmount=0.00;
        double vDblWireFeeTotal=0.00;
        double vDblGrossChargesTotal=0.00;

        PayoutManager payoutManager=new PayoutManager();
        BankPartnerPayoutReportVO bankPartnerPayoutReportVO=new BankPartnerPayoutReportVO();
        String bankAgentReportType=payoutManager.getBankPartnerReportType(partnerId, pgTypeId, accountId);
        if("wireReport".equals(bankAgentReportType))
        {
            bankPartnerPayoutReportVO.setWireReport(true);
            GatewayType  gatewayType= null;
            try
            {
                PayoutDAO payoutDAO=new PayoutDAO();
                PartnerDAO partnerDAO=new PartnerDAO();
                WireChargeVO wireChargeVO=null;
                List<ChargeDetailsVO> grossChargeVOList=new ArrayList<ChargeDetailsVO>();
                SettlementDateVO settlementDateVO=null;
                //1:Check agent-bank configuration
                PartnerDetailsVO partnerDetailsVO=partnerDAO.getPartnerDetails(partnerId);
                if(partnerDetailsVO!=null)
                {
                    GatewayManager gatewayManager=new GatewayManager();
                    ChargeManager chargeManager=new ChargeManager();
                    boolean isPartnerRefereedToBank = gatewayManager.isPartnerRefereedToBank(partnerId, pgTypeId);
                    if(isPartnerRefereedToBank)
                    {
                        //2:Get bank table name form accountId
                        GatewayTypeVO gatewayTypeVO=gatewayManager.getGatewayTypeForPgTypeId(pgTypeId);
                        gatewayType=gatewayTypeVO.getGatewayType();

                        String tableName= Database.getTableName(gatewayType.getGateway());
                        GatewayAccount  gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);

                        WireAmountVO wireAmountVO = new WireAmountVO();

                        payoutDAO.setBankPartnerWireAmountVO(partnerId, gatewayAccount, wireAmountVO);
                        payoutDAO.setBankPartnerWireUnpaidAmount(partnerId, gatewayAccount, wireAmountVO);

                        vDblWirePaidAmount=wireAmountVO.getPaidAmount();
                        vDblWireBalanceAmount=wireAmountVO.getUnpaidBalanceAmount();

                        totalPaidWireCount=wireAmountVO.getWireCount();
                        //Step:pick settlement per account settlement date
                        if(gatewayType.getPgTypeId().equalsIgnoreCase(gatewayAccount.getPgTypeId()))
                        {
                            //3:Load the charges On gateway account with agent charge value
                            List<ChargeVO> chargeVOList=chargeManager.getGatewayAccountCharges(gatewayAccount);
                            if(chargeVOList!=null && chargeVOList.size()>0)
                            {
                                //4:Get All the transaction processed through account
                                String gatewayAccountFirstTransDate=gatewayManager.getGatewayAccountFirstTrans(accountId,tableName);
                                if(functions.isValueNull(gatewayAccountFirstTransDate))
                                {
                                    settlementDateVO=payoutManager.getSettlementDateOnGatewayAccount(gatewayAccount,tableName);
                                    settlementDateVO.setSettlementStartDate(gatewayAccountFirstTransDate);
                                    settlementDateVO.setDeclinedStartDate(gatewayAccountFirstTransDate);
                                    settlementDateVO.setReversedStartDate(gatewayAccountFirstTransDate);
                                    settlementDateVO.setChargebackStartDate(gatewayAccountFirstTransDate);

                                    TransactionSummaryVO transactionSummaryVO=payoutDAO.getGatewayAccountProcessedTrans(gatewayAccount,tableName);

                                    String currentDate=Functions.convertDateDBFormat(Calendar.getInstance().getTime());
                                    List<ChargeDetailsVO> detailsVOList=new ArrayList<ChargeDetailsVO>();
                                    ChargeDetailsVO chargeDetailsVO=null;
                                    for(ChargeVO  chargeVO:chargeVOList)
                                    {
                                        //5:Calculate the payout
                                        if(Charge_keyword.TotalReserveGenerated.toString().equals(chargeVO.getKeyword()) || Charge_keyword.TotalReserveRefunded.toString().equals(chargeVO.getKeyword()) || Charge_keyword.CalculatedReserveRefund.toString().equals(chargeVO.getKeyword()))
                                        {
                                            logger.debug("Skipping "+chargeVO.getChargename()+" From The Bank Agent Payout Report");
                                            continue;
                                        }
                                        else
                                        {
                                            //5.1:Get The Current charge rate form version store
                                            String bankPartnerVersionRate=chargeManager.getBankPartnerChargeVersionRate(chargeVO.getChargeid(),currentDate);
                                            if(bankPartnerVersionRate!=null)
                                            {
                                                chargeVO.setPartnerChargeValue(bankPartnerVersionRate);
                                            }

                                            if(Charge_category.Others.toString().equals(chargeVO.getCategory()) && Charge_keyword.Wire.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
                                            {
                                                 wireChargeVO=new WireChargeVO();
                                                wireChargeVO.setChargeValue(chargeVO.getPartnerChargeValue());
                                                wireChargeVO.setChargeName(chargeVO.getChargename());
                                                continue;
                                            }
                                            else if(Charge_category.Others.toString().equals(chargeVO.getCategory()) && Charge_keyword.GrossBalanceAmount.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
                                            {
                                                chargeDetailsVO.setChargeValue(chargeVO.getPartnerChargeValue());
                                                chargeDetailsVO.setValueType(chargeVO.getValuetype());
                                                grossChargeVOList.add(chargeDetailsVO);
                                                continue;
                                            }
                                            else
                                            {
                                                chargeDetailsVO=payoutManager.calculateGatewayAccountChargesForPartner(chargeVO, transactionSummaryVO);
                                                if(chargeDetailsVO!=null)
                                                {
                                                    totalChargesAmount=totalChargesAmount+chargeDetailsVO.getTotal();
                                                    detailsVOList.add(chargeDetailsVO);
                                                }
                                            }

                                        }

                                    }

                                    double vDblDeduct=totalChargesAmount;
                                    vDblTotalBalanceAmount=vDblDeduct;

                                    List<GrossChargeVO> grossTypeChargeVOList=new ArrayList<GrossChargeVO>();
                                    GrossChargeVO grossChargeVO=null;
                                    for(ChargeDetailsVO grossTypeDetailsVO:grossTypeChargeVOList)
                                    {
                                        double vDblGrossAmount=vDblTotalBalanceAmount;
                                        double vDblGrossChargesAmount=0.00;
                                        grossChargeVO=new GrossChargeVO();

                                        vDblGrossChargesAmount=(vDblTotalBalanceAmount*Double.valueOf(grossTypeDetailsVO.getChargeValue())/100);
                                        if(vDblGrossChargesAmount<0)
                                        {
                                            vDblGrossChargesAmount=0.00;
                                        }
                                        vDblGrossChargesTotal=vDblGrossChargesTotal+vDblGrossChargesAmount;

                                        if(Charge_unit.Percentage.toString().equals(grossTypeDetailsVO.getValueType()))
                                        {
                                            grossChargeVO.setChargeValue(grossTypeDetailsVO.getChargeValue()+"%");
                                        }
                                        else
                                        {
                                            grossChargeVO.setChargeValue(grossTypeDetailsVO.getChargeValue());
                                        }
                                        grossChargeVO.setChargeName(grossTypeDetailsVO.getChargeName());
                                        grossChargeVO.setCount(0);
                                        grossChargeVO.setAmount(vDblGrossAmount);
                                        grossChargeVO.setTotal(vDblGrossChargesAmount);
                                        grossTypeChargeVOList.add(grossChargeVO);
                                    }
                                    if(wireChargeVO!=null)
                                    {
                                        vDblWireFeeTotal=totalPaidWireCount*Double.parseDouble(wireChargeVO.getChargeValue());
                                        wireChargeVO.setCount(totalPaidWireCount);
                                        wireChargeVO.setAmount(0.00);
                                        wireChargeVO.setTotal(vDblWireFeeTotal);
                                    }

                                    cDblTotalFundedToBank=(totalChargesAmount+vDblWireBalanceAmount)-(vDblWirePaidAmount+vDblWireFeeTotal+vDblGrossChargesTotal);

                                    //cDblTotalFundedToBank=totalChargesAmount-(vDblWireFeeTotal+vDblGrossChargesTotal);
                                    //6:Call appropriate view by passing calculated agent data

                                    bankPartnerPayoutReportVO.setGatewayAccount(gatewayAccount);
                                    bankPartnerPayoutReportVO.setGatewayType(gatewayType);
                                    bankPartnerPayoutReportVO.setReportingDate(Functions.convertDateDBFormat(Calendar.getInstance().getTime()));
                                    bankPartnerPayoutReportVO.setChargesDetailsVOs(detailsVOList);
                                    bankPartnerPayoutReportVO.setSettlementDateVO(settlementDateVO);
                                    bankPartnerPayoutReportVO.setGrossTypeChargeVOList(grossTypeChargeVOList);
                                    bankPartnerPayoutReportVO.setWireChargeVO(wireChargeVO);
                                    bankPartnerPayoutReportVO.setPartnerTotalChargesAmount(totalChargesAmount);
                                    bankPartnerPayoutReportVO.setPartnerTotalFundedAmount(cDblTotalFundedToBank);
                                    bankPartnerPayoutReportVO.setPartnerWirePaidAmount(vDblWirePaidAmount);
                                    bankPartnerPayoutReportVO.setPartnerTotalBalanceAmount(vDblWireBalanceAmount);
                                    bankPartnerPayoutReportVO.setPartnerDetailsVO(partnerDetailsVO);

                                    request.setAttribute("bankPartnerPayoutReportVO",bankPartnerPayoutReportVO);
                                    rd.forward(request,response);
                                }
                                else
                                {
                                    errormsg="Transaction Not Found On Gateway Account.";
                                    logger.debug(errormsg);
                                    request.setAttribute("errormsg",errormsg);
                                    rd.forward(request,response);
                                }

                            }
                            else
                            {
                                errormsg="Charges Not Mapped On Gateway Account.";
                                logger.debug(errormsg);
                                request.setAttribute("errormsg",errormsg);
                                rd.forward(request,response);
                            }
                        }
                        else
                        {
                            errormsg="Invalid GatewayAccount "+gatewayAccount.getAccountId()+"("+gatewayAccount.getMerchantId()+")  For Bank "+gatewayType.getGateway();
                            logger.debug(errormsg);
                            request.setAttribute("errormsg",errormsg);
                            rd.forward(request, response);
                        }

                    }
                    else
                    {
                        errormsg="Invalid Partner-Bank Configuration ";
                        logger.debug(errormsg);
                        request.setAttribute("errormsg",errormsg);
                        rd.forward(request, response);
                    }
                }
                else
                {
                    errormsg="Invalid/Inactive Partner";
                    logger.debug(errormsg);
                    request.setAttribute("errormsg",errormsg);
                    rd.forward(request, response);
                }

            }
            catch (PZDBViolationException dbe)
            {
                logger.error("Sql exception while connecting to Db or due to incorrect query::", dbe);
                PZExceptionHandler.handleDBCVEException(dbe,null, "Sql exception while generating bank agent payout report");
                request.setAttribute("errormsg","Sql exception while generating bank agent payout report");
                rd.forward(request,response);
            }
            catch (Exception e)
            {
                logger.error("Exception:::::::"+e);
                logger.debug("Forwarding to bankAgentPayoutReport");
                rd.forward(request,response);
            }
        }
        else
        {
            HashMap<String,HashMap<String,List<ChargeDetailsVO>>>  stringHashMapHashMap=new HashMap<String, HashMap<String, List<ChargeDetailsVO>>>();
            RequestDispatcher rd1=request.getRequestDispatcher("/bankPartnerConsolidatePayoutSummary.jsp?ctoken="+user.getCSRFToken());
            PartnerDetailsVO partnerDetailsVO=null;
            try
            {
                double USDChargeValue=0.00;
                double INRChargeAmount=0.00;
                double EURChargeAmount=0.00;
                double GBPChargeAmount=0.00;
                double JPYChargeAmount=0.00;

                boolean isINRAccount=false;
                boolean isUSDAccount=false;
                boolean isEURAccount=false;
                boolean isGBPAccount=false;
                boolean isJPYAccount=false;


                ChargeManager chargeManager=new ChargeManager();
                GatewayManager gatewayManager=new GatewayManager();
                PayoutDAO payoutDAO=new PayoutDAO();
                partnerDetailsVO=new PartnerDAO().getPartnerDetails(partnerId);

                if(partnerDetailsVO == null)
                {
                    errormsg="Invalid/Inactive Partner";
                    logger.debug(errormsg);
                    request.setAttribute("errormsg",errormsg);
                    rd1.forward(request, response);
                }
                boolean isPartnerRefereedToAnyBank=gatewayManager.isPartnerRefereedToAnyBank(partnerId);
                if(!isPartnerRefereedToAnyBank)
                {
                    errormsg="Partner has Not Referred Any Bank";
                    logger.debug(errormsg);
                    request.setAttribute("errormsg",errormsg);
                    rd1.forward(request, response);
                }

                List<GatewayType> gatewayTypeList= gatewayManager.getGatewayTypeRefereedByPartner(partnerId);
                TransactionSummaryVO transactionSummaryVO=null;
                GatewayAccount  gatewayAccount=null;
                for(GatewayType gatewayType:gatewayTypeList)
                {
                    //2:Get bank table name form accountId
                    String tableName= Database.getTableName(gatewayType.getGateway());
                    if("transaction_icicicredit".equals(tableName))
                    {
                        logger.debug(tableName +" is Skipping......");
                        continue;
                    }

                    HashMap<String,List<ChargeDetailsVO>> stringListHashMap=new HashMap<String, List<ChargeDetailsVO>>();
                    Set accountIdsFromPgTypeId= GatewayAccountService.getAccountIdsFromPgTypeId(gatewayType.getPgTypeId());
                    Iterator iterator=accountIdsFromPgTypeId.iterator();
                    while(iterator.hasNext())
                    {
                        accountId=(String)iterator.next();
                        List<ChargeDetailsVO> detailsVOList=new ArrayList<ChargeDetailsVO>();
                        gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
                        if(partnerId.equalsIgnoreCase(gatewayAccount.getPartnerId()))
                        {
                            double perMidGrossAmount=0.00;
                            double perMidPaidAmount=0.00;
                            double perMidFundedAmount=0.00;

                            WireAmountVO wireAmountVO=new WireAmountVO();
                            payoutDAO.setBankPartnerWireAmountVO(partnerId,gatewayAccount,wireAmountVO);

                            perMidPaidAmount=wireAmountVO.getPaidAmount();

                            if(PZTransactionCurrency.INR.toString().equals(gatewayAccount.getCurrency()))
                            {
                                isINRAccount=true;
                                INRChargeAmount=INRChargeAmount-perMidPaidAmount;
                            }
                            else if(PZTransactionCurrency.USD.toString().equals(gatewayAccount.getCurrency()))
                            {
                                isUSDAccount=true;
                                USDChargeValue=USDChargeValue-perMidPaidAmount;
                            }
                            else if(PZTransactionCurrency.EUR.toString().equals(gatewayAccount.getCurrency()))
                            {
                                isEURAccount=true;
                                EURChargeAmount=EURChargeAmount-perMidPaidAmount;
                            }
                            else if(PZTransactionCurrency.GBP.toString().equals(gatewayAccount.getCurrency()))
                            {
                                isGBPAccount=true;
                                GBPChargeAmount=GBPChargeAmount-perMidPaidAmount;
                            }
                            else if(PZTransactionCurrency.JPY.toString().equals(gatewayAccount.getCurrency()))
                            {
                                isJPYAccount=true;
                                JPYChargeAmount=JPYChargeAmount-perMidPaidAmount;
                            }

                            //3:Load the charges On gateway account with agent charge value
                            List<ChargeVO> chargeVOList=chargeManager.getGatewayAccountCharges(gatewayAccount);
                            if(chargeVOList!=null && chargeVOList.size()>0)
                            {


                                //4:Get All the transaction processed through account
                                String currentDate=Functions.convertDateDBFormat(Calendar.getInstance().getTime());

                                transactionSummaryVO=payoutDAO.getGatewayAccountProcessedTrans(gatewayAccount,tableName);
                                for(ChargeVO  chargeVO:chargeVOList)
                                {
                                    //5:Calculate the payout

                                    //5.1:Get The Current charge rate form version store
                                    String bankPartnerVersionRate=chargeManager.getBankPartnerChargeVersionRate(chargeVO.getChargeid(),currentDate);
                                    if(bankPartnerVersionRate!=null)
                                    {
                                        chargeVO.setPartnerChargeValue(bankPartnerVersionRate);
                                    }
                                    ChargeDetailsVO chargeDetailsVO=payoutManager.calculateGatewayAccountChargesForPartner(chargeVO,transactionSummaryVO);
                                    if(chargeDetailsVO!=null)
                                    {
                                        if(PZTransactionCurrency.INR.toString().equals(gatewayAccount.getCurrency()))
                                        {
                                            INRChargeAmount=INRChargeAmount+chargeDetailsVO.getTotal();
                                        }
                                        else if(PZTransactionCurrency.USD.toString().equals(gatewayAccount.getCurrency()))
                                        {
                                            USDChargeValue=USDChargeValue+chargeDetailsVO.getTotal();
                                        }
                                        else if(PZTransactionCurrency.EUR.toString().equals(gatewayAccount.getCurrency()))
                                        {
                                            EURChargeAmount=EURChargeAmount+chargeDetailsVO.getTotal();
                                        }
                                        else if(PZTransactionCurrency.GBP.toString().equals(gatewayAccount.getCurrency()))
                                        {
                                            GBPChargeAmount=GBPChargeAmount+chargeDetailsVO.getTotal();
                                        }
                                        else if(PZTransactionCurrency.JPY.toString().equals(gatewayAccount.getCurrency()))
                                        {
                                            JPYChargeAmount=JPYChargeAmount+chargeDetailsVO.getTotal();
                                        }

                                        perMidGrossAmount=perMidGrossAmount+chargeDetailsVO.getTotal();

                                        totalChargesAmount=totalChargesAmount+chargeDetailsVO.getTotal();
                                        detailsVOList.add(chargeDetailsVO);
                                    }
                                }
                                perMidFundedAmount=perMidGrossAmount-perMidPaidAmount;
                                stringListHashMap.put(gatewayAccount.getMerchantId()+":"+perMidGrossAmount+":"+perMidPaidAmount+":"+perMidFundedAmount,detailsVOList);
                            }
                            else
                            {
                                errormsg="Charges Not Mapped On Gateway Account.";
                                logger.debug(errormsg);
                                stringListHashMap.put(gatewayAccount.getMerchantId()+":"+perMidGrossAmount+":"+perMidPaidAmount+":"+perMidFundedAmount,detailsVOList);
                            }
                        }
                        else
                        {
                            logger.debug(accountId +" Not Referred By Partner:::"+partnerId);
                        }

                    }
                    stringHashMapHashMap.put(gatewayType.getGateway()+":"+gatewayType.getCurrency(),stringListHashMap);
                }

                //6:Call appropriate view by passing calculated agent data

                bankPartnerPayoutReportVO.setReportingDate(Functions.convertDateDBFormat(Calendar.getInstance().getTime()));
                bankPartnerPayoutReportVO.setINR_CHARGE_AMOUNT(INRChargeAmount);
                bankPartnerPayoutReportVO.setUSD_CHARGE_AMOUNT(USDChargeValue);
                bankPartnerPayoutReportVO.setEUR_CHARGE_AMOUNT(EURChargeAmount);
                bankPartnerPayoutReportVO.setGBP_CHARGE_AMOUNT(GBPChargeAmount);
                bankPartnerPayoutReportVO.setJPY_CHARGE_AMOUNT(JPYChargeAmount);

                bankPartnerPayoutReportVO.setINRAccount(isINRAccount);
                bankPartnerPayoutReportVO.setUSDAccount(isUSDAccount);
                bankPartnerPayoutReportVO.setEURAccount(isEURAccount);
                bankPartnerPayoutReportVO.setGBPAccount(isGBPAccount);
                bankPartnerPayoutReportVO.setJPYAccount(isJPYAccount);

                bankPartnerPayoutReportVO.setPartnerTotalChargesAmount(totalChargesAmount);
                bankPartnerPayoutReportVO.setPartnerTotalFundedAmount(totalChargesAmount);
                bankPartnerPayoutReportVO.setStringHashMapHashMap(stringHashMapHashMap);
                bankPartnerPayoutReportVO.setPartnerDetailsVO(partnerDetailsVO);

                request.setAttribute("bankPartnerPayoutReportVO",bankPartnerPayoutReportVO);
                rd1.forward(request,response);

            }
            catch (PZDBViolationException dbe)
            {
                logger.error("Sql exception while connecting to Db or due to incorrect query::", dbe);
                PZExceptionHandler.handleDBCVEException(dbe,null, "Sql exception while generating bank agent payout report");
                request.setAttribute("errormsg","Sql exception while generating bank agent payout report");
                rd1.forward(request,response);
            }
            catch (Exception e)
            {
                logger.error(e);
            }

        }

    }
    private ValidationErrorList validateMandatoryParameter(HttpServletRequest request)
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PARTNERID);
        ValidationErrorList validationErrorList = new ValidationErrorList();
        inputValidator.InputValidations(request,inputFieldsListMandatory,validationErrorList,false);
        return validationErrorList;
    }

    private void validateOptionalParameter(HttpServletRequest request,ValidationErrorList validationErrorList)
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.GATEWAY);
        inputFieldsListOptional.add(InputFields.ACCOUNTID_SMALL);
        inputValidator.InputValidations(request,inputFieldsListOptional,validationErrorList,true);
    }
}
