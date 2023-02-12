import com.directi.pg.*;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 6/16/14
 * Time: 2:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExportMemberDetails extends HttpServlet
{
    public static Logger log = new Logger(ExportMemberDetails.class.getName());
    public boolean sendFile(String filepath, String filename, HttpServletResponse response)throws Exception
    {
        File f = new File(filepath);
        int length = 0;
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
        if (!Admin.isLoggedIn(session))
        {
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        String toid= Functions.checkStringNull(req.getParameter("toid"));
        String ignoredates= Functions.checkStringNull(req.getParameter("ignoredates"));
        String company_name=Functions.checkStringNull(req.getParameter("company_name"));
        String sitename=Functions.checkStringNull(req.getParameter("sitename"));
        String activation=Functions.checkStringNull(req.getParameter("activation"));
        String icici=Functions.checkStringNull(req.getParameter("icici"));
        String fdate = Functions.checkStringNull(req.getParameter("fdate"));
        String tdate = Functions.checkStringNull(req.getParameter("tdate"));
        String fmonth = Functions.checkStringNull(req.getParameter("fmonth"));
        String tmonth = Functions.checkStringNull(req.getParameter("tmonth"));
        String fyear = Functions.checkStringNull(req.getParameter("fyear"));
        String tyear = Functions.checkStringNull(req.getParameter("tyear"));
        String partnerId = (String) session.getAttribute("partnerId");
        String partnername =  Functions.checkStringNull(req.getParameter("partnername"));
        String username = Functions.checkStringNull(req.getParameter("login"));
        String contact_emails = Functions.checkStringNull(req.getParameter("contact_emails"));

        Calendar rightNow = Calendar.getInstance();

        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

        if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
        if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);

        if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);

        String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
        String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");

        int pageno = 1;
        int records = 100000;

        try
        {
            Hashtable transactionHash = null;
            Hashtable hash = listMembers(toid,company_name,sitename,activation,icici,fdtstamp,tdtstamp,ignoredates,partnername,username,contact_emails);
            int totalRecords = Integer.parseInt((String) hash.get("records"));
            String exportPath = ApplicationProperties.getProperty("EXPORT_FILE_LOCAL_PATH");
            String fileName = "Merchant_List-" +  "- from " + fdate + "-" + (Integer.parseInt(fmonth) + 1) + "-" + fyear + " to " + tdate + "-" + (Integer.parseInt(tmonth) + 1) + "-" + tyear + ".csv";
            PrintWriter writer = new PrintWriter(new FileOutputStream(exportPath + "/" + fileName));
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
        print(writer, "Member ID");
        print(writer, "Company_name");
        print(writer, "Contact_persons");
        print(writer, "Contact_emails");
        print(writer, "Country ");
        print(writer, "Telno");
        print(writer, "Sitename");
        /*print(writer, "Agent Name");*/
        print(writer, "Partner Name");
        print(writer, "Merchant Interface Access");
        print(writer, "Activation");
        printLast(writer, "Username");
    }

    private void printTransaction(PrintWriter writer, Hashtable transactionHash)
    {
        print(writer, Functions.convertDtstampToDBFormat((String) transactionHash.get("activation_date")== null ? "-" : (String) transactionHash.get("activation_date")));
        print(writer, (String) transactionHash.get("memberid"));
        print(writer, (String) transactionHash.get("company_name"));
        print(writer, (String) transactionHash.get("contact_persons"));
        print(writer, (String) transactionHash.get("contact_emails"));
        print(writer, (String) transactionHash.get("country"));
        print(writer, (String) transactionHash.get("telno"));
        print(writer, (String) transactionHash.get("sitename"));
        //print(writer, (String) transactionHash.get("agentName"));
        print(writer, (String) transactionHash.get("partnerName"));
        print(writer, (String) transactionHash.get("icici"));
        print(writer, (String) transactionHash.get("activation"));
        printLast(writer, (String) transactionHash.get("login"));
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

    public Hashtable listMembers(String memberid, String company_name, String sitename, String activation, String icici, String fdtstamp, String tdtstamp, String ignoredates , String partnerName ,String username,String contact_emails) throws SystemError
    {
        log.debug("Entering listMembers");
        Hashtable hash = null;
        Functions functions = new Functions();
        Connection cn=null;
        ResultSet rs = null;
        try
        {
            Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
            StringBuffer query = new StringBuffer("SELECT m.login,m.memberid,m.company_name,m.contact_persons,m.contact_emails,m.sitename,m.activation,m.icici,m.activation_date,p.partnerName,a.agentName,m.country,m.telno FROM members AS m LEFT JOIN agents AS a ON a.agentId=m.agentId LEFT JOIN partners AS p ON p.partnerId=m.partnerId WHERE 1=1");
            StringBuffer countquery = new StringBuffer("select count(*) from members as m LEFT JOIN partners AS p ON p.partnerId=m.partnerId where 1=1 ");
            StringBuffer condQuery = new StringBuffer();
            if (functions.isValueNull(memberid))
                condQuery.append(" and m.memberid='" + ESAPI.encoder().encodeForSQL(me,memberid)+"'");

            if (functions.isValueNull(company_name))
            {
                condQuery.append(" and m.company_name='" + ESAPI.encoder().encodeForSQL(me,company_name) + "'");
            }

            if (functions.isValueNull(sitename)){

                condQuery.append(" and m.sitename='" + ESAPI.encoder().encodeForSQL(me,sitename) + "'");
            }
            if (functions.isValueNull(activation))
                condQuery.append(" and m.activation='" + ESAPI.encoder().encodeForSQL(me,activation) + "'");

            if (functions.isValueNull(icici))
                condQuery.append(" and m.icici='" + ESAPI.encoder().encodeForSQL(me,icici) + "'");

            if (functions.isValueNull(partnerName))
            {
                condQuery.append(" and p.partnerName='" + ESAPI.encoder().encodeForSQL(me, partnerName) + "'");
            }
            if (functions.isValueNull(username))
            {
                condQuery.append(" and m.login='" + username + "'");
            }
            if (functions.isValueNull(contact_emails))
            {
                condQuery.append(" and m.contact_emails='" + ESAPI.encoder().encodeForSQL(me, contact_emails)+ "'");
            }

            if(ignoredates == null){
                if (functions.isValueNull(fdtstamp))
                    condQuery.append(" and m.dtstamp>=" + ESAPI.encoder().encodeForSQL(me,fdtstamp));

                if (functions.isValueNull(tdtstamp))
                    condQuery.append(" and m.dtstamp<=" + ESAPI.encoder().encodeForSQL(me,tdtstamp));
            }
            log.error("exportquery"+condQuery);
            query.append(condQuery);
            query.append(" order by m.memberid DESC ");
            countquery.append(condQuery);
            cn= Database.getConnection();
            hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), cn));
            rs = Database.executeQuery(countquery.toString(), cn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));

        }catch (SQLException se){
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(cn);
        }
        return hash;
    }

    public String getPartnerName(String id)
    {
        Connection connection=null;
        PreparedStatement pst=null;
        ResultSet resultSet=null;
        String name="";
        try
        {
            String qry="SELECT partnerName FROM partners WHERE partnerId=?";
            pst=connection.prepareStatement(qry);
            pst.setString(1,id);
            resultSet=pst.executeQuery();
            if(resultSet.next())
            {
                name=resultSet.getString("partnerName");
            }
        }
        catch (SQLException e)
        {
            log.error("Error while fetching partner Name",e);
        }
        finally {
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(pst);
            Database.closeConnection(connection);
        }
        return name;
    }
    public String getAgentName(String id)
    {
        Connection connection=null;
        PreparedStatement pst=null;
        ResultSet resultSet=null;
        String name="";
        try
        {
            String qry="SELECT agentName FROM agents WHERE agentId=?";
            pst=connection.prepareStatement(qry);
            pst.setString(1,id);
            resultSet=pst.executeQuery();
            if(resultSet.next())
            {
                name=resultSet.getString("partnerName");
            }
        }
        catch (SQLException e)
        {
            log.error("Error while fetching partner Name",e);
        }
        finally {
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(pst);
            Database.closeConnection(connection);
        }
        return name;
    }
}