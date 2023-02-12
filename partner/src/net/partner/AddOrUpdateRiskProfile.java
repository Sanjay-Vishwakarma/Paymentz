package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.ProfileManagementManager;
import com.manager.vo.ActionVO;
import com.manager.vo.riskRuleVOs.ProfileVO;
import com.manager.vo.riskRuleVOs.RuleVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
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
import java.util.*;

/**
 * Created by Pradeep on 01/09/2015.
 */
public class AddOrUpdateRiskProfile extends HttpServlet
{
    private static Logger logger = new Logger(AddOrUpdateRiskProfile.class.getName());

    private Functions functions = new Functions();

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        HttpSession session = functions.getNewSession(request);

        PartnerFunctions partnerFunctions = new PartnerFunctions();
        if (!partnerFunctions.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        ValidationErrorList validationErrorList = null;

        //List of RISk VO VO instance
        List<RuleVO> riskRuleVOList = new ArrayList<RuleVO>();
        Map<String,String> presentRuleInProfile=new HashMap<String, String>();

        //Vo instance
        ProfileVO profileVO = new ProfileVO();
        ActionVO actionVO= new ActionVO();
        //Manager instance
        ProfileManagementManager profileManagementManager=new ProfileManagementManager();

        int rowCount=0;

        //boolean Instance
        boolean isProfileSaved=false;
        boolean isProfileAdd=false;
        boolean isRuleSaved=false;
        boolean ruleDeleted=false;
        boolean ruleThere=false;

        String riskId;
        String isApplicable;
        String score;

        Integer totalScore=0;


        RequestDispatcher rdSuccess=null;
        RequestDispatcher rdError=request.getRequestDispatcher("/addRiskProfile.jsp?MES=ERR&ctoken="+user.getCSRFToken());

        try
        {
            validationErrorList=validateCountOfRow(request);
            if(!validationErrorList.isEmpty())
            {
                logger.error("validation error ");

                if(((ActionVO)session.getAttribute("actionVO")).isEdit() || ((ActionVO)session.getAttribute("actionVO")).isView() || ((ActionVO)session.getAttribute("actionVO")).isAdd())
                {
                    request.setAttribute("riskRuleList",session.getAttribute("riskRuleList"));
                }

                request.setAttribute("actionVO",session.getAttribute("actionVO"));
                request.setAttribute("errorL",validationErrorList);
                rdError.forward(request,response);
                return;
            }

            rowCount= Integer.parseInt(request.getParameter("countOfRow"));
            actionVO.setAllContentAuto(request.getParameter("action"));
            logger.debug("isADD::::"+actionVO.getActionCriteria());
            logger.debug("COUNT::::"+rowCount);
            if(rowCount>0)
            {
                for (int i = 1; i <= rowCount; i++)
                {
                    riskId = request.getParameter("Text|riskProfileId_" + i);
                    isApplicable = request.getParameter("riskProfileIsApplicable_" + i);
                    score = request.getParameter("riskProfileScore_" + i);

                    if (functions.isValueNull(riskId))
                    {
                        setValidationForEachIteration(riskId, isApplicable, score, validationErrorList, i, totalScore);
                        if (!validationErrorList.isEmpty())
                        {

                            logger.error("validation error");
                            if(((ActionVO)session.getAttribute("actionVO")).isEdit() || ((ActionVO)session.getAttribute("actionVO")).isView() || ((ActionVO)session.getAttribute("actionVO")).isAdd())
                            {
                                request.setAttribute("riskRuleList",session.getAttribute("riskRuleList"));
                            }
                            request.setAttribute("actionVO",session.getAttribute("actionVO"));
                            request.setAttribute("no", i);
                            request.setAttribute("errorL", validationErrorList);
                            rdError.forward(request, response);
                            return;
                        }

                        profileVO.setName(request.getParameter("riskProfileName"));

                        RuleVO riskRuleVO = new RuleVO();

                        riskRuleVO.setId(request.getParameter("Text|riskProfileId_" + i));
                        riskRuleVO.setIsApplicable(("Y".equals(request.getParameter("riskProfileIsApplicable_" + i)))?true:false);
                        riskRuleVO.setScore(functions.isValueNull(request.getParameter("riskProfileScore_" + i))?Integer.parseInt(request.getParameter("riskProfileScore_" + i)):0);

                        riskRuleVOList.add(riskRuleVO);
                        ruleThere=true;
                    }
                }
            }
            else
            {
                if(((ActionVO)session.getAttribute("actionVO")).isEdit() || ((ActionVO)session.getAttribute("actionVO")).isView() || ((ActionVO)session.getAttribute("actionVO")).isAdd())
                {
                    request.setAttribute("riskRuleList",session.getAttribute("riskRuleList"));
                }

                request.setAttribute("actionVO",session.getAttribute("actionVO"));
                request.setAttribute("catchErrorL", "Please add risk rule for the profile");

                rdError.forward(request, response);
                return;
            }

            if(!ruleThere)
            {
                if(((ActionVO)session.getAttribute("actionVO")).isEdit() || ((ActionVO)session.getAttribute("actionVO")).isView() || ((ActionVO)session.getAttribute("actionVO")).isAdd())
                {
                    request.setAttribute("riskRuleList",session.getAttribute("riskRuleList"));
                }

                request.setAttribute("actionVO",session.getAttribute("actionVO"));
                request.setAttribute("catchErrorL", "Please add risk rule for the profile");

                rdError.forward(request, response);
                return;
            }

            if(actionVO.isAdd())
            {
                if (!profileManagementManager.uniqueRiskProfileName(profileVO.getName(), session.getAttribute("merchantid").toString()))
                {
                    logger.debug("insert new profile:::");
                    isProfileAdd = profileManagementManager.insertRiskProfileMapping(profileVO, session.getAttribute("merchantid").toString());
                }
                else
                {
                    logger.debug("Profile Name Already Exists");
                    request.setAttribute("msg", "Profile Name Already Exists.");
                    rdSuccess=request.getRequestDispatcher("/riskProfile.jsp?ctoken="+user.getCSRFToken());
                    rdSuccess.forward(request,response);
                    return;
                }
            }
            else
            {
                logger.debug("update profile:::");
                profileVO.setId(actionVO.getActionCriteria().split("_")[0]);
                isProfileSaved=  profileManagementManager.updateRiskProfileMapping(profileVO);
            }

            if(isProfileSaved)
            {
                ruleDeleted=profileManagementManager.deleteRiskProfileForProfile(profileVO.getId());
                for(RuleVO ruleVO:riskRuleVOList)
                {

                        isRuleSaved = profileManagementManager.insertRiskProfile(ruleVO, profileVO.getId());

                }

                rdSuccess=request.getRequestDispatcher("/net/RiskProfileList?profileid="+profileVO.getId()+"&ctoken="+user.getCSRFToken());
                request.setAttribute("update",isRuleSaved);
                rdSuccess.forward(request,response);
                return;
            }
            //Created condition to check result for added data.
            if(isProfileAdd)
            {
                ruleDeleted=profileManagementManager.deleteRiskProfileForProfile(profileVO.getId());
                for(RuleVO ruleVO:riskRuleVOList)
                {

                    isRuleSaved = profileManagementManager.insertRiskProfile(ruleVO, profileVO.getId());

                }

                rdSuccess=request.getRequestDispatcher("/net/RiskProfileList?profileid="+profileVO.getId()+"&ctoken="+user.getCSRFToken());
                request.setAttribute("insert",isRuleSaved);
                rdSuccess.forward(request,response);
                return;
            }

            request.setAttribute("catchErrorL","Kindly check on for the Risk Profile after some time");
            rdError.forward(request,response);
            return;


        }
        catch (PZDBViolationException e)
        {
            PZExceptionHandler.handleDBCVEException(e,null,null);
            request.setAttribute("catchErrorL","Kindly check on for the Risk Profile after some time");
            rdError.forward(request,response);
            return;
        }


    }

