import com.directi.pg.*;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by admin on 28-11-2017.
 */

public class VerifyMail extends HttpServlet
{
    private static Logger log=new Logger(VerifyMail.class.getName());
    private static TransactionLogger transactionLogger=new TransactionLogger(VerifyMail.class.getName());



    public void doGet(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
    {

        doPost(req,res);

    }
    public void doPost(HttpServletRequest req,HttpServletResponse res)throws ServletException, IOException
    {
        //System.out.println("inside verify mail---->");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("Anonymous");

        String errormsg="";
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        String memberid= req.getParameter("mem");
        String etoken= req.getParameter("et");
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        String loginName="";
        String partnerid="";
        String companyname="";
        String partnername="";
        String logoname="";
        String defaultheme="";
        String currenttheme="";
        String icon="";
        String hostUrl = "";
        RequestDispatcher rdsuccesss = req.getRequestDispatcher("/verifyMail.jsp?action=S");
        RequestDispatcher rdfailed=req.getRequestDispatcher("/verifyMail.jsp?action=E");

        MerchantDAO merchantDAO = new MerchantDAO();
        try
        {
            merchantDetailsVO=   merchantDAO.getMemberDetails(memberid);
            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
            loginName = commonValidatorVO.getMerchantDetailsVO().getLogin();
            partnerid = commonValidatorVO.getMerchantDetailsVO().getPartnerId();
            companyname=commonValidatorVO.getMerchantDetailsVO().getCompany_name();
            partnername=commonValidatorVO.getMerchantDetailsVO().getPartnerName();
            logoname=commonValidatorVO.getMerchantDetailsVO().getLogoName();
            defaultheme=commonValidatorVO.getMerchantDetailsVO().getDefaultTheme();
            currenttheme=commonValidatorVO.getMerchantDetailsVO().getCurrentTheme();
            icon=commonValidatorVO.getMerchantDetailsVO().getIcon();
            hostUrl = merchantDetailsVO.getHostUrl();
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException in VerifyMail ::::",e);
        }

        String isemailverify =emailverified(loginName);
        //System.out.println("isemailverify---"+isemailverify);

        if (isemailverify.equals("N"))
        {

            if (etoken != null)
                if (etoken.equals(etoken(loginName)))
                {
                    updateisemail(loginName);
                    session.setAttribute("login", loginName);
                    session.setAttribute("company_name", companyname);
                    session.setAttribute("partnerid", partnerid);
                    session.setAttribute("partnerName", partnername);
                    session.setAttribute("logoName", logoname);
                    session.setAttribute("default_theme", defaultheme);
                    session.setAttribute("current_theme", currenttheme);
                    session.setAttribute("icon", icon);
                    session.setAttribute("hosturl", hostUrl);
                    rdsuccesss.forward(req, res);

                }
        }
        else
        {
            session.setAttribute("login", loginName);
            session.setAttribute("company_name", companyname);
            session.setAttribute("partnerid", partnerid);
            session.setAttribute("partnerName", partnername);
            session.setAttribute("icon", icon);
            session.setAttribute("logoName", logoname);
            session.setAttribute("default_theme", defaultheme);
            session.setAttribute("current_theme", currenttheme);
            session.setAttribute("hosturl", hostUrl);
            rdfailed.forward(req, res);
        }


    }

    public void updateisemail (String login)
    {
        boolean flag = false;
        String query = "UPDATE members SET isemailverified='Y' WHERE login=?";
        Connection conn = null;
        PreparedStatement ps = null;
        try
        {
            conn = Database.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1,login);
            ps.executeUpdate();

            //System.out.println("query of email flag----"+ps);

        }

        catch (Exception e)
        {
            log.error("Error occured while updating ismail flag", e);
        }
        finally
        {
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }

    }
    public String emailverified(String login)
    {
        Connection conn = null;
        String emailverify="";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getConnection();

            transactionLogger.debug("check isMember method");
            String selquery = "SELECT isemailverified FROM members WHERE login =? ";

            pstmt= conn.prepareStatement(selquery);
            pstmt.setString(1, login);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                emailverify=rs.getString("isemailverified");

            }

        }
        catch (SystemError se)
        {
            transactionLogger.error(" SystemError in isMember method: ",se);

            //throw new SystemError("Error: " + se.getMessage());
        }
        catch (SQLException e)
        {
            transactionLogger.error("Exception in isMember method: ",e);
            //throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return emailverify;

    }

    public String etoken(String login)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String etoken="";
        try
        {
            conn = Database.getConnection();

            transactionLogger.debug("check isMember method");
            String selquery = "SELECT emailtoken FROM members WHERE login =? ";

            pstmt= conn.prepareStatement(selquery);
            pstmt.setString(1, login);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                etoken=rs.getString("emailtoken");

            }

        }
        catch (SystemError se)
        {
            transactionLogger.error(" SystemError in isMember method: ",se);

            //throw new SystemError("Error: " + se.getMessage());
        }
        catch (SQLException e)
        {
            transactionLogger.error("Exception in isMember method: ",e);
            //throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return etoken;

    }
}
