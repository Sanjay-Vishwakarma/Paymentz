
import com.directi.pg.*;
import com.directi.pg.core.valueObjects.CupResponseVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Oct 24, 2012
 * Time: 12:29:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class CupFrontendServlet extends TransecuteServlet
{
    public CupFrontendServlet()
    {
        super();
    }
    static Logger log = new Logger(CupFrontendServlet.class.getName());
    Connection con = null;
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
        ServletContext ctx = getServletContext();
        res.setContentType("text/html");
        if (!req.getParameterMap().isEmpty()){
        String captureAmount = ESAPI.encoder().encodeForHTML(req.getParameter("settleAmount"));
        int amount = Integer.parseInt(captureAmount)/100;

        String respCode = ESAPI.encoder().encodeForHTML(req.getParameter("respCode"));
        String message = ESAPI.encoder().encodeForHTML(req.getParameter("respMsg"));
        String status = null;
        if(respCode.equals("00")){
            status="Y";
        }
        else{
            status="N";
        }
        String orderId = ESAPI.encoder().encodeForHTML(req.getParameter("orderNumber"));

        String trackingId =null;
        String orderNumber = null;
        String toId = null;
        String redirectUrl = null;
        String clkey = null;
        String checksumAlgo = null;
        String checksum = null;
        try{
            con = Database.getConnection();
            String sql = "select trackingid from transaction_common_details where detailid = ? ";
            PreparedStatement p=con.prepareStatement(sql);

            p.setString(1,orderId);
            ResultSet rs=p.executeQuery();
            while(rs.next())
            {
                trackingId= rs.getString("trackingid");

            }
            sql = "select toid, description, redirecturl from transaction_common where trackingid = ? ";
            p=con.prepareStatement(sql);

            p.setString(1,trackingId);
            rs=p.executeQuery();
            while(rs.next())
            {
                toId = rs.getString("toid");
                orderNumber= rs.getString("description");
                redirectUrl= rs.getString("redirecturl");

            }
            con = Database.getConnection();
            String query = "select clkey,checksumalgo from members where memberid=?";
            p=con.prepareStatement(query);
            p.setString(1,toId);
            rs = p.executeQuery();
            if (rs.next())
            {
                clkey = rs.getString("clkey");
                checksumAlgo = rs.getString("checksumalgo");

            }
            checksum = Checksum.generateChecksumV2(orderNumber, String.valueOf(amount), status, clkey, checksumAlgo);
        }
        catch(Exception e)
        {
            log.debug("Error while processing transaction response:"+e);
            log.error("Error in CupFrontendServlet...",e);
        }
            Map<String, String> map = new TreeMap<String, String>();
            map.put("status", status);
            map.put("message", message);
            map.put("desc", orderNumber);
            map.put("amount", String.valueOf(amount));
            map.put("newchecksum", checksum);
            map.put("trackingid", trackingId);
            String html = generateAutoSubmitForm(redirectUrl,map);
            res.setContentType("text/html;charset=UTF-8");
            res.setCharacterEncoding("UTF-8");
            try
            {
                res.getWriter().write(html);
            }
            catch (IOException e)
            {
                log.error("Exception while redirecting to merchant redirectUrl!",e);
            }
            res.setStatus(HttpServletResponse.SC_OK);
        //String redirectString = redirectUrl + "?status=" + status + "&message=" + message +  "&desc=" + orderNumber + "&amount=" + amount + "&newchecksum=" + checksum + "&trackingid=" + trackingId;
        //res.sendRedirect(redirectString);
        }
    }
        private static String generateAutoSubmitForm(String actionUrl, Map<String, String> paramMap) {
        StringBuilder html = new StringBuilder();
        html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>\n");
        html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"").append(actionUrl).append("\" method=\"post\">\n");

        for (String key : paramMap.keySet()) {
            html.append("<input type=\"hidden\" name=\"" + key + "\" id=\"" + key + "\" value=\"" + paramMap.get(key) + "\">\n");
        }
        html.append("</form>\n");
        return html.toString();
    }

}
