import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayTypeService;
import com.logicboxes.util.ApplicationProperties;
import com.logicboxes.util.Util;
import com.manager.TransactionManager;
import com.manager.dao.PartnerDAO;
import org.owasp.esapi.User;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
/**
 * Created by IntelliJ IDEA.
 * User: Suneeta
 * Date: Jan 14, 2019
 * Time: 12:29:24 AM
 */
public class ExportRejectedTransactionsList extends HttpServlet
{
    private static Logger log = new Logger(ExportRejectedTransactionsList.class.getName());

    /**
     * To send file to browser for download.. <BR>
     * first set content type to "application/octat-stream" so that browser invokes download dialog. Then
     * set Content-Disposition as "filename=".."" , so that filename to save as appears in the download dialog.<BR><BR>
     * <p>
     * Then read file in bunch of 1024 bytes and send it to the end client.
     */
    public static boolean sendFile(String filepath, String filename, HttpServletResponse response) throws Exception
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

        // file must be deleted after transfer...
        // caution: select to download only files which are temporarily created zip files
        // do not call this servlets with any other files which may be required later on.
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
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        Functions functions=new Functions();

        if (!Admin.isLoggedIn(session))
        {
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        String fdate = req.getParameter("fdate");
        String tdate = req.getParameter("tdate");
        String fmonth = req.getParameter("fmonth");
        String tmonth = req.getParameter("tmonth");
        String fyear = req.getParameter("fyear");
        String tyear = req.getParameter("tyear");
        String rejectReason = req.getParameter("rejectreason");
        String amount = req.getParameter("amount");
        String emailAddr = req.getParameter("emailaddr");
        String toId = "";
        if (!req.getParameter("toid").equalsIgnoreCase("0"))
        {
            toId = req.getParameter("toid");
        }
        String name = req.getParameter("name");
        String description = req.getParameter("desc");
        String partnerid = req.getParameter("partnerid");
        String partnername="";
        PartnerDAO partnerDAO = new PartnerDAO();
        if(functions.isValueNull(partnername))
        {
            partnername=partnerDAO.getPartnerName(partnerid);
        }
        String firstSix= req.getParameter("firstfourofccnum");
        String lastFour = req.getParameter("lastfourofccnum");
        String phone = req.getParameter("phone");


        Calendar rightNow = Calendar.getInstance();

        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(Calendar.DATE);

        if (fmonth == null) fmonth = "" + (rightNow.get(Calendar.MONTH));
        if (tmonth == null) tmonth = "" + (rightNow.get(Calendar.MONTH));

        if (fyear == null) fyear = "" + rightNow.get(Calendar.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(Calendar.YEAR);

        String startTime        = req.getParameter("starttime");
        String endTime          = req.getParameter("endtime");

        System.out.println("before startTime::: "+startTime+ " endTime:::: "+endTime);

        if (functions.isValueNull(startTime)){
            startTime   = startTime.trim();
        }
        else{
            startTime   = "00:00:00";
        }

        if (functions.isValueNull(endTime)){
            endTime = endTime.trim();
        }else{
            endTime = "23:59:59";
        }
        System.out.println("after startTime::: "+startTime+ " endTime:::: "+endTime);
      /*  String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
        String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");*/

        String startTimeArr[]   = startTime.split(":");
        String endTimeArr[]     = endTime.split(":");
        String fdtstamp         =  Functions.converttomillisec(fmonth, fdate, fyear, startTimeArr[0], startTimeArr[1], startTimeArr[2]);
        String tdtstamp         = Functions.converttomillisec(tmonth, tdate, tyear, endTimeArr[0], endTimeArr[1], endTimeArr[2]);

        try
        {
            TransactionEntry transactionentry = new TransactionEntry();
            SortedMap statushash = transactionentry.getSortedMap();
            Hashtable hash = listTransactions(toId, partnername, description, name, emailAddr, rejectReason, amount, firstSix, lastFour, tdtstamp, fdtstamp,phone);
            int totalRecords = Integer.parseInt((String) hash.get("records"));
            Hashtable transactionHash = null;

            String exportPath = ApplicationProperties.getProperty("EXPORT_FILE_LOCAL_PATH");
            String fileName = "/Transactions-" +  "- from " + fdate + "-" + (Integer.parseInt(fmonth) + 1) + "-" + fyear + " to " + tdate + "-" + (Integer.parseInt(tmonth) + 1) + "-" + tyear + ".csv";

            PrintWriter writer = new PrintWriter(new FileOutputStream(exportPath + fileName));

            printCriteria(writer, fdtstamp, tdtstamp,partnername,toId,statushash);
            printHeader(writer);

            for (int pos = 1; pos <= totalRecords; pos++)
            {
                transactionHash = (Hashtable) hash.get(pos + "");
                printTransaction(writer, transactionHash, statushash);
            }

            writer.close();
            sendFile(exportPath + "/" + fileName, fileName, res);
            return;

        }
        catch (Exception e)
        {
            log.error("Exception", e);
            req.setAttribute("errormessage", "Internal error while processing your request.");
            RequestDispatcher rd = req.getRequestDispatcher("/rejectedTransactionsList.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
    }
    public Hashtable listTransactions(String toId, String partnername, String description, String name, String email, String rejectReason, String amount, String firstSix, String lastFour, String tdtstamp, String fdtstamp,String phone) throws SystemError
    {
        Hashtable hash = null;
        int start = 0; // start index
        int end = 0; // end index

        Connection conn = null;
        ResultSet rs = null;
        Functions functions = new Functions();
        try
        {
            conn = Database.getConnection();
            StringBuffer query = new StringBuffer("SELECT * from transaction_fail_log where id>0 ");
            StringBuffer countquery = new StringBuffer("select count(*) from transaction_fail_log as temp where id>0  ");
            if (functions.isValueNull(amount))
            {
                query.append(" and amount = '" + amount + "'");
                countquery.append(" and amount = '" + amount + "'");
            }
            if (functions.isValueNull(email))
            {
                query.append(" and email = '" + email + "'");
                countquery.append(" and email = '" + email + "'");
            }
            if (functions.isValueNull(toId))
            {
                query.append(" and toid = " + toId);
                countquery.append(" and toid = " + toId);
            }
            if (functions.isValueNull(partnername))
            {
                query.append(" and totype = '" + partnername + "'");
                countquery.append(" and totype = '" + partnername + "'");
            }
            if (functions.isValueNull(rejectReason))
            {
                query.append(" and rejectreason = '" + rejectReason + "'");
                countquery.append(" and rejectreason = '" + rejectReason + "'");
            }
            if (functions.isValueNull(description))
            {
                query.append(" and description = '" + description + "'");
                countquery.append(" and description = '" + description + "'");
            }
            if (functions.isValueNull(name))
            {
                String arr[] = name.split(" ");
                String firstName = "";
                String lastName = "";
                try
                {
                    firstName = arr[0];
                }
                catch (ArrayIndexOutOfBoundsException e)
                {
                    firstName = "";
                }
                try
                {
                    lastName = arr[1];
                }
                catch (ArrayIndexOutOfBoundsException e)
                {
                    lastName = "";
                }
                if (functions.isValueNull(firstName))
                {
                    query.append(" and firstname = '" + firstName + "'");
                    query.append(" and lastname = '" + lastName + "'");
                }
                if (functions.isValueNull(lastName))
                {
                    countquery.append(" and firstname = '" + firstName + "'");
                    countquery.append(" and lastname = '" + lastName + "'");
                }
            }
            if (functions.isValueNull(firstSix))
            {
                query.append(" and firstsix = '" + firstSix + "'");
                countquery.append(" and firstsix = '" + firstSix+ "'");
            }
            if (functions.isValueNull(lastFour))
            {
                query.append(" and lastfour = '" + lastFour + "'");
                countquery.append(" and lastfour = '" + lastFour + "'");
            }
            if (functions.isValueNull(phone))
            {
                query.append(" and phone = '" + phone + "'");
                countquery.append(" and phone ='" + phone + "'");
            }
            if (functions.isValueNull(fdtstamp))
            {
                query.append(" and dtstamp >= '" + fdtstamp + "'");
                countquery.append(" and dtstamp >= '" + fdtstamp + "'");
            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" and dtstamp <= '" + tdtstamp + "'");
                countquery.append(" and dtstamp <= '" + tdtstamp + "'");
            }
            query.append(" order by  dtstamp DESC");
            //query.append(" limit " + start + "," + end);

            log.debug("===query===" + query);
            log.debug("===count query===" + countquery);

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));

        }
        catch (SQLException se)
        {
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        return hash;
    }

    private void printCriteria(PrintWriter writer, String fromDate, String toDate,String partnername,String toId,SortedMap statushash)
    {
        printLast(writer, "Report Criteria");
        print(writer, "Start Date");
        printLast(writer, Functions.convertDtstamptoDate(fromDate));
        print(writer, "End Date");
        printLast(writer, Functions.convertDtstamptoDate(toDate));
        print(writer, "Partner ID");
        printLast(writer, Functions.checkStringNull(partnername) == null ? "" : partnername);
        print(writer, "Merchant ID");
        printLast(writer, Functions.checkStringNull(toId) == null ? "" : toId);
        printLast(writer, "");
        printLast(writer, "Rejected Transactions Report");

    }

    private void printHeader(PrintWriter writer)
    {
        print(writer, " Transaction Date(MM/DD/YYYY)");
        print(writer,"Tracking Id");
        print(writer,"Merchant Id");
        print(writer, "ToType");
        print(writer, "Description");
        print(writer,"Amount");
        print(writer,"Remark");
        print(writer, "First Name");
        print(writer, "Last Name");
        print(writer, "First Six");
        print(writer, "Last Four");
        print(writer, "Card Type");
        print(writer, "Payment TypeID");
        print(writer, "Currency");
        print(writer, "Email");
        print(writer, "Phone Number");
        print(writer, "IP Address");
        print(writer, "Reject Reason");
        printLast(writer, "Terminal Id");

    }

    private void printTransaction(PrintWriter writer, Hashtable transactionHash, SortedMap statushash)
    {
        Functions functions=new Functions();
        String trackingid="";
        if (functions.isValueNull((String)transactionHash.get("id")))
        {
            trackingid = "'"+ (String)transactionHash.get("id");
        }
        else
        {
            trackingid = "-";
        }

        String description="";
        if (functions.isValueNull((String)transactionHash.get("description")))
        {
            description= "'"+ (String)transactionHash.get("description");
        }
        else
        {
            description= "-";
        }
        String firstsix="";
        if (functions.isValueNull((String)transactionHash.get("firstsix")))
        {
            firstsix = "'"+ (String)transactionHash.get("firstsix");
        }
        else
        {
            firstsix="-";
        }
        String lastfour="";
        if (functions.isValueNull((String)transactionHash.get("lastfour")))
        {
            lastfour= "'"+ (String)transactionHash.get("lastfour");
        }
        else
        {
            lastfour="-";
        }
        String remark="";
        if(functions.isValueNull((String)transactionHash.get("remark")))
        {
            remark=((String)transactionHash.get("remark")).replaceAll("<BR>","");
        }
        else
        {
            remark="-";
        }
        String rejectreason="";
        if(functions.isValueNull((String)transactionHash.get("rejectreason")))
        {
            rejectreason=((String)transactionHash.get("rejectreason")).replaceAll("<BR>","");
        }
        else
        {
            rejectreason="-";
        }
        String phone="";
        if (functions.isValueNull((String)transactionHash.get("phone")))
        {
            phone= "'"+ (String)transactionHash.get("phone");
        }
        else
        {
            phone= "-";
        }
        print(writer, Functions.convertDtstampToDBFormat((String) transactionHash.get("dtstamp")));
        print(writer, trackingid);
        print(writer, transactionHash.get("toid")== null ? "-" : (String) transactionHash.get("toid"));

        print(writer, transactionHash.get("totype")== null ? "-" : (String) transactionHash.get("totype"));
        print(writer, description);
        print(writer, transactionHash.get("amount")== null ? "-" : (String) transactionHash.get("amount"));
        print(writer, remark);
        print(writer, transactionHash.get("firstname")== null ? "-" : (String) transactionHash.get("firstname"));
        print(writer, transactionHash.get("lastname")== null ? "-" : (String) transactionHash.get("lastname"));
        print(writer, firstsix);
        print(writer, lastfour);
        print(writer, transactionHash.get("cardtypeid")== null ? "-" : (String) transactionHash.get("cardtypeid"));
        print(writer, transactionHash.get("paymenttypeid")== null ? "-" : (String) transactionHash.get("paymenttypeid"));
        print(writer, transactionHash.get("currency")== null ? "-" : (String) transactionHash.get("currency"));
        print(writer, transactionHash.get("email")== null ? "-" : (String) transactionHash.get("email"));
        print(writer, phone);
        print(writer, transactionHash.get("requestedip")== null ? "-" : (String) transactionHash.get("requestedip"));
        print(writer, rejectreason);
        printLast(writer, transactionHash.get("terminalid")== null ? "-" : (String) transactionHash.get("terminalid"));


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

}
