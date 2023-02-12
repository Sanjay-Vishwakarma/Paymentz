/**
 * RequestType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003;

public class RequestType implements java.io.Serializable
{
    private static java.util.HashMap _table_ = new java.util.HashMap();
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RequestType.class);
    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "RequestType"));
    }
    private String _value_;
    // Constructor
    protected RequestType(String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final String _Sale = "Sale";
    public static final String _PreAuthorisation = "PreAuthorisation";
    public static final String _Completion = "Completion";
    public static final String _Refund = "Refund";
    public static final String _CFT = "CFT";
    public static final String _Void = "Void";
    public static final String _ThreeDSSale = "ThreeDSSale";
    public static final String _ThreeDSPreAuthorisation = "ThreeDSPreAuthorisation";
    public static final String _ThreeDSFinalisation = "ThreeDSFinalisation";
    public static final RequestType Sale = new RequestType(_Sale);
    public static final RequestType PreAuthorisation = new RequestType(_PreAuthorisation);
    public static final RequestType Completion = new RequestType(_Completion);
    public static final RequestType Refund = new RequestType(_Refund);
    public static final RequestType CFT = new RequestType(_CFT);
    public static final RequestType Void = new RequestType(_Void);
    public static final RequestType ThreeDSSale = new RequestType(_ThreeDSSale);
    public static final RequestType ThreeDSPreAuthorisation = new RequestType(_ThreeDSPreAuthorisation);
    public static final RequestType ThreeDSFinalisation = new RequestType(_ThreeDSFinalisation);

    public static RequestType fromValue(String value)
          throws IllegalArgumentException {
        RequestType enumeration = (RequestType)
            _table_.get(value);
        if (enumeration==null) throw new IllegalArgumentException();
        return enumeration;
    }

    public static RequestType fromString(String value)
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
