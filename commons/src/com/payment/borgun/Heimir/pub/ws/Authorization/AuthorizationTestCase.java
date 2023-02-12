/**
 * AuthorizationTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.borgun.Heimir.pub.ws.Authorization;

import com.directi.pg.TransactionLogger;

public class AuthorizationTestCase extends junit.framework.TestCase {

    private static TransactionLogger transactionLogger = new TransactionLogger(AuthorizationTestCase.class.getName());

    public AuthorizationTestCase(java.lang.String name) {
        super(name);
    }

    public void testHeimir_pub_ws_Authorization_PortWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new com.payment.borgun.Heimir.pub.ws.Authorization.AuthorizationLocator().getHeimir_pub_ws_Authorization_PortAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new com.payment.borgun.Heimir.pub.ws.Authorization.AuthorizationLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1Heimir_pub_ws_Authorization_PortGetSettlementChargeback() throws Exception {

        com.payment.borgun.Heimir.pub.ws.Authorization.Heimir_pub_ws_Authorization_BinderStub binding;
        try {
            binding = (com.payment.borgun.Heimir.pub.ws.Authorization.Heimir_pub_ws_Authorization_BinderStub)
                          new com.payment.borgun.Heimir.pub.ws.Authorization.AuthorizationLocator().getHeimir_pub_ws_Authorization_Port();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.getSettlementChargeback(new java.lang.String());
        // TBD - validate results
    }

    public void test2Heimir_pub_ws_Authorization_PortSendDetailData() throws Exception {
        com.payment.borgun.Heimir.pub.ws.Authorization.Heimir_pub_ws_Authorization_BinderStub binding;
        try {
            binding = (com.payment.borgun.Heimir.pub.ws.Authorization.Heimir_pub_ws_Authorization_BinderStub)
                          new com.payment.borgun.Heimir.pub.ws.Authorization.AuthorizationLocator().getHeimir_pub_ws_Authorization_Port();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.sendDetailData(new java.lang.String());
        // TBD - validate results
    }

    public void test3Heimir_pub_ws_Authorization_PortGetActionCodeTexts() throws Exception {
        com.payment.borgun.Heimir.pub.ws.Authorization.Heimir_pub_ws_Authorization_BinderStub binding;
        try {
            binding = (com.payment.borgun.Heimir.pub.ws.Authorization.Heimir_pub_ws_Authorization_BinderStub)
                          new com.payment.borgun.Heimir.pub.ws.Authorization.AuthorizationLocator().getHeimir_pub_ws_Authorization_Port();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.getActionCodeTexts(new java.lang.String());
        // TBD - validate results
    }

    public void test4Heimir_pub_ws_Authorization_PortGetSettlementTransactions() throws Exception {
        com.payment.borgun.Heimir.pub.ws.Authorization.Heimir_pub_ws_Authorization_BinderStub binding;
        try {
            binding = (com.payment.borgun.Heimir.pub.ws.Authorization.Heimir_pub_ws_Authorization_BinderStub)
                          new com.payment.borgun.Heimir.pub.ws.Authorization.AuthorizationLocator().getHeimir_pub_ws_Authorization_Port();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.getSettlementTransactions(new java.lang.String());
        // TBD - validate results
    }

    public void test5Heimir_pub_ws_Authorization_PortGetSettlementBatch() throws Exception {
        com.payment.borgun.Heimir.pub.ws.Authorization.Heimir_pub_ws_Authorization_BinderStub binding;
        try {
            binding = (com.payment.borgun.Heimir.pub.ws.Authorization.Heimir_pub_ws_Authorization_BinderStub)
                          new com.payment.borgun.Heimir.pub.ws.Authorization.AuthorizationLocator().getHeimir_pub_ws_Authorization_Port();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.getSettlementBatch(new java.lang.String());
        // TBD - validate results
    }

    public void test6Heimir_pub_ws_Authorization_PortNewBatch() throws Exception {
        com.payment.borgun.Heimir.pub.ws.Authorization.Heimir_pub_ws_Authorization_BinderStub binding;
        try {
            binding = (com.payment.borgun.Heimir.pub.ws.Authorization.Heimir_pub_ws_Authorization_BinderStub)
                          new com.payment.borgun.Heimir.pub.ws.Authorization.AuthorizationLocator().getHeimir_pub_ws_Authorization_Port();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.newBatch(new java.lang.String());
        // TBD - validate results
    }

    public void test7Heimir_pub_ws_Authorization_PortGetTransactionList() throws Exception {
        com.payment.borgun.Heimir.pub.ws.Authorization.Heimir_pub_ws_Authorization_BinderStub binding;
        try {
            binding = (com.payment.borgun.Heimir.pub.ws.Authorization.Heimir_pub_ws_Authorization_BinderStub)
                          new com.payment.borgun.Heimir.pub.ws.Authorization.AuthorizationLocator().getHeimir_pub_ws_Authorization_Port();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.getTransactionList(new java.lang.String());
        // TBD - validate results
    }

    public void test8Heimir_pub_ws_Authorization_PortGetVirtualCard() throws Exception {
        com.payment.borgun.Heimir.pub.ws.Authorization.Heimir_pub_ws_Authorization_BinderStub binding;
        try {
            binding = (com.payment.borgun.Heimir.pub.ws.Authorization.Heimir_pub_ws_Authorization_BinderStub)
                          new com.payment.borgun.Heimir.pub.ws.Authorization.AuthorizationLocator().getHeimir_pub_ws_Authorization_Port();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.getVirtualCard(new java.lang.String());
        // TBD - validate results
    }

    public void test9Heimir_pub_ws_Authorization_PortCancelAuthorization() throws Exception {
        com.payment.borgun.Heimir.pub.ws.Authorization.Heimir_pub_ws_Authorization_BinderStub binding;
        try {
            binding = (com.payment.borgun.Heimir.pub.ws.Authorization.Heimir_pub_ws_Authorization_BinderStub)
                          new com.payment.borgun.Heimir.pub.ws.Authorization.AuthorizationLocator().getHeimir_pub_ws_Authorization_Port();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;


        //value = binding.cancelAuthorization(new java.lang.String());
        String data ="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<cancelAuthorization>\n" +
                "<Version>1000</Version>   \n" +
                "<Processor>108</Processor>   \n" +
                "<MerchantID>108</MerchantID>   \n" +
                "<TerminalID>1</TerminalID>   \n" +
                "<TransType>5</TransType>   \n" +
                "<TrAmount>12000</TrAmount>\n" +
                "<Transaction>39</Transaction>  \n"+
             //   "<PAN></PAN>   \n" +
              //  "<PAN>4507280000053760</PAN>    \n"+
               // "<ExpDate>1213</ExpDate>    \n"+
                "<TrCurrency>840</TrCurrency>   \n" +
                "<DateAndTime>110901083732</DateAndTime>   \n" +
                "<RRN>WC0000013031</RRN>   \n" +
                "<AuthCode>123456</AuthCode>   \n" +
                "</cancelAuthorization>";

        data = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<cancelAuthorization>\n" +
                "  <Version>1000</Version>\n" +
                "  <Processor>108</Processor>\n" +
                "  <MerchantID>108</MerchantID>\n" +
                "  <TerminalId>1</TerminalId>\n" +
                "  <TransType>1</TransType>\n" +
                "  <TrAmount>000000001200</TrAmount>\n" +
                "  <TrCurrency>840</TrCurrency>\n" +
                "  <DateAndTime>130901083744</DateAndTime>\n" +
                "  <RRN>WC0000016031</RRN>\n" +
                "  <Transaction>42</Transaction>\n" +
                "  <Batch>1</Batch>\n" +
                "  <CardAccId>9256684</CardAccId>\n" +
                "  <AuthCode>123456</AuthCode>\n" +
            //  "   <PAN>4507280000053760</PAN>\n" +
                "  <StoreTerminal>00010001</StoreTerminal>\n" +
                "  <CardType>Visa</CardType>\n" +
                "</cancelAuthorization>";

        data = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n" +
                "<cancelAuthorization>\n" +
                "  <Version>1000</Version>\n" +
                "  <Processor>108</Processor>\n" +
                "  <MerchantID>108</MerchantID>\n" +
                "  <TerminalID>1</TerminalID>\n" +
                "  <TransType>1</TransType>\n" +
                "  <TrAmount>000000012300</TrAmount>\n" +
                "  <TrCurrency>978</TrCurrency>\n" +
                "  <DateAndTime>130601083732</DateAndTime>\n" +
               //"  <PAN>4507280000053760</PAN>\n" +
                "  <RRN>WC0000000001</RRN>\n" +
                "  <Transaction>8</Transaction>\n" +
                "  <Batch>1</Batch>\n" +
                "  <CardAccId>9256684</CardAccId>\n" +
                "  <AuthCode>123456</AuthCode>\n" +
                "  <ActionCode>000</ActionCode>\n" +
                "  <StoreTerminal>00010001</StoreTerminal>\n" +
                "  <CardType>Visa</CardType>\n" +
                "</cancelAuthorization>";


        data ="<?xml version='1.0' encoding=\"utf-8\"?>\n" +
                "<cancelAuthorization>\n" +
                "<Version>1000</Version>\n" +
                "<Processor>123</Processor>\n" +
                "<MerchantID>2</MerchantID>\n" +
                "<TerminalId>1</TerminalId>\n" +
                "<TransType>1</TransType>\n" +
                "<TrAmount>000000100000</TrAmount>\n" +
                "<TrCurrency>352</TrCurrency>\n" +
                "<DateAndTime>060216103700</DateAndTime>\n" +
                "<PAN>5401710000006716</PAN>\n" +
                "<ExpDate>0705</ExpDate>\n" +
                "<RRN>060215012345</RRN>\n" +
                "<AuthCode>809285</AuthCode>\n" +
                "</cancelAuthorization>";

        data = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<cancelAuthorization>\n" +
                "  <Version>1000</Version>\n" +
                "  <Processor>108</Processor>\n" +
                "  <MerchantID>108</MerchantID>\n" +
                "  <TerminalID>1</TerminalID>\n" +
                "  <TransType>5</TransType>\n" +
                "  <TrAmount>000000001200</TrAmount>\n" +
                "  <TrCurrency>840</TrCurrency>\n" +
                "  <DateAndTime>131014062955</DateAndTime>\n" +
        //        "  <PAN>6011111111111117</PAN>\n" +
                "  <RRN>WC0000028031</RRN>\n" +
                "  <Transaction>58</Transaction>\n" +
                "  <Batch>1</Batch>\n" +
                "  <CardAccId>9256684</CardAccId>\n" +
                "  <CardAccName>pz\\Armuli 30\\Reykjavik\\108\\\\IS</CardAccName>\n" +
                "  <AuthCode>123456</AuthCode>\n" +
                "  <ActionCode>000</ActionCode>\n" +
                "  <StoreTerminal>00010001</StoreTerminal>\n" +
                "  <CardType>MasterCard</CardType>\n" +
                "</cancelAuthorization>";

        data ="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<cancelAuthorization>\n" +
                "  <Version>1000</Version>\n" +
                "  <Processor>108</Processor>\n" +
                "  <MerchantID>108</MerchantID>\n" +
                "  <TerminalID>1</TerminalID>\n" +
                "  <TransType>5</TransType>\n" +
                "  <TrAmount>000000001200</TrAmount>\n" +
                "  <TrCurrency>840</TrCurrency>\n" +
                "  <DateAndTime>131014063455</DateAndTime>\n" +
                //"  <PAN>6011111111111117</PAN>\n" +
                "  <RRN>WC0000028031</RRN>\n" +
                "  <Transaction>60</Transaction>\n" +
                "  <Batch>1</Batch>\n" +
              //  "  <CardAccId>9256684</CardAccId>\n" +
              //  "  <CardAccName>pz\\Armuli 30\\Reykjavik\\108\\\\IS</CardAccName>\n" +
                "  <AuthCode>123456</AuthCode>\n" +
             //   "  <ActionCode>000</ActionCode>\n" +
             //   "  <StoreTerminal>00010001</StoreTerminal>\n" +
             //   "  <CardType>MasterCard</CardType>\n" +
                "</cancelAuthorization>";

        data = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<cancelAuthorization>\n" +
                "  <Version>1000</Version>\n" +
                "  <Processor>108</Processor>\n" +
                "  <MerchantID>108</MerchantID>\n" +
                "  <TerminalID>1</TerminalID>\n" +
                "  <TransType>5</TransType>\n" +
                "  <TrAmount>000000001200</TrAmount>\n" +
                "  <TrCurrency>840</TrCurrency>\n" +
                "  <DateAndTime>131014064255</DateAndTime>\n" +
              //  "  <PAN>6011111111111117</PAN>\n" +
                "  <RRN>WC0000028031</RRN>\n" +
                "  <Transaction>62</Transaction>\n" +
                "  <Batch>1</Batch>\n" +
              //  "  <CardAccId>9256684</CardAccId>\n" +
             //   "  <CardAccName>pz\\Armuli 30\\Reykjavik\\108\\\\IS</CardAccName>\n" +
                "  <AuthCode>123456</AuthCode>\n" +
                //"  <ActionCode>000</ActionCode>\n" +
               // "  <StoreTerminal>00010001</StoreTerminal>\n" +
               // "  <CardType>MasterCard</CardType>\n" +
                "</cancelAuthorization>";
         value = binding.cancelAuthorization(data);
        //System.out.println(" result : " + value);

        // TBD - validate results
    }

    public void test10Heimir_pub_ws_Authorization_PortGetAuthorization() throws Exception {
        com.payment.borgun.Heimir.pub.ws.Authorization.Heimir_pub_ws_Authorization_BinderStub binding;
        try {
            binding = (com.payment.borgun.Heimir.pub.ws.Authorization.Heimir_pub_ws_Authorization_BinderStub)
                          new com.payment.borgun.Heimir.pub.ws.Authorization.AuthorizationLocator().getHeimir_pub_ws_Authorization_Port();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);
        String value ;
        String data,capture,sale,refund;

         data = "<?xml version='1.0' encoding='utf-8'?>\n" +
                 " <getAuthorization>\n" +
                 "   <Version>1000</Version>  \n" +
                 "   <Processor>108</Processor>  \n" +
                 "   <MerchantID>108</MerchantID> \n" +
                 "   <TerminalID>1</TerminalID> \n" +
                 "   <TransType>5</TransType> \n" +
                 "   <TrAmount>1200</TrAmount> \n" +
                 "   <TrCurrency>840</TrCurrency> \n" +
                 "   <DateAndTime>131014064255</DateAndTime> \n" +
                 "   <PAN>6011111111111117</PAN> \n" +
                 "   <ExpDate>0914</ExpDate> \n" +
                 "   <RRN>WC0000028031</RRN> \n" +
                 "   <CVC2>310</CVC2> \n" +
                 " </getAuthorization>";


        capture = "<?xml version='1.0' encoding='utf-8'?>\n" +
                " <getAuthorization>\n" +
                "   <Version>1000</Version>  \n" +
                "   <Processor>108</Processor>  \n" +
                "   <MerchantID>108</MerchantID> \n" +
                "   <TerminalID>1</TerminalID> \n" +
                "   <TransType>1</TransType> \n" +
                "   <TrAmount>1200</TrAmount> \n" +
                "   <TrCurrency>840</TrCurrency> \n" +
                "   <DateAndTime>110701083732</DateAndTime> \n" +
                //"   <RRN>WC0000011031</RRN> \n" +
                "   <Transaction>37</Transaction>\n" +
                "   <Batch>1</Batch>\n" +
                "   <AuthCode>123456</AuthCode>\n" +
                " </getAuthorization>";


        value = binding.getAuthorization(data);


        //System.out.println(" The result " + value);
        // TBD - validate results
    }

    public void test11Heimir_pub_ws_Authorization_PortGetDisputedTransactions() throws Exception {
        com.payment.borgun.Heimir.pub.ws.Authorization.Heimir_pub_ws_Authorization_BinderStub binding;
        try {
            binding = (com.payment.borgun.Heimir.pub.ws.Authorization.Heimir_pub_ws_Authorization_BinderStub)
                          new com.payment.borgun.Heimir.pub.ws.Authorization.AuthorizationLocator().getHeimir_pub_ws_Authorization_Port();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.getDisputedTransactions(new java.lang.String());
        // TBD - validate results
    }

    public void test12Heimir_pub_ws_Authorization_PortGetFraudTransactions() throws Exception {
        com.payment.borgun.Heimir.pub.ws.Authorization.Heimir_pub_ws_Authorization_BinderStub binding;
        try {
            binding = (com.payment.borgun.Heimir.pub.ws.Authorization.Heimir_pub_ws_Authorization_BinderStub)
                          new com.payment.borgun.Heimir.pub.ws.Authorization.AuthorizationLocator().getHeimir_pub_ws_Authorization_Port();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::: ", jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.getFraudTransactions(new java.lang.String());
        // TBD - validate results
    }

    public void test13Heimir_pub_ws_Authorization_PortGetSettlements() throws Exception {
        com.payment.borgun.Heimir.pub.ws.Authorization.Heimir_pub_ws_Authorization_BinderStub binding;
        try {
            binding = (com.payment.borgun.Heimir.pub.ws.Authorization.Heimir_pub_ws_Authorization_BinderStub)
                          new com.payment.borgun.Heimir.pub.ws.Authorization.AuthorizationLocator().getHeimir_pub_ws_Authorization_Port();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                transactionLogger.error("javax.xml.rpc.ServiceException :::: ",jre);
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.getSettlements(new java.lang.String());
        // TBD - validate results
    }

}
