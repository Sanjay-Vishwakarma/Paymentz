/**
 * RecurringOperationsSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.payvision.core.message;

public interface RecurringOperationsSoap extends java.rmi.Remote {
    public com.payment.payvision.core.message.TransactionResult authorize(int memberId, java.lang.String memberGuid, int countryId, java.math.BigDecimal amount, int currencyId, java.lang.String trackingMemberCode, int cardId, java.lang.String cardGuid, int merchantAccountType, java.lang.String dynamicDescriptor) throws java.rmi.RemoteException;
    public com.payment.payvision.core.message.TransactionResult payment(int memberId, java.lang.String memberGuid, int countryId, java.math.BigDecimal amount, int currencyId, java.lang.String trackingMemberCode, int cardId, java.lang.String cardGuid, int merchantAccountType, java.lang.String dynamicDescriptor) throws java.rmi.RemoteException;
    public com.payment.payvision.core.message.TransactionResult credit(int memberId, java.lang.String memberGuid, int countryId, java.math.BigDecimal amount, int currencyId, java.lang.String trackingMemberCode, int cardId, java.lang.String cardGuid, int merchantAccountType, java.lang.String dynamicDescriptor) throws java.rmi.RemoteException;
    public com.payment.payvision.core.message.TransactionResult cardFundTransfer(int memberId, java.lang.String memberGuid, int countryId, java.math.BigDecimal amount, int currencyId, java.lang.String trackingMemberCode, int cardId, java.lang.String cardGuid, int merchantAccountType, java.lang.String dynamicDescriptor) throws java.rmi.RemoteException;
    public com.payment.payvision.core.message.RegisterCardResult registerCard(int memberId, java.lang.String memberGuid, java.lang.String number, java.lang.String holder, org.apache.axis.types.UnsignedByte expiryMonth, short expiryYear, java.lang.String cardType) throws java.rmi.RemoteException;
}
