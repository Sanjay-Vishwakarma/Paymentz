package com.manager.vo.morrisBarVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Set;

/**
 * Created by Admin on 16/12/2016.
 */
@XmlRootElement(name = "MorrisBarChartVO")
@XmlAccessorType(XmlAccessType.FIELD)
public class MorrisBarChartVO
{
    @XmlElement(name = "element")
    String element;

    @XmlElement(name = "data")
    List<Data> data;

    @XmlElement(name = "xkey")
    String xkey;

    @XmlElement(name = "ykeys")
    String[] ykeys;

    @XmlElement(name = "labels")
    String[] labels;

    @XmlElement(name = "barColors")
    List<String> barColors;

    public String getElement()
    {
        return element;
    }

    public void setElement(String element)
    {
        this.element = element;
    }

    public List<Data> getData()
    {
        return data;
    }

    public void setData(List<Data> data)
    {
        this.data = data;
    }

    public String getXkey()
    {
        return xkey;
    }

    public void setXkey(String xkey)
    {
        this.xkey = xkey;
    }

    public String[] getYkeys()
    {
        return ykeys;
    }

    public void setYkeys(String[] ykeys)
    {
        this.ykeys = ykeys;
    }

    public String[] getLabels()
    {
        return labels;
    }

    public void setLabels(String[] labels)
    {
        this.labels = labels;
    }

    public List<String> getBarColors()
    {
        return barColors;
    }

    public void setBarColors(List<String> barColors)
    {
        this.barColors = barColors;
    }
}
