package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.TransactionManager;
import com.manager.vo.TransactionDetailsVO;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.statussync.StatusSyncDAO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Enumeration;

/**
 * Created by Balaji on 24-Jan-20.
 */

public class ZotaBackEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger= new TransactionLogger(ZotaPayBackEndServlet.class.getName());
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request,response);
    }

    public void doService(HttpServletRequest request,HttpServletResponse response)
    {

        transactionLogger.error("-----Inside ZotaPayBackEndServlet---------");
        Connection con = null;
        try
        {
            CommResponseVO transRespDetails = null;
            TransactionManager transactionManager = new TransactionManager();
            StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
            AuditTrailVO auditTrailVO = new AuditTrailVO();
            Functions functions = new Functions();
            String status = "";
            String responseStatus = "";
            String message = "";
            StringBuffer dbBuffer = new StringBuffer();
            ActionEntry entry = new ActionEntry();

            String trackingId = "";
            String transactionId = "";
            String statusFromResponse = "";

            PrintWriter pWriter = response.getWriter();
            StringBuilder responseMsg = new StringBuilder();

            BufferedReader br = request.getReader();
            String str;
            while((str = br.readLine()) != null ){
                responseMsg.append(str);
            }

            transactionLogger.error("-----payment confirmation ZotaBackeEndNotification-----"+responseMsg);

            if (functions.isValueNull(responseMsg.toString()) && responseMsg.toString().contains("{"))
            {
                transactionLogger.error("inside response Msg read-->");
                if (responseMsg !=null)
                {

                    JSONObject jsonObject = new JSONObject(String.valueOf(responseMsg));

                    if (jsonObject.has("status"))
                    {
                        statusFromResponse= jsonObject.getString("status");
                        transactionLogger.error("status -- >"+statusFromResponse);
                    }
                    if (jsonObject.has("merchantOrderID"))
                    {
                        trackingId = jsonObject.getString("merchantOrderID");
                        transactionLogger.error("merchantOrderID -- >"+trackingId);
                    }
                    if (jsonObject.has("orderID"))
                    {
                        transactionId = jsonObject.getString("orderID");
                        transactionLogger.error("orderID --->"+transactionId);
                    }
                    if (jsonObject.has("errorMessage"))
                    {
                        message = jsonObject.getString("errorMessage");
                        transactionLogger.error("message is -- >"+message);
                    }
                }
            }

            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
            String accountId = transactionDetailsVO.getAccountId();
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

            String dbStatus = transactionDetailsVO.getStatus();
            String currency = transactionDetailsVO.getCurrency();
            String amount1 = transactionDetailsVO.getAmount();
            String tmpl_amt = transactionDetailsVO.getTemplateamount();
            String tmpl_currency = transactionDetailsVO.getTemplatecurrency();
            String billingDesc = gatewayAccount.getDisplayName();


            con = Database.getConnection();
            if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) && statusFromResponse.equalsIgnoreCase("APPROVED")/* && isService.equalsIgnoreCase("Y")*/)
            {
                //successful transaction entry
                transactionLogger.error("Inside db status AUTH_STARTED ");
                transRespDetails = new CommResponseVO();
                status = "success";
                responseStatus = "Successful";
                if (message.isEmpty()&& message.contains(""))
                {
                    message = "Transaction Successful";
                }
                else
                {
                    message = message;
                }
                dbStatus = "capturesuccess";
                // transRespDetails.setDescription(ResponseMsg);
                transRespDetails.setStatus(status);
                transRespDetails.setRemark("Transaction Successful");
                transRespDetails.setDescriptor(billingDesc);
                transRespDetails.setTransactionId(transactionId);
                transRespDetails.setTmpl_Amount(tmpl_amt);
                transRespDetails.setTmpl_Currency(tmpl_currency);
                transRespDetails.setCurrency(currency);


                dbBuffer.append("update transaction_common set captureamount='" + amount1 + "',paymentid='" + transactionId + "',status='capturesuccess',remark='" + message + "' where trackingid = " + trackingId);
                transactionLogger.error("update query------------>" + dbBuffer.toString());
                Database.executeUpdate(dbBuffer.toString(), con);
                entry.actionEntryForCommon(trackingId, amount1, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, auditTrailVO, null);
            }
            else  if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) && statusFromResponse.equalsIgnoreCase("DECLINED")/* && isService.equalsIgnoreCase("Y")*/)
            {
                //Declined transaction entry
                transactionLogger.error("Inside declined transaction");
                status = "fail";
                responseStatus = "DECLINED";
                if (message.isEmpty()&& message.contains(""))
                {
                    message = "Transaction Failed";
                }
                else
                {
                    message = message;
                }
                dbStatus = "authfailed";

                dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',remark='" + message + "' where trackingid = " + trackingId);
                transactionLogger.error("update query------------>" + dbBuffer.toString());
                Database.executeUpdate(dbBuffer.toString(), con);
                entry.actionEntryForCommon(trackingId, amount1, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
            }
            else if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) && (statusFromResponse.equalsIgnoreCase("PENDING")|| statusFromResponse.equalsIgnoreCase("CREATED") || statusFromResponse.equalsIgnoreCase("PROCESSING")))
            {
                //Pending transaction entry
                transactionLogger.error("Inside pending transaction");
                status = "fail";
                responseStatus = "Pending";
                message = "Transaction Pending";
                dbStatus = "pending";

                dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',remark='" + message + "' where trackingid = " + trackingId);
                transactionLogger.error("update query------------>" + dbBuffer.toString());
                Database.executeUpdate(dbBuffer.toString(), con);
                entry.actionEntryForCommon(trackingId, amount1, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);

            }

            pWriter.println("HTTP 200 OK");
            pWriter.flush();
        }
        catch (IOException e)
        {
            transactionLogger.error("IOException---->",e);
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException---->",e);
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError---->",se);
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException In ZotaBackEndServlet---->",e);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
}
