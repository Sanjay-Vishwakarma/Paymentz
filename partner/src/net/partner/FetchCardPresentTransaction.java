package net.partner;
import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.manager.MerchantConfigManager;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.ecomprocessing.ECPPaymentGateway;
import com.payment.ecomprocessing.EcpResponseVo;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * Created with IntelliJ IDEA.
 * User: Mahima
 * Date: 10/8/13
 * Time: 2:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class FetchCardPresentTransaction extends HttpServlet
{
    static Logger log = new Logger(FetchCardPresentTransaction.class.getName());
    private static Functions functions = new Functions();
    PartnerFunctions partnerFunctions = new PartnerFunctions();
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        log.debug("Enter in FetchCardPresentTransaction ");
        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        RequestDispatcher rd = req.getRequestDispatcher("/fetchCardPresentTransaction.jsp?ctoken="+user.getCSRFToken());
        log.debug("CSRF check successful ");
        String partnerid = (String) session.getAttribute("merchantid");
        String error = "";
        /*boolean archive =false;
        int pageno=1;
        int records=30;*/
        String merchantid=null;
        String errormsg = "";

        String EOL = "<BR>";
       /* String fyear = "";
        String fmonth = "";
        String fdate = "";
        String tyear = "";
        String tmonth = "";
        String tdate = "";*/

//        Calendar rightNow = Calendar.getInstance();


        error = error + validateOptionalParameters(req);

        try
        {
            InputValidator inputValidator = new InputValidator();
            List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
            inputFieldsListMandatory.add(InputFields.PAGENO);
            inputFieldsListMandatory.add(InputFields.RECORDS);
            inputValidator.InputValidations(req,inputFieldsListMandatory,true);
            if(!error.isEmpty())
            {
                req.setAttribute("error",error);
                rd.forward(req,res);
                return;
            }
        }
        catch (ValidationException e)
        {
            log.error("Enter valid input",e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            log.debug("message..."+e.getMessage());
            req.setAttribute("error",errormsg);
            rd.forward(req, res);
            return;
        }

        /*String startTime =req.getParameter("starttime");
        String endTime =req.getParameter("endtime");
        String timezone = req.getParameter("timezone");*/
        String pid = req.getParameter("pid");
        String fromDate = req.getParameter("fromdate");
        String toDate = req.getParameter("todate");
        String accountid = req.getParameter("accountid");
        String memberid = req.getParameter("memberid")==null?"":req.getParameter("memberid");
        String processingType="card_present";

        System.out.println("memberid-------"+memberid);
        System.out.println("accountid-------"+accountid);
       /* List<String> errorList=new ArrayList();
        if(!ESAPI.validator().isValidInput("startDate", fromDate, "fromDate", 25, false)){
            errorList.add("Kindly provide valid Start Date ");
        }
        if(!ESAPI.validator().isValidInput("endDate", toDate, "fromDate", 25, false)){
            errorList.add("Kindly provide valid End Date ");
        }
        if(!ESAPI.validator().isValidInput("accountid", accountid, "OnlyNumber", 10, true)){
            errorList.add("Kindly provide valid Account ID ");
        }
        if(!ESAPI.validator().isValidInput("merchantid", memberid, "OnlyNumber", 10, true)){
            errorList.add("Kindly provide valid member ID ");
        }
        if(errorList.size()>0){
            errormsg=getErrorMessage(errorList);
            req.setAttribute("errormsg",errormsg);
//            req.setAttribute("accountList",accountList);
            req.setAttribute("accountid",accountid);
            req.setAttribute("memberid",memberid);
            req.setAttribute("fromdate",fromDate);
            req.setAttribute("todate",toDate);

            RequestDispatcher rd2 = req.getRequestDispatcher("/bankTransactionReport.jsp?ctoken="+user.getCSRFToken());
            rd2.forward(req, res);
            return;
        }*/


        String fromdate="";
        String todate="";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
        try
        {
            fromdate = sdf.format(sdf2.parse(fromDate));
            todate = sdf.format(sdf2.parse(toDate));
            log.error("from date--"+fromdate+"   todate---"+todate);
        }catch (Exception e){
            log.error("Exception parsing date--",e);
        }

        String gateway = "";
        String currency = "";
        String gateway_name = "";
        Set<String> accountIdSet=new HashSet<>();

        gateway="ecpcp";

        log.error("gateway name-----"+gateway);
        if(functions.isValueNull(accountid)){
            log.error("input account id found-----"+accountid);

            if(isValidAccountId(accountid))
                accountIdSet.add(accountid);
            else{
                error = "Invalid accountId.";
                req.setAttribute("error", error);
                rd = req.getRequestDispatcher("/fetchCardPresentTransaction.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
        }
        else
        {
            accountIdSet=getAccountId(gateway,memberid);
        }
        res.setContentType("text/html");

        if (accountIdSet.size()<1)
        {
            error = "No gateway account found.";
            req.setAttribute("error", error);
            rd = req.getRequestDispatcher("/fetchCardPresentTransaction.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        try
        {

//            Set<String> gatewayTypeSet = new HashSet();
            Hashtable hash = null;
            StringBuilder sb = new StringBuilder();
            if (functions.isValueNull(pid) && partner.isPartnerSuperpartnerMapped(pid, partnerid))
            {
                hash = partner.getPartnerMemberDetails(pid);
            }
            else if (!functions.isValueNull(pid))
            {
                hash = partner.getPartnerMemberDetailsNew(partnerid);
            }
            else
            {
                error = "Invalid partner mapping.";
                req.setAttribute("error", error);
                rd = req.getRequestDispatcher("/fetchCardPresentTransaction.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            if (hash.size() > 0)
            {
                Enumeration enu3 = hash.keys();
                String key3 = "";
                while (enu3.hasMoreElements())
                {
                    key3 = (String) enu3.nextElement();
                    merchantid = (String) hash.get(key3);
                    sb.append((String) hash.get(key3));
                    sb.append(",");
                }
                if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ',')
                {
                    merchantid = sb.substring(0, sb.length() - 1);
                }
            }
            else
                merchantid = "0";

            if (functions.isValueNull(req.getParameter("memberid")) && merchantid.contains(req.getParameter("memberid")))
            {
                merchantid = req.getParameter("memberid");
            }
            else if (functions.isValueNull(req.getParameter("memberid")) && !merchantid.contains(req.getParameter("memberid")))
            {
                error = "Invalid partner member configuration.";
                req.setAttribute("error", error);
                rd = req.getRequestDispatcher("/fetchCardPresentTransaction.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
                return;
            }

//            PartnerFunctions transdetail = new PartnerFunctions();

            int transactionCounter=0;
            int successCounter=0;
            int duplicateCounter=0;
//            AbstractPaymentGateway pg =null;
            log.error("runnig cron loop for no. of account ids-----"+accountIdSet.size());
            for(String accountId : accountIdSet)
            {
                log.error("calling gateway method---------------accountId="+accountId);
                ECPPaymentGateway ecpPaymentGateway = new ECPPaymentGateway(accountId);
                List<EcpResponseVo> responseVoList = ecpPaymentGateway.processCardPresentTransactionByDate(fromdate, todate, processingType);
//                transactionCounter = responseVoList.size();

                log.error("total card_present transaction found :::: " + transactionCounter);
                if(responseVoList!=null )
                {
                    for (EcpResponseVo ecpResponseVo : responseVoList)
                    {
                        String cardType = ecpResponseVo.getCardBrand();
                        if (cardType.contains("."))
                        {
                            cardType = cardType.replace(".", "");
                        }

                        String cardTypeId = "1";
                        if (cardType.equalsIgnoreCase("visa"))
                        {
                            cardTypeId = "1";
                        }
                        if (cardType.equalsIgnoreCase("MasterCard"))
                        {
                            cardTypeId = "2";
                        }

                        boolean isPresent = true;
                        //check already same transaction present
                        String paymentId = ecpResponseVo.getOriginalTransactionUniqueId();
                        if (functions.isValueNull(paymentId))
                        {
                            transactionCounter++;
                            isPresent = checkEntryPresent(ecpResponseVo.getOriginalTransactionUniqueId());

                            log.error(ecpResponseVo.getOriginalTransactionUniqueId() + " : isTransactionPresent-----" + isPresent);
                            if (!isPresent)
                            {
//                        transactionLogger.error("ecpResponseVo.getServiceTypeDesc()-------------"+ecpResponseVo.getServiceTypeDesc().trim());
//                                currency=ecpResponseVo.getCurrency();
                                String paymodeId = "32";//CP - CardPresent
                                log.error("cardTypeId=" + cardTypeId + "  accountId=" + accountId + "  paymodeId=" + paymodeId);
                                TransactionDetailsVO transactionDetailsVO = getTerminalDetails(accountId, cardTypeId, paymodeId);
                                String memberId = transactionDetailsVO.getToid();
                                if (functions.isValueNull(memberId))
                                {
                                    MerchantDetailsVO merchantDetailsVO = new MerchantConfigManager().getMerchantDetailFromToId(transactionDetailsVO.getToid());

                                    //insert into transaction_card_present
                                    String trackingId = insertEntry(ecpResponseVo, transactionDetailsVO, merchantDetailsVO);

                                    //insert into transaction_common_details_card_present
                                    insertTransactionCommonDetailsCardPresentEntry(ecpResponseVo, transactionDetailsVO, merchantDetailsVO, trackingId);

                                    successCounter++;
                                }
                            }
                            else
                            {
                                duplicateCounter++;
                            }
                        }
                    }
                }
            }
            log.error("TransactionCounter::::"+transactionCounter);
            log.error("SuccessCounter::::"+successCounter);
            log.error("DuplicateCounter::::"+duplicateCounter);
            req.setAttribute("transactionCounter", transactionCounter);
            req.setAttribute("successCounter", successCounter);
            req.setAttribute("duplicateCounter", duplicateCounter);
            rd = req.getRequestDispatcher("/fetchCardPresentTransaction.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        catch (Exception e)
        {
            log.error("Exception ::::",e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        /*session.setAttribute("bank",accountid);
        req.setAttribute("error", error);
        rd = req.getRequestDispatcher("/fetchCardPresentTransaction.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
        return;*/
    }

    private String validateOptionalParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields>  inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.ACCOUNTID_SMALL);
        inputFieldsListOptional.add(InputFields.FDATE);
        inputFieldsListOptional.add(InputFields.TDATE);
        inputFieldsListOptional.add(InputFields.PID);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional,errorList,true);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error.concat(errorList.getError(inputFields.toString()).getMessage() + EOL);
                }
            }
        }
        return error;
    }
    public boolean checkEntryPresent(String paymetnId){
        boolean isPresent = false;
        Connection con=null;
        try{
            con= Database.getConnection();
            String query  = "select paymentid from transaction_card_present where paymentid = ?";
            PreparedStatement ps =con.prepareStatement(query);
            ps.setString(1,paymetnId);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
            {
                isPresent= true;
            }
        }catch(Exception e){
            log.error(e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return isPresent;
    }
    public String insertEntry(EcpResponseVo ecpResponseVo, TransactionDetailsVO transactionDetailsVO,MerchantDetailsVO merchantDetailsVO)
    {

        String amount= ecpResponseVo.getAmount();
        String currency=ecpResponseVo.getCurrency();
        String cardType = ecpResponseVo.getCardBrand();
        String paymentId = ecpResponseVo.getOriginalTransactionUniqueId();
        String orderid = ecpResponseVo.getMerchantNumber();
        String binCountry = ecpResponseVo.getBinCountry();
        String cardNumber = ecpResponseVo.getCardNumber();
        String transactionType = ecpResponseVo.getTransactionType();
        String transactionTime = (ecpResponseVo.getTransactionDate()).substring(0,19);

        String toid = transactionDetailsVO.getToid();
        String accountId = transactionDetailsVO.getAccountId();
        String paymodeId = transactionDetailsVO.getPaymodeId();
        String cardTypeId = transactionDetailsVO.getCardTypeId();
        String terminalId = transactionDetailsVO.getTerminalId();


        log.error("cardTypeId====="+cardTypeId);

        String totype=merchantDetailsVO.getPartnerName();
        String fromid="";

        String captureamount=amount;
        String templateamount = amount;
        String templateCurrency=currency;

        String orderdesc="SYS: order description";
        String description="SYS: description";
        String remark ="SYS: remark";
        String status = "capturesuccess";
        String fromType="ecpcp";
        Connection con= null;
        try
        {
        String query = "INSERT INTO transaction_card_present(amount,currency,cardtype,paymentid,status,fromtype,country,accountid,paymodeid,cardtypeid,description,orderdescription,captureamount,toid,templateamount,terminalid,templatecurrency,remark,totype,ccnum,transactionTime,dtstamp)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,UNIX_TIMESTAMP(NOW()))";

            con=Database.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1,amount);
            ps.setString(2,currency);
            ps.setString(3,cardType);
            ps.setString(4,paymentId);
            ps.setString(5,status);
            ps.setString(6,fromType);
            ps.setString(7,binCountry);
            ps.setString(8,accountId);
            ps.setString(9,paymodeId);
            ps.setString(10,cardTypeId);
            ps.setString(11,description);
            ps.setString(12,orderdesc);
            ps.setString(13,captureamount);
            ps.setString(14,toid);
            ps.setString(15,templateamount);
            ps.setString(16,terminalId);
            ps.setString(17,templateCurrency);
            ps.setString(18,remark);
            ps.setString(19,totype);
            ps.setString(20,cardNumber);
            ps.setString(21,transactionTime);

            int result = ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                return rs.getString(1);//returning new generated tracking id
            }
        }catch (Exception e){
            log.error("Exception in insertEntry()-------"+e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return "";
    }
    public void insertTransactionCommonDetailsCardPresentEntry(EcpResponseVo ecpResponseVo, TransactionDetailsVO transactionDetailsVO,MerchantDetailsVO merchantDetailsVO,String trackingid)
    {
        Connection connection=null;
        try
        {
            String merchantId = merchantDetailsVO.getMemberId();
            String trackingId = trackingid;
            log.error("tracking id ---------"+trackingId);
            String action = "Capture Successful";
            String actionExecutorName = "PartnerManualCron";
            String status = "capturesuccess";
            String amount = ecpResponseVo.getAmount();
            String currency = ecpResponseVo.getCurrency();
            String remark = "SYS: remark";
            String responseTransactionId = ecpResponseVo.getOriginalTransactionUniqueId();
            String arn = ecpResponseVo.getArn();
            connection=Database.getConnection();
            String query = "insert into transaction_common_details_card_present (trackingid,action,status,amount,currency,remark,responsetransactionid,arn,actionexecutorname,actionexecutorid) values(?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps=connection.prepareStatement(query);
            ps.setString(1,trackingId);
            ps.setString(2,action);
            ps.setString(3,status);
            ps.setString(4,amount);
            ps.setString(5,currency);
            ps.setString(6,remark);
            ps.setString(7,responseTransactionId);
            ps.setString(8,arn);
            ps.setString(9,actionExecutorName);
            ps.setString(10,merchantId);
            int result = ps.executeUpdate();

        }catch(Exception e){
            log.error("Exception in insertTransactionCommonDetailsCardPresentEntry() ---------"+e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
    }
    public TransactionDetailsVO getTerminalDetails(String accountId, String cardTypeId,String paymodeId)
    {
        TransactionDetailsVO transactionDetailsVO = new TransactionDetailsVO();
        Connection con=null;
        try{
            con=Database.getConnection();
            String query = "SELECT memberid,accountid,paymodeid,cardtypeid,terminalid FROM member_account_mapping WHERE accountid = ? AND cardtypeid = ? AND paymodeid = ? ";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1,accountId);
            ps.setString(2,cardTypeId);
            ps.setString(3,paymodeId);


            ResultSet rs = ps.executeQuery();
            System.out.println("query-----"+ps);
            if(rs.next()){

                transactionDetailsVO.setToid(rs.getString("memberid"));
                transactionDetailsVO.setAccountId(rs.getString("accountid"));
                transactionDetailsVO.setPaymodeId(rs.getString("paymodeid"));
                transactionDetailsVO.setCardTypeId(rs.getString("cardtypeid"));
                transactionDetailsVO.setTerminalId(rs.getString("terminalid"));
            }

        }catch(Exception e){
            log.error("Exception in getTransactionDetails() ----------"+e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return transactionDetailsVO;
    }
    public String getPaymodeFromPaymentType(String payModeVal){
        String paymode="";
        if("Debit Card".equalsIgnoreCase(payModeVal)){
            paymode="DC";
        }
        if("Credit Card".equalsIgnoreCase(payModeVal)){
            paymode="CC";
        }
        return paymode;
    }
    public Set getAccountId(String gateway,String memberid){
        Set<String> set = new HashSet<String>();
        Connection con=null;
        try{
            con=Database.getConnection();
//            String query = "SELECT ga.accountid FROM gateway_accounts ga,gateway_type gt WHERE ga.pgtypeid=gt.pgtypeid and gt.gateway=?";
            StringBuilder query = new StringBuilder("SELECT accountid FROM gateway_accounts WHERE pgtypeid IN (SELECT pgtypeid FROM gateway_type WHERE gateway=?) ");
            if(functions.isValueNull(memberid)){
                query.append(" AND merchantid="+memberid);
            }
            PreparedStatement ps=con.prepareStatement(query.toString());
            ps.setString(1,gateway);

            ResultSet rs=ps.executeQuery();
            while(rs.next()){
                set.add(rs.getString("accountid"));
                log.error("accoutid in getAccountId : ----"+rs.getString("accountid"));
            }

        }catch(Exception e){
            log.error("Exception in getAccountId() ---"+e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return set;
    }
    public String getErrorMessage( List<String> list)
    {
        StringBuffer errorMessage=new StringBuffer();

        for(String message:list)
        {
            if(errorMessage.length()>0)
            {
                errorMessage.append(", ");
            }
            errorMessage.append(message);

        }
        return errorMessage.toString();
    }
    public boolean isValidAccountId(String accountId){
        Connection conn=null;
        try{
            conn=Database.getConnection();
            String query = "SELECT accountid FROM gateway_accounts WHERE pgtypeid IN (SELECT pgtypeid FROM gateway_type WHERE gateway='ecpcp') AND accountid=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1,accountId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                return  true;
            }
        }catch (Exception e){
            log.error("Exception ---",e);
        }finally
        {
            Database.closeConnection(conn);
        }
        return  false;
    }
}