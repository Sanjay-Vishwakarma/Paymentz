package net.partner;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
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
 * Created by ThinkPadT410 on 3/15/2017.
 */
public class ExportTransactions extends HttpServlet
{
    private static Logger logger = new Logger(ExportTransactions.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(ExportTransactions.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        logger.debug("Entering in ExportTransactions::::");
        HttpSession session = req.getSession();
        PartnerFunctions partner=new PartnerFunctions();
        Functions functions = new Functions();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }

        String merchantid = (String) session.getAttribute("merchantid");
        String currency = (String) session.getAttribute("currency");

        String toid = req.getParameter("toid");
        String fromdate = Functions.checkStringNull(req.getParameter("fdate"));
        String todate = Functions.checkStringNull(req.getParameter("tdate"));
        String rejectreason = Functions.checkStringNull(req.getParameter("rejectreason"));
        String amount = Functions.checkStringNull(req.getParameter("amount"));
        String email = Functions.checkStringNull(req.getParameter("emailaddr"));
        //String firstname = Functions.checkStringNull(req.getParameter("firstname"));
        //String lastname = Functions.checkStringNull(req.getParameter("lastname"));
        String name = Functions.checkStringNull(req.getParameter("name"));
        String desc = Functions.checkStringNull(req.getParameter("description"));
        String accountid = Functions.checkStringNull(req.getParameter("accountid"));
        String terminalid = req.getParameter("terminalid");
        String role = req.getParameter("role");
        String firstsix= Functions.checkStringNull(req.getParameter("firstsix"));
        String lastfour= Functions.checkStringNull(req.getParameter("lastfour"));

        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date=null;

        int pageno = 1;
        int records = 10000;

        try
        {
            //from Date
            date = sdf.parse(fromdate);
            rightNow.setTime(date);
            String fdate = String.valueOf(rightNow.get(Calendar.DATE));
            String fmonth = String.valueOf(rightNow.get(Calendar.MONTH));
            String fyear = String.valueOf(rightNow.get(Calendar.YEAR));

            //to Date
            date = sdf.parse(todate);
            rightNow.setTime(date);
            String tdate = String.valueOf(rightNow.get(Calendar.DATE));
            String tmonth = String.valueOf(rightNow.get(Calendar.MONTH));
            String tyear = String.valueOf(rightNow.get(Calendar.YEAR));
            logger.debug("From date dd::" + fdate + " MM;;" + fmonth + " YY::" + fyear + " To date dd::" + tdate + " MM::" + tmonth + " YY::" + tyear);

            String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
            String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");
            logger.debug("fdtstamp----"+fdtstamp);
            logger.debug("tdtstamp----"+tdtstamp);

            if(!functions.isValueNull(toid))
            {
                StringBuilder sb    = new StringBuilder();
                Hashtable hash      = partner.getPartnerMemberDetails(merchantid);
                Enumeration enu3    = hash.keys();
                String key3 = "";
                while (enu3.hasMoreElements())
                {

                    key3    = (String) enu3.nextElement();
                    toid    =(String) hash.get(key3);
                    sb.append((String) hash.get(key3) + ",");
                }
                if (sb.length() > 0 && sb.charAt(sb.length()-1)==',')
                {
                    toid = sb.substring(0, sb.length()-1);
                }
            }
            TransactionEntry transactionentry = (TransactionEntry) session.getAttribute("transactionentry");
            Set<String> gatewayTypeSet = new HashSet();
           // gatewayTypeSet.addAll(transactionentry.getGatewayHashByPartnerMembers(toid,accountid).keySet());

            Hashtable hash = transactionentry.listTransactionsForRejected(fdtstamp, tdtstamp,rejectreason, amount, email, toid, name,desc,records, pageno, gatewayTypeSet, accountid,terminalid,firstsix,lastfour);
            int totalRecords = Integer.parseInt((String) hash.get("records"));
            logger.debug("totalRecords----"+totalRecords);
            Hashtable transactionHash = null;
            String exportPath = ApplicationProperties.getProperty("EXPORT_FILE_LOCAL_PATH");
            String fileName = "Transactions-" + merchantid + "- from " + fdate + "-" + (Integer.parseInt(fmonth) + 1) + "-" + fyear + " to " + tdate + "-" + (Integer.parseInt(tmonth) + 1) + "-" + tyear + ".csv";

            PrintWriter writer = new PrintWriter(new FileOutputStream(exportPath + "/" + fileName));
            printCriteria(writer, fdtstamp, tdtstamp, toid,desc, accountid,merchantid);
            printHeader(writer, currency);

            for (int pos = 1; pos <= totalRecords; pos++)
            {
                transactionHash = (Hashtable) hash.get(pos + "");
                logger.debug("transactionHash---"+transactionHash);
                printTransaction(writer, transactionHash,role);
            }

            writer.close();
            sendFile(exportPath + "/" + fileName, fileName, res);
            return;
        }
        catch (ParseException e)
        {
            logger.error("ParseException:::",e);
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::",systemError);
        }
        catch (Exception e)
        {
            logger.error("Exception:::",e);
        }
    }

