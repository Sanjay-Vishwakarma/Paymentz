import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.manager.AppRequestManager;
//import com.manager.ApplicationManager;
import com.enums.Module;
import com.payment.Mail.AsynchronousMailService;
import com.vo.applicationManagerVOs.ApplicationManagerVO;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.Mail.MailService;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Pradeep on 07/08/2015.
 */
public class BankMail extends HttpServlet
{
    private static Logger log = new Logger(BankMail.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        Merchants merchants = new Merchants();

        if (!merchants.isLoggedIn(session))
        {
            log.debug("member is logout ");
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        //MailService mailService = new MailService();
        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
        HashMap applyModificationMail = new HashMap();
        AppRequestManager appRequestManager = new AppRequestManager();
//        ApplicationManager applicationManager = new ApplicationManager();

        ApplicationManagerVO applicationManagerVO = null;

        RequestDispatcher rdSuccess = req.getRequestDispatcher("/viewapplicationdetails.jsp?MES=Success&ctoken=" + user.getCSRFToken());


        //Applay mail when merchant wants modification
        try
        {
            applicationManagerVO = appRequestManager.getApplicationManagerVO(session);
            applicationManagerVO.setAppliedToModify("Y");
            applicationManagerVO.setUser(Module.MERCHANT.name());
//            applicationManager.updateAppManagerStatus(applicationManagerVO);
            applyModificationMail.put(MailPlaceHolder.APPLICATION_ID, applicationManagerVO.getApplicationId());
            applyModificationMail.put(MailPlaceHolder.TOID, session.getAttribute("merchantid"));
            applyModificationMail.put(MailPlaceHolder.SUBJECT, " For MemberID " + session.getAttribute("merchantid"));
            asynchronousMailService.sendMerchantSignup(MailEventEnum.Apply_Modification_Mail, applyModificationMail);
            rdSuccess.forward(req, res);
        }
        catch (Exception e)
        {
            log.error("Exceptionn-------" + e);
        }

    }
}
