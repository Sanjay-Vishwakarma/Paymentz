import com.directi.pg.Logger;
import com.manager.MerchantMonitoringManager;
import com.manager.vo.merchantmonitoring.MonitoringParameterVO;
import org.owasp.esapi.User;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by user on 7/6/2016.
 */
public class MonitoringRiskRuleDetails extends HttpServlet
{
    static Logger logger = new Logger(MonitoringRiskRuleDetails.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        String ruleId=req.getParameter("ruleid");
        MerchantMonitoringManager merchantMonitoringManager=new MerchantMonitoringManager();
        try
        {
            MonitoringParameterVO monitoringParameterVO=merchantMonitoringManager.getMonitoringParameterDetails(ruleId);
            req.setAttribute("monitoringParameterVO",monitoringParameterVO);

        }
        catch (Exception e)
        {
            logger.error("Catch Exception...",e);
        }
        req.getRequestDispatcher("/riskruledetails.jsp?ctoken="+user.getCSRFToken()).forward(req,res);
    }
}
