package com.manager.vo;

/**
 * Created by Admin on 11/2/2017.
 */
public class ChartVolumeVO
{
    String salesChart;

    String refundChart;

    String chargebackChart;

    public String getSalesChart()
    {
        return salesChart;
    }

    public void setSalesChart(String salesChart)
    {
        this.salesChart = salesChart;
    }

    public String getRefundChart()
    {
        return refundChart;
    }

    public void setRefundChart(String refundChart)
    {
        this.refundChart = refundChart;
    }

    public String getChargebackChart()
    {
        return chargebackChart;
    }

    public void setChargebackChart(String chargebackChart)
    {
        this.chargebackChart = chargebackChart;
    }
}
