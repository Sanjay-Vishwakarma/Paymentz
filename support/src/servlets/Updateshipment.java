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

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 5/10/14
 * Time: 6:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class Updateshipment extends HttpServlet
{
     static Logger log=new Logger("logger1");
    static final String classname= Updateshipment.class.getName();
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
        String blank="";
        String redirectPage ="";
        String success="";
        RequestDispatcher rd;
        boolean flag=true;
        if(request.getParameterValues("checkbox")==null)
        {   log.debug(classname+" NO CHECK BOX CHECKED:: ");
            request.setAttribute("check","no");
            log.debug(classname+" forwarding to shipment page with error for user to check in the corresponding tracking id");
             rd=request.getRequestDispatcher("/servlet/ShipmentDetails?ctoken="+user.getCSRFToken());
            rd.forward(request,response);}
        else{

            String value[]  =request.getParameterValues("checkbox");

            for(int i=0;i<value.length;i++)
        {   String pod=null;
            String podbatch=null;
            String status=null;
            log.debug(classname+" CHECKED IN VALUE::"+value[i]);
            Integer trackingid = Integer.valueOf(value[i]);


            try
            {
                pod= ESAPI.validator().getValidInput("pod", request.getParameter("pod"+trackingid), "SafeString", 13, false);
                podbatch= ESAPI.validator().getValidInput("podbatch", request.getParameter("podbatch"+trackingid), "SafeString", 18, false);
            }
            catch (ValidationException e)
            {
                log.debug(classname+" pod and podbatch Validation Exception::"+pod+" "+podbatch);  //To change body of catch statement use File | Settings | File Templates.
                if(pod==null)
                {
                 errormsg=errormsg+" POD for"+trackingid+""+EOL;
                }
                if(podbatch==null)
                {
                    errormsg=errormsg+" PODBATCH for"+trackingid+""+EOL;
                }
             redirectPage="/support/servlet/ShipmentDetails?ctoken="+user.getCSRFToken();
             }
            try
            {
            status= ESAPI.validator().getValidInput("shippingstatus", request.getParameter("shipstatus" + trackingid), "ShipStatus", 20, true);
            }catch(ValidationException e)
            {
                log.debug(classname+" Status Validation Exception");
                errormsg=errormsg+" status for"+trackingid+""+EOL;
               redirectPage="/servlet/ShipmentDetails?ctoken="+user.getCSRFToken();
            }

            if(pod==null || podbatch==null)
            {
                log.debug(classname+" not entered pod "+pod+"or podbath::"+podbatch+" to corresponding tracking id::"+trackingid);
                blank=blank+" pod Or pod batch is blank for the trackingId"+trackingid+""+EOL;
                redirectPage="/servlet/ShipmentDetails?ctoken="+user.getCSRFToken();
            }
            else{
                try {
                    log.debug(classname+"   entered pod "+pod+"or podbath::"+podbatch+" to corresponding tracking id::"+trackingid);
                    count+=csc.updateShipment(trackingid,pod,podbatch,status);
                    success=success+"success updation for tracking::"+trackingid+""+EOL;
                    redirectPage="/servlet/ShipmentDetails?ctoken="+user.getCSRFToken();
                } catch (SQLException e) {
                    log.error(classname+" SQL CONNECTION EXCEPTION",e);  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
            log.debug(classname+" update"+count);
            request.setAttribute("success",success);
            request.setAttribute("error",errormsg);
            request.setAttribute("blank",blank);
            log.debug(classname + " forwarding to shipmentDetails.java");
            rd =request.getRequestDispatcher(redirectPage);
            rd.forward(request,response);
        }}


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
