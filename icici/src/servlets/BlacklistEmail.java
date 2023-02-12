import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemAccessLogger;
import com.manager.BlacklistManager;
import com.manager.vo.BlacklistVO;

import com.manager.vo.PaginationVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
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

import java.util.*;


/**
 * Created by 123 on 5/10/2015.
 */
public class BlacklistEmail extends HttpServlet
{
    private static Logger logger = new Logger(BlacklistEmail.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException
    {

        logger.debug("Entering in BlockedEmailList");

        HttpSession session = request.getSession();

        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        BlacklistManager blacklistManager = new BlacklistManager();
        String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;
        String reason= request.getParameter("reason");
        String remark=request.getParameter("remark");
        String error = "";
        String sMessage = "";
        String email = "";
        email = request.getParameter("email");
        List<BlacklistVO> listOfEmail = null;
        PaginationVO paginationVO = new PaginationVO();
        int count = 0;
        paginationVO.setPageNo(Functions.convertStringtoInt(request.getParameter("SPageno"), 1));
        paginationVO.setRecordsPerPage(Functions.convertStringtoInt(request.getParameter("SRecords"), 15));
        try
        {
            error = error + validateOptionalParameters(request);
            if("search".equalsIgnoreCase(request.getParameter("sbtn")))
            {
                error = error+validateParameters(request,"search");
                if(error!=null && !error.equals(""))
                {
                    request.setAttribute("error",error);
                    RequestDispatcher rd = request.getRequestDispatcher("/blacklistEmail.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(request, response);
                }
                else
                {
                    listOfEmail = blacklistManager.getBlockedEmail(request.getParameter("email"),reason,remark, paginationVO);
                }
            }

            if("block".equalsIgnoreCase(request.getParameter("bbtn")))
            {
                error = error+validateParameters(request,"block");
                if(error!=null && !error.equals(""))
                {
                    request.setAttribute("error",error);
                    RequestDispatcher rd = request.getRequestDispatcher("/blacklistEmail.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(request, response);
                }
                else
                {
                    count = blacklistManager.insertBlockedEmailAddress(email, reason,remark,actionExecutorId,actionExecutorName);
                    if (count != 1)
                    {
                        sMessage = email + " is already Blocked";
                    }
                    else
                    {
                        sMessage = email + " is Blocked Successfully";
                    }
                    listOfEmail = blacklistManager.getBlockedEmail(request.getParameter("email"),reason,remark, paginationVO);
                }
            }
            paginationVO.setPage(BlacklistEmail.class.getName());
            paginationVO.setInputs("&email=" + email);
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(request.getParameter("currentblock"), 1));
            if("unblock".equalsIgnoreCase(request.getParameter("unblock")) && error.equals(""))
            {
                    String[] ids=request.getParameterValues("id");
                    for(String id : ids)
                    {
                        blacklistManager.unblockEmailAddress(id);
                    }
                    listOfEmail = blacklistManager.getBlockedEmail(email,reason,remark,paginationVO);
                    sMessage = " Emails Unblocked Successfully";
            }
            listOfEmail = blacklistManager.getBlockedEmail(email,reason,remark,paginationVO);
        }
        catch (PZDBViolationException dbe)
        {
            logger.error("exception in BlacklistEmail---",dbe);
            PZExceptionHandler.handleDBCVEException(dbe, "", "Blacklist Email");
        }

        request.setAttribute("paginationVO", paginationVO);
        request.setAttribute("error",error);
        request.setAttribute("msg",sMessage);
        request.setAttribute("listofemail",listOfEmail);
        request.setAttribute("count",count);
        RequestDispatcher rd = request.getRequestDispatcher("/blacklistEmail.jsp?ctoken=" + user.getCSRFToken());
        rd.forward(request, response);
    }

    private String validateParameters(HttpServletRequest req,String action)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        //String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.EMAIL);

        ValidationErrorList errorList = new ValidationErrorList();
        if("search".equalsIgnoreCase(action))
        {
            inputValidator.InputValidations(req, inputFieldsListOptional, errorList, true);
        }
        if("block".equalsIgnoreCase(action) || "unblock".equalsIgnoreCase(action))
        {
            inputValidator.InputValidations(req, inputFieldsListOptional, errorList, false);
        }

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage();
                }
            }
        }
        return error;
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
        inputValidator.InputValidations(req, inputFieldsListOptional, errorList, true);

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
