package net.partner;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;

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
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Ajit.k on 18/09/2019.
 */
public class PartnerDetailList extends HttpServlet
{
    private static Logger log = new Logger(PartnerDetailList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        PartnerFunctions partner=new PartnerFunctions();
        Functions functions=new Functions();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        int counter = 1;
        int records=30;
        int pageno=1;
        String errorList =null;
        int start = 0; // start index
        int end = 0; // end index

        String superAdminId = null;
        String partnerId = null;
        String partnerName = null;
        String login=null;

        errorList = validateOptionalParameters(req);
        HashMap memberHash = null;

        if(errorList!=null && !errorList.equals(""))
        {
            //redirect to jsp page for invalid data entry
            req.setAttribute("error",errorList);
            RequestDispatcher rd = req.getRequestDispatcher("/partnerlist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        else
        {
            superAdminId=req.getParameter("superAdminId");
            partnerId=req.getParameter("partnerId");
            partnerName=req.getParameter("partnerName");
        }
        try
        {
            pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("SPageno",req.getParameter("SPageno"),"Numbers",5,true), 1);
            records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("SRecords",req.getParameter("SRecords"),"Numbers",5,true), 30);
        }
        catch(ValidationException e)
        {
            log.error("Invalid page no or records",e);
            pageno = 1;
            records = 30;
        }
        try
        {
            start = (pageno - 1) * records;
            end = records;
            conn= Database.getRDBConnection();
            StringBuilder selectPartnerDetail=new StringBuilder("SELECT partnerid,partnerName,superadminid,login FROM partners WHERE superadminid=?");
            StringBuilder countquery = new StringBuilder("select count(*) from partners WHERE superadminid=? ");
            if (functions.isValueNull(partnerId))
            {
                selectPartnerDetail.append(" and partnerId = ?");
                countquery.append(" and partnerId = ?");
            }

            if(functions.isValueNull(partnerName))
            {
                selectPartnerDetail.append(" and partnerName=?");
                countquery.append(" and partnerName=?");
            }
           
            selectPartnerDetail.append(" order by partnerid DESC");
            selectPartnerDetail.append(" LIMIT ? , ?");

            log.debug("selectpartnerDetail query:::::"+selectPartnerDetail.toString());

            pstmt = conn.prepareStatement(selectPartnerDetail.toString());
            pstmt1 = conn.prepareStatement(countquery.toString());

            pstmt.setString(counter, superAdminId);
            pstmt1.setString(counter, superAdminId);
            counter++;

            if (functions.isValueNull(partnerId))
            {
                pstmt.setString(counter, partnerId);
                pstmt1.setString(counter, partnerId);
                counter++;
            }
            if (functions.isValueNull(partnerName))
            {
                pstmt.setString(counter, partnerName);
                pstmt1.setString(counter, partnerName);
                counter++;
            }
            pstmt.setInt(counter, start);
            counter++;
            pstmt.setInt(counter, end);

            log.debug("selectPartnerDetail:::::" + pstmt);
            memberHash = Database.getHashMapFromResultSetForTransactionEntry(pstmt.executeQuery());

            rs = pstmt1.executeQuery();
            rs.next();
            int totalrecords = rs.getInt(1);

            memberHash.put("totalrecords", "" + totalrecords);
            memberHash.put("records", "0");

            if (totalrecords > 0)
            {
                memberHash.put("records", "" + (memberHash.size() - 2));
            }

            req.setAttribute("transdetails", memberHash);
            //redirect to jsp page with records

        }
        catch (SystemError systemError)
        {
            log.error("SystemError while listing member details",systemError);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        catch (SQLException e)
        {
            log.error("Sql exception while listing member details", e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closePreparedStatement(pstmt1);
            Database.closeConnection(conn);
        }
        RequestDispatcher rd = req.getRequestDispatcher("/partnerlist.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);

    }

    private String validateOptionalParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.PARTNER_ID);
        inputFieldsListOptional.add(InputFields.PARTNER_NAME);
        inputFieldsListOptional.add(InputFields.PAGENO);
        inputFieldsListOptional.add(InputFields.RECORDS);
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional, errorList,true);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error.concat(errorList.getError(inputFields.toString()).getMessage()+EOL);
                }
            }
        }
        return error;
    }
}
