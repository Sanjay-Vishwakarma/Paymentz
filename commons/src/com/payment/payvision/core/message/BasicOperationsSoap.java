/**
 * BasicOperationsSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.payvision.core.message;

public interface BasicOperationsSoap extends java.rmi.Remote {
    public com.payment.payvision.core.message.TransactionResult authorize(int memberId, java.lang.String memberGuid, int countryId, java.math.BigDecimal amount, int currencyId, java.lang.String trackingMemberCode, java.lang.String cardNumber, java.lang.String cardHolder, org.apache.axis.types.UnsignedByte cardExpiryMonth, short cardExpiryYear, java.lang.String cardCvv, java.lang.String cardType, java.lang.String issueNumber, int merchantAccountType, java.lang.String dynamicDescriptor, java.lang.String avsAddress, java.lang.String avsZip) throws java.rmi.RemoteException;
    public com.payment.payvision.core.message.TransactionResult payment(int memberId, java.lang.String memberGuid, int countryId, java.math.BigDecimal amount, int currencyId, java.lang.String trackingMemberCode, java.lang.String cardNumber, java.lang.String cardHolder, org.apache.axis.types.UnsignedByte cardExpiryMonth, short cardExpiryYear, java.lang.String cardCvv, java.lang.String cardType, java.lang.String issueNumber, int merchantAccountType, java.lang.String dynamicDescriptor, java.lang.String avsAddress, java.lang.String avsZip) throws java.rmi.RemoteException;
    public com.payment.payvision.core.message.TransactionResult credit(int memberId, java.lang.String memberGuid, int countryId, java.math.BigDecimal amount, int currencyId, java.lang.String trackingMemberCode, java.lang.String cardNumber, java.lang.String cardHolder, org.apache.axis.types.UnsignedByte cardExpiryMonth, short cardExpiryYear, java.lang.String cardCvv, java.lang.String cardType, java.lang.String issueNumber, int merchantAccountType, java.lang.String dynamicDescriptor, java.lang.String avsAddress, java.lang.String avsZip) throws java.rmi.RemoteException;
    public com.payment.payvision.core.message.TransactionResult refund(int memberId, java.lang.String memberGuid, int transactionId, java.lang.String transactionGuid, java.math.BigDecimal amount, int currencyId, java.lang.String trackingMemberCode) throws java.rmi.RemoteException;
    public com.payment.payvision.core.message.TransactionResult capture(int memberId, java.lang.String memberGuid, int transactionId, java.lang.String transactionGuid, java.math.BigDecimal amount, int currencyId, java.lang.String trackingMemberCode) throws java.rmi.RemoteException;
    public com.payment.payvision.core.message.TransactionResult _void(int memberId, java.lang.String memberGuid, int transactionId, java.lang.String transactionGuid, java.lang.String trackingMemberCode) throws java.rmi.RemoteException;
    public com.payment.payvision.core.message.TransactionResult referralApproval(int memberId, java.lang.String memberGuid, int transactionId, java.lang.String transactionGuid, java.math.BigDecimal amount, int currencyId, java.lang.String trackingMemberCode, java.lang.String approvalCode) throws java.rmi.RemoteException;
    public com.payment.payvision.core.message.TransactionResult cardFundTransfer(int memberId, java.lang.String memberGuid, int countryId, java.math.BigDecimal amount, int currencyId, java.lang.String trackingMemberCode, java.lang.String cardNumber, java.lang.String cardHolder, org.apache.axis.types.UnsignedByte cardExpiryMonth, short cardExpiryYear, java.lang.String cardCvv, java.lang.String cardType, java.lang.String issueNumber, int merchantAccountType, java.lang.String dynamicDescriptor, java.lang.String avsAddress, java.lang.String avsZip) throws java.rmi.RemoteException;
    public com.payment.payvision.core.message.TransactionResult retrieveTransactionResult(int memberId, java.lang.String memberGuid, java.lang.String trackingMemberCode, java.util.Calendar transactionDate) throws java.rmi.RemoteException;
}
