import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.enums.ApplicationStatus;
import com.manager.ApplicationManager;
import com.manager.dao.PartnerDAO;
import com.validators.BankInputName;
import com.vo.applicationManagerVOs.AppValidationVO;
import com.vo.applicationManagerVOs.ApplicationManagerVO;
import com.vo.applicationManagerVOs.NavigationVO;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 1/20/15
 * Time: 8:15 PM
 * To change this template use File | Settings | File Templates.
 */

public class ApplicationManagerMerchant extends HttpServlet
{


    Logger logger=new Logger(ApplicationManagerMerchant.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {

        //instance created for util classes
        Functions functions=new Functions();
        Merchants merchants =new Merchants();
        //instance created for Vo
        ApplicationManagerVO applicationManagerVO=new ApplicationManagerVO();
        NavigationVO navigationVO = new NavigationVO();
        AppValidationVO appValidationVO = null;
        //instance of manager
        ApplicationManager applicationManager = new ApplicationManager();
        HttpSession session = Functions.getNewSession(request);
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        response.setHeader("X-Frame-Options", "ALLOWALL");
        session.setAttribute("X-Frame-Options", "ALLOWALL");


        if (!merchants.isLoggedIn(session))
        {   logger.debug("Merchant is logout ");
            response.sendRedirect("/merchant/logout.jsp");
            return;
        }


        Map<Integer, Map<Boolean, Set<BankInputName>>> fullValidationForStep=new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();
        Map<Integer, Map<Boolean, Set<BankInputName>>> dependencyFullValidationForStep=new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();
        Map<Integer, Map<Boolean, Set<BankInputName>>> dependencyOtherFullValidationForStep=new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();
        Map<Boolean,Set<BankInputName>> dependencyPageViseValidation=new HashMap<Boolean, Set<BankInputName>>();
        Map<Boolean,Set<BankInputName>> dependencyOtherPageViseValidation=new HashMap<Boolean, Set<BankInputName>>();
        Map<Integer,Set<BankInputName>> otherValidation=new HashMap<Integer, Set<BankInputName>>();
        Set<BankInputName> otherValidationPageVise=new HashSet<BankInputName>();
        PartnerDAO partnerDAO = new PartnerDAO();
        //Request Dispatcher
        RequestDispatcher rdSuccessU = request.getRequestDispatcher("/appNavigation.jsp?MES=Success&ctoken="+user.getCSRFToken());
        RequestDispatcher rdSuccessV=request.getRequestDispatcher("/viewapplicationdetails.jsp?MES=Success&ctoken="+user.getCSRFToken());
        //todo 1st validation for each input divided into mandatory & optional
        try
        {
            logger.debug("inside populate Application");
            applicationManagerVO.setMemberId(session.getAttribute("merchantid").toString());
            applicationManagerVO.setPartnerid(session.getAttribute("partnerid").toString());
            session.setAttribute("company", partnerDAO.getPartnerName((String)session.getAttribute("partnerid").toString()));
            appValidationVO=applicationManager.loadAllMerchantBankMapping(applicationManagerVO.getMemberId());
            if(functions.isValueNull(String.valueOf(session.getAttribute("applicationManagerVO"))))
            {
                applicationManagerVO=(ApplicationManagerVO)session.getAttribute("applicationManagerVO");
            }
            else
            {
                applicationManager.populateAppllicationData(applicationManagerVO);
            }
            applicationManager.setValidationForMember(applicationManagerVO.getMemberId(), navigationVO, fullValidationForStep, dependencyFullValidationForStep,dependencyPageViseValidation,otherValidation,dependencyOtherFullValidationForStep,otherValidationPageVise,dependencyOtherPageViseValidation,appValidationVO);

            //set in session value
            session.setAttribute("applicationManagerVO",applicationManagerVO);
            session.setAttribute("navigationVO",navigationVO);
            session.setAttribute("appValidationVO",appValidationVO);
            request.setAttribute("fullValidationForStep", fullValidationForStep);
            request.setAttribute("dependencyFullValidationForStep", dependencyFullValidationForStep);
            request.setAttribute("otherValidation", otherValidation);
            request.setAttribute("dependencyOtherFullValidationForStep", dependencyOtherFullValidationForStep);

            //pages in order how to be navigated
            if(applicationManagerVO.getMemberId()!=null &&((!functions.isValueNull(applicationManagerVO.getStatus()) || (ApplicationStatus.SAVED.name().equals(applicationManagerVO.getStatus()) || ApplicationStatus.MODIFIED.name().equals(applicationManagerVO.getStatus())|| ApplicationStatus.STEP1_SAVED.name().equals(applicationManagerVO.getStatus())|| ApplicationStatus.STEP1_SUBMIT.name().equals(applicationManagerVO.getStatus())))))
            {
                System.out.println("came hre tada");
                navigationVO.addStepAndPageName("companyprofile.jsp");
                navigationVO.addStepAndPageName("ownershipprofile.jsp");
                navigationVO.addStepAndPageName("businessprofile.jsp");
                navigationVO.addStepAndPageName("bankapplication.jsp");
                navigationVO.addStepAndPageName("cardholderprofile.jsp");
                navigationVO.addStepAndPageName("upload.jsp");
                //navigationVO.addStepAndPageName("reports.jsp");
                rdSuccessU.forward(request, response);
            }
            else
            {
                rdSuccessV.forward(request,response);
            }

        }
        catch (Exception e)
        {
            logger.error("Mail class exception::",e);
        }
        //todo after that set it in Vo directly without checking for null value

    }


}

