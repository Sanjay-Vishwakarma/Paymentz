/**
 * ResponseThreeDSTypeEnrolledStatus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003;

public class ResponseThreeDSTypeEnrolledStatus implements java.io.Serializable {

    private static java.util.HashMap _table_ = new java.util.HashMap();
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ResponseThreeDSTypeEnrolledStatus.class);
    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Response.ThreeDSType.EnrolledStatus"));
    }
    private String _value_;
    // Constructor
    protected ResponseThreeDSTypeEnrolledStatus(String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final String _Yes = "Yes";
    public static final String _No = "No";
    public static final String _Fail = "Fail";
    public static final String _NotAllowed = "NotAllowed";
    public static final ResponseThreeDSTypeEnrolledStatus Yes = new ResponseThreeDSTypeEnrolledStatus(_Yes);
    public static final ResponseThreeDSTypeEnrolledStatus No = new ResponseThreeDSTypeEnrolledStatus(_No);
    public static final ResponseThreeDSTypeEnrolledStatus Fail = new ResponseThreeDSTypeEnrolledStatus(_Fail);
    public static final ResponseThreeDSTypeEnrolledStatus NotAllowed = new ResponseThreeDSTypeEnrolledStatus(_NotAllowed);

    public static ResponseThreeDSTypeEnrolledStatus fromValue(String value)
          throws IllegalArgumentException {
        ResponseThreeDSTypeEnrolledStatus enumeration = (ResponseThreeDSTypeEnrolledStatus)
            _table_.get(value);
        if (enumeration==null) throw new IllegalArgumentException();
        return enumeration;
    }

    public static ResponseThreeDSTypeEnrolledStatus fromString(String value)
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
