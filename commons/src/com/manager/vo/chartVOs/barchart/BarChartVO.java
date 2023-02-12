package com.manager.vo.chartVOs.barchart;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Sagar on 8/18/16.
 */
@XmlRootElement(name="BarChartVO")
@XmlAccessorType(XmlAccessType.FIELD)
public class BarChartVO
{
    @XmlElement(name = "labels")
    List<String> labels;

    @XmlElement(name = "datasets")
    List<Dataset> datasets;

    public List<String> getLabels()
    {
        return labels;
    }
    public void setLabels(List<String> labels)
    {
        this.labels = labels;
    }
    public List<Dataset> getDatasets()
    {
        return datasets;
    }
    public void setDatasets(List<Dataset> datasets)
    {
        this.datasets = datasets;
    }
}
