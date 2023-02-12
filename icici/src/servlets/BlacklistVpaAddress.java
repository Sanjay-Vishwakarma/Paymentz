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
 * Created by Diksha on 01-Oct-20.
 */
public class BlacklistVpaAddress extends HttpServlet
{
    private static Logger logger = new Logger(BlacklistVpaAddress.class.getName());
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        logger.debug("Entering in BlacklistVpaAddress");
        HttpSession session = req.getSession();
        Functions functions=new Functions();
        StringBuilder sErrorMessage = new StringBuilder();
        StringBuilder sSuccessMessage = new StringBuilder();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        WhiteListDAO whiteListDAO=new WhiteListDAO();
        PaginationVO paginationVO = new PaginationVO();
        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        List<BlacklistVO> listVO = new ArrayList<BlacklistVO>();
        BlacklistManager blacklistManager =new BlacklistManager();
        BlacklistDAO blacklistDAO = new BlacklistDAO();
        String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String action=req.getParameter("unblock");
        String vpaAddress ="";
        vpaAddress=req.getParameter("vpaAddress");
        String reason = req.getParameter("reason");
        //String remark = "Blocked Successfully.";
        String actionExecutorName=role+"-"+username;
        String error = "";
        String sMessage = "";
        int count=0;
        String EOL="<BR>";
        paginationVO.setPageNo(Functions.convertStringtoInt(req.getParameter("SPageno"), 1));
        paginationVO.setRecordsPerPage(Functions.convertStringtoInt(req.getParameter("SRecords"), 15));

        try
        {

            logger.debug(" VPA Address------>"+vpaAddress);
            error = error + validateOptionalParameters(req);
            if(error!=null && !error.equals(""))
            {
                req.setAttribute("error",error);
                RequestDispatcher rd = req.getRequestDispatcher("/blacklistVpaAddress.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            if("search".equalsIgnoreCase(req.getParameter("sbtn")))
            {
                logger.debug("inside search VPA Address------>"+vpaAddress);
                //error = error+validateParameters(req,"search");
                if (!ESAPI.validator().isValidInput("vpaAddress", vpaAddress, "VPAAddress", 50, true))
                {
                    logger.debug("Invalid VPA Address");
                    sErrorMessage.append("Invalid VPA Address and VPA Address should not be empty." + EOL);
                }
                if (sErrorMessage.length() > 0)
                {
                    req.setAttribute("sErrorMessage", sErrorMessage.toString());
                    RequestDispatcher rd = req.getRequestDispatcher("/blacklistVpaAddress.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }

                logger.error(" inside search");
                if(error!=null && !error.equals(""))
                {
                    req.setAttribute("error",error);
                    RequestDispatcher rd = req.getRequestDispatcher("/blacklistVpaAddress.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }
                else if (functions.isValueNull(req.getParameter("vpaAddress")))
                {
                    listVO = blacklistManager.getBlackListedVpaAddress(req.getParameter("vpaAddress"),reason, paginationVO);
                }
                else
                {
                    listVO = blacklistManager.getBlackListedVpaAddress(req.getParameter("vpaAddress"),reason, paginationVO);
                }
            }

            if("block".equalsIgnoreCase(req.getParameter("bbtn")))
            {    logger.debug("Inside block VPA Address------>"+vpaAddress);
                if (!ESAPI.validator().isValidInput("vpaAddress", vpaAddress, "VPAAddress", 50, false))
                {
                    logger.debug("Invalid VPA Address");
                    sErrorMessage.append("Invalid VPA Address and VPA Address should not be empty." + EOL);
                }
                if (!ESAPI.validator().isValidInput("reason", reason, "Description", 100, false))
                {
                    sErrorMessage.append("Invalid Blacklist Reason or Blacklist Reason should not be empty." + EOL);
                }
                if (sErrorMessage.length() > 0)
                {
                    req.setAttribute("sErrorMessage", sErrorMessage.toString());
                    RequestDispatcher rd = req.getRequestDispatcher("/blacklistVpaAddress.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }
                if (!functions.isValueNull(sErrorMessage.toString()))
                {
                    if ("block".equalsIgnoreCase(req.getParameter("bbtn")) && !req.getParameter("vpaAddress").equals("") && !req.getParameter("reason").equals(""))
                    {
                        //error = error+validateParameters(req,"block");
                        if(error!=null && !error.equals(""))
                        {
                            req.setAttribute("error",error);
                            RequestDispatcher rd = req.getRequestDispatcher("/blacklistVpaAddress.jsp?ctoken=" + user.getCSRFToken());
                            rd.forward(req, res);
                            return;
                        }
                        else
                        {
                            count = blacklistManager.insertBlockedVpaAddress(vpaAddress, req.getParameter("reason"), actionExecutorId, actionExecutorName);
                            logger.error("count----- "+count);
                            if (count != 1)
                            {
                                sMessage = req.getParameter("vpaAddress") + " is already Blocked.";
                            }
                            else
                            {
                                sMessage = req.getParameter("vpaAddress") + " is Blocked Successfully.";
                            }
                        }
                        listVO = blacklistManager.getBlackListedVpaAddress(req.getParameter("vpaAddress"),req.getParameter("reason"), paginationVO);
                    }
                }
            }


            paginationVO.setPage(BlacklistVpaAddress.class.getName());
            paginationVO.setInputs("&vpaAddress=" + vpaAddress);
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(req.getParameter("currentblock"), 1));

            if("unblock".equalsIgnoreCase(action) && error.equals(""))
            {
               // System.out.println("inside unblock");
                String[] ids=req.getParameterValues("id");
                //System.out.println("ids--"+ids);
                for(String id : ids)
                {
                    blacklistManager.unblockVpaAddress(id);
                }
                listVO = blacklistManager.getBlackListedVpaAddress(vpaAddress,reason, paginationVO);
                sMessage =" Records Unblocked Successfully.";
            }
            listVO = blacklistManager.getBlackListedVpaAddress(vpaAddress,reason, paginationVO);

        }
        catch (PZDBViolationException e)
        {
            logger.debug("db exception---"+e);
            PZExceptionHandler.handleDBCVEException(e, "", "Inserting Blacklisted CustomerId");
        }

        StringBuilder message = new StringBuilder();
        message.append(sSuccessMessage.toString());
        message.append("<BR/>");
        message.append(sErrorMessage.toString());

        req.setAttribute("paginationVO", paginationVO);
        req.setAttribute("error",error);
        req.setAttribute("msg",sMessage);
        req.setAttribute("listofVpaAddres",listVO);
        req.setAttribute("count",count);
        RequestDispatcher rd = req.getRequestDispatcher("/blacklistVpaAddress.jsp?ctoken=" + user.getCSRFToken());
        rd.forward(req, res);


    }


    private String validateParameters(HttpServletRequest req,String action)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        //String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.VPA_ADDRESS);

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
