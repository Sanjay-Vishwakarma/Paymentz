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
import javax.servlet.ServletContext;
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
 * Created by Kanchan on 18-01-2021.
 * Time: 16:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchantAgentMapping extends HttpServlet
{
    static Logger log = new Logger(MerchantAgentMapping.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        log.debug("Entering in Listagent details......");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        ServletContext application = getServletContext();
        Connection conn = null;
        PartnerFunctions partner = new PartnerFunctions();
        String partnerid = (String)session.getAttribute("merchantid");
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        log.debug("ctoken===" + request.getParameter("ctoken"));
        if (!partner.isLoggedInPartner(session))
        {
            log.debug("Partner is logout");
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        String errorList = "";
        int records = 15;
        int pageno = 1;
        int start = 0; // start index
        int end = 0; // end index

        String errormsg="";
        Functions functions = new Functions();
        Hashtable hash = null;


        errorList = validateOptionalParameters(request);
        if(errorList!=null && !errorList.equals(""))
        {
            //redirect to jsp page for invalid data entry
            request.setAttribute("error",errorList);
            RequestDispatcher rd = request.getRequestDispatcher("/merchantAgentMapping.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            return;
        }

        String agentId = request.getParameter("agentid");
        String memberId = request.getParameter("memberid");
        String pid= request.getParameter("pid");
        String partner_id= "";
        PartnerDAO partnerDAO = new PartnerDAO();
        String superpartnerid= partnerDAO.getSuperAdminId(pid);
        String partnerid1= String.valueOf(session.getAttribute("partnerId"));

        if (functions.isValueNull(pid)){
            if (!partnerid1.equals(superpartnerid) && !pid.equals(partnerid1))
            {
                errormsg= "Invalid partner superpartner configuration";
                request.setAttribute("error",errormsg);
                RequestDispatcher rd= request.getRequestDispatcher("/merchantAgentMapping.jsp?ctoken="+user.getCSRFToken());
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
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("SELECT mam.mappingid,mam.memberid,mam.agentid,a.partnerId,a.agentName AS agentname,m.company_name AS merchantname,FROM_UNIXTIME(creationtime) AS mappingon FROM merchant_agent_mapping AS mam JOIN agents AS a ON mam.agentid=a.agentid JOIN members AS m ON mam.memberid=m.memberid JOIN partners AS p ON p.partnerId=a.partnerId WHERE p.superadminid>0");
            StringBuffer countquery = new StringBuffer("select count(*) from merchant_agent_mapping as mam join agents as a on mam.agentid=a.agentid join members as m on mam.memberid=m.memberid JOIN partners AS p ON p.partnerId=a.partnerId WHERE p.superadminid>0");
            if (functions.isValueNull(pid))
            {
                query.append(" and  p.partnerId='" + ESAPI.encoder().encodeForSQL(me, pid) + "'");
                countquery.append(" and  p.partnerId='" + ESAPI.encoder().encodeForSQL(me, pid) + "'");
            }
            else
            {
                query.append(" and (p.superadminid='" + ESAPI.encoder().encodeForSQL(me, partnerid1) + "' or p.partnerId='" + ESAPI.encoder().encodeForSQL(me, partnerid1) + "')");
                countquery.append(" and (p.superadminid='" + ESAPI.encoder().encodeForSQL(me, partnerid1) + "' or p.partnerId='" + ESAPI.encoder().encodeForSQL(me, partnerid1) + "')");
            }
            if (functions.isValueNull(memberId))
            {
                query.append(" and mam.memberid='" + ESAPI.encoder().encodeForSQL(me, memberId) + "'");
                countquery.append(" and mam.memberid='" + ESAPI.encoder().encodeForSQL(me, memberId) + "'");
            }
            if (functions.isValueNull(agentId))
            {
                query.append(" and mam.agentid='" + ESAPI.encoder().encodeForSQL(me,agentId ) + "'");
                countquery.append(" and mam.agentid='" + ESAPI.encoder().encodeForSQL(me,agentId) + "'");
            }

            query.append(" order by mam.mappingid desc LIMIT " + start + "," + end);
            log.debug("===query===" + query);
            log.debug("===countquery===" + countquery);
            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));

            System.out.println("hash"+hash);
            rs= Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");
            if (totalrecords > 0)
            {
                hash.put("records", "" + (hash.size() - 2));
            }
            request.setAttribute("transdetails", hash);
            log.debug("forward to jsp" + hash);

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
            Database.closeConnection(conn);
        }
        RequestDispatcher rd = request.getRequestDispatcher("/merchantAgentMapping.jsp?ctoken="+user.getCSRFToken());
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
        inputFieldsListOptional.add(InputFields.MEMBERID);
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
