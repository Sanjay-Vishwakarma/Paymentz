/**
 * PAResultStatusCode.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003;

public class PAResultStatusCode implements java.io.Serializable {

    private static java.util.HashMap _table_ = new java.util.HashMap();
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PAResultStatusCode.class);
    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "PAResult.StatusCode"));
    }
    private String _value_;
    // Constructor
    protected PAResultStatusCode(String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final String _Full = "Full";
    public static final String _Declined = "Declined";
    public static final String _Unavailable = "Unavailable";
    public static final String _AcknowledgedNotEnrolled = "AcknowledgedNotEnrolled";
    public static final String _Invalid = "Invalid";
    public static final PAResultStatusCode Full = new PAResultStatusCode(_Full);
    public static final PAResultStatusCode Declined = new PAResultStatusCode(_Declined);
    public static final PAResultStatusCode Unavailable = new PAResultStatusCode(_Unavailable);
    public static final PAResultStatusCode AcknowledgedNotEnrolled = new PAResultStatusCode(_AcknowledgedNotEnrolled);
    public static final PAResultStatusCode Invalid = new PAResultStatusCode(_Invalid);

    public static PAResultStatusCode fromValue(String value)
          throws IllegalArgumentException {
        PAResultStatusCode enumeration = (PAResultStatusCode)
            _table_.get(value);
        if (enumeration==null) throw new IllegalArgumentException();
        return enumeration;
    }

    public static PAResultStatusCode fromString(String value)
          throws IllegalArgumentException {
        return fromValue(value);
    }

    public static org.apache.axis.encoding.Serializer getSerializer(
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return
          new org.apache.axis.encoding.ser.EnumSerializer(
            _javaType, _xmlType);
    }

    public static org.apache.axis.encoding.Deserializer getDeserializer(
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return
          new org.apache.axis.encoding.ser.EnumDeserializer(
            _javaType, _xmlType);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    public String getValue() { return _value_;}

    public boolean equals(Object obj) {return (obj == this);}

    public int hashCode() { return toString().hashCode();}

    public String toString() { return _value_;}

    public Object readResolve() throws java.io.ObjectStreamException { return fromValue(_value_);}

}
