package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemAccessLogger;
import com.manager.FraudRuleManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.fraudruleconfVOs.FraudRuleChangeIntimationVO;
import com.manager.vo.fraudruleconfVOs.FraudSystemAccountVO;
import com.manager.vo.fraudruleconfVOs.FraudSystemSubAccountRuleVO;
import com.manager.vo.fraudruleconfVOs.SubAccountRuleChangeRequestVO;
import com.payment.exceptionHandler.PZDBViolationException;
import org.owasp.esapi.User;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 12/11/14
 * Time: 3:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class ManagePartnerMerchantFraudRule extends HttpServlet
{
    private static Logger logger = new Logger(ManagePartnerMerchantFraudRule.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
    {

        HttpSession session = request.getSession();
        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session)){
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        RequestDispatcher rd = request.getRequestDispatcher("/partnerMerchantFraudRule.jsp?ctoken="+user.getCSRFToken());

        String score=null;
        String status=null;
        String partnerId=null;
        String fsId=null;

        String toId=request.getParameter("memberid");
        String fsAccountId=request.getParameter("fsaccountid");
        String fsSubAccountId=request.getParameter("fssubaccountid");
        String[] ruleIds=request.getParameterValues("ruleid");

        StringBuffer updateMsg = new StringBuffer();
        StringBuffer sb = new StringBuffer();

        Functions functions=new Functions();
        FraudRuleManager  fraudRuleManager=new FraudRuleManager();
        MerchantDAO merchantDAO=new MerchantDAO();
        try
        {
            MerchantDetailsVO merchantDetailsVO=merchantDAO.getMemberDetails(toId);
            if(merchantDetailsVO.getPartnerId()==null)
            {
                request.setAttribute("updateMsg","PartnerId Not Found");
                rd.forward(request,response);
                return;
            }

            partnerId=merchantDetailsVO.getPartnerId();
            FraudSystemAccountVO fraudSystemAccountVO=fraudRuleManager.getFraudAccountDetails(fsAccountId);
            if(fraudSystemAccountVO.getFraudSystemAccountId()==null)
            {
                request.setAttribute("updateMsg","FraudSystem Account Not Found.");
                rd.forward(request,response);
                return;
            }

            fsId=fraudSystemAccountVO.getFraudSystemId();
            if(!functions.isValueNull(fsSubAccountId))
            {
                request.setAttribute("updateMsg","FraudSystem SubAccount Not Found.");
                rd.forward(request,response);
                return;
            }

            //Step1:add intimation entry in fraud intimation
            FraudRuleChangeIntimationVO intimationVO=new FraudRuleChangeIntimationVO();

            intimationVO.setFraudSystemId(fsId);
            intimationVO.setFsAccountId(fsAccountId);
            intimationVO.setFsSubAccountId(fsSubAccountId);
            intimationVO.setPartnerId(partnerId);
            intimationVO.setMemberId(toId);
            intimationVO.setStatus("Initiated");

            List<FraudSystemSubAccountRuleVO> subAccountRuleVOList=new ArrayList<FraudSystemSubAccountRuleVO>();
            FraudSystemSubAccountRuleVO subAccountRuleVO=null;
            for (String ruleId : ruleIds)
            {
                score = request.getParameter("score_" + ruleId);
                status = request.getParameter("status_" + ruleId);
                if(functions.isNumericVal(score))
                {
                    subAccountRuleVO = new FraudSystemSubAccountRuleVO();
                    subAccountRuleVO.setRuleId(ruleId);
                    subAccountRuleVO.setScore(score);
                    subAccountRuleVO.setStatus(status);
                    subAccountRuleVOList.add(subAccountRuleVO);
                }
                else
                {
                    sb.append(ruleId);
                }
            }
            if(sb.length()>0)
            {
                request.setAttribute("updateMsg","Please enter valid input.");
                rd.forward(request,response);
                return;
            }
            else
            {
                if(subAccountRuleVOList.size()>0)
                {
                    SubAccountRuleChangeRequestVO changeRequestVO=new SubAccountRuleChangeRequestVO(intimationVO,
                            subAccountRuleVOList,fsSubAccountId,fsAccountId,fsId,toId,partnerId);
                    String updatedMsg=fraudRuleManager.handleSubAccountFraudRuleChangeRequest(changeRequestVO);
                    if("success".equals(updatedMsg))
                    {
                        updateMsg.append("Rules updated successfully");
                    }
                    else
                    {
                        updateMsg.append("Rules updating failed");
                    }
                }
                else
                {
                    updateMsg.append("No valid rules to update");
                }
            }

        }
        catch (PZDBViolationException e){
            logger.error("Error while performing db operation in ::::"+ e);
            updateMsg.append(e.getMessage());
        }
        catch (Exception e){
            logger.error("Error while performing db operation in ::::"+ e);
            updateMsg.append(e.getMessage());
        }
        request.setAttribute("updateMsg",updateMsg.toString());
        rd.forward(request,response);
    }
}
