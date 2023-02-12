package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.jeton.JetonPaymentGateway;
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
import java.util.Enumeration;

/**
 * Created by Admin on 7/6/17.
 */
public class JetonFrontEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(JetonFrontEndServlet.class.getName());
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
        transactionLogger.error("-----inside JetonFrontEndServlet-----");
        TransactionManager transactionManager = new TransactionManager();
        ActionEntry entry = new ActionEntry();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO= new GenericCardDetailsVO();
        TransactionUtility transactionUtility = new TransactionUtility();
        MerchantDetailsVO merchantDetailsVO = null;
        MerchantDAO merchantDAO = new MerchantDAO();
        PaymentManager paymentManager = new PaymentManager();
        Functions functions = new Functions();
        HttpSession session = req.getSession(true);
        Connection con = null;
        String toId = "";
        String accountId = "";
        String status = "";
        String amount = "";
        String description = "";
        String redirectUrl = "";
        String autoRedirect = "";
        String logoName = "";
        String partnerName = "";
        String confirmStatus = "";
        String orderDesc = "";
        String currency = "";
        String message = "";
        String billingDesc = "";
        String transType = "sale";
        String dbStatus = "";
        String tmpl_amount = "";
        String tmpl_currency = "";
        String trackingId = "";
        String paymodeid = "";
        String cardtypeid= "";
        String custEmail = "";
        String customerid="";
        String transactionStatus="";
        String version="";
        String notificationUrl="";
        String terminalid="";
        String updatedStatus="";

        Enumeration enumeration = req.getParameterNames();
        while (enumeration.hasMoreElements())
        {
            String keyName = (String) enumeration.nextElement();
            transactionLogger.error(keyName + ":" + req.getParameter(keyName));
        }

        if (functions.isValueNull(req.getParameter("trackingId")))
        {
            trackingId = req.getParameter("trackingId");
            String transactionId = req.getParameter("paymentId");
            String customerNumber = req.getParameter("customerNumber");


            transactionLogger.error("trackingId::::::" + trackingId);
            transactionLogger.error("transactionId::::::" + transactionId);
            try
            {

                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);

                if (transactionDetailsVO != null)
                {

                    toId = transactionDetailsVO.getToid();
                    accountId = transactionDetailsVO.getAccountId();
                    amount = transactionDetailsVO.getAmount();
                    description = transactionDetailsVO.getDescription();
                    redirectUrl = transactionDetailsVO.getRedirectURL();
                    tmpl_amount = transactionDetailsVO.getTemplateamount();
                    tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                    dbStatus = transactionDetailsVO.getStatus();
                    paymodeid = transactionDetailsVO.getPaymodeId();
                    cardtypeid = transactionDetailsVO.getCardTypeId();
                    custEmail = transactionDetailsVO.getEmailaddr();
                    customerid = transactionDetailsVO.getCustomerId();
                    version=transactionDetailsVO.getVersion();
                    notificationUrl=transactionDetailsVO.getNotificationUrl();
                    terminalid=transactionDetailsVO.getTerminalId();

                    merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                    if (merchantDetailsVO != null)
                    {
                        autoRedirect = merchantDetailsVO.getAutoRedirect();
                        logoName = merchantDetailsVO.getLogoName();
                        partnerName = merchantDetailsVO.getPartnerName();
                    }
                    auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                    auditTrailVO.setActionExecutorId(toId);

                    if (functions.isValueNull(transactionDetailsVO.getOrderDescription()))
                        orderDesc = transactionDetailsVO.getOrderDescription();
                    currency = transactionDetailsVO.getCurrency();
                    transactionLogger.error("dbStatus-----" + dbStatus);

                    CommResponseVO commResponseVO = null;
                    if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                    {
                        StringBuffer dbBuffer = new StringBuffer();
                        JetonPaymentGateway gateway = new JetonPaymentGateway(accountId);
                        CommRequestVO commRequestVO = new CommRequestVO();
                        CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
                        commTransactionDetailsVO.setPrevTransactionStatus(dbStatus);
                        commTransactionDetailsVO.setPreviousTransactionId(transactionId);
                        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);

                        commResponseVO = (CommResponseVO) gateway.processQuery(trackingId, commRequestVO);
                        if (commResponseVO!=null)
                        {
                            transactionStatus = commResponseVO.getStatus();
                            transactionId = commResponseVO.getTransactionId();
                            message = commResponseVO.getRemark();
                            if(!functions.isValueNull(commResponseVO.getCurrency()))
                                commResponseVO.setCurrency(currency);
                            if(!functions.isValueNull(commResponseVO.getTmpl_Amount()))
                                commResponseVO.setTmpl_Amount(tmpl_amount);
                            if(!functions.isValueNull(commResponseVO.getTmpl_Currency()))
                                commResponseVO.setTmpl_Currency(tmpl_currency);
                            if(functions.isValueNull(commResponseVO.getAmount()))
                                amount = commResponseVO.getAmount();

                            if ("success".equalsIgnoreCase(transactionStatus))
                            {
                                status = "Successful";
                                billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

                                commResponseVO.setDescription(message);
                                commResponseVO.setStatus(status);
                                commResponseVO.setRemark(message);
                                commResponseVO.setDescriptor(billingDesc);

                                confirmStatus = "Y";
                                dbStatus = "capturesuccess";
                                updatedStatus="capturesuccess";
                                dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',status='capturesuccess'");
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                paymentManager.insertJetonDetails(trackingId, transactionStatus, transactionId, customerNumber);
                            }
                            else
                            {
                                confirmStatus = "N";
                                status = "fail";
                                commResponseVO.setStatus(status);
                                commResponseVO.setDescription(message);
                                commResponseVO.setRemark(message);
                                dbStatus = "authfailed";
                                updatedStatus="authfailed";
                                dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "'");
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                                paymentManager.insertJetonDetails(trackingId, transactionStatus, transactionId, customerNumber);
                            }

                            dbBuffer.append(" ,remark='" + message + "' where trackingid = " + trackingId);
                            con = Database.getConnection();
                            Database.executeUpdate(dbBuffer.toString(), con);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);

                            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                            asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                            AsynchronousSmsService smsService = new AsynchronousSmsService();
                            smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
                        }else {
                            transactionLogger.debug("-----inside pending-----");
                            status = "pending";
                            message = "FE:Transaction is pending";
                            updatedStatus="pending";
                        }

                    }
                    else
                    {
                        if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                        {
                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                            status = "success";
                            message = "Transaction Successful";
                            updatedStatus=PZTransactionStatus.CAPTURE_SUCCESS.toString();

                        }else if(PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus)){
                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                            status = "success";
                            message = "Transaction Successful";
                            updatedStatus=PZTransactionStatus.AUTH_SUCCESS.toString();
                        }else if(PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus)){
                            status = "fail";
                            message = "Transaction Failed";
                            updatedStatus=PZTransactionStatus.AUTH_FAILED.toString();

                        }
                        else
                        {
                            status = "fail";
                            message = "Transaction Declined";
                            updatedStatus=PZTransactionStatus.FAILED.toString();

                        }
                    }

                    if (status.equalsIgnoreCase("success"))
                    {
                        status = "successful";
                    }
                    genericTransDetailsVO.setOrderId(description);
                    genericTransDetailsVO.setAmount(amount);
                    genericTransDetailsVO.setCurrency(currency);
                    genericTransDetailsVO.setOrderDesc(orderDesc);
                    genericTransDetailsVO.setRedirectUrl(redirectUrl);
                    genericTransDetailsVO.setNotificationUrl(notificationUrl);
                    genericTransDetailsVO.setBillingDiscriptor(billingDesc);

                    addressDetailsVO.setEmail(custEmail);
                    addressDetailsVO.setTmpl_amount(tmpl_amount);
                    addressDetailsVO.setTmpl_currency(tmpl_currency);
                    commonValidatorVO.setLogoName(logoName);
                    commonValidatorVO.setPartnerName(partnerName);
                    commonValidatorVO.setPaymentType(paymodeid);
                    commonValidatorVO.setCardType(cardtypeid);
                    commonValidatorVO.setTrackingid(trackingId);

                    commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                    commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                    commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    commonValidatorVO.setCustomerId(customerid);
                    commonValidatorVO.setTerminalId(terminalid);

                    if (functions.isValueNull(notificationUrl))
                    {
                        transactionLogger.error("inside sending notification---" + notificationUrl);
                        TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                        asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, updatedStatus, message,"");
                    }

                    if ("Y".equalsIgnoreCase(autoRedirect))
                    {
                        transactionUtility.doAutoRedirect(commonValidatorVO, res, status, billingDesc);
                    }
                    else
                    {
                        session.setAttribute("ctoken", ctoken);
                        req.setAttribute("transDetail", commonValidatorVO);
                        req.setAttribute("responceStatus", status);
                        req.setAttribute("remark", message);
                        req.setAttribute("displayName", GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                        String confirmationPage = "";

                        if (functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                            confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                        else
                            confirmationPage = "/confirmationpage.jsp?ctoken=";
                        RequestDispatcher rd = req.getRequestDispatcher(confirmationPage + ctoken);
                        rd.forward(req, res);
                    }
                }
            }
            catch(SystemError se)
            {
                transactionLogger.error("SystemError::::::", se);
                PZExceptionHandler.raiseAndHandleDBViolationException("JetonFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toId, null);
            }
            catch (PZDBViolationException tve)
            {
                transactionLogger.error("PZDBViolationException:::::", tve);
            }catch (PZConstraintViolationException e){
                PZExceptionHandler.raiseAndHandleDBViolationException("JetonFrontEndServlet.java", "doService()", null, "Transaction", null, null, null, e.getMessage(), e.getCause(), toId, null);
            }catch (PZTechnicalViolationException e){
                PZExceptionHandler.raiseAndHandleDBViolationException("JetonFrontEndServlet.java", "doService()", null, "Transaction", null, null, null, e.getMessage(), e.getCause(), toId, null);
            }
            finally
            {
                Database.closeConnection(con);
            }
        }
    }
}
