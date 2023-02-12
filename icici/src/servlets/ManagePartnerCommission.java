import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.ChargeManager;
import com.manager.TerminalManager;
import com.manager.dao.PartnerDAO;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.PartnerCommissionVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
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

        if (!Admin.isLoggedIn(session)){
            logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        String EOL = "<BR>";
        StringBuilder sberror=new StringBuilder();
        String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;
        RequestDispatcher rd=request.getRequestDispatcher("/managePartnerCommission.jsp?ctoken="+user.getCSRFToken());

        if (!ESAPI.validator().isValidInput("partnerid", request.getParameter("partnerid"),"Numbers",10,false))
        {
            sberror.append("Invalid Partner ID"+EOL);
        }
        if (!ESAPI.validator().isValidInput("memberid", request.getParameter("memberid"),"SafeString",10,false))
        {
            sberror.append("Invalid Member ID"+EOL);
        }
        if (!ESAPI.validator().isValidInput("terminalid", request.getParameter("terminalid"),"SafeString", 10, false))
        {
            sberror.append("Invalid Terminal ID"+EOL);
        }
        if (!ESAPI.validator().isValidInput("commissionon", request.getParameter("commissionon"),"Numbers", 10, false))
        {
            sberror.append("Invalid Commission Name"+EOL);
        }
        if (!ESAPI.validator().isValidInput("commissionvalue", request.getParameter("commissionvalue"),"AmountStr", 10, false))
        {
            sberror.append("Invalid Commission Value"+EOL);
        }
        if (!ESAPI.validator().isValidInput("sequencenum", request.getParameter("sequencenum"), "Numbers", 10, false))
        {
            sberror.append("Invalid Sequence Number"+EOL);
        }
        if (sberror.length() > 0)
        {
            request.setAttribute("statusMsg",sberror.toString());
            rd.forward(request,response);
            return;

        }

        String partnerId=request.getParameter("partnerid");
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
            partnerCommissionVO.setActionExecutorId(actionExecutorId);
            partnerCommissionVO.setActionExecutorName(actionExecutorName);


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
                }
                else
                {
                    status="Invalid member-terminal configuration";
                }
            }
            else
            {
                status="Invalid member-partner configuration";
            }

            request.setAttribute("statusMsg",status);
            rd.forward(request,response);
            return;

        }
        catch (Exception e)
        {
            request.setAttribute("statusMsg",e.getMessage());
            rd.forward(request,response);
            return;
        }
    }
}


