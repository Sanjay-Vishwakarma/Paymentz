import com.directi.pg.*;
import com.manager.WLPartnerInvoiceManager;
import com.payment.exceptionHandler.PZDBViolationException;
import org.owasp.esapi.User;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Calendar;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: Supriya
 * Date: 12/05/16
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class WLPartnerInvoiceList extends HttpServlet
{
    private static Logger log = new Logger(WLPartnerInvoiceList.class.getName());

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        RequestDispatcher rd = req.getRequestDispatcher("/wlPartnerInvoiceList.jsp?ctoken=" + user.getCSRFToken());
        Hashtable hash = null;
        String partnerId = req.getParameter("partnerid");
        String pgtypeId = req.getParameter("pgtypeid");
        String isPaid = req.getParameter("ispaid");


        String fdate = req.getParameter("fdate");
        String tdate = req.getParameter("tdate");
        String fmonth = req.getParameter("fmonth");
        String tmonth = req.getParameter("tmonth");
        String fyear = req.getParameter("fyear");
        String tyear = req.getParameter("tyear");
        int records = 15;
        int pageno = 1;

        int start = 0; // start index
        int end = 0; // end index

        Calendar rightNow = Calendar.getInstance();

        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(Calendar.DATE);

        if (fmonth == null) fmonth = "" + (rightNow.get(Calendar.MONTH));
        if (tmonth == null) tmonth = "" + (rightNow.get(Calendar.MONTH));

        if (fyear == null) fyear = "" + rightNow.get(Calendar.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(Calendar.YEAR);

        pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);

        String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
        String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");

        start = (pageno - 1) * records;
        end = records;

        WLPartnerInvoiceManager wlPartnerInvoiceManager=new WLPartnerInvoiceManager();
        try
        {
            hash= wlPartnerInvoiceManager.getListOfWLPartnerInvoices(fdtstamp, tdtstamp, partnerId, isPaid, start, end);
            req.setAttribute("transdetails", hash);
        }
        catch (PZDBViolationException pze)
        {
            log.error("System error while perform select query",pze);
            Functions.ShowMessage("Error", "Internal System Error while getting list of WL Partner Invoices");
        }
        rd.forward(req, res);
        return;

    }
}

