package net.partner;

import com.directi.pg.Logger;
import com.manager.PartnerManager;
import com.manager.vo.PartnerDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.operations.PZOperations;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 4/14/15
 * Time: 11:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class MerchantTheme extends HttpServlet
{
    private static Logger logger = new Logger(MerchantTheme.class.getName());
    //instance for validation error list messages
   // private static CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        //Manager instance create
        PartnerManager partnerManager=new PartnerManager();

        HttpSession session = request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            logger.debug("partner is logout ");
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }
        String partnerid=((String)session.getAttribute("merchantid"));
        //System.out.println("partnerid----------"+partnerid);


        // RequestDispatcher rdError=request.getRequestDispatcher("/viewpartnerdetails.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        RequestDispatcher rdSuccess= request.getRequestDispatcher("/merchantTheme.jsp?Success=YES&ctoken="+user.getCSRFToken());


        try
        {
            PartnerDetailsVO partnerDetailsVOs= partnerManager.getselfPartnerDetails(partnerid);
            //System.out.println("partnerDetailsVOs------------"+partnerDetailsVOs);
            request.setAttribute("partnerDetailsVOs", partnerDetailsVOs);
            //System.out.println("Default theme" + partnerDetailsVOs.getDefaultTheme());
            //System.out.println("Current theme"+partnerDetailsVOs.getCurrentTheme());
            rdSuccess.forward(request,response);

        }
        catch (PZDBViolationException dbe)
        {
            logger.error("Db exception while getting Data for Member Config Details",dbe);
            PZExceptionHandler.handleDBCVEException(dbe,session.getAttribute("merchantid").toString(),PZOperations.MEMBER_CONFIG);
            request.setAttribute("catchError","Kindly check for the Member Config Details after sometime");
            //rdError.forward(request,response);
            return;
        }

    }


}
