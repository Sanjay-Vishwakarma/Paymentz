package com.manager;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.logicboxes.util.ApplicationProperties;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.manager.dao.*;
import com.manager.enums.*;
import com.manager.utils.AccountUtil;
import com.manager.vo.*;
import com.manager.vo.merchantmonitoring.DateVO;
import com.manager.vo.payoutVOs.*;
import com.payment.Enum.EU_Country;
import com.payment.PZTransactionStatus;
import com.payment.exceptionHandler.PZDBViolationException;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Admin on 9/5/2020.
 */
public class ConsolidatedPayoutManager
{
    private final static String AGENT_PAYOUT_REPORT_FILE_PATH = ApplicationProperties.getProperty("AGENT_PAYOUT_REPORT_FILE_PATH");
    private final static String SETTLEMENT_FILE_PATH = ApplicationProperties.getProperty("SETTLEMENT_FILE_PATH");
    private final static String PAYOUT_REPORT_FILE_PATH = ApplicationProperties.getProperty("PAYOUT_REPORT_FILE_PATH");
    private final static String PARTNER_LOGO_PATH = ApplicationProperties.getProperty("PARTNER_LOGO_PATH");
    Logger logger = new Logger(ConsolidatedPayoutManager.class.getName());
    PayoutDAO payoutDAO = new PayoutDAO();
    Functions functions = new Functions();
    PayoutManager payoutManager = new PayoutManager();
    AccountUtil accountUtil = new AccountUtil();
    ConsolidatedPayoutDAO consolidatedPayoutDAO = new ConsolidatedPayoutDAO();

    public List<String> consolidatedMerchantPayoutReportBasedOnBankWire(String bankWireId, HashMap<String, List<TerminalVO>> getListOfMember, List<TerminalVO> pendingList) throws Exception
    {
        String cyclememberlist = null;
        SettlementDateVO settlementDateVO = null;
        RollingReserveDateVO rollingReserveDateVO = null;
        List<String> stringList = new ArrayList<String>();
        List<TerminalVO> successList = new ArrayList<>();

        String issettlementcronexceuted = "Y";
        String ispayoutcronexcuted = "N";

        logger.debug("Payout Cron Is Going To Execute:::::" + getListOfMember);
        BankWireManagerVO bankWireManagerVO = payoutDAO.getBankWireDetails(bankWireId, issettlementcronexceuted, ispayoutcronexcuted);
        if (bankWireManagerVO == null)
        {
            cyclememberlist = "0:0:0:0:Failed:No New Wire Received From  Bank Manager";
            stringList.add(cyclememberlist);
            logger.debug(cyclememberlist);
            return stringList;
        }

        logger.debug("Going To Execute=====" + bankWireManagerVO.getBankwiremanagerId());
        String accountId = bankWireManagerVO.getAccountId();

        DateVO dateVO = new DateVO();
        dateVO.setStartDate(bankWireManagerVO.getServer_start_date());
        dateVO.setEndDate(bankWireManagerVO.getServer_end_date());

        Set<String> key = getListOfMember.keySet();
        for (String memberId : key)
        {
            List<TerminalVO> terminalVOList = getListOfMember.get(memberId);
            rollingReserveDateVO = new RollingReserveDateVO();
            rollingReserveDateVO.setRollingReserveEndDate(bankWireManagerVO.getRollingreservereleasedateupto());
            rollingReserveDateVO.setRollingReserveStartDate(bankWireManagerVO.getRollingreservereleaseStartdate());
            settlementDateVO = new SettlementDateVO();
            /*for (TerminalVO vo : terminalVOList){
                if (bankWireManagerVO.getBankwiremanagerId().equalsIgnoreCase(bankWireId) && accountId.equalsIgnoreCase(vo.getAccountId())){
                    settlementDateVO.setSettlementcycleNumber(bankWireManagerVO.getBankwiremanagerId());
                }
            }*/
            settlementDateVO.setSettlementcycleNumber(bankWireManagerVO.getBankwiremanagerId());
            settlementDateVO.setSettlementEndDate(bankWireManagerVO.getServer_end_date());
            settlementDateVO.setDeclinedEndDate(bankWireManagerVO.getDeclinedcoveredupto());
            settlementDateVO.setReversedEndDate(bankWireManagerVO.getReversedCoveredUpto());
            settlementDateVO.setChargebackEndDate(bankWireManagerVO.getChargebackcoveredupto());
            settlementDateVO.setSettlementStartDate(bankWireManagerVO.getServer_start_date());
            if (functions.isValueNull(bankWireManagerVO.getDeclinedcoveredStartdate()))
            {
                settlementDateVO.setDeclinedStartDate(bankWireManagerVO.getDeclinedcoveredStartdate());
            }
            else
            {
                settlementDateVO.setDeclinedStartDate(bankWireManagerVO.getServer_start_date());
            }

            if (functions.isValueNull(bankWireManagerVO.getReversedCoveredStartdate()))
            {
                settlementDateVO.setReversedStartDate(bankWireManagerVO.getReversedCoveredStartdate());
            }
            else
            {
                settlementDateVO.setReversedStartDate(bankWireManagerVO.getServer_start_date());
            }

            if (functions.isValueNull(bankWireManagerVO.getChargebackcoveredStartdate()))
            {
                settlementDateVO.setChargebackStartDate(bankWireManagerVO.getChargebackcoveredStartdate());
            }
            else
            {
                settlementDateVO.setChargebackStartDate(bankWireManagerVO.getServer_start_date());
            }

            WeeklyPayoutReportVO weeklyPayoutReportVO = new WeeklyPayoutReportVO();
            weeklyPayoutReportVO.setSettlementDateVO(settlementDateVO);
            weeklyPayoutReportVO.setRollingReserveDateVO(rollingReserveDateVO);
            weeklyPayoutReportVO.setPendingList(pendingList);
            weeklyPayoutReportVO.setRequestTerminalVO(terminalVOList);
            String status = generateWeeklyPayoutReportBasedOnMerchantConsolidated(weeklyPayoutReportVO, terminalVOList, memberId, accountId);
            String terminalIds = "";
            String cycleIds = "";
            String accountIds = "";
            int i = 0;
            for (TerminalVO terminalVO : terminalVOList)
            {
                if (i == (terminalVOList.size() - 1)){
                    terminalIds = terminalIds + terminalVO.getTerminalId();
                    cycleIds = cycleIds + terminalVO.getWireId();
                    accountIds = accountIds + terminalVO.getAccountId();}
                else{
                    terminalIds = terminalIds + terminalVO.getTerminalId() + ",";
                    cycleIds = cycleIds + terminalVO.getWireId() + ",";
                    accountIds = accountIds + terminalVO.getAccountId() + ",";}
                i++;
            }
            if ("success".equalsIgnoreCase(status))
            {
                logger.error("successList::::" + successList.size());
                cyclememberlist = cycleIds + ":" + memberId + ":" + accountIds + ":" + terminalIds + ":" + status + ":" + "Wire Is Created Successfully";
            }
            else
            {
                cyclememberlist = cycleIds + ":" + memberId + ":" + accountIds + ":" + terminalIds + ":" + status + ":" + "Wire Is Creation Failed";
            }
            stringList.add(cyclememberlist);
            logger.debug(stringList);
            continue;
        }
        return stringList;
    }

