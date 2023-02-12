package payment;

import com.directi.pg.Checksum;
import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Oct 24, 2012
 * Time: 12:29:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class CupFrontendServlet extends PzServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(CupFrontendServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);
    public CupFrontendServlet()
    {
        super();
    }

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
        transactionLogger.debug("-------Enter in doService of CupFrontServlet-------");
        transactionLogger.debug("---Backend IpAddress---" + req.getRemoteAddr());
        transactionLogger.debug("---Backend HOST---" + req.getRemoteHost());
        transactionLogger.debug("---Backend URI---"+req.getRequestURI());
        transactionLogger.debug("---Backend URL---" + req.getRequestURL());

        Connection con = null;
        PreparedStatement p= null;
        ResultSet rs= null;

        String headername = "";
        for(Enumeration e = req.getHeaderNames();
            e.hasMoreElements();)
        {
            headername = (String)e.nextElement();
            transactionLogger.debug("----frontend---"+req.getHeader(headername) + "<br/>");
        }

        res.setContentType("text/html");
        HttpSession session = req.getSession(true);

        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO=new GenericTransDetailsVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        MerchantDAO merchantDAO=new MerchantDAO();
        if (!req.getParameterMap().isEmpty())
        {
            String respCode = ESAPI.encoder().encodeForHTML(req.getParameter("respCode"));
            String message = ESAPI.encoder().encodeForHTML(req.getParameter("respMsg"));
            String amountInCents = ESAPI.encoder().encodeForHTML(req.getParameter("settleAmount"));
            String captureAmount = amountInCents.substring(0,amountInCents.length()-2) + "." + amountInCents.substring(amountInCents.length()-2);
            //int amount = Integer.parseInt(captureAmount)/100;

            transactionLogger.debug("RespCode---"+respCode + "message---"+message);

            String status = null;
            if(respCode.equals("00")){
                status="Y";
            }
            else{
                status="N";
            }

            transactionLogger.debug("Status CupFrontEnd-------------------------------" + status);
            String orderId = ESAPI.encoder().encodeForHTML(req.getParameter("orderNumber"));

            String trackingId =null;
            String orderNumber = null;
            String toId = null;
            String redirectUrl = null;
            String clkey = null;
            String checksumAlgo = null;
            String autoredirect = "";
            String checksum = null;
            String accountid = "";
            String orderdescription = "";
            String currency = "";
            String logoName = "";
            String partnerName = "";
            String partnerId = "";
            String isPowerBy = "";
            String displayName = "";
            try
            {
                con = Database.getConnection();
                String sql = "select trackingid from transaction_common_details where detailid = ? ";
                p=con.prepareStatement(sql);
                p.setString(1,orderId);
                rs=p.executeQuery();
                while(rs.next())
                {
                    trackingId= rs.getString("trackingid");
                }
                sql = "select toid, description, redirecturl, t.accountid, orderdescription, currency, displayname from transaction_common AS t,gateway_accounts AS g where trackingid = ? AND t.accountid=g.accountid";
                p=con.prepareStatement(sql);
                p.setString(1,trackingId);
                rs=p.executeQuery();
                while(rs.next())
                {
                    toId = rs.getString("toid");
                    orderNumber= rs.getString("description");
                    redirectUrl= rs.getString("redirecturl");
                    accountid = rs.getString("accountid");
                    orderdescription = rs.getString("orderdescription");
                    currency = rs.getString("currency");
                    displayName = rs.getString("displayname");
                }
                transactionLogger.debug("Accountid--"+accountid);
                String query = "select m.clkey,checksumalgo,autoredirect,m.partnerId,m.isPoweredBy,p.partnerName,p.logoName from members AS m,partners AS p where m.partnerId=p.partnerId AND memberid=?";
                p=con.prepareStatement(query);
                p.setString(1,toId);
                rs = p.executeQuery();
                if (rs.next())
                {
                    clkey = rs.getString("clkey");
                    checksumAlgo = rs.getString("checksumalgo");
                    autoredirect = rs.getString("autoredirect");
                    logoName = rs.getString("logoName");
                    partnerId = rs.getString("partnerId");
                    isPowerBy = rs.getString("isPoweredBy");
                    partnerName = rs.getString("partnerName");
                }
                checksum = Checksum.generateChecksumV2(orderNumber, String.valueOf(captureAmount), status, clkey, checksumAlgo);
            }
            catch(Exception e)
            {
                transactionLogger.debug("Error while processing transaction response:"+e);
                transactionLogger.error("Error in CupFrontendServlet...",e);
            }
            finally
            {
                Database.closePreparedStatement(p);
                Database.closeResultSet(rs);
                Database.closeConnection(con);
            }

            Map<String, String> map = new TreeMap<String, String>();
            String billingDesc = "";
            if("Y".equals(status))
            {
                billingDesc = displayName;
            }
            map.put("status", status);
            map.put("message", message);
            map.put("desc", orderNumber);
            map.put("amount", String.valueOf(captureAmount));
            map.put("checksum", checksum);
            map.put("trackingid", trackingId);
            map.put("currency",currency);
            map.put("descriptor",billingDesc);

            transactionLogger.debug("map of CupFrontEnd--------"+map.toString());

            if("Y".equalsIgnoreCase(autoredirect))
            {
                String html = generateAutoSubmitForm(redirectUrl,map);
                res.setContentType("text/html;charset=UTF-8");
                res.setCharacterEncoding("UTF-8");
                try
                {
                    res.getWriter().write(html);
                }
                catch (IOException e)
                {
                    transactionLogger.error("Exception while redirecting to merchant redirectUrl!",e);
                }
            }
            else
            {
                commonValidatorVO.setTrackingid(trackingId);
                genericTransDetailsVO.setOrderId(orderNumber);
                genericTransDetailsVO.setAmount(captureAmount);
                genericTransDetailsVO.setCurrency(currency);
                genericTransDetailsVO.setOrderDesc(orderdescription);
                String respstatus = message;
                if("Pay Approved".equalsIgnoreCase(message))
                {
                    respstatus = "Successful ("+message+")";
                }
                req.setAttribute("responceStatus", respstatus);
                req.setAttribute("displayName", displayName);
                genericTransDetailsVO.setRedirectUrl(redirectUrl);
                commonValidatorVO.setLogoName(logoName);
                commonValidatorVO.setPartnerName(partnerName);
                merchantDetailsVO.setPoweredBy(isPowerBy);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                try
                {
                    merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                }
                catch (PZDBViolationException e)
                {
                    transactionLogger.debug("Error while processing transaction response:"+e);
                    transactionLogger.error("Error in CupFrontendServlet...",e);
                }
                session.setAttribute("ctoken",ctoken);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                req.setAttribute("transDetail", commonValidatorVO);
                Functions functions = new Functions();
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
            res.setStatus(HttpServletResponse.SC_OK);
            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
            asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, null,null);

        }
    }

}
