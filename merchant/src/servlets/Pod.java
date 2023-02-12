import com.directi.pg.*;
import com.directi.pg.core.paymentgateway.SBMPaymentGateway;
import com.manager.TerminalManager;
import com.manager.vo.TerminalVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.*;

//import javax.servlet.ServletContext;
//import java.io.PrintWriter;



public class Pod extends HttpServlet
{
    private static Logger log = new Logger(Pod.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        log.debug("Entering in POD");
        HttpSession session = req.getSession();
        Merchants merchants = new Merchants();

        if (!merchants.isLoggedIn(session))
        {
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        log.debug("CSRF check successful ");
        String merchantid = (String) session.getAttribute("merchantid");
       // ServletContext application = getServletContext();
        String terminalid=null;
        //String message=req.getParameter("error");

        String sb = req.getParameter("terminalbuffer");

        int start = 0; // start index
        int end = 0; // end index
        int records=5;
        int pageno=1;
        res.setContentType("text/html");
        Functions functions = null;

        String errormsg = "";
      //  String EOL = "<BR>";

       // PrintWriter out = res.getWriter();
        try
        {
           // validateOptionalParameter(req);
            InputValidator inputValidator = new InputValidator();
            List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
            inputFieldsListMandatory.add(InputFields.PAGENO);
            inputFieldsListMandatory.add(InputFields.RECORDS);

            inputValidator.InputValidations(req,inputFieldsListMandatory,true);
        }
        catch (ValidationException e)
        {
            log.error("Enter valid input",e);
            errormsg += /*"<center><font class=\"text\" face=\"arial\"><b>"+ */errormsg + e.getMessage() /*+ EOL + "</b></font></center>"*/;
            log.debug("message..."+e.getMessage());
            req.setAttribute("message",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/pod.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 5);
        // calculating start & end
        start = (pageno - 1) * records;
        end = records;

        String trackingid = Functions.checkStringNull(req.getParameter("trackingid"));
        String paymentId=null;
        String accountid=null;
        String isPartialCapture =null;
        terminalid = req.getParameter("terminalid");
        try
        {
            trackingid = ESAPI.validator().getValidInput("trackingid",req.getParameter("trackingid"),"OnlyNumber",20,true);
            paymentId = ESAPI.validator().getValidInput("paymentid",req.getParameter("paymentid"),"SafeString",100,true);
            //accountid = ESAPI.validator().getValidInput("bank",req.getParameter("bank"),"Numbers",20,true);
            isPartialCapture = ESAPI.validator().getValidInput("partialCapture",req.getParameter("partialCapture"),"SafeString",20,true);
            log.debug("is partial capture---"+isPartialCapture);
        }
        catch(ValidationException e)
        {
            log.error("Invalid icicitransid",e);
            String error="Invalid TrackingID allowed only numeric value e.g.[0 to 9].";
            req.setAttribute("error", error);
            RequestDispatcher rd = req.getRequestDispatcher("/pod.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        Hashtable hash = null;
        RequestDispatcher rd;
        org.owasp.esapi.codecs.Codec MYSQL_CODEC = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        log.debug("Fatching records from select query in POD");

        TransactionEntry transactionentry = (TransactionEntry) session.getAttribute("transactionentry");
        Set<String> gatewayTypeSet = new HashSet();

        TerminalVO terminalVO = new TerminalVO();
        TerminalManager terminalManager = new TerminalManager();
        functions = new Functions();

        if(functions.isValueNull(terminalid) && !terminalid.equalsIgnoreCase("all"))
        {
            try
            {
                terminalVO=terminalManager.getTerminalByTerminalId(terminalid);
                if (terminalVO !=null)
                {
                    terminalVO.setTerminalId("("+terminalVO.getTerminalId()+")");
                    accountid=terminalVO.getAccountId();
                }

            }
            catch (PZDBViolationException e)
            {
                //e.printStackTrace();
                log.debug("PZDBViolationException :" + e);
            }
        }
        else
        {
            terminalVO.setTerminalId(sb);
        }

        if (terminalVO==null)
        {
            rd = req.getRequestDispatcher("/pod.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }

        gatewayTypeSet.addAll(transactionentry.getGatewayHash(merchantid,accountid).keySet());
        if(gatewayTypeSet.size()==0)
        {
         gatewayTypeSet.add(SBMPaymentGateway.GATEWAY_TYPE);
        }
        String tablename="";
        StringBuilder query = new StringBuilder();

        Iterator i = gatewayTypeSet.iterator();
        while(i.hasNext())
        {
        tablename = Database.getTableName((String)i.next());
        if(tablename.equals("transaction_icicicredit"))
        {
        query.append("select T.icicitransid as trackingid,T.description,T.amount,T.accountid,T.status,T.dtstamp,T.paymodeid,T.cardtype from transaction_icicicredit as T, members as M");
        }
        else
        {
        query.append("select T.trackingid as trackingid,T.description,T.amount,T.accountid,T.status,T.dtstamp,T.paymodeid,T.cardtype,T.terminalid,T.currency from  " +tablename+ " as T, members as M ");
        }

        query.append(" where T.toid=M.memberid and T.toid="+ESAPI.encoder().encodeForSQL(MYSQL_CODEC, merchantid)+" and (T.pod is null OR T.podbatch is null) and (T.status='authsuccessful' OR (T.status='capturesuccess' AND M.isPODRequired=TRUE)) ");
        if (functions.isValueNull(trackingid))
        {
            if(tablename.equals("transaction_icicicredit"))
            {
            query.append(" and icicitransid='"+ESAPI.encoder().encodeForSQL(MYSQL_CODEC, trackingid)+"'");
            }
            else
            {
            query.append(" and trackingid='"+ESAPI.encoder().encodeForSQL(MYSQL_CODEC, trackingid)+"'");
            }
        }
            if (functions.isValueNull(paymentId))
            {
                if(tablename.equals("transaction_qwipi"))
                {
                    query.append(" and qwipiPaymentOrderNumber='").append(ESAPI.encoder().encodeForSQL(MYSQL_CODEC, paymentId)).append("'");
                }
                else if(tablename.equals("transaction_ecore"))
                {
                    query.append(" and ecorePaymentOrderNumber='").append(ESAPI.encoder().encodeForSQL(MYSQL_CODEC, paymentId)).append("'");
                }
                else
                {
                    query.append(" and paymentid='").append(ESAPI.encoder().encodeForSQL(MYSQL_CODEC, paymentId)).append("'");
                }
            }
            /*if(terminalVO !=null && functions.isValueNull(terminalVO.getTerminalId()) && terminalVO.getTerminalId().contains(","))*/
            if(terminalVO !=null && functions.isValueNull(terminalVO.getTerminalId()))
            {
                query.append(" and terminalid IN ").append(terminalVO.getTerminalId());
            }

            /*if(i.hasNext())
            {
                query.append(" UNION ");
            }*/
        }

        StringBuilder countquery = new StringBuilder("select count(*) from ( ").append(query).append( ") as temp ");
        query.append(" order by trackingid desc LIMIT ").append(start).append(",").append(end);

        Connection conn = null;
        ResultSet rs = null;
        try

        {   log.debug(query.toString());
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            log.error("Query for Pod.java++++  "+query);
            //	out.println(hash.toString());
            rs = Database.executeQuery(countquery.toString(), conn);
            log.error("Countquery for pod.java+++ "+countquery);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
            {
                hash.put("records", "" + (hash.size() - 2));
            }

            //	out.println(hash.toString());
            req.setAttribute("poddetails", hash);
            session.setAttribute("bank",accountid);


            req.setAttribute("terminal",terminalid);
            req.setAttribute("paymentid",paymentId);

        }
        catch (SystemError se)
        {
            log.error("System error in capture::::",se);
            //System.out.println(se.toString());
            Functions.NewShowConfirmation1("Error", "Internal System Error while getting list of Transactions");

        }
        catch (Exception e)
        {
            log.error("Exception occure",e);
            Functions.NewShowConfirmation1("Error", "Internal System Error while getting list of Transactions");
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        if (isPartialCapture!=null && trackingid!=null)
        {
            rd = req.getRequestDispatcher("/partialCapture.jsp?ctoken="+user.getCSRFToken());
        }
        else
        {
            rd = req.getRequestDispatcher("/pod.jsp?ctoken="+user.getCSRFToken());
        }
        rd.forward(req, res);
    }

   /* private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }*/
}