package net.partner;

import com.directi.pg.*;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
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
import java.util.*;

/**
 * Created by Ajit on 3/24/2018.
 */
public class PartnerUserBlockedList extends HttpServlet
{
    private Logger logger = new Logger(PartnerUserBlockedList.class.getName());

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        logger.debug("Entering in PartnerUserUnblockedAccount");
        HttpSession session = request.getSession();
        String partnerId=null;
        PartnerFunctions partner=new PartnerFunctions();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        int start = 0; // start index
        int end = 0; // end index

        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }
        if(user.getRoles().contains("superpartner")){
            partnerId=partner.getListOfSubPartner((String) session.getAttribute("merchantid"));
        }else
            partnerId = (String) session.getAttribute("merchantid");

        logger.error("PartnerId::::"+partnerId);

        int records=15;
        int pageno=1;
        response.setContentType("text/html");

        try
        {
            String error = "";
            error = error + validateOptionalParameter(request);
            if(error.length() > 0)
            {
                request.setAttribute("error",error);
                RequestDispatcher rd = request.getRequestDispatcher("/partnerUserUnblockedAccount.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request, response);
            }
        }
        catch (Exception e)
        {
            logger.error("Invalid page no or records",e);
            pageno = 1;
            records = 15;
        }
        pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(request.getParameter("SRecords"), 15);
        // calculating start & end
        start = (pageno - 1) * records;
        end = records;
        HashMap partnerhash = null;

        StringBuilder query = new StringBuilder("SELECT p.partnerId,u.login, pu.userid,u.roles,pu.contact_emails FROM user AS u LEFT JOIN partner_users AS pu ON u.accountid = pu.accid LEFT JOIN partners AS p ON pu.partnerid = p.partnerId WHERE unblocked = 'locked'AND faillogincount= '5' AND p.partnerId IN("+partnerId+") ORDER BY p.partnerId ASC LIMIT ? , ? ");
        StringBuilder countquery = new StringBuilder("SELECT COUNT(*) FROM user AS u LEFT JOIN partner_users AS pu ON u.accountid = pu.accid LEFT JOIN partners AS p ON pu.partnerid = p.partnerId WHERE unblocked = 'locked'AND faillogincount= '5' AND p.partnerId IN("+partnerId+")");
        Connection cn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        Functions functions = new Functions();
        try
        {
            cn = Database.getConnection();
            pstmt = cn.prepareStatement(query.toString());
            pstmt1 = cn.prepareStatement(countquery.toString());
            /*if (functions.isValueNull(partnerid))
            {
                pstmt.setString(1, partnerid);
                pstmt1.setString(1, partnerid);
            }*/
            pstmt.setInt(1, start);
            pstmt.setInt(2, end);
            partnerhash = Database.getHashMapFromResultSet(pstmt.executeQuery());
            logger.error("Query:::::"+pstmt);
            rs = pstmt1.executeQuery();
            rs.next();
            int totalemailrecords = rs.getInt(1);

            partnerhash.put("totalrecords", "" + totalemailrecords);
            partnerhash.put("records", "0");

            if (totalemailrecords > 0)
            {
                partnerhash.put("records", "" + (partnerhash.size() - 2));
            }
            if(request.getAttribute("msg")!=null)
            {
                request.setAttribute("msg",request.getAttribute("msg"));
            }
            request.setAttribute("blockedsubpartner", partnerhash);

            RequestDispatcher rd = request.getRequestDispatcher("/partnerUserUnblockedAccount.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
        }
        catch (SystemError se)
        {
            logger.error("SystemError in BlockedEmailList::::",se);
        }
        catch (Exception e)
        {
            logger.error("Exception in BlockedEmailList",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closePreparedStatement(pstmt1);
            Database.closeConnection(cn);
        }
    }
    private String validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        String error = "";
        String EOL = "<BR>";
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListMandatory,errorList ,true);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListMandatory)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error.concat(errorList.getError(inputFields.toString()).getMessage()+EOL);
                }
            }
        }
        return error;
    }
}
