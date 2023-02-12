package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.ApplicationManager;
import com.manager.GatewayManager;
import com.manager.vo.ActionVO;
import com.vo.applicationManagerVOs.BankTypeVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NIKET on 1/28/2016.
 */
public class AddOrUpdateMemberBankMapping extends HttpServlet
{
    private Logger logger = new Logger(AddOrUpdateMemberBankMapping.class.getName());

    private Functions functions = new Functions();

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        logger.debug("Inside AddOrUpdateMemberBankMapping REQUEST");

        HttpSession session = Functions.getNewSession(request);

        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        //Manager Instance
        ApplicationManager applicationManager = new ApplicationManager();
        GatewayManager gatewayManager = new GatewayManager();

        //VO instance
        ActionVO actionVO = new ActionVO();
        List<BankTypeVO> bankTypeVOs =new ArrayList<BankTypeVO>();

        //Validation Error List
        ValidationErrorList validationErrorList=null;

        boolean update=false;

        RequestDispatcher rdSuccess= request.getRequestDispatcher("/merchantmappingbank.jsp?ctoken=" + user.getCSRFToken());
        RequestDispatcher rdError= request.getRequestDispatcher("/merchantmappingbank.jsp?MES=ERR&ctoken=" + user.getCSRFToken());

        try
        {
            logger.debug("Inside AddOrUpdateMemberBankMapping");

            validationErrorList=validateMandatoryParameter(request);
            if(!validationErrorList.isEmpty())
            {
                logger.error("validation error");
                request.setAttribute("error",validationErrorList);
                rdError.forward(request,response);
                return;
            }

            actionVO.setAllContentAuto(request.getParameter("action"));

            String pgTypeIds[]=null;

            pgTypeIds =request.getParameterValues("pgTypeId");

            if(pgTypeIds!=null && pgTypeIds.length>0)
            {
                for(String pgTypeId:pgTypeIds)
                {

                    logger.error("Inside PgTypeId");
                    BankTypeVO bankTypeVO = new BankTypeVO();
                    bankTypeVO.setBankId(pgTypeId);
                    bankTypeVOs.add(bankTypeVO);
                }

                String memberId="";
                if(actionVO.isEdit())
                {
                    memberId = actionVO.getActionCriteria().split("_")[0];
                    applicationManager.deleteBankMerchantMappingForUpdate(actionVO.getActionCriteria().split("_")[0]);

                    update = applicationManager.insertBankMerchantMapping(bankTypeVOs,actionVO.getActionCriteria().split("_")[0]);
                }
                else if(actionVO.isAdd())
                {
                    memberId=request.getParameter("memberid");
                    update=applicationManager.insertBankMerchantMapping(bankTypeVOs,request.getParameter("memberid"));
                }
                else if(actionVO.isUpdate())
                {
                    //memberId=request.getParameter("memberid");

                    applicationManager.updateDefaultApplicationGatewayForPartnerAndPgTypeId(session.getAttribute("merchantid").toString(),null,false);
                    for(BankTypeVO bankTypeVO : bankTypeVOs)
                    {
                        update = applicationManager.updateDefaultApplicationGatewayForPartnerAndPgTypeId(session.getAttribute("merchantid").toString(),bankTypeVO.getBankId(),true);
                    }
                }

                //logger.debug("Gateway Type VOS:::"+gatewayTypeVOs);

                request.setAttribute("update",update);

                //This is for loading the changes done during upload of the bank template pdf order important

            }
            else
            {
                request.setAttribute("success","Updated Successfully");

                if(actionVO.isEdit())
                {
                    update=applicationManager.deleteBankMerchantMappingForUpdate(actionVO.getActionCriteria().split("_")[0]);
                }
                else if(actionVO.isUpdate())
                {
                    update=applicationManager.updateDefaultApplicationGatewayForPartnerAndPgTypeId(session.getAttribute("merchantid").toString(),null,false);
                }
                //rdError.forward(request, response);
            }
            rdSuccess.forward(request,response);
            return;
        }
        catch(PZDBViolationException e)
        {
            logger.error("Db violation exception while getting plane details", e);
            PZExceptionHandler.handleDBCVEException(e, null, null);
            request.setAttribute("catchError", "Kindly check for the Member Bank Mapping after sometime");
            rdError.forward(request, response);
            return;
        }

    }

    /**
     * Validator  For mandatory field
     * @param request
     * @return
     */
    private ValidationErrorList validateMandatoryParameter(HttpServletRequest request)
    {
        List<InputFields> inputMandatoryParameter= new ArrayList<InputFields>();
        inputMandatoryParameter.add(InputFields.SMALL_ACTION);

        InputValidator inputValidator = new InputValidator();
        ValidationErrorList validationErrorList = new ValidationErrorList();
        inputValidator.InputValidations(request,inputMandatoryParameter,validationErrorList,false);

        ActionVO actionVO = new ActionVO();

        actionVO.setAllContentAuto(request.getParameter("action"));

        if(validationErrorList.isEmpty())
        {
            if(!actionVO.isUpdate())
            {
                inputMandatoryParameter = new ArrayList<InputFields>();
                inputMandatoryParameter.add(InputFields.MEMBERID);
                inputValidator.InputValidations(request, inputMandatoryParameter, validationErrorList, false);
            }
        }
        return validationErrorList;
    }
}
