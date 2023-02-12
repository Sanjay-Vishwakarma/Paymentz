package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.clearsettle.ClearSettleVoucherPaymentGateway;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;

public class ClearSettleVoucherFrontEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(ClearSettleVoucherFrontEndServlet.class.getName());
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
        transactionLogger.error("-------Enter in doService of ClearSettleVoucherFrontEndServlet-------" + req.getHeader("X-Forwarded-For"));

        TransactionManager transactionManager = new TransactionManager();
        ActionEntry entry = new ActionEntry();
        CommResponseVO commResponseVO = null;
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO= new GenericCardDetailsVO();
        TransactionUtility transactionUtility = new TransactionUtility();
        MerchantDetailsVO merchantDetailsVO = null;
        MerchantDAO merchantDAO = new MerchantDAO();

        HttpSession session = req.getSession(true);

        Connection con = null;
        PreparedStatement p = null;
        ResultSet rs = null;

        String toId = "";
        String isService = "";
        String accountId = "";
        String dbStatus = "";
        String status = "";
        String statusDescription = "";
        String amount = "";

        String description = "";
        String redirectUrl = "";
        String clkey = "";
        String checksumNew = "";
        String autoRedirect = "";
        String logoName = "";
        String partnerName = "";
        String confirmStatus = "";

        String token = "";
        String orderDesc = "";
        String currency = "";
        String billingDesc = "";
        String tmpl_amt = "";
        String tmpl_currency = "";
        String paymodeid = "";
        String cardtypeid = "";
        String email = "";
        String transactionId="";
        String respStatus="";
        String notificationUrl="";
        String updatedStatus="";
        String version="";
        String firstName="";
        String lastName="";


        Enumeration enumeration = req.getParameterNames();
        while (enumeration.hasMoreElements())
        {
            String paraName = (String) enumeration.nextElement();
            String value = req.getParameter(paraName);
            transactionLogger.error(paraName + ":" + value);
        }

        Functions functions = new Functions();
        String transactionStatus = req.getParameter("status");
        if (functions.isValueNull(transactionStatus))
        {
            if ("WAITING".equalsIgnoreCase(transactionStatus))
            {
                PrintWriter pw = res.getWriter();
                pw.println(new String(Base64.decode(req.getParameter("returnForm"))));
            }
            else if ("APPROVED".equalsIgnoreCase(transactionStatus) || "DECLINED".equalsIgnoreCase(transactionStatus))
            {
                String trackingId = req.getParameter("referenceNo");
                String message = req.getParameter("message");
                    try
                    {
                        TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                        toId = transactionDetailsVO.getToid();
                        accountId = transactionDetailsVO.getAccountId();
                        amount = transactionDetailsVO.getAmount();
                        tmpl_amt = transactionDetailsVO.getTemplateamount();
                        tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                        description = transactionDetailsVO.getDescription();
                        redirectUrl = transactionDetailsVO.getRedirectURL();
                        if (functions.isValueNull(transactionDetailsVO.getOrderDescription()))
                            orderDesc = transactionDetailsVO.getOrderDescription();
                        currency = transactionDetailsVO.getCurrency();
                        paymodeid = transactionDetailsVO.getPaymodeId();
                        cardtypeid = transactionDetailsVO.getCardTypeId();
                        email = transactionDetailsVO.getEmailaddr();
                        dbStatus = transactionDetailsVO.getStatus();
                        notificationUrl=transactionDetailsVO.getNotificationUrl();
                        version=transactionDetailsVO.getVersion();
                        firstName=transactionDetailsVO.getFirstName();
                        lastName=transactionDetailsVO.getLastName();

                        ClearSettleVoucherPaymentGateway clearSettleVoucherPaymentGateway = new ClearSettleVoucherPaymentGateway(accountId);
                        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();


                        transactionLogger.debug("dbStatus-------" + dbStatus);
                        CommRequestVO commRequestVO = new CommRequestVO();
                        CommTransactionDetailsVO commTransactionDetailsVO= new CommTransactionDetailsVO();
                        commTransactionDetailsVO.setOrderId(trackingId);
                        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                         commResponseVO = (CommResponseVO) clearSettleVoucherPaymentGateway.processInquiry(commRequestVO);
                        transactionStatus = commResponseVO.getStatus();
                        transactionId = commResponseVO.getTransactionId();


                        merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                        if (merchantDetailsVO != null)
                        {
                            autoRedirect = merchantDetailsVO.getAutoRedirect();
                            logoName = merchantDetailsVO.getLogoName();
                            partnerName = merchantDetailsVO.getPartnerName();
                        }

                        if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                        {

                            transactionLogger.debug("transactionStatus-----"+transactionStatus);
                            transactionLogger.debug("dbStatus-----"+dbStatus);
                            if (functions.isValueNull(transactionStatus))
                            {
                                auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                                auditTrailVO.setActionExecutorId(toId);
                                StringBuffer dbBuffer = new StringBuffer();


                                commResponseVO.setTransactionId(transactionId);
                                commResponseVO.setTransactionStatus(transactionStatus);

                                if ("success".equals(transactionStatus))
                                {
                                    status = "success";
                                    message = commResponseVO.getRemark();
                                    respStatus = "Successful";
                                    billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                                    dbStatus = "capturesuccess";
                                    updatedStatus=PZTransactionStatus.CAPTURE_SUCCESS.toString();
                                    dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',status='capturesuccess'");
                                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                }
                                else
                                {
                                    status = "fail";
                                    respStatus = "Failed";
                                    message = commResponseVO.getRemark();
                                    dbStatus = "authfailed";
                                    updatedStatus=PZTransactionStatus.AUTH_FAILED.toString();
                                    dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "'");
                                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                                }
                                dbBuffer.append(" ,remark='" + message + "' where trackingid = " + trackingId);
                                transactionLogger.debug("Update Query---" + dbBuffer);
                                con = Database.getConnection();
                                Database.executeUpdate(dbBuffer.toString(), con);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);
                                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
                                AsynchronousSmsService smsService = new AsynchronousSmsService();
                                smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
                            }

                        }
                        else
                        {
                            if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                            {
                                billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                                status = "success";
                                message = "Transaction Successful";
                                respStatus = "Successful";
                                updatedStatus=PZTransactionStatus.CAPTURE_SUCCESS.toString();
                            }else if(PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus)){
                                status = "fail";
                                message = "Transaction Failed";
                                respStatus = "Failed";
                                updatedStatus=PZTransactionStatus.AUTH_FAILED.toString();
                            }
                            else
                            {
                                status = "fail";
                                message = "Transaction Declined";
                                respStatus = "Failed";
                                updatedStatus=PZTransactionStatus.FAILED.toString();
                            }
                        }
                        commonValidatorVO.setTrackingid(trackingId);
                        genericTransDetailsVO.setOrderId(description);
                        genericTransDetailsVO.setAmount(amount);
                        genericTransDetailsVO.setCurrency(currency);
                        genericTransDetailsVO.setOrderDesc(orderDesc);
                        genericTransDetailsVO.setRedirectUrl(redirectUrl);
                        genericTransDetailsVO.setBillingDiscriptor(billingDesc);
                        genericTransDetailsVO.setNotificationUrl(notificationUrl);
                        commonValidatorVO.setLogoName(logoName);
                        commonValidatorVO.setPartnerName(partnerName);
                        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                        addressDetailsVO.setTmpl_amount(tmpl_amt);
                        addressDetailsVO.setFirstname(firstName);
                        addressDetailsVO.setLastname(lastName);
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
                        commonValidatorVO.setCustomerId(transactionDetailsVO.getCustomerId());
                        commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                        commonValidatorVO.setCardDetailsVO(cardDetailsVO);


                        transactionLogger.error("updatedStatus-----"+updatedStatus);
                        transactionLogger.error("message-----"+message);
                        transactionLogger.error("respStatus in Y---" + respStatus);
                        if (functions.isValueNull(notificationUrl))
                        {
                            transactionLogger.error("inside sending notification---" + notificationUrl);
                            TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                            AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                            asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, updatedStatus, message,"");
                        }
                        if ("Y".equalsIgnoreCase(autoRedirect))
                        {

                            transactionLogger.error("respStatus in Y---" + respStatus);
                            transactionUtility.doAutoRedirect(commonValidatorVO, res, respStatus, billingDesc);
                        }
                        else
                        {
                            session.setAttribute("ctoken", ctoken);
                            req.setAttribute("transDetail", commonValidatorVO);
                            req.setAttribute("responceStatus", respStatus);
                            req.setAttribute("displayName", billingDesc);
                            String confirmationPage = "";
                            transactionLogger.debug("version---"+version);
                            if (functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                                confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                            else
                                confirmationPage = "/confirmationpage.jsp?ctoken=";

                            RequestDispatcher rd = req.getRequestDispatcher(confirmationPage + ctoken);
                            rd.forward(req, res);
                        }

                    }
                    catch (PZTechnicalViolationException e)
                    {
                        transactionLogger.error("PZTechnicalViolationException:::::", e);
                    }
                    catch (PZDBViolationException tve)
                    {
                        transactionLogger.error("PZDBViolationException:::::", tve);
                    }
                    catch (PZConstraintViolationException e)
                    {
                        transactionLogger.error("PZConstraintViolationException-----", e);
                    }
                    catch (SystemError e)
                    {
                        transactionLogger.error("SystemError:::::", e);
                        PZExceptionHandler.raiseAndHandleDBViolationException("ClearSettleVoucherFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toId, null);
                    }
                    finally
                    {
                        Database.closePreparedStatement(p);
                        Database.closeResultSet(rs);
                        Database.closeConnection(con);
                    }
            }
        }
    }
}
