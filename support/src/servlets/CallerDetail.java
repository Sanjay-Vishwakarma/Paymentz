import com.directi.pg.CustomerSupport;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.payment.validators.InputFields;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

public class CallerDetail extends HttpServlet
{      static Logger logger=new Logger("logger1");
    static String classname= CallerDetail.class.getName();
    protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        HttpSession session= Functions.getNewSession(request);
        User user=(User)session.getAttribute("ESAPIUserSessionKey");
        if(!CustomerSupport.isLoggedIn(session))
        {
            response.sendRedirect("/support/logout.jsp");
        }
        int records=15;
        int pageno=1;
        try
        {
            pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("pageno", request.getParameter("SPageno"), "Numbers", 5, true), 1);
            records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("records", request.getParameter("SRecords"), "Numbers", 5, true), 15);
        }
        catch(ValidationException e)
        {
            logger.error(classname+" Invalid page no or records",e);
            pageno = 1;
            records = 15;

        }
        Hashtable error,callList;
        List<InputFields> inputFieldsListOptional= new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.STRACKINGID);
        inputFieldsListOptional.add(InputFields.TOID);
        inputFieldsListOptional.add(InputFields.FROMDATE);
        inputFieldsListOptional.add(InputFields.FROMMONTH);
        inputFieldsListOptional.add(InputFields.FROMYEAR);
        inputFieldsListOptional.add(InputFields.TODATE);
        inputFieldsListOptional.add(InputFields.TOMONTH);
        inputFieldsListOptional.add(InputFields.TOYEAR);
        error= CustomerSupport.validateOptionalParameters(request, inputFieldsListOptional);

        try
        {
        if(error.isEmpty())
        {
            logger.debug(classname+" trackingid::"+request.getParameter("STrakingid")+" toid::"+request.getParameter("toid")+" fdate::"+request.getParameter("fdate")+"");
            String trackingid=request.getParameter("STrakingid");
            String toid=request.getParameter("toid");
            String fdate=request.getParameter("fdate");
            String fmonth=request.getParameter("fmonth");
            String fyear=request.getParameter("fyear");
            String tdate=request.getParameter("tdate");
            String tmonth=request.getParameter("tmonth");
            String tyear=request.getParameter("tyear");

            Calendar rightNow = Calendar.getInstance();

            if (fdate == null) fdate = "" + 1;
            if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

            if (fmonth == null) fmonth = "" + (rightNow.get(rightNow.MONTH));
            if (tmonth == null) tmonth = "" + (rightNow.get(rightNow.MONTH));

            if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
            if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);

            if(toid.equals(""))
            {
                toid=null;
            }

            if(trackingid.equals(""))
            {
                trackingid=null;
            }

            String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0")+"000";
            String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59")+"000";

            logger.debug(classname+" fdtstamp::"+fdtstamp+" tdtstamp::"+tdtstamp);
            try
            {
                callList= CustomerSupport.callerList(trackingid, toid, fdtstamp, tdtstamp, pageno, records);
                request.setAttribute("callList",callList);
                RequestDispatcher rd=request.getRequestDispatcher("/callerDetails.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request,response);
            }
            catch (SystemError systemError)
            {
                logger.error(classname+" System error::",systemError);  //To change body of catch statement use File | Settings | File Templates.
            }


        }
        else
        {
            request.setAttribute("error",error);
            RequestDispatcher rd=request.getRequestDispatcher("/callerDetails.jsp?MES=X&ctoken="+user.getCSRFToken());
            rd.forward(request,response);
        }
    }catch(Exception e)
        {
            logger.error(classname+" Main class name exception::",e);
        }

    }
    protected void doGet(HttpServletRequest request,HttpServletResponse response)   throws ServletException,IOException
    {
        doPost(request,response);
    }
}
