package servlets;

import com.directi.pg.Admin;
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
import java.io.*;
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
        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        response.setContentType("text/html");
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");


        Functions functions = new Functions();
        Hashtable hash      = new Hashtable();
        String action       = "";
        String fileName                 = "";
        String transactionLogsName      = "transaction";
        String facileroName             = "facilero";
        String filePath                 = ApplicationProperties.getProperty("LOGS_FILE_PATH");
       // File file = null;
        RequestDispatcher dispatcher    = request.getRequestDispatcher("/transactionLogs.jsp?ctoken=" + user.getCSRFToken());
        String  error                   = "";
        String downloadFileName         = "";
        SimpleDateFormat formater       = new SimpleDateFormat("yyyy-MM-dd");
        try
        {


            if(request.getParameter("action") != null){
                action = request.getParameter("action");
            }
            if(request.getParameter("fileName") != null){
                fileName = request.getParameter("fileName");

                if(fileName.equalsIgnoreCase("transaction.log") || fileName.equalsIgnoreCase("facilero.log")){
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

                List<String> fileNameList       = new LinkedList<>();
                File[] files                = new File(filePath).listFiles();
                Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());

                for (File file : files) {
                    if (file.exists() && file.getName().toLowerCase().contains(transactionLogsName.toLowerCase())) {
                        fileNameList.add(file.getName());
                    }
                    if (file.exists() && file.getName().toLowerCase().contains(facileroName.toLowerCase())) {
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
