/**
 * Isrvproceso.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.procesosmc.org.tempuri;

public interface Isrvproceso extends java.rmi.Remote {
    public String deposito(String xml) throws java.rmi.RemoteException;
    public String anulacion(String xml) throws java.rmi.RemoteException;
    public String confirma(String xml) throws java.rmi.RemoteException;
    public String consulta(String xml) throws java.rmi.RemoteException;
    public String test(String xml) throws java.rmi.RemoteException;
    public String autoriza(String xml) throws java.rmi.RemoteException;
}
