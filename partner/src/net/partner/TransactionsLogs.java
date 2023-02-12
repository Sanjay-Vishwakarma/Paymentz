package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.logicboxes.util.ApplicationProperties;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Pramod on 31/5/2022.
 */
public class TransactionsLogs extends HttpServlet
{
    private static Logger log   = new Logger(TransactionsLogs.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {

        HttpSession session         = request.getSession();
        log.debug("Enter in Transection ");
        PartnerFunctions partner    = new PartnerFunctions();

        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");


        Functions functions = new Functions();
        Hashtable hash      = new Hashtable();
        String action       = "";
        String fileName     = "";
        String filePath     = ApplicationProperties.getProperty("LOGS_FILE_PATH");
       // File file = null;
        RequestDispatcher dispatcher    = request.getRequestDispatcher("/transactionLogs.jsp?ctoken=" + user.getCSRFToken());
        String  error                   = "";
        String downloadFileName         ="";
        SimpleDateFormat formater       = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            String partnerId    =  String.valueOf(session.getAttribute("partnerid"));
            String partnerName  = partner.getPartnerName(partnerId);

            log.error("partnerName >>>>>>>>>>>> "+partnerName);

            if(request.getParameter("action") != null){
                action = request.getParameter("action");
            }
            if(request.getParameter("fileName") != null){
                fileName = request.getParameter("fileName");

                if(fileName.equalsIgnoreCase("facilero.log")){
                    String dateStr      = formater.format(new Date()).toString();
                    downloadFileName    = fileName+"."+dateStr;
                }else{
                    downloadFileName    = fileName;
                }
            }
            String exportPath   = ApplicationProperties.getProperty("LOGS_FILE_PATH");

            if(action.equalsIgnoreCase("download")){
                sendFile(exportPath + "/" + fileName, downloadFileName, response);
                return;
            }else{

                List<String> fileNameList   = new LinkedList<String>();
                File[] files                = new File(filePath).listFiles();
                Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
                for (File file : files) {
                    if (file.isFile() && file.getName().toLowerCase().contains(partnerName.toLowerCase())) {
                    //if (file.isFile() && file.getName().contains("facilero")) {
                        fileNameList.add(file.getName());
                    }
                }

                request.setAttribute("fileNameList",fileNameList);

                if(fileNameList.isEmpty() ){
                    request.setAttribute("error","No Logs Files Found");
                }

                dispatcher.forward(request,response);
                return;
            }

        }catch(SystemError se){
            log.error("Error",se);
            Functions.NewShowConfirmation1("Error!", "Invalid Transaction");
        }

        catch (Exception e)
        {
            log.error("Error",e);
            Functions.NewShowConfirmation1("Error!", "Invalid Transaction");
        }

    }

    public static boolean sendFile(String filepath, String filename, HttpServletResponse response) throws Exception
    {
        File f          = new File(filepath);
        int length      = 0;
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
       // File file = new File(filepath);
        //file.delete();
        log.info("Successful#######");
        return true;
    }

}
