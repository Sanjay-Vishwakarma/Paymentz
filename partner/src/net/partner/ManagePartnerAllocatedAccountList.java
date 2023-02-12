package net.partner;

import com.directi.pg.*;
import com.manager.FraudRuleManager;
import org.apache.commons.lang.StringUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
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
import java.util.Hashtable;
import java.util.ResourceBundle;

/**
 Created by IntelliJ IDEA.
 User: Shipra
 Date: 10/12/18
 **/
public class ManagePartnerAllocatedAccountList extends HttpServlet
{
    private static Logger logger = new Logger(ManagePartnerAllocatedAccountList.class.getName());
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
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        ResourceBundle rb1 = null;
        String language_property1 = (String) session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String ManagePartnerAllocatedAccountList_Deleted_errormsg = StringUtils.isNotEmpty(rb1.getString("ManagePartnerAllocatedAccountList_Deleted_errormsg")) ? rb1.getString("ManagePartnerAllocatedAccountList_Deleted_errormsg") : "- Deleted Successfully";
        String ManagePartnerAllocatedAccountList_partner_errormsg = StringUtils.isNotEmpty(rb1.getString("ManagePartnerAllocatedAccountList_partner_errormsg")) ? rb1.getString("ManagePartnerAllocatedAccountList_partner_errormsg") : "Invalid Partner ID<BR>";
        String ManagePartnerAllocatedAccountList_fraudsystem_errormsg = StringUtils.isNotEmpty(rb1.getString("ManagePartnerAllocatedAccountList_fraudsystem_errormsg")) ? rb1.getString("ManagePartnerAllocatedAccountList_fraudsystem_errormsg") : "Invalid FraudSystem Account<BR>";

        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        RequestDispatcher rd = request.getRequestDispatcher("/managePartnerAllocatedAccountList.jsp?ctoken="+user.getCSRFToken());
        response.setContentType("text/html");
        String action = request.getParameter("action");
        FraudRuleManager fraudRuleManager = new FraudRuleManager();
        StringBuffer errorMsg = new StringBuffer();
        String error ="";
        String mes ="";
        if(action.equals("delete")){
            try
            {
                String partneri1d = request.getParameter("partner_id");
                String account_id = request.getParameter("fsaccount_id");
                String status = fraudRuleManager.DeleteFraudSystemAccount(partneri1d, account_id);
                if(status.equals("delete")){
                    mes = partneri1d+ ManagePartnerAllocatedAccountList_Deleted_errormsg;
                    request.setAttribute("success", mes);
                    //System.out.println("status"+status);
                    rd.forward(request, response);
                    return;
                }
            }catch(Exception e){
                error = e.getMessage();
                request.setAttribute("error", error);
                //System.out.println("error message"+error);
                rd.forward(request, response);
                return;
            }
        }

        String partnerid = null;
        String fsAccount = null;
        partnerid ="";
        Functions functions =  new Functions();
        String pid=request.getParameter("partnerid");
        String partner_id = String.valueOf(session.getAttribute("merchantid"));

        fsAccount = request.getParameter("fsAccount");
        if (!ESAPI.validator().isValidInput("partnerid", partnerid, "Numbers", 10, true))
        {
            errorMsg.append(ManagePartnerAllocatedAccountList_partner_errormsg);
        }
        if (!ESAPI.validator().isValidInput("fsAccount", fsAccount, "SafeString", 100, true))
        {
            errorMsg.append(ManagePartnerAllocatedAccountList_fraudsystem_errormsg);
        }
        if(errorMsg.length()>0)
        {
            request.setAttribute("error", errorMsg.toString());
            rd.forward(request, response);
            return;
        }

        Hashtable hash = null;
        int records=15;
        int pageno=1;
        int start = 0; // start index
        int end = 0; // end index
        pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(request.getParameter("SRecords"), 15);
        start = (pageno - 1) * records;
        end = records;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        Connection conn = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("SELECT pfs.`partnerid`,pfs.`fsaccountid`,fam.accountname,pfs.`isActive` FROM partner_fsaccounts_mapping AS pfs JOIN `fraudsystem_account_mapping` AS fam ON fam.fsaccountid=pfs.`fsaccountid` JOIN partners p ON pfs.partnerid=p.partnerId");
            StringBuffer countquery = new StringBuffer("select count(*) from partner_fsaccounts_mapping As pfs join fraudsystem_account_mapping As fam ON fam.fsaccountid=pfs.`fsaccountid` JOIN partners p ON pfs.partnerid=p.partnerId ");

            if(functions.isValueNull(pid)){
                query.append(" and pfs.partnerid=" + ESAPI.encoder().encodeForSQL(me,pid));
                countquery.append(" and pfs.partnerid=" + ESAPI.encoder().encodeForSQL(me,pid));
            }else{
                query.append(" and (p.partnerid=" + ESAPI.encoder().encodeForSQL(me,partner_id)+ " OR  p.superadminid=" + ESAPI.encoder().encodeForSQL(me,partner_id)+")");
                countquery.append(" and (p.partnerid=" + ESAPI.encoder().encodeForSQL(me,partner_id)+ " OR  p.superadminid=" + ESAPI.encoder().encodeForSQL(me,partner_id)+")");
            }

            if(functions.isValueNull(fsAccount))
            {
                query.append(" and fam.fsaccountid=" + ESAPI.encoder().encodeForSQL(me,fsAccount));
                countquery.append(" and fam.fsaccountid=" + ESAPI.encoder().encodeForSQL(me,fsAccount));
            }
            query.append(" order by pfs.partnerid desc LIMIT " + start + "," + end);
            logger.debug("Query:-" + query);
            logger.debug("CountQuery:-"+countquery);

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);
            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");
            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
            request.setAttribute("transdetails", hash);
            rd.forward(request, response);
        }
        catch (SystemError s)
        {
            logger.error("SystemError::::::",s);
            request.setAttribute("error","Internal Error while processing your request");
            rd.forward(request,response);
            return;
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::",e);
            request.setAttribute("error","Internal Error while processing your request");
            rd.forward(request,response);
            return;
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
    }
}
