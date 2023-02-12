

package net.agent;
import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
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
import java.util.Hashtable;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: saurabh
 * Date: 20/2/13
 * Time: 3:51 PM
 * To change this template use File | Settings | File Templates.
 */

public class AgentMemberConfig extends HttpServlet
{
    private static Logger log = new Logger(AgentMemberConfig.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        HttpSession session = req.getSession();
        log.debug("Enter in Transaction ");
        AgentFunctions agent=new AgentFunctions();
        if (!agent.isLoggedInAgent(session))
        {
            res.sendRedirect("/agent/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        int start=0;
        int end =0;
        String memberid=null;
        String errormsg="";
        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        int pageno=1;
        int records=15;
        try
        {
            memberid = ESAPI.validator().getValidInput("memberid",req.getParameter("memberid"),"Numbers",20,false);

        }
        catch(ValidationException e)
        {
            log.error("ENTER valid memberid in memberid::::",e);
            errormsg = errormsg + "Please Enter valid Member Id.";
        }

        try
        {
            pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("SPageno",req.getParameter("SPageno"),"Numbers",5,true), 1);
            records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("SRecords",req.getParameter("SRecords"),"Numbers",5,true), 15);
        }
        catch(ValidationException e)
        {
            log.error("Invalid page no or records",e);
            pageno = 1;
            records = 15;
        }

        // calculating start & end
        start = (pageno - 1) * records;
        end = records;
        Hashtable hash = null;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StringBuffer query = new StringBuffer("select company_name,contact_emails,memberid,invoicetemplate,isPoweredBy,template,activation,icici,aptprompt,reserves,chargeper,fixamount,reserve_reason,accountid,reversalcharge,withdrawalcharge,chargebackcharge,taxper,isPharma,isValidateEmail,custremindermail,daily_amount_limit,monthly_amount_limit,daily_card_limit,weekly_card_limit,monthly_card_limit,activation,icici,haspaid,isservice,hralertproof,autoredirect,vbv,hrparameterised,masterCardSupported,check_limit,agentid,iswhitelisted,card_transaction_limit,card_check_limit,daily_card_amount_limit,weekly_card_amount_limit,monthly_card_amount_limit,currency from members as M where memberid=?");
        StringBuffer countquery = new StringBuffer("select count(*) from members where memberid=?");
        query.append(" order by memberid asc LIMIT " + start + "," + end);

        Connection con = null;
        PreparedStatement p = null;
        PreparedStatement p1 = null;
        ResultSet rs = null;
        try
        {
            con = Database.getConnection();
            if (memberid != null)
            {
                p=con.prepareStatement(query.toString());
                p.setString(1,memberid);

                hash = Database.getHashFromResultSet(p.executeQuery());
                p1=con.prepareStatement(countquery.toString());
                p1.setString(1,memberid);
                rs = p1.executeQuery();
                rs.next();
                int totalrecords = rs.getInt(1);

                hash.put("totalrecords", "" + totalrecords);
                hash.put("records", "0");
                req.setAttribute("accoutIDwiseMerchantHash", GatewayAccountService.getMerchantDetails());
                if (totalrecords > 0)
                {
                    hash.put("records", "" + (hash.size() - 2));
                }
            }
            else
            {
                hash = new Hashtable();
                hash.put("records", "0");
                hash.put("totalrecords", "0");
            }

            req.setAttribute("memberdetails", hash);
        }
        catch (SQLException e)
        {
            log.error("Sql Exception",e);
            sErrorMessage.append("Internal Error");
        }
        catch (SystemError systemError)
        {
            log.error("System Exception", systemError);
            sErrorMessage.append("Internal System Error");
        }
        finally
        {
            Database.closePreparedStatement(p);
            Database.closePreparedStatement(p1);
            Database.closeResultSet(rs);
            Database.closeConnection(con);
        }


        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());
        req.setAttribute("cbmessage", chargeBackMessage.toString());
        log.debug("forwarding to member preference");
        RequestDispatcher rd = req.getRequestDispatcher("/memberConfig.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);

    }
}

