import com.directi.pg.*;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class RejectedTransactionsList extends HttpServlet
{
    private static Logger log = new Logger(RejectedTransactionsList.class.getName());
    private Functions functions = new Functions();
    Merchants merchants= new Merchants();
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(req, res);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = Functions.getNewSession(request);
        if (!merchants.isLoggedIn(session))
        {
            response.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        //boolean flag=false;
        String errorMsg="";
        String fromdate = request.getParameter("fromdate");
        String todate = request.getParameter("todate");
        String startTime    = request.getParameter("starttime");
        String endTime      = request.getParameter("endtime");
        String EOL = "<BR>";

        String toId = (String) session.getAttribute("merchantid");

        if(functions.isValueNull(request.getParameter("action")) && "RejectedTransactionsList".equals(request.getParameter("action")) && functions.isValueNull(request.getParameter("STrackingid")) )
        {
            String icicitransid = request.getParameter("STrackingid");
            try
            {
                Hashtable hash = getTransactionDetails(icicitransid);
                request.setAttribute("transactionsdetails", hash);
                RequestDispatcher rd = request.getRequestDispatcher("/rejectedTransactionDetails.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request, response);
            }
            catch (SystemError se)
            {
                log.error("SystemError:::::",se);
                errorMsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errorMsg + se.getMessage() + EOL + "</b></font></center>";
                request.setAttribute("errormessage",errorMsg);
                RequestDispatcher rd = request.getRequestDispatcher("/rejectedTransactionsList.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request, response);
            }
            catch (Exception e)
            {
                errorMsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errorMsg + e.getMessage() + EOL + "</b></font></center>";
                log.error("Exception:::::", e);
                request.setAttribute("errormessage",errorMsg);
                RequestDispatcher rd = request.getRequestDispatcher("/rejectedTransactionsList.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request, response);
            }
            return;
        }
        ValidationErrorList validationErrorList =null;
        RequestDispatcher rdError=request.getRequestDispatcher("/rejectedTransactionsList.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        try
        {
            validationErrorList=validateOptionalParameter(request);
            if(!validationErrorList.isEmpty())
            {
                request.setAttribute("validationErrorList",validationErrorList);
                rdError.forward(request,response);
                return;
            }
        }
        catch (ValidationException e)
        {
            log.error("ValidationException:::::: ",e);
            request.setAttribute("errormessage",e.getMessage());
            RequestDispatcher rd = request.getRequestDispatcher("/rejectedTransactionsList.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
        }

        //String toId = (String) session.getAttribute("merchantid");
        String description=request.getParameter("description");
        String rejectReason=request.getParameter("rejectreason");
        String name = request.getParameter("name");
        String amount = request.getParameter("amount");
        String firstSix = request.getParameter("firstsix");
        String lastFour = request.getParameter("lastfour");
        String emailAddr = request.getParameter("emailaddr");
        String perfectMatch= Functions.checkStringNull(request.getParameter("perfectmatch"));

        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date= null;

        int pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
        int records = Functions.convertStringtoInt(request.getParameter("SRecords"), 30);

        if(functions.isFutureDateComparisonWithFromAndToDate(fromdate, todate, "dd/MM/yyyy"))
        {
            request.setAttribute("catchError","Invalid From & To date");
            rdError.forward(request, response);
            return;
        }

        try
        {

            date=sdf.parse(fromdate);
            rightNow.setTime(date);
            String fdate=String.valueOf(rightNow.get(Calendar.DATE));
            String fmonth=String.valueOf(rightNow.get(Calendar.MONTH));
            String fyear=String.valueOf(rightNow.get(Calendar.YEAR));

            //to Date
            date=sdf.parse(todate);
            rightNow.setTime(date);
            String tdate=String.valueOf(rightNow.get(Calendar.DATE));
            String tmonth=String.valueOf(rightNow.get(Calendar.MONTH));
            String tyear=String.valueOf(rightNow.get(Calendar.YEAR));

            String startTimeArr[]   = startTime.split(":");
            String endTimeArr[]     = endTime.split(":");

            String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, startTimeArr[0], startTimeArr[1], startTimeArr[2]);
            String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, endTimeArr[0], endTimeArr[1], endTimeArr[2]);

            //String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
            //String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");

            if(!functions.isValueNull(toId))
            {
                StringBuilder sb=new StringBuilder();

                Hashtable hash=merchants.getMemberDetails(toId);
                Enumeration enu3 = hash.keys();
                String key3 = "";
                while (enu3.hasMoreElements())
                {
                    key3 = (String) enu3.nextElement();
                    toId=(String) hash.get(key3);
                    sb.append((String) hash.get(key3) + ",");
                }
                if (sb.length() > 0 && sb.charAt(sb.length()-1)==',')
                {
                    toId = sb.substring(0, sb.length()-1);
                }
            }

            Hashtable hash = null;

            hash = listTransactions(toId,description,name,emailAddr,rejectReason,amount,firstSix,lastFour,tdtstamp, fdtstamp,records, pageno, perfectMatch);

            request.setAttribute("transactionsdetails", hash);
            RequestDispatcher rd = request.getRequestDispatcher("/rejectedTransactionsList.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
        }
        catch (SystemError se)
        {
            log.error("SystemError::::::",se);
            request.setAttribute("errormessage",se.getMessage());
            RequestDispatcher rd = request.getRequestDispatcher("/rejectedTransactionsList.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
        }
        catch (Exception e)
        {
            log.error("Exception::::",e);
            request.setAttribute("errormessage",e.getMessage());
            RequestDispatcher rd = request.getRequestDispatcher("/rejectedTransactionsList.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
        }
    }
    public Hashtable listTransactions(String toId,String description,String name,String email,String rejectReason,String amount, String firstSix, String lastFour,String tdtstamp, String fdtstamp, int records, int pageno, String perfectmatch) throws SystemError
    {
        Hashtable hash = null;
        int start = 0; // start index
        int end = 0; // end index
        start = (pageno - 1) * records;
        end = records;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        fdtstamp= ESAPI.encoder().encodeForSQL(me,fdtstamp);
        tdtstamp=ESAPI.encoder().encodeForSQL(me,tdtstamp);
        Connection conn = null;
        ResultSet rs = null;
        Functions functions = new Functions();
        try
        {
            conn = Database.getConnection();
            StringBuffer query = new StringBuffer("SELECT * from transaction_fail_log where id>0 ");
            StringBuffer countquery = new StringBuffer("select count(*) from transaction_fail_log as temp where id>0  ");
            if (functions.isValueNull(amount))
            {
                query.append(" and amount = '" + amount+"'");
                countquery.append(" and amount = '" +amount+"'");
            }
            if (functions.isValueNull(email))
            {
                query.append(" and email = '" + email+"'");
                countquery.append(" and email = '" + email+"'");
            }
            if (functions.isValueNull(toId))
            {
                query.append(" and toid in (" +toId+")");
                countquery.append(" and toid in(" +toId+")");
            }
            if (functions.isValueNull(rejectReason))
            {
                query.append(" and rejectreason = '" + rejectReason+"'");
                countquery.append(" and rejectreason = '" + rejectReason+"'");
            }
            if (functions.isValueNull(description))
            {
                query.append(" and description = '" + description+"'");
                countquery.append(" and description = '" + description+"'");
            }
            if (functions.isValueNull(name))
            {
                String arr[]=name.split(" ");
                String firstName="";
                String lastName="";
                try
                {
                    firstName=arr[0];
                }
                catch (ArrayIndexOutOfBoundsException e)
                {
                    firstName="";
                }
                try
                {
                    lastName=arr[1];
                }
                catch (ArrayIndexOutOfBoundsException e)
                {
                    lastName="";
                }
                if(functions.isValueNull(firstName))
                {
                    query.append(" and firstname = '" + firstName+"'");
                    query.append(" and lastname = '" + lastName+"'");
                }
                if(functions.isValueNull(lastName))
                {
                    countquery.append(" and firstname = '" + firstName+"'");
                    countquery.append(" and lastname = '" + lastName+"'");
                }
            }
            if (functions.isValueNull(firstSix))
            {
                query.append(" and firstsix = '" + firstSix+"'");
                countquery.append(" and firstsix = '" + firstSix+"'");
            }
            if (functions.isValueNull(lastFour))
            {
                query.append(" and lastfour = '" + lastFour+"'");
                countquery.append(" and lastfour = '" + lastFour+"'");
            }
            if (functions.isValueNull(fdtstamp))
            {
                query.append(" and dtstamp >= '" + fdtstamp+"'");
                countquery.append(" and dtstamp >= '" +fdtstamp+"'");
            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" and dtstamp <= '" +tdtstamp+"'");
                countquery.append(" and dtstamp <= '" + tdtstamp+"'");
            }
            query.append(" order by  dtstamp DESC");
            query.append(" limit " + start + "," + end);

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0){
                hash.put("records", "" + (hash.size() - 2));

            }
        }
        catch (SQLException se)
        {
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        return hash;
    }
    public Hashtable getTransactionDetails(String iciciTransId) throws SystemError
    {
        Hashtable hash = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        try
        {
            conn = Database.getConnection();
            StringBuilder query = new StringBuilder("SELECT tfl.*,from_unixtime(tfl.dtstamp) as transactiontime,m.* from transaction_fail_log as tfl left join members as m on tfl.toid=m.memberid where id=?");
            pstmt=conn.prepareStatement(query.toString());
            pstmt.setString(1,iciciTransId);
            hash = Database.getHashFromResultSetForTransactionEntry(pstmt.executeQuery());
        }
        catch (SQLException se)
        {
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return hash;
    }
    private ValidationErrorList validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.FDATE);
        inputFieldsListMandatory.add(InputFields.TDATE);
        inputFieldsListMandatory.add(InputFields.NAME_SMALL);
        inputFieldsListMandatory.add(InputFields.DESCRIPTION);
        inputFieldsListMandatory.add(InputFields.AMOUNT);
        inputFieldsListMandatory.add(InputFields.FIRSTSIX);
        inputFieldsListMandatory.add(InputFields.LASTFOUR);
        inputFieldsListMandatory.add(InputFields.EMAILADDR);
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListMandatory, errorList,true);
        return errorList;
    }
}
