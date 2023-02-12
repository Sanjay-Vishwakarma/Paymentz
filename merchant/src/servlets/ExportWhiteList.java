import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.logicboxes.util.ApplicationProperties;
import com.logicboxes.util.Util;
import com.manager.dao.WhiteListDAO;
import com.manager.vo.PaginationVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.apache.commons.lang3.StringUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by kalyani on 20/9/2022.
 */
public class ExportWhiteList extends HttpServlet
{
    private static Logger log = new Logger(ExportWhiteList.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        System.out.println("ExportWhiteList------------123------");

        Merchants merchants= new Merchants();
        Functions functions = new Functions();

        HttpSession session = Functions.getNewSession(req);
        if (!merchants.isLoggedIn(session))
        {
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        String memberid = (String) session.getAttribute("merchantid");
        String accountID = req.getParameter("accountid"); // Added new column.
        String firstSix = Functions.checkStringNull(req.getParameter("firstsix"));
        System.out.println("firstSix"+firstSix);
        String lastFour = Functions.checkStringNull(req.getParameter("lastfour"));
        System.out.println("lastFour"+lastFour);
        String emailAddress = Functions.checkStringNull(req.getParameter("emailAddr"));
        System.out.println("emailAddress"+emailAddress);

        String name = Functions.checkStringNull(req.getParameter("name"));
        String ipAddress = Functions.checkStringNull(req.getParameter("ipAddress"));
        System.out.println("ipaddress"+ipAddress);




           /* if (!ESAPI.validator().isValidInput("accountid", accountID, "Numbers", 50, isValid))
            {
                log.debug("Invalid AccountId");
                stringBuffer.append(WhiteListCard_accountid_errormsg + EOL);
            }
            else if (functions.isValueNull(memberid) && functions.isValueNull(accountID))
            {
                boolean valid = whiteListDAO.getMemberidAccountid(memberid, accountID);
                if (valid == false)
                {
                    stringBuffer.append(WhiteListCard_memberid_errormsg);
                }
            }*/
        TransactionEntry transactionentry = new TransactionEntry();


        try
        {
            Hashtable hash =  transactionentry.getWhiteListCardForMerchant(firstSix, lastFour, emailAddress, name, ipAddress,memberid, accountID);


            int totalRecords = Integer.parseInt((String) hash.get("records"));
            log.debug("totalRecords----" + totalRecords);
            Hashtable transactionHash = null;
            String exportPath = ApplicationProperties.getProperty("EXPORT_FILE_LOCAL_PATH");
            String fileName = "/WhitelistCard-" +  ".csv";


            PrintWriter writer = new PrintWriter(new FileOutputStream(exportPath + "/" + fileName));

            printHeader(writer);

            for (int pos = 1; pos <= totalRecords; pos++)
            {
                transactionHash = (Hashtable) hash.get(pos + "");
                log.debug("transactionHash---" + transactionHash);
                printTransaction(writer, transactionHash);
            }

            writer.close();
            sendFile(exportPath + "/" + fileName, fileName, res);
            return;

        }
        catch (PZDBViolationException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void printHeader(PrintWriter writer)
    {
        print(writer, "FirstSix");
        print(writer,"LastFour");
        print(writer, "Email Address");
        print(writer, "Name");
        print(writer, "IP Address");
        printLast(writer, "AccountID");
    }

    private void printTransaction(PrintWriter writer, Hashtable transactionHash)
    {
        Functions functions = new Functions();
        /*String expiryDate = (String) transactionHash.get("expirydate");
        if (expiryDate == null || expiryDate =="" || expiryDate =="null")
            expiryDate = "-";
        else
            expiryDate = Encryptor.decryptExpiryDate(expiryDate);*/
        String firstsix="";
        if (functions.isValueNull((String)transactionHash.get("firstsix")))
        {
            firstsix = "'"+ (String)transactionHash.get("firstsix");
        }
        else
        {
            firstsix= "-";
        }
        String lastfour="";
        if (functions.isValueNull((String) transactionHash.get("lastfour")))
        {
            lastfour= "'"+ (String)transactionHash.get("lastfour");
        }
        else
        {
            lastfour= "-";
        }
        String emailAddr="";
        if (functions.isValueNull((String)transactionHash.get("emailAddr")))
        {
            emailAddr= "'"+ (String)transactionHash.get("emailAddr");
        }
        else
        {
            emailAddr ="-";
        }
        String name="";
        if (functions.isValueNull((String)transactionHash.get("name")))
        {
            name = "'"+ (String)transactionHash.get("name");
        }
        else
        {
            name ="-";
        }
        String ipAddress="";
        if (functions.isValueNull((String)transactionHash.get("ipAddress")))
        {
            ipAddress = "'"+ (String)transactionHash.get("ipAddress");
        }
        else
        {
            ipAddress ="-";
        }
        String accountid="";
        if (functions.isValueNull((String)transactionHash.get("accountid")))
        {
            accountid = "'"+ (String)transactionHash.get("accountid");
        }
        else
        {
            accountid ="-";
        }


        print(writer, firstsix);
        print(writer, lastfour);
        print(writer, emailAddr);
        print(writer, name);
        print(writer, ipAddress);
        printLast(writer, accountid);

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
        log.info("Successful#######");
        return true;

    }



}
