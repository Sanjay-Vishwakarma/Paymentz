package net.partner;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.logicboxes.util.ApplicationProperties;
import com.logicboxes.util.Util;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

/**
 * Created by Pranav on 04-01-2018.
 */
public class ExportMemberDetails extends HttpServlet
{
    private static Logger log = new Logger(ExportMemberDetails.class.getName());

    public static boolean sendFile(String filepath, String filename, HttpServletResponse response)throws Exception
    {
        File f = new File(filepath);
        int length = 0;

        // Set browser download related headers
        response.setContentType("application/octat-stream");
        response.setContentLength((int) f.length());
        response.addHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        javax.servlet.ServletOutputStream op = response.getOutputStream();

        byte[] bbuf = new byte[1024];
        DataInputStream in = new DataInputStream(new FileInputStream(f));

        while ((in != null) && ((length = in.read(bbuf)) != -1))
        {
            op.write(bbuf, 0, length);
        }

        in.close();
        op.flush();
        op.close();

        File file = new File(filepath);
        file.delete();
        log.info("Successful#######");
        return true;

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        //PartnerFunctions partner=new PartnerFunctions();
        //Functions functions = new Functions();
        String dateType = null;
        String toid= Functions.checkStringNull(req.getParameter("toid"));
        String ignoredates= Functions.checkStringNull(req.getParameter("ignoredates"));
        String company_name=Functions.checkStringNull(req.getParameter("company_name"));
        String sitename=Functions.checkStringNull(req.getParameter("sitename"));
        String activation=Functions.checkStringNull(req.getParameter("status"));
        String icici=Functions.checkStringNull(req.getParameter("icici"));
        String fromdate = Functions.checkStringNull(req.getParameter("fromdate"));
        String todate = Functions.checkStringNull(req.getParameter("todate"));
        String login = Functions.checkStringNull(req.getParameter("username"));
        String contactpersonname = Functions.checkStringNull(req.getParameter("contact_persons"));
        String contactpersonemail = Functions.checkStringNull(req.getParameter("contact_emails"));
        String country = Functions.checkStringNull(req.getParameter("country"));
        dateType = Functions.checkStringNull(req.getParameter("datetype"));
       /* String fmonth = Functions.checkStringNull(req.getParameter("fmonth"));
        String tmonth = Functions.checkStringNull(req.getParameter("tmonth"));
        String fyear = Functions.checkStringNull(req.getParameter("fyear"));
        String tyear = Functions.checkStringNull(req.getParameter("tyear"));*/
        String partnerid = String.valueOf(session.getAttribute("merchantid"));
        String pid= Functions.checkStringNull(req.getParameter("spid"));
        //System.out.println("partnerid in export---"+partnerid);

        Calendar rightNow = Calendar.getInstance();

        /*if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

        if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
        if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);

        if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);*/
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date=null;

        //int pageno = 1;
        //int records = 100000;

        try
        {

            date=sdf.parse(fromdate);
            rightNow.setTime(date);
            String fdate=String.valueOf(rightNow.get(Calendar.DATE));
            String fmonth=String.valueOf(rightNow.get(Calendar.MONTH));
            String fyear=String.valueOf(rightNow.get(Calendar.YEAR));

            //to Date
            date=sdf.parse(todate);
            rightNow.setTime(date);
            String tdate=String.valueOf(rightNow.get(Calendar.DATE));
            String tmonth=String.valueOf(rightNow.get(Calendar.MONTH));
            String tyear=String.valueOf(rightNow.get(Calendar.YEAR));

            String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
            String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");

            Hashtable transactionHash = null;
            Hashtable hash = listMembers(toid,company_name,sitename,activation,icici,dateType,fdtstamp,tdtstamp,login,contactpersonname,contactpersonemail,country,ignoredates,partnerid,pid);
            int totalRecords = Integer.parseInt((String) hash.get("records"));
            String exportPath = ApplicationProperties.getProperty("EXPORT_FILE_LOCAL_PATH");
            String fileName = "Merchant_List-" +  "- PartnerId " + partnerid + ".csv";
            PrintWriter writer = new PrintWriter(new FileOutputStream(exportPath + "/" + fileName));
            //printCriteria(writer, fdtstamp, tdtstamp, status, desc, orderdesc, archive ? "Archives" : "Current", trackingid,statushash,accountid);
            printHeader(writer);
            for (int pos = 1; pos <= totalRecords; pos++)
            {
                transactionHash = (Hashtable) hash.get(pos + "");
                printTransaction(writer, transactionHash);
            }
            writer.close();
            writer = null;

            sendFile(exportPath + "/" + fileName, fileName, res);
            return;
        }
        catch (SystemError systemError)
        {
            log.error("System Error while getting sending file",systemError);
        }
        catch (Exception e)
        {
            log.error("Exception while ExportMemberDetails",e);

        }

    }

    private void printHeader(PrintWriter writer)
    {
        print(writer, "Activation Date (mm/dd/yyyy hh:mm:ss)");
        print(writer, "Partner ID");
        print(writer, "Member ID");
        print(writer, "Creation Date");
        print(writer, "Company_name");
        print(writer, "Contact_persons");
        print(writer, "Contact_emails");
        print(writer, "Country ");
        print(writer, "Telno");
        print(writer, "Sitename");
        /*print(writer, "Agent Name");*/
        print(writer, "Partner Name");
        print(writer, "Merchant Interface Access");
        printLast(writer, "Activation");

    }

