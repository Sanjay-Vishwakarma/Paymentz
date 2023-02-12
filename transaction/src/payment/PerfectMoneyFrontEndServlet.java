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
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.perfectmoney.PerfectMoneyPaymentGateway;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Created by Admin on 7/6/17.
 */
public class PerfectMoneyFrontEndServlet extends HttpServlet
{
    //private static PerfectMoneyLogger transactionLogger = new PerfectMoneyLogger(PerfectMoneyFrontEndServlet.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PerfectMoneyFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

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
        transactionLogger.error("inside PerfectMoneyFrontEndServlet for trackingid::::::::::"+req.getParameter("PAYMENT_ID"));
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
        Functions functions = new Functions();
        BlacklistManager blacklistManager=new BlacklistManager();
        BlacklistVO blacklistVO=new BlacklistVO();
        HttpSession session = req.getSession(true);
        Connection con = null;
        PreparedStatement p = null;
        ResultSet rs = null;
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
        String tmpl_Amount = "";
        String tmpl_currency = "";
        String email = "";
        String respStatus = "";
        String version="";
        String cardHolderName="";
        String phone="";
        String requestIp=Functions.getIpAddress(req);

        Enumeration enumeration = req.getParameterNames();
        while (enumeration.hasMoreElements())
        {
            String keyName = (String) enumeration.nextElement();
            transactionLogger.error(keyName + ":" + req.getParameter(keyName));
        }

        if (functions.isValueNull(req.getParameter("PAYMENT_ID")))
        {

            String trackingId = req.getParameter("PAYMENT_ID");
            String transactionStatus = req.getParameter("STATUS");
            String transactionId = req.getParameter("PAYMENT_BATCH_NUM");
            String payeeAccount = req.getParameter("PAYEE_ACCOUNT");
            String payerAccount = req.getParameter("PAYER_ACCOUNT");
            String timestampGMT = req.getParameter("TIMESTAMPGMT");
            String paymentAmount = req.getParameter("PAYMENT_AMOUNT");
            String paymentUnit = req.getParameter("PAYMENT_UNITS");

            transactionLogger.error("trackingId::::::" + trackingId);
            transactionLogger.error("transactionStatus::::::" + transactionStatus);

            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);

