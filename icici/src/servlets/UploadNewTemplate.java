package servlets;

import com.directi.pg.*;
import com.manager.AppFileManager;
import com.manager.ApplicationManager;
import com.manager.vo.PaginationVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.vo.applicationManagerVOs.AppFileDetailsVO;
import com.vo.applicationManagerVOs.BankTypeVO;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Vishal on 6/13/2017.
 */
public class UploadNewTemplate extends HttpServlet
{
    public static Logger logger = new Logger(UploadNewTemplate.class.getName());
    ResourceBundle applicationResourceBundle = LoadProperties.getProperty("com.directi.pg.documentServer");
    public String GATEWAY_TEMPLATE_PATH = applicationResourceBundle.getString("GATEWAY_TEMPLATE_PATH");
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        HttpSession session = request.getSession();
        logger.debug("Enter in Upload New Template");
        if (!Admin.isLoggedIn(session))
        {
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        RequestDispatcher rd = request.getRequestDispatcher("/bankMapping.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        response.setContentType("multipart/form-data");
        AppFileManager fileManager = new AppFileManager();
        Functions functions = new Functions();
        ApplicationManager applicationManager = new ApplicationManager();

        try
        {
            logger.debug("action===="+request.getParameter("action"));
            if (functions.isValueNull(request.getParameter("action")) && request.getParameter("action").contains("upload"))
            {
                Map<String, AppFileDetailsVO> fileDetailsVOMap = fileManager.uploadMultipleBankTemplate(request);
                List<BankTypeVO> bankTypeVOList = applicationManager.getBankMappingDetails();
                PaginationVO paginationVO = (PaginationVO) session.getAttribute("paginationVO");

                request.setAttribute("fileDetailsVOMap", fileDetailsVOMap);
                request.setAttribute("bankMappingVOList", bankTypeVOList);
                request.setAttribute("paginationVO", paginationVO);

                logger.debug("fileDetailsVOMap===" + fileDetailsVOMap);
                logger.debug("bankTypeVOList==="+bankTypeVOList);
                rd.forward(request, response);
            }
            else
            {
                String fileName = request.getParameter("fileName");
                String filePath = GATEWAY_TEMPLATE_PATH + fileName;

                applicationManager.sendFile(filePath, fileName, response);
                return;
            }
        }
        catch (PZTechnicalViolationException e)
        {
            logger.debug(e.getMessage());
        }
        catch (PZDBViolationException e)
        {
           logger.error(" Catch PZDBViolationExeption..",e);
        }
        catch (SystemError systemError)
        {
            logger.error("Catch SYstemError",systemError);
        }
        catch (Exception e)
        {
            logger.error("Catch Exception.",e);
        }
    }
}
