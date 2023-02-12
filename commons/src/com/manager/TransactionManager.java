package com.manager;


import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayType;
import com.manager.dao.TransactionDAO;
import com.manager.enums.StatusChartsType;
import com.manager.utils.AccountUtil;
import com.manager.vo.*;
import com.manager.vo.merchantmonitoring.DateVO;
import com.manager.vo.morrisBarVOs.Data;
import com.manager.vo.morrisBarVOs.MorrisBarChartVO;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.validators.vo.CommonValidatorVO;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 8/18/14
 * Time: 12:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class TransactionManager
{
    Logger logger = new Logger(TransactionManager.class.getName());

    //transaction Dao initialization
    TransactionDAO transactionDAO= new TransactionDAO();
    TerminalManager terminalManager = new TerminalManager();
    AccountUtil accountUtil= new AccountUtil();

    public TransactionReportVO getMerchantTransactionReportAndCharts(InputDateVO inputDateVO,TerminalVO terminalVO,HttpSession session) throws PZDBViolationException, PZTechnicalViolationException
    {

        String sessionColor=(String)session.getAttribute("colorPallet");

        String[] colorPallet=sessionColor.split(",");

        for (String color : colorPallet)
        {
            logger.debug("Session Color Dashboard.java.........."+color);
        }


        //FileManager instanint salesCount;tization
        FileManager fileManager = new FileManager();
        //TransactionReportsVO initialization
        TransactionReportVO transactionReportVO = new TransactionReportVO();
        //ColorPalletVO colorPalletVO = new ColorPalletVO();
        //map initialization
        LinkedHashMap<LinkedHashMap<String,String>,String> monthAndStatus = new LinkedHashMap<LinkedHashMap<String,String>,String>();
        //HashMap<String,StringBuilder> fileContent=new HashMap<String, StringBuilder>();
        LinkedHashMap<String,MorrisBarChartVO> statusViseBarChartVOHashMap=new LinkedHashMap<String, MorrisBarChartVO>();
        Set<String> sTable = new HashSet<String>();
        //This is to handle duplication of data according to the different status for same month of the year
        ArrayList<String> salesDetailAddedForMonthAndYear = new ArrayList<String>();

        //List<Data> morrisListData = new ArrayList<Data>();
        //calculation sTable
        long totalCount=0;
        double totalAmount=0.0;
        int count = 1;

        HashMap<String,String> gateWayHash=terminalManager.getGateWayHash(terminalVO);
        sTable=Database.getTableSet(gateWayHash.keySet());
        logger.debug(" account id not provided " );

        //creating chart
        LinkedHashMap<String,TransactionVO> transactiontVOMonthAndStatus=transactionDAO.getMerchantTransactionReports(inputDateVO,terminalVO,sTable,monthAndStatus);
        logger.debug("after Query the calculation part");
        logger.debug("month And status"+monthAndStatus);

        for(Map.Entry<LinkedHashMap<String,String>,String> outerMonthAndStatusPair: monthAndStatus.entrySet())
        {
            LinkedHashMap<String,String> innerMonthAndStatusPair =outerMonthAndStatusPair.getKey();
            for(Map.Entry<String,String> monthAndStatusPair:innerMonthAndStatusPair.entrySet())
            {
                String monthAndYear = monthAndStatusPair.getKey();
                String status = monthAndStatusPair.getValue();
                TransactionVO transactionVO=transactiontVOMonthAndStatus.get(monthAndYear+","+status);
                transactionReportVO.setTransactionVOHashMap(status,transactionVO);
                totalAmount=totalAmount+Double.valueOf(transactionVO.getAmount());
                totalCount=totalCount+transactionVO.getCount();
                setFileContentForCharts(transactionVO, colorPallet, inputDateVO, monthAndYear, transactiontVOMonthAndStatus, salesDetailAddedForMonthAndYear, statusViseBarChartVOHashMap);
                count++;
            }
        }

        transactionReportVO.setTotalAmount(Double.valueOf(accountUtil.convert2Decimal(String.valueOf(totalAmount))));
        transactionReportVO.setTotalCount(totalCount);
        transactionReportVO.setInputDateVO(inputDateVO);
        transactionReportVO.setTerminalVO(terminalVO);
        fileManager.createStatusChartXML(statusViseBarChartVOHashMap,inputDateVO,terminalVO,transactionReportVO);


        return transactionReportVO;
    }


    private void setFileContentForCharts(TransactionVO transactionVO,String []colorPallet,InputDateVO inputDateVO,String monthAndYear,LinkedHashMap<String,TransactionVO> transactiontVOMonthAndStatus,ArrayList<String> salesDetailAddedForMonthAndYear,LinkedHashMap<String,MorrisBarChartVO> statusViseBarChartVOMap)
    {
        //colorPalletVO
        logger.debug("inside setFileContentForCharts");
        StringBuilder content=null;

        if((transactionVO.getStatus()).equalsIgnoreCase(PZTransactionStatus.CAPTURE_SUCCESS.toString()) || (transactionVO.getStatus()).equalsIgnoreCase(PZTransactionStatus.SETTLED.toString()) ||(transactionVO.getStatus()).equalsIgnoreCase(PZTransactionStatus.AUTH_SUCCESS.toString())||(transactionVO.getStatus()).equals(PZTransactionStatus.MARKED_FOR_REVERSAL))
        {
            if(!salesDetailAddedForMonthAndYear.contains(monthAndYear))
            {
                //BarChartVO salesChart=null;
                MorrisBarChartVO morrisSalesChart=null;

                salesDetailAddedForMonthAndYear.add(monthAndYear);
                logger.debug("monthYear::"+monthAndYear+" currentStatus::"+transactionVO.getStatus());
                double totalCaptureAmountForStatus=transactiontVOMonthAndStatus.get(monthAndYear+","+transactionVO.getStatus())==null ?0.00 :transactiontVOMonthAndStatus.get(monthAndYear+","+transactionVO.getStatus()).getCaptureAmount() ;
                if(!transactionVO.getStatus().equalsIgnoreCase(PZTransactionStatus.CAPTURE_SUCCESS.toString()) && transactiontVOMonthAndStatus.get(monthAndYear+","+PZTransactionStatus.CAPTURE_SUCCESS.toString())!=null)
                {
                    totalCaptureAmountForStatus += transactiontVOMonthAndStatus.get(monthAndYear+","+PZTransactionStatus.CAPTURE_SUCCESS.toString()).getCaptureAmount() ;
                    logger.debug("capture amount for capturesuccess::"+transactiontVOMonthAndStatus.get(monthAndYear+","+PZTransactionStatus.CAPTURE_SUCCESS.toString()).getCaptureAmount());
                }
                if(!transactionVO.getStatus().equalsIgnoreCase(PZTransactionStatus.SETTLED.toString()) && transactiontVOMonthAndStatus.get(monthAndYear+","+PZTransactionStatus.SETTLED.toString())!=null)
                {
                    totalCaptureAmountForStatus += transactiontVOMonthAndStatus.get(monthAndYear+","+PZTransactionStatus.SETTLED.toString()).getCaptureAmount() ;
                    logger.debug("capture amount for settled::"+transactiontVOMonthAndStatus.get(monthAndYear+","+PZTransactionStatus.SETTLED.toString()).getCaptureAmount());
                }
                if(!transactionVO.getStatus().equalsIgnoreCase("authsuccessful") && transactiontVOMonthAndStatus.get(monthAndYear+",authsuccessful")!=null) //authSuccessful status is not found under PZTransactionStatus
                {
                    totalCaptureAmountForStatus += transactiontVOMonthAndStatus.get(monthAndYear+",authsuccessful").getCaptureAmount() ;
                    logger.debug("capture amount for authsuccessful::"+transactiontVOMonthAndStatus.get(monthAndYear+",authsuccessful").getCaptureAmount());
                }
                if(!transactionVO.getStatus().equalsIgnoreCase("markedforreversal") && transactiontVOMonthAndStatus.get(monthAndYear+",markedforreversal")!=null) //authSuccessful status is not found under PZTransactionStatus
                {
                    totalCaptureAmountForStatus += transactiontVOMonthAndStatus.get(monthAndYear+",markedforreversal").getCaptureAmount() ;
                    logger.debug("capture amount for markedforreversal::"+transactiontVOMonthAndStatus.get(monthAndYear+",markedforreversal").getCaptureAmount());
                }

                logger.debug("total::"+totalCaptureAmountForStatus);
                if(statusViseBarChartVOMap.containsKey(StatusChartsType.SALES.toString()))
                {
                    morrisSalesChart = statusViseBarChartVOMap.get(StatusChartsType.SALES.toString());
                    //morrisListDatas = new ArrayList<Data>();
                    //String color=colorPalletVO.getSalesColor();

                    List<Data> morrisDatas = morrisSalesChart.getData();
                    Data morrisData = new Data();

                    //List<Double> monthData = (List) morrisData.getMonth();
                    morrisData.setAmount(Double.valueOf(Functions.convert2Decimal(totalCaptureAmountForStatus)));
                    morrisData.setMonth(monthAndYear);

                    morrisDatas.add(morrisData);
                    morrisSalesChart.setData(morrisDatas);

                    List<String> graphColor = morrisSalesChart.getBarColors();
                    if(!graphColor.equals(null))
                    {
                        for (String s : colorPallet)
                        {
                            graphColor.add(s);
                        }
                    }

                    logger.debug("sale---"+graphColor+"--"+graphColor.size());

                }
                else
                {
                    morrisSalesChart = new MorrisBarChartVO();

                    //String color=colorPalletVO.getSalesColor();

                    List<Data> data= new ArrayList<Data>();

                    Data dataset = new Data();

                    dataset.setMonth(monthAndYear);
                    dataset.setAmount(Double.valueOf(Functions.convert2Decimal(totalCaptureAmountForStatus)));

                    data.add(dataset);

                    List<String> backgroundColor= new ArrayList<String>();
                    for(String s : colorPallet)
                    {
                        backgroundColor.add(s);
                    }

                    logger.debug("sale else---" + backgroundColor + "--" + backgroundColor.size());

                    String[] amount = {"amount"};

                    morrisSalesChart.setData(data);
                    morrisSalesChart.setElement("bar-example");
                    morrisSalesChart.setXkey("month");
                    morrisSalesChart.setYkeys(amount);
                    morrisSalesChart.setLabels(amount);
                    morrisSalesChart.setBarColors(backgroundColor);
                }
                statusViseBarChartVOMap.put(StatusChartsType.SALES.toString(), morrisSalesChart);

            }
        }


        if((transactionVO.getStatus()).equalsIgnoreCase(PZTransactionStatus.REVERSED.toString()) )
        {
            //BarChartVO refundChart= null;
            MorrisBarChartVO morrisRefundChart= null;

            if(statusViseBarChartVOMap.containsKey(StatusChartsType.REFUND.toString()))
            {
                morrisRefundChart = statusViseBarChartVOMap.get(StatusChartsType.REFUND.toString());

                //String color=colorPallet.toString();

                List<Data> morrisDatas = morrisRefundChart.getData();
                Data morrisData = new Data();

                morrisData.setAmount(Double.valueOf(Functions.convert2Decimal(transactionVO.getCaptureAmount())));
                morrisData.setMonth(monthAndYear);

                morrisDatas.add(morrisData);
                morrisRefundChart.setData(morrisDatas);

                List<String> graphColor = morrisRefundChart.getBarColors();
                //graphColor.add(color);

                colorPallet = Arrays.copyOfRange(colorPallet,2,colorPallet.length);

                if(!graphColor.equals(null))
                {
                    for (String s : colorPallet)
                    {
                        graphColor.add(s);

                    }
                    logger.debug("refund----" + graphColor + "--" + graphColor.size());
                }

            }
            else
            {
                morrisRefundChart = new MorrisBarChartVO();

                //String color=colorPallet.toString();

                List<Data> data = new ArrayList<Data>();

                Data dataset = new Data();

                dataset.setMonth(monthAndYear);
                dataset.setAmount(Double.valueOf(Functions.convert2Decimal(transactionVO.getCaptureAmount())));

                data.add(dataset);

                List<String> backgroundColor= new ArrayList<String>();
                colorPallet = Arrays.copyOfRange(colorPallet,2,colorPallet.length);
                for(String s : colorPallet)
                {
                    backgroundColor.add(s);

                }
                logger.debug("refund else----"+backgroundColor+"--"+backgroundColor.size());

                //backgroundColor.add(color);

                String[] amount = {"amount"};

                morrisRefundChart.setData(data);
                morrisRefundChart.setElement("refund");
                morrisRefundChart.setXkey("month");
                morrisRefundChart.setYkeys(amount);
                morrisRefundChart.setLabels(amount);
                morrisRefundChart.setBarColors(backgroundColor);
            }

            statusViseBarChartVOMap.put(StatusChartsType.REFUND.toString(), morrisRefundChart);
        }


        if((transactionVO.getStatus()).equalsIgnoreCase(PZTransactionStatus.CHARGEBACK.toString()))
        {
            //BarChartVO chargeBackChart= null;
            MorrisBarChartVO morrisChargeBackChart= null;

            if(statusViseBarChartVOMap.containsKey(StatusChartsType.CHARGEBACK.toString()))
            {
                morrisChargeBackChart = statusViseBarChartVOMap.get(StatusChartsType.CHARGEBACK.toString());

                //String color=colorPallet.toString();

                List<Data> morrisDatas = morrisChargeBackChart.getData();
                Data morrisData = new Data();

                morrisData.setAmount(Double.valueOf(Functions.convert2Decimal(transactionVO.getCaptureAmount())));
                morrisData.setMonth(monthAndYear);

                morrisDatas.add(morrisData);
                morrisChargeBackChart.setData(morrisDatas);

                List<String> graphColor = morrisChargeBackChart.getBarColors();
                //graphColor.add(color);
                colorPallet = Arrays.copyOfRange(colorPallet,3,colorPallet.length);
                if(!graphColor.equals(null))
                {
                    for (String s : colorPallet)
                    {
                        graphColor.add(s);

                    }
                    logger.debug("Chargeback----" + graphColor + "--" + graphColor.size());
                }
            }
            else
            {
                morrisChargeBackChart = new MorrisBarChartVO();

                //String color=colorPallet.

                List<Data> data = new ArrayList<Data>();

                Data dataset = new Data();

                dataset.setMonth(monthAndYear);
                dataset.setAmount(Double.valueOf(Functions.convert2Decimal(transactionVO.getCaptureAmount())));

                data.add(dataset);

                List<String> backgroundColor = new ArrayList<String>();
                //backgroundColor.add(color);
                colorPallet = Arrays.copyOfRange(colorPallet,3,colorPallet.length);
                for(String s : colorPallet)
                {
                    backgroundColor.add(s);

                }
                logger.debug("Chargeback else----"+backgroundColor+"--"+backgroundColor.size());

                String[] amount = {"amount"};

                morrisChargeBackChart.setData(data);
                morrisChargeBackChart.setElement("chargeback");
                morrisChargeBackChart.setXkey("month");
                morrisChargeBackChart.setYkeys(amount);
                morrisChargeBackChart.setLabels(amount);
                morrisChargeBackChart.setBarColors(backgroundColor);

            }
            statusViseBarChartVOMap.put(StatusChartsType.CHARGEBACK.toString(), morrisChargeBackChart);
        }

    }

    public TransactionDetailsVO getTransDetailFromCommon(String trackingId)
    {
        return transactionDAO.getDetailFromCommon(trackingId);
    }
    public TransactionDetailsVO getTransDetailsFromCommonByPaymentId(String paymentid)
    {
        return transactionDAO.getTransDetailsFromCommonBypaymentid(paymentid);
    }
    public void getCommonTransactionDetails(TransactionDetailsVO transactionDetailsVO,String trackingId,String description)
    {
        transactionDAO.getCommonTransactionDetails(transactionDetailsVO,trackingId,description);
    }

    public List<TransactionDetailsVO> getCommonTransactionDetailsAPI(TransactionDetailsVO transactionDetailsVO,String trackingId,String description)
    {
        return transactionDAO.getCommonTransactionDetails(transactionDetailsVO,trackingId,description);
    }

    public List<TransactionDetailsVO> getTransactionforStatusAPI(String tableName,String trackingId, String orderId, String memberId)
    {
        return transactionDAO.getTransactionforStatusAPI(tableName, trackingId, orderId, memberId);
    }

    public TransactionDetailsVO getTransactionQwipiDetails(String trackingId,String description)
    {
        return transactionDAO.getTransactionQwipiDetails(trackingId,description);
    }
    public TransactionDetailsVO getTransactionEcoreDetails(String trackingId,String description)
    {
        return transactionDAO.getTransactionEcoreDetails(trackingId, description);
    }
    public List<TransactionDetailsVO> getAllStuckTransactionList(String status) throws PZDBViolationException
    {
        return transactionDAO.getAllStuckTransactionList(status);
    }
    public CommonValidatorVO getDetailFromTransCommon(CommonValidatorVO commonValidatorVO,TokenDetailsVO tokenDetailsVO)
    {
        return transactionDAO.getDetailFromTransCommon(commonValidatorVO, tokenDetailsVO);
    }
    public List<TransactionDetailsVO> getListOfBinTransaction(TerminalVO terminalVO, DateVO dateVO, String binStr,String lastFour)
    {
        return transactionDAO.getListOfBinTransaction(terminalVO, dateVO, binStr, lastFour);
    }
    public List<TransactionDetailsVO> getListHighRiskAmountTransaction(TerminalVO terminalVO, DateVO dateVO, String amount)
    {
        return transactionDAO.getListHighRiskAmountTransaction(terminalVO, dateVO, amount);
    }
    public List<TransactionDetailsVO> getListPreAuthTransactionList(TerminalVO terminalVO,DateVO dateVO)
    {
        return transactionDAO.getListPreAuthTransactionList(terminalVO, dateVO);
    }
    public List<TransactionDetailsVO> getTransactionListByRejectReason(TerminalVO terminalVO, DateVO dateVO,String rejectReason)
    {
        return transactionDAO.getTransactionListByRejectReason(terminalVO, dateVO, rejectReason);
    }
    public TransactionSummaryVO getGatewayAccountProcessingDetails(GatewayAccount gatewayAccount, String tableName) throws PZDBViolationException
    {
        return transactionDAO.getGatewayAccountProcessingDetails(gatewayAccount, tableName);
    }
    public TransactionSummaryVO getGatewayAccountProcessingDetails(GatewayAccount gatewayAccount,DateVO dateVO,String tableName) throws PZDBViolationException
    {
        return transactionDAO.getGatewayAccountProcessingDetails(gatewayAccount,dateVO,tableName);
    }
    public long getRetrivalRequestOnGatewayAccount(GatewayAccount gatewayAccount, String tableName,DateVO dateVO)
    {
        return transactionDAO.getRetrivalRequestOnGatewayAccount(gatewayAccount, tableName, dateVO);
    }
    public String getMemberFirstTransactionDateOnGatewayAccount(GatewayAccount gatewayAccount)throws PZDBViolationException
    {
        return transactionDAO.getMemberFirstTransactionDateOnGatewayAccount(gatewayAccount);
    }
    public TransactionSummaryVO getGatewayTypeProcessingDetails(GatewayType gatewayType, DateVO dateVO, String tableName) throws PZDBViolationException
    {
        return transactionDAO.getGatewayTypeProcessingDetails(gatewayType, dateVO, tableName);
    }
    public String getPartnersFirstTransactionOnGatewayType(GatewayType gatewayType, String partnerName) throws PZDBViolationException
    {
        return transactionDAO.getPartnersFirstTransactionOnGatewayType(gatewayType, partnerName);
    }

    public String getPartnersFirstTransactionDate(String partnerName) throws PZDBViolationException
    {
        return transactionDAO.getPartnersFirstTransactionDate(partnerName);
    }

    public TransactionDetailsVO getTransDetailsFromCommon(String orderId, String memberId)
    {
        return transactionDAO.getTransDetailsFromCommon(orderId, memberId);
    }

    public Hashtable getTransactionListForCommonInquiry(TransactionVO transactionVO, String fdtstamp, String tdtstamp, String start, String end)
    {
        return transactionDAO.getTransactionListForCommonInquiry(transactionVO, fdtstamp, tdtstamp, start, end);
    }

    public Hashtable getTransactionListForCommonActionHistory(TransactionVO transactionVO, String start, String end)
    {
        return transactionDAO.getTransactionListForCommonActionHistory(transactionVO, start, end);
    }

    public TransactionDetailsVO getTransactionDetailsToProcessCommonSettlement(String trackingId,String tablename) throws PZDBViolationException
    {
        return transactionDAO.getTransactionDetailsToProcessCommonSettlement(trackingId,tablename);
    }

    public Hashtable getTransactionList(String trackingid, String name, String desc, String orderdesc, String tdtstamp, String fdtstamp, String status, int records, int pageno, String perfectmatch,Set<String> gatewayTypeSet)
    {
        return transactionDAO.getTransactionList(trackingid, name, desc, orderdesc, tdtstamp, fdtstamp, status, records, pageno, perfectmatch, gatewayTypeSet);
    }

    public Hashtable listTransactions(String toid, String trackingid, String name, String desc, String orderdesc, String amount, String refundamount, String firstfourofccnum, String lastfourofccnum, String emailaddr, String tdtstamp, String fdtstamp, String status, int records, int pageno, String perfectmatch, boolean archive, Set<String> gatewayTypeSet, String remark, String cardtype, String issuing_bank, String statusFlag, String gateway_name, String currency, String accountid,String paymentid, String dateType,String arn , String rrn, String auth,String customerId,String transactionMode, String telno,String telnocc,String totype, String bankaccount)
    {
        return transactionDAO.listTransactions(toid, trackingid, name, desc, orderdesc, amount, refundamount, firstfourofccnum, lastfourofccnum, emailaddr, tdtstamp, fdtstamp, status, records, pageno, perfectmatch, archive, gatewayTypeSet, remark, cardtype, issuing_bank, statusFlag, gateway_name, currency, accountid, paymentid, dateType,arn,rrn,auth,customerId,transactionMode,telno,telnocc,totype,bankaccount);
    }
    public Hashtable listTransactionsBasedOnDetailStatus(String toid, String trackingid, String name, String desc, String orderdesc, String amount, String refundamount, String firstfourofccnum, String lastfourofccnum, String emailaddr, String tdtstamp, String fdtstamp, String status, int records, int pageno, String perfectmatch, boolean archive, Set<String> gatewayTypeSet, String remark, String cardtype, String issuing_bank, String statusFlag, String gateway_name, String currency, String accountid,String paymentid, String dateType,String arn , String rrn, String auth,String detailStatus,String customerId,String transactionMode, String telno,String telnocc,String totype)
    {
        return transactionDAO.listTransactionsBasedOnDetailStatus(toid, trackingid, name, desc, orderdesc, amount, refundamount, firstfourofccnum, lastfourofccnum, emailaddr, tdtstamp, fdtstamp, status, records, pageno, perfectmatch, archive, gatewayTypeSet, remark, cardtype, issuing_bank, statusFlag, gateway_name, currency, accountid, paymentid, dateType,arn,rrn,auth,detailStatus,customerId,transactionMode,telno,telnocc,totype);
    }

    public Hashtable fraudTransactionList(String toid, String mid,String trackingid,String terminalId,String transactionType, String amount, String refundamount, String firstfourofccnum, String lastfourofccnum, String tdtstamp, String fdtstamp, int records, int pageno,String currency, String accountid,String paymentid, String dateType,String isRefund,String rrn,String authCode)
    {
        return transactionDAO.fraudTransactionList(toid,mid, trackingid,terminalId,transactionType,amount, refundamount, firstfourofccnum, lastfourofccnum, tdtstamp, fdtstamp,records, pageno, currency, accountid, paymentid, dateType,isRefund,rrn,authCode);
    }

    public Hashtable listTransactionsFraudAlertExportInExcel(String toid, String mid,String trackingid,String terminalId,String transactionType, String amount, String refundamount, String firstfourofccnum, String lastfourofccnum, String tdtstamp, String fdtstamp,String currency, String accountid,String paymentid, String dateType,String isRefund,String rrn,String authCode)
    {
        return transactionDAO.listTransactionsFraudAlertExportInExcel(toid,mid, trackingid,terminalId,transactionType,amount, refundamount, firstfourofccnum, lastfourofccnum, tdtstamp, fdtstamp,currency, accountid, paymentid, dateType,isRefund,rrn,authCode);
    }

    public Hashtable cardPresentlistTransactions(String toid, String trackingid, String name, String desc, String orderdesc, String amount, String refundamount, String firstfourofccnum, String lastfourofccnum, String emailaddr, String tdtstamp, String fdtstamp, String status, int records, int pageno, String perfectmatch, boolean archive, Set<String> gatewayTypeSet, String remark, String cardtype, String issuing_bank, String statusFlag, String gateway_name, String currency, String accountid,String paymentid, String dateType, String customerId, String telno,String telnocc,String totype)
    {
        return transactionDAO.cardPresentlistTransactions(toid, trackingid, name, desc, orderdesc, amount, refundamount, firstfourofccnum, lastfourofccnum, emailaddr, tdtstamp, fdtstamp, status, records, pageno, perfectmatch, archive, gatewayTypeSet, remark, cardtype, issuing_bank, statusFlag, gateway_name, currency, accountid, paymentid, dateType,customerId,telno,telnocc,totype);
    }

    public Hashtable getTransactionDetails(String icicitransId, boolean archive,String accountid)
    {
        return transactionDAO.getTransactionDetails(icicitransId, archive, accountid);
    }

    public Hashtable getCardPresentTransactionDetails(String icicitransId, boolean archive,String accountid)
    {
        return transactionDAO.getCardPresentTransactionDetails(icicitransId, archive, accountid);
    }

    public Hashtable listTransactionsForExportInExcel(String toid, String pgTypeId, String trackingid, String name, String desc, String orderdesc, String amount, String firstfourofccnum, String lastfourofccnum, String emailaddr, String tdtstamp, String fdtstamp, String status, String remark,String paymentId, String perfectmatch, boolean archive, Set<String> gatewayTypeSet, String gateway_name, String accountid, String dateType, String currency, String cardtype, String issuing_bank,String arn , String rrn, String auth,String transactionMode, String customerid, String statusflag, String telno, String telnocc,String totype,String bankname, String bankaccount,String ifsc,String fromid) throws PZDBViolationException
    {
        return transactionDAO.listTransactionsForExportInExcel(toid, pgTypeId, trackingid, name, desc, orderdesc, amount, firstfourofccnum, lastfourofccnum, emailaddr, tdtstamp, fdtstamp, status, remark,paymentId, perfectmatch, archive, gatewayTypeSet, gateway_name, accountid, dateType, currency, cardtype, issuing_bank,arn,rrn,auth,transactionMode,customerid,statusflag,telno,telnocc,totype,bankname,bankaccount,ifsc,fromid);
    }

    public Hashtable listTransactionsForExportInExcelForDetailStatus(String toid, String pgTypeId, String trackingid, String name, String desc,String orderdesc, String amount, String firstfourofccnum, String lastfourofccnum,String emailaddr, String tdtstamp, String fdtstamp, String status,String remark,String paymentId, String perfectmatch, boolean archive, Set<String> gatewayTypeSet, String gateway_name, String accountid, String dateType, String currency, String cardtype, String issuing_bank,String arn , String rrn, String auth,String detailStatus,String transactionMode, String customerid, String statusflag,String telno,String telnocc,String totype,String fromid) throws PZDBViolationException
    {
        return transactionDAO.listTransactionsForExportInExcelForDetailStatus(toid, pgTypeId, trackingid, name, desc, orderdesc, amount, firstfourofccnum, lastfourofccnum, emailaddr, tdtstamp, fdtstamp, status, remark,paymentId, perfectmatch, archive, gatewayTypeSet, gateway_name, accountid, dateType, currency, cardtype, issuing_bank,arn,rrn,auth,detailStatus,transactionMode, customerid,statusflag,telno,telnocc,totype,fromid);
    }

    public Hashtable listCPTransactionsForExportInExcel(String toid, String pgTypeId, String trackingid, String name, String desc, String orderdesc,String amount, String firstfourofccnum, String lastfourofccnum, String emailaddr, String tdtstamp,String fdtstamp, String status, String remark,String paymentId, String perfectmatch,boolean archive, Set<String> gatewayTypeSet, String gateway_name, String accountid,String dateType, String currency, String cardtype, String issuing_bank, String customerid,String fromid) throws PZDBViolationException
    {
        return transactionDAO.listCPTransactionsForExportInExcel(toid, pgTypeId, trackingid, name, desc, orderdesc, amount, firstfourofccnum, lastfourofccnum, emailaddr, tdtstamp, fdtstamp, status, remark,paymentId, perfectmatch, archive, gatewayTypeSet, gateway_name, accountid, dateType, currency, cardtype, issuing_bank, customerid,fromid);
    }

    public Hashtable getListOfTransactionForCompliance(String gateway, String accountid, String currency, String firstsix, String lastfour, int start, int end) throws PZDBViolationException
    {
        return transactionDAO.getListOfTransactionForCompliance(gateway, accountid, currency, firstsix, lastfour, start, end);
    }

    public Hashtable listTransaction(String accountid, String gateway, String currency, String fromDate, String toDate, String startTime, String endTime, String view, int pageno, int records) throws PZDBViolationException
    {
        return transactionDAO.listTransaction(accountid, gateway, currency, fromDate, toDate, startTime, endTime, view, pageno, records);
    }
    public List<String> getListOfCurrencies(String toid)
    {
        return transactionDAO.getListOfCurrencies(toid);
    }
    public List<String> getListOfCurrenciesForSubMerchant(String toid,String login)
    {
        return transactionDAO.getListOfCurrenciesForSubMerchant(toid, login);
    }

    public TransactionDetailsVO getCommonTransactionDetailsForChargeBack(String trackingId) throws PZDBViolationException
    {
        return transactionDAO.getCommonTransactionDetailsForChargeBack(trackingId);
    }

    public TransactionDetailsVO getBorgunCommonTransactionDetailsForChargeBack(String RRN) throws PZDBViolationException
    {
        return transactionDAO.getBorgunCommonTransactionDetailsForChargeBack(RRN);
    }

    public TransactionDetailsVO getCommonTransactionDetailsForChargeBackByBankId(String paymentId) throws PZDBViolationException
    {
        return transactionDAO.getCommonTransactionDetailsForChargeBackByBankId(paymentId);
    }

    public Hashtable listTransactions(String toid,String startDate, String endDate, String status, int records, int pageno,Set<String> gatewayTypeSet,String payModeId,String cardTypeId,String accountId) throws SystemError
    {
        return transactionDAO.listTransactions(toid, startDate, endDate, status, records, pageno, gatewayTypeSet, payModeId, cardTypeId, accountId);
    }

    public Hashtable getReport(String toid, String terminalId, String startDate, String endDate,Set<String> gatewayTypeSet,String payModeId,String cardTypeId,String accountId) throws SystemError
    {
        return transactionDAO.getReport(toid, terminalId, startDate, endDate, gatewayTypeSet, payModeId, cardTypeId, accountId);
    }

    public Hashtable getRefundChargebackReport(String toid, String startDate, String endDate,Set<String> gatewayTypeSet,String payModeId,String cardTypeId,String accountId) throws SystemError
    {
        return transactionDAO.getRefundChargebackReport(toid, startDate, endDate, gatewayTypeSet, payModeId, cardTypeId, accountId);
    }

    public List<TransactionVO> getTransactionDetail(TransactionVO transactionVO,CommonValidatorVO commonValidatorVO, List<TransactionVO> transDetailVOList,String fdtstamp,String tdtstamp) throws PZDBViolationException
    {
        return transactionDAO.transactionDetailsForAPI(transactionVO, commonValidatorVO, transDetailVOList, fdtstamp, tdtstamp);
    }
    public boolean checkPendingTransaction(GatewayAccount gatewayAccount,DateVO dateVO)throws PZDBViolationException{
        return transactionDAO.checkPendingTransaction(gatewayAccount,dateVO);
    }
    public boolean checkPendingTransactionOfMerchant(GatewayAccount gatewayAccount,String memberId,String terminalId,DateVO dateVO)throws PZDBViolationException{
        return transactionDAO.checkPendingTransactionOfMerchant(gatewayAccount,memberId,terminalId,dateVO);
    }
    public Set<String> getAllGatewaysAssociatedWithMerchant(String memberid) throws PZDBViolationException
    {
        return transactionDAO.getAllGatewaysAssociatedWithMerchant(memberid);
    }

    public Map<String, List<TransactionVO>> getGatewayWiseMerchantTransactionStatus(TransactionVO transactionVO, Set<String> gatewaySet) throws PZDBViolationException
    {
        return transactionDAO.getGatewayWiseMerchantTransactionStatus(transactionVO, gatewaySet);
    }

    public Map<String, List<TransactionVO>> getBankTransactionStatus(TransactionVO transactionVO, Set<String> gatewaySet) throws PZDBViolationException
    {
        return transactionDAO.getBankTransactionStatus(transactionVO, gatewaySet);
    }
    public List<TransactionVO> getAccountWiseTransactionDetails(TransactionVO transactionVO) throws PZDBViolationException
    {
        return transactionDAO.getAccountWiseTransactionDetails(transactionVO);
    }

    public List<TransactionVO> getSalesReportMap(TransactionVO transactionVO,HashMap<String,TransactionVO> getListOfMemberId) throws PZDBViolationException
    {
        return transactionDAO.getSalesReportMap(transactionVO,getListOfMemberId);
    }

    public Map<String, List<TransactionVO>> getGatewayWiseMerchantRefundStatus(TransactionVO transactionVO, Set<String> gatewaySet) throws PZDBViolationException
    {
        return transactionDAO.getGatewayWiseMerchantRefundStatus(transactionVO, gatewaySet);
    }
    public Map<String, List<TransactionVO>> getBankTransactionRefundStatus(TransactionVO transactionVO, Set<String> gatewaySet) throws PZDBViolationException
    {
        return transactionDAO.getBankTransactionRefundStatus(transactionVO, gatewaySet);
    }

    public Set<String> getAllGatewaysAssociatedWithPartner(String partnerid) throws PZDBViolationException
    {
        return transactionDAO.getAllGatewaysAssociatedWithPartner(partnerid);
    }
    public void getCommonTransactionDetailsNew(TransactionDetailsVO transactionDetailsVO,String trackingId,String description)
    {
        transactionDAO.getCommonTransactionDetailsNew(transactionDetailsVO,trackingId,description);
    }
    public void addCustomerName(String customerName,Set<String> cardNum, Set<String> emailAddr,Set<String> phone)throws PZDBViolationException
    {
        transactionDAO.addCustomerName(customerName,cardNum,emailAddr,phone);
    }
    public List<TransactionDetailsVO> getTransactionDetails(List<String> trackingId)throws PZDBViolationException
    {
        return transactionDAO.getTransactionDetails(trackingId);
    }
    public List<TransactionDetailsVO> getTransactionIdFromBin(String firstSix,String lastFour,String name)throws PZDBViolationException
    {
        return transactionDAO.getTransactionIdFromBin(firstSix,lastFour,name);
    }
    public List<TransactionDetailsVO> getTransactionDetailsForCommon(List<String> trackingId)throws PZDBViolationException
    {
        return transactionDAO.getTransactionDetailsForCommon(trackingId);
    }
    public HashMap<String,TransactionVO> getMemberListFromTransaction(String memberid,String partnerName,String gateway,String currency,String startDate,String endDate)throws PZDBViolationException
    {
        return transactionDAO.getMemberListFromTransaction(memberid,partnerName,gateway,currency,startDate,endDate);
    }
    public HashMap<String,TransactionVO> getAccountIdListFromTransaction(String gateway,String currency,String startDate,String endDate)throws PZDBViolationException
    {
        return transactionDAO.getAccountIdListFromTransaction(gateway, currency, startDate, endDate);
    }

    public TransactionDetailsVO getTransDetailFromCommonForAuthStarted(String trackingId)
    {
        return transactionDAO.getDetailFromCommonForAuthStarted(trackingId);
    }
    public List<MarketPlaceVO> getChildDetailsByParentTrackingid(String trackingId)
    {
        return transactionDAO.getChildDetailsByParentTrackingid(trackingId);
    }
    public MarketPlaceVO getParentDetailsByChildTrackingid(String trackingId)
    {
     return transactionDAO.getParentDetailsByChildTrackingid(trackingId);
    }
    public MarketPlaceVO getChildDetailsByChildTrackingid(String trackingId)
    {
        return transactionDAO.getChildDetailsByChildTrackingid(trackingId);
    }
    public CommTransactionDetailsVO getTransactionDetailFromCommonForQR(String trackingId)
    {
        return transactionDAO.getTransactionDetailFromCommonForQR(trackingId);
    }
    public String getTransactionDetailIdFromTrackingId(String trackingId) throws PZDBViolationException
    {
        return transactionDAO.getTransactionDetailIdFromTrackingId(trackingId);
    }
    public boolean updateTransactionReceiptByDetailId(CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        return transactionDAO.updateTransactionReceiptByDetailId(commonValidatorVO);
    }
    public List<TransactionVO> getTransactionListForVirtualCheckout(CommonValidatorVO commonValidatorVO,String fdtstamp,String tdtstamp) throws PZDBViolationException
    {
        return transactionDAO.getTransactionListForVirtualCheckout(commonValidatorVO,fdtstamp,tdtstamp);
    }
    public List<TransactionVO> gettransactionDetailsMemberUser(TransactionVO transactionVO,CommonValidatorVO commonValidatorVO, List<TransactionVO> transDetailVOList,String fdtstamp,String tdtstamp)
    {
        return transactionDAO.transactionDetailsForAPIMemberUser(transactionVO,commonValidatorVO,transDetailVOList,fdtstamp,tdtstamp);
    }
    public TransactionDetailsVO getDetailFraudDefender(CommonValidatorVO commonValidatorVO)
    {
       return transactionDAO.getDetailFraudDefender(commonValidatorVO );
    }

    public HashMap<String, String> getbincountrysuccessful(String toid ,String terminalid,String country, String tdtstamp, String fdtstamp) throws PZDBViolationException
    {
        return transactionDAO.getbincountrysuccessful( toid,terminalid,country,tdtstamp,fdtstamp );
    }

    public HashMap<String, String> getbincountryfailed(String toid ,String terminalid,String country, String tdtstamp, String fdtstamp) throws PZDBViolationException
    {
        return transactionDAO.getbincountryfailed( toid,terminalid,country, tdtstamp, fdtstamp);
    }

    public HashMap<String, String> getipcountrysuccessful( String toid,String terminalid,String tdtstamp, String fdtstamp) throws PZDBViolationException
    {
        return transactionDAO.getipcountrysuccessful( toid,terminalid,tdtstamp, fdtstamp);
    }

    public HashMap<String, String> getipcountryfailed(String toid ,String terminalid ,String tdtstamp, String fdtstamp) throws PZDBViolationException
    {
        return transactionDAO.getipcountryfailed(toid, terminalid, tdtstamp, fdtstamp);
    }
    public TransactionDetailsVO getDetailFraudDefenderForICard(CommonValidatorVO commonValidatorVO)
    {
        return transactionDAO.getDetailFraudDefenderForICard(commonValidatorVO);
    }
    public String insertFraudDefenderDetails(CommonValidatorVO commonValidatorVO)
    {
        return transactionDAO.insertFraudDefenderDetails(commonValidatorVO);
    }
    public boolean updateFraudTransactionDetails(CommonValidatorVO commonValidatorVO,TransactionDetailsVO transactionDetailsVO)
    {
        return transactionDAO.updateFraudTransactionDetails(commonValidatorVO,transactionDetailsVO);
    }
    public boolean updateRefundFraudTransactionDetails(String trackingId,String isRefund,String refundAmount)
    {
        return transactionDAO.updateRefundFraudTransactionDetails(trackingId,isRefund,refundAmount);
    }
    public boolean updateQueryRefundFraudTransactionDetails(String fraudId,String isRefund,String refundAmount)
    {
        return transactionDAO.updateQueryRefundFraudTransactionDetails(fraudId,isRefund,refundAmount);
    }
    public Hashtable fraudTransactionListPartner(String toid,String partnerid, String mid,String trackingid,String terminalId,String transactionType, String amount, String refundamount, String firstsix,String lastfour,String tdtstamp, String fdtstamp, int records, int pageno,String currency, String accountid,String paymentid, String dateType,String isRefund,String rrn,String authCode, int totalrecords)
    {
        return transactionDAO.fraudTransactionListPartner(toid, partnerid, mid, trackingid, terminalId, transactionType, amount, refundamount, firstsix, lastfour, tdtstamp, fdtstamp, records, pageno, currency, accountid, paymentid, dateType, isRefund,rrn,authCode,totalrecords);
    }
    public int getCountfraudTransactionListNew(String toid,String partnerid, String mid,String trackingid,String terminalId,String transactionType, String amount, String refundamount, String firstsix,String lastfour,String tdtstamp, String fdtstamp, int records, int pageno,String currency, String accountid,String paymentid, String dateType,String isRefund,String rrn,String authCode)
    {
        return transactionDAO.getCountfraudTransactionListNew(toid,partnerid,mid, trackingid,terminalId,transactionType,amount, refundamount, firstsix,lastfour, tdtstamp, fdtstamp,records, pageno, currency, accountid, paymentid, dateType,isRefund,rrn,authCode);
    }
    public Hashtable listTransactionsFraudAlertPartnerExportInExcel(String toid, String partnerid,String mid,String trackingid,String terminalId,String transactionType, String amount, String refundamount, String firstfourofccnum, String lastfourofccnum, String tdtstamp, String fdtstamp,String currency, String accountid,String paymentid, String dateType,String isRefund,String rrn,String authCode)
    {
        return transactionDAO.listTransactionsFraudAlertPartnerExportInExcel(toid,partnerid,mid, trackingid,terminalId,transactionType,amount, refundamount, firstfourofccnum, lastfourofccnum, tdtstamp, fdtstamp,currency, accountid, paymentid, dateType,isRefund,rrn,authCode);
    }
    public String getTerminalId(String accountid, String memberid)
    {
        return transactionDAO.getTerminalId(accountid, memberid);
    }
    public HashMap cardPresentlistTrackingIds(String toid, String trackingid, String name, String desc, String orderdesc, String amount, String refundamount, String firstfourofccnum, String lastfourofccnum, String emailaddr, String tdtstamp, String fdtstamp, String status, int records, int pageno, String perfectmatch, boolean archive, Set<String> gatewayTypeSet, String remark, String cardtype, String issuing_bank, String statusFlag, String gateway_name, String currency, String accountid,String paymentid, String dateType, String customerId, String telno,String telnocc,String totype)
    {
        return transactionDAO.cardPresentlistTrackingIds(toid, trackingid, name, desc, orderdesc, amount, refundamount, firstfourofccnum, lastfourofccnum, emailaddr, tdtstamp, fdtstamp, status, records, pageno, perfectmatch, archive, gatewayTypeSet, remark, cardtype, issuing_bank, statusFlag, gateway_name, currency, accountid, paymentid, dateType,customerId,telno,telnocc,totype);
    }
    public HashMap listTrackingIds(String toid, String trackingid, String name, String desc, String orderdesc, String amount, String refundamount, String firstfourofccnum, String lastfourofccnum, String emailaddr, String tdtstamp, String fdtstamp, String status, int records, int pageno, String perfectmatch, boolean archive, Set<String> gatewayTypeSet, String remark, String cardtype, String issuing_bank, String statusFlag, String gateway_name, String currency, String accountid,String paymentid, String dateType ,String arn,String rrn,String authcode,String customerId,String transactionMode,String telno, String telnocc,String totype)
    {
        return transactionDAO.listTrackingIds(toid, trackingid, name, desc, orderdesc, amount, refundamount, firstfourofccnum, lastfourofccnum, emailaddr, tdtstamp, fdtstamp, status, records, pageno, perfectmatch, archive, gatewayTypeSet, remark, cardtype, issuing_bank, statusFlag, gateway_name, currency, accountid, paymentid, dateType, arn, rrn, authcode,customerId,transactionMode,telno,telnocc,totype);
    }
    public HashMap listTrackingidsBasedOnDetailStatus(String toid, String trackingid, String name, String desc, String orderdesc, String amount, String refundamount, String firstfourofccnum, String lastfourofccnum, String emailaddr, String tdtstamp, String fdtstamp, String status, int records, int pageno, String perfectmatch, boolean archive, Set<String> gatewayTypeSet, String remark, String cardtype, String issuing_bank, String statusFlag, String gateway_name, String currency, String accountid,String paymentid, String dateType,String arn , String rrn, String auth,String detailStatus,String customerId,String transactionMode,String telno,String telnocc,String totype)
    {
        return transactionDAO.listTrackingidsBasedOnDetailStatus(toid, trackingid, name, desc, orderdesc, amount, refundamount, firstfourofccnum, lastfourofccnum, emailaddr, tdtstamp, fdtstamp, status, records, pageno, perfectmatch, archive, gatewayTypeSet, remark, cardtype, issuing_bank, statusFlag, gateway_name, currency, accountid, paymentid, dateType,arn,rrn,auth,detailStatus,customerId,transactionMode,telno,telnocc,totype);
    }
    public TransactionDetailsVO getDetailFromCommonForJPBank(String customerId)
    {
        return transactionDAO.getDetailFromCommonForJPBank(customerId);
    }
    public TransactionDetailsVO getTransDetailsForQuickpayments(String arn,String toid)
    {
        return transactionDAO.getTransDetailsForQuickpayments(arn,toid);
    }
    public boolean updateNotificationStatusCode(String trackingId,String responseCode, CommResponseVO commResponseVO,AuditTrailVO auditTrailVO)
    {
        return transactionDAO.updateNotificationStatusCode(trackingId,responseCode, commResponseVO,auditTrailVO);
    }
    public Hashtable getPayoutStartDetails(String trackingid)
    {
        return transactionDAO.getPayoutStartDetails(trackingid);
    }

    public Hashtable getPayoutAmountLimitDetails(String gatewayname,int pageno ,int pagerecords){
        return transactionDAO.getPayoutAmountLimitDetails(gatewayname, pageno, pagerecords);
    }
    public Map<String,Map<String,Map<String,Object>>> getDailySalesReport(CommonValidatorVO commonValidatorVO,List<Date> dateRange){
        return transactionDAO.getDailySalesReport(commonValidatorVO, dateRange);
    }

    public Hashtable payoutTransactionList(String toid, String trackingIds, String terminalId, String amount,String email, String description, String accountid, String tdtstamp, String fdtstamp, int records, int pageno, String totype,String dateType, String bankAccount, String bankNumber, String bankIfsc,String status)
    {
       return transactionDAO.payoutTransactionList(toid, trackingIds.toString(), terminalId, amount, email, description, accountid, tdtstamp, fdtstamp, records, pageno, totype,dateType, bankAccount,status);
    }

    public Hashtable listPayoutTransactionsForExportInExcel(String toid, String pgTypeId, String trackingid, String desc, String amount, String emailaddr, String tdtstamp, String fdtstamp, String status, String remark,String paymentId, String perfectmatch, boolean archive, Set<String> gatewayTypeSet, String gateway_name, String accountid, String dateType, String currency, String auth,String transactionMode, String customerid, String statusflag ,String totype,String bankname, String bankaccount,String ifsc ) throws PZDBViolationException
    {
        return transactionDAO.listPayoutTransactionsForExportInExcel(toid, pgTypeId, trackingid, desc, amount, emailaddr, tdtstamp, fdtstamp, status, remark, paymentId, perfectmatch, archive, gatewayTypeSet, gateway_name, accountid, dateType, currency, auth, transactionMode, customerid, statusflag, totype, bankname, bankaccount, ifsc);
    }
    public TransactionDetailsVO getDetailFromCommonBasedOnPaymentId(String trackingId)
    {
        return transactionDAO.getDetailFromCommonBasedOnPaymentId(trackingId);
    }
    public List<TransactionVO> getTransactionListForInquiry(TransactionVO transactionVO, String fdtstamp, String tdtstamp, PaginationVO paginationVO)
    {
        return transactionDAO.getTransactionListForInquiry(transactionVO,fdtstamp,tdtstamp,paginationVO);
    }
    public TransactionDetailsVO getInquiryDetailsbyTrackingid(String trackingid)
    {
        return transactionDAO.getInquiryDetailsbyTrackingid(trackingid);
    }
}