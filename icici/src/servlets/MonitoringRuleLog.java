import com.directi.pg.Admin;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.MerchantMonitoringManager;
import com.manager.vo.merchantmonitoring.MonitoringRuleLogVO;
import com.payment.exceptionHandler.PZDBViolationException;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Created by Vishal on 7/29/2016.
 */
public class MonitoringRuleLog extends HttpServlet
{
    private static Logger logger = new Logger(MonitoringRuleLog.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPOst(request,response);
    }
    public void doPOst(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        String memberId = request.getParameter("memberid");
        String terminalId = request.getParameter("terminalid");
        MerchantMonitoringManager merchantMonitoringManager = new MerchantMonitoringManager();
        try
        {
            List<MonitoringRuleLogVO> monitoringRuleLogVOs = merchantMonitoringManager.getMonitoringRuleLogDetails(memberId, terminalId);

            request.setAttribute("monitoringRuleLogVOs",monitoringRuleLogVOs);
            RequestDispatcher rd = request.getRequestDispatcher("/monitoringRuleLog.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        catch (PZDBViolationException e)
        {

        }
    }
}
