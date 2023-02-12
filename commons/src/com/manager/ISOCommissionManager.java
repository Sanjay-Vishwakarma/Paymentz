package com.manager;

import com.manager.vo.TransactionSummaryVO;
import com.manager.vo.payoutVOs.CommissionDetailsVO;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 8/8/16
 * Time: 3:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class ISOCommissionManager
{
   private String accountId;
   private String startDate;
   private String endDate;
   private List<CommissionDetailsVO> dynamicCommissionDetailsVOList;
   private List<CommissionDetailsVO> commissionDetailsVOList;
   private TransactionSummaryVO transactionSummaryVO;

   public ISOCommissionManager(String accountId,String startDate,String endDate,List<CommissionDetailsVO> dynamicCommissionDetailsVOList,List<CommissionDetailsVO> commissionDetailsVOList,TransactionSummaryVO transactionSummaryVO)
   {
     this.accountId=accountId;
     this.startDate=startDate;
     this.endDate=endDate;
     this.dynamicCommissionDetailsVOList=dynamicCommissionDetailsVOList;
     this.commissionDetailsVOList=commissionDetailsVOList;
     this.transactionSummaryVO=transactionSummaryVO;
   }


  public void prepareISOCommissionReport()
  {
    //Step1:Retrieve the rates,transaction details(status wise).



    //Step2:Retrieve all the keyword combination for calculation.

    //Step3:Start iterating on commission,

    //Step4:Save all the calculation in vo.

    //Step4:Prepare the pdf(report file) and excel(transaction file) based on calculation details.

    //Step5:Generate the wire in "ISO Wire Manager".

    //Step6:Return some acknowledgement.
  }
}