            if (transactionDetailsVO != null)
            {
                toId = transactionDetailsVO.getToid();
                accountId = transactionDetailsVO.getAccountId();
                amount = transactionDetailsVO.getAmount();
                description = transactionDetailsVO.getDescription();
                redirectUrl = transactionDetailsVO.getRedirectURL();
                paymodeid = transactionDetailsVO.getPaymodeId();
                cardtypeid = transactionDetailsVO.getCardTypeId();
                email = transactionDetailsVO.getEmailaddr();
                dbStatus = transactionDetailsVO.getStatus();
                version=transactionDetailsVO.getVersion();
                tmpl_Amount = transactionDetailsVO.getTemplateamount();
                tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                cardHolderName = transactionDetailsVO.getName();
                phone = transactionDetailsVO.getTelno();

                transactionLogger.error("dbStatus-----" + dbStatus);
                transactionLogger.error("tmpl_currency from db-----" + tmpl_currency);

                if (functions.isValueNull(transactionDetailsVO.getOrderDescription()))
                    orderDesc = transactionDetailsVO.getOrderDescription();
                currency = transactionDetailsVO.getCurrency();

                auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                auditTrailVO.setActionExecutorId(toId);

                transactionLogger.error("dbAmount----"+transactionDetailsVO.getAmount());

                transactionLogger.error("tmpl_Amount------"+tmpl_Amount);
                try
                {
                    merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                    transactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());
                    if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                    {
                        transactionLogger.error(" inside dbUpdate -----" + dbStatus);

                        con = Database.getConnection();
                        
                        if (functions.isValueNull(transactionStatus))
                        {
                            StringBuffer dbBuffer = new StringBuffer();

                            if ("success".equals(transactionStatus))
                            {
                                CommRequestVO commRequestVO = new CommRequestVO();
                                PerfectMoneyPaymentGateway perfectMoneyPaymentGateway = new PerfectMoneyPaymentGateway(accountId);

                                CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();
                                commAddressDetailsVO.setTime(transactionDetailsVO.getTransactionTime());
                                commRequestVO.setAddressDetailsVO(commAddressDetailsVO);

                                commResponseVO = (CommResponseVO)perfectMoneyPaymentGateway.processQuery(transactionDetailsVO.getTrackingid(),commRequestVO);

                                paymentAmount = commResponseVO.getAmount();
                                String statusMsg="";
                                if(functions.isValueNull(paymentAmount))
                                {
                                    Double dbAmount=Double.parseDouble(transactionDetailsVO.getAmount());
                                    Double resAmount=Double.parseDouble(paymentAmount);
                                    if (dbAmount!=resAmount && commResponseVO.getStatus().equalsIgnoreCase("success"))
                                    {
                                        commResponseVO.setStatus("failed");
                                        message = "Failed-IRA";
                                        statusMsg = "fraud";
                                        blacklistVO.setEmailAddress(email);
                                        blacklistVO.setActionExecutorId(toId);
                                        blacklistVO.setActionExecutorName("AcquirerBackEnd");
                                        blacklistVO.setRemark("IncorrectAmount");
                                        blacklistVO.setName(cardHolderName);
                                        blacklistVO.setPhone(phone);
                                        blacklistVO.setIpAddress(requestIp);
                                        blacklistManager.commonBlackListing(blacklistVO);
                                    }
                                }

                                if(functions.isValueNull(commResponseVO.getStatus()) && commResponseVO.getStatus().equalsIgnoreCase("success"))
                                {
                                    if(!transactionDetailsVO.getAmount().equalsIgnoreCase(paymentAmount))
                                    {
                                        tmpl_Amount = Functions.roundOff(String.valueOf((Double.parseDouble(transactionDetailsVO.getTemplateamount()) * Double.parseDouble(paymentAmount)) / Double.parseDouble(transactionDetailsVO.getAmount())));
                                    }
                                    else
                                    {
                                        tmpl_Amount=transactionDetailsVO.getTemplateamount();
                                    }

                                    status = "success";
                                    message = "Transaction Successful";
                                    respStatus = "Successful";
                                    billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

                                    commResponseVO.setDescription(message);
                                    commResponseVO.setStatus(status);
                                    commResponseVO.setDescriptor(billingDesc);
                                    commResponseVO.setTmpl_Amount(tmpl_Amount);
                                    commResponseVO.setTmpl_Currency(tmpl_currency);
                                    commResponseVO.setCurrency(currency);

                                    confirmStatus = "Y";
                                    dbStatus = "capturesuccess";
                                    dbBuffer.append("update transaction_common set captureamount='" + paymentAmount + "',paymentid='" + commResponseVO.getTransactionId() + "',status='capturesuccess'");
                                    entry.actionEntryForCommon(trackingId, paymentAmount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                    paymentManager.insertPerfectMoneyDetails(trackingId, status, trackingId, paymentAmount, commResponseVO.getCurrency(), commResponseVO.getAuthCode(), commResponseVO.getArn(), commResponseVO.getTransactionId(), commResponseVO.getResponseTime(), email, "");
                                    dbBuffer.append(" ,remark='" + message + "' where trackingid = " + trackingId);
                                    Database.executeUpdate(dbBuffer.toString(), con);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);

                                    if (functions.isValueNull(transactionDetailsVO.getNotificationUrl()))
                                    {
                                        transactionDetailsVO.setAmount(paymentAmount);
                                        transactionDetailsVO.setTemplateamount(tmpl_Amount);
                                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                                        asyncNotificationService.sendNotification(transactionDetailsVO,trackingId,dbStatus,message,"PM");
                                    }
                                }else if("fraud".equalsIgnoreCase(statusMsg))
                                {
                                    confirmStatus = "N";
                                    status = "fail";
                                    if(!functions.isValueNull(message))
                                        message = "Transaction Failed";
                                    commResponseVO.setStatus(status);
                                    commResponseVO.setDescription(message);
                                    commResponseVO.setRemark(message);
                                    dbStatus = "authfailed";
                                    respStatus = message;
                                    dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',captureamount='"+paymentAmount+"'");
                                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                                    paymentManager.insertPerfectMoneyDetails(trackingId, transactionStatus, trackingId, paymentAmount, paymentUnit, payeeAccount, payerAccount, transactionId, timestampGMT, email, "");

                                    dbBuffer.append(" ,remark='" + message + "' where trackingid = " + trackingId);
                                    Database.executeUpdate(dbBuffer.toString(), con);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);
                                }

                                else
                                {
                                    transactionLogger.error("Inside transaction status---Pending");
                                    confirmStatus = "P";
                                    dbStatus = "authstarted";
                                    message = "Deposit is pending";
                                    respStatus = "Pending";
                                }

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
                                dbStatus = "authfailed";
                                respStatus = message;
                                dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "'");
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                                paymentManager.insertPerfectMoneyDetails(trackingId, transactionStatus, trackingId, paymentAmount, paymentUnit, payeeAccount, payerAccount, transactionId, timestampGMT, email, "");

                                dbBuffer.append(" ,remark='" + message + "' where trackingid = " + trackingId);
                                Database.executeUpdate(dbBuffer.toString(), con);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);

                                //Sending Notification

                                if (functions.isValueNull(transactionDetailsVO.getNotificationUrl()))
                                {
                                    /*transactionDetailsVO.setAmount(paymentAmount);
                                    transactionDetailsVO.setTemplateamount(tmpl_Amount);*/
                                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                                    asyncNotificationService.sendNotification(transactionDetailsVO,trackingId,dbStatus,message,"PM");
                                }

                            }

                        }

                        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                        asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                        AsynchronousSmsService smsService = new AsynchronousSmsService();
                        smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
                    }
                    else
                    {
                        transactionLogger.debug(" inside else of dbUpdate -----");
                        if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus) || PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                        {
                            transactionLogger.debug(" inside else of dbUpdate -----" + dbStatus);
                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                            status = "success";
                            message = "Transaction Successful";
                            respStatus = "Successful";

                            commResponseVO.setDescription(message);
                            commResponseVO.setStatus(status);
                            commResponseVO.setRemark(message);
                            commResponseVO.setDescriptor(billingDesc);
                            commResponseVO.setAmount(amount);
                            paymentManager.updateTransactionForCommon(commResponseVO, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, trackingId, auditTrailVO, "transaction_common", "", transactionId, null, commResponseVO.getRemark());
                        }
                        /*else
                        {
                            status = "fail";
                            message = "Transaction Declined";
                            respStatus = "Failed";
                        }*/

                        //Sending Notification

                        if (functions.isValueNull(transactionDetailsVO.getNotificationUrl()))
                        {
                            transactionDetailsVO.setAmount(amount);
                            transactionDetailsVO.setTemplateamount(tmpl_Amount);
                            AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                            asyncNotificationService.sendNotification(transactionDetailsVO,trackingId,dbStatus,message,"PM");
                        }

                    }

                    transactionLogger.debug(" billingDesc-----" + billingDesc);
                    transactionLogger.error("Message-----" + message);

                    String custId = "";
                    String custBankId = "";
                    HashMap hashMap = paymentManager.getExtnDetailsForPM(trackingId);
                    if (functions.isValueNull((String) hashMap.get("customerId")))
                    {
                        custId = (String) hashMap.get("customerId");
                    }
                    if (functions.isValueNull((String) hashMap.get("customerBankId")))
                    {
                        custBankId = (String) hashMap.get("customerBankId");
                    }

                    merchantDetailsVO = merchantDAO.getMemberDetails(toId);

                    autoRedirect = merchantDetailsVO.getAutoRedirect();
                    logoName = merchantDetailsVO.getLogoName();
                    partnerName = merchantDetailsVO.getPartnerName();

                    commonValidatorVO.setTrackingid(trackingId);
                    genericTransDetailsVO.setOrderId(description);
                    genericTransDetailsVO.setAmount(amount);
                    genericTransDetailsVO.setCurrency(currency);
                    genericTransDetailsVO.setOrderDesc(orderDesc);
                    genericTransDetailsVO.setRedirectUrl(redirectUrl);
                    commonValidatorVO.setLogoName(logoName);
                    commonValidatorVO.setPartnerName(partnerName);
                    commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                    addressDetailsVO.setTmpl_amount(tmpl_Amount);
                    addressDetailsVO.setTmpl_currency(tmpl_currency);
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    commonValidatorVO.setPaymentType(transactionDetailsVO.getPaymodeId());
                    commonValidatorVO.setCardType(transactionDetailsVO.getCardTypeId());
                    if (functions.isValueNull(email))
                        addressDetailsVO.setEmail(email);
                    else
                        addressDetailsVO.setEmail(transactionDetailsVO.getEmailaddr());
                    commonValidatorVO.setPaymentType(paymodeid);
                    commonValidatorVO.setCardType(cardtypeid);

                    commonValidatorVO.setCustomerId(custId);
                    commonValidatorVO.setCustomerBankId(custBankId);

                    commonValidatorVO.setAddressDetailsVO(addressDetailsVO);

                    if ("Y".equalsIgnoreCase(autoRedirect))
                    {
                        TransactionUtility transactionUtility = new TransactionUtility();
                        transactionLogger.error("respStatus in Auto Redirect Y---" + respStatus);
                        transactionUtility.doAutoRedirect(commonValidatorVO, res, respStatus, billingDesc);
                    }
                    else
                    {
                        transactionLogger.error("respStatus in Auto Redirect N---" + respStatus);
                        session.setAttribute("ctoken", ctoken);
                        req.setAttribute("transDetail", commonValidatorVO);
                        req.setAttribute("responceStatus", respStatus);
                        req.setAttribute("displayName", billingDesc);
                        String confirmationPage = "";
                        if(functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                            confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                        else
                            confirmationPage = "/confirmationpage.jsp?ctoken=";
                        RequestDispatcher rd = req.getRequestDispatcher(confirmationPage + ctoken);
                        rd.forward(req, res);
                    }
                }
                catch (SystemError se)
                {
                    transactionLogger.error("SystemError::::::", se);
                    PZExceptionHandler.raiseAndHandleDBViolationException("PerfectMoneyFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toId, null);
                }
                catch (PZDBViolationException tve)
                {
                    transactionLogger.error("PZDBViolationException:::::", tve);
                }
                catch (Exception tve)
                {
                    transactionLogger.error("Exception:::::", tve);
                }
                finally
                {
                    Database.closeConnection(con);
                }
            }
        }
    }
}