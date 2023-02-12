package servlets;

import com.directi.pg.Admin;
import com.directi.pg.ExportTransactionExcel;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

/**
 * Created by Vivek on 8/27/2019.
 */
public class ExportSFTPTransaction extends HttpServlet
{
    private static Logger log = new Logger(ExportSFTPTransaction.class.getName());
    Functions functions = new Functions();
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        ExportTransactionExcel exportTransactionExcel=new ExportTransactionExcel();
        Hashtable hashtable=new Hashtable();
        RequestDispatcher rd = request.getRequestDispatcher("/exportSFTPTransaction.jsp?ctoken="+user.getCSRFToken());
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fdate = request.getParameter("fdate");
        String tdate = request.getParameter("tdate");
        String fmonth = request.getParameter("fmonth");
        String tmonth = request.getParameter("tmonth");
        String fyear = request.getParameter("fyear");
        String tyear = request.getParameter("tyear");
        String startTime =request.getParameter("starttime");
        String endTime =request.getParameter("endtime");
        String errormsg="";
        String EOL="<br>";

        Calendar rightNow = Calendar.getInstance();

        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(Calendar.DATE);

        if (fmonth == null) fmonth = "" + (rightNow.get(Calendar.MONTH));
        if (tmonth == null) tmonth = "" + (rightNow.get(Calendar.MONTH));

        if (fyear == null) fyear = "" + rightNow.get(Calendar.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(Calendar.YEAR);

        startTime=startTime.trim();
        endTime=endTime.trim();

        if (!functions.isValueNull(startTime))
        {
            startTime="00:00:00";
        }
        if (!functions.isValueNull(endTime))
        {
            endTime="23:59:59";
        }

        fmonth=String.valueOf(Integer.parseInt(fmonth)+1);
        tmonth=String.valueOf(Integer.parseInt(tmonth)+1);
        if (fmonth.length()==1)
            fmonth="0"+fmonth;
        if (tmonth.length()==1)
            tmonth="0"+tmonth;
        if (fdate.length()==1)
            fdate="0"+fdate;
        if (tdate.length()==1)
            tdate="0"+tdate;

        String fdatetime= fyear+"-"+fmonth+"-"+fdate+" "+startTime;
        String tdatetime= tyear+"-"+tmonth+"-"+tdate+" "+endTime;
        boolean isInvalid=false;
        try
        {
            Date date1 = format.parse(fdatetime);
            Date date2 = format.parse(tdatetime);
            Timestamp sDate = new Timestamp(date1.getTime());
            Timestamp eDate = new Timestamp(date2.getTime());
            if(sDate.after(eDate))
            {
                isInvalid=true;
            }
        }
        catch (ParseException e)
        {
           log.error("Catch ParseException...",e);
        }
        if(isInvalid)
        {
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>From date should not be greater then To date " + EOL + "</b></font></center>";
            request.setAttribute("errormessage",errormsg);
            rd.forward(request, response);
            return;
        }
        hashtable.put("fromDate", fdatetime);
        hashtable.put("toDate",tdatetime);
        String status=exportTransactionExcel.exportTransactionExcelCron(hashtable);

        if("success".equalsIgnoreCase(status))
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg+"SFTP Transaction export successfully." + EOL + "</b></font></center>";
        else
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg+"Export SFTP Transaction failed." + EOL + "</b></font></center>";
        request.setAttribute("errormessage",errormsg);
        rd.forward(request, response);
    }
}
