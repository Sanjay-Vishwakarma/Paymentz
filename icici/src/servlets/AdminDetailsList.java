import com.directi.pg.*;
import com.manager.AdminManager;
import com.manager.vo.AdminDetailsVO;
import com.manager.vo.PaginationVO;

import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 3/9/15
 * Time: 4:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class AdminDetailsList extends HttpServlet
{
    private static Logger logger = new Logger(AdminDetailsList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doProcess(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doProcess(request, response);
    }
    public void doProcess(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        Connection conn = null;
        int records=15;
        int pageno=1;

        int start = 0; // start index
        int end = 0; // end index

        String adminId=req.getParameter("adminid");
        String userName=req.getParameter("username");
        String errormsg = "";
        String EOL = "<BR>";

        String msg="";
        StringBuffer sb=new StringBuffer();
        PaginationVO paginationVO=new PaginationVO();

        RequestDispatcher rd = req.getRequestDispatcher("/adminlist.jsp?ctoken="+user.getCSRFToken());

        if(!ESAPI.validator().isValidInput("adminid", adminId, "OnlyNumber", 10, true))
        {   msg="Invalid Admin ID";
            sb.append(msg+",");
            logger.error(msg);
        }
        if(!ESAPI.validator().isValidInput("username", userName, "alphanum", 100, true))
        {   msg="Invalid Username ";
            logger.error(msg);
            sb.append(msg+",");
        }
        if(sb.length()>0)
        {
            req.setAttribute("message",sb.toString());
            rd.forward(req,res);
            return;
        }
        try
        {
            validateOptionalParameter(req);
        }
        catch (ValidationException e)
        {
            logger.error("Enter valid input",e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            logger.debug("message..." + e.getMessage());
            req.setAttribute("message",errormsg);
            rd = req.getRequestDispatcher("/adminlist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        try
        {
            start = (pageno - 1) * records;
            end = records;

            paginationVO.setPageNo(Functions.convertStringtoInt(req.getParameter("SPageno"),1));
            paginationVO.setPage(AdminDetailsList.class.getName());
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(req.getParameter("currentblock"), 1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(req.getParameter("SRecords"),15));

            AdminDetailsVO adminDetailsInputVO=new AdminDetailsVO();
            adminDetailsInputVO.setAdminId(adminId);
            adminDetailsInputVO.setLogin(userName);

            AdminManager adminManager=new AdminManager();
            List<AdminDetailsVO> adminDetailsVOList=adminManager.getAdminUsersList(adminDetailsInputVO,paginationVO);

            req.setAttribute("adminListVO",adminDetailsVOList);
            req.setAttribute("paginationVO",paginationVO);

        }
        catch (PZDBViolationException dbe)
        {
            logger.error("PZDBViolationException in AdminDetailsList.java------",dbe);
            //sb.append("No Records Found");
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        finally
        {
            Database.closeConnection(conn);
        }
        rd = req.getRequestDispatcher("/adminlist.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
        return;
    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }

}
