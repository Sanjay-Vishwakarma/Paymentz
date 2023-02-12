package net.partner;

import com.directi.pg.Logger;
import com.manager.BankManager;
import com.manager.vo.ActionVO;
import com.manager.vo.BankWireManagerVO;
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
 * Created by Admin on 1/31/2020.
 */
public class ViewOrEditBankWireManager extends HttpServlet
{
    private static Logger logger = new Logger(ViewOrEditBankWireManager.class.getName());
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        HttpSession session = request.getSession();
        PartnerFunctions partner=new PartnerFunctions();

        if (!partner.isLoggedInPartner(session))
        {   logger.debug("Admin is logout ");
            response.sendRedirect("/partner/logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        BankManager bankManager = new BankManager();
        ActionVO actionVO = new ActionVO();
        BankWireManagerVO singleBankWireManagerVO = null;
        ValidationErrorList validationErrorList =validateMandatoryParameter(request);

        RequestDispatcher rdError = request.getRequestDispatcher("/addNewBankWire.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        RequestDispatcher rdSuccess = request.getRequestDispatcher("/addNewBankWire.jsp?MES=Success&ctoken="+user.getCSRFToken());
        if(!validationErrorList.isEmpty())
        {
            request.setAttribute("error",validationErrorList);
            rdError.forward(request,response);
            return;
        }
        //getting Parameter
        String action=request.getParameter("action");

        actionVO.setActionCriteria(action);

        singleBankWireManagerVO=bankManager.getSingleBankWireManagerActionSpecific(actionVO);

        request.setAttribute("actionVO",actionVO);
        request.setAttribute("singleBankWireManagerVO",singleBankWireManagerVO);
        rdSuccess.forward(request,response);
    }

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request, response);
    }
    private ValidationErrorList validateMandatoryParameter(HttpServletRequest request)
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.SMALL_ACTION);
        ValidationErrorList validationErrorList = new ValidationErrorList();
        inputValidator.InputValidations(request,inputFieldsListMandatory,validationErrorList,false);
        return validationErrorList;
    }
}