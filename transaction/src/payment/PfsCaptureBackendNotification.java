package payment;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 3/5/15
 * Time: 2:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class PfsCaptureBackendNotification extends PzServlet
{
    public PfsCaptureBackendNotification()
    {
        super();
    }
    private static Logger log = new Logger(PfsCaptureBackendNotification.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PfsCaptureBackendNotification.class.getName());
    //Connection con = null;
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
        res.setContentType("text/html");
        log.debug("-------Inside PfsCaptureBackendNotification-----");
        log.debug("PfsCaptureBackendNotification FROM IP="+req.getRemoteAddr());
        log.debug("PfsCaptureBackendNotification forwarded-for---"+req.getHeader("X-Forwarded-For"));
        log.debug("PfsCaptureBackendNotification forwarded-host---"+req.getHeader("X-Forwarded-Host"));


    }
}
