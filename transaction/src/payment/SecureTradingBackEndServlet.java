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
import org.codehaus.jettison.json.JSONArray;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Pramod on 4/12/2021
 */
public class SecureTradingBackEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger  = new TransactionLogger(SecureTradingBackEndServlet.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        transactionLogger.error("----Inside SecureTradingBackEndServlet----");
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
        String txnStatus                = "";
        String acquirerresponsemessage  ="";
        String errorcode                = "";
        String acquirertypedescription  = "";
        String acquirerresponsecode     = "";
        String acquireradvicecode       = "";
        String accounttypedescription   = "";
        String authcode                 = "";
        String enrolled                 = "";
        String notificationreference    = "";
        String paymenttypedescription   = "";
        String requestreference         = "";
        String livestatus               = "";
        String parenttransactionreference = "";
        String transactionstartedtimestamp = "";
        PrintWriter pWriter         = response.getWriter();
        String responseCode         = "200";
        String responseStatus       = "OK";
        String isDynamicDescriptor  = "N";
        String chargedescriptionRes  = "";
        try
        {

                if(request.getParameter("orderreference") != null){
                      trackingId                  = request.getParameter("orderreference");
                }
                if(request.getParameter("livestatus") != null){
                    livestatus                      = request.getParameter("livestatus");
                }
                if(request.getParameter("settlestatus") != null){
                    status                      = request.getParameter("settlestatus");
                }
                if(request.getParameter("mainamount") != null){
                    respAmount                  = request.getParameter("mainamount");
                }
                if(request.getParameter("parenttransactionreference") != null){
                    parenttransactionreference                   = request.getParameter("parenttransactionreference");
                }
               if(request.getParameter("transactionreference") != null){
                    transactionId                   = request.getParameter("transactionreference");
                }

               if(request.getParameter("acquirerresponsemessage") != null){
                   acquirerresponsemessage     = request.getParameter("acquirerresponsemessage");
               }

               if(request.getParameter("acquirertypedescription") != null){
                   acquirertypedescription     = request.getParameter("acquirertypedescription");
               }

               if(request.getParameter("acquirerresponsecode") != null){
                   acquirerresponsecode     = request.getParameter("acquirerresponsecode");
               }
               if(request.getParameter("eci") != null){
                   eci     = request.getParameter("eci");
               }
               if(request.getParameter("currencyiso3a") != null){
                   currency     = request.getParameter("currencyiso3a");
               }
               if(request.getParameter("acquireradvicecode") != null){
                  acquireradvicecode     = request.getParameter("acquireradvicecode");
               }
               if(request.getParameter("accounttypedescription") != null){
                  accounttypedescription     = request.getParameter("accounttypedescription");
               }
               if(request.getParameter("authcode") != null){
                   authcode     = request.getParameter("authcode");
               }
               if(request.getParameter("enrolled") != null){
                   enrolled     = request.getParameter("enrolled");
               }
               if(request.getParameter("errorcode") != null){
                   errorcode     = request.getParameter("errorcode");
               }
               if(request.getParameter("notificationreference") != null){
                  notificationreference     = request.getParameter("notificationreference");
               }
               if(request.getParameter("paymenttypedescription") != null){
                 paymenttypedescription     = request.getParameter("paymenttypedescription");
               }
               if(request.getParameter("requestreference") != null){
                   requestreference     = request.getParameter("requestreference");
               }
               if(request.getParameter("transactionstartedtimestamp") != null){
                   bankTransactionDate     = request.getParameter("transactionstartedtimestamp");
               }
               if(request.getParameter("chargedescription") != null){
                   chargedescriptionRes     = request.getParameter("chargedescription");
               }


                transactionLogger.debug("orderreference---> "+trackingId);
                transactionLogger.debug("transactionreference---> "+transactionId);
                transactionLogger.debug("acquirerresponsemessage---> "+acquirerresponsemessage);
                transactionLogger.debug("acquirertypedescription---> "+acquirertypedescription);
                transactionLogger.debug("currency--->"+currency);
                transactionLogger.debug("requestreference---> "+requestreference);
                transactionLogger.debug("enrolled---> "+enrolled);
                transactionLogger.debug("notificationreference---> "+notificationreference);
                transactionLogger.debug("errorcode---> "+errorcode);
                transactionLogger.debug("authcode---> "+authcode);
                transactionLogger.debug("accounttypedescription---> "+accounttypedescription);
                transactionLogger.debug("acquireradvicecode---> "+acquireradvicecode);
                transactionLogger.debug("paymenttypedescription---> "+paymenttypedescription);
                transactionLogger.debug("acquirerresponsecode---> "+acquirerresponsecode);
                transactionLogger.debug("livestatus---> "+livestatus);
                transactionLogger.debug("parenttransactionreference---> "+parenttransactionreference);
                transactionLogger.debug("transactionstartedtimestamp---> "+bankTransactionDate);
                transactionLogger.debug("chargedescriptionRes---> "+chargedescriptionRes);


                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                if (transactionDetailsVO != null)
                {
                    accountId       = transactionDetailsVO.getAccountId();
                    cardType        = transactionDetailsVO.getCardtype();
                    toId            = transactionDetailsVO.getToid();
                    dbStatus        = transactionDetailsVO.getStatus();
                    notificationUrl = transactionDetailsVO.getNotificationUrl();
                    amount          = transactionDetailsVO.getAmount();
                    currency        = transactionDetailsVO.getCurrency();
                    tmpl_amt        = transactionDetailsVO.getTemplateamount();
                    tmpl_currency   = transactionDetailsVO.getTemplatecurrency();

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

                    transactionLogger.error("DbStatus------" + dbStatus);
                    if (PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus) || (PZTransactionStatus.AUTH_FAILED.toString().equalsIgnoreCase(dbStatus)))
                    {
                        con = Database.getConnection();

                        commResponseVO.setCurrency(currency);
                        commResponseVO.setTmpl_Amount(tmpl_amt);
                        commResponseVO.setTmpl_Currency(transactionDetailsVO.getCurrency());
                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setEci(eci);
                        commResponseVO.setBankTransactionDate(bankTransactionDate);
                        commResponseVO.setErrorCode(errorcode);
                        transactionLogger.error("transactionreferenceSet------ " + commResponseVO.getTransactionId());
                        StringBuffer dbBuffer = new StringBuffer();

                        if (("0".equals(status) || "1".equals(status) || "100".equals(status) || "10".equals(status)) && "0".equals(errorcode))
                        {
                            billingDesc     =gatewayAccount.getDisplayName();
                            message         = "Transaction Successful";
                            dbStatus        = PZTransactionStatus.CAPTURE_SUCCESS.toString();

                            commResponseVO.setDescription(message + acquirerresponsemessage);
                            commResponseVO.setStatus("success");
                            commResponseVO.setRemark(acquirerresponsemessage);

                            if(functions.isValueNull(chargedescriptionRes) && "Y".equalsIgnoreCase(isDynamicDescriptor))
                            {
                                commResponseVO.setDescriptor(chargedescriptionRes);
                                billingDesc=chargedescriptionRes;
                            }else{
                                commResponseVO.setDescriptor(billingDesc);
                            }

                            dbBuffer.append("update transaction_common set captureamount='" + respAmount + "' ,paymentid='" + transactionId + "',status='capturesuccess' ,remark='" + ESAPI.encoder().encodeForSQL(me, message) + "',successtimestamp='" + functions.getTimestamp() + "' where trackingid = " + trackingId);
                            transactionLogger.error("dbBuffer->" + dbBuffer);

                            Database.executeUpdate(dbBuffer.toString(), con);

                            entry.actionEntryForCommon(trackingId, respAmount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);

                            updatedStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                        }else if("2".equals(status) &&  "0".equals(errorcode)){

                            billingDesc     = gatewayAccount.getDisplayName();
                            message         = "Transaction Successful";
                            dbStatus        = PZTransactionStatus.AUTH_SUCCESS.toString();

                            commResponseVO.setDescription(message + acquirerresponsemessage);
                            commResponseVO.setStatus("success");
                            commResponseVO.setRemark(acquirerresponsemessage);

                            if(functions.isValueNull(chargedescriptionRes) && "Y".equalsIgnoreCase(isDynamicDescriptor))
                            {
                                commResponseVO.setDescriptor(chargedescriptionRes);
                                billingDesc=chargedescriptionRes;
                            }else{
                                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                            }

                            dbBuffer.append("update transaction_common set paymentid='" + transactionId + "',status='authsuccessful' ,remark='" + ESAPI.encoder().encodeForSQL(me, message) + "',successtimestamp='" + functions.getTimestamp() + "' where trackingid = " + trackingId);
                            transactionLogger.error("dbBuffer->" + dbBuffer);

                            Database.executeUpdate(dbBuffer.toString(), con);

                            entry.actionEntryForCommon(trackingId, respAmount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, auditTrailVO, null);

                            updatedStatus = PZTransactionStatus.AUTH_SUCCESS.toString();
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                        }
                        else
                        {
                            if("3".equalsIgnoreCase(status)){
                                acquirerresponsemessage = "Failed by bank";
                            }

                            message = acquirerresponsemessage;

                            commResponseVO.setDescription(message);
                            commResponseVO.setStatus("failed");
                            commResponseVO.setRemark(acquirerresponsemessage);

                            dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "' ,remark='" + ESAPI.encoder().encodeForSQL(me, message)+ "',failuretimestamp='" + functions.getTimestamp() + "' where trackingid = " + trackingId);
                            transactionLogger.error("dbBuffer->" + dbBuffer);
                            Database.executeUpdate(dbBuffer.toString(), con);

                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);

                            updatedStatus = PZTransactionStatus.AUTH_FAILED.toString();
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                        }

                        if(!functions.isValueNull(notificationUrl) && functions.isValueNull(merchantDetailsVO.getNotificationUrl())){
                            notificationUrl = merchantDetailsVO.getNotificationUrl();
                        }

                        if (functions.isValueNull(updatedStatus) && functions.isValueNull(notificationUrl))
                        {
                            transactionLogger.error("Inside sending notification---" + notificationUrl);
                            AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                            transactionDetailsVO.setBillingDesc(billingDesc);
                            transactionDetailsVO.setBankReferenceId(transactionId);
                            transactionLogger.error("Inside transactionDetailsVO.setBankreffno---" + transactionId);
                            if(functions.isValueNull(eci))
                                transactionDetailsVO.setEci(eci);
                            
                            if(functions.isValueNull(merchantDetailsVO.getNotificationUrl())){
                                transactionDetailsVO.setMerchantNotificationUrl(merchantDetailsVO.getNotificationUrl());
                            }else{
                                transactionDetailsVO.setMerchantNotificationUrl("");
                            }

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
