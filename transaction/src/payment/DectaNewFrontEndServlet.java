package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
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
import com.payment.common.core.*;
import com.payment.dectaNew.DectaNewPaymentGateway;
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
 * Created by Rihen on 7/27/2018.
 */
public class DectaNewFrontEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(DectaNewFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        transactionLogger.error("-----Inside DectaNewFrontEndServlet -----");

        Enumeration enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            String value = request.getParameter(key);

            transactionLogger.error("Key-----" + key + "----value----" + value);
        }

        String req_status = "";
        String updatedStatus = "";
        String toId = "";
        String paymentId = "";
        String payModeId = "";
        String cardTypeId = "";
        String isService = "";
        String accountId = "";
        String responceStatus = "";
        String amount = "";
        String description = "";
        String orderDescription = "";
        String redirectUrl = "";
        String clKey = "";
        String autoRedirect = "";
        String logoName = "";
        String partnerName = "";
        String firstName = "";
        String lastName = "";
        String tmpl_Amount = "";
        String tmpl_Currency = "";
        String ccnum = "";
        String expMonth = "";
        String expYear = "";
        String currency = "";
        String billingDesc = "";
        String message = "";
        String email = "";
        String dbStatus = "";
        String customerId = "";
        String eci = "";
        String trackingId = "";
        String notificationUrl = "";
        String terminalId = "";
        String expDate = "";


        String status_3d = "";
        String remark = "";

        HttpSession session = request.getSession(true);

        Functions functions = new Functions();
        TransactionManager transactionManager = new TransactionManager();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        MerchantDAO merchantDAO = new MerchantDAO();
        CommResponseVO commResponseVO = new CommResponseVO();
        ActionEntry entry = new ActionEntry();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        GenericCardDetailsVO cardDetailsVO = new GenericCardDetailsVO();
        TransactionUtility transactionUtility = new TransactionUtility();
        AbstractPaymentGateway pg = null;
        Connection con = null;

        try
        {
            if (functions.isValueNull(request.getParameter("trackingId")))
                trackingId = request.getParameter("trackingId");
            if (functions.isValueNull(request.getParameter("status")))
                req_status = request.getParameter("status");

            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);

            transactionLogger.error("=== tracking id ===" + trackingId);
            transactionLogger.error("=== req_status ===" + req_status);
            transactionLogger.error("=== toid ===" + transactionDetailsVO.getToid());

            toId = transactionDetailsVO.getToid();
            paymentId = transactionDetailsVO.getPaymentId();
            description = transactionDetailsVO.getDescription();
            redirectUrl = transactionDetailsVO.getRedirectURL();
            accountId = transactionDetailsVO.getAccountId();
            orderDescription = transactionDetailsVO.getOrderDescription();
            currency = transactionDetailsVO.getCurrency();
            amount = transactionDetailsVO.getAmount();
            dbStatus = transactionDetailsVO.getStatus();
            tmpl_Amount = transactionDetailsVO.getTemplateamount();
            tmpl_Currency = transactionDetailsVO.getTemplatecurrency();
            payModeId = transactionDetailsVO.getPaymodeId();
            cardTypeId = transactionDetailsVO.getCardTypeId();
            terminalId = transactionDetailsVO.getTerminalId();

            CommRequestVO commRequestVO = new CommRequestVO();
            CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();

            commTransactionDetailsVO.setPreviousTransactionId(paymentId);

            transactionLogger.error("Before Inquiry Payment ID ------- " + paymentId);

            commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
            DectaNewPaymentGateway dectaNewPaymentGateway = new DectaNewPaymentGateway(accountId);
            commResponseVO = (CommResponseVO) dectaNewPaymentGateway.processInquiry(commRequestVO);

            transactionLogger.error("After Inquiry ----");
            transactionLogger.error("After Inquiry Description = " + commResponseVO.getRemark());
            transactionLogger.error("After Inquiry Status 3D =" + commResponseVO.getAuthCode());
            transactionLogger.error("After Inquiry MPI Code = " + commResponseVO.getResponseHashInfo());

            if (functions.isValueNull(commResponseVO.getRemark()))
            {
                remark = commResponseVO.getRemark();
            }
            if (functions.isValueNull(commResponseVO.getAuthCode()))
            {
                status_3d = commResponseVO.getAuthCode();
            }
            if (functions.isValueNull(transactionDetailsVO.getFirstName()))
            {
                firstName = transactionDetailsVO.getFirstName();
            }
            if (functions.isValueNull(transactionDetailsVO.getLastName()))
            {
                lastName = transactionDetailsVO.getLastName();
            }
            if (functions.isValueNull(transactionDetailsVO.getEci()))
            {
                eci = transactionDetailsVO.getEci();
            }
            if (functions.isValueNull(transactionDetailsVO.getEmailaddr()))
            {
                email = transactionDetailsVO.getEmailaddr();
            }
            if (functions.isValueNull(transactionDetailsVO.getCustomerId()))
            {
                customerId = transactionDetailsVO.getCustomerId();
            }
            if (functions.isValueNull(transactionDetailsVO.getCcnum()))
            {
                ccnum = PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
            }
            if (functions.isValueNull(transactionDetailsVO.getExpdate()))
            {
                expDate = PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate());
                String temp[] = expDate.split("/");

                if (functions.isValueNull(temp[0]))
                {
                    expMonth = temp[0];
                }
                if (functions.isValueNull(temp[1]))
                {
                    expYear = temp[1];
                }
            }


            merchantDetailsVO = merchantDAO.getMemberDetails(toId);

            auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
            auditTrailVO.setActionExecutorId(toId);

            if (merchantDetailsVO != null)
            {
                clKey = merchantDetailsVO.getKey();
                autoRedirect = merchantDetailsVO.getAutoRedirect();
                logoName = merchantDetailsVO.getLogoName();
                partnerName = merchantDetailsVO.getPartnerName();
                isService = merchantDetailsVO.getIsService();
            }


            commResponseVO.setTransactionId(paymentId);
            commResponseVO.setCurrency(currency);
            commResponseVO.setAmount(amount);
            commResponseVO.setTmpl_Amount(tmpl_Amount);
            commResponseVO.setTmpl_Currency(tmpl_Currency);

            try
            {
                StringBuffer sb = new StringBuffer();

                transactionLogger.error("=== db status ===" + dbStatus);
                transactionLogger.error("=== transactionID ===" + commResponseVO.getTransactionId());

                if (PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus))
                {

                    if (functions.isValueNull(transactionDetailsVO.getNotificationUrl()))
                    {
                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                    }

                    sb.append("update transaction_common set ");

                    if ("success".equalsIgnoreCase(req_status))
                    {
                        billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        message = "Transaction Successful";
                        responceStatus = "Successful";

                        commResponseVO.setRemark(remark);
                        commResponseVO.setDescriptor(billingDesc);
                        commResponseVO.setStatus("success");
                        if ("N".equalsIgnoreCase(isService) || "PA".equalsIgnoreCase(transactionDetailsVO.getTransactionType())) // AUTH
                        {
                            transactionLogger.error("--- in Auth , authstarted---");
                            updatedStatus = "authsuccessful";
                            sb.append("remark='" + remark + "'");
                            sb.append(", status='authsuccessful'");
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, auditTrailVO, null);

                            if (status_3d.equalsIgnoreCase("true"))
                            {
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, commResponseVO, auditTrailVO, null);
                            }
                        }
                        else // SALE
                        {
                            transactionLogger.error("--- in Sale , capturesuccess ---");
                            updatedStatus = "capturesuccess";
                            sb.append("remark='" + remark + "'");
                            sb.append(", status='capturesuccess'");
                            sb.append(", captureamount=" + amount + "");
                            if (status_3d.equalsIgnoreCase("true"))
                            {
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, commResponseVO, auditTrailVO, null);
                            }
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                        }
                    }
                    else
                    {
                        transactionLogger.error("--- in else STATUS failed ---");
                        message = "Transaction Declined";
                        responceStatus = "fail";
                        updatedStatus = "authfailed";
                        commResponseVO.setRemark(remark);
                        commResponseVO.setStatus("fail");
                        sb.append("remark='" + remark + "'");
                        sb.append(", status='authfailed'");
                        if (status_3d.equalsIgnoreCase("true"))
                        {
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, commResponseVO, auditTrailVO, null);
                        }
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                    }

                    sb.append(" where trackingid = " + trackingId);
                    con = Database.getConnection();
                    transactionLogger.error("common update query DectaFrontendNotification---" + sb.toString());
                    Database.executeUpdate(sb.toString(), con);
                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                }
                else
                {
                    if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                    {
                        billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        responceStatus = "success";
                        if (functions.isValueNull(transactionDetailsVO.getRemark()))
                            message = transactionDetailsVO.getRemark();
                        else
                            message = "Transaction Successful";
                        updatedStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();

                    }
                    else if (PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                    {
                        billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        responceStatus = "success";
                        if (functions.isValueNull(transactionDetailsVO.getRemark()))
                            message = transactionDetailsVO.getRemark();
                        else
                            message = "Transaction Successful";
                        updatedStatus = PZTransactionStatus.AUTH_SUCCESS.toString();
                    }
                    else if (PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus))
                    {
                        responceStatus = "fail";
                        if (functions.isValueNull(transactionDetailsVO.getRemark()))
                            message = transactionDetailsVO.getRemark();
                        else if (!functions.isValueNull(message))
                            message = "Transaction Failed";
                        updatedStatus = PZTransactionStatus.AUTH_FAILED.toString();
                    }
                    else
                    {
                        responceStatus = "fail";
                        message = "Transaction Declined";
                        updatedStatus = PZTransactionStatus.FAILED.toString();
                    }
                }


                genericTransDetailsVO.setOrderId(description);
                genericTransDetailsVO.setOrderDesc(orderDescription);
                genericTransDetailsVO.setAmount(amount);
                genericTransDetailsVO.setCurrency(currency);
                genericTransDetailsVO.setBillingDiscriptor(billingDesc);
                genericTransDetailsVO.setNotificationUrl(notificationUrl);
                genericTransDetailsVO.setRedirectUrl(redirectUrl);
                addressDetailsVO.setEmail(email);
                addressDetailsVO.setFirstname(firstName);
                addressDetailsVO.setLastname(lastName);
                addressDetailsVO.setTmpl_amount(tmpl_Amount);
                addressDetailsVO.setTmpl_currency(tmpl_Currency);
                cardDetailsVO.setCardNum(ccnum);
                cardDetailsVO.setExpMonth(expMonth);
                cardDetailsVO.setExpYear(expYear);

                if (session.getAttribute("language") != null)
                {
                    addressDetailsVO.setLanguage(session.getAttribute("language").toString());
                }
                else
                {
                    addressDetailsVO.setLanguage("");
                }

                commonValidatorVO.setTrackingid(trackingId);
                commonValidatorVO.setLogoName(logoName);
                commonValidatorVO.setPartnerName(partnerName);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setPaymentType(payModeId);
                commonValidatorVO.setCardType(cardTypeId);
                commonValidatorVO.setCustomerId(customerId);
                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                commonValidatorVO.setEci(eci);
                commonValidatorVO.setTerminalId(terminalId);
                commonValidatorVO.setVersion(transactionDetailsVO.getVersion());

                transactionUtility.setToken(commonValidatorVO, responceStatus);


                if (functions.isValueNull(notificationUrl))
                {
                    transactionLogger.error("inside sending notification---" + notificationUrl + "--- for trackingid---" + trackingId);
                    TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                    transactionDetailsVO1.setTransactionMode("3D");
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, updatedStatus, message, "");
                }

                if ("Y".equalsIgnoreCase(autoRedirect))
                {
                    transactionUtility.doAutoRedirect(commonValidatorVO, response, responceStatus, billingDesc);
                }
                else
                {
                    request.setAttribute("responceStatus", responceStatus);
                    request.setAttribute("displayName", billingDesc);
                    request.setAttribute("remark", message);
                    request.setAttribute("transDetail", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);

                    String confirmationPage = "";
                    confirmationPage = "/confirmationCheckout.jsp?ctoken=";

                    RequestDispatcher rd = request.getRequestDispatcher(confirmationPage + ctoken);
                    rd.forward(request, response);
                }
            }
            catch (SystemError se)
            {
                transactionLogger.error("SystemError::::::", se);
                PZExceptionHandler.raiseAndHandleDBViolationException("DectaNewFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toId, null);
            }
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("DectaNewFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toId, null);
        }
        catch (Exception systemError)
        {
            transactionLogger.error("Exception IN DectaNewFrontEndServlet::::", systemError);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }


}