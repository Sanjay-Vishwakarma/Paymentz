package com.manager.vo.morrisChartVOs.morrisDonut;

import com.manager.vo.chartVOs.donutchart.Datasets;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Admin on 26/10/2016.
 */
@XmlRootElement(name="MorrisDonutChartVO")
@XmlAccessorType(XmlAccessType.FIELD)
public class MorrisDonutChartVO
{
    @XmlElement(name="element")
    String element;

    @XmlElement(name="data")
    List<Datas> data;

    @XmlElement(name = "colors")
    List<String> colors;

    public List<Datas> getDatas()
    {
        return data;
    }

    public String getElement()
    {
        return element;
    }

    public void setElement(String element)
    {
        this.element = element;
    }

    public void setDatas(List<Datas> data)

    {
        this.data = data;
    }

    public List<String> getColors()
    {
        return colors;
    }

    public void setColors(List<String> colors)
    {
        this.colors = colors;
    }
}
