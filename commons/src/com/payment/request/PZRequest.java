package com.payment.request;

import com.directi.pg.AuditTrailVO;
import com.manager.vo.MarketPlaceVO;

import java.util.Hashtable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 2/16/13
 * Time: 11:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class PZRequest
{
    private Integer trackingId;
    private Integer accountId;
    private Integer memberId;
    private String ipAddress;
    private AuditTrailVO auditTrailVO;
    private String paymentid;
    private String currency;
    private String terminalId;
    private Integer requestedTrackingid;
    private String marketPlaceFlag;
    private List<MarketPlaceVO> childDetailsList;

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public Integer getTrackingId()
    {
        return trackingId;
    }

    public void setTrackingId(Integer trackingId)
    {
        this.trackingId = trackingId;
    }

    public Integer getAccountId()
    {
        return accountId;
    }

    public void setAccountId(Integer accountId)
    {
        this.accountId = accountId;
    }

    public Integer getMemberId()
    {
        return memberId;
    }

    public void setMemberId(Integer memberId)
    {
        this.memberId = memberId;
    }
    public AuditTrailVO getAuditTrailVO()
    {
        return auditTrailVO;
    }

    public void setAuditTrailVO(AuditTrailVO auditTrailVO)
    {
        this.auditTrailVO = auditTrailVO;
    }

    public String getPaymentid()
    {
        return paymentid;
    }

    public void setPaymentid(String paymentid)
    {
        this.paymentid = paymentid;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getTerminalId()
    {
        return terminalId;
    }

    public void setTerminalId(String terminalId)
    {
        this.terminalId = terminalId;
    }

    public Integer getRequestedTrackingid()
    {
        return requestedTrackingid;
    }

    public void setRequestedTrackingid(Integer requestedTrackingid)
    {
        this.requestedTrackingid = requestedTrackingid;
    }

    public List<MarketPlaceVO> getChildDetailsList()
    {
        return childDetailsList;
    }

    public void setChildDetailsList(List<MarketPlaceVO> childDetailsList)
    {
        this.childDetailsList = childDetailsList;
    }

    public String getMarketPlaceFlag()
    {
        return marketPlaceFlag;
    }

    public void setMarketPlaceFlag(String marketPlaceFlag)
    {
        this.marketPlaceFlag = marketPlaceFlag;
    }
}
