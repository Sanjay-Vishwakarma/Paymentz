import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.manager.AccountManager;
import com.manager.ChargeManager;
import com.manager.PayoutManager;
import com.manager.TerminalManager;
import com.manager.dao.MerchantDAO;
import com.manager.dao.PayoutDAO;
import com.manager.enums.Charge_category;
import com.manager.enums.Charge_keyword;
import com.manager.enums.Charge_subKeyword;
import com.manager.utils.AccountUtil;
import com.manager.vo.*;
import com.manager.vo.payoutVOs.*;

import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;

import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: Sandip
 * Date: 6/20/14
 * Time: 7:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class GeneratePayoutServlet extends HttpServlet
{
    private static Logger logger = new Logger(GeneratePayoutServlet.class.getName());
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        HttpSession session = request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        MerchantPayoutReportVO merchantPayoutReportVO=new MerchantPayoutReportVO();
        String reportType=request.getParameter("reporttype");
        try
        {
            String sMemberId="";
            String sAccountId = "";
            if(!request.getParameter("toid").equals("0"))
            {
                sMemberId= request.getParameter("toid");
                request.setAttribute("toid",sMemberId);
            }
            if(!request.getParameter("accountid").equals("0"))
            {
                sAccountId= request.getParameter("accountid");
                request.setAttribute("accountid",sMemberId);
            }

            String sPayModeId=request.getParameter("paymode");
            String sCardTypeID=request.getParameter("cardtype");
            String gateway = request.getParameter("pgtypeid");

            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
            Hashtable<String, String> requestDetails=new Hashtable<String, String>();
            requestDetails.put("toid",sMemberId);
            requestDetails.put("ACCOUNTID",sAccountId);
            requestDetails.put("paymode",sPayModeId);
            requestDetails.put("cardtype",sCardTypeID);
            List<String> error=validateMandatoryParameterForPayoutReport(requestDetails);
            if(error.size()>0)
            {
                request.setAttribute("errorList",error);
                RequestDispatcher rd = request.getRequestDispatcher("/listMerchantPayoutReport.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request,response);
                logger.debug("forward by validation method.....");
            }
            else
            {
                long vCntSettled = 0;
                long vCntReversed = 0;
                long vCntChargeback = 0;
                long vCntAuthfailed = 0;
                long vCntWireCount=0;
                long vCntTotal = 0;
                long varifyOrderCount=0;
                long refundAlertCount=0;
                long retrivalRequestCount=0;

                double vDblAuthfailedAmount=0.0;
                double vDblSettledAmount=0.0;
                double vDblReversedAmount = 0.0;
                double vDblChargebackAmount = 0.0;
                double vDblTotalAmount = 0.0;

                double vDblChargeFeeTotal=0.0;
                double vDblGeneratedRollingReserveTotal=0.0;
                double vDblRefundedRollingReserveTotal=0.0;
                double vDblTotalBalanceAmount=0.0;
                double vDblWirePaidAmount=0.0;
                double vDblWireUnPaidAmount=0.0;
                double vDblWireBalanceAmount=0.0;
                double cDblTotalFundedToBank=0.0;
                double vDblWireFeeTotal=0.0;
                double vDblGrossChargesTotal=0.0;

                String sTableName=null;
                String sCurrency=null;
                String sMemberTerminalId=null;
                Functions functions=new Functions();
                RollingReserveDateVO rollingReserveDateVO=null;
                SettlementDateVO  settlementDateVO=null;
                WireChargeVO wireChargeVO=null;

                AccountUtil accountUtil=new AccountUtil();
                PayoutManager payoutManager=new PayoutManager();
                MerchantDAO merchantDAO=new MerchantDAO();
                //PayoutDAO  payoutDAO=new PayoutDAO();
                TerminalManager terminalManager=new TerminalManager();
                AccountManager accountManager=new AccountManager();

                sTableName=accountUtil.getTableNameFromAccountId(sAccountId);
                MerchantDetailsVO merchantDetailsVO=merchantDAO.getMemberDetails(sMemberId);

                /*if(!gateway.equals("0"))
                {
                    String currency[] = gateway.split("-");
                    sCurrency = currency[1];
                }
                else
                {
                    GatewayAccount account= GatewayAccountService.getGatewayAccount(sAccountId);
                    GatewayType gatewayType = GatewayTypeService.getGatewayType(account.getPgTypeId());
                    sCurrency=gatewayType.getCurrency();
                }*/


                sMemberTerminalId=terminalManager.getMemberTerminalId(sMemberId,sAccountId,sPayModeId,sCardTypeID);
                TerminalVO terminalVO=terminalManager.getTerminalByTerminalId(sMemberTerminalId);

                List<ChargeVO> chargeVOs=accountManager.getChargesAsPerTerminal(terminalVO);
                if(chargeVOs.size()>0)
                {
                    //Getting First And Last Transaction Date
                    String memberFirstTransDate=payoutManager.getMemberFirstTransactionDateOnAccountId(sMemberId, sAccountId);
                    if(memberFirstTransDate!=null)
                    {
                        rollingReserveDateVO=payoutManager.getRollingReserveReleaseDateOnAccount(sMemberId,sAccountId,reportType);
                        if(functions.isValueNull(rollingReserveDateVO.getRollingReserveEndDate()))
                        {
                            //covert date into the yyyy-mm-dd format
                            rollingReserveDateVO.setRollingReserveStartDate(targetFormat.format(targetFormat.parse(rollingReserveDateVO.getRollingReserveStartDate())));
                            rollingReserveDateVO.setRollingReserveEndDate(targetFormat.format(targetFormat.parse(rollingReserveDateVO.getRollingReserveEndDate())));

                            WireAmountVO wireAmountVO = new WireAmountVO();
                            payoutManager.setUnpaidBalanceAmount(terminalVO, wireAmountVO);
                            payoutManager.setWireAmount(terminalVO,wireAmountVO);


                            //vDblWireUnPaidAmount=wireAmountVO.getUnpaidAmount();
                            //vDblWireBalanceAmount=wireAmountVO.getWireBalanceAmount();

                            if("wirereport".equals(reportType))
                            {
                                vCntWireCount=1;
                                vDblWireBalanceAmount=wireAmountVO.getWireBalanceAmount();

                                settlementDateVO=payoutManager.getWireReportSettlementDateVO(terminalVO,sTableName);
                                SettlementDateVO settlementEndDateVO=payoutManager.getSettlementDateOnTerminal(terminalVO,sTableName);

                                settlementDateVO.setSettlementEndDate(settlementEndDateVO.getSettlementEndDate());
                                settlementDateVO.setDeclinedEndDate(settlementEndDateVO.getDeclinedEndDate());
                                settlementDateVO.setReversedEndDate(settlementEndDateVO.getReversedEndDate());
                                settlementDateVO.setChargebackEndDate(settlementEndDateVO.getChargebackEndDate());
                                merchantPayoutReportVO.setWireReport(true);
                            }
                            else
                            {
                                //vCntWireCount=payoutDAO.getWireCount(terminalVO)+1;
                                vCntWireCount=payoutManager.getWireCount(terminalVO)+1;
                                vDblWireBalanceAmount=wireAmountVO.getUnpaidBalanceAmount()+wireAmountVO.getUnpaidAmount();
                                vDblWirePaidAmount=wireAmountVO.getPaidAmount();
                                settlementDateVO=payoutManager.getSettlementDateOnTerminal(terminalVO,sTableName);
                            }

                            TransactionSummaryVO summaryVO=null;
                            summaryVO=payoutManager.getAuthFailCountAmountByDtstamp(settlementDateVO.getDeclinedStartDate(), terminalVO, sTableName);
                            vCntAuthfailed=summaryVO.getCountOfAuthfailed();
                            vDblAuthfailedAmount=summaryVO.getAuthfailedAmount();

                            //Getting Settled Count & Settled Amount
                            //summaryVO=payoutDAO.getSettledCountAmountByDtstamp(settlementDateVO.getSettlementStartDate(),terminalVO,sTableName);
                            summaryVO=payoutManager.getSettledCountAmountByDtstamp(settlementDateVO.getSettlementStartDate(),terminalVO,sTableName);
                            vCntSettled=summaryVO.getCountOfSettled();
                            vDblSettledAmount=summaryVO.getSettledAmount();

                            //Getting Reversed Count & Reversed Amount
                            //summaryVO=payoutDAO.getReversalCountAmountByTimestamp(settlementDateVO.getReversedStartDate(),terminalVO,sTableName);
                            summaryVO=payoutManager.getReversalCountAmountByTimestamp(settlementDateVO.getReversedStartDate(),terminalVO,sTableName);
                            vCntReversed=summaryVO.getCountOfReversed();
                            vDblReversedAmount=summaryVO.getReversedAmount();

                            //Getting Chargeback Count & Chargeback Amount
                            summaryVO=payoutManager.getChargebackCountAmountByTimestamp(settlementDateVO.getChargebackStartDate(),terminalVO,sTableName);
                            vCntChargeback=summaryVO.getCountOfChargeback();
                            vDblChargebackAmount=summaryVO.getChargebackAmount();

                            //Total Count (includes authfailed; settled; reversed; chargeback) & Total Amount includes (settled; reversed; chargeback)
                            vDblTotalAmount = vDblSettledAmount + vDblReversedAmount + vDblChargebackAmount;
                            vCntTotal = vCntAuthfailed + vCntSettled + vCntReversed + vCntChargeback;

                            TransactionSummaryVO summaryVOFinal=new TransactionSummaryVO();
                            summaryVOFinal.setCountOfAuthfailed(vCntAuthfailed);
                            summaryVOFinal.setAuthfailedAmount(vDblAuthfailedAmount);

                            summaryVOFinal.setCountOfSettled(vCntSettled);
                            summaryVOFinal.setSettledAmount(vDblSettledAmount);

                            summaryVOFinal.setCountOfReversed(vCntReversed);
                            summaryVOFinal.setReversedAmount(vDblReversedAmount);

                            summaryVOFinal.setCountOfChargeback(vCntChargeback);
                            summaryVOFinal.setChargebackAmount(vDblChargebackAmount);

                            List<ChargeDetailsVO> detailsVOList=new ArrayList<ChargeDetailsVO>();
                            List<ReserveGeneratedVO> reserveGeneratedVOList=new ArrayList<ReserveGeneratedVO>();
                            List<GrossChargeVO> grossChargeVOsList=new ArrayList<GrossChargeVO>();

                            List<ReserveRefundVO> reserveRefundVOList=new ArrayList<ReserveRefundVO>();
                            List<CalculatedReserveRefundVO> calculatedReserveRefundVOList=new ArrayList<CalculatedReserveRefundVO>();

                            GrossChargeVO grossChargeVO=null;
                            ReserveGeneratedVO reserveGeneratedVO=null;
                            ReserveRefundVO reserveRefundVO=null;
                            CalculatedReserveRefundVO calculatedReserveRefundVO=null;

                            String currentDate=Functions.convertDateDBFormat(Calendar.getInstance().getTime());
                            ChargeManager chargeManager=new ChargeManager();

                            ChargeDetailsVO chargeDetailsVO=null;
                            for(ChargeVO chargeVO:chargeVOs)
                            {
                                long vCntCounter=0;
                                double vDblAmount=0.0;
                                double vDblTotal=0.0;
                                logger.debug("ChargeName======="+chargeVO.getChargename());
                                String chargeVersionRate=chargeManager.getMerchantChargeVersionRate(chargeVO.getMappingid(),currentDate);
                                if(chargeVersionRate!=null)
                                {
                                    chargeVO.setChargevalue(chargeVersionRate);
                                }
                                if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.TotalReserveGenerated.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
                                {
                                    vCntCounter=vCntSettled+vCntReversed+vCntChargeback;
                                    vDblAmount=vDblSettledAmount+vDblReversedAmount+vDblChargebackAmount;
                                    vDblTotal=(vDblAmount*Double.valueOf(chargeVO.getChargevalue()))/100;
                                    vDblGeneratedRollingReserveTotal=vDblGeneratedRollingReserveTotal+vDblTotal;

                                    reserveGeneratedVO=new ReserveGeneratedVO();
                                    reserveGeneratedVO.setChargeName(chargeVO.getChargename());
                                    reserveGeneratedVO.setChargeValue(chargeVO.getChargevalue() + "%");
                                    reserveGeneratedVO.setCount(vCntCounter);
                                    reserveGeneratedVO.setAmount(vDblAmount);
                                    reserveGeneratedVO.setTotal(Functions.roundDBL(vDblTotal,2));

                                    reserveGeneratedVOList.add(reserveGeneratedVO);
                                    continue;
                                }
                                else if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.TotalReserveRefunded.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
                                {
                                    //TransactionSummaryVO releaseAmountCount=payoutDAO.getReleaseCountAndAmount(rollingReserveDateVO,terminalVO,sTableName);
                                    TransactionSummaryVO releaseAmountCount=payoutManager.getReleaseCountAndAmount(rollingReserveDateVO,terminalVO,sTableName);

                                    vCntCounter=releaseAmountCount.getCountOfreserveRefund();
                                    vDblAmount=releaseAmountCount.getReserveRefundAmount();
                                    vDblTotal=(vDblAmount*Double.valueOf(chargeVO.getChargevalue()))/100;
                                    vDblRefundedRollingReserveTotal=vDblRefundedRollingReserveTotal+vDblTotal;

                                    reserveRefundVO=new ReserveRefundVO();
                                    reserveRefundVO.setChargeName(chargeVO.getChargename());
                                    reserveRefundVO.setChargeValue(chargeVO.getChargevalue() + "%");
                                    reserveRefundVO.setCount(vCntCounter);
                                    reserveRefundVO.setAmount(vDblAmount);
                                    reserveRefundVO.setTotal(Functions.roundDBL(vDblTotal,2));

                                    reserveRefundVOList.add(reserveRefundVO);
                                    continue;
                                }
                                else if(Charge_category.Success.toString().equals(chargeVO.getCategory()) && Charge_keyword.CalculatedReserveRefund.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
                                {
                                    calculatedReserveRefundVO=new CalculatedReserveRefundVO();
                                    calculatedReserveRefundVO.setChargeName(chargeVO.getChargename());
                                    calculatedReserveRefundVO.setChargeValue(chargeVO.getChargevalue());
                                    calculatedReserveRefundVO.setValueType(chargeVO.getValuetype());

                                    calculatedReserveRefundVOList.add(calculatedReserveRefundVO);
                                    continue;
                                }
                                else if(Charge_category.Others.toString().equals(chargeVO.getCategory()) && Charge_keyword.Wire.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Count.toString().equals(chargeVO.getSubkeyword()))
                                {
                                    wireChargeVO=new WireChargeVO();
                                    wireChargeVO=new WireChargeVO();
                                    wireChargeVO.setChargeValue(chargeVO.getChargevalue());
                                    wireChargeVO.setChargeName(chargeVO.getChargename());
                                    continue;
                                }
                                else if(Charge_category.Others.toString().equals(chargeVO.getCategory()) && Charge_keyword.GrossBalanceAmount.toString().equals(chargeVO.getKeyword()) && Charge_subKeyword.Amount.toString().equals(chargeVO.getSubkeyword()))
                                {
                                    grossChargeVO=new GrossChargeVO();
                                    grossChargeVO.setChargeName(chargeVO.getChargename());
                                    grossChargeVO.setChargeValue(chargeVO.getChargevalue());
                                    grossChargeVO.setValueType(chargeVO.getValuetype());
                                    grossChargeVOsList.add(grossChargeVO);
                                    continue;
                                }

                                else
                                {
                                    chargeDetailsVO=payoutManager.calculateMerchantChargeValue(chargeVO, terminalVO, summaryVOFinal, sTableName, reportType);
                                    if(chargeDetailsVO!=null)
                                    {
                                        vDblChargeFeeTotal=vDblChargeFeeTotal+chargeDetailsVO.getTotal();
                                        logger.debug("=,"+chargeDetailsVO.getTotal());
                                        detailsVOList.add(chargeDetailsVO);

                                        if(chargeVO.getKeyword().equals(Charge_keyword.VerifyOrder.toString()))
                                        {
                                            varifyOrderCount=chargeDetailsVO.getCount();
                                        }
                                        if(chargeVO.getKeyword().equals(Charge_keyword.RefundAlert.toString()))
                                        {
                                            refundAlertCount=chargeDetailsVO.getCount();
                                        }
                                        if(chargeVO.getKeyword().equals(Charge_keyword.RetrivalRequest.toString()))
                                        {
                                            retrivalRequestCount=chargeDetailsVO.getCount();
                                        }
                                    }

                                }
                            }


                            if(calculatedReserveRefundVOList.size()>0)
                            {
                                double vDblCalculatedReserveRefundChargeAmount=0.0;
                                for (CalculatedReserveRefundVO calculatedReserveRefundVO2:calculatedReserveRefundVOList)
                                {
                                    reserveRefundVO=new ReserveRefundVO();
                                    vDblCalculatedReserveRefundChargeAmount=(Double.valueOf(calculatedReserveRefundVO2.getChargeValue())*vDblRefundedRollingReserveTotal)/100;

                                    reserveRefundVO.setChargeName(calculatedReserveRefundVO2.getChargeName());
                                    reserveRefundVO.setChargeValue(calculatedReserveRefundVO2.getChargeValue()+"%");
                                    reserveRefundVO.setCount(0);
                                    reserveRefundVO.setAmount(Functions.roundDBL(vDblRefundedRollingReserveTotal,2));
                                    reserveRefundVO.setTotal(Functions.roundDBL(vDblCalculatedReserveRefundChargeAmount,2));

                                    reserveRefundVOList.add(reserveRefundVO);

                                    vDblRefundedRollingReserveTotal=vDblRefundedRollingReserveTotal-Functions.roundDBL(vDblCalculatedReserveRefundChargeAmount,2);

                                }
                            }

                            double vDblDeduct=-1*(vDblChargeFeeTotal+vDblChargebackAmount+vDblReversedAmount+vDblGeneratedRollingReserveTotal);
                            vDblTotalBalanceAmount=vDblTotalAmount+vDblDeduct;

                            List<GrossChargeVO> grossChargeVOsFinal=new ArrayList<GrossChargeVO>();
                            GrossChargeVO grossChargeVOFinal=null;
                            if(grossChargeVOsList.size()>0)
                            {
                                long  vCntCount=0;
                                double vDblGrossAmount=vDblTotalBalanceAmount;
                                double vDblGrossChargesAmount=0.0;

                                for(GrossChargeVO grossChargeVO1:grossChargeVOsList)
                                {
                                    vCntCount=0;
                                    if(vDblGrossChargesAmount>0)
                                    {
                                        vDblGrossChargesAmount=(vDblTotalBalanceAmount*Double.valueOf(grossChargeVO1.getChargeValue())/100);
                                        vDblGrossChargesTotal=vDblGrossChargesTotal+vDblGrossChargesAmount;
                                    }

                                    grossChargeVOFinal=new GrossChargeVO();
                                    grossChargeVOFinal.setChargeName(grossChargeVO1.getChargeName());
                                    grossChargeVOFinal.setChargeValue(grossChargeVO1.getChargeValue()+"%");
                                    grossChargeVOFinal.setCount(vCntCount);
                                    grossChargeVOFinal.setAmount(vDblGrossAmount);
                                    grossChargeVOFinal.setTotal(Functions.roundDBL(vDblGrossChargesAmount,2));

                                    grossChargeVOsFinal.add(grossChargeVOFinal);

                                }
                            }

                            if(wireChargeVO!=null)
                            {
                                vDblWireFeeTotal=vCntWireCount*Double.parseDouble(wireChargeVO.getChargeValue());

                                wireChargeVO.setCount(vCntWireCount);
                                wireChargeVO.setAmount(0.00);
                                wireChargeVO.setTotal(Functions.roundDBL(vDblWireFeeTotal,2));
                            }

                            cDblTotalFundedToBank=(vDblTotalBalanceAmount+vDblWireBalanceAmount+vDblRefundedRollingReserveTotal)-(vDblWirePaidAmount+vDblWireFeeTotal+(-1*vDblGrossChargesTotal));

                            merchantPayoutReportVO.setMerchantTotalProcessingAmount(vDblTotalAmount);
                            merchantPayoutReportVO.setMerchantTotalChargesAmount(vDblChargeFeeTotal);
                            merchantPayoutReportVO.setMerchantRollingReleasedAmount(vDblRefundedRollingReserveTotal);
                            merchantPayoutReportVO.setMerchantRollingReserveAmount(vDblGeneratedRollingReserveTotal);
                            merchantPayoutReportVO.setMerchantTotalBalanceAmount(vDblTotalBalanceAmount);

                            merchantPayoutReportVO.setMerchantTotalSettledAmount(vDblSettledAmount);
                            merchantPayoutReportVO.setMerchantTotalDeclinedAmount(vDblAuthfailedAmount);
                            merchantPayoutReportVO.setMerchantTotalReversedAmount(vDblReversedAmount);
                            merchantPayoutReportVO.setMerchantTotalChargebackAmount(vDblChargebackAmount);

                            merchantPayoutReportVO.setMerchantWirePaidAmount(vDblWirePaidAmount);
                            merchantPayoutReportVO.setMerchantWireUnpaidAmount(vDblWireBalanceAmount);
                            merchantPayoutReportVO.setMerchantTotalFundedAmount(cDblTotalFundedToBank);

                            merchantPayoutReportVO.setVerifyOrderCount(varifyOrderCount);
                            merchantPayoutReportVO.setRefundAlertCount(refundAlertCount);
                            merchantPayoutReportVO.setRetrivalRequestCount(retrivalRequestCount);


                            merchantPayoutReportVO.setCurrency(sCurrency);
                            merchantPayoutReportVO.setSetupFeeCoveredDateUpTo(Functions.convertDateDBFormat(Calendar.getInstance().getTime()));

                            merchantPayoutReportVO.setChargesDetailsVOsList(detailsVOList);
                            merchantPayoutReportVO.setSettlementDateVO(settlementDateVO);
                            merchantPayoutReportVO.setRollingReserveDateVO(rollingReserveDateVO);
                            merchantPayoutReportVO.setWireChargeVO(wireChargeVO);
                            merchantPayoutReportVO.setMerchantDetailsVO(merchantDetailsVO);
                            merchantPayoutReportVO.setTerminalVO(terminalVO);
                            merchantPayoutReportVO.setGrossTypeChargeVOList(grossChargeVOsFinal);
                            merchantPayoutReportVO.setReserveRefundVOsList(reserveRefundVOList);
                            merchantPayoutReportVO.setReserveGeneratedVOList(reserveGeneratedVOList);
                            request.setAttribute("merchantPayoutReportVO",merchantPayoutReportVO);

                            RequestDispatcher rd = request.getRequestDispatcher("/listMerchantPayoutReport.jsp?ctoken="+user.getCSRFToken());
                            rd.forward(request,response);

                        }
                        else
                        {
                            logger.debug("No Rolling Release Date Found For Member "+sMemberId +" On Account Id=="+sAccountId);
                            error.add("No Rolling Release Date Found For Member "+sMemberId +" On Account Id=="+sAccountId);
                            request.setAttribute("errorList",error);
                            RequestDispatcher rd = request.getRequestDispatcher("/listMerchantPayoutReport.jsp?ctoken="+user.getCSRFToken());
                            rd.forward(request,response);
                        }

                    }
                    else
                    {
                        logger.debug("No Transactions Founds For Member "+sMemberId +" On Account Id=="+sAccountId);
                        error.add("No Transactions Founds For Member "+sMemberId +" On Account Id=="+sAccountId);
                        request.setAttribute("errorList",error);
                        RequestDispatcher rd = request.getRequestDispatcher("/listMerchantPayoutReport.jsp?ctoken="+user.getCSRFToken());
                        rd.forward(request,response);

                    }
                }
                else
                {
                    logger.debug("No Charges Mapped  For member "+sMemberId +" On Account Id=="+sAccountId);
                    error.add("No Charges Mapped  For member "+sMemberId +" On Account Id=="+sAccountId);
                    request.setAttribute("errorList",error);
                    RequestDispatcher rd = request.getRequestDispatcher("/listMerchantPayoutReport.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(request,response);
                }

            }

        }
        catch (Exception e)
        {
            logger.error("exception",e);
            request.setAttribute("errorList",new ArrayList().add(e.getMessage()));
            RequestDispatcher rd = request.getRequestDispatcher("/listMerchantPayoutReport.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request,response);
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request,response);
    }
    public List<String> validateMandatoryParameterForPayoutReport(Hashtable<String, String> otherDetail)
    {
        logger.debug("Enter Into validateMandatoryParameter() method");

        InputValidator inputValidator = new InputValidator();

        List<String> error = new ArrayList<String>();

        Hashtable<InputFields, String> hashTable =new Hashtable<InputFields, String>();

        hashTable.put(InputFields.TOID, (String) otherDetail.get("toid"));

        hashTable.put(InputFields.ACCOUNTID_CAPS,(String) otherDetail.get("ACCOUNTID"));

        hashTable.put(InputFields.CARDTYPE,(String) otherDetail.get("cardtype"));

        hashTable.put(InputFields.PAYMODE, (String) otherDetail.get("paymode"));

        /*hashTable.put(InputFields.DATE_SMALL, (String) otherDetail.get("strRollingReserveDate"));*/

        ValidationErrorList errorList = new ValidationErrorList();

        inputValidator.InputValidations(hashTable,errorList,false);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :hashTable.keySet())
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error.add(errorList.getError(inputFields.toString()).getMessage());
                }
            }

        }
        logger.debug("Leaving validateMandatoryParameter() method");
        return error;

    }







}
