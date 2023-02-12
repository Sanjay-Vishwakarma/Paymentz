import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.ApplicationManager;
import com.vo.applicationManagerVOs.ConsolidatedApplicationVO;

import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
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
import java.util.Map;


/**
 * Created by Pradeep on 22/07/2015.
 */
public class ManageBankApp extends HttpServlet
{
    private static Logger logger= new Logger(ManageBankApp.class.getName());
    protected void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {

        HttpSession session = request.getSession();

        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        Functions functions =new Functions();

        ValidationErrorList validationErrorList=null;

        //ConsolidatedApplicationVO consolidatedApplicationVO = new ConsolidatedApplicationVO();

        Map<String,ConsolidatedApplicationVO> consolidatedApplicationVOMap =null;

        ApplicationManager applicationManager = new ApplicationManager();

        RequestDispatcher rdError = request.getRequestDispatcher("/managebankApp.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        RequestDispatcher rdSuccess = request.getRequestDispatcher("/managebankApp.jsp?MES=Success&ctoken="+user.getCSRFToken());
        try
        {
            validationErrorList=validateOptionalParameter(request);

            if(!validationErrorList.isEmpty())
            {
                request.setAttribute("errorC",validationErrorList);
                rdError.forward(request,response);
                return;
            }

            String MemberID=request.getParameter("memberid");
            String Name=request.getParameter("gateway");
            String consolidatedId=request.getParameter("consolidatedId");

            consolidatedApplicationVOMap=applicationManager.getconsolidated_applicationForMemberIdOrPgTypeId(MemberID, Name, consolidatedId);
            if(consolidatedApplicationVOMap!=null && consolidatedApplicationVOMap.size()>0)
            {
                logger.debug("Query---------- "+consolidatedApplicationVOMap);
                request.setAttribute("consolidatedApplicationVOMap", consolidatedApplicationVOMap);
                rdSuccess.forward(request,response);
                return;
            }
            else
            {
                request.setAttribute("errorC","Invalid Member Gateway Configuration");
                rdError.forward(request,response);
                return;
            }


        }
        catch (Exception e)
        {
            logger.error("Main class exception::", e);
        }

    }

    protected void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request, response);
    }
    private ValidationErrorList validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.MEMBERID);
        inputFieldsListMandatory.add(InputFields.GATEWAY);
        inputFieldsListMandatory.add(InputFields.CONSOLIDATEDID);

        ValidationErrorList validationErrorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListMandatory,validationErrorList,true);
        return validationErrorList;
    }

}




