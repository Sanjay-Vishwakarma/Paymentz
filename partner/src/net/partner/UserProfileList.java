package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.ProfileManagementManager;
import com.manager.vo.PaginationVO;
import com.manager.vo.userProfileVOs.MerchantVO;
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
import java.util.Map;

/**
 * Created on 21/9/15.
 */
public class UserProfileList extends HttpServlet
{
    private static Logger logger = new Logger(UserProfileList.class.getName());

    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        HttpSession session = Functions.getNewSession(request);

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
        Map<String,MerchantVO> merchantVOMap =null;
        PaginationVO paginationVO=new PaginationVO();
        //Error List
        ValidationErrorList validationErrorList = null;

        RequestDispatcher rdError=request.getRequestDispatcher("/userProfile.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        try
        {
            validationErrorList = validateOptionParameter(request);
            if (!validationErrorList.isEmpty())
            {
                request.setAttribute("error", validationErrorList);
                rdError.forward(request, response);
                return;
            }
            //setting Pagination VO
            paginationVO.setInputs("toid=" + request.getParameter("toid"));
            paginationVO.setPageNo(Functions.convertStringtoInt(request.getParameter("SPageno"), 1));
            paginationVO.setPage(UserProfileList.class.getName().substring(UserProfileList.class.getName().lastIndexOf(".")+1));
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(request.getParameter("currentblock"), 1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(request.getParameter("SRecords"), 15));
            //getting Members according to Member and partner
            merchantVOMap = profileManagementManager.getMapOfUserSetting(request.getParameter("toid"), session.getAttribute("merchantid").toString(), paginationVO);

            request.setAttribute("merchantVOMap",merchantVOMap);
            request.setAttribute("paginationVO",paginationVO);
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZDViolation Exception while getting User profile details:::", e);
            PZExceptionHandler.handleDBCVEException(e, "partnerId:::" + (String) session.getAttribute("merchantid"), PZOperations.USERID);
            //request.setAttribute("catchError","Kindly check for the User Profile after sometime");
            rdError.forward(request,response);
            return;
        }

        RequestDispatcher rdSuccess=request.getRequestDispatcher("/userProfile.jsp?ctoken="+user.getCSRFToken());
        rdSuccess.forward(request,response);
        return;

    }


    public ValidationErrorList validateOptionParameter(HttpServletRequest request)
    {
        ValidationErrorList validationErrorList = new ValidationErrorList();
        List<InputFields> inputOptionalParameter = new ArrayList<InputFields>();

        //inputOptionalParameter.add(InputFields.TOID);
        inputOptionalParameter.add(InputFields.PAGENO);
        inputOptionalParameter.add(InputFields.RECORDS);

        InputValidator inputValidator = new InputValidator();
        inputValidator.InputValidations(request,inputOptionalParameter,validationErrorList,true);

        return validationErrorList;
    }
}
