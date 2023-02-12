package net.partner;
import com.directi.pg.*;
import com.logicboxes.util.ApplicationProperties;
import com.logicboxes.util.Util;
import com.manager.vo.chartVOs.barchart.BarChartVO;
import com.manager.vo.chartVOs.barchart.Dataset;
import com.manager.vo.morrisChartVOs.morrisDonut.Datas;
import com.manager.vo.morrisChartVOs.morrisDonut.MorrisDonutChartVO;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import java.io.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 10/5/13
 * Time: 3:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class PartnerDashboardperCurrency extends HttpServlet
{
    static Logger logger = new Logger(PartnerDashboard.class.getName());
    private static String xmlDataFilePath = ApplicationProperties.getProperty("PARTNER_XML_DATA_FILE_PATH");
    //private static String[] colorPallet = new String[]{"AFD8F8", "F6BD0F", "8BBA00", "FF8E46", "008E8E", "D64646", "8E468E", "588526", "B3AA00", "008ED6", "9D080D", "A186BE", "78F04A"};
    private static String[] colorPallet = new String[] {"#68c39f", "#edce8c", "#b4c0c0", "#e8a451","#e8e85a", "#4a525f", "#99e6ff", "#b399ff", "#6fc51b", "#ff99e6", "#ff9999", "#3b76c7", "#d689c0", "#b93a71", "#990000", "#54ce28", "#68c39f", "#edce8c", "#4A2CC9", "#C92C5C"};

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        HttpSession  session = req.getSession();
        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        Connection conn = null;

        String perCurrency = req.getParameter("currency");
        ResultSet rs = null;
        ResultSet rs1 = null;
        try
        {
            conn = Database.getConnection();
            //int partnerId = Integer.parseInt((String) session.getAttribute("merchantid"));
            String partnerName = (String) session.getAttribute("partnername");

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -365);
            //adjust to 12 months before current month with 0 hrs and min
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, 1, 0, 0, 0);
            String lastYearTime = String.valueOf(cal.getTimeInMillis() / 1000);

            //PartnerFunctions pPartner = new PartnerFunctions();
            //String sMemberList = pPartner.getPartnerMemberRS(String.valueOf(partnerId));
            //StringBuffer countquery=null;

            Set<String> gatewayTypeSet = gatewayTransactionTables();
            Iterator i = gatewayTypeSet.iterator();

            StringBuffer statusquery = new StringBuffer();
            StringBuffer salesquery = new StringBuffer();

            //Status Query
            String fields = "status,count(*) as count";
            String field = "date_format(from_unixtime(dtstamp),'%b-%y') as month,sum(captureamount) as amount,currency AS curr,max(dtstamp) as dtstamp ";
            String fieldz = "date_format(from_unixtime(dtstamp),'%b-%y') as month,sum(amount) as amount,currency AS curr,max(dtstamp) as dtstamp ";
            while(i.hasNext())
            {
                String tableName = (String)i.next();
                statusquery.append("select " + fields + " from " + tableName + " where");
                statusquery.append(" totype ='"+partnerName+"'");
                statusquery.append(" and dtstamp >='" + lastYearTime + "'");
                statusquery.append(" group by status");

                salesquery.append(" select " + field + " from " + tableName + " where");
                salesquery.append(" totype ='"+partnerName+"'");
                salesquery.append(" and dtstamp >='" + lastYearTime + "'");
                salesquery.append(" and currency='"+ perCurrency +"'");
                salesquery.append(" and status in ('capturesuccess','settled','chargeback','markedforreversal','reversed')");
                salesquery.append(" group by month,curr");
                salesquery.append(" UNION");
                salesquery.append(" select " + fieldz + " from " + tableName + " where");
                salesquery.append(" totype ='"+partnerName+"'");
                salesquery.append(" and dtstamp >='" + lastYearTime + "'");
                salesquery.append(" and currency='"+ perCurrency +"'");
                salesquery.append(" and status='authsuccessful'");
                salesquery.append(" group by month,curr");

                /*if(i.hasNext())
                {
                    salesquery.append(" UNION ");
                    statusquery.append(" UNION ");
                }*/
            }

            //For Status & ProgressStatus
            logger.debug(statusquery.toString()+"<---------->"+salesquery.toString());
            StringBuffer statusCountquery = new StringBuffer("select status, SUM(count) as count from (" + statusquery + ") as temp  group by status ");
            logger.debug("Countquery in Status Chart-------->" + statusCountquery);
            rs = Database.executeQuery(statusCountquery.toString(), conn);

            HashMap<String,Integer> statusHashMap = new HashMap<String, Integer>();
            int colourCount = 0;

            List<String> backgroundColor= new ArrayList<String>();
            while(rs.next())
            {
                statusHashMap.put(rs.getString("status"), rs.getInt("count"));

                backgroundColor.add(colorPallet[colourCount]);
                colourCount++;
            }

            //For Sales & Currency
            StringBuffer salesCountquery = new StringBuffer("select month, SUM(amount) as amount,curr,max(dtstamp) as dt from (" + salesquery + ") as temp  group by month,curr order by dt");
            logger.debug("Countquery in Sales Chart in doPost-------->"+salesCountquery);
            rs1 = Database.executeQuery(salesCountquery.toString(), conn);

            Set<String> currencySet= new HashSet<String>();

            List<String> salesArrayList = new ArrayList<String>();
            while (rs1.next())
            {
                salesArrayList.add(rs1.getString("month"));
                salesArrayList.add(rs1.getString("amount"));
                salesArrayList.add(rs1.getString("curr"));
                currencySet.add(rs1.getString("curr"));
            }

            //for Per Currency
            TreeSet<String> sortedCurrency = new TreeSet<String>(currencySet);

            String charttoken = Functions.getFormattedDate("yyMMddHHmmss");
            session.setAttribute("charttoken",charttoken);

            StringBuilder chartString = new StringBuilder();
            StringBuilder currencychartString = new StringBuilder();
            StringBuilder currencyString = new StringBuilder();

            prepareStatusChartData(charttoken, chartString,statusHashMap,backgroundColor,partnerName);
            prepareCurrencyChartData(charttoken, currencychartString,salesArrayList,backgroundColor,partnerName);
            String morris =prepareSalesChartData(charttoken, currencychartString,salesArrayList,partnerName);

            BigDecimal salesCount = totalSales(lastYearTime, conn, perCurrency, partnerName);

            List<BigDecimal> totalHashtable = totalChargebackCaptureRefund(lastYearTime, conn, perCurrency,partnerName);

            List<BigDecimal> totalAuthSuccessfulHashtable = totalAuthSuccessful(lastYearTime,perCurrency, conn, partnerName);

            req.setAttribute("totalAuthSuccessfulHashtable",totalAuthSuccessfulHashtable);

            req.setAttribute("totalHashtable",totalHashtable);

            req.setAttribute("morrisDemo",morris);

            req.setAttribute("countsales",salesCount);

            req.setAttribute("saleschart_donuts",currencychartString);
            req.setAttribute("statusChart",chartString);

            int totalHash = progressStatusTotal(lastYearTime,conn,partnerName);
            Hashtable statusHash = progressStatusChart(statusHashMap);

            //New code added here

            req.setAttribute("total",totalHash);
            req.setAttribute("statusHash",statusHash);

            logger.debug("----currencychartString-----"+currencychartString);
            logger.debug("----chartString-----"+chartString);
            logger.debug("----JSONSTRING currency chart String-----"+currencyString);

            RequestDispatcher rd = req.getRequestDispatcher("/partnerDashboardperCurrency.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (SQLException e)
        {
            logger.error("Error in DashBoard SQLException :",e);
        }
        catch (ParseException pe)
        {
            logger.error("Error while Parsing DashBoard :",pe);
        }
        catch (SystemError systemError)
        {
            logger.error("Error in DashBoard SystemError :", systemError);
        }
        finally
        {
            if (conn != null)
            {
                try
                {
                    Database.closeResultSet(rs);
                    Database.closeResultSet(rs1);
                    conn.close();
                }
                catch (SQLException e)
                {
                    logger.error("Error while clossing connection :" + e.getMessage());
                }
            }
        }
    }


    private void prepareStatusChartData(String charttoken,StringBuilder chartString,HashMap<String,Integer> statusHashMap,List<String> backgroundColor, String partnerName)
            throws SystemError, SQLException, ParseException
    {

        logger.debug("path---" + xmlDataFilePath + "/StatusData_" + partnerName + "_" + Functions.getFormattedDate("yyMM")+ ".json");
        if(isFileExist(xmlDataFilePath + "/StatusData_" + partnerName + "_"+ Functions.getFormattedDate("yyMM")+ ".json"))
            return ;
        logger.debug("prepare Status Chart Data for partner");

        MorrisDonutChartVO donutChartVO = null;

        if(statusHashMap.size()>0)
        {
            donutChartVO = new MorrisDonutChartVO();

            List<Datas> datasetses = new ArrayList<Datas>();

            Datas dataset = null;

            String element="donut-chart";

            String status = "";
            double count = 0.0;

            logger.debug("StatusHashMap outside------->"+statusHashMap.toString());

            Set statusSet = statusHashMap.entrySet();
            Iterator statusIterator = statusSet.iterator();
            while(statusIterator.hasNext())
            {
                dataset = new Datas();
                Map.Entry<String,Integer> statusMapEntry = (Map.Entry<String, Integer>) statusIterator.next();

                status = statusMapEntry.getKey();
                count = statusMapEntry.getValue();

                dataset.setValue(count);
                dataset.setLabel(status);
                datasetses.add(dataset);
            }

            donutChartVO.setDatas(datasetses);
            donutChartVO.setElement(element);
            donutChartVO.setColors(backgroundColor);
        }
        else
        {

            donutChartVO = new MorrisDonutChartVO();

            List<Datas> datasetses = new ArrayList<Datas>();

            Datas dataset = null;

            String element="donut-chart";

            dataset = new Datas();

            dataset.setValue(0.0);
            dataset.setLabel("No data to display");
            datasetses.add(dataset);

            donutChartVO.setDatas(datasetses);
            donutChartVO.setElement(element);
        }



        StringWriter stringWriter = new StringWriter();
        String jsonString = null;
        try
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(MorrisDonutChartVO.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
            jaxbMarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
            jaxbMarshaller.marshal(donutChartVO, stringWriter);
            logger.debug("XMLtoJSON BEFORE:::" + stringWriter.toString());
        }
        catch (PropertyException e)
        {
            logger.error("Property Exception while converting to json", e);
        }
        catch (JAXBException e)
        {
            logger.error("JAXB Exception while converting to json", e);
        }

        chartString.append(stringWriter.toString());

        createFile(xmlDataFilePath + "/StatusData_" + partnerName + "_" + Functions.getFormattedDate("yyMM") + "_" + charttoken + ".json", chartString);
    }

    private void prepareCurrencyChartData(String charttoken,StringBuilder donutString, List<String> salesArrayList,List<String> backGroundColor, String partnerName)
            throws SystemError, SQLException, ParseException
    {
        logger.debug("path---" + xmlDataFilePath + "/CurrencyData_" + partnerName + "_" + Functions.getFormattedDate("yyMM")+ ".json");
        if(isFileExist(xmlDataFilePath + "/CurrencyData_" + partnerName + "_"+ Functions.getFormattedDate("yyMM")+ ".json"))
            return;

        logger.debug("prepare Sales Chart Data");
        PartnerFunctions pPartner = new PartnerFunctions();

        MorrisDonutChartVO morrisDonutChartVO = null;
        if(salesArrayList.size()>0)
        {
            StringBuilder statusChartXml = new StringBuilder("<graph xaxisname='Months' yaxisname='Total Amount' caption='Monthwise sales for last 1 year' lineThickness='1' animation='1' showNames='1' alpha='100' showLimits='1' decimalPrecision='1' rotateNames='1' numDivLines='3' limitsDecimalPrecision='0' showValues='0'>");

            //BarChart
            BarChartVO barChartVO = new BarChartVO();          /// Add VO

            List<Dataset> datasetses = new ArrayList<Dataset>();
            List<Datas> morrisDatasets = new ArrayList<Datas>();

            List<String> labels= new ArrayList<String>();
            int colourCount = 0;
            String element="graph";

            Map<String,Dataset> datasetMap = new TreeMap<String,Dataset>();
            Map<String,Double[]> dataCurrencyMap = new TreeMap<String,Double[]>();
            Map<String,String> salesDonutCurrencyMap = new TreeMap<String,String>();

            for(int j=0;j<salesArrayList.size();j++)
            {
                String month = (String) salesArrayList.get(j);
                j++;
                String amount = (String) salesArrayList.get(j);
                j++;
                String currency = (String) salesArrayList.get(j);
                Dataset dataset = null;
                logger.debug("MOnth Amoiunt Currency---->"+month+"---"+amount+"----"+currency);

                if(!labels.contains(month))
                {
                    labels.add(month);
                }

                if(datasetMap.containsKey(currency))
                {
                    Double data[] =dataCurrencyMap.get(currency);

                    String previousAmount=salesDonutCurrencyMap.get(currency);
                    salesDonutCurrencyMap.put(currency, Functions.convert2Decimal(String.valueOf(Double.valueOf(previousAmount) + Double.valueOf(amount))));
                }
                else
                {
                    dataset = new Dataset();

                    backGroundColor.add(colorPallet[colourCount]);

                    dataset.setBackgroundColor(backGroundColor);
                    dataset.setLabel(currency);

                    Double data[] = new Double[labels.size()+1];
                    data[labels.indexOf(month)]=Double.valueOf(amount);
                    logger.debug("labels Size====" + labels.size() + "========index===" + labels.indexOf(month)+"====++="+data.length);

                    datasetMap.put(currency, dataset);
                    dataCurrencyMap.put(currency, data);
                    salesDonutCurrencyMap.put(currency,amount);

                    colourCount++;
                }
            }
            morrisDonutChartVO = new MorrisDonutChartVO();

            List<Dataset> datasets = new ArrayList<Dataset>();
            Dataset datasetDonut = new Dataset();
            Datas morrisData = null;

            List<String> labelss= new ArrayList<String>();
            //List<String> backgroundColor= new ArrayList<String>();
            List<Double> transactionCountperCurrency= new ArrayList<Double>();

            for(Map.Entry<String,Double[]> datasetEntry:dataCurrencyMap.entrySet())
            {
                morrisData = new Datas();
                Double[] data = datasetEntry.getValue();

                String currency = datasetEntry.getKey();
                Dataset dataset = datasetMap.get(currency);

                List<Double> dataList=new ArrayList<Double>();

                labelss.add(dataset.getLabel());
                transactionCountperCurrency.add(Double.valueOf(salesDonutCurrencyMap.get(currency)));

                morrisData.setLabel(dataset.getLabel());
                morrisData.setValue(Double.valueOf(salesDonutCurrencyMap.get(currency)));
                morrisDatasets.add(morrisData);
            }

            morrisDonutChartVO.setElement(element);
            morrisDonutChartVO.setDatas(morrisDatasets);
            morrisDonutChartVO.setColors(backGroundColor);
        }
        else
        {
            Datas morrisData = new Datas();
            List<Datas> morrisDatasets = new ArrayList<Datas>();
            morrisDonutChartVO = new MorrisDonutChartVO();

            morrisData.setLabel("No data to display");
            morrisData.setValue(0.0);
            morrisDatasets.add(morrisData);

            morrisDonutChartVO.setElement("graph");
            morrisDonutChartVO.setDatas(morrisDatasets);
        }


        StringWriter stringWriter = new StringWriter();
        StringWriter stringWriterDonut = new StringWriter();

        String jsonString=null;
        try
        {
            JAXBContext jaxbContextDonut = JAXBContext.newInstance(MorrisDonutChartVO.class);
            Marshaller jaxbMarshallerDonut = jaxbContextDonut.createMarshaller();

            // output Donut
            jaxbMarshallerDonut.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshallerDonut.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
            jaxbMarshallerDonut.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
            jaxbMarshallerDonut.marshal(morrisDonutChartVO, stringWriterDonut);

            jsonString = stringWriter.toString();
        }
        catch (PropertyException e)
        {
            logger.error("Property Exception while converting to json", e);
        }
        catch (JAXBException e)
        {
            logger.error("JAXB Exception while converting to json", e);
        }

        donutString.append(stringWriterDonut.toString());
        createFile(xmlDataFilePath + "/CurrencyData_" + partnerName + "_"+ Functions.getFormattedDate("yyMM")+"_"+charttoken+".json", donutString);
    }

    private String prepareSalesChartData(String charttoken,StringBuilder currencyString, List<String> salesArrayList, String partnerName)
            throws SystemError, SQLException, ParseException
    {
        if(isFileExist(xmlDataFilePath + "/StatusData_" + partnerName + "_"+ Functions.getFormattedDate("yyMM")+ ".json"))
            return null;

        logger.debug("prepare Status Chart Data");
        PartnerFunctions pPartner = new PartnerFunctions();

        String morris = "";
        if(salesArrayList.size()>0)
        {
            Hashtable<String, String> rsData = new Hashtable();
            StringBuffer sb = new StringBuffer();
            //Hashtable hashtable = new Hashtable();
            LinkedHashMap linkedHashMap = new LinkedHashMap();
            Set<String> currencySet = new HashSet<String>();

            List list = null;
            for(int j=0;j<salesArrayList.size();j++)
            {
                String month = (String) salesArrayList.get(j);
                j++;
                String amount = (String) salesArrayList.get(j);
                j++;
                String currency = (String) salesArrayList.get(j);
                Dataset dataset = null;
                logger.debug("MOnth Amoiunt Currency---->"+month+"---"+amount+"----"+currency);

                currencySet.add("'" + currency + "'");

                List newList = (List) linkedHashMap.get(month);
                if (newList == null)
                {
                    newList = new ArrayList();
                }

                newList.add(currency + ":" + amount);
                linkedHashMap.put(month, newList);
            }
            Set set = linkedHashMap.keySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext())
            {
                String month = (String) iterator.next();
                List list1 = (List) linkedHashMap.get(month);
                StringBuffer currencyAmount = new StringBuffer();
                for (Object s : list1)
                {
                    String currencyAmountData = s.toString();
                    currencyAmount.append(currencyAmountData + ",");
                }
                String monthData = "{month:'" + month.trim() + "'," + currencyAmount.toString() + "},\n";
                sb.append(monthData);
            }

            TreeSet<String> sortedCurrency = new TreeSet<String>(currencySet);

            morris = "{\n" +
                    "  element: 'bar-example',\n" +
                    "  stacked: 'true',\n" +
                    "  barColors: [\"#68c39f\", \"#edce8c\", \"#b4c0c0\", \"#e8a451\", \"#e8e85a\", \"#4a525f\", \"#99e6ff\", \"#b399ff\", \"#6fc51b\", \"#ff99e6\", \"#ff9999\", \"#3b76c7\", \"#d689c0\", \"#b93a71\", \"#990000\", \"#54ce28\",\"#68c39f\", \"#edce8c\", \"#4A2CC9\", \"#C92C5C\", \"#b4c0c0\", \"#e5d493\", \"#4a525f\", \"#7bC0AA\", \"#b4c0c0\", \"#e5d493\", \"#4a525f\", \"#7bC0AA\", \"rgba(180,192,192,0.1)\", \"rgba(229,212,147,0.1)\", \"rgba(74,82,95,0.1)\", \"rgba(123,192,170,0.1)\", \"rgba(11,98,164,0.1)\", \"rgba(11,98,164,0.1)\", \"rgba(11,98,164,0.1)\", \"rgba(11,98,164,0.1)\", \"rgba(11,98,164,0.7)\", \"rgba(11,98,164,0.7)\", \"rgba(11,98,164,0.7)\",\"rgba(11,98,164,0.7)\", \"rgba(11,98,164,0.7)\", \"rgba(11,98,164,0.7)\", \"rgba(11,98,164,0.7)\", \"rgba(11,98,164,0.7)\", \"rgba(11,98,164,0.7)\", \"rgba(11,98,164,0.7)\", \"rgba(11,98,164,0.7)\", \"rgba(11,98,164,0.7)\", \"rgba(11,98,164,0.7)\", \"rgba(11,98,164,0.7)\", \"rgba(11,98,164,0.7)\", \"rgba(11,98,164,0.7)\"],\n" +
                    "  data: [" + sb.toString() + "],\n" +
                    "  xkey: 'month',\n" +
                    "  ykeys: " + sortedCurrency + ",\n" +
                    "  labels: " + sortedCurrency + "\n" +
                    "}";
        }
        else
        {
            morris = "{\n" +
                    "  element: 'bar-example',\n" +
                    "  data: [{ y:'No data to display' , a:0.0 }],\n" +
                    "  xkey: 'y',\n" +
                    "  ykeys: ['a'],\n" +
                    "  labels: ['month']\n" +
                    "}";
        }

        StringWriter stringWriter = new StringWriter();

        currencyString.append(stringWriter.toString());
        createFile(xmlDataFilePath + "/CurrencyData_" + partnerName + "_" + Functions.getFormattedDate("yyMM") + "_" + charttoken + ".json", currencyString);

        return morris;
    }

    private void createFile(String fileName, StringBuilder statusChartXml)
    {
        File f = new File(fileName);

        FileWriter fw = null;
        try
        {
            logger.debug(" Creating xml data file " );
            fw = new FileWriter(f);
            fw.write(statusChartXml.toString());
            fw.flush();

        }
        catch (IOException e)
        {
            logger.error(" Error in creating xml data file ",e);
        }
        finally
        {
            if (fw != null)
                try
                {
                    fw.close();
                }
                catch (IOException e)
                {
                    logger.error("Could not close file handler" + Util.getStackTrace(e));
                }
        }
    }


    private boolean isFileExist(String fileName)
    {
        File f = new File(fileName);
        return f.exists();
    }

    private BigDecimal totalSales(String lastYearTime, Connection con, String perCurrency, String partnerName) throws SystemError,SQLException
    {
        String tablename = "";
        String fields = "";
        //PartnerFunctions pPartner = new PartnerFunctions();
        //String sMemberList =pPartner.getPartnerMemberRS(String.valueOf(partnerId));
        String status = "";
        StringBuffer query = new StringBuffer();
        Set<String> gatewayTypeSet = gatewayTransactionTables();
        Iterator i = gatewayTypeSet.iterator();

        fields = " sum(captureamount) as total ";
        status = "('markedforreversal', 'chargeback', 'capturesuccess', 'settled', 'reversed')";

        while(i.hasNext())
        {
            tablename = (String)i.next();
            query.append("select " + fields + " from " + tablename + " where");
            query.append(" status in "+status);
            query.append(" and totype ='"+partnerName+"'");
            query.append(" and dtstamp >='" + lastYearTime + "'");
            query.append(" and currency='"+ perCurrency +"'");
            query.append(" UNION ");
            query.append(" select sum(amount) as total from " + tablename + " where ");
            query.append(" status in ('authsuccessful')");
            query.append(" and totype ='"+partnerName+"'");
            query.append(" and dtstamp >='" + lastYearTime + "' ");
            query.append(" and currency='"+ perCurrency +"'");

            /*if(i.hasNext())
                query.append(" UNION ");*/
        }

        //double amount = 0.0;
        BigDecimal amount = null;
        StringBuffer countquery = null;
        if(query.length()>0)
        {
            countquery = new StringBuffer("select SUM(total) as total from ( " + query + ")as temp ");
            logger.debug("Countquery in TotalSales Chart in PerCurrency-------->"+countquery);
            ResultSet rs = Database.executeQuery(countquery.toString(), con);
            while(rs.next())
            {
                amount = BigDecimal.valueOf(rs.getDouble("total"));
            }
        }
        else
        {
            amount = BigDecimal.valueOf(0.0);
        }

        return amount;
    }

    private List<BigDecimal> totalChargebackCaptureRefund(String lastYearTime, Connection connection, String perCurrency, String partnerName) throws SystemError,SQLException
    {
        String tablename = "";
        String fields = "";
        //PartnerFunctions pPartner = new PartnerFunctions();
        //String sMemberList =pPartner.getPartnerMemberRS(String.valueOf(partnerId));
        StringBuffer query = new StringBuffer();
        Set<String> gatewayTypeSet = gatewayTransactionTables();
        Iterator i = gatewayTypeSet.iterator();
        while(i.hasNext())
        {
            tablename = (String)i.next();
            fields = "sum(captureamount) as capture, sum(refundamount) as refund, sum(chargebackamount) as chargeback";
            query.append("select " + fields + " from " + tablename + " where");
            query.append(" totype ='"+partnerName+"'");
            query.append(" and currency='"+ perCurrency +"'");
            query.append(" and dtstamp >='" + lastYearTime + "'");
            /*if(i.hasNext())
                query.append(" UNION ");*/
        }

        StringBuffer countquery = null;
        BigDecimal captureValue = null;
        BigDecimal chargebackValue = null;
        BigDecimal refundValue = null;

        if(query.length()>0)
        {
            countquery = new StringBuffer("select sum(capture) as capture, sum(refund) as refund, sum(chargeback) as chargeback from ( " + query + ")as temp ");
            logger.debug("Countquery in totalChargebackCaptureRefund in PerCurrency-------->"+countquery);
            BigDecimal refundAmount = null;
            BigDecimal captureAmount = null;
            BigDecimal chargebackAmount = null;

            Map<String, BigDecimal> captureMap = new HashMap<String, BigDecimal>();
            Map<String, BigDecimal> chargebackMap = new HashMap<String,BigDecimal>();
            Map<String, BigDecimal> refundMap = new HashMap<String,BigDecimal>();

            ResultSet rs = Database.executeQuery(countquery.toString(), connection);
            while (rs.next())
            {
                refundAmount = BigDecimal.valueOf(rs.getDouble("refund"));
                captureAmount = BigDecimal.valueOf(rs.getDouble("capture"));
                chargebackAmount = BigDecimal.valueOf(rs.getDouble("chargeback"));

                captureMap.put("capture", captureAmount);
                chargebackMap.put("chargeback", chargebackAmount);
                refundMap.put("refund", refundAmount);
            }

            Map.Entry<String,BigDecimal> captureEntry = captureMap.entrySet().iterator().next();
            Map.Entry<String,BigDecimal> chargebackEntry = chargebackMap.entrySet().iterator().next();
            Map.Entry<String,BigDecimal> refundEntry = refundMap.entrySet().iterator().next();

            //Get Capture
            captureValue = captureEntry.getValue();

            //Get Chargeback
            chargebackValue = chargebackEntry.getValue();

            //Get Refund
            refundValue = refundEntry.getValue();

        }
        else
        {
            captureValue = BigDecimal.valueOf(0.0);
            refundValue = BigDecimal.valueOf(0.0);
            chargebackValue = BigDecimal.valueOf(0.0);
        }
        return Arrays.asList(captureValue,refundValue,chargebackValue);
    }


    private List<BigDecimal> totalAuthSuccessful(String lastYearTime,String perCurrency, Connection connection,String partnerName) throws SystemError,SQLException
    {
        String tablename = "";
        String fields = "";
        StringBuffer query = new StringBuffer();
        Set<String> gatewayTypeSet = gatewayTransactionTables();
        Iterator i = gatewayTypeSet.iterator();
        while(i.hasNext())
        {
            tablename = (String)i.next();
            fields = "sum(amount) as amount";
            query.append("select " + fields + " from " + tablename + " where");
            query.append(" totype ='"+partnerName+"'");
            query.append(" and dtstamp >='" + lastYearTime + "'");
            query.append(" and status ='authsuccessful'");
            query.append(" and currency='"+ perCurrency +"'");

            /*if(i.hasNext())
                query.append(" UNION ");*/
        }

        StringBuffer countquery = null;
        BigDecimal authSuccessfulValue = null;

        if(query.length()>0)
        {
            countquery = new StringBuffer("select sum(amount) as amount from ( " + query + ")as temp ");
            logger.debug("Countquery in totalAuthSuccessful -------->"+countquery);
            BigDecimal authSuccessfulAmount = null;

            Map<String, BigDecimal> authSuccessfulMap = new HashMap<String, BigDecimal>();
            ResultSet rs = Database.executeQuery(countquery.toString(), connection);
            while (rs.next())
            {
                authSuccessfulAmount = BigDecimal.valueOf(rs.getDouble("amount"));

                authSuccessfulMap.put("authAmount", authSuccessfulAmount);
            }

            Map.Entry<String,BigDecimal> authSuccessEntry = authSuccessfulMap.entrySet().iterator().next();

            //Get authSuccessful
            authSuccessfulValue = authSuccessEntry.getValue();


        }
        else
        {
            authSuccessfulValue = BigDecimal.valueOf(0.0);
        }
        return Arrays.asList(authSuccessfulValue);
    }

    //New code added here

    private int progressStatusTotal(String lastYearTime,Connection con, String partnerName) throws SystemError,SQLException
    {
        //PartnerFunctions pPartner = new PartnerFunctions();
        //String sMemberList =pPartner.getPartnerMemberRS(String.valueOf(partnerId));
        StringBuffer query = new StringBuffer();
        String tablename = "";
        String fields = "";
        int count = 0;
        Set<String> gatewayTypeSet = gatewayTransactionTables();

        Iterator i = gatewayTypeSet.iterator();
        while(i.hasNext())
        {
            //tablename = Database.getTableName((String) i.next());
            fields = "count('status') as count";
            query.append("select " + fields + " from " + (String)i.next() + " where");
            query.append(" totype ='"+partnerName+"'");
            query.append(" and dtstamp >='" + lastYearTime + "'");
            /*if(i.hasNext())
                query.append(" UNION ");*/
        }
        if(query.length()>0)
        {
            StringBuffer countquery = new StringBuffer("select SUM(count) as count from ( " + query + ")as temp");
            logger.debug("Countquery in progress Status Total in PerCurrency-------->"+countquery);
            ResultSet rs = Database.executeQuery(countquery.toString(), con);

            while(rs.next())
            {
                count = rs.getInt("count");
            }
        }
        else
        {
            count = 0;
        }

        return count;
    }

    private Hashtable<String,Integer> progressStatusChart(HashMap<String,Integer> statusHashMap) throws SystemError,SQLException
    {
        logger.debug("prepare Status Chart Data");
        //PartnerFunctions pPartner = new PartnerFunctions();
        //String sMemberList =pPartner.getPartnerMemberRS(String.valueOf(partnerId));
        StringBuffer query = new StringBuffer();
        String tablename = "";
        String fields = "";

        Hashtable<String,Integer> statushash = new Hashtable();
        Set<String> gatewayTypeSet = gatewayTransactionTables();

        Iterator i = gatewayTypeSet.iterator();
        /*while(i.hasNext())
        {
            //tablename = Database.getTableName((String)i.next());
            fields = "status,count(*) as count";
            query.append("select " + fields + " from " + (String)i.next() + " where");
            query.append(" toid IN(" + sMemberList + ")");
            query.append(" and dtstamp >='" + lastYearTime + "'");
            query.append(" group by status");
            if(i.hasNext())
                query.append(" UNION ");
        }
        if(query.length()>0)
        {
            StringBuffer countquery = new StringBuffer("select status, SUM(count) as count from ( " + query + ")as temp  group by status ");
            logger.debug("Countquery in Progress Status Chart-------->"+countquery);
            ResultSet rs = Database.executeQuery(countquery.toString(), connection);*/

        logger.debug("StatusHashMap outside------->"+statusHashMap.toString());
        if(statusHashMap.size()>0)
        {

            Set statusSet = statusHashMap.entrySet();
            Iterator statusIterator = statusSet.iterator();
            while (statusIterator.hasNext())
            {
                //dataset = new Data();
                Map.Entry<String, Integer> statusMapEntry = (Map.Entry<String, Integer>) statusIterator.next();

                String status = statusMapEntry.getKey();
                int count = statusMapEntry.getValue();

                statushash.put(status, count);

            }
        }
        else
        {
            statushash.put("",0);
        }


        return statushash;
    }

    public Set<String> gatewayTransactionTables()
    {
        Set<String> gatewayTypeSet = new HashSet();
        gatewayTypeSet.add("transaction_common");
        /*gatewayTypeSet.add("transaction_qwipi");
        gatewayTypeSet.add("transaction_ecore");*/

        return gatewayTypeSet;
    }
}
