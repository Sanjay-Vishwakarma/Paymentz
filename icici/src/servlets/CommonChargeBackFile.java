package servlets;

import com.directi.pg.*;
import com.directi.pg.fileupload.FileUploadBean;
import com.logicboxes.util.ApplicationProperties;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by Admin on 12/16/2019.
 */
public class CommonChargeBackFile extends HttpServlet
{
    private static Logger logger = new Logger(CommonChargeBackFile.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        logger.debug("Entering in CommonChargeBackFile");
        HttpSession session = req.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        String filePath = ApplicationProperties.getProperty("MPR_FILE_STORE");
        String logPath = ApplicationProperties.getProperty("LOG_STORE");
        String userName = null;

        FileUploadBean fub = new FileUploadBean();
        Functions functions = new Functions();
        CommonChargeback commonChargeback = new CommonChargeback();
        RequestDispatcher rd = req.getRequestDispatcher("/commonChargebackUplaod.jsp?ctoken="+user.getCSRFToken());

        fub.setSavePath(filePath);
        fub.setLogpath(logPath);

        AuditTrailVO auditTrailVO = new AuditTrailVO();
        auditTrailVO.setActionExecutorName("Admin" + "-" + session.getAttribute("username").toString());
        auditTrailVO.setActionExecutorId("1");

        try
        {
            fub.doUpload(req, userName);
        }
        catch (SystemError sys)
        {
            req.setAttribute("ERROR", Functions.NewShowConfirmation("ERROR", sys.getMessage()));
            rd.forward(req, res);
            return;
        }

        String name = fub.getFilename();
        String gateway = fub.getFieldValue("gateway");
        if (functions.isValueNull(gateway))
        {
            if (ApplicationProperties.getProperty("MPR_FILE_STORE") + name != null)
            {
                try
                {
                    String fullFileLocation = ApplicationProperties.getProperty("MPR_FILE_STORE") + name;
                    StringBuffer val = commonChargeback.processChargeback(fullFileLocation, gateway, auditTrailVO);
                    if (val != null)
                    {
                        String str = "";
         /* str = str + "<table align=\"center\" width=\"90%\" cellpadding=\"2\" cellspacing=\"2\" ><tr><td>";*/

                        str = str + "<table bgcolor=\"#ecf0f1\" width=\"100%\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\">";
                        str = str + "<table border=\"1\" cellpadding=\"5\" cellspacing=\"0\" align=\"center\" class=\"table table-striped table-bordered table-hover table-green dataTable\">";

          /*str = str + "<tr height=30>";
          str = str + "<td colspan=\"3\" bgcolor=\"#34495e\"  class=\"texthead\" align=\"center\"><font color=\"#FFFFFF\" size=\"2\" face=\"Open Sans,Helvetica Neue,Helvetica,Arial,Palatino Linotype', 'Book Antiqua, Palatino, serif\">  Result</font></td>";
          str = str + "</tr>";

          str = str + "<tr><td>&nbsp;</td></tr>";*/

                        str = str + /*"<tr><td align=\"center\" class=\"textb\">" +*/ val.toString() /*+ "</td></tr>"*/;
                        str = str + "</table></table>";// </tr></td> </table>
                        req.setAttribute("result",Functions.NewShowConfirmation("RESULT",  str));
                        rd.forward(req, res);
                        return;
                    }
                }
                catch (Exception e)
                {
                    req.setAttribute("ERROR", Functions.NewShowConfirmation("ERROR", e.getMessage()));
                    rd.forward(req, res);
                    return;
                }
            }
        }
        else
        {
            req.setAttribute("ERROR", Functions.NewShowConfirmation("ERROR", "Kindly select account Id."));
            rd.forward(req, res);
            return;
        }
    }
}
