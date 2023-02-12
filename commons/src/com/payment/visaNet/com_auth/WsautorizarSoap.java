/**
 * WsautorizarSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.visaNet.com_auth;

public interface WsautorizarSoap extends java.rmi.Remote {

    /**
     * Este metodo permite generar una transación sin eticket
     */
    public java.lang.String autorizarTransaccion(java.lang.String strXml) throws java.rmi.RemoteException;
}
