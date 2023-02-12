
import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemAccessLogger;
import com.manager.ProfileManagementManager;
import com.manager.vo.PaginationVO;
import com.manager.vo.riskRuleVOs.RuleVO;

import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
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

import java.util.*;

/**
 * Created by Admin on 17/8/15.
 */
public class RiskRuleDetails extends HttpServlet
{
    private static Logger logger = new Logger(RiskRuleDetails.class.getName());
    public void doPost(HttpServletRequest request,HttpServletResponse response)  throws IOException,ServletException
    {

        HttpSession session = Functions.getNewSession(request);

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        //manager instance
        ProfileManagementManager profileManagementManager = new ProfileManagementManager();
        //LIST OF VO
        List<RuleVO> riskRuleVOList=null;
        //VO instance
        RuleVO riskRuleVO = new RuleVO();
        PaginationVO paginationVO = new PaginationVO();
        RequestDispatcher rdError = request.getRequestDispatcher("/riskRuleManagement.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        RequestDispatcher rdSuccess = request.getRequestDispatcher("/riskRuleManagement.jsp?UPDATE=Success&ctoken="+user.getCSRFToken());
        try
        {
            ValidationErrorList validationErrorList=validateParameter(request);
            if(!validationErrorList.isEmpty())
            {
                request.setAttribute("error", validationErrorList);
                rdError.forward(request,response);
                return;
            }
            //setting vo using request
            riskRuleVO.setId(request.getParameter("ruleid"));
            //pagination VO
            paginationVO.setInputs("ruleid="+request.getParameter("ruleid"));
            paginationVO.setPageNo(Functions.convertStringtoInt(request.getParameter("SPageno"),1));
            paginationVO.setPage(RiskRuleDetails.class.getName());
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(request.getParameter("currentblock"),1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(request.getParameter("SRecords"),15));
            riskRuleVOList=profileManagementManager.getListOfRiskRuleDetails(riskRuleVO,paginationVO);

            request.setAttribute("riskRuleVOList",riskRuleVOList);
            request.setAttribute("paginationVO",paginationVO);
            rdSuccess.forward(request,response);
        }
        catch(PZDBViolationException dbe)
        {
            logger.error("SQL exception",dbe);
            PZExceptionHandler.handleDBCVEException(dbe, session.getAttribute("merchantid").toString(), "Exception While getting Risk Rule List.");
            request.setAttribute("catchError","Kindly check on for the Risk Rule after some time");
            rdError.forward(request,response);
            return;
        }
    }
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
    {
        doGet(request,response);
    }

    private ValidationErrorList validateParameter(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.RISKRULEID);

        ValidationErrorList validationErrorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListMandatory,validationErrorList,true);

        return validationErrorList;
    }

}
