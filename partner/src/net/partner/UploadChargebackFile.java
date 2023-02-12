package net.partner;

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
 * Created by NAMRATA BARI on 16-09-2019.
 */

public class UploadChargebackFile extends HttpServlet
{
    private static Logger log = new Logger(UploadChargebackFile.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        PartnerFunctions partner = new PartnerFunctions();
        FileUploadBean fub = new FileUploadBean();
        Functions functions = new Functions();
        CommonChargeback commonChargeback = new CommonChargeback();

        RequestDispatcher rd = req.getRequestDispatcher("/commonChargeBackFile.jsp?ctoken=" + user.getCSRFToken());
        String filePath = ApplicationProperties.getProperty("MPR_FILE_STORE");
        String logPath = ApplicationProperties.getProperty("LOG_STORE");

        String userName = null;

        fub.setSavePath(filePath);
        fub.setLogpath(logPath);
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        String role = "";
        for (String s:user.getRoles())
        {
            role = role.concat(s);
        }
        auditTrailVO.setActionExecutorName(role+"-"+user.getAccountName());
        auditTrailVO.setActionExecutorId((String)session.getAttribute("merchantid"));

        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }

        try
        {
            fub.doUpload(req, userName);
        }
        catch (SystemError sys)
        {
            req.setAttribute("msg", "ERROR");
            req.setAttribute("Result", sys.getMessage());
            rd.forward(req, res);
            return;
        }

        String name = fub.getFilename();
        String gateway = fub.getFieldValue("pgtypeid");
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
                        req.setAttribute("msg", "Result");
                        req.setAttribute("Result", "<table class=\"tablestyle  table-bordered\">" +val.toString()+ "</table>");
                                rd.forward(req, res);
                        return;
                    }
                }
                catch (Exception e)
                {
                    req.setAttribute("msg", "ERROR");
                    req.setAttribute("Result", e.getMessage());
                    rd.forward(req, res);
                    return;
                }
            }
        }
        else
        {
            req.setAttribute("msg", "ERROR");
            req.setAttribute("Result", "Kindly select Bank Name.");
            rd.forward(req, res);
            return;
        }

        rd.forward(req, res);
        return;
    }
}
