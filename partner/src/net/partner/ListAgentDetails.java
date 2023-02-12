package net.partner;

import com.directi.pg.*;
import com.manager.dao.PartnerDAO;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

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
import java.util.HashMap;
import java.util.List;

/**
 * Created by Kanchan on 18-01-2021.
 * Time: 16:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class ListAgentDetails extends HttpServlet
{
    static Logger log = new Logger(ListAgentDetails.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        log.debug("Entering in Listagent details......");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        PartnerFunctions partner = new PartnerFunctions();

        log.debug("ctoken===" + request.getParameter("ctoken"));
        if (!partner.isLoggedInPartner(session))
        {
            log.debug("Partner is logout");
            response.sendRedirect("/partner/logout.jsp");
            return;
        }

        String errorList = "";
        Connection con = null;
        int records = 15;
        int pageno = 1;
        int start = 0; // start index
        int end = 0; // end index

        String errormsg="";
        Functions functions = new Functions();
        HashMap memberHash = null;

        errorList = validateOptionalParameters(request);
        if(errorList!=null && !errorList.equals(""))
        {
            //redirect to jsp page for invalid data entry
            request.setAttribute("error",errorList);
            RequestDispatcher rd = request.getRequestDispatcher("/agentInterface.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            return;
        }

        PartnerDAO partnerDAO = new PartnerDAO();
        String agentId = request.getParameter("agentid");
        String agentName = request.getParameter("agentname");
        String pid= request.getParameter("pid");
        String partner_id= "";
        String superpartnerid= partnerDAO.getSuperAdminId(pid);
        String partnerid1= String.valueOf(session.getAttribute("partnerId"));

        if (functions.isValueNull(pid)){
            if (!partnerid1.equals(superpartnerid) && !pid.equals(partnerid1))
            {
                errormsg= "Invalid partner superpartner configuration";
                request.setAttribute("error",errormsg);
                RequestDispatcher rd= request.getRequestDispatcher("/agentInterface.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request,response);
                return;
            }
        }

            if (functions.isValueNull(request.getParameter("pid")))
            {
                partner_id= pid;
            }
            else
            {
                partner_id= request.getParameter("partnerid");
            }

        pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(request.getParameter("SRecords"), 15);
        start = (pageno - 1) * records;
        end = records;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        ResultSet rs = null;

        try
        {
            con=Database.getRDBConnection();
           StringBuffer query = new StringBuffer("select a.partnerId,a.agentId,a.agentName,a.login from agents a , partners p where agentId>0");
            StringBuffer countquery = new StringBuffer("select count(*) from agents a , partners p where  agentId>0");

            if(pid.equals(partnerid1))
            {
                query.append(" AND a.superadminid = p.partnerId  AND a.partnerid=").append(partner_id);
                countquery.append(" AND a.superadminid = p.partnerId  AND a.partnerid=").append(partner_id);
            }
            else
            {
                query.append(" AND a.partnerId = p.partnerId  AND (a.partnerid=").append(partner_id).append(" or a.superadminid=").append(partner_id).append(")");
                countquery.append(" AND a.partnerId = p.partnerId  AND (a.partnerid=").append(partner_id).append(" or a.superadminid=").append(partner_id).append(")");
            }

            if (functions.isValueNull(agentId))
            {
                query.append(" and agentId='" + ESAPI.encoder().encodeForSQL(me,agentId)+"'");
                countquery.append(" and agentId='" + ESAPI.encoder().encodeForSQL(me,agentId)+"'");
            }

            if (functions.isValueNull(agentName))
            {
                query.append(" and agentName LIKE '%" + ESAPI.encoder().encodeForSQL(me,agentName) + "%'");
                countquery.append(" and agentName LIKE'%" + ESAPI.encoder().encodeForSQL(me,agentName) + "%'");
            }

            query.append(" order by agentId desc LIMIT " + start + "," + end);

            memberHash= Database.getHashMapFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), con));
            rs= Database.executeQuery(countquery.toString(), con);
            rs.next();
            int totalrecords = rs.getInt(1);

            memberHash.put("totalrecords", "" + totalrecords);
            memberHash.put("records", "0");
            if (totalrecords > 0)
            {
                memberHash.put("records", "" + (memberHash.size() - 2));
            }
            request.setAttribute("transdetails", memberHash);
            log.debug("forward to jsp" + memberHash);
        }
        catch (SystemError systemError)
        {
            log.error("SystemError while listing agent details",systemError);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        catch (SQLException e)
        {
            log.error("sql exception while getting agent details",e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(con);
        }
        RequestDispatcher rd = request.getRequestDispatcher("/agentInterface.jsp?ctoken="+user.getCSRFToken());
        rd.forward(request, response);
    }

    private String validateOptionalParameters(HttpServletRequest request)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.PID);
        inputFieldsListOptional.add(InputFields.AGENT_ID);
        inputFieldsListOptional.add(InputFields.AGENT_NAME);
        inputFieldsListOptional.add(InputFields.PAGENO);
        inputFieldsListOptional.add(InputFields.RECORDS);
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(request,inputFieldsListOptional, errorList,true);

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
