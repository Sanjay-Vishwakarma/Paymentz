package net.partner;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class MemberDetails extends HttpServlet
{

    private static Logger logger = new Logger(MemberDetails.class.getName());

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
        PrintWriter out = res.getWriter();
        //String data = req.getParameter( "data" );
        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        int pageno=1;
        int records=15;

        errormsg = errormsg + validateParameters(req);

        String company = req.getParameter("company");
        String memberid = req.getParameter("memberid");

        String month = req.getParameter("month");
        String year = req.getParameter("year");
        /*if(errormsg!="")
        {
            logger.debug("ENTER VALID DATA");
            sErrorMessage.append(errormsg);
            req.setAttribute("error",errormsg);

        }*/
        int newmonth = 0;
        if (month != null)

            newmonth = Integer.parseInt(month);


        /*else
            newmonth = ( Calendar.getInstance() ).get( Calendar.MONTH ) + 1;*/

        /*
        int newyear = 0;
        if ( year != null )
            newyear = Integer.parseInt( year );
        else
            newyear = ( Calendar.getInstance() ).get( Calendar.YEAR );*/

        /*if ( newmonth < 1 )
            newmonth = 12;
        */

        if (newmonth != 0)
        {
            if (newmonth < 10)
                month = "0" + newmonth; // require as mysql require month in 01 formate
            else
                month = "" + newmonth;
        }


        pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);

        // calculating start & end
        start = (pageno - 1) * records;
        end = records;

        //	out.println("merchantid "+ merchantid);

        Hashtable hash = null;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StringBuffer query = new StringBuffer("select company_name,contact_emails,memberid,invoicetemplate,isPoweredBy,template,activation,icici,aptprompt,reserves,chargeper,fixamount,reserve_reason,accountid,reversalcharge,withdrawalcharge,chargebackcharge,taxper,isPharma,isValidateEmail,custremindermail,daily_amount_limit,monthly_amount_limit,daily_card_limit,weekly_card_limit,monthly_card_limit,activation,icici,haspaid,isservice,hralertproof,autoredirect,vbv,hrparameterised,masterCardSupported,check_limit,partnerid,iswhitelisted,card_transaction_limit,card_check_limit,daily_card_amount_limit,weekly_card_amount_limit,monthly_card_amount_limit,isrefund,refunddailylimit,agentId,isIpWhitelisted,autoSelectTerminal,isPODRequired,maxScoreAllowed,maxScoreAutoReversal,weekly_amount_limit,isappmanageractivate,activation from members as M where memberid=?");
        StringBuffer countquery = new StringBuffer("select count(*) from members where memberid=?");
/*
        if (company != null)
        {
            query.append(" and company_name like '%" +ESAPI.encoder().encodeForSQL(me,company) + "%'");
            countquery.append(" and company_name like '%" + ESAPI.encoder().encodeForSQL(me,company) + "%'");
        }*/
/*        if (memberid != null)
        {
            query.append(" and memberid=" + ESAPI.encoder().encodeForSQL(me,memberid));
            countquery.append(" and memberid=" + ESAPI.encoder().encodeForSQL(me,memberid));
        }*/
        query.append(" order by memberid asc LIMIT " + start + "," + end);

        String str="select * from partners";
        String count="select count(*) from partners";

        String agentstr="select * from agents";
        String agentcount="select count(*) from agents";

        Connection con = null;
        PreparedStatement p = null;
        PreparedStatement p1 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        ResultSet agentrs1 = null;
        try
        {
            Functions f = new Functions();
            con = Database.getConnection();
            if (f.isValueNull(memberid))
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
                    hash.put("records", "" + (hash.size() - 2));


                // for partner hash
                Hashtable hash1 = Database.getHashFromResultSet(Database.executeQuery(str,con));

                rs1 = Database.executeQuery(count,con);
                rs1.next();
                int totalrecords1 = rs1.getInt(1);

                hash1.put("totalrecords1", "" + totalrecords1);
                hash1.put("records1", "0");

                if (totalrecords1 > 0)
                    hash1.put("records1", "" + (hash1.size() - 2));
                req.setAttribute("partners", hash1);

                //for agents hash
                Hashtable agenthash1 = Database.getHashFromResultSet(Database.executeQuery(agentstr,con));

                agentrs1 = Database.executeQuery(agentcount,con);
                agentrs1.next();
                int totalagentrecords1 = agentrs1.getInt(1);

                agenthash1.put("totalrecords2", "" + totalagentrecords1);
                agenthash1.put("records2", "0");

                if (totalagentrecords1 > 0)
                    agenthash1.put("records2", "" + (agenthash1.size() - 2));
                req.setAttribute("agents", agenthash1);
            }
            else
            {
                hash = new Hashtable();
                hash.put("records", "0");
                hash.put("totalrecords", "0");
            }
//            if (totalrecords > 0)
//                hash.put("records", "" + (hash.size() - 2));
//            else
//            {
//                req.setAttribute("memberdetails", hash);
//                System.out.println("forwarding to member preference");
//
//                RequestDispatcher rd = req.getRequestDispatcher("/memberpreference.jsp");
//                rd.forward(req, res);
//            }

//            int rowsize = hash.size() - 2;
//            Hashtable temphash = null;

