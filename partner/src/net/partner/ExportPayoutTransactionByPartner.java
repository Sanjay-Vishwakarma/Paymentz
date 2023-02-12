package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionEntry;
import com.logicboxes.util.ApplicationProperties;
import com.logicboxes.util.Util;

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
 * Created by Sanjay on 12/15/2021.
 */


public class ExportPayoutTransactionByPartner extends HttpServlet
{

    private static Logger log = new Logger(ExportPayoutTransactionByPartner.class.getName());

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException
    {
        log.debug("inside ExportPayoutTransaction by partner ");
//        System.out.println("inside ExportPayoutTransaction by partner ");
        HttpSession session = req.getSession();
        Functions functions = new Functions();
        PartnerFunctions partner = new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }
        String merchantid = (String) session.getAttribute("merchantid");
        String pid = Functions.checkStringNull(req.getParameter("pid"));
        String trackingid = Functions.checkStringNull(req.getParameter("trackingid"));
        String desc = Functions.checkStringNull(req.getParameter("desc"));
        String emailAddress = Functions.checkStringNull(req.getParameter("emailaddr"));
        String accountid = Functions.checkStringNull(req.getParameter("accountid"));
        String bankaccount = Functions.checkStringNull(req.getParameter("bankaccount"));
        String startTime    = Functions.checkStringNull(req.getParameter("starttime"));
        String endTime      = Functions.checkStringNull(req.getParameter("endtime"));
        String amount = Functions.checkStringNull(req.getParameter("amount"));
        String toId             = Functions.checkStringNull(req.getParameter("memberid"));
        String role         = Functions.checkStringNull(req.getParameter("role"));
        String dateType     = Functions.checkStringNull(req.getParameter("datetype"));
        String status     = Functions.checkStringNull(req.getParameter("status"));



        StringBuffer trackingIds = new StringBuffer();

        if (functions.isValueNull(trackingid))
        {
            List<String> trackingidList = null;
            if (trackingid.contains(","))
            {
                trackingidList = Arrays.asList(trackingid.split(","));
            }
            else
            {
                trackingidList = Arrays.asList(trackingid.split(" "));
            }

            int i = 0;
            Iterator itr = trackingidList.iterator();
            while (itr.hasNext())
            {
                if (i != 0)
                {
                    trackingIds.append(",");
                }
                trackingIds.append("" + itr.next() + "");
                i++;
            }
        }

        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdftimezone = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        String fyear = "";
        String fmonth = "";
        String fdate = "";
        String tyear = "";
        String tmonth = "";
        String tdate = "";

        int pageno = 1;
        int records = 10000;

