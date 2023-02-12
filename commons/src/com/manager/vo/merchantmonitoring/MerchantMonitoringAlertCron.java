package com.manager.vo.merchantmonitoring;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.logicboxes.util.ApplicationProperties;
import com.manager.DateManager;
import com.manager.MerchantMonitoringManager;
import com.manager.TerminalManager;
import com.manager.TransactionManager;
import com.manager.dao.AgentDAO;
import com.manager.dao.MerchantDAO;
import com.manager.dao.MerchantMonitoringDAO;
import com.manager.dao.TransactionDAO;
import com.manager.enums.TransReqRejectCheck;
import com.manager.helper.RatioCalculationHelper;
import com.manager.vo.*;
import com.manager.vo.merchantmonitoring.enums.*;
import com.payment.Mail.*;
import com.payment.PZTransactionStatus;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * Created by sandip on 5/11/16.
 */
public class MerchantMonitoringAlertCron
{
    private final static String SETTLEMENT_FILE_PATH = ApplicationProperties.getProperty("SETTLEMENT_FILE_PATH");
    Logger logger = new Logger(MerchantMonitoringAlertCron.class.getName());
    Functions functions = new Functions();
    public void dailyMerchantMonitoringRiskAlerts() throws Exception
    {
        MerchantMonitoringManager merchantMonitoringManager = new MerchantMonitoringManager();
        Set<String> partnersList=new TreeSet();
        Set<String> agentList=new TreeSet();
        AgentDAO agentDAO=new AgentDAO();
        Map<String, Map<String, List<MonitoringParameterMappingVO>>> stringListMap = merchantMonitoringManager.getMonitoringParameterGroupByMerchantDailyExecution();
        logger.debug("Number of Terminals to process" + stringListMap.size());
        if (stringListMap.size() > 0)
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM,YYYY");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            /* Admin Type-1 Consolidated Email */
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminSalesStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminCBStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminRFStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminTechStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminFraudStringListHashMapConsolidate = new HashMap();

            /* Admin Type-2 Consolidated Email */
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminSalesStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminCBStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminRFStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminFraudStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminTechStringAttachmentListHashMapConsolidate = new HashMap();

            /* Admin Type-3 Consolidated Email */
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminSalesStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminCBStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminRFStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminTechStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminFraudStringListHashMapConsolidateSuspension = new HashMap();

            /* Agent Type-1 Consolidated Email */
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentSalesStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentCBStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentRFStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentFraudStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentTechStringListHashMapConsolidate = new HashMap();

            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentSalesStringListHashMapConsolidatePerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentCBStringListHashMapConsolidatePerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentRFStringListHashMapConsolidatePerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentFraudStringListHashMapConsolidatePerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentTechStringListHashMapConsolidatePerAgent = new HashMap();

            /* Agent Type-2 Consolidated Email */
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentSalesStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentCBStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentRFStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentFraudStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentTechStringAttachmentListHashMapConsolidate = new HashMap();

            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentSalesStringAttachmentListHashMapConsolidatePerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentCBStringAttachmentListHashMapConsolidatePerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentRFStringAttachmentListHashMapConsolidatePerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentFraudStringAttachmentListHashMapConsolidatePerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentTechStringAttachmentListHashMapConsolidatePerAgent = new HashMap();


            /*Agent Type-3 Consolidated Email */
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentSalesStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentCBStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentRFStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentFraudStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentTechStringListHashMapConsolidateSuspension = new HashMap();

            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentSalesStringListHashMapConsolidateSuspensionPerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentCBStringListHashMapConsolidateSuspensionPerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentRFStringListHashMapConsolidateSuspensionPerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentFraudStringListHashMapConsolidateSuspensionPerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentTechStringListHashMapConsolidateSuspensionPerAgent = new HashMap();


            /*Partner Type-1 Consolidated Email-Per Merchant */
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerSalesStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerFraudStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerCBStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerRFStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerTechStringListHashMapConsolidate = new HashMap();

            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerSalesStringListHashMapConsolidatePerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerFraudStringListHashMapConsolidatePerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerCBStringListHashMapConsolidatePerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerRFStringListHashMapConsolidatePerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerTechStringListHashMapConsolidatePerPartner = new HashMap();

            /*Partner Type-2 Consolidated Email */
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerSalesStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerFraudStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerCBStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerRFStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerTechStringListHashMapConsolidateSuspension = new HashMap();

            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerSalesStringListHashMapConsolidateSuspensionPerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerFraudStringListHashMapConsolidateSuspensionPerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerCBStringListHashMapConsolidateSuspensionPerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerRFStringListHashMapConsolidateSuspensionPerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerTechStringListHashMapConsolidateSuspensionPerPartner = new HashMap();

            /*Partner Type-3 Consolidated Email */
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerSalesStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerCBStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerRFStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerFraudStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerTechStringAttachmentListHashMapConsolidate = new HashMap();

            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerSalesStringListHashMapConsolidateAttachmentPerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerFraudStringListHashMapConsolidateAttachmentPerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerCBStringListHashMapConsolidateAttachmentPerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerRFStringListHashMapConsolidateAttachmentPerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerTechStringListHashMapConsolidateAttachmentPerPartner = new HashMap();

            TerminalManager terminalManager = new TerminalManager();
            Set set = stringListMap.keySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext())
            {


                Map<String, List<MonitoringAlertDetailVO>> adminSalesStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminCBStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminRFStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminTechStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminFraudStringListHashMap = new HashMap();

                Map<String, List<MonitoringAlertDetailVO>> adminSalesStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminCBStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminRFStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminFraudStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminTechStringAttachmentListHashMapPerTerminal = new HashMap();

                Map<String, List<MonitoringAlertDetailVO>> adminSalesListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminCBListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminRFListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminFraudListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminTechListHashMapPerTerminalSuspension = new HashMap();

                /* Merchant Type-1 Consolidated Email */
                Map<String, List<MonitoringAlertDetailVO>> merchantSalesStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantCBStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantRFStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantFraudStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantTechnicalStringListHashMap = new HashMap();

                /* Merchant Type-2 Consolidated Email */
                Map<String, List<MonitoringAlertDetailVO>> merchantSalesStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantCBStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantRFStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantFraudStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantTechStringAttachmentListHashMapPerTerminal = new HashMap();

                /* Merchant Type-3 Consolidated Email */
                Map<String, List<MonitoringAlertDetailVO>> merchantSalesListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantCBListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantRFListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantFraudListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantTechListHashMapPerTerminalSuspension = new HashMap();

                Map<String, List<MonitoringAlertDetailVO>> partnerCBStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerRFStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerSalesStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerFraudStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerTechnicalStringListHashMap = new HashMap();

                Map<String, List<MonitoringAlertDetailVO>> partnerSalesStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerCBStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerRFStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerFraudStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerTechStringAttachmentListHashMapPerTerminal = new HashMap();

                Map<String, List<MonitoringAlertDetailVO>> partnerCBListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerRFListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerSalesListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerFraudListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerTechnicalListHashMapTerminalSuspension = new HashMap();

                Map<String, List<MonitoringAlertDetailVO>> agentSalesStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentCBStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentRFStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentFraudStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentTechStringListHashMap = new HashMap();

                Map<String, List<MonitoringAlertDetailVO>> agentSalesStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentCBStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentRFStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentFraudStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentTechStringAttachmentListHashMapPerTerminal = new HashMap();

                Map<String, List<MonitoringAlertDetailVO>> agentSalesStringListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentCBStringListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentRFStringListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentFraudStringListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentTechStringListHashMapPerTerminalSuspension = new HashMap();

                boolean isRuleVoilate=false;
                String memberId = (String) iterator.next();

                MerchantDAO merchantDAO = new MerchantDAO();
                MerchantDetailsVO merchantDetailsVO = merchantDAO.getMemberDetails(memberId);
                String partnerId=merchantDetailsVO.getPartnerId();
                partnersList.add(partnerId);
                List<AgentDetailsVO> agentDetailsVOs=agentDAO.getMerchantAgents(memberId);


                Map<String, List<MonitoringParameterMappingVO>> stringListMapTerminal = stringListMap.get(memberId);
                Set terminalSet = stringListMapTerminal.keySet();
                Iterator terminalIterator = terminalSet.iterator();
                while (terminalIterator.hasNext())
                {
                    String terminalId = (String) terminalIterator.next();
                    TerminalVO terminalVO = terminalManager.getMemberTerminalWithActivationDetails(memberId, terminalId);

                    String terminalActivationDate = terminalVO.getActivationDate();
                    String firstTransactionDate = merchantMonitoringManager.getMemberFirstSubmission(terminalVO);

                    String lastTransactionDate = merchantMonitoringManager.getMemberLastSubmission(terminalVO);

                    if (!functions.isValueNull(terminalVO.getActivationDate()))
                    {
                        terminalActivationDate = firstTransactionDate;
                    }

                    if(!functions.isValueNull(terminalActivationDate))
                    {
                        logger.debug("Terminal activation date is not found so we can not process risk rules on===" + terminalId);
                        continue;
                    }

                    //TerminalProcessingDetailsVO currentDayProcessingDetails = merchantMonitoringManager.getTerminalProcessingDetailsByDTStamp(terminalVO);
                    TerminalProcessingDetailsVO currentDayProcessingDetails = merchantMonitoringManager.getPreviousDayTerminalProcessingDetailsByDTStamp(terminalVO);
                    if (!(currentDayProcessingDetails.getTotalProcessingCount() > 0))
                    {
                        logger.debug("========================");
                        logger.debug("===" + memberId + ":" + terminalId);
                        logger.debug("No Transaction Founds For Today's Processing Cycle");
                        logger.debug("=========================");
                        //continue;
                    }
                    else
                    {
                        logger.debug("========================");
                        logger.debug("===" + memberId + ":" + terminalId);
                        logger.debug("Today's Processing Count========" + currentDayProcessingDetails.getTotalProcessingCount());
                        logger.debug("Today's Processing Amount=======" + currentDayProcessingDetails.getTotalProcessingAmount());
                        logger.debug("=========================");
                    }

                    DateManager dateManager = new DateManager();
                    DateVO dateVO = null;
                    Date startDate = null;
                    try
                    {
                        dateVO = dateManager.getPreviousDayDateRange();
                        startDate = dateFormat.parse(dateVO.getStartDate());
                    }
                    catch (Exception e)
                    {
                        logger.debug("Date Exception==="+e);
                    }

                    List<MonitoringParameterMappingVO> monitoringParameterMappingVOs = stringListMapTerminal.get(terminalId);

                    TerminalProcessingDetailsVO priorMonthProcessingDetailsVO = merchantMonitoringManager.getLastMonthProcessingDetailsAsPerCalendarMonth(terminalVO);
                    TerminalProcessingDetailsVO currentMonthProcessingDetails = merchantMonitoringManager.getCurrentMonthProcessingDetailsAsPerCalendarMonthByDTStamp(terminalVO);
                    TerminalProcessingDetailsVO lastThreeMonthProcessingDetailsVO = merchantMonitoringManager.getLastThreeMonthProcessingDetailsAsPerCalendarMonth(terminalVO);

                    List<TransactionVO> todaysCBTransactionVOsList = merchantMonitoringManager.getTransactionListByTimestamp(terminalVO, dateVO, "chargeback");
                    List<TransactionVO> todaysRFTransactionVOsList = merchantMonitoringManager.getTransactionListByTimestamp(terminalVO, dateVO, "reversed");
                    List<TransactionVO> todaysInCompleteTransactionVOsList = merchantMonitoringManager.getInCompleteTransactionListByTimestamp(terminalVO, dateVO);

                    List<BinAmountVO> binAmountVOList = merchantMonitoringManager.getSameCardSameAmountDetail(terminalVO,dateVO);
                    TerminalLimitsVO terminalLimitsVO = terminalManager.getMemberTerminalProcessingLimitVO(memberId, terminalId);

                    currentDayProcessingDetails.setFirstTransactionDate(firstTransactionDate);
                    currentDayProcessingDetails.setLastTransactionDate(lastTransactionDate);

                    terminalVO.setActivationDate(terminalActivationDate);

                    /*Admin Type-1 List*/
                    List<MonitoringAlertDetailVO> adminSalesMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> adminCBMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> adminRFMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> adminFraudMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> adminTechMonitoringAlertDetailVOs = new ArrayList();

                    /*Admin Type-2 List*/
                    List<MonitoringAlertDetailVO> adminSalesMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> adminCBMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> adminRFMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> adminFraudMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> adminTechMonitoringAttachmentAlertDetailVOs = new ArrayList();

                    /*Admin Type-3 List*/
                    List<MonitoringAlertDetailVO> adminSalesMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> adminCBMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> adminRFMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> adminFraudMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> adminTechMonitoringAlertDetailVOsSuspension = new ArrayList();

                    /*Agent Type-1 List*/
                    List<MonitoringAlertDetailVO> agentSalesMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> agentCBsMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> agentRFsMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> agentFraudsMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> agentTechsMonitoringAlertDetailVOs = new ArrayList();

                    /*Agent Type-2 List*/
                    List<MonitoringAlertDetailVO> agentSalesMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> agentCBMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> agentRFMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> agentFraudMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> agentTechMonitoringAttachmentAlertDetailVOs = new ArrayList();

                    /*Admin Type-2 List*/
                    List<MonitoringAlertDetailVO> agentSalesMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> agentCBMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> agentRFMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> agentFraudMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> agentTechMonitoringAlertDetailVOsSuspension = new ArrayList();

                    /*Merchant Type-1 List*/
                    List<MonitoringAlertDetailVO> merchantCBMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantRFMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantSalesMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantFraudMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantTechnicalMonitoringAlertDetailVOs = new ArrayList();

                    /*Merchant Type-2 List*/
                    List<MonitoringAlertDetailVO> merchantSalesMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantCBMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantRFMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantFraudMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantTechMonitoringAttachmentAlertDetailVOs = new ArrayList();

                    /*Merchant Type-3 List*/
                    List<MonitoringAlertDetailVO> merchantCBMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantRFMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantSalesMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantFraudMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantTechnicalMonitoringAlertDetailVOsSuspension = new ArrayList();

                    /*Partner Type-1 List*/
                    List<MonitoringAlertDetailVO> partnerCBMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerRFMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerSalesMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerFraudMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerTechnicalMonitoringAlertDetailVOs = new ArrayList();

                    /*Partner Type-2 List*/
                    List<MonitoringAlertDetailVO> partnerSalesMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerCBMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerRFMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerFraudMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerTechMonitoringAttachmentAlertDetailVOs = new ArrayList();

                    /*Partner Type-3 List*/
                    List<MonitoringAlertDetailVO> partnerCBMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerRFMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerSalesMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerFraudMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerTechMonitoringAlertDetailVOsSuspension = new ArrayList();

                    logger.debug("size=====" + monitoringParameterMappingVOs.size());
                    for (MonitoringParameterMappingVO monitoringParameterMappingVO : monitoringParameterMappingVOs)
                    {
                        MonitoringParameterVO monitoringParameterVO = monitoringParameterMappingVO.getMonitoringParameterVO();
                        MonitoringAlertDetailVO monitoringAlertDetailVO = null;
                        if ("Y".equals(monitoringParameterMappingVO.getAlertActivation()))
                        {
                            //MonitoringAlertDetailVO monitoringAlertDetailVO = null;
                            if (MonitoringKeyword.AuthSuccessful.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.StuckTransaction.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.BlockedCountry.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.BlockedIP.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.BlockedEmail.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.BlockedCard.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.BlockedName.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.MatureChargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.MatureRefund.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.HighAmountOrder.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.SameCardSameAmount.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.SameCardSameAmountConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || (MonitoringKeyword.Chargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())))
                            {
                                monitoringAlertDetailVO = calculateAttachment(monitoringParameterMappingVO, terminalVO, todaysInCompleteTransactionVOsList, todaysCBTransactionVOsList, todaysRFTransactionVOsList,binAmountVOList, dateVO,"daily");
                            }
                            else
                            {
                                monitoringAlertDetailVO = alertOnlyThresholdAnalysisDaily(monitoringParameterMappingVO, currentDayProcessingDetails, priorMonthProcessingDetailsVO, terminalVO, lastThreeMonthProcessingDetailsVO, terminalLimitsVO, currentMonthProcessingDetails);
                            }
                            if (monitoringAlertDetailVO != null)
                            {
                                isRuleVoilate=true;
                                if (monitoringAlertDetailVO instanceof MonitoringOnlyAlertDetailsVO)
                                {
                                    monitoringAlertDetailVO.setMonitoringAlertPeriod(simpleDateFormat.format(startDate));
                                    logger.debug("OnlyAlert Instance");
                                    monitoringAlertDetailVO.setAlertThreshold(monitoringParameterMappingVO.getAlertThreshold());
                                    monitoringAlertDetailVO.setSuspensionThreshold(monitoringParameterMappingVO.getSuspensionThreshold());
                                    setMailIntoAppropriateListGroup(monitoringAlertDetailVO, adminSalesMonitoringAlertDetailVOs, adminCBMonitoringAlertDetailVOs, adminRFMonitoringAlertDetailVOs, adminFraudMonitoringAlertDetailVOs, adminTechMonitoringAlertDetailVOs, agentSalesMonitoringAlertDetailVOs, agentCBsMonitoringAlertDetailVOs, agentRFsMonitoringAlertDetailVOs, agentFraudsMonitoringAlertDetailVOs, agentTechsMonitoringAlertDetailVOs, merchantCBMonitoringAlertDetailVOs, merchantRFMonitoringAlertDetailVOs, merchantSalesMonitoringAlertDetailVOs, merchantFraudMonitoringAlertDetailVOs, merchantTechnicalMonitoringAlertDetailVOs, partnerCBMonitoringAlertDetailVOs, partnerRFMonitoringAlertDetailVOs, partnerSalesMonitoringAlertDetailVOs, partnerFraudMonitoringAlertDetailVOs, partnerTechnicalMonitoringAlertDetailVOs, monitoringParameterMappingVO);
                                    setMailIntoAppropriateMapGroup(terminalId, adminSalesMonitoringAlertDetailVOs, adminCBMonitoringAlertDetailVOs, adminRFMonitoringAlertDetailVOs, adminFraudMonitoringAlertDetailVOs, adminTechMonitoringAlertDetailVOs, agentSalesMonitoringAlertDetailVOs, agentCBsMonitoringAlertDetailVOs, agentRFsMonitoringAlertDetailVOs, agentFraudsMonitoringAlertDetailVOs, agentTechsMonitoringAlertDetailVOs, merchantCBMonitoringAlertDetailVOs, merchantRFMonitoringAlertDetailVOs, merchantSalesMonitoringAlertDetailVOs, merchantFraudMonitoringAlertDetailVOs, merchantTechnicalMonitoringAlertDetailVOs, partnerCBMonitoringAlertDetailVOs, partnerRFMonitoringAlertDetailVOs, partnerSalesMonitoringAlertDetailVOs, partnerFraudMonitoringAlertDetailVOs, partnerTechnicalMonitoringAlertDetailVOs, adminSalesStringListHashMap, adminCBStringListHashMap, adminRFStringListHashMap, adminFraudStringListHashMap, adminTechStringListHashMap, agentSalesStringListHashMap, agentCBStringListHashMap, agentRFStringListHashMap, agentFraudStringListHashMap, agentTechStringListHashMap, merchantSalesStringListHashMap, merchantCBStringListHashMap, merchantRFStringListHashMap, merchantFraudStringListHashMap, merchantTechnicalStringListHashMap, partnerCBStringListHashMap, partnerRFStringListHashMap, partnerSalesStringListHashMap, partnerFraudStringListHashMap, partnerTechnicalStringListHashMap);
                                }
                                else if (monitoringAlertDetailVO instanceof MonitoringAttachmentAlertDetailsVO)
                                {
                                    monitoringAlertDetailVO.setMonitoringAlertPeriod(simpleDateFormat.format(startDate));
                                    logger.debug("Attachment Alert Instance");
                                    monitoringAlertDetailVO.setAlertThreshold(monitoringParameterMappingVO.getAlertThreshold());
                                    monitoringAlertDetailVO.setSuspensionThreshold(monitoringParameterMappingVO.getSuspensionThreshold());
                                    setMailIntoAppropriateListGroup2(monitoringAlertDetailVO, adminSalesMonitoringAttachmentAlertDetailVOs, adminCBMonitoringAttachmentAlertDetailVOs, adminRFMonitoringAttachmentAlertDetailVOs, adminFraudMonitoringAttachmentAlertDetailVOs, adminTechMonitoringAttachmentAlertDetailVOs, merchantSalesMonitoringAttachmentAlertDetailVOs, merchantCBMonitoringAttachmentAlertDetailVOs, merchantRFMonitoringAttachmentAlertDetailVOs, merchantFraudMonitoringAttachmentAlertDetailVOs, merchantTechMonitoringAttachmentAlertDetailVOs, agentSalesMonitoringAttachmentAlertDetailVOs, agentCBMonitoringAttachmentAlertDetailVOs, agentRFMonitoringAttachmentAlertDetailVOs, agentFraudMonitoringAttachmentAlertDetailVOs, agentTechMonitoringAttachmentAlertDetailVOs, partnerSalesMonitoringAttachmentAlertDetailVOs, partnerCBMonitoringAttachmentAlertDetailVOs, partnerRFMonitoringAttachmentAlertDetailVOs, partnerFraudMonitoringAttachmentAlertDetailVOs, partnerTechMonitoringAttachmentAlertDetailVOs, monitoringParameterMappingVO);
                                    setMailIntoAppropriateMapGroup2(terminalId, adminSalesMonitoringAttachmentAlertDetailVOs, adminCBMonitoringAttachmentAlertDetailVOs, adminRFMonitoringAttachmentAlertDetailVOs, adminFraudMonitoringAttachmentAlertDetailVOs, adminTechMonitoringAttachmentAlertDetailVOs, merchantSalesMonitoringAttachmentAlertDetailVOs, merchantCBMonitoringAttachmentAlertDetailVOs, merchantRFMonitoringAttachmentAlertDetailVOs, merchantFraudMonitoringAttachmentAlertDetailVOs, merchantTechMonitoringAttachmentAlertDetailVOs, agentSalesMonitoringAttachmentAlertDetailVOs, agentCBMonitoringAttachmentAlertDetailVOs, agentRFMonitoringAttachmentAlertDetailVOs, agentFraudMonitoringAttachmentAlertDetailVOs, agentTechMonitoringAttachmentAlertDetailVOs, partnerSalesMonitoringAttachmentAlertDetailVOs, partnerCBMonitoringAttachmentAlertDetailVOs, partnerRFMonitoringAttachmentAlertDetailVOs, partnerFraudMonitoringAttachmentAlertDetailVOs, partnerTechMonitoringAttachmentAlertDetailVOs, adminSalesStringAttachmentListHashMapPerTerminal, adminCBStringAttachmentListHashMapPerTerminal, adminRFStringAttachmentListHashMapPerTerminal, adminFraudStringAttachmentListHashMapPerTerminal, adminTechStringAttachmentListHashMapPerTerminal, merchantSalesStringAttachmentListHashMapPerTerminal, merchantCBStringAttachmentListHashMapPerTerminal, merchantRFStringAttachmentListHashMapPerTerminal, merchantFraudStringAttachmentListHashMapPerTerminal, merchantTechStringAttachmentListHashMapPerTerminal, agentSalesStringAttachmentListHashMapPerTerminal, agentCBStringAttachmentListHashMapPerTerminal, agentRFStringAttachmentListHashMapPerTerminal, agentFraudStringAttachmentListHashMapPerTerminal, agentTechStringAttachmentListHashMapPerTerminal, partnerSalesStringAttachmentListHashMapPerTerminal, partnerCBStringAttachmentListHashMapPerTerminal, partnerRFStringAttachmentListHashMapPerTerminal, partnerFraudStringAttachmentListHashMapPerTerminal, partnerTechStringAttachmentListHashMapPerTerminal);
                                    logger.debug("adminSalesMonitoringAlertDetailVOs size==" + adminSalesMonitoringAlertDetailVOs.size());
                                    logger.debug("adminSalesStringAttachmentListHashMapPerTerminal size==" + adminSalesStringAttachmentListHashMapPerTerminal.size());
                                }
                            }
                        }
                        if ("Y".equals(monitoringParameterMappingVO.getSuspensionActivation()))
                        {
                            //MonitoringAlertDetailVO monitoringAlertDetailVO = null;
                            if (MonitoringKeyword.AuthSuccessful.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.StuckTransaction.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.BlockedCountry.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.BlockedIP.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.BlockedEmail.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.BlockedCard.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.BlockedName.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.MatureChargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.MatureRefund.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.HighAmountOrder.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.SameCardSameAmount.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.SameCardSameAmountConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || (MonitoringKeyword.Chargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())))
                            {
                                monitoringAlertDetailVO = calculateRandomRiskRule(monitoringParameterMappingVO, terminalVO, todaysInCompleteTransactionVOsList, todaysCBTransactionVOsList, todaysRFTransactionVOsList,binAmountVOList, dateVO,"daily");
                            }
                            else
                            {
                                monitoringAlertDetailVO = suspensionThresholdAnalysisDaily(monitoringParameterMappingVO, currentDayProcessingDetails, priorMonthProcessingDetailsVO, terminalVO, lastThreeMonthProcessingDetailsVO, terminalLimitsVO, currentMonthProcessingDetails);
                            }

                            if (monitoringAlertDetailVO != null)
                            {
                                isRuleVoilate=true;
                                monitoringAlertDetailVO.setMonitoringAlertPeriod(simpleDateFormat.format(startDate));
                                logger.debug("startDate:::::::::::::::::::::::::::::::::::::::::::::::::" +startDate);
                                monitoringAlertDetailVO.setAlertThreshold(monitoringParameterMappingVO.getAlertThreshold());
                                monitoringAlertDetailVO.setSuspensionThreshold(monitoringParameterMappingVO.getSuspensionThreshold());
                                logger.debug("Suspension Instance");
                                setMailIntoAppropriateListGroup1(monitoringAlertDetailVO, adminSalesMonitoringAlertDetailVOsSuspension, adminCBMonitoringAlertDetailVOsSuspension, adminRFMonitoringAlertDetailVOsSuspension, adminFraudMonitoringAlertDetailVOsSuspension, adminTechMonitoringAlertDetailVOsSuspension, agentSalesMonitoringAlertDetailVOsSuspension, agentCBMonitoringAlertDetailVOsSuspension, agentRFMonitoringAlertDetailVOsSuspension, agentFraudMonitoringAlertDetailVOsSuspension, agentTechMonitoringAlertDetailVOsSuspension, merchantCBMonitoringAlertDetailVOsSuspension, merchantRFMonitoringAlertDetailVOsSuspension, merchantSalesMonitoringAlertDetailVOsSuspension, merchantFraudMonitoringAlertDetailVOsSuspension, merchantTechnicalMonitoringAlertDetailVOsSuspension, partnerCBMonitoringAlertDetailVOsSuspension, partnerRFMonitoringAlertDetailVOsSuspension, partnerSalesMonitoringAlertDetailVOsSuspension, partnerFraudMonitoringAlertDetailVOsSuspension, partnerTechMonitoringAlertDetailVOsSuspension, monitoringParameterMappingVO);
                                setMailIntoAppropriateMapGroup1(terminalId, adminSalesMonitoringAlertDetailVOsSuspension, adminCBMonitoringAlertDetailVOsSuspension, adminRFMonitoringAlertDetailVOsSuspension, adminFraudMonitoringAlertDetailVOsSuspension, adminTechMonitoringAlertDetailVOsSuspension, agentSalesMonitoringAlertDetailVOsSuspension, agentCBMonitoringAlertDetailVOsSuspension, agentRFMonitoringAlertDetailVOsSuspension, agentFraudMonitoringAlertDetailVOsSuspension, agentTechMonitoringAlertDetailVOsSuspension, merchantCBMonitoringAlertDetailVOsSuspension, merchantRFMonitoringAlertDetailVOsSuspension, merchantSalesMonitoringAlertDetailVOsSuspension, merchantFraudMonitoringAlertDetailVOsSuspension, merchantTechnicalMonitoringAlertDetailVOsSuspension, partnerCBMonitoringAlertDetailVOsSuspension, partnerRFMonitoringAlertDetailVOsSuspension, partnerSalesMonitoringAlertDetailVOsSuspension, partnerFraudMonitoringAlertDetailVOsSuspension, partnerTechMonitoringAlertDetailVOsSuspension, adminSalesListHashMapPerTerminalSuspension, adminCBListHashMapPerTerminalSuspension, adminRFListHashMapPerTerminalSuspension, adminFraudListHashMapPerTerminalSuspension, adminTechListHashMapPerTerminalSuspension, agentSalesStringListHashMapPerTerminalSuspension, agentCBStringListHashMapPerTerminalSuspension, agentRFStringListHashMapPerTerminalSuspension, agentFraudStringListHashMapPerTerminalSuspension, agentTechStringListHashMapPerTerminalSuspension, merchantSalesListHashMapPerTerminalSuspension, merchantCBListHashMapPerTerminalSuspension, merchantRFListHashMapPerTerminalSuspension, merchantFraudListHashMapPerTerminalSuspension, merchantTechListHashMapPerTerminalSuspension, partnerCBListHashMapPerTerminalSuspension, partnerRFListHashMapPerTerminalSuspension, partnerSalesListHashMapPerTerminalSuspension, partnerFraudListHashMapPerTerminalSuspension, partnerTechnicalListHashMapTerminalSuspension);
                            }
                        }
                    }
                }//Monitoring Terminal Loop End
                if(isRuleVoilate)
                {

                    //Merchant mail
                    logger.debug("merchantSalesStringAttachmentListHashMapPerTerminal size()===" + merchantSalesStringAttachmentListHashMapPerTerminal.size());
                    logger.debug("merchantCBStringAttachmentListHashMapPerTerminal size()===" + merchantCBStringAttachmentListHashMapPerTerminal.size());
                    logger.debug("merchantRFStringAttachmentListHashMapPerTerminal size()===" + merchantRFStringAttachmentListHashMapPerTerminal.size());
                    logger.debug("merchantFraudStringAttachmentListHashMapPerTerminal size()===" + merchantFraudStringAttachmentListHashMapPerTerminal.size());
                    logger.debug("merchantTechStringAttachmentListHashMapPerTerminal size()===" + merchantTechStringAttachmentListHashMapPerTerminal.size());

                    sendMailToMerchantSalesTeam(merchantSalesStringListHashMap, merchantDetailsVO, merchantSalesListHashMapPerTerminalSuspension, merchantSalesStringAttachmentListHashMapPerTerminal,MonitoringFrequency.Daily.toString());
                    sendMailToMerchantChargebackTeam(merchantCBStringListHashMap, merchantDetailsVO, merchantCBListHashMapPerTerminalSuspension, merchantCBStringAttachmentListHashMapPerTerminal,MonitoringFrequency.Daily.toString());
                    sendMailToMerchantRefundTeam(merchantRFStringListHashMap, merchantDetailsVO, merchantRFListHashMapPerTerminalSuspension, merchantRFStringAttachmentListHashMapPerTerminal,MonitoringFrequency.Daily.toString());
                    sendMailToMerchantFraudTeam(merchantFraudStringListHashMap, merchantDetailsVO, merchantFraudListHashMapPerTerminalSuspension, merchantFraudStringAttachmentListHashMapPerTerminal,MonitoringFrequency.Daily.toString());
                    sendMailToMerchantTechnicalTeam(merchantTechnicalStringListHashMap, merchantDetailsVO, merchantTechListHashMapPerTerminalSuspension, merchantTechStringAttachmentListHashMapPerTerminal,MonitoringFrequency.Daily.toString());

                /*Admin Type-1,2,3 Group Setting*/
                    fillConsolidateAdminAllTypeMailMap(memberId, adminSalesStringListHashMap, adminCBStringListHashMap, adminRFStringListHashMap, adminFraudStringListHashMap, adminTechStringListHashMap, adminSalesStringListHashMapConsolidate, adminCBStringListHashMapConsolidate, adminRFStringListHashMapConsolidate, adminFraudStringListHashMapConsolidate, adminTechStringListHashMapConsolidate);
                    fillConsolidateAdminAllTypeMailMap(memberId, adminSalesListHashMapPerTerminalSuspension, adminCBListHashMapPerTerminalSuspension, adminRFListHashMapPerTerminalSuspension, adminFraudListHashMapPerTerminalSuspension, adminTechListHashMapPerTerminalSuspension, adminSalesStringListHashMapConsolidateSuspension, adminCBStringListHashMapConsolidateSuspension, adminRFStringListHashMapConsolidateSuspension, adminFraudStringListHashMapConsolidateSuspension, adminTechStringListHashMapConsolidateSuspension);
                    fillConsolidateAdminAllTypeMailMap(memberId, adminSalesStringAttachmentListHashMapPerTerminal, adminCBStringAttachmentListHashMapPerTerminal, adminRFStringAttachmentListHashMapPerTerminal, adminFraudStringAttachmentListHashMapPerTerminal, adminTechStringAttachmentListHashMapPerTerminal, adminSalesStringAttachmentListHashMapConsolidate, adminCBStringAttachmentListHashMapConsolidate, adminRFStringAttachmentListHashMapConsolidate, adminFraudStringAttachmentListHashMapConsolidate, adminTechStringAttachmentListHashMapConsolidate);

                /*Agent Type-1,2,3 Group Setting*/
                    fillConsolidateAdminAllTypeMailMap(memberId, agentSalesStringListHashMap, agentCBStringListHashMap, agentRFStringListHashMap, agentFraudStringListHashMap, agentTechStringListHashMap, agentSalesStringListHashMapConsolidate, agentCBStringListHashMapConsolidate, agentRFStringListHashMapConsolidate, agentFraudStringListHashMapConsolidate, agentTechStringListHashMapConsolidate);
                    fillConsolidateAdminAllTypeMailMap(memberId, agentSalesStringListHashMapPerTerminalSuspension, agentCBStringListHashMapPerTerminalSuspension, agentRFStringListHashMapPerTerminalSuspension, agentFraudStringListHashMapPerTerminalSuspension, agentTechStringListHashMapPerTerminalSuspension, agentSalesStringListHashMapConsolidateSuspension, agentCBStringListHashMapConsolidateSuspension, agentRFStringListHashMapConsolidateSuspension, agentFraudStringListHashMapConsolidateSuspension, agentTechStringListHashMapConsolidateSuspension);
                    fillConsolidateAdminAllTypeMailMap(memberId, agentSalesStringAttachmentListHashMapPerTerminal, agentCBStringAttachmentListHashMapPerTerminal, agentRFStringAttachmentListHashMapPerTerminal, agentFraudStringAttachmentListHashMapPerTerminal, agentTechStringAttachmentListHashMapPerTerminal, agentSalesStringAttachmentListHashMapConsolidate, agentCBStringAttachmentListHashMapConsolidate, agentRFStringAttachmentListHashMapConsolidate, agentFraudStringAttachmentListHashMapConsolidate, agentTechStringAttachmentListHashMapConsolidate);

                /*Partner Type-1,2,3 Group Setting*/
                    fillConsolidateAdminAllTypeMailMap(memberId, partnerSalesStringListHashMap, partnerCBStringListHashMap, partnerRFStringListHashMap, partnerFraudStringListHashMap, partnerTechnicalStringListHashMap, partnerSalesStringListHashMapConsolidate, partnerCBStringListHashMapConsolidate, partnerRFStringListHashMapConsolidate, partnerFraudStringListHashMapConsolidate, partnerTechStringListHashMapConsolidate);
                    fillConsolidateAdminAllTypeMailMap(memberId, partnerSalesListHashMapPerTerminalSuspension, partnerCBListHashMapPerTerminalSuspension, partnerRFListHashMapPerTerminalSuspension, partnerFraudListHashMapPerTerminalSuspension, partnerTechnicalListHashMapTerminalSuspension, partnerSalesStringListHashMapConsolidateSuspension, partnerCBStringListHashMapConsolidateSuspension, partnerRFStringListHashMapConsolidateSuspension, partnerFraudStringListHashMapConsolidateSuspension, partnerTechStringListHashMapConsolidateSuspension);
                    fillConsolidateAdminAllTypeMailMap(memberId, partnerSalesStringAttachmentListHashMapPerTerminal, partnerCBStringAttachmentListHashMapPerTerminal, partnerRFStringAttachmentListHashMapPerTerminal, partnerFraudStringAttachmentListHashMapPerTerminal, partnerTechStringAttachmentListHashMapPerTerminal, partnerSalesStringAttachmentListHashMapConsolidate, partnerCBStringAttachmentListHashMapConsolidate, partnerRFStringAttachmentListHashMapConsolidate, partnerFraudStringAttachmentListHashMapConsolidate, partnerTechStringAttachmentListHashMapConsolidate);

                    fillConsolidatePerPartnerMailMap(partnerId,partnerSalesStringListHashMapConsolidatePerPartner,partnerCBStringListHashMapConsolidatePerPartner,partnerRFStringListHashMapConsolidatePerPartner,partnerFraudStringListHashMapConsolidatePerPartner,partnerTechStringListHashMapConsolidatePerPartner,partnerSalesStringListHashMapConsolidate,partnerCBStringListHashMapConsolidate,partnerRFStringListHashMapConsolidate,partnerFraudStringListHashMapConsolidate,partnerTechStringListHashMapConsolidate);
                    fillConsolidatePerPartnerMailMap(partnerId,partnerSalesStringListHashMapConsolidateSuspensionPerPartner,partnerCBStringListHashMapConsolidateSuspensionPerPartner,partnerRFStringListHashMapConsolidateSuspensionPerPartner,partnerFraudStringListHashMapConsolidateSuspensionPerPartner,partnerTechStringListHashMapConsolidateSuspensionPerPartner, partnerSalesStringListHashMapConsolidateSuspension, partnerCBStringListHashMapConsolidateSuspension, partnerRFStringListHashMapConsolidateSuspension, partnerFraudStringListHashMapConsolidateSuspension, partnerTechStringListHashMapConsolidateSuspension);
                    fillConsolidatePerPartnerMailMap(partnerId,partnerSalesStringListHashMapConsolidateAttachmentPerPartner,partnerCBStringListHashMapConsolidateAttachmentPerPartner,partnerRFStringListHashMapConsolidateAttachmentPerPartner,partnerFraudStringListHashMapConsolidateAttachmentPerPartner,partnerTechStringListHashMapConsolidateAttachmentPerPartner,partnerSalesStringAttachmentListHashMapConsolidate, partnerCBStringAttachmentListHashMapConsolidate, partnerRFStringAttachmentListHashMapConsolidate, partnerFraudStringAttachmentListHashMapConsolidate, partnerTechStringAttachmentListHashMapConsolidate);

                    for(AgentDetailsVO agentDetailsVO:agentDetailsVOs)
                    {
                        String agentId=agentDetailsVO.getAgentId();

                        fillConsolidatePerAgentMailMap(agentId,agentSalesStringListHashMapConsolidatePerAgent,agentCBStringListHashMapConsolidatePerAgent,agentRFStringListHashMapConsolidatePerAgent,agentFraudStringListHashMapConsolidatePerAgent,agentTechStringListHashMapConsolidatePerAgent,agentSalesStringListHashMapConsolidate, agentCBStringListHashMapConsolidate, agentRFStringListHashMapConsolidate, agentFraudStringListHashMapConsolidate, agentTechStringListHashMapConsolidate);
                        fillConsolidatePerAgentMailMap(agentId,agentSalesStringListHashMapConsolidateSuspensionPerAgent, agentCBStringListHashMapConsolidateSuspensionPerAgent, agentRFStringListHashMapConsolidateSuspensionPerAgent, agentFraudStringListHashMapConsolidateSuspensionPerAgent, agentTechStringListHashMapConsolidateSuspensionPerAgent, agentSalesStringListHashMapConsolidateSuspension, agentCBStringListHashMapConsolidateSuspension, agentRFStringListHashMapConsolidateSuspension, agentFraudStringListHashMapConsolidateSuspension, agentTechStringListHashMapConsolidateSuspension);
                        fillConsolidatePerAgentMailMap(agentId,agentSalesStringAttachmentListHashMapConsolidatePerAgent, agentCBStringAttachmentListHashMapConsolidatePerAgent, agentRFStringAttachmentListHashMapConsolidatePerAgent, agentFraudStringAttachmentListHashMapConsolidatePerAgent, agentTechStringAttachmentListHashMapConsolidatePerAgent, agentSalesStringAttachmentListHashMapConsolidate, agentCBStringAttachmentListHashMapConsolidate, agentRFStringAttachmentListHashMapConsolidate, agentFraudStringAttachmentListHashMapConsolidate, agentTechStringAttachmentListHashMapConsolidate);

                        agentList.add(agentId);
                    }
                }

            }//Monitoring Merchant Loop End

            /*System.out.println("adminSalesStringAttachmentListHashMapConsolidate size()=====" + adminSalesStringAttachmentListHashMapConsolidate.size());
            System.out.println("adminCBStringAttachmentListHashMapConsolidate size()=====" + adminCBStringAttachmentListHashMapConsolidate.size());
            System.out.println("adminRFStringAttachmentListHashMapConsolidate size()=====" + adminRFStringAttachmentListHashMapConsolidate.size());
            System.out.println("adminFraudStringAttachmentListHashMapConsolidate size()=====" + adminFraudStringAttachmentListHashMapConsolidate.size());
            System.out.println("adminTechStringAttachmentListHashMapConsolidate size()=====" + adminTechStringAttachmentListHashMapConsolidate.size());

            System.out.println("partnerSalesStringAttachmentListHashMapConsolidate size()=====" + partnerSalesStringListHashMapConsolidate.size());
            System.out.println("partnerCBStringAttachmentListHashMapConsolidate size()=====" + partnerCBStringListHashMapConsolidate.size());
            System.out.println("partnerRFStringAttachmentListHashMapConsolidate size()=====" + partnerRFStringListHashMapConsolidate.size());
            System.out.println("partnerFraudStringAttachmentListHashMapConsolidate size()=====" + partnerFraudStringListHashMapConsolidate.size());
            System.out.println("partnerTechStringAttachmentListHashMapConsolidate size()=====" + partnerTechStringListHashMapConsolidate.size());

            System.out.println("agentSalesStringAttachmentListHashMapConsolidate size()=====" + agentSalesStringAttachmentListHashMapConsolidate.size());
            System.out.println("agentCBStringAttachmentListHashMapConsolidate size()=====" + agentCBStringAttachmentListHashMapConsolidate.size());
            System.out.println("agentRFStringAttachmentListHashMapConsolidate size()=====" + agentRFStringAttachmentListHashMapConsolidate.size());
            System.out.println("agentFraudStringAttachmentListHashMapConsolidate size()=====" + agentFraudStringAttachmentListHashMapConsolidate.size());
            System.out.println("agentTechStringAttachmentListHashMapConsolidate size()=====" + agentTechStringAttachmentListHashMapConsolidate.size());*/


            sendMailToAdminConsolidate(adminSalesStringListHashMapConsolidate, adminSalesStringListHashMapConsolidateSuspension, adminSalesStringAttachmentListHashMapConsolidate);
            sendMailToAdminCBConsolidate(adminCBStringListHashMapConsolidate, adminCBStringListHashMapConsolidateSuspension, adminCBStringAttachmentListHashMapConsolidate);
            sendMailToAdminRFConsolidate(adminRFStringListHashMapConsolidate, adminRFStringListHashMapConsolidateSuspension, adminRFStringAttachmentListHashMapConsolidate);
            sendMailToAdminFraudConsolidate(adminFraudStringListHashMapConsolidate, adminFraudStringListHashMapConsolidateSuspension, adminFraudStringAttachmentListHashMapConsolidate);
            sendMailToAdminTechConsolidate(adminTechStringListHashMapConsolidate, adminTechStringListHashMapConsolidateSuspension, adminTechStringAttachmentListHashMapConsolidate);


            //Mail Should be for multiple partners
            for(String partnerId:partnersList)
            {
               // System.out.println("Mail Should be for multiple partners:::"+partnerId);
                sendMailToPartnerSalesConsolidate(partnerId, partnerSalesStringListHashMapConsolidatePerPartner.get(partnerId), partnerSalesStringListHashMapConsolidateSuspensionPerPartner.get(partnerId), partnerSalesStringListHashMapConsolidateAttachmentPerPartner.get(partnerId));
                sendMailToPartnerFraudConsolidate(partnerId,partnerFraudStringListHashMapConsolidatePerPartner.get(partnerId), partnerFraudStringListHashMapConsolidateSuspensionPerPartner.get(partnerId), partnerFraudStringListHashMapConsolidateAttachmentPerPartner.get(partnerId));
                sendMailToPartnerCBConsolidate(partnerId,partnerCBStringListHashMapConsolidatePerPartner.get(partnerId), partnerCBStringListHashMapConsolidateSuspensionPerPartner.get(partnerId), partnerCBStringListHashMapConsolidateAttachmentPerPartner.get(partnerId));
                sendMailToPartnerRFConsolidate(partnerId,partnerRFStringListHashMapConsolidatePerPartner.get(partnerId), partnerRFStringListHashMapConsolidateSuspensionPerPartner.get(partnerId), partnerRFStringListHashMapConsolidateAttachmentPerPartner.get(partnerId));
                sendMailToPartnerTechnicalConsolidate(partnerId,partnerTechStringListHashMapConsolidatePerPartner.get(partnerId), partnerTechStringListHashMapConsolidateSuspensionPerPartner.get(partnerId), partnerTechStringListHashMapConsolidateAttachmentPerPartner.get(partnerId));
            }
            for(String agentId:agentList)
            {
                //Mail Should be for multiple agents
                sendMailToAgentSalesConsolidate(agentId,agentSalesStringListHashMapConsolidatePerAgent.get(agentId), agentSalesStringListHashMapConsolidateSuspensionPerAgent.get(agentId), agentSalesStringAttachmentListHashMapConsolidatePerAgent.get(agentId));
                sendMailToAgentCBConsolidate(agentId,agentCBStringListHashMapConsolidatePerAgent.get(agentId), agentCBStringListHashMapConsolidateSuspensionPerAgent.get(agentId), agentCBStringAttachmentListHashMapConsolidatePerAgent.get(agentId));
                sendMailToAgentRFConsolidate(agentId,agentRFStringListHashMapConsolidatePerAgent.get(agentId), agentRFStringListHashMapConsolidateSuspensionPerAgent.get(agentId), agentRFStringAttachmentListHashMapConsolidatePerAgent.get(agentId));
                sendMailToAgentFraudConsolidate(agentId,agentFraudStringListHashMapConsolidatePerAgent.get(agentId), agentFraudStringListHashMapConsolidateSuspensionPerAgent.get(agentId), agentFraudStringAttachmentListHashMapConsolidatePerAgent.get(agentId));
                sendMailToAgentTechConsolidate(agentId,agentTechStringListHashMapConsolidatePerAgent.get(agentId), agentTechStringListHashMapConsolidateSuspensionPerAgent.get(agentId), agentTechStringAttachmentListHashMapConsolidatePerAgent.get(agentId));

            }
        }
        else
        {
            logger.debug("No Terminals Founds For Implementation[Daily]");
        }
    }

    public void weeklyMerchantMonitoringRiskAlerts() throws Exception
    {
        MerchantMonitoringManager merchantMonitoringManager = new MerchantMonitoringManager();
        Set<String> partnersList=new TreeSet();
        Set<String> agentList=new TreeSet();
        AgentDAO agentDAO=new AgentDAO();
        Map<String, Map<String, List<MonitoringParameterMappingVO>>> stringListMap = merchantMonitoringManager.getMonitoringParameterGroupByMerchantWeeklyExecution();
        if (stringListMap.size() > 0)
        {
            /* Admin Type-1 Consolidated Email */
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminSalesStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminCBStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminRFStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminTechStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminFraudStringListHashMapConsolidate = new HashMap();

            /* Admin Type-2 Consolidated Email */
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminSalesStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminCBStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminRFStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminFraudStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminTechStringAttachmentListHashMapConsolidate = new HashMap();

            /* Admin Type-3 Consolidated Email */
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminSalesStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminCBStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminRFStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminTechStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminFraudStringListHashMapConsolidateSuspension = new HashMap();

            /* Agent Type-1 Consolidated Email */
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentSalesStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentCBStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentRFStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentFraudStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentTechStringListHashMapConsolidate = new HashMap();

            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentSalesStringListHashMapConsolidatePerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentCBStringListHashMapConsolidatePerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentRFStringListHashMapConsolidatePerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentFraudStringListHashMapConsolidatePerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentTechStringListHashMapConsolidatePerAgent = new HashMap();

            /* Agent Type-2 Consolidated Email */
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentSalesStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentCBStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentRFStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentFraudStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentTechStringAttachmentListHashMapConsolidate = new HashMap();

            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentSalesStringAttachmentListHashMapConsolidatePerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentCBStringAttachmentListHashMapConsolidatePerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentRFStringAttachmentListHashMapConsolidatePerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentFraudStringAttachmentListHashMapConsolidatePerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentTechStringAttachmentListHashMapConsolidatePerAgent = new HashMap();


            /*Agent Type-3 Consolidated Email */
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentSalesStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentCBStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentRFStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentFraudStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentTechStringListHashMapConsolidateSuspension = new HashMap();

            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentSalesStringListHashMapConsolidateSuspensionPerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentCBStringListHashMapConsolidateSuspensionPerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentRFStringListHashMapConsolidateSuspensionPerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentFraudStringListHashMapConsolidateSuspensionPerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentTechStringListHashMapConsolidateSuspensionPerAgent = new HashMap();


            /*Partner Type-1 Consolidated Email-Per Merchant */
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerSalesStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerFraudStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerCBStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerRFStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerTechStringListHashMapConsolidate = new HashMap();

            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerSalesStringListHashMapConsolidatePerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerFraudStringListHashMapConsolidatePerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerCBStringListHashMapConsolidatePerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerRFStringListHashMapConsolidatePerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerTechStringListHashMapConsolidatePerPartner = new HashMap();

            /*Partner Type-2 Consolidated Email */
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerSalesStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerFraudStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerCBStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerRFStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerTechStringListHashMapConsolidateSuspension = new HashMap();

            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerSalesStringListHashMapConsolidateSuspensionPerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerFraudStringListHashMapConsolidateSuspensionPerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerCBStringListHashMapConsolidateSuspensionPerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerRFStringListHashMapConsolidateSuspensionPerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerTechStringListHashMapConsolidateSuspensionPerPartner = new HashMap();

            /*Partner Type-3 Consolidated Email */
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerSalesStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerCBStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerRFStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerFraudStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerTechStringAttachmentListHashMapConsolidate = new HashMap();

            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerSalesStringListHashMapConsolidateAttachmentPerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerFraudStringListHashMapConsolidateAttachmentPerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerCBStringListHashMapConsolidateAttachmentPerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerRFStringListHashMapConsolidateAttachmentPerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerTechStringListHashMapConsolidateAttachmentPerPartner = new HashMap();

            Set set = stringListMap.keySet();
            Iterator iterator = set.iterator();
            TerminalManager terminalManager = new TerminalManager();
            while (iterator.hasNext())
            {
                Map<String, List<MonitoringAlertDetailVO>> adminSalesStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminCBStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminRFStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminTechStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminFraudStringListHashMap = new HashMap();

                Map<String, List<MonitoringAlertDetailVO>> adminSalesStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminCBStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminRFStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminFraudStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminTechStringAttachmentListHashMapPerTerminal = new HashMap();

                Map<String, List<MonitoringAlertDetailVO>> adminSalesListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminCBListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminRFListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminFraudListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminTechListHashMapPerTerminalSuspension = new HashMap();

                /* Merchant Type-1 Consolidated Email */
                Map<String, List<MonitoringAlertDetailVO>> merchantSalesStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantCBStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantRFStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantFraudStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantTechnicalStringListHashMap = new HashMap();

                /* Merchant Type-2 Consolidated Email */
                Map<String, List<MonitoringAlertDetailVO>> merchantSalesStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantCBStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantRFStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantFraudStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantTechStringAttachmentListHashMapPerTerminal = new HashMap();

                /* Merchant Type-3 Consolidated Email */
                Map<String, List<MonitoringAlertDetailVO>> merchantSalesListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantCBListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantRFListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantFraudListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantTechListHashMapPerTerminalSuspension = new HashMap();

                Map<String, List<MonitoringAlertDetailVO>> partnerCBStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerRFStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerSalesStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerFraudStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerTechnicalStringListHashMap = new HashMap();

                Map<String, List<MonitoringAlertDetailVO>> partnerSalesStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerCBStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerRFStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerFraudStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerTechStringAttachmentListHashMapPerTerminal = new HashMap();

                Map<String, List<MonitoringAlertDetailVO>> partnerCBListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerRFListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerSalesListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerFraudListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerTechnicalListHashMapTerminalSuspension = new HashMap();

                Map<String, List<MonitoringAlertDetailVO>> agentSalesStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentCBStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentRFStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentFraudStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentTechStringListHashMap = new HashMap();

                Map<String, List<MonitoringAlertDetailVO>> agentSalesStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentCBStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentRFStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentFraudStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentTechStringAttachmentListHashMapPerTerminal = new HashMap();

                Map<String, List<MonitoringAlertDetailVO>> agentSalesStringListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentCBStringListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentRFStringListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentFraudStringListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentTechStringListHashMapPerTerminalSuspension = new HashMap();

                boolean isRuleVoilate=false;
                String memberId = (String) iterator.next();


                MerchantDAO merchantDAO = new MerchantDAO();
                MerchantDetailsVO merchantDetailsVO = merchantDAO.getMemberDetails(memberId);
                String partnerId=merchantDetailsVO.getPartnerId();
                partnersList.add(partnerId);
                List<AgentDetailsVO> agentDetailsVOs=agentDAO.getMerchantAgents(memberId);

                Map<String, List<MonitoringParameterMappingVO>> stringListMapTerminal = stringListMap.get(memberId);
                Set terminalSet = stringListMapTerminal.keySet();
                Iterator terminalIterator = terminalSet.iterator();
                while (terminalIterator.hasNext())
                {
                    String terminalId = (String) terminalIterator.next();
                    TerminalVO terminalVO = terminalManager.getMemberTerminalWithActivationDetails(memberId, terminalId);
                    List<MonitoringParameterMappingVO> monitoringParameterMappingVOs = stringListMapTerminal.get(terminalId);

                    String terminalActivationDate = terminalVO.getActivationDate();
                    String firstTransactionDate = merchantMonitoringManager.getMemberFirstSubmission(terminalVO);
                    String lastTransactionDate = merchantMonitoringManager.getMemberLastSubmission(terminalVO);

                    if (!functions.isValueNull(terminalVO.getActivationDate()))
                    {
                        terminalActivationDate = firstTransactionDate;
                    }

                    if(!functions.isValueNull(terminalActivationDate))
                    {
                        logger.debug("Terminal activation date is not found so we can not process risk rules on===" + terminalId);
                        continue;
                    }

                    DateManager dateManager = new DateManager();
                    DateVO dateVO = dateManager.getPreviousWeekDateRange();

                    logger.debug("current week start date===" + dateVO.getStartDate());
                    logger.debug("current week end date===" + dateVO.getEndDate());

                    TerminalProcessingDetailsVO priorMonthProcessingDetailsVO = merchantMonitoringManager.getLastMonthProcessingDetailsAsPerCalendarMonth(terminalVO);
                    //TerminalProcessingDetailsVO currentDayProcessingDetails = merchantMonitoringManager.getCurrentWeekProcessingDetailsByDTStamp(terminalVO);
                    TerminalProcessingDetailsVO lastThreeMonthProcessingDetailsVO = merchantMonitoringManager.getLastThreeMonthProcessingDetailsAsPerCalendarMonth(terminalVO);
                    TerminalProcessingDetailsVO currentMonthProcessingDetails = merchantMonitoringManager.getCurrentMonthProcessingDetailsAsPerCalendarMonthByDTStamp(terminalVO);
                    //TerminalProcessingDetailsVO currentWeekProcessingDetailsVO = merchantMonitoringManager.getCurrentWeekProcessingDetailsByDTStamp(terminalVO);
                    TerminalProcessingDetailsVO currentWeekProcessingDetailsVO = merchantMonitoringManager.getPreviousWeekProcessingDetailsByDTStamp(terminalVO);

                    List<TransactionVO> currentWeekCBTransactionVOsList = merchantMonitoringManager.getTransactionListByTimestamp(terminalVO, dateVO, "chargeback");
                    List<TransactionVO> currentWeekRFTransactionVOsList = merchantMonitoringManager.getTransactionListByTimestamp(terminalVO, dateVO, "reversed");
                    List<TransactionVO> currentWeekInCompleteTransactionVOsList = merchantMonitoringManager.getInCompleteTransactionListByTimestamp(terminalVO, dateVO);
                    //List<TransactionVO> currentWeekTransactionVOsList = merchantMonitoringManager.getTransactionListByDTStamp(terminalVO, dateVO);

                    List<BinAmountVO> binAmountVOList = merchantMonitoringManager.getSameCardSameAmountDetail(terminalVO, dateVO);
                    TerminalLimitsVO terminalLimitsVO = terminalManager.getMemberTerminalProcessingLimitVO(memberId, terminalId);

                    currentWeekProcessingDetailsVO.setFirstTransactionDate(firstTransactionDate);
                    currentWeekProcessingDetailsVO.setLastTransactionDate(lastTransactionDate);

                    terminalVO.setActivationDate(terminalActivationDate);

                    /*Admin Type-1 List*/
                    List<MonitoringAlertDetailVO> adminSalesMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> adminCBMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> adminRFMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> adminFraudMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> adminTechMonitoringAlertDetailVOs = new ArrayList();

                    /*Admin Type-2 List*/
                    List<MonitoringAlertDetailVO> adminSalesMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> adminCBMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> adminRFMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> adminFraudMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> adminTechMonitoringAttachmentAlertDetailVOs = new ArrayList();

                    /*Admin Type-3 List*/
                    List<MonitoringAlertDetailVO> adminSalesMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> adminCBMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> adminRFMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> adminFraudMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> adminTechMonitoringAlertDetailVOsSuspension = new ArrayList();

                    /*Agent Type-1 List*/
                    List<MonitoringAlertDetailVO> agentSalesMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> agentCBsMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> agentRFsMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> agentFraudsMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> agentTechsMonitoringAlertDetailVOs = new ArrayList();

                    /*Agent Type-2 List*/
                    List<MonitoringAlertDetailVO> agentSalesMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> agentCBMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> agentRFMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> agentFraudMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> agentTechMonitoringAttachmentAlertDetailVOs = new ArrayList();

                    /*Admin Type-2 List*/
                    List<MonitoringAlertDetailVO> agentSalesMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> agentCBMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> agentRFMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> agentFraudMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> agentTechMonitoringAlertDetailVOsSuspension = new ArrayList();

                    /*Merchant Type-1 List*/
                    List<MonitoringAlertDetailVO> merchantCBMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantRFMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantSalesMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantFraudMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantTechnicalMonitoringAlertDetailVOs = new ArrayList();

                    /*Merchant Type-2 List*/
                    List<MonitoringAlertDetailVO> merchantSalesMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantCBMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantRFMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantFraudMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantTechMonitoringAttachmentAlertDetailVOs = new ArrayList();

                    /*Merchant Type-3 List*/
                    List<MonitoringAlertDetailVO> merchantCBMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantRFMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantSalesMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantFraudMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantTechnicalMonitoringAlertDetailVOsSuspension = new ArrayList();

                    /*Partner Type-1 List*/
                    List<MonitoringAlertDetailVO> partnerCBMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerRFMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerSalesMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerFraudMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerTechnicalMonitoringAlertDetailVOs = new ArrayList();

                    /*Partner Type-2 List*/
                    List<MonitoringAlertDetailVO> partnerSalesMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerCBMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerRFMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerFraudMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerTechMonitoringAttachmentAlertDetailVOs = new ArrayList();

                    /*Partner Type-3 List*/
                    List<MonitoringAlertDetailVO> partnerCBMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerRFMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerSalesMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerFraudMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerTechMonitoringAlertDetailVOsSuspension = new ArrayList();

                    for (MonitoringParameterMappingVO monitoringParameterMappingVO : monitoringParameterMappingVOs)
                    {
                        //MonitoringAlertDetailVO monitoringAlertDetailVO = thresholdAnalysisWeekly(monitoringParameterMappingVO, currentWeekProcessingDetailsVO, terminalLimitsVO, lastThreeMonthProcessingDetailsVO);
                        MonitoringParameterVO monitoringParameterVO=monitoringParameterMappingVO.getMonitoringParameterVO();
                        logger.debug("Risk Rule Name===" + monitoringParameterVO.getMonitoringParameterName());
                        MonitoringAlertDetailVO monitoringAlertDetailVO = null;
                        if ("Y".equals(monitoringParameterMappingVO.getAlertActivation()))
                        {
                            //MonitoringAlertDetailVO monitoringAlertDetailVO = null;
                            if (MonitoringKeyword.AuthSuccessful.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.StuckTransaction.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.BlockedCountry.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.BlockedIP.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.BlockedEmail.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.BlockedCard.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.BlockedName.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.MatureChargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.MatureRefund.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.HighAmountOrder.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.SameCardSameAmount.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.SameCardSameAmountConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || (MonitoringKeyword.Chargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())))
                            {
                                logger.debug("inside condition");
                                monitoringAlertDetailVO = calculateAttachment(monitoringParameterMappingVO, terminalVO, currentWeekInCompleteTransactionVOsList, currentWeekCBTransactionVOsList, currentWeekRFTransactionVOsList,binAmountVOList, dateVO,"weekly");
                                logger.debug("monitoringAlertDetailVO==" + monitoringAlertDetailVO);
                            }
                            else
                            {
                                //System.out.println("inside ratio");
                                monitoringAlertDetailVO = alertOnlyThresholdWeekly(monitoringParameterMappingVO, currentWeekProcessingDetailsVO, terminalLimitsVO, lastThreeMonthProcessingDetailsVO,priorMonthProcessingDetailsVO,currentMonthProcessingDetails,terminalVO);
                                //System.out.println("monitoringAlertDetailVO==="+monitoringAlertDetailVO);
                            }

                            if (monitoringAlertDetailVO != null)
                            {
                                isRuleVoilate=true;

                                String weeklyPeriod=dateManager.getLastWeeklyMonitorPeriod();
                                if (monitoringAlertDetailVO instanceof MonitoringOnlyAlertDetailsVO)
                                {
                                    monitoringAlertDetailVO.setMonitoringAlertPeriod(weeklyPeriod);
                                    monitoringAlertDetailVO.setAlertThreshold(monitoringParameterMappingVO.getWeeklyAlertThreshold());
                                    monitoringAlertDetailVO.setSuspensionThreshold(monitoringParameterMappingVO.getWeeklySuspensionThreshold());
                                    //System.out.println("Onlylert Instance");
                                    setMailIntoAppropriateListGroup(monitoringAlertDetailVO, adminSalesMonitoringAlertDetailVOs, adminCBMonitoringAlertDetailVOs, adminRFMonitoringAlertDetailVOs, adminFraudMonitoringAlertDetailVOs, adminTechMonitoringAlertDetailVOs, agentSalesMonitoringAlertDetailVOs, agentCBsMonitoringAlertDetailVOs, agentRFsMonitoringAlertDetailVOs, agentFraudsMonitoringAlertDetailVOs, agentTechsMonitoringAlertDetailVOs, merchantCBMonitoringAlertDetailVOs, merchantRFMonitoringAlertDetailVOs, merchantSalesMonitoringAlertDetailVOs, merchantFraudMonitoringAlertDetailVOs, merchantTechnicalMonitoringAlertDetailVOs, partnerCBMonitoringAlertDetailVOs, partnerRFMonitoringAlertDetailVOs, partnerSalesMonitoringAlertDetailVOs, partnerFraudMonitoringAlertDetailVOs, partnerTechnicalMonitoringAlertDetailVOs, monitoringParameterMappingVO);
                                    setMailIntoAppropriateMapGroup(terminalId, adminSalesMonitoringAlertDetailVOs, adminCBMonitoringAlertDetailVOs, adminRFMonitoringAlertDetailVOs, adminFraudMonitoringAlertDetailVOs, adminTechMonitoringAlertDetailVOs, agentSalesMonitoringAlertDetailVOs, agentCBsMonitoringAlertDetailVOs, agentRFsMonitoringAlertDetailVOs, agentFraudsMonitoringAlertDetailVOs, agentTechsMonitoringAlertDetailVOs, merchantCBMonitoringAlertDetailVOs, merchantRFMonitoringAlertDetailVOs, merchantSalesMonitoringAlertDetailVOs, merchantFraudMonitoringAlertDetailVOs, merchantTechnicalMonitoringAlertDetailVOs, partnerCBMonitoringAlertDetailVOs, partnerRFMonitoringAlertDetailVOs, partnerSalesMonitoringAlertDetailVOs, partnerFraudMonitoringAlertDetailVOs, partnerTechnicalMonitoringAlertDetailVOs, adminSalesStringListHashMap, adminCBStringListHashMap, adminRFStringListHashMap, adminFraudStringListHashMap, adminTechStringListHashMap, agentSalesStringListHashMap, agentCBStringListHashMap, agentRFStringListHashMap, agentFraudStringListHashMap, agentTechStringListHashMap, merchantSalesStringListHashMap, merchantCBStringListHashMap, merchantRFStringListHashMap, merchantFraudStringListHashMap, merchantTechnicalStringListHashMap, partnerCBStringListHashMap, partnerRFStringListHashMap, partnerSalesStringListHashMap, partnerFraudStringListHashMap, partnerTechnicalStringListHashMap);
                                }
                                else if (monitoringAlertDetailVO instanceof MonitoringAttachmentAlertDetailsVO)
                                {
                                    monitoringAlertDetailVO.setMonitoringAlertPeriod(weeklyPeriod);
                                    monitoringAlertDetailVO.setAlertThreshold(monitoringParameterMappingVO.getWeeklyAlertThreshold());
                                    monitoringAlertDetailVO.setSuspensionThreshold(monitoringParameterMappingVO.getWeeklySuspensionThreshold());
                                    logger.debug("Alert Instance");
                                    setMailIntoAppropriateListGroup2(monitoringAlertDetailVO, adminSalesMonitoringAttachmentAlertDetailVOs, adminCBMonitoringAttachmentAlertDetailVOs, adminRFMonitoringAttachmentAlertDetailVOs, adminFraudMonitoringAttachmentAlertDetailVOs, adminTechMonitoringAttachmentAlertDetailVOs, merchantSalesMonitoringAttachmentAlertDetailVOs, merchantCBMonitoringAttachmentAlertDetailVOs, merchantRFMonitoringAttachmentAlertDetailVOs, merchantFraudMonitoringAttachmentAlertDetailVOs, merchantTechMonitoringAttachmentAlertDetailVOs, agentSalesMonitoringAttachmentAlertDetailVOs, agentCBMonitoringAttachmentAlertDetailVOs, agentRFMonitoringAttachmentAlertDetailVOs, agentFraudMonitoringAttachmentAlertDetailVOs, agentTechMonitoringAttachmentAlertDetailVOs, partnerSalesMonitoringAttachmentAlertDetailVOs, partnerCBMonitoringAttachmentAlertDetailVOs, partnerRFMonitoringAttachmentAlertDetailVOs, partnerFraudMonitoringAttachmentAlertDetailVOs, partnerTechMonitoringAttachmentAlertDetailVOs, monitoringParameterMappingVO);
                                    setMailIntoAppropriateMapGroup2(terminalId, adminSalesMonitoringAttachmentAlertDetailVOs, adminCBMonitoringAttachmentAlertDetailVOs, adminRFMonitoringAttachmentAlertDetailVOs, adminFraudMonitoringAttachmentAlertDetailVOs, adminTechMonitoringAttachmentAlertDetailVOs, merchantSalesMonitoringAttachmentAlertDetailVOs, merchantCBMonitoringAttachmentAlertDetailVOs, merchantRFMonitoringAttachmentAlertDetailVOs, merchantFraudMonitoringAttachmentAlertDetailVOs, merchantTechMonitoringAttachmentAlertDetailVOs, agentSalesMonitoringAttachmentAlertDetailVOs, agentCBMonitoringAttachmentAlertDetailVOs, agentRFMonitoringAttachmentAlertDetailVOs, agentFraudMonitoringAttachmentAlertDetailVOs, agentTechMonitoringAttachmentAlertDetailVOs, partnerSalesMonitoringAttachmentAlertDetailVOs, partnerCBMonitoringAttachmentAlertDetailVOs, partnerRFMonitoringAttachmentAlertDetailVOs, partnerFraudMonitoringAttachmentAlertDetailVOs, partnerTechMonitoringAttachmentAlertDetailVOs, adminSalesStringAttachmentListHashMapPerTerminal, adminCBStringAttachmentListHashMapPerTerminal, adminRFStringAttachmentListHashMapPerTerminal, adminFraudStringAttachmentListHashMapPerTerminal, adminTechStringAttachmentListHashMapPerTerminal, merchantSalesStringAttachmentListHashMapPerTerminal, merchantCBStringAttachmentListHashMapPerTerminal, merchantRFStringAttachmentListHashMapPerTerminal, merchantFraudStringAttachmentListHashMapPerTerminal, merchantTechStringAttachmentListHashMapPerTerminal, agentSalesStringAttachmentListHashMapPerTerminal, agentCBStringAttachmentListHashMapPerTerminal, agentRFStringAttachmentListHashMapPerTerminal, agentFraudStringAttachmentListHashMapPerTerminal, agentTechStringAttachmentListHashMapPerTerminal, partnerSalesStringAttachmentListHashMapPerTerminal, partnerCBStringAttachmentListHashMapPerTerminal, partnerRFStringAttachmentListHashMapPerTerminal, partnerFraudStringAttachmentListHashMapPerTerminal, partnerTechStringAttachmentListHashMapPerTerminal);
                                    logger.debug("adminSalesMonitoringAlertDetailVOs size==" + adminSalesMonitoringAlertDetailVOs.size());
                                    logger.debug("adminSalesStringAttachmentListHashMapPerTerminal size==" + adminSalesStringAttachmentListHashMapPerTerminal.size());
                                }

                            }

                        }
                        if ("Y".equals(monitoringParameterMappingVO.getSuspensionActivation()))
                        {
                            //MonitoringAlertDetailVO monitoringAlertDetailVO = null;
                            if (MonitoringKeyword.AuthSuccessful.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.StuckTransaction.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.BlockedCountry.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.BlockedIP.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.BlockedEmail.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.BlockedCard.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.BlockedName.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.MatureChargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.MatureRefund.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.HighAmountOrder.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.SameCardSameAmount.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.SameCardSameAmountConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || (MonitoringKeyword.Chargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())))
                            {
                                monitoringAlertDetailVO = calculateRandomRiskRule(monitoringParameterMappingVO, terminalVO, currentWeekInCompleteTransactionVOsList, currentWeekCBTransactionVOsList, currentWeekRFTransactionVOsList, binAmountVOList, dateVO, "weekly");
                            }
                            else
                            {
                                monitoringAlertDetailVO = suspensionThresholdWeekly(monitoringParameterMappingVO, currentWeekProcessingDetailsVO, terminalLimitsVO, lastThreeMonthProcessingDetailsVO, terminalVO, priorMonthProcessingDetailsVO, currentMonthProcessingDetails);
                            }
                            if (monitoringAlertDetailVO != null)
                            {
                                isRuleVoilate = true;
                                String weeklyPeriod = dateManager.getLastWeeklyMonitorPeriod();
                                monitoringAlertDetailVO.setMonitoringAlertPeriod(weeklyPeriod);
                                monitoringAlertDetailVO.setAlertThreshold(monitoringParameterMappingVO.getWeeklyAlertThreshold());
                                monitoringAlertDetailVO.setSuspensionThreshold(monitoringParameterMappingVO.getWeeklySuspensionThreshold());
                                logger.debug("Suspension Instance");
                                setMailIntoAppropriateListGroup1(monitoringAlertDetailVO, adminSalesMonitoringAlertDetailVOsSuspension, adminCBMonitoringAlertDetailVOsSuspension, adminRFMonitoringAlertDetailVOsSuspension, adminFraudMonitoringAlertDetailVOsSuspension, adminTechMonitoringAlertDetailVOsSuspension, agentSalesMonitoringAlertDetailVOsSuspension, agentCBMonitoringAlertDetailVOsSuspension, agentRFMonitoringAlertDetailVOsSuspension, agentFraudMonitoringAlertDetailVOsSuspension, agentTechMonitoringAlertDetailVOsSuspension, merchantCBMonitoringAlertDetailVOsSuspension, merchantRFMonitoringAlertDetailVOsSuspension, merchantSalesMonitoringAlertDetailVOsSuspension, merchantFraudMonitoringAlertDetailVOsSuspension, merchantTechnicalMonitoringAlertDetailVOsSuspension, partnerCBMonitoringAlertDetailVOsSuspension, partnerRFMonitoringAlertDetailVOsSuspension, partnerSalesMonitoringAlertDetailVOsSuspension, partnerFraudMonitoringAlertDetailVOsSuspension, partnerTechMonitoringAlertDetailVOsSuspension, monitoringParameterMappingVO);
                                setMailIntoAppropriateMapGroup1(terminalId, adminSalesMonitoringAlertDetailVOsSuspension, adminCBMonitoringAlertDetailVOsSuspension, adminRFMonitoringAlertDetailVOsSuspension, adminFraudMonitoringAlertDetailVOsSuspension, adminTechMonitoringAlertDetailVOsSuspension, agentSalesMonitoringAlertDetailVOsSuspension, agentCBMonitoringAlertDetailVOsSuspension, agentRFMonitoringAlertDetailVOsSuspension, agentFraudMonitoringAlertDetailVOsSuspension, agentTechMonitoringAlertDetailVOsSuspension, merchantCBMonitoringAlertDetailVOsSuspension, merchantRFMonitoringAlertDetailVOsSuspension, merchantSalesMonitoringAlertDetailVOsSuspension, merchantFraudMonitoringAlertDetailVOsSuspension, merchantTechnicalMonitoringAlertDetailVOsSuspension, partnerCBMonitoringAlertDetailVOsSuspension, partnerRFMonitoringAlertDetailVOsSuspension, partnerSalesMonitoringAlertDetailVOsSuspension, partnerFraudMonitoringAlertDetailVOsSuspension, partnerTechMonitoringAlertDetailVOsSuspension, adminSalesListHashMapPerTerminalSuspension, adminCBListHashMapPerTerminalSuspension, adminRFListHashMapPerTerminalSuspension, adminFraudListHashMapPerTerminalSuspension, adminTechListHashMapPerTerminalSuspension, agentSalesStringListHashMapPerTerminalSuspension, agentCBStringListHashMapPerTerminalSuspension, agentRFStringListHashMapPerTerminalSuspension, agentFraudStringListHashMapPerTerminalSuspension, agentTechStringListHashMapPerTerminalSuspension, merchantSalesListHashMapPerTerminalSuspension, merchantCBListHashMapPerTerminalSuspension, merchantRFListHashMapPerTerminalSuspension, merchantFraudListHashMapPerTerminalSuspension, merchantTechListHashMapPerTerminalSuspension, partnerCBListHashMapPerTerminalSuspension, partnerRFListHashMapPerTerminalSuspension, partnerSalesListHashMapPerTerminalSuspension, partnerFraudListHashMapPerTerminalSuspension, partnerTechnicalListHashMapTerminalSuspension);
                            }

                        }
                        /*logger.debug("isRuleVoilate::::::"+isRuleVoilate);
                        if(isRuleVoilate)
                        {
                            System.out.println("hello inside before isRuleVoilate");
                           //TODO:Add log entry into database
                            merchantMonitoringManager.logRuleDetails(memberId, partnerId, terminalId, "Weekly increase in no. of declined authorization", monitoringAlertDetailVO.getMonitoringAlertName());
                            System.out.println("hello inside after isRuleVoilate");
                        }
*/
                    }
                }//Monitoring Terminal Loop End

                if(isRuleVoilate)
                {
                    //Merchant mail
                    logger.debug("merchantSalesStringAttachmentListHashMapPerTerminal size()===" + merchantSalesStringAttachmentListHashMapPerTerminal.size());
                    logger.debug("merchantCBStringAttachmentListHashMapPerTerminal size()===" + merchantCBStringAttachmentListHashMapPerTerminal.size());
                    logger.debug("merchantRFStringAttachmentListHashMapPerTerminal size()===" + merchantRFStringAttachmentListHashMapPerTerminal.size());
                    logger.debug("merchantFraudStringAttachmentListHashMapPerTerminal size()===" + merchantFraudStringAttachmentListHashMapPerTerminal.size());
                    logger.debug("merchantTechStringAttachmentListHashMapPerTerminal size()===" + merchantTechStringAttachmentListHashMapPerTerminal.size());

                    sendMailToMerchantSalesTeam(merchantSalesStringListHashMap, merchantDetailsVO, merchantSalesListHashMapPerTerminalSuspension, merchantSalesStringAttachmentListHashMapPerTerminal,MonitoringFrequency.Weekly.toString());
                    sendMailToMerchantChargebackTeam(merchantCBStringListHashMap, merchantDetailsVO, merchantCBListHashMapPerTerminalSuspension, merchantCBStringAttachmentListHashMapPerTerminal,MonitoringFrequency.Weekly.toString());
                    sendMailToMerchantRefundTeam(merchantRFStringListHashMap, merchantDetailsVO, merchantRFListHashMapPerTerminalSuspension, merchantRFStringAttachmentListHashMapPerTerminal,MonitoringFrequency.Weekly.toString());
                    sendMailToMerchantFraudTeam(merchantFraudStringListHashMap, merchantDetailsVO, merchantFraudListHashMapPerTerminalSuspension, merchantFraudStringAttachmentListHashMapPerTerminal,MonitoringFrequency.Weekly.toString());
                    sendMailToMerchantTechnicalTeam(merchantTechnicalStringListHashMap, merchantDetailsVO, merchantTechListHashMapPerTerminalSuspension, merchantTechStringAttachmentListHashMapPerTerminal,MonitoringFrequency.Weekly.toString());

                /*Admin Type-1,2,3 Group Setting*/
                    fillConsolidateAdminAllTypeMailMap(memberId, adminSalesStringListHashMap, adminCBStringListHashMap, adminRFStringListHashMap, adminFraudStringListHashMap, adminTechStringListHashMap, adminSalesStringListHashMapConsolidate, adminCBStringListHashMapConsolidate, adminRFStringListHashMapConsolidate, adminFraudStringListHashMapConsolidate, adminTechStringListHashMapConsolidate);
                    fillConsolidateAdminAllTypeMailMap(memberId, adminSalesListHashMapPerTerminalSuspension, adminCBListHashMapPerTerminalSuspension, adminRFListHashMapPerTerminalSuspension, adminFraudListHashMapPerTerminalSuspension, adminTechListHashMapPerTerminalSuspension, adminSalesStringListHashMapConsolidateSuspension, adminCBStringListHashMapConsolidateSuspension, adminRFStringListHashMapConsolidateSuspension, adminFraudStringListHashMapConsolidateSuspension, adminTechStringListHashMapConsolidateSuspension);
                    fillConsolidateAdminAllTypeMailMap(memberId, adminSalesStringAttachmentListHashMapPerTerminal, adminCBStringAttachmentListHashMapPerTerminal, adminRFStringAttachmentListHashMapPerTerminal, adminFraudStringAttachmentListHashMapPerTerminal, adminTechStringAttachmentListHashMapPerTerminal, adminSalesStringAttachmentListHashMapConsolidate, adminCBStringAttachmentListHashMapConsolidate, adminRFStringAttachmentListHashMapConsolidate, adminFraudStringAttachmentListHashMapConsolidate, adminTechStringAttachmentListHashMapConsolidate);

                /*Agent Type-1,2,3 Group Setting*/
                    fillConsolidateAdminAllTypeMailMap(memberId, agentSalesStringListHashMap, agentCBStringListHashMap, agentRFStringListHashMap, agentFraudStringListHashMap, agentTechStringListHashMap, agentSalesStringListHashMapConsolidate, agentCBStringListHashMapConsolidate, agentRFStringListHashMapConsolidate, agentFraudStringListHashMapConsolidate, agentTechStringListHashMapConsolidate);
                    fillConsolidateAdminAllTypeMailMap(memberId, agentSalesStringListHashMapPerTerminalSuspension, agentCBStringListHashMapPerTerminalSuspension, agentRFStringListHashMapPerTerminalSuspension, agentFraudStringListHashMapPerTerminalSuspension, agentTechStringListHashMapPerTerminalSuspension, agentSalesStringListHashMapConsolidateSuspension, agentCBStringListHashMapConsolidateSuspension, agentRFStringListHashMapConsolidateSuspension, agentFraudStringListHashMapConsolidateSuspension, agentTechStringListHashMapConsolidateSuspension);
                    fillConsolidateAdminAllTypeMailMap(memberId, agentSalesStringAttachmentListHashMapPerTerminal, agentCBStringAttachmentListHashMapPerTerminal, agentRFStringAttachmentListHashMapPerTerminal, agentFraudStringAttachmentListHashMapPerTerminal, agentTechStringAttachmentListHashMapPerTerminal, agentSalesStringAttachmentListHashMapConsolidate, agentCBStringAttachmentListHashMapConsolidate, agentRFStringAttachmentListHashMapConsolidate, agentFraudStringAttachmentListHashMapConsolidate, agentTechStringAttachmentListHashMapConsolidate);

                /*Partner Type-1,2,3 Group Setting*/
                    fillConsolidateAdminAllTypeMailMap(memberId, partnerSalesStringListHashMap, partnerCBStringListHashMap, partnerRFStringListHashMap, partnerFraudStringListHashMap, partnerTechnicalStringListHashMap, partnerSalesStringListHashMapConsolidate, partnerCBStringListHashMapConsolidate, partnerRFStringListHashMapConsolidate, partnerFraudStringListHashMapConsolidate, partnerTechStringListHashMapConsolidate);
                    fillConsolidateAdminAllTypeMailMap(memberId, partnerSalesListHashMapPerTerminalSuspension, partnerCBListHashMapPerTerminalSuspension, partnerRFListHashMapPerTerminalSuspension, partnerFraudListHashMapPerTerminalSuspension, partnerTechnicalListHashMapTerminalSuspension, partnerSalesStringListHashMapConsolidateSuspension, partnerCBStringListHashMapConsolidateSuspension, partnerRFStringListHashMapConsolidateSuspension, partnerFraudStringListHashMapConsolidateSuspension, partnerTechStringListHashMapConsolidateSuspension);
                    fillConsolidateAdminAllTypeMailMap(memberId, partnerSalesStringAttachmentListHashMapPerTerminal, partnerCBStringAttachmentListHashMapPerTerminal, partnerRFStringAttachmentListHashMapPerTerminal, partnerFraudStringAttachmentListHashMapPerTerminal, partnerTechStringAttachmentListHashMapPerTerminal, partnerSalesStringAttachmentListHashMapConsolidate, partnerCBStringAttachmentListHashMapConsolidate, partnerRFStringAttachmentListHashMapConsolidate, partnerFraudStringAttachmentListHashMapConsolidate, partnerTechStringAttachmentListHashMapConsolidate);

                    fillConsolidatePerPartnerMailMap(partnerId,partnerSalesStringListHashMapConsolidatePerPartner,partnerCBStringListHashMapConsolidatePerPartner,partnerRFStringListHashMapConsolidatePerPartner,partnerFraudStringListHashMapConsolidatePerPartner,partnerTechStringListHashMapConsolidatePerPartner,partnerSalesStringListHashMapConsolidate,partnerCBStringListHashMapConsolidate,partnerRFStringListHashMapConsolidate,partnerFraudStringListHashMapConsolidate,partnerTechStringListHashMapConsolidate);
                    fillConsolidatePerPartnerMailMap(partnerId,partnerSalesStringListHashMapConsolidateSuspensionPerPartner,partnerCBStringListHashMapConsolidateSuspensionPerPartner,partnerRFStringListHashMapConsolidateSuspensionPerPartner,partnerFraudStringListHashMapConsolidateSuspensionPerPartner,partnerTechStringListHashMapConsolidateSuspensionPerPartner, partnerSalesStringListHashMapConsolidateSuspension, partnerCBStringListHashMapConsolidateSuspension, partnerRFStringListHashMapConsolidateSuspension, partnerFraudStringListHashMapConsolidateSuspension, partnerTechStringListHashMapConsolidateSuspension);
                    fillConsolidatePerPartnerMailMap(partnerId,partnerSalesStringListHashMapConsolidateAttachmentPerPartner,partnerCBStringListHashMapConsolidateAttachmentPerPartner,partnerRFStringListHashMapConsolidateAttachmentPerPartner,partnerFraudStringListHashMapConsolidateAttachmentPerPartner,partnerTechStringListHashMapConsolidateAttachmentPerPartner,partnerSalesStringAttachmentListHashMapConsolidate, partnerCBStringAttachmentListHashMapConsolidate, partnerRFStringAttachmentListHashMapConsolidate, partnerFraudStringAttachmentListHashMapConsolidate, partnerTechStringAttachmentListHashMapConsolidate);

                    for(AgentDetailsVO agentDetailsVO:agentDetailsVOs)
                    {
                        String agentId=agentDetailsVO.getAgentId();

                        fillConsolidatePerAgentMailMap(agentId,agentSalesStringListHashMapConsolidatePerAgent,agentCBStringListHashMapConsolidatePerAgent,agentRFStringListHashMapConsolidatePerAgent,agentFraudStringListHashMapConsolidatePerAgent,agentTechStringListHashMapConsolidatePerAgent,agentSalesStringListHashMapConsolidate, agentCBStringListHashMapConsolidate, agentRFStringListHashMapConsolidate, agentFraudStringListHashMapConsolidate, agentTechStringListHashMapConsolidate);
                        fillConsolidatePerAgentMailMap(agentId,agentSalesStringListHashMapConsolidateSuspensionPerAgent, agentCBStringListHashMapConsolidateSuspensionPerAgent, agentRFStringListHashMapConsolidateSuspensionPerAgent, agentFraudStringListHashMapConsolidateSuspensionPerAgent, agentTechStringListHashMapConsolidateSuspensionPerAgent, agentSalesStringListHashMapConsolidateSuspension, agentCBStringListHashMapConsolidateSuspension, agentRFStringListHashMapConsolidateSuspension, agentFraudStringListHashMapConsolidateSuspension, agentTechStringListHashMapConsolidateSuspension);
                        fillConsolidatePerAgentMailMap(agentId,agentSalesStringAttachmentListHashMapConsolidatePerAgent, agentCBStringAttachmentListHashMapConsolidatePerAgent, agentRFStringAttachmentListHashMapConsolidatePerAgent, agentFraudStringAttachmentListHashMapConsolidatePerAgent, agentTechStringAttachmentListHashMapConsolidatePerAgent, agentSalesStringAttachmentListHashMapConsolidate, agentCBStringAttachmentListHashMapConsolidate, agentRFStringAttachmentListHashMapConsolidate, agentFraudStringAttachmentListHashMapConsolidate, agentTechStringAttachmentListHashMapConsolidate);

                        agentList.add(agentId);
                    }
                }



            }//Monitoring Merchant Loop End

           /* System.out.println("adminSalesStringAttachmentListHashMapConsolidate size()=====" + adminSalesStringAttachmentListHashMapConsolidate.size());
            System.out.println("adminCBStringAttachmentListHashMapConsolidate size()=====" + adminCBStringAttachmentListHashMapConsolidate.size());
            System.out.println("adminRFStringAttachmentListHashMapConsolidate size()=====" + adminRFStringAttachmentListHashMapConsolidate.size());
            System.out.println("adminFraudStringAttachmentListHashMapConsolidate size()=====" + adminFraudStringAttachmentListHashMapConsolidate.size());
            System.out.println("adminTechStringAttachmentListHashMapConsolidate size()=====" + adminTechStringAttachmentListHashMapConsolidate.size());

            System.out.println("partnerSalesStringAttachmentListHashMapConsolidate size()=====" + partnerSalesStringAttachmentListHashMapConsolidate.size());
            System.out.println("partnerCBStringAttachmentListHashMapConsolidate size()=====" + partnerCBStringAttachmentListHashMapConsolidate.size());
            System.out.println("partnerRFStringAttachmentListHashMapConsolidate size()=====" + partnerRFStringAttachmentListHashMapConsolidate.size());
            System.out.println("partnerFraudStringAttachmentListHashMapConsolidate size()=====" + partnerFraudStringAttachmentListHashMapConsolidate.size());
            System.out.println("partnerTechStringAttachmentListHashMapConsolidate size()=====" + partnerTechStringAttachmentListHashMapConsolidate.size());

            System.out.println("agentSalesStringAttachmentListHashMapConsolidate size()=====" + agentSalesStringAttachmentListHashMapConsolidate.size());
            System.out.println("agentCBStringAttachmentListHashMapConsolidate size()=====" + agentCBStringAttachmentListHashMapConsolidate.size());
            System.out.println("agentRFStringAttachmentListHashMapConsolidate size()=====" + agentRFStringAttachmentListHashMapConsolidate.size());
            System.out.println("agentFraudStringAttachmentListHashMapConsolidate size()=====" + agentFraudStringAttachmentListHashMapConsolidate.size());
            System.out.println("agentTechStringAttachmentListHashMapConsolidate size()=====" + agentTechStringAttachmentListHashMapConsolidate.size());*/


            sendMailToAdminConsolidate(adminSalesStringListHashMapConsolidate, adminSalesStringListHashMapConsolidateSuspension, adminSalesStringAttachmentListHashMapConsolidate);
            sendMailToAdminCBConsolidate(adminCBStringListHashMapConsolidate, adminCBStringListHashMapConsolidateSuspension, adminCBStringAttachmentListHashMapConsolidate);
            sendMailToAdminRFConsolidate(adminRFStringListHashMapConsolidate, adminRFStringListHashMapConsolidateSuspension, adminRFStringAttachmentListHashMapConsolidate);
            sendMailToAdminFraudConsolidate(adminFraudStringListHashMapConsolidate, adminFraudStringListHashMapConsolidateSuspension, adminFraudStringAttachmentListHashMapConsolidate);
            sendMailToAdminTechConsolidate(adminTechStringListHashMapConsolidate, adminTechStringListHashMapConsolidateSuspension, adminTechStringAttachmentListHashMapConsolidate);


            //Mail Should be for multiple partners
            for(String partnerId:partnersList)
            {
                sendMailToPartnerSalesConsolidate(partnerId,partnerSalesStringListHashMapConsolidatePerPartner.get(partnerId), partnerSalesStringListHashMapConsolidateSuspensionPerPartner.get(partnerId), partnerSalesStringListHashMapConsolidateAttachmentPerPartner.get(partnerId));
                sendMailToPartnerFraudConsolidate(partnerId,partnerFraudStringListHashMapConsolidatePerPartner.get(partnerId), partnerFraudStringListHashMapConsolidateSuspensionPerPartner.get(partnerId), partnerFraudStringListHashMapConsolidateAttachmentPerPartner.get(partnerId));
                sendMailToPartnerCBConsolidate(partnerId,partnerCBStringListHashMapConsolidatePerPartner.get(partnerId), partnerCBStringListHashMapConsolidateSuspensionPerPartner.get(partnerId), partnerCBStringListHashMapConsolidateAttachmentPerPartner.get(partnerId));
                sendMailToPartnerRFConsolidate(partnerId,partnerRFStringListHashMapConsolidatePerPartner.get(partnerId), partnerRFStringListHashMapConsolidateSuspensionPerPartner.get(partnerId), partnerRFStringListHashMapConsolidateAttachmentPerPartner.get(partnerId));
                sendMailToPartnerTechnicalConsolidate(partnerId,partnerTechStringListHashMapConsolidatePerPartner.get(partnerId), partnerTechStringListHashMapConsolidateSuspensionPerPartner.get(partnerId), partnerTechStringListHashMapConsolidateAttachmentPerPartner.get(partnerId));
            }
            for(String agentId:agentList)
            {
                //Mail Should be for multiple agents
                sendMailToAgentSalesConsolidate(agentId,agentSalesStringListHashMapConsolidatePerAgent.get(agentId), agentSalesStringListHashMapConsolidateSuspensionPerAgent.get(agentId), agentSalesStringAttachmentListHashMapConsolidatePerAgent.get(agentId));
                sendMailToAgentCBConsolidate(agentId,agentCBStringListHashMapConsolidatePerAgent.get(agentId), agentCBStringListHashMapConsolidateSuspensionPerAgent.get(agentId), agentCBStringAttachmentListHashMapConsolidatePerAgent.get(agentId));
                sendMailToAgentRFConsolidate(agentId,agentRFStringListHashMapConsolidatePerAgent.get(agentId), agentRFStringListHashMapConsolidateSuspensionPerAgent.get(agentId), agentRFStringAttachmentListHashMapConsolidatePerAgent.get(agentId));
                sendMailToAgentFraudConsolidate(agentId,agentFraudStringListHashMapConsolidatePerAgent.get(agentId), agentFraudStringListHashMapConsolidateSuspensionPerAgent.get(agentId), agentFraudStringAttachmentListHashMapConsolidatePerAgent.get(agentId));
                sendMailToAgentTechConsolidate(agentId,agentTechStringListHashMapConsolidatePerAgent.get(agentId), agentTechStringListHashMapConsolidateSuspensionPerAgent.get(agentId), agentTechStringAttachmentListHashMapConsolidatePerAgent.get(agentId));

            }
        }
        else
        {
            logger.debug("No Terminals Founds For Implementation[Weekly]");
        }
    }

    public void monthlyMerchantMonitoringRiskAlert() throws Exception
    {
        MerchantMonitoringManager merchantMonitoringManager = new MerchantMonitoringManager();
        Set<String> partnersList=new TreeSet();
        Set<String> agentList=new TreeSet();
        AgentDAO agentDAO=new AgentDAO();
        Map<String, Map<String, List<MonitoringParameterMappingVO>>> stringListMap = merchantMonitoringManager.getMonitoringParameterGroupByMerchantMonthlyExecution();
        if (stringListMap.size() > 0)
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM,YYYY");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            /* Admin Type-1 Consolidated Email */
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminSalesStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminCBStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminRFStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminTechStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminFraudStringListHashMapConsolidate = new HashMap();

            /* Admin Type-2 Consolidated Email */
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminSalesStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminCBStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminRFStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminFraudStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminTechStringAttachmentListHashMapConsolidate = new HashMap();

            /* Admin Type-3 Consolidated Email */
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminSalesStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminCBStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminRFStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminTechStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminFraudStringListHashMapConsolidateSuspension = new HashMap();

            /* Agent Type-1 Consolidated Email */
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentSalesStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentCBStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentRFStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentFraudStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentTechStringListHashMapConsolidate = new HashMap();

            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentSalesStringListHashMapConsolidatePerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentCBStringListHashMapConsolidatePerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentRFStringListHashMapConsolidatePerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentFraudStringListHashMapConsolidatePerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentTechStringListHashMapConsolidatePerAgent = new HashMap();

            /* Agent Type-2 Consolidated Email */
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentSalesStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentCBStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentRFStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentFraudStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentTechStringAttachmentListHashMapConsolidate = new HashMap();

            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentSalesStringAttachmentListHashMapConsolidatePerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentCBStringAttachmentListHashMapConsolidatePerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentRFStringAttachmentListHashMapConsolidatePerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentFraudStringAttachmentListHashMapConsolidatePerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentTechStringAttachmentListHashMapConsolidatePerAgent = new HashMap();

            /*Agent Type-3 Consolidated Email */
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentSalesStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentCBStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentRFStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentFraudStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentTechStringListHashMapConsolidateSuspension = new HashMap();

            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentSalesStringListHashMapConsolidateSuspensionPerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentCBStringListHashMapConsolidateSuspensionPerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentRFStringListHashMapConsolidateSuspensionPerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentFraudStringListHashMapConsolidateSuspensionPerAgent = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentTechStringListHashMapConsolidateSuspensionPerAgent = new HashMap();


            /*Partner Type-1 Consolidated Email-Per Merchant */
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerSalesStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerFraudStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerCBStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerRFStringListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerTechStringListHashMapConsolidate = new HashMap();

            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerSalesStringListHashMapConsolidatePerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerFraudStringListHashMapConsolidatePerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerCBStringListHashMapConsolidatePerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerRFStringListHashMapConsolidatePerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerTechStringListHashMapConsolidatePerPartner = new HashMap();

            /*Partner Type-2 Consolidated Email */
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerSalesStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerFraudStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerCBStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerRFStringListHashMapConsolidateSuspension = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerTechStringListHashMapConsolidateSuspension = new HashMap();

            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerSalesStringListHashMapConsolidateSuspensionPerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerFraudStringListHashMapConsolidateSuspensionPerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerCBStringListHashMapConsolidateSuspensionPerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerRFStringListHashMapConsolidateSuspensionPerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerTechStringListHashMapConsolidateSuspensionPerPartner = new HashMap();

            /*Partner Type-3 Consolidated Email */
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerSalesStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerCBStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerRFStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerFraudStringAttachmentListHashMapConsolidate = new HashMap();
            Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerTechStringAttachmentListHashMapConsolidate = new HashMap();

            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerSalesStringListHashMapConsolidateAttachmentPerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerFraudStringListHashMapConsolidateAttachmentPerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerCBStringListHashMapConsolidateAttachmentPerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerRFStringListHashMapConsolidateAttachmentPerPartner = new HashMap();
            Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerTechStringListHashMapConsolidateAttachmentPerPartner = new HashMap();

            TerminalManager terminalManager = new TerminalManager();

            Set set = stringListMap.keySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext())
            {

                Map<String, List<MonitoringAlertDetailVO>> adminSalesStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminCBStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminRFStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminTechStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminFraudStringListHashMap = new HashMap();

                Map<String, List<MonitoringAlertDetailVO>> adminSalesStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminCBStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminRFStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminFraudStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminTechStringAttachmentListHashMapPerTerminal = new HashMap();

                Map<String, List<MonitoringAlertDetailVO>> adminSalesListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminCBListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminRFListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminFraudListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> adminTechListHashMapPerTerminalSuspension = new HashMap();

                /* Merchant Type-1 Consolidated Email */
                Map<String, List<MonitoringAlertDetailVO>> merchantSalesStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantCBStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantRFStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantFraudStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantTechnicalStringListHashMap = new HashMap();

                /* Merchant Type-2 Consolidated Email */
                Map<String, List<MonitoringAlertDetailVO>> merchantSalesStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantCBStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantRFStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantFraudStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantTechStringAttachmentListHashMapPerTerminal = new HashMap();

                /* Merchant Type-3 Consolidated Email */
                Map<String, List<MonitoringAlertDetailVO>> merchantSalesListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantCBListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantRFListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantFraudListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> merchantTechListHashMapPerTerminalSuspension = new HashMap();

                Map<String, List<MonitoringAlertDetailVO>> partnerCBStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerRFStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerSalesStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerFraudStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerTechnicalStringListHashMap = new HashMap();

                Map<String, List<MonitoringAlertDetailVO>> partnerSalesStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerCBStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerRFStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerFraudStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerTechStringAttachmentListHashMapPerTerminal = new HashMap();

                Map<String, List<MonitoringAlertDetailVO>> partnerCBListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerRFListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerSalesListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerFraudListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> partnerTechnicalListHashMapTerminalSuspension = new HashMap();

                Map<String, List<MonitoringAlertDetailVO>> agentSalesStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentCBStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentRFStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentFraudStringListHashMap = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentTechStringListHashMap = new HashMap();

                Map<String, List<MonitoringAlertDetailVO>> agentSalesStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentCBStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentRFStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentFraudStringAttachmentListHashMapPerTerminal = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentTechStringAttachmentListHashMapPerTerminal = new HashMap();

                Map<String, List<MonitoringAlertDetailVO>> agentSalesStringListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentCBStringListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentRFStringListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentFraudStringListHashMapPerTerminalSuspension = new HashMap();
                Map<String, List<MonitoringAlertDetailVO>> agentTechStringListHashMapPerTerminalSuspension = new HashMap();

                DateManager dateManager = new DateManager();
                DateVO dateVO = null;
                Date startDate = null;
                try
                {
                    dateVO = dateManager.getPreviousMonthDateRange();
                    startDate = dateFormat.parse(dateVO.getStartDate());
                }
                catch (ParseException e)
                {
                    logger.debug("Date Exception==="+e);
                }

                boolean isRuleViolate=false;
                String memberId = (String) iterator.next();
                Map<String, List<MonitoringParameterMappingVO>> stringListMapTerminal = stringListMap.get(memberId);
                Set terminalSet = stringListMapTerminal.keySet();
                Iterator terminalIterator = terminalSet.iterator();
                while (terminalIterator.hasNext())
                {

                    String terminalId = (String) terminalIterator.next();
                    TerminalVO terminalVO = terminalManager.getMemberTerminalWithActivationDetails(memberId, terminalId);
                    List<MonitoringParameterMappingVO> monitoringParameterMappingVOs = stringListMapTerminal.get(terminalId);

                    String terminalActivationDate = terminalVO.getActivationDate();
                    String firstTransactionDate = merchantMonitoringManager.getMemberFirstSubmission(terminalVO);
                    String lastTransactionDate = merchantMonitoringManager.getMemberLastSubmission(terminalVO);

                    if(!functions.isValueNull(terminalVO.getActivationDate()))
                    {
                        terminalActivationDate = firstTransactionDate;
                    }

                    if(!functions.isValueNull(terminalActivationDate))
                    {
                       // System.out.println("Terminal activation date is not found so we can not process risk rules on==="+terminalId);
                        continue;
                    }

                    TerminalProcessingDetailsVO priorMonthProcessingDetailsVO = merchantMonitoringManager.getLastMonthProcessingDetailsAsPerCalendarMonth(terminalVO);
                    //TerminalProcessingDetailsVO currentDayProcessingDetails = merchantMonitoringManager.getCurrentMonthProcessingDetailsAsPerCalendarMonthByDTStamp(terminalVO);
                    TerminalProcessingDetailsVO lastThreeMonthProcessingDetailsVO = merchantMonitoringManager.getLastThreeMonthProcessingDetailsAsPerCalendarMonth(terminalVO);
                    //TerminalProcessingDetailsVO currentMonthProcessingDetails = merchantMonitoringManager.getCurrentMonthProcessingDetailsAsPerCalendarMonthByDTStamp(terminalVO);
                    TerminalProcessingDetailsVO currentMonthProcessingDetails = merchantMonitoringManager.getPreviousMonthProcessingDetailsAsPerCalendarMonthByDTStamp(terminalVO);
                    TerminalLimitsVO terminalLimitsVO = terminalManager.getMemberTerminalProcessingLimitVO(memberId, terminalId);

                    currentMonthProcessingDetails.setFirstTransactionDate(firstTransactionDate);
                    currentMonthProcessingDetails.setLastTransactionDate(lastTransactionDate);

                    terminalVO.setActivationDate(terminalActivationDate);

                    List<TransactionVO> currentMonthCBTransactionVOsList = merchantMonitoringManager.getTransactionListByTimestamp(terminalVO, dateVO, "chargeback");
                    List<TransactionVO> currentMonthRFTransactionVOsList = merchantMonitoringManager.getTransactionListByTimestamp(terminalVO, dateVO, "reversed");
                    List<TransactionVO> currentMonthInCompleteTransactionVOsList = merchantMonitoringManager.getInCompleteTransactionListByTimestamp(terminalVO, dateVO);
                    List<TransactionVO> currentMonthTransactionVOsList = merchantMonitoringManager.getTransactionListByDTStamp(terminalVO, dateVO);

                    List<BinAmountVO> binAmountVOList = merchantMonitoringManager.getSameCardSameAmountDetail(terminalVO, dateVO);

                    /*Admin Type-1 List*/
                    List<MonitoringAlertDetailVO> adminSalesMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> adminCBMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> adminRFMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> adminFraudMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> adminTechMonitoringAlertDetailVOs = new ArrayList();

                    /*Admin Type-2 List*/
                    List<MonitoringAlertDetailVO> adminSalesMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> adminCBMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> adminRFMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> adminFraudMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> adminTechMonitoringAttachmentAlertDetailVOs = new ArrayList();

                    /*Admin Type-3 List*/
                    List<MonitoringAlertDetailVO> adminSalesMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> adminCBMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> adminRFMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> adminFraudMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> adminTechMonitoringAlertDetailVOsSuspension = new ArrayList();

                    /*Agent Type-1 List*/
                    List<MonitoringAlertDetailVO> agentSalesMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> agentCBsMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> agentRFsMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> agentFraudsMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> agentTechsMonitoringAlertDetailVOs = new ArrayList();

                    /*Agent Type-2 List*/
                    List<MonitoringAlertDetailVO> agentSalesMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> agentCBMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> agentRFMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> agentFraudMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> agentTechMonitoringAttachmentAlertDetailVOs = new ArrayList();

                    /*Admin Type-2 List*/
                    List<MonitoringAlertDetailVO> agentSalesMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> agentCBMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> agentRFMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> agentFraudMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> agentTechMonitoringAlertDetailVOsSuspension = new ArrayList();

                    /*Merchant Type-1 List*/
                    List<MonitoringAlertDetailVO> merchantCBMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantRFMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantSalesMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantFraudMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantTechnicalMonitoringAlertDetailVOs = new ArrayList();

                    /*Merchant Type-2 List*/
                    List<MonitoringAlertDetailVO> merchantSalesMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantCBMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantRFMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantFraudMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantTechMonitoringAttachmentAlertDetailVOs = new ArrayList();

                    /*Merchant Type-3 List*/
                    List<MonitoringAlertDetailVO> merchantCBMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantRFMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantSalesMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantFraudMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> merchantTechnicalMonitoringAlertDetailVOsSuspension = new ArrayList();

                    /*Partner Type-1 List*/
                    List<MonitoringAlertDetailVO> partnerCBMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerRFMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerSalesMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerFraudMonitoringAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerTechnicalMonitoringAlertDetailVOs = new ArrayList();

                    /*Partner Type-2 List*/
                    List<MonitoringAlertDetailVO> partnerSalesMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerCBMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerRFMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerFraudMonitoringAttachmentAlertDetailVOs = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerTechMonitoringAttachmentAlertDetailVOs = new ArrayList();

                    /*Partner Type-3 List*/
                    List<MonitoringAlertDetailVO> partnerCBMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerRFMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerSalesMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerFraudMonitoringAlertDetailVOsSuspension = new ArrayList();
                    List<MonitoringAlertDetailVO> partnerTechMonitoringAlertDetailVOsSuspension = new ArrayList();
                    //System.out.println("size()====="+ monitoringParameterMappingVOs.size());
                    for (MonitoringParameterMappingVO monitoringParameterMappingVO : monitoringParameterMappingVOs)
                    {
                        MonitoringParameterVO monitoringParameterVO=monitoringParameterMappingVO.getMonitoringParameterVO();
                        logger.debug("Risk Rule Name===" + monitoringParameterVO.getMonitoringParameterName());
                        MonitoringAlertDetailVO monitoringAlertDetailVO = null;

                        MerchantDAO merchantDAO = new MerchantDAO();
                        MerchantDetailsVO merchantDetailsVO = merchantDAO.getMemberDetails(memberId);
                        String partnerId=merchantDetailsVO.getPartnerId();
                        partnersList.add(partnerId);
                        List<AgentDetailsVO> agentDetailsVOs=agentDAO.getMerchantAgents(memberId);
                        logger.debug("monitoringParameterVO===" + monitoringParameterVO.getMonitoringParameterName());
                        if ("Y".equals(monitoringParameterMappingVO.getAlertActivation()))
                        {
                            if (MonitoringKeyword.AuthSuccessful.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.StuckTransaction.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.BlockedCountry.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.BlockedIP.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.BlockedEmail.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.BlockedCard.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.BlockedName.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.MatureChargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.MatureRefund.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.HighAmountOrder.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.SameCardSameAmount.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.SameCardSameAmountConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || (MonitoringKeyword.Chargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())))
                            {
                                monitoringAlertDetailVO = calculateAttachment(monitoringParameterMappingVO, terminalVO, currentMonthInCompleteTransactionVOsList, currentMonthCBTransactionVOsList, currentMonthRFTransactionVOsList,binAmountVOList, dateVO,"monthly");
                            }
                            else
                            {
                                monitoringAlertDetailVO = alertOnlyThresholdMonthly(monitoringParameterMappingVO, currentMonthProcessingDetails, terminalLimitsVO, lastThreeMonthProcessingDetailsVO,priorMonthProcessingDetailsVO,terminalVO);
                            }

                            if(monitoringAlertDetailVO!=null)
                            {
                                isRuleViolate=true;
                                if (monitoringAlertDetailVO instanceof MonitoringOnlyAlertDetailsVO)
                                {
                                    monitoringAlertDetailVO.setMonitoringAlertPeriod(simpleDateFormat.format(startDate));
                                    logger.debug("OnlyAlert Instance");
                                    monitoringAlertDetailVO.setAlertThreshold(monitoringParameterMappingVO.getMonthlyAlertThreshold());
                                    monitoringAlertDetailVO.setSuspensionThreshold(monitoringParameterMappingVO.getMonthlySuspensionThreshold());
                                    setMailIntoAppropriateListGroup(monitoringAlertDetailVO, adminSalesMonitoringAlertDetailVOs, adminCBMonitoringAlertDetailVOs, adminRFMonitoringAlertDetailVOs, adminFraudMonitoringAlertDetailVOs, adminTechMonitoringAlertDetailVOs, agentSalesMonitoringAlertDetailVOs, agentCBsMonitoringAlertDetailVOs, agentRFsMonitoringAlertDetailVOs, agentFraudsMonitoringAlertDetailVOs, agentTechsMonitoringAlertDetailVOs, merchantCBMonitoringAlertDetailVOs, merchantRFMonitoringAlertDetailVOs, merchantSalesMonitoringAlertDetailVOs, merchantFraudMonitoringAlertDetailVOs, merchantTechnicalMonitoringAlertDetailVOs, partnerCBMonitoringAlertDetailVOs, partnerRFMonitoringAlertDetailVOs, partnerSalesMonitoringAlertDetailVOs, partnerFraudMonitoringAlertDetailVOs, partnerTechnicalMonitoringAlertDetailVOs, monitoringParameterMappingVO);
                                    setMailIntoAppropriateMapGroup(terminalId, adminSalesMonitoringAlertDetailVOs, adminCBMonitoringAlertDetailVOs, adminRFMonitoringAlertDetailVOs, adminFraudMonitoringAlertDetailVOs, adminTechMonitoringAlertDetailVOs, agentSalesMonitoringAlertDetailVOs, agentCBsMonitoringAlertDetailVOs, agentRFsMonitoringAlertDetailVOs, agentFraudsMonitoringAlertDetailVOs, agentTechsMonitoringAlertDetailVOs, merchantCBMonitoringAlertDetailVOs, merchantRFMonitoringAlertDetailVOs, merchantSalesMonitoringAlertDetailVOs, merchantFraudMonitoringAlertDetailVOs, merchantTechnicalMonitoringAlertDetailVOs, partnerCBMonitoringAlertDetailVOs, partnerRFMonitoringAlertDetailVOs, partnerSalesMonitoringAlertDetailVOs, partnerFraudMonitoringAlertDetailVOs, partnerTechnicalMonitoringAlertDetailVOs, adminSalesStringListHashMap, adminCBStringListHashMap, adminRFStringListHashMap, adminFraudStringListHashMap, adminTechStringListHashMap, agentSalesStringListHashMap, agentCBStringListHashMap, agentRFStringListHashMap, agentFraudStringListHashMap, agentTechStringListHashMap, merchantSalesStringListHashMap, merchantCBStringListHashMap, merchantRFStringListHashMap, merchantFraudStringListHashMap, merchantTechnicalStringListHashMap, partnerCBStringListHashMap, partnerRFStringListHashMap, partnerSalesStringListHashMap, partnerFraudStringListHashMap, partnerTechnicalStringListHashMap);
                                }
                                else if (monitoringAlertDetailVO instanceof MonitoringAttachmentAlertDetailsVO)
                                {
                                    monitoringAlertDetailVO.setMonitoringAlertPeriod(simpleDateFormat.format(startDate));
                                    logger.debug("Attachment Alert Instance");
                                    monitoringAlertDetailVO.setAlertThreshold(monitoringParameterMappingVO.getMonthlyAlertThreshold());
                                    monitoringAlertDetailVO.setSuspensionThreshold(monitoringParameterMappingVO.getMonthlySuspensionThreshold());
                                    setMailIntoAppropriateListGroup2(monitoringAlertDetailVO, adminSalesMonitoringAttachmentAlertDetailVOs, adminCBMonitoringAttachmentAlertDetailVOs, adminRFMonitoringAttachmentAlertDetailVOs, adminFraudMonitoringAttachmentAlertDetailVOs, adminTechMonitoringAttachmentAlertDetailVOs, merchantSalesMonitoringAttachmentAlertDetailVOs, merchantCBMonitoringAttachmentAlertDetailVOs, merchantRFMonitoringAttachmentAlertDetailVOs, merchantFraudMonitoringAttachmentAlertDetailVOs, merchantTechMonitoringAttachmentAlertDetailVOs, agentSalesMonitoringAttachmentAlertDetailVOs, agentCBMonitoringAttachmentAlertDetailVOs, agentRFMonitoringAttachmentAlertDetailVOs, agentFraudMonitoringAttachmentAlertDetailVOs, agentTechMonitoringAttachmentAlertDetailVOs, partnerSalesMonitoringAttachmentAlertDetailVOs, partnerCBMonitoringAttachmentAlertDetailVOs, partnerRFMonitoringAttachmentAlertDetailVOs, partnerFraudMonitoringAttachmentAlertDetailVOs, partnerTechMonitoringAttachmentAlertDetailVOs, monitoringParameterMappingVO);
                                    setMailIntoAppropriateMapGroup2(terminalId, adminSalesMonitoringAttachmentAlertDetailVOs, adminCBMonitoringAttachmentAlertDetailVOs, adminRFMonitoringAttachmentAlertDetailVOs, adminFraudMonitoringAttachmentAlertDetailVOs, adminTechMonitoringAttachmentAlertDetailVOs, merchantSalesMonitoringAttachmentAlertDetailVOs, merchantCBMonitoringAttachmentAlertDetailVOs, merchantRFMonitoringAttachmentAlertDetailVOs, merchantFraudMonitoringAttachmentAlertDetailVOs, merchantTechMonitoringAttachmentAlertDetailVOs, agentSalesMonitoringAttachmentAlertDetailVOs, agentCBMonitoringAttachmentAlertDetailVOs, agentRFMonitoringAttachmentAlertDetailVOs, agentFraudMonitoringAttachmentAlertDetailVOs, agentTechMonitoringAttachmentAlertDetailVOs, partnerSalesMonitoringAttachmentAlertDetailVOs, partnerCBMonitoringAttachmentAlertDetailVOs, partnerRFMonitoringAttachmentAlertDetailVOs, partnerFraudMonitoringAttachmentAlertDetailVOs, partnerTechMonitoringAttachmentAlertDetailVOs, adminSalesStringAttachmentListHashMapPerTerminal, adminCBStringAttachmentListHashMapPerTerminal, adminRFStringAttachmentListHashMapPerTerminal, adminFraudStringAttachmentListHashMapPerTerminal, adminTechStringAttachmentListHashMapPerTerminal, merchantSalesStringAttachmentListHashMapPerTerminal, merchantCBStringAttachmentListHashMapPerTerminal, merchantRFStringAttachmentListHashMapPerTerminal, merchantFraudStringAttachmentListHashMapPerTerminal, merchantTechStringAttachmentListHashMapPerTerminal, agentSalesStringAttachmentListHashMapPerTerminal, agentCBStringAttachmentListHashMapPerTerminal, agentRFStringAttachmentListHashMapPerTerminal, agentFraudStringAttachmentListHashMapPerTerminal, agentTechStringAttachmentListHashMapPerTerminal, partnerSalesStringAttachmentListHashMapPerTerminal, partnerCBStringAttachmentListHashMapPerTerminal, partnerRFStringAttachmentListHashMapPerTerminal, partnerFraudStringAttachmentListHashMapPerTerminal, partnerTechStringAttachmentListHashMapPerTerminal);
                                    logger.debug("adminSalesMonitoringAlertDetailVOs size==" + adminSalesMonitoringAlertDetailVOs.size());
                                    logger.debug("adminSalesStringAttachmentListHashMapPerTerminal size==" + adminSalesStringAttachmentListHashMapPerTerminal.size());
                                }
                            }
                        }
                        if ("Y".equals(monitoringParameterMappingVO.getSuspensionActivation()))
                        {
                           // MonitoringAlertDetailVO monitoringAlertDetailVO = null;
                            if(MonitoringKeyword.AuthSuccessful.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.StuckTransaction.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.BlockedCountry.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.BlockedIP.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.BlockedEmail.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.BlockedCard.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.BlockedName.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.MatureChargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.MatureRefund.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.HighAmountOrder.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.SameCardSameAmount.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.SameCardSameAmountConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || (MonitoringKeyword.Chargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())))
                            {
                                monitoringAlertDetailVO = calculateRandomRiskRule(monitoringParameterMappingVO, terminalVO, currentMonthInCompleteTransactionVOsList, currentMonthCBTransactionVOsList, currentMonthRFTransactionVOsList,binAmountVOList, dateVO,"monthly");
                            }
                            else
                            {
                                monitoringAlertDetailVO = suspensionThresholdMonthly(monitoringParameterMappingVO, currentMonthProcessingDetails, terminalLimitsVO, lastThreeMonthProcessingDetailsVO,terminalVO,priorMonthProcessingDetailsVO);
                            }
                            if (monitoringAlertDetailVO != null)
                            {
                                isRuleViolate = true;
                                monitoringAlertDetailVO.setMonitoringAlertPeriod(simpleDateFormat.format(startDate));
                                logger.debug("Suspension Instance");
                                monitoringAlertDetailVO.setAlertThreshold(monitoringParameterMappingVO.getMonthlyAlertThreshold());
                                monitoringAlertDetailVO.setSuspensionThreshold(monitoringParameterMappingVO.getMonthlySuspensionThreshold());
                                setMailIntoAppropriateListGroup1(monitoringAlertDetailVO, adminSalesMonitoringAlertDetailVOsSuspension, adminCBMonitoringAlertDetailVOsSuspension, adminRFMonitoringAlertDetailVOsSuspension, adminFraudMonitoringAlertDetailVOsSuspension, adminTechMonitoringAlertDetailVOsSuspension, agentSalesMonitoringAlertDetailVOsSuspension, agentCBMonitoringAlertDetailVOsSuspension, agentRFMonitoringAlertDetailVOsSuspension, agentFraudMonitoringAlertDetailVOsSuspension, agentTechMonitoringAlertDetailVOsSuspension, merchantCBMonitoringAlertDetailVOsSuspension, merchantRFMonitoringAlertDetailVOsSuspension, merchantSalesMonitoringAlertDetailVOsSuspension, merchantFraudMonitoringAlertDetailVOsSuspension, merchantTechnicalMonitoringAlertDetailVOsSuspension, partnerCBMonitoringAlertDetailVOsSuspension, partnerRFMonitoringAlertDetailVOsSuspension, partnerSalesMonitoringAlertDetailVOsSuspension, partnerFraudMonitoringAlertDetailVOsSuspension, partnerTechMonitoringAlertDetailVOsSuspension, monitoringParameterMappingVO);
                                setMailIntoAppropriateMapGroup1(terminalId, adminSalesMonitoringAlertDetailVOsSuspension, adminCBMonitoringAlertDetailVOsSuspension, adminRFMonitoringAlertDetailVOsSuspension, adminFraudMonitoringAlertDetailVOsSuspension, adminTechMonitoringAlertDetailVOsSuspension, agentSalesMonitoringAlertDetailVOsSuspension, agentCBMonitoringAlertDetailVOsSuspension, agentRFMonitoringAlertDetailVOsSuspension, agentFraudMonitoringAlertDetailVOsSuspension, agentTechMonitoringAlertDetailVOsSuspension, merchantCBMonitoringAlertDetailVOsSuspension, merchantRFMonitoringAlertDetailVOsSuspension, merchantSalesMonitoringAlertDetailVOsSuspension, merchantFraudMonitoringAlertDetailVOsSuspension, merchantTechnicalMonitoringAlertDetailVOsSuspension, partnerCBMonitoringAlertDetailVOsSuspension, partnerRFMonitoringAlertDetailVOsSuspension, partnerSalesMonitoringAlertDetailVOsSuspension, partnerFraudMonitoringAlertDetailVOsSuspension, partnerTechMonitoringAlertDetailVOsSuspension, adminSalesListHashMapPerTerminalSuspension, adminCBListHashMapPerTerminalSuspension, adminRFListHashMapPerTerminalSuspension, adminFraudListHashMapPerTerminalSuspension, adminTechListHashMapPerTerminalSuspension, agentSalesStringListHashMapPerTerminalSuspension, agentCBStringListHashMapPerTerminalSuspension, agentRFStringListHashMapPerTerminalSuspension, agentFraudStringListHashMapPerTerminalSuspension, agentTechStringListHashMapPerTerminalSuspension, merchantSalesListHashMapPerTerminalSuspension, merchantCBListHashMapPerTerminalSuspension, merchantRFListHashMapPerTerminalSuspension, merchantFraudListHashMapPerTerminalSuspension, merchantTechListHashMapPerTerminalSuspension, partnerCBListHashMapPerTerminalSuspension, partnerRFListHashMapPerTerminalSuspension, partnerSalesListHashMapPerTerminalSuspension, partnerFraudListHashMapPerTerminalSuspension, partnerTechnicalListHashMapTerminalSuspension);
                            }
                        }
                    }
                }//Monitoring Terminal Loop End

                if(isRuleViolate)
                {
                    MerchantDAO merchantDAO = new MerchantDAO();
                    MerchantDetailsVO merchantDetailsVO = merchantDAO.getMemberDetails(memberId);
                    String partnerId=merchantDetailsVO.getPartnerId();
                    partnersList.add(partnerId);
                    List<AgentDetailsVO> agentDetailsVOs=agentDAO.getMerchantAgents(memberId);

                    //Merchant mail
                    logger.debug("merchantSalesStringAttachmentListHashMapPerTerminal size()===" + merchantSalesStringAttachmentListHashMapPerTerminal.size());
                    logger.debug("merchantCBStringAttachmentListHashMapPerTerminal size()===" + merchantCBStringAttachmentListHashMapPerTerminal.size());
                    logger.debug("merchantRFStringAttachmentListHashMapPerTerminal size()===" + merchantRFStringAttachmentListHashMapPerTerminal.size());
                    logger.debug("merchantFraudStringAttachmentListHashMapPerTerminal size()===" + merchantFraudStringAttachmentListHashMapPerTerminal.size());
                    logger.debug("merchantTechStringAttachmentListHashMapPerTerminal size()===" + merchantTechStringAttachmentListHashMapPerTerminal.size());

                    sendMailToMerchantSalesTeam(merchantSalesStringListHashMap, merchantDetailsVO, merchantSalesListHashMapPerTerminalSuspension, merchantSalesStringAttachmentListHashMapPerTerminal,MonitoringFrequency.Monthly.toString());
                    sendMailToMerchantChargebackTeam(merchantCBStringListHashMap, merchantDetailsVO, merchantCBListHashMapPerTerminalSuspension, merchantCBStringAttachmentListHashMapPerTerminal,MonitoringFrequency.Monthly.toString());
                    sendMailToMerchantRefundTeam(merchantRFStringListHashMap, merchantDetailsVO, merchantRFListHashMapPerTerminalSuspension, merchantRFStringAttachmentListHashMapPerTerminal,MonitoringFrequency.Monthly.toString());
                    sendMailToMerchantFraudTeam(merchantFraudStringListHashMap, merchantDetailsVO, merchantFraudListHashMapPerTerminalSuspension, merchantFraudStringAttachmentListHashMapPerTerminal,MonitoringFrequency.Monthly.toString());
                    sendMailToMerchantTechnicalTeam(merchantTechnicalStringListHashMap, merchantDetailsVO, merchantTechListHashMapPerTerminalSuspension, merchantTechStringAttachmentListHashMapPerTerminal,MonitoringFrequency.Monthly.toString());

                /*Admin Type-1,2,3 Group Setting*/
                    fillConsolidateAdminAllTypeMailMap(memberId, adminSalesStringListHashMap, adminCBStringListHashMap, adminRFStringListHashMap, adminFraudStringListHashMap, adminTechStringListHashMap, adminSalesStringListHashMapConsolidate, adminCBStringListHashMapConsolidate, adminRFStringListHashMapConsolidate, adminFraudStringListHashMapConsolidate, adminTechStringListHashMapConsolidate);
                    fillConsolidateAdminAllTypeMailMap(memberId, adminSalesListHashMapPerTerminalSuspension, adminCBListHashMapPerTerminalSuspension, adminRFListHashMapPerTerminalSuspension, adminFraudListHashMapPerTerminalSuspension, adminTechListHashMapPerTerminalSuspension, adminSalesStringListHashMapConsolidateSuspension, adminCBStringListHashMapConsolidateSuspension, adminRFStringListHashMapConsolidateSuspension, adminFraudStringListHashMapConsolidateSuspension, adminTechStringListHashMapConsolidateSuspension);
                    fillConsolidateAdminAllTypeMailMap(memberId, adminSalesStringAttachmentListHashMapPerTerminal, adminCBStringAttachmentListHashMapPerTerminal, adminRFStringAttachmentListHashMapPerTerminal, adminFraudStringAttachmentListHashMapPerTerminal, adminTechStringAttachmentListHashMapPerTerminal, adminSalesStringAttachmentListHashMapConsolidate, adminCBStringAttachmentListHashMapConsolidate, adminRFStringAttachmentListHashMapConsolidate, adminFraudStringAttachmentListHashMapConsolidate, adminTechStringAttachmentListHashMapConsolidate);

                /*Agent Type-1,2,3 Group Setting*/
                    fillConsolidateAdminAllTypeMailMap(memberId, agentSalesStringListHashMap, agentCBStringListHashMap, agentRFStringListHashMap, agentFraudStringListHashMap, agentTechStringListHashMap, agentSalesStringListHashMapConsolidate, agentCBStringListHashMapConsolidate, agentRFStringListHashMapConsolidate, agentFraudStringListHashMapConsolidate, agentTechStringListHashMapConsolidate);
                    fillConsolidateAdminAllTypeMailMap(memberId, agentSalesStringListHashMapPerTerminalSuspension, agentCBStringListHashMapPerTerminalSuspension, agentRFStringListHashMapPerTerminalSuspension, agentFraudStringListHashMapPerTerminalSuspension, agentTechStringListHashMapPerTerminalSuspension, agentSalesStringListHashMapConsolidateSuspension, agentCBStringListHashMapConsolidateSuspension, agentRFStringListHashMapConsolidateSuspension, agentFraudStringListHashMapConsolidateSuspension, agentTechStringListHashMapConsolidateSuspension);
                    fillConsolidateAdminAllTypeMailMap(memberId, agentSalesStringAttachmentListHashMapPerTerminal, agentCBStringAttachmentListHashMapPerTerminal, agentRFStringAttachmentListHashMapPerTerminal, agentFraudStringAttachmentListHashMapPerTerminal, agentTechStringAttachmentListHashMapPerTerminal, agentSalesStringAttachmentListHashMapConsolidate, agentCBStringAttachmentListHashMapConsolidate, agentRFStringAttachmentListHashMapConsolidate, agentFraudStringAttachmentListHashMapConsolidate, agentTechStringAttachmentListHashMapConsolidate);

                /*Partner Type-1,2,3 Group Setting*/
                    fillConsolidateAdminAllTypeMailMap(memberId, partnerSalesStringListHashMap, partnerCBStringListHashMap, partnerRFStringListHashMap, partnerFraudStringListHashMap, partnerTechnicalStringListHashMap, partnerSalesStringListHashMapConsolidate, partnerCBStringListHashMapConsolidate, partnerRFStringListHashMapConsolidate, partnerFraudStringListHashMapConsolidate, partnerTechStringListHashMapConsolidate);
                    fillConsolidateAdminAllTypeMailMap(memberId, partnerSalesListHashMapPerTerminalSuspension, partnerCBListHashMapPerTerminalSuspension, partnerRFListHashMapPerTerminalSuspension, partnerFraudListHashMapPerTerminalSuspension, partnerTechnicalListHashMapTerminalSuspension, partnerSalesStringListHashMapConsolidateSuspension, partnerCBStringListHashMapConsolidateSuspension, partnerRFStringListHashMapConsolidateSuspension, partnerFraudStringListHashMapConsolidateSuspension, partnerTechStringListHashMapConsolidateSuspension);
                    fillConsolidateAdminAllTypeMailMap(memberId, partnerSalesStringAttachmentListHashMapPerTerminal, partnerCBStringAttachmentListHashMapPerTerminal, partnerRFStringAttachmentListHashMapPerTerminal, partnerFraudStringAttachmentListHashMapPerTerminal, partnerTechStringAttachmentListHashMapPerTerminal, partnerSalesStringAttachmentListHashMapConsolidate, partnerCBStringAttachmentListHashMapConsolidate, partnerRFStringAttachmentListHashMapConsolidate, partnerFraudStringAttachmentListHashMapConsolidate, partnerTechStringAttachmentListHashMapConsolidate);

                    fillConsolidatePerPartnerMailMap(partnerId,partnerSalesStringListHashMapConsolidatePerPartner,partnerCBStringListHashMapConsolidatePerPartner,partnerRFStringListHashMapConsolidatePerPartner,partnerFraudStringListHashMapConsolidatePerPartner,partnerTechStringListHashMapConsolidatePerPartner,partnerSalesStringListHashMapConsolidate,partnerCBStringListHashMapConsolidate,partnerRFStringListHashMapConsolidate,partnerFraudStringListHashMapConsolidate,partnerTechStringListHashMapConsolidate);
                    fillConsolidatePerPartnerMailMap(partnerId,partnerSalesStringListHashMapConsolidateSuspensionPerPartner,partnerCBStringListHashMapConsolidateSuspensionPerPartner,partnerRFStringListHashMapConsolidateSuspensionPerPartner,partnerFraudStringListHashMapConsolidateSuspensionPerPartner,partnerTechStringListHashMapConsolidateSuspensionPerPartner, partnerSalesStringListHashMapConsolidateSuspension, partnerCBStringListHashMapConsolidateSuspension, partnerRFStringListHashMapConsolidateSuspension, partnerFraudStringListHashMapConsolidateSuspension, partnerTechStringListHashMapConsolidateSuspension);
                    fillConsolidatePerPartnerMailMap(partnerId,partnerSalesStringListHashMapConsolidateAttachmentPerPartner,partnerCBStringListHashMapConsolidateAttachmentPerPartner,partnerRFStringListHashMapConsolidateAttachmentPerPartner,partnerFraudStringListHashMapConsolidateAttachmentPerPartner,partnerTechStringListHashMapConsolidateAttachmentPerPartner,partnerSalesStringAttachmentListHashMapConsolidate, partnerCBStringAttachmentListHashMapConsolidate, partnerRFStringAttachmentListHashMapConsolidate, partnerFraudStringAttachmentListHashMapConsolidate, partnerTechStringAttachmentListHashMapConsolidate);

                    for(AgentDetailsVO agentDetailsVO:agentDetailsVOs)
                    {
                        String agentId=agentDetailsVO.getAgentId();

                        fillConsolidatePerAgentMailMap(agentId,agentSalesStringListHashMapConsolidatePerAgent,agentCBStringListHashMapConsolidatePerAgent,agentRFStringListHashMapConsolidatePerAgent,agentFraudStringListHashMapConsolidatePerAgent,agentTechStringListHashMapConsolidatePerAgent,agentSalesStringListHashMapConsolidate, agentCBStringListHashMapConsolidate, agentRFStringListHashMapConsolidate, agentFraudStringListHashMapConsolidate, agentTechStringListHashMapConsolidate);
                        fillConsolidatePerAgentMailMap(agentId,agentSalesStringListHashMapConsolidateSuspensionPerAgent, agentCBStringListHashMapConsolidateSuspensionPerAgent, agentRFStringListHashMapConsolidateSuspensionPerAgent, agentFraudStringListHashMapConsolidateSuspensionPerAgent, agentTechStringListHashMapConsolidateSuspensionPerAgent, agentSalesStringListHashMapConsolidateSuspension, agentCBStringListHashMapConsolidateSuspension, agentRFStringListHashMapConsolidateSuspension, agentFraudStringListHashMapConsolidateSuspension, agentTechStringListHashMapConsolidateSuspension);
                        fillConsolidatePerAgentMailMap(agentId,agentSalesStringAttachmentListHashMapConsolidatePerAgent, agentCBStringAttachmentListHashMapConsolidatePerAgent, agentRFStringAttachmentListHashMapConsolidatePerAgent, agentFraudStringAttachmentListHashMapConsolidatePerAgent, agentTechStringAttachmentListHashMapConsolidatePerAgent, agentSalesStringAttachmentListHashMapConsolidate, agentCBStringAttachmentListHashMapConsolidate, agentRFStringAttachmentListHashMapConsolidate, agentFraudStringAttachmentListHashMapConsolidate, agentTechStringAttachmentListHashMapConsolidate);

                        agentList.add(agentId);
                    }
                }
            }//Monitoring Merchant Loop End

            logger.debug("adminSalesStringAttachmentListHashMapConsolidate size()=====" + adminSalesStringAttachmentListHashMapConsolidate.size());
            logger.debug("adminCBStringAttachmentListHashMapConsolidate size()=====" + adminCBStringAttachmentListHashMapConsolidate.size());
            logger.debug("adminRFStringAttachmentListHashMapConsolidate size()=====" + adminRFStringAttachmentListHashMapConsolidate.size());
            logger.debug("adminFraudStringAttachmentListHashMapConsolidate size()=====" + adminFraudStringAttachmentListHashMapConsolidate.size());
            logger.debug("adminTechStringAttachmentListHashMapConsolidate size()=====" + adminTechStringAttachmentListHashMapConsolidate.size());

            logger.debug("partnerSalesStringAttachmentListHashMapConsolidate size()=====" + partnerSalesStringAttachmentListHashMapConsolidate.size());
            logger.debug("partnerCBStringAttachmentListHashMapConsolidate size()=====" + partnerCBStringAttachmentListHashMapConsolidate.size());
            logger.debug("partnerRFStringAttachmentListHashMapConsolidate size()=====" + partnerRFStringAttachmentListHashMapConsolidate.size());
            logger.debug("partnerFraudStringAttachmentListHashMapConsolidate size()=====" + partnerFraudStringAttachmentListHashMapConsolidate.size());
            logger.debug("partnerTechStringAttachmentListHashMapConsolidate size()=====" + partnerTechStringAttachmentListHashMapConsolidate.size());

            logger.debug("agentSalesStringAttachmentListHashMapConsolidate size()=====" + agentSalesStringAttachmentListHashMapConsolidate.size());
            logger.debug("agentCBStringAttachmentListHashMapConsolidate size()=====" + agentCBStringAttachmentListHashMapConsolidate.size());
            logger.debug("agentRFStringAttachmentListHashMapConsolidate size()=====" + agentRFStringAttachmentListHashMapConsolidate.size());
            logger.debug("agentFraudStringAttachmentListHashMapConsolidate size()=====" + agentFraudStringAttachmentListHashMapConsolidate.size());
            logger.debug("agentTechStringAttachmentListHashMapConsolidate size()=====" + agentTechStringAttachmentListHashMapConsolidate.size());


            sendMailToAdminConsolidate(adminSalesStringListHashMapConsolidate, adminSalesStringListHashMapConsolidateSuspension, adminSalesStringAttachmentListHashMapConsolidate);
            sendMailToAdminCBConsolidate(adminCBStringListHashMapConsolidate, adminCBStringListHashMapConsolidateSuspension, adminCBStringAttachmentListHashMapConsolidate);
            sendMailToAdminRFConsolidate(adminRFStringListHashMapConsolidate, adminRFStringListHashMapConsolidateSuspension, adminRFStringAttachmentListHashMapConsolidate);
            sendMailToAdminFraudConsolidate(adminFraudStringListHashMapConsolidate, adminFraudStringListHashMapConsolidateSuspension, adminFraudStringAttachmentListHashMapConsolidate);
            sendMailToAdminTechConsolidate(adminTechStringListHashMapConsolidate, adminTechStringListHashMapConsolidateSuspension, adminTechStringAttachmentListHashMapConsolidate);


            //Mail Should be for multiple partners
            for(String partnerId:partnersList)
            {
                sendMailToPartnerSalesConsolidate(partnerId,partnerSalesStringListHashMapConsolidatePerPartner.get(partnerId), partnerSalesStringListHashMapConsolidateSuspensionPerPartner.get(partnerId), partnerSalesStringListHashMapConsolidateAttachmentPerPartner.get(partnerId));
                sendMailToPartnerFraudConsolidate(partnerId,partnerFraudStringListHashMapConsolidatePerPartner.get(partnerId), partnerFraudStringListHashMapConsolidateSuspensionPerPartner.get(partnerId), partnerFraudStringListHashMapConsolidateAttachmentPerPartner.get(partnerId));
                sendMailToPartnerCBConsolidate(partnerId,partnerCBStringListHashMapConsolidatePerPartner.get(partnerId), partnerCBStringListHashMapConsolidateSuspensionPerPartner.get(partnerId), partnerCBStringListHashMapConsolidateAttachmentPerPartner.get(partnerId));
                sendMailToPartnerRFConsolidate(partnerId,partnerRFStringListHashMapConsolidatePerPartner.get(partnerId), partnerRFStringListHashMapConsolidateSuspensionPerPartner.get(partnerId), partnerRFStringListHashMapConsolidateAttachmentPerPartner.get(partnerId));
                sendMailToPartnerTechnicalConsolidate(partnerId,partnerTechStringListHashMapConsolidatePerPartner.get(partnerId), partnerTechStringListHashMapConsolidateSuspensionPerPartner.get(partnerId), partnerTechStringListHashMapConsolidateAttachmentPerPartner.get(partnerId));
            }
            for(String agentId:agentList)
            {
                //Mail Should be for multiple agents
                sendMailToAgentSalesConsolidate(agentId,agentSalesStringListHashMapConsolidatePerAgent.get(agentId), agentSalesStringListHashMapConsolidateSuspensionPerAgent.get(agentId), agentSalesStringAttachmentListHashMapConsolidatePerAgent.get(agentId));
                sendMailToAgentCBConsolidate(agentId,agentCBStringListHashMapConsolidatePerAgent.get(agentId), agentCBStringListHashMapConsolidateSuspensionPerAgent.get(agentId), agentCBStringAttachmentListHashMapConsolidatePerAgent.get(agentId));
                sendMailToAgentRFConsolidate(agentId,agentRFStringListHashMapConsolidatePerAgent.get(agentId), agentRFStringListHashMapConsolidateSuspensionPerAgent.get(agentId), agentRFStringAttachmentListHashMapConsolidatePerAgent.get(agentId));
                sendMailToAgentFraudConsolidate(agentId,agentFraudStringListHashMapConsolidatePerAgent.get(agentId), agentFraudStringListHashMapConsolidateSuspensionPerAgent.get(agentId), agentFraudStringAttachmentListHashMapConsolidatePerAgent.get(agentId));
                sendMailToAgentTechConsolidate(agentId,agentTechStringListHashMapConsolidatePerAgent.get(agentId), agentTechStringListHashMapConsolidateSuspensionPerAgent.get(agentId), agentTechStringAttachmentListHashMapConsolidatePerAgent.get(agentId));

            }
        }
        else
        {
            logger.debug("No Terminals Founds For Implementation[Monthly]");
        }
    }
    void setMonitoringToEntityFlags(MonitoringAlertDetailVO monitoringAlertDetailVO, MonitoringParameterMappingVO monitoringParameterMappingVO)
    {
        if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAdmin()))
        {
            monitoringAlertDetailVO.setAlertToAdmin(true);
        }
        if ("Y".equals(monitoringParameterMappingVO.getIsAlertToMerchant()))
        {
            monitoringAlertDetailVO.setAlertToMerchant(true);
        }
        if ("Y".equals(monitoringParameterMappingVO.getIsAlertToPartner()))
        {
            monitoringAlertDetailVO.setAlertToPartner(true);
        }
        if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAgent()))
        {
            monitoringAlertDetailVO.setAlertToAgent(true);
        }
    }

    public MonitoringAlertDetailVO thresholdAnalysis(MonitoringParameterMappingVO monitoringParameterMappingVO, TerminalProcessingDetailsVO currentDayProcessingDetails, TerminalProcessingDetailsVO priorMonthProcessingDetailsVO, TerminalVO terminalVO, TerminalProcessingDetailsVO lastThreeMonthProcessingDetailsVO, TerminalLimitsVO terminalLimitsVO, TerminalProcessingDetailsVO currentMonthProcessingDetails)
    {
        if ("Y".equals(monitoringParameterMappingVO.getAlertActivation()))
        {
            MonitoringParameterVO monitoringParameterVO = monitoringParameterMappingVO.getMonitoringParameterVO();
            logger.debug("keyword===" + monitoringParameterVO.getMonitoingKeyword());
            return alertOnlyThresholdAnalysis(monitoringParameterMappingVO, currentDayProcessingDetails, priorMonthProcessingDetailsVO, terminalVO, lastThreeMonthProcessingDetailsVO, terminalLimitsVO, currentMonthProcessingDetails);
        }
        if ("Y".equals(monitoringParameterMappingVO.getSuspensionActivation()))
        {
            return suspensionThresholdAnalysis(monitoringParameterMappingVO, currentDayProcessingDetails, priorMonthProcessingDetailsVO, terminalVO, lastThreeMonthProcessingDetailsVO, terminalLimitsVO, currentMonthProcessingDetails);
        }
        else
            return null;
    }

    /*public MonitoringAlertDetailVO thresholdAnalysisDaily(MonitoringParameterMappingVO monitoringParameterMappingVO, TerminalProcessingDetailsVO currentDayProcessingDetails, TerminalProcessingDetailsVO priorMonthProcessingDetailsVO, TerminalVO terminalVO, TerminalProcessingDetailsVO lastThreeMonthProcessingDetailsVO, TerminalLimitsVO terminalLimitsVO, TerminalProcessingDetailsVO currentMonthProcessingDetails)
    {
        MonitoringParameterVO monitoringParameterVO = monitoringParameterMappingVO.getMonitoringParameterVO();
        MerchantMonitoringDAO merchantMonitoringDAO=new MerchantMonitoringDAO();
        MerchantMonitoringManager merchantMonitoringManager = new MerchantMonitoringManager();
        DateManager dateManager = new DateManager();
        if ("Y".equals(monitoringParameterMappingVO.getAlertActivation()))
        {
            //Card velocity
            if(MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardSameAmount.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
            {
                int sameCardSameAmountCountActual = 0;
                int sameCardSameAmountCountThreshold = 0;
                String binString="";

                sameCardSameAmountCountThreshold = Double.valueOf(monitoringParameterMappingVO.getAlertThreshold()).intValue();

                DateVO dateVO = dateManager.getCurrentDayDateRange();
                List<BinAmountVO> binAmountVOList = null;
                try
                {
                    binAmountVOList = merchantMonitoringManager.getSameCardSameAmountDetail(terminalVO, dateVO);
                }
                catch (Exception e)
                {

                }
                for (BinAmountVO binAmountVO : binAmountVOList)
                {
                    sameCardSameAmountCountActual = binAmountVO.getCount();
                    binString=binAmountVO.getBinString();
                    System.out.println("=========================");
                    System.out.println("binString====="+binString);
                    System.out.println("sameCardSameAmountCountActual====="+sameCardSameAmountCountActual);
                    System.out.println("=========================");

                    if(sameCardSameAmountCountActual > sameCardSameAmountCountThreshold)
                    {

                        System.out.println("Action need to be taken====");


                    }
                }
            }
            else if(MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardSameAmountConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
            {
                //Authorization log - Consecutive use same account same amount
                int sameCardSameAmountConsequenceCountActual=0;
                int sameCardSameAmountConsequenceCountThreshold=0;

                sameCardSameAmountConsequenceCountThreshold=Double.valueOf(monitoringParameterMappingVO.getAlertThreshold()).intValue();

                DateVO dateVO=dateManager.getCurrentDayDateRange();
                List<BinAmountVO> binAmountVOList=null;
                try
                {
                    binAmountVOList=merchantMonitoringManager.getSameCardSameAmountConsequenceDetail(terminalVO, dateVO);
                }
                catch (Exception e)
                {

                }

                for(BinAmountVO binAmountVO:binAmountVOList)
                {
                    sameCardSameAmountConsequenceCountActual=binAmountVO.getCount();
                    System.out.println("==================");
                    System.out.println(terminalVO.toString());
                    System.out.println("sameCardSameAmountCountActual=="+sameCardSameAmountConsequenceCountActual);
                    System.out.println("sameCardSameAmountCountThreshold=="+sameCardSameAmountConsequenceCountThreshold);
                    System.out.println("bin String=="+binAmountVO.getBinString());
                    System.out.println("bin Amount=="+binAmountVO.getBinAmount());

                    if(sameCardSameAmountConsequenceCountActual>sameCardSameAmountConsequenceCountThreshold)
                    {
                        System.out.println("action need to be taken...");
                    }
                }
            }
            else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
            {
                //Card velocity - Same account consecutive days
                //
                try
                {
                    DateVO currentMonthDateRange = dateManager.getCurrentDayDateRange();
                    Set<String> todayBinDetails = merchantMonitoringDAO.getAllCardsGroupByBins(terminalVO, currentMonthDateRange);

                    System.out.println("todayBinDetails===" + todayBinDetails);
                    MerchantTerminalThresholdVO merchantTerminalThresholdVO = merchantMonitoringManager.getTerminalThresholdDetails(terminalVO);
                    TerminalThresholdsVO terminalThresholdsVO = merchantTerminalThresholdVO.getTerminalThresholdsVO();
                    List<Set> sets = new ArrayList();
                    int days = terminalThresholdsVO.getSameCardConsequentlyThreshold();
                    System.out.println("day===" + days);

                    int i = 1;
                    while (days > i)
                    {
                        DateVO previousDayDateRange = dateManager.getPreviousDayDateRange(i);
                        Set<String> set1 = merchantMonitoringDAO.getAllCardsGroupByBins(terminalVO, previousDayDateRange);
                        System.out.println("set1===" + set1);
                        if (set1.size() > 0)
                        {
                            sets.add(set1);
                        }
                        i++;
                    }


                    System.out.println("set===" + sets);
                    for (String bin : todayBinDetails)
                    {
                        boolean status = false;
                        for (Set set2 : sets)
                        {
                            if (set2.contains(bin))
                            {
                                status = true;
                            }
                            else
                            {
                                status = false;
                                break;
                            }
                        }
                        if (status)
                        {
                            System.out.println("Action to be taken======on Card velocity - Same account consecutive days");
                        }
                    }
                }
                catch (Exception e)
                {

                }
            }
            else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.StuckTransaction.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
            {
                MonitoringAttachmentAlertDetailsVO monitoringAttachmentAlertDetailsVO = null;
                List<FileAttachmentVO> fileAttachmentVOs = new ArrayList();
                List<PZTransactionStatus> transactionStatuses = new ArrayList();
                transactionStatuses.add(PZTransactionStatus.AUTH_STARTED);
                transactionStatuses.add(PZTransactionStatus.CAPTURE_STARTED);
                transactionStatuses.add(PZTransactionStatus.MARKED_FOR_REVERSAL);
                for (PZTransactionStatus pzTransactionStatus : transactionStatuses)
                {
                    System.out.println("pzTransactionStatus=====" + pzTransactionStatus);
                    FileAttachmentVO fileAttachmentVO = prepareExcelFile(terminalVO, Double.valueOf(monitoringParameterMappingVO.getAlertThreshold()).intValue(), pzTransactionStatus);
                    if (fileAttachmentVO != null)
                    {
                        fileAttachmentVOs.add(fileAttachmentVO);
                    }
                }

                if (fileAttachmentVOs.size() > 0)
                {
                    monitoringAttachmentAlertDetailsVO = new MonitoringAttachmentAlertDetailsVO();
                    monitoringAttachmentAlertDetailsVO.setIntimationDepartment("tech");
                    monitoringAttachmentAlertDetailsVO.setAlertWithAttachment(true);
                    monitoringAttachmentAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                    monitoringAttachmentAlertDetailsVO.setFileAttachmentVOList(fileAttachmentVOs);
                    setMonitoringToEntityFlags(monitoringAttachmentAlertDetailsVO, monitoringParameterMappingVO);
                }
                return monitoringAttachmentAlertDetailsVO;
            }
            else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.MatureChargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
            {
                //Chargeback of mature transaction
                try
                {
                    System.out.println("terminalVO===" + terminalVO);
                    //DateVO dateVO=dateManager.getCurrentDayDateRange();
                    DateVO dateVO = dateManager.getCurrentMonthDateRange();
                    //DateVO dateVO=dateManager.getLastSixCalendarMonthDateRange();
                    List<TransactionVO> transactionVOs = merchantMonitoringDAO.getTransactionListByTimestamp(terminalVO, dateVO, "chargeback");
                    System.out.println("transactionVOs====size" + transactionVOs.size());
                    for (TransactionVO transactionVO : transactionVOs)
                    {
                        System.out.println("trackingid=====" + transactionVO.getTrackingId());
                        System.out.println("transaction date=======" + transactionVO.getTransactionDate());
                        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String currentMonthEndDate = targetFormat.format(new Date());

                        String currentMonthStartDate = "";
                        long day = Functions.DATEDIFF(transactionVO.getTransactionDate(), currentMonthEndDate);
                        System.out.println("date diff=====" + day);
                        if (day > monitoringParameterMappingVO.getAlertThreshold())//value should be 180 days by default
                        {
                            System.out.println("Action to be taken======on Chargeback of mature transaction");
                        }
                    }

                }
                catch (Exception e)
                {

                }

            }
            else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.MatureRefund.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
            {
                //refund of mature transaction
                try
                {
                    System.out.println("terminalVO===" + terminalVO);
                    //DateVO dateVO=dateManager.getCurrentDayDateRange();
                    DateVO dateVO = dateManager.getCurrentMonthDateRange();
                    //DateVO dateVO=dateManager.getLastSixCalendarMonthDateRange();
                    List<TransactionVO> transactionVOs = merchantMonitoringDAO.getTransactionListByTimestamp(terminalVO, dateVO, "refund");
                    System.out.println("transactionVOs====size" + transactionVOs.size());
                    for (TransactionVO transactionVO : transactionVOs)
                    {
                        System.out.println("trackingid=====" + transactionVO.getTrackingId());
                        System.out.println("transaction date=======" + transactionVO.getTransactionDate());
                        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String currentMonthEndDate = targetFormat.format(new Date());

                        String currentMonthStartDate = "";
                        long day = Functions.DATEDIFF(transactionVO.getTransactionDate(), currentMonthEndDate);
                        System.out.println("date diff=====" + day);
                        if (day > monitoringParameterMappingVO.getAlertThreshold())//value should be 180 days by default
                        {
                            System.out.println("Action to be taken======on refund of mature transaction");
                        }
                    }

                }
                catch (Exception e)
                {

                }
            }
            else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameAmountWithRound.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
            {
                //Rounded transaction amounts


            }
            else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.HighAmountOrder.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
            {
                //High amount transaction


            }
            else
            {
                return alertOnlyThresholdAnalysisDaily(monitoringParameterMappingVO, currentDayProcessingDetails, priorMonthProcessingDetailsVO, terminalVO, lastThreeMonthProcessingDetailsVO, terminalLimitsVO, currentMonthProcessingDetails);

            }
        }
        if ("Y".equals(monitoringParameterMappingVO.getSuspensionActivation()))
        {
            return suspensionThresholdAnalysisDaily(monitoringParameterMappingVO, currentDayProcessingDetails, priorMonthProcessingDetailsVO, terminalVO, lastThreeMonthProcessingDetailsVO, terminalLimitsVO, currentMonthProcessingDetails);
        }
        else
            return null;
    }*/

   /* public MonitoringAlertDetailVO thresholdAnalysisWeekly(MonitoringParameterMappingVO monitoringParameterMappingVO, TerminalProcessingDetailsVO processingDetailsVO, TerminalLimitsVO terminalLimitsVO, TerminalProcessingDetailsVO lastThreeMonthProcessingDetails)
    {
        if ("Y".equals(monitoringParameterMappingVO.getAlertActivation()))
        {
            return alertOnlyThresholdWeekly(monitoringParameterMappingVO, processingDetailsVO, terminalLimitsVO, lastThreeMonthProcessingDetails);
        }
        if ("Y".equals(monitoringParameterMappingVO.getSuspensionActivation()))
        {
            return suspensionThresholdWeekly(monitoringParameterMappingVO, processingDetailsVO, terminalLimitsVO, lastThreeMonthProcessingDetails);
        }
        else
            return null;
    }
*/
    /*public MonitoringAlertDetailVO thresholdAnalysisMonthly(MonitoringParameterMappingVO monitoringParameterMappingVO, TerminalProcessingDetailsVO processingDetailsVO, TerminalLimitsVO terminalLimitsVO, TerminalProcessingDetailsVO lastThreeMonthProcessingDetails)
    {
        if ("Y".equals(monitoringParameterMappingVO.getAlertActivation()))
        {
            return alertOnlyThresholdMonthly(monitoringParameterMappingVO, processingDetailsVO, terminalLimitsVO, lastThreeMonthProcessingDetails);
        }
        if ("Y".equals(monitoringParameterMappingVO.getSuspensionActivation()))
        {
            return suspensionThresholdMonthly(monitoringParameterMappingVO, processingDetailsVO, terminalLimitsVO, lastThreeMonthProcessingDetails);
        }
        else
            return null;
    }*/

    public MonitoringOnlyAlertDetailsVO alertOnlyThresholdAnalysis(MonitoringParameterMappingVO monitoringParameterMappingVO, TerminalProcessingDetailsVO processingDetails, TerminalProcessingDetailsVO priorMonthProcessingDetailsVO, TerminalVO terminalVO, TerminalProcessingDetailsVO lastThreeMonthProcessingDetailsVO, TerminalLimitsVO terminalLimitsVO, TerminalProcessingDetailsVO currentMonthProcessingDetails)
    {
        MonitoringOnlyAlertDetailsVO monitoringOnlyAlertDetailsVO = null;
        MonitoringParameterVO monitoringParameterVO = monitoringParameterMappingVO.getMonitoringParameterVO();

        double targetThreshold = monitoringParameterMappingVO.getAlertThreshold();
        ParaCalculationDetailsVO paraCalculationDetailsVO = calculateThresholdWithLiveData(monitoringParameterVO, processingDetails, priorMonthProcessingDetailsVO, terminalVO, lastThreeMonthProcessingDetailsVO, terminalLimitsVO, currentMonthProcessingDetails);

        if (MonitoringDeviation.Higher.toString().equals(monitoringParameterVO.getMonitoingDeviation()))
        {
            if (paraCalculationDetailsVO.getCalculation() > targetThreshold)
            {
                monitoringOnlyAlertDetailsVO = new MonitoringOnlyAlertDetailsVO();
                monitoringOnlyAlertDetailsVO.setIntimationDepartment(paraCalculationDetailsVO.getCalculationArea());
                monitoringOnlyAlertDetailsVO.setAlertOnly(true);
                monitoringOnlyAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                monitoringOnlyAlertDetailsVO.setAlertMsg(monitoringParameterMappingVO.getAlertMessage());
                setMonitoringToEntityFlags(monitoringOnlyAlertDetailsVO, monitoringParameterMappingVO);
            }
        }
        else if (MonitoringDeviation.Lower.toString().equals(monitoringParameterVO.getMonitoingDeviation()))
        {
            if (paraCalculationDetailsVO.getCalculation() < targetThreshold)
            {
                monitoringOnlyAlertDetailsVO = new MonitoringOnlyAlertDetailsVO();
                monitoringOnlyAlertDetailsVO.setIntimationDepartment(paraCalculationDetailsVO.getCalculationArea());
                monitoringOnlyAlertDetailsVO.setAlertOnly(true);
                monitoringOnlyAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                monitoringOnlyAlertDetailsVO.setAlertMsg(monitoringParameterMappingVO.getAlertMessage());
                setMonitoringToEntityFlags(monitoringOnlyAlertDetailsVO, monitoringParameterMappingVO);
            }
        }
        return monitoringOnlyAlertDetailsVO;
    }

    public MonitoringOnlyAlertDetailsVO alertOnlyThresholdWeekly(MonitoringParameterMappingVO monitoringParameterMappingVO, TerminalProcessingDetailsVO processingDetails, TerminalLimitsVO terminalLimitsVO, TerminalProcessingDetailsVO lastThreeMonthProcessingDetailsVO,TerminalProcessingDetailsVO priorMonthProcessingDetailsVO,TerminalProcessingDetailsVO currentMonthProcessingDetailsVO,TerminalVO terminalVO)
    {
        MonitoringOnlyAlertDetailsVO monitoringOnlyAlertDetailsVO = null;
        MonitoringParameterVO monitoringParameterVO = monitoringParameterMappingVO.getMonitoringParameterVO();
        double targetThreshold = monitoringParameterMappingVO.getWeeklyAlertThreshold();
        ParaCalculationDetailsVO paraCalculationDetailsVO = calculateThresholdWeekly(monitoringParameterVO, processingDetails, terminalLimitsVO, lastThreeMonthProcessingDetailsVO,priorMonthProcessingDetailsVO,currentMonthProcessingDetailsVO,terminalVO);
        logger.debug("calculation=====" + paraCalculationDetailsVO.getCalculation());
        logger.debug("targetThreshold=====" + targetThreshold);
        if (MonitoringDeviation.Higher.toString().equals(monitoringParameterVO.getMonitoingDeviation()))
        {

            if (paraCalculationDetailsVO.getCalculation() > targetThreshold)
            {
                monitoringOnlyAlertDetailsVO = new MonitoringOnlyAlertDetailsVO();
                monitoringOnlyAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                //monitoringOnlyAlertDetailsVO.setMonitoringAlertPeriod();
                String validaMsg = "";
                if (MonitoringUnit.Percentage.toString().equals(monitoringParameterVO.getMonitoringUnit()))
                {
                    validaMsg = String.valueOf(Functions.round(paraCalculationDetailsVO.getCalculation(), 2) + " %");
                }
                else
                {
                    validaMsg = String.valueOf(Double.valueOf(paraCalculationDetailsVO.getCalculation()).intValue());
                }
                monitoringOnlyAlertDetailsVO.setAlertMsg(getAlertMessage(monitoringParameterMappingVO.getAlertMessage(), validaMsg));
                setMonitoringToEntityFlags(monitoringOnlyAlertDetailsVO, monitoringParameterMappingVO);
            }
        }
        else if (MonitoringDeviation.Lower.toString().equals(monitoringParameterVO.getMonitoingDeviation()))
        {
            logger.debug("calculation=====" + paraCalculationDetailsVO.getCalculation());
            logger.debug("targetThreshold=====" + targetThreshold);
            if(MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) || MonitoringAlertType.AvgTicketDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
            {
                targetThreshold=targetThreshold*-1;
            }
            if (paraCalculationDetailsVO.getCalculation() < targetThreshold)
            {
                monitoringOnlyAlertDetailsVO = new MonitoringOnlyAlertDetailsVO();
                monitoringOnlyAlertDetailsVO.setIntimationDepartment(paraCalculationDetailsVO.getCalculationArea());
                monitoringOnlyAlertDetailsVO.setAlertOnly(true);
                monitoringOnlyAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                String validaMsg = "";
                if (MonitoringUnit.Percentage.toString().equals(monitoringParameterVO.getMonitoringUnit()))
                {
                    validaMsg = String.valueOf(Functions.round(paraCalculationDetailsVO.getCalculation(), 2) + " %");
                }
                else
                {
                    validaMsg = String.valueOf(Double.valueOf(paraCalculationDetailsVO.getCalculation()).intValue());
                }
                monitoringOnlyAlertDetailsVO.setAlertMsg(getAlertMessage(monitoringParameterMappingVO.getAlertMessage(), validaMsg));
                setMonitoringToEntityFlags(monitoringOnlyAlertDetailsVO, monitoringParameterMappingVO);
            }



        }
        if(monitoringOnlyAlertDetailsVO!=null)
        {
            monitoringOnlyAlertDetailsVO.setActualratio(paraCalculationDetailsVO.getCalculation());

        }
        return monitoringOnlyAlertDetailsVO;
    }

    public MonitoringOnlyAlertDetailsVO alertOnlyThresholdMonthly(MonitoringParameterMappingVO monitoringParameterMappingVO, TerminalProcessingDetailsVO processingDetails, TerminalLimitsVO terminalLimitsVO, TerminalProcessingDetailsVO lastThreeMonthProcessingDetailsVO,TerminalProcessingDetailsVO priorMonthProcessingDetailsVO,TerminalVO terminalVO)
    {
        MonitoringOnlyAlertDetailsVO monitoringOnlyAlertDetailsVO = null;
        MonitoringParameterVO monitoringParameterVO = monitoringParameterMappingVO.getMonitoringParameterVO();

        double targetThreshold = monitoringParameterMappingVO.getMonthlyAlertThreshold();
        ParaCalculationDetailsVO paraCalculationDetailsVO = calculateThresholdMonthly(monitoringParameterVO, processingDetails, terminalLimitsVO, lastThreeMonthProcessingDetailsVO,priorMonthProcessingDetailsVO,terminalVO);
        logger.debug("calculation=====" + paraCalculationDetailsVO.getCalculation());
        logger.debug("targetThreshold=====" + targetThreshold);
        if (MonitoringDeviation.Higher.toString().equals(monitoringParameterVO.getMonitoingDeviation()))
        {

            if (paraCalculationDetailsVO.getCalculation() > targetThreshold)
            {
                monitoringOnlyAlertDetailsVO = new MonitoringOnlyAlertDetailsVO();
                monitoringOnlyAlertDetailsVO.setIntimationDepartment(paraCalculationDetailsVO.getCalculationArea());
                monitoringOnlyAlertDetailsVO.setAlertOnly(true);
                monitoringOnlyAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                //monitoringOnlyAlertDetailsVO.setAlertMsg(monitoringParameterMappingVO.getAlertMessage());
                String validaMsg = "";
                if (MonitoringUnit.Percentage.toString().equals(monitoringParameterVO.getMonitoringUnit()))
                {
                    validaMsg = String.valueOf(Functions.round(paraCalculationDetailsVO.getCalculation(), 2) + " %");
                }
                else
                {
                    validaMsg = String.valueOf(Double.valueOf(paraCalculationDetailsVO.getCalculation()).intValue());
                }
                monitoringOnlyAlertDetailsVO.setAlertMsg(getAlertMessage(monitoringParameterMappingVO.getAlertMessage(), validaMsg));
                setMonitoringToEntityFlags(monitoringOnlyAlertDetailsVO, monitoringParameterMappingVO);
            }
        }
        else if (MonitoringDeviation.Lower.toString().equals(monitoringParameterVO.getMonitoingDeviation()))
        {
            if(MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) || MonitoringAlertType.AvgTicketDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
            {
                targetThreshold=targetThreshold*-1;
            }
            if (paraCalculationDetailsVO.getCalculation() < targetThreshold)
            {
                monitoringOnlyAlertDetailsVO = new MonitoringOnlyAlertDetailsVO();
                monitoringOnlyAlertDetailsVO.setIntimationDepartment(paraCalculationDetailsVO.getCalculationArea());
                monitoringOnlyAlertDetailsVO.setAlertOnly(true);
                monitoringOnlyAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                //monitoringOnlyAlertDetailsVO.setAlertMsg(monitoringParameterMappingVO.getAlertMessage());
                String validaMsg = "";
                if (MonitoringUnit.Percentage.toString().equals(monitoringParameterVO.getMonitoringUnit()))
                {
                    validaMsg = String.valueOf(Functions.round(paraCalculationDetailsVO.getCalculation(), 2) + " %");
                }
                else
                {
                    validaMsg = String.valueOf(Double.valueOf(paraCalculationDetailsVO.getCalculation()).intValue());
                }
                monitoringOnlyAlertDetailsVO.setAlertMsg(getAlertMessage(monitoringParameterMappingVO.getAlertMessage(), validaMsg));
                setMonitoringToEntityFlags(monitoringOnlyAlertDetailsVO, monitoringParameterMappingVO);
            }


        }
        if(monitoringOnlyAlertDetailsVO!=null)
        {
            monitoringOnlyAlertDetailsVO.setActualratio(paraCalculationDetailsVO.getCalculation());
        }
        return monitoringOnlyAlertDetailsVO;
    }

    public MonitoringOnlyAlertDetailsVO alertOnlyThresholdAnalysisDaily(MonitoringParameterMappingVO monitoringParameterMappingVO, TerminalProcessingDetailsVO processingDetails, TerminalProcessingDetailsVO priorMonthProcessingDetailsVO, TerminalVO terminalVO, TerminalProcessingDetailsVO lastThreeMonthProcessingDetailsVO, TerminalLimitsVO terminalLimitsVO, TerminalProcessingDetailsVO currentMonthProcessingDetails)
    {
        MonitoringOnlyAlertDetailsVO monitoringOnlyAlertDetailsVO = null;
        MonitoringParameterVO monitoringParameterVO = monitoringParameterMappingVO.getMonitoringParameterVO();
        double targetThreshold = monitoringParameterMappingVO.getAlertThreshold();

        ParaCalculationDetailsVO paraCalculationDetailsVO = calculateThresholdDaily(monitoringParameterVO, processingDetails, priorMonthProcessingDetailsVO, terminalVO, lastThreeMonthProcessingDetailsVO, terminalLimitsVO, currentMonthProcessingDetails);
        logger.debug("calculation=====" + paraCalculationDetailsVO.getCalculation());
        logger.debug("targetThreshold=====" + targetThreshold);
        if (MonitoringDeviation.Higher.toString().equals(monitoringParameterVO.getMonitoingDeviation()))
        {
            if (paraCalculationDetailsVO.getCalculation() > targetThreshold)
            {
                monitoringOnlyAlertDetailsVO = new MonitoringOnlyAlertDetailsVO();
                monitoringOnlyAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());

                String validaMsg = "";
                if (MonitoringUnit.Percentage.toString().equals(monitoringParameterVO.getMonitoringUnit()))
                {
                    validaMsg = String.valueOf(Functions.round(paraCalculationDetailsVO.getCalculation(), 2) + " %");
                }
                else
                {
                    validaMsg = String.valueOf(Double.valueOf(paraCalculationDetailsVO.getCalculation()).intValue());
                }
                monitoringOnlyAlertDetailsVO.setAlertMsg(getAlertMessage(monitoringParameterMappingVO.getAlertMessage(), validaMsg));
                setMonitoringToEntityFlags(monitoringOnlyAlertDetailsVO, monitoringParameterMappingVO);
            }
        }
        else if (MonitoringDeviation.Lower.toString().equals(monitoringParameterVO.getMonitoingDeviation()))
        {
            if(MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) || MonitoringAlertType.AvgTicketDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
            {
                targetThreshold=targetThreshold*-1;
            }
            /*System.out.println("calculation=====" + paraCalculationDetailsVO.getCalculation());
            System.out.println("targetThreshold=====" + targetThreshold);*/
            if (paraCalculationDetailsVO.getCalculation() < targetThreshold)
            {
                monitoringOnlyAlertDetailsVO = new MonitoringOnlyAlertDetailsVO();
                monitoringOnlyAlertDetailsVO.setIntimationDepartment(paraCalculationDetailsVO.getCalculationArea());
                monitoringOnlyAlertDetailsVO.setAlertOnly(true);
                monitoringOnlyAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                String validaMsg = "";
                if (MonitoringUnit.Percentage.toString().equals(monitoringParameterVO.getMonitoringUnit()))
                {
                    validaMsg = String.valueOf(Functions.round(paraCalculationDetailsVO.getCalculation(), 2) + " %");
                }
                else
                {
                    validaMsg = String.valueOf(Double.valueOf(paraCalculationDetailsVO.getCalculation()).intValue());
                }
                monitoringOnlyAlertDetailsVO.setAlertMsg(getAlertMessage(monitoringParameterMappingVO.getAlertMessage(), validaMsg));
                setMonitoringToEntityFlags(monitoringOnlyAlertDetailsVO, monitoringParameterMappingVO);
            }
        }
        if(monitoringOnlyAlertDetailsVO!=null){
            monitoringOnlyAlertDetailsVO.setActualratio(paraCalculationDetailsVO.getCalculation());
        }
        return monitoringOnlyAlertDetailsVO;
    }

    public MonitoringSuspensionAlertDetailsVO suspensionThresholdAnalysis(MonitoringParameterMappingVO monitoringParameterMappingVO, TerminalProcessingDetailsVO processingDetails, TerminalProcessingDetailsVO priorMonthProcessingDetails, TerminalVO terminalVO, TerminalProcessingDetailsVO lastThreeMonthProcessingDetailsVO, TerminalLimitsVO terminalLimitsVO, TerminalProcessingDetailsVO currentMonthProcessingDetails)
    {
        MonitoringSuspensionAlertDetailsVO monitoringSuspensionAlertDetailsVO = null;
        MonitoringParameterVO monitoringParameterVO = monitoringParameterMappingVO.getMonitoringParameterVO();
        double targetThreshold = monitoringParameterMappingVO.getSuspensionThreshold();
        ParaCalculationDetailsVO paraCalculationDetailsVO = calculateThresholdWithLiveData(monitoringParameterVO, processingDetails, priorMonthProcessingDetails, terminalVO, lastThreeMonthProcessingDetailsVO, terminalLimitsVO, currentMonthProcessingDetails);

        if (MonitoringDeviation.Higher.toString().equals(monitoringParameterVO.getMonitoingDeviation()))
        {
            if (paraCalculationDetailsVO.getCalculation() > targetThreshold)
            {
                monitoringSuspensionAlertDetailsVO = new MonitoringSuspensionAlertDetailsVO();
                monitoringSuspensionAlertDetailsVO.setIntimationDepartment(paraCalculationDetailsVO.getCalculationArea());
                monitoringSuspensionAlertDetailsVO.setSuspension(true);
                monitoringSuspensionAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                //monitoringSuspensionAlertDetailsVO.setAlertMsg(monitoringParameterMappingVO.getAlertMessage());
                String validaMsg = "";
                if (MonitoringUnit.Percentage.toString().equals(monitoringParameterVO.getMonitoringUnit()))
                {
                    validaMsg = String.valueOf(Functions.round(paraCalculationDetailsVO.getCalculation(), 2) + " %");
                }
                else
                {
                    validaMsg = String.valueOf(Double.valueOf(paraCalculationDetailsVO.getCalculation()).intValue());
                }
                monitoringSuspensionAlertDetailsVO.setAlertMsg(getAlertMessage(monitoringParameterMappingVO.getAlertMessage(), validaMsg));
                setMonitoringToEntityFlags(monitoringSuspensionAlertDetailsVO, monitoringParameterMappingVO);
            }
        }
        else if (MonitoringDeviation.Lower.toString().equals(monitoringParameterVO.getMonitoingDeviation()))
        {
            if (paraCalculationDetailsVO.getCalculation() < targetThreshold)
            {
                monitoringSuspensionAlertDetailsVO = new MonitoringSuspensionAlertDetailsVO();
                monitoringSuspensionAlertDetailsVO.setIntimationDepartment(paraCalculationDetailsVO.getCalculationArea());
                monitoringSuspensionAlertDetailsVO.setSuspension(true);
                monitoringSuspensionAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                //monitoringSuspensionAlertDetailsVO.setAlertMsg(monitoringParameterMappingVO.getAlertMessage());
                String validaMsg = "";
                if (MonitoringUnit.Percentage.toString().equals(monitoringParameterVO.getMonitoringUnit()))
                {
                    validaMsg = String.valueOf(Functions.round(paraCalculationDetailsVO.getCalculation(), 2) + " %");
                }
                else
                {
                    validaMsg = String.valueOf(Double.valueOf(paraCalculationDetailsVO.getCalculation()).intValue());
                }
                monitoringSuspensionAlertDetailsVO.setAlertMsg(getAlertMessage(monitoringParameterMappingVO.getAlertMessage(), validaMsg));
                setMonitoringToEntityFlags(monitoringSuspensionAlertDetailsVO, monitoringParameterMappingVO);
            }
        }
        return monitoringSuspensionAlertDetailsVO;
    }

    public MonitoringSuspensionAlertDetailsVO suspensionThresholdMonthly(MonitoringParameterMappingVO monitoringParameterMappingVO, TerminalProcessingDetailsVO processingDetails, TerminalLimitsVO terminalLimitsVO, TerminalProcessingDetailsVO lastThreeMonthProcessingDetailsVO,TerminalVO terminalVO,TerminalProcessingDetailsVO priorMonthProcessingDetailsVO)
    {
        MonitoringSuspensionAlertDetailsVO monitoringSuspensionAlertDetailsVO = null;
        MonitoringParameterVO monitoringParameterVO = monitoringParameterMappingVO.getMonitoringParameterVO();
        double targetThreshold = monitoringParameterMappingVO.getMonthlySuspensionThreshold();
        ParaCalculationDetailsVO paraCalculationDetailsVO = calculateThresholdMonthly(monitoringParameterVO, processingDetails, terminalLimitsVO, lastThreeMonthProcessingDetailsVO,priorMonthProcessingDetailsVO,terminalVO);

        if (MonitoringDeviation.Higher.toString().equals(monitoringParameterVO.getMonitoingDeviation()))
        {
            TerminalManager terminalManager = new TerminalManager();
            if (paraCalculationDetailsVO.getCalculation() > targetThreshold)
            {
                try
                {
                    terminalManager.doInactiveTerminalProcessing(terminalVO.getTerminalId());
                    monitoringSuspensionAlertDetailsVO = new MonitoringSuspensionAlertDetailsVO();
                    monitoringSuspensionAlertDetailsVO.setIntimationDepartment(paraCalculationDetailsVO.getCalculationArea());
                    monitoringSuspensionAlertDetailsVO.setSuspension(true);
                    monitoringSuspensionAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                    String validaMsg = "";
                    if (MonitoringUnit.Percentage.toString().equals(monitoringParameterVO.getMonitoringUnit()))
                    {
                        validaMsg = String.valueOf(Functions.round(paraCalculationDetailsVO.getCalculation(), 2) + " %");
                    }
                    else
                    {
                        validaMsg = String.valueOf(Double.valueOf(paraCalculationDetailsVO.getCalculation()).intValue());
                    }
                    monitoringSuspensionAlertDetailsVO.setAlertMsg(getAlertMessage(monitoringParameterMappingVO.getAlertMessage(), validaMsg));
                    setMonitoringToEntityFlags(monitoringSuspensionAlertDetailsVO, monitoringParameterMappingVO);
                }
                catch (Exception e)
                {
                    logger.error("Exception--->", e);
                }

            }
        }
        else if (MonitoringDeviation.Lower.toString().equals(monitoringParameterVO.getMonitoingDeviation()))
        {
            if(MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) || MonitoringAlertType.AvgTicketDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
            {
                targetThreshold=targetThreshold*-1;
            }
            if (paraCalculationDetailsVO.getCalculation() < targetThreshold)
            {
                TerminalManager terminalManager = new TerminalManager();
                try
                {
                    terminalManager.doInactiveTerminalProcessing(terminalVO.getTerminalId());
                    monitoringSuspensionAlertDetailsVO = new MonitoringSuspensionAlertDetailsVO();
                    monitoringSuspensionAlertDetailsVO.setIntimationDepartment(paraCalculationDetailsVO.getCalculationArea());
                    monitoringSuspensionAlertDetailsVO.setSuspension(true);
                    monitoringSuspensionAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                    //monitoringSuspensionAlertDetailsVO.setAlertMsg(monitoringParameterMappingVO.getAlertMessage());
                    String validaMsg = "";
                    if (MonitoringUnit.Percentage.toString().equals(monitoringParameterVO.getMonitoringUnit()))
                    {
                        validaMsg = String.valueOf(Functions.round(paraCalculationDetailsVO.getCalculation(), 2) + " %");
                    }
                    else
                    {
                        validaMsg = String.valueOf(Double.valueOf(paraCalculationDetailsVO.getCalculation()).intValue());
                    }
                    monitoringSuspensionAlertDetailsVO.setAlertMsg(getAlertMessage(monitoringParameterMappingVO.getAlertMessage(), validaMsg));
                    setMonitoringToEntityFlags(monitoringSuspensionAlertDetailsVO, monitoringParameterMappingVO);
                }
                catch (Exception e)
                {
                    logger.error("Exception--->", e);
                }
            }

        }
        return monitoringSuspensionAlertDetailsVO;
    }

    public MonitoringSuspensionAlertDetailsVO suspensionThresholdWeekly(MonitoringParameterMappingVO monitoringParameterMappingVO, TerminalProcessingDetailsVO processingDetails, TerminalLimitsVO terminalLimitsVO, TerminalProcessingDetailsVO lastThreeMonthProcessingDetailsVO,TerminalVO terminalVO,TerminalProcessingDetailsVO priorMonthProcessingDetailsVO,TerminalProcessingDetailsVO currentMonthProcessingDetails)
    {
        MonitoringSuspensionAlertDetailsVO monitoringSuspensionAlertDetailsVO = null;
        MonitoringParameterVO monitoringParameterVO = monitoringParameterMappingVO.getMonitoringParameterVO();
        double targetThreshold = monitoringParameterMappingVO.getWeeklySuspensionThreshold();
        ParaCalculationDetailsVO paraCalculationDetailsVO = calculateThresholdWeekly(monitoringParameterVO, processingDetails, terminalLimitsVO, lastThreeMonthProcessingDetailsVO,priorMonthProcessingDetailsVO,currentMonthProcessingDetails,terminalVO);

        if (MonitoringDeviation.Higher.toString().equals(monitoringParameterVO.getMonitoingDeviation()))
        {
            TerminalManager terminalManager = new TerminalManager();
            if (paraCalculationDetailsVO.getCalculation() > targetThreshold)
            {
                try
                {
                    terminalManager.doInactiveTerminalProcessing(terminalVO.getTerminalId());
                    monitoringSuspensionAlertDetailsVO = new MonitoringSuspensionAlertDetailsVO();
                    monitoringSuspensionAlertDetailsVO.setIntimationDepartment(paraCalculationDetailsVO.getCalculationArea());
                    monitoringSuspensionAlertDetailsVO.setSuspension(true);
                    monitoringSuspensionAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                    String validaMsg = "";
                    if (MonitoringUnit.Percentage.toString().equals(monitoringParameterVO.getMonitoringUnit()))
                    {
                        validaMsg = String.valueOf(Functions.round(paraCalculationDetailsVO.getCalculation(), 2) + " %");
                    }
                    else
                    {
                        validaMsg = String.valueOf(Double.valueOf(paraCalculationDetailsVO.getCalculation()).intValue());
                    }
                    monitoringSuspensionAlertDetailsVO.setAlertMsg(getAlertMessage(monitoringParameterMappingVO.getAlertMessage(), validaMsg));
                    setMonitoringToEntityFlags(monitoringSuspensionAlertDetailsVO, monitoringParameterMappingVO);
                }
                catch (Exception e)
                {
                    logger.error("Exception--->", e);
                }

            }
        }
        else if (MonitoringDeviation.Lower.toString().equals(monitoringParameterVO.getMonitoingDeviation()))
        {
            if(MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) || MonitoringAlertType.AvgTicketDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
            {
                targetThreshold=targetThreshold*-1;
            }
            if(paraCalculationDetailsVO.getCalculation() < targetThreshold)
            {
                TerminalManager terminalManager = new TerminalManager();
                try
                {
                    terminalManager.doInactiveTerminalProcessing(terminalVO.getTerminalId());
                    monitoringSuspensionAlertDetailsVO = new MonitoringSuspensionAlertDetailsVO();
                    monitoringSuspensionAlertDetailsVO.setIntimationDepartment(paraCalculationDetailsVO.getCalculationArea());
                    monitoringSuspensionAlertDetailsVO.setSuspension(true);
                    monitoringSuspensionAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                    //monitoringSuspensionAlertDetailsVO.setAlertMsg(monitoringParameterMappingVO.getAlertMessage());
                    String validaMsg = "";
                    if (MonitoringUnit.Percentage.toString().equals(monitoringParameterVO.getMonitoringUnit()))
                    {
                        validaMsg = String.valueOf(Functions.round(paraCalculationDetailsVO.getCalculation(), 2) + " %");
                    }
                    else
                    {
                        validaMsg = String.valueOf(Double.valueOf(paraCalculationDetailsVO.getCalculation()).intValue());
                    }
                    monitoringSuspensionAlertDetailsVO.setAlertMsg(getAlertMessage(monitoringParameterMappingVO.getAlertMessage(), validaMsg));
                    setMonitoringToEntityFlags(monitoringSuspensionAlertDetailsVO, monitoringParameterMappingVO);
                }
                catch (Exception e)
                {
                    logger.error("Exception--->", e);
                }
            }
            if(monitoringSuspensionAlertDetailsVO!=null){
                monitoringSuspensionAlertDetailsVO.setActualratio(paraCalculationDetailsVO.getCalculation());
            }

        }

        return monitoringSuspensionAlertDetailsVO;
    }

    public MonitoringSuspensionAlertDetailsVO suspensionThresholdAnalysisDaily(MonitoringParameterMappingVO monitoringParameterMappingVO, TerminalProcessingDetailsVO processingDetails, TerminalProcessingDetailsVO priorMonthProcessingDetails, TerminalVO terminalVO, TerminalProcessingDetailsVO lastThreeMonthProcessingDetailsVO, TerminalLimitsVO terminalLimitsVO, TerminalProcessingDetailsVO currentMonthProcessingDetails)
    {
        MonitoringSuspensionAlertDetailsVO monitoringSuspensionAlertDetailsVO = null;
        MonitoringParameterVO monitoringParameterVO = monitoringParameterMappingVO.getMonitoringParameterVO();
        double targetThreshold = monitoringParameterMappingVO.getSuspensionThreshold();
        ParaCalculationDetailsVO paraCalculationDetailsVO = calculateThresholdDaily(monitoringParameterVO, processingDetails, priorMonthProcessingDetails, terminalVO, lastThreeMonthProcessingDetailsVO, terminalLimitsVO, currentMonthProcessingDetails);

        if (MonitoringDeviation.Higher.toString().equals(monitoringParameterVO.getMonitoingDeviation()))
        {
            TerminalManager terminalManager = new TerminalManager();
            if (paraCalculationDetailsVO.getCalculation() > targetThreshold)
            {
                try
                {
                    terminalManager.doInactiveTerminalProcessing(terminalVO.getTerminalId());

                    monitoringSuspensionAlertDetailsVO = new MonitoringSuspensionAlertDetailsVO();
                    monitoringSuspensionAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                    String validaMsg = "";
                    if (MonitoringUnit.Percentage.toString().equals(monitoringParameterVO.getMonitoringUnit()))
                    {
                        validaMsg = String.valueOf(Functions.round(paraCalculationDetailsVO.getCalculation(), 2) + " %");
                    }
                    else
                    {
                        validaMsg = String.valueOf(Double.valueOf(paraCalculationDetailsVO.getCalculation()).intValue());
                    }
                    monitoringSuspensionAlertDetailsVO.setAlertMsg(getAlertMessage(monitoringParameterMappingVO.getAlertMessage(), validaMsg));
                    setMonitoringToEntityFlags(monitoringSuspensionAlertDetailsVO, monitoringParameterMappingVO);
                }
                catch (Exception e)
                {
                    logger.error("Exception--->", e);
                }

            }
        }
        else if (MonitoringDeviation.Lower.toString().equals(monitoringParameterVO.getMonitoingDeviation()))
        {
            if(MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) || MonitoringAlertType.AvgTicketDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
            {
                targetThreshold=targetThreshold*-1;
            }
            if (paraCalculationDetailsVO.getCalculation() < targetThreshold)
            {
                TerminalManager terminalManager = new TerminalManager();
                try
                {
                    terminalManager.doInactiveTerminalProcessing(terminalVO.getTerminalId());
                    monitoringSuspensionAlertDetailsVO = new MonitoringSuspensionAlertDetailsVO();
                    monitoringSuspensionAlertDetailsVO.setIntimationDepartment(paraCalculationDetailsVO.getCalculationArea());
                    monitoringSuspensionAlertDetailsVO.setSuspension(true);
                    monitoringSuspensionAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                    //monitoringSuspensionAlertDetailsVO.setAlertMsg(monitoringParameterMappingVO.getAlertMessage());
                    String validaMsg = "";
                    if (MonitoringUnit.Percentage.toString().equals(monitoringParameterVO.getMonitoringUnit()))
                    {
                        validaMsg = String.valueOf(Functions.round(paraCalculationDetailsVO.getCalculation(), 2) + " %");
                    }
                    else
                    {
                        validaMsg = String.valueOf(Double.valueOf(paraCalculationDetailsVO.getCalculation()).intValue());
                    }
                    monitoringSuspensionAlertDetailsVO.setAlertMsg(getAlertMessage(monitoringParameterMappingVO.getAlertMessage(), validaMsg));
                    setMonitoringToEntityFlags(monitoringSuspensionAlertDetailsVO, monitoringParameterMappingVO);
                }
                catch (Exception e)
                {
                    logger.error("Exception--->", e);
                }
            }
        }

        return monitoringSuspensionAlertDetailsVO;
    }

    private ParaCalculationDetailsVO calculateThresholdWithLiveData(MonitoringParameterVO monitoringParameterVO, TerminalProcessingDetailsVO processingDetails, TerminalProcessingDetailsVO priorMonthProcessingDetailsVO, TerminalVO terminalVO, TerminalProcessingDetailsVO lastThreeMonthProcessingDetailsVO, TerminalLimitsVO terminalLimitsVO, TerminalProcessingDetailsVO currentMonthProcessingDetails)
    {
        double calculation = 0.00;
        String calculationArea = "";
        ParaCalculationDetailsVO paraCalculationDetailsVO = new ParaCalculationDetailsVO();
        RatioCalculationHelper ratioCalculationHelper = new RatioCalculationHelper();
        if (MonitoringAlertType.Ratio.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
        {
            calculation = calculateRatio(monitoringParameterVO, processingDetails);
        }
        else
        {
            if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Chargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
            {
                calculation = (1.00 * processingDetails.getChargebackCount());

            }
            else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Reversed.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.RefundDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
            {
                //Refunds ratio - volume (This month refunds/Last month sales)
                calculation = ratioCalculationHelper.calculateRefundDeviation(priorMonthProcessingDetailsVO.getSalesAmount(), processingDetails.getRefundAmount()+processingDetails.getOldTransRefundAmount());

            }
            else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.FirstSubmission.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.ProcessingSetup.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
            {
                SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                long day = 0;
                try
                {
                    day = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(terminalVO.getActivationDate())), targetFormat.format(new Date()));
                }
                catch (Exception e)
                {

                }
                calculation = day;

            }
            else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.InactivityPeriod.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.ProcessingSetup.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
            {
                SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                long day = 0;
                try
                {
                    day = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(terminalVO.getActivationDate())), processingDetails.getLastTransactionDate());
                }
                catch (Exception e)
                {

                }
                calculation = day;

            }
            else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
            {
                //Lower or higher sales than contracted
                double monthlyActualSalesAmount = currentMonthProcessingDetails.getSalesAmount();
                if (monthlyActualSalesAmount > 0)
                {
                    calculation = (((monthlyActualSalesAmount - terminalLimitsVO.getMonthlyAmountLimit()) / terminalLimitsVO.getMonthlyAmountLimit()) * 100);
                }

            }
            else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.AvgTicketAmount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.AvgTicketDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
            {
                //Transaction higher than contracted - New merchants
                double contractedAvgTicketAmount = 100.00;
                double currentDayActualAvgTicketAmount = 0.00;

                currentDayActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(processingDetails.getSalesAmount(), processingDetails.getSalesCount());
                calculation = ((Functions.roundDBL(currentDayActualAvgTicketAmount, 2) - Functions.roundDBL(contractedAvgTicketAmount, 2)) / Functions.roundDBL(contractedAvgTicketAmount, 2)) * 100;

            }
            else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.AvgTicketAmount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.AvgTicketDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.Old.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
            {
                //Transaction higher than contracted - Old merchants
                double lastThreeMonthActualAvgTicketAmount = 0.00;
                double currentDayActualAvgTicketAmount = 0.00;

                lastThreeMonthActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(lastThreeMonthProcessingDetailsVO.getSalesAmount(), lastThreeMonthProcessingDetailsVO.getSalesCount());
                currentDayActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(processingDetails.getSalesAmount(), processingDetails.getSalesCount());
                calculation = ((Functions.roundDBL(currentDayActualAvgTicketAmount, 2) - Functions.roundDBL(lastThreeMonthActualAvgTicketAmount, 2)) / Functions.roundDBL(lastThreeMonthActualAvgTicketAmount, 2)) * 100;

            }
        }
        paraCalculationDetailsVO.setCalculation(calculation);
        paraCalculationDetailsVO.setCalculationArea(calculationArea);
        return paraCalculationDetailsVO;


    }

    private ParaCalculationDetailsVO calculateThresholdWeekly(MonitoringParameterVO monitoringParameterVO, TerminalProcessingDetailsVO processingDetails, TerminalLimitsVO terminalLimitsVO, TerminalProcessingDetailsVO lastThreeMonthProcessingDetailsVO,TerminalProcessingDetailsVO priorMonthProcessingDetailsVO,TerminalProcessingDetailsVO currentMonthProcessingDetailsVO,TerminalVO terminalVO)
    {
        double calculation = 0.00;
        String calculationArea = "";
        ParaCalculationDetailsVO paraCalculationDetailsVO = new ParaCalculationDetailsVO();
        RatioCalculationHelper ratioCalculationHelper = new RatioCalculationHelper();
        if (MonitoringAlertType.Ratio.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
        {
            calculation = calculateRatio(monitoringParameterVO, processingDetails);
        }
        else
        {
            if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Reversed.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
            {
                //Refunds ratio - volume (This month refunds/Last month sales)
                calculation = ratioCalculationHelper.calculateRefundDeviation(priorMonthProcessingDetailsVO.getSalesAmount(), currentMonthProcessingDetailsVO.getOldTransRefundAmount());
            }
            else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.FirstSubmission.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.ProcessingSetup.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
            {
                //System.out.println("FirstSubmission======================"+processingDetails.getFirstTransactionDate());
                if(!functions.isValueNull(processingDetails.getFirstTransactionDate()))
                {
                    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long day = 0;
                    try
                    {
                       // System.out.println("terminal activation time===="+terminalVO.getActivationDate());
                        day = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(terminalVO.getActivationDate())), targetFormat.format(new Date()));
                    }
                    catch (Exception e)
                    {
                        logger.error("Exception--->", e);
                    }
                    calculation = day;
                }
                else
                {
                    logger.debug("Transaction founds on terminal");
                }
            }
            else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.InactivityPeriod.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.ProcessingSetup.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
            {
                //Inactivity period calculation for both new terminals
                if(functions.isValueNull(processingDetails.getLastTransactionDate()))
                {
                    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long day = 0;
                    long day1 = 0;
                    try
                    {
                        day1 = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(processingDetails.getFirstTransactionDate())),targetFormat.format(new Date()));
                        if(day1<=90)
                        {
                            day = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(processingDetails.getLastTransactionDate())),targetFormat.format(new Date()));
                            calculation = day;
                        }
                    }
                    catch (Exception e)
                    {
                        logger.error("Exception--->", e);
                    }
                }
                else
                {
                    logger.debug("Last transaction not fond on terminal");
                }
            }
            else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.InactivityPeriod.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.ProcessingSetup.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.Old.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
            {
                //Inactivity period calculation for both old terminals
                if(functions.isValueNull(processingDetails.getLastTransactionDate()))
                {
                    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long day = 0;
                    long day1 = 0;
                    try
                    {
                        day1 = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(processingDetails.getFirstTransactionDate())),targetFormat.format(new Date()));
                        if(day1>90)
                        {
                            day = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(processingDetails.getLastTransactionDate())),targetFormat.format(new Date()));
                            calculation = day;
                        }
                    }
                    catch (Exception e)
                    {
                        logger.error("Exception--->", e);
                    }
                }
                else
                {
                    logger.debug("Last transaction not fond on terminal");
                }
            }
            else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
            {
                //Lower or higher sales than contracted -New Terminals
                double weeklyActualSalesAmount = processingDetails.getSalesAmount();
                double weeklyTargetSalesAmount=terminalLimitsVO.getWeeklyAmountLimit();
                logger.debug("weeklyTargetSalesAmount===========" + weeklyTargetSalesAmount);
                logger.debug("monthlyActualSalesAmount===========" + weeklyActualSalesAmount);
                if(functions.isValueNull(processingDetails.getFirstTransactionDate()))
                {
                    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long day = 0;
                    try
                    {
                        day = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(processingDetails.getFirstTransactionDate())),targetFormat.format(new Date()));
                    }
                    catch (Exception e)
                    {
                        logger.error("Exception--->", e);
                    }
                    if(day<90)
                    {
                        if (weeklyTargetSalesAmount > 0)
                        {
                            calculation = (((weeklyActualSalesAmount /weeklyTargetSalesAmount) * 100));
                            calculation=calculation-100;

                        }
                    }
                }
            }
            else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.Old.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
            {
                //Lower or higher sales than contracted-Old Terminals
                if(functions.isValueNull(processingDetails.getFirstTransactionDate()))
                {
                    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long day = 0;
                    try
                    {
                        day = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(processingDetails.getFirstTransactionDate())),targetFormat.format(new Date()));
                    }
                    catch (Exception e)
                    {
                        logger.error("Exception--->", e);
                    }
                    if(day>90)
                    {
                        double weeklyActualSalesAmount = processingDetails.getSalesAmount();
                        double monthlyTargetSalesAmount=lastThreeMonthProcessingDetailsVO.getSalesAmount()/3;
                        double weeklyTargetSalesAmount=monthlyTargetSalesAmount/4;
                        logger.debug("Current Week actual Sales======" + weeklyActualSalesAmount);
                        logger.debug("Current Week taget sales Sales======" + weeklyTargetSalesAmount);

                        if (weeklyTargetSalesAmount > 0)
                        {
                            calculation = ((weeklyActualSalesAmount/weeklyTargetSalesAmount) * 100);
                            calculation=calculation-100;
                        }
                    }
                }
                logger.debug("calculation=======" + calculation);
            }
            else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.AvgTicketAmount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.AvgTicketDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
            {
                //Transaction higher than contracted - New merchants
                double contractedAvgTicketAmount =terminalLimitsVO.getWeeklyAvgTicketAmount();
                double currentDayActualAvgTicketAmount = 0.00;
                if(functions.isValueNull(processingDetails.getFirstTransactionDate()))
                {
                    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long day = 0;
                    try
                    {
                        day = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(processingDetails.getFirstTransactionDate())),targetFormat.format(new Date()));
                    }
                    catch (Exception e)
                    {
                        logger.error("Exception--->", e);
                    }
                    if(day<90)
                    {
                        currentDayActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(processingDetails.getSalesAmount(), processingDetails.getSalesCount());
                        calculation = (currentDayActualAvgTicketAmount/contractedAvgTicketAmount) * 100;
                        calculation=calculation-100;
                    }
                    logger.debug("contractedAvgTicketAmount====" + contractedAvgTicketAmount);
                    logger.debug("currentDayActualAvgTicketAmount====" + currentDayActualAvgTicketAmount);
                    logger.debug("calculation====" + calculation);
                }
            }
            else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.AvgTicketAmount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.AvgTicketDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.Old.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
            {
                //Transaction higher than contracted - Old merchants
                double lastThreeMonthActualAvgTicketAmount = 0.00;
                double currentDayActualAvgTicketAmount = 0.00;
                if(functions.isValueNull(processingDetails.getFirstTransactionDate()))
                {
                    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long day = 0;
                    try
                    {
                        day = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(processingDetails.getFirstTransactionDate())),targetFormat.format(new Date()));
                    }
                    catch (Exception e)
                    {
                        logger.error("Exception--->", e);
                    }
                    if(day>90)
                    {
                        lastThreeMonthActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(lastThreeMonthProcessingDetailsVO.getSalesAmount(), lastThreeMonthProcessingDetailsVO.getSalesCount());
                        currentDayActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(processingDetails.getSalesAmount(), processingDetails.getSalesCount());
                        calculation = (currentDayActualAvgTicketAmount/lastThreeMonthActualAvgTicketAmount) * 100;
                        calculation=calculation-100;
                    }
                    logger.debug("lastThreeMonthActualAvgTicketAmount==" + lastThreeMonthActualAvgTicketAmount);
                    logger.debug("currentDayActualAvgTicketAmount==" + currentDayActualAvgTicketAmount);
                    logger.debug("calculation==" + calculation);
                }
            }
        }
        paraCalculationDetailsVO.setCalculation(calculation);
        paraCalculationDetailsVO.setCalculationArea(calculationArea);
        return paraCalculationDetailsVO;


    }

    private ParaCalculationDetailsVO calculateThresholdMonthly(MonitoringParameterVO monitoringParameterVO, TerminalProcessingDetailsVO processingDetails, TerminalLimitsVO terminalLimitsVO, TerminalProcessingDetailsVO lastThreeMonthProcessingDetailsVO,TerminalProcessingDetailsVO priorMonthProcessingDetailsVO,TerminalVO terminalVO)
    {
        double calculation = 0.00;
        String calculationArea = "";
        ParaCalculationDetailsVO paraCalculationDetailsVO = new ParaCalculationDetailsVO();
        RatioCalculationHelper ratioCalculationHelper = new RatioCalculationHelper();
        if (MonitoringAlertType.Ratio.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
        {
            calculation = calculateRatio(monitoringParameterVO, processingDetails);
        }
        else
        {
            if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Reversed.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
            {
                //Refunds ratio - volume (This month refunds/Last month sales)
                calculation = ratioCalculationHelper.calculateRefundDeviation(priorMonthProcessingDetailsVO.getSalesAmount(),processingDetails.getOldTransRefundAmount());
            }
            else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.FirstSubmission.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.ProcessingSetup.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
            {
                //System.out.println("FirstSubmission======================"+processingDetails.getFirstTransactionDate());
                if(!functions.isValueNull(processingDetails.getFirstTransactionDate()))
                {
                    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long day = 0;
                    try
                    {
                        logger.debug("terminal activation time====" + terminalVO.getActivationDate());
                        day = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(terminalVO.getActivationDate())), targetFormat.format(new Date()));
                    }
                    catch (Exception e)
                    {
                        logger.error("Exception--->", e);
                    }
                    calculation = day;
                }
                else
                {
                    logger.debug("Transaction founds on terminal");
                }
            }
            else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.InactivityPeriod.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.ProcessingSetup.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
            {
                //Inactivity period calculation for both new terminals
                if(functions.isValueNull(processingDetails.getLastTransactionDate()))
                {
                    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long day = 0;
                    long day1 = 0;
                    try
                    {
                        day1 = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(processingDetails.getFirstTransactionDate())),targetFormat.format(new Date()));
                        if(day1<=90)
                        {
                            day = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(processingDetails.getLastTransactionDate())),targetFormat.format(new Date()));
                            calculation = day;
                        }
                    }
                    catch (Exception e)
                    {
                        logger.error("Exception--->", e);
                    }
                }
                else
                {
                    logger.debug("Last transaction not fond on terminal");
                }
            }
            else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.InactivityPeriod.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.ProcessingSetup.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.Old.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
            {
                //Inactivity period calculation for both old terminals
                if(functions.isValueNull(processingDetails.getLastTransactionDate()))
                {
                    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long day = 0;
                    long day1 = 0;
                    try
                    {
                        day1 = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(processingDetails.getFirstTransactionDate())),targetFormat.format(new Date()));
                        if(day1>90)
                        {
                            day = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(processingDetails.getLastTransactionDate())),targetFormat.format(new Date()));
                            calculation = day;
                        }
                    }
                    catch (Exception e)
                    {
                        logger.error("Exception--->",e);
                    }
                }
                else
                {
                    logger.debug("Last transaction not fond on terminal");
                }
            }
            else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
            {
                //Lower or higher sales than contracted -New Terminals
                double monthlyActualSalesAmount =processingDetails.getSalesAmount();
                double monthlyTargetSalesAmount =terminalLimitsVO.getMonthlyAmountLimit();
                if(functions.isValueNull(processingDetails.getFirstTransactionDate()))
                {
                    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long day = 0;
                    try
                    {
                        day = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(processingDetails.getFirstTransactionDate())),targetFormat.format(new Date()));
                    }
                    catch (Exception e)
                    {
                        logger.error("Exception--->",e);
                    }
                    if(day<90)
                    {
                        if (monthlyTargetSalesAmount>0)
                        {
                            calculation = ((monthlyActualSalesAmount / monthlyTargetSalesAmount) * 100);
                            calculation=calculation-100;
                        }
                    }
                }
            }
            else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.Old.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
            {
                //Lower or higher sales than contracted-Old Terminals
                if(functions.isValueNull(processingDetails.getFirstTransactionDate()))
                {
                    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long day = 0;
                    try
                    {
                        day = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(processingDetails.getFirstTransactionDate())),targetFormat.format(new Date()));
                    }
                    catch (Exception e)
                    {
                        logger.error("Exception--->",e);
                    }
                    if(day>90)
                    {
                        double monthlyActualSalesAmount = processingDetails.getSalesAmount();
                        double monthlyTargetSalesAmount = lastThreeMonthProcessingDetailsVO.getSalesAmount()/3;
                        logger.debug("monthlyActualSalesAmount=====" + monthlyActualSalesAmount);
                        logger.debug("monthlyTargetSalesAmount=====" + monthlyTargetSalesAmount);
                        if (monthlyTargetSalesAmount > 0)
                        {
                            calculation = ((monthlyActualSalesAmount/monthlyTargetSalesAmount) * 100);
                            calculation=calculation-100;
                        }
                    }
                }
                logger.debug("calculation==========" + calculation);
            }
            else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.AvgTicketAmount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.AvgTicketDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
            {
                //Transaction higher than contracted - New merchants
                double contractedAvgTicketAmount = terminalLimitsVO.getMonthlyAvgTicketAmount();
                double currentDayActualAvgTicketAmount = 0.00;

                if(functions.isValueNull(processingDetails.getFirstTransactionDate()))
                {
                    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long day = 0;
                    try
                    {
                        day = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(processingDetails.getFirstTransactionDate())),targetFormat.format(new Date()));
                    }
                    catch (Exception e)
                    {
                        logger.error("Exception--->",e);
                    }
                    if(day<90)
                    {
                        currentDayActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(processingDetails.getSalesAmount(),processingDetails.getSalesCount());
                        calculation = (currentDayActualAvgTicketAmount/contractedAvgTicketAmount) * 100;
                        calculation=calculation-100;
                    }
                    logger.debug("contractedAvgTicketAmount=====" + contractedAvgTicketAmount);
                    logger.debug("currentMonthActualAvgTicketAmount=====" + currentDayActualAvgTicketAmount);
                    logger.debug("calculation=====" + calculation);
                }
            }
            else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.AvgTicketAmount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.AvgTicketDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.Old.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
            {
                //Transaction higher than contracted - Old merchants
                double lastThreeMonthActualAvgTicketAmount = 0.00;
                double currentDayActualAvgTicketAmount = 0.00;

                if(functions.isValueNull(processingDetails.getFirstTransactionDate()))
                {
                    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long day = 0;
                    try
                    {
                        day = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(processingDetails.getFirstTransactionDate())),targetFormat.format(new Date()));
                    }
                    catch (Exception e)
                    {
                        logger.error("Exception--->",e);
                    }
                    if(day>90)
                    {
                        lastThreeMonthActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(lastThreeMonthProcessingDetailsVO.getSalesAmount(), lastThreeMonthProcessingDetailsVO.getSalesCount());
                        currentDayActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(processingDetails.getSalesAmount(), processingDetails.getSalesCount());
                        calculation = (currentDayActualAvgTicketAmount/lastThreeMonthActualAvgTicketAmount) * 100;
                        calculation=calculation-100;
                    }
                    logger.debug("lastThreeMonthActualAvgTicketAmount=====" + lastThreeMonthActualAvgTicketAmount);
                    logger.debug("currentMonthActualAvgTicketAmount=====" + currentDayActualAvgTicketAmount);
                    logger.debug("calculation=====" + calculation);
                }

            }
        }
        paraCalculationDetailsVO.setCalculation(calculation);
        paraCalculationDetailsVO.setCalculationArea(calculationArea);
        return paraCalculationDetailsVO;
    }
    private ParaCalculationDetailsVO calculateThresholdDaily(MonitoringParameterVO monitoringParameterVO, TerminalProcessingDetailsVO processingDetails, TerminalProcessingDetailsVO priorMonthProcessingDetailsVO, TerminalVO terminalVO, TerminalProcessingDetailsVO lastThreeMonthProcessingDetailsVO, TerminalLimitsVO terminalLimitsVO, TerminalProcessingDetailsVO currentMonthProcessingDetails)
    {
        double calculation = 0.00;
        String calculationArea = "";
        ParaCalculationDetailsVO paraCalculationDetailsVO = new ParaCalculationDetailsVO();
        RatioCalculationHelper ratioCalculationHelper = new RatioCalculationHelper();
        if (MonitoringAlertType.Ratio.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
        {
            calculation = calculateRatio(monitoringParameterVO, processingDetails);
        }
        else
        {
            if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Reversed.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
            {
                //Refunds ratio - volume (This month refunds/Last month sales)
                calculation = ratioCalculationHelper.calculateRefundDeviation(priorMonthProcessingDetailsVO.getSalesAmount(), currentMonthProcessingDetails.getOldTransRefundAmount());
            }
            else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.FirstSubmission.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.ProcessingSetup.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
            {
                //System.out.println("FirstSubmission======================"+processingDetails.getFirstTransactionDate());
                if(!functions.isValueNull(processingDetails.getFirstTransactionDate()))
                {
                    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long day = 0;
                    try
                    {
                        //System.out.println("terminal activation time===="+terminalVO.getActivationDate());
                        day = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(terminalVO.getActivationDate())), targetFormat.format(new Date()));
                    }
                    catch (Exception e)
                    {
                        logger.error("Exception--->", e);
                    }
                    calculation = day;
                }
                else
                {
                    logger.debug("Transaction founds on terminal");
                }
            }
            else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.InactivityPeriod.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.ProcessingSetup.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
            {
                //Inactivity period calculation for both new terminals
                if(functions.isValueNull(processingDetails.getLastTransactionDate()))
                {
                    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    long day = 0;
                    long day1 = 0;
                    try
                    {
                        day1 = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(processingDetails.getFirstTransactionDate())),targetFormat.format(new Date()));
                        if(day1<=90)
                        {
                            day = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(processingDetails.getLastTransactionDate())),targetFormat.format(new Date()));
                            calculation = day;
                        }
                    }
                    catch (Exception e)
                    {
                        logger.error("Exception--->", e);
                    }
                }
                else
                {
                    logger.debug("Last transaction not fond on terminal");
                }
            }
            else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.InactivityPeriod.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.ProcessingSetup.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.Old.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
            {
                //Inactivity period calculation for both old terminals
                if(functions.isValueNull(processingDetails.getLastTransactionDate()))
                {
                    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long day = 0;
                    long day1 = 0;
                    try
                    {
                        day1 = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(processingDetails.getFirstTransactionDate())),targetFormat.format(new Date()));
                        if(day1>90)
                        {
                            day = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(processingDetails.getLastTransactionDate())),targetFormat.format(new Date()));
                            calculation = day;
                        }
                    }
                    catch (Exception e)
                    {
                        logger.error("Exception--->", e);
                    }
                }
                else
                {
                    logger.debug("Last transaction not fond on terminal");
                }
            }
            else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
            {
                //Lower or higher sales than contracted -New Terminals
                /*System.out.println("Current days actual Sales=====" + processingDetails.getSalesAmount());
                System.out.println("Current day limit=====" + terminalLimitsVO.getDailyAmountLimit());*/

                double dailyActualSalesAmount = processingDetails.getSalesAmount();
                double dailyTargetSalesAmount = terminalLimitsVO.getDailyAmountLimit();

                if(functions.isValueNull(processingDetails.getFirstTransactionDate()))
                {
                    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long day = 0;
                    try
                    {
                        day = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(processingDetails.getFirstTransactionDate())),targetFormat.format(new Date()));
                    }
                    catch (Exception e)
                    {
                        logger.error("Exception--->", e);
                    }
                    if(day<90)
                    {
                        if(dailyTargetSalesAmount>0)
                        {
                            calculation = (((dailyActualSalesAmount ) / dailyTargetSalesAmount) * 100);
                            calculation=calculation-100;
                        }
                    }
                }
                //System.out.println("Current days actual Sales calculation=====" + calculation);
                //System.out.println("Current day limit calculation=====" + terminalLimitsVO.getDailyAmountLimit());

            }
            else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.Old.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
            {
                //Lower or higher sales than contracted-Old Terminals
               /* System.out.println("Lower or higher sales than contracted-Old Terminals");
                System.out.println("Current days actual Sales====="+processingDetails.getSalesAmount());
                System.out.println("Current day limit====="+terminalLimitsVO.getDailyAmountLimit());*/
                if(functions.isValueNull(processingDetails.getFirstTransactionDate()))
                {
                    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long day = 0;
                    try
                    {
                        day = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(processingDetails.getFirstTransactionDate())),targetFormat.format(new Date()));
                    }
                    catch (Exception e)
                    {
                        logger.error("Exception--->", e);
                    }
                    if(day>90)
                    {
                        double dailyActualSalesAmount =processingDetails.getSalesAmount();
                        double dailyTargetSalesAmount =lastThreeMonthProcessingDetailsVO.getSalesAmount()/90;
                        //System.out.println("lastThreeMonthProcessingDetailsVO======"+lastThreeMonthProcessingDetailsVO.getSalesAmount());
                        //System.out.println("dailyTargetSalesAmount====="+dailyTargetSalesAmount);
                        if (dailyTargetSalesAmount > 0)
                        {
                            calculation = (((dailyActualSalesAmount/dailyTargetSalesAmount)) * 100);
                            calculation=calculation-100;
                        }
                    }
                }
                logger.debug("Current days actual Sales calculation=====" + calculation);
            }
            else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.AvgTicketAmount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.AvgTicketDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
            {
                //Transaction higher than contracted - New merchants
                //System.out.println("Transaction higher than contracted - New merchants");
                double contractedAvgTicketAmount =terminalLimitsVO.getDailyAvgTicketAmount();
                double currentDayActualAvgTicketAmount = 0.00;

                if(functions.isValueNull(processingDetails.getFirstTransactionDate()))
                {
                    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long day = 0;
                    try
                    {
                        day = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(processingDetails.getFirstTransactionDate())),targetFormat.format(new Date()));
                    }
                    catch (Exception e)
                    {
                        logger.error("Exception--->",e);
                    }
                    if(day<90)
                    {
                        currentDayActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(processingDetails.getSalesAmount(),processingDetails.getSalesCount());
                        calculation = (currentDayActualAvgTicketAmount/contractedAvgTicketAmount) * 100;
                        calculation=calculation-100;
                    }
                   /* System.out.println("contractedAvgTicketAmount===" + contractedAvgTicketAmount);
                    System.out.println("currentDayActualAvgTicketAmount===" + currentDayActualAvgTicketAmount);
                    System.out.println("calculation===" + calculation);*/
                }
            }
            else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.AvgTicketAmount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.AvgTicketDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.Old.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
            {
                //Transaction higher than contracted - Old merchants
               /* System.out.println("Transaction higher than contracted - Old merchants");
                System.out.println("memberid===="+terminalVO.getMemberId());
                System.out.println("terminalid===="+terminalVO.getTerminalId());*/
                double lastThreeMonthActualAvgTicketAmount = 0.00;
                double currentDayActualAvgTicketAmount = 0.00;
                if(functions.isValueNull(processingDetails.getFirstTransactionDate()))
                {
                    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long day = 0;
                    try
                    {
                        day = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(processingDetails.getFirstTransactionDate())),targetFormat.format(new Date()));
                    }
                    catch (Exception e)
                    {
                        logger.error("Exception--->", e);
                    }
                    if(day>90)
                    {
                        /*System.out.println("lastThreeMonthProcessingDetailsVO.getSalesAmount()==="+lastThreeMonthProcessingDetailsVO.getSalesAmount());
                        System.out.println("lastThreeMonthProcessingDetailsVO.getSalesCount()==="+lastThreeMonthProcessingDetailsVO.getSalesCount());*/

                        lastThreeMonthActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(lastThreeMonthProcessingDetailsVO.getSalesAmount(),lastThreeMonthProcessingDetailsVO.getSalesCount());
                        currentDayActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(processingDetails.getSalesAmount(), processingDetails.getSalesCount());
                        calculation = (currentDayActualAvgTicketAmount/lastThreeMonthActualAvgTicketAmount) * 100;
                        calculation=calculation-100;
                    }
                    /*System.out.println("lastThreeMonthActualAvgTicketAmount====" + lastThreeMonthActualAvgTicketAmount);
                    System.out.println("currentDayActualAvgTicketAmount====" + currentDayActualAvgTicketAmount);
                    System.out.println("calculation====" + calculation);*/
                }
                else
                {
                    logger.debug("First Transaction Not Found");
                }
            }

        }
        paraCalculationDetailsVO.setCalculation(calculation);
        paraCalculationDetailsVO.setCalculationArea(calculationArea);
        return paraCalculationDetailsVO;


    }

    public double calculateRatio(MonitoringParameterVO monitoringParameterVO, TerminalProcessingDetailsVO processingDetails)
    {
        double calculation = 0.00;
        RatioCalculationHelper ratioCalculationHelper = new RatioCalculationHelper();
        if (MonitoringCategory.Failure.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Ratio.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
        {
            calculation = ratioCalculationHelper.calculateDeclineRatioAmount(processingDetails.getSalesAmount(), processingDetails.getDeclinedAmount());
        }
        else if (MonitoringCategory.Failure.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Ratio.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
        {
            logger.debug("calculationSalesCount=====" + processingDetails.getSalesCount());
            logger.debug("calculationDeclinedCount=====" +  processingDetails.getDeclinedCount());
            calculation = ratioCalculationHelper.calculateDeclineRatioCount(processingDetails.getSalesCount(), processingDetails.getDeclinedCount());
            logger.debug("calculationOfDeclideCount====" + calculation );
        }
        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Ratio.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
        {
            calculation = ratioCalculationHelper.calculateApprovalRatioAmount(processingDetails.getSalesAmount(), processingDetails.getDeclinedAmount());
        }
        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Ratio.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
        {
            calculation = ratioCalculationHelper.calculateApprovalRatioCount(processingDetails.getSalesCount(), processingDetails.getDeclinedCount());
        }
        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Chargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Ratio.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
        {
            logger.debug("calculationCBSalecount=====" + processingDetails.getSalesCount());
            logger.debug("calculationgetCBCount=====" + processingDetails.getChargebackCount());
            calculation = ratioCalculationHelper.calculateCBRatioCount(processingDetails.getSalesCount(), processingDetails.getChargebackCount());
            logger.debug("calculationCBCountresult====" + calculation );
        }
        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Chargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Ratio.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
        {
            calculation = ratioCalculationHelper.calculateCBRatioAmount(processingDetails.getSalesAmount(), processingDetails.getChargebackAmount());

        }
        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Reversed.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Ratio.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
        {
            calculation = ratioCalculationHelper.calculateRFRatioCount(processingDetails.getSalesCount(), processingDetails.getRefundCount());
        }
        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Reversed.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Ratio.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
        {
            calculation = ratioCalculationHelper.calculateRFRatioAmount(processingDetails.getSalesAmount(), processingDetails.getRefundAmount());
        }
        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.AuthSuccessful.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Ratio.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
        {
            calculation = ratioCalculationHelper.calculateApprovalRatioAmount(processingDetails.getPreAuthAmount(), processingDetails.getPreAuthCount());
        }
        return calculation;
    }
    public void sendMailToAdmin(Map<String, List<MonitoringAlertDetailVO>> adminStringListHashMap, MerchantDetailsVO merchantDetailsVO, Map<String, List<MonitoringAlertDetailVO>> adminStringListHashMap1)
    {
        //Admin Mail
        if (adminStringListHashMap.size() > 0)
        {
            //System.out.println("Mail Should be to admin");
            MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHash(adminStringListHashMap, merchantDetailsVO));
            mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ALERT_TO_ADMIN, mailPlaceHolder);
        }
        if (adminStringListHashMap1.size() > 0)
        {
            //System.out.println("Mail Should be to admin");
            MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHash(adminStringListHashMap1, merchantDetailsVO));
            mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_ADMIN, mailPlaceHolder);
        }
    }

    public void sendMailToAdminCBConsolidate(Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminCBStringListHashMapMap, Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminCBStringListHashMapConsolidateSuspension, Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminCBStringAttachmentListHashMapConsolidate)
    {
        //Admin Mail
        if (adminCBStringListHashMapMap.size() > 0)
        {
            //System.out.println("Mail Should be to admin");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHashConsolidate(adminCBStringListHashMapMap));
            //mailService.sendMail(MailEventEnum.MERCHANT_CB_MONITORING_ALERT_TO_ADMIN, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_CB_MONITORING_ALERT_TO_ADMIN,mailPlaceHolder);

        }
        if (adminCBStringListHashMapConsolidateSuspension.size() > 0)
        {
            //System.out.println("Mail Should be to admin");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHashConsolidate(adminCBStringListHashMapConsolidateSuspension));
            //mailService.sendMail(MailEventEnum.MERCHANT_CB_MONITORING_SUSPENSION_TO_ADMIN, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_CB_MONITORING_SUSPENSION_TO_ADMIN,mailPlaceHolder);

        }
        if (adminCBStringAttachmentListHashMapConsolidate.size() > 0)
        {
            //System.out.println("From Admin Sales -Type-1 Email");
            List<FileAttachmentVO> fileAttachmentVOs = new ArrayList();
            Set set = adminCBStringAttachmentListHashMapConsolidate.keySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext())
            {
                String memberId = (String) iterator.next();
                MerchantDetailsVO merchantDetailsVO = null;
                try
                {
                    merchantDetailsVO = new MerchantDAO().getMemberDetails(memberId);
                }
                catch (Exception e)
                {

                }

                Map<String, List<MonitoringAlertDetailVO>> stringListMap = adminCBStringAttachmentListHashMapConsolidate.get(memberId);
                Set set1 = stringListMap.keySet();
                Iterator iterator1 = set1.iterator();
                while (iterator1.hasNext())
                {
                    String termialId = (String) iterator1.next();
                    List<MonitoringAlertDetailVO> monitoringAlertDetailVOs = stringListMap.get(termialId);
                    for (MonitoringAlertDetailVO monitoringAlertDetailVO : monitoringAlertDetailVOs)
                    {
                        MonitoringAttachmentAlertDetailsVO monitoringAttachmentAlertDetailsVO = (MonitoringAttachmentAlertDetailsVO) monitoringAlertDetailVO;
                        FileAttachmentVO fileAttachmentVO = monitoringAttachmentAlertDetailsVO.getFileAttachmentVO();
                        fileAttachmentVOs.add(fileAttachmentVO);

                       // MailService mailService = new MailService();
                        HashMap mailPlaceHolder = new HashMap();
                        MailServiceHelper mailServiceHelper = new MailServiceHelper();
                        mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableDataFromForSingleRule(monitoringAlertDetailVO, merchantDetailsVO, termialId));
                        mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILENAME, fileAttachmentVO.getFileName());
                        mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILEPATH, fileAttachmentVO.getFilePath());
                        //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_ADMIN_CB, mailPlaceHolder);
                        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
                        asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_ADMIN_CB,mailPlaceHolder);

                    }
                }
            }
        }
    }

    public void sendMailToAdminRFConsolidate(Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminRFStringListHashMapMap, Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminRFStringListHashMapConsolidateSuspension, Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminRFStringAttachmentListHashMapConsolidate)
    {
        //Admin Mail
        if (adminRFStringListHashMapMap.size() > 0)
        {
            //System.out.println("Mail Should be to admin");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHashConsolidate(adminRFStringListHashMapMap));
            //mailService.sendMail(MailEventEnum.MERCHANT_RF_MONITORING_ALERT_TO_ADMIN, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_RF_MONITORING_ALERT_TO_ADMIN,mailPlaceHolder);
        }
        if (adminRFStringListHashMapConsolidateSuspension.size() > 0)
        {
            //System.out.println("Mail Should be to admin");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHashConsolidate(adminRFStringListHashMapConsolidateSuspension));
            //mailService.sendMail(MailEventEnum.MERCHANT_RF_MONITORING_SUSPENSION_TO_ADMIN, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_RF_MONITORING_SUSPENSION_TO_ADMIN,mailPlaceHolder);
        }
        if (adminRFStringAttachmentListHashMapConsolidate.size() > 0)
        {
            //System.out.println("From Admin Sales -Type-1 Email");
            List<FileAttachmentVO> fileAttachmentVOs = new ArrayList();
            Set set = adminRFStringAttachmentListHashMapConsolidate.keySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext())
            {
                String memberId = (String) iterator.next();
                MerchantDetailsVO merchantDetailsVO = null;
                try
                {
                    merchantDetailsVO = new MerchantDAO().getMemberDetails(memberId);
                }
                catch (Exception e)
                {

                }

                Map<String, List<MonitoringAlertDetailVO>> stringListMap = adminRFStringAttachmentListHashMapConsolidate.get(memberId);
                Set set1 = stringListMap.keySet();
                Iterator iterator1 = set1.iterator();
                while (iterator1.hasNext())
                {
                    String termialId = (String) iterator1.next();
                    List<MonitoringAlertDetailVO> monitoringAlertDetailVOs = stringListMap.get(termialId);
                    for (MonitoringAlertDetailVO monitoringAlertDetailVO : monitoringAlertDetailVOs)
                    {
                        MonitoringAttachmentAlertDetailsVO monitoringAttachmentAlertDetailsVO = (MonitoringAttachmentAlertDetailsVO) monitoringAlertDetailVO;
                        FileAttachmentVO fileAttachmentVO = monitoringAttachmentAlertDetailsVO.getFileAttachmentVO();
                        fileAttachmentVOs.add(fileAttachmentVO);

                        //MailService mailService = new MailService();
                        HashMap mailPlaceHolder = new HashMap();
                        MailServiceHelper mailServiceHelper = new MailServiceHelper();
                        mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableDataFromForSingleRule(monitoringAlertDetailVO, merchantDetailsVO, termialId));
                        mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILENAME, fileAttachmentVO.getFileName());
                        mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILEPATH, fileAttachmentVO.getFilePath());
                        //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_ADMIN_RF, mailPlaceHolder);
                        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
                        asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_ADMIN_RF,mailPlaceHolder);
                    }
                }
            }
        }
    }

    public void sendMailToAdminFraudConsolidate(Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminFraudStringListHashMapMap, Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminFraudStringListHashMapConsolidateSuspension, Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminFraudStringAttachmentListHashMapConsolidate)
    {
        //Admin Mail
        if (adminFraudStringListHashMapMap.size() > 0)
        {
            //System.out.println("Mail Should be to admin");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHashConsolidate(adminFraudStringListHashMapMap));
            //mailService.sendMail(MailEventEnum.MERCHANT_FRAUD_MONITORING_ALERT_TO_ADMIN, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_FRAUD_MONITORING_ALERT_TO_ADMIN,mailPlaceHolder);
        }
        if (adminFraudStringListHashMapConsolidateSuspension.size() > 0)
        {
            //System.out.println("Mail Should be to admin");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHashConsolidate(adminFraudStringListHashMapConsolidateSuspension));
            //mailService.sendMail(MailEventEnum.MERCHANT_FRAUD_MONITORING_SUSPENSION_TO_ADMIN, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_FRAUD_MONITORING_SUSPENSION_TO_ADMIN,mailPlaceHolder);
        }
        if (adminFraudStringAttachmentListHashMapConsolidate.size() > 0)
        {
            logger.debug("From Admin Sales -Type-1 Email");
            List<FileAttachmentVO> fileAttachmentVOs = new ArrayList();
            Set set = adminFraudStringAttachmentListHashMapConsolidate.keySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext())
            {
                String memberId = (String) iterator.next();
                MerchantDetailsVO merchantDetailsVO = null;
                try
                {
                    merchantDetailsVO = new MerchantDAO().getMemberDetails(memberId);
                }
                catch (Exception e)
                {

                }

                Map<String, List<MonitoringAlertDetailVO>> stringListMap = adminFraudStringAttachmentListHashMapConsolidate.get(memberId);
                Set set1 = stringListMap.keySet();
                Iterator iterator1 = set1.iterator();
                while (iterator1.hasNext())
                {
                    String termialId = (String) iterator1.next();
                    List<MonitoringAlertDetailVO> monitoringAlertDetailVOs = stringListMap.get(termialId);
                    for (MonitoringAlertDetailVO monitoringAlertDetailVO : monitoringAlertDetailVOs)
                    {
                        MonitoringAttachmentAlertDetailsVO monitoringAttachmentAlertDetailsVO = (MonitoringAttachmentAlertDetailsVO) monitoringAlertDetailVO;
                        FileAttachmentVO fileAttachmentVO = monitoringAttachmentAlertDetailsVO.getFileAttachmentVO();
                        fileAttachmentVOs.add(fileAttachmentVO);

                        //MailService mailService = new MailService();
                        HashMap mailPlaceHolder = new HashMap();
                        MailServiceHelper mailServiceHelper = new MailServiceHelper();
                        mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableDataFromForSingleRule(monitoringAlertDetailVO, merchantDetailsVO, termialId));
                        mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILENAME, fileAttachmentVO.getFileName());
                        mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILEPATH, fileAttachmentVO.getFilePath());
                        //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_ADMIN_FRAUD, mailPlaceHolder);
                        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
                        asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_ADMIN_FRAUD,mailPlaceHolder);
                    }
                }
            }
        }
    }

    public void sendMailToAdminTechConsolidate(Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminTechStringListHashMapMap, Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminTechStringListHashMapConsolidateSuspension, Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminTechStringAttachmentListHashMapConsolidate)
    {
        //Admin Mail
        if (adminTechStringListHashMapMap.size() > 0)
        {
            //System.out.println("Mail Should be to admin");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHashConsolidate(adminTechStringListHashMapMap));
            //mailService.sendMail(MailEventEnum.MERCHANT_TECH_MONITORING_ALERT_TO_ADMIN, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_TECH_MONITORING_ALERT_TO_ADMIN,mailPlaceHolder);
        }
        if (adminTechStringListHashMapConsolidateSuspension.size() > 0)
        {
            //System.out.println("Mail Should be to admin");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHashConsolidate(adminTechStringListHashMapConsolidateSuspension));
            //mailService.sendMail(MailEventEnum.MERCHANT_TECH_MONITORING_SUSPENSION_TO_ADMIN, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_TECH_MONITORING_SUSPENSION_TO_ADMIN,mailPlaceHolder);
        }
        if (adminTechStringAttachmentListHashMapConsolidate.size() > 0)
        {
            logger.debug("From Admin Sales -Type-1 Email");
            List<FileAttachmentVO> fileAttachmentVOs = new ArrayList();
            Set set = adminTechStringAttachmentListHashMapConsolidate.keySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext())
            {
                String memberId = (String) iterator.next();
                MerchantDetailsVO merchantDetailsVO = null;
                try
                {
                    merchantDetailsVO = new MerchantDAO().getMemberDetails(memberId);
                }
                catch (Exception e)
                {

                }

                Map<String, List<MonitoringAlertDetailVO>> stringListMap = adminTechStringAttachmentListHashMapConsolidate.get(memberId);
                Set set1 = stringListMap.keySet();
                Iterator iterator1 = set1.iterator();
                while (iterator1.hasNext())
                {
                    String termialId = (String) iterator1.next();
                    List<MonitoringAlertDetailVO> monitoringAlertDetailVOs = stringListMap.get(termialId);
                    for (MonitoringAlertDetailVO monitoringAlertDetailVO : monitoringAlertDetailVOs)
                    {
                        MonitoringAttachmentAlertDetailsVO monitoringAttachmentAlertDetailsVO = (MonitoringAttachmentAlertDetailsVO) monitoringAlertDetailVO;
                        FileAttachmentVO fileAttachmentVO = monitoringAttachmentAlertDetailsVO.getFileAttachmentVO();
                        fileAttachmentVOs.add(fileAttachmentVO);

                        //MailService mailService = new MailService();
                        HashMap mailPlaceHolder = new HashMap();
                        MailServiceHelper mailServiceHelper = new MailServiceHelper();
                        mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableDataFromForSingleRule(monitoringAlertDetailVO, merchantDetailsVO, termialId));
                        mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILENAME, fileAttachmentVO.getFileName());
                        mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILEPATH, fileAttachmentVO.getFilePath());
                        //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_ADMIN_TECH, mailPlaceHolder);
                        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
                        asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_ADMIN_TECH,mailPlaceHolder);
                    }
                }
            }
        }
    }

    public void sendMailToAdminConsolidate(Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminStringListHashMapMap, Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminStringListHashMapConsolidateSuspension, Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminStringListHashMapConsolidateAttachments)
    {
        //Admin Mail
        if (adminStringListHashMapMap.size() > 0)
        {
            //System.out.println("Mail Should be to admin");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHashConsolidate(adminStringListHashMapMap));
            //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ALERT_TO_ADMIN, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_ALERT_TO_ADMIN,mailPlaceHolder);
        }
        if (adminStringListHashMapConsolidateSuspension.size() > 0)
        {
            //System.out.println("Mail Should be to admin");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHashConsolidate(adminStringListHashMapConsolidateSuspension));
            //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_ADMIN, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_ADMIN,mailPlaceHolder);
        }
        if (adminStringListHashMapConsolidateAttachments.size() > 0)
        {
            logger.debug("From Admin Sales -Type-1 Email");
            List<FileAttachmentVO> fileAttachmentVOs = new ArrayList();
            Set set = adminStringListHashMapConsolidateAttachments.keySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext())
            {
                String memberId = (String) iterator.next();
                MerchantDetailsVO merchantDetailsVO = null;
                try
                {
                    merchantDetailsVO = new MerchantDAO().getMemberDetails(memberId);
                }
                catch (Exception e)
                {

                }

                Map<String, List<MonitoringAlertDetailVO>> stringListMap = adminStringListHashMapConsolidateAttachments.get(memberId);
                Set set1 = stringListMap.keySet();
                Iterator iterator1 = set1.iterator();
                while (iterator1.hasNext())
                {
                    String termialId = (String) iterator1.next();
                    List<MonitoringAlertDetailVO> monitoringAlertDetailVOs = stringListMap.get(termialId);
                    for (MonitoringAlertDetailVO monitoringAlertDetailVO : monitoringAlertDetailVOs)
                    {
                        MonitoringAttachmentAlertDetailsVO monitoringAttachmentAlertDetailsVO = (MonitoringAttachmentAlertDetailsVO) monitoringAlertDetailVO;
                        FileAttachmentVO fileAttachmentVO = monitoringAttachmentAlertDetailsVO.getFileAttachmentVO();
                        fileAttachmentVOs.add(fileAttachmentVO);

                        //MailService mailService = new MailService();
                        HashMap mailPlaceHolder = new HashMap();
                        MailServiceHelper mailServiceHelper = new MailServiceHelper();
                        mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableDataFromForSingleRule(monitoringAlertDetailVO, merchantDetailsVO, termialId));
                        mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILENAME, fileAttachmentVO.getFileName());
                        mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILEPATH, fileAttachmentVO.getFilePath());
                        //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_ADMIN_SALES, mailPlaceHolder);
                        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
                        asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_ADMIN_SALES,mailPlaceHolder);

                    }
                }
            }
        }
    }
    public void sendMailToAdmin1(Map<String, List<MonitoringAlertDetailVO>> adminStringListHashMap, MerchantDetailsVO merchantDetailsVO, Map<String, List<MonitoringAlertDetailVO>> adminStringListHashMap1, Map<String, List<MonitoringAlertDetailVO>> adminStringListHashMap2)
    {
        //Admin Mail
        if (adminStringListHashMap.size() > 0)
        {
            //System.out.println("Mail Should be to admin");
            MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHash(adminStringListHashMap, merchantDetailsVO));
            mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ALERT_TO_ADMIN, mailPlaceHolder);
        }
        if (adminStringListHashMap1.size() > 0)
        {
            //System.out.println("Mail Should be to admin");
            MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHash(adminStringListHashMap1, merchantDetailsVO));
            mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_ADMIN, mailPlaceHolder);
        }
        if (adminStringListHashMap2.size() > 0)
        {
            List<FileAttachmentVO> fileAttachmentVOs = new ArrayList();
            Set set = adminStringListHashMap2.keySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext())
            {
                String terminalId = (String) iterator.next();
                List<MonitoringAlertDetailVO> monitoringAlertDetailVOs = adminStringListHashMap2.get(terminalId);
                for (MonitoringAlertDetailVO monitoringAlertDetailVO : monitoringAlertDetailVOs)
                {
                    MonitoringAttachmentAlertDetailsVO monitoringAttachmentAlertDetailsVO = (MonitoringAttachmentAlertDetailsVO) monitoringAlertDetailVO;
                    for (FileAttachmentVO fileAttachmentVO : monitoringAttachmentAlertDetailsVO.getFileAttachmentVOList())
                    {
                        fileAttachmentVOs.add(fileAttachmentVO);
                        /*System.out.println("FileName===" + fileAttachmentVO.getFileName());
                        System.out.println("FilePath===" + fileAttachmentVO.getFilePath());*/
                    }
                }
            }
            logger.debug("total attachments====" + fileAttachmentVOs.size());
            logger.debug("attachment mail should to be admin");
            //System.out.println("Mail Should be to admin");
            MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHash(adminStringListHashMap2, merchantDetailsVO));
            mailService.sendMailWithMultipleAttachment(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_TO_ADMIN, mailPlaceHolder, fileAttachmentVOs);
        }
    }
    public void sendMailToAgent(Map<String, List<MonitoringAlertDetailVO>> agentStringListHashMap, MerchantDetailsVO merchantDetailsVO, Map<String, List<MonitoringAlertDetailVO>> agentStringListHashMap1)
    {
        if (agentStringListHashMap.size() > 0)
        {
            logger.debug("Mail Should be to admin");
            MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID, "1");
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHash(agentStringListHashMap, merchantDetailsVO));
            mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ALERT_TO_AGENT, mailPlaceHolder);

        }
    }

    public void sendMailToAgentSalesConsolidate(String agentId,Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentStringListHashMap, Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentStringListHashMapConsolidateSuspension, Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentSalesStringAttachmentListHashMapConsolidate)
    {
        if (agentStringListHashMap !=null && agentStringListHashMap.size() > 0)
        {
            logger.debug("Mail Should be to agent");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID, agentId);
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHashConsolidate(agentStringListHashMap));
            //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ALERT_TO_AGENT, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_ALERT_TO_AGENT,mailPlaceHolder);
        }
        if (agentStringListHashMapConsolidateSuspension!=null && agentStringListHashMapConsolidateSuspension.size() > 0)
        {
            logger.debug("Mail Should be to agent");
            ///MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID, agentId);
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHashConsolidate(agentStringListHashMapConsolidateSuspension));
            //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_AGENT, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_AGENT,mailPlaceHolder);
        }
        if (agentSalesStringAttachmentListHashMapConsolidate !=null && agentSalesStringAttachmentListHashMapConsolidate.size() > 0)
        {
            logger.debug("From Admin Sales -Type-1 Email");
            List<FileAttachmentVO> fileAttachmentVOs = new ArrayList();
            Set set = agentSalesStringAttachmentListHashMapConsolidate.keySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext())
            {
                String memberId = (String) iterator.next();
                MerchantDetailsVO merchantDetailsVO = null;
                try
                {
                    merchantDetailsVO = new MerchantDAO().getMemberDetails(memberId);
                }
                catch (Exception e)
                {

                }

                Map<String, List<MonitoringAlertDetailVO>> stringListMap = agentSalesStringAttachmentListHashMapConsolidate.get(memberId);
                Set set1 = stringListMap.keySet();
                Iterator iterator1 = set1.iterator();
                while (iterator1.hasNext())
                {
                    String termialId = (String) iterator1.next();
                    List<MonitoringAlertDetailVO> monitoringAlertDetailVOs = stringListMap.get(termialId);
                    for (MonitoringAlertDetailVO monitoringAlertDetailVO : monitoringAlertDetailVOs)
                    {
                        MonitoringAttachmentAlertDetailsVO monitoringAttachmentAlertDetailsVO = (MonitoringAttachmentAlertDetailsVO) monitoringAlertDetailVO;
                        FileAttachmentVO fileAttachmentVO = monitoringAttachmentAlertDetailsVO.getFileAttachmentVO();
                        fileAttachmentVOs.add(fileAttachmentVO);

                        //MailService mailService = new MailService();
                        HashMap mailPlaceHolder = new HashMap();
                        MailServiceHelper mailServiceHelper = new MailServiceHelper();
                        mailPlaceHolder.put(MailPlaceHolder.TOID, agentId);
                        mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableDataFromForSingleRule(monitoringAlertDetailVO, merchantDetailsVO, termialId));
                        mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILENAME, fileAttachmentVO.getFileName());
                        mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILEPATH, fileAttachmentVO.getFilePath());
                       //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_AGENT_SALES, mailPlaceHolder);
                        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
                        asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_AGENT_SALES,mailPlaceHolder);
                    }
                }
            }
        }
    }

    public void sendMailToAgentCBConsolidate(String agentId,Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentCBStringListHashMap, Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentCBStringListHashMapConsolidateSuspension, Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentCBStringAttachmentListHashMapConsolidate)
    {
        if (agentCBStringListHashMap !=null && agentCBStringListHashMap.size() > 0)
        {
            //System.out.println("Mail Should be to agent");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID, agentId);
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHashConsolidate(agentCBStringListHashMap));
            //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ALERT_TO_AGENT_CB, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_ALERT_TO_AGENT_CB,mailPlaceHolder);
        }
        if (agentCBStringListHashMapConsolidateSuspension !=null && agentCBStringListHashMapConsolidateSuspension.size() > 0)
        {
            //System.out.println("Mail Should be to agent");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID,agentId);
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHashConsolidate(agentCBStringListHashMapConsolidateSuspension));
            //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_AGENT_CB, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_AGENT_CB,mailPlaceHolder);
        }
        if (agentCBStringAttachmentListHashMapConsolidate !=null && agentCBStringAttachmentListHashMapConsolidate.size() > 0)
        {
            logger.debug("From Admin Sales -Type-1 Email");
            List<FileAttachmentVO> fileAttachmentVOs = new ArrayList();
            Set set = agentCBStringAttachmentListHashMapConsolidate.keySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext())
            {
                String memberId = (String) iterator.next();
                MerchantDetailsVO merchantDetailsVO = null;
                try
                {
                    merchantDetailsVO = new MerchantDAO().getMemberDetails(memberId);
                }
                catch (Exception e)
                {

                }

                Map<String, List<MonitoringAlertDetailVO>> stringListMap = agentCBStringAttachmentListHashMapConsolidate.get(memberId);
                Set set1 = stringListMap.keySet();
                Iterator iterator1 = set1.iterator();
                while (iterator1.hasNext())
                {
                    String termialId = (String) iterator1.next();
                    List<MonitoringAlertDetailVO> monitoringAlertDetailVOs = stringListMap.get(termialId);
                    for (MonitoringAlertDetailVO monitoringAlertDetailVO : monitoringAlertDetailVOs)
                    {
                        MonitoringAttachmentAlertDetailsVO monitoringAttachmentAlertDetailsVO = (MonitoringAttachmentAlertDetailsVO) monitoringAlertDetailVO;
                        FileAttachmentVO fileAttachmentVO = monitoringAttachmentAlertDetailsVO.getFileAttachmentVO();
                        fileAttachmentVOs.add(fileAttachmentVO);
                        //MailService mailService = new MailService();
                        HashMap mailPlaceHolder = new HashMap();
                        MailServiceHelper mailServiceHelper = new MailServiceHelper();
                        mailPlaceHolder.put(MailPlaceHolder.TOID, agentId);
                        mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableDataFromForSingleRule(monitoringAlertDetailVO, merchantDetailsVO, termialId));
                        mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILENAME, fileAttachmentVO.getFileName());
                        mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILEPATH, fileAttachmentVO.getFilePath());
                        //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_AGENT_CB, mailPlaceHolder);
                        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
                        asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_AGENT_CB,mailPlaceHolder);
                    }
                }
            }
        }
    }

    public void sendMailToAgentRFConsolidate(String agentId,Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentRFStringListHashMap, Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentRFStringListHashMapConsolidateSuspension, Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentRFStringAttachmentListHashMapConsolidate)
    {
        if (agentRFStringListHashMap !=null && agentRFStringListHashMap.size() > 0)
        {
            //System.out.println("Mail Should be to agent");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID, agentId);
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHashConsolidate(agentRFStringListHashMap));
            //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ALERT_TO_AGENT_RF, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_ALERT_TO_AGENT_RF,mailPlaceHolder);
        }
        if (agentRFStringListHashMapConsolidateSuspension !=null && agentRFStringListHashMapConsolidateSuspension.size() > 0)
        {
            //System.out.println("Mail Should be to agent");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID, agentId);
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHashConsolidate(agentRFStringListHashMapConsolidateSuspension));
            //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_AGENT_RF, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_AGENT_RF,mailPlaceHolder);
        }
        if (agentRFStringAttachmentListHashMapConsolidate !=null && agentRFStringAttachmentListHashMapConsolidate.size() > 0)
        {
            logger.debug("From Admin Sales -Type-1 Email");
            List<FileAttachmentVO> fileAttachmentVOs = new ArrayList();
            Set set = agentRFStringAttachmentListHashMapConsolidate.keySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext())
            {
                String memberId = (String) iterator.next();
                MerchantDetailsVO merchantDetailsVO = null;
                try
                {
                    merchantDetailsVO = new MerchantDAO().getMemberDetails(memberId);
                }
                catch (Exception e)
                {

                }

                Map<String, List<MonitoringAlertDetailVO>> stringListMap = agentRFStringAttachmentListHashMapConsolidate.get(memberId);
                Set set1 = stringListMap.keySet();
                Iterator iterator1 = set1.iterator();
                while (iterator1.hasNext())
                {
                    String termialId = (String) iterator1.next();
                    List<MonitoringAlertDetailVO> monitoringAlertDetailVOs = stringListMap.get(termialId);
                    for (MonitoringAlertDetailVO monitoringAlertDetailVO : monitoringAlertDetailVOs)
                    {
                        MonitoringAttachmentAlertDetailsVO monitoringAttachmentAlertDetailsVO = (MonitoringAttachmentAlertDetailsVO) monitoringAlertDetailVO;
                        FileAttachmentVO fileAttachmentVO = monitoringAttachmentAlertDetailsVO.getFileAttachmentVO();
                        fileAttachmentVOs.add(fileAttachmentVO);
                        //MailService mailService = new MailService();
                        HashMap mailPlaceHolder = new HashMap();
                        MailServiceHelper mailServiceHelper = new MailServiceHelper();
                        mailPlaceHolder.put(MailPlaceHolder.TOID, agentId);
                        mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableDataFromForSingleRule(monitoringAlertDetailVO, merchantDetailsVO, termialId));
                        mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILENAME, fileAttachmentVO.getFileName());
                        mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILEPATH, fileAttachmentVO.getFilePath());
                        //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_AGENT_RF, mailPlaceHolder);
                        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
                        asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_AGENT_RF,mailPlaceHolder);
                    }
                }
            }
        }
    }

    public void sendMailToAgentFraudConsolidate(String agentId,Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentFraudStringListHashMap, Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentFraudStringListHashMapConsolidateSuspension, Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentFraudStringAttachmentListHashMapConsolidate)
    {
        if (agentFraudStringListHashMap !=null && agentFraudStringListHashMap.size() > 0)
        {
            //System.out.println("Mail Should be to agent");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID, agentId);
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHashConsolidate(agentFraudStringListHashMap));
            //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ALERT_TO_AGENT_FRAUD, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_ALERT_TO_AGENT_FRAUD,mailPlaceHolder);
        }
        if (agentFraudStringListHashMapConsolidateSuspension!=null && agentFraudStringListHashMapConsolidateSuspension.size() > 0)
        {
            //System.out.println("Mail Should be to agent");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID,agentId);
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHashConsolidate(agentFraudStringListHashMapConsolidateSuspension));
            //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_AGENT_FRAUD, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_AGENT_FRAUD,mailPlaceHolder);
        }
        if (agentFraudStringAttachmentListHashMapConsolidate !=null && agentFraudStringAttachmentListHashMapConsolidate.size() > 0)
        {
            logger.debug("From Admin Sales -Type-1 Email");
            List<FileAttachmentVO> fileAttachmentVOs = new ArrayList();
            Set set = agentFraudStringAttachmentListHashMapConsolidate.keySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext())
            {
                String memberId = (String) iterator.next();
                MerchantDetailsVO merchantDetailsVO = null;
                try
                {
                    merchantDetailsVO = new MerchantDAO().getMemberDetails(memberId);
                }
                catch (Exception e)
                {

                }

                Map<String, List<MonitoringAlertDetailVO>> stringListMap = agentFraudStringAttachmentListHashMapConsolidate.get(memberId);
                Set set1 = stringListMap.keySet();
                Iterator iterator1 = set1.iterator();
                while (iterator1.hasNext())
                {
                    String termialId = (String) iterator1.next();
                    List<MonitoringAlertDetailVO> monitoringAlertDetailVOs = stringListMap.get(termialId);
                    for (MonitoringAlertDetailVO monitoringAlertDetailVO : monitoringAlertDetailVOs)
                    {
                        MonitoringAttachmentAlertDetailsVO monitoringAttachmentAlertDetailsVO = (MonitoringAttachmentAlertDetailsVO) monitoringAlertDetailVO;
                        FileAttachmentVO fileAttachmentVO = monitoringAttachmentAlertDetailsVO.getFileAttachmentVO();
                        fileAttachmentVOs.add(fileAttachmentVO);
                        //MailService mailService = new MailService();
                        HashMap mailPlaceHolder = new HashMap();
                        MailServiceHelper mailServiceHelper = new MailServiceHelper();
                        mailPlaceHolder.put(MailPlaceHolder.TOID, agentId);
                        mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableDataFromForSingleRule(monitoringAlertDetailVO, merchantDetailsVO, termialId));
                        mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILENAME, fileAttachmentVO.getFileName());
                        mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILEPATH, fileAttachmentVO.getFilePath());
                        //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_AGENT_FRAUD, mailPlaceHolder);
                        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
                        asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_AGENT_FRAUD,mailPlaceHolder);
                    }
                }
            }
        }

    }

    public void sendMailToAgentTechConsolidate(String agentId,Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentTechStringListHashMap, Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentTechStringListHashMapConsolidateSuspension, Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentTechStringAttachmentListHashMapConsolidate)
    {
        if (agentTechStringListHashMap !=null && agentTechStringListHashMap.size() > 0)
        {
            //System.out.println("Mail Should be to agent");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID, agentId);
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHashConsolidate(agentTechStringListHashMap));
            //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ALERT_TO_AGENT_TECH, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_ALERT_TO_AGENT_TECH,mailPlaceHolder);
        }
        if (agentTechStringListHashMapConsolidateSuspension !=null && agentTechStringListHashMapConsolidateSuspension.size() > 0)
        {
            //System.out.println("Mail Should be to agent");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID, agentId);
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHashConsolidate(agentTechStringListHashMapConsolidateSuspension));
            //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_AGENT_TECH, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_AGENT_TECH,mailPlaceHolder);
        }
        if (agentTechStringAttachmentListHashMapConsolidate !=null && agentTechStringAttachmentListHashMapConsolidate.size() > 0)
        {
            logger.debug("From Admin Sales -Type-1 Email");
            List<FileAttachmentVO> fileAttachmentVOs = new ArrayList();
            Set set = agentTechStringAttachmentListHashMapConsolidate.keySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext())
            {
                String memberId = (String) iterator.next();
                MerchantDetailsVO merchantDetailsVO = null;
                try
                {
                    merchantDetailsVO = new MerchantDAO().getMemberDetails(memberId);
                }
                catch (Exception e)
                {

                }

                Map<String, List<MonitoringAlertDetailVO>> stringListMap = agentTechStringAttachmentListHashMapConsolidate.get(memberId);
                Set set1 = stringListMap.keySet();
                Iterator iterator1 = set1.iterator();
                while (iterator1.hasNext())
                {
                    String termialId = (String) iterator1.next();
                    List<MonitoringAlertDetailVO> monitoringAlertDetailVOs = stringListMap.get(termialId);
                    for (MonitoringAlertDetailVO monitoringAlertDetailVO : monitoringAlertDetailVOs)
                    {
                        MonitoringAttachmentAlertDetailsVO monitoringAttachmentAlertDetailsVO = (MonitoringAttachmentAlertDetailsVO) monitoringAlertDetailVO;
                        FileAttachmentVO fileAttachmentVO = monitoringAttachmentAlertDetailsVO.getFileAttachmentVO();
                        fileAttachmentVOs.add(fileAttachmentVO);
                        //MailService mailService = new MailService();
                        HashMap mailPlaceHolder = new HashMap();
                        MailServiceHelper mailServiceHelper = new MailServiceHelper();
                        mailPlaceHolder.put(MailPlaceHolder.TOID, agentId);
                        mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableDataFromForSingleRule(monitoringAlertDetailVO, merchantDetailsVO, termialId));
                        mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILENAME, fileAttachmentVO.getFileName());
                        mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILEPATH, fileAttachmentVO.getFilePath());
                        //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_AGENT_TECH, mailPlaceHolder);
                        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
                        asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_AGENT_TECH,mailPlaceHolder);
                    }
                }
            }
        }

    }

    public void sendMailToPartnerSalesConsolidate(String partnerId,Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerSalesStringListHashMapAlertOnly, Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerSalesStringListHashMapSuspensionAlert, Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerSalesStringAttachmentListHashMapConsolidate)
    {

        if (partnerSalesStringListHashMapAlertOnly!=null && partnerSalesStringListHashMapAlertOnly.size() > 0)
        {
            //System.out.println("Mail Should be to partner");
           // MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID,partnerId);
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHashConsolidate(partnerSalesStringListHashMapAlertOnly));
           //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ALERT_TO_PARTNER, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_ALERT_TO_PARTNER,mailPlaceHolder);
        }
        if (partnerSalesStringListHashMapSuspensionAlert!=null && partnerSalesStringListHashMapSuspensionAlert.size() > 0)
        {
            //System.out.println("Mail Should be to partner");
           // MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID,partnerId);
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHashConsolidate(partnerSalesStringListHashMapSuspensionAlert));
            //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_PARTNER_SALES, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_PARTNER_SALES,mailPlaceHolder);
        }
        if (partnerSalesStringAttachmentListHashMapConsolidate!=null && partnerSalesStringAttachmentListHashMapConsolidate.size() > 0)
        {
            logger.debug("From Admin Sales -Type-1 Email");
            List<FileAttachmentVO> fileAttachmentVOs = new ArrayList();
            Set set = partnerSalesStringAttachmentListHashMapConsolidate.keySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext())
            {
                String memberId = (String) iterator.next();
                MerchantDetailsVO merchantDetailsVO = null;
                try
                {
                    merchantDetailsVO = new MerchantDAO().getMemberDetails(memberId);
                }
                catch (Exception e)
                {

                }

                Map<String, List<MonitoringAlertDetailVO>> stringListMap = partnerSalesStringAttachmentListHashMapConsolidate.get(memberId);
                Set set1 = stringListMap.keySet();
                Iterator iterator1 = set1.iterator();
                while (iterator1.hasNext())
                {
                    String termialId = (String) iterator1.next();
                    List<MonitoringAlertDetailVO> monitoringAlertDetailVOs = stringListMap.get(termialId);
                    for (MonitoringAlertDetailVO monitoringAlertDetailVO : monitoringAlertDetailVOs)
                    {
                        MonitoringAttachmentAlertDetailsVO monitoringAttachmentAlertDetailsVO = (MonitoringAttachmentAlertDetailsVO) monitoringAlertDetailVO;
                        FileAttachmentVO fileAttachmentVO = monitoringAttachmentAlertDetailsVO.getFileAttachmentVO();
                        fileAttachmentVOs.add(fileAttachmentVO);

                        //MailService mailService = new MailService();
                        HashMap mailPlaceHolder = new HashMap();
                        MailServiceHelper mailServiceHelper = new MailServiceHelper();
                        mailPlaceHolder.put(MailPlaceHolder.TOID,partnerId);
                        mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableDataFromForSingleRule(monitoringAlertDetailVO, merchantDetailsVO, termialId));
                        mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILENAME, fileAttachmentVO.getFileName());
                        mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILEPATH, fileAttachmentVO.getFilePath());
                        //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_PARTNER_SALES, mailPlaceHolder);
                        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
                        asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_PARTNER_SALES,mailPlaceHolder);
                    }
                }
            }
        }
    }

    public void sendMailToPartnerCBConsolidate(String partnerId,Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerCBListHashMapAlertOnly, Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerCBListHashMapSuspensionAlert, Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerCBStringAttachmentListHashMapConsolidate)
    {
        if (partnerCBListHashMapAlertOnly !=null && partnerCBListHashMapAlertOnly.size() > 0)
        {
            //System.out.println("Mail Should be to partner");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID, partnerId);
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHashConsolidate(partnerCBListHashMapAlertOnly));
            //mailService.sendMail(MailEventEnum.MERCHANT_CB_MONITORING_ALERT_TO_PARTNER, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_CB_MONITORING_ALERT_TO_PARTNER,mailPlaceHolder);
        }
        if (partnerCBListHashMapSuspensionAlert!=null && partnerCBListHashMapSuspensionAlert.size() > 0)
        {
            //System.out.println("Mail Should be to partner");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID,partnerId);
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHashConsolidate(partnerCBListHashMapSuspensionAlert));
            //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_PARTNER_CB, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_PARTNER_CB,mailPlaceHolder);
        }
        if (partnerCBStringAttachmentListHashMapConsolidate!=null && partnerCBStringAttachmentListHashMapConsolidate.size() > 0)
        {
            logger.debug("From Admin Sales -Type-1 Email");
            List<FileAttachmentVO> fileAttachmentVOs = new ArrayList();
            Set set = partnerCBStringAttachmentListHashMapConsolidate.keySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext())
            {
                String memberId = (String) iterator.next();
                MerchantDetailsVO merchantDetailsVO = null;
                try
                {
                    merchantDetailsVO = new MerchantDAO().getMemberDetails(memberId);
                }
                catch (Exception e)
                {

                }

                Map<String, List<MonitoringAlertDetailVO>> stringListMap = partnerCBStringAttachmentListHashMapConsolidate.get(memberId);
                Set set1 = stringListMap.keySet();
                Iterator iterator1 = set1.iterator();
                while (iterator1.hasNext())
                {
                    String termialId = (String) iterator1.next();
                    List<MonitoringAlertDetailVO> monitoringAlertDetailVOs = stringListMap.get(termialId);
                    for (MonitoringAlertDetailVO monitoringAlertDetailVO : monitoringAlertDetailVOs)
                    {
                        MonitoringAttachmentAlertDetailsVO monitoringAttachmentAlertDetailsVO = (MonitoringAttachmentAlertDetailsVO) monitoringAlertDetailVO;
                        FileAttachmentVO fileAttachmentVO = monitoringAttachmentAlertDetailsVO.getFileAttachmentVO();
                        fileAttachmentVOs.add(fileAttachmentVO);

                        //MailService mailService = new MailService();
                        HashMap mailPlaceHolder = new HashMap();
                        MailServiceHelper mailServiceHelper = new MailServiceHelper();
                        mailPlaceHolder.put(MailPlaceHolder.TOID, partnerId);
                        mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableDataFromForSingleRule(monitoringAlertDetailVO, merchantDetailsVO, termialId));
                        mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILENAME, fileAttachmentVO.getFileName());
                        mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILEPATH, fileAttachmentVO.getFilePath());
                        //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_PARTNER_CB, mailPlaceHolder);
                        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
                        asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_PARTNER_CB,mailPlaceHolder);
                    }
                }
            }
        }
    }

    public void sendMailToPartnerRFConsolidate(String partnerId,Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerRFListHashMapAlertOnly, Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerRFListHashMapSuspensionAlert, Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerRFStringAttachmentListHashMapConsolidate)
    {
        if (partnerRFListHashMapAlertOnly !=null&& partnerRFListHashMapAlertOnly.size() > 0)
        {
            //System.out.println("Mail Should be to partner");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID, partnerId);
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHashConsolidate(partnerRFListHashMapAlertOnly));
           //mailService.sendMail(MailEventEnum.MERCHANT_RF_MONITORING_ALERT_TO_PARTNER, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_RF_MONITORING_ALERT_TO_PARTNER,mailPlaceHolder);
        }
        if (partnerRFListHashMapSuspensionAlert!=null && partnerRFListHashMapSuspensionAlert.size() > 0)
        {
            //System.out.println("Mail Should be to partner");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID,partnerId);
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHashConsolidate(partnerRFListHashMapSuspensionAlert));
            //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_PARTNER_RF, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_PARTNER_RF,mailPlaceHolder);
        }
        if (partnerRFStringAttachmentListHashMapConsolidate!=null && partnerRFStringAttachmentListHashMapConsolidate.size() > 0)
        {
            logger.debug("From Admin Sales -Type-1 Email");
            List<FileAttachmentVO> fileAttachmentVOs = new ArrayList();
            Set set = partnerRFStringAttachmentListHashMapConsolidate.keySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext())
            {
                String memberId = (String) iterator.next();
                MerchantDetailsVO merchantDetailsVO = null;
                try
                {
                    merchantDetailsVO = new MerchantDAO().getMemberDetails(memberId);
                }
                catch (Exception e)
                {

                }

                Map<String, List<MonitoringAlertDetailVO>> stringListMap = partnerRFStringAttachmentListHashMapConsolidate.get(memberId);
                Set set1 = stringListMap.keySet();
                Iterator iterator1 = set1.iterator();
                while (iterator1.hasNext())
                {
                    String termialId = (String) iterator1.next();
                    List<MonitoringAlertDetailVO> monitoringAlertDetailVOs = stringListMap.get(termialId);
                    for (MonitoringAlertDetailVO monitoringAlertDetailVO : monitoringAlertDetailVOs)
                    {
                        MonitoringAttachmentAlertDetailsVO monitoringAttachmentAlertDetailsVO = (MonitoringAttachmentAlertDetailsVO) monitoringAlertDetailVO;
                        FileAttachmentVO fileAttachmentVO = monitoringAttachmentAlertDetailsVO.getFileAttachmentVO();
                        fileAttachmentVOs.add(fileAttachmentVO);

                       // MailService mailService = new MailService();
                        HashMap mailPlaceHolder = new HashMap();
                        MailServiceHelper mailServiceHelper = new MailServiceHelper();
                        mailPlaceHolder.put(MailPlaceHolder.TOID, partnerId);
                        mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableDataFromForSingleRule(monitoringAlertDetailVO, merchantDetailsVO, termialId));
                        mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILENAME, fileAttachmentVO.getFileName());
                        mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILEPATH, fileAttachmentVO.getFilePath());
                        //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_PARTNER_RF, mailPlaceHolder);
                        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
                        asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_PARTNER_RF,mailPlaceHolder);
                    }
                }
            }
        }

    }

    public void sendMailToPartnerFraudConsolidate(String partnerId,Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerFraudListHashMapAlertOnly, Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerFraudListHashMapSuspensionAlert, Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerFraudStringAttachmentListHashMapConsolidate)
    {
        if (partnerFraudListHashMapAlertOnly !=null && partnerFraudListHashMapAlertOnly.size() > 0)
        {
            logger.debug("Mail Should be to partner fraud alert");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID, partnerId);
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHashConsolidate(partnerFraudListHashMapAlertOnly));
            //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ALERT_TO_PARTNER_FRAUD, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_ALERT_TO_PARTNER_FRAUD,mailPlaceHolder);
        }
        if (partnerFraudListHashMapSuspensionAlert!=null && partnerFraudListHashMapSuspensionAlert.size() > 0)
        {
            logger.debug("Mail Should be to partner fraud suspension alert");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID, partnerId);
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHashConsolidate(partnerFraudListHashMapSuspensionAlert));
            //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_PARTNER_FRAUD, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_PARTNER_FRAUD,mailPlaceHolder);
        }
        if (partnerFraudStringAttachmentListHashMapConsolidate!=null && partnerFraudStringAttachmentListHashMapConsolidate.size() > 0)
        {
            logger.debug("From Admin Sales -Type-1 Email");
            List<FileAttachmentVO> fileAttachmentVOs = new ArrayList();
            Set set = partnerFraudStringAttachmentListHashMapConsolidate.keySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext())
            {
                String memberId = (String) iterator.next();
                MerchantDetailsVO merchantDetailsVO = null;
                try
                {
                    merchantDetailsVO = new MerchantDAO().getMemberDetails(memberId);
                }
                catch (Exception e)
                {

                }

                Map<String, List<MonitoringAlertDetailVO>> stringListMap = partnerFraudStringAttachmentListHashMapConsolidate.get(memberId);
                Set set1 = stringListMap.keySet();
                Iterator iterator1 = set1.iterator();
                while (iterator1.hasNext())
                {
                    String termialId = (String) iterator1.next();
                    List<MonitoringAlertDetailVO> monitoringAlertDetailVOs = stringListMap.get(termialId);
                    for (MonitoringAlertDetailVO monitoringAlertDetailVO : monitoringAlertDetailVOs)
                    {
                        MonitoringAttachmentAlertDetailsVO monitoringAttachmentAlertDetailsVO = (MonitoringAttachmentAlertDetailsVO) monitoringAlertDetailVO;
                        FileAttachmentVO fileAttachmentVO = monitoringAttachmentAlertDetailsVO.getFileAttachmentVO();
                        fileAttachmentVOs.add(fileAttachmentVO);

                        //MailService mailService = new MailService();
                        HashMap mailPlaceHolder = new HashMap();
                        MailServiceHelper mailServiceHelper = new MailServiceHelper();
                        mailPlaceHolder.put(MailPlaceHolder.TOID,partnerId);
                        mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableDataFromForSingleRule(monitoringAlertDetailVO, merchantDetailsVO, termialId));
                        mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILENAME, fileAttachmentVO.getFileName());
                        mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILEPATH, fileAttachmentVO.getFilePath());
                        //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_PARTNER_FRAUD, mailPlaceHolder);
                        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
                        asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_PARTNER_FRAUD,mailPlaceHolder);
                    }
                }
            }
        }
    }

    public void sendMailToPartnerTechnicalConsolidate(String partnerId,Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerTechListHashMapAlertOnly, Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerTechListHashMapSuspensionAlert, Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerTechStringAttachmentListHashMapConsolidate)
    {
        if (partnerTechListHashMapAlertOnly!=null && partnerTechListHashMapAlertOnly.size() > 0)
        {
            //System.out.println("Mail Should be to partner");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID,partnerId);
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHashConsolidate(partnerTechListHashMapAlertOnly));
            //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ALERT_TO_PARTNER_TECH, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_ALERT_TO_PARTNER_TECH,mailPlaceHolder);
        }
        if (partnerTechListHashMapSuspensionAlert!=null && partnerTechListHashMapSuspensionAlert.size() > 0)
        {
            //System.out.println("Mail Should be to partner");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID,partnerId);
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHashConsolidate(partnerTechListHashMapSuspensionAlert));
            //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_PARTNER_TECH, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_PARTNER_TECH,mailPlaceHolder);

        }
        if (partnerTechStringAttachmentListHashMapConsolidate!=null && partnerTechStringAttachmentListHashMapConsolidate.size() > 0)
        {
            logger.debug("From Admin Sales -Type-1 Email");
            List<FileAttachmentVO> fileAttachmentVOs = new ArrayList();
            Set set = partnerTechStringAttachmentListHashMapConsolidate.keySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext())
            {
                String memberId = (String) iterator.next();
                MerchantDetailsVO merchantDetailsVO = null;
                try
                {
                    merchantDetailsVO = new MerchantDAO().getMemberDetails(memberId);
                }
                catch (Exception e)
                {

                }

                Map<String, List<MonitoringAlertDetailVO>> stringListMap = partnerTechStringAttachmentListHashMapConsolidate.get(memberId);
                Set set1 = stringListMap.keySet();
                Iterator iterator1 = set1.iterator();
                while (iterator1.hasNext())
                {
                    String termialId = (String) iterator1.next();
                    List<MonitoringAlertDetailVO> monitoringAlertDetailVOs = stringListMap.get(termialId);
                    for (MonitoringAlertDetailVO monitoringAlertDetailVO : monitoringAlertDetailVOs)
                    {
                        MonitoringAttachmentAlertDetailsVO monitoringAttachmentAlertDetailsVO = (MonitoringAttachmentAlertDetailsVO) monitoringAlertDetailVO;
                        FileAttachmentVO fileAttachmentVO = monitoringAttachmentAlertDetailsVO.getFileAttachmentVO();
                        fileAttachmentVOs.add(fileAttachmentVO);
                        //MailService mailService = new MailService();
                        HashMap mailPlaceHolder = new HashMap();
                        MailServiceHelper mailServiceHelper = new MailServiceHelper();
                        mailPlaceHolder.put(MailPlaceHolder.TOID,partnerId);
                        mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableDataFromForSingleRule(monitoringAlertDetailVO, merchantDetailsVO, termialId));
                        mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILENAME, fileAttachmentVO.getFileName());
                        mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILEPATH, fileAttachmentVO.getFilePath());
                       // mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_PARTNER_TECH, mailPlaceHolder);
                        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
                        asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_PARTNER_TECH,mailPlaceHolder);
                    }
                }
            }
        }
    }

    public void sendMailToPartnerSalesTeam(Map<String, List<MonitoringAlertDetailVO>> partnerSalesStringListHashMap, MerchantDetailsVO merchantDetailsVO, Map<String, List<MonitoringAlertDetailVO>> partnerSalesStringListHashMap1)
    {
        if (partnerSalesStringListHashMap.size() > 0)
        {
            logger.debug("Mail Should be to admin");
            MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID, "1");
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHash(partnerSalesStringListHashMap, merchantDetailsVO));
            mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ALERT_TO_PARTNER, mailPlaceHolder);
        }
    }

    public void sendMailToPartnerChargebackTeam(Map<String, List<MonitoringAlertDetailVO>> partnerCBStringListHashMap, MerchantDetailsVO merchantDetailsVO, Map<String, List<MonitoringAlertDetailVO>> partnerCBStringListHashMap1)
    {
        if (partnerCBStringListHashMap.size() > 0)
        {
            logger.debug("Mail Should be to admin");
            MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID, "1");
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHash(partnerCBStringListHashMap, merchantDetailsVO));
            mailService.sendMail(MailEventEnum.MERCHANT_CB_MONITORING_ALERT_TO_PARTNER, mailPlaceHolder);
        }
    }

    public void sendMailToPartnerRefundTeam(Map<String, List<MonitoringAlertDetailVO>> partnerRFStringListHashMap, MerchantDetailsVO merchantDetailsVO, Map<String, List<MonitoringAlertDetailVO>> partnerRFStringListHashMap1)
    {
        if (partnerRFStringListHashMap.size() > 0)
        {
            logger.debug("Mail should be to partner refund team");
            MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID, "1");
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHash(partnerRFStringListHashMap, merchantDetailsVO));
            mailService.sendMail(MailEventEnum.MERCHANT_RF_MONITORING_ALERT_TO_PARTNER, mailPlaceHolder);
        }
    }

    public void sendMailToPartnerFraudTeam(Map<String, List<MonitoringAlertDetailVO>> partnerFraudStringListHashMap, MerchantDetailsVO merchantDetailsVO, Map<String, List<MonitoringAlertDetailVO>> partnerFraudStringListHashMap1)
    {
        if (partnerFraudStringListHashMap.size() > 0)
        {

            logger.debug("Mail should be to partner fraud team");
            MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID, "1");
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHash(partnerFraudStringListHashMap, merchantDetailsVO));
            mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ALERT_TO_PARTNER_FRAUD, mailPlaceHolder);
        }
    }

    public void sendMailToPartnerTechnicalTeam(Map<String, List<MonitoringAlertDetailVO>> partnerTechnicalStringListHashMap, MerchantDetailsVO merchantDetailsVO, Map<String, List<MonitoringAlertDetailVO>> partnerTechnicalStringListHashMap1)
    {
        if (partnerTechnicalStringListHashMap.size() > 0)
        {

            logger.debug("Mail should be to partner technical team");
            MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID, "1");
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHash(partnerTechnicalStringListHashMap, merchantDetailsVO));
            mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ALERT_TO_PARTNER_TECH, mailPlaceHolder);
        }
    }

    public void sendMailToMerchantSalesTeam(Map<String, List<MonitoringAlertDetailVO>> merchantSalesStringListHashMapAlertOnly, MerchantDetailsVO merchantDetailsVO, Map<String, List<MonitoringAlertDetailVO>> merchantSalesStringListHashMapSuspension, Map<String, List<MonitoringAlertDetailVO>> merchantSalesStringAttachmentListHashMapPerTerminal,String monitoringFrequency)throws Exception
    {
        if (merchantSalesStringListHashMapAlertOnly.size() > 0)
        {
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID, merchantDetailsVO.getMemberId());
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHash(merchantSalesStringListHashMapAlertOnly, merchantDetailsVO));
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_ALERT_TO_MERCHANT,mailPlaceHolder);
            MerchantMonitoringManager monitoringManager=new MerchantMonitoringManager();
            int alertId=monitoringManager.logAlert(monitoringFrequency,"Sales",merchantDetailsVO.getMemberId(),"","");
            logger.debug("alertId----:::::"+alertId);
            Set set=merchantSalesStringListHashMapAlertOnly.keySet();
            Iterator iterator=set.iterator();
            while (iterator.hasNext())
            {
                String terminalId=(String)iterator.next();

                List<MonitoringAlertDetailVO> monitoringAlertDetailVOs=merchantSalesStringListHashMapAlertOnly.get(terminalId);
                for (MonitoringAlertDetailVO monitoringAlertDetailVO:monitoringAlertDetailVOs)
                {
                    String monitoringPeriod=monitoringAlertDetailVO.getMonitoringAlertPeriod();
                    String alertMsg=monitoringAlertDetailVO.getAlertMsg();
                    String ruleName=monitoringAlertDetailVO.getMonitoringAlertName();

                    String actualRatio=String.valueOf(monitoringAlertDetailVO.getActualratio());
                    String alertThreshold=String.valueOf(monitoringAlertDetailVO.getAlertThreshold());
                    String suspensionThreshold=String.valueOf(monitoringAlertDetailVO.getSuspensionThreshold());
                    String monitroingStartDate=String.valueOf(monitoringAlertDetailVO.getMonitroingStartDate());
                    String monitroingEndDate=String.valueOf(monitoringAlertDetailVO.getMonitroingEndDate());
                    String alertDate=String.valueOf(monitoringAlertDetailVO.getAlertDate());
                    //int terminalId=monitoringAlertDetailVO.getTerminalId();

                    //System.out.println("monitoringPeriod::::"+monitoringPeriod);

                    //TODO:Add log entry into database
                           /* merchantMonitoringManager.logRuleDetails(ruleName,ruleMessage,actualRatio,alertThreshold,suspentionThreshold,monitroingStartDate, moniotringEndDate, alertDate,terminalId, alertId);*/
                            /*merchantMonitoringManager.logRuleDetails(monitoringParameterVO.getMonitoringParameterName(),monitoringParameterVO.getDefaultAlertMsg(),"calculateThresholdWithLiveData().calculation","calculateThresholdDaily().calculation","suspensionThresholdAnalysisDaily().getAlertType()","alertThreshold","suspentionThreshold","monitroingStartDate", "moniotringEndDate", "alertDate","terminalId", "alertId");*/
                      monitoringManager.logRuleDetails(ruleName,alertMsg,actualRatio,alertThreshold,suspensionThreshold,monitroingStartDate,monitroingEndDate,alertDate,terminalId,String.valueOf(alertId));

                }
            }
        }
        if (merchantSalesStringListHashMapSuspension.size() > 0)
        {
            //System.out.println("Mail should be to merchant sales team ");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID, merchantDetailsVO.getMemberId());
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHash(merchantSalesStringListHashMapSuspension, merchantDetailsVO));
            //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_MERCHANT_SALES, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_MERCHANT_SALES,mailPlaceHolder);
            MerchantMonitoringManager monitoringManager=new MerchantMonitoringManager();
            int alertId=monitoringManager.logAlert(monitoringFrequency,"Sales",merchantDetailsVO.getMemberId(),"","");
            logger.debug("alertId----:::::"+alertId);
            Set set=merchantSalesStringListHashMapAlertOnly.keySet();
            Iterator iterator=set.iterator();
            while (iterator.hasNext())
            {
                String terminalId=(String)iterator.next();

                List<MonitoringAlertDetailVO> monitoringAlertDetailVOs=merchantSalesStringListHashMapAlertOnly.get(terminalId);
                for (MonitoringAlertDetailVO monitoringAlertDetailVO:monitoringAlertDetailVOs)
                {
                    String monitoringPeriod=monitoringAlertDetailVO.getMonitoringAlertPeriod();
                    String alertMsg=monitoringAlertDetailVO.getAlertMsg();
                    String ruleName=monitoringAlertDetailVO.getMonitoringAlertName();
                    String actualRatio=String.valueOf(monitoringAlertDetailVO.getActualratio());
                    String alertThreshold=String.valueOf(monitoringAlertDetailVO.getAlertThreshold());
                    String suspensionThreshold=String.valueOf(monitoringAlertDetailVO.getSuspensionThreshold());
                    String monitroingStartDate=String.valueOf(monitoringAlertDetailVO.getMonitroingStartDate());
                    String monitroingEndDate=String.valueOf(monitoringAlertDetailVO.getMonitroingEndDate());
                    String alertDate=String.valueOf(monitoringAlertDetailVO.getAlertDate());
                    //System.out.println("monitoringPeriod::::"+monitoringPeriod);

                    //TODO:Add log entry into database
                           /* merchantMonitoringManager.logRuleDetails(ruleName,ruleMessage,actualRatio,alertThreshold,suspentionThreshold,monitroingStartDate, moniotringEndDate, alertDate,terminalId, alertId);*/
                            /*merchantMonitoringManager.logRuleDetails(monitoringParameterVO.getMonitoringParameterName(),monitoringParameterVO.getDefaultAlertMsg(),"calculateThresholdWithLiveData().calculation","calculateThresholdDaily().calculation","suspensionThresholdAnalysisDaily().getAlertType()","alertThreshold","suspentionThreshold","monitroingStartDate", "moniotringEndDate", "alertDate","terminalId", "alertId");*/
                    monitoringManager.logRuleDetails(ruleName,alertMsg,actualRatio,alertThreshold,suspensionThreshold,monitroingStartDate,monitroingEndDate,alertDate,terminalId,String.valueOf(alertId));

                }
            }
        }
        if (merchantSalesStringAttachmentListHashMapPerTerminal.size() > 0)
        {
            Set set1 = merchantSalesStringAttachmentListHashMapPerTerminal.keySet();
            Iterator iterator1 = set1.iterator();
            while (iterator1.hasNext())
            {
                String termialId = (String) iterator1.next();

                List<MonitoringAlertDetailVO> monitoringAlertDetailVOs = merchantSalesStringAttachmentListHashMapPerTerminal.get(termialId);
                for (MonitoringAlertDetailVO monitoringAlertDetailVO : monitoringAlertDetailVOs)
                {
                    MonitoringAttachmentAlertDetailsVO monitoringAttachmentAlertDetailsVO = (MonitoringAttachmentAlertDetailsVO) monitoringAlertDetailVO;
                    FileAttachmentVO fileAttachmentVO = monitoringAttachmentAlertDetailsVO.getFileAttachmentVO();
                    String monitoringPeriod=monitoringAlertDetailVO.getMonitoringAlertPeriod();
                    String alertMsg=monitoringAlertDetailVO.getAlertMsg();
                    String ruleName=monitoringAlertDetailVO.getMonitoringAlertName();
                    String actualRatio=String.valueOf(monitoringAlertDetailVO.getActualratio());
                    String alertThreshold=String.valueOf(monitoringAlertDetailVO.getAlertThreshold());
                    String suspensionThreshold=String.valueOf(monitoringAlertDetailVO.getSuspensionThreshold());
                    String monitroingStartDate=String.valueOf(monitoringAlertDetailVO.getMonitroingStartDate());
                    String monitroingEndDate=String.valueOf(monitoringAlertDetailVO.getMonitroingEndDate());
                    String alertDate=String.valueOf(monitoringAlertDetailVO.getAlertDate());

                    //System.out.println("monitoringPeriod::::"+monitoringPeriod);

                    //MailService mailService = new MailService();
                    HashMap mailPlaceHolder = new HashMap();
                    MailServiceHelper mailServiceHelper = new MailServiceHelper();
                    mailPlaceHolder.put(MailPlaceHolder.TOID, merchantDetailsVO.getMemberId());
                    mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableDataFromForSingleRule(monitoringAlertDetailVO, merchantDetailsVO, termialId));
                    mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILENAME, fileAttachmentVO.getFileName());
                    mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILEPATH, fileAttachmentVO.getFilePath());
                    //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_MERCHANT_SALES, mailPlaceHolder);
                    AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
                    asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_MERCHANT_SALES,mailPlaceHolder);

                    MerchantMonitoringManager monitoringManager=new MerchantMonitoringManager();
                    int alertId= monitoringManager.logAlert(monitoringFrequency,"Sales",merchantDetailsVO.getMemberId(),termialId,fileAttachmentVO.getFileName());
                    logger.debug("alertId----:::::"+alertId);
                    monitoringManager.logRuleDetails(ruleName,alertMsg,actualRatio,alertThreshold,suspensionThreshold,monitroingStartDate,monitroingEndDate,alertDate,termialId,String.valueOf(alertId));
                }
            }
         }
    }

    public void sendMailToMerchantChargebackTeam(Map<String, List<MonitoringAlertDetailVO>> merchantCBListHashMapAlertOnly, MerchantDetailsVO merchantDetailsVO, Map<String, List<MonitoringAlertDetailVO>> merchantCBListHashMapSuspesnionAlert, Map<String, List<MonitoringAlertDetailVO>> merchantCBStringAttachmentListHashMapPerTerminal,String monitoringFrequency) throws Exception
    {
        if (merchantCBListHashMapAlertOnly.size() > 0)
        {
            logger.debug("Mail should be to merchant chargeback team");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID, merchantDetailsVO.getMemberId());
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHash(merchantCBListHashMapAlertOnly, merchantDetailsVO));
            //mailService.sendMail(MailEventEnum.MERCHANT_CB_MONITORING_ALERT_TO_MERCHANT, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_CB_MONITORING_ALERT_TO_MERCHANT,mailPlaceHolder);
            MerchantMonitoringManager monitoringManager=new MerchantMonitoringManager();
            int alertId=monitoringManager.logAlert(monitoringFrequency,"Chargeback",merchantDetailsVO.getMemberId(),"0","");
            logger.debug("alertId:::::"+alertId);
            Set set=merchantCBListHashMapAlertOnly.keySet();
            Iterator iterator=set.iterator();
            while (iterator.hasNext())
            {
                String terminalId= (String) iterator.next();
                List<MonitoringAlertDetailVO> monitoringAlertDetailVOs=merchantCBListHashMapAlertOnly.get(terminalId);
                for (MonitoringAlertDetailVO monitoringAlertDetailVO:monitoringAlertDetailVOs)
                {
                    String monitoringPeriod=monitoringAlertDetailVO.getMonitoringAlertPeriod();
                    String alertMsg=monitoringAlertDetailVO.getAlertMsg();
                    String ruleName=monitoringAlertDetailVO.getMonitoringAlertName();

                    String actualRatio=String.valueOf(monitoringAlertDetailVO.getActualratio());
                    String alertThreshold=String.valueOf(monitoringAlertDetailVO.getAlertThreshold());
                    String suspensionThreshold=String.valueOf(monitoringAlertDetailVO.getSuspensionThreshold());
                    monitoringManager.logRuleDetails(ruleName,alertMsg,actualRatio,alertThreshold,suspensionThreshold,"6","7","8","59",String.valueOf(alertId));
                }
            }
        }
        if (merchantCBListHashMapSuspesnionAlert.size() > 0)
        {
            logger.debug("Mail should be to merchant chargeback team");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID, merchantDetailsVO.getMemberId());
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHash(merchantCBListHashMapSuspesnionAlert, merchantDetailsVO));
            //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_MERCHANT_CB, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_MERCHANT_CB,mailPlaceHolder);
            MerchantMonitoringManager monitoringManager=new MerchantMonitoringManager();
            int alertId=monitoringManager.logAlert(monitoringFrequency,"Chargeback",merchantDetailsVO.getMemberId(),"0","");
            logger.debug("alertId:::::::"+alertId);
            Set set=merchantCBListHashMapSuspesnionAlert.keySet();
            Iterator iterator=set.iterator();
            while (iterator.hasNext())
            {
                String terminalId=(String)iterator.next();

                List<MonitoringAlertDetailVO> monitoringAlertDetailVOs=merchantCBListHashMapSuspesnionAlert.get(terminalId);
                for (MonitoringAlertDetailVO monitoringAlertDetailVO:monitoringAlertDetailVOs)
                {
                    String monitoringPeriod=monitoringAlertDetailVO.getMonitoringAlertPeriod();
                    String alertMsg=monitoringAlertDetailVO.getAlertMsg();
                    String ruleName=monitoringAlertDetailVO.getMonitoringAlertName();

                    String actualRatio=String.valueOf(monitoringAlertDetailVO.getActualratio());
                    String alertThreshold=String.valueOf(monitoringAlertDetailVO.getAlertThreshold());
                    String suspensionThreshold=String.valueOf(monitoringAlertDetailVO.getSuspensionThreshold());
                    //System.out.println("monitoringPeriod::::"+monitoringPeriod);

                    //TODO:Add log entry into database
                           /* merchantMonitoringManager.logRuleDetails(ruleName,ruleMessage,actualRatio,alertThreshold,suspentionThreshold,monitroingStartDate, moniotringEndDate, alertDate,terminalId, alertId);*/
                            /*merchantMonitoringManager.logRuleDetails(monitoringParameterVO.getMonitoringParameterName(),monitoringParameterVO.getDefaultAlertMsg(),"calculateThresholdWithLiveData().calculation","calculateThresholdDaily().calculation","suspensionThresholdAnalysisDaily().getAlertType()","alertThreshold","suspentionThreshold","monitroingStartDate", "moniotringEndDate", "alertDate","terminalId", "alertId");*/
                    monitoringManager.logRuleDetails(ruleName,alertMsg,actualRatio,alertThreshold,suspensionThreshold,"6","7","8",terminalId,String.valueOf(alertId));

                }
            }
        }
        if (merchantCBStringAttachmentListHashMapPerTerminal.size() > 0)
        {
            Set set1 = merchantCBStringAttachmentListHashMapPerTerminal.keySet();
            Iterator iterator1 = set1.iterator();
            while (iterator1.hasNext())
            {
                String termialId = (String) iterator1.next();
                List<MonitoringAlertDetailVO> monitoringAlertDetailVOs = merchantCBStringAttachmentListHashMapPerTerminal.get(termialId);
                for (MonitoringAlertDetailVO monitoringAlertDetailVO : monitoringAlertDetailVOs)
                {
                    MonitoringAttachmentAlertDetailsVO monitoringAttachmentAlertDetailsVO = (MonitoringAttachmentAlertDetailsVO) monitoringAlertDetailVO;
                    FileAttachmentVO fileAttachmentVO = monitoringAttachmentAlertDetailsVO.getFileAttachmentVO();
                    String monitoringPeriod=monitoringAlertDetailVO.getMonitoringAlertPeriod();
                    String alertMsg=monitoringAlertDetailVO.getAlertMsg();
                    String ruleName=monitoringAlertDetailVO.getMonitoringAlertName();

                    String actualRatio=String.valueOf(monitoringAlertDetailVO.getActualratio());
                    String alertThreshold=String.valueOf(monitoringAlertDetailVO.getAlertThreshold());
                    String suspensionThreshold=String.valueOf(monitoringAlertDetailVO.getSuspensionThreshold());
                    //System.out.println("monitoringPeriod::::"+monitoringPeriod);
                    //MailService mailService = new MailService();
                    HashMap mailPlaceHolder = new HashMap();
                    MailServiceHelper mailServiceHelper = new MailServiceHelper();
                    mailPlaceHolder.put(MailPlaceHolder.TOID, merchantDetailsVO.getMemberId());
                    mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableDataFromForSingleRule(monitoringAlertDetailVO, merchantDetailsVO, termialId));
                    mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILENAME, fileAttachmentVO.getFileName());
                    mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILEPATH, fileAttachmentVO.getFilePath());
                    //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_MERCHANT_CB, mailPlaceHolder);
                    AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
                    asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_MERCHANT_CB,mailPlaceHolder);
                    MerchantMonitoringManager monitoringManager=new MerchantMonitoringManager();
                    int alertId=monitoringManager.logAlert(monitoringFrequency,"Chargeback",merchantDetailsVO.getMemberId(),termialId,fileAttachmentVO.getFileName());
                    logger.debug("alertId----:::::"+alertId);
                    monitoringManager.logRuleDetails(ruleName,alertMsg,actualRatio,alertThreshold,suspensionThreshold,"6","7","8",termialId,String.valueOf(alertId));
                }
            }
        }

    }

    public void sendMailToMerchantRefundTeam(Map<String, List<MonitoringAlertDetailVO>> merchantRFStringListHashMap, MerchantDetailsVO merchantDetailsVO, Map<String, List<MonitoringAlertDetailVO>> merchantRFListHashMapSuspensionAlert, Map<String, List<MonitoringAlertDetailVO>> merchantRFStringAttachmentListHashMapPerTerminal,String monitoringFrequency) throws Exception
    {
        if (merchantRFStringListHashMap.size() > 0)
        {
            //System.out.println("Mail should be to merchant refund team");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID, merchantDetailsVO.getMemberId());
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHash(merchantRFStringListHashMap, merchantDetailsVO));
            //mailService.sendMail(MailEventEnum.MERCHANT_RF_MONITORING_ALERT_TO_MERCHANT, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_RF_MONITORING_ALERT_TO_MERCHANT,mailPlaceHolder);
            MerchantMonitoringManager monitoringManager=new MerchantMonitoringManager();
            int alertId=monitoringManager.logAlert(monitoringFrequency,"Refund",merchantDetailsVO.getMemberId(),"0","");
            logger.debug("alertId::::"+alertId);
            Set set=merchantRFStringListHashMap.keySet();
            Iterator iterator=set.iterator();
            while (iterator.hasNext())
            {
                String terminalId= (String) iterator.next();
                List<MonitoringAlertDetailVO> monitoringAlertDetailVOs=merchantRFStringListHashMap.get(terminalId);
                for (MonitoringAlertDetailVO monitoringAlertDetailVO:monitoringAlertDetailVOs)
                {
                    String monitoringPeriod=monitoringAlertDetailVO.getMonitoringAlertPeriod();
                    String alertMsg=monitoringAlertDetailVO.getAlertMsg();
                    String ruleName=monitoringAlertDetailVO.getMonitoringAlertName();

                    String actualRatio=String.valueOf(monitoringAlertDetailVO.getActualratio());
                    String alertThreshold=String.valueOf(monitoringAlertDetailVO.getAlertThreshold());
                    String suspensionThreshold=String.valueOf(monitoringAlertDetailVO.getSuspensionThreshold());
                    monitoringManager.logRuleDetails(ruleName,alertMsg,actualRatio,alertThreshold,suspensionThreshold,"6","7","8","59",String.valueOf(alertId));
                }
            }
        }
        if (merchantRFListHashMapSuspensionAlert.size() > 0)
        {
            //System.out.println("Mail should be to merchant refund team");
           // MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID, merchantDetailsVO.getMemberId());
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHash(merchantRFListHashMapSuspensionAlert, merchantDetailsVO));
            //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_MERCHANT_RF, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_MERCHANT_RF,mailPlaceHolder);
            MerchantMonitoringManager monitoringManager=new MerchantMonitoringManager();
            int alertId=monitoringManager.logAlert(monitoringFrequency,"Refund",merchantDetailsVO.getMemberId(),"0","");
            logger.debug("alertId:::::::::"+alertId);
            Set set=merchantRFListHashMapSuspensionAlert.keySet();
            Iterator iterator=set.iterator();
            while (iterator.hasNext())
            {
                String terminalId = (String) iterator.next();

                List<MonitoringAlertDetailVO> monitoringAlertDetailVOs = merchantRFListHashMapSuspensionAlert.get(terminalId);
                for (MonitoringAlertDetailVO monitoringAlertDetailVO : monitoringAlertDetailVOs)
                {
                    String monitoringPeriod = monitoringAlertDetailVO.getMonitoringAlertPeriod();
                    String alertMsg = monitoringAlertDetailVO.getAlertMsg();
                    String ruleName = monitoringAlertDetailVO.getMonitoringAlertName();

                    String actualRatio=String.valueOf(monitoringAlertDetailVO.getActualratio());
                    String alertThreshold=String.valueOf(monitoringAlertDetailVO.getAlertThreshold());
                    String suspensionThreshold=String.valueOf(monitoringAlertDetailVO.getSuspensionThreshold());
                    //System.out.println("monitoringPeriod::::" + monitoringPeriod);

                    //TODO:Add log entry into database
                           /* merchantMonitoringManager.logRuleDetails(ruleName,ruleMessage,actualRatio,alertThreshold,suspentionThreshold,monitroingStartDate, moniotringEndDate, alertDate,terminalId, alertId);*/
                            /*merchantMonitoringManager.logRuleDetails(monitoringParameterVO.getMonitoringParameterName(),monitoringParameterVO.getDefaultAlertMsg(),"calculateThresholdWithLiveData().calculation","calculateThresholdDaily().calculation","suspensionThresholdAnalysisDaily().getAlertType()","alertThreshold","suspentionThreshold","monitroingStartDate", "moniotringEndDate", "alertDate","terminalId", "alertId");*/
                    monitoringManager.logRuleDetails(ruleName, alertMsg,actualRatio,alertThreshold,suspensionThreshold , "6", "7", "8", terminalId, String.valueOf(alertId));

                }
            }
        }
        if (merchantRFStringAttachmentListHashMapPerTerminal.size() > 0)
        {
            Set set1 = merchantRFStringAttachmentListHashMapPerTerminal.keySet();
            Iterator iterator1 = set1.iterator();
            while (iterator1.hasNext())
            {
                String termialId = (String) iterator1.next();
                List<MonitoringAlertDetailVO> monitoringAlertDetailVOs = merchantRFStringAttachmentListHashMapPerTerminal.get(termialId);
                for (MonitoringAlertDetailVO monitoringAlertDetailVO : monitoringAlertDetailVOs)
                {
                    MonitoringAttachmentAlertDetailsVO monitoringAttachmentAlertDetailsVO = (MonitoringAttachmentAlertDetailsVO) monitoringAlertDetailVO;
                    FileAttachmentVO fileAttachmentVO = monitoringAttachmentAlertDetailsVO.getFileAttachmentVO();
                    MailService mailService = new MailService();
                    String monitoringPeriod=monitoringAlertDetailVO.getMonitoringAlertPeriod();
                    String alertMsg=monitoringAlertDetailVO.getAlertMsg();
                    String ruleName=monitoringAlertDetailVO.getMonitoringAlertName();

                    String actualRatio=String.valueOf(monitoringAlertDetailVO.getActualratio());
                    String alertThreshold=String.valueOf(monitoringAlertDetailVO.getAlertThreshold());
                    String suspensionThreshold=String.valueOf(monitoringAlertDetailVO.getSuspensionThreshold());
                    //System.out.println("monitoringPeriod::::"+monitoringPeriod);
                    HashMap mailPlaceHolder = new HashMap();
                    MailServiceHelper mailServiceHelper = new MailServiceHelper();
                    mailPlaceHolder.put(MailPlaceHolder.TOID, merchantDetailsVO.getMemberId());
                    mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableDataFromForSingleRule(monitoringAlertDetailVO, merchantDetailsVO, termialId));
                    mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILENAME, fileAttachmentVO.getFileName());
                    mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILEPATH, fileAttachmentVO.getFilePath());
                    mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_MERCHANT_RF, mailPlaceHolder);
                    AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
                    asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_MERCHANT_RF,mailPlaceHolder);
                    MerchantMonitoringManager monitoringManager=new MerchantMonitoringManager();
                    int alertId=monitoringManager.logAlert(monitoringFrequency,"Refund",merchantDetailsVO.getMemberId(),termialId,fileAttachmentVO.getFileName());
                    logger.debug("alertId----:::::"+alertId);
                    monitoringManager.logRuleDetails(ruleName,alertMsg,actualRatio,alertThreshold,suspensionThreshold,"6","7","8",termialId,String.valueOf(alertId));
                }
            }
        }

    }

    public void sendMailToMerchantFraudTeam(Map<String, List<MonitoringAlertDetailVO>> merchantFraudListHashMapSales, MerchantDetailsVO merchantDetailsVO, Map<String, List<MonitoringAlertDetailVO>> merchantFraudListHashMapSuspesionAlert, Map<String, List<MonitoringAlertDetailVO>> merchantFraudStringAttachmentListHashMapPerTerminal,String monitoringFrequency) throws Exception
    {
        if (merchantFraudListHashMapSales.size() > 0)
        {
            //System.out.println("Mail should be to merchant fraud team");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID, merchantDetailsVO.getMemberId());
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHash(merchantFraudListHashMapSales, merchantDetailsVO));
            //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ALERT_TO_MERCHANT_FRAUD, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_ALERT_TO_MERCHANT_FRAUD,mailPlaceHolder);
            MerchantMonitoringManager monitoringManager=new MerchantMonitoringManager();
            int alertId=monitoringManager.logAlert(monitoringFrequency,"Fraud",merchantDetailsVO.getMemberId(),"0","");
            logger.debug("alertId:::"+alertId);
            Set set=merchantFraudListHashMapSales.keySet();
            Iterator iterator=set.iterator();
            while (iterator.hasNext())
            {
                String terminalId= (String) iterator.next();
                List<MonitoringAlertDetailVO> monitoringAlertDetailVOs=merchantFraudListHashMapSales.get(terminalId);
                for (MonitoringAlertDetailVO monitoringAlertDetailVO:monitoringAlertDetailVOs)
                {
                    String monitoringPeriod=monitoringAlertDetailVO.getMonitoringAlertPeriod();
                    String alertMsg=monitoringAlertDetailVO.getAlertMsg();
                    String ruleName=monitoringAlertDetailVO.getMonitoringAlertName();

                    String actualRatio=String.valueOf(monitoringAlertDetailVO.getActualratio());
                    String alertThreshold=String.valueOf(monitoringAlertDetailVO.getAlertThreshold());
                    String suspensionThreshold=String.valueOf(monitoringAlertDetailVO.getSuspensionThreshold());
                    monitoringManager.logRuleDetails(ruleName, alertMsg,actualRatio ,alertThreshold , suspensionThreshold, "6", "7", "8", "59", String.valueOf(alertId));
                }
            }
        }
        if (merchantFraudListHashMapSuspesionAlert.size() > 0)
        {
            //System.out.println("Mail should be to merchant fraud team");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID, merchantDetailsVO.getMemberId());
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHash(merchantFraudListHashMapSuspesionAlert, merchantDetailsVO));
            //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_MERCHANT_FRAUD, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_MERCHANT_FRAUD,mailPlaceHolder);
            MerchantMonitoringManager monitoringManager=new MerchantMonitoringManager();
            int alertId=monitoringManager.logAlert(monitoringFrequency,"Fraud",merchantDetailsVO.getMemberId(),"0","");
            logger.debug("alertId:::::::"+alertId);
            Set set=merchantFraudListHashMapSuspesionAlert.keySet();
            Iterator iterator=set.iterator();
            while (iterator.hasNext())
            {
                String terminalId=(String)iterator.next();

                List<MonitoringAlertDetailVO> monitoringAlertDetailVOs=merchantFraudListHashMapSuspesionAlert.get(terminalId);
                for (MonitoringAlertDetailVO monitoringAlertDetailVO:monitoringAlertDetailVOs)
                {
                    String monitoringPeriod=monitoringAlertDetailVO.getMonitoringAlertPeriod();
                    String alertMsg=monitoringAlertDetailVO.getAlertMsg();
                    String ruleName=monitoringAlertDetailVO.getMonitoringAlertName();

                    String actualRatio=String.valueOf(monitoringAlertDetailVO.getActualratio());
                    String alertThreshold=String.valueOf(monitoringAlertDetailVO.getAlertThreshold());
                    String suspensionThreshold=String.valueOf(monitoringAlertDetailVO.getSuspensionThreshold());
                    //System.out.println("monitoringPeriod::::"+monitoringPeriod);

                    //TODO:Add log entry into database
                           /* merchantMonitoringManager.logRuleDetails(ruleName,ruleMessage,actualRatio,alertThreshold,suspentionThreshold,monitroingStartDate, moniotringEndDate, alertDate,terminalId, alertId);*/
                            /*merchantMonitoringManager.logRuleDetails(monitoringParameterVO.getMonitoringParameterName(),monitoringParameterVO.getDefaultAlertMsg(),"calculateThresholdWithLiveData().calculation","calculateThresholdDaily().calculation","suspensionThresholdAnalysisDaily().getAlertType()","alertThreshold","suspentionThreshold","monitroingStartDate", "moniotringEndDate", "alertDate","terminalId", "alertId");*/
                    monitoringManager.logRuleDetails(ruleName,alertMsg,actualRatio,alertThreshold,suspensionThreshold,"6","7","8",terminalId,String.valueOf(alertId));

                }
            }
        }
        if (merchantFraudStringAttachmentListHashMapPerTerminal.size() > 0)
        {
            Set set1 = merchantFraudStringAttachmentListHashMapPerTerminal.keySet();
            Iterator iterator1 = set1.iterator();
            while (iterator1.hasNext())
            {
                String termialId = (String) iterator1.next();
                List<MonitoringAlertDetailVO> monitoringAlertDetailVOs = merchantFraudStringAttachmentListHashMapPerTerminal.get(termialId);
                for (MonitoringAlertDetailVO monitoringAlertDetailVO : monitoringAlertDetailVOs)
                {
                    MonitoringAttachmentAlertDetailsVO monitoringAttachmentAlertDetailsVO = (MonitoringAttachmentAlertDetailsVO) monitoringAlertDetailVO;
                    FileAttachmentVO fileAttachmentVO = monitoringAttachmentAlertDetailsVO.getFileAttachmentVO();
                    String monitoringPeriod=monitoringAlertDetailVO.getMonitoringAlertPeriod();
                    String alertMsg=monitoringAlertDetailVO.getAlertMsg();
                    String ruleName=monitoringAlertDetailVO.getMonitoringAlertName();

                    String actualRatio=String.valueOf(monitoringAlertDetailVO.getActualratio());
                    String alertThreshold=String.valueOf(monitoringAlertDetailVO.getAlertThreshold());
                    String suspensionThreshold=String.valueOf(monitoringAlertDetailVO.getSuspensionThreshold());
                    //System.out.println("monitoringPeriod::::"+monitoringPeriod);
                    //MailService mailService = new MailService();
                    HashMap mailPlaceHolder = new HashMap();
                    MailServiceHelper mailServiceHelper = new MailServiceHelper();
                    mailPlaceHolder.put(MailPlaceHolder.TOID, merchantDetailsVO.getMemberId());
                    mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableDataFromForSingleRule(monitoringAlertDetailVO, merchantDetailsVO, termialId));
                    mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILENAME, fileAttachmentVO.getFileName());
                    mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILEPATH, fileAttachmentVO.getFilePath());
                    //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_MERCHANT_FRAUD, mailPlaceHolder);
                    AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
                    asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_MERCHANT_FRAUD,mailPlaceHolder);
                    MerchantMonitoringManager monitoringManager=new MerchantMonitoringManager();
                    int alertId=monitoringManager.logAlert(monitoringFrequency,"Fraud",merchantDetailsVO.getMemberId(),termialId,fileAttachmentVO.getFileName());
                    logger.debug("alertId----:::::"+alertId);
                    monitoringManager.logRuleDetails(ruleName,alertMsg,actualRatio,alertThreshold,suspensionThreshold,"6","7","8",termialId,String.valueOf(alertId));
                }
            }
        }
    }

    public void sendMailToMerchantTechnicalTeam(Map<String, List<MonitoringAlertDetailVO>> merchantTechListHashMapAlertOnly, MerchantDetailsVO merchantDetailsVO, Map<String, List<MonitoringAlertDetailVO>> merchantTechListHashMapSuspensionAlert, Map<String, List<MonitoringAlertDetailVO>> merchantTechStringAttachmentListHashMapPerTerminal,String monitoringFrequency) throws Exception
    {
        if (merchantTechListHashMapAlertOnly.size() > 0)
        {
            logger.debug("Mail should be to merchant technical team");
           // MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID, merchantDetailsVO.getMemberId());
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHash(merchantTechListHashMapAlertOnly, merchantDetailsVO));
            //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ALERT_TO_MERCHANT_TECH, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_ALERT_TO_MERCHANT_TECH,mailPlaceHolder);
            MerchantMonitoringManager monitoringManager=new MerchantMonitoringManager();
            int alertId=monitoringManager.logAlert(monitoringFrequency,"Technical",merchantDetailsVO.getMemberId(),"0","");
            logger.debug("alertId:::::"+alertId);
            Set set=merchantTechListHashMapAlertOnly.keySet();
            Iterator iterator=set.iterator();
            while (iterator.hasNext())
            {
                String terminalId= (String) iterator.next();
                List<MonitoringAlertDetailVO> monitoringAlertDetailVOs=merchantTechListHashMapAlertOnly.get(terminalId);
                for (MonitoringAlertDetailVO monitoringAlertDetailVO:monitoringAlertDetailVOs)
                {
                    String monitoringPeriod=monitoringAlertDetailVO.getMonitoringAlertPeriod();
                    String alertMsg=monitoringAlertDetailVO.getAlertMsg();
                    String ruleName=monitoringAlertDetailVO.getMonitoringAlertName();

                    String actualRatio=String.valueOf(monitoringAlertDetailVO.getActualratio());
                    String alertThreshold=String.valueOf(monitoringAlertDetailVO.getAlertThreshold());
                    String suspensionThreshold=String.valueOf(monitoringAlertDetailVO.getSuspensionThreshold());
                    monitoringManager.logRuleDetails(ruleName,alertMsg,actualRatio,alertThreshold,suspensionThreshold,"6","7","8","59",String.valueOf(alertId));
                }
            }
        }
        if (merchantTechListHashMapSuspensionAlert.size() > 0)
        {
            logger.debug("Mail should be to merchant technical team");
            //MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.TOID, merchantDetailsVO.getMemberId());
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableListFromHash(merchantTechListHashMapSuspensionAlert, merchantDetailsVO));
            //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_MERCHANT_TECH, mailPlaceHolder);
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_MERCHANT_TECH,mailPlaceHolder);
            MerchantMonitoringManager monitoringManager=new MerchantMonitoringManager();
            int alertId=monitoringManager.logAlert(monitoringFrequency, "Technical", merchantDetailsVO.getMemberId(), "0","");
            logger.debug("alertId:::::::"+alertId);
            Set set=merchantTechListHashMapSuspensionAlert.keySet();
            Iterator iterator=set.iterator();
            while (iterator.hasNext())
            {
                String terminalId=(String)iterator.next();

                List<MonitoringAlertDetailVO> monitoringAlertDetailVOs=merchantTechListHashMapSuspensionAlert.get(terminalId);
                for (MonitoringAlertDetailVO monitoringAlertDetailVO:monitoringAlertDetailVOs)
                {
                    String monitoringPeriod=monitoringAlertDetailVO.getMonitoringAlertPeriod();
                    String alertMsg=monitoringAlertDetailVO.getAlertMsg();
                    String ruleName=monitoringAlertDetailVO.getMonitoringAlertName();

                    String actualRatio=String.valueOf(monitoringAlertDetailVO.getActualratio());
                    String alertThreshold=String.valueOf(monitoringAlertDetailVO.getAlertThreshold());
                    String suspensionThreshold=String.valueOf(monitoringAlertDetailVO.getSuspensionThreshold());
                    //System.out.println("monitoringPeriod::::"+monitoringPeriod);

                    //TODO:Add log entry into database
                           /* merchantMonitoringManager.logRuleDetails(ruleName,ruleMessage,actualRatio,alertThreshold,suspentionThreshold,monitroingStartDate, moniotringEndDate, alertDate,terminalId, alertId);*/
                            /*merchantMonitoringManager.logRuleDetails(monitoringParameterVO.getMonitoringParameterName(),monitoringParameterVO.getDefaultAlertMsg(),"calculateThresholdWithLiveData().calculation","calculateThresholdDaily().calculation","suspensionThresholdAnalysisDaily().getAlertType()","alertThreshold","suspentionThreshold","monitroingStartDate", "moniotringEndDate", "alertDate","terminalId", "alertId");*/
                    monitoringManager.logRuleDetails(ruleName,alertMsg,actualRatio,alertThreshold,suspensionThreshold,"6","7","8",terminalId,String.valueOf(alertId));

                }
            }
        }
        if (merchantTechStringAttachmentListHashMapPerTerminal.size() > 0)
        {
            Set set1 = merchantTechStringAttachmentListHashMapPerTerminal.keySet();
            Iterator iterator1 = set1.iterator();
            while (iterator1.hasNext())
            {
                String termialId = (String) iterator1.next();
                List<MonitoringAlertDetailVO> monitoringAlertDetailVOs = merchantTechStringAttachmentListHashMapPerTerminal.get(termialId);
                for (MonitoringAlertDetailVO monitoringAlertDetailVO : monitoringAlertDetailVOs)
                {
                    MonitoringAttachmentAlertDetailsVO monitoringAttachmentAlertDetailsVO = (MonitoringAttachmentAlertDetailsVO) monitoringAlertDetailVO;
                    FileAttachmentVO fileAttachmentVO = monitoringAttachmentAlertDetailsVO.getFileAttachmentVO();
                    String monitoringPeriod=monitoringAlertDetailVO.getMonitoringAlertPeriod();
                    String alertMsg=monitoringAlertDetailVO.getAlertMsg();
                    String ruleName=monitoringAlertDetailVO.getMonitoringAlertName();

                    String actualRatio=String.valueOf(monitoringAlertDetailVO.getActualratio());
                    String alertThreshold=String.valueOf(monitoringAlertDetailVO.getAlertThreshold());
                    String suspensionThreshold=String.valueOf(monitoringAlertDetailVO.getSuspensionThreshold());
                    //System.out.println("monitoringPeriod::::"+monitoringPeriod);
                    //MailService mailService = new MailService();
                    HashMap mailPlaceHolder = new HashMap();
                    MailServiceHelper mailServiceHelper = new MailServiceHelper();
                    mailPlaceHolder.put(MailPlaceHolder.TOID, merchantDetailsVO.getMemberId());
                    mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getDetailTableDataFromForSingleRule(monitoringAlertDetailVO, merchantDetailsVO, termialId));
                    mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILENAME, fileAttachmentVO.getFileName());
                    mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILEPATH, fileAttachmentVO.getFilePath());
                   //mailService.sendMail(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_MERCHANT_TECH, mailPlaceHolder);
                    AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
                    asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_MERCHANT_TECH,mailPlaceHolder);
                    MerchantMonitoringManager monitoringManager=new MerchantMonitoringManager();
                    int alertId=monitoringManager.logAlert(monitoringFrequency,"Technical",merchantDetailsVO.getMemberId(),termialId,fileAttachmentVO.getFileName());
                    logger.debug("alertId----:::::"+alertId);
                    monitoringManager.logRuleDetails(ruleName,alertMsg,actualRatio,alertThreshold,suspensionThreshold,"6","7","8",termialId,String.valueOf(alertId));

                }
            }
        }
    }


    public void setMailIntoAppropriateListGroup(MonitoringAlertDetailVO monitoringAlertDetailVO, List<MonitoringAlertDetailVO> adminSalesMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> adminCBMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> adminRFMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> adminFraudMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> adminTechMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> agentSalesMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> agentCBMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> agentRFMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> agentFraudMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> agentTechMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> merchantCBMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> merchantRFMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> merchantSalesMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> merchantFraudMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> merchantTechnicalMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> partnerCBMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> partnerRFMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> partnerSalesMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> partnerFraudMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> partnerTechnicalMonitoringAlertDetailVOs, MonitoringParameterMappingVO monitoringParameterMappingVO)
    {
        logger.debug("isAlertToAdmin==" + monitoringAlertDetailVO.isAlertToAdmin());
        if (monitoringAlertDetailVO.isAlertToAdmin())
        {
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAdminSales()))
            {
                adminSalesMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAdminCB()))
            {
                adminCBMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAdminRF()))
            {
                adminRFMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAdminFraud()))
            {
                adminFraudMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAdminTech()))
            {
                adminTechMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
        }
        if (monitoringAlertDetailVO.isAlertToMerchant())
        {
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToMerchantSales()))
            {
                merchantSalesMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToMerchantRF()))
            {
                merchantRFMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToMerchantCB()))
            {
                merchantCBMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToMerchantFraud()))
            {
                merchantFraudMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToMerchantTech()))
            {
                merchantTechnicalMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
        }
        if (monitoringAlertDetailVO.isAlertToPartner())
        {
            logger.debug("only Alert getIsAlertToPartnerFraud==" + monitoringParameterMappingVO.getIsAlertToPartnerFraud());
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToPartnerSales()))
            {
                partnerSalesMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToPartnerFraud()))
            {
                partnerFraudMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToPartnerCB()))
            {
                partnerCBMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToPartnerRF()))
            {
                partnerRFMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToPartnerTech()))
            {
                partnerTechnicalMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
        }
        if (monitoringAlertDetailVO.isAlertToAgent())
        {
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAgentSales()))
            {
                agentSalesMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAgentCB()))
            {
                agentCBMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAgentRF()))
            {
                agentRFMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAgentFraud()))
            {
                agentFraudMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAgentTech()))
            {
                agentTechMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }

        }
    }

    public void setMailIntoAppropriateListGroup2(MonitoringAlertDetailVO monitoringAlertDetailVO, List<MonitoringAlertDetailVO> adminSalesMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> adminCBMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> adminRFMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> adminFraudMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> adminTechMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> merchantSalesMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> merchantCBMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> merchantRFMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> merchantFraudMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> merchantTechMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> agentSalesMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> agentCBMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> agentRFMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> agentFraudMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> agentTechMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> partnerSalesMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> partnerCBMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> partnerRFMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> partnerFraudMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> partnerTechMonitoringAttachmentAlertDetailVOs, MonitoringParameterMappingVO monitoringParameterMappingVO)
    {
        if (monitoringAlertDetailVO.isAlertToAdmin())
        {
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAdminSales()))
            {
                adminSalesMonitoringAttachmentAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAdminCB()))
            {
                adminCBMonitoringAttachmentAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAdminRF()))
            {
                adminRFMonitoringAttachmentAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAdminFraud()))
            {
                adminFraudMonitoringAttachmentAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAdminTech()))
            {
                adminTechMonitoringAttachmentAlertDetailVOs.add(monitoringAlertDetailVO);
            }
        }
        if (monitoringAlertDetailVO.isAlertToMerchant())
        {
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToMerchantSales()))
            {
                merchantSalesMonitoringAttachmentAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToMerchantCB()))
            {
                merchantCBMonitoringAttachmentAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToMerchantRF()))
            {
                merchantRFMonitoringAttachmentAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToMerchantFraud()))
            {
                merchantFraudMonitoringAttachmentAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToMerchantTech()))
            {
                merchantTechMonitoringAttachmentAlertDetailVOs.add(monitoringAlertDetailVO);
            }
        }

        if (monitoringAlertDetailVO.isAlertToPartner())
        {
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToPartnerSales()))
            {
                partnerSalesMonitoringAttachmentAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToPartnerCB()))
            {
                partnerCBMonitoringAttachmentAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToPartnerRF()))
            {
                partnerRFMonitoringAttachmentAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToPartnerFraud()))
            {
                partnerFraudMonitoringAttachmentAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToPartnerTech()))
            {
                partnerTechMonitoringAttachmentAlertDetailVOs.add(monitoringAlertDetailVO);
            }
        }
        if (monitoringAlertDetailVO.isAlertToAgent())
        {
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAgentSales()))
            {
                agentSalesMonitoringAttachmentAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAgentCB()))
            {
                agentCBMonitoringAttachmentAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAgentRF()))
            {
                agentRFMonitoringAttachmentAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAgentFraud()))
            {
                agentFraudMonitoringAttachmentAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAgentTech()))
            {
                agentTechMonitoringAttachmentAlertDetailVOs.add(monitoringAlertDetailVO);
            }
        }
    }

    public void setMailIntoAppropriateListGroup1(MonitoringAlertDetailVO monitoringAlertDetailVO, List<MonitoringAlertDetailVO> adminSalesMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> adminCBMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> adminRFMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> adminFraudMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> adminTechMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> agentSalesMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> agentCBMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> agentRFMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> agentFraudMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> agentTechMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> merchantCBMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> merchantRFMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> merchantSalesMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> merchantFraudMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> merchantTechnicalMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> partnerCBMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> partnerRFMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> partnerSalesMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> partnerFraudMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> partnerTechnicalMonitoringAlertDetailVOs, MonitoringParameterMappingVO monitoringParameterMappingVO)
    {

        if (monitoringAlertDetailVO.isAlertToAdmin())
        {

            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAdminSales()))
            {
                adminSalesMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAdminCB()))
            {
                adminCBMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAdminRF()))
            {
                adminRFMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAdminFraud()))
            {
                adminFraudMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAdminTech()))
            {
                adminTechMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
        }
        if (monitoringAlertDetailVO.isAlertToMerchant())
        {
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToMerchantSales()))
            {
                merchantSalesMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToMerchantRF()))
            {
                merchantRFMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToMerchantCB()))
            {
                merchantCBMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToMerchantFraud()))
            {
                merchantFraudMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToMerchantTech()))
            {
                merchantTechnicalMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
        }
        if (monitoringAlertDetailVO.isAlertToPartner())
        {
            logger.debug("Suspension Alert getIsAlertToPartnerFraud==" + monitoringParameterMappingVO.getIsAlertToPartnerFraud());
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToPartnerSales()))
            {
                partnerSalesMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToPartnerFraud()))
            {
                partnerFraudMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToPartnerCB()))
            {
                partnerCBMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToPartnerRF()))
            {
                partnerRFMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToPartnerTech()))
            {
                partnerTechnicalMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
        }
        if (monitoringAlertDetailVO.isAlertToAgent())
        {
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAgentSales()))
            {
                agentSalesMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAgentCB()))
            {
                agentCBMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAgentRF()))
            {
                agentRFMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAgentFraud()))
            {
                agentFraudMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
            if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAgentTech()))
            {
                agentTechMonitoringAlertDetailVOs.add(monitoringAlertDetailVO);
            }
        }
    }

    public void setMailIntoAppropriateMapGroup(String terminalId, List<MonitoringAlertDetailVO> adminSalesMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> adminCBMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> adminRFMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> adminFraudMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> adminTechMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> agentSalesMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> agentCBMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> agentRFMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> agentFraudMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> agentTechMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> merchantCBMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> merchantRFMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> merchantSalesMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> merchantFraudMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> merchantTechnicalMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> partnerCBMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> partnerRFMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> partnerSalesMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> partnerFraudMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> partnerTechnicalMonitoringAlertDetailVOs, Map<String, List<MonitoringAlertDetailVO>> adminSalesStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> adminCBStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> adminRFStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> adminFraudStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> adminTechStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> agentSalesStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> agentCBStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> agentRFStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> agentFraudStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> agentTechStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> merchantSalesStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> merchantCBStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> merchantRFStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> merchantFraudStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> merchantTechnicalStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> partnerCBStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> partnerRFStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> partnerSalesStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> partnerFraudStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> partnerTechnicalStringListHashMap)
    {
        //System.out.println("adminMonitoringAlertDetailVOs size===" + adminSalesMonitoringAlertDetailVOs.size());
        if (adminSalesMonitoringAlertDetailVOs.size() > 0)
        {
            adminSalesStringListHashMap.put(terminalId, adminSalesMonitoringAlertDetailVOs);
        }
        if (adminCBMonitoringAlertDetailVOs.size() > 0)
        {
            adminCBStringListHashMap.put(terminalId, adminCBMonitoringAlertDetailVOs);
        }
        if (adminRFMonitoringAlertDetailVOs.size() > 0)
        {
            adminRFStringListHashMap.put(terminalId, adminRFMonitoringAlertDetailVOs);
        }
        if (adminFraudMonitoringAlertDetailVOs.size() > 0)
        {
            adminFraudStringListHashMap.put(terminalId, adminFraudMonitoringAlertDetailVOs);
        }
        if (adminTechMonitoringAlertDetailVOs.size() > 0)
        {
            adminTechStringListHashMap.put(terminalId, adminTechMonitoringAlertDetailVOs);
        }

        if (agentSalesMonitoringAlertDetailVOs.size() > 0)
        {
            agentSalesStringListHashMap.put(terminalId, agentSalesMonitoringAlertDetailVOs);
        }
        if (agentCBMonitoringAlertDetailVOs.size() > 0)
        {
            agentCBStringListHashMap.put(terminalId, agentCBMonitoringAlertDetailVOs);
        }
        if (agentRFMonitoringAlertDetailVOs.size() > 0)
        {
            agentRFStringListHashMap.put(terminalId, agentRFMonitoringAlertDetailVOs);
        }
        if (agentFraudMonitoringAlertDetailVOs.size() > 0)
        {
            agentFraudStringListHashMap.put(terminalId, agentFraudMonitoringAlertDetailVOs);
        }
        if (agentTechMonitoringAlertDetailVOs.size() > 0)
        {
            agentTechStringListHashMap.put(terminalId, agentTechMonitoringAlertDetailVOs);
        }

        if (merchantSalesMonitoringAlertDetailVOs.size() > 0)
        {
            merchantSalesStringListHashMap.put(terminalId, merchantSalesMonitoringAlertDetailVOs);
        }
        if (merchantCBMonitoringAlertDetailVOs.size() > 0)
        {
            merchantCBStringListHashMap.put(terminalId, merchantCBMonitoringAlertDetailVOs);
        }
        if (merchantRFMonitoringAlertDetailVOs.size() > 0)
        {
            merchantRFStringListHashMap.put(terminalId, merchantRFMonitoringAlertDetailVOs);
        }
        if (merchantFraudMonitoringAlertDetailVOs.size() > 0)
        {
            merchantFraudStringListHashMap.put(terminalId, merchantFraudMonitoringAlertDetailVOs);
        }
        if (merchantTechnicalMonitoringAlertDetailVOs.size() > 0)
        {
            merchantTechnicalStringListHashMap.put(terminalId, merchantTechnicalMonitoringAlertDetailVOs);
        }

        if (partnerSalesMonitoringAlertDetailVOs.size() > 0)
        {
            partnerSalesStringListHashMap.put(terminalId, partnerSalesMonitoringAlertDetailVOs);
        }
        if (partnerCBMonitoringAlertDetailVOs.size() > 0)
        {
            partnerCBStringListHashMap.put(terminalId, partnerCBMonitoringAlertDetailVOs);
        }
        if (partnerRFMonitoringAlertDetailVOs.size() > 0)
        {
            partnerRFStringListHashMap.put(terminalId, partnerRFMonitoringAlertDetailVOs);
        }
        if (partnerFraudMonitoringAlertDetailVOs.size() > 0)
        {
            partnerFraudStringListHashMap.put(terminalId, partnerFraudMonitoringAlertDetailVOs);
        }
        if (partnerTechnicalMonitoringAlertDetailVOs.size() > 0)
        {
            partnerTechnicalStringListHashMap.put(terminalId, partnerTechnicalMonitoringAlertDetailVOs);
        }
    }

    public void setMailIntoAppropriateMapGroup2(String terminalId, List<MonitoringAlertDetailVO> adminSalesMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> adminCBMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> adminRFMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> adminFraudMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> adminTechMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> merchantSalesMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> merchantCBMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> merchantRFMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> merchantFraudMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> merchantTechMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> agentSalesMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> agentCBMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> agentRFMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> agentFraudMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> agentTechMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> partnerSalesMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> partnerCBMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> partnerRFMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> partnerFraudMonitoringAttachmentAlertDetailVOs, List<MonitoringAlertDetailVO> partnerTechMonitoringAttachmentAlertDetailVOs, Map<String, List<MonitoringAlertDetailVO>> adminSalesStringAttachmentListHashMap, Map<String, List<MonitoringAlertDetailVO>> adminCBStringAttachmentListHashMap, Map<String, List<MonitoringAlertDetailVO>> adminRFStringAttachmentListHashMap, Map<String, List<MonitoringAlertDetailVO>> adminFraudStringAttachmentListHashMap, Map<String, List<MonitoringAlertDetailVO>> adminTechStringAttachmentListHashMap, Map<String, List<MonitoringAlertDetailVO>> merchantSalesStringAttachmentListHashMap, Map<String, List<MonitoringAlertDetailVO>> merchantCBStringAttachmentListHashMap, Map<String, List<MonitoringAlertDetailVO>> merchantRFStringAttachmentListHashMap, Map<String, List<MonitoringAlertDetailVO>> merchantFraudStringAttachmentListHashMap, Map<String, List<MonitoringAlertDetailVO>> merchantTechStringAttachmentListHashMap, Map<String, List<MonitoringAlertDetailVO>> agentSalesStringAttachmentListHashMap, Map<String, List<MonitoringAlertDetailVO>> agentCBStringAttachmentListHashMap, Map<String, List<MonitoringAlertDetailVO>> agentRFStringAttachmentListHashMap, Map<String, List<MonitoringAlertDetailVO>> agentFraudStringAttachmentListHashMap, Map<String, List<MonitoringAlertDetailVO>> agentTechStringAttachmentListHashMap, Map<String, List<MonitoringAlertDetailVO>> partnerSalesStringAttachmentListHashMap, Map<String, List<MonitoringAlertDetailVO>> partnerCBStringAttachmentListHashMap, Map<String, List<MonitoringAlertDetailVO>> partnerRFStringAttachmentListHashMap, Map<String, List<MonitoringAlertDetailVO>> partnerFraudStringAttachmentListHashMap, Map<String, List<MonitoringAlertDetailVO>> partnerTechStringAttachmentListHashMap)
    {
        if (adminSalesMonitoringAttachmentAlertDetailVOs.size() > 0)
        {
            adminSalesStringAttachmentListHashMap.put(terminalId, adminSalesMonitoringAttachmentAlertDetailVOs);
        }
        if (adminCBMonitoringAttachmentAlertDetailVOs.size() > 0)
        {
            adminCBStringAttachmentListHashMap.put(terminalId, adminCBMonitoringAttachmentAlertDetailVOs);
        }
        if (adminRFMonitoringAttachmentAlertDetailVOs.size() > 0)
        {
            adminRFStringAttachmentListHashMap.put(terminalId, adminRFMonitoringAttachmentAlertDetailVOs);
        }
        if (adminFraudMonitoringAttachmentAlertDetailVOs.size() > 0)
        {
            adminFraudStringAttachmentListHashMap.put(terminalId, adminFraudMonitoringAttachmentAlertDetailVOs);
        }
        if (adminTechMonitoringAttachmentAlertDetailVOs.size() > 0)
        {
            adminTechStringAttachmentListHashMap.put(terminalId, adminTechMonitoringAttachmentAlertDetailVOs);
        }

        if (merchantSalesMonitoringAttachmentAlertDetailVOs.size() > 0)
        {
            merchantSalesStringAttachmentListHashMap.put(terminalId, merchantSalesMonitoringAttachmentAlertDetailVOs);
        }
        if (merchantCBMonitoringAttachmentAlertDetailVOs.size() > 0)
        {
            merchantCBStringAttachmentListHashMap.put(terminalId, merchantCBMonitoringAttachmentAlertDetailVOs);
        }
        if (merchantRFMonitoringAttachmentAlertDetailVOs.size() > 0)
        {
            merchantRFStringAttachmentListHashMap.put(terminalId, merchantRFMonitoringAttachmentAlertDetailVOs);
        }
        if (merchantFraudMonitoringAttachmentAlertDetailVOs.size() > 0)
        {
            merchantFraudStringAttachmentListHashMap.put(terminalId, merchantFraudMonitoringAttachmentAlertDetailVOs);
        }
        if (merchantTechMonitoringAttachmentAlertDetailVOs.size() > 0)
        {
            merchantTechStringAttachmentListHashMap.put(terminalId, merchantTechMonitoringAttachmentAlertDetailVOs);
        }

        if (agentSalesMonitoringAttachmentAlertDetailVOs.size() > 0)
        {
            agentSalesStringAttachmentListHashMap.put(terminalId, agentSalesMonitoringAttachmentAlertDetailVOs);
        }
        if (agentCBMonitoringAttachmentAlertDetailVOs.size() > 0)
        {
            agentCBStringAttachmentListHashMap.put(terminalId, agentCBMonitoringAttachmentAlertDetailVOs);
        }
        if (agentRFMonitoringAttachmentAlertDetailVOs.size() > 0)
        {
            agentRFStringAttachmentListHashMap.put(terminalId, agentRFMonitoringAttachmentAlertDetailVOs);
        }
        if (agentFraudMonitoringAttachmentAlertDetailVOs.size() > 0)
        {
            agentFraudStringAttachmentListHashMap.put(terminalId, agentFraudMonitoringAttachmentAlertDetailVOs);
        }
        if (agentTechMonitoringAttachmentAlertDetailVOs.size() > 0)
        {
            agentTechStringAttachmentListHashMap.put(terminalId, agentTechMonitoringAttachmentAlertDetailVOs);
        }

        if (partnerSalesMonitoringAttachmentAlertDetailVOs.size() > 0)
        {
            partnerSalesStringAttachmentListHashMap.put(terminalId, partnerSalesMonitoringAttachmentAlertDetailVOs);
        }
        if (partnerCBMonitoringAttachmentAlertDetailVOs.size() > 0)
        {
            partnerCBStringAttachmentListHashMap.put(terminalId, partnerCBMonitoringAttachmentAlertDetailVOs);
        }
        if (partnerRFMonitoringAttachmentAlertDetailVOs.size() > 0)
        {
            partnerRFStringAttachmentListHashMap.put(terminalId, partnerRFMonitoringAttachmentAlertDetailVOs);
        }
        if (partnerFraudMonitoringAttachmentAlertDetailVOs.size() > 0)
        {
            partnerFraudStringAttachmentListHashMap.put(terminalId, partnerFraudMonitoringAttachmentAlertDetailVOs);
        }
        if (partnerTechMonitoringAttachmentAlertDetailVOs.size() > 0)
        {
            partnerTechStringAttachmentListHashMap.put(terminalId, partnerTechMonitoringAttachmentAlertDetailVOs);
        }
    }


    public void setMailIntoAppropriateMapGroup1(String terminalId, List<MonitoringAlertDetailVO> adminSalesMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> adminCBMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> adminRFMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> adminFraudMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> adminTechMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> agentSalesMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> agentCBMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> agentRFMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> agentFraudMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> agentTechMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> merchantCBMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> merchantRFMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> merchantSalesMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> merchantFraudMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> merchantTechnicalMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> partnerCBMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> partnerRFMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> partnerSalesMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> partnerFraudMonitoringAlertDetailVOs, List<MonitoringAlertDetailVO> partnerTechnicalMonitoringAlertDetailVOs, Map<String, List<MonitoringAlertDetailVO>> adminSalesStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> adminCBStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> adminRFStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> adminFraudStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> adminTechStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> agentSalesStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> agentCBStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> agentRFStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> agentFraudStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> agentTechStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> merchantSalesStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> merchantCBStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> merchantRFStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> merchantFraudStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> merchantTechnicalStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> partnerCBStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> partnerRFStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> partnerSalesStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> partnerFraudStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> partnerTechnicalStringListHashMap)
    {
        if (adminSalesMonitoringAlertDetailVOs.size() > 0)
        {
            adminSalesStringListHashMap.put(terminalId, adminSalesMonitoringAlertDetailVOs);
        }
        if (adminCBMonitoringAlertDetailVOs.size() > 0)
        {
            adminCBStringListHashMap.put(terminalId, adminCBMonitoringAlertDetailVOs);
        }
        if (adminRFMonitoringAlertDetailVOs.size() > 0)
        {
            adminRFStringListHashMap.put(terminalId, adminRFMonitoringAlertDetailVOs);
        }
        if (adminFraudMonitoringAlertDetailVOs.size() > 0)
        {
            adminFraudStringListHashMap.put(terminalId, adminFraudMonitoringAlertDetailVOs);
        }
        if (adminTechMonitoringAlertDetailVOs.size() > 0)
        {
            adminTechStringListHashMap.put(terminalId, adminTechMonitoringAlertDetailVOs);
        }

        if (agentSalesMonitoringAlertDetailVOs.size() > 0)
        {
            agentSalesStringListHashMap.put(terminalId, agentSalesMonitoringAlertDetailVOs);
        }
        if (agentCBMonitoringAlertDetailVOs.size() > 0)
        {
            agentCBStringListHashMap.put(terminalId, agentCBMonitoringAlertDetailVOs);
        }
        if (agentRFMonitoringAlertDetailVOs.size() > 0)
        {
            agentRFStringListHashMap.put(terminalId, agentRFMonitoringAlertDetailVOs);
        }
        if (agentFraudMonitoringAlertDetailVOs.size() > 0)
        {
            agentFraudStringListHashMap.put(terminalId, agentFraudMonitoringAlertDetailVOs);
        }
        if (agentTechMonitoringAlertDetailVOs.size() > 0)
        {
            agentTechStringListHashMap.put(terminalId, agentTechMonitoringAlertDetailVOs);
        }

        if (merchantSalesMonitoringAlertDetailVOs.size() > 0)
        {
            merchantSalesStringListHashMap.put(terminalId, merchantSalesMonitoringAlertDetailVOs);
        }
        if (merchantCBMonitoringAlertDetailVOs.size() > 0)
        {
            merchantCBStringListHashMap.put(terminalId, merchantCBMonitoringAlertDetailVOs);
        }
        if (merchantRFMonitoringAlertDetailVOs.size() > 0)
        {
            merchantRFStringListHashMap.put(terminalId, merchantRFMonitoringAlertDetailVOs);
        }
        if (merchantFraudMonitoringAlertDetailVOs.size() > 0)
        {
            merchantFraudStringListHashMap.put(terminalId, merchantFraudMonitoringAlertDetailVOs);
        }
        if (merchantTechnicalMonitoringAlertDetailVOs.size() > 0)
        {
            merchantTechnicalStringListHashMap.put(terminalId, merchantTechnicalMonitoringAlertDetailVOs);
        }

        if (partnerSalesMonitoringAlertDetailVOs.size() > 0)
        {
            partnerSalesStringListHashMap.put(terminalId, partnerSalesMonitoringAlertDetailVOs);
        }
        if (partnerCBMonitoringAlertDetailVOs.size() > 0)
        {
            partnerCBStringListHashMap.put(terminalId, partnerCBMonitoringAlertDetailVOs);
        }
        if (partnerRFMonitoringAlertDetailVOs.size() > 0)
        {
            partnerRFStringListHashMap.put(terminalId, partnerRFMonitoringAlertDetailVOs);
        }
        if (partnerFraudMonitoringAlertDetailVOs.size() > 0)
        {
            partnerFraudStringListHashMap.put(terminalId, partnerFraudMonitoringAlertDetailVOs);
        }
        if (partnerTechnicalMonitoringAlertDetailVOs.size() > 0)
        {
            partnerTechnicalStringListHashMap.put(terminalId, partnerTechnicalMonitoringAlertDetailVOs);
        }
    }

    public FileAttachmentVO prepareExcelFile(TerminalVO terminalVO, int threshold, PZTransactionStatus pzTransactionStatus)
    {
        logger.debug("terminalVO==" + terminalVO.toString());
        logger.debug("threshold==" + threshold);
        logger.debug("pzTransactionStatus==" + pzTransactionStatus.toString());
        List<TransactionVO> transactionVOs = null;

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(terminalVO.getAccountId());
        GatewayType gatewayType = GatewayTypeService.getGatewayType(gatewayAccount.getPgTypeId());
        String tableName = Database.getTableName(gatewayType.getGateway());

        MerchantMonitoringDAO merchantMonitoringDAO = new MerchantMonitoringDAO();
        transactionVOs = merchantMonitoringDAO.getTransactionListByDtstamp(tableName, terminalVO, pzTransactionStatus.toString());
        if (transactionVOs.size() > 0)
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

            HSSFCell cell0 = header.createCell((short) 0);
            HSSFCell cell1 = header.createCell((short) 1);
            HSSFCell cell2 = header.createCell((short) 2);
            HSSFCell cell3 = header.createCell((short) 3);
            HSSFCell cell4 = header.createCell((short) 4);
            HSSFCell cell5 = header.createCell((short) 5);
            HSSFCell cell6 = header.createCell((short) 6);
            HSSFCell cell7 = header.createCell((short) 7);

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

            cell0.setCellValue("TrackingID");
            cell1.setCellValue("Description");
            cell2.setCellValue("Trade date");
            cell3.setCellValue("Amount");
            cell4.setCellValue("Currency");
            cell5.setCellValue("Status");
            cell6.setCellValue("Billing FirstName");
            cell7.setCellValue("Billing LastName");

            int index = 1;

            //Get All AuthFailed The Transaction Between Date Renage Based On Dtstamp
            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            int thresholdDays = threshold;
            for (TransactionVO detailsVO : transactionVOs)
            {
                String transactionDate = detailsVO.getTransactionDate();
                long day = Functions.DATEDIFF(transactionDate, targetFormat.format(new Date()));

                if (day > thresholdDays)
                {
                    HSSFRow row = sheet.createRow((short) index);
                    row.createCell((short) 0).setCellValue(detailsVO.getTrackingId());
                    row.createCell((short) 1).setCellValue(detailsVO.getOrderId());
                    row.createCell((short) 2).setCellValue(detailsVO.getTransactionDate());
                    row.createCell((short) 3).setCellValue(detailsVO.getAmount());
                    row.createCell((short) 4).setCellValue(detailsVO.getCurrency());
                    row.createCell((short) 5).setCellValue(detailsVO.getStatus());
                    row.createCell((short) 6).setCellValue(detailsVO.getCustFirstName());
                    row.createCell((short) 7).setCellValue(detailsVO.getCustLastName());
                    index = index + 1;
                }
            }

            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMddHHmmss");
            String currentSystemDate = dateFormater.format(new Date());

            String fileName = null;
            String filePath = null;
            fileName = "Merchant_Pending_Manual_" + pzTransactionStatus.toString() + "_" + terminalVO.getMemberId() + "_" + terminalVO.getAccountId() + "_" + currentSystemDate;
            fileName = fileName + ".xls";

            try
            {
                File filepath = new File(SETTLEMENT_FILE_PATH + fileName);
                //System.out.println("Looking for the file in...." + filepath.getCanonicalPath());
                filePath = filepath.getPath();

                FileOutputStream out = new FileOutputStream(filepath);
                workbook.write(out);
                out.close();
            }
            catch (Exception e)
            {
                logger.error("Exception--->", e);
            }

            FileAttachmentVO fileAttachmentVO = new FileAttachmentVO();
            fileAttachmentVO.setFileName(fileName);
            fileAttachmentVO.setFilePath(filePath);
            return fileAttachmentVO;
        }
        else
        {
            logger.debug("All okay with terminal==" + terminalVO.toString());
            return null;
        }
    }

    public FileAttachmentVO prepareCardVelocityExcelFile(TerminalVO terminalVO, List<TransactionDetailsVO> transactionDetailsVOs, String name)
    {
        if (transactionDetailsVOs.size() > 0)
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

            cell0.setCellValue("TrackingID");
            cell1.setCellValue("Description");
            cell2.setCellValue("Trade date");
            cell3.setCellValue("Amount");
            cell4.setCellValue("Currency");
            cell5.setCellValue("Status");
            cell6.setCellValue("Billing FirstName");
            cell7.setCellValue("Billing LastName");
            cell8.setCellValue("First Six");
            cell9.setCellValue("Last Four");

            int index = 1;

            //Get All AuthFailed The Transaction Between Date Renage Based On Dtstamp
            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (TransactionDetailsVO detailsVO : transactionDetailsVOs)
            {
                HSSFRow row = sheet.createRow((short) index);
                row.createCell((short) 0).setCellValue(detailsVO.getTrackingid());
                row.createCell((short) 1).setCellValue(detailsVO.getDescription());
                row.createCell((short) 2).setCellValue(detailsVO.getTransactionTime());
                row.createCell((short) 3).setCellValue(detailsVO.getAmount());
                row.createCell((short) 4).setCellValue(detailsVO.getCurrency());
                row.createCell((short) 5).setCellValue(detailsVO.getStatus());
                row.createCell((short) 6).setCellValue(detailsVO.getFirstName());
                row.createCell((short) 7).setCellValue(detailsVO.getLastName());
                row.createCell((short) 8).setCellValue(detailsVO.getFirstSix());
                row.createCell((short) 9).setCellValue(detailsVO.getLastFour());
                index = index + 1;

            }

            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMddHHmmss");
            String currentSystemDate = dateFormater.format(new Date());

            String fileName = null;
            String filePath = null;
            fileName = "Merchant_CardVelocity_" + terminalVO.getMemberId() + "_" + terminalVO.getAccountId() + "_" + terminalVO.getTerminalId() + "_" + currentSystemDate;
            fileName = fileName + ".xls";
            try
            {
                File filepath = new File(SETTLEMENT_FILE_PATH + fileName);
                //System.out.println("Looking for the file in...." + filepath.getCanonicalPath());
                filePath = filepath.getPath();

                FileOutputStream out = new FileOutputStream(filepath);
                workbook.write(out);
                out.close();
            }
            catch (Exception e)
            {
                logger.error("Exception--->", e);
            }

            FileAttachmentVO fileAttachmentVO = new FileAttachmentVO();
            fileAttachmentVO.setFileName(fileName);
            fileAttachmentVO.setFilePath(filePath);
            return fileAttachmentVO;
        }
        else
        {
            logger.debug("Transaction Not Founds");
            return null;
        }
    }

    public FileAttachmentVO prepareMatureTransactionExcelFile(TerminalVO terminalVO, List<TransactionDetailsVO> transactionDetailsVOs, String status)
    {
        if (transactionDetailsVOs.size() > 0)
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

            HSSFCell cell0 = header.createCell((short) 0);
            HSSFCell cell1 = header.createCell((short) 1);
            HSSFCell cell2 = header.createCell((short) 2);
            HSSFCell cell3 = header.createCell((short) 3);
            HSSFCell cell4 = header.createCell((short) 4);
            HSSFCell cell5 = header.createCell((short) 5);
            HSSFCell cell6 = header.createCell((short) 6);
            HSSFCell cell7 = header.createCell((short) 7);

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

            cell0.setCellValue("TrackingID");
            cell1.setCellValue("Description");
            cell2.setCellValue("Trade date");
            cell3.setCellValue("Amount");
            cell4.setCellValue("Currency");
            cell5.setCellValue("Status");
            cell6.setCellValue("Billing FirstName");
            cell7.setCellValue("Billing LastName");

            int index = 1;

            //Get All AuthFailed The Transaction Between Date Renage Based On Dtstamp
            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (TransactionDetailsVO detailsVO : transactionDetailsVOs)
            {
                HSSFRow row = sheet.createRow((short) index);
                row.createCell((short) 0).setCellValue(detailsVO.getTrackingid());
                row.createCell((short) 1).setCellValue(detailsVO.getDescription());
                row.createCell((short) 2).setCellValue(detailsVO.getTransactionTime());
                row.createCell((short) 3).setCellValue(detailsVO.getAmount());
                row.createCell((short) 4).setCellValue(detailsVO.getCurrency());
                row.createCell((short) 5).setCellValue(detailsVO.getStatus());
                row.createCell((short) 6).setCellValue(detailsVO.getFirstName());
                row.createCell((short) 7).setCellValue(detailsVO.getLastName());
                index = index + 1;

            }

            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMddHHmmss");
            String currentSystemDate = dateFormater.format(new Date());

            String fileName = null;
            String filePath = null;
            fileName = "Merchant_" + status + "_" + terminalVO.getMemberId() + "_" + terminalVO.getAccountId() + "_" + terminalVO.getTerminalId() + "_" + currentSystemDate;
            fileName = fileName + ".xls";
            try
            {
                File filepath = new File(SETTLEMENT_FILE_PATH + fileName);
                logger.debug("Looking for the file in...." + filepath.getCanonicalPath());
                filePath = filepath.getPath();

                FileOutputStream out = new FileOutputStream(filepath);
                workbook.write(out);
                out.close();
            }
            catch (Exception e)
            {
                logger.error("Exception--->", e);
            }

            FileAttachmentVO fileAttachmentVO = new FileAttachmentVO();
            fileAttachmentVO.setFileName(fileName);
            fileAttachmentVO.setFilePath(filePath);
            return fileAttachmentVO;
        }
        else
        {
            logger.debug("No Transaction Founds");
            return null;
        }
    }
    public FileAttachmentVO prepareTransactionExcelFile(TerminalVO terminalVO, List<TransactionVO> transactionDetailsVOs, String status)
    {
        if (transactionDetailsVOs.size() > 0)
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
           /* sheet.setColumnWidth((short) 8, (short) 200000);
            sheet.setColumnWidth((short) 9, (short) 200000);*/

            HSSFCell cell0 = header.createCell((short) 0);
            HSSFCell cell1 = header.createCell((short) 1);
            HSSFCell cell2 = header.createCell((short) 2);
            HSSFCell cell3 = header.createCell((short) 3);
            HSSFCell cell4 = header.createCell((short) 4);
            HSSFCell cell5 = header.createCell((short) 5);
            HSSFCell cell6 = header.createCell((short) 6);
            HSSFCell cell7 = header.createCell((short) 7);
            /*HSSFCell cell8 = header.createCell((short) 8);
            HSSFCell cell9 = header.createCell((short) 9);*/

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
            /*cell8.setCellStyle(style);
            cell9.setCellStyle(style);*/

            cell0.setCellValue("TrackingId");
            cell1.setCellValue("Description");
            cell2.setCellValue("Trade date");
            cell3.setCellValue("Amount");
            cell4.setCellValue("Currency");
            cell5.setCellValue("Status");
            cell6.setCellValue("Billing FirstName");
            cell7.setCellValue("Billing LastName");
            /*cell8.setCellValue("First Six");
            cell7.setCellValue("Last Four");*/

            int index = 1;

            //Get All AuthFailed The Transaction Between Date Renage Based On Dtstamp
            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (TransactionVO detailsVO : transactionDetailsVOs)
            {
                HSSFRow row = sheet.createRow((short) index);
                row.createCell((short) 0).setCellValue(detailsVO.getTrackingId());
                row.createCell((short) 1).setCellValue(detailsVO.getOrderId());
                row.createCell((short) 2).setCellValue(detailsVO.getTransactionDate());
                row.createCell((short) 3).setCellValue(detailsVO.getAmount());
                row.createCell((short) 4).setCellValue(detailsVO.getCurrency());
                row.createCell((short) 5).setCellValue(detailsVO.getStatus());
                row.createCell((short) 6).setCellValue(detailsVO.getCustFirstName());
                row.createCell((short) 7).setCellValue(detailsVO.getCustLastName());
                /*row.createCell((short) 8).setCellValue(detailsVO.getFirstSix());
                row.createCell((short) 9).setCellValue(detailsVO.getLastFour());*/
                index = index + 1;

            }

            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMddHHmmss");
            String currentSystemDate = dateFormater.format(new Date());

            String fileName = null;
            String filePath = null;
            fileName = "Merchant_" + status + "_" + terminalVO.getMemberId() + "_" + terminalVO.getAccountId() + "_" + terminalVO.getTerminalId() + "_" + currentSystemDate;
            fileName = fileName + ".xls";
            try
            {
                File filepath = new File(SETTLEMENT_FILE_PATH + fileName);
                logger.debug("Looking for the file in...." + filepath.getCanonicalPath());
                filePath = filepath.getPath();

                FileOutputStream out = new FileOutputStream(filepath);
                workbook.write(out);
                out.close();
            }
            catch (Exception e)
            {
                logger.error("Exception--->",e);
            }

            FileAttachmentVO fileAttachmentVO = new FileAttachmentVO();
            fileAttachmentVO.setFileName(fileName);
            fileAttachmentVO.setFilePath(filePath);
            return fileAttachmentVO;
        }
        else
        {
            logger.debug("No Transaction Founds");
            return null;
        }
    }

    public String getAlertMessage(String inputString, String msg)
    {
        int startTagPos = inputString.indexOf("|");
        int endTagPos = inputString.indexOf("|", startTagPos + 1);
        String subString = inputString.substring(startTagPos, endTagPos + 1);
        return inputString.replace(subString, msg);

    }

    public void fillConsolidateAdminAllTypeMailMap(String memberId, Map<String, List<MonitoringAlertDetailVO>> adminSalesStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> adminCBStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> adminRFStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> adminFraudStringListHashMap, Map<String, List<MonitoringAlertDetailVO>> adminTechStringListHashMap, Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminSalesStringListHashMapConsolidate, Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminCBStringListHashMapConsolidate, Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminRFStringListHashMapConsolidate, Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminFraudStringListHashMapConsolidate, Map<String, Map<String, List<MonitoringAlertDetailVO>>> adminTechStringListHashMapConsolidate)
    {
        if (adminSalesStringListHashMap.size() > 0)
        {
            adminSalesStringListHashMapConsolidate.put(memberId, adminSalesStringListHashMap);
        }
        if (adminCBStringListHashMap.size() > 0)
        {
            adminCBStringListHashMapConsolidate.put(memberId, adminCBStringListHashMap);
        }
        if (adminRFStringListHashMap.size() > 0)
        {
            adminRFStringListHashMapConsolidate.put(memberId, adminRFStringListHashMap);
        }
        if (adminFraudStringListHashMap.size() > 0)
        {
            adminFraudStringListHashMapConsolidate.put(memberId, adminFraudStringListHashMap);
        }
        if (adminTechStringListHashMap.size() > 0)
        {
            adminTechStringListHashMapConsolidate.put(memberId, adminTechStringListHashMap);
        }
    }

    public void fillConsolidatePerPartnerMailMap(String partnerId,Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerSalesStringListHashMapConsolidatePerPartner,Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerCBStringListHashMapConsolidatePerPartner,Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerRFStringListHashMapConsolidatePerPartner,Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerFraudStringListHashMapConsolidatePerPartner,Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> partnerTechStringListHashMapConsolidatePerPartner,Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerSalesStringListHashMapConsolidate,Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerCBStringListHashMapConsolidate,Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerRFStringListHashMapConsolidate,Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerFraudStringListHashMapConsolidate,Map<String, Map<String, List<MonitoringAlertDetailVO>>> partnerTechStringListHashMapConsolidate)
    {
        if(partnerSalesStringListHashMapConsolidate.size()>0)
        {
            partnerSalesStringListHashMapConsolidatePerPartner.put(partnerId,partnerSalesStringListHashMapConsolidate);
        }
        if(partnerCBStringListHashMapConsolidate.size()>0)
        {
            partnerCBStringListHashMapConsolidatePerPartner.put(partnerId,partnerCBStringListHashMapConsolidate);
        }
        if(partnerRFStringListHashMapConsolidate.size()>0)
        {
            partnerRFStringListHashMapConsolidatePerPartner.put(partnerId,partnerRFStringListHashMapConsolidate);
        }
        if(partnerFraudStringListHashMapConsolidate.size()>0)
        {
            partnerFraudStringListHashMapConsolidatePerPartner.put(partnerId,partnerFraudStringListHashMapConsolidate);
        }
        if(partnerTechStringListHashMapConsolidate.size()>0)
        {
            partnerTechStringListHashMapConsolidatePerPartner.put(partnerId,partnerTechStringListHashMapConsolidate);
        }
    }
    public void fillConsolidatePerAgentMailMap(String agentId,Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentSalesStringListHashMapConsolidatePerPartner,Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentCBStringListHashMapConsolidatePerPartner,Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentRFStringListHashMapConsolidatePerPartner,Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentFraudStringListHashMapConsolidatePerPartner,Map<String,Map<String, Map<String, List<MonitoringAlertDetailVO>>>> agentTechStringListHashMapConsolidatePerPartner,Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentSalesStringListHashMapConsolidate,Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentCBStringListHashMapConsolidate,Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentRFStringListHashMapConsolidate,Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentFraudStringListHashMapConsolidate,Map<String, Map<String, List<MonitoringAlertDetailVO>>> agentTechStringListHashMapConsolidate)
    {
        if(agentSalesStringListHashMapConsolidate.size()>0)
        {
            agentSalesStringListHashMapConsolidatePerPartner.put(agentId, agentSalesStringListHashMapConsolidate);
        }
        if(agentCBStringListHashMapConsolidate.size()>0)
        {
            agentCBStringListHashMapConsolidatePerPartner.put(agentId, agentCBStringListHashMapConsolidate);
        }
        if(agentRFStringListHashMapConsolidate.size()>0)
        {
            agentRFStringListHashMapConsolidatePerPartner.put(agentId, agentRFStringListHashMapConsolidate);
        }
        if(agentFraudStringListHashMapConsolidate.size()>0)
        {
            agentFraudStringListHashMapConsolidatePerPartner.put(agentId, agentFraudStringListHashMapConsolidate);
        }
        if(agentTechStringListHashMapConsolidate.size()>0)
        {
            agentTechStringListHashMapConsolidatePerPartner.put(agentId, agentTechStringListHashMapConsolidate);
        }
    }

    public FileAttachmentVO compareThresholdAndPrepareExcel(TerminalVO terminalVO, int threshold, List<TransactionVO> transactionVOList, String actionType)
    {
        List<TransactionDetailsVO> transactionDetails = new ArrayList();
        FileAttachmentVO fileAttachmentVO = null;
        for (TransactionVO transactionVO : transactionVOList)
        {
            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String todaysDate = targetFormat.format(new Date());
            TransactionDetailsVO transactionDetailsVO = null;
            long day = Functions.DATEDIFF(transactionVO.getTransactionDate(), todaysDate);
            if (day > threshold)
            {
                transactionDetailsVO = new TransactionDetailsVO();
                transactionDetailsVO.setTrackingid(transactionVO.getTrackingId());
                transactionDetailsVO.setDescription(transactionVO.getOrderId());
                transactionDetailsVO.setAmount(transactionVO.getAmount());
                transactionDetailsVO.setFirstName(transactionVO.getCustFirstName());
                transactionDetailsVO.setLastName(transactionVO.getCustLastName());
                transactionDetailsVO.setStatus(transactionVO.getStatus());
                transactionDetailsVO.setCurrency(transactionVO.getCurrency());
                transactionDetailsVO.setTransactionTime(transactionVO.getTransactionDate());
                transactionDetails.add(transactionDetailsVO);
            }
        }
        if (transactionDetails.size() > 0)
        {
            fileAttachmentVO = prepareMatureTransactionExcelFile(terminalVO, transactionDetails, actionType);
        }
        return fileAttachmentVO;
    }
    public FileAttachmentVO prepareInCompleteTransactionExcel(TerminalVO terminalVO, List<TransactionVO> transactionVOList, String actionType)
    {
        List<TransactionDetailsVO> transactionDetails = new ArrayList();
        FileAttachmentVO fileAttachmentVO = null;
        for (TransactionVO transactionVO : transactionVOList)
        {
            TransactionDetailsVO transactionDetailsVO = null;
            transactionDetailsVO = new TransactionDetailsVO();
            transactionDetailsVO.setTrackingid(transactionVO.getTrackingId());
            transactionDetailsVO.setDescription(transactionVO.getOrderId());
            transactionDetailsVO.setAmount(transactionVO.getAmount());
            transactionDetailsVO.setFirstName(transactionVO.getCustFirstName());
            transactionDetailsVO.setLastName(transactionVO.getCustLastName());
            transactionDetailsVO.setStatus(transactionVO.getStatus());
            transactionDetailsVO.setCurrency(transactionVO.getCurrency());
            transactionDetailsVO.setTransactionTime(transactionVO.getTransactionDate());
            transactionDetails.add(transactionDetailsVO);
        }
        if (transactionDetails.size() > 0)
        {
            fileAttachmentVO = prepareMatureTransactionExcelFile(terminalVO, transactionDetails, actionType);
        }
        return fileAttachmentVO;
    }

    public FileAttachmentVO compareThresholdAndPrepareHighDepositExcel(TerminalVO terminalVO, double threshold, List<TransactionVO> transactionVOList, String actionType)
    {
        List<TransactionDetailsVO> transactionDetails = new ArrayList();
        FileAttachmentVO fileAttachmentVO = null;
        for (TransactionVO transactionVO : transactionVOList)
        {
            TransactionDetailsVO transactionDetailsVO = null;
            if (Double.valueOf(transactionVO.getAmount()) > threshold)
            {
                transactionDetailsVO = new TransactionDetailsVO();
                transactionDetailsVO.setTrackingid(transactionVO.getTrackingId());
                transactionDetailsVO.setDescription(transactionVO.getOrderId());
                transactionDetailsVO.setAmount(transactionVO.getAmount());
                transactionDetailsVO.setFirstName(transactionVO.getCustFirstName());
                transactionDetailsVO.setLastName(transactionVO.getCustLastName());
                transactionDetailsVO.setStatus(transactionVO.getStatus());
                transactionDetailsVO.setCurrency(transactionVO.getCurrency());
                transactionDetailsVO.setTransactionTime(transactionVO.getTransactionDate());
                transactionDetails.add(transactionDetailsVO);
            }
        }
        if (transactionDetails.size() > 0)
        {
            fileAttachmentVO = prepareMatureTransactionExcelFile(terminalVO, transactionDetails, actionType);
        }
        return fileAttachmentVO;
    }
    public FileAttachmentVO compareThresholdAndPrepareAuthorizationExcel(TerminalVO terminalVO, double threshold, List<TransactionVO> transactionVOList, String actionType)
    {
        List<TransactionDetailsVO> transactionDetails = new ArrayList();
        FileAttachmentVO fileAttachmentVO = null;
        for (TransactionVO transactionVO : transactionVOList)
        {
            //System.out.println(transactionVO.getTrackingId()+":"+transactionVO.getStatus());
            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String todaysDate = targetFormat.format(new Date());
            TransactionDetailsVO transactionDetailsVO = null;
            if("authsuccessful".equals(transactionVO.getStatus()))
            {
                long day = Functions.DATEDIFF(transactionVO.getTransactionDate(), todaysDate);
                if (day>threshold)
                {
                    transactionDetailsVO = new TransactionDetailsVO();
                    transactionDetailsVO.setTrackingid(transactionVO.getTrackingId());
                    transactionDetailsVO.setDescription(transactionVO.getOrderId());
                    transactionDetailsVO.setAmount(transactionVO.getAmount());
                    transactionDetailsVO.setFirstName(transactionVO.getCustFirstName());
                    transactionDetailsVO.setLastName(transactionVO.getCustLastName());
                    transactionDetailsVO.setStatus(transactionVO.getStatus());
                    transactionDetailsVO.setCurrency(transactionVO.getCurrency());
                    transactionDetailsVO.setTransactionTime(transactionVO.getTransactionDate());
                    transactionDetails.add(transactionDetailsVO);
                }
            }
        }
        if (transactionDetails.size() > 0)
        {
            fileAttachmentVO = prepareMatureTransactionExcelFile(terminalVO, transactionDetails, actionType);
        }
        return fileAttachmentVO;
    }
    public MonitoringAttachmentAlertDetailsVO calculateAttachment(MonitoringParameterMappingVO monitoringParameterMappingVO, TerminalVO terminalVO, List<TransactionVO> inCompleteTransactionVOsList, List<TransactionVO> cbTransactionVOsList, List<TransactionVO> rfTransactionVOsList,List<BinAmountVO> binAmountVOList, DateVO dateVO,String frequency)
    {
        MonitoringParameterVO monitoringParameterVO = monitoringParameterMappingVO.getMonitoringParameterVO();
        MonitoringAttachmentAlertDetailsVO monitoringAttachmentAlertDetailsVO = null;
        TransactionManager transactionManager=new TransactionManager();
        double thresholdValue1=0.00;
        if("daily".equals(frequency))
        {
            thresholdValue1 =monitoringParameterMappingVO.getAlertThreshold();
        }
        else if("weekly".equals(frequency))
        {
            thresholdValue1 =monitoringParameterMappingVO.getWeeklyAlertThreshold();
        }
        else if("monthly".equals(frequency))
        {
            thresholdValue1 =monitoringParameterMappingVO.getMonthlyAlertThreshold();
        }

        if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Chargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
        {
            /*Total Number Of Chargebaks*/
            int thresholdValue = Double.valueOf(thresholdValue1).intValue();
            if(cbTransactionVOsList.size()>thresholdValue)
            {
                FileAttachmentVO fileAttachmentVO = prepareTransactionExcelFile(terminalVO, cbTransactionVOsList, "Chargeback_List");
                if (fileAttachmentVO != null)
                {
                    monitoringAttachmentAlertDetailsVO = new MonitoringAttachmentAlertDetailsVO();
                    monitoringAttachmentAlertDetailsVO.setAlertWithAttachment(true);
                    monitoringAttachmentAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                    String validaMsg = String.valueOf(cbTransactionVOsList.size());
                    monitoringAttachmentAlertDetailsVO.setAlertMsg(getAlertMessage(monitoringParameterMappingVO.getAlertMessage(), validaMsg));
                    monitoringAttachmentAlertDetailsVO.setFileAttachmentVO(fileAttachmentVO);
                    setMonitoringToEntityFlags(monitoringAttachmentAlertDetailsVO, monitoringParameterMappingVO);
                    monitoringAttachmentAlertDetailsVO.setActualratio(cbTransactionVOsList.size());
                }
            }
        }
        if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.StuckTransaction.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
        {
            /*Stuck Transaction List*/
            FileAttachmentVO fileAttachmentVO = prepareInCompleteTransactionExcel(terminalVO, inCompleteTransactionVOsList, "Incomplete_Transactions_List");
            if (fileAttachmentVO != null)
            {
                monitoringAttachmentAlertDetailsVO = new MonitoringAttachmentAlertDetailsVO();
                monitoringAttachmentAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                monitoringAttachmentAlertDetailsVO.setAlertMsg(monitoringParameterMappingVO.getAlertMessage());
                monitoringAttachmentAlertDetailsVO.setFileAttachmentVO(fileAttachmentVO);
                monitoringAttachmentAlertDetailsVO.setActualratio(inCompleteTransactionVOsList.size());
                setMonitoringToEntityFlags(monitoringAttachmentAlertDetailsVO, monitoringParameterMappingVO);
            }
        }
        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.MatureChargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
        {
            //Chargeback of mature transaction
            int thresholdValue = Double.valueOf(thresholdValue1).intValue();
            FileAttachmentVO fileAttachmentVO = compareThresholdAndPrepareExcel(terminalVO, thresholdValue, cbTransactionVOsList, "Mature_Chargeback");
            if (fileAttachmentVO != null)
            {
                monitoringAttachmentAlertDetailsVO = new MonitoringAttachmentAlertDetailsVO();
                monitoringAttachmentAlertDetailsVO.setAlertWithAttachment(true);
                monitoringAttachmentAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                String validaMsg=String.valueOf(thresholdValue);
                monitoringAttachmentAlertDetailsVO.setAlertMsg(getAlertMessage(monitoringParameterMappingVO.getAlertMessage(), validaMsg));
                monitoringAttachmentAlertDetailsVO.setFileAttachmentVO(fileAttachmentVO);
                monitoringAttachmentAlertDetailsVO.setActualratio(cbTransactionVOsList.size());
                setMonitoringToEntityFlags(monitoringAttachmentAlertDetailsVO, monitoringParameterMappingVO);
            }
        }
        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.MatureRefund.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
        {
            //refund of mature transaction
            int thresholdValue = Double.valueOf(thresholdValue1).intValue();
            FileAttachmentVO fileAttachmentVO = compareThresholdAndPrepareExcel(terminalVO, thresholdValue, rfTransactionVOsList, "Mature_Refund");
            if (fileAttachmentVO != null)
            {
                monitoringAttachmentAlertDetailsVO = new MonitoringAttachmentAlertDetailsVO();
                monitoringAttachmentAlertDetailsVO.setAlertWithAttachment(true);
                monitoringAttachmentAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                String validaMsg=String.valueOf(thresholdValue);
                monitoringAttachmentAlertDetailsVO.setAlertMsg(getAlertMessage(monitoringParameterMappingVO.getAlertMessage(), validaMsg));
                monitoringAttachmentAlertDetailsVO.setFileAttachmentVO(fileAttachmentVO);
                monitoringAttachmentAlertDetailsVO.setActualratio(rfTransactionVOsList.size());
                setMonitoringToEntityFlags(monitoringAttachmentAlertDetailsVO, monitoringParameterMappingVO);
            }
        }
        else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.HighAmountOrder.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
        {
            //High Deposite Amount
            MerchantMonitoringManager  merchantMonitoringManager=new MerchantMonitoringManager();
            double thresholdValue=thresholdValue1;
            List<TransactionDetailsVO> transactionDevtailsVOs = transactionManager.getListHighRiskAmountTransaction(terminalVO, dateVO, Functions.round(thresholdValue, 2));
            List<TransactionDetailsVO> transactionDetailsVOs1 = merchantMonitoringManager.getListHighRiskAmountRejectedTransaction(terminalVO, dateVO, Functions.roundDBL(thresholdValue, 2), TransReqRejectCheck.MAX_TICKET_AMOUNT_LIMIT.toString());
            transactionDevtailsVOs.addAll(transactionDetailsVOs1);
            FileAttachmentVO fileAttachmentVO =null;//compareThresholdAndPrepareHighDepositExcel(terminalVO, monitoringParameterMappingVO.getAlertThreshold(), transactionDetailsVOs, "High_Deposite_Transaction_List");
            if (transactionDevtailsVOs.size() > 0)
            {
                fileAttachmentVO = prepareMatureTransactionExcelFile(terminalVO, transactionDevtailsVOs, "High_Deposite_Transaction_List");
            }
            if (fileAttachmentVO != null)
            {
                monitoringAttachmentAlertDetailsVO = new MonitoringAttachmentAlertDetailsVO();
                monitoringAttachmentAlertDetailsVO.setAlertWithAttachment(true);
                monitoringAttachmentAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                String validaMsg=Functions.round(thresholdValue,2);
                monitoringAttachmentAlertDetailsVO.setAlertMsg(getAlertMessage(monitoringParameterMappingVO.getAlertMessage(), validaMsg));
                monitoringAttachmentAlertDetailsVO.setFileAttachmentVO(fileAttachmentVO);
                monitoringAttachmentAlertDetailsVO.setActualratio(transactionDevtailsVOs.size());
                setMonitoringToEntityFlags(monitoringAttachmentAlertDetailsVO, monitoringParameterMappingVO);
            }
        }
        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.AuthSuccessful.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
        {
            //Pending Pre-Authorization
            DateManager dateManager=new DateManager();
            int thresholdValue = Double.valueOf(thresholdValue1).intValue();
            DateVO preAuthEndDate=dateManager.getPreviousDayDateRange(thresholdValue);
            List<TransactionDetailsVO> transactionDetailsVOs=transactionManager.getListPreAuthTransactionList(terminalVO,preAuthEndDate);
            if (transactionDetailsVOs.size() > 0)
            {
                FileAttachmentVO fileAttachmentVO= prepareMatureTransactionExcelFile(terminalVO, transactionDetailsVOs, "Pre-Auth_Transactions");
                if (fileAttachmentVO != null)
                {
                    monitoringAttachmentAlertDetailsVO = new MonitoringAttachmentAlertDetailsVO();
                    monitoringAttachmentAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                    String validaMsg=String.valueOf(thresholdValue);
                    monitoringAttachmentAlertDetailsVO.setAlertMsg(getAlertMessage(monitoringParameterMappingVO.getAlertMessage(), validaMsg));
                    monitoringAttachmentAlertDetailsVO.setFileAttachmentVO(fileAttachmentVO);
                    monitoringAttachmentAlertDetailsVO.setActualratio(transactionDetailsVOs.size());
                    setMonitoringToEntityFlags(monitoringAttachmentAlertDetailsVO, monitoringParameterMappingVO);
                }
            }
        }
        if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardSameAmountConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
        {
            //Authorization log - Consecutive use same account same amount
            MerchantMonitoringManager merchantMonitoringManager=new MerchantMonitoringManager();
            int thresholdValue = Double.valueOf(thresholdValue1).intValue();
            List<TransactionDetailsVO> transactionDetailsVOs=merchantMonitoringManager.getConsequtiveSameCardSameAmountDetail(terminalVO, dateVO,thresholdValue);
            if (transactionDetailsVOs.size() > 0)
            {
                FileAttachmentVO fileAttachmentVO = prepareCardVelocityExcelFile(terminalVO, transactionDetailsVOs, MonitoringKeyword.SameCardSameAmountConsecutive.toString());
                if (fileAttachmentVO != null)
                {
                    monitoringAttachmentAlertDetailsVO = new MonitoringAttachmentAlertDetailsVO();
                    monitoringAttachmentAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                    String validaMsg=String.valueOf(thresholdValue);
                    monitoringAttachmentAlertDetailsVO.setAlertMsg(getAlertMessage(monitoringParameterMappingVO.getAlertMessage(), validaMsg));
                    monitoringAttachmentAlertDetailsVO.setFileAttachmentVO(fileAttachmentVO);
                    monitoringAttachmentAlertDetailsVO.setActualratio(transactionDetailsVOs.size());
                    setMonitoringToEntityFlags(monitoringAttachmentAlertDetailsVO, monitoringParameterMappingVO);
                }
            }
        }
        if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
        {
            //Card velocity:
            int thresholdValue = Double.valueOf(thresholdValue1).intValue();
            int sameCardSameAmountCountThreshold =thresholdValue;
            int sameCardSameAmountCountActual =0;
            boolean actionNeedToBeTaken = false;
            StringBuffer stringBuffer = new StringBuffer();
            StringBuffer lastFour = new StringBuffer();

            for (BinAmountVO binAmountVO : binAmountVOList)
            {
                sameCardSameAmountCountActual = binAmountVO.getCount();
                String binString = binAmountVO.getBinString();
                String lastFourString = binAmountVO.getLastFourString();
                if (sameCardSameAmountCountActual > sameCardSameAmountCountThreshold)
                {
                    actionNeedToBeTaken = true;
                    if (stringBuffer.length() > 0)
                    {
                        stringBuffer.append(",");
                        lastFour.append(",");
                    }
                    stringBuffer.append(binString);
                    lastFour.append(lastFourString);
                }
            }
            if (stringBuffer.length() > 0)
            {
                List<TransactionDetailsVO> transactionDetailsVOs = null;
                transactionDetailsVOs = transactionManager.getListOfBinTransaction(terminalVO, dateVO, stringBuffer.toString(),lastFour.toString());
                FileAttachmentVO fileAttachmentVO = prepareCardVelocityExcelFile(terminalVO, transactionDetailsVOs, MonitoringKeyword.SameCardSameAmount.toString());
                if (fileAttachmentVO != null)
                {
                    monitoringAttachmentAlertDetailsVO = new MonitoringAttachmentAlertDetailsVO();
                    monitoringAttachmentAlertDetailsVO.setAlertWithAttachment(true);
                    monitoringAttachmentAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                    String validaMsg=String.valueOf(thresholdValue);
                    monitoringAttachmentAlertDetailsVO.setAlertMsg(getAlertMessage(monitoringParameterMappingVO.getAlertMessage(), validaMsg));
                    monitoringAttachmentAlertDetailsVO.setFileAttachmentVO(fileAttachmentVO);
                    monitoringAttachmentAlertDetailsVO.setActualratio(transactionDetailsVOs.size());
                    setMonitoringToEntityFlags(monitoringAttachmentAlertDetailsVO, monitoringParameterMappingVO);
                }
            }
        }
        if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
        {
            if ("daily".equals(frequency))
            {
                //Card velocity - Same account consecutive days
                DateManager dateManager = new DateManager();
                MerchantMonitoringManager merchantMonitoringManager = new MerchantMonitoringManager();
                Set<String> todayBinDetails = merchantMonitoringManager.getAllCardsGroupByBins(terminalVO, dateVO);
                List<Set> sets = new ArrayList();
                int thresholdValue = Double.valueOf(thresholdValue1).intValue();
                int days = thresholdValue;
                boolean actionToBeTaken = false;
                int i = 0;
                while (days > i)
                {
                    DateVO previousDayDateRange = dateManager.getPreviousDayDateRange(i);
                    Set<String> set1 = merchantMonitoringManager.getAllCardsGroupByBins(terminalVO, previousDayDateRange);
                    //System.out.println("set1======" + set1);
                    if (set1.size() > 0)
                    {
                        sets.add(set1);
                    }
                    i++;
                }
                StringBuffer firstSix = new StringBuffer();
                StringBuffer lastFour = new StringBuffer();
                for (String bin : todayBinDetails)
                {
                    boolean status = false;
                    for (Set set2 : sets)
                    {
                        if (set2.contains(bin))
                        {
                            status = true;
                        }
                        else
                        {
                            status = false;
                            break;
                        }
                    }
                    if (status)
                    {
                        actionToBeTaken = true;
                        if (firstSix.length() > 0)
                        {
                            firstSix.append(",");
                            lastFour.append(",");
                        }
                        firstSix.append(bin.split(":")[0]);
                        lastFour.append(bin.split(":")[1]);
                    }

                    if (actionToBeTaken && firstSix.length() > 0)
                    {
                        TransactionDAO transactionDAO = new TransactionDAO();
                        dateVO.setStartDate(dateManager.getPreviousDayDateRange(days).getStartDate());
                        List<TransactionDetailsVO> transactionDetailsVOs = transactionDAO.getListOfBinTransaction(terminalVO, dateVO, firstSix.toString(), lastFour.toString());
                        if (transactionDetailsVOs != null)
                        {
                            FileAttachmentVO fileAttachmentVO = prepareCardVelocityExcelFile(terminalVO, transactionDetailsVOs, MonitoringKeyword.SameCardConsecutive.toString());
                            if (fileAttachmentVO != null)
                            {
                                monitoringAttachmentAlertDetailsVO = new MonitoringAttachmentAlertDetailsVO();
                                monitoringAttachmentAlertDetailsVO.setAlertWithAttachment(true);
                                monitoringAttachmentAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                                String validaMsg = String.valueOf(days);
                                monitoringAttachmentAlertDetailsVO.setAlertMsg(getAlertMessage(monitoringParameterMappingVO.getAlertMessage(), validaMsg));
                                monitoringAttachmentAlertDetailsVO.setFileAttachmentVO(fileAttachmentVO);
                                monitoringAttachmentAlertDetailsVO.setActualratio(transactionDetailsVOs.size());
                                setMonitoringToEntityFlags(monitoringAttachmentAlertDetailsVO, monitoringParameterMappingVO);
                            }
                        }
                    }
                }
            }
        }
        if(MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedCountry.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
        {
            //Transaction from blacklisted country
            int thresholdValue = Double.valueOf(thresholdValue1).intValue();
            List<TransactionDetailsVO> transactionDetailsVOs=transactionManager.getTransactionListByRejectReason(terminalVO, dateVO, TransReqRejectCheck.CUSTOMER_BLACKLISTED_COUNTRY.toString());
            FileAttachmentVO fileAttachmentVO =null;
            if (transactionDetailsVOs.size() >thresholdValue)
            {
                fileAttachmentVO = prepareMatureTransactionExcelFile(terminalVO, transactionDetailsVOs, "Blocked_Country_Transaction_List");
            }
            if (fileAttachmentVO != null)
            {
                monitoringAttachmentAlertDetailsVO = new MonitoringAttachmentAlertDetailsVO();
                monitoringAttachmentAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                //String validaMsg=String.valueOf(monitoringParameterMappingVO.getAlertThreshold());
                monitoringAttachmentAlertDetailsVO.setAlertMsg(monitoringParameterMappingVO.getAlertMessage());
                monitoringAttachmentAlertDetailsVO.setFileAttachmentVO(fileAttachmentVO);
                monitoringAttachmentAlertDetailsVO.setActualratio(transactionDetailsVOs.size());
                setMonitoringToEntityFlags(monitoringAttachmentAlertDetailsVO, monitoringParameterMappingVO);
            }
        }
        if(MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedIP.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
        {
            //Transaction from blacklisted IP's
            int thresholdValue = Double.valueOf(thresholdValue1).intValue();
            List<TransactionDetailsVO> transactionDetailsVOs=transactionManager.getTransactionListByRejectReason(terminalVO, dateVO, TransReqRejectCheck.CUSTOMER_BLACKLISTED_IP.toString());
            FileAttachmentVO fileAttachmentVO =null;
            if (transactionDetailsVOs.size() >thresholdValue)
            {
                fileAttachmentVO = prepareMatureTransactionExcelFile(terminalVO, transactionDetailsVOs, "Blocked_IPs_Transaction_List");
            }
            if (fileAttachmentVO != null)
            {
                monitoringAttachmentAlertDetailsVO = new MonitoringAttachmentAlertDetailsVO();
                monitoringAttachmentAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                //String validaMsg=String.valueOf(monitoringParameterMappingVO.getAlertThreshold());
                monitoringAttachmentAlertDetailsVO.setAlertMsg(monitoringParameterMappingVO.getAlertMessage());
                monitoringAttachmentAlertDetailsVO.setFileAttachmentVO(fileAttachmentVO);
                monitoringAttachmentAlertDetailsVO.setActualratio(transactionDetailsVOs.size());
                setMonitoringToEntityFlags(monitoringAttachmentAlertDetailsVO, monitoringParameterMappingVO);
            }
        }
        if(MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedEmail.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
        {
            //Transaction from blacklisted Email's
            int thresholdValue = Double.valueOf(thresholdValue1).intValue();
            List<TransactionDetailsVO> transactionDetailsVOs=transactionManager.getTransactionListByRejectReason(terminalVO, dateVO, TransReqRejectCheck.CUSTOMER_BLACKLISTED_EMAIL.toString());
            FileAttachmentVO fileAttachmentVO =null;
            if (transactionDetailsVOs.size() > thresholdValue)
            {
                fileAttachmentVO = prepareMatureTransactionExcelFile(terminalVO, transactionDetailsVOs, "Blocked_Emails_Transaction_List");
            }
            if (fileAttachmentVO != null)
            {
                monitoringAttachmentAlertDetailsVO = new MonitoringAttachmentAlertDetailsVO();
                monitoringAttachmentAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                //String validaMsg=String.valueOf(monitoringParameterMappingVO.getAlertThreshold());
                monitoringAttachmentAlertDetailsVO.setAlertMsg(monitoringParameterMappingVO.getAlertMessage());
                monitoringAttachmentAlertDetailsVO.setFileAttachmentVO(fileAttachmentVO);
                monitoringAttachmentAlertDetailsVO.setActualratio(transactionDetailsVOs.size());
                setMonitoringToEntityFlags(monitoringAttachmentAlertDetailsVO, monitoringParameterMappingVO);
            }
        }
        if(MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedName.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
        {
            //Transaction from blacklisted Email's
            int thresholdValue = Double.valueOf(thresholdValue1).intValue();
            List<TransactionDetailsVO> transactionDetailsVOs=transactionManager.getTransactionListByRejectReason(terminalVO, dateVO, TransReqRejectCheck.CUSTOMER_BLACKLISTED_NAME.toString());
            FileAttachmentVO fileAttachmentVO =null;
            if (transactionDetailsVOs.size() > thresholdValue)
            {
                fileAttachmentVO = prepareMatureTransactionExcelFile(terminalVO, transactionDetailsVOs, "Blocked_Name_Transaction_List");
            }
            if (fileAttachmentVO != null)
            {
                monitoringAttachmentAlertDetailsVO = new MonitoringAttachmentAlertDetailsVO();
                monitoringAttachmentAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                //String validaMsg=String.valueOf(monitoringParameterMappingVO.getAlertThreshold());
                monitoringAttachmentAlertDetailsVO.setAlertMsg(monitoringParameterMappingVO.getAlertMessage());
                monitoringAttachmentAlertDetailsVO.setFileAttachmentVO(fileAttachmentVO);
                monitoringAttachmentAlertDetailsVO.setActualratio(transactionDetailsVOs.size());
                setMonitoringToEntityFlags(monitoringAttachmentAlertDetailsVO, monitoringParameterMappingVO);
            }
        }
        if(MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedCard.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
        {
            //Transaction from blacklisted Card's
            int thresholdValue = Double.valueOf(thresholdValue1).intValue();
            List<TransactionDetailsVO> transactionDetailsVOs=transactionManager.getTransactionListByRejectReason(terminalVO, dateVO, TransReqRejectCheck.CUSTOMER_BLACKLISTED_CARD.toString());
            FileAttachmentVO fileAttachmentVO =null;
            if (transactionDetailsVOs.size() > thresholdValue)
            {
                fileAttachmentVO = prepareMatureTransactionExcelFile(terminalVO, transactionDetailsVOs, "Blocked_Card_Transaction_List");
            }
            if(fileAttachmentVO != null)
            {
                monitoringAttachmentAlertDetailsVO = new MonitoringAttachmentAlertDetailsVO();
                monitoringAttachmentAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                //String validaMsg=String.valueOf(monitoringParameterMappingVO.getAlertThreshold());
                monitoringAttachmentAlertDetailsVO.setAlertMsg(monitoringParameterMappingVO.getAlertMessage());
                monitoringAttachmentAlertDetailsVO.setFileAttachmentVO(fileAttachmentVO);
                monitoringAttachmentAlertDetailsVO.setActualratio(transactionDetailsVOs.size());
                setMonitoringToEntityFlags(monitoringAttachmentAlertDetailsVO, monitoringParameterMappingVO);
            }
        }
        return monitoringAttachmentAlertDetailsVO;
    }
    public MonitoringSuspensionAlertDetailsVO calculateRandomRiskRule(MonitoringParameterMappingVO monitoringParameterMappingVO, TerminalVO terminalVO, List<TransactionVO> inCompleteTransactionVOsList, List<TransactionVO> cbTransactionVOsList, List<TransactionVO> rfTransactionVOsList,List<BinAmountVO> binAmountVOList, DateVO dateVO,String frequency)
    {
        TerminalManager terminalManager = new TerminalManager();
        TransactionManager transactionManager=new TransactionManager();
        MonitoringParameterVO monitoringParameterVO = monitoringParameterMappingVO.getMonitoringParameterVO();
        MonitoringSuspensionAlertDetailsVO monitoringSuspensionAlertDetailsVO = null;
        double thresholdValue1=0.00;

        if("daily".equals(frequency))
        {
            thresholdValue1 =monitoringParameterMappingVO.getSuspensionThreshold();
        }
        else if("weekly".equals(frequency))
        {
            thresholdValue1 =monitoringParameterMappingVO.getWeeklySuspensionThreshold();
        }
        else if("monthly".equals(frequency))
        {
            thresholdValue1 =monitoringParameterMappingVO.getMonthlySuspensionThreshold();
        }

        if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Chargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
        {
            /*Total Number Of Chargebaks*/
            int thresholdValue = Double.valueOf(thresholdValue1).intValue();
            if(cbTransactionVOsList.size()>thresholdValue)
            {
                terminalManager.doInactiveTerminalProcessing(terminalVO.getTerminalId());

                monitoringSuspensionAlertDetailsVO = new MonitoringSuspensionAlertDetailsVO();
                monitoringSuspensionAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                String validaMsg=String.valueOf(cbTransactionVOsList.size());
                monitoringSuspensionAlertDetailsVO.setAlertMsg(getAlertMessage(monitoringParameterMappingVO.getAlertMessage(), validaMsg));
                monitoringSuspensionAlertDetailsVO.setActualratio(cbTransactionVOsList.size());
                setMonitoringToEntityFlags(monitoringSuspensionAlertDetailsVO, monitoringParameterMappingVO);
            }
        }
        if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.StuckTransaction.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
        {
         /*Stuck Transaction Suspension*//*
            if (inCompleteTransactionVOsList.size() > Double.valueOf(monitoringParameterMappingVO.getSuspensionThreshold()).intValue())
            {
                terminalManager.doInactiveTerminalProcessing(terminalVO.getTerminalId());

                monitoringSuspensionAlertDetailsVO = new MonitoringSuspensionAlertDetailsVO();
                monitoringSuspensionAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                monitoringSuspensionAlertDetailsVO.setAlertMsg(monitoringParameterMappingVO.getAlertMessage());
                setMonitoringToEntityFlags(monitoringSuspensionAlertDetailsVO, monitoringParameterMappingVO);
            }*/
        }
        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.MatureChargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
        {
            /*//Chargeback of mature transaction
            if (cbTransactionVOsList.size() > Double.valueOf(monitoringParameterMappingVO.getSuspensionThreshold()).intValue())
            {
                terminalManager.doInactiveTerminalProcessing(terminalVO.getTerminalId());

                monitoringSuspensionAlertDetailsVO = new MonitoringSuspensionAlertDetailsVO();
                monitoringSuspensionAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                String validaMsg=String.valueOf(monitoringParameterMappingVO.getSuspensionThreshold());
                monitoringSuspensionAlertDetailsVO.setAlertMsg(getAlertMessage(monitoringParameterMappingVO.getAlertMessage(), validaMsg));
                setMonitoringToEntityFlags(monitoringSuspensionAlertDetailsVO, monitoringParameterMappingVO);
            }*/
        }
        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.MatureRefund.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
        {
            /*//refund of mature transaction
            if (rfTransactionVOsList.size() > Double.valueOf(monitoringParameterMappingVO.getSuspensionThreshold()).intValue())
            {
                terminalManager.doInactiveTerminalProcessing(terminalVO.getTerminalId());

                terminalManager.doInactiveTerminalProcessing(terminalVO.getTerminalId());
                monitoringSuspensionAlertDetailsVO = new MonitoringSuspensionAlertDetailsVO();
                monitoringSuspensionAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                String validaMsg=String.valueOf(monitoringParameterMappingVO.getSuspensionThreshold());
                monitoringSuspensionAlertDetailsVO.setAlertMsg(getAlertMessage(monitoringParameterMappingVO.getAlertMessage(), validaMsg));
                setMonitoringToEntityFlags(monitoringSuspensionAlertDetailsVO, monitoringParameterMappingVO);
            }*/
        }
        else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.HighAmountOrder.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
        {
            //High Deposite Amount
            boolean actionToBeTaken = false;
            double thresholdValue = thresholdValue1;
            List<TransactionDetailsVO> transactionDetailsVOs=transactionManager.getListHighRiskAmountTransaction(terminalVO,dateVO,String.valueOf(thresholdValue));
            if(transactionDetailsVOs.size()>0)
            {
                actionToBeTaken = true;
            }
            if(actionToBeTaken)
            {
                terminalManager.doInactiveTerminalProcessing(terminalVO.getTerminalId());

                monitoringSuspensionAlertDetailsVO = new MonitoringSuspensionAlertDetailsVO();
                monitoringSuspensionAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                //String validaMsg=String.valueOf(thresholdValue);
                String validaMsg=Functions.round(thresholdValue,2);
                monitoringSuspensionAlertDetailsVO.setAlertMsg(getAlertMessage(monitoringParameterMappingVO.getAlertMessage(), validaMsg));
                monitoringSuspensionAlertDetailsVO.setActualratio(transactionDetailsVOs.size());
                setMonitoringToEntityFlags(monitoringSuspensionAlertDetailsVO, monitoringParameterMappingVO);
            }
        }
        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.AuthSuccessful.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
        {
            //Pending Pre-Authorization
            /*DateManager dateManager=new DateManager();
            DateVO preAuthEndDate=dateManager.getPreviousDayDateRange(Double.valueOf(monitoringParameterMappingVO.getAlertThreshold()).intValue());
            List<TransactionDetailsVO> transactionDetailsVOs=transactionManager.getListPreAuthTransactionList(terminalVO,preAuthEndDate);
            if (transactionDetailsVOs.size()>0)
            {
                terminalManager.doInactiveTerminalProcessing(terminalVO.getTerminalId());

                monitoringSuspensionAlertDetailsVO = new MonitoringSuspensionAlertDetailsVO();
                monitoringSuspensionAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                String validaMsg=String.valueOf(Double.valueOf(monitoringParameterMappingVO.getAlertThreshold()).intValue());
                monitoringSuspensionAlertDetailsVO.setAlertMsg(getAlertMessage(monitoringParameterMappingVO.getAlertMessage(), validaMsg));
                setMonitoringToEntityFlags(monitoringSuspensionAlertDetailsVO, monitoringParameterMappingVO);
            }*/
        }
        if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardSameAmountConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
        {
            //Authorization log - Consecutive use same account same amount
            MerchantMonitoringManager merchantMonitoringManager=new MerchantMonitoringManager();
            List<TransactionDetailsVO> transactionDetailsVOs=merchantMonitoringManager.getConsequtiveSameCardSameAmountDetail(terminalVO, dateVO, Double.valueOf(thresholdValue1).intValue());
            if (transactionDetailsVOs.size() > 0)
            {
                terminalManager.doInactiveTerminalProcessing(terminalVO.getTerminalId());

                monitoringSuspensionAlertDetailsVO = new MonitoringSuspensionAlertDetailsVO();
                monitoringSuspensionAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                String validaMsg = String.valueOf(Double.valueOf(thresholdValue1).intValue());
                monitoringSuspensionAlertDetailsVO.setAlertMsg(getAlertMessage(monitoringParameterMappingVO.getAlertMessage(), validaMsg));
                monitoringSuspensionAlertDetailsVO.setActualratio(transactionDetailsVOs.size());
                setMonitoringToEntityFlags(monitoringSuspensionAlertDetailsVO, monitoringParameterMappingVO);
            }
        }
        if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
        {
            /*int sameCardSameAmountCountActual = 0;
            int sameCardSameAmountCountThreshold = 0;

            sameCardSameAmountCountThreshold = Double.valueOf(thresholdValue1).intValue();

            boolean actionNeedToBeTaken = false;
            StringBuffer firstSix = new StringBuffer();
            StringBuffer lastFour = new StringBuffer();
            for (BinAmountVO binAmountVO : binAmountVOList)
            {
                sameCardSameAmountCountActual = binAmountVO.getCount();
                String binString = binAmountVO.getBinString();
                String lastFourString = binAmountVO.getLastFourString();
                if (sameCardSameAmountCountActual > sameCardSameAmountCountThreshold)
                {
                    actionNeedToBeTaken = true;
                    if (firstSix.length() > 0)
                    {
                        firstSix.append(",");
                        lastFour.append(",");
                    }
                    firstSix.append(binString);
                    lastFour.append(lastFourString);
                }
            }
            if(actionNeedToBeTaken)
            {
                terminalManager.doInactiveTerminalProcessing(terminalVO.getTerminalId());

                monitoringSuspensionAlertDetailsVO = new MonitoringSuspensionAlertDetailsVO();
                monitoringSuspensionAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                String validaMsg=String.valueOf(firstSix.length());
                monitoringSuspensionAlertDetailsVO.setAlertMsg(getAlertMessage(monitoringParameterMappingVO.getAlertMessage(), validaMsg));
                setMonitoringToEntityFlags(monitoringSuspensionAlertDetailsVO, monitoringParameterMappingVO);
            }*/
        }
       /* else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardSameAmountConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
        {

        }*/

        if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
        {
            /*//Card velocity:Same card exceeds the threshold
            int thresholdValue = Double.valueOf(thresholdValue1).intValue();
            int sameCardThreshold =thresholdValue;
            int sameCardCountActual =0;
            boolean actionNeedToBeTaken = false;
            StringBuffer stringBuffer = new StringBuffer();
            StringBuffer lastFour = new StringBuffer();

            for (BinAmountVO binAmountVO : binAmountVOList)
            {
                sameCardCountActual = binAmountVO.getCount();
                String binString = binAmountVO.getBinString();
                String lastFourString = binAmountVO.getLastFourString();
                if (sameCardCountActual > sameCardThreshold)
                {
                    actionNeedToBeTaken = true;
                    if (stringBuffer.length() > 0)
                    {
                        stringBuffer.append(",");
                        lastFour.append(",");
                    }
                    stringBuffer.append(binString);
                    lastFour.append(lastFourString);
                }
            }
            if (actionNeedToBeTaken)
            {
                terminalManager.doInactiveTerminalProcessing(terminalVO.getTerminalId());
                monitoringSuspensionAlertDetailsVO = new MonitoringSuspensionAlertDetailsVO();
                monitoringSuspensionAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                String validaMsg=String.valueOf(thresholdValue);
                monitoringSuspensionAlertDetailsVO.setAlertMsg(getAlertMessage(monitoringParameterMappingVO.getAlertMessage(), validaMsg));
                setMonitoringToEntityFlags(monitoringSuspensionAlertDetailsVO, monitoringParameterMappingVO);
            }*/
        }

       /* if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
        {
            //Card velocity - Same account consecutive days
            DateManager dateManager = new DateManager();
            MerchantMonitoringManager merchantMonitoringManager = new MerchantMonitoringManager();
            Set<String> todayBinDetails = merchantMonitoringManager.getAllCardsGroupByBins(terminalVO, dateVO);
            List<Set> sets = new ArrayList();
            int days = Double.valueOf(thresholdValue1).intValue();
            int i = 0;
            boolean actionToBeTaken = false;
            while (days > i)
            {
                DateVO previousDayDateRange = dateManager.getPreviousDayDateRange(i);
                Set<String> set = merchantMonitoringManager.getAllCardsGroupByBins(terminalVO, previousDayDateRange);
                if (set.size() > 0)
                {
                    sets.add(set);
                }
                i++;
            }
            for (String bin : todayBinDetails)
            {
                boolean status = false;
                for (Set set : sets)
                {
                    if (set.contains(bin))
                    {
                        status = true;
                        actionToBeTaken = true;
                    }
                    else
                    {
                        status = false;
                        break;
                    }
                }
            }
            if(actionToBeTaken)
            {
                terminalManager.doInactiveTerminalProcessing(terminalVO.getTerminalId());
                monitoringSuspensionAlertDetailsVO = new MonitoringSuspensionAlertDetailsVO();
                monitoringSuspensionAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                String validaMsg=String.valueOf(days);
                monitoringSuspensionAlertDetailsVO.setAlertMsg(getAlertMessage(monitoringParameterMappingVO.getAlertMessage(), validaMsg));
                setMonitoringToEntityFlags(monitoringSuspensionAlertDetailsVO, monitoringParameterMappingVO);
            }
        }*/
        if(MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedCountry.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
        {
            //Transaction from blacklisted country
            int thresholdValue = Double.valueOf(thresholdValue1).intValue();
            List<TransactionDetailsVO> transactionDetailsVOs=transactionManager.getTransactionListByRejectReason(terminalVO, dateVO, TransReqRejectCheck.CUSTOMER_BLACKLISTED_COUNTRY.toString());
            if (transactionDetailsVOs.size() >thresholdValue)
            {
                terminalManager.doInactiveTerminalProcessing(terminalVO.getTerminalId());
                monitoringSuspensionAlertDetailsVO = new MonitoringSuspensionAlertDetailsVO();
                monitoringSuspensionAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                monitoringSuspensionAlertDetailsVO.setAlertMsg(monitoringParameterMappingVO.getAlertMessage());
                monitoringSuspensionAlertDetailsVO.setActualratio(transactionDetailsVOs.size());
                setMonitoringToEntityFlags(monitoringSuspensionAlertDetailsVO, monitoringParameterMappingVO);
            }
        }
        if(MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedIP.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
        {
            //Transaction from blacklisted IP's
            int thresholdValue = Double.valueOf(thresholdValue1).intValue();
            List<TransactionDetailsVO> transactionDetailsVOs=transactionManager.getTransactionListByRejectReason(terminalVO, dateVO, TransReqRejectCheck.CUSTOMER_BLACKLISTED_IP.toString());
            if (transactionDetailsVOs.size() >thresholdValue)
            {
                terminalManager.doInactiveTerminalProcessing(terminalVO.getTerminalId());
                monitoringSuspensionAlertDetailsVO = new MonitoringSuspensionAlertDetailsVO();
                monitoringSuspensionAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                monitoringSuspensionAlertDetailsVO.setAlertMsg(monitoringParameterMappingVO.getAlertMessage());
                monitoringSuspensionAlertDetailsVO.setActualratio(transactionDetailsVOs.size());
                setMonitoringToEntityFlags(monitoringSuspensionAlertDetailsVO, monitoringParameterMappingVO);
            }
        }
        if(MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedEmail.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
        {
            //Transaction from blacklisted Email's
            int thresholdValue = Double.valueOf(thresholdValue1).intValue();
            List<TransactionDetailsVO> transactionDetailsVOs=transactionManager.getTransactionListByRejectReason(terminalVO, dateVO, TransReqRejectCheck.CUSTOMER_BLACKLISTED_EMAIL.toString());
            if (transactionDetailsVOs.size() >thresholdValue)
            {
                terminalManager.doInactiveTerminalProcessing(terminalVO.getTerminalId());
                monitoringSuspensionAlertDetailsVO = new MonitoringSuspensionAlertDetailsVO();
                monitoringSuspensionAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                monitoringSuspensionAlertDetailsVO.setAlertMsg(monitoringParameterMappingVO.getAlertMessage());
                monitoringSuspensionAlertDetailsVO.setActualratio(transactionDetailsVOs.size());
                setMonitoringToEntityFlags(monitoringSuspensionAlertDetailsVO, monitoringParameterMappingVO);
            }
        }
        if(MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedName.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
        {
            //Transaction from blacklisted Email's
            int thresholdValue = Double.valueOf(thresholdValue1).intValue();
            List<TransactionDetailsVO> transactionDetailsVOs=transactionManager.getTransactionListByRejectReason(terminalVO, dateVO, TransReqRejectCheck.CUSTOMER_BLACKLISTED_NAME.toString());
            if (transactionDetailsVOs.size() >thresholdValue)
            {
                terminalManager.doInactiveTerminalProcessing(terminalVO.getTerminalId());
                monitoringSuspensionAlertDetailsVO = new MonitoringSuspensionAlertDetailsVO();
                monitoringSuspensionAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                monitoringSuspensionAlertDetailsVO.setAlertMsg(monitoringParameterMappingVO.getAlertMessage());
                monitoringSuspensionAlertDetailsVO.setActualratio(transactionDetailsVOs.size());
                setMonitoringToEntityFlags(monitoringSuspensionAlertDetailsVO, monitoringParameterMappingVO);
            }
        }
        if(MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedCard.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
        {
            //Transaction from blacklisted Card's
            int thresholdValue = Double.valueOf(thresholdValue1).intValue();
            List<TransactionDetailsVO> transactionDetailsVOs=transactionManager.getTransactionListByRejectReason(terminalVO, dateVO, TransReqRejectCheck.CUSTOMER_BLACKLISTED_CARD.toString());
            if (transactionDetailsVOs.size() >thresholdValue)
            {
                terminalManager.doInactiveTerminalProcessing(terminalVO.getTerminalId());
                monitoringSuspensionAlertDetailsVO = new MonitoringSuspensionAlertDetailsVO();
                monitoringSuspensionAlertDetailsVO.setMonitoringAlertName(monitoringParameterVO.getMonitoringParameterName());
                monitoringSuspensionAlertDetailsVO.setAlertMsg(monitoringParameterMappingVO.getAlertMessage());
                monitoringSuspensionAlertDetailsVO.setActualratio(transactionDetailsVOs.size());
                setMonitoringToEntityFlags(monitoringSuspensionAlertDetailsVO, monitoringParameterMappingVO);
            }
        }
        return monitoringSuspensionAlertDetailsVO;
    }
}