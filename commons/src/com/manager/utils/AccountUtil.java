package com.manager.utils;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 8/2/14
 * Time: 4:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class AccountUtil
{
    static Logger logger = new Logger(AccountUtil.class.getName());
    public String getTableNameFromAccountId(String accountId)
    {
        GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
        GatewayType gatewayType = GatewayTypeService.getGatewayType(account.getPgTypeId());
        String tableName = Database.getTableName(gatewayType.getGateway());
        return tableName;
    }
    public String getTableNameSettlement(String accountId)
    {
        GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
        GatewayType gatewayType = GatewayTypeService.getGatewayType(account.getPgTypeId());
        String tableName = Database.getTableNameForSettlement(gatewayType.getGateway());
        return tableName;
    }
    public String getReportFileName(String sMemberId,String sAccountId,String terminalId,String sStartTransactionDate,String sEndTransactionDate)
    {
        String filename=null;
        try
        {
            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMddHHmmss");
            String currentSystemDate=dateFormater.format(new Date());
            String[] tempStartDate=sStartTransactionDate.split(" ");
            String[] tempEndDate=sEndTransactionDate.split(" ");
            filename="Settlement_Merchant_"+sMemberId+"_"+sAccountId+"_"+terminalId+"_"+tempStartDate[0].replace("-","")+"_"+tempEndDate[0].replace("-","")+"_"+currentSystemDate;
        }
        catch (Exception e)
        {
            logger.error("Check the getReportFileName() for exception");
        }
        return filename;
    }
    public String getConsolidatedReportName(String sMemberId,String sAccountId,String sStartTransactionDate,String sEndTransactionDate)
    {
        String filename=null;
        try
        {
            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMddHHmmss");
            String currentSystemDate=dateFormater.format(new Date());
            String[] tempStartDate=sStartTransactionDate.split(" ");
            String[] tempEndDate=sEndTransactionDate.split(" ");
            filename="Consolidated_Settlement_Merchant_"+sMemberId+"_"+tempStartDate[0].replace("-","")+"_"+tempEndDate[0].replace("-","")+"_"+currentSystemDate;
        }
        catch (Exception e)
        {
            logger.error("Check the getReportFileName() for exception");
        }
        return filename;
    }

    public String convert2Decimal(String strAmount)
    {

        BigDecimal amount = new BigDecimal(strAmount);
        amount = amount.setScale(2, BigDecimal.ROUND_DOWN); //as amount value in database was round down by mysql while inserting in php page
        return amount.toString();

    }
    public boolean isValueNull(String str)
    {
        if(str != null && !str.equals("null") && !str.equals(""))
        {
            return true;
        }
        return false;
    }

    //conversion of dtStamp to Date(yyyy-MM-dd HH:mm:ss)
    public  String new_convertDtStampToDate(String str)
    {
        String dt = null;
        SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            long longdt = Long.parseLong(str);

            dt =simpleDateFormat.format(new java.util.Date(((long) longdt) * 1000));

        }
        catch (NumberFormatException ne)
        {
        }
        if (dt == null) dt = "";
        return dt;
    }

}
