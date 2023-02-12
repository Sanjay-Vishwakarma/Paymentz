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
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Vivek on 7/13/2020.
 */
public class SafexPayBackEndServlet extends HttpServlet
{
    private static SafexPayGatewayLogger transactionLogger=new SafexPayGatewayLogger(SafexPayBackEndServlet.class.getName());

    public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        transactionLogger.error("----Inside SafexPayBackEndServlet----");
        Functions functions=new Functions();
        AuditTrailVO auditTrailVO=new AuditTrailVO();
        StringBuilder responseMsg=new StringBuilder();
        TransactionManager transactionManager=new TransactionManager();
        MerchantDAO merchantDAO=new MerchantDAO();
        MerchantDetailsVO merchantDetailsVO=new MerchantDetailsVO();
        CommResponseVO commResponseVO=new CommResponseVO();
        ActionEntry entry = new ActionEntry();
        MySQLCodec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();

        BufferedReader br=request.getReader();
        String str;
        while ((str=br.readLine())!=null)
        {
            responseMsg.append(str);
        }
        transactionLogger.error("-----Notification JSON-----" + responseMsg);
        String trackingId = "";
        Connection con=null;
        try
        {
            if(responseMsg != null)
            {
                List<HashMap> responseList = new ArrayList();
                HashMap<String,String> innerMap = null;
                JSONArray responseArray = new JSONArray(responseMsg.toString());
                if (responseArray.length() > 0)
                {
                    JSONObject jsonObject = responseArray.getJSONObject(0);
                    if (jsonObject.has("Data"))
                    {
                        JSONObject data = jsonObject.getJSONObject("Data");
                        if (data.has("Success"))
                        {
                            JSONArray successResponseArray = data.getJSONArray("Success");
                            if (successResponseArray != null && successResponseArray.length() > 0)
                            {
                                for (int i = 0; i < successResponseArray.length(); i++)
                                {
                                    innerMap = new HashMap();
                                    JSONObject successResponse = successResponseArray.getJSONObject(i);
                                    if (successResponse.has("agRef"))
                                        innerMap.put("agRef", successResponse.getString("agRef"));
                                    if (successResponse.has("orderNo"))
                                        innerMap.put("orderNo", successResponse.getString("orderNo"));
                                    if (successResponse.has("amount"))
                                        innerMap.put("amount", successResponse.getString("amount"));
                                    if (successResponse.has("status"))
                                        innerMap.put("status", successResponse.getString("status"));
                                    if (successResponse.has("txnDate"))
                                        innerMap.put("txnDate", successResponse.getString("txnDate"));
                                    innerMap.put("txnStatus", "Success");

                                    responseList.add(innerMap);
                                }
                            }
                        }
                        if (data.has("Failure"))
                        {
                            JSONArray failResponseArray = data.getJSONArray("Failure");
                            if (failResponseArray != null && failResponseArray.length() > 0)
                            {
                                for (int i = 0; i < failResponseArray.length(); i++)
                                {
                                    innerMap = new HashMap();
                                    JSONObject failResponse = failResponseArray.getJSONObject(i);
                                    if (failResponse.has("agRef"))
                                        innerMap.put("agRef", failResponse.getString("agRef"));
                                    if (failResponse.has("orderNo"))
                                        innerMap.put("orderNo", failResponse.getString("orderNo"));
                                    if (failResponse.has("amount"))
                                        innerMap.put("amount", failResponse.getString("amount"));
                                    if (failResponse.has("status"))
                                        innerMap.put("status", failResponse.getString("status"));
                                    if (failResponse.has("txnDate"))
                                        innerMap.put("txnDate", failResponse.getString("txnDate"));
                                    innerMap.put("txnStatus", "Failure");

                                    responseList.add(innerMap);
                                }
                            }
                        }
                        if (data.has("Aborted"))
                        {
                            JSONArray abortResponseArray = data.getJSONArray("Aborted");
                            if (abortResponseArray != null && abortResponseArray.length() > 0)
                            {
                                for (int i = 0; i < abortResponseArray.length(); i++)
                                {
                                    innerMap = new HashMap();
                                    JSONObject abortResponse = abortResponseArray.getJSONObject(i);
                                    if (abortResponse.has("agRef"))
                                        innerMap.put("agRef", abortResponse.getString("agRef"));
                                    if (abortResponse.has("orderNo"))
                                        innerMap.put("orderNo", abortResponse.getString("orderNo"));
                                    if (abortResponse.has("amount"))
                                        innerMap.put("amount", abortResponse.getString("amount"));
                                    if (abortResponse.has("status"))
                                        innerMap.put("status", abortResponse.getString("status"));
                                    if (abortResponse.has("txnDate"))
                                        innerMap.put("txnDate", abortResponse.getString("txnDate"));

                                        innerMap.put("txnStatus", "Aborted");

                                    responseList.add(innerMap);
                                }
                            }
                        }
                    }
                }
                transactionLogger.error("responseList--->"+responseList.size());
                for (HashMap<String,String> responseMap : responseList)
                {
                    merchantDetailsVO=new MerchantDetailsVO();
                    trackingId = "";
                    String toId = "";
                    String accountId = "";
                    String fromType = "";
                    String paymentId = "";
                    String respAmount = "";
                    String amount = "";
                    String currency = "";
                    String errorCode = "";
                    String description = "";
                    String descriptor = "";
                    String transactionStatus = "";
                    String tmpl_currency = "";
                    String tmpl_amt = "";
                    String timestamp = "";
                    String eci = "";
                    String transactionId = "";
                    String isService = "";
                    String transType = "";
                    String status = "";
                    String message = "";
                    String dbStatus = "";
                    String errorName = "";
                    String confirmStatus = "";
                    String billingDesc = "";
                    String merchantTransactionId = "";
                    String notificationUrl = "";
                    String updatedStatus = "";
                    String cardType = "";
                    String cardTypeName = "";
                    String bankTransactionDate = "";
                    String txnStatus = "";
                    String RESPONSE_CODE = "";
                    String restatus = "";

                    if(responseMap.containsKey("orderNo"))
                        trackingId=responseMap.get("orderNo");
                    if(responseMap.containsKey("status"))
                        status=responseMap.get("status");
                    if(responseMap.containsKey("agRef"))
                        transactionId=responseMap.get("agRef");
                    if(responseMap.containsKey("amount"))
                        respAmount=responseMap.get("amount");
                    if(responseMap.containsKey("txnDate"))
                        bankTransactionDate=responseMap.get("txnDate");
                    if(responseMap.containsKey("txnStatus"))
                        txnStatus=responseMap.get("txnStatus");

                    TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                    if (transactionDetailsVO != null)
                    {
                        accountId = transactionDetailsVO.getAccountId();
                        cardType = transactionDetailsVO.getCardtype();
                        toId = transactionDetailsVO.getToid();
                        dbStatus = transactionDetailsVO.getStatus();
                        notificationUrl = "";
                        amount = transactionDetailsVO.getAmount();
                        currency = transactionDetailsVO.getCurrency();
                        tmpl_amt = transactionDetailsVO.getTemplateamount();
                        tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                        if (functions.isValueNull(transactionDetailsVO.getCcnum()))
                            transactionDetailsVO.setCcnum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
                        if (functions.isValueNull(transactionDetailsVO.getExpdate()))
                            transactionDetailsVO.setExpdate(PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate()));

                        auditTrailVO.setActionExecutorName("AcquirerCommonBackEnd");
                        auditTrailVO.setActionExecutorId(toId);
                        merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                        transactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());

                        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                        transactionLogger.error("DbStatus------" + dbStatus);
                        if (PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus) || (PZTransactionStatus.AUTH_FAILED.toString().equalsIgnoreCase(dbStatus) &&"Success".equalsIgnoreCase(txnStatus)))
                        {
                            con = Database.getConnection();

                            commResponseVO.setCurrency(currency);
                            commResponseVO.setTmpl_Amount(tmpl_amt);
                            commResponseVO.setTmpl_Currency(transactionDetailsVO.getCurrency());
                            commResponseVO.setTransactionId(transactionId);
                            commResponseVO.setEci(eci);
                            commResponseVO.setBankTransactionDate(bankTransactionDate);
                            commResponseVO.setErrorCode(status);
                            Double compRsAmount= Double.valueOf(respAmount);
                            Double compDbAmount= Double.valueOf(amount);
                            transactionLogger.error("response amount --->"+compRsAmount);
                            transactionLogger.error(" DB Amount--->"+compDbAmount);
                            if(!compDbAmount.equals(compRsAmount)){
                                status="17";
                                message="Failed-IRA";
                                transactionLogger.error("inside safexpay backend else Amount incorrect--->"+respAmount);
                                RESPONSE_CODE="11111";
                            }

                            StringBuffer dbBuffer = new StringBuffer();
                            if ("16".equalsIgnoreCase(status))
                            {
                                notificationUrl = transactionDetailsVO.getNotificationUrl();
                                restatus="success";
                                billingDesc=gatewayAccount.getDisplayName();
                                message = "Transaction Successful";
                                commResponseVO.setDescription(message);
                                commResponseVO.setStatus("success");
                                commResponseVO.setRemark(message);
                                commResponseVO.setDescriptor(billingDesc);
                                dbStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                                dbBuffer.append("update transaction_common set captureamount='" + respAmount + "',currency='" + currency + "' ,paymentid='" + transactionId + "',status='capturesuccess' ,remark='" + ESAPI.encoder().encodeForSQL(me, message) + "' ,successtimestamp='" + functions.getTimestamp() + "' where trackingid = " + trackingId);
                                transactionLogger.error("dbBuffer->" + dbBuffer);
                                Database.executeUpdate(dbBuffer.toString(), con);
                                entry.actionEntryForCommon(trackingId, respAmount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                updatedStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                            }
                            else if ("17".equalsIgnoreCase(status) || "25".equalsIgnoreCase(status))
                            {
                                notificationUrl = transactionDetailsVO.getNotificationUrl();
                                restatus="Failed";
                                message = txnStatus;
                                commResponseVO.setDescription(message);
                                commResponseVO.setStatus("failed");
                                commResponseVO.setRemark(message);
                                dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "' ,remark='" + ESAPI.encoder().encodeForSQL(me, message) + "'");
                                if("11111".equalsIgnoreCase(RESPONSE_CODE)){
                                    dbBuffer.append(",captureamount='"+ amount+"'");
                                }
                                dbBuffer.append(" ,remark='" + message + "',failuretimestamp='" + functions.getTimestamp() + "' where trackingid = " + trackingId);
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
                                asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, restatus, message, "");
                            }
                        }
                    }
                }
                response.setStatus(200);
                return;
            }

        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException ---->",e);
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException --"+trackingId+"-->", e);
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError --" + trackingId + "-->", systemError);
        }
        finally
        {
            Database.closeConnection(con);
        }

    }
}
