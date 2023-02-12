package payment;

import com.directi.pg.*;

import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.PZTransactionStatus;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.flexepin.FlexepinPaymentGateway;
import com.payment.flexepin.FlexepinUtils;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONObject;
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
 * Created by Admin on 2022-07-13.
 */
public class FPBackendServlet extends HttpServlet
{

    private static TransactionLogger transactionlogger = new TransactionLogger(FPBackendServlet.class.getName());
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
        transactionlogger.error("Entering  FPBackendServlet......");

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
        JSONObject jsonResObject                            = new JSONObject();
        Connection con          = null;
        PreparedStatement p     = null;
        ResultSet rs            = null;
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
        String tmpl_amt         = "";
        String tmpl_currency    = "";
        String custId           = "";
        String message          = "";
        String billingDesc      = "";
        String dbStatus         = "";
        String notificationUrl  = "";
        String requestIp        = "";
        String paymentId        = "";
        String RRN              = "";
        String terminalId       = "";
        String respStatus       = "";
        String trans_no         = "";
        String Transaction_id   = "";
        String qty              = "";
        String valueAmount      = "";
        PrintWriter pWriter     = res.getWriter();
        String responseCode     = "200";
        String returnResStatus  = "OK";
        JSONObject responseJson = null;

        try
        {
            transactionlogger.error("----- FPBackendServlet  response-----" + trackingId);

            Enumeration requestHeaders =  req.getHeaderNames();
            transactionlogger.error(trackingId+"====> callback Headers[]:");
            while (requestHeaders.hasMoreElements())
            {
                String key = (String) requestHeaders.nextElement();
                String value = req.getHeader(key);
                transactionlogger.error(trackingId + " ---Key---" + key + "---Value---" + value);
            }

            transactionlogger.error(trackingId+"====> response parameters");
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
            transactionlogger.error(trackingId+"----- FPBackendServlet  response-----" + responseMsg);


            if (FlexepinUtils.isJSONValid(String.valueOf(responseMsg)))
            {
                responseJson = new JSONObject(String.valueOf(responseMsg));

                if (responseJson.has("trans_no") && functions.isValueNull(responseJson.getString("trans_no")))
                    trans_no = responseJson.getString("trans_no");

                if (responseJson.has("transaction_id") && functions.isValueNull(responseJson.getString("transaction_id")))
                    Transaction_id = responseJson.getString("transaction_id");

                if (responseJson.has("qty") && functions.isValueNull(responseJson.getString("qty")))
                    qty = responseJson.getString("qty");

                if (responseJson.has("value") && functions.isValueNull(responseJson.getString("value")))
                    valueAmount = responseJson.getString("value");
            }

            trackingId = Transaction_id;
            transactionlogger.error(trackingId+"----- FPBackendServlet  response-----" + responseMsg);

            if (functions.isValueNull(trackingId))
            {
                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);

                if (transactionDetailsVO != null)
                {
                    toid            = transactionDetailsVO.getToid();
                    description     = transactionDetailsVO.getDescription();
                    redirectUrl     = transactionDetailsVO.getRedirectURL();
                    accountId       = transactionDetailsVO.getAccountId();
                    orderDesc       = transactionDetailsVO.getOrderDescription();
                    currency        = transactionDetailsVO.getCurrency();
                    amount          = transactionDetailsVO.getAmount();
                    paymentId       = transactionDetailsVO.getPaymentId();
                    terminalId      = transactionDetailsVO.getTerminalId();

                    if (functions.isValueNull(transactionDetailsVO.getTemplateamount()))
                        tmpl_amt = transactionDetailsVO.getTemplateamount();
                    else
                        tmpl_amt = amount;

                    tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                    if (functions.isValueNull(tmpl_currency))
                        tmpl_currency = transactionDetailsVO.getTemplatecurrency();

                    requestIp           = Functions.getIpAddress(req);
                    custId              = transactionDetailsVO.getCustomerId();
                    dbStatus            = transactionDetailsVO.getStatus();
                    merchantDetailsVO   = merchantDAO.getMemberDetails(toid);
                    logoName            = merchantDetailsVO.getLogoName();
                    partnerName         = merchantDetailsVO.getPartnerName();
                    displayName         = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    auditTrailVO.setActionExecutorName("AcquirerBackEnd");
                    auditTrailVO.setActionExecutorId(toid);

                    if (functions.isValueNull(valueAmount))
                        amount = valueAmount;

                    if (functions.isValueNull(trans_no))
                        paymentId = trans_no;

                    transactionlogger.error("trackingId-> " + trackingId + " transactionId ----->" + paymentId);
                    transactionlogger.error("dbStatus--> " + trackingId + " " + dbStatus);
                    transactionlogger.error("db amount-->: "+ transactionDetailsVO.getAmount());
                    transactionlogger.error("resp amount-->: "+ valueAmount);


                    if (PZTransactionStatus.PAYOUT_STARTED.toString().equals(dbStatus))
                    {
                        transactionlogger.error("accountid backend--->" + accountId);
                        requestVO.setTransDetailsVO(commTransactionDetailsVO);
                        requestVO.setAddressDetailsVO(commAddressDetailsVO);
                        transactionlogger.error("inside PAYOUT_STARTED--- " + trackingId + " " + requestVO.getTransDetailsVO().getPreviousTransactionId());
                        con                                 = Database.getConnection();
                        StringBuffer dbBuffer               = new StringBuffer();
                        FlexepinPaymentGateway flexepinPaymentGateway = new FlexepinPaymentGateway(accountId);
                        transactionlogger.error("resp Amount ----- " + valueAmount);

                        commResponseVO.setTmpl_Amount(tmpl_amt);
                        commResponseVO.setTmpl_Currency(tmpl_currency);
                        commResponseVO.setIpaddress(requestIp);
                        commResponseVO.setDescription(remark);
                        commResponseVO.setRemark(remark);
                        commResponseVO.setAmount(amount);
                        commResponseVO.setCurrency(currency);

                        commResponseVO  = (CommResponseVO) flexepinPaymentGateway.processPayoutInquiry(trackingId, requestVO);
                        respStatus = commResponseVO.getStatus();

                        if (functions.isValueNull(commResponseVO.getDescription()))
                            description = commResponseVO.getDescription();

                        if (functions.isValueNull(commResponseVO.getCurrency()))
                            currency = commResponseVO.getCurrency();

                        if (functions.isValueNull(commResponseVO.getRemark()))
                            remark = commResponseVO.getRemark();
                        
                        transactionlogger.error("inside FPBackendServlet transactionStatus " + respStatus);
                        transactionlogger.error("inside FPBackendServlet description " + description);
                        transactionlogger.error("inside FPBackendServlet currency " + currency);
                        transactionlogger.error("inside FPBackendServlet remark " + remark);

                        if ("success".equalsIgnoreCase(respStatus))
                        {
                            transactionlogger.error("inside success FPBackendServlet transactionStatus " + respStatus);
                            notificationUrl = transactionDetailsVO.getNotificationUrl();
                            status      = "success";
                            billingDesc = displayName;

                            if (functions.isValueNull(remark))
                                message = remark;
                            else
                                message = "Payment successful";

                            commResponseVO.setDescriptor(billingDesc);
                            dbStatus = "payoutsuccessful";
                            commResponseVO.setStatus(dbStatus);

                            dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + paymentId + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency + "',rrn='" + RRN + "" + "',status='payoutsuccessful'" + " ,successtimestamp='" + functions.getTimestamp());
                            dbBuffer.append("',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' ,remark='" + ESAPI.encoder().encodeForSQL(me, message) + "' where trackingid = " + trackingId);
                            transactionlogger.error("Update Query---" + dbBuffer);

                            Database.executeUpdate(dbBuffer.toString(), con);
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_PAYOUT_SUCCESSFUL, ActionEntry.STATUS_PAYOUT_SUCCESSFUL, commResponseVO, auditTrailVO, requestIp);
                            jsonResObject.put("resultCode", 0);
                            jsonResObject.put("resultDescription", "Success");
                        }
                        else if ("failed".equalsIgnoreCase(respStatus))
                        {
                            transactionlogger.error("inside fail or error FPBackendServlet transactionStatus " + respStatus);
                            notificationUrl = transactionDetailsVO.getNotificationUrl();
                            status          = "failed";
                            if (functions.isValueNull(remark))
                                message = remark;
                            else
                                message = "Transaction rejected";

                            dbStatus = "payoutfailed";
                            dbBuffer.append("update transaction_common set status='payoutfailed',paymentid='" + paymentId + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency + "'" + " ,failuretimestamp='" + functions.getTimestamp());
                            dbBuffer.append("',authorization_code='" + "" + "',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' ,remark='" + ESAPI.encoder().encodeForSQL(me, message) + "' where trackingid = " + trackingId);

                            transactionlogger.error("Update Query---" + dbBuffer);

                            Database.executeUpdate(dbBuffer.toString(), con);
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_PAYOUT_FAILED, ActionEntry.STATUS_PAYOUT_FAILED, commResponseVO, auditTrailVO, requestIp);
                            jsonResObject.put("resultCode", 10218);
                            jsonResObject.put("resultDescription", "Validation failed");
                        }
                        else
                        {
                            transactionlogger.error("inside else pending FPBackendServlet transactionStatus " + respStatus);
                            //pending
                            dbStatus = PZTransactionStatus.PAYOUT_STARTED.toString();
                            status = "Pending";
                            message = "Pending";
                        }
                    }
                    else
                    {
                        if (PZTransactionStatus.PAYOUT_SUCCESS.toString().equals(dbStatus))
                        {
                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                            status = "success";
                            if (functions.isValueNull(transactionDetailsVO.getRemark()))
                                message = transactionDetailsVO.getRemark();
                            else
                                message = "Payout Successful";
                            dbStatus = PZTransactionStatus.PAYOUT_SUCCESS.toString();
                            jsonResObject.put("resultCode", 0);
                            jsonResObject.put("resultDescription", "Success");
                        }
                        else if (PZTransactionStatus.PAYOUT_FAILED.toString().equals(dbStatus))
                        {
                            status = "failed";
                            if (functions.isValueNull(transactionDetailsVO.getRemark()))
                                message = transactionDetailsVO.getRemark();
                            else if (!functions.isValueNull(message))
                                message = "Payout Failed";
                            dbStatus = PZTransactionStatus.PAYOUT_FAILED.toString();
                            jsonResObject.put("resultCode", 10218);
                            jsonResObject.put("resultDescription", "Validation failed");
                        }
                        else
                        {
                            status = "pending";
                            message = "Transaction pending";
                            dbStatus = PZTransactionStatus.PAYOUT_STARTED.toString();
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
                    addressDetailsVO.setCardHolderIpAddress(requestIp);
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    commonValidatorVO.setPaymentType(transactionDetailsVO.getPaymodeId());
                    commonValidatorVO.setCardType(transactionDetailsVO.getCardTypeId());
                    commonValidatorVO.setCustomerId(custId);
                    commonValidatorVO.setCustomerBankId(custId);

                    commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                    commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                    commonValidatorVO.setReason(message);

                    if (functions.isValueNull(notificationUrl))
                    {
                        transactionlogger.error(trackingId+ " inside sending notification---" + notificationUrl);
                        TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                        transactionDetailsVO1.setBillingDesc(billingDesc);
                        transactionDetailsVO1.setBankReferenceId(paymentId);
                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                        asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, dbStatus, message, "");
                    }

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
            PZExceptionHandler.raiseAndHandleDBViolationException("FPBackendServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
        }
        catch (Exception e)
        {
            transactionlogger.error("PZDBViolationException:::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("FPBackendServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }

}
