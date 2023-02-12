import com.directi.pg.*;
import com.directi.pg.core.GatewayTypeService;
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
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 3/5/14
 * Time: 1:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExportShippingTransaction extends HttpServlet
{
    static Logger log = new Logger(ExportShippingTransaction.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        HttpSession session = req.getSession();

        //PrintWriter printwriter = res.getWriter();
        if (!Admin.isLoggedIn(session))
        {
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        log.debug("Inside export to excel");
        String toid= Functions.checkStringNull(req.getParameter("toid"));
        String trackingid=Functions.checkStringNull(req.getParameter("trackingid"));
        String gateway=Functions.checkStringNull(req.getParameter("gateway"));
        String desc = Functions.checkStringNull(req.getParameter("desc"));
        String fromid = Functions.checkStringNull(req.getParameter("fromid"));
        String fdate = Functions.checkStringNull(req.getParameter("fdate"));
        String tdate = Functions.checkStringNull(req.getParameter("tdate"));
        String fmonth = Functions.checkStringNull(req.getParameter("fmonth"));
        String tmonth = Functions.checkStringNull(req.getParameter("tmonth"));
        String fyear = Functions.checkStringNull(req.getParameter("fyear"));
        String tyear = Functions.checkStringNull(req.getParameter("tyear"));

        Calendar rightNow = Calendar.getInstance();
        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

        if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
        if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);

        if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);

        String fdtstamp=  Functions.converttomillisec(fmonth, fdate, fyear,"0", "0", "0");
        String tdtstamp= Functions.converttomillisec(tmonth, tdate, tyear,"23", "59", "59");
        req.setAttribute("fdtstamp", fdtstamp);
        req.setAttribute("tdtstamp", tdtstamp);

        int pageno = 1;
        int records = 100000;

        try
        {


            TransactionEntry transactionentry = new TransactionEntry();
            Hashtable hash = listTransactions(tdtstamp,fdtstamp,toid,trackingid,desc,fromid,gateway);
            int totalRecords = Integer.parseInt((String) hash.get("records"));
            Hashtable transactionHash = null;

            String exportPath = ApplicationProperties.getProperty("EXPORT_FILE_LOCAL_PATH");
            String fileName = "Transactions_Shipping_Details.csv";
            //System.out.println("=1======fileName====="+fileName);

            PrintWriter writer = new PrintWriter(new FileOutputStream(exportPath + "/" + fileName));

            printCriteria(writer ,fdtstamp,tdtstamp);
            // printHeader(writer, currency);
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
        catch (SystemError se)
        {
            log.error("Error",se);
            Functions.ShowMessage("Error!", "Invalid Transection");
        }
        catch (Exception e)
        {
            log.error("Error",e);
            Functions.ShowMessage("Error!", "Invalid Transection");
        }
    }

    private void printCriteria(PrintWriter writer, String fromDate, String toDate)
    {
        printLast(writer, "Shipping Transactions Report");
        print(writer, "Start Date");
        printLast(writer, Functions.convertDtstamptoDate(fromDate));
        print(writer, "End Date");
        printLast(writer, Functions.convertDtstamptoDate(toDate));
    }

    private void printHeader(PrintWriter writer)
    {
        print(writer, "Tracking ID");
        print(writer, "Member ID");
        print(writer, "fromid");
        print(writer, "Description");
        print(writer, "amount");
        print(writer, "status");
        print(writer, "Card Holder's Name");
        print(writer, "paymentId");
        print(writer, "POD");
        printLast(writer, "Site Address");
    }

    private void printTransaction(PrintWriter writer, Hashtable transactionHash)
    {
        print(writer, (String) transactionHash.get("trackingid"));
        print(writer, (String) transactionHash.get("toid"));
        print(writer, (String)transactionHash.get("fromid"));
        print(writer, (String)transactionHash.get("description"));
        print(writer, (String) transactionHash.get("amount"));
        print(writer, (String) transactionHash.get("status"));
        print(writer, (String) transactionHash.get("name"));
        print(writer, (String) transactionHash.get("paymentid"));
        print(writer, (String) transactionHash.get("pod"));
        printLast(writer, (String) transactionHash.get("podbatch"));
    }

    public Hashtable listTransactions(String tdtstamp, String fdtstamp,String toid, String trackingid, String description, String fromid,String gateway) throws SystemError
    {
        Hashtable hash = null;

        String fields = "";
        String tablename=Database.getTableName(gateway);
        StringBuffer query = new StringBuffer();
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        fdtstamp= ESAPI.encoder().encodeForSQL(me,fdtstamp);
        tdtstamp=ESAPI.encoder().encodeForSQL(me,tdtstamp);
        //System.out.println(tablename);
        Connection conn = null;
        ResultSet rs = null;
        try
        {

                //SELECT t.trackingid AS trackingid,t.description,t.amount,t.accountid,t.status,t.dtstamp,t.paymodeid,t.cardtype,t.pod,t.podbatch FROM transaction_qwipi AS t, members AS m WHERE t.toid=m.memberid AND (t.toid=10108) AND (t.pod IS NULL OR t.podbatch IS NULL) AND (t.status='authsuccessful' OR (t.status='capturesuccess' AND m.isPODRequired=TRUE))

            fields = "t.trackingid AS trackingid,t.description,t.amount,t.accountid,t.status,(FROM_UNIXTIME(t.dtstamp)) AS dt,t.paymodeid,t.cardtype,t.pod,t.podbatch,t.fromid,t.toid,t.name";
            if(tablename.equals("transaction_qwipi"))
            {
                query.append("select " + fields + ",t.qwipiPaymentOrderNumber AS paymentid from transaction_qwipi AS t,members AS m ");
            }
            else if(tablename.equals("transaction_ecore"))
            {
                query.append("select " + fields + ",t.ecorePaymentOrderNumber AS paymentid from transaction_ecore AS t,members AS m ");
            }
            else
            {
                query.append("select " + fields + ",t.paymentid AS paymentid from transaction_common AS t,members AS m ");
            }


            query.append(" where t.toid=m.memberid AND (t.pod IS NOT NULL AND t.podbatch IS NOT NULL) AND (t.status='authsuccessful' OR (t.status='capturesuccess' AND m.isPODRequired='Y'))");       //AND m.isPODRequired=TRUE
            if (toid != null)
            {
                query.append(" AND t.toid='" + ESAPI.encoder().encodeForSQL(me,toid) + "'");
            }
            if (trackingid != null)
            {
                query.append(" AND t.trackingid='" + ESAPI.encoder().encodeForSQL(me,trackingid) + "'");
            }

            if (description != null)
            {
                query.append(" AND t.description ='" + ESAPI.encoder().encodeForSQL(me,description) + "'");
            }

            if (fromid != null)
            {
                query.append(" AND t.fromid='" + ESAPI.encoder().encodeForSQL(me,fromid) + "'");
            }
            if (gateway !=null)
            {
                query.append("  AND t.fromtype='" +gateway + "'");
            }
            if (fdtstamp != null)
            {
                query.append(" AND t.dtstamp >= " + fdtstamp);
                //System.out.println("fdtstamp======"+fdtstamp);
            }

            if (tdtstamp != null)
            {
                query.append(" AND t.dtstamp <= " + tdtstamp);
                //System.out.println("tdtstamp====="+tdtstamp);
            }
            log.error("----> "+query.toString());
            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");

            conn = Database.getConnection();
            hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), conn));
            rs = Database.executeQuery(countquery.toString(), conn);

            int totalrecords = 0;

            if (rs.next())
            {
                totalrecords = rs.getInt(1);
            }

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }

        catch (SQLException se)
        {   log.error("SQL Exception:::::",se);

            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        return hash;
    }

    public Set<String> getGatewayHash(String gateway)
    {
        Set<String> gatewaySet = new HashSet<String>();

        if(gateway==null || gateway.equals("") || gateway.equals("null"))
        {
            gatewaySet.addAll(GatewayTypeService.getGateways());
        }
        else
        {

            gatewaySet.add(GatewayTypeService.getGatewayType(gateway).getGateway());
        }


        return gatewaySet;
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
}
