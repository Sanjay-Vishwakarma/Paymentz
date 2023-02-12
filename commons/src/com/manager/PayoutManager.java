package com.manager;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.logicboxes.util.ApplicationProperties;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;
import com.manager.dao.*;
import com.manager.enums.*;
import com.manager.utils.AccountUtil;
import com.manager.vo.*;
import com.manager.vo.merchantmonitoring.DateVO;
import com.manager.vo.payoutVOs.*;
import com.payment.Enum.EU_Country;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.Mail.MailService;
import com.payment.PZTransactionStatus;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.payout.PayoutReportUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 9/2/14
 * Time: 3:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class PayoutManager
{
    private final static String AGENT_PAYOUT_REPORT_FILE_PATH = ApplicationProperties.getProperty("AGENT_PAYOUT_REPORT_FILE_PATH");
    private final static String PARTNER_PAYOUT_REPORT_FILE_PATH = ApplicationProperties.getProperty("PARTNER_PAYOUT_REPORT_FILE_PATH");
    private final static String SETTLEMENT_FILE_PATH = ApplicationProperties.getProperty("SETTLEMENT_FILE_PATH");
    private final static String PAYOUT_REPORT_FILE_PATH = ApplicationProperties.getProperty("PAYOUT_REPORT_FILE_PATH");
    private final static String PARTNER_LOGO_PATH = ApplicationProperties.getProperty("PARTNER_LOGO_PATH");
    PayoutDAO payoutDAO=new PayoutDAO();
    MerchantDAO merchantDAO=new MerchantDAO();
    AccountUtil accountUtil=new AccountUtil();
    Functions functions=new Functions();
    Logger logger = new Logger(PayoutManager.class.getName());

    public static List<ChargeVO> loadchargename()
    {
        return PayoutDAO.loadchargename();
    }

    public List<String> agentCommissionCron(String bankWireId, HashMap<String,List<TerminalVO>> agentTerminalHash, List<TerminalVO> pendingList)throws Exception
    {
        String cyclememberlist = null;
        SettlementDateVO settlementDateVO = null;
        List<String> stringList = new ArrayList<String>();
        List<TerminalVO> successList=new ArrayList<>();

        String isSettlementCronExecuted = "Y";
        String isAgentCommissionCronExecuted = "N";
        String isPayoutCronExcuted="Y";

        BankWireManagerVO bankWireManagerVO = payoutDAO.getBankWireListForAgentCommissionCron(bankWireId, isSettlementCronExecuted, isAgentCommissionCronExecuted,isPayoutCronExcuted);
        if (bankWireManagerVO == null)
        {
            cyclememberlist = "0:0:0:0:Failed:No New Wire To Execute Agent Commission Cron";
            stringList.add(cyclememberlist);
            logger.debug(cyclememberlist);
            return stringList;
        }

        TransactionManager transactionManager = new TransactionManager();
        ChargeManager chargeManager = new ChargeManager();
        PartnerDAO partnerDAO = new PartnerDAO();
        AgentDAO agentDAO = new AgentDAO();
        String accountId = bankWireManagerVO.getAccountId();
        MerchantDetailsVO merchantDetailsVO = null;
        PartnerDetailsVO partnerDetailsVO = null;
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        DateVO dateVO = new DateVO();
        dateVO.setStartDate(bankWireManagerVO.getServer_start_date());
        dateVO.setEndDate(bankWireManagerVO.getServer_end_date());

        Set<String> key=agentTerminalHash.keySet();

        for(String agentId:key)
        {
            List<TerminalVO> terminalVOList = agentTerminalHash.get(agentId);
            AgentDetailsVO agentDetailsVO=agentDAO.getAgentDetails(agentId);
            for (TerminalVO terminalVO1 : terminalVOList)
            {
                String memberId = terminalVO1.getMemberId();
                String terminalId = terminalVO1.getTerminalId();
                boolean pendingTransaction = transactionManager.checkPendingTransactionOfMerchant(gatewayAccount, memberId, terminalId, dateVO);
                if (pendingTransaction)
                {
                    cyclememberlist = bankWireManagerVO.getBankwiremanagerId() + ":" + memberId + ":" + accountId + ":"+ agentId + ":"+terminalId+":Failed:Transaction status need to be corrected";
                    stringList.add(cyclememberlist);
                    logger.debug(cyclememberlist);
                    continue;
                }
                String tableName = accountUtil.getTableNameSettlement(accountId);
                TerminalManager terminalManager = new TerminalManager();
                terminalVO1 = terminalManager.getTerminalsByMemberAccountIdForPayoutReportRequest(memberId, accountId, terminalId);
                String memberFirstTransactionDateOnTerminal = payoutDAO.getMemberFirstTransactionDateOnTerminal(terminalVO1);
                merchantDetailsVO = merchantDAO.getMemberDetails(memberId);
                partnerDetailsVO = partnerDAO.getPartnerDetails(merchantDetailsVO.getPartnerId());
                if (functions.isValueNull(memberFirstTransactionDateOnTerminal))
                {
                    List<AgentCommissionVO> agentCommissionVOs = chargeManager.getAgentCommissionOnTerminal(agentId, memberId, terminalVO1.getTerminalId());
                    if (agentCommissionVOs != null && agentCommissionVOs.size() > 0)
                    {
                        settlementDateVO=new SettlementDateVO();
                        /*settlementDateVO = payoutDAO.getAgentCommissionReportStartDate(terminalVO1);
                        if (settlementDateVO == null)
                        {
                            settlementDateVO = new SettlementDateVO();
                            settlementDateVO.setSettlementStartDate(targetFormat.format(targetFormat.parse(memberFirstTransactionDateOnTerminal)));
                            settlementDateVO.setDeclinedStartDate(targetFormat.format(targetFormat.parse(memberFirstTransactionDateOnTerminal)));
                            settlementDateVO.setReversedStartDate(targetFormat.format(targetFormat.parse(memberFirstTransactionDateOnTerminal)));
                            settlementDateVO.setChargebackStartDate(targetFormat.format(targetFormat.parse(memberFirstTransactionDateOnTerminal)));
                        }*/
                        settlementDateVO.setSettlementStartDate(targetFormat.format(targetFormat.parse(bankWireManagerVO.getServer_start_date())));

                        if(functions.isValueNull(bankWireManagerVO.getDeclinedcoveredStartdate()))
                        {
                            settlementDateVO.setDeclinedStartDate(targetFormat.format(targetFormat.parse(bankWireManagerVO.getDeclinedcoveredStartdate())));
                        }
                        else{
                            settlementDateVO.setDeclinedStartDate(targetFormat.format(targetFormat.parse(bankWireManagerVO.getServer_start_date())));
                        }
                        if(functions.isValueNull(bankWireManagerVO.getReversedCoveredStartdate()))
                        {
                            settlementDateVO.setReversedStartDate(targetFormat.format(targetFormat.parse(bankWireManagerVO.getReversedCoveredStartdate())));
                        }
                        else{
                            settlementDateVO.setReversedStartDate(targetFormat.format(targetFormat.parse(bankWireManagerVO.getServer_start_date())));
                        }
                        if(functions.isValueNull(bankWireManagerVO.getChargebackcoveredStartdate()))
                        {
                            settlementDateVO.setChargebackStartDate(targetFormat.format(targetFormat.parse(bankWireManagerVO.getChargebackcoveredStartdate())));
                        }
                        else{
                            settlementDateVO.setChargebackStartDate(targetFormat.format(targetFormat.parse(bankWireManagerVO.getServer_start_date())));
                        }

                        settlementDateVO.setSettlementcycleNumber(bankWireManagerVO.getBankwiremanagerId());
                        settlementDateVO.setSettlementEndDate(targetFormat.format(targetFormat.parse(bankWireManagerVO.getServer_end_date())));
                        settlementDateVO.setDeclinedEndDate(targetFormat.format(targetFormat.parse(bankWireManagerVO.getDeclinedcoveredupto())));
                        settlementDateVO.setReversedEndDate(targetFormat.format(targetFormat.parse(bankWireManagerVO.getReversedCoveredUpto())));
                        settlementDateVO.setChargebackEndDate(targetFormat.format(targetFormat.parse(bankWireManagerVO.getChargebackcoveredupto())));
                        settlementDateVO.setSettlementcycleNumber(bankWireManagerVO.getBankwiremanagerId());

                        TransactionSummaryVO transactionSummaryVO = getTotalSuccessCountAmountByTerminalNew(terminalVO1, settlementDateVO, tableName);

                        AgentCommissionReportVO agentCommissionReportVO = new AgentCommissionReportVO();

                        agentCommissionReportVO.setMerchantDetailsVO(merchantDetailsVO);
                        agentCommissionReportVO.setPartnerDetailsVO(partnerDetailsVO);
                        agentCommissionReportVO.setAgentCommissionVOList(agentCommissionVOs);
                        agentCommissionReportVO.setSettlementDateVO(settlementDateVO);
                        agentCommissionReportVO.setTransactionSummaryVO(transactionSummaryVO);
                        agentCommissionReportVO.setTerminalVO(terminalVO1);
                        agentCommissionReportVO.setAgentDetailsVO(agentDetailsVO);

                        String status = "";
                        String statusMsg = "";
                        status = generateWeeklyAgentCommissionReportBasedOnTerminal(agentCommissionReportVO);
                        if ("success".equals(status))
                        {
                            successList.add(terminalVO1);
                            statusMsg = "Agent Commission Report Created Successfully";
                            logger.error("pandingList----"+pendingList.size());
                            logger.error("successList----"+successList.size());
                            if (pendingList.size()==successList.size())
                            {
                                boolean b = payoutDAO.updateIsAgentCommCronExecutedFlag(bankWireManagerVO.getAccountId(), bankWireManagerVO.getBankwiremanagerId());
                                if (b)
                                {
                                    logger.debug("Commission cron executed successfully for cycle " + bankWireManagerVO.getBankwiremanagerId());
                                }
                            }
                        }
                        else
                        {
                            status = "Agent Commission Report Creation Failed";
                        }
                        cyclememberlist = bankWireManagerVO.getBankwiremanagerId() + ":" + memberId + ":" + accountId + ":"+ agentId + ":"+ terminalVO1.getTerminalId() + ":" + status + ":" + statusMsg;
                        stringList.add(cyclememberlist);
                        continue;
                    }
                    else
                    {
                        cyclememberlist = bankWireManagerVO.getBankwiremanagerId() + ":" + memberId + ":" + accountId + ":"+ agentId + ":"+ terminalVO1.getTerminalId() + ":Failed:No Charges Found On Terminal";
                        stringList.add(cyclememberlist);
                        logger.debug(stringList);
                        continue;
                    }
                }
                else
                {
                    cyclememberlist = bankWireManagerVO.getBankwiremanagerId() + ":" + memberId + ":" + accountId + ":" +":"+ agentId + ":"+ terminalVO1.getTerminalId() + ":Failed:No Transaction Found";
                    stringList.add(cyclememberlist);
                    logger.debug(cyclememberlist);
                    continue;
                }
            }
        }
        return stringList;
    }

    public List<String> partnerCommissionCron()throws Exception
    {
        String memberId=null;
        String accountId=null;
        String cyclememberlist=null;

        SettlementDateVO settlementDateVO=null;
        List<String> stringList=new ArrayList<String>();

        String isSettlementCronExecuted="Y";
        String isPartnerCommissionExecuted="N";

        List<BankWireManagerVO> bankWireManagerVOs=payoutDAO.getBankWireListForPartnerCommissionCron(isSettlementCronExecuted, isPartnerCommissionExecuted);
        if(bankWireManagerVOs == null || bankWireManagerVOs.isEmpty())
        {
            cyclememberlist="0:0:0:0:Failed:No New Bank Wire To Execute Partner Commission";
            stringList.add(cyclememberlist);
            logger.debug(cyclememberlist);
            return stringList;
        }

        TransactionManager transactionManager=new TransactionManager();
        ChargeManager chargeManager=new ChargeManager();
        TerminalManager terminalManager=new TerminalManager();
        PartnerDAO  partnerDAO=new PartnerDAO();
        MerchantDetailsVO merchantDetailsVO=null;
        PartnerDetailsVO partnerDetailsVO=null;
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(BankWireManagerVO bankWireManagerVO:bankWireManagerVOs)
        {
            accountId=bankWireManagerVO.getAccountId();

            GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
            DateVO dateVO=new DateVO();
            dateVO.setStartDate(bankWireManagerVO.getServer_start_date());
            dateVO.setEndDate(bankWireManagerVO.getServer_end_date());

            boolean pendingTransaction=transactionManager.checkPendingTransaction(gatewayAccount,dateVO);
            if(pendingTransaction){
                cyclememberlist=bankWireManagerVO.getBankwiremanagerId()+":-:"+accountId+":-:Failed:Transaction status need to be corrected";
                stringList.add(cyclememberlist);
                logger.debug(cyclememberlist);
                return stringList;
            }

            Set<String> memberSet=getMembersByAccountId(accountId);
            if(memberSet != null && memberSet.size()>0)
            {
                String tableName=accountUtil.getTableNameSettlement(accountId);
                Iterator itr=memberSet.iterator();
                while (itr.hasNext())
                {
                    memberId=(String)itr.next();
                    merchantDetailsVO=merchantDAO.getMemberDetails(memberId);
                    if(merchantDetailsVO!=null)
                    {
                        partnerDetailsVO = partnerDAO.getPartnerDetails(merchantDetailsVO.getPartnerId());
                        List<TerminalVO> terminalVOList = terminalManager.getTerminalsByMemberAccountIdForPayoutReport(memberId, accountId);
                        if (terminalVOList != null && terminalVOList.size() > 0)
                        {
                            for (TerminalVO terminalVO : terminalVOList)
                            {
                                String memberFirstTransactionDateOnTerminal = payoutDAO.getMemberFirstTransactionDateOnTerminal(terminalVO);
                                if (functions.isValueNull(memberFirstTransactionDateOnTerminal))
                                {
                                    List<PartnerCommissionVO> partnerCommissionVOs = chargeManager.getPartnerCommissionOnTerminal(merchantDetailsVO.getPartnerId(), memberId, terminalVO.getTerminalId());
                                    if (partnerCommissionVOs != null && partnerCommissionVOs.size() > 0)
                                    {
                                        settlementDateVO = payoutDAO.getPartnerCommissionReportStartDate(terminalVO);
                                        if (settlementDateVO == null)
                                        {
                                            settlementDateVO = new SettlementDateVO();
                                            settlementDateVO.setSettlementStartDate(targetFormat.format(targetFormat.parse(memberFirstTransactionDateOnTerminal)));
                                            settlementDateVO.setDeclinedStartDate(targetFormat.format(targetFormat.parse(memberFirstTransactionDateOnTerminal)));
                                            settlementDateVO.setReversedStartDate(targetFormat.format(targetFormat.parse(memberFirstTransactionDateOnTerminal)));
                                            settlementDateVO.setChargebackStartDate(targetFormat.format(targetFormat.parse(memberFirstTransactionDateOnTerminal)));

                                        }

                                        settlementDateVO.setSettlementcycleNumber(bankWireManagerVO.getBankwiremanagerId());
                                        settlementDateVO.setSettlementEndDate(targetFormat.format(targetFormat.parse(bankWireManagerVO.getServer_end_date())));
                                        settlementDateVO.setDeclinedEndDate(targetFormat.format(targetFormat.parse(bankWireManagerVO.getDeclinedcoveredupto())));
                                        settlementDateVO.setReversedEndDate(targetFormat.format(targetFormat.parse(bankWireManagerVO.getReversedCoveredUpto())));
                                        settlementDateVO.setChargebackEndDate(targetFormat.format(targetFormat.parse(bankWireManagerVO.getChargebackcoveredupto())));

                                        TransactionSummaryVO transactionSummaryVO = getTotalSuccessCountAmountByTerminalNew(terminalVO, settlementDateVO, tableName);

                                        PartnerCommissionReportVO partnerCommissionReportVO = new PartnerCommissionReportVO();

                                        partnerCommissionReportVO.setMerchantDetailsVO(merchantDetailsVO);
                                        partnerCommissionReportVO.setPartnerCommissionVOList(partnerCommissionVOs);
                                        partnerCommissionReportVO.setSettlementDateVO(settlementDateVO);
                                        partnerCommissionReportVO.setTransactionSummaryVO(transactionSummaryVO);
                                        partnerCommissionReportVO.setTerminalVO(terminalVO);
                                        partnerCommissionReportVO.setPartnerDetailsVO(partnerDetailsVO);

                                        String status = "";
                                        String statusMsg = "";
                                        status = generateWeeklyPartnerCommissionReportBasedOnTerminal(partnerCommissionReportVO);
                                        if ("success".equals(status))
                                        {
                                            statusMsg = "Partner Commission Report Created Successfully";
                                            boolean b = payoutDAO.updateIsPartnerCommCronExecutedFlag(bankWireManagerVO.getAccountId(), bankWireManagerVO.getBankwiremanagerId());
                                            if (b)
                                            {
                                                logger.debug("Bank Wire Updated Successfully.");
                                            }

                                        }
                                        else
                                        {
                                            status = "Partner Commission Report Creation Failed";
                                        }
                                        cyclememberlist = bankWireManagerVO.getBankwiremanagerId() + ":" + memberId + ":" + accountId + ":" + terminalVO.getTerminalId() + ":" + status + ":" + statusMsg;
                                        stringList.add(cyclememberlist);
                                    }
                                    else
                                    {
                                        cyclememberlist = bankWireManagerVO.getBankwiremanagerId() + ":" + memberId + ":" + accountId + ":" + terminalVO.getTerminalId() + ":Failed:No Charges Found On Terminal";
                                        stringList.add(cyclememberlist);
                                        logger.debug(stringList);
                                        continue;
                                    }
                                }
                                else
                                {
                                    cyclememberlist = bankWireManagerVO.getBankwiremanagerId() + ":" + memberId + ":" + accountId + ":" + terminalVO.getTerminalId() + ":Failed:Transactions Not Found On Terminal";
                                    stringList.add(cyclememberlist);
                                    logger.debug(cyclememberlist);
                                    continue;
                                }
                            }
                        }
                        else
                        {
                            cyclememberlist = bankWireManagerVO.getBankwiremanagerId() + ":" + memberId + ":" + accountId + ":0:Failed:Terminals Not Founds On Account";
                            stringList.add(cyclememberlist);
                            logger.debug(cyclememberlist);
                            continue;
                        }
                    }
                    else
                    {
                        cyclememberlist=bankWireManagerVO.getBankwiremanagerId()+":0:"+accountId+":0:Failed:Members Not Founds On Account";
                        stringList.add(cyclememberlist);
                        logger.debug(stringList);
                        continue;
                    }
                }
            }
            else
            {
                cyclememberlist=bankWireManagerVO.getBankwiremanagerId()+":0:"+accountId+":0:Failed:Members Not Founds On Account";
                stringList.add(cyclememberlist);
                logger.debug(stringList);
                continue;
            }
        }
        return stringList;
    }

    public List<String> partnerCommissionCronPerWireId(String wireId)throws Exception
    {
        String memberId=null;
        String accountId=null;
        String cyclememberlist=null;

        SettlementDateVO settlementDateVO=null;
        List<String> stringList=new ArrayList<String>();

        String isSettlementCronExecuted="Y";
        String isPartnerCommissionExecuted="N";

        BankWireManagerVO bankWireManagerVO=payoutDAO.getBankWireDetailsForPartnerCommission(isSettlementCronExecuted, isPartnerCommissionExecuted, wireId);
        if(bankWireManagerVO == null)
        {
            cyclememberlist="0:0:0:0:Failed:No New Bank Wire To Execute Partner Commission";
            stringList.add(cyclememberlist);
            logger.debug(cyclememberlist);
            return stringList;
        }

        TransactionManager transactionManager=new TransactionManager();
        ChargeManager chargeManager=new ChargeManager();
        TerminalManager terminalManager=new TerminalManager();
        PartnerDAO  partnerDAO=new PartnerDAO();
        MerchantDetailsVO merchantDetailsVO=null;
        PartnerDetailsVO partnerDetailsVO=null;
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        accountId=bankWireManagerVO.getAccountId();

        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        DateVO dateVO=new DateVO();
        dateVO.setStartDate(bankWireManagerVO.getServer_start_date());
        dateVO.setEndDate(bankWireManagerVO.getServer_end_date());

        boolean pendingTransaction=transactionManager.checkPendingTransaction(gatewayAccount,dateVO);
        if(pendingTransaction){
            cyclememberlist=bankWireManagerVO.getBankwiremanagerId()+":-:"+accountId+":-:Failed:Transaction status need to be corrected";
            stringList.add(cyclememberlist);
            logger.debug(cyclememberlist);
            return stringList;
        }

        Set<String> memberSet=getMembersByAccountId(accountId);
        if(memberSet != null && memberSet.size()>0)
        {
            String tableName=accountUtil.getTableNameSettlement(accountId);
            Iterator itr=memberSet.iterator();
            while (itr.hasNext())
            {
                memberId=(String)itr.next();
                merchantDetailsVO=merchantDAO.getMemberDetails(memberId);
                if(merchantDetailsVO!=null)
                {
                    partnerDetailsVO = partnerDAO.getPartnerDetails(merchantDetailsVO.getPartnerId());
                    List<TerminalVO> terminalVOList = terminalManager.getTerminalsByMemberAccountIdForPayoutReport(memberId, accountId);
                    if (terminalVOList != null && terminalVOList.size() > 0)
                    {
                        for (TerminalVO terminalVO : terminalVOList)
                        {
                            String memberFirstTransactionDateOnTerminal = payoutDAO.getMemberFirstTransactionDateOnTerminal(terminalVO);
                            if (functions.isValueNull(memberFirstTransactionDateOnTerminal))
                            {
                                List<PartnerCommissionVO> partnerCommissionVOs = chargeManager.getPartnerCommissionOnTerminal(merchantDetailsVO.getPartnerId(), memberId, terminalVO.getTerminalId());
                                if (partnerCommissionVOs != null && partnerCommissionVOs.size() > 0)
                                {
                                    settlementDateVO = payoutDAO.getPartnerCommissionReportStartDate(terminalVO);
                                    if (settlementDateVO == null)
                                    {
                                        settlementDateVO = new SettlementDateVO();
                                        settlementDateVO.setSettlementStartDate(targetFormat.format(targetFormat.parse(memberFirstTransactionDateOnTerminal)));
                                        settlementDateVO.setDeclinedStartDate(targetFormat.format(targetFormat.parse(memberFirstTransactionDateOnTerminal)));
                                        settlementDateVO.setReversedStartDate(targetFormat.format(targetFormat.parse(memberFirstTransactionDateOnTerminal)));
                                        settlementDateVO.setChargebackStartDate(targetFormat.format(targetFormat.parse(memberFirstTransactionDateOnTerminal)));

                                    }

                                    settlementDateVO.setSettlementcycleNumber(bankWireManagerVO.getBankwiremanagerId());
                                    settlementDateVO.setSettlementEndDate(targetFormat.format(targetFormat.parse(bankWireManagerVO.getServer_end_date())));
                                    settlementDateVO.setDeclinedEndDate(targetFormat.format(targetFormat.parse(bankWireManagerVO.getDeclinedcoveredupto())));
                                    settlementDateVO.setReversedEndDate(targetFormat.format(targetFormat.parse(bankWireManagerVO.getReversedCoveredUpto())));
                                    settlementDateVO.setChargebackEndDate(targetFormat.format(targetFormat.parse(bankWireManagerVO.getChargebackcoveredupto())));

                                    TransactionSummaryVO transactionSummaryVO = getTotalSuccessCountAmountByTerminalNew(terminalVO, settlementDateVO, tableName);

                                    PartnerCommissionReportVO partnerCommissionReportVO = new PartnerCommissionReportVO();

                                    partnerCommissionReportVO.setMerchantDetailsVO(merchantDetailsVO);
                                    partnerCommissionReportVO.setPartnerCommissionVOList(partnerCommissionVOs);
                                    partnerCommissionReportVO.setSettlementDateVO(settlementDateVO);
                                    partnerCommissionReportVO.setTransactionSummaryVO(transactionSummaryVO);
                                    partnerCommissionReportVO.setTerminalVO(terminalVO);
                                    partnerCommissionReportVO.setPartnerDetailsVO(partnerDetailsVO);

                                    String status = "";
                                    String statusMsg = "";
                                    status = generateWeeklyPartnerCommissionReportBasedOnTerminal(partnerCommissionReportVO);
                                    if ("success".equals(status))
                                    {
                                        statusMsg = "Partner Commission Report Created Successfully";
                                        boolean b = payoutDAO.updateIsPartnerCommCronExecutedFlag(bankWireManagerVO.getAccountId(), bankWireManagerVO.getBankwiremanagerId());
                                        if (b)
                                        {
                                            logger.debug("Bank Wire Updated Successfully.");
                                        }

                                    }
                                    else
                                    {
                                        status = "Partner Commission Report Creation Failed";
                                    }
                                    cyclememberlist = bankWireManagerVO.getBankwiremanagerId() + ":" + memberId + ":" + accountId + ":" + terminalVO.getTerminalId() + ":" + status + ":" + statusMsg;
                                    stringList.add(cyclememberlist);
                                }
                                else
                                {
                                    cyclememberlist = bankWireManagerVO.getBankwiremanagerId() + ":" + memberId + ":" + accountId + ":" + terminalVO.getTerminalId() + ":Failed:No Charges Found On Terminal";
                                    stringList.add(cyclememberlist);
                                    logger.debug(stringList);
                                    continue;
                                }
                            }
                            else
                            {
                                cyclememberlist = bankWireManagerVO.getBankwiremanagerId() + ":" + memberId + ":" + accountId + ":" + terminalVO.getTerminalId() + ":Failed:Transactions Not Found On Terminal";
                                stringList.add(cyclememberlist);
                                logger.debug(cyclememberlist);
                                continue;
                            }
                        }
                    }
                    else
                    {
                        cyclememberlist = bankWireManagerVO.getBankwiremanagerId() + ":" + memberId + ":" + accountId + ":0:Failed:Terminals Not Founds On Account";
                        stringList.add(cyclememberlist);
                        logger.debug(cyclememberlist);
                        continue;
                    }
                }
                else
                {
                    cyclememberlist=bankWireManagerVO.getBankwiremanagerId()+":0:"+accountId+":0:Failed:Members Not Founds On Account";
                    stringList.add(cyclememberlist);
                    logger.debug(stringList);
                    continue;
                }
            }
        }
        else
        {
            cyclememberlist=bankWireManagerVO.getBankwiremanagerId()+":0:"+accountId+":0:Failed:Members Not Founds On Account";
            stringList.add(cyclememberlist);
            logger.debug(stringList);
        }
        return stringList;
    }

    private String generateWeeklyAgentCommissionReportBasedOnTerminal(AgentCommissionReportVO agentCommissionReportVO)throws Exception
    {
        TerminalVO terminalVO= agentCommissionReportVO.getTerminalVO();
        MerchantDetailsVO merchantDetailsVO= agentCommissionReportVO.getMerchantDetailsVO();
        List<AgentCommissionVO> agentCommissionVOs= agentCommissionReportVO.getAgentCommissionVOList();
        PartnerDetailsVO partnerDetailsVO= agentCommissionReportVO.getPartnerDetailsVO();
        SettlementDateVO settlementDateVO= agentCommissionReportVO.getSettlementDateVO();
        TransactionSummaryVO transactionSummaryVO= agentCommissionReportVO.getTransactionSummaryVO();
        AgentDetailsVO agentDetailsVO=agentCommissionReportVO.getAgentDetailsVO();

        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String settlementStartDate=targetFormat.format(targetFormat.parse(settlementDateVO.getSettlementStartDate()));
        String settlementEndDate=targetFormat.format(targetFormat.parse(settlementDateVO.getSettlementEndDate()));

        settlementDateVO.setSettlementStartDate(settlementStartDate);
        settlementDateVO.setSettlementEndDate(settlementEndDate);

        String terminalId=terminalVO.getTerminalId();
        String memberId=terminalVO.getMemberId();
        String accountId=terminalVO.getAccountId();

        GatewayAccount account= GatewayAccountService.getGatewayAccount(accountId);
        GatewayType gatewayType = GatewayTypeService.getGatewayType(account.getPgTypeId());

        String currency=gatewayType.getCurrency();

        double totalCommissionAmount=0.00;
        double totalChargesAmount=0.00;
        double agentTotalFundedAmount=0.00;
        double previousBalance=payoutDAO.getAgentUnpaidBalanceAmountOnTerminal(terminalVO);
        double wireFee=0.00;

        HashMap<String,CommissionDetailsVO> commissionDetailsVOHashMap=new LinkedHashMap<String,CommissionDetailsVO>();
        CommissionDetailsVO  commissionDetailsVO=null;

        WireChargeVO wireChargeVO=null;
        for(AgentCommissionVO agentCommissionVO:agentCommissionVOs)
        {
            ChargeMasterVO chargeMasterVO=agentCommissionVO.getChargeMasterVO();
            logger.error("KeyWord:::::"+chargeMasterVO.getKeyword());
            if(chargeMasterVO.getCategory().equals(Charge_category.Others.toString()) && chargeMasterVO.getKeyword().equals(Charge_keyword.Setup.toString())&&chargeMasterVO.getSubKeyword().equals(Charge_subKeyword.Count.toString()))
            {

                commissionDetailsVO= getAgentCommissionFromMerchantSetupFee(agentCommissionVO, terminalVO, settlementDateVO);
            }
            else if(Charge_category.Others.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Wire.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeMasterVO.getSubKeyword()))
            {
                long vCntCounter=payoutDAO.getMerchantPaidWireCountForCommissionCalculation(terminalVO, settlementDateVO);//get the total count of paid wires from merchant_wiremanager between settlement date
                double vDblAmount=0.00;
                double vDblTotal=vCntCounter*agentCommissionVO.getCommissionValue();

                commissionDetailsVO=new CommissionDetailsVO();

                commissionDetailsVO.setChargeName(chargeMasterVO.getChargeName());
                commissionDetailsVO.setValueType(chargeMasterVO.getValueType());
                commissionDetailsVO.setChargeValue(String.valueOf(agentCommissionVO.getCommissionValue()));

                commissionDetailsVO.setCount(vCntCounter);
                commissionDetailsVO.setAmount(vDblAmount);
                commissionDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
            }
            else if (chargeMasterVO.getCategory().equals(Charge_category.Success.toString()) && chargeMasterVO.getKeyword().equals(Charge_keyword.NetFinalAmount.toString()) && chargeMasterVO.getSubKeyword().equals(Charge_subKeyword.Amount.toString()))
            {
                logger.error("inside NetFinalAmount:::::"+chargeMasterVO.getKeyword());
                double totalFundedToBank=payoutDAO.getMerchantTotalAmountFunded(terminalVO, settlementDateVO);//get the total count of paid wires from merchant_wiremanager between settlement date
                double vDblTotal=totalFundedToBank * Double.valueOf(agentCommissionVO.getCommissionValue()) / 100;
                commissionDetailsVO=new CommissionDetailsVO();
                commissionDetailsVO.setChargeName(chargeMasterVO.getChargeName());
                commissionDetailsVO.setValueType(chargeMasterVO.getValueType());
                commissionDetailsVO.setChargeValue(String.valueOf(agentCommissionVO.getCommissionValue()));

                commissionDetailsVO.setCount(0);
                commissionDetailsVO.setAmount(totalFundedToBank);
                commissionDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
            }
            else
            {
                commissionDetailsVO=calculateAgentCommissionValue(agentCommissionVO, transactionSummaryVO);
            }

            totalCommissionAmount=totalCommissionAmount+commissionDetailsVO.getTotal();
            commissionDetailsVOHashMap.put(commissionDetailsVO.getChargeName(),commissionDetailsVO);

        }

        ChargeManager chargeManager=new ChargeManager();
        HashMap<String,ChargeDetailsVO> chargeDetailsVOHashMap=new LinkedHashMap();
        ChargeDetailsVO  chargeDetailsVO=null;

        List<ChargeVO> chargeVOList=chargeManager.getTobeDebitedAgentCharges(terminalVO);
        //System.out.println("list of charges found====="+chargeVOList.size());
        for(ChargeVO chargeVO:chargeVOList)
        {
            //System.out.println(chargeVO.getChargename()+"========="+chargeVO.getAgentChargeValue());
            if(Charge_keyword.TotalReserveGenerated.toString().equals(chargeVO.getKeyword()) || Charge_keyword.TotalReserveRefunded.toString().equals(chargeVO.getKeyword()) || Charge_keyword.CalculatedReserveRefund.toString().equals(chargeVO.getKeyword()) || Charge_keyword.Wire.toString().equals(chargeVO.getKeyword()) || Charge_keyword.GrossBalanceAmount.toString().equals(chargeVO.getKeyword()) || Charge_keyword.Setup.toString().equals(chargeVO.getKeyword()) || Charge_keyword.VerifyOrder.toString().equals(chargeVO.getKeyword()) || Charge_keyword.RefundAlert.toString().equals(chargeVO.getKeyword()) || Charge_keyword.RetrivalRequest.toString().equals(chargeVO.getKeyword()) ||(Charge_keyword.Reversed.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword())))
            {
                //System.out.println("kipping "+chargeVO.getChargename());
                logger.debug("Skipping "+chargeVO.getChargename()+" Form Agent Summary Charge Payout");
                continue;
            }
            else
            {
                chargeDetailsVO=calculateAgentChargesValue(chargeVO, transactionSummaryVO);
                //System.out.println("Charge Name===="+chargeVO.getChargename()+"::::::"+chargeDetailsVO.getTotal());
                totalChargesAmount=totalChargesAmount+chargeDetailsVO.getTotal();
                chargeDetailsVOHashMap.put(chargeVO.getChargeid(),chargeDetailsVO);
            }
        }

        //System.out.println("totalChargesAmount===="+totalChargesAmount);
        agentTotalFundedAmount=agentTotalFundedAmount+totalCommissionAmount+previousBalance-totalChargesAmount;
        String reportFileName=generateAgentCommissionReport(commissionDetailsVOHashMap, partnerDetailsVO,agentDetailsVO,terminalVO, settlementDateVO, totalCommissionAmount, agentTotalFundedAmount, currency,previousBalance,wireChargeVO,chargeDetailsVOHashMap,totalChargesAmount);

        AgentWireVO agentWireVO=new AgentWireVO();

        agentWireVO.setSettlementStartDate(settlementDateVO.getSettlementStartDate());
        agentWireVO.setSettlementEndDate(settlementDateVO.getSettlementEndDate());
        agentWireVO.setAgentType("merchantaccount");
        agentWireVO.setAgentChargeAmount(totalCommissionAmount);
        agentWireVO.setAgentUnpaidAmount(0.00);
        agentWireVO.setAgentTotalFundedAmount(agentTotalFundedAmount);
        agentWireVO.setCurrency(currency);
        agentWireVO.setStatus("unpaid");
        agentWireVO.setSettlementReportFileName(reportFileName);
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
        agentWireVO.setSettlementCycleNo(settlementDateVO.getSettlementcycleNumber());
        String wireCreationStatus=createAgentWire(agentWireVO);
        return wireCreationStatus;
    }

    private String generateWeeklyPartnerCommissionReportBasedOnTerminal(PartnerCommissionReportVO partnerCommissionReportVO)throws Exception
    {
        TerminalVO terminalVO= partnerCommissionReportVO.getTerminalVO();
        MerchantDetailsVO merchantDetailsVO= partnerCommissionReportVO.getMerchantDetailsVO();
        List<PartnerCommissionVO> partnerCommissionVOs= partnerCommissionReportVO.getPartnerCommissionVOList();
        PartnerDetailsVO partnerDetailsVO= partnerCommissionReportVO.getPartnerDetailsVO();
        SettlementDateVO settlementDateVO= partnerCommissionReportVO.getSettlementDateVO();
        TransactionSummaryVO transactionSummaryVO= partnerCommissionReportVO.getTransactionSummaryVO();

        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String settlementStartDate=targetFormat.format(targetFormat.parse(settlementDateVO.getSettlementStartDate()));
        String settlementEndDate=targetFormat.format(targetFormat.parse(settlementDateVO.getSettlementEndDate()));

        settlementDateVO.setSettlementStartDate(settlementStartDate);
        settlementDateVO.setSettlementEndDate(settlementEndDate);

        String terminalId=terminalVO.getTerminalId();
        String memberId=terminalVO.getMemberId();
        String accountId=terminalVO.getAccountId();

        String resStatus="Failed";
        GatewayAccount account= GatewayAccountService.getGatewayAccount(accountId);
        GatewayType gatewayType = GatewayTypeService.getGatewayType(account.getPgTypeId());

        String currency=gatewayType.getCurrency();
        String status=null;

        long totalSuccessCount=0;

        double authfailedAmount=0.00;
        double settledAmount=0.00;
        double reversedAmount = 0.00;
        double chargebackAmount =0.00;
        double wireChargeAmount=0.00;

        double totalCommissionAmount=0.00;
        double totalChargesAmount=0.00;
        double partnerTotalFundedAmount=0.00;
        double previousBalance=payoutDAO.getPartnerUnpaidBalanceAmountOnTerminal(terminalVO);
        double wireFee=0.00;

        HashMap<String,CommissionDetailsVO> commissionDetailsVOHashMap=new LinkedHashMap<String,CommissionDetailsVO>();
        CommissionDetailsVO  commissionDetailsVO=null;

        WireChargeVO wireChargeVO=null;

        for(PartnerCommissionVO partnerCommissionVO:partnerCommissionVOs)
        {
            ChargeMasterVO chargeMasterVO=partnerCommissionVO.getChargeMasterVO();
            if(chargeMasterVO.getCategory().equals(Charge_category.Others.toString()) && chargeMasterVO.getKeyword().equals(Charge_keyword.Setup.toString())&&chargeMasterVO.getSubKeyword().equals(Charge_subKeyword.Count.toString()))
            {
                commissionDetailsVO= getPartnerCommissionFromMerchantSetupFee(partnerCommissionVO, terminalVO, settlementDateVO);
            }
            else if(Charge_category.Others.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Wire.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeMasterVO.getSubKeyword()))
            {
                long vCntCounter=payoutDAO.getMerchantPaidWireCountForCommissionCalculation(terminalVO, settlementDateVO);//get the total count of paid wires from merchant_wiremanager between settlement date
                double vDblAmount=0.00;
                double vDblTotal=vCntCounter*partnerCommissionVO.getCommissionValue();

                commissionDetailsVO=new CommissionDetailsVO();

                commissionDetailsVO.setChargeName(chargeMasterVO.getChargeName());
                commissionDetailsVO.setValueType(chargeMasterVO.getValueType());
                commissionDetailsVO.setChargeValue(String.valueOf(partnerCommissionVO.getCommissionValue()));

                commissionDetailsVO.setCount(vCntCounter);
                commissionDetailsVO.setAmount(vDblAmount);
                commissionDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
            }
            else
            {
                commissionDetailsVO=calculatePartnerCommissionValue(partnerCommissionVO, transactionSummaryVO);
            }

            totalCommissionAmount=totalCommissionAmount+commissionDetailsVO.getTotal();
            commissionDetailsVOHashMap.put(commissionDetailsVO.getChargeName(),commissionDetailsVO);
        }
        ChargeManager chargeManager=new ChargeManager();

        HashMap<String,ChargeDetailsVO> chargeDetailsVOHashMap=new LinkedHashMap();
        ChargeDetailsVO  chargeDetailsVO=null;

        List<ChargeVO> chargeVOList=chargeManager.getTobeDebitedPartnerCharges(terminalVO);
        for(ChargeVO chargeVO:chargeVOList)
        {
            //System.out.println(chargeVO.getChargename()+"========="+chargeVO.getPartnerChargeValue());
            if(Charge_keyword.TotalReserveGenerated.toString().equals(chargeVO.getKeyword()) || Charge_keyword.TotalReserveRefunded.toString().equals(chargeVO.getKeyword()) || Charge_keyword.CalculatedReserveRefund.toString().equals(chargeVO.getKeyword()) || Charge_keyword.Wire.toString().equals(chargeVO.getKeyword()) || Charge_keyword.GrossBalanceAmount.toString().equals(chargeVO.getKeyword()) || Charge_keyword.Setup.toString().equals(chargeVO.getKeyword()) || Charge_keyword.VerifyOrder.toString().equals(chargeVO.getKeyword()) || Charge_keyword.RefundAlert.toString().equals(chargeVO.getKeyword()) || Charge_keyword.RetrivalRequest.toString().equals(chargeVO.getKeyword()) ||(Charge_keyword.Reversed.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword())))
            {
                //System.out.println("skipping "+chargeVO.getChargename());
                logger.debug("Skipping "+chargeVO.getChargename()+" Form Agent Summary Charge Payout");
                continue;
            }
            else
            {
                chargeDetailsVO=calculatePartnerChargesValue(chargeVO, transactionSummaryVO);
                totalChargesAmount=totalChargesAmount+chargeDetailsVO.getTotal();
                chargeDetailsVOHashMap.put(chargeVO.getChargeid(),chargeDetailsVO);
            }
        }

        //System.out.println("chargeDetailsVOHashMap======"+chargeDetailsVOHashMap.size());
        partnerTotalFundedAmount=partnerTotalFundedAmount+totalCommissionAmount+previousBalance-totalChargesAmount;
        String reportFileName=generatePartnerCommissionReport(commissionDetailsVOHashMap,partnerDetailsVO,terminalVO,settlementDateVO,totalCommissionAmount,partnerTotalFundedAmount,currency,previousBalance,wireChargeVO,chargeDetailsVOHashMap,totalChargesAmount);
        PartnerWireVO partnerWireVO=new BankPartnerWireVO();

        partnerWireVO.setSettlementStartDate(settlementDateVO.getSettlementStartDate());
        partnerWireVO.setSettlementEndDate(settlementDateVO.getSettlementEndDate());
        partnerWireVO.setPartnerType("merchantaccount");
        partnerWireVO.setPartnerChargeAmount(totalCommissionAmount);
        partnerWireVO.setPartnerUnpaidAmount(0.00);
        partnerWireVO.setPartnerTotalFundedAmount(partnerTotalFundedAmount);
        partnerWireVO.setCurrency(currency);
        partnerWireVO.setStatus("unpaid");
        partnerWireVO.setSettlementReportFileName(reportFileName);
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
        String wireCreationStatus=createPartnerWire(partnerWireVO);

        return wireCreationStatus;
    }

    public String generateAgentCommissionReport(HashMap<String,CommissionDetailsVO> commissionDetailsVOHashMap,PartnerDetailsVO partnerDetailsVO,AgentDetailsVO agentDetailsVO,TerminalVO terminalVO,SettlementDateVO settlementDateVO,double totalCommissionAmount,double agentTotalFundedAmount,String currency,double previousBalanceAmount,WireChargeVO wireChargeVO,HashMap<String,ChargeDetailsVO> chargeDetailsVOHashMap,double totalChargesAmount)
    {
        PayoutReportUtils payoutReportUtils=new PayoutReportUtils();
        Document document = new Document(PageSize.A3, 50, 50, 50, 50);
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );

        String reportFileName=payoutReportUtils.getAgentReportFileName(agentDetailsVO.getAgentId(), terminalVO.getMemberId(),terminalVO.getTerminalId(), settlementDateVO.getSettlementEndDate(), settlementDateVO.getSettlementEndDate());

        try
        {

            reportFileName=reportFileName+".pdf";
            File filePath=new File(AGENT_PAYOUT_REPORT_FILE_PATH+reportFileName);

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            String reportingDate=targetFormat.format(new Date());
            if(!functions.isValueNull(partnerDetailsVO.getLogoName()))
            {
                partnerDetailsVO.setLogoName("logo.jpg");
            }

            Image partnerImageInstance = Image.getInstance(PARTNER_LOGO_PATH+partnerDetailsVO.getLogoName());
            partnerImageInstance.scaleAbsolute(150f, 150f);
            partnerImageInstance.scaleAbsoluteHeight(40f);


            Table table = new Table(6);
            table.setWidth(100);
            table.setBorderColor(new Color(0, 0,0));
            table.setPadding(1);

            Cell partnerNameCaptionCell=new Cell("Processor:"+partnerDetailsVO.getCompanyName());
            partnerNameCaptionCell.setColspan(2);
            partnerNameCaptionCell.setBackgroundColor(Color.white.brighter());

            Cell reportingDateCaptionCe11=new Cell("Reporting Date:"+reportingDate);
            reportingDateCaptionCe11.setColspan(2);
            reportingDateCaptionCe11.setBackgroundColor(Color.white.brighter());

            Cell partnerLogoCell=new Cell(partnerImageInstance);
            partnerLogoCell.setColspan(2);
            partnerLogoCell.setHorizontalAlignment(Element.ALIGN_CENTER);

            table.addCell(partnerNameCaptionCell);
            table.addCell(reportingDateCaptionCe11);
            table.addCell(partnerLogoCell);

            Cell agentDetails=new Cell("Agent Details");
            agentDetails.setColspan(6);
            agentDetails.setHorizontalAlignment(Element.ALIGN_CENTER);
            agentDetails.setVerticalAlignment(Element.ALIGN_CENTER);
            agentDetails.setBackgroundColor(Color.gray.brighter());

            table.addCell(agentDetails);

            Cell agentNameLabel=new Cell("Agent Id:");
            Cell agentNameValue=new Cell(agentDetailsVO.getAgentId());

            agentNameLabel.setColspan(4);
            agentNameValue.setColspan(2);

            table.addCell(agentNameLabel);
            table.addCell(agentNameValue);

            Cell memberIdLabel=new Cell("Member ID:");
            Cell memberIdValue=new Cell(terminalVO.getMemberId());

            memberIdLabel.setColspan(4);
            memberIdValue.setColspan(2);

            table.addCell(memberIdLabel);
            table.addCell(memberIdValue);

            Cell terminalIdLabel=new Cell("Terminal Id:");
            Cell terminalValue=new Cell(terminalVO.getTerminalId());

            terminalIdLabel.setColspan(4);
            terminalValue.setColspan(2);

            table.addCell(terminalIdLabel);
            table.addCell(terminalValue);

            Cell partnerCompanyNameLabel=new Cell("Agent Name:");
            Cell partnerCompanyValue=new Cell(agentDetailsVO.getAgentName());

            partnerCompanyNameLabel.setColspan(4);
            partnerCompanyValue.setColspan(2);

            table.addCell(partnerCompanyNameLabel);
            table.addCell(partnerCompanyValue);

            Cell contactPersonNameLabel=new Cell("Contact Person Name:");
            Cell contactPersonNameValue=new Cell(agentDetailsVO.getContactPerson());

            contactPersonNameLabel.setColspan(4);
            contactPersonNameValue.setColspan(2);

            table.addCell(contactPersonNameLabel);
            table.addCell(contactPersonNameValue);

            Cell payModeLabel=new Cell("Payment Mode:");
            Cell payModeValue=new Cell(terminalVO.getPaymentTypeName());

            payModeLabel.setColspan(4);
            payModeValue.setColspan(2);

            table.addCell(payModeLabel);
            table.addCell(payModeValue);

            Cell cardTypeLabel=new Cell("Card Type:");
            Cell cardTypeValue=new Cell(terminalVO.getCardType());

            cardTypeLabel.setColspan(4);
            cardTypeValue.setColspan(2);

            table.addCell(cardTypeLabel);
            table.addCell(cardTypeValue);

            Cell currencyLabel=new Cell("Currency:");
            Cell currencyValue=new Cell(currency);

            currencyLabel.setColspan(4);
            currencyValue.setColspan(2);

            table.addCell(currencyLabel);
            table.addCell(currencyValue);

            Cell settlementFromDateLabel=new Cell("Settlement From:");
            Cell settlementFromDateValue=new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getSettlementStartDate())));

            settlementFromDateLabel.setColspan(4);
            settlementFromDateValue.setColspan(2);

            table.addCell(settlementFromDateLabel);
            table.addCell(settlementFromDateValue);

            Cell settlementToDateLabel=new Cell("Settlement To:");
            Cell settlementToDateValue=new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getSettlementEndDate())));

            settlementToDateLabel.setColspan(4);
            settlementToDateValue.setColspan(2);

            table.addCell(settlementToDateLabel);
            table.addCell(settlementToDateValue);

            Cell agentCommissionDetailsLabel=new Cell("Agent Commission Details");
            agentCommissionDetailsLabel.setColspan(6);
            agentCommissionDetailsLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            agentCommissionDetailsLabel.setBackgroundColor(Color.gray.brighter());
            table.addCell(agentCommissionDetailsLabel);

            Cell commissionNameLabel = new Cell("Commission Name");
            Cell commissionRateLabel = new Cell("Commission Rate");
            Cell commissionCounterLabel = new Cell("Counter");
            Cell commissionAmountLabel = new Cell("Amount");
            Cell commissionTotalLabel = new Cell("Total");

            commissionNameLabel.setColspan(2);

            commissionNameLabel.setBackgroundColor(Color.gray.brighter());
            commissionRateLabel.setBackgroundColor(Color.gray.brighter());
            commissionCounterLabel.setBackgroundColor(Color.gray.brighter());
            commissionAmountLabel.setBackgroundColor(Color.gray.brighter());
            commissionTotalLabel.setBackgroundColor(Color.gray.brighter());

            commissionNameLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            commissionRateLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            commissionCounterLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            commissionAmountLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            commissionTotalLabel.setHorizontalAlignment(Element.ALIGN_CENTER);

            table.addCell(commissionNameLabel);
            table.addCell(commissionRateLabel);
            table.addCell(commissionCounterLabel);
            table.addCell(commissionAmountLabel);
            table.addCell(commissionTotalLabel);

            Cell commissionNameValue,commissionRateValue,commissionCounterValue,commissionAmountValue,commissionTotalValue;

            Set set=commissionDetailsVOHashMap.keySet();
            Iterator iterator=set.iterator();
            while (iterator.hasNext())
            {
                CommissionDetailsVO commissionDetailsVO=commissionDetailsVOHashMap.get(iterator.next());
                String vCntCounter=(new Long(commissionDetailsVO.getCount())).toString();

                commissionNameValue = new Cell(commissionDetailsVO.getChargeName());
                commissionTotalValue = new Cell(Functions.round(commissionDetailsVO.getTotal(),2));

                if(Charge_unit.Percentage.toString().equals(commissionDetailsVO.getValueType()))
                {
                    commissionRateValue = new Cell(Functions.round(Double.valueOf(commissionDetailsVO.getChargeValue()),2)+"%");
                }
                else
                {
                    commissionRateValue = new Cell(Functions.round(Double.valueOf(commissionDetailsVO.getChargeValue()),2));
                }

                commissionCounterValue = new Cell(vCntCounter);
                commissionAmountValue = new Cell(Functions.round(commissionDetailsVO.getAmount(),2));
                commissionNameValue.setColspan(2);

                commissionRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                commissionCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                commissionAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                commissionTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                table.addCell(commissionNameValue);
                table.addCell(commissionRateValue);
                table.addCell(commissionCounterValue);
                table.addCell(commissionAmountValue);
                table.addCell(commissionTotalValue);
            }

            Cell totalLabel=new Cell("Total");
            Cell totalValue=new Cell(Functions.round(totalCommissionAmount,2));

            totalLabel.setColspan(5);
            totalLabel.setHeader(true);

           /* totalLabel.setBackgroundColor(Color.gray.brighter());
            totalValue.setBackgroundColor(Color.gray.brighter());*/
            totalLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(totalLabel);
            table.addCell(totalValue);

            Cell agentChargesDetailsLabel=new Cell("Agent Charges Details");
            agentChargesDetailsLabel.setColspan(6);
            agentChargesDetailsLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            agentChargesDetailsLabel.setBackgroundColor(Color.gray.brighter());
            table.addCell(agentChargesDetailsLabel);

            Cell chargesNameLabel = new Cell("Charge Name");
            Cell chargesRateLabel = new Cell("Charge Rate");
            Cell chargesCounterLabel = new Cell("Counter");
            Cell chargesAmountLabel = new Cell("Amount");
            Cell chargesTotalLabel = new Cell("Total");

            chargesNameLabel.setColspan(2);

            chargesNameLabel.setBackgroundColor(Color.gray.brighter());
            chargesRateLabel.setBackgroundColor(Color.gray.brighter());
            chargesCounterLabel.setBackgroundColor(Color.gray.brighter());
            chargesAmountLabel.setBackgroundColor(Color.gray.brighter());
            chargesTotalLabel.setBackgroundColor(Color.gray.brighter());

            chargesNameLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            chargesRateLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            chargesCounterLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            chargesAmountLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            chargesTotalLabel.setHorizontalAlignment(Element.ALIGN_CENTER);

            table.addCell(chargesNameLabel);
            table.addCell(chargesRateLabel);
            table.addCell(chargesCounterLabel);
            table.addCell(chargesAmountLabel);
            table.addCell(chargesTotalLabel);

            Cell chargesNameValue,chargesRateValue,chargesCounterValue,chargesAmountValue,chargesTotalValue;

            Set set1=chargeDetailsVOHashMap.keySet();
            Iterator iterator1=set1.iterator();
            while (iterator1.hasNext())
            {
                ChargeDetailsVO chargeDetailsVO=chargeDetailsVOHashMap.get(iterator1.next());
                String vCntCounter=(new Long(chargeDetailsVO.getCount())).toString();

                chargesNameValue = new Cell(chargeDetailsVO.getChargeName());
                chargesTotalValue = new Cell(Functions.round(chargeDetailsVO.getTotal(),2));

                if(Charge_unit.Percentage.toString().equals(chargeDetailsVO.getValueType()))
                {
                    chargesRateValue = new Cell(Functions.round(Double.valueOf(chargeDetailsVO.getChargeValue()),2)+"%");
                }
                else
                {
                    chargesRateValue = new Cell(Functions.round(Double.valueOf(chargeDetailsVO.getChargeValue()),2));
                }

                chargesCounterValue = new Cell(vCntCounter);
                chargesAmountValue = new Cell(Functions.round(chargeDetailsVO.getAmount(),2));
                chargesNameValue.setColspan(2);

                chargesRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                chargesCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                chargesAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                chargesTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                table.addCell(chargesNameValue);
                table.addCell(chargesRateValue);
                table.addCell(chargesCounterValue);
                table.addCell(chargesAmountValue);
                table.addCell(chargesTotalValue);
            }

            Cell totalLabel1=new Cell("Total");
            Cell totalValue1=new Cell(Functions.round(totalChargesAmount,2));

            totalLabel1.setColspan(5);
            totalLabel1.setHeader(true);

            /*totalLabel1.setBackgroundColor(Color.gray.brighter());
            totalValue1.setBackgroundColor(Color.gray.brighter());*/
            totalLabel1.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalValue1.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(totalLabel1);
            table.addCell(totalValue1);

            Cell agentSummaryDetailsLabel=new Cell("Summary");
            agentSummaryDetailsLabel.setColspan(6);
            agentSummaryDetailsLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            agentSummaryDetailsLabel.setBackgroundColor(Color.gray.brighter());
            table.addCell(agentSummaryDetailsLabel);

            Cell agentCommissionAmountLabel=new Cell("Agent Total Commission Amount");
            Cell agentCommissionAmountValue=new Cell(Functions.round(totalCommissionAmount,2));

            agentCommissionAmountLabel.setColspan(5);
            agentCommissionAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(agentCommissionAmountLabel);
            table.addCell(agentCommissionAmountValue);

            Cell agentChargesAmountLabel=new Cell("Agent Total Charges Amount");
            Cell agentChargesAmountValue=new Cell(isValidAmount(Functions.round(totalChargesAmount,2)));

            agentChargesAmountLabel.setColspan(5);
            agentChargesAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(agentChargesAmountLabel);
            table.addCell(agentChargesAmountValue);

            Cell previousBalanceAmountLabel=new Cell("Previous Balance Amount");
            Cell previousBalanceAmountValue=new Cell(Functions.round(previousBalanceAmount,2));

            previousBalanceAmountLabel.setColspan(5);
            previousBalanceAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(previousBalanceAmountLabel);
            table.addCell(previousBalanceAmountValue);

            Cell agentTotalAmountFundedLabel=new Cell("Total Amount Funded");
            Cell agenttotalAmountFundedValue=new Cell(Functions.round(agentTotalFundedAmount,2));

            agentTotalAmountFundedLabel.setColspan(5);
            agenttotalAmountFundedValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

            agentTotalAmountFundedLabel.setBackgroundColor(Color.gray.brighter());
            agenttotalAmountFundedValue.setBackgroundColor(Color.gray.brighter());

            table.addCell(agentTotalAmountFundedLabel);
            table.addCell(agenttotalAmountFundedValue);


            /*Image poweredByLogoValue = Image.getInstance(PARTNER_LOGO_PATH+"logo2.jpg");

            Cell poweredByLogoLabel=new Cell("");
            Cell poweredByLogoValueCell1=new Cell(poweredByLogoValue);

            poweredByLogoLabel.setColspan(5);
            poweredByLogoValueCell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
            poweredByLogoLabel.setBackgroundColor(Color.white.brighter());
            poweredByLogoValueCell1.setBackgroundColor(Color.white.brighter());

            table.addCell(poweredByLogoLabel);
            table.addCell(poweredByLogoValueCell1);*/

            document.add(table);
            document.close();

        }
        catch (Exception ex)
        {
            logger.error(ex);
        }
        return reportFileName;
    }

    public String generatePartnerCommissionReport(HashMap<String,CommissionDetailsVO> commissionDetailsVOHashMap,PartnerDetailsVO partnerDetailsVO,TerminalVO terminalVO,SettlementDateVO settlementDateVO,double totalCommissionAmount,double partnerTotalFundedAmount,String currency,double previousBalanceAmount,WireChargeVO  wireChargeVO,HashMap<String,ChargeDetailsVO> chargeDetailsVOHashMap,double totalChargesAmount)
    {
        PayoutReportUtils payoutReportUtils=new PayoutReportUtils();
        Document document = new Document(PageSize.A3, 50, 50, 50, 50);
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );

        String reportFileName=payoutReportUtils.getPartnerReportFileName(partnerDetailsVO.getPartnerId(), terminalVO.getMemberId(),terminalVO.getTerminalId(), settlementDateVO.getSettlementEndDate(), settlementDateVO.getSettlementEndDate());
        try
        {
            reportFileName=reportFileName+".pdf";
            File filePath=new File(PARTNER_PAYOUT_REPORT_FILE_PATH+reportFileName);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            String reportingDate=targetFormat.format(new Date());
            if(!functions.isValueNull(partnerDetailsVO.getLogoName()))
            {
                partnerDetailsVO.setLogoName("logo.jpg");
            }

            Image partnerImageInstance = Image.getInstance(PARTNER_LOGO_PATH+partnerDetailsVO.getLogoName());
            partnerImageInstance.scaleAbsolute(150f, 150f);
            partnerImageInstance.scaleAbsoluteHeight(40f);

            Table table = new Table(6);
            table.setWidth(100);
            table.setBorderColor(new Color(0, 0,0));
            table.setPadding(1);


            Cell partnerNameCaptionCell=new Cell("Processor:"+partnerDetailsVO.getCompanyName());
            partnerNameCaptionCell.setColspan(2);
            partnerNameCaptionCell.setBackgroundColor(Color.white.brighter());

            Cell reportingDateCaptionCe11=new Cell("Reporting Date:"+reportingDate);
            reportingDateCaptionCe11.setColspan(2);
            reportingDateCaptionCe11.setBackgroundColor(Color.white.brighter());

            Cell partnerLogoCell=new Cell(partnerImageInstance);
            partnerLogoCell.setColspan(2);
            partnerLogoCell.setHorizontalAlignment(Element.ALIGN_CENTER);

            table.addCell(partnerNameCaptionCell);
            table.addCell(reportingDateCaptionCe11);
            table.addCell(partnerLogoCell);

            Cell partnerDetails=new Cell("Partner Details");
            partnerDetails.setColspan(6);
            partnerDetails.setHorizontalAlignment(Element.ALIGN_CENTER);
            partnerDetails.setVerticalAlignment(Element.ALIGN_CENTER);
            partnerDetails.setBackgroundColor(Color.gray.brighter());

            table.addCell(partnerDetails);

            Cell partnerNameLabel=new Cell("Partner Id:");
            Cell partnerNameValue=new Cell(partnerDetailsVO.getPartnerId());

            partnerNameLabel.setColspan(4);
            partnerNameValue.setColspan(2);

            table.addCell(partnerNameLabel);
            table.addCell(partnerNameValue);

            Cell memberIdLabel=new Cell("Member Id:");
            Cell memberIdValue=new Cell(terminalVO.getMemberId());

            memberIdLabel.setColspan(4);
            memberIdValue.setColspan(2);

            table.addCell(memberIdLabel);
            table.addCell(memberIdValue);

            Cell terminalIdLabel=new Cell("Terminal Id:");
            Cell terminalValue=new Cell(terminalVO.getTerminalId());

            terminalIdLabel.setColspan(4);
            terminalValue.setColspan(2);

            table.addCell(terminalIdLabel);
            table.addCell(terminalValue);

            Cell partnerCompanyNameLabel=new Cell("Partner Company Name:");
            Cell partnerCompanyNameValue=new Cell(partnerDetailsVO.getCompanyName());

            partnerCompanyNameLabel.setColspan(4);
            partnerCompanyNameValue.setColspan(2);

            table.addCell(partnerCompanyNameLabel);
            table.addCell(partnerCompanyNameValue);

            Cell contactPersonNameLabel=new Cell("Contact Person Name:");
            Cell contactPersonNameValue=new Cell(partnerDetailsVO.getContactPerson());

            contactPersonNameLabel.setColspan(4);
            contactPersonNameValue.setColspan(2);

            table.addCell(contactPersonNameLabel);
            table.addCell(contactPersonNameValue);

            Cell payModeLabel=new Cell("Payment Mode:");
            Cell payModeValue=new Cell(terminalVO.getPaymentTypeName());

            payModeLabel.setColspan(4);
            payModeValue.setColspan(2);

            table.addCell(payModeLabel);
            table.addCell(payModeValue);

            Cell cardTypeLabel=new Cell("Card Type:");
            Cell cardTypeValue=new Cell(terminalVO.getCardType());

            cardTypeLabel.setColspan(4);
            cardTypeValue.setColspan(2);

            table.addCell(cardTypeLabel);
            table.addCell(cardTypeValue);

            Cell currencyLabel=new Cell("Currency:");
            Cell currencyValue=new Cell(currency);

            currencyLabel.setColspan(4);
            currencyValue.setColspan(2);

            table.addCell(currencyLabel);
            table.addCell(currencyValue);

            Cell settlementFromDateLabel=new Cell("Settlement From:");
            Cell settlementFromDateValue=new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getSettlementStartDate())));

            settlementFromDateLabel.setColspan(4);
            settlementFromDateValue.setColspan(2);

            table.addCell(settlementFromDateLabel);
            table.addCell(settlementFromDateValue);

            Cell settlementToDateLabel=new Cell("Settlement To:");
            Cell settlementToDateValue=new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getSettlementEndDate())));

            settlementToDateLabel.setColspan(4);
            settlementToDateValue.setColspan(2);

            table.addCell(settlementToDateLabel);
            table.addCell(settlementToDateValue);
            /*Cell sDeclinedPeriodCaptionCell=new Cell("Decline Covered Upto:");
            Cell sDeclinedEndValueCell=new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getDeclinedEndDate())));

            sDeclinedPeriodCaptionCell.setColspan(4);
            sDeclinedEndValueCell.setColspan(2);

            table.addCell(sDeclinedPeriodCaptionCell);
            table.addCell(sDeclinedEndValueCell);

            Cell sReversedPeriodCaptionCell=new Cell("Reversal Covered Upto:");
            Cell sReversedEndValueCell=new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getReversedEndDate())));

            sReversedPeriodCaptionCell.setColspan(4);
            sReversedEndValueCell.setColspan(2);

            table.addCell(sReversedPeriodCaptionCell);
            table.addCell(sReversedEndValueCell);

            Cell sChargebackPeriodCaptionCell=new Cell("Chargeback Covered Upto:");
            Cell sChargebackEndValueCell=new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getChargebackEndDate())));

            sChargebackPeriodCaptionCell.setColspan(4);
            sChargebackEndValueCell.setColspan(2);

            table.addCell(sChargebackPeriodCaptionCell);
            table.addCell(sChargebackEndValueCell);*/

            Cell partnerCommissionDetailsLabel=new Cell("Partner Commission Details");
            partnerCommissionDetailsLabel.setColspan(6);
            partnerCommissionDetailsLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            partnerCommissionDetailsLabel.setBackgroundColor(Color.gray.brighter());
            table.addCell(partnerCommissionDetailsLabel);

            Cell commissionNameLabel  = new Cell("Commission Name");
            Cell commissionRateLabel  = new Cell("Commission Rate");
            Cell commissionCounterLabel  = new Cell("Counter");
            Cell commissionAmountLabel  = new Cell("Amount");
            Cell commissionTotalLabel  = new Cell("Total");

            commissionNameLabel.setColspan(2);

            commissionNameLabel.setBackgroundColor(Color.gray.brighter());
            commissionRateLabel.setBackgroundColor(Color.gray.brighter());
            commissionCounterLabel.setBackgroundColor(Color.gray.brighter());
            commissionAmountLabel.setBackgroundColor(Color.gray.brighter());
            commissionTotalLabel.setBackgroundColor(Color.gray.brighter());

            commissionNameLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            commissionRateLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            commissionCounterLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            commissionAmountLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            commissionTotalLabel.setHorizontalAlignment(Element.ALIGN_CENTER);

            table.addCell(commissionNameLabel);
            table.addCell(commissionRateLabel);
            table.addCell(commissionCounterLabel);
            table.addCell(commissionAmountLabel);
            table.addCell(commissionTotalLabel);

            Cell commissionNameValue,commissionRateValue,commissionCounterValue,commissionAmountValue,commissionTotalValue;

            Set set=commissionDetailsVOHashMap.keySet();
            Iterator iterator=set.iterator();
            while (iterator.hasNext())
            {
                CommissionDetailsVO commissionDetailsVO=commissionDetailsVOHashMap.get(iterator.next());
                String vCntCounter=(new Long(commissionDetailsVO.getCount())).toString();

                commissionNameValue = new Cell(commissionDetailsVO.getChargeName());
                commissionTotalValue = new Cell(Functions.round(commissionDetailsVO.getTotal(),2));

                if(Charge_unit.Percentage.toString().equals(commissionDetailsVO.getValueType()))
                {
                    commissionRateValue = new Cell(Functions.round(Double.valueOf(commissionDetailsVO.getChargeValue()),2)+"%");
                }
                else
                {
                    commissionRateValue = new Cell(Functions.round(Double.valueOf(commissionDetailsVO.getChargeValue()),2));
                }

                commissionCounterValue = new Cell(vCntCounter);
                commissionAmountValue = new Cell(Functions.round(commissionDetailsVO.getAmount(),2));
                commissionNameValue.setColspan(2);

                commissionRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                commissionCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                commissionAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                commissionTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                table.addCell(commissionNameValue);
                table.addCell(commissionRateValue);
                table.addCell(commissionCounterValue);
                table.addCell(commissionAmountValue);
                table.addCell(commissionTotalValue);
            }
            Cell totalLabel=new Cell("Total");
            Cell totalValue=new Cell(Functions.round(totalCommissionAmount,2));

            totalLabel.setColspan(5);
            totalLabel.setHeader(true);

            /*totalLabel.setBackgroundColor(Color.gray.brighter());
            totalValue.setBackgroundColor(Color.gray.brighter());*/
            totalLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(totalLabel);
            table.addCell(totalValue);

            Cell partnerChargesDetailsLabel=new Cell("Partner Charges Details");
            partnerChargesDetailsLabel.setColspan(6);
            partnerChargesDetailsLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            partnerChargesDetailsLabel.setBackgroundColor(Color.gray.brighter());
            table.addCell(partnerChargesDetailsLabel);

            Cell chargesNameLabel = new Cell("Charge Name");
            Cell chargesRateLabel = new Cell("Charge Rate");
            Cell chargesCounterLabel = new Cell("Counter");
            Cell chargesAmountLabel = new Cell("Amount");
            Cell chargesTotalLabel = new Cell("Total");

            chargesNameLabel.setColspan(2);

            chargesNameLabel.setBackgroundColor(Color.gray.brighter());
            chargesRateLabel.setBackgroundColor(Color.gray.brighter());
            chargesCounterLabel.setBackgroundColor(Color.gray.brighter());
            chargesAmountLabel.setBackgroundColor(Color.gray.brighter());
            chargesTotalLabel.setBackgroundColor(Color.gray.brighter());

            chargesNameLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            chargesRateLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            chargesCounterLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            chargesAmountLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            chargesTotalLabel.setHorizontalAlignment(Element.ALIGN_CENTER);

            table.addCell(chargesNameLabel);
            table.addCell(chargesRateLabel);
            table.addCell(chargesCounterLabel);
            table.addCell(chargesAmountLabel);
            table.addCell(chargesTotalLabel);

            Cell chargesNameValue,chargesRateValue,chargesCounterValue,chargesAmountValue,chargesTotalValue;

            Set set1=chargeDetailsVOHashMap.keySet();
            Iterator iterator1=set1.iterator();
            while (iterator1.hasNext())
            {
                ChargeDetailsVO chargeDetailsVO=chargeDetailsVOHashMap.get(iterator1.next());
                String vCntCounter=(new Long(chargeDetailsVO.getCount())).toString();

                chargesNameValue = new Cell(chargeDetailsVO.getChargeName());
                chargesTotalValue = new Cell(Functions.round(chargeDetailsVO.getTotal(),2));

                if(Charge_unit.Percentage.toString().equals(chargeDetailsVO.getValueType()))
                {
                    chargesRateValue = new Cell(Functions.round(Double.valueOf(chargeDetailsVO.getChargeValue()),2)+"%");
                }
                else
                {
                    chargesRateValue = new Cell(Functions.round(Double.valueOf(chargeDetailsVO.getChargeValue()),2));
                }

                chargesCounterValue = new Cell(vCntCounter);
                chargesAmountValue = new Cell(Functions.round(chargeDetailsVO.getAmount(),2));
                chargesNameValue.setColspan(2);

                chargesRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                chargesCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                chargesAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                chargesTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                table.addCell(chargesNameValue);
                table.addCell(chargesRateValue);
                table.addCell(chargesCounterValue);
                table.addCell(chargesAmountValue);
                table.addCell(chargesTotalValue);
            }

            Cell totalLabel1=new Cell("Total");
            Cell totalValue1=new Cell(Functions.round(totalChargesAmount,2));

            totalLabel1.setColspan(5);
            totalLabel1.setHeader(true);

            /*totalLabel1.setBackgroundColor(Color.gray.brighter());
            totalValue1.setBackgroundColor(Color.gray.brighter());*/
            totalLabel1.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalValue1.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(totalLabel1);
            table.addCell(totalValue1);

            Cell partnerSummaryDetailsLabel=new Cell("Summary");
            partnerSummaryDetailsLabel.setColspan(6);
            partnerSummaryDetailsLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            partnerSummaryDetailsLabel.setBackgroundColor(Color.gray.brighter());
            table.addCell(partnerSummaryDetailsLabel);


            Cell partnerCommissionAmountLabel=new Cell("Partner Commission Amount");
            Cell partnerCommissionAmountValue=new Cell(Functions.round(totalCommissionAmount,2));

            partnerCommissionAmountLabel.setColspan(5);

            partnerCommissionAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(partnerCommissionAmountLabel);
            table.addCell(partnerCommissionAmountValue);

            Cell partnerChargesAmountLabel=new Cell("Partner Charges Amount");
            Cell partnerChargesAmountValue=new Cell(isValidAmount(Functions.round(totalChargesAmount,2)));

            partnerChargesAmountLabel.setColspan(5);

            partnerChargesAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(partnerChargesAmountLabel);
            table.addCell(partnerChargesAmountValue);

            Cell previousBalanceAmountLabel=new Cell("Previous Balance Amount");
            Cell previousBalanceAmountValue=new Cell(Functions.round(previousBalanceAmount,2));

            previousBalanceAmountLabel.setColspan(5);
            previousBalanceAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(previousBalanceAmountLabel);
            table.addCell(previousBalanceAmountValue);

            Cell partnerTotalAmountFundedLabel=new Cell("Total Amount Funded");
            Cell partnerTotalAmountFundedValue=new Cell(Functions.round(partnerTotalFundedAmount,2));
            partnerTotalAmountFundedLabel.setColspan(5);
            partnerTotalAmountFundedValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
            partnerTotalAmountFundedLabel.setBackgroundColor(Color.gray.brighter());
            partnerTotalAmountFundedValue.setBackgroundColor(Color.gray.brighter());
            table.addCell(partnerTotalAmountFundedLabel);
            table.addCell(partnerTotalAmountFundedValue);


            Image poweredByLogoValue = Image.getInstance(PARTNER_LOGO_PATH+"poweredby_new_logo.png");

            Cell poweredByLogoLabel=new Cell("");
            Cell poweredByLogoValueCell1=new Cell(poweredByLogoValue);

            poweredByLogoLabel.setColspan(5);
            poweredByLogoValueCell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
            poweredByLogoLabel.setBackgroundColor(Color.white.brighter());
            poweredByLogoValueCell1.setBackgroundColor(Color.white.brighter());

            table.addCell(poweredByLogoLabel);
            table.addCell(poweredByLogoValueCell1);

            document.add(table);
            document.close();

        }
        catch (Exception ex)
        {
            logger.error(ex);
        }
        return reportFileName;
    }

    public List<String> payoutCronUsingBankWireManagerPerTerminal()throws Exception
    {
        String memberId=null;
        String accountId=null;
        String cyclememberlist=null;

        SettlementDateVO settlementDateVO=null;
        RollingReserveDateVO rollingReserveDateVO=null;
        List<String> stringList=new ArrayList<String>();
        List accountIdFailList=new ArrayList();

        String issettlementcronexceuted="Y";
        String ispayoutcronexcuted="N";

        logger.debug("Payout Cron Is Going To Execute:::::");
        List<BankWireManagerVO> bankWireManagerVOs=payoutDAO.getBankReceivedWireList(issettlementcronexceuted,ispayoutcronexcuted);
        if(bankWireManagerVOs == null || bankWireManagerVOs.isEmpty())
        {
            cyclememberlist="0:0:0:0:Failed:No New Wire Received From  Bank Manager";
            stringList.add(cyclememberlist);
            logger.debug(cyclememberlist);
            return stringList;
        }

        logger.debug("Total Bank Received Wire=====" + bankWireManagerVOs.size());
        ChargeManager chargeManager=new ChargeManager();
        for(BankWireManagerVO bankWireManagerVO:bankWireManagerVOs)
        {
            logger.debug("Going To Execute=====" + bankWireManagerVO.getBankwiremanagerId());
            accountId=bankWireManagerVO.getAccountId();
            if(accountIdFailList.contains(bankWireManagerVO.getAccountId()))
            {
                cyclememberlist=bankWireManagerVO.getBankwiremanagerId()+":0:"+accountId+":Failed:Invalid Settlement Date Order In BankWireManager";
                logger.debug(cyclememberlist);
                stringList.add(cyclememberlist);
                continue;
            }

            //Step:Add flag for make sure that current bankwire is min wire for current account in case for.
            boolean isValidBankWireToProcess=payoutDAO.isBankWireValidToProcess(bankWireManagerVO.getBankwiremanagerId(), accountId);
            if(isValidBankWireToProcess)
            {
                Set<String> memberSet=getMembersByAccountId(accountId);
                if(memberSet != null && memberSet.size()>0)
                {
                    Iterator itr=memberSet.iterator();
                    while (itr.hasNext())
                    {
                        //Read All The Member Respectively
                        memberId=(String)itr.next();
                        settlementDateVO=new SettlementDateVO();
                        rollingReserveDateVO=new RollingReserveDateVO();

                        //Step3:Set The Rolling Reserve Release Date For Current Member
                        rollingReserveDateVO.setRollingReserveEndDate(bankWireManagerVO.getRollingreservereleasedateupto());
                        setRollingReserveStartDateToMerchantPayout(memberId, accountId, rollingReserveDateVO);

                        //Get All The Terminal OF Member And Execute The cycle Based On Terminal
                        TerminalManager terminalManager=new TerminalManager();
                        List<TerminalVO> terminalVOList=terminalManager.getTerminalsByMemberAccountIdForPayoutReport(memberId, accountId);
                        for (TerminalVO terminalVO:terminalVOList)
                        {
                            //Step:Check there transaction started form current terminal
                            String memberFirstTransactionDateOnTerminal=payoutDAO.getMemberFirstTransactionDateOnTerminal(terminalVO);
                            if(functions.isValueNull(memberFirstTransactionDateOnTerminal))
                            {
                                //Step4:Check The Charges Mapped To Current Member On Current Account
                                List<ChargeVO> chargeVOs=payoutDAO.getChargesAsPerTerminal(terminalVO);
                                if(chargeVOs!=null && chargeVOs.size()>0)
                                {
                                    //Stpe4.2:Check If Any Random Charges For Merchant On Terminal.
                                    List<MerchantRandomChargesVO> randomChargesVOs=chargeManager.getMerchantRandomChargesList(bankWireManagerVO.getBankwiremanagerId(),memberId,terminalVO.getTerminalId());

                                    //Step5:Set The Various Start Date And End Date
                                    settlementDateVO.setSettlementcycleNumber(bankWireManagerVO.getBankwiremanagerId());
                                    settlementDateVO.setSettlementEndDate(bankWireManagerVO.getServer_end_date());
                                    settlementDateVO.setDeclinedEndDate(bankWireManagerVO.getDeclinedcoveredupto());
                                    settlementDateVO.setReversedEndDate(bankWireManagerVO.getReversedCoveredUpto());
                                    settlementDateVO.setChargebackEndDate(bankWireManagerVO.getChargebackcoveredupto());

                                    setSettledStartDateToMerchantPayout(terminalVO, settlementDateVO);
                                    setDeclinedStartDateToMerchantPayout(terminalVO, settlementDateVO);
                                    setReversedStartDateToMerchantPayout(terminalVO, settlementDateVO);
                                    setChargebackStartDateToMerchantPayout(terminalVO, settlementDateVO);

                                    TransactionSummaryVO transactionSummaryVO=getTotalSuccessCountAmountByTerminal(terminalVO,settlementDateVO);

                                    WeeklyPayoutReportVO weeklyPayoutReportVO=new WeeklyPayoutReportVO();
                                    weeklyPayoutReportVO.setTerminalVO(terminalVO);
                                    weeklyPayoutReportVO.setSettlementDateVO(settlementDateVO);
                                    weeklyPayoutReportVO.setRollingReserveDateVO(rollingReserveDateVO);
                                    weeklyPayoutReportVO.setChargeVOList(chargeVOs);
                                    weeklyPayoutReportVO.setTransactionSummaryVO(transactionSummaryVO);
                                    weeklyPayoutReportVO.setMerchantRandomChargesVOList(randomChargesVOs);
                                    String status=generateWeeklyPayoutReportBasedOnTerminal(weeklyPayoutReportVO);
                                    if("success".equalsIgnoreCase(status))
                                    {
                                        cyclememberlist=bankWireManagerVO.getBankwiremanagerId()+":"+memberId+":"+accountId+":"+terminalVO.getTerminalId()+":"+status+":"+"Wire Is Created Successfully";
                                    }
                                    else
                                    {
                                        cyclememberlist=bankWireManagerVO.getBankwiremanagerId()+":"+memberId+":"+accountId+":"+terminalVO.getTerminalId()+":"+status+":"+"Wire Is Creation Failed";
                                    }
                                    stringList.add(cyclememberlist);
                                    logger.debug(stringList);
                                }
                                else
                                {
                                    cyclememberlist=bankWireManagerVO.getBankwiremanagerId()+":"+memberId+":"+accountId+":"+terminalVO.getTerminalId()+":Failed:No Charges Mapped On Terminal";
                                    stringList.add(cyclememberlist);
                                    logger.debug(stringList);
                                    continue;
                                }
                            }
                            else
                            {
                                cyclememberlist=bankWireManagerVO.getBankwiremanagerId()+":"+memberId+":"+accountId+":"+terminalVO.getTerminalId()+":Failed:No Transaction Found On Terminal";
                                stringList.add(cyclememberlist);
                                logger.debug(cyclememberlist);
                                continue;
                            }
                        }
                    }
                }
                else
                {
                    cyclememberlist=bankWireManagerVO.getBankwiremanagerId()+":0:"+accountId+":Failed:No Member Mapped With This Account";
                    stringList.add(cyclememberlist);
                    logger.debug(stringList);
                    continue;
                }
            }
            else
            {
                //SomeThing is Wrong don't do anything with that account
                accountIdFailList.add(bankWireManagerVO.getAccountId());
                cyclememberlist=bankWireManagerVO.getBankwiremanagerId()+":0:"+accountId+":Failed:Invalid Settlement Date Order In BankWireManager";
                logger.debug(cyclememberlist);
                stringList.add(cyclememberlist);
                continue;
            }

        }
        return stringList;
    }

    public List<String> merchantPayoutReportBasedOnBankWire(String bankWireId,HashMap<String, String> exchangeRate,HashMap<String, String> dynamicCountAmountMap)throws Exception
    {
        String memberId=null;
        String accountId=null;
        String cyclememberlist=null;
        boolean isFailed=true;

        SettlementDateVO settlementDateVO=null;
        RollingReserveDateVO rollingReserveDateVO=null;
        List<String> stringList=new ArrayList<String>();
        List accountIdFailList=new ArrayList();

        String issettlementcronexceuted="Y";
        String ispayoutcronexcuted="N";

        logger.debug("Payout Cron Is Going To Execute:::::");
        BankWireManagerVO bankWireManagerVO=payoutDAO.getBankWireDetails(bankWireId,issettlementcronexceuted,ispayoutcronexcuted);
        if(bankWireManagerVO == null)
        {
            cyclememberlist="0:0:0:0:Failed:No New Wire Received From  Bank Manager";
            stringList.add(cyclememberlist);
            logger.debug(cyclememberlist);
            return stringList;
        }

        ChargeManager chargeManager=new ChargeManager();
        TransactionManager transactionManager=new TransactionManager();
        /*for(BankWireManagerVO bankWireManagerVO:bankWireManagerVOs)
        {*/
        logger.debug("Going To Execute=====" + bankWireManagerVO.getBankwiremanagerId());
        accountId=bankWireManagerVO.getAccountId();

        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);

        DateVO dateVO=new DateVO();
        dateVO.setStartDate(bankWireManagerVO.getServer_start_date());
        dateVO.setEndDate(bankWireManagerVO.getServer_end_date());

        boolean pendingTransaction=transactionManager.checkPendingTransaction(gatewayAccount,dateVO);
        if(pendingTransaction){
            cyclememberlist=bankWireManagerVO.getBankwiremanagerId()+":-:"+accountId+":-:Failed:Transaction status need to be corrected";
            stringList.add(cyclememberlist);
            stringList.add("fail");
            logger.debug(cyclememberlist);
            return stringList;
        }

        //Step:Add flag for make sure that current bankwire is min wire for current account in case for.
        boolean isValidBankWireToProcess=payoutDAO.isBankWireValidToProcess(bankWireManagerVO.getBankwiremanagerId(), accountId);
        if(isValidBankWireToProcess)
        {
            Set<String> memberSet=getMembersByAccountId(accountId);
            if(memberSet != null && memberSet.size()>0)
            {
                Iterator itr=memberSet.iterator();
                while (itr.hasNext())
                {
                    //Read All The Member Respectively
                    memberId=(String)itr.next();
                    settlementDateVO=new SettlementDateVO();
                    rollingReserveDateVO=new RollingReserveDateVO();

                    //Step3:Set The Rolling Reserve Release Date For Current Member
                    rollingReserveDateVO.setRollingReserveEndDate(bankWireManagerVO.getRollingreservereleasedateupto());
                    setRollingReserveStartDateToMerchantPayout(memberId, accountId, rollingReserveDateVO);

                    //Get All The Terminal OF Member And Execute The cycle Based On Terminal
                    TerminalManager terminalManager=new TerminalManager();
                    List<TerminalVO> terminalVOList=terminalManager.getTerminalsByMemberAccountIdForPayoutReport(memberId,accountId);
                    for (TerminalVO terminalVO:terminalVOList)
                    {
                        //Step:Check there transaction started form current terminal
                        String memberFirstTransactionDateOnTerminal=payoutDAO.getMemberFirstTransactionDateOnTerminal(terminalVO);
                        if(functions.isValueNull(memberFirstTransactionDateOnTerminal))
                        {
                            //Step4:Check The Charges Mapped To Current Member On Current Account
                            List<ChargeVO> chargeVOs=payoutDAO.getChargesAsPerTerminal(terminalVO);
                            if(chargeVOs!=null && chargeVOs.size()>0)
                            {
                                //Stpe4.2:Check If Any Random Charges For Merchant On Terminal.
                                List<MerchantRandomChargesVO> randomChargesVOs=chargeManager.getMerchantRandomChargesList(bankWireManagerVO.getBankwiremanagerId(),memberId,terminalVO.getTerminalId());

                                //Step5:Set The Various Start Date And End Date
                                settlementDateVO.setSettlementcycleNumber(bankWireManagerVO.getBankwiremanagerId());
                                settlementDateVO.setSettlementEndDate(bankWireManagerVO.getServer_end_date());
                                settlementDateVO.setDeclinedEndDate(bankWireManagerVO.getDeclinedcoveredupto());
                                settlementDateVO.setReversedEndDate(bankWireManagerVO.getReversedCoveredUpto());
                                settlementDateVO.setChargebackEndDate(bankWireManagerVO.getChargebackcoveredupto());

                                boolean wireDateFound;
                                wireDateFound = setSettledStartDateToMerchantPayout(terminalVO, settlementDateVO);
                                if (!wireDateFound)
                                {
                                    settlementDateVO.setSettlementStartDate(bankWireManagerVO.getServer_start_date());
                                }

                                wireDateFound = setDeclinedStartDateToMerchantPayout(terminalVO, settlementDateVO);
                                if (!wireDateFound)
                                {
                                    settlementDateVO.setDeclinedStartDate(bankWireManagerVO.getServer_start_date());
                                }

                                wireDateFound = setReversedStartDateToMerchantPayout(terminalVO, settlementDateVO);
                                if (!wireDateFound)
                                {
                                    settlementDateVO.setReversedStartDate(bankWireManagerVO.getServer_start_date());
                                }

                                wireDateFound = setChargebackStartDateToMerchantPayout(terminalVO, settlementDateVO);
                                if (!wireDateFound)
                                {
                                    settlementDateVO.setChargebackStartDate(bankWireManagerVO.getServer_start_date());
                                }

                                TransactionSummaryVO transactionSummaryVO=getTotalSuccessCountAmountByTerminal(terminalVO,settlementDateVO);

                                WeeklyPayoutReportVO weeklyPayoutReportVO=new WeeklyPayoutReportVO();
                                weeklyPayoutReportVO.setTerminalVO(terminalVO);
                                weeklyPayoutReportVO.setSettlementDateVO(settlementDateVO);
                                weeklyPayoutReportVO.setRollingReserveDateVO(rollingReserveDateVO);
                                weeklyPayoutReportVO.setChargeVOList(chargeVOs);
                                weeklyPayoutReportVO.setTransactionSummaryVO(transactionSummaryVO);
                                weeklyPayoutReportVO.setMerchantRandomChargesVOList(randomChargesVOs);
                                weeklyPayoutReportVO.setExchangeRate(exchangeRate);
                                weeklyPayoutReportVO.setDynamicCountAmountMap(dynamicCountAmountMap);
                                String status=generateWeeklyPayoutReportBasedOnTerminal(weeklyPayoutReportVO);
                                if("success".equalsIgnoreCase(status))
                                {
                                    cyclememberlist=bankWireManagerVO.getBankwiremanagerId()+":"+memberId+":"+accountId+":"+terminalVO.getTerminalId()+":"+status+":"+"Wire Is Created Successfully";
                                    stringList.add(cyclememberlist);
                                    isFailed=false;
                                }
                                else
                                {
                                    cyclememberlist=bankWireManagerVO.getBankwiremanagerId()+":"+memberId+":"+accountId+":"+terminalVO.getTerminalId()+":"+status+":"+"Wire Is Creation Failed";
                                    stringList.add(cyclememberlist);
                                }
                                logger.debug(stringList);
                            }
                            else
                            {
                                cyclememberlist=bankWireManagerVO.getBankwiremanagerId()+":"+memberId+":"+accountId+":"+terminalVO.getTerminalId()+":Failed:No Charges Mapped On Terminal";
                                stringList.add(cyclememberlist);
                                logger.debug(stringList);
                                continue;
                            }
                        }
                        else
                        {
                            cyclememberlist=bankWireManagerVO.getBankwiremanagerId()+":"+memberId+":"+accountId+":"+terminalVO.getTerminalId()+":Failed:Terminal with no Activities";
                            stringList.add(cyclememberlist);
                            logger.debug(cyclememberlist);
                            continue;
                        }
                    }
                }
                if(isFailed)
                    stringList.add("fail");
            }
            else
            {
                cyclememberlist=bankWireManagerVO.getBankwiremanagerId()+":0:"+accountId+":Failed:No Member Mapped With This Account";
                stringList.add(cyclememberlist);
                stringList.add("fail");
                logger.debug(stringList);
            }
        }
        else
        {
            //SomeThing is Wrong don't do anything with that account
            accountIdFailList.add(bankWireManagerVO.getAccountId());
            cyclememberlist=bankWireManagerVO.getBankwiremanagerId()+":0:"+accountId+":Failed:Invalid Settlement Date Order In BankWireManager";
            logger.debug(cyclememberlist);
            stringList.add(cyclememberlist);
            stringList.add("fail");
        }

       /* }*/
        return stringList;
    }
    public List<String> merchantPayoutReportBasedOnBankWire1(String bankWireId,HashMap<String, String> dynamicCountAmountMap,List<TerminalVO> terminalVOs,List<TerminalVO> pendingList)throws Exception
    {
        String cyclememberlist=null;
        SettlementDateVO settlementDateVO=null;
        RollingReserveDateVO rollingReserveDateVO=null;
        List<String> stringList=new ArrayList<String>();
        List<TerminalVO> successList=new ArrayList<>();

        String issettlementcronexceuted="Y";
        String ispayoutcronexcuted="N";

        logger.debug("Payout Cron Is Going To Execute:::::");
        logger.debug("Pending List:::::"+pendingList.size());
        BankWireManagerVO bankWireManagerVO=payoutDAO.getBankWireDetails(bankWireId,issettlementcronexceuted,ispayoutcronexcuted);
        if(bankWireManagerVO == null)
        {
            cyclememberlist="0:0:0:0:Failed:No New Wire Received From  Bank Manager";
            stringList.add(cyclememberlist);
            logger.debug(cyclememberlist);
            return stringList;
        }

        ChargeManager chargeManager=new ChargeManager();
        TransactionManager transactionManager=new TransactionManager();
        logger.debug("Going To Execute=====" + bankWireManagerVO.getBankwiremanagerId());
        String accountId=bankWireManagerVO.getAccountId();

        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);

        DateVO dateVO=new DateVO();
        dateVO.setStartDate(bankWireManagerVO.getServer_start_date());
        dateVO.setEndDate(bankWireManagerVO.getServer_end_date());

        for(TerminalVO terminalVO1:terminalVOs)
        {
            String memberId = terminalVO1.getMemberId();
            String terminalId = terminalVO1.getTerminalId();
            String conversionRate= String.valueOf(terminalVO1.getConversionRate());

            rollingReserveDateVO=new RollingReserveDateVO();
            rollingReserveDateVO.setRollingReserveEndDate(bankWireManagerVO.getRollingreservereleasedateupto());
            rollingReserveDateVO.setRollingReserveStartDate(bankWireManagerVO.getRollingreservereleaseStartdate());
            settlementDateVO = new SettlementDateVO();
            TerminalManager terminalManager=new TerminalManager();
            terminalVO1= terminalManager.getTerminalsByMemberAccountIdForPayoutReportRequest(memberId,accountId,terminalId);
            String memberFirstTransactionDateOnTerminal = payoutDAO.getMemberFirstTransactionDateOnTerminalId(terminalVO1);
            if (functions.isValueNull(memberFirstTransactionDateOnTerminal))
            {
                boolean pendingTransaction = transactionManager.checkPendingTransactionOfMerchant(gatewayAccount, memberId, terminalId, dateVO);
                if (pendingTransaction)
                {
                    cyclememberlist = bankWireManagerVO.getBankwiremanagerId() + ":" + terminalVO1.getMemberId()  +":" + terminalVO1.getAccountId()+ ":" +terminalVO1.getTerminalId()+":Failed:Transaction status need to be corrected";
                    stringList.add(cyclememberlist);
                    logger.debug(cyclememberlist);
                    continue;
                }
                List<ChargeVO> chargeVOs = payoutDAO.getChargesAsPerTerminal(terminalVO1);
                if (chargeVOs != null && chargeVOs.size() > 0)
                {
                    HashMap<String,MerchantRandomChargesVO> randomChargesVOsNew = chargeManager.getMerchantRandomChargesListVO(bankWireManagerVO.getBankwiremanagerId(), terminalVO1.getMemberId(), terminalVO1.getTerminalId());
                    List<MerchantRandomChargesVO> randomChargesVOs = chargeManager.getMerchantRandomChargesList(bankWireManagerVO.getBankwiremanagerId(), terminalVO1.getMemberId(), terminalVO1.getTerminalId());

                    settlementDateVO.setSettlementcycleNumber(bankWireManagerVO.getBankwiremanagerId());
                    settlementDateVO.setSettlementEndDate(bankWireManagerVO.getServer_end_date());
                    settlementDateVO.setDeclinedEndDate(bankWireManagerVO.getDeclinedcoveredupto());
                    settlementDateVO.setReversedEndDate(bankWireManagerVO.getReversedCoveredUpto());
                    settlementDateVO.setChargebackEndDate(bankWireManagerVO.getChargebackcoveredupto());

                    settlementDateVO.setSettlementStartDate(bankWireManagerVO.getServer_start_date());

                    if(functions.isValueNull(bankWireManagerVO.getDeclinedcoveredStartdate())) {
                        settlementDateVO.setDeclinedStartDate(bankWireManagerVO.getDeclinedcoveredStartdate());
                    }
                    else{
                        settlementDateVO.setDeclinedStartDate(bankWireManagerVO.getServer_start_date());
                    }

                    if(functions.isValueNull(bankWireManagerVO.getReversedCoveredStartdate())){
                        settlementDateVO.setReversedStartDate(bankWireManagerVO.getReversedCoveredStartdate());
                    }
                    else{
                        settlementDateVO.setReversedStartDate(bankWireManagerVO.getServer_start_date());
                    }

                    if (functions.isValueNull(bankWireManagerVO.getChargebackcoveredStartdate())){
                        settlementDateVO.setChargebackStartDate(bankWireManagerVO.getChargebackcoveredStartdate());
                    }
                    else{
                        settlementDateVO.setChargebackStartDate(bankWireManagerVO.getServer_start_date());
                    }

                    if(functions.isValueNull(conversionRate)) {
                        terminalVO1.setConversionRate(Double.parseDouble(conversionRate));
                    }
                    TransactionSummaryVO transactionSummaryVO = getTotalSuccessCountAmountByTerminal(terminalVO1, settlementDateVO);
                    WeeklyPayoutReportVO weeklyPayoutReportVO = new WeeklyPayoutReportVO();
                    weeklyPayoutReportVO.setTerminalVO(terminalVO1);
                    weeklyPayoutReportVO.setSettlementDateVO(settlementDateVO);
                    weeklyPayoutReportVO.setRollingReserveDateVO(rollingReserveDateVO);
                    weeklyPayoutReportVO.setChargeVOList(chargeVOs);
                    weeklyPayoutReportVO.setTransactionSummaryVO(transactionSummaryVO);
                    weeklyPayoutReportVO.setMerchantRandomChargesVOListNew(randomChargesVOsNew);
                    weeklyPayoutReportVO.setMerchantRandomChargesVOList(randomChargesVOs);
                    // weeklyPayoutReportVO.setExchangeRate(exchangeRate);
                    weeklyPayoutReportVO.setDynamicCountAmountMap(dynamicCountAmountMap);
                    weeklyPayoutReportVO.setPendingList(pendingList);
                    weeklyPayoutReportVO.setRequestTerminalVO(terminalVOs);
                    String status = generateWeeklyPayoutReportBasedOnTerminalMerchant(weeklyPayoutReportVO);
                    if ("success".equalsIgnoreCase(status))
                    {
                        successList.add(terminalVO1);
                        logger.error("successList::::" + successList.size());
                        logger.error("pendingList::::"+pendingList.size());
                        if(pendingList.size()==successList.size())
                        {
                            boolean ispayoutcronupdated = payoutDAO.updatePayoutCronExecutedFlag(accountId, bankWireId);
                        }
                        cyclememberlist = bankWireManagerVO.getBankwiremanagerId() + ":" + memberId + ":" + accountId + ":" + terminalVO1.getTerminalId() + ":" + status + ":" + "Wire Is Created Successfully";
                    }
                    else
                    {
                        cyclememberlist = bankWireManagerVO.getBankwiremanagerId() + ":" + memberId + ":" + accountId + ":" + terminalVO1.getTerminalId() + ":" + status + ":" + "Wire Is Creation Failed";
                    }
                    stringList.add(cyclememberlist);
                    logger.debug(stringList);
                    continue;
                }
                else
                {
                    cyclememberlist = bankWireManagerVO.getBankwiremanagerId() + ":" + memberId + ":" + accountId + ":" + terminalVO1.getTerminalId() + ":Failed:No Charges Mapped On Terminal";
                    stringList.add(cyclememberlist);
                    logger.debug(stringList);
                    continue;
                }
            }
            else
            {
                cyclememberlist = bankWireManagerVO.getBankwiremanagerId() + ":" + memberId + ":" + accountId + ":" + terminalVO1.getTerminalId() + ":Failed:Terminal with no Activities";
                stringList.add(cyclememberlist);
                logger.debug(cyclememberlist);
                continue;
            }
        }
        return stringList;
    }
    public List<String> consolidatedMerchantPayoutReportBasedOnBankWire1(String bankWireId,HashMap<String,List<TerminalVO>> getListOfMember)throws Exception
    {
        String cyclememberlist=null;
        SettlementDateVO settlementDateVO=null;
        RollingReserveDateVO rollingReserveDateVO=null;
        List<String> stringList=new ArrayList<String>();
        List<TerminalVO> successList=new ArrayList<>();

        String issettlementcronexceuted="Y";
        String ispayoutcronexcuted="N";

        logger.debug("Payout Cron Is Going To Execute:::::"+getListOfMember);
        BankWireManagerVO bankWireManagerVO=payoutDAO.getBankWireDetails(bankWireId,issettlementcronexceuted,ispayoutcronexcuted);
        if(bankWireManagerVO == null)
        {
            cyclememberlist="0:0:0:0:Failed:No New Wire Received From  Bank Manager";
            stringList.add(cyclememberlist);
            logger.debug(cyclememberlist);
            return stringList;
        }

        ChargeManager chargeManager=new ChargeManager();
        TransactionManager transactionManager=new TransactionManager();
        logger.debug("Going To Execute=====" + bankWireManagerVO.getBankwiremanagerId());
        String accountId=bankWireManagerVO.getAccountId();

        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);

        DateVO dateVO=new DateVO();
        dateVO.setStartDate(bankWireManagerVO.getServer_start_date());
        dateVO.setEndDate(bankWireManagerVO.getServer_end_date());

        Set<String> key=getListOfMember.keySet();
        for(String memberId:key)
        {
            List<TerminalVO> terminalVOList = getListOfMember.get(memberId);
            rollingReserveDateVO = new RollingReserveDateVO();
            rollingReserveDateVO.setRollingReserveEndDate(bankWireManagerVO.getRollingreservereleasedateupto());
            rollingReserveDateVO.setRollingReserveStartDate(bankWireManagerVO.getRollingreservereleaseStartdate());
            settlementDateVO = new SettlementDateVO();
            // List<MerchantRandomChargesVO> randomChargesVOs = chargeManager.getMerchantRandomChargesList(bankWireManagerVO.getBankwiremanagerId(), terminalVO1.getMemberId(), terminalVO1.getTerminalId());
            settlementDateVO.setSettlementcycleNumber(bankWireManagerVO.getBankwiremanagerId());
            settlementDateVO.setSettlementEndDate(bankWireManagerVO.getServer_end_date());
            settlementDateVO.setDeclinedEndDate(bankWireManagerVO.getDeclinedcoveredupto());
            settlementDateVO.setReversedEndDate(bankWireManagerVO.getReversedCoveredUpto());
            settlementDateVO.setChargebackEndDate(bankWireManagerVO.getChargebackcoveredupto());
            settlementDateVO.setSettlementStartDate(bankWireManagerVO.getServer_start_date());
            if (functions.isValueNull(bankWireManagerVO.getDeclinedcoveredStartdate())) {
                settlementDateVO.setDeclinedStartDate(bankWireManagerVO.getDeclinedcoveredStartdate());
            }
            else {
                settlementDateVO.setDeclinedStartDate(bankWireManagerVO.getServer_start_date());
            }

            if (functions.isValueNull(bankWireManagerVO.getReversedCoveredStartdate())) {
                settlementDateVO.setReversedStartDate(bankWireManagerVO.getReversedCoveredStartdate());
            }
            else {
                settlementDateVO.setReversedStartDate(bankWireManagerVO.getServer_start_date());
            }

            if (functions.isValueNull(bankWireManagerVO.getChargebackcoveredStartdate())) {
                settlementDateVO.setChargebackStartDate(bankWireManagerVO.getChargebackcoveredStartdate());
            }
            else {
                settlementDateVO.setChargebackStartDate(bankWireManagerVO.getServer_start_date());
            }

            WeeklyPayoutReportVO weeklyPayoutReportVO = new WeeklyPayoutReportVO();
            weeklyPayoutReportVO.setSettlementDateVO(settlementDateVO);
            weeklyPayoutReportVO.setRollingReserveDateVO(rollingReserveDateVO);
            String status = generateWeeklyPayoutReportBasedOnMerchantConsolidated(weeklyPayoutReportVO, terminalVOList,memberId,accountId);
            if ("success".equalsIgnoreCase(status))
            {
                logger.error("successList::::" + successList.size());
                cyclememberlist = bankWireManagerVO.getBankwiremanagerId() + ":" + memberId + ":" + accountId + ":" + "-" + ":" + status + ":" + "Wire Is Created Successfully";
            }
            else
            {
                cyclememberlist = bankWireManagerVO.getBankwiremanagerId() + ":" + memberId + ":" + accountId + ":" + "-" + ":" + status + ":" + "Wire Is Creation Failed";
            }
            stringList.add(cyclememberlist);
            logger.debug(stringList);
            continue;
        }
        return stringList;
    }

    public RollingReserveDateVO getRollingReserveReleaseDate(String memberId,String accountId)//sandip
    {
        RollingReserveDateVO rollingReserveDateVO=new RollingReserveDateVO();
        String reserveReleasedStartDate=payoutDAO.getReserveReleaseStartDate(memberId);
        if(reserveReleasedStartDate==null)
        {
            reserveReleasedStartDate=payoutDAO.getMemberFirstTransactionDate(memberId,accountId);
        }
        String reserveReleasedEndDate=payoutDAO.getRRDateFromBankRollingReserveManager(accountId);
        rollingReserveDateVO.setRollingReserveStartDate(reserveReleasedStartDate);
        rollingReserveDateVO.setRollingReserveEndDate(reserveReleasedEndDate);
        return rollingReserveDateVO;
    }

    public String getMemberFirstTransactionDateOnAccountId(String memberId, String accountId)
    {
        return payoutDAO.getMemberFirstTransactionDateOnAccountId(memberId,accountId);
    }

    public void setUnpaidBalanceAmount(TerminalVO terminalVO, WireAmountVO wireAmountVO)
    {
        payoutDAO.setUnpaidBalanceAmount(terminalVO,wireAmountVO);
    }

    public void setWireAmount(TerminalVO terminalVO, WireAmountVO wireAmountVO)
    {
        payoutDAO.setWireAmount(terminalVO, wireAmountVO);
    }

    public long getWireCount(TerminalVO terminalVO)
    {
        return payoutDAO.getWireCount(terminalVO);
    }

    public TransactionSummaryVO getAuthFailCountAmountByDtstamp(String sDate, TerminalVO terminalVO, String tableName)
    {
        return payoutDAO.getAuthFailCountAmountByDtstamp(sDate, terminalVO, tableName);
    }

    public TransactionSummaryVO getReleaseCountAndAmount(RollingReserveDateVO rollingReserveDateVO, TerminalVO terminalVO, String tableName)
    {
        return payoutDAO.getReleaseCountAndAmount(rollingReserveDateVO, terminalVO, tableName);
    }

    public TransactionSummaryVO getSettledCountAmountByDtstamp(String sDate, TerminalVO terminalVO, String tableName)
    {
        return payoutDAO.getSettledCountAmountByDtstamp(sDate, terminalVO, tableName);
    }

    public TransactionSummaryVO getReversalCountAmountByTimestamp(String sDate, TerminalVO terminalVO, String tableName)
    {
        return payoutDAO.getReversalCountAmountByTimestamp(sDate, terminalVO, tableName);
    }

    public TransactionSummaryVO getChargebackCountAmountByTimestamp(String sDate, TerminalVO terminalVO, String tableName)
    {
        return payoutDAO.getChargebackCountAmountByTimestamp(sDate, terminalVO, tableName);
    }

    public RollingReserveDateVO getRollingReserveReleaseDateOnAccount(String memberId,String accountId,String reportType)//sandip
    {
        RollingReserveDateVO rollingReserveDateVO=new RollingReserveDateVO();
        String reserveReleasedStartDate=payoutDAO.getReserveReleaseStartDateOnAccount(memberId,accountId);
        if("summary".equals(reportType) || reserveReleasedStartDate==null)
        {
            reserveReleasedStartDate=payoutDAO.getMemberFirstTransactionDate(memberId,accountId);
        }
        String reserveReleasedEndDate=payoutDAO.getRRDateFromBankRollingReserveManager(accountId);
        rollingReserveDateVO.setRollingReserveStartDate(reserveReleasedStartDate);
        rollingReserveDateVO.setRollingReserveEndDate(reserveReleasedEndDate);
        return rollingReserveDateVO;
    }

    private String generateWeeklyPayoutReportBasedOnTerminal(WeeklyPayoutReportVO weeklyPayoutReportVO)throws Exception
    {
        TerminalVO terminalVO=weeklyPayoutReportVO.getTerminalVO();
        SettlementDateVO settlementDateVO=weeklyPayoutReportVO.getSettlementDateVO();
        RollingReserveDateVO rollingReserveDateVO=weeklyPayoutReportVO.getRollingReserveDateVO();
        List<ChargeVO> chargeVOList=weeklyPayoutReportVO.getChargeVOList();
        TransactionSummaryVO transactionSummaryVO=weeklyPayoutReportVO.getTransactionSummaryVO();
        List<MerchantRandomChargesVO> merchantRandomChargesVOList=weeklyPayoutReportVO.getMerchantRandomChargesVOList();
        HashMap<String,MerchantRandomChargesVO> merchantRandomChargesVOListNew=weeklyPayoutReportVO.getMerchantRandomChargesVOListNew();

        HashMap<String, String> exchangeRate=weeklyPayoutReportVO.getExchangeRate();
        HashMap<String, String> dynamicCountAmountMap=weeklyPayoutReportVO.getDynamicCountAmountMap();

        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String rollingReserveStartDate=targetFormat.format(targetFormat.parse(rollingReserveDateVO.getRollingReserveStartDate()));
        String rollingReserveEndDate=targetFormat.format(targetFormat.parse(rollingReserveDateVO.getRollingReserveEndDate()));

        String settlementStartDate=targetFormat.format(targetFormat.parse(settlementDateVO.getSettlementStartDate()));
        String settlementEndDate=targetFormat.format(targetFormat.parse(settlementDateVO.getSettlementEndDate()));

        rollingReserveDateVO.setRollingReserveStartDate(rollingReserveStartDate);
        rollingReserveDateVO.setRollingReserveEndDate(rollingReserveEndDate);

        settlementDateVO.setSettlementStartDate(settlementStartDate);
        settlementDateVO.setSettlementEndDate(settlementEndDate);

        String tableName="";//accountUtil.getTableNameSettlement(terminalVO.getAccountId());
        MerchantDetailsVO merchantDetailsVO=merchantDAO.getMemberDetails(terminalVO.getMemberId());

        String contactPerson=merchantDetailsVO.getContact_persons();
        String companyName=merchantDetailsVO.getCompany_name();
        String terminalId=terminalVO.getTerminalId();
        String memberId=terminalVO.getMemberId();
        String accountId=terminalVO.getAccountId();
        String isPoweredBy=merchantDetailsVO.getIsPoweredBy();

        String resStatus="Failed";
        GatewayAccount account= GatewayAccountService.getGatewayAccount(accountId);
        GatewayType gatewayType = GatewayTypeService.getGatewayType(account.getPgTypeId());

        String currency=gatewayType.getCurrency();
        String settlementCurrency=terminalVO.getSettlementCurrency();

        /*String cardType=GatewayAccountService.getCardType(cardTypeId);
        String paymentType=GatewayAccountService.getPaymentTypes(payModeId);*/
        String status=null;

        long totalSuccessCount=0;
        double authfailedAmount=0.00;
        double settledAmount=0.0;
        double reversedAmount = 0.0;
        double chargebackAmount =0.0;
        double wireChargeAmount=0.00;
        double statementChargeAmount=0.00;

        double totalProcessingAmount= 0.0;
        double grossFee=0.0;
        double grossChargesAmount=0.00;
        double otherChargesAmount=0.00;
        double serviceTaxChargesAmount=0.00;
        double reserveGeneratedAmount=0.0;
        double reserveReleaseAmount=0.0;
        double payoutAmount = 0.0;
        double totalChargebackReversal=0.00;
        double reversalChargebackAmount=0.00;
        double previousBalanceAmount=0.00;
        double grossSetupFee=0.0;
        long verifyOrderCount=0;
        boolean negativebalance=false;
        long refundAlertCount=0;
        long retrivalRequestCount=0;
        long fraudulentTransactionCount=0;
        double serviceTaxonFeesChargeAmount=0.00;

        String latestSetupFeeDate=null;

        WireAmountVO wireAmountVO = new WireAmountVO();
        payoutDAO.setUnpaidBalanceAmountMWMOnTerminal(terminalVO, wireAmountVO);

        //Calculate The previous balance Amount consider only the unpaid column field amount
        previousBalanceAmount=wireAmountVO.getUnpaidBalanceAmount();

        HashMap<String,ChargeDetailsVO> chargeDetailsMapOFVOs=new LinkedHashMap<String,ChargeDetailsVO>();
        HashMap<String,ReserveGeneratedVO>  reserveGeneratedVOHashMap=new LinkedHashMap<String,ReserveGeneratedVO>();
        HashMap<String,ReserveRefundVO>     reserveRefundVOHashMap=new LinkedHashMap<String, ReserveRefundVO>() ;
        HashMap<String,CalculatedReserveRefundVO> calculatedReserveRefundVOHashMap=new LinkedHashMap<String, CalculatedReserveRefundVO>();
        HashMap<String,SetupChargeVO> setupChargeVOHashMap=new LinkedHashMap<String, SetupChargeVO>();

        HashMap<String,GrossChargeVO> grossChargeVOMap=new LinkedHashMap<String, GrossChargeVO>();
        HashMap<String,GrossChargeVO> grossChargeVOMapFinal=new LinkedHashMap<String, GrossChargeVO>();
        HashMap<String,ServiceTaxChargeVO> serviceTaxChargeVOHashMap=new LinkedHashMap<String, ServiceTaxChargeVO>();
        HashMap<String,ServiceTaxChargeVO> serviceTaxChargeVOHashMapFinal=new LinkedHashMap<String, ServiceTaxChargeVO>();
        HashMap<String,StatementChargeVO> statementChargeVOHashMap=new LinkedHashMap<String, StatementChargeVO>();
        HashMap<String,SettlementExpensesVO> settlementExpensesVOHashMap=new LinkedHashMap<String, SettlementExpensesVO>();
        HashMap<String,WireChargeVO> wireChargeVOHashMap=new LinkedHashMap<String, WireChargeVO>();
        HashMap<String,MerchantRandomChargesVO> merchantRandomChargesVOHashMap = new LinkedHashMap<String,MerchantRandomChargesVO>();

        HashMap<String,TotalFeesChargesVO> totalFeesChargeVOMap=new LinkedHashMap<String, TotalFeesChargesVO>();
        HashMap<String,TotalFeesChargesVO> totalFeesChargesVOHashMapFinal=new LinkedHashMap<String, TotalFeesChargesVO>();

        HashMap<String,OtherChargesVO> otherChargeVOMap=new LinkedHashMap<String, OtherChargesVO>();
        HashMap<String,OtherChargesVO> otherChargesVOHashMapFinal=new LinkedHashMap<String, OtherChargesVO>();


        WireChargeVO wireChargeVO=null;
        SettlementExpensesVO settlementExpensesVO =null;
        SetupChargeVO setupChargeVO=null;
        ChargeDetailsVO chargeDetailsVO=null;
        StatementChargeVO statementChargeVO=null;

        String currentDate=Functions.convertDateDBFormat(Calendar.getInstance().getTime());
        ChargeManager chargeManager=new ChargeManager();
        ChargeDetailsVO chargebackReverseVO=new ChargeDetailsVO();
        double chargebackReversalAmount=transactionSummaryVO.getChargebackReversedAmount();
        long chargebackReverseCount=transactionSummaryVO.getCountOfChargebackReversed();
        chargebackReverseVO.setCount(chargebackReverseCount);
        chargebackReverseVO.setAmount(chargebackReversalAmount);


        for(ChargeVO chargeVO:chargeVOList)
        {
            if(Charge_keyword.TotalReserveGenerated.toString().equals(chargeVO.getKeyword()) || Charge_keyword.TotalReserveRefunded.toString().equals(chargeVO.getKeyword()) || Charge_keyword.CalculatedReserveRefund.toString().equals(chargeVO.getKeyword()) || Charge_keyword.Wire.toString().equals(chargeVO.getKeyword()) || Charge_keyword.Statement.toString().equals(chargeVO.getKeyword()) || Charge_keyword.GrossBalanceAmount.toString().equals(chargeVO.getKeyword()) || Charge_keyword.Setup.toString().equals(chargeVO.getKeyword()) || Charge_keyword.ServiceTax.toString().equals(chargeVO.getKeyword()) || Charge_keyword.NetFinalAmount.toString().equals(chargeVO.getKeyword()) || Charge_keyword.TotalFees.toString().equals(chargeVO.getKeyword()) || Charge_keyword.OtherCharges.toString().equals(chargeVO.getKeyword()))
            {
                //Get Merchant Charge Version Rate
                String chargeVersionRate=chargeManager.getMerchantChargeVersionRate(chargeVO.getMappingid(),currentDate);
                if(chargeVersionRate!=null)
                {
                    chargeVO.setChargevalue(chargeVersionRate);
                }

                if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.TotalReserveGenerated.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
                {
                    double reserveGeneratedChargeFee=0.00;

                    ReserveGeneratedVO generatedVO= new ReserveGeneratedVO();

                    generatedVO.setCount(transactionSummaryVO.getTotalProcessingCount());
                    generatedVO.setChargeName(chargeVO.getChargename());
                    generatedVO.setValueType(chargeVO.getValuetype());
                    generatedVO.setChargeValue(chargeVO.getChargevalue());
                    generatedVO.setAmount(transactionSummaryVO.getTotalProcessingAmount());
                    reserveGeneratedChargeFee=(transactionSummaryVO.getTotalProcessingAmount()*Double.valueOf(chargeVO.getChargevalue()))/100;
                    generatedVO.setTotal(Functions.roundDBL(reserveGeneratedChargeFee,2));
                    reserveGeneratedAmount=reserveGeneratedAmount+reserveGeneratedChargeFee;
                    reserveGeneratedVOHashMap.put(chargeVO.getChargename(),generatedVO);
                }
                else if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.TotalReserveRefunded.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
                {
                    double reserveRefundedChargeFee=0.0;
                    tableName=accountUtil.getTableNameSettlement(accountId);

                    TransactionSummaryVO amountAndCount=payoutDAO.getReleaseCountAndAmount(rollingReserveDateVO,terminalVO,tableName);

                    ReserveRefundVO refundVO=new ReserveRefundVO();

                    refundVO.setCount(amountAndCount.getCountOfreserveRefund());
                    refundVO.setChargeName(chargeVO.getChargename());
                    refundVO.setValueType(chargeVO.getValuetype());
                    refundVO.setChargeValue(chargeVO.getChargevalue());
                    refundVO.setAmount(amountAndCount.getReserveRefundAmount());
                    reserveRefundedChargeFee=(amountAndCount.getReserveRefundAmount()*Double.valueOf(chargeVO.getChargevalue()))/100;
                    refundVO.setTotal(Functions.roundDBL(reserveRefundedChargeFee,2));

                    reserveReleaseAmount=reserveReleaseAmount+reserveRefundedChargeFee;

                    reserveRefundVOHashMap.put(chargeVO.getChargename(),refundVO);

                }
                else if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.CalculatedReserveRefund.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
                {
                    CalculatedReserveRefundVO calculatedReserveRefundVO= new CalculatedReserveRefundVO();
                    calculatedReserveRefundVO.setCount(totalSuccessCount);
                    calculatedReserveRefundVO.setChargeName(chargeVO.getChargename());
                    calculatedReserveRefundVO.setValueType(chargeVO.getValuetype());
                    calculatedReserveRefundVO.setChargeValue(chargeVO.getChargevalue());
                    //set Amount and Total From outside the loop
                    calculatedReserveRefundVOHashMap.put(chargeVO.getChargename(),calculatedReserveRefundVO);
                }
                else if(chargeVO.getCategory().equals(Charge_category.Others.toString()) && chargeVO.getKeyword().equals(Charge_keyword.Statement.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString()))
                {
                    long currentCounter=1;
                    statementChargeVO=new StatementChargeVO();
                    statementChargeVO.setCount(currentCounter);
                    statementChargeVO.setChargeName(chargeVO.getChargename());
                    statementChargeVO.setChargeValue(chargeVO.getChargevalue());
                    statementChargeVO.setValueType(chargeVO.getValuetype());
                    statementChargeVO.setAmount(0.00);
                    if(statementChargeAmount>0.00){
                        statementChargeAmount=statementChargeAmount+currentCounter*Double.valueOf(chargeVO.getChargevalue());
                    }else
                        statementChargeAmount=currentCounter*Double.valueOf(chargeVO.getChargevalue());
                    statementChargeVO.setTotal(Functions.roundDBL(currentCounter*Double.valueOf(chargeVO.getChargevalue()),2));
                    statementChargeVOHashMap.put(chargeVO.getChargename(),statementChargeVO);
                }
                else if(chargeVO.getCategory().equals(Charge_category.Others.toString()) && chargeVO.getKeyword().equals(Charge_keyword.Wire.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString()))
                {
                    long currentCounter=1;
                    wireChargeVO=new WireChargeVO();
                    wireChargeVO.setCount(currentCounter);
                    wireChargeVO.setChargeName(chargeVO.getChargename());
                    wireChargeVO.setChargeValue(chargeVO.getChargevalue());
                    wireChargeVO.setValueType(chargeVO.getValuetype());
                    wireChargeVO.setAmount(0.00);
                    if(wireChargeAmount>0.00){
                        wireChargeAmount=wireChargeAmount+currentCounter*Double.valueOf(chargeVO.getChargevalue());
                    }else
                        wireChargeAmount=currentCounter*Double.valueOf(chargeVO.getChargevalue());

                    wireChargeVO.setTotal(Functions.roundDBL(currentCounter*Double.valueOf(chargeVO.getChargevalue()), 2));
                    wireChargeVOHashMap.put(chargeVO.getChargename(), wireChargeVO);
                }
                if(chargeVO.getCategory().equals(Charge_category.Success.toString()) && chargeVO.getKeyword().equals(Charge_keyword.NetFinalAmount.toString())&&chargeVO.getSubkeyword().equals(Charge_subKeyword.Amount.toString()))
                {
                    settlementExpensesVO =new SettlementExpensesVO();
                    settlementExpensesVO.setChargeName(chargeVO.getChargename());
                    settlementExpensesVO.setChargeValue(chargeVO.getChargevalue());
                    settlementExpensesVO.setValueType(chargeVO.getValuetype());
                    settlementExpensesVO.setChargeid(chargeVO.getChargeid());
                    settlementExpensesVO.setNegativebalance(chargeVO.getNegativebalance());
                    settlementExpensesVOHashMap.put(chargeVO.getChargename(),settlementExpensesVO);
                }
                else if(chargeVO.getCategory().equals(Charge_category.Others.toString()) && chargeVO.getKeyword().equals(Charge_keyword.Setup.toString())&&chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString()))
                {
                    setupChargeVO= applySetupChargeUptoSettledDate(chargeVO, terminalVO,settlementDateVO);
                    if(setupChargeVO!=null)
                    {
                        grossSetupFee=grossSetupFee+setupChargeVO.getTotal();
                        latestSetupFeeDate=setupChargeVO.getLastChargeDate();
                    }
                    setupChargeVOHashMap.put(setupChargeVO.getChargeName(), setupChargeVO);
                }
                else if(chargeVO.getCategory().equals(Charge_category.Others.toString()) && chargeVO.getKeyword().equals(Charge_keyword.GrossBalanceAmount.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Amount.toString()))
                {
                    GrossChargeVO grossChargeVO=new GrossChargeVO();
                    grossChargeVO.setChargeName(chargeVO.getChargename());
                    grossChargeVO.setChargeValue(chargeVO.getChargevalue());
                    grossChargeVO.setValueType(chargeVO.getValuetype());
                    grossChargeVOMap.put(chargeVO.getChargename(), grossChargeVO);
                }
                else if(chargeVO.getCategory().equals(Charge_category.Others.toString()) && chargeVO.getKeyword().equals(Charge_keyword.ServiceTax.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Amount.toString()))
                {
                    ServiceTaxChargeVO serviceTaxChargeVO = new ServiceTaxChargeVO();
                    serviceTaxChargeVO.setChargeName(chargeVO.getChargename());
                    serviceTaxChargeVO.setChargeValue(chargeVO.getChargevalue());
                    serviceTaxChargeVO.setValueType(chargeVO.getValuetype());
                    serviceTaxChargeVOHashMap.put(chargeVO.getChargename(),serviceTaxChargeVO);
                }
                else if(chargeVO.getCategory().equals(Charge_category.Others.toString()) && chargeVO.getKeyword().equals(Charge_keyword.TotalFees.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Amount.toString()))
                {
                    TotalFeesChargesVO totalFeesChargesVO = new TotalFeesChargesVO();
                    totalFeesChargesVO.setChargeName(chargeVO.getChargename());
                    totalFeesChargesVO.setChargeValue(chargeVO.getChargevalue());
                    totalFeesChargesVO.setValueType(chargeVO.getValuetype());
                    totalFeesChargeVOMap.put(chargeVO.getChargename(),totalFeesChargesVO);
                }
                else if(chargeVO.getCategory().equals(Charge_category.Others.toString()) && chargeVO.getKeyword().equals(Charge_keyword.OtherCharges.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Amount.toString()))
                {
                    OtherChargesVO otherChargesVO = new OtherChargesVO();
                    otherChargesVO.setChargeName(chargeVO.getChargename());
                    otherChargesVO.setChargeValue(chargeVO.getChargevalue());
                    otherChargesVO.setValueType(chargeVO.getValuetype());
                    otherChargeVOMap.put(chargeVO.getChargename(),otherChargesVO);
                }

            }
            else
            {
                //Get Merchant Charge Version Rate
                String chargeVersionRate=chargeManager.getMerchantChargeVersionRate(chargeVO.getMappingid(),currentDate);
                if(chargeVersionRate!=null)
                {
                    chargeVO.setChargevalue(chargeVersionRate);
                }

                chargeDetailsVO= applyChargeOnTerminal(terminalVO, chargeVO, settlementDateVO,tableName,transactionSummaryVO,dynamicCountAmountMap);
                if(chargeDetailsVO.getChargeName()!=null)
                {

                    grossFee=grossFee+chargeDetailsVO.getTotal();
                    String terminalName=chargeVO.getTerminalid()+"-"+chargeVO.getPaymentName()+"-"+chargeVO.getCardType();
                    chargeDetailsMapOFVOs.put(chargeDetailsVO.getChargeName()+":"+terminalName,chargeDetailsVO);
                    if(chargeVO.getKeyword().equals(Charge_keyword.Reversed.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString()))
                    {
                        reversedAmount=reversedAmount+chargeDetailsVO.getAmount();
                    }
                    if(chargeVO.getKeyword().equals(Charge_keyword.Payout.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString()))
                    {
                        payoutAmount=payoutAmount+chargeDetailsVO.getAmount();
                    }
                    if(chargeVO.getKeyword().equals(Charge_keyword.Chargeback.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString()))
                    {
                        chargebackAmount=chargebackAmount+chargeDetailsVO.getAmount();
                        chargebackReverseVO.setChargeValue(chargeDetailsVO.getChargeValue());
                        chargebackReverseVO.setTotal(chargebackReverseCount*Double.valueOf(chargeDetailsVO.getChargeValue()));
                    }
                    if(chargeVO.getKeyword().equals(Charge_keyword.VerifyOrder.toString()))
                    {
                        verifyOrderCount=chargeDetailsVO.getCount();
                    }
                    if(chargeVO.getKeyword().equals(Charge_keyword.RefundAlert.toString()))
                    {
                        refundAlertCount=chargeDetailsVO.getCount();
                    }
                    if(chargeVO.getKeyword().equals(Charge_keyword.RetrivalRequest.toString()))
                    {
                        retrivalRequestCount=chargeDetailsVO.getCount();
                    }
                    if(chargeVO.getKeyword().equals(Charge_keyword.FraudulentTransaction.toString()))
                    {
                        fraudulentTransactionCount=chargeDetailsVO.getCount();
                    }

                }
            }
        }



        //adding the setup gross fee in gross fee
        grossFee=grossFee+grossSetupFee-chargebackReverseVO.getTotal();
        totalProcessingAmount=transactionSummaryVO.getTotalProcessingAmount();
        reversalChargebackAmount=chargebackAmount+reversedAmount;
        //calculate gross amount here coz need to pass gross Amount to Calculate gross level charges

        if(totalFeesChargeVOMap.size()>0)
        {
            String chargeName=null;
            Set set=totalFeesChargeVOMap.keySet();
            Iterator itr=set.iterator();
            while(itr.hasNext())
            {
                chargeName=(String)itr.next();
                TotalFeesChargesVO totalFeesChargesVO=calculateTotalFeesCharge(chargeName, totalFeesChargeVOMap, grossFee);
                serviceTaxonFeesChargeAmount=serviceTaxonFeesChargeAmount+totalFeesChargesVO.getTotal();
                totalFeesChargesVOHashMapFinal.put(totalFeesChargesVO.getChargeName(), totalFeesChargesVO);
            }
        }

        double grossDeduct=-1*(grossFee+serviceTaxonFeesChargeAmount+chargebackAmount+reversedAmount+reserveGeneratedAmount+payoutAmount);
        double grossAmount=totalProcessingAmount+grossDeduct;

        grossAmount=Functions.roundDBL(grossAmount,2);

        if(calculatedReserveRefundVOHashMap.size()>0)
        {
            Set set=calculatedReserveRefundVOHashMap.keySet();
            Iterator itr=set.iterator();
            while(itr.hasNext())
            {
                String chargeName=itr.next().toString();
                ReserveRefundVO refundVO= applyChargeOnReserveReleaseAmount(chargeName,calculatedReserveRefundVOHashMap,reserveReleaseAmount);
                reserveReleaseAmount=reserveReleaseAmount-refundVO.getTotal();
                reserveRefundVOHashMap.put(chargeName,refundVO);
            }
        }
        if(grossChargeVOMap.size()>0)
        {
            String chargeName=null;
            Set set=grossChargeVOMap.keySet();
            Iterator itr=set.iterator();
            while(itr.hasNext())
            {
                chargeName=(String)itr.next();
                GrossChargeVO grossChargesVO=applyGrossCharge(chargeName, grossChargeVOMap, grossAmount);
                grossChargesAmount=grossChargesAmount+grossChargesVO.getTotal();
                grossChargeVOMapFinal.put(grossChargesVO.getChargeName(), grossChargesVO);
            }
        }

        if(otherChargeVOMap.size()>0)
        {
            String chargeName=null;
            Set set=otherChargesVOHashMapFinal.keySet();
            Iterator itr=set.iterator();
            while(itr.hasNext())
            {
                chargeName=(String)itr.next();
                OtherChargesVO otherChargesVO=CalculateOthercharges(chargeName, otherChargeVOMap, grossAmount);
                otherChargesAmount=otherChargesAmount+otherChargesVO.getTotal();
                otherChargesVOHashMapFinal.put(otherChargesVO.getChargeName(), otherChargesVO);
            }
        }
        if(serviceTaxChargeVOHashMap.size()>0)
        {
            String chargeName=null;
            Set set=serviceTaxChargeVOHashMap.keySet();
            Iterator itr=set.iterator();
            while(itr.hasNext())
            {
                chargeName=(String)itr.next();
                ServiceTaxChargeVO serviceTaxChargeVO=calculateServiveTax(chargeName,serviceTaxChargeVOHashMap,grossFee+wireChargeAmount);
                serviceTaxChargesAmount=serviceTaxChargesAmount+serviceTaxChargeVO.getTotal();
                serviceTaxChargeVOHashMapFinal.put(serviceTaxChargeVO.getChargeName(), serviceTaxChargeVO);
            }
        }

        /*File Updated:28-07-2015 by sandip
         *Retriving Random Charges Total amount
            1)to deducting same amount from netfinal if charge type=Deducted
            1)to add same amount in netfinal if charge type=Added
            2)to send amount to display in report pdf file as miscellnious adjustment
       */
        double merchantRandomChargesGrossAmount=0.00;
        double deductedAmount=0.00;
        double addedAmount=0.00;
        if(merchantRandomChargesVOListNew!=null && merchantRandomChargesVOListNew.size()>0)
        {
            String chargeName=null;
            Set set=merchantRandomChargesVOListNew.keySet();
            Iterator itr=set.iterator();
            while(itr.hasNext())
            {
                chargeName=(String)itr.next();
                MerchantRandomChargesVO detailsVO=merchantRandomChargesVOListNew.get(chargeName);
                MerchantRandomChargesVO merchantRandomChargesVO=new MerchantRandomChargesVO();
                merchantRandomChargesVO.setChargeCounter(0);
                merchantRandomChargesVO.setChargeValue(Double.valueOf(detailsVO.getChargeValue()));
                merchantRandomChargesVO.setChargeName(detailsVO.getChargeName());
                merchantRandomChargesVO.setChargeValueType(detailsVO.getChargeValueType());
                double total=0.00;
                if(detailsVO.getChargeValueType().equalsIgnoreCase("Percentage"))
                {
                    total = grossAmount * Double.valueOf(detailsVO.getChargeValue()) / 100;
                    merchantRandomChargesVO.setChargeAmount(total);
                }else{
                    total = Double.valueOf(detailsVO.getChargeValue());
                    merchantRandomChargesVO.setChargeAmount(total);
                }
                if("Deducted".equals(detailsVO.getChargeType()))
                {
                    deductedAmount=deductedAmount+total;
                }
                else
                {
                    addedAmount=addedAmount+total;
                }
                merchantRandomChargesVOHashMap.put(merchantRandomChargesVO.getChargeName(), merchantRandomChargesVO);
            }
            merchantRandomChargesGrossAmount=addedAmount-deductedAmount;
        }
        /*for(MerchantRandomChargesVO merchantRandomChargesVO:merchantRandomChargesVOList)
        {
            if("Deducted".equals(merchantRandomChargesVO.getChargeType()))
            {
                //System.out.println(merchantRandomChargesVO.getChargeType());
                deductedAmount=deductedAmount+merchantRandomChargesVO.getChargeValue();
            }
            else
            {
                addedAmount=addedAmount+merchantRandomChargesVO.getChargeValue();
            }
        }
        merchantRandomChargesGrossAmount=addedAmount-deductedAmount;*/
        //System.out.println("merchant random charges amount====="+merchantRandomChargesGrossAmount);

        //calculate total funded to bank coz need to pass the totalFundedToBank to generate pdf file
        totalChargebackReversal=chargebackReverseVO.getAmount();
        double refundReversedAmount=transactionSummaryVO.getRefundReverseAmount();
        double totalFundedToBank=(reserveReleaseAmount+grossAmount+previousBalanceAmount+merchantRandomChargesGrossAmount+totalChargebackReversal+refundReversedAmount)-((wireChargeAmount+statementChargeAmount)+(-1*grossChargesAmount)+serviceTaxChargesAmount+otherChargesAmount);
        double minPayoutAmount=Double.valueOf(terminalVO.getMinPayoutAmount());
        boolean isWireFeeRequired=false;
        if(wireChargeVO!=null){
            if(totalFundedToBank>=minPayoutAmount){
                isWireFeeRequired=true;
            }
        }

        if(wireChargeVO !=null && !isWireFeeRequired){
            totalFundedToBank=totalFundedToBank+wireChargeAmount;
        }

        double exchangeRateDbl=1.00;
        if(exchangeRate.containsKey(settlementCurrency)){
            exchangeRateDbl=Double.valueOf(exchangeRate.get(settlementCurrency));
        }

        double totalFundedToBankBeforeSettlementExpenses=totalFundedToBank;
        if(settlementExpensesVOHashMap!=null && settlementExpensesVOHashMap.size()>0){
            Set set=settlementExpensesVOHashMap.keySet();
            Iterator iterator=set.iterator();
            while (iterator.hasNext())
            {
                String chargename = (String) iterator.next();
                SettlementExpensesVO settlementExpensesVO1 = settlementExpensesVOHashMap.get(chargename);
                double settlementExpensesAmount=0;
                ChargesDAO ChargesDAO = new ChargesDAO();
                String flag="";

                negativebalance=ChargesDAO.getMerchantChargeNegativeBalance(memberId,accountId,terminalId,settlementExpensesVO1.getChargeid());

                if (totalFundedToBank < 0)
                {
                    if(settlementExpensesVO1.getNegativebalance().equals("Y")){
                        flag=" [Positive Settlement Charges]";
                    }
                    else{
                        flag=" [No Settlement Charges]";
                    }
                    if(negativebalance)
                    {
                        settlementExpensesAmount = -((totalFundedToBankBeforeSettlementExpenses * Double.valueOf(settlementExpensesVO1.getChargeValue()) / 100));
                    }
                    else{
                        settlementExpensesVO1.setChargeValue("0.00");
                        settlementExpensesAmount = totalFundedToBankBeforeSettlementExpenses * Double.valueOf(settlementExpensesVO1.getChargeValue()) / 100;
                    }
                }else{
                    settlementExpensesAmount = totalFundedToBankBeforeSettlementExpenses * Double.valueOf(settlementExpensesVO1.getChargeValue()) / 100;
                }
                settlementExpensesVO=new SettlementExpensesVO();
                settlementExpensesVO.setChargeName(chargename+flag);
                settlementExpensesVO.setChargeValue(settlementExpensesVO1.getChargeValue());
                settlementExpensesVO.setAmount(totalFundedToBankBeforeSettlementExpenses);
                settlementExpensesVO.setTotal(settlementExpensesAmount);
                settlementExpensesVOHashMap.put(chargename,settlementExpensesVO);
                if (totalFundedToBank < 0)
                {
                    if(negativebalance)
                    {
                        if (settlementExpensesAmount > 0)
                        {
                            totalFundedToBank = totalFundedToBank + settlementExpensesAmount;

                        }
                        else
                        {
                            totalFundedToBank = totalFundedToBank + (-1 * settlementExpensesAmount);
                        }
                    }else{
                        if (settlementExpensesAmount > 0)
                        {
                            totalFundedToBank = totalFundedToBank - settlementExpensesAmount;

                        }
                        else
                        {
                            totalFundedToBank = totalFundedToBank - (-1 * settlementExpensesAmount);
                        }
                    }
                }else{
                    if (settlementExpensesAmount > 0)
                    {
                        totalFundedToBank = totalFundedToBank - settlementExpensesAmount;

                    }
                    else
                    {
                        totalFundedToBank = totalFundedToBank - (-1 * settlementExpensesAmount);
                    }
                }

            }
        }
        /*if(settlementExpensesVO !=null){
            double settlementExpensesAmount=totalFundedToBank*Double.valueOf(settlementExpensesVO.getChargeValue())/100;
            settlementExpensesVO.setAmount(totalFundedToBankBeforeSettlementExpenses);
            settlementExpensesVO.setTotal(settlementExpensesAmount);
            if(settlementExpensesAmount>0){
                totalFundedToBank=totalFundedToBank-settlementExpensesAmount;
            }
            else{
                totalFundedToBank=totalFundedToBank-(-1*settlementExpensesAmount);
            }
        }*/

        double totalFundedInSettlementCurrency=exchangeRateDbl*totalFundedToBank;
        totalFundedToBank=Functions.roundDBL(totalFundedToBank,2);
        if(chargeDetailsMapOFVOs.size()>=0)
        {
            String pdfFilePath=getPDFReportFile(totalFundedToBank,grossAmount,previousBalanceAmount,reversedAmount,chargebackAmount,payoutAmount,chargebackReverseVO,refundReversedAmount,totalProcessingAmount,grossFee,reserveGeneratedAmount,reserveReleaseAmount,companyName,contactPerson,/*cardType,paymentType,*/terminalVO,chargeDetailsMapOFVOs,reserveGeneratedVOHashMap,reserveRefundVOHashMap,settlementDateVO,rollingReserveDateVO,wireChargeVOHashMap,grossChargeVOMapFinal,setupChargeVOHashMap,serviceTaxChargeVOHashMapFinal,merchantRandomChargesVOListNew,merchantRandomChargesGrossAmount,currency,isPoweredBy,settlementCurrency,totalFundedInSettlementCurrency,exchangeRateDbl,statementChargeVOHashMap,isWireFeeRequired, settlementExpensesVOHashMap,totalFundedToBankBeforeSettlementExpenses,totalFeesChargesVOHashMapFinal,grossFee,otherChargesVOHashMapFinal);
            String settleTransExcelFilePath= createMerchantTransactionFile(settlementDateVO, tableName, terminalVO);
            String rollingReserveExcelFilePath= createRollingReserveTransactionFile(rollingReserveDateVO, terminalVO);
            logger.error("rollingReserveExcelFilePath::::"+rollingReserveExcelFilePath);

            WireVO wireVO=new WireVO();
            wireVO.setFirstDate(settlementDateVO.getSettlementStartDate());
            wireVO.setLastDate(settlementDateVO.getSettlementEndDate());
            wireVO.setCurrency(currency);
            wireVO.setMarkForDeletion("N");
            wireVO.setWireAmount(totalProcessingAmount);
            wireVO.setWireBalanceAmount(grossAmount);
            wireVO.setNetFinalAmount(totalFundedToBank);
            wireVO.setUnpaidAmount(0.00);
            wireVO.setStatus("unpaid");
            wireVO.setSettlementReportFilePath(pdfFilePath);
            wireVO.setSettledTransactionFilePath(settleTransExcelFilePath);
            wireVO.setReserveReleasedUptoDate(rollingReserveDateVO.getRollingReserveEndDate());
            wireVO.setRollingReserveIncluded("Y");
            wireVO.setTerminalVO(terminalVO);
            wireVO.setSettlementCycleNo(settlementDateVO.getSettlementcycleNumber());
            wireVO.setDeclinedcoverdateupto(settlementDateVO.getDeclinedEndDate());
            wireVO.setReversedcoverdateupto(settlementDateVO.getReversedEndDate());
            wireVO.setChargebackcoverdateupto(settlementDateVO.getChargebackEndDate());
            wireVO.setRollingReserveFilePath(rollingReserveExcelFilePath);
            wireVO.setReportid(payoutDAO.getreportid());
            status=payoutDAO.generateSettlementCycleWire(wireVO);
            if("success".equals(status))
            {
                String mailStatus="Failed";

                //Call MailService To Give Alert Merchant To See payout report in merchant Back Office
                MailService mailService=new MailService();
                AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
                HashMap cbCountRecord=new HashMap();
                cbCountRecord.put(MailPlaceHolder.TOID,terminalVO.getMemberId());
                cbCountRecord.put(MailPlaceHolder.FROMDATE,settlementStartDate.split(" ")[0]);
                cbCountRecord.put(MailPlaceHolder.TODATE,settlementEndDate.split(" ")[0]);
                if(merchantRandomChargesVOList.size()>0)
                {
                    cbCountRecord.put(MailPlaceHolder.MULTIPALTRANSACTION,mailService.getDetailTableForRandomCharges(merchantRandomChargesVOList));
                }
                asynchronousMailService.sendMerchantSignup(MailEventEnum.MERCHANT_PAYOUT_ALERT_MAIL,cbCountRecord);
                mailStatus="success";
                boolean isMeberCycleYUpdated=payoutDAO.updateMemberCycleDetailsWithAccountId(memberId, accountId, terminalId, verifyOrderCount, refundAlertCount,retrivalRequestCount,latestSetupFeeDate,settlementDateVO.getSettlementcycleNumber(),fraudulentTransactionCount, null);
                if(isMeberCycleYUpdated)
                {
                    logger.debug("member_settlementcycle_details updated successfully");
                }

                boolean isBankMerchantSettlementMasterUpdated=payoutDAO.merchantSettlementMasterEntry(memberId,accountId,terminalId,settlementDateVO.getSettlementcycleNumber(),"N", null);
                if(isBankMerchantSettlementMasterUpdated)
                {
                    boolean ispayoutcronupdated=payoutDAO.updatePayoutCronExecutedFlag(accountId,settlementDateVO.getSettlementcycleNumber());
                    if(ispayoutcronupdated)
                    {
                        resStatus="Success";
                    }
                }
            }
        }
        return resStatus;
    }
    private String generateWeeklyPayoutReportBasedOnTerminalMerchant(WeeklyPayoutReportVO weeklyPayoutReportVO)throws Exception
    {
        TerminalVO terminalVO=weeklyPayoutReportVO.getTerminalVO();
        SettlementDateVO settlementDateVO=weeklyPayoutReportVO.getSettlementDateVO();
        RollingReserveDateVO rollingReserveDateVO=weeklyPayoutReportVO.getRollingReserveDateVO();
        List<ChargeVO> chargeVOList=weeklyPayoutReportVO.getChargeVOList();
        TransactionSummaryVO transactionSummaryVO=weeklyPayoutReportVO.getTransactionSummaryVO();
        HashMap<String,MerchantRandomChargesVO> merchantRandomChargesVOListNew=weeklyPayoutReportVO.getMerchantRandomChargesVOListNew();
        List<MerchantRandomChargesVO> merchantRandomChargesVOList=weeklyPayoutReportVO.getMerchantRandomChargesVOList();

        HashMap<String, String> exchangeRate=weeklyPayoutReportVO.getExchangeRate();
        HashMap<String, String> dynamicCountAmountMap=weeklyPayoutReportVO.getDynamicCountAmountMap();

        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String rollingReserveStartDate=targetFormat.format(targetFormat.parse(rollingReserveDateVO.getRollingReserveStartDate()));
        String rollingReserveEndDate=targetFormat.format(targetFormat.parse(rollingReserveDateVO.getRollingReserveEndDate()));

        String settlementStartDate=targetFormat.format(targetFormat.parse(settlementDateVO.getSettlementStartDate()));
        String settlementEndDate=targetFormat.format(targetFormat.parse(settlementDateVO.getSettlementEndDate()));

        rollingReserveDateVO.setRollingReserveStartDate(rollingReserveStartDate);
        rollingReserveDateVO.setRollingReserveEndDate(rollingReserveEndDate);

        settlementDateVO.setSettlementStartDate(settlementStartDate);
        settlementDateVO.setSettlementEndDate(settlementEndDate);

        String tableName="";//accountUtil.getTableNameSettlement(terminalVO.getAccountId());
        MerchantDetailsVO merchantDetailsVO=merchantDAO.getMemberDetails(terminalVO.getMemberId());

        String contactPerson=merchantDetailsVO.getContact_persons();
        String companyName=merchantDetailsVO.getCompany_name();
        String terminalId=terminalVO.getTerminalId();
        String memberId=terminalVO.getMemberId();
        String accountId=terminalVO.getAccountId();
        String isPoweredBy=merchantDetailsVO.getIsPoweredBy();

        String resStatus="Failed";
        GatewayAccount account= GatewayAccountService.getGatewayAccount(accountId);
        GatewayType gatewayType = GatewayTypeService.getGatewayType(account.getPgTypeId());

        String currency=gatewayType.getCurrency();
        String settlementCurrency=terminalVO.getSettlementCurrency();

        /*String cardType=GatewayAccountService.getCardType(cardTypeId);
        String paymentType=GatewayAccountService.getPaymentTypes(payModeId);*/
        String status=null;

        long totalSuccessCount=0;
        double authfailedAmount=0.00;
        double settledAmount=0.0;
        double reversedAmount = 0.0;
        double payoutAmount = 0.0;
        double totalChargebackReversal=0.00;
        double chargebackAmount =0.0;
        double wireChargeAmount=0.00;
        double statementChargeAmount=0.00;

        double totalProcessingAmount= 0.0;
        double grossFee=0.0;
        double grossChargesAmount=0.00;
        double otherChargesAmount=0.00;
        double serviceTaxonFeesChargeAmount=0.00;
        double serviceTaxChargesAmount=0.00;
        double reserveGeneratedAmount=0.0;
        double reserveReleaseAmount=0.0;
        double reversalChargebackAmount=0.00;
        double previousBalanceAmount=0.00;
        double grossSetupFee=0.0;
        long verifyOrderCount=0;
        boolean negativebalance=false;
        long refundAlertCount=0;
        long retrivalRequestCount=0;
        long fraudulentTransactionCount=0;

        String latestSetupFeeDate=null;

        WireAmountVO wireAmountVO = new WireAmountVO();
        payoutDAO.setUnpaidBalanceAmountMWMOnTerminal(terminalVO, wireAmountVO);

        //Calculate The previous balance Amount consider only the unpaid column field amount
        previousBalanceAmount=wireAmountVO.getUnpaidBalanceAmount();

        HashMap<String,ChargeDetailsVO> chargeDetailsMapOFVOs=new LinkedHashMap<String,ChargeDetailsVO>();
        HashMap<String,ReserveGeneratedVO>  reserveGeneratedVOHashMap=new LinkedHashMap<String,ReserveGeneratedVO>();
        HashMap<String,ReserveRefundVO>     reserveRefundVOHashMap=new LinkedHashMap<String, ReserveRefundVO>() ;
        HashMap<String,CalculatedReserveRefundVO> calculatedReserveRefundVOHashMap=new LinkedHashMap<String, CalculatedReserveRefundVO>();
        HashMap<String,SetupChargeVO> setupChargeVOHashMap=new LinkedHashMap<String, SetupChargeVO>();
        HashMap<String,StatementChargeVO> statementChargeVOHashMap=new LinkedHashMap<String, StatementChargeVO>();
        HashMap<String,SettlementExpensesVO> settlementExpensesVOHashMap=new LinkedHashMap<String, SettlementExpensesVO>();
        HashMap<String,WireChargeVO> wireChargeVOHashMap=new LinkedHashMap<String, WireChargeVO>();
        HashMap<String,MerchantRandomChargesVO> merchantRandomChargesVOHashMap=new LinkedHashMap<String, MerchantRandomChargesVO>();


        HashMap<String,GrossChargeVO> grossChargeVOMap=new LinkedHashMap<String, GrossChargeVO>();
        HashMap<String,GrossChargeVO> grossChargeVOMapFinal=new LinkedHashMap<String, GrossChargeVO>();

        HashMap<String,ServiceTaxChargeVO> serviceTaxChargeVOHashMap=new LinkedHashMap<String, ServiceTaxChargeVO>();
        HashMap<String,ServiceTaxChargeVO> serviceTaxChargeVOHashMapFinal=new LinkedHashMap<String, ServiceTaxChargeVO>();

        HashMap<String,TotalFeesChargesVO> totalFeesChargeVOMap=new LinkedHashMap<String, TotalFeesChargesVO>();
        HashMap<String,TotalFeesChargesVO> totalFeesChargesVOHashMapFinal=new LinkedHashMap<String, TotalFeesChargesVO>();

        HashMap<String,OtherChargesVO> otherChargeVOMap=new LinkedHashMap<String, OtherChargesVO>();
        HashMap<String,OtherChargesVO> otherChargesVOHashMapFinal=new LinkedHashMap<String, OtherChargesVO>();


        WireChargeVO wireChargeVO=null;
        SettlementExpensesVO settlementExpensesVO =null;
        SetupChargeVO setupChargeVO=null;
        ChargeDetailsVO chargeDetailsVO=null;
        StatementChargeVO statementChargeVO=null;

        String currentDate=Functions.convertDateDBFormat(Calendar.getInstance().getTime());
        ChargeManager chargeManager=new ChargeManager();
        ChargeDetailsVO chargebackReverseVO=new ChargeDetailsVO();
        double chargebackReversalAmount=transactionSummaryVO.getChargebackReversedAmount();
        long chargebackReverseCount=transactionSummaryVO.getCountOfChargebackReversed();
        chargebackReverseVO.setCount(chargebackReverseCount);
        chargebackReverseVO.setAmount(chargebackReversalAmount);

        for(ChargeVO chargeVO:chargeVOList)
        {
            if(Charge_keyword.TotalReserveGenerated.toString().equals(chargeVO.getKeyword()) || Charge_keyword.TotalReserveRefunded.toString().equals(chargeVO.getKeyword()) || Charge_keyword.CalculatedReserveRefund.toString().equals(chargeVO.getKeyword()) || Charge_keyword.Wire.toString().equals(chargeVO.getKeyword()) || Charge_keyword.Statement.toString().equals(chargeVO.getKeyword()) || Charge_keyword.GrossBalanceAmount.toString().equals(chargeVO.getKeyword()) || Charge_keyword.Setup.toString().equals(chargeVO.getKeyword()) || Charge_keyword.ServiceTax.toString().equals(chargeVO.getKeyword()) || Charge_keyword.NetFinalAmount.toString().equals(chargeVO.getKeyword()) || Charge_keyword.TotalFees.toString().equals(chargeVO.getKeyword()) || Charge_keyword.OtherCharges.toString().equals(chargeVO.getKeyword()))
            {
                //Get Merchant Charge Version Rate
                String chargeVersionRate=chargeManager.getMerchantChargeVersionRate(chargeVO.getMappingid(),currentDate);
                if(chargeVersionRate!=null)
                {
                    chargeVO.setChargevalue(chargeVersionRate);
                }

                if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.TotalReserveGenerated.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
                {
                    double reserveGeneratedChargeFee=0.00;

                    ReserveGeneratedVO generatedVO= new ReserveGeneratedVO();

                    generatedVO.setCount(transactionSummaryVO.getTotalProcessingCount());
                    generatedVO.setChargeName(chargeVO.getChargename());
                    generatedVO.setValueType(chargeVO.getValuetype());
                    generatedVO.setChargeValue(chargeVO.getChargevalue());
                    generatedVO.setAmount(transactionSummaryVO.getTotalProcessingAmount());
                    reserveGeneratedChargeFee=(transactionSummaryVO.getTotalProcessingAmount()*Double.valueOf(chargeVO.getChargevalue()))/100;
                    generatedVO.setTotal(Functions.roundDBL(reserveGeneratedChargeFee,2));
                    reserveGeneratedAmount=reserveGeneratedAmount+reserveGeneratedChargeFee;
                    reserveGeneratedVOHashMap.put(chargeVO.getChargename(),generatedVO);
                }
                else if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.TotalReserveRefunded.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
                {
                    double reserveRefundedChargeFee=0.0;
                    tableName=accountUtil.getTableNameSettlement(accountId);

                    TransactionSummaryVO amountAndCount=payoutDAO.getReleaseCountAndAmount(rollingReserveDateVO,terminalVO,tableName);

                    ReserveRefundVO refundVO=new ReserveRefundVO();

                    refundVO.setCount(amountAndCount.getCountOfreserveRefund());
                    refundVO.setChargeName(chargeVO.getChargename());
                    refundVO.setValueType(chargeVO.getValuetype());
                    refundVO.setChargeValue(chargeVO.getChargevalue());
                    refundVO.setAmount(amountAndCount.getReserveRefundAmount());
                    reserveRefundedChargeFee=(amountAndCount.getReserveRefundAmount()*Double.valueOf(chargeVO.getChargevalue()))/100;
                    refundVO.setTotal(Functions.roundDBL(reserveRefundedChargeFee,2));

                    reserveReleaseAmount=reserveReleaseAmount+reserveRefundedChargeFee;

                    reserveRefundVOHashMap.put(chargeVO.getChargename(),refundVO);

                }
                else if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.CalculatedReserveRefund.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
                {
                    CalculatedReserveRefundVO calculatedReserveRefundVO= new CalculatedReserveRefundVO();
                    calculatedReserveRefundVO.setCount(totalSuccessCount);
                    calculatedReserveRefundVO.setChargeName(chargeVO.getChargename());
                    calculatedReserveRefundVO.setValueType(chargeVO.getValuetype());
                    calculatedReserveRefundVO.setChargeValue(chargeVO.getChargevalue());
                    //set Amount and Total From outside the loop
                    calculatedReserveRefundVOHashMap.put(chargeVO.getChargename(),calculatedReserveRefundVO);
                }
                else if(chargeVO.getCategory().equals(Charge_category.Others.toString()) && chargeVO.getKeyword().equals(Charge_keyword.Statement.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString()))
                {
                    long currentCounter=1;
                    statementChargeVO=new StatementChargeVO();
                    statementChargeVO.setCount(currentCounter);
                    statementChargeVO.setChargeName(chargeVO.getChargename());
                    statementChargeVO.setChargeValue(chargeVO.getChargevalue());
                    statementChargeVO.setValueType(chargeVO.getValuetype());
                    statementChargeVO.setAmount(0.00);
                    if(statementChargeAmount>0.00){
                        statementChargeAmount=statementChargeAmount+currentCounter*Double.valueOf(chargeVO.getChargevalue());
                    }else
                        statementChargeAmount=currentCounter*Double.valueOf(chargeVO.getChargevalue());
                    statementChargeVO.setTotal(Functions.roundDBL(currentCounter*Double.valueOf(chargeVO.getChargevalue()),2));
                    statementChargeVOHashMap.put(chargeVO.getChargename(),statementChargeVO);
                }
                else if(chargeVO.getCategory().equals(Charge_category.Others.toString()) && chargeVO.getKeyword().equals(Charge_keyword.Wire.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString()))
                {
                    long currentCounter=1;
                    wireChargeVO=new WireChargeVO();
                    wireChargeVO.setCount(currentCounter);
                    wireChargeVO.setChargeName(chargeVO.getChargename());
                    wireChargeVO.setChargeValue(chargeVO.getChargevalue());
                    wireChargeVO.setValueType(chargeVO.getValuetype());
                    wireChargeVO.setAmount(0.00);
                    if(wireChargeAmount>0.00){
                        wireChargeAmount=wireChargeAmount+currentCounter*Double.valueOf(chargeVO.getChargevalue());
                    }else
                        wireChargeAmount=currentCounter*Double.valueOf(chargeVO.getChargevalue());
                    wireChargeVO.setTotal(Functions.roundDBL(currentCounter*Double.valueOf(chargeVO.getChargevalue()), 2));
                    wireChargeVOHashMap.put(chargeVO.getChargename(),wireChargeVO);
                }
                if(chargeVO.getCategory().equals(Charge_category.Success.toString()) && chargeVO.getKeyword().equals(Charge_keyword.NetFinalAmount.toString())&&chargeVO.getSubkeyword().equals(Charge_subKeyword.Amount.toString()))
                {
                    settlementExpensesVO =new SettlementExpensesVO();
                    settlementExpensesVO.setChargeName(chargeVO.getChargename());
                    settlementExpensesVO.setChargeValue(chargeVO.getChargevalue());
                    settlementExpensesVO.setValueType(chargeVO.getValuetype());
                    settlementExpensesVO.setChargeid(chargeVO.getChargeid());
                    settlementExpensesVO.setNegativebalance(chargeVO.getNegativebalance());
                    settlementExpensesVOHashMap.put(chargeVO.getChargename(),settlementExpensesVO);
                }
                else if(chargeVO.getCategory().equals(Charge_category.Others.toString()) && chargeVO.getKeyword().equals(Charge_keyword.Setup.toString())&&chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString()))
                {
                    setupChargeVO= applySetupChargeUptoSettledDate(chargeVO, terminalVO,settlementDateVO);
                    if(setupChargeVO!=null)
                    {
                        grossSetupFee=grossSetupFee+setupChargeVO.getTotal();
                        latestSetupFeeDate=setupChargeVO.getLastChargeDate();
                    }
                    setupChargeVOHashMap.put(setupChargeVO.getChargeName(), setupChargeVO);
                }
                else if(chargeVO.getCategory().equals(Charge_category.Others.toString()) && chargeVO.getKeyword().equals(Charge_keyword.GrossBalanceAmount.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Amount.toString()))
                {
                    GrossChargeVO grossChargeVO=new GrossChargeVO();
                    grossChargeVO.setChargeName(chargeVO.getChargename());
                    grossChargeVO.setChargeValue(chargeVO.getChargevalue());
                    grossChargeVO.setValueType(chargeVO.getValuetype());
                    grossChargeVOMap.put(chargeVO.getChargename(), grossChargeVO);
                }
                else if(chargeVO.getCategory().equals(Charge_category.Others.toString()) && chargeVO.getKeyword().equals(Charge_keyword.ServiceTax.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Amount.toString()))
                {
                    ServiceTaxChargeVO serviceTaxChargeVO = new ServiceTaxChargeVO();
                    serviceTaxChargeVO.setChargeName(chargeVO.getChargename());
                    serviceTaxChargeVO.setChargeValue(chargeVO.getChargevalue());
                    serviceTaxChargeVO.setValueType(chargeVO.getValuetype());
                    serviceTaxChargeVOHashMap.put(chargeVO.getChargename(),serviceTaxChargeVO);
                }
                else if(chargeVO.getCategory().equals(Charge_category.Others.toString()) && chargeVO.getKeyword().equals(Charge_keyword.TotalFees.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Amount.toString()))
                {
                    TotalFeesChargesVO totalFeesChargesVO = new TotalFeesChargesVO();
                    totalFeesChargesVO.setChargeName(chargeVO.getChargename());
                    totalFeesChargesVO.setChargeValue(chargeVO.getChargevalue());
                    totalFeesChargesVO.setValueType(chargeVO.getValuetype());
                    totalFeesChargeVOMap.put(chargeVO.getChargename(),totalFeesChargesVO);
                }
                else if(chargeVO.getCategory().equals(Charge_category.Others.toString()) && chargeVO.getKeyword().equals(Charge_keyword.OtherCharges.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Amount.toString()))
                {
                    OtherChargesVO otherChargesVO = new OtherChargesVO();
                    otherChargesVO.setChargeName(chargeVO.getChargename());
                    otherChargesVO.setChargeValue(chargeVO.getChargevalue());
                    otherChargesVO.setValueType(chargeVO.getValuetype());
                    otherChargeVOMap.put(chargeVO.getChargename(),otherChargesVO);
                }
            }
            else
            {
                //Get Merchant Charge Version Rate
                String chargeVersionRate=chargeManager.getMerchantChargeVersionRate(chargeVO.getMappingid(),currentDate);
                if(chargeVersionRate!=null)
                {
                    chargeVO.setChargevalue(chargeVersionRate);
                }

                chargeDetailsVO= applyChargeOnTerminal(terminalVO, chargeVO, settlementDateVO,tableName,transactionSummaryVO,dynamicCountAmountMap);
                if(chargeDetailsVO.getChargeName()!=null)
                {

                    grossFee=grossFee+chargeDetailsVO.getTotal();
                    String terminalName=chargeVO.getTerminalid()+"-"+chargeVO.getPaymentName()+"-"+chargeVO.getCardType();
                    chargeDetailsMapOFVOs.put(chargeDetailsVO.getChargeName()+":"+terminalName,chargeDetailsVO);
                    if(chargeVO.getKeyword().equals(Charge_keyword.Reversed.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString()))
                    {
                        reversedAmount=reversedAmount+chargeDetailsVO.getAmount();
                    }
                    if(chargeVO.getKeyword().equals(Charge_keyword.Payout.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Amount.toString()))
                    {
                        payoutAmount=payoutAmount+chargeDetailsVO.getAmount();
                    }
                    if(chargeVO.getKeyword().equals(Charge_keyword.Chargeback.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString()))
                    {
                        chargebackAmount=chargebackAmount+chargeDetailsVO.getAmount();
                        chargebackReverseVO.setChargeValue(chargeDetailsVO.getChargeValue());
                        chargebackReverseVO.setTotal(chargebackReverseCount*Double.valueOf(chargeDetailsVO.getChargeValue()));
                    }
                    if(chargeVO.getKeyword().equals(Charge_keyword.VerifyOrder.toString()))
                    {
                        verifyOrderCount=chargeDetailsVO.getCount();
                    }
                    if(chargeVO.getKeyword().equals(Charge_keyword.RefundAlert.toString()))
                    {
                        refundAlertCount=chargeDetailsVO.getCount();
                    }
                    if(chargeVO.getKeyword().equals(Charge_keyword.RetrivalRequest.toString()))
                    {
                        retrivalRequestCount=chargeDetailsVO.getCount();
                    }
                    if(chargeVO.getKeyword().equals(Charge_keyword.FraudulentTransaction.toString()))
                    {
                        fraudulentTransactionCount=chargeDetailsVO.getCount();
                    }

                }
            }
        }

        //adding the setup gross fee in gross fee
        grossFee=grossFee+grossSetupFee-chargebackReverseVO.getTotal();
        totalProcessingAmount=transactionSummaryVO.getTotalProcessingAmount();
        reversalChargebackAmount=chargebackAmount+reversedAmount;
        //calculate gross amount here coz need to pass gross Amount to Calculate gross level charges

        if(totalFeesChargeVOMap.size()>0 && grossFee != 0)
        {
            String chargeName=null;
            Set set=totalFeesChargeVOMap.keySet();
            Iterator itr=set.iterator();
            while(itr.hasNext())
            {
                chargeName=(String)itr.next();
                TotalFeesChargesVO totalFeesChargesVO=calculateTotalFeesCharge(chargeName, totalFeesChargeVOMap, grossFee);
                serviceTaxonFeesChargeAmount=serviceTaxonFeesChargeAmount+totalFeesChargesVO.getTotal();
                totalFeesChargesVOHashMapFinal.put(totalFeesChargesVO.getChargeName(), totalFeesChargesVO);
            }
        }
        double grossDeduct=-1*(grossFee + serviceTaxonFeesChargeAmount+chargebackAmount+reversedAmount+reserveGeneratedAmount+payoutAmount);
        double grossAmount=totalProcessingAmount+grossDeduct;

        grossAmount=Functions.roundDBL(grossAmount,2);

        if(calculatedReserveRefundVOHashMap.size()>0)
        {
            Set set=calculatedReserveRefundVOHashMap.keySet();
            Iterator itr=set.iterator();
            while(itr.hasNext())
            {
                String chargeName=itr.next().toString();
                ReserveRefundVO refundVO= applyChargeOnReserveReleaseAmount(chargeName,calculatedReserveRefundVOHashMap,reserveReleaseAmount);
                reserveReleaseAmount=reserveReleaseAmount-refundVO.getTotal();
                reserveRefundVOHashMap.put(chargeName,refundVO);
            }
        }
        if(grossChargeVOMap.size()>0)
        {
            String chargeName=null;
            Set set=grossChargeVOMap.keySet();
            Iterator itr=set.iterator();
            while(itr.hasNext())
            {
                chargeName=(String)itr.next();
                GrossChargeVO grossChargesVO=applyGrossCharge(chargeName, grossChargeVOMap, grossAmount);
                grossChargesAmount=grossChargesAmount+grossChargesVO.getTotal();
                grossChargeVOMapFinal.put(grossChargesVO.getChargeName(), grossChargesVO);
            }
        }

        if(otherChargeVOMap.size()>0)
        {
            String chargeName=null;
            Set set=otherChargeVOMap.keySet();
            Iterator itr=set.iterator();
            while(itr.hasNext())
            {
                chargeName=(String)itr.next();
                OtherChargesVO otherChargesVO=CalculateOthercharges(chargeName, otherChargeVOMap, grossAmount);
                otherChargesAmount=otherChargesAmount+otherChargesVO.getTotal();
                otherChargesVOHashMapFinal.put(otherChargesVO.getChargeName(), otherChargesVO);
            }
        }

        if(serviceTaxChargeVOHashMap.size()>0)
        {
            String chargeName=null;
            Set set=serviceTaxChargeVOHashMap.keySet();
            Iterator itr=set.iterator();
            while(itr.hasNext())
            {
                chargeName=(String)itr.next();
                ServiceTaxChargeVO serviceTaxChargeVO=calculateServiveTax(chargeName,serviceTaxChargeVOHashMap,grossFee+wireChargeAmount);
                serviceTaxChargesAmount=serviceTaxChargesAmount+serviceTaxChargeVO.getTotal();
                serviceTaxChargeVOHashMapFinal.put(serviceTaxChargeVO.getChargeName(), serviceTaxChargeVO);
            }
        }



        /*File Updated:28-07-2015 by sandip
         *Retriving Random Charges Total amount
            1)to deducting same amount from netfinal if charge type=Deducted
            1)to add same amount in netfinal if charge type=Added
            2)to send amount to display in report pdf file as miscellnious adjustment
       */
        double merchantRandomChargesGrossAmount=0.00;
        double deductedAmount=0.00;
        double addedAmount=0.00;
        if(merchantRandomChargesVOListNew.size()>0)
        {
            String chargeName=null;
            Set set=merchantRandomChargesVOListNew.keySet();
            Iterator itr=set.iterator();
            while(itr.hasNext())
            {
                chargeName=(String)itr.next();
                MerchantRandomChargesVO detailsVO=merchantRandomChargesVOListNew.get(chargeName);
                MerchantRandomChargesVO merchantRandomChargesVO=new MerchantRandomChargesVO();
                merchantRandomChargesVO.setChargeCounter(0);
                merchantRandomChargesVO.setChargeValue(Double.valueOf(detailsVO.getChargeValue()));
                merchantRandomChargesVO.setChargeName(detailsVO.getChargeName());
                merchantRandomChargesVO.setChargeValueType(detailsVO.getChargeValueType());
                merchantRandomChargesVO.setChargeType(detailsVO.getChargeType());

                double total=0.00;
                if(detailsVO.getChargeValueType().equalsIgnoreCase("Percentage"))
                {
                    double amount = grossAmount;
                    if(grossAmount < 0){
                        amount = -1*grossAmount;
                    }
                     total = amount * Double.valueOf(detailsVO.getChargeValue()) / 100;
                     merchantRandomChargesVO.setChargeAmount(total);
                }else{
                     total = Double.valueOf(detailsVO.getChargeValue());
                     merchantRandomChargesVO.setChargeAmount(total);
                }

                if("Deducted".equals(detailsVO.getChargeType()))
                {
                    deductedAmount=deductedAmount+total;
                }
                else
                {
                    addedAmount=addedAmount+total;
                }
                merchantRandomChargesVOHashMap.put(merchantRandomChargesVO.getChargeName(), merchantRandomChargesVO);
            }
            merchantRandomChargesGrossAmount=Double.valueOf(addedAmount-deductedAmount);
        }
        /*for(MerchantRandomChargesVO merchantRandomChargesVO:merchantRandomChargesVOList)
        {
            if("Deducted".equals(merchantRandomChargesVO.getChargeType()))
            {
                deductedAmount=deductedAmount+merchantRandomChargesVO.getChargeValue();
            }
            else
            {
                addedAmount=addedAmount+merchantRandomChargesVO.getChargeValue();
            }
        }*/

        //calculate total funded to bank coz need to pass the totalFundedToBank to generate pdf file
        totalChargebackReversal=chargebackReverseVO.getAmount();
        double refundReversedAmount=transactionSummaryVO.getRefundReverseAmount();
        double totalFundedToBank=(reserveReleaseAmount+grossAmount+previousBalanceAmount+merchantRandomChargesGrossAmount+totalChargebackReversal+refundReversedAmount)-((wireChargeAmount+statementChargeAmount)+(-1*grossChargesAmount)+serviceTaxChargesAmount+otherChargesAmount);
        double minPayoutAmount=Double.valueOf(terminalVO.getMinPayoutAmount());
        boolean isWireFeeRequired=false;
        if(wireChargeVO!=null){
            if(totalFundedToBank>=minPayoutAmount){
                isWireFeeRequired=true;
            }
        }

        if(wireChargeVO !=null && !isWireFeeRequired){
            totalFundedToBank=totalFundedToBank+wireChargeAmount;
        }

        double exchangeRateDbl=1.00;
        /*if(exchangeRate.containsKey(settlementCurrency)){
            exchangeRateDbl=Double.valueOf(exchangeRate.get(settlementCurrency));
        }*/
        if(settlementCurrency!=null){
            exchangeRateDbl=terminalVO.getConversionRate();
            logger.error("exchangeRateDbl----"+exchangeRateDbl);
        }

        double totalFundedToBankBeforeSettlementExpenses=totalFundedToBank;
        if(settlementExpensesVOHashMap!=null && settlementExpensesVOHashMap.size()>0){
            Set set=settlementExpensesVOHashMap.keySet();
            Iterator iterator=set.iterator();
            while (iterator.hasNext())
            {
                String chargename = (String) iterator.next();
                SettlementExpensesVO settlementExpensesVO1 = settlementExpensesVOHashMap.get(chargename);
                double settlementExpensesAmount=0;
                ChargesDAO ChargesDAO = new ChargesDAO();
                String flag=" ";
                negativebalance=ChargesDAO.getMerchantChargeNegativeBalance(memberId,accountId,terminalId,settlementExpensesVO1.getChargeid());
                    if (totalFundedToBank < 0)
                    {
                        if(settlementExpensesVO1.getNegativebalance().equals("Y")){
                            flag=" [Positive Settlement Charges]";
                        }
                        else{
                            flag=" [No Settlement Charges]";
                        }
                        if(negativebalance)
                        {
                            settlementExpensesAmount = -((totalFundedToBankBeforeSettlementExpenses * Double.valueOf(settlementExpensesVO1.getChargeValue()) / 100));
                        }
                        else{
                            settlementExpensesVO1.setChargeValue("0.00");
                            settlementExpensesAmount = totalFundedToBankBeforeSettlementExpenses * Double.valueOf(settlementExpensesVO1.getChargeValue()) / 100;
                        }
                    }else{
                        settlementExpensesAmount = totalFundedToBankBeforeSettlementExpenses * Double.valueOf(settlementExpensesVO1.getChargeValue()) / 100;
                    }
                settlementExpensesVO=new SettlementExpensesVO();
                settlementExpensesVO.setChargeName(chargename + flag);
                settlementExpensesVO.setChargeValue(settlementExpensesVO1.getChargeValue());
                settlementExpensesVO.setAmount(totalFundedToBankBeforeSettlementExpenses);
                settlementExpensesVO.setTotal(settlementExpensesAmount);
                settlementExpensesVOHashMap.put(chargename,settlementExpensesVO);
                if (totalFundedToBank < 0)
                {
                    if(negativebalance)
                    {
                        if (settlementExpensesAmount > 0)
                        {
                            totalFundedToBank = totalFundedToBank + settlementExpensesAmount;

                        }
                        else
                        {
                            totalFundedToBank = totalFundedToBank + (-1 * settlementExpensesAmount);
                        }
                    }else{
                        if (settlementExpensesAmount > 0)
                        {
                            totalFundedToBank = totalFundedToBank - settlementExpensesAmount;

                        }
                        else
                        {
                            totalFundedToBank = totalFundedToBank - (-1 * settlementExpensesAmount);
                        }
                    }
                }else{
                    if (settlementExpensesAmount > 0)
                    {
                        totalFundedToBank = totalFundedToBank - settlementExpensesAmount;

                    }
                    else
                    {
                        totalFundedToBank = totalFundedToBank - (-1 * settlementExpensesAmount);
                    }
                }
            }
        }
        /*if(settlementExpensesVO !=null){
            double settlementExpensesAmount=totalFundedToBank*Double.valueOf(settlementExpensesVO.getChargeValue())/100;
            settlementExpensesVO.setAmount(totalFundedToBankBeforeSettlementExpenses);
            settlementExpensesVO.setTotal(settlementExpensesAmount);
            if(settlementExpensesAmount>0){
                totalFundedToBank=totalFundedToBank-settlementExpensesAmount;
            }
            else{
                totalFundedToBank=totalFundedToBank-(-1*settlementExpensesAmount);
            }
        }*/

        double totalFundedInSettlementCurrency=exchangeRateDbl*totalFundedToBank;
        totalFundedToBank=Functions.roundDBL(totalFundedToBank,2);
        if(chargeDetailsMapOFVOs.size()>=0)
        {
            String pdfFilePath=getPDFReportFile(totalFundedToBank,grossAmount,previousBalanceAmount,reversedAmount,chargebackAmount,payoutAmount,chargebackReverseVO,refundReversedAmount,totalProcessingAmount,grossFee,reserveGeneratedAmount,reserveReleaseAmount,companyName,contactPerson,/*cardType,paymentType,*/terminalVO,chargeDetailsMapOFVOs,reserveGeneratedVOHashMap,reserveRefundVOHashMap,settlementDateVO,rollingReserveDateVO,wireChargeVOHashMap,grossChargeVOMapFinal,setupChargeVOHashMap,serviceTaxChargeVOHashMapFinal,merchantRandomChargesVOHashMap,merchantRandomChargesGrossAmount,currency,isPoweredBy,settlementCurrency,totalFundedInSettlementCurrency,exchangeRateDbl,statementChargeVOHashMap,isWireFeeRequired, settlementExpensesVOHashMap,totalFundedToBankBeforeSettlementExpenses,totalFeesChargesVOHashMapFinal,grossFee,otherChargesVOHashMapFinal);
            String settleTransExcelFilePath= createMerchantTransactionFile(settlementDateVO, tableName, terminalVO);
            String rollingReserveExcelFilePath= createRollingReserveTransactionFile(rollingReserveDateVO, terminalVO);
            logger.error("rollingReserveExcelFilePath::::"+rollingReserveExcelFilePath);

            WireVO wireVO=new WireVO();
            wireVO.setFirstDate(settlementDateVO.getSettlementStartDate());
            wireVO.setLastDate(settlementDateVO.getSettlementEndDate());
            wireVO.setCurrency(currency);
            wireVO.setMarkForDeletion("N");
            wireVO.setWireAmount(totalProcessingAmount);
            wireVO.setWireBalanceAmount(grossAmount);
            wireVO.setNetFinalAmount(totalFundedToBank);
            wireVO.setUnpaidAmount(0.00);
            wireVO.setStatus("unpaid");
            wireVO.setSettlementReportFilePath(pdfFilePath);
            wireVO.setSettledTransactionFilePath(settleTransExcelFilePath);
            wireVO.setReserveReleasedUptoDate(rollingReserveDateVO.getRollingReserveEndDate());
            wireVO.setRollingReserveIncluded("Y");
            wireVO.setTerminalVO(terminalVO);
            wireVO.setSettlementCycleNo(settlementDateVO.getSettlementcycleNumber());
            wireVO.setDeclinedcoverdateupto(settlementDateVO.getDeclinedEndDate());
            wireVO.setReversedcoverdateupto(settlementDateVO.getReversedEndDate());
            wireVO.setChargebackcoverdateupto(settlementDateVO.getChargebackEndDate());
            wireVO.setRollingReserveFilePath(rollingReserveExcelFilePath);
            wireVO.setReportid(payoutDAO.getreportid());
            status=payoutDAO.generateSettlementCycleWire(wireVO);
            if("success".equals(status))
            {
                //System.out.println("Wire Generated Successfully for memberId==="+memberId);
                String mailStatus = "Failed";

                //Call MailService To Give Alert Merchant To See payout report in merchant Back Office
                MailService mailService = new MailService();
                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                HashMap cbCountRecord = new HashMap();
                cbCountRecord.put(MailPlaceHolder.TOID, terminalVO.getMemberId());
                cbCountRecord.put(MailPlaceHolder.FROMDATE, settlementStartDate.split(" ")[0]);
                cbCountRecord.put(MailPlaceHolder.TODATE, settlementEndDate.split(" ")[0]);
                if (merchantRandomChargesVOList.size() > 0)
                {
                    cbCountRecord.put(MailPlaceHolder.MULTIPALTRANSACTION, mailService.getDetailTableForRandomCharges(merchantRandomChargesVOList));
                }
                asynchronousMailService.sendMerchantSignup(MailEventEnum.MERCHANT_PAYOUT_ALERT_MAIL, cbCountRecord);
                mailStatus = "success";
                boolean isMemberCycleUpdated = payoutDAO.updateMemberCycleDetailsWithAccountId(memberId, accountId, terminalId, verifyOrderCount, refundAlertCount, retrivalRequestCount, latestSetupFeeDate, settlementDateVO.getSettlementcycleNumber(), fraudulentTransactionCount,null);

                if (isMemberCycleUpdated)
                {
                    logger.debug("member_settlementcycle_details updated successfully");
                }

                //Important: Make entry for memberid,accountid,cycleid,ispaid='N' in the bank_merchant_settlement_master
                boolean isBankMerchantSettlementMasterUpdated = payoutDAO.merchantSettlementMasterEntry(memberId, accountId, terminalId, settlementDateVO.getSettlementcycleNumber(), "N",null);
                logger.error("pending List Size----"+weeklyPayoutReportVO.getPendingList().size());
                logger.error("Request List Size----"+weeklyPayoutReportVO.getRequestTerminalVO().size());

                if (isBankMerchantSettlementMasterUpdated)
                {
                    //System.out.println("Making Entry Into The bank_merchant_settlement_master successfully");
                    /*int data = weeklyPayoutReportVO.getPendingList().size() - weeklyPayoutReportVO.getRequestTerminalVO().size();
                    if (data == 0)
                    {
                        boolean ispayoutcronupdated = payoutDAO.updatePayoutCronExecutedFlag(accountId, settlementDateVO.getSettlementcycleNumber());
                        if (ispayoutcronupdated)
                        {
                            resStatus = "Success";
                        }
                    }*/
                    resStatus = "Success";
                }
            }
        }
        return resStatus;
    }

    public ChargeDetailsVO applyChargeOnTerminal(TerminalVO terminalVO, ChargeVO chargeVO, SettlementDateVO settlementDateVO,String tableName,TransactionSummaryVO transactionSummaryVO,HashMap<String, String> dynamicCountAmountMap) throws PZDBViolationException
    {
        ChargeDetailsVO chargeDetailsVO=new ChargeDetailsVO();
        String country=payoutDAO.getGatewayCountryFromAccountId(terminalVO.getAccountId());
        if(chargeVO.getCategory().equals(Charge_category.Success.toString()) && chargeVO.getKeyword().equals(Charge_keyword.FraudAlert.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString()))
        {
            logger.error("Inside fraudAlert-------");
            long   totalSuccessCount=0;
            double totalSuccessAmount=0.0;

            transactionSummaryVO=getFraudDefenderCountAmountByTerminal(terminalVO, settlementDateVO);

            totalSuccessCount=transactionSummaryVO.getTotalProcessingCount();
            totalSuccessAmount=transactionSummaryVO.getTotalProcessingAmount();

            double total=totalSuccessCount*Double.valueOf(chargeVO.getChargevalue());

            chargeDetailsVO.setCount(totalSuccessCount);
            chargeDetailsVO.setAmount(totalSuccessAmount);
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());
            chargeDetailsVO.setValueType(chargeVO.getValuetype());
            chargeDetailsVO.setTotal(Functions.roundDBL(total,2));
        }
        if(chargeVO.getCategory().equals(Charge_category.Success.toString()) && chargeVO.getKeyword().equals(Charge_keyword.Total.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Amount.toString()))
        {
            long   totalSuccessCount=0;
            double totalSuccessAmount=0.0;

            if("Y".equals(chargeVO.getIsinputrequired())){
                String userInput=dynamicCountAmountMap.get(chargeVO.getMappingid());
                if(functions.isValueNull(userInput)){
                    String userInputArr[]=userInput.split(":");
                    if(!"-".equals(userInputArr[0])){
                        totalSuccessCount=Integer.parseInt(userInputArr[0]);
                    }
                    totalSuccessAmount=Double.parseDouble(userInputArr[1]);
                }
            }
            else{
                totalSuccessCount=transactionSummaryVO.getTotalProcessingCount();
                totalSuccessAmount=transactionSummaryVO.getTotalProcessingAmount();
            }
            chargeDetailsVO.setCount(totalSuccessCount);
            chargeDetailsVO.setAmount(totalSuccessAmount);
            double total=totalSuccessAmount*Double.valueOf(chargeVO.getChargevalue())/100;

            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());
            chargeDetailsVO.setValueType(chargeVO.getValuetype());
            chargeDetailsVO.setTotal(Functions.roundDBL(total,2));

        }
        if(chargeVO.getCategory().equals(Charge_category.Success.toString()) && chargeVO.getKeyword().equals(Charge_keyword.Total.toString())&&chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString()))
        {
            long   totalSuccessCount=0;
            double totalSuccessAmount=0.0;

            totalSuccessCount=transactionSummaryVO.getTotalProcessingCount();
            totalSuccessAmount=transactionSummaryVO.getTotalProcessingAmount();

            double total=totalSuccessCount*Double.valueOf(chargeVO.getChargevalue());

            chargeDetailsVO.setCount(totalSuccessCount);
            chargeDetailsVO.setAmount(totalSuccessAmount);
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());
            chargeDetailsVO.setValueType(chargeVO.getValuetype());
            chargeDetailsVO.setTotal(Functions.roundDBL(total,2));
        }
        if(chargeVO.getCategory().equals(Charge_category.Success.toString()) && chargeVO.getKeyword().equals(Charge_keyword.DomesticTotal.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Amount.toString()) && functions.isValueNull(country))
        {
            long   totalSuccessCount=0;
            double totalSuccessAmount=0.0;
            //String country=payoutDAO.getGatewayCountryFromAccountId(terminalVO.getAccountId());

            if(functions.isValueNull(country))
            {
                String data = String.valueOf(EU_Country.getEnum(country));
                if(functions.isValueNull(data)){
                    transactionSummaryVO = getDomesticTotalAmountByTerminalForEUCountry(terminalVO, settlementDateVO);
                    if ("Y".equals(chargeVO.getIsinputrequired())) {
                        String userInput = dynamicCountAmountMap.get(chargeVO.getMappingid());
                        if (functions.isValueNull(userInput)) {
                            String userInputArr[] = userInput.split(":");
                            if (!"-".equals(userInputArr[0]))
                            {
                                totalSuccessCount = Integer.parseInt(userInputArr[0]);
                            }
                            totalSuccessAmount = Double.parseDouble(userInputArr[1]);
                        }
                    }
                    else
                    {
                        totalSuccessCount = transactionSummaryVO.getTotalProcessingCount();
                        totalSuccessAmount = transactionSummaryVO.getTotalProcessingAmount();
                    }
                }else{
                    transactionSummaryVO = getDomesticTotalAmountByTerminalForNonEU(terminalVO, settlementDateVO,country);
                    if ("Y".equals(chargeVO.getIsinputrequired())) {
                        String userInput = dynamicCountAmountMap.get(chargeVO.getMappingid());
                        if (functions.isValueNull(userInput)) {
                            String userInputArr[] = userInput.split(":");
                            if (!"-".equals(userInputArr[0]))
                            {
                                totalSuccessCount = Integer.parseInt(userInputArr[0]);
                            }
                            totalSuccessAmount = Double.parseDouble(userInputArr[1]);
                        }
                    }
                    else
                    {
                        totalSuccessCount = transactionSummaryVO.getTotalProcessingCount();
                        totalSuccessAmount = transactionSummaryVO.getTotalProcessingAmount();
                    }
                }

                chargeDetailsVO.setCount(totalSuccessCount);
                chargeDetailsVO.setAmount(totalSuccessAmount);
                double total = totalSuccessAmount * Double.valueOf(chargeVO.getChargevalue()) / 100;

                chargeDetailsVO.setChargeName(chargeVO.getChargename());
                chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());
                chargeDetailsVO.setValueType(chargeVO.getValuetype());
                chargeDetailsVO.setTotal(Functions.roundDBL(total, 2));
            }
        }
        if(chargeVO.getCategory().equals(Charge_category.Success.toString()) && chargeVO.getKeyword().equals(Charge_keyword.InternationalTotal.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Amount.toString()) && functions.isValueNull(country))
        {
            long   totalSuccessCount=0;
            double totalSuccessAmount=0.0;
            if(functions.isValueNull(country))
            {
                String data = String.valueOf(EU_Country.getEnum(country));
                if(functions.isValueNull(data)){
                    transactionSummaryVO = getInternationalTotalAmountByTerminalForEUCountry(terminalVO, settlementDateVO);
                    if ("Y".equals(chargeVO.getIsinputrequired())) {
                        String userInput = dynamicCountAmountMap.get(chargeVO.getMappingid());
                        if (functions.isValueNull(userInput)) {
                            String userInputArr[] = userInput.split(":");
                            if (!"-".equals(userInputArr[0]))
                            {
                                totalSuccessCount = Integer.parseInt(userInputArr[0]);
                            }
                            totalSuccessAmount = Double.parseDouble(userInputArr[1]);
                        }
                    }
                    else
                    {
                        totalSuccessCount = transactionSummaryVO.getTotalProcessingCount();
                        totalSuccessAmount = transactionSummaryVO.getTotalProcessingAmount();
                    }
                }else{
                    transactionSummaryVO = getInternationalTotalAmountByTerminalForNonEU(terminalVO, settlementDateVO, country);
                    if ("Y".equals(chargeVO.getIsinputrequired())) {
                        String userInput = dynamicCountAmountMap.get(chargeVO.getMappingid());
                        if (functions.isValueNull(userInput)) {
                            String userInputArr[] = userInput.split(":");
                            if (!"-".equals(userInputArr[0]))
                            {
                                totalSuccessCount = Integer.parseInt(userInputArr[0]);
                            }
                            totalSuccessAmount = Double.parseDouble(userInputArr[1]);
                        }
                    }
                    else
                    {
                        totalSuccessCount = transactionSummaryVO.getTotalProcessingCount();
                        totalSuccessAmount = transactionSummaryVO.getTotalProcessingAmount();
                    }
                }

                chargeDetailsVO.setCount(totalSuccessCount);
                chargeDetailsVO.setAmount(totalSuccessAmount);
                double total = totalSuccessAmount * Double.valueOf(chargeVO.getChargevalue()) / 100;

                chargeDetailsVO.setChargeName(chargeVO.getChargename());
                chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());
                chargeDetailsVO.setValueType(chargeVO.getValuetype());
                chargeDetailsVO.setTotal(Functions.roundDBL(total, 2));
            }
        }
        else if(chargeVO.getCategory().equals(Charge_category.Success.toString()) && chargeVO.getKeyword().equals(Charge_keyword.Reversed.toString())&&chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString()))
        {
            long   reversedCount=transactionSummaryVO.getCountOfReversed();
            double reversedAmount=transactionSummaryVO.getReversedAmount();

            double total=reversedCount*Double.valueOf(chargeVO.getChargevalue());

            chargeDetailsVO.setCount(reversedCount);
            chargeDetailsVO.setAmount(reversedAmount);
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());
            chargeDetailsVO.setValueType(chargeVO.getValuetype());
            chargeDetailsVO.setTotal(Functions.roundDBL(total,2));
        }
        else if(chargeVO.getCategory().equals(Charge_category.Success.toString()) && chargeVO.getKeyword().equals(Charge_keyword.Payout.toString())/*&&chargeVO.getSubkeyword().equals(Charge_subKeyword.Amount.toString())*/)
        {
            long   payoutCount=transactionSummaryVO.getCountOfPayout();
            double payoutAmount=transactionSummaryVO.getPayoutAmount();

            double total=0;

            if (chargeVO.getSubkeyword().equals(Charge_subKeyword.Amount.toString())){
            total=payoutAmount*Double.valueOf(chargeVO.getChargevalue())/100;}
            else if (chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString())){
                total=payoutCount*Double.valueOf(chargeVO.getChargevalue());
            }

            chargeDetailsVO.setCount(payoutCount);
            chargeDetailsVO.setAmount(payoutAmount);
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());
            chargeDetailsVO.setValueType(chargeVO.getValuetype());
            chargeDetailsVO.setTotal(Functions.roundDBL(total,2));
        }

        else if(chargeVO.getCategory().equals(Charge_category.Success.toString()) && chargeVO.getKeyword().equals(Charge_keyword.Reversed.toString())&&chargeVO.getSubkeyword().equals(Charge_subKeyword.Amount.toString()))
        {
            long   reversedCount=transactionSummaryVO.getCountOfReversed();
            double reversedAmount=transactionSummaryVO.getReversedAmount();

            double total=reversedAmount*Double.valueOf(chargeVO.getChargevalue())/100;

            chargeDetailsVO.setCount(reversedCount);
            chargeDetailsVO.setAmount(reversedAmount);
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());
            chargeDetailsVO.setValueType(chargeVO.getValuetype());
            chargeDetailsVO.setTotal(Functions.roundDBL(total,2));
        }
        else if(chargeVO.getCategory().equals(Charge_category.Success.toString()) && chargeVO.getKeyword().equals(Charge_keyword.Chargeback.toString())&&chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString()))
        {
            long   chargebackCount=transactionSummaryVO.getCountOfChargeback();
            double chargebackAmount=transactionSummaryVO.getChargebackAmount();

            double total=chargebackCount*Double.valueOf(chargeVO.getChargevalue());

            chargeDetailsVO.setCount(chargebackCount);
            chargeDetailsVO.setAmount(chargebackAmount);
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());
            chargeDetailsVO.setValueType(chargeVO.getValuetype());
            chargeDetailsVO.setTotal(Functions.roundDBL(total,2));

        }
        else if(chargeVO.getCategory().equals(Charge_category.Failure.toString()) && chargeVO.getKeyword().equals(Charge_keyword.Total.toString())&&chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString()))
        {
            long   authfailedCount=transactionSummaryVO.getCountOfAuthfailed();
            double authfailedAmount=transactionSummaryVO.getAuthfailedAmount();

            double total=authfailedCount*Double.valueOf(chargeVO.getChargevalue());

            chargeDetailsVO.setCount(authfailedCount);
            chargeDetailsVO.setAmount(authfailedAmount);
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());
            chargeDetailsVO.setValueType(chargeVO.getValuetype());
            chargeDetailsVO.setTotal(Functions.roundDBL(total,2));

        }
        else if(chargeVO.getCategory().equals(Charge_category.Failure.toString()) && chargeVO.getKeyword().equals(Charge_keyword.Total.toString())&&chargeVO.getSubkeyword().equals(Charge_subKeyword.Amount.toString()))
        {
            long   authfailedCount=transactionSummaryVO.getCountOfAuthfailed();
            double authfailedAmount=transactionSummaryVO.getAuthfailedAmount();

            double total=authfailedAmount*Double.valueOf(chargeVO.getChargevalue())/100;

            chargeDetailsVO.setCount(authfailedCount);
            chargeDetailsVO.setAmount(authfailedAmount);
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());
            chargeDetailsVO.setValueType(chargeVO.getValuetype());
            chargeDetailsVO.setTotal(Functions.roundDBL(total,2));

        }
        else if(chargeVO.getCategory().equals(Charge_category.Others.toString()) && chargeVO.getKeyword().equals(Charge_keyword.Total.toString())&&chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString()))
        {

            long   totalSuccessCount=0;
            double totalSuccessAmount=0.0;

            totalSuccessCount=transactionSummaryVO.getTotalProcessingCount();
            totalSuccessAmount=transactionSummaryVO.getTotalProcessingAmount();

            transactionSummaryVO=getAuthFailedCountAmountByTerminal(terminalVO,settlementDateVO);

            long   authfailedCount=transactionSummaryVO.getCountOfAuthfailed();
            double authfailedAmount=transactionSummaryVO.getAuthfailedAmount();
            long otherCount=totalSuccessCount+authfailedCount;

            double total=otherCount*Double.valueOf(chargeVO.getChargevalue());

            chargeDetailsVO.setCount(otherCount);
            chargeDetailsVO.setAmount(totalSuccessAmount+authfailedAmount);
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());
            chargeDetailsVO.setValueType(chargeVO.getValuetype());
            chargeDetailsVO.setTotal(Functions.roundDBL(total,2));
        }
        else if(chargeVO.getCategory().equals(Charge_category.Others.toString()) && chargeVO.getKeyword().equals(Charge_keyword.FraudulentTransaction.toString())&&chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString()))
        {
            long fraudulentTransactionCount=payoutDAO.getFraudulentTransactionCountOnTerminal(terminalVO);
            long fraudulentTransactionPaidCount=payoutDAO.getPaidFraudulentTransactionCountOnTerminal(terminalVO);
            long fraudulentOrderCount=fraudulentTransactionCount-fraudulentTransactionPaidCount;
            double total=fraudulentOrderCount*Double.valueOf(chargeVO.getChargevalue());

            chargeDetailsVO.setCount(fraudulentOrderCount);
            chargeDetailsVO.setAmount(0.00);
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());
            chargeDetailsVO.setValueType(chargeVO.getValuetype());
            chargeDetailsVO.setTotal(Functions.roundDBL(total,2));
        }
        else if(chargeVO.getCategory().equals(Charge_category.Others.toString()) && chargeVO.getKeyword().equals(Charge_keyword.CaseFiling.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString()))
        {
            long caseFilingTransactionCount=payoutDAO.getCaseFilingTransactionCount(terminalVO,settlementDateVO);
            double total=caseFilingTransactionCount*Double.valueOf(chargeVO.getChargevalue());

            chargeDetailsVO.setCount(caseFilingTransactionCount);
            chargeDetailsVO.setAmount(0.00);
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());
            chargeDetailsVO.setValueType(chargeVO.getValuetype());
            chargeDetailsVO.setTotal(Functions.roundDBL(total,2));
        }
        else if(chargeVO.getCategory().equals(Charge_category.Others.toString()) && chargeVO.getKeyword().equals(Charge_keyword.VerifyOrder.toString())&&chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString()))
        {
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());
            chargeDetailsVO.setValueType(chargeVO.getValuetype());

            //Make The DB Call And Get Count  then calculate
            long memberVerifyOrderCount=payoutDAO.getVerifyOrderCountOnTerminal(terminalVO, settlementDateVO);
            long verifyOrderPaidCount=payoutDAO.getPaidVerifyOrderCountOnTerminal(terminalVO);

            long verifyOrderCount=memberVerifyOrderCount-verifyOrderPaidCount;

            double total=verifyOrderCount*Double.valueOf(chargeVO.getChargevalue());

            chargeDetailsVO.setCount(verifyOrderCount);
            chargeDetailsVO.setAmount(0.00);
            chargeDetailsVO.setTotal(Functions.roundDBL(total,2));
        }
        else if(chargeVO.getCategory().equals(Charge_category.Others.toString()) && chargeVO.getKeyword().equals(Charge_keyword.RefundAlert.toString())&&chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString()))
        {
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());
            chargeDetailsVO.setValueType(chargeVO.getValuetype());

            //Make The DB Call To Get Count  then calculated
            long memberRefundAlertCount=payoutDAO.getRefundAlertCountOnTerminal(terminalVO, settlementDateVO);
            long refundAlertPaidCount=payoutDAO.getPaidRefundAlertCountOnTerminal(terminalVO);
            long refundAlertCount=memberRefundAlertCount-refundAlertPaidCount;

            double total=refundAlertCount*Double.valueOf(chargeVO.getChargevalue());
            chargeDetailsVO.setCount(refundAlertCount);
            chargeDetailsVO.setAmount(0.00);
            chargeDetailsVO.setTotal(Functions.roundDBL(total,2));
        }
        else if(chargeVO.getCategory().equals(Charge_category.Others.toString()) && chargeVO.getKeyword().equals(Charge_keyword.RetrivalRequest.toString())&&chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString()))
        {
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());
            chargeDetailsVO.setValueType(chargeVO.getValuetype());

            //Make The DB Call To Get Count  then calculated
            long allRetrivalRequestCount=payoutDAO.getRetrivalRequestCount(terminalVO, tableName);
            long paidRetrivalRequestCount=payoutDAO.getPaidRetrivalRequestCount(terminalVO);
            long retrivalRequestCount=allRetrivalRequestCount-paidRetrivalRequestCount;

            double total=retrivalRequestCount*Double.valueOf(chargeVO.getChargevalue());
            chargeDetailsVO.setCount(retrivalRequestCount);
            chargeDetailsVO.setAmount(0.00);
            chargeDetailsVO.setTotal(Functions.roundDBL(total,2));
        }
        return  chargeDetailsVO;


    }

    public SetupChargeVO applySetupChargeForSummary(ChargeVO chargeVO, TerminalVO terminalVO) //sandip
    {
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
        SetupChargeVO  setupChargeVO=new SetupChargeVO();

        String latestSetupFeeDate=null;
        String strCurrentDate=null;

        Date currentDate=null;
        Date updatedSetupFeeDate=null;

        long setupCounter=0;
        long days= 0;
        boolean oneTimeChargeFlag=false;
        currentDate=new Date();

        setupChargeVO.setChargeName(chargeVO.getChargename());
        setupChargeVO.setChargeValue(chargeVO.getChargevalue());
        setupChargeVO.setValueType(chargeVO.getValuetype());

        //Calculate setup fee based on merchants first transaction date irrespctive of status
        latestSetupFeeDate=payoutDAO.getMemberFirstTransactionDate(terminalVO.getMemberId(),terminalVO.getAccountId());
        try
        {
            days = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(latestSetupFeeDate)), targetFormat.format(new Date()));
            strCurrentDate=targetFormat.format(currentDate);
            updatedSetupFeeDate = targetFormat.parse(latestSetupFeeDate);
        }
        catch (ParseException e)
        {
            logger.error(e);
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(updatedSetupFeeDate);
        //Make The DB Call To Get last setup added date by passing memberId
        if(chargeVO.getFrequency().equals(Charge_frequency.One_Time.toString()))
        {
            setupCounter=1;
            setupChargeVO.setLastChargeDate(strCurrentDate);
        }
        else if(chargeVO.getFrequency().equals(Charge_frequency.Weekly.toString()))
        {
            setupCounter=(days/7)+1;
            cal.add(Calendar.DATE,(int)setupCounter*7); // add counter*7 days in lastsetupfee date
            updatedSetupFeeDate = cal.getTime();
            setupChargeVO.setLastChargeDate(targetFormat.format(updatedSetupFeeDate));
        }
        else if(chargeVO.getFrequency().equals(Charge_frequency.Monthly.toString()))
        {
            setupCounter=(days/30)+1;
            cal.add(Calendar.DATE,(int)setupCounter*30); // add counter*30 days in lastsetupfee date
            updatedSetupFeeDate = cal.getTime();
            setupChargeVO.setLastChargeDate(targetFormat.format(updatedSetupFeeDate));
        }
        else if(chargeVO.getFrequency().equals(Charge_frequency.Yearly.toString()))
        {
            setupCounter=(days/365)+1;
            cal.add(Calendar.DATE,(int)setupCounter*365); // add 10 days
            updatedSetupFeeDate = cal.getTime();
            setupChargeVO.setLastChargeDate(targetFormat.format(updatedSetupFeeDate));
        }
        setupChargeVO.setCount(setupCounter);
        setupChargeVO.setAmount(0.00);
        setupChargeVO.setTotal(setupCounter*Double.valueOf(chargeVO.getChargevalue()));

        return setupChargeVO;
    }

    public SetupChargeVO applySetupChargeForAgentSummary(ChargeVO chargeVO, TerminalVO terminalVO,String memberFirstTransactionDate) //sandip
    {
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
        SetupChargeVO  setupChargeVO=new SetupChargeVO();

        String latestSetupFeeDate=null;
        String strCurrentDate=null;

        Date currentDate=null;
        Date updatedSetupFeeDate=null;

        long setupCounter=0;
        long days= 0;
        boolean oneTimeChargeFlag=false;
        currentDate=new Date();

        setupChargeVO.setChargeName(chargeVO.getChargename());
        setupChargeVO.setChargeValue(chargeVO.getAgentChargeValue());
        setupChargeVO.setValueType(chargeVO.getValuetype());

        //Calculate setup fee based on merchants first transaction date irrespctive of status
        latestSetupFeeDate=memberFirstTransactionDate;//payoutDAO.getMemberFirstTransactionDate(terminalVO.getMemberId(),terminalVO.getAccountId());
        try
        {
            days = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(latestSetupFeeDate)), targetFormat.format(new Date()));
            strCurrentDate=targetFormat.format(currentDate);
            updatedSetupFeeDate = targetFormat.parse(latestSetupFeeDate);
        }
        catch (ParseException e)
        {
            logger.error(e);
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(updatedSetupFeeDate);
        //Make The DB Call To Get last setup added date by passing memberId
        if(chargeVO.getFrequency().equals(Charge_frequency.One_Time.toString()))
        {
            setupCounter=1;
            setupChargeVO.setLastChargeDate(strCurrentDate);
        }
        else if(chargeVO.getFrequency().equals(Charge_frequency.Weekly.toString()))
        {
            setupCounter=(days/7)+1;
            cal.add(Calendar.DATE,(int)setupCounter*7); // add counter*7 days in lastsetupfee date
            updatedSetupFeeDate = cal.getTime();
            setupChargeVO.setLastChargeDate(targetFormat.format(updatedSetupFeeDate));
        }
        else if(chargeVO.getFrequency().equals(Charge_frequency.Monthly.toString()))
        {
            setupCounter=(days/30)+1;
            cal.add(Calendar.DATE,(int)setupCounter*30); // add counter*30 days in lastsetupfee date
            updatedSetupFeeDate = cal.getTime();
            setupChargeVO.setLastChargeDate(targetFormat.format(updatedSetupFeeDate));
        }
        else if(chargeVO.getFrequency().equals(Charge_frequency.Yearly.toString()))
        {
            setupCounter=(days/365)+1;
            cal.add(Calendar.DATE,(int)setupCounter*365); // add 10 days
            updatedSetupFeeDate = cal.getTime();
            setupChargeVO.setLastChargeDate(targetFormat.format(updatedSetupFeeDate));
        }
        setupChargeVO.setCount(setupCounter);
        setupChargeVO.setAmount(0.00);
        setupChargeVO.setTotal(setupCounter*Double.valueOf(chargeVO.getAgentChargeValue()));

        return setupChargeVO;
    }

    public SetupChargeVO applySetupChargeForPartnerSummary(ChargeVO chargeVO,String firstTransactionDate) //sandip
    {
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
        SetupChargeVO  setupChargeVO=new SetupChargeVO();

        String latestSetupFeeDate=null;
        String strCurrentDate=null;

        Date currentDate=null;
        Date updatedSetupFeeDate=null;

        long setupCounter=0;
        long days= 0;
        boolean oneTimeChargeFlag=false;
        currentDate=new Date();

        setupChargeVO.setChargeName(chargeVO.getChargename());
        setupChargeVO.setChargeValue(chargeVO.getPartnerChargeValue());
        setupChargeVO.setValueType(chargeVO.getValuetype());

        //Calculate setup fee based on merchants first transaction date irrespctive of status
        latestSetupFeeDate=firstTransactionDate;//payoutDAO.getMemberFirstTransactionDate(terminalVO.getMemberId(),terminalVO.getAccountId());
        try
        {
            days = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(latestSetupFeeDate)), targetFormat.format(new Date()));
            strCurrentDate=targetFormat.format(currentDate);
            updatedSetupFeeDate = targetFormat.parse(latestSetupFeeDate);
        }
        catch (ParseException e)
        {
            logger.error(e);
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(updatedSetupFeeDate);
        //Make The DB Call To Get last setup added date by passing memberId
        if(chargeVO.getFrequency().equals(Charge_frequency.One_Time.toString()))
        {
            setupCounter=1;
            setupChargeVO.setLastChargeDate(strCurrentDate);
        }
        else if(chargeVO.getFrequency().equals(Charge_frequency.Weekly.toString()))
        {
            setupCounter=(days/7)+1;
            cal.add(Calendar.DATE,(int)setupCounter*7); // add counter*7 days in lastsetupfee date
            updatedSetupFeeDate = cal.getTime();
            setupChargeVO.setLastChargeDate(targetFormat.format(updatedSetupFeeDate));
        }
        else if(chargeVO.getFrequency().equals(Charge_frequency.Monthly.toString()))
        {
            setupCounter=(days/30)+1;
            cal.add(Calendar.DATE,(int)setupCounter*30); // add counter*30 days in lastsetupfee date
            updatedSetupFeeDate = cal.getTime();
            setupChargeVO.setLastChargeDate(targetFormat.format(updatedSetupFeeDate));
        }
        else if(chargeVO.getFrequency().equals(Charge_frequency.Yearly.toString()))
        {
            setupCounter=(days/365)+1;
            cal.add(Calendar.DATE,(int)setupCounter*365); // add 10 days
            updatedSetupFeeDate = cal.getTime();
            setupChargeVO.setLastChargeDate(targetFormat.format(updatedSetupFeeDate));
        }
        setupChargeVO.setCount(setupCounter);
        setupChargeVO.setAmount(0.00);
        setupChargeVO.setTotal(setupCounter*Double.valueOf(chargeVO.getPartnerChargeValue()));

        return setupChargeVO;
    }

    public SetupChargeVO applySetupChargeForWireReport(ChargeVO chargeVO, TerminalVO terminalVO)throws ParseException//sandip
    {
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
        SetupChargeVO  setupChargeVO=new SetupChargeVO();

        String strCurrentDate=null;
        Date currentDate=null;

        long setupCounter=0;
        boolean oneTimeChargeFlag=false;
        currentDate=new Date();
        strCurrentDate=targetFormat.format(currentDate);

        setupChargeVO.setChargeName(chargeVO.getChargename());
        setupChargeVO.setChargeValue(chargeVO.getChargevalue());
        setupChargeVO.setValueType(chargeVO.getValuetype());

        String memberFirstTransactionDate=payoutDAO.getMemberFirstTransactionDate(terminalVO.getMemberId(),terminalVO.getAccountId());
        String lastSetFeeDate=payoutDAO.getLastSetupFeeDate(terminalVO.getMemberId(),terminalVO.getTerminalId());
        if(lastSetFeeDate==null)
        {
            oneTimeChargeFlag=true;
        }

        if(chargeVO.getFrequency().equals(Charge_frequency.One_Time.toString()))
        {
            if(oneTimeChargeFlag)
            {
                setupCounter=1;
                setupChargeVO.setLastChargeDate(strCurrentDate);
            }
            else
            {
                setupChargeVO.setLastChargeDate(strCurrentDate);
            }
        }
        else if(chargeVO.getFrequency().equals(Charge_frequency.Weekly.toString()))
        {
            long totalWeekCount=0;
            long ChargedCount=0;
            long dy=Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)),targetFormat.format(targetFormat.parse(strCurrentDate)));
            totalWeekCount=dy/7+1;
            if(lastSetFeeDate!=null)
            {
                long d=Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)),targetFormat.format(targetFormat.parse(lastSetFeeDate)));
                ChargedCount=d/7+1;
            }
            setupCounter=totalWeekCount-ChargedCount;
        }
        else if(chargeVO.getFrequency().equals(Charge_frequency.Monthly.toString()))
        {
            long totalMonthCount=0;
            long ChargedCount=0;
            long dy=Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)),targetFormat.format(targetFormat.parse(strCurrentDate)));
            totalMonthCount=dy/30+1;
            if(lastSetFeeDate!=null)
            {
                long d=Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)),targetFormat.format(targetFormat.parse(lastSetFeeDate)));
                ChargedCount=d/30+1;
            }
            setupCounter=totalMonthCount-ChargedCount;
        }
        else if(chargeVO.getFrequency().equals(Charge_frequency.Yearly.toString()))
        {
            long totalYearCount=0;
            long ChargedCount=0;
            long dy=Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)),targetFormat.format(targetFormat.parse(strCurrentDate)));
            totalYearCount=dy/365+1;
            if(lastSetFeeDate!=null)
            {
                long d=Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)),targetFormat.format(targetFormat.parse(lastSetFeeDate)));
                ChargedCount=d/365+1;
            }
            setupCounter=totalYearCount-ChargedCount;
        }
        strCurrentDate=targetFormat.format(currentDate);
        setupChargeVO.setLastChargeDate(strCurrentDate);
        setupChargeVO.setCount(setupCounter);
        setupChargeVO.setAmount(0.00);
        setupChargeVO.setTotal(setupCounter*Double.valueOf(chargeVO.getChargevalue()));

        return setupChargeVO;
    }

    public SetupChargeVO applySetupChargeUptoSettledDate(ChargeVO chargeVO, TerminalVO terminalVO,SettlementDateVO settlementDateVO)throws ParseException//sandip
    {
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
        SetupChargeVO  setupChargeVO=new SetupChargeVO();

        //String strCurrentDate=null;
        Date currentDate=null;
        String setupFeeDateUpto=settlementDateVO.getSettlementEndDate();
        long setupCounter=0;
        boolean oneTimeChargeFlag=false;
        //currentDate=new Date();
        //strCurrentDate=targetFormat.format(currentDate);

        setupChargeVO.setChargeName(chargeVO.getChargename());
        setupChargeVO.setChargeValue(chargeVO.getChargevalue());
        setupChargeVO.setValueType(chargeVO.getValuetype());

        String memberFirstTransactionDate=payoutDAO.getMemberFirstTransactionDateOnAccountId(terminalVO.getMemberId(), terminalVO.getAccountId(),terminalVO.getTerminalId());
        String lastSetFeeDate=payoutDAO.getLastSetupFeeDateByMemberId(terminalVO.getMemberId(), terminalVO.getAccountId(),terminalVO.getTerminalId());
        if(lastSetFeeDate==null)
        {
            oneTimeChargeFlag=true;
        }

        if(chargeVO.getFrequency().equals(Charge_frequency.One_Time.toString()))
        {
            if(oneTimeChargeFlag)
            {
                setupCounter=1;
                setupChargeVO.setLastChargeDate(setupFeeDateUpto);
            }
            else
            {
                setupChargeVO.setLastChargeDate(setupFeeDateUpto);
            }
        }
        else if(chargeVO.getFrequency().equals(Charge_frequency.Weekly.toString()))
        {
            long totalWeekCount=0;
            long ChargedCount=0;
            long dy=Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)),targetFormat.format(targetFormat.parse(setupFeeDateUpto)));
            totalWeekCount=dy/7+1;
            if(lastSetFeeDate!=null)
            {
                long d=Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)),targetFormat.format(targetFormat.parse(lastSetFeeDate)));
                ChargedCount=d/7+1;
            }
            setupCounter=totalWeekCount-ChargedCount;
        }
        else if(chargeVO.getFrequency().equals(Charge_frequency.Monthly.toString()))
        {
            long totalMonthCount=0;
            long ChargedCount=0;
            long dy=Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)),targetFormat.format(targetFormat.parse(setupFeeDateUpto)));
            totalMonthCount=dy/30+1;
            if(lastSetFeeDate!=null)
            {
                long d=Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)),targetFormat.format(targetFormat.parse(lastSetFeeDate)));
                ChargedCount=d/30+1;
            }
            setupCounter=totalMonthCount-ChargedCount;
        }
        else if(chargeVO.getFrequency().equals(Charge_frequency.Yearly.toString()))
        {
            long totalYearCount=0;
            long ChargedCount=0;
            long dy=Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)),targetFormat.format(targetFormat.parse(setupFeeDateUpto)));
            totalYearCount=dy/365+1;
            if(lastSetFeeDate!=null)
            {
                long d=Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)),targetFormat.format(targetFormat.parse(lastSetFeeDate)));
                ChargedCount=d/365+1;
            }
            setupCounter=totalYearCount-ChargedCount;
        }
        //strCurrentDate=targetFormat.format(currentDate);
        setupChargeVO.setLastChargeDate(setupFeeDateUpto);
        double total=setupCounter*Double.valueOf(chargeVO.getChargevalue());

        setupChargeVO.setCount(setupCounter);
        setupChargeVO.setAmount(0.00);
        setupChargeVO.setTotal(Functions.roundDBL(total,2));

        return setupChargeVO;
    }

    public CommissionDetailsVO getAgentCommissionFromMerchantSetupFee(AgentCommissionVO agentCommissionVO, TerminalVO terminalVO, SettlementDateVO settlementDateVO)throws ParseException//sandip
    {
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
        String setupFeeDateUpto=payoutDAO.getMerchantWireCoveredDate(terminalVO.getMemberId(),terminalVO.getAccountId(),terminalVO.getTerminalId(),settlementDateVO.getSettlementcycleNumber());//settlementDateVO.getSettlementEndDate();
        long setupCounter=0;
        boolean oneTimeChargeFlag=false;

        ChargeMasterVO chargeMasterVO=agentCommissionVO.getChargeMasterVO();
        CommissionDetailsVO chargeDetailsVO=new CommissionDetailsVO();

        chargeDetailsVO.setChargeName(chargeMasterVO.getChargeName());
        chargeDetailsVO.setValueType(chargeMasterVO.getValueType());
        chargeDetailsVO.setChargeValue(String.valueOf(agentCommissionVO.getCommissionValue()));

        String memberFirstTransactionDate=payoutDAO.getMemberFirstTransactionDateOnTerminal(terminalVO);
        String lastSetFeeDate=payoutDAO.getMerchantWireLastCoveredDate(terminalVO.getMemberId(),terminalVO.getAccountId(),terminalVO.getTerminalId(),settlementDateVO.getSettlementcycleNumber());
        if(lastSetFeeDate==null)
        {
            oneTimeChargeFlag=true;
        }
        if(chargeMasterVO.getFrequency().equals(Charge_frequency.One_Time.toString()))
        {
            if(oneTimeChargeFlag)
            {
                setupCounter=1;
            }
        }
        else if(chargeMasterVO.getFrequency().equals(Charge_frequency.Weekly.toString()))
        {
            long totalWeekCount=0;
            long ChargedCount=0;
            long dy=Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)),targetFormat.format(targetFormat.parse(setupFeeDateUpto)));
            totalWeekCount=dy/7+1;
            if(lastSetFeeDate!=null)
            {
                long d=Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)),targetFormat.format(targetFormat.parse(lastSetFeeDate)));
                ChargedCount=d/7+1;
            }
            setupCounter=totalWeekCount-ChargedCount;
        }
        else if(chargeMasterVO.getFrequency().equals(Charge_frequency.Monthly.toString()))
        {
            long totalMonthCount=0;
            long ChargedCount=0;
            long dy=Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)),targetFormat.format(targetFormat.parse(setupFeeDateUpto)));
            totalMonthCount=dy/30+1;
            if(lastSetFeeDate!=null)
            {
                long d=Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)),targetFormat.format(targetFormat.parse(lastSetFeeDate)));
                ChargedCount=d/30+1;
            }
            setupCounter=totalMonthCount-ChargedCount;
        }
        else if(chargeMasterVO.getFrequency().equals(Charge_frequency.Yearly.toString()))
        {
            long totalYearCount=0;
            long ChargedCount=0;
            if(setupFeeDateUpto!=null)
            {
                long dy = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)), targetFormat.format(targetFormat.parse(setupFeeDateUpto)));
                totalYearCount = dy / 365 + 1;
            }
            //totalYearCount=dy/365+1;
            if(lastSetFeeDate!=null)
            {
                long d=Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)),targetFormat.format(targetFormat.parse(lastSetFeeDate)));
                ChargedCount=d/365+1;
            }
            setupCounter=totalYearCount-ChargedCount;
        }
        double total=setupCounter*Double.valueOf(agentCommissionVO.getCommissionValue());

        chargeDetailsVO.setCount(setupCounter);
        chargeDetailsVO.setAmount(0.00);
        chargeDetailsVO.setTotal(total);
        return chargeDetailsVO;
    }

    public CommissionDetailsVO getPartnerCommissionFromMerchantSetupFee(PartnerCommissionVO partnerCommissionVO, TerminalVO terminalVO, SettlementDateVO settlementDateVO)throws ParseException//sandip
    {
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
        String setupFeeDateUpto=payoutDAO.getMerchantWireCoveredDate(terminalVO.getMemberId(),terminalVO.getAccountId(),terminalVO.getTerminalId(),settlementDateVO.getSettlementcycleNumber());//settlementDateVO.getSettlementEndDate();
        long setupCounter=0;
        boolean oneTimeChargeFlag=false;

        ChargeMasterVO chargeMasterVO=partnerCommissionVO.getChargeMasterVO();
        CommissionDetailsVO chargeDetailsVO=new CommissionDetailsVO();

        chargeDetailsVO.setChargeName(chargeMasterVO.getChargeName());
        chargeDetailsVO.setValueType(chargeMasterVO.getValueType());
        chargeDetailsVO.setChargeValue(String.valueOf(partnerCommissionVO.getCommissionValue()));

        String memberFirstTransactionDate=payoutDAO.getMemberFirstTransactionDateOnTerminal(terminalVO);
        String lastSetFeeDate=payoutDAO.getMerchantWireLastCoveredDate(terminalVO.getMemberId(),terminalVO.getAccountId(),terminalVO.getTerminalId(),settlementDateVO.getSettlementcycleNumber());
        if(lastSetFeeDate==null)
        {
            oneTimeChargeFlag=true;
        }
        if(chargeMasterVO.getFrequency().equals(Charge_frequency.One_Time.toString()))
        {
            if(oneTimeChargeFlag)
            {
                setupCounter=1;
            }
        }
        else if(chargeMasterVO.getFrequency().equals(Charge_frequency.Weekly.toString()))
        {
            long totalWeekCount=0;
            long ChargedCount=0;
            long dy=Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)),targetFormat.format(targetFormat.parse(setupFeeDateUpto)));
            totalWeekCount=dy/7+1;
            if(lastSetFeeDate!=null)
            {
                long d=Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)),targetFormat.format(targetFormat.parse(lastSetFeeDate)));
                ChargedCount=d/7+1;
            }
            setupCounter=totalWeekCount-ChargedCount;
        }
        else if(chargeMasterVO.getFrequency().equals(Charge_frequency.Monthly.toString()))
        {
            long totalMonthCount=0;
            long ChargedCount=0;
            long dy=Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)),targetFormat.format(targetFormat.parse(setupFeeDateUpto)));
            totalMonthCount=dy/30+1;
            if(lastSetFeeDate!=null)
            {
                long d=Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)),targetFormat.format(targetFormat.parse(lastSetFeeDate)));
                ChargedCount=d/30+1;
            }
            setupCounter=totalMonthCount-ChargedCount;
        }
        else if(chargeMasterVO.getFrequency().equals(Charge_frequency.Yearly.toString()))
        {
            long totalYearCount=0;
            long ChargedCount=0;
            if(setupFeeDateUpto!=null)
            {
                long dy=Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)),targetFormat.format(targetFormat.parse(setupFeeDateUpto)));
                totalYearCount=dy/365+1;
            }

            if(lastSetFeeDate!=null)
            {
                long d=Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)),targetFormat.format(targetFormat.parse(lastSetFeeDate)));
                ChargedCount=d/365+1;
            }
            setupCounter=totalYearCount-ChargedCount;
        }
        double total=setupCounter*Double.valueOf(partnerCommissionVO.getCommissionValue());

        chargeDetailsVO.setCount(setupCounter);
        chargeDetailsVO.setAmount(0.00);
        chargeDetailsVO.setTotal(total);
        return chargeDetailsVO;
    }

    private String getPDFReportFile(double totalFundedToBank,double grossAmount,double previousBalanceAmount,double grossReversal,double grossChargeback, double totalPayoutAmount,ChargeDetailsVO chargebackReversalVO,double refundReverseAmount,double totalProcessingAmount,double grossTotalFee,double gorssReserveGenerated,double gossReserveRefunded,String companyName,String contactPerson/*,String cardType,String paymentType*/,TerminalVO terminalVO,HashMap<String,ChargeDetailsVO> chargeDetailsMapOFVOs,HashMap<String,ReserveGeneratedVO>  reserveGeneratedVOHashMap,HashMap<String,ReserveRefundVO> reserveRefundVOHashMap,SettlementDateVO settlementDateVO,RollingReserveDateVO rollingReserveDateVO,HashMap<String,WireChargeVO> wireChargeVOHashMap,HashMap<String,GrossChargeVO> grossChargeVOHashMap,HashMap<String,SetupChargeVO> setupChargeVOHashMap,HashMap<String,ServiceTaxChargeVO> serviceTaxChargeVOHashMap,HashMap<String,MerchantRandomChargesVO> merchantRandomChargesVOs,double miscGross,String currency,String isPoweredBy,String settlementCurrency,double totalFundedInSettlementCurrency,double exchangeRate,HashMap<String ,StatementChargeVO>statementChargeVOHashMap,boolean isWireFeeRequired,HashMap<String,SettlementExpensesVO> settlementExpensesVOHashMap,double totalFundedToBankBeforeSettlementExpenses,HashMap<String,TotalFeesChargesVO> totalFeesChargesVOHashMap,Double grossFee,HashMap<String,OtherChargesVO> otherChargesVOHashMapFinal)
    {
        AccountUtil accountUtil=new AccountUtil();
        Document document = new Document(PageSize.A3);
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
        String reportFilePath=null;
        String reportFileName=null;
        try
        {
            String sMemberId=terminalVO.getMemberId();
            String sAccountId=terminalVO.getAccountId();
            String sStartTransactionDate=settlementDateVO.getSettlementStartDate();
            String sEndTransactionDate=settlementDateVO.getSettlementEndDate();


            reportFileName=accountUtil.getReportFileName(sMemberId,sAccountId,terminalVO.getTerminalId(),sStartTransactionDate,sEndTransactionDate);
            HashMap partnerDetails=payoutDAO.getPartnerDetailsFromTerminal(sMemberId, terminalVO.getTerminalId());
            reportFileName=reportFileName+".pdf";
            //String partnerName=(String)partnerDetails.get("partnername");
            String partnerLogoName=(String)partnerDetails.get("logoName");
            String addressDetails=(String)partnerDetails.get("address");
            String telNo=(String)partnerDetails.get("telno");
            String companySupportMailId=(String)partnerDetails.get("companysupportmailid");
            String backgroundColor=(String)partnerDetails.get("reportfile_bgcolor");
            Color bgColor=Color.gray.brighter();
            if("Blue".equalsIgnoreCase(backgroundColor)){
                bgColor=new Color(0,127,255);
            }
            else if("Orange".equalsIgnoreCase(backgroundColor)){
                bgColor=new Color(245, 130, 42);
            }

            if(!functions.isValueNull(partnerLogoName)){
                partnerLogoName="pay2.png";
            }

            File filepath=new File(PAYOUT_REPORT_FILE_PATH+reportFileName);
            reportFilePath=filepath.getPath();

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filepath));
            if(functions.isValueNull(addressDetails) && functions.isValueNull(companySupportMailId) && functions.isValueNull(telNo)){
                Phrase phraseLine1=new Phrase(addressDetails);
                Phrase phraseLine2=new Phrase(" I "+companySupportMailId+" I "+telNo+"");
                writer.setBoxSize("art", writer.getPageSize());
                HeaderFooterPageEvent event = new HeaderFooterPageEvent(phraseLine1,phraseLine2);
                writer.setPageEvent(event);
            }

            document.open();
            // demonstrate some table features
            Table table = new Table(7);
            table.setWidth(100);
            table.setBorder(Table.NO_BORDER);

            /*table.setBorderWidth(1);*/
            table.setBorderColor(new Color(0, 0,0));
            table.setPadding(1);
            /*table.setSpacing(1);*/

            String reportingDate=targetFormat.format(new Date());
            Image partnerImageInstance=null;
            File file=new File(PARTNER_LOGO_PATH+partnerLogoName);
            if(file.exists())
            {
                partnerImageInstance = Image.getInstance(PARTNER_LOGO_PATH + partnerLogoName);
            }
            //partnerImageInstance.scaleAbsolute(150f, 150f);
            //partnerImageInstance.scaleAbsoluteHeight(70f);

            //final BaseFont font = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED);
            Font f1 = FontFactory.getFont(FontFactory.TIMES_ROMAN,20);
            f1.setColor(Color.BLACK);

            Font f2 = FontFactory.getFont(FontFactory.TIMES_BOLD,15);
            f2.setColor(Color.WHITE);

            Font f3 = FontFactory.getFont(FontFactory.TIMES_BOLD,15);
            f3.setColor(Color.BLACK);

            Cell partnerNameCaptionCell=new Cell(new Paragraph(20, "SETTLEMENT REPORT",f1));

            Cell partnerLogoCell=null;
            if(partnerImageInstance!=null){
                partnerLogoCell=new Cell(partnerImageInstance);
            }else{
                partnerLogoCell=new Cell("");
            }

            partnerNameCaptionCell.setColspan(4);
            partnerLogoCell.setColspan(3);

            partnerLogoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            partnerLogoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            partnerNameCaptionCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            partnerNameCaptionCell.setBackgroundColor(Color.white.brighter());

            partnerLogoCell.setBorder(Cell.NO_BORDER);
            partnerNameCaptionCell.setBorder(Cell.NO_BORDER);

            table.addCell(partnerNameCaptionCell);
            table.addCell(partnerNameCaptionCell);
            table.addCell(partnerLogoCell);

            Cell sMemberDetailsHeaderCell=new Cell(new Paragraph(20,"Member Details",f2));
            sMemberDetailsHeaderCell.setColspan(7);
            sMemberDetailsHeaderCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            sMemberDetailsHeaderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            sMemberDetailsHeaderCell.setBackgroundColor(bgColor);
            table.addCell(sMemberDetailsHeaderCell);

            Cell sMemberIdCaptionCell=new Cell("Member ID:");
            Cell sMemberIdValueCell=new Cell(terminalVO.getMemberId());
            sMemberIdCaptionCell.setColspan(4);
            sMemberIdValueCell.setColspan(3);
            table.addCell(sMemberIdCaptionCell);
            table.addCell(sMemberIdValueCell);

            Cell sCompanyNameCaptionCell=new Cell("Company Name:");
            Cell sCompanyNameValueCell=new Cell(companyName);
            sCompanyNameCaptionCell.setColspan(4);
            sCompanyNameValueCell.setColspan(3);
            table.addCell(sCompanyNameCaptionCell);
            table.addCell(sCompanyNameValueCell);

            Cell sContactPersonCaptionCell=new Cell("Contact Person:");
            Cell sContactPersonValueCell=new Cell(contactPerson);
            sContactPersonCaptionCell.setColspan(4);
            sContactPersonValueCell.setColspan(3);
            table.addCell(sContactPersonCaptionCell);
            table.addCell(sContactPersonValueCell);

            Cell sTerminalCaptionCell=new Cell("Terminal Id:");
            Cell sTerminalValueCell=new Cell(terminalVO.getTerminalId());
            sTerminalCaptionCell.setColspan(4);
            sTerminalValueCell.setColspan(3);
            table.addCell(sTerminalCaptionCell);
            table.addCell(sTerminalValueCell);

            Cell sCurrencyCaptionCell=new Cell("Processing Currency:");
            Cell sCurrencyValueCell=new Cell(currency);
            sCurrencyCaptionCell.setColspan(4);
            sCurrencyValueCell.setColspan(3);
            table.addCell(sCurrencyCaptionCell);
            table.addCell(sCurrencyValueCell);

            Cell sSettlementCaptionCell=new Cell("Settlement Currency:");
            Cell sSettlementValueCell=new Cell(settlementCurrency);
            sSettlementCaptionCell.setColspan(4);
            sSettlementValueCell.setColspan(3);
            table.addCell(sSettlementCaptionCell);
            table.addCell(sSettlementValueCell);

            Cell sSettlePeriodCaptionCell=new Cell("Settle Transaction Period:");
            Cell sSettleStartValueCell=new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getSettlementStartDate()))+" TO "+targetFormat.format(targetFormat.parse(settlementDateVO.getSettlementEndDate())));

            sSettlePeriodCaptionCell.setColspan(4);
            sSettleStartValueCell.setColspan(3);

            table.addCell(sSettlePeriodCaptionCell);
            table.addCell(sSettleStartValueCell);

            Cell sDeclinedPeriodCaptionCell=new Cell("Decline Transaction Period:");
            Cell sDeclinedStartValueCell=new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getDeclinedStartDate()))+" TO "+targetFormat.format(targetFormat.parse(settlementDateVO.getDeclinedEndDate())));

            sDeclinedPeriodCaptionCell.setColspan(4);
            sDeclinedStartValueCell.setColspan(3);

            table.addCell(sDeclinedPeriodCaptionCell);
            table.addCell(sDeclinedStartValueCell);

            Cell sReversedPeriodCaptionCell=new Cell("Reversal Transaction Period:");
            Cell sReversedStartValueCell=new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getReversedStartDate()))+" TO "+targetFormat.format(targetFormat.parse(settlementDateVO.getReversedEndDate())));

            sReversedPeriodCaptionCell.setColspan(4);
            sReversedStartValueCell.setColspan(3);

            table.addCell(sReversedPeriodCaptionCell);
            table.addCell(sReversedStartValueCell);

            Cell sChargebackPeriodCaptionCell=new Cell("Chargeback Transaction Period:");
            Cell sChargebackStartValueCell=new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getChargebackStartDate()))+" TO "+targetFormat.format(targetFormat.parse(settlementDateVO.getChargebackEndDate())));

            sChargebackPeriodCaptionCell.setColspan(4);
            sChargebackStartValueCell.setColspan(3);

            table.addCell(sChargebackPeriodCaptionCell);
            table.addCell(sChargebackStartValueCell);

            Cell sChargebackReversedPeriodCaptionCell=new Cell("Chargeback Reversed Transaction Period:");
            Cell sChargebackReversedStartValueCell=new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getChargebackStartDate()))+" TO "+targetFormat.format(targetFormat.parse(settlementDateVO.getChargebackEndDate())));

            sChargebackReversedPeriodCaptionCell.setColspan(4);
            sChargebackReversedStartValueCell.setColspan(3);

            table.addCell(sChargebackReversedPeriodCaptionCell);
            table.addCell(sChargebackReversedStartValueCell);

            Cell sPayoutPeriodCaptionCell=new Cell("Payout Transaction Period:");
            Cell sPayoutStartValueCell=new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getReversedStartDate()))+" TO "+targetFormat.format(targetFormat.parse(settlementDateVO.getReversedEndDate())));

            sPayoutPeriodCaptionCell.setColspan(4);
            sPayoutStartValueCell.setColspan(3);

            table.addCell(sPayoutPeriodCaptionCell);
            table.addCell(sPayoutStartValueCell);

            Cell sRollingReserveReleaseUptoDateCaptionCell=new Cell("Rolling Release Period:");
            Cell sRollingReserveReleaseStartDateValueCell=new Cell(targetFormat.format(targetFormat.parse(rollingReserveDateVO.getRollingReserveStartDate()))+" TO "+targetFormat.format(targetFormat.parse(rollingReserveDateVO.getRollingReserveEndDate())));

            sRollingReserveReleaseUptoDateCaptionCell.setColspan(4);
            sRollingReserveReleaseStartDateValueCell.setColspan(3);

            table.addCell(sRollingReserveReleaseUptoDateCaptionCell);
            table.addCell(sRollingReserveReleaseStartDateValueCell);

            Cell sChargesDetailsHeaderCell=new Cell(new Paragraph(20,"Charges Details",f2));
            sChargesDetailsHeaderCell.setColspan(7);
            sChargesDetailsHeaderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            sChargesDetailsHeaderCell.setBackgroundColor(bgColor);
            table.addCell(sChargesDetailsHeaderCell);

            Cell sChargeNameHeader = new Cell(new Paragraph(20,"Charge Name",f3));
            Cell sChargeRateHeader = new Cell(new Paragraph(20,"Rate/Fee",f3));
            Cell sChargeTerminalHeader = new Cell(new Paragraph(20,"Terminal",f3));
            Cell sChargeCounterHeader = new Cell(new Paragraph(20,"Counter",f3));
            Cell sChargeAmountHeader = new Cell(new Paragraph(20,"Amount",f3));
            Cell sChargeTotalHeader = new Cell(new Paragraph(20,"Total",f3));

            sChargeNameHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            sChargeRateHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            sChargeTerminalHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            sChargeCounterHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            sChargeAmountHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            sChargeTotalHeader.setHorizontalAlignment(Element.ALIGN_CENTER);

            sChargeNameHeader.setColspan(2);

            table.addCell(sChargeNameHeader);
            table.addCell(sChargeTerminalHeader);
            table.addCell(sChargeCounterHeader);
            table.addCell(sChargeAmountHeader);
            table.addCell(sChargeRateHeader);
            table.addCell(sChargeTotalHeader);

            Cell sChargeNameValue,sChargeRateValue,sChargeTerminalValue,sChargeCounterValue,sChargeAmountValue,sChargeTotalValue;

            Set set=chargeDetailsMapOFVOs.keySet();
            Iterator itr=set.iterator();
            String terminalId="-";
            while(itr.hasNext())
            {
                String chargeName=(String)itr.next();
                ChargeDetailsVO detailsVO=chargeDetailsMapOFVOs.get(chargeName);
                String vDblAmountChar=(Functions.round(detailsVO.getAmount(),2));
                String vCntCounter="-";
                String[] arr=chargeName.split(":");
                sChargeNameValue = new Cell(arr[0]);
                terminalId=arr[1];
                sChargeTerminalValue = new Cell(arr[1]);
                sChargeTotalValue = new Cell(Functions.round(detailsVO.getTotal(),2));
                if("Percentage".equals(detailsVO.getValueType()))
                {
                    sChargeRateValue = new Cell(detailsVO.getChargeValue()+"%");
                }
                else
                {
                    sChargeRateValue = new Cell(detailsVO.getChargeValue());
                    vCntCounter=(new Long(detailsVO.getCount())).toString();
                }
                sChargeCounterValue = new Cell(vCntCounter);
                sChargeAmountValue = new Cell(vDblAmountChar);

                sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTerminalValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                sChargeCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                sChargeNameValue.setColspan(2);

                table.addCell(sChargeNameValue);
                table.addCell(sChargeTerminalValue);
                table.addCell(sChargeCounterValue);
                table.addCell(sChargeAmountValue);
                table.addCell(sChargeRateValue);
                table.addCell(sChargeTotalValue);

            }
            Set setupChargeSet=setupChargeVOHashMap.keySet();
            Iterator setupChargeItr=setupChargeSet.iterator();
            while(setupChargeItr.hasNext())
            {
                SetupChargeVO setupChargeVO=setupChargeVOHashMap.get(setupChargeItr.next());
                sChargeNameValue = new Cell(setupChargeVO.getChargeName());
                sChargeRateValue = new Cell(setupChargeVO.getChargeValue());
                sChargeTerminalValue = new Cell("-");
                sChargeCounterValue = new Cell(new Long(setupChargeVO.getCount()).toString());
                sChargeAmountValue = new Cell("-");
                sChargeTotalValue = new Cell(Functions.round(setupChargeVO.getTotal(),2));

                sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTerminalValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                sChargeCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                sChargeNameValue.setColspan(2);

                table.addCell(sChargeNameValue);
                table.addCell(sChargeTerminalValue);
                table.addCell(sChargeCounterValue);
                table.addCell(sChargeAmountValue);
                table.addCell(sChargeRateValue);
                table.addCell(sChargeTotalValue);
            }
            if(chargebackReversalVO.getCount()>0)
            {
                sChargeNameValue = new Cell("Chargeback Reversal");
                sChargeRateValue = new Cell(chargebackReversalVO.getChargeValue());
                sChargeTerminalValue = new Cell(terminalId);
                sChargeCounterValue = new Cell(new Long(chargebackReversalVO.getCount()).toString());
                sChargeAmountValue = new Cell(Functions.round(chargebackReversalVO.getAmount(), 2));
                sChargeTotalValue = new Cell(Functions.round(-chargebackReversalVO.getTotal(), 2));

                sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTerminalValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                sChargeCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                sChargeNameValue.setColspan(2);

                table.addCell(sChargeNameValue);
                table.addCell(sChargeTerminalValue);
                table.addCell(sChargeCounterValue);
                table.addCell(sChargeAmountValue);
                table.addCell(sChargeRateValue);
                table.addCell(sChargeTotalValue);
            }

            Cell sChargeFeeTotalCaption=new Cell(new Paragraph(20,"Total",f3));
            Cell sChargeFeeTotalValue=new Cell(new Paragraph(20,Functions.round(grossTotalFee,2),f3));
            sChargeFeeTotalCaption.setColspan(6);
            sChargeFeeTotalCaption.setHeader(true);
            sChargeFeeTotalCaption.setHorizontalAlignment(Element.ALIGN_LEFT);
            sChargeFeeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(sChargeFeeTotalCaption);
            table.addCell(sChargeFeeTotalValue);

            Cell sGeneratedReserveDetailsHeaderCell=new Cell(new Paragraph(20,"Generated Reserve Details",f2));
            sGeneratedReserveDetailsHeaderCell.setColspan(7);
            sGeneratedReserveDetailsHeaderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            sGeneratedReserveDetailsHeaderCell.setBackgroundColor(bgColor);
            table.addCell(sGeneratedReserveDetailsHeaderCell);

            Set generatedRollingReserveSet=reserveGeneratedVOHashMap.keySet();
            Iterator generatedRollingReserveIterator=generatedRollingReserveSet.iterator();
            while(generatedRollingReserveIterator.hasNext())
            {
                String generatedRollingReserveChargeName=(String)generatedRollingReserveIterator.next();
                ReserveGeneratedVO generatedVO=reserveGeneratedVOHashMap.get(generatedRollingReserveChargeName);

                //String arr[]=generatedRollingReserveChargeName.split(":");
                sChargeNameValue = new Cell(generatedRollingReserveChargeName);
                sChargeTerminalValue = new Cell("-");
                sChargeCounterValue = new Cell("-"/*(new Long(generatedRollingReserveChargeDetails.get("vCntTotal").toString())).toString()*/);
                sChargeAmountValue = new Cell(Functions.round(generatedVO.getAmount(),2));
                sChargeTotalValue = new Cell(Functions.round(generatedVO.getTotal(),2));

                sChargeRateValue = new Cell(generatedVO.getChargeValue()+"%");

                sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTerminalValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                sChargeCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                sChargeNameValue.setColspan(2);

                table.addCell(sChargeNameValue);
                table.addCell(sChargeTerminalValue);
                table.addCell(sChargeCounterValue);
                table.addCell(sChargeAmountValue);
                table.addCell(sChargeRateValue);
                table.addCell(sChargeTotalValue);

            }

            Cell sReserveGeneratedTotalCaption=new Cell(new Paragraph(20,"Total",f3));
            Cell sReserveGeneratedTotalValue=new Cell(new Paragraph(20,Functions.round(gorssReserveGenerated,2),f3));
            sReserveGeneratedTotalCaption.setColspan(6);
            sReserveGeneratedTotalCaption.setHeader(true);
            sReserveGeneratedTotalCaption.setHorizontalAlignment(Element.ALIGN_LEFT);
            sReserveGeneratedTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(sReserveGeneratedTotalCaption);
            table.addCell(sReserveGeneratedTotalValue);

            Cell sRefundedReserveDetailsHeaderCell=new Cell(new Paragraph(20,"Refunded Reserve Details",f2));
            sRefundedReserveDetailsHeaderCell.setColspan(7);
            sRefundedReserveDetailsHeaderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            sRefundedReserveDetailsHeaderCell.setBackgroundColor(bgColor);
            table.addCell(sRefundedReserveDetailsHeaderCell);

            Set refundedRollingReserveSet=reserveRefundVOHashMap.keySet();
            Iterator refundedRollingReserveIterator=refundedRollingReserveSet.iterator();
            while(refundedRollingReserveIterator.hasNext())
            {
                String refundedRollingReserveChargeName=(String)refundedRollingReserveIterator.next();
                ReserveRefundVO reserveRefundVO=reserveRefundVOHashMap.get(refundedRollingReserveChargeName);

                sChargeNameValue = new Cell(refundedRollingReserveChargeName);
                sChargeCounterValue = new Cell("-"/*(new Long(refundedRollingReserveChargeDetails.get("vCntTotal").toString())).toString()*/);
                sChargeAmountValue = new Cell(Functions.round(reserveRefundVO.getAmount(),2));
                sChargeTotalValue = new Cell(Functions.round(reserveRefundVO.getTotal(),2));
                sChargeRateValue = new Cell(reserveRefundVO.getChargeValue()+"%");
                sChargeTerminalValue = new Cell("-");

                sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTerminalValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                sChargeCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                sChargeNameValue.setColspan(2);

                table.addCell(sChargeNameValue);
                table.addCell(sChargeTerminalValue);
                table.addCell(sChargeCounterValue);
                table.addCell(sChargeAmountValue);
                table.addCell(sChargeRateValue);
                table.addCell(sChargeTotalValue);
            }

            Cell sReserveRefundedTotalCaption=new Cell(new Paragraph(20,"Total",f3));
            Cell sReserveRefundedTotalValue=new Cell(new Paragraph(20,Functions.round(gossReserveRefunded,2),f3));

            sReserveRefundedTotalCaption.setColspan(6);
            sReserveRefundedTotalCaption.setHeader(true);
            sReserveRefundedTotalCaption.setHorizontalAlignment(Element.ALIGN_LEFT);
            sReserveRefundedTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);


            table.addCell(sReserveRefundedTotalCaption);
            table.addCell(sReserveRefundedTotalValue);

            Cell sPayoutReportSummaryHeaderCell=new Cell(new Paragraph(20,"Summary",f2));
            sPayoutReportSummaryHeaderCell.setColspan(7);
            sPayoutReportSummaryHeaderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            sPayoutReportSummaryHeaderCell.setBackgroundColor(bgColor);
            table.addCell(sPayoutReportSummaryHeaderCell);

            /*//Titles added in order to change lable sequence to add value type
            Cell summChargeNameHeader = new Cell(new Paragraph(20,"Charge Name",f3));
            Cell summChargeChargeUnit = new Cell(new Paragraph(20,"Charge Unit",f3));
            Cell summChargeRateHeader = new Cell(new Paragraph(20,"Rate/Fee",f3));
            Cell summChargeCounterHeader = new Cell(new Paragraph(20,"Counter",f3));
            Cell summChargeAmountHeader = new Cell(new Paragraph(20,"Amount",f3));
            Cell summChargeTotalHeader = new Cell(new Paragraph(20,"Total",f3));

            summChargeNameHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            summChargeChargeUnit.setHorizontalAlignment(Element.ALIGN_CENTER);
            summChargeRateHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            summChargeCounterHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            summChargeAmountHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            summChargeTotalHeader.setHorizontalAlignment(Element.ALIGN_CENTER);

            summChargeNameHeader.setColspan(2);

            table.addCell(summChargeNameHeader);
            table.addCell(summChargeChargeUnit);
            table.addCell(summChargeRateHeader);
            table.addCell(summChargeCounterHeader);
            table.addCell(summChargeAmountHeader);
            table.addCell(summChargeTotalHeader);*/

            Cell sTotalProcessingAmountCaptionCell=new Cell("Total Processing Amount");
            Cell sTotalProcessingAmountValueCell=new Cell(Functions.round(totalProcessingAmount,2));
            sTotalProcessingAmountCaptionCell.setColspan(6);
            sTotalProcessingAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sTotalProcessingAmountCaptionCell);
            table.addCell(sTotalProcessingAmountValueCell);

            Cell sTotalFeesCaptionCell=new Cell(new Paragraph(20,"Total Fees",f3));
            Cell sTotalFeesValueCell=new Cell(isValidAmount(Functions.round(grossTotalFee,2)));
            sTotalFeesCaptionCell.setColspan(6);
            sTotalFeesValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sTotalFeesCaptionCell);
            table.addCell(sTotalFeesValueCell);

            if(totalFeesChargesVOHashMap!=null && totalFeesChargesVOHashMap.size()>0){
                Set totalFeesChargeSet=totalFeesChargesVOHashMap.keySet();
                Iterator totalFeesChargeIterator=totalFeesChargeSet.iterator();
                while (totalFeesChargeIterator.hasNext()){
                    String totalFeesChargeName= (String) totalFeesChargeIterator.next();
                    TotalFeesChargesVO totalFeesChargesVO=totalFeesChargesVOHashMap.get(totalFeesChargeName);
                    sChargeNameValue = new Cell(totalFeesChargeName);
                    sChargeTerminalValue = new Cell(totalFeesChargesVO.getValueType());
                    if("Percentage".equals(totalFeesChargesVO.getValueType()))
                    {
                        sChargeRateValue = new Cell(totalFeesChargesVO.getChargeValue()+"%");
                    }else{
                        sChargeRateValue = new Cell(totalFeesChargesVO.getChargeValue());
                    }
                    sChargeCounterValue = new Cell("-");
                    sChargeAmountValue = new Cell(Functions.round(grossFee,2));
                    sChargeTotalValue = new Cell("-"+Functions.round(totalFeesChargesVO.getTotal(),2));

                    sChargeTerminalValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                    sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    sChargeCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    sChargeAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    sChargeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                    sChargeNameValue.setColspan(4);

                    table.addCell(sChargeNameValue);
                   // table.addCell(sChargeTerminalValue);
                   // table.addCell(sChargeCounterValue);
                    table.addCell(sChargeAmountValue);
                    table.addCell(sChargeRateValue);
                    table.addCell(sChargeTotalValue);
                }
            }

            Cell sTotalReversalCaptionCell=new Cell("Total Reversal");
            Cell sTotalReversalValueCell=new Cell(isValidAmount(Functions.round(grossReversal,2)));
            sTotalReversalCaptionCell.setColspan(6);
            sTotalReversalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(sTotalReversalCaptionCell);
            table.addCell(sTotalReversalValueCell);

            Cell sTotalChargebackCaptionCell=new Cell("Total Chargeback");
            Cell sTotalChargebackValueCell=new Cell(isValidAmount(Functions.round(grossChargeback,2)));
            sTotalChargebackCaptionCell.setColspan(6);
            sTotalChargebackValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(sTotalChargebackCaptionCell);
            table.addCell(sTotalChargebackValueCell);

            Cell sTotalPayoutReversalCaptionCell=new Cell("Total Payout Amount");
            Cell sTotalPayoutValueCell=new Cell(isValidAmount(Functions.round(totalPayoutAmount ,2)));
            sTotalPayoutReversalCaptionCell.setColspan(6);
            sTotalPayoutValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(sTotalPayoutReversalCaptionCell);
            table.addCell(sTotalPayoutValueCell);

            Cell sGeneratedReserveCaptionCell=new Cell("Generated Reserve");
            Cell sGeneratedReserveValueCell=new Cell(isValidAmount(Functions.round(gorssReserveGenerated,2)));
            sGeneratedReserveCaptionCell.setColspan(6);
            sGeneratedReserveValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sGeneratedReserveCaptionCell);
            table.addCell(sGeneratedReserveValueCell);

            //Cell sGrossAmountCaptionCell=new Cell("Gross Amount");
            Cell sGrossAmountCaptionCell=new Cell(new Paragraph(20,"Gross Amount",f3));
            Cell sGrossAmountValueCell=new Cell(new Paragraph(20,Functions.round(grossAmount,2),f3));
            sGrossAmountCaptionCell.setColspan(6);
            sGrossAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sGrossAmountCaptionCell);
            table.addCell(sGrossAmountValueCell);

            if(statementChargeVOHashMap!=null && statementChargeVOHashMap.size()>0){
                Set statementChargeSet=statementChargeVOHashMap.keySet();
                Iterator statementChargeIterator=statementChargeSet.iterator();
                while (statementChargeIterator.hasNext()){
                    String statementChargeName= (String) statementChargeIterator.next();
                    StatementChargeVO statementChargeVO=statementChargeVOHashMap.get(statementChargeName);
                    sChargeNameValue = new Cell(statementChargeVO.getChargeName());
                    sChargeTerminalValue = new Cell(statementChargeVO.getValueType());
                    sChargeRateValue = new Cell(statementChargeVO.getChargeValue());
                    sChargeCounterValue = new Cell(new Long(statementChargeVO.getCount()).toString());
                    sChargeAmountValue = new Cell("-");
                    sChargeTotalValue = new Cell("-"+Functions.round(statementChargeVO.getTotal(),2));

                    sChargeTerminalValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                    sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    sChargeCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    sChargeAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    sChargeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                    sChargeNameValue.setColspan(4);

                    table.addCell(sChargeNameValue);
                    //table.addCell(sChargeTerminalValue);
                    //table.addCell(sChargeCounterValue);
                    table.addCell(sChargeAmountValue);
                    table.addCell(sChargeRateValue);
                    table.addCell(sChargeTotalValue);
                }
            }

            /*if(statementChargeVO!=null){
                sChargeNameValue = new Cell(statementChargeVO.getChargeName());
                sChargeRateValue = new Cell(statementChargeVO.getChargeValue());
                sChargeTerminalValue = new Cell("-");
                sChargeCounterValue = new Cell(new Long(statementChargeVO.getCount()).toString());
                sChargeAmountValue = new Cell("-");
                sChargeTotalValue = new Cell("-"+Functions.round(statementChargeVO.getTotal(),2));

                sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTerminalValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                sChargeCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                sChargeNameValue.setColspan(2);

                table.addCell(sChargeNameValue);
                table.addCell(sChargeRateValue);
                table.addCell(sChargeTerminalValue);
                table.addCell(sChargeCounterValue);
                table.addCell(sChargeAmountValue);
                table.addCell(sChargeTotalValue);
            }*/
            if(wireChargeVOHashMap!=null && wireChargeVOHashMap.size()>0 && isWireFeeRequired){
                Set wireSet=wireChargeVOHashMap.keySet();
                Iterator wireIterator=wireSet.iterator();
                while (wireIterator.hasNext()){
                    String wireChargeName=(String)wireIterator.next();
                    WireChargeVO wireChargeVO=wireChargeVOHashMap.get(wireChargeName);
                    sChargeNameValue = new Cell(wireChargeVO.getChargeName());
                    sChargeTerminalValue = new Cell(wireChargeVO.getValueType());
                    sChargeRateValue = new Cell(wireChargeVO.getChargeValue());
                    sChargeCounterValue = new Cell(new Long(wireChargeVO.getCount()).toString());
                    sChargeAmountValue = new Cell("-");
                    sChargeTotalValue = new Cell("-"+Functions.round(wireChargeVO.getTotal(),2));

                    sChargeTerminalValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                    sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    sChargeCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    sChargeAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    sChargeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                    sChargeNameValue.setColspan(4);

                    table.addCell(sChargeNameValue);
                    //table.addCell(sChargeTerminalValue);
                    //table.addCell(sChargeCounterValue);
                    table.addCell(sChargeAmountValue);
                    table.addCell(sChargeRateValue);
                    table.addCell(sChargeTotalValue);
                }
            }

            /*if(wireChargeVO!=null && isWireFeeRequired)
            {
                sChargeNameValue = new Cell(wireChargeVO.getChargeName());
                sChargeRateValue = new Cell(wireChargeVO.getChargeValue());
                sChargeTerminalValue = new Cell("-");
                sChargeCounterValue = new Cell(new Long(wireChargeVO.getCount()).toString());
                sChargeAmountValue = new Cell("-");
                sChargeTotalValue = new Cell("-"+Functions.round(wireChargeVO.getTotal(),2));

                sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTerminalValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                sChargeCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                sChargeNameValue.setColspan(2);

                table.addCell(sChargeNameValue);
                table.addCell(sChargeRateValue);
                table.addCell(sChargeTerminalValue);
                table.addCell(sChargeCounterValue);
                table.addCell(sChargeAmountValue);
                table.addCell(sChargeTotalValue);
            }*/

            Set grossTypeChargeSet=grossChargeVOHashMap.keySet();

            Iterator grossTypeChargeIterator=grossTypeChargeSet.iterator();
            while(grossTypeChargeIterator.hasNext())
            {
                String grossTypeChargeName=(String)grossTypeChargeIterator.next();
                GrossChargeVO grossChargeVO=grossChargeVOHashMap.get(grossTypeChargeName);

                sChargeNameValue = new Cell(grossTypeChargeName);
                sChargeTerminalValue = new Cell(grossChargeVO.getValueType());
                if("Percentage".equals(grossChargeVO.getValueType()))
                {
                    sChargeRateValue = new Cell(grossChargeVO.getChargeValue()+"%");
                }else{
                    sChargeRateValue = new Cell(grossChargeVO.getChargeValue());
                }
                sChargeCounterValue = new Cell("-");
                sChargeAmountValue = new Cell(Functions.round(grossAmount,2));
                sChargeTotalValue = new Cell(isValidAmount(Functions.round(grossChargeVO.getTotal(),2)));

                sChargeTerminalValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                sChargeNameValue.setColspan(4);

                table.addCell(sChargeNameValue);
                //table.addCell(sChargeTerminalValue);
                //table.addCell(sChargeCounterValue);
                table.addCell(sChargeAmountValue);
                table.addCell(sChargeRateValue);
                table.addCell(sChargeTotalValue);

            }

            Set othercharges=otherChargesVOHashMapFinal.keySet();
            Iterator otherchargesIterator=othercharges.iterator();
            while(otherchargesIterator.hasNext())
            {
                String OtherchargesName=(String)otherchargesIterator.next();
                OtherChargesVO otherChargesVO=otherChargesVOHashMapFinal.get(OtherchargesName);

                sChargeNameValue = new Cell(OtherchargesName);
                sChargeTerminalValue = new Cell(otherChargesVO.getValueType());
                if("Percentage".equals(otherChargesVO.getValueType()))
                {
                    sChargeRateValue = new Cell(otherChargesVO.getChargeValue()+"%");
                }else{
                    sChargeRateValue = new Cell(otherChargesVO.getChargeValue());
                }
                sChargeCounterValue = new Cell("-");
                sChargeAmountValue = new Cell(Functions.round(otherChargesVO.getAmount(),2));
                sChargeTotalValue = new Cell(isValidAmount(Functions.round(otherChargesVO.getTotal(),2)));

                sChargeTerminalValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                sChargeNameValue.setColspan(4);

                table.addCell(sChargeNameValue);
                //table.addCell(sChargeTerminalValue);
                //table.addCell(sChargeCounterValue);
                table.addCell(sChargeAmountValue);
                table.addCell(sChargeRateValue);
                table.addCell(sChargeTotalValue);
            }

            Set serviceTaxSet=serviceTaxChargeVOHashMap.keySet();
            Iterator serviceTaxIterator=serviceTaxSet.iterator();
            while(serviceTaxIterator.hasNext())
            {
                String serviceTaxChargeName=(String)serviceTaxIterator.next();
                ServiceTaxChargeVO serviceTaxChargeVO=serviceTaxChargeVOHashMap.get(serviceTaxChargeName);

                sChargeNameValue = new Cell(serviceTaxChargeName);
                sChargeTerminalValue = new Cell(serviceTaxChargeVO.getValueType());
                sChargeRateValue = new Cell(serviceTaxChargeVO.getChargeValue()+"%");
                sChargeCounterValue = new Cell("-");
                sChargeAmountValue = new Cell(Functions.round(serviceTaxChargeVO.getAmount(),2));
                sChargeTotalValue = new Cell(isValidAmount(Functions.round(serviceTaxChargeVO.getTotal(),2)));

                sChargeTerminalValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                sChargeNameValue.setColspan(4);

                table.addCell(sChargeNameValue);
                //table.addCell(sChargeTerminalValue);
                //table.addCell(sChargeCounterValue);
                table.addCell(sChargeAmountValue);
                table.addCell(sChargeRateValue);
                table.addCell(sChargeTotalValue);
            }
            if(chargebackReversalVO.getAmount()>0.00)
            {
                Cell sChargebackReversedAmountCaptionCell = new Cell("Chargeback Reversed Amount");
                Cell sChargebackReversedAmountValueCell = new Cell(Functions.round(chargebackReversalVO.getAmount(), 2));
                sChargebackReversedAmountCaptionCell.setColspan(6);
                sChargebackReversedAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(sChargebackReversedAmountCaptionCell);
                table.addCell(sChargebackReversedAmountValueCell);
            }
            if(refundReverseAmount>0.00)
            {
                Cell sRefundReversedAmountCaptionCell = new Cell("Reversal Amount Adjustment");
                Cell sRefundReversedAmountValueCell = new Cell(Functions.round(refundReverseAmount, 2));
                sRefundReversedAmountCaptionCell.setColspan(6);
                sRefundReversedAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(sRefundReversedAmountCaptionCell);
                table.addCell(sRefundReversedAmountValueCell);
            }
            if(merchantRandomChargesVOs!=null && merchantRandomChargesVOs.size()>0){
                Set chargeset=merchantRandomChargesVOs.keySet();
                Iterator ChargeIterator=chargeset.iterator();
                while (ChargeIterator.hasNext()){
                    String RandomeChargeName=(String)ChargeIterator.next();
                    MerchantRandomChargesVO merchantRandomChargesVO=merchantRandomChargesVOs.get(RandomeChargeName);
                    sChargeNameValue = new Cell(merchantRandomChargesVO.getChargeName());
                    sChargeTerminalValue = new Cell(merchantRandomChargesVO.getChargeValueType());
                    if("Percentage".equals(merchantRandomChargesVO.getChargeValueType()))
                    {
                        sChargeRateValue = new Cell(merchantRandomChargesVO.getChargeValue()+"%");
                    }else{
                        sChargeRateValue = new Cell(String.valueOf(merchantRandomChargesVO.getChargeValue()));
                    }
                    sChargeCounterValue = new Cell("-");
                    double amount = grossAmount;
                    if( grossAmount < 0){
                        amount = -1*grossAmount;
                    }
                    sChargeAmountValue = new Cell(Functions.round(amount, 2));
                    if(merchantRandomChargesVO.getChargeType().equalsIgnoreCase("Deducted")) {
                        sChargeTotalValue = new Cell("-" +Functions.round(merchantRandomChargesVO.getChargeAmount(), 2));
                    }else{
                        sChargeTotalValue = new Cell(Functions.round(merchantRandomChargesVO.getChargeAmount(), 2));
                    }

                    sChargeTerminalValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                    sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    sChargeCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    sChargeAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    sChargeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                    sChargeNameValue.setColspan(4);

                    table.addCell(sChargeNameValue);
                    //table.addCell(sChargeTerminalValue);
                    //table.addCell(sChargeCounterValue);
                    table.addCell(sChargeAmountValue);
                    table.addCell(sChargeRateValue);
                    table.addCell(sChargeTotalValue);
                }
            }
            else
            {
                Cell sMiscellaneousChargesAmountCaptionCell = new Cell("Miscellaneous Adjustment");
                Cell sMiscellaneousChargesValueCell = new Cell(Functions.round(miscGross, 2));
                sMiscellaneousChargesAmountCaptionCell.setColspan(6);
                sMiscellaneousChargesValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(sMiscellaneousChargesAmountCaptionCell);
                table.addCell(sMiscellaneousChargesValueCell);
            }

            Cell sPreviousBalanceAmountCaptionCell=new Cell("Previous Balance Amount");
            Cell sPreviousBalanceAmountValueCell=new Cell(Functions.round(previousBalanceAmount,2));
            sPreviousBalanceAmountCaptionCell.setColspan(6);
            sPreviousBalanceAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sPreviousBalanceAmountCaptionCell);
            table.addCell(sPreviousBalanceAmountValueCell);

            Cell sRefundedRollingReserveCaptionCell=new Cell("Refunded Rolling Reserve");
            Cell sRefundedRollingReserveValueCell=new Cell(Functions.round(gossReserveRefunded,2));
            sRefundedRollingReserveCaptionCell.setColspan(6);
            sRefundedRollingReserveValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sRefundedRollingReserveCaptionCell);
            table.addCell(sRefundedRollingReserveValueCell);

            Cell sTotalAmountCaptionCell=new Cell(new Paragraph(20,"Total",f3));
            Cell sTotalAmountValueCell=new Cell(new Paragraph(20,Functions.round(totalFundedToBankBeforeSettlementExpenses,2),f3));
            sTotalAmountCaptionCell.setColspan(6);
            sTotalAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sTotalAmountCaptionCell);
            table.addCell(sTotalAmountValueCell);

            Cell sTotalAmountFundedCaptionCell=new Cell(new Paragraph(20,"Total Amount Funded["+currency+"]",f3));
            Cell sTotalAmountFundedValueCell=new Cell(new Paragraph(20,Functions.round(totalFundedToBankBeforeSettlementExpenses,2),f3));
            sTotalAmountFundedCaptionCell.setColspan(6);
            sTotalAmountFundedValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            sTotalAmountFundedCaptionCell.setBackgroundColor(Color.white.brighter());
            sTotalAmountFundedValueCell.setBackgroundColor(Color.white.brighter());
            table.addCell(sTotalAmountFundedCaptionCell);
            table.addCell(sTotalAmountFundedValueCell);

            if(settlementExpensesVOHashMap!=null && settlementExpensesVOHashMap.size()>0){
                Set settlementChargeSet=settlementExpensesVOHashMap.keySet();
                Iterator settlementExpenseChargeIteraor=settlementChargeSet.iterator();
                while (settlementExpenseChargeIteraor.hasNext()){
                    String settlementExpenseChargeName= (String) settlementExpenseChargeIteraor.next();
                    SettlementExpensesVO settlementExpensesVO= settlementExpensesVOHashMap.get(settlementExpenseChargeName);
                    Cell sSettlementExpensesCaptionCell=new Cell(new Paragraph(20, settlementExpensesVO.getChargeName(),f3));
                    Cell sSettlementExpensesAmountValueCell=new Cell(Functions.round(settlementExpensesVO.getAmount(),2));
                    Cell sSettlementExpensesRateValueCell=new Cell(Functions.round(Double.valueOf(settlementExpensesVO.getChargeValue()),2)+"%");
                    Cell sSettlementExpensesTotalValueCell;
                            if(totalFundedToBankBeforeSettlementExpenses<0)
                            {
                                 sSettlementExpensesTotalValueCell = new Cell(new Paragraph(20, Functions.round(settlementExpensesVO.getTotal(), 2), f3));
                            }else {
                                 sSettlementExpensesTotalValueCell = new Cell(new Paragraph(20, isValidAmount(Functions.round(settlementExpensesVO.getTotal(), 2)), f3));
                            }
                    sSettlementExpensesCaptionCell.setColspan(4);
                    sSettlementExpensesTotalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    sSettlementExpensesAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    sSettlementExpensesRateValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    sSettlementExpensesCaptionCell.setBackgroundColor(Color.white.brighter());
                    sSettlementExpensesTotalValueCell.setBackgroundColor(Color.white.brighter());

                    table.addCell(sSettlementExpensesCaptionCell);
                    table.addCell(sSettlementExpensesAmountValueCell);
                    table.addCell(sSettlementExpensesRateValueCell);
                    table.addCell(sSettlementExpensesTotalValueCell);
                }
            }

            /*if(settlementExpensesVO !=null){
                Cell sSettlementExpensesCaptionCell=new Cell(new Paragraph(20, settlementExpensesVO.getChargeName(),f3));
                Cell sSettlementExpensesAmountValueCell=new Cell(Functions.round(settlementExpensesVO.getAmount(),2));
                Cell sSettlementExpensesRateValueCell=new Cell(Functions.round(Double.valueOf(settlementExpensesVO.getChargeValue()),2)+"%");
                Cell sSettlementExpensesTotalValueCell=new Cell(new Paragraph(20,isValidAmount(Functions.round(settlementExpensesVO.getTotal(), 2)),f3));

                sSettlementExpensesCaptionCell.setColspan(4);
                sSettlementExpensesTotalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sSettlementExpensesAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sSettlementExpensesRateValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sSettlementExpensesCaptionCell.setBackgroundColor(Color.white.brighter());
                sSettlementExpensesTotalValueCell.setBackgroundColor(Color.white.brighter());

                table.addCell(sSettlementExpensesCaptionCell);
                table.addCell(sSettlementExpensesAmountValueCell);
                table.addCell(sSettlementExpensesRateValueCell);
                table.addCell(sSettlementExpensesTotalValueCell);
            }*/

            Cell sTotalAmountFundedSettlementCaptionCell=new Cell(new Paragraph(20,"Total Amount Funded["+settlementCurrency+"]",f3));
            Cell sTotalExchangeAmountValueCell=new Cell(Functions.round(totalFundedToBank,2));
            Cell sTotalExchangeRateValueCell=new Cell(Functions.round(exchangeRate,2));

            Cell sTotalAmountFundedSettlementValueCell=new Cell(new Paragraph(20,Functions.round(totalFundedToBank*exchangeRate,2),f3));

            sTotalAmountFundedSettlementCaptionCell.setColspan(4);

            sTotalAmountFundedSettlementValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            sTotalExchangeAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            sTotalExchangeRateValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

            sTotalAmountFundedSettlementCaptionCell.setBackgroundColor(Color.white.brighter());
            sTotalAmountFundedSettlementValueCell.setBackgroundColor(Color.white.brighter());

            table.addCell(sTotalAmountFundedSettlementCaptionCell);
            table.addCell(sTotalExchangeAmountValueCell);
            table.addCell(sTotalExchangeRateValueCell);
            table.addCell(sTotalAmountFundedSettlementValueCell);

           /* if("Y".equals(isPoweredBy))
            {
                Image poweredByLogo = Image.getInstance(PARTNER_LOGO_PATH+"poweredby_new_logo.png");
                Cell poweredByLogoCaptionCell1=new Cell("");
                Cell poweredByLogoValueCell1=new Cell(poweredByLogo);
                poweredByLogoCaptionCell1.setColspan(5);
                poweredByLogoValueCell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                poweredByLogoCaptionCell1.setBackgroundColor(Color.white.brighter());
                poweredByLogoValueCell1.setBackgroundColor(Color.white.brighter());
                table.addCell(poweredByLogoCaptionCell1);
                table.addCell(poweredByLogoValueCell1);
            }*/
            document.add(table);
            document.close();
        }
        catch (Exception ex){
            logger.error(ex);
        }
        return reportFileName;
    }

    public String createMerchantTransactionFile(SettlementDateVO settlementDateVO, String tableName, TerminalVO terminalVO)throws SystemError
    {
        String filename=null;
        String filePath=null;
        try
        {
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("Settled Report");
            HSSFCellStyle textBoldStyle = workbook.createCellStyle();
            HSSFFont font = workbook.createFont();
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            textBoldStyle.setFont(font);
            textBoldStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            textBoldStyle.setBorderLeft((short) 1);
            textBoldStyle.setBorderRight((short) 1);
            textBoldStyle.setBorderTop((short) 1);
            textBoldStyle.setBorderBottom((short) 1);
            //Excel sheet 1 for Settled Report
            settledReport(sheet, workbook, textBoldStyle, settlementDateVO, tableName, terminalVO);

            //Excel sheet 2 for Reverse data
            HSSFSheet sheet1 = workbook.createSheet("Reverse Report");
            refundReport(sheet1, workbook, textBoldStyle, settlementDateVO, tableName, terminalVO);

            //Excel sheet 2 for Chargeback data
            HSSFSheet sheet2 = workbook.createSheet("Chargeback Report");
            chargebackReport(sheet2, workbook, textBoldStyle, settlementDateVO, tableName, terminalVO);

            //Excel sheet 3 for Chargeback data
            HSSFSheet sheet3 = workbook.createSheet("Payout Report");
            payoutReport(sheet3, workbook, textBoldStyle, settlementDateVO, tableName, terminalVO);

            //Excel sheet 4 for Chargeback Reversed data
            HSSFSheet sheet4 = workbook.createSheet("Chargeback Reversed Report");
            chargebackReversedReport(sheet4, workbook, textBoldStyle, settlementDateVO, tableName, terminalVO);

            //Excel sheet 5 for Reversal Rollback data
            HSSFSheet sheet5 = workbook.createSheet("Reversal Rollback Report");
            RefundReversedReport(sheet5, workbook, textBoldStyle, settlementDateVO, tableName, terminalVO);

            //Excel sheet 5 for Case Filing data
            HSSFSheet sheet6 = workbook.createSheet("Case Filing Report");
            CaseFilingReport(sheet6, workbook, textBoldStyle, settlementDateVO, tableName, terminalVO);




            SimpleDateFormat dateFormater =new SimpleDateFormat("yyyyMMddHHmmss");

            String currentSystemDate=dateFormater.format(new Date());
            String[] tempStartDate=settlementDateVO.getSettlementStartDate().split(" ");
            String[] tempEndDate=settlementDateVO.getSettlementEndDate().split(" ");

            filename="Settlement_Merchant_"+terminalVO.getMemberId()+"_"+terminalVO.getAccountId()+"_"+terminalVO.getTerminalId()+"_"+tempStartDate[0].replace("-","")+"_"+tempEndDate[0].replace("-","")+"_"+currentSystemDate;
            filename=filename+".xls";
            File filepath=new File(SETTLEMENT_FILE_PATH+filename);
            filePath=filepath.getPath();
            FileOutputStream out =new FileOutputStream(filepath);
            workbook.write(out);
            out.close();
            logger.debug("Excel written successfully on drive");
        }
        catch (FileNotFoundException e)
        {
            logger.error("" + e);
        }
        catch (IOException e)
        {
            logger.error("" + e);

        }
        catch (Exception e)
        {
            logger.error("Exception---->", e);
        }
        return filename;
    }

    public String createRollingReserveTransactionFile(RollingReserveDateVO rollingReserveDateVO,TerminalVO terminalVO)throws SystemError
    {
        String filename=null;
        String filePath=null;
        List<TransactionVO> transactionVOs=null;
        PayoutDAO payoutDAO=new PayoutDAO();
        try
        {
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("Sample sheet");

            HSSFRow header=sheet.createRow(0);

            sheet.setColumnWidth((short)0,(short) 200000);
            sheet.setColumnWidth((short)1,(short) 200000);
            sheet.setColumnWidth((short)2,(short) 200000);
            sheet.setColumnWidth((short)3,(short) 200000);
            sheet.setColumnWidth((short)4,(short) 200000);
            sheet.setColumnWidth((short)5,(short) 200000);
            sheet.setColumnWidth((short)6,(short) 200000);
            sheet.setColumnWidth((short)7,(short) 200000);
            sheet.setColumnWidth((short)8,(short) 200000);

            HSSFCell cell0=header.createCell((short)0);
            HSSFCell cell1=header.createCell((short)1);
            HSSFCell cell2=header.createCell((short)2);
            HSSFCell cell3=header.createCell((short)3);
            HSSFCell cell4=header.createCell((short)4);
            HSSFCell cell5=header.createCell((short)5);
            HSSFCell cell6=header.createCell((short)6);
            HSSFCell cell7=header.createCell((short)7);
            HSSFCell cell8=header.createCell((short)8);

            // Start Style Added For Displaying Header Background As Gray And Strong
            HSSFCellStyle style = workbook.createCellStyle();
            style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
            style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            style.setBorderLeft((short)1);
            style.setBorderRight((short)1);
            style.setBorderTop((short)1);
            style.setBorderBottom((short)1);

            HSSFFont font = workbook.createFont();
            font.setBoldweight((short)2);
            style.setFont(font);

            // End Style Added For Displaying Header Background As Gray And Strong

            cell0.setCellStyle(style);
            cell1.setCellStyle(style);
            cell2.setCellStyle(style);
            cell3.setCellStyle(style);
            cell4.setCellStyle(style);
            cell5.setCellStyle(style);
            cell6.setCellStyle(style);
            cell7.setCellStyle(style);
            cell8.setCellStyle(style);

            cell0.setCellValue("TrackingID");
            cell1.setCellValue("Description");
            cell2.setCellValue("Trade date");
            cell3.setCellValue("Amount");
            cell4.setCellValue("Currency");
            cell5.setCellValue("Current Status");
            cell6.setCellValue("Billing firstname");
            cell7.setCellValue("Billing last name");
            cell8.setCellValue("Rolling Reserve");

            int index=1;

            //Get All Rolling Reservere transaction between given Rolling Reserve Start Date or End Date.
            transactionVOs=payoutDAO.getRollingReserveTransaction(rollingReserveDateVO.getRollingReserveStartDate(), rollingReserveDateVO.getRollingReserveEndDate(), terminalVO);
            for(TransactionVO detailsVO:transactionVOs)
            {
                HSSFRow row = sheet.createRow((short)index);
                row.createCell((short)0).setCellValue(detailsVO.getTrackingId());
                row.createCell((short)1).setCellValue(detailsVO.getOrderId());
                row.createCell((short)2).setCellValue(detailsVO.getTransactionDate());
                row.createCell((short)3).setCellValue(detailsVO.getCaptureAmount());
                row.createCell((short)4).setCellValue(detailsVO.getCurrency());
                row.createCell((short)5).setCellValue(detailsVO.getStatus()) ;
                row.createCell((short)6).setCellValue(detailsVO.getCustFirstName());
                row.createCell((short)7).setCellValue(detailsVO.getCustLastName());
                row.createCell((short)8).setCellValue(detailsVO.getIsRollingReserve());
                index=index+1;
            }

            SimpleDateFormat dateFormater =new SimpleDateFormat("yyyyMMddHHmmss");

            String currentSystemDate=dateFormater.format(new Date());
            String[] tempStartDate=rollingReserveDateVO.getRollingReserveStartDate().split(" ");
            String[] tempEndDate=rollingReserveDateVO.getRollingReserveEndDate().split(" ");

            filename="Rolling_Reserve_Transaction_Details_Of_"+terminalVO.getMemberId()+"_"+terminalVO.getAccountId()+"_"+tempStartDate[0].replace("-","")+"_"+tempEndDate[0].replace("-","")+"_"+currentSystemDate;
            filename=filename+".xls";
            File filepath=new File(SETTLEMENT_FILE_PATH+filename);
            filePath=filepath.getPath();
            FileOutputStream out =new FileOutputStream(filepath);
            workbook.write(out);
            out.close();
            logger.debug("Excel written successfully on drive");
        }
        catch (FileNotFoundException e)
        {
            logger.error("" + e);
        }
        catch (IOException e)
        {
            logger.error("" + e);

        }
        catch (Exception e)
        {
            logger.error("" + e);
        }
        return filename;
    }

    public ReserveRefundVO applyChargeOnReserveReleaseAmount(String chargeName, HashMap<String, CalculatedReserveRefundVO> calculatedReserveRefundVOHashMap, double gossReserveRefunded)
    {
        ChargeDetailsVO detailsVO=calculatedReserveRefundVOHashMap.get(chargeName);
        ReserveRefundVO refundVO=new ReserveRefundVO();
        refundVO.setCount(0);
        refundVO.setChargeValue(detailsVO.getChargeValue());
        refundVO.setChargeName(detailsVO.getChargeName());
        refundVO.setValueType(detailsVO.getValueType());
        refundVO.setAmount(gossReserveRefunded);

        double total=gossReserveRefunded*Double.valueOf(detailsVO.getChargeValue())/100;
        refundVO.setTotal(Functions.roundDBL(total,2));
        return refundVO;
    }

    public GrossChargeVO applyGrossCharge(String chargeName,HashMap<String,GrossChargeVO> grossChargeVOHashMap,double grossAmount)
    {
        ChargeDetailsVO detailsVO=grossChargeVOHashMap.get(chargeName);
        GrossChargeVO grossChargeVO=new GrossChargeVO();
        grossChargeVO.setCount(0);
        grossChargeVO.setChargeValue(detailsVO.getChargeValue());
        grossChargeVO.setChargeName(detailsVO.getChargeName());
        grossChargeVO.setValueType(detailsVO.getValueType());
        grossChargeVO.setAmount(grossAmount);
        if (grossAmount > 0)
        {
            if(detailsVO.getValueType().equalsIgnoreCase("Percentage"))
            {
                double total = grossAmount * Double.valueOf(detailsVO.getChargeValue()) / 100;
                grossChargeVO.setTotal(Functions.roundDBL(total, 2));
            }else{
                double total = Double.valueOf(detailsVO.getChargeValue());
                grossChargeVO.setTotal(Functions.roundDBL(total, 2));
            }
        }
        else
        {
            grossChargeVO.setTotal(0.0);
        }
        return grossChargeVO;
    }

    public OtherChargesVO CalculateOthercharges(String chargeName,HashMap<String,OtherChargesVO> otherChargesVOHashMap,double grossAmount)
    {
        ChargeDetailsVO detailsVO=otherChargesVOHashMap.get(chargeName);
        OtherChargesVO otherChargesVO=new OtherChargesVO();
        otherChargesVO.setCount(0);
        otherChargesVO.setChargeValue(detailsVO.getChargeValue());
        otherChargesVO.setChargeName(detailsVO.getChargeName());
        otherChargesVO.setValueType(detailsVO.getValueType());

       if(grossAmount < 0){
           grossAmount = -1*grossAmount;
       }
             otherChargesVO.setAmount(grossAmount);
            if(detailsVO.getValueType().equalsIgnoreCase("Percentage"))
            {
                double total = grossAmount * Double.valueOf(detailsVO.getChargeValue()) / 100;
                otherChargesVO.setTotal(Functions.roundDBL(total, 2));
            }else{
                double total = Double.valueOf(detailsVO.getChargeValue());
                otherChargesVO.setTotal(Functions.roundDBL(total, 2));
            }
        return otherChargesVO;
    }

    public ServiceTaxChargeVO calculateServiveTax(String chargeName,HashMap<String,ServiceTaxChargeVO> grossChargeVOHashMap,double serviceTaxAmount)
    {
        ChargeDetailsVO detailsVO=grossChargeVOHashMap.get(chargeName);
        ServiceTaxChargeVO serviceTaxChargeVO=new ServiceTaxChargeVO();
        serviceTaxChargeVO.setCount(0);
        serviceTaxChargeVO.setChargeValue(detailsVO.getChargeValue());
        serviceTaxChargeVO.setChargeName(detailsVO.getChargeName());
        serviceTaxChargeVO.setValueType(detailsVO.getValueType());
        serviceTaxChargeVO.setAmount(serviceTaxAmount);
        double total=serviceTaxAmount*Double.valueOf(detailsVO.getChargeValue())/100;
        serviceTaxChargeVO.setTotal(Functions.roundDBL(total,2));
        return serviceTaxChargeVO;
    }

    public TotalFeesChargesVO calculateTotalFeesCharge(String chargeName,HashMap<String,TotalFeesChargesVO> totalFeesChargesVOHashMap,double TotalFees)
    {
        ChargeDetailsVO detailsVO=totalFeesChargesVOHashMap.get(chargeName);
        TotalFeesChargesVO totalFeesChargesVO=new TotalFeesChargesVO();
        totalFeesChargesVO.setCount(0);
        totalFeesChargesVO.setChargeValue(detailsVO.getChargeValue());
        totalFeesChargesVO.setChargeName(detailsVO.getChargeName());
        totalFeesChargesVO.setValueType(detailsVO.getValueType());
        totalFeesChargesVO.setAmount(TotalFees);
        if (TotalFees > 0)
        {
            if(detailsVO.getValueType().equalsIgnoreCase("Percentage"))
            {
                double total=TotalFees * Double.valueOf(detailsVO.getChargeValue()) /100;
                totalFeesChargesVO.setTotal(Functions.roundDBL(total,2));
            }else{
                double total=Double.valueOf(detailsVO.getChargeValue());
                totalFeesChargesVO.setTotal(Functions.roundDBL(total,2));
            }
        }
        else
        {
            totalFeesChargesVO.setTotal(0.0);
        }

        return totalFeesChargesVO;
    }

    public Set<String> getMembersByAccountId(String accountId)
    {

        return payoutDAO.getActiveMembersOfAccount(accountId);

    }

    public TransactionSummaryVO getTotalSuccessCountAmountByTerminal(TerminalVO terminalVO,SettlementDateVO settlementDateVO) throws PZDBViolationException
    {
        long  authfailedCount=0;
        long   settledCount = 0;
        long   reversedCount = 0;
        long   chargebackCount = 0;
        long   chargebackReversalCount = 0;
        long   totalSuccessCount = 0;
        long   payoutCount=0;
        long refundReverseCount=0;

        double authfailedAmount=0.00;
        double settledAmount=0.0;
        double reversedAmount = 0.0;
        double chargebackAmount =0.0;
        double chargebackReversalAmount =0.0;
        double totalSuccessAmount =0.0;
        double payoutAmount=0.00;
        double refundReverseAmount=0.00;

        TransactionSummaryVO grossSummaryVO=new TransactionSummaryVO();
        TransactionSummaryVO summaryVO;

        //Code Changed on 8 july 2017
        //summaryVO=getSettledCountAmountByTerminal(memberId,accountId,terminalId,settlementDateVO);
        //settledCount=summaryVO.getCountOfSettled();
        //settledAmount=summaryVO.getSettledAmount();
        summaryVO=getProcessingCountAmountDetails(terminalVO,settlementDateVO);
        totalSuccessAmount=summaryVO.getTotalProcessingAmount();
        totalSuccessCount=summaryVO.getTotalProcessingCount();

//        authfailedCount=summaryVO.getCountOfAuthfailed();
//        authfailedAmount=summaryVO.getAuthfailedAmount();

        summaryVO=getAuthFailedCountAmountByTerminal(terminalVO,settlementDateVO);
        authfailedCount=summaryVO.getCountOfAuthfailed();
        authfailedAmount=summaryVO.getAuthfailedAmount();

        summaryVO=getChargebackCountAmountByTerminal(terminalVO,settlementDateVO);
        chargebackCount=summaryVO.getCountOfChargeback();
        chargebackAmount=summaryVO.getChargebackAmount();

        summaryVO=getChargebackReversedCountAmountByTerminal(terminalVO, settlementDateVO);
        chargebackReversalCount=summaryVO.getCountOfChargebackReversed();
        chargebackReversalAmount=summaryVO.getChargebackReversedAmount();

        summaryVO=getReversedCountAmountByTerminal(terminalVO,settlementDateVO);
        reversedCount=summaryVO.getCountOfReversed();
        reversedAmount=summaryVO.getReversedAmount();
//--------- payout dao
        summaryVO=getPayoutCountAmountByTerminal(terminalVO,settlementDateVO);
        payoutCount=summaryVO.getCountOfPayout();
        payoutAmount=summaryVO.getPayoutAmount();

        summaryVO=getRefundReverseCountAmountByTerminal(terminalVO, settlementDateVO);
        refundReverseCount=summaryVO.getRefundReverseCount();
        refundReverseAmount=summaryVO.getRefundReverseAmount();

        grossSummaryVO.setRefundReverseCount(refundReverseCount);
        grossSummaryVO.setRefundReverseAmount(refundReverseAmount);

        grossSummaryVO.setCountOfAuthfailed(authfailedCount);
        grossSummaryVO.setAuthfailedAmount(authfailedAmount);

        grossSummaryVO.setCountOfSettled(settledCount);
        grossSummaryVO.setSettledAmount(settledAmount);

        grossSummaryVO.setCountOfReversed(reversedCount);
        grossSummaryVO.setReversedAmount(reversedAmount);

        grossSummaryVO.setCountOfPayout(payoutCount);
        grossSummaryVO.setPayoutAmount(payoutAmount);

        grossSummaryVO.setCountOfChargeback(chargebackCount);
        grossSummaryVO.setChargebackAmount(chargebackAmount);

        grossSummaryVO.setCountOfChargebackReversed(chargebackReversalCount);
        grossSummaryVO.setChargebackReversedAmount(chargebackReversalAmount);

        grossSummaryVO.setTotalProcessingAmount(totalSuccessAmount);
        grossSummaryVO.setTotalProcessingCount(totalSuccessCount);

        return grossSummaryVO;
    }

    public TransactionSummaryVO getTotalSuccessCountAmountByTerminalNew(TerminalVO terminalVO,SettlementDateVO settlementDateVO,String tableName) throws PZDBViolationException
    {
        long  authfailedCount=0;
        long   settledCount = 0;
        long   reversedCount = 0;
        long   chargebackCount = 0;
        long   totalSuccessCount = 0;

        double authfailedAmount=0.00;
        double settledAmount=0.0;
        double reversedAmount = 0.0;
        double chargebackAmount =0.0;
        double totalSuccessAmount =0.0;

        TransactionSummaryVO grossSummaryVO=new TransactionSummaryVO();
        TransactionSummaryVO summaryVO;

        summaryVO=payoutDAO.getProcessingCountAmountDetails(settlementDateVO.getSettlementStartDate(),settlementDateVO.getSettlementEndDate(),terminalVO,tableName);

        totalSuccessAmount=summaryVO.getTotalProcessingAmount();
        totalSuccessCount=summaryVO.getTotalProcessingCount();

        /*System.out.println("totalSuccessAmount======="+totalSuccessAmount);
        System.out.println("totalSuccessCount======="+totalSuccessCount);

        System.out.println("settlement start date====="+settlementDateVO.getSettlementStartDate());
        System.out.println("settlement end date====="+settlementDateVO.getSettlementEndDate());*/

//        summaryVO=payoutDAO.getAuthFailCountAmountByDtstamp(settlementDateVO.getDeclinedStartDate(),settlementDateVO.getDeclinedEndDate(), terminalVO, tableName);
        authfailedCount=summaryVO.getCountOfAuthfailed();
        authfailedAmount=summaryVO.getAuthfailedAmount();

        /*System.out.println("authfailedCount======="+authfailedCount);
        System.out.println("authfailedAmount======="+authfailedAmount);

        System.out.println("decline start date====="+settlementDateVO.getDeclinedStartDate());
        System.out.println("decline end date====="+settlementDateVO.getDeclinedEndDate());*/

        summaryVO=payoutDAO.getChargebackCountAmountByTimestamp(settlementDateVO.getChargebackStartDate(),settlementDateVO.getChargebackEndDate(),terminalVO,tableName);
        chargebackCount=summaryVO.getCountOfChargeback();
        chargebackAmount=summaryVO.getChargebackAmount();

        /*System.out.println("chargebackCount======="+chargebackCount);
        System.out.println("chargebackAmount======="+chargebackAmount);

        System.out.println("chargeback start date====="+settlementDateVO.getChargebackStartDate());
        System.out.println("chargeback end date====="+settlementDateVO.getChargebackEndDate());*/

        summaryVO=payoutDAO.getReversalCountAmountByTimestamp(settlementDateVO.getReversedStartDate(),settlementDateVO.getReversedEndDate(), terminalVO, tableName);
        reversedCount=summaryVO.getCountOfReversed();
        reversedAmount=summaryVO.getReversedAmount();

        /*System.out.println("reversedCount======="+reversedCount);
        System.out.println("reversedAmount======="+reversedAmount);

        System.out.println("reversed start date====="+settlementDateVO.getReversedStartDate());
        System.out.println("reversed end date====="+settlementDateVO.getReversedEndDate());*/

        grossSummaryVO.setCountOfAuthfailed(authfailedCount);
        grossSummaryVO.setAuthfailedAmount(authfailedAmount);

        grossSummaryVO.setCountOfSettled(settledCount);
        grossSummaryVO.setSettledAmount(settledAmount);

        grossSummaryVO.setCountOfReversed(reversedCount);
        grossSummaryVO.setReversedAmount(reversedAmount);

        grossSummaryVO.setCountOfChargeback(chargebackCount);
        grossSummaryVO.setChargebackAmount(chargebackAmount);

        grossSummaryVO.setTotalProcessingAmount(totalSuccessAmount);
        grossSummaryVO.setTotalProcessingCount(totalSuccessCount);

        return grossSummaryVO;
    }

    public TransactionSummaryVO getAuthFailedCountAmountByTerminal(TerminalVO terminalVO,SettlementDateVO settlementDateVO) throws PZDBViolationException
    {
        TerminalManager terminalManager=new TerminalManager();
        //TerminalVO terminalVO=terminalManager.getTerminalByTerminalId(terminalId);
        String tableName=accountUtil.getTableNameSettlement(terminalVO.getAccountId());
        TransactionSummaryVO transactionSummary=payoutDAO.getAuthFailCountAmountByDtstamp(settlementDateVO.getDeclinedStartDate(),settlementDateVO.getDeclinedEndDate(), terminalVO, tableName);
        return transactionSummary;
    }
    public TransactionSummaryVO  getFraudDefenderCountAmountByTerminal(TerminalVO terminalVO,SettlementDateVO settlementDateVO) throws PZDBViolationException
    {
        TerminalManager terminalManager=new TerminalManager();
        //TerminalVO terminalVO=terminalManager.getTerminalByTerminalId(terminalId);
        TransactionSummaryVO transactionSummary=payoutDAO.getFraudDefenderCountAmountByDtstamp(settlementDateVO.getSettlementStartDate(), settlementDateVO.getSettlementEndDate(), terminalVO);
        return transactionSummary;
    }
    public TransactionSummaryVO  getDomesticTotalAmountByTerminalForEUCountry(TerminalVO terminalVO,SettlementDateVO settlementDateVO) throws PZDBViolationException
    {
        TransactionSummaryVO transactionSummary=payoutDAO.getDomesticTotalAmountByTerminalForEU(settlementDateVO.getSettlementStartDate(), settlementDateVO.getSettlementEndDate(), terminalVO);
        return transactionSummary;
    }
    public TransactionSummaryVO  getDomesticTotalAmountByTerminalForNonEU(TerminalVO terminalVO,SettlementDateVO settlementDateVO,String country) throws PZDBViolationException
    {
        TransactionSummaryVO transactionSummary=payoutDAO.getDomesticTotalAmountByTerminalForNonEU(settlementDateVO.getSettlementStartDate(), settlementDateVO.getSettlementEndDate(), terminalVO,country);
        return transactionSummary;
    }

    public TransactionSummaryVO  getInternationalTotalAmountByTerminalForEUCountry(TerminalVO terminalVO,SettlementDateVO settlementDateVO) throws PZDBViolationException
    {
        TransactionSummaryVO transactionSummary=payoutDAO.getInternationalTotalAmountByTerminalForEU(settlementDateVO.getSettlementStartDate(), settlementDateVO.getSettlementEndDate(), terminalVO);
        return transactionSummary;
    }
    public TransactionSummaryVO  getInternationalTotalAmountByTerminalForNonEU(TerminalVO terminalVO,SettlementDateVO settlementDateVO,String country) throws PZDBViolationException
    {
        TransactionSummaryVO transactionSummary=payoutDAO.getInternationalTotalAmountByTerminalForNonEU(settlementDateVO.getSettlementStartDate(), settlementDateVO.getSettlementEndDate(), terminalVO,country);
        return transactionSummary;
    }

    public TransactionSummaryVO getReversedCountAmountByTerminal(TerminalVO terminalVO,SettlementDateVO settlementDateVO) throws PZDBViolationException
    {
        TerminalManager terminalManager=new TerminalManager();
        //TerminalVO terminalVO=terminalManager.getTerminalByTerminalId(terminalId);
        String tableName=accountUtil.getTableNameSettlement(terminalVO.getAccountId());
        TransactionSummaryVO transactionSummary=payoutDAO.getReversalCountAmountByTimestamp(settlementDateVO.getReversedStartDate(),settlementDateVO.getReversedEndDate(), terminalVO, tableName);
        return transactionSummary;
    }
    public TransactionSummaryVO getChargebackReversedCountAmountByTerminal(TerminalVO terminalVO,SettlementDateVO settlementDateVO) throws PZDBViolationException
    {
        TerminalManager terminalManager=new TerminalManager();
        //TerminalVO terminalVO=terminalManager.getTerminalByTerminalId(terminalId);
        String tableName=accountUtil.getTableNameSettlement(terminalVO.getAccountId());
        TransactionSummaryVO transactionSummary=payoutDAO.getChargebackReversedCountAmountByTerminal(settlementDateVO.getChargebackStartDate(), settlementDateVO.getChargebackEndDate(), terminalVO, tableName);
        return transactionSummary;
    }
    public TransactionSummaryVO getPayoutCountAmountByTerminal(TerminalVO terminalVO,SettlementDateVO settlementDateVO) throws PZDBViolationException
    {
        TerminalManager terminalManager=new TerminalManager();
        //TerminalVO terminalVO=terminalManager.getTerminalByTerminalId(terminalId);
        String tableName=accountUtil.getTableNameSettlement(terminalVO.getAccountId());
        TransactionSummaryVO transactionSummary=payoutDAO.getPayoutCountAmountByTerminal(settlementDateVO.getReversedStartDate(), settlementDateVO.getReversedEndDate(), terminalVO, tableName);
        return transactionSummary;
    }
    public TransactionSummaryVO getRefundReverseCountAmountByTerminal(TerminalVO terminalVO,SettlementDateVO settlementDateVO) throws PZDBViolationException
    {
        TerminalManager terminalManager=new TerminalManager();
        //TerminalVO terminalVO=terminalManager.getTerminalByTerminalId(terminalId);
        String tableName=accountUtil.getTableNameSettlement(terminalVO.getAccountId());
        TransactionSummaryVO transactionSummary = payoutDAO.getRefundReverseCountAmountByTerminal(settlementDateVO.getReversedStartDate(), settlementDateVO.getReversedEndDate(), terminalVO, tableName);
        return transactionSummary;
    }

    public TransactionSummaryVO getChargebackCountAmountByTerminal(TerminalVO terminalVO,SettlementDateVO settlementDateVO) throws PZDBViolationException
    {
        TerminalManager terminalManager=new TerminalManager();
        //TerminalVO terminalVO=terminalManager.getTerminalByTerminalId(terminalId);
        String tableName=accountUtil.getTableNameSettlement(terminalVO.getAccountId());
        TransactionSummaryVO transactionSummary=payoutDAO.getChargebackCountAmountByTimestamp(settlementDateVO.getChargebackStartDate(),settlementDateVO.getChargebackEndDate(),terminalVO,tableName);
        return transactionSummary;
    }

    /*
       @programmer:sandip
        added new methods for getting total processing count and amount for certain time period
     */
    public TransactionSummaryVO getProcessingCountAmountDetails(TerminalVO terminalVO,SettlementDateVO settlementDateVO) throws PZDBViolationException
    {
        TerminalManager terminalManager=new TerminalManager();
        //TerminalVO terminalVO=terminalManager.getTerminalByTerminalId(terminalId);
        String tableName=accountUtil.getTableNameSettlement(terminalVO.getAccountId());
//        TransactionSummaryVO transactionSummary = payoutDAO.getProcessingCountAmountDetails(settlementDateVO.getSettlementStartDate(),settlementDateVO.getSettlementEndDate(),terminalVO,tableName);
        TransactionSummaryVO transactionSummary = payoutDAO.getProcessingAllCountAmountDetails(settlementDateVO.getReversedStartDate(),settlementDateVO.getReversedEndDate(),settlementDateVO.getSettlementStartDate(),settlementDateVO.getSettlementEndDate(),terminalVO,tableName);
        return transactionSummary;
    }

    //New Method Added During Final During Change wiremanager To merchant_wiremanager
    public void setRollingReserveStartDateToMerchantPayout(String memberId, String accountId, RollingReserveDateVO rollingReserveDateVO)
    {
        String reserveReleasedStartDate=payoutDAO.getRollingReserveStartDateMerchantPayout(memberId, accountId);
        if(reserveReleasedStartDate==null)
        {
            reserveReleasedStartDate=payoutDAO.getMemberFirstTransactionDateOnAccountId(memberId, accountId);
        }
        rollingReserveDateVO.setRollingReserveStartDate(reserveReleasedStartDate);
    }

    public void setRollingReserveStartDateToMerchantPayout(String memberId, String accountId, String terminalId,RollingReserveDateVO rollingReserveDateVO)
    {
        String reserveReleasedStartDate=payoutDAO.getRollingReserveStartDateMerchantPayout(memberId, accountId,terminalId);
        if(reserveReleasedStartDate==null)
        {
            reserveReleasedStartDate=payoutDAO.getMemberFirstTransactionDateOnAccountId(memberId, accountId,terminalId);
            logger.debug("reserveReleasedStartDate----"+reserveReleasedStartDate);
        }
        rollingReserveDateVO.setRollingReserveStartDate(reserveReleasedStartDate);
    }

    public boolean setSettledStartDateToMerchantPayout(TerminalVO terminalVO, SettlementDateVO settlementDateVO)
    {
        //Get the start date from wiremanager based on members previous wire on selected account.if wire not found in wiremanager then get first transaction date irrespective of status.
        boolean wireFound = false;
        String settleTransStartDate=payoutDAO.getSettledStartDateToMerchantPayout(terminalVO);
        if (functions.isValueNull(settleTransStartDate))
        {
            //settleTransStartDate=payoutDAO.getMemberFirstTransactionDateOnTerminal(terminalVO);
            settlementDateVO.setSettlementStartDate(settleTransStartDate);
            wireFound = true;
        }
        return wireFound;
    }

    public boolean setDeclinedStartDateToMerchantPayout(TerminalVO terminalVO, SettlementDateVO settlementDateVO)
    {
        //Get the start date from wiremanager based on members previous wire on selected account.if wire not found in wiremanager then get first transaction date irrespective of status.
        boolean wireFound = false;
        String declineTransStartDate=payoutDAO.getDeclineStartDateToMerchantPayout(terminalVO);
        if (functions.isValueNull(declineTransStartDate))
        {
            //declineTransStartDate=payoutDAO.getMemberFirstTransactionDateOnTerminal(terminalVO);
            settlementDateVO.setDeclinedStartDate(declineTransStartDate);
            wireFound = true;
        }
        return wireFound;
    }

    public boolean setReversedStartDateToMerchantPayout(TerminalVO terminalVO, SettlementDateVO settlementDateVO)
    {
        //Get the start date from wiremanager based on members previous wire on selected account.if wire not found in wiremanager then get first transaction date irrespective of status.
        boolean wireFound = false;
        String reverseTransStartDate=payoutDAO.getReverseStartDateToMerchantPayout(terminalVO);
        if (functions.isValueNull(reverseTransStartDate))
        {
            //reverseTransStartDate=payoutDAO.getMemberFirstTransactionDateOnTerminal(terminalVO);
            settlementDateVO.setReversedStartDate(reverseTransStartDate);
            wireFound = true;
        }
        return wireFound;
    }

    public boolean setChargebackStartDateToMerchantPayout(TerminalVO terminalVO, SettlementDateVO settlementDateVO)
    {
        //Get the start date from wiremanager based on members previous wire on selected account.if wire not found in wiremanager then get first transaction date irrespective of status.
        boolean wireFound = false;
        String chargebackTransStartDate=payoutDAO.getChargebackStartDateToMerchant(terminalVO);
        if (functions.isValueNull(chargebackTransStartDate))
        {
            //chargebackTransStartDate=payoutDAO.getMemberFirstTransactionDateOnTerminal(terminalVO);
            settlementDateVO.setChargebackStartDate(chargebackTransStartDate);
            wireFound = true;
        }
        return wireFound;
    }

    public String createAgentReportFile(AgentPayoutReportVO agentPayoutReportVO)
    {
        AgentDetailsVO agentDetailsVO=agentPayoutReportVO.getAgentDetailsVO();
        TerminalVO terminalVO=agentPayoutReportVO.getTerminalVO();
        SettlementDateVO settlementDateVO=agentPayoutReportVO.getSettlementDateVO();
        List<ChargeDetailsVO> chargesDetailsVOs=agentPayoutReportVO.getChargesDetailsVOs();
        WireChargeVO wireChargeVO=agentPayoutReportVO.getWireChargeVO();
        List<GrossChargeVO> grossTypeChargeVOList=agentPayoutReportVO.getGrossTypeChargeVOList();
        PayoutReportUtils payoutReportUtils=new PayoutReportUtils();

        Document document = new Document(PageSize.A3, 50, 50, 50, 50);
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );

        String reportFileName=payoutReportUtils.getAgentReportFileName(agentDetailsVO.getAgentId(), terminalVO.getMemberId(),terminalVO.getTerminalId(), settlementDateVO.getSettlementEndDate(), settlementDateVO.getSettlementEndDate());
        try
        {
            String sMemberId=terminalVO.getMemberId();

            HashMap partnerDetails=payoutDAO.getPartnerDetails(sMemberId);
            reportFileName=reportFileName+".pdf";

            String partnerName=(String)partnerDetails.get("partnername");
            String partnerLogoName=(String)partnerDetails.get("logoName");
            if(!functions.isValueNull(partnerLogoName))
            {
                partnerLogoName="logo.jpg";
            }

            File filepath=new File(AGENT_PAYOUT_REPORT_FILE_PATH+reportFileName);


            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filepath));
            document.open();

            // demonstrate some table features
            Table table = new Table(6);
            table.setWidth(100);

            /*table.setBorderWidth(1);*/
            table.setBorderColor(new Color(0, 0,0));
            table.setPadding(1);
            /*table.setSpacing(1);*/

            String reportingDate=targetFormat.format(new Date());

            Image partnerImageInstance = Image.getInstance(PARTNER_LOGO_PATH+partnerLogoName);
            partnerImageInstance.scaleAbsolute(150f, 150f);
            partnerImageInstance.scaleAbsoluteHeight(40f);

            Cell partnerNameCaptionCell=new Cell("Processor:"+partnerName);
            Cell reportingDateCaptionCe11=new Cell("Reporting Date:"+reportingDate);
            Cell partnerLogoCell=new Cell(partnerImageInstance);

            partnerNameCaptionCell.setColspan(2);
            reportingDateCaptionCe11.setColspan(2);
            partnerLogoCell.setColspan(2);

            partnerLogoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            partnerNameCaptionCell.setBackgroundColor(Color.white.brighter());
            reportingDateCaptionCe11.setBackgroundColor(Color.white.brighter());

            table.addCell(partnerNameCaptionCell);
            table.addCell(reportingDateCaptionCe11);
            table.addCell(partnerLogoCell);

            Cell sMemberDetailsHeaderCell=new Cell("Agent Details");
            sMemberDetailsHeaderCell.setColspan(6);
            sMemberDetailsHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            sMemberDetailsHeaderCell.setVerticalAlignment(Element.ALIGN_CENTER);

            sMemberDetailsHeaderCell.setBackgroundColor(Color.gray.brighter());
            table.addCell(sMemberDetailsHeaderCell);

            Cell sAgentCaptionCell=new Cell("Agent ID:");
            Cell sAgentCaptionValueCell=new Cell(agentDetailsVO.getAgentId());
            sAgentCaptionCell.setColspan(4);
            sAgentCaptionValueCell.setColspan(2);
            table.addCell(sAgentCaptionCell);
            table.addCell(sAgentCaptionValueCell);

            Cell sMemberIdCaptionCell=new Cell("Member ID:");
            Cell sMemberIdValueCell=new Cell(terminalVO.getMemberId());
            sMemberIdCaptionCell.setColspan(4);
            sMemberIdValueCell.setColspan(2);
            table.addCell(sMemberIdCaptionCell);
            table.addCell(sMemberIdValueCell);

            Cell sTerminalCaptionCell=new Cell("Termial Id:");
            Cell sTerminalValueCell=new Cell(terminalVO.getTerminalId());
            sTerminalCaptionCell.setColspan(4);
            sTerminalValueCell.setColspan(2);
            table.addCell(sTerminalCaptionCell);
            table.addCell(sTerminalValueCell);

            Cell sCompanyNameCaptionCell=new Cell("Company Name:");
            Cell sCompanyNameValueCell=new Cell(agentDetailsVO.getAgentName());
            sCompanyNameCaptionCell.setColspan(4);
            sCompanyNameValueCell.setColspan(2);
            table.addCell(sCompanyNameCaptionCell);
            table.addCell(sCompanyNameValueCell);

            Cell sContactPersonCaptionCell=new Cell("Contact Person:");
            Cell sContactPersonValueCell=new Cell(agentDetailsVO.getContactPerson());
            sContactPersonCaptionCell.setColspan(4);
            sContactPersonValueCell.setColspan(2);
            table.addCell(sContactPersonCaptionCell);
            table.addCell(sContactPersonValueCell);

            Cell sPayModeCaptionCell=new Cell("Payment Mode:");
            Cell sPayModeValueCell=new Cell(terminalVO.getPaymentTypeName());
            sPayModeCaptionCell.setColspan(4);
            sPayModeValueCell.setColspan(2);
            table.addCell(sPayModeCaptionCell);
            table.addCell(sPayModeValueCell);

            Cell sCardTypeCaptionCell=new Cell("Card Type:");
            Cell sCardTypeValueCell=new Cell(terminalVO.getCardType());
            sCardTypeCaptionCell.setColspan(4);
            sCardTypeValueCell.setColspan(2);
            table.addCell(sCardTypeCaptionCell);
            table.addCell(sCardTypeValueCell);

            Cell sCurrencyCaptionCell=new Cell("Currency:");
            Cell sCurrencyValueCell=new Cell(agentPayoutReportVO.getCurrency());
            sCurrencyCaptionCell.setColspan(4);
            sCurrencyValueCell.setColspan(2);
            table.addCell(sCurrencyCaptionCell);
            table.addCell(sCurrencyValueCell);

            Cell sSettlePeriodCaptionCell=new Cell("Settle Covered Upto:");
            Cell sSettleEndValueCell=new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getSettlementEndDate())));

            sSettlePeriodCaptionCell.setColspan(4);
            sSettleEndValueCell.setColspan(2);

            table.addCell(sSettlePeriodCaptionCell);
            table.addCell(sSettleEndValueCell);

            Cell sDeclinedPeriodCaptionCell=new Cell("Decline Covered Upto:");
            Cell sDeclinedEndValueCell=new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getDeclinedEndDate())));

            sDeclinedPeriodCaptionCell.setColspan(4);
            sDeclinedEndValueCell.setColspan(2);

            table.addCell(sDeclinedPeriodCaptionCell);
            table.addCell(sDeclinedEndValueCell);

            Cell sReversedPeriodCaptionCell=new Cell("Reversal Covered Upto:");
            Cell sReversedEndValueCell=new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getReversedEndDate())));

            sReversedPeriodCaptionCell.setColspan(4);
            sReversedEndValueCell.setColspan(2);

            table.addCell(sReversedPeriodCaptionCell);
            table.addCell(sReversedEndValueCell);

            Cell sChargebackPeriodCaptionCell=new Cell("Chargeback Covered Upto:");
            Cell sChargebackEndValueCell=new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getChargebackEndDate())));

            sChargebackPeriodCaptionCell.setColspan(4);
            sChargebackEndValueCell.setColspan(2);

            table.addCell(sChargebackPeriodCaptionCell);
            table.addCell(sChargebackEndValueCell);

            Cell sChargesDetailsHeaderCell=new Cell("Agent Charges Details");
            sChargesDetailsHeaderCell.setColspan(6);
            sChargesDetailsHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            sChargesDetailsHeaderCell.setBackgroundColor(Color.gray.brighter());
            table.addCell(sChargesDetailsHeaderCell);

            Cell sChargeNameHeader = new Cell("Charge Name");
            Cell sChargeRateHeader = new Cell("Rate/Fee");
            Cell sChargeCounterHeader = new Cell("Counter");
            Cell sChargeAmountHeader = new Cell("Amount");
            Cell sChargeTotalHeader = new Cell("Total");

            sChargeNameHeader.setColspan(2);

            sChargeNameHeader.setBackgroundColor(Color.gray.brighter());
            sChargeRateHeader.setBackgroundColor(Color.gray.brighter());

            sChargeCounterHeader.setBackgroundColor(Color.gray.brighter());
            sChargeAmountHeader.setBackgroundColor(Color.gray.brighter());
            sChargeTotalHeader.setBackgroundColor(Color.gray.brighter());

            sChargeNameHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            sChargeRateHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            sChargeCounterHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            sChargeAmountHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            sChargeTotalHeader.setHorizontalAlignment(Element.ALIGN_CENTER);

            table.addCell(sChargeNameHeader);
            table.addCell(sChargeRateHeader);
            table.addCell(sChargeCounterHeader);
            table.addCell(sChargeAmountHeader);
            table.addCell(sChargeTotalHeader);

            Cell sChargeNameValue,sChargeRateValue,sChargeCounterValue,sChargeAmountValue,sChargeTotalValue;

            for(ChargeDetailsVO chargeDetailsVO:chargesDetailsVOs)
            {

                String vCntCounter=(new Long(chargeDetailsVO.getCount())).toString();
                sChargeNameValue = new Cell(chargeDetailsVO.getChargeName());
                sChargeTotalValue = new Cell(Functions.round(chargeDetailsVO.getTotal(),2));
                sChargeRateValue = new Cell(chargeDetailsVO.getChargeValue());
                sChargeCounterValue = new Cell(vCntCounter);
                sChargeAmountValue = new Cell(Functions.round(chargeDetailsVO.getAmount(),2));

                sChargeNameValue.setColspan(2);

                sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                sChargeCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                table.addCell(sChargeNameValue);
                table.addCell(sChargeRateValue);
                table.addCell(sChargeCounterValue);
                table.addCell(sChargeAmountValue);
                table.addCell(sChargeTotalValue);

            }

            Cell sChargeFeeTotalCaption=new Cell("Total");
            Cell sChargeFeeTotalValue=new Cell(Functions.round(agentPayoutReportVO.getAgentTotalChargesAmount(),2));
            sChargeFeeTotalCaption.setColspan(5);
            sChargeFeeTotalCaption.setHeader(true);

            sChargeFeeTotalCaption.setBackgroundColor(Color.gray.brighter());
            sChargeFeeTotalValue.setBackgroundColor(Color.gray.brighter());

            sChargeFeeTotalCaption.setHorizontalAlignment(Element.ALIGN_RIGHT);
            sChargeFeeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);


            table.addCell(sChargeFeeTotalCaption);
            table.addCell(sChargeFeeTotalValue);

            Cell sAgentChangesAmountCaptionCell=new Cell("Agent Charges Total Amount");
            Cell sAgentChangesAmountValueCell=new Cell(Functions.round(agentPayoutReportVO.getAgentTotalChargesAmount(),2));

            sAgentChangesAmountCaptionCell.setColspan(5);

            sAgentChangesAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(sAgentChangesAmountCaptionCell);
            table.addCell(sAgentChangesAmountValueCell);

            if(wireChargeVO!=null)
            {
                sChargeNameValue = new Cell(wireChargeVO.getChargeName());
                sChargeRateValue = new Cell(wireChargeVO.getChargeValue());

                sChargeCounterValue = new Cell(new Long(wireChargeVO.getCount()).toString());
                sChargeAmountValue = new Cell("-");
                sChargeTotalValue = new Cell("-"+Functions.round(wireChargeVO.getTotal(),2));

                sChargeNameValue.setColspan(2);

                sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                sChargeCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                table.addCell(sChargeNameValue);
                table.addCell(sChargeRateValue);
                table.addCell(sChargeCounterValue);
                table.addCell(sChargeAmountValue);
                table.addCell(sChargeTotalValue);
            }

            for(GrossChargeVO grossChargeVO:grossTypeChargeVOList)
            {
                sChargeNameValue = new Cell(grossChargeVO.getChargeName());
                sChargeRateValue = new Cell(grossChargeVO.getChargeValue());
                sChargeCounterValue = new Cell("-");
                sChargeAmountValue = new Cell(Functions.round(agentPayoutReportVO.getAgentTotalChargesAmount(),2));
                sChargeTotalValue = new Cell(isValidAmount(Functions.round(grossChargeVO.getTotal(),2)));

                sChargeNameValue.setColspan(2);

                sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                table.addCell(sChargeNameValue);
                table.addCell(sChargeRateValue);
                table.addCell(sChargeCounterValue);
                table.addCell(sChargeAmountValue);
                table.addCell(sChargeTotalValue);

            }
            Cell sUnpaidAmountCaptionCell=new Cell("Paid Amount");
            Cell sUnpaidAmountValueCell=new Cell(isValidAmount(Functions.round(agentPayoutReportVO.getAgentWirePaidAmount(),2)));
            sUnpaidAmountCaptionCell.setColspan(5);
            sUnpaidAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sUnpaidAmountCaptionCell);
            table.addCell(sUnpaidAmountValueCell);


            Cell sPreviousBalanceAmountCaptionCell=new Cell("Previous Balance Amount");
            Cell sPreviousBalanceAmountValueCell=new Cell(Functions.round(agentPayoutReportVO.getAgentTotalBalanceAmount(),2));
            sPreviousBalanceAmountCaptionCell.setColspan(5);
            sPreviousBalanceAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sPreviousBalanceAmountCaptionCell);
            table.addCell(sPreviousBalanceAmountValueCell);

            Cell sTotalAmountFundedCaptionCell=new Cell("Total Amount Funded");
            Cell sTotalAmountFundedValueCell=new Cell(Functions.round(agentPayoutReportVO.getAgentTotalFundedAmount(),2));
            sTotalAmountFundedCaptionCell.setColspan(5);
            sTotalAmountFundedValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            sTotalAmountFundedCaptionCell.setBackgroundColor(Color.gray.brighter());
            sTotalAmountFundedValueCell.setBackgroundColor(Color.gray.brighter());
            table.addCell(sTotalAmountFundedCaptionCell);
            table.addCell(sTotalAmountFundedValueCell);


            Image poweredByLogo = Image.getInstance(PARTNER_LOGO_PATH+"logo2.jpg");
            Cell poweredByLogoCaptionCell1=new Cell("");
            Cell poweredByLogoValueCell1=new Cell(poweredByLogo);
            poweredByLogoCaptionCell1.setColspan(5);
            poweredByLogoValueCell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
            poweredByLogoCaptionCell1.setBackgroundColor(Color.white.brighter());
            poweredByLogoValueCell1.setBackgroundColor(Color.white.brighter());
            table.addCell(poweredByLogoCaptionCell1);
            table.addCell(poweredByLogoValueCell1);


            document.add(table);
            document.close();

        }
        catch (Exception ex)
        {
            logger.error(ex);
        }

        return reportFileName;
    }

    public String createBankAgentReportFile(BankAgentPayoutReportVO bankAgentPayoutReportVO)
    {
        AgentDetailsVO agentDetailsVO=bankAgentPayoutReportVO.getAgentDetailsVO();
        PartnerDetailsVO partnerDetailsVO=bankAgentPayoutReportVO.getPartnerDetailsVO();
        GatewayAccount gatewayAccount=bankAgentPayoutReportVO.getGatewayAccount();
        SettlementDateVO settlementDateVO=bankAgentPayoutReportVO.getSettlementDateVO();
        List<ChargeDetailsVO> chargesDetailsVOs=bankAgentPayoutReportVO.getChargesDetailsVOs();
        WireChargeVO wireChargeVO=bankAgentPayoutReportVO.getWireChargeVO();
        List<GrossChargeVO> grossTypeChargeVOList=bankAgentPayoutReportVO.getGrossTypeChargeVOList();
        PayoutReportUtils payoutReportUtils=new PayoutReportUtils();


        Document document = new Document(PageSize.A3, 50, 50, 50, 50);
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );

        String reportFileName=payoutReportUtils.getBankAgentReportFileName(agentDetailsVO.getAgentId(), gatewayAccount.getMerchantId(),settlementDateVO.getSettlementEndDate(),settlementDateVO.getSettlementEndDate());
        try
        {
            String sMId=gatewayAccount.getMerchantId();


            reportFileName=reportFileName+".pdf";

            String partnerName=partnerDetailsVO.getCompanyName();
            String partnerLogoName=partnerDetailsVO.getLogoName();
            if(!functions.isValueNull(partnerLogoName))
            {
                partnerLogoName="logo.jpg";
            }

            File filepath=new File(AGENT_PAYOUT_REPORT_FILE_PATH+reportFileName);


            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filepath));
            document.open();

            // demonstrate some table features
            Table table = new Table(6);
            table.setWidth(100);

            /*table.setBorderWidth(1);*/
            table.setBorderColor(new Color(0, 0,0));
            table.setPadding(1);
            /*table.setSpacing(1);*/

            String reportingDate=targetFormat.format(new Date());

            Image partnerImageInstance = Image.getInstance(PARTNER_LOGO_PATH+partnerLogoName);
            partnerImageInstance.scaleAbsolute(150f, 150f);
            partnerImageInstance.scaleAbsoluteHeight(40f);

            Cell partnerNameCaptionCell=new Cell("Processor:"+partnerName);
            Cell reportingDateCaptionCe11=new Cell("Reporting Date:"+reportingDate);
            Cell partnerLogoCell=new Cell(partnerImageInstance);

            partnerNameCaptionCell.setColspan(2);
            reportingDateCaptionCe11.setColspan(2);
            partnerLogoCell.setColspan(2);

            partnerLogoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            partnerNameCaptionCell.setBackgroundColor(Color.white.brighter());
            reportingDateCaptionCe11.setBackgroundColor(Color.white.brighter());

            table.addCell(partnerNameCaptionCell);
            table.addCell(reportingDateCaptionCe11);
            table.addCell(partnerLogoCell);

            Cell sMemberDetailsHeaderCell=new Cell("Agent Details");
            sMemberDetailsHeaderCell.setColspan(6);
            sMemberDetailsHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            sMemberDetailsHeaderCell.setVerticalAlignment(Element.ALIGN_CENTER);

            sMemberDetailsHeaderCell.setBackgroundColor(Color.gray.brighter());
            table.addCell(sMemberDetailsHeaderCell);

            Cell sAgentCaptionCell=new Cell("Agent ID:");
            Cell sAgentCaptionValueCell=new Cell(agentDetailsVO.getAgentId());
            sAgentCaptionCell.setColspan(4);
            sAgentCaptionValueCell.setColspan(2);
            table.addCell(sAgentCaptionCell);
            table.addCell(sAgentCaptionValueCell);

            Cell sCompanyNameCaptionCell=new Cell("Company Name:");
            Cell sCompanyNameValueCell=new Cell(agentDetailsVO.getAgentName());
            sCompanyNameCaptionCell.setColspan(4);
            sCompanyNameValueCell.setColspan(2);
            table.addCell(sCompanyNameCaptionCell);
            table.addCell(sCompanyNameValueCell);

            Cell sContactPersonCaptionCell=new Cell("Contact Person:");
            Cell sContactPersonValueCell=new Cell(agentDetailsVO.getContactPerson());
            sContactPersonCaptionCell.setColspan(4);
            sContactPersonValueCell.setColspan(2);
            table.addCell(sContactPersonCaptionCell);
            table.addCell(sContactPersonValueCell);

            Cell sMemberIdCaptionCell=new Cell("MID:");
            Cell sMemberIdValueCell=new Cell(gatewayAccount.getMerchantId());
            sMemberIdCaptionCell.setColspan(4);
            sMemberIdValueCell.setColspan(2);
            table.addCell(sMemberIdCaptionCell);
            table.addCell(sMemberIdValueCell);

            Cell sCurrencyCaptionCell=new Cell("Currency:");
            Cell sCurrencyValueCell=new Cell(gatewayAccount.getCurrency());
            sCurrencyCaptionCell.setColspan(4);
            sCurrencyValueCell.setColspan(2);
            table.addCell(sCurrencyCaptionCell);
            table.addCell(sCurrencyValueCell);

            Cell sSettlePeriodCaptionCell=new Cell("Settle Covered Upto:");
            Cell sSettleEndValueCell=new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getSettlementEndDate())));

            sSettlePeriodCaptionCell.setColspan(4);
            sSettleEndValueCell.setColspan(2);

            table.addCell(sSettlePeriodCaptionCell);
            table.addCell(sSettleEndValueCell);

            Cell sDeclinedPeriodCaptionCell=new Cell("Decline Covered Upto:");
            Cell sDeclinedEndValueCell=new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getDeclinedEndDate())));

            sDeclinedPeriodCaptionCell.setColspan(4);
            sDeclinedEndValueCell.setColspan(2);

            table.addCell(sDeclinedPeriodCaptionCell);
            table.addCell(sDeclinedEndValueCell);

            Cell sReversedPeriodCaptionCell=new Cell("Reversal Covered Upto:");
            Cell sReversedEndValueCell=new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getReversedEndDate())));

            sReversedPeriodCaptionCell.setColspan(4);
            sReversedEndValueCell.setColspan(2);

            table.addCell(sReversedPeriodCaptionCell);
            table.addCell(sReversedEndValueCell);

            Cell sChargebackPeriodCaptionCell=new Cell("Chargeback Covered Upto:");
            Cell sChargebackEndValueCell=new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getChargebackEndDate())));

            sChargebackPeriodCaptionCell.setColspan(4);
            sChargebackEndValueCell.setColspan(2);

            table.addCell(sChargebackPeriodCaptionCell);
            table.addCell(sChargebackEndValueCell);

            Cell sChargesDetailsHeaderCell=new Cell("Agent Charges Details");
            sChargesDetailsHeaderCell.setColspan(6);
            sChargesDetailsHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            sChargesDetailsHeaderCell.setBackgroundColor(Color.gray.brighter());
            table.addCell(sChargesDetailsHeaderCell);

            Cell sChargeNameHeader = new Cell("Charge Name");
            Cell sChargeRateHeader = new Cell("Rate/Fee");
            Cell sChargeCounterHeader = new Cell("Counter");
            Cell sChargeAmountHeader = new Cell("Amount");
            Cell sChargeTotalHeader = new Cell("Total");

            sChargeNameHeader.setColspan(2);

            sChargeNameHeader.setBackgroundColor(Color.gray.brighter());
            sChargeRateHeader.setBackgroundColor(Color.gray.brighter());

            sChargeCounterHeader.setBackgroundColor(Color.gray.brighter());
            sChargeAmountHeader.setBackgroundColor(Color.gray.brighter());
            sChargeTotalHeader.setBackgroundColor(Color.gray.brighter());

            sChargeNameHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            sChargeRateHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            sChargeCounterHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            sChargeAmountHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            sChargeTotalHeader.setHorizontalAlignment(Element.ALIGN_CENTER);

            table.addCell(sChargeNameHeader);
            table.addCell(sChargeRateHeader);
            table.addCell(sChargeCounterHeader);
            table.addCell(sChargeAmountHeader);
            table.addCell(sChargeTotalHeader);

            Cell sChargeNameValue,sChargeRateValue,sChargeCounterValue,sChargeAmountValue,sChargeTotalValue;

            for(ChargeDetailsVO chargeDetailsVO:chargesDetailsVOs)
            {

                String vCntCounter=(new Long(chargeDetailsVO.getCount())).toString();
                sChargeNameValue = new Cell(chargeDetailsVO.getChargeName());
                sChargeTotalValue = new Cell(Functions.round(chargeDetailsVO.getTotal(),2));
                sChargeRateValue = new Cell(chargeDetailsVO.getChargeValue());
                sChargeCounterValue = new Cell(vCntCounter);
                sChargeAmountValue = new Cell(Functions.round(chargeDetailsVO.getAmount(),2));

                sChargeNameValue.setColspan(2);

                sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                sChargeCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                table.addCell(sChargeNameValue);
                table.addCell(sChargeRateValue);
                table.addCell(sChargeCounterValue);
                table.addCell(sChargeAmountValue);
                table.addCell(sChargeTotalValue);

            }

            Cell sChargeFeeTotalCaption=new Cell("Total");
            Cell sChargeFeeTotalValue=new Cell(Functions.round(bankAgentPayoutReportVO.getAgentTotalChargesAmount(),2));
            sChargeFeeTotalCaption.setColspan(5);
            sChargeFeeTotalCaption.setHeader(true);

            sChargeFeeTotalCaption.setBackgroundColor(Color.gray.brighter());
            sChargeFeeTotalValue.setBackgroundColor(Color.gray.brighter());

            sChargeFeeTotalCaption.setHorizontalAlignment(Element.ALIGN_RIGHT);
            sChargeFeeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);


            table.addCell(sChargeFeeTotalCaption);
            table.addCell(sChargeFeeTotalValue);

            Cell sAgentChangesAmountCaptionCell=new Cell("Agent Charges Total Amount");
            Cell sAgentChangesAmountValueCell=new Cell(Functions.round(bankAgentPayoutReportVO.getAgentTotalChargesAmount(),2));

            sAgentChangesAmountCaptionCell.setColspan(5);

            sAgentChangesAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(sAgentChangesAmountCaptionCell);
            table.addCell(sAgentChangesAmountValueCell);

            if(wireChargeVO!=null)
            {
                sChargeNameValue = new Cell(wireChargeVO.getChargeName());
                sChargeRateValue = new Cell(wireChargeVO.getChargeValue());

                sChargeCounterValue = new Cell(new Long(wireChargeVO.getCount()).toString());
                sChargeAmountValue = new Cell("-");
                sChargeTotalValue = new Cell(isValidAmount(Functions.round(wireChargeVO.getTotal(),2)));

                sChargeNameValue.setColspan(2);

                sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                sChargeCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                table.addCell(sChargeNameValue);
                table.addCell(sChargeRateValue);
                table.addCell(sChargeCounterValue);
                table.addCell(sChargeAmountValue);
                table.addCell(sChargeTotalValue);
            }

            for(GrossChargeVO grossChargeVO:grossTypeChargeVOList)
            {
                sChargeNameValue = new Cell(grossChargeVO.getChargeName());
                sChargeRateValue = new Cell(grossChargeVO.getChargeValue());
                sChargeCounterValue = new Cell("-");
                sChargeAmountValue = new Cell(Functions.round(bankAgentPayoutReportVO.getAgentTotalChargesAmount(),2));
                sChargeTotalValue = new Cell(isValidAmount(Functions.round(grossChargeVO.getTotal(),2)));

                sChargeNameValue.setColspan(2);

                sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                table.addCell(sChargeNameValue);
                table.addCell(sChargeRateValue);
                table.addCell(sChargeCounterValue);
                table.addCell(sChargeAmountValue);
                table.addCell(sChargeTotalValue);

            }
            Cell sUnpaidAmountCaptionCell=new Cell("Paid Amount");
            Cell sUnpaidAmountValueCell=new Cell(isValidAmount(Functions.round(bankAgentPayoutReportVO.getAgentWirePaidAmount(),2)));
            sUnpaidAmountCaptionCell.setColspan(5);
            sUnpaidAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sUnpaidAmountCaptionCell);
            table.addCell(sUnpaidAmountValueCell);


            Cell sPreviousBalanceAmountCaptionCell=new Cell("Previous Balance Amount");
            Cell sPreviousBalanceAmountValueCell=new Cell(Functions.round(bankAgentPayoutReportVO.getAgentTotalBalanceAmount(),2));
            sPreviousBalanceAmountCaptionCell.setColspan(5);
            sPreviousBalanceAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sPreviousBalanceAmountCaptionCell);
            table.addCell(sPreviousBalanceAmountValueCell);

            Cell sTotalAmountFundedCaptionCell=new Cell("Total Amount Funded");
            Cell sTotalAmountFundedValueCell=new Cell(Functions.round(bankAgentPayoutReportVO.getAgentTotalFundedAmount(),2));
            sTotalAmountFundedCaptionCell.setColspan(5);
            sTotalAmountFundedValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            sTotalAmountFundedCaptionCell.setBackgroundColor(Color.gray.brighter());
            sTotalAmountFundedValueCell.setBackgroundColor(Color.gray.brighter());
            table.addCell(sTotalAmountFundedCaptionCell);
            table.addCell(sTotalAmountFundedValueCell);


            Image poweredByLogo = Image.getInstance(PARTNER_LOGO_PATH+"logo2.jpg");
            Cell poweredByLogoCaptionCell1=new Cell("");
            Cell poweredByLogoValueCell1=new Cell(poweredByLogo);
            poweredByLogoCaptionCell1.setColspan(5);
            poweredByLogoValueCell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
            poweredByLogoCaptionCell1.setBackgroundColor(Color.white.brighter());
            poweredByLogoValueCell1.setBackgroundColor(Color.white.brighter());
            table.addCell(poweredByLogoCaptionCell1);
            table.addCell(poweredByLogoValueCell1);
            document.add(table);
            document.close();

        }
        catch (Exception ex)
        {
            logger.error(ex.getStackTrace());
        }

        return reportFileName;
    }

    public String createBankPartnerReportFile(BankPartnerPayoutReportVO bankPartnerPayoutReportVO)
    {
        PartnerDetailsVO partnerDetailsVO=bankPartnerPayoutReportVO.getPartnerDetailsVO();

        GatewayAccount gatewayAccount=bankPartnerPayoutReportVO.getGatewayAccount();
        SettlementDateVO settlementDateVO=bankPartnerPayoutReportVO.getSettlementDateVO();
        List<ChargeDetailsVO> chargesDetailsVOs=bankPartnerPayoutReportVO.getChargesDetailsVOs();
        WireChargeVO wireChargeVO=bankPartnerPayoutReportVO.getWireChargeVO();
        List<GrossChargeVO> grossTypeChargeVOList=bankPartnerPayoutReportVO.getGrossTypeChargeVOList();
        PayoutReportUtils payoutReportUtils=new PayoutReportUtils();


        Document document = new Document(PageSize.A3, 50, 50, 50, 50);
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );

        String reportFileName=payoutReportUtils.getBankPartnerReportFileName(partnerDetailsVO.getPartnerId(), gatewayAccount.getMerchantId(),settlementDateVO.getSettlementEndDate(),settlementDateVO.getSettlementEndDate());
        try
        {
            String sMId=gatewayAccount.getMerchantId();


            reportFileName=reportFileName+".pdf";

            String partnerName=partnerDetailsVO.getCompanyName();
            String partnerLogoName=partnerDetailsVO.getLogoName();
            if(!functions.isValueNull(partnerLogoName))
            {
                partnerLogoName="logo.jpg";
            }

            File filepath=new File(PARTNER_PAYOUT_REPORT_FILE_PATH+reportFileName);


            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filepath));
            document.open();

            // demonstrate some table features
            Table table = new Table(6);
            table.setWidth(100);

            /*table.setBorderWidth(1);*/
            table.setBorderColor(new Color(0, 0,0));
            table.setPadding(1);
            /*table.setSpacing(1);*/

            String reportingDate=targetFormat.format(new Date());

            Image partnerImageInstance = Image.getInstance(PARTNER_LOGO_PATH+partnerLogoName);
            partnerImageInstance.scaleAbsolute(150f, 150f);
            partnerImageInstance.scaleAbsoluteHeight(40f);

            Cell partnerNameCaptionCell=new Cell("Processor:"+partnerName);
            Cell reportingDateCaptionCe11=new Cell("Reporting Date:"+reportingDate);
            Cell partnerLogoCell=new Cell(partnerImageInstance);

            partnerNameCaptionCell.setColspan(2);
            reportingDateCaptionCe11.setColspan(2);
            partnerLogoCell.setColspan(2);

            partnerLogoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            partnerNameCaptionCell.setBackgroundColor(Color.white.brighter());
            reportingDateCaptionCe11.setBackgroundColor(Color.white.brighter());

            table.addCell(partnerNameCaptionCell);
            table.addCell(reportingDateCaptionCe11);
            table.addCell(partnerLogoCell);

            Cell sMemberDetailsHeaderCell=new Cell("Bank Partner Details");
            sMemberDetailsHeaderCell.setColspan(6);
            sMemberDetailsHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            sMemberDetailsHeaderCell.setVerticalAlignment(Element.ALIGN_CENTER);

            sMemberDetailsHeaderCell.setBackgroundColor(Color.gray.brighter());
            table.addCell(sMemberDetailsHeaderCell);

            Cell sAgentCaptionCell=new Cell("Partner ID:");
            Cell sAgentCaptionValueCell=new Cell(partnerDetailsVO.getPartnerId());
            sAgentCaptionCell.setColspan(4);
            sAgentCaptionValueCell.setColspan(2);
            table.addCell(sAgentCaptionCell);
            table.addCell(sAgentCaptionValueCell);

            Cell sCompanyNameCaptionCell=new Cell("Company Name:");
            Cell sCompanyNameValueCell=new Cell(partnerDetailsVO.getCompanyName());
            sCompanyNameCaptionCell.setColspan(4);
            sCompanyNameValueCell.setColspan(2);
            table.addCell(sCompanyNameCaptionCell);
            table.addCell(sCompanyNameValueCell);

            Cell sContactPersonCaptionCell=new Cell("Contact Person:");
            Cell sContactPersonValueCell=new Cell(partnerDetailsVO.getContactPerson());
            sContactPersonCaptionCell.setColspan(4);
            sContactPersonValueCell.setColspan(2);
            table.addCell(sContactPersonCaptionCell);
            table.addCell(sContactPersonValueCell);

            Cell sMemberIdCaptionCell=new Cell("MID:");
            Cell sMemberIdValueCell=new Cell(gatewayAccount.getMerchantId());
            sMemberIdCaptionCell.setColspan(4);
            sMemberIdValueCell.setColspan(2);
            table.addCell(sMemberIdCaptionCell);
            table.addCell(sMemberIdValueCell);

            Cell sCurrencyCaptionCell=new Cell("Currency:");
            Cell sCurrencyValueCell=new Cell(gatewayAccount.getCurrency());
            sCurrencyCaptionCell.setColspan(4);
            sCurrencyValueCell.setColspan(2);
            table.addCell(sCurrencyCaptionCell);
            table.addCell(sCurrencyValueCell);

            Cell sSettlePeriodCaptionCell=new Cell("Settle Covered Upto:");
            Cell sSettleEndValueCell=new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getSettlementEndDate())));

            sSettlePeriodCaptionCell.setColspan(4);
            sSettleEndValueCell.setColspan(2);

            table.addCell(sSettlePeriodCaptionCell);
            table.addCell(sSettleEndValueCell);

            Cell sDeclinedPeriodCaptionCell=new Cell("Decline Covered Upto:");
            Cell sDeclinedEndValueCell=new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getDeclinedEndDate())));

            sDeclinedPeriodCaptionCell.setColspan(4);
            sDeclinedEndValueCell.setColspan(2);

            table.addCell(sDeclinedPeriodCaptionCell);
            table.addCell(sDeclinedEndValueCell);

            Cell sReversedPeriodCaptionCell=new Cell("Reversal Covered Upto:");
            Cell sReversedEndValueCell=new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getReversedEndDate())));

            sReversedPeriodCaptionCell.setColspan(4);
            sReversedEndValueCell.setColspan(2);

            table.addCell(sReversedPeriodCaptionCell);
            table.addCell(sReversedEndValueCell);

            Cell sChargebackPeriodCaptionCell=new Cell("Chargeback Covered Upto:");
            Cell sChargebackEndValueCell=new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getChargebackEndDate())));

            sChargebackPeriodCaptionCell.setColspan(4);
            sChargebackEndValueCell.setColspan(2);

            table.addCell(sChargebackPeriodCaptionCell);
            table.addCell(sChargebackEndValueCell);

            Cell sChargesDetailsHeaderCell=new Cell("Partner Charges Details");
            sChargesDetailsHeaderCell.setColspan(6);
            sChargesDetailsHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            sChargesDetailsHeaderCell.setBackgroundColor(Color.gray.brighter());
            table.addCell(sChargesDetailsHeaderCell);

            Cell sChargeNameHeader = new Cell("Charge Name");
            Cell sChargeRateHeader = new Cell("Rate/Fee");
            Cell sChargeCounterHeader = new Cell("Counter");
            Cell sChargeAmountHeader = new Cell("Amount");
            Cell sChargeTotalHeader = new Cell("Total");

            sChargeNameHeader.setColspan(2);

            sChargeNameHeader.setBackgroundColor(Color.gray.brighter());
            sChargeRateHeader.setBackgroundColor(Color.gray.brighter());

            sChargeCounterHeader.setBackgroundColor(Color.gray.brighter());
            sChargeAmountHeader.setBackgroundColor(Color.gray.brighter());
            sChargeTotalHeader.setBackgroundColor(Color.gray.brighter());

            sChargeNameHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            sChargeRateHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            sChargeCounterHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            sChargeAmountHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            sChargeTotalHeader.setHorizontalAlignment(Element.ALIGN_CENTER);

            table.addCell(sChargeNameHeader);
            table.addCell(sChargeRateHeader);
            table.addCell(sChargeCounterHeader);
            table.addCell(sChargeAmountHeader);
            table.addCell(sChargeTotalHeader);

            Cell sChargeNameValue,sChargeRateValue,sChargeCounterValue,sChargeAmountValue,sChargeTotalValue;

            for(ChargeDetailsVO chargeDetailsVO:chargesDetailsVOs)
            {

                String vCntCounter=(new Long(chargeDetailsVO.getCount())).toString();
                sChargeNameValue = new Cell(chargeDetailsVO.getChargeName());
                sChargeTotalValue = new Cell(Functions.round(chargeDetailsVO.getTotal(),2));
                sChargeRateValue = new Cell(chargeDetailsVO.getChargeValue());
                sChargeCounterValue = new Cell(vCntCounter);
                sChargeAmountValue = new Cell(Functions.round(chargeDetailsVO.getAmount(),2));

                sChargeNameValue.setColspan(2);

                sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                sChargeCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                table.addCell(sChargeNameValue);
                table.addCell(sChargeRateValue);
                table.addCell(sChargeCounterValue);
                table.addCell(sChargeAmountValue);
                table.addCell(sChargeTotalValue);

            }

            Cell sChargeFeeTotalCaption=new Cell("Total");
            Cell sChargeFeeTotalValue=new Cell(Functions.round(bankPartnerPayoutReportVO.getPartnerTotalChargesAmount(),2));
            sChargeFeeTotalCaption.setColspan(5);
            sChargeFeeTotalCaption.setHeader(true);

            sChargeFeeTotalCaption.setBackgroundColor(Color.gray.brighter());
            sChargeFeeTotalValue.setBackgroundColor(Color.gray.brighter());

            sChargeFeeTotalCaption.setHorizontalAlignment(Element.ALIGN_RIGHT);
            sChargeFeeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);


            table.addCell(sChargeFeeTotalCaption);
            table.addCell(sChargeFeeTotalValue);

            Cell sAgentChangesAmountCaptionCell=new Cell("Partner Charges Total Amount");
            Cell sAgentChangesAmountValueCell=new Cell(Functions.round(bankPartnerPayoutReportVO.getPartnerTotalChargesAmount(),2));

            sAgentChangesAmountCaptionCell.setColspan(5);

            sAgentChangesAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(sAgentChangesAmountCaptionCell);
            table.addCell(sAgentChangesAmountValueCell);

            if(wireChargeVO!=null)
            {
                sChargeNameValue = new Cell(wireChargeVO.getChargeName());
                sChargeRateValue = new Cell(wireChargeVO.getChargeValue());

                sChargeCounterValue = new Cell(new Long(wireChargeVO.getCount()).toString());
                sChargeAmountValue = new Cell("-");
                sChargeTotalValue = new Cell(isValidAmount(Functions.round(wireChargeVO.getTotal(),2)));

                sChargeNameValue.setColspan(2);

                sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                sChargeCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                table.addCell(sChargeNameValue);
                table.addCell(sChargeRateValue);
                table.addCell(sChargeCounterValue);
                table.addCell(sChargeAmountValue);
                table.addCell(sChargeTotalValue);
            }

            for(GrossChargeVO grossChargeVO:grossTypeChargeVOList)
            {
                sChargeNameValue = new Cell(grossChargeVO.getChargeName());
                sChargeRateValue = new Cell(grossChargeVO.getChargeValue());
                sChargeCounterValue = new Cell("-");
                sChargeAmountValue = new Cell(Functions.round(bankPartnerPayoutReportVO.getPartnerTotalChargesAmount(),2));
                sChargeTotalValue = new Cell(isValidAmount(Functions.round(grossChargeVO.getTotal(),2)));

                sChargeNameValue.setColspan(2);

                sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                table.addCell(sChargeNameValue);
                table.addCell(sChargeRateValue);
                table.addCell(sChargeCounterValue);
                table.addCell(sChargeAmountValue);
                table.addCell(sChargeTotalValue);

            }
            Cell sUnpaidAmountCaptionCell=new Cell("Paid Amount");
            Cell sUnpaidAmountValueCell=new Cell(isValidAmount(Functions.round(bankPartnerPayoutReportVO.getPartnerWirePaidAmount(),2)));
            sUnpaidAmountCaptionCell.setColspan(5);
            sUnpaidAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sUnpaidAmountCaptionCell);
            table.addCell(sUnpaidAmountValueCell);


            Cell sPreviousBalanceAmountCaptionCell=new Cell("Previous Balance Amount");
            Cell sPreviousBalanceAmountValueCell=new Cell(Functions.round(bankPartnerPayoutReportVO.getPartnerTotalBalanceAmount(),2));
            sPreviousBalanceAmountCaptionCell.setColspan(5);
            sPreviousBalanceAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sPreviousBalanceAmountCaptionCell);
            table.addCell(sPreviousBalanceAmountValueCell);

            Cell sTotalAmountFundedCaptionCell=new Cell("Total Amount Funded");
            Cell sTotalAmountFundedValueCell=new Cell(Functions.round(bankPartnerPayoutReportVO.getPartnerTotalFundedAmount(),2));
            sTotalAmountFundedCaptionCell.setColspan(5);
            sTotalAmountFundedValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            sTotalAmountFundedCaptionCell.setBackgroundColor(Color.gray.brighter());
            sTotalAmountFundedValueCell.setBackgroundColor(Color.gray.brighter());
            table.addCell(sTotalAmountFundedCaptionCell);
            table.addCell(sTotalAmountFundedValueCell);


            Image poweredByLogo = Image.getInstance(PARTNER_LOGO_PATH+"logo2.jpg");
            Cell poweredByLogoCaptionCell1=new Cell("");
            Cell poweredByLogoValueCell1=new Cell(poweredByLogo);
            poweredByLogoCaptionCell1.setColspan(5);
            poweredByLogoValueCell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
            poweredByLogoCaptionCell1.setBackgroundColor(Color.white.brighter());
            poweredByLogoValueCell1.setBackgroundColor(Color.white.brighter());
            table.addCell(poweredByLogoCaptionCell1);
            table.addCell(poweredByLogoValueCell1);
            document.add(table);
            document.close();

        }
        catch (Exception ex)
        {
            logger.error(ex.getStackTrace());
        }

        return reportFileName;
    }

    public String createPartnerReportFile(PartnerPayoutReportVO partnerPayoutReportVO)
    {
        PartnerDetailsVO partnerDetailsVO=partnerPayoutReportVO.getPartnerDetailsVO();
        TerminalVO terminalVO=partnerPayoutReportVO.getTerminalVO();
        SettlementDateVO settlementDateVO=partnerPayoutReportVO.getSettlementDateVO();
        List<ChargeDetailsVO> chargesDetailsVOs=partnerPayoutReportVO.getChargesDetailsVOs();
        WireChargeVO wireChargeVO=partnerPayoutReportVO.getWireChargeVO();
        List<GrossChargeVO> grossTypeChargeVOList=partnerPayoutReportVO.getGrossTypeChargeVOList();
        PayoutReportUtils payoutReportUtils=new PayoutReportUtils();

        Document document = new Document(PageSize.A3, 50, 50, 50, 50);
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );

        String reportFileName=payoutReportUtils.getPartnerReportFileName(partnerDetailsVO.getPartnerId(), terminalVO.getMemberId(),terminalVO.getTerminalId(), settlementDateVO.getSettlementEndDate(), settlementDateVO.getSettlementEndDate());
        try
        {
            reportFileName=reportFileName+".pdf";

            File filepath=new File(PARTNER_PAYOUT_REPORT_FILE_PATH+reportFileName);


            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filepath));
            document.open();

            // demonstrate some table features
            Table table = new Table(6);
            table.setWidth(100);

            /*table.setBorderWidth(1);*/
            table.setBorderColor(new Color(0, 0,0));
            table.setPadding(1);
            /*table.setSpacing(1);*/

            String reportingDate=targetFormat.format(new Date());
            if(!functions.isValueNull(partnerDetailsVO.getLogoName()))
            {
                partnerDetailsVO.setLogoName("logo.jpg");
            }

            Image partnerImageInstance = Image.getInstance(PARTNER_LOGO_PATH+partnerDetailsVO.getLogoName());
            partnerImageInstance.scaleAbsolute(150f, 150f);
            partnerImageInstance.scaleAbsoluteHeight(40f);

            Cell partnerNameCaptionCell=new Cell("Processor:"+partnerDetailsVO.getCompanyName());
            Cell reportingDateCaptionCe11=new Cell("Reporting Date:"+reportingDate);
            Cell partnerLogoCell=new Cell(partnerImageInstance);

            partnerNameCaptionCell.setColspan(2);
            reportingDateCaptionCe11.setColspan(2);
            partnerLogoCell.setColspan(2);

            partnerLogoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            partnerNameCaptionCell.setBackgroundColor(Color.white.brighter());
            reportingDateCaptionCe11.setBackgroundColor(Color.white.brighter());

            table.addCell(partnerNameCaptionCell);
            table.addCell(reportingDateCaptionCe11);
            table.addCell(partnerLogoCell);

            Cell sMemberDetailsHeaderCell=new Cell("Partner Details");
            sMemberDetailsHeaderCell.setColspan(6);
            sMemberDetailsHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            sMemberDetailsHeaderCell.setVerticalAlignment(Element.ALIGN_CENTER);

            sMemberDetailsHeaderCell.setBackgroundColor(Color.gray.brighter());
            table.addCell(sMemberDetailsHeaderCell);

            Cell sPartnerCaptionCell=new Cell("Partner ID:");
            Cell sPartnerCaptionValueCell=new Cell(partnerDetailsVO.getPartnerId());
            sPartnerCaptionCell.setColspan(4);
            sPartnerCaptionValueCell.setColspan(2);
            table.addCell(sPartnerCaptionCell);
            table.addCell(sPartnerCaptionValueCell);

            Cell sMemberIdCaptionCell=new Cell("Member ID:");
            Cell sMemberIdValueCell=new Cell(terminalVO.getMemberId());
            sMemberIdCaptionCell.setColspan(4);
            sMemberIdValueCell.setColspan(2);
            table.addCell(sMemberIdCaptionCell);
            table.addCell(sMemberIdValueCell);

            Cell sTerminalCaptionCell=new Cell("Termial Id:");
            Cell sTerminalValueCell=new Cell(terminalVO.getTerminalId());
            sTerminalCaptionCell.setColspan(4);
            sTerminalValueCell.setColspan(2);
            table.addCell(sTerminalCaptionCell);
            table.addCell(sTerminalValueCell);

            Cell sCompanyNameCaptionCell=new Cell("Company Name:");
            Cell sCompanyNameValueCell=new Cell(partnerDetailsVO.getCompanyName());
            sCompanyNameCaptionCell.setColspan(4);
            sCompanyNameValueCell.setColspan(2);
            table.addCell(sCompanyNameCaptionCell);
            table.addCell(sCompanyNameValueCell);

            Cell sContactPersonCaptionCell=new Cell("Contact Person:");
            Cell sContactPersonValueCell=new Cell(partnerDetailsVO.getContactPerson());
            sContactPersonCaptionCell.setColspan(4);
            sContactPersonValueCell.setColspan(2);
            table.addCell(sContactPersonCaptionCell);
            table.addCell(sContactPersonValueCell);

            Cell sPayModeCaptionCell=new Cell("Payment Mode:");
            Cell sPayModeValueCell=new Cell(terminalVO.getPaymentTypeName());
            sPayModeCaptionCell.setColspan(4);
            sPayModeValueCell.setColspan(2);
            table.addCell(sPayModeCaptionCell);
            table.addCell(sPayModeValueCell);

            Cell sCardTypeCaptionCell=new Cell("Card Type:");
            Cell sCardTypeValueCell=new Cell(terminalVO.getCardType());
            sCardTypeCaptionCell.setColspan(4);
            sCardTypeValueCell.setColspan(2);
            table.addCell(sCardTypeCaptionCell);
            table.addCell(sCardTypeValueCell);

            Cell sCurrencyCaptionCell=new Cell("Currency:");
            Cell sCurrencyValueCell=new Cell(partnerPayoutReportVO.getCurrency());
            sCurrencyCaptionCell.setColspan(4);
            sCurrencyValueCell.setColspan(2);
            table.addCell(sCurrencyCaptionCell);
            table.addCell(sCurrencyValueCell);

            Cell sSettlePeriodCaptionCell=new Cell("Settle Covered Upto:");
            Cell sSettleEndValueCell=new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getSettlementEndDate())));

            sSettlePeriodCaptionCell.setColspan(4);
            sSettleEndValueCell.setColspan(2);

            table.addCell(sSettlePeriodCaptionCell);
            table.addCell(sSettleEndValueCell);

            Cell sDeclinedPeriodCaptionCell=new Cell("Decline Covered Upto:");
            Cell sDeclinedEndValueCell=new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getDeclinedEndDate())));

            sDeclinedPeriodCaptionCell.setColspan(4);
            sDeclinedEndValueCell.setColspan(2);

            table.addCell(sDeclinedPeriodCaptionCell);
            table.addCell(sDeclinedEndValueCell);

            Cell sReversedPeriodCaptionCell=new Cell("Reversal Covered Upto:");
            Cell sReversedEndValueCell=new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getReversedEndDate())));

            sReversedPeriodCaptionCell.setColspan(4);
            sReversedEndValueCell.setColspan(2);

            table.addCell(sReversedPeriodCaptionCell);
            table.addCell(sReversedEndValueCell);

            Cell sChargebackPeriodCaptionCell=new Cell("Chargeback Covered Upto:");
            Cell sChargebackEndValueCell=new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getChargebackEndDate())));

            sChargebackPeriodCaptionCell.setColspan(4);
            sChargebackEndValueCell.setColspan(2);

            table.addCell(sChargebackPeriodCaptionCell);
            table.addCell(sChargebackEndValueCell);

            Cell sChargesDetailsHeaderCell=new Cell("Partner Charges Details");
            sChargesDetailsHeaderCell.setColspan(6);
            sChargesDetailsHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            sChargesDetailsHeaderCell.setBackgroundColor(Color.gray.brighter());
            table.addCell(sChargesDetailsHeaderCell);

            Cell sChargeNameHeader = new Cell("Charge Name");
            Cell sChargeRateHeader = new Cell("Rate/Fee");
            Cell sChargeCounterHeader = new Cell("Counter");
            Cell sChargeAmountHeader = new Cell("Amount");
            Cell sChargeTotalHeader = new Cell("Total");

            sChargeNameHeader.setColspan(2);

            sChargeNameHeader.setBackgroundColor(Color.gray.brighter());
            sChargeRateHeader.setBackgroundColor(Color.gray.brighter());

            sChargeCounterHeader.setBackgroundColor(Color.gray.brighter());
            sChargeAmountHeader.setBackgroundColor(Color.gray.brighter());
            sChargeTotalHeader.setBackgroundColor(Color.gray.brighter());

            sChargeNameHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            sChargeRateHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            sChargeCounterHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            sChargeAmountHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            sChargeTotalHeader.setHorizontalAlignment(Element.ALIGN_CENTER);

            table.addCell(sChargeNameHeader);
            table.addCell(sChargeRateHeader);
            table.addCell(sChargeCounterHeader);
            table.addCell(sChargeAmountHeader);
            table.addCell(sChargeTotalHeader);

            Cell sChargeNameValue,sChargeRateValue,sChargeCounterValue,sChargeAmountValue,sChargeTotalValue;

            for(ChargeDetailsVO chargeDetailsVO:chargesDetailsVOs)
            {

                String vCntCounter=(new Long(chargeDetailsVO.getCount())).toString();
                sChargeNameValue = new Cell(chargeDetailsVO.getChargeName());
                sChargeTotalValue = new Cell(Functions.round(chargeDetailsVO.getTotal(),2));
                sChargeRateValue = new Cell(chargeDetailsVO.getChargeValue());
                sChargeCounterValue = new Cell(vCntCounter);
                sChargeAmountValue = new Cell(Functions.round(chargeDetailsVO.getAmount(),2));

                sChargeNameValue.setColspan(2);

                sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                sChargeCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                table.addCell(sChargeNameValue);
                table.addCell(sChargeRateValue);
                table.addCell(sChargeCounterValue);
                table.addCell(sChargeAmountValue);
                table.addCell(sChargeTotalValue);

            }

            Cell sChargeFeeTotalCaption=new Cell("Total");
            Cell sChargeFeeTotalValue=new Cell(Functions.round(partnerPayoutReportVO.getPartnerTotalChargesAmount(),2));
            sChargeFeeTotalCaption.setColspan(5);
            sChargeFeeTotalCaption.setHeader(true);

            sChargeFeeTotalCaption.setBackgroundColor(Color.gray.brighter());
            sChargeFeeTotalValue.setBackgroundColor(Color.gray.brighter());

            sChargeFeeTotalCaption.setHorizontalAlignment(Element.ALIGN_RIGHT);
            sChargeFeeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);


            table.addCell(sChargeFeeTotalCaption);
            table.addCell(sChargeFeeTotalValue);

            Cell sAgentChangesAmountCaptionCell=new Cell("Partner Charges Total Amount");
            Cell sAgentChangesAmountValueCell=new Cell(Functions.round(partnerPayoutReportVO.getPartnerTotalChargesAmount(),2));

            sAgentChangesAmountCaptionCell.setColspan(5);

            sAgentChangesAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(sAgentChangesAmountCaptionCell);
            table.addCell(sAgentChangesAmountValueCell);

            if(wireChargeVO!=null)
            {
                sChargeNameValue = new Cell(wireChargeVO.getChargeName());
                sChargeRateValue = new Cell(wireChargeVO.getChargeValue());

                sChargeCounterValue = new Cell(new Long(wireChargeVO.getCount()).toString());
                sChargeAmountValue = new Cell("-");
                sChargeTotalValue = new Cell("-"+Functions.round(wireChargeVO.getTotal(),2));

                sChargeNameValue.setColspan(2);

                sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                sChargeCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                table.addCell(sChargeNameValue);
                table.addCell(sChargeRateValue);
                table.addCell(sChargeCounterValue);
                table.addCell(sChargeAmountValue);
                table.addCell(sChargeTotalValue);
            }

            for(GrossChargeVO grossChargeVO:grossTypeChargeVOList)
            {
                sChargeNameValue = new Cell(grossChargeVO.getChargeName());
                sChargeRateValue = new Cell(grossChargeVO.getChargeValue());
                sChargeCounterValue = new Cell("-");
                sChargeAmountValue = new Cell(Functions.round(partnerPayoutReportVO.getPartnerTotalChargesAmount(),2));
                sChargeTotalValue = new Cell("-"+Functions.round(grossChargeVO.getTotal(),2));

                sChargeNameValue.setColspan(2);

                sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                table.addCell(sChargeNameValue);
                table.addCell(sChargeRateValue);
                table.addCell(sChargeCounterValue);
                table.addCell(sChargeAmountValue);
                table.addCell(sChargeTotalValue);

            }
            Cell sUnpaidAmountCaptionCell=new Cell("Paid Amount");
            Cell sUnpaidAmountValueCell=new Cell(isValidAmount("-"+Functions.round(partnerPayoutReportVO.getPartnerWirePaidAmount(),2)));
            sUnpaidAmountCaptionCell.setColspan(5);
            sUnpaidAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sUnpaidAmountCaptionCell);
            table.addCell(sUnpaidAmountValueCell);


            Cell sPreviousBalanceAmountCaptionCell=new Cell("Previous Balance Amount");
            Cell sPreviousBalanceAmountValueCell=new Cell(Functions.round(partnerPayoutReportVO.getPartnerTotalBalanceAmount(),2));
            sPreviousBalanceAmountCaptionCell.setColspan(5);
            sPreviousBalanceAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sPreviousBalanceAmountCaptionCell);
            table.addCell(sPreviousBalanceAmountValueCell);

            Cell sTotalAmountFundedCaptionCell=new Cell("Total Amount Funded");
            Cell sTotalAmountFundedValueCell=new Cell(Functions.round(partnerPayoutReportVO.getPartnerTotalFundedAmount(),2));
            sTotalAmountFundedCaptionCell.setColspan(5);
            sTotalAmountFundedValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            sTotalAmountFundedCaptionCell.setBackgroundColor(Color.gray.brighter());
            sTotalAmountFundedValueCell.setBackgroundColor(Color.gray.brighter());
            table.addCell(sTotalAmountFundedCaptionCell);
            table.addCell(sTotalAmountFundedValueCell);


            Image poweredByLogo = Image.getInstance(PARTNER_LOGO_PATH+"logo2.jpg");
            Cell poweredByLogoCaptionCell1=new Cell("");
            Cell poweredByLogoValueCell1=new Cell(poweredByLogo);
            poweredByLogoCaptionCell1.setColspan(5);
            poweredByLogoValueCell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
            poweredByLogoCaptionCell1.setBackgroundColor(Color.white.brighter());
            poweredByLogoValueCell1.setBackgroundColor(Color.white.brighter());
            table.addCell(poweredByLogoCaptionCell1);
            table.addCell(poweredByLogoValueCell1);


            document.add(table);
            document.close();

        }
        catch (Exception ex)
        {
            logger.error(ex);
        }

        return reportFileName;
    }

    public String  getAgentReportType(String memberId,String accountId,String payModeId,String cardTypeId)
    {
        Functions functions=new Functions();
        if(functions.isValueNull(memberId) && functions.isValueNull(accountId) &&functions.isValueNull(payModeId) && functions.isValueNull(cardTypeId))
        {
            return "wireReport";
        }
        else if(functions.isValueNull(memberId))
        {
            return "summaryReport";
        }
        else
        {
            return "consolidateReport";
        }

    }

    public String  getBankAgentReportType(String AgentId,String pgTypeId,String accountId)
    {
        Functions functions=new Functions();
        if(functions.isValueNull(AgentId) && functions.isValueNull(pgTypeId) && functions.isValueNull(accountId))
        {
            return "wireReport";
        }
        else
        {
            return "summaryReport";
        }

    }

    public String  getBankPartnerReportType(String partnerId,String pgTypeId,String accountId)
    {
        Functions functions=new Functions();
        if(functions.isValueNull(partnerId) && functions.isValueNull(pgTypeId) && functions.isValueNull(accountId))
        {
            return "wireReport";
        }
        else
        {
            return "summaryReport";
        }

    }

    public String  getPartnerReportType(String memberId,String accountId,String payModeId,String cardTypeId)
    {
        Functions functions=new Functions();
        if(functions.isValueNull(memberId) && functions.isValueNull(accountId) &&functions.isValueNull(payModeId) && functions.isValueNull(cardTypeId))
        {
            return "wireReport";
        }
        else if(functions.isValueNull(memberId))
        {
            return "summaryReport";
        }
        else
        {
            return "consolidateReport";
        }
    }

    public String convert2Decimal(double amount)
    {
        BigDecimal value=new BigDecimal(amount);
        String strAmount=String.valueOf(value.setScale(2,BigDecimal.ROUND_DOWN));
        return strAmount;
    }

    public ChargeDetailsVO calculatePartnerChargeValue(ChargeVO chargeVO,TerminalVO terminalVO,String sTableName,TransactionSummaryVO summaryVO,String firstTransactionDate)
    {
        long settledTransCount = 0;
        long reversedTransCount = 0;
        long chargebackTransCount = 0;
        long authfailedTransCount = 0;
        long varifyOrderCount=0;
        long refundAlertCount=0;
        long retrivalRequestCount=0;

        double totalAuthfailedTransAmount=0.00;
        double totalSettledTransAmount=0.00;
        double totalReversedTransAmount = 0.00;
        double totalChargebackTransAmount = 0.00;

        authfailedTransCount=summaryVO.getCountOfAuthfailed();
        totalAuthfailedTransAmount=summaryVO.getAuthfailedAmount();

        settledTransCount=summaryVO.getCountOfSettled();
        totalSettledTransAmount=summaryVO.getSettledAmount();

        reversedTransCount=summaryVO.getCountOfReversed();
        totalReversedTransAmount=summaryVO.getReversedAmount();

        chargebackTransCount=summaryVO.getCountOfChargeback();
        totalChargebackTransAmount=summaryVO.getChargebackAmount();

        long vCntCounter=0;
        double vDblAmount=0.00;
        double vDblTotal=0.00;
        ChargeDetailsVO chargeDetailsVO=new ChargeDetailsVO();
        chargeDetailsVO.setChargeName(chargeVO.getChargename());
        if(Charge_unit.Percentage.toString().equals(chargeVO.getValuetype()))
        {
            chargeDetailsVO.setChargeValue(chargeVO.getPartnerChargeValue()+"%");
        }
        else
            chargeDetailsVO.setChargeValue(chargeVO.getPartnerChargeValue());

        if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=settledTransCount+reversedTransCount+chargebackTransCount;
            vDblAmount=totalSettledTransAmount+totalReversedTransAmount+totalChargebackTransAmount;
            vDblTotal=(vDblAmount*Double.valueOf(chargeVO.getPartnerChargeValue()))/100;

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=settledTransCount+reversedTransCount+chargebackTransCount;
            vDblAmount=totalSettledTransAmount+totalReversedTransAmount+totalChargebackTransAmount;
            vDblTotal=vCntCounter*Double.valueOf(chargeVO.getPartnerChargeValue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));

        }
        else if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Reversed.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=reversedTransCount;
            vDblAmount=totalReversedTransAmount;
            vDblTotal=(vCntCounter*Double.valueOf(chargeVO.getPartnerChargeValue()));

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Reversed.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=reversedTransCount;
            vDblAmount=totalReversedTransAmount;
            vDblTotal=(vDblAmount*Double.valueOf(chargeVO.getPartnerChargeValue()))/100;

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Chargeback.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=chargebackTransCount;
            vDblAmount=totalChargebackTransAmount;
            vDblTotal=(vCntCounter*Double.valueOf(chargeVO.getPartnerChargeValue()));

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Failure.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=authfailedTransCount;
            vDblAmount=totalAuthfailedTransAmount;
            vDblTotal=(vCntCounter*Double.valueOf(chargeVO.getPartnerChargeValue()));

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Failure.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=authfailedTransCount;
            vDblAmount=totalAuthfailedTransAmount;
            vDblTotal=(vDblAmount*Double.valueOf(chargeVO.getPartnerChargeValue()))/100;

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Others.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter= authfailedTransCount + settledTransCount + reversedTransCount + chargebackTransCount;
            vDblAmount=totalAuthfailedTransAmount+totalReversedTransAmount+totalChargebackTransAmount+totalSettledTransAmount;
            vDblTotal=(vCntCounter*Double.valueOf(chargeVO.getPartnerChargeValue()));

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Others.toString().equals(chargeVO.getCategory()) && Charge_keyword.VerifyOrder.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            long memberVerifyOrderAlertCount=payoutDAO.getVerifyOrderCount(terminalVO,new SettlementDateVO());
            vCntCounter=memberVerifyOrderAlertCount;
            varifyOrderCount=varifyOrderCount+vCntCounter;
            vDblTotal=vCntCounter*Double.valueOf(chargeVO.getPartnerChargeValue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Others.toString().equals(chargeVO.getCategory()) && Charge_keyword.RefundAlert.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            long memberRefundAlertCount=payoutDAO.getRefundAlertCount(terminalVO,new SettlementDateVO());
            vCntCounter=memberRefundAlertCount;
            refundAlertCount=refundAlertCount+vCntCounter;
            vDblTotal=vCntCounter*Double.valueOf(chargeVO.getPartnerChargeValue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Others.toString().equals(chargeVO.getCategory()) && Charge_keyword.RetrivalRequest.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            long allRetrivalRequestCount=payoutDAO.getRetrivalRequestCount(terminalVO,sTableName);
            vCntCounter=allRetrivalRequestCount;
            retrivalRequestCount=retrivalRequestCount+vCntCounter;
            vDblTotal=vCntCounter*Double.valueOf(chargeVO.getAgentChargeValue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Others.toString().equals(chargeVO.getCategory()) && Charge_keyword.Setup.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            SetupChargeVO setupChargeVO=null;
            setupChargeVO=applySetupChargeForPartnerSummary(chargeVO,firstTransactionDate);
            vCntCounter=setupChargeVO.getCount();
            vDblTotal=setupChargeVO.getTotal();

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        return chargeDetailsVO;
    }

    public CommissionDetailsVO calculateAgentCommissionValue(AgentCommissionVO agentCommissionVO,TransactionSummaryVO summaryVO)
    {
        long settledTransCount = 0;
        long reversedTransCount = 0;
        long chargebackTransCount = 0;
        long authfailedTransCount = 0;
        long totalSaleCount = 0;

        double totalAuthfailedTransAmount=0.00;
        double totalSettledTransAmount=0.00;
        double totalReversedTransAmount = 0.00;
        double totalChargebackTransAmount = 0.00;
        double totalSaleAmount = 0.00;

        totalSaleCount=summaryVO.getTotalProcessingCount();
        totalSaleAmount=summaryVO.getTotalProcessingAmount();

        authfailedTransCount=summaryVO.getCountOfAuthfailed();
        totalAuthfailedTransAmount=summaryVO.getAuthfailedAmount();

        settledTransCount=summaryVO.getCountOfSettled();
        totalSettledTransAmount=summaryVO.getSettledAmount();

        reversedTransCount=summaryVO.getCountOfReversed();
        totalReversedTransAmount=summaryVO.getReversedAmount();

        chargebackTransCount=summaryVO.getCountOfChargeback();
        totalChargebackTransAmount=summaryVO.getChargebackAmount();

        long vCntCounter=0;
        double vDblAmount=0.00;
        double vDblTotal=0.00;

        ChargeMasterVO  chargeMasterVO=agentCommissionVO.getChargeMasterVO();

        CommissionDetailsVO chargeDetailsVO=new CommissionDetailsVO();

        chargeDetailsVO.setChargeName(chargeMasterVO.getChargeName());
        chargeDetailsVO.setChargeValue(String.valueOf(agentCommissionVO.getCommissionValue()));
        chargeDetailsVO.setValueType(chargeMasterVO.getValueType());

        if(Charge_category.Success.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeMasterVO.getSubKeyword()))
        {
            vCntCounter=totalSaleCount;/*settledTransCount+reversedTransCount+chargebackTransCount;*/
            vDblAmount=totalSaleAmount;/*totalSettledTransAmount+totalReversedTransAmount+totalChargebackTransAmount;*/
            vDblTotal=(vDblAmount*agentCommissionVO.getCommissionValue())/100;

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Success.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeMasterVO.getSubKeyword()))
        {
            vCntCounter=totalSaleCount;/*settledTransCount+reversedTransCount+chargebackTransCount;*/
            vDblAmount=totalSaleAmount;/*totalSettledTransAmount+totalReversedTransAmount+totalChargebackTransAmount;*/
            vDblTotal=vCntCounter*agentCommissionVO.getCommissionValue();

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));

        }
        else if(Charge_category.Success.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Reversed.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeMasterVO.getSubKeyword()))
        {
            vCntCounter=reversedTransCount;
            vDblAmount=totalReversedTransAmount;
            vDblTotal=(vCntCounter*agentCommissionVO.getCommissionValue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Success.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Chargeback.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeMasterVO.getSubKeyword()))
        {
            vCntCounter=chargebackTransCount;
            vDblAmount=totalChargebackTransAmount;
            vDblTotal=(vCntCounter*agentCommissionVO.getCommissionValue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Failure.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeMasterVO.getSubKeyword()))
        {
            vCntCounter=authfailedTransCount;
            vDblAmount=totalAuthfailedTransAmount;
            vDblTotal=(vCntCounter*agentCommissionVO.getCommissionValue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Others.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeMasterVO.getSubKeyword()))
        {
            vCntCounter= totalSaleCount+authfailedTransCount;
            vDblAmount=totalSaleAmount+totalAuthfailedTransAmount;
            vDblTotal=(vCntCounter*agentCommissionVO.getCommissionValue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        return chargeDetailsVO;
    }

    public ChargeDetailsVO calculateAgentChargesValue(ChargeVO chargeVO,TransactionSummaryVO summaryVO)
    {
        long settledTransCount = 0;
        long reversedTransCount = 0;
        long chargebackTransCount = 0;
        long authfailedTransCount = 0;
        long totalSaleCount = 0;

        double totalAuthfailedTransAmount=0.00;
        double totalSettledTransAmount=0.00;
        double totalReversedTransAmount = 0.00;
        double totalChargebackTransAmount = 0.00;
        double totalSaleAmount = 0.00;

        totalSaleCount=summaryVO.getTotalProcessingCount();
        totalSaleAmount=summaryVO.getTotalProcessingAmount();

        authfailedTransCount=summaryVO.getCountOfAuthfailed();
        totalAuthfailedTransAmount=summaryVO.getAuthfailedAmount();

        settledTransCount=summaryVO.getCountOfSettled();
        totalSettledTransAmount=summaryVO.getSettledAmount();

        reversedTransCount=summaryVO.getCountOfReversed();
        totalReversedTransAmount=summaryVO.getReversedAmount();

        chargebackTransCount=summaryVO.getCountOfChargeback();
        totalChargebackTransAmount=summaryVO.getChargebackAmount();

        long vCntCounter=0;
        double vDblAmount=0.00;
        double vDblTotal=0.00;


        ChargeDetailsVO chargeDetailsVO=new ChargeDetailsVO();

        chargeDetailsVO.setChargeName(chargeVO.getChargename());
        chargeDetailsVO.setChargeValue(String.valueOf(chargeVO.getAgentChargeValue()));
        chargeDetailsVO.setValueType(chargeVO.getValuetype());

        if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=totalSaleCount;/*settledTransCount+reversedTransCount+chargebackTransCount;*/
            vDblAmount=totalSaleAmount;/*totalSettledTransAmount+totalReversedTransAmount+totalChargebackTransAmount;*/
            vDblTotal=(vDblAmount*Double.valueOf(chargeVO.getAgentChargeValue()))/100;

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=totalSaleCount;/*settledTransCount+reversedTransCount+chargebackTransCount;*/
            vDblAmount=totalSaleAmount;/*totalSettledTransAmount+totalReversedTransAmount+totalChargebackTransAmount;*/
            vDblTotal=vCntCounter*Double.valueOf(chargeVO.getAgentChargeValue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));

        }
        else if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Reversed.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=reversedTransCount;
            vDblAmount=totalReversedTransAmount;
            vDblTotal=(vCntCounter*Double.valueOf(chargeVO.getAgentChargeValue()));

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Chargeback.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=chargebackTransCount;
            vDblAmount=totalChargebackTransAmount;
            vDblTotal=(vCntCounter*Double.valueOf(chargeVO.getAgentChargeValue()));

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Failure.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=authfailedTransCount;
            vDblAmount=totalAuthfailedTransAmount;
            vDblTotal=(vCntCounter*Double.valueOf(chargeVO.getAgentChargeValue()));

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Others.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=totalSaleCount+authfailedTransCount;
            vDblAmount=totalSaleAmount+totalAuthfailedTransAmount;
            vDblTotal=(vCntCounter*Double.valueOf(chargeVO.getAgentChargeValue()));

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Others.toString().equals(chargeVO.getCategory()) && Charge_keyword.Wire.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=1;
            vDblAmount=0.00;
            vDblTotal=(vCntCounter*Double.valueOf(chargeVO.getPartnerChargeValue()));

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        return chargeDetailsVO;
    }

    public ChargeDetailsVO calculatePartnerChargesValue(ChargeVO chargeVO,TransactionSummaryVO summaryVO)
    {
        long settledTransCount = 0;
        long reversedTransCount = 0;
        long chargebackTransCount = 0;
        long authfailedTransCount = 0;
        long totalSaleCount = 0;

        double totalAuthfailedTransAmount=0.00;
        double totalSettledTransAmount=0.00;
        double totalReversedTransAmount = 0.00;
        double totalChargebackTransAmount = 0.00;
        double totalSaleAmount = 0.00;

        totalSaleCount=summaryVO.getTotalProcessingCount();
        totalSaleAmount=summaryVO.getTotalProcessingAmount();

        authfailedTransCount=summaryVO.getCountOfAuthfailed();
        totalAuthfailedTransAmount=summaryVO.getAuthfailedAmount();

        settledTransCount=summaryVO.getCountOfSettled();
        totalSettledTransAmount=summaryVO.getSettledAmount();

        reversedTransCount=summaryVO.getCountOfReversed();
        totalReversedTransAmount=summaryVO.getReversedAmount();

        chargebackTransCount=summaryVO.getCountOfChargeback();
        totalChargebackTransAmount=summaryVO.getChargebackAmount();

        long vCntCounter=0;
        double vDblAmount=0.00;
        double vDblTotal=0.00;

        ChargeDetailsVO chargeDetailsVO=new ChargeDetailsVO();

        chargeDetailsVO.setChargeName(chargeVO.getChargename());
        chargeDetailsVO.setChargeValue(String.valueOf(chargeVO.getPartnerChargeValue()));
        chargeDetailsVO.setValueType(chargeVO.getValuetype());

        if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=totalSaleCount;/*settledTransCount+reversedTransCount+chargebackTransCount;*/
            vDblAmount=totalSaleAmount;/*totalSettledTransAmount+totalReversedTransAmount+totalChargebackTransAmount;*/
            vDblTotal=(vDblAmount*Double.valueOf(chargeVO.getPartnerChargeValue()))/100;

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=totalSaleCount;/*settledTransCount+reversedTransCount+chargebackTransCount;*/
            vDblAmount=totalSaleAmount;/*totalSettledTransAmount+totalReversedTransAmount+totalChargebackTransAmount;*/
            vDblTotal=vCntCounter*Double.valueOf(chargeVO.getPartnerChargeValue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));

        }
        else if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Reversed.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=reversedTransCount;
            vDblAmount=totalReversedTransAmount;
            vDblTotal=(vCntCounter*Double.valueOf(chargeVO.getPartnerChargeValue()));

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Chargeback.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=chargebackTransCount;
            vDblAmount=totalChargebackTransAmount;
            vDblTotal=(vCntCounter*Double.valueOf(chargeVO.getPartnerChargeValue()));

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Failure.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=authfailedTransCount;
            vDblAmount=totalAuthfailedTransAmount;
            vDblTotal=(vCntCounter*Double.valueOf(chargeVO.getPartnerChargeValue()));

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Others.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=totalSaleCount+authfailedTransCount;
            vDblAmount=totalSaleAmount+totalAuthfailedTransAmount;
            vDblTotal=(vCntCounter*Double.valueOf(chargeVO.getPartnerChargeValue()));

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Others.toString().equals(chargeVO.getCategory()) && Charge_keyword.Wire.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=1;
            vDblAmount=0.00;
            vDblTotal=(vCntCounter*Double.valueOf(chargeVO.getPartnerChargeValue()));

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        return chargeDetailsVO;
    }

    public CommissionDetailsVO calculatePartnerCommissionValue(PartnerCommissionVO partnerCommissionVO,TransactionSummaryVO summaryVO)
    {
        long settledTransCount = 0;
        long reversedTransCount = 0;
        long chargebackTransCount = 0;
        long authfailedTransCount = 0;
        long totalSaleCount = 0;

        double totalAuthfailedTransAmount=0.00;
        double totalSettledTransAmount=0.00;
        double totalReversedTransAmount = 0.00;
        double totalChargebackTransAmount = 0.00;
        double totalSaleAmount = 0.00;

        totalSaleAmount=summaryVO.getTotalProcessingAmount();
        totalSaleCount=summaryVO.getTotalProcessingCount();

        authfailedTransCount=summaryVO.getCountOfAuthfailed();
        totalAuthfailedTransAmount=summaryVO.getAuthfailedAmount();

        settledTransCount=summaryVO.getCountOfSettled();
        totalSettledTransAmount=summaryVO.getSettledAmount();

        reversedTransCount=summaryVO.getCountOfReversed();
        totalReversedTransAmount=summaryVO.getReversedAmount();

        chargebackTransCount=summaryVO.getCountOfChargeback();
        totalChargebackTransAmount=summaryVO.getChargebackAmount();

        long vCntCounter=0;
        double vDblAmount=0.00;
        double vDblTotal=0.00;

        ChargeMasterVO  chargeMasterVO=partnerCommissionVO.getChargeMasterVO();

        CommissionDetailsVO chargeDetailsVO=new CommissionDetailsVO();

        chargeDetailsVO.setChargeName(chargeMasterVO.getChargeName());
        chargeDetailsVO.setValueType(chargeMasterVO.getValueType());
        chargeDetailsVO.setChargeValue(String.valueOf(partnerCommissionVO.getCommissionValue()));

        if(Charge_category.Success.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeMasterVO.getSubKeyword()))
        {
            vCntCounter=totalSaleCount;/*settledTransCount+reversedTransCount+chargebackTransCount;*/
            vDblAmount=totalSaleAmount;/*totalSettledTransAmount+totalReversedTransAmount+totalChargebackTransAmount;*/
            vDblTotal=(vDblAmount*partnerCommissionVO.getCommissionValue())/100;

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Success.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeMasterVO.getSubKeyword()))
        {
            vCntCounter=totalSaleCount;/*settledTransCount+reversedTransCount+chargebackTransCount;*/
            vDblAmount=totalSaleAmount;/*totalSettledTransAmount+totalReversedTransAmount+totalChargebackTransAmount;*/
            vDblTotal=vCntCounter*partnerCommissionVO.getCommissionValue();

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));

        }
        else if(Charge_category.Success.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Reversed.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeMasterVO.getSubKeyword()))
        {
            vCntCounter=reversedTransCount;
            vDblAmount=totalReversedTransAmount;
            vDblTotal=(vCntCounter*partnerCommissionVO.getCommissionValue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Success.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Chargeback.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeMasterVO.getSubKeyword()))
        {
            vCntCounter=chargebackTransCount;
            vDblAmount=totalChargebackTransAmount;
            vDblTotal=(vCntCounter*partnerCommissionVO.getCommissionValue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Failure.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeMasterVO.getSubKeyword()))
        {
            vCntCounter=authfailedTransCount;
            vDblAmount=totalAuthfailedTransAmount;
            vDblTotal=(vCntCounter*partnerCommissionVO.getCommissionValue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Others.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeMasterVO.getSubKeyword()))
        {
            vCntCounter=totalSaleCount+authfailedTransCount;
            vDblAmount=totalSaleAmount+totalAuthfailedTransAmount;
            vDblTotal=(vCntCounter*partnerCommissionVO.getCommissionValue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        return chargeDetailsVO;
    }

    public ChargeDetailsVO calculateAgentChargeValue(ChargeVO chargeVO,TerminalVO terminalVO,String sTableName,TransactionSummaryVO summaryVO,String memberFirstTransactionDate)
    {
        long settledTransCount = 0;
        long reversedTransCount = 0;
        long chargebackTransCount = 0;
        long authfailedTransCount = 0;
        long varifyOrderCount=0;
        long refundAlertCount=0;
        long retrivalRequestCount=0;

        double totalAuthfailedTransAmount=0.00;
        double totalSettledTransAmount=0.00;
        double totalReversedTransAmount = 0.00;
        double totalChargebackTransAmount = 0.00;

        authfailedTransCount=summaryVO.getCountOfAuthfailed();
        totalAuthfailedTransAmount=summaryVO.getAuthfailedAmount();

        settledTransCount=summaryVO.getCountOfSettled();
        totalSettledTransAmount=summaryVO.getSettledAmount();

        reversedTransCount=summaryVO.getCountOfReversed();
        totalReversedTransAmount=summaryVO.getReversedAmount();

        chargebackTransCount=summaryVO.getCountOfChargeback();
        totalChargebackTransAmount=summaryVO.getChargebackAmount();

        long vCntCounter=0;
        double vDblAmount=0.00;
        double vDblTotal=0.00;

        ChargeDetailsVO chargeDetailsVO=new ChargeDetailsVO();
        chargeDetailsVO.setChargeName(chargeVO.getChargename());
        if(Charge_unit.Percentage.toString().equals(chargeVO.getValuetype()))
        {
            chargeDetailsVO.setChargeValue(chargeVO.getAgentChargeValue()+"%");
        }
        else
            chargeDetailsVO.setChargeValue(chargeVO.getAgentChargeValue());

        if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=settledTransCount+reversedTransCount+chargebackTransCount;
            vDblAmount=totalSettledTransAmount+totalReversedTransAmount+totalChargebackTransAmount;
            vDblTotal=(vDblAmount*Double.valueOf(chargeVO.getAgentChargeValue()))/100;

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=settledTransCount+reversedTransCount+chargebackTransCount;
            vDblAmount=totalSettledTransAmount+totalReversedTransAmount+totalChargebackTransAmount;
            vDblTotal=vCntCounter*Double.valueOf(chargeVO.getAgentChargeValue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));

        }
        else if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Reversed.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=reversedTransCount;
            vDblAmount=totalReversedTransAmount;
            vDblTotal=(vCntCounter*Double.valueOf(chargeVO.getAgentChargeValue()));

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Reversed.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=reversedTransCount;
            vDblAmount=totalReversedTransAmount;
            vDblTotal=(vDblAmount*Double.valueOf(chargeVO.getAgentChargeValue()))/100;

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Chargeback.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=chargebackTransCount;
            vDblAmount=totalChargebackTransAmount;
            vDblTotal=(vCntCounter*Double.valueOf(chargeVO.getAgentChargeValue()));

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Failure.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=authfailedTransCount;
            vDblAmount=totalAuthfailedTransAmount;
            vDblTotal=(vCntCounter*Double.valueOf(chargeVO.getAgentChargeValue()));

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Failure.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=authfailedTransCount;
            vDblAmount=totalAuthfailedTransAmount;
            vDblTotal=(vDblAmount*Double.valueOf(chargeVO.getAgentChargeValue()))/100;

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Others.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter= authfailedTransCount + settledTransCount + reversedTransCount + chargebackTransCount;
            vDblAmount=totalAuthfailedTransAmount+totalReversedTransAmount+totalChargebackTransAmount+totalSettledTransAmount;
            vDblTotal=(vCntCounter*Double.valueOf(chargeVO.getAgentChargeValue()));

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Others.toString().equals(chargeVO.getCategory()) && Charge_keyword.VerifyOrder.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            long memberVerifyOrderAlertCount=payoutDAO.getVerifyOrderCount(terminalVO,new SettlementDateVO());
            vCntCounter=memberVerifyOrderAlertCount;
            varifyOrderCount=varifyOrderCount+vCntCounter;
            vDblTotal=vCntCounter*Double.valueOf(chargeVO.getAgentChargeValue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Others.toString().equals(chargeVO.getCategory()) && Charge_keyword.RefundAlert.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            long memberRefundAlertCount=payoutDAO.getRefundAlertCount(terminalVO,new SettlementDateVO());
            vCntCounter=memberRefundAlertCount;
            refundAlertCount=refundAlertCount+vCntCounter;
            vDblTotal=vCntCounter*Double.valueOf(chargeVO.getAgentChargeValue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Others.toString().equals(chargeVO.getCategory()) && Charge_keyword.RetrivalRequest.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            long allRetrivalRequestCount=payoutDAO.getRetrivalRequestCount(terminalVO,sTableName);
            vCntCounter=allRetrivalRequestCount;
            retrivalRequestCount=retrivalRequestCount+vCntCounter;
            vDblTotal=vCntCounter*Double.valueOf(chargeVO.getAgentChargeValue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(vDblTotal);
        }
        else if(Charge_category.Others.toString().equals(chargeVO.getCategory()) && Charge_keyword.Setup.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            SetupChargeVO setupChargeVO=null;
            setupChargeVO=applySetupChargeForAgentSummary(chargeVO,terminalVO,memberFirstTransactionDate);
            vCntCounter=setupChargeVO.getCount();
            vDblTotal=setupChargeVO.getTotal();

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        return chargeDetailsVO;
    }

    public ChargeDetailsVO calculateMerchantChargeValue(ChargeVO chargeVO, TerminalVO terminalVO, TransactionSummaryVO summaryVO, String sTableName, String reportType) throws ParseException
    {
        long vCntSettled = 0;
        long vCntReversed = 0;
        long vCntChargeback = 0;
        long vCntAuthfailed = 0;

        double vDblAuthfailedAmount=0.0;
        double vDblSettledAmount=0.0;
        double vDblReversedAmount = 0.0;
        double vDblChargebackAmount = 0.0;

        long vCntCounter=0;
        double vDblAmount=0.0;
        double vDblTotal=0.0;

        vCntAuthfailed=summaryVO.getCountOfAuthfailed();
        vDblAuthfailedAmount=summaryVO.getAuthfailedAmount();

        vCntSettled=summaryVO.getCountOfSettled();
        vDblSettledAmount=summaryVO.getSettledAmount();

        vCntReversed=summaryVO.getCountOfReversed();
        vDblReversedAmount=summaryVO.getReversedAmount();

        vCntChargeback=summaryVO.getCountOfChargeback();
        vDblChargebackAmount=summaryVO.getChargebackAmount();

        ChargeDetailsVO chargeDetailsVO=null;

        if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=vCntSettled+vCntReversed+vCntChargeback;
            vDblAmount=vDblSettledAmount+vDblReversedAmount+vDblChargebackAmount;
            vDblTotal=(vDblAmount*Double.valueOf(chargeVO.getChargevalue()))/100;

            chargeDetailsVO=new ChargeDetailsVO();
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            if(Charge_unit.Percentage.toString().equals(chargeVO.getValuetype()))
            {
                chargeDetailsVO.setChargeValue(chargeVO.getChargevalue()+"%");
            }
            else
                chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=vCntSettled+vCntReversed+vCntChargeback;
            vDblAmount=vDblSettledAmount+vDblReversedAmount+vDblChargebackAmount;
            vDblTotal=vCntCounter*Double.valueOf(chargeVO.getChargevalue());

            chargeDetailsVO=new ChargeDetailsVO();
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            if(Charge_unit.Percentage.toString().equals(chargeVO.getValuetype()))
            {
                chargeDetailsVO.setChargeValue(chargeVO.getChargevalue()+"%");
            }
            else
                chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Reversed.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=vCntReversed;
            vDblAmount=vDblReversedAmount;
            vDblTotal=(vCntCounter*Double.valueOf(chargeVO.getChargevalue()));

            chargeDetailsVO=new ChargeDetailsVO();
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            if(Charge_unit.Percentage.toString().equals(chargeVO.getValuetype()))
            {
                chargeDetailsVO.setChargeValue(chargeVO.getChargevalue()+"%");
            }
            else
                chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));


        }
        else if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Reversed.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=vCntReversed;
            vDblAmount=vDblReversedAmount;
            vDblTotal=(vDblAmount*Double.valueOf(chargeVO.getChargevalue()))/100;

            chargeDetailsVO=new ChargeDetailsVO();
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            if(Charge_unit.Percentage.toString().equals(chargeVO.getValuetype()))
            {
                chargeDetailsVO.setChargeValue(chargeVO.getChargevalue()+"%");
            }
            else
                chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));

        }
        else if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Chargeback.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=vCntChargeback;
            vDblAmount=vDblChargebackAmount;
            vDblTotal=(vCntCounter*Double.valueOf(chargeVO.getChargevalue()));

            chargeDetailsVO=new ChargeDetailsVO();
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            if(Charge_unit.Percentage.toString().equals(chargeVO.getValuetype()))
            {
                chargeDetailsVO.setChargeValue(chargeVO.getChargevalue()+"%");
            }
            else
                chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));

        }
        else if(Charge_category.Failure.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=vCntAuthfailed;
            vDblAmount=vDblAuthfailedAmount;
            vDblTotal=(vCntCounter*Double.valueOf(chargeVO.getChargevalue()));

            chargeDetailsVO=new ChargeDetailsVO();
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            if(Charge_unit.Percentage.toString().equals(chargeVO.getValuetype()))
            {
                chargeDetailsVO.setChargeValue(chargeVO.getChargevalue()+"%");
            }
            else
                chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Failure.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=vCntAuthfailed;
            vDblAmount=vDblAuthfailedAmount;
            vDblTotal=(vDblAmount*Double.valueOf(chargeVO.getChargevalue()))/100;

            chargeDetailsVO=new ChargeDetailsVO();
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            if(Charge_unit.Percentage.toString().equals(chargeVO.getValuetype()))
            {
                chargeDetailsVO.setChargeValue(chargeVO.getChargevalue()+"%");
            }
            else
                chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Others.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter= vCntAuthfailed + vCntSettled + vCntReversed + vCntChargeback;
            vDblAmount=vDblAuthfailedAmount+vDblSettledAmount+vDblReversedAmount+vDblChargebackAmount;
            vDblTotal=(vCntCounter*Double.valueOf(chargeVO.getChargevalue()));

            chargeDetailsVO=new ChargeDetailsVO();
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            if(Charge_unit.Percentage.toString().equals(chargeVO.getValuetype()))
            {
                chargeDetailsVO.setChargeValue(chargeVO.getChargevalue()+"%");
            }
            else
                chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));

        }
        else if(Charge_category.Others.toString().equals(chargeVO.getCategory()) && Charge_keyword.VerifyOrder.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            long memberVerifyOrderAlertCount=payoutDAO.getVerifyOrderCount(terminalVO,new SettlementDateVO());
            long verifyOrderAlertPaidCount=payoutDAO.getPaidVerifyOrderCount(terminalVO);
            if("wirereport".equals(reportType))
            {
                vCntCounter=memberVerifyOrderAlertCount-verifyOrderAlertPaidCount;
            }
            else
            {
                vCntCounter=memberVerifyOrderAlertCount;
            }

            vDblTotal=vCntCounter*Double.valueOf(chargeVO.getChargevalue());

            chargeDetailsVO=new ChargeDetailsVO();
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            if(Charge_unit.Percentage.toString().equals(chargeVO.getValuetype()))
            {
                chargeDetailsVO.setChargeValue(chargeVO.getChargevalue()+"%");
            }
            else
                chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Others.toString().equals(chargeVO.getCategory()) && Charge_keyword.RefundAlert.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            long memberRefundAlertCount=payoutDAO.getRefundAlertCount(terminalVO,new SettlementDateVO());
            long refundAlertPaidCount=payoutDAO.getPaidRefundAlertCount(terminalVO);
            if("wirereport".equals(reportType))
            {
                vCntCounter=memberRefundAlertCount-refundAlertPaidCount;
            }
            else
            {
                vCntCounter=memberRefundAlertCount;
            }

            vDblTotal=vCntCounter*Double.valueOf(chargeVO.getChargevalue());

            chargeDetailsVO=new ChargeDetailsVO();
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            if(Charge_unit.Percentage.toString().equals(chargeVO.getValuetype()))
            {
                chargeDetailsVO.setChargeValue(chargeVO.getChargevalue()+"%");
            }
            else
                chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));


        }
        else if(Charge_category.Others.toString().equals(chargeVO.getCategory()) && Charge_keyword.RetrivalRequest.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            long allRetrivalRequestCount=payoutDAO.getRetrivalRequestCount(terminalVO,sTableName);
            long paidRetrivalRequestCount=payoutDAO.getPaidRetrivalRequestCount(terminalVO);
            if("wirereport".equals(reportType))
            {
                vCntCounter=allRetrivalRequestCount-paidRetrivalRequestCount;
            }
            else
            {
                vCntCounter=allRetrivalRequestCount;
            }

            vDblTotal=vCntCounter*Double.valueOf(chargeVO.getChargevalue());

            chargeDetailsVO=new ChargeDetailsVO();
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            if(Charge_unit.Percentage.toString().equals(chargeVO.getValuetype()))
            {
                chargeDetailsVO.setChargeValue(chargeVO.getChargevalue()+"%");
            }
            else
                chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Others.toString().equals(chargeVO.getCategory()) && Charge_keyword.Setup.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            SetupChargeVO setupChargeVO=null;
            if("wirereport".equals(reportType))
            {
                setupChargeVO=applySetupChargeForWireReport(chargeVO,terminalVO);
            }
            else
            {
                setupChargeVO=applySetupChargeForSummary(chargeVO,terminalVO);
            }

            vCntCounter=setupChargeVO.getCount();
            vDblTotal=setupChargeVO.getTotal();

            chargeDetailsVO=new ChargeDetailsVO();
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            if(Charge_unit.Percentage.toString().equals(chargeVO.getValuetype()))
            {
                chargeDetailsVO.setChargeValue(chargeVO.getChargevalue()+"%");
            }
            else
                chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        return chargeDetailsVO;
    }

    public SettlementDateVO getSettlementDateOnTerminal(TerminalVO terminalVO,String tableName)
    {
        return payoutDAO.getSettlementDateOnTerminal(terminalVO,tableName);
    }

    public SettlementDateVO getSettlementDateOnAccount(String memberId,String accountId,String tableName)
    {
        return payoutDAO.getSettlementDateOnAccount(memberId,accountId,tableName);
    }

    public SettlementDateVO getSettlementDateOnGatewayAccount(GatewayAccount gatewayAccount,String tableName)
    {
        return payoutDAO.getSettlementDateOnGatewayAccount(gatewayAccount, tableName);
    }

    public SettlementDateVO getWireReportSettlementDateVO(TerminalVO terminalVO,String tableName)throws Exception
    {
        return payoutDAO.getWireReportSettlementDateVO(terminalVO,tableName);
    }

    public String createMerchantWire(MerchantWireVO merchantWireVO)
    {
        return payoutDAO.createMerchantWire(merchantWireVO);
    }

    public String createAgentWire(AgentWireVO agentWireVO) throws Exception
    {
        return payoutDAO.createAgentWire(agentWireVO);
    }

    public String createBankAgentWire(BankAgentWireVO bankAgentWireVO) throws Exception
    {
        return payoutDAO.createBankAgentWire(bankAgentWireVO);
    }

    public String createBankPartnerWire(BankPartnerWireVO bankPartnerWireVO) throws Exception
    {
        return payoutDAO.createBankPartnerWire(bankPartnerWireVO);
    }

    public String createPartnerWire(PartnerWireVO partnerWireVO) throws SQLException, SystemError
    {
        return payoutDAO.createPartnerWire(partnerWireVO);
    }

    public String createMerchantReportFile(MerchantPayoutReportVO merchantPayoutReportVO)
    {
        MerchantDetailsVO merchantDetailsVO=merchantPayoutReportVO.getMerchantDetailsVO();
        TerminalVO terminalVO=merchantPayoutReportVO.getTerminalVO();
        SettlementDateVO settlementDateVO=merchantPayoutReportVO.getSettlementDateVO();
        RollingReserveDateVO rollingReserveDateVO=merchantPayoutReportVO.getRollingReserveDateVO();
        List<ChargeDetailsVO> chargeDetailsVOList=merchantPayoutReportVO.getChargesDetailsVOsList();
        List<ReserveGeneratedVO> reserveGeneratedVOList=merchantPayoutReportVO.getReserveGeneratedVOList();
        List<ReserveRefundVO> reserveRefundVOList=merchantPayoutReportVO.getReserveRefundVOsList();
        List<GrossChargeVO> grossChargeVOList=merchantPayoutReportVO.getGrossTypeChargeVOList();
        WireChargeVO wireChargeVO=merchantPayoutReportVO.getWireChargeVO();

        Document document = new Document(PageSize.A3, 50, 50, 50, 50);
        String reportFileName=null;
        try
        {


            PayoutReportUtils  payoutReportUtils=new PayoutReportUtils();

            reportFileName=payoutReportUtils.getMerchantReportFileName(merchantDetailsVO.getMemberId(), terminalVO.getAccountId(), settlementDateVO.getSettlementStartDate(), settlementDateVO.getSettlementEndDate());
            reportFileName=reportFileName+".pdf";

            File filepath=new File(PAYOUT_REPORT_FILE_PATH+reportFileName);

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filepath));
            document.open();

            Table headerTable = new Table(5);
            headerTable.setWidth(100);
            headerTable.setPadding(1);

            Cell partnerNameCaptionCell=new Cell("Processor:");
            partnerNameCaptionCell.setColspan(1);
            headerTable.addCell(partnerNameCaptionCell);

            Cell partnerNameValueCell=new Cell(merchantDetailsVO.getPartnerName());
            partnerNameValueCell.setColspan(1);
            headerTable.addCell(partnerNameValueCell);

            Image image = Image.getInstance(PARTNER_LOGO_PATH+merchantDetailsVO.getLogoName());
            Cell partnerLogoCell=new Cell(image);
            partnerLogoCell.setColspan(3);
            partnerLogoCell.setRowspan(2);
            partnerLogoCell.setWidth(Element.JPEG2000);
            partnerLogoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            partnerLogoCell.setVerticalAlignment(Element.ALIGN_TOP);
            headerTable.addCell(partnerLogoCell);

            SimpleDateFormat dateFormater = new SimpleDateFormat("dd-MM-yyyy");
            String reportingDate=dateFormater.format(new Date());

            Cell reportingDateCaptionCell=new Cell("Reporting Date:");
            reportingDateCaptionCell.setColspan(1);
            headerTable.addCell(reportingDateCaptionCell);

            Cell reportingDateValueCell=new Cell(reportingDate);
            reportingDateValueCell.setColspan(1);
            headerTable.addCell(reportingDateValueCell);

            document.add(headerTable);

            // demonstrate some table features
            Table table = new Table(5);
            table.setWidth(100);

            /*table.setBorderWidth(1);*/
            table.setBorderColor(new Color(0, 0,0));
            table.setPadding(1);
            /*table.setSpacing(1);*/


            Cell sMemberDetailsHeaderCell=new Cell("Member Details");
            sMemberDetailsHeaderCell.setColspan(5);
            sMemberDetailsHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            sMemberDetailsHeaderCell.setVerticalAlignment(Element.ALIGN_CENTER);

            sMemberDetailsHeaderCell.setBackgroundColor(Color.gray.brighter());
            table.addCell(sMemberDetailsHeaderCell);

            Cell sMemberIdCaptionCell=new Cell("Member ID:");
            Cell sMemberIdValueCell=new Cell(merchantDetailsVO.getMemberId());
            sMemberIdCaptionCell.setColspan(3);
            sMemberIdValueCell.setColspan(2);
            table.addCell(sMemberIdCaptionCell);
            table.addCell(sMemberIdValueCell);

            Cell sTerminalIdCaptionCell=new Cell("Terminal ID:");
            Cell sTerminalIdValueCell=new Cell(terminalVO.getTerminalId());
            sTerminalIdCaptionCell.setColspan(3);
            sTerminalIdValueCell.setColspan(2);
            table.addCell(sTerminalIdCaptionCell);
            table.addCell(sTerminalIdValueCell);

            Cell sCompanyNameCaptionCell=new Cell("Company Name:");
            Cell sCompanyNameValueCell=new Cell(merchantDetailsVO.getCompany_name());
            sCompanyNameCaptionCell.setColspan(3);
            sCompanyNameValueCell.setColspan(2);
            table.addCell(sCompanyNameCaptionCell);
            table.addCell(sCompanyNameValueCell);

            Cell sContactPersonCaptionCell=new Cell("Contact Person:");
            Cell sContactPersonValueCell=new Cell(merchantDetailsVO.getContact_persons());
            sContactPersonCaptionCell.setColspan(3);
            sContactPersonValueCell.setColspan(2);
            table.addCell(sContactPersonCaptionCell);
            table.addCell(sContactPersonValueCell);

            Cell sPayModeCaptionCell=new Cell("Payment Mode:");
            Cell sPayModeValueCell=new Cell(terminalVO.getPaymentTypeName());
            sPayModeCaptionCell.setColspan(3);
            sPayModeValueCell.setColspan(2);
            table.addCell(sPayModeCaptionCell);
            table.addCell(sPayModeValueCell);

            Cell sCardTypeCaptionCell=new Cell("Card Type:");
            Cell sCardTypeValueCell=new Cell(terminalVO.getCardType());
            sCardTypeCaptionCell.setColspan(3);
            sCardTypeValueCell.setColspan(2);
            table.addCell(sCardTypeCaptionCell);
            table.addCell(sCardTypeValueCell);

            Cell sCurrencyCaptionCell=new Cell("Currency:");
            Cell sCurrencyValueCell=new Cell(merchantPayoutReportVO.getCurrency());
            sCurrencyCaptionCell.setColspan(3);
            sCurrencyValueCell.setColspan(2);
            table.addCell(sCurrencyCaptionCell);
            table.addCell(sCurrencyValueCell);

            Cell settleCoveredDateCaptionCell=new Cell("Settle Covered Upto:");
            Cell settleCoveredDateCaptionValue=new Cell(settlementDateVO.getSettlementEndDate());
            settleCoveredDateCaptionCell.setColspan(3);
            settleCoveredDateCaptionValue.setColspan(2);
            table.addCell(settleCoveredDateCaptionCell);
            table.addCell(settleCoveredDateCaptionValue);

            Cell declinedCoveredDateCaptionCell=new Cell("Decline Covered Upto:");
            Cell declinedCoveredDateCaptionValue=new Cell(settlementDateVO.getDeclinedEndDate());
            declinedCoveredDateCaptionCell.setColspan(3);
            declinedCoveredDateCaptionValue.setColspan(2);
            table.addCell(declinedCoveredDateCaptionCell);
            table.addCell(declinedCoveredDateCaptionValue);

            Cell reversedCoveredDateCaptionCell=new Cell("Reversal Covered Upto:");
            Cell reversedCoveredDateCaptionValue=new Cell(settlementDateVO.getReversedEndDate());
            reversedCoveredDateCaptionCell.setColspan(3);
            reversedCoveredDateCaptionValue.setColspan(2);
            table.addCell(reversedCoveredDateCaptionCell);
            table.addCell(reversedCoveredDateCaptionValue);

            Cell chargebackCoveredDateCaptionCell=new Cell("Chargeback Covered Upto:");
            Cell chargebackCoveredDateCaptionValue=new Cell(settlementDateVO.getChargebackEndDate());
            chargebackCoveredDateCaptionCell.setColspan(3);
            chargebackCoveredDateCaptionValue.setColspan(2);
            table.addCell(chargebackCoveredDateCaptionCell);
            table.addCell(chargebackCoveredDateCaptionValue);

            Cell sRollingReserveReleaseUptoDateCaptionCell=new Cell("Rolling Reserve Release Date Upto:");
            Cell sRollingReserveReleaseUptoDateValueCell=new Cell(rollingReserveDateVO.getRollingReserveEndDate());
            sRollingReserveReleaseUptoDateCaptionCell.setColspan(3);
            sRollingReserveReleaseUptoDateValueCell.setColspan(2);
            table.addCell(sRollingReserveReleaseUptoDateCaptionCell);
            table.addCell(sRollingReserveReleaseUptoDateValueCell);

            Cell sChargesDetailsHeaderCell=new Cell("Charges Details");
            sChargesDetailsHeaderCell.setColspan(5);
            sChargesDetailsHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            sChargesDetailsHeaderCell.setBackgroundColor(Color.gray.brighter());
            table.addCell(sChargesDetailsHeaderCell);

            Cell sChargeNameHeader = new Cell("Charge Name");
            Cell sChargeRateHeader = new Cell("Rate/Fee");
            Cell sChargeCounterHeader = new Cell("Counter");
            Cell sChargeAmountHeader = new Cell("Amount");
            Cell sChargeTotalHeader = new Cell("Total");

            sChargeNameHeader.setBackgroundColor(Color.gray.brighter());
            sChargeRateHeader.setBackgroundColor(Color.gray.brighter());
            sChargeCounterHeader.setBackgroundColor(Color.gray.brighter());
            sChargeAmountHeader.setBackgroundColor(Color.gray.brighter());
            sChargeTotalHeader.setBackgroundColor(Color.gray.brighter());

            sChargeNameHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            sChargeRateHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            sChargeCounterHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            sChargeAmountHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            sChargeTotalHeader.setHorizontalAlignment(Element.ALIGN_CENTER);

            table.addCell(sChargeNameHeader);
            table.addCell(sChargeRateHeader);
            table.addCell(sChargeCounterHeader);
            table.addCell(sChargeAmountHeader);
            table.addCell(sChargeTotalHeader);

            Cell sChargeNameValue,sChargeRateValue,sChargeCounterValue,sChargeAmountValue,sChargeTotalValue;

            for(ChargeDetailsVO chargeDetailsVO:chargeDetailsVOList)
            {
                sChargeNameValue = new Cell(chargeDetailsVO.getChargeName());
                sChargeRateValue = new Cell(chargeDetailsVO.getChargeValue());
                sChargeCounterValue = new Cell(String.valueOf(chargeDetailsVO.getCount()));
                sChargeAmountValue = new Cell(Functions.round(chargeDetailsVO.getAmount(),2));
                sChargeTotalValue = new Cell(Functions.round(chargeDetailsVO.getTotal(),2));

                sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                table.addCell(sChargeNameValue);
                table.addCell(sChargeRateValue);
                table.addCell(sChargeCounterValue);
                table.addCell(sChargeAmountValue);
                table.addCell(sChargeTotalValue);
            }

            Cell sChargeFeeTotalCaption=new Cell("Total");
            Cell sChargeFeeTotalValue=new Cell(Functions.round(merchantPayoutReportVO.getMerchantTotalChargesAmount(),2));
            sChargeFeeTotalCaption.setColspan(4);
            sChargeFeeTotalCaption.setHeader(true);
            sChargeFeeTotalCaption.setHorizontalAlignment(Element.ALIGN_RIGHT);
            sChargeFeeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
            sChargeFeeTotalValue.setBackgroundColor(Color.gray.brighter());

            table.addCell(sChargeFeeTotalCaption);
            table.addCell(sChargeFeeTotalValue);

            Cell sGeneratedReserveDetailsHeaderCell=new Cell("Generated Reserve Details");
            sGeneratedReserveDetailsHeaderCell.setColspan(5);
            sGeneratedReserveDetailsHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            sGeneratedReserveDetailsHeaderCell.setBackgroundColor(Color.gray.brighter());
            table.addCell(sGeneratedReserveDetailsHeaderCell);

            for(ReserveGeneratedVO reserveGeneratedVO:reserveGeneratedVOList)
            {

                sChargeNameValue = new Cell(reserveGeneratedVO.getChargeName());
                sChargeRateValue = new Cell(reserveGeneratedVO.getChargeValue());
                sChargeCounterValue = new Cell("-");
                sChargeAmountValue = new Cell(Functions.round(reserveGeneratedVO.getAmount(),2));
                sChargeTotalValue = new Cell(Functions.round(reserveGeneratedVO.getTotal(),2));

                sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                table.addCell(sChargeNameValue);
                table.addCell(sChargeRateValue);
                table.addCell(sChargeCounterValue);
                table.addCell(sChargeAmountValue);
                table.addCell(sChargeTotalValue);

            }

            Cell sReserveGeneratedTotalCaption=new Cell("Total");
            Cell sReserveGeneratedTotalValue=new Cell(Functions.round(merchantPayoutReportVO.getMerchantRollingReserveAmount(),2));
            sReserveGeneratedTotalCaption.setColspan(4);
            sReserveGeneratedTotalCaption.setHeader(true);
            sReserveGeneratedTotalCaption.setHorizontalAlignment(Element.ALIGN_RIGHT);
            sReserveGeneratedTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
            sReserveGeneratedTotalValue.setBackgroundColor(Color.gray.brighter());

            table.addCell(sReserveGeneratedTotalCaption);
            table.addCell(sReserveGeneratedTotalValue);

            Cell sRefundedReserveDetailsHeaderCell=new Cell("Refunded Reserve Details");
            sRefundedReserveDetailsHeaderCell.setColspan(5);
            sRefundedReserveDetailsHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            sRefundedReserveDetailsHeaderCell.setBackgroundColor(Color.gray.brighter());
            table.addCell(sRefundedReserveDetailsHeaderCell);

            for(ReserveRefundVO reserveRefundVO:reserveRefundVOList)
            {

                sChargeNameValue = new Cell(reserveRefundVO.getChargeName());
                sChargeRateValue = new Cell(reserveRefundVO.getChargeValue());
                sChargeCounterValue = new Cell("-");
                sChargeAmountValue = new Cell(Functions.round(reserveRefundVO.getAmount(),2));
                sChargeTotalValue = new Cell(Functions.round(reserveRefundVO.getTotal(),2));

                sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                table.addCell(sChargeNameValue);
                table.addCell(sChargeRateValue);
                table.addCell(sChargeCounterValue);
                table.addCell(sChargeAmountValue);
                table.addCell(sChargeTotalValue);
            }

            Cell sReserveRefundedTotalCaption=new Cell("Total");
            Cell sReserveRefundedTotalValue=new Cell(Functions.round(merchantPayoutReportVO.getMerchantRollingReleasedAmount(),2));

            sReserveRefundedTotalCaption.setColspan(4);
            sReserveRefundedTotalCaption.setHeader(true);
            sReserveRefundedTotalCaption.setHorizontalAlignment(Element.ALIGN_RIGHT);
            sReserveRefundedTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

            sReserveRefundedTotalValue.setBackgroundColor(Color.gray.brighter());

            table.addCell(sReserveRefundedTotalCaption);
            table.addCell(sReserveRefundedTotalValue);

            Cell sPayoutReportSummaryHeaderCell=new Cell("Summary");
            sPayoutReportSummaryHeaderCell.setColspan(5);
            sPayoutReportSummaryHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            sPayoutReportSummaryHeaderCell.setBackgroundColor(Color.gray.brighter());
            table.addCell(sPayoutReportSummaryHeaderCell);

            Cell sTotalProcessingAmountCaptionCell=new Cell("Total Processing Amount:");
            Cell sTotalProcessingAmountValueCell=new Cell(Functions.round(merchantPayoutReportVO.getMerchantTotalProcessingAmount(),2));
            sTotalProcessingAmountCaptionCell.setColspan(4);
            sTotalProcessingAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sTotalProcessingAmountCaptionCell);
            table.addCell(sTotalProcessingAmountValueCell);

            Cell sTotalFeesCaptionCell=new Cell("Total Fees:");

            Cell sTotalFeesValueCell=new Cell(isValidAmount(Functions.round(merchantPayoutReportVO.getMerchantTotalChargesAmount(),2)));
            sTotalFeesCaptionCell.setColspan(4);
            sTotalFeesValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sTotalFeesCaptionCell);
            table.addCell(sTotalFeesValueCell);

            Cell sTotalChargebackReversalCaptionCell=new Cell("Total Chargeback/Reversal:");
            Cell sTotalChargebackReversalValueCell=new Cell(isValidAmount(Functions.round(merchantPayoutReportVO.getMerchantTotalReversedAmount()+merchantPayoutReportVO.getMerchantTotalChargebackAmount(),2)));
            sTotalChargebackReversalCaptionCell.setColspan(4);
            sTotalChargebackReversalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(sTotalChargebackReversalCaptionCell);
            table.addCell(sTotalChargebackReversalValueCell);

            Cell sGeneratedReserveCaptionCell=new Cell("Generated Reserve:");
            Cell sGeneratedReserveValueCell=new Cell(isValidAmount(Functions.round(merchantPayoutReportVO.getMerchantRollingReserveAmount(),2)));
            sGeneratedReserveCaptionCell.setColspan(4);
            sGeneratedReserveValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sGeneratedReserveCaptionCell);
            table.addCell(sGeneratedReserveValueCell);

            Cell sGrossAmountCaptionCell=new Cell("Gross Amount:");
            Cell sGrossAmountValueCell=new Cell(isValidAmount(Functions.round(merchantPayoutReportVO.getMerchantTotalBalanceAmount(),2)));
            sGrossAmountCaptionCell.setColspan(4);
            sGrossAmountCaptionCell.setBackgroundColor(Color.gray.brighter());
            sGrossAmountValueCell.setBackgroundColor(Color.gray.brighter());
            sGrossAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sGrossAmountCaptionCell);
            table.addCell(sGrossAmountValueCell);

            if(wireChargeVO!=null)
            {
                sChargeNameValue = new Cell(wireChargeVO.getChargeName());
                sChargeRateValue = new Cell(wireChargeVO.getChargeValue());
                sChargeCounterValue = new Cell(String.valueOf(wireChargeVO.getCount()));
                sChargeAmountValue = new Cell("-");
                sChargeTotalValue = new Cell(isValidAmount(Functions.round(wireChargeVO.getTotal(),2)));

                sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                table.addCell(sChargeNameValue);
                table.addCell(sChargeRateValue);
                table.addCell(sChargeCounterValue);
                table.addCell(sChargeAmountValue);
                table.addCell(sChargeTotalValue);
            }


            for(GrossChargeVO grossChargeVO:grossChargeVOList)
            {

                sChargeNameValue = new Cell(grossChargeVO.getChargeName());
                sChargeRateValue = new Cell(grossChargeVO.getChargeValue());
                sChargeCounterValue = new Cell("-");
                sChargeAmountValue = new Cell(Functions.round(grossChargeVO.getAmount(),2));
                sChargeTotalValue = new Cell(Functions.round(grossChargeVO.getTotal(),2));

                sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                table.addCell(sChargeNameValue);
                table.addCell(sChargeRateValue);
                table.addCell(sChargeCounterValue);
                table.addCell(sChargeAmountValue);
                table.addCell(sChargeTotalValue);

            }

            Cell sPaidAmountCaptionCell=new Cell("Paid Amount:");
            Cell sPaidAmountValueCell=new Cell(isValidAmount(Functions.round(merchantPayoutReportVO.getMerchantWirePaidAmount(),2)));
            sPaidAmountCaptionCell.setColspan(4);
            sPaidAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sPaidAmountCaptionCell);
            table.addCell(sPaidAmountValueCell);

            Cell sPreviousBalanceAmountCaptionCell=new Cell("Previous Balance Amount:");
            Cell sPreviousBalanceAmountValueCell=new Cell(Functions.round(merchantPayoutReportVO.getMerchantWireUnpaidAmount(),2));
            sPreviousBalanceAmountCaptionCell.setColspan(4);
            sPreviousBalanceAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sPreviousBalanceAmountCaptionCell);
            table.addCell(sPreviousBalanceAmountValueCell);

            Cell sRefundedRollingReserveCaptionCell=new Cell("Refunded Rolling Reserve:");
            Cell sRefundedRollingReserveValueCell=new Cell(Functions.round(merchantPayoutReportVO.getMerchantRollingReleasedAmount(),2));
            sRefundedRollingReserveCaptionCell.setColspan(4);
            sRefundedRollingReserveValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sRefundedRollingReserveCaptionCell);
            table.addCell(sRefundedRollingReserveValueCell);

            Cell sTotalAmountFundedCaptionCell=new Cell("Total Amount Funded:");
            Cell sTotalAmountFundedValueCell=new Cell(Functions.round(merchantPayoutReportVO.getMerchantTotalFundedAmount(),2));
            sTotalAmountFundedCaptionCell.setColspan(4);
            sTotalAmountFundedValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            sTotalAmountFundedCaptionCell.setBackgroundColor(Color.gray.brighter());
            sTotalAmountFundedValueCell.setBackgroundColor(Color.gray.brighter());
            table.addCell(sTotalAmountFundedCaptionCell);
            table.addCell(sTotalAmountFundedValueCell);

            document.add(table);

            Image poweredByLogo = Image.getInstance(PARTNER_LOGO_PATH+"logo2.jpg");
            poweredByLogo.setAlignment(Element.ALIGN_RIGHT);
            poweredByLogo.setWidthPercentage(20);

            document.add(poweredByLogo);
            document.close();

        }
        catch (Exception ex)
        {
            logger.error(ex);
        }
        return reportFileName;
    }

    public String isValidAmount(String isValidAmount)
    {
        if(Double.parseDouble(isValidAmount)>0)
        {
            isValidAmount="-"+isValidAmount;
        }

        return isValidAmount;
    }

    public ChargeDetailsVO calculateGatewayAccountChargesForAgent(ChargeVO chargeVO,TransactionSummaryVO summaryVO)
    {
        long vCntSettled = 0;
        long vCntReversed = 0;
        long vCntChargeback = 0;
        long vCntAuthfailed = 0;

        double vDblAuthfailedAmount=0.0;
        double vDblSettledAmount=0.0;
        double vDblReversedAmount = 0.0;
        double vDblChargebackAmount = 0.0;

        long vCntCounter=0;
        double vDblAmount=0.0;
        double vDblTotal=0.0;

        vCntAuthfailed=summaryVO.getCountOfAuthfailed();
        vDblAuthfailedAmount=summaryVO.getAuthfailedAmount();

        vCntSettled=summaryVO.getCountOfSettled();
        vDblSettledAmount=summaryVO.getSettledAmount();

        vCntReversed=summaryVO.getCountOfReversed();
        vDblReversedAmount=summaryVO.getReversedAmount();

        vCntChargeback=summaryVO.getCountOfChargeback();
        vDblChargebackAmount=summaryVO.getChargebackAmount();

        ChargeDetailsVO chargeDetailsVO=null;

        if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=vCntSettled+vCntReversed+vCntChargeback;
            vDblAmount=vDblSettledAmount+vDblReversedAmount+vDblChargebackAmount;
            vDblTotal=(vDblAmount*Double.valueOf(chargeVO.getAgentChargeValue()))/100;

            chargeDetailsVO=new ChargeDetailsVO();
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            if(Charge_unit.Percentage.toString().equals(chargeVO.getValuetype()))
            {
                chargeDetailsVO.setChargeValue(chargeVO.getAgentChargeValue()+"%");
            }
            else
                chargeDetailsVO.setChargeValue(chargeVO.getAgentChargeValue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=vCntSettled+vCntReversed+vCntChargeback;
            vDblAmount=vDblSettledAmount+vDblReversedAmount+vDblChargebackAmount;
            vDblTotal=vCntCounter*Double.valueOf(chargeVO.getAgentChargeValue());

            chargeDetailsVO=new ChargeDetailsVO();
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            if(Charge_unit.Percentage.toString().equals(chargeVO.getValuetype()))
            {
                chargeDetailsVO.setChargeValue(chargeVO.getAgentChargeValue()+"%");
            }
            else
                chargeDetailsVO.setChargeValue(chargeVO.getAgentChargeValue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Reversed.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=vCntReversed;
            vDblAmount=vDblReversedAmount;
            vDblTotal=(vCntCounter*Double.valueOf(chargeVO.getAgentChargeValue()));

            chargeDetailsVO=new ChargeDetailsVO();
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            if(Charge_unit.Percentage.toString().equals(chargeVO.getValuetype()))
            {
                chargeDetailsVO.setChargeValue(chargeVO.getAgentChargeValue()+"%");
            }
            else
                chargeDetailsVO.setChargeValue(chargeVO.getAgentChargeValue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));


        }
        else if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Reversed.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=vCntReversed;
            vDblAmount=vDblReversedAmount;
            vDblTotal=(vDblAmount*Double.valueOf(chargeVO.getAgentChargeValue()))/100;

            chargeDetailsVO=new ChargeDetailsVO();
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            if(Charge_unit.Percentage.toString().equals(chargeVO.getValuetype()))
            {
                chargeDetailsVO.setChargeValue(chargeVO.getAgentChargeValue()+"%");
            }
            else
                chargeDetailsVO.setChargeValue(chargeVO.getAgentChargeValue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));

        }
        else if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Chargeback.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=vCntChargeback;
            vDblAmount=vDblChargebackAmount;
            vDblTotal=(vCntCounter*Double.valueOf(chargeVO.getAgentChargeValue()));

            chargeDetailsVO=new ChargeDetailsVO();
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            if(Charge_unit.Percentage.toString().equals(chargeVO.getValuetype()))
            {
                chargeDetailsVO.setChargeValue(chargeVO.getAgentChargeValue()+"%");
            }
            else
                chargeDetailsVO.setChargeValue(chargeVO.getAgentChargeValue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));

        }
        else if(Charge_category.Failure.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=vCntAuthfailed;
            vDblAmount=vDblAuthfailedAmount;
            vDblTotal=(vCntCounter*Double.valueOf(chargeVO.getAgentChargeValue()));

            chargeDetailsVO=new ChargeDetailsVO();
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            if(Charge_unit.Percentage.toString().equals(chargeVO.getValuetype()))
            {
                chargeDetailsVO.setChargeValue(chargeVO.getAgentChargeValue()+"%");
            }
            else
                chargeDetailsVO.setChargeValue(chargeVO.getAgentChargeValue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Failure.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=vCntAuthfailed;
            vDblAmount=vDblAuthfailedAmount;
            vDblTotal=(vDblAmount*Double.valueOf(chargeVO.getAgentChargeValue()))/100;

            chargeDetailsVO=new ChargeDetailsVO();
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            if(Charge_unit.Percentage.toString().equals(chargeVO.getValuetype()))
            {
                chargeDetailsVO.setChargeValue(chargeVO.getAgentChargeValue()+"%");
            }
            else
                chargeDetailsVO.setChargeValue(chargeVO.getAgentChargeValue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        return chargeDetailsVO;

    }

    public ChargeDetailsVO calculateGatewayAccountChargesForPartner(ChargeVO chargeVO,TransactionSummaryVO summaryVO)
    {
        long vCntSettled = 0;
        long vCntReversed = 0;
        long vCntChargeback = 0;
        long vCntAuthfailed = 0;

        double vDblAuthfailedAmount=0.0;
        double vDblSettledAmount=0.0;
        double vDblReversedAmount = 0.0;
        double vDblChargebackAmount = 0.0;

        long vCntCounter=0;
        double vDblAmount=0.0;
        double vDblTotal=0.0;

        vCntAuthfailed=summaryVO.getCountOfAuthfailed();
        vDblAuthfailedAmount=summaryVO.getAuthfailedAmount();

        vCntSettled=summaryVO.getCountOfSettled();
        vDblSettledAmount=summaryVO.getSettledAmount();

        vCntReversed=summaryVO.getCountOfReversed();
        vDblReversedAmount=summaryVO.getReversedAmount();

        vCntChargeback=summaryVO.getCountOfChargeback();
        vDblChargebackAmount=summaryVO.getChargebackAmount();

        ChargeDetailsVO chargeDetailsVO=null;

        if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=vCntSettled+vCntReversed+vCntChargeback;
            vDblAmount=vDblSettledAmount+vDblReversedAmount+vDblChargebackAmount;
            vDblTotal=(vDblAmount*Double.valueOf(chargeVO.getPartnerChargeValue()))/100;

            chargeDetailsVO=new ChargeDetailsVO();
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            if(Charge_unit.Percentage.toString().equals(chargeVO.getValuetype()))
            {
                chargeDetailsVO.setChargeValue(chargeVO.getPartnerChargeValue()+"%");
            }
            else
                chargeDetailsVO.setChargeValue(chargeVO.getPartnerChargeValue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=vCntSettled+vCntReversed+vCntChargeback;
            vDblAmount=vDblSettledAmount+vDblReversedAmount+vDblChargebackAmount;
            vDblTotal=vCntCounter*Double.valueOf(chargeVO.getPartnerChargeValue());

            chargeDetailsVO=new ChargeDetailsVO();
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            if(Charge_unit.Percentage.toString().equals(chargeVO.getValuetype()))
            {
                chargeDetailsVO.setChargeValue(chargeVO.getPartnerChargeValue()+"%");
            }
            else
                chargeDetailsVO.setChargeValue(chargeVO.getPartnerChargeValue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Reversed.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=vCntReversed;
            vDblAmount=vDblReversedAmount;
            vDblTotal=(vCntCounter*Double.valueOf(chargeVO.getPartnerChargeValue()));

            chargeDetailsVO=new ChargeDetailsVO();
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            if(Charge_unit.Percentage.toString().equals(chargeVO.getValuetype()))
            {
                chargeDetailsVO.setChargeValue(chargeVO.getPartnerChargeValue()+"%");
            }
            else
                chargeDetailsVO.setChargeValue(chargeVO.getPartnerChargeValue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));


        }
        else if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Reversed.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=vCntReversed;
            vDblAmount=vDblReversedAmount;
            vDblTotal=(vDblAmount*Double.valueOf(chargeVO.getPartnerChargeValue()))/100;

            chargeDetailsVO=new ChargeDetailsVO();
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            if(Charge_unit.Percentage.toString().equals(chargeVO.getValuetype()))
            {
                chargeDetailsVO.setChargeValue(chargeVO.getPartnerChargeValue()+"%");
            }
            else
                chargeDetailsVO.setChargeValue(chargeVO.getPartnerChargeValue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));

        }
        else if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Chargeback.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=vCntChargeback;
            vDblAmount=vDblChargebackAmount;
            vDblTotal=(vCntCounter*Double.valueOf(chargeVO.getPartnerChargeValue()));

            chargeDetailsVO=new ChargeDetailsVO();
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            if(Charge_unit.Percentage.toString().equals(chargeVO.getValuetype()))
            {
                chargeDetailsVO.setChargeValue(chargeVO.getPartnerChargeValue()+"%");
            }
            else
                chargeDetailsVO.setChargeValue(chargeVO.getPartnerChargeValue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));

        }
        else if(Charge_category.Failure.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=vCntAuthfailed;
            vDblAmount=vDblAuthfailedAmount;
            vDblTotal=(vCntCounter*Double.valueOf(chargeVO.getPartnerChargeValue()));

            chargeDetailsVO=new ChargeDetailsVO();
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            if(Charge_unit.Percentage.toString().equals(chargeVO.getValuetype()))
            {
                chargeDetailsVO.setChargeValue(chargeVO.getPartnerChargeValue()+"%");
            }
            else
                chargeDetailsVO.setChargeValue(chargeVO.getPartnerChargeValue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        else if(Charge_category.Failure.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
        {
            vCntCounter=vCntAuthfailed;
            vDblAmount=vDblAuthfailedAmount;
            vDblTotal=(vDblAmount*Double.valueOf(chargeVO.getPartnerChargeValue()))/100;

            chargeDetailsVO=new ChargeDetailsVO();
            chargeDetailsVO.setChargeName(chargeVO.getChargename());
            if(Charge_unit.Percentage.toString().equals(chargeVO.getValuetype()))
            {
                chargeDetailsVO.setChargeValue(chargeVO.getPartnerChargeValue()+"%");
            }
            else
                chargeDetailsVO.setChargeValue(chargeVO.getPartnerChargeValue());

            chargeDetailsVO.setCount(vCntCounter);
            chargeDetailsVO.setAmount(vDblAmount);
            chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal,2));
        }
        return chargeDetailsVO;

    }

    public Hashtable getMerchantWireList(String toid,String accountid,String terminalid,String cycleid, String parentcycleid,String gateway,String is_paid,String fdtstamp, String tdtstamp,int pageno, int records,String reportid)
    {
        return PayoutDAO.getMerchantWireList(toid,accountid,terminalid,cycleid,parentcycleid,gateway,is_paid,fdtstamp, tdtstamp,pageno, records,reportid);
    }

    public String getMerchantpayOutHash(PayoutDetailsVO payoutDetailsVO)
    {
        return PayoutDAO.getMerchantpayOutHash(payoutDetailsVO);
    }

    public String merchantpayOutUpdate(PayoutDetailsVO payoutDetailsVO)
    {
        return PayoutDAO.merchantpayOutUpdate(payoutDetailsVO);
    }

    public Hashtable getISOWireReport(String accountid,String gateway,String isPaid,String fdtstamp, String tdtstamp,int pageno, int records)
    {
        return PayoutDAO.getISOWireReport(accountid, gateway, isPaid, fdtstamp, tdtstamp, pageno, records);
    }

    public  List<TerminalVO> getTerminalForMerchantWireList()
    {
        return PayoutDAO.getTerminalForMerchantWireList();
    }

    public List<TerminalVO> loadcardtypeids()
    {
        return PayoutDAO.loadcardtypeids();
    }

    public List<TerminalVO> loadPaymodeids()
    {
        return PayoutDAO.loadPaymodeids();
    }

    public boolean getPriviousWireDetails(TerminalVO terminalVO, SettlementDateVO settlementDateVO)
    {
        //Get the start date from wiremanager based on members previous wire on selected account.if wire not found in wiremanager then get first transaction date irrespective of status.
        boolean wireFound = false;
        String chargebackTransStartDate=payoutDAO.getChargebackStartDateToMerchant(terminalVO);
        if (functions.isValueNull(chargebackTransStartDate))
        {
            //chargebackTransStartDate=payoutDAO.getMemberFirstTransactionDateOnTerminal(terminalVO);
            settlementDateVO.setChargebackStartDate(chargebackTransStartDate);
            wireFound = true;
        }
        return wireFound;
    }
    public void settledReport(HSSFSheet sheet, HSSFWorkbook workbook, HSSFCellStyle style, SettlementDateVO settlementDateVO, String tableName, TerminalVO terminalVO)throws SystemError
    {
        HSSFRow header=sheet.createRow(0);
        List<TransactionVO> transactionVOs=null;
        PayoutDAO payoutDAO=new PayoutDAO();

        sheet.setColumnWidth((short)0,(short) 200000);
        sheet.setColumnWidth((short)1,(short) 200000);
        sheet.setColumnWidth((short)2,(short) 200000);
        sheet.setColumnWidth((short)3,(short) 200000);
        sheet.setColumnWidth((short)4,(short) 200000);
        sheet.setColumnWidth((short)5,(short) 200000);
        sheet.setColumnWidth((short)6,(short) 200000);
        sheet.setColumnWidth((short)7,(short) 200000);
        sheet.setColumnWidth((short)8,(short) 200000);
        sheet.setColumnWidth((short)9,(short) 200000);
        sheet.setColumnWidth((short)10,(short) 200000);

        HSSFCell cell0=header.createCell((short)0);
        HSSFCell cell1=header.createCell((short)1);
        HSSFCell cell2=header.createCell((short)2);
        HSSFCell cell3=header.createCell((short)3);
        HSSFCell cell4=header.createCell((short)4);
        HSSFCell cell5=header.createCell((short)5);
        HSSFCell cell6=header.createCell((short)6);
        HSSFCell cell7=header.createCell((short)7);
        HSSFCell cell8=header.createCell((short)8);
        HSSFCell cell9=header.createCell((short)9);
        HSSFCell cell10=header.createCell((short)10);

        // Start Style Added For Displaying Header Background As Gray And Strong
        style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setBorderLeft((short)1);
        style.setBorderRight((short)1);
        style.setBorderTop((short)1);
        style.setBorderBottom((short)1);

        HSSFFont font = workbook.createFont();
        font.setBoldweight((short)2);
        style.setFont(font);

        // End Style Added For Displaying Header Background As Gray And Strong

        cell0.setCellStyle(style);
        cell1.setCellStyle(style);
        cell2.setCellStyle(style);
        cell3.setCellStyle(style);
        cell4.setCellStyle(style);
        cell5.setCellStyle(style);
        cell6.setCellStyle(style);
        cell7.setCellStyle(style);
        cell8.setCellStyle(style);
        cell9.setCellStyle(style);
        cell10.setCellStyle(style);

        cell0.setCellValue("TrackingID");
        cell1.setCellValue("Description");
        cell2.setCellValue("Trade date");
        cell3.setCellValue("Amount");
        cell4.setCellValue("Currency");
        cell5.setCellValue("Current Status");
        cell6.setCellValue("Billing firstname");
        cell7.setCellValue("Billing last name");
        cell8.setCellValue("Wallet Currency");
        cell9.setCellValue("Wallet Amount");
        cell10.setCellValue("Settlement Currency");


        int index=1;

        //Get All Settled The Transaction Between Date Renage Based On Dtstamp
        transactionVOs=payoutDAO.getTransactionDetailsByDtstamp(tableName, settlementDateVO.getSettlementStartDate(), settlementDateVO.getSettlementEndDate(), terminalVO, PZTransactionStatus.SETTLED);
        for(TransactionVO detailsVO:transactionVOs)
        {
            HSSFRow row = sheet.createRow((short)index);
            row.createCell((short)0).setCellValue(detailsVO.getTrackingId());
            row.createCell((short)1).setCellValue(detailsVO.getOrderId());
            row.createCell((short)2).setCellValue(detailsVO.getTransactionDate());
            row.createCell((short)3).setCellValue(detailsVO.getCaptureAmount());
            row.createCell((short)4).setCellValue(detailsVO.getCurrency());
            row.createCell((short)5).setCellValue(detailsVO.getStatus()) ;
            row.createCell((short)6).setCellValue(detailsVO.getCustFirstName());
            row.createCell((short)7).setCellValue(detailsVO.getCustLastName());
            row.createCell((short)8).setCellValue(detailsVO.getWalletCurrency());
            row.createCell((short)9).setCellValue(detailsVO.getWalletAmount());
            row.createCell((short)10).setCellValue(detailsVO.getSettlmentCurrency());
            index=index+1;
        }

        //Get All reversed The Transaction Between Date Renage Based On Dtstamp
        transactionVOs = payoutDAO.getTransactionDetailsByDtstamp(tableName, settlementDateVO.getSettlementStartDate(), settlementDateVO.getSettlementEndDate(), terminalVO, PZTransactionStatus.REVERSED);
        for (TransactionVO detailsVO : transactionVOs)
        {
            HSSFRow row = sheet.createRow((short) index);
            row.createCell((short) 0).setCellValue(detailsVO.getTrackingId());
            row.createCell((short) 1).setCellValue(detailsVO.getOrderId());
            row.createCell((short) 2).setCellValue(detailsVO.getTransactionDate());
            row.createCell((short) 3).setCellValue(detailsVO.getCaptureAmount());
            row.createCell((short) 4).setCellValue(detailsVO.getCurrency());
            row.createCell((short) 5).setCellValue(detailsVO.getStatus());
            row.createCell((short) 6).setCellValue(detailsVO.getCustFirstName());
            row.createCell((short) 7).setCellValue(detailsVO.getCustLastName());
            row.createCell((short)8).setCellValue(detailsVO.getWalletCurrency());
            row.createCell((short)9).setCellValue(detailsVO.getWalletAmount());
            row.createCell((short) 10).setCellValue(detailsVO.getSettlmentCurrency());
            index = index + 1;
        }

        //Get All chargeback The Transaction Between Date Renage Based On Dtstamp
        transactionVOs = payoutDAO.getTransactionDetailsByDtstamp(tableName, settlementDateVO.getSettlementStartDate(), settlementDateVO.getSettlementEndDate(), terminalVO, PZTransactionStatus.CHARGEBACK);
        for (TransactionVO detailsVO : transactionVOs)
        {
            HSSFRow row = sheet.createRow((short) index);
            row.createCell((short) 0).setCellValue(detailsVO.getTrackingId());
            row.createCell((short) 1).setCellValue(detailsVO.getOrderId());
            row.createCell((short) 2).setCellValue(detailsVO.getTransactionDate());
            row.createCell((short) 3).setCellValue(detailsVO.getCaptureAmount());
            row.createCell((short) 4).setCellValue(detailsVO.getCurrency());
            row.createCell((short) 5).setCellValue(detailsVO.getStatus());
            row.createCell((short) 6).setCellValue(detailsVO.getCustFirstName());
            row.createCell((short) 7).setCellValue(detailsVO.getCustLastName());
            row.createCell((short)8).setCellValue(detailsVO.getWalletCurrency());
            row.createCell((short)9).setCellValue(detailsVO.getWalletAmount());
            row.createCell((short) 10).setCellValue(detailsVO.getSettlmentCurrency());
            index = index + 1;
        }

        //Get All AuthFailed The Transaction Between Date Renage Based On Dtstamp
        transactionVOs=payoutDAO.getTransactionDetailsByDtstamp(tableName, settlementDateVO.getDeclinedStartDate(), settlementDateVO.getDeclinedEndDate(), terminalVO, PZTransactionStatus.AUTH_FAILED);
        for(TransactionVO detailsVO:transactionVOs)
        {
            HSSFRow row = sheet.createRow((short)index);
            row.createCell((short)0).setCellValue(detailsVO.getTrackingId());
            row.createCell((short)1).setCellValue(detailsVO.getOrderId());
            row.createCell((short)2).setCellValue(detailsVO.getTransactionDate());
            row.createCell((short)3).setCellValue(detailsVO.getAmount());
            row.createCell((short)4).setCellValue(detailsVO.getCurrency());
            row.createCell((short)5).setCellValue(detailsVO.getStatus()) ;
            row.createCell((short)6).setCellValue(detailsVO.getCustFirstName());
            row.createCell((short)7).setCellValue(detailsVO.getCustLastName());
            row.createCell((short)8).setCellValue(detailsVO.getWalletCurrency());
            row.createCell((short)9).setCellValue(detailsVO.getWalletAmount());
            row.createCell((short)10).setCellValue(detailsVO.getSettlmentCurrency());
            index=index+1;
        }
        transactionVOs=payoutDAO.getTransactionDetailsByDtstamp(tableName, settlementDateVO.getSettlementStartDate(), settlementDateVO.getSettlementEndDate(), terminalVO, PZTransactionStatus.CHARGEBACKREVERSED);
        for(TransactionVO detailsVO:transactionVOs)
        {
            HSSFRow row = sheet.createRow((short)index);
            row.createCell((short)0).setCellValue(detailsVO.getTrackingId());
            row.createCell((short)1).setCellValue(detailsVO.getOrderId());
            row.createCell((short)2).setCellValue(detailsVO.getTransactionDate());
            row.createCell((short)3).setCellValue(detailsVO.getCaptureAmount());
            row.createCell((short)4).setCellValue(detailsVO.getCurrency());
            row.createCell((short)5).setCellValue(detailsVO.getStatus()) ;
            row.createCell((short)6).setCellValue(detailsVO.getCustFirstName());
            row.createCell((short)7).setCellValue(detailsVO.getCustLastName());
            row.createCell((short)8).setCellValue(detailsVO.getWalletCurrency());
            row.createCell((short)9).setCellValue(detailsVO.getWalletAmount());
            row.createCell((short)10).setCellValue(detailsVO.getSettlmentCurrency());
            index=index+1;
        }
    }

    public void refundReport(HSSFSheet sheet, HSSFWorkbook workbook, HSSFCellStyle style, SettlementDateVO settlementDateVO, String tableName, TerminalVO terminalVO)throws SystemError
    {
        HSSFRow header=sheet.createRow(0);
        List<TransactionVO> transactionVOs=null;
        PayoutDAO payoutDAO=new PayoutDAO();
       /* int rowno = 1;
        HSSFRow dateHeader = sheet.createRow(rowno);
        sheet.addMergedRegion(new Region(rowno, (short) 0, rowno, (short) 5));
        rowno++;
        HSSFCell dateCell = dateHeader.createCell((short) 0);
        dateCell.setCellValue(" Reverse Transaction Report Received (" + settlementDateVO.getReversedStartDate() + " - " + settlementDateVO.getReversedEndDate() + ")");

*/
        sheet.setColumnWidth((short)0,(short) 200000);
        sheet.setColumnWidth((short)1,(short) 200000);
        sheet.setColumnWidth((short)2,(short) 200000);
        sheet.setColumnWidth((short)3,(short) 200000);
        sheet.setColumnWidth((short)4,(short) 200000);
        sheet.setColumnWidth((short)5,(short) 200000);
        sheet.setColumnWidth((short)6,(short) 200000);
        sheet.setColumnWidth((short)7,(short) 200000);
        sheet.setColumnWidth((short)8,(short) 200000);
        sheet.setColumnWidth((short)9,(short) 200000);
        sheet.setColumnWidth((short)10,(short) 200000);

        HSSFCell cell0=header.createCell((short)0);
        HSSFCell cell1=header.createCell((short)1);
        HSSFCell cell2=header.createCell((short)2);
        HSSFCell cell3=header.createCell((short)3);
        HSSFCell cell4=header.createCell((short)4);
        HSSFCell cell5=header.createCell((short)5);
        HSSFCell cell6=header.createCell((short)6);
        HSSFCell cell7=header.createCell((short)7);
        HSSFCell cell8=header.createCell((short)8);
        HSSFCell cell9=header.createCell((short)9);
        HSSFCell cell10=header.createCell((short)10);

        // Start Style Added For Displaying Header Background As Gray And Strong
        style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setBorderLeft((short)1);
        style.setBorderRight((short)1);
        style.setBorderTop((short)1);
        style.setBorderBottom((short)1);

        HSSFFont font = workbook.createFont();
        font.setBoldweight((short)2);
        style.setFont(font);

        // End Style Added For Displaying Header Background As Gray And Strong

        cell0.setCellStyle(style);
        cell1.setCellStyle(style);
        cell2.setCellStyle(style);
        cell3.setCellStyle(style);
        cell4.setCellStyle(style);
        cell5.setCellStyle(style);
        cell6.setCellStyle(style);
        cell7.setCellStyle(style);
        cell8.setCellStyle(style);
        cell9.setCellStyle(style);
        cell10.setCellStyle(style);

        cell0.setCellValue("TrackingID");
        cell1.setCellValue("Description");
        cell2.setCellValue("Refund date");
        cell3.setCellValue("Refund Amount");
        cell4.setCellValue("Currency");
        cell5.setCellValue("Current Status");
        cell6.setCellValue("Billing firstname");
        cell7.setCellValue("Billing last name");
        cell8.setCellValue("Wallet Currency");
        cell9.setCellValue("Wallet Amount");
        cell10.setCellValue("Settlement Currency");


        int index=1;
        //Get All The Reversal Transaction Between Date Renge Based On TimeStamp

        transactionVOs=payoutDAO.getTransactionDetailsByTimestamp(tableName,settlementDateVO.getReversedStartDate(),settlementDateVO.getReversedEndDate(),terminalVO,PZTransactionStatus.REVERSED);
        for(TransactionVO detailsVO:transactionVOs)
        {
            HSSFRow row = sheet.createRow((short)index);
            row.createCell((short)0).setCellValue(detailsVO.getTrackingId());
            row.createCell((short)1).setCellValue(detailsVO.getOrderId());
            row.createCell((short)2).setCellValue(detailsVO.getTransactionDate());
            row.createCell((short)3).setCellValue(detailsVO.getRefundAmount());
            row.createCell((short)4).setCellValue(detailsVO.getCurrency());
            row.createCell((short)5).setCellValue(detailsVO.getStatus()) ;
            row.createCell((short)6).setCellValue(detailsVO.getCustFirstName());
            row.createCell((short)7).setCellValue(detailsVO.getCustLastName());
            row.createCell((short)8).setCellValue(detailsVO.getWalletCurrency());
            row.createCell((short)9).setCellValue(detailsVO.getWalletAmount());
            row.createCell((short)10).setCellValue(detailsVO.getSettlmentCurrency());
            index=index+1;
        }
    }

    public void payoutReport(HSSFSheet sheet, HSSFWorkbook workbook, HSSFCellStyle style, SettlementDateVO settlementDateVO, String tableName, TerminalVO terminalVO)throws SystemError
    {
        HSSFRow header=sheet.createRow(0);
        List<TransactionVO> transactionVOs=null;
        PayoutDAO payoutDAO=new PayoutDAO();

        sheet.setColumnWidth((short)0,(short) 200000);
        sheet.setColumnWidth((short)1,(short) 200000);
        sheet.setColumnWidth((short)2,(short) 200000);
        sheet.setColumnWidth((short)3,(short) 200000);
        sheet.setColumnWidth((short)4,(short) 200000);
        sheet.setColumnWidth((short)5,(short) 200000);
        sheet.setColumnWidth((short)6,(short) 200000);
        sheet.setColumnWidth((short)7,(short) 200000);
        sheet.setColumnWidth((short)8,(short) 200000);
        sheet.setColumnWidth((short)9,(short) 200000);

        HSSFCell cell0=header.createCell((short)0);
        HSSFCell cell1=header.createCell((short)1);
        HSSFCell cell2=header.createCell((short)2);
        HSSFCell cell3=header.createCell((short)3);
        HSSFCell cell4=header.createCell((short)4);
        HSSFCell cell5=header.createCell((short)5);
        HSSFCell cell6=header.createCell((short)6);
        HSSFCell cell7=header.createCell((short)7);
        HSSFCell cell8=header.createCell((short)8);
        HSSFCell cell9=header.createCell((short)9);

        style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setBorderLeft((short)1);
        style.setBorderRight((short)1);
        style.setBorderTop((short)1);
        style.setBorderBottom((short)1);

        HSSFFont font = workbook.createFont();
        font.setBoldweight((short)2);
        style.setFont(font);

        cell0.setCellStyle(style);
        cell1.setCellStyle(style);
        cell2.setCellStyle(style);
        cell3.setCellStyle(style);
        cell4.setCellStyle(style);
        cell5.setCellStyle(style);
        cell6.setCellStyle(style);
        cell7.setCellStyle(style);
        cell8.setCellStyle(style);
        cell9.setCellStyle(style);

        cell0.setCellValue("TrackingID");
        cell1.setCellValue("Description");
        cell2.setCellValue("Payout date");
        cell3.setCellValue("Payout Amount");
        cell4.setCellValue("Currency");
        cell5.setCellValue("Current Status");
        cell6.setCellValue("Billing firstname");
        cell7.setCellValue("Billing last name");
        cell8.setCellValue("Wallet Currency");
        cell9.setCellValue("Wallet Amount");


        int index=1;
        //Get All The Payout Transaction Between Date Renge Based On TimeStamp

        transactionVOs=payoutDAO.getPayoutTransactionDetailsByTimestamp(tableName, settlementDateVO.getReversedStartDate(), settlementDateVO.getReversedEndDate(), terminalVO, PZTransactionStatus.PAYOUT_SUCCESS);
        for(TransactionVO detailsVO:transactionVOs)
        {
            HSSFRow row = sheet.createRow((short)index);
            row.createCell((short)0).setCellValue(detailsVO.getTrackingId());
            row.createCell((short)1).setCellValue(detailsVO.getOrderId());
            row.createCell((short)2).setCellValue(detailsVO.getTransactionDate());
            row.createCell((short)3).setCellValue(detailsVO.getPayoutAmount());
            row.createCell((short)4).setCellValue(detailsVO.getCurrency());
            row.createCell((short)5).setCellValue(detailsVO.getStatus()) ;
            row.createCell((short)6).setCellValue(detailsVO.getCustFirstName());
            row.createCell((short)7).setCellValue(detailsVO.getCustLastName());
            row.createCell((short)8).setCellValue(detailsVO.getWalletCurrency());
            row.createCell((short)9).setCellValue(detailsVO.getWalletAmount());
            index=index+1;
        }
    }

    public void RefundReversedReport(HSSFSheet sheet, HSSFWorkbook workbook, HSSFCellStyle style, SettlementDateVO settlementDateVO, String tableName, TerminalVO terminalVO)throws SystemError
    {
        HSSFRow header=sheet.createRow(0);
        List<TransactionVO> transactionVOs=null;
        PayoutDAO payoutDAO=new PayoutDAO();

        sheet.setColumnWidth((short)0,(short) 200000);
        sheet.setColumnWidth((short)1,(short) 200000);
        sheet.setColumnWidth((short)2,(short) 200000);
        sheet.setColumnWidth((short)3,(short) 200000);
        sheet.setColumnWidth((short)4,(short) 200000);
        sheet.setColumnWidth((short)5,(short) 200000);
        sheet.setColumnWidth((short)6,(short) 200000);
        sheet.setColumnWidth((short)7,(short) 200000);
        sheet.setColumnWidth((short)8,(short) 200000);
        sheet.setColumnWidth((short)9,(short) 200000);

        HSSFCell cell0=header.createCell((short)0);
        HSSFCell cell1=header.createCell((short)1);
        HSSFCell cell2=header.createCell((short)2);
        HSSFCell cell3=header.createCell((short)3);
        HSSFCell cell4=header.createCell((short)4);
        HSSFCell cell5=header.createCell((short)5);
        HSSFCell cell6=header.createCell((short)6);
        HSSFCell cell7=header.createCell((short)7);
        HSSFCell cell8=header.createCell((short)8);
        HSSFCell cell9=header.createCell((short)9);

        style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setBorderLeft((short)1);
        style.setBorderRight((short)1);
        style.setBorderTop((short)1);
        style.setBorderBottom((short)1);

        HSSFFont font = workbook.createFont();
        font.setBoldweight((short)2);
        style.setFont(font);

        cell0.setCellStyle(style);
        cell1.setCellStyle(style);
        cell2.setCellStyle(style);
        cell3.setCellStyle(style);
        cell4.setCellStyle(style);
        cell5.setCellStyle(style);
        cell6.setCellStyle(style);
        cell7.setCellStyle(style);
        cell8.setCellStyle(style);
        cell9.setCellStyle(style);

        cell0.setCellValue("TrackingID");
        cell1.setCellValue("Description");
        cell2.setCellValue("Refund Rollback date");
        cell3.setCellValue("Refund Rollback Amount");
        cell4.setCellValue("Currency");
        cell5.setCellValue("Current Status");
        cell6.setCellValue("Billing firstname");
        cell7.setCellValue("Billing last name");
        cell8.setCellValue("Wallet Currency");
        cell9.setCellValue("Wallet Amount");
        int index=1;
        transactionVOs=payoutDAO.getRefundReverseTransactionDetailsByTimestamp(tableName, settlementDateVO.getReversedStartDate(), settlementDateVO.getReversedEndDate(), terminalVO,null );
        for(TransactionVO detailsVO:transactionVOs)
        {
            HSSFRow row = sheet.createRow((short)index);
            row.createCell((short)0).setCellValue(detailsVO.getTrackingId());
            row.createCell((short)1).setCellValue(detailsVO.getOrderId());
            row.createCell((short)2).setCellValue(detailsVO.getTransactionDate());
            row.createCell((short)3).setCellValue(detailsVO.getAmount());
            row.createCell((short)4).setCellValue(detailsVO.getCurrency());
            row.createCell((short)5).setCellValue(detailsVO.getStatus()) ;
            row.createCell((short)6).setCellValue(detailsVO.getCustFirstName());
            row.createCell((short)7).setCellValue(detailsVO.getCustLastName());
            row.createCell((short)8).setCellValue(detailsVO.getWalletCurrency());
            row.createCell((short)9).setCellValue(detailsVO.getWalletAmount());
            index=index+1;
        }
    }

    public void CaseFilingReport(HSSFSheet sheet, HSSFWorkbook workbook, HSSFCellStyle style, SettlementDateVO settlementDateVO, String tableName, TerminalVO terminalVO)throws SystemError
    {
        HSSFRow header=sheet.createRow(0);
        List<TransactionVO> transactionVOs=null;
        PayoutDAO payoutDAO=new PayoutDAO();

        sheet.setColumnWidth((short)0,(short) 200000);
        sheet.setColumnWidth((short)1,(short) 200000);
        sheet.setColumnWidth((short)2,(short) 200000);
        sheet.setColumnWidth((short)3,(short) 200000);
        sheet.setColumnWidth((short)4,(short) 200000);
        sheet.setColumnWidth((short)5,(short) 200000);
        sheet.setColumnWidth((short)6,(short) 200000);
        sheet.setColumnWidth((short)7,(short) 200000);
        sheet.setColumnWidth((short)8,(short) 200000);
        sheet.setColumnWidth((short)9,(short) 200000);

        HSSFCell cell0=header.createCell((short)0);
        HSSFCell cell1=header.createCell((short)1);
        HSSFCell cell2=header.createCell((short)2);
        HSSFCell cell3=header.createCell((short)3);
        HSSFCell cell4=header.createCell((short)4);
        HSSFCell cell5=header.createCell((short)5);
        HSSFCell cell6=header.createCell((short)6);
        HSSFCell cell7=header.createCell((short)7);
        HSSFCell cell8=header.createCell((short)8);
        HSSFCell cell9=header.createCell((short)9);

        style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setBorderLeft((short)1);
        style.setBorderRight((short)1);
        style.setBorderTop((short)1);
        style.setBorderBottom((short)1);

        HSSFFont font = workbook.createFont();
        font.setBoldweight((short)2);
        style.setFont(font);

        cell0.setCellStyle(style);
        cell1.setCellStyle(style);
        cell2.setCellStyle(style);
        cell3.setCellStyle(style);
        cell4.setCellStyle(style);
        cell5.setCellStyle(style);
        cell6.setCellStyle(style);
        cell7.setCellStyle(style);
        cell8.setCellStyle(style);
        cell9.setCellStyle(style);

        cell0.setCellValue("TrackingID");
        cell1.setCellValue("Description");
        cell2.setCellValue("Case Filing Date");
        cell3.setCellValue("Case Filing Amount");
        cell4.setCellValue("Currency");
        cell5.setCellValue("Current Status");
        cell6.setCellValue("Billing firstname");
        cell7.setCellValue("Billing last name");
        cell8.setCellValue("Wallet Currency");
        cell9.setCellValue("Wallet Amount");
        int index=1;
        transactionVOs=payoutDAO.getCaseFilingTransactionDetailsByTimestamp(tableName, settlementDateVO.getChargebackStartDate(), settlementDateVO.getChargebackEndDate(), terminalVO,null );
        for(TransactionVO detailsVO:transactionVOs)
        {
            HSSFRow row = sheet.createRow((short)index);
            row.createCell((short)0).setCellValue(detailsVO.getTrackingId());
            row.createCell((short)1).setCellValue(detailsVO.getOrderId());
            row.createCell((short)2).setCellValue(detailsVO.getTransactionDate());
            row.createCell((short)3).setCellValue(detailsVO.getAmount());
            row.createCell((short)4).setCellValue(detailsVO.getCurrency());
            row.createCell((short)5).setCellValue(detailsVO.getStatus()) ;
            row.createCell((short)6).setCellValue(detailsVO.getCustFirstName());
            row.createCell((short)7).setCellValue(detailsVO.getCustLastName());
            row.createCell((short)8).setCellValue(detailsVO.getWalletCurrency());
            row.createCell((short)9).setCellValue(detailsVO.getWalletAmount());
            index=index+1;
        }
    }

    public void chargebackReport(HSSFSheet sheet, HSSFWorkbook workbook, HSSFCellStyle style, SettlementDateVO settlementDateVO, String tableName, TerminalVO terminalVO)throws SystemError
    {
        HSSFRow header=sheet.createRow(0);
        List<TransactionVO> transactionVOs=null;
        PayoutDAO payoutDAO=new PayoutDAO();
        sheet.setColumnWidth((short)0,(short) 200000);
        sheet.setColumnWidth((short)1,(short) 200000);
        sheet.setColumnWidth((short)2,(short) 200000);
        sheet.setColumnWidth((short)3,(short) 200000);
        sheet.setColumnWidth((short)4,(short) 200000);
        sheet.setColumnWidth((short)5,(short) 200000);
        sheet.setColumnWidth((short)6,(short) 200000);
        sheet.setColumnWidth((short)7,(short) 200000);
        sheet.setColumnWidth((short)8,(short) 200000);
        sheet.setColumnWidth((short)9,(short) 200000);
        sheet.setColumnWidth((short)10,(short) 200000);

        HSSFCell cell0=header.createCell((short)0);
        HSSFCell cell1=header.createCell((short)1);
        HSSFCell cell2=header.createCell((short)2);
        HSSFCell cell3=header.createCell((short)3);
        HSSFCell cell4=header.createCell((short)4);
        HSSFCell cell5=header.createCell((short)5);
        HSSFCell cell6=header.createCell((short)6);
        HSSFCell cell7=header.createCell((short)7);
        HSSFCell cell8=header.createCell((short)8);
        HSSFCell cell9=header.createCell((short)9);
        HSSFCell cell10=header.createCell((short)10);

        // Start Style Added For Displaying Header Background As Gray And Strong
        style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setBorderLeft((short)1);
        style.setBorderRight((short)1);
        style.setBorderTop((short)1);
        style.setBorderBottom((short)1);

        HSSFFont font = workbook.createFont();
        font.setBoldweight((short)2);
        style.setFont(font);

        // End Style Added For Displaying Header Background As Gray And Strong

        cell0.setCellStyle(style);
        cell1.setCellStyle(style);
        cell2.setCellStyle(style);
        cell3.setCellStyle(style);
        cell4.setCellStyle(style);
        cell5.setCellStyle(style);
        cell6.setCellStyle(style);
        cell7.setCellStyle(style);
        cell8.setCellStyle(style);
        cell9.setCellStyle(style);
        cell10.setCellStyle(style);

        cell0.setCellValue("TrackingID");
        cell1.setCellValue("Description");
        cell2.setCellValue("Chargeback date");
        cell3.setCellValue("Chargeback Amount");
        cell4.setCellValue("Currency");
        cell5.setCellValue("Current Status");
        cell6.setCellValue("Billing firstname");
        cell7.setCellValue("Billing last name");
        cell8.setCellValue("Wallet Currency");
        cell9.setCellValue("Wallet Amount");
        cell10.setCellValue("Settlement Currency");
        int index = 1;
        //Get All The Chargeback Transaction Between Date Renge Based On TimeStamp
        transactionVOs = payoutDAO.getTransactionDetailsByTimestampForChargeback(tableName,settlementDateVO.getChargebackStartDate(),settlementDateVO.getChargebackEndDate(),terminalVO,PZTransactionStatus.CHARGEBACK);
        for(TransactionVO detailsVO:transactionVOs)
        {
            HSSFRow row = sheet.createRow((short)index);
            row.createCell((short)0).setCellValue(detailsVO.getTrackingId());
            row.createCell((short)1).setCellValue(detailsVO.getOrderId());
            row.createCell((short)2).setCellValue(detailsVO.getTransactionDate());
            row.createCell((short)3).setCellValue(detailsVO.getChargebackAmount());
            row.createCell((short)4).setCellValue(detailsVO.getCurrency());
            row.createCell((short)5).setCellValue(detailsVO.getStatus()) ;
            row.createCell((short)6).setCellValue(detailsVO.getCustFirstName());
            row.createCell((short)7).setCellValue(detailsVO.getCustLastName());
            row.createCell((short)8).setCellValue(detailsVO.getWalletCurrency());
            row.createCell((short)9).setCellValue(detailsVO.getWalletAmount());
            row.createCell((short)10).setCellValue(detailsVO.getSettlmentCurrency());
            index=index+1;
        }
    }

    public void chargebackReversedReport(HSSFSheet sheet, HSSFWorkbook workbook, HSSFCellStyle style, SettlementDateVO settlementDateVO, String tableName, TerminalVO terminalVO)throws SystemError
    {
        HSSFRow header=sheet.createRow(0);
        List<TransactionVO> transactionVOs=null;
        PayoutDAO payoutDAO=new PayoutDAO();

        sheet.setColumnWidth((short)0,(short) 200000);
        sheet.setColumnWidth((short)1,(short) 200000);
        sheet.setColumnWidth((short)2,(short) 200000);
        sheet.setColumnWidth((short)3,(short) 200000);
        sheet.setColumnWidth((short)4,(short) 200000);
        sheet.setColumnWidth((short)5,(short) 200000);
        sheet.setColumnWidth((short)6,(short) 200000);
        sheet.setColumnWidth((short)7,(short) 200000);
        sheet.setColumnWidth((short)8,(short) 200000);
        sheet.setColumnWidth((short)9,(short) 200000);

        HSSFCell cell0=header.createCell((short)0);
        HSSFCell cell1=header.createCell((short)1);
        HSSFCell cell2=header.createCell((short)2);
        HSSFCell cell3=header.createCell((short)3);
        HSSFCell cell4=header.createCell((short)4);
        HSSFCell cell5=header.createCell((short)5);
        HSSFCell cell6=header.createCell((short)6);
        HSSFCell cell7=header.createCell((short)7);
        HSSFCell cell8=header.createCell((short)8);
        HSSFCell cell9=header.createCell((short)9);

        // Start Style Added For Displaying Header Background As Gray And Strong
        style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setBorderLeft((short)1);
        style.setBorderRight((short)1);
        style.setBorderTop((short)1);
        style.setBorderBottom((short)1);

        HSSFFont font = workbook.createFont();
        font.setBoldweight((short)2);
        style.setFont(font);

        // End Style Added For Displaying Header Background As Gray And Strong

        cell0.setCellStyle(style);
        cell1.setCellStyle(style);
        cell2.setCellStyle(style);
        cell3.setCellStyle(style);
        cell4.setCellStyle(style);
        cell5.setCellStyle(style);
        cell6.setCellStyle(style);
        cell7.setCellStyle(style);
        cell8.setCellStyle(style);
        cell9.setCellStyle(style);

        cell0.setCellValue("TrackingID");
        cell1.setCellValue("Description");
        cell2.setCellValue("Chargeback Reversed date");
        cell3.setCellValue("Chargeback Reversed Amount");
        cell4.setCellValue("Currency");
        cell5.setCellValue("Current Status");
        cell6.setCellValue("Billing firstname");
        cell7.setCellValue("Billing last name");
        cell8.setCellValue("Wallet Currency");
        cell9.setCellValue("Wallet Amount");
        int index=1;
        //Get All The Chargeback Reversed Transaction Between Date Renge Based On TimeStamp
        transactionVOs=payoutDAO.geTransactionDetailsForChargebackReversed(tableName,settlementDateVO.getChargebackStartDate(),settlementDateVO.getChargebackEndDate(),terminalVO,PZTransactionStatus.CHARGEBACKREVERSED);
        try
        {
            for (TransactionVO detailsVO : transactionVOs)
            {
                HSSFRow row = sheet.createRow((short) index);
                row.createCell((short) 0).setCellValue(detailsVO.getTrackingId());
                row.createCell((short) 1).setCellValue(detailsVO.getOrderId());
                row.createCell((short) 2).setCellValue(detailsVO.getTransactionDate());
                row.createCell((short) 3).setCellValue(detailsVO.getChargebackAmount());
                row.createCell((short) 4).setCellValue(detailsVO.getCurrency());
                row.createCell((short) 5).setCellValue(detailsVO.getStatus());
                row.createCell((short) 6).setCellValue(detailsVO.getCustFirstName());
                row.createCell((short) 7).setCellValue(detailsVO.getCustLastName());
                row.createCell((short) 8).setCellValue(detailsVO.getWalletCurrency());
                row.createCell((short) 9).setCellValue(detailsVO.getWalletAmount());
                index = index + 1;
            }
        }catch (Exception e){
            logger.error("Exception::::",e);
        }
    }

    private String generateWeeklyPayoutReportBasedOnMerchantConsolidated(WeeklyPayoutReportVO weeklyPayoutReportVO,List<TerminalVO> terminalVOList,String memberId,String accountId)throws Exception
    {
        SettlementDateVO settlementDateVO=weeklyPayoutReportVO.getSettlementDateVO();
        RollingReserveDateVO rollingReserveDateVO=weeklyPayoutReportVO.getRollingReserveDateVO();
        //List<MerchantRandomChargesVO> merchantRandomChargesVOList=weeklyPayoutReportVO.getMerchantRandomChargesVOList();
        HashMap<String, String> dynamicCountAmountMap=weeklyPayoutReportVO.getDynamicCountAmountMap();

        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String rollingReserveStartDate=targetFormat.format(targetFormat.parse(rollingReserveDateVO.getRollingReserveStartDate()));
        String rollingReserveEndDate=targetFormat.format(targetFormat.parse(rollingReserveDateVO.getRollingReserveEndDate()));

        String settlementStartDate=targetFormat.format(targetFormat.parse(settlementDateVO.getSettlementStartDate()));
        String settlementEndDate=targetFormat.format(targetFormat.parse(settlementDateVO.getSettlementEndDate()));

        rollingReserveDateVO.setRollingReserveStartDate(rollingReserveStartDate);
        rollingReserveDateVO.setRollingReserveEndDate(rollingReserveEndDate);

        settlementDateVO.setSettlementStartDate(settlementStartDate);
        settlementDateVO.setSettlementEndDate(settlementEndDate);

        String tableName="";//accountUtil.getTableNameSettlement(terminalVO.getAccountId());
        //MerchantDetailsVO merchantDetailsVO=merchantDAO.getMemberDetails(terminalVO.getMemberId());

       /* String contactPerson=merchantDetailsVO.getContact_persons();
        String companyName=merchantDetailsVO.getCompany_name();
        String terminalId=terminalVO.getTerminalId();
        String memberId=terminalVO.getMemberId();
        String accountId=terminalVO.getAccountId();
        String isPoweredBy=merchantDetailsVO.getIsPoweredBy();
*/
        String resStatus="Failed";
        GatewayAccount account= GatewayAccountService.getGatewayAccount(accountId);
        GatewayType gatewayType = GatewayTypeService.getGatewayType(account.getPgTypeId());

        String currency=gatewayType.getCurrency();

        /*String cardType=GatewayAccountService.getCardType(cardTypeId);
        String paymentType=GatewayAccountService.getPaymentTypes(payModeId);*/
        String status=null;

        long totalSuccessCount=0;
        double authfailedAmount=0.00;
        double settledAmount=0.0;
        double reversedAmount = 0.0;
        double chargebackAmount =0.0;
        double wireChargeAmount=0.00;
        double statementChargeAmount=0.00;

        double totalProcessingAmount= 0.0;
        double grossFee=0.0;
        double grossChargesAmount=0.00;
        double serviceTaxChargesAmount=0.00;
        double reserveGeneratedAmount=0.0;
        double reserveReleaseAmount=0.0;
        double reversalChargebackAmount=0.00;
        double previousBalanceAmount=0.00;
        double grossSetupFee=0.0;
        long verifyOrderCount=0;
        long refundAlertCount=0;
        long retrivalRequestCount=0;
        long fraudulentTransactionCount=0;

        String latestSetupFeeDate=null;

        WireAmountVO wireAmountVO = new WireAmountVO();
        //payoutDAO.setUnpaidBalanceAmountMWMOnTerminal(terminalVO, wireAmountVO);

        //Calculate The previous balance Amount consider only the unpaid column field amount
        //previousBalanceAmount=wireAmountVO.getUnpaidBalanceAmount();
        HashMap<String,List<ChargeDetailsVO>> chargeDetailsMapOFVOs=new LinkedHashMap<String,List<ChargeDetailsVO>>();
        HashMap<String,Double> totalProcessingAmountMap=new LinkedHashMap<String,Double>();
        HashMap<String,Double> reverseChargebackAmountMap=new LinkedHashMap<String,Double>();
        HashMap<String,Double> miscellaneousAdjustmentMap=new LinkedHashMap<String,Double>();
        HashMap<String,Double> totalAmountFundedToBankMap=new LinkedHashMap<String,Double>();
        HashMap<String,Double> totalAmountAfterExchangeRateMap=new LinkedHashMap<String,Double>();
        HashMap<String,ReserveGeneratedVO>  reserveGeneratedVOHashMap=new LinkedHashMap<String,ReserveGeneratedVO>();
        HashMap<String,Double>     reserveRefundVOHashMap=new LinkedHashMap<String, Double>() ;
        HashMap<String,CalculatedReserveRefundVO> calculatedReserveRefundVOHashMap=new LinkedHashMap<String, CalculatedReserveRefundVO>();
        HashMap<String,SetupChargeVO> setupChargeVOHashMap=new LinkedHashMap<String, SetupChargeVO>();

        HashMap<String,GrossChargeVO> grossChargeVOMap=new LinkedHashMap<String, GrossChargeVO>();
        HashMap<String,GrossChargeVO> grossChargeVOMapFinal=new LinkedHashMap<String, GrossChargeVO>();

        HashMap<String,ServiceTaxChargeVO> serviceTaxChargeVOHashMap=new LinkedHashMap<String, ServiceTaxChargeVO>();
        HashMap<String,ServiceTaxChargeVO> serviceTaxChargeVOHashMapFinal=new LinkedHashMap<String, ServiceTaxChargeVO>();

        SetupChargeVO setupChargeVO=null;
        ChargeDetailsVO chargeDetailsVO=null;
        SettlementExpensesVO settlementExpensesVO=null;
        CalculatedReserveRefundVO calculatedReserveRefundVO=null;

        String currentDate=Functions.convertDateDBFormat(Calendar.getInstance().getTime());
        ChargeManager chargeManager=new ChargeManager();
        HashMap<String,String> settlementCurrencyMap=new HashMap<>();
        HashMap<String,Double> exchangeRateMap=new HashMap<>();
        for(TerminalVO terminalVO:terminalVOList)
        {
            List<ChargeDetailsVO> chargeDetailsVOList=new ArrayList<>();
            List<ChargeVO> chargeVOs = payoutDAO.getChargesAsPerTerminal(terminalVO);
            List<MerchantRandomChargesVO> merchantRandomChargesVOList=chargeManager.getMerchantRandomChargesList(settlementDateVO.getSettlementcycleNumber(), terminalVO.getMemberId(), terminalVO.getTerminalId());
            String settlementCurrency=terminalVO.getSettlementCurrency();
            settlementCurrencyMap.put(terminalVO.getTerminalId(),settlementCurrency);

            TransactionSummaryVO transactionSummaryVO = getTotalSuccessCountAmountByTerminal(terminalVO, settlementDateVO);

            for (ChargeVO chargeVO : chargeVOs)
            {
                String terminalName=chargeVO.getTerminalid()+"-"+chargeVO.getPaymentName()+"-"+chargeVO.getCardType();
                if (Charge_keyword.TotalReserveGenerated.toString().equals(chargeVO.getKeyword()) || Charge_keyword.TotalReserveRefunded.toString().equals(chargeVO.getKeyword()) || Charge_keyword.CalculatedReserveRefund.toString().equals(chargeVO.getKeyword()) || Charge_keyword.Wire.toString().equals(chargeVO.getKeyword()) || Charge_keyword.Statement.toString().equals(chargeVO.getKeyword()) || Charge_keyword.GrossBalanceAmount.toString().equals(chargeVO.getKeyword()) || Charge_keyword.Setup.toString().equals(chargeVO.getKeyword()) || Charge_keyword.ServiceTax.toString().equals(chargeVO.getKeyword()) || Charge_keyword.NetFinalAmount.toString().equals(chargeVO.getKeyword()))
                {
                    String chargeVersionRate = chargeManager.getMerchantChargeVersionRate(chargeVO.getMappingid(), currentDate);
                    if (chargeVersionRate != null)
                    {
                        chargeVO.setChargevalue(chargeVersionRate);
                    }

                    if (Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.TotalReserveGenerated.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
                    {
                        double reserveGeneratedChargeFee = 0.00;
                        chargeDetailsVO = new ReserveGeneratedVO();
                        chargeDetailsVO.setCount(transactionSummaryVO.getTotalProcessingCount());
                        chargeDetailsVO.setChargeName(chargeVO.getChargename());
                        chargeDetailsVO.setValueType(chargeVO.getValuetype());
                        chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());
                        chargeDetailsVO.setAmount(transactionSummaryVO.getTotalProcessingAmount());
                        reserveGeneratedChargeFee = (transactionSummaryVO.getTotalProcessingAmount() * Double.valueOf(chargeVO.getChargevalue())) / 100;
                        chargeDetailsVO.setTotal(Functions.roundDBL(reserveGeneratedChargeFee, 2));
                        reserveGeneratedAmount = reserveGeneratedAmount + reserveGeneratedChargeFee;
                        //reserveGeneratedVOHashMap.put(terminalVO.getTerminalId(), chargeDetailsVO);
                        chargeDetailsVOList.add(chargeDetailsVO);
                        chargeDetailsMapOFVOs.put(terminalName, chargeDetailsVOList);
                    }
                    else if (Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.TotalReserveRefunded.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
                    {
                        double reserveRefundedChargeFee = 0.0;
                        tableName = accountUtil.getTableNameSettlement(accountId);
                        TransactionSummaryVO amountAndCount = payoutDAO.getReleaseCountAndAmount(rollingReserveDateVO, terminalVO, tableName);
                        ReserveRefundVO refundVO = new ReserveRefundVO();
                        refundVO.setCount(amountAndCount.getCountOfreserveRefund());
                        refundVO.setChargeName(chargeVO.getChargename());
                        refundVO.setValueType(chargeVO.getValuetype());
                        refundVO.setChargeValue(chargeVO.getChargevalue());
                        refundVO.setAmount(amountAndCount.getReserveRefundAmount());
                        reserveRefundedChargeFee = (amountAndCount.getReserveRefundAmount() * Double.valueOf(chargeVO.getChargevalue())) / 100;
                        refundVO.setTotal(Functions.roundDBL(reserveRefundedChargeFee, 2));
                        reserveReleaseAmount = reserveReleaseAmount + reserveRefundedChargeFee;
                        reserveRefundVOHashMap.put(terminalVO.getTerminalId(), reserveReleaseAmount);
                    }
                    else if (Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.CalculatedReserveRefund.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
                    {
                        calculatedReserveRefundVO = new CalculatedReserveRefundVO();
                        calculatedReserveRefundVO.setCount(totalSuccessCount);
                        calculatedReserveRefundVO.setChargeName(chargeVO.getChargename());
                        calculatedReserveRefundVO.setValueType(chargeVO.getValuetype());
                        calculatedReserveRefundVO.setChargeValue(chargeVO.getChargevalue());
                        //set Amount and Total From outside the loop
                        calculatedReserveRefundVOHashMap.put(chargeDetailsVO.getChargeName(), calculatedReserveRefundVO);
                        chargeDetailsVOList.add(calculatedReserveRefundVO);
                        chargeDetailsMapOFVOs.put(terminalName, chargeDetailsVOList);
                    }
                    else if (chargeVO.getCategory().equals(Charge_category.Others.toString()) && chargeVO.getKeyword().equals(Charge_keyword.Statement.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString()))
                    {
                        long currentCounter = 1;
                        chargeDetailsVO = new ChargeDetailsVO();
                        chargeDetailsVO.setCount(currentCounter);
                        chargeDetailsVO.setChargeName(chargeVO.getChargename());
                        chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());
                        chargeDetailsVO.setValueType(chargeVO.getValuetype());
                        chargeDetailsVO.setAmount(0.00);
                        statementChargeAmount = currentCounter * Double.valueOf(chargeVO.getChargevalue());
                        chargeDetailsVO.setTotal(Functions.roundDBL(statementChargeAmount, 2));
                        chargeDetailsVOList.add(chargeDetailsVO);
                        chargeDetailsMapOFVOs.put(terminalName, chargeDetailsVOList);
                    }
                    else if (chargeVO.getCategory().equals(Charge_category.Others.toString()) && chargeVO.getKeyword().equals(Charge_keyword.Wire.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString()))
                    {
                        long currentCounter = 1;
                        chargeDetailsVO = new ChargeDetailsVO();
                        chargeDetailsVO.setCount(currentCounter);
                        chargeDetailsVO.setChargeName(chargeVO.getChargename());
                        chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());
                        chargeDetailsVO.setValueType(chargeVO.getValuetype());
                        chargeDetailsVO.setAmount(0.00);
                        wireChargeAmount = currentCounter * Double.valueOf(chargeVO.getChargevalue());
                        chargeDetailsVO.setTotal(Functions.roundDBL(wireChargeAmount, 2));
                        chargeDetailsVOList.add(chargeDetailsVO);
                        chargeDetailsMapOFVOs.put(terminalName, chargeDetailsVOList);
                    }
                    if (chargeVO.getCategory().equals(Charge_category.Success.toString()) && chargeVO.getKeyword().equals(Charge_keyword.NetFinalAmount.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Amount.toString()))
                    {
                        settlementExpensesVO= new SettlementExpensesVO();
                        settlementExpensesVO.setChargeName(chargeVO.getChargename());
                        settlementExpensesVO.setChargeValue(chargeVO.getChargevalue());
                        settlementExpensesVO.setValueType(chargeVO.getValuetype());
                        chargeDetailsVOList.add(settlementExpensesVO);
                        chargeDetailsMapOFVOs.put(terminalName, chargeDetailsVOList);
                    }
                    else if (chargeVO.getCategory().equals(Charge_category.Others.toString()) && chargeVO.getKeyword().equals(Charge_keyword.Setup.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString()))
                    {
                        setupChargeVO = applySetupChargeUptoSettledDate(chargeVO, terminalVO, settlementDateVO);
                        if (setupChargeVO != null)
                        {
                            grossSetupFee = grossSetupFee + setupChargeVO.getTotal();
                            latestSetupFeeDate = setupChargeVO.getLastChargeDate();
                        }
                        //setupChargeVOHashMap.put(terminalVO.getTerminalId(), setupChargeVO);
                        chargeDetailsVOList.add(setupChargeVO);
                        chargeDetailsMapOFVOs.put(terminalName, chargeDetailsVOList);
                    }
                    else if (chargeVO.getCategory().equals(Charge_category.Others.toString()) && chargeVO.getKeyword().equals(Charge_keyword.GrossBalanceAmount.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Amount.toString()))
                    {
                        GrossChargeVO grossChargeVO = new GrossChargeVO();
                        grossChargeVO.setChargeName(chargeVO.getChargename());
                        grossChargeVO.setChargeValue(chargeVO.getChargevalue());
                        grossChargeVO.setValueType(chargeVO.getValuetype());
                        grossChargeVOMap.put(grossChargeVO.getChargeName(), grossChargeVO);
                        chargeDetailsVOList.add(setupChargeVO);
                        chargeDetailsMapOFVOs.put(terminalName, chargeDetailsVOList);
                    }
                    else if (chargeVO.getCategory().equals(Charge_category.Others.toString()) && chargeVO.getKeyword().equals(Charge_keyword.ServiceTax.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Amount.toString()))
                    {
                        ServiceTaxChargeVO serviceTaxChargeVO = new ServiceTaxChargeVO();
                        serviceTaxChargeVO.setChargeName(chargeVO.getChargename());
                        serviceTaxChargeVO.setChargeValue(chargeVO.getChargevalue());
                        serviceTaxChargeVO.setValueType(chargeVO.getValuetype());
                        serviceTaxChargeVOHashMap.put(chargeDetailsVO.getChargeName(), serviceTaxChargeVO);
                        chargeDetailsVOList.add(serviceTaxChargeVO);
                        chargeDetailsMapOFVOs.put(terminalName, chargeDetailsVOList);
                    }
                }
                else
                {
                    //Get Merchant Charge Version Rate
                    String chargeVersionRate = chargeManager.getMerchantChargeVersionRate(chargeVO.getMappingid(), currentDate);
                    if (chargeVersionRate != null)
                    {
                        chargeVO.setChargevalue(chargeVersionRate);
                    }

                    chargeDetailsVO = applyChargeOnTerminal(terminalVO, chargeVO, settlementDateVO, tableName, transactionSummaryVO, dynamicCountAmountMap);
                    if (chargeDetailsVO.getChargeName() != null)
                    {

                        grossFee = grossFee + chargeDetailsVO.getTotal();
                        chargeDetailsVOList.add(chargeDetailsVO);
                        chargeDetailsMapOFVOs.put(terminalName, chargeDetailsVOList);
                        if (chargeVO.getKeyword().equals(Charge_keyword.Reversed.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString()))
                        {
                            reversedAmount = reversedAmount + chargeDetailsVO.getAmount();
                        }
                        if (chargeVO.getKeyword().equals(Charge_keyword.Chargeback.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString()))
                        {
                            chargebackAmount = chargebackAmount + chargeDetailsVO.getAmount();
                        }
                        if (chargeVO.getKeyword().equals(Charge_keyword.VerifyOrder.toString()))
                        {
                            verifyOrderCount = chargeDetailsVO.getCount();
                        }
                        if (chargeVO.getKeyword().equals(Charge_keyword.RefundAlert.toString()))
                        {
                            refundAlertCount = chargeDetailsVO.getCount();
                        }
                        if (chargeVO.getKeyword().equals(Charge_keyword.RetrivalRequest.toString()))
                        {
                            retrivalRequestCount = chargeDetailsVO.getCount();
                        }
                        if (chargeVO.getKeyword().equals(Charge_keyword.FraudulentTransaction.toString()))
                        {
                            fraudulentTransactionCount = chargeDetailsVO.getCount();
                        }
                    }
                }
            }
            totalProcessingAmount=transactionSummaryVO.getTotalProcessingAmount();
            totalProcessingAmountMap.put(terminalVO.getTerminalId(),totalProcessingAmount);

            reversalChargebackAmount=chargebackAmount+reversedAmount;
            reverseChargebackAmountMap.put(terminalVO.getTerminalId(),reversalChargebackAmount);

            double merchantRandomChargesGrossAmount=0.00;
            double deductedAmount=0.00;
            double addedAmount=0.00;
            for(MerchantRandomChargesVO merchantRandomChargesVO:merchantRandomChargesVOList)
            {
                if("Deducted".equals(merchantRandomChargesVO.getChargeType()))
                {
                    //System.out.println(merchantRandomChargesVO.getChargeType());
                    deductedAmount=deductedAmount+merchantRandomChargesVO.getChargeValue();
                }
                else
                {
                    addedAmount=addedAmount+merchantRandomChargesVO.getChargeValue();
                }
            }
            merchantRandomChargesGrossAmount=addedAmount-deductedAmount;
            miscellaneousAdjustmentMap.put(terminalVO.getTerminalId(), merchantRandomChargesGrossAmount);

            //calculate gross amount here coz need to pass gross Amount to Calculate gross level charges
            double grossDeduct=-1*(grossFee+chargebackAmount+reversedAmount+reserveGeneratedAmount);
            double grossAmount=totalProcessingAmount+grossDeduct;

            grossAmount=Functions.roundDBL(grossAmount,2);

            if(calculatedReserveRefundVOHashMap.size()>0)
            {
                Set set=calculatedReserveRefundVOHashMap.keySet();
                Iterator itr=set.iterator();
                while(itr.hasNext())
                {
                    String chargeName=itr.next().toString();
                    ReserveRefundVO refundVO= applyChargeOnReserveReleaseAmount(chargeName,calculatedReserveRefundVOHashMap,reserveReleaseAmount);
                    reserveReleaseAmount=reserveReleaseAmount-refundVO.getTotal();
                    reserveRefundVOHashMap.put(terminalVO.getTerminalId(),reserveReleaseAmount);
                }
            }
            if(grossChargeVOMap.size()>0)
            {
                String chargeName=null;
                Set set=grossChargeVOMap.keySet();
                Iterator itr=set.iterator();
                while(itr.hasNext())
                {
                    chargeName=(String)itr.next();
                    GrossChargeVO grossChargesVO=applyGrossCharge(chargeName, grossChargeVOMap, grossAmount);
                    grossChargesAmount=grossChargesAmount+grossChargesVO.getTotal();
                    grossChargeVOMapFinal.put(grossChargesVO.getChargeName(), grossChargesVO);
                }
            }
            if(serviceTaxChargeVOHashMap.size()>0)
            {
                String chargeName=null;
                Set set=serviceTaxChargeVOHashMap.keySet();
                Iterator itr=set.iterator();
                while(itr.hasNext())
                {
                    chargeName=(String)itr.next();
                    ServiceTaxChargeVO serviceTaxChargeVO=calculateServiveTax(chargeName,serviceTaxChargeVOHashMap,grossFee+wireChargeAmount);
                    serviceTaxChargesAmount=serviceTaxChargesAmount+serviceTaxChargeVO.getTotal();
                    serviceTaxChargeVOHashMapFinal.put(serviceTaxChargeVO.getChargeName(), serviceTaxChargeVO);
                }
            }

            merchantRandomChargesGrossAmount=addedAmount-deductedAmount;
            double totalFundedToBank=(reserveReleaseAmount+grossAmount+previousBalanceAmount+merchantRandomChargesGrossAmount)-((wireChargeAmount+statementChargeAmount)+(-1*grossChargesAmount)+serviceTaxChargesAmount);
            double minPayoutAmount=Double.valueOf(terminalVO.getMinPayoutAmount());
            /*boolean isWireFeeRequired=false;
            if(wireChargeVO!=null){
                if(totalFundedToBank>=minPayoutAmount){
                    isWireFeeRequired=true;
                }
            }

            if(wireChargeVO !=null && !isWireFeeRequired){
                totalFundedToBank=totalFundedToBank+wireChargeAmount;
            }*/

            double exchangeRateDbl=1.00;
            if(settlementCurrency!=null){
                exchangeRateDbl=terminalVO.getConversionRate();
                logger.error("exchangeRateDbl----"+exchangeRateDbl);
            }
            exchangeRateMap.put(terminalVO.getTerminalId(),exchangeRateDbl);

            double totalFundedToBankBeforeSettlementExpenses=totalFundedToBank;
            if(settlementExpensesVO !=null){
                double settlementExpensesAmount=totalFundedToBank*Double.valueOf(settlementExpensesVO.getChargeValue())/100;
                settlementExpensesVO.setAmount(totalFundedToBankBeforeSettlementExpenses);
                settlementExpensesVO.setTotal(settlementExpensesAmount);
                if(settlementExpensesAmount>0){
                    totalFundedToBank=totalFundedToBank-settlementExpensesAmount;
                }
                else{
                    totalFundedToBank=totalFundedToBank-(-1*settlementExpensesAmount);
                }
            }
            double totalFundedInSettlementCurrency=exchangeRateDbl*totalFundedToBank;
            totalFundedToBank=Functions.roundDBL(totalFundedToBank,2);
            totalAmountFundedToBankMap.put(terminalVO.getTerminalId(), totalFundedToBank);
            totalAmountAfterExchangeRateMap.put(terminalVO.getTerminalId(), totalFundedInSettlementCurrency);

        }

        if(chargeDetailsMapOFVOs.size()>=0)
        {
            String pdfFilePath=getConsolidatedMerchantReport(weeklyPayoutReportVO,terminalVOList,memberId,accountId,currency,settlementCurrencyMap,exchangeRateMap,chargeDetailsMapOFVOs,totalProcessingAmountMap,reverseChargebackAmountMap,reserveRefundVOHashMap,miscellaneousAdjustmentMap,totalAmountFundedToBankMap,totalAmountAfterExchangeRateMap);
            /*System.out.println("PDF File Generated Successfully");
            String settleTransExcelFilePath= createMerchantTransactionFile(settlementDateVO, tableName, terminalVO);
            String rollingReserveExcelFilePath= createRollingReserveTransactionFile(rollingReserveDateVO, terminalVO);
            logger.error("rollingReserveExcelFilePath::::"+rollingReserveExcelFilePath);

            WireVO wireVO=new WireVO();
            wireVO.setFirstDate(settlementDateVO.getSettlementStartDate());
            wireVO.setLastDate(settlementDateVO.getSettlementEndDate());
            wireVO.setCurrency(currency);
            wireVO.setMarkForDeletion("N");
            wireVO.setWireAmount(totalProcessingAmount);
            wireVO.setWireBalanceAmount(grossAmount);
            wireVO.setNetFinalAmount(totalFundedToBank);
            wireVO.setUnpaidAmount(0.00);
            wireVO.setStatus("unpaid");
            wireVO.setSettlementReportFilePath(pdfFilePath);
            wireVO.setSettledTransactionFilePath(settleTransExcelFilePath);
            wireVO.setReserveReleasedUptoDate(rollingReserveDateVO.getRollingReserveEndDate());
            wireVO.setRollingReserveIncluded("Y");
            wireVO.setTerminalVO(terminalVO);
            wireVO.setSettlementCycleNo(settlementDateVO.getSettlementcycleNumber());
            wireVO.setDeclinedcoverdateupto(settlementDateVO.getDeclinedEndDate());
            wireVO.setReversedcoverdateupto(settlementDateVO.getReversedEndDate());
            wireVO.setChargebackcoverdateupto(settlementDateVO.getChargebackEndDate());
            wireVO.setRollingReserveFilePath(rollingReserveExcelFilePath);
            status=payoutDAO.generateSettlementCycleWire(wireVO);
            if("success".equals(status))
            {
                //System.out.println("Wire Generated Successfully for memberId==="+memberId);
                String mailStatus = "Failed";

                //Call MailService To Give Alert Merchant To See payout report in merchant Back Office
                MailService mailService = new MailService();
                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                HashMap cbCountRecord = new HashMap();
                cbCountRecord.put(MailPlaceHolder.TOID, terminalVO.getMemberId());
                cbCountRecord.put(MailPlaceHolder.FROMDATE, settlementStartDate.split(" ")[0]);
                cbCountRecord.put(MailPlaceHolder.TODATE, settlementEndDate.split(" ")[0]);
                if (merchantRandomChargesVOList.size() > 0)
                {
                    cbCountRecord.put(MailPlaceHolder.MULTIPALTRANSACTION, mailService.getDetailTableForRandomCharges(merchantRandomChargesVOList));
                }
                asynchronousMailService.sendMerchantSignup(MailEventEnum.MERCHANT_PAYOUT_ALERT_MAIL, cbCountRecord);
                mailStatus = "success";
                boolean isMemberCycleUpdated = payoutDAO.updateMemberCycleDetailsWithAccountId(memberId, accountId, terminalId, verifyOrderCount, refundAlertCount, retrivalRequestCount, latestSetupFeeDate, settlementDateVO.getSettlementcycleNumber(), fraudulentTransactionCount);

                if (isMemberCycleUpdated)
                {
                    logger.debug("member_settlementcycle_details updated successfully");
                }

                //Important: Make entry for memberid,accountid,cycleid,ispaid='N' in the bank_merchant_settlement_master
                boolean isBankMerchantSettlementMasterUpdated = payoutDAO.merchantSettlementMasterEntry(memberId, accountId, terminalId, settlementDateVO.getSettlementcycleNumber(), "N");
                logger.error("pending List Size----"+weeklyPayoutReportVO.getPendingList().size());
                logger.error("Request List Size----"+weeklyPayoutReportVO.getRequestTerminalVO().size());

                if (isBankMerchantSettlementMasterUpdated)
                {
                    //System.out.println("Making Entry Into The bank_merchant_settlement_master successfully");
                    *//*int data = weeklyPayoutReportVO.getPendingList().size() - weeklyPayoutReportVO.getRequestTerminalVO().size();
                    if (data == 0)
                    {
                        boolean ispayoutcronupdated = payoutDAO.updatePayoutCronExecutedFlag(accountId, settlementDateVO.getSettlementcycleNumber());
                        if (ispayoutcronupdated)
                        {
                            resStatus = "Success";
                        }
                    }*//*
                    resStatus = "Success";
                }
            }*/
        }
        return resStatus;
    }

    private String getConsolidatedMerchantReport(WeeklyPayoutReportVO weeklyPayoutReportVO,List<TerminalVO> terminalVOList,String memberId,String accountId,String currency,HashMap<String,String> settlementCurrencyMap,HashMap<String,Double> exchangeRate,HashMap<String,List<ChargeDetailsVO>> chargeVOHashMap,HashMap<String,Double> totalProcessingMap,HashMap<String,Double> refundChargebackMap,HashMap<String,Double> reserveRefundVOHashMap,HashMap<String,Double> miscellaneousAdjustment,HashMap<String,Double> totalAmountFundedToBankMap,HashMap<String,Double> totalAmountAfterExchangeRateMap)
    {
        AccountUtil accountUtil=new AccountUtil();
        Document document = new Document(PageSize.A3);
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
        String reportFilePath=null;
        String reportFileName=null;
        try
        {
            SettlementDateVO settlementDateVO=weeklyPayoutReportVO.getSettlementDateVO();
            RollingReserveDateVO rollingReserveDateVO=weeklyPayoutReportVO.getRollingReserveDateVO();
            MerchantDAO merchantDAO=new MerchantDAO();
            String sStartTransactionDate=settlementDateVO.getSettlementStartDate();
            String sEndTransactionDate=settlementDateVO.getSettlementEndDate();

            reportFileName=accountUtil.getConsolidatedReportName(memberId,accountId,sStartTransactionDate,sEndTransactionDate);
            HashMap partnerDetails=payoutDAO.getPartnerDetails(memberId);
            Hashtable hashtable=merchantDAO.getMerchantDetails(memberId);
            String companyName="";
            String contactPerson="";
            if(hashtable!=null){
                if(functions.isValueNull((String)hashtable.get("company_name")))
                    companyName=(String)hashtable.get("company_name");
                if(functions.isValueNull((String)hashtable.get("contact_persons")))
                    contactPerson=(String)hashtable.get("contact_persons");
            }
            reportFileName=reportFileName+".pdf";
            String partnerLogoName=(String)partnerDetails.get("logoName");
            String addressDetails=(String)partnerDetails.get("address");
            String telNo=(String)partnerDetails.get("telno");
            String companySupportMailId=(String)partnerDetails.get("companysupportmailid");
            String backgroundColor=(String)partnerDetails.get("reportfile_bgcolor");
            Color bgColor=Color.gray.brighter();
            if("Blue".equalsIgnoreCase(backgroundColor)){
                bgColor=new Color(0,127,255);
            }
            else if("Orange".equalsIgnoreCase(backgroundColor)){
                bgColor=new Color(245, 130, 42);
            }

            if(!functions.isValueNull(partnerLogoName)){
                partnerLogoName="pay2.png";
            }

            File filepath=new File("E:/tomcat8/reportfiles/"+reportFileName);
            //File filepath=new File(PAYOUT_REPORT_FILE_PATH+reportFileName);
            reportFilePath=filepath.getPath();

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filepath));
            if(functions.isValueNull(addressDetails) && functions.isValueNull(companySupportMailId) && functions.isValueNull(telNo)){
                Phrase phraseLine1=new Phrase(addressDetails);
                Phrase phraseLine2=new Phrase(" I "+companySupportMailId+" I "+telNo+"");
                writer.setBoxSize("art", writer.getPageSize());
                HeaderFooterPageEvent event = new HeaderFooterPageEvent(phraseLine1,phraseLine2);
                writer.setPageEvent(event);
            }

            document.open();
            Table table = new Table(7);
            table.setWidth(100);
            table.setBorder(Table.NO_BORDER);

            table.setBorderColor(new Color(0, 0,0));
            table.setPadding(1);

            String reportingDate=targetFormat.format(new Date());

            Image partnerImageInstance = Image.getInstance(PARTNER_LOGO_PATH+partnerLogoName);
            Font f1 = FontFactory.getFont(FontFactory.TIMES_ROMAN,20);
            f1.setColor(Color.BLACK);

            Font f2 = FontFactory.getFont(FontFactory.TIMES_BOLD,15);
            f2.setColor(Color.WHITE);

            Font f3 = FontFactory.getFont(FontFactory.TIMES_BOLD,15);
            f3.setColor(Color.BLACK);

            Cell partnerNameCaptionCell=new Cell(new Paragraph(20, "SETTLEMENT REPORT",f1));

            Cell partnerLogoCell=null;
            if(partnerImageInstance!=null){
                partnerLogoCell=new Cell(partnerImageInstance);
            }else{
                partnerLogoCell=new Cell("");
            }

            partnerNameCaptionCell.setColspan(4);
            partnerLogoCell.setColspan(3);

            partnerLogoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            partnerLogoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            partnerNameCaptionCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            partnerNameCaptionCell.setBackgroundColor(Color.white.brighter());

            partnerLogoCell.setBorder(Cell.NO_BORDER);
            partnerNameCaptionCell.setBorder(Cell.NO_BORDER);

            table.addCell(partnerNameCaptionCell);
            table.addCell(partnerNameCaptionCell);
            table.addCell(partnerLogoCell);

            Cell sMemberDetailsHeaderCell=new Cell(new Paragraph(20,"Member Details",f2));
            sMemberDetailsHeaderCell.setColspan(7);
            sMemberDetailsHeaderCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            sMemberDetailsHeaderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            sMemberDetailsHeaderCell.setBackgroundColor(bgColor);
            table.addCell(sMemberDetailsHeaderCell);

            Cell sMemberIdCaptionCell=new Cell("Member ID:");
            Cell sMemberIdValueCell=new Cell(memberId);
            sMemberIdCaptionCell.setColspan(4);
            sMemberIdValueCell.setColspan(3);
            table.addCell(sMemberIdCaptionCell);
            table.addCell(sMemberIdValueCell);

            Cell sCompanyNameCaptionCell=new Cell("Company Name:");
            Cell sCompanyNameValueCell=new Cell(companyName);
            sCompanyNameCaptionCell.setColspan(4);
            sCompanyNameValueCell.setColspan(3);
            table.addCell(sCompanyNameCaptionCell);
            table.addCell(sCompanyNameValueCell);

            Cell sContactPersonCaptionCell=new Cell("Contact Person:");
            Cell sContactPersonValueCell=new Cell(contactPerson);
            sContactPersonCaptionCell.setColspan(4);
            sContactPersonValueCell.setColspan(3);
            table.addCell(sContactPersonCaptionCell);
            table.addCell(sContactPersonValueCell);

            Cell sSettlePeriodCaptionCell=new Cell("Settle Transaction Period:");
            Cell sSettleStartValueCell=new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getSettlementStartDate()))+" TO "+targetFormat.format(targetFormat.parse(settlementDateVO.getSettlementEndDate())));

            sSettlePeriodCaptionCell.setColspan(4);
            sSettleStartValueCell.setColspan(3);

            table.addCell(sSettlePeriodCaptionCell);
            table.addCell(sSettleStartValueCell);

            Cell sDeclinedPeriodCaptionCell=new Cell("Decline Transaction Period:");
            Cell sDeclinedStartValueCell=new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getDeclinedStartDate()))+" TO "+targetFormat.format(targetFormat.parse(settlementDateVO.getDeclinedEndDate())));

            sDeclinedPeriodCaptionCell.setColspan(4);
            sDeclinedStartValueCell.setColspan(3);

            table.addCell(sDeclinedPeriodCaptionCell);
            table.addCell(sDeclinedStartValueCell);

            Cell sReversedPeriodCaptionCell=new Cell("Reversal Transaction Period:");
            Cell sReversedStartValueCell=new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getReversedStartDate()))+" TO "+targetFormat.format(targetFormat.parse(settlementDateVO.getReversedEndDate())));

            sReversedPeriodCaptionCell.setColspan(4);
            sReversedStartValueCell.setColspan(3);

            table.addCell(sReversedPeriodCaptionCell);
            table.addCell(sReversedStartValueCell);

            Cell sChargebackPeriodCaptionCell=new Cell("Chargeback Transaction Period:");
            Cell sChargebackStartValueCell=new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getChargebackStartDate()))+" TO "+targetFormat.format(targetFormat.parse(settlementDateVO.getChargebackEndDate())));

            sChargebackPeriodCaptionCell.setColspan(4);
            sChargebackStartValueCell.setColspan(3);

            table.addCell(sChargebackPeriodCaptionCell);
            table.addCell(sChargebackStartValueCell);

            Cell sRollingReserveReleaseUptoDateCaptionCell=new Cell("Rolling Release Period:");
            Cell sRollingReserveReleaseStartDateValueCell=new Cell(targetFormat.format(targetFormat.parse(rollingReserveDateVO.getRollingReserveStartDate()))+" TO "+targetFormat.format(targetFormat.parse(rollingReserveDateVO.getRollingReserveEndDate())));

            sRollingReserveReleaseUptoDateCaptionCell.setColspan(4);
            sRollingReserveReleaseStartDateValueCell.setColspan(3);

            table.addCell(sRollingReserveReleaseUptoDateCaptionCell);
            table.addCell(sRollingReserveReleaseStartDateValueCell);
            Set<String> terminalList=chargeVOHashMap.keySet();
            double totalProcessingAmount=0.00;
            double totalChargesFee=0.00;
            double totalReverseChargeback=0.00;
            double totalAmountFunded=0.00;
            double totalAmountAfterConversion=0.00;
            for(String terminalId:terminalList)
            {
                String terminalDetails[]=terminalId.split("-");
                String terminal=terminalDetails[0];

                Cell sReportHeader=new Cell(new Paragraph(20,"Report For - "+terminalId,f2));
                sReportHeader.setColspan(7);
                sReportHeader.setHorizontalAlignment(Element.ALIGN_LEFT);
                sReportHeader.setBackgroundColor(bgColor);
                table.addCell(sReportHeader);

                Cell sChargesDetailsHeaderCell=new Cell(new Paragraph(20,"Charges Details ",f2));
                sChargesDetailsHeaderCell.setColspan(7);
                sChargesDetailsHeaderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                sChargesDetailsHeaderCell.setBackgroundColor(Color.GRAY);
                table.addCell(sChargesDetailsHeaderCell);

                Cell sChargeNameHeader = new Cell(new Paragraph(20,"Charge Name",f3));
                Cell sChargeRateHeader = new Cell(new Paragraph(20,"Rate/Fee",f3));
                Cell sChargeCounterHeader = new Cell(new Paragraph(20,"Counter",f3));
                Cell sChargeAmountHeader = new Cell(new Paragraph(20,"Amount",f3));
                Cell sChargeTotalHeader = new Cell(new Paragraph(20,"Total",f3));

                sChargeNameHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
                sChargeRateHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
                sChargeCounterHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
                sChargeAmountHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
                sChargeTotalHeader.setHorizontalAlignment(Element.ALIGN_CENTER);

                sChargeNameHeader.setColspan(3);

                table.addCell(sChargeNameHeader);
                table.addCell(sChargeRateHeader);
                table.addCell(sChargeCounterHeader);
                table.addCell(sChargeAmountHeader);
                table.addCell(sChargeTotalHeader);

                Cell sChargeNameValue,sChargeRateValue,sChargeCounterValue,sChargeAmountValue,sChargeTotalValue;
                List<ChargeDetailsVO> chargeDetailsVOList=chargeVOHashMap.get(terminalId);
                double grossTotalFee=0.00;
                for(ChargeDetailsVO chargeDetailsVO:chargeDetailsVOList)
                {
                    String vDblAmountChar=(Functions.round(chargeDetailsVO.getAmount(),2));
                    String vCntCounter="-";
                    sChargeNameValue = new Cell(chargeDetailsVO.getChargeName());
                    sChargeTotalValue = new Cell(Functions.round(chargeDetailsVO.getTotal(),2));
                    grossTotalFee=grossTotalFee+chargeDetailsVO.getTotal();
                    if("Percentage".equals(chargeDetailsVO.getValueType()))
                    {
                        sChargeRateValue = new Cell(chargeDetailsVO.getChargeValue()+"%");
                    }
                    else
                    {
                        sChargeRateValue = new Cell(chargeDetailsVO.getChargeValue());
                        vCntCounter=(new Long(chargeDetailsVO.getCount())).toString();
                    }
                    sChargeCounterValue = new Cell(vCntCounter);
                    sChargeAmountValue = new Cell(vDblAmountChar);

                    sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    sChargeCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    sChargeAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    sChargeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                    sChargeNameValue.setColspan(3);

                    table.addCell(sChargeNameValue);
                    table.addCell(sChargeRateValue);
                    table.addCell(sChargeCounterValue);
                    table.addCell(sChargeAmountValue);
                    table.addCell(sChargeTotalValue);
                }
                Cell sChargeFeeTotalCaption=new Cell(new Paragraph(20,"Total",f3));
                Cell sChargeFeeTotalValue=new Cell(new Paragraph(20,Functions.round(grossTotalFee,2),f3));
                sChargeFeeTotalCaption.setColspan(6);
                sChargeFeeTotalCaption.setHeader(true);
                sChargeFeeTotalCaption.setHorizontalAlignment(Element.ALIGN_LEFT);
                sChargeFeeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                table.addCell(sChargeFeeTotalCaption);
                table.addCell(sChargeFeeTotalValue);

                Cell sPayoutReportSummaryHeaderCell=new Cell(new Paragraph(20,"Summary",f2));
                sPayoutReportSummaryHeaderCell.setColspan(7);
                sPayoutReportSummaryHeaderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                sPayoutReportSummaryHeaderCell.setBackgroundColor(Color.GRAY);
                table.addCell(sPayoutReportSummaryHeaderCell);

                Cell sTotalProcessingAmountCaptionCell=new Cell("Total Processing Amount");
                totalProcessingAmount=totalProcessingAmount+totalProcessingMap.get(terminal);
                Cell sTotalProcessingAmountValueCell=new Cell(Functions.round(totalProcessingMap.get(terminal),2));
                sTotalProcessingAmountCaptionCell.setColspan(6);
                sTotalProcessingAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(sTotalProcessingAmountCaptionCell);
                table.addCell(sTotalProcessingAmountValueCell);

                Cell sTotalFeesCaptionCell=new Cell("Total Fees");
                totalChargesFee=totalChargesFee+grossTotalFee;
                Cell sTotalFeesValueCell=new Cell(isValidAmount(Functions.round(grossTotalFee,2)));
                sTotalFeesCaptionCell.setColspan(6);
                sTotalFeesValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(sTotalFeesCaptionCell);
                table.addCell(sTotalFeesValueCell);

                Cell sTotalChargebackReversalCaptionCell=new Cell("Total Chargeback/Reversal");
                totalReverseChargeback=totalReverseChargeback+refundChargebackMap.get(terminal);
                Cell sTotalChargebackReversalValueCell=new Cell(isValidAmount(Functions.round(refundChargebackMap.get(terminal), 2)));
                sTotalChargebackReversalCaptionCell.setColspan(6);
                sTotalChargebackReversalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(sTotalChargebackReversalCaptionCell);
                table.addCell(sTotalChargebackReversalValueCell);

               /* Cell sGeneratedReserveCaptionCell=new Cell("Generated Reserve");
                Cell sGeneratedReserveValueCell=new Cell(isValidAmount(Functions.round(0.00,2)));
                sGeneratedReserveCaptionCell.setColspan(6);
                sGeneratedReserveValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(sGeneratedReserveCaptionCell);
                table.addCell(sGeneratedReserveValueCell);*/

                Cell sGrossAmountCaptionCell=new Cell(new Paragraph(20,"Gross Amount",f3));
                double grossAmount=totalProcessingMap.get(terminal)-grossTotalFee-refundChargebackMap.get(terminal);
                Cell sGrossAmountValueCell=new Cell(new Paragraph(20,Functions.round(grossAmount,2),f3));
                sGrossAmountCaptionCell.setColspan(6);
                sGrossAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(sGrossAmountCaptionCell);
                table.addCell(sGrossAmountValueCell);

                Cell sMiscellaneousChargesAmountCaptionCell=new Cell("Miscellaneous Adjustment");
                Cell sMiscellaneousChargesValueCell=new Cell(Functions.round(miscellaneousAdjustment.get(terminal),2));
                sMiscellaneousChargesAmountCaptionCell.setColspan(6);
                sMiscellaneousChargesValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(sMiscellaneousChargesAmountCaptionCell);
                table.addCell(sMiscellaneousChargesValueCell);

                Cell sPreviousBalanceAmountCaptionCell=new Cell("Previous Balance Amount");
                Cell sPreviousBalanceAmountValueCell=new Cell(Functions.round(0.00,2));
                sPreviousBalanceAmountCaptionCell.setColspan(6);
                sPreviousBalanceAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(sPreviousBalanceAmountCaptionCell);
                table.addCell(sPreviousBalanceAmountValueCell);

                Cell sRefundedRollingReserveCaptionCell=new Cell("Refunded Rolling Reserve");
                double reserveRefundVO=0.00;
                if(reserveRefundVOHashMap!=null && reserveRefundVOHashMap.size()>0){
                    reserveRefundVO=reserveRefundVOHashMap.get(terminal);
                }
                Cell sRefundedRollingReserveValueCell=new Cell(Functions.round(reserveRefundVO,2));
                sRefundedRollingReserveCaptionCell.setColspan(6);
                sRefundedRollingReserveValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(sRefundedRollingReserveCaptionCell);
                table.addCell(sRefundedRollingReserveValueCell);

                Cell sTotalAmountCaptionCell=new Cell(new Paragraph(20,"Total",f3));
                Cell sTotalAmountValueCell=new Cell(new Paragraph(20,Functions.round(totalAmountFundedToBankMap.get(terminal),2),f3));
                sTotalAmountCaptionCell.setColspan(6);
                sTotalAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(sTotalAmountCaptionCell);
                table.addCell(sTotalAmountValueCell);

                Cell sTotalAmountFundedCaptionCell=new Cell(new Paragraph(20,"Total Amount Funded["+currency+"]",f3));
                totalAmountFunded=totalAmountFunded+totalAmountFundedToBankMap.get(terminal);
                Cell sTotalAmountFundedValueCell=new Cell(new Paragraph(20,Functions.round(totalAmountFundedToBankMap.get(terminal),2),f3));
                sTotalAmountFundedCaptionCell.setColspan(6);
                sTotalAmountFundedValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sTotalAmountFundedCaptionCell.setBackgroundColor(Color.white.brighter());
                sTotalAmountFundedValueCell.setBackgroundColor(Color.white.brighter());
                table.addCell(sTotalAmountFundedCaptionCell);
                table.addCell(sTotalAmountFundedValueCell);

                Cell sTotalAmountFundedSettlementCaptionCell=new Cell(new Paragraph(20,"Total Amount Funded["+settlementCurrencyMap.get(terminal)+"]",f3));
                //totalAmountAfterConversion=totalAmountAfterConversion+totalAmountAfterExchangeRateMap.get(terminal);
                Cell sTotalExchangeAmountValueCell=new Cell(Functions.round(totalAmountFundedToBankMap.get(terminal),2));
                Cell sTotalExchangeRateValueCell=new Cell(Functions.round(exchangeRate.get(terminal),2));
                Cell sTotalAmountFundedSettlementValueCell=new Cell(new Paragraph(20,Functions.round(totalAmountAfterExchangeRateMap.get(terminal),2),f3));
                sTotalAmountFundedSettlementCaptionCell.setColspan(4);

                sTotalAmountFundedSettlementValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sTotalExchangeAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sTotalExchangeRateValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sTotalAmountFundedSettlementCaptionCell.setBackgroundColor(Color.white.brighter());
                sTotalAmountFundedSettlementValueCell.setBackgroundColor(Color.white.brighter());

                table.addCell(sTotalAmountFundedSettlementCaptionCell);
                table.addCell(sTotalExchangeAmountValueCell);
                table.addCell(sTotalExchangeRateValueCell);
                table.addCell(sTotalAmountFundedSettlementValueCell);
            }

            Cell sReportHeader=new Cell(new Paragraph(20,"Gross Final Summary",f2));
            sReportHeader.setColspan(7);
            sReportHeader.setHorizontalAlignment(Element.ALIGN_LEFT);
            sReportHeader.setBackgroundColor(bgColor);
            table.addCell(sReportHeader);

            Cell sTotalProcessingAmountCaptionCell=new Cell("Total Processing Amount");
            Cell sTotalProcessingAmountValueCell=new Cell(Functions.round(totalProcessingAmount,2));
            sTotalProcessingAmountCaptionCell.setColspan(6);
            sTotalProcessingAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sTotalProcessingAmountCaptionCell);
            table.addCell(sTotalProcessingAmountValueCell);

            Cell sTotalFeesCaptionCell=new Cell("Total Fees");
            Cell sTotalFeesValueCell=new Cell(isValidAmount(Functions.round(totalChargesFee,2)));
            sTotalFeesCaptionCell.setColspan(6);
            sTotalFeesValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sTotalFeesCaptionCell);
            table.addCell(sTotalFeesValueCell);

            Cell sTotalAmountFundedCaptionCell=new Cell(new Paragraph(20,"Total Amount Funded["+currency+"]",f3));
            Cell sTotalAmountFundedValueCell=new Cell(new Paragraph(20,Functions.round(totalAmountFunded,2),f3));
            sTotalAmountFundedCaptionCell.setColspan(6);
            sTotalAmountFundedValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            sTotalAmountFundedCaptionCell.setBackgroundColor(Color.white.brighter());
            sTotalAmountFundedValueCell.setBackgroundColor(Color.white.brighter());
            table.addCell(sTotalAmountFundedCaptionCell);
            table.addCell(sTotalAmountFundedValueCell);

            Map.Entry<String,String> entry = settlementCurrencyMap.entrySet().iterator().next();
            String setCurrency  = entry.getValue();

            Cell sTotalAmountFundedSettlementCaptionCell=new Cell(new Paragraph(20,"Total Amount Funded["+setCurrency+"]",f3));
            Cell sTotalExchangeAmountValueCell=new Cell(Functions.round(totalAmountFunded,2));
            Map.Entry<String,Double> entry1 = exchangeRate.entrySet().iterator().next();
            double exchangerate = entry1.getValue();

            Cell sTotalExchangeRateValueCell=new Cell(Functions.round(exchangerate,2));
            totalAmountAfterConversion=totalAmountFunded*exchangerate;
            Cell sTotalAmountFundedSettlementValueCell=new Cell(new Paragraph(20,Functions.round(totalAmountAfterConversion,2),f3));
            sTotalAmountFundedSettlementCaptionCell.setColspan(4);

            sTotalAmountFundedSettlementValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            sTotalExchangeAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            sTotalExchangeRateValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            sTotalAmountFundedSettlementCaptionCell.setBackgroundColor(Color.white.brighter());
            sTotalAmountFundedSettlementValueCell.setBackgroundColor(Color.white.brighter());

            table.addCell(sTotalAmountFundedSettlementCaptionCell);
            table.addCell(sTotalExchangeAmountValueCell);
            table.addCell(sTotalExchangeRateValueCell);
            table.addCell(sTotalAmountFundedSettlementValueCell);



            document.add(table);
            document.close();
        }
        catch (Exception ex){
            logger.error(ex);
        }
        return reportFileName;
    }

    public static List<AgentCommissionVO> loadchargenameAgent()
    {
        return PayoutDAO.loadchargenameAgent();
    }

}
