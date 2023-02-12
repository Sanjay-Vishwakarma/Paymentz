package payment;

import com.directi.pg.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Sep 29, 2012
 * Time: 2:40:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class PayVoucherProcess  extends HttpServlet
{

    private static Logger log = new Logger(PayVoucherProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PayVoucherProcess.class.getName());
    Connection con = null;
    private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.PayVoucherServlet");
    final static String PVSERVLET =  RB.getString("PVSERVLET");
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("Entered in PayVoucherProcess");
        transactionLogger.debug("Entered in PayVoucherProcess");
        PrintWriter pWriter = res.getWriter();
        res.setContentType("text/html");
        pWriter.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">");
        String ctoken = null;
        Functions functions=new Functions();
        HttpSession session = req.getSession();

        TransactionUtility transactionUtility = new TransactionUtility();
        Transaction transaction = new Transaction();
        Merchants merchants = new Merchants();

        if(req.getSession()== null)
          {
             pWriter.print(Functions.ShowMessage("Message","Your session is Expire."));
             return;
          }
          if(session !=null)
          {
              ctoken =  (String)session.getAttribute("ctoken");

              log.debug("CSRF token from session"+ctoken);
              transactionLogger.debug("CSRF token from session"+ctoken);
          }
          /*if(ctoken!=null && !ctoken.equals("") && !req.getParameter("ctoken").equals(ctoken))
          {

              log.debug("CSRF token not match ");
              pWriter.print(Functions.ShowMessage("Message","UnAuthorized Access"));
          }*/
        String template=null;
        String vbv=null,autoredirect=null;

        int TRACKING_ID=0;
        String sTransParameter=req.getParameter("hiddenvariables");
        Hashtable value=functions.getHashValue(sTransParameter) ;
        String header=req.getParameter("HEADDER");
        value.put("HEADER",header);
        value.put("ctoken",ctoken);

        String toid= (String)value.get("TOID");
        try
        {
            //inserting transaction Details
            TRACKING_ID = transaction.insertTransCommon(toid,(String)value.get("TOTYPE"),(String)value.get("FROMID"),(String)value.get("FROMTYPE"),(String)value.get("DESCRIPTION"),(String)value.get("ORDER_DESCRIPTION"),(String)value.get("TXN_AMT"),(String)value.get("REDIRECTURL"),(String)value.get("ACCOUNTID"),Integer.parseInt((String)value.get("PAYMODEID")),Integer.parseInt((String)value.get("CARDTYPEID")),(String)value.get("CURRENCY"),header,null);
            value.put("TRACKING_ID",TRACKING_ID);

            String INVOICE_NO = null;

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
           Functions.ShowMessage("Error","Internal error") ;
          // TODO error page
        }
        transactionUtility.redirectToServlet(pWriter,value,ctoken,PVSERVLET);
    }
}
