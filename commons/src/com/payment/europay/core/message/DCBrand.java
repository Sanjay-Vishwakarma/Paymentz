
package com.payment.europay.core.message;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DCBrand.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DCBrand">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="DebitCardClassic"/>
 *     &lt;enumeration value="DebitCardSepa"/>
 *     &lt;enumeration value="GiroCard"/>
 *     &lt;enumeration value="Maestro"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DCBrand")
@XmlEnum
public enum DCBrand {

    @XmlEnumValue("DebitCardClassic")
    DEBIT_CARD_CLASSIC("DebitCardClassic"),
    @XmlEnumValue("DebitCardSepa")
    DEBIT_CARD_SEPA("DebitCardSepa"),
    @XmlEnumValue("GiroCard")
    GIRO_CARD("GiroCard"),
    @XmlEnumValue("Maestro")
    MAESTRO("Maestro");
    private final String value;

    DCBrand(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DCBrand fromValue(String v) {
        for (DCBrand c: DCBrand.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
