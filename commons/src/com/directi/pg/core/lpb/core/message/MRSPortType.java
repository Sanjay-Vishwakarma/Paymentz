/**
 * MRSPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.lpb.core.message;

public interface MRSPortType extends java.rmi.Remote {
    public MrsDATA mrsREQUEST(MrsDATA input) throws java.rmi.RemoteException;
    public MrsDATA mrsDECLINE(MrsDATA input) throws java.rmi.RemoteException;
    public MrsDATA mrsUNAPPROVE(MrsDATA input) throws java.rmi.RemoteException;
    public MrsDATA mrsAPPROVE(MrsDATA input) throws java.rmi.RemoteException;
    public MrsDATA mrsDEPOSIT(MrsDATA input) throws java.rmi.RemoteException;
    public MrsDATA mrsREVERSE(MrsDATA input) throws java.rmi.RemoteException;
    public MrsDATA mrsPROCESS(MrsDATA input) throws java.rmi.RemoteException;
    public MrsDATA mrsRETURN(MrsDATA input) throws java.rmi.RemoteException;
    public MrsDATA mrsCLOSE(MrsDATA input) throws java.rmi.RemoteException;
    public MrsDATA mrsARCHIVE(MrsDATA input) throws java.rmi.RemoteException;
    public MrsDATA mrsSTATUS(MrsDATA input) throws java.rmi.RemoteException;
    public MrsDATA mrsTOKENACQ(MrsDATA input) throws java.rmi.RemoteException;
}
