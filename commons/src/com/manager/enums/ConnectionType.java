package com.manager.enums;

/**
 * Created by 123 on 6/30/2015.
 */
public enum ConnectionType
{
    CONNECTION_HTTP("CONNECTION_HTTP"),
    CONNECTION_IRIDIUM("CONNECTION_IRIDIUM"),
    CONNECTION_OFFLINE("CONNECTION_OFFLINE");

    private String connectiontype;

    ConnectionType(String fileAction)
    {
        this.connectiontype=fileAction;
    }

    public String toString()
    {
        return connectiontype;
    }
}
