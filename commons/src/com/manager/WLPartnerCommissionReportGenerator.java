package com.manager;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.core.GatewayType;
import com.logicboxes.util.ApplicationProperties;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;
import com.manager.dao.ChargesDAO;
import com.manager.dao.PayoutDAO;
import com.manager.enums.*;
import com.manager.vo.CommissionVO;
import com.manager.vo.PartnerDetailsVO;
import com.manager.vo.TransactionSummaryVO;
import com.manager.vo.WLPartnerInvoiceVO;
import com.manager.vo.merchantmonitoring.DateVO;
import com.manager.vo.payoutVOs.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;

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
 * Date: 12/12/16
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class WLPartnerCommissionReportGenerator
{
    private final static String WL_PARTNER_INVOICE_FILE_PATH = ApplicationProperties.getProperty("WL_PARTNER_INVOICE_FILE_PATH");
    private final static String PARTNER_LOGO_PATH = ApplicationProperties.getProperty("PARTNER_LOGO_PATH");
    Logger log = new Logger(WLPartnerCommissionReportGenerator.class.getName());

    public WLPartnerCommissionDetailsVO calculateWLPartnerSetupFee(PartnerDetailsVO partnerDetailsVO, CommissionVO partnerCommissionVO, DateVO dateVO) throws ParseException, PZDBViolationException//sandip
    {
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String setupFeeDateUpto = dateVO.getEndDate();
        long setupCounter = 0;
        boolean oneTimeChargeFlag = false;

        TransactionManager transactionManager = new TransactionManager();
        WLPartnerCommissionDetailsVO wlPartnerCommissionDetailsVO = new WLPartnerCommissionDetailsVO();
        ChargeMasterVO chargeMasterVO = partnerCommissionVO.getChargeMasterVO();


        wlPartnerCommissionDetailsVO.setChargeName(chargeMasterVO.getChargeName());
        wlPartnerCommissionDetailsVO.setValueType(chargeMasterVO.getValueType());
        wlPartnerCommissionDetailsVO.setChargeValue(String.valueOf(partnerCommissionVO.getCommissionValue()));

        //String memberFirstTransactionDate = transactionManager.getPartnersFirstTransactionDate(partnerDetailsVO.getCompanyName());
        String memberFirstTransactionDate = getGatewayTypeSetupStartDate(partnerDetailsVO.getPartnerId());
        Functions functions = new Functions();
        if (!functions.isValueNull(memberFirstTransactionDate))
        {
            memberFirstTransactionDate = dateVO.getStartDate();
        }
        String lastSetFeeDate = getGatewayTypeSetupCoveredDate(partnerDetailsVO.getPartnerId());
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
            long chargedCount = 0;

            DateManager dateManager = new DateManager();
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");

            Date d1 = f.parse(memberFirstTransactionDate.split(" ")[0]);
            Date d2 = f.parse(setupFeeDateUpto.split(" ")[0]);

            totalMonthCount = dateManager.differenceInMonths(d1, d2);
            //long dy = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)), targetFormat.format(targetFormat.parse(setupFeeDateUpto)));
            totalMonthCount = totalMonthCount + 1;
            if (lastSetFeeDate != null)
            {
                SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd");
                Date d11 = f1.parse(memberFirstTransactionDate.split(" ")[0]);
                Date d22 = f1.parse(lastSetFeeDate.split(" ")[0]);
                //long d = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)), targetFormat.format(targetFormat.parse(lastSetFeeDate)));
                chargedCount = dateManager.differenceInMonths(d11, d22);
                chargedCount = chargedCount + 1;
            }
            setupCounter = totalMonthCount - chargedCount;
        }
        else if (chargeMasterVO.getFrequency().equals(Charge_frequency.Yearly.toString()))
        {
            long totalYearCount = 0;
            long ChargedCount = 0;
            long dy = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)), targetFormat.format(targetFormat.parse(setupFeeDateUpto)));
            totalYearCount = dy / 365 + 1;
            if (lastSetFeeDate != null)
            {
                long d = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(memberFirstTransactionDate)), targetFormat.format(targetFormat.parse(lastSetFeeDate)));
                ChargedCount = d / 365 + 1;
            }
            setupCounter = totalYearCount - ChargedCount;
        }
        double total = setupCounter * Double.valueOf(partnerCommissionVO.getCommissionValue());
        wlPartnerCommissionDetailsVO.setCount(setupCounter);
        wlPartnerCommissionDetailsVO.setAmount(0.00);
        wlPartnerCommissionDetailsVO.setTotal(total);
        return wlPartnerCommissionDetailsVO;
    }

    public WLPartnerCommissionDetailsVO calculateWLPartnerCommissionValue(CommissionVO commissionVO, TransactionSummaryVO summaryVO)
    {
        long reversedTransCount = 0;
        long chargebackTransCount = 0;
        long authfailedTransCount = 0;
        long authstartedTransCount = 0;
        long totalSaleCount = 0;

        double totalAuthstartedTransAmount = 0.00;
        double totalAuthfailedTransAmount = 0.00;
        double totalReversedTransAmount = 0.00;
        double totalChargebackTransAmount = 0.00;
        double totalSaleAmount = 0.00;

        totalSaleAmount = summaryVO.getTotalProcessingAmount();
        totalSaleCount = summaryVO.getTotalProcessingCount();

        authfailedTransCount = summaryVO.getCountOfAuthfailed();
        totalAuthfailedTransAmount = summaryVO.getAuthfailedAmount();

        authstartedTransCount = summaryVO.getAuthstartedCount();
        totalAuthstartedTransAmount = summaryVO.getAuthstartedAmount();

        reversedTransCount = summaryVO.getCountOfReversed();
        totalReversedTransAmount = summaryVO.getReversedAmount();

        chargebackTransCount = summaryVO.getCountOfChargeback();
        totalChargebackTransAmount = summaryVO.getChargebackAmount();

        long vCntCounter = 0;
        double vDblAmount = 0.00;
        double vDblTotal = 0.00;

        ChargeMasterVO chargeMasterVO = commissionVO.getChargeMasterVO();
        WLPartnerCommissionDetailsVO wlPartnerCommissionDetailsVO = new WLPartnerCommissionDetailsVO();

        wlPartnerCommissionDetailsVO.setChargeName(chargeMasterVO.getChargeName());
        wlPartnerCommissionDetailsVO.setValueType(chargeMasterVO.getValueType());
        wlPartnerCommissionDetailsVO.setChargeValue(String.valueOf(commissionVO.getCommissionValue()));

        if (Charge_category.Success.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.NetProfit.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeMasterVO.getSubKeyword()))
        {
            //Profit Share In Percentage
            vCntCounter = totalSaleCount;
            vDblAmount = totalSaleAmount;
            vDblTotal = (vDblAmount * commissionVO.getCommissionValue()) / 100;

            wlPartnerCommissionDetailsVO.setCount(vCntCounter);
            wlPartnerCommissionDetailsVO.setAmount(vDblAmount);
            wlPartnerCommissionDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
        }
        if (Charge_category.Success.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeMasterVO.getSubKeyword()))
        {
            //MDR In Percentage
            vCntCounter = totalSaleCount;
            vDblAmount = totalSaleAmount;
            vDblTotal = (vDblAmount * commissionVO.getCommissionValue()) / 100;

            wlPartnerCommissionDetailsVO.setCount(vCntCounter);
            wlPartnerCommissionDetailsVO.setAmount(vDblAmount);
            wlPartnerCommissionDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
        }
        else if (Charge_category.Success.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeMasterVO.getSubKeyword()))
        {
            //Approval/Authorization- Per Transaction Fees
            vCntCounter = totalSaleCount;
            vDblAmount = totalSaleAmount;
            vDblTotal = vCntCounter * commissionVO.getCommissionValue();

            wlPartnerCommissionDetailsVO.setCount(vCntCounter);
            wlPartnerCommissionDetailsVO.setAmount(vDblAmount);
            wlPartnerCommissionDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));

        }
        else if (Charge_category.Success.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Reversed.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeMasterVO.getSubKeyword()))
        {
            //Reversal Fee
            vCntCounter = reversedTransCount;
            vDblAmount = totalReversedTransAmount;
            vDblTotal = (vCntCounter * commissionVO.getCommissionValue());

            wlPartnerCommissionDetailsVO.setCount(vCntCounter);
            wlPartnerCommissionDetailsVO.setAmount(vDblAmount);
            wlPartnerCommissionDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
        }
        else if (Charge_category.Success.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Chargeback.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeMasterVO.getSubKeyword()))
        {
            //Chargeback Fee
            vCntCounter = chargebackTransCount;
            vDblAmount = totalChargebackTransAmount;
            vDblTotal = (vCntCounter * commissionVO.getCommissionValue());

            wlPartnerCommissionDetailsVO.setCount(vCntCounter);
            wlPartnerCommissionDetailsVO.setAmount(vDblAmount);
            wlPartnerCommissionDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
        }
        else if (Charge_category.Failure.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeMasterVO.getSubKeyword()))
        {
            //Declined Fee
            vCntCounter = authfailedTransCount;
            vDblAmount = totalAuthfailedTransAmount;
            vDblTotal = (vCntCounter * commissionVO.getCommissionValue());

            wlPartnerCommissionDetailsVO.setCount(vCntCounter);
            wlPartnerCommissionDetailsVO.setAmount(vDblAmount);
            wlPartnerCommissionDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
        }
        else if (Charge_category.Others.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.AuthStarted.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeMasterVO.getSubKeyword()))
        {
            //Authstarted Fee
            vCntCounter = authstartedTransCount;
            vDblAmount = totalAuthstartedTransAmount;
            vDblTotal = (vCntCounter * commissionVO.getCommissionValue());

            wlPartnerCommissionDetailsVO.setCount(vCntCounter);
            wlPartnerCommissionDetailsVO.setAmount(vDblAmount);
            wlPartnerCommissionDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
        }
        else if (Charge_category.Others.toString().equals(chargeMasterVO.getCategory()) && Charge_keyword.Total.toString().equals(chargeMasterVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeMasterVO.getSubKeyword()))
        {
            //Fraud Service Fee
            vCntCounter = totalSaleCount + authfailedTransCount;
            vDblAmount = totalSaleAmount + totalAuthfailedTransAmount;
            vDblTotal = (vCntCounter * commissionVO.getCommissionValue());

            wlPartnerCommissionDetailsVO.setCount(vCntCounter);
            wlPartnerCommissionDetailsVO.setAmount(vDblAmount);
            wlPartnerCommissionDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));
        }
        return wlPartnerCommissionDetailsVO;
    }

    public String getGatewayTypeSetupCoveredDate(GatewayType gatewayType) throws PZDBViolationException
    {
        String lastSetupFeeDate = null;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        try
        {
            con = Database.getConnection();
            String Query = "SELECT end_date as lastsetupfeedate FROM wl_invoice_manager WHERE pgtype_id=? ORDER BY id DESC LIMIT 1";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, gatewayType.getPgTypeId());
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                lastSetupFeeDate = rsPayout.getString("lastsetupfeedate");
            }
        }
        catch (SQLException e)
        {
            //logger.error("SQLException while getting whitelabel partner setup fee",e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getGatewayTypeSetupCoveredDate", null, "Common", "Sql exception while connecting to iso wire manager  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            //logger.error("SystemError while getting whitelabel partner setup fee",systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getGatewayTypeSetupCoveredDate()", null, "Common", "Sql exception while connecting to iso wire manager table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return lastSetupFeeDate;
    }

    public String getGatewayTypeSetupCoveredDate(String partnerId) throws PZDBViolationException
    {
        String lastSetupFeeDate = null;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        try
        {
            con = Database.getConnection();
            String Query = "SELECT end_date as lastsetupfeedate FROM wl_invoice_manager WHERE partner_id=? ORDER BY id DESC LIMIT 1";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, partnerId);
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                lastSetupFeeDate = rsPayout.getString("lastsetupfeedate");
            }
        }
        catch (SQLException e)
        {
            //logger.error("SQLException while getting whitelabel partner setup fee",e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getGatewayTypeSetupCoveredDate", null, "Common", "Sql exception while connecting to iso wire manager  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            //logger.error("SystemError while getting whitelabel partner setup fee",systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getGatewayTypeSetupCoveredDate()", null, "Common", "Sql exception while connecting to iso wire manager table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return lastSetupFeeDate;
    }

    public String getGatewayTypeSetupStartDate(String partnerId) throws PZDBViolationException
    {
        String lastSetupStartDate = null;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout = null;
        try
        {
            con = Database.getConnection();
            String Query = "SELECT start_date FROM wl_invoice_manager WHERE partner_id=? ORDER BY id ASC LIMIT 1";
            preparedStatement = con.prepareStatement(Query);
            preparedStatement.setString(1, partnerId);
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                lastSetupStartDate = rsPayout.getString("start_date");
            }
        }
        catch (SQLException e)
        {
            //logger.error("SQLException while getting whitelabel partner setup fee",e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getGatewayTypeSetupStartDate", null, "Common", "Sql exception while connecting to iso wire manager  table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            //logger.error("SystemError while getting whitelabel partner setup fee",systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getGatewayTypeSetupStartDate()", null, "Common", "Sql exception while connecting to iso wire manager table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return lastSetupStartDate;
    }

    public String getMemberFirstTransactionDateOnGatewayType(GatewayType gatewayType) throws PZDBViolationException
    {
        String tableName = Database.getTableName(gatewayType.getGateway());
        String strQuery = null;
        String memberFirstTransactionDate = null;
        ResultSet rsPayout = null;
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            strQuery = "SELECT MIN(trackingid),FROM_UNIXTIME(dtstamp) AS 'FirstTransDate' FROM " + tableName + " WHERE fromtype=?";
            PreparedStatement preparedStatement = conn.prepareStatement(strQuery);
            preparedStatement.setString(1, gatewayType.getGateway());
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                memberFirstTransactionDate = rsPayout.getString("FirstTransDate");
            }
        }
        catch (SystemError systemError)
        {
            //logger.error("SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException(PayoutDAO.class.getName(), "getMemberFirstTransactionDateOnGatewayType", null, "Common", "SystemError while getting first transaction date on main transaction tables", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            //logger.error("SQL Exception while collect gateway account first transaction date", e);
            PZExceptionHandler.raiseDBViolationException(PayoutDAO.class.getName(), "getMemberFirstTransactionDateOnGatewayType", null, "Common", "SqlException due to incorrect query on main transaction tables", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return memberFirstTransactionDate;
    }

    public String createWLPartnerReportFile(WLPartnerCommissionReportVO wlPartnerCommissionReportVO)
    {
        PartnerDetailsVO partnerDetailsVO = wlPartnerCommissionReportVO.getPartnerDetailsVO();

        String reportingCurrency = wlPartnerCommissionReportVO.getCurrency();
        String invoiceDate = wlPartnerCommissionReportVO.getInvoiceDate();
        String invoiceNumber = wlPartnerCommissionReportVO.getInvoiceNumber();
        String pspName = partnerDetailsVO.getHostUrl();
        String customerName = partnerDetailsVO.getCompanyName();
        String contactName = partnerDetailsVO.getContactPerson();
        String startPeriod = wlPartnerCommissionReportVO.getStartPeriod();
        String endPeriod = wlPartnerCommissionReportVO.getEndPeriod();
        String address = partnerDetailsVO.getAddress();
        String telephoneNumber = partnerDetailsVO.getTelno();
        String processorPartnerId = partnerDetailsVO.getProcessorPartnerId();

        Functions functions = new Functions();
        if (!functions.isValueNull(customerName))
        {
            customerName = "";
        }
        if (!functions.isValueNull(contactName))
        {
            contactName = "";
        }
        if (!functions.isValueNull(address))
        {
            address = "";
        }
        if (!functions.isValueNull(telephoneNumber))
        {
            telephoneNumber = "";
        }

        double totalCommissionAmount = 0.00;
        double totalPreviousAmount = wlPartnerCommissionReportVO.getPreviousBalanceAmount();
        double netFinalCommissionAmount = wlPartnerCommissionReportVO.getNetFinalFeeAmount();
        double otherTotalCommissionAmount = wlPartnerCommissionReportVO.getOtherFeeAmount();
        double transactionFee = wlPartnerCommissionReportVO.getTotalTransactionFee();

        HashMap<String, WLPartnerCommissionDetailsVO> otherFeeHashMap = wlPartnerCommissionReportVO.getOtherFeeHashMap();
        HashMap<String, ServiceTaxChargeVO> serviceTaxChargeVOHashMapFinal = wlPartnerCommissionReportVO.getServiceTaxChargeVOHashMapFinal();
        List<WLPartnerPerBankCommissionReportVO> wlPartnerPerBankCommissionReportVOs = wlPartnerCommissionReportVO.getWlPartnerPerBankCommissionReportVOList();

        double transactionFeeAmount = 0.00;
        //double monthlyMinimumFee=0.00;
        Document document = new Document(PageSize.A3, 50, 50, 50, 50);
        String reportFileName = wlPartnerCommissionReportVO.getInvoiceNumber();
        try
        {
            reportFileName = wlPartnerCommissionReportVO.getInvoiceNumber() + ".pdf";
            File filePath = new File(WL_PARTNER_INVOICE_FILE_PATH + reportFileName);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));

            //Getting all admin details
            String adminId = "1";

            if (functions.isValueNull(processorPartnerId))
            {
                adminId = partnerDetailsVO.getProcessorPartnerId();
            }

            PartnerManager partnerManager = new PartnerManager();
            PartnerDetailsVO adminInfoVO = partnerManager.getPartnerDetails(adminId);
            String backgroundColor = adminInfoVO.getReportFileBGColor();
            String adminAddress = adminInfoVO.getAddress();
            String adminCompanySupportMailId = adminInfoVO.getSupportMailId();
            String adminTelNo = adminInfoVO.getTelno();

            if (functions.isValueNull(adminAddress) && functions.isValueNull(adminCompanySupportMailId) && functions.isValueNull(adminTelNo))
            {
                Phrase phraseLine1 = new Phrase("Office Address: 69, 3rd Floor,Aditya Industrial Estate, Mindspace, Malad West, Mumbai, Maharashtra-400064.");
                Phrase phraseLine2 = new Phrase(" Telephone No. : 022-4014-3344 | Website: www.pz.com");
                writer.setBoxSize("art", writer.getPageSize());
                HeaderFooterPageEvent event = new HeaderFooterPageEvent(phraseLine1, phraseLine2);
                writer.setPageEvent(event);
            }

            document.open();

            Color bgColor = Color.gray.brighter();
            if ("Blue".equalsIgnoreCase(backgroundColor))
            {
                bgColor = new Color(0, 127, 255);
            }
            else if ("Orange".equalsIgnoreCase(backgroundColor))
            {
                bgColor = new Color(245, 130, 42);
            }

            String logoName = "pz_black_logo.png";
            if (functions.isValueNull(adminInfoVO.getLogoName()))
            {
                logoName = adminInfoVO.getLogoName();
            }

            Font f1 = FontFactory.getFont(FontFactory.TIMES_ROMAN, 20);
            f1.setColor(Color.BLACK);

            Font f2 = FontFactory.getFont(FontFactory.TIMES_BOLD, 15);
            f2.setColor(Color.WHITE);

            Font f3 = FontFactory.getFont(FontFactory.TIMES_BOLD, 15);
            f3.setColor(Color.BLACK);

            Image imageInstance = Image.getInstance(PARTNER_LOGO_PATH + logoName);
            /*imageInstance.scaleAbsolute(150, 150f);
            imageInstance.scaleAbsoluteHeight(50f);*/

            //Adjust colspan

            int totalCommissionValueTest = 0;
            for (WLPartnerPerBankCommissionReportVO wlPartnerPerBankCommissionReportVO : wlPartnerPerBankCommissionReportVOs)
            {
                HashMap<String, WLPartnerCommissionDetailsVO> commissionDetailsVOHashMap = wlPartnerPerBankCommissionReportVO.getTransactionFeeHashMap();
                Set set = commissionDetailsVOHashMap.keySet();
                Iterator iterator = set.iterator();
                while (iterator.hasNext())
                {
                    String commissionName = (String) iterator.next();
                    WLPartnerCommissionDetailsVO wlPartnerCommissionDetailsVO = commissionDetailsVOHashMap.get(commissionName);
                    totalCommissionAmount = totalCommissionAmount + wlPartnerCommissionDetailsVO.getAmount();
                    if(totalCommissionAmount >0)
                    {
                        totalCommissionValueTest = 1;
                        break;
                    }
                }
            }
            Table table = new Table(10);
            table.setWidth(100);
            table.setBorder(Table.NO_BORDER);

            table.setBorderColor(new Color(0, 0, 0));
            table.setPadding(1);

            Cell logoCell = new Cell(imageInstance);
            Cell companyName = new Cell(new Paragraph(20, " INVOICE", f1));
            //Cell companyName = new Cell("                                                                                      INVOICE");
//            Cell name = new Cell((new Paragraph(18, "Payment Gateway Solutions Pvt. Ltd.", f3)));
            Cell emptycell = new Cell((new Paragraph(15, "", f3)));

            logoCell.setColspan(4);
            companyName.setColspan(6);
//            name.setColspan(5);
            emptycell.setColspan(5);
            logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            companyName.setHorizontalAlignment(Element.ALIGN_CENTER);
//            name.setHorizontalAlignment(Element.ALIGN_LEFT);
            emptycell.setHorizontalAlignment(Element.ALIGN_RIGHT);

            logoCell.setBorder(Cell.NO_BORDER);
            companyName.setBorder(Cell.NO_BORDER);
//            name.setBorder(Cell.NO_BORDER);
            emptycell.setBorder(Cell.NO_BORDER);

            table.addCell(logoCell);
            table.addCell(companyName);
//            table.addCell(name);
            table.addCell(emptycell);
            table.addCell(emptycell);
            table.addCell(emptycell);
            table.addCell(emptycell);

            //heading
            Cell invoiceDetails = new Cell(new Paragraph(20, "Invoice Details", f3));
            invoiceDetails.setColspan(5);
            invoiceDetails.setHorizontalAlignment(Element.ALIGN_CENTER);
            invoiceDetails.setVerticalAlignment(Element.ALIGN_TOP);
            invoiceDetails.setBackgroundColor(bgColor);
            table.addCell(invoiceDetails);

            Cell customerDetails = new Cell(new Paragraph(20, "Customer Details", f3));
            customerDetails.setColspan(5);
            customerDetails.setHorizontalAlignment(Element.ALIGN_CENTER);
            customerDetails.setBackgroundColor(bgColor);
            table.addCell(customerDetails);

            //first row
            Cell invoiceNumberLabel = new Cell("Invoice Id: " + invoiceNumber);
            invoiceNumberLabel.setColspan(5);
            invoiceNumberLabel.setHorizontalAlignment(Element.ALIGN_LEFT);
            invoiceNumberLabel.setBackgroundColor(Color.WHITE);
            table.addCell(invoiceNumberLabel);

            Cell nameLabel = new Cell("Customer Name: " + customerName);
            nameLabel.setColspan(5);
            nameLabel.setHorizontalAlignment(Element.ALIGN_LEFT);
            nameLabel.setBackgroundColor(Color.WHITE);
            table.addCell(nameLabel);

            //second row
            Cell invoiceDateLabel = new Cell("Invoice Date: " + invoiceDate);
            invoiceDateLabel.setColspan(5);
            invoiceDateLabel.setHorizontalAlignment(Element.ALIGN_LEFT);
            invoiceDateLabel.setBackgroundColor(Color.WHITE);
            table.addCell(invoiceDateLabel);

            Cell typeLabel = new Cell("Customer Type: " + "PSP");
            typeLabel.setColspan(5);
            typeLabel.setHorizontalAlignment(Element.ALIGN_LEFT);
            typeLabel.setBackgroundColor(Color.WHITE);
            table.addCell(typeLabel);

            //third row
            Cell pspNameLabel = new Cell("PSP Name: " + pspName);
            pspNameLabel.setColspan(5);
            pspNameLabel.setHorizontalAlignment(Element.ALIGN_LEFT);
            pspNameLabel.setBackgroundColor(Color.WHITE);
            table.addCell(pspNameLabel);

            Cell contactPersonLabel = new Cell("Contact Name: " + contactName);
            contactPersonLabel.setColspan(5);
            contactPersonLabel.setHorizontalAlignment(Element.ALIGN_LEFT);
            contactPersonLabel.setBackgroundColor(Color.WHITE);
            table.addCell(contactPersonLabel);

            //forth row
            Cell periodLabel = new Cell("Period: " + startPeriod + " to " + endPeriod);
            periodLabel.setColspan(5);
            periodLabel.setHorizontalAlignment(Element.ALIGN_LEFT);
            periodLabel.setBackgroundColor(Color.WHITE);
            table.addCell(periodLabel);

            Cell addressLabel = new Cell("Address: " + address);
            addressLabel.setColspan(5);
            addressLabel.setHorizontalAlignment(Element.ALIGN_LEFT);
            addressLabel.setBackgroundColor(Color.WHITE);
            table.addCell(addressLabel);

            //fifth row
            Cell currencyLabel = new Cell("Currency: " + reportingCurrency);
            currencyLabel.setColspan(5);
            currencyLabel.setHorizontalAlignment(Element.ALIGN_LEFT);
            currencyLabel.setBackgroundColor(Color.WHITE);
            table.addCell(currencyLabel);

            Cell bankNameLabel = new Cell("Telephone Number: " + telephoneNumber);
            bankNameLabel.setColspan(5);
            bankNameLabel.setHorizontalAlignment(Element.ALIGN_LEFT);
            bankNameLabel.setBackgroundColor(Color.WHITE);
            table.addCell(bankNameLabel);
            //six cell
            Cell getgstlable = new Cell("GST IN: 27AAECP5135G1ZH ");
            getgstlable.setColspan(5);
            getgstlable.setHorizontalAlignment(Element.ALIGN_LEFT);
            getgstlable.setBackgroundColor(Color.WHITE);
            table.addCell(getgstlable);

            Cell emptycellone = new Cell("");
            emptycellone.setColspan(5);
            emptycellone.setHorizontalAlignment(Element.ALIGN_LEFT);
            emptycellone.setBackgroundColor(Color.WHITE);
            table.addCell(emptycellone);
            //seven cell
            Cell getlustno = new Cell("LUT NO: AD270820003263J ");
            getlustno.setColspan(5);
            getlustno.setHorizontalAlignment(Element.ALIGN_LEFT);
            getlustno.setBackgroundColor(Color.WHITE);
            table.addCell(getlustno);

            Cell emptycelltwo = new Cell("");
            emptycelltwo.setColspan(5);
            emptycelltwo.setHorizontalAlignment(Element.ALIGN_LEFT);
            emptycelltwo.setBackgroundColor(Color.WHITE);
            table.addCell(emptycelltwo);

            //PZ Transaction Fees
            Cell feesLabel = new Cell(new Paragraph(20, "PZ Transactions Fee", f3));
            feesLabel.setColspan(10);
            feesLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            feesLabel.setBackgroundColor(bgColor);
            table.addCell(feesLabel);


            Cell feeNameLabel = new Cell("Fee Name");
            Cell processorNameLabel = new Cell("Processor");
            Cell processingCurrencyNameLabel = new Cell("Currency");
            Cell rateLabel = new Cell("Rate/Fee");
            Cell counterLabel = new Cell("Counter");
            Cell amountLabel = new Cell("Amount");
            Cell totalLabel = new Cell("Total");
            Cell lineTotalLabel = new Cell("Line Total");

            if (totalCommissionValueTest==1){
                feeNameLabel.setColspan(3);
            }else{feeNameLabel.setColspan(4);}

            processorNameLabel.setColspan(1);
            processingCurrencyNameLabel.setColspan(1);
            rateLabel.setColspan(1);
            counterLabel.setColspan(1);
            if (totalCommissionValueTest==1){
            amountLabel.setColspan(1);
            }
            totalLabel.setColspan(1);
            lineTotalLabel.setColspan(1);

            //feeNameLabel.setBackgroundColor(Color.LIGHT_GRAY);
            //processorNameLabel.setBackgroundColor(Color.LIGHT_GRAY);
            //processingCurrencyNameLabel.setBackgroundColor(Color.LIGHT_GRAY);
            //rateLabel.setBackgroundColor(Color.LIGHT_GRAY);
            //counterLabel.setBackgroundColor(Color.LIGHT_GRAY);
            //amountLabel.setBackgroundColor(Color.LIGHT_GRAY);
            //totalLabel.setBackgroundColor(Color.LIGHT_GRAY);
            //lineTotalLabel.setBackgroundColor(Color.LIGHT_GRAY);

            feeNameLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            processorNameLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            processingCurrencyNameLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            rateLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            //rateLabel.setVerticalAlignment(Element.ALIGN_CENTER);
            counterLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            amountLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            totalLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            lineTotalLabel.setHorizontalAlignment(Element.ALIGN_CENTER);

            table.addCell(feeNameLabel);
            table.addCell(processorNameLabel);
            table.addCell(processingCurrencyNameLabel);
            table.addCell(rateLabel);
            table.addCell(counterLabel);
            if(totalCommissionValueTest == 1)
            {
                table.addCell(amountLabel);
            }
            table.addCell(totalLabel);
            table.addCell(lineTotalLabel);

            Cell commissionNameValue, processorNameValue, processingCurrencyNameValue, commissionRateValue, commissionCounterValue, commissionAmountValue, commissionTotalValue, commissionLineTotalValue;

            //Values
            if (wlPartnerPerBankCommissionReportVOs.size() > 0)
            {
                for (WLPartnerPerBankCommissionReportVO wlPartnerPerBankCommissionReportVO : wlPartnerPerBankCommissionReportVOs)
                {
                    String bankName = wlPartnerPerBankCommissionReportVO.getGatewayType().getName();
                    String processingCurrency = wlPartnerPerBankCommissionReportVO.getProcessingCurrency();
                    totalCommissionAmount = totalCommissionAmount + wlPartnerPerBankCommissionReportVO.getTransactionFeeAmount();

                    HashMap<String, WLPartnerCommissionDetailsVO> commissionDetailsVOHashMap = wlPartnerPerBankCommissionReportVO.getTransactionFeeHashMap();
                    Set set = commissionDetailsVOHashMap.keySet();
                    Iterator iterator = set.iterator();
                    while (iterator.hasNext())
                    {
                        String commissionName = (String) iterator.next();
                        WLPartnerCommissionDetailsVO wlPartnerCommissionDetailsVO = commissionDetailsVOHashMap.get(commissionName);

                        String vCntCounter = (new Long(wlPartnerCommissionDetailsVO.getCount())).toString();

                        commissionNameValue = new Cell(wlPartnerCommissionDetailsVO.getChargeName());
                        processorNameValue = new Cell(bankName);
                        processingCurrencyNameValue = new Cell(processingCurrency);

                        commissionTotalValue = new Cell(Functions.round(wlPartnerCommissionDetailsVO.getTotal(), 2));
                        commissionLineTotalValue = new Cell(Functions.round(wlPartnerCommissionDetailsVO.getTotal(), 2));

                        if (Charge_unit.Percentage.toString().equals(wlPartnerCommissionDetailsVO.getValueType()))
                        {
                            commissionRateValue = new Cell(Double.valueOf(wlPartnerCommissionDetailsVO.getChargeValue()) + "%");
                        }
                        else
                        {
                            commissionRateValue = new Cell(Double.valueOf(wlPartnerCommissionDetailsVO.getChargeValue()) + "");
                        }

                        commissionCounterValue = new Cell(vCntCounter);
                        commissionAmountValue = new Cell(Functions.round(wlPartnerCommissionDetailsVO.getAmount(), 2));
                        if (totalCommissionValueTest==1){
                        commissionNameValue.setColspan(3);}
                        else{
                            commissionNameValue.setColspan(4);
                        }
                        processorNameValue.setColspan(1);
                        processingCurrencyNameValue.setColspan(1);
                        commissionRateValue.setColspan(1);
                        commissionCounterValue.setColspan(1);
                        if (totalCommissionValueTest==1){
                        commissionAmountValue.setColspan(1);}
                        commissionTotalValue.setColspan(1);
                        commissionLineTotalValue.setColspan(1);

                        processorNameValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                        processingCurrencyNameValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                        commissionRateValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        commissionCounterValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        commissionAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        commissionTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        commissionLineTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

                        table.addCell(commissionNameValue);
                        table.addCell(processorNameValue);
                        table.addCell(processingCurrencyNameValue);
                        table.addCell(commissionRateValue);
                        table.addCell(commissionCounterValue);
                        log.error("commissionAmountValue--------------"+commissionAmountValue);
                        if(totalCommissionValueTest == 1)
                        {
                            table.addCell(commissionAmountValue);
                        }
                        table.addCell(commissionTotalValue);
                        table.addCell(commissionLineTotalValue);
                    }
                    //total
                    Cell totalNameLabel = new Cell("");
                    totalNameLabel.setColspan(9);
                    totalNameLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    totalNameLabel.setBackgroundColor(Color.WHITE);
                    table.addCell(totalNameLabel);

                    Cell grandTotalValue = new Cell(Functions.round(wlPartnerPerBankCommissionReportVO.getTransactionFeeAmount(), 2));
                    grandTotalValue.setColspan(1);
                    grandTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(grandTotalValue);
                }
            }
           /* else
            {
                //No Transaction Fee
                Cell noTransactionFeeNameLabel = new Cell("No Transaction Fee");
                noTransactionFeeNameLabel.setColspan(10);
                noTransactionFeeNameLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
                noTransactionFeeNameLabel.setBackgroundColor(Color.WHITE);
                table.addCell(noTransactionFeeNameLabel);

                //total
                Cell totalNameLabel = new Cell("");
                totalNameLabel.setColspan(9);
                totalNameLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
                totalNameLabel.setBackgroundColor(Color.WHITE);
                table.addCell(totalNameLabel);

                Cell grandTotalValue = new Cell("00.00");
                grandTotalValue.setColspan(1);
                grandTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(grandTotalValue);
                System.out.println("grandTotalValue:::"+grandTotalValue);
            }*/

            //Other Fees
            Cell totalAmtFundedLabel = new Cell(new Paragraph(20, "Other Fees", f3));
            totalAmtFundedLabel.setColspan(10);
            totalAmtFundedLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            totalAmtFundedLabel.setBackgroundColor(bgColor);
            table.addCell(totalAmtFundedLabel);

            Cell commissionNameValue1, commissionRateValue1, commissionCounterValue1, commissionAmountValue1, commissionTotalValue1, commissionLineTotalValue1;

            //Values
            Set set1 = otherFeeHashMap.keySet();
            if (set1.size() > 0 || functions.isValueNull(wlPartnerCommissionReportVO.getRandomChargeName()))
            {
                Iterator iterator1 = set1.iterator();
                while (iterator1.hasNext())
                {
                    String commissionName = (String) iterator1.next();
                    WLPartnerCommissionDetailsVO wlPartnerCommissionDetailsVO = otherFeeHashMap.get(commissionName);

                    String vCntCounter = (new Long(wlPartnerCommissionDetailsVO.getCount())).toString();

                    commissionNameValue1 = new Cell(wlPartnerCommissionDetailsVO.getChargeName());
                    commissionTotalValue1 = new Cell(Functions.round(wlPartnerCommissionDetailsVO.getTotal(), 2));
                    commissionLineTotalValue1 = new Cell(Functions.round(wlPartnerCommissionDetailsVO.getTotal(), 2));

                    if (Charge_unit.Percentage.toString().equals(wlPartnerCommissionDetailsVO.getValueType()))
                    {
                        commissionRateValue1 = new Cell(Functions.round(Double.valueOf(wlPartnerCommissionDetailsVO.getChargeValue()), 2) + "%");
                    }
                    else
                    {
                        commissionRateValue1 = new Cell(Functions.round(Double.valueOf(wlPartnerCommissionDetailsVO.getChargeValue()), 2));
                    }

                    /*if(monthlyMinimumFeeAmount > transactionFeeAmount){
                        otherTotalCommissionAmount = monthlyMinimumFeeAmount - transactionFeeAmount;
                    }
                    else{
                        otherTotalCommissionAmount = monthlyMinimumFeeAmount;
                    }*/

                    commissionCounterValue1 = new Cell(vCntCounter);
                    commissionAmountValue1 = new Cell(Functions.round(wlPartnerCommissionDetailsVO.getAmount(), 2));
                    commissionNameValue1.setColspan(5);
                    commissionRateValue1.setColspan(1);
                    commissionCounterValue1.setColspan(1);
                    commissionAmountValue1.setColspan(1);
                    commissionTotalValue1.setColspan(1);
                    commissionLineTotalValue1.setColspan(1);

                    commissionRateValue1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    commissionCounterValue1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    commissionAmountValue1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    commissionTotalValue1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    commissionLineTotalValue1.setHorizontalAlignment(Element.ALIGN_RIGHT);

                    table.addCell(commissionNameValue1);
                    table.addCell(commissionRateValue1);
                    table.addCell(commissionCounterValue1);
                    table.addCell(commissionAmountValue1);
                    table.addCell(commissionTotalValue1);
                    table.addCell(commissionLineTotalValue1);
                }
                if (functions.isValueNull(wlPartnerCommissionReportVO.getRandomChargeName()))
                {
                    Cell randomChargeNameLabel = new Cell(wlPartnerCommissionReportVO.getRandomChargeName());
                    randomChargeNameLabel.setColspan(9);
                    randomChargeNameLabel.setHorizontalAlignment(Element.ALIGN_LEFT);
                    randomChargeNameLabel.setBackgroundColor(Color.WHITE);
                    table.addCell(randomChargeNameLabel);

                    Cell randomChargeValue = new Cell(Functions.round(wlPartnerCommissionReportVO.getRandomChargeValue(), 2));
                    randomChargeValue.setColspan(1);
                    randomChargeValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                /*randomChargeValue.setBackgroundColor(Color.LIGHT_GRAY);*/
                    table.addCell(randomChargeValue);
                }

            }
            else
            {
                Cell noOtherFeeNameLabel = new Cell("No Other Fee");
                noOtherFeeNameLabel.setColspan(10);
                noOtherFeeNameLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
                noOtherFeeNameLabel.setBackgroundColor(Color.WHITE);
                table.addCell(noOtherFeeNameLabel);
            }

            //Other Fees Total Value
            Cell totalNameLabel1 = new Cell("");
            totalNameLabel1.setColspan(9);
            totalNameLabel1.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalNameLabel1.setBackgroundColor(Color.WHITE);
            table.addCell(totalNameLabel1);

            Cell grandtotalValue1 = new Cell(Functions.round(wlPartnerCommissionReportVO.getOtherFeeAmount(), 2));
            grandtotalValue1.setColspan(1);
            grandtotalValue1.setHorizontalAlignment(Element.ALIGN_RIGHT);
            //grandtotalValue1.setBackgroundColor(Color.LIGHT_GRAY);
            table.addCell(grandtotalValue1);

            //Gross Summary
            Cell miscellaneousLabel = new Cell("Gross Summary");
            Cell feesTotalNameLabel = new Cell("Fees Total");
            Cell currencyMultiplierLabel = new Cell("Multiplier");
            Cell lineTotalLabel1 = new Cell("Line Total");

            miscellaneousLabel.setColspan(7);
            feesTotalNameLabel.setColspan(1);
            currencyMultiplierLabel.setColspan(1);
            lineTotalLabel1.setColspan(1);

            miscellaneousLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            feesTotalNameLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            currencyMultiplierLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            lineTotalLabel1.setHorizontalAlignment(Element.ALIGN_CENTER);

            //miscellaneousLabel.setBackgroundColor(Color.LIGHT_GRAY);
            //feesTotalNameLabel.setBackgroundColor(Color.LIGHT_GRAY);
            //currencyMultiplierLabel.setBackgroundColor(Color.LIGHT_GRAY);
            //lineTotalLabel1.setBackgroundColor(Color.LIGHT_GRAY);

            table.addCell(miscellaneousLabel);
            table.addCell(feesTotalNameLabel);
            table.addCell(currencyMultiplierLabel);
            table.addCell(lineTotalLabel1);

            for (WLPartnerPerBankCommissionReportVO wlPartnerPerBankCommissionReportVO : wlPartnerCommissionReportVO.getWlPartnerPerBankCommissionReportVOList())
            {
                Cell miscellaneousLabel1 = new Cell(wlPartnerPerBankCommissionReportVO.getGatewayType().getName() + " Transactions Fees [" + wlPartnerPerBankCommissionReportVO.getGatewayType().getCurrency() + "]");
                miscellaneousLabel1.setColspan(7);
                miscellaneousLabel1.setHorizontalAlignment(Element.ALIGN_LEFT);
                miscellaneousLabel1.setBackgroundColor(Color.WHITE);
                table.addCell(miscellaneousLabel1);

                Cell miscellaneousValue1 = new Cell(Functions.round(wlPartnerPerBankCommissionReportVO.getTransactionFeeAmount(), 2));
                miscellaneousValue1.setColspan(1);
                miscellaneousValue1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                miscellaneousValue1.setBackgroundColor(Color.WHITE);
                table.addCell(miscellaneousValue1);

                Cell currencyMultiplierValue = new Cell(Functions.round(wlPartnerPerBankCommissionReportVO.getConversionRate(), 2));
                currencyMultiplierValue.setColspan(1);
                currencyMultiplierValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                currencyMultiplierValue.setBackgroundColor(Color.WHITE);
                table.addCell(currencyMultiplierValue);

                Cell lineTotalValue1 = new Cell(Functions.round(wlPartnerPerBankCommissionReportVO.getConvertedAmount(), 2));
                lineTotalValue1.setColspan(1);
                lineTotalValue1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                lineTotalValue1.setBackgroundColor(Color.WHITE);
                table.addCell(lineTotalValue1);
            }

            /*added transaction fee*/
            Cell miscellaneousLabel1 = new Cell("Transaction Fee [" + reportingCurrency + "]");
            miscellaneousLabel1.setColspan(9);
            miscellaneousLabel1.setHorizontalAlignment(Element.ALIGN_LEFT);
            miscellaneousLabel1.setBackgroundColor(Color.WHITE);
            table.addCell(miscellaneousLabel1);

            Cell miscellaneousValue1 = new Cell(Functions.round(transactionFee, 2));
            miscellaneousValue1.setColspan(1);
            miscellaneousValue1.setHorizontalAlignment(Element.ALIGN_RIGHT);
            miscellaneousValue1.setBackgroundColor(Color.WHITE);
            table.addCell(miscellaneousValue1);


            Cell miscellaneousLabel2 = new Cell("Other Fee [" + reportingCurrency + "]");
            miscellaneousLabel2.setColspan(9);
            miscellaneousLabel2.setHorizontalAlignment(Element.ALIGN_LEFT);
            miscellaneousLabel2.setBackgroundColor(Color.WHITE);
            table.addCell(miscellaneousLabel2);

            Cell miscellaneousValue2 = new Cell(Functions.round(otherTotalCommissionAmount, 2));
            miscellaneousValue2.setColspan(1);
            miscellaneousValue2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            miscellaneousValue2.setBackgroundColor(Color.WHITE);
            table.addCell(miscellaneousValue2);

            //adding service tax:16oct2018
            Set serviceSet1 = serviceTaxChargeVOHashMapFinal.keySet();
            if (serviceSet1.size() > 0)
            {
                Iterator serviceIterator1 = serviceSet1.iterator();
                while (serviceIterator1.hasNext())
                {
                    String commissionName = (String) serviceIterator1.next();
                    ServiceTaxChargeVO serviceTaxChargeVO = serviceTaxChargeVOHashMapFinal.get(commissionName);
                    if (serviceTaxChargeVO != null)
                    {
                        commissionNameValue1 = new Cell(serviceTaxChargeVO.getChargeName());
                        commissionTotalValue1 = new Cell(Functions.round(serviceTaxChargeVO.getTotal(), 2));
                        commissionLineTotalValue1 = new Cell(Functions.round(serviceTaxChargeVO.getTotal(), 2));
                        if (Charge_unit.Percentage.toString().equals(serviceTaxChargeVO.getValueType()))
                        {
                            commissionRateValue1 = new Cell(Functions.round(Double.valueOf(serviceTaxChargeVO.getChargeValue()), 2) + "%");
                        }
                        else
                        {
                            commissionRateValue1 = new Cell(Functions.round(Double.valueOf(serviceTaxChargeVO.getChargeValue()), 2));
                        }
                        commissionCounterValue1 = new Cell("-");
                        commissionAmountValue1 = new Cell(Functions.round(serviceTaxChargeVO.getAmount(), 2));

                        commissionNameValue1.setColspan(5);
                        commissionRateValue1.setColspan(1);
                        commissionCounterValue1.setColspan(1);
                        commissionAmountValue1.setColspan(1);
                        commissionTotalValue1.setColspan(1);
                        commissionLineTotalValue1.setColspan(1);

                        commissionRateValue1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        commissionCounterValue1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        commissionAmountValue1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        commissionTotalValue1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        commissionLineTotalValue1.setHorizontalAlignment(Element.ALIGN_RIGHT);

                        table.addCell(commissionNameValue1);
                        table.addCell(commissionRateValue1);
                        table.addCell(commissionCounterValue1);
                        table.addCell(commissionAmountValue1);
                        table.addCell(commissionTotalValue1);
                        table.addCell(commissionLineTotalValue1);
                    }
                    else
                    {
                        log.debug("service charge details not found");
                    }
                }
            }
            else
            {
                log.debug("No service charges found");
            }

            Cell miscellaneousLabel3 = new Cell("Previous Balance Amount");
            miscellaneousLabel3.setColspan(9);
            miscellaneousLabel3.setHorizontalAlignment(Element.ALIGN_LEFT);
            miscellaneousLabel3.setBackgroundColor(Color.WHITE);
            table.addCell(miscellaneousLabel3);

            Cell miscellaneousValue3 = new Cell(Functions.round(totalPreviousAmount, 2));
            miscellaneousValue3.setColspan(1);
            miscellaneousValue3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            miscellaneousValue3.setBackgroundColor(Color.WHITE);
            table.addCell(miscellaneousValue3);

            //Net Final Amount

            Cell netFinalAmount = new Cell("Net Final Amt: [" + reportingCurrency + "]");
            netFinalAmount.setColspan(9);
            netFinalAmount.setHorizontalAlignment(Element.ALIGN_RIGHT);
            //netFinalAmount.setBackgroundColor(Color.LIGHT_GRAY);
            table.addCell(netFinalAmount);

            Cell netFinalAmountValue = new Cell(Functions.round(netFinalCommissionAmount, 2));
            netFinalAmountValue.setColspan(1);
            netFinalAmountValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
            //netFinalAmountValue.setBackgroundColor(Color.LIGHT_GRAY);
            table.addCell(netFinalAmountValue);

            /*Image poweredByLogoValue = Image.getInstance(PARTNER_LOGO_PATH + "poweredby_new_logo.png");
            poweredByLogoValue.scaleAbsoluteHeight(50f);

            Cell poweredByLogoLabel = new Cell("");
            Cell poweredByLogoValueCell1 = new Cell(poweredByLogoValue);

            poweredByLogoLabel.setColspan(9);
            poweredByLogoValueCell1.setColspan(1);
            poweredByLogoValueCell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
            poweredByLogoLabel.setBackgroundColor(Color.white.brighter());
            poweredByLogoValueCell1.setBackgroundColor(Color.white.brighter());

            table.addCell(poweredByLogoLabel);
            table.addCell(poweredByLogoValueCell1);*/
            //Bank details
            /*Cell emptycellthree = new Cell(new Paragraph(20,"", f2));
            emptycellthree.setColspan(10);
            emptycellthree.setHorizontalAlignment(Element.ALIGN_LEFT);
            emptycellthree.setBackgroundColor(Color.WHITE);
            table.addCell(emptycellthree);*/

            table.addCell(emptycell);
            table.addCell(emptycell);
            table.addCell(emptycell);
            table.addCell(emptycell);
            table.addCell(emptycell);
            table.addCell(emptycell);
            Cell BankDetails = new Cell(new Paragraph(20, "Bank Details", f3));
            BankDetails.setColspan(10);
            BankDetails.setHorizontalAlignment(Element.ALIGN_CENTER);
            BankDetails.setBackgroundColor(bgColor);
            table.addCell(BankDetails);

            Cell BankName = new Cell(new Paragraph("Beneficiary Bank Name : "));
            BankName.setColspan(5);
            BankName.setHorizontalAlignment(Element.ALIGN_LEFT);
            BankName.setBackgroundColor(Color.white);
            table.addCell(BankName);

            Cell BankNamevalue = new Cell(new Paragraph("Bank of Baroda"));
            BankNamevalue.setColspan(5);
            BankNamevalue.setHorizontalAlignment(Element.ALIGN_LEFT);
            BankNamevalue.setBackgroundColor(Color.white);
            table.addCell(BankNamevalue);

            Cell BankAddress = new Cell(new Paragraph("Beneficiary Bank Branch with Address : "));
            BankAddress.setColspan(5);
            BankAddress.setHorizontalAlignment(Element.ALIGN_LEFT);
            BankAddress.setBackgroundColor(Color.white);
            table.addCell(BankAddress);

            Cell BankAddressValue = new Cell(new Paragraph("Crawford Market Branch, Dada Manzil,67/69 Mohammad Ali Road, Mumbai 400003"));
            BankAddressValue.setColspan(5);
            BankAddressValue.setHorizontalAlignment(Element.ALIGN_LEFT);
            BankAddressValue.setBackgroundColor(Color.white);
            table.addCell(BankAddressValue);

            Cell AccountHolderName = new Cell(new Paragraph("Beneficiary : "));
            AccountHolderName.setColspan(5);
            AccountHolderName.setHorizontalAlignment(Element.ALIGN_LEFT);
            AccountHolderName.setBackgroundColor(Color.white);
            table.addCell(AccountHolderName);

            Cell AccountHolderNameValue = new Cell(new Paragraph("Payment Gateway Solutions Private Limited"));
            AccountHolderNameValue.setColspan(5);
            AccountHolderNameValue.setHorizontalAlignment(Element.ALIGN_LEFT);
            AccountHolderNameValue.setBackgroundColor(Color.white);
            table.addCell(AccountHolderNameValue);

            Cell AccountNo = new Cell(new Paragraph("Account No. of Beneficiary : "));
            AccountNo.setColspan(5);
            AccountNo.setHorizontalAlignment(Element.ALIGN_LEFT);
            AccountNo.setBackgroundColor(Color.white);
            table.addCell(AccountNo);

            Cell AccountNoValue = new Cell(new Paragraph("03920200001625"));
            AccountNoValue.setColspan(5);
            AccountNoValue.setHorizontalAlignment(Element.ALIGN_LEFT);
            AccountNoValue.setBackgroundColor(Color.white);
            table.addCell(AccountNoValue);

            Cell IfscNo = new Cell(new Paragraph("IFSC Code : "));
            IfscNo.setColspan(5);
            IfscNo.setHorizontalAlignment(Element.ALIGN_LEFT);
            IfscNo.setBackgroundColor(Color.white);
            table.addCell(IfscNo);

            Cell IfscNoValue = new Cell(new Paragraph("BARB0CRAWFO (5th character is zero)"));
            IfscNoValue.setColspan(5);
            IfscNoValue.setHorizontalAlignment(Element.ALIGN_LEFT);
            IfscNoValue.setBackgroundColor(Color.white);
            table.addCell(IfscNoValue);

            Cell SwiftNo = new Cell(new Paragraph("SWIFT Code : "));
            SwiftNo.setColspan(5);
            SwiftNo.setHorizontalAlignment(Element.ALIGN_LEFT);
            SwiftNo.setBackgroundColor(Color.white);
            table.addCell(SwiftNo);

            Cell SwiftNoVlaue = new Cell(new Paragraph("BARBINBBCRM"));
            SwiftNoVlaue.setColspan(5);
            SwiftNoVlaue.setHorizontalAlignment(Element.ALIGN_LEFT);
            SwiftNoVlaue.setBackgroundColor(Color.white);
            table.addCell(SwiftNoVlaue);

        /*    Cell emptycellthree = new Cell("");
            emptycellthree.setColspan(5);
            emptycellthree.setHorizontalAlignment(Element.ALIGN_LEFT);
            emptycellthree.setBackgroundColor(Color.WHITE);
            table.addCell(emptycellthree);*/

            document.add(table);
            document.close();

        }
        catch (Exception e)
        {
            log.error("Exception---->",e);
        }
        return reportFileName;
    }

    public boolean generateWhiteLabelPartnerInvoice(WLPartnerInvoiceVO wlPartnerInvoiceVO )
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean statusMsg = false;
        try
        {
            conn = Database.getConnection();
            String query = "insert into wl_invoice_manager(id,partner_id,start_date,end_date,netfinal_amount,unpaid_amount,currency,status,reportfile_path,action_executer,creation_on) values(null,?,?,?,?,?,?,?,?,?,UNIX_TIMESTAMP(NOW()))";
            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1, wlPartnerInvoiceVO.getPartnerId());
            pstmt.setString(2, wlPartnerInvoiceVO.getStartDate());
            pstmt.setString(3, wlPartnerInvoiceVO.getEndDate());
            pstmt.setDouble(4, wlPartnerInvoiceVO.getNetFinalAmount());
            pstmt.setDouble(5, wlPartnerInvoiceVO.getUnpaidAmount());
            pstmt.setString(6, wlPartnerInvoiceVO.getCurrency());
            pstmt.setString(7, wlPartnerInvoiceVO.getStatus());
            pstmt.setString(8, wlPartnerInvoiceVO.getReportFilePath());
            pstmt.setString(9, wlPartnerInvoiceVO.getActionExecutor());
            int k = pstmt.executeUpdate();
            if (k > 0)
            {
                statusMsg = true;
            }
        }
        catch (Exception e)
        {
            log.error("Exception::::::::::" + e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return statusMsg;
    }

    public boolean checkForDuplicated(WLPartnerInvoiceVO wlPartnerInvoiceVO )
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean statusMsg = false;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT partner_id,start_date,end_date FROM wl_invoice_manager WHERE partner_id=? AND start_date=? AND end_date =?";
            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1, wlPartnerInvoiceVO.getPartnerId());
            pstmt.setString(2, wlPartnerInvoiceVO.getStartDate());
            pstmt.setString(3, wlPartnerInvoiceVO.getEndDate());
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                statusMsg = true;
            }
        }
        catch (Exception e)
        {
            log.error("Exception::::::::::" + e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return statusMsg;
    }
}
