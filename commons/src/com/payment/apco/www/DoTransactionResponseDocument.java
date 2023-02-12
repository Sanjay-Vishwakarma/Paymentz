/*
 * An XML document type.
 * Localname: DoTransactionResponse
 * Namespace: https://www.apsp.biz/
 * Java type: com.payment.apco.www.DoTransactionResponseDocument
 *
 * Automatically generated - do not modify.
 */
package com.payment.apco.www;


/**
 * A document containing one DoTransactionResponse(@https://www.apsp.biz/) element.
 *
 * This is a complex type.
 */
public interface DoTransactionResponseDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(DoTransactionResponseDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s385088F2E2BDDE409AE9EF7EEAABD3BB").resolveHandle("dotransactionresponse6693doctype");
    
    /**
     * Gets the "DoTransactionResponse" element
     */
    com.payment.apco.www.DoTransactionResponseDocument.DoTransactionResponse getDoTransactionResponse();
    
    /**
     * Sets the "DoTransactionResponse" element
     */
    void setDoTransactionResponse(com.payment.apco.www.DoTransactionResponseDocument.DoTransactionResponse doTransactionResponse);
    
    /**
     * Appends and returns a new empty "DoTransactionResponse" element
     */
    com.payment.apco.www.DoTransactionResponseDocument.DoTransactionResponse addNewDoTransactionResponse();
    
    /**
     * An XML DoTransactionResponse(@https://www.apsp.biz/).
     *
     * This is a complex type.
     */
    public interface DoTransactionResponse extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(DoTransactionResponse.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s385088F2E2BDDE409AE9EF7EEAABD3BB").resolveHandle("dotransactionresponseeb93elemtype");
        
        /**
         * Gets the "DoTransactionResult" element
         */
        java.lang.String getDoTransactionResult();
        
        /**
         * Gets (as xml) the "DoTransactionResult" element
         */
        org.apache.xmlbeans.XmlString xgetDoTransactionResult();
        
        /**
         * True if has "DoTransactionResult" element
         */
        boolean isSetDoTransactionResult();
        
        /**
         * Sets the "DoTransactionResult" element
         */
        void setDoTransactionResult(java.lang.String doTransactionResult);
        
        /**
         * Sets (as xml) the "DoTransactionResult" element
         */
        void xsetDoTransactionResult(org.apache.xmlbeans.XmlString doTransactionResult);
        
        /**
         * Unsets the "DoTransactionResult" element
         */
        void unsetDoTransactionResult();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static com.payment.apco.www.DoTransactionResponseDocument.DoTransactionResponse newInstance() {
              return (com.payment.apco.www.DoTransactionResponseDocument.DoTransactionResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static com.payment.apco.www.DoTransactionResponseDocument.DoTransactionResponse newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (com.payment.apco.www.DoTransactionResponseDocument.DoTransactionResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static com.payment.apco.www.DoTransactionResponseDocument newInstance() {
          return (com.payment.apco.www.DoTransactionResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static com.payment.apco.www.DoTransactionResponseDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (com.payment.apco.www.DoTransactionResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static com.payment.apco.www.DoTransactionResponseDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (com.payment.apco.www.DoTransactionResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static com.payment.apco.www.DoTransactionResponseDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.payment.apco.www.DoTransactionResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static com.payment.apco.www.DoTransactionResponseDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.payment.apco.www.DoTransactionResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static com.payment.apco.www.DoTransactionResponseDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.payment.apco.www.DoTransactionResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static com.payment.apco.www.DoTransactionResponseDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.payment.apco.www.DoTransactionResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static com.payment.apco.www.DoTransactionResponseDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.payment.apco.www.DoTransactionResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static com.payment.apco.www.DoTransactionResponseDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.payment.apco.www.DoTransactionResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static com.payment.apco.www.DoTransactionResponseDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.payment.apco.www.DoTransactionResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static com.payment.apco.www.DoTransactionResponseDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.payment.apco.www.DoTransactionResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static com.payment.apco.www.DoTransactionResponseDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.payment.apco.www.DoTransactionResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static com.payment.apco.www.DoTransactionResponseDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (com.payment.apco.www.DoTransactionResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static com.payment.apco.www.DoTransactionResponseDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.payment.apco.www.DoTransactionResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static com.payment.apco.www.DoTransactionResponseDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (com.payment.apco.www.DoTransactionResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static com.payment.apco.www.DoTransactionResponseDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.payment.apco.www.DoTransactionResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.payment.apco.www.DoTransactionResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.payment.apco.www.DoTransactionResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.payment.apco.www.DoTransactionResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.payment.apco.www.DoTransactionResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
