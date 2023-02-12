import com.directi.pg.*;
import com.directi.pg.core.paymentgateway.SBMPaymentGateway;
import com.manager.TerminalManager;
import com.manager.vo.TerminalVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.Codec;
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


public class ReverseList extends HttpServlet
{
    private static Logger logger = new Logger(ReverseList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        logger.error("INSIDE ReverseList class..");
        String merchantid   = null;
        HttpSession session = req.getSession();
        res.setContentType("text/html");
        Merchants merchants = new Merchants();
        //ServletContext application = getServletContext();

        if (!merchants.isLoggedIn(session))
        {
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        logger.debug("CSRF check successful ");
        String error        = "";
       //PrintWriter out    = res.getWriter();
        String description  = null;
        String trakingid    = null;
        String terminalid   = null;
        String paymentId    = null;
        String accountid    = null;

        terminalid  = req.getParameter("terminalid");
        String sb   = req.getParameter("terminalbuffer");
        paymentId   = req.getParameter("paymentid");

        //boolean isOptional = false;
        int start   = 0; // start index
        int end     = 0; // end index
        int pageno  = 1;
        int records = 30;

        String EOL      = "<BR>";
        String errormsg = "";

        Functions functions = new Functions();

        /*if(functions.isValueNull(req.getParameter("STrakingid")))
        {
            if(!functions.isNumericVal(req.getParameter("STrakingid")))
            {
                logger.error("Invalid TrackingID allowed only numeric value e.g.[0 to 9] .");
                error="Invalid TrackingID allowed only numeric value e.g.[0 to 9].";
                req.setAttribute("error",error);
                RequestDispatcher rd = req.getRequestDispatcher("/reverselist.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
        }*/
        if (!ESAPI.validator().isValidInput("STrakingid",req.getParameter("STrakingid"),"OnlyNumber",20,true))
        {
            logger.error("step 1: INSIDE invalid trackingid:: "+req.getParameter("STrakingid"));
            logger.error("Invalid TrackingID."+EOL);
            error=error+"Invalid TrackingID.";
        }

        else
        {
            logger.error(" step 2: INSIDE else part of valid trackingid");
            if(functions.isValueNull(req.getParameter("STrakingid")))
            {
                logger.error("step 1 :  "+req.getParameter("STrakingid"));
                trakingid = req.getParameter("STrakingid");
            }
            if(functions.isValueNull(req.getParameter("icicitransid")))
            {
                logger.error("step 2:  "+req.getParameter("icicitransid"));
                trakingid = req.getParameter("icicitransid");
            }
        }

        //System.out.println("id---"+trakingid);
        if (!ESAPI.validator().isValidInput("paymentid",req.getParameter("paymentid"),"SafeString",100,true))
        {
            logger.error("Invalid PaymentId."+EOL);
            error=error+"Invalid PaymentId.";
        }
        else
        {
            paymentId = req.getParameter("paymentid");
        }

        if (!ESAPI.validator().isValidInput("SDescription",req.getParameter("SDescription"),"Description",60,true))
        {
            logger.error("Invalid Order ID");
            error   = error + "Invalid Order ID."+EOL;
        }
        else
        {
            description = req.getParameter("SDescription");
        }
        //System.out.println("id---"+description);

        if (!ESAPI.validator().isValidInput("bank",req.getParameter("bank"),"Numbers",20,true))
        {
            logger.error("Invalid bank");
           // error=error+="Invalid bank."+EOL;
        }
        else
        {
            accountid = req.getParameter("bank");
        }

        if(functions.isValueNull(error))
        {
            logger.error("INSIDE error condition...");
            req.setAttribute("error",error);
            RequestDispatcher rd = req.getRequestDispatcher("/reverselist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        try
        {
            logger.error("INSIDE try...");
           // validateOptionalParameter(req);
            InputValidator inputValidator               = new InputValidator();
            List<InputFields> inputFieldsListMandatory  = new ArrayList<InputFields>();
            inputFieldsListMandatory.add(InputFields.PAGENO);
            inputFieldsListMandatory.add(InputFields.RECORDS);

            inputValidator.InputValidations(req, inputFieldsListMandatory, true);
        }
        catch (ValidationException e)
        {
            logger.error("Enter valid input",e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            logger.debug("message..."+e.getMessage());
            req.setAttribute("message",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/reverselist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        pageno      = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records     = Functions.convertStringtoInt(req.getParameter("SRecords"), 30);
        /*try
        {
            pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Spageno",req.getParameter("SPageno"),"Numbers",3,true), 1);
            records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Srecord",req.getParameter("SRecords"),"Numbers",3,true), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));
        }
        catch(ValidationException e)
        {
            logger.error("Invalid page no or records",e);
            pageno = 1;
            records = 15;
        }*/

        // calculating start & end
        start   = (pageno - 1) * records;
        end     = records;

        /*
         HttpSession session=req.getSession();
      if(session.getAttribute("merchantid")==null)
          {
              //----------decoding  data-------------
              str=new String(Base64.decode(data));
              int pos=str.indexOf("|");
              key=str.substring(0,pos);
              checksum=str.substring(pos+1);

              if(Checksum.verifyChecksum(key,checksum))
              {
                  str=new String(Base64.decode(key));

                  int fpos=str.indexOf("|");
                  int lpos=str.indexOf("|",fpos+1);
                  merchantid=str.substring(fpos+1,lpos);
                  session.setAttribute("merchantid",merchantid);
              }
              else
              {
                  //out.println("Wrong Check Sum");
              }
          }  */
/*

        TerminalVO terminalVO = null;
        TerminalManager terminalManager = new TerminalManager();
        try
        {

            terminalVO = terminalManager.getTerminalByTerminalId(terminalid);
        }
        catch (PZDBViolationException e)
        {
            e.printStackTrace();
        }

*/

        Hashtable hash      = null;
        RequestDispatcher rd;
        merchantid                          = (String) session.getAttribute("merchantid");
        TransactionEntry transactionentry   = (TransactionEntry) session.getAttribute("transactionentry");
        Set<String> gatewayTypeSet          = new HashSet();

        TerminalManager terminalManager = new TerminalManager();
        TerminalVO terminalVO           = new TerminalVO();
        functions                       = new Functions();
        StringBuffer terminals          = null;
        try
        {
            logger.error("INSIDE 1st try for terminals method...");
            terminals = terminalManager.getVendorsTerminalByMemberid(merchantid);
        }
        catch (PZDBViolationException e)
        {
            logger.error("DBException---", e);
        }
        if(functions.isValueNull(terminalid) && !terminalid.equalsIgnoreCase("all"))
        {
            try
            {
                logger.error("INSIDE terminalid try block...");
                terminalVO  = terminalManager.getTerminalByTerminalId(terminalid);

                if (terminalVO != null)
                {
                    logger.error("INSIDE terminalVO not null condition");
                    terminals.append(terminalVO.getTerminalId());
                    accountid   = terminalVO.getAccountId();
                }
                else
                {
                    terminalVO  = new TerminalVO();
                }
                terminalVO.setTerminalId("("+terminals.toString()+")");

            }
            catch (PZDBViolationException e)
            {
                logger.error("DBException---",e);
            }
        }
        else
        {
            logger.error("-------------------inside else at 277---------");
            String sb1  = sb.substring(0,sb.length()-1);
            if(functions.isValueNull(terminals.toString()))
                sb1 = sb1+","+terminals.toString();
            sb1+=")";
            terminalVO.setTerminalId(sb1);
        }

        if (terminalVO == null)
        {
            logger.error("INSIDE terminalVO null condition");
            rd = req.getRequestDispatcher("/reverselist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }

        gatewayTypeSet.addAll(transactionentry.getGatewayHash(merchantid,accountid).keySet());

        if(gatewayTypeSet.size()==0)
        {
            logger.error("----------------gatewaytype size ====="+gatewayTypeSet.size());
            gatewayTypeSet.add(SBMPaymentGateway.GATEWAY_TYPE);
        }

        Codec me            = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StringBuilder query = new StringBuilder();

        String tablename    = "";
        Iterator i          = gatewayTypeSet.iterator();
        logger.error("gatewayTypeSet size+++ "+gatewayTypeSet.size());
        logger.error("i+++ "+i);
       /* while(i.hasNext())
        {*/
            tablename = Database.getTableName((String)i.next());
            if(tablename.equals("transaction_icicicredit"))
            {
                logger.error("step 3:: INSIDE transaction_icicicredit table "+trakingid+" "+"transaction_icicicredit");
                query.append("select T.timestamp, T.status,T.icicitransid as trackingid,T.paymentid as paymentid,T.transid,T.description,T.amount,T.captureamount,T.refundamount,T.paymodeid,T.accountid,M.company_name from transaction_icicicredit as T,members as M where icicitransid>1");
            }
            else if(tablename.equals("transaction_qwipi"))
            {
                logger.error("step 4:: INSIDE transaction_qwipi table:: "+trakingid+" "+"transaction_qwipi");
                query.append("select T.timestamp, T.status,T.trackingid as trackingid,T.qwipiPaymentOrderNumber as paymentid,T.transid,T.description,T.amount,T.captureamount,T.refundamount,T.paymodeid,T.accountid,M.company_name,T.terminalid,T.currency from transaction_qwipi as T,members as M where trackingid>1");
            }
            else if(tablename.equals("transaction_ecore"))
            {
                logger.error("step 5:: INSIDE transaction_ecore table:: "+trakingid+" "+"transaction_ecore");
                query.append("select T.timestamp, T.status,T.trackingid as trackingid,T.ecorePaymentOrderNumber as paymentid ,T.transid,T.description,T.amount,T.captureamount,T.refundamount,T.paymodeid,T.accountid,M.company_name,T.terminalid,T.currency from transaction_ecore as T,members as M where trackingid>1");
            }
            else
            {
                logger.error("step 6:: INSIDE else part of table:: "+trakingid+" "+tablename);
                query.append("select T.timestamp, T.status,T.trackingid as trackingid,T.paymentid as paymentid,T.transid,T.description,T.amount,T.captureamount,T.refundamount,T.paymodeid,T.accountid,M.company_name,T.terminalid,T.currency from "+tablename+" as T,members as M where trackingid>1");
                logger.error("query inside while 1:: "+query);
            }

            if (description != null && !description.equalsIgnoreCase(""))
            {
                query.append(" and description like '%" +ESAPI.encoder().encodeForSQL(me,description) + "%'");

            }
            if(paymentId !=null && !paymentId.equalsIgnoreCase(""))
            {
                if(tablename.equals("transaction_icicicredit"))
                {
                    query.append(" and paymentid like '%" +ESAPI.encoder().encodeForSQL(me,paymentId) + "%'");
                }
                else if(tablename.equals("transaction_qwipi"))
                {
                    query.append(" and qwipiPaymentOrderNumber like '%" +ESAPI.encoder().encodeForSQL(me,paymentId) + "%'");
                }
                else if(tablename.equals("transaction_ecore"))
                {
                    query.append(" and ecorePaymentOrderNumber like '%" +ESAPI.encoder().encodeForSQL(me,paymentId) + "%'");
                }
                else
                {
                    query.append(" and paymentid like '%" +ESAPI.encoder().encodeForSQL(me,paymentId) + "%'");
                }
            }
            if (trakingid != null && !trakingid.equalsIgnoreCase(""))
            {
                //System.out.println("inside if --"+trakingid);
                logger.error("step 7:: inside append trackingid case:: "+trakingid);
                if(tablename.equals("transaction_icicicredit"))
                {
                    logger.error("step 1 inside append trackingid case:: "+trakingid);
                    query.append(" and icicitransid=").append(ESAPI.encoder().encodeForSQL(me, trakingid));
                }
                else
                {
                    logger.error("step 2 inside append trackingid case:: "+trakingid);
                    query.append(" and trackingid=").append(ESAPI.encoder().encodeForSQL(me,trakingid));
                    logger.error("query inside while 2:: "+query);
                }
            }
            //query.append(" and terminalid IN " + terminalVO.getTerminalId());
            /*if(terminalVO !=null && functions.isValueNull(terminalVO.getTerminalId()) && terminalVO.getTerminalId().contains(","))*/
            if(terminalVO !=null && functions.isValueNull(terminalVO.getTerminalId()))
            {
                if(tablename.equals("transaction_common"))
                {
                    query.append(" and terminalid IN ").append(terminalVO.getTerminalId());
                }
                else
                {
                    query.append(" and terminalid IN ").append(terminalVO.getTerminalId());
                }
            }

            /*if(functions.isValueNull(terminalid))
            {
                query.append(" and terminalid IN "+terminalid);
            }*/
        /*if(functions.isValueNull(terminalid))
        {
            query.append(" and T.accountid=" +terminalVO.getAccountId()+" and T.paymodeid="+terminalVO.getPaymodeId()+" and T.cardtypeid="+terminalVO.getCardTypeId());
        }*/

            if(tablename.equals("transaction_qwipi"))
            {
                logger.error("step 8:: INSIDE status case:: "+tablename);
                query.append(" and status IN ('settled','capturesuccess') and toid=" +ESAPI.encoder().encodeForSQL(me,merchantid)+ " and T.toid=M.memberid ");
            }
            else if(tablename.equals("transaction_ecore"))
            {
                logger.error("step 9:: INSIDE status case:: "+tablename);
                query.append(" and status IN ('settled','capturesuccess') and toid=" +ESAPI.encoder().encodeForSQL(me,merchantid)+ " and T.toid=M.memberid ");
            }
            else
            {
                logger.error("step 10:: INSIDE status case:: "+tablename);
                query.append(" and status IN ('settled','capturesuccess','reversed') and toid=" +ESAPI.encoder().encodeForSQL(me,merchantid)+ " and T.toid=M.memberid  and T.captureamount>T.refundamount");
                logger.error("query inside while 3:: "+query);
            }
           /* if(i.hasNext())
            {
                query.append(" UNION ");
            }*/

        //logger.debug(query);
        StringBuilder countquery = new StringBuilder("select count(*) from ( " + query + ") as temp ");
        query.append(" order by trackingid desc LIMIT " + start + "," + end);
        logger.error("query inside while 4:: "+query);
        logger.error("QUERY before try for Reverselist::::  " + query.toString());
        logger.error("step 11:: after orderby trackingid:: " + trakingid);
        logger.debug(" query =="+query.toString());
        //logger.debug(query);
        Connection cn = null;
        ResultSet rs = null;
        try
        {
            //cn = Database.getConnection();
            cn = Database.getRDBConnection();
            logger.debug("calling database to fetch transaction details for reverse");
            hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), cn));
            logger.debug("calling database to fetch count of transaction details for reverse");
            logger.error("Query for ReverseList.java ::: "+query);
            rs = Database.executeQuery(countquery.toString(), cn);
            logger.error("CountQuery for ReverseList.java ::: "+countquery);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
            {
                hash.put("records", "" + (hash.size() - 2));
            }

            //	out.println(hash.toString());
            req.setAttribute("transdetails", hash);
            req.setAttribute("terminal", terminalid);
            req.setAttribute("terminalbuffer", sb);
            req.setAttribute("trackingid", req.getParameter("STrackingid"));
            req.setAttribute("icicitransid", req.getParameter("icicitransid"));
            req.setAttribute("desc", description);
            req.setAttribute("paymentid", paymentId);
            session.setAttribute("bank",terminalid);

        }
        catch (SystemError se)
        {
            logger.error("System error",se);
            Functions.NewShowConfirmation1("Error", "Internal System Error while getting list of Transactions");
        }
        catch (Exception e)
        {
            logger.error("Exception Occure while fetching reversal record", e);
            Functions.NewShowConfirmation1("Error", "Internal System Error while getting list of Transactions");
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(cn);
        }
        rd = req.getRequestDispatcher("/reverselist.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }

    /*private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        inputValidator.InputValidations(req, inputFieldsListMandatory, true);
    }*/
}