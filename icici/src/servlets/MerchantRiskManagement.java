import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.MerchantMonitoringManager;
import com.manager.TerminalManager;
import com.manager.dao.MerchantDAO;
import com.manager.enums.PZTransactionCurrency;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TerminalVO;
import com.manager.vo.TransactionSummaryVO;
import com.manager.vo.merchantmonitoring.CardTypeAmountVO;
import com.manager.vo.merchantmonitoring.MerchantRiskParameterVO;
import com.manager.vo.merchantmonitoring.TerminalProcessingDetailsVO;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 3/9/16
 * Time: 10:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchantRiskManagement extends HttpServlet
{
    private static Logger logger = new Logger(MerchantRiskManagement.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        String errormsg = "";
        RequestDispatcher rd = req.getRequestDispatcher("/merchantriskmanagement.jsp?ctoken=" + user.getCSRFToken());

        errormsg = errormsg + validateParameters(req);
        Functions functions=new Functions();

        if(functions.isValueNull(errormsg))
        {
            req.setAttribute("error",errormsg);
            rd.forward(req,res);
            return;
        }

        String memberId = req.getParameter("memberid");
        HashMap<String,TerminalProcessingDetailsVO> processingDetailsVOHashMap=new HashMap();
        HashMap<String,CardTypeAmountVO> hashMap=new HashMap();
        HashMap<String,CardTypeAmountVO> hashMap1=new HashMap();
        MerchantDAO merchantDAO=new MerchantDAO();
        MerchantDetailsVO merchantDetailsVO=null;
        try
        {
            merchantDetailsVO=merchantDAO.getMemberDetails(memberId);
            if(functions.isValueNull(merchantDetailsVO.getMemberId()))
            {
                TerminalManager terminalManager=new TerminalManager();
                MerchantMonitoringManager merchantMonitoringManager=new MerchantMonitoringManager();
                List<TerminalVO> terminalVOList=terminalManager.getTerminalsByMerchantId(memberId);
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                MerchantRiskParameterVO merchantRiskParameterVO=null;
                TransactionSummaryVO transactionSummaryVO=null;


                double MC_EURO_TOTAL_SALES_AMOUNT=0.00;
                double MC_USD_TOTAL_SALES_AMOUNT=0.00;
                double MC_GBP_TOTAL_SALES_AMOUNT=0.00;
                double MC_JPY_TOTAL_SALES_AMOUNT=0.00;
                double MC_PEN_TOTAL_SALES_AMOUNT=0.00;

                double VISA_EURO_TOTAL_SALES_AMOUNT=0.00;
                double VISA_USD_TOTAL_SALES_AMOUNT=0.00;
                double VISA_GBP_TOTAL_SALES_AMOUNT=0.00;
                double VISA_JPY_TOTAL_SALES_AMOUNT=0.00;
                double VISA_PEN_TOTAL_SALES_AMOUNT=0.00;

                double AMEX_EURO_TOTAL_SALES_AMOUNT=0.00;
                double AMEX_USD_TOTAL_SALES_AMOUNT=0.00;
                double AMEX_GBP_TOTAL_SALES_AMOUNT=0.00;
                double AMEX_JPY_TOTAL_SALES_AMOUNT=0.00;
                double AMEX_PEN_TOTAL_SALES_AMOUNT=0.00;

                double DINERS_EURO_TOTAL_SALES_AMOUNT=0.00;
                double DINERS_USD_TOTAL_SALES_AMOUNT=0.00;
                double DINERS_GBP_TOTAL_SALES_AMOUNT=0.00;
                double DINERS_JPY_TOTAL_SALES_AMOUNT=0.00;
                double DINERS_PEN_TOTAL_SALES_AMOUNT=0.00;

                double CUREENTMONTH_MC_EURO_TOTAL_SALES_AMOUNT=0.00;
                double CURRENTMONTH_MC_USD_TOTAL_SALES_AMOUNT=0.00;
                double CURRENTMONTH_MC_GBP_TOTAL_SALES_AMOUNT=0.00;
                double CURRENTMONTH_MC_JPY_TOTAL_SALES_AMOUNT=0.00;
                double CURRENTMONTH_MC_PEN_TOTAL_SALES_AMOUNT=0.00;

                double CUREENTMONTH_VISA_EURO_TOTAL_SALES_AMOUNT=0.00;
                double CURRENTMONTH_VISA_USD_TOTAL_SALES_AMOUNT=0.00;
                double CURRENTMONTH_VISA_GBP_TOTAL_SALES_AMOUNT=0.00;
                double CURRENTMONTH_VISA_JPY_TOTAL_SALES_AMOUNT=0.00;
                double CURRENTMONTH_VISA_PEN_TOTAL_SALES_AMOUNT=0.00;

                double CURRENTMONTH_AMEX_EURO_TOTAL_SALES_AMOUNT=0.00;
                double CURRENTMONTH_AMEX_USD_TOTAL_SALES_AMOUNT=0.00;
                double CURRENTMONTH_AMEX_GBP_TOTAL_SALES_AMOUNT=0.00;
                double CURRENTMONTH_AMEX_JPY_TOTAL_SALES_AMOUNT=0.00;
                double CURRENTMONTH_AMEX_PEN_TOTAL_SALES_AMOUNT=0.00;

                double CURRENTMONTH_DINERS_EURO_TOTAL_SALES_AMOUNT=0.00;
                double CURRENTMONTH_DINERS_USD_TOTAL_SALES_AMOUNT=0.00;
                double CURRENTMONTH_DINERS_GBP_TOTAL_SALES_AMOUNT=0.00;
                double CURRENTMONTH_DINERS_JPY_TOTAL_SALES_AMOUNT=0.00;
                double CURRENTMONTH_DINERS_PEN_TOTAL_SALES_AMOUNT=0.00;

                boolean isUSDAccount=false;
                boolean isEURAccount=false;
                boolean isGBPAccount=false;
                boolean isJPYAccount=false;
                boolean isPENAccount=false;

                if(terminalVOList.size()>0)
                {
                    for(TerminalVO terminalVO:terminalVOList)
                    {

                        String activationDate=terminalVO.getActivationDate();
                        if(functions.isValueNull(activationDate))
                        {
                            activationDate=simpleDateFormat.format(simpleDateFormat.parse(activationDate));
                        }
                        String firstTransactionDate=merchantMonitoringManager.getMemberFirstSubmission(terminalVO);
                        String lastTransactionDate=merchantMonitoringManager.getMemberLastSubmission(terminalVO);

                        long firstSubmissionInDays=Functions.DATEDIFF(activationDate,firstTransactionDate);
                        long lastSubmissionInDays=Functions.DATEDIFF(lastTransactionDate,simpleDateFormat.format(new Date()));

                        int firstSubmissionInMonth=(int)firstSubmissionInDays/30;
                        int lastSubmissionInMonth=(int)lastSubmissionInDays/30;

                        int firstSubmissionRemainderDays=(int)firstSubmissionInDays%30;
                        int lastSubmissionRemainderDays=(int)lastSubmissionInDays%30;

                        String firstSubmissionInMonthDaysFormat=firstSubmissionInMonth+":"+firstSubmissionRemainderDays;
                        String lastSubmissionInMonthDaysFormat=lastSubmissionInMonth+":"+lastSubmissionRemainderDays;

                        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(terminalVO.getAccountId());

                        String gateway=gatewayAccount.getGateway();
                        String currency=gatewayAccount.getCurrency();


                        merchantRiskParameterVO=new MerchantRiskParameterVO();

                        merchantRiskParameterVO.setMerchantFirstSubmissionInDays((int) firstSubmissionInDays);
                        merchantRiskParameterVO.setMerchantLastSubmissionInDays((int) lastSubmissionInDays);

                        merchantRiskParameterVO.setFirstSubmissionInMonthDaysFormat(firstSubmissionInMonthDaysFormat);
                        merchantRiskParameterVO.setLastSubmissionInMonthDaysFormat(lastSubmissionInMonthDaysFormat);

                        terminalVO.setActivationDate(activationDate);
                        TerminalProcessingDetailsVO processingDetailsVO=merchantMonitoringManager.getCurrentMonthProcessingDetails(terminalVO);

                        transactionSummaryVO = merchantMonitoringManager.getTotalSalesAmount(terminalVO);
                        double totalProcessingAmount=transactionSummaryVO.getTotalProcessingAmount();
                        double currentMonthTotalProcessingAmount=processingDetailsVO.getSalesAmount();

                        if(PZTransactionCurrency.USD.toString().equals(currency))
                        {
                            isUSDAccount=true;
                            if("1".equals(terminalVO.getCardTypeId()))
                            {
                                VISA_USD_TOTAL_SALES_AMOUNT=VISA_USD_TOTAL_SALES_AMOUNT+totalProcessingAmount;
                                CURRENTMONTH_VISA_USD_TOTAL_SALES_AMOUNT=CURRENTMONTH_VISA_USD_TOTAL_SALES_AMOUNT+currentMonthTotalProcessingAmount;

                            }
                            else if("2".equals(terminalVO.getCardTypeId()))
                            {
                                MC_USD_TOTAL_SALES_AMOUNT=MC_USD_TOTAL_SALES_AMOUNT+totalProcessingAmount;
                                CURRENTMONTH_MC_USD_TOTAL_SALES_AMOUNT=CURRENTMONTH_MC_USD_TOTAL_SALES_AMOUNT+currentMonthTotalProcessingAmount;
                            }
                            else if("3".equals(terminalVO.getCardTypeId()))
                            {
                                DINERS_USD_TOTAL_SALES_AMOUNT=DINERS_USD_TOTAL_SALES_AMOUNT+totalProcessingAmount;
                                CURRENTMONTH_DINERS_USD_TOTAL_SALES_AMOUNT=CURRENTMONTH_DINERS_USD_TOTAL_SALES_AMOUNT+currentMonthTotalProcessingAmount;
                            }
                            else if("4".equals(terminalVO.getCardTypeId()))
                            {
                                AMEX_USD_TOTAL_SALES_AMOUNT=AMEX_USD_TOTAL_SALES_AMOUNT+totalProcessingAmount;
                                CURRENTMONTH_AMEX_USD_TOTAL_SALES_AMOUNT=CURRENTMONTH_AMEX_USD_TOTAL_SALES_AMOUNT+currentMonthTotalProcessingAmount;
                            }

                        }
                        else if(PZTransactionCurrency.EUR.toString().equals(currency))
                        {
                            isEURAccount=true;
                            if("1".equals(terminalVO.getCardTypeId()))
                            {
                                VISA_EURO_TOTAL_SALES_AMOUNT=VISA_EURO_TOTAL_SALES_AMOUNT+totalProcessingAmount;
                                CUREENTMONTH_VISA_EURO_TOTAL_SALES_AMOUNT=CUREENTMONTH_VISA_EURO_TOTAL_SALES_AMOUNT+currentMonthTotalProcessingAmount;
                            }
                            else if("2".equals(terminalVO.getCardTypeId()))
                            {
                                MC_EURO_TOTAL_SALES_AMOUNT=MC_EURO_TOTAL_SALES_AMOUNT+totalProcessingAmount;
                                CURRENTMONTH_MC_USD_TOTAL_SALES_AMOUNT=CURRENTMONTH_MC_USD_TOTAL_SALES_AMOUNT+currentMonthTotalProcessingAmount;
                            }
                            else if("3".equals(terminalVO.getCardTypeId()))
                            {
                                DINERS_EURO_TOTAL_SALES_AMOUNT=DINERS_EURO_TOTAL_SALES_AMOUNT+totalProcessingAmount;
                                CURRENTMONTH_DINERS_EURO_TOTAL_SALES_AMOUNT=CURRENTMONTH_DINERS_EURO_TOTAL_SALES_AMOUNT+currentMonthTotalProcessingAmount;
                            }
                            else if("4".equals(terminalVO.getCardTypeId()))
                            {
                                AMEX_EURO_TOTAL_SALES_AMOUNT=AMEX_EURO_TOTAL_SALES_AMOUNT+totalProcessingAmount;
                                CURRENTMONTH_AMEX_EURO_TOTAL_SALES_AMOUNT=CURRENTMONTH_AMEX_EURO_TOTAL_SALES_AMOUNT+currentMonthTotalProcessingAmount;
                            }
                        }
                        else if(PZTransactionCurrency.GBP.toString().equals(currency))
                        {
                            isGBPAccount=true;
                            if("1".equals(terminalVO.getCardTypeId()))
                            {
                                VISA_GBP_TOTAL_SALES_AMOUNT=VISA_GBP_TOTAL_SALES_AMOUNT+totalProcessingAmount;
                                CURRENTMONTH_VISA_GBP_TOTAL_SALES_AMOUNT=CURRENTMONTH_VISA_GBP_TOTAL_SALES_AMOUNT+currentMonthTotalProcessingAmount;
                            }
                            else if("2".equals(terminalVO.getCardTypeId()))
                            {
                                MC_GBP_TOTAL_SALES_AMOUNT=MC_GBP_TOTAL_SALES_AMOUNT+totalProcessingAmount;
                                CURRENTMONTH_MC_GBP_TOTAL_SALES_AMOUNT=CURRENTMONTH_MC_GBP_TOTAL_SALES_AMOUNT+currentMonthTotalProcessingAmount;
                            }
                            else if("3".equals(terminalVO.getCardTypeId()))
                            {
                                DINERS_GBP_TOTAL_SALES_AMOUNT=DINERS_GBP_TOTAL_SALES_AMOUNT+totalProcessingAmount;
                                CURRENTMONTH_DINERS_GBP_TOTAL_SALES_AMOUNT=CURRENTMONTH_DINERS_GBP_TOTAL_SALES_AMOUNT+currentMonthTotalProcessingAmount;
                            }
                            else if("4".equals(terminalVO.getCardTypeId()))
                            {
                                AMEX_GBP_TOTAL_SALES_AMOUNT=AMEX_GBP_TOTAL_SALES_AMOUNT+totalProcessingAmount;
                                CURRENTMONTH_AMEX_GBP_TOTAL_SALES_AMOUNT=CURRENTMONTH_AMEX_GBP_TOTAL_SALES_AMOUNT+currentMonthTotalProcessingAmount;
                            }
                        }
                        else if(PZTransactionCurrency.JPY.toString().equals(currency))
                        {
                            isJPYAccount=true;
                            if("1".equals(terminalVO.getCardTypeId()))
                            {
                                VISA_JPY_TOTAL_SALES_AMOUNT=VISA_JPY_TOTAL_SALES_AMOUNT+totalProcessingAmount;
                                CURRENTMONTH_VISA_JPY_TOTAL_SALES_AMOUNT=CURRENTMONTH_VISA_JPY_TOTAL_SALES_AMOUNT+currentMonthTotalProcessingAmount;
                            }
                            else if("2".equals(terminalVO.getCardTypeId()))
                            {
                                MC_JPY_TOTAL_SALES_AMOUNT=MC_JPY_TOTAL_SALES_AMOUNT+totalProcessingAmount;
                                CURRENTMONTH_MC_JPY_TOTAL_SALES_AMOUNT=CURRENTMONTH_MC_JPY_TOTAL_SALES_AMOUNT+currentMonthTotalProcessingAmount;
                            }
                            else if("3".equals(terminalVO.getCardTypeId()))
                            {
                                DINERS_JPY_TOTAL_SALES_AMOUNT=DINERS_JPY_TOTAL_SALES_AMOUNT+totalProcessingAmount;
                                CURRENTMONTH_DINERS_JPY_TOTAL_SALES_AMOUNT=CURRENTMONTH_DINERS_JPY_TOTAL_SALES_AMOUNT+currentMonthTotalProcessingAmount;
                            }
                            else if("4".equals(terminalVO.getCardTypeId()))
                            {
                                AMEX_JPY_TOTAL_SALES_AMOUNT=AMEX_JPY_TOTAL_SALES_AMOUNT+totalProcessingAmount;
                                CURRENTMONTH_AMEX_JPY_TOTAL_SALES_AMOUNT=CURRENTMONTH_AMEX_JPY_TOTAL_SALES_AMOUNT+currentMonthTotalProcessingAmount;
                            }
                        }
                        else if(PZTransactionCurrency.PEN.toString().equals(currency))
                        {
                            isPENAccount=true;
                            if("1".equals(terminalVO.getCardTypeId()))
                            {
                                VISA_PEN_TOTAL_SALES_AMOUNT=VISA_PEN_TOTAL_SALES_AMOUNT+totalProcessingAmount;
                                CURRENTMONTH_VISA_PEN_TOTAL_SALES_AMOUNT=CURRENTMONTH_VISA_PEN_TOTAL_SALES_AMOUNT+currentMonthTotalProcessingAmount;
                            }
                            else if("2".equals(terminalVO.getCardTypeId()))
                            {
                                MC_PEN_TOTAL_SALES_AMOUNT=MC_PEN_TOTAL_SALES_AMOUNT+totalProcessingAmount;
                                CURRENTMONTH_MC_PEN_TOTAL_SALES_AMOUNT=CURRENTMONTH_MC_PEN_TOTAL_SALES_AMOUNT+currentMonthTotalProcessingAmount;
                            }
                            else if("3".equals(terminalVO.getCardTypeId()))
                            {
                                DINERS_PEN_TOTAL_SALES_AMOUNT=DINERS_PEN_TOTAL_SALES_AMOUNT+totalProcessingAmount;
                                CURRENTMONTH_DINERS_PEN_TOTAL_SALES_AMOUNT=CURRENTMONTH_DINERS_PEN_TOTAL_SALES_AMOUNT+currentMonthTotalProcessingAmount;
                            }
                            else if("4".equals(terminalVO.getCardTypeId()))
                            {
                                AMEX_PEN_TOTAL_SALES_AMOUNT=AMEX_PEN_TOTAL_SALES_AMOUNT+totalProcessingAmount;
                                CURRENTMONTH_AMEX_PEN_TOTAL_SALES_AMOUNT=CURRENTMONTH_AMEX_PEN_TOTAL_SALES_AMOUNT+currentMonthTotalProcessingAmount;
                            }
                        }

                        processingDetailsVO.setFirstTransactionDate(firstTransactionDate);
                        processingDetailsVO.setLastTransactionDate(lastTransactionDate);
                        processingDetailsVO.setActivationDate(activationDate);
                        processingDetailsVO.setTerminalVO(terminalVO);
                        processingDetailsVO.setRiskParameterVO(merchantRiskParameterVO);
                        processingDetailsVO.setTotalProcessingAmount(totalProcessingAmount);

                        processingDetailsVO.setCurrency(currency);
                        processingDetailsVO.setBankName(gateway);

                        double salesPercentage=0.00;
                        double chargebackPercentage=0.00;
                        double refundPercentage=0.00;
                        double declinePercentage=0.00;

                        if(processingDetailsVO.getSalesAmount()>0)
                        {
                            salesPercentage=(processingDetailsVO.getSalesAmount()/(processingDetailsVO.getSalesAmount()+processingDetailsVO.getDeclinedAmount())*100);
                        }
                        if(processingDetailsVO.getChargebackAmount()>0)
                        {
                            chargebackPercentage=(processingDetailsVO.getChargebackAmount()/processingDetailsVO.getSalesAmount()*100);
                        }
                        if(processingDetailsVO.getRefundAmount()>0)
                        {
                            refundPercentage=(processingDetailsVO.getRefundAmount()/processingDetailsVO.getSalesAmount()*100);
                        }
                        if(processingDetailsVO.getDeclinedAmount()>0)
                        {
                            declinePercentage=(processingDetailsVO.getDeclinedAmount()/(processingDetailsVO.getSalesAmount()+processingDetailsVO.getDeclinedAmount())*100);
                        }

                        processingDetailsVO.setChargebackPercentage(chargebackPercentage);
                        processingDetailsVO.setRefundPercentage(refundPercentage);
                        processingDetailsVO.setDeclinedPercentage(declinePercentage);
                        processingDetailsVO.setSalesPercentage(salesPercentage);
                        processingDetailsVOHashMap.put(terminalVO.getTerminalId(),processingDetailsVO);
                    }


                    CardTypeAmountVO cardTypeAmountVO=null;
                    CardTypeAmountVO currentMonthCardTypeAmountVO=null;
                    if(isUSDAccount)
                    {
                        cardTypeAmountVO=new CardTypeAmountVO();
                        currentMonthCardTypeAmountVO=new CardTypeAmountVO();

                        cardTypeAmountVO.setVISAAmount(VISA_USD_TOTAL_SALES_AMOUNT);
                        cardTypeAmountVO.setMCAmount(MC_USD_TOTAL_SALES_AMOUNT);
                        cardTypeAmountVO.setAMEXAmount(AMEX_USD_TOTAL_SALES_AMOUNT);
                        cardTypeAmountVO.setDINRESAmount(DINERS_USD_TOTAL_SALES_AMOUNT);

                        currentMonthCardTypeAmountVO.setVISAAmount(CURRENTMONTH_VISA_USD_TOTAL_SALES_AMOUNT);
                        currentMonthCardTypeAmountVO.setMCAmount(CURRENTMONTH_MC_USD_TOTAL_SALES_AMOUNT);
                        currentMonthCardTypeAmountVO.setAMEXAmount(CURRENTMONTH_AMEX_USD_TOTAL_SALES_AMOUNT);
                        currentMonthCardTypeAmountVO.setDINRESAmount(CURRENTMONTH_DINERS_USD_TOTAL_SALES_AMOUNT);

                        hashMap.put("USD",cardTypeAmountVO);
                        hashMap1.put("USD",currentMonthCardTypeAmountVO);
                    }
                    if(isEURAccount)
                    {
                        cardTypeAmountVO=new CardTypeAmountVO();
                        currentMonthCardTypeAmountVO=new CardTypeAmountVO();

                        cardTypeAmountVO.setVISAAmount(VISA_EURO_TOTAL_SALES_AMOUNT);
                        cardTypeAmountVO.setMCAmount(MC_EURO_TOTAL_SALES_AMOUNT);
                        cardTypeAmountVO.setAMEXAmount(AMEX_EURO_TOTAL_SALES_AMOUNT);
                        cardTypeAmountVO.setDINRESAmount(DINERS_EURO_TOTAL_SALES_AMOUNT);

                        currentMonthCardTypeAmountVO.setVISAAmount(CUREENTMONTH_VISA_EURO_TOTAL_SALES_AMOUNT);
                        currentMonthCardTypeAmountVO.setMCAmount(CUREENTMONTH_MC_EURO_TOTAL_SALES_AMOUNT);
                        currentMonthCardTypeAmountVO.setAMEXAmount(CURRENTMONTH_AMEX_EURO_TOTAL_SALES_AMOUNT);
                        currentMonthCardTypeAmountVO.setDINRESAmount(CURRENTMONTH_DINERS_EURO_TOTAL_SALES_AMOUNT);

                        hashMap.put("EUR",cardTypeAmountVO);
                        hashMap1.put("EUR",currentMonthCardTypeAmountVO);
                    }
                    if(isGBPAccount)
                    {
                        cardTypeAmountVO=new CardTypeAmountVO();
                        currentMonthCardTypeAmountVO=new CardTypeAmountVO();

                        cardTypeAmountVO.setVISAAmount(VISA_GBP_TOTAL_SALES_AMOUNT);
                        cardTypeAmountVO.setMCAmount(MC_GBP_TOTAL_SALES_AMOUNT);
                        cardTypeAmountVO.setAMEXAmount(AMEX_GBP_TOTAL_SALES_AMOUNT);
                        cardTypeAmountVO.setDINRESAmount(DINERS_GBP_TOTAL_SALES_AMOUNT);

                        currentMonthCardTypeAmountVO.setVISAAmount(CURRENTMONTH_VISA_GBP_TOTAL_SALES_AMOUNT);
                        currentMonthCardTypeAmountVO.setMCAmount(CURRENTMONTH_MC_GBP_TOTAL_SALES_AMOUNT);
                        currentMonthCardTypeAmountVO.setAMEXAmount(CURRENTMONTH_AMEX_GBP_TOTAL_SALES_AMOUNT);
                        currentMonthCardTypeAmountVO.setDINRESAmount(CURRENTMONTH_DINERS_GBP_TOTAL_SALES_AMOUNT);

                        hashMap.put("GBP",cardTypeAmountVO);
                        hashMap1.put("GBP",currentMonthCardTypeAmountVO);
                    }
                    if(isJPYAccount)
                    {
                        cardTypeAmountVO=new CardTypeAmountVO();
                        currentMonthCardTypeAmountVO=new CardTypeAmountVO();

                        cardTypeAmountVO.setVISAAmount(VISA_JPY_TOTAL_SALES_AMOUNT);
                        cardTypeAmountVO.setMCAmount(MC_JPY_TOTAL_SALES_AMOUNT);
                        cardTypeAmountVO.setAMEXAmount(AMEX_JPY_TOTAL_SALES_AMOUNT);
                        cardTypeAmountVO.setDINRESAmount(DINERS_JPY_TOTAL_SALES_AMOUNT);

                        currentMonthCardTypeAmountVO.setVISAAmount(CURRENTMONTH_VISA_JPY_TOTAL_SALES_AMOUNT);
                        currentMonthCardTypeAmountVO.setMCAmount(CURRENTMONTH_MC_JPY_TOTAL_SALES_AMOUNT);
                        currentMonthCardTypeAmountVO.setAMEXAmount(CURRENTMONTH_AMEX_JPY_TOTAL_SALES_AMOUNT);
                        currentMonthCardTypeAmountVO.setDINRESAmount(CURRENTMONTH_DINERS_JPY_TOTAL_SALES_AMOUNT);

                        hashMap.put("JPY",cardTypeAmountVO);
                        hashMap1.put("JPY",currentMonthCardTypeAmountVO);
                    }
                    if(isPENAccount)
                    {
                        cardTypeAmountVO=new CardTypeAmountVO();
                        currentMonthCardTypeAmountVO=new CardTypeAmountVO();

                        cardTypeAmountVO.setVISAAmount(VISA_PEN_TOTAL_SALES_AMOUNT);
                        cardTypeAmountVO.setMCAmount(MC_PEN_TOTAL_SALES_AMOUNT);
                        cardTypeAmountVO.setAMEXAmount(AMEX_PEN_TOTAL_SALES_AMOUNT);
                        cardTypeAmountVO.setDINRESAmount(DINERS_PEN_TOTAL_SALES_AMOUNT);

                        currentMonthCardTypeAmountVO.setVISAAmount(CURRENTMONTH_VISA_PEN_TOTAL_SALES_AMOUNT);
                        currentMonthCardTypeAmountVO.setMCAmount(CURRENTMONTH_MC_PEN_TOTAL_SALES_AMOUNT);
                        currentMonthCardTypeAmountVO.setAMEXAmount(CURRENTMONTH_AMEX_PEN_TOTAL_SALES_AMOUNT);
                        currentMonthCardTypeAmountVO.setDINRESAmount(CURRENTMONTH_DINERS_PEN_TOTAL_SALES_AMOUNT);

                        hashMap.put("PEN",cardTypeAmountVO);
                        hashMap1.put("PEN",currentMonthCardTypeAmountVO);
                    }
                }
            }
        }
        catch (SystemError se)
        {
            logger.error("System Error::::", se);
        }
        catch (Exception e)
        {
            logger.error("Exception::::", e);
        }
        req.setAttribute("currencyAmount",hashMap);
        req.setAttribute("currentmonthcurrencyamount",hashMap1);
        req.setAttribute("processingDetailsVOHashMap",processingDetailsVOHashMap);
        req.setAttribute("merchantDetailsVO",merchantDetailsVO);
        rd.forward(req, res);
    }
    private String validateParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.MEMBERID);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req, inputFieldsListOptional, errorList, false);
        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage();
                }
            }
        }
        return error;
    }
}
