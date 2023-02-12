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
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;
import com.manager.dao.CommissionDAO;
import com.manager.dao.CommissionManager;
import com.manager.enums.*;
import com.manager.vo.*;
import com.manager.vo.merchantmonitoring.DateVO;
import com.manager.vo.payoutVOs.ChargeMasterVO;
import com.manager.vo.payoutVOs.CommissionDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.payout.PayoutReportUtils;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
/**
 * Created with IntelliJ IDEA.
 * User: Sandip
 * Date: 8/8/16
 * Time: 3:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class ISOCommissionGenerator
{
    private static Logger logger=new Logger(ISOCommissionGenerator.class.getName());
    private final static String PARTNER_PAYOUT_REPORT_FILE_PATH = ApplicationProperties.getProperty("ISO_COMMISSION_REPORT_FILE_PATH");
    private final static String PARTNER_LOGO_PATH = ApplicationProperties.getProperty("PARTNER_LOGO_PATH");
    public ISOCommReportVO generateISOCommissionReport(String accountId, DateVO dateVO,HashMap<String,String> dynamicInputValues,GatewayAccount gatewayAccount,List<CommissionVO> commissionVOList)
    {
        HashMap<String,CommissionDetailsVO> commissionDetailsVOHashMap=new LinkedHashMap();
        CommissionManager commissionManager=new CommissionManager();
        TransactionManager transactionManager=new TransactionManager();
        ISOCommReportVO isoCommReportVO=null;
        try
        {
            /*Variable declaration*/
            double totalCommissionAmount=0.00;

            GatewayType gatewayType=GatewayTypeService.getGatewayType(gatewayAccount.getPgTypeId());
            String tableName = Database.getTableName(gatewayType.getGateway());

            PartnerManager partnerManager=new PartnerManager();
            PartnerDetailsVO partnerDetailsVO=partnerManager.getPartnerDetails(gatewayAccount.getPartnerId());

            long invoiceCount=commissionManager.getGatewayAccountWireCount(gatewayAccount);

            long invoiceNumber=invoiceCount+1;

            //Step@1.1:Retrieve all the commissions from db where is input required='N';
            /*List<CommissionVO> commissionVOList=commissionManager.getGatewayAccountCommission(accountId);*/
            //System.out.println("Total Commission Founds====="+commissionVOList.size());
            //Step1.2:transaction details(status wise).
            TransactionSummaryVO transactionSummaryVO=transactionManager.getGatewayAccountProcessingDetails(gatewayAccount,dateVO,tableName);
            long retrivalRequestCount=transactionManager.getRetrivalRequestOnGatewayAccount(gatewayAccount,tableName,dateVO);

            transactionSummaryVO.setCountOfRetrievalRequest(retrivalRequestCount);
            //Step3:Start iterating on commission
            CommissionDetailsVO  commissionDetailsVO=null;
            for(CommissionVO commissionVO:commissionVOList)
            {
                //Step4:Save all the calculation in vo.
                ChargeMasterVO chargeMasterVO=commissionVO.getChargeMasterVO();

              /*  System.out.println("Commission Name======="+commissionVO.getChargeMasterVO().getChargeName());
                System.out.println("value type===="+commissionVO.getChargeMasterVO().getValueType());
                System.out.println("charge id===="+commissionVO.getChargeMasterVO().getChargeId());*/
                if(chargeMasterVO.getCategory().equals(Charge_category.Others.toString()) && chargeMasterVO.getKeyword().equals(Charge_keyword.Setup.toString())&&chargeMasterVO.getSubKeyword().equals(Charge_subKeyword.Count.toString()))
                {
                    commissionDetailsVO=calculateISOCommissionOnSetupFee(commissionVO,gatewayAccount,dateVO);
                }
                else if(Charge_category.Others.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Wire.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeMasterVO.getSubKeyword()))
                {
                    commissionDetailsVO=new CommissionDetailsVO();
                    commissionDetailsVO.setChargeName(chargeMasterVO.getChargeName());
                    commissionDetailsVO.setValueType(chargeMasterVO.getValueType());
                    commissionDetailsVO.setChargeValue(String.valueOf(commissionVO.getCommissionValue()));

                    int cntCounter=0;
                    if(dynamicInputValues.get(commissionVO.getChargeId())!=null)
                    {
                        cntCounter=Integer.parseInt(dynamicInputValues.get(commissionVO.getChargeId()));
                    }
                    double vDblAmount=0.00;
                    double dblTotal=(cntCounter*commissionVO.getCommissionValue());

                    commissionDetailsVO.setCount(cntCounter);
                    commissionDetailsVO.setAmount(dblTotal);
                    commissionDetailsVO.setTotal(Functions.roundDBL(dblTotal, 2));
                }
                else if(Charge_category.Others.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Statement.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeMasterVO.getSubKeyword()))
                {
                    commissionDetailsVO=new CommissionDetailsVO();
                    commissionDetailsVO.setChargeName(chargeMasterVO.getChargeName());
                    commissionDetailsVO.setValueType(chargeMasterVO.getValueType());
                    commissionDetailsVO.setChargeValue(String.valueOf(commissionVO.getCommissionValue()));

                    /*int cntCounter=Integer.parseInt(dynamicInputValues.get(commissionVO.getChargeId()));*/
                    int cntCounter=0;
                    if(dynamicInputValues.get(commissionVO.getChargeId())!=null)
                    {
                        cntCounter=Integer.parseInt(dynamicInputValues.get(commissionVO.getChargeId()));
                    }

                    double vDblAmount=0.00;
                    double dblTotal=(cntCounter*commissionVO.getCommissionValue());

                    commissionDetailsVO.setCount(cntCounter);
                    commissionDetailsVO.setAmount(dblTotal);
                    commissionDetailsVO.setTotal(Functions.roundDBL(dblTotal, 2));
                }
                else
                {
                    commissionDetailsVO=calculateISOCommissionValue(commissionVO,transactionSummaryVO);
                }
                totalCommissionAmount=totalCommissionAmount+commissionDetailsVO.getTotal();
                commissionDetailsVOHashMap.put(commissionDetailsVO.getChargeName(),commissionDetailsVO);
            }
            if(commissionDetailsVOHashMap.size()>0)
            {
                String reportFile=generateISOCommissionReport(commissionDetailsVOHashMap,gatewayType,gatewayAccount,dateVO,totalCommissionAmount,partnerDetailsVO,invoiceNumber);
                //System.out.println("reportFile====="+reportFile);
                isoCommReportVO=new ISOCommReportVO();
                isoCommReportVO.setStartDate(dateVO.getStartDate());
                isoCommReportVO.setEndDate(dateVO.getEndDate());
                isoCommReportVO.setAccountId(accountId);
                isoCommReportVO.setReportFilePath(reportFile);
                isoCommReportVO.setBankName(gatewayType.getGateway());
                isoCommReportVO.setCurrency(gatewayAccount.getCurrency());
                isoCommReportVO.setAmount(transactionSummaryVO.getTotalProcessingAmount());
                isoCommReportVO.setNetfinalamount(totalCommissionAmount);
                isoCommReportVO.setStatus("unpaid");
                isoCommReportVO.setUnpaidAmount(0.00);
            }
            //System.out.println("totalCommissionAmount======"+totalCommissionAmount);
            //Step4:Prepare the pdf(report file) and excel(transaction file) based on calculation details.
            //Step5:Generate the wire in "ISO Wire Manager".
            //Step6:Return some acknowledgement.
        }
        catch (PZDBViolationException e)
        {
            //Add Exception Message
            logger.error("PZDBViolationException---->",e);
        }
        catch (Exception e)
        {
            logger.error("Exception---->", e);
            //Add Exception Message
        }
        return isoCommReportVO;
    }
    private CommissionDetailsVO calculateISOCommissionValue(CommissionVO commissionVO, TransactionSummaryVO summaryVO)
    {
        long settledTransCount = 0;
        long reversedTransCount = 0;
        long chargebackTransCount = 0;
        long authfailedTransCount = 0;
        long totalSaleCount = 0;
        long retivalRequestCount = 0;

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

        retivalRequestCount=summaryVO.getCountOfRetrievalRequest();

        long vCntCounter=0;
        double vDblAmount=0.00;
        double vDblTotal=0.00;

        ChargeMasterVO  chargeMasterVO=commissionVO.getChargeMasterVO();

        CommissionDetailsVO commissionDetailsVO=new CommissionDetailsVO();

        commissionDetailsVO.setChargeName(chargeMasterVO.getChargeName());
        commissionDetailsVO.setValueType(chargeMasterVO.getValueType());
        commissionDetailsVO.setChargeValue(String.valueOf(commissionVO.getCommissionValue()));

        if(Charge_category.Success.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeMasterVO.getSubKeyword()))
        {
            //%MDR
            vCntCounter=totalSaleCount;
            vDblAmount=totalSaleAmount;
            vDblTotal=(vDblAmount*commissionVO.getCommissionValue())/100;

            commissionDetailsVO.setCount(vCntCounter);
            commissionDetailsVO.setAmount(vDblAmount);
            commissionDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
        }
        else if(Charge_category.Success.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeMasterVO.getSubKeyword()))
        {
            //Approval /Authorization Fee
            vCntCounter=totalSaleCount;
            vDblAmount=totalSaleAmount;
            vDblTotal=vCntCounter*commissionVO.getCommissionValue();

            commissionDetailsVO.setCount(vCntCounter);
            commissionDetailsVO.setAmount(vDblAmount);
            commissionDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));

        }
        else if(Charge_category.Success.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Reversed.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeMasterVO.getSubKeyword()))
        {
            //Reversal Fee
            vCntCounter=reversedTransCount;
            vDblAmount=totalReversedTransAmount;
            vDblTotal=(vCntCounter*commissionVO.getCommissionValue());

            commissionDetailsVO.setCount(vCntCounter);
            commissionDetailsVO.setAmount(vDblAmount);
            commissionDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
        }
        else if(Charge_category.Success.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Chargeback.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeMasterVO.getSubKeyword()))
        {
            //Chargeback Fee
            vCntCounter=chargebackTransCount;
            vDblAmount=totalChargebackTransAmount;
            vDblTotal=(vCntCounter*commissionVO.getCommissionValue());

            commissionDetailsVO.setCount(vCntCounter);
            commissionDetailsVO.setAmount(vDblAmount);
            commissionDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
        }
        else if(Charge_category.Failure.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeMasterVO.getSubKeyword()))
        {
            //Declined Fee
            vCntCounter=authfailedTransCount;
            vDblAmount=totalAuthfailedTransAmount;
            vDblTotal=(vCntCounter*commissionVO.getCommissionValue());

            commissionDetailsVO.setCount(vCntCounter);
            commissionDetailsVO.setAmount(vDblAmount);
            commissionDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
        }
        else if(Charge_category.Others.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeMasterVO.getSubKeyword()))
        {
            //Fraud Checking
            vCntCounter=totalSaleCount+authfailedTransCount;
            vDblAmount=totalSaleAmount+totalAuthfailedTransAmount;
            vDblTotal=(vCntCounter*commissionVO.getCommissionValue());

            commissionDetailsVO.setCount(vCntCounter);
            commissionDetailsVO.setAmount(vDblAmount);
            commissionDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
        }
        else if(Charge_category.Others.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.RetrivalRequest.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeMasterVO.getSubKeyword()))
        {
            //Retrieval Fee
            vCntCounter=retivalRequestCount;
            vDblAmount=0.00;
            vDblTotal=(vCntCounter*commissionVO.getCommissionValue());

            commissionDetailsVO.setCount(vCntCounter);
            commissionDetailsVO.setAmount(vDblAmount);
            commissionDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
        }
        return commissionDetailsVO;
    }

    public String generateISOCommissionReport(HashMap<String, CommissionDetailsVO> commissionDetailsVOHashMap,GatewayType gatewayType,GatewayAccount gatewayAccount,DateVO dateVO,double totalCommissionAmount,PartnerDetailsVO partnerDetailsVO,long invoiceNumber)
    {
        PayoutReportUtils payoutReportUtils=new PayoutReportUtils();
        Document document = new Document(PageSize.A3, 50, 50, 50, 50);
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
        String reportFileName=payoutReportUtils.getISOCommissionReportFileName(gatewayType.getGateway(),gatewayAccount.getMerchantId(),dateVO.getStartDate(),dateVO.getEndDate());
        try
        {
            reportFileName=reportFileName+".pdf";
            File filePath=new File(PARTNER_PAYOUT_REPORT_FILE_PATH+reportFileName);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            Functions functions=new Functions();
            String logoName="pay.png";
            if(functions.isValueNull(partnerDetailsVO.getLogoName()))
            {
                logoName=partnerDetailsVO.getLogoName();
            }
            String reportingDate=targetFormat.format(new Date());

            Image partnerImageInstance = Image.getInstance(PARTNER_LOGO_PATH+logoName);
            partnerImageInstance.scaleAbsolute(150f, 150f);
            partnerImageInstance.scaleAbsoluteHeight(40f);

            Table table = new Table(6);
            table.setWidth(100);
            table.setBorderColor(new Color(0, 0,0));
            table.setPadding(0.75f);

            Cell partnerLogoCell=new Cell(partnerImageInstance);
            partnerLogoCell.setColspan(2);
            partnerLogoCell.setHorizontalAlignment(Element.ALIGN_CENTER);


            Cell partnerNameCaptionValue=new Cell(partnerDetailsVO.getCompanyName());
            partnerNameCaptionValue.setColspan(4);
            partnerNameCaptionValue.setHorizontalAlignment(Element.ALIGN_LEFT);

            table.addCell(partnerLogoCell);
            table.addCell(partnerNameCaptionValue);

            Cell invoiceInfo=new Cell("Invoice Info");
            invoiceInfo.setColspan(6);
            invoiceInfo.setHorizontalAlignment(Element.ALIGN_CENTER);
            invoiceInfo.setBackgroundColor(Color.lightGray);
            table.addCell(invoiceInfo);

            Cell invoiceNumberLabel  = new Cell("Invoice No:"+invoiceNumber);
            invoiceNumberLabel.setColspan(2);
            invoiceNumberLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(invoiceNumberLabel);

            Cell accountNameLabel  = new Cell("Merchant:"+gatewayAccount.getMerchantId());
            accountNameLabel.setColspan(2);
            accountNameLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(accountNameLabel);

            //Cell bankNameLabelValue  = new Cell();
            Cell reportCurrencyLabel  = new Cell("Currency:"+gatewayAccount.getCurrency());
            reportCurrencyLabel.setColspan(2);
            reportCurrencyLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(reportCurrencyLabel);

            Cell bankNameLabel  = new Cell("Bank Name:"+gatewayType.getGateway());
            bankNameLabel.setColspan(2);
            bankNameLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(bankNameLabel);

            Cell gatewayAccountIdLabel  = new Cell("Gateway Account:"+gatewayAccount.getAccountId());
            gatewayAccountIdLabel.setColspan(2);
            gatewayAccountIdLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(gatewayAccountIdLabel);


            Cell invoiceAmountLabel  = new Cell("Final Amount:"+Functions.round(totalCommissionAmount,2));
            invoiceAmountLabel.setColspan(2);
            invoiceAmountLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(invoiceAmountLabel);

            Cell invoiceDateLabel  = new Cell("Invoice Date:"+reportingDate);
            invoiceDateLabel.setColspan(2);
            invoiceDateLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(invoiceDateLabel);

            Cell startDateLabel  = new Cell("Start Date:"+dateVO.getStartDate());
            startDateLabel.setColspan(2);
            startDateLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(startDateLabel);

            Cell endDateLabel  = new Cell("End Date:"+dateVO.getEndDate());
            endDateLabel.setColspan(2);
            endDateLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(endDateLabel);


            Cell commissionInfo=new Cell("Commission Info");
            commissionInfo.setColspan(6);
            commissionInfo.setHorizontalAlignment(Element.ALIGN_CENTER);
            commissionInfo.setBackgroundColor(Color.lightGray);

            Cell commissionNameLabel  = new Cell("Fees");
            Cell commissionRateLabel  = new Cell("Rate");
            Cell commissionCounterLabel  = new Cell("Count");
            Cell commissionAmountLabel  = new Cell("Amount");
            Cell commissionTotalLabel  = new Cell("Total");

            commissionNameLabel.setColspan(2);

            commissionNameLabel.setBackgroundColor(Color.lightGray);
            commissionRateLabel.setBackgroundColor(Color.lightGray);
            commissionCounterLabel.setBackgroundColor(Color.lightGray);
            commissionAmountLabel.setBackgroundColor(Color.lightGray);
            commissionTotalLabel.setBackgroundColor(Color.lightGray);

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
            totalLabel.setBackgroundColor(Color.lightGray);

            totalLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(totalLabel);
            table.addCell(totalValue);

            Image poweredByLogoValue = Image.getInstance(PARTNER_LOGO_PATH+"logo2.jpg");

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
            logger.error("Exception--->", ex);
        }
        return reportFileName;
    }
    public CommissionDetailsVO calculateISOCommissionOnSetupFee(CommissionVO partnerCommissionVO,GatewayAccount gatewayAccount,DateVO dateVO)throws ParseException,PZDBViolationException//sandip
    {
        CommissionDAO commissionDAO=new CommissionDAO();
        TransactionManager transactionManager=new TransactionManager();
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String setupFeeDateUpto=dateVO.getEndDate();
        long setupCounter=0;
        boolean oneTimeChargeFlag=false;

        ChargeMasterVO chargeMasterVO=partnerCommissionVO.getChargeMasterVO();
        CommissionDetailsVO chargeDetailsVO=new CommissionDetailsVO();

        chargeDetailsVO.setChargeName(chargeMasterVO.getChargeName());
        chargeDetailsVO.setValueType(chargeMasterVO.getValueType());
        chargeDetailsVO.setChargeValue(String.valueOf(partnerCommissionVO.getCommissionValue()));

        String memberFirstTransactionDate=transactionManager.getMemberFirstTransactionDateOnGatewayAccount(gatewayAccount);
        String lastSetFeeDate=commissionDAO.getGatewayAccountSetupCoveredDate(gatewayAccount);
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
            long dy=Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)),targetFormat.format(targetFormat.parse(setupFeeDateUpto)));
            totalYearCount=dy/365+1;
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
}
