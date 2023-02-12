package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.BlacklistManager;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.BlacklistVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.PZTransactionStatus;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.response.PZResponseStatus;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.json.JSONObject;
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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;

/**
 * Created by Admin on 2022-01-22.
 */
public class PGSBackEndServlet extends HttpServlet
{
    private static TransactionLogger transactionlogger = new TransactionLogger(PGSBackEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public void doGet(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
    {
        try
        {
            doService(req,res);
        }
        catch (PZDBViolationException e)
        {
            transactionlogger.error("Entering PGSBackEndServlet ......",e);
        }
    }

    public void doPost(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
    {
        try
        {
            doService(req,res);
        }
        catch (PZDBViolationException e)
        {
            transactionlogger.error("Entering PGSBackEndServlet ......",e);
        }
    }

    public void doService(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException, PZDBViolationException
    {
        transactionlogger.error("....Entering PGSBackEndServlet ......");
        HttpSession session = req.getSession();
        Functions functions = new Functions();
        ActionEntry entry                           = new ActionEntry();
        AuditTrailVO auditTrailVO                   = new AuditTrailVO();
        TransactionManager transactionManager       = new TransactionManager();
        CommonValidatorVO commonValidatorVO         = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        MerchantDetailsVO merchantDetailsVO         = new MerchantDetailsVO();
        MerchantDAO merchantDAO                     = new MerchantDAO();
        Comm3DResponseVO comm3DResponseVO           = new Comm3DResponseVO();
        GenericAddressDetailsVO addressDetailsVO    = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO          = new GenericCardDetailsVO();
        StatusSyncDAO statusSyncDAO                 = new StatusSyncDAO();
        PaymentManager paymentManager               = new PaymentManager();
        TransactionUtility transactionUtility       = new TransactionUtility();
        MySQLCodec me                               = new MySQLCodec(MySQLCodec.Mode.STANDARD);

        Connection con      = null;
        PreparedStatement p = null;
        ResultSet rs        = null;
        String toid         = "";
        String description  = "";
        String redirectUrl  = "";
        String accountId    = "";
        String orderDesc    = "";
        String currency     = "";
        String clkey        = "";
        String checksumAlgo = "";
        String checksum     = "";
        String autoredirect = "";
        String isService    = "";
        String displayName  = "";
        String isPowerBy    = "";
        String logoName     = "";
        String partnerName  = "";
        String amount       = "";
        String trackingId   = req.getParameter("trackingId");
        String status       = "";
        String remark       = "";

        String bankTransactionStatus = "";
        String name                 = "";
        String email                = "";
        String tmpl_amt             = "";
        String tmpl_currency        = "";
        String firstName            = "";
        String lastName             = "";
        String paymodeid            = "";
        String cardtypeid           = "";
        String custId               = "";
        String transactionStatus    = "";
        String transactionId        = "";
        String message              = "";
        String billingDesc          = "";
        String dbStatus             = "";
        String notificationUrl      = "";
        String ccnum                = "";
        String expMonth             = "";
        String expYear              = "";
        String requestIp            = "";
        String txtdesc              = "";
        String STATUS               = "";
        String responseAmount       = "";
        String firstSix             = "";
        String lastFour             = "";
        String cardHolderName       = "";
        String terminalId           = "";
        String toType               = "";


        String resAmount        = "";
        String out_trade_no     = "";
        String method           = "";
        String trade_status     = "";
        String trade_no         = "";
        String respCurrency     = "";
        String out_request_no   = "";
        String timestamp        = "";

        PrintWriter pWriter         = res.getWriter();
        String responseCode         = "0";
        String returnResStatus      = "OK";
        String payoutId      = "";

        try
        {

            BufferedReader bufferedReader = req.getReader();
            StringBuilder responseMsg   = new StringBuilder();
            String string;
            while ((string = bufferedReader.readLine()) != null)
            {
                responseMsg.append(string);
            }
            transactionlogger.error("----- PGSBackEndServlet Response -----" + responseMsg);

            JSONObject responseJOSN =  new JSONObject(responseMsg.toString());
            transactionlogger.error("----- JSONObject  -- " + responseJOSN);

           /* if(responseJOSN.has("out_trade_no")){
                trackingId = responseJOSN.getString("out_trade_no");
            }*/
            if(responseJOSN.has("amount")){
                resAmount = responseJOSN.getString("amount");
            }
            if(responseJOSN.has("method")){
                method = responseJOSN.getString("method");
            }
            if(responseJOSN.has("trade_status")){
                trade_status = responseJOSN.getString("trade_status");
            }
            if(responseJOSN.has("trade_no")){
                trade_no = responseJOSN.getString("trade_no");
            }
            if(responseJOSN.has("currency")){
                respCurrency = responseJOSN.getString("currency");
            }
            if(responseJOSN.has("timestamp")){
                timestamp = responseJOSN.getString("timestamp");
            }
            //Payout
            if(responseJOSN.has("status")){
                status = responseJOSN.getString("status");
            }
            if(responseJOSN.has("msg")){
                message = responseJOSN.getString("msg");
            }
            if(responseJOSN.has("payoutId")){
                payoutId = responseJOSN.getString("payoutId");
            }

            transactionlogger.error("trade_status >>>>>>>>>> " + trade_status);
            transactionlogger.error("trade_no >>>>>>>>>> " + trade_no);
            transactionlogger.error("resAmount >>>>>>>>>> " + resAmount);
            transactionlogger.error("trackingId >>>>>>>>>> " + trackingId);
            transactionlogger.error("payoutId >>>>>>>>>> " + payoutId);



            TransactionDetailsVO transactionDetailsVO   = transactionManager.getTransDetailFromCommon(trackingId);
            CommRequestVO requestVO                     = new CommRequestVO();
            BlacklistManager blacklistManager           = new BlacklistManager();
            BlacklistVO blacklistVO                     = new BlacklistVO();

            if (transactionDetailsVO != null)
            {
                ccnum        = transactionDetailsVO.getCcnum();
                if (functions.isValueNull(ccnum))
                {
                    ccnum    = PzEncryptor.decryptPAN(ccnum);
                    firstSix = functions.getFirstSix(ccnum);
                    lastFour = functions.getLastFour(ccnum);
                }
                toid        = transactionDetailsVO.getToid();
                description = transactionDetailsVO.getDescription();
                redirectUrl = transactionDetailsVO.getRedirectURL();
                accountId   = transactionDetailsVO.getAccountId();
                orderDesc   = transactionDetailsVO.getOrderDescription();
                currency    = transactionDetailsVO.getCurrency();
                transactionlogger.error("db amount-->: "+ transactionDetailsVO.getAmount());
                amount = transactionDetailsVO.getAmount();

                terminalId = transactionDetailsVO.getTerminalId();
                remark     = transactionDetailsVO.getRemark();

                requestIp = Functions.getIpAddress(req);
                transactionlogger.error("requestIp --- >" + requestIp);
                transactionlogger.error("notificationUrl ---" + notificationUrl);
                if (functions.isValueNull(transactionDetailsVO.getTemplateamount()))
                {
                    tmpl_amt = transactionDetailsVO.getTemplateamount();
                }
                else
                {
                    tmpl_amt = amount;
                }

                tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                if (functions.isValueNull(tmpl_currency))
                {
                    tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                }
                else
                {
                    transactionlogger.error("inside else of tmpl_currency --->");
                    tmpl_currency = "";
                }

                if (functions.isValueNull(transactionDetailsVO.getExpdate()))
                {
                    String expDate = PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate());

                    String temp[] = expDate.split("/");

                    if (functions.isValueNull(temp[0]))
                    {
                        expMonth = temp[0];
                    }
                    if (functions.isValueNull(temp[1]))
                    {
                        expYear = temp[1];
                    }
                }

                firstName   = transactionDetailsVO.getFirstName();
                lastName    = transactionDetailsVO.getLastName();
                name        = transactionDetailsVO.getFirstName() + " " + transactionDetailsVO.getLastName();

                if(functions.isValueNull(transactionDetailsVO.getName())){
                    cardHolderName=transactionDetailsVO.getName();
                }
                paymodeid       = transactionDetailsVO.getPaymodeId();
                cardtypeid      = transactionDetailsVO.getCardTypeId();
                custId          = transactionDetailsVO.getCustomerId();
                transactionId   = transactionDetailsVO.getPaymentId();
                dbStatus        = transactionDetailsVO.getStatus();
                transactionlogger.error("dbStatus ---" + dbStatus);

                merchantDetailsVO   = merchantDAO.getMemberDetails(toid);
                clkey               = merchantDetailsVO.getKey();
                checksumAlgo        = merchantDetailsVO.getChecksumAlgo();
                autoredirect        = merchantDetailsVO.getAutoRedirect();
                isPowerBy           = merchantDetailsVO.getIsPoweredBy();
                logoName            = merchantDetailsVO.getLogoName();
                isService           = merchantDetailsVO.getIsService();
                partnerName         = merchantDetailsVO.getPartnerName();
                email               = transactionDetailsVO.getEmailaddr();
                toType              = transactionDetailsVO.getTotype();

                auditTrailVO.setActionExecutorName("PGSBackEnd");
                auditTrailVO.setActionExecutorId(toid);

                GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
                displayName                                         = gatewayAccount.getDisplayName();

                transactionlogger.error("status dbStatus ----> "+ dbStatus);
                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus) ||  PZTransactionStatus.AUTH_FAILED.toString().equalsIgnoreCase(dbStatus))
                {
                    transactionlogger.error("inside AUTH_STARTED---");
                    StringBuffer dbBuffer = new StringBuffer();

                    con               = Database.getConnection();

                    /*String inStatus     = comm3DResponseVO.getStatus();
                    String inRemark     = comm3DResponseVO.getRemark();
                    String inCode       = comm3DResponseVO.getAuthCode();
                    responseAmount      = comm3DResponseVO.getAmount();
                    TXN_ID              = comm3DResponseVO.getTransactionId();
                    String errorcode    = comm3DResponseVO.getErrorCode();
                    String responsetime = comm3DResponseVO.getResponseTime();
                    */

                    if(trade_status.equalsIgnoreCase("SUCCESS")){
                        comm3DResponseVO.setStatus("success");
                    }else if(trade_status.equalsIgnoreCase("CANCEL") || trade_status.equalsIgnoreCase("DISPUTE") || trade_status.equalsIgnoreCase("REFUSED")
                            || trade_status.equalsIgnoreCase("RISK_CONTROLLING")){
                        comm3DResponseVO.setStatus("failed");
                    }else{
                        comm3DResponseVO.setStatus("pending");
                    }


                    comm3DResponseVO.setRemark(trade_status);
                    comm3DResponseVO.setDescription(trade_status);
                    comm3DResponseVO.setAmount(resAmount);
                    comm3DResponseVO.setTransactionId(trade_no);
                    comm3DResponseVO.setResponseTime(timestamp);


                    String inStatus     = comm3DResponseVO.getStatus();
                    String inRemark     = comm3DResponseVO.getRemark();
                    String inCode       = comm3DResponseVO.getAuthCode();
                    responseAmount      = comm3DResponseVO.getAmount();
                    String errorcode    = comm3DResponseVO.getErrorCode();
                    String responsetime = comm3DResponseVO.getResponseTime();


                    transactionlogger.error("inquiry trackingid--->"+trackingId);
                    transactionlogger.error("inquiry inStatus--->"+inStatus);
                    transactionlogger.error("inquiry amount--->"+responseAmount);
                    transactionlogger.error("inquiry inCode--->"+inCode);
                    transactionlogger.error("inquiry inRemark--->"+inRemark);
                    transactionlogger.error("inquiry VendorOrderId--->"+trade_no);

                    if(functions.isValueNull(responseAmount))
                    {
                        Double compRsAmount = Double.valueOf(responseAmount);
                        Double compDbAmount = Double.valueOf(amount);

                        transactionlogger.error("response amount --->" + compRsAmount);
                        transactionlogger.error(" DB Amount--->" + compDbAmount);

                        if (compDbAmount.equals(compRsAmount))
                        {
                            STATUS = comm3DResponseVO.getStatus();
                            amount = comm3DResponseVO.getAmount();
                            remark = comm3DResponseVO.getRemark();
                        }
                        else if (!compDbAmount.equals(compRsAmount) && "success".equalsIgnoreCase(inStatus))
                        {
                            remark = "Failed-IRA";
                            STATUS = "failed";
                            amount = responseAmount;
                            blacklistVO.setVpaAddress(custId);
                            blacklistVO.setIpAddress(requestIp);
                            blacklistVO.setEmailAddress(email);
                            blacklistVO.setActionExecutorId(toid);
                            blacklistVO.setActionExecutorName("PGSBackEnd");
                            blacklistVO.setRemark("IncorrectAmount Trackingid : "+trackingId);
                            blacklistVO.setFirstSix(firstSix);
                            blacklistVO.setLastFour(lastFour);
                            blacklistVO.setName(cardHolderName);
                            blacklistManager.commonBlackListing(blacklistVO);
                        }
                    }
                    //currency    = comm3DResponseVO.getCurrency();
                    txtdesc     = comm3DResponseVO.getDescription();
                    String bankTransactionDate  = comm3DResponseVO.getBankTransactionDate();

                    comm3DResponseVO.setTmpl_Amount(tmpl_amt);
                    comm3DResponseVO.setTmpl_Currency(tmpl_currency);
                    comm3DResponseVO.setIpaddress(requestIp);
                    comm3DResponseVO.setErrorCode(errorcode);
                    comm3DResponseVO.setResponseTime(responsetime);
                    comm3DResponseVO.setTransactionId(trade_no);

                    transactionlogger.error("currency--->"+currency);
                    transactionlogger.error("inquiry txtdesc--->"+txtdesc);
                    transactionlogger.error("inquiry bankTransactionDate--->"+bankTransactionDate);
                    transactionlogger.error("remark--->"+remark);

                    if(STATUS.equalsIgnoreCase("SUCCESS"))
                    {
                        transactionlogger.error("sucess  front end operation: " + trackingId +" "+STATUS);
                        notificationUrl    = transactionDetailsVO.getNotificationUrl();
                        status              = "success";
                        billingDesc         = displayName;
                        message             = remark;
                        comm3DResponseVO.setDescriptor(billingDesc);

                        dbStatus = "capturesuccess";

                        comm3DResponseVO.setStatus(dbStatus);

                        dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + trade_no + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency +  "',status='capturesuccess'" + " ,successtimestamp='" + functions.getTimestamp());
                        dbBuffer.append("',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' ,remark='" + ESAPI.encoder().encodeForSQL(me, message) + "' where trackingid = " + trackingId);
                        transactionlogger.error("Update Query---" + dbBuffer);

                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, comm3DResponseVO, auditTrailVO, requestIp);//+ ",rrn='" + rrn
                    }
                    else if("failed".equalsIgnoreCase(STATUS))
                    {
                        transactionlogger.error("failed  front end operation: " + trackingId +" "+STATUS);
                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                        status          = "failed";
                        message         = remark;

                        dbStatus = "authfailed";
                        dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + trade_no + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency + "'" + " ,failuretimestamp='" + functions.getTimestamp());

                        dbBuffer.append("',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' ,remark='" + ESAPI.encoder().encodeForSQL(me, message) + "' where trackingid = " + trackingId);
                        transactionlogger.error("Update Query---" + dbBuffer);
                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, comm3DResponseVO, auditTrailVO, requestIp);
                    }
                    else
                    {
                        //pending
                        dbStatus    = "authstarted";
                        status      = "Pending";
                        message     = "Pending";
                    }
                } else if (PZTransactionStatus.PAYOUT_STARTED.toString().equalsIgnoreCase(dbStatus))
                {

                    if ("PAID".equalsIgnoreCase(status))
                    {

                        remark             = message;
                        dbStatus            = PZTransactionStatus.PAYOUT_SUCCESS.toString();
                        StringBuffer sb     = new StringBuffer();

                        notificationUrl = transactionDetailsVO.getNotificationUrl();

                        transactionDetailsVO.setBillingDesc(displayName);
                        comm3DResponseVO.setDescriptor(displayName);
                        comm3DResponseVO.setStatus("success");
                        comm3DResponseVO.setRemark(remark);
                        comm3DResponseVO.setDescription("payout Successful");
                        comm3DResponseVO.setCurrency(currency);
                        comm3DResponseVO.setTmpl_Amount(tmpl_amt);
                        comm3DResponseVO.setTmpl_Currency(transactionDetailsVO.getTemplatecurrency());
                        //commResponseVO.setTransactionId(pspid);
                        //commResponseVO.setErrorCode(authCode);

                        sb.append("update transaction_common set ");
                        sb.append(" payoutamount='" + amount + "'");
                        sb.append(", status='payoutsuccessful'");
                        //sb.append(" ,remark='" + remark + "',paymentid='" + pspid + "',firstname='" + firstname + "',lastname='" + lastname + "',Name='" + CardHName + "',ccnum='" + CardNum + "',payouttimestamp='" + functions.getTimestamp() + "' where trackingid =" + trackingId + "");
                        sb.append(" ,remark='" + remark + "',payouttimestamp='" + functions.getTimestamp() + "' where trackingid =" + trackingId + "");
                        transactionlogger.error("payoutquery "+ trackingId + " "+sb.toString());
                        con     = Database.getConnection();
                        int result  = Database.executeUpdate(sb.toString(), con);
                        transactionlogger.error("payoutquery "+ trackingId + " "+result);

                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_PAYOUT_SUCCESSFUL, ActionEntry.STATUS_PAYOUT_SUCCESSFUL, comm3DResponseVO, auditTrailVO, null);
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.PAYOUT_SUCCESS.toString());

