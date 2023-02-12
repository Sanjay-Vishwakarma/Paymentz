package net.agent;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemAccessLogger;
import com.manager.FileManager;
import com.manager.utils.FileHandlingUtil;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 2/3/15
 * Time: 6:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class SendFile extends HttpServlet
{
    private static Logger logger = new Logger(SendFile.class.getName());

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        RequestDispatcher rdError = null;
        HttpSession session = null;
        try
        {
            logger.debug("Inside Send File");
            AgentFunctions agentFunctions = new AgentFunctions();
            //File instance
            File file = null;
            //manager instance
            FileManager fileManager = new FileManager();
            //fileHandling util instance
            FileHandlingUtil fileHandlingUtil = new FileHandlingUtil();
            session = Functions.getNewSession(request);
            if (!agentFunctions.isLoggedInAgent(session))
            {
                response.sendRedirect("/agent/logout.jsp");
                return;
            }
            User user = (User) session.getAttribute("ESAPIUserSessionKey");

            rdError = request.getRequestDispatcher("/wireReport.jsp?MES=FILEERR&ctoken=" + user.getCSRFToken());


            ValidationErrorList errorList = validateMandatoryParameters(request);

            if (!errorList.isEmpty())
            {
                logger.debug("invalid data Provided for Send File");
                request.setAttribute("error", errorList);
                rdError.forward(request, response);
                return;
            }
            logger.debug("valid data Provided for Send File");

            String fileName = request.getParameter("file");
            file = fileManager.getAgentReportFile(fileName);
            //sending file if found
            fileHandlingUtil.sendFile(file, response);

        }
        catch (PZTechnicalViolationException pte)
        {
            logger.error("File Not found exception or IOException::", pte);  //To change body of catch statement use File | Settings | File Templates.
            PZExceptionHandler.handleTechicalCVEException(pte, session.getAttribute("merchantid").toString(), "Exception while downloading the files from server related to Wire Reports(.pdf Or .xls)");
            request.setAttribute("file", "no");
            rdError.forward(request, response);
        }
        /*catch (FileNotFoundException e)
        {
            logger.error("File Not found exception::", e);
            request.setAttribute("file", "no");
            rdError.forward(request, response);
        }*/
        /*catch (Exception e)
        {
            logger.error("Servlet File Send Exception ::", e);
        }*/
    }
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request,response);
    }
    public ValidationErrorList validateMandatoryParameters(HttpServletRequest req)
    {
        List<InputFields> inputMandatoryParameter= new ArrayList<InputFields>();
        inputMandatoryParameter.add(InputFields.FILENAME);
        InputValidator inputValidator = new InputValidator();

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputMandatoryParameter, errorList,false);

        return errorList;
    }

}
