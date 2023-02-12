package servlets;

import com.directi.pg.*;

import com.logicboxes.util.ApplicationProperties;
import org.owasp.esapi.User;


import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.List;
import java.util.function.Function;

/**
 * Created by devendra on 3/16/2020.
 */
public class ExportTerminalDetails extends HttpServlet
{
    private static Logger log = new Logger(ExportTerminalDetails.class.getName());
    Functions functions = new Functions();

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


        List<HashMap<String, String>> hashMapList = new ArrayList<>();
        HashMap<String, String> hashReadData = null;
        String fileName = "";
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        Enumeration<String> en = req.getParameterNames();
        while (en.hasMoreElements())
        {

            String key = en.nextElement();
            String value = req.getParameter(key);
            log.error("Enumeration---------->" + key + "-----" + value);
        }

        String url = "/membermappingpreference.jsp?ctoken=\"" + user.getCSRFToken() + "\"";
        String accountid = req.getParameter("accountid");
        String memberid = req.getParameter("memberId");
        String memberId1 = "";
        String memberid2 = "";
        String paymodeid= "";
        String cardTypeId = "";
        String ActiveOrInActive = "";
        String ispayoutactive = "";

        if(req.getParameter("paymodeid") != null)
            paymodeid = req.getParameter("paymodeid");
        if (req.getParameter("ctype1") != null)
            cardTypeId = req.getParameter("ctype1");
        if (req.getParameter("isactive") != null)
            ActiveOrInActive = req.getParameter("isactive");
        if ("All".equalsIgnoreCase(ActiveOrInActive))
            ActiveOrInActive = "";
        if (req.getParameter("ispayoutactive") != null)
            ispayoutactive = req.getParameter("ispayoutactive");
        if ("All".equalsIgnoreCase(ispayoutactive))
            ispayoutactive = "";

        String ExportRequest = req.getParameter("ExportRequest");
        log.error("ExportRequest----------------->" + ExportRequest);


       /* String memberid ="12482";
        String accountid ="3080";*/
        log.error("accountid----------------->" + accountid);
        log.error("memberid----------------->" + memberid);
        log.error("paymodeid----------------->" + paymodeid);
        log.error("cardTypeId----------------->" + cardTypeId);
        log.error("ActiveOrInActive----------------->" + ActiveOrInActive);
        log.error("ispayoutactive----------------->" + ispayoutactive);
        log.error("inside ExportTerminalDetails---------------------------->");
        Connection con = null;

        try
        {


            StringBuilder query = new StringBuilder("SELECT memberid,m.accountid,terminalid,gt.currency,m.isactive,m.min_transaction_amount,m.max_transaction_amount,ct.cardType,pm.paymode,isCardWhitelisted,displayname,m.payoutActivation FROM member_account_mapping AS m , card_type AS ct , payment_type AS pm, gateway_accounts AS ga, gateway_type AS gt WHERE m.accountid>0 ");

            if (functions.isValueNull(memberid) && functions.isValueNull(accountid))
                query.append(" AND memberid= " + memberid + " " + " AND m.`accountid`= " + accountid);
            else if (functions.isValueNull(memberid))
                query.append(" AND memberid= " + memberid);
            else if (functions.isValueNull(accountid))
                query.append(" AND m.`accountid`= " + accountid);

            if (functions.isValueNull(paymodeid))
                query.append(" AND pm.`paymodeid`= " + paymodeid);
            if (functions.isValueNull(cardTypeId))
                query.append(" AND ct.`cardtypeid`= " + cardTypeId);
            if (!functions.isEmptyOrNull(ActiveOrInActive))
                query.append(" and m.isactive='" + ActiveOrInActive +"' ");
            if (!functions.isEmptyOrNull(ispayoutactive))
                query.append(" and m.payoutActivation='" + ispayoutactive +"' ");

            query.append(" AND m.`cardtypeid`= ct.cardtypeid AND m.`paymodeid`=pm.paymodeid AND ga.accountid=m.accountid AND ga.pgtypeid=gt.pgtypeid ORDER BY memberid ");
            log.error("database query------------------------------------------------------->" + query);
            con = Database.getConnection();
            ResultSet rs = null;
            PreparedStatement ps = con.prepareStatement(query.toString());
            rs = ps.executeQuery();
            log.error("reading data from database---------------------------->");
            while (rs.next())
            {
                hashReadData = new HashMap<>();
                hashReadData.put("memberid", rs.getString("memberid"));
                hashReadData.put("accountid", rs.getString("accountid"));
                hashReadData.put("terminalid", rs.getString("terminalid"));
                hashReadData.put("currency", rs.getString("currency"));
                hashReadData.put("min_transaction_amount", rs.getString("min_transaction_amount"));
                hashReadData.put("max_transaction_amount", rs.getString("max_transaction_amount"));
                hashReadData.put("cardType", rs.getString("cardType"));
                hashReadData.put("paymode", rs.getString("paymode"));
                hashReadData.put("displayname", rs.getString("displayname"));
                hashReadData.put("isCardWhitelisted", rs.getString("isCardWhitelisted"));
                hashReadData.put("isactive", rs.getString("isactive"));
                hashReadData.put("payoutActivation", rs.getString("payoutActivation"));

                hashMapList.add(hashReadData);

                memberId1 = hashReadData.get("memberid");
                if (memberid2.contains(memberId1))
                {
                    memberid2 = memberid2 + "";
                }
                else
                {
                    memberid2 = memberId1 + "," + memberid2;
                }

                log.error("rs.getString(memberid)-----------------" + hashReadData.get("memberid"));
                log.error("rs.getString(accountid)-----------------" + hashReadData.get("accountid"));
                log.error("rs.getString(terminalid)-----------------" + hashReadData.get("terminalid"));
                log.error("rs.getString(currency)-----------------" + hashReadData.get("currency"));
                log.error("rs.getString(min_transaction_amount)-----------------" + hashReadData.get("min_transaction_amount"));
                log.error("rs.getString(max_transaction_amount)-----------------" + hashReadData.get("max_transaction_amount"));
                log.error("rs.getString(cardType)-----------------" + hashReadData.get("cardType"));
                log.error("rs.getString(paymode)-----------------" + hashReadData.get("paymode"));
                log.error("rs.getString(displayname)-----------------" + hashReadData.get("displayname"));
                log.error("rs.getString(isCardWhitelisted)-----------------" + hashReadData.get("isCardWhitelisted"));
                log.error("rs.getString(isactive)-----------------" + hashReadData.get("isactive"));


            }
            //fileName=hashReadData.get("memberid")+"_TerminalDetails"+".csv";
            if ((functions.isValueNull(memberid) && functions.isValueNull(accountid)) || (!functions.isValueNull(memberid) && functions.isValueNull(accountid)))
            {
                fileName = accountid + "_TerminalDetails" + ".csv";
            }
            else if (functions.isValueNull(memberid))
            {
                fileName = memberid + "_TerminalDetails" + ".csv";
            }
        }
        catch (Exception e)
        {
            log.error("exception----------------------------->", e);

        }
        finally
        {
            Database.closeConnection(con);
        }


