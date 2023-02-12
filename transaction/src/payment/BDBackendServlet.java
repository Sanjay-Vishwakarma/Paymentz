package payment;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Admin on 8/9/2017.
 */
public class BDBackendServlet extends PzServlet
{
    private static Logger log = new Logger(BDBackendServlet.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(BDBackendServlet.class.getName());
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        //System.out.println("-------Enter in doService of BDBackendServlet---ip address----" + req.getHeader("X-Forwarded-For"));
        log.error("-------Enter in doService of BDBackendServlet---ip address----" + req.getHeader("X-Forwarded-For"));
        transactionLogger.debug("-------Enter in doService of BDBackendServlet-------" + req.getHeader("X-Forwarded-For"));
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        HttpSession session = req.getSession(true);

        CommResponseVO commResponseVO = new CommResponseVO();
        CommRequestVO commRequestVO = new CommRequestVO();
        for (Object key : req.getParameterMap().keySet())
        {
            log.error("----for loop BDBackendServlet-----" + key + "=" + req.getParameter((String) key) + "--------------");
            transactionLogger.error("----for loop BDBackendServlet-----" + key + "=" + req.getParameter((String) key) + "--------------");
        }
    }
}
