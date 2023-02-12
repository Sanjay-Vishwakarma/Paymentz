/**
 * RequestOptionsType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003;

public class RequestOptionsType  implements java.io.Serializable {
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RequestOptionsType.class, true);
    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Request.OptionsType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("acceptLanguage");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "AcceptLanguage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("forwardedIP");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "ForwardedIP"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("IPv4Address");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "IPv4Address"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("MAP");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "MAP"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("randomCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "RandomCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sessionID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "SessionID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("token");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Token"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("urlReferrer");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "UrlReferrer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userAgent");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "UserAgent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }
    private String acceptLanguage;
    private String forwardedIP;
    private String IPv4Address;
    private Boolean MAP;
    private Boolean randomCode;
    private String sessionID;
    private String token;
    private String urlReferrer;
    private String userAgent;
    private Object __equalsCalc = null;
    private boolean __hashCodeCalc = false;


    public RequestOptionsType() {
    }


    public RequestOptionsType(
           String acceptLanguage,
           String forwardedIP,
           String IPv4Address,
           Boolean MAP,
           Boolean randomCode,
           String sessionID,
           String token,
           String urlReferrer,
           String userAgent) {
           this.acceptLanguage = acceptLanguage;
           this.forwardedIP = forwardedIP;
           this.IPv4Address = IPv4Address;
           this.MAP = MAP;
           this.randomCode = randomCode;
           this.sessionID = sessionID;
           this.token = token;
           this.urlReferrer = urlReferrer;
           this.userAgent = userAgent;
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Gets the acceptLanguage value for this RequestOptionsType.
     *
     * @return acceptLanguage
     */
    public String getAcceptLanguage() {
        return acceptLanguage;
    }

    /**
     * Sets the acceptLanguage value for this RequestOptionsType.
     *
     * @param acceptLanguage
     */
    public void setAcceptLanguage(String acceptLanguage) {
        this.acceptLanguage = acceptLanguage;
    }

    /**
     * Gets the forwardedIP value for this RequestOptionsType.
     *
     * @return forwardedIP
     */
    public String getForwardedIP() {
        return forwardedIP;
    }

    /**
     * Sets the forwardedIP value for this RequestOptionsType.
     *
     * @param forwardedIP
     */
    public void setForwardedIP(String forwardedIP) {
        this.forwardedIP = forwardedIP;
    }

    /**
     * Gets the IPv4Address value for this RequestOptionsType.
     *
     * @return IPv4Address
     */
    public String getIPv4Address() {
        return IPv4Address;
    }

    /**
     * Sets the IPv4Address value for this RequestOptionsType.
     *
     * @param IPv4Address
     */
    public void setIPv4Address(String IPv4Address) {
        this.IPv4Address = IPv4Address;
    }

    /**
     * Gets the MAP value for this RequestOptionsType.
     *
     * @return MAP
     */
    public Boolean getMAP() {
        return MAP;
    }

    /**
     * Sets the MAP value for this RequestOptionsType.
     *
     * @param MAP
     */
    public void setMAP(Boolean MAP) {
        this.MAP = MAP;
    }

    /**
     * Gets the randomCode value for this RequestOptionsType.
     *
     * @return randomCode
     */
    public Boolean getRandomCode() {
        return randomCode;
    }

    /**
     * Sets the randomCode value for this RequestOptionsType.
     *
     * @param randomCode
     */
    public void setRandomCode(Boolean randomCode) {
        this.randomCode = randomCode;
    }

    /**
     * Gets the sessionID value for this RequestOptionsType.
     *
     * @return sessionID
     */
    public String getSessionID() {
        return sessionID;
    }

    /**
     * Sets the sessionID value for this RequestOptionsType.
     *
     * @param sessionID
     */
    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    /**
     * Gets the token value for this RequestOptionsType.
     *
     * @return token
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the token value for this RequestOptionsType.
     *
     * @param token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Gets the urlReferrer value for this RequestOptionsType.
     *
     * @return urlReferrer
     */
    public String getUrlReferrer() {
        return urlReferrer;
    }

    /**
     * Sets the urlReferrer value for this RequestOptionsType.
     *
     * @param urlReferrer
     */
    public void setUrlReferrer(String urlReferrer) {
        this.urlReferrer = urlReferrer;
    }

    /**
     * Gets the userAgent value for this RequestOptionsType.
     *
     * @return userAgent
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * Sets the userAgent value for this RequestOptionsType.
     *
     * @param userAgent
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof RequestOptionsType)) return false;
        RequestOptionsType other = (RequestOptionsType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.acceptLanguage==null && other.getAcceptLanguage()==null) ||
             (this.acceptLanguage!=null &&
              this.acceptLanguage.equals(other.getAcceptLanguage()))) &&
            ((this.forwardedIP==null && other.getForwardedIP()==null) ||
             (this.forwardedIP!=null &&
              this.forwardedIP.equals(other.getForwardedIP()))) &&
            ((this.IPv4Address==null && other.getIPv4Address()==null) ||
             (this.IPv4Address!=null &&
              this.IPv4Address.equals(other.getIPv4Address()))) &&
            ((this.MAP==null && other.getMAP()==null) ||
             (this.MAP!=null &&
              this.MAP.equals(other.getMAP()))) &&
            ((this.randomCode==null && other.getRandomCode()==null) ||
             (this.randomCode!=null &&
              this.randomCode.equals(other.getRandomCode()))) &&
            ((this.sessionID==null && other.getSessionID()==null) ||
             (this.sessionID!=null &&
              this.sessionID.equals(other.getSessionID()))) &&
            ((this.token==null && other.getToken()==null) ||
             (this.token!=null &&
              this.token.equals(other.getToken()))) &&
            ((this.urlReferrer==null && other.getUrlReferrer()==null) ||
             (this.urlReferrer!=null &&
              this.urlReferrer.equals(other.getUrlReferrer()))) &&
            ((this.userAgent==null && other.getUserAgent()==null) ||
             (this.userAgent!=null &&
              this.userAgent.equals(other.getUserAgent())));
        __equalsCalc = null;
        return _equals;
    }

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getAcceptLanguage() != null) {
            _hashCode += getAcceptLanguage().hashCode();
        }
        if (getForwardedIP() != null) {
            _hashCode += getForwardedIP().hashCode();
        }
        if (getIPv4Address() != null) {
            _hashCode += getIPv4Address().hashCode();
        }
        if (getMAP() != null) {
            _hashCode += getMAP().hashCode();
        }
        if (getRandomCode() != null) {
            _hashCode += getRandomCode().hashCode();
        }
        if (getSessionID() != null) {
            _hashCode += getSessionID().hashCode();
        }
        if (getToken() != null) {
            _hashCode += getToken().hashCode();
        }
        if (getUrlReferrer() != null) {
            _hashCode += getUrlReferrer().hashCode();
        }
        if (getUserAgent() != null) {
            _hashCode += getUserAgent().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}
