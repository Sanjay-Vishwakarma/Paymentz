/**
 * VEResultStatusCode.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003;

public class VEResultStatusCode implements java.io.Serializable {
   
    private static java.util.HashMap _table_ = new java.util.HashMap();
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(VEResultStatusCode.class);
    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "VEResult.StatusCode"));
    }
    private String _value_;
    // Constructor
    protected VEResultStatusCode(String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final String _Enrolled = "Enrolled";
    public static final String _NotEnrolled = "NotEnrolled";
    public static final String _Unavailable = "Unavailable";
    public static final String _Invalid = "Invalid";
    public static final VEResultStatusCode Enrolled = new VEResultStatusCode(_Enrolled);
    public static final VEResultStatusCode NotEnrolled = new VEResultStatusCode(_NotEnrolled);
    public static final VEResultStatusCode Unavailable = new VEResultStatusCode(_Unavailable);
    public static final VEResultStatusCode Invalid = new VEResultStatusCode(_Invalid);

    public static VEResultStatusCode fromValue(String value)
          throws IllegalArgumentException {
        VEResultStatusCode enumeration = (VEResultStatusCode)
            _table_.get(value);
        if (enumeration==null) throw new IllegalArgumentException();
        return enumeration;
    }

    public static VEResultStatusCode fromString(String value)
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
