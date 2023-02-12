package net.partner;

import com.directi.pg.*;
import com.manager.dao.BlacklistDAO;
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


public class BlockVPAAddress extends HttpServlet
{
    private static Logger log = new Logger(BlockVPAAddress.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        BlacklistDAO blacklistDAO = new BlacklistDAO();
        BlacklistVO blacklistVO = null;
        List<BlacklistVO> listOfCards = null;
        PaginationVO paginationVO = new PaginationVO();
        Functions function = new Functions();

        String error = "";
        String message ="";
        String vpaAddress = req.getParameter("vpaAddress");
        String reason = req.getParameter("reason");

        String action = req.getParameter("upload");
        String role = (String) session.getAttribute("role");
        String username = (String) session.getAttribute("username");
        String actionExecutorId = (String) session.getAttribute("merchantid");
        String actionExecutorName = role + "-" + username;
        PartnerFunctions partnerFunctions = new PartnerFunctions();
        if (!partnerFunctions.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/logout.jsp");
            return;
        }
        try
        {

            if(function.isValueNull(error)){
                req.setAttribute("error", error);
                RequestDispatcher rd = req.getRequestDispatcher("/blockVPAAddress.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
                return;
            }

            paginationVO.setPageNo(Functions.convertStringtoInt(req.getParameter("SPageno"), 1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(req.getParameter("SRecords"), 15));
            paginationVO.setPage(BlockPhoneNumber.class.getName());
            paginationVO.setInputs("&vpaAddress=" + vpaAddress + "&reason=" + reason);

            paginationVO.setCurrentBlock(Functions.convertStringtoInt(req.getParameter("currentblock"), 1));

            listOfCards = blacklistDAO.getBlacklistedvpaaddress(vpaAddress, reason, paginationVO);

            if ("upload".equalsIgnoreCase(action))
            {
                if (!ESAPI.validator().isValidInput("vpaAddress", vpaAddress, "VPAAddress", 50, false))
                {

                    error = error + "Invalid VPAAddress or VPAAddress should not be empty. <BR>";
                }
                if (!ESAPI.validator().isValidInput("reason", reason, "Description", 100, false))
                {
                    error =error +  "Invalid Blacklist Reason or Blacklist Reason should not be empty. <BR>";
                }

                if(function.isValueNull(error)){
                    req.setAttribute("error", error);
                    RequestDispatcher rd = req.getRequestDispatcher("/blockVPAAddress.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }
                String statusduplicate = isvpablacklisted(vpaAddress,reason);
                if(statusduplicate.equals("true")){
                    error =   vpaAddress+ "  is already blacklisted.";
                }
                else
                {
                    String status = blacklistvpa(vpaAddress, reason, actionExecutorId, actionExecutorName);
                    if (status.equals("true"))
                    {

                        message = vpaAddress+ " is Blocked Successfully";
                    }
                    else if (status.contains("Duplicate"))
                    {
                        error = vpaAddress+" is already Blocked";

                    }
                    else
                    {
                        error = status;
                    }
                }
                listOfCards = blacklistDAO.getBlackListedPhoneNo(vpaAddress, reason,paginationVO);
            }
        }
        catch (PZDBViolationException dbe)
        {
            log.debug("db exception---" + dbe);
            PZExceptionHandler.handleDBCVEException(dbe, "", "Fetching Blacklisted Cards");
        }
        req.setAttribute("paginationVO", paginationVO);
        req.setAttribute("error", error);
        req.setAttribute("message", message);
        req.setAttribute("listofcard", listOfCards);

        RequestDispatcher rd = req.getRequestDispatcher("/blockVPAAddress.jsp?ctoken=" + user.getCSRFToken());
        rd.forward(req, res);
        return;
    }

    public String blacklistvpa(String vpaAddress, String reason, String actionExecutorId, String actionExecutorName)
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        String status="false";
        try
        {
            connection = Database.getConnection();
            String update = "INSERT INTO blacklist_vpaAddress (vpaAddress,reason,actionExecutorId,actionExecutorName) VALUES (?,?,?,?)";
            PreparedStatement pstmt = connection.prepareStatement(update);
            pstmt.setString(1, vpaAddress);
            pstmt.setString(2, reason);
            pstmt.setString(3, actionExecutorId);
            pstmt.setString(4, actionExecutorName);

            int i = pstmt.executeUpdate();
            System.out.println(pstmt);
            if (i>0)
            {
                status ="true";
            }
        }

        catch (Exception e)
        {
            if(e.getMessage().contains("Duplicate")){
                status="Duplicate";
            }else{
                status=e.getMessage();
            }

            log.error("error while updating blacklist cards---", e);
        }

        finally

        {
            Database.closeResultSet(result);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return status;
    }

    public String isvpablacklisted(String vpaAddress, String reason)
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        String status="false";
        try
        {
            connection = Database.getConnection();
            String update = "select 'X' FROM blacklist_vpaAddress WHERE vpaAddress ='"+vpaAddress+"' AND reason='"+reason+"'";
            PreparedStatement pstmt = connection.prepareStatement(update);
            result = pstmt.executeQuery();
            while (result.next()){
                String value = result.getString(1);
                if(value.equals("X")){
                    status ="true";
                }
            }
        }

        catch (Exception e)
        {
            if(e.getMessage().contains("Duplicate")){
                status="Duplicate";
            }else{
                status=e.getMessage();
            }

            log.error("error while updating blacklist cards---", e);
        }

        finally

        {
            Database.closeResultSet(result);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return status;
    }


}

