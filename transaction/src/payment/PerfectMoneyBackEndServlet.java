package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.BlacklistManager;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.BlacklistVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.perfectmoney.PerfectMoneyUtils;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.ResourceBundle;

/**
 * Created by Trupti on 11/28/2017.
 */
public class PerfectMoneyBackEndServlet extends PzServlet
{
    //private static PerfectMoneyLogger transactionLogger = new PerfectMoneyLogger(PerfectMoneyBackEndServlet.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PerfectMoneyBackEndServlet.class.getName());

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
        transactionLogger.error("Inside PerfectMoneyBackEndServlet TrackingID----"+req.getParameter("PAYMENT_ID"));
        ResourceBundle RB=LoadProperties.getProperty("com.directi.pg.perfectmoney");
        TransactionManager transactionManager = new TransactionManager();
        ActionEntry entry = new ActionEntry();
        CommResponseVO commResponseVO = new CommResponseVO();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        MerchantDetailsVO merchantDetailsVO = null;
        MerchantDAO merchantDAO = new MerchantDAO();
        PaymentManager paymentManager = new PaymentManager();
        BlacklistManager blacklistManager=new BlacklistManager();
        BlacklistVO blacklistVO=new BlacklistVO();
        Functions functions = new Functions();
        HttpSession session = req.getSession(true);
        Connection con = null;
        String toId = "";
        String accountId = "";
        String status = "";
        String amount = "";
        String description = "";
        String redirectUrl = "";
        String clKey = "";
        String checksumNew = "";
        String autoRedirect = "";
        String logoName = "";
        String partnerName = "";
        String confirmStatus = "";
        String token = "";
        String orderDesc = "";
        String currency = "";
        String errorCode = "";
        String message = "";
        String billingDesc = "";
        String transType = "sale";
        String dbStatus = "";
        String paymodeid = "";
        String cardtypeid = "";
        String tmpl_amt = "";
        String tmpl_currency = "";
        String email = "";
        String transactionStatus="";
        String cardHolderName="";
        String phone="";
        try
        {

            /*Enumeration enumeration = req.getParameterNames();
            while (enumeration.hasMoreElements())
            {
                String keyName = (String) enumeration.nextElement();
                transactionLogger.error(keyName + ":" + req.getParameter(keyName));
            }*/
            if(functions.isValueNull(req.getParameter("PAYMENT_ID")))
            {
                String trackingId = req.getParameter("PAYMENT_ID");
                String payeeAccount = req.getParameter("PAYEE_ACCOUNT");
                String paymentAmount = req.getParameter("PAYMENT_AMOUNT");
                String paymentUnit = req.getParameter("PAYMENT_UNITS");
                String transactionId = req.getParameter("PAYMENT_BATCH_NUM");
                String payerAccount = req.getParameter("PAYER_ACCOUNT");
                String timestampGMT = req.getParameter("TIMESTAMPGMT");
                String V2_HASH = req.getParameter("V2_HASH");

                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                accountId = transactionDetailsVO.getAccountId();

                transactionLogger.error("trackingId::::::" + trackingId);
                transactionLogger.error("payeeAccount::::::" + payeeAccount);
                transactionLogger.error("paymentAmount::::::" + paymentAmount);
                transactionLogger.error("paymentUnit::::::" + paymentUnit);
                transactionLogger.error("transactionId::::::" + transactionId);
                transactionLogger.error("payerAccount::::::" + payerAccount);
                transactionLogger.error("timestampGMT::::::" + timestampGMT);
                transactionLogger.error("V2_HASH::::::" + V2_HASH);

                //String alternetPhase = RB.getString("alternetPhase");
                String alternetPhase = GatewayAccountService.getGatewayAccount(accountId).getCHARGEBACK_FTP_PATH();
                transactionLogger.debug("alternetPhase in PerfectMoneyBackEndServlet ---"+alternetPhase);

                String alternetPhase_Hash = PerfectMoneyUtils.getMD5HashVal(alternetPhase);

                String concat = trackingId + ":" + payeeAccount + ":" + paymentAmount + ":" + paymentUnit + ":" + transactionId + ":" + payerAccount + ":" + alternetPhase_Hash.toUpperCase() + ":" + timestampGMT;
                String md5 = PerfectMoneyUtils.getMD5HashVal(concat);

                transactionLogger.error("MD5_HASH------" + md5.toUpperCase());
                transactionLogger.error("V2_HASH------" + V2_HASH);

                if (md5.toUpperCase().equals(V2_HASH))
                {
                    transactionStatus = "success";

                    if (transactionDetailsVO != null)
                    {
                        toId = transactionDetailsVO.getToid();
                        amount = transactionDetailsVO.getAmount();
                        description = transactionDetailsVO.getDescription();
                        redirectUrl = transactionDetailsVO.getRedirectURL();
                        paymodeid = transactionDetailsVO.getPaymodeId();
                        cardtypeid = transactionDetailsVO.getCardTypeId();
                        email = transactionDetailsVO.getEmailaddr();
                        dbStatus = transactionDetailsVO.getStatus();
                        cardHolderName = transactionDetailsVO.getName();
                        phone = transactionDetailsVO.getTelno();
                        merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                        transactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());

                        transactionLogger.debug("dbStatus------" + dbStatus);

                        if (functions.isValueNull(transactionDetailsVO.getOrderDescription()))
                            orderDesc = transactionDetailsVO.getOrderDescription();
                        currency = transactionDetailsVO.getCurrency();

                        transactionLogger.error("dbAmount----"+transactionDetailsVO.getAmount());
                        transactionLogger.error("paymentAmount----"+paymentAmount);
                        String tmpl_Amount="";
                        if(!transactionDetailsVO.getAmount().equalsIgnoreCase(paymentAmount))
                        {
                            tmpl_Amount = Functions.roundOff(String.valueOf((Double.parseDouble(transactionDetailsVO.getTemplateamount()) * Double.parseDouble(paymentAmount)) / Double.parseDouble(transactionDetailsVO.getAmount())));
                        }else {
                            tmpl_Amount=transactionDetailsVO.getTemplateamount();
                        }

                        transactionLogger.error("tmpl_Amount------"+tmpl_Amount);
                        String statusMsg="";
                        if(functions.isValueNull(paymentAmount))
                        {
                            Double dbAmount=Double.parseDouble(transactionDetailsVO.getAmount());
                            Double resAmount=Double.parseDouble(paymentAmount);
                            if (dbAmount!=resAmount && "success".equals(transactionStatus))
                            {
                                transactionStatus = "failed";
                                message = "Failed-IRA";
                                statusMsg = "fraud";
                                blacklistVO.setEmailAddress(email);
                                blacklistVO.setActionExecutorId(toId);
                                blacklistVO.setActionExecutorName("AcquirerBackEnd");
                                blacklistVO.setRemark("IncorrectAmount");
                                blacklistVO.setName(cardHolderName);
                                blacklistVO.setPhone(phone);
                                blacklistManager.commonBlackListing(blacklistVO);
                            }
                        }
                        if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                        {
                            if (functions.isValueNull(transactionStatus))
                            {
                                auditTrailVO.setActionExecutorName("AcquirerBackEnd");
                                auditTrailVO.setActionExecutorId(toId);
                                StringBuffer dbBuffer = new StringBuffer();
                                con = Database.getConnection();

                                if ("success".equals(transactionStatus))
                                {
                                    status = "success";
                                    message = "Transaction Successful";
                                    billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

                                    commResponseVO.setDescription(message);
                                    commResponseVO.setStatus(status);
                                    commResponseVO.setRemark(message);
                                    commResponseVO.setDescriptor(billingDesc);
                                    commResponseVO.setCurrency(paymentUnit);
                                    commResponseVO.setTmpl_Amount(tmpl_Amount);
                                    commResponseVO.setTmpl_Currency(transactionDetailsVO.getTemplatecurrency());

                                    confirmStatus = "Y";
                                    dbStatus = "authsuccessful";
                                    dbBuffer.append("update transaction_common set templateamount='"+tmpl_Amount+"', captureamount='" + paymentAmount + "',paymentid='" + transactionId + "',status='authsuccessful',notificationCount=1");
                                    entry.actionEntryForCommon(trackingId, paymentAmount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                    paymentManager.insertPerfectMoneyDetails(trackingId, transactionStatus, trackingId, paymentAmount, paymentUnit, payeeAccount, payerAccount, transactionId, timestampGMT, email, "");
                                }
                                else
                                {
                                    confirmStatus = "N";
                                    status = "fail";
                                    if(!functions.isValueNull(message))
                                        message = "Transaction Failed";
                                    commResponseVO.setStatus(status);
                                    commResponseVO.setDescription(message);
                                    commResponseVO.setRemark(message);
                                    commResponseVO.setCurrency(paymentUnit);
                                    commResponseVO.setTmpl_Amount(tmpl_Amount);
                                    commResponseVO.setTmpl_Currency(transactionDetailsVO.getTemplatecurrency());
                                    dbStatus = "authfailed";
                                    dbBuffer.append("update transaction_common set templateamount='"+tmpl_Amount+"',status='authfailed',paymentid='" + transactionId + "',notificationCount=1");
                                    if("fraud".equalsIgnoreCase(statusMsg))
                                        dbBuffer.append(" ,captureamount='"+paymentAmount+"'");
                                    entry.actionEntryForCommon(trackingId, paymentAmount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                                    paymentManager.insertPerfectMoneyDetails(trackingId, transactionStatus, trackingId, paymentAmount, paymentUnit, payeeAccount, payerAccount, transactionId, timestampGMT, email, "");
                                }
                                dbBuffer.append(" ,remark='" + message + "' where trackingid = " + trackingId);
                                Database.executeUpdate(dbBuffer.toString(), con);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);

                                //Sending Async Notification

                                if (functions.isValueNull(transactionDetailsVO.getNotificationUrl()))
                                {
                                    transactionDetailsVO.setAmount(paymentAmount);
                                    transactionDetailsVO.setTemplateamount(tmpl_Amount);
                                    transactionDetailsVO.setTemplatecurrency(transactionDetailsVO.getTemplatecurrency());
                                    transactionLogger.error("Before sending notification for TrackingID----"+trackingId);
                                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                                    asyncNotificationService.sendNotification(transactionDetailsVO,trackingId,status,message,"PM");
                                    transactionLogger.error("After sending notification for TrackingID----"+trackingId);
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException in PerfectMoneyBackEndServlet---",e);
        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("PZTechnicalViolationException in PerfectMoneyBackEndServlet---", e);
        }
        catch (SystemError e)
        {
            transactionLogger.error("SystemError in PerfectMoneyBackEndServlet---", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
}
