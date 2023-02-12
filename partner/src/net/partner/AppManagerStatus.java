package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.logicboxes.util.ApplicationProperties;
import com.manager.AppRequestManager;
import com.manager.ApplicationManager;
import com.enums.ApplicationStatus;
import com.enums.BankApplicationStatus;
import com.enums.ConsolidatedAppStatus;
import com.enums.Module;
import com.manager.dao.PartnerDAO;
import com.manager.vo.ActionVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.vo.applicationManagerVOs.ApplicationManagerVO;
import com.vo.applicationManagerVOs.BankApplicationMasterVO;
import com.vo.applicationManagerVOs.ConsolidatedApplicationVO;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;


public class AppManagerStatus extends HttpServlet
{
    private static Logger logger = new Logger(AppManagerStatus.class.getName());
    private Functions functions = new Functions();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {

        HttpSession session = request.getSession();

        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            logger.debug("Partner is logout ");
            response.sendRedirect("/partner/logout.jsp");
            return;
        }
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        ApplicationManager applicationManager = new ApplicationManager();

        AppRequestManager appRequestManager =new AppRequestManager();

        ApplicationManagerVO applicationManagerVO=null;

        BankApplicationMasterVO bankApplicationMasterVO=null;

        ConsolidatedApplicationVO consolidatedApplicationVO=null;

        ActionVO actionVO = new ActionVO();

        boolean saved=false;
        RequestDispatcher rdSuccess = request.getRequestDispatcher("/net/ListofAppMember?MES=Success&ctoken="+user.getCSRFToken());

        try
        {
            applicationManagerVO=appRequestManager.getApplicationManagerVO(session);
            applicationManagerVO.setUser(Module.PARTNER.name());
            String speed_status=applicationManagerVO.getSpeed_status();



            if(ApplicationStatus.MODIFIED.name().equals(request.getParameter("status")))
            {
                applicationManagerVO.setAppliedToModify("N");
                //this is for set clause
                bankApplicationMasterVO=new BankApplicationMasterVO();
                bankApplicationMasterVO.setStatus(BankApplicationStatus.INVALIDATED.name());

                saved=applicationManager.updateBankApplicationMasterVO(bankApplicationMasterVO,null,applicationManagerVO.getMemberId());
            }
            if(ApplicationStatus.MODIFIED.name().equals(request.getParameter("status")))
            {
                applicationManagerVO.setAppliedToModify("N");
                //this is for set clause
                consolidatedApplicationVO=new ConsolidatedApplicationVO();
                consolidatedApplicationVO.setStatus(ConsolidatedAppStatus.INVALIDATED.name());
                saved=applicationManager.updateconsolidatedapplication(consolidatedApplicationVO, null, applicationManagerVO.getMemberId());
                logger.debug("updateConsolidatedMasterVO......."+saved);

            }
            if(ApplicationStatus.MODIFIED.name().equals(request.getParameter("status")))
            {
                applicationManagerVO.setAppliedToModify("N");
                //this is for set clause
                consolidatedApplicationVO=new ConsolidatedApplicationVO();
                consolidatedApplicationVO.setStatus(ConsolidatedAppStatus.INVALIDATED.name());
                saved=applicationManager.updateconsolidated_applicationHistoryStatus(consolidatedApplicationVO,null,applicationManagerVO.getMemberId());
                logger.debug("updateConsolidatedMasterVO......."+saved);

            }


            if(/*ApplicationStatus.SAVED.name().equals(request.getParameter("status")) || ApplicationStatus.SUBMIT.name().equals(request.getParameter("status")) || */ApplicationStatus.MODIFIED.name().equals(request.getParameter("status"))|| ((ApplicationStatus.VERIFIED.name().equals(request.getParameter("status"))) && !("Y".equals(request.getParameter("SPEED")))))
            {
                applicationManagerVO.setStatus(request.getParameter("status"));
                applicationManagerVO.setKyc_Status(request.getParameter("status"));
                applicationManagerVO.setMaf_Status(request.getParameter("status"));
                saved=applicationManager.updateAppManagerStatus(applicationManagerVO);

            }
            else if(ApplicationStatus.SAVED.name().equals(request.getParameter("status")) || ApplicationStatus.SUBMIT.name().equals(request.getParameter("status")))
            {
                String status = applicationManager.getAppManagerStatus(request.getParameter("apptoid"));
                if(!status.equals(ApplicationStatus.VERIFIED.name()))
                {
                    applicationManagerVO.setStatus(request.getParameter("status"));
                    applicationManagerVO.setKyc_Status(request.getParameter("status"));
                    applicationManagerVO.setMaf_Status(request.getParameter("status"));
                    saved = applicationManager.updateAppManagerStatus(applicationManagerVO);
                }
            }
            else if(ApplicationStatus.STEP1_SAVED.name().equals(request.getParameter("status")) || ApplicationStatus.STEP1_SUBMIT.name().equals(request.getParameter("status")) || ((ApplicationStatus.VERIFIED.name().equals(request.getParameter("status"))) && ("Y".equals(request.getParameter("SPEED")))))
            {
                applicationManagerVO.setSpeed_status(request.getParameter("status"));

                applicationManagerVO.setSpeed_user(applicationManagerVO.getSpeed_user());

                if(functions.isValueNull(applicationManagerVO.getSpeed_user()))
                {
                    applicationManagerVO.setSpeed_user(applicationManagerVO.getSpeed_user());
                }



                saved=applicationManager.updateAppManagerStatus(applicationManagerVO);
            }

            request.setAttribute("SAVED",saved);
            request.setAttribute("apptoid",applicationManagerVO.getMemberId());
            logger.error("saved"+saved);

            rdSuccess.forward(request, response);
        }
        catch (Exception e)
        {
            logger.error("Main class exception::", e);
        }

        PartnerDAO partnerDAO=new PartnerDAO();
        String memberid=applicationManagerVO.getMemberId();
        String partnerid="";
        if (functions.isValueNull(memberid)){
            partnerid =partnerDAO.getPartnerIdfromMemberid(memberid);
        }
        String status=request.getParameter("status");
        String mailbody="Merchant Application Form status has been changed to <b>"+status+"</b>";
        //client mail
        logger.error("Merchant id---"+applicationManagerVO.getMemberId());
        logger.error("Application id---"+applicationManagerVO.getApplicationId());
        logger.error("login---" + session.getAttribute("username"));
        logger.error("partnerid---"+applicationManagerVO.getPartnerid());
        String liveUrl = ApplicationProperties.getProperty("COMPANY_LIVE_URL");
        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
        HashMap mailValues=new HashMap();
        mailValues.put(MailPlaceHolder.USERNAME,session.getAttribute("username"));
        mailValues.put(MailPlaceHolder.PARTNERID,partnerid);
        mailValues.put(MailPlaceHolder.TOID, applicationManagerVO.getMemberId());
        mailValues.put(MailPlaceHolder.APPLICATION_ID,applicationManagerVO.getApplicationId());
        mailValues.put(MailPlaceHolder.PARTNER_URL,liveUrl);
        mailValues.put(MailPlaceHolder.LINE1,mailbody);
        asynchronousMailService.MAFStatusChange(MailEventEnum.MERCHANT_APPLICATION_FORM_STATUS_CHANGE, mailValues);
    }

}