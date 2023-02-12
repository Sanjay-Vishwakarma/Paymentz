package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
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
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.sms.AsynchronousSmsService;
import com.payment.validators.vo.CommonValidatorVO;
import org.json.JSONException;
import org.json.JSONObject;
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

/**
 * Created by Roshan on 11/3/2017.
 */
public class RVFrontEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(RVFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

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
        HttpSession session = request.getSession(true);

        ActionEntry entry = new ActionEntry();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO= new GenericCardDetailsVO();
        TransactionUtility transactionUtility = new TransactionUtility();

        MerchantDetailsVO merchantDetailsVO = null;
        Connection con = null;

        String toId = "";
        String payModeId = "";
        String cardTypeId = "";
        String isService = "";
        String accountId = "";
        String status = "";
        String responseStatus = "";
        String amount = "";
        String description = "";
        String orderDescription = "";
        String redirectUrl = "";
        String clKey = "";
        String checksumNew = "";
        String autoRedirect = "";
        String logoName = "";
        String partnerName = "";
        String confirmStatus = "";
        String isPowerBy = "";
        String firstName="";
        String lastName="";
        String responceStatus = "";

        String token = "";
        // String orderDesc = "";
        String currency = "";
        String billingDesc = "";
        String message = "";
        String email = "";
        String TMPLAmount = "";
        String TMPLCurrency = "";
        String ccnum="";
        String transType = "";

        String transactionId = "";
        String transactionStatus = "";
        String errorCode = "";
        String trackingId = "";
        String dbStatus="";
        String eci="";
        String customerId="";

        Functions functions = new Functions();
        Transaction transaction = new Transaction();

        if (functions.isValueNull(request.getParameter("response")))
        {

            String responseData = request.getParameter("response");


            transactionLogger.error("response:::::" + request.getParameter("response"));
            try
            {
                if (functions.isValueNull(responseData))
                {
                    JSONObject jsonObject = new JSONObject(responseData);
                    String opStatus = jsonObject.getString("status");
                    errorCode = jsonObject.getString("vbvrespcode");
                    message = jsonObject.getString("vbvrespmessage");
                    transactionId = jsonObject.getString("flwRef");
                    trackingId = jsonObject.getString("txRef");
                    if ("successful".equals(opStatus) && "00".equals(errorCode))
                    {
                        transactionStatus = "success";
                    }
                    else
                    {
                        transactionStatus = "failed";
                    }
                }
                else
                {
                    transactionStatus = "failed";
                    message = "Bank connectivity issue";
                }


                TransactionManager transactionManager = new TransactionManager();
                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                if (transactionDetailsVO != null)
                {
                    description = transactionDetailsVO.getDescription();
                    orderDescription = transactionDetailsVO.getOrderDescription();
                    amount = transactionDetailsVO.getAmount();
                    currency = transactionDetailsVO.getCurrency();
                    toId = transactionDetailsVO.getToid();
                    payModeId = transactionDetailsVO.getPaymentId();
                    cardTypeId = transactionDetailsVO.getCardTypeId();
                    email = transactionDetailsVO.getEmailaddr();
                    accountId = transactionDetailsVO.getAccountId();
                    TMPLAmount = transactionDetailsVO.getTemplateamount();
                    TMPLCurrency = transactionDetailsVO.getTemplatecurrency();
                    ccnum= PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
                    redirectUrl = transactionDetailsVO.getRedirectURL();
                    dbStatus=transactionDetailsVO.getStatus();
                    firstName=transactionDetailsVO.getFirstName();
                    lastName=transactionDetailsVO.getLastName();
                    customerId=transactionDetailsVO.getCustomerId();

                    transactionLogger.debug("TMPLAmount-----"+TMPLAmount);
                    transactionLogger.debug("TMPLCurrency-----"+TMPLCurrency);
                    transactionLogger.debug("ccnum-----"+ccnum);
                }

                GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                MerchantDAO merchantDAO= new MerchantDAO();
                merchantDetailsVO = merchantDAO.getMemberDetails(toId);

                auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                auditTrailVO.setActionExecutorId(toId);

                CommResponseVO commResponseVO = new CommResponseVO();

                commResponseVO.setTransactionId(transactionId);
                commResponseVO.setErrorCode(errorCode);
                commResponseVO.setTransactionType(transType);
                commResponseVO.setTransactionStatus(transactionStatus);

                commResponseVO.setDescription(message);
                commResponseVO.setStatus(status);
                commResponseVO.setRemark(message);
                commResponseVO.setDescriptor(billingDesc);

                if (merchantDetailsVO != null)
                {
                    autoRedirect = merchantDetailsVO.getAutoRedirect();
                    logoName = merchantDetailsVO.getLogoName();
                    partnerName = merchantDetailsVO.getPartnerName();
                    isService = merchantDetailsVO.getIsService();
                }

                try
                {
                    transactionLogger.debug("dbStatus-------"+dbStatus);
                    if(PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                    {
                        transType = "sale";
                        if ("N".equals(isService))
                        {
                            transType = "auth";
                        }

                        StringBuffer dbBuffer = new StringBuffer();
                        if ("success".equals(transactionStatus))
                        {
                            status = "success";
                            confirmStatus = "Y";
                            responceStatus = "Successful";
                            billingDesc = gatewayAccount.getDisplayName();
                            if ("sale".equals(transType))
                            {
                                dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',status='capturesuccess'");
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                            }
                            else
                            {
                                dbBuffer.append("update transaction_common set paymentid='" + transactionId + "' ,status='authsuccessful'");
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                            }
                        }
                        else
                        {
                            confirmStatus = "N";
                            status = "fail";
                            responceStatus = "Failed(" + message + ")";
                            dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "'");
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                        }
                        dbBuffer.append(" ,remark='" + message + "' where trackingid = " + trackingId);
                        con = Database.getConnection();
                        Database.executeUpdate(dbBuffer.toString(), con);

                        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                        asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                        AsynchronousSmsService smsService = new AsynchronousSmsService();
                        smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
                    }
                    else
                    {
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

                    genericTransDetailsVO.setOrderId(description);
                    genericTransDetailsVO.setOrderDesc(orderDescription);
                    genericTransDetailsVO.setAmount(amount);
                    genericTransDetailsVO.setCurrency(currency);
                    genericTransDetailsVO.setRedirectUrl(redirectUrl);

                    if (functions.isValueNull(email))
                        addressDetailsVO.setEmail(email);
                    if (functions.isValueNull(firstName))
                        addressDetailsVO.setFirstname(firstName);
                    if (functions.isValueNull(lastName))
                        addressDetailsVO.setLastname(lastName);
                    addressDetailsVO.setTmpl_amount(TMPLAmount);
                    addressDetailsVO.setTmpl_currency(TMPLCurrency);

                    cardDetailsVO.setCardNum(ccnum);

                    commonValidatorVO.setLogoName(logoName);
                    commonValidatorVO.setPartnerName(partnerName);
                    commonValidatorVO.setPaymentType(payModeId);
                    commonValidatorVO.setCardType(cardTypeId);
                    commonValidatorVO.setTrackingid(trackingId);
                    commonValidatorVO.setCustomerId(customerId);

                    commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                    commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                    commonValidatorVO.setEci(eci);

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
                        String version = (String)session.getAttribute("version");
                        if(functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                            confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                        else
                            confirmationPage = "/confirmationpage.jsp?ctoken=";

                        RequestDispatcher rd = request.getRequestDispatcher(confirmationPage + ctoken);
                        rd.forward(request, response);
                        session.invalidate();
                    }
                }
                catch (SystemError se)
                {
                    transactionLogger.error("SystemError::::::", se);
                    PZExceptionHandler.raiseAndHandleDBViolationException("RVFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toId, null);
                }
                finally
                {
                    Database.closeConnection(con);
                }
            }
            catch (PZDBViolationException e)
            {
                transactionLogger.error("error::::", e);
                PZExceptionHandler.raiseAndHandleDBViolationException("RVFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toId, null);
            }
            catch (JSONException ne)
            {
                transactionLogger.error("error:::", ne);
                PZExceptionHandler.raiseAndHandleTechnicalViolationException("RVFrontEndServlet.java", "doService()", null, "Transaction", null, PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, ne.getMessage(), ne.getCause(), toId, null);
            }
        }
    }
}