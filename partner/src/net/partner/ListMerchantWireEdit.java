package net.partner;

import com.directi.pg.*;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: sanjeet
 * Date: 8/3/15
 * Time: 7:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class ListMerchantWireEdit extends HttpServlet
{
    Logger logger=new Logger(ListMerchantWireEdit.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        doProcess(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        doProcess(request,response);
    }
    public void doProcess(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        HttpSession session = request.getSession();
        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session)){
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }


        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        RequestDispatcher rd=request.getRequestDispatcher("/listMerchantWireEdit.jsp?ctoken="+user.getCSRFToken());
        Date date= null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal= Calendar.getInstance();
        String memberId=request.getParameter("memberid");
        String terminalId=request.getParameter("terminalid");
        String action=request.getParameter("action");
        String status=request.getParameter("status");
        String firstdate=request.getParameter("firstdate");
        String lastdate=request.getParameter("lastdate");
        String settledId=request.getParameter("settledid");
        String fdtstamp = "";
        String tdtstamp = "";
        HashMap hash = null;
        Functions functions=new Functions();
        StringBuffer sb=new StringBuffer();
        Connection con=null;
        HashMap memberHash = null;
        PartnerFunctions partnerFunctions = new PartnerFunctions();

        try
        {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date firstDate=  format.parse(firstdate);
            Date lastDate=format.parse(lastdate);
            SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");


            firstdate = sd.format(firstDate);
            lastdate = sd.format(lastDate);
            //from date
            date = sdf.parse(firstdate);
            cal.setTime(date);
            String fdate = String.valueOf(cal.get(Calendar.DATE));
            String fmonth = String.valueOf(cal.get(Calendar.MONTH));
            String fyear = String.valueOf(cal.get(Calendar.YEAR));

            //to Date
            date = sdf.parse(lastdate);
            cal.setTime(date);
            String tdate = String.valueOf(cal.get(Calendar.DATE));
            String tmonth = String.valueOf(cal.get(Calendar.MONTH));
            String tyear = String.valueOf(cal.get(Calendar.YEAR));

            logger.debug("From date dd::" + fdate + " MM;;" + fmonth + " YY::" + fyear + " To date dd::" + tdate + " MM::" + tmonth + " YY::" + tyear);
            //conversion to dtstamp
            fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
            tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");
        }
        catch (Exception e)
        {
            logger.debug("Exception:::"+e);
        }

        if(!ESAPI.validator().isValidInput("memberid", memberId, "Numbers", 20,true))
        {
            sb.append("Invalid Member Id,");
        }
        if(!ESAPI.validator().isValidInput("terminalid", terminalId, "Numbers", 5,true))
        {
            sb.append("Invalid Terminal Id,");
        }

        if(sb.length()>0)
        {
            request.setAttribute("statusMsg",sb.toString());
            rd.forward(request,response);
            return;
        }

       if ("modify".equals(action))
       {
           try
           {
               memberHash = partnerFunctions.getMerchantWireReports(memberId, terminalId, fdtstamp, tdtstamp,settledId);
               request.setAttribute("memberDetail", memberHash);
           }
           catch (Exception e)
           {
               logger.error("Exception in isMemberUser method: ", e);
           }
       }

        rd.forward(request,response);
        return;
    }
}

