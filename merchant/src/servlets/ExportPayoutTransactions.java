
import com.directi.pg.*;
import com.logicboxes.util.ApplicationProperties;
import com.logicboxes.util.Util;
import com.manager.vo.TerminalVO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Sanjay on 1/3/2022.
 */
public class ExportPayoutTransactions extends HttpServlet
{

    private static Logger log = new Logger(ExportPayoutTransactions.class.getName());

    public static boolean sendFile(String filepath, String filename, javax.servlet.http.HttpServletResponse response) throws Exception
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

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException
    {
        doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException
    {

        HttpSession session = req.getSession();
        Merchants merchants = new Merchants();
        Hashtable hash = new Hashtable();


        if (!merchants.isLoggedIn(session))
        {
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        Functions functions = new Functions();
        String accountid = Functions.checkStringNull(req.getParameter("accountid"));
        String desc = Functions.checkStringNull(req.getParameter("desc"));
        String trackingid = Functions.checkStringNull(req.getParameter("trackingid"));
        String status = Functions.checkStringNull(req.getParameter("status"));
        String dateType = Functions.checkStringNull(req.getParameter("datetype"));
        String amount = Functions.checkStringNull(req.getParameter("amount"));
        String bankaccount = Functions.checkStringNull(req.getParameter("bankaccount"));
        String emailAddress = Functions.checkStringNull(req.getParameter("emailaddr"));
        String fromdate = Functions.checkStringNull(req.getParameter("fdate"));
        String todate = Functions.checkStringNull(req.getParameter("tdate"));
        String startTime = req.getParameter("starttime");
        String endTime = req.getParameter("endtime");
        String ifsc = "";
        String fullname = "";
        String currency = "";
        String remark="";
        String partnerName="";
        String partnerid="";

        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        String merchantid = (String) session.getAttribute("merchantid");

        int pageno = 1;
        int records = 10000;

        try
        {

            String fyear = "";
            String fmonth = "";
            String fdate = "";
            String tyear = "";
            String tmonth = "";
            String tdate = "";

            startTime = startTime.trim();
            endTime = endTime.trim();

            if (!functions.isValueNull(startTime))
            {
                startTime = "00:00:00";
            }
            if (!functions.isValueNull(endTime))
            {
                endTime = "23:59:59";
            }

            //from date
            date    = sdf.parse(fromdate);
            rightNow.setTime(date);
            fdate   = String.valueOf(rightNow.get(Calendar.DATE));
            fmonth  = String.valueOf(rightNow.get(Calendar.MONTH));
            fyear   = String.valueOf(rightNow.get(Calendar.YEAR));

            //to Date
            date = sdf.parse(todate);
            rightNow.setTime(date);
            tdate   = String.valueOf(rightNow.get(Calendar.DATE));
            tmonth  = String.valueOf(rightNow.get(Calendar.MONTH));
            tyear   = String.valueOf(rightNow.get(Calendar.YEAR));

            log.debug("startTime::" + startTime);
            log.debug("endTime::" + endTime);


            String startTimeArr[]   = startTime.split(":");
            String endTimeArr[]     = endTime.split(":");

            log.debug("From date dd::"+fdate+" MM;;"+fmonth+" YY::"+ fyear + " To date dd::" + tdate + " MM::" + tmonth+" YY::"+tyear);

            String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, startTimeArr[0], startTimeArr[1], startTimeArr[2]);
            String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, endTimeArr[0], endTimeArr[1], endTimeArr[2]);

            TransactionEntry transactionentry   = (TransactionEntry) session.getAttribute("transactionentry");
            SortedMap statushash = transactionentry.getSortedMap();
            TerminalVO terminalVO = new TerminalVO();
            hash = transactionentry.payoutTransactionLists(desc, status, tdtstamp, fdtstamp, trackingid, accountid,emailAddress, dateType, terminalVO, pageno, records, bankaccount,fullname,ifsc,amount,currency, remark,partnerName,partnerid,merchantid);

            log.error("export excel ===" + hash);
            int totalRecords            = Integer.parseInt((String) hash.get("records"));
            Hashtable transactionHash   = null;

            String exportPath   = ApplicationProperties.getProperty("EXPORT_FILE_LOCAL_PATH");
            String fileName     = "Transactions-" + merchantid + "- from " + fdate + "-" + (Integer.parseInt(fmonth) + 1) + "-" + fyear + " to " + tdate + "-" + (Integer.parseInt(tmonth) + 1) + "-" + tyear + ".csv";
            OutputStream os     = new FileOutputStream(exportPath + "/" + fileName);
            os.write(239);//239 EF
            os.write(187);//187 BB
            os.write(191);//191 BF
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(os,"UTF-8"));
            printCriteria(writer, fdtstamp, tdtstamp,status,desc, trackingid,statushash,accountid);
            printHeader(writer, req);

            for (int pos = 1; pos <= totalRecords; pos++)
            {
                transactionHash = (Hashtable) hash.get(pos + "");
                printTransaction(writer, transactionHash, statushash);
            }

            writer.close();
            writer = null;

            sendFile(exportPath + "/" + fileName, fileName, res);
            return;


        }catch(SystemError se){
            log.error("Error",se);
            Functions.NewShowConfirmation1("Error!", "Invalid Transaction");
        }

