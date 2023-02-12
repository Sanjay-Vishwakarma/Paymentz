import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.logicboxes.util.ApplicationProperties;
import com.logicboxes.util.Util;
import com.manager.WhiteListManager;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.Calendar;
import java.util.Hashtable;
/**
 * Created with IntelliJ IDEA.
 * User: Mahima
 * Date: 25/03/18
 * Time: 2:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExportWhitelistDetails extends HttpServlet
{
    static Logger log = new Logger(ExportWhitelistDetails.class.getName());

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
        String memberId     = req.getParameter("merchantid");
        String accountId    = req.getParameter("accountid");
        String emailAddr    = Functions.checkStringNull(req.getParameter("emailaddr"));
        String firstSix     =Functions.checkStringNull(req.getParameter("firstsix"));
        String lastFour     = Functions.checkStringNull(req.getParameter("lastfour"));
        String fdate        = Functions.checkStringNull(req.getParameter("fdate"));
        String tdate        = Functions.checkStringNull(req.getParameter("tdate"));
        String fmonth       = Functions.checkStringNull(req.getParameter("fmonth"));
        String tmonth       = Functions.checkStringNull(req.getParameter("tmonth"));
        String fyear        = Functions.checkStringNull(req.getParameter("fyear"));
        String tyear        = Functions.checkStringNull(req.getParameter("tyear"));
        String startTime    = req.getParameter("starttime");
        String endTime      = req.getParameter("endtime");
        String isTemp       = req.getParameter("isTemp");
        String role         = "Admin";
        String username     = (String)session.getAttribute("username");
        String actionExecutorId = (String)session.getAttribute("merchantid");
        String actionExecutorName   = role+"-"+username;


        boolean archive = Boolean.valueOf(req.getParameter("archive")).booleanValue();

        Calendar rightNow = Calendar.getInstance();
        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

        if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
        if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);

        if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);

        Functions functions = new Functions();
        if (functions.isValueNull(startTime))
        {
            startTime = startTime.trim();
        }
        else
        {
            startTime = "00:00:00";
        }

        if (functions.isValueNull(endTime))
        {
            endTime = endTime.trim();
        }
        else
        {
            endTime = "23:59:59";
        }
        String startTimeArr[] = startTime.split(":");
        String endTimeArr[] = endTime.split(":");

        String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, startTimeArr[0], startTimeArr[1], startTimeArr[2]);
        String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, endTimeArr[0], endTimeArr[1], endTimeArr[2]);
        try
        {
            WhiteListManager whiteListManager = new WhiteListManager();
            Hashtable hash = whiteListManager.getWhiteListEmailDetailsForExport(memberId, accountId,emailAddr,firstSix,lastFour,actionExecutorId,actionExecutorName,isTemp);
            log.debug(hash);

            int totalRecords            = Integer.parseInt((String) hash.get("records"));
            Hashtable transactionHash   = null;

            String exportPath   = ApplicationProperties.getProperty("EXPORT_FILE_LOCAL_PATH");
            String fileName     = "/CardToBeWhiteListed-"+".csv";
            PrintWriter writer  = new PrintWriter(new FileOutputStream(exportPath + fileName));
            printHeader(writer);

            for (int pos = 1; pos <= totalRecords; pos++)
            {
                transactionHash = (Hashtable) hash.get(pos + "");
                if ((functions.isValueNull( (String) transactionHash.get("firstsix") ) && functions.isValueNull( (String) transactionHash.get("lastfour") ) ) || functions.isValueNull((String) transactionHash.get("emailAddr")))
                {
                    printTransaction(writer, transactionHash);
                }
            }
            writer.close();
            sendFile(exportPath + "/" + fileName, fileName, res);
            return;
        }
        catch (Exception e)
        {
            log.error("Exception", e);
            req.setAttribute("errormessage", "Internal error while processing your request.");
            RequestDispatcher rd = req.getRequestDispatcher("/uploadwhitelistemaildetails.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
    }
    private void printHeader(PrintWriter writer)
    {
        print(writer, "Member Id");
        print(writer, "Account ID");
        print(writer, "First Six");
        print(writer, "Last Four");
        print(writer, "Is Approved");
        print(writer, "Is Temp");
        print(writer, "Action Executor Id");
        print(writer, "Action Executor Name");
        printLast(writer, "Email Address");

    }
    private void printTransaction(PrintWriter writer, Hashtable transactionHash)
    {
        Functions functions=new Functions();

        print(writer, (String) transactionHash.get("memberid") == null ? "-" : (String) transactionHash.get("memberid"));
        print(writer, (String) transactionHash.get("accountid") == null ? "-" : (String) transactionHash.get("accountid"));
        print(writer, (String) transactionHash.get("firstsix") == null ? "-" : (String) transactionHash.get("firstsix"));
        print(writer, (String) transactionHash.get("lastfour") == null ? "-" : (String) transactionHash.get("lastfour"));
        print(writer, (String) transactionHash.get("isApproved") == null ? "-" : (String) transactionHash.get("isApproved"));
        print(writer, (String) transactionHash.get("isTemp") == null ? "-" : (String) transactionHash.get("isTemp"));
        print(writer, (String) transactionHash.get("actionExecutorId") == null ? "-" : (String) transactionHash.get("actionExecutorId"));
        print(writer, (String) transactionHash.get("actionExecutorName") == null ? "-" : (String) transactionHash.get("actionExecutorName"));
        printLast(writer, (String) transactionHash.get("emailAddr") == null ? "-" : (String) transactionHash.get("emailAddr"));

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