    private String generateWeeklyPayoutReportBasedOnMerchantConsolidated(WeeklyPayoutReportVO weeklyPayoutReportVO, List<TerminalVO> terminalVOList, String memberId, String accountId) throws Exception
    {
        SettlementDateVO settlementDateVO = weeklyPayoutReportVO.getSettlementDateVO();
        RollingReserveDateVO rollingReserveDateVO = weeklyPayoutReportVO.getRollingReserveDateVO();
        HashMap<String, String> dynamicCountAmountMap = weeklyPayoutReportVO.getDynamicCountAmountMap();

        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String rollingReserveStartDate = targetFormat.format(targetFormat.parse(rollingReserveDateVO.getRollingReserveStartDate()));
        String rollingReserveEndDate = targetFormat.format(targetFormat.parse(rollingReserveDateVO.getRollingReserveEndDate()));

        String settlementStartDate = targetFormat.format(targetFormat.parse(settlementDateVO.getSettlementStartDate()));
        String settlementEndDate = targetFormat.format(targetFormat.parse(settlementDateVO.getSettlementEndDate()));

        rollingReserveDateVO.setRollingReserveStartDate(rollingReserveStartDate);
        rollingReserveDateVO.setRollingReserveEndDate(rollingReserveEndDate);

        settlementDateVO.setSettlementStartDate(settlementStartDate);
        settlementDateVO.setSettlementEndDate(settlementEndDate);

        String tableName = "";
        String resStatus = "Failed";
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        GatewayType gatewayType = GatewayTypeService.getGatewayType(account.getPgTypeId());

        String currency = gatewayType.getCurrency();
        long totalSuccessCount = 0;
        double authfailedAmount = 0.00;
        double settledAmount = 0.0;
        double wireChargeAmount = 0.00;
        double statementChargeAmount = 0.00;

        double grossChargesAmount = 0.00;
        double serviceTaxChargesAmount = 0.00;
        double reserveReleaseAmount = 0.0;
        //double previousBalanceAmount=0.00;
        double grossSetupFee = 0.0;
        long verifyOrderCount = 0;
        long refundAlertCount = 0;
        long retrivalRequestCount = 0;
        long fraudulentTransactionCount = 0;

        double otherChargesAmount = 0.00;

        WireAmountVO wireAmountVO = new WireAmountVO();
        HashMap<String, List<ChargeDetailsVO>> chargeDetailsMapOFVOs = new LinkedHashMap<String, List<ChargeDetailsVO>>();
        HashMap<String, ReserveGeneratedVO> reserveGeneratedVOHashMap = new LinkedHashMap<String, ReserveGeneratedVO>();
        HashMap<String, ReserveRefundVO> reserveRefundVOHashMap = new LinkedHashMap<String, ReserveRefundVO>();
        HashMap<String, CalculatedReserveRefundVO> calculatedReserveRefundVOHashMap = new LinkedHashMap<String, CalculatedReserveRefundVO>();
        HashMap<String, HashMap<String, TotalFeesChargesVO>> ServiceFeesChargesTerminalwise = new LinkedHashMap<String, HashMap<String, TotalFeesChargesVO>>();
        HashMap<String, HashMap<String, OtherChargesVO>> OtherChargesTerminalwise = new LinkedHashMap<String, HashMap<String, OtherChargesVO>>();
        HashMap<String, HashMap<String, MerchantRandomChargesVO>> RandomeChargesTerminalwise = new LinkedHashMap<String, HashMap<String, MerchantRandomChargesVO>>();
        HashMap<String, HashMap<String, ServiceTaxChargeVO>> ServicetaxTerminalwise = new LinkedHashMap<String, HashMap<String, ServiceTaxChargeVO>>();
        HashMap<String, HashMap<String, GrossChargeVO>> GrossChargeterminalWise = new LinkedHashMap<String, HashMap<String, GrossChargeVO>>();

        HashMap<String, List<StatementChargeVO>> statementChargeVOHashMap = new LinkedHashMap<String, List<StatementChargeVO>>();
        HashMap<String, List<SettlementExpensesVO>> settlementExpensesVOHashMap = new LinkedHashMap<String, List<SettlementExpensesVO>>();
        HashMap<String, List<WireChargeVO>> wireChargeVOHashMap = new LinkedHashMap<String, List<WireChargeVO>>();
        HashMap<String, WireChargeVO> wireChargeVOMap = new LinkedHashMap<String, WireChargeVO>();
        HashMap<String, StatementChargeVO> statementChargeMap = new LinkedHashMap<String, StatementChargeVO>();
        HashMap<String, SettlementExpensesVO> settlementExpensesVOMap = new LinkedHashMap<String, SettlementExpensesVO>();
        HashMap<String, ChargeVO> chargeVOHashMap = new LinkedHashMap<String, ChargeVO>();
        HashMap<String, ChargeDetailsVO> chargebackReverseVOMap = new LinkedHashMap<String, ChargeDetailsVO>();

        HashMap<String, Double> totalProcessingAmountMap = new LinkedHashMap<String, Double>();
        HashMap<String, Double> reverseAmountMap = new LinkedHashMap<String, Double>();
        HashMap<String, Double> chargebackAmountMap = new LinkedHashMap<String, Double>();
        HashMap<String, Double> payoutAmountMap = new LinkedHashMap<String, Double>();
        HashMap<String, Double> refundReverseMap = new LinkedHashMap<String, Double>();
        HashMap<String, Double> chargebackReversedMap = new LinkedHashMap<String, Double>();
        //HashMap<String,Double> miscellaneousAdjustmentMap=new LinkedHashMap<String,Double>();
        HashMap<String, Double> previousBalanceAmountMap = new LinkedHashMap<String, Double>();

        HashMap<String, GrossChargeVO> grossChargeVOMap = new LinkedHashMap<String, GrossChargeVO>();
        HashMap<String, GrossChargeVO> grossChargeVOMapFinal = new LinkedHashMap<String, GrossChargeVO>();

        HashMap<String, ServiceTaxChargeVO> serviceTaxChargeVOHashMap = new LinkedHashMap<String, ServiceTaxChargeVO>();
        HashMap<String, ServiceTaxChargeVO> serviceTaxChargeVOHashMapFinal = new LinkedHashMap<String, ServiceTaxChargeVO>();
        HashMap<String, Boolean> minpayout = new LinkedHashMap<String, Boolean>();
        HashMap<String, TotalFeesChargesVO> totalFeesChargeVOMap = new LinkedHashMap<String, TotalFeesChargesVO>();

        HashMap<String, OtherChargesVO> otherChargeVOMap = new LinkedHashMap<String, OtherChargesVO>();


        WireChargeVO wireChargeVO = null;
        SettlementExpensesVO settlementExpensesVO = null;
        SetupChargeVO setupChargeVO = null;
        ChargeDetailsVO chargeDetailsVO = null;
        ReserveGeneratedVO reserveGeneratedVO = null;
        CalculatedReserveRefundVO calculatedReserveRefundVO = null;
        StatementChargeVO statementChargeVO = null;
        ChargeVO chargeVO1 = null;
        ChargeDetailsVO chargebackReverseVO = null;


        List<WireChargeVO> wireChargeVOs = new ArrayList<>();
        List<StatementChargeVO> statementChargeVOs = new ArrayList<>();
        List<SettlementExpensesVO> settlementExpensesVOs = new ArrayList<>();


        String currentDate = Functions.convertDateDBFormat(Calendar.getInstance().getTime());
        ChargeManager chargeManager = new ChargeManager();
        HashMap<String, String> settlementCurrencyMap = new HashMap<>();
        HashMap<String, Double> exchangeRateMap = new HashMap<>();
        String settlementCurrency = null;
        try
        {
            for (TerminalVO terminalVO : terminalVOList)
            {
                chargeVO1 = new ChargeVO();
                chargebackReverseVO = new ChargeDetailsVO();
                HashMap<String, TotalFeesChargesVO> totalFeesChargesVOHashMapFinal = new LinkedHashMap<String, TotalFeesChargesVO>();
                HashMap<String, OtherChargesVO> otherChargesVOHashMapFinal = new LinkedHashMap<String, OtherChargesVO>();
                HashMap<String, MerchantRandomChargesVO> merchantRandomChargesVOHashMap = new LinkedHashMap<String, MerchantRandomChargesVO>();

                double reversedAmount = 0.0;
                double reserveGeneratedAmount = 0.0;
                double payoutAmount = 0.0;
                double chargebackAmount = 0.0;
                double totalProcessingAmount = 0.0;
                double serviceTaxonFeesChargeAmount = 0.00;
                double grossFee = 0.0;
                String latestSetupFeeDate = null;

                payoutDAO.setUnpaidBalanceAmountMWMOnTerminal(terminalVO, wireAmountVO);

                //Calculate The previous balance Amount consider only the unpaid column field amount
                double previousBalanceAmount = wireAmountVO.getUnpaidBalanceAmount();
                List<ChargeDetailsVO> chargeDetailsVOList = new ArrayList<>();
                List<ChargeVO> chargeVOs = payoutDAO.getChargesAsPerTerminal(terminalVO);
                List<MerchantRandomChargesVO> merchantRandomChargesVOList = chargeManager.getMerchantRandomChargesList(settlementDateVO.getSettlementcycleNumber(), terminalVO.getMemberId(), terminalVO.getTerminalId());
                HashMap<String, MerchantRandomChargesVO> merchantRandomChargesVOListNew = chargeManager.getMerchantRandomChargesListVO(settlementDateVO.getSettlementcycleNumber(), terminalVO.getMemberId(), terminalVO.getTerminalId());

                settlementCurrency = terminalVO.getSettlementCurrency();
                settlementCurrencyMap.put(terminalVO.getTerminalId(), settlementCurrency);

                TransactionSummaryVO transactionSummaryVO = payoutManager.getTotalSuccessCountAmountByTerminal(terminalVO, settlementDateVO);
                double chargebackReversalAmount = transactionSummaryVO.getChargebackReversedAmount();
                long chargebackReverseCount = transactionSummaryVO.getCountOfChargebackReversed();
                chargebackReverseVO.setCount(chargebackReverseCount);
                chargebackReverseVO.setAmount(chargebackReversalAmount);

                for (ChargeVO chargeVO : chargeVOs)
                {
                    String terminalName = chargeVO.getTerminalid() + "-" + chargeVO.getPaymentName() + "-" + chargeVO.getCardType();
                    if (Charge_keyword.TotalReserveGenerated.toString().equals(chargeVO.getKeyword()) || Charge_keyword.TotalReserveRefunded.toString().equals(chargeVO.getKeyword()) || Charge_keyword.CalculatedReserveRefund.toString().equals(chargeVO.getKeyword()) || Charge_keyword.Wire.toString().equals(chargeVO.getKeyword()) || Charge_keyword.Statement.toString().equals(chargeVO.getKeyword()) || Charge_keyword.GrossBalanceAmount.toString().equals(chargeVO.getKeyword()) || Charge_keyword.Setup.toString().equals(chargeVO.getKeyword()) || Charge_keyword.ServiceTax.toString().equals(chargeVO.getKeyword()) || Charge_keyword.NetFinalAmount.toString().equals(chargeVO.getKeyword()) || Charge_keyword.TotalFees.toString().equals(chargeVO.getKeyword()) || Charge_keyword.OtherCharges.toString().equals(chargeVO.getKeyword()))
                    {
                        String chargeVersionRate = chargeManager.getMerchantChargeVersionRate(chargeVO.getMappingid(), currentDate);
                        if (chargeVersionRate != null)
                        {
                            chargeVO.setChargevalue(chargeVersionRate);
                        }

                        if (Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.TotalReserveGenerated.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
                        {
                            double reserveGeneratedChargeFee = 0.00;
                            reserveGeneratedVO = new ReserveGeneratedVO();
                            reserveGeneratedVO.setCount(transactionSummaryVO.getTotalProcessingCount());
                            reserveGeneratedVO.setChargeName(chargeVO.getChargename());
                            reserveGeneratedVO.setValueType(chargeVO.getValuetype());
                            reserveGeneratedVO.setChargeValue(chargeVO.getChargevalue());
                            reserveGeneratedVO.setAmount(transactionSummaryVO.getTotalProcessingAmount());
                            reserveGeneratedChargeFee = (transactionSummaryVO.getTotalProcessingAmount() * Double.valueOf(chargeVO.getChargevalue())) / 100;
                            reserveGeneratedVO.setTotal(Functions.roundDBL(reserveGeneratedChargeFee, 2));
                            reserveGeneratedAmount = reserveGeneratedAmount + reserveGeneratedChargeFee;
                            reserveGeneratedVOHashMap.put(terminalName, reserveGeneratedVO);
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
                            reserveRefundVOHashMap.put(terminalName, refundVO);
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
                            statementChargeVO = new StatementChargeVO();
                            statementChargeVO.setCount(currentCounter);
                            statementChargeVO.setChargeName(chargeVO.getChargename());
                            statementChargeVO.setChargeValue(chargeVO.getChargevalue());
                            statementChargeVO.setValueType(chargeVO.getValuetype());
                            statementChargeVO.setAmount(0.00);
                            if (statementChargeAmount > 0.00)
                            {
                                statementChargeAmount = statementChargeAmount + currentCounter * Double.valueOf(chargeVO.getChargevalue());
                            }
                            else
                                statementChargeAmount = currentCounter * Double.valueOf(chargeVO.getChargevalue());
                            statementChargeVO.setTotal(Functions.roundDBL(currentCounter * Double.valueOf(chargeVO.getChargevalue()), 2));
                        /*statementChargeVOs.add(statementChargeVO);*/
                            if (statementChargeVOHashMap != null && statementChargeVOHashMap.size() > 0 && statementChargeVOHashMap.containsKey(terminalName))
                            {
                                statementChargeMap.put(chargeVO.getChargename(), statementChargeVO);
                                statementChargeVOs.add(statementChargeVO);
                                statementChargeVOHashMap.put(terminalName, statementChargeVOs);
                            }
                            else if (statementChargeVOHashMap != null && statementChargeVOHashMap.size() > 0 && !statementChargeVOHashMap.containsKey(terminalName) && !statementChargeMap.containsKey(chargeVO.getChargename()))
                            {
                                statementChargeMap.put(chargeVO.getChargename(), statementChargeVO);
                                statementChargeVOs = new ArrayList<>();
                                statementChargeVOs.add(statementChargeVO);
                                statementChargeVOHashMap.put(terminalName, statementChargeVOs);
                            }
                            else if (statementChargeVOHashMap != null && statementChargeVOHashMap.size() > 0 && !statementChargeVOHashMap.containsKey(terminalName) && statementChargeMap.containsKey(chargeVO.getChargename()))
                            {
                                continue;
                            }
                            else
                            {
                                statementChargeMap.put(chargeVO.getChargename(), statementChargeVO);
                                statementChargeVOs.add(statementChargeVO);
                                statementChargeVOHashMap.put(terminalName, statementChargeVOs);
                            }
                        }
                        else if (chargeVO.getCategory().equals(Charge_category.Others.toString()) && chargeVO.getKeyword().equals(Charge_keyword.Wire.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString()))
                        {
                            long currentCounter = 1;
                            wireChargeVO = new WireChargeVO();
                            wireChargeVO.setCount(currentCounter);
                            wireChargeVO.setChargeName(chargeVO.getChargename());
                            wireChargeVO.setChargeValue(chargeVO.getChargevalue());
                            wireChargeVO.setValueType(chargeVO.getValuetype());
                            wireChargeVO.setAmount(0.00);
                            if (wireChargeAmount > 0.00)
                            {
                                wireChargeAmount = wireChargeAmount + currentCounter * Double.valueOf(chargeVO.getChargevalue());
                            }
                            else
                                wireChargeAmount = currentCounter * Double.valueOf(chargeVO.getChargevalue());
                            wireChargeVO.setTotal(Functions.roundDBL(currentCounter * Double.valueOf(chargeVO.getChargevalue()), 2));
                            if (wireChargeVOHashMap != null && wireChargeVOHashMap.size() > 0 && wireChargeVOHashMap.containsKey(terminalName))
                            {
                                wireChargeVOMap.put(chargeVO.getChargename(), wireChargeVO);
                                wireChargeVOs.add(wireChargeVO);
                                wireChargeVOHashMap.put(terminalName, wireChargeVOs);
                            }
                            else if (wireChargeVOHashMap != null && wireChargeVOHashMap.size() > 0 && !wireChargeVOHashMap.containsKey(terminalName) && !wireChargeVOMap.containsKey(chargeVO.getChargename()))
                            {
                                wireChargeVOMap.put(chargeVO.getChargename(), wireChargeVO);
                                wireChargeVOs = new ArrayList<>();
                                wireChargeVOs.add(wireChargeVO);
                                wireChargeVOHashMap.put(terminalName, wireChargeVOs);
                            }
                            else if (wireChargeVOHashMap != null && wireChargeVOHashMap.size() > 0 && !wireChargeVOHashMap.containsKey(terminalName) && wireChargeVOMap.containsKey(chargeVO.getChargename()))
                            {
                                continue;
                            }
                            else
                            {
                                wireChargeVOMap.put(chargeVO.getChargename(), wireChargeVO);
                                wireChargeVOs.add(wireChargeVO);
                                wireChargeVOHashMap.put(terminalName, wireChargeVOs);
                            }
                        }
                        if (chargeVO.getCategory().equals(Charge_category.Success.toString()) && chargeVO.getKeyword().equals(Charge_keyword.NetFinalAmount.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Amount.toString()))
                        {
                            settlementExpensesVO = new SettlementExpensesVO();
                            settlementExpensesVO.setChargeName(chargeVO.getChargename());
                            settlementExpensesVO.setChargeValue(chargeVO.getChargevalue());
                            settlementExpensesVO.setValueType(chargeVO.getValuetype());
                            settlementExpensesVO.setChargeid(chargeVO.getChargeid());
                            settlementExpensesVO.setNegativebalance(chargeVO.getNegativebalance());
                            if (settlementExpensesVOHashMap != null && settlementExpensesVOHashMap.size() > 0 && settlementExpensesVOHashMap.containsKey(terminalName))
                            {
                                settlementExpensesVOMap.put(chargeVO.getChargename(), settlementExpensesVO);
                                settlementExpensesVOs.add(settlementExpensesVO);
                                settlementExpensesVOHashMap.put(terminalName, settlementExpensesVOs);
                            }
                            else if (settlementExpensesVOHashMap != null && settlementExpensesVOHashMap.size() > 0 && !settlementExpensesVOHashMap.containsKey(terminalName) && !settlementExpensesVOMap.containsKey(chargeVO.getChargename()))
                            {
                                settlementExpensesVOMap.put(chargeVO.getChargename(), settlementExpensesVO);
                                settlementExpensesVOs = new ArrayList<>();
                                settlementExpensesVOs.add(settlementExpensesVO);
                                settlementExpensesVOHashMap.put(terminalName, settlementExpensesVOs);
                            }
                            else if (settlementExpensesVOHashMap != null && settlementExpensesVOHashMap.size() > 0 && !settlementExpensesVOHashMap.containsKey(terminalName)&& settlementExpensesVOMap.containsKey(chargeVO.getChargename()))
                            {
                                continue;
                            }
                            else
                            {
                                settlementExpensesVOMap.put(chargeVO.getChargename(), settlementExpensesVO);
                                settlementExpensesVOs.add(settlementExpensesVO);
                                settlementExpensesVOHashMap.put(terminalName, settlementExpensesVOs);
                            }
                        }
                        else if (chargeVO.getCategory().equals(Charge_category.Others.toString()) && chargeVO.getKeyword().equals(Charge_keyword.Setup.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString()))
                        {
                            setupChargeVO = payoutManager.applySetupChargeUptoSettledDate(chargeVO, terminalVO, settlementDateVO);
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
                        else if (chargeVO.getCategory().equals(Charge_category.Others.toString()) && chargeVO.getKeyword().equals(Charge_keyword.TotalFees.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Amount.toString()))
                        {
                            TotalFeesChargesVO totalFeesChargesVO = new TotalFeesChargesVO();
                            totalFeesChargesVO.setChargeName(chargeVO.getChargename());
                            totalFeesChargesVO.setChargeValue(chargeVO.getChargevalue());
                            totalFeesChargesVO.setValueType(chargeVO.getValuetype());
                            totalFeesChargeVOMap.put(chargeVO.getChargename(), totalFeesChargesVO);
                        }
                        else if (chargeVO.getCategory().equals(Charge_category.Others.toString()) && chargeVO.getKeyword().equals(Charge_keyword.OtherCharges.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Amount.toString()))
                        {
                            OtherChargesVO otherChargesVO = new OtherChargesVO();
                            otherChargesVO.setChargeName(chargeVO.getChargename());
                            otherChargesVO.setChargeValue(chargeVO.getChargevalue());
                            otherChargesVO.setValueType(chargeVO.getValuetype());
                            otherChargeVOMap.put(chargeVO.getChargename(), otherChargesVO);
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

                        chargeDetailsVO = payoutManager.applyChargeOnTerminal(terminalVO, chargeVO, settlementDateVO, tableName, transactionSummaryVO, dynamicCountAmountMap);
                        if (chargeDetailsVO.getChargeName() != null)
                        {
                            grossFee = grossFee + chargeDetailsVO.getTotal();
                            chargeDetailsVOList.add(chargeDetailsVO);
                            chargeDetailsMapOFVOs.put(terminalName, chargeDetailsVOList);
                            if (chargeVO.getKeyword().equals(Charge_keyword.Reversed.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString()))
                            {
                                reversedAmount = reversedAmount + chargeDetailsVO.getAmount();
                            }
                            if (chargeVO.getKeyword().equals(Charge_keyword.Payout.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Amount.toString()))
                            {
                                payoutAmount = payoutAmount + chargeDetailsVO.getAmount();
                            }
                            if (chargeVO.getKeyword().equals(Charge_keyword.Chargeback.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString()))
                            {
                                chargebackAmount = chargebackAmount + chargeDetailsVO.getAmount();
                                chargebackReverseVO.setChargeValue(chargeDetailsVO.getChargeValue());
                                chargebackReverseVO.setTotal(chargebackReverseCount * Double.valueOf(chargeDetailsVO.getChargeValue()));

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
                chargeVO1.setVerifyOrderCount(verifyOrderCount);
                chargeVO1.setRefundAlertCount(refundAlertCount);
                chargeVO1.setRetrivalRequestCount(retrivalRequestCount);
                chargeVO1.setFraudulentTransactionCount(fraudulentTransactionCount);
                chargeVO1.setLatestSetupFeeDate(latestSetupFeeDate);

                totalProcessingAmount = transactionSummaryVO.getTotalProcessingAmount();
                totalProcessingAmountMap.put(terminalVO.getTerminalId(), totalProcessingAmount);
                chargeVOHashMap.put(terminalVO.getTerminalId(), chargeVO1);

                reverseAmountMap.put(terminalVO.getTerminalId(), reversedAmount);
                chargebackAmountMap.put(terminalVO.getTerminalId(), chargebackAmount);
                payoutAmountMap.put(terminalVO.getTerminalId(), payoutAmount);
                previousBalanceAmountMap.put(terminalVO.getTerminalId(), previousBalanceAmount);

                double merchantRandomChargesGrossAmount = 0.00;
                double deductedAmount = 0.00;
                double addedAmount = 0.00;
            /*for (MerchantRandomChargesVO merchantRandomChargesVO : merchantRandomChargesVOList)
            {
                if ("Deducted".equals(merchantRandomChargesVO.getChargeType()))
                {
                    deductedAmount = deductedAmount + merchantRandomChargesVO.getChargeValue();
                }
                else
                {
                    addedAmount = addedAmount + merchantRandomChargesVO.getChargeValue();
                }
            }
            merchantRandomChargesGrossAmount = addedAmount - deductedAmount;*/

                //miscellaneousAdjustmentMap.put(terminalVO.getTerminalId(), merchantRandomChargesGrossAmount);

                //calculate gross amount here coz need to pass gross Amount to Calculate gross level charges
                String terminalName = terminalVO.getTerminalId() + "-" + terminalVO.getPaymentName() + "-" + terminalVO.getCardType();

                if (totalFeesChargeVOMap.size() > 0)
                {
                    String chargeName = null;
                    Set set = totalFeesChargeVOMap.keySet();
                    Iterator itr = set.iterator();
                    while (itr.hasNext())
                    {
                        chargeName = (String) itr.next();
                        TotalFeesChargesVO totalFeesChargesVO = calculateTotalFeesCharge(chargeName, totalFeesChargeVOMap, grossFee);
                        serviceTaxonFeesChargeAmount = serviceTaxonFeesChargeAmount + totalFeesChargesVO.getTotal();
                        totalFeesChargesVOHashMapFinal.put(totalFeesChargesVO.getChargeName(), totalFeesChargesVO);
                    }
                    ServiceFeesChargesTerminalwise.put(terminalName, totalFeesChargesVOHashMapFinal);
                }
                double grossDeduct = -1 * (grossFee + serviceTaxonFeesChargeAmount + chargebackAmount + reversedAmount + reserveGeneratedAmount);
                double grossAmount = totalProcessingAmount + grossDeduct;

                grossAmount = Functions.roundDBL(grossAmount, 2);
                if (merchantRandomChargesVOListNew.size() > 0)
                {
                    String chargeName = null;
                    Set set = merchantRandomChargesVOListNew.keySet();
                    Iterator itr = set.iterator();
                    while (itr.hasNext())
                    {
                        chargeName = (String) itr.next();
                        MerchantRandomChargesVO detailsVO = merchantRandomChargesVOListNew.get(chargeName);
                        MerchantRandomChargesVO merchantRandomChargesVO = new MerchantRandomChargesVO();
                        merchantRandomChargesVO.setChargeCounter(0);
                        merchantRandomChargesVO.setChargeValue(Double.valueOf(detailsVO.getChargeValue()));
                        merchantRandomChargesVO.setChargeName(detailsVO.getChargeName());
                        merchantRandomChargesVO.setChargeValueType(detailsVO.getChargeValueType());
                        merchantRandomChargesVO.setChargeType(detailsVO.getChargeType());
                        merchantRandomChargesVO.setChargeType(detailsVO.getChargeType());
                        merchantRandomChargesVO.setChargeAmount(grossAmount);


                        double total = 0.00;
                        if (detailsVO.getChargeValueType().equalsIgnoreCase("Percentage"))
                        {
                            double amount = grossAmount;
                            if (grossAmount < 0)
                            {
                                amount = -1 * grossAmount;
                            }
                            total = amount * Double.valueOf(detailsVO.getChargeValue()) / 100;
                            merchantRandomChargesVO.setTotal(total);

                        }
                        else
                        {
                            total = Double.valueOf(detailsVO.getChargeValue());
                            merchantRandomChargesVO.setTotal(total);
                        }
                        if ("Deducted".equals(detailsVO.getChargeType()))
                        {
                            deductedAmount = deductedAmount + total;
                        }
                        else
                        {
                            addedAmount = addedAmount + total;
                        }
                        merchantRandomChargesVOHashMap.put(merchantRandomChargesVO.getChargeName(), merchantRandomChargesVO);
                    }
                    merchantRandomChargesGrossAmount = Double.valueOf(addedAmount - deductedAmount);
                    RandomeChargesTerminalwise.put(terminalName, merchantRandomChargesVOHashMap);
                }

                if (grossChargeVOMap.size() > 0)
                {
                    String chargeName = null;
                    Set set = grossChargeVOMap.keySet();
                    Iterator itr = set.iterator();
                    while (itr.hasNext())
                    {
                        chargeName = (String) itr.next();
                        GrossChargeVO grossChargesVO = payoutManager.applyGrossCharge(chargeName, grossChargeVOMap, grossAmount);
                        grossChargesAmount = grossChargesAmount + grossChargesVO.getTotal();
                        grossChargeVOMapFinal.put(grossChargesVO.getChargeName(), grossChargesVO);
                    }
                    GrossChargeterminalWise.put(terminalName, grossChargeVOMapFinal);
                }
                if (otherChargeVOMap.size() > 0)
                {
                    String chargeName = null;
                    Set set = otherChargeVOMap.keySet();
                    Iterator itr = set.iterator();
                    while (itr.hasNext())
                    {
                        chargeName = (String) itr.next();
                        OtherChargesVO otherChargesVO = CalculateOthercharges(chargeName, otherChargeVOMap, grossAmount);
                        otherChargesAmount = otherChargesAmount + otherChargesVO.getTotal();
                        otherChargesVOHashMapFinal.put(otherChargesVO.getChargeName(), otherChargesVO);
                    }
                    OtherChargesTerminalwise.put(terminalName, otherChargesVOHashMapFinal);
                }
                if (serviceTaxChargeVOHashMap.size() > 0)
                {
                    String chargeName = null;
                    Set set = serviceTaxChargeVOHashMap.keySet();
                    Iterator itr = set.iterator();
                    while (itr.hasNext())
                    {
                        chargeName = (String) itr.next();
                        ServiceTaxChargeVO serviceTaxChargeVO = payoutManager.calculateServiveTax(chargeName, serviceTaxChargeVOHashMap, grossFee + wireChargeAmount);
                        serviceTaxChargesAmount = serviceTaxChargesAmount + serviceTaxChargeVO.getTotal();
                        serviceTaxChargeVOHashMapFinal.put(serviceTaxChargeVO.getChargeName(), serviceTaxChargeVO);
                    }
                    ServicetaxTerminalwise.put(terminalName, serviceTaxChargeVOHashMapFinal);
                }

                //merchantRandomChargesGrossAmount = addedAmount - deductedAmount;
                double totalChargebackReversal = chargebackReverseVO.getAmount();
                double refundReversedAmount = transactionSummaryVO.getRefundReverseAmount();

                double totalFundedToBank = (reserveReleaseAmount + grossAmount + previousBalanceAmount + merchantRandomChargesGrossAmount + totalChargebackReversal + refundReversedAmount) - ((wireChargeAmount + statementChargeAmount) + (-1 * grossChargesAmount) + serviceTaxChargesAmount + otherChargesAmount);
                double minPayoutAmount = Double.valueOf(terminalVO.getMinPayoutAmount());
                boolean isWireFeeRequired = false;
                if (wireChargeVO != null)
                {
                    if (totalFundedToBank >= minPayoutAmount)
                    {
                        isWireFeeRequired = true;
                    }
                }
                logger.error("iswirerequired yes::::::::::::" + isWireFeeRequired + "minPayoutAmount:::::::" + minPayoutAmount + "terminalVO." + terminalVO.getTerminalId());
                minpayout.put(terminalVO.getTerminalId(), isWireFeeRequired);

                if (wireChargeVO != null && !isWireFeeRequired)
                {
                    totalFundedToBank = totalFundedToBank + wireChargeAmount;
                }
                double exchangeRateDbl = 1.00;
                if (settlementCurrency != null)
                {
                    exchangeRateDbl = terminalVO.getConversionRate();
                    logger.error("exchangeRateDbl----" + exchangeRateDbl);
                }
                exchangeRateMap.put(terminalVO.getTerminalId(), exchangeRateDbl);

                refundReverseMap.put(terminalVO.getTerminalId(), refundReversedAmount);
                chargebackReversedMap.put(terminalVO.getTerminalId(), chargebackReversalAmount);
                chargebackReverseVOMap.put(terminalVO.getTerminalId(), chargebackReverseVO);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (chargeDetailsMapOFVOs.size() >= 0)
        {
            System.out.println("here inside it");
            int i = 0;
            String terminalIds = "";
            String accountIds = "";
            for (TerminalVO terminalVO : terminalVOList)
            {
                if (i == (terminalVOList.size() - 1)){
                    terminalIds = terminalIds + terminalVO.getTerminalId();
                    accountIds = accountIds + terminalVO.getAccountId();}
                else{
                    terminalIds = terminalIds + terminalVO.getTerminalId() + ",";
                    accountIds = accountIds + terminalVO.getAccountId() + ",";}
                i++;
            }
            String settleTransExcelFilePath = createMerchantTransactionFileConsolidated(settlementDateVO, tableName, memberId, accountIds, terminalIds);
            String rollingReserveExcelFilePath = createRollingReserveTransactionFileConsoldated(rollingReserveDateVO, memberId, accountIds, terminalIds);
            String status = getConsolidatedMerchantReport(weeklyPayoutReportVO, terminalVOList, memberId, accountId, currency, settlementCurrency, exchangeRateMap, chargeDetailsMapOFVOs, chargebackReverseVOMap, totalProcessingAmountMap, reverseAmountMap, chargebackAmountMap, payoutAmountMap, refundReverseMap, chargebackReversedMap, reserveRefundVOHashMap, reserveGeneratedVOHashMap, statementChargeVOHashMap, wireChargeVOHashMap, RandomeChargesTerminalwise, previousBalanceAmountMap, settlementExpensesVOHashMap, settleTransExcelFilePath, rollingReserveExcelFilePath, chargeVOHashMap, ServiceFeesChargesTerminalwise, OtherChargesTerminalwise, GrossChargeterminalWise, ServicetaxTerminalwise, minpayout);
            System.out.println("status:::::::::::::in first methdod" +status);
            resStatus = status;
            logger.error("rollingReserveExcelFilePath::::" + rollingReserveExcelFilePath);
        }
        return resStatus;
    }

    private String getConsolidatedMerchantReport(WeeklyPayoutReportVO weeklyPayoutReportVO, List<TerminalVO> terminalVOList, String memberId, String accountId, String currency, String settlementCurrency, HashMap<String, Double> exchangeRate, HashMap<String, List<ChargeDetailsVO>> chargeVOHashMap, HashMap<String, ChargeDetailsVO> chargebackReverseVOMap, HashMap<String, Double> totalProcessingMap, HashMap<String, Double> refundMap, HashMap<String, Double> chargebackMap, HashMap<String, Double> payoutMap, HashMap<String, Double> refundReverseMap, HashMap<String, Double> chargebackReverseMap, HashMap<String, ReserveRefundVO> reserveRefundVOHashMap, HashMap<String, ReserveGeneratedVO> reserveGeneratedVOHashMap, HashMap<String, List<StatementChargeVO>> statementChargeVOHashMap, HashMap<String, List<WireChargeVO>> wireChargeVOHashMap, HashMap<String, HashMap<String, MerchantRandomChargesVO>> RandomeChargesTerminalwise, HashMap<String, Double> previousBalanceAmountMap, HashMap<String, List<SettlementExpensesVO>> settlementExpensesVOHashMap, String settleTransExcelFilePath, String rollingReserveExcelFilePath, HashMap<String, ChargeVO> detailChargeVo, HashMap<String, HashMap<String, TotalFeesChargesVO>> ServiceFeesChargesTerminalwise, HashMap<String, HashMap<String, OtherChargesVO>> otherChargesVOHashMapFinal, HashMap<String, HashMap<String, GrossChargeVO>> GrossChargessTerminalwise, HashMap<String, HashMap<String, ServiceTaxChargeVO>> ServixTaxTerminalwise, HashMap<String, Boolean> minpayout)
    {
        AccountUtil accountUtil = new AccountUtil();
        Document document = new Document(PageSize.A3, 40, 40, 55, 55);
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String reportFileName = null;
        String status = "failed";
        try
        {
            SettlementDateVO settlementDateVO = weeklyPayoutReportVO.getSettlementDateVO();
            RollingReserveDateVO rollingReserveDateVO = weeklyPayoutReportVO.getRollingReserveDateVO();
            MerchantDAO merchantDAO = new MerchantDAO();
            String sStartTransactionDate = settlementDateVO.getSettlementStartDate();
            String sEndTransactionDate = settlementDateVO.getSettlementEndDate();

            reportFileName = accountUtil.getConsolidatedReportName(memberId, accountId, sStartTransactionDate, sEndTransactionDate);
            HashMap partnerDetails = payoutDAO.getPartnerDetails(memberId);
            MerchantDetailsVO merchantDetailsVO = merchantDAO.getMemberDetails(memberId);
            String contactPerson = merchantDetailsVO.getContact_persons();
            String companyName = merchantDetailsVO.getCompany_name();

            reportFileName = reportFileName + ".pdf";
            String partnerLogoName = (String) partnerDetails.get("logoName");
            String addressDetails = (String) partnerDetails.get("address");
            String telNo = (String) partnerDetails.get("telno");
            String companySupportMailId = (String) partnerDetails.get("companysupportmailid");
            String backgroundColor = (String) partnerDetails.get("reportfile_bgcolor");
            Color bgColor = Color.gray.brighter();
            if ("Blue".equalsIgnoreCase(backgroundColor))
            {
                bgColor = new Color(0, 127, 255);
            }
            else if ("Orange".equalsIgnoreCase(backgroundColor))
            {
                bgColor = new Color(245, 130, 42);
            }

            if (!functions.isValueNull(partnerLogoName))
            {
                partnerLogoName = "pay2.png";
            }

            //File filepath=new File("E:/tomcat8/reportfiles/"+reportFileName);
            File filepath = new File(PAYOUT_REPORT_FILE_PATH + reportFileName);

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filepath));
            if (functions.isValueNull(addressDetails) && functions.isValueNull(companySupportMailId) && functions.isValueNull(telNo))
            {
                Phrase phraseLine1 = new Phrase(addressDetails);
                Phrase phraseLine2 = new Phrase(" I " + companySupportMailId + " I " + telNo + "");
                writer.setBoxSize("art", writer.getPageSize());
                HeaderFooterPageEvent event = new HeaderFooterPageEvent(phraseLine1, phraseLine2);
                writer.setPageEvent(event);
            }

            document.open();
            Table table = new Table(7);
            table.setWidth(100);
            table.setBorder(Table.NO_BORDER);

            table.setBorderColor(new Color(0, 0, 0));
            table.setPadding(2);
            String reportingDate = targetFormat.format(new Date());

            com.lowagie.text.Image partnerImageInstance = com.lowagie.text.Image.getInstance(PARTNER_LOGO_PATH + partnerLogoName);
            com.lowagie.text.Font f1 = FontFactory.getFont(FontFactory.TIMES_ROMAN, 20);
            f1.setColor(Color.BLACK);

            com.lowagie.text.Font f2 = FontFactory.getFont(FontFactory.TIMES_BOLD, 15);
            f2.setColor(Color.WHITE);

            com.lowagie.text.Font f3 = FontFactory.getFont(FontFactory.TIMES_BOLD, 15);
            f3.setColor(Color.BLACK);

            Cell partnerNameCaptionCell = new Cell(new Paragraph(20, "SETTLEMENT REPORT", f1));

            Cell partnerLogoCell = null;
            if (partnerImageInstance != null)
            {
                partnerLogoCell = new Cell(partnerImageInstance);
            }
            else
            {
                partnerLogoCell = new Cell("");
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

            Cell sMemberDetailsHeaderCell = new Cell(new Paragraph(20, "Member Details", f2));
            sMemberDetailsHeaderCell.setColspan(7);
            sMemberDetailsHeaderCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            sMemberDetailsHeaderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            sMemberDetailsHeaderCell.setBackgroundColor(bgColor);
            table.addCell(sMemberDetailsHeaderCell);

            Cell sMemberIdCaptionCell = new Cell("Member ID:");
            Cell sMemberIdValueCell = new Cell(memberId);
            sMemberIdCaptionCell.setColspan(4);
            sMemberIdValueCell.setColspan(3);
            table.addCell(sMemberIdCaptionCell);
            table.addCell(sMemberIdValueCell);

            Cell sCompanyNameCaptionCell = new Cell("Company Name:");
            Cell sCompanyNameValueCell = new Cell(companyName);
            sCompanyNameCaptionCell.setColspan(4);
            sCompanyNameValueCell.setColspan(3);
            table.addCell(sCompanyNameCaptionCell);
            table.addCell(sCompanyNameValueCell);

            Cell sContactPersonCaptionCell = new Cell("Contact Person:");
            Cell sContactPersonValueCell = new Cell(contactPerson);
            sContactPersonCaptionCell.setColspan(4);
            sContactPersonValueCell.setColspan(3);
            table.addCell(sContactPersonCaptionCell);
            table.addCell(sContactPersonValueCell);

            Cell sCurrencyCaptionCell = new Cell("Processing Currency:");
            Cell sCurrencyValueCell = new Cell(currency);
            sCurrencyCaptionCell.setColspan(4);
            sCurrencyValueCell.setColspan(3);
            table.addCell(sCurrencyCaptionCell);
            table.addCell(sCurrencyValueCell);

            Cell sSettlementCaptionCell = new Cell("Settlement Currency:");
            Cell sSettlementValueCell = new Cell(settlementCurrency);
            sSettlementCaptionCell.setColspan(4);
            sSettlementValueCell.setColspan(3);
            table.addCell(sSettlementCaptionCell);
            table.addCell(sSettlementValueCell);

            Cell sSettlePeriodCaptionCell = new Cell("Settle Transaction Period:");
            Cell sSettleStartValueCell = new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getSettlementStartDate())) + " TO " + targetFormat.format(targetFormat.parse(settlementDateVO.getSettlementEndDate())));

            sSettlePeriodCaptionCell.setColspan(4);
            sSettleStartValueCell.setColspan(3);

            table.addCell(sSettlePeriodCaptionCell);
            table.addCell(sSettleStartValueCell);

            Cell sDeclinedPeriodCaptionCell = new Cell("Decline Transaction Period:");
            Cell sDeclinedStartValueCell = new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getDeclinedStartDate())) + " TO " + targetFormat.format(targetFormat.parse(settlementDateVO.getDeclinedEndDate())));

            sDeclinedPeriodCaptionCell.setColspan(4);
            sDeclinedStartValueCell.setColspan(3);

            table.addCell(sDeclinedPeriodCaptionCell);
            table.addCell(sDeclinedStartValueCell);

            Cell sReversedPeriodCaptionCell = new Cell("Reversal Transaction Period:");
            Cell sReversedStartValueCell = new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getReversedStartDate())) + " TO " + targetFormat.format(targetFormat.parse(settlementDateVO.getReversedEndDate())));

            sReversedPeriodCaptionCell.setColspan(4);
            sReversedStartValueCell.setColspan(3);

            table.addCell(sReversedPeriodCaptionCell);
            table.addCell(sReversedStartValueCell);

            Cell sChargebackPeriodCaptionCell = new Cell("Chargeback Transaction Period:");
            Cell sChargebackStartValueCell = new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getChargebackStartDate())) + " TO " + targetFormat.format(targetFormat.parse(settlementDateVO.getChargebackEndDate())));

            sChargebackPeriodCaptionCell.setColspan(4);
            sChargebackStartValueCell.setColspan(3);

            table.addCell(sChargebackPeriodCaptionCell);
            table.addCell(sChargebackStartValueCell);

            Cell sChargebackReversedPeriodCaptionCell = new Cell("Chargeback Reversed Transaction Period:");
            Cell sChargebackReversedStartValueCell = new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getChargebackStartDate())) + " TO " + targetFormat.format(targetFormat.parse(settlementDateVO.getChargebackEndDate())));

            sChargebackReversedPeriodCaptionCell.setColspan(4);
            sChargebackReversedStartValueCell.setColspan(3);

            table.addCell(sChargebackReversedPeriodCaptionCell);
            table.addCell(sChargebackReversedStartValueCell);

            Cell sPayoutPeriodCaptionCell = new Cell("Payout Transaction Period:");
            Cell sPayoutStartValueCell = new Cell(targetFormat.format(targetFormat.parse(settlementDateVO.getReversedStartDate())) + " TO " + targetFormat.format(targetFormat.parse(settlementDateVO.getReversedEndDate())));

            sPayoutPeriodCaptionCell.setColspan(4);
            sPayoutStartValueCell.setColspan(3);

            table.addCell(sPayoutPeriodCaptionCell);
            table.addCell(sPayoutStartValueCell);

            Cell sRollingReserveReleaseUptoDateCaptionCell = new Cell("Rolling Release Period:");
            Cell sRollingReserveReleaseStartDateValueCell = new Cell(targetFormat.format(targetFormat.parse(rollingReserveDateVO.getRollingReserveStartDate())) + " TO " + targetFormat.format(targetFormat.parse(rollingReserveDateVO.getRollingReserveEndDate())));

            sRollingReserveReleaseUptoDateCaptionCell.setColspan(4);
            sRollingReserveReleaseStartDateValueCell.setColspan(3);

            table.addCell(sRollingReserveReleaseUptoDateCaptionCell);
            table.addCell(sRollingReserveReleaseStartDateValueCell);

            Cell sReportHeader = new Cell(new Paragraph(20, "Charges Details ", f2));
            sReportHeader.setColspan(7);
            sReportHeader.setHorizontalAlignment(Element.ALIGN_LEFT);
            sReportHeader.setBackgroundColor(bgColor);
            table.addCell(sReportHeader);


            Set<String> terminalList = chargeVOHashMap.keySet();
            Cell sChargeNameHeader = new Cell(new Paragraph(20, "Charge Name", f3));
            Cell sChargeTerminalHeader = new Cell(new Paragraph(20, "Terminal ID", f3));
            Cell sChargeRateHeader = new Cell(new Paragraph(20, "Rate/Fee", f3));
            Cell sChargeCounterHeader = new Cell(new Paragraph(20, "Counter", f3));
            Cell sChargeAmountHeader = new Cell(new Paragraph(20, "Amount", f3));
            Cell sChargeTotalHeader = new Cell(new Paragraph(20, "Total", f3));

            sChargeNameHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            sChargeTerminalHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            sChargeRateHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
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

            double totalChargesFee = 0.00;
            double totalProcessingAmount = 0.00;
            double totalReverseAmount = 0.00;
            double totalChargebackAmount = 0.00;
            double totalPayoutAmount = 0.00;
            double totalMiscellaneousAdjustment = 0.00;
            double previousBalanceAmount = 0.00;
            double OthercahargesAmount = 0.00;
            double totalAmountFunded = 0.00;
            double exchangerate = 1.00;
            double refundReverseAmount = 0.00;
            double chargebackReverseAmount = 0.00;
            double grossReserveRefunded = 0.00;
            double grossReserveGenerated = 0.00;
            double statementChargeAmount = 0.00;
            double wireChargeAmount = 0.00;
            boolean negativebalance = false;
            Cell sChargeNameValue, sChargeTerminalValue, sChargeRateValue, sChargeCounterValue, sChargeAmountValue, sChargeTotalValue;

            for (String terminalId : terminalList)
            {
                Cell terminalHeader = new Cell(new Paragraph(20, terminalId, f2));
                terminalHeader.setColspan(7);
                terminalHeader.setHorizontalAlignment(Element.ALIGN_LEFT);
                terminalHeader.setBackgroundColor(Color.gray);
                table.addCell(terminalHeader);

                String terminalDetails[] = terminalId.split("-");
                String terminal = terminalDetails[0];

                totalProcessingAmount = totalProcessingAmount + totalProcessingMap.get(terminal);
                if (refundMap != null && refundMap.size() > 0)
                {
                    totalReverseAmount = totalReverseAmount + refundMap.get(terminal);
                }

                if (chargebackMap != null && chargebackMap.size() > 0)
                {
                    totalChargebackAmount = totalChargebackAmount + chargebackMap.get(terminal);
                }
                if (payoutMap != null && payoutMap.size() > 0)
                {
                    totalPayoutAmount = totalPayoutAmount + payoutMap.get(terminal);
                }
                /*if(miscellaneousAdjustment!=null && miscellaneousAdjustment.size()>0) {
                    totalMiscellaneousAdjustment=totalMiscellaneousAdjustment+miscellaneousAdjustment.get(terminal);
                }*/
                if (previousBalanceAmountMap != null && previousBalanceAmountMap.size() > 0)
                {
                    previousBalanceAmount = previousBalanceAmount + previousBalanceAmountMap.get(terminal);
                }
                if (exchangeRate != null && exchangeRate.size() > 0)
                {
                    exchangerate = exchangeRate.get(terminal);
                }
                if (refundReverseMap != null && refundReverseMap.size() > 0)
                {
                    refundReverseAmount = refundReverseAmount + refundReverseMap.get(terminal);
                }
                if (chargebackReverseMap != null && chargebackReverseMap.size() > 0)
                {
                    chargebackReverseAmount = chargebackReverseAmount + chargebackReverseMap.get(terminal);
                }

                List<ChargeDetailsVO> chargeDetailsVOList = chargeVOHashMap.get(terminalId);
                double totalSubChargesFee = 0.00;
                for (ChargeDetailsVO chargeDetailsVO : chargeDetailsVOList)
                {
                    String vDblAmountChar = (Functions.round(chargeDetailsVO.getAmount(), 2));
                    String vCntCounter = "-";
                    sChargeNameValue = new Cell(chargeDetailsVO.getChargeName());
                    sChargeTerminalValue = new Cell(terminalId);
                    sChargeTotalValue = new Cell(Functions.round(chargeDetailsVO.getTotal(), 2));
                    totalChargesFee = totalChargesFee + chargeDetailsVO.getTotal();
                    totalSubChargesFee = totalSubChargesFee + chargeDetailsVO.getTotal();
                    if ("Percentage".equals(chargeDetailsVO.getValueType()))
                    {
                        sChargeRateValue = new Cell(chargeDetailsVO.getChargeValue() + "%");
                    }
                    else
                    {
                        sChargeRateValue = new Cell(chargeDetailsVO.getChargeValue());
                        vCntCounter = (new Long(chargeDetailsVO.getCount())).toString();
                    }
                    sChargeCounterValue = new Cell(vCntCounter);
                    sChargeAmountValue = new Cell(vDblAmountChar);

                    sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
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
                if (chargebackReverseVOMap != null && chargebackReverseVOMap.size() > 0)
                {
                    ChargeDetailsVO chargebackReversalVO = chargebackReverseVOMap.get(terminal);
                    if (chargebackReversalVO.getCount() > 0)
                    {
                        sChargeNameValue = new Cell("Chargeback Reversal");
                        sChargeRateValue = new Cell(chargebackReversalVO.getChargeValue());
                        sChargeTerminalValue = new Cell(terminalId);
                        sChargeCounterValue = new Cell(new Long(chargebackReversalVO.getCount()).toString());
                        sChargeAmountValue = new Cell(Functions.round(chargebackReversalVO.getAmount(), 2));
                        totalChargesFee = totalChargesFee - chargebackReversalVO.getTotal();
                        totalSubChargesFee = totalSubChargesFee - chargebackReversalVO.getTotal();
                        sChargeTotalValue = new Cell(Functions.round(-chargebackReversalVO.getTotal(), 2));

                        sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        sChargeTerminalValue.setHorizontalAlignment(Element.ALIGN_LEFT);
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
                }
                Cell sChargeFeeSubTotalCaption = new Cell(new Paragraph(20, "Sub Total", f3));
                Cell sChargeFeeSubTotalValue = new Cell(new Paragraph(20, Functions.round(totalSubChargesFee, 2), f3));
                sChargeFeeSubTotalCaption.setColspan(6);
                sChargeFeeSubTotalCaption.setHeader(true);
                sChargeFeeSubTotalCaption.setHorizontalAlignment(Element.ALIGN_LEFT);
                sChargeFeeSubTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                table.addCell(sChargeFeeSubTotalCaption);
                table.addCell(sChargeFeeSubTotalValue);
            }
            Cell sChargeFeeTotalCaption = new Cell(new Paragraph(20, "Total", f3));
            Cell sChargeFeeTotalValue = new Cell(new Paragraph(20, Functions.round(totalChargesFee, 2), f3));
            sChargeFeeTotalCaption.setColspan(6);
            sChargeFeeTotalCaption.setHeader(true);
            sChargeFeeTotalCaption.setHorizontalAlignment(Element.ALIGN_LEFT);
            sChargeFeeTotalCaption.setBackgroundColor(Color.gray);
            sChargeFeeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(sChargeFeeTotalCaption);
            table.addCell(sChargeFeeTotalValue);

            Cell sGeneratedReserveDetailsHeaderCell = new Cell(new Paragraph(20, "Generated Reserve Details", f2));
            sGeneratedReserveDetailsHeaderCell.setColspan(7);
            sGeneratedReserveDetailsHeaderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            sGeneratedReserveDetailsHeaderCell.setBackgroundColor(bgColor);
            table.addCell(sGeneratedReserveDetailsHeaderCell);

            Set generatedRollingReserveSet = reserveGeneratedVOHashMap.keySet();
            Iterator generatedRollingReserveIterator = generatedRollingReserveSet.iterator();
            while (generatedRollingReserveIterator.hasNext())
            {
                String terminalId = (String) generatedRollingReserveIterator.next();
                ReserveGeneratedVO generatedVO = reserveGeneratedVOHashMap.get(terminalId);

                //String arr[]=generatedRollingReserveChargeName.split(":");
                sChargeNameValue = new Cell(generatedVO.getChargeName());
                sChargeTerminalValue = new Cell(terminalId);
                sChargeCounterValue = new Cell("-");
                sChargeAmountValue = new Cell(Functions.round(generatedVO.getAmount(), 2));
                grossReserveGenerated = grossReserveGenerated + generatedVO.getTotal();
                sChargeTotalValue = new Cell(Functions.round(generatedVO.getTotal(), 2));
                sChargeRateValue = new Cell(generatedVO.getChargeValue() + "%");

                sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTerminalValue.setHorizontalAlignment(Element.ALIGN_LEFT);
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

            Cell sReserveGeneratedTotalCaption = new Cell(new Paragraph(20, "Total", f3));
            Cell sReserveGeneratedTotalValue = new Cell(new Paragraph(20, Functions.round(grossReserveGenerated, 2), f3));
            sReserveGeneratedTotalCaption.setColspan(6);
            sReserveGeneratedTotalCaption.setHeader(true);
            sReserveGeneratedTotalCaption.setHorizontalAlignment(Element.ALIGN_LEFT);
            sReserveGeneratedTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(sReserveGeneratedTotalCaption);
            table.addCell(sReserveGeneratedTotalValue);

            Cell sRefundedReserveDetailsHeaderCell = new Cell(new Paragraph(20, "Refunded Reserve Details", f2));
            sRefundedReserveDetailsHeaderCell.setColspan(7);
            sRefundedReserveDetailsHeaderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            sRefundedReserveDetailsHeaderCell.setBackgroundColor(bgColor);
            table.addCell(sRefundedReserveDetailsHeaderCell);

            Set refundedRollingReserveSet = reserveRefundVOHashMap.keySet();
            Iterator refundedRollingReserveIterator = refundedRollingReserveSet.iterator();
            while (refundedRollingReserveIterator.hasNext())
            {
                String terminalId = (String) refundedRollingReserveIterator.next();
                ReserveRefundVO reserveRefundVO = reserveRefundVOHashMap.get(terminalId);

                sChargeNameValue = new Cell(reserveRefundVO.getChargeName());
                sChargeCounterValue = new Cell("-");
                sChargeAmountValue = new Cell(Functions.round(reserveRefundVO.getAmount(), 2));
                grossReserveRefunded = grossReserveRefunded + reserveRefundVO.getTotal();
                sChargeTotalValue = new Cell(Functions.round(reserveRefundVO.getTotal(), 2));
                sChargeRateValue = new Cell(reserveRefundVO.getChargeValue() + "%");
                sChargeTerminalValue = new Cell(terminalId);

                sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sChargeTerminalValue.setHorizontalAlignment(Element.ALIGN_LEFT);
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

            Cell sReserveRefundedTotalCaption = new Cell(new Paragraph(20, "Total", f3));
            Cell sReserveRefundedTotalValue = new Cell(new Paragraph(20, Functions.round(grossReserveRefunded, 2), f3));

            sReserveRefundedTotalCaption.setColspan(6);
            sReserveRefundedTotalCaption.setHeader(true);
            sReserveRefundedTotalCaption.setHorizontalAlignment(Element.ALIGN_LEFT);
            sReserveRefundedTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(sReserveRefundedTotalCaption);
            table.addCell(sReserveRefundedTotalValue);

            Cell sPayoutReportSummaryHeaderCell = new Cell(new Paragraph(20, "Summary", f2));
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

            Cell sTotalProcessingAmountCaptionCell = new Cell("Total Processing Amount");
            Cell sTotalProcessingAmountValueCell = new Cell(Functions.round(totalProcessingAmount, 2));
            sTotalProcessingAmountCaptionCell.setColspan(6);
            sTotalProcessingAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sTotalProcessingAmountCaptionCell);
            table.addCell(sTotalProcessingAmountValueCell);

            Cell sTotalFeesCaptionCell = new Cell("Total Fees");
            Cell sTotalFeesValueCell = new Cell(isValidAmount(Functions.round(totalChargesFee, 2)));
            sTotalFeesCaptionCell.setColspan(6);
            sTotalFeesValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sTotalFeesCaptionCell);
            table.addCell(sTotalFeesValueCell);

            double ServiceFees = 0.00;
            if (ServiceFeesChargesTerminalwise != null && ServiceFeesChargesTerminalwise.size() > 0 && totalChargesFee != 0)
            {
                Set Terminalset = ServiceFeesChargesTerminalwise.keySet();
                Iterator TerminalIterator = Terminalset.iterator();
                while (TerminalIterator.hasNext())
                {
                    String TerminalName = (String) TerminalIterator.next();
                    HashMap<String, TotalFeesChargesVO> Totalfees = ServiceFeesChargesTerminalwise.get(TerminalName);
                    Set totalFeesChargeSet = Totalfees.keySet();
                    Iterator totalFeesChargeIterator = totalFeesChargeSet.iterator();
                    while (totalFeesChargeIterator.hasNext())
                    {
                        String totalFeesChargeName = (String) totalFeesChargeIterator.next();
                        TotalFeesChargesVO totalFeesChargesVO = Totalfees.get(totalFeesChargeName);
                        sChargeNameValue = new Cell(totalFeesChargesVO.getChargeName());
                        sChargeTerminalValue = new Cell(TerminalName);
                        if ("Percentage".equals(totalFeesChargesVO.getValueType()))
                        {
                            sChargeRateValue = new Cell(totalFeesChargesVO.getChargeValue() + "%");
                        }
                        else
                        {
                            sChargeRateValue = new Cell(totalFeesChargesVO.getChargeValue());
                        }
                        sChargeCounterValue = new Cell("-");
                        sChargeAmountValue = new Cell(Functions.round(totalChargesFee, 2));
                        if (totalFeesChargesVO.getValueType().equalsIgnoreCase("Percentage"))
                        {
                            double total = totalChargesFee * Double.valueOf(totalFeesChargesVO.getChargeValue()) / 100;
                            totalFeesChargesVO.setTotal(Functions.roundDBL(total, 2));
                        }
                        else
                        {
                            double total = Double.valueOf(totalFeesChargesVO.getChargeValue());
                            totalFeesChargesVO.setTotal(Functions.roundDBL(total, 2));
                        }
                        sChargeTotalValue = new Cell("-" + Functions.round(totalFeesChargesVO.getTotal(), 2));
                        ServiceFees = ServiceFees + totalFeesChargesVO.getTotal();
                        sChargeTerminalValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                        sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        sChargeCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        sChargeAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        sChargeTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                        sChargeNameValue.setColspan(4);

                        table.addCell(sChargeNameValue);
                        // table.addCell(sChargeTerminalValue);
                        //table.addCell(sChargeCounterValue);
                        table.addCell(sChargeAmountValue);
                        table.addCell(sChargeRateValue);
                        table.addCell(sChargeTotalValue);
                    }
                    break;
                }
            }

            Cell sTotalReversalCaptionCell = new Cell("Total Reversal");
            Cell sTotalReversalValueCell = new Cell(isValidAmount(Functions.round(totalReverseAmount, 2)));
            sTotalReversalCaptionCell.setColspan(6);
            sTotalReversalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sTotalReversalCaptionCell);
            table.addCell(sTotalReversalValueCell);

            Cell sTotalChargebackCaptionCell = new Cell("Total Chargeback");
            Cell sTotalChargebackValueCell = new Cell(isValidAmount(Functions.round(totalChargebackAmount, 2)));
            sTotalChargebackCaptionCell.setColspan(6);
            sTotalChargebackValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sTotalChargebackCaptionCell);
            table.addCell(sTotalChargebackValueCell);

            Cell sTotalPayoutCaptionCell = new Cell("Total Payout Amount");
            Cell sTotalPayoutValueCell = new Cell(isValidAmount(Functions.round(totalPayoutAmount, 2)));
            sTotalPayoutCaptionCell.setColspan(6);
            sTotalPayoutValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sTotalPayoutCaptionCell);
            table.addCell(sTotalPayoutValueCell);


            Cell sGeneratedReserveCaptionCell = new Cell("Generated Reserve");
            Cell sGeneratedReserveValueCell = new Cell(isValidAmount(Functions.round(grossReserveGenerated, 2)));
            sGeneratedReserveCaptionCell.setColspan(6);
            sGeneratedReserveValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sGeneratedReserveCaptionCell);
            table.addCell(sGeneratedReserveValueCell);

            Cell sGrossAmountCaptionCell = new Cell(new Paragraph(20, "Gross Amount", f3));
            double grossAmount = totalProcessingAmount - (totalChargesFee + ServiceFees + totalReverseAmount + totalChargebackAmount + totalPayoutAmount + grossReserveGenerated);
            Cell sGrossAmountValueCell = new Cell(new Paragraph(20, Functions.round(grossAmount, 2), f3));
            sGrossAmountCaptionCell.setColspan(6);
            sGrossAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sGrossAmountCaptionCell);
            table.addCell(sGrossAmountValueCell);

            if (statementChargeVOHashMap != null && statementChargeVOHashMap.size() > 0)
            {
                Set statementChargeSet = statementChargeVOHashMap.keySet();
                Iterator statementChargeIterator = statementChargeSet.iterator();
                while (statementChargeIterator.hasNext())
                {
                    String statementTerminal = (String) statementChargeIterator.next();
                    List<StatementChargeVO> statementChargeVOs = statementChargeVOHashMap.get(statementTerminal);
                    for (StatementChargeVO statementChargeVO : statementChargeVOs)
                    {
                        sChargeNameValue = new Cell(statementChargeVO.getChargeName());
                        sChargeTerminalValue = new Cell(statementTerminal);
                        sChargeRateValue = new Cell(statementChargeVO.getChargeValue());

                        sChargeCounterValue = new Cell(new Long(statementChargeVO.getCount()).toString());
                        sChargeAmountValue = new Cell("-");
                        statementChargeAmount = statementChargeAmount + statementChargeVO.getTotal();
                        sChargeTotalValue = new Cell("-" + Functions.round(statementChargeVO.getTotal(), 2));

                        sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        sChargeTerminalValue.setHorizontalAlignment(Element.ALIGN_CENTER);
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
            }
            boolean wirerequired = false;
            try
            {
                if (wireChargeVOHashMap != null && wireChargeVOHashMap.size() > 0)
                {
                    Set wireSet = wireChargeVOHashMap.keySet();
                    Iterator wireIterator = wireSet.iterator();
                    while (wireIterator.hasNext())
                    {
                        String wireTerminal = (String) wireIterator.next();
                        List<WireChargeVO> wireChargeVOs = wireChargeVOHashMap.get(wireTerminal);
                        String terminalDetails[] = wireTerminal.split("-");
                        String terminal = terminalDetails[0];
                        logger.error("terminal:::::::::::" + terminal);
                        wirerequired = minpayout.get(terminal);
                        logger.error("wirerequired:::::::::::" + minpayout.get(terminal));
                        if (wirerequired)
                        {
                            logger.error("wirerequired========="+wirerequired);
                            for (WireChargeVO wireChargeVO : wireChargeVOs)
                            {
                                sChargeNameValue = new Cell(wireChargeVO.getChargeName());
                                sChargeTerminalValue = new Cell(wireTerminal);
                                sChargeRateValue = new Cell(wireChargeVO.getChargeValue());
                                sChargeCounterValue = new Cell(new Long(wireChargeVO.getCount()).toString());
                                sChargeAmountValue = new Cell("-");
                                wireChargeAmount = wireChargeAmount + wireChargeVO.getTotal();
                                sChargeTotalValue = new Cell("-" + Functions.round(wireChargeVO.getTotal(), 2));

                                sChargeRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                sChargeTerminalValue.setHorizontalAlignment(Element.ALIGN_CENTER);
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
                    }
                }
            }
            catch (Exception e)
            {
                logger.error("checking error", e);
            }
            if (otherChargesVOHashMapFinal != null && otherChargesVOHashMapFinal.size() > 0)
            {
                Set Terminalset = otherChargesVOHashMapFinal.keySet();
                Iterator TerminalIterator = Terminalset.iterator();
                while (TerminalIterator.hasNext())
                {
                    String TerminalName = (String) TerminalIterator.next();
                    HashMap<String, OtherChargesVO> otherCharges = otherChargesVOHashMapFinal.get(TerminalName);
                    Set OtherchargesChargeSet = otherCharges.keySet();
                    Iterator OtherChargeIterator = OtherchargesChargeSet.iterator();
                    while (OtherChargeIterator.hasNext())
                    {
                        String OtherchargesName = (String) OtherChargeIterator.next();
                        OtherChargesVO otherChargesVO = otherCharges.get(OtherchargesName);

                        sChargeNameValue = new Cell(OtherchargesName);
                        sChargeTerminalValue = new Cell(TerminalName);
                        if ("Percentage".equals(otherChargesVO.getValueType()))
                        {
                            sChargeRateValue = new Cell(otherChargesVO.getChargeValue() + "%");
                        }
                        else
                        {
                            sChargeRateValue = new Cell(otherChargesVO.getChargeValue());
                        }
                        sChargeCounterValue = new Cell("-");
                        double amount = grossAmount;
                        if (grossAmount < 0)
                        {
                            amount = -1 * grossAmount;
                        }
                        sChargeAmountValue = new Cell(Functions.round(amount, 2));

                        if (otherChargesVO.getValueType().equalsIgnoreCase("Percentage"))
                        {
                            double total = amount * Double.valueOf(otherChargesVO.getChargeValue()) / 100;
                            otherChargesVO.setTotal(Functions.roundDBL(total, 2));
                        }
                        else
                        {
                            double total = Double.valueOf(otherChargesVO.getChargeValue());
                            otherChargesVO.setTotal(Functions.roundDBL(total, 2));
                        }

                        sChargeTotalValue = new Cell(isValidAmount(Functions.round(otherChargesVO.getTotal(), 2)));
                        OthercahargesAmount = OthercahargesAmount + Double.valueOf(otherChargesVO.getTotal());

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
                    break;
                }
            }
            if (chargebackReverseAmount > 0.00)
            {
                Cell sChargebackReversedAmountCaptionCell = new Cell("Chargeback Reversed Amount");
                Cell sChargebackReversedAmountValueCell = new Cell(Functions.round(chargebackReverseAmount, 2));
                sChargebackReversedAmountCaptionCell.setColspan(6);
                sChargebackReversedAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(sChargebackReversedAmountCaptionCell);
                table.addCell(sChargebackReversedAmountValueCell);
            }
            if (refundReverseAmount > 0.00)
            {
                Cell sRefundReversedAmountCaptionCell = new Cell("Reversal Amount Adjustment");
                Cell sRefundReversedAmountValueCell = new Cell(Functions.round(refundReverseAmount, 2));
                sRefundReversedAmountCaptionCell.setColspan(6);
                sRefundReversedAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(sRefundReversedAmountCaptionCell);
                table.addCell(sRefundReversedAmountValueCell);
            }
            if (RandomeChargesTerminalwise != null && RandomeChargesTerminalwise.size() > 0)
            {
                Set TerminalSet = RandomeChargesTerminalwise.keySet();
                Iterator TerminalIterator = TerminalSet.iterator();
                double totalrandomecharge = 0.00;
                while (TerminalIterator.hasNext())
                {
                    double Added = 0.00;
                    double Deducted = 0.00;
                    String terminal = (String) TerminalIterator.next();
                    HashMap<String, MerchantRandomChargesVO> merchantRandomCharges = RandomeChargesTerminalwise.get(terminal);
                    Set chargeset = merchantRandomCharges.keySet();
                    Iterator ChargeIterator = chargeset.iterator();
                    while (ChargeIterator.hasNext())
                    {
                        String RandomeChargeName = (String) ChargeIterator.next();
                        MerchantRandomChargesVO merchantRandomChargesVO = merchantRandomCharges.get(RandomeChargeName);
                        sChargeNameValue = new Cell(RandomeChargeName);
                        sChargeTerminalValue = new Cell(terminal);
                        double total = 0.00;
                        double amount = grossAmount;
                        if (grossAmount < 0)
                        {
                            amount = -1 * grossAmount;
                        }
                        if ("Percentage".equals(merchantRandomChargesVO.getChargeValueType()))
                        {

                            sChargeRateValue = new Cell(merchantRandomChargesVO.getChargeValue() + "%");
                            total = amount * Double.valueOf(merchantRandomChargesVO.getChargeValue()) / 100;
                            merchantRandomChargesVO.setTotal(total);
                        }
                        else
                        {
                            sChargeRateValue = new Cell(String.valueOf(merchantRandomChargesVO.getChargeValue()));
                            total = Double.valueOf(merchantRandomChargesVO.getChargeValue());
                            merchantRandomChargesVO.setTotal(total);
                        }
                        sChargeCounterValue = new Cell("-");
                        sChargeAmountValue = new Cell(Functions.round(amount, 2));
                        if (merchantRandomChargesVO.getChargeType().equalsIgnoreCase("Deducted"))
                        {
                            if (merchantRandomChargesVO.getTotal() < 0)
                            {
                                sChargeTotalValue = new Cell(Functions.round(merchantRandomChargesVO.getTotal(), 2));
                            }
                            else
                            {
                                sChargeTotalValue = new Cell("-" + Functions.round(merchantRandomChargesVO.getTotal(), 2));
                            }
                            Deducted = Deducted + Double.valueOf(merchantRandomChargesVO.getTotal());
                        }
                        else
                        {
                            sChargeTotalValue = new Cell(Functions.round(merchantRandomChargesVO.getTotal(), 2));
                            Added = Added + Double.valueOf(merchantRandomChargesVO.getTotal());
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
                    totalMiscellaneousAdjustment = Added - Deducted;
                    break;
                }
            }
            else
            {
                Cell sMiscellaneousChargesAmountCaptionCell = new Cell("Miscellaneous Adjustment");
                Cell sMiscellaneousChargesValueCell = new Cell(Functions.round(totalMiscellaneousAdjustment, 2));
                sMiscellaneousChargesAmountCaptionCell.setColspan(6);
                sMiscellaneousChargesValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(sMiscellaneousChargesAmountCaptionCell);
                table.addCell(sMiscellaneousChargesValueCell);
            }

            Cell sPreviousBalanceAmountCaptionCell = new Cell("Previous Balance Amount");
            Cell sPreviousBalanceAmountValueCell = new Cell(Functions.round(previousBalanceAmount, 2));
            sPreviousBalanceAmountCaptionCell.setColspan(6);
            sPreviousBalanceAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sPreviousBalanceAmountCaptionCell);
            table.addCell(sPreviousBalanceAmountValueCell);

            Cell sRefundedRollingReserveCaptionCell = new Cell("Refunded Rolling Reserve");
            Cell sRefundedRollingReserveValueCell = new Cell(Functions.round(grossReserveRefunded, 2));
            sRefundedRollingReserveCaptionCell.setColspan(6);
            sRefundedRollingReserveValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sRefundedRollingReserveCaptionCell);
            table.addCell(sRefundedRollingReserveValueCell);

            Cell sTotalAmountCaptionCell = new Cell(new Paragraph(20, "Total", f3));
            double total = grossAmount + chargebackReverseAmount + refundReverseAmount + totalMiscellaneousAdjustment + grossReserveRefunded + previousBalanceAmount - (statementChargeAmount + wireChargeAmount + OthercahargesAmount);
            Cell sTotalAmountValueCell = new Cell(new Paragraph(20, Functions.round(total, 2), f3));
            sTotalAmountCaptionCell.setColspan(6);
            sTotalAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(sTotalAmountCaptionCell);
            table.addCell(sTotalAmountValueCell);

            Cell sTotalAmountFundedCaptionCell = new Cell(new Paragraph(20, "Total Amount Funded[" + currency + "]", f3));
            Cell sTotalAmountFundedValueCell = new Cell(new Paragraph(20, Functions.round(total, 2), f3));
            sTotalAmountFundedCaptionCell.setColspan(6);
            sTotalAmountFundedValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            sTotalAmountFundedCaptionCell.setBackgroundColor(Color.white.brighter());
            sTotalAmountFundedValueCell.setBackgroundColor(Color.white.brighter());
            table.addCell(sTotalAmountFundedCaptionCell);
            table.addCell(sTotalAmountFundedValueCell);

            totalAmountFunded = total;
            if (settlementExpensesVOHashMap != null && settlementExpensesVOHashMap.size() > 0)
            {
                Set settlementChargeSet = settlementExpensesVOHashMap.keySet();
                Iterator settlementExpenseChargeIteraor = settlementChargeSet.iterator();
                while (settlementExpenseChargeIteraor.hasNext())
                {
                    String settlementExpenseChargeName = (String) settlementExpenseChargeIteraor.next();
                    String ctokenSplit[] = settlementExpenseChargeName.split("\\-");
                    List<SettlementExpensesVO> settlementExpensesVOs = settlementExpensesVOHashMap.get(settlementExpenseChargeName);
                    for (SettlementExpensesVO settlementExpensesVO : settlementExpensesVOs)
                    {
                        String flag = " ";
                        ChargesDAO chargesDAO = new ChargesDAO();
                        negativebalance = chargesDAO.getMerchantChargeNegativeBalance(memberId, accountId, ctokenSplit[0], settlementExpensesVO.getChargeid());
                        double settlementTotal = 0;
                        if (total < 0)
                        {
                            if (settlementExpensesVO.getNegativebalance().equals("Y"))
                            {
                                flag = " [Positive Settlement Charges]";
                            }
                            else
                            {
                                flag = " [No Settlement Charges]";
                            }

                            if (negativebalance)
                            {
                                settlementTotal = -(total * Double.parseDouble(settlementExpensesVO.getChargeValue()) / 100);
                            }
                            else
                            {
                                settlementExpensesVO.setChargeValue("0");
                                settlementTotal = total * Double.parseDouble(settlementExpensesVO.getChargeValue()) / 100;
                            }
                        }
                        else
                        {
                            settlementTotal = total * Double.parseDouble(settlementExpensesVO.getChargeValue()) / 100;
                        }
                        Cell sSettlementExpensesCaptionCell = new Cell(new Paragraph(20, settlementExpensesVO.getChargeName() + flag, f3));
                        Cell sSettlementExpensesAmountValueCell = new Cell(Functions.round(total, 2));

                        Cell sSettlementExpensesRateValueCell = new Cell(Functions.round(Double.valueOf(settlementExpensesVO.getChargeValue()), 2) + "%");
                        // double settlementTotal = total * Double.parseDouble(settlementExpensesVO.getChargeValue()) / 100;
                        Cell sSettlementExpensesTotalValueCell;
                        if (total < 0)
                        {
                            sSettlementExpensesTotalValueCell = new Cell(new Paragraph(20, Functions.round(settlementTotal, 2), f3));
                        }
                        else
                        {
                            sSettlementExpensesTotalValueCell = new Cell(new Paragraph(20, payoutManager.isValidAmount(Functions.round(settlementTotal, 2)), f3));
                        }

                        sSettlementExpensesCaptionCell.setColspan(4);
                        sSettlementExpensesTotalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        sSettlementExpensesAmountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        sSettlementExpensesRateValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        sSettlementExpensesCaptionCell.setBackgroundColor(Color.white.brighter());
                        sSettlementExpensesTotalValueCell.setBackgroundColor(Color.white.brighter());


                        if (totalAmountFunded < 0)
                        {
                            if (negativebalance)
                            {
                                if (settlementTotal > 0)
                                {
                                    totalAmountFunded = totalAmountFunded + settlementTotal;
                                }
                                else
                                {
                                    totalAmountFunded = totalAmountFunded + (-1 * settlementTotal);
                                }
                            }
                            else
                            {
                                if (settlementTotal > 0)
                                {
                                    totalAmountFunded = totalAmountFunded - settlementTotal;
                                }
                                else
                                {
                                    totalAmountFunded = totalAmountFunded - (-1 * settlementTotal);
                                }
                            }
                        }
                        else
                        {
                            if (settlementTotal > 0)
                            {
                                totalAmountFunded = totalAmountFunded - settlementTotal;
                            }
                            else
                            {
                                totalAmountFunded = totalAmountFunded - (-1 * settlementTotal);
                            }
                        }

                        table.addCell(sSettlementExpensesCaptionCell);
                        table.addCell(sSettlementExpensesAmountValueCell);
                        table.addCell(sSettlementExpensesRateValueCell);
                        table.addCell(sSettlementExpensesTotalValueCell);
                    }
                }
            }

            Cell sTotalAmountFundedSettlementCaptionCell = new Cell(new Paragraph(20, "Total Amount Funded[" + settlementCurrency + "]", f3));
            Cell sTotalExchangeAmountValueCell = new Cell(Functions.round(totalAmountFunded, 2));
            Cell sTotalExchangeRateValueCell = new Cell(Functions.round(exchangerate, 2));
            double totalAmountAfterExchange = totalAmountFunded * exchangerate;
            Cell sTotalAmountFundedSettlementValueCell = new Cell(new Paragraph(20, Functions.round(totalAmountAfterExchange, 2), f3));
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

            if (functions.isValueNull(reportFileName))
            {
                System.out.println("i m here in secind state");
                int reportid = payoutDAO.getreportid();
                for (TerminalVO terminalVO : terminalVOList)
                {
                    WireVO wireVO = new WireVO();
                    wireVO.setFirstDate(settlementDateVO.getSettlementStartDate());
                    wireVO.setLastDate(settlementDateVO.getSettlementEndDate());
                    wireVO.setCurrency(currency);
                    wireVO.setMarkForDeletion("N");
                    wireVO.setWireAmount(totalProcessingMap.get(terminalVO.getTerminalId()));
                    wireVO.setWireBalanceAmount(grossAmount);
                    wireVO.setNetFinalAmount(totalAmountFunded);
                    wireVO.setUnpaidAmount(0.00);
                    wireVO.setStatus("unpaid");
                    wireVO.setSettlementReportFilePath(reportFileName);
                    wireVO.setSettledTransactionFilePath(settleTransExcelFilePath);
                    wireVO.setReserveReleasedUptoDate(rollingReserveDateVO.getRollingReserveEndDate());
                    wireVO.setRollingReserveIncluded("Y");
                    wireVO.setTerminalVO(terminalVO);
//                    wireVO.setSettlementCycleNo(settlementDateVO.getSettlementcycleNumber());
                    wireVO.setSettlementCycleNo(terminalVO.getWireId());
                    wireVO.setParent_settlementCycleNo(terminalVO.getParentBankWireId());
                    wireVO.setDeclinedcoverdateupto(settlementDateVO.getDeclinedEndDate());
                    wireVO.setReversedcoverdateupto(settlementDateVO.getReversedEndDate());
                    wireVO.setChargebackcoverdateupto(settlementDateVO.getChargebackEndDate());
                    wireVO.setRollingReserveFilePath(rollingReserveExcelFilePath);
                    wireVO.setReportid(reportid);
                    status = payoutDAO.generateSettlementCycleWire(wireVO);
                    long verifyOrderCount = 0;
                    long refundAlertCount = 0;
                    long retrivalRequestCount = 0;
                    String latestSetupFeeDate = "";
                    long fraudulentTransactionCount = 0;
                    if (detailChargeVo != null && detailChargeVo.size() > 0)
                    {
                        ChargeVO chargeVO = detailChargeVo.get(terminalVO.getTerminalId());
                        verifyOrderCount = chargeVO.getVerifyOrderCount();
                        refundAlertCount = chargeVO.getRefundAlertCount();
                        retrivalRequestCount = chargeVO.getRetrivalRequestCount();
                        latestSetupFeeDate = chargeVO.getLatestSetupFeeDate();
                        fraudulentTransactionCount = chargeVO.getFraudulentTransactionCount();
                    }

                    if ("success".equals(status))
                    {
                        boolean isMemberCycleUpdated = payoutDAO.updateMemberCycleDetailsWithAccountId(memberId, terminalVO.getAccountId(), terminalVO.getTerminalId(), verifyOrderCount, refundAlertCount, retrivalRequestCount, latestSetupFeeDate, terminalVO.getWireId(), fraudulentTransactionCount,terminalVO.getParentBankWireId());
                        if (isMemberCycleUpdated)
                        {
                            logger.debug("member_settlementcycle_details updated successfully");
                        }
                        boolean isBankMerchantSettlementMasterUpdated = payoutDAO.merchantSettlementMasterEntry(memberId, terminalVO.getAccountId(), terminalVO.getTerminalId(), terminalVO.getWireId(), "N",terminalVO.getParentBankWireId());
                        if (isBankMerchantSettlementMasterUpdated)
                        {
                            logger.debug("BankMerchantSettlementMasterUpdated updated successfully");
                        }
                        //Important: Make entry for memberid,accountid,cycleid,ispaid='N' in the bank_merchant_settlement_master
                        logger.error("pending List Size----" + weeklyPayoutReportVO.getPendingList().size());
                        logger.error("Request List Size----" + weeklyPayoutReportVO.getRequestTerminalVO().size());

                        if (isBankMerchantSettlementMasterUpdated)
                        {
                            TerminalManager terminalManager = new TerminalManager();
                            List<TerminalVO> pendinList = terminalManager.getTerminalsByAccountID(terminalVO.getAccountId(),null,null);
                            List<TerminalVO> genratereportList = null;
                            genratereportList = terminalManager.getGenratedReportByAccountID(terminalVO.getAccountId());
                            weeklyPayoutReportVO.setPendingList(pendinList);
                            weeklyPayoutReportVO.setRequestTerminalVO(genratereportList);

                            int data = weeklyPayoutReportVO.getPendingList().size() - weeklyPayoutReportVO.getRequestTerminalVO().size();
                            if (data==0)
                            {
                                boolean ispayoutcronupdated = payoutDAO.updatePayoutCronExecutedFlag(terminalVO.getAccountId(), terminalVO.getWireId());
                            }
                            status = "Success";
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            logger.error(ex);
        }
        return status;
    }

    public String createMerchantTransactionFileConsolidated(SettlementDateVO settlementDateVO, String tableName, String memberId, String accountId, String terminalIds) throws SystemError
    {
        String filename = null;
        String filePath = null;
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
            settledReport(sheet, workbook, textBoldStyle, settlementDateVO, tableName, terminalIds, memberId, accountId);

            //Excel sheet 2 for Reverse data
            HSSFSheet sheet1 = workbook.createSheet("Reverse Report");
            refundReport(sheet1, workbook, textBoldStyle, settlementDateVO, tableName, terminalIds, memberId, accountId);

            //Excel sheet 2 for Chargeback data
            HSSFSheet sheet2 = workbook.createSheet("Chargeback Report");
            chargebackReport(sheet2, workbook, textBoldStyle, settlementDateVO, tableName, terminalIds, memberId, accountId);

            //Excel sheet 3 for Chargeback data
            HSSFSheet sheet3 = workbook.createSheet("Payout Report");
            payoutReport(sheet3, workbook, textBoldStyle, settlementDateVO, tableName, terminalIds, memberId, accountId);

            //Excel sheet 4 for Chargeback Reversed data
            HSSFSheet sheet4 = workbook.createSheet("Chargeback Reversed Report");
            chargebackReversedReport(sheet4, workbook, textBoldStyle, settlementDateVO, tableName, terminalIds, memberId, accountId);

            //Excel sheet 5 for Chargeback Reversed data
            HSSFSheet sheet5 = workbook.createSheet("Reversal Rollback Report");
            RefundReversedReport(sheet5, workbook, textBoldStyle, settlementDateVO, tableName, terminalIds, memberId, accountId);

            HSSFSheet sheet6 = workbook.createSheet("Case Filing Report");
            CaseFilingReport(sheet6, workbook, textBoldStyle, settlementDateVO, tableName, terminalIds, memberId, accountId);


            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMddHHmmss");

            String currentSystemDate = dateFormater.format(new Date());
            String[] tempStartDate = settlementDateVO.getSettlementStartDate().split(" ");
            String[] tempEndDate = settlementDateVO.getSettlementEndDate().split(" ");

            filename = "Settlement_Merchant_" + memberId + "_" + tempStartDate[0].replace("-", "") + "_" + tempEndDate[0].replace("-", "") + "_" + currentSystemDate;
            filename = filename + ".xls";
            File filepath = new File(SETTLEMENT_FILE_PATH + filename);
            filePath = filepath.getPath();
            FileOutputStream out = new FileOutputStream(filepath);
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
            logger.error("Exception--->", e);
        }
        return filename;
    }

    public String createRollingReserveTransactionFileConsoldated(RollingReserveDateVO rollingReserveDateVO, String memeberId, String accountId, String terminalIds) throws SystemError
    {
        String filename = null;
        String filePath = null;
        List<TransactionVO> transactionVOs = null;
        PayoutDAO payoutDAO = new PayoutDAO();
        try
        {
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("Sample sheet");

            HSSFRow header = sheet.createRow(0);

            sheet.setColumnWidth((short) 0, (short) 200000);
            sheet.setColumnWidth((short) 1, (short) 200000);
            sheet.setColumnWidth((short) 2, (short) 200000);
            sheet.setColumnWidth((short) 3, (short) 200000);
            sheet.setColumnWidth((short) 4, (short) 200000);
            sheet.setColumnWidth((short) 5, (short) 200000);
            sheet.setColumnWidth((short) 6, (short) 200000);
            sheet.setColumnWidth((short) 7, (short) 200000);
            sheet.setColumnWidth((short) 8, (short) 200000);

            HSSFCell cell0 = header.createCell((short) 0);
            HSSFCell cell1 = header.createCell((short) 1);
            HSSFCell cell2 = header.createCell((short) 2);
            HSSFCell cell3 = header.createCell((short) 3);
            HSSFCell cell4 = header.createCell((short) 4);
            HSSFCell cell5 = header.createCell((short) 5);
            HSSFCell cell6 = header.createCell((short) 6);
            HSSFCell cell7 = header.createCell((short) 7);
            HSSFCell cell8 = header.createCell((short) 8);
            HSSFCell cell9 = header.createCell((short) 9);

            // Start Style Added For Displaying Header Background As Gray And Strong
            HSSFCellStyle style = workbook.createCellStyle();
            style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
            style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            style.setBorderLeft((short) 1);
            style.setBorderRight((short) 1);
            style.setBorderTop((short) 1);
            style.setBorderBottom((short) 1);

            HSSFFont font = workbook.createFont();
            font.setBoldweight((short) 2);
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

            cell0.setCellValue("TerminalID");
            cell1.setCellValue("TrackingID");
            cell2.setCellValue("Description");
            cell3.setCellValue("Trade date");
            cell4.setCellValue("Amount");
            cell5.setCellValue("Currency");
            cell6.setCellValue("Current Status");
            cell7.setCellValue("Billing firstname");
            cell8.setCellValue("Billing last name");
            cell9.setCellValue("Rolling Reserve Released");

            int index = 1;

            //Get All Rolling Reservere transaction between given Rolling Reserve Start Date or End Date.
            transactionVOs = consolidatedPayoutDAO.getRollingReserveTransaction(memeberId, accountId, terminalIds, rollingReserveDateVO.getRollingReserveStartDate(), rollingReserveDateVO.getRollingReserveEndDate());
            for (TransactionVO detailsVO : transactionVOs)
            {
                HSSFRow row = sheet.createRow((short) index);
                row.createCell((short) 0).setCellValue(detailsVO.getTerminalId());
                row.createCell((short) 1).setCellValue(detailsVO.getTrackingId());
                row.createCell((short) 2).setCellValue(detailsVO.getOrderId());
                row.createCell((short) 3).setCellValue(detailsVO.getTransactionDate());
                row.createCell((short) 4).setCellValue(detailsVO.getCaptureAmount());
                row.createCell((short) 5).setCellValue(detailsVO.getCurrency());
                row.createCell((short) 6).setCellValue(detailsVO.getStatus());
                row.createCell((short) 7).setCellValue(detailsVO.getCustFirstName());
                row.createCell((short) 8).setCellValue(detailsVO.getCustLastName());
                row.createCell((short) 9).setCellValue(detailsVO.getIsRollingReserve());
                index = index + 1;
            }

            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMddHHmmss");

            String currentSystemDate = dateFormater.format(new Date());
            String[] tempStartDate = rollingReserveDateVO.getRollingReserveStartDate().split(" ");
            String[] tempEndDate = rollingReserveDateVO.getRollingReserveEndDate().split(" ");

            filename = "Rolling_Reserve_Transaction_Details_Of_" + memeberId + "_" + tempStartDate[0].replace("-", "") + "_" + tempEndDate[0].replace("-", "") + "_" + currentSystemDate;
            filename = filename + ".xls";
            File filepath = new File(SETTLEMENT_FILE_PATH + filename);
            filePath = filepath.getPath();
            FileOutputStream out = new FileOutputStream(filepath);
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

    public void settledReport(HSSFSheet sheet, HSSFWorkbook workbook, HSSFCellStyle style, SettlementDateVO settlementDateVO, String tableName, String terminalIds, String memberId, String accountId) throws SystemError
    {
        HSSFRow header = sheet.createRow(0);
        List<TransactionVO> transactionVOs = null;

        sheet.setColumnWidth((short) 0, (short) 200000);
        sheet.setColumnWidth((short) 1, (short) 200000);
        sheet.setColumnWidth((short) 2, (short) 200000);
        sheet.setColumnWidth((short) 3, (short) 200000);
        sheet.setColumnWidth((short) 4, (short) 200000);
        sheet.setColumnWidth((short) 5, (short) 200000);
        sheet.setColumnWidth((short) 6, (short) 200000);
        sheet.setColumnWidth((short) 7, (short) 200000);
        sheet.setColumnWidth((short) 8, (short) 200000);
        sheet.setColumnWidth((short) 9, (short) 200000);
        sheet.setColumnWidth((short) 10, (short) 200000);

        HSSFCell cell0 = header.createCell((short) 0);
        HSSFCell cell1 = header.createCell((short) 1);
        HSSFCell cell2 = header.createCell((short) 2);
        HSSFCell cell3 = header.createCell((short) 3);
        HSSFCell cell4 = header.createCell((short) 4);
        HSSFCell cell5 = header.createCell((short) 5);
        HSSFCell cell6 = header.createCell((short) 6);
        HSSFCell cell7 = header.createCell((short) 7);
        HSSFCell cell8 = header.createCell((short) 8);
        HSSFCell cell9 = header.createCell((short) 9);
        HSSFCell cell10 = header.createCell((short) 10);
        HSSFCell cell11 = header.createCell((short) 11);

        // Start Style Added For Displaying Header Background As Gray And Strong
        style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setBorderLeft((short) 1);
        style.setBorderRight((short) 1);
        style.setBorderTop((short) 1);
        style.setBorderBottom((short) 1);

        HSSFFont font = workbook.createFont();
        font.setBoldweight((short) 2);
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
        cell11.setCellStyle(style);

        cell0.setCellValue("Terminal ID");
        cell1.setCellValue("TrackingID");
        cell2.setCellValue("Description");
        cell3.setCellValue("Trade date");
        cell4.setCellValue("Amount");
        cell5.setCellValue("Currency");
        cell6.setCellValue("Current Status");
        cell7.setCellValue("Billing firstname");
        cell8.setCellValue("Billing last name");
        cell9.setCellValue("Wallet Currency");
        cell10.setCellValue("Wallet Amount");
        cell11.setCellValue("Settlement Currency");
        int index = 1;

        //Get All Settled The Transaction Between Date Renage Based On Dtstamp
        transactionVOs = consolidatedPayoutDAO.getTransactionDetailsByDtstamp(memberId, accountId, terminalIds, tableName, settlementDateVO.getSettlementStartDate(), settlementDateVO.getSettlementEndDate(), PZTransactionStatus.SETTLED);
        int temp = 1;
        for (TransactionVO detailsVO : transactionVOs)
        {
            int rowNum = sheet.getLastRowNum();
            if (rowNum >= 65530)
            {
                index = 1;
                temp += 1;
                HSSFRow temp_row = sheet.getRow(0);
                sheet = workbook.createSheet("Settled report".concat(String.valueOf(temp)));
                HSSFRow row = sheet.createRow(0);
                for (int i = 0; i <= temp_row.getLastCellNum(); i++)
                {
                    // Grab a copy of the old/new cell
                    HSSFCell oldCell = temp_row.getCell((short) i);
                    HSSFCell newCell = row.createCell((short) i);
                    newCell.setCellStyle(oldCell.getCellStyle());
                    newCell.setCellValue(oldCell.getStringCellValue());
                }
            }
            HSSFRow row = sheet.createRow((short) index);
            row.createCell((short) 0).setCellValue(detailsVO.getTerminalId());
            row.createCell((short) 1).setCellValue(detailsVO.getTrackingId());
            row.createCell((short) 2).setCellValue(detailsVO.getOrderId());
            row.createCell((short) 3).setCellValue(detailsVO.getTransactionDate());
            row.createCell((short) 4).setCellValue(detailsVO.getCaptureAmount());
            row.createCell((short) 5).setCellValue(detailsVO.getCurrency());
            row.createCell((short) 6).setCellValue(detailsVO.getStatus());
            row.createCell((short) 7).setCellValue(detailsVO.getCustFirstName());
            row.createCell((short) 8).setCellValue(detailsVO.getCustLastName());
            row.createCell((short) 9).setCellValue(detailsVO.getWalletCurrency());
            row.createCell((short) 10).setCellValue(detailsVO.getWalletAmount());
            row.createCell((short) 11).setCellValue(detailsVO.getSettlmentCurrency());
            index = index + 1;
        }

        //Get All reversed The Transaction Between Date Renage Based On Dtstamp
        transactionVOs = consolidatedPayoutDAO.getTransactionDetailsByDtstamp(memberId, accountId, terminalIds, tableName, settlementDateVO.getSettlementStartDate(), settlementDateVO.getSettlementEndDate(), PZTransactionStatus.REVERSED);
        for (TransactionVO detailsVO : transactionVOs)
        {
            int rowNum = sheet.getLastRowNum();
            if (rowNum >= 65530)
            {
                index = 1;
                temp += 1;
                HSSFRow temp_row = sheet.getRow(0);
                sheet = workbook.createSheet("Settled report".concat(String.valueOf(temp)));
                HSSFRow row = sheet.createRow(0);
                for (int i = 0; i <= temp_row.getLastCellNum(); i++)
                {
                    // Grab a copy of the old/new cell
                    HSSFCell oldCell = temp_row.getCell((short) i);
                    HSSFCell newCell = row.createCell((short) i);
                    newCell.setCellStyle(oldCell.getCellStyle());
                    newCell.setCellValue(oldCell.getStringCellValue());
                }
            }
            HSSFRow row = sheet.createRow((short) index);
            row.createCell((short) 0).setCellValue(detailsVO.getTerminalId());
            row.createCell((short) 1).setCellValue(detailsVO.getTrackingId());
            row.createCell((short) 2).setCellValue(detailsVO.getOrderId());
            row.createCell((short) 3).setCellValue(detailsVO.getTransactionDate());
            row.createCell((short) 4).setCellValue(detailsVO.getCaptureAmount());
            row.createCell((short) 5).setCellValue(detailsVO.getCurrency());
            row.createCell((short) 6).setCellValue(detailsVO.getStatus());
            row.createCell((short) 7).setCellValue(detailsVO.getCustFirstName());
            row.createCell((short) 8).setCellValue(detailsVO.getCustLastName());
            row.createCell((short) 9).setCellValue(detailsVO.getWalletCurrency());
            row.createCell((short) 10).setCellValue(detailsVO.getWalletAmount());
            row.createCell((short) 11).setCellValue(detailsVO.getSettlmentCurrency());
            index = index + 1;
        }

        //Get All chargeback The Transaction Between Date Renage Based On Dtstamp
        transactionVOs = consolidatedPayoutDAO.getTransactionDetailsByDtstamp(memberId, accountId, terminalIds, tableName, settlementDateVO.getSettlementStartDate(), settlementDateVO.getSettlementEndDate(), PZTransactionStatus.CHARGEBACK);
        for (TransactionVO detailsVO : transactionVOs)
        {
            int rowNum = sheet.getLastRowNum();
            if (rowNum >= 65530)
            {
                index = 1;
                temp += 1;
                HSSFRow temp_row = sheet.getRow(0);
                sheet = workbook.createSheet("Settled report".concat(String.valueOf(temp)));
                HSSFRow row = sheet.createRow(0);
                for (int i = 0; i <= temp_row.getLastCellNum(); i++)
                {
                    // Grab a copy of the old/new cell
                    HSSFCell oldCell = temp_row.getCell((short) i);
                    HSSFCell newCell = row.createCell((short) i);
                    newCell.setCellStyle(oldCell.getCellStyle());
                    newCell.setCellValue(oldCell.getStringCellValue());
                }
            }
            HSSFRow row = sheet.createRow((short) index);
            row.createCell((short) 0).setCellValue(detailsVO.getTerminalId());
            row.createCell((short) 1).setCellValue(detailsVO.getTrackingId());
            row.createCell((short) 2).setCellValue(detailsVO.getOrderId());
            row.createCell((short) 3).setCellValue(detailsVO.getTransactionDate());
            row.createCell((short) 4).setCellValue(detailsVO.getCaptureAmount());
            row.createCell((short) 5).setCellValue(detailsVO.getCurrency());
            row.createCell((short) 6).setCellValue(detailsVO.getStatus());
            row.createCell((short) 7).setCellValue(detailsVO.getCustFirstName());
            row.createCell((short) 8).setCellValue(detailsVO.getCustLastName());
            row.createCell((short) 9).setCellValue(detailsVO.getWalletCurrency());
            row.createCell((short) 10).setCellValue(detailsVO.getWalletAmount());
            row.createCell((short) 11).setCellValue(detailsVO.getSettlmentCurrency());
            index = index + 1;
        }

        //Get All AuthFailed The Transaction Between Date Renage Based On Dtstamp
        transactionVOs = consolidatedPayoutDAO.getTransactionDetailsByDtstamp(memberId, accountId, terminalIds, tableName, settlementDateVO.getDeclinedStartDate(), settlementDateVO.getDeclinedEndDate(), PZTransactionStatus.AUTH_FAILED);
        for (TransactionVO detailsVO : transactionVOs)
        {
            int rowNum = sheet.getLastRowNum();
            if (rowNum >= 65530)
            {
                index = 1;
                temp += 1;
                HSSFRow temp_row = sheet.getRow(0);
                sheet = workbook.createSheet("Settled report".concat(String.valueOf(temp)));
                HSSFRow row = sheet.createRow(0);
                for (int i = 0; i <= temp_row.getLastCellNum(); i++)
                {
                    // Grab a copy of the old/new cell
                    HSSFCell oldCell = temp_row.getCell((short) i);
                    HSSFCell newCell = row.createCell((short) i);
                    newCell.setCellStyle(oldCell.getCellStyle());
                    newCell.setCellValue(oldCell.getStringCellValue());
                }
            }
            HSSFRow row = sheet.createRow((short) index);
            row.createCell((short) 0).setCellValue(detailsVO.getTerminalId());
            row.createCell((short) 1).setCellValue(detailsVO.getTrackingId());
            row.createCell((short) 2).setCellValue(detailsVO.getOrderId());
            row.createCell((short) 3).setCellValue(detailsVO.getTransactionDate());
            row.createCell((short) 4).setCellValue(detailsVO.getCaptureAmount());
            row.createCell((short) 5).setCellValue(detailsVO.getCurrency());
            row.createCell((short) 6).setCellValue(detailsVO.getStatus());
            row.createCell((short) 7).setCellValue(detailsVO.getCustFirstName());
            row.createCell((short) 8).setCellValue(detailsVO.getCustLastName());
            row.createCell((short) 9).setCellValue(detailsVO.getWalletCurrency());
            row.createCell((short) 10).setCellValue(detailsVO.getWalletAmount());
            row.createCell((short) 11).setCellValue(detailsVO.getSettlmentCurrency());
            index = index + 1;
        }
        transactionVOs = consolidatedPayoutDAO.getTransactionDetailsByDtstamp(memberId, accountId, terminalIds, tableName, settlementDateVO.getSettlementStartDate(), settlementDateVO.getSettlementEndDate(), PZTransactionStatus.CHARGEBACKREVERSED);
        for (TransactionVO detailsVO : transactionVOs)
        {
            int rowNum = sheet.getLastRowNum();
            if (rowNum >= 65530)
            {
                index = 1;
                temp += 1;
                HSSFRow temp_row = sheet.getRow(0);
                sheet = workbook.createSheet("Settled report".concat(String.valueOf(temp)));
                HSSFRow row = sheet.createRow(0);
                for (int i = 0; i <= temp_row.getLastCellNum(); i++)
                {
                    // Grab a copy of the old/new cell
                    HSSFCell oldCell = temp_row.getCell((short) i);
                    HSSFCell newCell = row.createCell((short) i);
                    newCell.setCellStyle(oldCell.getCellStyle());
                    newCell.setCellValue(oldCell.getStringCellValue());
                }
            }
            HSSFRow row = sheet.createRow((short) index);
            row.createCell((short) 0).setCellValue(detailsVO.getTerminalId());
            row.createCell((short) 1).setCellValue(detailsVO.getTrackingId());
            row.createCell((short) 2).setCellValue(detailsVO.getOrderId());
            row.createCell((short) 3).setCellValue(detailsVO.getTransactionDate());
            row.createCell((short) 4).setCellValue(detailsVO.getCaptureAmount());
            row.createCell((short) 5).setCellValue(detailsVO.getCurrency());
            row.createCell((short) 6).setCellValue(detailsVO.getStatus());
            row.createCell((short) 7).setCellValue(detailsVO.getCustFirstName());
            row.createCell((short) 8).setCellValue(detailsVO.getCustLastName());
            row.createCell((short) 9).setCellValue(detailsVO.getWalletCurrency());
            row.createCell((short) 10).setCellValue(detailsVO.getWalletAmount());
            row.createCell((short) 11).setCellValue(detailsVO.getSettlmentCurrency());
            index = index + 1;
        }
    }

    public void refundReport(HSSFSheet sheet, HSSFWorkbook workbook, HSSFCellStyle style, SettlementDateVO settlementDateVO, String tableName, String terminalIds, String memberId, String accountId) throws SystemError
    {
        HSSFRow header = sheet.createRow(0);
        List<TransactionVO> transactionVOs = null;
        sheet.setColumnWidth((short) 0, (short) 200000);
        sheet.setColumnWidth((short) 1, (short) 200000);
        sheet.setColumnWidth((short) 2, (short) 200000);
        sheet.setColumnWidth((short) 3, (short) 200000);
        sheet.setColumnWidth((short) 4, (short) 200000);
        sheet.setColumnWidth((short) 5, (short) 200000);
        sheet.setColumnWidth((short) 6, (short) 200000);
        sheet.setColumnWidth((short) 7, (short) 200000);
        sheet.setColumnWidth((short) 8, (short) 200000);
        sheet.setColumnWidth((short) 9, (short) 200000);
        sheet.setColumnWidth((short) 10, (short) 200000);

        HSSFCell cell0 = header.createCell((short) 0);
        HSSFCell cell1 = header.createCell((short) 1);
        HSSFCell cell2 = header.createCell((short) 2);
        HSSFCell cell3 = header.createCell((short) 3);
        HSSFCell cell4 = header.createCell((short) 4);
        HSSFCell cell5 = header.createCell((short) 5);
        HSSFCell cell6 = header.createCell((short) 6);
        HSSFCell cell7 = header.createCell((short) 7);
        HSSFCell cell8 = header.createCell((short) 8);
        HSSFCell cell9 = header.createCell((short) 9);
        HSSFCell cell10 = header.createCell((short) 10);
        HSSFCell cell11 = header.createCell((short) 11);

        // Start Style Added For Displaying Header Background As Gray And Strong
        style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setBorderLeft((short) 1);
        style.setBorderRight((short) 1);
        style.setBorderTop((short) 1);
        style.setBorderBottom((short) 1);

        HSSFFont font = workbook.createFont();
        font.setBoldweight((short) 2);
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
        cell11.setCellStyle(style);

        cell0.setCellValue("Terminal ID");
        cell1.setCellValue("TrackingID");
        cell2.setCellValue("Description");
        cell3.setCellValue("Refund Date");
        cell4.setCellValue("Refund Amount");
        cell5.setCellValue("Currency");
        cell6.setCellValue("Current Status");
        cell7.setCellValue("Billing firstname");
        cell8.setCellValue("Billing last name");
        cell9.setCellValue("Wallet Currency");
        cell10.setCellValue("Wallet Amount");
        cell11.setCellValue("Settlement Currency");


        int index = 1;
        //Get All The Reversal Transaction Between Date Renge Based On TimeStamp

        transactionVOs = consolidatedPayoutDAO.getTransactionDetailsByTimestamp(memberId, accountId, terminalIds, tableName, settlementDateVO.getReversedStartDate(), settlementDateVO.getReversedEndDate(), PZTransactionStatus.REVERSED);
        int temp = 1;
        for (TransactionVO detailsVO : transactionVOs)
        {
            int rowNum = sheet.getLastRowNum();
            if (rowNum >= 65530)
            {
                index = 1;
                temp += 1;
                HSSFRow temp_row = sheet.getRow(0);
                sheet = workbook.createSheet("Reverse Report".concat(String.valueOf(temp)));
                HSSFRow row = sheet.createRow(0);
                for (int i = 0; i <= temp_row.getLastCellNum(); i++)
                {
                    // Grab a copy of the old/new cell
                    HSSFCell oldCell = temp_row.getCell((short) i);
                    HSSFCell newCell = row.createCell((short) i);
                    newCell.setCellStyle(oldCell.getCellStyle());
                    newCell.setCellValue(oldCell.getStringCellValue());
                }
            }
            HSSFRow row = sheet.createRow((short) index);
            row.createCell((short) 0).setCellValue(detailsVO.getTerminalId());
            row.createCell((short) 1).setCellValue(detailsVO.getTrackingId());
            row.createCell((short) 2).setCellValue(detailsVO.getOrderId());
            row.createCell((short) 3).setCellValue(detailsVO.getTransactionDate());
            row.createCell((short) 4).setCellValue(detailsVO.getRefundAmount());
            row.createCell((short) 5).setCellValue(detailsVO.getCurrency());
            row.createCell((short) 6).setCellValue(detailsVO.getStatus());
            row.createCell((short) 7).setCellValue(detailsVO.getCustFirstName());
            row.createCell((short) 8).setCellValue(detailsVO.getCustLastName());
            row.createCell((short) 9).setCellValue(detailsVO.getWalletCurrency());
            row.createCell((short) 10).setCellValue(detailsVO.getWalletAmount());
            row.createCell((short) 11).setCellValue(detailsVO.getSettlmentCurrency());
            index = index + 1;
        }
    }

    public void payoutReport(HSSFSheet sheet, HSSFWorkbook workbook, HSSFCellStyle style, SettlementDateVO settlementDateVO, String tableName, String terminalIds, String memberId, String accountId) throws SystemError
    {
        HSSFRow header = sheet.createRow(0);
        List<TransactionVO> transactionVOs = null;

        sheet.setColumnWidth((short) 0, (short) 200000);
        sheet.setColumnWidth((short) 1, (short) 200000);
        sheet.setColumnWidth((short) 2, (short) 200000);
        sheet.setColumnWidth((short) 3, (short) 200000);
        sheet.setColumnWidth((short) 4, (short) 200000);
        sheet.setColumnWidth((short) 5, (short) 200000);
        sheet.setColumnWidth((short) 6, (short) 200000);
        sheet.setColumnWidth((short) 7, (short) 200000);
        sheet.setColumnWidth((short) 8, (short) 200000);
        sheet.setColumnWidth((short) 9, (short) 200000);

        HSSFCell cell0 = header.createCell((short) 0);
        HSSFCell cell1 = header.createCell((short) 1);
        HSSFCell cell2 = header.createCell((short) 2);
        HSSFCell cell3 = header.createCell((short) 3);
        HSSFCell cell4 = header.createCell((short) 4);
        HSSFCell cell5 = header.createCell((short) 5);
        HSSFCell cell6 = header.createCell((short) 6);
        HSSFCell cell7 = header.createCell((short) 7);
        HSSFCell cell8 = header.createCell((short) 8);
        HSSFCell cell9 = header.createCell((short) 9);
        HSSFCell cell10 = header.createCell((short) 10);

        style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setBorderLeft((short) 1);
        style.setBorderRight((short) 1);
        style.setBorderTop((short) 1);
        style.setBorderBottom((short) 1);

        HSSFFont font = workbook.createFont();
        font.setBoldweight((short) 2);
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
        cell10.setCellStyle(style);

        cell0.setCellValue("Terminal ID");
        cell1.setCellValue("TrackingID");
        cell2.setCellValue("Description");
        cell3.setCellValue("Payout date");
        cell4.setCellValue("Payout Amount");
        cell5.setCellValue("Currency");
        cell6.setCellValue("Current Status");
        cell7.setCellValue("Billing firstname");
        cell8.setCellValue("Billing last name");
        cell9.setCellValue("Wallet Currency");
        cell10.setCellValue("Wallet Amount");


        int index = 1;
        //Get All The Payout Transaction Between Date Renge Based On TimeStamp

        transactionVOs = consolidatedPayoutDAO.getPayoutTransactionDetailsByTimestamp(memberId, accountId, terminalIds, tableName, settlementDateVO.getReversedStartDate(), settlementDateVO.getReversedEndDate(), PZTransactionStatus.PAYOUT_SUCCESS);
        int temp = 1;
        for (TransactionVO detailsVO : transactionVOs)
        {
            int rowNum = sheet.getLastRowNum();
            if (rowNum >= 65530)
            {
                index = 1;
                temp += 1;
                HSSFRow temp_row = sheet.getRow(0);
                sheet = workbook.createSheet("Payout Report".concat(String.valueOf(temp)));
                HSSFRow row = sheet.createRow(0);
                for (int i = 0; i <= temp_row.getLastCellNum(); i++)
                {
                    // Grab a copy of the old/new cell
                    HSSFCell oldCell = temp_row.getCell((short) i);
                    HSSFCell newCell = row.createCell((short) i);
                    newCell.setCellStyle(oldCell.getCellStyle());
                    newCell.setCellValue(oldCell.getStringCellValue());
                }
            }
            HSSFRow row = sheet.createRow((short) index);
            row.createCell((short) 0).setCellValue(detailsVO.getTerminalId());
            row.createCell((short) 1).setCellValue(detailsVO.getTrackingId());
            row.createCell((short) 2).setCellValue(detailsVO.getOrderId());
            row.createCell((short) 3).setCellValue(detailsVO.getTransactionDate());
            row.createCell((short) 4).setCellValue(detailsVO.getPayoutAmount());
            row.createCell((short) 5).setCellValue(detailsVO.getCurrency());
            row.createCell((short) 6).setCellValue(detailsVO.getStatus());
            row.createCell((short) 7).setCellValue(detailsVO.getCustFirstName());
            row.createCell((short) 8).setCellValue(detailsVO.getCustLastName());
            row.createCell((short) 9).setCellValue(detailsVO.getWalletCurrency());
            row.createCell((short) 10).setCellValue(detailsVO.getWalletAmount());
            index = index + 1;
        }
    }

    public void RefundReversedReport(HSSFSheet sheet, HSSFWorkbook workbook, HSSFCellStyle style, SettlementDateVO settlementDateVO, String tableName, String terminalIds, String memberId, String accountId) throws SystemError
    {
        HSSFRow header = sheet.createRow(0);
        List<TransactionVO> transactionVOs = null;

        sheet.setColumnWidth((short) 0, (short) 200000);
        sheet.setColumnWidth((short) 1, (short) 200000);
        sheet.setColumnWidth((short) 2, (short) 200000);
        sheet.setColumnWidth((short) 3, (short) 200000);
        sheet.setColumnWidth((short) 4, (short) 200000);
        sheet.setColumnWidth((short) 5, (short) 200000);
        sheet.setColumnWidth((short) 6, (short) 200000);
        sheet.setColumnWidth((short) 7, (short) 200000);
        sheet.setColumnWidth((short) 8, (short) 200000);
        sheet.setColumnWidth((short) 9, (short) 200000);
        sheet.setColumnWidth((short) 10, (short) 200000);

        HSSFCell cell0 = header.createCell((short) 0);
        HSSFCell cell1 = header.createCell((short) 1);
        HSSFCell cell2 = header.createCell((short) 2);
        HSSFCell cell3 = header.createCell((short) 3);
        HSSFCell cell4 = header.createCell((short) 4);
        HSSFCell cell5 = header.createCell((short) 5);
        HSSFCell cell6 = header.createCell((short) 6);
        HSSFCell cell7 = header.createCell((short) 7);
        HSSFCell cell8 = header.createCell((short) 8);
        HSSFCell cell9 = header.createCell((short) 9);
        HSSFCell cell10 = header.createCell((short) 10);

        style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setBorderLeft((short) 1);
        style.setBorderRight((short) 1);
        style.setBorderTop((short) 1);
        style.setBorderBottom((short) 1);

        HSSFFont font = workbook.createFont();
        font.setBoldweight((short) 2);
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
        cell10.setCellStyle(style);

        cell0.setCellValue("Terminal ID");
        cell1.setCellValue("TrackingID");
        cell2.setCellValue("Description");
        cell3.setCellValue("Refund Rollback date");
        cell4.setCellValue("Refund Rollback Amount");
        cell5.setCellValue("Currency");
        cell6.setCellValue("Current Status");
        cell7.setCellValue("Billing firstname");
        cell8.setCellValue("Billing last name");
        cell9.setCellValue("Wallet Currency");
        cell10.setCellValue("Wallet Amount");
        int index = 1;
        transactionVOs = consolidatedPayoutDAO.getRefundReverseTransactionDetailsByTimestamp(memberId, accountId, terminalIds, tableName, settlementDateVO.getReversedStartDate(), settlementDateVO.getReversedEndDate(), null);
            int temp = 1;
            for (TransactionVO detailsVO : transactionVOs)
            {
                int rowNum = sheet.getLastRowNum();
                if (rowNum >= 65530)
                {
                    index = 1;
                    temp += 1;
                    HSSFRow temp_row = sheet.getRow(0);
                    sheet = workbook.createSheet("Reversal Rollback Report".concat(String.valueOf(temp)));
                    HSSFRow row = sheet.createRow(0);
                    for (int i = 0; i <= temp_row.getLastCellNum(); i++)
                    {
                        // Grab a copy of the old/new cell
                        HSSFCell oldCell = temp_row.getCell((short) i);
                        HSSFCell newCell = row.createCell((short) i);
                        newCell.setCellStyle(oldCell.getCellStyle());
                        newCell.setCellValue(oldCell.getStringCellValue());
                    }
                }
            HSSFRow row = sheet.createRow((short) index);
            row.createCell((short) 0).setCellValue(detailsVO.getTerminalId());
            row.createCell((short) 1).setCellValue(detailsVO.getTrackingId());
            row.createCell((short) 2).setCellValue(detailsVO.getOrderId());
            row.createCell((short) 3).setCellValue(detailsVO.getTransactionDate());
            row.createCell((short) 4).setCellValue(detailsVO.getAmount());
            row.createCell((short) 5).setCellValue(detailsVO.getCurrency());
            row.createCell((short) 6).setCellValue(detailsVO.getStatus());
            row.createCell((short) 7).setCellValue(detailsVO.getCustFirstName());
            row.createCell((short) 8).setCellValue(detailsVO.getCustLastName());
            row.createCell((short) 9).setCellValue(detailsVO.getWalletCurrency());
            row.createCell((short) 10).setCellValue(detailsVO.getWalletAmount());
            index = index + 1;
        }
    }

    public void CaseFilingReport(HSSFSheet sheet, HSSFWorkbook workbook, HSSFCellStyle style, SettlementDateVO settlementDateVO, String tableName, String terminalIds, String memberId, String accountId) throws SystemError
    {
        HSSFRow header = sheet.createRow(0);
        List<TransactionVO> transactionVOs = null;

        sheet.setColumnWidth((short) 0, (short) 200000);
        sheet.setColumnWidth((short) 1, (short) 200000);
        sheet.setColumnWidth((short) 2, (short) 200000);
        sheet.setColumnWidth((short) 3, (short) 200000);
        sheet.setColumnWidth((short) 4, (short) 200000);
        sheet.setColumnWidth((short) 5, (short) 200000);
        sheet.setColumnWidth((short) 6, (short) 200000);
        sheet.setColumnWidth((short) 7, (short) 200000);
        sheet.setColumnWidth((short) 8, (short) 200000);
        sheet.setColumnWidth((short) 9, (short) 200000);
        sheet.setColumnWidth((short) 10, (short) 200000);

        HSSFCell cell0 = header.createCell((short) 0);
        HSSFCell cell1 = header.createCell((short) 1);
        HSSFCell cell2 = header.createCell((short) 2);
        HSSFCell cell3 = header.createCell((short) 3);
        HSSFCell cell4 = header.createCell((short) 4);
        HSSFCell cell5 = header.createCell((short) 5);
        HSSFCell cell6 = header.createCell((short) 6);
        HSSFCell cell7 = header.createCell((short) 7);
        HSSFCell cell8 = header.createCell((short) 8);
        HSSFCell cell9 = header.createCell((short) 9);
        HSSFCell cell10 = header.createCell((short) 10);

        style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setBorderLeft((short) 1);
        style.setBorderRight((short) 1);
        style.setBorderTop((short) 1);
        style.setBorderBottom((short) 1);

        HSSFFont font = workbook.createFont();
        font.setBoldweight((short) 2);
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
        cell10.setCellStyle(style);

        cell0.setCellValue("Terminal ID");
        cell1.setCellValue("TrackingID");
        cell2.setCellValue("Description");
        cell3.setCellValue("Case Filing Date");
        cell4.setCellValue("Case Filing Amount");
        cell5.setCellValue("Currency");
        cell6.setCellValue("Current Status");
        cell7.setCellValue("Billing firstname");
        cell8.setCellValue("Billing last name");
        cell9.setCellValue("Wallet Currency");
        cell10.setCellValue("Wallet Amount");
        int index = 1;
        transactionVOs = consolidatedPayoutDAO.getCaseFilingTransactionDetailsByTimestamp(memberId, accountId, terminalIds, tableName, settlementDateVO.getChargebackStartDate(), settlementDateVO.getChargebackEndDate(), null);
        int temp = 1;
        for (TransactionVO detailsVO : transactionVOs)
        {
            int rowNum = sheet.getLastRowNum();
            if (rowNum >= 65530)
            {
                index = 1;
                temp += 1;
                HSSFRow temp_row = sheet.getRow(0);
                sheet = workbook.createSheet("Case Filing Report".concat(String.valueOf(temp)));
                HSSFRow row = sheet.createRow(0);
                for (int i = 0; i <= temp_row.getLastCellNum(); i++)
                {
                    // Grab a copy of the old/new cell
                    HSSFCell oldCell = temp_row.getCell((short) i);
                    HSSFCell newCell = row.createCell((short) i);
                    newCell.setCellStyle(oldCell.getCellStyle());
                    newCell.setCellValue(oldCell.getStringCellValue());
                }
            }
            HSSFRow row = sheet.createRow((short) index);
            row.createCell((short) 0).setCellValue(detailsVO.getTerminalId());
            row.createCell((short) 1).setCellValue(detailsVO.getTrackingId());
            row.createCell((short) 2).setCellValue(detailsVO.getOrderId());
            row.createCell((short) 3).setCellValue(detailsVO.getTransactionDate());
            row.createCell((short) 4).setCellValue(detailsVO.getAmount());
            row.createCell((short) 5).setCellValue(detailsVO.getCurrency());
            row.createCell((short) 6).setCellValue(detailsVO.getStatus());
            row.createCell((short) 7).setCellValue(detailsVO.getCustFirstName());
            row.createCell((short) 8).setCellValue(detailsVO.getCustLastName());
            row.createCell((short) 9).setCellValue(detailsVO.getWalletCurrency());
            row.createCell((short) 10).setCellValue(detailsVO.getWalletAmount());
            index = index + 1;
        }
    }

    public void chargebackReport(HSSFSheet sheet, HSSFWorkbook workbook, HSSFCellStyle style, SettlementDateVO settlementDateVO, String tableName, String terminalIds, String memberId, String accountId) throws SystemError
    {
        HSSFRow header = sheet.createRow(0);
        List<TransactionVO> transactionVOs = null;
        PayoutDAO payoutDAO = new PayoutDAO();
        sheet.setColumnWidth((short) 0, (short) 200000);
        sheet.setColumnWidth((short) 1, (short) 200000);
        sheet.setColumnWidth((short) 2, (short) 200000);
        sheet.setColumnWidth((short) 3, (short) 200000);
        sheet.setColumnWidth((short) 4, (short) 200000);
        sheet.setColumnWidth((short) 5, (short) 200000);
        sheet.setColumnWidth((short) 6, (short) 200000);
        sheet.setColumnWidth((short) 7, (short) 200000);
        sheet.setColumnWidth((short) 8, (short) 200000);
        sheet.setColumnWidth((short) 9, (short) 200000);
        sheet.setColumnWidth((short) 10, (short) 200000);

        HSSFCell cell0 = header.createCell((short) 0);
        HSSFCell cell1 = header.createCell((short) 1);
        HSSFCell cell2 = header.createCell((short) 2);
        HSSFCell cell3 = header.createCell((short) 3);
        HSSFCell cell4 = header.createCell((short) 4);
        HSSFCell cell5 = header.createCell((short) 5);
        HSSFCell cell6 = header.createCell((short) 6);
        HSSFCell cell7 = header.createCell((short) 7);
        HSSFCell cell8 = header.createCell((short) 8);
        HSSFCell cell9 = header.createCell((short) 9);
        HSSFCell cell10 = header.createCell((short) 10);
        HSSFCell cell11 = header.createCell((short) 10);

        // Start Style Added For Displaying Header Background As Gray And Strong
        style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setBorderLeft((short) 1);
        style.setBorderRight((short) 1);
        style.setBorderTop((short) 1);
        style.setBorderBottom((short) 1);

        HSSFFont font = workbook.createFont();
        font.setBoldweight((short) 2);
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
        cell11.setCellStyle(style);

        cell0.setCellValue("Terminal ID");
        cell1.setCellValue("TrackingID");
        cell2.setCellValue("Description");
        cell3.setCellValue("Chargeback date");
        cell4.setCellValue("Chargeback Amount");
        cell5.setCellValue("Currency");
        cell6.setCellValue("Current Status");
        cell7.setCellValue("Billing firstname");
        cell8.setCellValue("Billing last name");
        cell9.setCellValue("Wallet Currency");
        cell10.setCellValue("Wallet Amount");
        cell11.setCellValue("Settlement Currency");
        int index = 1;
        //Get All The Chargeback Transaction Between Date Renge Based On TimeStamp
        transactionVOs = consolidatedPayoutDAO.getTransactionDetailsByTimestampForChargeback(memberId, accountId, terminalIds, tableName, settlementDateVO.getChargebackStartDate(), settlementDateVO.getChargebackEndDate(), PZTransactionStatus.CHARGEBACK);
        int temp = 1;
        for (TransactionVO detailsVO : transactionVOs)
        {
            int rowNum = sheet.getLastRowNum();
            if (rowNum >= 65530)
            {
                index = 1;
                temp += 1;
                HSSFRow temp_row = sheet.getRow(0);
                sheet = workbook.createSheet("Chargeback Report".concat(String.valueOf(temp)));
                HSSFRow row = sheet.createRow(0);
                for (int i = 0; i <= temp_row.getLastCellNum(); i++)
                {
                    // Grab a copy of the old/new cell
                    HSSFCell oldCell = temp_row.getCell((short) i);
                    HSSFCell newCell = row.createCell((short) i);
                    newCell.setCellStyle(oldCell.getCellStyle());
                    newCell.setCellValue(oldCell.getStringCellValue());
                }
            }
            HSSFRow row = sheet.createRow((short) index);
            row.createCell((short) 0).setCellValue(detailsVO.getTerminalId());
            row.createCell((short) 1).setCellValue(detailsVO.getTrackingId());
            row.createCell((short) 2).setCellValue(detailsVO.getOrderId());
            row.createCell((short) 3).setCellValue(detailsVO.getTransactionDate());
            row.createCell((short) 4).setCellValue(detailsVO.getChargebackAmount());
            row.createCell((short) 5).setCellValue(detailsVO.getCurrency());
            row.createCell((short) 6).setCellValue(detailsVO.getStatus());
            row.createCell((short) 7).setCellValue(detailsVO.getCustFirstName());
            row.createCell((short) 8).setCellValue(detailsVO.getCustLastName());
            row.createCell((short) 9).setCellValue(detailsVO.getWalletCurrency());
            row.createCell((short) 10).setCellValue(detailsVO.getWalletAmount());
            row.createCell((short) 11).setCellValue(detailsVO.getSettlmentCurrency());
            index = index + 1;
        }
    }

    public void chargebackReversedReport(HSSFSheet sheet, HSSFWorkbook workbook, HSSFCellStyle style, SettlementDateVO settlementDateVO, String tableName, String terminalIds, String memberId, String accountId) throws SystemError
    {
        HSSFRow header = sheet.createRow(0);
        List<TransactionVO> transactionVOs = null;

        sheet.setColumnWidth((short) 0, (short) 200000);
        sheet.setColumnWidth((short) 1, (short) 200000);
        sheet.setColumnWidth((short) 2, (short) 200000);
        sheet.setColumnWidth((short) 3, (short) 200000);
        sheet.setColumnWidth((short) 4, (short) 200000);
        sheet.setColumnWidth((short) 5, (short) 200000);
        sheet.setColumnWidth((short) 6, (short) 200000);
        sheet.setColumnWidth((short) 7, (short) 200000);
        sheet.setColumnWidth((short) 8, (short) 200000);
        sheet.setColumnWidth((short) 9, (short) 200000);

        HSSFCell cell0 = header.createCell((short) 0);
        HSSFCell cell1 = header.createCell((short) 1);
        HSSFCell cell2 = header.createCell((short) 2);
        HSSFCell cell3 = header.createCell((short) 3);
        HSSFCell cell4 = header.createCell((short) 4);
        HSSFCell cell5 = header.createCell((short) 5);
        HSSFCell cell6 = header.createCell((short) 6);
        HSSFCell cell7 = header.createCell((short) 7);
        HSSFCell cell8 = header.createCell((short) 8);
        HSSFCell cell9 = header.createCell((short) 9);
        HSSFCell cell10 = header.createCell((short) 10);

        // Start Style Added For Displaying Header Background As Gray And Strong
        style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setBorderLeft((short) 1);
        style.setBorderRight((short) 1);
        style.setBorderTop((short) 1);
        style.setBorderBottom((short) 1);

        HSSFFont font = workbook.createFont();
        font.setBoldweight((short) 2);
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

        cell0.setCellValue("Terminal ID");
        cell1.setCellValue("Tracking ID");
        cell2.setCellValue("Description");
        cell3.setCellValue("Chargeback Reversed date");
        cell4.setCellValue("Chargeback Reversed Amount");
        cell5.setCellValue("Currency");
        cell6.setCellValue("Current Status");
        cell7.setCellValue("Billing firstname");
        cell8.setCellValue("Billing last name");
        cell9.setCellValue("Wallet Currency");
        cell10.setCellValue("Wallet Amount");
        int index = 1;
        //Get All The Chargeback Reversed Transaction Between Date Renge Based On TimeStamp
        transactionVOs = consolidatedPayoutDAO.geTransactionDetailsForChargebackReversed(memberId, accountId, terminalIds, tableName, settlementDateVO.getChargebackStartDate(), settlementDateVO.getChargebackEndDate(), PZTransactionStatus.CHARGEBACKREVERSED);
        try
        {
            int temp = 1;
            for (TransactionVO detailsVO : transactionVOs)
            {
                int rowNum = sheet.getLastRowNum();
                if (rowNum >= 65530)
                {
                    index = 1;
                    temp += 1;
                    HSSFRow temp_row = sheet.getRow(0);
                    sheet = workbook.createSheet("Chargeback Reversed Report".concat(String.valueOf(temp)));
                    HSSFRow row = sheet.createRow(0);
                    for (int i = 0; i <= temp_row.getLastCellNum(); i++)
                    {
                        // Grab a copy of the old/new cell
                        HSSFCell oldCell = temp_row.getCell((short) i);
                        HSSFCell newCell = row.createCell((short) i);
                        newCell.setCellStyle(oldCell.getCellStyle());
                        newCell.setCellValue(oldCell.getStringCellValue());
                    }
                }
                HSSFRow row = sheet.createRow((short) index);
                row.createCell((short) 0).setCellValue(detailsVO.getTerminalId());
                row.createCell((short) 1).setCellValue(detailsVO.getTrackingId());
                row.createCell((short) 2).setCellValue(detailsVO.getOrderId());
                row.createCell((short) 3).setCellValue(detailsVO.getTransactionDate());
                row.createCell((short) 4).setCellValue(detailsVO.getChargebackAmount());
                row.createCell((short) 5).setCellValue(detailsVO.getCurrency());
                row.createCell((short) 6).setCellValue(detailsVO.getStatus());
                row.createCell((short) 7).setCellValue(detailsVO.getCustFirstName());
                row.createCell((short) 8).setCellValue(detailsVO.getCustLastName());
                row.createCell((short) 9).setCellValue(detailsVO.getWalletCurrency());
                row.createCell((short) 10).setCellValue(detailsVO.getWalletAmount());
                index = index + 1;
            }
        }
        catch (Exception e)
        {
            logger.error("Exception::::", e);
        }
    }

    //Agent Consolidated Report.
    public List<String> agentCommissionConsolidatedReport(String startDate, String endDate, HashMap<String, String> currencyConversion, TreeMap<String, TerminalVO> agentTerminalHash, List<TerminalVO> pendingList) throws Exception
    {
        SettlementDateVO settlementDateVO = null;
        List<String> stringList = new ArrayList<String>();
        String isSettlementCronExecuted = "Y";
        String isAgentCommissionCronExecuted = "N";
        String isPayoutCronExcuted = "Y";

        ChargeManager chargeManager = new ChargeManager();
        PartnerDAO partnerDAO = new PartnerDAO();
        AgentDAO agentDAO = new AgentDAO();
        MerchantDAO merchantDAO = new MerchantDAO();
        MerchantDetailsVO merchantDetailsVO = null;
        PartnerDetailsVO partnerDetailsVO = null;
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Set memberDetails = new HashSet<>();
        Set terminalDetails = new HashSet<>();
        HashMap<String, String> currencySet = new HashMap<>();

        Set<String> key = agentTerminalHash.keySet();
        AgentDetailsVO agentDetailsVO = null;
        HashMap<String, HashMap<String, CommissionDetailsVO>> commissionDetailsVOHashMap = new LinkedHashMap<String, HashMap<String, CommissionDetailsVO>>();
        HashMap<String, HashMap<String, ChargeDetailsVO>> chargeDetailsVOHashMap = new LinkedHashMap<>();
        Timestamp timestart = null;
        Timestamp timeend = null;
        for (String agentTerminalWireId : key)
        {
            TerminalVO terminalVO1 = agentTerminalHash.get(agentTerminalWireId);
            agentDetailsVO = agentDAO.getAgentDetails(terminalVO1.getAgentId());
            BankWireManagerVO bankWireManagerVO = payoutDAO.getBankWireListForAgentCommissionCron(terminalVO1.getWireId(), isSettlementCronExecuted, isAgentCommissionCronExecuted, isPayoutCronExcuted);
            DateVO dateVO = new DateVO();
            if (timestart == null && timeend == null)
            {
                timestart = convertStringToTimestamp1(bankWireManagerVO.getServer_start_date());
                timeend = convertStringToTimestamp1(bankWireManagerVO.getServer_end_date());
            }
            Timestamp timestampSD = convertStringToTimestamp1(bankWireManagerVO.getServer_start_date());
            Timestamp timestampED = convertStringToTimestamp1(bankWireManagerVO.getServer_end_date());
            if (timestampSD.before(timestart))
            {
                timestart = timestampSD;
            }
            if (timestampED.after(timeend))
            {
                timeend = timestampED;
            }
            logger.error("StartDate:::" + timestart + "--EndDate--" + timeend);
            startDate = String.valueOf(timestart);
            endDate = String.valueOf(timeend);
            dateVO.setStartDate(bankWireManagerVO.getServer_start_date());
            dateVO.setEndDate(bankWireManagerVO.getServer_end_date());
            String memberId = terminalVO1.getMemberId();
            String terminalId = terminalVO1.getTerminalId();
            String accountId = terminalVO1.getAccountId();
            String wireId = terminalVO1.getWireId();
            if (!memberDetails.contains(memberId))
            {
                memberDetails.add(memberId);
            }
            if (!terminalDetails.contains(terminalId))
            {
                terminalDetails.add(terminalId);
            }
            String tableName = accountUtil.getTableNameSettlement(accountId);
            TerminalManager terminalManager = new TerminalManager();
            terminalVO1 = terminalManager.getTerminalsByMemberAccountIdForPayoutReportRequest(memberId, accountId, terminalId);
            terminalVO1.setWireId(wireId);
            merchantDetailsVO = merchantDAO.getMemberDetails(memberId);
            partnerDetailsVO = partnerDAO.getPartnerDetails(merchantDetailsVO.getPartnerId());
            List<AgentCommissionVO> agentCommissionVOs = chargeManager.getAgentCommissionOnTerminal(agentDetailsVO.getAgentId(), memberId, terminalVO1.getTerminalId());
            settlementDateVO = new SettlementDateVO();
            settlementDateVO.setSettlementStartDate(targetFormat.format(targetFormat.parse(bankWireManagerVO.getServer_start_date())));
            if (functions.isValueNull(bankWireManagerVO.getDeclinedcoveredStartdate()))
            {
                settlementDateVO.setDeclinedStartDate(targetFormat.format(targetFormat.parse(bankWireManagerVO.getDeclinedcoveredStartdate())));
            }
            else
            {
                settlementDateVO.setDeclinedStartDate(targetFormat.format(targetFormat.parse(bankWireManagerVO.getServer_start_date())));
            }
            if (functions.isValueNull(bankWireManagerVO.getReversedCoveredStartdate()))
            {
                settlementDateVO.setReversedStartDate(targetFormat.format(targetFormat.parse(bankWireManagerVO.getReversedCoveredStartdate())));
            }
            else
            {
                settlementDateVO.setReversedStartDate(targetFormat.format(targetFormat.parse(bankWireManagerVO.getServer_start_date())));
            }
            if (functions.isValueNull(bankWireManagerVO.getChargebackcoveredStartdate()))
            {
                settlementDateVO.setChargebackStartDate(targetFormat.format(targetFormat.parse(bankWireManagerVO.getChargebackcoveredStartdate())));
            }
            else
            {
                settlementDateVO.setChargebackStartDate(targetFormat.format(targetFormat.parse(bankWireManagerVO.getServer_start_date())));
            }
            settlementDateVO.setSettlementcycleNumber(bankWireManagerVO.getBankwiremanagerId());
            settlementDateVO.setSettlementEndDate(targetFormat.format(targetFormat.parse(bankWireManagerVO.getServer_end_date())));
            settlementDateVO.setDeclinedEndDate(targetFormat.format(targetFormat.parse(bankWireManagerVO.getDeclinedcoveredupto())));
            settlementDateVO.setReversedEndDate(targetFormat.format(targetFormat.parse(bankWireManagerVO.getReversedCoveredUpto())));
            settlementDateVO.setChargebackEndDate(targetFormat.format(targetFormat.parse(bankWireManagerVO.getChargebackcoveredupto())));
            TransactionSummaryVO transactionSummaryVO = getTotalSuccessCountAmountByTerminalNew(terminalVO1, settlementDateVO, tableName);

            AgentCommissionReportVO agentCommissionReportVO = new AgentCommissionReportVO();
            agentCommissionReportVO.setMerchantDetailsVO(merchantDetailsVO);
            agentCommissionReportVO.setPartnerDetailsVO(partnerDetailsVO);
            agentCommissionReportVO.setAgentCommissionVOList(agentCommissionVOs);
            agentCommissionReportVO.setSettlementDateVO(settlementDateVO);
            agentCommissionReportVO.setTransactionSummaryVO(transactionSummaryVO);
            agentCommissionReportVO.setTerminalVO(terminalVO1);
            agentCommissionReportVO.setAgentDetailsVO(agentDetailsVO);
            RollingReserveDateVO rollingReserveDateVO = new RollingReserveDateVO();
            rollingReserveDateVO.setRollingReserveStartDate(bankWireManagerVO.getRollingreservereleaseStartdate());
            rollingReserveDateVO.setRollingReserveEndDate(bankWireManagerVO.getRollingreservereleasedateupto());
            /*TerminalVO terminalVO= agentCommissionReportVO.getTerminalVO();
            MerchantDetailsVO merchantDetailsVO= agentCommissionReportVO.getMerchantDetailsVO();
            List<AgentCommissionVO> agentCommissionVOs= agentCommissionReportVO.getAgentCommissionVOList();
            PartnerDetailsVO partnerDetailsVO= agentCommissionReportVO.getPartnerDetailsVO();
            SettlementDateVO settlementDateVO= agentCommissionReportVO.getSettlementDateVO();
            TransactionSummaryVO transactionSummaryVO= agentCommissionReportVO.getTransactionSummaryVO();
            AgentDetailsVO agentDetailsVO=agentCommissionReportVO.getAgentDetailsVO();

            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
*/
            String settlementStartDate = targetFormat.format(targetFormat.parse(settlementDateVO.getSettlementStartDate()));
            String settlementEndDate = targetFormat.format(targetFormat.parse(settlementDateVO.getSettlementEndDate()));

            settlementDateVO.setSettlementStartDate(settlementStartDate);
            settlementDateVO.setSettlementEndDate(settlementEndDate);

            GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
            GatewayType gatewayType = GatewayTypeService.getGatewayType(account.getPgTypeId());

            String currency = gatewayType.getCurrency();
            currencySet.put(terminalId, currency);

            double totalCommissionAmount = 0.00;
            double totalChargesAmount = 0.00;
            double agentTotalFundedAmount = 0.00;
            double previousBalance = payoutDAO.getAgentUnpaidBalanceAmountOnTerminal(terminalVO1);
            double wireFee = 0.00;

            HashMap<String, CommissionDetailsVO> commissionDetailsVOs = new HashMap<>();
            HashMap<String, ChargeDetailsVO> chargeDetailsVOList = new HashMap<>();

            WireChargeVO wireChargeVO = null;
            long count = 0;
            double amount = 0.00;
            for (AgentCommissionVO agentCommissionVO : agentCommissionVOs)
            {
                ChargeMasterVO chargeMasterVO = agentCommissionVO.getChargeMasterVO();
                CommissionDetailsVO commissionDetailsVO = new CommissionDetailsVO();
                logger.error("KeyWord:::::" + chargeMasterVO.getKeyword());
                commissionDetailsVOs = commissionDetailsVOHashMap.get(terminalId);
                if (commissionDetailsVOs == null)
                {
                    commissionDetailsVOs = new HashMap<>();
                }
                commissionDetailsVO.setChargeName(chargeMasterVO.getChargeName());
                commissionDetailsVO.setValueType(chargeMasterVO.getValueType());
                commissionDetailsVO.setChargeValue(String.valueOf(agentCommissionVO.getCommissionValue()));

                if (Charge_category.Others.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.FraudulentTransaction.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeMasterVO.getSubKeyword()))
                {
                    if (commissionDetailsVOHashMap.get(terminalId) != null)
                    {
                        commissionDetailsVOs = commissionDetailsVOHashMap.get(terminalId);
                        CommissionDetailsVO commissionDetailsVO1 = commissionDetailsVOs.get(chargeMasterVO.getChargeName());
                        if (commissionDetailsVO1 != null)
                        {
                            count = commissionDetailsVO1.getCount();
                            amount = commissionDetailsVO1.getAmount();
                        }
                    }
                    long vCntCounter = getPaidFraudulentTransactionCountOnTerminal(terminalVO1);
                    double total = (vCntCounter + count) * agentCommissionVO.getCommissionValue();
                    commissionDetailsVO.setCount(vCntCounter + count);
                    commissionDetailsVO.setAmount(amount);
                    commissionDetailsVO.setTotal(Functions.roundDBL(total, 2));
                }
                else if (Charge_category.Success.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.TotalReserveRefunded.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeMasterVO.getSubKeyword()))
                {
                    if (commissionDetailsVOHashMap.get(terminalId) != null)
                    {
                        commissionDetailsVOs = commissionDetailsVOHashMap.get(terminalId);
                        CommissionDetailsVO commissionDetailsVO1 = commissionDetailsVOs.get(chargeMasterVO.getChargeName());
                        if (commissionDetailsVO1 != null)
                        {
                            count = commissionDetailsVO1.getCount();
                            amount = commissionDetailsVO1.getAmount();
                        }
                    }
                    logger.error("inside TotalReserveRefunded----" + count + "----" + amount);
                    double reserveRefundedChargeFee = 0.0;
                    tableName = accountUtil.getTableNameSettlement(accountId);
                    TransactionSummaryVO amountAndCount = payoutDAO.getReleaseCountAndAmount(rollingReserveDateVO, terminalVO1, tableName);
                    reserveRefundedChargeFee = ((amountAndCount.getReserveRefundAmount() + amount) * Double.valueOf(agentCommissionVO.getCommissionValue())) / 100;
                    logger.error("inside TotalReserveRefunded two----" + amountAndCount.getCountOfreserveRefund() + "----" + amountAndCount.getReserveRefundAmount());

                    commissionDetailsVO.setCount(amountAndCount.getCountOfreserveRefund() + count);
                    commissionDetailsVO.setAmount(amountAndCount.getReserveRefundAmount() + amount);
                    commissionDetailsVO.setTotal(Functions.roundDBL(reserveRefundedChargeFee, 2));
                }
                else if (chargeMasterVO.getCategory().equals(Charge_category.Others.toString()) && chargeMasterVO.getKeyword().equals(Charge_keyword.Statement.toString()) && chargeMasterVO.getSubKeyword().equals(Charge_subKeyword.Count.toString()))
                {
                    if (commissionDetailsVOHashMap.get(terminalId) != null)
                    {
                        commissionDetailsVOs = commissionDetailsVOHashMap.get(terminalId);
                        CommissionDetailsVO commissionDetailsVO1 = commissionDetailsVOs.get(chargeMasterVO.getChargeName());
                        if (commissionDetailsVO1 != null)
                        {
                            count = commissionDetailsVO1.getCount();
                            amount = commissionDetailsVO1.getAmount();
                        }
                    }
                    long vCntCounter = payoutDAO.getMerchantPaidWireCountForCommissionCalculation(terminalVO1, settlementDateVO);//get the total count of paid wires from merchant_wiremanager between settlement date
                    double vDblAmount = 0.00;
                    double vDblTotal = (vCntCounter + count) * agentCommissionVO.getCommissionValue();

                    commissionDetailsVO.setCount(vCntCounter + count);
                    commissionDetailsVO.setAmount(vDblAmount + amount);
                    commissionDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
                }
                /*else if (chargeMasterVO.getCategory().equals(Charge_category.Others.toString()) && chargeMasterVO.getKeyword().equals(Charge_keyword.GrossBalanceAmount.toString()) && chargeMasterVO.getSubKeyword().equals(Charge_subKeyword.Amount.toString()))
                {
                    GrossChargeVO grossChargeVO = new GrossChargeVO();
                    grossChargeVO.setChargeName(chargeVO.getChargename());
                    grossChargeVO.setChargeValue(chargeVO.getChargevalue());
                    grossChargeVO.setValueType(chargeVO.getValuetype());
                    grossChargeVOMap.put(grossChargeVO.getChargeName(), grossChargeVO);
                    chargeDetailsVOList.add(setupChargeVO);
                    chargeDetailsMapOFVOs.put(terminalName, chargeDetailsVOList);
                }
                else if (chargeMasterVO.getCategory().equals(Charge_category.Others.toString()) && chargeMasterVO.getKeyword().equals(Charge_keyword.ServiceTax.toString()) && chargeMasterVO.getSubKeyword().equals(Charge_subKeyword.Amount.toString()))
                {
                    ServiceTaxChargeVO serviceTaxChargeVO = new ServiceTaxChargeVO();
                    serviceTaxChargeVO.setChargeName(chargeVO.getChargename());
                    serviceTaxChargeVO.setChargeValue(chargeVO.getChargevalue());
                    serviceTaxChargeVO.setValueType(chargeVO.getValuetype());
                    serviceTaxChargeVOHashMap.put(chargeDetailsVO.getChargeName(), serviceTaxChargeVO);
                    chargeDetailsVOList.add(serviceTaxChargeVO);
                    chargeDetailsMapOFVOs.put(terminalName, chargeDetailsVOList);
                }*/
                else if (chargeMasterVO.getCategory().equals(Charge_category.Others.toString()) && chargeMasterVO.getKeyword().equals(Charge_keyword.Setup.toString()) && chargeMasterVO.getSubKeyword().equals(Charge_subKeyword.Count.toString()))
                {
                    if (commissionDetailsVOHashMap.get(terminalId) != null)
                    {
                        commissionDetailsVOs = commissionDetailsVOHashMap.get(terminalId);
                        CommissionDetailsVO commissionDetailsVO1 = commissionDetailsVOs.get(chargeMasterVO.getChargeName());
                        if (commissionDetailsVO1 != null)
                        {
                            count = commissionDetailsVO1.getCount();
                            amount = commissionDetailsVO1.getAmount();
                        }
                    }
                    CommissionDetailsVO commissionDetailsVO1 = getAgentCommissionFromMerchantSetupFee(agentCommissionVO, terminalVO1, settlementDateVO);
                    commissionDetailsVO.setCount(commissionDetailsVO1.getCount() + count);
                    commissionDetailsVO.setAmount(commissionDetailsVO1.getAmount() + amount);
                    double vDblTotal = (commissionDetailsVO1.getCount() + count) * agentCommissionVO.getCommissionValue();
                    commissionDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
                }
                else if (Charge_category.Others.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Wire.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeMasterVO.getSubKeyword()))
                {
                    if (commissionDetailsVOHashMap.get(terminalId) != null)
                    {
                        commissionDetailsVOs = commissionDetailsVOHashMap.get(terminalId);
                        CommissionDetailsVO commissionDetailsVO1 = commissionDetailsVOs.get(chargeMasterVO.getChargeName());
                        if (commissionDetailsVO1 != null)
                        {
                            count = commissionDetailsVO1.getCount();
                            amount = commissionDetailsVO1.getAmount();
                        }
                    }
                    long vCntCounter = payoutDAO.getMerchantPaidWireCountForCommissionCalculation(terminalVO1, settlementDateVO);//get the total count of paid wires from merchant_wiremanager between settlement date
                    double vDblAmount = 0.00;
                    double vDblTotal = (vCntCounter + count) * agentCommissionVO.getCommissionValue();

                    commissionDetailsVO.setCount(vCntCounter + count);
                    commissionDetailsVO.setAmount(vDblAmount + amount);
                    commissionDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
                }
                else if (chargeMasterVO.getCategory().equals(Charge_category.Success.toString()) && chargeMasterVO.getKeyword().equals(Charge_keyword.NetFinalAmount.toString()) && chargeMasterVO.getSubKeyword().equals(Charge_subKeyword.Amount.toString()))
                {
                    if (commissionDetailsVOHashMap.get(terminalId) != null)
                    {
                        commissionDetailsVOs = commissionDetailsVOHashMap.get(terminalId);
                        CommissionDetailsVO commissionDetailsVO1 = commissionDetailsVOs.get(chargeMasterVO.getChargeName());
                        if (commissionDetailsVO1 != null)
                        {
                            count = commissionDetailsVO1.getCount();
                            amount = commissionDetailsVO1.getAmount();
                        }
                    }
                    double totalFundedToBank = payoutDAO.getMerchantTotalAmountFunded(terminalVO1, settlementDateVO);//get the total count of paid wires from merchant_wiremanager between settlement date
                    logger.error("totalFundedToBank---" + totalFundedToBank + "---" + amount);
                    double vDblTotal = (totalFundedToBank + amount) * Double.valueOf(agentCommissionVO.getCommissionValue()) / 100;

                    commissionDetailsVO.setCount(0);
                    commissionDetailsVO.setAmount(totalFundedToBank + amount);
                    commissionDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
                }
                else
                {
                    commissionDetailsVO = calculateAgentCommissionValue(agentCommissionVO, transactionSummaryVO, commissionDetailsVOHashMap, terminalVO1, agentCommissionReportVO);
                }
                commissionDetailsVOs.put(commissionDetailsVO.getChargeName(), commissionDetailsVO);
                commissionDetailsVOHashMap.put(terminalId, commissionDetailsVOs);

            }
            ChargeDetailsVO chargeDetailsVO = null;

            List<ChargeVO> chargeVOList = chargeManager.getTobeDebitedAgentCharges(terminalVO1);
            for (ChargeVO chargeVO : chargeVOList)
            {
                chargeDetailsVOList = chargeDetailsVOHashMap.get(terminalId);
                if (chargeDetailsVOList == null)
                {
                    chargeDetailsVOList = new HashMap<>();
                }
                if (Charge_keyword.TotalReserveGenerated.toString().equals(chargeVO.getKeyword()) || Charge_keyword.TotalReserveRefunded.toString().equals(chargeVO.getKeyword()) || Charge_keyword.CalculatedReserveRefund.toString().equals(chargeVO.getKeyword()) || Charge_keyword.Wire.toString().equals(chargeVO.getKeyword()) || Charge_keyword.GrossBalanceAmount.toString().equals(chargeVO.getKeyword()) || Charge_keyword.Setup.toString().equals(chargeVO.getKeyword()) || Charge_keyword.VerifyOrder.toString().equals(chargeVO.getKeyword()) || Charge_keyword.RefundAlert.toString().equals(chargeVO.getKeyword()) || Charge_keyword.RetrivalRequest.toString().equals(chargeVO.getKeyword()) || (Charge_keyword.Reversed.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword())))
                {
                    logger.debug("Skipping " + chargeVO.getChargename() + " Form Agent Summary Charge Payout");
                    continue;
                }
                else
                {
                    chargeDetailsVO = calculateAgentChargesValue(chargeVO, transactionSummaryVO, chargeDetailsVOHashMap, terminalId, agentCommissionReportVO);
                    totalChargesAmount = totalChargesAmount + chargeDetailsVO.getTotal();
                    chargeDetailsVOList.put(chargeDetailsVO.getChargeName(), chargeDetailsVO);
                    chargeDetailsVOHashMap.put(terminalId, chargeDetailsVOList);
                }
            }
            //agentTotalFundedAmount=agentTotalFundedAmount+totalCommissionAmount+previousBalance-totalChargesAmount;
        }
        logger.error("commissionDetailsVOHashMap:::::" + commissionDetailsVOHashMap);
        logger.error("chargeDetailsVOHashMap:::::" + chargeDetailsVOHashMap);
        logger.error("agentTerminalHash:::::" + agentTerminalHash);
        stringList = generateAgentCommissionReport(startDate, endDate, currencyConversion, agentTerminalHash, commissionDetailsVOHashMap, chargeDetailsVOHashMap, partnerDetailsVO, agentDetailsVO, memberDetails, terminalDetails, currencySet);
        return stringList;
    }

    public TransactionSummaryVO getTotalSuccessCountAmountByTerminalNew(TerminalVO terminalVO, SettlementDateVO settlementDateVO, String tableName) throws PZDBViolationException
    {
        long authfailedCount = 0;
        long settledCount = 0;
        long reversedCount = 0;
        long chargebackCount = 0;
        long totalSuccessCount = 0;
        long payoutCount = 0;

        double authfailedAmount = 0.00;
        double settledAmount = 0.0;
        double reversedAmount = 0.0;
        double chargebackAmount = 0.0;
        double totalSuccessAmount = 0.0;
        double payoutAmount = 0.00;

        TransactionSummaryVO grossSummaryVO = new TransactionSummaryVO();
        TransactionSummaryVO summaryVO;

        summaryVO = payoutDAO.getProcessingCountAmountDetails(settlementDateVO.getSettlementStartDate(), settlementDateVO.getSettlementEndDate(), terminalVO, tableName);

        totalSuccessAmount = summaryVO.getTotalProcessingAmount();
        totalSuccessCount = summaryVO.getTotalProcessingCount();

        summaryVO = payoutDAO.getAuthFailCountAmountByDtstamp(settlementDateVO.getDeclinedStartDate(), settlementDateVO.getDeclinedEndDate(), terminalVO, tableName);
        authfailedCount = summaryVO.getCountOfAuthfailed();
        authfailedAmount = summaryVO.getAuthfailedAmount();

        summaryVO = payoutDAO.getChargebackCountAmountByTimestamp(settlementDateVO.getChargebackStartDate(), settlementDateVO.getChargebackEndDate(), terminalVO, tableName);
        chargebackCount = summaryVO.getCountOfChargeback();
        chargebackAmount = summaryVO.getChargebackAmount();

        summaryVO = payoutDAO.getReversalCountAmountByTimestamp(settlementDateVO.getReversedStartDate(), settlementDateVO.getReversedEndDate(), terminalVO, tableName);
        reversedCount = summaryVO.getCountOfReversed();
        reversedAmount = summaryVO.getReversedAmount();

        summaryVO = payoutDAO.getPayoutCountAmountByTerminal(settlementDateVO.getReversedStartDate(), settlementDateVO.getReversedEndDate(), terminalVO, tableName);
        payoutCount = summaryVO.getCountOfPayout();
        payoutAmount = summaryVO.getPayoutAmount();

        grossSummaryVO.setCountOfPayout(payoutCount);
        grossSummaryVO.setPayoutAmount(payoutAmount);

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

    public List<String> generateAgentCommissionReport(String startDate, String endDate, HashMap<String, String> currencyConversion, TreeMap<String, TerminalVO> agentTerminalHash, HashMap<String, HashMap<String, CommissionDetailsVO>> commissionDetailsVOHashMap, HashMap<String, HashMap<String, ChargeDetailsVO>> chargeDetailsVOHashMap, PartnerDetailsVO partnerDetailsVO, AgentDetailsVO agentDetailsVO, Set memberDetails, Set terminalDetails, HashMap<String, String> currencySet)
    {
        List<String> stringList = new ArrayList<>();
        Document document = new Document(PageSize.A3, 50, 50, 50, 50);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentSystemDate = dateFormater.format(new Date());
        // String reportFileName=payoutReportUtils.getAgentReportFileName(agentDetailsVO.getAgentId(), terminalVO.getMemberId(),terminalVO.getTerminalId(), settlementDateVO.getSettlementEndDate(), settlementDateVO.getSettlementEndDate());
        try
        {
            String reportFileName = "ConsolidatedAgentReport_" + agentDetailsVO.getAgentId() + "_" + dateFormater.format(simpleDateFormat.parse(startDate)) + "_" + dateFormater.format(simpleDateFormat.parse(endDate)) + "_" + currentSystemDate;

            reportFileName = reportFileName + ".pdf";
            File filePath = new File(AGENT_PAYOUT_REPORT_FILE_PATH + reportFileName);

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            String reportingDate = targetFormat.format(new Date());
            if (!functions.isValueNull(partnerDetailsVO.getLogoName()))
            {
                partnerDetailsVO.setLogoName("logo.jpg");
            }

            com.lowagie.text.Image partnerImageInstance = com.lowagie.text.Image.getInstance(PARTNER_LOGO_PATH + partnerDetailsVO.getLogoName());
            partnerImageInstance.scaleAbsolute(150f, 150f);
            partnerImageInstance.scaleAbsoluteHeight(40f);

            com.lowagie.text.Font f1 = FontFactory.getFont(FontFactory.TIMES_ROMAN, 20);
            f1.setColor(Color.BLACK);

            com.lowagie.text.Font f2 = FontFactory.getFont(FontFactory.TIMES_BOLD, 15);
            f2.setColor(Color.WHITE);

            com.lowagie.text.Font f3 = FontFactory.getFont(FontFactory.TIMES_BOLD, 15);
            f3.setColor(Color.BLACK);

            Table table = new Table(6);
            table.setWidth(100);
            table.setBorderColor(new Color(0, 0, 0));
            table.setPadding(1);

            Cell partnerNameCaptionCell = new Cell("Processor:" + partnerDetailsVO.getCompanyName());
            partnerNameCaptionCell.setColspan(2);
            partnerNameCaptionCell.setBackgroundColor(Color.white.brighter());

            Cell reportingDateCaptionCe11 = new Cell("Reporting Date:" + reportingDate);
            reportingDateCaptionCe11.setColspan(2);
            reportingDateCaptionCe11.setBackgroundColor(Color.white.brighter());

            Cell partnerLogoCell = new Cell(partnerImageInstance);
            partnerLogoCell.setColspan(2);
            partnerLogoCell.setHorizontalAlignment(Element.ALIGN_CENTER);

            table.addCell(partnerNameCaptionCell);
            table.addCell(reportingDateCaptionCe11);
            table.addCell(partnerLogoCell);

            Cell agentDetails = new Cell("Agent Details");
            agentDetails.setColspan(6);
            agentDetails.setHorizontalAlignment(Element.ALIGN_CENTER);
            agentDetails.setVerticalAlignment(Element.ALIGN_CENTER);
            agentDetails.setBackgroundColor(new Color(0, 127, 255));

            table.addCell(agentDetails);

            Cell agentNameLabel = new Cell("Agent Id:");
            Cell agentNameValue = new Cell(agentDetailsVO.getAgentId());

            agentNameLabel.setColspan(3);
            agentNameValue.setColspan(3);

            table.addCell(agentNameLabel);
            table.addCell(agentNameValue);

            Cell memberIdLabel = new Cell("Member ID:");
            Cell memberIdValue = new Cell(memberDetails.toString());

            memberIdLabel.setColspan(3);
            memberIdValue.setColspan(3);

            table.addCell(memberIdLabel);
            table.addCell(memberIdValue);

            Cell terminalIdLabel = new Cell("Terminal Id:");
            Cell terminalValue = new Cell(terminalDetails.toString());

            terminalIdLabel.setColspan(3);
            terminalValue.setColspan(3);

            table.addCell(terminalIdLabel);
            table.addCell(terminalValue);

            Cell partnerCompanyNameLabel = new Cell("Agent Name:");
            Cell partnerCompanyValue = new Cell(agentDetailsVO.getAgentName());

            partnerCompanyNameLabel.setColspan(3);
            partnerCompanyValue.setColspan(3);

            table.addCell(partnerCompanyNameLabel);
            table.addCell(partnerCompanyValue);

            Cell contactPersonNameLabel = new Cell("Contact Person Name:");
            Cell contactPersonNameValue = new Cell(agentDetailsVO.getContactPerson());

            contactPersonNameLabel.setColspan(3);
            contactPersonNameValue.setColspan(3);

            table.addCell(contactPersonNameLabel);
            table.addCell(contactPersonNameValue);

            Cell settlementFromDateLabel = new Cell("Settlement From:");
            Cell settlementFromDateValue = new Cell(targetFormat.format(simpleDateFormat.parse(startDate)));

            settlementFromDateLabel.setColspan(3);
            settlementFromDateValue.setColspan(3);

            table.addCell(settlementFromDateLabel);
            table.addCell(settlementFromDateValue);

            Cell settlementToDateLabel = new Cell("Settlement To:");
            Cell settlementToDateValue = new Cell(targetFormat.format(simpleDateFormat.parse(endDate)));

            settlementToDateLabel.setColspan(3);
            settlementToDateValue.setColspan(3);

            table.addCell(settlementToDateLabel);
            table.addCell(settlementToDateValue);

            Cell agentCommissionDetailsLabel = new Cell("Agent Commission Details");
            agentCommissionDetailsLabel.setColspan(6);
            agentCommissionDetailsLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            agentCommissionDetailsLabel.setBackgroundColor(new Color(0, 127, 255));
            table.addCell(agentCommissionDetailsLabel);

            Cell commissionNameLabel = new Cell(new Paragraph(20, "Commission Name", f3));
            Cell commissionRateLabel = new Cell(new Paragraph(20, "Commission Rate", f3));
            Cell commissionCounterLabel = new Cell(new Paragraph(20, "Counter", f3));
            Cell commissionAmountLabel = new Cell(new Paragraph(20, "Amount", f3));
            Cell commissionTotalLabel = new Cell(new Paragraph(20, "Total", f3));

            commissionNameLabel.setColspan(2);

            /*commissionNameLabel.setBackgroundColor(Color.gray.brighter());
            commissionRateLabel.setBackgroundColor(Color.gray.brighter());
            commissionCounterLabel.setBackgroundColor(Color.gray.brighter());
            commissionAmountLabel.setBackgroundColor(Color.gray.brighter());
            commissionTotalLabel.setBackgroundColor(Color.gray.brighter());
*/
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

            double totalCommissionAmount = 0.00;
            double agentTotalFundedAmount = 0.00;
            double previousBalance = 0.00;

            Set<String> terminalList = agentTerminalHash.keySet();
            HashMap<String, TerminalVO> terminalHashMap = new HashMap<>();
            for (String agentTerminalWireId : terminalList)
            {
                TerminalVO terminalVO = agentTerminalHash.get(agentTerminalWireId);
                if (terminalHashMap.keySet().contains(terminalVO.getTerminalId()))
                {
                    TerminalVO terminalVO1 = terminalHashMap.get(terminalVO.getTerminalId());
                    terminalVO.setStartDate(terminalVO1.getStartDate());
                }
                else
                {
                    terminalVO.setStartDate(terminalVO.getStartDate());
                }
                terminalHashMap.put(terminalVO.getTerminalId(), terminalVO);
            }
            terminalList = terminalHashMap.keySet();
            for (String terminalIdKey : terminalList)
            {
                TerminalVO terminalVO = terminalHashMap.get(terminalIdKey);
                String terminalid = terminalVO.getTerminalId();
                String paymode = GatewayAccountService.getPaymentMode(terminalVO.getPaymodeId());
                String cardType = GatewayAccountService.getCardType(terminalVO.getCardTypeId());
                double totalSubCommissionAmount = 0.00;
                Cell terminalHeader = new Cell(terminalid + "-" + paymode + "-" + cardType + " [" + currencySet.get(terminalid) + "] [" + terminalVO.getMemberId() + "]" +
                        "                                                                                               " + terminalVO.getStartDate() + "-" + terminalVO.getEndDate());
                terminalHeader.setColspan(6);
                terminalHeader.setHorizontalAlignment(Element.ALIGN_LEFT);
                terminalHeader.setBackgroundColor(Color.gray);
                table.addCell(terminalHeader);

                Cell commissionNameValue, commissionRateValue, commissionCounterValue, commissionAmountValue, commissionTotalValue;
                HashMap<String, CommissionDetailsVO> commissionDetailsVOs = commissionDetailsVOHashMap.get(terminalid);
                Set commissionDetailsSet = commissionDetailsVOs.keySet();
                Iterator commissionIterate = commissionDetailsSet.iterator();
                while (commissionIterate.hasNext())
                {
                    String chargeName = (String) commissionIterate.next();
                    CommissionDetailsVO commissionDetailsVO = commissionDetailsVOs.get(chargeName);
                    String vCntCounter = (new Long(commissionDetailsVO.getCount())).toString();
                    commissionNameValue = new Cell(commissionDetailsVO.getChargeName());
                    commissionTotalValue = new Cell(Functions.round(commissionDetailsVO.getTotal(), 2));
                    totalSubCommissionAmount = totalSubCommissionAmount + commissionDetailsVO.getTotal();

                    if (Charge_unit.Percentage.toString().equals(commissionDetailsVO.getValueType()))
                    {
                        commissionRateValue = new Cell(Functions.round(Double.valueOf(commissionDetailsVO.getChargeValue()), 2) + "%");
                    }
                    else
                    {
                        commissionRateValue = new Cell(Functions.round(Double.valueOf(commissionDetailsVO.getChargeValue()), 2));
                    }

                    commissionCounterValue = new Cell(vCntCounter);
                    commissionAmountValue = new Cell(Functions.round(commissionDetailsVO.getAmount(), 2));
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
                Cell totalLabel = new Cell(new Paragraph("Sub Total in EUR", f3));
                String currency = currencySet.get(terminalid);
                double exchangeRate = Double.parseDouble(currencyConversion.get(currency + "_" + "EUR"));
                Cell commissionValue = new Cell(Functions.round(totalSubCommissionAmount, 2));
                if (currency.equalsIgnoreCase("EUR"))
                {
                    totalSubCommissionAmount = totalSubCommissionAmount * 1.00;
                }
                else
                {
                    totalSubCommissionAmount = totalSubCommissionAmount * exchangeRate;
                }
                totalCommissionAmount = totalCommissionAmount + totalSubCommissionAmount;
                Cell rateValue = new Cell(Functions.round(exchangeRate, 4));
                Cell totalValue = new Cell(Functions.round(totalSubCommissionAmount, 2));
                totalLabel.setColspan(3);
                totalLabel.setHeader(true);

                totalLabel.setHorizontalAlignment(Element.ALIGN_LEFT);
                commissionValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                rateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                totalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(totalLabel);
                table.addCell(commissionValue);
                table.addCell(rateValue);
                table.addCell(totalValue);
            }

            Cell totalLabel = new Cell(new Paragraph("Total Commission", f3));
            Cell totalValue = new Cell(Functions.round(totalCommissionAmount, 2));

            totalLabel.setColspan(5);
            totalLabel.setHeader(true);

            totalLabel.setBackgroundColor(Color.gray.brighter());
            totalValue.setBackgroundColor(Color.gray.brighter());
            totalLabel.setHorizontalAlignment(Element.ALIGN_LEFT);
            totalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(totalLabel);
            table.addCell(totalValue);
            double totalChargesAmount = 0.00;

            if (chargeDetailsVOHashMap != null && chargeDetailsVOHashMap.size() > 0)
            {
                Cell agentChargesDetailsLabel = new Cell("Agent Charges Details");
                agentChargesDetailsLabel.setColspan(6);
                agentChargesDetailsLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
                agentChargesDetailsLabel.setBackgroundColor(new Color(0, 127, 255));
                table.addCell(agentChargesDetailsLabel);

                Cell chargesNameLabel = new Cell(new Paragraph(20, "Charge Name", f3));
                Cell chargesRateLabel = new Cell(new Paragraph(20, "Charge Rate", f3));
                Cell chargesCounterLabel = new Cell(new Paragraph(20, "Counter", f3));
                Cell chargesAmountLabel = new Cell(new Paragraph(20, "Amount", f3));
                Cell chargesTotalLabel = new Cell(new Paragraph(20, "Total", f3));

                chargesNameLabel.setColspan(2);

            /*chargesNameLabel.setBackgroundColor(Color.gray.brighter());
            chargesRateLabel.setBackgroundColor(Color.gray.brighter());
            chargesCounterLabel.setBackgroundColor(Color.gray.brighter());
            chargesAmountLabel.setBackgroundColor(Color.gray.brighter());
            chargesTotalLabel.setBackgroundColor(Color.gray.brighter());*/

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

                Iterator terminalListForCharge = terminalDetails.iterator();
                //double totalChargesAmount = 0.00;
                for (String terminalIdKey : terminalList)
                {
                    TerminalVO terminalVO = terminalHashMap.get(terminalIdKey);
                    String terminalid = terminalVO.getTerminalId();
                    String paymode = GatewayAccountService.getPaymentMode(terminalVO.getPaymodeId());
                    String cardType = GatewayAccountService.getCardType(terminalVO.getCardTypeId());
                    double totalSubChargesAmount = 0.00;
                    Cell terminalHeader = new Cell(terminalid + "-" + paymode + "-" + cardType + " [" + currencySet.get(terminalid) + "] [" + terminalVO.getMemberId() + "]" +
                            "                                                                                               " + terminalVO.getStartDate() + "-" + terminalVO.getEndDate());
                    terminalHeader.setColspan(6);
                    terminalHeader.setBackgroundColor(Color.gray);
                    terminalHeader.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(terminalHeader);

                    Cell chargesNameValue, chargesRateValue, chargesCounterValue, chargesAmountValue, chargesTotalValue;
                    HashMap<String, ChargeDetailsVO> chargeDetailsVOList = chargeDetailsVOHashMap.get(terminalid);
                    Set chargeDetailsVO = chargeDetailsVOList.keySet();
                    Iterator iterator = chargeDetailsVO.iterator();
                    while (iterator.hasNext())
                    {
                        String chargeName = (String) iterator.next();
                        ChargeDetailsVO detailsVO = chargeDetailsVOList.get(chargeName);
                        String vCntCounter = (new Long(detailsVO.getCount())).toString();
                        chargesNameValue = new Cell(detailsVO.getChargeName());
                        chargesTotalValue = new Cell(Functions.round(detailsVO.getTotal(), 2));
                        totalSubChargesAmount = totalSubChargesAmount + detailsVO.getTotal();

                        if (Charge_unit.Percentage.toString().equals(detailsVO.getValueType()))
                        {
                            chargesRateValue = new Cell(Functions.round(Double.valueOf(detailsVO.getChargeValue()), 2) + "%");
                        }
                        else
                        {
                            chargesRateValue = new Cell(Functions.round(Double.valueOf(detailsVO.getChargeValue()), 2));
                        }

                        chargesCounterValue = new Cell(vCntCounter);
                        chargesAmountValue = new Cell(Functions.round(detailsVO.getAmount(), 2));
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
                    Cell totalLabel1 = new Cell(new Paragraph("Sub Total in EUR", f3));
                    String currency = currencySet.get(terminalid);
                    double exchangeRate = Double.parseDouble(currencyConversion.get(currency + "_" + "EUR"));
                    Cell chargeValue = new Cell(Functions.round(totalSubChargesAmount, 2));
                    if (currency.equalsIgnoreCase("EUR"))
                    {
                        totalSubChargesAmount = totalSubChargesAmount * 1.00;
                    }
                    else
                    {
                        totalSubChargesAmount = totalSubChargesAmount * exchangeRate;
                    }
                    totalChargesAmount = totalChargesAmount + totalSubChargesAmount;
                    Cell rateValue1 = new Cell(Functions.round(exchangeRate, 4));
                    Cell totalValue1 = new Cell(Functions.round(totalSubChargesAmount, 2));
                    totalLabel1.setColspan(3);
                    totalLabel1.setHeader(true);

                    totalLabel1.setHorizontalAlignment(Element.ALIGN_LEFT);
                    rateValue1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    chargeValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    totalValue1.setHorizontalAlignment(Element.ALIGN_RIGHT);

                    table.addCell(totalLabel1);
                    table.addCell(chargeValue);
                    table.addCell(rateValue1);
                    table.addCell(totalValue1);
                }

                Cell totalLabel1 = new Cell(new Paragraph("Total Charges", f3));
                Cell totalValue1 = new Cell(Functions.round(totalChargesAmount, 2));

                totalLabel1.setColspan(5);
                totalLabel1.setHeader(true);

                totalLabel1.setBackgroundColor(Color.gray.brighter());
                totalValue1.setBackgroundColor(Color.gray.brighter());
                totalLabel1.setHorizontalAlignment(Element.ALIGN_LEFT);
                totalValue1.setHorizontalAlignment(Element.ALIGN_RIGHT);

                table.addCell(totalLabel1);
                table.addCell(totalValue1);
            }

            Cell agentSummaryDetailsLabel = new Cell("Summary");
            agentSummaryDetailsLabel.setColspan(6);
            agentSummaryDetailsLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            agentSummaryDetailsLabel.setBackgroundColor(new Color(0, 127, 255));
            table.addCell(agentSummaryDetailsLabel);

            Cell agentCommissionAmountLabel = new Cell("Agent Total Commission Amount In EUR");
            Cell agentCommissionAmountValue = new Cell(Functions.round(totalCommissionAmount, 2));

            agentCommissionAmountLabel.setColspan(5);
            agentCommissionAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(agentCommissionAmountLabel);
            table.addCell(agentCommissionAmountValue);

            Cell agentChargesAmountLabel = new Cell("Agent Total Charges Amount In EUR");
            Cell agentChargesAmountValue = new Cell(isValidAmount(Functions.round(totalChargesAmount, 2)));

            agentChargesAmountLabel.setColspan(5);
            agentChargesAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(agentChargesAmountLabel);
            table.addCell(agentChargesAmountValue);

            Cell previousBalanceAmountLabel = new Cell("Previous Balance Amount");
            Cell previousBalanceAmountValue = new Cell(Functions.round(0.00, 2));
            //Cell previousBalanceAmountValue=new Cell(Functions.round(previousBalanceAmount,2));

            previousBalanceAmountLabel.setColspan(5);
            previousBalanceAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(previousBalanceAmountLabel);
            table.addCell(previousBalanceAmountValue);

            Cell agentTotalAmountFundedLabel = new Cell("Total Amount Funded");
            //Cell agenttotalAmountFundedValue=new Cell(Functions.round(0.00,2));
            agentTotalFundedAmount = agentTotalFundedAmount + totalCommissionAmount + previousBalance - totalChargesAmount;
            Cell agenttotalAmountFundedValue = new Cell(Functions.round(agentTotalFundedAmount, 2));

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
            table.addCell(poweredByLogoValueCell1);
            */
            document.add(table);
            document.close();
            Set<String> set = agentTerminalHash.keySet();
            String cyclememberlist = "";

            if (functions.isValueNull(reportFileName))
            {
                for (String agentTerminalWireId : agentTerminalHash.keySet())
                {
                    TerminalVO terminalVO = agentTerminalHash.get(agentTerminalWireId);
                    AgentWireVO agentWireVO = new AgentWireVO();
                    agentWireVO.setSettlementStartDate(targetFormat.format(simpleDateFormat.parse(terminalVO.getStartDate())));
                    agentWireVO.setSettlementEndDate(targetFormat.format(simpleDateFormat.parse(terminalVO.getEndDate())));
                    agentWireVO.setAgentType("merchantaccount");
                    agentWireVO.setAgentChargeAmount(totalCommissionAmount);
                    agentWireVO.setAgentUnpaidAmount(0.00);
                    agentWireVO.setAgentTotalFundedAmount(agentTotalFundedAmount);
                    agentWireVO.setCurrency("EUR");
                    agentWireVO.setStatus("unpaid");
                    agentWireVO.setSettlementReportFileName(reportFileName);
                    agentWireVO.setMarkedForDeletion("N");
                    agentWireVO.setAgentId(agentDetailsVO.getAgentId());
                    agentWireVO.setMemberId(terminalVO.getMemberId());
                    agentWireVO.setAccountId(terminalVO.getAccountId());
                    agentWireVO.setTerminalId(terminalVO.getTerminalId());
                    agentWireVO.setPayModeId(terminalVO.getPaymodeId());
                    agentWireVO.setCardTypeId(terminalVO.getCardTypeId());
                    agentWireVO.setDeclinedCoverDateUpTo(targetFormat.format(simpleDateFormat.parse(endDate)));
                    agentWireVO.setReversedCoverDateUpTo(targetFormat.format(simpleDateFormat.parse(endDate)));
                    agentWireVO.setChargebackCoverDateUpTo(targetFormat.format(simpleDateFormat.parse(endDate)));
                    agentWireVO.setSettlementCycleNo(terminalVO.getWireId());
                    String wireCreationStatus = payoutManager.createAgentWire(agentWireVO);
                    String description = "";
                    if ("success".equals(wireCreationStatus))
                    {
                        description = "Agent Commission Report Creation Successfull";
                    }
                    else
                    {
                        description = "Agent Commission Report Creation Failed";
                    }
                /*if ("success".equals(status))
                {
                    successList.add(terminalVO1);
                    statusMsg = "Agent Commission Report Created Successfully";
                    logger.error("pandingList----" + pendingList.size());
                    logger.error("successList----" + successList.size());
                    if (pendingList.size() == successList.size())
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
                }*/

                    cyclememberlist = terminalVO.getWireId() + ":" + terminalVO.getMemberId() + ":" + terminalVO.getAccountId() + ":" + terminalVO.getAgentId() + ":" + terminalVO.getTerminalId() + ":" + wireCreationStatus + ":" + description + "";
                    stringList.add(cyclememberlist);
                }
            }
        }
        catch (Exception ex)
        {
            logger.error("Exception::::", ex);
        }
        return stringList;
    }

    public CommissionDetailsVO getAgentCommissionFromMerchantSetupFee(AgentCommissionVO agentCommissionVO, TerminalVO terminalVO, SettlementDateVO settlementDateVO) throws ParseException//sandip
    {
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String setupFeeDateUpto = payoutDAO.getMerchantWireCoveredDate(terminalVO.getMemberId(), terminalVO.getAccountId(), terminalVO.getTerminalId(), settlementDateVO.getSettlementcycleNumber());//settlementDateVO.getSettlementEndDate();
        long setupCounter = 0;
        boolean oneTimeChargeFlag = false;

        ChargeMasterVO chargeMasterVO = agentCommissionVO.getChargeMasterVO();
        CommissionDetailsVO chargeDetailsVO = new CommissionDetailsVO();

        chargeDetailsVO.setChargeName(chargeMasterVO.getChargeName());
        chargeDetailsVO.setValueType(chargeMasterVO.getValueType());
        chargeDetailsVO.setChargeValue(String.valueOf(agentCommissionVO.getCommissionValue()));

        String memberFirstTransactionDate = payoutDAO.getMemberFirstTransactionDateOnTerminal(terminalVO);
        String lastSetFeeDate = payoutDAO.getMerchantWireLastCoveredDate(terminalVO.getMemberId(), terminalVO.getAccountId(), terminalVO.getTerminalId(), settlementDateVO.getSettlementcycleNumber());
        if (lastSetFeeDate == null)
        {
            oneTimeChargeFlag = true;
        }
        if (chargeMasterVO.getFrequency().equals(Charge_frequency.One_Time.toString()))
        {
            if (oneTimeChargeFlag)
            {
                setupCounter = 1;
            }
        }
        else if (chargeMasterVO.getFrequency().equals(Charge_frequency.Weekly.toString()))
        {
            long totalWeekCount = 0;
            long ChargedCount = 0;
            long dy = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)), targetFormat.format(targetFormat.parse(setupFeeDateUpto)));
            totalWeekCount = dy / 7 + 1;
            if (lastSetFeeDate != null)
            {
                long d = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)), targetFormat.format(targetFormat.parse(lastSetFeeDate)));
                ChargedCount = d / 7 + 1;
            }
            setupCounter = totalWeekCount - ChargedCount;
        }
        else if (chargeMasterVO.getFrequency().equals(Charge_frequency.Monthly.toString()))
        {
            long totalMonthCount = 0;
            long ChargedCount = 0;
            logger.error("memberFirstTransactionDate---" + memberFirstTransactionDate + "---setupFeeDateUpto---" + setupFeeDateUpto + "---lastSetFeeDate---" + lastSetFeeDate);
            long dy = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)), targetFormat.format(targetFormat.parse(setupFeeDateUpto)));
            totalMonthCount = dy / 30 + 1;
            if (lastSetFeeDate != null)
            {
                long d = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)), targetFormat.format(targetFormat.parse(lastSetFeeDate)));
                ChargedCount = d / 30 + 1;
            }
            setupCounter = totalMonthCount - ChargedCount;
        }
        else if (chargeMasterVO.getFrequency().equals(Charge_frequency.Yearly.toString()))
        {
            long totalYearCount = 0;
            long ChargedCount = 0;
            logger.error("Yearly---" + memberFirstTransactionDate + "---setupFeeDateUpto---" + setupFeeDateUpto + "---lastSetFeeDate---" + lastSetFeeDate);
            if (setupFeeDateUpto != null)
            {
                long dy = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)), targetFormat.format(targetFormat.parse(setupFeeDateUpto)));
                totalYearCount = dy / 365 + 1;
            }
            //totalYearCount=dy/365+1;
            if (lastSetFeeDate != null)
            {
                long d = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)), targetFormat.format(targetFormat.parse(lastSetFeeDate)));
                ChargedCount = d / 365 + 1;
            }
            logger.error("totalYearCount---" + totalYearCount + "--ChargedCount---" + ChargedCount);
            setupCounter = totalYearCount - ChargedCount;
        }
        double total = setupCounter * Double.valueOf(agentCommissionVO.getCommissionValue());

        chargeDetailsVO.setCount(setupCounter);
        chargeDetailsVO.setAmount(0.00);
        chargeDetailsVO.setTotal(total);
        return chargeDetailsVO;
    }

    public CommissionDetailsVO calculateAgentCommissionValue(AgentCommissionVO agentCommissionVO, TransactionSummaryVO summaryVO, HashMap<String, HashMap<String, CommissionDetailsVO>> commissionDetailsVOHashMap, TerminalVO terminalVO, AgentCommissionReportVO agentCommissionReportVO)
    {
        long settledTransCount = 0;
        long reversedTransCount = 0;
        long chargebackTransCount = 0;
        long authfailedTransCount = 0;
        long totalSaleCount = 0;
        long totalPayoutCount = 0;

        double totalAuthfailedTransAmount = 0.00;
        double totalSettledTransAmount = 0.00;
        double totalReversedTransAmount = 0.00;
        double totalChargebackTransAmount = 0.00;
        double totalSaleAmount = 0.00;
        double totalPayoutAmount = 0.00;

        totalSaleCount = summaryVO.getTotalProcessingCount();
        totalSaleAmount = summaryVO.getTotalProcessingAmount();

        authfailedTransCount = summaryVO.getCountOfAuthfailed();
        totalAuthfailedTransAmount = summaryVO.getAuthfailedAmount();

        settledTransCount = summaryVO.getCountOfSettled();
        totalSettledTransAmount = summaryVO.getSettledAmount();

        reversedTransCount = summaryVO.getCountOfReversed();
        totalReversedTransAmount = summaryVO.getReversedAmount();

        chargebackTransCount = summaryVO.getCountOfChargeback();
        totalChargebackTransAmount = summaryVO.getChargebackAmount();

        totalPayoutCount = summaryVO.getCountOfPayout();
        totalPayoutAmount = summaryVO.getPayoutAmount();

        long vCntCounter = 0;
        double vDblAmount = 0.00;
        double vDblTotal = 0.00;

        ChargeMasterVO chargeMasterVO = agentCommissionVO.getChargeMasterVO();

        CommissionDetailsVO chargeDetailsVO = new CommissionDetailsVO();

        chargeDetailsVO.setChargeName(chargeMasterVO.getChargeName());
        chargeDetailsVO.setChargeValue(String.valueOf(agentCommissionVO.getCommissionValue()));
        chargeDetailsVO.setValueType(chargeMasterVO.getValueType());
        long count = 0;
        double amount = 0.00;
        TransactionSummaryVO transactionSummaryVO = null;
        SettlementDateVO settlementDateVO = agentCommissionReportVO.getSettlementDateVO();
        String country = payoutDAO.getGatewayCountryFromAccountId(terminalVO.getAccountId());

        try
        {
            String terminalId = terminalVO.getTerminalId();
            if (chargeMasterVO.getCategory().equals(Charge_category.Success.toString()) && chargeMasterVO.getKeyword().equals(Charge_keyword.FraudAlert.toString()) && chargeMasterVO.getSubKeyword().equals(Charge_subKeyword.Count.toString()))
            {
                if (commissionDetailsVOHashMap.get(terminalId) != null)
                {
                    HashMap<String, CommissionDetailsVO> commissionDetailsVOs = commissionDetailsVOHashMap.get(terminalId);
                    CommissionDetailsVO commissionDetailsVO1 = commissionDetailsVOs.get(chargeMasterVO.getChargeName());
                    if (commissionDetailsVO1 != null)
                    {
                        count = commissionDetailsVO1.getCount();
                        amount = commissionDetailsVO1.getAmount();
                    }
                }
                transactionSummaryVO = payoutManager.getFraudDefenderCountAmountByTerminal(terminalVO, settlementDateVO);
                vCntCounter = transactionSummaryVO.getTotalProcessingCount() + count;
                vDblAmount = transactionSummaryVO.getTotalProcessingAmount() + amount;
                vDblTotal = vCntCounter * agentCommissionVO.getCommissionValue();

                chargeDetailsVO.setCount(vCntCounter);
                chargeDetailsVO.setAmount(vDblAmount);
                chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
            }
            else if (chargeMasterVO.getCategory().equals(Charge_category.Others.toString()) && chargeMasterVO.getKeyword().equals(Charge_keyword.CaseFiling.toString()) && chargeMasterVO.getSubKeyword().equals(Charge_subKeyword.Count.toString()))
            {
                if (commissionDetailsVOHashMap.get(terminalId) != null)
                {
                    HashMap<String, CommissionDetailsVO> commissionDetailsVOs = commissionDetailsVOHashMap.get(terminalId);
                    CommissionDetailsVO commissionDetailsVO1 = commissionDetailsVOs.get(chargeMasterVO.getChargeName());
                    if (commissionDetailsVO1 != null)
                    {
                        count = commissionDetailsVO1.getCount();
                        amount = commissionDetailsVO1.getAmount();
                    }
                }
                long caseFilingTransactionCount = payoutDAO.getCaseFilingTransactionCount(terminalVO, settlementDateVO);
                vCntCounter = caseFilingTransactionCount + count;
                vDblAmount = 0.00;
                vDblTotal = (vCntCounter * Double.valueOf(agentCommissionVO.getCommissionValue()));

                chargeDetailsVO.setCount(vCntCounter);
                chargeDetailsVO.setAmount(vDblAmount);
                chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
            }
            else if (Charge_category.Success.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeMasterVO.getSubKeyword()))
            {
                if (commissionDetailsVOHashMap.get(terminalId) != null)
                {
                    HashMap<String, CommissionDetailsVO> commissionDetailsVOs = commissionDetailsVOHashMap.get(terminalId);
                    CommissionDetailsVO commissionDetailsVO1 = commissionDetailsVOs.get(chargeMasterVO.getChargeName());
                    if (commissionDetailsVO1 != null)
                    {
                        count = commissionDetailsVO1.getCount();
                        amount = commissionDetailsVO1.getAmount();
                    }
                }
                vCntCounter = totalSaleCount + count;/*settledTransCount+reversedTransCount+chargebackTransCount;*/
                vDblAmount = totalSaleAmount + amount;/*totalSettledTransAmount+totalReversedTransAmount+totalChargebackTransAmount;*/
                vDblTotal = (vDblAmount * agentCommissionVO.getCommissionValue()) / 100;

                chargeDetailsVO.setCount(vCntCounter);
                chargeDetailsVO.setAmount(vDblAmount);
                chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
            }
            else if (chargeMasterVO.getCategory().equals(Charge_category.Success.toString()) && chargeMasterVO.getKeyword().equals(Charge_keyword.DomesticTotal.toString()) && chargeMasterVO.getSubKeyword().equals(Charge_subKeyword.Amount.toString()) && functions.isValueNull(country))
            {
                long totalSuccessCount = 0;
                double totalSuccessAmount = 0.0;

                if (functions.isValueNull(country))
                {
                    if (commissionDetailsVOHashMap.get(terminalId) != null)
                    {
                        HashMap<String, CommissionDetailsVO> commissionDetailsVOs = commissionDetailsVOHashMap.get(terminalId);
                        CommissionDetailsVO commissionDetailsVO1 = commissionDetailsVOs.get(chargeMasterVO.getChargeName());
                        if (commissionDetailsVO1 != null)
                        {
                            count = commissionDetailsVO1.getCount();
                            amount = commissionDetailsVO1.getAmount();
                        }
                    }
                    String data = String.valueOf(EU_Country.getEnum(country));
                    if (functions.isValueNull(data))
                    {
                        transactionSummaryVO = payoutManager.getDomesticTotalAmountByTerminalForEUCountry(terminalVO, settlementDateVO);
                        totalSuccessCount = transactionSummaryVO.getTotalProcessingCount();
                        totalSuccessAmount = transactionSummaryVO.getTotalProcessingAmount();
                    }
                    else
                    {
                        transactionSummaryVO = payoutManager.getDomesticTotalAmountByTerminalForNonEU(terminalVO, settlementDateVO, country);
                        totalSuccessCount = transactionSummaryVO.getTotalProcessingCount();
                        totalSuccessAmount = transactionSummaryVO.getTotalProcessingAmount();
                    }

                    vCntCounter = totalSuccessCount + count;
                    vDblAmount = totalSuccessAmount + amount;
                    vDblTotal = (vDblAmount * agentCommissionVO.getCommissionValue()) / 100;

                    chargeDetailsVO.setCount(vCntCounter);
                    chargeDetailsVO.setAmount(vDblAmount);
                    chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
                }
            }
            else if (chargeMasterVO.getCategory().equals(Charge_category.Success.toString()) && chargeMasterVO.getKeyword().equals(Charge_keyword.InternationalTotal.toString()) && chargeMasterVO.getSubKeyword().equals(Charge_subKeyword.Amount.toString()) && functions.isValueNull(country))
            {
                long totalSuccessCount = 0;
                double totalSuccessAmount = 0.0;
                if (functions.isValueNull(country))
                {
                    if (commissionDetailsVOHashMap.get(terminalId) != null)
                    {
                        HashMap<String, CommissionDetailsVO> commissionDetailsVOs = commissionDetailsVOHashMap.get(terminalId);
                        CommissionDetailsVO commissionDetailsVO1 = commissionDetailsVOs.get(chargeMasterVO.getChargeName());
                        if (commissionDetailsVO1 != null)
                        {
                            count = commissionDetailsVO1.getCount();
                            amount = commissionDetailsVO1.getAmount();
                        }
                    }
                    String data = String.valueOf(EU_Country.getEnum(country));
                    if (functions.isValueNull(data))
                    {
                        transactionSummaryVO = payoutManager.getInternationalTotalAmountByTerminalForEUCountry(terminalVO, settlementDateVO);
                        totalSuccessCount = transactionSummaryVO.getTotalProcessingCount();
                        totalSuccessAmount = transactionSummaryVO.getTotalProcessingAmount();
                    }
                    else
                    {
                        transactionSummaryVO = payoutManager.getInternationalTotalAmountByTerminalForNonEU(terminalVO, settlementDateVO, country);
                        totalSuccessCount = transactionSummaryVO.getTotalProcessingCount();
                        totalSuccessAmount = transactionSummaryVO.getTotalProcessingAmount();
                    }

                    vCntCounter = totalSuccessCount + count;
                    vDblAmount = totalSuccessAmount + amount;
                    vDblTotal = (vDblAmount * agentCommissionVO.getCommissionValue()) / 100;

                    chargeDetailsVO.setCount(vCntCounter);
                    chargeDetailsVO.setAmount(vDblAmount);
                    chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
                }
            }

            else if (Charge_category.Success.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.TotalReserveGenerated.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeMasterVO.getSubKeyword()))
            {
                if (commissionDetailsVOHashMap.get(terminalId) != null)
                {
                    HashMap<String, CommissionDetailsVO> commissionDetailsVOs = commissionDetailsVOHashMap.get(terminalId);
                    CommissionDetailsVO commissionDetailsVO1 = commissionDetailsVOs.get(chargeMasterVO.getChargeName());
                    if (commissionDetailsVO1 != null)
                    {
                        count = commissionDetailsVO1.getCount();
                        amount = commissionDetailsVO1.getAmount();
                    }
                }
                vCntCounter = totalSaleCount + count;
                vDblAmount = totalSaleAmount + amount;
                vDblTotal = (vDblAmount * agentCommissionVO.getCommissionValue()) / 100;

                chargeDetailsVO.setCount(vCntCounter);
                chargeDetailsVO.setAmount(vDblAmount);
                chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
            }
            else if (Charge_category.Success.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.CalculatedReserveRefund.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeMasterVO.getSubKeyword()))
            {
                if (commissionDetailsVOHashMap.get(terminalId) != null)
                {
                    HashMap<String, CommissionDetailsVO> commissionDetailsVOs = commissionDetailsVOHashMap.get(terminalId);
                    CommissionDetailsVO commissionDetailsVO1 = commissionDetailsVOs.get(chargeMasterVO.getChargeName());
                    if (commissionDetailsVO1 != null)
                    {
                        count = commissionDetailsVO1.getCount();
                        amount = commissionDetailsVO1.getAmount();
                    }
                }
                vCntCounter = totalSaleCount + count;/*settledTransCount+reversedTransCount+chargebackTransCount;*/
                vDblAmount = totalSaleAmount + amount;/*totalSettledTransAmount+totalReversedTransAmount+totalChargebackTransAmount;*/
                vDblTotal = (vDblAmount * agentCommissionVO.getCommissionValue()) / 100;

                chargeDetailsVO.setCount(vCntCounter);
                chargeDetailsVO.setAmount(vDblAmount);
                chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
            }
            else if (Charge_category.Success.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeMasterVO.getSubKeyword()))
            {
                if (commissionDetailsVOHashMap.get(terminalId) != null)
                {
                    HashMap<String, CommissionDetailsVO> commissionDetailsVOs = commissionDetailsVOHashMap.get(terminalId);
                    CommissionDetailsVO commissionDetailsVO1 = commissionDetailsVOs.get(chargeMasterVO.getChargeName());
                    if (commissionDetailsVO1 != null)
                    {
                        count = commissionDetailsVO1.getCount();
                        amount = commissionDetailsVO1.getAmount();
                    }
                }
                vCntCounter = totalSaleCount + count;/*settledTransCount+reversedTransCount+chargebackTransCount;*/
                vDblAmount = totalSaleAmount + amount;/*totalSettledTransAmount+totalReversedTransAmount+totalChargebackTransAmount;*/
                vDblTotal = vCntCounter * agentCommissionVO.getCommissionValue();

                chargeDetailsVO.setCount(vCntCounter);
                chargeDetailsVO.setAmount(vDblAmount);
                chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
            }
            else if (Charge_category.Success.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Reversed.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeMasterVO.getSubKeyword()))
            {
                if (commissionDetailsVOHashMap.get(terminalId) != null)
                {
                    HashMap<String, CommissionDetailsVO> commissionDetailsVOs = commissionDetailsVOHashMap.get(terminalId);
                    CommissionDetailsVO commissionDetailsVO1 = commissionDetailsVOs.get(chargeMasterVO.getChargeName());
                    if (commissionDetailsVO1 != null)
                    {
                        count = commissionDetailsVO1.getCount();
                        amount = commissionDetailsVO1.getAmount();
                    }
                }
                vCntCounter = reversedTransCount + count;
                vDblAmount = totalReversedTransAmount + amount;
                vDblTotal = (vCntCounter * agentCommissionVO.getCommissionValue());

                chargeDetailsVO.setCount(vCntCounter);
                chargeDetailsVO.setAmount(vDblAmount);
                chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
            }
            else if (Charge_category.Success.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Chargeback.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeMasterVO.getSubKeyword()))
            {
                if (commissionDetailsVOHashMap.get(terminalId) != null)
                {
                    HashMap<String, CommissionDetailsVO> commissionDetailsVOs = commissionDetailsVOHashMap.get(terminalId);
                    CommissionDetailsVO commissionDetailsVO1 = commissionDetailsVOs.get(chargeMasterVO.getChargeName());
                    if (commissionDetailsVO1 != null)
                    {
                        count = commissionDetailsVO1.getCount();
                        amount = commissionDetailsVO1.getAmount();
                    }
                }
                vCntCounter = chargebackTransCount + count;
                vDblAmount = totalChargebackTransAmount + amount;
                vDblTotal = (vCntCounter * agentCommissionVO.getCommissionValue());

                chargeDetailsVO.setCount(vCntCounter);
                chargeDetailsVO.setAmount(vDblAmount);
                chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
            }
            else if (chargeMasterVO.getCategory().equals(Charge_category.Success.toString()) && chargeMasterVO.getKeyword().equals(Charge_keyword.Payout.toString()) && chargeMasterVO.getSubKeyword().equals(Charge_subKeyword.Amount.toString()))
            {
                if (commissionDetailsVOHashMap.get(terminalId) != null)
                {
                    HashMap<String, CommissionDetailsVO> commissionDetailsVOs = commissionDetailsVOHashMap.get(terminalId);
                    CommissionDetailsVO commissionDetailsVO1 = commissionDetailsVOs.get(chargeMasterVO.getChargeName());
                    if (commissionDetailsVO1 != null)
                    {
                        count = commissionDetailsVO1.getCount();
                        amount = commissionDetailsVO1.getAmount();
                    }
                }
                vCntCounter = totalPayoutCount + count;
                vDblAmount = totalPayoutAmount + amount;
                vDblTotal = vDblAmount * Double.valueOf(agentCommissionVO.getCommissionValue()) / 100;
                chargeDetailsVO.setCount(vCntCounter);
                chargeDetailsVO.setAmount(vDblAmount);
                chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
            }
            else if (Charge_category.Failure.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeMasterVO.getSubKeyword()))
            {
                if (commissionDetailsVOHashMap.get(terminalId) != null)
                {
                    HashMap<String, CommissionDetailsVO> commissionDetailsVOs = commissionDetailsVOHashMap.get(terminalId);
                    CommissionDetailsVO commissionDetailsVO1 = commissionDetailsVOs.get(chargeMasterVO.getChargeName());
                    if (commissionDetailsVO1 != null)
                    {
                        count = commissionDetailsVO1.getCount();
                        amount = commissionDetailsVO1.getAmount();
                    }
                }
                vCntCounter = authfailedTransCount + count;
                vDblAmount = totalAuthfailedTransAmount + amount;
                vDblTotal = (vCntCounter * agentCommissionVO.getCommissionValue());

                chargeDetailsVO.setCount(vCntCounter);
                chargeDetailsVO.setAmount(vDblAmount);
                chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
            }
            else if (Charge_category.Others.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeMasterVO.getSubKeyword()))
            {
                if (commissionDetailsVOHashMap.get(terminalId) != null)
                {
                    HashMap<String, CommissionDetailsVO> commissionDetailsVOs = commissionDetailsVOHashMap.get(terminalId);
                    CommissionDetailsVO commissionDetailsVO1 = commissionDetailsVOs.get(chargeMasterVO.getChargeName());
                    if (commissionDetailsVO1 != null)
                    {
                        count = commissionDetailsVO1.getCount();
                        amount = commissionDetailsVO1.getAmount();
                    }
                }

                vCntCounter = totalSaleCount + authfailedTransCount + count;
                vDblAmount = totalSaleAmount + totalAuthfailedTransAmount + amount;
                vDblTotal = (vCntCounter * agentCommissionVO.getCommissionValue());

                chargeDetailsVO.setCount(vCntCounter);
                chargeDetailsVO.setAmount(vDblAmount);
                chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
            }
        }
        catch (Exception e)
        {
            logger.error("Exception:::::", e);
        }
        return chargeDetailsVO;
    }

    public ChargeDetailsVO calculateAgentChargesValue(ChargeVO chargeVO, TransactionSummaryVO summaryVO, HashMap<String, HashMap<String, ChargeDetailsVO>> chargeDetailsVOHashMap, String terminalId, AgentCommissionReportVO agentCommissionReportVO)
    {
        long settledTransCount = 0;
        long reversedTransCount = 0;
        long chargebackTransCount = 0;
        long authfailedTransCount = 0;
        long totalSaleCount = 0;
        long totalPayoutCount = 0;

        double totalAuthfailedTransAmount = 0.00;
        double totalSettledTransAmount = 0.00;
        double totalReversedTransAmount = 0.00;
        double totalChargebackTransAmount = 0.00;
        double totalSaleAmount = 0.00;
        double totalPayoutAmount = 0.00;

        totalSaleCount = summaryVO.getTotalProcessingCount();
        totalSaleAmount = summaryVO.getTotalProcessingAmount();

        totalPayoutCount = summaryVO.getCountOfPayout();
        totalPayoutAmount = summaryVO.getPayoutAmount();

        authfailedTransCount = summaryVO.getCountOfAuthfailed();
        totalAuthfailedTransAmount = summaryVO.getAuthfailedAmount();

        settledTransCount = summaryVO.getCountOfSettled();
        totalSettledTransAmount = summaryVO.getSettledAmount();

        reversedTransCount = summaryVO.getCountOfReversed();
        totalReversedTransAmount = summaryVO.getReversedAmount();

        chargebackTransCount = summaryVO.getCountOfChargeback();
        totalChargebackTransAmount = summaryVO.getChargebackAmount();

        long vCntCounter = 0;
        double vDblAmount = 0.00;
        double vDblTotal = 0.00;


        ChargeDetailsVO chargeDetailsVO = new ChargeDetailsVO();

        chargeDetailsVO.setChargeName(chargeVO.getChargename());
        chargeDetailsVO.setChargeValue(String.valueOf(chargeVO.getAgentChargeValue()));
        chargeDetailsVO.setValueType(chargeVO.getValuetype());
        long count = 0;
        double amount = 0.00;
        TransactionSummaryVO transactionSummaryVO = null;
        TerminalVO terminalVO = agentCommissionReportVO.getTerminalVO();
        SettlementDateVO settlementDateVO = agentCommissionReportVO.getSettlementDateVO();
        String country = payoutDAO.getGatewayCountryFromAccountId(terminalVO.getAccountId());
        try
        {
            if (chargeVO.getCategory().equals(Charge_category.Success.toString()) && chargeVO.getKeyword().equals(Charge_keyword.FraudAlert.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString()))
            {
                if (chargeDetailsVOHashMap.get(terminalId) != null)
                {
                    HashMap<String, ChargeDetailsVO> commissionDetailsVOs = chargeDetailsVOHashMap.get(terminalId);
                    ChargeDetailsVO commissionDetailsVO1 = commissionDetailsVOs.get(chargeDetailsVO.getChargeName());
                    if (commissionDetailsVO1 != null)
                    {
                        count = commissionDetailsVO1.getCount();
                        amount = commissionDetailsVO1.getAmount();
                    }
                }
                transactionSummaryVO = payoutManager.getFraudDefenderCountAmountByTerminal(terminalVO, settlementDateVO);
                vCntCounter = transactionSummaryVO.getTotalProcessingCount() + count;
                vDblAmount = transactionSummaryVO.getTotalProcessingAmount() + amount;
                vDblTotal = vCntCounter * Double.valueOf(chargeVO.getAgentChargeValue());
                chargeDetailsVO.setCount(vCntCounter);
                chargeDetailsVO.setAmount(vDblAmount);
                chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
            }
            if (Charge_category.Others.toString().equals(chargeVO.getCategory()) && Charge_keyword.FraudulentTransaction.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
            {
                if (chargeDetailsVOHashMap.get(terminalId) != null)
                {
                    HashMap<String, ChargeDetailsVO> commissionDetailsVOs = chargeDetailsVOHashMap.get(terminalId);
                    ChargeDetailsVO commissionDetailsVO1 = commissionDetailsVOs.get(chargeDetailsVO.getChargeName());
                    if (commissionDetailsVO1 != null)
                    {
                        count = commissionDetailsVO1.getCount();
                        amount = commissionDetailsVO1.getAmount();
                    }
                }
                vCntCounter = getPaidFraudulentTransactionCountOnTerminal(terminalVO);
                vDblTotal = (vCntCounter + count) * Double.valueOf(chargeVO.getAgentChargeValue());
                chargeDetailsVO.setCount(vCntCounter + count);
                chargeDetailsVO.setAmount(amount);
                chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
            }
            if (chargeVO.getCategory().equals(Charge_category.Success.toString()) && chargeVO.getKeyword().equals(Charge_keyword.NetFinalAmount.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Amount.toString()))
            {
                if (chargeDetailsVOHashMap.get(terminalId) != null)
                {
                    HashMap<String, ChargeDetailsVO> commissionDetailsVOs = chargeDetailsVOHashMap.get(terminalId);
                    ChargeDetailsVO commissionDetailsVO1 = commissionDetailsVOs.get(chargeDetailsVO.getChargeName());
                    if (commissionDetailsVO1 != null)
                    {
                        count = commissionDetailsVO1.getCount();
                        amount = commissionDetailsVO1.getAmount();
                    }
                }
                double totalFundedToBank = payoutDAO.getMerchantTotalAmountFunded(terminalVO, settlementDateVO);//get the total count of paid wires from merchant_wiremanager between settlement date
                logger.error("totalFundedToBank in charge---" + totalFundedToBank + "---" + amount);
                vDblTotal = (totalFundedToBank + amount) * Double.valueOf(chargeVO.getAgentChargeValue()) / 100;

                chargeDetailsVO.setCount(0);
                chargeDetailsVO.setAmount(totalFundedToBank + amount);
                chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
            }
            if (chargeVO.getCategory().equals(Charge_category.Success.toString()) && chargeVO.getKeyword().equals(Charge_keyword.Payout.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Amount.toString()))
            {
                if (chargeDetailsVOHashMap.get(terminalId) != null)
                {
                    HashMap<String, ChargeDetailsVO> commissionDetailsVOs = chargeDetailsVOHashMap.get(terminalId);
                    ChargeDetailsVO commissionDetailsVO1 = commissionDetailsVOs.get(chargeDetailsVO.getChargeName());
                    if (commissionDetailsVO1 != null)
                    {
                        count = commissionDetailsVO1.getCount();
                        amount = commissionDetailsVO1.getAmount();
                    }
                }
                vCntCounter = totalPayoutCount + count;
                vDblAmount = totalPayoutAmount + amount;
                vDblTotal = vDblAmount * Double.valueOf(chargeVO.getAgentChargeValue()) / 100;
                chargeDetailsVO.setCount(vCntCounter);
                chargeDetailsVO.setAmount(vDblAmount);
                chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
            }
            if (Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
            {
                if (chargeDetailsVOHashMap.get(terminalId) != null)
                {
                    HashMap<String, ChargeDetailsVO> commissionDetailsVOs = chargeDetailsVOHashMap.get(terminalId);
                    ChargeDetailsVO commissionDetailsVO1 = commissionDetailsVOs.get(chargeDetailsVO.getChargeName());
                    if (commissionDetailsVO1 != null)
                    {
                        count = commissionDetailsVO1.getCount();
                        amount = commissionDetailsVO1.getAmount();
                    }
                }
                vCntCounter = totalSaleCount + count;/*settledTransCount+reversedTransCount+chargebackTransCount;*/
                vDblAmount = totalSaleAmount + amount;/*totalSettledTransAmount+totalReversedTransAmount+totalChargebackTransAmount;*/
                vDblTotal = (vDblAmount * Double.valueOf(chargeVO.getAgentChargeValue())) / 100;

                chargeDetailsVO.setCount(vCntCounter);
                chargeDetailsVO.setAmount(vDblAmount);
                chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
            }
            if (Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
            {
                if (chargeDetailsVOHashMap.get(terminalId) != null)
                {
                    HashMap<String, ChargeDetailsVO> commissionDetailsVOs = chargeDetailsVOHashMap.get(terminalId);
                    ChargeDetailsVO commissionDetailsVO1 = commissionDetailsVOs.get(chargeDetailsVO.getChargeName());
                    if (commissionDetailsVO1 != null)
                    {
                        count = commissionDetailsVO1.getCount();
                        amount = commissionDetailsVO1.getAmount();
                    }
                }
                vCntCounter = totalSaleCount + count;/*settledTransCount+reversedTransCount+chargebackTransCount;*/
                vDblAmount = totalSaleAmount + amount;/*totalSettledTransAmount+totalReversedTransAmount+totalChargebackTransAmount;*/
                vDblTotal = vCntCounter * Double.valueOf(chargeVO.getAgentChargeValue());

                chargeDetailsVO.setCount(vCntCounter);
                chargeDetailsVO.setAmount(vDblAmount);
                chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));

            }
            if (Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Reversed.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
            {
                if (chargeDetailsVOHashMap.get(terminalId) != null)
                {
                    HashMap<String, ChargeDetailsVO> commissionDetailsVOs = chargeDetailsVOHashMap.get(terminalId);
                    ChargeDetailsVO commissionDetailsVO1 = commissionDetailsVOs.get(chargeDetailsVO.getChargeName());
                    if (commissionDetailsVO1 != null)
                    {
                        count = commissionDetailsVO1.getCount();
                        amount = commissionDetailsVO1.getAmount();
                    }
                }
                vCntCounter = reversedTransCount + count;
                vDblAmount = totalReversedTransAmount + amount;
                vDblTotal = (vCntCounter * Double.valueOf(chargeVO.getAgentChargeValue()));

                chargeDetailsVO.setCount(vCntCounter);
                chargeDetailsVO.setAmount(vDblAmount);
                chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
            }
            if (Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.Chargeback.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
            {
                if (chargeDetailsVOHashMap.get(terminalId) != null)
                {
                    HashMap<String, ChargeDetailsVO> commissionDetailsVOs = chargeDetailsVOHashMap.get(terminalId);
                    ChargeDetailsVO commissionDetailsVO1 = commissionDetailsVOs.get(chargeDetailsVO.getChargeName());
                    if (commissionDetailsVO1 != null)
                    {
                        count = commissionDetailsVO1.getCount();
                        amount = commissionDetailsVO1.getAmount();
                    }
                }
                vCntCounter = chargebackTransCount + count;
                vDblAmount = totalChargebackTransAmount + amount;
                vDblTotal = (vCntCounter * Double.valueOf(chargeVO.getAgentChargeValue()));

                chargeDetailsVO.setCount(vCntCounter);
                chargeDetailsVO.setAmount(vDblAmount);
                chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
            }
            if (Charge_category.Failure.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
            {
                if (chargeDetailsVOHashMap.get(terminalId) != null)
                {
                    HashMap<String, ChargeDetailsVO> commissionDetailsVOs = chargeDetailsVOHashMap.get(terminalId);
                    ChargeDetailsVO commissionDetailsVO1 = commissionDetailsVOs.get(chargeDetailsVO.getChargeName());
                    if (commissionDetailsVO1 != null)
                    {
                        count = commissionDetailsVO1.getCount();
                        amount = commissionDetailsVO1.getAmount();
                    }
                }
                vCntCounter = authfailedTransCount + count;
                vDblAmount = totalAuthfailedTransAmount + amount;
                vDblTotal = (vCntCounter * Double.valueOf(chargeVO.getAgentChargeValue()));

                chargeDetailsVO.setCount(vCntCounter);
                chargeDetailsVO.setAmount(vDblAmount);
                chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
            }
            if (Charge_category.Others.toString().equals(chargeVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
            {
                if (chargeDetailsVOHashMap.get(terminalId) != null)
                {
                    HashMap<String, ChargeDetailsVO> commissionDetailsVOs = chargeDetailsVOHashMap.get(terminalId);
                    ChargeDetailsVO commissionDetailsVO1 = commissionDetailsVOs.get(chargeDetailsVO.getChargeName());
                    if (commissionDetailsVO1 != null)
                    {
                        count = commissionDetailsVO1.getCount();
                        amount = commissionDetailsVO1.getAmount();
                    }
                }
                vCntCounter = totalSaleCount + authfailedTransCount + count;
                vDblAmount = totalSaleAmount + totalAuthfailedTransAmount + amount;
                vDblTotal = (vCntCounter * Double.valueOf(chargeVO.getAgentChargeValue()));

                chargeDetailsVO.setCount(vCntCounter);
                chargeDetailsVO.setAmount(vDblAmount);
                chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
            }
            if (Charge_category.Others.toString().equals(chargeVO.getCategory()) && Charge_keyword.Wire.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
            {
                vCntCounter = 1;
                vDblAmount = 0.00;
                vDblTotal = (vCntCounter * Double.valueOf(chargeVO.getAgentChargeValue()));

                chargeDetailsVO.setCount(vCntCounter);
                chargeDetailsVO.setAmount(vDblAmount);
                chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
            }
            if (chargeVO.getCategory().equals(Charge_category.Others.toString()) && chargeVO.getKeyword().equals(Charge_keyword.CaseFiling.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Count.toString()))
            {
                if (chargeDetailsVOHashMap.get(terminalId) != null)
                {
                    HashMap<String, ChargeDetailsVO> commissionDetailsVOs = chargeDetailsVOHashMap.get(terminalId);
                    ChargeDetailsVO commissionDetailsVO1 = commissionDetailsVOs.get(chargeDetailsVO.getChargeName());
                    if (commissionDetailsVO1 != null)
                    {
                        count = commissionDetailsVO1.getCount();
                        //amount = commissionDetailsVO1.getAmount();
                    }
                }
                long caseFilingTransactionCount = payoutDAO.getCaseFilingTransactionCount(terminalVO, settlementDateVO);
                vCntCounter = caseFilingTransactionCount + count;
                vDblAmount = 0.00;
                vDblTotal = (vCntCounter * Double.valueOf(chargeVO.getAgentChargeValue()));

                chargeDetailsVO.setCount(vCntCounter);
                chargeDetailsVO.setAmount(vDblAmount);
                chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
            }
            if (chargeVO.getCategory().equals(Charge_category.Success.toString()) && chargeVO.getKeyword().equals(Charge_keyword.DomesticTotal.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Amount.toString()) && functions.isValueNull(country))
            {
                long totalSuccessCount = 0;
                double totalSuccessAmount = 0.0;

                if (functions.isValueNull(country))
                {
                    if (chargeDetailsVOHashMap.get(terminalId) != null)
                    {
                        HashMap<String, ChargeDetailsVO> commissionDetailsVOs = chargeDetailsVOHashMap.get(terminalId);
                        ChargeDetailsVO commissionDetailsVO1 = commissionDetailsVOs.get(chargeDetailsVO.getChargeName());
                        if (commissionDetailsVO1 != null)
                        {
                            count = commissionDetailsVO1.getCount();
                            amount = commissionDetailsVO1.getAmount();
                        }
                    }
                    String data = String.valueOf(EU_Country.getEnum(country));
                    if (functions.isValueNull(data))
                    {
                        transactionSummaryVO = payoutManager.getDomesticTotalAmountByTerminalForEUCountry(terminalVO, settlementDateVO);
                        totalSuccessCount = transactionSummaryVO.getTotalProcessingCount();
                        totalSuccessAmount = transactionSummaryVO.getTotalProcessingAmount();
                    }
                    else
                    {
                        transactionSummaryVO = payoutManager.getDomesticTotalAmountByTerminalForNonEU(terminalVO, settlementDateVO, country);
                        totalSuccessCount = transactionSummaryVO.getTotalProcessingCount();
                        totalSuccessAmount = transactionSummaryVO.getTotalProcessingAmount();
                    }

                    vCntCounter = totalSuccessCount + count;
                    vDblAmount = totalSuccessAmount + amount;
                    vDblTotal = (vDblAmount * Double.parseDouble(chargeVO.getAgentChargeValue())) / 100;

                    chargeDetailsVO.setCount(vCntCounter);
                    chargeDetailsVO.setAmount(vDblAmount);
                    chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
                }
            }
            if (chargeVO.getCategory().equals(Charge_category.Success.toString()) && chargeVO.getKeyword().equals(Charge_keyword.InternationalTotal.toString()) && chargeVO.getSubkeyword().equals(Charge_subKeyword.Amount.toString()) && functions.isValueNull(country))
            {
                long totalSuccessCount = 0;
                double totalSuccessAmount = 0.0;
                if (functions.isValueNull(country))
                {
                    if (chargeDetailsVOHashMap.get(terminalId) != null)
                    {
                        HashMap<String, ChargeDetailsVO> commissionDetailsVOs = chargeDetailsVOHashMap.get(terminalId);
                        ChargeDetailsVO commissionDetailsVO1 = commissionDetailsVOs.get(chargeDetailsVO.getChargeName());
                        if (commissionDetailsVO1 != null)
                        {
                            count = commissionDetailsVO1.getCount();
                            amount = commissionDetailsVO1.getAmount();
                        }
                    }
                    String data = String.valueOf(EU_Country.getEnum(country));
                    if (functions.isValueNull(data))
                    {
                        transactionSummaryVO = payoutManager.getInternationalTotalAmountByTerminalForEUCountry(terminalVO, settlementDateVO);
                        totalSuccessCount = transactionSummaryVO.getTotalProcessingCount();
                        totalSuccessAmount = transactionSummaryVO.getTotalProcessingAmount();
                    }
                    else
                    {
                        transactionSummaryVO = payoutManager.getInternationalTotalAmountByTerminalForNonEU(terminalVO, settlementDateVO, country);
                        totalSuccessCount = transactionSummaryVO.getTotalProcessingCount();
                        totalSuccessAmount = transactionSummaryVO.getTotalProcessingAmount();
                    }

                    vCntCounter = totalSuccessCount + count;
                    vDblAmount = totalSuccessAmount + amount;
                    vDblTotal = (vDblAmount * Double.parseDouble(chargeVO.getAgentChargeValue())) / 100;

                    chargeDetailsVO.setCount(vCntCounter);
                    chargeDetailsVO.setAmount(vDblAmount);
                    chargeDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
                }
            }
        }
        catch (Exception e)
        {
            logger.error("Exception--->", e);
        }
        return chargeDetailsVO;
    }

    public long getPaidFraudulentTransactionCountOnTerminal(TerminalVO terminalVO)
    {
        long paidFraudulentTransactionCount = 0;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        try
        {
            con = Database.getRDBConnection();
            String Query = "SELECT SUM(fraudulent_transaction_count) AS 'FraudulentTransactionPaidCount' FROM member_settlementcycle_details WHERE memberid=? and terminalid=? and cycleid=?";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, terminalVO.getMemberId());
            preparedStatement.setString(2, terminalVO.getTerminalId());
            preparedStatement.setString(3, terminalVO.getWireId());
            logger.error("getPaidFraudulentTransactionCountOnTerminal::::" + preparedStatement);
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                paidFraudulentTransactionCount = rsPayout.getLong("FraudulentTransactionPaidCount");
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError e)
        {
            logger.error("SystemError ::::::: Leaving transactionEntry ", e);
        }
        finally
        {
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return paidFraudulentTransactionCount;
    }

    public String isValidAmount(String isValidAmount)
    {
        if (Double.parseDouble(isValidAmount) > 0)
        {
            isValidAmount = "-" + isValidAmount;
        }

        return isValidAmount;
    }

    private static Timestamp convertStringToTimestamp1(String date) throws ParseException
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date parsedDate = null;
        parsedDate = dateFormat.parse(date);
        Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
        return timestamp;
    }

    public TotalFeesChargesVO calculateTotalFeesCharge(String chargeName, HashMap<String, TotalFeesChargesVO> totalFeesChargesVOHashMap, double TotalFees)
    {
        ChargeDetailsVO detailsVO = totalFeesChargesVOHashMap.get(chargeName);
        TotalFeesChargesVO totalFeesChargesVO = new TotalFeesChargesVO();
        totalFeesChargesVO.setChargeName(detailsVO.getChargeName());
        totalFeesChargesVO.setCount(0);
        totalFeesChargesVO.setChargeValue(detailsVO.getChargeValue());
        totalFeesChargesVO.setValueType(detailsVO.getValueType());
        totalFeesChargesVO.setAmount(TotalFees);
        if (TotalFees > 0)
        {
            if (detailsVO.getValueType().equalsIgnoreCase("Percentage"))
            {
                double total = TotalFees * Double.valueOf(detailsVO.getChargeValue()) / 100;
                totalFeesChargesVO.setTotal(Functions.roundDBL(total, 2));
            }
            else
            {
                double total = Double.valueOf(detailsVO.getChargeValue());
                totalFeesChargesVO.setTotal(Functions.roundDBL(total, 2));
            }
        }
        else
        {
            totalFeesChargesVO.setTotal(0.0);
        }

        return totalFeesChargesVO;
    }

    public OtherChargesVO CalculateOthercharges(String chargeName, HashMap<String, OtherChargesVO> otherChargesVOHashMap, double grossAmount)
    {
        ChargeDetailsVO detailsVO = otherChargesVOHashMap.get(chargeName);
        OtherChargesVO otherChargesVO = new OtherChargesVO();
        otherChargesVO.setCount(0);
        otherChargesVO.setChargeValue(detailsVO.getChargeValue());
        otherChargesVO.setChargeName(detailsVO.getChargeName());
        otherChargesVO.setValueType(detailsVO.getValueType());
        otherChargesVO.setAmount(grossAmount);
        if (grossAmount < 0)
        {
            grossAmount = -1 * grossAmount;
        }
        if (detailsVO.getValueType().equalsIgnoreCase("Percentage"))
        {
            double total = grossAmount * Double.valueOf(detailsVO.getChargeValue()) / 100;
            otherChargesVO.setTotal(Functions.roundDBL(total, 2));
        }
        else
        {
            double total = Double.valueOf(detailsVO.getChargeValue());
            otherChargesVO.setTotal(Functions.roundDBL(total, 2));
        }

        return otherChargesVO;
    }
}