    private ValidationErrorList validateCountOfRow(HttpServletRequest request)
    {
        ValidationErrorList validationErrorList = new ValidationErrorList();
        List<InputFields> mandatoryField=new ArrayList<InputFields>();

        mandatoryField.add(InputFields.COUNTOFROW);
        mandatoryField.add(InputFields.SMALL_ACTION);
        mandatoryField.add(InputFields.RISKPROFILENAME);

        InputValidator inputValidator = new InputValidator();
        inputValidator.InputValidations(request,mandatoryField,validationErrorList,false);

        return  validationErrorList;

    }

    private void setValidationForEachIteration(String riskId,String isApplicable,String riskscore,ValidationErrorList validationErrorList,int id,Integer totalScore)
    {


        if (!ESAPI.validator().isValidInput("riskProfileId_"+id, riskId, "OnlyNumber", 10, false))
        {
            validationErrorList.addError("riskProfileId_"+id,new ValidationException("Invalid Risk Profile","Invalid Risk Profile:::::"+riskId,String.valueOf(id)));
        }
        if (!ESAPI.validator().isValidInput("riskProfileIsApplicable_"+id, isApplicable, "isYN", 10, true))
        {
            validationErrorList.addError("riskProfileIsApplicable_"+id,new ValidationException("Invalid Risk Is Applicable","Invalid Risk Is Applicable:::::"+isApplicable,String.valueOf(id)));
        }
        else if(functions.isValueNull(isApplicable))
        {
            if (!ESAPI.validator().isValidInput("riskProfileScore_" + id, riskscore, "Percentage", 10, false))
            {
                validationErrorList.addError("riskProfileScore_" + id, new ValidationException("Invalid Risk Profile score", "Invalid Risk Profile score:::::" + riskscore, String.valueOf(id)));
            }
        }
    }
}