        catch (Exception e)
        {
            log.error("Error",e);
            Functions.NewShowConfirmation1("Error!", "Invalid Transaction");
        }

    }
    private void printTransaction(PrintWriter writer, Hashtable transactionHash, SortedMap statushash) throws Exception
    {
        Functions functions = new Functions();
        log.debug("transaction hashhhh---"+transactionHash);

        String transid="";
        if(functions.isValueNull((String) transactionHash.get("transid")))
        {
            transid="'"+transactionHash.get("transid");
        }
        else
        {
            transid="-";
        }

        String description="";
        if (functions.isValueNull((String)transactionHash.get("description")))
        {
            description= "'"+transactionHash.get("description");
        }
        else
        {
            description="-";
        }
        String ifsc="";
        if (functions.isValueNull((String)transactionHash.get("ifsc")))
        {
            ifsc= "'"+transactionHash.get("ifsc");
        }
        else
        {
            ifsc="-";
        }
        String bankaccount="";
        if (functions.isValueNull((String)transactionHash.get("bankaccount")))
        {
            bankaccount= "'"+transactionHash.get("bankaccount");
        }
        else
        {
            bankaccount="-";
        }
        String accountid="";
        if(functions.isValueNull((String)transactionHash.get("accountid")))
        {
            accountid="'"+transactionHash.get("accountid");
        }else{
            accountid="-";
        }

        String customermail =(String) transactionHash.get("emailaddr");
        print(writer, Functions.convertDtstampToDBFormat((String) transactionHash.get("dtstamp")));
        print(writer, (String) transactionHash.get("partnerid"));
        print(writer, (String) transactionHash.get("toid"));
        print(writer,  transactionHash.get("totype")==null ? "-":(String)transactionHash.get("totype") );
        print(writer, transid);
        print(writer, description);
        print(writer,  transactionHash.get("terminalid")==null ? "-":(String)transactionHash.get("terminalid") );
        print(writer,accountid);
        print(writer,  transactionHash.get("amount")==null ? "-":(String)transactionHash.get("amount"));
        print(writer,  transactionHash.get("currency")==null  ? "-":(String)transactionHash.get("currency"));
        print(writer, customermail == null ? "-" : customermail);
        print(writer,  transactionHash.get("fullname")==null ? "-":(String)transactionHash.get("fullname"));
        print(writer,bankaccount);
        print(writer, ifsc);
        print(writer, (String) statushash.get(transactionHash.get("status")));
        print(writer,  transactionHash.get("remark") == null ? "-" : (String) transactionHash.get("remark"));

        String initDateFormat = "yyyy-MM-dd HH:mm:ss";
        String targetDateFormat = "MM/dd/yyyy HH:mm:ss";
        String parsedDate ="" ;
        try
        {
            parsedDate = formatDate((String) transactionHash.get("timestamp"), initDateFormat, targetDateFormat);
        }
        catch (ParseException e)
        {
            log.error("ParseException---",e);
        }
        printLast(writer,(parsedDate));
    }

    private void printHeader(PrintWriter writer, HttpServletRequest req)
    {

        print(writer, " Transaction Date(MM/DD/YYYY)");
        print(writer,"Partner ID");
        print(writer,"Member ID");
        print(writer, "Partner Name");
        print(writer, "Tracking ID");
        print(writer, "Description");
        print(writer, "Terminal ID");
        print(writer,"Account Id");
        print(writer,"Amount");
        print(writer,"Currency");
        print(writer,"Customer Email");
        print(writer,"Beneficiery Name");
        print(writer,"Bank Account Number");
        print(writer,"Bank IFSC");
        print(writer,"Status");
        print(writer,"Remark");

        printLast(writer, "Last Update Date(MM/DD/YYYY)");
    }
    private void printCriteria(PrintWriter writer, String fromDate, String toDate,  String status, String description, String trackingid, SortedMap statushash, String accountid)
    {
        printLast(writer, "Report Criteria");
        print(writer, "Start Date");
        printLast(writer, Functions.convertDtstampToDBFormat(fromDate));
        print(writer, "End Date");
        printLast(writer, Functions.convertDtstampToDBFormat(toDate));
        print(writer, "Status");
        if("0".equals(status)) {
            printLast(writer, Functions.checkStringNull(status) == null ? "" : "All");
        }else {
            printLast(writer, Functions.checkStringNull(status) == null ? "" : (String) statushash.get(status));
        }
        print(writer, "Tracking ID");
        printLast(writer, "'" + (Functions.checkStringNull(trackingid) == null ? "" : trackingid));
        print(writer, "Description");
        printLast(writer, Functions.checkStringNull(description) == null ? "" : description);
        print(writer, "Account Id");
        printLast(writer, Functions.checkStringNull(accountid) == null ? "" : accountid);
        printLast(writer, "");
        printLast(writer, "Transactions Report");
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
    public String formatDate(String date, String initDateFormat, String endDateFormat) throws ParseException
    {

        Date initDate = new SimpleDateFormat(initDateFormat).parse(date);
        SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat);
        String parsedDate = formatter.format(initDate);
        return parsedDate;
    }
}
