package net.partner;

import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemAccessLogger;
import com.manager.vo.UserVO;
import com.payment.MultipleMemberUtill;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.*;


/**
 * Created by Jinesh on 1/23/2016.
 */
public class AddUserTerminal extends HttpServlet
{
    private static Logger log = new Logger(AddUserTerminal.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        log.debug("Enter in AddUserTerminal ");
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        HttpSession session = req.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        StringBuffer sErrorMessage = new StringBuffer();
        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }

        String[] terminalStr = null;

        String redirectPage = "";

        String accountid = "";
        String success = "";
        String paymodeid = "";
        String cardtypeid = "";
        UserVO userVO = new UserVO();
        String memberid = req.getParameter("memberid");
        String userId = req.getParameter("userid");
        Hashtable hash1 = new Hashtable();

        MultipleMemberUtill multipleMemberUtill = new MultipleMemberUtill();

        userVO.setUserid(userId);
        userVO.setUserMerchantId(memberid);
        if (req.getParameterValues("terminal")!= null)
        {
            terminalStr = req.getParameterValues("terminal");
        }

        if (Functions.checkArrayNull(terminalStr) == null)
        {

            int i = multipleMemberUtill.deleteSelectedTerminal(userVO);
            if(i!=0)
            {
               success="Terminal Configuration Updated Successfully";
            }
            hash1 = multipleMemberUtill.getDetailsForSubmerchant(memberid);
            req.setAttribute("detailHash", hash1);
            req.setAttribute("memberid", memberid);
            req.setAttribute("success", success);
            redirectPage = "/memberChildList.jsp?ctoken=";

        }
        else
        {
            String error = "";
            int terminal = multipleMemberUtill.deleteSelectedTerminal(userVO);
            for (String terminalId : terminalStr)
            {
                paymodeid = req.getParameter("paymenttypeid" + terminalId);
                cardtypeid = req.getParameter("cardtypeid" + terminalId);
                accountid=req.getParameter("accountid"+terminalId);


                userVO.setUserTerminalId(terminalId);
                userVO.setUserCardTypeId(cardtypeid);
                userVO.setUserPaymodeId(paymodeid);
                userVO.setUserid(userId);
                userVO.setUserMerchantId(memberid);
                userVO.setUserAccountId(accountid);

                int i = multipleMemberUtill.addUserTerminal(userVO);

                if (i != 0)
                {
                    //sErrorMessage.append("Updated Successfully");
                    success = "Terminal Configuration Updated Successfully";
                }

            }
            hash1 = multipleMemberUtill.getDetailsForSubmerchant(memberid);

            req.setAttribute("detailHash", hash1);
            req.setAttribute("memberid", memberid);
            req.setAttribute("success", success);
            redirectPage = "/memberChildList.jsp?ctoken=";
        }
        RequestDispatcher rd = req.getRequestDispatcher(redirectPage+user.getCSRFToken());
        rd.forward(req, res);
        return;
    }

}
