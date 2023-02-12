import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.logicboxes.util.ApplicationProperties;
import com.logicboxes.util.Util;
import com.manager.AccountManager;
import com.manager.TerminalManager;
import com.manager.vo.InputDateVO;
import com.manager.vo.PaginationVO;
import com.manager.vo.TerminalVO;
import com.manager.vo.payoutVOs.WireReportsVO;

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
 * Created by Kalyani on 02/11/2022.
 */
public class ExportReports extends HttpServlet
{
    private static Logger logger = new Logger(ExportReports.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(ExportReports.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        System.out.println("exportreports");
        logger.debug("Entering in ExportReports::::");
        Merchants merchants= new Merchants();
        Functions functions = new Functions();
        TerminalManager terminalManager=new TerminalManager();
        AccountManager accountManager= new AccountManager();
        InputDateVO inputDateVO= new InputDateVO();
        PaginationVO paginationVO = new PaginationVO();


        HttpSession session = Functions.getNewSession(req);
        if (!merchants.isLoggedIn(session))
        {
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        String toid = (String) session.getAttribute("merchantid");
        String currency = (String) session.getAttribute("currency");
        String terminalid = req.getParameter("terminalid");
        String sb = Functions.checkStringNull(req.getParameter("terminalbuffer"));
        String fromDate=Functions.checkStringNull(req.getParameter("firstdate"));
        String toDate= Functions.checkStringNull(req.getParameter("lastdate"));
        String fdate = req.getParameter("fdate");
        String tdate = req.getParameter("tdate");
        String fmonth = req.getParameter("fmonth");
        String tmonth = req.getParameter("tmonth");
        String fyear = req.getParameter("fyear");
        String tyear = req.getParameter("tyear");
        String netfinalamount= Functions.checkStringNull(req.getParameter("netfinalamount"));
        String isPaid=Functions.checkStringNull(req.getParameter("paid"));

        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
        Date date=null;

        int pageno = 1;
        int records = 10000;

        try
        {
            //from Date
            if (fdate == null) fdate = "" + 1;
            if (tdate == null) tdate = "" + rightNow.get(Calendar.DATE);

            if (fmonth == null) fmonth = "" + (rightNow.get(Calendar.MONTH));
            if (tmonth == null) tmonth = "" + (rightNow.get(Calendar.MONTH));

            if (fyear == null) fyear = "" + rightNow.get(Calendar.YEAR);
            if (tyear == null) tyear = "" + rightNow.get(Calendar.YEAR);

            String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
            String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");


            logger.debug("From date dd::"+fdate+" MM;;"+fmonth+" YY::"+fyear+" To date dd::"+tdate+" MM::"+tmonth+" YY::"+tyear);

            //conversion to dtstamp

            //setting in InputDateVO
            inputDateVO.setFdate(fdate);
            inputDateVO.setFmonth(fmonth);
            inputDateVO.setFyear(fyear);
            inputDateVO.setTdate(tdate);
            inputDateVO.setTmonth(tmonth);
            inputDateVO.setTyear(tyear);
            inputDateVO.setFdtstamp(fdtstamp);
            inputDateVO.setTdtstamp(tdtstamp);
            //getting IsPaid parameter
            System.out.println("tr");
            TerminalVO terminalVO=new TerminalVO();
            if(functions.isValueNull(terminalid) && !terminalid.equalsIgnoreCase("all"))
            {
                terminalVO=terminalManager.getTerminalByTerminalId(terminalid);
                terminalVO.setTerminalId("("+terminalVO.getTerminalId()+")");
                System.out.println("terminal2");
                //accountid=terminalVO.getAccountId();
            }
            else
            {
                terminalVO.setTerminalId(sb);
            }

            //Getting TerminalVO
            logger.debug(" getting terminalVo using terminalIdss");
            terminalVO=terminalManager.getTerminalByTerminalId(req.getParameter("terminalid"));
            logger.debug("terminalVO111::"+terminalVO.toString());
            //setting Pagination VO

            TransactionEntry transactionentry = (TransactionEntry) session.getAttribute("transactionentry");


            //accountManager call for list of Reports
            Hashtable hash=transactionentry.exportreports(terminalVO, inputDateVO, pageno, records);

            logger.debug("hash----"+hash);
            logger.debug("hash111----" + hash.get("firstdate"));
            int totalRecords = Integer.parseInt((String) hash.get("records"));
            System.out.println("totalRecords"+totalRecords);
            logger.debug("totalRecords----"+totalRecords);
            Hashtable transactionHash = null;
            String exportPath = ApplicationProperties.getProperty("EXPORT_FILE_LOCAL_PATH");
            String fileName = "Transactions-" + toid + "- from " + fdate + "-" + (Integer.parseInt(fmonth) + 1) + "-" + fyear + " to " + tdate + "-" + (Integer.parseInt(tmonth) + 1) + "-" + tyear + ".csv";

            PrintWriter writer = new PrintWriter(new FileOutputStream(exportPath + "/" + fileName));
            printHeader(writer,currency);
            for (int pos = 1; pos <= totalRecords; pos++)
            {
                transactionHash = (Hashtable) hash.get(pos + "");
                logger.debug("transactionHash---"+transactionHash);
                printTransaction(writer, transactionHash);
            }

            writer.close();
            sendFile(exportPath + "/" + fileName, fileName, res);
            return;
        }
        catch (ParseException e)
        {
            logger.error("ParseException:::",e);
        }
        catch (NumberFormatException nfe)
        {
            logger.error("NumberFormatException:::",nfe);
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

    private void printHeader(PrintWriter writer,String currency)
    {
        print(writer, "First Date");
        print(writer,"Last Date");
        print(writer, "Net Final Amount");
        print(writer, "Unpaid Amount");
        print(writer, "Currency");
        printLast(writer, "Date Of Record Generation");
    }

    private void printTransaction(PrintWriter writer, Hashtable transactionHash)
    {
        Functions functions = new Functions();

        String firstdate="";
        if (functions.isValueNull((String)transactionHash.get("firstdate")))
        {
            firstdate = "'"+ (String)transactionHash.get("firstdate");
        }
        else
        {
            firstdate= "-";
        }
        String lastdate="";
        if (functions.isValueNull((String) transactionHash.get("lastdate")))
        {
            lastdate= "'"+ (String)transactionHash.get("lastdate");
        }
        else
        {
            lastdate= "-";
        }


        print(writer, firstdate);
        print(writer, lastdate);
        print(writer, transactionHash.get("netfinalamount") == null ? "-" : (String) transactionHash.get("netfinalamount"));
        print(writer, transactionHash.get("unpaidamount") == null ? "-" : (String) transactionHash.get("unpaidamount"));
        print(writer, transactionHash.get("currency") == null ? "-" : (String) transactionHash.get("currency"));
        String initDateFormat = "yyyy-MM-dd HH:mm:ss";
        String targetDateFormat = "MM/dd/yyyy HH:mm:ss";
        String parsedDate ="" ;
        try
        {
            parsedDate = formatDate((String) transactionHash.get("timestamp"), initDateFormat, targetDateFormat);
        }
        catch (ParseException e)
        {
            logger.error("ParseException---",e);
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
        file.delete();
        logger.info("Successful#######");
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
