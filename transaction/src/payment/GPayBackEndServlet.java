package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.manager.BlacklistManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.BlacklistVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Gpay.GpayPaymentzGateway;
import com.payment.Gpay.GpayUtils;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.apco.core.ApcoPayUtills;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.response.PZResponseStatus;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Map;

/**
 * Created by ThinkPadT410 on 2/4/2017.
 */
public class GPayBackEndServlet extends PzServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(GPayBackEndServlet.class.getName());

    public GPayBackEndServlet()
    {
        super();
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        transactionLogger.error("------inside GPayBackEndServlet-----");
        CommResponseVO commResponseVO = new CommResponseVO();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        MerchantDAO merchantDAO=new MerchantDAO();
        MerchantDetailsVO merchantDetailsVO=new MerchantDetailsVO();
        TransactionManager transactionManager = new TransactionManager();
        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO = new GenericCardDetailsVO();
        Functions functions= new Functions();
        CommResponseVO transRespDetails = null;
        AuditTrailVO auditTrailVO           = new AuditTrailVO();
        AsynchronousSmsService smsService   = new AsynchronousSmsService();

        auditTrailVO.setActionExecutorName("AcquirerBackEnd");

        ActionEntry entry = new ActionEntry();
        StringBuffer sb = new StringBuffer();

        String amount = "";
        String trackingId = "";
        String description = "";
        String status = "";
        String displayName = "";
        String toId = "";

        String remark = "";
        String accountId = "";

        String bankStatus = "";
        String pspid = "";
        String declineReason = "";
        String notificationUrl="";
        String respStatus="";
        String message="";
        String dbStatus="";
        String expDate="";
        String expMonth="";
        String expYear="";
        String isService="";
        String type   = "";
        String ISOResp= "";
        String tmpl_amount= "";
        String currency= "";
        String authCode="";
        String Currency="";
        String firstname="";
        String lastname="";
        String CardHName="";
        String CardNum="";
        String firstsix="";
        String lastfour="";
        String ccnum = "";
        String firstSix1="";
        String lastFour1="";
        PrintWriter pWriter = res.getWriter();
        String responseCode = "200";
        String returnResStatus = "OK";



        String  str="";

        int result=0;
        Enumeration enumeration = req.getParameterNames();
        Connection con = null;

        String xmlResponse = req.getParameter("params");

        // type = req.getParameter("trackingId");

        transactionLogger.error("request type ::::" + req.getParameter("type"));
        transactionLogger.error("xmlResponse::::" + xmlResponse);



        BufferedReader br = req.getReader();
        StringBuilder responseMsg = new StringBuilder();

        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            String value = req.getParameter(key);


            transactionLogger.error("---Key---" + key + "---Value---" + value);
        }
        while ((str = br.readLine()) != null)
        {
            responseMsg.append(str);
        }
        GatewayAccount  gatewayAccount = null;

        transactionLogger.error("-----GPayBackEndServlet   response-----" + responseMsg);

        try
        {
            if (xmlResponse != null)
                {
                String trackingStr = req.getParameter("trackingId");
                    transactionLogger.error("-----trackingStr in backend==============>" + trackingStr);
                if(trackingStr != null && trackingStr.contains("_")){
                   // trackingId  = req.getParameter("trackingId").split("_")[0];
                    type        = req.getParameter("trackingId").split("_")[1];
                    transactionLogger.error("-----type in backend==============>" + type);
                }


                    transactionLogger.error("-----type in backend==============>" + type);
                Map<String, String> stringStringMap = GpayUtils.readGPayRedirectionXMLResponse(xmlResponse);

                trackingId      = stringStringMap.get("ORef");
                bankStatus      = stringStringMap.get("Result");
                pspid           = stringStringMap.get("pspid");
                amount          = stringStringMap.get("Value");
                declineReason   = stringStringMap.get("ExtendedErr");
                ISOResp         = stringStringMap.get("ISOResp");
                authCode        = stringStringMap.get("AuthCode");
                Currency        =stringStringMap.get("Currency");

                if(stringStringMap.containsKey("CardNum")){
                    CardNum = stringStringMap.get("CardNum");
                    if(functions.isValueNull(CardNum)){
                        if(CardNum.contains("******")){
                            firstsix = CardNum.substring(0,6);
                            lastfour = CardNum.substring(CardNum.length() - 4, CardNum.length());
                        }
                        if(CardNum.contains(",")){
                            firstsix = CardNum.substring(0,6);
                            lastfour = CardNum.substring(CardNum.length()-4,CardNum.length());
                        }
                    }
                }
                if(stringStringMap.containsKey("CardHName")){
                    CardHName   = stringStringMap.get("CardHName");
                    if(functions.isValueNull(CardHName)){
                        String[] stringNameArr= CardHName.split("\\s+");
                        if(stringNameArr.length > 0){
                            firstname = stringNameArr[0];
                            lastname = stringNameArr[1];
                        }
                    }
                }




                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                BlacklistManager blacklistManager           = new BlacklistManager();
                BlacklistVO blacklistVO                     = new BlacklistVO();

                if (transactionDetailsVO != null)
                {
                    description     = transactionDetailsVO.getDescription();
                    accountId       = transactionDetailsVO.getAccountId();
                    toId            = transactionDetailsVO.getToid();
                    dbStatus        = transactionDetailsVO.getStatus();
                    displayName     = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    currency        = transactionDetailsVO.getCurrency();
                    tmpl_amount     = transactionDetailsVO.getTemplateamount();
                    gatewayAccount  = GatewayAccountService.getGatewayAccount(accountId);

                    commResponseVO.setDescription(description);
                    commResponseVO.setTransactionId(pspid);
                    transactionLogger.error("PaymentId----" + commResponseVO.getTransactionId());

                    ccnum        = transactionDetailsVO.getCcnum();
                    if (functions.isValueNull(ccnum))
                    {
                        ccnum    = PzEncryptor.decryptPAN(ccnum);
                        firstSix1 = functions.getFirstSix(ccnum);
                        lastFour1 = functions.getLastFour(ccnum);
                    }

                    expDate=PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate());

                    String temp[]=expDate.split("/");

                    if(functions.isValueNull(temp[0])){

                        expMonth=temp[0];
                    }
                    if(functions.isValueNull(temp[1])){
                        expYear=temp[1];
                    }

                    if (functions.isValueNull(transactionDetailsVO.getCcnum()))
                    {
                        transactionDetailsVO.setCcnum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
                    }

                    //transactionDetailsVO.setExpdate("");
                    transactionDetailsVO.setExpdate(expDate);
                    merchantDetailsVO = merchantDAO.getMemberDetails(transactionDetailsVO.getToid());
                    transactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());


                    isService = merchantDetailsVO.getIsService();


                    if (!functions.isValueNull(amount))
                        amount = transactionDetailsVO.getAmount();

                    amount = Functions.roundOff(amount);
                    transactionLogger.debug("amount-----" + amount);

                    auditTrailVO.setActionExecutorId(toId);
                    blacklistVO.setFirstSix(firstSix1);
                    blacklistVO.setLastFour(lastFour1);

                    transactionLogger.error("DB Status----" + dbStatus);
                    transactionLogger.error("Tracking  Id----" + trackingId);
                    transactionLogger.error("Type of method ----" + type);


                    if (type.equalsIgnoreCase("auth") || type.equalsIgnoreCase("sale"))
                    {
                        if (PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus))
                        {
                            if ("OK".equals(bankStatus) || "CAPTURED".equals(bankStatus) || "APPROVED".equals(bankStatus))
                            {
                                transactionLogger.error("trackingId= "+trackingId +" isService = "+isService +"TransactionType "+transactionDetailsVO.getTransactionType());
                                if ("N".equalsIgnoreCase(isService) || "PA".equalsIgnoreCase(transactionDetailsVO.getTransactionType()) || type.equalsIgnoreCase("auth")) // AUTH
                                {
                                    notificationUrl = transactionDetailsVO.getNotificationUrl();
                                    respStatus      = "success";
                                    remark          = "APPROVED";
                                    transactionLogger.error("remark====>"+remark);
                                    dbStatus      = "authsuccessful";
                                    if(transactionDetailsVO.getBillingDesc() != null){
                                        displayName = transactionDetailsVO.getBillingDesc();
                                    }else{
                                        displayName = gatewayAccount.getDisplayName();
                                    }

                                    transactionDetailsVO.setBillingDesc(displayName);
                                    commResponseVO.setDescriptor(displayName);
                                    commResponseVO.setStatus("success");
                                    commResponseVO.setRemark(remark);
                                    commResponseVO.setDescription("Transaction Successful");
                                    commResponseVO.setCurrency(currency);
                                    commResponseVO.setTransactionId(pspid);
                                    commResponseVO.setErrorCode(authCode);
                                    commResponseVO.setAuthCode(authCode);
                                    cardDetailsVO.setCardNum(CardNum);
                                    commResponseVO.setTmpl_Amount(tmpl_amount);
                                    commResponseVO.setTmpl_Currency(transactionDetailsVO.getTemplatecurrency());

                                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.AUTH_SUCCESS.toString());
                                    sb.append("update transaction_common set ");
                                    sb.append("status='authsuccessful'");
                                    sb.append(" ,remark='" + remark + "',paymentid='" + pspid +"',transaction_mode=null" + ",successtimestamp='" + functions.getTimestamp() + "',authorization_code='"+ authCode + "' where trackingid =" + trackingId + "");
                                    con     = Database.getConnection();
                                    transactionLogger.error("QUERY============>"+sb.toString());
                                    result  = Database.executeUpdate(sb.toString(), con);
                                    if (result != 1)
                                    {
                                        Database.rollback(con);
                                        //Mail.sendAdminMail("Exception while Updating status", "Exception has occured while updating status for tracking id=" + trackingId + "Auth message=" + status);
                                        // asynchronousMailService.sendAdminMail("Exception while Updating status", "Exception has occured while updating status for tracking id=" + trackingId + "Auth message=" + status);
                                    }
                                }
                                else //SALE
                                {
                                    if(transactionDetailsVO.getBillingDesc() != null){
                                        displayName = transactionDetailsVO.getBillingDesc();
                                    }else{
                                        displayName = gatewayAccount.getDisplayName();
                                    }

                                    notificationUrl = transactionDetailsVO.getNotificationUrl();
                                    respStatus      = "success";
                                    remark          = "APPROVED";
                                    transactionLogger.error("remark====>"+remark);
                                    dbStatus      = "capturesuccess";
                                    transactionDetailsVO.setBillingDesc(displayName);
                                    commResponseVO.setDescriptor(displayName);
                                    commResponseVO.setStatus("success");
                                    commResponseVO.setRemark(remark);
                                    commResponseVO.setDescription("Transaction Successful");
                                    commResponseVO.setCurrency(currency);
                                    commResponseVO.setTransactionId(pspid);
                                    commResponseVO.setErrorCode(authCode);
                                    commResponseVO.setAuthCode(authCode);
                                    cardDetailsVO.setCardNum(CardNum);
                                    commResponseVO.setTmpl_Amount(tmpl_amount);
                                    commResponseVO.setTmpl_Currency(transactionDetailsVO.getTemplatecurrency());

                                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.CAPTURE_SUCCESS.toString());
                                    sb.append("update transaction_common set ");
                                    sb.append(" captureamount='" + amount + "'");
                                    sb.append(", status='capturesuccess'");
                                    sb.append(" ,remark='" + remark + "',paymentid='" + pspid + "',transaction_mode=null" + ",successtimestamp='" + functions.getTimestamp() + "',authorization_code='"+ authCode + "' where trackingid =" + trackingId + "");
                                    con = Database.getConnection();
                                    transactionLogger.error("QUERY============>"+sb.toString());
                                    result = Database.executeUpdate(sb.toString(), con);
                                    if (result != 1)
                                    {
                                        Database.rollback(con);
                                        //Mail.sendAdminMail("Exception while Updating status", "Exception has occured while updating status for tracking id=" + trackingId + "Auth message=" + status);
                                        // asynchronousMailService.sendAdminMail("Exception while Updating status", "Exception has occured while updating status for tracking id=" + trackingId + "Auth message=" + status);
                                    }
                                }
                            }
                            else if ("NOTOK".equalsIgnoreCase(respStatus) || "DECLINED".equals(bankStatus))
                            {
                                notificationUrl = transactionDetailsVO.getNotificationUrl();
                                sb.append("update transaction_common set ");
                                respStatus = "failed";

                                if(functions.isValueNull(declineReason))
                                    remark = declineReason;

                                if("B000 - DECLINED - Do not honor".equalsIgnoreCase(remark)||remark.contains("Do not honor")||remark.contains("do not honor"))
                                {
                                    remark="Please contact to your issuer bank or try it again with another card";
                                }
                                dbStatus="authfailed";
                                transactionLogger.error("ISOResp============>"+ISOResp+ "remark====>"+remark);
                                transactionDetailsVO.setBillingDesc(displayName);
                                commResponseVO.setStatus("failed");
                                commResponseVO.setRemark(declineReason);
                                commResponseVO.setCurrency(currency);
                                commResponseVO.setTransactionId(pspid);
                                commResponseVO.setDescription("Transaction failed");
                                commResponseVO.setErrorCode(authCode);
                                commResponseVO.setAuthCode(authCode);
                                commResponseVO.setTmpl_Amount(tmpl_amount);
                                commResponseVO.setTmpl_Currency(transactionDetailsVO.getTemplatecurrency());
                                cardDetailsVO.setCardNum(CardNum);
                                sb.append(" amount='" + amount + "'");
                                sb.append(", status='authfailed'");
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.AUTH_FAILED.toString());

                                transactionLogger.debug("Sql Query-----" + sb.toString());
                                sb.append(" ,remark='" + remark + "',paymentid='" + pspid +  "',transaction_mode=null" + ",failuretimestamp='" + functions.getTimestamp() + "',authorization_code='"+ authCode + "' where trackingid =" + trackingId + "");
                                con = Database.getConnection();
                                transactionLogger.error("QUERY============>"+sb.toString());

                                result = Database.executeUpdate(sb.toString(), con);
                                if (result != 1)
                                {
                                    Database.rollback(con);
                                    //Mail.sendAdminMail("Exception while Updating status", "Exception has occured while updating status for tracking id=" + trackingId + "Auth message=" + status);
                                    asynchronousMailService.sendAdminMail("Exception while Updating status", "Exception has occured while updating status for tracking id=" + trackingId + "Auth message=" + status);
                                }
                            }
                            else
                            {
                                respStatus = "pending";
                                status = "pending";
                                remark = "Transaction Pending";
                                commResponseVO.setStatus(status);
                                commResponseVO.setDescription(remark);
                                commResponseVO.setRemark(remark);
                            }

                            asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), respStatus, remark, displayName);
                            smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), respStatus, remark, displayName);


                            if (functions.isValueNull(notificationUrl))
                            {
                                transactionLogger.error("inside sending notification---" + notificationUrl);
                                AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                                transactionDetailsVO.setBillingDesc(displayName);
                                asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, dbStatus, remark, "");
                                transactionLogger.error("dbstatus===========>"+dbStatus);
                            }
                        }
                    }

                    //REFUND
                    else if (type.equalsIgnoreCase("refund"))
                    {
                        if (PZTransactionStatus.MARKED_FOR_REVERSAL.toString().equalsIgnoreCase(dbStatus))
                        {
                            if ("OK".equals(bankStatus) ||  "CAPTURED".equals(ISOResp))
                            {
                                transactionLogger.error("--- in REFUND , reversesuccess ---");
                                status = "success";
                                displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                                remark          = "APPROVED";
                                auditTrailVO.setActionExecutorName("AcquirerBackEnd");
                                auditTrailVO.setActionExecutorId(toId);
                                String confirmStatus = "Y";
                                dbStatus = PZTransactionStatus.REVERSED.toString();
                                String refundstatus = "reversed";
                                String refundedAmount = transactionDetailsVO.getRefundAmount();
                                String captureAmount = transactionDetailsVO.getCaptureAmount();
                                double refAmount = Double.parseDouble(refundedAmount) + Double.parseDouble(amount);
                                transactionLogger.error("remark====>"+remark);

                                transactionLogger.error("double refAmount================>"+refAmount);
                                String actionEntryAction = ActionEntry.ACTION_REVERSAL_SUCCESSFUL;
                                String actionEntryStatus = ActionEntry.STATUS_REVERSAL_SUCCESSFUL;
                                String transactionStatus = PZResponseStatus.SUCCESS.toString();

                                commResponseVO.setStatus("success");
                                commResponseVO.setRemark(remark);
                                commResponseVO.setDescriptor(displayName);
                                commResponseVO.setTransactionStatus(status);
                                commResponseVO.setTransactionId(pspid);
                                commResponseVO.setCurrency(currency);
                                commResponseVO.setTmpl_Amount(tmpl_amount);
                                commResponseVO.setTmpl_Currency(transactionDetailsVO.getTemplatecurrency());
                                commResponseVO.setTransactionType(type);
                                commResponseVO.setErrorCode(authCode);
                                commResponseVO.setAuthCode(authCode);
                                commResponseVO.setDescription("Reversal Successful");

                                transactionLogger.error("captureAmount==========>"+captureAmount);
                                transactionLogger.error("refAmount============>"+refAmount);
                                if (Double.parseDouble(captureAmount) > Double.parseDouble(String.valueOf(refAmount)))
                                {
                                    status = "reversed";
                                    dbStatus = "";
                                    actionEntryAction = ActionEntry.ACTION_PARTIAL_REFUND;
                                    actionEntryStatus = ActionEntry.STATUS_PARTIAL_REFUND;
                                    transactionStatus = PZResponseStatus.PARTIALREFUND.toString();
                                }

                                if (captureAmount.equals(String.format("%.2f", refAmount)))
                                {
                                    status = "reversed";
                                    actionEntryAction = ActionEntry.ACTION_REVERSAL_SUCCESSFUL;
                                    actionEntryStatus = ActionEntry.STATUS_REVERSAL_SUCCESSFUL;
                                    transactionStatus = PZResponseStatus.SUCCESS.toString();
                                }
                                StringBuffer dbBuffer=new StringBuffer();
                                dbBuffer.append("update transaction_common set status='reversed',refundAmount='" + String.format("%.2f", refAmount) +  "',refundtimestamp='" + functions.getTimestamp() +  "',authorization_code='"+ authCode + "'where trackingid = " + trackingId);
                                transactionLogger.error("dbBuffer->" + dbBuffer);

                                con = Database.getConnection();
                                Database.executeUpdate(dbBuffer.toString(), con);

                                entry.actionEntryForCommon(trackingId, String.format("%.2f", refAmount), actionEntryAction, actionEntryStatus, commResponseVO, auditTrailVO, null);
                                String updatedStatus = PZTransactionStatus.REVERSED.toString();
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);

                                if (functions.isValueNull(notificationUrl))
                                {
                                    transactionLogger.error("inside sending notification---" + notificationUrl);
                                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                                    transactionDetailsVO.setBillingDesc(displayName);
                                    asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, updatedStatus, remark, "");
                                }

                            }
                        }

                    }

                    //capture

                    else if (type.equalsIgnoreCase("capture"))
                    { transactionLogger.error(" before in CAPTURE , capturesuccess ---"+dbStatus+"  ISOResp"+ISOResp);
                        if (PZTransactionStatus.CAPTURE_STARTED.toString().equalsIgnoreCase(dbStatus))
                        {
                            if ("OK".equals(bankStatus) || "CAPTURED".equalsIgnoreCase(ISOResp))
                            {
                                transactionLogger.error("--- in CAPTURE , capturesuccess ---");
                                status = "success";
                                displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                                auditTrailVO.setActionExecutorName("AcquirerBackEnd");
                                auditTrailVO.setActionExecutorId(toId);

                                String confirmStatus = "Y";
                                dbStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                                String capturestatus = "capturesuccess";
                                double captureAmount = Double.parseDouble(amount);
                                transactionLogger.error("ISOResp============>"+ISOResp+ "remark====>"+remark);

                                transactionLogger.error("double captureAmount================>"+captureAmount);

                                String actionEntryAction = ActionEntry.ACTION_CAPTURE_SUCCESSFUL;
                                String actionEntryStatus = ActionEntry.STATUS_CAPTURE_SUCCESSFUL;
                                String transactionStatus = PZResponseStatus.SUCCESS.toString();
                                StringBuffer dbBuffer=new StringBuffer();

                                transactionDetailsVO.setBillingDesc(displayName);
                                commResponseVO.setDescriptor(displayName);
                                commResponseVO.setStatus("success");
                                commResponseVO.setRemark(bankStatus);
                                commResponseVO.setDescription("Capture Successful");
                                commResponseVO.setCurrency(currency);
                                commResponseVO.setTransactionId(pspid);
                                commResponseVO.setErrorCode(authCode);
                                commResponseVO.setAuthCode(authCode);
                                cardDetailsVO.setCardNum(CardNum);
                                commResponseVO.setTmpl_Amount(tmpl_amount);
                                commResponseVO.setTmpl_Currency(transactionDetailsVO.getTemplatecurrency());

                                dbBuffer.append("update transaction_common set status='capturesuccess',captureAmount='" + String.format("%.2f", captureAmount)  + "',successtimestamp='" + functions.getTimestamp() +  "',authorization_code='"+ authCode + "' where trackingid = " + trackingId);
                                transactionLogger.error("dbBuffer->" + dbBuffer);

                                con = Database.getConnection();
                                Database.executeUpdate(dbBuffer.toString(), con);

                                entry.actionEntryForCommon(trackingId, String.format("%.2f", captureAmount), actionEntryAction, actionEntryStatus, commResponseVO, auditTrailVO, null);
                                String updatedStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                                if (functions.isValueNull(notificationUrl))
                                {
                                    transactionLogger.error("Inside sending notification---" + notificationUrl);
                                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                                    transactionDetailsVO.setBillingDesc(displayName);
                                    asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, updatedStatus, message, "");
                                }

                            }
                            else if("NOTOK".equalsIgnoreCase(bankStatus))
                            {
                                sb.append("update transaction_common set ");
                                respStatus = "authfailed";
                                if(functions.isValueNull(declineReason))
                                    remark = declineReason;
                                transactionLogger.error("ISOResp============>"+ISOResp+ "remark====>"+remark);

                                commResponseVO.setStatus("failed");
                                commResponseVO.setRemark(remark);
                                commResponseVO.setCurrency(currency);
                                commResponseVO.setTransactionId(pspid);
                                commResponseVO.setErrorCode(authCode);
                                commResponseVO.setAuthCode(authCode);
                                cardDetailsVO.setCardNum(CardNum);
                                commResponseVO.setTmpl_Amount(tmpl_amount);
                                commResponseVO.setTmpl_Currency(transactionDetailsVO.getTemplatecurrency());

                                sb.append(" amount='" + amount + "'");
                                sb.append(", status='authfailed'");
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.AUTH_FAILED.toString());

                                transactionLogger.debug("Sql Query-----" + sb.toString());
                                sb.append(" ,remark='" + remark + "',paymentid='" + pspid +  "',authorization_code='"+ authCode + "' where trackingid =" + trackingId + "");
                                con = Database.getConnection();
                                result = Database.executeUpdate(sb.toString(), con);
                                if (result != 1)
                                {
                                    Database.rollback(con);
                                    //Mail.sendAdminMail("Exception while Updating status", "Exception has occured while updating status for tracking id=" + trackingId + "Auth message=" + status);
                                    asynchronousMailService.sendAdminMail("Exception while Updating status", "Exception has occured while updating status for tracking id=" + trackingId + "Auth message=" + status);
                                }
                            }
                            else
                            {
                                respStatus = "pending";
                                status = "pending";
                                message = "Transaction Pending";
                                commResponseVO.setStatus(status);
                                commResponseVO.setDescription(message);
                                commResponseVO.setRemark(message);
                            }
                           /* if (functions.isValueNull(notificationUrl))
                            {
                                transactionLogger.error("inside sending notification---" + notificationUrl);
                                AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                                transactionDetailsVO.setBillingDesc(displayName);
                                asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, respStatus, remark, displayName);
                            }*/
                        }
                    }


                    //cancel
                    else if (type.equalsIgnoreCase("cancel"))
                    {
                        if (PZTransactionStatus.CANCEL_STARTED.toString().equalsIgnoreCase(dbStatus))
                        {
                            if ("OK".equals(bankStatus) || "VOIDED".equalsIgnoreCase(ISOResp) )
                            {
                                transactionLogger.error("--- in VOID CANCEL , cancelsuccess ---");
                                sb.append("update transaction_common set ");
                                respStatus = "authcancelled";
                                remark = "CANCELLED";
                                transactionLogger.error("remark====>"+remark);
                                displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

                                transactionDetailsVO.setBillingDesc(displayName);
                                commResponseVO.setDescriptor(displayName);
                                commResponseVO.setDescription("Transaction Cancel Successful");
                                commResponseVO.setStatus("success");
                                commResponseVO.setRemark(remark);
                                commResponseVO.setCurrency(currency);
                                commResponseVO.setTmpl_Amount(tmpl_amount);
                                commResponseVO.setTmpl_Currency(transactionDetailsVO.getTemplatecurrency());
                                commResponseVO.setTransactionId(pspid);
                                commResponseVO.setErrorCode(authCode);

                                sb.append("status='authcancelled'");
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CANCEL_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_CANCLLED, commResponseVO, auditTrailVO, null);
                                //                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.CANCEL_STARTED.toString());
                                sb.append(" ,remark='" + remark + "',paymentid='" + pspid +"',authorization_code='"+ authCode + "' where trackingid =" + trackingId + "");
                                con = Database.getConnection();
                                transactionLogger.error("QUERY============>"+sb.toString());
                                result = Database.executeUpdate(sb.toString(), con);

                                if (result != 1)
                                {
                                    Database.rollback(con);
                                    asynchronousMailService.sendAdminMail("Exception while Updating status", "Exception has occured while updating status for tracking id=" + trackingId + "Auth message=" + status);
                                }
                            }
                            else {
                                commResponseVO.setStatus("pending");
                                commResponseVO.setRemark("pending");
                                message = "Transaction Pending";
                                commResponseVO.setDescription(message);
                            }
                            if (functions.isValueNull(notificationUrl))
                            {
                                transactionLogger.error("inside sending notification---" + notificationUrl);
                                AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                                transactionDetailsVO.setBillingDesc(displayName);
                                asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, respStatus, remark, "");
                            }
                        }
                    }
                    //PAYOUT
                    else if (type.equalsIgnoreCase("payout"))
                    {
                        transactionLogger.error("InsidePayOut "+ trackingId + " bankStatus "+bankStatus +" ISOResp= "+ISOResp );
                        if (PZTransactionStatus.PAYOUT_STARTED.toString().equalsIgnoreCase(dbStatus))
                        {
                            if ("OK".equals(bankStatus) || "CAPTURED".equalsIgnoreCase(ISOResp))
                            {
                                respStatus  = "payoutsuccessful";
                                remark      = bankStatus;
                                transactionLogger.error("ISOResp============>"+ISOResp+ "remark====>"+remark);
                                displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

                                transactionDetailsVO.setBillingDesc(displayName);
                                commResponseVO.setDescriptor(displayName);
                                commResponseVO.setStatus("success");
                                commResponseVO.setRemark(remark);
                                commResponseVO.setDescription("payout Successful");
                                commResponseVO.setCurrency(currency);
                                commResponseVO.setTmpl_Amount(tmpl_amount);
                                commResponseVO.setTmpl_Currency(transactionDetailsVO.getTemplatecurrency());
                                commResponseVO.setTransactionId(pspid);
                                commResponseVO.setErrorCode(authCode);
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_PAYOUT_SUCCESSFUL, ActionEntry.STATUS_PAYOUT_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.PAYOUT_SUCCESS.toString());
                                sb.append("update transaction_common set ");
                                sb.append(" amount='" + amount + "'");
                                sb.append(", status='payoutsuccessful'");
                                sb.append(" ,remark='" + remark + "',paymentid='" + pspid + "',payouttimestamp='" + functions.getTimestamp() +  "',authorization_code='"+ authCode + "' where trackingid =" + trackingId + "");
                                transactionLogger.error("payoutquery "+ trackingId + " "+sb.toString());
                                con     = Database.getConnection();
                                transactionLogger.error("QUERY============>"+sb.toString());
                                result  = Database.executeUpdate(sb.toString(), con);
                                transactionLogger.error("payoutquery "+ trackingId + " "+result);
                                if (result != 1)
                                {
                                    Database.rollback(con);
                                    asynchronousMailService.sendAdminMail("Exception while Updating status", "Exception has occured while updating status for tracking id=" + trackingId + "Auth message=" + status);
                                }
                            }
                            else
                            {
                                sb.append("update transaction_common set ");
                                respStatus = "payoutfailed";
                                remark = bankStatus;
                                commResponseVO.setStatus("failed");
                                commResponseVO.setRemark(remark);
                                sb.append(" amount='" + amount + "'");
                                sb.append(", status='payoutfailed'");
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_PAYOUT_FAILED, ActionEntry.STATUS_PAYOUT_FAILED, commResponseVO, auditTrailVO, null);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.PAYOUT_FAILED.toString());

                                transactionLogger.debug("Sql Query-----" + sb.toString());
                                sb.append(" ,remark='" + remark + "',paymentid='" + pspid + "',authorization_code='" + authCode + "'where trackingid =" + trackingId + "");
                                con = Database.getConnection();
                                transactionLogger.error("QUERY============>"+sb.toString());

                                result = Database.executeUpdate(sb.toString(), con);
                                if (result != 1)
                                {
                                    Database.rollback(con);
                                    //Mail.sendAdminMail("Exception while Updating status", "Exception has occured while updating status for tracking id=" + trackingId + "Auth message=" + status);
                                    asynchronousMailService.sendAdminMail("Exception while Updating status", "Exception has occured while updating status for tracking id=" + trackingId + "Auth message=" + status);
                                }
                            }
                            asynchronousMailService.sendEmail(MailEventEnum.PAYOUT_TRANSACTION, String.valueOf(trackingId), respStatus, remark, null);

                            if (functions.isValueNull(notificationUrl))
                            {
                                transactionLogger.error("inside sending notification---" + notificationUrl);
                                AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                                transactionDetailsVO.setBillingDesc(displayName);
                                asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, respStatus, remark, "");
                            }
                        }
                    }
                }

         }
            JSONObject jsonResObject = new JSONObject();
            jsonResObject.put("responseCode", responseCode);
            jsonResObject.put("responseStatus", returnResStatus);
            res.setContentType("application/json");
            res.setStatus(200);
            pWriter.println(jsonResObject.toString());
            pWriter.flush();
            return;
        }
        catch (SQLException e)
        {
                transactionLogger.error("SQL Exception in GPayBackEndServlet---", e);
        }
        catch (SystemError e)
        {
                transactionLogger.error("System Exception in GPayBackEndServlet---", e);
        }
        catch (NoSuchAlgorithmException e)
        {
                transactionLogger.error("NoSuchAlgorithm Exception in GPayBackEndServlet---", e);
        }
        catch (PZDBViolationException e)
        {
                transactionLogger.error("SQL Exception in GPayBackEndServlet---", e);
        }
        catch (Exception e)
        {
                transactionLogger.error("SQL Exception in GPayBackEndServlet---", e);
        }
        finally
        {
                Database.closeConnection(con);
        }

    }
}