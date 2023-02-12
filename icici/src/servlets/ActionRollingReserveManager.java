import com.directi.pg.*;
import com.manager.BankManager;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.BankRollingReserveVO;
import org.owasp.esapi.User;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 11/26/14
 * Time: 3:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActionRollingReserveManager extends HttpServlet
{
    private static Logger logger = new Logger(ActionRollingReserveManager.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        logger.debug("Entering in ActionRollingReserveManager");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        String errormsg = "";
        String EOL = "<BR>";

        String action=req.getParameter("action");
        String mappingId=req.getParameter("mappingid");
        Functions functions=new Functions();
        RequestDispatcher rd =null;
        BankManager bankManager=new BankManager();
        CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();
        try
        {
            if(!functions.isValueNull(action))
            {
                errormsg += "<center><font class=\"text\" face=\"arial\"><b>Action Or mapping ID is not provided." + EOL + "</b></font></center>";
                req.setAttribute("message",errormsg);
                rd=req.getRequestDispatcher("/actionRollingReserve.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }

            if(action.equalsIgnoreCase("view"))
            {
                BankRollingReserveVO bankRollingReserveVO=bankManager.getBankRollingReserveForAction(mappingId);

                String[] rollingreserve_timestamp=commonFunctionUtil.convertTimestampToDateTimePicker(bankRollingReserveVO.getRollingReserveDateUpTo());
                bankRollingReserveVO.setRollingReserveDateUpTo(rollingreserve_timestamp[0]);
                bankRollingReserveVO.setRollingRelease_time(rollingreserve_timestamp[1]);

                req.setAttribute("action",action);
                req.setAttribute("data",bankRollingReserveVO);
                rd=req.getRequestDispatcher("/actionRollingReserve.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
            }
            else if(action.equalsIgnoreCase("modify"))
            {
                BankRollingReserveVO bankRollingReserveVO=bankManager.getBankRollingReserveForAction(mappingId);

                String[] rollingreserve_timestamp=commonFunctionUtil.convertTimestampToDateTimePicker(bankRollingReserveVO.getRollingReserveDateUpTo());
                bankRollingReserveVO.setRollingReserveDateUpTo(rollingreserve_timestamp[0]);
                bankRollingReserveVO.setRollingRelease_time(rollingreserve_timestamp[1]);

                req.setAttribute("action",action);
                req.setAttribute("data",bankRollingReserveVO);
                rd=req.getRequestDispatcher("/actionRollingReserve.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
            }
            else if(action.equalsIgnoreCase("history"))
            {
                List<BankRollingReserveVO> bankRollingReserveVOList=bankManager.getBankRollingReserveHistory(mappingId);

                req.setAttribute("action",action);
                req.setAttribute("data",bankRollingReserveVOList);
                rd=req.getRequestDispatcher("/bankRollingReserveHistory.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
            }

        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::"+systemError);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>Internal Error While Processing Request,Check The Log File</b></font></center>";
            req.setAttribute("message",errormsg);
            rd=req.getRequestDispatcher("/actionRollingReserve.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (SQLException se)
        {
            logger.error("SQLException:::"+se);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>Internal Error While Processing Request,Check The Log File</b></font></center>";
            req.setAttribute("message",errormsg);
            rd=req.getRequestDispatcher("/actionRollingReserve.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (Exception e)
        {
            logger.error("GenericException:::"+e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>Internal Error While Processing Request,Check The Log File</font></center>";
            req.setAttribute("message",errormsg);
            rd=req.getRequestDispatcher("/actionRollingReserve.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }

    }
}
