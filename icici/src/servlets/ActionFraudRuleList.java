import com.directi.pg.Admin;
import com.directi.pg.Database;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by PZ on 18/7/15.
 */
public class ActionFraudRuleList extends HttpServlet
{
    private static Logger logger = new Logger(ActionFraudRuleList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        String errormsg="";

        StringBuffer  statusMsg= new StringBuffer();
        StringBuffer sb=new StringBuffer();

        String EOL = "<BR>";
        RequestDispatcher rd =null;
        try
        {
            validateOptionalParameter(req);
        }
        catch (ValidationException e)
        {
            logger.error("Enter valid input", e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            logger.debug("message..." + e.getMessage());
            req.setAttribute("errormessage",errormsg);
            rd = req.getRequestDispatcher("/actionFraudRuleList.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        String ruleid=req.getParameter("ruleid");
        String action=req.getParameter("action");
        try
        {
            conn= Database.getConnection();
            HashMap rsDetails=null;
            if(action.equalsIgnoreCase("modify"))
            {
                rsDetails=new HashMap();
                StringBuffer qry=new StringBuffer("select ruleid,rulename,ruledescription,rulegroup,score,status from rule_master where ruleid=?");
                pstmt=conn.prepareStatement(qry.toString());
                pstmt.setString(1,ruleid);
                rs=pstmt.executeQuery();
                if(rs.next())
                {
                    rsDetails.put("ruleid",rs.getString("ruleid"));
                    rsDetails.put("rulename",rs.getString("rulename"));
                    rsDetails.put("ruledescription",rs.getString("ruledescription"));
                    rsDetails.put("rulegroup",rs.getString("rulegroup"));
                    rsDetails.put("score",rs.getString("score"));
                    rsDetails.put("status",rs.getString("status"));
                }
                rsDetails.put("ruleid",ruleid);
                req.setAttribute("chargedetails",rsDetails);
            }
            else if(action.equalsIgnoreCase("update"))
            {
                String ruledescription=req.getParameter("ruledescription");
                String rulegroup=req.getParameter("rulegroup");
                String score=req.getParameter("score");
                String rulename=req.getParameter("rulename");
                String status = req.getParameter("status");

                if (!ESAPI.validator().isValidInput("rulename",rulename,"Description",255,false))
                {
                    sb.append("Invalid Rule Name <br>");
                }
                if (!ESAPI.validator().isValidInput("ruledescription",ruledescription,"Description",255,true))
                {
                    sb.append("Invalid Rule Description <br>");
                }
                if (!ESAPI.validator().isValidInput("rulegroup",rulegroup,"Description",255,false))
                {
                    sb.append("Invalid Rule Group <br>");
                }
                if (!ESAPI.validator().isValidInput("score",score,"Numbers",2,false))
                {
                    sb.append("Invalid Score <br>");
                }
                if (!ESAPI.validator().isValidInput("status",status,"Description",255,false))
                {
                    sb.append("Invalid Status <br>");
                }
                if(sb.length()>0)
                {
                    rsDetails=new HashMap();
                    rsDetails.put("ruleid",ruleid);
                    rsDetails.put("rulename",rulename);
                    rsDetails.put("ruledescription",ruledescription);
                    rsDetails.put("rulegroup",rulegroup);
                    rsDetails.put("score",score);
                    rsDetails.put("status",status);

                    req.setAttribute("errormsg", sb.toString());
                    req.setAttribute("chargedetails",rsDetails);
                    rd = req.getRequestDispatcher("/actionFraudRuleList.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(req,res);
                    return;
                }

                Hashtable hashtable=new Hashtable();
                hashtable.put("ruledescription",ruledescription);
                hashtable.put("rulegroup",rulegroup);
                hashtable.put("score",score);

                StringBuffer qry=new StringBuffer("update rule_master set rulename=?,ruledescription=?,rulegroup=?,score=?,status=? where ruleid=?");
                pstmt=conn.prepareStatement(qry.toString());
                pstmt.setString(1, rulename);
                pstmt.setString(2,ruledescription);
                pstmt.setString(3,rulegroup);
                pstmt.setString(4,score);
                pstmt.setString(5,status);
                pstmt.setString(6,ruleid);
                int k=pstmt.executeUpdate();
                if(k>0)
                {
                    statusMsg.append("<center><font class=\"textb\"><b> Fraud Rule Configuration Updated Successfully</b></font></center>");
                }
                else
                {
                    statusMsg.append("<center><font class=\"textb\" face=\"arial\"><b>Could Not Update Fraud Rule Configuration</b></font></center>");
                }
            }
            else
            {
                statusMsg.append("<center><font class=\"textb\" face=\"arial\"><b>Action Is Not Defined</b></font></center>");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("system error occurred ", systemError);
            statusMsg.append("<center><font class=\"textb\" face=\"arial\"><b>Could Not Update Fraud Rule Configuration,Some Technical Exception</b></font></center>");
        }
        catch (SQLException e)
        {
            logger.error("SQLException occurred", e);
            statusMsg.append("<center><font class=\"textb\" face=\"arial\"><b>Could Not Update Fraud Rule Configuration,Some Technical Exception</b></font></center>");
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        req.setAttribute("statusMsg",statusMsg.toString());
        rd = req.getRequestDispatcher("/actionFraudRuleList.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }
    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.RULEID);
        inputValidator.InputValidations(req,inputFieldsListMandatory,false);
    }
}
