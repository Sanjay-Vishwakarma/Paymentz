/**
 * WsanularSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.visaNet.com_cancel;

public interface WsanularSoap extends java.rmi.Remote {

    /**
     * Este metodo permite anular una transación sin necesidad de
     * Eticket
     */
    public java.lang.String anularPedido(java.lang.String strXml) throws java.rmi.RemoteException;

    /**
     * Este metodo permite anular una transación con eticket
     */
    public java.lang.String anularEticket(java.lang.String strXml) throws java.rmi.RemoteException;
}
