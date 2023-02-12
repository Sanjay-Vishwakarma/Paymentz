import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemAccessLogger;
import com.manager.FraudRuleManager;
import com.manager.vo.PaginationVO;
import com.manager.vo.fraudruleconfVOs.FraudRuleChangeIntimationVO;

import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

import java.util.*;

/**
 * Created by Sneha on 13/8/15.
 */
public class FraudRuleChangeIntimationList extends HttpServlet
{
    private static Logger logger = new Logger(FraudRuleChangeIntimationList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doProcess(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        doProcess(request, response);
    }
    public void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {
            logger.debug("session out");
            response.sendRedirect("/icici/sessionout.jsp");
            return;
        }

        RequestDispatcher rd = request.getRequestDispatcher("/fraudRuleChangeIntimationList.jsp?ctoken=" + user.getCSRFToken());
        String errormsg = "";
        String EOL = "<BR>";
        try
        {
            validateOptionalParameter(request);
        }
        catch (ValidationException e)
        {
            logger.error("Enter valid input",e);
            errormsg +=  errormsg + e.getMessage() + EOL ;
            request.setAttribute("msg",errormsg);
            rd.forward(request, response);
            return;
        }
        StringBuffer msg = new StringBuffer();

        String fsaccountid = request.getParameter("fsaccountid");
        String status = request.getParameter("status");

        if (!ESAPI.validator().isValidInput("fsaccountid",fsaccountid,"SafeString",255,false))
        {
            msg.append("Please Select Fraud Account ID");
        }
        if(msg.length()>0)
        {
            request.setAttribute("msg", msg.toString());
            rd.forward(request, response);
            return;
        }
        FraudRuleManager ruleManager = new FraudRuleManager();
        PaginationVO paginationVO = new PaginationVO();

        try
        {
            paginationVO.setInputs("fsaccountid=" + fsaccountid);
            paginationVO.setPage("FraudRuleChangeIntimationList");
            paginationVO.setPageNo(Functions.convertStringtoInt(request.getParameter("SPageno"), 1));
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(request.getParameter("currentblock"), 1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(request.getParameter("SRecords"), 20));

            List<FraudRuleChangeIntimationVO> intimationVOList = ruleManager.getFraudRuleChargeIntimation(fsaccountid, status, paginationVO);
            request.setAttribute("intimationVO", intimationVOList);
            request.setAttribute("paginationVO", paginationVO);
            request.setAttribute("fsaccountid", fsaccountid);

        }
        catch (PZDBViolationException e)
        {
            logger.error("Error while performing db operation in ::::" + e);
            msg.append("No records found.");
        }

        request.setAttribute("msg", msg.toString());
        rd = request.getRequestDispatcher("/fraudRuleChangeIntimationList.jsp?ctoken=" + user.getCSRFToken());
        rd.forward(request, response);
        return;
    }
    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }

}
