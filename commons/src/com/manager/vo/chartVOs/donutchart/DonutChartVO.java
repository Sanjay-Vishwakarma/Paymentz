package com.manager.vo.chartVOs.donutchart;

import com.manager.vo.chartVOs.barchart.Dataset;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Created by admin on 8/27/2016.
 */
public class DonutChartVO
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
