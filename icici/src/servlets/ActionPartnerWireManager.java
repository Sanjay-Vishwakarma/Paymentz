import com.directi.pg.*;
import com.logicboxes.util.ApplicationProperties;
import com.manager.dao.PartnerDAO;
import com.manager.vo.payoutVOs.PartnerWireVO;

import org.owasp.esapi.User;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.SQLException;

import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 2/10/15
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActionPartnerWireManager extends HttpServlet
{
    private final static String PARTNER_PAYOUT_REPORT_FILE_PATH = ApplicationProperties.getProperty("PARTNER_PAYOUT_REPORT_FILE_PATH");
    private static Logger log = new Logger(ActionPartnerWireManager.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        log.debug("Entering in ActionPartnerWireManager");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        String errormsg = "";
        String EOL = "<BR>";

        String action=req.getParameter("action");
        String wireId=req.getParameter("mappingid");
        try
        {
            if(action.equals("") || wireId.equals(""))
            {
                errormsg += "<center><font class=\"text\" face=\"arial\"><b>Action Or mapping ID is not provided." + EOL + "</b></font></center>";
                req.setAttribute("message",errormsg);
                RequestDispatcher rd = req.getRequestDispatcher("/actionPartnerWireManager.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            else
            {
                PartnerDAO partnerDAO=new PartnerDAO();
                PartnerWireVO partnerWireVO=null;
                if(action.equalsIgnoreCase("view"))
                {
                    partnerWireVO=partnerDAO.getSinglePartnerWire(wireId);

                    req.setAttribute("readonly","readonly");
                    req.setAttribute("action",action);
                    req.setAttribute("partnerWireVO",partnerWireVO);
                    RequestDispatcher rd = req.getRequestDispatcher("/actionPartnerWireManager.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(req, res);
                }
                else if(action.equalsIgnoreCase("update"))
                {
                    partnerWireVO=partnerDAO.getSinglePartnerWire(wireId);

                    req.setAttribute("readonly","readonly");
                    req.setAttribute("action",action);
                    req.setAttribute("partnerWireVO",partnerWireVO);
                    RequestDispatcher rd = req.getRequestDispatcher("/actionPartnerWireManager.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(req, res);
                }
                else if(action.equalsIgnoreCase("delete"))
                {
                    boolean isMarkForDelete=partnerDAO.setPartnerWireMarkForDelete(wireId);
                    if(isMarkForDelete)
                    {
                        errormsg += "<center><font class=\"text\" face=\"arial\"><b>Partner Wire Marked For Deletion successful." + EOL + "</b></font></center>";
                    }
                    else
                    {
                        errormsg += "<center><font class=\"text\" face=\"arial\"><b>Partner Wire Failed To Mark For Deletion." + EOL + "</b></font></center>";
                    }
                    req.setAttribute("message",errormsg);
                    RequestDispatcher rd = req.getRequestDispatcher("/partnerWireList.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(req, res);
                }
                else if(action.equalsIgnoreCase("sendPdfFile"))
                {
                    String exportPath =null;
                    exportPath=partnerDAO.getPartnerWireReportFileName(wireId);
                    if(exportPath==null || exportPath.isEmpty())
                    {
                        errormsg += "<center><font class=\"text\" face=\"arial\"><b>File Is Not Available." + EOL + "</b></font></center>";
                        req.setAttribute("message",errormsg);
                        RequestDispatcher rd = req.getRequestDispatcher("/partnerWireList.jsp?ctoken="+user.getCSRFToken());
                        rd.forward(req, res);
                    }

                    File f = new File(PARTNER_PAYOUT_REPORT_FILE_PATH+exportPath);
                    String filename=f.getName();
                    if(filename==null || filename.isEmpty())
                    {
                        errormsg += "<center><font class=\"text\" face=\"arial\"><b>File Is Not Available." + EOL + "</b></font></center>";
                        req.setAttribute("message",errormsg);
                        RequestDispatcher rd = req.getRequestDispatcher("/partnerWireList.jsp?ctoken="+user.getCSRFToken());
                        rd.forward(req, res);
                    }
                    sendFile(exportPath,filename,f,res);

                }
            }
        }
        catch (SystemError systemError)
        {
            log.error("SystemError:::::"+systemError);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>Internal Error Wire Processing Request,Check The Log File</b></font></center>";
            req.setAttribute("message",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/actionPartnerWireManager.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (SQLException se)
        {
            log.error("SQLException:::::"+se);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>Internal Error Wire Processing Request,Check The Log File</b></font></center>";
            req.setAttribute("message",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/actionPartnerWireManager.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (Exception e)
        {
            log.error("GenericException:::::"+e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>Internal Error Wire Processing Request,Check The Log File</b></font></center>";
            req.setAttribute("message",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/actionPartnerWireManager.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }

    }

    public static boolean sendFile(String filepath,String filename,File f,HttpServletResponse response)throws Exception
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

        log.info("Successfully donloaded  file======"+filename);
        return true;

    }
}
