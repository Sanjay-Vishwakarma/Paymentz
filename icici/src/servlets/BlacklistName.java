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
import org.owasp.esapi.ESAPI;
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

import java.util.*;

/**
 * Created by 123 on 5/10/2015.
 */
public class BlacklistName extends HttpServlet
{
    private static Logger logger = new Logger(BlacklistName.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException
    {

        logger.debug("Entering in BlockedNameList");

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
        String error = "";
        String sMessage = "";
        String name = "";
        name = request.getParameter("name");
        String reason= request.getParameter("reason");
        String remark= request.getParameter("remark");
        List<BlacklistVO> listOfName = null;
        int count = 0;
        PaginationVO paginationVO = new PaginationVO();
        Functions functions=new Functions();
        if (!ESAPI.validator().isValidInput(name, name, "companyName", 20, true))
        {
             request.setAttribute("error","Invalid First Name & Last Name");
              RequestDispatcher rd = request.getRequestDispatcher("/blacklistName.jsp?ctoken=" + user.getCSRFToken());
             rd.forward(request, response);
        }else
        {
            paginationVO.setPageNo(Functions.convertStringtoInt(request.getParameter("SPageno"), 1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(request.getParameter("SRecords"), 15));
            try
            {
                error = error + validateOptionalParameters(request);

                if ("search".equalsIgnoreCase(request.getParameter("sbtn")))
                {
                    listOfName = blacklistManager.getBlockedName(request.getParameter("name"),reason,remark, paginationVO);
                }

                if ("block".equalsIgnoreCase(request.getParameter("bbtn")) && !request.getParameter("name").equals(""))
                {
                    name = request.getParameter("name");

                    count = blacklistManager.insertBlockedName(name, reason,remark,actionExecutorId, actionExecutorName);
                    if (count != 1)
                    {
                        sMessage = name + " is already Blocked";
                    }
                    else
                    {
                        sMessage = name + " is Blocked Successfully";
                    }

                    listOfName = blacklistManager.getBlockedName(name, reason,remark,paginationVO);
                }
            /*else
            {
                error = error+"Please valid name to block:::";
            }*/
                if (!functions.isValueNull(name))
                {
                    name = "";
                }
                paginationVO.setPage(BlacklistName.class.getName());
                paginationVO.setInputs("&name=" + name);
                paginationVO.setCurrentBlock(Functions.convertStringtoInt(request.getParameter("currentblock"), 1));
                if ("unblock".equalsIgnoreCase(request.getParameter("unblock")) && error.equals(""))
                {
                    String[] ids = request.getParameterValues("id");
                    for (String id : ids)
                    {
                        blacklistManager.unblockName(id);
                    }
                    listOfName = blacklistManager.getBlockedName(name,reason,remark, paginationVO);
                    sMessage = "Names Unblocked Successfully";
                }
                listOfName = blacklistManager.getBlockedName(name,reason,remark, paginationVO);

            }
            catch (PZDBViolationException dbe)
            {
                logger.error("exception in BlacklistName---", dbe);
                PZExceptionHandler.handleDBCVEException(dbe, "", "Blacklist Name");
            }
            request.setAttribute("paginationVO", paginationVO);
            request.setAttribute("error", error);
            request.setAttribute("msg", sMessage);
            request.setAttribute("listofname", listOfName);
            request.setAttribute("count", count);
            RequestDispatcher rd = request.getRequestDispatcher("/blacklistName.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
        }
    }

    private String validateParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        //String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.NAME_SMALL);
        inputFieldsListOptional.add(InputFields.REASON);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req, inputFieldsListOptional, errorList, true);

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
