import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 3/30/13
 * Time: 11:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class DdpServlet extends TcServlet
{
    static Logger logger = new Logger(DdpServlet.class.getName());
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.CommServlet");
    final static String WAITSERVLET = RB.getString("WAITSERVLET");
    final static String PROXYSCHEME = RB.getString("PROXYSCHEME");
    final static String PROXYHOST = RB.getString("PROXYHOST");
    final static String PROXYPORT = RB.getString("PROXYPORT");

    public DdpServlet()
    {
        super();
    }

//        public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
//        {
//            doService(req, res);
//        }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        logger.debug("Entering in CommServlet");
        Merchants merchants = new Merchants();
        PrintWriter pWriter = res.getWriter();
        res.setContentType("text/html");
        pWriter.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">");
        String ctoken = null;
        HttpSession session = req.getSession();
        if (req.getSession() == null)
        {
            Functions.ShowMessage("Invalid Request", "Your session has expired");
            return;
        }
        if (session != null)
        {
            ctoken = (String) session.getAttribute("ctoken");
            logger.debug("CSRF token from session" + ctoken);
        }

        if (ctoken != null && !ctoken.equals("") && !req.getParameter("ctoken").equals(ctoken))
        {
            logger.debug("CSRF token did not match ");
            Functions.ShowMessage("Invalid Request", "UnAuthorized Request");
            return;
        }

        try
        {
            validateMandatoryParameter(req);
        }
        catch (ValidationException e)
        {
            logger.error("INVALID INPUT", e);
            pWriter.println("<br><center><font color=red>Invalid Input Value</font><br></center>");
            return;
        }

        String toid = (String) req.getParameter("TOID");
        String description = (String) req.getParameter("DESCRIPTION");
        String trackingId = req.getParameter("TRACKING_ID");
        String accountId = req.getParameter("ACCOUNTID");
        toid = toid.trim();
        String sessionID = toid + "#~#" + description;

        if (session.getAttribute(sessionID) != null)
        {
            int onOfTries = Integer.parseInt((String) session.getAttribute(sessionID));

            if (onOfTries >= 2)
            {
                Functions.ShowMessage("Message", "No of tries Exceeded for this transaction");
                //Todo Error page
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
        catch (ValidationException e)
        {
            logger.error("Validation Exception", e);
            pWriter.println("<br><center><font color=red>Pls Enter Valid Amount OR CURRENCY</font><br></center>");
            return;
        }
        String curcode = req.getParameter("TMPL_CURRENCY");
        String curval = req.getParameter("TMPL_AMOUNT");

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

        currency = GatewayAccountService.getGatewayAccount(accountId).getCurrency();
        Connection con = null;
        try
        {
            con = Database.getConnection();
            String q1 = "select clkey,checksumalgo,accountid from members where memberid=?";
            PreparedStatement p1 = con.prepareStatement(q1);
            p1.setString(1, toid);
            ResultSet rs = p1.executeQuery();
            logger.debug(q1);
            if (rs.next())
            {
                key = rs.getString("clkey");
                checksumAlgo = rs.getString("checksumalgo");
            }
        }
        catch (SQLException e)
        {
            logger.error("ERROR", e);
            pWriter.println("Error in retrieving currency ");
            Database.closeConnection(con);
            return;
        }
        catch (SystemError systemError)
        {
            logger.error("System Error ", systemError);
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
            if (curcode != null)
            {
                con = Database.getConnection();
                logger.debug("Updating the details" + curval);
                String strsql = "update transaction_common set templatecurrency =? ,templateamount =? where trackingid  =?";
                PreparedStatement p2 = con.prepareStatement(strsql);
                p2.setString(1, curcode);
                p2.setString(2, curval);
                p2.setString(3, trackingId);
                p2.executeUpdate();
            }
        }
        catch (SQLException systemError)
        {
            logger.error("Sql Exception :::::", systemError);

        }
        catch (SystemError systemError)
        {
            logger.error("System Error ", systemError);

        }
        finally
        {
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
        {
            logger.error("Checksum Exception:::::", e);
            //log("Algorithm Not Found", e);
            throw new ServletException(e);
        }

        hiddenvariables.append("<INPUT TYPE=\"HIDDEN\" NAME=\"checksum\" VALUE=\"" + checksum + "\">");
        hiddenvariables.append("<INPUT TYPE=\"HIDDEN\" NAME=\"ctoken\" VALUE=\"" + ctoken + "\">");
        otherdetails.put("TMPL_CUSTOMISE", cur);
        otherdetails.put("TMPL_MSG", curmsg);
        otherdetails.put("HIDDENVARIABLES", hiddenvariables.toString());
        otherdetails.put("ctoken", ctoken);
        boolean isMasterCardSupported = GatewayAccountService.getGatewayAccount(accountId).isMasterCardSupported();
        if (isMasterCardSupported)
            otherdetails.put("CARDTYPEMSG", "( Visa/Mastercard only )");
        else
            otherdetails.put("CARDTYPEMSG", "( Visa Cards only )");


        otherdetails.put("VBVLOGO", "<img border=\"0\" src=\"/icici/images/logo.jpg\" >");
        logger.debug("-5- success");
        String partnerLogo = "";
        try
        {
            partnerLogo = merchants.getPartnerLogo(toid);
        }
        catch (Exception e)
        {

        }
        if (partnerLogo != null && partnerLogo.equals(""))
        {
            otherdetails.put("PARTNERLOGO", "<img border=\"0\" height=40 width=105 src=\"/icici/images/" + partnerLogo + "\" >");
            logger.debug("add partnerlogo");
        }
        otherdetails.put("CONTINUEBUTTON", "<input type=\"submit\" value=\"Continue\" onclick=\"return submitForm('" + PROXYSCHEME + "://" + PROXYHOST + ":" + PROXYPORT + "" + WAITSERVLET + "?ctoken=" + ctoken + "');\">");

        try
        {
            pWriter.println(Template.getDdpCreditPage(toid, otherdetails));
        }
        catch (SystemError ex)
        {
            logger.error("System Error::::::", ex);
            pWriter.println("Error in Template " + ex.toString());
        }
    }
    private String getTCMessage(String memberId)
    {
        Merchants merchants = new Merchants();
        String msg = "";
        if (ifTCRequired(memberId))
        {
            String siteName = "";
            try
            {
                siteName = merchants.getColumn("sitename", memberId);
            }
            catch (SystemError ex)
            {
                logger.error("exception",ex);
            }
            msg = "<input type=\"checkbox\" value=\"tc\" name=\"TC\"> I have read the Terms and condition of " + siteName + " and I accept the same . Also I have necessary prescription of the product purchased.";
        }
        else
        {
            msg="<input type=\"checkbox\" value=\"tc\" name=\"TC\"> I have read the Terms and condition and I accept the same.";
        }
        return msg;
    }
    private boolean ifTCRequired(String memberId)
    {
        Connection con = null;
        try
        {
            con = Database.getConnection();
            ResultSet rs = Database.executeQuery("select * from members where memberid="+memberId + " and isPharma='Y'", con);
            return rs.next();
        }
        catch (Exception ex)
        {
          logger.error("exception",ex);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return false;
    }

    private void validateMandatoryParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.TOID_CAPS);
        inputFieldsListMandatory.add(InputFields.TRACKINGID_CAPS);
        inputFieldsListMandatory.add(InputFields.ACCOUNTID_CAPS);
        inputFieldsListMandatory.add(InputFields.DESCRIPTION_CAPS);

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
