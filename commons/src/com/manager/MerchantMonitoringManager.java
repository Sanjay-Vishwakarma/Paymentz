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
import com.manager.dao.MerchantMonitoringDAO;
import com.manager.vo.*;
import com.manager.vo.merchantmonitoring.*;
import com.manager.vo.merchantmonitoring.enums.MonitoringFrequency;
import com.payment.Mail.FileAttachmentVO;
import com.payment.Mail.MailPlaceHolder;
import com.payment.Mail.MailService;
import com.payment.Mail.MailServiceHelper;
import com.payment.PZTransactionStatus;
import com.payment.exceptionHandler.PZDBViolationException;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 3/9/16
 * Time: 10:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchantMonitoringManager
{
    private final static String SETTLEMENT_FILE_PATH = ApplicationProperties.getProperty("SETTLEMENT_FILE_PATH");
    Logger logger = new Logger(MerchantMonitoringManager.class.getName());
    MerchantMonitoringDAO merchantMonitoringDAO = new MerchantMonitoringDAO();

    public  static void main(String args[])throws  Exception
    {
        MerchantMonitoringManager merchantMonitoringManager=new MerchantMonitoringManager();
        //merchantMonitoringManager.monthlyApprovalRatioOnCountAlert();
        //merchantMonitoringManager.dailyDeclineTransactionRatio();//DONE
        //merchantMonitoringManager.sendMonthlyRefundRatioTransactionMailOnCount();//DONE
        //merchantMonitoringManager.sendMonthlyChargebackRatioTransactionMailOnCount();//DONE
        //merchantMonitoringManager.sendMonthlyChargebackRatioTransactionMailOnAmount();//DONE
        //merchantMonitoringManager.firstSubmissionAlert();//DONE
        // merchantMonitoringManager.terminalInactiveThreshold();//DONE
        //merchantMonitoringManager.monthlySalesVSContractedSalesAlert();//DONE
        //merchantMonitoringManager.monthlyAvgTicketAmountIntimationMail();//DONE
        // merchantMonitoringManager.DailyAvgTicketAmountIntimationMail();//DONE
        //merchantMonitoringManager.lastThreeMonthAvgTicketAmountExceedsByCurrentMonth();//DONE
        //merchantMonitoringManager.monthlySalesExceedsContractSales();//DONE
        // merchantMonitoringManager.lastTwoMonthsRefundsVsCurrentMonthSales();//DONE
        //merchantMonitoringManager.lastTwoMonthsChargebacksVsCurrentMonthSales();
        merchantMonitoringManager.dailyManualCaptureAlert();

    }

    public String getMemberFirstSubmission(TerminalVO terminalVO) throws PZDBViolationException
    {
        return merchantMonitoringDAO.getMerchantFirstSubmission(terminalVO);
    }
    public String getMemberLastSubmission(TerminalVO terminalVO) throws PZDBViolationException
    {
         return merchantMonitoringDAO.getMerchantLastSubmission(terminalVO);
    }
    public TerminalProcessingDetailsVO getCurrentMonthProcessingDetails(TerminalVO terminalVO) throws Exception
    {
        TerminalProcessingDetailsVO processingDetailsVO = new TerminalProcessingDetailsVO();
        TransactionSummaryVO transactionSummaryVO = null;

        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentMonthEndDate=targetFormat.format(new Date());

        String currentMonthStartDate="";
        long day= Functions.DATEDIFF(terminalVO.getActivationDate(), currentMonthEndDate);
        if(day/30>0)
        {
            if(day%30>0)
            {
                long month=day%30;
                Calendar cal = Calendar.getInstance();
                cal.setTime(targetFormat.parse(currentMonthEndDate));
                cal.add(Calendar.DAY_OF_MONTH,(int)month*-1);
                currentMonthStartDate = targetFormat.format(cal.getTime());
            }
        }
        else
        {
            currentMonthStartDate=terminalVO.getActivationDate();
        }

        transactionSummaryVO = merchantMonitoringDAO.getCurrentMonthSales(terminalVO,currentMonthStartDate,currentMonthEndDate);
        processingDetailsVO.setSalesAmount(transactionSummaryVO.getTotalProcessingAmount());
        processingDetailsVO.setSalesCount((int) transactionSummaryVO.getTotalProcessingCount());

        transactionSummaryVO = merchantMonitoringDAO.getCurrentMonthChargebackDetails(terminalVO,currentMonthStartDate,currentMonthEndDate);
        processingDetailsVO.setChargebackCount((int) transactionSummaryVO.getCountOfChargeback());
        processingDetailsVO.setChargebackAmount(transactionSummaryVO.getChargebackAmount());

        transactionSummaryVO = merchantMonitoringDAO.getCurrentMonthDeclinedDetails(terminalVO,currentMonthStartDate,currentMonthEndDate);
        processingDetailsVO.setDeclinedCount((int) transactionSummaryVO.getCountOfAuthfailed());
        processingDetailsVO.setDeclinedAmount(transactionSummaryVO.getAuthfailedAmount());

        transactionSummaryVO = merchantMonitoringDAO.getCurrentMonthRefundDetails(terminalVO,currentMonthStartDate,currentMonthEndDate);
        processingDetailsVO.setRefundCount((int) transactionSummaryVO.getCountOfReversed());
        processingDetailsVO.setRefundAmount(transactionSummaryVO.getReversedAmount());

        processingDetailsVO.setCurrentMonthStartDate(currentMonthStartDate);
        processingDetailsVO.setCurrentMonthEndDate(currentMonthEndDate);

        return processingDetailsVO;
    }
    public TerminalProcessingDetailsVO getCurrentMonthProcessingDetailsAsPerCalendarMonth(TerminalVO terminalVO) throws Exception
    {
        TerminalProcessingDetailsVO processingDetailsVO = new TerminalProcessingDetailsVO();
        TransactionSummaryVO transactionSummaryVO = null;

        DateManager dateManager=new DateManager();
        DateVO dateVO=dateManager.getCurrentMonthDateRange();

        String currentMonthStartDate=dateVO.getStartDate();
        String currentMonthEndDate=dateVO.getEndDate();

        transactionSummaryVO = merchantMonitoringDAO.getCurrentMonthSales(terminalVO, currentMonthStartDate, currentMonthEndDate);
        processingDetailsVO.setSalesAmount(transactionSummaryVO.getTotalProcessingAmount());
        processingDetailsVO.setSalesCount((int) transactionSummaryVO.getTotalProcessingCount());

        transactionSummaryVO = merchantMonitoringDAO.getCBDetailsByDtstamp(terminalVO, currentMonthStartDate, currentMonthEndDate);
        processingDetailsVO.setChargebackCount((int) transactionSummaryVO.getCountOfChargeback());
        processingDetailsVO.setChargebackAmount(transactionSummaryVO.getChargebackAmount());

        transactionSummaryVO = merchantMonitoringDAO.getCurrentMonthDeclinedDetails(terminalVO, currentMonthStartDate, currentMonthEndDate);
        processingDetailsVO.setDeclinedCount((int) transactionSummaryVO.getCountOfAuthfailed());
        processingDetailsVO.setDeclinedAmount(transactionSummaryVO.getAuthfailedAmount());

        transactionSummaryVO = merchantMonitoringDAO.getRFDetailsByDtstamp(terminalVO, currentMonthStartDate, currentMonthEndDate);
        processingDetailsVO.setRefundCount((int) transactionSummaryVO.getCountOfReversed());
        processingDetailsVO.setRefundAmount(transactionSummaryVO.getReversedAmount());

        processingDetailsVO.setCurrentMonthStartDate(currentMonthStartDate);
        processingDetailsVO.setCurrentMonthEndDate(currentMonthEndDate);

        return processingDetailsVO;
    }
    public TerminalProcessingDetailsVO getCurrentMonthProcessingDetailsAsPerCalendarMonthByDTStamp(TerminalVO terminalVO) throws Exception
    {
        TerminalProcessingDetailsVO processingDetailsVO = new TerminalProcessingDetailsVO();
        TransactionSummaryVO transactionSummaryVO = null;

        DateManager dateManager=new DateManager();
        DateVO dateVO=dateManager.getCurrentMonthDateRange();

        String currentMonthStartDate=dateVO.getStartDate();
        String currentMonthEndDate=dateVO.getEndDate();

        transactionSummaryVO = merchantMonitoringDAO.getCurrentMonthSales(terminalVO, currentMonthStartDate, currentMonthEndDate);
        processingDetailsVO.setSalesAmount(transactionSummaryVO.getTotalProcessingAmount());
        processingDetailsVO.setSalesCount((int) transactionSummaryVO.getTotalProcessingCount());

        processingDetailsVO.setTotalProcessingAmount(processingDetailsVO.getSalesAmount() + processingDetailsVO.getDeclinedAmount());
        processingDetailsVO.setTotalProcessingCount(processingDetailsVO.getSalesCount() + processingDetailsVO.getDeclinedCount());

        transactionSummaryVO = merchantMonitoringDAO.getCBDetailsByDtstamp(terminalVO, currentMonthStartDate, currentMonthEndDate);
        processingDetailsVO.setChargebackCount((int) transactionSummaryVO.getCountOfChargeback());
        processingDetailsVO.setChargebackAmount(transactionSummaryVO.getChargebackAmount());

        transactionSummaryVO = merchantMonitoringDAO.getCurrentMonthDeclinedDetails(terminalVO, currentMonthStartDate, currentMonthEndDate);
        processingDetailsVO.setDeclinedCount((int) transactionSummaryVO.getCountOfAuthfailed());
        processingDetailsVO.setDeclinedAmount(transactionSummaryVO.getAuthfailedAmount());

        transactionSummaryVO = merchantMonitoringDAO.getRFDetailsByDtstamp(terminalVO, currentMonthStartDate, currentMonthEndDate);
        processingDetailsVO.setRefundCount((int) transactionSummaryVO.getCountOfReversed());
        processingDetailsVO.setRefundAmount(transactionSummaryVO.getReversedAmount());

        transactionSummaryVO = merchantMonitoringDAO.getRFDetailsByTimeStamp(terminalVO, currentMonthStartDate, currentMonthEndDate);
        processingDetailsVO.setOldTransRefundCount((int) transactionSummaryVO.getCountOfreserveRefund());
        processingDetailsVO.setOldTransRefundAmount(transactionSummaryVO.getReversedAmount());

        processingDetailsVO.setCurrentMonthStartDate(currentMonthStartDate);
        processingDetailsVO.setCurrentMonthEndDate(currentMonthEndDate);

        return processingDetailsVO;
    }

    public TerminalProcessingDetailsVO getPreviousMonthProcessingDetailsAsPerCalendarMonthByDTStamp(TerminalVO terminalVO) throws Exception
    {
        TerminalProcessingDetailsVO processingDetailsVO = new TerminalProcessingDetailsVO();
        TransactionSummaryVO transactionSummaryVO = null;

        DateManager dateManager=new DateManager();
        DateVO dateVO = dateManager.getPreviousMonthDateRange();

        String currentMonthStartDate=dateVO.getStartDate();
        String currentMonthEndDate=dateVO.getEndDate();

        transactionSummaryVO = merchantMonitoringDAO.getCurrentMonthSales(terminalVO, currentMonthStartDate, currentMonthEndDate);
        processingDetailsVO.setSalesAmount(transactionSummaryVO.getTotalProcessingAmount());
        processingDetailsVO.setSalesCount((int) transactionSummaryVO.getTotalProcessingCount());

        processingDetailsVO.setTotalProcessingAmount(processingDetailsVO.getSalesAmount() + processingDetailsVO.getDeclinedAmount());
        processingDetailsVO.setTotalProcessingCount(processingDetailsVO.getSalesCount() + processingDetailsVO.getDeclinedCount());

        transactionSummaryVO = merchantMonitoringDAO.getCBDetailsByDtstamp(terminalVO, currentMonthStartDate, currentMonthEndDate);
        processingDetailsVO.setChargebackCount((int) transactionSummaryVO.getCountOfChargeback());
        processingDetailsVO.setChargebackAmount(transactionSummaryVO.getChargebackAmount());

        transactionSummaryVO = merchantMonitoringDAO.getCurrentMonthDeclinedDetails(terminalVO, currentMonthStartDate, currentMonthEndDate);
        processingDetailsVO.setDeclinedCount((int) transactionSummaryVO.getCountOfAuthfailed());
        processingDetailsVO.setDeclinedAmount(transactionSummaryVO.getAuthfailedAmount());

        transactionSummaryVO = merchantMonitoringDAO.getRFDetailsByDtstamp(terminalVO, currentMonthStartDate, currentMonthEndDate);
        processingDetailsVO.setRefundCount((int) transactionSummaryVO.getCountOfReversed());
        processingDetailsVO.setRefundAmount(transactionSummaryVO.getReversedAmount());

        transactionSummaryVO = merchantMonitoringDAO.getRFDetailsByTimeStamp(terminalVO, currentMonthStartDate, currentMonthEndDate);
        processingDetailsVO.setOldTransRefundCount((int) transactionSummaryVO.getCountOfreserveRefund());
        processingDetailsVO.setOldTransRefundAmount(transactionSummaryVO.getReversedAmount());

        processingDetailsVO.setCurrentMonthStartDate(currentMonthStartDate);
        processingDetailsVO.setCurrentMonthEndDate(currentMonthEndDate);

        return processingDetailsVO;
    }


    public TerminalProcessingDetailsVO getLastThreeMonthProcessingDetailsAsPerCalendarMonth(TerminalVO terminalVO) throws Exception
    {
        TerminalProcessingDetailsVO processingDetailsVO = new TerminalProcessingDetailsVO();
        TransactionSummaryVO transactionSummaryVO = null;

        DateManager dateManager=new DateManager();
        DateVO dateVO=dateManager.getLastThreeCalendarMonthDateRange();

        String lastThreeMonthStartDate=dateVO.getStartDate();
        String lastThreeMonthEndDate=dateVO.getEndDate();

        transactionSummaryVO = merchantMonitoringDAO.getCurrentMonthSales(terminalVO, lastThreeMonthStartDate, lastThreeMonthEndDate);
        processingDetailsVO.setSalesAmount(transactionSummaryVO.getTotalProcessingAmount());
        processingDetailsVO.setSalesCount((int) transactionSummaryVO.getTotalProcessingCount());

        transactionSummaryVO = merchantMonitoringDAO.getCBDetailsByDtstamp(terminalVO, lastThreeMonthStartDate, lastThreeMonthEndDate);
        processingDetailsVO.setChargebackCount((int) transactionSummaryVO.getCountOfChargeback());
        processingDetailsVO.setChargebackAmount(transactionSummaryVO.getChargebackAmount());

        transactionSummaryVO = merchantMonitoringDAO.getCurrentMonthDeclinedDetails(terminalVO, lastThreeMonthStartDate, lastThreeMonthEndDate);
        processingDetailsVO.setDeclinedCount((int) transactionSummaryVO.getCountOfAuthfailed());
        processingDetailsVO.setDeclinedAmount(transactionSummaryVO.getAuthfailedAmount());

        transactionSummaryVO = merchantMonitoringDAO.getRFDetailsByDtstamp(terminalVO, lastThreeMonthStartDate, lastThreeMonthEndDate);
        processingDetailsVO.setRefundCount((int) transactionSummaryVO.getCountOfReversed());
        processingDetailsVO.setRefundAmount(transactionSummaryVO.getReversedAmount());

        processingDetailsVO.setCurrentMonthStartDate(lastThreeMonthStartDate);
        processingDetailsVO.setCurrentMonthEndDate(lastThreeMonthEndDate);

        return processingDetailsVO;
    }
    public TerminalProcessingDetailsVO getLastThreeMonthProcessingDetailsAsPerCalendarMonth(TerminalVO terminalVO, DateVO dateVO) throws Exception
    {
        TerminalProcessingDetailsVO processingDetailsVO = new TerminalProcessingDetailsVO();
        TransactionSummaryVO transactionSummaryVO = null;

        DateManager dateManager=new DateManager();
        //DateVO dateVO=dateManager.getLastThreeCalendarMonthDateRange();

        String lastThreeMonthStartDate = dateVO.getLastThreeMonthStartDate();
        String lastThreeMonthEndDate = dateVO.getLastThreeMonthEndDate();

        transactionSummaryVO = merchantMonitoringDAO.getCurrentMonthSales(terminalVO, lastThreeMonthStartDate, lastThreeMonthEndDate);
        processingDetailsVO.setSalesAmount(transactionSummaryVO.getTotalProcessingAmount());
        processingDetailsVO.setSalesCount((int) transactionSummaryVO.getTotalProcessingCount());

        transactionSummaryVO = merchantMonitoringDAO.getCBDetailsByDtstamp(terminalVO, lastThreeMonthStartDate, lastThreeMonthEndDate);
        processingDetailsVO.setChargebackCount((int) transactionSummaryVO.getCountOfChargeback());
        processingDetailsVO.setChargebackAmount(transactionSummaryVO.getChargebackAmount());

        transactionSummaryVO = merchantMonitoringDAO.getCurrentMonthDeclinedDetails(terminalVO, lastThreeMonthStartDate, lastThreeMonthEndDate);
        processingDetailsVO.setDeclinedCount((int) transactionSummaryVO.getCountOfAuthfailed());
        processingDetailsVO.setDeclinedAmount(transactionSummaryVO.getAuthfailedAmount());

        transactionSummaryVO = merchantMonitoringDAO.getRFDetailsByDtstamp(terminalVO, lastThreeMonthStartDate, lastThreeMonthEndDate);
        processingDetailsVO.setRefundCount((int) transactionSummaryVO.getCountOfReversed());
        processingDetailsVO.setRefundAmount(transactionSummaryVO.getReversedAmount());

        processingDetailsVO.setCurrentMonthStartDate(lastThreeMonthStartDate);
        processingDetailsVO.setCurrentMonthEndDate(lastThreeMonthEndDate);

        return processingDetailsVO;
    }


    public TerminalProcessingDetailsVO getLastTwoMonthProcessingDetailsAsPerCalendarMonth(TerminalVO terminalVO) throws Exception
    {
        TerminalProcessingDetailsVO processingDetailsVO = new TerminalProcessingDetailsVO();
        TransactionSummaryVO transactionSummaryVO = null;

        DateManager dateManager=new DateManager();
        DateVO dateVO=dateManager.getLastTwoCalendarMonthDateRange();

        String lastThreeMonthStartDate=dateVO.getStartDate();
        String lastThreeMonthEndDate=dateVO.getEndDate();

        transactionSummaryVO = merchantMonitoringDAO.getCurrentMonthSales(terminalVO, lastThreeMonthStartDate, lastThreeMonthEndDate);
        processingDetailsVO.setSalesAmount(transactionSummaryVO.getTotalProcessingAmount());
        processingDetailsVO.setSalesCount((int) transactionSummaryVO.getTotalProcessingCount());

        transactionSummaryVO = merchantMonitoringDAO.getCurrentMonthChargebackDetails(terminalVO, lastThreeMonthStartDate, lastThreeMonthEndDate);
        processingDetailsVO.setChargebackCount((int) transactionSummaryVO.getCountOfChargeback());
        processingDetailsVO.setChargebackAmount(transactionSummaryVO.getChargebackAmount());

        transactionSummaryVO = merchantMonitoringDAO.getCurrentMonthDeclinedDetails(terminalVO, lastThreeMonthStartDate, lastThreeMonthEndDate);
        processingDetailsVO.setDeclinedCount((int) transactionSummaryVO.getCountOfAuthfailed());
        processingDetailsVO.setDeclinedAmount(transactionSummaryVO.getAuthfailedAmount());

        transactionSummaryVO = merchantMonitoringDAO.getCurrentMonthRefundDetails(terminalVO, lastThreeMonthStartDate, lastThreeMonthEndDate);
        processingDetailsVO.setRefundCount((int) transactionSummaryVO.getCountOfReversed());
        processingDetailsVO.setRefundAmount(transactionSummaryVO.getReversedAmount());

        processingDetailsVO.setCurrentMonthStartDate(lastThreeMonthStartDate);
        processingDetailsVO.setCurrentMonthEndDate(lastThreeMonthEndDate);

        return processingDetailsVO;
    }
    public TerminalProcessingDetailsVO getLastMonthProcessingDetailsAsPerCalendarMonth(TerminalVO terminalVO) throws Exception
    {
        TerminalProcessingDetailsVO processingDetailsVO = new TerminalProcessingDetailsVO();
        TransactionSummaryVO transactionSummaryVO = null;

        DateManager dateManager=new DateManager();
        DateVO dateVO=dateManager.getLastMonthCalendarMonthDateRange();

        String lastThreeMonthStartDate=dateVO.getStartDate();
        String lastThreeMonthEndDate=dateVO.getEndDate();

        transactionSummaryVO = merchantMonitoringDAO.getCurrentMonthSales(terminalVO, lastThreeMonthStartDate, lastThreeMonthEndDate);
        processingDetailsVO.setSalesAmount(transactionSummaryVO.getTotalProcessingAmount());
        processingDetailsVO.setSalesCount((int) transactionSummaryVO.getTotalProcessingCount());

        transactionSummaryVO = merchantMonitoringDAO.getCBDetailsByDtstamp(terminalVO, lastThreeMonthStartDate, lastThreeMonthEndDate);
        processingDetailsVO.setChargebackCount((int) transactionSummaryVO.getCountOfChargeback());
        processingDetailsVO.setChargebackAmount(transactionSummaryVO.getChargebackAmount());

        transactionSummaryVO = merchantMonitoringDAO.getCurrentMonthDeclinedDetails(terminalVO, lastThreeMonthStartDate, lastThreeMonthEndDate);
        processingDetailsVO.setDeclinedCount((int) transactionSummaryVO.getCountOfAuthfailed());
        processingDetailsVO.setDeclinedAmount(transactionSummaryVO.getAuthfailedAmount());

        transactionSummaryVO = merchantMonitoringDAO.getRFDetailsByDtstamp(terminalVO, lastThreeMonthStartDate, lastThreeMonthEndDate);
        processingDetailsVO.setRefundCount((int) transactionSummaryVO.getCountOfReversed());
        processingDetailsVO.setRefundAmount(transactionSummaryVO.getReversedAmount());

        processingDetailsVO.setCurrentMonthStartDate(lastThreeMonthStartDate);
        processingDetailsVO.setCurrentMonthEndDate(lastThreeMonthEndDate);

        return processingDetailsVO;
    }

    public TerminalProcessingDetailsVO getLastMonthProcessingDetailsAsPerCalendarMonth(TerminalVO terminalVO, DateVO dateVO) throws Exception
    {
        TerminalProcessingDetailsVO processingDetailsVO = new TerminalProcessingDetailsVO();
        TransactionSummaryVO transactionSummaryVO = null;

       /* DateManager dateManager=new DateManager();
        DateVO dateVO=dateManager.getLastMonthCalendarMonthDateRange();*/

        String lastThreeMonthStartDate = dateVO.getPreviousMonthStartDate();
        String lastThreeMonthEndDate = dateVO.getPreviousMonthEndDate();

        transactionSummaryVO = merchantMonitoringDAO.getCurrentMonthSales(terminalVO, lastThreeMonthStartDate, lastThreeMonthEndDate);
        processingDetailsVO.setSalesAmount(transactionSummaryVO.getTotalProcessingAmount());
        processingDetailsVO.setSalesCount((int) transactionSummaryVO.getTotalProcessingCount());

        transactionSummaryVO = merchantMonitoringDAO.getCBDetailsByDtstamp(terminalVO, lastThreeMonthStartDate, lastThreeMonthEndDate);
        processingDetailsVO.setChargebackCount((int) transactionSummaryVO.getCountOfChargeback());
        processingDetailsVO.setChargebackAmount(transactionSummaryVO.getChargebackAmount());

        transactionSummaryVO = merchantMonitoringDAO.getCurrentMonthDeclinedDetails(terminalVO, lastThreeMonthStartDate, lastThreeMonthEndDate);
        processingDetailsVO.setDeclinedCount((int) transactionSummaryVO.getCountOfAuthfailed());
        processingDetailsVO.setDeclinedAmount(transactionSummaryVO.getAuthfailedAmount());

        transactionSummaryVO = merchantMonitoringDAO.getRFDetailsByDtstamp(terminalVO, lastThreeMonthStartDate, lastThreeMonthEndDate);
        processingDetailsVO.setRefundCount((int) transactionSummaryVO.getCountOfReversed());
        processingDetailsVO.setRefundAmount(transactionSummaryVO.getReversedAmount());

        processingDetailsVO.setCurrentMonthStartDate(lastThreeMonthStartDate);
        processingDetailsVO.setCurrentMonthEndDate(lastThreeMonthEndDate);

        return processingDetailsVO;
    }

    public TerminalProcessingDetailsVO getPriorMonthProcessingDetailsAsPerCalendarMonth(TerminalVO terminalVO) throws Exception
    {
        TerminalProcessingDetailsVO processingDetailsVO = new TerminalProcessingDetailsVO();
        TransactionSummaryVO transactionSummaryVO = null;

        DateManager dateManager = new DateManager();
        DateVO dateVO = dateManager.getLastMonthCalendarMonthDateRange();

        String lastThreeMonthStartDate = dateVO.getStartDate();
        String lastThreeMonthEndDate = dateVO.getEndDate();

        transactionSummaryVO = merchantMonitoringDAO.getCurrentMonthSales(terminalVO, lastThreeMonthStartDate, lastThreeMonthEndDate);
        processingDetailsVO.setSalesAmount(transactionSummaryVO.getTotalProcessingAmount());
        processingDetailsVO.setSalesCount((int) transactionSummaryVO.getTotalProcessingCount());

        transactionSummaryVO = merchantMonitoringDAO.getCBDetailsByDtstamp(terminalVO, lastThreeMonthStartDate, lastThreeMonthEndDate);
        processingDetailsVO.setChargebackCount((int) transactionSummaryVO.getCountOfChargeback());
        processingDetailsVO.setChargebackAmount(transactionSummaryVO.getChargebackAmount());

        transactionSummaryVO = merchantMonitoringDAO.getCurrentMonthDeclinedDetails(terminalVO, lastThreeMonthStartDate, lastThreeMonthEndDate);
        processingDetailsVO.setDeclinedCount((int) transactionSummaryVO.getCountOfAuthfailed());
        processingDetailsVO.setDeclinedAmount(transactionSummaryVO.getAuthfailedAmount());

        transactionSummaryVO = merchantMonitoringDAO.getRFDetailsByDtstamp(terminalVO, lastThreeMonthStartDate, lastThreeMonthEndDate);
        processingDetailsVO.setRefundCount((int) transactionSummaryVO.getCountOfReversed());
        processingDetailsVO.setRefundAmount(transactionSummaryVO.getReversedAmount());

        processingDetailsVO.setCurrentMonthStartDate(lastThreeMonthStartDate);
        processingDetailsVO.setCurrentMonthEndDate(lastThreeMonthEndDate);

        return processingDetailsVO;
    }
    public TerminalProcessingDetailsVO getLastSixMonthProcessingDetailsAsPerCalendarMonth(TerminalVO terminalVO) throws Exception
    {
        TerminalProcessingDetailsVO processingDetailsVO = new TerminalProcessingDetailsVO();
        TransactionSummaryVO transactionSummaryVO = null;

        DateManager dateManager=new DateManager();
        DateVO dateVO=dateManager.getLastSixCalendarMonthDateRange();

        String lastSixMonthStartDate=dateVO.getStartDate();
        String lastSixMonthEndDate=dateVO.getEndDate();

        transactionSummaryVO = merchantMonitoringDAO.getCurrentMonthSales(terminalVO, lastSixMonthStartDate, lastSixMonthEndDate);
        processingDetailsVO.setSalesAmount(transactionSummaryVO.getTotalProcessingAmount());
        processingDetailsVO.setSalesCount((int) transactionSummaryVO.getTotalProcessingCount());

        transactionSummaryVO = merchantMonitoringDAO.getCurrentMonthChargebackDetails(terminalVO, lastSixMonthStartDate, lastSixMonthEndDate);
        processingDetailsVO.setChargebackCount((int) transactionSummaryVO.getCountOfChargeback());
        processingDetailsVO.setChargebackAmount(transactionSummaryVO.getChargebackAmount());

        transactionSummaryVO = merchantMonitoringDAO.getCurrentMonthDeclinedDetails(terminalVO, lastSixMonthStartDate, lastSixMonthEndDate);
        processingDetailsVO.setDeclinedCount((int) transactionSummaryVO.getCountOfAuthfailed());
        processingDetailsVO.setDeclinedAmount(transactionSummaryVO.getAuthfailedAmount());

        transactionSummaryVO = merchantMonitoringDAO.getCurrentMonthRefundDetails(terminalVO, lastSixMonthStartDate, lastSixMonthEndDate);
        processingDetailsVO.setRefundCount((int) transactionSummaryVO.getCountOfReversed());
        processingDetailsVO.setRefundAmount(transactionSummaryVO.getReversedAmount());

        processingDetailsVO.setCurrentMonthStartDate(lastSixMonthStartDate);
        processingDetailsVO.setCurrentMonthEndDate(lastSixMonthEndDate);

        return processingDetailsVO;
    }
    public TerminalProcessingDetailsVO getCurrentDayProcessingDetails(TerminalVO terminalVO) throws Exception
    {
        TerminalProcessingDetailsVO processingDetailsVO = new TerminalProcessingDetailsVO();
        TransactionSummaryVO transactionSummaryVO = null;

        DateManager dateManager=new DateManager();
        DateVO dateVO=dateManager.getCurrentDayDateRange();

        String currentDayEndDate = dateVO.getStartDate();
        String currentDayStartDate = dateVO.getEndDate();

        transactionSummaryVO = merchantMonitoringDAO.getTerminalSalesDetails(terminalVO, currentDayStartDate, currentDayEndDate);
        processingDetailsVO.setSalesAmount(transactionSummaryVO.getTotalProcessingAmount());
        processingDetailsVO.setSalesCount((int) transactionSummaryVO.getTotalProcessingCount());

        transactionSummaryVO = merchantMonitoringDAO.getCurrentDayChargebackDetails(terminalVO, currentDayStartDate, currentDayEndDate);
        processingDetailsVO.setChargebackCount((int) transactionSummaryVO.getCountOfChargeback());
        processingDetailsVO.setChargebackAmount(transactionSummaryVO.getChargebackAmount());

        transactionSummaryVO = merchantMonitoringDAO.getCurrentDayDeclinedDetails(terminalVO, currentDayStartDate, currentDayEndDate);
        processingDetailsVO.setDeclinedCount((int) transactionSummaryVO.getCountOfAuthfailed());
        processingDetailsVO.setDeclinedAmount(transactionSummaryVO.getAuthfailedAmount());

        transactionSummaryVO = merchantMonitoringDAO.getCurrentDayRefundDetails(terminalVO, currentDayStartDate, currentDayEndDate);
        processingDetailsVO.setRefundCount((int) transactionSummaryVO.getCountOfReversed());
        processingDetailsVO.setRefundAmount(transactionSummaryVO.getReversedAmount());

        processingDetailsVO.setCurrentMonthStartDate(currentDayStartDate);
        processingDetailsVO.setCurrentMonthEndDate(currentDayEndDate);

        return processingDetailsVO;
    }
    public TerminalProcessingDetailsVO c(TerminalVO terminalVO) throws Exception
    {
        TerminalProcessingDetailsVO processingDetailsVO = new TerminalProcessingDetailsVO();
        TransactionSummaryVO transactionSummaryVO = null;

        DateManager dateManager=new DateManager();
        DateVO dateVO=dateManager.getCurrentDayDateRange();

        String currentDayEndDate = dateVO.getStartDate();
        String currentDayStartDate = dateVO.getEndDate();

        transactionSummaryVO = merchantMonitoringDAO.getTerminalSalesDetails(terminalVO, currentDayStartDate, currentDayEndDate);
        processingDetailsVO.setSalesAmount(transactionSummaryVO.getTotalProcessingAmount());
        processingDetailsVO.setSalesCount((int) transactionSummaryVO.getTotalProcessingCount());

        transactionSummaryVO = merchantMonitoringDAO.getCBDetailsByDtstamp(terminalVO, currentDayStartDate, currentDayEndDate);
        processingDetailsVO.setChargebackCount((int) transactionSummaryVO.getCountOfChargeback());
        processingDetailsVO.setChargebackAmount(transactionSummaryVO.getChargebackAmount());

        transactionSummaryVO = merchantMonitoringDAO.getCurrentDayDeclinedDetails(terminalVO, currentDayStartDate, currentDayEndDate);
        processingDetailsVO.setDeclinedCount((int) transactionSummaryVO.getCountOfAuthfailed());
        processingDetailsVO.setDeclinedAmount(transactionSummaryVO.getAuthfailedAmount());

        transactionSummaryVO = merchantMonitoringDAO.getRFDetailsByDtstamp(terminalVO, currentDayStartDate, currentDayEndDate);
        processingDetailsVO.setRefundCount((int) transactionSummaryVO.getCountOfReversed());
        processingDetailsVO.setRefundAmount(transactionSummaryVO.getReversedAmount());

        processingDetailsVO.setCurrentMonthStartDate(currentDayStartDate);
        processingDetailsVO.setCurrentMonthEndDate(currentDayEndDate);

        return processingDetailsVO;
    }
    public TerminalProcessingDetailsVO getTerminalProcessingDetailsByDTStamp(TerminalVO terminalVO) throws Exception
    {
        TerminalProcessingDetailsVO processingDetailsVO = new TerminalProcessingDetailsVO();
        TransactionSummaryVO transactionSummaryVO = null;

        DateManager dateManager=new DateManager();
        DateVO dateVO=dateManager.getCurrentDayDateRange();

        String currentDayStartDate = dateVO.getStartDate();
        String currentDayEndDate = dateVO.getEndDate();

        /*System.out.println("currentDayEndDate===="+currentDayStartDate);
        System.out.println("currentDayEndDate===="+currentDayEndDate);*/

        transactionSummaryVO = merchantMonitoringDAO.getCurrentMonthSales(terminalVO, currentDayStartDate, currentDayEndDate);

        processingDetailsVO.setSalesAmount(transactionSummaryVO.getTotalProcessingAmount());
        processingDetailsVO.setSalesCount((int) transactionSummaryVO.getTotalProcessingCount());

        transactionSummaryVO = merchantMonitoringDAO.getPreAuthDetails(terminalVO, currentDayStartDate, currentDayEndDate);
        processingDetailsVO.setPreAuthCount((int) transactionSummaryVO.getAuthSuccessCount());
        processingDetailsVO.setPreAuthAmount(transactionSummaryVO.getAuthSuccessAmount());

        transactionSummaryVO = merchantMonitoringDAO.getCBDetailsByDtstamp(terminalVO, currentDayStartDate, currentDayEndDate);
        processingDetailsVO.setChargebackCount((int) transactionSummaryVO.getCountOfChargeback());
        processingDetailsVO.setChargebackAmount(transactionSummaryVO.getChargebackAmount());

        transactionSummaryVO = merchantMonitoringDAO.getCurrentDayDeclinedDetails(terminalVO, currentDayStartDate, currentDayEndDate);
        processingDetailsVO.setDeclinedCount((int) transactionSummaryVO.getCountOfAuthfailed());
        processingDetailsVO.setDeclinedAmount(transactionSummaryVO.getAuthfailedAmount());

        transactionSummaryVO = merchantMonitoringDAO.getRFDetailsByDtstamp(terminalVO, currentDayStartDate, currentDayEndDate);
        processingDetailsVO.setRefundCount((int) transactionSummaryVO.getCountOfReversed());
        processingDetailsVO.setRefundAmount(transactionSummaryVO.getReversedAmount());

        processingDetailsVO.setTotalProcessingAmount(processingDetailsVO.getSalesAmount() + processingDetailsVO.getDeclinedAmount());
        processingDetailsVO.setTotalProcessingCount(processingDetailsVO.getSalesCount() + processingDetailsVO.getDeclinedCount());

        processingDetailsVO.setCurrentMonthStartDate(currentDayStartDate);
        processingDetailsVO.setCurrentMonthEndDate(currentDayEndDate);

       /* System.out.println("total processing count====="+processingDetailsVO.getTotalProcessingCount());
        System.out.println("total processing amount====="+processingDetailsVO.getTotalProcessingAmount());*/

        return processingDetailsVO;
    }

    public TerminalProcessingDetailsVO getPreviousDayTerminalProcessingDetailsByDTStamp(TerminalVO terminalVO) throws Exception
    {
        TerminalProcessingDetailsVO processingDetailsVO = new TerminalProcessingDetailsVO();
        TransactionSummaryVO transactionSummaryVO = null;

        DateManager dateManager = new DateManager();
        DateVO dateVO = dateManager.getPreviousDayDateRange();

        String currentDayStartDate = dateVO.getStartDate();
        String currentDayEndDate = dateVO.getEndDate();

        transactionSummaryVO = merchantMonitoringDAO.getCurrentMonthSales(terminalVO, currentDayStartDate, currentDayEndDate);

        processingDetailsVO.setSalesAmount(transactionSummaryVO.getTotalProcessingAmount());
        processingDetailsVO.setSalesCount((int) transactionSummaryVO.getTotalProcessingCount());

        transactionSummaryVO = merchantMonitoringDAO.getPreAuthDetails(terminalVO, currentDayStartDate, currentDayEndDate);
        processingDetailsVO.setPreAuthCount((int) transactionSummaryVO.getAuthSuccessCount());
        processingDetailsVO.setPreAuthAmount(transactionSummaryVO.getAuthSuccessAmount());

        transactionSummaryVO = merchantMonitoringDAO.getCBDetailsByDtstamp(terminalVO, currentDayStartDate, currentDayEndDate);
        processingDetailsVO.setChargebackCount((int) transactionSummaryVO.getCountOfChargeback());
        processingDetailsVO.setChargebackAmount(transactionSummaryVO.getChargebackAmount());

        transactionSummaryVO = merchantMonitoringDAO.getCurrentDayDeclinedDetails(terminalVO, currentDayStartDate, currentDayEndDate);
        processingDetailsVO.setDeclinedCount((int) transactionSummaryVO.getCountOfAuthfailed());
        processingDetailsVO.setDeclinedAmount(transactionSummaryVO.getAuthfailedAmount());

        transactionSummaryVO = merchantMonitoringDAO.getRFDetailsByDtstamp(terminalVO, currentDayStartDate, currentDayEndDate);
        processingDetailsVO.setRefundCount((int) transactionSummaryVO.getCountOfReversed());
        processingDetailsVO.setRefundAmount(transactionSummaryVO.getReversedAmount());

        processingDetailsVO.setTotalProcessingAmount(processingDetailsVO.getSalesAmount() + processingDetailsVO.getDeclinedAmount());
        processingDetailsVO.setTotalProcessingCount(processingDetailsVO.getSalesCount() + processingDetailsVO.getDeclinedCount());

        processingDetailsVO.setCurrentMonthStartDate(currentDayStartDate);
        processingDetailsVO.setCurrentMonthEndDate(currentDayEndDate);

        return processingDetailsVO;
    }


    public TerminalProcessingDetailsVO getCurrentWeekProcessingDetailsByDTStamp(TerminalVO terminalVO) throws Exception
    {
        TerminalProcessingDetailsVO processingDetailsVO = new TerminalProcessingDetailsVO();
        TransactionSummaryVO transactionSummaryVO = null;

        DateManager dateManager = new DateManager();
        DateVO dateVO = dateManager.getCalendarCurrentWeekDateRange();

        String startDate = dateVO.getStartDate();
        String endDate = dateVO.getEndDate();

        transactionSummaryVO = merchantMonitoringDAO.getCurrentMonthSales(terminalVO, startDate, endDate);

        processingDetailsVO.setSalesAmount(transactionSummaryVO.getTotalProcessingAmount());
        processingDetailsVO.setSalesCount((int) transactionSummaryVO.getTotalProcessingCount());

        processingDetailsVO.setTotalProcessingAmount(processingDetailsVO.getSalesAmount() + processingDetailsVO.getDeclinedAmount());
        processingDetailsVO.setTotalProcessingCount(processingDetailsVO.getSalesCount() + processingDetailsVO.getDeclinedCount());

        transactionSummaryVO = merchantMonitoringDAO.getCBDetailsByDtstamp(terminalVO, startDate, endDate);
        processingDetailsVO.setChargebackCount((int) transactionSummaryVO.getCountOfChargeback());
        processingDetailsVO.setChargebackAmount(transactionSummaryVO.getChargebackAmount());

        transactionSummaryVO = merchantMonitoringDAO.getCurrentDayDeclinedDetails(terminalVO, startDate, endDate);
        processingDetailsVO.setDeclinedCount((int) transactionSummaryVO.getCountOfAuthfailed());
        processingDetailsVO.setDeclinedAmount(transactionSummaryVO.getAuthfailedAmount());

        transactionSummaryVO = merchantMonitoringDAO.getRFDetailsByDtstamp(terminalVO, startDate, endDate);
        processingDetailsVO.setRefundCount((int) transactionSummaryVO.getCountOfReversed());
        processingDetailsVO.setRefundAmount(transactionSummaryVO.getReversedAmount());

        processingDetailsVO.setCurrentMonthStartDate(startDate);
        processingDetailsVO.setCurrentMonthEndDate(endDate);

        return processingDetailsVO;
    }

    public TerminalProcessingDetailsVO getPreviousWeekProcessingDetailsByDTStamp(TerminalVO terminalVO) throws Exception
    {
        TerminalProcessingDetailsVO processingDetailsVO = new TerminalProcessingDetailsVO();
        TransactionSummaryVO transactionSummaryVO = null;

        DateManager dateManager = new DateManager();
        DateVO dateVO = dateManager.getPreviousWeekDateRange();

        String startDate = dateVO.getStartDate();
        String endDate = dateVO.getEndDate();

        transactionSummaryVO = merchantMonitoringDAO.getCurrentMonthSales(terminalVO, startDate, endDate);

        processingDetailsVO.setSalesAmount(transactionSummaryVO.getTotalProcessingAmount());
        processingDetailsVO.setSalesCount((int) transactionSummaryVO.getTotalProcessingCount());

        processingDetailsVO.setTotalProcessingAmount(processingDetailsVO.getSalesAmount() + processingDetailsVO.getDeclinedAmount());
        processingDetailsVO.setTotalProcessingCount(processingDetailsVO.getSalesCount() + processingDetailsVO.getDeclinedCount());

        transactionSummaryVO = merchantMonitoringDAO.getCBDetailsByDtstamp(terminalVO, startDate, endDate);
        processingDetailsVO.setChargebackCount((int) transactionSummaryVO.getCountOfChargeback());
        processingDetailsVO.setChargebackAmount(transactionSummaryVO.getChargebackAmount());

        transactionSummaryVO = merchantMonitoringDAO.getCurrentDayDeclinedDetails(terminalVO, startDate, endDate);
        processingDetailsVO.setDeclinedCount((int) transactionSummaryVO.getCountOfAuthfailed());
        processingDetailsVO.setDeclinedAmount(transactionSummaryVO.getAuthfailedAmount());

        transactionSummaryVO = merchantMonitoringDAO.getRFDetailsByDtstamp(terminalVO, startDate, endDate);
        processingDetailsVO.setRefundCount((int) transactionSummaryVO.getCountOfReversed());
        processingDetailsVO.setRefundAmount(transactionSummaryVO.getReversedAmount());

        processingDetailsVO.setCurrentMonthStartDate(startDate);
        processingDetailsVO.setCurrentMonthEndDate(endDate);

        return processingDetailsVO;
    }


    public TransactionSummaryVO getTotalSalesAmount(TerminalVO terminalVO) throws PZDBViolationException
    {
        return merchantMonitoringDAO.getTotalSalesAmount(terminalVO);
    }
    public void sendStuckTransactionMailToAdmin()
    {
        TransactionManager transactionManager=new TransactionManager();
        MailService mailService=new MailService();
        MailServiceHelper mailServiceHelper=new MailServiceHelper();

        try
        {
            List<PZTransactionStatus> transactionStatuses = new ArrayList();
            transactionStatuses.add(PZTransactionStatus.AUTH_STARTED);
            transactionStatuses.add(PZTransactionStatus.CAPTURE_STARTED);
            transactionStatuses.add(PZTransactionStatus.MARKED_FOR_REVERSAL);
            for (PZTransactionStatus pzTransactionStatus : transactionStatuses)
            {
                List<TransactionDetailsVO> transactionDetailsVOList = transactionManager.getAllStuckTransactionList(pzTransactionStatus.toString());
                if (transactionDetailsVOList.size() > 0)
                {
                    HashMap hashMap = new HashMap();
                    hashMap.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getTabularFormat(transactionDetailsVOList));
                    hashMap.put(MailPlaceHolder.TOID, "1");
                    hashMap.put(MailPlaceHolder.TRANS_STATUS, pzTransactionStatus.toString());
                    hashMap.put(MailPlaceHolder.TRANS_COUNT,transactionDetailsVOList.size());
                    //mailService.sendMail(MailEventEnum.AUTHSTARTED_TRANSACTION_ALERT_MAIL, hashMap);
                }
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZDBViolationException---->", e);
        }
    }

    public List<MerchantTerminalThresholdVO> getMerchantTerminalThresholdDetails(String memberId) throws PZDBViolationException
    {
        return merchantMonitoringDAO.getMerchantTerminalThresholdDetails(memberId);
    }

    public MerchantTerminalThresholdVO getTerminalThresholdDetails(TerminalVO terminalVO) throws PZDBViolationException
    {
        return merchantMonitoringDAO.getTerminalThresholdDetails(terminalVO);
    }
    public List<BinAmountVO> getSameCardSameAmountDetail(TerminalVO terminalVO,DateVO dateVO) throws PZDBViolationException
    {
        return merchantMonitoringDAO.getSameCardSameAmountDetail(terminalVO,dateVO);
    }
    public List<BinAmountVO> getSameCardSameAmountConsequenceDetail(TerminalVO terminalVO,DateVO dateVO) throws PZDBViolationException
    {
        return merchantMonitoringDAO.getSameCardSameAmountConsequenceDetail(terminalVO, dateVO);
    }
    public List<BinAmountVO> getSameCardConsequently(TerminalVO terminalVO,DateVO dateVO) throws PZDBViolationException
    {
        return merchantMonitoringDAO.getSameCardConsequently(terminalVO,dateVO);
    }


    public void dailyDeclinedRatioOnCountAlert()
    {
        TerminalManager terminalManager = new TerminalManager();
        try
        {
            Map<String,List<TerminalVO>> listMap=terminalManager.getAllTerminalsGroupByMerchant();
            Map<String,List<TerminalVO>> listMapMailToSent=new HashMap();
            Set set=listMap.keySet();
            Iterator  iterator=set.iterator();
            while (iterator.hasNext())
            {
                String memberId=(String)iterator.next();

                List<TerminalVO> terminalVOList =listMap.get(memberId);
                List<TerminalVO> thresholdPassingTerminalListToMerchant=new ArrayList();

                for (TerminalVO terminalVO : terminalVOList)
                {
                    TerminalProcessingDetailsVO currentDayProcessingDetails = getTerminalProcessingDetailsByDTStamp(terminalVO);

                    double declineActualRatio = 0.00;
                    double approvedThresholdRatio = 0.00;
                    double declineThresholdRatio = 0.00;

                    if (currentDayProcessingDetails.getDeclinedCount() > 0)
                    {
                        declineActualRatio = (currentDayProcessingDetails.getDeclinedCount() / (currentDayProcessingDetails.getSalesCount() + currentDayProcessingDetails.getDeclinedCount()));
                    }

                    MerchantTerminalThresholdVO merchantTerminalThresholdVO = getTerminalThresholdDetails(terminalVO);
                    approvedThresholdRatio = merchantTerminalThresholdVO.getTerminalThresholdsVO().getDailyApprovalRatio();
                    declineThresholdRatio = 100 - approvedThresholdRatio;

                    if (declineActualRatio>declineThresholdRatio)
                    {
                        thresholdPassingTerminalListToMerchant.add(terminalVO);
                    }
                    else
                    {
                        logger.debug("All okay --"+terminalVO.toString());
                    }
                }
                if(thresholdPassingTerminalListToMerchant.size()>0)
                {
                    listMapMailToSent.put(memberId,thresholdPassingTerminalListToMerchant);
                }

            }
            if(listMapMailToSent.size()>0)
            {
                MailService mailService=new MailService();
                HashMap mailPlaceHolder=new HashMap();
                MailServiceHelper mailServiceHelper=new MailServiceHelper();
                mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(listMapMailToSent,"Exceeding Daily Deline Ratio"));
                //mailService.sendMail(MailEventEnum.DAILY_DECLINE_RATIO_ADMIN_ALERT,mailPlaceHolder);
            }
            else
            {
                logger.debug("all okay with all terminals");
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZDBViolationException---->", e);
        }
        catch (Exception e)
        {
            logger.error("Exception---->", e);
        }
    }

    public void monthlyCBRatioOnCountAlert()
    {
        TerminalManager terminalManager = new TerminalManager();
        try
        {
            Map<String,List<TerminalVO>> listMap=terminalManager.getAllTerminalsGroupByMerchant();
            Map<String,List<TerminalVO>> listMapMailToSent=new HashMap();
            Set set=listMap.keySet();
            Iterator  iterator=set.iterator();
            while (iterator.hasNext())
            {
                String memberId = (String) iterator.next();
                List<TerminalVO> terminalVOList = listMap.get(memberId);
                List<TerminalVO> thresholdPassingTerminalListToMerchant = new ArrayList();

                for (TerminalVO terminalVO : terminalVOList)
                {
                    TerminalProcessingDetailsVO currentMonthProcessingDetails = getCurrentMonthProcessingDetailsAsPerCalendarMonthByDTStamp(terminalVO);

                    double chargebackActualRatio = 0.0;
                    double chargebackThreshold = 0.0;

                    if (currentMonthProcessingDetails.getSalesCount() > 0)
                    {
                        chargebackActualRatio = (currentMonthProcessingDetails.getChargebackCount() * 100) / currentMonthProcessingDetails.getSalesCount();
                    }

                    MerchantTerminalThresholdVO merchantTerminalThresholdVO = getTerminalThresholdDetails(terminalVO);
                    chargebackThreshold = merchantTerminalThresholdVO.getTerminalThresholdsVO().getMonthlyCBRatio();

                   /* System.out.println("============================");
                    System.out.println(terminalVO.toString());
                    System.out.println("current month cb count===="+currentMonthProcessingDetails.getChargebackCount());
                    System.out.println("current month sales count===="+currentMonthProcessingDetails.getSalesCount());
                    System.out.println("chargebackActualRatio===="+chargebackActualRatio);
                    System.out.println("chargebackThreshold===="+chargebackThreshold);*/

                    if (chargebackActualRatio > chargebackThreshold)
                    {
                        thresholdPassingTerminalListToMerchant.add(terminalVO);
                    }
                    else
                    {
                        logger.debug("All okay with terminal=====" + terminalVO.toString());
                    }
                }
                if(thresholdPassingTerminalListToMerchant.size()>0)
                {
                    MailService mailService=new MailService();
                    HashMap mailPlaceHolder=new HashMap();
                    MailServiceHelper mailServiceHelper=new MailServiceHelper();
                    mailPlaceHolder.put(MailPlaceHolder.TOID, memberId);
                    mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(thresholdPassingTerminalListToMerchant,"Exceeding Monthly CB Count Ratio"));
                    //mailService.sendMail(MailEventEnum.MONTHLY_CB_RATIO_MERCHANT_ALERT_ON_COUNT,mailPlaceHolder);

                    listMapMailToSent.put(memberId,thresholdPassingTerminalListToMerchant);
                }
            }
            if(listMapMailToSent.size()>0)
            {
                //System.out.println("Action to be taken...");
                MailService mailService=new MailService();
                HashMap mailPlaceHolder=new HashMap();
                MailServiceHelper mailServiceHelper=new MailServiceHelper();
                mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getSuccessfulListOfTerminals(listMapMailToSent, "Exceeding Monthly CB Count Ratio"));
                //mailService.sendMail(MailEventEnum.MONTHLY_CB_RATIO_ADMIN_ALERT_ON_COUNT, mailPlaceHolder);
            }

        }
        catch (PZDBViolationException e)
        {
            logger.error("PZDBViolationException---->", e);
        }
        catch (Exception e)
        {
            logger.error("Exception---->", e);
        }
    }
    public void monthlyCBRatioOnAmountAlert()
    {
        TerminalManager terminalManager = new TerminalManager();
        try
        {

            Map<String,List<TerminalVO>> listMap=terminalManager.getAllTerminalsGroupByMerchant();
            Map<String,List<TerminalVO>> listMapMailToSent=new HashMap();
            Set set=listMap.keySet();
            Iterator  iterator=set.iterator();
            while (iterator.hasNext())
            {
                String memberId = (String) iterator.next();
                List<TerminalVO> terminalVOList = listMap.get(memberId);
                List<TerminalVO> thresholdPassingTerminalListToMerchant = new ArrayList();
                for (TerminalVO terminalVO : terminalVOList)
                {
                    TerminalProcessingDetailsVO currentMonthProcessingDetails =getCurrentMonthProcessingDetailsAsPerCalendarMonthByDTStamp(terminalVO);
                    double chargebackActualRatioAmt = 0.00;
                    double chargebackThresholdAmt = 0.00;

                    if (currentMonthProcessingDetails.getSalesAmount() > 0)
                    {
                        chargebackActualRatioAmt = (currentMonthProcessingDetails.getChargebackAmount()/currentMonthProcessingDetails.getSalesAmount())*100;
                    }

                    MerchantTerminalThresholdVO merchantTerminalThresholdVO = getTerminalThresholdDetails(terminalVO);
                    chargebackThresholdAmt = merchantTerminalThresholdVO.getTerminalThresholdsVO().getMonthlyCBRatioAmount();

                   /* System.out.println("============================");
                    System.out.println("chargebackActualRatioAmt="+chargebackActualRatioAmt);
                    System.out.println("chargebackThresholdAmt="+chargebackThresholdAmt);*/

                   /* System.out.println("============================");
                    System.out.println(terminalVO.toString());
                    System.out.println("current month cb amount===="+currentMonthProcessingDetails.getChargebackAmount());
                    System.out.println("current month sales amount===="+currentMonthProcessingDetails.getSalesAmount());
                    System.out.println("chargebackActualRatioAmt===="+chargebackActualRatioAmt);
                    System.out.println("chargebackThresholdAmt===="+chargebackThresholdAmt);*/

                    if (chargebackActualRatioAmt > chargebackThresholdAmt)
                    {
                        //Action to be taken
                        logger.debug("Alert need to be sent");
                        thresholdPassingTerminalListToMerchant.add(terminalVO);
                    }
                    else
                    {
                        logger.debug("All okay with terminal======"+terminalVO.toString());
                    }
                }
                if(thresholdPassingTerminalListToMerchant.size()>0)
                {
                    MailService mailService=new MailService();
                    HashMap mailPlaceHolder=new HashMap();
                    MailServiceHelper mailServiceHelper=new MailServiceHelper();
                    mailPlaceHolder.put(MailPlaceHolder.TOID, memberId);
                    mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(thresholdPassingTerminalListToMerchant,"Exceeding CB Monthly Amount Ratio") );
                    // mailService.sendMail(MailEventEnum.MONTHLY_CB_RATIO_MERCHANT_ALERT_ON_AMOUNT,mailPlaceHolder);
                    listMapMailToSent.put(memberId,thresholdPassingTerminalListToMerchant);
                }
            }
            if(listMapMailToSent.size()>0)
            {
                //System.out.println("mail need to be sent admin");

                MailService mailService=new MailService();
                HashMap mailPlaceHolder=new HashMap();
                MailServiceHelper mailServiceHelper=new MailServiceHelper();
                mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(listMapMailToSent,"Exceeding CB Monthly Amount Ratio"));
                //mailService.sendMail(MailEventEnum.MONTHLY_CB_RATIO_ADMIN_ALERT_ON_AMOUNT,mailPlaceHolder);
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZDBViolationException---->", e);
        }
        catch (Exception e)
        {
            logger.error("Exception---->", e);
        }
    }

    public void dailyCBRatioOnCountAlert()
    {
        TerminalManager terminalManager = new TerminalManager();
        try
        {
            Map<String,List<TerminalVO>> listMap=terminalManager.getAllTerminalsGroupByMerchant();
            Map<String,List<TerminalVO>> listMapMailToSent=new HashMap();
            Map<String,List<TerminalVO>> listMapMailToSent1=new HashMap();
            Set set=listMap.keySet();
            Iterator  iterator=set.iterator();
            while (iterator.hasNext())
            {
                String memberId = (String) iterator.next();
                List<TerminalVO> terminalVOList = listMap.get(memberId);
                List<TerminalVO> thresholdPassingTerminalListToMerchant = new ArrayList();
                List<TerminalVO> thresholdPassingTerminalListToMerchantForSuspension = new ArrayList();

                for (TerminalVO terminalVO : terminalVOList)
                {

                    TerminalProcessingDetailsVO currentDayProcessingDetails = getTerminalProcessingDetailsByDTStamp(terminalVO);

                    double chargebackActualRatio = 0.00;
                    double chargebackThreshold = 0.00;
                    double chargebackRatioSuspension = 0.00;

                    if (currentDayProcessingDetails.getSalesCount() > 0)
                    {
                        chargebackActualRatio = (currentDayProcessingDetails.getChargebackCount() * 100) / currentDayProcessingDetails.getSalesCount();
                    }

                    MerchantTerminalThresholdVO merchantTerminalThresholdVO = getTerminalThresholdDetails(terminalVO);
                    chargebackThreshold = merchantTerminalThresholdVO.getTerminalThresholdsVO().getDailyCBRatio();
                    chargebackRatioSuspension = merchantTerminalThresholdVO.getTerminalThresholdsVO().getDailyCBRatioSuspension();

                    /*System.out.println("============================");
                    System.out.println(terminalVO.toString());
                    System.out.println("today's cb count===="+currentDayProcessingDetails.getChargebackCount());
                    System.out.println("today's cb sales count===="+currentDayProcessingDetails.getSalesCount());
                    System.out.println("chargebackActualRatioAmt===="+chargebackActualRatio);
                    System.out.println("chargebackThresholdAmt===="+chargebackThreshold);
                    System.out.println("chargebackThresholdForSuspension===="+chargebackRatioSuspension);*/

                    if (chargebackActualRatio > chargebackThreshold)
                    {
                        logger.debug("Alert need to be sent");
                        thresholdPassingTerminalListToMerchant.add(terminalVO);
                    }
                    else
                    {
                        logger.debug("All okay with terminal for alert=====" + terminalVO.toString());
                    }

                    if (chargebackActualRatio > chargebackRatioSuspension)
                    {
                        logger.debug("Alert need to be sent");
                        boolean b=terminalManager.doInactiveTerminalProcessing(terminalVO.getTerminalId());
                        if(b==true)
                        {
                            thresholdPassingTerminalListToMerchantForSuspension.add(terminalVO);
                        }
                    }
                    else
                    {
                       logger.debug("All okay with terminal for suspension=====" + terminalVO.toString());
                    }
                }
                if(thresholdPassingTerminalListToMerchant.size()>0)
                {
                    MailService mailService=new MailService();
                    HashMap mailPlaceHolder=new HashMap();
                    MailServiceHelper mailServiceHelper=new MailServiceHelper();
                    mailPlaceHolder.put(MailPlaceHolder.TOID, memberId);
                    mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getSuccessfulListOfTerminals(thresholdPassingTerminalListToMerchant,"Exceeding Daily CB Count Ratio For Alert"));
                    //mailService.sendMail(MailEventEnum.DAILY_CB_RATIO_MERCHANT_ALERT_ON_COUNT,mailPlaceHolder);
                    listMapMailToSent.put(memberId,thresholdPassingTerminalListToMerchant);
                }
                if(thresholdPassingTerminalListToMerchantForSuspension.size()>0)
                {
                    MailService mailService=new MailService();
                    HashMap mailPlaceHolder=new HashMap();
                    MailServiceHelper mailServiceHelper=new MailServiceHelper();
                    mailPlaceHolder.put(MailPlaceHolder.TOID, memberId);
                    mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getSuccessfulListOfTerminals(thresholdPassingTerminalListToMerchantForSuspension,"Exceeding Daily CB Count Ratio For Suspension"));
                    //mailService.sendMail(MailEventEnum.DAILY_CB_RATIO_MERCHANT_ALERT_SUSPENSION,mailPlaceHolder);
                    listMapMailToSent1.put(memberId,thresholdPassingTerminalListToMerchantForSuspension);
                }
            }
            if(listMapMailToSent.size()>0)
            {
                //System.out.println("mail need to be sent admin");
                MailService mailService=new MailService();
                HashMap mailPlaceHolder=new HashMap();
                MailServiceHelper mailServiceHelper=new MailServiceHelper();
                mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(listMapMailToSent,"Exceeding Daily CB Count Ratio For Alert"));
                // mailService.sendMail(MailEventEnum.DAILY_CB_RATIO_ADMIN_ALERT_ON_COUNT,mailPlaceHolder);
            }
            if(listMapMailToSent1.size()>0)
            {
                //System.out.println("mail need to be sent admin");
                MailService mailService=new MailService();
                HashMap mailPlaceHolder=new HashMap();
                MailServiceHelper mailServiceHelper=new MailServiceHelper();
                mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(listMapMailToSent1,"Exceeding Daily CB Count Ratio For Suspension"));
                //mailService.sendMail(MailEventEnum.DAILY_CB_RATIO_ADMIN_ALERT_SUSPENSION,mailPlaceHolder);
            }

        }
        catch (PZDBViolationException e)
        {
            logger.error("PZDBViolationException---->", e);
        }
        catch (Exception e)
        {
            logger.error("Exception---->", e);
        }
    }
    public void dailyCBRatioOnAmountAlert()
    {
        TerminalManager terminalManager = new TerminalManager();
        try
        {
            Map<String,List<TerminalVO>> listMap=terminalManager.getAllTerminalsGroupByMerchant();
            Map<String,List<TerminalVO>> listMapMailToSent=new HashMap();
            Map<String,List<TerminalVO>> listMapMailToSent1=new HashMap();
            Set set=listMap.keySet();
            Iterator  iterator=set.iterator();
            while (iterator.hasNext())
            {
                String memberId = (String) iterator.next();
                List<TerminalVO> terminalVOList = listMap.get(memberId);
                List<TerminalVO> thresholdPassingTerminalListToMerchant = new ArrayList();
                List<TerminalVO> thresholdPassingTerminalListToMerchantForSuspension = new ArrayList();

                for (TerminalVO terminalVO : terminalVOList)
                {
                    TerminalProcessingDetailsVO currentDayProcessingDetails = getTerminalProcessingDetailsByDTStamp(terminalVO);
                    double chargebackActualRatioAmt = 0.00;
                    double chargebackThresholdAmt = 0.00;
                    double chargebackRatioAmtSuspension = 0.00;

                    if (currentDayProcessingDetails.getSalesAmount() > 0)
                    {
                        chargebackActualRatioAmt = (currentDayProcessingDetails.getChargebackAmount() / currentDayProcessingDetails.getSalesAmount())*100;
                    }

                    MerchantTerminalThresholdVO merchantTerminalThresholdVO = getTerminalThresholdDetails(terminalVO);
                    chargebackThresholdAmt = merchantTerminalThresholdVO.getTerminalThresholdsVO().getDailyCBRatioAmount();
                    chargebackRatioAmtSuspension = merchantTerminalThresholdVO.getTerminalThresholdsVO().getDailyCBAmountRatioSuspension();

                  /*  System.out.println("============================");
                    System.out.println(terminalVO.toString());
                    System.out.println("today's cb count===="+currentDayProcessingDetails.getChargebackAmount());
                    System.out.println("today's cb sales count===="+currentDayProcessingDetails.getSalesAmount());
                    System.out.println("chargebackActualRatioAmt===="+chargebackActualRatioAmt);
                    System.out.println("chargebackThresholdAmt===="+chargebackThresholdAmt);
                    System.out.println("chargebackRatioAmtSuspension===="+chargebackRatioAmtSuspension);*/

                    if (chargebackActualRatioAmt > chargebackThresholdAmt)
                    {
                        //Action to be taken
                        logger.debug("Alert need to be sent");
                        thresholdPassingTerminalListToMerchant.add(terminalVO);
                    }
                    else
                    {
                        logger.debug("All okay with terminal for alert======"+terminalVO.toString());
                    }

                    if (chargebackActualRatioAmt > chargebackRatioAmtSuspension)
                    {
                        //Action to be taken
                        logger.debug("Alert need to be sent");
                        boolean b = terminalManager.doInactiveTerminalProcessing(terminalVO.getTerminalId());
                        if(b==true)
                        {
                            thresholdPassingTerminalListToMerchantForSuspension.add(terminalVO);
                        }
                    }
                    else
                    {
                        logger.debug("All okay with terminal for suspensions======"+terminalVO.toString());
                    }

                }
                if(thresholdPassingTerminalListToMerchant.size()>0)
                {
                    MailService mailService=new MailService();
                    HashMap mailPlaceHolder=new HashMap();
                    MailServiceHelper mailServiceHelper=new MailServiceHelper();
                    mailPlaceHolder.put(MailPlaceHolder.TOID, memberId);
                    mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(thresholdPassingTerminalListToMerchant,"Exceeding Daily CB Amount Ratio For Alert"));
                    //mailService.sendMail(MailEventEnum.DAILY_CB_RATIO_MERCHANT_ALERT_ON_AMOUNT,mailPlaceHolder);
                    listMapMailToSent.put(memberId,thresholdPassingTerminalListToMerchant);
                }
                if(thresholdPassingTerminalListToMerchantForSuspension.size()>0)
                {
                    MailService mailService=new MailService();
                    HashMap mailPlaceHolder=new HashMap();
                    MailServiceHelper mailServiceHelper=new MailServiceHelper();
                    mailPlaceHolder.put(MailPlaceHolder.TOID, memberId);
                    mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(thresholdPassingTerminalListToMerchantForSuspension,"Exceeding Daily CB Amount Ratio For Suspension"));
                    //mailService.sendMail(MailEventEnum.DAILY_CB_RATIO_MERCHANT_ALERT_AMT_SUSPENSION,mailPlaceHolder);
                    listMapMailToSent1.put(memberId,thresholdPassingTerminalListToMerchantForSuspension);
                }
            }
            if(listMapMailToSent.size()>0)
            {
                //System.out.println("mail need to be sent admin");
                MailService mailService=new MailService();
                HashMap mailPlaceHolder=new HashMap();
                MailServiceHelper mailServiceHelper=new MailServiceHelper();
                mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(listMapMailToSent,"Exceeding Daily CB Amount Ratio"));
                //mailService.sendMail(MailEventEnum.DAILY_CB_RATIO_ADMIN_ALERT_ON_AMOUNT, mailPlaceHolder);
            }

            if(listMapMailToSent1.size()>0)
            {
                //System.out.println("mail need to be sent admin");
                MailService mailService=new MailService();
                HashMap mailPlaceHolder=new HashMap();
                MailServiceHelper mailServiceHelper=new MailServiceHelper();
                mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(listMapMailToSent1,"Exceeding Daily CB Amount Ratio For Suspension"));
                // mailService.sendMail(MailEventEnum.DAILY_CB_RATIO_ADMIN_ALERT_AMT_SUSPENSION, mailPlaceHolder);
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZDBViolationException---->",e);
        }
        catch (Exception e)
        {
            logger.error("Exception---->", e);
        }
    }
    public void monthlySuspensionOrAlertOnCBCountThreshold()throws Exception
    {
        TerminalManager terminalManager=new TerminalManager();
        //Step1:Picking all newly created terminals.
        Map<String,List<TerminalVO>> listMap=terminalManager.getAllTerminalsGroupByMerchant();
        Map<String,List<TerminalVO>> suspendListMapMailToSent=new HashMap();
        Map<String,List<TerminalVO>> alertsListMapMailToSent=new HashMap();
        Set set=listMap.keySet();
        Iterator  iterator=set.iterator();
        while (iterator.hasNext())
        {
            String memberId = (String) iterator.next();
            List<TerminalVO> terminalVOList = listMap.get(memberId);
            List<TerminalVO> thresholdPassingTerminalListToMerchant = new ArrayList();
            List<TerminalVO> suspendedPassingTerminalListToMerchant = new ArrayList();
            for (TerminalVO terminalVO : terminalVOList)
            {
                int currentMonthTargetCBCountForAlert=0;
                int currentMonthTargetCBCountForSuspend=0;
                int currentMonthActualCBCount=0;

                TerminalProcessingDetailsVO currentMonthTerminalProcessingDetailsVO=getCurrentMonthProcessingDetails(terminalVO);
                currentMonthActualCBCount=currentMonthTerminalProcessingDetailsVO.getChargebackCount();

                MerchantTerminalThresholdVO merchantTerminalThresholdVO=getTerminalThresholdDetails(terminalVO);
                TerminalThresholdsVO terminalThresholdsVO=merchantTerminalThresholdVO.getTerminalThresholdsVO();

                currentMonthTargetCBCountForAlert=terminalThresholdsVO.getAlertCBCountThreshold();
                currentMonthTargetCBCountForSuspend=terminalThresholdsVO.getSuspendCBCountThreshold();

               /* System.out.println("=============================");
                System.out.println(terminalVO.toString());
                System.out.println("currentMonthActualCBCount="+currentMonthActualCBCount);
                System.out.println("currentMonthTargetCBCountForAlert="+currentMonthTargetCBCountForAlert);
                System.out.println("currentMonthTargetCBCountForSuspend="+currentMonthTargetCBCountForSuspend);*/

                if(currentMonthActualCBCount>currentMonthTargetCBCountForAlert)
                {
                    thresholdPassingTerminalListToMerchant.add(terminalVO);
                }
                if(currentMonthActualCBCount>currentMonthTargetCBCountForSuspend)
                {
                    //Suspend the merchant.
                    boolean b=terminalManager.doInactiveTerminalProcessing(terminalVO.getTerminalId());
                    suspendedPassingTerminalListToMerchant.add(terminalVO);
                }
            }
            if(thresholdPassingTerminalListToMerchant.size()>0)
            {
                //Sent Alert to merchant
                //System.out.println("Alert need to be sent to merchant about alert");
                MailService mailService=new MailService();
                HashMap mailPlaceHolder = new HashMap();
                MailServiceHelper mailServiceHelper = new MailServiceHelper();
                mailPlaceHolder.put(MailPlaceHolder.TOID, memberId);
                mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(thresholdPassingTerminalListToMerchant,"Monthly CB Count threshold exceeds"));
                //mailService.sendMail(MailEventEnum.ALERT_ON_CB_COUNT_THRESHOLD_MERCHANT_ALERT,mailPlaceHolder);

                alertsListMapMailToSent.put(memberId,thresholdPassingTerminalListToMerchant);

            }
            if(suspendedPassingTerminalListToMerchant.size()>0)
            {
                //Sent Alert to merchant
                //System.out.println("Alert need to be sent to merchant about suspend");
                MailService mailService=new MailService();
                HashMap mailPlaceHolder = new HashMap();
                MailServiceHelper mailServiceHelper = new MailServiceHelper();
                mailPlaceHolder.put(MailPlaceHolder.TOID, memberId);
                mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(suspendedPassingTerminalListToMerchant,"Monthly CB Count threshold exceeds,Terminal suspended"));
                //mailService.sendMail(MailEventEnum.SUSPENSION_ON_CB_COUNT_THRESHOLD_MERCHANT_ALERT,mailPlaceHolder);

                suspendListMapMailToSent.put(memberId,suspendedPassingTerminalListToMerchant);
            }
        }
        if(suspendListMapMailToSent.size()>0)
        {
            //System.out.println("Alert need to be sent to admin about suspend");
            MailService mailService=new MailService();
            HashMap mailPlaceHolder=new HashMap();
            MailServiceHelper mailServiceHelper=new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(suspendListMapMailToSent,"Monthly CB Count threshold exceeds,Terminal suspended"));
            //mailService.sendMail(MailEventEnum.SUSPENSION_ON_CB_COUNT_THRESHOLD_ADMIN_ALERT,mailPlaceHolder);
        }
        if(alertsListMapMailToSent.size()>0)
        {
            //System.out.println("Alert need to be sent to admin about alert");
            MailService mailService=new MailService();
            HashMap mailPlaceHolder=new HashMap();
            MailServiceHelper mailServiceHelper=new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(alertsListMapMailToSent,"Monthly CB Count threshold exceeds"));
            //mailService.sendMail(MailEventEnum.ALERT_ON_CB_COUNT_THRESHOLD_ADMIN_ALERT,mailPlaceHolder);
        }
    }
    public void lastMonthsSalesVsCurrentMonthRefunds()throws Exception
    {
        TerminalManager terminalManager=new TerminalManager();
        //Need to add check cron start after first 10 days on months.
       /* Calendar cal = Calendar.getInstance();
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        if(dayOfMonth>10)
        {*/
        //Step1:Picking all newly created terminals.
        Map<String, List<TerminalVO>> listMap = terminalManager.getAllTerminalsGroupByMerchant();
        Map<String, List<TerminalVO>> listMapMailToSent = new HashMap();
        Set set = listMap.keySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext())
        {
            String memberId = (String) iterator.next();
            List<TerminalVO> terminalVOList = listMap.get(memberId);
            List<TerminalVO> thresholdPassingTerminalListToMerchant = new ArrayList();
            for (TerminalVO terminalVO : terminalVOList)
            {
                double lastMonthlyActualSalesAmount = 0.00;
                double currentMonthActualRefundAmount = 0.00;
                double salesVSRefundRatio = 0.00;
                double salesVSRefundRatioThreshold = 0.00;

                TerminalProcessingDetailsVO lastMonthProcessingDetailsAsPerCalendarMonth = getLastMonthProcessingDetailsAsPerCalendarMonth(terminalVO);
                lastMonthlyActualSalesAmount = lastMonthProcessingDetailsAsPerCalendarMonth.getSalesAmount();

                TerminalProcessingDetailsVO currentMonthTerminalProcessingDetailsVO = getCurrentMonthProcessingDetailsAsPerCalendarMonthByDTStamp(terminalVO);
                currentMonthActualRefundAmount = currentMonthTerminalProcessingDetailsVO.getRefundAmount();

                MerchantTerminalThresholdVO merchantTerminalThresholdVO = getTerminalThresholdDetails(terminalVO);
                TerminalThresholdsVO terminalThresholdsVO = merchantTerminalThresholdVO.getTerminalThresholdsVO();

                salesVSRefundRatioThreshold = terminalThresholdsVO.getPriorMonthRFVsCurrentMonthSales();

                if (currentMonthActualRefundAmount > 0)
                {
                    salesVSRefundRatio = (currentMonthActualRefundAmount / lastMonthlyActualSalesAmount) * 100;
                }

              /*  System.out.println("============================");
                System.out.println(terminalVO.toString());
                System.out.println("lastMonthlyActualSalesAmount====" + lastMonthlyActualSalesAmount);
                System.out.println("currentMonthActualRefundAmount====" + currentMonthActualRefundAmount);
                System.out.println("salesVSRefundRatio====" + salesVSRefundRatio);
                System.out.println("salesVSRefundRatioThreshold====" + salesVSRefundRatioThreshold);*/

                if (salesVSRefundRatio > salesVSRefundRatioThreshold)
                {
                    //System.out.println("Action to be taken.");
                    thresholdPassingTerminalListToMerchant.add(terminalVO);
                }
            }
            if (thresholdPassingTerminalListToMerchant.size() > 0)
            {
                listMapMailToSent.put(memberId, thresholdPassingTerminalListToMerchant);
            }
        }
        if (listMapMailToSent.size() > 0)
        {
            //Send mail to admin

            //System.out.println("Mail need to be sent to admin");
            MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getSuccessfulListOfTerminals(listMapMailToSent, "Last month sales exceeding current month refund"));
            //mailService.sendMail(MailEventEnum.LAST_SALES_VS_CURRENT_MONTH_REFUND_ADMIN_ALERT, mailPlaceHolder);
        }
        /*}
        else
        {
            System.out.println("Cron execution time is not valid");
        }
*/
    }

    public void dailyNewTerminalFirstSubmissionAlert() throws PZDBViolationException
    {
        TerminalManager terminalManager=new TerminalManager();
        Functions functions = new Functions();
        Map<String,List<TerminalVO>> listMap=terminalManager.getNewTerminalsGroupByMerchant();
        Map<String,List<TerminalVO>> listMapMailToSent=new HashMap();
        Set set=listMap.keySet();
        Iterator  iterator=set.iterator();
        while (iterator.hasNext())
        {
            String memberId = (String) iterator.next();
            List<TerminalVO> terminalVOList = listMap.get(memberId);
            List<TerminalVO> thresholdPassingTerminalListToMerchant = new ArrayList();
            for (TerminalVO terminalVO : terminalVOList)
            {
                //Step2://Picking terminals last transaction date.
                String terminalLastTransactionDate = getMemberLastSubmission(terminalVO);
                if (functions.isValueNull(terminalLastTransactionDate))
                {
                   logger.debug("Terminal has started processing so skipping terminal id ");
                    continue;
                }

                //Step3:Getting terminal activation date.
                String terminalActivationDate = terminalVO.getActivationDate();
                if (!functions.isValueNull(terminalActivationDate))
                {
                    logger.debug("Terminal Activation Date Not Found");
                    continue;
                }

                SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                long day = 0;
                try
                {
                    //Step4:Calculating days between terminal activation and system current date
                    day = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(terminalActivationDate)), targetFormat.format(new Date()));

                    if (day > Integer.parseInt(terminalVO.getFirstSubmissionAllowed()))
                    {
                        logger.debug("Action to be taken.");
                        thresholdPassingTerminalListToMerchant.add(terminalVO);
                    }
                    else
                    {
                        logger.debug("All okay with terminal====="+terminalVO.toString());
                    }

                }
                catch (ParseException e)
                {
                    logger.error("ParseException---->", e);
                }

            }
            if(thresholdPassingTerminalListToMerchant.size()>0)
            {
                //System.out.println("Alert to merchant");
                MailService mailService=new MailService();
                HashMap mailPlaceHolder=new HashMap();
                MailServiceHelper mailServiceHelper=new MailServiceHelper();
                mailPlaceHolder.put(MailPlaceHolder.TOID, memberId);
                mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(thresholdPassingTerminalListToMerchant,"Exceeding First Submission Threshold"));
                // mailService.sendMail(MailEventEnum.NEW_TERMINAL_FIRST_SUBMISSION_MERCHANT_ALERT,mailPlaceHolder);

                listMapMailToSent.put(memberId,thresholdPassingTerminalListToMerchant);
            }
        }
        if(listMapMailToSent.size()>0)
        {

            //System.out.println("Mail need to be sent to admin");
            MailService mailService=new MailService();
            HashMap mailPlaceHolder=new HashMap();
            MailServiceHelper mailServiceHelper=new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(listMapMailToSent,"Exceeding First Submission Threshold"));
            // mailService.sendMail(MailEventEnum.NEW_TERMINAL_FIRST_SUBMISSION_ADMIN_ALERT,mailPlaceHolder);
        }

    }
    public void dailyNewTerminalsResumeProcessingAlert() throws PZDBViolationException
    {
        TerminalManager terminalManager=new TerminalManager();
        Functions functions = new Functions();
        Map<String,List<TerminalVO>> listMap=terminalManager.getNewTerminalsGroupByMerchant();
        Map<String,List<TerminalVO>> listMapMailToSent=new HashMap();
        Set set=listMap.keySet();
        if (set.size() > 0)
        {
            Iterator iterator = set.iterator();
            while (iterator.hasNext())
            {
                String memberId = (String) iterator.next();
                List<TerminalVO> terminalVOList = listMap.get(memberId);
                List<TerminalVO> thresholdPassingTerminalListToMerchant = new ArrayList();

                //Step1:Picking all newly created terminals.
                for (TerminalVO terminalVO : terminalVOList)
                {
                    //Step2://Picking terminals last transaction date.
                    String terminalLastTransactionDate = getMemberLastSubmission(terminalVO);
                    if (!functions.isValueNull(terminalLastTransactionDate))
                    {
                        //System.out.println("Terminal has not yet started processing so skipping terminal id ");
                        continue;
                    }

                    //Step3:Getting terminal activation date.
                    String terminalActivationDate = terminalVO.getActivationDate();
                    if (!functions.isValueNull(terminalActivationDate))
                    {
                        //System.out.println("Terminal Activation Date Not Found");
                        continue;
                    }

                    MerchantTerminalThresholdVO merchantTerminalThresholdVO = getTerminalThresholdDetails(terminalVO);
                    TerminalThresholdsVO terminalThresholdsVO = merchantTerminalThresholdVO.getTerminalThresholdsVO();

                    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long day = 0;
                    try
                    {
                        //Step4:Calculating days between terminal activation and system current date
                        day = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(terminalLastTransactionDate)), targetFormat.format(new Date()));

                        //Step5://if days crossing threshold then take action.

                        /*System.out.println("=======================");
                        System.out.println(terminalVO.toString());
                        System.out.println("Resume processing threshold====" + terminalThresholdsVO.getResumeProcessingAlert());
                        System.out.println("actual inActivity period====" + day);*/

                        if (day > terminalThresholdsVO.getResumeProcessingAlert())
                        {
                           logger.debug("Action to be taken.mail to be merchant");
                            thresholdPassingTerminalListToMerchant.add(terminalVO);
                        }
                        else
                        {
                            logger.debug("All okay with terminal=====" + terminalVO.toString());
                        }

                    }
                    catch (ParseException e)
                    {
                        logger.error("ParseException---->", e);
                    }
                }
                if (thresholdPassingTerminalListToMerchant.size() > 0)
                {
                    //System.out.println("Alert to merchant");
                    MailService mailService = new MailService();
                    HashMap mailPlaceHolder = new HashMap();
                    MailServiceHelper mailServiceHelper = new MailServiceHelper();
                    mailPlaceHolder.put(MailPlaceHolder.TOID, memberId);
                    mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getSuccessfulListOfTerminals(thresholdPassingTerminalListToMerchant, "Exceeding Inactivity Period Threshold."));
                    //mailService.sendMail(MailEventEnum.NEW_TERMINAL_RESUME_PROCESSING_MERCHANT_ALERT, mailPlaceHolder);

                    listMapMailToSent.put(memberId, thresholdPassingTerminalListToMerchant);
                }

            }
        }
        else
        {
            logger.debug("Terminals Not Founds");
        }
        if(listMapMailToSent.size()>0)
        {

            //System.out.println("Mail need to be sent to admin");
            MailService mailService=new MailService();
            HashMap mailPlaceHolder=new HashMap();
            MailServiceHelper mailServiceHelper=new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(listMapMailToSent,"Exceeding Inactivity Period Threshold."));
            // mailService.sendMail(MailEventEnum.NEW_TERMINAL_RESUME_PROCESSING_ADMIN_ALERT,mailPlaceHolder);
        }
    }
    public void dailyTerminalSuspensionOnInactivityPeriodActionAlert() throws PZDBViolationException
    {
        TerminalManager terminalManager=new TerminalManager();
        Functions functions = new Functions();
        Map<String,List<TerminalVO>> listMap=terminalManager.getOldTerminalsGroupByMerchant();
        Map<String,List<TerminalVO>> listMapMailToSent=new HashMap();
        Set set=listMap.keySet();
        if (listMap.size() > 0)
        {
            Iterator iterator = set.iterator();
            while (iterator.hasNext())
            {
                String memberId = (String) iterator.next();
                List<TerminalVO> terminalVOList = listMap.get(memberId);
                List<TerminalVO> thresholdPassingTerminalListToMerchant = new ArrayList();
                for (TerminalVO terminalVO : terminalVOList)
                {
                    //Step2://Picking terminals last transaction date.
                    String terminalLastTransactionDate = getMemberLastSubmission(terminalVO);
                    if (!functions.isValueNull(terminalLastTransactionDate))
                    {
                        //System.out.println("Terminal not yet started processing so skipping");
                        continue;
                    }

                    //Step3:Getting terminal activation date.
                    String terminalActivationDate = terminalVO.getActivationDate();
                    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long day = 0;

                    try
                    {
                        //Step4:Calculating days between terminal activation and system current date
                        day = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(terminalActivationDate)), targetFormat.format(new Date()));

                        //Step5://if days crossing threshold then take action.
                        if (day > Integer.parseInt(terminalVO.getInactivePeriodAllowed()))
                        {

                            //System.out.println("Action to be taken.");
                            //Action1:stop Terminal processing.
                            boolean b = terminalManager.doInactiveTerminalProcessing(terminalVO.getTerminalId());
                            //Action2:Send intimation to merchant.
                            thresholdPassingTerminalListToMerchant.add(terminalVO);

                        }
                        else
                        {
                            logger.debug("All okay with terminal===" + terminalVO.toString());
                        }
                    }
                    catch (Exception e)
                    {
                        logger.error("Exception---->", e);
                    }

                }
                if (thresholdPassingTerminalListToMerchant.size() > 0)
                {
                    //System.out.println("merchant mail need to be sent");
                    //System.out.println("Alert to merchant");
                    MailService mailService = new MailService();
                    HashMap mailPlaceHolder = new HashMap();
                    MailServiceHelper mailServiceHelper = new MailServiceHelper();
                    mailPlaceHolder.put(MailPlaceHolder.TOID, memberId);
                    mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getSuccessfulListOfTerminals(thresholdPassingTerminalListToMerchant, "Terminal has been stopped,exceeding inActivity period"));
                    //mailService.sendMail(MailEventEnum.OLD_TERMINAL_SUSPENSION_ON_INACTIVITY_MERCHANT_ALERT, mailPlaceHolder);

                    listMapMailToSent.put(memberId, thresholdPassingTerminalListToMerchant);
                }

            }
        }
        else
        {
            logger.debug("Terminals not found");
        }
        if(listMapMailToSent.size()>0)
        {
            //System.out.println("Mail need to be sent to admin");
            MailService mailService=new MailService();
            HashMap mailPlaceHolder=new HashMap();
            MailServiceHelper mailServiceHelper=new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(listMapMailToSent,"Terminal has been stopped,exceeding inActivity period"));
            //mailService.sendMail(MailEventEnum.OLD_TERMINAL_SUSPENSION_ON_INACTIVITY_ADMIN_ALERT, mailPlaceHolder);
        }
    }

    public void dailyVsLastThreeMonthAvgTicketAlert()throws Exception
    {
        TerminalManager terminalManager=new TerminalManager();
        Map<String,List<TerminalVO>> listMap=terminalManager.getAllTerminalsGroupByMerchant();
        Map<String,List<TerminalVO>> listMapMailToSentPlusSide=new HashMap();
        Map<String,List<TerminalVO>> listMapMailToSentMinusSide=new HashMap();
        Set set=listMap.keySet();
        Iterator  iterator=set.iterator();
        while (iterator.hasNext())
        {
            String memberId = (String) iterator.next();
            List<TerminalVO> terminalVOList = listMap.get(memberId);
            List<TerminalVO> thresholdPassingTerminalListToMerchantPlusSide = new ArrayList();
            List<TerminalVO> thresholdPassingTerminalListToMerchantMinusSide = new ArrayList();
            for (TerminalVO terminalVO : terminalVOList)
            {
                double lastThreeMonthActualAvgTicketAmount=0.00;

                double currentDayActualAvgTicketAmount=0.00;
                double currentDayTargetAvgTicketAmount=0.00;

                double currentDayActualAvgTicketPercentage=0.00;
                double currentDayTargetAvgTicketPercentage=0.00;

                TerminalProcessingDetailsVO lastThreeMonthProcessingDetails =getLastThreeMonthProcessingDetailsAsPerCalendarMonth(terminalVO);
                if (lastThreeMonthProcessingDetails.getSalesCount() > 0)
                {
                    lastThreeMonthActualAvgTicketAmount=(lastThreeMonthProcessingDetails.getSalesAmount()/lastThreeMonthProcessingDetails.getSalesCount());
                }

                MerchantMonitoringManager merchantMonitoringManager=new MerchantMonitoringManager();
                MerchantTerminalThresholdVO merchantTerminalThresholdVO=merchantMonitoringManager.getTerminalThresholdDetails(terminalVO);
                TerminalThresholdsVO terminalThresholdsVO=merchantTerminalThresholdVO.getTerminalThresholdsVO();

                currentDayTargetAvgTicketPercentage=terminalThresholdsVO.getDailyVsQuarterlyAvgTicketThreshold();

                TerminalProcessingDetailsVO terminalProcessingDetailsVO= getTerminalProcessingDetailsByDTStamp(terminalVO);

                if (terminalProcessingDetailsVO.getSalesCount() > 0)
                {
                    currentDayActualAvgTicketAmount= (terminalProcessingDetailsVO.getSalesAmount()/terminalProcessingDetailsVO.getSalesCount());
                }

                currentDayActualAvgTicketPercentage=((Functions.roundDBL(currentDayActualAvgTicketAmount,2)-Functions.roundDBL(lastThreeMonthActualAvgTicketAmount,2))/Functions.roundDBL(lastThreeMonthActualAvgTicketAmount,2))*100;

                /*System.out.println("===========================");
                System.out.println("currentDayActualAvgTicketAmount=="+currentDayActualAvgTicketAmount);
                System.out.println("lastThreeMonthActualAvgTicketAmount=="+lastThreeMonthActualAvgTicketAmount);
                System.out.println("currentDayActualAvgTicketPercentage=="+currentDayActualAvgTicketPercentage);
                System.out.println("currentDayTargetAvgTicketPercentage=="+currentDayTargetAvgTicketPercentage);*/

                if(currentDayActualAvgTicketPercentage>currentDayTargetAvgTicketPercentage)
                {
                    //System.out.println("Action to be taken");
                    thresholdPassingTerminalListToMerchantPlusSide.add(terminalVO);
                }
                else if(currentDayActualAvgTicketPercentage<(currentDayTargetAvgTicketPercentage*-1))
                {
                    //System.out.println("Action to be taken");
                    thresholdPassingTerminalListToMerchantMinusSide.add(terminalVO);
                }
            }
            if(thresholdPassingTerminalListToMerchantPlusSide.size()>0)
            {
                //System.out.println("merchant mail need to be sent");
                /*System.out.println("Alert to merchant");
                MailService mailService=new MailService();
                HashMap mailPlaceHolder=new HashMap();
                MailServiceHelper mailServiceHelper=new MailServiceHelper();
                mailPlaceHolder.put(MailPlaceHolder.TOID, memberId);
                mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(thresholdPassingTerminalListToMerchant,"Exceeding Daily Avg Ticket < 50 % "));
                mailService.sendMail(MailEventEnum.DAILY_AVG_TICKET_VS_LASTTHREEMONTH_AVG_MERCHANT_ALERT,mailPlaceHolder);*/

                listMapMailToSentPlusSide.put(memberId,thresholdPassingTerminalListToMerchantPlusSide);
            }
            if(thresholdPassingTerminalListToMerchantMinusSide.size()>0)
            {
                //System.out.println("merchant mail need to be sent");
                /*System.out.println("Alert to merchant");
                MailService mailService=new MailService();
                HashMap mailPlaceHolder=new HashMap();
                MailServiceHelper mailServiceHelper=new MailServiceHelper();
                mailPlaceHolder.put(MailPlaceHolder.TOID, memberId);
                mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(thresholdPassingTerminalListToMerchant,"Exceeding Daily Avg Ticket < 50 % "));
                mailService.sendMail(MailEventEnum.DAILY_AVG_TICKET_VS_LASTTHREEMONTH_AVG_MERCHANT_ALERT,mailPlaceHolder);*/
                listMapMailToSentMinusSide.put(memberId,thresholdPassingTerminalListToMerchantMinusSide);
            }

        }
        if(listMapMailToSentPlusSide.size()>0)
        {
            //System.out.println("Mail need to be sent to admin");
            MailService mailService=new MailService();
            HashMap mailPlaceHolder=new HashMap();
            MailServiceHelper mailServiceHelper=new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(listMapMailToSentPlusSide,"Exceeding Daily Avg Ticket configured % "));
            //mailService.sendMail(MailEventEnum.DAILY_AVG_TICKET_VS_LASTTHREEMONTH_AVG_PSIDE_ADMIN_ALERT,mailPlaceHolder);
        }
        if(listMapMailToSentMinusSide.size()>0)
        {
            //System.out.println("Mail need to be sent to admin");
            MailService mailService=new MailService();
            HashMap mailPlaceHolder=new HashMap();
            MailServiceHelper mailServiceHelper=new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(listMapMailToSentMinusSide,"Exceeding Daily Avg Ticket < configured % "));
            //mailService.sendMail(MailEventEnum.DAILY_AVG_TICKET_VS_LASTTHREEMONTH_AVG_MSIDE_ADMIN_ALERT,mailPlaceHolder);
        }

    }
    public void dailyAvgTicketAmountAlert()throws Exception
    {
        TerminalManager terminalManager=new TerminalManager();
        Map<String,List<TerminalVO>> listMap=terminalManager.getAllTerminalsGroupByMerchant();
        Map<String,List<TerminalVO>> listMapMailToSentPlusSide=new HashMap();
        Map<String,List<TerminalVO>> listMapMailToSentMinusSide=new HashMap();
        Set set=listMap.keySet();
        Iterator  iterator=set.iterator();
        while (iterator.hasNext())
        {
            String memberId = (String) iterator.next();
            List<TerminalVO> terminalVOList = listMap.get(memberId);
            List<TerminalVO> thresholdPassingTerminalListToMerchantPlusSide = new ArrayList();
            List<TerminalVO> thresholdPassingTerminalListToMerchantMinusSide = new ArrayList();
            for (TerminalVO terminalVO : terminalVOList)
            {

                double dailyActualAvgTicketAmount=0.00;
                double dailyTargetAvgTicketAmount=0.00;

                double lastThreeMonthActualAvgTicketAmount = 0.00;

                double dailyActualAvgTicketPercentageThreshold=0.00;
                double dailyTargetAvgTicketPercentageThreshold=0.00;

                MerchantTerminalThresholdVO merchantTerminalThresholdVO=getTerminalThresholdDetails(terminalVO);
                TerminalThresholdsVO terminalThresholdsVO=merchantTerminalThresholdVO.getTerminalThresholdsVO();

                dailyTargetAvgTicketPercentageThreshold=Functions.roundDBL(terminalThresholdsVO.getDailyAvgTicketPercentageThreshold(),2);


                String createDate = terminalVO.getActivationDate();
                SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                long dy = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(createDate)), targetFormat.format(new Date()));
                TerminalProcessingDetailsVO currentDayProcessingDetails = getTerminalProcessingDetailsByDTStamp(terminalVO);
                if(currentDayProcessingDetails.getSalesAmount()>0)
                {
                    dailyActualAvgTicketAmount=(currentDayProcessingDetails.getSalesAmount()/currentDayProcessingDetails.getSalesCount());
                }

                if (dy <= 90)
                {
                    dailyTargetAvgTicketAmount = terminalThresholdsVO.getDailyAvgTicketThreshold();
                    dailyActualAvgTicketPercentageThreshold=(((dailyActualAvgTicketAmount-dailyTargetAvgTicketAmount)/dailyTargetAvgTicketAmount)*100);
                }
                else
                {
                    TerminalProcessingDetailsVO lastThreeMonthProcessingDetails = getLastThreeMonthProcessingDetailsAsPerCalendarMonth(terminalVO);
                    if (lastThreeMonthProcessingDetails.getSalesCount() > 0)
                    {
                        lastThreeMonthActualAvgTicketAmount = (lastThreeMonthProcessingDetails.getSalesAmount() / lastThreeMonthProcessingDetails.getSalesCount());
                        dailyActualAvgTicketPercentageThreshold = (((dailyActualAvgTicketAmount - lastThreeMonthActualAvgTicketAmount) / lastThreeMonthActualAvgTicketAmount) * 100);
                    }
                }

               /* System.out.println("=================");
                System.out.println(terminalVO.toString());
                System.out.println("today's sales="+currentDayProcessingDetails.getSalesAmount());
                System.out.println("today's month sales count="+currentDayProcessingDetails.getSalesCount());
                System.out.println("dailyActualAvgTicketAmount=="+dailyActualAvgTicketAmount);
                System.out.println("dailyTargetAvgTicketAmount=="+dailyTargetAvgTicketAmount);
                System.out.println("dailyActualAvgTicketPercentage==" + dailyActualAvgTicketPercentageThreshold);
                System.out.println("dailyTargetAvgTicketPercentageThreshold== + side"+dailyTargetAvgTicketPercentageThreshold);
                System.out.println("dailyTargetAvgTicketPercentageThreshold== - side"+dailyTargetAvgTicketPercentageThreshold*-1);*/

                if(dailyActualAvgTicketPercentageThreshold>dailyTargetAvgTicketPercentageThreshold)
                {
                    thresholdPassingTerminalListToMerchantPlusSide.add(terminalVO);
                }
                else if(dailyActualAvgTicketPercentageThreshold<(dailyTargetAvgTicketPercentageThreshold*-1))
                {
                    thresholdPassingTerminalListToMerchantMinusSide.add(terminalVO);
                }
                else
                {
                    logger.debug("All okay with terminals");
                }
            }
            if(thresholdPassingTerminalListToMerchantPlusSide.size() > 0)
            {
                listMapMailToSentPlusSide.put(memberId, thresholdPassingTerminalListToMerchantPlusSide);
            }
            if (thresholdPassingTerminalListToMerchantMinusSide.size() > 0)
            {
                listMapMailToSentMinusSide.put(memberId, thresholdPassingTerminalListToMerchantMinusSide);
            }
        }
        if(listMapMailToSentPlusSide.size()>0)
        {
            //System.out.println("Mail need to be sent to admin");
            MailService mailService=new MailService();
            HashMap mailPlaceHolder=new HashMap();
            MailServiceHelper mailServiceHelper=new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(listMapMailToSentPlusSide,"Daily avg ticket % Exceeding configuration"));
            //mailService.sendMail(MailEventEnum.DAILY_AVG_TICKET_PERCENTAGE_THRESHOLD_PLUS_SIDE_ADMIN_ALERT,mailPlaceHolder);
        }
        if(listMapMailToSentMinusSide.size()>0)
        {
            //System.out.println("Mail need to be sent to admin");
            MailService mailService=new MailService();
            HashMap mailPlaceHolder=new HashMap();
            MailServiceHelper mailServiceHelper=new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(listMapMailToSentMinusSide,"Daily avg ticket % less than configured % "));
            //mailService.sendMail(MailEventEnum.DAILY_AVG_TICKET_PERCENTAGE_THRESHOLD_MINUS_SIDE_ADMIN_ALERT,mailPlaceHolder);
        }
    }
    public void monthlyRFRatioOnAmountAlert()
    {
        TerminalManager terminalManager = new TerminalManager();
        try
        {
            Map<String,List<TerminalVO>> listMap=terminalManager.getAllTerminalsGroupByMerchant();
            Map<String,List<TerminalVO>> listMapMailToSent=new HashMap();
            Set set=listMap.keySet();
            Iterator  iterator=set.iterator();
            while (iterator.hasNext())
            {
                String memberId = (String) iterator.next();
                List<TerminalVO> terminalVOList = listMap.get(memberId);
                List<TerminalVO> thresholdPassingTerminalListToMerchant = new ArrayList();
                for (TerminalVO terminalVO : terminalVOList)
                {
                    TerminalProcessingDetailsVO currentMonthProcessingDetails = getCurrentMonthProcessingDetailsAsPerCalendarMonthByDTStamp(terminalVO);

                    double refundActualRatio = 0.00;
                    double refundThreshold = 0.00;

                    if (currentMonthProcessingDetails.getRefundAmount() > 0)
                    {
                        refundActualRatio = Functions.roundDBL((currentMonthProcessingDetails.getRefundAmount() / currentMonthProcessingDetails.getSalesAmount())*100,2);
                    }

                    MerchantTerminalThresholdVO merchantTerminalThresholdVO = getTerminalThresholdDetails(terminalVO);
                    refundThreshold = Functions.roundDBL(merchantTerminalThresholdVO.getTerminalThresholdsVO().getMonthlyRFAmountRatio(),2);

                    /*System.out.println("=================");
                    System.out.println(terminalVO.toString());
                    System.out.println("current month sales="+currentMonthProcessingDetails.getSalesAmount());
                    System.out.println("current month refund="+currentMonthProcessingDetails.getRefundAmount());
                    System.out.println("refundActualRatio="+refundActualRatio);
                    System.out.println("refundThreshold="+refundThreshold);*/

                    if(refundActualRatio > refundThreshold)
                    {
                        /*System.out.println("Alert need to be sent to merchant");*/
                        thresholdPassingTerminalListToMerchant.add(terminalVO);
                    }
                    else
                    {
                        logger.debug("All okay with terminal----"+terminalVO.toString());
                    }
                }
                if(thresholdPassingTerminalListToMerchant.size()>0)
                {
                    /*System.out.println("merchant mail need to be sent");
                    System.out.println("Alert to merchant");*/
                    MailService mailService=new MailService();
                    HashMap mailPlaceHolder=new HashMap();
                    MailServiceHelper mailServiceHelper=new MailServiceHelper();
                    mailPlaceHolder.put(MailPlaceHolder.TOID, memberId);
                    mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(thresholdPassingTerminalListToMerchant,"Exceeding monthly RF Amt Ratio"));
                    //mailService.sendMail(MailEventEnum.MONTHLY_RF_RATIO_ON_AMOUNT_MERCHANT_ALERT,mailPlaceHolder);

                    listMapMailToSent.put(memberId,thresholdPassingTerminalListToMerchant);
                }

            }
            if(listMapMailToSent.size()>0)
            {
                /*System.out.println("Mail need to be sent to admin");*/
                MailService mailService=new MailService();
                HashMap mailPlaceHolder=new HashMap();
                MailServiceHelper mailServiceHelper=new MailServiceHelper();
                mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(listMapMailToSent,"Exceeding monthly RF Amt Ratio"));
                //mailService.sendMail(MailEventEnum.MONTHLY_RF_RATIO_ON_AMOUNT_ADMIN_ALERT,mailPlaceHolder);
            }
        }
        catch (PZDBViolationException e)
        {

        }
        catch (Exception e)
        {

        }

    }
    public void dailyRFRatioOnAmountAlert()
    {
        TerminalManager terminalManager = new TerminalManager();
        try
        {
            Map<String,List<TerminalVO>> listMap=terminalManager.getAllTerminalsGroupByMerchant();
            Map<String,List<TerminalVO>> listMapMailToSent=new HashMap();
            Set set=listMap.keySet();
            Iterator  iterator=set.iterator();
            while (iterator.hasNext())
            {
                String memberId = (String) iterator.next();
                List<TerminalVO> terminalVOList = listMap.get(memberId);
                List<TerminalVO> thresholdPassingTerminalListToMerchant = new ArrayList();
                for (TerminalVO terminalVO : terminalVOList)
                {
                    TerminalProcessingDetailsVO currentDayProcessingDetails = null;
                    try
                    {
                        currentDayProcessingDetails = getTerminalProcessingDetailsByDTStamp(terminalVO);
                    }
                    catch (Exception e)
                    {
                        logger.error("Exception---->", e);
                    }

                    double refundActualRatio = 0.00;
                    double refundThreshold = 0.00;

                    if (currentDayProcessingDetails.getSalesAmount() > 0)
                    {
                        refundActualRatio =(currentDayProcessingDetails.getRefundAmount() / currentDayProcessingDetails.getSalesAmount())*100;
                    }

                    MerchantTerminalThresholdVO merchantTerminalThresholdVO = getTerminalThresholdDetails(terminalVO);
                    refundThreshold = Functions.roundDBL(merchantTerminalThresholdVO.getTerminalThresholdsVO().getDailyRFAmountRatio(),2);
                   /* System.out.println("=================");
                    System.out.println(terminalVO.toString());
                    System.out.println("today's sales="+currentDayProcessingDetails.getSalesAmount());
                    System.out.println("refund amount="+currentDayProcessingDetails.getRefundAmount());
                    System.out.println("refundActualRatio="+refundActualRatio);
                    System.out.println("refundThreshold="+refundThreshold);*/

                    if(refundActualRatio>refundThreshold)
                    {
                        /*System.out.println("Exceeding threshold====");*/
                        thresholdPassingTerminalListToMerchant.add(terminalVO);
                    }
                    else
                    {
                        logger.debug("All okay with terminal----"+terminalVO.toString());
                    }
                }
                if(thresholdPassingTerminalListToMerchant.size()>0)
                {
                    //System.out.println("Alert to merchant");
                    MailService mailService=new MailService();
                    HashMap mailPlaceHolder=new HashMap();
                    MailServiceHelper mailServiceHelper=new MailServiceHelper();
                    mailPlaceHolder.put(MailPlaceHolder.TOID, memberId);
                    mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(thresholdPassingTerminalListToMerchant,"Exceeding daily RF Amt Ratio"));
                    //mailService.sendMail(MailEventEnum.DAILY_RF_RATIO_ON_AMOUNT_MERCHANT_ALERT, mailPlaceHolder);

                    listMapMailToSent.put(memberId,thresholdPassingTerminalListToMerchant);
                }

            }
            if(listMapMailToSent.size()>0)
            {
                //System.out.println("Mail need to be sent to admin");
                MailService mailService=new MailService();
                HashMap mailPlaceHolder=new HashMap();
                MailServiceHelper mailServiceHelper=new MailServiceHelper();
                mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(listMapMailToSent,"Exceeding daily RF Amt Ratio"));
                //mailService.sendMail(MailEventEnum.DAILY_RF_RATIO_ON_AMOUNT_ADMIN_ALERT,mailPlaceHolder);
            }
        }
        catch (PZDBViolationException e)
        {

        }
