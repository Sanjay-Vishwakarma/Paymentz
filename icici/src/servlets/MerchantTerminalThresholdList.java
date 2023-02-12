import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.MerchantMonitoringManager;
import com.manager.vo.merchantmonitoring.MerchantTerminalThresholdVO;
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

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 3/31/16
 * Time: 7:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchantTerminalThresholdList extends HttpServlet
{
    private static Logger logger = new Logger(MerchantTerminalThresholdList.class.getName());

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
        RequestDispatcher rd = req.getRequestDispatcher("/merchantTerminalThreshold.jsp?ctoken=" + user.getCSRFToken());

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
        List<MerchantTerminalThresholdVO> merchantTerminalThresholdVOs = null;

        try
        {
            merchantTerminalThresholdVOs = merchantMonitoringManager.getMerchantTerminalThresholdDetails(memberId);
        }
        catch (PZDBViolationException e)
        {

        }
        req.setAttribute("merchantTerminalThresholdVOs", merchantTerminalThresholdVOs);
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
