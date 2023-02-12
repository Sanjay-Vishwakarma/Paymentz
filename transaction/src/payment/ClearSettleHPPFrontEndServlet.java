package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.MerchantConfigManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
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
import java.sql.Connection;
import java.util.Enumeration;

/**
 * Created by Admin on 25-Jul-18.
 */
public class ClearSettleHPPFrontEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(ClearSettleHPPFrontEndServlet.class.getName());
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
        TransactionManager transactionManager = new TransactionManager();
        ActionEntry entry = new ActionEntry();
        CommResponseVO commResponseVO = new CommResponseVO();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO = new GenericCardDetailsVO();
        MerchantDetailsVO merchantDetailsVO = null;
        Functions functions = new Functions();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        TransactionUtility transactionUtility= new TransactionUtility();
        MerchantDAO merchantDAO= new MerchantDAO();

        HttpSession session = req.getSession(true);

        Connection con = null;

        String toId = "";
        String isService = "";
        String accountId = "";
        String dbStatus = "";
        String status = "";
        String amount = "";

        String description = "";
        String redirectUrl = "";
        String clkey = "";
        String checksumNew = "";
        String autoRedirect = "";
        String logoName = "";
        String partnerName = "";
        String confirmStatus = "";
        String responseStatus = "";

        String token = "";
        String currency = "";
        String billingDesc = "";
        String email = "";
        String eci = "";
        String errorName = "";
        String tmpl_amt = "";
        String tmpl_currency = "";
        String orderDescription = "";
        String payModeId = "";
        String cardTypeId = "";
        String cardNumber = "";

        String firstName="";
        String lastName="";

        Enumeration enumeration = req.getParameterNames();
        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            String value = req.getParameter(key);
            transactionLogger.error(key + ":" + value);
        }

        if (functions.isValueNull(req.getParameter("referenceNo")))
        {
            String trackingId = req.getParameter("referenceNo");
            String transactionId = req.getParameter("transactionId");
            String transactionStatus = req.getParameter("status");
            String message = req.getParameter("message");
            String errorCode = req.getParameter("code");

            if (functions.isValueNull(transactionStatus))
            {
                try
                {
                    TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                    toId = transactionDetailsVO.getToid();
                    accountId = transactionDetailsVO.getAccountId();
                    dbStatus = transactionDetailsVO.getStatus();
                    amount = transactionDetailsVO.getAmount();
                    orderDescription = transactionDetailsVO.getOrderDescription();
                    description = transactionDetailsVO.getDescription();
                    payModeId = transactionDetailsVO.getPaymodeId();
                    cardTypeId = transactionDetailsVO.getCardTypeId();
                    redirectUrl = transactionDetailsVO.getRedirectURL();
                    currency = transactionDetailsVO.getCurrency();
                    firstName=transactionDetailsVO.getFirstName();
                    lastName=transactionDetailsVO.getLastName();
                    email=transactionDetailsVO.getEmailaddr();
                    tmpl_amt=transactionDetailsVO.getTemplateamount();
                    tmpl_currency=transactionDetailsVO.getTemplatecurrency();
                    if (functions.isValueNull(transactionDetailsVO.getCcnum()))
                    {
                        cardNumber = PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
                    }

                    auditTrailVO.setActionExecutorName("Customer");
                    auditTrailVO.setActionExecutorId(toId);
                    StringBuffer dbBuffer = new StringBuffer();


                    merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                    if (merchantDetailsVO != null)
                    {
                        clkey = merchantDetailsVO.getKey();
                        autoRedirect = merchantDetailsVO.getAutoRedirect();
                        logoName = merchantDetailsVO.getLogoName();
                        partnerName = merchantDetailsVO.getPartnerName();
                        isService = merchantDetailsVO.getService();
                    }

                    String transType = "sale";
                    if ("N".equals(isService))
                    {
                        transType = "auth";
                    }
                    try
                    {
                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setErrorCode(errorCode);
                        commResponseVO.setTransactionType(transType);
                        commResponseVO.setTransactionStatus(transactionStatus);
                        commResponseVO.setDescription(message);
                        commResponseVO.setRemark(message);

                        if(PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                        {
                            if ("00".equals(errorCode))
                            {
                                status = "success";
                                confirmStatus = "Y";
                                responseStatus = "Successful";
                                billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                                commResponseVO.setStatus(status);
                                commResponseVO.setDescriptor(billingDesc);
                                if ("sale".equals(transType))
                                {
                                    dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',status='capturesuccess'");
                                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");

                                }
                                else
                                {
                                    dbBuffer.append("update transaction_common set paymentid='" + transactionId + "' ,status='authsuccessful'");
                                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authsuccessful");
                                }
                            }
                            else
                            {
                                confirmStatus = "N";
                                status = "fail";
                                commResponseVO.setStatus(status);
                                responseStatus = "Transaction Failed(" + transactionStatus + ")";
                                dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "'");
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authfailed");
                            }
                            dbBuffer.append(" ,remark='" + message + "' where trackingid = " + trackingId);
                            con = Database.getConnection();
                            Database.executeUpdate(dbBuffer.toString(), con);

                            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                            asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                            AsynchronousSmsService smsService = new AsynchronousSmsService();
                            smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
                        }

                        commonValidatorVO.setTrackingid(trackingId);
                        genericTransDetailsVO.setOrderId(description);
                        genericTransDetailsVO.setOrderDesc(orderDescription);
                        genericTransDetailsVO.setAmount(amount);
                        genericTransDetailsVO.setCurrency(currency);
                        genericTransDetailsVO.setRedirectUrl(redirectUrl);
                        commonValidatorVO.setLogoName(logoName);
                        commonValidatorVO.setPartnerName(partnerName);
                        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                        commonValidatorVO.setPaymentType(payModeId);
                        commonValidatorVO.setCardType(cardTypeId);
                        commonValidatorVO.setErrorName(errorName);
                        addressDetailsVO.setTmpl_amount(tmpl_amt);
                        addressDetailsVO.setTmpl_currency(tmpl_currency);
                        cardDetailsVO.setCardNum(cardNumber);
                        if (functions.isValueNull(email))
                            addressDetailsVO.setEmail(email);
                        if (functions.isValueNull(firstName))
                            addressDetailsVO.setFirstname(firstName);

                        if (functions.isValueNull(lastName))
                            addressDetailsVO.setLastname(lastName);


                        commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                        commonValidatorVO.setEci(eci);
                        commonValidatorVO.setCardDetailsVO(cardDetailsVO);

                        if ("Y".equalsIgnoreCase(autoRedirect))
                        {
                            transactionUtility.doAutoRedirect(commonValidatorVO, res, responseStatus, billingDesc);
                        }
                        else
                        {
                            req.setAttribute("responceStatus", responseStatus);
                            req.setAttribute("displayName", billingDesc);
                            req.setAttribute("remark", message);
                            req.setAttribute("errorName", errorName);
                            req.setAttribute("transDetail", commonValidatorVO);
                            session.setAttribute("ctoken", ctoken);

                            String confirmationPage = "";
                            String version = (String)session.getAttribute("version");
                            if(functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                                confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                            else
                                confirmationPage = "/confirmationpage.jsp?ctoken=";
                            RequestDispatcher rd = req.getRequestDispatcher(confirmationPage + ctoken);
                            rd.forward(req, res);
                            //session.invalidate();
                        }
                    }
                    catch (SystemError se)
                    {
                        transactionLogger.error("SystemError In ClearSettleHPPFrontEndServlet ::::::", se);
                        PZExceptionHandler.raiseAndHandleDBViolationException("ClearSettleFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toId, null);
                    }
                    finally
                    {
                        Database.closeConnection(con);
                    }
                }
                catch (PZDBViolationException tve)
                {
                    transactionLogger.error("PZDBViolationException In ClearSettleHPPFrontEndServlet :::::", tve);
                }
            }
        }
    }
}
