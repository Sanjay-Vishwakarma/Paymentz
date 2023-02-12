package com.manager.vo.payoutVOs;

import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayType;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 2/10/15
 * Time: 5:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class BankPartnerPayoutReportVO extends PartnerPayoutReportVO
{
    GatewayType gatewayType;
    GatewayAccount gatewayAccount;

    double INR_CHARGE_AMOUNT=0.00;
    double USD_CHARGE_AMOUNT=0.00;

    double EUR_CHARGE_AMOUNT=0.00;
    double GBP_CHARGE_AMOUNT=0.00;
    double JPY_CHARGE_AMOUNT=0.00;

    boolean isINRAccount;
    boolean isUSDAccount;
    boolean isEURAccount;
    boolean isGBPAccount;
    boolean isJPYAccount;

    public GatewayType getGatewayType()
    {
        return gatewayType;
    }

    public void setGatewayType(GatewayType gatewayType)
    {
        this.gatewayType = gatewayType;
    }

    public GatewayAccount getGatewayAccount()
    {
        return gatewayAccount;
    }

    public void setGatewayAccount(GatewayAccount gatewayAccount)
    {
        this.gatewayAccount = gatewayAccount;
    }

    public double getINR_CHARGE_AMOUNT()
    {
        return INR_CHARGE_AMOUNT;
    }

    public void setINR_CHARGE_AMOUNT(double INR_CHARGE_AMOUNT)
    {
        this.INR_CHARGE_AMOUNT = INR_CHARGE_AMOUNT;
    }

    public double getUSD_CHARGE_AMOUNT()
    {
        return USD_CHARGE_AMOUNT;
    }

    public void setUSD_CHARGE_AMOUNT(double USD_CHARGE_AMOUNT)
    {
        this.USD_CHARGE_AMOUNT = USD_CHARGE_AMOUNT;
    }

    public double getEUR_CHARGE_AMOUNT()
    {
        return EUR_CHARGE_AMOUNT;
    }

    public void setEUR_CHARGE_AMOUNT(double EUR_CHARGE_AMOUNT)
    {
        this.EUR_CHARGE_AMOUNT = EUR_CHARGE_AMOUNT;
    }

    public double getJPY_CHARGE_AMOUNT()
    {
        return JPY_CHARGE_AMOUNT;
    }

    public void setJPY_CHARGE_AMOUNT(double JPY_CHARGE_AMOUNT)
    {
        this.JPY_CHARGE_AMOUNT = JPY_CHARGE_AMOUNT;
    }

    public double getGBP_CHARGE_AMOUNT()
    {
        return GBP_CHARGE_AMOUNT;
    }

    public void setGBP_CHARGE_AMOUNT(double GBP_CHARGE_AMOUNT)
    {
        this.GBP_CHARGE_AMOUNT = GBP_CHARGE_AMOUNT;
    }

    public boolean isINRAccount()
    {
        return isINRAccount;
    }

    public void setINRAccount(boolean INRAccount)
    {
        isINRAccount = INRAccount;
    }

    public boolean isUSDAccount()
    {
        return isUSDAccount;
    }

    public void setUSDAccount(boolean USDAccount)
    {
        isUSDAccount = USDAccount;
    }

    public boolean isEURAccount()
    {
        return isEURAccount;
    }

    public void setEURAccount(boolean EURAccount)
    {
        isEURAccount = EURAccount;
    }

    public boolean isGBPAccount()
    {
        return isGBPAccount;
    }

    public void setGBPAccount(boolean GBPAccount)
    {
        isGBPAccount = GBPAccount;
    }

    public boolean isJPYAccount()
    {
        return isJPYAccount;
    }

    public void setJPYAccount(boolean JPYAccount)
    {
        isJPYAccount = JPYAccount;
    }
}
