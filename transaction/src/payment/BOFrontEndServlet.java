package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.common.core.CommRequestVO;
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
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Admin on 8/21/2017.
 */
public class BOFrontEndServlet extends PzServlet
{
    private static Logger log = new Logger(BOFrontEndServlet.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(BOFrontEndServlet.class.getName());
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
        log.error("-------Enter in doService of BOFrontEndServlet---ip address----" + req.getHeader("X-Forwarded-For"));
        transactionLogger.debug("-------Enter in doService of BOFrontEndServlet-------" + req.getHeader("X-Forwarded-For"));
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        HttpSession session = req.getSession(true);

        CommResponseVO commResponseVO = new CommResponseVO();
        CommRequestVO commRequestVO = new CommRequestVO();
        Functions functions = new Functions();
        for (Object key : req.getParameterMap().keySet())
        {
            log.error("----for loop BOFrontEndServlet-----" + key + "=" + req.getParameter((String) key) + "--------------");
            transactionLogger.error("----for loop BOFrontEndServlet-----" + key + "=" + req.getParameter((String) key) + "--------------");
        }

        String gResult = req.getParameter("results");
        String gTransId = "";
        String trackingid = "";
        String responseCode = "";
        if(functions.isValueNull(req.getParameter("tranid")))
        {
            gTransId = req.getParameter("tranid");
        }
        else
        {
            gTransId = req.getParameter("transid");
        }

        if(functions.isValueNull(req.getParameter("trackid")))
        {
            trackingid = req.getParameter("trackid");
        }
        else
        {
            trackingid = req.getParameter("trackids");
        }
        if(functions.isValueNull(req.getParameter("responsecode")))
        {
            responseCode = req.getParameter("responsecode");
        }
        else
        {
            responseCode = req.getParameter("responsecodes");
        }
        //String authCode = req.getParameter("auths");

        ActionEntry entry = new ActionEntry();
        AuditTrailVO auditTrailVO=new AuditTrailVO();

        TransactionManager transactionManager=new TransactionManager();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO=new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        MerchantDAO merchantDAO=new MerchantDAO();

        Connection con = null;
        PreparedStatement p = null;
        ResultSet rs = null;

        String toid = "";
        String description = "";
        String redirectUrl = "";
        String accountId = "";
        String orderDesc = "";
        String currency = "";
        String clkey = "";
        String checksumAlgo = "";
        String checksumNew = "";
        String autoredirect = "";
        String isPowerBy = "";
        String logoName = "";
        String partnerName = "";
        String partnerId = "";
        String amount = "";
        String status = "";
        String dbTrackingid = "";
        String dbStatus = "";
        String isService = "";
        String kStatus = "N";
        String billingDesc = "";
        String isTokenizationAllowed = "N";
        String isAddressDetailsRequired= "N";
        String token="";

        String tmpl_amt = "";
        String tmpl_currency = "";

        try
        {
            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingid);

            toid = transactionDetailsVO.getToid();
            description = transactionDetailsVO.getDescription();
            redirectUrl = transactionDetailsVO.getRedirectURL();
            accountId = transactionDetailsVO.getAccountId();
            orderDesc = transactionDetailsVO.getOrderDescription();
            currency = transactionDetailsVO.getCurrency();
            amount = transactionDetailsVO.getAmount();
            dbStatus = transactionDetailsVO.getStatus();

            tmpl_amt = transactionDetailsVO.getTemplateamount();
            tmpl_currency = transactionDetailsVO.getTemplatecurrency();

            con = Database.getConnection();

            String query = "select m.clkey,checksumalgo,m.autoredirect,m.partnerid,logoName,m.isPoweredBy,partnerName,m.isService,m.isTokenizationAllowed,m.isAddrDetailsRequired FROM members AS m,partners AS p WHERE m.partnerId=p.partnerId AND m.memberid=?";

            p = con.prepareStatement(query);
            p.setString(1, toid);
            rs = p.executeQuery();
            log.error("selected toid from trackingid---" + p);
            //rs = p.executeQuery();
            if (rs.next())
            {
                clkey = rs.getString("clkey");
                checksumAlgo = rs.getString("checksumalgo");
                autoredirect = rs.getString("autoredirect");
                isPowerBy = rs.getString("isPoweredBy");
                logoName = rs.getString("logoName");
                partnerName = rs.getString("partnerName");
                partnerId = rs.getString("partnerId");
                isService = rs.getString("isService");
                isTokenizationAllowed = rs.getString("isTokenizationAllowed");
                isAddressDetailsRequired = rs.getString("isAddrDetailsRequired");
            }

            //TODO add token generation

            checksumNew = Checksum.generateChecksumForStandardKit(trackingid, description, String.valueOf(amount), kStatus, clkey);

            commonValidatorVO.setTrackingid(trackingid);
            genericTransDetailsVO.setOrderId(description);
            genericTransDetailsVO.setAmount(amount);
            genericTransDetailsVO.setCurrency(currency);
            genericTransDetailsVO.setOrderDesc(orderDesc);
            String respstatus = status;

            auditTrailVO.setActionExecutorId(toid);
            auditTrailVO.setActionExecutorName("Customer3D");

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date date = new Date();
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            commResponseVO.setTransactionId(gTransId);
            commResponseVO.setErrorCode(responseCode);

            commResponseVO.setAuthCode(responseCode);

            StringBuffer sb = new StringBuffer();
            sb.append("update transaction_common set ");

            if ("authstarted".equalsIgnoreCase(dbStatus))
            {
                if (responseCode.equals("000"))
                {
                    billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    commResponseVO.setDescription("Transaction Successful");
                    if ("Y".equalsIgnoreCase(isService) && (!functions.isValueNull(transactionDetailsVO.getTransactionType()) || "DB".equalsIgnoreCase(transactionDetailsVO.getTransactionType())))
                    {

                        respstatus = "Successful Transaction";
                        log.debug("---status in BOFrontEndServlet---" + respstatus);
                        sb.append(" captureamount='" + amount + "'");
                        sb.append(", paymentid='" + gTransId + "'");
                        sb.append(", remark='" + respstatus + "'");
                        sb.append(", status='capturesuccess'");
                        commResponseVO.setRemark(respstatus);
                        commResponseVO.setStatus("success");
                        commResponseVO.setTransactionType("sale");
                        commResponseVO.setDescriptor(billingDesc);
                        entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                    }
                    else
                    {
                        respstatus = "Authorization Successful";
                        log.debug("---status in BOFrontEndServlet---" + respstatus);
                        sb.append(" amount='" + amount + "'");
                        sb.append(", paymentid='" + gTransId + "'");
                        sb.append(", remark='" + respstatus + "'");
                        sb.append(", status='authsuccessful'");
                        commResponseVO.setRemark(respstatus);
                        commResponseVO.setTransactionType("auth");
                        commResponseVO.setStatus("success");
                        commResponseVO.setDescriptor(billingDesc);
                        entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                    }


                }
                else
                {
                    respstatus = "Transaction Declined";
                    log.debug("---status in BOFrontEndServlet---" + respstatus);
                    sb.append(" amount='" + amount + "'");
                    sb.append(", paymentid='" + gTransId + "'");
                    sb.append(", remark='" + respstatus + "'");
                    sb.append(", status='authfailed'");
                    commResponseVO.setRemark(respstatus);
                    commResponseVO.setStatus("fail");
                    commResponseVO.setDescription("Transaction Failed");
                    entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.ACTION_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                }
                sb.append(" where trackingid = " + trackingid);
                log.debug("common update query BOFrontEndServlet---" + sb.toString());
                Database.executeUpdate(sb.toString(), con);
            }
            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
            asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), status, null, null);

            Map saleMap = new TreeMap();

            saleMap.put("desc", description);
            saleMap.put("checksum", checksumNew);
            saleMap.put("amount", amount);
            saleMap.put("trackingid", trackingid);
            saleMap.put("status", kStatus);
            saleMap.put("descriptor", billingDesc);
            saleMap.put("token", token);
            saleMap.put("tmpl_currency", tmpl_currency);
            saleMap.put("tmpl_amt", tmpl_amt);

            log.error("----autoredirect----" + autoredirect + "---redirecturl----" + redirectUrl);
            log.error("BOFrontEndServlet values------" + description + "-" + checksumNew + "-" + amount + "-" + trackingid + "-" + kStatus);

            if ("Y".equalsIgnoreCase(autoredirect))
            {
                log.error("inside autoredirect----" + autoredirect);
                String redirect = generateAutoSubmitForm(redirectUrl, saleMap);
                res.setContentType("text/html;charset=UTF-8");
                res.setCharacterEncoding("UTF-8");
                //System.out.println("redirect---" + redirect);
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
                log.error("inside else autoredirect----" + autoredirect);
                genericTransDetailsVO.setRedirectUrl(redirectUrl);
                commonValidatorVO.setLogoName(logoName);
                commonValidatorVO.setPartnerName(partnerName);
                addressDetailsVO.setTmpl_amount(tmpl_amt);
                addressDetailsVO.setTmpl_currency(tmpl_currency);
                //merchantDetailsVO.setPoweredBy(isPowerBy);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                //commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

                merchantDetailsVO = merchantDAO.getMemberDetails(toid);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setToken(token);

                session.setAttribute("ctoken", ctoken);
                req.setAttribute("transDetail", commonValidatorVO);
                req.setAttribute("responceStatus", respstatus);
                req.setAttribute("displayName", commResponseVO.getDescriptor());
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
        catch (PZDBViolationException dbe)
        {
            log.error("error:::",dbe);
            transactionLogger.error("error:::",dbe);
            PZExceptionHandler.handleDBCVEException(dbe, toid, null);
        }
        catch (SystemError se)
        {
            log.error("error:::",se);
            transactionLogger.error("error:::",se);
            PZExceptionHandler.raiseAndHandleDBViolationException("BOFrontEndServlet.java","doService()",null,"Transaction",null, PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause(),toid,null);
        }
        catch (SQLException e)
        {
            log.error("error:::",e);
            transactionLogger.error("error:::",e);
            PZExceptionHandler.raiseAndHandleDBViolationException("BOFrontEndServlet.java","doService()",null,"Transaction",null, PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause(),toid,null);
        }
        catch (NoSuchAlgorithmException ne)
        {
            log.error("error:::",ne);
            transactionLogger.error("error:::",ne);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("BOFrontEndServlet.java","doService()",null,"Transaction",null, PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null,ne.getMessage(),ne.getCause(),toid,null);
        }
        finally
        {
            Database.closePreparedStatement(p);
            Database.closeResultSet(rs);
            Database.closeConnection(con);
        }
    }

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
        return html.toString();
    }
}
