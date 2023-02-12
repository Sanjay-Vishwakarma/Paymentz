//package servlets;

import com.directi.pg.Admin;
import com.directi.pg.Logger;
import com.logicboxes.error.ValidationException;
import com.logicboxes.validation.Validation;
import com.manager.ChargeManager;
import com.manager.vo.payoutVOs.MerchantRandomChargesVO;
import com.payment.exceptionHandler.PZDBViolationException;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by kiran on 24/7/15.
 */
public class ActionMerchantRandomCharges extends HttpServlet
{

    private static Logger logger = new Logger(ActionMerchantRandomCharges.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        String action=req.getParameter("action");
        StringBuffer sb=new StringBuffer();
        String statusMsg = "";
        RequestDispatcher rd=null;
        try
        {


            if(action.equalsIgnoreCase("modify"))
            {
                String merchantrdmchargeid=req.getParameter("merchantrdmchargeid");

                ChargeManager chargeManager=new ChargeManager();
                MerchantRandomChargesVO merchantRandomChargesVO=chargeManager.getMerchantRandomChargeDetails(merchantrdmchargeid);
                req.setAttribute("merchantRandomChargesVO",merchantRandomChargesVO);
                rd=req.getRequestDispatcher("/actionMerchantRandomCharges.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req,res);
                return;

            }
            else if(action.equalsIgnoreCase("update"))
            {
                String merchantrdmchargeid=req.getParameter("merchantrdmchargeid");
                logger.debug("merchantrdmchargeid=>"+merchantrdmchargeid);
                String chargeValueType=req.getParameter("chargevaluetype");
                String chargeName=req.getParameter("chargename");
                String chargeRate=req.getParameter("chargerate");
                String chargeCounter=req.getParameter("chargecounter");
                String chargeAmount=req.getParameter("chargeamount");
                String chargeValue=req.getParameter("chargevalue");
                String chargeRemark=req.getParameter("chargeremark");
                String memberId=req.getParameter("memberid");
                String terminalId=req.getParameter("terminalid");

                if(!ESAPI.validator().isValidInput("chargename", chargeName, "SafeString",255, false))
                {
                    sb.append("Invalid Charge Name,");
                }
                if(!ESAPI.validator().isValidInput("chargerate", chargeRate, "AmountStr", 20, false))
                {
                    sb.append("Invalid Charge Rate,");
                }
                if(!ESAPI.validator().isValidInput("chargecounter", chargeCounter, "Numbers", 20, false))
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
                if(!ESAPI.validator().isValidInput("chargeremark",chargeRemark,"SafeString",255,false))
                {
                    sb.append("Invalid Charge Remark,");
                }
                if(sb.length() > 0)
                {
                    logger.debug("INSIDE IF OF VALIDATE");
                    req.setAttribute("statusMsg",sb.toString());
                    rd = req.getRequestDispatcher("/actionMerchantRandomCharges.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }

                MerchantRandomChargesVO merchantRandomChargesVO = new MerchantRandomChargesVO();
                merchantRandomChargesVO.setMerchantRdmChargeId(merchantrdmchargeid);
                merchantRandomChargesVO.setMemberId(memberId);
                merchantRandomChargesVO.setTerminalId(terminalId);
                merchantRandomChargesVO.setChargeValueType(chargeValueType);
                merchantRandomChargesVO.setChargeName(chargeName);
                merchantRandomChargesVO.setChargeRate(Double.valueOf(chargeRate));
                merchantRandomChargesVO.setChargeCounter(Integer.parseInt(chargeCounter));
                merchantRandomChargesVO.setChargeAmount(Double.valueOf(chargeAmount));
                merchantRandomChargesVO.setChargeValue(Double.valueOf(chargeValue));
                merchantRandomChargesVO.setChargeRemark(chargeRemark);

                ChargeManager chargeManager = new ChargeManager();
                String msg = chargeManager.updateMerchantRandomChargeDetails(merchantRandomChargesVO);

                if ("success".equals(msg))
                {
                    statusMsg = "Merchant Random Charges Updated Successfully";
                }
                else
                {
                    statusMsg = "Merchant Random Charges Updation Failed";
                }
                req.setAttribute("statusMsg", statusMsg);
                rd = req.getRequestDispatcher("/actionMerchantRandomCharges.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
        }
        catch (PZDBViolationException e)
        {

        }
        catch (IOException e)
        {
           logger.error("Catch IOException....",e);
        }
    }
}
