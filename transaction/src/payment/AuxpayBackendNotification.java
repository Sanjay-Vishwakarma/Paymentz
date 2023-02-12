package payment;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by 123 on 11/7/2015.
 */
public class AuxpayBackendNotification extends PzServlet
{
    private static Logger log = new Logger(AuxpayBackendNotification.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(AuxpayBackendNotification.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);
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
        BufferedReader in = new BufferedReader(new InputStreamReader(req.getInputStream()));
        String strResponse="";
        String decodedString;
        while ((decodedString = in.readLine()) != null)
        {
            strResponse = strResponse + decodedString;
        }
        log.debug("-------Enter in doService of AuxpayBackendNotification---ip address----"+req.getHeader("X-Forwarded-For"));
        log.error("-------Enter in doService of AuxpayBackendNotification---ip address----"+req.getHeader("X-Forwarded-For"));
        transactionLogger.debug("-------Enter in doService of AuxpayBackendNotification-------"+req.getHeader("X-Forwarded-For"));
        transactionLogger.debug("-------AuxpayBackendNotification Result-------"+strResponse);
        //res.setContentType("text/html");
        //PrintWriter out = res.getWriter();
        //HttpSession session = req.getSession(true);

        //CommResponseVO commResponseVO = new CommResponseVO();
        //CommRequestVO commRequestVO = new CommRequestVO();
        /*for(Object key : req.getParameterMap().keySet())
        {
            log.debug("----for loop AuxpayBackendNotification-----"+key+"="+req.getParameter((String)key)+"--------------");
            transactionLogger.debug("----for loop AuxpayBackendNotification-----"+key+"="+req.getParameter((String)key)+"--------------");
        }*/
    }
}
