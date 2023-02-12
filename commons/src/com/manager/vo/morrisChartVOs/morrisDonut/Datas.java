package com.manager.vo.morrisChartVOs.morrisDonut;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Admin on 26/10/2016.
 */
@XmlRootElement(name="data")
@XmlAccessorType(XmlAccessType.FIELD)
public class Datas
{
    @XmlElement(name="label")
    String label;

    @XmlElement(name="value")
    double value;

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public double getValue()
    {
        return value;
    }

    public void setValue(double value)
    {
        this.value = value;
    }
}
