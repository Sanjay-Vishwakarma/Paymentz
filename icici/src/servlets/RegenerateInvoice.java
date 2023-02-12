import com.directi.pg.*;
import com.invoice.dao.InvoiceEntry;
import com.invoice.vo.InvoiceVO;
import com.invoice.vo.ProductList;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Dhiresh
 * Date: 30/12/12
 * Time: 1:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class RegenerateInvoice extends HttpServlet
{

    private static Logger log = new Logger(RegenerateInvoice.class.getName());
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
        String orderid="";
        String validationError="";
         String custemail="";
        Hashtable paymodelist = new Hashtable();
        InvoiceEntry invoiceEntry= new InvoiceEntry();
        InvoiceVO invoiceVO = new InvoiceVO();
        List<ProductList> listOfProduct = new ArrayList<ProductList>();
        log.debug("invoice no "+invoiceno+" is being regenerated");

        Hashtable hiddenvariables= invoiceEntry.getInvoiceDetails(invoiceno);
        invoiceVO = invoiceEntry.getInvoiceConfigurationDetails(String.valueOf(hiddenvariables.get("memberid")));
        hiddenvariables.put("orderdesc","Duplicate order for "+hiddenvariables.get("orderid")+"");

        log.debug("Hashtable found is \n"+hiddenvariables.toString());

        orderid = invoiceEntry.getOrderId(String.valueOf(hiddenvariables.get("memberid")));
        orderid = invoiceVO.getInitial() + "_" + String.valueOf(hiddenvariables.get("memberid")) + orderid;
        hiddenvariables.put("orderid",orderid);
        /*if(!ESAPI.validator().isValidInput("orderid", req.getParameter("orderid"),"Address",100,false)||( req.getParameter("orderid")).equalsIgnoreCase((String)hiddenvariables.get("orderid")))
        {
            validationError+="Error in OrderID Kindly Enter Unique Order ID"+"<BR>";
        }
        else
        {
            orderid=req.getParameter("orderid");
            hiddenvariables.put("orderid",req.getParameter("orderid"));
        }*/


        if(!ESAPI.validator().isValidInput("email", req.getParameter("custemail"),"Email",100,false))
        {
            validationError+="Error in Email Address";
        }
        else
        {
           custemail=req.getParameter("custemail");
           hiddenvariables.put("custemail",req.getParameter("custemail"));
        }

        if(!ESAPI.validator().isValidInput("paymodeid",req.getParameter("paymenttype"),"SafeString",30,true))
        {
            log.debug("Invalid paymodeid");
            validationError+="Payment type should is invalid";
        }
        else
            hiddenvariables.put("paymodeid",req.getParameter("paymenttype"));



        RequestDispatcher rd=null;

        log.debug("validation error-----"+validationError);
        if(!validationError.equals(""))
        {   log.debug("validation Error");

            String toid = (String) hiddenvariables.get("memberid");
            log.debug(toid);

            paymodelist = invoiceEntry.getPayModeList(toid);

            rd = req.getRequestDispatcher("/regenerateInvoiceConfirm.jsp?ctoken="+user.getCSRFToken());
        }
        else{


            String ctoken=  ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);


            invoiceEntry.regenerateInvoice(invoiceno);

            hiddenvariables.put("orderid",orderid);
            hiddenvariables.put("custemail",custemail);
            hiddenvariables.put("ctoken",ctoken);
            hiddenvariables.put("ipaddress",Functions.getIpAddress(req));
            listOfProduct = invoiceEntry.getItemList(invoiceno);
            hiddenvariables.put("listofproducts",listOfProduct);
            hiddenvariables.put("invoicevo",invoiceVO);
            hiddenvariables.put("useraccname", hiddenvariables.get("raisedBy"));


            log.debug(hiddenvariables.toString());
            Hashtable tempHash =invoiceEntry.insertInvoice(hiddenvariables,true);
            invoiceno = ((Integer) tempHash.get("invoiceno"))+"";


            String query="select timestamp from invoice where invoiceno ="+invoiceno+";";
            String date=null,time=null;
            PreparedStatement p = null;
            ResultSet rs = null;
            try
            {
                con= Database.getConnection();
                p=con.prepareStatement(query);
                rs =p.executeQuery();
                if(rs.next())
                {  date=rs.getString("timestamp").substring(0,rs.getString("timestamp").lastIndexOf(" "));
                    time=rs.getString("timestamp").substring(rs.getString("timestamp").lastIndexOf(" "),rs.getString("timestamp").length());
                }
            }catch(Exception e)
            {
                log.error("Exception occured while inserting data",e);
            }
            finally
            {
                Database.closeResultSet(rs);
                Database.closePreparedStatement(p);
                Database.closeConnection(con);
            }




            log.debug("checkpoint1");
            hiddenvariables.put("invoiceno",invoiceno);

            hiddenvariables.put("date",date);
            hiddenvariables.put("time",time);





            rd= req.getRequestDispatcher("/invoiceGenerated.jsp?ctoken="+user.getCSRFToken());
        }

        req.setAttribute("hiddenvariables",hiddenvariables);
        req.setAttribute("validationError",validationError);
        req.setAttribute("paymodelist",paymodelist);
        log.debug("forwarding request");
        rd.forward(req, res);

    }
}