        try
        {
            log.error("creating excel---------------------------->");


            StringBuffer csvData = new StringBuffer("");


            // _Merchant Account Details_
            //csvData.append("accountid").append(',').append(map.get("accountid")).append(',').append('\n');
            csvData.append('\n');
            csvData.append('\n');
            csvData.append("           _Merchant Account Details_").append('\n');
            csvData.append("MerchantID/ToID").append(',').append(memberid2).append(',').append('\n');
            csvData.append("SecretKey").append(',').append("Merchant Backoffice->Settings->Generate key->Copy current key").append('\n');
            csvData.append("Partner Name/ServiceProvider/ToType").append(',').append("   ").append('\n');
            csvData.append("URL for Standard flow").append(',').append("   ").append('\n');
            csvData.append("URL for Standard flow").append(',').append("   ").append('\n');
            csvData.append("URL for REST API").append(',').append("   ").append('\n');
            csvData.append('\n');
            csvData.append('\n');


            for (HashMap<String, String> map : hashMapList)
            {
                //TERMINAL_INR_CC

                csvData.append("                _TERMINAL_" + map.get("currency") + "_" + map.get("paymode")).append('\n');
                csvData.append("MemberID").append(',').append(map.get("memberid")).append(',').append('\n');
                csvData.append("TerminalID").append(',').append(map.get("terminalid")).append(',').append('\n');
                csvData.append("Currency").append(',').append("            " + map.get("currency")).append(',').append('\n');
                csvData.append("Min_Transaction_Amount").append(',').append(map.get("min_transaction_amount")).append(',').append('\n');
                csvData.append("Max_Transaction_Amount").append(',').append(map.get("max_transaction_amount")).append(',').append('\n');
                csvData.append("Payment_Brand      ").append(',').append("           " + map.get("cardType")).append(',').append('\n');
                csvData.append("Payment_Mode       ").append(',').append("              " + map.get("paymode")).append(',').append('\n');
                csvData.append("Display_Name       ").append(',').append("           " + map.get("displayname")).append(',').append('\n');
                csvData.append('\n');
                csvData.append('\n');

            }
            String exportPath = ApplicationProperties.getProperty("EXPORT_FILE_LOCAL_PATH");
            log.error("EXPORT_FILE_LOCAL_PATH---------------------------->" + exportPath);
            PrintWriter pw = new PrintWriter(new File(exportPath + "/" + fileName));
            pw.write(csvData.toString());

            pw.close();
            log.error("excel created successfully---------------------------->");
            sendFile(exportPath + "/" + fileName, fileName, res);
            return;

        }
        catch (Exception e)
        {
            log.error("Exception", e);
            req.setAttribute("errormessage", "Internal error while processing your request.");
            RequestDispatcher rd = req.getRequestDispatcher("/memberUserTerminalMapping.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
    }


}




