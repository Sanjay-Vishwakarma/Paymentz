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
import com.payment.continentPay.ContinentPayUtils;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.reference.DefaultEncoder;
import org.apache.commons.httpclient.Header;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Enumeration;

/**
 * Created by Admin on 2022-04-21.
 */
public class CTBackendServlet extends HttpServlet
{
    private static TransactionLogger transactionlogger = new TransactionLogger(CTBackendServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

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
        transactionlogger.error("Entering  CTBackendServlet......");

        Functions functions             = new Functions();
        ActionEntry entry               = new ActionEntry();
        AuditTrailVO auditTrailVO       = new AuditTrailVO();
        TransactionManager transactionManager       = new TransactionManager();
        CommonValidatorVO commonValidatorVO         = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        MerchantDetailsVO merchantDetailsVO         = new MerchantDetailsVO();
        MerchantDAO merchantDAO                     = new MerchantDAO();
        CommResponseVO commResponseVO               = new CommResponseVO();
        GenericAddressDetailsVO addressDetailsVO    = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO          = new GenericCardDetailsVO();
        StatusSyncDAO statusSyncDAO                 = new StatusSyncDAO();
        CommRequestVO requestVO                     = new CommRequestVO();
        TransactionUtility transactionUtility       = new TransactionUtility();
        CommAddressDetailsVO commAddressDetailsVO   = new CommAddressDetailsVO();
        MySQLCodec me                               = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
        Connection con          = null;
        String toid             = "";
        String description      = "";
        String redirectUrl      = "";
        String accountId        = "";
        String orderDesc        = "";
        String currency         = "";
        String displayName      = "";
        String logoName         = "";
        String partnerName      = "";
        String amount           = "";
        String trackingId       = req.getParameter("trackingId");
        String status           = "";
        String remark           = "";
        String email            = "";
        String tmpl_amt         = "";
        String tmpl_currency    = "";
        String firstName        = "";
        String lastName         = "";
        String custId           = "";
        String transactionId    = "";
        String message          = "";
        String billingDesc      = "";
        String dbStatus         = "";
        String notificationUrl  = "";
        String ccnum            = "";
        String expMonth         = "";
        String expYear          = "";
        String requestIp        = "";
        String paymentId        = "";
        String RRN              = "";
        String name             = "";
        String terminalId       = "";
        String respAmount       = "";
        String respStatus       = "";
        String respCurrency     = "";
        String respErrors       = "";
        String respRedirect_url = "";
        String XSignature       = "";
        String created_at       = "";
        String crypto_currency  = "";
        String EncryptedResponse = "";
        String respAuthorize_status = "";
        PrintWriter pWriter = res.getWriter();
        String responseCode = "200";
        String returnResStatus = "OK";

        try
        {
            transactionlogger.error("----- CTBackendServlet  response-----" + trackingId);

            Enumeration requestHeaders =  req.getHeaderNames();
            transactionlogger.error(trackingId+"====> callback Headers[]:");
            while (requestHeaders.hasMoreElements())
            {
                String key = (String) requestHeaders.nextElement();
                String value = req.getHeader(key);
                transactionlogger.error(trackingId + " ---Key---" + key + "---Value---" + value);
            }

            XSignature = req.getHeader("x-signature");

            transactionlogger.error(trackingId+"====> callback parameters[]:");
            Enumeration enumeration = req.getParameterNames();
            while (enumeration.hasMoreElements())
            {
                String key = (String) enumeration.nextElement();
                String value = req.getParameter(key);
                transactionlogger.error(trackingId + " ---Key---" + key + "---Value---" + value);
            }

            StringBuilder responseMsg = new StringBuilder();
            BufferedReader bufferedReader = req.getReader();
            String string;
            while ((string = bufferedReader.readLine()) != null)
            {
                responseMsg.append(string);
            }
            transactionlogger.error(trackingId+"----- CTBackendServlet  response-----" + responseMsg);

            JSONObject responseJSON = new JSONObject(responseMsg);
            if (responseJSON.has("transaction") && functions.isValueNull(String.valueOf(responseJSON.getJSONObject("transaction"))))
            {
                JSONObject transactionJSON = new JSONObject(responseJSON.getJSONObject("transaction"));

                if (transactionJSON.has("uuid") && functions.isValueNull(transactionJSON.getString("uuid")))
                {
                    transactionId = transactionJSON.getString("uuid");
                }

                if (transactionJSON.has("status") && functions.isValueNull(transactionJSON.getString("status")))
                {
                    respStatus = transactionJSON.getString("status");
                }

                if (transactionJSON.has("amount") && functions.isValueNull(transactionJSON.getString("amount")))
                {
                    respAmount = transactionJSON.getString("amount");
                }

                if (transactionJSON.has("currency") && functions.isValueNull(transactionJSON.getString("currency")))
                {
                    respCurrency = transactionJSON.getString("currency");
                }

                if (transactionJSON.has("crypto_currency") && functions.isValueNull(transactionJSON.getString("crypto_currency")))
                {
                    crypto_currency = transactionJSON.getString("crypto_currency");
                }

                if (transactionJSON.has("created_at") && functions.isValueNull(transactionJSON.getString("created_at")))
                {
                    created_at = transactionJSON.getString("created_at");
                }

                if (transactionJSON.has("errors") && functions.isValueNull(String.valueOf(transactionJSON.getJSONArray("errors"))))
                {
                    JSONArray errorArray = transactionJSON.getJSONArray("errors");
                    for (int i = 0; i < errorArray.length(); i++)
                    {
                        respErrors += errorArray.getString(i);
                    }
                }
                else if (responseJSON.has("errors") && functions.isValueNull(String.valueOf(responseJSON.getJSONArray("errors"))))
                {
                    JSONArray errorArray = responseJSON.getJSONArray("errors");
                    for (int i = 0; i < errorArray.length(); i++)
                    {
                        respErrors += errorArray.getString(i);
                    }
                }

            }


            if (functions.isValueNull(trackingId))
            {
                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                if (transactionDetailsVO != null)
                {
                    if (functions.isValueNull(transactionDetailsVO.getCcnum()))
                    {
                        ccnum = PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
                    }
                    toid = transactionDetailsVO.getToid();
                    description = transactionDetailsVO.getDescription();
                    redirectUrl = transactionDetailsVO.getRedirectURL();
                    accountId = transactionDetailsVO.getAccountId();
                    orderDesc = transactionDetailsVO.getOrderDescription();
                    currency = transactionDetailsVO.getCurrency();
                    amount = transactionDetailsVO.getAmount();

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

                    firstName   = transactionDetailsVO.getFirstName();
                    lastName    = transactionDetailsVO.getLastName();
                    name        = transactionDetailsVO.getFirstName() + " " + transactionDetailsVO.getLastName();
                    requestIp   = Functions.getIpAddress(req);
                    custId      = transactionDetailsVO.getCustomerId();
                    transactionlogger.error("trackingId-> " + trackingId + " sale transactionId ----->" + transactionDetailsVO.getPaymentId());
                    transactionlogger.error("trackingId-> " + trackingId + " callback transactionId ----->" + transactionId);
                    if (!functions.isValueNull(transactionId))
                    {
                        transactionId = paymentId;
                    }

                    if (functions.isValueNull(respCurrency))
                    {
                        currency = respCurrency;
                    }

                    dbStatus = transactionDetailsVO.getStatus();
                    transactionlogger.error("dbStatus--> " + trackingId + " " + dbStatus);

                    merchantDetailsVO   = merchantDAO.getMemberDetails(toid);
                    logoName            = merchantDetailsVO.getLogoName();
                    partnerName         = merchantDetailsVO.getPartnerName();
                    email               = transactionDetailsVO.getEmailaddr();

                    auditTrailVO.setActionExecutorName("AcquirerBackEnd");
                    auditTrailVO.setActionExecutorId(toid);

                    if (functions.isValueNull(respErrors))
                        remark = respErrors;
                    else
                        remark = respStatus;

                    displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

                    EncryptedResponse = ContinentPayUtils.getHmac256Signature(String.valueOf(responseMsg),GatewayAccountService.getGatewayAccount(accountId).getMerchantId().getBytes());
                    transactionlogger.error(trackingId+ " >>>>>>> EncryptedResponse: " + EncryptedResponse+ "  XSignature: " + XSignature);
                    if (functions.isValueNull(XSignature) && !XSignature.equalsIgnoreCase(EncryptedResponse))
                    {
                        transactionlogger.error(trackingId+" =>>>> response XSignature does not match with encryption");
                        respStatus = "error";
                        remark     = "XSignature mismatch";
                    }

                    if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus))
                    {
                        requestVO.setTransDetailsVO(commTransactionDetailsVO);
                        requestVO.setAddressDetailsVO(commAddressDetailsVO);
                        transactionlogger.error("inside AUTH_STARTED--- " + trackingId + " " + requestVO.getTransDetailsVO().getPreviousTransactionId());
                        con = Database.getConnection();
                        StringBuffer dbBuffer = new StringBuffer();
                        transactionlogger.error("resp Amount ----- " + respAmount);

                        commResponseVO.setTmpl_Amount(tmpl_amt);
                        commResponseVO.setTmpl_Currency(tmpl_currency);
                        commResponseVO.setIpaddress(requestIp);
                        commResponseVO.setDescription(remark);
                        commResponseVO.setRemark(remark);
                        commResponseVO.setAmount(amount);
                        commResponseVO.setCurrency(currency);
                        commResponseVO.setBankDescription(remark);
                        commResponseVO.setBankTransactionDate(created_at);

                        transactionlogger.error(trackingId+" inside CTBackendServlet transactionStatus " + respStatus);

                        if ("purchase_complete".equalsIgnoreCase(respStatus))
                        {
                            transactionlogger.error("inside success CTBackendServlet transactionStatus " + respStatus);

                            if (functions.isValueNull(remark))
                                message = remark;
                            else if (functions.isValueNull(respStatus))
                                message = respStatus;
                            else
                                message = "Payment successful";

                            notificationUrl = transactionDetailsVO.getNotificationUrl();
                            status = "success";
                            billingDesc = displayName;
                            commResponseVO.setDescriptor(billingDesc);
                            dbStatus = "capturesuccess";
                            commResponseVO.setStatus(dbStatus);

                            dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency + "',rrn='" + RRN + "" + "',status='capturesuccess'" + " ,successtimestamp='" + functions.getTimestamp());
                            dbBuffer.append("',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' ,remark='" + ESAPI.encoder().encodeForSQL(me, message) + "' where trackingid = " + trackingId);
                            transactionlogger.error("Update Query---" + dbBuffer);

                            Database.executeUpdate(dbBuffer.toString(), con);
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, requestIp);//+ ",rrn='" + rrn
                        }
                        else if ("purchase_declined".equalsIgnoreCase(respStatus) || "error".equalsIgnoreCase(respStatus)|| "break".equalsIgnoreCase(respStatus) || functions.isValueNull(respErrors))
                        {
                            transactionlogger.error("inside fail or error CTBackendServlet transactionStatus " + respStatus + " "+ respErrors + " " + remark);

                            if (functions.isValueNull(remark))
                                message = remark;
                            else if (functions.isValueNull(respStatus))
                                message = respStatus;
                            else
                                message = "Transaction Failed";

                            notificationUrl = transactionDetailsVO.getNotificationUrl();
                            status = "failed";
                            dbStatus = "authfailed";
                            commResponseVO.setStatus(dbStatus);

                            dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency + "'" + " ,failuretimestamp='" + functions.getTimestamp());
                            dbBuffer.append("',authorization_code='" + respAuthorize_status + "',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' ,remark='" + ESAPI.encoder().encodeForSQL(me, message) + "' where trackingid = " + trackingId);
                            transactionlogger.error("Update Query---" + dbBuffer);
                            Database.executeUpdate(dbBuffer.toString(), con);
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, requestIp);
                        }
                        else
                        {
                            transactionlogger.error("inside else pending CTBackendServlet transactionStatus " + respStatus);
                            //pending
                            dbStatus = "authstarted";
                            status = "Pending";
                            message = "Pending";
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

                    /*AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
*/
//                    AsynchronousSmsService smsService = new AsynchronousSmsService();
//                    smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);


                    merchantDetailsVO = merchantDAO.getMemberDetails(toid);
                    genericTransDetailsVO.setOrderId(description);
                    genericTransDetailsVO.setAmount(amount);
                    genericTransDetailsVO.setCurrency(currency);
                    genericTransDetailsVO.setOrderDesc(orderDesc);
                    genericTransDetailsVO.setRedirectUrl(redirectUrl);
                    genericTransDetailsVO.setNotificationUrl(notificationUrl);
                    genericTransDetailsVO.setBillingDiscriptor(billingDesc);
                    genericTransDetailsVO.setTransactionDate(created_at);
                    addressDetailsVO.setTmpl_amount(tmpl_amt);
                    addressDetailsVO.setTmpl_currency(tmpl_currency);
                    addressDetailsVO.setFirstname(firstName);
                    addressDetailsVO.setLastname(lastName);
                    addressDetailsVO.setCardHolderIpAddress(requestIp);
                    if (functions.isValueNull(email))
                        addressDetailsVO.setEmail(email);
                    else
                        addressDetailsVO.setEmail(transactionDetailsVO.getEmailaddr());

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
                    commonValidatorVO.setReason(message);
                    commonValidatorVO.setBankDescription(message);
                    commonValidatorVO.setStatus(dbStatus);
                    commonValidatorVO.setTrackingid(trackingId);
                    commonValidatorVO.setTerminalId(terminalId);
                    commonValidatorVO.setReason(message);
                    commonValidatorVO.setLogoName(logoName);
                    commonValidatorVO.setPartnerName(partnerName);
                    commonValidatorVO.setPaymentType(transactionDetailsVO.getPaymodeId());
                    commonValidatorVO.setCardType(transactionDetailsVO.getCardTypeId());
                    commonValidatorVO.setCustomerId(custId);
                    commonValidatorVO.setCustomerBankId(custId);
                    commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                    commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                    commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

                    if (functions.isValueNull(notificationUrl))
                    {
                        transactionlogger.error("inside sending notification---" + notificationUrl);
                        TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                        transactionDetailsVO1.setBillingDesc(billingDesc);
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
            PZExceptionHandler.raiseAndHandleDBViolationException("CTBackendServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
        }
        catch (Exception e)
        {
            transactionlogger.error("PZDBViolationException:::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("CTBackendServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
}
