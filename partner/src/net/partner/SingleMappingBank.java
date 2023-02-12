package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;

import com.manager.vo.ActionVO;

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
 * Created by NIKET on 1/18/2016.
 */
public class SingleMappingBank extends HttpServlet
{
    private static Logger logger =new Logger(SingleUserProfile.class.getName());

    private Functions functions = new Functions();

    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        HttpSession session = functions.getNewSession(request);

        PartnerFunctions partnerFunctions=new PartnerFunctions();
        if (!partnerFunctions.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        //VO
        ActionVO actionVO = new ActionVO();


        //Validation Error List
        ValidationErrorList validationErrorList=null;



        //Request Dispatcher
        //RequestDispatcher rdSuccess= request.getRequestDispatcher("/addOrUpdateMerchantMappingBank.jsp?MES=SUCCESS&ctoken=" + user.getCSRFToken());
        RequestDispatcher rdError= request.getRequestDispatcher("/net/MerchantMappingBank?MES=ERR&ctoken=" + user.getCSRFToken());

        try
        {
            validationErrorList = validateMandatorParameter(request);
            if (!validationErrorList.isEmpty())
            {
                logger.error("validation error");
                request.setAttribute("error", validationErrorList);
                rdError.forward(request, response);
                return;
            }

            actionVO.setAllContentAuto(request.getParameter("action"));

            if (actionVO.isEdit() || actionVO.isView())
            {

            }

        }
        catch (Exception e)
        {
            logger.error("PZDViolation Exception while getting User profile details:::", e);

        }
        session.setAttribute("actionVO", actionVO);
        request.setAttribute("actionVO", actionVO);
        //rdSuccess.forward(request, response);



    }

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        doGet(request, response);
    }

    private ValidationErrorList validateMandatorParameter(HttpServletRequest request)
    {
        List<InputFields> inputMandatoryParameter= new ArrayList<InputFields>();
        inputMandatoryParameter.add(InputFields.SMALL_ACTION);

        InputValidator inputValidator = new InputValidator();
        ValidationErrorList validationErrorList = new ValidationErrorList();
        inputValidator.InputValidations(request,inputMandatoryParameter,validationErrorList,false);
        return validationErrorList;
    }

}
