package com.manager.vo;

import com.directi.pg.Functions;
import com.manager.utils.CommonFunctionUtil;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 8/1/14
 * Time: 1:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class TransactionSummaryVO
{   //util instance
    Functions functions = new Functions();
    CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();
    //Amount and count of transaction as Per status
    double authfailedAmount;
    double authSuccessAmount;

    double authstartedAmount;
    long authstartedCount;

    long  countOfAuthfailed;
    long authSuccessCount;

    double settledAmount;
    long  countOfSettled;

    double reversedAmount;
    long  countOfReversed;

    double payoutAmount;
    long  countOfPayout;

    double chargebackAmount;
    long  countOfChargeback;

    double chargebackReversedAmount;
    long  countOfChargebackReversed;

    //charges variables
    double reserveRefundAmount;
    long   countOfreserveRefund;

    double reserveGeneratedAmount;
    long  countOfReserveGenerated;

    double calculatedRefundAmount;
    long  countOfCalculatedRefund;

    long countOfVerifiedOrder;
    long countOfRefundAlert;

    double totalOfChargeBackAndReversal;
    double totalProcessingAmount;
    long  totalProcessingCount;

    long countOfRetrievalRequest;
    //min and max dates for each status
    String minAuthFailedDate;
    String maxAuthFailedDate;

    String minSettledDate;
    String maxSettledDate;

    String minReversedDate;
    String maxReversedDate;

    String minChargeBackDate;
    String maxChargeBackDate;

    String minOfAllStatusDate;
    String maxOfAllStatusDate;

    long captureSuccessCount;
    double captureSuccessAmount;

    long markForReversalCount;
    double markForReversalAmount;

    long refundReverseCount;
    double refundReverseAmount;

    public double getTotalProcessingAmount()
    {
        return totalProcessingAmount;
    }

    public void setTotalProcessingAmount(double totalProcessingAmount)
    {
        this.totalProcessingAmount = totalProcessingAmount;
    }

    public long getTotalProcessingCount()
    {
        return totalProcessingCount;
    }

    public void setTotalProcessingCount(long totalProcessingCount)
    {
        this.totalProcessingCount = totalProcessingCount;
    }
    //Access Mutator

    public long getCountOfAuthfailed()
    {
        return countOfAuthfailed;
    }

    public void setCountOfAuthfailed(long countOfAuthfailed)
    {
        this.countOfAuthfailed = countOfAuthfailed;
    }



    public long getCountOfSettled()
    {
        return countOfSettled;
    }

    public void setCountOfSettled(long countOfSettled)
    {
        this.countOfSettled = countOfSettled;
    }



    public long getCountOfReversed()
    {
        return countOfReversed;
    }

    public void setCountOfReversed(long countOfReversed)
    {
        this.countOfReversed = countOfReversed;
    }



    public long getCountOfChargeback()
    {
        return countOfChargeback;
    }

    public void setCountOfChargeback(long countOfChargeback)
    {
        this.countOfChargeback = countOfChargeback;
    }

    public double getAuthfailedAmount()
    {
        return authfailedAmount;
    }

    public void setAuthfailedAmount(double authfailedAmount)
    {
        this.authfailedAmount = authfailedAmount;
    }

    public double getSettledAmount()
    {
        return settledAmount;
    }

    public void setSettledAmount(double settledAmount)
    {
        this.settledAmount = settledAmount;
    }

    public double getReversedAmount()
    {
        return reversedAmount;
    }

    public void setReversedAmount(double reversedAmount)
    {
        this.reversedAmount = reversedAmount;
    }

    public double getChargebackAmount()
    {
        return chargebackAmount;
    }

    public void setChargebackAmount(double chargebackAmount)
    {
        this.chargebackAmount = chargebackAmount;
    }

    public double getReserveRefundAmount()
    {
        return reserveRefundAmount;
    }

    public void setReserveRefundAmount(double reserveRefundAmount)
    {
        this.reserveRefundAmount = reserveRefundAmount;
    }

    public long getCountOfreserveRefund()
    {
        return countOfreserveRefund;
    }

    public void setCountOfreserveRefund(long countOfreserveRefund)
    {
        this.countOfreserveRefund = countOfreserveRefund;
    }

    public double getReserveGeneratedAmount()
    {
        return reserveGeneratedAmount;
    }

    public void setReserveGeneratedAmount(double reserveGeneratedAmount)
    {
        this.reserveGeneratedAmount = reserveGeneratedAmount;
    }

    public long getCountOfReserveGenerated()
    {
        return countOfReserveGenerated;
    }

    public void setCountOfReserveGenerated(long countOfReserveGenerated)
    {
        this.countOfReserveGenerated = countOfReserveGenerated;
    }

    public double getCalculatedRefundAmount()
    {
        return calculatedRefundAmount;
    }

    public void setCalculatedRefundAmount(double calculatedRefundAmount)
    {
        this.calculatedRefundAmount = calculatedRefundAmount;
    }

    public long getCountOfCalculatedRefund()
    {
        return countOfCalculatedRefund;
    }

    public void setCountOfCalculatedRefund(long countOfCalculatedRefund)
    {
        this.countOfCalculatedRefund = countOfCalculatedRefund;
    }

    public long getCountOfVerifiedOrder()
    {
        return countOfVerifiedOrder;
    }

    public void setCountOfVerifiedOrder(long countOfVerifiedOrder)
    {
        this.countOfVerifiedOrder = countOfVerifiedOrder;
    }

    public long getCountOfRefundAlert()
    {
        return countOfRefundAlert;
    }

    public void setCountOfRefundAlert(long countOfRefundAlert)
    {
        this.countOfRefundAlert = countOfRefundAlert;
    }

    public double getTotalOfChargeBackAndReversal()
    {
        return totalOfChargeBackAndReversal;
    }

    public void setTotalOfChargeBackAndReversal(double totalOfChargeBackAndReversal)
    {
        this.totalOfChargeBackAndReversal = totalOfChargeBackAndReversal;
    }
    //access mutator for min and max dates as per status

    public String getMinAuthFailedDate()
    {
        return minAuthFailedDate;
    }

    public void setMinAuthFailedDate(String minAuthFailedDate)
    {
        this.minAuthFailedDate = minAuthFailedDate;
    }

    public String getMaxAuthFailedDate()
    {
        return maxAuthFailedDate;
    }

    public void setMaxAuthFailedDate(String maxAuthFailedDate)
    {
        this.maxAuthFailedDate = maxAuthFailedDate;
    }

    public String getMinSettledDate()
    {
        return minSettledDate;
    }

    public void setMinSettledDate(String minSettledDate)
    {
        this.minSettledDate = minSettledDate;
    }

    public String getMaxSettledDate()
    {
        return maxSettledDate;
    }

    public void setMaxSettledDate(String maxSettledDate)
    {
        this.maxSettledDate = maxSettledDate;
    }

    public String getMinReversedDate()
    {
        return minReversedDate;
    }

    public void setMinReversedDate(String minReversedDate)
    {
        this.minReversedDate = minReversedDate;
    }

    public String getMaxReversedDate()
    {
        return maxReversedDate;
    }

    public void setMaxReversedDate(String maxReversedDate)
    {
        this.maxReversedDate = maxReversedDate;
    }

    public String getMinChargeBackDate()
    {
        return minChargeBackDate;
    }

    public void setMinChargeBackDate(String minChargeBackDate)
    {
        this.minChargeBackDate = minChargeBackDate;
    }

    public String getMaxChargeBackDate()
    {
        return maxChargeBackDate;
    }

    public void setMaxChargeBackDate(String maxChargeBackDate)
    {
        this.maxChargeBackDate = maxChargeBackDate;
    }

    public String getMinOfAllStatusDate()
    {
        return minOfAllStatusDate;
    }

    public void setMinOfAllStatusDate(String minOfAllStatusDate)
    {
        if(functions.isValueNull(minOfAllStatusDate))
        {
            this.minOfAllStatusDate = commonFunctionUtil.compareTwoTimestamp(minOfAllStatusDate,this.minOfAllStatusDate,true);
        }
    }

    public String getMaxOfAllStatusDate()
    {
        return maxOfAllStatusDate;
    }

    public void setMaxOfAllStatusDate(String maxOfAllStatusDate)
    {
        if(functions.isValueNull(maxOfAllStatusDate))
        {
            this.maxOfAllStatusDate = commonFunctionUtil.compareTwoTimestamp(maxOfAllStatusDate,this.maxOfAllStatusDate,false);
        }
    }

    public long getCountOfRetrievalRequest()
    {
        return countOfRetrievalRequest;
    }

    public void setCountOfRetrievalRequest(long countOfRetrievalRequest)
    {
        this.countOfRetrievalRequest = countOfRetrievalRequest;
    }

    public double getAuthSuccessAmount()
    {
        return authSuccessAmount;
    }

    public void setAuthSuccessAmount(double authSuccessAmount)
    {
        this.authSuccessAmount = authSuccessAmount;
    }

    public long getAuthSuccessCount()
    {
        return authSuccessCount;
    }

    public void setAuthSuccessCount(long authSuccessCount)
    {
        this.authSuccessCount = authSuccessCount;
    }

    public long getCaptureSuccessCount()
    {
        return captureSuccessCount;
    }

    public void setCaptureSuccessCount(Long captureSuccessCount)
    {
        this.captureSuccessCount = captureSuccessCount;
    }

    public double getCaptureSuccessAmount()
    {
        return captureSuccessAmount;
    }

    public void setCaptureSuccessAmount(Double captureSuccessAmount)
    {
        this.captureSuccessAmount = captureSuccessAmount;
    }

    public long getMarkForReversalCount()
    {
        return markForReversalCount;
    }

    public void setMarkForReversalCount(Long markForReversalCount)
    {
        this.markForReversalCount = markForReversalCount;
    }

    public double getMarkForReversalAmount()
    {
        return markForReversalAmount;
    }

    public void setMarkForReversalAmount(Double markForReversalAmount)
    {
        this.markForReversalAmount = markForReversalAmount;
    }

    public long getAuthstartedCount()
    {
        return authstartedCount;
    }

    public void setAuthstartedCount(long authstartedCount)
    {
        this.authstartedCount = authstartedCount;
    }

    public double getAuthstartedAmount()
    {
        return authstartedAmount;
    }

    public void setAuthstartedAmount(double authstartedAmount)
    {
        this.authstartedAmount = authstartedAmount;
    }

    public double getPayoutAmount()
    {
        return payoutAmount;
    }

    public void setPayoutAmount(double payoutAmount)
    {
        this.payoutAmount = payoutAmount;
    }

    public long getCountOfPayout()
    {
        return countOfPayout;
    }

    public void setCountOfPayout(long countOfPayout)
    {
        this.countOfPayout = countOfPayout;
    }

    public double getChargebackReversedAmount()
    {
        return chargebackReversedAmount;
    }

    public void setChargebackReversedAmount(double chargebackReversedAmount)
    {
        this.chargebackReversedAmount = chargebackReversedAmount;
    }

    public long getCountOfChargebackReversed()
    {
        return countOfChargebackReversed;
    }

    public void setCountOfChargebackReversed(long countOfChargebackReversed)
    {
        this.countOfChargebackReversed = countOfChargebackReversed;
    }

    public long getRefundReverseCount()
    {
        return refundReverseCount;
    }

    public void setRefundReverseCount(long refundReverseCount)
    {
        this.refundReverseCount = refundReverseCount;
    }

    public double getRefundReverseAmount()
    {
        return refundReverseAmount;
    }

    public void setRefundReverseAmount(double refundReverseAmount)
    {
        this.refundReverseAmount = refundReverseAmount;
    }
}
