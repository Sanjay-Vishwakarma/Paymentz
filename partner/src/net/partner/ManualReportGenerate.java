package net.partner;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.fileupload.FileUploadBean;
import com.manager.PayoutManager;
import com.manager.dao.BankDao;
import com.manager.dao.PayoutDAO;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.BankWireManagerVO;
import com.manager.vo.payoutVOs.SettlementCycleVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Created by Mahima on 2/12/2018.
 */
public class ManualReportGenerate extends HttpServlet
{
    private static Logger logger = new Logger(ManualReportGenerate.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doProcess(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doProcess(request, response);
    }
    public void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        PartnerFunctions partner = new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            logger.debug("partner is logout ");
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }
        RequestDispatcher rd = null;
        StringBuffer errorMsg = new StringBuffer();
        List<SettlementCycleVO> listOfMerchantId = null;
        String EOL = "<BR>";

        PayoutDAO payoutDAO = new PayoutDAO();
        Functions functions = new Functions();
        BankDao bankDao = new BankDao();
        PayoutManager payoutManager=new PayoutManager();
        FileUploadBean fub=new FileUploadBean();

        BankWireManagerVO bankWireManagerVO = (BankWireManagerVO) session.getAttribute("bankWireManagerVO");
        String settlementCycleId = (String) session.getAttribute("settlementCycleId");
        String bankWireId = (String) session.getAttribute("bankWireId");
        String partnerId = request.getParameter("partnerId");

        try
        {
            fub.doUploadForMerchantSettled(request, null);
        }
        catch (Exception e){
            request.setAttribute("sberror", "File Already Exist in the System.Please upload new File OR Rename the File");
            session.setAttribute("bankWireManagerVO", bankWireManagerVO);
            rd = request.getRequestDispatcher("/manualReportGenerate.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
            return;
        }

        String action = fub.getFieldValue("action");
        String accountId = fub.getFieldValue("accountid");
        String memberId = fub.getFieldValue("memberid");
        String moneyReceived = fub.getFieldValue("ispaid");
        String amount=fub.getFieldValue("amount");
        String balanceAmount=fub.getFieldValue("balanceamount");
        String finalAmount = fub.getFieldValue("finalamount");
        String unpaidAmount = fub.getFieldValue("unpaidamt");
        String isTransactionFile = fub.getFieldValue("settledTransFile");
        String isReportFilePdf = fub.getFieldValue("settledReportFile");

        if(functions.isValueNull(moneyReceived)){
            if("Y".equalsIgnoreCase(moneyReceived))
                moneyReceived="paid";
            else if("N".equalsIgnoreCase(moneyReceived))
                moneyReceived="unpaid";
        }

        if ("next".equalsIgnoreCase(action))
        {
            if (!ESAPI.validator().isValidInput("memberid", memberId, "Numbers", 10, false))
            {
                errorMsg.append("Invalid MemberId" + EOL);
            }
            if (!ESAPI.validator().isValidInput("amount", fub.getFieldValue("amount"),"AmountStr",20,false))
            {
                errorMsg.append("Invalid Amount"+EOL);
            }
            if (!ESAPI.validator().isValidInput("balanceamount", fub.getFieldValue("balanceamount"),"AmountStr",20,false))
            {
                errorMsg.append("Invalid Balance Amount"+EOL);
            }
            if (errorMsg.length() > 0)
            {
                request.setAttribute("sberror", errorMsg.toString());
                rd = request.getRequestDispatcher("/manualReportGenerate.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
                return;
            }

            CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();
            String bankStartDate= commonFunctionUtil.convertDatepickerToTimestamp(fub.getFieldValue("bsdate"),fub.getFieldValue("bsdate"));
            String bankEndDate=commonFunctionUtil.convertDatepickerToTimestamp(fub.getFieldValue("bedate"),fub.getFieldValue("bedate"));
            String serverStartDate1=commonFunctionUtil.convertDatepickerToTimestamp(fub.getFieldValue("ssdate"),fub.getFieldValue("ssdate"));
            String serverEndDate1=commonFunctionUtil.convertDatepickerToTimestamp(fub.getFieldValue("sedate"),fub.getFieldValue("sedate"));
            String releaseDate = bankWireManagerVO.getRollingreservereleasedateupto();
            String moneyRecivedDate = bankWireManagerVO.getSettleddate();

            try
            {
                listOfMerchantId = payoutDAO.getMemberTerminalIdPartner(memberId, accountId);

                SettlementCycleVO settlementCycleVO = new SettlementCycleVO();
                settlementCycleVO.setStartDate(bankStartDate);
                settlementCycleVO.setEndDate(bankEndDate);
                settlementCycleVO.setAmount(amount);
                settlementCycleVO.setBalanceAmount(balanceAmount);
                settlementCycleVO.setFinalAmount(finalAmount);
                settlementCycleVO.setUnpaidAmount(unpaidAmount);
                settlementCycleVO.setStatus(moneyReceived);
                settlementCycleVO.setSettledTransactionFilePath(isTransactionFile);
                settlementCycleVO.setSettlementReportFilePath(isReportFilePdf);
                settlementCycleVO.setMemberId(memberId);
                settlementCycleVO.setAccountId(accountId);
                settlementCycleVO.setRollingReserveDate(releaseDate);
                settlementCycleVO.setPartnerId(partnerId);
                settlementCycleVO.setMarkForDeletion("N");
                settlementCycleVO.setRollingReserveIncluded("Y");
                settlementCycleVO.setMoneyRecivedDate(moneyRecivedDate);

                bankWireManagerVO = new BankWireManagerVO();
                bankWireManagerVO.setAccountId(accountId);
                bankWireManagerVO.setBank_start_date(bankStartDate);
                bankWireManagerVO.setBank_end_date(bankEndDate);
                bankWireManagerVO.setServer_start_date(serverStartDate1);
                bankWireManagerVO.setServer_end_date(serverEndDate1);

                String status = null;
                status = payoutDAO.generateSettlementCycleWireForAllTerminalPartner(settlementCycleVO, listOfMerchantId);
                if ("success".equals(status))
                {
                    bankDao.updateTheStatus(settlementCycleId, "MerchantWireGenerated");
                    bankDao.updateTheStatus(settlementCycleId, "Completed");
                    session.setAttribute("bankWireManagerVO", bankWireManagerVO);
                    session.setAttribute("success",status);
                    rd = request.getRequestDispatcher("/merchantPayoutReportSummary.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }
                else
                {
                    request.setAttribute("sberror", "Failed to Generate Merchant Report.");
                    session.setAttribute("bankWireManagerVO", bankWireManagerVO);
                    rd = request.getRequestDispatcher("/manualReportGenerate.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }
            }
            catch (Exception e)
            {
                logger.error("Exception:::::" + e + "====" + e.getStackTrace()[0].getFileName() + "\n" + e.getStackTrace()[0].getLineNumber());
                request.setAttribute("sberror", "Internal error while processing your request,please contact to  support team");
                session.setAttribute("bankWireManagerVO", bankWireManagerVO);
                rd = request.getRequestDispatcher("/manualReportGenerate.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
                return;
            }
        }
    }
}