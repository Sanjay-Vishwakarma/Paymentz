import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.manager.ChargeManager;
import com.manager.PayoutManager;
import com.manager.TerminalManager;
import com.manager.dao.AccountDAO;
import com.manager.dao.AgentDAO;
import com.manager.dao.MerchantDAO;
import com.manager.dao.PayoutDAO;
import com.manager.enums.Charge_category;
import com.manager.enums.Charge_keyword;
import com.manager.enums.Charge_subKeyword;
import com.manager.enums.Charge_unit;
import com.manager.utils.AccountUtil;
import com.manager.vo.*;
import com.manager.vo.payoutVOs.AgentPayoutReportVO;
import com.manager.vo.payoutVOs.ChargeDetailsVO;
import com.manager.vo.payoutVOs.GrossChargeVO;
import com.manager.vo.payoutVOs.WireChargeVO;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Sandip
 * Date: 12/10/14
 * Time: 3:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class GenerateAgentPayoutServlet extends HttpServlet
{
    static Logger logger = new Logger(GenerateAgentPayoutServlet.class.getName());

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        HttpSession session = request.getSession();
        Functions functions = new Functions();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        try
        {

            String sAgentId=request.getParameter("agentid");
            String sMemberId="";
            if(!request.getParameter("memberid").equals("0"))
            {
                sMemberId= request.getParameter("memberid");

            }
            String sAccountId="";

            if(!request.getParameter("accountid").equals("0"))
            {
                sAccountId= request.getParameter("accountid");

            }
            String pgtypeId=functions.splitGatewaySet(request.getParameter("pgtypeid"));
            if(!pgtypeId.equals("0"))
            {
                request.setAttribute("pgtypeid",pgtypeId);
            }
            else {
                pgtypeId="";
            }
            String sPayModeId=request.getParameter("paymode");
            String sCardTypeID=request.getParameter("cardtype");
            request.setAttribute("cardtype",sCardTypeID);

            Hashtable<String, String> requestDetails=new Hashtable<String, String>();

            requestDetails.put("sAgentId",sAgentId);
            requestDetails.put("sMemberId",sMemberId);
            requestDetails.put("accountid",sAccountId);
            requestDetails.put("sPayModeId",sPayModeId);
            requestDetails.put("sCardTypeID",sCardTypeID);

            List<String> error=validateMandatoryParaAgentPayout(requestDetails);
            List<String> errorList=validateOptionalParaAgentPayout(requestDetails);
            if(error.size()>0 || errorList.size()>0)
            {
                request.setAttribute("errorList",error);
                RequestDispatcher rd = request.getRequestDispatcher("/agentPayoutReport.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request,response);
                logger.debug("forward by GenerateAgentPayoutServlet.....");
            }
            else
            {
                TerminalManager terminalManager=new TerminalManager();
                PayoutManager payoutManager=new PayoutManager();

                AccountDAO accountDAO=new AccountDAO();
                AgentDAO agentDAO=new AgentDAO();
                MerchantDAO merchantDAO=new MerchantDAO();
                PayoutDAO  payoutDAO=new PayoutDAO();
                AccountUtil accountUtil=new AccountUtil();

                SettlementDateVO settlementDateVO=null;

                double totalChargesAmount=0.00;
                double agentTotalFundedAmount=0.00;
                String sCurrency=null;
                String sTableName=null;

                /*String settledTransStartDate=null;
                String settledTransEndDate=null;
                String authfailedTransStartDate=null;
                String authfailedTransEndDate=null;
                String reversedTransStartDate=null;
                String reversedTransEndDate=null;
                String chargebackTransStartDate=null;
                String chargebackTransEndDate=null;
*/
                long settledTransCount = 0;
                long reversedTransCount = 0;
                long chargebackTransCount = 0;
                long authfailedTransCount = 0;

                double totalAuthfailedTransAmount=0.00;
                double totalSettledTransAmount=0.00;
                double totalReversedTransAmount = 0.00;
                double totalChargebackTransAmount = 0.00;

                AgentDetailsVO agentDetailsVO=agentDAO.getAgentDetails(sAgentId);
                if(agentDetailsVO==null)
                {
                    logger.debug("Invalid Agent "+sAgentId);
                    error.add("Invalid Agent "+sAgentId);
                    request.setAttribute("errorList",error);
                    RequestDispatcher rd = request.getRequestDispatcher("/agentPayoutReport.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(request,response);
                    return;
                }

                String agentReportType=payoutManager.getAgentReportType(sMemberId,sAccountId,sPayModeId,sCardTypeID);
                MerchantDetailsVO merchantDetailsVO=null;
                if("summaryReport".equalsIgnoreCase(agentReportType))
                {
                    //This Block Executed When AgentId and MemberId Selected only
                    HashMap<String,List<ChargeDetailsVO>> stringListHashMap=new HashMap<String, List<ChargeDetailsVO>>();

                    merchantDetailsVO=merchantDAO.getMemberDetails(sMemberId,sAgentId);
                    if(merchantDetailsVO==null)
                    {
                        logger.debug("No Valid Member ID "+sMemberId +" For Agent Id=="+sAgentId);
                        error.add("No Valid Member ID "+sMemberId +" For Agent Id=="+sAgentId);
                        request.setAttribute("errorList",error);
                        RequestDispatcher rd = request.getRequestDispatcher("/agentPayoutReport.jsp?ctoken="+user.getCSRFToken());
                        rd.forward(request,response);
                        return;
                    }

                    if(functions.isValueNull(sAccountId))
                    {
                        String memberFirstTransDate=payoutDAO.getMemberFirstTransactionDateOnAccountId(sMemberId,sAccountId);
                        if(memberFirstTransDate==null)
                        {
                            logger.debug("No Transactions Founds For Member "+sMemberId +" On Account Id=="+sAccountId);
                            error.add("No Transactions Founds For Member "+sMemberId +" On Account Id=="+sAccountId);
                            request.setAttribute("errorList",error);
                            RequestDispatcher rd = request.getRequestDispatcher("/agentPayoutReport.jsp?ctoken="+user.getCSRFToken());
                            rd.forward(request,response);
                            return;
                        }

                        List<ChargeVO> chargeVOtest=payoutDAO.getChargesAsPerAccount(sMemberId,sAccountId);
                        if(chargeVOtest.size()<1)
                        {
                            logger.debug("No Charges Mapped  For member "+sMemberId +" On Account Id=="+sAccountId);
                            error.add("No Charges Mapped  For member " + sMemberId + " On Account");
                            request.setAttribute("errorList", error);
                            RequestDispatcher rd = request.getRequestDispatcher("/agentPayoutReport.jsp?ctoken="+user.getCSRFToken());
                            rd.forward(request,response);
                        }

                        GatewayAccount account= GatewayAccountService.getGatewayAccount(sAccountId);
                        GatewayType gatewayType = GatewayTypeService.getGatewayType(account.getPgTypeId());
                        sCurrency=gatewayType.getCurrency();
                        sTableName=accountUtil.getTableNameFromAccountId(sAccountId);

                        settlementDateVO=payoutManager.getSettlementDateOnAccount(sMemberId,sAccountId,sTableName);
                        settlementDateVO.setSettlementStartDate(memberFirstTransDate);
                        settlementDateVO.setDeclinedStartDate(memberFirstTransDate);
                        settlementDateVO.setReversedStartDate(memberFirstTransDate);
                        settlementDateVO.setChargebackStartDate(memberFirstTransDate);

                        List<TerminalVO> terminalVOs=terminalManager.getTerminalsByMemberAccountIdForPayoutReport(sMemberId, sAccountId);
                        for(TerminalVO terminalVO:terminalVOs)
                        {
                            List<ChargeDetailsVO> chargeDetailsVOs=new ArrayList<ChargeDetailsVO>();
                            TransactionSummaryVO summaryVO=null;

                            summaryVO=payoutDAO.getAuthFailCountAmountByDtstamp(memberFirstTransDate, terminalVO, sTableName);
                            authfailedTransCount=summaryVO.getCountOfAuthfailed();
                            totalAuthfailedTransAmount=summaryVO.getAuthfailedAmount();

                            //Getting Settled Count & Settled Amount
                            summaryVO=payoutDAO.getSettledCountAmountByDtstamp(memberFirstTransDate,terminalVO,sTableName);
                            settledTransCount=summaryVO.getCountOfSettled();
                            totalSettledTransAmount=summaryVO.getSettledAmount();

                            //Getting Reversed Count & Reversed Amount
                            summaryVO=payoutDAO.getReversalCountAmountByTimestamp(memberFirstTransDate,terminalVO,sTableName);
                            reversedTransCount=summaryVO.getCountOfReversed();
                            totalReversedTransAmount=summaryVO.getReversedAmount();

                            //Getting Chargeback Count & Chargeback Amount
                            summaryVO=payoutDAO.getChargebackCountAmountByTimestamp(memberFirstTransDate,terminalVO,sTableName);
                            chargebackTransCount=summaryVO.getCountOfChargeback();
                            totalChargebackTransAmount=summaryVO.getChargebackAmount();

                            TransactionSummaryVO summaryVOFinal=new TransactionSummaryVO();

                            summaryVOFinal.setCountOfAuthfailed(authfailedTransCount);
                            summaryVOFinal.setAuthfailedAmount(totalAuthfailedTransAmount);

                            summaryVOFinal.setCountOfSettled(settledTransCount);
                            summaryVOFinal.setSettledAmount(totalSettledTransAmount);

                            summaryVOFinal.setCountOfReversed(reversedTransCount);
                            summaryVOFinal.setReversedAmount(totalReversedTransAmount);

                            summaryVOFinal.setCountOfChargeback(chargebackTransCount);
                            summaryVOFinal.setChargebackAmount(totalChargebackTransAmount);

                            String currentDate=Functions.convertDateDBFormat(Calendar.getInstance().getTime());
                            ChargeManager chargeManager=new ChargeManager();

                            List<ChargeVO> chargeVOs=accountDAO.getChargesAsPerTerminal(terminalVO);
                            ChargeDetailsVO chargeDetailsVO=null;
                            for(ChargeVO chargeVO:chargeVOs)
                            {
                                logger.debug("ChargeName======="+chargeVO.getChargename());
                                if(Charge_keyword.TotalReserveGenerated.toString().equals(chargeVO.getKeyword()) || Charge_keyword.TotalReserveRefunded.toString().equals(chargeVO.getKeyword()) || Charge_keyword.CalculatedReserveRefund.toString().equals(chargeVO.getKeyword()) || Charge_keyword.Wire.toString().equals(chargeVO.getKeyword()) || Charge_keyword.GrossBalanceAmount.toString().equals(chargeVO.getKeyword()))
                                {
                                    logger.debug("Skipping "+chargeVO.getChargename()+" Form Agent Summary Charge Payout");
                                    continue;
                                }
                                else
                                {
                                    //Get Agent Charge Version Rate
                                    String chargeVersionRate=chargeManager.getAgentChargeVersionRate(chargeVO.getMappingid(),currentDate);
                                    if(chargeVersionRate!=null)
                                    {
                                        chargeVO.setAgentChargeValue(chargeVersionRate);
                                    }

                                    chargeDetailsVO=payoutManager.calculateAgentChargeValue(chargeVO,terminalVO,sTableName,summaryVOFinal,memberFirstTransDate);
                                    totalChargesAmount=totalChargesAmount+chargeDetailsVO.getTotal();
                                    logger.debug("=,"+chargeDetailsVO.getTotal());
                                    chargeDetailsVOs.add(chargeDetailsVO);
                                }
                            }
                            agentTotalFundedAmount=agentTotalFundedAmount+totalChargesAmount;
                            stringListHashMap.put(terminalVO.toString(),chargeDetailsVOs);
                        }

                        AgentPayoutReportVO agentPayoutReportVO=new AgentPayoutReportVO();

                        agentPayoutReportVO.setCurrency(sCurrency);
                        agentPayoutReportVO.setAgentTotalChargesAmount(totalChargesAmount);
                        agentPayoutReportVO.setAgentDetailsVO(agentDetailsVO);
                        agentPayoutReportVO.setMerchantDetailsVO(merchantDetailsVO);
                        agentPayoutReportVO.setStringListHashMap(stringListHashMap);
                        agentPayoutReportVO.setSettlementDateVO(settlementDateVO);

                        request.setAttribute("agentPayoutReportVO",agentPayoutReportVO);
                        RequestDispatcher rd = request.getRequestDispatcher("/agentPayoutSummary.jsp?ctoken="+user.getCSRFToken());
                        rd.forward(request,response);
                    }
                    else
                    {
                        List<TerminalVO> terminalVOs=terminalManager.getTerminalsByMerchantId(sMemberId);
                        for(TerminalVO terminalVO:terminalVOs)
                        {
                            sAccountId=terminalVO.getAccountId();
                            sTableName=accountUtil.getTableNameFromAccountId(sAccountId);

                            if("transaction_icicicredit".equalsIgnoreCase(sTableName))
                            {
                                logger.debug(sTableName);
                                continue;
                            }

                            String memberFirstTransDate=payoutDAO.getMemberFirstTransactionDateOnAccountId(sMemberId,sAccountId);
                            if(memberFirstTransDate==null)
                            {
                                logger.debug("No Transactions Founds For Member "+sMemberId +" On Account Id=="+sAccountId);
                                continue;
                            }

                            List<ChargeDetailsVO> chargeDetailsVOs=new ArrayList<ChargeDetailsVO>();
                            TransactionSummaryVO summaryVO=null;

                            summaryVO=payoutDAO.getAuthFailCountAmountByDtstamp(null, terminalVO, sTableName);
                            authfailedTransCount=summaryVO.getCountOfAuthfailed();
                            totalAuthfailedTransAmount=summaryVO.getAuthfailedAmount();

                            //Getting Settled Count & Settled Amount
                            summaryVO=payoutDAO.getSettledCountAmountByDtstamp(null,terminalVO,sTableName);
                            settledTransCount=summaryVO.getCountOfSettled();
                            totalSettledTransAmount=summaryVO.getSettledAmount();

                            //Getting Reversed Count & Reversed Amount
                            summaryVO=payoutDAO.getReversalCountAmountByTimestamp(null,terminalVO,sTableName);
                            reversedTransCount=summaryVO.getCountOfReversed();
                            totalReversedTransAmount=summaryVO.getReversedAmount();

                            //Getting Chargeback Count & Chargeback Amount
                            summaryVO=payoutDAO.getChargebackCountAmountByTimestamp(null,terminalVO,sTableName);
                            chargebackTransCount=summaryVO.getCountOfChargeback();
                            totalChargebackTransAmount=summaryVO.getChargebackAmount();

                            String currentDate=Functions.convertDateDBFormat(Calendar.getInstance().getTime());
                            ChargeManager chargeManager=new ChargeManager();


                            TransactionSummaryVO summaryVOFinal=new TransactionSummaryVO();

                            summaryVOFinal.setCountOfAuthfailed(authfailedTransCount);
                            summaryVOFinal.setAuthfailedAmount(totalAuthfailedTransAmount);

                            summaryVOFinal.setCountOfSettled(settledTransCount);
                            summaryVOFinal.setSettledAmount(totalSettledTransAmount);

                            summaryVOFinal.setCountOfReversed(reversedTransCount);
                            summaryVOFinal.setReversedAmount(totalReversedTransAmount);

                            summaryVOFinal.setCountOfChargeback(chargebackTransCount);
                            summaryVOFinal.setChargebackAmount(totalChargebackTransAmount);


                            List<ChargeVO> chargeVOs=accountDAO.getChargesAsPerTerminal(terminalVO);
                            ChargeDetailsVO chargeDetailsVO=null;
                            for(ChargeVO chargeVO:chargeVOs)
                            {
                                if(Charge_keyword.TotalReserveGenerated.toString().equals(chargeVO.getKeyword()) || Charge_keyword.TotalReserveRefunded.toString().equals(chargeVO.getKeyword()) || Charge_keyword.CalculatedReserveRefund.toString().equals(chargeVO.getKeyword()) || Charge_keyword.Wire.toString().equals(chargeVO.getKeyword()) || Charge_keyword.GrossBalanceAmount.toString().equals(chargeVO.getKeyword()))
                                {
                                    logger.debug("Skipping "+chargeVO.getChargename()+" Form Agent Summary Charge Payout");
                                    continue;
                                }
                                else
                                {
                                    //Get Agent Charge Version Rate
                                    String chargeVersionRate=chargeManager.getAgentChargeVersionRate(chargeVO.getMappingid(),currentDate);
                                    if(chargeVersionRate!=null)
                                    {
                                        chargeVO.setAgentChargeValue(chargeVersionRate);
                                    }

                                    chargeDetailsVO=payoutManager.calculateAgentChargeValue(chargeVO,terminalVO,sTableName,summaryVOFinal,memberFirstTransDate);
                                    totalChargesAmount=totalChargesAmount+chargeDetailsVO.getTotal();
                                    logger.debug("=,"+chargeDetailsVO.getTotal());
                                    chargeDetailsVOs.add(chargeDetailsVO);
                                }
                            }
                            agentTotalFundedAmount=agentTotalFundedAmount+totalChargesAmount;
                            stringListHashMap.put(terminalVO.toString(),chargeDetailsVOs);

                        }

                        AgentPayoutReportVO agentPayoutReportVO=new AgentPayoutReportVO();
                        agentPayoutReportVO.setAgentTotalChargesAmount(totalChargesAmount);
                        agentPayoutReportVO.setAgentDetailsVO(agentDetailsVO);
                        agentPayoutReportVO.setMerchantDetailsVO(merchantDetailsVO);
                        agentPayoutReportVO.setStringListHashMap(stringListHashMap);
                        agentPayoutReportVO.setReportingDate(Functions.convertDateDBFormat(Calendar.getInstance().getTime()));

                        request.setAttribute("agentPayoutReportVO",agentPayoutReportVO);
                        RequestDispatcher rd = request.getRequestDispatcher("/agentPerMemberPayoutSummary.jsp?ctoken="+user.getCSRFToken());
                        rd.forward(request,response);
                    }

                }
                else if("wireReport".equalsIgnoreCase(agentReportType))
                {
                    long vCntTotal = 0;
                    long totalPaidWireCount=0;

                    double vDblTotalAmount = 0.00;
                    double vDblTotalBalanceAmount=0.00;
                    double vDblWirePaidAmount=0.00;
                    double vDblWireUnPaidAmount=0.00;
                    double vDblWireBalanceAmount=0.00;
                    double cDblTotalFundedToBank=0.00;
                    double vDblWireAmount=0.00;
                    double vDblWireFeeTotal=0.00;
                    double vDblGrossChargesTotal=0.00;

                    String sReportingDate=null;
                    String sMemberTerminalId=null;

                    merchantDetailsVO=merchantDAO.getMemberDetails(sMemberId, sAgentId);

                    if(merchantDetailsVO==null)
                    {
                        logger.debug("No Valid Member ID "+sMemberId +" For Agent Id=="+sAgentId);
                        error.add("No Valid Member ID "+sMemberId +" For Agent Id=="+sAgentId);
                        request.setAttribute("errorList",error);
                        RequestDispatcher rd = request.getRequestDispatcher("/agentPayoutReport.jsp?ctoken="+user.getCSRFToken());
                        rd.forward(request,response);
                        return;
                    }

                    String memberFirstTransDate=payoutDAO.getMemberFirstTransactionDateOnAccountId(sMemberId,sAccountId);
                    if(memberFirstTransDate!=null)
                    {
                        sMemberTerminalId=payoutDAO.getMemberTerminalId(sMemberId,sAccountId,sPayModeId,sCardTypeID);
                        TerminalVO terminalVO=terminalManager.getTerminalByTerminalId(sMemberTerminalId);

                        List<ChargeVO> chargeVOs=accountDAO.getChargesAsPerTerminal(terminalVO);
                        if(chargeVOs.size()>0)
                        {
                            //Getting First And Last Transaction Date
                            sReportingDate =new Date().toString();
                            GatewayAccount account= GatewayAccountService.getGatewayAccount(sAccountId);
                            GatewayType gatewayType = GatewayTypeService.getGatewayType(account.getPgTypeId());
                            sCurrency=gatewayType.getCurrency();

                            sTableName=accountUtil.getTableNameFromAccountId(sAccountId);

                            WireAmountVO wireAmountVO = new WireAmountVO();
                            payoutDAO.setAgentWireAmountVO(agentDetailsVO,terminalVO,wireAmountVO);
                            payoutDAO.setAgentWireUnpaidAmount(agentDetailsVO,terminalVO,wireAmountVO);

                            vDblWirePaidAmount=wireAmountVO.getPaidAmount();
                            vDblWireUnPaidAmount=wireAmountVO.getUnpaidAmount();
                            vDblWireBalanceAmount=wireAmountVO.getWireBalanceAmount();
                            vDblWireBalanceAmount=vDblWireBalanceAmount+vDblWireUnPaidAmount;


                            totalPaidWireCount=wireAmountVO.getWireCount();

                            settlementDateVO=payoutManager.getSettlementDateOnTerminal(terminalVO,sTableName);
                            settlementDateVO.setSettlementStartDate(memberFirstTransDate);
                            settlementDateVO.setDeclinedStartDate(memberFirstTransDate);
                            settlementDateVO.setReversedStartDate(memberFirstTransDate);
                            settlementDateVO.setChargebackStartDate(memberFirstTransDate);

                            TransactionSummaryVO summaryVO=null;

                            summaryVO=payoutDAO.getAuthFailCountAmountByDtstamp(memberFirstTransDate, terminalVO, sTableName);
                            authfailedTransCount=summaryVO.getCountOfAuthfailed();
                            totalAuthfailedTransAmount=summaryVO.getAuthfailedAmount();

                            //Getting Settled Count & Settled Amount
                            summaryVO=payoutDAO.getSettledCountAmountByDtstamp(memberFirstTransDate,terminalVO,sTableName);
                            settledTransCount=summaryVO.getCountOfSettled();
                            totalSettledTransAmount=summaryVO.getSettledAmount();

                            //Getting Reversed Count & Reversed Amount
                            summaryVO=payoutDAO.getReversalCountAmountByTimestamp(memberFirstTransDate,terminalVO,sTableName);
                            reversedTransCount=summaryVO.getCountOfReversed();
                            totalReversedTransAmount=summaryVO.getReversedAmount();

                            //Getting Chargeback Count & Chargeback Amount
                            summaryVO=payoutDAO.getChargebackCountAmountByTimestamp(memberFirstTransDate,terminalVO,sTableName);
                            chargebackTransCount=summaryVO.getCountOfChargeback();
                            totalChargebackTransAmount=summaryVO.getChargebackAmount();

                            //Total Count (includes authfailed; settled; reversed; chargeback) & Total Amount includes (settled; reversed; chargeback)
                            vDblTotalAmount = totalSettledTransAmount + totalReversedTransAmount + totalChargebackTransAmount;
                            vCntTotal = authfailedTransCount + settledTransCount + reversedTransCount + chargebackTransCount;

                            TransactionSummaryVO summaryVOFinal=new TransactionSummaryVO();

                            summaryVOFinal.setCountOfAuthfailed(authfailedTransCount);
                            summaryVOFinal.setAuthfailedAmount(totalAuthfailedTransAmount);

                            summaryVOFinal.setCountOfSettled(settledTransCount);
                            summaryVOFinal.setSettledAmount(totalSettledTransAmount);

                            summaryVOFinal.setCountOfReversed(reversedTransCount);
                            summaryVOFinal.setReversedAmount(totalReversedTransAmount);

                            summaryVOFinal.setCountOfChargeback(chargebackTransCount);
                            summaryVOFinal.setChargebackAmount(totalChargebackTransAmount);

                            List<ChargeDetailsVO> chargeDetailsVOs=new ArrayList<ChargeDetailsVO>();
                            List<ChargeDetailsVO> grossTypeDetailsVOs=new ArrayList<ChargeDetailsVO>();
                            WireChargeVO wireChargeVO=null;

                            String currentDate=Functions.convertDateDBFormat(Calendar.getInstance().getTime());
                            ChargeManager chargeManager=new ChargeManager();
                            ChargeDetailsVO  chargeDetailsVO=null;
                            for(ChargeVO chargeVO:chargeVOs)
                            {
                                logger.debug("ChargeName======="+chargeVO.getChargename());
                                if(Charge_keyword.TotalReserveGenerated.toString().equals(chargeVO.getKeyword()) || Charge_keyword.TotalReserveRefunded.toString().equals(chargeVO.getKeyword()) || Charge_keyword.CalculatedReserveRefund.toString().equals(chargeVO.getKeyword()))
                                {
                                    logger.debug("Skipping "+chargeVO.getChargename()+" From The Agent Payout Report");
                                    continue;
                                }
                                else
                                {
                                    //Get Agent Charge Version Rate
                                    String chargeVersionRate=chargeManager.getAgentChargeVersionRate(chargeVO.getMappingid(),currentDate);
                                    if(chargeVersionRate!=null)
                                    {
                                        chargeVO.setAgentChargeValue(chargeVersionRate);
                                    }

                                    if(Charge_category.Others.toString().equals(chargeVO.getCategory()) && Charge_keyword.Wire.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
                                    {
                                        wireChargeVO=new WireChargeVO();
                                        wireChargeVO.setChargeValue(chargeVO.getAgentChargeValue());
                                        wireChargeVO.setChargeName(chargeVO.getChargename());
                                        continue;
                                    }
                                    else if(Charge_category.Others.toString().equals(chargeVO.getCategory()) && Charge_keyword.GrossBalanceAmount.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
                                    {
                                        chargeDetailsVO.setChargeValue(chargeVO.getAgentChargeValue());
                                        chargeDetailsVO.setValueType(chargeVO.getValuetype());
                                        grossTypeDetailsVOs.add(chargeDetailsVO);
                                        continue;
                                    }
                                    else
                                    {
                                        chargeDetailsVO=payoutManager.calculateAgentChargeValue(chargeVO,terminalVO,sTableName,summaryVOFinal,memberFirstTransDate);
                                        totalChargesAmount=totalChargesAmount+chargeDetailsVO.getTotal();
                                        logger.debug("=,"+chargeDetailsVO.getTotal());
                                        chargeDetailsVOs.add(chargeDetailsVO);
                                    }
                                }
                            }

                            double vDblDeduct=totalChargesAmount;
                            vDblTotalBalanceAmount=vDblDeduct;

                            List<GrossChargeVO> grossTypeChargeVOList=new ArrayList<GrossChargeVO>();
                            GrossChargeVO grossChargeVO=null;
                            for(ChargeDetailsVO grossTypeDetailsVO:grossTypeDetailsVOs)
                            {
                                double vDblGrossAmount=vDblTotalBalanceAmount;
                                double vDblGrossChargesAmount=0.00;
                                grossChargeVO=new GrossChargeVO();

                                vDblGrossChargesAmount=(vDblTotalBalanceAmount*Double.valueOf(grossTypeDetailsVO.getChargeValue())/100);
                                if(vDblGrossChargesAmount<0)
                                {
                                    vDblGrossChargesAmount=0.00;
                                }
                                vDblGrossChargesTotal=vDblGrossChargesTotal+vDblGrossChargesAmount;

                                if(Charge_unit.Percentage.toString().equals(grossTypeDetailsVO.getValueType()))
                                {
                                    grossChargeVO.setChargeValue(grossTypeDetailsVO.getChargeValue()+"%");
                                }
                                else
                                {
                                    grossChargeVO.setChargeValue(grossTypeDetailsVO.getChargeValue());
                                }
                                grossChargeVO.setChargeName(grossTypeDetailsVO.getChargeName());
                                grossChargeVO.setCount(0);
                                grossChargeVO.setAmount(vDblGrossAmount);
                                grossChargeVO.setTotal(vDblGrossChargesAmount);
                                grossTypeChargeVOList.add(grossChargeVO);
                            }
                            if(wireChargeVO!=null)
                            {
                                vDblWireFeeTotal=totalPaidWireCount*Double.parseDouble(wireChargeVO.getChargeValue());
                                wireChargeVO.setCount(totalPaidWireCount);
                                wireChargeVO.setAmount(0.00);
                                wireChargeVO.setTotal(vDblWireFeeTotal);
                            }

                            cDblTotalFundedToBank=(totalChargesAmount+vDblWireBalanceAmount)-(vDblWirePaidAmount+vDblWireFeeTotal+vDblGrossChargesTotal);


                            AgentPayoutReportVO agentPayoutReportVO=new AgentPayoutReportVO();

                            agentPayoutReportVO.setReportingDate(sReportingDate);
                            agentPayoutReportVO.setCurrency(sCurrency);
                            agentPayoutReportVO.setAgentTotalChargesAmount(totalChargesAmount);

                            agentPayoutReportVO.setAgentWirePaidAmount(vDblWirePaidAmount);
                            agentPayoutReportVO.setAgentWireUnpaidAmount(vDblWireUnPaidAmount);
                            agentPayoutReportVO.setAgentTotalBalanceAmount(vDblWireBalanceAmount);
                            agentPayoutReportVO.setAgentTotalFundedAmount(cDblTotalFundedToBank);

                            agentPayoutReportVO.setTerminalVO(terminalVO);

                            agentPayoutReportVO.setAgentDetailsVO(agentDetailsVO);
                            agentPayoutReportVO.setChargesDetailsVOs(chargeDetailsVOs);
                            agentPayoutReportVO.setGrossTypeChargeVOList(grossTypeChargeVOList);
                            agentPayoutReportVO.setWireChargeVO(wireChargeVO);
                            agentPayoutReportVO.setSettlementDateVO(settlementDateVO);

                            request.setAttribute("agentPayoutReportVO",agentPayoutReportVO);
                            RequestDispatcher rd = request.getRequestDispatcher("/agentPayoutReport.jsp?ctoken="+user.getCSRFToken());
                            rd.forward(request,response);

                        }
                        else
                        {
                            logger.debug("No Charges Mapped  For member "+sMemberId +" On Account Id=="+sAccountId);
                            error.add("No Charges Mapped  For member "+sMemberId +" On Account Id=="+sAccountId);
                            request.setAttribute("errorList",error);
                            RequestDispatcher rd = request.getRequestDispatcher("/agentPayoutReport.jsp?ctoken="+user.getCSRFToken());
                            rd.forward(request,response);
                        }
                    }
                    else
                    {
                        logger.debug("No Transactions Founds For Member "+sMemberId +" On Account Id=="+sAccountId);
                        error.add("No Transactions Founds For Member "+sMemberId +" On Account Id=="+sAccountId);
                        request.setAttribute("errorList",error);
                        RequestDispatcher rd = request.getRequestDispatcher("/agentPayoutReport.jsp?ctoken="+user.getCSRFToken());
                        rd.forward(request,response);

                    }
                }
                else if("consolidateReport".equals(agentReportType))
                {
                    //Step1:Get All The Members Of The Current Partner
                    Set<MerchantDetailsVO> merchantDetailsVOSet=merchantDAO.getAgentMembers(sAgentId);
                    if(merchantDetailsVOSet.size()>0)
                    {
                        HashMap<String,HashMap<String,List<ChargeDetailsVO>>> stringHashMapHashMap=new HashMap<String, HashMap<String,List<ChargeDetailsVO>>>();
                        Iterator merchantItr=merchantDetailsVOSet.iterator();
                        while (merchantItr.hasNext())
                        {
                            merchantDetailsVO=(MerchantDetailsVO)merchantItr.next();
                            sMemberId=merchantDetailsVO.getMemberId();

                            HashMap<String,List<ChargeDetailsVO>> stringListHashMap=new HashMap<String, List<ChargeDetailsVO>>();

                            List<TerminalVO> terminalVOs=terminalManager.getTerminalsByMerchantId(sMemberId);
                            for(TerminalVO terminalVO:terminalVOs)
                            {
                                List<ChargeDetailsVO> chargeDetailsVOs=new ArrayList<ChargeDetailsVO>();
                                TransactionSummaryVO summaryVO=null;

                                sAccountId=terminalVO.getAccountId();
                                sTableName=accountUtil.getTableNameFromAccountId(sAccountId);

                                if("transaction_icicicredit".equals(sTableName))
                                {
                                    logger.debug(sTableName);
                                    continue;
                                }

                                String memberFirstTransDate=payoutDAO.getMemberFirstTransactionDateOnAccountId(sMemberId,sAccountId);
                                if(memberFirstTransDate==null)
                                {
                                    logger.debug("No Transactions Founds For Member "+sMemberId +" On Account Id=="+sAccountId);
                                    continue;
                                }

                                summaryVO=payoutDAO.getAuthFailCountAmountByDtstamp(null, terminalVO, sTableName);
                                authfailedTransCount=summaryVO.getCountOfAuthfailed();
                                totalAuthfailedTransAmount=summaryVO.getAuthfailedAmount();

                                //Getting Settled Count & Settled Amount
                                summaryVO=payoutDAO.getSettledCountAmountByDtstamp(null,terminalVO,sTableName);
                                settledTransCount=summaryVO.getCountOfSettled();
                                totalSettledTransAmount=summaryVO.getSettledAmount();

                                //Getting Reversed Count & Reversed Amount
                                summaryVO=payoutDAO.getReversalCountAmountByTimestamp(null,terminalVO,sTableName);
                                reversedTransCount=summaryVO.getCountOfReversed();
                                totalReversedTransAmount=summaryVO.getReversedAmount();

                                //Getting Chargeback Count & Chargeback Amount
                                summaryVO=payoutDAO.getChargebackCountAmountByTimestamp(null,terminalVO,sTableName);
                                chargebackTransCount=summaryVO.getCountOfChargeback();
                                totalChargebackTransAmount=summaryVO.getChargebackAmount();

                                String currentDate=Functions.convertDateDBFormat(Calendar.getInstance().getTime());
                                ChargeManager chargeManager=new ChargeManager();

                                TransactionSummaryVO summaryVOFinal=new TransactionSummaryVO();

                                summaryVOFinal.setCountOfAuthfailed(authfailedTransCount);
                                summaryVOFinal.setAuthfailedAmount(totalAuthfailedTransAmount);

                                summaryVOFinal.setCountOfSettled(settledTransCount);
                                summaryVOFinal.setSettledAmount(totalSettledTransAmount);

                                summaryVOFinal.setCountOfReversed(reversedTransCount);
                                summaryVOFinal.setReversedAmount(totalReversedTransAmount);

                                summaryVOFinal.setCountOfChargeback(chargebackTransCount);
                                summaryVOFinal.setChargebackAmount(totalChargebackTransAmount);


                                List<ChargeVO> chargeVOs=accountDAO.getChargesAsPerTerminal(terminalVO);
                                ChargeDetailsVO chargeDetailsVO=null;
                                for(ChargeVO chargeVO:chargeVOs)
                                {
                                    logger.debug("ChargeName======="+chargeVO.getChargename());

                                    if(Charge_keyword.TotalReserveGenerated.toString().equals(chargeVO.getKeyword()) || Charge_keyword.TotalReserveRefunded.toString().equals(chargeVO.getKeyword()) || Charge_keyword.CalculatedReserveRefund.toString().equals(chargeVO.getKeyword()) || Charge_keyword.Wire.toString().equals(chargeVO.getKeyword()) || Charge_keyword.GrossBalanceAmount.toString().equals(chargeVO.getKeyword()))
                                    {
                                        logger.debug("Skipping "+chargeVO.getChargename()+" Form Agent Summary Charge Payout");
                                        continue;
                                    }
                                    else
                                    {
                                        //Get Agent Charge Version Rate
                                        String chargeVersionRate=chargeManager.getAgentChargeVersionRate(chargeVO.getMappingid(),currentDate);
                                        if(chargeVersionRate!=null)
                                        {
                                            chargeVO.setAgentChargeValue(chargeVersionRate);
                                        }

                                        chargeDetailsVO=payoutManager.calculateAgentChargeValue(chargeVO,terminalVO,sTableName,summaryVOFinal,memberFirstTransDate);
                                        totalChargesAmount=totalChargesAmount+chargeDetailsVO.getTotal();
                                        logger.debug("=,"+chargeDetailsVO.getTotal());
                                        chargeDetailsVOs.add(chargeDetailsVO);
                                    }
                                }
                                agentTotalFundedAmount=agentTotalFundedAmount+totalChargesAmount;
                                stringListHashMap.put(terminalVO.toString(),chargeDetailsVOs);
                            }
                            stringHashMapHashMap.put(sMemberId,stringListHashMap);
                        }

                        AgentPayoutReportVO agentPayoutReportVO=new AgentPayoutReportVO();

                        agentPayoutReportVO.setAgentTotalChargesAmount(totalChargesAmount);
                        agentPayoutReportVO.setAgentTotalFundedAmount(agentTotalFundedAmount);
                        agentPayoutReportVO.setAgentDetailsVO(agentDetailsVO);
                        agentPayoutReportVO.setStringHashMapHashMap(stringHashMapHashMap);
                        agentPayoutReportVO.setReportingDate(Functions.convertDateDBFormat(Calendar.getInstance().getTime()));

                        request.setAttribute("agentPayoutReportVO",agentPayoutReportVO);
                        RequestDispatcher rd = request.getRequestDispatcher("/agentConsolidatePayoutSummary.jsp?ctoken="+user.getCSRFToken());
                        rd.forward(request,response);

                    }
                    else
                    {
                        logger.debug("Agent not configured with any member");
                        error.add("Agent not configured with any member");
                        request.setAttribute("errorList",error);
                        RequestDispatcher rd = request.getRequestDispatcher("/agentPayoutReport.jsp?ctoken="+user.getCSRFToken());
                        rd.forward(request,response);
                        return;
                    }
                }
            }

        }
        catch (Exception e)
        {
            logger.error("exception",e);
        }
        finally
        {

        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            doPost(request,response);
        }
        catch (Exception e)
        {
            logger.error(e);
        }
    }

    public String convert2Decimal(String strAmount)
    {

        BigDecimal amount = new BigDecimal(strAmount);
        amount = amount.setScale(2, BigDecimal.ROUND_DOWN); //as amount value in database was round down by mysql while inserting in php page
        return amount.toString();

    }

    public List<String> validateMandatoryParaAgentPayout(Hashtable<String, String> otherDetail)
    {
        InputValidator inputValidator = new InputValidator();
        List<String> error = new ArrayList<String>();
        Hashtable<InputFields, String> hashTable =new Hashtable<InputFields, String>();
        hashTable.put(InputFields.AGENT_ID, (String) otherDetail.get("sAgentId"));
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(hashTable,errorList,false);
        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :hashTable.keySet())
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error.add(errorList.getError(inputFields.toString()).getMessage());
                }
            }
        }
        return error;

    }
    public List<String> validateOptionalParaAgentPayout(Hashtable<String, String> otherDetail)
    {
        InputValidator inputValidator = new InputValidator();
        List<String> error = new ArrayList<String>();
        Hashtable<InputFields, String> hashTable =new Hashtable<InputFields, String>();

        hashTable.put(InputFields.ACCOUNTID_CAPS,(String) otherDetail.get("accountid"));
        //System.out.println("accountid" + otherDetail.get("sAccountId"));
        hashTable.put(InputFields.TOID, (String) otherDetail.get("sMemberId"));
        hashTable.put(InputFields.CARDTYPE,(String) otherDetail.get("sCardTypeID"));
        hashTable.put(InputFields.PAYMENTID, (String) otherDetail.get("sPayModeId"));
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(hashTable,errorList,true);
        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :hashTable.keySet())
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error.add(errorList.getError(inputFields.toString()).getMessage());
                }
            }
        }
        return error;

    }







}
