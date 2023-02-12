package com.manager.vo.chartVOs.barchart;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagar on 8/18/16.
 */
@XmlRootElement(name = "datasets")
@XmlAccessorType(XmlAccessType.FIELD)
public class Dataset
{

    @XmlElement(name = "label")
    String label;

    @XmlElement(name = "backgroundColor")
    List<String> backgroundColor;

    @XmlElement(name = "borderColor")
    List<String> borderColor;

    @XmlElement(name = "borderWidth")
    String borderWidth;

    @XmlElement(name = "hoverBorderColor")
    List<String> hoverBorderColor;



    @XmlElement(name = "data")
    List<Double> data;

    public String getLabel()
    {
        return label;
    }
    public void setLabel(String label)
    {
        this.label = label;
    }

    public List<String> getBackgroundColor()
    {
        return backgroundColor;
    }

    public void setBackgroundColor(List<String> backgroundColor)
    {
        this.backgroundColor = backgroundColor;
    }

    public List<String> getBorderColor()
    {
        return borderColor;
    }

    public void setBorderColor(List<String> borderColor)
    {
        this.borderColor = borderColor;
    }

    public List<Double> getData()
    {
        return data;
    }

    public void setData(List<Double> data)
    {
        this.data = data;
    }

    public String getBorderWidth()
    {
        return borderWidth;
    }

    public void setBorderWidth(String borderWidth)
    {
        this.borderWidth = borderWidth;
    }

    public List<String> getHoverBorderColor()
    {
        return hoverBorderColor;
    }

    public void setHoverBorderColor(List<String> hoverBorderColor)
    {
        this.hoverBorderColor = hoverBorderColor;
    }
}
