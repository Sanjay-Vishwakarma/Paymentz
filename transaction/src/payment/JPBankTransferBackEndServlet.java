package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.JPBankTransferVO;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.MerchantConfigManager;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TerminalVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.omg.IOP.Codec;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.reference.DefaultEncoder;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.Enumeration;

/**
 * Created by Sagar on 28-Sep-19.
 */
public class JPBankTransferBackEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(JPBankTransferBackEndServlet.class.getName());

    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }

    public void doService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        MySQLCodec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        transactionLogger.error("Inside JPBankTransferBackEndServlet");
        Date date4 = new Date();
        transactionLogger.error("DirectTransactionRESTIMPL processDirectTransaction start #########" + date4.getTime());
        HttpSession session = req.getSession(true);
        Functions functions = new Functions();
        TransactionManager transactionManager = new TransactionManager();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        CommResponseVO transRespDetails = null;
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = new GenericCardDetailsVO();
        TransactionUtility transactionUtility = new TransactionUtility();
        Connection con = null;
        CommRequestVO commRequestVO = null;
        ActionEntry entry = new ActionEntry();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        PaymentManager paymentManager=new PaymentManager();
        MerchantDAO merchantDAO=new MerchantDAO();
        TerminalVO terminalVO=new TerminalVO();
        Enumeration enumeration = req.getParameterNames();
        boolean hasElements = enumeration.hasMoreElements();
        transactionLogger.error("hasElements ----" + hasElements);
        String value = "";
        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            value = req.getParameter(key);
            transactionLogger.error("JPBankTransferBackEndServlet Key ---" + key + "--- JPBankTransferBackEndServlet value ---" + value);
        }
        String trackingId = "";
        String customerId = "";
        String data = req.getParameter("data");
        transactionLogger.error("response data--->" + data);
        String transArr[]={};
        if(data.contains("__"))
        {
            transArr = data.split("__");
        }
        else
        {
            transArr=new String[1];
            transArr[0]=data;
        }
        for (String transData : transArr)
        {   //Date - 20200522   paymentid-PI00006  Amount -500   Customerid -100660
            //response data---> 20200522PI00006/500@100660
            //20200715PI00001%2F50000%405706151

            String string_before_slash = "";
            String response_amount = "";
            String responsetrackingId = "";
            String date="";
            String transactionId = "";
            boolean isRecordNotFound=false;
            if (transData.contains("/") && transData.contains("@"))
            {
                int indx_slash = transData.indexOf("/");
                int indx_at = transData.indexOf("@");
                //string_before_slash = transData.substring(8, indx_slash)+transData.substring(0,8);   //PI0000620200522
                response_amount = String.format("%.2f", Double.parseDouble(transData.substring(indx_slash + 1, indx_at)));//500
                date=transData.substring(0,8);   // 20200522
                customerId = transData.split("@")[1]; // 100660
                string_before_slash=transData.substring(0,indx_slash);
                transactionId=string_before_slash;
            }
            //  transactionLogger.error("Response Date-->" + date);
            transactionLogger.error("Payment Id-->" + string_before_slash);
            transactionLogger.error("Capture Amount-->" + response_amount);
            transactionLogger.error("responsetrackingId-->" + customerId);
            addressDetailsVO=new GenericAddressDetailsVO();
            genericTransDetailsVO=new GenericTransDetailsVO();
            cardDetailsVO=new GenericCardDetailsVO();

            String currency = "";
            String toid = "";
            String description = "";
            String redirectUrl = "";
            String accountId = "";
            String orderDesc = "";
            String amount1 = "";
            String tmpl_amt = "";
            String tmpl_currency = "";
            String firstName = "";
            String lastName = "";
            String paymodeid = "";
            String cardtypeid = "";
            String custId = "";
            String dbStatus = "";
            String notificationUrl = "";
            String logoName = "";
            String partnerName = "";
            String billingDesc = "";
            String status = "";
            String message = "";
            String custEmail = "";
            String terminalId = "";
            String ccnum = "";
            String eci = "";
            String clKey="";
            String responseStatus="";
            String paymentid="";

            try
            {
                TransactionDetailsVO transactionDetailsVO=null;
                transactionLogger.error("trackingId ---------->" + trackingId);

               // transactionDetailsVO = transactionManager.getDetailFromCommonForJPBank(customerId,response_amount,"");//select query
                if(transactionDetailsVO==null)
                {
                    transactionLogger.error("---trying to fetch records using trackingId---");
                    trackingId=customerId;
                    transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);//select query
                }else
                {
                    trackingId=transactionDetailsVO.getTrackingid();
                }
                if (transactionDetailsVO != null && functions.isValueNull(transactionDetailsVO.getTrackingid()))
                {
                    transactionLogger.error("---Record found---");
                    accountId = transactionDetailsVO.getAccountId();
                    GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                    toid = transactionDetailsVO.getToid();
                    description = transactionDetailsVO.getDescription();
                    redirectUrl = transactionDetailsVO.getRedirectURL();
                    orderDesc = transactionDetailsVO.getOrderDescription();
                    currency = transactionDetailsVO.getCurrency();
                    firstName = transactionDetailsVO.getFirstName();
                    lastName = transactionDetailsVO.getLastName();
                    paymodeid = transactionDetailsVO.getPaymodeId();
                    cardtypeid = transactionDetailsVO.getCardTypeId();
                    custEmail = transactionDetailsVO.getEmailaddr();
                    custId = transactionDetailsVO.getCustomerId();
                    paymentid = transactionId +"-"+transactionDetailsVO.getPaymentId();
                    dbStatus = transactionDetailsVO.getStatus();

                    terminalId = transactionDetailsVO.getTerminalId();
                    tmpl_amt = transactionDetailsVO.getTemplateamount();
                    tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                    ccnum = transactionDetailsVO.getCcnum();
                    eci = transactionDetailsVO.getEci();
                    billingDesc = gatewayAccount.getDisplayName();
                    merchantDetailsVO = merchantDAO.getMemberDetails(toid);
                    StringBuffer dbBuffer = new StringBuffer();
                    if (merchantDetailsVO != null)
                    {
                        clKey = merchantDetailsVO.getKey();
                        logoName = merchantDetailsVO.getLogoName();
                        partnerName = merchantDetailsVO.getPartnerName();
                    }
                    auditTrailVO.setActionExecutorName("AcquirerBackEnd");
                    auditTrailVO.setActionExecutorId(toid);
                    con = Database.getConnection();
                    transactionLogger.error("dbStatus--->"+dbStatus);
                    transactionLogger.error("transactionDetailsVO.getPaymentId()-->"+transactionDetailsVO.getPaymentId());
                    transactionLogger.error("transactionId-->"+transactionId);
                    if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))//this condition changes as per the response we get after transaction
                    {
                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                        transactionLogger.error("Inside Capture Success for Tracking Id ="+trackingId);
                        transRespDetails = new CommResponseVO();
                        status = "success";
                        responseStatus = "Successful";
                        message = "Transaction Successful";
                        dbStatus = "capturesuccess";
                        // transRespDetails.setDescription(ResponseMsg);
                        transRespDetails.setStatus(status);
                        transRespDetails.setRemark(message);
                        transRespDetails.setRemark(message);
                        transRespDetails.setDescriptor(billingDesc);
                        transRespDetails.setTransactionId(paymentid);
                        transRespDetails.setTmpl_Amount(response_amount);
                        transRespDetails.setTmpl_Currency(tmpl_currency);
                        transRespDetails.setCurrency(currency);
                        transRespDetails.setResponseTime(date);

                        dbBuffer.append("update transaction_common set captureamount='" + response_amount + "',paymentid='" + paymentid + "',status='"+dbStatus+"',remark='" + ESAPI.encoder().encodeForSQL(me,message) + "' where trackingid = " + trackingId);
                        transactionLogger.error("update query------------>" + dbBuffer.toString());
                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, response_amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, auditTrailVO, null);


                        /*else if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) )
                        {
                            transactionLogger.error("Inside failed");
                            transRespDetails = new CommResponseVO();
                            status = "failed";
                            responseStatus = "failed";
                            message = "Transaction failed";
                            dbStatus = "failed";
                            // transRespDetails.setDescription(ResponseMsg);
                            transRespDetails.setStatus(status);
                            transRespDetails.setRemark("Transaction Failed");
                            dbBuffer.append("update transaction_common set captureamount='" + amount1 + "',paymentid='" + transactionId + "',status='capturefailed',remark='" + ESAPI.encoder().encodeForSQL(me, message) + "' where trackingid = " + trackingId);          transactionLogger.error("update query------------>" + dbBuffer.toString());
                            Database.executeUpdate(dbBuffer.toString(), con);
                            entry.actionEntryForCommon(trackingId, amount1, ActionEntry.ACTION_CAPTURE_FAILED, ActionEntry.STATUS_CAPTURE_FAILED, transRespDetails, auditTrailVO, null);

                        }
    */
                        // this part is for sending status response to merchant side
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);
                        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                        asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                        AsynchronousSmsService smsService = new AsynchronousSmsService();
                        smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                        genericTransDetailsVO.setAmount(response_amount);
                        genericTransDetailsVO.setCurrency(currency);
                        genericTransDetailsVO.setOrderDesc(orderDesc);
                        genericTransDetailsVO.setRedirectUrl(redirectUrl);
                        genericTransDetailsVO.setOrderId(description);
                        genericTransDetailsVO.setNotificationUrl(notificationUrl);
                        genericTransDetailsVO.setBillingDiscriptor(billingDesc);
                        //genericTransDetailsVO.setRedirectMethod(redirectMethod);
                        commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                        commonValidatorVO.setCustomerId(custId);
                        commonValidatorVO.setTerminalId(terminalId);
                        commonValidatorVO.setLogoName(logoName);
                        commonValidatorVO.setPartnerName(partnerName);
                        commonValidatorVO.setPaymentType(paymodeid);
                        commonValidatorVO.setCardType(cardtypeid);
                        commonValidatorVO.setTrackingid(trackingId);
                        commonValidatorVO.setStatus(status);

                        if (functions.isValueNull(custEmail))
                        {
                            addressDetailsVO.setEmail(custEmail);
                        }
                        if (functions.isValueNull(firstName))
                        {
                            addressDetailsVO.setFirstname(firstName);
                        }

                        if (functions.isValueNull(lastName))
                        {
                            addressDetailsVO.setLastname(lastName);
                        }
                        addressDetailsVO.setTmpl_amount(response_amount);
                        addressDetailsVO.setTmpl_currency(tmpl_currency);

                        commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                        commonValidatorVO.setEci(eci);
                        transactionLogger.error("notificationUrl---" + notificationUrl);
                        if (functions.isValueNull(notificationUrl))
                        {
                            transactionLogger.error("inside sending notification---" + notificationUrl);
                            TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                            transactionDetailsVO1.setAmount(response_amount);
                            AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                            asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, status, message, "");
                        }
                        transactionLogger.debug("JPBankTransferBackEndServlet  end #########" + new Date().getTime());
                        transactionLogger.error("JPBankTransferBackEndServlet  diff #########" + (new Date().getTime() - date4.getTime()));
                    }else if(functions.isValueNull(transactionDetailsVO.getPaymentId()) && !transactionDetailsVO.getPaymentId().contains(transactionId))
                    {
                        //transactionDetailsVO = transactionManager.getDetailFromCommonForJPBank(customerId,response_amount,transactionId);//select query
                        if(transactionDetailsVO==null)
                        {
                            isRecordNotFound = true;
                        }
                    }
                }else
                {
                    isRecordNotFound=true;
                }
                if(isRecordNotFound){
                    transactionLogger.error("---Record Not found---");
                    //transactionDetailsVO = transactionManager.getDetailFromCommonForJPBank(customerId,"","");
                    if(transactionDetailsVO!=null)
                    {
                        accountId=transactionDetailsVO.getAccountId();

                        terminalId=transactionDetailsVO.getTerminalId();
                        toid=transactionDetailsVO.getToid();
                        firstName=transactionDetailsVO.getFirstName();
                        lastName=transactionDetailsVO.getLastName();
                        custEmail=transactionDetailsVO.getEmailaddr();
                        currency=transactionDetailsVO.getCurrency();
                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                        description=transactionId+"_"+customerId + "_" + toid;

                        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                        merchantDetailsVO=merchantDAO.getMemberDetails(toid);

                        terminalVO.setTerminalId(terminalId);
                        terminalVO.setPaymodeId(transactionDetailsVO.getPaymodeId());
                        terminalVO.setCardTypeId(transactionDetailsVO.getCardTypeId());
                        genericTransDetailsVO.setAmount(response_amount);
                        genericTransDetailsVO.setCurrency(currency);
                        genericTransDetailsVO.setOrderDesc(orderDesc);
                        genericTransDetailsVO.setOrderId(description);
                        genericTransDetailsVO.setNotificationUrl(notificationUrl);
                        genericTransDetailsVO.setRedirectUrl(transactionDetailsVO.getRedirectURL());
                        genericTransDetailsVO.setFromid(transactionDetailsVO.getFromid());
                        genericTransDetailsVO.setFromtype(transactionDetailsVO.getFromtype());
                        genericTransDetailsVO.setTotype(transactionDetailsVO.getTotype());
                        merchantDetailsVO.setAccountId(accountId);
                        addressDetailsVO.setEmail(custEmail);
                        addressDetailsVO.setFirstname(firstName);
                        addressDetailsVO.setLastname(lastName);
                        addressDetailsVO.setTmpl_amount(response_amount);
                        addressDetailsVO.setTmpl_currency(currency);
                        commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                        commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                        commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                        commonValidatorVO.setCardType(transactionDetailsVO.getCardTypeId());
                        commonValidatorVO.setPaymentType(transactionDetailsVO.getPaymodeId());
                        commonValidatorVO.setAccountId(accountId);
                        commonValidatorVO.setTerminalId(terminalId);
                        commonValidatorVO.setCustomerId(customerId);
                        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                        commonValidatorVO.setTerminalVO(terminalVO);
                        int trackingid = paymentManager.insertAuthStartedTransactionEntryForAsyncFlow(commonValidatorVO, auditTrailVO);
                        trackingId=String.valueOf(trackingid);
                        transactionLogger.error("Inside Capture Success for Tracking Id ="+trackingId);
                        auditTrailVO.setActionExecutorName("AcquirerBackEnd");
                        auditTrailVO.setActionExecutorId(toid);
                        con = Database.getConnection();
                        transRespDetails = new CommResponseVO();
                        status = "success";
                        message = "Transaction Successful";
                        dbStatus = "capturesuccess";
                        // transRespDetails.setDescription(ResponseMsg);
                        billingDesc = gatewayAccount.getDisplayName();
                        transRespDetails.setStatus(status);
                        transRespDetails.setRemark(message);
                        transRespDetails.setDescription(message);
                        transRespDetails.setDescriptor(billingDesc);
                        transRespDetails.setTransactionId(paymentid);
                        transRespDetails.setTmpl_Amount(response_amount);
                        transRespDetails.setTmpl_Currency(tmpl_currency);
                        transRespDetails.setCurrency(currency);
                        transRespDetails.setResponseTime(date);
                        StringBuffer dbBuffer=new StringBuffer();
                        dbBuffer.append("update transaction_common set captureamount='" + response_amount + "',paymentid='" + transactionId + "',status='"+dbStatus+"',remark='" + ESAPI.encoder().encodeForSQL(me,message) + "' where trackingid = " + trackingId);
                        transactionLogger.error("update query------------>" + dbBuffer.toString());
                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, response_amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);

                        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                        asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                        AsynchronousSmsService smsService = new AsynchronousSmsService();
                        smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                        if (functions.isValueNull(notificationUrl))
                        {
                            transactionLogger.error("inside sending notification---" + notificationUrl);
                            TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                            transactionDetailsVO1.setAmount(response_amount);
                            AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                            asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, status, message, "");
                        }
                    }

                }
            }
            catch (PZDBViolationException e)
            {
                transactionLogger.error("PZDBViolationException---->" , e);
            }
            catch (SystemError se)
            {
                transactionLogger.error("SystemError---->" , se);
            }
            finally
            {
                Database.closeConnection(con);
            }
        }
    }

}
