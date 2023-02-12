import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemAccessLogger;
import com.manager.ProfileManagementManager;
import com.manager.vo.PaginationVO;
import com.manager.vo.businessRuleVOs.RuleVO;

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
 * Created by admin on 27/8/15.
 */
public class BusinessRuleDetails extends HttpServlet
{
    private static Logger logger = new Logger(BusinessRuleDetails.class.getName());
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
        List<RuleVO> businessRuleVOList=null;
        //VO instance
        RuleVO businessRuleVO = new RuleVO();
        PaginationVO paginationVO = new PaginationVO();
        RequestDispatcher rdError = request.getRequestDispatcher("/businessRuleManagement.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        RequestDispatcher rdSuccess = request.getRequestDispatcher("/businessRuleManagement.jsp?UPDATE=Success&ctoken="+user.getCSRFToken());
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
            businessRuleVO.setId(request.getParameter("businessRuleId"));
            //pagination VO
            paginationVO.setInputs("businessRuleId="+request.getParameter("businessRuleId"));
            paginationVO.setPageNo(Functions.convertStringtoInt(request.getParameter("SPageno"),1));
            paginationVO.setPage(BusinessRuleDetails.class.getName());
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(request.getParameter("currentblock"),1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(request.getParameter("SRecords"),15));
            businessRuleVOList=profileManagementManager.getListOfBusinessRuleDetails(businessRuleVO, paginationVO);

            request.setAttribute("businessRuleVOList",businessRuleVOList);
            request.setAttribute("paginationVO",paginationVO);
            rdSuccess.forward(request,response);
        }
        catch(PZDBViolationException dbe)
        {
            logger.error("SQL exception",dbe);
            PZExceptionHandler.handleDBCVEException(dbe, session.getAttribute("merchantid").toString(), "Exception While getting Business Rule List.");
            request.setAttribute("catchError","Kindly check on for the Business Rule after some time");
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
        inputFieldsListMandatory.add(InputFields.BUSINESSRULEID);
        ValidationErrorList validationErrorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListMandatory,validationErrorList,true);
        return validationErrorList;
    }
}
