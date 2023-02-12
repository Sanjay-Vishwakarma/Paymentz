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
import com.payment.easypay.EasyPaymentGatewayUtils;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;
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
import java.util.Map;

/**
 * Created by Admin on 2022-01-05.
 */
public class EPBackendServlet extends HttpServlet
{

    private static TransactionLogger transactionlogger = new TransactionLogger(EPBackendServlet.class.getName());
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
        transactionlogger.error("Entering  EPBackendServlet......");

        Functions functions                                 = new Functions();
        ActionEntry entry                                   = new ActionEntry();
        AuditTrailVO auditTrailVO                           = new AuditTrailVO();
        TransactionManager transactionManager               = new TransactionManager();
        CommonValidatorVO commonValidatorVO                 = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO         = new GenericTransDetailsVO();
        MerchantDetailsVO merchantDetailsVO                 = new MerchantDetailsVO();
        MerchantDAO merchantDAO                             = new MerchantDAO();
        CommResponseVO commResponseVO                       = new CommResponseVO();
        GenericAddressDetailsVO addressDetailsVO            = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO                  = new GenericCardDetailsVO();
        StatusSyncDAO statusSyncDAO                         = new StatusSyncDAO();
        CommRequestVO requestVO                             = new CommRequestVO();
        TransactionUtility transactionUtility               = new TransactionUtility();
        CommAddressDetailsVO commAddressDetailsVO           = new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = new CommTransactionDetailsVO();
        Connection con              = null;
        PreparedStatement p         = null;
        ResultSet rs                = null;
        String toid                 = "";
        String description          = "";
        String redirectUrl          = "";
        String accountId            = "";
        String orderDesc            = "";
        String currency             = "";
        String clkey        = "";
        String checksumAlgo = "";
        String checksum     = "";
        String autoredirect = "";
        String isService    = "";
        String displayName          = "";
        String isPowerBy    = "";
        String logoName             = "";
        String partnerName          = "";
        String amount               = "";
        String trackingId           = req.getParameter("trackingId");
        String status               = "";
        String remark               = "";
        String email                = "";
        String tmpl_amt             = "";
        String tmpl_currency        = "";
        String firstName            = "";
        String lastName             = "";
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
        String paymentId            = "";
        String  RRN                 = "";
        String name                 = "";
        String terminalId           = "";
        String subscriptionPlan     = "";
        PrintWriter pWriter = res.getWriter();
        String responseCode = "200";
        String returnResStatus = "OK";

