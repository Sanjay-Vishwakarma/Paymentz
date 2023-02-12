package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.ProfileManagementManager;
import com.manager.enums.PZOperatorEnums;
import com.manager.enums.RuleTypeEnum;
import com.manager.vo.ActionVO;
import com.manager.vo.PayIfeTableInfo;
import com.manager.vo.businessRuleVOs.ProfileVO;
import com.manager.vo.businessRuleVOs.RuleOperation;
import com.manager.vo.businessRuleVOs.RuleVO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Niket on 01/09/2015.
 */
public class AddOrUpdateBusinessProfile extends HttpServlet
{
    private static Logger logger = new Logger(AddOrUpdateBusinessProfile.class.getName());

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
        List<RuleVO> businessRuleVOList = new ArrayList<RuleVO>();

        //Vo instance
        ProfileVO profileVO = new ProfileVO();
        ActionVO actionVO = new ActionVO();

        //Manager instance
        ProfileManagementManager profileManagementManager=new ProfileManagementManager();

        int rowCount=0;

        //boolean Instance
        boolean isProfileSaved=false;
        boolean isProfileAdd=false;
        boolean isRuleSaved=false;
        boolean ruleDeleted=false;
        boolean ruleThere=false;

        String businessId;
        String ruleType;
        String isApplicable;



        RequestDispatcher rdSuccess=null;
        RequestDispatcher rdError=request.getRequestDispatcher("/addBusinessProfile.jsp?MES=ERR&ctoken="+user.getCSRFToken());

