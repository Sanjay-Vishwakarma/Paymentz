package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommResponseVO;
import com.payment.globalgate.GlobalGateUtils;
import com.payment.statussync.StatusSyncDAO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.Map;

/**
 * Created by Jyoti on 20/8/2021.
 */
public class GlobalGateBackEndServlet extends PzServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(GlobalGateBackEndServlet.class.getName());

    public GlobalGateBackEndServlet()
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
        transactionLogger.error("------inside GlobalgateBackEndServlet-----");
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
        String amount = "";

        String remark = "";
        String accountId = "";

        String billingDesc="";
        String pspid = "";
        String declineReason = "";
        String notificationUrl="";
        String respStatus="";
        String message="";
        String dbStatus="";
        String expDate="";
        String expMonth="";
        String expYear="";
        String resultCode = "";
        String isService="";

        String tmpl_amt = "";
        String tmpl_currency = "";
        int result=0;

        Connection con = null;

        String xmlResponse = req.getParameter("params");
        transactionLogger.error("xmlResponse::::" + xmlResponse);
        if (xmlResponse != null)
        {
            try
            {
                Map<String, String> stringStringMap = GlobalGateUtils.readGlobalgateRedirectionXMLResponse(xmlResponse);

                trackingId = stringStringMap.get("ORef");
                resultCode = stringStringMap.get("Result");
                pspid = stringStringMap.get("pspid");

                captureAmount = stringStringMap.get("Value");
                declineReason = stringStringMap.get("ExtendedErr");

                transactionLogger.error("ORef----" +trackingId);
                transactionLogger.error("Result----" +resultCode);
                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);

                if(transactionDetailsVO!=null)
                {
                    description = transactionDetailsVO.getDescription();
                    accountId = transactionDetailsVO.getAccountId();
                    toId = transactionDetailsVO.getToid();
                    notificationUrl=transactionDetailsVO.getNotificationUrl();
                    dbStatus = transactionDetailsVO.getStatus();
                    tmpl_amt = transactionDetailsVO.getTemplateamount();
                    tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                    billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    amount = transactionDetailsVO.getAmount();

                    commResponseVO.setDescription(description);
                    commResponseVO.setTransactionId(pspid);
                    transactionLogger.error("PaymentId----" + commResponseVO.getTransactionId());

                    transactionLogger.error("tmpl_amount===========>"+tmpl_amt);
                    transactionLogger.error("tmpl_currency===========>"+tmpl_currency);

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
                    String resStatus = commResponseVO.getStatus();
                    String transactionId = commResponseVO.getTransactionId();
                    remark=commResponseVO.getRemark();
                    String authcode=commResponseVO.getAuthCode();
                    transactionLogger.error("resStatus===========>"+resStatus);
                    transactionLogger.error("transactionId===========>"+transactionId);
                    transactionLogger.error("remark===========>"+remark);
                    transactionLogger.error("authcode===========>"+authcode);
                    transactionLogger.error("billingDesc===========>"+billingDesc);
                    if(PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus))
                    {

                        StringBuffer dbBuffer = new StringBuffer();
                        if ("OK".equals(resultCode) || "CAPTURED".equals(resultCode) || "APPROVED".equals(resultCode))
                        {
                            if ("N".equalsIgnoreCase(isService) || "PA".equalsIgnoreCase(transactionDetailsVO.getTransactionType())) // AUTH
                            {
                                notificationUrl = transactionDetailsVO.getNotificationUrl();
                                transactionLogger.error("--- in Auth , authstarted---");
                                sb.append("update transaction_common set ");
                                respStatus = "Successful";
                                dbStatus="authsuccessful";
                                // remark = resultCode;
                                transactionDetailsVO.setBillingDesc(billingDesc);
                                commResponseVO.setDescriptor(billingDesc);
                                commResponseVO.setStatus("success");
                                commResponseVO.setRemark(remark);
                                commResponseVO.setDescription(remark);
                                //commResponseVO.setDescription("Transaction Successful");

                                commResponseVO.setAuthCode(authcode);
                                commResponseVO.setTmpl_Amount(tmpl_amt);
                                commResponseVO.setTmpl_Currency(tmpl_currency);
                                commResponseVO.setTransactionId(transactionId);
                                commResponseVO.setErrorCode(authcode);

                                sb.append("status='authsuccessful'");
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.AUTH_SUCCESS.toString());
                                sb.append(" ,remark='" + remark + "',paymentid='" + transactionId + "',successtimestamp='" + functions.getTimestamp()+ "',authorization_code='"+ authcode+ "' where trackingid =" + trackingId + "");
                                transactionLogger.error("querySuccess ===" + sb.toString());
                                con = Database.getConnection();
                                result = Database.executeUpdate(sb.toString(), con);
                                if (result != 1)
                                {
                                    Database.rollback(con);
                                    //Mail.sendAdminMail("Exception while Updating status", "Exception has occured while updating status for tracking id=" + trackingId + "Auth message=" + status);
                                    asynchronousMailService.sendAdminMail("Exception while Updating status", "Exception has occured while updating status for tracking id=" + trackingId + "Auth message=" + status);
                                }
                            }
                            else //SALE
                            {
                                notificationUrl = transactionDetailsVO.getNotificationUrl();
                                transactionLogger.error("--- in Sale , capturesuccess ---");
                                sb.append("update transaction_common set ");
                                respStatus = "Successful";
                                // remark = resultCode;
                                dbStatus="capturesuccess";
                                transactionDetailsVO.setBillingDesc(billingDesc);
                                commResponseVO.setDescriptor(billingDesc);
                                commResponseVO.setStatus("success");
                                commResponseVO.setRemark(remark);
                                //commResponseVO.setDescription("Transaction Successful");
                                commResponseVO.setDescription(remark);
                                commResponseVO.setAuthCode(authcode);
                                commResponseVO.setTmpl_Amount(tmpl_amt);
                                commResponseVO.setTmpl_Currency(tmpl_currency);
                                commResponseVO.setTransactionId(transactionId);
                                commResponseVO.setErrorCode(authcode);
                                sb.append(" captureamount='" + amount + "'");
                                sb.append(", status='capturesuccess'");
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.CAPTURE_SUCCESS.toString());
                                sb.append(" ,remark='" + remark + "',paymentid='" + transactionId + "',successtimestamp='" + functions.getTimestamp()+ "',authorization_code='"+ authcode + "' where trackingid =" + trackingId + "");
                                con = Database.getConnection();
                                result = Database.executeUpdate(sb.toString(), con);
                                if (result != 1)
                                {
                                    Database.rollback(con);
                                    //Mail.sendAdminMail("Exception while Updating status", "Exception has occured while updating status for tracking id=" + trackingId + "Auth message=" + status);
                                    asynchronousMailService.sendAdminMail("Exception while Updating status", "Exception has occured while updating status for tracking id=" + trackingId + "Auth message=" + status);
                                }
                            }
                        }
                        else if("fail".equalsIgnoreCase(resStatus))
                        {    notificationUrl = transactionDetailsVO.getNotificationUrl();
                            String confirmStatus = "N";
                            respStatus="failed";
                            status = "failed";
                            dbStatus = "authfailed";
                            billingDesc = "";
                            displayName = "";
                            dbBuffer.append("update transaction_common set paymentid='" + transactionId + "',failuretimestamp='" + functions.getTimestamp()+ "',status='authfailed',remark = '" + message + "'");

                            dbBuffer.append(" where trackingid = " + trackingId);
                            con = Database.getConnection();
                            Database.executeUpdate(dbBuffer.toString(), con);
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);


                        }
                        else
                        {
                            respStatus = "pending";
                            status = "pending";
                            message = "Transaction Pending";
                            commResponseVO.setStatus(status);
                            commResponseVO.setDescription(message);
                            commResponseVO.setRemark(message);
                        }

                        if (functions.isValueNull(notificationUrl))
                        {
                            transactionLogger.error("inside sending notification---" + notificationUrl);
                            AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                            asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, dbStatus, remark, "");
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
                transactionLogger.error("Exception in GlobalGtaeBackEndServlet---", se);
            }
            finally
            {
                Database.closeConnection(con);
            }
        }
    }
}