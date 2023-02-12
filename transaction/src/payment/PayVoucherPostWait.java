package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import org.owasp.esapi.ESAPI;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;

public class PayVoucherPostWait extends PzServlet
{
    private static Logger logger = new Logger(PayVoucherPostWait.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PayVoucherPostWait.class.getName());

    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.PayVoucherServlet");
    final static String PAYPROCESS = RB.getString("PAYPROCESS");
    /**
     * Sole constructor.
     */

    public PayVoucherPostWait()
    {
        super();
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doService(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
    {

        logger.debug("Entering doService of PayVoucherPostWait");
        transactionLogger.debug("Entering doService of PayVoucherPostWait");

        PrintWriter out = res.getWriter();
        res.setContentType("text/html");
        out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">");

        HttpSession session = req.getSession();
        ServletContext ctx = getServletContext();

        TransactionUtility transactionUtility = new TransactionUtility();
        Transaction transaction = new Transaction();
        Merchants merchants = new Merchants();

        //validate toid
        if (req.getParameter("TOID") == null)
        {
            //out.print(Functions.ShowMessage("Message","MerchantID is not found. Your session is Expire."));
            res.sendRedirect("/merchant/index.jsp");
            return;
        }
        //valdate ctoken
        String ctoken = transactionUtility.validateCtoken(req,out);

        logger.debug("CSRF check successful ");
        transactionLogger.debug("CSRF check successful ");

        boolean triesOver = false;
        //boolean proofrequired = false;
        boolean autoredirect = false;

        Hashtable otherdetails = new Hashtable();

        if (req.getParameter("TEMPLATE") != null)
            otherdetails.put("TEMPLATE", req.getParameter("TEMPLATE"));

        Enumeration enumeration = req.getParameterNames();

        while (enumeration.hasMoreElements())
        {
            String name = (String) enumeration.nextElement();
            if (name.startsWith("TMPL_"))
                otherdetails.put(name, req.getParameter(name));
        }

        StringBuffer templatevar = transactionUtility.getValueForPostTemplate(req);

       /* if ((req.getParameter("AUTOREDIRECT")).equals("Y"))
        {
            autoredirect = true;
        }
        */
        //autoredirect = true;

        triesOver = transactionUtility.noOfTries(req,session);
        String clkey = "";
        String checksumAlgo = null;
        String companyname = "";
        String brandname = "";
        String sitename = "";
        String currency = "";
        String version = req.getParameter("TMPL_VERSION");
        if (version == null)
            version = "";

        String memberId = req.getParameter("TOID");
        String accountId = req.getParameter("ACCOUNTID");
        BigDecimal amt = new BigDecimal(req.getParameter("amount"));
        amt = amt.setScale(2, BigDecimal.ROUND_HALF_UP);
        try
        {
            Hashtable transHash = merchants.getMemberDetailsForTransaction(memberId);
            if (!transHash.isEmpty())/*(rs.next())*/
            {
                clkey = (String) transHash.get("clkey");
                companyname = (String) transHash.get("company_name");
                brandname = (String) transHash.get("brandname");
                sitename = (String) transHash.get("sitename");
                checksumAlgo = (String) transHash.get("checksumalgo");

                GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
                currency = account.getCurrency();
                if (brandname.trim().equals(""))
                    brandname = companyname;

                if (sitename.trim().equals(""))
                    sitename = companyname;
            }
            else
            {
                throw new SystemError("Could not load Key");
            }

            //ctx.log( "Amount = " + ( String ) req.getParameter( "amount" ) );
            boolean verify = false;

            ctx.log("Merchant version=" + version);

            if (version.equals("2"))
            {
                //ctx.log( "!Checksum.verifyChecksum(" + memberId + "," + ( String ) req.getParameter( "status" ) + "," + ( String ) req.getParameter( "message" ) + "," + ( String ) req.getParameter( "DESCRIPTION" ) + "," + ( String ) req.getParameter( "amount" ) + "," + ( String ) req.getParameter( "chargeamt" ) + "," + clkey + "," + ( String ) req.getParameter( "checksum" ) );
                verify = Checksum.verifyChecksumV3(memberId, req.getParameter("status"), req.getParameter("message"), req.getParameter("DESCRIPTION"), req.getParameter("amount"), req.getParameter("chargeamt"), clkey, req.getParameter("checksum"), checksumAlgo);
            }
            else
            {
                //ctx.log( "!Checksum.verifyChecksum(" + memberId + "," + ( String ) req.getParameter( "status" ) + "," + ( String ) req.getParameter( "message" ) + "," + ( String ) req.getParameter( "DESCRIPTION" ) + "," + ( String ) req.getParameter( "amount" ) + "," + clkey + "," + ( String ) req.getParameter( "checksum" ) );
                verify = Checksum.verifyChecksumV1(memberId, req.getParameter("status"), req.getParameter("message"), req.getParameter("DESCRIPTION"), req.getParameter("amount"), clkey, req.getParameter("checksum"), checksumAlgo);
            }

            if (!verify)
            {
                out.println("Checksum mismatch. Illeagal Access");
                return;
            }

            //Generate a random number and also include it in checksum so that nobody can get exact formula for checksum
            int random = (int) ((2147483647) * Math.random());

            if ((req.getParameter("status")).equals("Y"))
            {
                otherdetails.put("TRANSACTIONSTATUS", "This Transaction is Successful.");
                otherdetails.put("RETRYMESSAGE", "<br>Please Click on Continue button below in order to complete the Transaction.");
                triesOver = true; //bcoz now the control has to go away irrespective of noOfTries
                //There was no form tag for retry and cancel button but on netscape it was giving error. SO put form tag before displaying this button.
                //as form tag is already written in template removed it from here
                //	otherdetails.put("CONTINUEBUTTON","<form action=\"" +  req.getParameter("REDIRECTURL").trim()+"?status="+(String)req.getParameter("status")+"&message="+URLEncoder.encode((String)req.getParameter("message"))+"&desc="+URLEncoder.encode((String)req.getParameter("DESCRIPTION"))+"&amount="+amt.toString()+"&newchecksum="+Checksum.getChecksum((String)req.getParameter("DESCRIPTION"),amt.toString(),(String)req.getParameter("status"),clkey) +"&trackingid="+req.getParameter("TRACKING_ID")+"\" method=\"post\"><input type=\"submit\" value=\"Continue\"> " );
                if (version.equals("2"))
                {
                    otherdetails.put("CONTINUEBUTTON", "<input type=\"button\" value=\"Continue\" onClick=\"document.location.href='" + req.getParameter("REDIRECTURL").trim() + "?status=" + (String) req.getParameter("status") + "&message=" + URLEncoder.encode((String) req.getParameter("message")) + "&desc=" + URLEncoder.encode(req.getParameter("DESCRIPTION")) + "&amount=" + amt.toString() + "&chargeamount=" + req.getParameter("chargeamt") + "&newchecksum=" + Checksum.generateChecksumV4(req.getParameter("DESCRIPTION"), amt.toString(), req.getParameter("chargeamt"), req.getParameter("status"), clkey, random, checksumAlgo) + "&trackingid=" + ESAPI.encoder().encodeForHTMLAttribute(req.getParameter("TRACKING_ID")) + "&random=" + random + "'\">");
                }
                else
                {
                    otherdetails.put("CONTINUEBUTTON", "<input type=\"button\" value=\"Continue\" onClick=\"document.location.href='" + req.getParameter("REDIRECTURL").trim() + "?status=" + (String) req.getParameter("status") + "&message=" + URLEncoder.encode((String) req.getParameter("message")) + "&desc=" + URLEncoder.encode(req.getParameter("DESCRIPTION")) + "&amount=" + amt.toString() + "&newchecksum=" + Checksum.generateChecksumV2(req.getParameter("DESCRIPTION"), amt.toString(), req.getParameter("status"), clkey, checksumAlgo) + "&trackingid=" + ESAPI.encoder().encodeForHTMLAttribute(req.getParameter("TRACKING_ID")) + "'\">");
                }
            }
            else
            {
                otherdetails.put("TRANSACTIONSTATUS", "This Transaction has Failed. This could be due to any of the below reasons</b><ul><li><b>Your account has insufficient funds</b></li><li><b>or Your  issuer bank declined the transaction for some reason. </b></li><li><b>or The Account details you submitted were invalid</b></li><li><b>or Some other error occurred during the processing of this transaction</b></li></ul>");
                if (!triesOver)
                {
                    otherdetails.put("RETRYMESSAGE", "Please Click on Retry below to verify your Account details and retry this transaction again, or hit Cancel Transaction in order to cancel the Transaction.");
                    otherdetails.put("RETRYBUTTON", "<input type=\"button\" value=\"Retry \" onClick=\"document.location.href='" + PAYPROCESS + "?toid=" + memberId + "&totype=&description=" + URLEncoder.encode(req.getParameter("DESCRIPTION")) + "&orderdescription=" + URLEncoder.encode(req.getParameter("ORDER_DESCRIPTION")) + "&amount=" + amt.toString() + "&redirecturl=" + req.getParameter("REDIRECTURL") + "&fromtype=icicicredit&checksum=" + Checksum.generateChecksumV1(memberId, "", amt.toString(), req.getParameter("DESCRIPTION"), req.getParameter("REDIRECTURL"), clkey, checksumAlgo) + "&" + templatevar + "&TMPL_VERSION=" + version + "'\">");
                }    //todo retry button

                if (version.equals("2"))
                {
                    otherdetails.put("CONTINUEBUTTON", "<input type=\"button\" value=\"Cancel Transaction\" onClick=\"document.location.href='" + req.getParameter("REDIRECTURL").trim() + "?status=" + req.getParameter("status") + "&message=" + URLEncoder.encode(req.getParameter("message")) + "&desc=" + URLEncoder.encode(req.getParameter("DESCRIPTION")) + "&amount=" + amt.toString() + "&chargeamount=" + req.getParameter("chargeamt") + "&newchecksum=" + Checksum.generateChecksumV4(req.getParameter("DESCRIPTION"), amt.toString(), req.getParameter("chargeamt"), req.getParameter("status"), clkey, random, checksumAlgo) + "&trackingid=" + ESAPI.encoder().encodeForHTMLAttribute(req.getParameter("TRACKING_ID")) + "&random=" + random + "'\">");
                }
                else
                {
                    otherdetails.put("CONTINUEBUTTON", "<input type=\"button\" value=\"Cancel Transaction\" onClick=\"document.location.href='" + req.getParameter("REDIRECTURL").trim() + "?status=" + req.getParameter("status") + "&message=" + URLEncoder.encode(req.getParameter("message")) + "&desc=" + URLEncoder.encode(req.getParameter("DESCRIPTION")) + "&amount=" + amt.toString() + "&newchecksum=" + Checksum.generateChecksumV2(req.getParameter("DESCRIPTION"), amt.toString(), req.getParameter("status"), clkey, checksumAlgo) + "&trackingid=" + ESAPI.encoder().encodeForHTMLAttribute(req.getParameter("TRACKING_ID")) + "'\">");
                }
            }

            try
            {
                otherdetails.put("TRACKING_ID", ESAPI.encoder().encodeForHTML(req.getParameter("TRACKING_ID")));
                otherdetails.put("DESCRIPTION", ESAPI.encoder().encodeForHTML(req.getParameter("DESCRIPTION")));
                otherdetails.put("ORDER_DESCRIPTION", ESAPI.encoder().encodeForHTML(req.getParameter("ORDER_DESCRIPTION")));
                otherdetails.put("TXN_AMT", amt.toString());
                otherdetails.put("MESSAGE", ESAPI.encoder().encodeForHTML(req.getParameter("message")));
                otherdetails.put("TMPL_CUSTOMISE", ESAPI.encoder().encodeForHTML(req.getParameter("TMPL_CUSTOMISE")));
                otherdetails.put("TMPL_MSG", ESAPI.encoder().encodeForHTML(req.getParameter("TMPL_MSG")));

                if (autoredirect && triesOver)
                {
                    if (version.equals("2"))
                    {
                        otherdetails.put("URL", req.getParameter("REDIRECTURL").trim() + "?status=" + req.getParameter("status") + "&message=" + URLEncoder.encode(req.getParameter("message")) + "&desc=" + URLEncoder.encode(req.getParameter("DESCRIPTION")) + "&amount=" + amt.toString() + "&chargeamount=" + req.getParameter("chargeamt") + "&newchecksum=" + Checksum.generateChecksumV4(req.getParameter("DESCRIPTION"), amt.toString(), req.getParameter("chargeamt"), req.getParameter("status"), clkey, random, checksumAlgo) + "&trackingid=" + ESAPI.encoder().encodeForHTMLAttribute(req.getParameter("TRACKING_ID")) + "&random=" + random);
                    }
                    else
                    {
                        otherdetails.put("URL", req.getParameter("REDIRECTURL").trim() + "?status=" + req.getParameter("status") + "&message=" + URLEncoder.encode(req.getParameter("message")) + "&desc=" + URLEncoder.encode(req.getParameter("DESCRIPTION")) + "&amount=" + amt.toString() + "&newchecksum=" + Checksum.generateChecksumV2(req.getParameter("DESCRIPTION"), amt.toString(), req.getParameter("status"), clkey, checksumAlgo) + "&trackingid=" + ESAPI.encoder().encodeForHTMLAttribute(req.getParameter("TRACKING_ID")));
                    }
                    out.println(Template.getAutoRedirectPage(memberId + "", otherdetails));
                }
                else
                {
                    //otherdetails.put("HEADER",session.getAttribute("HEADER"));
                    out.println(Template.getConfirmationPage(memberId + "", otherdetails,accountId+""));
                }
            }
            catch (SystemError ex)
            {
                out.println("Error in Template " + ex.toString());
            }
        }
        catch (SystemError e)
        {
            logger.error("System Error:::::",e);
            transactionLogger.error("System Error:::::",e);
            ctx.log(e.toString());
            //e.printStackTrace();
            throw new ServletException(e.getMessage());
        }
        catch (NoSuchAlgorithmException ex)
        {
            logger.error("Algorithm Exception",ex);
            transactionLogger.error("Algorithm Exception",ex);
            ctx.log(ex.toString());
            throw new ServletException(ex.getMessage());
        }
        finally
        {
            //Connection con = Database.getConnection();
            transaction.updateVoucherId(req.getParameter("TRACKING_ID"));
        }
    }
}