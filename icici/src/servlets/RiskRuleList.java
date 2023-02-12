import com.directi.pg.*;
import com.manager.MerchantMonitoringManager;
import com.manager.TerminalManager;
import com.manager.vo.TerminalVO;
import com.manager.vo.merchantmonitoring.MonitoringParameterMappingVO;
import com.payment.MultipleMemberUtill;
import com.payment.exceptionHandler.PZDBViolationException;
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
import java.util.Map;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 11/9/12
 * Time: 11:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class RiskRuleList extends HttpServlet
{
    private static Logger logger = new Logger(RiskRuleList.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        MultipleMemberUtill multipleMemberUtill = new MultipleMemberUtill();
        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        String errormsg = "";
        String requestedFile=req.getParameter("requestedfilename");
        RequestDispatcher rd=null;
        if("monitoringRuleLog".equals(requestedFile))
        {
            rd= req.getRequestDispatcher("/monitoringRuleLog.jsp?ctoken=" + user.getCSRFToken());
        }
        else
        {
            rd= req.getRequestDispatcher("/riskrulemapping.jsp?ctoken=" + user.getCSRFToken());
        }

        errormsg = errormsg + validateParameters(req);
        Functions functions = new Functions();

        if (functions.isValueNull(errormsg))
        {
            req.setAttribute("error", errormsg);
            rd.forward(req, res);
            return;
        }
        String memberId = "";
        String terminalId = "";
        if (functions.isValueNull(req.getParameter("memberid")) && functions.isValueNull(req.getParameter("terminalid")))
        {
            if (multipleMemberUtill.isMemberTerminalidMapped(req.getParameter("memberid"), req.getParameter("terminalid")))
            {
                memberId = req.getParameter("memberid");
                terminalId = req.getParameter("terminalid");
            }
        }
        if (functions.isValueNull(req.getParameter("memberid")) && functions.isValueNull(req.getParameter("terminalid")))
        {
            memberId = req.getParameter("memberid");
        }
        MerchantMonitoringManager merchantMonitoringManager = new MerchantMonitoringManager();
        TerminalManager terminalManager = new TerminalManager();

        List<MonitoringParameterMappingVO> monitoringParameterMappingVO = null;
        Map<String, List<MonitoringParameterMappingVO>> stringListMap = new LinkedHashMap();
        try
        {
            if (functions.isValueNull(terminalId))
            {
                monitoringParameterMappingVO = merchantMonitoringManager.getRiskRuleOnTerminalFromMapping(terminalId, memberId);
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
                    monitoringParameterMappingVO = merchantMonitoringManager.getRiskRuleOnTerminalFromMapping(terminalVO.getTerminalId(), memberId);
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
