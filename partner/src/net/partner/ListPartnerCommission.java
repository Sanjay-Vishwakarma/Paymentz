package net.partner;
import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
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

public class ListPartnerCommission extends HttpServlet
{
    static Logger logger = new Logger(ListPartnerCommission.class.getName());
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
        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        Connection conn = null;
        ResultSet rs = null;
        int records=15;
        int pageno=1;

        String partnerId=request.getParameter("partnerid");
        String memberId=request.getParameter("memberid");

        String errormsg = "";
        String EOL = "<BR>";

        try
        {
            validateOptionalParameter(request);
        }
        catch (ValidationException e)
        {
            logger.error("Enter valid input",e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            logger.debug("message..."+e.getMessage());
            request.setAttribute("message",errormsg);
            RequestDispatcher rd = request.getRequestDispatcher("/listPartnerCommission.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            return;
        }

        Hashtable hash = null;
        Functions functions=new Functions();
        StringBuffer sb=new StringBuffer();

        RequestDispatcher rd = request.getRequestDispatcher("/listPartnerCommission.jsp?ctoken="+user.getCSRFToken());

        if(!ESAPI.validator().isValidInput("partnerid", partnerId, "Number", 10,true))
        {
            sb.append("Invalid Partner Id<BR>");
        }
        if(!ESAPI.validator().isValidInput("memberid", memberId, "Number", 10,true))
        {
            sb.append("Invalid Member Id<BR>");
        }
        if(sb.length()>0)
        {
            request.setAttribute("message", sb.toString());
            rd.forward(request,response);
            return;
        }

        if(!functions.isValueNull(partnerId)){
            partnerId=partner.getSubpartner((String) session.getAttribute("merchantid"));
        }
        System.out.println("inside ListPartnerCommission:::"+partnerId);

        int start = 0; // start index
        int end = 0; // end index

        pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(request.getParameter("SRecords"), 15);
        start = (pageno - 1) * records;
        end = records;
        try
        {
            conn = Database.getConnection();
            StringBuffer query = new StringBuffer("select pc.id,pc.partnerid,pc.memberid,pc.terminalid,pc.chargeid,pc.commission_value,pc.startdate,pc.enddate,pc.sequence_no,cm.chargename from partner_commission as pc JOIN charge_master cm on pc.chargeid=cm.chargeid ");
            StringBuffer countquery = new StringBuffer("select count(*) from partner_commission as pc JOIN charge_master cm on pc.chargeid=cm.chargeid");
            if(functions.isValueNull(partnerId))
            {
                query.append(" and pc.partnerid IN ("+partnerId+")");
                countquery.append(" and pc.partnerid IN("+partnerId+")");
            }
            if(functions.isValueNull(memberId))
            {
                query.append(" and pc.memberid='"+memberId+"'");
                countquery.append(" and pc.memberid='"+memberId+"'");
            }
            query.append(" order by pc.id desc LIMIT " + start + "," + end);

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
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        catch (SQLException e)
        {
            logger.error("SQL error", e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        request.setAttribute("statusMsg",sb.toString());
        RequestDispatcher rd1 = request.getRequestDispatcher("/listPartnerCommission.jsp?ctoken="+user.getCSRFToken());
        rd1.forward(request,response);
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