import com.directi.pg.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 24/4/2017.
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

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        RequestDispatcher rdError=request.getRequestDispatcher("/addContractualPartner.jsp?MES=ERR&ctoken=" + user.getCSRFToken());
        RequestDispatcher rdSuccess= request.getRequestDispatcher("/addContractualPartner.jsp?Success=YES&ctoken="+user.getCSRFToken());

        Functions functions = new Functions();

        String partnerId = request.getParameter("partnerId");
        String bankName = request.getParameter("bankName");
        String contractualPartnerId = request.getParameter("contractualpartid");
        String contractualPartnerName = request.getParameter("contractualpartname");

        logger.debug("partnerId------"+partnerId+"----bankName----"+bankName+"----contractualPartnerId-----"+contractualPartnerId+"-----contractualPartnerName-----"+contractualPartnerName);

        String error = "";
        String status = "";
        StringBuffer errorMsg = new StringBuffer();
        error = error + validateMandatoryParameters(request);
        if (!ESAPI.validator().isValidInput("contractualPartnerId", request.getParameter("contractualPartnerId"), "Numbers", 20, true) || functions.hasHTMLTags(contractualPartnerId))
        {
            error = error + "Invalid Contractual Partner ID" ;
        }
        if (!ESAPI.validator().isValidInput("contractualpartname", request.getParameter("contractualpartname"), "Description", 100, true) || functions.hasHTMLTags(contractualPartnerName))
        {
            error = error + "Invalid Contractual Partner Name";
        }

        if(functions.isValueNull(error))
        {
            request.setAttribute("error",error);
            rdError.forward(request, response);
            return;
        }

        try
        {
            if(isMappingUnique(partnerId,bankName))
            {
                status = addContractualPartner(partnerId, bankName, contractualPartnerId, contractualPartnerName);

                request.setAttribute("status", status);
            }
            else
            {
                request.setAttribute("status","Mapping Already Available");
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZDBViolationException in AddContractualPartner.java------" +e.getMessage());
            PZExceptionHandler.handleDBCVEException(e, "", "");
        }
        catch (Exception e)
        {
            logger.error("Exception in AddContractualPartner.java------"+ e.getMessage());
        }
        request.setAttribute("message","Internal Error occurred : Please contact your Admin");

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
            con=Database.getRDBConnection();
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

    private String validateMandatoryParameters(HttpServletRequest request)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.BANK_NAME);
        inputFieldsListOptional.add(InputFields.PARTNER_ID);
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

}
