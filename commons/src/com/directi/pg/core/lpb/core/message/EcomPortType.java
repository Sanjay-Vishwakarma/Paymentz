/**
 * EcomPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.lpb.core.message;

public interface EcomPortType extends java.rmi.Remote {
    public EcomPaymentType payment(EcomPaymentType input) throws java.rmi.RemoteException;
    public EcomPaymentType authenticate(EcomPaymentType input) throws java.rmi.RemoteException;
    public EcomPaymentType deposit(EcomPaymentType input) throws java.rmi.RemoteException;
    public EcomPaymentType reverse(EcomPaymentType input) throws java.rmi.RemoteException;
    public EcomPaymentType close(EcomPaymentType input) throws java.rmi.RemoteException;
    public EcomPaymentType getPayment(EcomPaymentType input) throws java.rmi.RemoteException;
    public EcomPaymentType registerToken(EcomPaymentType input) throws java.rmi.RemoteException;
}
