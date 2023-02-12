

import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.ProfileManagementManager;
import com.manager.enums.AggregateFunctions;
import com.manager.enums.DataType;
import com.manager.enums.OperationTypeEnum;
import com.manager.enums.RuleTypeEnum;
import com.manager.vo.ActionVO;
import com.manager.vo.PayIfeTableInfo;
import com.manager.vo.businessRuleVOs.RuleOperation;
import com.manager.vo.businessRuleVOs.RuleVO;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by PZ on 27/8/15.
 */
public class AddBusinessRuleDetails extends HttpServlet
{
    private static Logger logger = new Logger(AddBusinessRuleDetails.class.getName());
    private Functions functions = new Functions();

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        HttpSession session = functions.getNewSession(request);

        Admin admin = new Admin();
        if (!admin.isLoggedIn(session))
        {
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        ProfileManagementManager profileManagementManager = new ProfileManagementManager();
        ActionVO actionVO = new ActionVO();
        RuleVO businessRuleVo = new RuleVO();
        //List<RuleOperation> ruleOperationsList=new ArrayList<RuleOperation>();

        ValidationErrorList validationErrorList = null;

        boolean saved = false;

        RequestDispatcher rdSuccess = null;
        RequestDispatcher rdError = request.getRequestDispatcher("/addOrUpdateBusinessRule.jsp?MES=ERR&ctoken=" + user.getCSRFToken());

        try
        {
            logger.debug("action :::" + request.getParameter("action"));
            validationErrorList = validateParameters(request,businessRuleVo);
            if (!validationErrorList.isEmpty())
            {
                request.setAttribute("ruleVO", businessRuleVo);
                request.setAttribute("actionVO", session.getAttribute("actionVO"));
                request.setAttribute("error", validationErrorList);
                rdError.forward(request, response);
                return;
            }

            actionVO.setAllContentAuto(request.getParameter("action"));
            System.out.println(request.getParameter("action"));

            businessRuleVo.setName(request.getParameter("businessRuleName"));
            businessRuleVo.setLabel(request.getParameter("businessRuleLabel"));
            businessRuleVo.setDescription(request.getParameter("businessRuleDescription"));


            businessRuleVo.setRuleType(request.getParameter("ruleType"));
            if ("DATABASE".equals(businessRuleVo.getRuleType()))
            {
                businessRuleVo.setQuery(request.getParameter("businessRuleQuery"));
            }

            if (actionVO.isAdd())
            {
                logger.debug("action ADD:::" + actionVO.isAdd());
                RuleVO ruleVO = profileManagementManager.getSingleBusinessDetails(null, businessRuleVo.getName());
                if (ruleVO == null)
                {
                    saved = profileManagementManager.insertBusinessDefinition(businessRuleVo);
                    if(saved)
                    {
                        saved=profileManagementManager.insertBusinessRuleOperation(businessRuleVo.getRuleOperation(),businessRuleVo.getId());
                    }
                }
                else
                {
                    request.setAttribute("actionVO", actionVO);
                    request.setAttribute("catchError", "Business Rule Is present");
                    rdError.forward(request, response);
                    return;
                }
            }
            else if (actionVO.isEdit())
            {
                saved = profileManagementManager.updateBusinessDefinition(businessRuleVo, actionVO.getActionCriteria().split("_")[0]);
                if(saved)
                {
                    profileManagementManager.deleteBusinessRuleOperation(actionVO.getActionCriteria().split("_")[0]);
                    saved = profileManagementManager.insertBusinessRuleOperation(businessRuleVo.getRuleOperation(), actionVO.getActionCriteria().split("_")[0]);
                }
                businessRuleVo.setId(actionVO.getActionCriteria().split("_")[0]);
            }
            if (saved)
                rdSuccess = request.getRequestDispatcher("/servlet/BusinessRuleDetails?businessRuleId=" + businessRuleVo.getId() + "&MES=SUCCESS&ctoken=" + user.getCSRFToken());
            else
            {
                request.setAttribute("actionVO", actionVO);
                request.setAttribute("catchError", "Kindly check for the Business Rule After sometime");
                rdError.forward(request, response);
                return;
            }
            request.setAttribute("saved", saved);
            request.setAttribute("actionVO", actionVO);
            rdSuccess.forward(request, response);

        }
        catch (PZDBViolationException e)
        {
            logger.error("Db violation exception while updating Business Rule details", e);
            PZExceptionHandler.handleDBCVEException(e, null, PZOperations.BUSINESS_RULE);
            request.setAttribute("catchError", "Kindly check for the Business Rule After sometime");
            rdError.forward(request, response);
            return;
        }


    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request, response);
    }

    private ValidationErrorList validateParameters(HttpServletRequest request,RuleVO ruleVO) throws PZDBViolationException
    {
        List<RuleOperation> ruleOperationsList=new ArrayList<RuleOperation>();

        List<InputFields> inputMandatoryParameter = new ArrayList<InputFields>();
        inputMandatoryParameter.add(InputFields.SMALL_ACTION);
        inputMandatoryParameter.add(InputFields.BUSINESS_RULE_NAME);
        inputMandatoryParameter.add(InputFields.BUSINESS_RULE_LABEL);
        inputMandatoryParameter.add(InputFields.BUSINESS_RULE_DESCRIPTION);
        inputMandatoryParameter.add(InputFields.RULETYPE);


        InputValidator inputValidator = new InputValidator();
        ValidationErrorList validationErrorList = new ValidationErrorList();

        inputValidator.InputValidations(request, inputMandatoryParameter, validationErrorList, false);



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

        Map<String,Integer> numberOfOperationTypeEnum=new HashMap<String, Integer>();
        if (RuleTypeEnum.REGULAR_EXPRESSION.name().contains(ruleTypeEnum.name()))
        {
            String regexInputSelected[] = request.getParameterValues("regexInputName");
            String regexOutputSelected[] = request.getParameterValues("regexOutputName");

            if (regexInputSelected != null || regexOutputSelected!=null)
            {
                int iCheck = 1;

                if (regexInputSelected != null)
                {
                    for (iCheck = 1; iCheck <= regexInputSelected.length; iCheck++)
                    {
                        OperationTypeEnum operationTypeEnum= OperationTypeEnum.getEnum(request.getParameter("regexOperationType_" + iCheck));
                        if(operationTypeEnum!=null)
                        {
                            if(numberOfOperationTypeEnum.containsKey(operationTypeEnum.name()))
                            {
                                Integer numberOfTime=numberOfOperationTypeEnum.get(operationTypeEnum.name());
                                int temp=numberOfTime.intValue();
                                numberOfTime=temp+1;
                                numberOfOperationTypeEnum.put(operationTypeEnum.name(),numberOfTime);
                            }
                            else
                            {
                                Integer numberOfTime=1;
                                numberOfOperationTypeEnum.put(operationTypeEnum.name(),numberOfTime);
                            }
                        }
                    }
                }
                if (regexOutputSelected != null)
                {
                    for (int i = 1; i <= regexOutputSelected.length; i++)
                    {
                        OperationTypeEnum operationTypeEnum= OperationTypeEnum.getEnum(request.getParameter("regexOperationType_" + iCheck));
                        if(operationTypeEnum!=null)
                        {
                            if(numberOfOperationTypeEnum.containsKey(operationTypeEnum.name()))
                            {
                                Integer numberOfTime=numberOfOperationTypeEnum.get(operationTypeEnum.name());
                                int temp=numberOfTime.intValue();
                                numberOfTime=temp+1;
                                numberOfOperationTypeEnum.put(operationTypeEnum.name(),numberOfTime);
                            }
                            else
                            {
                                Integer numberOfTime=1;
                                numberOfOperationTypeEnum.put(operationTypeEnum.name(),numberOfTime);
                            }
                        }
                        iCheck++;
                    }
                }
                int i = 1;
                if (regexInputSelected != null)
                {
                    for (i = 1; i <= regexInputSelected.length; i++)
                    {
                        logger.debug("REGEX operation::" + i);
                        OperationTypeEnum operationTypeEnum=null;

                        RuleOperation ruleOperation = new RuleOperation();
                        if (!ESAPI.validator().isValidInput("regexOperationType_" + i, request.getParameter("regexOperationType_" + i), "Description", 255, false))
                        {
                            validationErrorList.addError("regexOperationType_" + i, new ValidationException("Invalid Operation Type", "Invalid Operation Type :::" + request.getParameter("regexOperationType_" + i)));
                        }
                        else
                        {
                            operationTypeEnum= OperationTypeEnum.getEnum(request.getParameter("regexOperationType_" + i));
                            ruleOperation.setOperationType(operationTypeEnum.name());
                        }
                        if (!ESAPI.validator().isValidInput("regex_" + i, request.getParameter("regex_" + i), "Description", 255, false))
                        {
                            validationErrorList.addError("regex_" + i, new ValidationException("Invalid Regex", "Invalid Regex :::" + request.getParameter("regex_" + i)));
                        }
                        else
                        {
                            ruleOperation.setRegex(request.getParameter("regex_" + i));
                        }
                        if (!ESAPI.validator().isValidInput("regexOperator_" + i, request.getParameter("regexOperator_" + i), "Description", 100, false))
                        {
                            validationErrorList.addError("regexOperator_" + i, new ValidationException("Invalid Regex Operator", "Invalid Regex Operator:::" + request.getParameter("flatFileOperator_" + i)));
                        }
                        else
                        {
                            ruleOperation.setOperator(request.getParameter("regexOperator_" + i));
                        }

                        if (!ESAPI.validator().isValidInput("regexInputName_" + i, request.getParameter("regexInputName_" + i), "Description", 100, false))
                        {
                            validationErrorList.addError("regexInputName_" + i, new ValidationException("Invalid Regex Input Name", "Invalid Regex Input Name:::" + request.getParameter("regexInputName_" + i)));
                        }
                        else
                        {
                            ruleOperation.setInputName(request.getParameter("regexInputName_" + i));
                        }

                        if (!ESAPI.validator().isValidInput("isMandatory_" + i, request.getParameter("isMandatory_" + i), "isYN", 5, false))
                        {
                            ruleOperation.setMandatory(false);
                        }
                        else if (functions.isValueNull(request.getParameter("isMandatory_" + i)) && "Y".equalsIgnoreCase(request.getParameter("isMandatory_" + i)))
                        {
                            ruleOperation.setMandatory(true);
                        }

                        if (((i < regexInputSelected.length || (regexOutputSelected!=null && regexOutputSelected.length>0)) && (numberOfOperationTypeEnum.containsKey(ruleOperation.getOperationType()) && numberOfOperationTypeEnum.get(ruleOperation.getOperationType()).intValue()>1)))
                        {
                            if (!ESAPI.validator().isValidInput("regexComparator_" + i, request.getParameter("regexComparator_" + i), "Description", 100, false))
                            {
                                validationErrorList.addError("regexComparator_" + i, new ValidationException("Invalid Regex Comparator", "Invalid Regex Comparator:::" + request.getParameter("regexComparator_" + i)));
                            }
                            else
                            {
                                ruleOperation.setComparator(request.getParameter("regexComparator_" + i));
                            }
                            //This is to subtract number of current operationType
                            Integer numberOfTime=numberOfOperationTypeEnum.get(operationTypeEnum.name());
                            int temp=numberOfTime.intValue();
                            numberOfTime=temp-1;
                            numberOfOperationTypeEnum.put(operationTypeEnum.name(),numberOfTime);
                        }

                        ruleOperationList.add(ruleOperation);
                    }
                }

                if(regexOutputSelected!=null)
                {
                    for (int k = 1; k <= regexOutputSelected.length; k++)
                    {
                        logger.debug("REGEX operation::" + i);
                        OperationTypeEnum operationTypeEnum= null;

                        RuleOperation ruleOperation = new RuleOperation();
                        if (!ESAPI.validator().isValidInput("regexOperationType_" + i, request.getParameter("regexOperationType_" + i), "Description", 255, false))
                        {
                            validationErrorList.addError("regexOperationType_" + i, new ValidationException("Invalid Operation Type", "Invalid Operation Type :::" + request.getParameter("regexOperationType_" + i)));
                        }
                        else
                        {
                            operationTypeEnum= OperationTypeEnum.getEnum(request.getParameter("regexOperationType_" + i));
                            ruleOperation.setOperationType(operationTypeEnum.name());
                        }
                        if (!ESAPI.validator().isValidInput("regex_" + i, request.getParameter("regex_" + i), "SafeString", 255, false))
                        {
                            validationErrorList.addError("regex_" + i, new ValidationException("Invalid Regex", "Invalid Regex :::" + request.getParameter("regex_" + i)));
                        }
                        else
                        {
                            ruleOperation.setRegex(request.getParameter("regex_" + i));
                        }
                        if (!ESAPI.validator().isValidInput("regexOperator_" + i, request.getParameter("regexOperator_" + i), "Description", 100, false))
                        {
                            validationErrorList.addError("regexOperator_" + i, new ValidationException("Invalid Regex Operator", "Invalid Regex Operator:::" + request.getParameter("flatFileOperator_" + i)));
                        }
                        else
                        {
                            ruleOperation.setOperator(request.getParameter("regexOperator_" + i));
                        }

                        if (!ESAPI.validator().isValidInput("regexInputName_" + i, request.getParameter("regexInputName_" + i), "Description", 100, false))
                        {
                            validationErrorList.addError("regexInputName_" + i, new ValidationException("Invalid Regex Input Name", "Invalid Regex Input Name:::" + request.getParameter("regexInputName_" + i)));
                        }
                        else
                        {
                            ruleOperation.setInputName(request.getParameter("regexInputName_" + i));
                        }

                        if (!ESAPI.validator().isValidInput("isMandatory_" + i, request.getParameter("isMandatory_" + i), "isYN", 5, false))
                        {
                            ruleOperation.setMandatory(false);
                        }
                        else if (functions.isValueNull(request.getParameter("isMandatory_" + i)) && "Y".equalsIgnoreCase(request.getParameter("isMandatory_" + i)))
                        {
                            ruleOperation.setMandatory(true);
                        }

                        if (k < regexOutputSelected.length && (numberOfOperationTypeEnum.containsKey(ruleOperation.getOperationType()) && numberOfOperationTypeEnum.get(ruleOperation.getOperationType()).intValue()>1))
                        {
                            if (!ESAPI.validator().isValidInput("regexComparator_" + i, request.getParameter("regexComparator_" + i), "Description", 100, false))
                            {
                                validationErrorList.addError("regexComparator_" + i, new ValidationException("Invalid Regex Comparator", "Invalid Regex Comparator:::" + request.getParameter("regexComparator_" + i)));
                            }
                            else
                            {
                                ruleOperation.setComparator(request.getParameter("regexComparator_" + i));
                            }

                            //This is to subtract number of current operationType
                            Integer numberOfTime=numberOfOperationTypeEnum.get(operationTypeEnum.name());
                            int temp=numberOfTime.intValue();
                            numberOfTime=temp-1;
                            numberOfOperationTypeEnum.put(operationTypeEnum.name(),numberOfTime);
                        }

                        ruleOperationList.add(ruleOperation);
                        i++;
                    }
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
            String flatFileOutputSelected[] = request.getParameterValues("flatFileOutputName");
            if (flatFileInputSelected != null || flatFileOutputSelected!=null)
            {
                int iCheck = 1;

                if (flatFileInputSelected != null)
                {
                    for (iCheck = 1; iCheck <= flatFileInputSelected.length; iCheck++)
                    {
                        OperationTypeEnum operationTypeEnum= OperationTypeEnum.getEnum(request.getParameter("flatFileOperationType_" + iCheck));
                        if(operationTypeEnum!=null)
                        {
                            if(numberOfOperationTypeEnum.containsKey(operationTypeEnum.name()))
                            {
                                Integer numberOfTime=numberOfOperationTypeEnum.get(operationTypeEnum.name());
                                int temp=numberOfTime.intValue();
                                numberOfTime=temp+1;
                                numberOfOperationTypeEnum.put(operationTypeEnum.name(),numberOfTime);
                            }
                            else
                            {
                                Integer numberOfTime=1;
                                numberOfOperationTypeEnum.put(operationTypeEnum.name(),numberOfTime);
                            }
                        }
                    }
                }
                if (flatFileOutputSelected != null)
                {
                    for (int k = 1; k <= flatFileOutputSelected.length; k++)
                    {
                        OperationTypeEnum operationTypeEnum= OperationTypeEnum.getEnum(request.getParameter("flatFileOperationType_" + iCheck));
                        if(operationTypeEnum!=null)
                        {
                            if(numberOfOperationTypeEnum.containsKey(operationTypeEnum.name()))
                            {
                                Integer numberOfTime=numberOfOperationTypeEnum.get(operationTypeEnum.name());
                                int temp=numberOfTime.intValue();
                                numberOfTime=temp+1;
                                numberOfOperationTypeEnum.put(operationTypeEnum.name(),numberOfTime);
                            }
                            else
                            {
                                Integer numberOfTime=1;
                                numberOfOperationTypeEnum.put(operationTypeEnum.name(),numberOfTime);
                            }
                        }
                        iCheck++;
                    }
                }

                int i=1;
                if(flatFileInputSelected!=null)
                {
                    for ( i= 1; i <= flatFileInputSelected.length; i++)
                    {
                        logger.debug("Flat File operation::" + i);
                        OperationTypeEnum operationTypeEnum = null;

                        RuleOperation ruleOperation = new RuleOperation();


                        if (!ESAPI.validator().isValidInput("flatFileOperationType_" + i, request.getParameter("flatFileOperationType_" + i), "SafeString", 255, false))
                        {
                            validationErrorList.addError("flatFileOperationType_" + i, new ValidationException("Invalid Operation Type", "Invalid Operation Type :::" + request.getParameter("flatFileOperationType_" + i)));
                        }
                        else
                        {
                            operationTypeEnum = OperationTypeEnum.getEnum(request.getParameter("flatFileOperationType_" + i));
                            ruleOperation.setOperationType(operationTypeEnum.name());
                        }

                        if (!ESAPI.validator().isValidInput("flatFileInputName_" + i, request.getParameter("flatFileInputName_" + i), "Description", 255, false))
                        {
                            validationErrorList.addError("flatFileInputName_" + i, new ValidationException("Invalid Flat File Input Name", "Invalid Flat File Input Name :::" + request.getParameter("flatFileInputName_" + i)));
                        }
                        else
                        {
                            ruleOperation.setInputName(request.getParameter("flatFileInputName_" + i));
                        }

                        if (!ESAPI.validator().isValidInput("flatFileOperator_" + i, request.getParameter("flatFileOperator_" + i), "Description", 100, false))
                        {
                            validationErrorList.addError("flatFileOperator_" + i, new ValidationException("Invalid Flat File Operator", "Invalid Flat File Operator:::" + request.getParameter("flatFileOperator_" + i)));
                        }
                        else
                        {
                            ruleOperation.setOperator(request.getParameter("flatFileOperator_" + i));
                        }

                        if (((i < flatFileInputSelected.length || (flatFileOutputSelected != null && flatFileOutputSelected.length > 0)) && (numberOfOperationTypeEnum.containsKey(ruleOperation.getOperationType()) && numberOfOperationTypeEnum.get(ruleOperation.getOperationType()).intValue() > 1)))
                        {
                            if (!ESAPI.validator().isValidInput("flatFileComparator_" + i, request.getParameter("flatFileComparator_" + i), "Description", 100, false))
                            {
                                validationErrorList.addError("flatFileComparator_" + i, new ValidationException("Invalid Flat File Comparator", "Invalid Flat File Comparator:::" + request.getParameter("flatFileComparator_" + i)));
                            }
                            else
                            {
                                ruleOperation.setComparator(request.getParameter("flatFileComparator_" + i));
                            }

                            //This is to subtract number of current operationType
                            Integer numberOfTime = numberOfOperationTypeEnum.get(operationTypeEnum.name());
                            int temp = numberOfTime.intValue();
                            numberOfTime = temp - 1;
                            numberOfOperationTypeEnum.put(operationTypeEnum.name(), numberOfTime);
                        }

                        ruleOperationList.add(ruleOperation);
                    }
                }

                if(flatFileOutputSelected!=null)
                {
                    for (int k = 1; k <= flatFileOutputSelected.length; k++)
                    {
                        logger.debug("Flat File operation::" + i);
                        OperationTypeEnum operationTypeEnum = null;

                        RuleOperation ruleOperation = new RuleOperation();


                        if (!ESAPI.validator().isValidInput("flatFileOperationType_" + i, request.getParameter("flatFileOperationType_" + i), "Description", 255, false))
                        {
                            validationErrorList.addError("flatFileOperationType_" + i, new ValidationException("Invalid Operation Type", "Invalid Operation Type :::" + request.getParameter("flatFileOperationType_" + i)));
                        }
                        else
                        {
                            operationTypeEnum = OperationTypeEnum.getEnum(request.getParameter("flatFileOperationType_" + i));
                            ruleOperation.setOperationType(operationTypeEnum.name());
                        }

                        if (!ESAPI.validator().isValidInput("flatFileInputName_" + i, request.getParameter("flatFileInputName_" + i), "Description", 255, false))
                        {
                            validationErrorList.addError("flatFileInputName_" + i, new ValidationException("Invalid Flat File Input Name", "Invalid Flat File Input Name :::" + request.getParameter("flatFileInputName_" + i)));
                        }
                        else
                        {
                            ruleOperation.setInputName(request.getParameter("flatFileInputName_" + i));
                        }

                        if (!ESAPI.validator().isValidInput("flatFileOperator_" + i, request.getParameter("flatFileOperator_" + i), "Description", 100, false))
                        {
                            validationErrorList.addError("flatFileOperator_" + i, new ValidationException("Invalid Flat File Operator", "Invalid Flat File Operator:::" + request.getParameter("flatFileOperator_" + i)));
                        }
                        else
                        {
                            ruleOperation.setOperator(request.getParameter("flatFileOperator_" + i));
                        }

                        if (((flatFileOutputSelected.length > k && (numberOfOperationTypeEnum.containsKey(ruleOperation.getOperationType())) && numberOfOperationTypeEnum.get(ruleOperation.getOperationType()).intValue() > 1)))
                        {
                            if (!ESAPI.validator().isValidInput("flatFileComparator_" + i, request.getParameter("flatFileComparator_" + i), "Description", 100, false))
                            {
                                validationErrorList.addError("flatFileComparator_" + i, new ValidationException("Invalid Flat File Comparator", "Invalid Flat File Comparator:::" + request.getParameter("flatFileComparator_" + i)));
                            }
                            else
                            {
                                ruleOperation.setComparator(request.getParameter("flatFileComparator_" + i));
                            }

                            //This is to subtract number of current operationType
                            Integer numberOfTime = numberOfOperationTypeEnum.get(operationTypeEnum.name());
                            int temp = numberOfTime.intValue();
                            numberOfTime = temp - 1;
                            numberOfOperationTypeEnum.put(operationTypeEnum.name(), numberOfTime);
                        }

                        ruleOperationList.add(ruleOperation);
                        i++;
                    }

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
            String comparatorOutputSelected[] = request.getParameterValues("comparatorOutputName");
            if (comparatorInputSelected != null || comparatorOutputSelected!=null)
            {
                int iCheck = 1;

                if (comparatorInputSelected != null)
                {
                    for (iCheck = 1; iCheck <= comparatorInputSelected.length; iCheck++)
                    {
                        OperationTypeEnum operationTypeEnum= OperationTypeEnum.getEnum(request.getParameter("comparatorOperationType_" + iCheck));
                        if(operationTypeEnum!=null)
                        {
                            if(numberOfOperationTypeEnum.containsKey(operationTypeEnum.name()))
                            {
                                Integer numberOfTime=numberOfOperationTypeEnum.get(operationTypeEnum.name());
                                int temp=numberOfTime.intValue();
                                numberOfTime=temp+1;
                                numberOfOperationTypeEnum.put(operationTypeEnum.name(),numberOfTime);
                            }
                            else
                            {
                                Integer numberOfTime=1;
                                numberOfOperationTypeEnum.put(operationTypeEnum.name(),numberOfTime);
                            }
                        }
                    }
                }
                if (comparatorOutputSelected != null)
                {
                    for (int i = 1; i <= comparatorOutputSelected.length; i++)
                    {
                        OperationTypeEnum operationTypeEnum= OperationTypeEnum.getEnum(request.getParameter("comparatorOperationType_" + iCheck));
                        if(operationTypeEnum!=null)
                        {
                            if(numberOfOperationTypeEnum.containsKey(operationTypeEnum.name()))
                            {
                                Integer numberOfTime=numberOfOperationTypeEnum.get(operationTypeEnum.name());
                                int temp=numberOfTime.intValue();
                                numberOfTime=temp+1;
                                numberOfOperationTypeEnum.put(operationTypeEnum.name(),numberOfTime);
                            }
                            else
                            {
                                Integer numberOfTime=1;
                                numberOfOperationTypeEnum.put(operationTypeEnum.name(),numberOfTime);
                            }
                        }
                        iCheck++;
                    }
                }
                logger.debug("comparatorNumber of value:::"+numberOfOperationTypeEnum.toString());
                int i=1;
                if(comparatorInputSelected!=null)
                {
                    for (i = 1; i <= comparatorInputSelected.length; i++)
                    {
                        logger.debug("Comparator operation::" + i);
                        OperationTypeEnum operationTypeEnum=null;

                        RuleOperation ruleOperation = new RuleOperation();

                        if (!ESAPI.validator().isValidInput("comparatorOperationType_" + i, request.getParameter("comparatorOperationType_" + i), "Description", 255, false))
                        {
                            validationErrorList.addError("comparatorOperationType_" + i, new ValidationException("Invalid Operation Type", "Invalid Operation Type :::" + request.getParameter("comparatorOperationType_" + i)));
                        }
                        else
                        {
                            operationTypeEnum = OperationTypeEnum.getEnum(request.getParameter("comparatorOperationType_" + i));
                            ruleOperation.setOperationType(operationTypeEnum.name());
                        }


                        if (!ESAPI.validator().isValidInput("comparatorInputName_" + i, request.getParameter("comparatorInputName_" + i), "Description", 255, false))
                        {
                            validationErrorList.addError("comparatorInputName_" + i, new ValidationException("Invalid Comparator Input Name", "Invalid Comparator Input Name :::" + request.getParameter("comparatorInputName_" + i)));
                        }
                        else
                        {
                            ruleOperation.setInputName(request.getParameter("comparatorInputName_" + i));
                        }

                        if (!ESAPI.validator().isValidInput("comparatorOperator_" + i, request.getParameter("comparatorOperator_" + i), "Description", 100, false))
                        {
                            validationErrorList.addError("comparatorOperator_" + i, new ValidationException("Invalid Comparator Operator", "Invalid Comparator Operator:::" + request.getParameter("comparatorOperator_" + i)));
                        }
                        else
                        {
                            ruleOperation.setOperator(request.getParameter("comparatorOperator_" + i));
                        }

                        if (!ESAPI.validator().isValidInput("comparatorDataType_" + i, request.getParameter("comparatorDataType_" + i), "Description", 100, false))
                        {
                            validationErrorList.addError("comparatorOperator_" + i, new ValidationException("Invalid Data Type", "Invalid Data Type:::" + request.getParameter("comparatorDataType_" + i)));
                        }
                        else
                        {
                            ruleOperation.setDataType(request.getParameter("comparatorDataType_" + i));
                            if (DataType.ENUM.name().equals(request.getParameter("comparatorDataType_" + i)))
                            {
                                if (!ESAPI.validator().isValidInput("comparatorEnumValue_" + i, request.getParameter("comparatorEnumValue_" + i), "Description", 1000, false))
                                {
                                    validationErrorList.addError("comparatorEnumValue_" + i, new ValidationException("Invalid Enum value", "Invalid Enum Value:::" + request.getParameter("comparatorEnumValue_" + i)));
                                }
                                else
                                {
                                    ruleOperation.setEnumValue(request.getParameter("comparatorEnumValue_" + i).toUpperCase());
                                }
                            }
                        }

                        if ((i < comparatorInputSelected.length||(comparatorOutputSelected!=null && comparatorOutputSelected.length>0 ))&&(numberOfOperationTypeEnum.containsKey(ruleOperation.getOperationType()) && numberOfOperationTypeEnum.get(ruleOperation.getOperationType()).intValue() > 1))
                        {
                            logger.debug("inside 1st Comparator"+numberOfOperationTypeEnum.get(ruleOperation.getOperationType()).intValue());
                            if (!ESAPI.validator().isValidInput("comparatorComparator_" + i, request.getParameter("comparatorComparator_" + i), "Description", 100, false))
                            {
                                validationErrorList.addError("comparatorComparator_" + i, new ValidationException("Invalid Comparator Comparator", "Invalid Comparator Comparator:::" + request.getParameter("comparatorComparator_" + i)));
                            }
                            else
                            {
                                ruleOperation.setComparator(request.getParameter("comparatorComparator_" + i));
                            }

                            //This is to subtract number of current operationType
                            Integer numberOfTime = numberOfOperationTypeEnum.get(operationTypeEnum.name());
                            int temp = numberOfTime.intValue();
                            numberOfTime = temp - 1;
                            numberOfOperationTypeEnum.put(operationTypeEnum.name(), numberOfTime);
                        }

                        ruleOperationList.add(ruleOperation);
                    }
                }

                if(comparatorOutputSelected!=null)
                {
                    for (int k = 1; k <= comparatorOutputSelected.length; k++)
                    {
                        logger.debug("Comparator operation::" + i);
                        OperationTypeEnum operationTypeEnum=null;

                        RuleOperation ruleOperation = new RuleOperation();

                        if (!ESAPI.validator().isValidInput("comparatorOperationType_" + i, request.getParameter("comparatorOperationType_" + i), "Description", 255, false))
                        {
                            validationErrorList.addError("comparatorOperationType_" + i, new ValidationException("Invalid Operation Type", "Invalid Operation Type :::" + request.getParameter("comparatorOperationType_" + i)));
                        }
                        else
                        {
                            operationTypeEnum = OperationTypeEnum.getEnum(request.getParameter("comparatorOperationType_" + i));
                            ruleOperation.setOperationType(operationTypeEnum.name());
                        }


                        if (!ESAPI.validator().isValidInput("comparatorInputName_" + i, request.getParameter("comparatorInputName_" + i), "Description", 255, false))
                        {
                            validationErrorList.addError("comparatorInputName_" + i, new ValidationException("Invalid Comparator Input Name", "Invalid Comparator Input Name :::" + request.getParameter("comparatorInputName_" + i)));
                        }
                        else
                        {
                            ruleOperation.setInputName(request.getParameter("comparatorInputName_" + i));
                        }

                        if (!ESAPI.validator().isValidInput("comparatorOperator_" + i, request.getParameter("comparatorOperator_" + i), "Description", 100, false))
                        {
                            validationErrorList.addError("comparatorOperator_" + i, new ValidationException("Invalid Comparator Operator", "Invalid Comparator Operator:::" + request.getParameter("comparatorOperator_" + i)));
                        }
                        else
                        {
                            ruleOperation.setOperator(request.getParameter("comparatorOperator_" + i));
                        }

                        if (!ESAPI.validator().isValidInput("comparatorDataType_" + i, request.getParameter("comparatorDataType_" + i), "Description", 100, false))
                        {
                            validationErrorList.addError("comparatorOperator_" + i, new ValidationException("Invalid Data Type", "Invalid Data Type:::" + request.getParameter("comparatorDataType_" + i)));
                        }
                        else
                        {
                            ruleOperation.setDataType(request.getParameter("comparatorDataType_" + i));
                            if (DataType.ENUM.name().equals(request.getParameter("comparatorDataType_" + i)))
                            {
                                if (!ESAPI.validator().isValidInput("comparatorEnumValue_" + i, request.getParameter("comparatorEnumValue_" + i), "Description", 1000, false))
                                {
                                    validationErrorList.addError("comparatorEnumValue_" + i, new ValidationException("Invalid Enum value", "Invalid Enum Value:::" + request.getParameter("comparatorEnumValue_" + i)));
                                }
                                else
                                {
                                    ruleOperation.setEnumValue(request.getParameter("comparatorEnumValue_" + i).toUpperCase());
                                }
                            }
                        }

                        if (comparatorOutputSelected.length>k &&(numberOfOperationTypeEnum.containsKey(ruleOperation.getOperationType()) && numberOfOperationTypeEnum.get(ruleOperation.getOperationType()).intValue() > 1))
                        {
                            if (!ESAPI.validator().isValidInput("comparatorComparator_" + i, request.getParameter("comparatorComparator_" + i), "Description", 100, false))
                            {
                                validationErrorList.addError("comparatorComparator_" + i, new ValidationException("Invalid Comparator Comparator", "Invalid Comparator Comparator:::" + request.getParameter("comparatorComparator_" + i)));
                            }
                            else
                            {
                                ruleOperation.setComparator(request.getParameter("comparatorComparator_" + i));
                            }

                            //This is to subtract number of current operationType
                            Integer numberOfTime = numberOfOperationTypeEnum.get(operationTypeEnum.name());
                            int temp = numberOfTime.intValue();
                            numberOfTime = temp - 1;
                            numberOfOperationTypeEnum.put(operationTypeEnum.name(), numberOfTime);
                        }

                        ruleOperationList.add(ruleOperation);
                        i++;
                    }
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
            String queryOutputSelected[] = request.getParameterValues("databaseOutputName");
            if (querySelectSelected != null || queryOutputSelected!=null)
            {
                int iCheck = 1;

                if (querySelectSelected != null)
                {
                    for (iCheck = 1; iCheck <= querySelectSelected.length; iCheck++)
                    {
                        OperationTypeEnum operationTypeEnum= OperationTypeEnum.getEnum(request.getParameter("databaseOperationType_" + iCheck));
                        if(operationTypeEnum!=null)
                        {
                            if(numberOfOperationTypeEnum.containsKey(operationTypeEnum.name()))
                            {
                                Integer numberOfTime=numberOfOperationTypeEnum.get(operationTypeEnum.name());
                                int temp=numberOfTime.intValue();
                                numberOfTime=temp+1;
                                numberOfOperationTypeEnum.put(operationTypeEnum.name(),numberOfTime);
                            }
                            else
                            {
                                Integer numberOfTime=1;
                                numberOfOperationTypeEnum.put(operationTypeEnum.name(),numberOfTime);
                            }
                        }
                    }
                }
                if (queryOutputSelected != null)
                {
                    for (int k = 1; k <= queryOutputSelected.length; k++)
                    {
                        OperationTypeEnum operationTypeEnum= OperationTypeEnum.getEnum(request.getParameter("databaseOperationType_" + iCheck));
                        if(operationTypeEnum!=null)
                        {
                            if(numberOfOperationTypeEnum.containsKey(operationTypeEnum.name()))
                            {
                                Integer numberOfTime=numberOfOperationTypeEnum.get(operationTypeEnum.name());
                                int temp=numberOfTime.intValue();
                                numberOfTime=temp+1;
                                numberOfOperationTypeEnum.put(operationTypeEnum.name(),numberOfTime);
                            }
                            else
                            {
                                Integer numberOfTime=1;
                                numberOfOperationTypeEnum.put(operationTypeEnum.name(),numberOfTime);
                            }
                        }
                        iCheck++;
                    }
                }
                int i=1;
                if(querySelectSelected!=null)
                {
                    for ( i= 1; i <= querySelectSelected.length; i++)
                    {
                        logger.debug("Database operation::" + i);
                        OperationTypeEnum operationTypeEnum=null;

                        RuleOperation ruleOperation = new RuleOperation();

                        if (!ESAPI.validator().isValidInput("databaseOperationType_" + i, request.getParameter("databaseOperationType_" + i), "Description", 255, false))
                        {
                            validationErrorList.addError("databaseOperationType_" + i, new ValidationException("Invalid Operation Type", "Invalid Operation Type :::" + request.getParameter("databaseOperationType_" + i)));
                        }
                        else
                        {
                            operationTypeEnum= OperationTypeEnum.getEnum(request.getParameter("databaseOperationType_" + i));
                            ruleOperation.setOperationType(operationTypeEnum.name());
                        }

                        if (!ESAPI.validator().isValidInput("select_" + i, request.getParameter("select_" + i), "Description", 255, false))
                        {
                            validationErrorList.addError("select_" + i, new ValidationException("Invalid Select Statement", "Invalid Select Statement :::" + request.getParameter("select_" + i)));
                        }
                        else
                        {
                            ruleOperation.setInputName(request.getParameter("select_" + i));
                            if (functions.isValueNull(request.getParameter("select_" + i)) && payIfeTableInfoMap != null && payIfeTableInfoMap.containsKey(request.getParameter("select_" + i)))
                            {
                                PayIfeTableInfo payIfeTableInfo = payIfeTableInfoMap.get(request.getParameter("select_" + i));
                                ruleOperation.setDataType(payIfeTableInfo.getDataType());
                                if (DataType.ENUM.name().equals(payIfeTableInfo.getDataType()) && functions.isValueNull(payIfeTableInfo.getEnumValue()))
                                {
                                    ruleOperation.setEnumValue(payIfeTableInfo.getEnumValue().toUpperCase());
                                }
                            }
                            else if (ruleOperation.getInputName().contains("(") || ruleOperation.getInputName().contains(")"))
                            {
                                if (AggregateFunctions.getEnum(ruleOperation.getInputName().substring(0, ruleOperation.getInputName().indexOf("("))) != null)
                                    ruleOperation.setDataType(DataType.INT.name());
                            }

                        }

                        if (!ESAPI.validator().isValidInput("selectOperator_" + i, request.getParameter("selectOperator_" + i), "Description", 100, false))
                        {
                            validationErrorList.addError("selectOperator_" + i, new ValidationException("Invalid Select Operator", "Invalid Select Operator:::" + request.getParameter("selectOperator_" + i)));
                        }
                        else
                        {
                            ruleOperation.setOperator(request.getParameter("selectOperator_" + i));
                        }

                        if (!ESAPI.validator().isValidInput("isMandatory_" + i, request.getParameter("isMandatory_" + i), "isYN", 5, false))
                        {
                            ruleOperation.setMandatory(false);
                        }
                        else if (functions.isValueNull(request.getParameter("isMandatory_" + i)) && "Y".equalsIgnoreCase(request.getParameter("isMandatory_" + i)))
                        {
                            ruleOperation.setMandatory(true);
                        }

                        if ((i < querySelectSelected.length || (queryOutputSelected!=null && queryOutputSelected.length>0) &&(numberOfOperationTypeEnum.containsKey(ruleOperation.getOperationType()) && numberOfOperationTypeEnum.get(ruleOperation.getOperationType()).intValue() > 1)))
                        {
                            if (!ESAPI.validator().isValidInput("selectComparator_" + i, request.getParameter("selectComparator_" + i), "Description", 100, false))
                            {
                                validationErrorList.addError("selectComparator_" + i, new ValidationException("Invalid Select Comparator", "Invalid Select Comparator:::" + request.getParameter("selectComparator_" + i)));
                            }
                            else
                            {
                                ruleOperation.setComparator(request.getParameter("selectComparator_" + i));
                            }

                            //This is to subtract number of current operationType
                            Integer numberOfTime = numberOfOperationTypeEnum.get(operationTypeEnum.name());
                            int temp = numberOfTime.intValue();
                            numberOfTime = temp - 1;
                            numberOfOperationTypeEnum.put(operationTypeEnum.name(), numberOfTime);
                        }

                        ruleOperationList.add(ruleOperation);
                    }
                }
                if(queryOutputSelected!=null)
                {
                    for ( int k= 1; k <= queryOutputSelected.length; k++)
                    {
                        logger.debug("Database operation::" + i);
                        OperationTypeEnum operationTypeEnum=null;

                        RuleOperation ruleOperation = new RuleOperation();

                        if (!ESAPI.validator().isValidInput("databaseOperationType_" + i, request.getParameter("databaseOperationType_" + i), "Description", 255, false))
                        {
                            validationErrorList.addError("databaseOperationType_" + i, new ValidationException("Invalid Operation Type", "Invalid Operation Type :::" + request.getParameter("databaseOperationType_" + i)));
                        }
                        else
                        {
                            operationTypeEnum= OperationTypeEnum.getEnum(request.getParameter("databaseOperationType_" + i));
                            ruleOperation.setOperationType(operationTypeEnum.name());
                        }

                        if (!ESAPI.validator().isValidInput("select_" + i, request.getParameter("select_" + i), "Description", 255, false))
                        {
                            validationErrorList.addError("select_" + i, new ValidationException("Invalid Select Statement", "Invalid Select Statement :::" + request.getParameter("select_" + i)));
                        }
                        else
                        {
                            ruleOperation.setInputName(request.getParameter("select_" + i));
                            if (functions.isValueNull(request.getParameter("select_" + i)) && payIfeTableInfoMap != null && payIfeTableInfoMap.containsKey(request.getParameter("select_" + i)))
                            {
                                PayIfeTableInfo payIfeTableInfo = payIfeTableInfoMap.get(request.getParameter("select_" + i));
                                ruleOperation.setDataType(payIfeTableInfo.getDataType());
                                if (DataType.ENUM.name().equals(payIfeTableInfo.getDataType()) && functions.isValueNull(payIfeTableInfo.getEnumValue()))
                                {
                                    ruleOperation.setEnumValue(payIfeTableInfo.getEnumValue().toUpperCase());
                                }
                            }
                            else if (ruleOperation.getInputName().contains("(") || ruleOperation.getInputName().contains(")"))
                            {
                                if (AggregateFunctions.getEnum(ruleOperation.getInputName().substring(0, ruleOperation.getInputName().indexOf("("))) != null)
                                    ruleOperation.setDataType(DataType.INT.name());
                            }

                        }

                        if (!ESAPI.validator().isValidInput("selectOperator_" + i, request.getParameter("selectOperator_" + i), "Description", 100, false))
                        {
                            validationErrorList.addError("selectOperator_" + i, new ValidationException("Invalid Select Operator", "Invalid Select Operator:::" + request.getParameter("selectOperator_" + i)));
                        }
                        else
                        {
                            ruleOperation.setOperator(request.getParameter("selectOperator_" + i));
                        }

                        if (!ESAPI.validator().isValidInput("isMandatory_" + i, request.getParameter("isMandatory_" + i), "isYN", 5, false))
                        {
                            ruleOperation.setMandatory(false);
                        }
                        else if (functions.isValueNull(request.getParameter("isMandatory_" + i)) && "Y".equalsIgnoreCase(request.getParameter("isMandatory_" + i)))
                        {
                            ruleOperation.setMandatory(true);
                        }

                        if (queryOutputSelected.length>k &&(numberOfOperationTypeEnum.containsKey(ruleOperation.getOperationType()) && numberOfOperationTypeEnum.get(ruleOperation.getOperationType()).intValue() > 1))
                        {
                            if (!ESAPI.validator().isValidInput("selectComparator_" + i, request.getParameter("selectComparator_" + i), "Description", 100, false))
                            {
                                validationErrorList.addError("selectComparator_" + i, new ValidationException("Invalid Select Comparator", "Invalid Select Comparator:::" + request.getParameter("selectComparator_" + i)));
                            }
                            else
                            {
                                ruleOperation.setComparator(request.getParameter("selectComparator_" + i));
                            }

                            //This is to subtract number of current operationType
                            Integer numberOfTime = numberOfOperationTypeEnum.get(operationTypeEnum.name());
                            int temp = numberOfTime.intValue();
                            numberOfTime = temp - 1;
                            numberOfOperationTypeEnum.put(operationTypeEnum.name(), numberOfTime);
                        }

                        ruleOperationList.add(ruleOperation);
                        i++;
                    }
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
