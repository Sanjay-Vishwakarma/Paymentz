import com.directi.pg.Admin;
import com.directi.pg.*;
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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: May 26, 2012
 * Time: 4:50:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class AdminChargebackList extends HttpServlet
{
     private static Logger logger = new Logger(AdminChargebackList.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
          int start = 0; // start index
        int end = 0; // end index
        HttpSession session = req.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");


            if (!Admin.isLoggedIn(session))
            {   logger.debug("invalid user");
                res.sendRedirect("/icici/logout.jsp");
                return;
            }
        res.setContentType("text/html");

        PrintWriter out = res.getWriter();
        String data = req.getParameter("data");

       
        String description = Functions.checkStringNull(req.getParameter("SDescription"));
        String trakingid = Functions.checkStringNull(req.getParameter("STrakingid"));

        //Functions fn=new Functions();

        int pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        int records = Functions.convertStringtoInt(req.getParameter("SRecords"), 10);
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);

        // calculating start & end
        start = (pageno - 1) * records;
        end = records;

        Hashtable hash = null;
        //Select * from chargeback_transaction_list where processed='N' and cb_indicator='ADJM' and fk_chargeback_process_id in (Select chargeback_process_id from chargeback_report_process_history where parsed='Z')
        StringBuffer query = new StringBuffer("select CBL.*,T.status from chargeback_transaction_list as CBL,transaction_icicicredit as T where CBL.processed='N' and CBL.cb_indicator='ADJM' and CBL.fk_chargeback_process_id in (Select chargeback_process_id from chargeback_report_process_history where parsed='Z') and T.icicitransid = CBL.icicitransid");
        StringBuffer countquery = new StringBuffer("select count(*) from chargeback_transaction_list as CBL,transaction_icicicredit as T where CBL.processed='N' and CBL.cb_indicator='ADJM' and CBL.fk_chargeback_process_id in (Select chargeback_process_id from chargeback_report_process_history where parsed='Z') and T.icicitransid = CBL.icicitransid ");
        //String status= ("select t.status,c.icicitransid,c.toid,c.merchantid,c.amount from transaction_icicicredit as t,chargeback_transaction_list as c,chargeback_report_process_history as cbp where c.processed='N' and c.cb_indicator='ADJM' and cbp.parsed='Z'and c.icicitransid=t.icicitransid");
        if (description != null)
        {
            query.append(" and description like '%" + ESAPI.encoder().encodeForSQL(me, description) + "%'");
            countquery.append(" and description like '%" + ESAPI.encoder().encodeForSQL(me, description) + "%'");
        }

        if (trakingid != null)
        {
            query.append(" and icicitransid=" +ESAPI.encoder().encodeForSQL(me, trakingid));
            countquery.append(" and icicitransid=" + ESAPI.encoder().encodeForSQL(me, trakingid));
        }


        query.append(" order by icicitransid desc LIMIT " + start + "," + end);


        Connection conn = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getConnection();
            hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), conn));
           // hash = Database.getHashFromResultSet(Database.executeQuery(status.toString(), conn));

            rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));


            req.setAttribute("chargebackdetails", hash);

           // RequestDispatcher rd = req.getRequestDispatcher("/adminchargebacklist.jsp");
            //rd.forward(req, res);
        }
        catch (Exception e)
        {
            out.println(Functions.ShowMessageForAdmin("Error!", e.toString()));
        }


        //query for chargeback reversal start

        Hashtable hash1 = null;
        //Select * from chargeback_transaction_list where processed='N' and cb_indicator='CBRV' and fk_chargeback_process_id in (Select chargeback_process_id from chargeback_report_process_history where parsed='Z');
        //for status query
        //StringBuffer query1 = Select CBL.*,T.status from chargeback_transaction_list as CBL,transaction_icicicredit as T where CBL.processed='N' and CBL.cb_indicator='CBRV' and CBL.fk_chargeback_process_id in (Select chargeback_process_id from chargeback_report_process_history where parsed='Z') and T.icicitransid = CBL.icicitransid
        //
        StringBuffer query1 = new StringBuffer("Select CBL.*,T.status from chargeback_transaction_list as CBL,transaction_icicicredit as T where CBL.processed='N' and CBL.cb_indicator='CBRV' and CBL.fk_chargeback_process_id in (Select chargeback_process_id from chargeback_report_process_history where parsed='Z') and T.icicitransid = CBL.icicitransid");
         StringBuffer countquery1 = new StringBuffer("Select count(*) from chargeback_transaction_list as CBL,transaction_icicicredit as T where CBL.processed='N' and CBL.cb_indicator='CBRV' and CBL.fk_chargeback_process_id in (Select chargeback_process_id from chargeback_report_process_history where parsed='Z') and T.icicitransid = CBL.icicitransid");

        if (description != null)
        {
            query1.append(" and description like '%" + ESAPI.encoder().encodeForSQL(me, description) + "%'");
            countquery1.append(" and description like '%" + ESAPI.encoder().encodeForSQL(me, description) + "%'");
        }

        if (trakingid != null)
        {
            query1.append(" and icicitransid=" + ESAPI.encoder().encodeForSQL(me, trakingid));
            countquery1.append(" and icicitransid=" + ESAPI.encoder().encodeForSQL(me, trakingid));
        }


        //query1.append(" order by icicitransid desc LIMIT " + start + "," + end);


        //Connection con = null;
        try
        {
            //con = Database.getConnection();
            hash1 = Database.getHashFromResultSet(Database.executeQuery(query1.toString(), conn));


            rs = Database.executeQuery(countquery1.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash1.put("totalrecords", "" + totalrecords);
            hash1.put("records1", "0");

            if (totalrecords > 0)
                hash1.put("records1", "" + (hash1.size() - 2));

            req.setAttribute("chargebackreversal", hash1);


        }
        catch (Exception e)
        {   logger.error("SQL error",e);
            out.println(Functions.ShowMessageForAdmin("Error!", e.toString()));
        }
        //query for chargeback reversal END




        //query for AUTO chargeback reversal start
        Hashtable hash2 = null;
        //Select CBL.*,T.status from chargeback_transaction_list as CBL,transaction_icicicredit as T where CBL.processed='N' and CBL.cb_indicator='AUTO' and CBL.fk_chargeback_process_id in (Select chargeback_process_id from chargeback_report_process_history where parsed='Z') and T.icicitransid = CBL.icicitransid
        StringBuffer query2 = new StringBuffer("Select CBL.*,T.status from chargeback_transaction_list as CBL,transaction_icicicredit as T where CBL.processed='N' and CBL.cb_indicator='AUTO' and CBL.fk_chargeback_process_id in (Select chargeback_process_id from chargeback_report_process_history where parsed='Z') and T.icicitransid = CBL.icicitransid");
        StringBuffer countquery2 = new StringBuffer("Select count(*) from chargeback_transaction_list where processed='N' and cb_indicator='AUTO' and fk_chargeback_process_id in (Select chargeback_process_id from chargeback_report_process_history where parsed='Z')");

        if (description != null)
        {
            query2.append(" and description like '%" + ESAPI.encoder().encodeForSQL(me, description) + "%'");
            countquery2.append(" and description like '%" + ESAPI.encoder().encodeForSQL(me, description) + "%'");
        }

        if (trakingid != null)
        {
            query2.append(" and icicitransid=" +ESAPI.encoder().encodeForSQL(me, trakingid));
            countquery2.append(" and icicitransid=" +ESAPI.encoder().encodeForSQL(me, trakingid));
        }


        //query1.append(" order by icicitransid desc LIMIT " + start + "," + end);


        //Connection connn = null;
        try
        {
            //connn = Database.getConnection();
            hash2 = Database.getHashFromResultSet(Database.executeQuery(query2.toString(), conn));


            rs = Database.executeQuery(countquery2.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash2.put("totalrecords", "" + totalrecords);
            hash2.put("records2", "0");

            if (totalrecords > 0)
                hash2.put("records2", "" + (hash2.size() - 2));

            req.setAttribute("Autochargebackreversal", hash2);
        }
        catch (Exception e)
        {   logger.error("SQL error",e);
            out.println(Functions.ShowMessageForAdmin("Error!", e.toString()));
        }
        //Auto Reversal chargeback END




        //Fail File List is Start
        Hashtable hash3 = null;
        //Select * from chargeback_report_process_history where parsed = 'X';
        StringBuffer query3 = new StringBuffer("Select * from chargeback_report_process_history where parsed ='X' or parsed='N'");
         StringBuffer countquery3 = new StringBuffer("Select count(*) from chargeback_report_process_history where parsed ='X' or parsed='N'");

        try
        {
            //connn = Database.getConnection();
            hash3 = Database.getHashFromResultSet(Database.executeQuery(query3.toString(), conn));


            rs = Database.executeQuery(countquery3.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash3.put("totalrecords", "" + totalrecords);
            hash3.put("records3", "0");

            if (totalrecords > 0)
                hash3.put("records3", "" + (hash3.size() - 2));

            req.setAttribute("failfile", hash3);


        }
        catch (Exception e)
        {
            logger.error("system error",e);
            out.println(Functions.ShowMessageForAdmin("Error", e.toString()));
        }

        //not found file list start
        Hashtable hash4 = null;
        //Select file_name, unprocessed_transactions from chargeback_report_process_history where parsed='Z' and no_of_transactions < no_of_rows
        StringBuffer query4 = new StringBuffer("Select file_name, unprocessed_transactions from chargeback_report_process_history where parsed='Z' and no_of_transactions < no_of_rows");
         StringBuffer countquery4 = new StringBuffer("Select count(*) from chargeback_report_process_history where parsed='Z' and no_of_transactions < no_of_rows");

        try
        {
            //connn = Database.getConnection();
            hash4 = Database.getHashFromResultSet(Database.executeQuery(query4.toString(), conn));



            rs = Database.executeQuery(countquery4.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash4.put("totalrecords", "" + totalrecords);
            hash4.put("records4", "0");

            if (totalrecords > 0)
                hash4.put("records4", "" + (hash4.size() - 2));

            req.setAttribute("notfoundfile", hash4);


        }
        catch (Exception e)
        {
            logger.error("SQL error",e);
            out.println(Functions.ShowMessageForAdmin("Error!", e.toString()));
        }

        //Listing Not in Sync Transactions For Chargeback is Start
        Hashtable hash5 = null;
        //Select CBL.*,T.status from chargeback_transaction_list as CBL,transaction_icicicredit as T where CBL.processed='Z' and CBL.fk_chargeback_process_id in (Select chargeback_process_id from chargeback_report_process_history where parsed='Z') and T.icicitransid = CBL.icicitransid
        StringBuffer query5 = new StringBuffer("Select CBL.*,T.status from chargeback_transaction_list as CBL,transaction_icicicredit as T where CBL.processed='Z' and CBL.fk_chargeback_process_id in (Select chargeback_process_id from chargeback_report_process_history where parsed='Z') and T.icicitransid = CBL.icicitransid");
        StringBuffer countquery5 = new StringBuffer("Select count(*) from chargeback_transaction_list where processed='Z' and fk_chargeback_process_id in (Select chargeback_process_id from chargeback_report_process_history where parsed='Z')");

        try
        {
            //connn = Database.getConnection();
            hash5 = Database.getHashFromResultSet(Database.executeQuery(query5.toString(), conn));


            rs = Database.executeQuery(countquery5.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash5.put("totalrecords", "" + totalrecords);
            hash5.put("records5", "0");

            if (totalrecords > 0)
                hash5.put("records5", "" + (hash5.size() - 2));

            req.setAttribute("syncfile", hash5);



        }

        catch (SystemError se)
        {   logger.error("system error",se);
            out.println(Functions.ShowMessageForAdmin("Error", se.toString()));
            //System.out.println(se.toString());

        }
        catch (Exception e)
        {   logger.error("SQL error",e);
            out.println(Functions.ShowMessageForAdmin("Error!", e.toString()));
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }

        RequestDispatcher rd = req.getRequestDispatcher("/adminchargebacklist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);

    }
}
