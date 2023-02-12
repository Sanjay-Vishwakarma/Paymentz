package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.BlacklistManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.BlacklistVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.omnipay.OmnipayPaymentGatewayUtils;
import com.payment.response.PZResponseStatus;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.log4j.LogManager;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;


/**
 * Created by Admin on 2022-02-08.
 */
public class OMBackendServlet extends HttpServlet
{

    private static TransactionLogger transactionlogger  = new TransactionLogger(OMBackendServlet.class.getName());
    String ctoken                                       = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);
    org.apache.log4j.Logger facileroLogger              = LogManager.getLogger("facilerolog");

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
        transactionlogger.error("Entering  OMBackendServlet......");

        Functions functions                             = new Functions();
        ActionEntry entry                               = new ActionEntry();
        AuditTrailVO auditTrailVO                       = new AuditTrailVO();
        TransactionManager transactionManager           = new TransactionManager();
        CommonValidatorVO commonValidatorVO             = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO     = new GenericTransDetailsVO();
        MerchantDetailsVO merchantDetailsVO             = new MerchantDetailsVO();
        MerchantDAO merchantDAO                         = new MerchantDAO();
        CommResponseVO commResponseVO                   = new CommResponseVO();
        GenericAddressDetailsVO addressDetailsVO        = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO              = new GenericCardDetailsVO();
        StatusSyncDAO statusSyncDAO                     = new StatusSyncDAO();
        CommRequestVO requestVO                         = new CommRequestVO();
        TransactionUtility transactionUtility           = new TransactionUtility();
        CommAddressDetailsVO commAddressDetailsVO       = new CommAddressDetailsVO();
        MySQLCodec me                                       = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        CommTransactionDetailsVO commTransactionDetailsVO   = new CommTransactionDetailsVO();
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
        String amount = "";
        String trackingId = req.getParameter("trackingId");
        String status = "";
        String remark = "";
        String email = "";
        String tmpl_amt = "";
        String tmpl_currency = "";
        String firstName = "";
        String lastName = "";
        String custId = "";
        String transactionStatus = "";
        String transactionId = "";
        String message = "";
        String billingDesc = "";
        String dbStatus = "";
        String notificationUrl = "";
        String ccnum = "";
        String expMonth = "";
        String expYear = "";
        String requestIp = "";
        String paymentId = "";
        String RRN = "";
        String name = "";
        String terminalId = "";
        String respAmount = "";
        String respStatus = "";
        String type = "";
        String rrn = "";
        String captureAmount = "";
        String respMessage = "";
        String respAuthorize_status = "";
        PrintWriter pWriter = res.getWriter();
        String responseCode = "200";
        String returnResStatus = "OK";

        try
        {
            transactionlogger.error("----- OMBackend  response-----" + trackingId);
            Enumeration enumeration         = req.getParameterNames();
            StringBuilder responseBackend   = new StringBuilder();
            while (enumeration.hasMoreElements())
            {
                String key      = (String) enumeration.nextElement();
                String value    = req.getParameter(key);
                transactionlogger.error(trackingId + " ---Key---" + key + "---Value---" + value);
                responseBackend.append(key+"="+value+"&");
            }

            StringBuilder responseMsg       = new StringBuilder();
            BufferedReader bufferedReader   = req.getReader();
            String string;

            while ((string = bufferedReader.readLine()) != null)
            {
                responseMsg.append(string);
            }
            transactionlogger.error(trackingId+"----- OMBackend  response-----" + responseMsg);

            if (functions.isValueNull(req.getParameter("amount")))
            {
                respAmount = req.getParameter("amount");
                respAmount = String.format("%.2f", Double.parseDouble(respAmount)/100);
            }

            if (functions.isValueNull(req.getParameter("message")))
            {
                respMessage = req.getParameter("message");
            }

            if (functions.isValueNull(req.getParameter("authorize_status")))
            {
                respAuthorize_status = req.getParameter("authorize_status");
            }

            if (functions.isValueNull(req.getParameter("status")))
            {
                respStatus = req.getParameter("status");
            }
            if (functions.isValueNull(req.getParameter("type")))
            {
                type = req.getParameter("type");
            }
            if (functions.isValueNull(req.getParameter("rrn")))
            {
                rrn = req.getParameter("rrn");
            }

            if (functions.isValueNull(trackingId))
            {
                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                if (transactionDetailsVO != null)
                {
                    if("Facilero".equalsIgnoreCase(transactionDetailsVO.getTotype())){
                        facileroLogger.error("OmniPay Backend Response  ----> "+trackingId+" "+responseBackend.toString());
                    }
                    if (functions.isValueNull(transactionDetailsVO.getCcnum()))
                    {
                        ccnum = PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
                    }
                    toid            = transactionDetailsVO.getToid();
                    description     = transactionDetailsVO.getDescription();
                    redirectUrl     = transactionDetailsVO.getRedirectURL();
                    accountId       = transactionDetailsVO.getAccountId();
                    orderDesc       = transactionDetailsVO.getOrderDescription();
                    currency        = transactionDetailsVO.getCurrency();
                    transactionlogger.error("db amount-->: "+ transactionDetailsVO.getAmount());
                    amount          = transactionDetailsVO.getAmount();

                    paymentId = transactionDetailsVO.getPaymentId();
                    terminalId = transactionDetailsVO.getTerminalId();

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

                    firstName       = transactionDetailsVO.getFirstName();
                    lastName        = transactionDetailsVO.getLastName();
                    name            = transactionDetailsVO.getFirstName() + " " + transactionDetailsVO.getLastName();
                    requestIp       = Functions.getIpAddress(req);
                    custId          = transactionDetailsVO.getCustomerId();
                    transactionId   = transactionDetailsVO.getPaymentId();
                    transactionlogger.error("trackingId-> " + trackingId + " transactionId ----->" + transactionDetailsVO.getPaymentId());
                    dbStatus        = transactionDetailsVO.getStatus();
                    transactionlogger.error("dbStatus--> " + trackingId + " " + dbStatus);

                    merchantDetailsVO   = merchantDAO.getMemberDetails(toid);
                    clkey               = merchantDetailsVO.getKey();
                    checksumAlgo        = merchantDetailsVO.getChecksumAlgo();
                    autoredirect        = merchantDetailsVO.getAutoRedirect();
                    isPowerBy           = merchantDetailsVO.getIsPoweredBy();
                    logoName            = merchantDetailsVO.getLogoName();
                    isService           = merchantDetailsVO.getIsService();
                    partnerName         = merchantDetailsVO.getPartnerName();
                    email               = transactionDetailsVO.getEmailaddr();

                    auditTrailVO.setActionExecutorName("AcquirerBackEnd");
                    auditTrailVO.setActionExecutorId(toid);
                    BlacklistManager blacklistManager   = new BlacklistManager();
                    BlacklistVO blacklistVO             = new BlacklistVO();

                    displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    Thread.sleep(5000);
                    transactionlogger.error("dbStatus " +trackingId+" "+ dbStatus);
                    if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus) ||( PZTransactionStatus.AUTH_FAILED.toString().equalsIgnoreCase(dbStatus)&& !"PLATFORM_ORDER_BILLING_ERROR".equalsIgnoreCase(respMessage)))
                    {
                        transactionlogger.error("accountid backend--->" + accountId);
                        requestVO.setTransDetailsVO(commTransactionDetailsVO);
                        requestVO.setAddressDetailsVO(commAddressDetailsVO);
                        transactionlogger.error("inside AUTH_STARTED--- " + trackingId + " " + requestVO.getTransDetailsVO().getPreviousTransactionId());
                        con                     = Database.getConnection();
                        StringBuffer dbBuffer   = new StringBuffer();
                        transactionlogger.error("resp Amount ----- " + respAmount);

                        if (functions.isValueNull(respAmount))
                        {
                            Double compRsAmount = Double.valueOf(respAmount);
                            Double compDbAmount = Double.valueOf(amount);
                            transactionlogger.error("Inside if response amount --->" + compRsAmount);
                            transactionlogger.error(" DB Amount--->" + compDbAmount);
                            if (compDbAmount.equals(compRsAmount))
                            {
                                transactionStatus   = respStatus;
                                    remark              = respMessage;
                                amount              = respAmount;
                            }
                            else if (!compDbAmount.equals(compRsAmount) && "2".equalsIgnoreCase(respStatus))
                            {
                                transactionStatus   = "-1";     //status = -1: - Transaction rejected
                                remark              = "Failed-IRA";
                                amount              = respAmount;
                                transactionlogger.error(trackingId +" OMbackend inside else Amount incorrect--->" + respAmount);

                                blacklistVO.setVpaAddress(custId);
                                blacklistVO.setIpAddress(requestIp);
                                blacklistVO.setEmailAddress(email);
                                blacklistVO.setActionExecutorId(toid);
                                blacklistVO.setActionExecutorName("OMBackend");
                                blacklistVO.setRemark("IncorrectAmount");

                                blacklistManager.commonBlackListing(blacklistVO);
                            }
                        }
                        transactionlogger.error("remark message  bank----------------------------------->" + respMessage);

                        commResponseVO.setTmpl_Amount(tmpl_amt);
                        commResponseVO.setTmpl_Currency(tmpl_currency);
                        commResponseVO.setIpaddress(requestIp);
                        commResponseVO.setDescription(remark);
                        commResponseVO.setRemark(remark);
                        commResponseVO.setAmount(amount);
                        commResponseVO.setCurrency(currency);
                        commResponseVO.setBankDescription(respMessage);

                        transactionlogger.error("inside OMBackendServlet transactionStatus " + transactionStatus);

                        if ("2".equalsIgnoreCase(transactionStatus))   //status = 2: - Payment successful
                        {
                            transactionlogger.error("inside success OMBackendServlet transactionStatus " + transactionStatus);
                            notificationUrl = transactionDetailsVO.getNotificationUrl();
                            status = "success";
                            billingDesc = displayName;
                            if (functions.isValueNull(remark))
                            {
                                message = remark;
                            }
                            else
                            {
                                message = "Payment successful";
                            }
                            commResponseVO.setDescriptor(billingDesc);

                            dbStatus = "capturesuccess";

                            commResponseVO.setStatus(dbStatus);

                            dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency + "',rrn='" + RRN + "" + "',status='capturesuccess'" + " ,successtimestamp='" + functions.getTimestamp());
                            dbBuffer.append("',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' ,remark='" + ESAPI.encoder().encodeForSQL(me, message) + "' where trackingid = " + trackingId);
                            transactionlogger.error("Update Query---" + dbBuffer);

                            Database.executeUpdate(dbBuffer.toString(), con);
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, requestIp);//+ ",rrn='" + rrn
                        }
                        else if ("-1".equalsIgnoreCase(transactionStatus))    //status = -1: - Transaction rejected
                        {
                            transactionlogger.error("inside fail or error OMBackendServlet transactionStatus " + transactionStatus);
                            notificationUrl = transactionDetailsVO.getNotificationUrl();
                            status          = "failed";
                            if (functions.isValueNull(remark))
                            {
                                message = remark;
                            }
                            else
                            {
                                message = "Transaction rejected";
                            }

                            dbStatus = "authfailed";
                            dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency + "'" + " ,failuretimestamp='" + functions.getTimestamp());

                            dbBuffer.append("',authorization_code='" + respAuthorize_status + "',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' ,remark='" + ESAPI.encoder().encodeForSQL(me, message) + "' where trackingid = " + trackingId);
                            transactionlogger.error("Update Query---" + dbBuffer);
                            Database.executeUpdate(dbBuffer.toString(), con);
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, requestIp);
                        }
                        else
                        {
                            transactionlogger.error("inside else pending OMBackendServlet transactionStatus " + transactionStatus);
                            //pending
                            dbStatus = "authstarted";
                            status = "Pending";
                            message = "Pending";
                        }
                    }
                    //REFUND
                    else if (type.equalsIgnoreCase("Refund"))
                    {
                        if (PZTransactionStatus.MARKED_FOR_REVERSAL.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.REVERSED.toString().equalsIgnoreCase(dbStatus))
                        {
                            if ("2".equalsIgnoreCase(respStatus))
                            {
                                transactionlogger.error("--- in REFUND , reversesuccess ---");
                                notificationUrl = transactionDetailsVO.getNotificationUrl();
                                status              = "success";
                                displayName         = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                                if(functions.isValueNull(respMessage)){
                                    message              = respMessage;
                                }else{
                                    message              = "Reversed";
                                }

                                auditTrailVO.setActionExecutorName("AcquirerBackEnd");
                                auditTrailVO.setActionExecutorId(toid);

                                String confirmStatus        = "Y";
                                dbStatus                    = PZTransactionStatus.REVERSED.toString();
                                String refundstatus         = "reversed";
                                //String refundedAmount       = respAmount;
                                String refundedAmount       = transactionDetailsVO.getRefundAmount();
                                captureAmount               = transactionDetailsVO.getCaptureAmount();
                                double refAmount            = Double.parseDouble(refundedAmount);
                                transactionlogger.error("remark====>"+remark);

                                transactionlogger.error("double refAmount================>"+refAmount);
                                String actionEntryAction = ActionEntry.ACTION_REVERSAL_SUCCESSFUL;
                                String actionEntryStatus = ActionEntry.STATUS_REVERSAL_SUCCESSFUL;
                                transactionStatus        = PZResponseStatus.SUCCESS.toString();

                                commResponseVO.setStatus("success");
                                commResponseVO.setRemark(message);
                                commResponseVO.setDescriptor(displayName);
                                commResponseVO.setTransactionStatus(status);
                               // commResponseVO.setTransactionId(pspid);
                                commResponseVO.setCurrency(currency);
                                commResponseVO.setTmpl_Amount(tmpl_amt);
                                commResponseVO.setTmpl_Currency(transactionDetailsVO.getTemplatecurrency());
                                commResponseVO.setTransactionType(type);
                                //commResponseVO.setErrorCode(authCode);
                                //commResponseVO.setAuthCode(authCode);
                                commResponseVO.setRrn(rrn);
                                commResponseVO.setDescription("Reversal Successful");

                                transactionlogger.error("captureAmount==========>"+captureAmount);
                                transactionlogger.error("refAmount============>"+refAmount);
                                if (Double.parseDouble(captureAmount) > Double.parseDouble(String.valueOf(refAmount)))
                                {
                                    status              = "reversed";
                                    dbStatus            = "reversed";
                                    actionEntryAction   = ActionEntry.ACTION_PARTIAL_REFUND;
                                    actionEntryStatus   = ActionEntry.STATUS_PARTIAL_REFUND;
                                    transactionStatus   = PZResponseStatus.PARTIALREFUND.toString();
                                }

                                if (captureAmount.equals(String.format("%.2f", refAmount)))
                                {
                                    status = "reversed";
                                    actionEntryAction = ActionEntry.ACTION_REVERSAL_SUCCESSFUL;
                                    actionEntryStatus = ActionEntry.STATUS_REVERSAL_SUCCESSFUL;
                                    transactionStatus = PZResponseStatus.SUCCESS.toString();
                                }
                                StringBuffer dbBuffer = new StringBuffer();
                                dbBuffer.append("update transaction_common set status='reversed',refundAmount='" + String.format("%.2f", refAmount) +  "',refundtimestamp='" + functions.getTimestamp() +  "',rrn='"+ rrn + "'where trackingid = " + trackingId);
                                transactionlogger.error("dbBuffer->" + dbBuffer);

                                con = Database.getConnection();
                                Database.executeUpdate(dbBuffer.toString(), con);

                                //entry.actionEntryForCommon(trackingId, String.format("%.2f", refAmount), actionEntryAction, actionEntryStatus, commResponseVO, auditTrailVO, null);
                                String updatedStatus = PZTransactionStatus.REVERSED.toString();
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);

                               /* if (functions.isValueNull(notificationUrl))
                                {
                                    transactionlogger.error("inside sending notification---" + notificationUrl);
                                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                                    transactionDetailsVO.setBillingDesc(displayName);
                                    asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, updatedStatus, remark, "");
                                }*/

                            }
                        }

                    }
                    else
                    {
                        if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                        {
                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                            status = "success";
                            if (functions.isValueNull(transactionDetailsVO.getRemark()))
                                message = transactionDetailsVO.getRemark();
                            else
                                message = "Transaction Successful";
                            dbStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                        }
                        else if (PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus))
                        {
                            status = "failed";
                            if (functions.isValueNull(transactionDetailsVO.getRemark()))
                                message = transactionDetailsVO.getRemark();
                            else if (!functions.isValueNull(message))
                                message = "Transaction Failed";
                            dbStatus = PZTransactionStatus.AUTH_FAILED.toString();
                        }
                        else
                        {
                            status = "pending";
                            message = "Transaction pending";
                            dbStatus = PZTransactionStatus.AUTH_STARTED.toString();
                        }
                    }

                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);
                   /* AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
*/
//                    AsynchronousSmsService smsService = new AsynchronousSmsService();
//                    smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                    commonValidatorVO.setStatus(dbStatus);
                    commonValidatorVO.setTrackingid(trackingId);
                    commonValidatorVO.setTerminalId(terminalId);
                    commonValidatorVO.setReason(message);
                    genericTransDetailsVO.setOrderId(description);
                    genericTransDetailsVO.setAmount(amount);
                    genericTransDetailsVO.setCurrency(currency);
                    genericTransDetailsVO.setOrderDesc(orderDesc);
                    genericTransDetailsVO.setRedirectUrl(redirectUrl);
                    genericTransDetailsVO.setNotificationUrl(notificationUrl);
                    genericTransDetailsVO.setBillingDiscriptor(billingDesc);
                    commonValidatorVO.setLogoName(logoName);
                    commonValidatorVO.setPartnerName(partnerName);
                    commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                    merchantDetailsVO = merchantDAO.getMemberDetails(toid);
                    addressDetailsVO.setTmpl_amount(tmpl_amt);
                    addressDetailsVO.setTmpl_currency(tmpl_currency);
                    addressDetailsVO.setFirstname(firstName);
                    addressDetailsVO.setLastname(lastName);
                    addressDetailsVO.setCardHolderIpAddress(requestIp);
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    commonValidatorVO.setPaymentType(transactionDetailsVO.getPaymodeId());
                    commonValidatorVO.setCardType(transactionDetailsVO.getCardTypeId());

                    if (functions.isValueNull(email))
                        addressDetailsVO.setEmail(email);

                    else
                        addressDetailsVO.setEmail(transactionDetailsVO.getEmailaddr());

                    commonValidatorVO.setCustomerId(custId);
                    commonValidatorVO.setCustomerBankId(custId);

                    if (functions.isValueNull(transactionDetailsVO.getExpdate()))
                    {
                        transactionlogger.error("expDate --->" + transactionDetailsVO.getExpdate());
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
                    if (functions.isValueNull(ccnum))
                    {
                        cardDetailsVO.setCardNum(ccnum);
                    }
                    cardDetailsVO.setExpMonth(expMonth);
                    cardDetailsVO.setExpYear(expYear);
                    commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                    commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                    commonValidatorVO.setReason(message);
                    commonValidatorVO.setBankDescription(respMessage);
                    transactionlogger.error("backend notification---" + trackingId +" "+ notificationUrl);
                    if (functions.isValueNull(notificationUrl))
                    {
                        transactionlogger.error("inside sending notification---" + trackingId +"  "+notificationUrl);
                        TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                        transactionDetailsVO1.setBillingDesc(billingDesc);
                        transactionDetailsVO1.setBankReferenceId(transactionId);
                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                        asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, dbStatus, message, "");
                    }

                    JSONObject jsonResObject = new JSONObject();
                    jsonResObject.put("responseCode", responseCode);
                    jsonResObject.put("responseStatus", returnResStatus);
                    transactionlogger.error("finalResponseSend--------> " + trackingId + " " + jsonResObject.toString());
                    res.setContentType("application/json");
                    res.setStatus(200);
                    pWriter.println(jsonResObject.toString());
                    pWriter.flush();
                    return;
                }
            }
        }

        catch (NullPointerException e)
        {
            transactionlogger.error("NullPointerException:::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("OMBackendServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
        }

        catch (Exception e)
        {
            transactionlogger.error("PZDBViolationException:::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("OMBackendServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
}