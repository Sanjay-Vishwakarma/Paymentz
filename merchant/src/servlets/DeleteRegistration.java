import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.manager.TokenManager;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.PaginationVO;
import com.manager.vo.TokenDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.User;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Created by Khushali on 1/30/2017.
 */
public class DeleteRegistration extends HttpServlet
{
    private static Logger log = new Logger(DeleteRegistration.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        log.debug("inside Delete Registration");

        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        Merchants merchants = new Merchants();
        HttpSession session = request.getSession();
        List<TokenDetailsVO> tokenDetailsVOList = null;

        TokenManager tokenManager = new TokenManager();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        TokenDetailsVO tokenDetailsVO = new TokenDetailsVO();
        PaginationVO paginationVO = new PaginationVO();

        String fDate= null;
        String tDate = null;
        String firstName=null;
        String lastName=null;
        String email=null;
        String description=null;

        if (!merchants.isLoggedIn(session))
        {
            response.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        session.setAttribute("submit","Registration History");

        RequestDispatcher rd = request.getRequestDispatcher("/listOfMerchantRegistrations.jsp?ctoken=" + user.getCSRFToken());
        description = request.getParameter("description");
        firstName=request.getParameter("firstname");
        lastName=request.getParameter("lastname");
        email=request.getParameter("email");
        commonValidatorVO.setToken(request.getParameter("tokenid"));
        merchantDetailsVO.setMemberId((String) session.getAttribute("merchantid"));

        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setErrorCodeListVO(errorCodeListVO);

        try
        {
            fDate=request.getParameter("fdate");
            tDate=request.getParameter("tdate");

            request.setAttribute("fdate", request.getParameter("fdate"));
            request.setAttribute("tdate", request.getParameter("tdate"));
            request.setAttribute("firstname", firstName);
            request.setAttribute("lastName",lastName);
            request.setAttribute("email",email);

            paginationVO.setInputs("&fdate=" + fDate + "&tdate=" + tDate + "&firstname=" + firstName + "&lastname=" + lastName + "&description=" + description + "&email=" + email);
            paginationVO.setPage("ListOfMerchantRegistrations");

            //getting token details
            tokenDetailsVO = tokenManager.getRegisteredTokenDetailsByMerchant(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getToken(), commonValidatorVO, tokenDetailsVO); //get Merchant level registration details

            if("N".equals(tokenDetailsVO.getIsActive()))
            {
                errorCodeVO = errorCodeUtils.getReferenceErrorCode(ErrorName.VALIDATION_REFERENCE_DELETE_TOKEN);
                tokenManager.doMerchantRegisteredTokenInactive(commonValidatorVO.getMerchantDetailsVO().getMemberId(),commonValidatorVO.getToken());
                request.setAttribute("error",errorCodeVO.getApiDescription());
                rd.forward(request,response);
            }

            String status = tokenManager.doMerchantRegisteredTokenInactive(commonValidatorVO.getMerchantDetailsVO().getMemberId(),commonValidatorVO.getToken());
            if("success".equals(status))
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.TOKEN_INACTIVT_SUCCESSFUL);
                request.setAttribute("msg",errorCodeVO.getApiDescription());
                rd.forward(request,response);
            }
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.TOKEN_CREATION_FAILED);
                request.setAttribute("error",errorCodeVO.getApiDescription());
                rd.forward(request,response);
            }
            request.setAttribute("paginationVO", paginationVO);
            request.setAttribute("listOfMerchantRegistrations",tokenDetailsVOList);
            rd.forward(request, response);

        }
        catch (PZDBViolationException e)
        {
            request.setAttribute("error", "Internal error, please contact support");
            rd.forward(request,response);
        }

        catch (Exception e)
        {
            request.setAttribute("error", "Internal error, please contact support");
            rd.forward(request,response);
        }
    }
}
