package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.manager.DateManager;
import com.manager.PartnerManager;
import com.manager.dao.PartnerDAO;
import com.manager.vo.merchantmonitoring.DateVO;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Rajeev Singh
 * Date: 10/5/13
 * Time: 3:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class PartnerDashboard extends HttpServlet
{
    static private Logger log = new Logger(PartnerDashboard.class.getName());
    private static final ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.CurrencyColor");

    //private static String xmlDataFilePath = ApplicationProperties.getProperty("PARTNER_XML_DATA_FILE_PATH");
    //private static String[] colorPallet = new String[]{"#68c39f", "#edce8c", "#b4c0c0", "#e8a451", "#e8e85a", "#4a525f", "#99e6ff", "#b399ff", "#6fc51b", "#ff99e6", "#ff9999", "#3b76c7", "#d689c0", "#b93a71", "#990000", "#54ce28", "#68c39f", "#edce8c", "#4A2CC9", "#C92C5C"};

    public static void main(String[] args)
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, 1, 0, 0, 0);
        //String lastYearTime = String.valueOf(cal.getTimeInMillis() / 1000);
    }
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        try
        {
            HttpSession session = req.getSession();
            PartnerFunctions partner = new PartnerFunctions();
            if (!partner.isLoggedInPartner(session))
            {
                res.sendRedirect("/partner/Logout.jsp");
                return;
            }

            Calendar rightNow = Calendar.getInstance();
            //SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            //Date date = null;

            User user = (User) session.getAttribute("ESAPIUserSessionKey");
            rightNow.add(Calendar.DAY_OF_YEAR, -183);
            rightNow.set(rightNow.get(Calendar.YEAR), rightNow.get(Calendar.MONTH) + 1, 1, 0, 0, 0);//adjust to 12 months before current month with 0 hrs and min
            PartnerManager partnerManager = new PartnerManager();
            DateManager dateManager = new DateManager();
            //  String totalSalesAmount = "";
           /* String totalSettledAmount = "";
            String totalRefundAmount = "";
            String totalChargebackAmount = "";*/
            //   String totalDeclinedAmount = "";

            StringBuffer stringbuffersales= new StringBuffer();
            StringBuffer stringbufferDeclined= new StringBuffer();


            StringBuffer stringBufferSalesPerCurrencyChart = new StringBuffer();
            StringBuffer stringBufferValidStatusChart = new StringBuffer();
            StringBuffer stringBufferSalesBarChart = new StringBuffer();
            StringBuffer colorFromCurrencyProperties = new StringBuffer();

            StringBuffer sb = new StringBuffer();
            Functions functions = new Functions();
            PartnerDAO partnerDAO=new PartnerDAO();


            String currency = req.getParameter("currency");
            String pid = req.getParameter("pid");
            //String fromStartDate = req.getParameter("fromdate");
            //String toEndDate = req.getParameter("todate");
            String payBrand = req.getParameter("payBrand");
            String payMode = req.getParameter("payMode");
            String datefilter= req.getParameter("datefilter");
            String status=req.getParameter("status");
            RequestDispatcher rd = req.getRequestDispatcher("/partnerDashboard.jsp?ctoken=" + user.getCSRFToken());


            // System.out.print("Paymode Value"+payMode);
            String partner_id = "";
            String partnerid=(String)session.getAttribute("merchantid");

            try
            {
                if (functions.isValueNull(pid))
                {
                    partner_id = pid;
                }
                 else if (!functions.isValueNull(pid))
                {
                    String Roles = partner.getRoleofPartner(partnerid);
                    if (Roles.contains("superpartner"))
                    {
                        LinkedHashMap<Integer, String> memberHash = new LinkedHashMap();
                        memberHash =partner.getPartnerDetails(partnerid);
                        partner_id = partnerid;
                        for(int partnerID : memberHash.keySet())
                        {
                            partner_id+= "," + Integer.toString(partnerID);
                        }
                    }
                    else
                    {
                        partner_id = partnerid;
                    }
                }
                else
                {
                    req.setAttribute("error", "Invalid Partner Mapping");
                    rd.forward(req, res);
                    return;
                }
            }catch (Exception e){
                log.error("Exception---" + e);
            }
            String partnerName="";
            partnerName=partnerDAO.getPartnerName(partner_id );



            String partnerName1 = (String) session.getAttribute("partnername");
            List<String> currencyList = partnerManager.getValidCurrencyListForPartner(partnerName);
            String last="";

            if(!functions.isValueNull(currency))
            {
                if(currencyList!=null && currencyList.size()>0)
                    currency =  currencyList.get(0);
            }
            List<String> currencyListDb = new ArrayList<>();
            String singleCurrency = req.getParameter("currency");
            if (functions.isValueNull(singleCurrency))
            {
                currencyListDb.add(singleCurrency);
            }
            else
            {
                currencyListDb= currencyList;
            }
            if (currencyListDb.size()>0)
            {
                last= currencyListDb.get(currencyListDb.size()-1);
                for (String color: currencyListDb)
                {
                    colorFromCurrencyProperties.append(RB.getString(color));
                    if (!last.equals(color)) {
                        colorFromCurrencyProperties.append(",");
                    }
                }
            }

            //Select Option BrandList
            //  HashMap<String,String> payBrandList = partnerManager.getValidPayBrandListForPartner(partnerName);
            HashMap<String,String> payBrandList = partnerManager.getValidPayBrandListForPartner(partnerName,payMode);
            HashMap<String,String> payModeList = partnerManager.getValidPayModeListForPartner(partnerName);

            DateVO dateVOrange= new DateVO();
            if (functions.isValueNull(datefilter))
            {
                dateVOrange= dateManager.getDateRangeNew(datefilter);
            }
            String dashboard_value= req.getParameter("submit");
            //Select Option ModelList
            HashMap<String, String> hashMapSalesPerCurrencyChart =null;
            if (functions.isValueNull(dashboard_value) || functions.isValueNull(status) || (functions.isValueNull(dateVOrange.getDateLabel())))
            {
                hashMapSalesPerCurrencyChart = partnerManager.getSalesPerCurrencyChartForPartner(partnerName, currency, payBrand, payMode, dateVOrange, dashboard_value, status);
            }
            log.error("Sales per currency Chart---"+hashMapSalesPerCurrencyChart);


            HashMap<String, String> hashMapValidStatusChart = partnerManager.getStatusChartForPartner(partnerName, currency, payBrand, payMode,dateVOrange,dashboard_value,status);
            log.error("Status Chart---"+hashMapValidStatusChart);

            Hashtable<String,String> totalSalesAmount = partnerManager.getTotalSalesAmount(partnerName, currency, payBrand, payMode,dateVOrange,dashboard_value,status);
            if ( totalSalesAmount.size()>0)
            {
                for (Map.Entry<String,String> entry: totalSalesAmount.entrySet())
                {
                    String salesAmount= entry.getKey();
                    String salesCount= entry.getValue();
                    stringbuffersales.append(salesAmount+ "," +salesCount);
                }
            }
            String []hashtablesale= stringbuffersales.toString().split(",");

           /* totalSettledAmount = partnerManager.getTotalSettledAmount(partnerName, currency, payBrand, payMode);
            totalRefundAmount = partnerManager.getTotalRefund(partnerName, currency, payBrand, payMode);
            totalChargebackAmount = partnerManager.getTotalChargeback(partnerName, currency, payBrand, payMode);*/
            /*Hashtable<String,String> totalDeclinedAmount = partnerManager.getTotalDeclinedAmount(partnerName, currency, payBrand, payMode,dateVOrange,dashboard_value,status);

            if (totalDeclinedAmount.size()>0)
            {
                for (Map.Entry<String,String> entry: totalDeclinedAmount.entrySet())
                {
                    String declinedAmt= entry.getKey();
                    String declinedCount= entry.getValue();
                    stringbufferDeclined.append(declinedAmt+ "," +declinedCount);
                }
            }
            String[] hashtabledeclined= stringbufferDeclined.toString().split(",");
*/
            HashMap<String,String> totalAmount= partnerManager.getTotalAmountandCount(partnerName, currency, payBrand, payMode,dateVOrange,dashboard_value,status);
            log.error("totalAmount from PartnerDashboard++ "+totalAmount);

            if (hashMapSalesPerCurrencyChart!= null && hashMapSalesPerCurrencyChart.size() > 0) {
                for (Map.Entry<String, String> entry : hashMapSalesPerCurrencyChart.entrySet()) {
                    String countAmount = entry.getKey();
                    String countryName = entry.getValue();
                    stringBufferSalesPerCurrencyChart.append("{ value: " + countryName + " , label: '" + countAmount + "'},");
                }
            }
            else
            {
                String blankData= "No Data To Display";
                stringBufferSalesPerCurrencyChart.append("{ value: " + 00 + " , label: '" + blankData + "'},");
            }
            String salesPerCurrencyDonutChartJsonStr = "data : [" + stringBufferSalesPerCurrencyChart.toString() + "],";
            //-----------------------

            if (hashMapValidStatusChart.size() > 0) {
                for (Map.Entry<String, String> entry : hashMapValidStatusChart.entrySet()){
                    String countStatus = entry.getKey();
                    String statusName = entry.getValue();
                    stringBufferValidStatusChart.append("{ value: " + statusName  + " , label: '" + countStatus + "'},");
                }
            }
            else
            {
                String blankData = "No Data To Display";
                stringBufferValidStatusChart.append("{ value: " + 0.00 + " , label: '" + blankData + "'},");
            }
            String statusChartDonutChartJsonStr = "data : [" +stringBufferValidStatusChart.toString() + "],";

           /* HashMap<String, String> hashMapSalesBarChart = new LinkedHashMap();
            List<DateVO> dateVOs = dateManager.getMonthlyLineChartQuarter();
            for (DateVO dateVO : dateVOs) {
                String salesBarData = partnerManager.getMonthlyTransactionDetailsForPartner(partnerName, dateVO, currency, payBrand, payMode);
                log.error("Currency per amount---" + salesBarData);
                hashMapSalesBarChart.put(dateVO.getDateLabel(), salesBarData);
            }
            log.error("sales bar per month---" + hashMapSalesBarChart);

            for (Map.Entry<String, String> entry : hashMapSalesBarChart.entrySet()){
                String month = entry.getKey();
                String hashMapFinal = entry.getValue();
                stringBufferSalesBarChart.append("{x: '" + month + "', " + hashMapFinal + " },");
            }*/
            if (functions.isValueNull(dashboard_value) || functions.isValueNull(status) || (functions.isValueNull(dateVOrange.getDateLabel())))
            {
                stringBufferSalesBarChart.append(partnerManager.getMonthlyTransactionDetailsForPartnerNew(partnerName, dateVOrange, currency, payBrand, payMode, dashboard_value, status));
            }
            else
            {
                stringBufferSalesBarChart.append(partnerManager.getMonthlyTransactionDetailsForPartnerNew(partnerName, dateVOrange, currency, payBrand, payMode, dashboard_value, status));
            }
            log.error("stringBufferSalesBarChart---" + stringBufferSalesBarChart);

            for (String s:currencyListDb) {
                sb.append("'"+s+"',");
            }

            String salesBarChartJsonStr = "data : [" + stringBufferSalesBarChart.toString() + "],";
            String salesBar= stringBufferSalesBarChart.toString();
            if (functions.isValueNull(salesBar) && salesBar.contains(" "))
            {
                String blankdata="No Data To Display";
                sb.append("'"+blankdata+"'");
            }
            String salesBarChartLabelJsonStr =" "+sb+" ";

            req.setAttribute("list", currencyList);
            req.setAttribute("payBrandList", payBrandList);
            req.setAttribute("payModeList", payModeList);
            req.setAttribute("colorFromCurrencyProperties",colorFromCurrencyProperties);
            req.setAttribute("hashMapValidStatusChart", hashMapValidStatusChart);
            req.setAttribute("hashMapSalesPerCurrencyChart", hashMapSalesPerCurrencyChart);
            req.setAttribute("totalSalesAmount", totalSalesAmount);
            /*req.setAttribute("totalSettledAmount", totalSettledAmount);
            req.setAttribute("totalRefundAmount", totalRefundAmount);
            req.setAttribute("totalChargebackAmount", totalChargebackAmount);*/
           // req.setAttribute("totalDeclinedAmount", totalDeclinedAmount);
            req.setAttribute("salesPerCurrencyDonutChartJsonStr", salesPerCurrencyDonutChartJsonStr);
            req.setAttribute("statusChartDonutChartJsonStr", statusChartDonutChartJsonStr);
            req.setAttribute("salesBarChartJsonStr", salesBarChartJsonStr);
            req.setAttribute("salesBarChartLabelJsonStr", salesBarChartLabelJsonStr);
            req.setAttribute("hashtablesale",hashtablesale);
          //  req.setAttribute("hashtabledeclined",hashtabledeclined);
            req.setAttribute("totalAmount",totalAmount);
            req.setAttribute("datelabel",datefilter);
            req.setAttribute("status",status);

            //RequestDispatcher rd = req.getRequestDispatcher("/partnerDashboard.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (Exception e)
        {
            log.error("Exception in Partner Dashboard", e);
        }
    }
}