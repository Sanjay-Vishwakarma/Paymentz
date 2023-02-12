package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.manager.PayoutManager;
import com.payment.exceptionHandler.PZDBViolationException;
import org.apache.commons.lang.StringUtils;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Admin on 2/12/2020.
 */
public class GeneratePartnerWireReport extends HttpServlet
{
    private static Logger log=new Logger(GeneratePartnerWireReport.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        PartnerFunctions partnerFunctions=new PartnerFunctions();
        if (!partnerFunctions.isLoggedInPartner(session)) {
            log.debug("Partner is logout");
            response.sendRedirect("/partner/logout.jsp");
            return;
        }

        String wireId = request.getParameter("wireId");
        RequestDispatcher rd = request.getRequestDispatcher("/partnercommissioncron.jsp?&ctoken=" + user.getCSRFToken());
        ResourceBundle rb1 = null;
        String language_property1 = (String) session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String GeneratePartnerWireReport_please_errormsg = StringUtils.isNotEmpty(rb1.getString("GeneratePartnerWireReport_please_errormsg")) ? rb1.getString("GeneratePartnerWireReport_please_errormsg") : "Please select BankWire Id";

        Functions functions = new Functions();
        PayoutManager payoutManager=new PayoutManager();

        if (!functions.isValueNull(wireId)) {
            request.setAttribute("statusMsg", GeneratePartnerWireReport_please_errormsg);
            rd.forward(request, response);
            return;
        }

        String statusMsg = "";
        StringBuffer sbError = new StringBuffer();
        try {
            log.error("voucherWireId::::"+wireId);
            List<String> stringList=payoutManager.partnerCommissionCronPerWireId(wireId);
            request.setAttribute("result", stringList);
        }
        catch (PZDBViolationException e) {
            log.error("PZDBViolationException:::::", e);
            statusMsg = "Internal error while processing your request";
        } catch (Exception e) {
           
            log.error("Exception:::::", e);
            statusMsg = "Internal error while processing your request";
        }
        request.setAttribute("statusMsg", statusMsg);
        rd.forward(request, response);
        return;
    }
}