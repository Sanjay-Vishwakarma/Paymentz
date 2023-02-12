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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Admin on 10/10/2015.
 */
public class ListAgentCommission extends HttpServlet
{
    static Logger logger=new Logger(ListAgentCommission.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException
    {
        doProcess(request, response);
    }
    public void doProcess(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session=request.getSession();
        User user = (User)session.getAttribute("ESAPIUserSessionKey");
        if(!Admin.isLoggedIn(session))
        {
            response.sendRedirect("/icici/sessionout.jsp");
            return;
        }

        Functions functions = new Functions();
        Connection conn=null;
        int records=15;
        int pageno=1;
        String errormsg = "";
        String EOL = "<BR>";


        String delete="";

        if(functions.isValueNull(request.getParameter("delete")))
            delete = request.getParameter("delete");

        String qry="";
        PreparedStatement pstmt2=null;
        String success="";
        String error="";
        String agentId = request.getParameter("agentid");
        String memberId = request.getParameter("memberid");
        String terminalid=request.getParameter("terminalid");
        String chargeName = request.getParameter("chargeid");


        if (delete.equalsIgnoreCase("delete"))
        {
            try
            {
                String id1=request.getParameter("ids");
                String ids[]={};
                ids = id1.split(",");
                for (String id : ids)
                {
                    conn = Database.getConnection();
                    qry = "DELETE FROM agent_commission WHERE id=?";
                    pstmt2 = conn.prepareStatement(qry);
                    pstmt2.setString(1, id);
                    int j = pstmt2.executeUpdate();
                    if (j >= 1)
                    {
                        success = "Records Deleted Successfully.";
                    }
                    else
                    {
                        error = "Delete failed";
                    }
                    request.setAttribute("success", success);
                    request.setAttribute("error", error);
                }
                RequestDispatcher rd = request.getRequestDispatcher("/listAgentCommission.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request, response);
                return;
            }
            catch (Exception e)
            {
                logger.error(e);
                RequestDispatcher rd = request.getRequestDispatcher("/listAgentCommission.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request, response);
                return;
            }
        }

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
            RequestDispatcher rd = request.getRequestDispatcher("/listAgentCommission.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            return;
        }

        Hashtable hash=null;
        StringBuffer sb=new StringBuffer();
        RequestDispatcher rd=request.getRequestDispatcher("/listAgentCommission.jsp?ctoken="+user.getCSRFToken());

        if(!ESAPI.validator().isValidInput("agentid", agentId, "Numbers", 10,true))
        {
            sb.append("Invalid Agent Id<BR>");
        }
        if(!ESAPI.validator().isValidInput("memberid", memberId, "Numbers", 10,true))
        {
            sb.append("Invalid Member Id<BR>");
        }
        if(!ESAPI.validator().isValidInput("terminalid", terminalid, "Numbers", 5,true))
        {
            sb.append("Invalid Terminal Id<BR>");
        }
        if(sb.length()>0)
        {
            request.setAttribute("statusMsg", sb.toString());
            rd.forward(request,response);
            return;
        }

        int start=0;
        int end=0;

        pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(request.getParameter("SRecords"), 15);
        start = (pageno-1) * records;
        end = records;
        ResultSet rs = null;
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("SELECT ac.id,ac.agentid,ac.memberid,ac.terminalid,ac.chargeid,ac.commission_value,ac.startdate,ac.enddate,ac.sequence_no,ac.actionExecutorId,ac.actionExecutorName,cm.chargename FROM agent_commission AS ac JOIN charge_master cm ON ac.chargeid=cm.chargeid");
            StringBuffer countquery = new StringBuffer("SELECT COUNT(*) FROM agent_commission AS ac JOIN charge_master cm ON ac.chargeid=cm.chargeid");
            if(functions.isValueNull(agentId))
            {
                query.append(" and ac.agentid='"+agentId+"'");
                countquery.append(" and ac.agentid='"+agentId+"'");
            }
            if(functions.isValueNull(memberId))
            {
                query.append(" and ac.memberid='"+memberId+"'");
                countquery.append(" and ac.memberid='"+memberId+"'");
            }
            if(functions.isValueNull(terminalid))
            {
                query.append(" and ac.terminalid='"+terminalid+"'");
                countquery.append(" and ac.terminalid='"+terminalid+"'");
            }
            if(functions.isValueNull(chargeName))
            {
                query.append(" and ac.chargeid='"+chargeName+"'");
                countquery.append(" and ac.chargeid='"+chargeName+"'");
            }
            query.append(" order by ac.id desc LIMIT " + start + "," + end);

            logger.debug("Query:-" + query);
            logger.debug("CountQuery:-" + countquery);

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            rs=Database.executeQuery(countquery.toString(),conn);
            rs.next();
            int totalrecords=rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records","0");

            if(totalrecords>0)
                hash.put("records", "" + (hash.size()-2));

            request.setAttribute("transdetails",hash);
        }
        catch (SystemError se)
        {
            logger.error("SystemError while perform select query", se);
            //sb.append("SystemError while performing select query"+ se);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        catch (SQLException e)
        {
            logger.error("SQLException while perform select query", e);
            //sb.append("SQLException while performing select query"+ e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }

        request.setAttribute("statusMsg",sb.toString());
        RequestDispatcher rd1=request.getRequestDispatcher("/listAgentCommission.jsp?ctoken="+user.getCSRFToken());
        rd1.forward(request,response);
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
