import com.directi.pg.*;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;

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
import java.util.Hashtable;

import java.util.*;

public class CSEviewupdate extends HttpServlet
{
    static Logger logger=new Logger("logger1");
    protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {

        try
        {
            HttpSession session= Functions.getNewSession(request);
            User user=(User)session.getAttribute("ESAPIUserSessionKey");

            if(!com.directi.pg.Admin.isLoggedIn(session))
            {
                response.sendRedirect("/icici/logout.jsp");
            }
            String csId=request.getParameter("csId");
            Hashtable detail = null;
            String buttonvalue=request.getParameter("submit");
            int records=10;
            int pageno=1;
            try
            {
                pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("pageno", request.getParameter("SPageno"), "Numbers", 5, true), 1);
                records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("records", request.getParameter("SRecords"), "Numbers", 5, true), 10);
            }
            catch(ValidationException e)
            {
                logger.error("Invalid page no or records",e);
                pageno = 1;
                records = 10;

            }


            try
            {
                detail= CustomerSupport.CSEinfo(csId, pageno, records);
                int total=Integer.parseInt(detail.get("totalrecords").toString());
                request.setAttribute("CSEinfo",detail);

            }
            catch (SystemError systemError)
            {
                logger.error(" SYSTEM ERROR while getting CSEinfo:",systemError);

            }
            if("View".equals(buttonvalue))
            {
                request.setAttribute("readonly",true);
            }
            else if("Edit".equals(buttonvalue))
            {
                request.setAttribute("readonly",false);
            }
            else{
                request.setAttribute("readonly",true);
            }
            logger.debug(" full detail::"+detail);
            if(request.getParameter("MES")==null)
            {
                Hashtable error=(Hashtable)request.getAttribute("error");
                request.setAttribute("error",error);
                RequestDispatcher rd1=request.getRequestDispatcher("/cseViewUpdate.jsp?MES=X&ctoken="+user.getCSRFToken());
                rd1.forward(request,response);
            }
            else{
            RequestDispatcher rd=request.getRequestDispatcher("/cseViewUpdate.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request,response);
            }

        }catch (Exception e)
        {
            logger.error(" main class Exception::",e);
        }
    }

    protected void doGet(HttpServletRequest request,HttpServletResponse response)  throws ServletException,IOException
    {
        doPost(request,response);
    }
}

