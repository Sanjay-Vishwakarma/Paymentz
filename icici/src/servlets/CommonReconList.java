import com.directi.pg.*;
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
import java.sql.SQLException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Nishant
 * Date: 5/14/14
 * Time: 2:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommonReconList extends HttpServlet
{
    private static Logger log = new Logger(CommonReconList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        log.debug("Entering in commonreconcilationlist");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        String errormsg="";
        String EOL = "<BR>";

        log.debug("success");

        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        Connection conn = null;
        ResultSet rs =  null;
        int records=15;
        int pageno=1;
        try
        {
            validateOptionalParameter(req);
        }
        catch (ValidationException e)
        {
            log.error("Enter valid input",e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            req.setAttribute("errormessage",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/commonreconlist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }

        String  status=null;
        String trackingid = req.getParameter("trackingid");
        String memberid = req.getParameter("toid");
        String accountid = req.getParameter("accountid");
        String paymentordernumber = req.getParameter("paymentnumber");
        String desc = req.getParameter("description");
        String fdate = req.getParameter("fdate");
        String tdate = req.getParameter("tdate");
        String fmonth = req.getParameter("fmonth");
        String tmonth = req.getParameter("tmonth");
        String fyear = req.getParameter("fyear");
        String tyear = req.getParameter("tyear");
        status = Functions.checkStringNull(req.getParameter("status"));
        Functions functions =new Functions();
        StringBuffer trackingIds = new StringBuffer();
        if (functions.isValueNull(trackingid))
        {
            List<String> trackingidList=null;
            if(trackingid.contains(","))
            {
                trackingidList  = Arrays.asList(trackingid.split(","));
            }
            else
            {
                trackingidList= Arrays.asList(trackingid.split(" "));
            }

            int i = 0;
            Iterator itr = trackingidList.iterator();
            while (itr.hasNext())
            {
                if (i != 0)
                {
                    trackingIds.append(",");
                }
                trackingIds.append("" + itr.next() + "");
                i++;
            }
        }

        Calendar rightNow = Calendar.getInstance();

        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

        if (fmonth == null) fmonth = "" + (rightNow.get(rightNow.MONTH));
        if (tmonth == null) tmonth = "" + (rightNow.get(rightNow.MONTH));

        if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);

        String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
        String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");
        req.setAttribute("trackingid", trackingIds);
        req.setAttribute("paymentnumber", paymentordernumber);
        req.setAttribute("description", desc);
        req.setAttribute("toid", memberid);
        req.setAttribute("accountid", accountid);

        req.setAttribute("fdtstamp", fdtstamp);
        req.setAttribute("tdtstamp", tdtstamp);
        int start = 0; // start index
        int end = 0; // end index
        Hashtable hash = null;
        try
        {
            validateOptionalParameter(req);
        }
        catch(ValidationException e)
        {
            log.error("Invalid page no or records",e);
            pageno = 1;
            records = 30;
        }
        pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 30);

        start = (pageno - 1) * records;
        end = records;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("SELECT trackingid,notificationUrl,toid,accountid,description,fromid,amount,status,timestamp,accountid, paymentid,refundamount,captureamount,chargebackamount,payoutamount,fromtype FROM transaction_common WHERE (status IN('authfailed','authstarted','markedforreversal','capturestarted','cancelstarted','payoutstarted','payoutcancelsuccessful','capturesuccess','authstarted_3D','failed') OR (STATUS IN('chargeback','chargebackreversed')AND chargebackamount>0.00))");
            StringBuffer countquery = new StringBuffer("select count(*) from transaction_common where (status IN('authfailed','authstarted','markedforreversal','capturestarted','cancelstarted','payoutstarted','payoutcancelsuccessful','capturesuccess','authstarted_3D','failed') OR (STATUS IN('chargeback','chargebackreversed')AND chargebackamount>0.00))");
            if (isValueNull(memberid))
            {
                query.append(" and toid=" + ESAPI.encoder().encodeForSQL(me,memberid));
                countquery.append(" and toid=" + ESAPI.encoder().encodeForSQL(me,memberid));
            }
            if (isValueNull(accountid))
            {
                query.append(" and accountid=" + ESAPI.encoder().encodeForSQL(me,accountid));
                countquery.append(" and accountid=" + ESAPI.encoder().encodeForSQL(me,accountid));
            }
           /* if (isValueNull(trackingid))
            {
                query.append(" and trackingid=" + ESAPI.encoder().encodeForSQL(me,trackingid));
                countquery.append(" and trackingid=" + ESAPI.encoder().encodeForSQL(me,trackingid));
            }*/

            if (isValueNull(trackingid))
            {
                //query.append(" and trackingid='" + trackingid+"'");
                query.append(" and trackingid IN (" + trackingIds.toString() + ")");
                countquery.append(" and trackingid IN (" + trackingIds.toString() + ")");
                //countquery.append(" and trackingid='" + trackingid+"'");
            }

            if (isValueNull(paymentordernumber))
            {
                query.append(" and paymentid='" + paymentordernumber+"'");
                countquery.append(" and paymentid='" + paymentordernumber+"'");
            }
            if (isValueNull(desc))
            {
                query.append(" and description='" + ESAPI.encoder().encodeForSQL(me,desc)+"'");
                countquery.append(" and description='" + ESAPI.encoder().encodeForSQL(me,desc)+"'");
            }
            if (isValueNull(status) && !status.equalsIgnoreCase("all"))
            {
                query.append(" and status = '" +status+"'");
                countquery.append(" and status = '" +  status+"'");
            }
            if (isValueNull(fdtstamp))
            {
                query.append(" and dtstamp >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
                countquery.append(" and dtstamp >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
            }
            if (isValueNull(tdtstamp))
            {
                query.append(" and dtstamp <= " +ESAPI.encoder().encodeForSQL(me, tdtstamp));
                countquery.append(" and dtstamp <= " + ESAPI.encoder().encodeForSQL(me, tdtstamp));
            }

            query.append(" order by trackingid desc LIMIT " + start + "," + end);
            log.debug("query executed common recon---"+query);


            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));

            rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
            req.setAttribute("transdetails", hash);
        }
        catch(SystemError s)
        {
            log.error("SystemError",s);
            req.setAttribute("error","Invalid Tracking id");
            RequestDispatcher rd= req.getRequestDispatcher("/commonreconlist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req,res);
            return;
        }
        catch (SQLException e)
        {
            log.error("SQL Exception",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }

        RequestDispatcher rd = req.getRequestDispatcher("/commonreconlist.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }
    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);
        inputFieldsListMandatory.add(InputFields.TRACKINGID_SMALL);
        inputFieldsListMandatory.add(InputFields.TOID);
        inputFieldsListMandatory.add(InputFields.ACCOUNTID_SMALL);
        inputFieldsListMandatory.add(InputFields.PAYMENTNUMBER);
        inputFieldsListMandatory.add(InputFields.DESCRIPTION);
        inputFieldsListMandatory.add(InputFields.FROMDATE);
        inputFieldsListMandatory.add(InputFields.TODATE);
        inputFieldsListMandatory.add(InputFields.FROMMONTH);
        inputFieldsListMandatory.add(InputFields.TOMONTH);
        inputFieldsListMandatory.add(InputFields.FROMYEAR);
        inputFieldsListMandatory.add(InputFields.TOYEAR);
        //inputFieldsListMandatory.add(InputFields.STATUS);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }
    public boolean isValueNull(String str)
    {
        if(str != null && !str.equals("null") && !str.equals(""))
        {
            return true;
        }
        return false;
    }
}