    private void printTransaction(PrintWriter writer, Hashtable transactionHash)
    {
        print(writer, Functions.convertDtstampToDBFormat((String) transactionHash.get("activation_date")== null ? "-" : (String) transactionHash.get("activation_date")));
        print(writer, (String) transactionHash.get("partnerId"));
        print(writer, (String) transactionHash.get("memberid"));
        print(writer, Functions.convertDtstampToDBFormat((String)transactionHash.get("dtstamp")));
        print(writer, (String) transactionHash.get("company_name"));
        print(writer, (String) transactionHash.get("contact_persons"));
        print(writer, (String)transactionHash.get("contact_emails"));
        print(writer, (String) transactionHash.get("country"));
        print(writer, (String)transactionHash.get("telno"));
        print(writer, (String) transactionHash.get("sitename"));
       /* print(writer, (String) transactionHash.get("agentName"));*/
        print(writer, (String) transactionHash.get("partnerName"));
        print(writer, (String) transactionHash.get("icici"));
        printLast(writer, (String) transactionHash.get("activation"));
        /*print(writer, getPartnerName((String) transactionHash.get("partnerId")));
        printLast(writer, getAgentName((String) transactionHash.get("agentId")));
*/
    }

    void print(PrintWriter writer, String str)
    {
        writer.print("\"" + Util.replaceData(str, "\"", "\"\"") + "\"");
        writer.print(',');
    }

    void printLast(PrintWriter writer, String str)
    {
        writer.println("\"" + Util.replaceData(str, "\"", "\"\"") + "\"");
    }

    public Hashtable listMembers(String memberid, String company_name, String sitename, String activation, String icici,String dateType, String fdtstamp, String tdtstamp,String login,String contactpersonname,String contactpersonemail,String country, String ignoredates,String partnerid, String pid) throws SystemError
    {
        log.debug("Entering listMembers");

        Hashtable hash = null;
        Functions functions = new Functions();
        Connection cn=null;
        ResultSet rs = null;
        try
        {
            Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
            //StringBuffer query = new StringBuffer("select login,memberid,company_name,contact_persons,contact_emails,sitename,activation,icici,partnerId,agentId,country,telno from members where 1=1 ");
            StringBuffer query = new StringBuffer("SELECT m.login,m.memberid,m.partnerId,m.timestmp,m.dtstamp,m.activation_date,m.company_name,m.contact_persons,m.contact_emails,m.sitename,m.activation,m.icici,p.partnerName,m.country,m.telno FROM members AS m, agents AS a, partners AS p WHERE 1=1 AND p.partnerId=m.partnerId AND a.agentId=m.agentId");
            StringBuffer countquery = new StringBuffer("select count(*) from members as m ,partners as p where 1=1 and p.partnerId=m.partnerId");
            StringBuffer condQuery = new StringBuffer();
            if (functions.isValueNull(pid))
            {
                condQuery.append(" and m.partnerId='" + ESAPI.encoder().encodeForSQL(me, pid) + "'");
            }
            else {
                condQuery.append(" and (m.partnerId='" + ESAPI.encoder().encodeForSQL(me, partnerid) + "'");
                condQuery.append(" OR p.superadminid='" + ESAPI.encoder().encodeForSQL(me, partnerid) + "')");
            }

            if (functions.isValueNull(memberid))
            {
                condQuery.append(" and m.memberid='" + ESAPI.encoder().encodeForSQL(me, memberid) + "'");
            }

            if (functions.isValueNull(company_name))
            {
                condQuery.append(" and m.company_name='" + ESAPI.encoder().encodeForSQL(me,company_name) + "'");
            }

            if (functions.isValueNull(sitename))
            {

                condQuery.append(" and m.sitename='" + ESAPI.encoder().encodeForSQL(me,sitename) + "'");

            }
            if (functions.isValueNull(activation))
            {
                condQuery.append(" and m.activation='" + ESAPI.encoder().encodeForSQL(me, activation) + "'");
            }
            if (functions.isValueNull(icici))
            {
                condQuery.append(" and m.icici='" + ESAPI.encoder().encodeForSQL(me, icici) + "'");
            }
            if ("creation_date".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                condQuery.append(" and m.dtstamp>= " + ESAPI.encoder().encodeForSQL(me, fdtstamp));
            }
            else
            {
                condQuery.append(" and activation_date >='"+fdtstamp+"'");
            }

            if ("creation_date".equals(dateType) && functions.isValueNull(tdtstamp))
            {
                condQuery.append(" and m.dtstamp<= " + ESAPI.encoder().encodeForSQL(me, tdtstamp));
            }
            else
            {
                condQuery.append(" and activation_date <= '"+tdtstamp+"'");
                condQuery.append(" AND activation_date !='null'");
            }
            if (functions.isValueNull(login))
            {
                condQuery.append(" and m.login='" + ESAPI.encoder().encodeForSQL(me, login) + "'");
            }
            if (functions.isValueNull(contactpersonname))
            {
                condQuery.append(" and m.contact_persons='" + ESAPI.encoder().encodeForSQL(me, contactpersonname) + "'");
            }

            if (functions.isValueNull(contactpersonemail))
            {
                condQuery.append(" and m.contact_emails='" + ESAPI.encoder().encodeForSQL(me, contactpersonemail) + "'");
            }
            if (functions.isValueNull(country))
            {
                condQuery.append(" and m.country='" + ESAPI.encoder().encodeForSQL(me, country) + "'");
            }

            query.append(condQuery);
            query.append(" order by m.memberid DESC ");
            countquery.append(condQuery);
            // cn= Database.getConnection();
            cn= Database.getRDBConnection();
            hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), cn));
            rs = Database.executeQuery(countquery.toString(), cn);
            rs.next();
            int totalrecords = rs.getInt(1);
            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
            {
                hash.put("records", "" + (hash.size() - 2));
            }

        }catch (SQLException se){
            throw new SystemError(se.toString());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closeConnection(cn);
        }

        log.debug("Leaving listMembers");
        return hash;
    }
}
