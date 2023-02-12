package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.statussync.StatusSyncDAO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.MySQLCodec;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

/**
 * Created by Krishna on 15-May-21.
 */
public class MTBackEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger  = new TransactionLogger(MTBackEndServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        transactionLogger.error("----Inside MTBackEndServlet----");

        Functions functions                     = new Functions();
        AuditTrailVO auditTrailVO               = new AuditTrailVO();
        TransactionManager transactionManager   = new TransactionManager();
        MerchantDAO merchantDAO                 = new MerchantDAO();
        MerchantDetailsVO merchantDetailsVO     = new MerchantDetailsVO();
        CommResponseVO commResponseVO           = new CommResponseVO();
        ActionEntry entry                       = new ActionEntry();
        MySQLCodec me                           = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StatusSyncDAO statusSyncDAO             = new StatusSyncDAO();

        for (Object key : request.getParameterMap().keySet())
        {
            transactionLogger.debug("key= " + key + " value= " + request.getParameter((String) key) + "----------");
        }
        String trackingId   = "";
        Connection con      = null;
        merchantDetailsVO   = new MerchantDetailsVO ();
        trackingId                  = "";
        String toId                 = "";
        String accountId            = "";
        String dbControl            = "";
        String fromType             = "";
        String paymentId            = "";
        String respAmount           = "";
        String amount               = "";
        String currency             = "";
        String errorCode            = "";
        String description          = "";
        String descriptor           = "";
        String transactionStatus    = "";
        String tmpl_currency        = "";
        String tmpl_amt             = "";
        String timestamp            = "";
        String eci                  = "";
        String transactionId        = "";
        String isService            = "";
        String transType            = "";
        String status               = "";
        String message              = "";
        String dbStatus             = "";
        String errorName            = "";
        String confirmStatus        = "";
        String billingDesc          = "";
        String merchantTransactionId = "";
        String notificationUrl       = "";
        String updatedStatus         = "";
        String cardType              = "";
        String cardTypeName          = "";
        String bankTransactionDate   = "";
        String errorcode                = "";
        String authcode                 = "";
        PrintWriter pWriter         = response.getWriter();
        String responseCode         = "200";
        String responseStatus       = "OK";
        String isDynamicDescriptor  = "N";
        String control  = "";
        String rrn  = "";
        String id  = "";
        String amMessage  = "";
        String statusCode  = "";
        String airtelMoneyId  = "";

        try
        {
            StringBuilder responseMsg=new StringBuilder();
            BufferedReader br = request.getReader();
            String str;
            while ((str = br.readLine()) != null)
            {
                responseMsg.append(str);
            }

            if(functions.isValueNull(responseMsg.toString()) && functions.isJSONValid(responseMsg.toString()))
            {
                JSONObject jsonObject = new JSONObject(responseMsg.toString());

                if(jsonObject.has("transaction"))
                {
                    JSONObject transactionObject = jsonObject.getJSONObject("transaction");

                    if(transactionObject.has("id") && functions.isValueNull(transactionObject.getString("id")))
                        trackingId = transactionObject.getString("id");

                    if(transactionObject.has("message") && functions.isValueNull(transactionObject.getString("message")))
                        amMessage = transactionObject.getString("message");

                    if(transactionObject.has("status_code") && functions.isValueNull(transactionObject.getString("status_code")))
                        statusCode = transactionObject.getString("status_code");

                    if(transactionObject.has("airtel_money_id") && functions.isValueNull(transactionObject.getString("airtel_money_id")))
                        airtelMoneyId = transactionObject.getString("airtel_money_id");

                }
            }

            transactionLogger.error("responseMsg ===== " + responseMsg);
            transactionLogger.error("trackingId ===== " + trackingId);
            transactionLogger.error("amMessage ===== " + amMessage);
            transactionLogger.error("statusCode ===== " + statusCode);
            transactionLogger.error("airtelMoneyId ===== " + airtelMoneyId);

            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);

            if (transactionDetailsVO != null)
            {
                accountId       = transactionDetailsVO.getAccountId();
                cardType        = transactionDetailsVO.getCardtype();
                toId            = transactionDetailsVO.getToid();
                dbStatus        = transactionDetailsVO.getStatus();
                amount          = transactionDetailsVO.getAmount();
                currency        = transactionDetailsVO.getCurrency();
                tmpl_amt        = transactionDetailsVO.getTemplateamount();
                tmpl_currency   = transactionDetailsVO.getTemplatecurrency();

                transactionLogger.error("accountId ===== " + accountId);
                transactionLogger.error("cardType ===== " + cardType);
                transactionLogger.error("toId ===== " + toId);
                transactionLogger.error("dbStatus ===== " + dbStatus);
                transactionLogger.error("amount ===== " + amount);
                transactionLogger.error("currency ===== " + currency);
                transactionLogger.error("tmpl_amt ===== " + tmpl_amt);
                transactionLogger.error("trackingId ===== " + trackingId);
                transactionLogger.error("tmpl_currency ===== " + tmpl_currency);

                if (functions.isValueNull(transactionDetailsVO.getCcnum())){
                    transactionDetailsVO.setCcnum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
                }
                if (functions.isValueNull(transactionDetailsVO.getExpdate()))
                {
                    transactionDetailsVO.setExpdate(PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate()));
                }

                auditTrailVO.setActionExecutorName("AcquirerCommonBackEnd");
                auditTrailVO.setActionExecutorId(toId);

                merchantDetailsVO   = merchantDAO.getMemberDetails(toId);

                transactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());

                GatewayAccount gatewayAccount  = GatewayAccountService.getGatewayAccount(accountId);
                isDynamicDescriptor            = gatewayAccount.getIsDynamicDescriptor();

                transactionLogger.error("isDynamicDescriptor ------ " + isDynamicDescriptor);

                transactionLogger.error("DbStatus------" + dbStatus);
                if (PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus))
                {
                    con = Database.getConnection();

                    commResponseVO.setCurrency(currency);
                    commResponseVO.setTmpl_Amount(tmpl_amt);
                    commResponseVO.setTmpl_Currency(transactionDetailsVO.getCurrency());
                    commResponseVO.setTransactionId(transactionId);
                    commResponseVO.setEci(eci);
                    commResponseVO.setBankTransactionDate(bankTransactionDate);
                    commResponseVO.setErrorCode(errorcode);
                    transactionLogger.error("transactionreferenceSet ------ " + commResponseVO.getTransactionId());
                    StringBuffer dbBuffer = new StringBuffer();

                    if ("TS".equalsIgnoreCase(statusCode))
                    {
                        notificationUrl = transactionDetailsVO.getNotificationUrl();

                        billingDesc     = gatewayAccount.getDisplayName();
                        message         = "Transaction Successful";
                        dbStatus        = PZTransactionStatus.CAPTURE_SUCCESS.toString();

                        transactionLogger.error("billingDesc ------ " + billingDesc);
                        transactionLogger.error("dbStatus ------ " + dbStatus);

                        commResponseVO.setDescription(amMessage);
                        commResponseVO.setStatus("success");
                        commResponseVO.setRemark(amMessage);

                        if(functions.isValueNull(descriptor) && "Y".equalsIgnoreCase(isDynamicDescriptor))
                        {
                            commResponseVO.setDescriptor(descriptor);
                        }
                        else
                        {
                            commResponseVO.setDescriptor(billingDesc);
                        }

                        dbBuffer.append("update transaction_common set authorization_code='" +  statusCode  + "' ,paymentid='" + airtelMoneyId + "' ,status='capturesuccess' ,remark='" + ESAPI.encoder().encodeForSQL(me, message) + "' ,successtimestamp='" + functions.getTimestamp() + "' ,captureamount='" + amount + "' where trackingid = " + trackingId);
                        transactionLogger.error("dbBuffer payneteasy ->" + dbBuffer);

                        Database.executeUpdate(dbBuffer.toString(), con);

                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);

                        updatedStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                    }

                    else if("TF".equalsIgnoreCase(statusCode))
                    {
                        dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + airtelMoneyId + "',remark='" + ESAPI.encoder().encodeForSQL(me, amMessage) + "' where trackingid = " + trackingId);
                        transactionLogger.error("-----dbBuffer-----" + dbBuffer);
                        con = Database.getConnection();
                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authfailed");
                        updatedStatus = "authfailed";
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                    }
                    if (functions.isValueNull(updatedStatus) && functions.isValueNull(notificationUrl))
                    {
                        transactionLogger.error("Inside sending notification---" + notificationUrl);
                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                        transactionDetailsVO.setBillingDesc(billingDesc);
                        if(functions.isValueNull(eci))
                            transactionDetailsVO.setEci(eci);
                        asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, updatedStatus, message, "");
                    }
                }

                org.json.JSONObject jsonResObject = new org.json.JSONObject();
                jsonResObject.put("responseCode", responseCode);
                jsonResObject.put("responseStatus", responseStatus);
                response.setContentType("application/json");
                response.setStatus(200);
                pWriter.println(jsonResObject.toString());
                pWriter.flush();

                return;
            }
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException --"+trackingId+"-->", e);
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("---SecureTradingBackEndServlet-----SystemError --" + trackingId + "-->", systemError);
        }
        catch (org.json.JSONException e)
        {
            transactionLogger.error("---SecureTradingBackEndServlet-----JSONException --" + trackingId + "-->", e);
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException =====", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
}