//            String tempmemberid = "";
//
//            for (int pos = 1; pos <= rowsize; pos++)
//            {
//                String id = Integer.toString(pos);
//                temphash = (Hashtable) hash.get(id);
//
//                if (pos == rowsize)
//                    tempmemberid = tempmemberid + (String) temphash.get("memberid");
//                else
//                    tempmemberid = tempmemberid + (String) temphash.get("memberid") + ",";
//            }

//            StringBuffer newquery = new StringBuffer("select toid,status,sum(amount) as amount from transaction_icicicredit where status in ('settled','chargeback','reversed','podsent') ");
//
//            if (month != null)
//                newquery.append(" and date_format(from_unixtime(dtstamp),'%m') = '" + month + "' ");
//            if (year != null)
//                newquery.append(" and date_format(from_unixtime(dtstamp),'%Y') = '" + year + "' ");
//
//            newquery.append(" and toid in (" + tempmemberid + ") group by toid,status order by toid,status");
//
//            logger.info(newquery);
//            ResultSet temprs = Database.executeQuery(newquery.toString(), Database.getConnection());
//
//            int toid = -9999;
//            String tempamount = null;
//            String status = null;
//
//            String id = null;
//            int pos = 1;
//
//            while (temprs.next())
//            {
//                toid = temprs.getInt("toid");
//
//                for (pos = 1; pos <= rowsize; pos++)
//                {
//                    id = Integer.toString(pos);
//                    temphash = (Hashtable) hash.get(id);
//                    int hashmemberid = Integer.parseInt((String) temphash.get("memberid"));
//
//                    if (toid == hashmemberid)
//                        break;
//                }
//
//                temphash = (Hashtable) hash.get("" + pos);
//                tempamount = temprs.getString("amount");
//                status = temprs.getString("status");
//
//                if (tempamount != null)
//                    temphash.put("dtstamp_" + status + "amount", tempamount);
//
//                hash.put(id, temphash);
//            }
//
//            newquery.delete(0, newquery.length());
//
//            /*  newquery = "select toid,status,sum(amount) as amount from transaction_icicicredit where status in ('settled','chargeback','reversed') " +
//                                "and date_format(timestamp,'%m%Y') = '" + month + newyear + "' and toid in (" + tempmemberid + ") group by toid,status order by toid,status";
//            */
//
//            newquery.append("select toid,status,sum(amount) as amount from transaction_icicicredit where status in ('settled','chargeback','reversed','podsent') ");
//
//            if (month != null)
//                newquery.append(" and date_format(timestamp,'%m') = '" + month + "' ");
//            if (year != null)
//                newquery.append(" and date_format(timestamp,'%Y') = '" + year + "' ");
//
//            newquery.append(" and toid in (" + tempmemberid + ") group by toid,status order by toid,status");
//
//            logger.info(newquery);
//            ResultSet temprs1 = Database.executeQuery(newquery.toString(), Database.getConnection());
//
//            pos = 1;
//            while (temprs1.next())
//            {
//                toid = temprs1.getInt("toid");
//                temphash = (Hashtable) hash.get("" + toid);
//
//                for (pos = 1; pos <= rowsize; pos++)
//                {
//                    id = Integer.toString(pos);
//                    temphash = (Hashtable) hash.get(id);
//                    int hashmemberid = Integer.parseInt((String) temphash.get("memberid"));
//
//                    if (toid == hashmemberid)
//                        break;
//                }
//
//                tempamount = temprs1.getString("amount");
//                status = temprs1.getString("status");
//
//                if (tempamount != null)
//                    temphash.put("timestamp_" + status + "amount", tempamount);
//
//                hash.put(id, temphash);
//            }
            hash.put("month", "" + month);
            hash.put("year", "" + year);
            req.setAttribute("memberdetails", hash);
            /*StringBuilder chargeBackMessage = new StringBuilder();
            chargeBackMessage.append(sSuccessMessage.toString());
            chargeBackMessage.append("<BR/>");
            chargeBackMessage.append(sErrorMessage.toString());
            req.setAttribute("cbmessage", chargeBackMessage.toString());
            logger.debug("forwarding to member preference");
            RequestDispatcher rd = req.getRequestDispatcher("/memberpreference.jsp");
            rd.forward(req, res);*/
        }
        catch (SystemError se)
        {
            logger.error("System Error::::",se);
            //out.println(Functions.ShowMessage("stacktrace", sw.toString()));
            sErrorMessage.append("Internal System Error");
        }
        catch (Exception e)
        {
            logger.error("Exception::::",e);
            //out.println(Functions.ShowMessage("stacktrace", sw.toString()));
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
                con.close();
            }
            catch (SQLException e)
            {
                logger.error("SQL Exception::::",e);
                //out.println(Functions.ShowMessage("stacktrace", sw.toString()));
                sErrorMessage.append("Internal System Error");
            }
        }
        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());
        req.setAttribute("cbmessage", chargeBackMessage.toString());
        req.setAttribute("error",errormsg);
        logger.debug("forwarding to member preference");
        RequestDispatcher rd = req.getRequestDispatcher("/memberpreference.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }

    private String validateParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        //String EOL = "<BR>";
        List<InputFields>  inputFieldsListOptional = new ArrayList<InputFields>();
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
                    error = error+errorList.getError(inputFields.toString()).getMessage();
                }
            }
        }
        return error;
    }
}
