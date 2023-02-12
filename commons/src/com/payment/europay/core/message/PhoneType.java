
package com.payment.europay.core.message;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PhoneType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PhoneType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="mobile_office"/>
 *     &lt;enumeration value="mobile_private"/>
 *     &lt;enumeration value="landline_office"/>
 *     &lt;enumeration value="landline_private"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PhoneType")
@XmlEnum
public enum PhoneType {

    @XmlEnumValue("mobile_office")
    MOBILE_OFFICE("mobile_office"),
    @XmlEnumValue("mobile_private")
    MOBILE_PRIVATE("mobile_private"),
    @XmlEnumValue("landline_office")
    LANDLINE_OFFICE("landline_office"),
    @XmlEnumValue("landline_private")
    LANDLINE_PRIVATE("landline_private");
    private final String value;

    PhoneType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PhoneType fromValue(String v) {
        for (PhoneType c: PhoneType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
