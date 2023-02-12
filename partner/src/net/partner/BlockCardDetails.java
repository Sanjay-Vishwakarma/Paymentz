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

/**
 * Created by Namrata Bari on 6/11/2020.
 */
public class BlockCardDetails extends HttpServlet
{
    private static Logger log = new Logger(BlockCardDetails.class.getName());

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
        String firstSix = req.getParameter("firstsix");
        String lastFour = req.getParameter("lastfour");
        String Reason = req.getParameter("reason");
        String remark = req.getParameter("remark");

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
                  RequestDispatcher rd = req.getRequestDispatcher("/blockCard.jsp?ctoken=" + user.getCSRFToken());
                 rd.forward(req, res);
                return;
            }


            paginationVO.setPageNo(Functions.convertStringtoInt(req.getParameter("SPageno"), 1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(req.getParameter("SRecords"), 15));
            paginationVO.setPage(BlockCardDetails.class.getName());
            paginationVO.setInputs("&firstsix=" + firstSix + "&lastfour=" + lastFour +"&remark="+ remark);

            paginationVO.setCurrentBlock(Functions.convertStringtoInt(req.getParameter("currentblock"), 1));

            listOfCards = blacklistDAO.getBlackListedCardsPage(firstSix, lastFour, actionExecutorId, actionExecutorName,remark, paginationVO);

            if ("upload".equalsIgnoreCase(action))
            {
                if (!ESAPI.validator().isValidInput("firstsix", firstSix, "FirstSixcc", 6, false))
                {

                    error = error + "Invalid First Six or FirstSix should not be empty. <BR>";
                }
                if (!ESAPI.validator().isValidInput("lastfour", lastFour, "LastFourcc", 4, false))
                {

                    error =error +  "Invalid Last Four or LastFour should not be empty. <BR>";
                }
                if (!ESAPI.validator().isValidInput(Reason, Reason, "Description", 100, false))
                {
                    error =error +  "Invalid Blacklist Reason or Blacklist Reason should not be empty. <BR>";
                }



                if(function.isValueNull(error)){
                    req.setAttribute("error", error);
                    RequestDispatcher rd = req.getRequestDispatcher("/blockCard.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }
                String statusduplicate = iscardblacklisted(firstSix,lastFour);
                if(statusduplicate.equals("true")){
                    error =   "Card is already blacklisted.";
                }
              else
                {
                    String status = blacklistcard(firstSix, lastFour, actionExecutorId, actionExecutorName, Reason,remark);
                    if (status.equals("true"))
                    {

                        message = "Card Blacklisted Successfully";
                    }
                    else if (status.contains("Duplicate"))
                    {
                        error = "Card is already blacklisted";

                    }
                    else
                    {
                        error = status;
                    }
                }
                 listOfCards = blacklistDAO.getBlackListedCardsPage(firstSix, lastFour, actionExecutorId, actionExecutorName,remark, paginationVO);
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

        RequestDispatcher rd = req.getRequestDispatcher("/blockCard.jsp?ctoken=" + user.getCSRFToken());
        rd.forward(req, res);
        return;
    }

    public String blacklistcard(String firstSix, String lastFour, String actionExecutorId, String actionExecutorName, String reason,String remark)
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        String status="false";
        try
        {
            connection = Database.getConnection();
            String update = "INSERT INTO blacklist_cards (first_six,last_four,reason,actionExecutorId,actionExecutorName,remark) VALUES (?,?,?,?,?,?)";
            PreparedStatement pstmt = connection.prepareStatement(update);
            pstmt.setString(1, firstSix);
            pstmt.setString(2, lastFour);
            pstmt.setString(3, reason);
            pstmt.setString(4, actionExecutorId);
            pstmt.setString(5, actionExecutorName);
            pstmt.setString(6, remark);

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

    public String iscardblacklisted(String firstSix, String lastFour)
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        String status="false";
        try
        {
            connection = Database.getConnection();
            String update = "select 'X' FROM blacklist_cards WHERE first_six ='"+firstSix+"' AND last_four='"+lastFour+"'";
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
