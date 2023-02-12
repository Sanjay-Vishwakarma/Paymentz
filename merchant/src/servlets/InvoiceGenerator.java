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
import java.util.Hashtable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Dhiresh
 * Date: 28/12/12
 * Time: 1:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class InvoiceGenerator extends HttpServlet
{

    private static Logger log = new Logger(InvoiceGenerator.class.getName());
    private TransactionLogger transactionLogger = new TransactionLogger(InvoiceGenerator.class.getName());
    Connection conn = null;
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException,IOException
    {
        log.debug("Enter in InvoiceGenerator");
        HttpSession session = req.getSession();
        Merchants merchants = new Merchants();

        if (!merchants.isLoggedIn(session))
        {   log.debug("member is logout ");
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        log.debug("CSRF check successful");
        
        //variabe list
        Connection conn=null;
        String header="Client ="+req.getRemoteAddr()+":"+req.getServerPort()+",X-Forwarded="+req.getServletPath();
        int invoiceno=0;
        Hashtable hiddenvariables = new Hashtable();
        Functions functions = new Functions();
        String error="";
        String memberid ="";
        String orderid="";
        String orderdesc="";
        String address="";
        String currency="";
        String amount="";  //Amount
        String custname="";
        String city="";
        String zipcode="";
        String state="";
        String country="";
        String phonecc="";
        String phone="";
        String custemail="";
        String totype=(String) session.getAttribute("company");
        String redirecturl=new String();
        String expPeriod = "";
        String expTime = "";
        String taxAmount = req.getParameter("taxamount");
        String defaultLanguage = req.getParameter("defaultLanguage");


        if (!ESAPI.validator().isValidInput("expTime",(String) req.getParameter("expTime"),"Numbers",5,false))
        {   log.debug("PLS enter Valid Expiry Time");
            error = error + "Please enter Valid Expiry Time.";
        }
        else
        {
            expTime=(String)req.getParameter("expTime");
            expPeriod = (String)req.getParameter("expPeriod");
        }

        if (!ESAPI.validator().isValidInput("url",(String) req.getParameter("redirecturl"),"URL",100,false))
        {   log.debug("PLS enter Valid Redirect URL");
            error = error + "Please enter Valid Redirect URL.";
        }
        else
            redirecturl=(String)req.getParameter("redirecturl");

        //validate all requested variables
        if(!ESAPI.validator().isValidInput("memberid",req.getParameter("memberid"),"Numbers",30,false))
        {
            log.debug("Invalid memberid");
            error = error + "Invalid Merchant Id";
        }
        else
        {
            memberid = req.getParameter("memberid");
        }

        if(!ESAPI.validator().isValidInput("orderid",req.getParameter("orderid"),"SafeString",30,false))
        {
            log.debug("Invalid orderid");
            error = error + "Invalid Order Id ";
        }
        else
        {
            orderid = req.getParameter("orderid");
        }

        if(!ESAPI.validator().isValidInput("orderdesc",req.getParameter("orderdesc"),"SafeString",30,true))
        {
            log.debug("Invalid order description");
            error = error + "Invalid Order Description";
        }
        else
        {
            orderdesc = req.getParameter("orderdesc");
        }
        log.debug("Checkpoint 1");
        if(!ESAPI.validator().isValidInput("amount",req.getParameter("amount"),"Numbers",30,false) || (req.getParameter("amount").equalsIgnoreCase("0.00") || req.getParameter("amount").equalsIgnoreCase("0")))
        {
            log.debug("Invalid amount");
            error = error + "Invalid Amount";
        }
        else
        {
            amount = req.getParameter("amount");
        }

        if(!ESAPI.validator().isValidInput("custname",req.getParameter("custname"),"SafeString",30,true))
        {
          log.debug("Invalid name");
          error = error + "Invalid Name";
        }
        else
        {
            custname = req.getParameter("custname");
        }
        if(!ESAPI.validator().isValidInput("address",req.getParameter("address"),"Address",100,true))
        {
            log.debug("Invalid address");
            error = error + "Invalid Address";
        }
        else
        {
            address = req.getParameter("address");
        }
        if(!ESAPI.validator().isValidInput("city",req.getParameter("city"),"SafeString",30,true))
        {
            log.debug("Invalid city");
            error = error + "Invalid City";
        }
        else
        {
            city = req.getParameter("city");
        }
        if(!ESAPI.validator().isValidInput("zipcode",req.getParameter("zipcode"),"SafeString",30,true))
        {
            log.debug("Invalid Zip");
            error = error + "Invalid Zip Code";
        }
        else
        {
            zipcode = req.getParameter("zipcode");
        }

        if(!ESAPI.validator().isValidInput("state",req.getParameter("state"),"SafeString",30,true))
        {
            log.debug("Invalid state");
            error = error + "Invalid State";
        }
        else
        {
            state = req.getParameter("state");
        }

        if(!ESAPI.validator().isValidInput("countrycode",req.getParameter("countrycode"),"SafeString",30,true))
        {
            log.debug("Invalid countrycode");
            error = error + "Invalid Country Code";
        }
        else
        {
            country = req.getParameter("country");
        }
            log.debug("Checkpoint 2");
        if(!ESAPI.validator().isValidInput("phonecc",req.getParameter("phonecc"),"Phone",30,true))
        {
            log.debug("Invalid phone");
            error = error + "Invalid Country number";
        }
        else
        {
            phonecc = req.getParameter("phonecc");
        }

        if(!ESAPI.validator().isValidInput("phone",req.getParameter("phone"),"Phone",30,true))
        {
            log.debug("Invalid phone");
            error = error + "Invalid Phone Number";
        }
        else
        {
            phone = req.getParameter("phone");
        }

        if((!ESAPI.validator().isValidInput("custemail",req.getParameter("custemail"),"Email",50,true))||((String)req.getParameter("custemail")).equals(""))
        {
            log.debug("Invalid email");
            error = error + "Invalid Email";
        }
        else
        {
            custemail = req.getParameter("custemail");
        }

        if((!ESAPI.validator().isValidInput("currency",req.getParameter("currency"),"SafeString",30,true))/*||((String)req.getParameter("currency")).equals("")*/)
        {
            log.debug("Invalid currency");
            error = error + "Invalid currency";
        }
        else
        {
            currency = req.getParameter("currency");
        }

        if(!error.equals(""))
        {
            {
                req.setAttribute("error",error);
                log.debug("forwarding request to invoice generate");
                RequestDispatcher rd = req.getRequestDispatcher("/servlet/GenerateInvoice?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
            }
        }
        else
        {
            hiddenvariables.put("memberid",memberid);
            hiddenvariables.put("totype",totype);
            hiddenvariables.put("amount",amount);
            hiddenvariables.put("orderid",orderid);
            hiddenvariables.put("orderdesc",orderdesc);
            hiddenvariables.put("redirecturl",redirecturl);
            hiddenvariables.put("currency",currency);
            hiddenvariables.put("custemail",custemail);
            hiddenvariables.put("custname",custname);
            if (functions.isValueNull(req.getParameter("paymodeid")) || functions.isValueNull(req.getParameter("cardtype")) || functions.isValueNull(req.getParameter("terminalid")))
            {
                hiddenvariables.put("paymodeid", (String) req.getParameter("paymodeid"));
                hiddenvariables.put("terminalid", (String) req.getParameter("terminalid"));
                hiddenvariables.put("cardtype", (String) req.getParameter("cardtype"));
            }
            hiddenvariables.put("address",address);
            hiddenvariables.put("city",city);
            hiddenvariables.put("zipcode",zipcode);
            hiddenvariables.put("state",state);
            hiddenvariables.put("country",country);
            hiddenvariables.put("phonecc",phonecc);
            hiddenvariables.put("phone",phone);
            hiddenvariables.put("taxamount",taxAmount);
            String ctoken= ESAPI.randomizer().getRandomString(16, DefaultEncoder.CHAR_ALPHANUMERICS);
            hiddenvariables.put("ctoken",ctoken);
            String reqIp = "";
            String remoteAddr = Functions.getIpAddress(req);
            if(remoteAddr.contains(","))
            {
                String sIp[] = remoteAddr.split(",");
                reqIp = sIp[0].trim();
            }
            else
            {
                reqIp = remoteAddr;
            }
            hiddenvariables.put("ipaddress", reqIp);
            hiddenvariables.put("expirationPeriod",expPeriod);
            hiddenvariables.put("useraccname",user.getAccountName());
            hiddenvariables.put("listofproducts",(List<ProductList>)session.getAttribute("listofproducts"));
            hiddenvariables.put("langForInvoice",defaultLanguage);

            InvoiceEntry invoiceEntry = new InvoiceEntry();
            InvoiceVO invoiceVO = new InvoiceVO();
            invoiceVO = invoiceEntry.getInvoiceConfigurationDetails(memberid);
            hiddenvariables.put("invoicevo",invoiceVO);
            Hashtable tempHash =invoiceEntry.insertInvoice(hiddenvariables,false);
            session.removeAttribute("listofproducts");
            invoiceno = (Integer) tempHash.get("invoiceno");
            String query="select timestamp from invoice where invoiceno ="+invoiceno+";";
            String date=null,time=null;
            PreparedStatement p = null;
            ResultSet rs = null;
            try
            {
                conn=Database.getConnection();
                p=conn.prepareStatement(query);
                rs =p.executeQuery();
                if(rs.next())
                {  date=rs.getString("timestamp").substring(0,rs.getString("timestamp").lastIndexOf(" "));
                    time=rs.getString("timestamp").substring(rs.getString("timestamp").lastIndexOf(" "),rs.getString("timestamp").length());
                }
                rs.close();
            }
            catch(Exception e)
            {
                log.error("Exception occured while inserting data",e);
            }
            finally
            {
                Database.closeResultSet(rs);
                Database.closePreparedStatement(p);
                Database.closeConnection(conn);
            }

            log.debug("checkpoint1");
            hiddenvariables.put("invoiceno",invoiceno);
            hiddenvariables.put("date",date);
            hiddenvariables.put("time",time);

        ctoken=user.getCSRFToken();
        req.setAttribute("hiddenvariables",hiddenvariables);
        log.debug("creating rd");
        RequestDispatcher rd = req.getRequestDispatcher("/invoiceGenerated.jsp?ctoken="+ctoken);
        log.debug("forwarding request to invoice generated");
        rd.forward(req, res);
        }
    }
}
