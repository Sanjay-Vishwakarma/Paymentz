package net.partner;

import com.directi.pg.Logger;
import com.manager.ApplicationManager;
import com.vo.applicationManagerVOs.*;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by Pradeep on 20/06/2015.
 */
public class PartnerConsolidatedApplication extends HttpServlet
{
    private static Logger logger= new Logger(PartnerConsolidatedApplication.class.getName());
    protected void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        logger.debug("inside PartnerConsolidatedApplication---");
        HttpSession session = request.getSession();

        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        ApplicationManager applicationManager = new ApplicationManager();
        //List og GatewayType
        List<BankTypeVO> bankTypeVOList=null;
        Map<String, List<BankApplicationMasterVO>> bankApplicationMasterVOs=null;
        Map<String,FileDetailsListVO> filedetailsVOs=null;
        ValidationErrorList validationErrorList=null;
        BankApplicationMasterVO bankApplicationMasterVO = null;

        ConsolidatedApplicationVO consolidatedApplicationVO = new ConsolidatedApplicationVO();
        Map<String,ConsolidatedApplicationVO> consolidatedApplicationVOMap =null;

        RequestDispatcher rdError = request.getRequestDispatcher("/partnerconsolidatedapplication.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        RequestDispatcher rdSuccess = request.getRequestDispatcher("/partnerconsolidatedapplication.jsp?MES=Success&ctoken="+user.getCSRFToken());
        try
        {
            validationErrorList=validateOptionalParameter(request);

            if(!validationErrorList.isEmpty())
            {
                request.setAttribute("error",validationErrorList);
                rdError.forward(request,response);
                return;
            }
            bankTypeVOList = applicationManager.getListOfAllBankTypeWithAvailableTemplateName();

            bankApplicationMasterVO=new BankApplicationMasterVO();
            bankApplicationMasterVO.setMember_id(request.getParameter("memberid"));
            consolidatedApplicationVO.setMemberid(request.getParameter("memberid"));
            bankApplicationMasterVO.setStatus("VERIFIED");

            bankApplicationMasterVOs=applicationManager.getBankApplicationMasterVOForGatewayId(bankApplicationMasterVO," timestamp desc",null);
            filedetailsVOs=applicationManager.getApplicationUploadedDetail(request.getParameter("memberid"));
            consolidatedApplicationVOMap=applicationManager.getconsolidated_application(consolidatedApplicationVO);
            request.setAttribute("consolidatedApplicationVOMap",consolidatedApplicationVOMap);
            request.setAttribute("gatewayTypeVOList",bankTypeVOList);
            request.setAttribute("bankApplicationMasterVOs",bankApplicationMasterVOs);
            request.setAttribute("filedetailsVOs",filedetailsVOs);
            request.setAttribute("pid",request.getParameter("partnerid"));
            rdSuccess.forward(request,response);
        }
        catch (Exception e)
        {
            logger.error("Main class exception::", e);
        }

    }

    protected void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request, response);
    }
    private ValidationErrorList validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.MEMBERID);
        ValidationErrorList validationErrorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListMandatory,validationErrorList,false);
        return validationErrorList;
    }

}
