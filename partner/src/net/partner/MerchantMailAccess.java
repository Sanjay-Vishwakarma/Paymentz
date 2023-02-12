package net.partner;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
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
import java.util.Hashtable;
import java.util.List;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 4/14/15
 * Time: 4:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchantMailAccess extends HttpServlet
{
    private static Logger logger = new Logger(MerchantMailAccess.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }

        res.setContentType("text/html");
        int start = 0; // start index
        int end = 0; // end index
        String errormsg="";
        //PrintWriter out = res.getWriter();
        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        Functions functions =new Functions();
        int pageno=1;
        int records=15;

        errormsg = errormsg + validateParameters(req);
        if (!ESAPI.validator().isValidInput("pid", req.getParameter("pid"), "Numbers", 10, true))
        {
            errormsg = "Invalid Partner ID.";
        }
        if(functions.isValueNull(errormsg)){
            req.setAttribute("error",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/merchantmailaccess.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        String memberid = req.getParameter("memberid");
        String month = req.getParameter("month");
        String year = req.getParameter("year");

        String partnerid = req.getParameter("partnerid");
        try
        {
            if (functions.isValueNull(req.getParameter("pid")) && partner.isPartnerMemberMapped(memberid, req.getParameter("pid")))
            {
                partnerid = req.getParameter("pid");
            }
            else if (!functions.isValueNull(req.getParameter("pid")) && partner.isPartnerSuperpartnerMembersMapped(memberid, req.getParameter("partnerid")))
            {
                partnerid = req.getParameter("partnerid");
            }
            else
            {
                req.setAttribute("error","Invalid partner member configuration.");
                RequestDispatcher rd = req.getRequestDispatcher("/merchantmailaccess.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
        }catch(Exception e){
            logger.error("Exception---" + e);
        }
        int newmonth = 0;
        if (month != null)
        {

            newmonth = Integer.parseInt(month);
        }

        if (newmonth != 0)
        {
            if (newmonth < 10)
            {
                month = "0" + newmonth; // require as mysql require month in 01 formate
            }
            else
            {
                month = "" + newmonth;
            }
        }

        pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);

        // calculating start & end
        start = (pageno - 1) * records;
        end = records;
        Hashtable hash = null;
        //Codec me =
        StringBuilder query = new StringBuilder("select m.company_name,m.contact_emails,m.memberid,m.invoicetemplate,m.isPoweredBy,m.template,m.activation,m.icici,m.aptprompt,m.reserves,m.chargeper,m.fixamount,m.reserve_reason,m.accountid,m.reversalcharge,m.withdrawalcharge,m.chargebackcharge,m.taxper,m.isPharma,m.isValidateEmail,m.custremindermail,m.daily_amount_limit,m.monthly_amount_limit,m.daily_card_limit,m.weekly_card_limit,m.monthly_card_limit,m.activation,m.icici,m.haspaid,m.isservice,m.hralertproof,m.autoredirect,m.vbv,m.hrparameterised,m.masterCardSupported,m.check_limit,m.partnerid,m.iswhitelisted,m.card_transaction_limit,m.card_check_limit,m.daily_card_amount_limit,m.weekly_card_amount_limit,m.monthly_card_amount_limit,m.isrefund,m.refunddailylimit,m.agentId,m.isIpWhitelisted,m.autoSelectTerminal,m.isPODRequired,m.maxScoreAllowed,m.maxScoreAutoReversal,m.weekly_amount_limit,m.isappmanageractivate,m.emailSent,m.isRefundEmailSent,m.chargebackEmail,mc.merchantRegistrationMail,mc.merchantChangePassword,mc.merchantChangeProfile,mc.transactionSuccessfulMail,mc.transactionFailMail,mc.transactionCapture,mc.transactionPayoutSuccess,mc.transactionPayoutFail,mc.refundMail,mc.chargebackMail,mc.transactionInvoice,mc.cardRegistration,mc.payoutReport,mc.highRiskRefunds,mc.fraudFailedTxn,mc.dailyFraudReport,mc.customerTransactionSuccessfulMail,mc.customerTransactionFailMail,mc.customerTransactionPayoutSuccess,mc.customerTransactionPayoutFail,mc.customerRefundMail,mc.customerTokenizationMail from members as m JOIN merchant_configuration mc ON m.memberid=mc.memberid JOIN partners p where m.partnerId=p.partnerId and m.memberid=? and (m.partnerid=? or p.superadminid=?)");
        StringBuilder countquery = new StringBuilder("select count(*) from members m , partners p,merchant_configuration mc where m.partnerId = p.partnerId and m.memberid=? and (m.partnerid=? or p.superadminid=?)");

        query.append(" order by memberid asc LIMIT " + start + "," + end);

        String str="select * from partners";
        String count="select count(*) from partners";

        String agentstr="select * from agents";
        String agentcount="select count(*) from agents";

        Connection con = null;
        PreparedStatement p = null, p1 = null;
        ResultSet rs = null,rs1=null,agentrs1=null;
        try
        {
            Functions f = new Functions();
            //con = Database.getConnection();
            con = Database.getRDBConnection();
            if (f.isValueNull(memberid) && f.isValueNull(partnerid))
            {
                p=con.prepareStatement(query.toString());
                p.setString(1,memberid);
                p.setString(2,partnerid);
                p.setString(3,partnerid);
                logger.debug("query inside marchantMailAccess:::::"+p);

                hash = Database.getHashFromResultSet(p.executeQuery());
                p1=con.prepareStatement(countquery.toString());
                p1.setString(1,memberid);
                p1.setString(2,partnerid);
                p1.setString(3,partnerid);
                logger.debug("count query inside marchantMailAccess:::::"+p1);
                rs = p1.executeQuery();
                rs.next();
                int totalrecords = rs.getInt(1);

                hash.put("totalrecords", "" + totalrecords);
                hash.put("records", "0");
                req.setAttribute("accoutIDwiseMerchantHash", loadGatewayAccounts(partnerid));
                if (totalrecords > 0)
                {
                    hash.put("records", "" + (hash.size() - 2));
                }


                // for partner hash
                Hashtable hash1 = Database.getHashFromResultSet(Database.executeQuery(str,con));

                rs1 = Database.executeQuery(count,con);
                rs1.next();
                int totalrecords1 = rs1.getInt(1);

                hash1.put("totalrecords1", "" + totalrecords1);
                hash1.put("records1", "0");

                if (totalrecords1 > 0)
                {
                    hash1.put("records1", "" + (hash1.size() - 2));
                }
                req.setAttribute("partners", hash1);

                //for agents hash
                Hashtable agenthash1 = Database.getHashFromResultSet(Database.executeQuery(agentstr,con));

                agentrs1 = Database.executeQuery(agentcount,con);
                agentrs1.next();
                int totalagentrecords1 = agentrs1.getInt(1);

                agenthash1.put("totalrecords2", "" + totalagentrecords1);
                agenthash1.put("records2", "0");

                if (totalagentrecords1 > 0)
                {
                    agenthash1.put("records2", "" + (agenthash1.size() - 2));
                }
                req.setAttribute("agents", agenthash1);
            }
            else
            {
                hash = new Hashtable();
                hash.put("records", "0");
                hash.put("totalrecords", "0");
            }

            hash.put("month", "" + month);
            hash.put("year", "" + year);
            req.setAttribute("memberdetails", hash);

        }
        catch (SystemError se)
        {
            logger.error("System Error::::",se);
            sErrorMessage.append("Internal System Error");
        }
        catch (Exception e)
        {
            logger.error("Exception::::",e);
            sErrorMessage.append("Internal System Error");
        }
        finally
        {
            try
            {
                Database.closeResultSet(rs);
                Database.closeResultSet(rs1);
                Database.closeResultSet(agentrs1);
                Database.closePreparedStatement(p);
                Database.closePreparedStatement(p1);
                Database.closeConnection(con);
                //con.close();
            }
            catch (Exception e)
            {
                logger.error("SQL Exception::::",e);
                sErrorMessage.append("Internal System Error");
            }
        }
        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        /*chargeBackMessage.append("<BR/>");*/
        chargeBackMessage.append(sErrorMessage.toString());
        req.setAttribute("cbmessage", chargeBackMessage.toString());
        req.setAttribute("error",errormsg);
        RequestDispatcher rd = req.getRequestDispatcher("/merchantmailaccess.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
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
   TreeMap<Integer,String> loadGatewayAccounts(String partnerId)
    {
        TreeMap<Integer,String> gatewayaccounts=new TreeMap<Integer,String>();
        Connection conn=null;
        ResultSet rs = null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            rs = Database.executeQuery("select g.accountid,g.merchantid,g.pgtypeid,t.currency,t.name from gateway_accounts as g JOIN gateway_type as t ON t.pgtypeid=g.pgtypeid JOIN gateway_account_partner_mapping as gapm ON g.accountid=gapm.accountid and gapm.isActive='Y' and gapm.partnerid="+partnerId+" order by g.accountid asc ", conn);
            while (rs.next())
            {
                gatewayaccounts.put(rs.getInt("accountid"), rs.getString("merchantid")+"-"+rs.getString("name")+"-"+rs.getString("currency"));
            }
        }
        catch(Exception e)
        {
            logger.error("Exception while loading partner managed bank account",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        return gatewayaccounts;

    }
}
