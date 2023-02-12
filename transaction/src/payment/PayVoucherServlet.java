package payment;
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Sep 2, 2012
 * Time: 3:22:07 PM
 * To change this template use File | Settings | File Templates.
 */

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.*;


public class PayVoucherServlet extends PzServlet
{
    private static Logger logger = new Logger(PayVoucherServlet.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PayVoucherServlet.class.getName());

    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.PayVoucherServlet");
    final static String WAITSERVLET = RB.getString("WAITSERVLET");

    final static String PROXYSCHEME = RB.getString("PROXYSCHEME");
    final static String PROXYHOST = RB.getString("PROXYHOST");
    final static String PROXYPORT = RB.getString("PROXYPORT");

    public PayVoucherServlet()
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

        logger.debug("Entering in PayVoucherServlet");
        transactionLogger.debug("Entering in PayVoucherServlet");

        final  String PROXYHOST = RB.getString("PROXYHOST");
        final  String PROXYPORT = RB.getString("PROXYPORT");

        PrintWriter pWriter = res.getWriter();
        res.setContentType("text/html");
        pWriter.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">");
        HttpSession session = req.getSession();

        Transaction transaction = new Transaction();
        TransactionUtility transactionUtility = new TransactionUtility();

        //validating ctoken
        String ctoken = transactionUtility.validateCtoken(req,pWriter);
        if(ctoken == null)
            return;
        //validation for mandatory parameters
        try
        {
            validateMandatoryParameter(req);
        }
        catch(ValidationException e)
        {
          logger.error("INVALID INPUT",e);
          transactionLogger.error("INVALID INPUT",e);
          //Todo error page
          pWriter.println("<br><center><font color=red>Invalid Input Value</font><br></center>");
            return;
        }

        String toid = req.getParameter("TOID");
        String description = req.getParameter("DESCRIPTION");
        String trackingId = req.getParameter("TRACKING_ID");
        String accountId =  req.getParameter("ACCOUNTID");

        toid = toid.trim();

        String sessionID = toid + "#~#" + description;

        if (session.getAttribute(sessionID) != null)
        {
            int onOfTries = Integer.parseInt((String) session.getAttribute(sessionID));

            if (onOfTries >= 2)
            {
                //Todo Error page
                Functions.ShowMessage("Message","No of tries Exceeded for this transaction");

                return;
            }
        }
        //validate optional parameter
        try
        {
            validateOptionalParameter(req);
        }
        catch(ValidationException e)
        {
            logger.error("Validation Exception",e);
            transactionLogger.error("Validation Exception",e);
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

        //update currency value
        transaction.updateCurrencyCodeCommon(curcode,curval,trackingId);

        Hashtable otherdetails = prepareDetailsForWaitServlet(req,ctoken,WAITSERVLET);

        try
        {
            pWriter.println(Template.getPaylineCreditPage(toid, otherdetails));

        }
        catch (SystemError ex)
        {
            logger.error("System Error::::::",ex);
            transactionLogger.error("System Error::::::",ex);
            pWriter.println("Error in Template " + ex.toString());
        }
    }

    private Hashtable prepareDetailsForWaitServlet(HttpServletRequest req,String ctoken,String waitServlet) throws ServletException
    {

        logger.debug("Preparing other details");
        transactionLogger.debug("Preparing other details");
        String toid = req.getParameter("TOID");
        String description = req.getParameter("DESCRIPTION");
        String trackingId = req.getParameter("TRACKING_ID");
        String accountId =  req.getParameter("ACCOUNTID");
        String curcode = req.getParameter("TMPL_CURRENCY");
        String curval = req.getParameter("TMPL_AMOUNT");
        Merchants merchants = new Merchants();
        toid = toid.trim();
        Hashtable otherdetails = new Hashtable();
        Enumeration enu = req.getParameterNames();

        StringBuffer hiddenvariables = new StringBuffer();
        logger.debug("Fatching other details");
        transactionLogger.debug("Fatching other details");
        while (enu.hasMoreElements())
        {
            String name = (String) enu.nextElement();
            otherdetails.put(name, ESAPI.encoder().encodeForHTML(req.getParameter(name)));
            hiddenvariables.append("<INPUT TYPE=\"HIDDEN\" NAME=\"" + ESAPI.encoder().encodeForHTMLAttribute(name) + "\" VALUE=\"" + ESAPI.encoder().encodeForHTMLAttribute(req.getParameter(name)) + "\">");
        }

        BigDecimal amount = new BigDecimal((String) otherdetails.get("TXN_AMT"));
        amount = amount.setScale(2, BigDecimal.ROUND_DOWN);
        String currency = GatewayAccountService.getGatewayAccount(accountId).getCurrency();
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
        hiddenvariables.append("<INPUT TYPE=\"HIDDEN\" NAME=\"TMPL_CUSTOMISE\" VALUE=\"" + ESAPI.encoder().encodeForHTMLAttribute(cur) + "\">");
        hiddenvariables.append("<INPUT TYPE=\"HIDDEN\" NAME=\"TMPL_MSG\" VALUE=\"" + ESAPI.encoder().encodeForHTMLAttribute(curmsg) + "\">");
        hiddenvariables.append("<INPUT TYPE=\"HIDDEN\" NAME=\"TMPL_AMOUNT\" VALUE=\"" + ESAPI.encoder().encodeForHTMLAttribute(curval) + "\">");
        hiddenvariables.append("<INPUT TYPE=\"HIDDEN\" NAME=\"TMPL_CURRENCY\" VALUE=\"" + ESAPI.encoder().encodeForHTMLAttribute(curcode) + "\">");
        logger.debug("Generate the checksum of details");
        transactionLogger.debug("Generate the checksum of details");

        Hashtable membersDetails = null;
        membersDetails = merchants.getMemberDetailsForTransaction(toid);
        String key = (String)membersDetails.get("clkey");
        String checksumAlgo = (String)membersDetails.get("checksumalgo");
        String checksum = null;

        try
        {
            logger.debug("verify+Checksum.verifyChecksumV2"+String.valueOf(toid) + "=="+ description+"======="+ amount.doubleValue() + "" + "========" + key+"=====" + checksumAlgo);
            transactionLogger.debug("verify+Checksum.verifyChecksumV2"+String.valueOf(toid) + "=="+ description+"======="+ amount.doubleValue() + "" + "========" + key+"=====" + checksumAlgo);
            checksum = Checksum.generateChecksumV2(String.valueOf(toid), description, amount.doubleValue() + "", key, checksumAlgo);

        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("Checksum Exception:::::",e);
            transactionLogger.error("Checksum Exception:::::",e);
            //log("Algorithm Not Found", e);
            throw new ServletException(e);
        }

        String INVOICE_NO = null;
        INVOICE_NO =(String)req.getParameter("INVOICE_NO") ;
        if(INVOICE_NO!=null && !INVOICE_NO.equals(""))
        {
            otherdetails.put("DISABLED","readonly");
            hiddenvariables.append("<INPUT TYPE=\"HIDDEN\" NAME=\"INVOICE_NO\" VALUE=\"" + INVOICE_NO + "\">")  ;

        }

        hiddenvariables.append("<INPUT TYPE=\"HIDDEN\" NAME=\"checksum\" VALUE=\"" + checksum + "\">");
        hiddenvariables.append("<INPUT TYPE=\"HIDDEN\" NAME=\"ctoken\" VALUE=\"" + ctoken + "\">");
        otherdetails.put("TMPL_CUSTOMISE", cur);
        otherdetails.put("TMPL_MSG", curmsg);
        otherdetails.put("HIDDENVARIABLES", hiddenvariables.toString());
        otherdetails.put("ctoken",ctoken);
        String isPoweredBy="Y";
        isPoweredBy = merchants.isPoweredBy(toid);

        if(isPoweredBy.equalsIgnoreCase("Y"))
        {
            otherdetails.put("LOGO","<p align=\"left\"><a href=\"http://www.pz.com\"><IMG border=0 height=40 src=\"/icici/images/logo2.jpg\" width=105></a></p>");
        }
        else
        {
            otherdetails.put("LOGO","<p align=\"left\"></p>");
        }

        //partner logo
        String partnerLogo ="";
        try
        {
            partnerLogo = merchants.getPartnerLogo(toid);
        }
        catch(Exception e)
        {

        }

        if(partnerLogo!=null && !partnerLogo.equals(""))
        {
            otherdetails.put("PARTNERLOGO", "<img border=\"0\" height=40 width=105 src=\"/icici/images/"+partnerLogo+"\" >");
        }
        //otherdetails.put("HEADER",session.getAttribute("HEADER"));
        otherdetails.put("CONTINUEBUTTON", "<input type=\"submit\" value=\"Continue\" onclick=\"return submitForm('" + PROXYSCHEME + "://" + PROXYHOST + ":" + PROXYPORT + "" + WAITSERVLET +"?ctoken="+ctoken+ "');\">");

        INVOICE_NO  = null;
        INVOICE_NO =(String)req.getParameter("INVOICE_NO") ;
        if(INVOICE_NO!=null && !INVOICE_NO.equals(""))
        {
            otherdetails.put("DISABLED","readonly");
            hiddenvariables.append("<INPUT TYPE=\"HIDDEN\" NAME=\"INVOICE_NO\" VALUE=\"" + INVOICE_NO + "\">")  ;
        }

        return otherdetails;
    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();

        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.TMPL_CURRENCY);
        inputFieldsListOptional.add(InputFields.TMPL_AMOUNT);
        inputValidator.InputValidations(req, inputFieldsListOptional,true);

    }

    private void validateMandatoryParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();

        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.TOID_CAPS);
        inputFieldsListMandatory.add(InputFields.DESCRIPTION_CAPS);
        inputFieldsListMandatory.add(InputFields.TRACKINGID_CAPS);
        inputFieldsListMandatory.add(InputFields.ACCOUNTID_CAPS);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListMandatory,false);
    }
}