        try
        {

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

                String fromdate = req.getParameter("fromdate");
                String todate   = req.getParameter("todate");

                date    = sdf.parse(fromdate);
                rightNow.setTime(date);
                fdate   = String.valueOf(rightNow.get(Calendar.DATE));
                fmonth  = String.valueOf(rightNow.get(Calendar.MONTH));
                fyear   = String.valueOf(rightNow.get(Calendar.YEAR));

                //to Date
                date    = sdf.parse(todate);
                rightNow.setTime(date);
                tdate   = String.valueOf(rightNow.get(Calendar.DATE));
                tmonth  = String.valueOf(rightNow.get(Calendar.MONTH));
                tyear   = String.valueOf(rightNow.get(Calendar.YEAR));


            String startTimeArr[]   = startTime.split(":");
            String endTimeArr[]     = endTime.split(":");

            log.debug("From date dd::"+fdate+" MM;;"+fmonth+" YY::"+ fyear + " To date dd::" + tdate + " MM::" + tmonth+" YY::"+tyear);

            String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, startTimeArr[0], startTimeArr[1], startTimeArr[2]);
            String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, endTimeArr[0], endTimeArr[1], endTimeArr[2]);

            String memberid     = "";
            String partnerId    = "";
            Hashtable hash1              = null;
            if(functions.isValueNull(pid) && partner.isPartnerSuperpartnerMapped(pid,merchantid)){
                memberid    = partner.getPartnerMemberRS(pid);
                partnerId   = pid;
                hash1       = partner.getPartnerNameFromPartnerId(pid, req.getParameter("memberid"));
            }else if (!functions.isValueNull(pid)){
                memberid    = partner.getSuperpartnersMemberRS(merchantid);
                partnerId   = merchantid;
                hash1       = partner.getPartnerNameFromPartnerIdAndSuperPartnerId(partnerId, req.getParameter("memberid"));
            }
            if(functions.isValueNull(toId) && memberid.contains(toId))
            {
                memberid = toId;
            }
            else if(functions.isValueNull(toId) && !memberid.contains(toId)){
                memberid = "0";
            }
            String partnerName  = "";
            StringBuffer sb     = new StringBuffer();
            if(hash1.size()>0)
            {

                Enumeration enu3    = hash1.keys();
                String key3         = "";
                while (enu3.hasMoreElements())
                {
                    key3        = (String) enu3.nextElement();
                    partnerName = (String) hash1.get(key3);
                    sb.append("'"+partnerName+"'");
                    sb.append(",");
                }
                if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ',')
                {
                    partnerName = sb.substring(0, sb.length() - 1);
                }
            }
            log.error("Export  partnerName"+partnerName);


            TransactionEntry transactionentry   = (TransactionEntry) session.getAttribute("transactionentry");
            Set<String> gatewayTypeSet          = new HashSet();
            if (functions.isValueNull(memberid) && functions.isValueNull(accountid))
            {
                gatewayTypeSet.addAll(transactionentry.getGatewayHashByPartnerMembers(memberid, accountid).keySet());
            }
            SortedMap statushash    = transactionentry.getSortedMap();


            Hashtable hash = transactionentry.listForPayoutTransaction(desc,toId,tdtstamp,fdtstamp,trackingIds.toString(),status,records,pageno,accountid,emailAddress,amount,bankaccount,dateType,pid );
            log.error("hash while export----"+hash);

            int totalRecords            = Integer.parseInt((String) hash.get("records"));
            Hashtable transactionHash   = null;

            String exportPath   = ApplicationProperties.getProperty("EXPORT_FILE_LOCAL_PATH");
            String fileName     = "Transactions-" + partnerId + "- from " + fdate + "-" + (Integer.parseInt(fmonth) + 1) + "-" + fyear + " to " + tdate + "-" + (Integer.parseInt(tmonth) + 1) + "-" + tyear + ".csv";
            OutputStream os     = new FileOutputStream(exportPath + "/" + fileName);
            os.write(239);//239 EF
            os.write(187);//187 BB
            os.write(191);//191 BF
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(os,"UTF-8"));
            printCriteria(writer, fdtstamp, tdtstamp, partnerId,status,desc, trackingid,statushash,accountid);
            printHeader(writer, req);

            for (int pos = 1; pos <= totalRecords; pos++)
            {
                transactionHash = (Hashtable) hash.get(pos + "");
                printTransaction(writer, transactionHash, statushash, role);
            }

            writer.close();
            sendFile(exportPath + "/" + fileName, fileName, res);
            return;


        }
        catch (Exception e)
        {
            log.error("inside catch block" + e);
            Functions.NewShowConfirmation1("Error!", "Invalid Transaction");

        }
    }

    private void printTransaction(PrintWriter writer, Hashtable transactionHash, SortedMap statushash,String Roles) throws Exception
    {
        Functions functions = new Functions();
//        System.out.println("transactionhass----"+transactionHash);
//        log.debug("transaction hashhhh---"+transactionHash);

        String transid="";
        if(functions.isValueNull((String) transactionHash.get("transid")))
        {
            transid="'"+(String)transactionHash.get("transid");
        }
        else
        {
            transid="-";
        }

        String description="";
        if (functions.isValueNull((String)transactionHash.get("description")))
        {
            description= "'"+(String)transactionHash.get("description");
        }
        else
        {
            description="-";
        }
        String ifsc="";
        if (functions.isValueNull((String)transactionHash.get("ifsc")))
        {
            ifsc= "'"+(String)transactionHash.get("ifsc");
        }
        else
        {
            ifsc="-";
        }
        String bankaccount="";
        if (functions.isValueNull((String)transactionHash.get("bankaccount")))
        {
            bankaccount= "'"+(String)transactionHash.get("bankaccount");
        }
        else
        {
            bankaccount="-";
        }
        String accountid="";
        if(functions.isValueNull((String)transactionHash.get("accountid")))
        {
            accountid="'"+(String)transactionHash.get("accountid");
        }else{
            accountid="-";
        }


        String customermail =(String) transactionHash.get("emailaddr");
        String print_email = "";

        if(Roles.contains("superpartner")){
            print_email =customermail;
        }else{

            print_email=functions.getEmailMasking(customermail);
        }
        print(writer, Functions.convertDtstampToDBFormat((String) transactionHash.get("dtstamp")));
        print(writer, (String) transactionHash.get("partnerId"));
        print(writer, (String) transactionHash.get("toid"));
        print(writer, (String) transactionHash.get("totype")==null ? "-":(String)transactionHash.get("totype") );
        print(writer, transid);
        print(writer, description);
        print(writer, (String) transactionHash.get("terminalid")==null ? "-":(String)transactionHash.get("terminalid") );
        print(writer,accountid);
        print(writer, (String) transactionHash.get("amount")==null ? "-":(String)transactionHash.get("amount"));
        print(writer, (String) transactionHash.get("currency")==null  ? "-":(String)transactionHash.get("currency"));
        print(writer, print_email == null ? "-" : print_email);
        print(writer, "'"+(String) transactionHash.get("fullname")==null ? "-":(String)transactionHash.get("fullname"));
        print(writer,bankaccount);
        print(writer, ifsc);
        print(writer, (String) statushash.get(transactionHash.get("status")));
        print(writer, (String) transactionHash.get("remark") == null ? "-" : (String) transactionHash.get("remark"));

//        print(writer, (String) transactionHash.get("fromtype")==null ? "-":(String) transactionHash.get("fromtype"));
//        print(writer, transactionHash.get("name") == null ? "-" : (String) transactionHash.get("name"));
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
    private void printCriteria(PrintWriter writer, String fromDate, String toDate, String partnerId, String status, String description, String trackingid, SortedMap statushash, String accountid)
    {
        printLast(writer, "Report Criteria");
        print(writer, "Start Date");
        printLast(writer, Functions.convertDtstampToDBFormat(fromDate));
        print(writer, "End Date");
        printLast(writer, Functions.convertDtstampToDBFormat(toDate));
        print(writer, "Partner ID");
        printLast(writer, partnerId);
        print(writer, "Status");
        printLast(writer, Functions.checkStringNull(status) == null ? "All" : (String) statushash.get(status));
        print(writer, "Description");
        printLast(writer, "'" + (Functions.checkStringNull(description) == null ? "" : description));
        print(writer, "Tracking ID");
        printLast(writer, "'" + (Functions.checkStringNull(trackingid) == null ? "" : trackingid));
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

    public static boolean sendFile(String filepath, String filename, javax.servlet.http.HttpServletResponse response) throws Exception
    {

        File f = new File(filepath);
        int length = 0;

        // Set browser download related headers (Browsers to download the file)
        response.setContentType("application/octat-stream");
        response.setContentLength((int) f.length());
        response.addHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        //For Sending binary data to the client. This object normally retrieved via the response
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
        try
        {
            file.delete();
        }
        catch (Exception e)
        {
            log.debug("Exception:::::" + e);
        }
        log.info("Successful#######");
        return true;

    }

    public String formatDate(String date, String initDateFormat, String endDateFormat) throws ParseException
    {

        Date initDate = new SimpleDateFormat(initDateFormat).parse(date);
        SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat);
        String parsedDate = formatter.format(initDate);
        return parsedDate;
    }

}

