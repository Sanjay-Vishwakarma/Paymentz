
import com.logicboxes.util.ApplicationProperties;
import com.logicboxes.util.Util;
import com.logicboxes.cron.CronManager;


import javax.servlet.http.HttpServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;

/**
 * Created by IntelliJ IDEA.
 * User: amit.j
 * Date: Oct 6, 2006
 * Time: 6:28:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestmotoInitServlet extends HttpServlet {
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext application = getServletContext();
        try
        {
            ApplicationProperties.initilizeProperties();
            application.log("Before loading CronManager");
            CronManager.getInstance();
            application.log("After loading CronManager");
        }
        catch (Throwable e)
        {
            application.log("Error in loading CronManager " + Util.getStackTrace(e));
        }
    }
}
