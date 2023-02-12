import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.*;

import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.errors.ValidationException;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultEncoder;

public class NewPaymentServlet extends TcServlet
{
    static Logger logger = new Logger(NewPaymentServlet.class.getName());
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.Servlet");
    final static String WAITSERVLET = RB.getString("WAITSERVLET");
    final static String POSTPAYMENT = RB.getString("POSTPAYMENT");
    final static String VBVACTIVATION = RB.getString("VBVACTIVATION");
    final static String PROXYHOST = RB.getString("PROXYHOST");
    final static String PROXYPORT = RB.getString("PROXYPORT");
    final static String PROXYSCHEME=RB.getString("PROXYSCHEME");

    public NewPaymentServlet()
    {
        super();
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
        logger.debug("Entering in NewPaymentServlet");
        ServletContext ctx = getServletContext();
        PrintWriter pWriter = res.getWriter();
        res.setContentType("text/html");
        pWriter.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">");
        HttpSession session = req.getSession();
        Functions functions = new Functions();
        String  ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);
        session.setAttribute("ctoken",ctoken);
        req.setAttribute("ctoken",ctoken);

        String accountId = null;
        if(session== null)
        {
            //pWriter.print(Functions.ShowMessage("Message","Your session is Expire"));
            res.sendRedirect("http://www.pz.com");
            return;
        }
        if (req.getParameter("TOID") == null)
        {
           //pWriter.print(Functions.ShowMessage("Message","MerchantID is not found. Your session is Expire."));
            res.sendRedirect("http://www.pz.com");
            return;
        }

        try
        {
            validateMandatoryParameter(req);
        }
        catch(ValidationException e)
        {
          logger.error("INVALID INPUT",e);
          pWriter.println("<br><center><font color=red>Invalid Input Value</font><br></center>");
            return;
        }
        String toid = (String) req.getParameter("TOID");
        String description = (String) req.getParameter("DESCRIPTION");
        String trackingId = req.getParameter("TRACKING_ID");

        toid = toid.trim();

        String sessionID = toid + "#~#" + description;

        if (session.getAttribute(sessionID) != null)
        {
            int onOfTries = Integer.parseInt((String) session.getAttribute(sessionID));

            if (onOfTries >= 2)
            {   Functions.ShowMessage("Message","No of tries Exceeded for this transaction");
                //pWriter.println("<br><center><font color=red>No of tries Exceeded for this transaction</font><br></center>");
                return;
            }
        }

        Hashtable otherdetails = new Hashtable();
        Enumeration enu = req.getParameterNames();

        StringBuffer hiddenvariables = new StringBuffer();
        logger.debug("Fatching other details");
        while (enu.hasMoreElements())
        {
            String name = (String) enu.nextElement();
            otherdetails.put(name, ESAPI.encoder().encodeForHTML(req.getParameter(name)));
            hiddenvariables.append("<INPUT TYPE=\"HIDDEN\" NAME=\"" + ESAPI.encoder().encodeForHTMLAttribute(name) + "\" VALUE=\"" + ESAPI.encoder().encodeForHTMLAttribute(req.getParameter(name)) + "\">");
        }


        String currency = "";
        String key = "";
        String checksumAlgo = null;

        try
        {
            validateOptionalParameter(req);
        }
        catch(ValidationException e)
        {
            logger.error("Validation Exception",e);
            pWriter.println("<br><center><font color=red>Pls Enter Valid Amount OR CURRENCY</font><br></center>");
            return;
        }

        String curcode = (String) otherdetails.get("TMPL_CURRENCY");
        String curval = (String) otherdetails.get("TMPL_AMOUNT");

        if (!(Functions.checkStringNull(curval) == null))
        {
            BigDecimal amt = new BigDecimal(curval);
            curval = amt.setScale(2, BigDecimal.ROUND_DOWN) + "";
        }
        else
        {
            curval = "0.0";
        }

        BigDecimal amount = new BigDecimal((String) otherdetails.get("TXN_AMT"));
        amount = amount.setScale(2, BigDecimal.ROUND_DOWN);

        Connection con = null;
        PreparedStatement p1 = null;
        PreparedStatement p2 = null;
        ResultSet rs = null;
        try
        {
            con = Database.getConnection();
            String q1="select clkey,checksumalgo,accountid from members where memberid=?";
            p1=con.prepareStatement(q1);
            p1.setString(1,toid);
            rs = p1.executeQuery();
            if (rs.next())
            {
                accountId = rs.getString("accountid");
                currency = GatewayAccountService.getGatewayAccount(accountId).getCurrency();
                key = rs.getString("clkey");
                checksumAlgo = rs.getString("checksumalgo");
            }

        }
        catch (SQLException e)
        {   logger.error("ERROR",e);
            ctx.log(" exception " + e.getMessage());
            pWriter.println("Error in retrieving currency ");
            Database.closeConnection(con);
            return;
        }
        catch (SystemError systemError)
        {   logger.error("System Error ",systemError);
            ctx.log(" exception " + systemError.getMessage());
        }

        String cur = "";
        String curmsg = "";

        if (curcode != null && curval != null && !curcode.equals(currency))
        {
            cur = curcode + " " + curval + " ( " + currency + " " + amount + " )";
        }
        else if ("INR".equals(currency))
        {
            cur = "INR " + amount;
            curmsg = "(This amount is in Indian Rupees)";
        }
        else
        {
            cur = currency + " " + amount;
        }

        try
        {
            if (functions.isValueNull(curcode))
            {
                logger.debug("Updating the details");
                String strsql="update transaction_icicicredit set templatecurrency =? ,templateamount =? where icicitransid  =?";
                p2=con.prepareStatement(strsql);
                p2.setString(1,curcode);
                p2.setString(2,curval);
                p2.setString(3,trackingId);
                p2.executeUpdate();
            }
        }
        catch (SQLException systemError)
        {   logger.error("Sql Exception :::::",systemError);
            ctx.log("Exception while updating template fees : " + systemError.getMessage());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p1);
            Database.closePreparedStatement(p2);
            Database.closeConnection(con);
        }

        hiddenvariables.append("<INPUT TYPE=\"HIDDEN\" NAME=\"TMPL_CUSTOMISE\" VALUE=\"" + ESAPI.encoder().encodeForHTMLAttribute(cur) + "\">");
        hiddenvariables.append("<INPUT TYPE=\"HIDDEN\" NAME=\"TMPL_MSG\" VALUE=\"" + ESAPI.encoder().encodeForHTMLAttribute(curmsg) + "\">");
        hiddenvariables.append("<INPUT TYPE=\"HIDDEN\" NAME=\"TMPL_AMOUNT\" VALUE=\"" + ESAPI.encoder().encodeForHTMLAttribute(curval) + "\">");
        hiddenvariables.append("<INPUT TYPE=\"HIDDEN\" NAME=\"TMPL_CURRENCY\" VALUE=\"" + ESAPI.encoder().encodeForHTMLAttribute(curcode) + "\">");
        logger.debug("Generate the checksum of details");
        String checksum = null;
        try
        {
            checksum = Checksum.generateChecksumV2(String.valueOf(toid), description, amount.doubleValue() + "", key, checksumAlgo);
        }
        catch (NoSuchAlgorithmException e)
        {   logger.error("Checksum Exception:::::",e);
            //log("Algorithm Not Found", e);
            throw new ServletException(e);
        }

        hiddenvariables.append("<INPUT TYPE=\"HIDDEN\" NAME=\"checksum\" VALUE=\"" + checksum + "\">");
        hiddenvariables.append("<INPUT TYPE=\"HIDDEN\" NAME=\"ctoken\" VALUE=\"" + ctoken + "\">");
        otherdetails.put("TMPL_CUSTOMISE",ESAPI.encoder().encodeForHTMLAttribute(cur));
        otherdetails.put("TMPL_MSG",ESAPI.encoder().encodeForHTMLAttribute(curmsg));
        otherdetails.put("HIDDENVARIABLES", hiddenvariables.toString());
        otherdetails.put("ctoken",ctoken);
        boolean isMasterCardSupported = GatewayAccountService.getGatewayAccount(accountId).isMasterCardSupported();
        if (isMasterCardSupported)
            otherdetails.put("CARDTYPEMSG", "( Visa/Mastercard only )");
        else
            otherdetails.put("CARDTYPEMSG", "( Visa Cards only )");

        ctx.log("Global VBV ="+ VBVACTIVATION + "merchant="+req.getParameter("VBV"));



        if (req.getParameter("VBV").equalsIgnoreCase("Y") && VBVACTIVATION.equalsIgnoreCase("Y"))  // if merchant has enabled VBV
        {
            ctx.log("VBV is enable for this client and VBV is enable currently");
            otherdetails.put("VBVLOGO", "<img border=\"0\" src=\"/icici/images/logo.jpg\" >");
           //otherdetails.put("CONTINUEBUTTON", "<input type=\"submit\" value=\"Continue\" onclick=\"return submitForm('" + PROXYSCHEME + "://" + PROXYHOST + ":" + PROXYPORT + "" + POSTPAYMENT +"?ctoken="+ctoken +"');\">");
        }
        else
        {
            ctx.log("VBV is not enable for this client or VBV is disable currently");
            otherdetails.put("VBVLOGO", "<img border=\"0\" src=\"/icici/images/logo.jpg\" >");

        }
        otherdetails.put("CONTINUEBUTTON", "<input type=\"submit\" value=\"Continue\" onclick=\"return submitForm('" + PROXYSCHEME + "://" + PROXYHOST + ":" + PROXYPORT + "" + WAITSERVLET +"?ctoken="+ctoken+ "');\">");


        try
        {
            pWriter.println(Template.getCreditPage(toid, otherdetails));

        }
        catch (SystemError ex)
        {   logger.error("System Error::::::",ex);
            pWriter.println("Error in Template " + ex.toString());

      }

    }
    private void validateMandatoryParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.TOID_CAPS);
        inputFieldsListMandatory.add(InputFields.DESCRIPTION_CAPS);
        inputFieldsListMandatory.add(InputFields.TRACKINGID_CAPS);

        inputValidator.InputValidations(req,inputFieldsListMandatory,false);
    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.TMPL_CURRENCY);
        inputFieldsListMandatory.add(InputFields.TMPL_AMOUNT);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }
}
