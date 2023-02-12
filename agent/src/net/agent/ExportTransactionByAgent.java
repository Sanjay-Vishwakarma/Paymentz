package net.agent;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionEntry;
import com.logicboxes.util.ApplicationProperties;
import com.logicboxes.util.Util;
import org.owasp.esapi.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Diksha on 23-Mar-21.
 */
public class ExportTransactionByAgent extends HttpServlet
{
    private static Logger log = new Logger(ExportTransactionByAgent.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        log.debug("Inside ExportTransactionByAgent");
        AgentFunctions agent = new AgentFunctions();
        Functions functions = new Functions();
        TransactionEntry transactionentry = new TransactionEntry();
        if (!agent.isLoggedInAgent(session))
        {
            res.sendRedirect("/agent/Logout.jsp");
            return;
        }
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        log.debug("CSRF check successful ");

        String agentid = (String) session.getAttribute("merchantid");
        String merchantid= req.getParameter("memberid");
        String partnerid = (String) session.getAttribute("partnerid");
        String currency = (String) session.getAttribute("currency");
        String trackingid = Functions.checkStringNull(req.getParameter("trackingid"));
        String status = Functions.checkStringNull(req.getParameter("status"));
        String accountid = Functions.checkStringNull(req.getParameter("accountid"));
        String cardpresent = Functions.checkStringNull(req.getParameter("cardtype"));
        boolean archive = Boolean.valueOf(req.getParameter("archive"));
        String desc = Functions.checkStringNull(req.getParameter("desc"));
        String fromdate =req.getParameter("fromdate");
        String todate =req.getParameter("todate");

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

        String currency1 = "";
        String gateway_name = "";

        if (req.getParameter("pgtypeid") != null && req.getParameter("pgtypeid").split("-").length == 3
                && !req.getParameter("pgtypeid").equalsIgnoreCase(""))
        {
            currency1 = req.getParameter("pgtypeid").split("-")[1];
            gateway_name = req.getParameter("pgtypeid").split("-")[0];
        }

        Calendar rightNow            = Calendar.getInstance();
        SimpleDateFormat sdf         = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdftimezone = new SimpleDateFormat("yyyy-MM-dd");
        Date date       = null;
        String fyear    = "";
        String fmonth   = "";
        String fdate    = "";
        String tyear    = "";
        String tmonth   = "";
        String tdate    = "";
        String startTime    = "";
        String endTime    = "";

        int pageno  = 1;
        int records = 10000;

        try
        {
            startTime = "00:00:00";

            endTime = "23:59:59";

            date = sdf.parse(fromdate);
            rightNow.setTime(date);
            fdate = String.valueOf(rightNow.get(Calendar.DATE));
            fmonth = String.valueOf(rightNow.get(Calendar.MONTH));
            fyear = String.valueOf(rightNow.get(Calendar.YEAR));

            //to Date
            date = sdf.parse(todate);
            rightNow.setTime(date);
            tdate = String.valueOf(rightNow.get(Calendar.DATE));
            tmonth = String.valueOf(rightNow.get(Calendar.MONTH));
            tyear = String.valueOf(rightNow.get(Calendar.YEAR));

            String startTimeArr[] = startTime.split(":");
            String endTimeArr[] = endTime.split(":");

            log.debug("From date dd::" + fdate + " MM;;" + fmonth + " YY::" + fyear + " To date dd::" + tdate + " MM::" + tmonth + " YY::" + tyear);

            String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, startTimeArr[0], startTimeArr[1], startTimeArr[2]);
            String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, endTimeArr[0], endTimeArr[1], endTimeArr[2]);

            Set<String> gatewayTypeSet = new HashSet();
            gatewayTypeSet.addAll(transactionentry.getGatewayHashFromAgent(merchantid,agentid).keySet());

            if(!functions.isValueNull(merchantid))
            {
                merchantid = agent.getAgentMemberList(agentid);
            }

            SortedMap statushash    = transactionentry.getSortedMap();
            Hashtable hash          = null;

