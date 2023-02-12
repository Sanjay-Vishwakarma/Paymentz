package payment;

import com.directi.pg.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: Dhiresh
 * Date: 27/2/13
 * Time: 9:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyMonederoProcess extends HttpServlet
{
    private static Logger log = new Logger(MyMonederoProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(MyMonederoProcess.class.getName());


    Connection con = null;
    private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.MyMonederoServlet");
    final static String MMSERVLET =  RB.getString("MMSERVLET");
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("Entered in MyMonederoProcess");
        transactionLogger.debug("Entered in MyMonederoProcess");
        PrintWriter pWriter = res.getWriter();

        TransactionUtility transactionUtility = new TransactionUtility();
        Transaction transaction = new Transaction();
        Merchants merchants = new Merchants();

        res.setContentType("text/html");
        pWriter.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">");

        Functions functions=new Functions();
        String ctoken = null;
        HttpSession session = req.getSession();
        if(req.getSession()== null)
        {
            pWriter.print(Functions.ShowMessage("Message", "Your session is Expire."));
            //res.sendRedirect("http://www.<hostname>.com/");
            return;
        }
        if(session !=null)
        {
            ctoken =  (String)session.getAttribute("ctoken");

            log.debug("CSRF token from session"+ctoken);
            transactionLogger.debug("CSRF token from session"+ctoken);
        }

        String template=null;
        String header=req.getParameter("HEADDER");
        String key = "";
        String sTransParameter=req.getParameter("hiddenvariables");

        Hashtable value=functions.getHashValue(sTransParameter) ;
        value.put("HEADER",header);
        value.put("ctoken",ctoken);
        int TRACKING_ID=0;
        String toid= (String)value.get("TOID");

        try
        {
            //inserting transaction Details
            TRACKING_ID = transaction.insertTransCommon(toid,(String)value.get("TOTYPE"),(String)value.get("FROMID"),(String)value.get("FROMTYPE"),(String)value.get("DESCRIPTION"),(String)value.get("ORDER_DESCRIPTION"),(String)value.get("TXN_AMT"),(String)value.get("REDIRECTURL"),(String)value.get("ACCOUNTID"),Integer.parseInt((String)value.get("PAYMODEID")),Integer.parseInt((String)value.get("CARDTYPEID")),(String)value.get("CURRENCY"),header,null);
            value.put("TRACKING_ID",TRACKING_ID);
            //insert into table
            log.debug("insert data successfully");
            transactionLogger.debug("insert data successfully");

            Hashtable memTemplateDetails = merchants.getMemberTemplateDetails(toid);
            value.put("autoredirect",(String)memTemplateDetails.get("autoredirect"));
            value.put("TEMPLATE",(String)memTemplateDetails.get("template"));
            value.put("VBV",(String)memTemplateDetails.get("vbv"));
        }
        catch(Exception e)
        {
            log.error("Exception occur while insert data",e);
            transactionLogger.error("Exception occur while insert data",e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        Connection con = null;
        String checksumAlgo=null;

        Hashtable membersDetails = null;
        membersDetails = merchants.getMemberDetailsForTransaction(toid);


        key = (String)membersDetails.get("clkey");
        checksumAlgo = (String)membersDetails.get("checksumalgo");
        log.debug("Generate the checksum of details");
        transactionLogger.debug("Generate the checksum of details");
        String checksum = null;
        BigDecimal amount = new BigDecimal((String) value.get("TXN_AMT"));
        try
        {
            log.debug("Checksum :::::"+String.valueOf((String) value.get("TOID"))+"=="+(String) value.get("DESCRIPTION")+"==="+amount.doubleValue()+"==="+key+"==="+checksumAlgo);
            transactionLogger.debug("Checksum :::::"+String.valueOf((String) value.get("TOID"))+"=="+(String) value.get("DESCRIPTION")+"==="+amount.doubleValue()+"==="+key+"==="+checksumAlgo);
            checksum = Checksum.generateChecksumV2(String.valueOf((String) value.get("TOID")), (String) value.get("DESCRIPTION"), amount.doubleValue() + "", key, checksumAlgo);
        }
        catch (NoSuchAlgorithmException e)
        {
            log.error("Checksum Exception:::::",e);
            transactionLogger.error("Checksum Exception:::::",e);
            //log("Algorithm Not Found", e);
            throw new ServletException(e);
        }
        value.put("CHECKSUM",checksum);
        transactionUtility.redirectToServlet(pWriter,value,ctoken,MMSERVLET);
    }
}
