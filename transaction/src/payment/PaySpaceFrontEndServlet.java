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
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.payspace.PaySpacePaymentGateway;
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
import java.util.Map;

/**
 * Created by Uday on 7/31/17.
 */
public class PaySpaceFrontEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(PaySpaceFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    private static String generateAutoSubmitForm(String actionUrl, Map<String, String> paramMap)
    {
        StringBuilder html = new StringBuilder();
        html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>\n");
        html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"").append(actionUrl).append("\" method=\"post\">\n");

        for (String key : paramMap.keySet())
        {
            html.append("<input type=\"hidden\" name=\"" + key + "\" id=\"" + key + "\" value=\"" + paramMap.get(key) + "\">\n");
        }
        html.append("</form>\n");
        html.append("<script language=\"javascript\">");
        html.append("document.pay_form.submit();");
        html.append("</script>");
        html.append("</body>");
        html.append("</html>");
        transactionLogger.debug("html---" + html);
        return html.toString();
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
        TransactionManager transactionManager = new TransactionManager();
        ActionEntry entry = new ActionEntry();
        CommResponseVO commResponseVO = new CommResponseVO();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO= new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO= new GenericCardDetailsVO();
        CommRequestVO genericRequestVO = new CommRequestVO();
        MerchantDetailsVO merchantDetailsVO = null;
        MerchantDAO merchantDAO = new MerchantDAO();
        Functions functions=new Functions();
        HttpSession session = req.getSession(true);
        Connection con = null;
        PreparedStatement p = null;
        ResultSet rs = null;
        String toId = "";
        String responceStatus = "";
        String isService = "";
        String accountId = "";
        String status = "";
        String amount = "";
        String description = "";
        String redirectUrl = "";
        String clKey = "";
        String autoRedirect = "";
        String logoName = "";
        String partnerName = "";
        String confirmStatus = "";
        String token = "";
        String orderDesc = "";
        String currency = "";
        String transactionId = "";
        String transactionStatus = "";
        String errorCode = "";
        String message = "";
        String billingDesc = "";
        String transType = "";
        String dbStatus="";
        String email="";
        String cardNum="";
        String password="";
        String bankTransId="";
        String payModeId="";
        String cardTypeId="";
        String tmpl_Amount="";
        String tmpl_Currency="";
        String firstName="";
        String lastName="";
        String eci="";
        String customerId="";
        String ccnum="";



        String trackingId = req.getParameter("order_id");

        TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);

        toId = transactionDetailsVO.getToid();
        accountId = transactionDetailsVO.getAccountId();
        amount = transactionDetailsVO.getAmount();
        description = transactionDetailsVO.getDescription();
        redirectUrl = transactionDetailsVO.getRedirectURL();
        email=transactionDetailsVO.getEmailaddr();
        cardNum= PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
        bankTransId= transactionDetailsVO.getPaymentId();
        payModeId=transactionDetailsVO.getPaymodeId();
        cardTypeId=transactionDetailsVO.getCardTypeId();
        tmpl_Amount=transactionDetailsVO.getTemplateamount();
        tmpl_Currency=transactionDetailsVO.getTemplatecurrency();
        customerId=transactionDetailsVO.getCustomerId();
        firstName=transactionDetailsVO.getFirstName();
        lastName=transactionDetailsVO.getLastName();
        eci=transactionDetailsVO.getEci();
        dbStatus=transactionDetailsVO.getStatus();



        if(functions.isValueNull(transactionDetailsVO.getOrderDescription()))
            orderDesc = transactionDetailsVO.getOrderDescription();
        currency = transactionDetailsVO.getCurrency();

        PaySpacePaymentGateway paySpacePaymentGateway = new PaySpacePaymentGateway(accountId);
        StatusSyncDAO statusSyncDAO=new StatusSyncDAO();
        try{

            CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
            commTransactionDetailsVO.setOrderId(trackingId);
            commTransactionDetailsVO.setPreviousTransactionId(bankTransId);

            CommCardDetailsVO commCardDetailsVO=new CommCardDetailsVO();
            commCardDetailsVO.setCardNum(cardNum);

            CommAddressDetailsVO commAddressDetailsVO=new CommAddressDetailsVO();
            commAddressDetailsVO.setEmail(email);

            genericRequestVO.setCardDetailsVO(commCardDetailsVO);
            genericRequestVO.setAddressDetailsVO(commAddressDetailsVO);
            genericRequestVO.setTransDetailsVO(commTransactionDetailsVO);

            CommResponseVO commResponseVO1 = (CommResponseVO) paySpacePaymentGateway.processInquiry(genericRequestVO);

            transactionStatus = commResponseVO1.getStatus();
            errorCode = commResponseVO1.getErrorCode();
            transactionId = commResponseVO1.getTransactionId();

            auditTrailVO.setActionExecutorName("Customer");
            auditTrailVO.setActionExecutorId(toId);
            StringBuffer dbBuffer = new StringBuffer();

            merchantDetailsVO=merchantDAO.getMemberDetails(toId);

            if (merchantDetailsVO != null)
            {
                //clKey = merchantDetailsVO.getKey();
                autoRedirect = merchantDetailsVO.getAutoRedirect();
                logoName = merchantDetailsVO.getLogoName();
                partnerName = merchantDetailsVO.getPartnerName();
                isService = merchantDetailsVO.getIsService();
            }
            if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
            {
                transType = "sale";
                if ("N".equals(isService))
                {
                    transType = "auth";
                }
                commResponseVO.setTransactionId(transactionId);
                commResponseVO.setErrorCode(errorCode);
                commResponseVO.setTransactionType(transType);
                commResponseVO.setTransactionStatus(transactionStatus);

                if ("success".equals(transactionStatus))
                {
                    status = "success";
                    message = "Transaction Successful";
                    responceStatus = "Successful";
                    billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    commResponseVO.setDescription(message);
                    commResponseVO.setStatus(status);
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescriptor(billingDesc);
                    confirmStatus = "Y";

                    if ("sale".equals(transType))
                    {
                        dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',status='capturesuccess'");
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                        dbStatus = "capturesuccess";
                    }
                    else
                    {
                        dbBuffer.append("update transaction_common set paymentid='" + transactionId + "' ,status='authsuccessful'");
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                        dbStatus = "authsuccessful";
                    }
                }
                else
                {
                    confirmStatus = "N";
                    status = "fail";
                    message = "Transaction Failed";
                    responceStatus = "Failed";
                    commResponseVO.setStatus(status);
                    commResponseVO.setDescription(message);
                    commResponseVO.setRemark(message);
                    dbStatus = "authfailed";
                    dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "'");
                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
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
                if(PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus) || PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                {
                    billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    status = "success";
                    message = "Transaction Successful";
                    responceStatus = "Successful";
                }
                else
                {
                    status = "fail";
                    message = "Transaction Declined";
                    responceStatus = "Failed";
                }
            }
            commonValidatorVO.setTrackingid(trackingId);
            genericTransDetailsVO.setOrderId(description);
            genericTransDetailsVO.setOrderDesc(orderDesc);
            genericTransDetailsVO.setAmount(amount);
            genericTransDetailsVO.setCurrency(currency);

            genericTransDetailsVO.setRedirectUrl(redirectUrl);
            commonValidatorVO.setLogoName(logoName);
            commonValidatorVO.setPartnerName(partnerName);
            commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
            commonValidatorVO.setPaymentType(payModeId);
            commonValidatorVO.setCardType(cardTypeId);
            if (functions.isValueNull(email))
                addressDetailsVO.setEmail(email);
            if (functions.isValueNull(firstName))
                addressDetailsVO.setFirstname(firstName);

            if (functions.isValueNull(lastName))
                addressDetailsVO.setLastname(lastName);
            addressDetailsVO.setTmpl_amount(tmpl_Amount);
            addressDetailsVO.setTmpl_currency(tmpl_Currency);
            cardDetailsVO.setCardNum(ccnum);

            commonValidatorVO.setCustomerId(customerId);
            commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
            commonValidatorVO.setCardDetailsVO(cardDetailsVO);
            commonValidatorVO.setEci(eci);

            if ("Y".equalsIgnoreCase(autoRedirect))
            {
                TransactionUtility transactionUtility= new TransactionUtility();
                transactionUtility.doAutoRedirect(commonValidatorVO, res, responceStatus, billingDesc);
            }
            else
            {
                req.setAttribute("responceStatus", responceStatus);
                req.setAttribute("displayName", billingDesc);
                req.setAttribute("remark", message);
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
                session.invalidate();
            }

        }
        catch (SystemError systemError){
            transactionLogger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseAndHandleDBViolationException("PaySpaceFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(),systemError.getCause(), toId, null);
        }
        catch (PZDBViolationException e){
            transactionLogger.error("PZDBViolationException:::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("PaySpaceFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toId, null);
        }
        catch (PZTechnicalViolationException e){
            transactionLogger.error("PZTechnicalViolationException:::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("PaySpaceFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toId, null);
        }
        catch (PZConstraintViolationException e){
            transactionLogger.error("PZConstraintViolationException:::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("PaySpaceFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toId, null);
        }
        finally
        {
            Database.closePreparedStatement(p);
            Database.closeResultSet(rs);
            Database.closeConnection(con);
        }
    }
}
