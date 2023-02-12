import com.directi.pg.*;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Kiran on 24/7/15.
 */
public class ListMerchantRandomCharges extends HttpServlet
{
    static Logger logger = new Logger(ListMerchantRandomCharges.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doProcess(req, res);
    }
    public void doProcess(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        HttpSession session = request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if(!Admin.isLoggedIn(session))
        {
            response.sendRedirect("/icici/sessionout.jsp");
            return;
        }
        Connection conn = null;
        ResultSet rs = null;
        int records=15;
        int pageno=1;
        String errormsg = "";
        String EOL = "<BR>";

        String memberId=request.getParameter("memberid");
        String terminalId=request.getParameter("terminalid");
        String bankwireId=request.getParameter("bankwireid");

        Hashtable hash = null;
        Functions functions=new Functions();
        StringBuffer sb=new StringBuffer();
        RequestDispatcher rd=null;
        String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;


        if(functions.isValueNull(bankwireId) && functions.isValueNull(memberId) && functions.isValueNull(terminalId))
        {
            rd=request.getRequestDispatcher("/randomChargesInMerchantWire.jsp?ctoken="+user.getCSRFToken());
        }
        else if(functions.isValueNull(bankwireId))
        {
            rd=request.getRequestDispatcher("/randomChargesInBankWire.jsp?ctoken="+user.getCSRFToken());
        }
        else
        {
            rd=request.getRequestDispatcher("/listMerchantRandomCharges.jsp?ctoken="+user.getCSRFToken());
        }

        if(!ESAPI.validator().isValidInput("memberid", memberId, "Numbers", 20,true))
        {
            sb.append("Invalid Member Id,");
        }
        if(!ESAPI.validator().isValidInput("terminalid", terminalId, "Numbers", 5,true))
        {
            sb.append("Invalid Terminal Id,");
        }
        if(!ESAPI.validator().isValidInput("bankwireid", bankwireId, "Numbers", 5,true))
        {
            sb.append("Invalid Bankwire Id,");
        }
        if(sb.length()>0)
        {
            logger.error("Validation Failed===="+sb.toString());
            request.setAttribute("statusMsg",sb.toString());
            rd.forward(request,response);
            return;
        }

        int start = 0; // start index
        int end = 0; // end index

        try
        {
            validateOptionalParameter(request);
        }
        catch (ValidationException e)
        {
            logger.error("Enter valid input",e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            logger.debug("message..." + e.getMessage());
            request.setAttribute("message",errormsg);
            rd = request.getRequestDispatcher("/listMerchantRandomCharges.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(request.getParameter("SRecords"), 15);
        start = (pageno - 1) * records;
        end = records;
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("select merchantrdmchargeid,memberid,terminalid,chargename,chargerate,valuetype,chargecounter,chargeamount,chargevalue,chargeremark,actionExecutorId,actionExecutorName from merchant_random_charges where merchantrdmchargeid>0 ");
            StringBuffer countquery = new StringBuffer("select count(*) from merchant_random_charges where merchantrdmchargeid>0 ");


            if(functions.isValueNull(memberId))
            {
                query.append(" and memberid='"+memberId+"'");
                countquery.append(" and memberid='"+memberId+"'");
            }
            if(functions.isValueNull(terminalId))
            {
                query.append(" and terminalid='"+terminalId+"'");
                countquery.append(" and terminalid='"+terminalId+"'");
            }
            if(functions.isValueNull(bankwireId))
            {
                query.append(" and bankwireid='"+bankwireId+"'");
                countquery.append(" and bankwireid='"+bankwireId+"'");
            }
            query.append(" order by merchantrdmchargeid desc LIMIT " + start + "," + end);

            logger.debug("Query:-" + query);
            logger.debug("CountQuery:-" + countquery);

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));

            request.setAttribute("transdetails", hash);

        }
        catch (SystemError s)
        {
            logger.error("System error while perform select query", s);
            //sb.append("System error while perform select query"+s);
            Functions.ShowMessage("Error", "System error while perform select query");

        }
        catch (SQLException e)
        {
            logger.error("SQL error", e);
            //sb.append("SQL Exception while perform select query"+ e);
            Functions.ShowMessage("Error", "System error while perform select query");

        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        request.setAttribute("statusMsg",sb.toString());
        rd=request.getRequestDispatcher("/listMerchantRandomCharges.jsp?ctoken="+user.getCSRFToken());
        rd.forward(request,response);
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