        try
        {
            validationErrorList=validateCountOfRow(request);
            if(!validationErrorList.isEmpty())
            {
                //TODO error forward
                logger.error("validation error");


                if(((ActionVO)session.getAttribute("actionVO")).isEdit() || ((ActionVO)session.getAttribute("actionVO")).isView() || ((ActionVO)session.getAttribute("actionVO")).isAdd())
                {
                    request.setAttribute("businessRuleList",session.getAttribute("businessRuleList"));
                    request.setAttribute("payIfeTableInfo",session.getAttribute("payIfeTableInfo"));
                }

                request.setAttribute("actionVO",session.getAttribute("actionVO"));
                request.setAttribute("errorL",validationErrorList);
                rdError.forward(request,response);
                return;
            }
            profileVO.setName(request.getParameter("businessProfileName"));

            Map<String,RuleVO> businessRuleMap= (Map<String, RuleVO>) session.getAttribute("businessRuleList");
            Map<String,PayIfeTableInfo> payIfeTableInfoMap= (Map<String, PayIfeTableInfo>) session.getAttribute("payIfeTableInfo");


            rowCount= Integer.parseInt(request.getParameter("countOfRow"));
            actionVO.setAllContentAuto(request.getParameter("action"));

            logger.debug("COUNT::::"+rowCount);
            if(rowCount>0)
            {
                for (int i = 1; i <= rowCount; i++)
                {
                    businessId = request.getParameter("Text|businessProfileId_" + i);




                    if (functions.isValueNull(businessId) && businessRuleMap.containsKey(businessId))
                    {
                        RuleVO ruleVO=businessRuleMap.get(businessId);

                        isApplicable = request.getParameter("businessProfileIsApplicable_" + i);


                        setValidationForEachIteration(ruleVO,request,payIfeTableInfoMap, validationErrorList, i);
                        if (!validationErrorList.isEmpty())
                        {

                            logger.error("validation error");

                            if(((ActionVO)session.getAttribute("actionVO")).isEdit() || ((ActionVO)session.getAttribute("actionVO")).isView() || ((ActionVO)session.getAttribute("actionVO")).isAdd())
                            {
                                request.setAttribute("businessRuleList",session.getAttribute("businessRuleList"));
                                request.setAttribute("payIfeTableInfo",session.getAttribute("payIfeTableInfo"));
                            }

                            request.setAttribute("actionVO",session.getAttribute("actionVO"));
                            request.setAttribute("errorL", validationErrorList);
                            request.setAttribute("no",i);
                            rdError.forward(request, response);
                            return;
                        }



                        businessRuleVOList.add(ruleVO);

                        ruleThere=true;
                    }
                }
            }
            else
            {
                if(((ActionVO)session.getAttribute("actionVO")).isEdit() || ((ActionVO)session.getAttribute("actionVO")).isView() || ((ActionVO)session.getAttribute("actionVO")).isAdd())
                {
                    request.setAttribute("businessRuleList",session.getAttribute("businessRuleList"));
                    request.setAttribute("payIfeTableInfo",session.getAttribute("payIfeTableInfo"));
                }

                request.setAttribute("actionVO",session.getAttribute("actionVO"));

                request.setAttribute("catchErrorL", "Please add business rule for the profile");
                rdError.forward(request, response);
                return;
            }

            if(!ruleThere)
            {
                if(((ActionVO)session.getAttribute("actionVO")).isEdit() || ((ActionVO)session.getAttribute("actionVO")).isView() || ((ActionVO)session.getAttribute("actionVO")).isAdd())
                {
                    request.setAttribute("businessRuleList",session.getAttribute("businessRuleList"));
                    request.setAttribute("payIfeTableInfo",session.getAttribute("payIfeTableInfo"));
                }

                request.setAttribute("actionVO",session.getAttribute("actionVO"));

                request.setAttribute("catchErrorL", "Please add business rule for the profile");
                rdError.forward(request, response);
                return;
            }

            if(actionVO.isAdd())
            {
                if (!profileManagementManager.uniqueBusinessProfileName(profileVO.getName(), session.getAttribute("merchantid").toString()))
                {
                    logger.debug("insert new profile:::");
                    isProfileAdd = profileManagementManager.insertBusinessProfileMapping(profileVO, session.getAttribute("merchantid").toString());
                }
                else
                {
                    logger.debug("Profile Name Already Exists");
                    request.setAttribute("msg", "Profile Name Already Exists.");
                    rdSuccess=request.getRequestDispatcher("/businessProfile.jsp?ctoken="+user.getCSRFToken());
                    rdSuccess.forward(request,response);
                    return;
                }
            }
            else
            {
                logger.debug("update profile:::");
                profileVO.setId(actionVO.getActionCriteria().split("_")[0]);
                isProfileSaved=  profileManagementManager.updateBusinessProfileMapping(profileVO , session.getAttribute("merchantid").toString());
            }
            /*if(actionVO.isAdd())
            {
                isProfileSaved=profileManagementManager.insertBusinessProfileMapping(profileVO, session.getAttribute("merchantid").toString());
            }
            else
            {
                profileVO.setId(actionVO.getActionCriteria().split("_")[0]);
                isProfileSaved=  profileManagementManager.updateBusinessProfileMapping(profileVO, session.getAttribute("merchantid").toString());
            }*/

            if(isProfileSaved)
            {

                ruleDeleted=profileManagementManager.deleteBusinessProfileForProfile(profileVO.getId());
                for(RuleVO ruleVO:businessRuleVOList)
                {
                    isRuleSaved=profileManagementManager.insertBusinessProfile(ruleVO, profileVO.getId());
                }

                rdSuccess=request.getRequestDispatcher("/net/BusinessProfileList?profileid="+profileVO.getId()+"&ctoken="+user.getCSRFToken());
                request.setAttribute("update", isRuleSaved);
                rdSuccess.forward(request,response);
                return;
            }
            //Created condition to check result for added data.
            if(isProfileAdd)
            {

                ruleDeleted=profileManagementManager.deleteBusinessProfileForProfile(profileVO.getId());
                for(RuleVO ruleVO:businessRuleVOList)
                {
                    isRuleSaved=profileManagementManager.insertBusinessProfile(ruleVO, profileVO.getId());
                }

                rdSuccess=request.getRequestDispatcher("/net/BusinessProfileList?profileid="+profileVO.getId()+"&ctoken="+user.getCSRFToken());
                request.setAttribute("insert", isRuleSaved);
                rdSuccess.forward(request,response);
                return;
            }

            request.setAttribute("catchErrorL","Kindly check on for the Business Profile after some time");
            rdError.forward(request,response);
            return;


        }
        catch (PZDBViolationException e)
        {
            PZExceptionHandler.handleDBCVEException(e, null, null);
            request.setAttribute("actionVO",session.getAttribute("actionVO"));
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
        mandatoryField.add(InputFields.BUSINESSPROFILENAME);

        InputValidator inputValidator = new InputValidator();
        inputValidator.InputValidations(request,mandatoryField,validationErrorList,false);

        return  validationErrorList;

    }

