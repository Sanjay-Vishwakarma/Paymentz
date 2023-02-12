
package com.payment.europay.core.message;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MerchantKeyType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="MerchantKeyType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Service-Key"/>
 *     &lt;enumeration value="Routing-Key"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "MerchantKeyType")
@XmlEnum
public enum MerchantKeyType {

    @XmlEnumValue("Service-Key")
    SERVICE_KEY("Service-Key"),
    @XmlEnumValue("Routing-Key")
    ROUTING_KEY("Routing-Key");
    private final String value;

    MerchantKeyType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MerchantKeyType fromValue(String v) {
        for (MerchantKeyType c: MerchantKeyType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
