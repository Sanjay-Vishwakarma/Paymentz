package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.MerchantConfigManager;
import com.manager.TransactionManager;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.Triple000.Triple000Utils;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Map;

/**
 * Created by Admin on 10/19/2020.
 */
public class Triple000BackendServlet extends HttpServlet
{
    TransactionLogger transactionLogger= new TransactionLogger(Triple000BackendServlet.class.getName());
    Triple000Utils triple000Utils = new Triple000Utils();
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }
    public void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        transactionLogger.error("Inside Triple000BackendServlet ---");
        TransactionManager transactionManager=new TransactionManager();
        CommResponseVO transRespDetails = new CommResponseVO();
        ActionEntry entry = new ActionEntry();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        Functions functions=new Functions();
        PrintWriter pWriter = response.getWriter();
        AuditTrailVO auditTrailVO= new AuditTrailVO();
        ActionEntry actionEntry = new ActionEntry();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        Connection con=null;
        String trackingId = "";
        String toId="";
        String accountId = "";
//        String fromType = "";
//        String transactionStatus = "";
        String eci = "";
        String currency="";
        String amount1="";

        String transactionId = "";
        String status="";
        String message="";
        String dbStatus="";
        String billingDesc="";
        String notificationUrl="";
        String updatedStatus="";
        String cardType="";
        String tmpl_amt="";
        String tmpl_currency="";
        String cardTypeName="";
        StringBuilder responseMsg=new StringBuilder();
        String confirmStatus="";
        String tran_id="";
        String description ="";
        String referenceId="";
        String type="";
        String error_code="";
        BufferedReader br = request.getReader();
        String str;
        while ((str = br.readLine()) != null)
        {
            responseMsg.append(str);
        }
        try
        {
            String xml_response = request.getParameter("data");
            trackingId = request.getParameter("trackingId");
            Map<String, String> response_params = triple000Utils.readTriple000XMLResponse(xml_response, trackingId, type);
            status = response_params.get("message");
            tran_id=response_params.get("tran_id");
            error_code=response_params.get("error_code");


            transactionLogger.debug("Triple000BackendServlet  :  status_response---------> "+status);
            transactionLogger.error("Triple000BackendServlet  :  trackingId_response ---------->" + trackingId);
            transactionLogger.error("Triple000BackendServlet :  orderId_response ---------->" + tran_id);

            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);

            if (transactionDetailsVO != null)
            {
                toId = transactionDetailsVO.getToid();
                accountId = transactionDetailsVO.getAccountId();
                currency = transactionDetailsVO.getCurrency();
                amount1 = transactionDetailsVO.getAmount();
                dbStatus = transactionDetailsVO.getStatus();
                transactionLogger.debug("Triple000BackendServlet : dbStatus -----" + dbStatus);
                notificationUrl = transactionDetailsVO.getNotificationUrl();
                tmpl_amt = transactionDetailsVO.getTemplateamount();
                tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                MerchantConfigManager merchantConfigManager = new MerchantConfigManager();
                merchantDetailsVO = merchantConfigManager.getMerchantDetailFromToId(toId);
                StringBuffer dbBuffer = new StringBuffer();
                transactionId = tran_id;


                auditTrailVO.setActionExecutorName("AcquirerBackEnd");
                auditTrailVO.setActionExecutorId(toId);
                con = Database.getConnection();

                if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus) ){
//                  successful transaction
                    transactionLogger.error(" Triple000BackendServlet : Inside successful transaction ");
                    transRespDetails = new CommResponseVO();
                    billingDesc = gatewayAccount.getDisplayName();
                    status = "Success";
                    message = "Transaction Successful";
                    transRespDetails.setDescription(message);
                    transRespDetails.setStatus(status);
                    transRespDetails.setRemark(message);
                    transRespDetails.setDescriptor(billingDesc);
                    transRespDetails.setTransactionId(referenceId);
                    transRespDetails.setTmpl_Amount(tmpl_amt);
                    transRespDetails.setTmpl_Currency(tmpl_currency);
                    transRespDetails.setCurrency(currency);

                    dbBuffer.append("update transaction_common set paymentid='" + referenceId + "',status='capturesuccess',eci='" + eci + "' where trackingid = " + trackingId);
                    transactionLogger.error("Triple000BackendServlet : update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);
                    entry.actionEntryForCommon(trackingId, amount1, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");
                }
                else if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equalsIgnoreCase(dbStatus))
                {
                    transactionLogger.debug("Inside CAPTURE_SUCCESS and approved");
                    status = "success";
                    message = "SYS: Transaction Successful";
                    updatedStatus=PZTransactionStatus.CAPTURE_SUCCESS.toString();
                    dbBuffer.append("update transaction_common set paymentid='" + referenceId + "',status='capturesuccess',eci='" + eci + "' where trackingid ='" + trackingId + "'");
                    transactionLogger.error("update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);
                }
                else if (PZTransactionStatus.AUTH_FAILED.toString().equalsIgnoreCase(dbStatus))
                {
                    status = "Failed";
                    message = "SYS: Transaction Failed";

                    updatedStatus=PZTransactionStatus.AUTH_FAILED.toString();
                    dbBuffer.append("update transaction_common set paymentid='" + referenceId + "',status='authfailed' ,eci='" + eci +"'  where trackingid ='" + trackingId + "'");
                    transactionLogger.error("update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);
                }
                else if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) && !status.equalsIgnoreCase("captured"))
                {
//                   failed transaction

                    transactionLogger.error("Triple000BackendServlet : Inside failed transaction ");
                    transRespDetails = new CommResponseVO();
                    billingDesc = gatewayAccount.getDisplayName();
                    status = "Failed";
                    message = "Transaction Failed";
//                    transRespDetails.setDescription(description);
                    transRespDetails.setStatus(status);
                    transRespDetails.setRemark(message);
                    transRespDetails.setTransactionId(referenceId);
                    transRespDetails.setTmpl_Amount(tmpl_amt);
                    transRespDetails.setTmpl_Currency(tmpl_currency);
                    transRespDetails.setCurrency(currency);

                    dbBuffer.append("update transaction_common set paymentid='" + referenceId + "',status='authfailed',eci='" + eci + "' where trackingid = " + trackingId);
                    transactionLogger.error("Triple000BackendServlet : update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);
                    entry.actionEntryForCommon(trackingId, amount1, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authfailed");
                }

                AsynchronousMailService asynchronousMailService = AsynchronousMailService.getInstance();
                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                AsynchronousSmsService smsService = AsynchronousSmsService.getInstance();
                smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                if (functions.isValueNull(notificationUrl))
                {
                    transactionLogger.error("inside sending notification from Backend---" + notificationUrl);
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, updatedStatus, message,"");
                }
            }

        }catch (PZDBViolationException e){
            transactionLogger.error("Triple000BackendServlet : PZDBViolationException---->"+e);
        }
        catch (SystemError se){
            transactionLogger.error("Triple000BackendServlet : SystemError---->"+se);
        }

        finally
        {
            Database.closeConnection(con);
        }
    }
}

