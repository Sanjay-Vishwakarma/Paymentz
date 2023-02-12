

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.invoice.dao.InvoiceEntry;
import org.owasp.esapi.User;

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
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: Dhiresh
 * Date: 30/12/12
 * Time: 1:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class RegenerateInvoiceConfirm extends HttpServlet
{

    private static Logger log = new Logger(RegenerateInvoiceConfirm.class.getName());
    Connection con = null;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException,IOException
    {

        log.debug("Enter in InvoiceReGenerator");
        HttpSession session = req.getSession();
        log.debug("Enter in Invoice ");
        if (!Admin.isLoggedIn(session))
        {
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");


        log.debug("CSRF check successful ");

        String invoiceno=req.getParameter("invoiceno");
        Hashtable paymodelist = new Hashtable();
        InvoiceEntry invoiceEntry= new InvoiceEntry();

        Hashtable hiddenvariables= invoiceEntry.getInvoiceDetails(invoiceno);

        log.debug("Hashtable found is \n"+hiddenvariables.toString());
        String error="";
        String status=(String) hiddenvariables.get("status");
        String remindercounter=(String) hiddenvariables.get("remindercounter");
        String trackingid= (String) hiddenvariables.get("trackingid");
        String accountid=(String) hiddenvariables.get("accountid");
        String toid = (String) hiddenvariables.get("memberid");
        Connection conn=null;
        if(status.equalsIgnoreCase("processed"))
        {    String transStatus="";
            PreparedStatement ps = null;
            ResultSet rs = null;
            try{
                
                String tablename=Database.getTableName(GatewayAccountService.getGatewayAccount(accountid).getGateway());
                String fieldname= tablename.equalsIgnoreCase("transaction_icicicredit")?"icicitransid":"trackingid";
                conn=Database.getConnection() ;
                ps =conn.prepareStatement("select status from " + tablename + " where " + fieldname + "=?");
                ps.setString(1,trackingid);
                rs=ps.executeQuery();
                rs.next();
                transStatus = rs.getString("status");
                 log.debug(transStatus);

                log.debug(toid);

                //getting paymodelist
                paymodelist = invoiceEntry.getPayModeList(toid);
                        
                
                
            }
            catch(Exception e)
            {
                log.error("Error occured ",e);
            }
            finally
            {
                Database.closeResultSet(rs);
                Database.closePreparedStatement(ps);
                Database.closeConnection(conn);
            }



        }









        hiddenvariables.put("orderdesc","Duplicate order for "+hiddenvariables.get("orderid")+"");
        req.setAttribute("error",error);
        req.setAttribute("hiddenvariables",hiddenvariables);
        req.setAttribute("paymodelist",paymodelist);
        log.debug("creating rd");
        RequestDispatcher rd = req.getRequestDispatcher("/regenerateInvoiceConfirm.jsp?ctoken="+user.getCSRFToken());

        log.debug("forwarding request to invoice generated");
        rd.forward(req, res);

    }
}
