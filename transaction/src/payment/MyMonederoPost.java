package payment;

import com.directi.pg.*;
import com.logicboxes.util.ApplicationProperties;
import com.payment.errors.TransactionError;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: admin1
 * Date: 3/12/13
 * Time: 8:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyMonederoPost extends PzServlet
{
    private static Logger log=new Logger(MyMonederoPost.class.getName());
    private static TransactionLogger transactionLogger=new TransactionLogger(MyMonederoPost.class.getName());

    final static ResourceBundle RB2=LoadProperties.getProperty("com.directi.pg.template");

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
        log.debug("Entering doService of EcrPostWait");
        transactionLogger.debug("Entering doService of EcrPostWait");
        PrintWriter pWriter = res.getWriter();
        res.setContentType("text/html");
        pWriter.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">");

        PrintWriter out = res.getWriter();
        res.setContentType("text/html");
        out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">");

        HttpSession session = req.getSession();
        Hashtable error = new Hashtable();

        TransactionUtility transactionUtility = new TransactionUtility();
        TransactionError transactionError = new TransactionError();
        Transaction transaction = new Transaction();
        Merchants merchants = new Merchants();

        //validate toid
        if (req.getParameter("TOID") == null)
        {
            res.sendRedirect("/merchant/index.jsp");
            return;
        }

        String ctoken = transactionUtility.validateCtoken(req,pWriter);

        String memberId=null,accountId=null,redirecturl = null,description="",orderdescription="";
        String clkey="",checksumAlgo="",checksumVal="";
        boolean autoredirect = false;
        ResultSet rs=null;
        String version=null;
        String status=null,amount=null,message=null,TRACKING_ID=null;
        String currency="";
        Hashtable otherdetails=new Hashtable();
        //taking variables from request and validation

        error = validateMandatoryParameters(req);
        if(error.size()>0)
        {
            transactionError.displayErrors(error,otherdetails,res,req.getParameter("TOID"),pWriter);
            return;
        }

        memberId = req.getParameter("TOID");
        TRACKING_ID = req.getParameter("TRACKING_ID");
        amount = req.getParameter("amount");
        status = req.getParameter("status");
        message = req.getParameter("message");
        checksumVal = req.getParameter("checksum");

        if( version==null)
            version="";
        Boolean verify=null;
        
        try
        {
            Hashtable transHash = transaction.getTransactionDetailsForCommon(TRACKING_ID);
            if(!transHash.isEmpty())
            {
                currency= (String) transHash.get("currency");
                redirecturl= (String) transHash.get("redirecturl");
                accountId= (String) transHash.get("accountid");
                description = (String) transHash.get("description");
                orderdescription= (String) transHash.get("orderdescription");
            }
            else
            {
                message = "ERROR!!! Invalid Tracking ID . Please visit at "+ ApplicationProperties.getProperty("COMPANY_SUPPORT_URL")+" and create Support req to know the status of this transaction.<BR><BR>";
                transactionError.displayErrorPage(otherdetails,message,res,memberId,ESAPI.encoder().encodeForHTML(TRACKING_ID),ESAPI.encoder().encodeForHTML(description),ESAPI.encoder().encodeForHTML(orderdescription),pWriter);
                return;
            }

            String isService = "";
            Hashtable detailHash = merchants.getMemberDetailsForTransaction(memberId);
            
            if (!detailHash.isEmpty())
            {
                isService = (String)detailHash.get("isservice");
                clkey = (String)detailHash.get("clkey");
                autoredirect = (Boolean)detailHash.get("autoredirect");
//                version = rs.getString("version");
                checksumAlgo = (String)detailHash.get("checksumalgo");
            }
            else
            {

            }
            if (clkey == null || clkey.trim().equals(""))
                throw new SystemError("Could not load Key");
            /*log.debug("verify"+ Checksum.verifyChecksumV2(memberId + "", transactionDescription, new BigDecimal(amountInDb).doubleValue() + "", clkey, checksumVal, checksumAlgo));
            log.debug("verify+Checksum.verifyChecksumV2"+memberId + "=="+ transactionDescription+"======="+ new BigDecimal(amountInDb).doubleValue() + "========" + clkey+"=====" +checksumVal +"==============="+ checksumAlgo);
            log.debug("verify+Checksum.verifyChecksumV2"+Checksum.generateChecksumV2(String.valueOf(memberId), transactionDescription, new BigDecimal(amountInDb).doubleValue() + "", clkey, checksumAlgo));
            if (!Checksum.verifyChecksumV2(memberId + "", transactionDescription,new BigDecimal(amountInDb).doubleValue()+"", clkey, checksumVal, checksumAlgo))
            {*/

            if (version.equals("2"))
            {
                //ctx.log( "!Checksum.verifyChecksum(" + memberId + "," + ( String ) req.getParameter( "status" ) + "," + ( String ) req.getParameter( "message" ) + "," + ( String ) req.getParameter( "DESCRIPTION" ) + "," + ( String ) req.getParameter( "amount" ) + "," + ( String ) req.getParameter( "chargeamt" ) + "," + clkey + "," + ( String ) req.getParameter( "checksum" ) );
                verify = Checksum.verifyChecksumV3(memberId, status, message, description, amount,amount, clkey,checksumVal, checksumAlgo);
            }
            else
            {
                //ctx.log( "!Checksum.verifyChecksum(" + memberId + "," + ( String ) req.getParameter( "status" ) + "," + ( String ) req.getParameter( "message" ) + "," + ( String ) req.getParameter( "DESCRIPTION" ) + "," + ( String ) req.getParameter( "amount" ) + "," + clkey + "," + ( String ) req.getParameter( "checksum" ) );
                verify = Checksum.verifyChecksumV1(memberId, status, message, description, amount, clkey,checksumVal, checksumAlgo);
            }
            if (!verify)
            {
                message = "ERROR!!! checksum mismatched. Please visit at "+ ApplicationProperties.getProperty("COMPANY_SUPPORT_URL")+" and create Support req to know the status of this transaction.<BR><BR>";
                transactionError.displayErrorPage(otherdetails,message,res,memberId,ESAPI.encoder().encodeForHTML(TRACKING_ID),ESAPI.encoder().encodeForHTML(description),ESAPI.encoder().encodeForHTML(orderdescription),pWriter);
                return;
            }

            int random = (int) ((2147483647) * Math.random());
            String isPoweredBy="Y";
            isPoweredBy = merchants.isPoweredBy(memberId);
            if(isPoweredBy.equalsIgnoreCase("Y"))
            {
                otherdetails.put("LOGO","<p align=\"left\"><a href=\"http://www.pz.com\"><IMG border=0 height=40 src=\"/icici/images/logo2.jpg\" width=105></a></p>");
                otherdetails.put("PAYMENTMSG",RB2.getString("PAYMENTMSG"));
            }
            else
            {
                otherdetails.put("LOGO","<p align=\"left\"></p>");
                otherdetails.put("PAYMENTMSG","");
            }
            if (status.equals("Y"))
            {
                otherdetails.put("TRANSACTIONSTATUS", "This Transaction is Successful.");
                otherdetails.put("RETRYMESSAGE", "<br>Please Click on Continue button below in order to complete the Transaction.");

                if (version.equals("2"))
                {
                    otherdetails.put("CONTINUEBUTTON", "<input type=\"button\" value=\"Continue\" onClick=\"document.location.href='" + redirecturl.trim() + "?status=" + (String) status + "&message=" + URLEncoder.encode((String) message) + "&desc=" + URLEncoder.encode(description) + "&amount=" + amount + "&chargeamount=" + amount + "&newchecksum=" + Checksum.generateChecksumV4(description,amount, amount, status, clkey, random, checksumAlgo) + "&trackingid=" + ESAPI.encoder().encodeForHTMLAttribute(TRACKING_ID) + "&random=" + random + "'\">");
                }
                else
                {
                    otherdetails.put("CONTINUEBUTTON", "<input type=\"button\" value=\"Continue\" onClick=\"document.location.href='" + redirecturl.trim() + "?status=" + (String) status + "&message=" + URLEncoder.encode((String) message) + "&desc=" + URLEncoder.encode(description) + "&amount=" + amount + "&newchecksum=" + Checksum.generateChecksumV2(description, amount , status , clkey, checksumAlgo) + "&trackingid=" + ESAPI.encoder().encodeForHTMLAttribute(TRACKING_ID) + "'\">");
                }
            }
            else
            {
                otherdetails.put("TRANSACTIONSTATUS", "This Transaction has Failed.");

                if (version.equals("2"))
                {
                    otherdetails.put("CONTINUEBUTTON", "<input type=\"button\" value=\"Cancel Transaction\" onClick=\"document.location.href='" + redirecturl.trim() + "?status=" + status + "&message=" + URLEncoder.encode(message) + "&desc=" + URLEncoder.encode(description) + "&amount=" + amount + "&chargeamount=" + amount + "&newchecksum=" + Checksum.generateChecksumV4(description, amount, status, status, clkey, random, checksumAlgo) + "&trackingid=" + ESAPI.encoder().encodeForHTMLAttribute(req.getParameter("TRACKING_ID")) + "&random=" + random + "'\">");
                }
                else
                {
                    otherdetails.put("CONTINUEBUTTON", "<input type=\"button\" value=\"Cancel Transaction\" onClick=\"document.location.href='" + redirecturl.trim() + "?status=" + status + "&message=" + URLEncoder.encode(message) + "&desc=" + URLEncoder.encode(description) + "&amount=" + amount + "&newchecksum=" + Checksum.generateChecksumV2(description, amount, status, clkey, checksumAlgo) + "&trackingid=" + ESAPI.encoder().encodeForHTMLAttribute(TRACKING_ID) + "'\">");
                }
            }

            try
            {
                otherdetails.put("TRACKING_ID", ESAPI.encoder().encodeForHTML(TRACKING_ID));
                otherdetails.put("DESCRIPTION", ESAPI.encoder().encodeForHTML(description));
                otherdetails.put("ORDER_DESCRIPTION", ESAPI.encoder().encodeForHTML(orderdescription));
                otherdetails.put("TMPL_CUSTOMISE", amount);
                otherdetails.put("MESSAGE", ESAPI.encoder().encodeForHTML(message));
//                otherdetails.put("TMPL_CUSTOMISE", ESAPI.encoder().encodeForHTML(req.getParameter("TMPL_CUSTOMISE")));
                otherdetails.put("TMPL_MSG", currency);
                if (autoredirect )
                {
                    if (version.equals("2"))
                    {
                        otherdetails.put("URL", redirecturl.trim() + "?status=" + status + "&message=" + URLEncoder.encode(message) + "&desc=" + URLEncoder.encode(description) + "&amount=" + amount + "&chargeamount=" + amount + "&newchecksum=" + Checksum.generateChecksumV4(description, amount ,amount, status, clkey, random, checksumAlgo) + "&trackingid=" + ESAPI.encoder().encodeForHTMLAttribute(TRACKING_ID) + "&random=" + random);
                    }
                    else
                    {
                        otherdetails.put("URL", redirecturl.trim() + "?status=" + status + "&message=" + URLEncoder.encode(message) + "&desc=" + URLEncoder.encode(description) + "&amount=" + amount+ "&newchecksum=" + Checksum.generateChecksumV2(description, amount, status, clkey, checksumAlgo) + "&trackingid=" + ESAPI.encoder().encodeForHTMLAttribute(TRACKING_ID));
                    }
                    out.println(Template.getAutoRedirectPage(memberId + "", otherdetails));
                }
                else
                {   otherdetails.put("RETRYBUTTON"," ");
                    otherdetails.put("HIDDENVARIABLES"," ");
                
                    String template="";

                    Hashtable tempHash = merchants.getMemberTemplateDetails(memberId);
                    if(!tempHash.isEmpty())
                    {
                        template = (String) tempHash.get("template");
                    }
                    otherdetails.put("TEMPLATE",template);
                    //log.debug("OtherDetails  =="+otherdetails.toString());
                    out.println(Template.getConfirmationPage(memberId + "", otherdetails,accountId+""));
                }
            }
            catch (SystemError ex)
            {
                out.println("Error in Template " + ex.toString());
            }
        }
        catch(Exception e)
        {
            log.error("ERROR OCCURED",e);
            transactionLogger.error("ERROR OCCURED",e);
            message = "ERROR!!! Error Occured While Performing your Transaction. Please visit at "+ ApplicationProperties.getProperty("COMPANY_SUPPORT_URL")+" and create Support req to know the status of this transaction.<BR><BR>";
            transactionError.displayErrorPage(otherdetails,message,res,memberId,ESAPI.encoder().encodeForHTML(TRACKING_ID),ESAPI.encoder().encodeForHTML(description),ESAPI.encoder().encodeForHTML(orderdescription),pWriter);
            return;
        }
    }

    private Hashtable validateMandatoryParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        Hashtable error = new Hashtable();
        List<InputFields>  inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.TOID_CAPS);
        inputFieldsListMandatory.add(InputFields.TRACKINGID_CAPS);
        inputFieldsListMandatory.add(InputFields.AMOUNT);
        inputFieldsListMandatory.add(InputFields.STATUS);
        inputFieldsListMandatory.add(InputFields.MESSAGE);
        inputFieldsListMandatory.add(InputFields.CHECKSUM);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListMandatory, errorList,false);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListMandatory)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError("Invalid "+inputFields.toString()).getLogMessage());
                    transactionLogger.debug(errorList.getError("Invalid "+inputFields.toString()).getLogMessage());
                    error.put(inputFields.toString(),"Invalid "+errorList.getError(inputFields.toString()).getMessage());
                }
            }
        }
        return error;
    }
}