                        if (result != 1)
                        {
                            Database.rollback(con);
                            // asynchronousMailService.sendAdminMail("Exception while Updating status", "Exception has occured while updating status for tracking id=" + trackingId + "Auth message=" + status);
                        }

                    }else if ("REJECTED".equalsIgnoreCase(status))
                    {

                        remark              = message;
                        dbStatus            = PZTransactionStatus.PAYOUT_FAILED.toString();
                        StringBuffer sb     = new StringBuffer();

                        notificationUrl = transactionDetailsVO.getNotificationUrl();

                        transactionDetailsVO.setBillingDesc(displayName);
                        comm3DResponseVO.setDescriptor(displayName);
                        comm3DResponseVO.setStatus("failed");
                        comm3DResponseVO.setRemark(remark);
                        comm3DResponseVO.setDescription("payout failed");
                        comm3DResponseVO.setCurrency(currency);
                        comm3DResponseVO.setTmpl_Amount(tmpl_amt);
                        comm3DResponseVO.setTmpl_Currency(transactionDetailsVO.getTemplatecurrency());
                        //commResponseVO.setTransactionId(pspid);
                        //commResponseVO.setErrorCode(authCode);

                        sb.append("update transaction_common set ");
                       // sb.append(" payoutamount='" + amount + "'");
                        sb.append(" status='payoutfailed'");
                        sb.append(" ,remark='" + remark + "',payouttimestamp='" + functions.getTimestamp() + "' where trackingid =" + trackingId + "");
                        transactionlogger.error("payoutquery "+ trackingId + " "+sb.toString());
                        con         = Database.getConnection();
                        int result  = Database.executeUpdate(sb.toString(), con);
                        transactionlogger.error("payoutquery "+ trackingId + " "+result);

                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_PAYOUT_FAILED, ActionEntry.STATUS_PAYOUT_FAILED, comm3DResponseVO, auditTrailVO, null);
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.PAYOUT_FAILED.toString());

                        if (result != 1)
                        {
                            Database.rollback(con);
                            // asynchronousMailService.sendAdminMail("Exception while Updating status", "Exception has occured while updating status for tracking id=" + trackingId + "Auth message=" + status);
                        }

                    }else{

                        dbStatus        = PZResponseStatus.PENDING.toString();
                        remark         = message;
                    }

                }
                else
                {
                    if (PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                    {
                        billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        status      = "success";
                        if(functions.isValueNull(transactionDetailsVO.getRemark()))
                            message = transactionDetailsVO.getRemark();
                        else
                            message = "Transaction Successful";
                        dbStatus=PZTransactionStatus.AUTH_SUCCESS.toString();
                    }else if (PZTransactionStatus.PAYOUT_SUCCESS.toString().equals(dbStatus))
                    {
                        billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        status      = "success";
                        if(functions.isValueNull(transactionDetailsVO.getRemark()))
                            message = transactionDetailsVO.getRemark();
                        else
                            message = "Transaction Successful";
                        dbStatus=PZTransactionStatus.PAYOUT_SUCCESS.toString();
                    }else if (PZTransactionStatus.PAYOUT_FAILED.toString().equals(dbStatus))
                    {
                        status      = "failed";
                        if(functions.isValueNull(transactionDetailsVO.getRemark()))
                            message = transactionDetailsVO.getRemark();
                        else
                            message = "Transaction failed";
                        dbStatus=PZTransactionStatus.PAYOUT_FAILED.toString();
                    }
                    else if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                    {
                        billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        status      = "success";
                        if(functions.isValueNull(transactionDetailsVO.getRemark()))
                            message = transactionDetailsVO.getRemark();
                        else
                            message = "Transaction Successful";
                        dbStatus=PZTransactionStatus.CAPTURE_SUCCESS.toString();
                    }
                    else if(PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus))
                    {
                        status  = "failed";
                        if(functions.isValueNull(transactionDetailsVO.getRemark()))
                            message = transactionDetailsVO.getRemark();
                        else if(!functions.isValueNull(message))
                            message = "Transaction Failed";
                        dbStatus=PZTransactionStatus.AUTH_FAILED.toString();
                    }
                    else
                    {
                        transactionlogger.error("inside TXFrontEndServlet transactionStatus " + transactionStatus);
                        status      = "pending";
                        message     = "Transaction pending";
                        dbStatus    = PZTransactionStatus.AUTH_STARTED.toString();
                    }
                }
                statusSyncDAO.updateAllTransactionFlowFlag(trackingId,dbStatus);

                /*AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
*/
//                AsynchronousSmsService smsService = new AsynchronousSmsService();
//                smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                genericTransDetailsVO.setOrderId(description);
                genericTransDetailsVO.setAmount(amount);
                genericTransDetailsVO.setCurrency(currency);
                genericTransDetailsVO.setOrderDesc(orderDesc);
                genericTransDetailsVO.setRedirectUrl(redirectUrl);
                genericTransDetailsVO.setNotificationUrl(notificationUrl);
                genericTransDetailsVO.setBillingDiscriptor(billingDesc);
                genericTransDetailsVO.setRedirectMethod(transactionDetailsVO.getRedirectMethod());

                if (functions.isValueNull(email))
                    addressDetailsVO.setEmail(email);
                else
                    addressDetailsVO.setEmail(transactionDetailsVO.getEmailaddr());
                addressDetailsVO.setTmpl_amount(tmpl_amt);
                addressDetailsVO.setTmpl_currency(tmpl_currency);
                addressDetailsVO.setFirstname(firstName);
                addressDetailsVO.setLastname(lastName);
                addressDetailsVO.setCardHolderIpAddress(requestIp);

                cardDetailsVO.setCardNum(ccnum);
                cardDetailsVO.setExpMonth(expMonth);
                cardDetailsVO.setExpYear(expYear);

                commonValidatorVO.setStatus(dbStatus);
                commonValidatorVO.setTrackingid(trackingId);
                commonValidatorVO.setTerminalId(terminalId);
                commonValidatorVO.setReason(message);
                commonValidatorVO.setBankDescription(message);
                commonValidatorVO.setLogoName(logoName);
                commonValidatorVO.setPartnerName(partnerName);
                commonValidatorVO.setPaymentType(paymodeid);
                commonValidatorVO.setCardType(cardtypeid);
                commonValidatorVO.setCustomerId(custId);
                commonValidatorVO.setCustomerBankId(custId);
                commonValidatorVO.setAccountId(accountId);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                commonValidatorVO.setCardDetailsVO(cardDetailsVO);

                transactionUtility.setToken(commonValidatorVO, dbStatus);

                if (functions.isValueNull(notificationUrl))
                {
                    transactionlogger.error("inside sending notification---" + notificationUrl);
                    TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                    transactionDetailsVO1.setBillingDesc(billingDesc);
                    transactionDetailsVO1.setBankReferenceId(trade_no);
                    transactionDetailsVO1.setRemark(message);
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, dbStatus, message, "");
                    transactionlogger.error("remark message----------------------" + message);
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
        }
        catch (Exception e)
        {
            transactionlogger.error("PZDBViolationException:::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("TXFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
        }
        finally
        {
            Database.closePreparedStatement(p);
            Database.closeResultSet(rs);
            Database.closeConnection(con);
        }
    }
}
