import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.core.GatewayTypeService;
import com.logicboxes.util.ApplicationProperties;
import com.logicboxes.util.Util;
import com.manager.ChargeManager;
import com.manager.dao.GatewayAccountDAO;
import com.payment.exceptionHandler.PZDBViolationException;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: May 22, 2011
 * Time: 12:29:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class ExportChargeDetails extends HttpServlet
{
    private static Logger log = new Logger(ExportChargeDetails.class.getName());
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
        Functions functions=new Functions();
        if (!Admin.isLoggedIn(session))
        {
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        String pgTypeId = req.getParameter("pgtypeid");
        String memberId = req.getParameter("memberid");
        String accountId = req.getParameter("accountid");
        String chargeName = req.getParameter("chargename");
        String chargeType = req.getParameter("chargetype");
        String terminalId = req.getParameter("terminalid");
        String gateway = "";
        GatewayAccountDAO gatewayAccountDAO = new GatewayAccountDAO();

        try
        {
            if(functions.isValueNull(req.getParameter("pgtypeid")) && !"0".equals(req.getParameter("pgtypeid")) && "0".equals(req.getParameter("pgtypeid")))
            {
                String gatewayArr[] = req.getParameter("pgtypeid").split("-");
                gateway = gatewayArr[0];
                Set<String> accountids = null;
                try
                {
                    accountids = gatewayAccountDAO.getAccountIds(gateway);
                }
                catch (Exception e)
                {
                    log.error("Exception::::::::" + e);
                    req.setAttribute("errormessage", "Internal error while processing your request");
                    RequestDispatcher rd = req.getRequestDispatcher("/listMemberAccountsCharges.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                }
                accountId = String.valueOf(accountids).replace("[","").replace("]","");
            }
            if(!req.getParameter("accountid").equals("0"))
            {
                accountId= req.getParameter("accountid");
                req.setAttribute("accountid",accountId);
            }

            ChargeManager chargeManager=new ChargeManager();
            Hashtable hash=chargeManager.getMemberAccountChargesForExport(memberId, accountId, chargeName, chargeType, terminalId);
            String exportPath = ApplicationProperties.getProperty("EXPORT_FILE_LOCAL_PATH");
            SimpleDateFormat dateFormater =new SimpleDateFormat("yyyyMMddHHmmss");
            String currentSystemDate=dateFormater.format(new Date());
            String fileName = "/ChargeDetails-"+currentSystemDate +".csv";
            OutputStream os=new FileOutputStream(exportPath + fileName);
            os.write(239);//239 EF
            os.write(187);//187 BB
            os.write(191);//191 BF
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(os,"UTF-8"));

            printCriteria(writer, pgTypeId,memberId,accountId,terminalId,chargeName,chargeType);
            printHeader(writer);
            int totalRecords = Integer.parseInt((String) hash.get("records"));
            Hashtable transactionHash = null;

            for (int pos = 1; pos <= totalRecords; pos++)
            {
                transactionHash = (Hashtable) hash.get(pos + "");
                printTransaction(writer, transactionHash);
            }

            writer.close();
            sendFile(exportPath + "/" + fileName, fileName, res);
            return;

        }
        catch (Exception e)
        {
            log.error("Exception", e);
            req.setAttribute("errormessage", "Internal error while processing your request.");
            RequestDispatcher rd = req.getRequestDispatcher("/listMemberAccountsCharges.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
    }

    private void printCriteria(PrintWriter writer,String pgTypeId,String memberId,String accountId,String terminalId,String chargeName,String chargeType)
    {
        printLast(writer, "Report Criteria");
        print(writer, "Gateway");
        printLast(writer, Functions.checkStringNull(pgTypeId) == null ? "" : pgTypeId);
        print(writer, "Account ID");
        printLast(writer, Functions.checkStringNull(accountId) == null ? "" : accountId);
        print(writer, "Member ID");
        printLast(writer, Functions.checkStringNull(memberId) == null ? "" : memberId);
        print(writer, "Terminal ID");
        printLast(writer, Functions.checkStringNull(terminalId) == null ? "" : terminalId);
        printLast(writer, "");
        printLast(writer, "Charges Report");

    }

    private void printHeader(PrintWriter writer)
    {
        print(writer,"Account ID");
        print(writer,"Member ID");
        print(writer,"Terminal ID");
        print(writer,"Charge Name");
        print(writer, "Charge Type");
        print(writer, "Member Charge value");
        print(writer, "Partner Charge value");
        print(writer, "Agent Charge value");
        print(writer,"Keyword");
        print(writer,"Sub-Keyword");
        print(writer,"Sequence Number");
        print(writer, "Frequency");
        print(writer, "Category");
        printLast(writer, "Is Input Required");
    }

    private void printTransaction(PrintWriter writer, Hashtable transactionHash) throws PZDBViolationException
    {
        Functions functions=new Functions();
        String negativebalance=(String) transactionHash.get("negativebalance");
        String message ="";
        ChargeManager chargeManager = new ChargeManager();
        boolean Negativebalanceshow =chargeManager.Negativebalanceshow((String) transactionHash.get("chargeid"));
        if(Negativebalanceshow){
            if(negativebalance.equals("Y")){
                message=" [Positive Settlement Charges]";
            }else{
                message=" [No Settlement Charges]";
            }
        }
        print(writer, (String) transactionHash.get("accountid"));
        print(writer, (String) transactionHash.get("memberid"));
        print(writer, (String) transactionHash.get("terminalid"));
        print(writer, (String) transactionHash.get("chargename") + message);
        print(writer, (String) transactionHash.get("valuetype"));
        print(writer, (String) transactionHash.get("chargevalue"));
        print(writer, (String) transactionHash.get("partnerchargevalue"));
        print(writer, (String) transactionHash.get("agentchargevalue"));
        print(writer, (String) transactionHash.get("keyword"));
        print(writer, (String) transactionHash.get("subkeyword"));
        print(writer, (String) transactionHash.get("sequencenum"));
        print(writer, (String) transactionHash.get("frequency"));
        print(writer, (String) transactionHash.get("category"));
        printLast(writer, (String) transactionHash.get("isinput_required"));
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