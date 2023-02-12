package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.MerchantConfigManager;
import com.manager.TransactionManager;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
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
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.util.Enumeration;

/**
 * Created by Admin on 9/13/2021.
 */

public class EcospendBackentServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(EcospendBackentServlet.class.getName());
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
        CommRequestVO requestVO = new CommRequestVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = new CommTransactionDetailsVO();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        TransactionDetailsVO transactionDetailsVO=new TransactionDetailsVO();
        HttpSession session = req.getSession(true);

        Connection con = null;

        String toId = "";
        String accountId = "";
        String dbStatus = "";
        String status = "";
        String amount = "";

        String description = "";
        String redirectUrl = "";
        String clkey = "";
        String checksumNew = "";
        String autoredirect = "";
        String logoName = "";
        String partnerName = "";
        String confirmStatus = "";
        String responseStatus = "";
        String Currency ="";
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
        String paymentid="";
        String firstName="";
        String lastName="";
        String notifystatus="";
        String Payment_id="";
        String message="";
        String paylink_id="";
        String standing_order_id="";

        Enumeration enumeration = req.getParameterNames();
        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            String value = req.getParameter(key);
            transactionLogger.error(key + ":" + value);
        }
        if (functions.isValueNull(req.getParameter("status")))
        {
            notifystatus = req.getParameter("status");
        }

        if (functions.isValueNull(req.getParameter("payment_id")))
        {
            Payment_id=req.getParameter("payment_id");
        }

        if (functions.isValueNull(req.getParameter("message")))
        {
            message=req.getParameter("message");
        }

        if (functions.isValueNull(req.getParameter("paylink_id")))
        {
            paylink_id=req.getParameter("paylink_id");
        }
        if (functions.isValueNull(req.getParameter("standing_order_id")))
        {
            standing_order_id=req.getParameter("standing_order_id");
        }

        /*if (functions.isValueNull(Payment_id))
        {*/
            transactionLogger.error("Payment_id::::::::::" + Payment_id);

            try
            {

                if (functions.isValueNull(paylink_id))
                {
                    transactionDetailsVO = transactionManager.getTransDetailsFromCommonByPaymentId(paylink_id);
                }
                else if (functions.isValueNull(standing_order_id))
                {
                    transactionDetailsVO = transactionManager.getTransDetailsFromCommonByPaymentId(standing_order_id);
                }
                else if(functions.isValueNull(Payment_id))
                {
                    transactionDetailsVO = transactionManager.getTransDetailsFromCommonByPaymentId(Payment_id);
                }
                else
                {
                    transactionDetailsVO = transactionManager.getTransDetailsFromCommonByPaymentId(Payment_id);
                }


                    toId = transactionDetailsVO.getToid();
                accountId = transactionDetailsVO.getAccountId();
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
                commTransactionDetailsVO.setOrderId(transactionDetailsVO.getTrackingid());
                commTransactionDetailsVO.setPreviousTransactionId(paymentid);
                commTransactionDetailsVO.setResponseHashInfo(paymentid);
                commTransactionDetailsVO.setAmount(amount);
                commTransactionDetailsVO.setCurrency(Currency);
                requestVO.setTransDetailsVO(commTransactionDetailsVO);
                StringBuffer dbBuffer = new StringBuffer();
                dbStatus=transactionDetailsVO.getStatus();
                MerchantConfigManager merchantConfigManager = new MerchantConfigManager();
                merchantDetailsVO = merchantConfigManager.getMerchantDetailFromToId(toId);

                if (merchantDetailsVO != null)
                {
                    clkey = merchantDetailsVO.getKey();
                    autoredirect = merchantDetailsVO.getAutoRedirect();
                    logoName = merchantDetailsVO.getLogoName();
                    partnerName = merchantDetailsVO.getPartnerName();
                }
                con = Database.getConnection();
                try
                {
                    transactionLogger.error("dbStatus---->"+dbStatus);
                    if(PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus))
                    {
                    if(functions.isValueNull(notifystatus))
                    {
                        if (notifystatus.equals("Verified") || notifystatus.equals("Completed"))
                        {
                            status = "success";
                            confirmStatus = "Y";
                            responseStatus = "Successful";
                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                            commResponseVO.setStatus(status);
                            commResponseVO.setDescriptor(billingDesc);
                            dbBuffer.append("update transaction_common set captureamount='" + amount + "',status='capturesuccess'");
                            dbBuffer.append(" ,remark='" + message + "' where trackingid = " + transactionDetailsVO.getTrackingid());

                            Database.executeUpdate(dbBuffer.toString(), con);

                            entry.actionEntryForCommon(transactionDetailsVO.getTrackingid(), amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                            statusSyncDAO.updateAllTransactionFlowFlag(transactionDetailsVO.getTrackingid(), "capturesuccess");

                        }
                        else if (notifystatus.equals("Failed"))
                        {
                            confirmStatus = "N";
                            status = "failed";
                            commResponseVO.setStatus(status);
                            dbBuffer.append("update transaction_common set status='authfailed'");
                            entry.actionEntryForCommon(transactionDetailsVO.getTrackingid(), amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                            statusSyncDAO.updateAllTransactionFlowFlag(transactionDetailsVO.getTrackingid(), "authfailed");
                            dbBuffer.append(" ,remark='" + message + "' where trackingid = " + transactionDetailsVO.getTrackingid());

                            Database.executeUpdate(dbBuffer.toString(), con);

                        }
                        else
                        {
                            status = "pending";
                        }
                     }
                    }
                    else
                    {
                        if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                        {
                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                            status = "success";
                            if (functions.isValueNull(transactionDetailsVO.getRemark()))
                                message = transactionDetailsVO.getRemark();
                            else
                                message = "Transaction Successful";
                            status = PZTransactionStatus.CAPTURE_SUCCESS.toString();

                        }
                        else if (PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                        {
                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                            status = "success";
                            if (functions.isValueNull(transactionDetailsVO.getRemark()))
                                message = transactionDetailsVO.getRemark();
                            else
                                message = "Transaction Successful";
                            status = PZTransactionStatus.AUTH_SUCCESS.toString();
                        }
                        else if (PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus))
                        {
                            status = "fail";
                            if (functions.isValueNull(transactionDetailsVO.getRemark()))
                                message = transactionDetailsVO.getRemark();
                            else if (!functions.isValueNull(message))
                                message = "Transaction Failed";
                            status = PZTransactionStatus.AUTH_FAILED.toString();
                        }
                    }
                        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                        asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(transactionDetailsVO.getTrackingid()), status, message, billingDesc);

                        AsynchronousSmsService smsService = new AsynchronousSmsService();
                        smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(transactionDetailsVO.getTrackingid()), status, message, billingDesc);

                    checksumNew = Checksum.generateChecksumForStandardKit(transactionDetailsVO.getTrackingid(), description, String.valueOf(amount), confirmStatus, clkey);

                    commonValidatorVO.setTrackingid(transactionDetailsVO.getTrackingid());
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


                    if ("Y".equalsIgnoreCase(autoredirect))
                    {
                        TransactionUtility transactionUtility = new TransactionUtility();
                        transactionUtility.doAutoRedirect(commonValidatorVO, res, responseStatus, billingDesc);
                    }
                    else
                    {
                        genericTransDetailsVO.setRedirectUrl(redirectUrl);
                        commonValidatorVO.setLogoName(logoName);
                        commonValidatorVO.setPartnerName(partnerName);
                        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                        addressDetailsVO.setTmpl_amount(tmpl_amt);
                        addressDetailsVO.setTmpl_currency(tmpl_currency);
                        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

                        session.setAttribute("ctoken", ctoken);
                        req.setAttribute("transDetail", commonValidatorVO);
                        req.setAttribute("responceStatus", status);
                        req.setAttribute("displayName", billingDesc);
                        String confirmationPage = "";
                        String version = (String)session.getAttribute("version");
                        if(functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                            confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                        else
                            confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                        session.invalidate();
                        RequestDispatcher rd = req.getRequestDispatcher(confirmationPage + ctoken);
                        rd.forward(req, res);
                    }
                }
                catch (SystemError se)
                {
                    transactionLogger.error("SystemError In EcospendBackentServlet::::::", se);
                    PZExceptionHandler.raiseAndHandleDBViolationException("EcospendBackentServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toId, null);
                }
                finally
                {
                    Database.closeConnection(con);
                }
            }
            catch (PZDBViolationException tve)
            {
                transactionLogger.error("PZDBViolationException In EcospendBackentServlet :::::", tve);
            }
            catch (NoSuchAlgorithmException ne)
            {
                transactionLogger.error("NoSuchAlgorithmException In EcospendBackentServlet:::::", ne);
                PZExceptionHandler.raiseAndHandleTechnicalViolationException("EcospendBackentServlet.java", "doService()", null, "Transaction", null, PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, ne.getMessage(), ne.getCause(), toId, null);
            }
            catch (PZGenericConstraintViolationException e)
            {
                transactionLogger.error("PZGenericConstraintViolationException In EcospendBackentServlet:::::", e);
            }
            catch (SystemError systemError)
            {
                transactionLogger.error("systemError In EcospendBackentServlet:::::", systemError);

            }
            finally
            {
                Database.closeConnection(con);
            }
        res.setStatus(200);
        return;
    }
    // }
}
