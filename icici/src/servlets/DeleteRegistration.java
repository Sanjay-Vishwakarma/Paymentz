/**
 * Created by pranav on 06-05-2017.
 */
import com.directi.pg.Admin;
import com.directi.pg.Functions;
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
        Functions functions = new Functions();
        HttpSession session = request.getSession();
        if (!Admin.isLoggedIn(session))
        {
            response.sendRedirect("/icici/Logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        session.setAttribute("submit", "Registration History");

        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();

        List<TokenDetailsVO> tokenDetailsVOList = null;
        TokenManager tokenManager = new TokenManager();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        TokenDetailsVO tokenDetailsVO = new TokenDetailsVO();

        String fDate= null;
        String tDate = null;
        String firstName=null;
        String lastName=null;
        String email=null;
        String memberid=null;
        String fmonth = "";
        String tmonth = "";
        String fyear = "";
        String tyear = "";

        RequestDispatcher rd = request.getRequestDispatcher("/listOfMerchantRegistration.jsp?ctoken=" + user.getCSRFToken());

        firstName=request.getParameter("firstname");
        lastName=request.getParameter("lastname");
        email=request.getParameter("email");
        memberid=request.getParameter("memberid");
        tokenDetailsVOList = (List<TokenDetailsVO>)request.getAttribute("listOfMerchantRegistrations");
        String action= request.getParameter("action");
        String token= request.getParameter("tokenid");

        log.debug("memberid in Delete Regitrstion----" + memberid);
        commonValidatorVO.setToken(request.getParameter("tokenid"));
        //merchantDetailsVO.setMemberId((String) session.getAttribute("merchantid"));
        merchantDetailsVO.setMemberId(memberid);
        log.debug("memberid in Delete Regitrstion1----"+merchantDetailsVO.getMemberId());

        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setErrorCodeListVO(errorCodeListVO);

        int records=15;
        int pageno=1;
        pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(request.getParameter("SRecords"),15);
        try
        {
            fDate=request.getParameter("fdate");
            tDate=request.getParameter("tdate");
            fmonth=request.getParameter("fmonth");
            tmonth=request.getParameter("tmonth");
            fyear=request.getParameter("fyear");
            tyear=request.getParameter("tyear");

            PaginationVO paginationVO=new PaginationVO();
            paginationVO.setInputs("&fdate=" + fDate + "&tdate=" + tDate + "&firstname=" + firstName + "&lastname=" + lastName + "&email=" + email + "&memberid=" + memberid + "&fmonth=" + fmonth + "&tmonth=" + tmonth + "&fyear=" + fyear + "&tyear=" + tyear);
            paginationVO.setPage(DeleteRegistration.class.getName());
            paginationVO.setPageNo(pageno);
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(request.getParameter("currentblock"), 1));
            paginationVO.setRecordsPerPage(records);

            request.setAttribute("fdate", request.getParameter("fdate"));
            request.setAttribute("tdate", request.getParameter("tdate"));
            request.setAttribute("firstname", firstName);
            request.setAttribute("lastName", lastName);
            request.setAttribute("email", email);
            request.setAttribute("memberid", memberid);
            request.setAttribute("paginationVO", paginationVO);
            request.setAttribute("listOfMerchantRegistrations", tokenDetailsVOList);

            //getting token details
                tokenDetailsVO = tokenManager.getRegisteredTokenDetailsByMerchant(memberid, commonValidatorVO.getToken(), commonValidatorVO, tokenDetailsVO); //get Merchant level registration details
                /*errorCodeVO.setApiDescription("This registration can not be deleted.");
                request.setAttribute("msg",errorCodeVO.getApiDescription());
                rd.forward(request,response);*/

            if("N".equals(tokenDetailsVO.getIsActive()))
            {
                errorCodeVO = errorCodeUtils.getReferenceErrorCode(ErrorName.VALIDATION_REFERENCE_DELETE_TOKEN);
                //tokenManager.doMerchantRegisteredTokenInactive(commonValidatorVO.getMerchantDetailsVO().getMemberId(),commonValidatorVO.getToken());
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
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.TOKEN_INACTIVT_REJECTED);
                request.setAttribute("msg",errorCodeVO.getApiDescription());
                rd.forward(request,response);
            }

        }
        catch (PZDBViolationException e)
        {
            request.setAttribute("error", "Internal error, please contact support");
        }

        catch (Exception e)
        {
            request.setAttribute("error", "Internal error, please contact support");
        }

        try
        {
            if (functions.isValueNull(action) && "Delete".equals(action))
            {
                String message=tokenManager.deleteTokenFromRegistration(token);
                request.setAttribute("delmessage",message);
                rd.forward(request,response);
            }
        }
        catch (Exception e)
        {
            log.error("Exception while delete record:: ",e);
        }
        rd.forward(request, response);
    }
}

