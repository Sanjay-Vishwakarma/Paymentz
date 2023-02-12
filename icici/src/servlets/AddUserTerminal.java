import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
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
import java.util.List;

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
        if (!Admin.isLoggedIn(session))
        {
            log.debug("invalid user");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        String[] terminalStr = null;

        String redirectPage = "";

        String paymodeid = "";
        String cardtypeid = "";
        String acountid = "";
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
            if(i!=1)
            {
                sErrorMessage.append("Terminal Configuration Updated Successfully");
            }
            hash1 = multipleMemberUtill.getDetailsForSubmerchant(memberid);
            req.setAttribute("detailHash", hash1);
            req.setAttribute("memberid", memberid);
            req.setAttribute("error", sErrorMessage.toString());
            redirectPage = "/memberChildList.jsp?ctoken=";

        }
        else
        {
            String error = "";
            multipleMemberUtill.deleteSelectedTerminal(userVO);
            for (String terminalId : terminalStr)
            {
                paymodeid = req.getParameter("paymenttypeid" + terminalId);
                cardtypeid = req.getParameter("cardtypeid" + terminalId);
                acountid = req.getParameter("accountid" + terminalId);

                userVO.setUserTerminalId(terminalId);
                userVO.setUserCardTypeId(cardtypeid);
                userVO.setUserPaymodeId(paymodeid);
                userVO.setUserid(userId);
                userVO.setUserMerchantId(memberid);
                userVO.setUserAccountId(acountid);

                int i = multipleMemberUtill.addUserTerminal(userVO);

                if (i == 1)
                {
                    //sErrorMessage.append("Updated Successfully");
                    error = "Terminal Configuration Updated Successfully";
                }

            }
            hash1 = multipleMemberUtill.getDetailsForSubmerchant(memberid);
            req.setAttribute("detailHash", hash1);
            req.setAttribute("memberid", memberid);
            req.setAttribute("error", error);
            redirectPage = "/memberChildList.jsp?ctoken=";
        }
        RequestDispatcher rd = req.getRequestDispatcher(redirectPage+user.getCSRFToken());
        rd.forward(req, res);
        return;
    }

}
