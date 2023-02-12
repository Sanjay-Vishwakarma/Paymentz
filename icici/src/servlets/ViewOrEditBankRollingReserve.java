import com.directi.pg.Admin;
import com.directi.pg.Logger;
import com.manager.BankManager;
import com.manager.vo.ActionVO;
import com.manager.vo.BankRollingReserveVO;
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
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 9/4/14
 * Time: 5:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class ViewOrEditBankRollingReserve extends HttpServlet
{
    private static Logger logger = new Logger(ViewOrEditBankRollingReserve.class.getName());
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {

        HttpSession session = request.getSession();

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        //manager instance
        BankManager bankManager = new BankManager();
        //Vo instance
        ActionVO actionVO = new ActionVO();
        BankRollingReserveVO singleBankRollingReserveVO = null;
        ValidationErrorList validationErrorList =validateMandatoryParameter(request);

        RequestDispatcher rdError = request.getRequestDispatcher("/bankRollingReserveMaster.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        RequestDispatcher rdSuccess = request.getRequestDispatcher("/bankRollingReserveMaster.jsp?MES=Success&ctoken="+user.getCSRFToken());
        if(!validationErrorList.isEmpty())
        {
            request.setAttribute("error",validationErrorList);
            rdError.forward(request,response);
            return;
        }
        //getting Parameter
        String action=request.getParameter("action");

        actionVO.setActionCriteria(action);

        singleBankRollingReserveVO=bankManager.getSingleBankRollingReserveVOActionSpecific(actionVO);

        request.setAttribute("actionVO",actionVO);
        request.setAttribute("singleBankRollingReserveVO",singleBankRollingReserveVO);
        rdSuccess.forward(request,response);

    }

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        doPost(request,response);
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
