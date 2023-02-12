/**
 * AddressType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.payhost.core.za.co.paygate.www.PayHOST;

public class AddressType  implements java.io.Serializable {
    private org.apache.axis.types.Token[] addressLine;

    private org.apache.axis.types.Token city;

    private com.payment.payhost.core.za.co.paygate.www.PayHOST.CountryType country;

    private org.apache.axis.types.Token state;

    private org.apache.axis.types.Token zip;

    public AddressType() {
    }

    public AddressType(
           org.apache.axis.types.Token[] addressLine,
           org.apache.axis.types.Token city,
           com.payment.payhost.core.za.co.paygate.www.PayHOST.CountryType country,
           org.apache.axis.types.Token state,
           org.apache.axis.types.Token zip) {
           this.addressLine = addressLine;
           this.city = city;
           this.country = country;
           this.state = state;
           this.zip = zip;
    }


    /**
     * Gets the addressLine value for this AddressType.
     * 
     * @return addressLine
     */
    public org.apache.axis.types.Token[] getAddressLine() {
        return addressLine;
    }


    /**
     * Sets the addressLine value for this AddressType.
     * 
     * @param addressLine
     */
    public void setAddressLine(org.apache.axis.types.Token[] addressLine) {
        this.addressLine = addressLine;
    }

    public org.apache.axis.types.Token getAddressLine(int i) {
        return this.addressLine[i];
    }

    public void setAddressLine(int i, org.apache.axis.types.Token _value) {
        this.addressLine[i] = _value;
    }


    /**
     * Gets the city value for this AddressType.
     * 
     * @return city
     */
    public org.apache.axis.types.Token getCity() {
        return city;
    }


    /**
     * Sets the city value for this AddressType.
     * 
     * @param city
     */
    public void setCity(org.apache.axis.types.Token city) {
        this.city = city;
    }


    /**
     * Gets the country value for this AddressType.
     * 
     * @return country
     */
    public com.payment.payhost.core.za.co.paygate.www.PayHOST.CountryType getCountry() {
        return country;
    }


    /**
     * Sets the country value for this AddressType.
     * 
     * @param country
     */
    public void setCountry(com.payment.payhost.core.za.co.paygate.www.PayHOST.CountryType country) {
        this.country = country;
    }


    /**
     * Gets the state value for this AddressType.
     * 
     * @return state
     */
    public org.apache.axis.types.Token getState() {
        return state;
    }


    /**
     * Sets the state value for this AddressType.
     * 
     * @param state
     */
    public void setState(org.apache.axis.types.Token state) {
        this.state = state;
    }


    /**
     * Gets the zip value for this AddressType.
     * 
     * @return zip
     */
    public org.apache.axis.types.Token getZip() {
        return zip;
    }


    /**
     * Sets the zip value for this AddressType.
     * 
     * @param zip
     */
    public void setZip(org.apache.axis.types.Token zip) {
        this.zip = zip;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AddressType)) return false;
        AddressType other = (AddressType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.addressLine==null && other.getAddressLine()==null) || 
             (this.addressLine!=null &&
              java.util.Arrays.equals(this.addressLine, other.getAddressLine()))) &&
            ((this.city==null && other.getCity()==null) || 
             (this.city!=null &&
              this.city.equals(other.getCity()))) &&
            ((this.country==null && other.getCountry()==null) || 
             (this.country!=null &&
              this.country.equals(other.getCountry()))) &&
            ((this.state==null && other.getState()==null) || 
             (this.state!=null &&
              this.state.equals(other.getState()))) &&
            ((this.zip==null && other.getZip()==null) || 
             (this.zip!=null &&
              this.zip.equals(other.getZip())));
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
        if (getAddressLine() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAddressLine());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAddressLine(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getCity() != null) {
            _hashCode += getCity().hashCode();
        }
        if (getCountry() != null) {
            _hashCode += getCountry().hashCode();
        }
        if (getState() != null) {
            _hashCode += getState().hashCode();
        }
        if (getZip() != null) {
            _hashCode += getZip().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AddressType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "AddressType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("addressLine");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "AddressLine"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("city");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "City"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("country");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "Country"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "CountryType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("state");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "State"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("zip");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "Zip"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
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
