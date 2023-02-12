import com.directi.pg.Admin;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.logicboxes.util.ApplicationProperties;
import com.manager.WLPartnerInvoiceManager;
import com.manager.dao.WLPartnerInvoiceDAO;
import com.manager.vo.WLPartnerInvoiceVO;
import org.owasp.esapi.User;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: Supriya
 * Date: 12/05/16
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActionWLInvoiceManager extends HttpServlet
{
    private final static String WL_PARTNER_INVOICE_FILE_PATH = ApplicationProperties.getProperty("WL_PARTNER_INVOICE_FILE_PATH");
    private static Logger log = new Logger(ActionWLInvoiceManager.class.getName());

    public static boolean sendFile(String filepath, String filename, File f, HttpServletResponse response) throws Exception
    {
        int length = 0;
        // Set browser download related headers
        response.setContentType("application/octat-stream");
        response.setContentLength((int) f.length());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        javax.servlet.ServletOutputStream op = response.getOutputStream();

        byte[] bbuf = new byte[1024];
        DataInputStream in = new DataInputStream(new FileInputStream(f));

        while ((in != null) && ((length = in.read(bbuf)) != -1))
        {
            op.write(bbuf, 0, length);
        }

        in.close();
        op.flush();
        op.close();
        return true;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
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

        String errormsg = "";
        String EOL = "<BR>";

        String action = req.getParameter("action");
        String invoiceId = req.getParameter("invoiceid");

        try
        {
            if (action.equals("") || invoiceId.equals(""))
            {
                errormsg += "Action has not provided Or Mapping id is missing";
                req.setAttribute("message", errormsg);
                RequestDispatcher rd = req.getRequestDispatcher("/actionWLInvoiceManager.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            else
            {
                WLPartnerInvoiceVO wlPartnerInvoiceVO = null;
                WLPartnerInvoiceManager wlPartnerInvoiceManager=new WLPartnerInvoiceManager();
                if (action.equalsIgnoreCase("view"))
                {
                    wlPartnerInvoiceVO = wlPartnerInvoiceManager.getWLInvoiceReport(invoiceId);
                    req.setAttribute("action", action);
                    req.setAttribute("wlPartnerInvoiceVO", wlPartnerInvoiceVO);
                    RequestDispatcher rd = req.getRequestDispatcher("/actionWLInvoiceManager.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                }
                else if (action.equalsIgnoreCase("update"))
                {
                    wlPartnerInvoiceVO = wlPartnerInvoiceManager.getWLInvoiceReport(invoiceId);
                    req.setAttribute("action", action);
                    req.setAttribute("wlPartnerInvoiceVO", wlPartnerInvoiceVO);
                    RequestDispatcher rd = req.getRequestDispatcher("/actionWLInvoiceManager.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                }
                else if(action.equalsIgnoreCase("delete")){
                    boolean isDeleted = WLPartnerInvoiceDAO.deleteWLInvoiceReport(invoiceId);
                    if(isDeleted)
                        errormsg += "<center><font class=\"text\" face=\"arial\"><b> White Label Partner Invoice Record Deleted." + EOL + "</b></font></center>";
                    else
                        errormsg += "<center><font class=\"text\" face=\"arial\"><b> Failed To Delete White Label Partner Invoice Record." + EOL + "</b></font></center>";
                    req.setAttribute("message",errormsg);
                    req.setAttribute("action",action);
                    RequestDispatcher rd = req.getRequestDispatcher("/wlPartnerInvoiceList.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req,res);
                }
                else if (action.equalsIgnoreCase("sendPdfFile"))
                {
                    wlPartnerInvoiceVO = wlPartnerInvoiceManager.getWLInvoiceReport(invoiceId);
                    File f = new File(WL_PARTNER_INVOICE_FILE_PATH + wlPartnerInvoiceVO.getReportFilePath());
                    String filename = f.getName();
                    if (filename == null || filename.isEmpty())
                    {
                        errormsg += "<center><font class=\"text\" face=\"arial\"><b>File Is Not Available." + EOL + "</b></font></center>";
                        req.setAttribute("message", errormsg);
                        RequestDispatcher rd = req.getRequestDispatcher("/wlPartnerInvoiceList.jsp?ctoken=" + user.getCSRFToken());
                        rd.forward(req, res);
                    }
                    sendFile(wlPartnerInvoiceVO.getReportFilePath(), filename, f, res);
                }
            }
        }
        catch (SystemError systemError)
        {
            log.error("SystemError:::::",systemError);
            errormsg += "Internal error while processing you request";
            req.setAttribute("message", errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/actionWLInvoiceManager.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (SQLException se)
        {
            log.error("SQLException:::::",se);
            errormsg += "Internal error while processing you request";
            req.setAttribute("message", errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/actionWLInvoiceManager.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (Exception e)
        {
            log.error("GenericException:::::",e);
            errormsg += "Internal error while processing you request";
            req.setAttribute("message", errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/actionWLInvoiceManager.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
        }
    }

}
