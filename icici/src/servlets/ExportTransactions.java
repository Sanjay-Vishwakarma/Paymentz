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

    byte[] bbuf         = new byte[1024];
    DataInputStream in  = new DataInputStream(new FileInputStream(f));

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

        String desc         = Functions.checkStringNull(req.getParameter("desc"));
        String name         = Functions.checkStringNull(req.getParameter("name"));
        String toid         = Functions.checkStringNull(req.getParameter("toid"));
        String cardpresent  = Functions.checkStringNull(req.getParameter("cardpresent"));
        String amount       = Functions.checkStringNull(req.getParameter("amt"));
        String orderdesc    = Functions.checkStringNull(req.getParameter("orderdesc"));
        String emailaddr    = Functions.checkStringNull(req.getParameter("emailaddr"));
        String trackingid   = Functions.checkStringNull(req.getParameter("trackingid"));
        String firstfourofccnum = Functions.checkStringNull(req.getParameter("firstfourofccnum"));
        String lastfourofccnum  = Functions.checkStringNull(req.getParameter("lastfourofccnum"));
        String status           = Functions.checkStringNull(req.getParameter("status"));
        String remark           = Functions.checkStringNull(req.getParameter("remark"));
        String dateType         = Functions.checkStringNull(req.getParameter("datetype"));
        String paymentId        = Functions.checkStringNull(req.getParameter("paymentid"));

        String fdate            = Functions.checkStringNull(req.getParameter("fdate"));
        String tdate            = Functions.checkStringNull(req.getParameter("tdate"));
        String fmonth           = Functions.checkStringNull(req.getParameter("fmonth"));
        String tmonth           = Functions.checkStringNull(req.getParameter("tmonth"));
        String fyear            = Functions.checkStringNull(req.getParameter("fyear"));
        String tyear            = Functions.checkStringNull(req.getParameter("tyear"));
        String perfectmatch     = Functions.checkStringNull(req.getParameter("perfectmatch"));
        String accountid        = Functions.checkStringNull(req.getParameter("accountid"));
        String cardtype         = Functions.checkStringNull(req.getParameter("cardtype"));
        String issuing_bank     = Functions.checkStringNull(req.getParameter("issuing_bank"));
        String arn              = req.getParameter("arn");
        String rrn              = req.getParameter("rrn");
        String auth             = req.getParameter("authorization_code");
        String statusType       = req.getParameter("statusType");
        String transactionMode  = req.getParameter("transactionMode");
        Functions functions     = new Functions();
        String customerid= req.getParameter("customerid");
        String telno=   Functions.checkStringNull(req.getParameter("telno"));
        String telnocc= Functions.checkStringNull(req.getParameter("telnocc"));
        String totype= Functions.checkStringNull(req.getParameter("partnerid"));
        String bankname= req.getParameter("bankname");
        String bankaccount= req.getParameter("bankaccount");
        String ifsc= req.getParameter("ifsc");
         PartnerDAO partnerDAO = new PartnerDAO();
          if(functions.isValueNull(totype))
            {
                totype = partnerDAO.getPartnerName(totype);
            }

        String statusflag= req.getParameter("statusflag");
        if(!functions.isValueNull(req.getParameter("arn")))
        {
            arn = "";
        }
        if(!functions.isValueNull(req.getParameter("rrn")))
        {
            rrn = "";
        }
        if(!functions.isValueNull(req.getParameter("authorization_code")))
        {
            auth = "";
        }

        String gatewayid    = "";
        String gateway_name = "";
        String currency     = "";
        String fromid="";
        if (req.getParameter("pgtypeid") != null && req.getParameter("pgtypeid").split("-").length == 3
                && !req.getParameter("pgtypeid").equalsIgnoreCase(""))
        {
            gatewayid       = req.getParameter("pgtypeid").split("-")[2];
            currency        = req.getParameter("pgtypeid").split("-")[1];
            gateway_name    = req.getParameter("pgtypeid").split("-")[0];
        }

        String startTime    = req.getParameter("starttime");
        String endTime      = req.getParameter("endtime");
        String firstSix     = "";
        String lastFour     = "";
        boolean archive     = Boolean.valueOf(req.getParameter("archive")).booleanValue();

        Calendar rightNow = Calendar.getInstance();
        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

        if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
        if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);

        if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);

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

            int i           = 0;
            Iterator itr    = trackingidList.iterator();
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
        String startTimeArr[]   = startTime.split(":");
        String endTimeArr[]     = endTime.split(":");
        String fdtstamp         =  Functions.converttomillisec(fmonth, fdate, fyear, startTimeArr[0], startTimeArr[1], startTimeArr[2]);
        String tdtstamp         = Functions.converttomillisec(tmonth, tdate, tyear, endTimeArr[0], endTimeArr[1], endTimeArr[2]);

        try
        {
            TransactionEntry transactionentry   = new TransactionEntry();
            Set<String> gatewayTypeSet          = getGatewayHash(gatewayid);
            SortedMap statushash                = transactionentry.getSortedMap();
            Hashtable hash;

            TransactionManager transactionManager = new TransactionManager();
            if(cardpresent.equals("CP")){
                hash = transactionManager.listCPTransactionsForExportInExcel(toid, gatewayid, trackingIds.toString(), name, desc, orderdesc, amount,firstfourofccnum, lastfourofccnum, emailaddr, tdtstamp, fdtstamp, status,remark, paymentId, perfectmatch, archive, gatewayTypeSet, gateway_name,accountid, dateType, currency, cardtype, issuing_bank,customerid,fromid);

            }else
            {
                if(functions.isValueNull(statusType) && statusType.equals("detail")){
                    hash = transactionManager.listTransactionsForExportInExcelForDetailStatus(toid, gatewayid, trackingIds.toString(), name, desc,orderdesc, amount, firstfourofccnum, lastfourofccnum,emailaddr, tdtstamp, fdtstamp, status, remark, paymentId,perfectmatch, archive, gatewayTypeSet, gateway_name,accountid,dateType, currency, cardtype,issuing_bank,arn,rrn,auth,statusType,transactionMode,customerid,statusflag,telno,telnocc,totype,fromid);
                }else{
                    hash = transactionManager.listTransactionsForExportInExcel(toid, gatewayid, trackingIds.toString(), name, desc, orderdesc, amount, firstfourofccnum, lastfourofccnum, emailaddr, tdtstamp, fdtstamp, status, remark, paymentId, perfectmatch, archive, gatewayTypeSet, gateway_name, accountid, dateType, currency, cardtype, issuing_bank,arn,rrn,auth,transactionMode,customerid,statusflag,telno,telnocc,totype,bankname,bankaccount,ifsc,fromid);
                }
            }
            int totalRecords            = Integer.parseInt((String) hash.get("records"));
            Hashtable transactionHash   = null;

            String exportPath   = ApplicationProperties.getProperty("EXPORT_FILE_LOCAL_PATH");
            String fileName     = "/Transactions-" +  "- from " + fdate + "-" + (Integer.parseInt(fmonth) + 1) + "-" + fyear + " to " + tdate + "-" + (Integer.parseInt(tmonth) + 1) + "-" + tyear + ".csv";
            OutputStream os     = new FileOutputStream(exportPath + fileName);
            os.write(239);//239 EF
            os.write(187);//187 BB
            os.write(191);//191 BF
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(os,"UTF-8"));

            printCriteria(writer, fdtstamp, tdtstamp, status, desc, orderdesc, archive ? "Archives" : "Current", trackingid,statushash,accountid,transactionMode);
            printHeader(writer,statusType);

            for (int pos = 1; pos <= totalRecords; pos++)
            {
                transactionHash = (Hashtable) hash.get(pos + "");
                printTransaction(writer, transactionHash, statushash,firstSix,lastFour,statusType);
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

    private void printCriteria(PrintWriter writer, String fromDate, String toDate, String status, String description, String orderdesc, String dataSource, String trackingid,SortedMap statushash,String accountid,String transactionMode)
    {
        printLast(writer, "Report Criteria");
        print(writer, "Start Date");
        printLast(writer, Functions.convertDtstamptoDate(fromDate));
        print(writer, "End Date");
        printLast(writer, Functions.convertDtstamptoDate(toDate));
        print(writer, "Tracking ID");
        printLast(writer, "'"+(Functions.checkStringNull(trackingid) == null ? "" : trackingid));
        print(writer, "Status");
        printLast(writer, Functions.checkStringNull(status) == null ? "All" : (String) statushash.get(status));
        print(writer, "Description");
        printLast(writer, Functions.checkStringNull(orderdesc) == null ? "" : orderdesc);
        print(writer, " Order Description");
        printLast(writer, Functions.checkStringNull(description) == null ? "" : description);
        print(writer, "Bank");
        printLast(writer, (Functions.checkStringNull(accountid) == null || "0".equals(accountid)) ? "All" : GatewayAccountService.getGatewayAccount(accountid).getDisplayName());
        print(writer, "Data Source");
        printLast(writer, dataSource);
        print(writer, "Transaction Mode");
        printLast(writer, transactionMode.equals("") ? "All" : transactionMode );
        printLast(writer, "");
        printLast(writer, "Transactions Report");

    }

    private void printHeader(PrintWriter writer,String statusType)
    {
       /* print(writer, "Date");

        print(writer, "Tracking ID");
        print(writer, "Member ID");
        print(writer,"Payment ID");
        print(writer, "PayMode");
        print(writer, "CardType");
        print(writer, "Description");
        print(writer, "Card Holder's Name");

        print(writer, "Auth Amount ");
        print(writer, "Captured Amount");
        print(writer, "Refunded Amount");
        print(writer, "Status");
        printLast(writer, "Remark");*/
        print(writer, " Transaction Date(MM/DD/YYYY)");
        print(writer,"Member ID");
        print(writer,"Merchant Company Name");
        print(writer, "Tracking ID");
        print(writer, "Partner Name");
        print(writer,"Payment ID");
        print(writer,"Order ID");
        print(writer, "Order Description");
        print(writer, "Customer ID");
        print(writer, "Bank Account ID");
        print(writer, "Terminal ID");
        print(writer,"Payment Mode");
        print(writer,"Payment Brand");
        print(writer,"Transaction Mode");
        print(writer,"Bin Card Category");
        print(writer,"Bin Sub Brand");
        print(writer,"Sub Card Type");
        print(writer,"Issuing Bank Name");
        print(writer,"ISO Country");
        print(writer,"Currency");
        print(writer,"Card Holder's Name");
        print(writer,"Customer Email");
        print(writer, "Beneficiary Name");
        print(writer, "Beneficiary Account Number");
        print(writer,"IFSC");
        print(writer,"PhoneCC");
        print(writer,"Phone Number");
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
        print(writer,"MID");
        print(writer,"Processing BankName");
        if(statusType!=null && statusType.equals("detail")){
            print(writer,"Detail Status");
            print(writer,"Detail Amount");
            print(writer,"Detail Time");
        }
        print(writer,"Merchant IP");
        print(writer,"Customer IP");
        printLast(writer, "Last Update Date(MM/DD/YYYY)");

    }

    private void printTransaction(PrintWriter writer, Hashtable transactionHash, SortedMap statushash,String firstSix, String lastFour,String statusType)
    {
        Functions functions = new Functions();
        /*print(writer, Functions.convertDtstamptoDate((String) transactionHash.get("dtstamp")));

        print(writer, (String) transactionHash.get("trackingid"));
        print(writer, (String) transactionHash.get("memberid"));
        print(writer, transactionHash.get("paymentid") == null ? "-" : (String) transactionHash.get("paymentid"));
        print(writer, transactionHash.get("paymodeid") == null ? "-" : (String) transactionHash.get("paymodeid"));
        print(writer, transactionHash.get("cardtype") == null ? "-" : (String) transactionHash.get("cardtype"));
        print(writer, (String) transactionHash.get("description"));
        print(writer, transactionHash.get("name") == null ? "-" : (String) transactionHash.get("name"));
        print(writer, (String) transactionHash.get("amount"));
        print(writer, (String) transactionHash.get("captureamount"));
        print(writer, (String) transactionHash.get("refundamount"));
        print(writer, (String) statushash.get((String) transactionHash.get("status")));
        printLast(writer, transactionHash.get("remark") == null ? "-" : (String) transactionHash.get("remark"));*/
        String transaction_mode = (String) transactionHash.get("transaction_mode");
        if(!functions.isValueNull(transaction_mode)){
            transaction_mode = "-";
        }
         String paymentid="";
        if (functions.isValueNull((String) transactionHash.get("paymentid")))
        {
            paymentid= "'"+(String) transactionHash.get("paymentid");
        }
        else
        {
            paymentid="-";
        }

        String trackingid="";
        if(functions.isValueNull((String)transactionHash.get("trackingid")))
        {
            trackingid= "'"+(String)transactionHash.get("trackingid");
        }
        else
        {
            trackingid="-";
        }
        String description="";
        if(functions.isValueNull((String) transactionHash.get("description")))
        {
            description= "'"+(String) transactionHash.get("description");
        }
        else
        {
            description="-";
        }
        String orderdescription="";
        if (functions.isValueNull((String) transactionHash.get("orderdescription")))
        {
            orderdescription= "'"+(String) transactionHash.get("orderdescription");
        }
        else
        {
            orderdescription="-";
        }
        String customerId="";
        if (functions.isValueNull((String) transactionHash.get("customerId")))
        {
            customerId= "'"+(String)transactionHash.get("customerId");
        }
        else
        {
            customerId="-";
        }
        String rrn= "";
        if (functions.isValueNull((String)transactionHash.get("rrn")))
        {
            rrn= "'"+(String)transactionHash.get("rrn");
        }
        else
        {
            rrn="-";
        }
        String arn= "";
        if (functions.isValueNull((String)transactionHash.get("arn")))
        {
            arn= "'"+(String)transactionHash.get("arn");
        }
        else
        {
            arn="-";
        }
        String authorization_code="";
        if(functions.isValueNull((String)transactionHash.get("authorization_code")))
        {
            authorization_code="'"+(String)transactionHash.get("authorization_code");
        }
        else
        {
            authorization_code="-";
        }
        String telnocc="";
        if (functions.isValueNull((String)transactionHash.get("telnocc")))
        {
            telnocc= "'"+(String)transactionHash.get("telnocc");
        }
        else
        {
            telnocc="-";
        }
        String telno="";
        if (functions.isValueNull((String)transactionHash.get("telno")))
        {
            telno= "'"+(String)transactionHash.get("telno");
        }
        else
        {
            telno="-";
        }
        String totype="";
        if (functions.isValueNull((String)transactionHash.get("totype")))
        {
            totype= "'"+(String)transactionHash.get("totype");
        }
        else
        {
            totype="-";
        }
        String bankaccount="";
        if (functions.isValueNull((String)transactionHash.get("bankaccount")))
        {
            bankaccount = "'"+(String)transactionHash.get("bankaccount");
        }
        else
        {
            bankaccount="-";
        }
        String fromid="";
        if (functions.isValueNull((String)transactionHash.get("fromid")))
        {
            fromid = "'"+(String)transactionHash.get("fromid");
        }
        else
        {
            fromid="-";
        }
        print(writer, Functions.convertDtstampToDBFormat((String) transactionHash.get("dtstamp")));
        print(writer, (String) transactionHash.get("memberid"));
        print(writer, (String) transactionHash.get("company_name") == null ? "-" : (String) transactionHash.get("company_name"));

        print(writer,trackingid);
        print(writer, totype);

        print(writer, paymentid);
        print(writer, description);
        print(writer, orderdescription);
        print(writer, customerId);
        print(writer, (String) transactionHash.get("accountid")==null ? "-":(String) transactionHash.get("accountid"));
        print(writer, (String) transactionHash.get("terminalid")==null ? "-":(String) transactionHash.get("terminalid"));
        print(writer, transactionHash.get("paymodeid") == null ? "-" : (String) transactionHash.get("paymodeid"));
        print(writer, transactionHash.get("cardtype") == null ? "-" : (String) transactionHash.get("cardtype"));
        print(writer, transaction_mode);
        print(writer, transactionHash.get("bin_card_category") == null ? "-" : (String) transactionHash.get("bin_card_category"));
        print(writer, transactionHash.get("bin_sub_brand") == null ? "-" : (String) transactionHash.get("bin_sub_brand"));
        print(writer, transactionHash.get("subcard_type") == null ? "-" : (String) transactionHash.get("subcard_type"));
        print(writer, transactionHash.get("issuing_bank") == null ? "-" : (String) transactionHash.get("issuing_bank"));
        print(writer, transactionHash.get("bin_country_name") == null ? "-" : (String) transactionHash.get("bin_country_name"));
        print(writer, (String) transactionHash.get("currency"));
        print(writer, transactionHash.get("name") == null ? "-" : (String) transactionHash.get("name"));
        print(writer, transactionHash.get("emailaddr") == null ? "-" : (String) transactionHash.get("emailaddr"));
        print(writer, (String) transactionHash.get("fullname") == null ? "-" : (String) transactionHash.get("fullname"));
        print(writer, bankaccount);
        print(writer,(String) transactionHash.get("ifsc")== null ? "-" : (String)transactionHash.get("ifsc"));
        print(writer, telnocc);
        print(writer, telno);
        print(writer, transactionHash.get("country") == null ? "-" : (String) transactionHash.get("country"));
        print(writer, (String) transactionHash.get("amount"));
        print(writer, (String) transactionHash.get("captureamount"));
        print(writer, (String) transactionHash.get("refundamount"));
        print(writer, (String) transactionHash.get("chargebackamount"));
        print(writer, (String) transactionHash.get("payoutamount"));
        String encCardNum = (String) transactionHash.get("ccnum");

        String firstSixBin = (String) transactionHash.get("first_six");
        String lastFourBin = (String) transactionHash.get("last_four");
        /*if (functions.isValueNull(encCardNum))
        {
            firstSix = encCardNum.substring(0, 6);
            lastFour = encCardNum.substring((encCardNum.length() - 4), encCardNum.length());
        }*//*else{
            firstSix = "-";
            lastFour = "-";
        } *//*
        String firstsixvar="";
        if(functions.isValueNull(firstSix))
        {
            firstsixvar= "'"+firstSix;
        }
        else
        {
           firstsixvar="-";
        }

        String lastfourvar="";
        if (functions.isValueNull(lastFour))
        {
            lastfourvar= "'"+lastFour;
        }
        else
        {
            lastfourvar="-";
        }*/

        if (functions.isValueNull(firstSixBin))
        {
            firstSixBin= "'"+firstSixBin;
        }
        else
        {
            firstSixBin="-";
        }
        if (functions.isValueNull(lastFourBin))
        {
            lastFourBin= "'"+lastFourBin;
        }
        else
        {
            lastFourBin="-";
        }


        /*print(writer, firstsixvar);
        print(writer, lastfourvar);*/

        print(writer, firstSixBin);
        print(writer, lastFourBin);

        print(writer, rrn);
        print(writer, arn);
        print(writer, authorization_code);
        print(writer, (String) statushash.get(transactionHash.get("status")));
        print(writer, ((String) transactionHash.get("chargebackinfo") == null ? "-" : (String) transactionHash.get("chargebackinfo")));
        print(writer, (String) transactionHash.get("remark") == null ? "-" : (String) transactionHash.get("remark"));
        print(writer, fromid);
        print(writer, (String) transactionHash.get("processingbank") == null ? "-" : (String) transactionHash.get("processingbank"));
        log.debug("timestamp:::::" + transactionHash.get("timestamp"));
        if(functions.isValueNull(statusType) && statusType.equals("detail"))
        {
            print(writer, (String) transactionHash.get("detailStatus") == null ? "-" : (String) transactionHash.get("detailStatus"));
            print(writer, (String) transactionHash.get("detailAmount") == null ? "-" : (String) transactionHash.get("detailAmount"));
            print(writer, (String) transactionHash.get("detailTimestamp") == null ? "-" : (String) transactionHash.get("detailTimestamp"));
        }
        print(writer, (String) transactionHash.get("ipAddress") == null ? "-" : (String) transactionHash.get("ipAddress"));
        print(writer, (String) transactionHash.get("customerIp") == null ? "-" : (String) transactionHash.get("customerIp"));
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
        //printLast(writer, (String) transactionHash.get("timestamp"));

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

    public String formatDate (String date, String initDateFormat, String endDateFormat) throws ParseException
    {

        Date initDate = new SimpleDateFormat(initDateFormat).parse(date);
        SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat);
        String parsedDate = formatter.format(initDate);
        return parsedDate;
    }

}
