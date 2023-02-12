import com.directi.pg.Admin;
import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.manager.*;
import com.manager.dao.CommissionManager;
import com.manager.enums.Charge_category;
import com.manager.enums.Charge_frequency;
import com.manager.enums.Charge_keyword;
import com.manager.enums.Charge_subKeyword;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.PartnerDetailsVO;
import com.manager.vo.TransactionSummaryVO;
import com.manager.vo.WLPartnerCommissionVO;
import com.manager.vo.WLPartnerInvoiceVO;
import com.manager.vo.merchantmonitoring.DateVO;
import com.manager.vo.payoutVOs.*;
import com.payment.exceptionHandler.PZDBViolationException;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * Created by IntelliJ IDEA.
 * User: Supriya
 * Date: 5/9/2016
 * Time: 3:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class WhitelableInvoice extends HttpServlet
{
    private static Logger log = new Logger(WhitelableInvoice.class.getName());
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(req, res);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        RequestDispatcher rd = req.getRequestDispatcher("/whitelabelinvoice.jsp?&ctoken=" + user.getCSRFToken());

        String partnerId = req.getParameter("partnerid");
        String startDate = req.getParameter("firstdate");
        String endDate = req.getParameter("lastdate");
        String startTime = req.getParameter("settledstarttime");
        String endTime = req.getParameter("settledendtime");
        String dynamicCommissionIds = req.getParameter("dynamiccommissionids");
        String processingCurrency = req.getParameter("processingcurrency");
        String randomChargeName = req.getParameter("randomchargename");
        String randomChargeValue = req.getParameter("randomchargevalue");
        CommissionManager commissionManager = new CommissionManager();
        Functions functions = new Functions();
        CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();
        List<WLPartnerCommissionVO> wlPartnerDynamicCommissionVOList = null;
        PartnerManager partnerManager = new PartnerManager();
        GatewayManager gatewayManager = new GatewayManager();
        PartnerDetailsVO partnerDetailsVO = null;
        Set<String> processingCurrSet = null;
        String monthlyMinCommissionModule;
        String profitSharingCommissionModule;
        String partnerShortName;
        String processorShortName;
        try
        {
            if (!ESAPI.validator().isValidInput("partnerid", partnerId, "OnlyNumber", 11, false))
            {
                //checkForDynamicCharges = false;
                req.setAttribute("statusMsg", "Invalid Partner Name<BR>");
                rd.forward(req, res);
                return;
            }
            if (functions.isValueNull(dynamicCommissionIds))
            {
                wlPartnerDynamicCommissionVOList = commissionManager.getWLPartnerDynamicCommissions(partnerId);
                req.setAttribute("wlPartnerCommissionVOList", wlPartnerDynamicCommissionVOList);
            }

            partnerDetailsVO = partnerManager.getPartnerDetails(partnerId);

            String reportingCurrency = partnerDetailsVO.getReportingCurrency();
            String processorPartnerId = partnerDetailsVO.getProcessorPartnerId();
            monthlyMinCommissionModule = partnerDetailsVO.getMonthlyMinCommissionModule();
            profitSharingCommissionModule = partnerDetailsVO.getProfitShareCommissionModule();

            //Getting all admin details
            String adminId = "1";
            if (functions.isValueNull(processorPartnerId))
            {
                adminId = partnerDetailsVO.getProcessorPartnerId();
            }

            partnerShortName = partnerDetailsVO.getCompanyName();
            if (functions.isValueNull(partnerDetailsVO.getPartnerShortName()))
            {
                partnerShortName = partnerDetailsVO.getPartnerShortName();
            }


            PartnerDetailsVO adminInfoVO = partnerManager.getPartnerDetails(adminId);
            processorShortName = adminInfoVO.getCompanyName();
            if (functions.isValueNull(adminInfoVO.getPartnerShortName()))
            {
                processorShortName = adminInfoVO.getPartnerShortName();
            }
            if ("Y".equals(profitSharingCommissionModule))
            {
                List<GatewayType> gatewayTypeList = gatewayManager.getPartnerProcessingBanksList(partnerId);
                if (gatewayTypeList.size() > 0)
                {
                    processingCurrSet = new HashSet();
                    for (GatewayType gatewayType : gatewayTypeList)
                    {
                        if (!reportingCurrency.equalsIgnoreCase(gatewayType.getCurrency()))
                        {
                            processingCurrSet.add(gatewayType.getCurrency());
                        }
                    }
                }
                req.setAttribute("processingCurrSet", processingCurrSet);
            }
            req.setAttribute("reportingCurrency", reportingCurrency);
        }
        catch (Exception e)
        {
            log.error("Exception for WL invoice::::::", e);
            req.setAttribute("statusMsg", "Internal error while processing your request");
            rd.forward(req, res);
            return;
        }

        //Validation Check
        StringBuffer sbError = new StringBuffer();
        if (!ESAPI.validator().isValidInput("firstdate", startDate, "fromDate", 16, false))
        {
            sbError.append("Invalid Start Date<BR>");
        }
        if (!ESAPI.validator().isValidInput("settledstarttime", req.getParameter("settledstarttime"), "time", 10, false))
        {
            sbError.append("Invalid Start Time<BR>");
        }
        if (!ESAPI.validator().isValidInput("lastdate", endDate, "fromDate", 16, false))
        {
            sbError.append("Invalid End Date<BR>");
        }
        if (!ESAPI.validator().isValidInput("settledendtime", req.getParameter("settledendtime"), "time", 10, false))
        {
            sbError.append("Invalid End Time<BR>");
        }
        if (!ESAPI.validator().isValidInput("randomchargename", randomChargeName, "Description", 500, true))
        {
            sbError.append("Invalid Random Charge Name<BR>");
        }
        if (!ESAPI.validator().isValidInput("randomchargename", randomChargeName, "SafeString", 500, true))
        {
            sbError.append("Invalid Random Charge Name<BR>");
        }


        //Dyanamic Commission Input Validation:input will be received from interface
        HashMap<String, String> dynamicInputValues = new HashMap();
        if (functions.isValueNull(dynamicCommissionIds))
        {
            String dynamicIds[] = dynamicCommissionIds.split(",");
            for (String id : dynamicIds)
            {
                String dCommName = req.getParameter("dynamicchargename_" + id);
                String dCommValueType = req.getParameter("dynamicchargetype_" + id);
                String dCommValue = req.getParameter("dynamicchargevalue_" + id);
                if ("Percentage".equals(dCommValueType))
                {
                    if (!ESAPI.validator().isValidInput(dCommName, dCommValue, "AmountStr", 20, false))
                    {
                        sbError.append("Invalid " + dCommName + " Value<BR>");
                    }
                    else
                    {
                        dynamicInputValues.put(id, dCommValue);
                    }
                }
                else
                {
                    if (!ESAPI.validator().isValidInput(dCommName, dCommValue, "Numbers", 20, false))
                    {
                        sbError.append("Invalid " + dCommName + " Value<BR>");
                    }
                    else
                    {
                        dynamicInputValues.put(id, dCommValue);
                    }
                }
            }
        }

        //Currency Conversion Value Validation:Value will be received from interface
        HashMap<String, String> currencyConversionRate = new HashMap();
        if (functions.isValueNull(processingCurrency))
        {
            String processingCurrencies[] = processingCurrency.split(",");
            for (String currency : processingCurrencies)
            {
                String conversionRate = req.getParameter(currency + "_conversion_rate");
                if (!ESAPI.validator().isValidInput(currency + "_conversion_rate", conversionRate, "NDigitsAmount", 20, false))
                {
                    sbError.append("Invalid " + currency + " conversion_rate" + " Value<BR>");
                }
                else
                {
                    currencyConversionRate.put(currency, conversionRate);
                }
            }
        }
        if (sbError.length() > 0)
        {
            req.setAttribute("statusMsg", sbError.toString());
            rd.forward(req, res);
            return;
        }

        String effectiveStartDate = commonFunctionUtil.convertDatepickerToTimestamp(startDate, startTime);
        String effectiveEndDate = commonFunctionUtil.convertDatepickerToTimestamp(endDate, endTime);
        String message = commonFunctionUtil.newValidateDateWithNewFormat(effectiveStartDate, effectiveEndDate, null, null);

        if (message != null)
        {
            log.error("Invalid settlement date");
            req.setAttribute("statusMsg", message);
            rd.forward(req, res);
            return;
        }

        String statusMsg = "";
        try
        {
            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat reportingFormat = new SimpleDateFormat("dd-MM-yyyy");


            TransactionManager transactionManager = new TransactionManager();
            WLPartnerCommissionReportGenerator partnerCommissionReportGenerator = new WLPartnerCommissionReportGenerator();
            WLPartnerInvoiceManager wlPartnerInvoiceManager=new WLPartnerInvoiceManager();

            HashMap<String, WLPartnerCommissionDetailsVO> otherCommissionDetailsVOHashMap = new LinkedHashMap();
            List<WLPartnerPerBankCommissionReportVO> wlPartnerPerBankCommissionReportVOList = new ArrayList();
            HashMap<String, ServiceTaxChargeVO> serviceTaxChargeVOHashMap = new LinkedHashMap();
            HashMap<String, ServiceTaxChargeVO> serviceTaxChargeVOHashMapFinal = new LinkedHashMap();

            String partnerFirstTransactionDate = wlPartnerInvoiceManager.getPartnerFirstTransactionDate(partnerDetailsVO.getCompanyName());
            log.error("partnerFirstTransactionDate::::" + partnerFirstTransactionDate);

            HashMap<String, List<WLPartnerCommissionVO>> listHashMap = commissionManager.getWLPartnerCommissionsList(partnerId);
            log.debug("listHashMap::" + listHashMap);
            if (!(listHashMap.size() > 0))
            {
                log.error("Commission Not Found");
                req.setAttribute("statusMsg", "Commission Not Found On Partner Bank");
                rd.forward(req, res);
                return;
            }

            WLPartnerInvoiceVO wlPartnerLastInvoiceVO = commissionManager.getLastWLPartnerInvoiceDetails(partnerId);
            if (wlPartnerLastInvoiceVO != null && functions.isValueNull(wlPartnerLastInvoiceVO.getEndDate()) )
            {
                Calendar cal = Calendar.getInstance();
                cal.setTime(targetFormat.parse(wlPartnerLastInvoiceVO.getEndDate()));
                cal.add(Calendar.SECOND, 1);
               // effectiveStartDate = targetFormat.format(cal.getTime());
            }
            /*else
            {
                //effectiveStartDate = targetFormat.format(targetFormat.parse(partnerFirstTransactionDate));
            }*/

            long invoiceCount = commissionManager.getWLPartnerWireCount(partnerId);
            long invoiceNumber = invoiceCount + 1;

            String invoiceName = partnerShortName + "_" + processorShortName + "_" + partnerDetailsVO.getReportingCurrency() + "_" + "000" + invoiceNumber;
            String invoiceDate = reportingFormat.format(new Date());

            DateVO dateVO = new DateVO();
            dateVO.setStartDate(effectiveStartDate);
            dateVO.setEndDate(effectiveEndDate);

            double netFinalCommissionAmount = 0.00;
            double otherTotalCommissionAmount = 0.00;
            double monthlyMinimumFee = 0.00;
            double totalTransactionFee = 0.00;
            double serviceTaxChargesAmount = 0.00;
            double previousBalanceAmount = wlPartnerInvoiceManager.getWLPartnerUnpaidAmount(partnerId);

            Set set = listHashMap.keySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext())
            {
                String pgTypeId = (String) iterator.next();
                GatewayType gatewayType = GatewayTypeService.getGatewayType(pgTypeId);

                double totalCommissionAmount = 0.00;
                double currencyRate = 1.00;
                double valueAfterConversion = 0.00;
                String gatewaysFirstTransactionDate = null;
                String tableName = null;
                TransactionSummaryVO transactionSummaryVO = null;
                if (gatewayType != null)
                {
                    if (currencyConversionRate.get(gatewayType.getCurrency()) != null)
                    {
                        currencyRate = Double.valueOf(currencyConversionRate.get(gatewayType.getCurrency()));
                    }

                    /*gatewaysFirstTransactionDate = transactionManager.getPartnersFirstTransactionOnGatewayType(gatewayType, partnerDetailsVO.getCompanyName());
                    if (!functions.isValueNull(gatewaysFirstTransactionDate))
                    {
                        continue;
                    }*/
                    tableName = Database.getTableName(gatewayType.getGateway());
                    gatewayType.setPartnerId(partnerDetailsVO.getCompanyName());
                    transactionSummaryVO = transactionManager.getGatewayTypeProcessingDetails(gatewayType, dateVO, tableName);
                }
                List<WLPartnerCommissionVO> wlPartnerCommissionVOsList = listHashMap.get(pgTypeId);
                HashMap<String, WLPartnerCommissionDetailsVO> commissionDetailsVOHashMap = new LinkedHashMap();
                try
                {
                    WLPartnerCommissionDetailsVO wlPartnerCommissionDetailsVO = null;
                    WLPartnerPerBankCommissionReportVO wlPartnerPerBankCommissionReportVO = null;
                    for (WLPartnerCommissionVO commissionVO : wlPartnerCommissionVOsList)
                    {
                        ChargeMasterVO chargeMasterVO = commissionVO.getChargeMasterVO();
                        if (chargeMasterVO.getCategory().equals(Charge_category.Others.toString()) && chargeMasterVO.getKeyword().equals(Charge_keyword.Setup.toString()) && chargeMasterVO.getSubKeyword().equals(Charge_subKeyword.Count.toString()))
                        {
                            wlPartnerCommissionDetailsVO = partnerCommissionReportGenerator.calculateWLPartnerSetupFee(partnerDetailsVO, commissionVO, dateVO);
                            otherTotalCommissionAmount = otherTotalCommissionAmount + wlPartnerCommissionDetailsVO.getTotal();
                            otherCommissionDetailsVOHashMap.put(wlPartnerCommissionDetailsVO.getChargeName(), wlPartnerCommissionDetailsVO);
                            if (chargeMasterVO.getCategory().equals(Charge_category.Others.toString()) && chargeMasterVO.getKeyword().equals(Charge_keyword.Setup.toString()) && chargeMasterVO.getSubKeyword().equals(Charge_subKeyword.Count.toString()) && chargeMasterVO.getFrequency().equals(Charge_frequency.Monthly.toString()))
                            {
                                monthlyMinimumFee = monthlyMinimumFee + wlPartnerCommissionDetailsVO.getTotal();
                            }
                        }
                        else if (chargeMasterVO.getCategory().equals(Charge_category.Others.toString()) && chargeMasterVO.getKeyword().equals(Charge_keyword.SSLCertificate.toString()) && chargeMasterVO.getSubKeyword().equals(Charge_subKeyword.Count.toString()))
                        {
                            long vCntCounter = 0;
                            double vDblAmount = 0.00;
                            double vDblTotal = 0.00;

                            wlPartnerCommissionDetailsVO = new WLPartnerCommissionDetailsVO();
                            wlPartnerCommissionDetailsVO.setChargeName(chargeMasterVO.getChargeName());
                            wlPartnerCommissionDetailsVO.setValueType(chargeMasterVO.getValueType());
                            wlPartnerCommissionDetailsVO.setChargeValue(String.valueOf(commissionVO.getCommissionValue()));

                            if (dynamicInputValues.get(commissionVO.getChargeId()) != null)
                            {
                                vCntCounter = Integer.parseInt(dynamicInputValues.get(commissionVO.getChargeId()));
                            }

                            vDblTotal = (vCntCounter * commissionVO.getCommissionValue());

                            wlPartnerCommissionDetailsVO.setCount(vCntCounter);
                            wlPartnerCommissionDetailsVO.setAmount(vDblAmount);
                            wlPartnerCommissionDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));

                            otherTotalCommissionAmount = otherTotalCommissionAmount + vDblTotal;
                            otherCommissionDetailsVOHashMap.put(wlPartnerCommissionDetailsVO.getChargeName(), wlPartnerCommissionDetailsVO);

                        }
                        else if (chargeMasterVO.getCategory().equals(Charge_category.Others.toString()) && chargeMasterVO.getKeyword().equals(Charge_keyword.BankIntegration.toString()) && chargeMasterVO.getSubKeyword().equals(Charge_subKeyword.Count.toString()))
                        {
                            long vCntCounter = 0;
                            double vDblAmount = 0.00;
                            double vDblTotal = 0.00;

                            wlPartnerCommissionDetailsVO = new WLPartnerCommissionDetailsVO();
                            wlPartnerCommissionDetailsVO.setChargeName(chargeMasterVO.getChargeName());
                            wlPartnerCommissionDetailsVO.setValueType(chargeMasterVO.getValueType());
                            wlPartnerCommissionDetailsVO.setChargeValue(String.valueOf(commissionVO.getCommissionValue()));

                            if (dynamicInputValues.get(commissionVO.getChargeId()) != null)
                            {
                                vCntCounter = Integer.parseInt(dynamicInputValues.get(commissionVO.getChargeId()));
                            }

                            vDblTotal = (vCntCounter * commissionVO.getCommissionValue());

                            wlPartnerCommissionDetailsVO.setCount(vCntCounter);
                            wlPartnerCommissionDetailsVO.setAmount(vDblAmount);
                            wlPartnerCommissionDetailsVO.setTotal(Functions.roundDBL(vDblTotal, 2));

                            otherTotalCommissionAmount = otherTotalCommissionAmount + vDblTotal;
                            otherCommissionDetailsVOHashMap.put(wlPartnerCommissionDetailsVO.getChargeName(), wlPartnerCommissionDetailsVO);
                        }
                        else if (chargeMasterVO.getCategory().equals(Charge_category.Others.toString()) && chargeMasterVO.getKeyword().equals(Charge_keyword.ServiceTax.toString()) && chargeMasterVO.getSubKeyword().equals(Charge_subKeyword.Amount.toString()))
                        {
                            ServiceTaxChargeVO serviceTaxChargeVO = new ServiceTaxChargeVO();
                            serviceTaxChargeVO.setChargeName(chargeMasterVO.getChargeName());
                            serviceTaxChargeVO.setChargeValue(String.valueOf(commissionVO.getCommissionValue()));
                            serviceTaxChargeVO.setValueType(chargeMasterVO.getValueType());
                            serviceTaxChargeVOHashMap.put(chargeMasterVO.getChargeName(), serviceTaxChargeVO);
                        }
                        else
                        {
                            wlPartnerCommissionDetailsVO = partnerCommissionReportGenerator.calculateWLPartnerCommissionValue(commissionVO, transactionSummaryVO);
                            totalCommissionAmount = totalCommissionAmount + wlPartnerCommissionDetailsVO.getTotal();
                            commissionDetailsVOHashMap.put(wlPartnerCommissionDetailsVO.getChargeName(), wlPartnerCommissionDetailsVO);
                        }
                    }
                    /*
                    * Add conversion only in case of profit sharing commission module
                    * */

                    if ("Y".equals(profitSharingCommissionModule))
                    {
                        valueAfterConversion = totalCommissionAmount * currencyRate;
                    }
                    else
                    {
                        valueAfterConversion = totalCommissionAmount;
                    }

                    netFinalCommissionAmount = netFinalCommissionAmount + valueAfterConversion;

                    if (gatewayType != null)
                    {
                        wlPartnerPerBankCommissionReportVO = new WLPartnerPerBankCommissionReportVO();
                        wlPartnerPerBankCommissionReportVO.setTransactionFeeHashMap(commissionDetailsVOHashMap);
                        wlPartnerPerBankCommissionReportVO.setGatewayType(gatewayType);
                        wlPartnerPerBankCommissionReportVO.setProcessingCurrency(gatewayType.getCurrency());
                        wlPartnerPerBankCommissionReportVO.setTransactionFeeAmount(totalCommissionAmount);
                        wlPartnerPerBankCommissionReportVO.setTransactionSummaryVO(transactionSummaryVO);
                        wlPartnerPerBankCommissionReportVO.setConversionRate(currencyRate);
                        wlPartnerPerBankCommissionReportVO.setConvertedAmount(valueAfterConversion);
                        wlPartnerPerBankCommissionReportVOList.add(wlPartnerPerBankCommissionReportVO);
                    }
                }
                catch (PZDBViolationException e)
                {
                    //e.printStackTrace();
                    statusMsg = "Internal error while processing your request";
                    log.error("PZDBViolationException:::::" + e);
                }
                catch (Exception e)
                {
                    //e.printStackTrace();
                    log.error("Exception:::::::",e);
                    statusMsg = "Internal error while processing your request";
                }
            }
            WLPartnerCommissionReportVO wlPartnerCommissionReportVO = new WLPartnerCommissionReportVO();
            if (functions.isValueNull(randomChargeName) && functions.isValueNull(randomChargeValue))
            {
                double randomChargeValueDbl = Double.valueOf(randomChargeValue);
                otherTotalCommissionAmount = otherTotalCommissionAmount + randomChargeValueDbl;
                wlPartnerCommissionReportVO.setRandomChargeName(randomChargeName);
                wlPartnerCommissionReportVO.setRandomChargeValue(Functions.roundDBL(Double.valueOf(randomChargeValue), 2));
            }

            //added new monthly minimum fee formula
            totalTransactionFee = netFinalCommissionAmount;
            if ("Y".equals(monthlyMinCommissionModule))
            {
                if (totalTransactionFee > monthlyMinimumFee)
                {
                    totalTransactionFee = totalTransactionFee - monthlyMinimumFee;
                }
                else
                {
                    totalTransactionFee = 0.00;
                }
            }

            //added new service charge
            if (serviceTaxChargeVOHashMap.size() > 0)
            {
                String chargeName = null;
                Set serviceChargeSet = serviceTaxChargeVOHashMap.keySet();
                Iterator itr = serviceChargeSet.iterator();
                while (itr.hasNext())
                {
                    chargeName = (String) itr.next();
                    ServiceTaxChargeVO serviceTaxChargeVO = calculateServiveTax(chargeName, serviceTaxChargeVOHashMap, totalTransactionFee + otherTotalCommissionAmount);
                    serviceTaxChargesAmount = serviceTaxChargesAmount + serviceTaxChargeVO.getTotal();
                    serviceTaxChargeVOHashMapFinal.put(serviceTaxChargeVO.getChargeName(), serviceTaxChargeVO);
                }
            }

            netFinalCommissionAmount = totalTransactionFee + otherTotalCommissionAmount + serviceTaxChargesAmount + previousBalanceAmount;
            wlPartnerCommissionReportVO.setInvoiceNumber(invoiceName);
            wlPartnerCommissionReportVO.setInvoiceDate(invoiceDate);
            wlPartnerCommissionReportVO.setCurrency(partnerDetailsVO.getReportingCurrency());
            wlPartnerCommissionReportVO.setPspName(partnerDetailsVO.getHostUrl());
            if(functions.isValueNull(partnerDetailsVO.getOrganizationName()))
                wlPartnerCommissionReportVO.setCustomer(partnerDetailsVO.getOrganizationName());
            else
                wlPartnerCommissionReportVO.setCustomer(partnerDetailsVO.getPartnerName());

            wlPartnerCommissionReportVO.setStartDate(effectiveStartDate);
            wlPartnerCommissionReportVO.setEndDate(effectiveEndDate);
            wlPartnerCommissionReportVO.setStartPeriod(reportingFormat.format(targetFormat.parse(effectiveStartDate)));
            wlPartnerCommissionReportVO.setEndPeriod(reportingFormat.format(targetFormat.parse(effectiveEndDate)));

            wlPartnerCommissionReportVO.setTotalTransactionFee(totalTransactionFee);
            wlPartnerCommissionReportVO.setNetFinalFeeAmount(netFinalCommissionAmount);
            wlPartnerCommissionReportVO.setOtherFeeAmount(otherTotalCommissionAmount);
            wlPartnerCommissionReportVO.setServiceTaxChargesAmount(serviceTaxChargesAmount);
            wlPartnerCommissionReportVO.setPreviousBalanceAmount(previousBalanceAmount);

            wlPartnerCommissionReportVO.setOtherFeeHashMap(otherCommissionDetailsVOHashMap);
            wlPartnerCommissionReportVO.setServiceTaxChargeVOHashMapFinal(serviceTaxChargeVOHashMapFinal);
            wlPartnerCommissionReportVO.setPartnerDetailsVO(partnerDetailsVO);
            //System.out.println("total connectors=" + wlPartnerPerBankCommissionReportVOList.size());
            wlPartnerCommissionReportVO.setWlPartnerPerBankCommissionReportVOList(wlPartnerPerBankCommissionReportVOList);

            session.setAttribute("wlPartnerCommissionReportVO", wlPartnerCommissionReportVO);
            session.setAttribute("entity", "wlpartner");

        }
        catch (PZDBViolationException e)
        {
            //e.printStackTrace();
            log.error("PZDBViolationException:::::" , e);
            statusMsg = "Internal error while processing your request";
        }
        catch (ParseException pe)
        {
            //pe.printStackTrace();
            log.error("ParseException:::::" , pe);
            statusMsg = "Internal error while processing your request";
        }
        req.setAttribute("statusMsg", statusMsg);
        rd.forward(req, res);
        return;
    }

    public ServiceTaxChargeVO calculateServiveTax(String chargeName, HashMap<String, ServiceTaxChargeVO> grossChargeVOHashMap, double serviceTaxAmount)
    {
        ChargeDetailsVO detailsVO = grossChargeVOHashMap.get(chargeName);
        ServiceTaxChargeVO serviceTaxChargeVO = new ServiceTaxChargeVO();
        serviceTaxChargeVO.setCount(0);
        serviceTaxChargeVO.setChargeValue(detailsVO.getChargeValue());
        serviceTaxChargeVO.setChargeName(detailsVO.getChargeName());
        serviceTaxChargeVO.setValueType(detailsVO.getValueType());
        serviceTaxChargeVO.setAmount(serviceTaxAmount);
        double total = serviceTaxAmount * Double.valueOf(detailsVO.getChargeValue()) / 100;
        serviceTaxChargeVO.setTotal(Functions.roundDBL(total, 2));
        return serviceTaxChargeVO;
    }

}