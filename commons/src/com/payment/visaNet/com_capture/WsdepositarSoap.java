/**
 * WsdepositarSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.visaNet.com_capture;

public interface WsdepositarSoap extends java.rmi.Remote {

    /**
     * Deposita Operaciones según ETicket
     */
    public java.lang.String depositarEticket(java.lang.String xmlIn) throws java.rmi.RemoteException;

    /**
     * Deposita Operaciones sin necesidad de adquirir un ETikect
     */
    public java.lang.String depositarPedido(java.lang.String xmlIn) throws java.rmi.RemoteException;

    /**
     * Anula un depósito sin necesidad de adquirir un ETikect
     */
    public java.lang.String anulaDepositoPedido(java.lang.String xmlIn) throws java.rmi.RemoteException;

    /**
     * Anula un depósito Con ETikect
     */
    public java.lang.String anulaDepositoEticket(java.lang.String xmlIn) throws java.rmi.RemoteException;
}
