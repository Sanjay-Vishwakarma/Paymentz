package servlets;

import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.BlacklistManager;
import com.manager.dao.BlacklistDAO;
import com.manager.dao.WhiteListDAO;
import com.manager.vo.BlacklistVO;
import com.manager.vo.PaginationVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
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
 * Created by Kalyani on 01-Oct-20.
 */
public class BlockPhoneNumber extends HttpServlet
{
    private static Logger logger = new Logger(BlockPhoneNumber.class.getName());
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        logger.debug("Entering in BlockPhoneNo");
        HttpSession session = req.getSession();
        Functions functions=new Functions();
        StringBuilder sErrorMessage = new StringBuilder();
        StringBuilder sSuccessMessage = new StringBuilder();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        PaginationVO paginationVO = new PaginationVO();
        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        List<BlacklistVO> listVO = new ArrayList<BlacklistVO>();
        BlacklistManager blacklistManager =new BlacklistManager();
        String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String action=req.getParameter("unblock");
        String phone ="";
        phone=req.getParameter("phone");
        String reason = req.getParameter("reason");
        String actionExecutorName=role+"-"+username;
        String error = "";
        String sMessage = "";
        int count=0;
        String EOL="<BR>";
        paginationVO.setPageNo(Functions.convertStringtoInt(req.getParameter("SPageno"), 1));
        paginationVO.setRecordsPerPage(Functions.convertStringtoInt(req.getParameter("SRecords"), 15));
        System.out.println("reqpage"+req.getParameter("SPageno"));
        if (!functions.isValueNull(phone))
        {
            phone = "";
        }
        paginationVO.setPage(BlockPhoneNumber.class.getName());
        paginationVO.setInputs("&phone=" + phone+"&ctoken=" + user.getCSRFToken());
        paginationVO.setCurrentBlock(Functions.convertStringtoInt(req.getParameter("currentblock"), 1));
        System.out.println("blockphone");
        try
        {

            error = error + validateOptionalParameters(req);
            if(error!=null && !error.equals(""))
            {
                req.setAttribute("error",error);
                RequestDispatcher rd = req.getRequestDispatcher("/blockphone.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            if("search".equalsIgnoreCase(req.getParameter("sbtn")))
            {
                logger.debug("inside search phone------>"+phone);
                //error = error+validateParameters(req,"search");
                if (!ESAPI.validator().isValidInput("phone", phone, "Phone", 24, true))
                {
                    logger.debug("Invalid Phone Number");
                    sErrorMessage.append("Invalid Phone Number and Phone Number should not be empty." + EOL);
                }
                if (sErrorMessage.length() > 0)
                {
                    req.setAttribute("sErrorMessage", sErrorMessage.toString());
                    RequestDispatcher rd = req.getRequestDispatcher("/blockphone.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }

                logger.error(" inside search");
                if(error!=null && !error.equals(""))
                {
                    req.setAttribute("error",error);
                    RequestDispatcher rd = req.getRequestDispatcher("/blockphone.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }
                else if (functions.isValueNull(req.getParameter("phone")))
                {
                    listVO = blacklistManager.getBlackListedPhoneNo(req.getParameter("phone"), reason, paginationVO);
                }
                else
                {
                    listVO = blacklistManager.getBlackListedPhoneNo(req.getParameter("phone"), reason, paginationVO);
                }
            }

            else if("block".equalsIgnoreCase(req.getParameter("bbtn")))
            {
                if (!ESAPI.validator().isValidInput("phone", phone, "Phone", 24, false))
                {
                    sErrorMessage.append("Invalid Phone Number and Phone Number should not be empty." + EOL);
                }
                if (!ESAPI.validator().isValidInput("reason", reason, "Description", 100, false))
                {
                    sErrorMessage.append("Invalid Blacklist Reason or Blacklist Reason should not be empty." + EOL);
                }
                if (sErrorMessage.length() > 0)
                {
                    req.setAttribute("sErrorMessage", sErrorMessage.toString());
                    RequestDispatcher rd = req.getRequestDispatcher("/blockphone.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }
                    if (functions.isValueNull(phone) && functions.isValueNull(reason))
                    {

                            count = blacklistManager.insertBlockedPhone(phone, req.getParameter("reason"), actionExecutorId, actionExecutorName);
                            logger.error("count--"+count);
                            if (count != 1)
                            {
                                sMessage = req.getParameter("phone") + " is already Blocked.";
                            }
                            else
                            {
                                sMessage = req.getParameter("phone") + " is Blocked Successfully.";
                            }

                        listVO = blacklistManager.getBlackListedPhoneNo(req.getParameter("phone"), req.getParameter("reason"), paginationVO);
                    }

            }
            else if("unblock".equalsIgnoreCase(action) && error.equals(""))
            {
                // System.out.println("inside unblock");
                String[] ids=req.getParameterValues("id");
                //System.out.println("ids--"+ids);
                for(String id : ids)
                {
                    blacklistManager.unblockPhoneNo(id);
                }
                listVO = blacklistManager.getBlackListedPhoneNo(phone, reason, paginationVO);
                sMessage =" Records Unblocked Successfully.";
            }

        }


        catch (PZDBViolationException e)
        {
            logger.error("db exception---",e);
            PZExceptionHandler.handleDBCVEException(e, "", "Internal error occured");
        }


        StringBuilder message = new StringBuilder();
        message.append(sSuccessMessage.toString());
        message.append("<BR/>");
        message.append(sErrorMessage.toString());

        req.setAttribute("paginationVO", paginationVO);
        req.setAttribute("error",error);
        req.setAttribute("msg",sMessage);
        req.setAttribute("listofphone",listVO);
        req.setAttribute("count",count);
        RequestDispatcher rd = req.getRequestDispatcher("/blockphone.jsp?ctoken=" + user.getCSRFToken());
        rd.forward(req, res);


    }
    private String validateOptionalParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
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
