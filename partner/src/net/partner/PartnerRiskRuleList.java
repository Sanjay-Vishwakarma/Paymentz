package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.MerchantMonitoringManager;
import com.manager.TerminalManager;
import com.manager.vo.TerminalVO;
import com.manager.vo.merchantmonitoring.MonitoringParameterMappingVO;
import com.payment.exceptionHandler.PZDBViolationException;
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
 * Created with IntelliJ IDEA.
 * User: Supriya
 * Date: 11/9/12
 * Time: 11:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class PartnerRiskRuleList extends HttpServlet
{
    private static Logger logger = new Logger(PartnerRiskRuleList.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }
        RequestDispatcher rd=req.getRequestDispatcher("/partnerRiskRuleMapping.jsp?ctoken=" + user.getCSRFToken());
        String errormsg = "";
        Functions functions=new Functions();
        errormsg = errormsg + validateParameters(req);
        String errorMsg = "";
        String EOL = "<BR>";
        if (!ESAPI.validator().isValidInput("pid", req.getParameter("pid"), "Numbers", 10, true))
        {
            errormsg = errormsg + "Invalid Partner ID." + EOL ;
        }

        if(functions.isValueNull(errormsg)){
            req.setAttribute("error",errormsg);
            rd.forward(req, res);
            return;
        }
        String memberId = req.getParameter("memberid");
        String terminalId = req.getParameter("terminalid");
        String partnerid = (String) session.getAttribute("merchantid");
        String pid = req.getParameter("pid");
        String partner_id="";
        try
        {
            if(functions.isValueNull(req.getParameter("pid")) && partner.isPartnerSuperpartnerMapped(pid, partnerid))
            {
                if (!partner.isPartnerMemberMapped(memberId, pid))
                {
                    req.setAttribute("error","Invalid partner member mapping.");
                    rd.forward(req, res);
                    return;
                }
            }
            else if(!functions.isValueNull(req.getParameter("pid"))){

                if ( !partner.isPartnerSuperpartnerMembersMapped(memberId, partnerid))
                {
                    req.setAttribute("error","Invalid partner member mapping.");
                    rd.forward(req, res);
                    return;
                }
            }
            else{
                req.setAttribute("error","Invalid partner mapping.");
                rd.forward(req, res);
                return;
            }
        }catch(Exception e)
        {
            logger.error("Exception---" + e);
        }



        MerchantMonitoringManager merchantMonitoringManager = new MerchantMonitoringManager();
        TerminalManager terminalManager = new TerminalManager();

        List<MonitoringParameterMappingVO> monitoringParameterMappingVO = null;
        Map<String, List<MonitoringParameterMappingVO>> stringListMap = new LinkedHashMap();
        try
        {

            if (functions.isValueNull(terminalId))
            {
                Date date72 = new Date();
                logger.debug("before calling getRiskRuleOnTerminalFromMappingForPartner::::::" + date72.getTime());

                monitoringParameterMappingVO = merchantMonitoringManager.getRiskRuleOnTerminalFromMappingForPartner(terminalId, memberId);
                logger.debug("After calling getRiskRuleOnTerminalFromMappingForPartner::::" + new Date().getTime());
                logger.debug("After calling this getRiskRuleOnTerminalFromMappingForPartner::::" + (new Date().getTime() - date72.getTime()));

                if (monitoringParameterMappingVO != null && monitoringParameterMappingVO.size() > 0)
                {
                    stringListMap.put(terminalId+":custom", monitoringParameterMappingVO);
                }
                else
                {
                    monitoringParameterMappingVO = merchantMonitoringManager.getRiskRuleOnTerminalFromMaster();
                    if (monitoringParameterMappingVO != null && monitoringParameterMappingVO.size() > 0)
                    {
                        stringListMap.put(terminalId+":default", monitoringParameterMappingVO);
                    }
                }
            }
            else
            {
                List<TerminalVO> terminalVOList = terminalManager.getTerminalsByMerchantId(memberId);
                for (TerminalVO terminalVO :terminalVOList)
                {
                    monitoringParameterMappingVO = merchantMonitoringManager.getRiskRuleOnTerminalFromMappingForPartner(terminalVO.getTerminalId(), memberId);
                    if (monitoringParameterMappingVO != null && monitoringParameterMappingVO.size() > 0)
                    {
                        stringListMap.put(terminalVO.getTerminalId()+":custom", monitoringParameterMappingVO);
                    }
                    else
                    {
                        monitoringParameterMappingVO = merchantMonitoringManager.getRiskRuleOnTerminalFromMaster();
                        if (monitoringParameterMappingVO != null && monitoringParameterMappingVO.size() > 0)
                        {
                            stringListMap.put(terminalVO.getTerminalId()+":default", monitoringParameterMappingVO);
                        }
                    }
                }
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZDBViolationException:::::"+e);
            req.setAttribute("updatemsg", "Internal error occurs while processing your request.");
            rd.forward(req, res);
            return;
        }
        catch (Exception ex)
        {
            logger.debug("Exception::::" + ex);
        }
        req.setAttribute("stringListMap", stringListMap);
        rd.forward(req, res);
    }

    private String validateParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.MEMBERID);
        inputFieldsListOptional.add(InputFields.TERMINALID);
        //inputFieldsListOptional.add(InputFields.PARTNER_ID);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req, inputFieldsListOptional, errorList, false);
        if (!errorList.isEmpty())
        {
            for (InputFields inputFields : inputFieldsListOptional)
            {
                if (errorList.getError(inputFields.toString()) != null)
                {
                    logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error + errorList.getError(inputFields.toString()).getMessage();
                }
                error = error + "<BR>";
            }
        }
        return error;
    }

}