        try
        {

            StringBuilder responseMsg   = new StringBuilder();
            Enumeration enumeration     = req.getParameterNames();

            while (enumeration.hasMoreElements())
            {
                String key      = (String) enumeration.nextElement();
                String value    = req.getParameter(key);
                transactionlogger.error("---Key---" + key + "---Value---" + value);
            }

            BufferedReader bufferedReader = req.getReader();
            String string;

            while ((string = bufferedReader.readLine()) != null)
            {
                responseMsg.append(string);
            }
            transactionlogger.error("----- EPBACKEND  response-----" + responseMsg);

            String splitStatus      = "";
            String splitTrackingId  = "";

            transactionlogger.error("EPBackendServlet combined trackingId with status: " + trackingId);
            if (trackingId.split("_").length > 0)
            {
                splitTrackingId = trackingId.split("_")[0];
                splitStatus = trackingId.split("_")[1];
            }


            Map<String, String> resultMap = EasyPaymentGatewayUtils.readEasyPayment3DRedirectionXMLReponse(responseMsg.toString());
            transactionlogger.error("XML READED response :" + splitTrackingId + " " + resultMap);

            String respStatus           = "";
            String respAmount           = "";
            String respCurrency         = "";
            String respMessage          = "";
            String respPaymentMethod    = "";
            String respOperation        = "";
            String code                 = "";
            String details              = "";
            String payFrexTransactionId = "";
            String rfOperationDateTime  = "";


            if (resultMap != null)
            {
                if (resultMap.containsKey("status") && functions.isValueNull(resultMap.get("status")))
                {
                    respStatus = resultMap.get("status");
                }
                if (resultMap.containsKey("message") && functions.isValueNull(resultMap.get("message")))
                {
                    respMessage = resultMap.get("message");
                }
                if (resultMap.containsKey("amount") && functions.isValueNull(resultMap.get("amount")))
                {
                    respAmount = resultMap.get("amount");
                }
                if (resultMap.containsKey("currency") && functions.isValueNull(resultMap.get("currency")))
                {
                    respCurrency = resultMap.get("currency");
                }
                if (resultMap.containsKey("paymentMethod") && functions.isValueNull(resultMap.get("paymentMethod")))
                {
                    respPaymentMethod = resultMap.get("paymentMethod");
                }
                if (resultMap.containsKey("code") && functions.isValueNull(resultMap.get("code")))
                {
                    code = resultMap.get("code");
                }
                if (resultMap.containsKey("payFrexTransactionId") && functions.isValueNull(resultMap.get("payFrexTransactionId")))
                {
                    payFrexTransactionId = resultMap.get("payFrexTransactionId");
                }
                if (resultMap.containsKey("details") && functions.isValueNull(resultMap.get("details")))
                {
                    details = resultMap.get("details");
                }
                if (resultMap.containsKey("subscriptionPlan") && functions.isValueNull(resultMap.get("subscriptionPlan")))
                {
                    subscriptionPlan = resultMap.get("subscriptionPlan");
                }

                if (functions.isValueNull(details))
                {
                    JSONObject detailsJson = new JSONObject(details);
                    if (detailsJson.has("values") && functions.isValueNull(detailsJson.getString("values")))
                    {
                        JSONObject valuesJson = new JSONObject(detailsJson.getString("values"));
                        if (valuesJson.has("rfOperationDateTime") && functions.isValueNull(valuesJson.getString("rfOperationDateTime")))
                        {
                            rfOperationDateTime = valuesJson.getString("rfOperationDateTime");
                        }
                    }
                }

                if (functions.isValueNull(respPaymentMethod))
                {
                    JSONObject paymentMethodinfo = new JSONObject(respPaymentMethod);
                    if (paymentMethodinfo.has("operation") && functions.isValueNull(paymentMethodinfo.getString("operation")))
                    {
                        respOperation = paymentMethodinfo.getString("operation");
                    }
                }


                if(functions.isValueNull(splitTrackingId))
                {
                    TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(splitTrackingId);
                    if (transactionDetailsVO != null)
                    {
                        if (functions.isValueNull(transactionDetailsVO.getCcnum()))
                        {
                            ccnum = PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
                        }
                        trackingId = transactionDetailsVO.getTrackingid();
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

                        firstName = transactionDetailsVO.getFirstName();
                        lastName = transactionDetailsVO.getLastName();
                        name = transactionDetailsVO.getFirstName() + " " + transactionDetailsVO.getLastName();
//                        requestIp = Functions.getIpAddress(req);
                        custId = transactionDetailsVO.getCustomerId();
                        transactionId = transactionDetailsVO.getPaymentId();
                        transactionlogger.error("trackingId-> " + trackingId + " transactionId ----->" + transactionDetailsVO.getPaymentId());
                        dbStatus = transactionDetailsVO.getStatus();
                        transactionlogger.error("dbStatus--> " + trackingId + " " + dbStatus);

                        merchantDetailsVO = merchantDAO.getMemberDetails(toid);
                        clkey = merchantDetailsVO.getKey();
                        checksumAlgo = merchantDetailsVO.getChecksumAlgo();
                        autoredirect = merchantDetailsVO.getAutoRedirect();
                        isPowerBy = merchantDetailsVO.getIsPoweredBy();
                        logoName = merchantDetailsVO.getLogoName();
                        isService = merchantDetailsVO.getIsService();
                        partnerName = merchantDetailsVO.getPartnerName();
                        email = transactionDetailsVO.getEmailaddr();

                        auditTrailVO.setActionExecutorName("AcquirerBackEnd");
                        auditTrailVO.setActionExecutorId(toid);
                        BlacklistManager blacklistManager = new BlacklistManager();
                        BlacklistVO blacklistVO = new BlacklistVO();

                        displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

                        if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus))
                        {
                            transactionlogger.error("accountid backend--->" + accountId);
                            commTransactionDetailsVO.setPreviousTransactionId(payFrexTransactionId);

                            requestVO.setTransDetailsVO(commTransactionDetailsVO);
                            requestVO.setAddressDetailsVO(commAddressDetailsVO);
                            transactionlogger.error("inside AUTH_STARTED--- " + trackingId + " " + requestVO.getTransDetailsVO().getPreviousTransactionId());
                            con = Database.getConnection();
                            StringBuffer dbBuffer = new StringBuffer();
                            transactionlogger.error("response operation ----- " + respOperation);
                            transactionlogger.error("response Amount ----- " + respAmount);
                            transactionlogger.error("DB Amount ----- " + respAmount);

                            if (functions.isValueNull(respAmount))
                            {
                                Double compRsAmount = Double.valueOf(respAmount);
                                Double compDbAmount = Double.valueOf(amount);
                                transactionlogger.error("Inside if response amount --->" + compRsAmount);
                                transactionlogger.error(" DB Amount--->" + compDbAmount);
                                if (compDbAmount.equals(compRsAmount))
                                {
                                    transactionStatus = respStatus;
                                    remark = respMessage;
                                    amount = respAmount;
                                }
                                else if (!compDbAmount.equals(compRsAmount) && "success".equalsIgnoreCase(respStatus))
                                {
                                    transactionStatus = "Failed";
                                    remark = "Failed-IRA";
                                    amount = respAmount;
                                    transactionlogger.error(trackingId + " EPBackend inside else Amount incorrect--->" + respAmount);

                                    blacklistVO.setVpaAddress(custId);
                                    blacklistVO.setIpAddress(requestIp);
                                    blacklistVO.setEmailAddress(email);
                                    blacklistVO.setActionExecutorId(toid);
                                    blacklistVO.setActionExecutorName("EPBackEnd");
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
                            commResponseVO.setErrorCode(code);
                            commResponseVO.setResponseTime(rfOperationDateTime);
                            commResponseVO.setTransactionId(payFrexTransactionId);
                            commResponseVO.setBankDescription(respMessage);
                            commResponseVO.setBankCode(code);

                            transactionlogger.error("inside EPBackendServlet transactionStatus " + transactionStatus);

                            if ("Success".equalsIgnoreCase(transactionStatus) && "payin".equalsIgnoreCase(respOperation))
                            {
                                transactionlogger.error("inside success EPBackendServlet transactionStatus " + transactionStatus);
                                notificationUrl = transactionDetailsVO.getNotificationUrl();
                                status = "success";
                                billingDesc = displayName;
                                message = remark;
                                commResponseVO.setDescriptor(billingDesc);

                                dbStatus = "capturesuccess";

                                commResponseVO.setStatus(dbStatus);

                                dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + payFrexTransactionId + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency + "',rrn='" + RRN + "" + "',status='capturesuccess'" + " ,successtimestamp='" + functions.getTimestamp());
                                if(functions.isValueNull(subscriptionPlan))
                                {
                                    dbBuffer.append("' ,boiledname='"+subscriptionPlan);
                                }
                                dbBuffer.append("',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' ,remark='" + remark + "' where trackingid = " + splitTrackingId);
                                transactionlogger.error("Update Query---" + dbBuffer);

                                Database.executeUpdate(dbBuffer.toString(), con);
                                entry.actionEntryForCommon(splitTrackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, requestIp);//+ ",rrn='" + rrn
                            }
                            else if ("pending".equalsIgnoreCase(transactionStatus) && "auth".equalsIgnoreCase(respOperation))
                            {
                                transactionlogger.error("inside pending EPBackendServlet transactionStatus " + transactionStatus);
                                notificationUrl = transactionDetailsVO.getNotificationUrl();
                                status = "success";
                                billingDesc = displayName;
                                message = remark;
                                commResponseVO.setDescriptor(billingDesc);

                                dbStatus = "authsuccessful";
                                commResponseVO.setStatus(dbStatus);

                                dbBuffer.append("update transaction_common set paymentid='" + payFrexTransactionId + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency + "',rrn='" + RRN + "" + "',status='authsuccessful'" + " ,successtimestamp='" + functions.getTimestamp());
                                if(functions.isValueNull(subscriptionPlan))
                                {
                                    dbBuffer.append("' ,boiledname='"+subscriptionPlan);
                                }
                                dbBuffer.append("',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' ,remark='" + remark + "' where trackingid = " + splitTrackingId);
                                transactionlogger.error("Update Query---" + dbBuffer);

                                Database.executeUpdate(dbBuffer.toString(), con);
                                entry.actionEntryForCommon(splitTrackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, auditTrailVO, requestIp);//+ ",rrn='" + rrn

                            }
                            else if ("Failed".equalsIgnoreCase(transactionStatus) || "declined".equalsIgnoreCase(transactionStatus) || "ERROR".equalsIgnoreCase(transactionStatus) || "FAIL".equalsIgnoreCase(transactionStatus) || "ERROR3DS".equalsIgnoreCase(transactionStatus))
                            {
                                transactionlogger.error("inside fail or error EPBackendServlet transactionStatus " + transactionStatus);
                                notificationUrl = transactionDetailsVO.getNotificationUrl();
                                status = "failed";
                                message = remark;

                                dbStatus = "authfailed";
                                dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + payFrexTransactionId + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency + "'" + " ,failuretimestamp='" + functions.getTimestamp());
                                if(functions.isValueNull(subscriptionPlan))
                                {
                                    dbBuffer.append("' ,boiledname='"+subscriptionPlan);
                                }
                                dbBuffer.append("',authorization_code='" + code + "',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' ,remark='" + remark + "' where trackingid = " + splitTrackingId);
                                transactionlogger.error("Update Query---" + dbBuffer);
                                Database.executeUpdate(dbBuffer.toString(), con);
                                entry.actionEntryForCommon(splitTrackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, requestIp);
                            }
                            else
                            {
                                transactionlogger.error("inside else pending EPBackendServlet transactionStatus " + transactionStatus);
                                //pending
                                dbStatus = "authstarted";
                                status = "Pending";
                                message = "Pending";
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
                            }
                            else if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
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
                            else if ((PZTransactionStatus.MARKED_FOR_REVERSAL.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.REVERSED.toString().equalsIgnoreCase(dbStatus)) && "success".equalsIgnoreCase(respStatus))
                            {
                                billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                                status = "success";
                                if (functions.isValueNull(respMessage))
                                    message = respMessage;
                                else if (functions.isValueNull(transactionDetailsVO.getRemark()))
                                    message = transactionDetailsVO.getRemark();
                                else
                                    message = "Refund Successful";
                                dbStatus = PZTransactionStatus.REVERSED.toString();
                            }
                            else
                            {
                                status = "pending";
                                message = "Transaction pending";
                                dbStatus = PZTransactionStatus.AUTH_STARTED.toString();
                            }
                        }

                        statusSyncDAO.updateAllTransactionFlowFlag(splitTrackingId, dbStatus);

                        /*AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                        asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(splitTrackingId), status, remark, billingDesc);
                        */
//                        AsynchronousSmsService smsService = new AsynchronousSmsService();
//                        smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(splitTrackingId), status, remark, billingDesc);

                        commonValidatorVO.setStatus(dbStatus);
                        commonValidatorVO.setTrackingid(splitTrackingId);
                        commonValidatorVO.setTerminalId(terminalId);
                        commonValidatorVO.setReason(respMessage);
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
                        commonValidatorVO.setReason(remark);
                        commonValidatorVO.setBankCode(code);
                        commonValidatorVO.setBankDescription(respMessage);

                        if (functions.isValueNull(notificationUrl))
                        {
                            transactionlogger.error("inside sending notification---" + notificationUrl);
                            TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                            transactionDetailsVO1.setBillingDesc(billingDesc);
                            transactionDetailsVO1.setBankReferenceId(payFrexTransactionId);
                            AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                            asyncNotificationService.sendNotification(transactionDetailsVO1, splitTrackingId, dbStatus, message, "");
                            transactionlogger.error("remark message----------------------" + message);
                        }

                        JSONObject jsonResObject = new JSONObject();
                        jsonResObject.put("responseCode", responseCode);
                        jsonResObject.put("responseStatus", returnResStatus);
                        transactionlogger.error("finalResponseSend--------> " + splitTrackingId + " " + jsonResObject.toString());
                        res.setContentType("application/json");
                        res.setStatus(200);
                        pWriter.println(jsonResObject.toString());
                        pWriter.flush();
                        return;

                    }
                }
            }
        }

        catch (NullPointerException e)
        {
            transactionlogger.error("NullPointerException:::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("EPBackendServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
        }

        catch (Exception e)
        {
            transactionlogger.error("PZDBViolationException:::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("EPBackendServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
}
