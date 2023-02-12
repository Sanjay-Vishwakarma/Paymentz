import com.directi.pg.Logger;
//import com.manager.dao.CommissionManager;
import com.manager.dao.CommissionManager;
import com.manager.vo.CommissionVO;
import org.owasp.esapi.User;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sandip
 * Date: Feb 27, 2007
 * Time: 2:21:14 PM
 * To change this template use File | Settings | File Templates.
 */

public class GetDynamicInputCharges extends HttpServlet
{
    static Logger logger = new Logger(GetDynamicInputCharges.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        String accountId=req.getParameter("accountid");
        CommissionManager commissionManager=new CommissionManager();
        try
        {
           List<CommissionVO> commissionVOList=commissionManager.getGatewayAccountDynamicInputCommissions(accountId);
           req.setAttribute("commissionVOList",commissionVOList);
        }
        catch (Exception e)
        {
            logger.debug("Exception::::::::"+e);
        }
        req.getRequestDispatcher("/addnewisocommwire.jsp?ctoken="+user.getCSRFToken()).forward(req,res);
    }
}
