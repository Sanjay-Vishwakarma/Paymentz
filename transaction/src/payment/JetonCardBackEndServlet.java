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
import org.json.JSONException;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Admin on 2/20/2020.
 */
public class JetonCardBackEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger= new TransactionLogger(JetonCardBackEndServlet.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        doService(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        doService(request,response);
    }

    public void doService(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException
    {
        Functions functions=new Functions();
        Comm3DResponseVO transRespDetails = new Comm3DResponseVO();
        AuditTrailVO auditTrailVO=new AuditTrailVO();
        TransactionManager transactionManager=new TransactionManager();
        CommonPaymentProcess paymentProcess= new CommonPaymentProcess();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        MerchantDetailsVO merchantDetailsVO=new MerchantDetailsVO();
        MerchantDAO merchantDAO=new MerchantDAO();
        ActionEntry actionEntry=new ActionEntry();
        PrintWriter pWriter = response.getWriter();
        String responseCode = "200";
        String responseStatus = "OK";
        StringBuilder responseMsg = new StringBuilder();
        BufferedReader br = request.getReader();
        String str;
        while ((str = br.readLine()) != null)
        {
            responseMsg.append(str);
        }
        transactionLogger.error("-----JetonCardBackEndServlet Notification JSON-----" + responseMsg);
        Connection con=null;
        String errorCode="";
        String transactionId="";
        String message="";
        String responseMessage="";
        String descriptor="";
        String trackingId="";
        String timestamp="";
        String paymentMethod="";
        String toId="";
        String accountId="";
        String dbStatus="";
        String updatedStatus="";
        String notificationUrl="";
        String resStatus="";
        String sKey="";
        try
        {
            JSONObject jsonObject=new JSONObject(responseMsg.toString());
            if(jsonObject.has("code"))
                errorCode=jsonObject.getString("code");
            if(jsonObject.has("status"))
                resStatus=jsonObject.getString("status");
            if(jsonObject.has("transactionId"))
                transactionId=jsonObject.getString("transactionId");
            if(jsonObject.has("message"))
                responseMessage=jsonObject.getString("message");
            if(jsonObject.has("referenceNo"))
                trackingId = jsonObject.getString("referenceNo");
            if(jsonObject.has("timestamp"))
                timestamp=jsonObject.getString("timestamp");
            if(jsonObject.has("timestamp"))
                paymentMethod=jsonObject.getString("paymentMethod");
            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
            if (responseMsg!=null)
            {
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
                    if(functions.isValueNull(transactionDetailsVO.getExpdate())){
                        transactionDetailsVO.setExpdate(PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate()));
                    }
                    transRespDetails.setTransactionId(transactionId);
                    transRespDetails.setAmount(transactionDetailsVO.getAmount());
                    transRespDetails.setCurrency(transactionDetailsVO.getCurrency());
                    transRespDetails.setBankTransactionDate(timestamp);
                    transRespDetails.setTransactionType(paymentMethod);
                    StringBuffer dbBuffer = new StringBuffer();
                    transactionLogger.error("Inside DBStatus...." + dbStatus);
                    if("CREDIT_CARD".equalsIgnoreCase(paymentMethod))
                    {
                        if ((PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus)))
                        {
                            notificationUrl = transactionDetailsVO.getNotificationUrl();
                            transRespDetails.setRemark(responseMessage);
                            transRespDetails.setDescription(responseMessage);
                            //todo set status success and db status capturesuccess and update other details also
                            if ("00000".equalsIgnoreCase(errorCode))
                            {
                                transRespDetails.setStatus("success");
                                transRespDetails.setDescriptor(descriptor);
                                transactionDetailsVO.setBillingDesc(descriptor);
                                dbBuffer.append("update transaction_common set paymentid='" + transactionId + "',captureamount='" + transactionDetailsVO.getAmount() + "',status='capturesuccess'");
                                paymentProcess.actionEntry(trackingId, transactionDetailsVO.getAmount(), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");
                                updatedStatus = "capturesuccess";
                            }
                            else
                            {
                                responseStatus = "Failed(" + responseMessage + ")";
                                updatedStatus = "authfailed";
                                dbBuffer.append("update transaction_common set status='authfailed'");
                                actionEntry.actionEntryForCommon(trackingId, transactionDetailsVO.getAmount(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authfailed");
                            }
                            transactionLogger.error("responseCode---->" + responseCode);
                            dbBuffer.append(" ,paymentid='" + transactionId + "',remark='" + responseMessage + "' where trackingid = " + trackingId);
                            con = Database.getConnection();
                            Database.executeUpdate(dbBuffer.toString(), con);
                            transactionLogger.error("JetonCardBackEnd dbBuffer.....!!!" + dbBuffer);
                        }
                        else
                        {
                            if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                            {
                                transRespDetails.setDescriptor(descriptor);
                                transactionDetailsVO.setBillingDesc(descriptor);
                                message = "Transaction Successful";
                                responseStatus = "Transaction Successful";
                                updatedStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();

                            }
                            else if (PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                            {
                                message = "Transaction Successful";
                                responseStatus = "Transaction Successful";
                                updatedStatus = PZTransactionStatus.AUTH_SUCCESS.toString();
                            }
                            else if (PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus))
                            {
                                message = "Transaction Declined";
                                responseStatus = "Transaction Failed";
                                updatedStatus = PZTransactionStatus.AUTH_FAILED.toString();
                            }
                            else
                            {
                                message = "Transaction Declined";
                                responseStatus = "Transaction Failed";
                                updatedStatus = PZTransactionStatus.FAILED.toString();
                            }
                            if (functions.isValueNull(responseMessage) && functions.isValueNull(trackingId))
                            {
                                dbBuffer.append("update transaction_common set remark=? where trackingId=?");
                                con = Database.getConnection();
                                PreparedStatement ps = con.prepareStatement(dbBuffer.toString());
                                ps.setString(1, responseMessage);
                                ps.setString(2, trackingId);
                                transactionLogger.error("ps---->" + ps);
                                ps.executeUpdate();
                            }

                        }
                    }else if("PAY_TO_CARD".equalsIgnoreCase(paymentMethod))
                    {
                        if (PZTransactionStatus.PAYOUT_STARTED.toString().equalsIgnoreCase(dbStatus))
                        {
                            notificationUrl = transactionDetailsVO.getNotificationUrl();
                            transRespDetails.setErrorCode(errorCode);
                            if("40111".equalsIgnoreCase(errorCode) && "APPROVED".equalsIgnoreCase(resStatus))//APPROVED
                            {
                                transRespDetails.setDescription(resStatus);
                                transRespDetails.setRemark(resStatus);
                                transRespDetails.setStatus("success");
                                transRespDetails.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());

                                dbBuffer.append("update transaction_common set payoutamount='" + transactionDetailsVO.getAmount() + "',currency='" + transactionDetailsVO.getCurrency() + "' ,paymentid='" + transactionId + "',status='payoutsuccessful' ");
                                actionEntry.actionEntryForCommon(trackingId, transactionDetailsVO.getAmount(), ActionEntry.ACTION_PAYOUT_SUCCESSFUL, ActionEntry.ACTION_PAYOUT_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                                updatedStatus = PZTransactionStatus.PAYOUT_SUCCESS.toString();
                            }else
                            {
                                transRespDetails.setStatus("fail");
                                transRespDetails.setDescription(resStatus);
                                transRespDetails.setRemark(resStatus);
                                dbStatus = PZTransactionStatus.PAYOUT_FAILED.toString();
                                dbBuffer.append("update transaction_common set status='payoutfailed',paymentid='" + transactionId + "'");
                                actionEntry.actionEntryForCommon(trackingId, transactionDetailsVO.getAmount(), ActionEntry.ACTION_PAYOUT_FAILED, ActionEntry.ACTION_PAYOUT_FAILED, transRespDetails, auditTrailVO, null);
                                updatedStatus = PZTransactionStatus.PAYOUT_FAILED.toString();
                            }
                            dbBuffer.append(" ,remark='" + resStatus + "' where trackingid = " + trackingId);
                            transactionLogger.error("payout dbBuffer--->"+dbBuffer);
                            con = Database.getConnection();
                            Database.executeUpdate(dbBuffer.toString(), con);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);
                        }

                    }
                    if (functions.isValueNull(notificationUrl))
                    {
                        transactionLogger.error("inside sending notification---" + notificationUrl);
                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                        asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, updatedStatus, responseMessage,"");
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
        }
        catch (JSONException e)
        {
           transactionLogger.error("JSONException --->",e);
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException --->", e);
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError --->", systemError);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException --->", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
}
