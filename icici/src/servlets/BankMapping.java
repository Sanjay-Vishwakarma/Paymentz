import com.directi.pg.*;
import com.manager.ApplicationManager;
import com.manager.vo.PaginationVO;
import com.vo.applicationManagerVOs.BankTypeVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
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
 * Created by Vishal on 6/5/2017.
 */
public class BankMapping extends HttpServlet
{
    public static Logger logger = new Logger(BankMapping.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        HttpSession session = request.getSession();
        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        List<String> errorList = new ArrayList<String>();
        String error = "";
        try
        {
            String bankId = request.getParameter("bankId");
            String bankName = request.getParameter("bankName");
            ApplicationManager applicationManager = new ApplicationManager();

            if (!ESAPI.validator().isValidInput("bankId", bankId, "Numbers", 11, true))
            {
                errorList.add("Invalid Bank ID");
            }
            if (!ESAPI.validator().isValidInput("bankName", bankName, "alphanum", 11, true))
            {
                errorList.add("Invalid Bank Name");
            }

            if (!errorList.isEmpty())
            {
                request.setAttribute("sberror", errorList);
                RequestDispatcher rd = request.getRequestDispatcher("/bankMapping.jsp?MES=ERR&ctoken="+user.getCSRFToken());
                rd.forward(request, response);
                return;
            }

            error = error + validateOptionalParameters(request);
            PaginationVO paginationVO = new PaginationVO();
            try
            {
                paginationVO.setInputs("bankId=" + bankId + "&bankName=" + bankName);
                paginationVO.setPageNo(Functions.convertStringtoInt(request.getParameter("SPageno"), 1));
                paginationVO.setCurrentBlock(Functions.convertStringtoInt(request.getParameter("currentblock"), 1));
                paginationVO.setRecordsPerPage(Functions.convertStringtoInt(request.getParameter("SRecords"), 15));

                List<BankTypeVO> bankTypeVOList = applicationManager.getBankMappingDetails(bankId, bankName, paginationVO);
                request.setAttribute("bankMappingVOList", bankTypeVOList);
                request.setAttribute("paginationVO", paginationVO);
            }
            catch (PZDBViolationException se)
            {
                logger.debug("Internal error while processing qury"+se.getMessage());
                Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
            }
        }
        catch (Exception e)
        {
            logger.debug("Exception"+ e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        RequestDispatcher rd = request.getRequestDispatcher("/bankMapping.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        request.setAttribute("error", error);
        rd.forward(request, response);
        return;
    }

    private String validateOptionalParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields>  inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.PAGENO);
        inputFieldsListOptional.add(InputFields.RECORDS);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional,errorList,true);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }
}
