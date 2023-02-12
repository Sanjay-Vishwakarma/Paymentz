package net.partner;

import com.directi.pg.*;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Namrata on 26-09-2019.
 */

public class VirtualCheckOutList extends HttpServlet
{
    private Logger logger = new Logger(ActionPartnerAccountCharges.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doProcess(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doProcess(request, response);
    }

    public void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        HttpSession session = request.getSession();
        PartnerFunctions partner = new PartnerFunctions();
        Functions functions = new Functions();
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        Connection conn = null;
        PreparedStatement pstmt1 = null, pstmt2 =null ;
        ResultSet rs1 = null, rs2 = null;
        String errorMsg = "";
        String EOL = "<BR>";
        HashMap rsDetails = new HashMap();
        RequestDispatcher rd = request.getRequestDispatcher("/virtualCheckOut.jsp?ctoken="+user.getCSRFToken());
        //String success1 = "";

        String action = request.getParameter("action");
        String memberid=request.getParameter("memberid"); ;
        String v_host_url=null;

        errorMsg = errorMsg + validateParameters(request);

        //To retrive host url of partner;
        try{
            conn = Database.getConnection();
            String qry = "select p.hosturl URL from partners p,members m where p.partnerId = m.partnerId and memberid= ?";
            pstmt1 = conn.prepareStatement(qry);
            pstmt1.setString(1, memberid);
            rs1 = pstmt1.executeQuery();
            while (rs1.next())
            {
                v_host_url = rs1.getString("URL");
            }
        }catch(Exception e){
            errorMsg = e.getMessage();
        }
        finally{
            Database.closeResultSet(rs1);
            Database.closePreparedStatement(pstmt1);
            Database.closeConnection(conn);
        }

        try{
            conn = Database.getConnection();
            String qry = "SELECT DISTINCT memberid,gt.currency CURRENCY FROM member_account_mapping AS m, gateway_accounts \n" +
                    "AS ga,gateway_type AS gt WHERE memberid IN (?) AND ga.accountid=m.accountid AND \n" +
                    "ga.pgtypeid=gt.pgtypeid AND m.isactive='Y'";
            pstmt2 = conn.prepareStatement(qry);
            pstmt2.setString(1, memberid);
            rs2 = pstmt2.executeQuery();
            int i = 1;
            while (rs2.next())
            {
                HashMap record = new HashMap();
                record.put("CURRENCY", rs2.getString("CURRENCY"));
                rsDetails.put(i, record);
                i = i + 1;
            }

        }catch(Exception e){
            errorMsg = e.getMessage();
        }
        finally{
            Database.closeResultSet(rs2);
            Database.closePreparedStatement(pstmt2);
            Database.closeConnection(conn);
        }


        PzEncryptor encrypt = new PzEncryptor();
        String v_encpt_mid = "";
        try
        {
            String URLdecoded = ESAPI.encoder().decodeFromURL(memberid);
            String decoded = URLdecoded.replaceAll(" ", "+");
            v_encpt_mid = encrypt.encryptName(decoded);
        }catch(Exception e){
            logger.error("Exception---" + e);
        }
        request.setAttribute("errormessage", errorMsg);
        request.setAttribute("v_host_url", v_host_url);
        request.setAttribute("encrypt_member", v_encpt_mid);
        request.setAttribute("currencyList", rsDetails);
        rd.forward(request, response);
        return;
    }


    private String validateParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.MEMBERID);


        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req, inputFieldsListOptional, errorList, false);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error.concat(errorList.getError(inputFields.toString()).getMessage());
                }
            }
        }
        return error;
    }

}
