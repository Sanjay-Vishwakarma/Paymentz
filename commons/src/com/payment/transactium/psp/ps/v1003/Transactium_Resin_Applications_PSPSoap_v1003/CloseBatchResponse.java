/**
 * CloseBatchResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003;

public class CloseBatchResponse  implements java.io.Serializable {
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CloseBatchResponse.class, true);
    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "CloseBatchResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("closeBatchDetails");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "CloseBatchDetails"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "CloseBatchResponse.CloseBatchDetail"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "CloseBatchResponse.CloseBatchDetail"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("notifications");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Notifications"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("http://schemas.microsoft.com/2003/10/Serialization/Arrays", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("result");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Result"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "ResultCode"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }
    private com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.CloseBatchResponseCloseBatchDetail[] closeBatchDetails;
    private String[] notifications;
    private com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.ResultCode result;
    private Object __equalsCalc = null;
    private boolean __hashCodeCalc = false;


    public CloseBatchResponse() {
    }


    public CloseBatchResponse(
           com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.CloseBatchResponseCloseBatchDetail[] closeBatchDetails,
           String[] notifications,
           com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.ResultCode result) {
           this.closeBatchDetails = closeBatchDetails;
           this.notifications = notifications;
           this.result = result;
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
     * Gets the closeBatchDetails value for this CloseBatchResponse.
     *
     * @return closeBatchDetails
     */
    public com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.CloseBatchResponseCloseBatchDetail[] getCloseBatchDetails() {
        return closeBatchDetails;
    }

    /**
     * Sets the closeBatchDetails value for this CloseBatchResponse.
     *
     * @param closeBatchDetails
     */
    public void setCloseBatchDetails(com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.CloseBatchResponseCloseBatchDetail[] closeBatchDetails) {
        this.closeBatchDetails = closeBatchDetails;
    }

    /**
     * Gets the notifications value for this CloseBatchResponse.
     *
     * @return notifications
     */
    public String[] getNotifications() {
        return notifications;
    }

    /**
     * Sets the notifications value for this CloseBatchResponse.
     *
     * @param notifications
     */
    public void setNotifications(String[] notifications) {
        this.notifications = notifications;
    }

    /**
     * Gets the result value for this CloseBatchResponse.
     *
     * @return result
     */
    public com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.ResultCode getResult() {
        return result;
    }

    /**
     * Sets the result value for this CloseBatchResponse.
     *
     * @param result
     */
    public void setResult(com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.ResultCode result) {
        this.result = result;
    }

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof CloseBatchResponse)) return false;
        CloseBatchResponse other = (CloseBatchResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.closeBatchDetails==null && other.getCloseBatchDetails()==null) ||
             (this.closeBatchDetails!=null &&
              java.util.Arrays.equals(this.closeBatchDetails, other.getCloseBatchDetails()))) &&
            ((this.notifications==null && other.getNotifications()==null) ||
             (this.notifications!=null &&
              java.util.Arrays.equals(this.notifications, other.getNotifications()))) &&
            ((this.result==null && other.getResult()==null) ||
             (this.result!=null &&
              this.result.equals(other.getResult())));
        __equalsCalc = null;
        return _equals;
    }

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getCloseBatchDetails() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getCloseBatchDetails());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getCloseBatchDetails(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getNotifications() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getNotifications());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getNotifications(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getResult() != null) {
            _hashCode += getResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}
