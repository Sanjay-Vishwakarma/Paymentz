package servlets;

import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemAccessLogger;
import com.manager.TerminalManager;
import com.manager.vo.TerminalVO;
import com.manager.vo.UserVO;
import com.payment.MultipleMemberUtill;

import com.payment.exceptionHandler.PZDBViolationException;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.*;


/**
 * Created by 123 on 1/22/2016.
 */
public class MemberUserTerminalList extends HttpServlet
{
    static Logger log = new Logger(MemberUserTerminalList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("in side MemberUserTerminalList---");
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        TerminalManager terminalManager = new TerminalManager();
        MultipleMemberUtill multipleMemberUtill = new MultipleMemberUtill();
        List<TerminalVO> terminalVOList = null;
        List<UserVO> userVOList = null;
        Set<String> stringSet=null;
        String memberid = "";
        String userId = "";
        String errormsg = "";
        res.setContentType("text/html");
        UserVO userVO = new UserVO();
        String error = "";

        try
        {
            memberid = req.getParameter("memberid");
            userId = req.getParameter("userid");
            terminalVOList = terminalManager.getTerminalsByMerchantId(memberid);
            //userVOList = multipleMemberUtill.getUserList(memberid);
            userVOList = multipleMemberUtill.getUserTerminalList(memberid, userId);
            stringSet = multipleMemberUtill.getUserTerminalSet(memberid,userId);

            /*if(req.getParameter("action").equalsIgnoreCase("delete"))
            {
                userVO.setUserTerminalId(req.getParameter("terminalid"));
                userVO.setUserCardTypeId(req.getParameter("cardtype"));
                userVO.setUserPaymodeId(req.getParameter("paymode"));
                userVO.setUserid(userId);
                userVO.setUserMerchantId(memberid);
                int i = multipleMemberUtill.deleteSelectedTerminal(userVO);
                if(i==1)
                {
                    error = "Removed Successfully";
                }
                userVOList = multipleMemberUtill.getUserTerminalList(memberid, userId);
            }*/

        }
        catch (PZDBViolationException dbe)
        {

        }
        req.setAttribute("terminalVOList",terminalVOList);
        req.setAttribute("stringSet",stringSet);
        req.setAttribute("userVOList",userVOList);
        req.setAttribute("memberid",memberid);
        req.setAttribute("userid",userId);
        req.setAttribute("error",error);
        RequestDispatcher rd = req.getRequestDispatcher("/memberUserTerminalMapping.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
        return;
    }
}
