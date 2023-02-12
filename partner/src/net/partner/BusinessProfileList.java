package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.ProfileManagementManager;
import com.manager.vo.PaginationVO;
import com.manager.vo.businessRuleVOs.ProfileVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.operations.PZOperations;
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
 * Created by Pradeep on 28/08/2015.
 */
public class BusinessProfileList extends HttpServlet
{
    private static Logger logger = new Logger(BusinessProfileList.class.getName());
   // private Functions functions = new Functions();

    public void doPost(HttpServletRequest request,HttpServletResponse response)throws IOException,ServletException
    {
        HttpSession session = Functions.getNewSession(request);

       // String partnerid = (String)session.getAttribute("merchantid");

        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        //Manager Instance
        ProfileManagementManager profileManagementManager = new ProfileManagementManager();
        //List Of Vo
        List<ProfileVO> businessProfileVOList =null;
        PaginationVO paginationVO=new PaginationVO();
        //Error List
        ValidationErrorList validationErrorList = null;

        RequestDispatcher rdError=request.getRequestDispatcher("/businessProfile.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        validationErrorList=validateOptionalParameter(request);
        try
        {
            if(!validationErrorList.isEmpty())
            {
                request.setAttribute("error",validationErrorList);
                rdError.forward(request,response);
                return;
            }
            logger.debug("profileID::::"+request.getParameter("profileid"));
            //setting Pagination VO
            paginationVO.setInputs("profileid=" + request.getParameter("profileid"));
            paginationVO.setPageNo(Functions.convertStringtoInt(request.getParameter("SPageno"), 1));
            paginationVO.setPage(BusinessProfileList.class.getName().substring(BusinessProfileList.class.getName().lastIndexOf(".")+1));
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(request.getParameter("currentblock"), 1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(request.getParameter("SRecords"),15));

            businessProfileVOList=profileManagementManager.getListOfBusinessProfileVO((String) session.getAttribute("merchantid"), request.getParameter("profileid"), " profileid ASC", paginationVO);
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZDViolation Exception while getting Business profile details:::",e);
            PZExceptionHandler.handleDBCVEException(e,"partnerId:::"+(String)session.getAttribute("merchantid"), PZOperations.BUSINESSPROFILE);
            //request.setAttribute("catchError","Kindly check for the Business Profile after sometime");
            rdError.forward(request,response);
            return;
        }

        request.setAttribute("businessProfileVOList",businessProfileVOList);
        request.setAttribute("paginationVO",paginationVO);
        RequestDispatcher rdSuccess=request.getRequestDispatcher("/businessProfile.jsp?ctoken="+user.getCSRFToken());
        rdSuccess.forward(request,response);
        return;

    }

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request, response);
    }

    private ValidationErrorList validateOptionalParameter(HttpServletRequest request)
    {
        List<InputFields> inputOptionalFields=new ArrayList<InputFields>();
        inputOptionalFields.add(InputFields.BUSINESSPROFILEID);
        inputOptionalFields.add(InputFields.PAGENO);
        inputOptionalFields.add(InputFields.RECORDS);

        ValidationErrorList validationErrorList = new ValidationErrorList();
        InputValidator inputValidator = new InputValidator();
        inputValidator.InputValidations(request,inputOptionalFields,validationErrorList,true);

        return validationErrorList;
    }
}
