import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.logicboxes.util.ApplicationProperties;
import com.logicboxes.util.Util;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
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
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: May 22, 2011
 * Time: 12:29:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class ExportTransactions extends HttpServlet
{

    private static Logger log = new Logger(ExportTransactions.class.getName());

    /**
     * To send file to browser for download.. <BR>
     * first set content type to "application/octat-stream" so that browser invokes download dialog. Then
     * set Content-Disposition as "filename=".."" , so that filename to save as appears in the download dialog.<BR><BR>
     * <p/>
     * Then read file in bunch of 1024 bytes and send it to the end client.
     */
    public static boolean sendFile(String filepath, String filename, javax.servlet.http.HttpServletResponse response) throws Exception
    {

        File f      = new File(filepath);
        int length  = 0;

        // Set browser download related headers
        response.setContentType("application/octat-stream");
        response.setContentLength((int) f.length());
        response.addHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        javax.servlet.ServletOutputStream op = response.getOutputStream();

        byte[] bbuf         = new byte[1024];
        DataInputStream in  = new DataInputStream(new FileInputStream(f));

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
        Merchants merchants = new Merchants();
        Hashtable hash      = new Hashtable();

        if (!merchants.isLoggedIn(session))
        {
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        ServletContext ctx = getServletContext();
        Functions functions = new Functions();

        String terminalId       = Functions.checkStringNull(req.getParameter("terminalid"));
        String terminalBuffer   = Functions.checkStringNull(req.getParameter("terminalbuffer"));
        String accountid        = Functions.checkStringNull(req.getParameter("bank"));
        String desc             = Functions.checkStringNull(req.getParameter("desc"));
        String orderDescription = Functions.checkStringNull(req.getParameter("orderdescription"));
        String trackingid       = Functions.checkStringNull(req.getParameter("trackingid"));
        String customerId       = Functions.checkStringNull(req.getParameter("customerid"));
        String status           = Functions.checkStringNull(req.getParameter("status"));
        String timestamp        = Functions.checkStringNull(req.getParameter("timestamp"));
        String dateType         = Functions.checkStringNull(req.getParameter("datetype"));
        String firstSix         = Functions.checkStringNull(req.getParameter("firstsix"));
        String lastFour         = Functions.checkStringNull(req.getParameter("lastfour"));
        String firstName        = Functions.checkStringNull(req.getParameter("firstname"));
        String lastName         = Functions.checkStringNull(req.getParameter("lastname"));
        String emailAddress     = Functions.checkStringNull(req.getParameter("emailaddr"));
        String statusflag       = Functions.checkStringNull(req.getParameter("statusflag"));
        String cardtype         = Functions.checkStringNull(req.getParameter("cardtype"));

        String fromdate         = Functions.checkStringNull(req.getParameter("fdate"));
        String todate           = Functions.checkStringNull(req.getParameter("tdate"));
        String startTime        = req.getParameter("starttime");
        String endTime          = req.getParameter("endtime");
        String issuingBank      = req.getParameter("issuingbank");
        String timezone         = req.getParameter("timezone");
        String transactionMode  = req.getParameter("transactionMode");
        if (functions.isValueNull(timezone))
        {
            timezone = timezone.substring(0,timezone.indexOf("|"));
        }
        if (!functions.isValueNull(transactionMode))
        {
            transactionMode ="";
        }


        boolean archive         = Boolean.valueOf(req.getParameter("archive")).booleanValue();
        Calendar rightNow       = Calendar.getInstance();
        SimpleDateFormat sdf    = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdftimezone    = new SimpleDateFormat("yyyy-MM-dd");
        Date date                       = null;
        String merchantid               = (String) session.getAttribute("merchantid");
       // String currency = (String) session.getAttribute("currency");
        String currency = "";

       // String gateway_name = "";

       /* if (req.getParameter("pgtypeid")!=null && req.getParameter("pgtypeid").split("-").length == 3 && !req.getParameter("pgtypeid").equalsIgnoreCase(""))
        {
            currency = req.getParameter("pgtypeid").split("-")[1];
            gateway_name = req.getParameter("pgtypeid").split("-")[0];

        }*/

        int pageno = 1;
        int records = 10000;

        try
        {
            String fyear    = "";
            String fmonth   = "";
            String fdate    = "";
            String tyear    = "";
            String tmonth   = "";
            String tdate    = "";
            String successtimestamp = "";
            String failuretimestamp = "";
            String refundtimestamp  = "";
            String payouttimestamp  = "";
            String chargebacktimestamp  = "";

            startTime   = startTime.trim();
            endTime     = endTime.trim();

            if (!functions.isValueNull(startTime))
            {
                startTime   = "00:00:00";
            }
            if (!functions.isValueNull(endTime))
            {
                endTime = "23:59:59";
            }
             if (functions.isValueNull(timezone))
                {
                    /*String fromdate = req.getParameter("fdate");
                    String todate = req.getParameter("tdate");*/
                    //from date
                    Date frtzdate       = sdf.parse(fromdate);
                    String fdatetime    = sdftimezone.format(frtzdate)+ " " + startTime;
                    //to Date
                    Date totzdate       = sdf.parse(todate);
                    String tdatetime    = sdftimezone.format(totzdate) + " " + endTime;
                    String fdatetime1   = functions.convertDateTimeToTimeZone1(fdatetime, timezone);
                    String tdatetime1   = functions.convertDateTimeToTimeZone1(tdatetime, timezone);
                    String fdt[]        = fdatetime1.split(" ");
                    String tdt[]        = tdatetime1.split(" ");
                    date                = sdftimezone.parse(fdt[0]);
                    rightNow.setTime(date);
                    fdate       = String.valueOf(rightNow.get(Calendar.DATE));
                    fmonth      = String.valueOf(rightNow.get(Calendar.MONTH));
                    fyear       = String.valueOf(rightNow.get(Calendar.YEAR));
                    startTime   = fdt[1];
                    date        = sdftimezone.parse(tdt[0]);
                    rightNow.setTime(date);
                    tdate       = String.valueOf(rightNow.get(Calendar.DATE));
                    tmonth      = String.valueOf(rightNow.get(Calendar.MONTH));
                    tyear       = String.valueOf(rightNow.get(Calendar.YEAR));
                    endTime     = tdt[1];
                }
                else
                {

                 /*   String fromdate = req.getParameter("fdate");
                    String todate = req.getParameter("tdate");*/
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

                }

            /*Functions functions=new Functions();
            if (functions.isValueNull(startTime)){
                startTime=startTime.trim();
            }
            else{
                startTime="00:00:00";
            }

            if (functions.isValueNull(endTime)){
                endTime = endTime.trim();
            }else{
                endTime="23:59:59";d
            }*/

            String startTimeArr[]   = startTime.split(":");
            String endTimeArr[]     = endTime.split(":");

            log.debug("From date dd::"+fdate+" MM;;"+fmonth+" YY::"+ fyear + " To date dd::" + tdate + " MM::" + tmonth+" YY::"+tyear);

            String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, startTimeArr[0], startTimeArr[1], startTimeArr[2]);
            String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, endTimeArr[0], endTimeArr[1], endTimeArr[2]);

            TransactionEntry transactionentry   = (TransactionEntry) session.getAttribute("transactionentry");
            Set<String> gatewayTypeSet          = new HashSet();
            gatewayTypeSet.addAll(transactionentry.getGatewayHash(merchantid,accountid).keySet());

            /*TerminalVO terminalVO = null;
            TerminalManager terminalManager = new TerminalManager();
            if (functions.isValueNull(terminalId) && !terminalId.equalsIgnoreCase("all"))
            {
                terminalVO = terminalManager.getTerminalByTerminalId(terminalId);
                terminalVO.setTerminalId("(" + terminalVO.getTerminalId() + ")");
                accountid = terminalVO.getAccountId();
            }
            else
            {
                terminalVO = new TerminalVO();
                terminalVO.setTerminalId(terminalBuffer.toString());
            }*/

            SortedMap statushash = transactionentry.getSortedMap();
            //Hashtable hash = transactionentry.listTransactionsNew(desc, tdtstamp, fdtstamp, trackingid, status,timestamp, records, pageno, archive, gatewayTypeSet, firstName, lastName, emailAddress,dateType,customerId, terminalId, statusflag, issuingBank,firstSix,lastFour);
            if (functions.isValueNull(cardtype) && cardtype.equals("CP"))
            {
                // Entering for Card present (transaction_card_present)
                hash = transactionentry.listPresentCardTransactions(desc, tdtstamp, fdtstamp, trackingid, status, timestamp, records, pageno, archive, gatewayTypeSet, firstName, lastName, emailAddress, dateType, customerId, terminalId, statusflag, issuingBank, firstSix, lastFour,successtimestamp,failuretimestamp,refundtimestamp,payouttimestamp,chargebacktimestamp);
            }
            else
            {
                // Entering for Card not present(transaction_common)
                hash = transactionentry.listTransactionsNew(desc, tdtstamp, fdtstamp, trackingid, status, timestamp, records, pageno, archive, gatewayTypeSet, firstName, lastName, emailAddress, dateType, customerId, terminalId, statusflag, issuingBank, firstSix, lastFour,transactionMode,successtimestamp,failuretimestamp,refundtimestamp,payouttimestamp,chargebacktimestamp);
            }
            int totalRecords            = Integer.parseInt((String) hash.get("records"));
            Hashtable transactionHash   = null;

            String exportPath   = ApplicationProperties.getProperty("EXPORT_FILE_LOCAL_PATH");
            String fileName     = "Transactions-" + merchantid + "- from " + fdate + "-" + (Integer.parseInt(fmonth) + 1) + "-" + fyear + " to " + tdate + "-" + (Integer.parseInt(tmonth) + 1) + "-" + tyear + ".csv";
            OutputStream os     = new FileOutputStream(exportPath + "/" + fileName);
            os.write(239);//239 EF
            os.write(187);//187 BB
            os.write(191);//191 BF
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(os,"UTF-8"));

            printCriteria(writer, fdtstamp, tdtstamp, status, desc,orderDescription, archive ? "Archives" : "Current", trackingid,statushash,accountid,transactionMode);
            printHeader(writer, currency, req);

            for (int pos = 1; pos <= totalRecords; pos++)
            {
                transactionHash = (Hashtable) hash.get(pos + "");
                printTransaction(writer, transactionHash, statushash,firstSix, lastFour, timezone);
            }

            writer.close();
            writer = null;

            sendFile(exportPath + "/" + fileName, fileName, res);
            return;

        }
        catch (SystemError se)
        {
            log.error("Error",se);
            Functions.NewShowConfirmation1("Error!", "Invalid Transaction");
        }
        catch (Exception e)
        {
            log.error("Error",e);
            Functions.NewShowConfirmation1("Error!", "Invalid Transaction");
        }
    }

    private void printCriteria(PrintWriter writer, String fromDate, String toDate, String status, String description,String orderDescription, String dataSource, String trackingid,SortedMap statushash,String accountid,String transactionMode)
    {
        printLast(writer, "Report Criteria");
        print(writer, "Start Date");
        printLast(writer, Functions.convertDtstampToDBFormat(fromDate));
        print(writer, "End Date");
        printLast(writer, Functions.convertDtstampToDBFormat(toDate));
        print(writer, "Tracking ID");
        printLast(writer, "'"+(Functions.checkStringNull(trackingid) == null ? "" : trackingid));
        print(writer, "Status");
        printLast(writer, Functions.checkStringNull(status)==null?"All":(String) statushash.get(status));
        print(writer, "Description");
        printLast(writer, Functions.checkStringNull(description)==null?"":description);
        print(writer, "Order Description");
        printLast(writer, Functions.checkStringNull(orderDescription)==null?"":orderDescription);
        print(writer, "Bank");
        printLast(writer, Functions.checkStringNull(accountid)==null?"All":GatewayAccountService.getGatewayAccount(accountid).getDisplayName());
        print(writer, "Data Source");
        printLast(writer, dataSource);
        print(writer, "Transaction Mode");
        printLast(writer, transactionMode.equals("") ? "All" : transactionMode);
        printLast(writer, "");
        printLast(writer, "Transactions Report");

    }

    private void printHeader(PrintWriter writer, String currency, HttpServletRequest req)
    {
        Functions functions = new Functions();
        String timezone = "";
        if (functions.isValueNull(req.getParameter("timezone")))
        {
            timezone = req.getParameter("timezone");
        }
        print(writer, " Transaction Date(MM/DD/YYYY)");
        print(writer, "TimeZone("+timezone+")");
        print(writer,"Member ID");
        print(writer, "Tracking ID");
        //print(writer,"Payment ID");
        print(writer,"Order ID");
        print(writer, "Order Description");
        print(writer, "Customer ID");
        print(writer, "Processing Bank");
        print(writer, "Terminal ID");
        print(writer,"Payment Mode");
        print(writer,"Payment Brand");
        print(writer,"Currency");
        print(writer,"Transaction Mode");
        print(writer,"walletAmount");
        print(writer,"walletCurrency");
        print(writer,"Issuing Bank");
        print(writer,"Card Holder's Name");
        print(writer,"Customer Email");
        print(writer,"Auth Amount");
        print(writer,"Captured Amount");
        print(writer,"Refund Amount");
        print(writer,"Payout Amount");
        print(writer,"First Six");
        print(writer,"Last four");
        print(writer, "ARN");
        print(writer, "RRN");
        print(writer,"Status");
        print(writer,"Reason");
        print(writer,"Remark");
        print(writer,"Success Time Stamp");
        print(writer,"Failure Time Stamp");
        print(writer,"Refund Time Stamp");
        print(writer,"Payout Time Stamp");
        print(writer,"Chargeback Time Stamp");
        printLast(writer, "Last Update Date(MM/DD/YYYY)");
    }

    private void printTransaction(PrintWriter writer, Hashtable transactionHash, SortedMap statushash, String firstSix, String lastFour, String timezone) throws Exception
    {
        Functions functions     = new Functions();
        String tz               = "-";
        String dt               = (String) transactionHash.get("timestamp");
        String transactionMode  = (String) transactionHash.get("transaction_mode");

        if (functions.isValueNull(timezone))
        {
            tz = functions.convertDateTimeToTimeZone(dt, timezone);
        }
        else
        {
            tz = "-";
        }

        if(!functions.isValueNull(transactionMode)){
            transactionMode = "-";
        }
        String customerId   = "";
        if(functions.isValueNull((String) transactionHash.get("customerId")))
        {
            customerId  = "'"+(String)transactionHash.get("customerId");
        }
        else
        {
            customerId  = "-";
        }
        String processingbank = "";
        if(functions.isValueNull((String) transactionHash.get("processingbank")))
        {
            processingbank = "'"+(String)transactionHash.get("processingbank");
        }
        else
        {
            processingbank = "-";
        }

        print(writer, Functions.convertDtstampToDBFormat((String) transactionHash.get("dtstamp")));

        print(writer, tz);
        print(writer, (String) transactionHash.get("toid"));
        print(writer, ("'"+((String) transactionHash.get("transid"))));
       /* String paymentid=(String) transactionHash.get("paymentid");
        if (functions.isValueNull(paymentid))
        {
            print(writer, "'"+paymentid+"'");
        }
        else
        {
            print(writer, paymentid==null ? "-": paymentid );
        }*/
        String Description  = "";
        if (functions.isValueNull((String) transactionHash.get("description")))
        {
            Description="'"+(String)transactionHash.get("description");
        }
        else
        {
            Description="-";
        }

        String orderDescription = "";
        if (functions.isValueNull((String)transactionHash.get("orderdescription")))
        {
            orderDescription="'"+(String)transactionHash.get("orderdescription");
        }
        else
        {
            orderDescription="-";
        }
        String arn="";
        if (functions.isValueNull((String)transactionHash.get("arn")))
        {
            arn= "'"+(String)transactionHash.get("arn");
        }
        else
        {
            arn= "-";
        }
        String rrn="";
        if (functions.isValueNull((String)transactionHash.get("rrn")))
        {
            rrn= "'"+ (String)transactionHash.get("rrn");
        }
        else
        {
            rrn= "-";
        }
        print(writer, Description);
        print(writer, orderDescription);
        print(writer, transactionHash.get("customerId") == null ? "-" : functions.maskVpaAddress((String) transactionHash.get("customerId")));
       // print(writer, customerId);
        print(writer, processingbank);
        print(writer, (String) transactionHash.get("terminalid")==null ? "-":(String) transactionHash.get("terminalid"));
        print(writer, (String)transactionHash.get("paymodeid") == null ? "-" : (String) transactionHash.get("paymodeid"));
        print(writer, transactionHash.get("cardtype") == null ? "-" : (String) transactionHash.get("cardtype"));
        print(writer, (String) transactionHash.get("currency"));
        print(writer, transactionMode);
        print(writer, (String) transactionHash.get("walletAmount") == null ? "-":(String) transactionHash.get("walletAmount"));
        print(writer, (String) transactionHash.get("walletCurrency") == null ? "-":(String) transactionHash.get("walletCurrency"));
        print(writer, (String) transactionHash.get("issuing_bank")== null ? "-" : (String) transactionHash.get("issuing_bank"));
        print(writer, transactionHash.get("name") == null ? "-" : (String) transactionHash.get("name"));
        print(writer, transactionHash.get("emailaddr") == null ? "-" : functions.getEmailMasking((String) transactionHash.get("emailaddr")));
        print(writer, (String) transactionHash.get("amount"));
        print(writer, (String) transactionHash.get("captureamount"));
        print(writer, (String) transactionHash.get("refundamount"));
        print(writer, (String) transactionHash.get("payoutamount"));
        String encCardNum = (String) transactionHash.get("ccnum");

        String lastFourBin      = (String) transactionHash.get("last_four");
        String firstSixBin      = (String) transactionHash.get("first_six");
        /*if (functions.isValueNull(encCardNum))
        {
            firstSix = encCardNum.substring(0, 6);
            lastFour = encCardNum.substring((encCardNum.length() - 4), encCardNum.length());
        }*//*else{
            firstSix = "-";
            lastFour = "-";
        } *//*
        String firstsixvar="";
        if (functions.isValueNull(firstSix))
        {
           firstsixvar="'"+firstSix;
        }
        else
        {
           firstsixvar="-";
        }
        String lastfourvar="";
        if (functions.isValueNull(lastFour))
        {
            lastfourvar="'"+lastFour;
        }
        else
        {
           lastfourvar="-";
        }*/

        if (functions.isValueNull(lastFourBin))
        {
            lastFourBin="'"+lastFourBin;
        }
        else
        {
            lastFourBin="-";
        }
        if (functions.isValueNull(firstSixBin))
        {
            firstSixBin="'"+firstSixBin;
        }
        else
        {
            firstSixBin="-";
        }


        /*print(writer, firstsixvar);
        print(writer, lastfourvar);*/

        print(writer, firstSixBin);
        print(writer, lastFourBin);
        print(writer, arn);
        print(writer, rrn);
        print(writer, (String) statushash.get(transactionHash.get("status")));
        print(writer,  transactionHash.get("chargebackinfo") == null ? "-":(String) transactionHash.get("chargebackinfo"));
        print(writer, (String) transactionHash.get("remark") == null ? "-" : (String) transactionHash.get("remark"));
        print(writer, (String) transactionHash.get("successtimestamp") == null ? "-" : "'"+(String) transactionHash.get("successtimestamp"));
        print(writer, (String) transactionHash.get("failuretimestamp") == null ? "-" : "'"+(String) transactionHash.get("failuretimestamp"));
        print(writer, (String) transactionHash.get("refundtimestamp") == null ? "-" :"'"+ (String) transactionHash.get("refundtimestamp"));
        print(writer, (String) transactionHash.get("payouttimestamp") == null ? "-" :"'"+ (String) transactionHash.get("payouttimestamp"));
        print(writer, (String) transactionHash.get("chargebacktimestamp") == null ? "-" : "'"+(String) transactionHash.get("chargebacktimestamp"));
        String initDateFormat   = "yyyy-MM-dd HH:mm:ss";
        String targetDateFormat = "MM/dd/yyyy HH:mm:ss";
        String parsedDate       = "" ;
        try
        {
            parsedDate = formatDate((String) transactionHash.get("timestamp"), initDateFormat, targetDateFormat);
        }
        catch (ParseException e)
        {
            log.error("ParseException---",e);
        }
        printLast(writer,(parsedDate));
        //printLast(writer,((String) transactionHash.get("timestamp")));

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

    public String formatDate (String date, String initDateFormat, String endDateFormat) throws ParseException
    {

        Date initDate = new SimpleDateFormat(initDateFormat).parse(date);
        SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat);
        String parsedDate = formatter.format(initDate);
        return parsedDate;
    }
}
