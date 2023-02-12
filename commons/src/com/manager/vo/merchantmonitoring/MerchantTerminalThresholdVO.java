package com.manager.vo.merchantmonitoring;

import com.manager.vo.TerminalVO;

/**
 * Created by admin on 3/31/2016.
 */
public class MerchantTerminalThresholdVO
{
    String memberId;
    TerminalVO terminalVO;
    TerminalLimitsVO terminalLimitsVO;
    TerminalThresholdsVO terminalThresholdsVO;
    TerminalProcessingDetailsVO terminalProcessingDetailsVO;

    public TerminalVO getTerminalVO()
    {
        return terminalVO;
    }

    public void setTerminalVO(TerminalVO terminalVO)
    {
        this.terminalVO = terminalVO;
    }

    public TerminalLimitsVO getTerminalLimitsVO()
    {
        return terminalLimitsVO;
    }

    public void setTerminalLimitsVO(TerminalLimitsVO terminalLimitsVO)
    {
        this.terminalLimitsVO = terminalLimitsVO;
    }

    public TerminalThresholdsVO getTerminalThresholdsVO()
    {
        return terminalThresholdsVO;
    }

    public void setTerminalThresholdsVO(TerminalThresholdsVO terminalThresholdsVO)
    {
        this.terminalThresholdsVO = terminalThresholdsVO;
    }

    public TerminalProcessingDetailsVO getTerminalProcessingDetailsVO()
    {
        return terminalProcessingDetailsVO;
    }

    public void setTerminalProcessingDetailsVO(TerminalProcessingDetailsVO terminalProcessingDetailsVO)
    {
        this.terminalProcessingDetailsVO = terminalProcessingDetailsVO;
    }

    public String getMemberId()
    {
        return memberId;
    }

    public void setMemberId(String memberId)
    {
        this.memberId = memberId;
    }
}
