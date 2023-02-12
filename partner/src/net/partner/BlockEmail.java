package net.partner;

import com.directi.pg.*;
import com.manager.BlacklistManager;
import com.manager.dao.BlacklistDAO;
import com.manager.vo.BlacklistVO;
import com.manager.vo.PaginationVO;
import com.payment.emexpay.vo.request;
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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by Kalyani on 21/12/2021.
 */
public class BlockEmail extends HttpServlet
{
    private static Logger log = new Logger(BlockEmail.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        PartnerFunctions partnerFunctions=new PartnerFunctions();
        Functions functions=new Functions();
        StringBuilder sErrorMessage=new StringBuilder();
        BlacklistManager blacklistManager=new BlacklistManager();
        PaginationVO paginationVO=new PaginationVO();
        List<BlacklistVO> listOfEmail = null;

        String errormsg = "";
        String error = "";
        String EOL = "<BR>";
         String sMessage="";
        HttpSession session=Functions.getNewSession(request);
        User user= (User)session.getAttribute("ESAPIUserSessionKey");
        RequestDispatcher rd=request.getRequestDispatcher("/blockEmail.jsp?ctoken="+user.getCSRFToken());
        if (!partnerFunctions.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/logout.jsp");
            return;
        }
        if (functions.isValueNull(errormsg))
        {
            request.setAttribute("error", errormsg);
            rd.forward(request, response);
            return;
        }
        try
        {
            InputValidator inputValidator = new InputValidator();
            List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
            inputFieldsListMandatory.add(InputFields.PAGENO);
            inputFieldsListMandatory.add(InputFields.RECORDS);
            inputValidator.InputValidations(request,inputFieldsListMandatory,true);
            if(!error.isEmpty())
            {
                request.setAttribute("error",error);
                rd.forward(request,response);
                return;
            }
        }
        catch (ValidationException e)
        {
            log.error("Enter valid input",e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            request.setAttribute("error", errormsg);
            rd.forward(request, response);
            return;
        }
        String role=(String)session.getAttribute("role");
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;
        String email=request.getParameter("email");
        String reason=request.getParameter("reason");
        String remark=request.getParameter("remark");
        String action = request.getParameter("upload");

        int count = 0;
        int records=15;
        int pageno=1;
        int start = 0;
        int end = 0;
        try
        {
            pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("SPageno",request.getParameter("SPageno"),"Numbers",5,true), 1);
            records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("SRecords",request.getParameter("SRecords"),"Numbers",5,true), 15);
        }
        catch(ValidationException e)
        {
            log.error("Invalid page no or records",e);
            pageno = 1;
            records = 15;
        }
        start = (pageno - 1) * records;
        end = records;

        if(functions.isValueNull(error)){
            request.setAttribute("error",error);
            rd.forward(request, response);
            return;
        }
        try
        {
            if (!ESAPI.validator().isValidInput("email", email, "Email", 50, true))
            {
                sErrorMessage.append("Invalid Email ID or Email ID should not be empty" + EOL);
            }
            if (!ESAPI.validator().isValidInput("reason", reason, "Description", 100, true))
            {
                sErrorMessage.append("Invalid Reason or Reason should not be empty" + EOL);
            }
            if (!ESAPI.validator().isValidInput("remark", remark, "Description", 100, true))
            {
                sErrorMessage.append("Invalid Remark or Remark should not be empty" + EOL);
            }

            if (sErrorMessage.length() > 0)
            {
                request.setAttribute("error", sErrorMessage.toString());
                rd = request.getRequestDispatcher("/blockEmail.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
                return;
            }
            listOfEmail=blacklistManager.getBlockEmail(email, reason, remark, records, pageno, request);
            if ("upload".equalsIgnoreCase(action))
            {
                count = blacklistManager.insertBlockedEmailAddress(email, reason, remark, actionExecutorId, actionExecutorName);
                if (count != 1)
                {
                    errormsg = email + " already exists";
                }
                else
                {
                    sMessage = email + " blocked Successfully";
                }
            }
        }

        catch (PZDBViolationException e)
        {
            log.debug("Exception:::::"+e);
        }
        catch (Exception e)
        {
            log.debug("Exception:::::"+e);
        }
        request.setAttribute("msg",sMessage);
        request.setAttribute("error",errormsg);
        request.setAttribute("listofemail",listOfEmail);
        rd.forward(request, response);
        return;
    }
}


