package com.directi.pg.core.valueObjects;

import com.payment.common.core.CommRequestVO;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 6/19/13
 * Time: 3:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class SwiffpayRequestVO extends CommRequestVO
{
    String accid;
    String subid;
    String historyid;
    String merchantordernumber;
    String merchantpin;
    String ipaddress;

    public String getMerchantordernumber()
    {
        return merchantordernumber;
    }

    public void setMerchantordernumber(String merchantordernumber)
    {
        this.merchantordernumber = merchantordernumber;
    }

    public String getMerchantpin()
    {
        return merchantpin;
    }

    public void setMerchantpin(String merchantpin)
    {
        this.merchantpin = merchantpin;
    }

    public String getIpaddress()
    {
        return ipaddress;
    }

    public void setIpaddress(String ipaddress)
    {
        this.ipaddress = ipaddress;
    }


    public String getAccid()
    {
        return accid;
    }

    public void setAccid(String accid)
    {
        this.accid = accid;
    }

    public String getSubid()
    {
        return subid;
    }

    public void setSubid(String subid)
    {
        this.subid = subid;
    }

    public String getHistoryid()
    {
        return historyid;
    }

    public void setHistoryid(String historyid)
    {
        this.historyid = historyid;
    }



}