            if(cardpresent.equals("CP")){
                hash = transactionentry.listForAgentCardPresentTransactions(tdtstamp, fdtstamp, status, desc, merchantid, archive, trackingIds.toString(), currency, records, pageno, gatewayTypeSet, accountid, gateway_name, currency1);
            }else
            {
                hash = transactionentry.listForAgentTransactions(tdtstamp, fdtstamp, status, desc, merchantid, archive, trackingIds.toString(), currency, records, pageno, gatewayTypeSet, accountid, gateway_name, currency1);
            }

            int totalRecords            = Integer.parseInt((String) hash.get("records"));
            Hashtable transactionHash   = null;
            String firstSix             = "";
            String lastFour             = "";
            //Hashtable innerHash = (Hashtable) hash.get("1");
            String exportPath   = ApplicationProperties.getProperty("EXPORT_FILE_LOCAL_PATH");
            String fileName     = "Transactions-" + agentid + "- from " + fdate + "-" + (Integer.parseInt(fmonth) + 1) + "-" + fyear + " to " + tdate + "-" + (Integer.parseInt(tmonth) + 1) + "-" + tyear + ".csv";
            OutputStream os     = new FileOutputStream(exportPath + "/" + fileName);
            os.write(239);//239 EF
            os.write(187);//187 BB
            os.write(191);//191 BF
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(os,"UTF-8"));
            printCriteria(writer, fdtstamp, tdtstamp, agentid, status, desc, archive ? "Archives" : "Current", trackingid, statushash);
            printHeader(writer, currency, req);

            for (int pos = 1; pos <= totalRecords; pos++)
            {
                transactionHash = (Hashtable) hash.get(pos + "");
                printTransaction(writer, transactionHash, statushash, firstSix, lastFour);
            }

            writer.close();
            sendFile(exportPath + "/" + fileName, fileName, res);
            return;
        }
        catch (ParseException e)
        {
            log.error("SQLException In ExportTransactionByAgent ", e);
        }
        catch (SQLException e)
        {
            log.error("SQLException In ExportTransactionByAgent ", e);
        }
        catch (SystemError systemError)
        {
            log.error("SystemError In ExportTransactionByAgent ", systemError);
        }
        catch (Exception e)
        {
            log.error("Exception In ExportTransactionByAgent ", e);
        }

    }

    private void printCriteria(PrintWriter writer, String fromDate, String toDate, String agentid, String status, String description, String dataSource, String trackingid,SortedMap statushash)
    {
        printLast(writer, "Report Criteria");
        print(writer, "Start Date");
        printLast(writer, Functions.convertDtstampToDBFormat(fromDate));
        print(writer, "End Date");
        printLast(writer, Functions.convertDtstampToDBFormat(toDate));
        print(writer, "Agent ID");
        printLast(writer, agentid);
        print(writer, "Status");
        printLast(writer, Functions.checkStringNull(status) == null ? "All" : (String) statushash.get(status));
        print(writer, "Description");
        printLast(writer, "'"+(Functions.checkStringNull(description) == null ? "" : description));
        print(writer, "Data Source");
        printLast(writer, dataSource);
        print(writer, "Tracking ID");
        printLast(writer, "'"+(Functions.checkStringNull(trackingid) == null ? "" : trackingid));
        printLast(writer, "");
        printLast(writer, "Transactions Report");
    }

    private void printHeader(PrintWriter writer, String currency, HttpServletRequest req)
    {
        Functions functions = new Functions();

        print(writer, " Transaction Date(MM/DD/YYYY)");
        print(writer,"Partner ID");
        print(writer,"Member ID");
        print(writer,"Merchant Company Name");
        print(writer, "Tracking ID");
        print(writer, "Payment ID");
        print(writer,"Order ID");
        print(writer, "Order Description");
        print(writer, "Customer ID");
        print(writer, "Acquirer Bank");
        print(writer, "Acquirer Account ID");
        print(writer, "Terminal ID");
        print(writer,"Payment Mode");
        print(writer,"Payment Brand");
        print(writer,"Transaction Mode");
        print(writer,"Bin Card Category");
        print(writer,"Bin Card Type");
        print(writer,"Bin Sub Brand");
        print(writer,"Sub Card Type");
        print(writer,"Issuing Bank Name");
        print(writer,"ISO Country");
        print(writer,"Currency");
        print(writer,"walletAmount");
        print(writer,"walletCurrency");
        print(writer,"Card Holder's Name");
        print(writer,"Customer Email");
        print(writer,"Transaction Country");
        print(writer,"Auth Amount");
        print(writer,"Captured Amount");
        print(writer,"Refund Amount");
        print(writer,"Chargeback Amount");
        print(writer,"Payout Amount");
        print(writer,"First Six");
        print(writer,"Last four");
        print(writer,"RRN");
        print(writer,"ARN");
        print(writer,"Authorization Code");
        print(writer,"Status");
        print(writer,"Reason");
        print(writer,"Remark");
        printLast(writer, "Last Update Date(MM/DD/YYYY)");
    }

    private void printTransaction(PrintWriter writer, Hashtable transactionHash, SortedMap statushash, String firstSix, String lastFour) throws Exception
    {
        Functions functions = new Functions();
        String dt = Functions.convertDtstampToDateTimeforTimezone((String) transactionHash.get("dtstamp"));

        String paymentid = "";
        if (functions.isValueNull((String)transactionHash.get("paymentid")))
        {
            paymentid = "'"+(String)transactionHash.get("paymentid");
        }
        else
        {
            paymentid = "-";
        }
        String paymodeid = "";
        if (!functions.isValueNull((String)transactionHash.get("paymodeid")) || "0".equals((String)transactionHash.get("paymodeid")))
        {
            paymodeid = "-";
        }
        else
        {
            paymodeid = (String) transactionHash.get("paymodeid");
        }
        String trackingid= "";
        if (functions.isValueNull((String)transactionHash.get("transid")))
        {
            trackingid = "'"+ (String)transactionHash.get("transid");
        }
        else
        {
            trackingid ="-";
        }
        String orderid= "";
        if (functions.isValueNull((String)transactionHash.get("description")))
        {
            orderid = "'"+ (String)transactionHash.get("description");
        }
        else
        {
            orderid= "-";
        }
        String orderdescription = "";
        if (functions.isValueNull((String)transactionHash.get("orderdescription")))
        {
            orderdescription= "'"+ (String)transactionHash.get("orderdescription");
        }
        else
        {
            orderdescription= "-";
        }
        String customerId="";
        if (functions.isValueNull((String)transactionHash.get("customerId")))
        {
            customerId= "'"+ (String)transactionHash.get("customerId");
        }
        else
        {
            customerId= "-";
        }
        String firstsix="";
        if (functions.isValueNull((String)transactionHash.get("first_six")))
        {
            firstsix= "'"+ (String)transactionHash.get("first_six");
        }
        else
        {
            firstsix="-";
        }
        String lastfour= "";
        if (functions.isValueNull((String)transactionHash.get("last_four")))
        {
            lastfour= "'"+ (String)transactionHash.get("last_four");
        }
        else
        {
            lastfour = "-";
        }
        String rrn="";
        if (functions.isValueNull((String)transactionHash.get("rrn")))
        {
            rrn= "'"+(String)transactionHash.get("rrn");
        }
        else
        {
            rrn="-";
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
        String authorization_code= "";
        if (functions.isValueNull((String)transactionHash.get("authorization_code")))
        {
            authorization_code= "'"+(String)transactionHash.get("authorization_code");
        }
        else
        {
            authorization_code= "-";
        }
        String print_email =(String) transactionHash.get("emailaddr");

        print(writer, Functions.convertDtstampToDBFormat((String) transactionHash.get("dtstamp")));
        print(writer, (String) transactionHash.get("partnerId"));
        print(writer, (String) transactionHash.get("memberid"));
        print(writer, (String) transactionHash.get("company_name")==null ? "-":(String)transactionHash.get("company_name") );
        print(writer, trackingid);
        print(writer, paymentid);
        print(writer, orderid);
        print(writer, orderdescription);
        print(writer, customerId);
        print(writer, (String) transactionHash.get("fromtype")==null ? "-":(String) transactionHash.get("fromtype"));
        print(writer, (String) transactionHash.get("accountid")==null ? "-":(String) transactionHash.get("accountid"));
        print(writer, (String) transactionHash.get("terminalid")==null ? "-":(String)transactionHash.get("terminalid") );
        print(writer, paymodeid);
        print(writer, transactionHash.get("cardtype") == null ? "-" : (String) transactionHash.get("cardtype"));
        print(writer, transactionHash.get("transaction_mode") == null ? "-" : (String) transactionHash.get("transaction_mode"));
        print(writer, transactionHash.get("bin_card_category") == null ? "-" : (String) transactionHash.get("bin_card_category"));
        print(writer, transactionHash.get("bin_card_type") == null ? "-" : (String) transactionHash.get("bin_card_type"));
        print(writer, transactionHash.get("bin_sub_brand") == null ? "-" : (String) transactionHash.get("bin_sub_brand"));
        print(writer, transactionHash.get("subcard_type") == null ? "-" : (String) transactionHash.get("subcard_type"));
        print(writer, transactionHash.get("issuing_bank") == null ? "-" : (String) transactionHash.get("issuing_bank"));
        print(writer, transactionHash.get("country_name") == null ? "-" : (String) transactionHash.get("country_name"));
        print(writer, (String) transactionHash.get("currency"));
        print(writer, (String) transactionHash.get("walletAmount")==null ? "-":(String) transactionHash.get("walletAmount"));
        print(writer, (String) transactionHash.get("walletCurrency")==null ? "-":(String) transactionHash.get("walletCurrency"));
        print(writer, transactionHash.get("name") == null ? "-" : (String) transactionHash.get("name"));
        print(writer, print_email == null ? "-" : print_email);
        print(writer, transactionHash.get("country") == null ? "-" : (String) transactionHash.get("country"));
        print(writer, (String) transactionHash.get("amount"));
        print(writer, (String) transactionHash.get("captureamount"));
        print(writer, (String) transactionHash.get("refundamount"));
        print(writer, (String) transactionHash.get("chargebackamount"));
        print(writer, (String) transactionHash.get("payoutamount"));
       /* String encCardNum = (String) transactionHash.get("ccnum");
        if (functions.isValueNull(encCardNum))
        {
            firstSix = encCardNum.substring(0, 6);
            lastFour = encCardNum.substring((encCardNum.length() - 4), encCardNum.length());
        }else {
            firstSix = "-";
            lastFour = "-";
        }*/


        print(writer, firstsix);
        print(writer, lastfour);
        print(writer, rrn);
        print(writer, arn);
        print(writer, authorization_code);
        print(writer, (String) statushash.get(transactionHash.get("status")));
        print(writer,  transactionHash.get("chargebackinfo") == null? "-" : (String) transactionHash.get("chargebackinfo"));
        print(writer, (String) transactionHash.get("remark") == null ? "-" : (String) transactionHash.get("remark"));
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

    void print(PrintWriter writer, String str)
    {
        writer.print("\"" + Util.replaceData(str, "\"", "\"\"") + "\"");
        writer.print(',');
    }

    void printLast(PrintWriter writer, String str)
    {
        writer.println("\"" + Util.replaceData(str, "\"", "\"\"") + "\"");
    }

    public static boolean sendFile(String filepath, String filename, javax.servlet.http.HttpServletResponse response)throws Exception
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
            log.debug("Exception:::::"+e);
        }
        log.info("Successful#######");
        return true;

    }

    public String formatDate (String date, String initDateFormat, String endDateFormat) throws ParseException
    {

        Date initDate = new SimpleDateFormat(initDateFormat).parse(date);
        SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat);
        String parsedDate = formatter.format(initDate);
        return parsedDate;
    }

}