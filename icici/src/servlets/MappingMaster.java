import com.directi.pg.*;
import com.manager.MerchantMonitoringManager;
import com.manager.vo.merchantmonitoring.MonitoringParameterMappingVO;

import com.payment.exceptionHandler.PZDBViolationException;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 11/9/12
 * Time: 11:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class MappingMaster extends HttpServlet
{
    private static Logger logger = new Logger(MappingMaster.class.getName());
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
            logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        String errormsg = "";
        RequestDispatcher rd = req.getRequestDispatcher("/mappingMaster.jsp?ctoken=" + user.getCSRFToken());

        errormsg = errormsg + validateParameters(req);
        Functions functions = new Functions();

        if (functions.isValueNull(errormsg))
        {
            req.setAttribute("error", errormsg);
            rd.forward(req, res);
            return;
        }
        String memberId = req.getParameter("memberid");

        MerchantMonitoringManager merchantMonitoringManager = new MerchantMonitoringManager();
        //List<MerchantTerminalThresholdVO> merchantTerminalThresholdVOs = null;


        Map<String, List<MonitoringParameterMappingVO>> stringListMap=null;
        try
        {
            stringListMap=merchantMonitoringManager.getDailyMonitoringParameterGroupByMerchantId(memberId);
        }
        catch (PZDBViolationException e)
        {
           logger.error("Catch PZDBViolationException...",e);
        }

        //req.setAttribute("merchantTerminalThresholdVOs", merchantTerminalThresholdVOs);
        req.setAttribute("stringListMap", stringListMap);
        rd.forward(req, res);
    }

    private String validateParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.MEMBERID);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req, inputFieldsListOptional, errorList, false);
        if (!errorList.isEmpty())
        {
            for (InputFields inputFields : inputFieldsListOptional)
            {
                if (errorList.getError(inputFields.toString()) != null)
                {
                    logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error + errorList.getError(inputFields.toString()).getMessage();
                }
            }
        }
        return error;
    }

}
