import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayTypeService;
import com.logicboxes.util.ApplicationProperties;
import com.logicboxes.util.Util;
import com.manager.TransactionManager;
import org.owasp.esapi.User;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: May 22, 2011
 * Time: 12:29:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class ExportFraudAlertTransactions extends HttpServlet
{
    private static Logger log = new Logger(ExportFraudAlertTransactions.class.getName());
    public static boolean sendFile(String filepath, String filename, HttpServletResponse response) throws Exception
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
        if (!Admin.isLoggedIn(session))
        {
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        String toid= Functions.checkStringNull(req.getParameter("toid"));
        String mid= Functions.checkStringNull(req.getParameter("mid"));
        String transactiontype= Functions.checkStringNull(req.getParameter("transactionType"));
        String amount= Functions.checkStringNull(req.getParameter("amt"));
        String refundamount= Functions.checkStringNull(req.getParameter("refundamount"));
        String trackingid=Functions.checkStringNull(req.getParameter("trackingId"));
        String paymentId = Functions.checkStringNull(req.getParameter("paymentid"));
        String terminalId = Functions.checkStringNull(req.getParameter("terminalId"));
        String currency = Functions.checkStringNull(req.getParameter("currency"));
        String firstSix = Functions.checkStringNull(req.getParameter("firstSix"));
        String lastFour = Functions.checkStringNull(req.getParameter("lastFour"));
        String authCode = Functions.checkStringNull(req.getParameter("authCode"));
        String rrn = Functions.checkStringNull(req.getParameter("rrn"));
        String isRefund = Functions.checkStringNull(req.getParameter("isRefund"));

        String fdate = Functions.checkStringNull(req.getParameter("fdate"));
        String tdate = Functions.checkStringNull(req.getParameter("tdate"));
        String fmonth = Functions.checkStringNull(req.getParameter("fmonth"));
        String tmonth = Functions.checkStringNull(req.getParameter("tmonth"));
        String fyear = Functions.checkStringNull(req.getParameter("fyear"));
        String tyear = Functions.checkStringNull(req.getParameter("tyear"));
        String accountid = Functions.checkStringNull(req.getParameter("accountid"));

        String startTime=req.getParameter("starttime");
        String endTime=req.getParameter("endtime");
        boolean archive = Boolean.valueOf(req.getParameter("archive")).booleanValue();

        Calendar rightNow = Calendar.getInstance();
        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

        if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
        if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);

        if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);

        Functions functions=new Functions();
        if (functions.isValueNull(startTime)){
            startTime=startTime.trim();
        }
        else{
            startTime="00:00:00";
        }

        if (functions.isValueNull(endTime)){
            endTime = endTime.trim();
        }else{
            endTime="23:59:59";
        }

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
        String startTimeArr[]=startTime.split(":");
        String endTimeArr[]=endTime.split(":");

        String fdtstamp=  Functions.converttomillisec(fmonth, fdate, fyear, startTimeArr[0], startTimeArr[1], startTimeArr[2]);
        String tdtstamp= Functions.converttomillisec(tmonth, tdate, tyear, endTimeArr[0], endTimeArr[1], endTimeArr[2]);

        try
        {
            Hashtable hash;
            TransactionManager transactionManager = new TransactionManager();
            hash = transactionManager.listTransactionsFraudAlertExportInExcel(toid,mid, trackingIds.toString(), terminalId,transactiontype, amount, refundamount, firstSix, lastFour, tdtstamp, fdtstamp, currency, accountid, paymentId, "", isRefund, rrn, authCode);

            int totalRecords = Integer.parseInt((String) hash.get("records"));
            Hashtable transactionHash = null;

            String exportPath = ApplicationProperties.getProperty("EXPORT_FILE_LOCAL_PATH");
            String fileName = "/Fraud_Alert_Transactions-" +  "- from " + fdate + "-" + (Integer.parseInt(fmonth) + 1) + "-" + fyear + " to " + tdate + "-" + (Integer.parseInt(tmonth) + 1) + "-" + tyear + ".csv";
            OutputStream os=new FileOutputStream(exportPath + fileName);
            os.write(239);//239 EF
            os.write(187);//187 BB
            os.write(191);//191 BF
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(os,"UTF-8"));

            printCriteria(writer, fdtstamp, tdtstamp);
            printHeader(writer);

            for (int pos = 1; pos <= totalRecords; pos++)
            {
                transactionHash = (Hashtable) hash.get(pos + "");
                printTransaction(writer, transactionHash, firstSix,lastFour);
            }

            writer.close();
            sendFile(exportPath + "/" + fileName, fileName, res);
            return;

        }
        catch (Exception e)
        {
            log.error("Exception", e);
            req.setAttribute("errormessage", "Internal error while processing your request.");
            RequestDispatcher rd = req.getRequestDispatcher("/transactions.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
    }

    private void printCriteria(PrintWriter writer, String fromDate, String toDate)
    {
        printLast(writer, "Report Criteria");
        print(writer, "Start Date");
        printLast(writer, Functions.convertDtstamptoDate(fromDate));
        print(writer, "End Date");
        printLast(writer, Functions.convertDtstamptoDate(toDate));
        printLast(writer, "");
        printLast(writer, "Transactions Report");

    }

    private void printHeader(PrintWriter writer)
    {
        print(writer, " Transaction Date(MM/DD/YYYY)");
        print(writer, "Partner ID");
        print(writer,"Member ID");
        print(writer,"Account ID");
        print(writer, "Tracking ID");
        print(writer,"Payment ID");
        print(writer, "Terminal ID");
        print(writer,"Currency");
        print(writer,"Amount");
        print(writer,"Refund Amount");
        print(writer,"First Six");
        print(writer, "Last four");
        print(writer, "Transaction Type");
        print(writer, "MID");
        print(writer, "Authorization Code");
        print(writer, "RRN");
        print(writer, "ARN");
        print(writer, "Is Refunded");
        print(writer, "Call Type");
        printLast(writer, "Card Type");
    }

    private void printTransaction(PrintWriter writer, Hashtable transactionHash,String firstSix, String lastFour)
    {
        Functions functions=new Functions();
        print(writer, Functions.convertDtstampToDBFormat((String) transactionHash.get("dtstamp")));
        print(writer, (String) transactionHash.get("partnerid")== null ? "-" : (String) transactionHash.get("partnerid"));
        print(writer, (String) transactionHash.get("toid")== null ? "-" : (String) transactionHash.get("toid"));
        print(writer, (String) transactionHash.get("accountId") == null ? "-" : (String) transactionHash.get("accountId"));
        print(writer, (String) transactionHash.get("trackingId")== null ? "-" : (String) transactionHash.get("trackingId"));
        print(writer, (String) transactionHash.get("paymentid") == null || ((String) transactionHash.get("paymentid")).equals("null") ? "-" : (String) transactionHash.get("paymentid"));
        print(writer, (String) transactionHash.get("terminalId") == null ? "-" : (String) transactionHash.get("terminalId"));
        print(writer, (String) transactionHash.get("currency") == null ? "-" : (String) transactionHash.get("currency"));
        print(writer, (String) transactionHash.get("amount") == null ? "-" : (String) transactionHash.get("amount"));
        print(writer, (String) transactionHash.get("refund_amount") == null ? "-" : (String) transactionHash.get("refund_amount"));
        String encCardNum = (String) transactionHash.get("personalAccountNumber");
        if (functions.isValueNull(encCardNum))
        {
            firstSix = encCardNum.substring(0, 6);
            lastFour = encCardNum.substring((encCardNum.length() - 4), encCardNum.length());
        }
        print(writer, firstSix);
        if (functions.isValueNull(lastFour))
        {
            print(writer, "'" + lastFour + "'");
        }
        else
        {
            print(writer, lastFour);
        }
        print(writer, (String) transactionHash.get("transactionType")== null ? "-" : (String) transactionHash.get("transactionType"));
        print(writer, (String) transactionHash.get("merchant_id")== null ? "-" : (String) transactionHash.get("merchant_id"));
        print(writer, (String) transactionHash.get("authorization_code")== null ? "-" : (String) transactionHash.get("authorization_code"));
        print(writer, (String) transactionHash.get("rrn")== null ? "-" : (String) transactionHash.get("rrn"));
        print(writer, (String) transactionHash.get("arn")== null ? "-" : (String) transactionHash.get("arn"));
        print(writer, (String) transactionHash.get("isRefunded")== null ? "-" : (String) transactionHash.get("isRefunded"));
        print(writer, (String) transactionHash.get("call_type")== null ? "-" : (String) transactionHash.get("call_type"));
        printLast(writer, (String) transactionHash.get("cardType")== null ? "-" : (String) transactionHash.get("cardType"));
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
}