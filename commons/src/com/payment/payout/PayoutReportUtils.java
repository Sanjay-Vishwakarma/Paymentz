package com.payment.payout;

import com.directi.pg.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 7/5/14
 * Time: 3:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class PayoutReportUtils
{
    static Logger logger = new Logger(PayoutReportUtils.class.getName());

    public String getMerchantReportFileName(String sMemberId, String sAccountId, String sStartTransactionDate, String sEndTransactionDate)
    {
        String filename=null;
        try
        {
            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMddHHmmss");
            String currentSystemDate=dateFormater.format(new Date());
            String[] tempStartDate=sStartTransactionDate.split(" ");
            String[] tempEndDate=sEndTransactionDate.split(" ");
            filename="Settlement_Merchant_"+sMemberId+"_"+sAccountId+"_"+tempStartDate[0].replace("-","")+"_"+tempEndDate[0].replace("-","")+"_"+currentSystemDate;
        }
        catch (Exception e)
        {
            logger.error("Check the getMerchantReportFileName() for exception");
        }
        return filename;
    }
    public String getAgentReportFileName(String agentId, String memberId,String terminalId, String sStartTransactionDate, String sEndTransactionDate)
    {
        String filename=null;
        try
        {
            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMddHHmmss");
            String currentSystemDate=dateFormater.format(new Date());
            String[] tempStartDate=sStartTransactionDate.split(" ");
            String[] tempEndDate=sEndTransactionDate.split(" ");
            filename="Settlement_Agent_"+agentId+"_"+memberId+"_"+terminalId+"_"+tempStartDate[0].replace("-","")+"_"+tempEndDate[0].replace("-","")+"_"+currentSystemDate;
        }
        catch (Exception e)
        {
            logger.error("Check the getAgentReportFileName() for exception"+e);
        }
        return filename;
    }
    public String getBankAgentReportFileName(String agentId, String mid, String sStartTransactionDate, String sEndTransactionDate)
    {
        String filename=null;
        try
        {
            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMddHHmmss");
            String currentSystemDate=dateFormater.format(new Date());
            String[] tempStartDate=sStartTransactionDate.split(" ");
            String[] tempEndDate=sEndTransactionDate.split(" ");
            filename="Settlement_Bank_Agent_"+agentId+"_"+mid+"_"+tempStartDate[0].replace("-","")+"_"+tempEndDate[0].replace("-","")+"_"+currentSystemDate;
        }
        catch (Exception e)
        {
            logger.error("Check the getBankAgentReportFileName() for exception"+e);
        }
        return filename;
    }
    public String getBankPartnerReportFileName(String partnerId, String mid, String sStartTransactionDate, String sEndTransactionDate)
    {
        String filename=null;
        try
        {
            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMddHHmmss");
            String currentSystemDate=dateFormater.format(new Date());
            String[] tempStartDate=sStartTransactionDate.split(" ");
            String[] tempEndDate=sEndTransactionDate.split(" ");
            filename="Settlement_Bank_Partner_"+partnerId+"_"+mid+"_"+tempStartDate[0].replace("-","")+"_"+tempEndDate[0].replace("-","")+"_"+currentSystemDate;
        }
        catch (Exception e)
        {
            logger.error("Check the getBankPartnerReportFileName() for exception"+e);
        }
        return filename;
    }
    public String getPartnerReportFileName(String partnerId, String memberId,String terminalId, String sStartTransactionDate, String sEndTransactionDate)
    {
        String filename=null;
        try
        {
            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMddHHmmss");
            String currentSystemDate=dateFormater.format(new Date());
            String[] tempStartDate=sStartTransactionDate.split(" ");
            String[] tempEndDate=sEndTransactionDate.split(" ");
            filename="Settlement_Partner_"+partnerId+"_"+memberId+"_"+terminalId+"_"+tempStartDate[0].replace("-","")+"_"+tempEndDate[0].replace("-","")+"_"+currentSystemDate;
        }
        catch (Exception e)
        {
            logger.error("Check the getPartnerReportFileName() for exception"+e);
        }
        return filename;
    }
    public String getISOCommissionReportFileName(String bankName,String MID,String startDate, String endDate)
    {
        String filename=null;
        try
        {
            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMddHHmmss");
            String currentSystemDate=dateFormater.format(new Date());
            String[] tempStartDate=startDate.split(" ");
            String[] tempEndDate=endDate.split(" ");
            filename=bankName+"_"+MID+"_"+tempStartDate[0].replace("-","")+"_"+tempEndDate[0].replace("-","")+"_"+currentSystemDate;
        }
        catch (Exception e)
        {
            logger.error("Check the getISOCommissionReportFileName() for exception"+e);
        }
        return filename;
    }

    public String getWLCommissionReportFileName(String partnerId, String bankName, String startDate, String endDate)
    {
        String filename = null;
        try
        {
            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMddHHmmss");
            String currentSystemDate = dateFormater.format(new Date());
            String[] tempStartDate = startDate.split(" ");
            String[] tempEndDate = endDate.split(" ");
            filename = partnerId + "_" + bankName + "_" + tempStartDate[0].replace("-", "") + "_" + tempEndDate[0].replace("-", "") + "_" + currentSystemDate;
        }
        catch (Exception e)
        {
            logger.error("Check the getWLCommissionReportFileName() for exception" + e);
        }
        return filename;
    }
}
