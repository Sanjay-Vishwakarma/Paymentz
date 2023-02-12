import com.directi.pg.Admin;
import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.logicboxes.util.ApplicationProperties;
import com.manager.vo.ISOCommReportVO;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 2/10/15
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActionISOCommissionWireManager   extends HttpServlet
{
    private final static String PARTNER_PAYOUT_REPORT_FILE_PATH = ApplicationProperties.getProperty("ISO_COMMISSION_REPORT_FILE_PATH");
    private static Logger log = new Logger(ActionISOCommissionWireManager.class.getName());

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

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
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
        String isoCommId= req.getParameter("isocommid");

        try
        {
            if(action.equals("") || isoCommId.equals(""))
            {
                errormsg += "Action has not provided";
                req.setAttribute("message",errormsg);
                RequestDispatcher rd = req.getRequestDispatcher("/actionISOCommissionWireManager.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            else
            {
                ISOCommReportVO isoCommReportVO=null;
                if(action.equalsIgnoreCase("view"))
                {
                    isoCommReportVO=getISOCommWireReport(isoCommId);
                    req.setAttribute("action",action);
                    req.setAttribute("isoCommReportVO",isoCommReportVO);
                    RequestDispatcher rd = req.getRequestDispatcher("/actionISOCommissionWireManager.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(req, res);
                }
                else if(action.equalsIgnoreCase("update"))
                {
                    isoCommReportVO=getISOCommWireReport(isoCommId);
                    req.setAttribute("action",action);
                    req.setAttribute("isoCommReportVO",isoCommReportVO);
                    RequestDispatcher rd = req.getRequestDispatcher("/actionISOCommissionWireManager.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(req, res);
                }
                else if(action.equalsIgnoreCase("sendPdfFile"))
                {
                    isoCommReportVO=getISOCommWireReport(isoCommId);
                    File f = new File(PARTNER_PAYOUT_REPORT_FILE_PATH+isoCommReportVO.getReportFilePath());
                    String filename=f.getName();
                    if(filename==null || filename.isEmpty())
                    {
                       errormsg += "<center><font class=\"text\" face=\"arial\"><b>File Is Not Available." + EOL + "</b></font></center>";
                       req.setAttribute("message",errormsg);
                       RequestDispatcher rd = req.getRequestDispatcher("/isocommwiremanagerlist.jsp?ctoken="+user.getCSRFToken());
                       rd.forward(req, res);
                    }
                    sendFile(isoCommReportVO.getReportFilePath(),filename,f,res);
                }
            }
        }
        catch (SystemError systemError)
        {
            log.error("SystemError:::::"+systemError);
            errormsg += "Internal error while processing you request";
            req.setAttribute("message",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/actionISOCommissionWireManager.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (SQLException se)
        {
            log.error("SQLException:::::"+se);
            errormsg += "Internal error while processing you request";
            req.setAttribute("message",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/actionISOCommissionWireManager.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (Exception e)
        {
            log.error("GenericException:::::"+e);
            errormsg += "Internal error while processing you request";
            req.setAttribute("message",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/actionISOCommissionWireManager.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
    }

    public ISOCommReportVO getISOCommWireReport(String isoCommId)throws SystemError,SQLException
    {
        Connection con = null;
        PreparedStatement pstmt= null;
        ResultSet rs=null;
        ISOCommReportVO isoCommReportVO=null;
        try
        {
            con= Database.getRDBConnection();
            StringBuffer sbQuery=new StringBuffer("select * from iso_commission_wire_manager where iso_comm_id=?");
            pstmt=con.prepareStatement(sbQuery.toString());
            pstmt.setString(1,isoCommId);
            rs=pstmt.executeQuery();
            if(rs.next())
            {
                isoCommReportVO=new ISOCommReportVO();
                isoCommReportVO.setIsoWireId(rs.getString("iso_comm_id"));
                isoCommReportVO.setAccountId(rs.getString("accountid"));
                isoCommReportVO.setCurrency(rs.getString("currency"));
                isoCommReportVO.setStartDate(rs.getString("startdate"));
                isoCommReportVO.setEndDate(rs.getString("enddate"));
                isoCommReportVO.setSettledDate(rs.getString("settleddate"));
                isoCommReportVO.setAmount(rs.getDouble("amount"));
                isoCommReportVO.setNetfinalamount(rs.getDouble("netfinalamount"));
                isoCommReportVO.setUnpaidAmount(rs.getDouble("unpaidamount"));
                isoCommReportVO.setStatus(rs.getString("status"));
                isoCommReportVO.setReportFilePath(rs.getString("reportfilepath"));
                isoCommReportVO.setTransactionFilePath(rs.getString("transactionfilepath"));
                isoCommReportVO.setActionExecutor(rs.getString("actionexecutor"));
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return isoCommReportVO;
    }
}
