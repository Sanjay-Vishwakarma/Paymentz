import com.directi.pg.Admin;
import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
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
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 2/11/15
 * Time: 3:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActionGatewayAccountsCharges extends HttpServlet
{
    private static Logger log = new Logger(ActionGatewayAccountsCharges.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        String errormsg="";
        String EOL = "<BR>";
        try
        {
            validateOptionalParameter(req);
        }
        catch (ValidationException e)
        {
            log.error("Enter valid input",e);
            errormsg +=errormsg + e.getMessage() + EOL;
            log.debug("message..."+e.getMessage());
            req.setAttribute("errormessage",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/actionGatewayAccountsCharges.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        String action=req.getParameter("action");
        String mappingid=req.getParameter("mappingid");
        try
        {
            conn= Database.getRDBConnection();
            String qry="";
            HashMap rsDetails=new HashMap();
            if(action.equalsIgnoreCase("history"))
            {
                qry="SELECT m.terminalid,m.accountid,m.mappingid,h.* FROM ChargeVersionGatewayMaster AS h, gateway_accounts_charges_mapping AS m WHERE m.mappingid=h.gateway_accounts_charges_mapping_id AND m.mappingid=? ORDER BY chargeversionId DESC ";
                pstmt=conn.prepareStatement(qry);
                pstmt.setString(1,mappingid);
                rs=pstmt.executeQuery();
                int i=1;
                while (rs.next())
                {
                    HashMap record=new HashMap();
                    record.put("terminalid",rs.getString("terminalid"));
                    record.put("accountid",rs.getString("accountid"));
                    record.put("mappingid",rs.getString("mappingid"));
                    record.put("gatewayChargeValue",rs.getString("gatewayChargeValue"));
                    record.put("agentCommision",rs.getString("agentCommision"));
                    record.put("partnerCommision",rs.getString("partnerCommision"));
                    record.put("effectiveStartDate",rs.getString("effectiveStartDate"));
                    record.put("effectiveEndDate",rs.getString("effectiveEndDate"));
                    rsDetails.put(i,record);
                    i=i+1;
                }
            }
            else if(action.equalsIgnoreCase("modify"))
            {
                qry="select * from gateway_accounts_charges_mapping where mappingid=?";
                pstmt=conn.prepareStatement(qry);
                pstmt.setString(1,mappingid);
                ResultSet rs2=pstmt.executeQuery();
                if(rs2.next())
                {
                    rsDetails.put("accountid",rs2.getString("accountid"));
                    rsDetails.put("paymodeid",rs2.getString("paymodeid"));
                    rsDetails.put("isinputrequired",rs2.getString("isinputrequired"));
                    rsDetails.put("chargeid",rs2.getString("chargeid"));
                    rsDetails.put("chargevalue",rs2.getString("chargevalue"));
                    rsDetails.put("sequencenum",rs2.getString("sequencenum"));
                }
                qry="SELECT effectiveStartDate,effectiveEndDate FROM ChargeVersionGatewayMaster WHERE chargeversionId=(SELECT MAX(chargeversionId) FROM ChargeVersionGatewayMaster WHERE gateway_accounts_charges_mapping_id=?)";
                pstmt=conn.prepareStatement(qry);
                pstmt.setString(1,mappingid);
                rs2=pstmt.executeQuery();
                int i=0;
                if(rs2.next())
                {

                    rsDetails.put("lastupdateDate",rs2.getString("effectiveEndDate"));
                    rsDetails.put("effectiveStartDate",rs2.getString("effectiveStartDate"));
                }
                rsDetails.put("mappingid",mappingid);
            }
            else
            {
                errormsg = "<center><font class=\"text\" face=\"arial\"><b> Action is not defined</b></font></center>";
            }
            req.setAttribute("action",action);
            req.setAttribute("chargedetails",rsDetails);
            req.setAttribute("errormessage",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/actionGatewayAccountsCharges.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);

        }
        catch (SystemError systemError)
        {
            log.error("system error occurred ",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLException occurred",e);
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.MAPPINGID);
        inputFieldsListMandatory.add(InputFields.ACTION);
        inputValidator.InputValidations(req,inputFieldsListMandatory,false);
    }
}
