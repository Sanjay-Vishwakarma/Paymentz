package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.paySafeCard.PaySafeCardPaymentGateway;
import com.payment.paySafeCard.PaySafeCardUtils;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Sandip on 5/20/2017.
 */
public class PaySafeCardFrontEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(PaySafeCardFrontEndServlet.class.getName());
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
        transactionLogger.debug("-------Enter in doService of PaySafeCardFrontEndServlet-------");

        TransactionManager transactionManager = new TransactionManager();
        ActionEntry entry = new ActionEntry();
        CommResponseVO commResponseVO = new CommResponseVO();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        GenericRequestVO genericRequestVO = new GenericRequestVO();
        MerchantDetailsVO merchantDetailsVO = null;
        MerchantDAO merchantDAO = new MerchantDAO();

        HttpSession session = req.getSession(true);
        PaySafeCardUtils paySafeCardUtils = new PaySafeCardUtils();
        Connection con = null;
        PreparedStatement p =null;
        ResultSet rs = null;
        String isService = "";
        String accountId = "";
        String status = "";

        String description = "";
        String clkey = "";
        String checksumNew = "";
        String autoredirect = "";
        String logoName = "";
        String partnerName = "";
        String confirmStatus = "N";
        String token = "";

        String toId = "";

        String amount = "";
        String currency = "";
        String orderDesc = "";
        String redirectUrl = "";

        String transactionStatus = "";
        String errorCode = "";
        String resultCode = "";
        String transactionId = "";
        String message = "";
        String bankStatus = "";
        String billingDesc = "";
        String email = "";
        String paymodeid="";
        String cardtypeid="";
        String tmpl_amount="";
        String tmpl_currency="";
        String customerid="";
        String firstName="";
        String lastName="";

        Functions functions = new Functions();
        if (functions.isValueNull(req.getParameter("trackingid")))
        {


            String trackingId = req.getParameter("trackingid");
            try
            {
                Thread.sleep(3000);
                transactionLogger.debug("-------Enter in doService of PaySafeCardFrontEndServlet 2-------");
                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                if (transactionDetailsVO != null)
                {
                    toId = transactionDetailsVO.getToid();
                    accountId = transactionDetailsVO.getAccountId();
                    amount = transactionDetailsVO.getAmount();
                    redirectUrl = transactionDetailsVO.getRedirectURL();
                    currency = transactionDetailsVO.getCurrency();
                    orderDesc = transactionDetailsVO.getOrderDescription();
                    description = transactionDetailsVO.getDescription();
                    email = transactionDetailsVO.getEmailaddr();
                    paymodeid=transactionDetailsVO.getPaymodeId();
                    cardtypeid=transactionDetailsVO.getCardTypeId();
                    customerid=transactionDetailsVO.getCustomerId();
                    tmpl_amount=transactionDetailsVO.getTemplateamount();
                    tmpl_currency=transactionDetailsVO.getTemplatecurrency();
                    firstName=transactionDetailsVO.getFirstName();
                    lastName=transactionDetailsVO.getLastName();



                    PaySafeCardPaymentGateway paySafeCardPaymentGateway = new PaySafeCardPaymentGateway(transactionDetailsVO.getAccountId());

                    auditTrailVO.setActionExecutorName("FrontEndServlet");
                    auditTrailVO.setActionExecutorId(toId);

                    merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                    if(merchantDetailsVO!=null){
                        autoredirect=merchantDetailsVO.getAutoRedirect();
                        logoName=merchantDetailsVO.getLogoName();
                        partnerName=merchantDetailsVO.getPartnerName();
                        isService=merchantDetailsVO.getIsService();
                    }

                    con = Database.getConnection();
                    CommResponseVO commResponseVO1 = (CommResponseVO) paySafeCardPaymentGateway.processQuery(trackingId, genericRequestVO);
                    transactionStatus = commResponseVO1.getStatus();
                    errorCode = commResponseVO1.getRemark();
                    resultCode = commResponseVO1.getErrorCode();
                    transactionId = commResponseVO1.getTransactionId();
                    bankStatus = commResponseVO1.getTransactionStatus();

                    transactionLogger.error("transactionStatus-----" + transactionStatus);

                    if (commResponseVO1 != null)
                    {
                        transactionLogger.debug("1st query status FEServlet---" + commResponseVO1.getStatus());
                        if (commResponseVO1.getStatus().equalsIgnoreCase("success"))
                        {
                            transactionLogger.debug("2nd query status FEServlet---" + transactionDetailsVO.getStatus());
                            transactionLogger.debug("2nd query Transaction status FEServlet---" + commResponseVO1.getTransactionStatus());
                            if (commResponseVO1.getTransactionStatus().equalsIgnoreCase("S") && !transactionDetailsVO.getStatus().equalsIgnoreCase("capturesuccess"))
                            {
                                billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                                transactionLogger.debug("3rd query status FEServlet---" + commResponseVO1.getStatus());
                                status = "success";
                                confirmStatus = "Y";
                                commResponseVO.setStatus(status);
                                commResponseVO.setTransactionId(transactionId);
                                commResponseVO.setErrorCode(errorCode);
                                commResponseVO.setTransactionType("sale");
                                commResponseVO.setTransactionStatus(status);
                                commResponseVO.setDescriptor(billingDesc);


                                String asQuery = "update transaction_common set paymentid='" + commResponseVO.getTransactionId() + "',status='authsuccessful' ,remark='Transaction Successful' where trackingid = " + trackingId;
                                Database.executeUpdate(asQuery.toString(), con);
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, auditTrailVO, null);

                                String csQuery = "update transaction_common set captureamount='" + amount + "', paymentid='" + commResponseVO.getTransactionId() + "',status='capturestarted' where trackingid = " + trackingId;
                                Database.executeUpdate(csQuery.toString(), con);
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_STARTED, ActionEntry.ACTION_CAPTURE_STARTED, commResponseVO, auditTrailVO, null);

                                genericRequestVO = paySafeCardUtils.getRequestDataForDabit(trackingId, commResponseVO.getTransactionId());
                                CommResponseVO commResVO = (CommResponseVO) paySafeCardPaymentGateway.processCapture(trackingId, genericRequestVO);
                                commResVO.setTransactionId(commResponseVO.getTransactionId());

                                transactionLogger.debug("status for capture---" + commResponseVO1.getStatus());
                                if (commResponseVO1.getStatus().equalsIgnoreCase("success"))
                                {

                                    transactionLogger.debug("inside success after capture---");
                                    commResponseVO = (CommResponseVO) paySafeCardPaymentGateway.processQuery(trackingId, genericRequestVO);
                                    transactionLogger.debug("inside success after capture---");

                                    status = "success";
                                    String cQuery = "update transaction_common set paymentid='" + commResponseVO.getTransactionId() + "',status='capturesuccess' ,remark='Transaction Successful' where trackingid = " + trackingId;
                                    Database.executeUpdate(cQuery.toString(), con);
                                    commResponseVO.setTransactionStatus(status);
                                    commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                }
                                else
                                {
                                    status = "failed";

                                    String fQuery = "update transaction_common set paymentid='" + commResponseVO.getTransactionId() + "',status='capturefailed' ,remark='Capture Failed' where trackingid = " + trackingId;
                                    Database.executeUpdate(fQuery.toString(), con);
                                    commResponseVO.setTransactionStatus(status);
                                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_FAILED, ActionEntry.STATUS_CAPTURE_FAILED, commResponseVO, auditTrailVO, null);

                                }
                            }
                            else if (commResponseVO1.getTransactionStatus().equalsIgnoreCase("O"))
                            {
                                billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                                status = "success";
                                confirmStatus = "Y";
                                if (!transactionDetailsVO.getStatus().equalsIgnoreCase("capturesuccess"))
                                {

                                    String cQuery = "update transaction_common set paymentid='" + commResponseVO.getTransactionId() + "',status='capturesuccess' ,remark='Transaction Successful' where trackingid = " + trackingId;
                                    Database.executeUpdate(cQuery.toString(), con);
                                    commResponseVO.setTransactionStatus(status);
                                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                }
                            }
                        }
                        else
                        {
                            status = "failed";
                            message = "Transaction Failed";
                            String aQuery = "update transaction_common set paymentid='" + commResponseVO.getTransactionId() + "',status='authfailed' ,remark='" + message + "' where trackingid = " + trackingId;
                            Database.executeUpdate(aQuery.toString(), con);
                            commResponseVO.setTransactionStatus(status);
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                        }
                    }

                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, null, null);


                    if (transactionDetailsVO.getStatus().equalsIgnoreCase("capturesuccess") || transactionDetailsVO.getStatus().equalsIgnoreCase("authsuccessful"))
                    {
                        confirmStatus = "Y";
                        billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        status = "success";
                    }

                    if(status.equalsIgnoreCase("success")){
                        status="successful";
                    }
                    commonValidatorVO.setTrackingid(trackingId);
                    genericTransDetailsVO.setOrderId(description);
                    genericTransDetailsVO.setAmount(amount);
                    genericTransDetailsVO.setCurrency(currency);
                    genericTransDetailsVO.setOrderDesc(orderDesc);
                    genericTransDetailsVO.setRedirectUrl(redirectUrl);


                    addressDetailsVO.setEmail(email);
                    addressDetailsVO.setTmpl_amount(tmpl_amount);
                    addressDetailsVO.setTmpl_currency(tmpl_currency);
                    addressDetailsVO.setFirstname(firstName);
                    addressDetailsVO.setLastname(lastName);
                    commonValidatorVO.setLogoName(logoName);
                    commonValidatorVO.setPartnerName(partnerName);
                    commonValidatorVO.setPaymentType(paymodeid);
                    commonValidatorVO.setCardType(cardtypeid);
                    commonValidatorVO.setTrackingid(trackingId);
                    commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                    commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    commonValidatorVO.setCustomerId(customerid);

                    if ("Y".equalsIgnoreCase(autoredirect))
                    {
                        TransactionUtility transactionUtility = new TransactionUtility();
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

            }catch(PZDBViolationException tve)
            {
                transactionLogger.error("PZDBViolationException in PaySafeCardFrontEndServlet", tve);
            }
            catch(PZTechnicalViolationException tve)
            {
                transactionLogger.error("PZDBViolationException in PaySafeCardFrontEndServlet", tve);
            }
            catch(SystemError se)
            {
                transactionLogger.error("PZDBViolationException in PaySafeCardFrontEndServlet", se);
            }
            catch(InterruptedException ne)
            {
                transactionLogger.error("error:::", ne);
                PZExceptionHandler.raiseAndHandleTechnicalViolationException("PaySafeCardFrontEndServlet.java", "doService()", null, "Transaction", null, PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, ne.getMessage(), ne.getCause(), toId, null);
            }finally
            {
                Database.closeConnection(con);
                Database.closePreparedStatement(p);
                Database.closeResultSet(rs);
            }
        }
    }
}
