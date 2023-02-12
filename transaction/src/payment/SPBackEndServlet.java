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
public class SPBackEndServlet extends HttpServlet
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
        transactionlogger.error("Entering  SPBackendServlet......");

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
        String responseStatus  = "";
        String isPowerBy    = "";
        String logoName     = "";
        String partnerName  = "";
        String amount = "";
        String trackingId = "";
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
        String responseRemark = "";
        String created_time = "";
        String captureAmount = "";
        String respMessage = "";
        String respAuthorize_status = "";
        PrintWriter pWriter = res.getWriter();
        String responseCode = "200";
        String returnResStatus = "OK";

        try
        {

            if (functions.isValueNull(req.getParameter("merchant_reference")))
            {
                trackingId  =   req.getParameter("merchant_reference");
            }

            transactionlogger.error("----- SPBackend  response-----" + trackingId);
            Enumeration enumeration         = req.getParameterNames();
            while (enumeration.hasMoreElements())
            {
                String key      = (String) enumeration.nextElement();
                String value    = req.getParameter(key);
                transactionlogger.error(trackingId + " ---Key---" + key + "---Value---" + value);
            }


            if(functions.isValueNull(req.getParameter("callpay_transaction_id")))
            {
                transactionId=req.getParameter("callpay_transaction_id");
            }
            if(functions.isValueNull(req.getParameter("amount")))
            {
                respAmount=req.getParameter("amount");
            }
            if(functions.isValueNull(req.getParameter("status")))
            {
                status=req.getParameter("status");
            }
            if (functions.isValueNull(req.getParameter("created")))
            {
                created_time = req.getParameter("created");
            }
            if (functions.isValueNull(req.getParameter("reason")) && !status.equalsIgnoreCase("complete"))
            {
                responseRemark=req.getParameter("reason");
                transactionlogger.error("Remark in failed condition ------> "+responseRemark);
            }

            if (functions.isValueNull(trackingId))
            {
                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                if (transactionDetailsVO != null)
                {
//                    if("Facilero".equalsIgnoreCase(transactionDetailsVO.getTotype())){
//                        facileroLogger.error("Swiffy Backend Response  ----> "+trackingId+" "+responseBackend.toString());
//                    }
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

                    auditTrailVO.setActionExecutorName("SPBackEnd");
                    auditTrailVO.setActionExecutorId(toid);
                    BlacklistManager blacklistManager   = new BlacklistManager();
                    BlacklistVO blacklistVO             = new BlacklistVO();

                    displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    Thread.sleep(5000);
                    transactionlogger.error("dbStatus " +trackingId+" "+ dbStatus);
                    if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus) ||( PZTransactionStatus.AUTH_FAILED.toString().equalsIgnoreCase(dbStatus)&& !"PLATFORM_ORDER_BILLING_ERROR".equalsIgnoreCase(respMessage)))
                    {
                        transactionlogger.error("accountid backend--->" + accountId);
//                        requestVO.setTransDetailsVO(commTransactionDetailsVO);
//                        requestVO.setAddressDetailsVO(commAddressDetailsVO);
                        transactionlogger.error("inside AUTH_STARTED--- " + trackingId + " " + requestVO.getTransDetailsVO().getPreviousTransactionId());
                        con                     = Database.getConnection();
                        StringBuffer dbBuffer   = new StringBuffer();
                        transactionlogger.error("resp Amount ----- " + respAmount);
                        transactionStatus = status;

                        commResponseVO.setTmpl_Amount(tmpl_amt);
                        commResponseVO.setTmpl_Currency(tmpl_currency);
                        commResponseVO.setIpaddress(requestIp);
                        commResponseVO.setDescription(responseRemark);
                        commResponseVO.setRemark(responseRemark);
                        commResponseVO.setAmount(respAmount);
                        commResponseVO.setCurrency(currency);

                        transactionlogger.error("inside SPBackendServlet transactionStatus " + transactionStatus);

                        if ("complete".equalsIgnoreCase(transactionStatus))   //status = 2: - Payment successful
                        {
                            transactionlogger.error("inside success SPBackendServlet transactionStatus " + transactionStatus);
                            notificationUrl = transactionDetailsVO.getNotificationUrl();
                            status = "success";
                            billingDesc = displayName;
                            message = "Payment successful";

                            commResponseVO.setDescriptor(billingDesc);

                            dbStatus = "capturesuccess";

                            commResponseVO.setStatus(dbStatus);
                            String params = "";


                            if (functions.isValueNull(responseRemark) && !responseRemark.equalsIgnoreCase("n/a"))
                                params = ",remark='" + ESAPI.encoder().encodeForSQL(me, message)+ "' ";

                            dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency + "',rrn='" + RRN + "" + "',status='capturesuccess'" + " ,successtimestamp='" + functions.getTimestamp());
                            dbBuffer.append("',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "'"  + params + " where trackingid = " + trackingId);
                            transactionlogger.error("Update Query---" + dbBuffer);

                            Database.executeUpdate(dbBuffer.toString(), con);
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, requestIp);//+ ",rrn='" + rrn
                        }
                        else if ("failed".equalsIgnoreCase(transactionStatus))
                        {
                            transactionlogger.error("inside fail or error SPBackendServlet transactionStatus " + transactionStatus);
                            notificationUrl = transactionDetailsVO.getNotificationUrl();
                            status          = "failed";
                            if (functions.isValueNull(responseRemark))
                            {
                                message = responseRemark;
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
                            transactionlogger.error("inside else pending SPBackendServlet transactionStatus " + transactionStatus);
                            //pending
                            dbStatus = "authstarted";
                            status = "Pending";
                            message = "Pending";
                        }
                    }
                    //REFUND
                    else if (PZTransactionStatus.PAYOUT_STARTED.toString().equals(dbStatus) || PZTransactionStatus.PAYOUT_FAILED.toString().equalsIgnoreCase(dbStatus))
                    {
                        transactionlogger.error("Inside payout condition ========================>");
                        StringBuffer dbBuffer = new StringBuffer();
                        transactionStatus = status;
                        if ("complete".equalsIgnoreCase(transactionStatus))
                        {
                            transactionlogger.error("Inside complete condition ========================>");
                            notificationUrl = transactionDetailsVO.getNotificationUrl();

                            responseStatus = "payoutsuccessful";
                            remark = transactionStatus;
//                            updatedStatus = PZTransactionStatus.PAYOUT_SUCCESS.toString();
                            StringBuffer sb = new StringBuffer();

                            transactionDetailsVO.setBillingDesc(displayName);
                            commResponseVO.setDescriptor(displayName);
                            commResponseVO.setStatus("success");
                            commResponseVO.setRemark(remark);
                            commResponseVO.setDescription("payout Successful");
                            commResponseVO.setCurrency(currency);
                            commResponseVO.setTmpl_Amount(tmpl_amt);
                            commResponseVO.setTmpl_Currency(transactionDetailsVO.getTemplatecurrency());
                            commResponseVO.setTransactionId(transactionId);
                            commResponseVO.setResponseHashInfo(transactionId);

                            sb.append("update transaction_common set ");
                            sb.append(" payoutamount='" + respAmount + "'");
                            sb.append(", status='payoutsuccessful'");
                            sb.append(" ,remark='" + remark + "' ,paymentid='" + transactionId + "',payouttimestamp='" + functions.getTimestamp() + "' where trackingid =" + trackingId + "");
                            transactionlogger.error("payoutquery " + trackingId + " " + sb.toString());
                            con = Database.getConnection();
                            int result = Database.executeUpdate(sb.toString(), con);
                            transactionlogger.error("payoutquery " + trackingId + " " + result);

                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_PAYOUT_SUCCESSFUL, ActionEntry.STATUS_PAYOUT_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.PAYOUT_SUCCESS.toString());

                            if (result != 1)
                            {
                                Database.rollback(con);
                            }

                        }
                        else if ("failed".equalsIgnoreCase(transactionStatus))
                        {

                            transactionlogger.error("Inside Failed condition =====================>");
                            transactionStatus = status;

                            responseStatus = "payoutfailed";
                            remark = transactionStatus;
                            //updatedStatus = PZTransactionStatus.PAYOUT_FAILED.toString();
                            StringBuffer sb = new StringBuffer();

                            notificationUrl = transactionDetailsVO.getNotificationUrl();

                            transactionDetailsVO.setBillingDesc(displayName);
                            commResponseVO.setDescriptor(displayName);
                            commResponseVO.setStatus("failed");
                            commResponseVO.setRemark(remark);
                            commResponseVO.setDescription("Payout failed");
                            commResponseVO.setCurrency(currency);
                            commResponseVO.setTmpl_Amount(tmpl_amt);
                            commResponseVO.setTmpl_Currency(transactionDetailsVO.getTemplatecurrency());

                            commResponseVO.setTransactionId(transactionId);
                            commResponseVO.setResponseHashInfo(transactionId);

                            sb.append("update transaction_common set ");
                            // sb.append(" payoutamount='" + OrderAmount + "'");
                            sb.append(" status='payoutfailed'");
                            sb.append(" ,remark='" + remark + "' ,paymentid='" + transactionId + "',payouttimestamp='" + functions.getTimestamp() + "' where trackingid =" + trackingId + "");
                            transactionlogger.error("payoutquery " + trackingId + " " + sb.toString());
                            con = Database.getConnection();
                            int result = Database.executeUpdate(sb.toString(), con);
                            transactionlogger.error("payoutquery " + trackingId + " " + result);

                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_PAYOUT_FAILED, ActionEntry.STATUS_PAYOUT_FAILED, commResponseVO, auditTrailVO, null);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.PAYOUT_FAILED.toString());

                            if (result != 1)
                            {
                                Database.rollback(con);
                            }

                        }
                        else
                        {
                            responseStatus = PZResponseStatus.PENDING.toString();
                            //updatedStatus = "pending";
                            remark = status;

                        }

                    }
                    else
                    {
                        transactionlogger.error("Inside else condition ------------->");
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
            PZExceptionHandler.raiseAndHandleDBViolationException("SPBackendServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
        }

        catch (Exception e)
        {
            transactionlogger.error("PZDBViolationException:::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("SPBackendServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
}