    private void printCriteria(PrintWriter writer, String fromDate, String toDate, String toid,String description,String accountid,String merchantid)
    {
        printLast(writer, "Report Criteria");
        print(writer, "Start Date");
        printLast(writer, Functions.convertDtstamptoDate(fromDate));
        print(writer, "End Date");
        printLast(writer, Functions.convertDtstamptoDate(toDate));
        print(writer, "Partner ID");
        printLast(writer, merchantid);
        print(writer, "Description");
        printLast(writer, Functions.checkStringNull(description)==null?"":description);
        print(writer, "Bank");
        printLast(writer, Functions.checkStringNull(accountid)==null?"All": GatewayAccountService.getGatewayAccount(accountid).getDisplayName());
        printLast(writer, "");
        printLast(writer, "Transactions Report");
    }

    private void printHeader(PrintWriter writer, String currency)
    {
        print(writer, "Date");
        print(writer,"Tracking Id");
        print(writer, "Member ID");
        print(writer, "Totype");
        print(writer, "Transaction Amount");
        print(writer, "Description");
        print(writer, "Rejecte Reason");
        print(writer, "Remark");
        print(writer, "First Name");
        print(writer, "Last Name");
        print(writer, "First Six");
        print(writer, "Last Four");
        print(writer, "Card TypeID");
        print(writer, "Payment TypeID ");
        print(writer, "Currency");
        print(writer, "Requested IP");
        print(writer, "Customer's Email");
        printLast(writer, "Terminal ID");
    }

    private void printTransaction(PrintWriter writer, Hashtable transactionHash ,String Roles)
    {
        Functions functions=new Functions();
        String id="";
        if (functions.isValueNull((String) transactionHash.get("id")))
        {
            id= "'"+(String)transactionHash.get("id");
        }
        else
        {
            id="-";
        }

        String description="";
        if (functions.isValueNull((String) transactionHash.get("description")))
        {
            description= "'"+(String)transactionHash.get("description");
        }
        else
        {
            description="-";
        }
        String firstsix="";
        if (functions.isValueNull((String)transactionHash.get("firstsix")))
        {
            firstsix= "'"+(String)transactionHash.get("firstsix");
        }
        else
        {
            firstsix="-";
        }

        String lastfour="";
        if (functions.isValueNull((String)transactionHash.get("lastfour")))
        {
            lastfour= "'"+(String)transactionHash.get("lastfour");
        }
        else
        {
            lastfour="-";
        }
        String rejectreason="";
        if(functions.isValueNull((String) transactionHash.get("rejectreason")))
        {
            rejectreason=((String) transactionHash.get("rejectreason")).replaceAll("<BR>","");
        }
        else
        {
            rejectreason="-";
        }
        String remark="";
        if (functions.isValueNull((String) transactionHash.get("remark")))
        {
            remark = ((String) transactionHash.get("remark")).replaceAll("<BR>", "");
        }
        else
        {
            remark="-";
        }
        /*String expiryDate = (String) transactionHash.get("expirydate");
        if (expiryDate == null || expiryDate =="" || expiryDate =="null")
            expiryDate = "-";
        else
            expiryDate = PzEncryptor.decryptExpiryDate(expiryDate);*/
        print(writer, Functions.convertDtstamptoDate((String) transactionHash.get("dtstamp")));
        print(writer, id);
        print(writer, transactionHash.get("toid") == null ? "-" : (String) transactionHash.get("toid"));
        print(writer, transactionHash.get("totype") == null ? "-" : (String) transactionHash.get("totype"));
        //print(writer, transactionHash.get("login") == null ? "-" : (String) transactionHash.get("login"));
        print(writer, transactionHash.get("amount") == null ? "-" : (String) transactionHash.get("amount"));
        print(writer, description);
        print(writer, rejectreason);
        print(writer, remark);
        print(writer, transactionHash.get("firstname") == null ? "-" : (String) transactionHash.get("firstname"));
        print(writer, transactionHash.get("lastname") == null ? "-" : (String) transactionHash.get("lastname"));
        print(writer, firstsix);
        print(writer, lastfour);
        print(writer, transactionHash.get("paymenttypeid") == null ? "-" : (String) transactionHash.get("paymenttypeid"));
        print(writer, transactionHash.get("cardtypeid") == null ? "-" : (String) transactionHash.get("cardtypeid"));
        print(writer, transactionHash.get("currency") == null ? "-" : (String) transactionHash.get("currency"));
        print(writer, transactionHash.get("requestedip") == null ? "-" : (String) transactionHash.get("requestedip"));
        if(Roles.contains("superpartner")){
            print(writer, transactionHash.get("email") == null ? "-" : (String) transactionHash.get("email"));
        }else{
            print(writer, transactionHash.get("email") == null ? "-" : functions.getEmailMasking((String) transactionHash.get("email")));
        }
        printLast(writer, transactionHash.get("terminalid") == null ? "-" : (String) transactionHash.get("terminalid"));
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
        file.delete();
        logger.info("Successful#######");
        return true;

    }
}
