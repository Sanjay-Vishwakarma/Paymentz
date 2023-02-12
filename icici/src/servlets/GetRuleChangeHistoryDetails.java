import com.directi.pg.Logger;
import com.manager.MerchantMonitoringManager;
import com.manager.vo.merchantmonitoring.MonitoringRuleLogVO;
import org.owasp.esapi.User;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by Sandip on 7/6/2016.
 */
public class GetRuleChangeHistoryDetails extends HttpServlet
{
    static Logger logger = new Logger(GetRuleChangeHistoryDetails.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        String historyId=req.getParameter("historyid");
        String ruleId=req.getParameter("ruleid");
        String terminalId=req.getParameter("terminalid");
        MerchantMonitoringManager merchantMonitoringManager=new MerchantMonitoringManager();
        try
        {
            MonitoringRuleLogVO monitoringRuleLogVO=merchantMonitoringManager.getRuleChangeHistoryDetails(historyId);
            MonitoringRuleLogVO monitoringRuleLogVO2=merchantMonitoringManager.getRulePreviousChangeHistoryDetails(ruleId,historyId,terminalId);
            if(monitoringRuleLogVO2.getMonitoringParameterMappingVO()==null)
            {
               monitoringRuleLogVO2=merchantMonitoringManager.getPreviousRuleChangeHistoryDetails(ruleId);
            }

            req.setAttribute("monitoringRuleLogVO",monitoringRuleLogVO);
            req.setAttribute("monitoringRuleLogVO1",monitoringRuleLogVO2);

        }
        catch (Exception e)
        {
            logger.debug("Exception::::::::"+e);
        }
        req.getRequestDispatcher("/riskRuleChangeHistory.jsp?ctoken="+user.getCSRFToken()).forward(req,res);
    }

}
