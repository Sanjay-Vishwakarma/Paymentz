package com.vo.applicationManagerVOs;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 08-09-2017.
 */
@XmlRootElement(name= "FileDetailsListVO")
@XmlAccessorType(XmlAccessType.FIELD)
public class FileDetailsListVO
{
       @XmlElement(name="filedetailsvo")
        List<AppFileDetailsVO> filedetailsvo = new ArrayList<AppFileDetailsVO>();

    public List<AppFileDetailsVO> getFiledetailsvo()
    {
        return filedetailsvo;
    }

    public void setFiledetailsvo(AppFileDetailsVO filedetailsvo)
    {
        this.filedetailsvo.add(filedetailsvo);
    }

    public void setFiledetailsvo(List<AppFileDetailsVO> filedetailsvo)
    {
        this.filedetailsvo = filedetailsvo;
    }
}
