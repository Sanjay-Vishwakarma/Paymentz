package net.agent;

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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;

import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: saurabh.b
 * Date: 2/20/13
 * Time: 4:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchantDetails extends HttpServlet
{
    private static Logger Log = new Logger(MerchantDetails.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html");
        HttpSession session = request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        RequestDispatcher rdError=request.getRequestDispatcher("/merchantDetails.jsp?MES=ERR&ctoken=" + user.getCSRFToken());

        AgentFunctions agent=new AgentFunctions();
        if (!agent.isLoggedInAgent(session))
        {   Log.debug("Agent is logout ");
            response.sendRedirect("/Agent/Logout.jsp");
            return;
        }

        Connection conn = null;
        ResultSet rs = null;
        String agentid=(String)session.getAttribute("merchantid");
        StringBuffer error = new StringBuffer();
        String memberid = request.getParameter("memberid");
        String fromdate=null;
        String todate=null;
        /*String fmonth=null;
        String tmonth=null;
        String fyear=null;
        String tyear=null;*/
        String errormsg="";
        int records=15;
        int pageno=1;

        Functions functions = new Functions();

        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date= null;

        if (!ESAPI.validator().isValidInput("memberid ",request.getParameter("memberid"),"Numbers",10,true))
        {   Log.debug("enter valid memberid");
            error.append("Enter Valid Memberid (Allow Only Numeric Values).<br>");
        }
        if(error.length() > 0)
        {
            request.setAttribute("error",error);
            RequestDispatcher rd = request.getRequestDispatcher("/merchantDetails.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        try
        {
            fromdate = ESAPI.validator().getValidInput("fromdate",request.getParameter("fromdate"),"Days",3,true);
            todate = ESAPI.validator().getValidInput("todate",request.getParameter("todate"),"Days",3,true);
            /*fmonth = ESAPI.validator().getValidInput("fmonth",request.getParameter("fmonth"),"Months",2,true);
            tmonth = ESAPI.validator().getValidInput("tmonth",request.getParameter("tmonth"),"Months",2,true);
            fyear = ESAPI.validator().getValidInput("fyear",request.getParameter("fyear"),"Years",4,true);
            tyear = ESAPI.validator().getValidInput("tyear",request.getParameter("tyear"),"Years",4,true);*/
        }
        catch (ValidationException e)
        {
            Log.error("Enter valid input ",e);
            error.append("Enter Valid Input ");
        }

        int start = 0; // start index
        int end = 0; // end index

        try
        {
            pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("pageno",request.getParameter("SPageno"),"Numbers",5,true), 1);
            records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("records",request.getParameter("SRecords"),"Numbers",5,true), 30);
        }
        catch(ValidationException e)
        {
            Log.error("Invalid Page Number or Records",e);
            error.append("Invalid Page Number or Records.<br>");
            pageno = 1;
            records = 30;
        }
        start = (pageno - 1) * records;
        end = records;

        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);

        StringBuffer sb=null;
        StringBuffer count=null;
        Hashtable hash = null;

        fromdate=request.getParameter("fromdate");
        todate= request.getParameter("todate");

        try
        {
            errormsg = errormsg + validateParameters(request);
            if(!errormsg.isEmpty())
            {
                request.setAttribute("catchError",errormsg);
                rdError.forward(request, response);
                return;
            }
            //from Date
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

            Log.debug("From date dd::" + fdate + " MM;;" + fmonth + " YY::" + fyear + " To date dd::" + tdate + " MM::" + tmonth + " YY::" + tyear);

            String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
            String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");

            //conn=Database.getConnection();
            conn=Database.getRDBConnection();
            sb=new StringBuffer("SELECT m.memberid,m.company_name,m.contact_persons,m.contact_emails,m.country,ma.terminalid,ma.isActive,ga.displayname, gt.currency FROM members AS m,member_account_mapping AS ma,gateway_accounts AS ga, merchant_agent_mapping AS mam, gateway_type AS gt WHERE mam.agentId= " + ESAPI.encoder().encodeForSQL(me,agentid)+" AND m.memberid=ma.memberid AND ma.accountid=ga.accountid AND m.memberid=mam.memberid AND ga.pgtypeid=gt.pgtypeid");
            count=new StringBuffer("SELECT count(*) FROM  members AS m,member_account_mapping AS ma,gateway_accounts AS ga, merchant_agent_mapping AS mam where mam.agentId = " + ESAPI.encoder().encodeForSQL(me,agentid) +" AND m.memberid=ma.memberid AND ma.accountid=ga.accountid AND m.memberid=mam.memberid");

            if (functions.isValueNull(fdtstamp))
            {
                sb.append(" and m.dtstamp >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
                count.append(" and m.dtstamp >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
            }

            if(functions.isValueNull(tdtstamp))
            {
                sb.append(" and m.dtstamp <= " +ESAPI.encoder().encodeForSQL(me, tdtstamp));
                count.append(" and m.dtstamp <= " + ESAPI.encoder().encodeForSQL(me, tdtstamp));
            }

            if(functions.isValueNull(memberid))
            {
                sb.append(" and m.memberid = '" + memberid+"'");
                count.append(" and m.memberid = '" +memberid+"'");
            }

            sb.append(" order by m.memberid desc LIMIT " + start + "," + end);
            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(sb.toString(), conn));

            rs = Database.executeQuery(count.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));

            request.setAttribute("transdetails", hash);
            request.setAttribute("memberid", memberid);

            request.setAttribute("fdtstamp", fdtstamp);
            request.setAttribute("tdtstamp", tdtstamp);
        }
        catch (SQLException e)
        {
            Log.error("SQLException While Fetching Record ",e);
            error.append("Internal error while accessing data.");
        }
        catch (SystemError systemError)
        {
            Log.error("SystemError While Fetching Record",systemError);
            error.append("Internal error while accessing data.");
        }
        catch (ParseException e)
        {
            error.append("Internal error while accessing data.");
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        request.setAttribute("error",error.toString());
        RequestDispatcher rd = request.getRequestDispatcher("/merchantDetails.jsp?ctoken="+user.getCSRFToken());
        rd.forward(request, response);
    }

    private String validateParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.PAGENO);
        inputFieldsListOptional.add(InputFields.RECORDS);
        inputFieldsListOptional.add(InputFields.MEMBERID);
        inputFieldsListOptional.add(InputFields.FDATE);
        inputFieldsListOptional.add(InputFields.TDATE);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req, inputFieldsListOptional, errorList, true);

        if (!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    Log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage();
                }
            }
        }
        return error;
    }
}