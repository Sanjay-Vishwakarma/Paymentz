/**
 * PayHOSTSoap11Stub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.payhost.core.za.co.paygate.www.PayHOST;

public class PayHOSTSoap11Stub extends org.apache.axis.client.Stub implements com.payment.payhost.core.za.co.paygate.www.PayHOST.PayHOST {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[5];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("SinglePayout");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "SinglePayoutRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", ">SinglePayoutRequest"), com.payment.payhost.core.za.co.paygate.www.PayHOST.SinglePayoutRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", ">SinglePayoutResponse"));
        oper.setReturnClass(com.payment.payhost.core.za.co.paygate.www.PayHOST.SinglePayoutResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "SinglePayoutResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("SinglePayment");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "SinglePaymentRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", ">SinglePaymentRequest"), com.payment.payhost.core.za.co.paygate.www.PayHOST.SinglePaymentRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", ">SinglePaymentResponse"));
        oper.setReturnClass(com.payment.payhost.core.za.co.paygate.www.PayHOST.SinglePaymentResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "SinglePaymentResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("SingleFollowUp");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "SingleFollowUpRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", ">SingleFollowUpRequest"), com.payment.payhost.core.za.co.paygate.www.PayHOST.SingleFollowUpRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", ">SingleFollowUpResponse"));
        oper.setReturnClass(com.payment.payhost.core.za.co.paygate.www.PayHOST.SingleFollowUpResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "SingleFollowUpResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[2] = oper;

        /*oper = new org.apache.axis.description.OperationDesc();
        oper.setName("SingleVault");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "SingleVaultRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", ">SingleVaultRequest"), com.payment.payhost.core.za.co.paygate.www.PayHOST.SingleVaultRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", ">SingleVaultResponse"));
        oper.setReturnClass(com.payment.payhost.core.za.co.paygate.www.PayHOST.SingleVaultResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "SingleVaultResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[3] = oper;*/

        /*oper = new org.apache.axis.description.OperationDesc();
        oper.setName("Ping");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "PingRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", ">PingRequest"), com.payment.payhost.core.za.co.paygate.www.PayHOST.PingRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", ">PingResponse"));
        oper.setReturnClass(com.payment.payhost.core.za.co.paygate.www.PayHOST.PingResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "PingResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[4] = oper;*/

    }

    public PayHOSTSoap11Stub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public PayHOSTSoap11Stub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public PayHOSTSoap11Stub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
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
            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", ">FlightLegType>ArrivalAirport");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.Token.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", ">FlightLegType>ArrivalCity");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.Token.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", ">FlightLegType>DepartureAirport");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.Token.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", ">FlightLegType>DepartureCity");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.Token.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", ">PassengerType>TravellerType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.PassengerTypeTravellerType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);*/

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", ">PingRequest");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.PingRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);*/

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", ">PingResponse");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.PingResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);*/

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", ">SingleFollowUpRequest");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.SingleFollowUpRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", ">SingleFollowUpResponse");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.SingleFollowUpResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", ">SinglePaymentRequest");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.SinglePaymentRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", ">SinglePaymentResponse");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.SinglePaymentResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", ">SinglePayoutRequest");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.SinglePayoutRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", ">SinglePayoutResponse");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.SinglePayoutResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", ">SingleVaultRequest");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.SingleVaultRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);*/

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", ">SingleVaultResponse");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.SingleVaultResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);*/

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "AddressType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.AddressType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);*/

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "AirlineBookingDetailsType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.AirlineBookingDetailsType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);*/

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "BankPayoutRequestType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.BankPayoutRequestType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);*/

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "BankPayoutResponseType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.BankPayoutResponseType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);*/

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "BillingDetailsType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.BillingDetailsType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);*/

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "BrowserType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.BrowserType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);*/

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "BudgetPeriodType");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.Token.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "CardDateType");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.Token.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "CardNumberType");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.Token.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "CardPaymentRequestType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.CardPaymentRequestType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "CardPaymentResponseType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.CardPaymentResponseType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "CardPayoutRequestType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.CardPayoutRequestType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);*/

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "CardPayoutResponseType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.CardPayoutResponseType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);*/

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "CardVaultRequestType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.CardVaultRequestType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "CardVaultResponseType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.CardVaultResponseType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "CarrierCodeType");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.Token.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "CountryType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.CountryType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "CurrencyType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.CurrencyType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "CvvType");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.Token.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "DeleteVaultRequestType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.DeleteVaultRequestType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);*/

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "DeleteVaultResponseType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.DeleteVaultResponseType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);*/

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "FlightLegType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.FlightLegType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);*/

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "IssueNumberType");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.Token.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "KeyValueType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.KeyValueType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);*/

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "LanguageType");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.Token.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "LookUpVaultRequestType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.LookUpVaultRequestType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);*/

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "LookUpVaultResponseType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.LookUpVaultResponseType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);*/

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "OrderItemType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.OrderItemType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "OrderType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.OrderType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "PassengerType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.PassengerType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);*/

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "PayGateAccountType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.PayGateAccountType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "PaymentDetailType");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.Token.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "PaymentMethodType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.PaymentMethodType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "PaymentType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.PaymentType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "PersonType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.PersonType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "QueryRequestType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.QueryRequestType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "QueryResponseType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.QueryResponseType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "QueryTransactionType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.QueryTransactionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "RedirectRequestType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.RedirectRequestType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);*/

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "RedirectResponseType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.RedirectResponseType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);*/

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "RefundRequestType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.RefundRequestType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "RefundResponseType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.RefundResponseType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "RiskType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.RiskType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);*/

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "SettleRequestType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.SettleRequestType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "SettleResponseType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.SettleResponseType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "ShippingDetailsType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.ShippingDetailsType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);*/

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "StatusNameType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.StatusNameType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "StatusType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.StatusType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "ThreeDSecureType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.ThreeDSecureType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);*/

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "TimeZoneType");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.Token.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "TransactionType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.TransactionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);*/

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "VaultDataType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.VaultDataType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);*/

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "VaultKeyType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.VaultKeyType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);*/

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "VoidRequestType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.VoidRequestType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "VoidResponseType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.VoidResponseType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "WalletPayoutRequestType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.WalletPayoutRequestType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);*/

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "WalletPayoutResponseType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.WalletPayoutResponseType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);*/

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "WalletVaultRequestType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.WalletVaultRequestType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);*/

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "WalletVaultResponseType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.WalletVaultResponseType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);*/

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "WebPaymentRequestType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.WebPaymentRequestType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);*/

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "WebPaymentResponseType");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.WebPaymentResponseType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);*/

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "YNU");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.YNU.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);*/

            /*qName = new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "YNUA");
            cachedSerQNames.add(qName);
            cls = com.payment.payhost.core.za.co.paygate.www.PayHOST.YNUA.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);*/

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
                    _call.setEncodingStyle(null);
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

    public com.payment.payhost.core.za.co.paygate.www.PayHOST.SinglePayoutResponse singlePayout(com.payment.payhost.core.za.co.paygate.www.PayHOST.SinglePayoutRequest singlePayoutRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "SinglePayout"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {singlePayoutRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.payment.payhost.core.za.co.paygate.www.PayHOST.SinglePayoutResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.payment.payhost.core.za.co.paygate.www.PayHOST.SinglePayoutResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.payment.payhost.core.za.co.paygate.www.PayHOST.SinglePayoutResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.payment.payhost.core.za.co.paygate.www.PayHOST.SinglePaymentResponse singlePayment(com.payment.payhost.core.za.co.paygate.www.PayHOST.SinglePaymentRequest singlePaymentRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "SinglePayment"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {singlePaymentRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.payment.payhost.core.za.co.paygate.www.PayHOST.SinglePaymentResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.payment.payhost.core.za.co.paygate.www.PayHOST.SinglePaymentResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.payment.payhost.core.za.co.paygate.www.PayHOST.SinglePaymentResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.payment.payhost.core.za.co.paygate.www.PayHOST.SingleFollowUpResponse singleFollowUp(com.payment.payhost.core.za.co.paygate.www.PayHOST.SingleFollowUpRequest singleFollowUpRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "SingleFollowUp"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {singleFollowUpRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.payment.payhost.core.za.co.paygate.www.PayHOST.SingleFollowUpResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.payment.payhost.core.za.co.paygate.www.PayHOST.SingleFollowUpResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.payment.payhost.core.za.co.paygate.www.PayHOST.SingleFollowUpResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    /*public com.payment.payhost.core.za.co.paygate.www.PayHOST.SingleVaultResponse singleVault(com.payment.payhost.core.za.co.paygate.www.PayHOST.SingleVaultRequest singleVaultRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "SingleVault"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {singleVaultRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.payment.payhost.core.za.co.paygate.www.PayHOST.SingleVaultResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.payment.payhost.core.za.co.paygate.www.PayHOST.SingleVaultResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.payment.payhost.core.za.co.paygate.www.PayHOST.SingleVaultResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }*/

    /*public com.payment.payhost.core.za.co.paygate.www.PayHOST.PingResponse ping(com.payment.payhost.core.za.co.paygate.www.PayHOST.PingRequest pingRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "Ping"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {pingRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.payment.payhost.core.za.co.paygate.www.PayHOST.PingResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.payment.payhost.core.za.co.paygate.www.PayHOST.PingResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.payment.payhost.core.za.co.paygate.www.PayHOST.PingResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }*/

}
