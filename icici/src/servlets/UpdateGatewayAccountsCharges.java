import com.directi.pg.*;
import com.manager.ChargeManager;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.payoutVOs.ChargeVersionVO;
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
/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 2/11/15
 * Time: 7:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateGatewayAccountsCharges  extends HttpServlet
{
    private static Logger log = new Logger(UpdateGatewayAccountsCharges.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("Entering in UpdateGatewayAccountsCharges");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        log.debug("ctoken==="+req.getParameter("ctoken"));

        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        Connection conn = null;

        StringBuilder sberror=new StringBuilder();
        String EOL = "<BR>";
        String gatewayChargeValue=null;

        if (!ESAPI.validator().isValidInput("chargevalue",req.getParameter("chargevalue"),"AmountStr",25,false))
        {
            sberror.append("Invalid Charge Value "+EOL);
        }
        //System.out.println("Charge Value::::::::::::::"+gatewayChargeValue);
        if (!ESAPI.validator().isValidInput("effectivestartdate", req.getParameter("effectivestartdate"), "fromDate", 25, false))
        {
            sberror.append("Invalid Start Date"+EOL);
        }
        if (!ESAPI.validator().isValidInput("effectiveenddate", req.getParameter("effectiveenddate"), "fromDate", 25, false))
        {
            sberror.append("Invalid End Date"+EOL);
        }
        if(sberror.length()>0)
        {
            req.setAttribute("errormessage",sberror.toString());
            RequestDispatcher rd = req.getRequestDispatcher("/actionGatewayAccountsCharges.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }


        gatewayChargeValue=req.getParameter("chargevalue");
        String effectiveStartDate=req.getParameter("effectivestartdate");
        String effectiveEndDate=req.getParameter("effectiveenddate");

        String mappingId=req.getParameter("mappingid");
        String lastUpdatedStartDate=req.getParameter("pstartdate");
        String lastUpdatedEndDate=req.getParameter("penddate");


        CommonFunctionUtil commonFunctionUtil=new CommonFunctionUtil();
        lastUpdatedStartDate=commonFunctionUtil.convertTimestampToDatepicker(lastUpdatedStartDate);
        lastUpdatedEndDate=commonFunctionUtil.convertTimestampToDatepicker(lastUpdatedEndDate);

        String message=commonFunctionUtil.newValidateDate(effectiveStartDate, effectiveEndDate, lastUpdatedStartDate,lastUpdatedEndDate);
        if(message!=null)
        {
            sberror.append(message);
            req.setAttribute("errormessage",sberror.toString());
            RequestDispatcher rd = req.getRequestDispatcher("/actionGatewayAccountsCharges.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        try
        {
            effectiveStartDate=commonFunctionUtil.convertDatepickerToTimestamp(effectiveStartDate,"00:00:00");
            effectiveEndDate=commonFunctionUtil.convertDatepickerToTimestamp(effectiveEndDate,"23:59:59");

            ChargeVersionVO chargeVersionVO=new ChargeVersionVO();

            chargeVersionVO.setAccounts_charges_mapping_id(Integer.parseInt(mappingId));
            chargeVersionVO.setChargeValue(Double.valueOf(gatewayChargeValue));
            chargeVersionVO.setEffectiveStartDate(effectiveStartDate);
            chargeVersionVO.setEffectiveEndDate(effectiveEndDate);


            ChargeManager chargeManager=new ChargeManager();
            String status=chargeManager.updateChargeVersionOnGatewayAccount(chargeVersionVO);
            if("success".equalsIgnoreCase(status))
            {
                sberror.append("Charge Updated Successfully");
            }
            else
            {
                sberror.append("Charge Updatedation Failed");
            }
        }
        finally
        {
            Database.closeConnection(conn);
        }
        req.setAttribute("errormessage",sberror.toString());
        RequestDispatcher rd = req.getRequestDispatcher("/actionGatewayAccountsCharges.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }


}
