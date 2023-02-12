package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
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
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by 123 on 1/8/2016.
 */
public class PaysecFrontendNotification extends PzServlet
{
    private static Logger log = new Logger(PaysecFrontendNotification.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PaysecFrontendNotification.class.getName());
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
        log.debug("DATA--->"+html);
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
        log.debug("-------Enter in doService of PaysecFrontendNotification---ip address----" + req.getHeader("X-Forwarded-For"));
        log.error("-------Enter in doService of PaysecFrontendNotification---ip address----" + req.getHeader("X-Forwarded-For"));
        transactionLogger.error("-------Enter in doService of PaysecFrontendNotification-------" + req.getHeader("X-Forwarded-For"));
        transactionLogger.error("-------PaysecFrontendNotification-------");

        for(Object key : req.getParameterMap().keySet())
        {
            log.debug("----for loop PaysecFrontendNotification-----"+key+"="+req.getParameter((String)key)+"--------------");
            transactionLogger.error("----for loop PaysecFrontendNotification-----" + key + "=" + req.getParameter((String) key) + "--------------");
        }

        String responseTransactionId = req.getParameter("oid");
        String responseStatus = req.getParameter("status");
        String cartId = req.getParameter("cartid");

        TransactionManager transactionManager = new TransactionManager();
        ActionEntry entry = new ActionEntry();
        CommResponseVO commResponseVO = new CommResponseVO();
        AuditTrailVO auditTrailVO = new AuditTrailVO();

        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO=new GenericTransDetailsVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        MerchantDAO merchantDAO=new MerchantDAO();
        Functions functions = new Functions();

        HttpSession session = req.getSession(true);

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
        String isPowerBy = "";
        String logoName = "";
        String partnerName = "";
        String partnerId = "";
        String confirmStatus = "";
        String cStatus = "";

        String orderDesc = "";
        String currency = "";

        /*String resDesc = "";
        String resErrorCode = "";*/

