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
import org.json.JSONException;
import org.json.JSONObject;
import org.owasp.esapi.codecs.MySQLCodec;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Enumeration;

/**
 * Created by Admin on 2/2/2021.
 */
public class PayonOppwaBackEndServlet extends HttpServlet
{
    TransactionLogger transactionLogger= new TransactionLogger(PayonOppwaBackEndServlet.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        transactionLogger.error("---- Inside PayonOppwaBackEndServlet ----");
        Connection con = null;
        Functions functions = new Functions();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        TransactionManager transactionManager = new TransactionManager();
        MerchantDAO merchantDAO = new MerchantDAO();
        ActionEntry entry = new ActionEntry();
        CommResponseVO transRespDetails = new CommResponseVO();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        PrintWriter pWriter = response.getWriter();
        StringBuilder responseMsg = new StringBuilder();

        MySQLCodec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        try
        {

            JSONObject jsonObject = new JSONObject(response);
            JSONObject redirect=jsonObject.getJSONObject("redirect");
            String value="";
            String url_in_response="";
            String name="";

            if(jsonObject.has("redirect"))
            {

                if(redirect.has("url"))
                {
                    url_in_response=redirect.getString("url");
                }
                if(redirect.has("parameters"))
                {
                    name=redirect.getString("parameters");

                    org.json.JSONArray jsonArray = redirect.getJSONArray("parameters");
                    if (jsonArray != null)
                    {
                        for (int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject parameters = jsonArray.getJSONObject(i);
                            if (i != 0)
                            {
                                name =parameters.getString("name");

                                    value=parameters.getString("value");
                                }}}


                        }
                    }}
            catch (JSONException e)
            {
                e.printStackTrace();
            }





                String trackingId = request.getParameter("p_order_num");
            String p_pay_result = request.getParameter("p_pay_result");
            String p_pay_info=request.getParameter("p_pay_info");
            String responsecode=request.getParameter("responseCode");
            String paymentId=request.getParameter("p_trans_num");



            String currency = "";
            String status = "";
            String message = "";
            String toId = "";
            String accountId = "";
            String dbStatus = "";
            String billingDesc = "";
            String notificationUrl = "";
            String description = "";
            String amount = "";
            String tmpl_amt = "";
            String tmpl_currency = "";
            transactionLogger.error("trackingId-->" + trackingId);
            transactionLogger.error("transactionId-->" + paymentId);
            transactionLogger.error("status-->" + status);
            transactionLogger.error("tradeStatus-->" + p_pay_info);
            transactionLogger.error("remark-->" + p_pay_result);

            if (functions.isValueNull(trackingId))
            {
                try{

                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                if (transactionDetailsVO != null)
                {
                    toId = transactionDetailsVO.getToid();
                    accountId = transactionDetailsVO.getAccountId();
                    GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                    dbStatus = transactionDetailsVO.getStatus();
                    amount = transactionDetailsVO.getAmount();
                    currency = transactionDetailsVO.getCurrency();
                    tmpl_amt = transactionDetailsVO.getTemplateamount();
                    tmpl_currency = transactionDetailsVO.getTemplatecurrency();

                    if (functions.isValueNull(transactionDetailsVO.getCcnum()))
                        transactionDetailsVO.setCcnum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
                    if (functions.isValueNull(transactionDetailsVO.getExpdate()))
                    {
                        transactionDetailsVO.setExpdate(PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate()));
                    }
                    merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                    transactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());

                    transactionLogger.error("DbStatus------" + dbStatus);
                    con = Database.getConnection();
                    if ((PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus)))
                    {
                        auditTrailVO.setActionExecutorName("AcquirerBackEnd");
                        auditTrailVO.setActionExecutorId(toId);
                        StringBuffer dbBuffer = new StringBuffer();
                        transRespDetails = new CommResponseVO();
                        transRespDetails.setTransactionId(paymentId);
                        transRespDetails.setTmpl_Amount(tmpl_amt);
                        transRespDetails.setTmpl_Currency(tmpl_currency);
                        transRespDetails.setCurrency(currency);

                        if ((p_pay_result.equalsIgnoreCase("1")))
                        {
                            notificationUrl = transactionDetailsVO.getNotificationUrl();
                            transactionLogger.error("Inside AUTH_STARTED ");

                            billingDesc = gatewayAccount.getDisplayName();
                            status = "success";
                            if (functions.isValueNull(p_pay_info))
                            {
                                message = p_pay_info;
                            }
                            else
                            {
                                message = "Transaction Successful";
                            }

                            dbStatus = "capturesuccess";
                            transRespDetails.setStatus(status);
                            transRespDetails.setRemark(message);
                            transRespDetails.setDescriptor(billingDesc);
                            transRespDetails.setDescription(message);
                            //transRespDetails.setBankTransactionDate();


                            dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + paymentId + "',status='capturesuccess',remark='" + message + "' where trackingid = " + trackingId);
                            transactionLogger.error("update query------------>" + dbBuffer.toString());
                            Database.executeUpdate(dbBuffer.toString(), con);
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);
                        }
                        else if(p_pay_result.equalsIgnoreCase("-1"))
                        {
                            billingDesc = gatewayAccount.getDisplayName();
                            status = "pending";

                            if(functions.isValueNull(p_pay_info))
                            {
                                message=p_pay_info;
                            }
                            else
                            {
                                message = "Transaction pending";
                            }
                            dbStatus = "pending";

                        }
                        else
                        {
                            notificationUrl = transactionDetailsVO.getNotificationUrl();
                            transactionLogger.error("Inside AUTH_STARTED ");

                            status = "failed";

                            if (functions.isValueNull(p_pay_info))
                            {
                                message = p_pay_info;
                            }
                            else
                            {
                                message = "Transaction failed";
                            }

                            dbStatus = "authfailed";
                            transRespDetails.setStatus(status);
                            transRespDetails.setRemark(message);
                            transRespDetails.setDescription(message);
                            //  transRespDetails.setBankTransactionDate();


                            dbBuffer.append("update transaction_common set paymentid='" + paymentId + "',status='authfailed',remark='" + message + "' where trackingid = " + trackingId);
                            transactionLogger.error("update query------------>" + dbBuffer.toString());
                            Database.executeUpdate(dbBuffer.toString(), con);
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);
                        }


                    }

                    if (functions.isValueNull(notificationUrl)&& !status.equalsIgnoreCase("pending"))
                    {
                        transactionLogger.error("Inside sending notification ---" + notificationUrl);
                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                        transactionDetailsVO.setBillingDesc(billingDesc);
                        asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, dbStatus, message, "");
                    }
                }


                String responseCode = "200";
                String responseStatus = "success";
                response.setStatus(200);
                pWriter.println(responseStatus);
                pWriter.flush();
                return;


        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException --",e);
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError --",systemError);

        }


        finally
        {
            Database.closeConnection(con);
        }
    }
}}


