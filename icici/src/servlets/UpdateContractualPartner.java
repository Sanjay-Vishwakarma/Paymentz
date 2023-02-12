import com.directi.pg.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Swamy on 17/5/2017.
 */
public class UpdateContractualPartner extends HttpServlet
{
    private static Logger logger = new Logger(UpdateContractualPartner.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        logger.debug("Inside UpdateContractualPartner.java");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        Functions functions = new Functions();
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        String error = "";
        String status = "";

        String partnerId = request.getParameter("partnerid");
        String bankName = request.getParameter("bankname");
        String action = request.getParameter("action");
        String contractualPartnerId = "";
        String contractualPartnerName = "";

        RequestDispatcher rd =null;
        try
        {
            conn= Database.getConnection();
            HashMap rsDetails=null;
            if(action.equalsIgnoreCase("modify"))
            {
                rsDetails=new HashMap();
                StringBuffer qry=new StringBuffer("SELECT partnerid, bankname, contractual_partnerid, contractual_partnername FROM contractualpartner_appmanager WHERE partnerid=? AND bankname=?");
                pstmt=conn.prepareStatement(qry.toString());
                pstmt.setString(1,partnerId);
                pstmt.setString(2,bankName);
                rs=pstmt.executeQuery();
                if(rs.next())
                {
                    rsDetails.put("partnerid",rs.getString("partnerid"));
                    rsDetails.put("bankname",rs.getString("bankname"));
                    rsDetails.put("contractual_partnerid",rs.getString("contractual_partnerid"));
                    rsDetails.put("contractual_partnername",rs.getString("contractual_partnername"));
                }
                rsDetails.put("partnerid",partnerId);
                rsDetails.put("bankname",bankName);
                request.setAttribute("contractualDetails",rsDetails);
            }
            if(action.equals("update"))
            {
                partnerId = request.getParameter("partnerid");
                bankName = request.getParameter("bankname");
                contractualPartnerId = request.getParameter("contractualpartid");
                contractualPartnerName = request.getParameter("contractualpartname");
                if (functions.hasHTMLTags(contractualPartnerId))
                {
                    error = error + "Invalid Contractual Partner ID";
                }
                if (functions.hasHTMLTags(contractualPartnerName))
                {
                    error = error + "Invalid Contractual Partner Name";
                }

                error = error + validateMandatoryParameters(request);

                if(functions.isValueNull(error))
                {
                    rsDetails=new HashMap();
                    rsDetails.put("partnerid",partnerId);
                    rsDetails.put("bankname",bankName);
                    rsDetails.put("contractual_partnerid",contractualPartnerId);
                    rsDetails.put("contractual_partnername",contractualPartnerName);

                    request.setAttribute("error", error);
                    request.setAttribute("contractualDetails",rsDetails);
                    rd = request.getRequestDispatcher("/updateContractualPartner.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(request,response);
                    return;
                }

                status = updateContractualPartner(partnerId, bankName, contractualPartnerId, contractualPartnerName);
                request.setAttribute("statusMsg", status);
            }
        }
        catch (Exception e)
        {
            logger.error("Exception in UpdateContractualPartner.java------"+ e.getMessage());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }

        rd = request.getRequestDispatcher("/updateContractualPartner.jsp?ctoken="+user.getCSRFToken());
        rd.forward(request,response);
        return;
    }

    private String validateMandatoryParameters(HttpServletRequest request)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.CONTRACTUAL_PARTNER_ID);
        inputFieldsListOptional.add(InputFields.CONTRACTUAL_PARTNER_NAME);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(request,inputFieldsListOptional,errorList,false);

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

    private String updateContractualPartner(String partnerId, String bankName, String contractualPartnerId, String contractualPartnerName) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstm = null;
        String status = "";
        String query = "";

        try
        {
            con = Database.getConnection();
            query = "update contractualpartner_appmanager set contractual_partnerid=?, contractual_partnername=? where partnerid=? and bankname=?";
            pstm=con.prepareStatement(query);
            pstm.setString(1,contractualPartnerId);
            pstm.setString(2,contractualPartnerName);
            pstm.setString(3,partnerId);
            pstm.setString(4,bankName);
            logger.debug("Query---->"+pstm);
            int k = pstm.executeUpdate();
            if(k>0)
            {
                status = "Successfully Updated";
            }
            else
            {
                status = "Updation Failed";
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("UpdateContractualPartner.java", "updateContractualPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("UpdateContractualPartner.java", "updateContractualPartner()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstm);
            Database.closeConnection(con);
        }
        return status;
    }
}
