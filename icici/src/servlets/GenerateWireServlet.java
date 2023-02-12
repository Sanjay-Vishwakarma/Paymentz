import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.core.GatewayAccount;
import com.manager.PayoutManager;
import com.manager.WLPartnerCommissionReportGenerator;
import com.manager.dao.PayoutDAO;
import com.manager.utils.AccountUtil;
import com.manager.vo.*;
import com.manager.vo.payoutVOs.*;
import org.owasp.esapi.User;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User:sandip
 * Date: 12/12/14
 * Time: 4:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class GenerateWireServlet extends HttpServlet
{
    Logger  logger=new Logger(GenerateWireServlet.class.getName());
    PayoutManager payoutManager=new PayoutManager();

    public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {

        logger.debug("Entering Into GenerateWire  Servlet");
        HttpSession session = request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        StringBuffer sbError = new StringBuffer();

        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        Functions functions=new Functions();
       /* RequestDispatcher rd1 = request.getRequestDispatcher("/whitelabelinvoice.jsp?&ctoken=" + user.getCSRFToken());
        wlPartnerInvoiceList*/
        String entity=(String)session.getAttribute("entity");
        if(functions.isValueNull(entity))
        {
            if("partner".equalsIgnoreCase(entity))
            {
                try
                {
                    PartnerPayoutReportVO partnerPayoutReportVO=(PartnerPayoutReportVO)session.getAttribute("partnerPayoutReportVO");
                    PartnerDetailsVO partnerDetailsVO=partnerPayoutReportVO.getPartnerDetailsVO();
                    TerminalVO terminalVO=partnerPayoutReportVO.getTerminalVO();
                    SettlementDateVO settlementDateVO=partnerPayoutReportVO.getSettlementDateVO();

                    String pdfFileName=payoutManager.createPartnerReportFile(partnerPayoutReportVO);

                    PartnerWireVO partnerWireVO=new PartnerWireVO();
                    partnerWireVO.setSettlementStartDate(settlementDateVO.getSettlementStartDate());
                    partnerWireVO.setSettlementEndDate(settlementDateVO.getSettlementEndDate());
                    partnerWireVO.setPartnerType("merchantaccount");
                    partnerWireVO.setPartnerChargeAmount(partnerPayoutReportVO.getPartnerTotalChargesAmount());
                    partnerWireVO.setPartnerUnpaidAmount(partnerPayoutReportVO.getPartnerWireUnpaidAmount());
                    partnerWireVO.setPartnerTotalFundedAmount(partnerPayoutReportVO.getPartnerTotalFundedAmount());
                    partnerWireVO.setCurrency(partnerPayoutReportVO.getCurrency());
                    partnerWireVO.setStatus("unpaid");
                    partnerWireVO.setSettlementReportFileName(pdfFileName);
                    partnerWireVO.setMarkedForDeletion("N");
                    partnerWireVO.setPartnerId(partnerDetailsVO.getPartnerId());
                    partnerWireVO.setMemberId(terminalVO.getMemberId());
                    partnerWireVO.setAccountId(terminalVO.getAccountId());
                    partnerWireVO.setTerminalId(terminalVO.getTerminalId());
                    partnerWireVO.setPayModeId(terminalVO.getPaymodeId());
                    partnerWireVO.setCardTypeId(terminalVO.getCardTypeId());
                    partnerWireVO.setDeclinedCoverDateUpTo(settlementDateVO.getDeclinedEndDate());
                    partnerWireVO.setReversedCoverDateUpTo(settlementDateVO.getReversedEndDate());
                    partnerWireVO.setChargebackCoverDateUpTo(settlementDateVO.getChargebackEndDate());

                    String wirecreationStatus=payoutManager.createPartnerWire(partnerWireVO);
                    request.setAttribute("wireStatus",wirecreationStatus);
                    RequestDispatcher rd = request.getRequestDispatcher("/partnerWirePayoutReport.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(request,response);
                    logger.debug("forward by GenerateWire Partner.....");
                }
                catch (SystemError systemError)
                {
                    logger.error("SystemError::::"+systemError);
                }
                catch (SQLException e)
                {
                    logger.error("SQLException::::"+e);
                }
            }
            else if("bankpartner".equals(entity))
            {
                String wireStatus="";
                logger.debug("Entering into bankpartner wire generation block");

                BankPartnerPayoutReportVO bankPartnerPayoutReportVO=(BankPartnerPayoutReportVO)session.getAttribute("bankPartnerPayoutReportVO");
                PartnerDetailsVO partnerDetailsVO=bankPartnerPayoutReportVO.getPartnerDetailsVO();
                SettlementDateVO settlementDateVO=bankPartnerPayoutReportVO.getSettlementDateVO();

                GatewayAccount gatewayAccount=bankPartnerPayoutReportVO.getGatewayAccount();
                BankPartnerWireVO bankPartnerWireVO=new BankPartnerWireVO();
                String bankPartnerPayoutPDFFileName=payoutManager.createBankPartnerReportFile(bankPartnerPayoutReportVO);

                bankPartnerWireVO.setSettlementStartDate(settlementDateVO.getSettlementStartDate());
                bankPartnerWireVO.setSettlementEndDate(settlementDateVO.getSettlementEndDate());
                bankPartnerWireVO.setPartnerType("bankaccount");
                bankPartnerWireVO.setPartnerChargeAmount(bankPartnerPayoutReportVO.getPartnerTotalChargesAmount());
                bankPartnerWireVO.setPartnerUnpaidAmount(0.00);
                bankPartnerWireVO.setPartnerTotalFundedAmount(bankPartnerPayoutReportVO.getPartnerTotalFundedAmount());
                bankPartnerWireVO.setCurrency(gatewayAccount.getCurrency());
                bankPartnerWireVO.setStatus("unpaid");
                bankPartnerWireVO.setSettlementReportFileName(bankPartnerPayoutPDFFileName);
                bankPartnerWireVO.setMarkedForDeletion("N");
                bankPartnerWireVO.setPartnerId(partnerDetailsVO.getPartnerId());
                bankPartnerWireVO.setMid(gatewayAccount.getMerchantId());
                bankPartnerWireVO.setAccountId(String.valueOf(gatewayAccount.getAccountId()));
                bankPartnerWireVO.setTerminalId("1");
                bankPartnerWireVO.setPayModeId("1");
                bankPartnerWireVO.setCardTypeId("1");
                bankPartnerWireVO.setDeclinedCoverDateUpTo(settlementDateVO.getDeclinedEndDate());
                bankPartnerWireVO.setReversedCoverDateUpTo(settlementDateVO.getReversedEndDate());
                bankPartnerWireVO.setChargebackCoverDateUpTo(settlementDateVO.getChargebackEndDate());
                try
                {
                    wireStatus=payoutManager.createBankPartnerWire(bankPartnerWireVO);
                }
                catch (Exception e)
                {
                    logger.error(e.getMessage());
                }

                request.setAttribute("wireStatus",wireStatus);
                RequestDispatcher rd = request.getRequestDispatcher("/bankPartnerPayoutReport.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request,response);
                logger.debug("forward by GenerateWire  Bank Partner.....");

            }
            else if("agent".equalsIgnoreCase(entity))
            {
                try
                {
                    AgentPayoutReportVO agentPayoutReportVO=(AgentPayoutReportVO)session.getAttribute("agentPayoutReportVO");
                    AgentDetailsVO agentDetailsVO=agentPayoutReportVO.getAgentDetailsVO();
                    TerminalVO terminalVO=agentPayoutReportVO.getTerminalVO();
                    SettlementDateVO settlementDateVO=agentPayoutReportVO.getSettlementDateVO();

                    AgentWireVO agentWireVO=new AgentWireVO();

                    String agentPayoutPDFFileName=payoutManager.createAgentReportFile(agentPayoutReportVO);

                    agentWireVO.setSettlementStartDate(settlementDateVO.getSettlementStartDate());
                    agentWireVO.setSettlementEndDate(settlementDateVO.getSettlementEndDate());
                    agentWireVO.setAgentType("merchantaccount");
                    agentWireVO.setAgentChargeAmount(agentPayoutReportVO.getAgentTotalChargesAmount());
                    agentWireVO.setAgentUnpaidAmount(agentPayoutReportVO.getAgentWireUnpaidAmount());
                    agentWireVO.setAgentTotalFundedAmount(agentPayoutReportVO.getAgentTotalFundedAmount());
                    agentWireVO.setCurrency(agentPayoutReportVO.getCurrency());
                    agentWireVO.setStatus("unpaid");
                    agentWireVO.setSettlementReportFileName(agentPayoutPDFFileName);
                    agentWireVO.setMarkedForDeletion("N");
                    agentWireVO.setAgentId(agentDetailsVO.getAgentId());
                    agentWireVO.setMemberId(terminalVO.getMemberId());
                    agentWireVO.setAccountId(terminalVO.getAccountId());
                    agentWireVO.setTerminalId(terminalVO.getTerminalId());
                    agentWireVO.setPayModeId(terminalVO.getPaymodeId());
                    agentWireVO.setCardTypeId(terminalVO.getCardTypeId());
                    agentWireVO.setDeclinedCoverDateUpTo(settlementDateVO.getDeclinedEndDate());
                    agentWireVO.setReversedCoverDateUpTo(settlementDateVO.getReversedEndDate());
                    agentWireVO.setChargebackCoverDateUpTo(settlementDateVO.getChargebackEndDate());

                    String wireStatus=payoutManager.createAgentWire(agentWireVO);

                    request.setAttribute("wireStatus",wireStatus);
                    RequestDispatcher rd = request.getRequestDispatcher("/agentPayoutReport.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(request,response);
                    logger.debug("forward by GenerateWire  Agent.....");
                }
                catch (SystemError systemError)
                {
                    logger.error("SystemError===="+systemError);
                }
                catch (SQLException se)
                {
                    logger.error("SQLException===="+se);
                }
                catch (Exception e)
                {
                    logger.error("GenericException===="+e);
                }
            }
            else if("merchant".equalsIgnoreCase(entity))
            {
                try
                {
                    MerchantPayoutReportVO merchantPayoutReportVO=(MerchantPayoutReportVO)session.getAttribute("merchantPayoutReportVO");
                    MerchantDetailsVO merchantDetailsVO=merchantPayoutReportVO.getMerchantDetailsVO();
                    SettlementDateVO  settlementDateVO=merchantPayoutReportVO.getSettlementDateVO();
                    TerminalVO terminalVO=merchantPayoutReportVO.getTerminalVO();
                    RollingReserveDateVO rollingReserveDateVO=merchantPayoutReportVO.getRollingReserveDateVO();
                    MerchantWireVO merchantWireVO=new MerchantWireVO();
                    AccountUtil accountUtil=new AccountUtil();

                    String tableName=accountUtil.getTableNameFromAccountId(terminalVO.getAccountId());

                    String merchantReportFileName=payoutManager.createMerchantReportFile(merchantPayoutReportVO);
                    String merchantTransFileName=payoutManager.createMerchantTransactionFile(settlementDateVO, tableName, terminalVO);

                    merchantWireVO.setSettlementStartDate(settlementDateVO.getSettlementStartDate());
                    merchantWireVO.setSettlementEndDate(settlementDateVO.getSettlementEndDate());
                    merchantWireVO.setCurrency(merchantPayoutReportVO.getCurrency());
                    merchantWireVO.setMarkedForDeletion("N");
                    merchantWireVO.setAmount(merchantPayoutReportVO.getMerchantTotalProcessingAmount());
                    merchantWireVO.setBalanceAmount(merchantPayoutReportVO.getMerchantTotalBalanceAmount());
                    merchantWireVO.setUnpaidAmount(0.00);
                    merchantWireVO.setNetFinalAmount(merchantPayoutReportVO.getMerchantTotalFundedAmount());
                    merchantWireVO.setStatus("unpaid");
                    merchantWireVO.setMemberId(merchantDetailsVO.getMemberId());
                    merchantWireVO.setTerminalId(terminalVO.getTerminalId());
                    merchantWireVO.setAccountId(terminalVO.getAccountId());
                    merchantWireVO.setPayModeId(terminalVO.getPaymodeId());
                    merchantWireVO.setCardTypeId(terminalVO.getCardTypeId());
                    merchantWireVO.setRollingReserveIncluded("Y");
                    merchantWireVO.setRollingReserveReleaseDateUpTo(rollingReserveDateVO.getRollingReserveEndDate());
                    merchantWireVO.setReportFileName(merchantReportFileName);
                    merchantWireVO.setTransactionFileName(merchantTransFileName);
                    merchantWireVO.setDeclinedCoverDateUpTo(settlementDateVO.getDeclinedEndDate());
                    merchantWireVO.setReversedCoverDateUpTo(settlementDateVO.getReversedEndDate());
                    merchantWireVO.setChargebackCoverDateUpTo(settlementDateVO.getChargebackEndDate());

                    String wireStatus=payoutManager.createMerchantWire(merchantWireVO);
                    PayoutDAO payoutDAO=new PayoutDAO();
                    if("success".equalsIgnoreCase(wireStatus))
                    {
                        payoutDAO.updateMemberCycleDetails(merchantDetailsVO.getMemberId(),terminalVO.getTerminalId() ,merchantPayoutReportVO.getVerifyOrderCount(),merchantPayoutReportVO.getRefundAlertCount(),merchantPayoutReportVO.getRetrivalRequestCount(),merchantPayoutReportVO.getSetupFeeCoveredDateUpTo());
                    }
                    request.setAttribute("wireStatus",wireStatus);
                    RequestDispatcher rd = request.getRequestDispatcher("/listMerchantPayoutReport.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(request,response);
                    logger.debug("forward by GenerateWire  Merchant.....");
                }
                catch (SystemError systemError)
                {
                    logger.error(systemError);
                }
                catch (Exception e)
                {
                    logger.error(e);
                }
            }
            else if("bankagent".equalsIgnoreCase(entity))
            {
                String wireStatus="";
                logger.debug("Entering into bankagent wire generation block");
                BankAgentPayoutReportVO bankAgentPayoutReportVO=(BankAgentPayoutReportVO)session.getAttribute("bankAgentPayoutReportVO");
                AgentDetailsVO agentDetailsVO=bankAgentPayoutReportVO.getAgentDetailsVO();
                SettlementDateVO settlementDateVO=bankAgentPayoutReportVO.getSettlementDateVO();

                GatewayAccount gatewayAccount=bankAgentPayoutReportVO.getGatewayAccount();
                BankAgentWireVO bankAgentWireVO=new BankAgentWireVO();
                String bankAgentPayoutPDFFileName=payoutManager.createBankAgentReportFile(bankAgentPayoutReportVO);

                bankAgentWireVO.setSettlementStartDate(settlementDateVO.getSettlementStartDate());
                bankAgentWireVO.setSettlementEndDate(settlementDateVO.getSettlementEndDate());
                bankAgentWireVO.setAgentType("bankaccount");
                bankAgentWireVO.setAgentChargeAmount(bankAgentPayoutReportVO.getAgentTotalChargesAmount());
                bankAgentWireVO.setAgentUnpaidAmount(0.00);
                bankAgentWireVO.setAgentTotalFundedAmount(bankAgentPayoutReportVO.getAgentTotalFundedAmount());
                bankAgentWireVO.setCurrency(gatewayAccount.getCurrency());
                bankAgentWireVO.setStatus("unpaid");
                bankAgentWireVO.setSettlementReportFileName(bankAgentPayoutPDFFileName);
                bankAgentWireVO.setMarkedForDeletion("N");
                bankAgentWireVO.setAgentId(agentDetailsVO.getAgentId());
                bankAgentWireVO.setMid(gatewayAccount.getMerchantId());
                bankAgentWireVO.setAccountId(String.valueOf(gatewayAccount.getAccountId()));
                bankAgentWireVO.setTerminalId("1");
                bankAgentWireVO.setPayModeId("1");
                bankAgentWireVO.setCardTypeId("1");
                bankAgentWireVO.setDeclinedCoverDateUpTo(settlementDateVO.getDeclinedEndDate());
                bankAgentWireVO.setReversedCoverDateUpTo(settlementDateVO.getReversedEndDate());
                bankAgentWireVO.setChargebackCoverDateUpTo(settlementDateVO.getChargebackEndDate());
                try
                {
                    wireStatus=payoutManager.createBankAgentWire(bankAgentWireVO);
                }
                catch (Exception e)
                {
                    logger.error(e.getMessage());
                }

                request.setAttribute("wireStatus",wireStatus);
                RequestDispatcher rd = request.getRequestDispatcher("/bankAgentPayoutReport.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request,response);
                logger.debug("forward by GenerateWire  BankAhent.....");
            }
            else if ("wlpartner".equalsIgnoreCase(entity))
            {
                String wireStatus = "";
                WLPartnerCommissionReportGenerator partnerCommissionReportGenerator = new WLPartnerCommissionReportGenerator();
                WLPartnerCommissionReportVO wlPartnerCommissionReportVO = (WLPartnerCommissionReportVO) session.getAttribute("wlPartnerCommissionReportVOForWireGeneration");
                session.removeAttribute("wlPartnerCommissionReportVOForWireGeneration");

                PartnerDetailsVO partnerDetailsVO = wlPartnerCommissionReportVO.getPartnerDetailsVO();
                String wlPartnerInvoiceFileName =partnerCommissionReportGenerator.createWLPartnerReportFile(wlPartnerCommissionReportVO);

                WLPartnerInvoiceVO wlPartnerInvoiceVO = new WLPartnerInvoiceVO();
                wlPartnerInvoiceVO.setPartnerId(partnerDetailsVO.getPartnerId());
                wlPartnerInvoiceVO.setStartDate(wlPartnerCommissionReportVO.getStartDate());
                wlPartnerInvoiceVO.setEndDate(wlPartnerCommissionReportVO.getEndDate());
                wlPartnerInvoiceVO.setNetFinalAmount(wlPartnerCommissionReportVO.getNetFinalFeeAmount());
                wlPartnerInvoiceVO.setUnpaidAmount(0.00);
                wlPartnerInvoiceVO.setStatus("unpaid");
                wlPartnerInvoiceVO.setCurrency(partnerDetailsVO.getReportingCurrency());
                wlPartnerInvoiceVO.setReportFilePath(wlPartnerInvoiceFileName);
                wlPartnerInvoiceVO.setActionExecutor(user.getAccountName());
                boolean isDuplicatedFound = false;
                isDuplicatedFound = partnerCommissionReportGenerator.checkForDuplicated(wlPartnerInvoiceVO);
                if (isDuplicatedFound == true)
                {
                    wireStatus="error";
                }
                if (wireStatus.length() > 0)
                {
                    request.setAttribute("wireStatus", wireStatus);
                    RequestDispatcher rd = request.getRequestDispatcher("/wlPartnerInvoiceList.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(request, response);
                }
                boolean status = partnerCommissionReportGenerator.generateWhiteLabelPartnerInvoice(wlPartnerInvoiceVO);
                if (status)
                {
                    wireStatus = "success";
                }
                else
                {
                    wireStatus = "failure";
                }
                request.setAttribute("wireStatus", wireStatus);
                RequestDispatcher rd = request.getRequestDispatcher("/wlPartnerInvoiceList.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
            }
        }
    }
}
