import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemAccessLogger;
import com.manager.ProfileManagementManager;
import com.manager.enums.PZOperatorEnums;
import com.manager.enums.RuleTypeEnum;
import com.manager.vo.ActionVO;
import com.manager.vo.PayIfeTableInfo;
import com.manager.vo.riskRuleVOs.RuleOperation;
import com.manager.vo.riskRuleVOs.RuleVO;

import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.operations.PZOperations;
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

import java.util.*;

/**
 * Created by PZ on 17/8/15.
 */
public class AddRiskRuleDetails extends HttpServlet
{
    private static Logger logger =new Logger(AddRiskRuleDetails.class.getName());
    private Functions functions = new Functions();

    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {

        HttpSession session = functions.getNewSession(request);

        Admin admin=new Admin();
        if (!admin.isLoggedIn(session))
        {
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        ProfileManagementManager profileManagementManager = new ProfileManagementManager();
        ActionVO actionVO = new ActionVO();
        RuleVO riskRuleVo=new RuleVO();

        ValidationErrorList validationErrorList=null;

        boolean saved=false;

        RequestDispatcher rdSuccess= null;
        RequestDispatcher rdError= request.getRequestDispatcher("/addOrUpdateRiskRule.jsp?MES=ERR&ctoken=" + user.getCSRFToken());

        try
        {
            logger.debug("action :::"+request.getParameter("action"));
            validationErrorList=validateParameters(request,riskRuleVo);
            System.out.println(request.getParameter("riskRuleName"));
            System.out.println(request.getParameter("riskRuleLabel"));
            System.out.println(request.getParameter("riskRuleDescription"));
            if (!ESAPI.validator().isValidInput("riskRuleName", request.getParameter("riskRuleName"), "companyName", 100, false))
                validationErrorList.addError("riskRuleName", new ValidationException("Invalid Risk Rule Name", "Invalid Risk Rule Name:::" + request.getParameter("riskRuleName")));


            if (!ESAPI.validator().isValidInput("riskRuleLabel", request.getParameter("riskRuleLabel"), "companyName", 100, false))
                    validationErrorList.addError("riskRuleLabel", new ValidationException("Invalid Risk Rule Label", "Invalid Risk Rule Label:::" + request.getParameter("riskRuleLabel")));


            if (!ESAPI.validator().isValidInput("riskRuleDescription", request.getParameter("riskRuleDescription"), "companyName", 100, false))
                    validationErrorList.addError("riskRuleDescription", new ValidationException("Invalid Risk Rule Description", "Invalid Risk Rule Description:::" + request.getParameter("riskRuleDescription")));

            if(!validationErrorList.isEmpty())
            {
                request.setAttribute("ruleVO", riskRuleVo);
                request.setAttribute("actionVO",session.getAttribute("actionVO"));
                request.setAttribute("error",validationErrorList);
                rdError.forward(request,response);
                return;
            }

            actionVO.setAllContentAuto(request.getParameter("action"));

            riskRuleVo.setName(request.getParameter("riskRuleName"));
            riskRuleVo.setLabel(request.getParameter("riskRuleLabel"));
            riskRuleVo.setDescription(request.getParameter("riskRuleDescription"));
            riskRuleVo.setRuleType(request.getParameter("ruleType"));

            if (RuleTypeEnum.DATABASE.name().equals(riskRuleVo.getRuleType()))
            {
                riskRuleVo.setQuery(request.getParameter("businessRuleQuery"));
            }
            if(actionVO.isAdd())
            {
                logger.debug("action ADD:::"+actionVO.isAdd());
                RuleVO ruleVO=profileManagementManager.getSingleRiskDetails(null,riskRuleVo.getName());
                if(ruleVO==null)
                {
                    saved = profileManagementManager.insertRuleDefinition(riskRuleVo);
                    if(saved)
                    {
                        saved=profileManagementManager.insertRiskRuleOperations(riskRuleVo.getRuleOperation(),riskRuleVo.getId());
                    }
                }
                else
                {
                    request.setAttribute("actionVO",session.getAttribute("actionVO"));
                    request.setAttribute("catchError","Risk Rule Is present");
                    rdError.forward(request,response);
                    return;
                }
            }
            else if(actionVO.isEdit())
            {
                logger.debug("action Edit:::"+actionVO.isEdit());
                saved =profileManagementManager.updateRuleDefinition(riskRuleVo, actionVO.getActionCriteria().split("_")[0]);
                if(saved)
                {
                    profileManagementManager.deleteRiskRuleOperations(actionVO.getActionCriteria().split("_")[0]);
                    saved=profileManagementManager.insertRiskRuleOperations(riskRuleVo.getRuleOperation(),actionVO.getActionCriteria().split("_")[0]);
                }
                riskRuleVo.setId(actionVO.getActionCriteria().split("_")[0]);
            }
            if(saved)
                rdSuccess=request.getRequestDispatcher("/servlet/RiskRuleDetails?ruleid="+riskRuleVo.getId()+"&MES=SUCCESS&ctoken=" + user.getCSRFToken());
            else
            {
                request.setAttribute("actionVO",actionVO);
                request.setAttribute("catchError","Kindly check for the Risk Rule After sometime");
                rdError.forward(request,response);
                return;
            }
            request.setAttribute("saved",saved);
            request.setAttribute("actionVO",actionVO);
            rdSuccess.forward(request,response);

        }
        catch (PZDBViolationException e)
        {
            logger.error("Db violation exception while updating Risk Rule details",e);
            PZExceptionHandler.handleDBCVEException(e, null, PZOperations.RISK_RULE);
            request.setAttribute("catchError","Kindly check for the Risk Rule After sometime");
            rdError.forward(request,response);
            return;
        }

    }

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
    {
        doGet(request, response);
    }

    private ValidationErrorList validateParameters(HttpServletRequest request,RuleVO ruleVO) throws PZDBViolationException
    {
        List<RuleOperation> ruleOperationsList=new ArrayList<RuleOperation>();

        List<InputFields> inputMandatoryParameter= new ArrayList<InputFields>();
        inputMandatoryParameter.add(InputFields.SMALL_ACTION);
        /*inputMandatoryParameter.add(InputFields.RISK_RULE_NAME);
        inputMandatoryParameter.add(InputFields.RISK_RULE_LABEL);
        inputMandatoryParameter.add(InputFields.RISK_RULE_DESCRIPTION);*/
        inputMandatoryParameter.add(InputFields.RULETYPE);

        InputValidator inputValidator = new InputValidator();
        ValidationErrorList validationErrorList = new ValidationErrorList();

        inputValidator.InputValidations(request,inputMandatoryParameter,validationErrorList,false);



        List<InputFields> inputConditionalParameter = new ArrayList<InputFields>();
        if ("DATABASE".equals(request.getParameter("ruleType")))
        {
            inputConditionalParameter.add(InputFields.QUERY);
            ruleVO.setRuleType(RuleTypeEnum.DATABASE.name());
            setValidationErrorForRequestRelatedToType(request, RuleTypeEnum.DATABASE, ruleOperationsList, validationErrorList);
        }
        else if ("COMPARATOR".equals(request.getParameter("ruleType")))
        {
            ruleVO.setRuleType(RuleTypeEnum.COMPARATOR.name());
            setValidationErrorForRequestRelatedToType(request, RuleTypeEnum.COMPARATOR,ruleOperationsList,validationErrorList);
        }
        else if ("REGULAR_EXPRESSION".equals(request.getParameter("ruleType")))
        {
            ruleVO.setRuleType(RuleTypeEnum.REGULAR_EXPRESSION.name());
            setValidationErrorForRequestRelatedToType(request, RuleTypeEnum.REGULAR_EXPRESSION,ruleOperationsList,validationErrorList);
        }
        else if ("FLAT_FILE".equals(request.getParameter("ruleType")))
        {
            ruleVO.setRuleType(RuleTypeEnum.FLAT_FILE.name());
            setValidationErrorForRequestRelatedToType(request, RuleTypeEnum.FLAT_FILE,ruleOperationsList,validationErrorList);
        }
        inputValidator.InputValidations(request, inputConditionalParameter, validationErrorList, false);

        logger.debug("SIZE:::"+ruleOperationsList.size());
        ruleVO.setRuleOperation(ruleOperationsList);

        return validationErrorList;
    }

    private List<RuleOperation> setValidationErrorForRequestRelatedToType(HttpServletRequest request, RuleTypeEnum ruleTypeEnum,List<RuleOperation> ruleOperationList,ValidationErrorList validationErrorList) throws PZDBViolationException
    {
        logger.debug("ruleTypeEnum.name()---->"+ruleTypeEnum.name());
        if (RuleTypeEnum.REGULAR_EXPRESSION.name().contains(ruleTypeEnum.name()))
        {
            String regexInputSelected[] = request.getParameterValues("regexInputName");

            if (regexInputSelected != null)
            {


                for (int i = 1; i <= regexInputSelected.length; i++)
                {
                    logger.debug("REGEX operation::"+i);
                    RuleOperation ruleOperation = new RuleOperation();
                    if (!ESAPI.validator().isValidInput("regex_" + i, request.getParameter("regex_" + i), "SafeString", 255, false))
                    {
                        validationErrorList.addError("regex_" + i, new ValidationException("Invalid Regex", "Invalid Regex :::" + request.getParameter("regex_" + i)));
                    }
                    else
                    {
                        ruleOperation.setRegex(request.getParameter("regex_" + i));
                    }
                    if (!ESAPI.validator().isValidInput("regexOperator_" + i, request.getParameter("regexOperator_" + i), "SafeString", 100, false))
                    {
                        validationErrorList.addError("regexOperator_" + i, new ValidationException("Invalid Regex Operator", "Invalid Regex Operator:::" + request.getParameter("flatFileOperator_" + i)));
                    }
                    else
                    {
                        ruleOperation.setOperator(request.getParameter("regexOperator_" + i));
                    }

                    if (!ESAPI.validator().isValidInput("regexInputName_" + i, request.getParameter("regexInputName_" + i), "SafeString", 100, false))
                    {
                        validationErrorList.addError("regexInputName_" + i, new ValidationException("Invalid Regex Input Name", "Invalid Regex Input Name:::" + request.getParameter("regexInputName_" + i)));
                    }
                    else
                    {
                        ruleOperation.setInputName(request.getParameter("regexInputName_" + i));
                    }

                    if(!ESAPI.validator().isValidInput("isMandatory_" + i, request.getParameter("isMandatory_" + i), "isYN", 5, false))
                    {
                        ruleOperation.setMandatory(false);
                    }
                    else if(functions.isValueNull(request.getParameter("isMandatory_" + i)) && "Y".equalsIgnoreCase(request.getParameter("isMandatory_" + i)))
                    {
                        ruleOperation.setMandatory(true);
                    }

                    if (i < regexInputSelected.length)
                    {
                        if (!ESAPI.validator().isValidInput("regexComparator_" + i, request.getParameter("regexComparator_" + i), "SafeString", 100, false))
                        {
                            validationErrorList.addError("regexComparator_" + i, new ValidationException("Invalid Regex Comparator", "Invalid Regex Comparator:::" + request.getParameter("regexComparator_" + i)));
                        }
                        else
                        {
                            ruleOperation.setComparator(request.getParameter("regexComparator_" + i));
                        }
                    }

                    ruleOperationList.add(ruleOperation);
                }
            }
            else
            {
                validationErrorList.addError("regexInputName", new ValidationException("Invalid Regex Rule", "Invalid Regex Rule:::" + request.getParameter("regexInputName")));
            }

        }
        else if (RuleTypeEnum.FLAT_FILE.name().contains(ruleTypeEnum.name()))
        {

            String flatFileInputSelected[] = request.getParameterValues("flatFileInputName");
            if (flatFileInputSelected != null)
            {


                for (int i = 1; i <= flatFileInputSelected.length; i++)
                {
                    logger.debug("Flat File operation::"+i);
                    RuleOperation ruleOperation = new RuleOperation();
                    if (!ESAPI.validator().isValidInput("flatFileInputName_" + i, request.getParameter("flatFileInputName_" + i), "SafeString", 255, false))
                    {
                        validationErrorList.addError("flatFileInputName_" + i, new ValidationException("Invalid Flat File Input Name", "Invalid Flat File Input Name :::" + request.getParameter("flatFileInputName_" + i)));
                    }
                    else
                    {
                        ruleOperation.setInputName(request.getParameter("flatFileInputName_" + i));
                    }

                    if (!ESAPI.validator().isValidInput("flatFileOperator_" + i, request.getParameter("flatFileOperator_" + i), "SafeString", 100, false))
                    {
                        validationErrorList.addError("flatFileOperator_" + i, new ValidationException("Invalid Flat File Operator", "Invalid Flat File Operator:::" + request.getParameter("flatFileOperator_" + i)));
                    }
                    else
                    {
                        ruleOperation.setOperator(request.getParameter("flatFileOperator_" + i));
                    }

                    if (!ESAPI.validator().isValidInput("riskRuleValue1_" + i, request.getParameter("riskRuleValue1_" + i), "SafeString", 100, false))
                    {
                        validationErrorList.addError("riskRuleValue1_" + i, new ValidationException("Invalid Value1", "Invalid Value1:::" + request.getParameter("riskRuleValue1_" + i)));
                    }
                    else
                    {
                        ruleOperation.setValue1(request.getParameter("riskRuleValue1_" + i));
                    }

                    if (i < flatFileInputSelected.length)
                    {
                        if (!ESAPI.validator().isValidInput("flatFileComparator_" + i, request.getParameter("flatFileComparator_" + i), "SafeString", 100, false))
                        {
                            validationErrorList.addError("flatFileComparator_" + i, new ValidationException("Invalid Flat File Comparator", "Invalid Flat File Comparator:::" + request.getParameter("flatFileComparator_" + i)));
                        }
                        else
                        {
                            ruleOperation.setComparator(request.getParameter("flatFileComparator_" + i));
                        }
                    }

                    ruleOperationList.add(ruleOperation);
                }
            }
            else
            {
                validationErrorList.addError("flatFileInputName", new ValidationException("Invalid Flat File Rule", "Invalid Flat File Rule:::" + request.getParameter("flatFileInputName")));
            }
        }
        else if(RuleTypeEnum.COMPARATOR.name().contains(ruleTypeEnum.name()))
        {
            String comparatorInputSelected[] = request.getParameterValues("comparatorInputName");
            if (comparatorInputSelected != null)
            {


                for (int i = 1; i <= comparatorInputSelected.length; i++)
                {
                    logger.debug("Comparator operation::"+i);
                    RuleOperation ruleOperation = new RuleOperation();
                    if (!ESAPI.validator().isValidInput("comparatorInputName_" + i, request.getParameter("comparatorInputName_" + i), "SafeString", 255, false))
                    {
                        validationErrorList.addError("comparatorInputName_" + i, new ValidationException("Invalid Comparator Input Name", "Invalid Comparator Input Name :::" + request.getParameter("comparatorInputName_" + i)));
                    }
                    else
                    {
                        ruleOperation.setInputName(request.getParameter("comparatorInputName_" + i));
                    }

                    if (!ESAPI.validator().isValidInput("comparatorOperator_" + i, request.getParameter("comparatorOperator_" + i), "SafeString", 100, false))
                    {
                        validationErrorList.addError("comparatorOperator_" + i, new ValidationException("Invalid Comparator Operator", "Invalid Comparator Operator:::" + request.getParameter("comparatorOperator_" + i)));
                    }
                    else if(functions.isValueNull(request.getParameter("comparatorOperator_" + i)))
                    {
                        ruleOperation.setOperator(request.getParameter("comparatorOperator_" + i));
                        if(PZOperatorEnums.BETWEEN.name().equals(request.getParameter("comparatorOperator_" + i)))
                        {
                            if (!ESAPI.validator().isValidInput("riskRuleValue2_" + i, request.getParameter("riskRuleValue2_" + i), "SafeString", 100, false))
                            {
                                validationErrorList.addError("riskRuleValue2_" + i, new ValidationException("Invalid Value2", "Invalid Value2:::" + request.getParameter("riskRuleValue2_" + i)));
                            }
                            else
                            {
                                ruleOperation.setValue1(request.getParameter("riskRuleValue2_" + i));
                            }
                        }
                    }

                    if (!ESAPI.validator().isValidInput("riskRuleValue1_" + i, request.getParameter("riskRuleValue1_" + i), "SafeString", 100, false))
                    {
                        validationErrorList.addError("riskRuleValue1_" + i, new ValidationException("Invalid Value1", "Invalid Value1:::" + request.getParameter("riskRuleValue1_" + i)));
                    }
                    else
                    {
                        ruleOperation.setValue1(request.getParameter("riskRuleValue1_" + i));
                    }

                    if (i < comparatorInputSelected.length)
                    {
                        if (!ESAPI.validator().isValidInput("comparatorComparator_" + i, request.getParameter("comparatorComparator_" + i), "SafeString", 100, false))
                        {
                            validationErrorList.addError("comparatorComparator_" + i, new ValidationException("Invalid Comparator Comparator", "Invalid Comparator Comparator:::" + request.getParameter("comparatorComparator_" + i)));
                        }
                        else
                        {
                            ruleOperation.setComparator(request.getParameter("comparatorComparator_" + i));
                        }
                    }

                    ruleOperationList.add(ruleOperation);
                }
            }
            else
            {
                validationErrorList.addError("comparatorInputName", new ValidationException("Invalid Comparator Rule", "Invalid Comparator Rule:::" + request.getParameter("comparatorInputName")));
            }
        }
        else if(RuleTypeEnum.DATABASE.name().equals(ruleTypeEnum.name()))
        {
            ProfileManagementManager profileManagementManager = new ProfileManagementManager();
            Map<String,PayIfeTableInfo> payIfeTableInfoMap =profileManagementManager.getAllPayIfeFieldsInformation(null,null,null);
            String querySelectSelected[] = request.getParameterValues("querySelect");
            if (querySelectSelected != null)
            {


                for (int i = 1; i <= querySelectSelected.length; i++)
                {
                    logger.debug("Database operation::"+i);
                    RuleOperation ruleOperation = new RuleOperation();
                    if (!ESAPI.validator().isValidInput("select_" + i, request.getParameter("select_" + i), "SafeString", 255, false))
                    {
                        validationErrorList.addError("select_" + i, new ValidationException("Invalid Select Statement", "Invalid Select Statement :::" + request.getParameter("select_" + i)));
                    }
                    else
                    {
                        ruleOperation.setInputName(request.getParameter("select_" + i));
                    }

                    if (!ESAPI.validator().isValidInput("selectOperator_" + i, request.getParameter("selectOperator_" + i), "SafeString", 100, false))
                    {
                        validationErrorList.addError("selectOperator_" + i, new ValidationException("Invalid Select Operator", "Invalid Select Operator:::" + request.getParameter("selectOperator_" + i)));
                    }
                    else if(functions.isValueNull(request.getParameter("selectOperator_" + i)))
                    {
                        ruleOperation.setOperator(request.getParameter("selectOperator_" + i));

                        if(PZOperatorEnums.BETWEEN.name().equals(request.getParameter("selectOperator_" + i)))
                        {
                            if (!ESAPI.validator().isValidInput("riskRuleValue2_" + i, request.getParameter("riskRuleValue2_" + i), "SafeString", 100, false))
                            {
                                validationErrorList.addError("riskRuleValue2_" + i, new ValidationException("Invalid Value2", "Invalid Value2:::" + request.getParameter("riskRuleValue2_" + i)));
                            }
                            else
                            {
                                ruleOperation.setValue2(request.getParameter("riskRuleValue2_" + i));
                            }
                        }
                    }

                    if (!ESAPI.validator().isValidInput("riskRuleValue1_" + i, request.getParameter("riskRuleValue1_" + i), "SafeString", 100, false))
                    {
                        validationErrorList.addError("riskRuleValue1_" + i, new ValidationException("Invalid Value1", "Invalid Value1:::" + request.getParameter("riskRuleValue1_" + i)));
                    }
                    else
                    {
                        ruleOperation.setValue1(request.getParameter("riskRuleValue1_" + i));
                    }



                    if(!ESAPI.validator().isValidInput("isMandatory_" + i, request.getParameter("isMandatory_" + i), "isYN", 5, false))
                    {
                        ruleOperation.setMandatory(false);
                    }
                    else if(functions.isValueNull(request.getParameter("isMandatory_" + i)) && "Y".equalsIgnoreCase(request.getParameter("isMandatory_" + i)))
                    {
                        ruleOperation.setMandatory(true);
                    }

                    if (i < querySelectSelected.length)
                    {
                        if (!ESAPI.validator().isValidInput("selectComparator_" + i, request.getParameter("selectComparator_" + i), "SafeString", 100, false))
                        {
                            validationErrorList.addError("selectComparator_" + i, new ValidationException("Invalid Select Comparator", "Invalid Select Comparator:::" + request.getParameter("selectComparator_" + i)));
                        }
                        else
                        {
                            ruleOperation.setComparator(request.getParameter("selectComparator_" + i));
                        }
                    }

                    ruleOperationList.add(ruleOperation);
                }
            }
            else
            {
                validationErrorList.addError("querySelect", new ValidationException("Invalid Query Rule", "Invalid Query Rule:::" + request.getParameter("querySelect")));
            }
        }
        return ruleOperationList;
    }

}
