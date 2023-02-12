package net.agent;

import com.directi.pg.*;
import com.logicboxes.util.ApplicationProperties;
import org.owasp.esapi.User;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.*;

/**
 * Created by Sneha on 10/21/2015.
 */
public class ActionMerchantWireReports extends HttpServlet
{
    private final static String PAYOUT_REPORT_FILE_PATH = ApplicationProperties.getProperty("PAYOUT_REPORT_FILE_PATH");
    private final static String SETTLEMENT_FILE_PATH = ApplicationProperties.getProperty("SETTLEMENT_FILE_PATH");

    private static Logger log = new Logger(ActionMerchantWireReports.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }
    public void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        log.debug("Entering in ActionMerchantWireReports");
        AgentFunctions agentFunctions = new AgentFunctions();

        HttpSession session = Functions.getNewSession(request);
        if (!agentFunctions.isLoggedInAgent(session))
        {
            response.sendRedirect("/agent/logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        Connection conn = null;
        ResultSet rs=null;
        PreparedStatement preparedStatement = null;
        StringBuffer errormsg = new StringBuffer();
        RequestDispatcher rd = request.getRequestDispatcher("/merchantWireReports.jsp?ctoken=" + user.getCSRFToken());

        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            String action = request.getParameter("action");
            String wireid = request.getParameter("mappingid");
            String exportPath = null;

            if (action.equalsIgnoreCase("sendPdfFile"))
            {
                StringBuffer qry= new StringBuffer("SELECT settlementreportfilepath FROM merchant_wiremanager WHERE settledid=?");
                preparedStatement=conn.prepareStatement(qry.toString());
                preparedStatement.setString(1,wireid);
                rs=preparedStatement.executeQuery();
                rs.next();
                exportPath=rs.getString("settlementreportfilepath");
                if(exportPath==null || exportPath.isEmpty())
                {
                    errormsg.append("File Not Found");
                    request.setAttribute("error", errormsg);
                    rd.forward(request, response);
                }
                File f = new File(PAYOUT_REPORT_FILE_PATH+exportPath);
                String filename=f.getName();
                if(filename==null || filename.isEmpty())
                {
                    errormsg.append("File Not Found");
                    request.setAttribute("message",errormsg);
                    rd.forward(request, response);
                }
                sendFile(filename,f,response);
            }
            else if(action.equalsIgnoreCase("sendExcelFile"))
            {

                StringBuffer qry= new StringBuffer("SELECT settledtransactionfilepath FROM merchant_wiremanager WHERE settledid=?");
                preparedStatement=conn.prepareStatement(qry.toString());
                preparedStatement.setString(1,wireid);
                rs=preparedStatement.executeQuery();
                rs.next();
                exportPath=rs.getString("settledtransactionfilepath");
                if(exportPath==null || exportPath.isEmpty())
                {
                    errormsg.append("File Not Found");
                    request.setAttribute("error", errormsg);
                    rd.forward(request, response);
                }
                File f = new File(SETTLEMENT_FILE_PATH+exportPath);
                String filename=f.getName();
                if(filename==null || filename.isEmpty())
                {
                    errormsg.append("File Not Found");
                    request.setAttribute("error",errormsg);
                    rd.forward(request, response);
                }
                sendFile(filename,f,response);
            }
  }
        catch (FileNotFoundException e)
        {
            log.debug("SystemError::"+e);
            request.setAttribute("error","File Not Found");
        }
        catch (SystemError systemError)
        {
            log.error("SystemError::",systemError);
            request.setAttribute("error","Internal System Error, Please Contact Support");
            rd.forward(request, response);
        }
        catch (SQLException e)
        {
            log.error("SQLException::",e);
            request.setAttribute("error", "Internal System Error, Please Contact Support");
            rd.forward(request, response);
        }
        catch (Exception e)
        {
            log.error("Exception::",e);
            request.setAttribute("error", "Internal System Error, Please Contact Support");
            rd.forward(request, response);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
    }

    public static boolean sendFile(String filename,File f,HttpServletResponse response)throws Exception
    {
        int length = 0;
        // Set browser download related headers
        response.setContentType("application/octat-stream");
        response.setContentLength((int) f.length());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
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

        log.info("Successfully downloaded  file======"+filename);
        return true;
    }
}
