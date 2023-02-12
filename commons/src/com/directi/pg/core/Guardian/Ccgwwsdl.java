package com.directi.pg.core.Guardian;

public interface Ccgwwsdl extends javax.xml.rpc.Service {
    public String getccgwwsdlPortAddress();

    public CcgwwsdlPortType getccgwwsdlPort() throws javax.xml.rpc.ServiceException;

    public CcgwwsdlPortType getccgwwsdlPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
