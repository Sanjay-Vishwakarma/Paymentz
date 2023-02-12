
package com.payment.europay.core.message;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Connector.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Connector">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Andaz"/>
 *     &lt;enumeration value="CompuTop"/>
 *     &lt;enumeration value="PayOn"/>
 *     &lt;enumeration value="DummyConnector"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "Connector")
@XmlEnum
public enum Connector {

    @XmlEnumValue("Andaz")
    ANDAZ("Andaz"),
    @XmlEnumValue("CompuTop")
    COMPU_TOP("CompuTop"),
    @XmlEnumValue("PayOn")
    PAY_ON("PayOn"),
    @XmlEnumValue("DummyConnector")
    DUMMY_CONNECTOR("DummyConnector");
    private final String value;

    Connector(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Connector fromValue(String v) {
        for (Connector c: Connector.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
