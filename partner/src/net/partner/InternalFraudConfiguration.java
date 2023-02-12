package net.partner;
import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.FraudRuleManager;
import com.manager.dao.ChargesDAO;
import com.manager.vo.PaginationVO;
import com.manager.vo.fraudruleconfVOs.RuleMasterVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class InternalFraudConfiguration extends HttpServlet
{
    Logger logger = new Logger(PartnerMerchantFraudRuleList.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        PartnerFunctions partner = new PartnerFunctions();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        PaginationVO paginationVO = new PaginationVO();
        FraudRuleManager ruleManager = new FraudRuleManager();
        List<RuleMasterVO> internalLevelRuleMapping = new ArrayList<>();
        Functions functions=new Functions();

        StringBuffer statusMsg = new StringBuffer();
        StringBuffer errormsg = new StringBuffer();

        String partnerid = String.valueOf(session.getAttribute("partnerid"));


        String EOL = "<BR>";
        RequestDispatcher rd;

        String action=request.getParameter("action");
        String score = null;
        String status = null;
        String value = null;
        String ruleName = null;

        if ("update".equals(action))
        {
            try
            {
                List<RuleMasterVO> rulelist = new ArrayList<>();
                String[] ruleIds = request.getParameterValues("ruleid");
                RuleMasterVO ruleMasterVO = null;

                for (String ruleId : ruleIds)
                {
                    score = request.getParameter("score_" + ruleId);
                    value = request.getParameter("value_" + ruleId);
                    status = request.getParameter("status_" + ruleId);
                    ruleName = request.getParameter("name_" + ruleId);
                    int count=0;

                    if (!ESAPI.validator().isValidInput("score",score,"Numbers",2,false))
                    {
                        errormsg.append(ruleId + "-" + "Invalid Rule Score" + " ");
                        count++;
                    }
                    if(functions.isValueNull(value))
                    {

                        if(!"Block_Card_By_Usage".equalsIgnoreCase(ruleName) && !"Block_Card_By_Type".equalsIgnoreCase(ruleName))
                        {
                            if (!ESAPI.validator().isValidInput("value",value,"Numbers",10,false))
                            {
                                errormsg.append(ruleId + "-" + "Invalid Rule Limit");
                                count++;
                            }
                        }else if("Block_Card_By_Usage".equalsIgnoreCase(ruleName))
                        {
                            if (!ESAPI.validator().isValidInput("value", value, "CardByUsages", 30, false))
                            {
                                errormsg.append(ruleId + "-" + "Invalid Rule Limit");
                                count++;
                            }
                        }else if("Block_Card_By_Type".equalsIgnoreCase(ruleName))
                        {
                            if (!ESAPI.validator().isValidInput("value", value, "CardByType", 30, false))
                            {
                                errormsg.append(ruleId + "-" + "Invalid Rule Limit");
                                count++;
                            }
                        }
                    }

                    if (count > 0)
                    {
                        errormsg.append(EOL);
                    }

                    if (functions.isNumericVal(score))
                    {
                        ruleMasterVO = new RuleMasterVO();
                        ruleMasterVO.setRuleId(ruleId);
                        ruleMasterVO.setDefaultScore(score);
                        ruleMasterVO.setDefaultValue(value);
                        ruleMasterVO.setDefaultStatus(status);
                        rulelist.add(ruleMasterVO);
                    }
                    else
                    {
                        statusMsg.append(ruleIds);
                    }
                }
                if (errormsg.length() > 0)
                {
                    request.setAttribute("errormsg", errormsg);;
                }
                else
                {
                    if(rulelist.size() > 0)
                    {
                        String result = ruleManager.getUpdateInternalAccountLevelRiskRuleList(rulelist, partnerid);

                        if ("success".equals(result))
                        {
                            statusMsg.append("Rules updated successfully");
                        }
                        else
                        {
                            statusMsg.append("Rules updating failed");
                        }
                    }
                    else
                    {
                        statusMsg.append("No valid rules to update");
                    }
                }
            }
            catch (Exception e){
                logger.error(e);
            }
        }

        Set cardSet = new TreeSet();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs=null;
        try
        {
            con= Database.getBinConnection();
            StringBuilder query =new StringBuilder("SELECT DISTINCT TYPE FROM binbase");
            pstmt=con.prepareStatement(query.toString());
             rs=pstmt.executeQuery();
            while(rs.next())
            {
                if(functions.isValueNull(rs.getString("type")))
                    cardSet.add(rs.getString("type"));
            }
        }
        catch (SQLException e)
        {

            logger.error("SQL Exception while getting internal level risk rules", e);
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting internal level risk rules::", systemError);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }

        try
        {
            internalLevelRuleMapping = ruleManager.getPartnerInternalAccountLevelRiskRuleList(partnerid);
            request.setAttribute("partnerid", partnerid);
            request.setAttribute("internalLevelRuleMapping", internalLevelRuleMapping);
            request.setAttribute("cardSet", cardSet);


        }
        catch (PZDBViolationException e)
        {
            logger.error("Error while performing db operation in ::::"+ e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        catch (Exception e)
        {
            logger.error("Error while performing db operation in ::::"+ e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }


        request.setAttribute("statusMsg", statusMsg);
        request.setAttribute("partnerid",partnerid);
        request.setAttribute("errormsg", errormsg);

        rd = request.getRequestDispatcher("/internalFraudConfiguration.jsp?ctoken="+user.getCSRFToken());
        rd.forward(request, response);
        return;
    }
}
