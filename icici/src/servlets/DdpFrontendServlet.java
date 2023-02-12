
import com.directi.pg.Checksum;
import com.directi.pg.Database;
import com.directi.pg.Logger;
import org.owasp.esapi.ESAPI;


import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Oct 24, 2012
 * Time: 12:29:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class DdpFrontendServlet extends TcServlet
{
    public DdpFrontendServlet()
    {
        super();
    }
    static Logger log = new Logger(DdpFrontendServlet.class.getName());
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
        String msg =req.getParameter("encodedMessage");

        byte[] msgByte = DatatypeConverter.parseBase64Binary(msg);
        String s = new String(msgByte);

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
        catch(Exception e){
            log.debug("Error while processing transaction response:"+e);

        }
        finally
        {
            Database.closeConnection(con);
        }
        String redirectString = redirectUrl + "?status=" + status + "&message=" + message +  "&desc=" + orderNumber + "&amount=" + amount + "&newchecksum=" + checksum + "&trackingid=" + trackingId;
        res.sendRedirect(redirectString);
    }
}
