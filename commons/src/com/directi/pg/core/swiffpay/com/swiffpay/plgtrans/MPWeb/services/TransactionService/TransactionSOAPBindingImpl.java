/**
 * TransactionSOAPBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService;

public interface TransactionSOAPBindingImpl extends java.rmi.Remote {
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processCCSale(com.directi.pg.core.swiffpay.MPTransProcess.CreditCardInfo ccinfo) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processCCAuth(com.directi.pg.core.swiffpay.MPTransProcess.CreditCardInfo ccinfo) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processCCVoid(com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditPost miscprocess) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processCCPost(com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditPost miscprocess) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processACHSale(com.directi.pg.core.swiffpay.MPTransProcess.ACHInfo ckinfo) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processACHVerification(com.directi.pg.core.swiffpay.MPTransProcess.ACHInfo ckinfo) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processACHCheck21(com.directi.pg.core.swiffpay.MPTransProcess.ACHCheck21Info ckinfo) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processCCCredit(com.directi.pg.core.swiffpay.MPTransProcess.CreditCardInfo ccinfo) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processACHCredit(com.directi.pg.core.swiffpay.MPTransProcess.ACHInfo ckinfo) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processCredit(com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditPost miscprocess) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processDebitSale(com.directi.pg.core.swiffpay.MPTransProcess.DebitInfo debitinfo) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processDebitReturn(com.directi.pg.core.swiffpay.MPTransProcess.DebitReturn debitreturn) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult processCCProfileAdd(com.directi.pg.core.swiffpay.MPTransProcess.CreditCardInfo ccinfo) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult processCKProfileAdd(com.directi.pg.core.swiffpay.MPTransProcess.ACHInfo achinfo) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult processProfileRetrieve(com.directi.pg.core.swiffpay.MPTransProcess.ProfileRetrieve miscprocess) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult processProfileDelete(com.directi.pg.core.swiffpay.MPTransProcess.ProfileDelete miscprocess) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult processProfileSale(com.directi.pg.core.swiffpay.MPTransProcess.ProfileSale miscprocess) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult processProfileUpdate(com.directi.pg.core.swiffpay.MPTransProcess.ProfileUpdate miscprocess) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult processProfileCredit(com.directi.pg.core.swiffpay.MPTransProcess.ProfileCredit miscprocess) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processCheck21Void(com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditCheck21 miscprocess) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processCheck21Credit(com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditCheck21 miscprocess) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processExtACHVoid(com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditPost miscprocess) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processExtACHCredit(com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditPost miscprocess) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processExtACHSale(com.directi.pg.core.swiffpay.MPTransProcess.ACHInfo ckinfo) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processC21RCCVoid(com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditPost miscprocess) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processC21RCCCredit(com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditPost miscprocess) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processC21RCCSale(com.directi.pg.core.swiffpay.MPTransProcess.ACHInfo ckinfo) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processExtACHConsumerDisbursement(com.directi.pg.core.swiffpay.MPTransProcess.ACHInfo ckinfo) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processTransRetrieve(com.directi.pg.core.swiffpay.MPTransProcess.TransRetrieve miscprocess) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessRecurRetrieveResult processRecurRetrieve(com.directi.pg.core.swiffpay.MPTransProcess.RecurRetrieve miscprocess) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processC21ICLSale(com.directi.pg.core.swiffpay.MPTransProcess.C21ICLInfo ckinfo) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processC21ICLVoid(com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditC21ICL miscprocess) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processC21ICLCredit(com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditC21ICL miscprocess) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processCCAuthentication(com.directi.pg.core.swiffpay.MPTransProcess.CreditCardInfo ccinfo) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults processCCSales(com.directi.pg.core.swiffpay.MPTransProcess.CreditCardInfo ccinfo) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults processCCAuths(com.directi.pg.core.swiffpay.MPTransProcess.CreditCardInfo ccinfo) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults processCCCredits(com.directi.pg.core.swiffpay.MPTransProcess.CreditCardInfo ccinfo) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults processDebitSales(com.directi.pg.core.swiffpay.MPTransProcess.DebitInfo debitinfo) throws java.rmi.RemoteException;
    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults processDebitReturns(com.directi.pg.core.swiffpay.MPTransProcess.DebitReturn debitreturn) throws java.rmi.RemoteException;
}
