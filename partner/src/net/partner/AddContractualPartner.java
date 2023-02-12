package net.partner;

import com.directi.pg.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import org.apache.commons.lang.StringUtils;
import org.owasp.esapi.ESAPI;
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
import java.util.ResourceBundle;

/**
 * Created by Admin on 27/4/2017.
 */
public class AddContractualPartner extends HttpServlet
{
    private static Logger logger = new Logger(AddContractualPartner.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request,response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        logger.debug("Inside AddContractualPartner.java");

        HttpSession session = request.getSession();

        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        RequestDispatcher rdError=request.getRequestDispatcher("/addContractualPartner.jsp?MES=ERR&ctoken=" + user.getCSRFToken());
        RequestDispatcher rdSuccess= request.getRequestDispatcher("/addContractualPartner.jsp?Success=YES&ctoken="+user.getCSRFToken());

        Functions functions = new Functions();
        ResourceBundle rb1 = null;
        String language_property1 = (String) session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String AddContractualPartner_PartnerID_errormsg = StringUtils.isNotEmpty(rb1.getString("AddContractualPartner_PartnerID_errormsg")) ? rb1.getString("AddContractualPartner_PartnerID_errormsg") : "Invalid Partner ID.";
        String AddContractualPartner_mapping_errormsg = StringUtils.isNotEmpty(rb1.getString("AddContractualPartner_mapping_errormsg")) ? rb1.getString("AddContractualPartner_mapping_errormsg") : "Invalid Partner Mapping.";
        String AddContractualPartner_mandatory_errormsg = StringUtils.isNotEmpty(rb1.getString("AddContractualPartner_mandatory_errormsg")) ? rb1.getString("AddContractualPartner_mandatory_errormsg") : "Enter Mandatory Parameters.";
        String AddContractualPartner_already_errormsg = StringUtils.isNotEmpty(rb1.getString("AddContractualPartner_already_errormsg")) ? rb1.getString("AddContractualPartner_already_errormsg") : "Mapping Already Available.";


         String partnerId="";
         String partnerid = (String) session.getAttribute("merchantid");
        String pid = request.getParameter("pid");
        if (functions.isValueNull(pid))
        {
            partnerId = pid;
        }
        else
        {
            partnerId = partnerid;
        }
        String errorMsg="";
        String bankName = request.getParameter("bankName");
        String contractualPartnerId = request.getParameter("contractualpartid");
        String contractualPartnerName = request.getParameter("contractualpartname");


        if (!ESAPI.validator().isValidInput("pid", request.getParameter("pid"), "Numbers", 20, false))
        {
            errorMsg =AddContractualPartner_PartnerID_errormsg ;
            request.setAttribute("error", errorMsg);
            rdError.forward(request, response);
            return;
        }

        if (!ESAPI.validator().isValidInput("contractualPartnerId", contractualPartnerId, "Numbers", 20, false))
        {
            errorMsg ="Invalid Contractual Partner ID" ;
            request.setAttribute("error", errorMsg);
            rdError.forward(request, response);
            return;
        }

        if (!ESAPI.validator().isValidInput("contractualPartnerName", contractualPartnerName, "Description", 100, false))
        {
            errorMsg ="Invalid Contractual Partner Name" ;
            request.setAttribute("error", errorMsg);
            rdError.forward(request, response);
            return;
        }


        try
        {
            if (functions.isValueNull(request.getParameter("pid")) && !partner.isPartnerSuperpartnerMapped(request.getParameter("pid"), partnerid))
            {
                errorMsg =AddContractualPartner_mapping_errormsg ;
                request.setAttribute("error", errorMsg);
                rdError.forward(request, response);
                return;
            }
        }
        catch (Exception e)
        {
            logger.error("Exception---"+e);
        }


        logger.debug("partnerId------"+partnerId+"----bankName----"+bankName+"----contractualPartnerId-----"+contractualPartnerId+"-----contractualPartnerName-----"+contractualPartnerName);

        String status = "";

        try
        {
            if (isMappingUnique(partnerId, bankName))
            {
                if (functions.isValueNull(contractualPartnerId) && functions.isValueNull(contractualPartnerName) && functions.isValueNull(bankName))
                {
                    status = addContractualPartner(partnerId, bankName, contractualPartnerId, contractualPartnerName);
                    request.setAttribute("statusMsg", status);
                }
                else
                {
                    request.setAttribute("status",AddContractualPartner_mandatory_errormsg );
                }
            }
            else
            {
                request.setAttribute("status",AddContractualPartner_already_errormsg );
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZDBViolationException in AddContractualPartner.java------", e);
            PZExceptionHandler.handleDBCVEException(e, "", "");
        }
        catch (Exception e)
        {
            logger.error("Exception in AddContractualPartner.java------", e);
            request.setAttribute("message","Internal Error occurred : Please contact your Admin");
        }

        rdSuccess.forward(request,response);
        return;
    }

    public boolean isMappingUnique(String partnerid,String bankname)throws Exception
    {
        Connection con=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        boolean status=true;
        try
        {
            con= Database.getConnection();
            StringBuffer sb=new StringBuffer("select contractualid from contractualpartner_appmanager where partnerid=? and bankname=?");
            pstmt=con.prepareStatement(sb.toString());
            pstmt.setString(1,partnerid);
            pstmt.setString(2,bankname);
            rs=pstmt.executeQuery();
            if(rs.next())
            {
                status=false;
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return status;
    }

    private String addContractualPartner(String partnerId, String bankName, String contractualPartnerId, String contractualPartnerName) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstm = null;
        String status = "";
        String query = "";

        try
        {
            con = Database.getConnection();
            query = "insert into contractualpartner_appmanager(partnerid,bankname,contractual_partnerid,contractual_partnername)values(?,?,?,?)";
            pstm=con.prepareStatement(query);
            pstm.setString(1,partnerId);
            pstm.setString(2,bankName);
            pstm.setString(3,contractualPartnerId);
            pstm.setString(4,contractualPartnerName);
            int k = pstm.executeUpdate();
            if(k>0)
            {
                status = "Successfully Added";
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("AddContractualPartner.java", "addContractualPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("AddContractualPartner.java", "addContractualPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstm);
            Database.closeConnection(con);
        }

        return status;
    }

}
