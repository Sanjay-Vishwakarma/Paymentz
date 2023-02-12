import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemAccessLogger;
import com.manager.dao.PartnerDAO;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.payoutVOs.PartnerWireVO;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
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
 * User: sandip
 * Date: 2/10/15
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdatePartnerWire  extends HttpServlet
{
    Logger logger=new Logger(UpdatePartnerWire.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException
    {

        logger.debug("Entering Into UpdatePartnerWire");
        HttpSession session = request.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");


        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        String errormsg="";
        String EOL = "<BR>";
        String mailSent="";
        StringBuffer sberror=new StringBuffer();

        if(!ESAPI.validator().isValidInput("settledate", request.getParameter("settledate"), "fromDate", 20, false))
        {
            sberror.append("Invalid Settlement Date"+EOL);
        }
        if (!ESAPI.validator().isValidInput("partnerchargeamount",request.getParameter("partnerchargeamount"), "AmountMinus", 12, false) || request.getParameter("partnerchargeamount").length()>12)
        {
            sberror.append("Invalid Partner Charge Amount");
        }
        if (!ESAPI.validator().isValidInput("partnerunpaidamount",request.getParameter("partnerunpaidamount"), "AmountMinus", 12, false) || request.getParameter("partnerunpaidamount").length()>12)
        {
            sberror.append("Invalid Partner Unpaid Amount");
        }
        if (!ESAPI.validator().isValidInput("partnertotalfundedamount",request.getParameter("partnertotalfundedamount"), "AmountMinus", 12, false) || request.getParameter("partnertotalfundedamount").length()>12)
        {
            sberror.append("Invalid Partner Total Funded Amount");
        }
        if(sberror.length()>0)
        {
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + sberror.toString() + EOL + "</b></font></center>";
            request.setAttribute("message",errormsg);
            RequestDispatcher rd = request.getRequestDispatcher("/partnerWireList.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request,response);
        }

        String wireId=request.getParameter("id");
        String settlementDate=request.getParameter("settledate");
        double partnerChargeAmount=Double.valueOf(request.getParameter("partnerchargeamount"));
        double partnerUnpaidAmount=Double.valueOf(request.getParameter("partnerunpaidamount"));
        double partnerTotalFundedAmount=Double.valueOf(request.getParameter("partnertotalfundedamount"));
        String status=request.getParameter("status");

        CommonFunctionUtil commonFunctionUtil= new CommonFunctionUtil();
        String settleDateTimestamp=commonFunctionUtil.convertDatepickerToTimestamp(settlementDate,"00:00:00");

        PartnerWireVO partnerWireVO=new PartnerWireVO();
        partnerWireVO.setSettledId(wireId);
        partnerWireVO.setSettleDate(settleDateTimestamp);
        partnerWireVO.setPartnerChargeAmount(partnerChargeAmount);
        partnerWireVO.setPartnerUnpaidAmount(partnerUnpaidAmount);
        partnerWireVO.setPartnerTotalFundedAmount(partnerTotalFundedAmount);
        partnerWireVO.setStatus(status);

        PartnerDAO partnerDAO=new PartnerDAO();
        partnerDAO.updatePartnerWire(partnerWireVO);
        if(partnerWireVO.isUpdated())
        {
            //Call Payout Mail Email With Attachment
            errormsg += "<center><font class=\"textb\" face=\"arial\"><b>Wire Updated Successfully</b></font></center>";
        }
        else
        {
            errormsg += "<center><font class=\"textb\" face=\"arial\"><b>Wire Updation Failed</b></font></center>";
        }
        request.setAttribute("message",errormsg);
        RequestDispatcher rd = request.getRequestDispatcher("/partnerWireList.jsp?ctoken="+user.getCSRFToken());
        rd.forward(request, response);
    }

}
