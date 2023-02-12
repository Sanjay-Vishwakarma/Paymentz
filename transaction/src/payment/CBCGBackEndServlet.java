package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.PZTransactionStatus;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommonPaymentProcess;
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
 * Created by Admin on 8/14/2020.
 */
public class CBCGBackEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger= new TransactionLogger(FlutterWaveBackEndServlet.class.getName());
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request,response);
    }

    protected void doService(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException
    {
        transactionLogger.error("---Inside CBCGBackEndServlet---");

        MySQLCodec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        TransactionManager transactionManager=new TransactionManager();
        Functions functions=new Functions();
        PrintWriter pWriter=response.getWriter();
        AuditTrailVO auditTrailVO=new AuditTrailVO();
        Comm3DResponseVO transRespDetails = new Comm3DResponseVO();
        CommonPaymentProcess paymentProcess= new CommonPaymentProcess();
        StatusSyncDAO statusSyncDAO=new StatusSyncDAO();
        MerchantDetailsVO merchantDetailsVO=new MerchantDetailsVO();
        MerchantDAO merchantDAO=new MerchantDAO();
        String responseCode = "200";
        String responseStatus = "OK";
        String transactionDate="";
        String status="";
        String trackingId="";
        String transactionId="";
        String message="";
        String responseAmount="";
        String dbStatus="";
        String accountId="";
        String toId="";
        String descriptor="";
        String notificationUrl="";
        String updatedStatus="";
        String sKey="";

        StringBuilder responseMsg=new StringBuilder();
        BufferedReader br= request.getReader();
        String str;
        while ((str=br.readLine())!=null)
        {
            responseMsg.append(str);
        }
        transactionLogger.error("-----CBCGBackEndServlet Notification JSON-----" + responseMsg);
        Connection con=null;
        try
        {
            if (responseMsg!=null)
            {
                JSONObject responseJSON = new JSONObject(responseMsg.toString());
                if (responseJSON.has("ResponseDate"))
                    transactionDate = responseJSON.getString("ResponseDate");
                if (responseJSON.has("TrxID"))
                    transactionId = responseJSON.getString("TrxID");
                if (responseJSON.has("MerchantTrxID"))
                    trackingId = responseJSON.getString("MerchantTrxID");
                if (responseJSON.has("Status"))
                    status = responseJSON.getString("Status");
                if (responseJSON.has("Information"))
                    message = responseJSON.getString("Information");
                if (responseJSON.has("Amount"))
                    responseAmount = responseJSON.getString("Amount");

                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                if (transactionDetailsVO != null && functions.isValueNull(transactionDetailsVO.getTrackingid()))
                {
                    accountId = transactionDetailsVO.getAccountId();
                    toId = transactionDetailsVO.getToid();
                    dbStatus = transactionDetailsVO.getStatus();

                    auditTrailVO.setActionExecutorName("AcquirerCommonBackEnd");
                    auditTrailVO.setActionExecutorId(toId);
                    GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                    descriptor = gatewayAccount.getDisplayName();
                    merchantDetailsVO=merchantDAO.getMemberDetails(toId);
                    if(merchantDetailsVO!=null)
                        sKey=merchantDetailsVO.getKey();
                    transactionDetailsVO.setSecretKey(sKey);
                    if (functions.isValueNull(transactionDetailsVO.getCcnum()))
                    {
                        transactionDetailsVO.setCcnum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
                    }
                    if (functions.isValueNull(transactionDetailsVO.getExpdate()))
                    {
                        transactionDetailsVO.setExpdate(PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate()));
                    }
                    transRespDetails.setTransactionId(transactionId);
                    transRespDetails.setAmount(responseAmount);
                    transRespDetails.setCurrency(transactionDetailsVO.getCurrency());
                    transRespDetails.setBankTransactionDate(transactionDate);
                    StringBuffer dbBuffer = new StringBuffer();
                    transactionLogger.error("Inside DBStatus..trackingid--"+trackingId+"--" + dbStatus);
                    if ((PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus)))
                    {
                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                        transRespDetails.setRemark(message);
                        transRespDetails.setDescription(message);
                        //todo set status success and db status capturesuccess and update other details also
                        con = Database.getConnection();
                        if ("Approved".equalsIgnoreCase(status))
                        {
                            transRespDetails.setStatus("success");
                            transRespDetails.setDescriptor(descriptor);
                            transactionDetailsVO.setBillingDesc(descriptor);
                            dbBuffer.append("update transaction_common set paymentid='" + transactionId + "',captureamount='" + responseAmount + "',status='capturesuccess',paymentid='" + transactionId + "',remark='" + ESAPI.encoder().encodeForSQL(me,message) + "' where trackingid = " + trackingId);
                            transactionLogger.error("CBCGBackEndServlet dbBuffer.....!!!" + dbBuffer);
                            Database.executeUpdate(dbBuffer.toString(), con);
                            paymentProcess.actionEntry(trackingId, transactionDetailsVO.getAmount(), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");
                            updatedStatus = "capturesuccess";
                        }
                        else
                        {
                            updatedStatus = "authfailed";
                            dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',remark='" + ESAPI.encoder().encodeForSQL(me,message) + "' where trackingid = " + trackingId);
                            transactionLogger.error("CBCGBackEndServlet dbBuffer.....!!!" + dbBuffer);
                            Database.executeUpdate(dbBuffer.toString(), con);
                            paymentProcess.actionEntry(trackingId, transactionDetailsVO.getAmount(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, null, auditTrailVO);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authfailed");
                        }
                        if (functions.isValueNull(notificationUrl))
                        {
                            transactionLogger.error("inside sending notification---" + notificationUrl);
                            AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                            asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, updatedStatus, message,"");
                        }
                    }
                }
                JSONObject jsonResObject = new JSONObject();
                jsonResObject.put("responseCode", responseCode);
                jsonResObject.put("responseStatus", responseStatus);
                response.setContentType("application/json");
                response.setStatus(200);
                pWriter.println(jsonResObject.toString());
                pWriter.flush();
                return;
                }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException --->",e);
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException --"+trackingId+"--->", e);
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError --"+trackingId+"--->", systemError);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
}
