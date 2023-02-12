package com.manager.vo.merchantmonitoring;

import com.payment.Mail.FileAttachmentVO;

import java.util.List;

/**
 * Created by admin on 6/3/2016.
 */
public class MonitoringAttachmentAlertDetailsVO extends MonitoringAlertDetailVO
{
    List<FileAttachmentVO> fileAttachmentVOList;
    FileAttachmentVO fileAttachmentVO;

    public List<FileAttachmentVO> getFileAttachmentVOList()
    {
        return fileAttachmentVOList;
    }

    public void setFileAttachmentVOList(List<FileAttachmentVO> fileAttachmentVOList)
    {
        this.fileAttachmentVOList = fileAttachmentVOList;
    }

    public FileAttachmentVO getFileAttachmentVO()
    {
        return fileAttachmentVO;
    }

    public void setFileAttachmentVO(FileAttachmentVO fileAttachmentVO)
    {
        this.fileAttachmentVO = fileAttachmentVO;
    }
}
