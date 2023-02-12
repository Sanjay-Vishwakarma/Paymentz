package com.manager.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 8/18/14
 * Time: 4:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class TransactionReportVO
{
    //this vo consist of the value of variable according to the status
    HashMap<String,TransactionVO> transactionVOHashMap = new HashMap<String,TransactionVO>();
    HashMap<String,StringBuilder> chartContent=new HashMap<String,StringBuilder>();


    double totalAmount;

    long totalCount;

    //inputDateVo variable
    InputDateVO inputDateVO;
    TerminalVO terminalVO;


    public HashMap<String, TransactionVO> getTransactionVOHashMap()
    {
        return transactionVOHashMap;
    }

    public void setTransactionVOHashMap(String status,TransactionVO transactionVO)
    {
        if(transactionVOHashMap.get(status)!=null)
        {
            TransactionVO transactionVO1=transactionVOHashMap.get(status);
            transactionVO1.setCount(transactionVO1.getCount()+transactionVO.getCount());
            transactionVO1.setAmount(String.valueOf(Double.valueOf(transactionVO1.getAmount())+Double.valueOf(transactionVO.getAmount())));
            transactionVO1.setCaptureAmount(transactionVO1.getCaptureAmount()+transactionVO.getCaptureAmount());
            transactionVOHashMap.put(status,transactionVO1);
        }
        else
        {
            transactionVOHashMap.put(status,transactionVO);
        }
    }

    public HashMap<String, StringBuilder> getChartContent()
    {
        return chartContent;
    }

    public void setChartContent(String status,StringBuilder chartContent)
    {
        this.chartContent.put(status,chartContent);
    }

    public double getTotalAmount()
    {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount)
    {
        this.totalAmount = totalAmount;
    }

    public long getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(long totalCount)
    {
        this.totalCount = totalCount;
    }

    public InputDateVO getInputDateVO()
    {
        return inputDateVO;
    }

    public void setInputDateVO(InputDateVO inputDateVO)
    {
        this.inputDateVO = inputDateVO;
    }

    public TerminalVO getTerminalVO()
    {
        return terminalVO;
    }

    public void setTerminalVO(TerminalVO terminalVO)
    {
        this.terminalVO = terminalVO;
    }
}