        /*String resStaus[] = responseStatus.split("-");
        if(resStaus.length==2)
        {
            resDesc = resStaus[1];
            resErrorCode = resStaus[0];
        }
        transactionLogger.error("resDesc---"+resDesc+"--resErrorCode--"+resErrorCode);*/
        if(functions.isValueNull(responseStatus))
        {
            try
            {


                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(cartId);

                toid = transactionDetailsVO.getToid();
                accountId = transactionDetailsVO.getAccountId();
                dbStatus = transactionDetailsVO.getStatus();
                amount = transactionDetailsVO.getAmount();
                description = transactionDetailsVO.getDescription();
                redirectUrl = transactionDetailsVO.getRedirectURL();
                orderDesc = transactionDetailsVO.getOrderDescription();
                currency = transactionDetailsVO.getCurrency();

                auditTrailVO.setActionExecutorName("Customer");
                auditTrailVO.setActionExecutorId(toid);

                StringBuffer dbBuffer = new StringBuffer();

                try
                {
                    con = Database.getConnection();
                    //String query = "select m.clkey,autoredirect,m.partnerid,logoName,m.isPoweredBy,partnerName,isTokenizationAllowed,isAddrDetailsRequired FROM members AS m,partners AS p WHERE m.partnerId=p.partnerId AND m.memberid=?";
                    String query = "select m.clkey,autoredirect,m.partnerid,logoName,m.isPoweredBy,partnerName FROM members AS m,partners AS p WHERE m.partnerId=p.partnerId AND m.memberid=?";
                    p = con.prepareStatement(query);
                    p.setString(1, toid);
                    //ResultSet rs = p.executeQuery();
                    rs = p.executeQuery();
                    if (rs.next())
                    {
                        clkey = rs.getString("clkey");
                        autoredirect = rs.getString("autoredirect");
                        isPowerBy = rs.getString("isPoweredBy");
                        logoName = rs.getString("logoName");
                        partnerName = rs.getString("partnerName");
                        partnerId = rs.getString("partnerId");
                        //isAddressDetailsRequired = rs.getString("isAddrDetailsRequired");
                        //isTokenizationAllowed = rs.getString("isTokenizationAllowed");
                    }

                }
                catch (SystemError se)
                {
                    log.error("error::::", se);
                    transactionLogger.error("error::::", se);
                    PZExceptionHandler.raiseAndHandleDBViolationException("RBBackendNotification.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toid, null);
                }
                catch (SQLException se)
                {
                    log.error("error::::", se);
                    transactionLogger.error("error::::", se);
                    PZExceptionHandler.raiseAndHandleDBViolationException("RBBackendNotification.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toid, null);
                }
                finally
                {
                    Database.closePreparedStatement(p);
                    Database.closeResultSet(rs);
                    Database.closeConnection(con);
                }

                if ("authstarted".equalsIgnoreCase(dbStatus))
                {
                    commResponseVO.setTransactionId(responseTransactionId);
                    //commResponseVO.setErrorCode(resErrorCode);
                    commResponseVO.setTransactionType("sale");

                    if (responseStatus.equalsIgnoreCase("SUCCESS"))
                    {
                        status = "Success - Transaction successful";
                        cStatus = "Successful";
                        dbBuffer.append("update transaction_common set captureamount='" + amount + "', status='capturesuccess', paymentid='"+responseTransactionId+"', remark='"+status+"'");
                        commResponseVO.setTransactionStatus(status);
                        commResponseVO.setDescription(status);
                        //commResponseVO.setErrorCode(resErrorCode);

                        confirmStatus = "Y";
                        entry.actionEntryForCommon(cartId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                    }
                    else
                    {
                        status = "Failed - Transaction failed";
                        cStatus = "Failed";
                        dbBuffer.append("update transaction_common set status='authfailed', paymentid='"+responseTransactionId+"', remark='"+status+"'");
                        commResponseVO.setTransactionStatus(status);
                        commResponseVO.setDescription(status);
                        //commResponseVO.setErrorCode(resErrorCode);

                        entry.actionEntryForCommon(cartId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                    }
                    dbBuffer.append(" where trackingid = " + cartId);
                    log.error("common update query in PaysecFrontEnd---" + dbBuffer.toString());
                    try
                    {
                        con = Database.getConnection();
                        Database.executeUpdate(dbBuffer.toString(), con);
                    }
                    catch (SystemError se)
                    {
                        log.error("error::::", se);
                        transactionLogger.error("error::::", se);
                        PZExceptionHandler.raiseAndHandleDBViolationException("RBBackendNotification.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toid, null);
                    }
                    finally
                    {
                        Database.closeConnection(con);
                    }

                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(cartId), status, null,null);

                    checksumNew = Checksum.generateChecksumForStandardKit(cartId, description, String.valueOf(amount), confirmStatus, clkey);

                    commonValidatorVO.setTrackingid(cartId);
                    genericTransDetailsVO.setOrderId(description);
                    genericTransDetailsVO.setAmount(amount);
                    genericTransDetailsVO.setCurrency(currency);
                    genericTransDetailsVO.setOrderDesc(orderDesc);
                    Map saleMap = new TreeMap();
                    String respStatus = "N";
                    String billingDesc = "";
                    if(status!=null && status.contains("Successful") || status.contains("success"))
                    {
                        respStatus = "Y";
                        billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    }

                    saleMap.put("desc",description);
                    saleMap.put("checksum",checksumNew);
                    saleMap.put("amount",amount);
                    saleMap.put("trackingid",cartId);
                    saleMap.put("status",respStatus);
                    saleMap.put("descriptor",billingDesc);
                    //saleMap.put("token",token);

                    log.debug("----autoredirect----" + autoredirect + "---redirecturl----"+redirectUrl);
                    log.debug("Auxpay values------"+description+"-"+checksumNew+"-"+amount+"-"+cartId+"-"+confirmStatus);

                    if("Y".equalsIgnoreCase(autoredirect))
                    {

                        String redirect = generateAutoSubmitForm(redirectUrl,saleMap);
                        res.setContentType("text/html;charset=UTF-8");
                        res.setCharacterEncoding("UTF-8");

                        try
                        {
                            res.getWriter().write(redirect);
                        }
                        catch (IOException e)
                        {
                            log.error("IO Exception in InPayFrontEndServlet---",e);
                            transactionLogger.error("IO Exception in InPayFrontEndServlet---",e);
                        }
                    }

                    else
                    {
                        genericTransDetailsVO.setRedirectUrl(redirectUrl);
                        commonValidatorVO.setLogoName(logoName);
                        commonValidatorVO.setPartnerName(partnerName);
                        //merchantDetailsVO.setPoweredBy(isPowerBy);
                        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                        //commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

                        merchantDetailsVO = merchantDAO.getMemberDetails(toid);
                        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

                        transactionLogger.error("trackingid---" + commonValidatorVO.getTrackingid() + "-Description--" + commonValidatorVO.getTransDetailsVO().getOrderId() + "-Amount-" + commonValidatorVO.getTransDetailsVO().getAmount() + "-status-" +status);
                        transactionLogger.error("trackingid---" + cartId + "-Description--" + description + "-Amount-" + amount + "-status-" + status + "-" + commResponseVO.getDescriptor());

                        session.setAttribute("ctoken",ctoken);
                        req.setAttribute("transDetail", commonValidatorVO);
                        req.setAttribute("responceStatus", cStatus);
                        req.setAttribute("displayName", GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                        String confirmationPage = "";
                        String version = (String)session.getAttribute("version");
                        if(functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                            confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                        else
                            confirmationPage = "/confirmationpage.jsp?ctoken=";
                        session.invalidate();
                        RequestDispatcher rd = req.getRequestDispatcher(confirmationPage + ctoken);
                        rd.forward(req,res);
                    }

                }
            }
            catch (NoSuchAlgorithmException ne)
            {
                log.error("error:::",ne);
                transactionLogger.error("error:::",ne);
                PZExceptionHandler.raiseAndHandleTechnicalViolationException("PaysecFrontendNotification.java", "doService()", null, "Transaction", null, PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, ne.getMessage(), ne.getCause(), toid, null);
            }
            catch (PZDBViolationException e)
            {
                log.error("PZDBViolationException in PaysecFrontend---",e);
            }
        }
    }
}