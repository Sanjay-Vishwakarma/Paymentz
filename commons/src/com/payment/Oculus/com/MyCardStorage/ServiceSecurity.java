/**
 * ServiceSecurity.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.Oculus.com.MyCardStorage;

public class ServiceSecurity  implements java.io.Serializable {
    private java.lang.String serviceUserName;

    private java.lang.String servicePassword;

    private int MCSAccountID;

    private java.lang.String sessionID;

    public ServiceSecurity() {
    }

    public ServiceSecurity(
           java.lang.String serviceUserName,
           java.lang.String servicePassword,
           int MCSAccountID,
           java.lang.String sessionID) {
           this.serviceUserName = serviceUserName;
           this.servicePassword = servicePassword;
           this.MCSAccountID = MCSAccountID;
           this.sessionID = sessionID;
    }


    /**
     * Gets the serviceUserName value for this ServiceSecurity.
     * 
     * @return serviceUserName
     */
    public java.lang.String getServiceUserName() {
        return serviceUserName;
    }


    /**
     * Sets the serviceUserName value for this ServiceSecurity.
     * 
     * @param serviceUserName
     */
    public void setServiceUserName(java.lang.String serviceUserName) {
        this.serviceUserName = serviceUserName;
    }


    /**
     * Gets the servicePassword value for this ServiceSecurity.
     * 
     * @return servicePassword
     */
    public java.lang.String getServicePassword() {
        return servicePassword;
    }


    /**
     * Sets the servicePassword value for this ServiceSecurity.
     * 
     * @param servicePassword
     */
    public void setServicePassword(java.lang.String servicePassword) {
        this.servicePassword = servicePassword;
    }


    /**
     * Gets the MCSAccountID value for this ServiceSecurity.
     * 
     * @return MCSAccountID
     */
    public int getMCSAccountID() {
        return MCSAccountID;
    }


    /**
     * Sets the MCSAccountID value for this ServiceSecurity.
     * 
     * @param MCSAccountID
     */
    public void setMCSAccountID(int MCSAccountID) {
        this.MCSAccountID = MCSAccountID;
    }


    /**
     * Gets the sessionID value for this ServiceSecurity.
     * 
     * @return sessionID
     */
    public java.lang.String getSessionID() {
        return sessionID;
    }


    /**
     * Sets the sessionID value for this ServiceSecurity.
     * 
     * @param sessionID
     */
    public void setSessionID(java.lang.String sessionID) {
        this.sessionID = sessionID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ServiceSecurity)) return false;
        ServiceSecurity other = (ServiceSecurity) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.serviceUserName==null && other.getServiceUserName()==null) || 
             (this.serviceUserName!=null &&
              this.serviceUserName.equals(other.getServiceUserName()))) &&
            ((this.servicePassword==null && other.getServicePassword()==null) || 
             (this.servicePassword!=null &&
              this.servicePassword.equals(other.getServicePassword()))) &&
            this.MCSAccountID == other.getMCSAccountID() &&
            ((this.sessionID==null && other.getSessionID()==null) || 
             (this.sessionID!=null &&
              this.sessionID.equals(other.getSessionID())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getServiceUserName() != null) {
            _hashCode += getServiceUserName().hashCode();
        }
        if (getServicePassword() != null) {
            _hashCode += getServicePassword().hashCode();
        }
        _hashCode += getMCSAccountID();
        if (getSessionID() != null) {
            _hashCode += getSessionID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ServiceSecurity.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("https://MyCardStorage.com/", "ServiceSecurity"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceUserName");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "ServiceUserName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("servicePassword");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "ServicePassword"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("MCSAccountID");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "MCSAccountID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sessionID");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "SessionID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
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
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
