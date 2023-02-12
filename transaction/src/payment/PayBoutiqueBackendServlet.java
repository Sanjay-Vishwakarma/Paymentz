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
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.payboutique.PayBoutiqueUtils;
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
 * Created by Balaji on 08-Nov-19.
 */

public class PayBoutiqueBackendServlet extends HttpServlet
{
    TransactionLogger transactionLogger= new TransactionLogger(PayBoutiqueBackendServlet.class.getName());
    PayBoutiqueUtils payBoutiqueUtils = new PayBoutiqueUtils();
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
        transactionLogger.error("Inside PayBoutiqueBackendServlet ---");
        TransactionManager transactionManager=new TransactionManager();
        CommResponseVO transRespDetails = new CommResponseVO();
        ActionEntry entry = new ActionEntry();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        Functions functions=new Functions();
        PrintWriter pWriter = response.getWriter();
//        String responseCode = "200";
//        String responseStatus = "OK";
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
        String orderId="";
        String description ="";
        String referenceId="";
        BufferedReader br = request.getReader();
        String str;
        while ((str = br.readLine()) != null)
        {
            responseMsg.append(str);
        }
        try
        {
            String xml_response = request.getParameter("xml");
            trackingId = request.getParameter("trackingId");
            Map<String, String> response_params = payBoutiqueUtils.readResponseXML(xml_response);
            status = response_params.get("Status");
            orderId=response_params.get("OrderID");
            description=response_params.get("Description");
            referenceId=response_params.get("ReferenceID");

            transactionLogger.debug("PayBoutiqueBackendServlet  :  status_response---------> "+status);
            transactionLogger.error("PayBoutiqueBackendServlet  :  trackingId_response ---------->" + trackingId);
            transactionLogger.error("PayBoutiqueBackendServlet  :  orderId_response ---------->" + orderId);
            trackingId=orderId; // this is used bcz we will get backend response on the backend which is defind in merchant account level in backoffice.
            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);

            if (transactionDetailsVO != null)
            {
                toId = transactionDetailsVO.getToid();
                accountId = transactionDetailsVO.getAccountId();
                currency = transactionDetailsVO.getCurrency();
                amount1 = transactionDetailsVO.getAmount();
                dbStatus = transactionDetailsVO.getStatus();
                transactionLogger.debug("PayBoutiquendServlet : dbStatus -----" + dbStatus);
                notificationUrl = transactionDetailsVO.getNotificationUrl();
                tmpl_amt = transactionDetailsVO.getTemplateamount();
                tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                MerchantConfigManager merchantConfigManager = new MerchantConfigManager();
                merchantDetailsVO = merchantConfigManager.getMerchantDetailFromToId(toId);
                StringBuffer dbBuffer = new StringBuffer();
                transactionId = referenceId;


                auditTrailVO.setActionExecutorName("AcquirerBackEnd");
                auditTrailVO.setActionExecutorId(toId);
                con = Database.getConnection();

                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) && status.equalsIgnoreCase("captured")){
//                  successful transaction
                    transactionLogger.error(" PayBoutiqueBackendServlet : Inside successful transaction ");
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
                    transactionLogger.error("PayBoutiqueBackendServlet : update query------------>" + dbBuffer.toString());
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

                    transactionLogger.error("PayBoutiqueBackendServlet : Inside failed transaction ");
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
                    transactionLogger.error("PayBoutiquBackendServlet : update query------------>" + dbBuffer.toString());
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
            transactionLogger.error("PayBoutiquendServlet : BackePZDBViolationException---->"+e);
        }
        catch (SystemError se){
            transactionLogger.error("PayBoutiquendServlet : SystemError---->"+se);
        }
        catch(PZTechnicalViolationException e){
            transactionLogger.error("PayBoutiqueBackendServlet : PZTechnicalViolationException---->"+e);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
}
