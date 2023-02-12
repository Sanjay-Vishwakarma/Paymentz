package com.manager.vo.gatewayVOs;

import com.directi.pg.core.GatewayType;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 9/1/14
 * Time: 12:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class GatewayTypeVO
{
    private String pgTYypeId;
    private boolean defaultApplication;
    private boolean mappedForApplication;

    GatewayType gatewayType;

    public String getPgTYypeId()
    {
        return pgTYypeId;
    }

    public void setPgTYypeId(String pgTYypeId)
    {
        this.pgTYypeId = pgTYypeId;
    }

    public boolean isDefaultApplication()
    {
        return defaultApplication;
    }

    public void setDefaultApplication(boolean defaultApplication)
    {
        this.defaultApplication = defaultApplication;
    }

    public boolean isMappedForApplication()
    {
        return mappedForApplication;
    }

    public void setMappedForApplication(boolean mappedForApplication)
    {
        this.mappedForApplication = mappedForApplication;
    }

    public GatewayType getGatewayType()
    {
        return gatewayType;
    }

    public void setGatewayType(GatewayType gatewayType)
    {
        this.gatewayType = gatewayType;
    }
}
