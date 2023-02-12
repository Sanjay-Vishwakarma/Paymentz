import com.dao.ApplicationManagerDAO;
import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemAccessLogger;
import com.manager.ApplicationManager;
import com.manager.vo.PaginationVO;

import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import com.vo.applicationManagerVOs.ApplicationManagerVO;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;

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
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 3/30/15
 * Time: 2:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class ListofAppMember  extends HttpServlet
{
    private static Logger logger = new Logger(ListofAppMember.class.getName());
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {

        HttpSession session = request.getSession();

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        Functions functions =new Functions();
        ValidationErrorList validationErrorList=null;
        //merchantDao instance
        ApplicationManager applicationManager=new ApplicationManager();
        ApplicationManagerDAO applicationManagerDAO=new ApplicationManagerDAO();
        //Vo initialization
        PaginationVO paginationVO = new PaginationVO();
        //List of ApplicationManagerList Declaration
        List<ApplicationManagerVO> applicationManagerVOList=null;

        RequestDispatcher rdError = request.getRequestDispatcher("/listofapplicationmember.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        RequestDispatcher rdSuccess = request.getRequestDispatcher("/listofapplicationmember.jsp?MES=Success&ctoken="+user.getCSRFToken());
        try{

            logger.debug("CURRENTBLOCK:::"+request.getParameter("currentblock"));

            validationErrorList=validateOptionalParameter(request);
            if(!validationErrorList.isEmpty())
            {
                request.setAttribute("error",validationErrorList);
                rdError.forward(request,response);
                return;
            }
            session.setAttribute("applicationManagerVO",null);
            session.setAttribute("navigationVO",null);
            //memberID and ApplicationId
            String applicationId = request.getParameter("application_id");
            String memberId = request.getParameter("apptoid");
            if(functions.isValueNull((String) request.getAttribute("apptoid")))
                memberId = (String) request.getAttribute("apptoid");
            //inserting the values of Pagination
            paginationVO.setInputs("memberId="+memberId+"&applicationId="+applicationId);
            paginationVO.setPageNo(Functions.convertStringtoInt(request.getParameter("SPageno"), 1));
            paginationVO.setPage(ListofAppMember.class.getName());
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(request.getParameter("currentblock"), 1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(request.getParameter("SRecords"),15));
            //inserting MemberID & ApplicationID details
            applicationManagerVOList=applicationManager.getapplicationManagerVO(memberId, applicationId, paginationVO);
            //response
            request.setAttribute("paginationVO",paginationVO);
            request.setAttribute("applicationManagerVOs", applicationManagerVOList);
            request.setAttribute("memberId",memberId);
            rdSuccess.forward(request,response);

        }
        catch (Exception e)
        {
            logger.error("Main class exception::", e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
    }

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {

    }
    private ValidationErrorList validateOptionalParameter(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.APPTOID);
        inputFieldsListMandatory.add(InputFields.APPLICATIONID);
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);
        ValidationErrorList validationErrorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListMandatory,validationErrorList,true);
        return validationErrorList;
    }

}
