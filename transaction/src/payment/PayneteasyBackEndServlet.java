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
import com.payment.payneteasy.PayneteasyUtils;
import com.payment.statussync.StatusSyncDAO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.MySQLCodec;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Created by Krishna on 15-May-21.
 */
public class PayneteasyBackEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger  = new TransactionLogger(PayneteasyBackEndServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        transactionLogger.error("----Inside PayneteasyBackEndServlet----");
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

        try
        {
            if(functions.isValueNull(request.getParameter("trackingId")))
            {
                trackingId = request.getParameter("trackingId");
            }

            if(functions.isValueNull(request.getParameter("status")))
            {
                status = request.getParameter("status");
            }

            if(functions.isValueNull(request.getParameter("amount")))
            {
                respAmount = request.getParameter("amount");
            }

            if(functions.isValueNull(request.getParameter("currency")))
            {
                currency = request.getParameter("currency");
            }

            if(functions.isValueNull(request.getParameter("control")))
            {
                control = request.getParameter("control");
            }

            if(functions.isValueNull(request.getParameter("orderid")))
            {
                transactionId = request.getParameter("orderid");
            }

            if(functions.isValueNull(request.getParameter("type")))
            {
                transType = request.getParameter("type");
            }

            if(functions.isValueNull(request.getParameter("approval-code")))
            {
                authcode = request.getParameter("approval-code");
            }

            if(functions.isValueNull(request.getParameter("processor-rrn")))
            {
                rrn = request.getParameter("processor-rrn");
            }

            if(functions.isValueNull(request.getParameter("descriptor")))
            {
                descriptor = request.getParameter("descriptor");
            }
            if(functions.isValueNull(request.getParameter("error_message")))
            {
                message = request.getParameter("error_message").trim();
            }
            if(functions.isValueNull(request.getParameter("error_code")))
            {
                errorCode = request.getParameter("error_code");
            }

            transactionLogger.error("trackingId ===== " + trackingId);
            transactionLogger.error("status ===== " + status);
            transactionLogger.error("respAmount ===== " + respAmount);
            transactionLogger.error("currency ===== " + currency);
            transactionLogger.error("control ===== " + control);
            transactionLogger.error("transactionId ===== " + transactionId);
            transactionLogger.error("transType ===== " + transType);
            transactionLogger.error("authcode ===== " + authcode);
            transactionLogger.error("rrn ===== " + rrn);
            transactionLogger.error("descriptor ===== " + descriptor);

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

                    if ("approved".equalsIgnoreCase(status) && "sale".equalsIgnoreCase(transType))
                    {
                        notificationUrl = transactionDetailsVO.getNotificationUrl();

                        billingDesc     = gatewayAccount.getDisplayName();
                        message         = "Transaction Successful";
                        dbStatus        = PZTransactionStatus.CAPTURE_SUCCESS.toString();

                        transactionLogger.error("billingDesc ------ " + billingDesc);
                        transactionLogger.error("dbStatus ------ " + dbStatus);

                        commResponseVO.setDescription(message);
                        commResponseVO.setStatus("success");
                        commResponseVO.setRemark(message);

                        if(functions.isValueNull(descriptor) && "Y".equalsIgnoreCase(isDynamicDescriptor))
                        {
                            commResponseVO.setDescriptor(descriptor);
                        }
                        else
                        {
                            commResponseVO.setDescriptor(billingDesc);
                        }

                        dbBuffer.append("update transaction_common set captureamount='" + respAmount + "' ,rrn='" + rrn + "' ,authorization_code='" + authcode  + "' ,paymentid='" + transactionId + "' ,status='capturesuccess' ,remark='" + ESAPI.encoder().encodeForSQL(me, message) + "' ,successtimestamp='" + functions.getTimestamp() + "' where trackingid = " + trackingId);
                        transactionLogger.error("dbBuffer payneteasy ->" + dbBuffer);

                        Database.executeUpdate(dbBuffer.toString(), con);

                        entry.actionEntryForCommon(trackingId, respAmount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);

                        updatedStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                    }


                    else if("processing".equalsIgnoreCase(status))
                    {

                    }
                    else
                    {
                        if(functions.isValueNull(message) && message.startsWith("[") && message.endsWith("]"))
                        {
                            String code=message.substring(1,message.length()-1);
                            String errorMessage= PayneteasyUtils.getErrorCodeHash(code);
                            if(functions.isValueNull(errorMessage))
                                message=errorMessage;
                        }
                        if(!functions.isValueNull(message))
                            message = "Transaction Failed";
                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                        commResponseVO.setDescription(message);
                        commResponseVO.setStatus("failed");
                        commResponseVO.setRemark(message);
                        commResponseVO.setErrorCode(errorCode);

                        dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "' ,remark='" + ESAPI.encoder().encodeForSQL(me, message) + "' ,failuretimestamp='" + functions.getTimestamp() + "' where trackingid = " + trackingId);
                        transactionLogger.error("dbBuffer->" + dbBuffer);
                        Database.executeUpdate(dbBuffer.toString(), con);

                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);

                        updatedStatus = PZTransactionStatus.AUTH_FAILED.toString();
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
        finally
        {
            Database.closeConnection(con);
        }
    }
}