    private void setValidationForEachIteration(RuleVO ruleVO,HttpServletRequest request,Map<String,PayIfeTableInfo> payIfeTableInfoMap,ValidationErrorList validationErrorList,int id)
    {

        if (!ESAPI.validator().isValidInput("businessProfileIsApplicable_"+id, request.getParameter("businessProfileIsApplicable_" + id), "isYN", 255, true))
        {
            validationErrorList.addError("businessProfileIsApplicable_"+id,new ValidationException("Invalid Business Is Applicable","Invalid Business Is Applicable:::::"+request.getParameter("businessProfileIsApplicable_" + id),String.valueOf(id)));
        }
        else if(functions.isValueNull(request.getParameter("businessProfileIsApplicable_" + id)))
        {
            ruleVO.setIsApplicable(true);
            int i = 1;
            for (RuleOperation ruleOperation : ruleVO.getRuleOperation())
            {
                if (RuleTypeEnum.FLAT_FILE.name().equals(ruleVO.getRuleType()))
                {
                    if (!ESAPI.validator().isValidInput("businessProfileValue1_" + id + "_" + i, request.getParameter("businessProfileValue1_" + id + "_" + i), "SafeString", 255, false))
                    {
                        validationErrorList.addError("businessProfileValue1_" + id + "_" + i, new ValidationException("Invalid Business Profile FilePath", "Invalid Business Profile FilePath:::::" + request.getParameter("businessProfileValue1_" + id + "_" + i), String.valueOf(id)));
                    }
                    else
                    {
                        ruleOperation.setValue1(request.getParameter("businessProfileValue1_" + id + "_" + i));
                    }
                }
                else if (RuleTypeEnum.REGULAR_EXPRESSION.name().equals(ruleVO.getRuleType()))
                {

                }
                else if (RuleTypeEnum.DATABASE.name().equals(ruleVO.getRuleType()))
                {
                    String inputName = null;
                    String aggregateFunction = "";
                    if (ruleOperation.getInputName().contains("(") || ruleOperation.getInputName().contains(")"))
                    {
                        inputName = ruleOperation.getInputName().substring(ruleOperation.getInputName().indexOf("(") + 1, ruleOperation.getInputName().indexOf(")"));
                        aggregateFunction = ruleOperation.getInputName().substring(0, ruleOperation.getInputName().indexOf("(")) + " Of ";
                    }
                    else
                    {
                        inputName = ruleOperation.getInputName();
                    }

                    String validationProperty = "SafeString";
                    if ("INT".equals(ruleOperation.getDataType()))
                    {
                        validationProperty = "OnlyNumber";
                    }
                    else if ("DECIMAL".equals(ruleOperation.getDataType()))
                    {
                        validationProperty = "Amount";
                    }
                    else if ("ENUM".equals(ruleOperation.getDataType()))
                    {
                        validationProperty = "SafeString";
                    }

                    if (!ESAPI.validator().isValidInput("businessProfileValue1_" + id + "_" + i, request.getParameter("businessProfileValue1_" + id + "_" + i), validationProperty, 255, false))
                    {
                        validationErrorList.addError("businessProfileValue1_" + id+ "_" + i, new ValidationException("Invalid Business Profile Value1", "Invalid Business Profile Value1:::::" + request.getParameter("businessProfileValue1_" + id + "_" + i), String.valueOf(id)));
                    }
                    else
                    {
                        ruleOperation.setValue1(request.getParameter("businessProfileValue1_" + id + "_" + i));
                    }

                    if (PZOperatorEnums.BETWEEN.name().equals(ruleOperation.getOperator()))
                    {
                        if (!ESAPI.validator().isValidInput("businessProfileValue2_" + id + "_" + i, request.getParameter("businessProfileValue2_" + id + "_" + i), validationProperty, 255, false))
                        {
                            validationErrorList.addError("businessProfileValue2_" + id+ "_" + i, new ValidationException("Invalid Business Profile Value2", "Invalid Business Profile Value2:::::" + request.getParameter("businessProfileValue2_" + id + "_" + i), String.valueOf(id)));
                        }
                        else
                        {
                            ruleOperation.setValue2(request.getParameter("businessProfileValue2_" + id + "_" + i));
                        }
                    }
                    }
                else
                {
                    String validationProperty = "SafeString";
                    if ("INT".equals(ruleOperation.getDataType()))
                    {
                        validationProperty = "OnlyNumber";
                    }
                    else if ("DECIMAL".equals(ruleOperation.getDataType()))
                    {
                        validationProperty = "Amount";
                    }
                    else if ("ENUM".equals(ruleOperation.getDataType()))
                    {
                        validationProperty = "SafeString";
                    }

                    if (!ESAPI.validator().isValidInput("businessProfileValue1_" + id + "_" + i, request.getParameter("businessProfileValue1_" + id + "_" + i), validationProperty, 255, false))
                    {
                        validationErrorList.addError("businessProfileValue1_" + id + "_" + i, new ValidationException("Invalid Business Profile Value1", "Invalid Business Profile Value1:::::" + request.getParameter("businessProfileValue1_" + id + "_" + i), String.valueOf(id)));
                    }
                    else
                    {
                        ruleOperation.setValue1(request.getParameter("businessProfileValue1_" + id + "_" + i));
                    }

                    if (PZOperatorEnums.BETWEEN.name().equals(ruleOperation.getOperator()))
                    {
                        if (!ESAPI.validator().isValidInput("businessProfileValue2_" + id + "_" + i, request.getParameter("businessProfileValue2_" + id + "_" + i), validationProperty, 255, false))
                        {
                            validationErrorList.addError("businessProfileValue2_" + id + "_" + i, new ValidationException("Invalid Business Profile Value2", "Invalid Business Profile Value2:::::" + request.getParameter("businessProfileValue2_" + id + "_" + i), String.valueOf(id)));
                        }
                        else
                        {
                            ruleOperation.setValue1(request.getParameter("businessProfileValue2_" + id + "_" + i));
                        }
                    }
                }

                i++;
            }
        }
        else
        {
            ruleVO.setIsApplicable(false);
        }
    }
}
