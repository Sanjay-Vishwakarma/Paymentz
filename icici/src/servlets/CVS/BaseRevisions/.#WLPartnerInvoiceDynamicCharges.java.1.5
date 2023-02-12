import com.directi.pg.Logger;
import com.directi.pg.core.GatewayType;
import com.manager.GatewayManager;
import com.manager.PartnerManager;
import com.manager.dao.CommissionManager;
import com.manager.vo.PartnerDetailsVO;
import com.manager.vo.WLPartnerCommissionVO;
import org.owasp.esapi.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Supriya
 * Date: 5/9/2016
 * Time: 3:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class WLPartnerInvoiceDynamicCharges extends HttpServlet
{
    private static Logger logger = new Logger(WLPartnerInvoiceDynamicCharges.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        String partnerId = req.getParameter("partnerid");

        try
        {
            CommissionManager commissionManager = new CommissionManager();
            GatewayManager gatewayManager = new GatewayManager();
            PartnerManager partnerManager = new PartnerManager();

            PartnerDetailsVO partnerDetailsVO = partnerManager.getPartnerDetails(partnerId);
            String reportingCurrency = partnerDetailsVO.getReportingCurrency();
            String profitSharingCommissionModule = partnerDetailsVO.getProfitShareCommissionModule();

            List<WLPartnerCommissionVO> wlPartnerCommissionVOList = commissionManager.getWLPartnerDynamicCommissions(partnerId);
            logger.debug("profitSharingCommissionModule:::::" + profitSharingCommissionModule);
            if ("Y".equals(profitSharingCommissionModule))
            {
                logger.debug("inside profitSharingCommissionModule if ");
                List<GatewayType> gatewayTypeList = gatewayManager.getPartnerProcessingBanksList(partnerId);
                Set<String> processingCurrSet = null;
                if (gatewayTypeList.size() > 0)
                {
                    processingCurrSet = new HashSet();
                    for (GatewayType gatewayType : gatewayTypeList)
                    {
                        if (!reportingCurrency.equalsIgnoreCase(gatewayType.getCurrency()))
                        {
                            processingCurrSet.add(gatewayType.getCurrency());
                        }
                    }
                }
                req.setAttribute("processingCurrSet", processingCurrSet);
            }
            logger.debug("processingCurrSet:" + req.getAttribute("processingCurrSet"));
            req.setAttribute("wlPartnerCommissionVOList", wlPartnerCommissionVOList);
            req.setAttribute("reportingCurrency",reportingCurrency);
        }
        catch (Exception e)
        {
            logger.debug("Exception::::::::" + e);
            req.setAttribute("statusMsg","Internal error while processing your request.");
        }
        req.getRequestDispatcher("/whitelabelinvoice.jsp?ctoken=" + user.getCSRFToken()).forward(req, res);
    }
}
