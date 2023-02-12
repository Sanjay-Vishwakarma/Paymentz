package net.partner;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Hashtable;

public class MemberList extends HttpServlet
{

    static Logger logger = new Logger(MemberList.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        logger.debug("Entering in :::: MemberList");
        int start = 0; // start index
        int end = 0; // end index
        String str = null;
        String key = null;
        String checksum = null;
        String merchantid = null;

        res.setContentType("text/html");

        PrintWriter out = res.getWriter();
        String data = req.getParameter("data");


        String company = Functions.checkStringNull(req.getParameter("company"));
        String memberid = Functions.checkStringNull(req.getParameter("memberid"));
        String month = Functions.checkStringNull(req.getParameter("month"));
        String year = Functions.checkStringNull(req.getParameter("year"));


        int newmonth = 0;

        if (month != null)
        {
            newmonth = Integer.parseInt(month);
        }
        else
        {
            newmonth = (Calendar.getInstance()).get(Calendar.MONTH) - 1;
        }

        int newyear = 0;

        if (year != null)
        {
            newyear = Integer.parseInt(year);
        }
        else
        {
            newyear = (Calendar.getInstance()).get(Calendar.YEAR) - 1;
        }

        if (newmonth < 1)
            newmonth = 12;

        //Functions fn=new Functions();

        int pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        int records = Functions.convertStringtoInt(req.getParameter("SRecords"), 100);

        // calculating start & end
        start = (pageno - 1) * records;
        end = records;

        //	out.println("merchantid "+ merchantid);
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        Hashtable hash = null;

        StringBuffer query = new StringBuffer("select company_name,contact_emails,memberid,activation,icici,aptprompt,reserves,chargeper,fixamount from members as M where activation='Y'");
        StringBuffer countquery = new StringBuffer("select count(*) from members where activation='Y'");

        if (company != null)
        {
            query.append(" and company_name like '%" + ESAPI.encoder().encodeForSQL(me,company) + "%'");
            countquery.append(" and company_name like '%" +ESAPI.encoder().encodeForSQL(me, company )+ "%'");
        }

        if (memberid != null)
        {
            query.append(" and memberid=" +ESAPI.encoder().encodeForSQL(me, memberid));
            countquery.append(" and memberid=" +ESAPI.encoder().encodeForSQL(me, memberid));
        }

        query.append(" order by memberid asc LIMIT " + start + "," + end);


        //	out.println("query "+ query);
        //	out.println("countquery "+ countquery);

        Connection con = null;
        PreparedStatement p1 = null;
        PreparedStatement p2 = null;
        ResultSet rs =  null;
        ResultSet temprs = null;
        ResultSet temprs1 = null;


        try
        {
            hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), Database.getConnection()));
            //	out.println(hash.toString());

            rs = Database.executeQuery(countquery.toString(), Database.getConnection());
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));


            int rowsize = hash.size() - 2;
            Hashtable temphash = null;

            String tempmemberid = "";


            for (int pos = 1; pos <= rowsize; pos++)
            {
                String id = Integer.toString(pos);
                temphash = (Hashtable) hash.get(id);

                if (pos == rowsize)
                    tempmemberid = tempmemberid + (String) temphash.get("memberid");
                else
                    tempmemberid = tempmemberid + (String) temphash.get("memberid") + ",";
            }
            con = Database.getConnection();
            String newquery = "select toid,status,sum(amount) as amount from transaction_icicicredit where status in ('settled','chargeback','reversed','podsent') and date_format(from_unixtime(dtstamp),'%m%Y') =? and toid in (?) group by toid,status order by toid,status";

            p1=con.prepareStatement(newquery);
            p1.setInt(1,newmonth + newyear);
            p1.setString(2,tempmemberid);
            temprs = p1.executeQuery();

            int toid = -9999;
            String tempamount = null;
            String status = null;


            String id = null;
            int pos = 1;

            while (temprs.next())
            {
                toid = temprs.getInt("toid");
                logger.debug("toid " + toid);


                for (pos = 1; pos <= rowsize; pos++)
                {
                    id = Integer.toString(pos);
                    temphash = (Hashtable) hash.get(id);
                    int hashmemberid = Integer.parseInt((String) temphash.get("memberid"));

                    if (toid == hashmemberid)
                    {
                        logger.debug("found " + hashmemberid);
                        break;
                    }
                }

                temphash = (Hashtable) hash.get("" + pos);
                tempamount = temprs.getString("amount");
                status = temprs.getString("status");


                logger.debug("status " + status);
                logger.debug("tempamount " + tempamount);


                if (tempamount != null)
                    temphash.put("dtstamp_" + status + "amount", tempamount);

                hash.put(id, temphash);
            }


            newquery = "select toid,status,sum(amount) as amount from transaction_icicicredit where status in ('settled','chargeback','reversed') and date_format(timestamp,'%m%Y') =? and toid in (?) group by toid,status order by toid,status";

            p2=con.prepareStatement(newquery);
            p2.setInt(1,newmonth + newyear);
            p2.setString(2,tempmemberid);
            temprs1 = p2.executeQuery();

            pos = 1;
            while (temprs1.next())
            {
                toid = temprs1.getInt("toid");
                temphash = (Hashtable) hash.get("" + toid);

                for (pos = 1; pos <= rowsize; pos++)
                {
                    id = Integer.toString(pos);
                    temphash = (Hashtable) hash.get(id);
                    int hashmemberid = Integer.parseInt((String) temphash.get("memberid"));

                    if (toid == hashmemberid)
                    {
                        logger.debug("found " + hashmemberid);
                        break;
                    }
                }

                tempamount = temprs1.getString("amount");
                status = temprs1.getString("status");

                if (tempamount != null)
                    temphash.put("timestamp_" + status + "amount", tempamount);

                hash.put(id, temphash);
            }

            hash.put("month", "" + newmonth);
            hash.put("year", "" + newyear);

            //	out.println(hash.toString());
            req.setAttribute("memberdetails", hash);

            RequestDispatcher rd = req.getRequestDispatcher("/memberpreference.jsp");
            rd.forward(req, res);
        }
        catch (SystemError se)
        {
            //System.out.println(Functions.ShowMessage("Error",se.toString()));
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            logger.error("System Error::::",se);
            //out.println(Functions.ShowMessage("stacktrace", sw.toString()));
            out.println(Functions.ShowMessage("Error", "Record is not found"));
            //System.out.println(se.toString());

        }
        catch (Exception e)
        {
            //System.out.println(Functions.ShowMessage("Error",se.toString()));
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
           
            logger.error("Exception::::",e);
           // out.println(Functions.ShowMessage("stacktrace", sw.toString()));
            out.println(Functions.ShowMessage("Error!", "Record is not found"));
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeResultSet(temprs);
            Database.closeResultSet(temprs1);
            Database.closePreparedStatement(p1);
            Database.closePreparedStatement(p2);
            Database.closeConnection(con);
        }
    }
}
