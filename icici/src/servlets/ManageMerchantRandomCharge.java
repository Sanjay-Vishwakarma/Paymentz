import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.ChargeManager;
import com.manager.vo.payoutVOs.MerchantRandomChargesVO;
import com.payment.exceptionHandler.PZDBViolationException;
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
 * Created by Admin on 23/7/15.
 */
public class ManageMerchantRandomCharge extends HttpServlet
{

    private static Logger logger=new Logger(ManageMerchantRandomCharge.class.getName());
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
        HttpSession session = request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if(!Admin.isLoggedIn(session))
        {
            response.sendRedirect("/icici/sessionout.jsp");
            return;
        }
        String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;
        String memberId=request.getParameter("memberid");
        String bankWireId=request.getParameter("bankwireid");
        String terminalId=request.getParameter("terminalid");
        String chargeValueType=request.getParameter("chargevaluetype");
        String chargeType=request.getParameter("chargetype");
        String chargeName=request.getParameter("chargename");
        String chargeRate=request.getParameter("chargerate");
        String chargeCounter=request.getParameter("chargecounter");
        String chargeAmount=request.getParameter("chargeamount");
        String chargeValue=request.getParameter("chargevalue");
        String chargeRemark=request.getParameter("chargeremark");

        StringBuffer sb=new StringBuffer();
        RequestDispatcher rd=request.getRequestDispatcher("/manageMerchantRandomCharge.jsp?ctoken="+user.getCSRFToken());
        if(!ESAPI.validator().isValidInput("memberid", memberId, "Numbers", 20, false))
        {
            sb.append("Invalid Member Id,");
        }
        if(!ESAPI.validator().isValidInput("bankwireid", bankWireId, "Numbers", 50, false))
        {
            sb.append("Invalid BankWire Id,");
        }
        if(!ESAPI.validator().isValidInput("terminalid", terminalId, "Numbers", 5, false))
        {
            sb.append("Invalid Terminal Id,");
        }
        if(!ESAPI.validator().isValidInput("chargename", chargeName, "Description",255,false))
        {
            sb.append("Invalid Charge Name,");
        }
        if(!ESAPI.validator().isValidInput("chargerate", chargeRate, "AmountStr", 20, false))
        {
            sb.append("Invalid Charge Rate,");
        }
        if(!ESAPI.validator().isValidInput("chargecounter", chargeCounter, "Numbers", 20, true))
        {
            sb.append("Invalid Charge Count,");
        }
        if(!ESAPI.validator().isValidInput("chargeamount", chargeAmount, "AmountStr", 20, false))
        {
            sb.append("Invalid Charge Amount,");
        }
        if(!ESAPI.validator().isValidInput("chargevalue",chargeValue, "AmountStr", 20,false))
        {
            sb.append("Invalid Charge Value,");
        }
        if(!ESAPI.validator().isValidInput("chargeremark",chargeRemark,"Description",255,false))
        {
            sb.append("Invalid Charge Remark,");
        }

        if(sb.length()>0)
        {
            logger.error("Validation Failed===="+sb.toString());
            request.setAttribute("statusMsg",sb.toString());
            rd.forward(request,response);
            return;
        }

        MerchantRandomChargesVO merchantRandomChargesVO=new MerchantRandomChargesVO();
        ChargeManager chargeManager=new ChargeManager();
        Functions functions=new Functions();
        int chargeCounterInt=0;
        if(functions.isNumericVal(chargeCounter))
        {
            chargeCounterInt=Integer.parseInt(chargeCounter);
        }

        merchantRandomChargesVO.setBankWireId(bankWireId);
        merchantRandomChargesVO.setMemberId(memberId);
        merchantRandomChargesVO.setTerminalId(terminalId);
        merchantRandomChargesVO.setChargeValueType(chargeValueType);
        merchantRandomChargesVO.setChargeName(chargeName);
        merchantRandomChargesVO.setChargeRate(Double.valueOf(chargeRate));
        merchantRandomChargesVO.setChargeCounter(chargeCounterInt);
        merchantRandomChargesVO.setChargeAmount(Double.valueOf(chargeAmount));
        merchantRandomChargesVO.setChargeValue(Double.valueOf(chargeValue));
        merchantRandomChargesVO.setChargeRemark(chargeRemark);
        merchantRandomChargesVO.setChargeType(chargeType);
        merchantRandomChargesVO.setActionExecutorId(actionExecutorId);
        merchantRandomChargesVO.setActionExecutorName(actionExecutorName);
        try
        {
            String status=chargeManager.addMerchantRandomCharge(merchantRandomChargesVO);
            if("success".equals(status))
            {sb.append("New merchant random charge added successfully.");}
            else
            {sb.append("New merchant random charge adding failed.");}

        }
        catch(PZDBViolationException e)
        {
            sb.append(e.getMessage());
            logger.error("Exception while adding merchant random charge::::"+e);
        }
        catch(Exception e)
        {
            sb.append(e.getMessage());
            logger.error("Exception while adding merchant random charge::::"+e);
        }
        request.setAttribute("statusMsg",sb.toString());
        rd.forward(request,response);
        return;
    }
}
