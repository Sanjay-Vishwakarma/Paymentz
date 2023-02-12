package net.partner;

import com.directi.pg.Admin;
import com.directi.pg.Logger;
import com.manager.TerminalManager;
import com.manager.vo.TerminalVO;
import com.manager.vo.UserVO;
import com.payment.MultipleMemberUtill;
import com.payment.MultiplePartnerUtill;
import com.payment.exceptionHandler.PZDBViolationException;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created by 123 on 1/22/2016.
 */
public class PartnerUserTerminalList extends HttpServlet
{
    static Logger log = new Logger(PartnerUserTerminalList.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("in side PartnerUserTerminalList---");
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }

        TerminalManager terminalManager = new TerminalManager();
        MultiplePartnerUtill multiplePartnerUtill = new MultiplePartnerUtill();
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
            userVOList = multiplePartnerUtill.getUserTerminalList(memberid, userId);
            stringSet = multiplePartnerUtill.getUserTerminalSet(memberid,userId);

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
            log.error("Exception---",dbe);
        }
        req.setAttribute("terminalVOList",terminalVOList);
        req.setAttribute("stringSet",stringSet);
        req.setAttribute("userVOList",userVOList);
        req.setAttribute("memberid",memberid);
        req.setAttribute("userid",userId);
        req.setAttribute("error",error);
        RequestDispatcher rd = req.getRequestDispatcher("/partnerUserTerminalMapping.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
        return;
    }
}
