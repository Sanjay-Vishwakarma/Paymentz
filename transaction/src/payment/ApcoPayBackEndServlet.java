package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.PZTransactionStatus;
import com.payment.apco.core.ApcoPayUtills;
import com.payment.common.core.CommResponseVO;
import com.payment.statussync.StatusSyncDAO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.Map;

/**
 * Created by ThinkPadT410 on 2/4/2017.
 */
public class ApcoPayBackEndServlet extends PzServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(ApcoPayBackEndServlet.class.getName());

    public ApcoPayBackEndServlet()
    {
        super();
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        transactionLogger.error("------inside ApcoPayBackEndServlet-----");
        CommResponseVO commResponseVO = new CommResponseVO();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        MerchantDAO merchantDAO=new MerchantDAO();
        MerchantDetailsVO merchantDetailsVO=new MerchantDetailsVO();
        TransactionManager transactionManager = new TransactionManager();
        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
        Functions functions= new Functions();

        AuditTrailVO auditTrailVO = new AuditTrailVO();
        auditTrailVO.setActionExecutorName("AcquirerBackEnd");

        ActionEntry entry = new ActionEntry();
        StringBuffer sb = new StringBuffer();

        String captureAmount = "";
        String trackingId = "";
        String description = "";
        String status = "";
        String displayName = "";
        String toId = "";

        String remark = "";
        String accountId = "";

        String bankStatus = "";
        String pspid = "";
        String declineReason = "";
        String notificationUrl="";
        String respStatus="";
        String message="";
        String dbStatus="";
        String expDate="";
        String expMonth="";
        String expYear="";

        int result=0;

        Connection con = null;

        String xmlResponse = req.getParameter("params");
        transactionLogger.error("xmlResponse::::" + xmlResponse);
        if (xmlResponse != null)
        {
            try
            {
                Map<String, String> stringStringMap = ApcoPayUtills.readApcopayRedirectionXMLReponse(xmlResponse);

                trackingId = stringStringMap.get("ORef");
                bankStatus = stringStringMap.get("Result");
                pspid = stringStringMap.get("pspid");

                captureAmount = stringStringMap.get("Value");
                declineReason = stringStringMap.get("ExtendedErr");

                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);

                if(transactionDetailsVO!=null)
                {
                    description = transactionDetailsVO.getDescription();
                    accountId = transactionDetailsVO.getAccountId();
                    toId = transactionDetailsVO.getToid();
                    notificationUrl=transactionDetailsVO.getNotificationUrl();
                    dbStatus = transactionDetailsVO.getStatus();

                    displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

                    commResponseVO.setDescription(description);
                    commResponseVO.setTransactionId(pspid);
                    transactionLogger.error("PaymentId----" + commResponseVO.getTransactionId());

                    if(functions.isValueNull(transactionDetailsVO.getCcnum())){
                        transactionDetailsVO.setCcnum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
                    }
                    transactionDetailsVO.setExpdate("");
                    merchantDetailsVO = merchantDAO.getMemberDetails(transactionDetailsVO.getToid());
                    transactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());



                    if(!functions.isValueNull(captureAmount))
                        captureAmount = transactionDetailsVO.getAmount();

                    captureAmount= Functions.roundOff(captureAmount);
                    transactionLogger.debug("amount-----"+captureAmount);

                    auditTrailVO.setActionExecutorId(toId);

                    transactionLogger.error("DB Status----"+ dbStatus);

                    if(PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus))
                    {

                        if ("OK".equals(bankStatus) || "CAPTURED".equals(bankStatus) || "APPROVED".equals(bankStatus) || "PROCESSED".equals(bankStatus))
                        {
                            sb.append("update transaction_common set ");
                            respStatus = "capturesuccess";
                            remark = bankStatus;
                            transactionDetailsVO.setBillingDesc(displayName);
                            commResponseVO.setDescriptor(displayName);
                            commResponseVO.setStatus("success");
                            commResponseVO.setRemark(remark);
                            sb.append(" captureamount='" + captureAmount + "'");
                            sb.append(", status='capturesuccess'");
                            entry.actionEntryForCommon(trackingId, captureAmount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.CAPTURE_SUCCESS.toString());
                            sb.append(" ,remark='" + remark + "',paymentid='" + pspid + "' where trackingid =" + trackingId + "");
                            con = Database.getConnection();
                            result = Database.executeUpdate(sb.toString(), con);
                            if (result != 1)
                            {
                                Database.rollback(con);
                                //Mail.sendAdminMail("Exception while Updating status", "Exception has occured while updating status for tracking id=" + trackingId + "Auth message=" + status);
                                asynchronousMailService.sendAdminMail("Exception while Updating status", "Exception has occured while updating status for tracking id=" + trackingId + "Auth message=" + status);
                            }

                        }
                        else
                        {
                            sb.append("update transaction_common set ");
                            respStatus = "authfailed";
                            remark = declineReason;
                            commResponseVO.setStatus("failed");
                            commResponseVO.setRemark(remark);
                            sb.append(" amount='" + captureAmount + "'");
                            sb.append(", status='authfailed'");
                            entry.actionEntryForCommon(trackingId, captureAmount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.AUTH_FAILED.toString());

                            transactionLogger.debug("Sql Query-----"+sb.toString());
                            sb.append(" ,remark='" + remark + "',paymentid='" + pspid + "' where trackingid =" + trackingId + "");
                            con = Database.getConnection();
                            result = Database.executeUpdate(sb.toString(), con);
                            if (result != 1)
                            {
                                Database.rollback(con);
                                //Mail.sendAdminMail("Exception while Updating status", "Exception has occured while updating status for tracking id=" + trackingId + "Auth message=" + status);
                                asynchronousMailService.sendAdminMail("Exception while Updating status", "Exception has occured while updating status for tracking id=" + trackingId + "Auth message=" + status);
                            }
                        }
                        if (functions.isValueNull(notificationUrl))
                        {
                            transactionLogger.error("inside sending notification---" + notificationUrl);
                            AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                            asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, respStatus, remark, "");
                        }
                    }else if(PZTransactionStatus.AUTH_FAILED.toString().equalsIgnoreCase(dbStatus)){

                        sb.append("update transaction_common set ");
                        respStatus = "authfailed";
                        remark = declineReason;
                        commResponseVO.setStatus("failed");
                        commResponseVO.setRemark(remark);
                        sb.append(" amount='" + captureAmount + "'");
                        sb.append(", status='authfailed'");
                        sb.append(" ,remark='" + remark + "',paymentid='" + pspid + "' where trackingid =" + trackingId + "");

                        transactionLogger.debug("Sql Query-----"+sb.toString());
                        con = Database.getConnection();
                         result = Database.executeUpdate(sb.toString(), con);

                        if (functions.isValueNull(notificationUrl))
                        {
                            transactionLogger.error("inside sending notification---" + notificationUrl);
                            AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                            asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, respStatus, remark, "");
                        }
                        if (result != 1)
                        {
                            Database.rollback(con);
                            //Mail.sendAdminMail("Exception while Updating status", "Exception has occured while updating status for tracking id=" + trackingId + "Auth message=" + status);
                            asynchronousMailService.sendAdminMail("Exception while Updating status", "Exception has occured while updating status for tracking id=" + trackingId + "Auth message=" + status);
                        }
                    }
                }

            }
            catch (Exception se)
            {
                transactionLogger.error("Exception in ApcoPayBackEndServlet---", se);
            }
            finally
            {
                Database.closeConnection(con);
            }
        }
    }
}