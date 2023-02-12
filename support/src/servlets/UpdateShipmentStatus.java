import com.directi.pg.CustomerSupport;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
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
import java.io.PrintWriter;
import java.sql.SQLException;


public class UpdateShipmentStatus extends HttpServlet
{
    static Logger log=new Logger("logger1");
    static final String classname= UpdateShipmentStatus.class.getName();
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out=response.getWriter();
        HttpSession session= Functions.getNewSession(request);
        if(!CustomerSupport.isLoggedIn(session))
        {
            response.sendRedirect("/support/logout.jsp");
            return;
        }
        User user= (User) session.getAttribute("ESAPIUserSessionKey");
        int count=0;
        CustomerSupport csc=new CustomerSupport();
        String EOL = "<BR>";
        String errormsg="";
        String success="";
        RequestDispatcher rd;

        if(request.getParameterValues("checkbox")==null)
        {   log.debug(classname+" NO CHECK BOX CHECKED:: ");
            request.setAttribute("check","no");
            log.debug(classname+" forwarding to shipment page with error for user to check in the corresponding tracking id");
            rd=request.getRequestDispatcher("/servlet/ShipmentStatusDetails?ctoken="+user.getCSRFToken());
            rd.forward(request,response);}
        else{
            String value[]  =request.getParameterValues("checkbox");
            for(int i=0;i<value.length;i++)
            {
                String pod=null;
                String podbatch=null;
                String status =null;
                log.debug(classname+" CHECKED IN VALUE::"+value[i]);
                Integer trackingid = Integer.valueOf(value[i]);
                log.debug(classname+" shipment status::"+request.getParameter("shipstatus"+trackingid));
                try
                {
                    status= ESAPI.validator().getValidInput("shippingstatus", request.getParameter("shipstatus"+trackingid), "ShipStatus", 20, false);
                }catch(ValidationException e)
                {
                    log.debug(classname+" Status Validation Exception");
                    errormsg=errormsg+" status for"+trackingid+""+EOL;
                }
                log.debug(classname+" CHANGED STATUS IS::"+status);

                try { log.debug(classname+"   entered status::"+status+" to corresponding tracking id::"+trackingid);
                    count+=csc.updateShipment(trackingid,pod,podbatch,status);
                    success=success+"success updation for tracking::"+trackingid+""+EOL;
                } catch (SQLException e) {
                    log.error(classname+" SQL CONNECTION EXCEPTION::",e);  //To change body of catch statement use File | Settings | File Templates.
                }

            }
            log.debug(classname+" updated SHIPMENT DETAILS::"+count);


            log.debug(classname+" forwarding to ShipmentStatusDetails.java");
            request.setAttribute("error",errormsg);
            request.setAttribute("success",success);
            rd=request.getRequestDispatcher("/servlet/ShipmentStatusDetails?ctoken="+user.getCSRFToken());
            rd.forward(request,response);}
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
