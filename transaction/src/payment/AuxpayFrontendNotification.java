package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.TokenManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.auxpay_payment.core.AuxPayUtills;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Jinesh on 11/7/2015.
 */
public class AuxpayFrontendNotification extends PzServlet
{
    private static Logger log = new Logger(AuxpayFrontendNotification.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(AuxpayFrontendNotification.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    private static String generateAutoSubmitForm(String actionUrl, Map<String, String> paramMap)
    {
        StringBuilder html = new StringBuilder();
        html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>\n");
        html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"").append(actionUrl).append("\" method=\"post\">\n");

        for (String key : paramMap.keySet()) {
            html.append("<input type=\"hidden\" name=\"" + key + "\" id=\"" + key + "\" value=\"" + paramMap.get(key) + "\">\n");
        }
        html.append("</form>\n");
        html.append("<script language=\"javascript\">");
        html.append("document.pay_form.submit();");
        html.append("</script>");
        html.append("</body>");
        html.append("</html>");
        log.debug("html---"+html);
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
        log.debug("-------Enter in doService of AuxpayFrontendNotification---ip address----" + req.getHeader("X-Forwarded-For"));
        log.error("-------Enter in doService of AuxpayFrontendNotification---ip address----" + req.getHeader("X-Forwarded-For"));
        transactionLogger.debug("-------Enter in doService of AuxpayFrontendNotification-------" + req.getHeader("X-Forwarded-For"));
        transactionLogger.debug("-------AuxpayFrontendNotification-------" + req.getParameter("depositresult"));

        TransactionManager transactionManager = new TransactionManager();
        ActionEntry entry = new ActionEntry();
        CommResponseVO commResponseVO = new CommResponseVO();
        AuditTrailVO auditTrailVO = new AuditTrailVO();

        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO=new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        MerchantDAO merchantDAO=new MerchantDAO();

        HttpSession session = req.getSession(true);

        String xmlResponse = req.getParameter("depositresult");
        String transactionTrackingId = "";
        String transactionError = "";
        String transactionErrorMsg = "";
        String transactionTimeStamp = "";
        String transactionResponseId = "";
        Connection con = null;
        PreparedStatement p = null;
        ResultSet rs = null;

        String toid = "";
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

        String isTokenizationAllowed = "N";
        String token="";

        String orderDesc = "";
        String currency = "";
        String tmpl_amt = "";
        String tmpl_currency = "";

        if(xmlResponse!=null || !xmlResponse.equals("null"))
        {
            Map xmlResponseMap = new HashMap();
            try
            {
                xmlResponseMap = AuxPayUtills.readStepThreeResponse(xmlResponse);

                transactionTrackingId = xmlResponseMap.get("merchanttxid").toString();
                transactionError = xmlResponseMap.get("error").toString();
                transactionErrorMsg = xmlResponseMap.get("errormessage").toString();
                transactionTimeStamp = xmlResponseMap.get("txtimestamp").toString();
                transactionResponseId = xmlResponseMap.get("wallettxid").toString();

                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(transactionTrackingId);

                if (transactionDetailsVO != null)
                {
                    toid = transactionDetailsVO.getToid();
                    accountId = transactionDetailsVO.getAccountId();
                    dbStatus = transactionDetailsVO.getStatus();
                    amount = transactionDetailsVO.getAmount();
                    description = transactionDetailsVO.getDescription();
                    redirectUrl = transactionDetailsVO.getRedirectURL();
                    orderDesc = transactionDetailsVO.getOrderDescription();
                    currency = transactionDetailsVO.getCurrency();
                    tmpl_amt = transactionDetailsVO.getTemplateamount();
                    tmpl_currency = transactionDetailsVO.getTemplatecurrency();

                    auditTrailVO.setActionExecutorName("Customer");
                    auditTrailVO.setActionExecutorId(toid);

                    StringBuffer dbBuffer = new StringBuffer();
                    //Confirmation Page setting
                    try
                    {
                        con = Database.getConnection();
                        String query = "select m.clkey,m.autoredirect,m.partnerid,logoName,m.isPoweredBy,partnerName,m.isTokenizationAllowed,isAddrDetailsRequired FROM members AS m,partners AS p WHERE m.partnerId=p.partnerId AND m.memberid=?";
                        p = con.prepareStatement(query);
                        p.setString(1, toid);
                        //ResultSet rs = p.executeQuery();
                        rs = p.executeQuery();
                        if (rs.next())
                        {
                            clkey = rs.getString("clkey");
                            autoredirect = rs.getString("autoredirect");
                            //isPowerBy = rs.getString("isPoweredBy");
                            logoName = rs.getString("logoName");
                            partnerName = rs.getString("partnerName");
                            //partnerId = rs.getString("partnerId");
                            //isAddressDetailsRequired = rs.getString("isAddrDetailsRequired");
                            isTokenizationAllowed = rs.getString("isTokenizationAllowed");
                        }

                    }
                    catch (SystemError se)
                    {
                        log.error("error::::", se);
                        transactionLogger.error("error::::", se);
                        PZExceptionHandler.raiseAndHandleDBViolationException("AuxpayFrontendNotification.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toid, null);
                    }
                    finally
                    {
                        Database.closePreparedStatement(p);
                        Database.closeResultSet(rs);
                        Database.closeConnection(con);
                    }


                    if (PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus))
                    {
                        commResponseVO.setResponseTime(transactionTimeStamp);
                        commResponseVO.setTransactionId(transactionResponseId);
                        commResponseVO.setErrorCode(transactionError);
                        commResponseVO.setTransactionType("sale");

                        if (transactionError.equals("0") && transactionErrorMsg.equals(""))
                        {
                            dbBuffer.append("update transaction_common set captureamount='" + amount + "', status='capturesuccess'");

                            commResponseVO.setDescription("Transaction Approved");
                            status = "success";
                            commResponseVO.setTransactionStatus(status);
                            commResponseVO.setStatus(status);

                            confirmStatus = "Y";

                            entry.actionEntryForCommon(transactionTrackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                            if ("Y".equals(isTokenizationAllowed))
                            {
                                Functions functions = new Functions();
                                TokenManager tokenManager = new TokenManager();
                                String cardNumber = PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
                                String strToken = tokenManager.isCardAvailable(toid, cardNumber);
                                if (functions.isValueNull(strToken))
                                {
                                    token = strToken;
                                }
                                else
                                {
                                    token = tokenManager.createTokenByTrackingId(transactionTrackingId, transactionDetailsVO);
                                }
                            }
                        }
                        else
                        {
                            dbBuffer.append("update transaction_common set status='authfailed'");

                            commResponseVO.setDescription(transactionErrorMsg);
                            confirmStatus = "N";

                            status = "fail";
                            commResponseVO.setTransactionStatus(status);
                            commResponseVO.setStatus(status);
                            entry.actionEntryForCommon(transactionTrackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                        }
                        dbBuffer.append(" where trackingid = " + transactionTrackingId);
                        log.debug("common update query in AuxPayFrontEnd---" + dbBuffer.toString());
                        try
                        {
                            con = Database.getConnection();
                            Database.executeUpdate(dbBuffer.toString(), con);
                        }
                        catch (SystemError se)
                        {
                            log.error("error::::", se);
                            transactionLogger.error("error::::", se);
                            PZExceptionHandler.raiseAndHandleDBViolationException("AuxpayFrontendNotification.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toid, null);
                        }
                        finally
                        {
                            Database.closeConnection(con);
                        }

                    }

                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(transactionTrackingId), status, null, null);

                    checksumNew = Checksum.generateChecksumForStandardKit(transactionTrackingId, description, String.valueOf(amount), confirmStatus, clkey);

                    commonValidatorVO.setTrackingid(transactionTrackingId);
                    genericTransDetailsVO.setOrderId(description);
                    genericTransDetailsVO.setAmount(amount);
                    genericTransDetailsVO.setCurrency(currency);
                    genericTransDetailsVO.setOrderDesc(orderDesc);
                    Map saleMap = new TreeMap();
                    String billingDesc = "";
                    if (confirmStatus.equals("Y"))
                    {
                        billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    }

                    saleMap.put("desc", description);
                    saleMap.put("checksum", checksumNew);
                    saleMap.put("amount", amount);
                    saleMap.put("trackingid", transactionTrackingId);
                    saleMap.put("status", confirmStatus);
                    saleMap.put("descriptor", billingDesc);
                    saleMap.put("tmpl_currency", tmpl_currency);
                    saleMap.put("tmpl_amt", tmpl_amt);
                    saleMap.put("token", token);
                    saleMap.put("custMerchantId",transactionDetailsVO.getCustomerId());

                    log.debug("----autoredirect----" + autoredirect + "---redirecturl----" + redirectUrl);
                    log.debug("Auxpay values------" + description + "-" + checksumNew + "-" + amount + "-" + transactionTrackingId + "-" + confirmStatus);

                    if ("Y".equalsIgnoreCase(autoredirect))
                    {

                        String redirect = generateAutoSubmitForm(redirectUrl, saleMap);
                        res.setContentType("text/html;charset=UTF-8");
                        res.setCharacterEncoding("UTF-8");

                        try
                        {
                            res.getWriter().write(redirect);
                        }
                        catch (IOException e)
                        {
                            log.error("IO Exception in InPayFrontEndServlet---", e);
                            transactionLogger.error("IO Exception in InPayFrontEndServlet---", e);
                        }
                    }

                    else
                    {
                        genericTransDetailsVO.setRedirectUrl(redirectUrl);
                        commonValidatorVO.setLogoName(logoName);
                        commonValidatorVO.setPartnerName(partnerName);
                        //merchantDetailsVO.setPoweredBy(isPowerBy);
                        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                        addressDetailsVO.setTmpl_amount(tmpl_amt);
                        addressDetailsVO.setTmpl_currency(tmpl_currency);
                        //commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

                        merchantDetailsVO = merchantDAO.getMemberDetails(toid);
                        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

                        transactionLogger.debug("trackingid---" + commonValidatorVO.getTrackingid() + "-Description--" + commonValidatorVO.getTransDetailsVO().getOrderId() + "-Amount-" + commonValidatorVO.getTransDetailsVO().getAmount() + "-status-" + status);
                        transactionLogger.debug("trackingid---" + transactionTrackingId + "-Description--" + description + "-Amount-" + amount + "-status-" + status + "-" + commResponseVO.getDescriptor());

                        session.setAttribute("ctoken", ctoken);
                        req.setAttribute("transDetail", commonValidatorVO);
                        req.setAttribute("responceStatus", status);
                        req.setAttribute("displayName", GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                        Functions functions = new Functions();
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

            }
            catch (PZTechnicalViolationException tve)
            {
                log.error("PZTechnicalViolationException in AuxpayFrontEndNotification", tve);
            }
            catch (PZDBViolationException tve)
            {
                log.error("PZDBViolationException in AuxpayFrontEndNotification",tve);
            }
            /*catch (SystemError se)
            {
                log.error("error::::",se);
                transactionLogger.error("error::::",se);
                PZExceptionHandler.raiseAndHandleDBViolationException("AuxpayFrontendNotification.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toid, null);
            }*/
            catch (SQLException e)
            {
                log.error("error::::",e);
                transactionLogger.error("error::::",e);
                PZExceptionHandler.raiseAndHandleDBViolationException("AuxpayFrontendNotification.java","doService()",null,"Transaction",null, PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause(),toid,null);
            }
            catch (NoSuchAlgorithmException ne)
            {
                log.error("error:::",ne);
                transactionLogger.error("error:::",ne);
                PZExceptionHandler.raiseAndHandleTechnicalViolationException("RBFrontendNotification.java", "doService()", null, "Transaction", null, PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, ne.getMessage(), ne.getCause(), toid, null);
            }
            /*finally
            {
                Database.closeConnection(con);
            }*/
        }

    }
}
