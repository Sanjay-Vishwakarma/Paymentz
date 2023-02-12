package servlets;

import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.ApplicationManager;
import com.manager.vo.FileDetailsVO;
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

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 3/12/15
 * Time: 8:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class MemberDocoumentsList extends HttpServlet
{
    private static Logger logger = new Logger(MemberDocoumentsList.class.getName());

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
        //Manager instance
        ApplicationManager applicationManager =new ApplicationManager();
        //Vo initialization


        //List of BankMerchantSettlementList Declaration
        //List<FileDetailsVO> fileDetailsVOList= null;

        //Test start
        try{

            ValidationErrorList validationErrorList=validateOptionalParameter(request);

            RequestDispatcher rdError = request.getRequestDispatcher("/memberdocuments.jsp?MES=ERR&ctoken="+user.getCSRFToken());
            RequestDispatcher rdSuccess = request.getRequestDispatcher("/memberdocuments.jsp?MES=Success&ctoken="+user.getCSRFToken());
            if(!validationErrorList.isEmpty())
            {
                request.setAttribute("error",validationErrorList);
                rdError.forward(request,response);
                return;
            }

            //accountId and pgTypeId
            String merchantId= request.getParameter("memberid");

            //inserting merchant details
            if(functions.isValueNull(merchantId))
            {

               //FileDetailsVO fileDetailsVO=ApplicationManager.getfileDetailsVO(merchantId);
            }

            //response

           //request.setAttribute("filedetailsvOs",fileDetailsVOList);
            rdSuccess.forward(request,response);

        }
        catch (Exception e)
        {
            logger.error("Main class exception::", e);
        }
        //Test End








    }
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {

    }
    private ValidationErrorList validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.TOID);
        ValidationErrorList validationErrorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListMandatory,validationErrorList,true);
        return validationErrorList;
    }

}
