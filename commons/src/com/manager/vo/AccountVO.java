package com.manager.vo;

import com.manager.vo.payoutVOs.*;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 26/7/14
 * Time: 7:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class AccountVO
{

    String Currency;
    //todo temporary has to be removed after asking from sir
    InputDateVO inputDateVO;

    double totalOfReserveRefundAmount;
    double totalFundedToBank;
    double totalBalanceAmount;

    double totalFees;
    double totalProcessingAmount;
    double totalOfReserveGeneratedAmount;
    double totalReverseAndChargebackAmount;
    double totalOfGrossChargesAmount;

    WireChargeVO finalWireVO;
    GrossChargeVO grossTypeCharges= null;
    CalculatedReserveRefundVO calculatedReserveRefund =  null;

    HashMap<String,ReserveRefundVO> reserveRefundMap = new LinkedHashMap<String, ReserveRefundVO>();
    HashMap<String,ReserveGeneratedVO> reserveGeneratedMap = new LinkedHashMap<String, ReserveGeneratedVO>();



    HashMap<String,ChargeDetailsVO> chargesMap =  new LinkedHashMap<String, ChargeDetailsVO>();

    WireAmountVO wireAmountVO;
    TransactionSummaryVO transactionSummaryVO;
    MerchantDetailsVO merchantDetailsVO;
    TerminalVO terminalVO;
    RollingReserveDateVO rollingReserveDateVO;

    public void addReserveGeneratedMap(String chargeName, ReserveGeneratedVO reserveVO)
    {
        reserveGeneratedMap.put(chargeName,reserveVO);
    }

    public void addReserveRefundMap(String chargeName,ReserveRefundVO chargeDetailsVO)
    {
        reserveRefundMap.put(chargeName,chargeDetailsVO);
    }


    public void addChargesMap(String chargeName,ChargeDetailsVO chargeDetailsVO)
    {
        chargesMap.put(chargeName,chargeDetailsVO);
    }



    //wireAmount setter and getter

    public WireChargeVO getFinalWireVO()
    {
        return finalWireVO;
    }

    public void setFinalWireVO(WireChargeVO finalWireVO)
    {
        this.finalWireVO = finalWireVO;
    }
    //totalBalanceAmount


    public double getTotalBalanceAmount()
    {
        return totalBalanceAmount;
    }

    public void setTotalBalanceAmount(double totalBalanceAmount)
    {
        this.totalBalanceAmount = totalBalanceAmount;
    }

    //TotalFundedToBank
    public double getTotalFundedToBank()
    {
        return totalFundedToBank;
    }

    public void setTotalFundedToBank(double totalFundedToBank)
    {
        this.totalFundedToBank = totalFundedToBank;
    }




    //getter for HashMap


    public double getTotalOfReserveRefundAmount()
    {
        return totalOfReserveRefundAmount;
    }

    public void setTotalOfReserveRefundAmount(double totalOfReserveRefundAmount)
    {
        this.totalOfReserveRefundAmount = totalOfReserveRefundAmount;
    }

    public double getTotalOfReserveGeneratedAmount()
    {
        return totalOfReserveGeneratedAmount;
    }


    public void setTotalOfReserveGeneratedAmount(double totalOfReserveGeneratedAmount)
    {
        this.totalOfReserveGeneratedAmount = totalOfReserveGeneratedAmount;
    }


    public HashMap<String, ReserveGeneratedVO> getReserveGeneratedMap()
    {
        return reserveGeneratedMap;
    }

    public HashMap<String, ReserveRefundVO> getReserveRefundMap()
    {
        return reserveRefundMap;
    }



    public HashMap<String, ChargeDetailsVO> getChargesMap()
    {
        return chargesMap;
    }



    //setting and getting wireAmountVO

    public WireAmountVO getWireAmountVO()
    {
        return wireAmountVO;
    }

    public void setWireAmountVO(WireAmountVO wireAmountVO)
    {
        this.wireAmountVO = wireAmountVO;
    }

    public TransactionSummaryVO getTransactionSummaryVO()
    {
        return transactionSummaryVO;
    }

    public CalculatedReserveRefundVO getCalculatedReserveRefund()
    {
        return calculatedReserveRefund;
    }

    public void setCalculatedReserveRefund(CalculatedReserveRefundVO calculatedReserveRefund)
    {
        this.calculatedReserveRefund = calculatedReserveRefund;
    }

    public void setTransactionSummaryVO(TransactionSummaryVO transactionSummaryVO)
    {
        this.transactionSummaryVO = transactionSummaryVO;
    }

    public double getTotalFees()
    {
        return totalFees;
    }

    public void setTotalFees(double totalFees)
    {
        this.totalFees = totalFees;
    }

    public double getTotalProcessingAmount()
    {
        return totalProcessingAmount;
    }

    public void setTotalProcessingAmount(double totalProcessingAmount)
    {
        this.totalProcessingAmount = totalProcessingAmount;
    }

    public double getTotalReverseAndChargebackAmount()
    {
        return totalReverseAndChargebackAmount;
    }

    public void setTotalReverseAndChargebackAmount(double totalReverseAndChargebackAmount)
    {
        this.totalReverseAndChargebackAmount = totalReverseAndChargebackAmount;
    }


    public GrossChargeVO getGrossTypeCharges()
    {
        return grossTypeCharges;
    }

    public void setGrossTypeCharges(GrossChargeVO grossTypeCharges)
    {
        this.grossTypeCharges = grossTypeCharges;
    }

    public double getTotalOfGrossChargesAmount()
    {
        return totalOfGrossChargesAmount;
    }

    public void setTotalOfGrossChargesAmount(double totalOfGrossChargesAmount)
    {
        this.totalOfGrossChargesAmount = totalOfGrossChargesAmount;
    }

    //merchantDetailsVo getter ANd Setter & TerminalVO setter & getter

    public MerchantDetailsVO getMerchantDetailsVO()
    {
        return merchantDetailsVO;
    }

    public void setMerchantDetailsVO(MerchantDetailsVO merchantDetailsVO)
    {
        this.merchantDetailsVO = merchantDetailsVO;
    }

    public TerminalVO getTerminalVO()
    {
        return terminalVO;
    }

    public void setTerminalVO(TerminalVO terminalVO)
    {
        this.terminalVO = terminalVO;
    }

    public String getCurrency()
    {
        return Currency;
    }

    public void setCurrency(String currency)
    {
        Currency = currency;
    }

    public InputDateVO getInputDateVO()
    {
        return inputDateVO;
    }

    public void setInputDateVO(InputDateVO inputDateVO)
    {
        this.inputDateVO = inputDateVO;
    }

    public RollingReserveDateVO getRollingReserveDateVO()
    {
        return rollingReserveDateVO;
    }

    public void setRollingReserveDateVO(RollingReserveDateVO rollingReserveDateVO)
    {
        this.rollingReserveDateVO = rollingReserveDateVO;
    }
}
