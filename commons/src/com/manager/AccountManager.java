package com.manager;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.logicboxes.util.ApplicationProperties;
import com.manager.dao.AccountDAO;
import com.manager.dao.MerchantDAO;
import com.manager.enums.*;
import com.manager.utils.AccountUtil;
import com.manager.vo.*;
import com.manager.vo.payoutVOs.*;
import com.payment.exceptionHandler.PZDBViolationException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 26/7/14
 * Time: 7:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class AccountManager
{
    private static Logger logger=new Logger(AccountManager.class.getName());
    private static Functions functions = new Functions();
    // properties from setting.properties
    String pdfReportFilePath = ApplicationProperties.getProperty("PAYOUT_REPORT_FILE_PATH");
    String xlsSettledFilePath=ApplicationProperties.getProperty("SETTLEMENT_FILE_PATH");


    //Managers
    TerminalManager terminalManager = new TerminalManager();

    //Dao Object
    AccountDAO accountDAO= new AccountDAO();
    MerchantDAO merchantDAO=new MerchantDAO();
    //Util initialization
    AccountUtil accountUtil= new AccountUtil();

    public AccountVO generatePayoutReport(InputDateVO inputDateVO,TerminalVO terminalVO) throws PZDBViolationException
    {
        //VO objects
        TransactionSummaryVO transSummaryVO,transSummaryVO1;
        RollingReserveDateVO rollingReserveDateVO = new RollingReserveDateVO();

        WireAmountVO wireAmountVO;
        MerchantDetailsVO merchantDetailsVO;

        //List of charge VO
        List<ChargeVO> chargeVOList;

        //Account VO
        AccountVO accountVO = null;

        //Reporting Date
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sReportingDate =targetFormat.format(new Date());
        inputDateVO.setReportingDate(sReportingDate);
        //adding minTransactionDate and maxTransactionDate format(yyyy-MM-dd HH:mm:ss)
            /*logger.debug("sMinDate::"+inputDateVO.getFyear()+"-"+inputDateVO.getFmonth()+"-"+inputDateVO.getFdate()+" 00:00:00");
            inputDateVO.setsMinTransactionDate(inputDateVO.getFyear()+"-"+inputDateVO.getFmonth()+"-"+inputDateVO.getFdate()+" 00:00:00");
            inputDateVO.setsMaxTransactionDate(inputDateVO.getTyear()+"-"+inputDateVO.getTmonth()+"-"+inputDateVO.getTdate()+" 23:59:59");
            logger.debug("sMinTransactionDate::"+inputDateVO.getsMinTransactionDate());*/

        //checking if charges are mapped with TerminalId
        if(terminalManager.isChargesMappedTerminal(terminalVO.getTerminalId()))
        {
            logger.debug("charges mapped to terminal ::" + terminalVO.getTerminalId());
            //instantiating accountVO
            accountVO = new AccountVO();

            //Getting Table Name and Currency  for given Terminal
            GatewayAccount account = GatewayAccountService.getGatewayAccount(terminalVO.getAccountId());
            String pgtypeId = account.getPgTypeId();
            GatewayType gatewayType = GatewayTypeService.getGatewayType(pgtypeId);
            String sTableName = Database.getTableName(gatewayType.getGateway());
            String sCurrency = gatewayType.getCurrency();

            //getting contact_person and company_name from merchantDetailVo
            merchantDetailsVO = merchantDAO.getMemberDetails(terminalVO.getMemberId());

            wireAmountVO = new WireAmountVO();
            //Get Unpaid Balance Amount
            accountDAO.setUnpaidBalanceAmount(terminalVO, wireAmountVO);

            //Get Wire Amount
            accountDAO.setWireAmount(terminalVO, wireAmountVO);

            //Wire Balance Amount   = sum of unpaid wire + sum of unpaid amounts
            wireAmountVO.setWireBalanceAmount(wireAmountVO.getUnpaidAmount() + wireAmountVO.getUnpaidBalanceAmount());

            //Getting all the charges of member based on paymodeid,cardtypeid,accountid
            chargeVOList = accountDAO.getChargesAsPerTerminal(terminalVO);

            //Getting transactions summary as per Dates
            //Getting transaction Summary for reserve generated
            transSummaryVO = accountDAO.getTransactionSummaryByDtstamp(sTableName, inputDateVO, terminalVO);

            transSummaryVO1=accountDAO.getTransactionSummaryByTimestamp(sTableName, inputDateVO, terminalVO);

            //for reversed and chargeBack
            transSummaryVO.setReversedAmount(transSummaryVO1.getReversedAmount());
            transSummaryVO.setCountOfReversed(transSummaryVO1.getCountOfReversed());

            transSummaryVO.setChargebackAmount(transSummaryVO1.getChargebackAmount());
            transSummaryVO.setCountOfChargeback(transSummaryVO1.getCountOfChargeback());

            transSummaryVO.setReserveGeneratedAmount(transSummaryVO.getChargebackAmount()+transSummaryVO.getReversedAmount()+transSummaryVO.getSettledAmount()+transSummaryVO.getCaptureSuccessAmount());
            transSummaryVO.setCountOfReserveGenerated(transSummaryVO.getCountOfChargeback()+transSummaryVO.getCountOfReversed()+transSummaryVO.getCountOfSettled()+transSummaryVO.getCaptureSuccessCount());
            transSummaryVO.setTotalOfChargeBackAndReversal(transSummaryVO1.getTotalOfChargeBackAndReversal());

            //transfer of timeAttribute from transactionSummaryVO1 to transactionVO
            transSummaryVO.setMinReversedDate(transSummaryVO1.getMinReversedDate());
            transSummaryVO.setMaxReversedDate(transSummaryVO1.getMaxReversedDate());
            transSummaryVO.setMinChargeBackDate(transSummaryVO1.getMinChargeBackDate());
            transSummaryVO.setMaxChargeBackDate(transSummaryVO1.getMaxChargeBackDate());

            double vDblGeneratedRollingReserveTotal = 0.0;
            double vDblRefundedRollingReserveTotal = 0.0;
            double vDblChargeFeeTotal = 0.0;
            double vDblTotalBalanceAmount = 0.0;
            double vDblGrossChargesTotal = 0.0;
            double vReserveRefundedTotal = 0.0;

            logger.debug("transactionVO::  AuthFailedCount::"+transSummaryVO.getCountOfAuthfailed()+" AuthFailedAmount::"+transSummaryVO.getAuthfailedAmount());
            logger.debug("transactionVO::  chargeBackCount::"+transSummaryVO.getCountOfChargeback()+" chargeBackAmount::"+transSummaryVO.getChargebackAmount());
            logger.debug("transactionVO::  ReversedCount::"+transSummaryVO.getCountOfReversed()+" ReversedAmount::"+transSummaryVO.getReversedAmount());
            logger.debug("transactionVO::  SettledCount::"+transSummaryVO.getCountOfSettled()+" SettledAmount::"+transSummaryVO.getSettledAmount());
            logger.debug("transactionVO::  ReserveGeneratedCount::"+transSummaryVO.getCountOfReserveGenerated()+" ReserveGeneratedAmount::"+transSummaryVO.getReserveGeneratedAmount());

            //Loop through each ChargeVO object
            for (ChargeVO chargeVO : chargeVOList)
            {
                //Getting Charge Details
                ChargeDetailsVO reserveVO = getChargeDetails(chargeVO, transSummaryVO, terminalVO, sTableName);
                if (reserveVO != null)
                {
                    //Check for instance of VO
                    if (reserveVO instanceof ReserveGeneratedVO)
                    {
                        //totaling thoroughout the iteration
                        vDblGeneratedRollingReserveTotal = vDblGeneratedRollingReserveTotal + reserveVO.getTotal();
                        //adding Reserve Generated Map
                        accountVO.addReserveGeneratedMap(reserveVO.getChargeName(), (ReserveGeneratedVO) reserveVO);
                    }

                    else if (reserveVO instanceof ReserveRefundVO)
                    {
                        //totaling throughout the iteration
                        vDblRefundedRollingReserveTotal = vDblRefundedRollingReserveTotal + reserveVO.getTotal();
                        //adding ReserveRefundMap
                        rollingReserveDateVO = ((ReserveRefundVO) reserveVO).getRollingReserveDateVO();
                        //System.out.println("rollimg reserve date=====s" + rollingReserveDateVO.getRollingReserveStartDate() + "" + rollingReserveDateVO.getRollingReserveEndDate());

                        accountVO.addReserveRefundMap(reserveVO.getChargeName(), (ReserveRefundVO) reserveVO);
                    }
                    else if (reserveVO instanceof CalculatedReserveRefundVO)
                    {
                        //adding calculatedReserveRefundMap
                        accountVO.setCalculatedReserveRefund((CalculatedReserveRefundVO) reserveVO);
                    }
                    else
                    {
                        //totaling throughout the iteration
                        vDblChargeFeeTotal = vDblChargeFeeTotal + reserveVO.getTotal();
                        //adding chargesMap
                        accountVO.addChargesMap(reserveVO.getChargeName(), reserveVO);
                    }
                }
                else
                {
                    ChargeDetailsVO chargeDetailsVO = getOtherCharges(chargeVO, transSummaryVO, terminalVO, inputDateVO, wireAmountVO,sTableName);

                    if (chargeDetailsVO != null)
                    {
                        if (chargeDetailsVO instanceof GrossChargeVO)
                        {
                            //adding to grossTypeChargeMap
                            accountVO.setGrossTypeCharges((GrossChargeVO) chargeDetailsVO);
                        }
                        else if (chargeDetailsVO instanceof WireChargeVO)
                        {
                            if (chargeDetailsVO != null)
                            {
                                accountVO.setFinalWireVO((WireChargeVO) chargeDetailsVO);
                            }
                        }
                        else
                        {    //totaling throughout the iteration
                            vDblChargeFeeTotal = vDblChargeFeeTotal + chargeDetailsVO.getTotal();
                            //adding chargesMap
                            accountVO.addChargesMap(chargeDetailsVO.getChargeName(), chargeDetailsVO);
                        }
                    }
                }
            }
            //calculation of Reserve Refunded
            vReserveRefundedTotal = calculatedReserveRefund(accountVO, vDblRefundedRollingReserveTotal);

            double vDblDeduct = vDblChargeFeeTotal + transSummaryVO.getReversedAmount() + transSummaryVO.getChargebackAmount() + vDblGeneratedRollingReserveTotal;
            vDblTotalBalanceAmount = transSummaryVO.getReserveGeneratedAmount() - vDblDeduct;

            //calculation of final Gross Balance Amount
            vDblGrossChargesTotal = getFinalGrossBalanceAmount(accountVO, vDblTotalBalanceAmount);

            //transactionSummary And wireAmountVo added to AccountVO
            accountVO.setTransactionSummaryVO(transSummaryVO);
            accountVO.setWireAmountVO(wireAmountVO);
            //adding MerchantDetailsVO  & terminalVO
            accountVO.setMerchantDetailsVO(merchantDetailsVO);
            accountVO.setTerminalVO(terminalVO);
            //charges added to accountVO (Total)
            accountVO.setRollingReserveDateVO(rollingReserveDateVO);
            accountVO.setInputDateVO(inputDateVO);
            accountVO.setCurrency(sCurrency);
            accountVO.setTotalBalanceAmount(Double.valueOf(accountUtil.convert2Decimal(String.valueOf(vDblTotalBalanceAmount))));
            accountVO.setTotalOfReserveRefundAmount(vReserveRefundedTotal);
            accountVO.setTotalReverseAndChargebackAmount(transSummaryVO.getTotalOfChargeBackAndReversal());
            accountVO.setTotalOfGrossChargesAmount(vDblGrossChargesTotal);
            accountVO.setTotalOfReserveGeneratedAmount(vDblGeneratedRollingReserveTotal);
            accountVO.setTotalFees(vDblChargeFeeTotal);
            accountVO.setTotalProcessingAmount(transSummaryVO.getReserveGeneratedAmount());

            //logger.debug("TotalBalanceAmount::"+vDblTotalBalanceAmount+" wireBalanceAmount::"+wireAmountVO.getWireBalanceAmount()+" refundRollingReservetotal::"+vReserveRefundedTotal+" paidAmount::"+wireAmountVO.getPaidAmount()+" finalWire total::"+accountVO.getFinalWireVO().getTotal()+" GrossChargesTotal::"+vDblGrossChargesTotal);
            if (vDblTotalBalanceAmount != 0 && wireAmountVO.getWireBalanceAmount() != 0 && vReserveRefundedTotal != 0 && wireAmountVO.getPaidAmount() != 0 && accountVO.getFinalWireVO().getTotal() != 0 && vDblGrossChargesTotal != 0)
            {
                logger.debug("TotalBalanceAmount===" + vDblTotalBalanceAmount);
                logger.debug("wireBalanceAmount==="+wireAmountVO.getWireBalanceAmount());
                logger.debug("refundRollingReservetotal==="+vReserveRefundedTotal);
                logger.debug("paidAmount==="+wireAmountVO.getPaidAmount());
                logger.debug("finalWire total==="+accountVO.getFinalWireVO().getTotal());
                logger.debug("GrossChargesTotal==="+vDblGrossChargesTotal);
                accountVO.setTotalFundedToBank(Double.valueOf(accountUtil.convert2Decimal(String.valueOf((vDblTotalBalanceAmount + wireAmountVO.getWireBalanceAmount() + vReserveRefundedTotal) - (wireAmountVO.getPaidAmount() + accountVO.getFinalWireVO().getTotal() + (-1 * vDblGrossChargesTotal))))));
            }
        }
        return accountVO;
    }

    public WireReportsVO getWireReports(InputDateVO inputDateVO,TerminalVO terminalVO,PaginationVO paginationVO,String isPaid) throws PZDBViolationException
    {
        WireReportsVO wireReportsVO = new WireReportsVO();
        wireReportsVO.setWireReportsList(accountDAO.getListOfMerchantWireReports(inputDateVO,terminalVO,paginationVO,isPaid));
        wireReportsVO.setPaginationVO(paginationVO);
        return wireReportsVO;
    }
    //getting list of Merchant wire Report under current partner
    public WireReportsVO getWireReportsForPartner(InputDateVO inputDateVO,TerminalVO terminalVO,PaginationVO paginationVO,String isPaid,String toId) throws PZDBViolationException
    {
        WireReportsVO wireReportsVO = new WireReportsVO();
        wireReportsVO.setWireReportsList(accountDAO.getListOfMerchantWireReportsForPartner(inputDateVO,terminalVO,paginationVO,isPaid,toId));
        wireReportsVO.setPaginationVO(paginationVO);
        return wireReportsVO;
    }

    //getting list of partner Wire Report
    public WireReportsVO getPartnerWireReports(InputDateVO inputDateVO,PaginationVO paginationVO,String isPaid,String partnerId) throws PZDBViolationException
    {
        WireReportsVO wireReportsVO = new WireReportsVO();
        wireReportsVO.setWireReportsList(accountDAO.getListOfPartnerWireReports(inputDateVO,paginationVO,isPaid,partnerId));
        wireReportsVO.setPaginationVO(paginationVO);
        return wireReportsVO;
    }
    //private methods for Report calculation
    private ChargeDetailsVO getChargeDetails(ChargeVO chargeVO,TransactionSummaryVO transSummaryVO,TerminalVO terminalVO,String sTableName) throws PZDBViolationException
    {
        ChargeDetailsVO chargeDetailsVO=null;
        double vDblAmount=0.0;
        long vCntCounter=0;
        if(Charge_category.Success.name().equals(chargeVO.getCategory()))
        {

            if(Charge_keyword.TotalReserveGenerated.name().equals(chargeVO.getKeyword()))
            {
                chargeDetailsVO = new ReserveGeneratedVO();
                vCntCounter = transSummaryVO.getCountOfReserveGenerated();
                vDblAmount=transSummaryVO.getReserveGeneratedAmount();
                calculateCharges(chargeVO,vCntCounter,vDblAmount,chargeDetailsVO);

            }
            else if(Charge_keyword.TotalReserveRefunded.name().equals(chargeVO.getKeyword()))
            {

                chargeDetailsVO = new ReserveRefundVO();
                // get Start Date and end Date for rolling reserve Release
                //RollingReserveDateVO rollingReserveDateVO=accountDAO.getReserveReleaseDate(terminalVO.getMemberId());
                RollingReserveDateVO rollingReserveDateVO=accountDAO.getReserveReleaseDate(terminalVO);
                logger.debug("rollimg reserve date=====s"+rollingReserveDateVO.getRollingReserveStartDate()+""+rollingReserveDateVO.getRollingReserveEndDate());
                ((ReserveRefundVO)chargeDetailsVO).setRollingReserveDateVO(rollingReserveDateVO);
                //getting  ReserveRefundAmount and ReserveRefundCount
                TransactionSummaryVO transSummaryForReserveRefundedVO=accountDAO.getReservedRefundedSummary(rollingReserveDateVO,terminalVO,sTableName);

                vCntCounter = transSummaryForReserveRefundedVO.getCountOfreserveRefund();
                vDblAmount=transSummaryForReserveRefundedVO.getReserveRefundAmount();

                calculateCharges(chargeVO,vCntCounter,vDblAmount, chargeDetailsVO);

            }
            else if( Charge_keyword.CalculatedReserveRefund.name().equals(chargeVO.getKeyword()) )
            {

                chargeDetailsVO = new CalculatedReserveRefundVO();
                chargeDetailsVO.setChargeName(chargeVO.getChargename());
                chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());
                chargeDetailsVO.setValueType(chargeVO.getValuetype());

            }
            else if((Charge_keyword.Total.name().equals(chargeVO.getKeyword())))
            {
                chargeDetailsVO = new ChargeDetailsVO();

                vCntCounter=transSummaryVO.getCountOfReserveGenerated();
                vDblAmount=transSummaryVO.getReserveGeneratedAmount();

                calculateCharges(chargeVO, vCntCounter, vDblAmount,chargeDetailsVO);

            }
            else if((Charge_keyword.Reversed.name().equals(chargeVO.getKeyword())))
            {
                chargeDetailsVO = new ChargeDetailsVO();
                vCntCounter=transSummaryVO.getCountOfReversed();
                vDblAmount=transSummaryVO.getReversedAmount();
                calculateCharges(chargeVO, vCntCounter, vDblAmount,chargeDetailsVO);
            }
            else if((Charge_keyword.Chargeback.name().equals(chargeVO.getKeyword())))
            {
                chargeDetailsVO = new ChargeDetailsVO();
                vCntCounter=transSummaryVO.getCountOfChargeback();
                vDblAmount=transSummaryVO.getChargebackAmount();
                calculateCharges(chargeVO, vCntCounter, vDblAmount,chargeDetailsVO);
            }


        }
        else  if(Charge_category.Failure.name().equals(chargeVO.getCategory()))
        {
            if((Charge_keyword.Total.name().equals(chargeVO.getKeyword())))
            {
                chargeDetailsVO = new ChargeDetailsVO();
                vCntCounter=transSummaryVO.getCountOfAuthfailed();
                vDblAmount=transSummaryVO.getAuthfailedAmount();
                calculateCharges(chargeVO,vCntCounter,vDblAmount,chargeDetailsVO);
            }
        }


        return chargeDetailsVO;
    }

    private ChargeDetailsVO getOtherCharges(ChargeVO chargeVO, TransactionSummaryVO transSummaryVO, TerminalVO terminalVO, InputDateVO inputDateVO, WireAmountVO wireAmountVO,String sTableName) throws PZDBViolationException
    {
        ChargeDetailsVO chargeDetailsVO= null;
        double vDblAmount=0.0;
        long vCntCounter=0;
        if(Charge_category.Others.name().equals(chargeVO.getCategory()))
        {
            if (Charge_keyword.Total.name().equals(chargeVO.getKeyword()))
            {
                chargeDetailsVO= new ChargeDetailsVO();

                vCntCounter=transSummaryVO.getCountOfAuthfailed()+transSummaryVO.getCountOfChargeback()+transSummaryVO.getCountOfReversed()+transSummaryVO.getCountOfSettled();
                vDblAmount=transSummaryVO.getAuthfailedAmount()+transSummaryVO.getChargebackAmount()+transSummaryVO.getReversedAmount()+transSummaryVO.getSettledAmount();

                calculateCharges(chargeVO,vCntCounter,vDblAmount,chargeDetailsVO);
            }
            else if (Charge_keyword.VerifyOrder.name().equals(chargeVO.getKeyword()))
            {
                chargeDetailsVO= new ChargeDetailsVO();

                TransactionSummaryVO transactionVerifyOrderVO =accountDAO.getCountOfVerifyOrder(inputDateVO,terminalVO);
                vCntCounter=transactionVerifyOrderVO.getCountOfVerifiedOrder();

                calculateCharges(chargeVO,vCntCounter,vDblAmount,chargeDetailsVO);
            }
            else if (Charge_keyword.RefundAlert.name().equals(chargeVO.getKeyword()))
            {
                chargeDetailsVO= new ChargeDetailsVO();

                TransactionSummaryVO transactionRefundAlertVO=accountDAO.getCountOfRefundAlert(inputDateVO,terminalVO);
                vCntCounter=transactionRefundAlertVO.getCountOfRefundAlert();

                calculateCharges(chargeVO,vCntCounter,vDblAmount,chargeDetailsVO);
            }
            else if (Charge_keyword.RetrivalRequest.name().equals(chargeVO.getKeyword()))
            {
                chargeDetailsVO= new ChargeDetailsVO();

                TransactionSummaryVO transactionRetrievalRequestVO=accountDAO.getRetrievalRequestInfo(terminalVO, sTableName);
                vCntCounter=transactionRetrievalRequestVO==null?0:transactionRetrievalRequestVO.getCountOfRetrievalRequest();

                calculateCharges(chargeVO,vCntCounter,vDblAmount,chargeDetailsVO);
            }
            else if(Charge_keyword.Wire.name().equals(chargeVO.getKeyword()))
            {
                chargeDetailsVO= new WireChargeVO();
                vCntCounter = wireAmountVO.getWireCount();
                calculateCharges(chargeVO, vCntCounter, vDblAmount, chargeDetailsVO);

                //no amount and count
            }
            else if(Charge_keyword.GrossBalanceAmount.name().equals(chargeVO.getKeyword()))
            {
                chargeDetailsVO= new GrossChargeVO();
                chargeDetailsVO.setChargeName(chargeVO.getChargename());
                chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());
                chargeDetailsVO.setValueType(chargeVO.getValuetype());
                // no amount and count
            }
            else if(Charge_keyword.Setup.name().equals(chargeVO.getKeyword()))
            {
                chargeDetailsVO= new ChargeDetailsVO();

                SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                long days= Functions.DATEDIFF(transSummaryVO.getMinOfAllStatusDate(),targetFormat.format(new Date()));

                long currentCounter=1;

                if(Charge_frequency.One_Time.name().equals(chargeVO.getFrequency()))
                {
                    vCntCounter=1;

                }
                else if(Charge_frequency.Yearly.name().equals(chargeVO.getFrequency()))
                {
                    vCntCounter=(days/365)+currentCounter;

                }
                else if(Charge_frequency.Monthly.name().equals(chargeVO.getFrequency()))
                {
                    vCntCounter=(days/30)+currentCounter;

                }
                else if(Charge_frequency.Weekly.name().equals(chargeVO.getFrequency()))
                {
                    vCntCounter=(days/7)+currentCounter;

                }
                calculateCharges(chargeVO,vCntCounter,vDblAmount,chargeDetailsVO);


            }
        }

        return chargeDetailsVO;
    }

    private void calculateCharges(ChargeVO chargeVO,long vCntCounter,double vDblAmount,ChargeDetailsVO chargeDetailsVO)
    {
        chargeDetailsVO.setChargeName(chargeVO.getChargename());
        chargeDetailsVO.setCount(vCntCounter);
        chargeDetailsVO.setAmount(Double.valueOf(accountUtil.convert2Decimal(String.valueOf(vDblAmount))));
        chargeDetailsVO.setValueType(chargeVO.getValuetype());
        double vDblTotal=0.0;
        if(Charge_unit.Percentage.name().equals(chargeVO.getValuetype()) && Charge_subKeyword.Amount.name().equals(chargeVO.getSubkeyword()))
        { //percentage with Amount as Sub keyword

            chargeDetailsVO.setChargeValue(chargeVO.getChargevalue() + " %");
            vDblTotal= (vDblAmount*Double.valueOf(chargeVO.getChargevalue()))/100;

        }
        else  if(Charge_unit.FlatRate.name().equals(chargeVO.getValuetype()) &&  Charge_subKeyword.Count.name().equals(chargeVO.getSubkeyword()))
        {//Flat Rate with  Count as Sub Keyword

            chargeDetailsVO.setChargeValue(chargeVO.getChargevalue());
            vDblTotal=vCntCounter*Double.valueOf(chargeVO.getChargevalue());
        }
        chargeDetailsVO.setTotal(Double.valueOf(accountUtil.convert2Decimal(String.valueOf(vDblTotal))));

    }

    private double calculatedReserveRefund(AccountVO accountVO,double totalOfReserveRefund)
    {
        CalculatedReserveRefundVO calculatedReserveRefund = accountVO.getCalculatedReserveRefund();

        if (calculatedReserveRefund!=null)
        {
            double vDblCalculatedReserveRefundChargeAmount = 0.0;
            double vDblRefundedRollingReserveTotal = 0.0;

            //adding details into calculatedReserveRefund
            calculatedReserveRefund.setCount(0);
            calculatedReserveRefund.setAmount(Double.valueOf(accountUtil.convert2Decimal(String.valueOf(totalOfReserveRefund))));
            vDblCalculatedReserveRefundChargeAmount = Double.valueOf(calculatedReserveRefund.getChargeValue()) * totalOfReserveRefund / 100;
            calculatedReserveRefund.setChargeValue(calculatedReserveRefund.getChargeValue() + " %");
            calculatedReserveRefund.setTotal(Double.valueOf(accountUtil.convert2Decimal(String.valueOf(vDblCalculatedReserveRefundChargeAmount))));

            accountVO.setCalculatedReserveRefund(calculatedReserveRefund);

            vDblRefundedRollingReserveTotal = Double.valueOf(accountUtil.convert2Decimal(String.valueOf(totalOfReserveRefund - vDblCalculatedReserveRefundChargeAmount)));
            return vDblRefundedRollingReserveTotal;
        }


        return 0.0;
    }


    private double getFinalGrossBalanceAmount(AccountVO accountVO,double vDblTotalBalanceAmount)
    {

        double vDblGrossAmount=vDblTotalBalanceAmount;
        double vDblGrossChargesAmount=0.0;
        double vDblGrossChargesTotal=0.0;
        GrossChargeVO grossCharges=accountVO.getGrossTypeCharges();

        if(grossCharges!=null)
        {
            //adding details to GrossCharges
            grossCharges.setCount(0);

            vDblGrossChargesAmount=(vDblTotalBalanceAmount*Double.valueOf(grossCharges.getChargeValue())/100);
            if(vDblGrossChargesAmount<0)
            {
                vDblGrossChargesAmount=0.00;
            }
            vDblGrossChargesTotal=vDblGrossChargesTotal+vDblGrossChargesAmount;
            grossCharges.setChargeValue(grossCharges.getChargeValue() + "%");
            grossCharges.setAmount(Double.valueOf(accountUtil.convert2Decimal(new Double(vDblGrossAmount).toString())));
            grossCharges.setTotal(Double.valueOf(accountUtil.convert2Decimal(new Double(vDblGrossChargesAmount).toString())));
            accountVO.setGrossTypeCharges(grossCharges);
        }
        return vDblGrossChargesTotal;
    }

    public  RollingReserveDateVO getRollingReserveReleaseDate(String memberId,String accountId) throws PZDBViolationException//sandip
    {
        String reserveReleasedStartDate=accountDAO.getReserveReleaseStartDate(memberId);
        if(reserveReleasedStartDate==null)
        {
            reserveReleasedStartDate=accountDAO.getMemberFirstTransactionDate(memberId,accountId);
        }
        String reserveReleasedEndDate=accountDAO.getReserveReleaseEndDate(accountId);
        RollingReserveDateVO rollingReserveDateVO=accountDAO.getReserveReleaseDate(memberId);
        rollingReserveDateVO.setRollingReserveStartDate(reserveReleasedStartDate);
        rollingReserveDateVO.setRollingReserveEndDate(reserveReleasedEndDate);
        return rollingReserveDateVO;
    }
    public SetupChargeVO applySetupCharge(ChargeVO chargeVO, TerminalVO terminalVO) //sandip
    {
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
        SetupChargeVO  setupChargeVO=new SetupChargeVO();

        String latestSetupFeeDate=null;
        String strCurrentDate=null;

        Date currentDate=null;
        Date updatedSetupFeeDate=null;

        long setupCounter=0;
        long days= 0;

        currentDate=new Date();

        setupChargeVO.setChargeName(chargeVO.getChargename());
        setupChargeVO.setChargeValue(chargeVO.getChargevalue());
        setupChargeVO.setValueType(chargeVO.getValuetype());

        latestSetupFeeDate=accountDAO.getLastSetupFeeDate(terminalVO.getMemberId(),terminalVO.getTerminalId());

        if(latestSetupFeeDate==null)
        {
            latestSetupFeeDate=accountDAO.getMemberFirstTransactionDate(terminalVO.getMemberId(),terminalVO.getAccountId());
        }
        try
        {
            days = Functions.DATEDIFF(targetFormat.format(targetFormat.parse(latestSetupFeeDate)), targetFormat.format(new Date()));
            strCurrentDate=targetFormat.format(currentDate);
            updatedSetupFeeDate = targetFormat.parse(latestSetupFeeDate);
        }
        catch (ParseException e)
        {
            logger.error(e);
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(updatedSetupFeeDate);
        //Make The DB Call To Get last setup added date by passing memberId
        if(chargeVO.getFrequency().equals(Charge_frequency.One_Time.toString()))
        {
            if(latestSetupFeeDate==null)
            {
                setupCounter=1;
                setupChargeVO.setLastChargeDate(strCurrentDate);
            }
            else
            {
                setupChargeVO.setLastChargeDate(latestSetupFeeDate);
            }

        }
        else if(chargeVO.getFrequency().equals(Charge_frequency.Weekly.toString()))
        {
            if(days>7)
            {
                setupCounter=(days/7);
                cal.add(Calendar.DATE,(int)setupCounter*7); // add 10 days
                updatedSetupFeeDate = cal.getTime();
                setupChargeVO.setLastChargeDate(targetFormat.format(updatedSetupFeeDate));

            }
            else
            {
                setupChargeVO.setLastChargeDate(latestSetupFeeDate);
            }

        }
        else if(chargeVO.getFrequency().equals(Charge_frequency.Monthly.toString()))
        {
            if(days>30)
            {
                setupCounter=(days/30);
                cal.add(Calendar.DATE,(int)setupCounter*30); // add 10 days
                updatedSetupFeeDate = cal.getTime();
                setupChargeVO.setLastChargeDate(targetFormat.format(updatedSetupFeeDate));
            }
            else
            {
                setupChargeVO.setLastChargeDate(latestSetupFeeDate);
            }

        }
        else if(chargeVO.getFrequency().equals(Charge_frequency.Yearly.toString()))
        {
            if(days>365)
            {
                setupCounter=(days/365);
                cal.add(Calendar.DATE,(int)setupCounter*365); // add 10 days
                updatedSetupFeeDate = cal.getTime();
                setupChargeVO.setLastChargeDate(targetFormat.format(updatedSetupFeeDate));
            }
            else
            {
                setupChargeVO.setLastChargeDate(latestSetupFeeDate);
            }

        }
        setupChargeVO.setCount(setupCounter);
        setupChargeVO.setAmount(0.00);
        setupChargeVO.setTotal(setupCounter*Double.valueOf(chargeVO.getChargevalue()));

        return setupChargeVO;
    }

    //getting List of Charges from `member_accounts_charges_mapping` & Charge master
    public List<ChargeVO> getChargesAsPerTerminal(TerminalVO terminalVO,PaginationVO paginationVO) throws PZDBViolationException
    {
        List<ChargeVO> chargeVOList= accountDAO.getChargesAsPerTerminal(terminalVO);
        paginationVO.setTotalRecords(chargeVOList.size());
        //this is for pagination
        List<ChargeVO> chargeVOs = new ArrayList<ChargeVO>();

        if (paginationVO.getPageNo() != 0)
        {
            for (int i = paginationVO.getStart(); i <= (paginationVO.getEnd() * paginationVO.getPageNo()); i++)
            {
                if (chargeVOList.size() > i)
                {
                    ChargeVO chargeVO = chargeVOList.get(i);
                    chargeVO.setChargevalue(chargeVO.getChargevalue().concat(Charge_unit.Percentage.name().equals(chargeVO.getValuetype()) ? "%" : ""));
                    chargeVO.setFrequency(chargeVO.getFrequency().replaceAll("_", " "));
                    chargeVOs.add(chargeVO);
                }
            }
        }
        return chargeVOs;
    }

    public List<ChargeVO> getChargesAsPerTerminal(TerminalVO terminalVO) throws PZDBViolationException
    {
        return accountDAO.getChargesAsPerTerminal(terminalVO);
    }

    //getting list of wireReports for Agent
    public List<AgentWireVO> getAgentWireReports(InputDateVO inputDateVO,TerminalVO terminalVO,PaginationVO paginationVO,String isPaid) throws PZDBViolationException
    {
        return accountDAO.getListOfAgentWireReports(inputDateVO,terminalVO,paginationVO,isPaid);
    }
    public List<AgentWireVO> getListOfAgentWireReportByTerminal(InputDateVO inputDateVO,String agentid, String terminalId,PaginationVO paginationVO,String isPaid) throws PZDBViolationException
    {
        return accountDAO.getListOfAgentWireReportByTerminal(inputDateVO, agentid, terminalId, paginationVO, isPaid);
    }
}
