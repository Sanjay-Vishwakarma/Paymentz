import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.SystemError;
import com.directi.pg.fileupload.FileUploadBean;
import com.directi.pg.newProcessFIRC;
import org.owasp.esapi.User;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import com.directi.pg.Logger;
/**
 * Created by IntelliJ IDEA.
 * User: jignesh.r
 * Date: Jul 11, 2006
 * Time: 2:58:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProcessFIRCServlet extends HttpServlet
{     private static Logger log=new Logger(ProcessFIRCServlet.class.getName());
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {


        HttpSession session = request.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");




        if (!Admin.isLoggedIn(session))
        {
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        ServletContext application = getServletContext();
        String pathToFirc = (String) application.getAttribute("PATHTOFIRC");
        FileUploadBean fircFileBean = new FileUploadBean();
        fircFileBean.setSavePath(pathToFirc);
        try
        {
            fircFileBean.doUpload(request, null);
        }
        catch (SystemError systemError)
        {
            log.error("System Error while uploading file",systemError);

        }
        String uploadedFileName = fircFileBean.getFilename();
        String merchantId = fircFileBean.getFieldValue("merchantId");
        String totalAmount = fircFileBean.getFieldValue("totalAmount");
        String defaultMerchantId = (String) application.getAttribute("MERCHANTID");
        out.println(newProcessFIRC.processFIRCFiles(pathToFirc, uploadedFileName, totalAmount, merchantId, defaultMerchantId));
    }
}