//        catch (Exception e)
        {

        }

    }
    //POINT 12-DONE: CAN LIVE?YES
    public void monthlySalesVSContractedSalesAlert()throws Exception
    {
        TerminalManager terminalManager=new TerminalManager();
        Map<String,List<TerminalVO>> listMap=terminalManager.getAllTerminalsGroupByMerchant();
        Map<String,List<TerminalVO>> listMapMailToSentPlusSide=new HashMap();
        Map<String,List<TerminalVO>> listMapMailToSentMinusSide=new HashMap();
        Set set=listMap.keySet();
        Iterator  iterator=set.iterator();
        while (iterator.hasNext())
        {
            String memberId = (String) iterator.next();
            List<TerminalVO> terminalVOList = listMap.get(memberId);
            List<TerminalVO> thresholdPassingTerminalListToMerchantPlusSide = new ArrayList();
            List<TerminalVO> thresholdPassingTerminalListToMerchantMinusSide = new ArrayList();
            for (TerminalVO terminalVO : terminalVOList)
            {
                double monthlyTargetSalesAmount=0.00;
                double monthlyActualSalesAmount=0.00;
                double monthlySalesThreshold=0.00;
                double salesRatio=0.00;

                MerchantTerminalThresholdVO merchantTerminalThresholdVO=getTerminalThresholdDetails(terminalVO);
                TerminalLimitsVO terminalLimitsVO=merchantTerminalThresholdVO.getTerminalLimitsVO();
                monthlyTargetSalesAmount=terminalLimitsVO.getMonthlyAmountLimit();
                //monthlySalesThreshold=merchantTerminalThresholdVO.getTerminalThresholdsVO().getMonthlySalesThreshold();
                //System.out.println("monthlySalesThreshold==="+monthlySalesThreshold);

                TerminalProcessingDetailsVO currentMonthProcessingDetails =getCurrentMonthProcessingDetailsAsPerCalendarMonthByDTStamp(terminalVO);
                monthlyActualSalesAmount=currentMonthProcessingDetails.getSalesAmount();

                if(monthlyActualSalesAmount>0)
                {
                    salesRatio=(((monthlyActualSalesAmount-monthlyTargetSalesAmount)/monthlyTargetSalesAmount)*100);
                    //System.out.println("SalesRatio==="+salesRatio);

                }
                /*System.out.println("==================");
                System.out.println(terminalVO.toString());
                System.out.println("monthlyTargetSalesAmount=="+monthlyTargetSalesAmount);
                System.out.println("monthlyActualSalesAmount=="+monthlyActualSalesAmount);
                System.out.println("salesRatio== + side" + salesRatio*-1);
                System.out.println("salesRatio== - side" + salesRatio);
*/
                if(salesRatio>monthlySalesThreshold)
                {
                    thresholdPassingTerminalListToMerchantPlusSide.add(terminalVO);
                }
                if(salesRatio<(monthlySalesThreshold*-1))
                {
                    thresholdPassingTerminalListToMerchantMinusSide.add(terminalVO);
                }
                else
                {
                    logger.debug("All okay with terminals");
                }
            }
            if(thresholdPassingTerminalListToMerchantPlusSide.size() > 0)
            {
                listMapMailToSentPlusSide.put(memberId, thresholdPassingTerminalListToMerchantPlusSide);
            }
            if(thresholdPassingTerminalListToMerchantMinusSide.size()>0)
            {
                listMapMailToSentMinusSide.put(memberId,thresholdPassingTerminalListToMerchantMinusSide);
            }
        }

        if(listMapMailToSentPlusSide.size()>0)
        {
            //System.out.println("Mail need to be sent to admin");
            MailService mailService=new MailService();
            HashMap mailPlaceHolder=new HashMap();
            MailServiceHelper mailServiceHelper=new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(listMapMailToSentPlusSide,"Monthly Sales > 50% Of Contracted Sales"));
            // mailService.sendMail(MailEventEnum.SALES_VS_CONTRACTED_SALES_PLUS_SIDE_ADMIN_ALERT,mailPlaceHolder);
        }
        if(listMapMailToSentMinusSide.size()>0)
        {
            //System.out.println("Mail need to be sent to admin");
            MailService mailService=new MailService();
            HashMap mailPlaceHolder=new HashMap();
            MailServiceHelper mailServiceHelper=new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(listMapMailToSentMinusSide,"Monthly Sales < 50% Of Contracted Sales "));
            //mailService.sendMail(MailEventEnum.SALES_VS_CONTRACTED_SALES_MINUS_SIDE_ADMIN_ALERT,mailPlaceHolder);
        }
    }

    public void weeklySalesVSContractedSalesAlert() throws Exception
    {
        TerminalManager terminalManager = new TerminalManager();
        Map<String, List<TerminalVO>> listMap = terminalManager.getAllTerminalsGroupByMerchant();
        Map<String, List<TerminalVO>> listMapMailToSentPlusSide = new HashMap();
        Map<String, List<TerminalVO>> listMapMailToSentMinusSide = new HashMap();
        Set set = listMap.keySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext())
        {
            String memberId = (String) iterator.next();
            List<TerminalVO> terminalVOList = listMap.get(memberId);
            List<TerminalVO> thresholdPassingTerminalListToMerchantPlusSide = new ArrayList();
            List<TerminalVO> thresholdPassingTerminalListToMerchantMinusSide = new ArrayList();
            for (TerminalVO terminalVO : terminalVOList)
            {
                double monthlyTargetSalesAmount = 0.00;
                double monthlyActualSalesAmount = 0.00;
                double lastThreeMonthlyActualSalesAmount = 0.00;
                double monthlySalesThreshold = 0.00;
                double salesRatio = 0.00;

                MerchantTerminalThresholdVO merchantTerminalThresholdVO = getTerminalThresholdDetails(terminalVO);
                TerminalLimitsVO terminalLimitsVO = merchantTerminalThresholdVO.getTerminalLimitsVO();

                monthlyTargetSalesAmount = terminalLimitsVO.getMonthlyAmountLimit();
                //monthlySalesThreshold=merchantTerminalThresholdVO.getTerminalThresholdsVO().getMonthlySalesThreshold();
                //System.out.println("monthlySalesThreshold===" + monthlySalesThreshold);

                TerminalProcessingDetailsVO currentMonthProcessingDetails = getCurrentMonthProcessingDetailsAsPerCalendarMonthByDTStamp(terminalVO);
                monthlyActualSalesAmount = currentMonthProcessingDetails.getSalesAmount();

                TerminalProcessingDetailsVO lastThreeMonthProcessingDetailsAsPerCalendarMonth = getLastThreeMonthProcessingDetailsAsPerCalendarMonth(terminalVO);
                lastThreeMonthlyActualSalesAmount = lastThreeMonthProcessingDetailsAsPerCalendarMonth.getSalesAmount();

                String createDate = terminalVO.getActivationDate();
                SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                long dy = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(createDate)), targetFormat.format(new Date()));
                if (dy <= 90)
                {
                    if (monthlyActualSalesAmount > 0)
                    {
                        salesRatio = (((monthlyActualSalesAmount - monthlyTargetSalesAmount) / monthlyTargetSalesAmount) * 100);
                        //System.out.println("SalesRatio===" + salesRatio);
                    }
                }
                else
                {
                    if (monthlyActualSalesAmount > 0)
                    {
                        salesRatio = (((monthlyActualSalesAmount - lastThreeMonthlyActualSalesAmount) / lastThreeMonthlyActualSalesAmount) * 100);
                        //System.out.println("SalesRatio===" + salesRatio);
                    }
                }

                /*System.out.println("==================");
                System.out.println(terminalVO.toString());
                System.out.println("monthlyTargetSalesAmount==" + monthlyTargetSalesAmount);
                System.out.println("monthlyActualSalesAmount==" + monthlyActualSalesAmount);
                System.out.println("salesRatio== + side" + salesRatio * -1);
                System.out.println("salesRatio== - side" + salesRatio);
*/
                salesRatio = salesRatio / 4;
                monthlySalesThreshold = monthlySalesThreshold / 4;

                if (salesRatio > monthlySalesThreshold)
                {
                    thresholdPassingTerminalListToMerchantPlusSide.add(terminalVO);
                }
                if (salesRatio < (monthlySalesThreshold * -1))
                {
                    thresholdPassingTerminalListToMerchantMinusSide.add(terminalVO);
                }
                else
                {
                    logger.debug("All okay with terminals");
                }
            }
            if (thresholdPassingTerminalListToMerchantPlusSide.size() > 0)
            {
                listMapMailToSentPlusSide.put(memberId, thresholdPassingTerminalListToMerchantPlusSide);
            }
            if (thresholdPassingTerminalListToMerchantMinusSide.size() > 0)
            {
                listMapMailToSentMinusSide.put(memberId, thresholdPassingTerminalListToMerchantMinusSide);
            }
        }

        if (listMapMailToSentPlusSide.size() > 0)
        {
            //System.out.println("Mail need to be sent to admin");
            MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getSuccessfulListOfTerminals(listMapMailToSentPlusSide, "Monthly Sales > 50% Of Contracted Sales"));
            //mailService.sendMail(MailEventEnum.SALES_VS_CONTRACTED_SALES_PLUS_SIDE_ADMIN_ALERT, mailPlaceHolder);
        }
        if (listMapMailToSentMinusSide.size() > 0)
        {
            //System.out.println("Mail need to be sent to admin");
            MailService mailService = new MailService();
            HashMap mailPlaceHolder = new HashMap();
            MailServiceHelper mailServiceHelper = new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION, mailServiceHelper.getSuccessfulListOfTerminals(listMapMailToSentMinusSide, "Monthly Sales < 50% Of Contracted Sales "));
            //mailService.sendMail(MailEventEnum.SALES_VS_CONTRACTED_SALES_MINUS_SIDE_ADMIN_ALERT, mailPlaceHolder);
        }
    }

    //Need to be updated as per isService flag.
    public void dailyManualCaptureAlert()throws Exception
    {
        TerminalManager terminalManager=new TerminalManager();
        Map<String,List<TerminalVO>> listMap=terminalManager.getAuthCaptureModeAllTerminalsGroupByMerchant();
        Map<String,List<TerminalVO>> listMapMailToSent=new HashMap();
        Set set=listMap.keySet();
        Iterator  iterator=set.iterator();
        String fileName=null;
        String filePath=null;
        while (iterator.hasNext())
        {
            String memberId = (String) iterator.next();
            List<TerminalVO> terminalVOList = listMap.get(memberId);
            List<TerminalVO> thresholdPassingTerminalListToMerchant = new ArrayList();
            List<FileAttachmentVO> fileAttachmentVOs=new ArrayList();

            for (TerminalVO terminalVO : terminalVOList)
            {
                MerchantTerminalThresholdVO merchantTerminalThresholdVO = getTerminalThresholdDetails(terminalVO);
                TerminalThresholdsVO terminalThresholdsVO = merchantTerminalThresholdVO.getTerminalThresholdsVO();

                List<TransactionVO> transactionVOs = null;

                GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(terminalVO.getAccountId());
                GatewayType gatewayType = GatewayTypeService.getGatewayType(gatewayAccount.getPgTypeId());
                String tableName = Database.getTableName(gatewayType.getGateway());

                MerchantMonitoringDAO merchantMonitoringDAO = new MerchantMonitoringDAO();
                transactionVOs = merchantMonitoringDAO.getTransactionListByDtstamp(tableName, terminalVO, "authsuccessful");
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
                    int thresholdDays = terminalThresholdsVO.getManualCaptureAlertThreshold();
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

                    fileName = "Merchant_Pending_Manual_Capture_" + terminalVO.getMemberId() + "_" + terminalVO.getAccountId() + "_" + terminalVO.toString() + "_" + currentSystemDate;
                    fileName = fileName + ".xls";

                    File filepath = new File(SETTLEMENT_FILE_PATH + fileName);
                    //System.out.println("Looking for the file in...." + filepath.getCanonicalPath());
                    filePath = filepath.getPath();

                    FileOutputStream out = new FileOutputStream(filepath);
                    workbook.write(out);
                    out.close();
                    logger.debug("Excel written successfully on drive");

                    thresholdPassingTerminalListToMerchant.add(terminalVO);

                    FileAttachmentVO fileAttachmentVO = new FileAttachmentVO();
                    fileAttachmentVO.setFileName(fileName);
                    fileAttachmentVO.setFilePath(filePath);
                    fileAttachmentVOs.add(fileAttachmentVO);
                }
                else
                {
                   logger.debug("All okay with terminal==" + terminalVO.toString());
                }


            }
            if(thresholdPassingTerminalListToMerchant.size()>0)
            {
                //System.out.println("Alert to merchant");
                MailService mailService=new MailService();
                HashMap mailPlaceHolder=new HashMap();
                MailServiceHelper mailServiceHelper=new MailServiceHelper();
                /*mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILENAME,fileName);
                mailPlaceHolder.put(MailPlaceHolder.ATTACHMENTFILEPATH, filePath);*/
                mailPlaceHolder.put(MailPlaceHolder.TOID, memberId);
                mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(thresholdPassingTerminalListToMerchant,"Exceeding manual capture threshold"));
                //mailService.sendMailWithMultipleAttachment(MailEventEnum.DAILY_MANUAL_CAPTURE_MERCHANT_ALERT, mailPlaceHolder, fileAttachmentVOs);
                listMapMailToSent.put(memberId,thresholdPassingTerminalListToMerchant);
            }
        }
        if(listMapMailToSent.size()>0)
        {
            //System.out.println("Mail need to be sent to admin");
            MailService mailService=new MailService();
            HashMap mailPlaceHolder=new HashMap();
            MailServiceHelper mailServiceHelper=new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(listMapMailToSent,"Exceeding manual capture threshold."));
            //mailService.sendMail(MailEventEnum.DAILY_MANUAL_CAPTURE_ADMIN_ALERT,mailPlaceHolder);
        }
    }
    public void dailySameCardSameAmountAlert()throws Exception
    {
        TerminalManager terminalManager=new TerminalManager();
        Map<String,List<TerminalVO>> listMap=terminalManager.getAllTerminalsGroupByMerchant();
        Map<String,List<TerminalVO>> listMapMailToSent=new HashMap();
        Set set=listMap.keySet();
        Iterator  iterator=set.iterator();
        DateManager dateManager=new DateManager();
        while (iterator.hasNext())
        {
            String memberId = (String) iterator.next();
            List<TerminalVO> terminalVOList = listMap.get(memberId);
            List<TerminalVO> thresholdPassingTerminalListToMerchant = new ArrayList();
            for (TerminalVO terminalVO : terminalVOList)
            {
                int sameCardSameAmountCountActual=0;
                int sameCardSameAmountCountThreshold=0;

                MerchantTerminalThresholdVO merchantTerminalThresholdVO=getTerminalThresholdDetails(terminalVO);
                TerminalThresholdsVO terminalThresholdsVO=merchantTerminalThresholdVO.getTerminalThresholdsVO();

                sameCardSameAmountCountThreshold=terminalThresholdsVO.getSameCardSameAmountThreshold();

                DateVO dateVO=dateManager.getCurrentDayDateRange();
                List<BinAmountVO> binAmountVOList=getSameCardSameAmountDetail(terminalVO,dateVO);
                for(BinAmountVO binAmountVO:binAmountVOList)
                {
                    sameCardSameAmountCountActual=binAmountVO.getCount();
                   /* System.out.println("==================");
                    System.out.println(terminalVO.toString());
                    System.out.println("sameCardSameAmountCountActual=="+sameCardSameAmountCountActual);
                    System.out.println("sameCardSameAmountCountThreshold=="+sameCardSameAmountCountThreshold);
                    System.out.println("bin String=="+binAmountVO.getBinString());
                    System.out.println("bin Amount=="+binAmountVO.getBinAmount());*/

                    if(sameCardSameAmountCountActual>sameCardSameAmountCountThreshold)
                    {
                       logger.debug("action need to be taken...");
                        thresholdPassingTerminalListToMerchant.add(terminalVO);
                    }
                    else
                    {
                        logger.debug("All okay with terminal===="+terminalVO.toString());
                    }
                }
            }
            if(thresholdPassingTerminalListToMerchant.size() > 0)
            {
                //System.out.println("Merchant mail need to be sent");

                MailService mailService=new MailService();
                HashMap mailPlaceHolder=new HashMap();
                MailServiceHelper mailServiceHelper=new MailServiceHelper();
                mailPlaceHolder.put(MailPlaceHolder.TOID, memberId);
                mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(thresholdPassingTerminalListToMerchant,"Exceeding daily same card same amount threshold"));
                // mailService.sendMail(MailEventEnum.DAILY_SAME_CARD_SAME_AMOUNT_MERCHANT_ALERT, mailPlaceHolder);
                listMapMailToSent.put(memberId, thresholdPassingTerminalListToMerchant);
            }
        }
        if(listMapMailToSent.size()>0)
        {
            //System.out.println("Mail need to be sent to admin");

            MailService mailService=new MailService();
            HashMap mailPlaceHolder=new HashMap();
            MailServiceHelper mailServiceHelper=new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(listMapMailToSent,"Exceeding daily same card same amount threshold"));
            //mailService.sendMail(MailEventEnum.DAILY_SAME_CARD_SAME_AMOUNT_ADMIN_ALERT, mailPlaceHolder);
        }
    }

    public void dailySameCardSameAmountConsequenceAlert()throws Exception
    {
        TerminalManager terminalManager=new TerminalManager();
        Map<String,List<TerminalVO>> listMap=terminalManager.getAllTerminalsGroupByMerchant();
        Map<String,List<TerminalVO>> listMapMailToSent=new HashMap();
        Set set=listMap.keySet();
        Iterator  iterator=set.iterator();
        DateManager dateManager=new DateManager();
        while (iterator.hasNext())
        {
            String memberId = (String) iterator.next();
            List<TerminalVO> terminalVOList = listMap.get(memberId);
            List<TerminalVO> thresholdPassingTerminalListToMerchant = new ArrayList();
            for (TerminalVO terminalVO : terminalVOList)
            {
                //System.out.println("terminalVO==="+terminalVO.toString());
                int sameCardSameAmountConsequenceCountActual=0;
                int sameCardSameAmountConsequenceCountThreshold=0;

                MerchantTerminalThresholdVO merchantTerminalThresholdVO=getTerminalThresholdDetails(terminalVO);
                TerminalThresholdsVO terminalThresholdsVO=merchantTerminalThresholdVO.getTerminalThresholdsVO();

                sameCardSameAmountConsequenceCountThreshold=terminalThresholdsVO.getSameCardSameAmountConsequenceThreshold();

                //DateVO dateVO=dateManager.getCurrentDayDateRange();
                DateVO dateVO=dateManager.getCurrentMonthDateRange();
                List<BinAmountVO> binAmountVOList=getSameCardSameAmountConsequenceDetail(terminalVO, dateVO);
                for(BinAmountVO binAmountVO:binAmountVOList)
                {
                    sameCardSameAmountConsequenceCountActual=binAmountVO.getCount();
                    /*System.out.println("==================");
                    System.out.println(terminalVO.toString());
                    System.out.println("sameCardSameAmountCountActual=="+sameCardSameAmountConsequenceCountActual);
                    System.out.println("sameCardSameAmountCountThreshold=="+sameCardSameAmountConsequenceCountThreshold);
                    System.out.println("bin String=="+binAmountVO.getBinString());
                    System.out.println("bin Amount=="+binAmountVO.getBinAmount());*/

                    if(sameCardSameAmountConsequenceCountActual>sameCardSameAmountConsequenceCountThreshold)
                    {
                       logger.debug("action need to be taken...");
                        thresholdPassingTerminalListToMerchant.add(terminalVO);
                    }
                    else
                    {
                        logger.debug("All okay with terminal===="+terminalVO.toString());
                    }
                }
            }
            if(thresholdPassingTerminalListToMerchant.size() > 0)
            {
                //System.out.println("Merchant mail need to be sent");

                MailService mailService=new MailService();
                HashMap mailPlaceHolder=new HashMap();
                MailServiceHelper mailServiceHelper=new MailServiceHelper();
                mailPlaceHolder.put(MailPlaceHolder.TOID, memberId);
                mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(thresholdPassingTerminalListToMerchant,"Exceeding daily same card same amount consequence threshold"));
                //mailService.sendMail(MailEventEnum.DAILY_SAME_CARD_SAME_AMOUNT_CONSEQUENCE_MERCHANT_ALERT, mailPlaceHolder);
                listMapMailToSent.put(memberId, thresholdPassingTerminalListToMerchant);
            }
        }
        if(listMapMailToSent.size()>0)
        {
            //System.out.println("Mail need to be sent to admin");

            MailService mailService=new MailService();
            HashMap mailPlaceHolder=new HashMap();
            MailServiceHelper mailServiceHelper=new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSuccessfulListOfTerminals(listMapMailToSent,"Exceeding daily same card same amount consequence threshold"));
            //mailService.sendMail(MailEventEnum.DAILY_SAME_CARD_SAME_AMOUNT_CONSEQUENCE_ADMIN_ALERT, mailPlaceHolder);
        }
    }

    public void dailySameCardConsequentlyAlert() throws Exception
    {
        TerminalManager terminalManager=new TerminalManager();
        DateManager dateManager=new DateManager();
        MerchantMonitoringDAO merchantMonitoringDAO=new MerchantMonitoringDAO();
        MerchantMonitoringManager merchantMonitoringManager=new MerchantMonitoringManager();
        Map<String, List<TerminalVO>> listMap=terminalManager.getAllTerminalsGroupByMerchant();
        List<TerminalVO> transactionVOsPassingThreshold = new ArrayList();
        //System.out.println("listMap==="+listMap);
        Set set=listMap.keySet();
        //System.out.println("set==="+set);
        Iterator iterator=set.iterator();
        while (iterator.hasNext())
        {
            String memberId=(String) iterator.next();
            List<TerminalVO> terminalVOList=listMap.get(memberId);
            //System.out.println("terminalVOList==="+terminalVOList);
            for(TerminalVO terminalVO:terminalVOList)
            {
                DateVO currentMonthDateRange=dateManager.getCurrentDayDateRange();
                Set<String> todayBinDetails=merchantMonitoringDAO.getAllCardsGroupByBins(terminalVO,currentMonthDateRange);
                //System.out.println("todayBinDetails==="+todayBinDetails);

                MerchantTerminalThresholdVO merchantTerminalThresholdVO=getTerminalThresholdDetails(terminalVO);
                TerminalThresholdsVO terminalThresholdsVO=merchantTerminalThresholdVO.getTerminalThresholdsVO();
                List<Set> sets=new ArrayList();
                int days=terminalThresholdsVO.getSameCardConsequentlyThreshold();
                //System.out.println("day==="+days);

                int i=1;
                while(days>i)
                {
                    DateVO previousDayDateRange=dateManager.getPreviousDayDateRange(i);
                    Set<String> set1=merchantMonitoringDAO.getAllCardsGroupByBins(terminalVO,previousDayDateRange);
                    //System.out.println("set1==="+set1);
                    if(set1.size()>0)
                    {
                        sets.add(set1);
                    }
                    i++;
                }

                //System.out.println("set==="+sets);
                for(String bin:todayBinDetails)
                {
                    boolean status=false;
                    //System.out.println("inside for loop");
                    for(Set set2:sets)
                    {
                        if(set2.contains(bin))
                        {
                            status=true;
                        }
                        else
                        {
                            status=false;
                            break;
                        }

                    }

                    if(status)
                    {
                        //Alert need to be send
                        MailService mailService=new MailService();
                        HashMap mailPlaceHolder=new HashMap();
                        MailServiceHelper mailServiceHelper=new MailServiceHelper();
                        mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getSameCardConsequntlyDetails(terminalVOList, "Same card same amount consequently"));
                        //mailService.sendMail(MailEventEnum.DAILY_SAME_CARD_CONSEQUENCE_ADMIN_ALERT,mailPlaceHolder);
                    }
                }

                /*DateVO lastOneDayCalendarMonthDateRange=dateManager.getLastOneDayCalendarMonthDateRange();
                Set<String> lastOneDayBinDetails=merchantMonitoringDAO.getAllCardsGroupByBins(terminalVO,lastOneDayCalendarMonthDateRange);
                System.out.println("lastOneDayBinDetails==="+lastOneDayBinDetails);

                DateVO lastTwoDayCalendarMonthDateRange=dateManager.getLastTwoDayCalendarMonthDateRange();
                Set<String> lastTwoDayBinDetails=merchantMonitoringDAO.getAllCardsGroupByBins(terminalVO,lastTwoDayCalendarMonthDateRange);
                System.out.println("lastTwoDayBinDetails==="+lastTwoDayBinDetails);*/

                /*System.out.println("s1==="+s1);
                s1.add("411111");
                s1.add("407221");
                System.out.println("s1===="+s1);
*/
                /*for(String str:s1)
                {
                    if(s2.contains(str))
                    {

                    }
                    if(s3.contains(str))
                    {

                    }
                }
*/
            }

        }


    }

    public void dailyDayCB180DayThresholdAlert() throws Exception
    {
        TerminalManager terminalManager = new TerminalManager();
        MerchantMonitoringDAO merchantMonitoringDAO = new MerchantMonitoringDAO();
        DateManager dateManager = new DateManager();
        Map<String, List<TerminalVO>> listMap = terminalManager.getAllTerminalsGroupByMerchant();
        List<TransactionVO> transactionVOsPassingThreshold = new ArrayList();
        //System.out.println("terminal===" + listMap);
        Set set = listMap.keySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext())
        {
            String memberId = (String) iterator.next();
            List<TerminalVO> terminalVOList = listMap.get(memberId);
            //System.out.println("terminalVOList==" + terminalVOList);
            for (TerminalVO terminalVO : terminalVOList)
            {
                //System.out.println("terminalVO===" + terminalVO);

                //DateVO dateVO=dateManager.getCurrentDayDateRange();
                DateVO dateVO = dateManager.getCurrentMonthDateRange();
                //DateVO dateVO=dateManager.getLastSixCalendarMonthDateRange();
                List<TransactionVO> transactionVOs = merchantMonitoringDAO.getCurrentDayChargebackTransactionDetails(terminalVO, dateVO);
                //System.out.println("transactionVOs====size" + transactionVOs.size());
                for (TransactionVO transactionVO : transactionVOs)
                {
                    /*System.out.println("trackingid=====" + transactionVO.getTrackingId());
                    System.out.println("transaction date=======" + transactionVO.getTransactionDate());*/
                    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentMonthEndDate = targetFormat.format(new Date());

                    String currentMonthStartDate = "";
                    long day = Functions.DATEDIFF(transactionVO.getTransactionDate(), currentMonthEndDate);
                    //System.out.println("date diff=====" + day);
                    if (day > 5)//value should be 180 days by default
                    {
                        transactionVOsPassingThreshold.add(transactionVO);
                    }
                }
                //listMap1.add();

            }
        }
        if (transactionVOsPassingThreshold.size() > 0)
        {
            //System.out.println("Admin mail need to be sent");

            MailService mailService=new MailService();
            HashMap mailPlaceHolder=new HashMap();
            MailServiceHelper mailServiceHelper=new MailServiceHelper();
            mailPlaceHolder.put(MailPlaceHolder.MULTIPALTRANSACTION,mailServiceHelper.getCurrentDayChargebackDetails(transactionVOsPassingThreshold, "Exceeding 180 days threshold "));
            //mailService.sendMail(MailEventEnum.DAILY_CB_EXPIRY_THRESHOLD_ADMIN_ALERT, mailPlaceHolder);
        }
    }
    public  String addNewMonitoringParameter(MonitoringParameterVO monitoringParameterVO)throws PZDBViolationException
    {
        return merchantMonitoringDAO.addNewMonitoringParameter(monitoringParameterVO);
    }
    public String monitoringParameterMapping(MonitoringParameterMappingVO monitoringParameterMappingVO) throws PZDBViolationException
    {
        return merchantMonitoringDAO.monitoringParameterMapping(monitoringParameterMappingVO);
    }
    public String monitoringParameterMappingFromPartnerAccount(MonitoringParameterMappingVO monitoringParameterMappingVO) throws PZDBViolationException
    {
        return merchantMonitoringDAO.monitoringParameterMappingFromPartnerAccount(monitoringParameterMappingVO);
    }

    public String monitoringParameterMappingLogDetails(MonitoringParameterMappingVO monitoringParameterMappingVO, String actionExecutor) throws PZDBViolationException
    {
        return merchantMonitoringDAO.monitoringParameterMappingLogDetails(monitoringParameterMappingVO, actionExecutor);
    }

    public Map<String, Map<String, List<MonitoringParameterMappingVO>>> getMonitoringParameterGroupByMerchant(MonitoringFrequency monitoringFrequency) throws PZDBViolationException
    {
        return merchantMonitoringDAO.getMonitoringParameterGroupByMerchant(monitoringFrequency);
    }
    public Map<String, List<MonitoringParameterMappingVO>> getDailyMonitoringParameterGroupByMerchantId(String memberId) throws PZDBViolationException
    {
        return merchantMonitoringDAO.getDailyMonitoringParameterGroupByMerchantId(memberId);
    }
    public boolean isMappingAvailable(String memberId, String terminalId, String parameterId)throws  PZDBViolationException
    {
        return merchantMonitoringDAO.isMappingAvailable(memberId,terminalId,parameterId);
    }
    public boolean isParameterAvailable(MonitoringParameterVO monitoringParameterVO)throws  PZDBViolationException
    {
        return merchantMonitoringDAO.isParameterAvailable(monitoringParameterVO);
    }
    public boolean isParameterNameAvailable(MonitoringParameterVO monitoringParameterVO)throws  PZDBViolationException
    {
        return merchantMonitoringDAO.isParameterNameAvailable(monitoringParameterVO);
    }
    public MonitoringParameterVO getMonitoringParameterDetails(String monitoringParaId) throws PZDBViolationException
    {
        return merchantMonitoringDAO.getMonitoringParameterDetails(monitoringParaId);
    }
    public MonitoringRuleLogVO getRuleChangeHistoryDetails(String historyId) throws PZDBViolationException
    {
        return merchantMonitoringDAO.getRuleChangeHistoryDetails(historyId);
    }
    public MonitoringRuleLogVO getRulePreviousChangeHistoryDetails(String ruleId,String historyId,String terminalId) throws PZDBViolationException
    {
        return merchantMonitoringDAO.getPreviousRuleChangeHistoryDetails(ruleId, historyId,terminalId);
    }
    public MonitoringRuleLogVO getPreviousRuleChangeHistoryDetails(String ruleId) throws PZDBViolationException
    {
        return merchantMonitoringDAO.getPreviousRuleChangeHistoryDetails(ruleId);
    }
    public Map<String, Map<String, List<MonitoringParameterMappingVO>>> getMonitoringParameterGroupByMerchantNew(MonitoringFrequency monitoringFrequency) throws PZDBViolationException
    {
        return merchantMonitoringDAO.getMonitoringParameterGroupByMerchantNew(monitoringFrequency);
    }

    public Map<String, Map<String, List<MonitoringParameterMappingVO>>> getMonitoringParameterGroupByMerchantDailyExecution() throws PZDBViolationException
    {
        return merchantMonitoringDAO.getMonitoringParameterGroupByMerchantDailyExecution();
    }

    public Map<String, Map<String, List<MonitoringParameterMappingVO>>> getMonitoringParameterGroupByMerchantWeeklyExecution() throws PZDBViolationException
    {
        return merchantMonitoringDAO.getMonitoringParameterGroupByMerchantWeeklyExecution();
    }

    public Map<String, Map<String, List<MonitoringParameterMappingVO>>> getMonitoringParameterGroupByMerchantMonthlyExecution() throws PZDBViolationException
    {
        return merchantMonitoringDAO.getMonitoringParameterGroupByMerchantMonthlyExecution();
    }

    /*public Map<String, List<MonitoringParameterMappingVO>> getDailyMonitoringParameterGroupByMerchantIdNew(String memberId) throws PZDBViolationException
    {
        return merchantMonitoringDAO.getDailyMonitoringParameterGroupByMerchantIdNew(memberId);
    }*/
    public List<MonitoringParameterMappingVO> getRiskRuleOnTerminalFromMapping(String terminalId, String memberId) throws PZDBViolationException
    {
        return merchantMonitoringDAO.getRiskRuleOnTerminalFromMapping(terminalId, memberId);
    }
    public List<MonitoringParameterMappingVO> getRiskRuleOnTerminalFromMappingForPartner(String terminalId, String memberId) throws PZDBViolationException
    {
        return merchantMonitoringDAO.getRiskRuleOnTerminalFromMappingForPartner(terminalId, memberId);
    }

    public List<MonitoringParameterMappingVO> getRiskRuleOnTerminalFromMaster() throws PZDBViolationException
    {
        return merchantMonitoringDAO.getRiskRuleOnTerminalFromMaster();
    }

    public boolean removeRiskRuleMapping(String memberId, String terminalId, String riskRuleId) throws PZDBViolationException
    {
        return merchantMonitoringDAO.removeRiskRuleMapping(memberId, terminalId, riskRuleId);
    }

    public List<TransactionVO> getTransactionListByTimestamp(TerminalVO terminalVO, DateVO dateVO, String status) throws PZDBViolationException
    {
        return merchantMonitoringDAO.getTransactionListByTimestamp(terminalVO, dateVO, status);
    }

    public List<TransactionVO> getInCompleteTransactionListByTimestamp(TerminalVO terminalVO, DateVO dateVO) throws PZDBViolationException
    {
        return merchantMonitoringDAO.getInCompleteTransactionListByTimestamp(terminalVO, dateVO);
    }

    public List<TransactionVO> getTransactionListByDTStamp(TerminalVO terminalVO, DateVO dateVO) throws PZDBViolationException
    {
        return merchantMonitoringDAO.getTransactionListByDTStamp(terminalVO, dateVO);
    }

    public Set<String> getAllCardsGroupByBins(TerminalVO terminalVO, DateVO dateVO)
    {
        return merchantMonitoringDAO.getAllCardsGroupByBins(terminalVO, dateVO);
    }
    public boolean isParameterNameAvailableBasedOnId(MonitoringParameterVO monitoringParameterVO)throws  PZDBViolationException
    {
        return merchantMonitoringDAO.isParameterNameAvailableBasedOnId(monitoringParameterVO);
    }
    public List<TransactionDetailsVO> getConsequtiveSameCardSameAmountDetail(TerminalVO terminalVO,DateVO dateVO,int threshold)
    {
     return merchantMonitoringDAO.getConsequtiveSameCardSameAmountDetail(terminalVO, dateVO, threshold);
    }

    public List<MonitoringRuleLogVO> getMonitoringRuleLogDetails(String memberId, String terminalId) throws PZDBViolationException
    {
        return merchantMonitoringDAO.getMonitoringRuleLogDetails(memberId, terminalId);
    }
    public MonitoringParameterMappingVO getMonitoringParameterDetailsFromMapping(String monitoringParaId,String memberId,String terminalId)throws PZDBViolationException
    {
        return merchantMonitoringDAO.getMonitoringParameterDetailsFromMapping(monitoringParaId,memberId,terminalId);
    }
    public MonitoringParameterMappingVO getMonitoringParameterFromMaster(String monitoringParaId)throws PZDBViolationException
    {
       return merchantMonitoringDAO.getMonitoringParameterFromMaster(monitoringParaId);
    }
    public List<TransactionDetailsVO> getListHighRiskAmountRejectedTransaction(TerminalVO terminalVO,DateVO dateVO,double threshold, String rejectReason)
    {
        return merchantMonitoringDAO.getListHighRiskAmountRejectedTransaction(terminalVO,dateVO,threshold,rejectReason);
    }
   public int logRuleDetails(String ruleName,String ruleMessage,String actualRatio,String alertThreshold,String suspensionThreshold,String monitroingStartDate,String moniotringEndDate,String alertDate,String terminalId,String alertId)throws Exception
   {
       return merchantMonitoringDAO.logRuleDetails(ruleName,ruleMessage,actualRatio,alertThreshold,suspensionThreshold,monitroingStartDate,moniotringEndDate,alertDate,terminalId,alertId);
   }
    public int logAlert(String alertType,String alertTeam,String memberId,String terminalId,String report)throws Exception
    {
        return merchantMonitoringDAO.logAlert(alertType, alertTeam,memberId,terminalId,report);
    }
    public MonitoringAlertDetailVO getAlertDetail(String alertId) throws SystemError
    {
        return merchantMonitoringDAO.getAlertDetail(alertId);
    }
    public List<MonitoringAlertDetailVO> getRuleLogDetail(String alertId)
    {
        return merchantMonitoringDAO.getRuleLogDetail(alertId);
    }
    public MonitoringAlertDetailVO getLogAlertDetail(String alertId)
    {
        return merchantMonitoringDAO.getLogAlertDetail(alertId);
    }
    public Map<String,List<MonitoringAlertDetailVO>> getRuleLogDetailPerTerminal(String alertId)
    {
        return merchantMonitoringDAO.getRuleLogDetailPerTerminal(alertId);
    }
    public Map<String,List<MonitoringAlertDetailVO>> getRuleLogDetailPerTerminalForResend(String alertId)
    {
        return merchantMonitoringDAO.getRuleLogDetailPerTerminalForResend(alertId);
    }
}