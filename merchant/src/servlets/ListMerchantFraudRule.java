import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.fraud.vo.FraudRequestVO;
import com.manager.FraudRuleManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.PaginationVO;
import com.manager.vo.fraudruleconfVOs.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.apache.commons.lang3.StringUtils;
import org.owasp.esapi.User;
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
import java.util.ResourceBundle;

//import com.directi.pg.SystemAccessLogger;

/**
 * Created by Sneha on 10/8/15.
 */
public class ListMerchantFraudRule extends HttpServlet
{
    private static Logger logger = new Logger(ListMerchantFraudRule.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
    {
        doService(request, response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        doService(request, response);
    }
    public void doService(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {

        logger.debug("Entering into ListMerchantFraudRule");
        Merchants merchants = new Merchants();
        HttpSession session = Functions.getNewSession(request);
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        Functions functions=new Functions();
        ResourceBundle rb1 = null;
        String language_property1 = (String)session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String ListMerchantFraudRule_no_errormsg = StringUtils.isNotEmpty(rb1.getString("ListMerchantFraudRule_no_errormsg"))?rb1.getString("ListMerchantFraudRule_no_errormsg"): "No Rules Mapped";
        String manageMerchantFraudRule_Fraud_Rule_Status = StringUtils.isNotEmpty(rb1.getString("manageMerchantFraudRule_Fraud_Rule_Status"))?rb1.getString("manageMerchantFraudRule_Fraud_Rule_Status"): "Fraud Rule Status";
        String ListMerchantFraudRule_data_errormsg = StringUtils.isNotEmpty(rb1.getString("ListMerchantFraudRule_data_errormsg"))?rb1.getString("ListMerchantFraudRule_data_errormsg"): "No data Found";
        String ListMerchantFraudRule_fraudconfig_errormsg = StringUtils.isNotEmpty(rb1.getString("ListMerchantFraudRule_fraudconfig_errormsg"))?rb1.getString("ListMerchantFraudRule_fraudconfig_errormsg"): "Fraud rule configuration invisible to this account";

        if (!merchants.isLoggedIn(session))
        {
            response.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        String error= "";
        String EOL = "<BR>";
        int pageno=1;

        try
        {
            //validateOptionalParameter(request);
            InputValidator inputValidator = new InputValidator();
            List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
            inputFieldsListMandatory.add(InputFields.PAGENO);
            inputFieldsListMandatory.add(InputFields.RECORDS);

            inputValidator.InputValidations(request,inputFieldsListMandatory,true);
        }
        catch (ValidationException e)
        {
            logger.error("Enter valid input",e);
            error += "<center><font class=\"text\" face=\"arial\"><b>"+ error + e.getMessage() + EOL + "</b></font></center>";
            request.setAttribute("statusMsg",error);
            RequestDispatcher rd = request.getRequestDispatcher("/manageMerchantFraudRule.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
        }
        RequestDispatcher rd =request.getRequestDispatcher("/manageMerchantFraudRule.jsp?ctoken=" + user.getCSRFToken());

        String merchantid = (String) session.getAttribute("merchantid");
        StringBuffer statusMsg = new StringBuffer();

        MerchantDAO merchantDAO = new MerchantDAO();
        PaginationVO paginationVO = new PaginationVO();
        FraudRuleManager ruleManager = new FraudRuleManager();

        pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);

        if(0==pageno || 00==pageno)
        {
            request.setAttribute("statusMsg", "Invalid Page No.");
            rd = request.getRequestDispatcher("/manageMerchantFraudRule.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
        }
        try
        {
            paginationVO.setInputs("merchantId=" +merchantid);
            paginationVO.setPageNo(pageno);
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(request.getParameter("currentblock"), 1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(request.getParameter("SRecords"), 15));
            List<RuleMasterVO> internalLevelRuleMapping = new ArrayList<>();
            List<RuleMasterVO> subAccountLevelRuleMapping = new ArrayList<>();

            MerchantFraudAccountVO fraudAccountVO = merchantDAO.getMerchantFraudServiceConfigurationDetails1(merchantid);
            if (fraudAccountVO != null)
            {
                if(fraudAccountVO.getIsVisible().equalsIgnoreCase("Y") && fraudAccountVO.getIsActive().equalsIgnoreCase("Y"))
                {
                    FraudSystemSubAccountVO subAccountVO = ruleManager.getFraudServiceSubAccountDetails(fraudAccountVO.getFsSubAccountId());
                    if (subAccountVO != null)
                    {
                        internalLevelRuleMapping = ruleManager.getInternalSubLevelRiskRuleList(merchantid);
                        if (internalLevelRuleMapping.size() <= 0)
                        {
                            internalLevelRuleMapping = ruleManager.getInternalAccountLevelRiskRuleList(merchantid);
                        }

                        List<RuleMasterVO> accountLevelRuleMapping = ruleManager.getAccountLevelRiskRuleList(subAccountVO.getFraudSystemAccountId());
                        if (!functions.isValueNull(subAccountVO.getSubmerchantUsername()) && !functions.isValueNull(subAccountVO.getSubmerchantPassword()))
                        {
                            subAccountLevelRuleMapping = ruleManager.getSuBAccountLevelRiskRuleList(subAccountVO.getFraudSystemSubAccountId(), paginationVO);
                        }
                        else
                        {
                            statusMsg.append(ListMerchantFraudRule_no_errormsg);
                        }


                        request.setAttribute("merchantid", merchantid);
                        request.setAttribute("paginationVO", paginationVO);
                        request.setAttribute("subAccountVO", subAccountVO);
                        request.setAttribute("fraudAccountVO", fraudAccountVO);
                        request.setAttribute("internalLevelRuleMapping",internalLevelRuleMapping);
                        request.setAttribute("accountLevelRuleMapping", accountLevelRuleMapping);
                        request.setAttribute("subAccountLevelRuleMapping", subAccountLevelRuleMapping);
                }
                else
                {
                    statusMsg.append(ListMerchantFraudRule_data_errormsg);
                }
            }
            else
            {
                statusMsg.append(ListMerchantFraudRule_fraudconfig_errormsg);
            }
        }
        else
        {
            internalLevelRuleMapping = ruleManager.getInternalAccountLevelRiskRuleList(merchantid);

                request.setAttribute("merchantid", merchantid);
                request.setAttribute("paginationVO", paginationVO);
                request.setAttribute("internalLevelRuleMapping", internalLevelRuleMapping);

                if (internalLevelRuleMapping.size()<=0){
                    statusMsg.append(ListMerchantFraudRule_data_errormsg);
                }
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error("Error while performing db operation in ::::"+ e);
            statusMsg.append(e.getMessage());
        }
        catch (Exception e)
        {
            logger.error("Error while performing db operation in ::::"+ e);
            statusMsg.append(e.getMessage());
        }
        request.setAttribute("statusMsg", statusMsg.toString());
        rd.forward(request, response);
        return;
    }
   /* private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }*/

}
