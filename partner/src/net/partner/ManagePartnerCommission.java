package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.manager.ChargeManager;
import com.manager.TerminalManager;
import com.manager.dao.PartnerDAO;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.PartnerCommissionVO;
import org.apache.commons.lang.StringUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 2/11/15
 * Time: 4:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class ManagePartnerCommission extends HttpServlet
{
    Logger  logger=new Logger(ManagePartnerCommission.class.getName());

    public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        doProcess(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        doProcess(request,response);
    }
    public void doProcess(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        logger.debug("Entering ManagePartnerCommission");
        HttpSession session = request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        ResourceBundle rb1 = null;
        String language_property1 = (String) session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String ManagePartnerCommission_PartnerID_errormsg = StringUtils.isNotEmpty(rb1.getString("ManagePartnerCommission_PartnerID_errormsg")) ? rb1.getString("ManagePartnerCommission_PartnerID_errormsg") : "Invalid Partner ID";
        String ManagePartnerCommission_MemberID_errormsg = StringUtils.isNotEmpty(rb1.getString("ManagePartnerCommission_MemberID_errormsg")) ? rb1.getString("ManagePartnerCommission_MemberID_errormsg") : "Invalid Member ID";
        String ManagePartnerCommission_TerminalID_errormsg = StringUtils.isNotEmpty(rb1.getString("ManagePartnerCommission_TerminalID_errormsg")) ? rb1.getString("ManagePartnerCommission_TerminalID_errormsg") : "Invalid Terminal ID";
        String ManagePartnerCommission_Commission_Name_errormsg = StringUtils.isNotEmpty(rb1.getString("ManagePartnerCommission_Commission_Name_errormsg")) ? rb1.getString("ManagePartnerCommission_Commission_Name_errormsg") : "Invalid Commission Name";
        String ManagePartnerCommission_Commision_Value_errormsg = StringUtils.isNotEmpty(rb1.getString("ManagePartnerCommission_Commision_Value_errormsg")) ? rb1.getString("ManagePartnerCommission_Commision_Value_errormsg") : "Invalid Commission Value";
        String ManagePartnerCommission_Sequence_errormsg = StringUtils.isNotEmpty(rb1.getString("ManagePartnerCommission_Sequence_errormsg")) ? rb1.getString("ManagePartnerCommission_Sequence_errormsg") : "Invalid Sequence Number";
        String ManagePartnerCommission_memberterminal_errormsg = StringUtils.isNotEmpty(rb1.getString("ManagePartnerCommission_memberterminal_errormsg")) ? rb1.getString("ManagePartnerCommission_memberterminal_errormsg") : "Invalid member-terminal configuration";
        String ManagePartnerCommission_Partner_configuration_errormsg = StringUtils.isNotEmpty(rb1.getString("ManagePartnerCommission_Partner_configuration_errormsg")) ? rb1.getString("ManagePartnerCommission_Partner_configuration_errormsg") : "Invalid member-partner configuration";

        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }
        String EOL = "<BR>";
        StringBuilder sberror=new StringBuilder();
        RequestDispatcher rd=request.getRequestDispatcher("/managePartnerCommission.jsp?ctoken="+user.getCSRFToken());

        if (!ESAPI.validator().isValidInput("partnerid", request.getParameter("partnerId"),"Numbers",10,false))
        {
            sberror.append(ManagePartnerCommission_PartnerID_errormsg+EOL);
        }
        if (!ESAPI.validator().isValidInput("memberid", request.getParameter("memberid"),"SafeString",10,false))
        {
            sberror.append(ManagePartnerCommission_MemberID_errormsg+EOL);
        }
        if (!ESAPI.validator().isValidInput("terminalid", request.getParameter("terminalid"),"SafeString", 10, false))
        {
            sberror.append(ManagePartnerCommission_TerminalID_errormsg+EOL);
        }
        if (!ESAPI.validator().isValidInput("commissionon", request.getParameter("commissionon"),"Numbers", 10, false))
        {
            sberror.append(ManagePartnerCommission_Commission_Name_errormsg+EOL);
        }
        if (!ESAPI.validator().isValidInput("commissionvalue", request.getParameter("commissionvalue"),"AmountStr", 10, false))
        {
            sberror.append(ManagePartnerCommission_Commision_Value_errormsg+EOL);
        }
        if (!ESAPI.validator().isValidInput("sequencenum", request.getParameter("sequencenum"), "Numbers", 10, false))
        {
            sberror.append(ManagePartnerCommission_Sequence_errormsg+EOL);
        }
        if (sberror.length() > 0)
        {
            request.setAttribute("statusMsg",sberror.toString());
            rd.forward(request,response);
            return;

        }

        String partnerId=request.getParameter("partnerId");
        String memberId=request.getParameter("memberid");
        String terminalId=request.getParameter("terminalid");
        String commissionOn=request.getParameter("commissionon");
        String commissionValue=request.getParameter("commissionvalue");
        String sequenceNo=request.getParameter("sequencenum");

        CommonFunctionUtil commonFunctionUtil=new CommonFunctionUtil();
        Functions functions=new Functions();
        String msg=commonFunctionUtil.newValidateDate(request.getParameter("startDate"),request.getParameter("endDate"),null,null);
        if(functions.isValueNull(msg))
        {
            request.setAttribute("statusMsg", sberror.toString());
            rd.forward(request, response);
            return;
        }
        String effectiveStartDate=commonFunctionUtil.convertDatepickerToTimestamp(request.getParameter("startDate"), "00:00:00");
        String effectiveEndDate=commonFunctionUtil.convertDatepickerToTimestamp(request.getParameter("endDate"),"23:59:59");
        try{

            PartnerCommissionVO partnerCommissionVO=new PartnerCommissionVO();

            partnerCommissionVO.setPartnerId(partnerId);
            partnerCommissionVO.setMemberId(memberId);
            partnerCommissionVO.setTerminalId(terminalId);
            partnerCommissionVO.setChargeId(commissionOn);
            partnerCommissionVO.setCommissionValue(Double.parseDouble(commissionValue));
            partnerCommissionVO.setStartDate(effectiveStartDate);
            partnerCommissionVO.setEndDate(effectiveEndDate);
            partnerCommissionVO.setSequenceNo(sequenceNo);

            ChargeManager chargeManager=new ChargeManager();
            PartnerDAO partnerDAO=new PartnerDAO();
            TerminalManager terminalManager=new TerminalManager();
            String status="";
            boolean b=partnerDAO.isMemberMappedWithPartner(partnerId,memberId);
            if(b)
            {
                boolean b1=terminalManager.isMemberMappedWithTerminal(memberId, terminalId);
                if(b1)
                {
                    status = chargeManager.addNewPartnerCommission(partnerCommissionVO);
                    request.setAttribute("statusMsg",status);
                    rd.forward(request,response);
                    return;
                }
                else
                {
                    status=ManagePartnerCommission_memberterminal_errormsg;
                    request.setAttribute("statusMsg",status);
                    rd.forward(request,response);
                    return;
                }
            }
            else
            {
                status=ManagePartnerCommission_Partner_configuration_errormsg;
                request.setAttribute("statusMsg",status);
                rd.forward(request,response);
                return;
            }
        }
        catch (Exception e)
        {
            request.setAttribute("statusMsg",e.getMessage());
            rd.forward(request,response);
            return;
        }
    }
}