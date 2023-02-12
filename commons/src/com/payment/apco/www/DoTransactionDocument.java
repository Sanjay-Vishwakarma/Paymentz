/*
 * An XML document type.
 * Localname: DoTransaction
 * Namespace: https://www.apsp.biz/
 * Java type: com.payment.apco.www.DoTransactionDocument
 *
 * Automatically generated - do not modify.
 */
package com.payment.apco.www;


/**
 * A document containing one DoTransaction(@https://www.apsp.biz/) element.
 *
 * This is a complex type.
 */
public interface DoTransactionDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(DoTransactionDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s385088F2E2BDDE409AE9EF7EEAABD3BB").resolveHandle("dotransaction56f2doctype");
    
    /**
     * Gets the "DoTransaction" element
     */
    com.payment.apco.www.DoTransactionDocument.DoTransaction getDoTransaction();
    
    /**
     * Sets the "DoTransaction" element
     */
    void setDoTransaction(com.payment.apco.www.DoTransactionDocument.DoTransaction doTransaction);
    
    /**
     * Appends and returns a new empty "DoTransaction" element
     */
    com.payment.apco.www.DoTransactionDocument.DoTransaction addNewDoTransaction();
    
    /**
     * An XML DoTransaction(@https://www.apsp.biz/).
     *
     * This is a complex type.
     */
    public interface DoTransaction extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(DoTransaction.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s385088F2E2BDDE409AE9EF7EEAABD3BB").resolveHandle("dotransaction3f51elemtype");
        
        /**
         * Gets the "MerchID" element
         */
        java.lang.String getMerchID();
        
        /**
         * Gets (as xml) the "MerchID" element
         */
        org.apache.xmlbeans.XmlString xgetMerchID();
        
        /**
         * True if has "MerchID" element
         */
        boolean isSetMerchID();
        
        /**
         * Sets the "MerchID" element
         */
        void setMerchID(java.lang.String merchID);
        
        /**
         * Sets (as xml) the "MerchID" element
         */
        void xsetMerchID(org.apache.xmlbeans.XmlString merchID);
        
        /**
         * Unsets the "MerchID" element
         */
        void unsetMerchID();
        
        /**
         * Gets the "Pass" element
         */
        java.lang.String getPass();
        
        /**
         * Gets (as xml) the "Pass" element
         */
        org.apache.xmlbeans.XmlString xgetPass();
        
        /**
         * True if has "Pass" element
         */
        boolean isSetPass();
        
        /**
         * Sets the "Pass" element
         */
        void setPass(java.lang.String pass);
        
        /**
         * Sets (as xml) the "Pass" element
         */
        void xsetPass(org.apache.xmlbeans.XmlString pass);
        
        /**
         * Unsets the "Pass" element
         */
        void unsetPass();
        
        /**
         * Gets the "TrType" element
         */
        java.lang.String getTrType();
        
        /**
         * Gets (as xml) the "TrType" element
         */
        org.apache.xmlbeans.XmlString xgetTrType();
        
        /**
         * True if has "TrType" element
         */
        boolean isSetTrType();
        
        /**
         * Sets the "TrType" element
         */
        void setTrType(java.lang.String trType);
        
        /**
         * Sets (as xml) the "TrType" element
         */
        void xsetTrType(org.apache.xmlbeans.XmlString trType);
        
        /**
         * Unsets the "TrType" element
         */
        void unsetTrType();
        
        /**
         * Gets the "CardNum" element
         */
        java.lang.String getCardNum();
        
        /**
         * Gets (as xml) the "CardNum" element
         */
        org.apache.xmlbeans.XmlString xgetCardNum();
        
        /**
         * True if has "CardNum" element
         */
        boolean isSetCardNum();
        
        /**
         * Sets the "CardNum" element
         */
        void setCardNum(java.lang.String cardNum);
        
        /**
         * Sets (as xml) the "CardNum" element
         */
        void xsetCardNum(org.apache.xmlbeans.XmlString cardNum);
        
        /**
         * Unsets the "CardNum" element
         */
        void unsetCardNum();
        
        /**
         * Gets the "CVV2" element
         */
        java.lang.String getCVV2();
        
        /**
         * Gets (as xml) the "CVV2" element
         */
        org.apache.xmlbeans.XmlString xgetCVV2();
        
        /**
         * True if has "CVV2" element
         */
        boolean isSetCVV2();
        
        /**
         * Sets the "CVV2" element
         */
        void setCVV2(java.lang.String cvv2);
        
        /**
         * Sets (as xml) the "CVV2" element
         */
        void xsetCVV2(org.apache.xmlbeans.XmlString cvv2);
        
        /**
         * Unsets the "CVV2" element
         */
        void unsetCVV2();
        
        /**
         * Gets the "ExpDay" element
         */
        java.lang.String getExpDay();
        
        /**
         * Gets (as xml) the "ExpDay" element
         */
        org.apache.xmlbeans.XmlString xgetExpDay();
        
        /**
         * True if has "ExpDay" element
         */
        boolean isSetExpDay();
        
        /**
         * Sets the "ExpDay" element
         */
        void setExpDay(java.lang.String expDay);
        
        /**
         * Sets (as xml) the "ExpDay" element
         */
        void xsetExpDay(org.apache.xmlbeans.XmlString expDay);
        
        /**
         * Unsets the "ExpDay" element
         */
        void unsetExpDay();
        
        /**
         * Gets the "ExpMonth" element
         */
        java.lang.String getExpMonth();
        
        /**
         * Gets (as xml) the "ExpMonth" element
         */
        org.apache.xmlbeans.XmlString xgetExpMonth();
        
        /**
         * True if has "ExpMonth" element
         */
        boolean isSetExpMonth();
        
        /**
         * Sets the "ExpMonth" element
         */
        void setExpMonth(java.lang.String expMonth);
        
        /**
         * Sets (as xml) the "ExpMonth" element
         */
        void xsetExpMonth(org.apache.xmlbeans.XmlString expMonth);
        
        /**
         * Unsets the "ExpMonth" element
         */
        void unsetExpMonth();
        
        /**
         * Gets the "ExpYear" element
         */
        java.lang.String getExpYear();
        
        /**
         * Gets (as xml) the "ExpYear" element
         */
        org.apache.xmlbeans.XmlString xgetExpYear();
        
        /**
         * True if has "ExpYear" element
         */
        boolean isSetExpYear();
        
        /**
         * Sets the "ExpYear" element
         */
        void setExpYear(java.lang.String expYear);
        
        /**
         * Sets (as xml) the "ExpYear" element
         */
        void xsetExpYear(org.apache.xmlbeans.XmlString expYear);
        
        /**
         * Unsets the "ExpYear" element
         */
        void unsetExpYear();
        
        /**
         * Gets the "CardHName" element
         */
        java.lang.String getCardHName();
        
        /**
         * Gets (as xml) the "CardHName" element
         */
        org.apache.xmlbeans.XmlString xgetCardHName();
        
        /**
         * True if has "CardHName" element
         */
        boolean isSetCardHName();
        
        /**
         * Sets the "CardHName" element
         */
        void setCardHName(java.lang.String cardHName);
        
        /**
         * Sets (as xml) the "CardHName" element
         */
        void xsetCardHName(org.apache.xmlbeans.XmlString cardHName);
        
        /**
         * Unsets the "CardHName" element
         */
        void unsetCardHName();
        
        /**
         * Gets the "Amount" element
         */
        java.lang.String getAmount();
        
        /**
         * Gets (as xml) the "Amount" element
         */
        org.apache.xmlbeans.XmlString xgetAmount();
        
        /**
         * True if has "Amount" element
         */
        boolean isSetAmount();
        
        /**
         * Sets the "Amount" element
         */
        void setAmount(java.lang.String amount);
        
        /**
         * Sets (as xml) the "Amount" element
         */
        void xsetAmount(org.apache.xmlbeans.XmlString amount);
        
        /**
         * Unsets the "Amount" element
         */
        void unsetAmount();
        
        /**
         * Gets the "CurrencyCode" element
         */
        java.lang.String getCurrencyCode();
        
        /**
         * Gets (as xml) the "CurrencyCode" element
         */
        org.apache.xmlbeans.XmlString xgetCurrencyCode();
        
        /**
         * True if has "CurrencyCode" element
         */
        boolean isSetCurrencyCode();
        
        /**
         * Sets the "CurrencyCode" element
         */
        void setCurrencyCode(java.lang.String currencyCode);
        
        /**
         * Sets (as xml) the "CurrencyCode" element
         */
        void xsetCurrencyCode(org.apache.xmlbeans.XmlString currencyCode);
        
        /**
         * Unsets the "CurrencyCode" element
         */
        void unsetCurrencyCode();
        
        /**
         * Gets the "Addr" element
         */
        java.lang.String getAddr();
        
        /**
         * Gets (as xml) the "Addr" element
         */
        org.apache.xmlbeans.XmlString xgetAddr();
        
        /**
         * True if has "Addr" element
         */
        boolean isSetAddr();
        
        /**
         * Sets the "Addr" element
         */
        void setAddr(java.lang.String addr);
        
        /**
         * Sets (as xml) the "Addr" element
         */
        void xsetAddr(org.apache.xmlbeans.XmlString addr);
        
        /**
         * Unsets the "Addr" element
         */
        void unsetAddr();
        
        /**
         * Gets the "PostCode" element
         */
        java.lang.String getPostCode();
        
        /**
         * Gets (as xml) the "PostCode" element
         */
        org.apache.xmlbeans.XmlString xgetPostCode();
        
        /**
         * True if has "PostCode" element
         */
        boolean isSetPostCode();
        
        /**
         * Sets the "PostCode" element
         */
        void setPostCode(java.lang.String postCode);
        
        /**
         * Sets (as xml) the "PostCode" element
         */
        void xsetPostCode(org.apache.xmlbeans.XmlString postCode);
        
        /**
         * Unsets the "PostCode" element
         */
        void unsetPostCode();
        
        /**
         * Gets the "TransID" element
         */
        java.lang.String getTransID();
        
        /**
         * Gets (as xml) the "TransID" element
         */
        org.apache.xmlbeans.XmlString xgetTransID();
        
        /**
         * True if has "TransID" element
         */
        boolean isSetTransID();
        
        /**
         * Sets the "TransID" element
         */
        void setTransID(java.lang.String transID);
        
        /**
         * Sets (as xml) the "TransID" element
         */
        void xsetTransID(org.apache.xmlbeans.XmlString transID);
        
        /**
         * Unsets the "TransID" element
         */
        void unsetTransID();
        
        /**
         * Gets the "UserIP" element
         */
        java.lang.String getUserIP();
        
        /**
         * Gets (as xml) the "UserIP" element
         */
        org.apache.xmlbeans.XmlString xgetUserIP();
        
        /**
         * True if has "UserIP" element
         */
        boolean isSetUserIP();
        
        /**
         * Sets the "UserIP" element
         */
        void setUserIP(java.lang.String userIP);
        
        /**
         * Sets (as xml) the "UserIP" element
         */
        void xsetUserIP(org.apache.xmlbeans.XmlString userIP);
        
        /**
         * Unsets the "UserIP" element
         */
        void unsetUserIP();
        
        /**
         * Gets the "UDF1" element
         */
        java.lang.String getUDF1();
        
        /**
         * Gets (as xml) the "UDF1" element
         */
        org.apache.xmlbeans.XmlString xgetUDF1();
        
        /**
         * True if has "UDF1" element
         */
        boolean isSetUDF1();
        
        /**
         * Sets the "UDF1" element
         */
        void setUDF1(java.lang.String udf1);
        
        /**
         * Sets (as xml) the "UDF1" element
         */
        void xsetUDF1(org.apache.xmlbeans.XmlString udf1);
        
        /**
         * Unsets the "UDF1" element
         */
        void unsetUDF1();
        
        /**
         * Gets the "UDF2" element
         */
        java.lang.String getUDF2();
        
        /**
         * Gets (as xml) the "UDF2" element
         */
        org.apache.xmlbeans.XmlString xgetUDF2();
        
        /**
         * True if has "UDF2" element
         */
        boolean isSetUDF2();
        
        /**
         * Sets the "UDF2" element
         */
        void setUDF2(java.lang.String udf2);
        
        /**
         * Sets (as xml) the "UDF2" element
         */
        void xsetUDF2(org.apache.xmlbeans.XmlString udf2);
        
        /**
         * Unsets the "UDF2" element
         */
        void unsetUDF2();
        
        /**
         * Gets the "UDF3" element
         */
        java.lang.String getUDF3();
        
        /**
         * Gets (as xml) the "UDF3" element
         */
        org.apache.xmlbeans.XmlString xgetUDF3();
        
        /**
         * True if has "UDF3" element
         */
        boolean isSetUDF3();
        
        /**
         * Sets the "UDF3" element
         */
        void setUDF3(java.lang.String udf3);
        
        /**
         * Sets (as xml) the "UDF3" element
         */
        void xsetUDF3(org.apache.xmlbeans.XmlString udf3);
        
        /**
         * Unsets the "UDF3" element
         */
        void unsetUDF3();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static com.payment.apco.www.DoTransactionDocument.DoTransaction newInstance() {
              return (com.payment.apco.www.DoTransactionDocument.DoTransaction) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static com.payment.apco.www.DoTransactionDocument.DoTransaction newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (com.payment.apco.www.DoTransactionDocument.DoTransaction) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static com.payment.apco.www.DoTransactionDocument newInstance() {
          return (com.payment.apco.www.DoTransactionDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static com.payment.apco.www.DoTransactionDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (com.payment.apco.www.DoTransactionDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static com.payment.apco.www.DoTransactionDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (com.payment.apco.www.DoTransactionDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static com.payment.apco.www.DoTransactionDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.payment.apco.www.DoTransactionDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static com.payment.apco.www.DoTransactionDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.payment.apco.www.DoTransactionDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static com.payment.apco.www.DoTransactionDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.payment.apco.www.DoTransactionDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static com.payment.apco.www.DoTransactionDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.payment.apco.www.DoTransactionDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static com.payment.apco.www.DoTransactionDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.payment.apco.www.DoTransactionDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static com.payment.apco.www.DoTransactionDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.payment.apco.www.DoTransactionDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static com.payment.apco.www.DoTransactionDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.payment.apco.www.DoTransactionDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static com.payment.apco.www.DoTransactionDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.payment.apco.www.DoTransactionDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static com.payment.apco.www.DoTransactionDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.payment.apco.www.DoTransactionDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static com.payment.apco.www.DoTransactionDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (com.payment.apco.www.DoTransactionDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static com.payment.apco.www.DoTransactionDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.payment.apco.www.DoTransactionDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static com.payment.apco.www.DoTransactionDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (com.payment.apco.www.DoTransactionDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static com.payment.apco.www.DoTransactionDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.payment.apco.www.DoTransactionDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.payment.apco.www.DoTransactionDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.payment.apco.www.DoTransactionDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.payment.apco.www.DoTransactionDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.payment.apco.www.DoTransactionDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
