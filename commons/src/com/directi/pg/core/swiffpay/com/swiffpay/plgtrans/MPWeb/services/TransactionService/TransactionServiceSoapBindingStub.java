/**
 * TransactionServiceSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService;

public class TransactionServiceSoapBindingStub extends org.apache.axis.client.Stub implements com.directi.pg.core.swiffpay.com.swiffpay.plgtrans.MPWeb.services.TransactionService.TransactionSOAPBindingImpl {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[39];
        _initOperationDesc1();
        _initOperationDesc2();
        _initOperationDesc3();
        _initOperationDesc4();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processCCSale");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "ccinfo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "CreditCardInfo"), com.directi.pg.core.swiffpay.MPTransProcess.CreditCardInfo.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processCCSaleReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processCCAuth");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "ccinfo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "CreditCardInfo"), com.directi.pg.core.swiffpay.MPTransProcess.CreditCardInfo.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processCCAuthReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processCCVoid");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "miscprocess"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "VoidCreditPost"), com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditPost.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processCCVoidReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processCCPost");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "miscprocess"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "VoidCreditPost"), com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditPost.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processCCPostReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processACHSale");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "ckinfo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "ACHInfo"), com.directi.pg.core.swiffpay.MPTransProcess.ACHInfo.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processACHSaleReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processACHVerification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "ckinfo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "ACHInfo"), com.directi.pg.core.swiffpay.MPTransProcess.ACHInfo.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processACHVerificationReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processACHCheck21");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "ckinfo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "ACHCheck21Info"), com.directi.pg.core.swiffpay.MPTransProcess.ACHCheck21Info.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processACHCheck21Return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processCCCredit");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "ccinfo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "CreditCardInfo"), com.directi.pg.core.swiffpay.MPTransProcess.CreditCardInfo.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processCCCreditReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[7] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processACHCredit");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "ckinfo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "ACHInfo"), com.directi.pg.core.swiffpay.MPTransProcess.ACHInfo.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processACHCreditReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[8] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processCredit");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "miscprocess"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "VoidCreditPost"), com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditPost.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processCreditReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[9] = oper;

    }

    private static void _initOperationDesc2(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processDebitSale");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "debitinfo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "DebitInfo"), com.directi.pg.core.swiffpay.MPTransProcess.DebitInfo.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processDebitSaleReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[10] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processDebitReturn");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "debitreturn"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "DebitReturn"), com.directi.pg.core.swiffpay.MPTransProcess.DebitReturn.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processDebitReturnReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[11] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processCCProfileAdd");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "ccinfo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "CreditCardInfo"), com.directi.pg.core.swiffpay.MPTransProcess.CreditCardInfo.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessProfileResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processCCProfileAddReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[12] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processCKProfileAdd");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "achinfo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "ACHInfo"), com.directi.pg.core.swiffpay.MPTransProcess.ACHInfo.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessProfileResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processCKProfileAddReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[13] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processProfileRetrieve");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "miscprocess"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "ProfileRetrieve"), com.directi.pg.core.swiffpay.MPTransProcess.ProfileRetrieve.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessProfileResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processProfileRetrieveReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[14] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processProfileDelete");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "miscprocess"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "ProfileDelete"), com.directi.pg.core.swiffpay.MPTransProcess.ProfileDelete.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessProfileResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processProfileDeleteReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[15] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processProfileSale");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "miscprocess"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "ProfileSale"), com.directi.pg.core.swiffpay.MPTransProcess.ProfileSale.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessProfileResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processProfileSaleReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[16] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processProfileUpdate");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "miscprocess"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "ProfileUpdate"), com.directi.pg.core.swiffpay.MPTransProcess.ProfileUpdate.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessProfileResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processProfileUpdateReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[17] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processProfileCredit");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "miscprocess"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "ProfileCredit"), com.directi.pg.core.swiffpay.MPTransProcess.ProfileCredit.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessProfileResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processProfileCreditReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[18] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processCheck21Void");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "miscprocess"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "VoidCreditCheck21"), com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditCheck21.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processCheck21VoidReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[19] = oper;

    }

    private static void _initOperationDesc3(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processCheck21Credit");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "miscprocess"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "VoidCreditCheck21"), com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditCheck21.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processCheck21CreditReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[20] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processExtACHVoid");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "miscprocess"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "VoidCreditPost"), com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditPost.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processExtACHVoidReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[21] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processExtACHCredit");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "miscprocess"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "VoidCreditPost"), com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditPost.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processExtACHCreditReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[22] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processExtACHSale");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "ckinfo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "ACHInfo"), com.directi.pg.core.swiffpay.MPTransProcess.ACHInfo.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processExtACHSaleReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[23] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processC21RCCVoid");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "miscprocess"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "VoidCreditPost"), com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditPost.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processC21RCCVoidReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[24] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processC21RCCCredit");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "miscprocess"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "VoidCreditPost"), com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditPost.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processC21RCCCreditReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[25] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processC21RCCSale");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "ckinfo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "ACHInfo"), com.directi.pg.core.swiffpay.MPTransProcess.ACHInfo.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processC21RCCSaleReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[26] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processExtACHConsumerDisbursement");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "ckinfo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "ACHInfo"), com.directi.pg.core.swiffpay.MPTransProcess.ACHInfo.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processExtACHConsumerDisbursementReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[27] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processTransRetrieve");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "miscprocess"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "TransRetrieve"), com.directi.pg.core.swiffpay.MPTransProcess.TransRetrieve.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processTransRetrieveReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[28] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processRecurRetrieve");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "miscprocess"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "RecurRetrieve"), com.directi.pg.core.swiffpay.MPTransProcess.RecurRetrieve.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessRecurRetrieveResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessRecurRetrieveResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processRecurRetrieveReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[29] = oper;

    }

    private static void _initOperationDesc4(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processC21ICLSale");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "ckinfo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "C21ICLInfo"), com.directi.pg.core.swiffpay.MPTransProcess.C21ICLInfo.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processC21ICLSaleReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[30] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processC21ICLVoid");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "miscprocess"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "VoidCreditC21ICL"), com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditC21ICL.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processC21ICLVoidReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[31] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processC21ICLCredit");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "miscprocess"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "VoidCreditC21ICL"), com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditC21ICL.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processC21ICLCreditReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[32] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processCCAuthentication");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "ccinfo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "CreditCardInfo"), com.directi.pg.core.swiffpay.MPTransProcess.CreditCardInfo.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResult"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processCCAuthenticationReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[33] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processCCSales");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "ccinfo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "CreditCardInfo"), com.directi.pg.core.swiffpay.MPTransProcess.CreditCardInfo.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResults"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processCCSalesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[34] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processCCAuths");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "ccinfo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "CreditCardInfo"), com.directi.pg.core.swiffpay.MPTransProcess.CreditCardInfo.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResults"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processCCAuthsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[35] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processCCCredits");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "ccinfo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "CreditCardInfo"), com.directi.pg.core.swiffpay.MPTransProcess.CreditCardInfo.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResults"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processCCCreditsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[36] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processDebitSales");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "debitinfo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "DebitInfo"), com.directi.pg.core.swiffpay.MPTransProcess.DebitInfo.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResults"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processDebitSalesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[37] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("processDebitReturns");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "debitreturn"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:MPTransProcess", "DebitReturn"), com.directi.pg.core.swiffpay.MPTransProcess.DebitReturn.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResults"));
        oper.setReturnClass(com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "processDebitReturnsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[38] = oper;

    }

    public TransactionServiceSoapBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public TransactionServiceSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public TransactionServiceSoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("urn:MPTransProcess", "ACHCheck21Info");
            cachedSerQNames.add(qName);
            cls = com.directi.pg.core.swiffpay.MPTransProcess.ACHCheck21Info.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:MPTransProcess", "ACHInfo");
            cachedSerQNames.add(qName);
            cls = com.directi.pg.core.swiffpay.MPTransProcess.ACHInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:MPTransProcess", "address");
            cachedSerQNames.add(qName);
            cls = com.directi.pg.core.swiffpay.MPTransProcess.Address.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:MPTransProcess", "AutoRental");
            cachedSerQNames.add(qName);
            cls = com.directi.pg.core.swiffpay.MPTransProcess.AutoRental.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:MPTransProcess", "AutoRepair");
            cachedSerQNames.add(qName);
            cls = com.directi.pg.core.swiffpay.MPTransProcess.AutoRepair.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:MPTransProcess", "C21ICLInfo");
            cachedSerQNames.add(qName);
            cls = com.directi.pg.core.swiffpay.MPTransProcess.C21ICLInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:MPTransProcess", "CreditCardInfo");
            cachedSerQNames.add(qName);
            cls = com.directi.pg.core.swiffpay.MPTransProcess.CreditCardInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:MPTransProcess", "customEmail");
            cachedSerQNames.add(qName);
            cls = com.directi.pg.core.swiffpay.MPTransProcess.CustomEmail.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:MPTransProcess", "CustomFields");
            cachedSerQNames.add(qName);
            cls = com.directi.pg.core.swiffpay.MPTransProcess.CustomFields.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:MPTransProcess", "DebitInfo");
            cachedSerQNames.add(qName);
            cls = com.directi.pg.core.swiffpay.MPTransProcess.DebitInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:MPTransProcess", "DebitReturn");
            cachedSerQNames.add(qName);
            cls = com.directi.pg.core.swiffpay.MPTransProcess.DebitReturn.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:MPTransProcess", "FSA");
            cachedSerQNames.add(qName);
            cls = com.directi.pg.core.swiffpay.MPTransProcess.FSA.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:MPTransProcess", "HotelLodging");
            cachedSerQNames.add(qName);
            cls = com.directi.pg.core.swiffpay.MPTransProcess.HotelLodging.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessProfileResult");
            cachedSerQNames.add(qName);
            cls = com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessRecurRetrieveResult");
            cachedSerQNames.add(qName);
            cls = com.directi.pg.core.swiffpay.MPTransProcess.ProcessRecurRetrieveResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResult");
            cachedSerQNames.add(qName);
            cls = com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResults");
            cachedSerQNames.add(qName);
            cls = com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:MPTransProcess", "ProfileCredit");
            cachedSerQNames.add(qName);
            cls = com.directi.pg.core.swiffpay.MPTransProcess.ProfileCredit.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:MPTransProcess", "ProfileDelete");
            cachedSerQNames.add(qName);
            cls = com.directi.pg.core.swiffpay.MPTransProcess.ProfileDelete.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:MPTransProcess", "ProfileRetrieve");
            cachedSerQNames.add(qName);
            cls = com.directi.pg.core.swiffpay.MPTransProcess.ProfileRetrieve.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:MPTransProcess", "ProfileSale");
            cachedSerQNames.add(qName);
            cls = com.directi.pg.core.swiffpay.MPTransProcess.ProfileSale.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:MPTransProcess", "ProfileUpdate");
            cachedSerQNames.add(qName);
            cls = com.directi.pg.core.swiffpay.MPTransProcess.ProfileUpdate.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:MPTransProcess", "PurchaseCardLevel2");
            cachedSerQNames.add(qName);
            cls = com.directi.pg.core.swiffpay.MPTransProcess.PurchaseCardLevel2.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:MPTransProcess", "Recur");
            cachedSerQNames.add(qName);
            cls = com.directi.pg.core.swiffpay.MPTransProcess.Recur.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:MPTransProcess", "RecurRetrieve");
            cachedSerQNames.add(qName);
            cls = com.directi.pg.core.swiffpay.MPTransProcess.RecurRetrieve.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:MPTransProcess", "Restaurant");
            cachedSerQNames.add(qName);
            cls = com.directi.pg.core.swiffpay.MPTransProcess.Restaurant.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:MPTransProcess", "TransRetrieve");
            cachedSerQNames.add(qName);
            cls = com.directi.pg.core.swiffpay.MPTransProcess.TransRetrieve.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:MPTransProcess", "VoidCreditC21ICL");
            cachedSerQNames.add(qName);
            cls = com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditC21ICL.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:MPTransProcess", "VoidCreditCheck21");
            cachedSerQNames.add(qName);
            cls = com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditCheck21.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:MPTransProcess", "VoidCreditPost");
            cachedSerQNames.add(qName);
            cls = com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditPost.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
                    _call.setEncodingStyle(org.apache.axis.Constants.URI_SOAP11_ENC);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processCCSale(com.directi.pg.core.swiffpay.MPTransProcess.CreditCardInfo ccinfo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processCCSale"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {ccinfo});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processCCAuth(com.directi.pg.core.swiffpay.MPTransProcess.CreditCardInfo ccinfo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processCCAuth"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {ccinfo});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processCCVoid(com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditPost miscprocess) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processCCVoid"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {miscprocess});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processCCPost(com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditPost miscprocess) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processCCPost"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {miscprocess});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processACHSale(com.directi.pg.core.swiffpay.MPTransProcess.ACHInfo ckinfo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processACHSale"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {ckinfo});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processACHVerification(com.directi.pg.core.swiffpay.MPTransProcess.ACHInfo ckinfo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processACHVerification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {ckinfo});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processACHCheck21(com.directi.pg.core.swiffpay.MPTransProcess.ACHCheck21Info ckinfo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processACHCheck21"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {ckinfo});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processCCCredit(com.directi.pg.core.swiffpay.MPTransProcess.CreditCardInfo ccinfo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processCCCredit"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {ccinfo});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processACHCredit(com.directi.pg.core.swiffpay.MPTransProcess.ACHInfo ckinfo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[8]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processACHCredit"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {ckinfo});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processCredit(com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditPost miscprocess) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[9]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processCredit"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {miscprocess});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processDebitSale(com.directi.pg.core.swiffpay.MPTransProcess.DebitInfo debitinfo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[10]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processDebitSale"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {debitinfo});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processDebitReturn(com.directi.pg.core.swiffpay.MPTransProcess.DebitReturn debitreturn) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[11]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processDebitReturn"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {debitreturn});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult processCCProfileAdd(com.directi.pg.core.swiffpay.MPTransProcess.CreditCardInfo ccinfo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[12]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processCCProfileAdd"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {ccinfo});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult processCKProfileAdd(com.directi.pg.core.swiffpay.MPTransProcess.ACHInfo achinfo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[13]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processCKProfileAdd"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {achinfo});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult processProfileRetrieve(com.directi.pg.core.swiffpay.MPTransProcess.ProfileRetrieve miscprocess) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[14]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processProfileRetrieve"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {miscprocess});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult processProfileDelete(com.directi.pg.core.swiffpay.MPTransProcess.ProfileDelete miscprocess) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[15]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processProfileDelete"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {miscprocess});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult processProfileSale(com.directi.pg.core.swiffpay.MPTransProcess.ProfileSale miscprocess) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[16]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processProfileSale"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {miscprocess});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult processProfileUpdate(com.directi.pg.core.swiffpay.MPTransProcess.ProfileUpdate miscprocess) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[17]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processProfileUpdate"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {miscprocess});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult processProfileCredit(com.directi.pg.core.swiffpay.MPTransProcess.ProfileCredit miscprocess) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[18]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processProfileCredit"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {miscprocess});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessProfileResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processCheck21Void(com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditCheck21 miscprocess) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[19]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processCheck21Void"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {miscprocess});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processCheck21Credit(com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditCheck21 miscprocess) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[20]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processCheck21Credit"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {miscprocess});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processExtACHVoid(com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditPost miscprocess) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[21]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processExtACHVoid"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {miscprocess});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processExtACHCredit(com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditPost miscprocess) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[22]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processExtACHCredit"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {miscprocess});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processExtACHSale(com.directi.pg.core.swiffpay.MPTransProcess.ACHInfo ckinfo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[23]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processExtACHSale"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {ckinfo});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processC21RCCVoid(com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditPost miscprocess) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[24]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processC21RCCVoid"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {miscprocess});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processC21RCCCredit(com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditPost miscprocess) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[25]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processC21RCCCredit"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {miscprocess});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processC21RCCSale(com.directi.pg.core.swiffpay.MPTransProcess.ACHInfo ckinfo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[26]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processC21RCCSale"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {ckinfo});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processExtACHConsumerDisbursement(com.directi.pg.core.swiffpay.MPTransProcess.ACHInfo ckinfo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[27]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processExtACHConsumerDisbursement"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {ckinfo});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processTransRetrieve(com.directi.pg.core.swiffpay.MPTransProcess.TransRetrieve miscprocess) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[28]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processTransRetrieve"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {miscprocess});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessRecurRetrieveResult processRecurRetrieve(com.directi.pg.core.swiffpay.MPTransProcess.RecurRetrieve miscprocess) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[29]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processRecurRetrieve"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {miscprocess});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessRecurRetrieveResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessRecurRetrieveResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessRecurRetrieveResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processC21ICLSale(com.directi.pg.core.swiffpay.MPTransProcess.C21ICLInfo ckinfo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[30]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processC21ICLSale"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {ckinfo});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processC21ICLVoid(com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditC21ICL miscprocess) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[31]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processC21ICLVoid"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {miscprocess});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processC21ICLCredit(com.directi.pg.core.swiffpay.MPTransProcess.VoidCreditC21ICL miscprocess) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[32]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processC21ICLCredit"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {miscprocess});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult processCCAuthentication(com.directi.pg.core.swiffpay.MPTransProcess.CreditCardInfo ccinfo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[33]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processCCAuthentication"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {ccinfo});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults processCCSales(com.directi.pg.core.swiffpay.MPTransProcess.CreditCardInfo ccinfo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[34]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processCCSales"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {ccinfo});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults processCCAuths(com.directi.pg.core.swiffpay.MPTransProcess.CreditCardInfo ccinfo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[35]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processCCAuths"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {ccinfo});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults processCCCredits(com.directi.pg.core.swiffpay.MPTransProcess.CreditCardInfo ccinfo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[36]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processCCCredits"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {ccinfo});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults processDebitSales(com.directi.pg.core.swiffpay.MPTransProcess.DebitInfo debitinfo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[37]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processDebitSales"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {debitinfo});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults processDebitReturns(com.directi.pg.core.swiffpay.MPTransProcess.DebitReturn debitreturn) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[38]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://MPTransProcess", "processDebitReturns"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {debitreturn});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults) org.apache.axis.utils.JavaUtils.convert(_resp, com.directi.pg.core.swiffpay.MPTransProcess.ProcessResults.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
