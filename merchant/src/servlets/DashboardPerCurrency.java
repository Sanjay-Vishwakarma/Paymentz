import com.directi.pg.*;
import com.logicboxes.util.ApplicationProperties;
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
 * Created by Admin on 3/1/2017.
 */
public class DashboardPerCurrency extends HttpServlet
{
    Logger logger = new Logger(DashboardPerCurrency.class.getName());

    private static String xmlDataFilePath = ApplicationProperties.getProperty("XML_DATA_FILE_PATH");

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException
    {
        doPost(request,response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException,IOException
    {
        logger.debug("Inside DashboardPerCurrency");
        HttpSession session = req.getSession();

        Merchants merchants = new Merchants();
        if (!merchants.isLoggedIn(session))
        {
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        logger.debug("CSRF successful");

        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        Connection conn = null;

        String perCurrency = req.getParameter("currency");

        String sessionColor=(String)session.getAttribute("colorPallet");

        String[] colorPallet=sessionColor.split(",");

        for (String color : colorPallet)
        {
            logger.debug("Session Color Dashboard.java.........."+color);
        }
        TreeMap<String,String> colorMap = (TreeMap<String,String>)session.getAttribute("currencycolorMap");
        String color = colorMap.get(perCurrency);
        ResultSet rs = null;
        ResultSet rs1 = null;
        try
        {
            conn = Database.getConnection();
            int merchantId = Integer.parseInt((String) session.getAttribute("merchantid"));

            StringBuilder chartString = new StringBuilder();
            StringBuilder currencychartString = new StringBuilder();

            try
            {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_YEAR, -365);
                //adjust to 12 months before current month with 0 hrs and min
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1, 0, 0, 0);
                String lastYearTime = String.valueOf(cal.getTimeInMillis() / 1000);
                String charttoken = Functions.getFormattedDate("yyMMddHHmmss");
                session.setAttribute("charttoken", charttoken);

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
                    statusquery.append(" toid ='" + merchantId + "'");
                    statusquery.append(" and dtstamp >='" + lastYearTime + "'");
                    statusquery.append(" group by status");

                    salesquery.append(" select " + field + " from " + tableName + " where");
                    salesquery.append(" toid ='" + merchantId + "'");
                    salesquery.append(" and dtstamp >='" + lastYearTime + "'");
                    salesquery.append(" and currency='"+ perCurrency +"'");
                    salesquery.append(" and status in ('capturesuccess','settled','chargeback','markedforreversal','reversed')");
                    salesquery.append(" group by month,curr");
                    salesquery.append(" UNION");
                    salesquery.append(" select " + fieldz + " from " + tableName + " where");
                    salesquery.append(" toid ='" + merchantId + "'");
                    salesquery.append(" and dtstamp >='" + lastYearTime + "'");
                    salesquery.append(" and currency='"+ perCurrency +"'");
                    salesquery.append(" and status='authsuccessful'");
                    salesquery.append(" group by month,curr");

                   /* if(i.hasNext())
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
               /* int colourCount = 0;

                List<String> backgroundColor= new ArrayList<String>();
                *//*while(rs.next())
                {
                    backgroundColor.add(colorPallet[colourCount]);
                    colourCount++;
                }*//*
                List<String> backgroundColorstatus= new ArrayList<String>();*/
                while(rs.next())
                {
                    statusHashMap.put(rs.getString("status"), rs.getInt("count"));

                    /*backgroundColorstatus.add(colorPallet[colourCount]);
                    colourCount++;*/
                }

                //For Sales & Currency
                StringBuffer salesCountquery = new StringBuffer("select month, SUM(amount) as amount,curr,max(dtstamp) as dt from (" + salesquery + ") as temp  group by month,curr order by dt ");
                logger.debug("Countquery in Sales Chart in doPost-------->"+salesCountquery);
                rs1 = Database.executeQuery(salesCountquery.toString(), conn);



                List<String> salesArrayList = new ArrayList<String>();
                while (rs1.next())
                {
                    salesArrayList.add(rs1.getString("month"));
                    salesArrayList.add(rs1.getString("amount"));
                    salesArrayList.add(rs1.getString("curr"));

                }

                prepareStatusChartData(merchantId, charttoken, chartString,statusHashMap,colorPallet);
                prepareCurrencyChartData(merchantId, charttoken, currencychartString,salesArrayList,color);
                String morris =prepareSalesChartData(merchantId, charttoken, currencychartString,salesArrayList,color);

                BigDecimal salesCount = totalSales(merchantId, lastYearTime, conn, perCurrency);

                List<BigDecimal> totalHashtable = totalChargebackCaptureRefund(lastYearTime, merchantId, conn, perCurrency);
                List<BigDecimal> authSuccessfuleHashtable = totalauthsuccessfulHashtable(lastYearTime, merchantId,perCurrency, conn);

                List<BigDecimal> declinedHashtable = totaldeclinedHashtable(lastYearTime, merchantId,perCurrency, conn);
                req.setAttribute("declined",declinedHashtable);

                List<BigDecimal> setteledHashtable = totalsetteledHashtable(lastYearTime, merchantId,perCurrency, conn);
                req.setAttribute("setteled",setteledHashtable);


                req.setAttribute("authSuccessful",authSuccessfuleHashtable);

                req.setAttribute("totalHashtable",totalHashtable);

                req.setAttribute("morrisDemo",morris);

                req.setAttribute("countsales",salesCount);

                req.setAttribute("saleschart_donuts",currencychartString);
                req.setAttribute("statusChart",chartString);

                int totalHash = progressStatusTotal(merchantId, session, lastYearTime,conn);
                Hashtable statusHash = progressStatusChart(statusHashMap);

                session.setAttribute("colorPallet",colorPallet);

                //New code added here

                req.setAttribute("total",totalHash);
                req.setAttribute("statusHash",statusHash);

                logger.debug("----currencychartString-----"+currencychartString);
                logger.debug("----chartString-----"+chartString);

            }
            catch (SQLException e)
            {
                logger.error("Error in DashBoard SQLException :",e);
            }
            catch (ParseException pe)
            {
                logger.error("Error while Parsing DashBoard :", pe);
            }
            catch (SystemError systemError)
            {
                logger.error("Error in DashBoard SystemError :", systemError);
            }
            RequestDispatcher rd = req.getRequestDispatcher("/dashboardPerCurrency.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req,res);

        }
        catch (SystemError systemError)
        {
            logger.error("Error while getting connection :",systemError);
            out.print(Functions.NewShowConfirmation("Error", "Error while generating report"));
            return;
        }
        finally
        {
            if(conn!=null)
            {
                try
                {
                    conn.close();
                    Database.closeResultSet(rs);
                    Database.closeResultSet(rs1);
                }
                catch (SQLException e)
                {
                    logger.error("Error while closing connection :", e);
                }
            }
        }
    }

    private void prepareStatusChartData(int merchantId,String charttoken,StringBuilder chartString,HashMap<String,Integer> statusHashMap,String[] colorPallet)
            throws SystemError, SQLException, ParseException
    {

        logger.debug("path---" + xmlDataFilePath + "/StatusData_" + merchantId + "_" + Functions.getFormattedDate("yyMM")+ ".json");
        if(isFileExist(xmlDataFilePath + "/StatusData_" + merchantId + "_"+ Functions.getFormattedDate("yyMM")+ ".json"))
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
            List<String> backgroundColor = new ArrayList<String>();
            int colourCount =0;
            while(statusIterator.hasNext())
            {
                dataset = new Datas();
                Map.Entry<String,Integer> statusMapEntry = (Map.Entry<String, Integer>) statusIterator.next();

                status = statusMapEntry.getKey();
                count = statusMapEntry.getValue();

                dataset.setValue(count);
                dataset.setLabel(status);
                datasetses.add(dataset);

                //Setting background color
                if(colourCount < colorPallet.length)
                    backgroundColor.add(colorPallet[colourCount]);
                else
                    backgroundColor.add(colorPallet[colourCount % colorPallet.length]);

                colourCount++;

            }
            logger.debug("Status of chart"+status);

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

        createFile(xmlDataFilePath + "/StatusData_" + merchantId + "_" + Functions.getFormattedDate("yyMM") + "_" + charttoken + ".json", chartString);
    }

    private void prepareCurrencyChartData(int merchantId,String charttoken,StringBuilder donutString, List<String> salesArrayList,String colorPallet)
            throws SystemError, SQLException, ParseException
    {
        logger.debug("path---" + xmlDataFilePath + "/CurrencyData_" + merchantId + "_" + Functions.getFormattedDate("yyMM")+ ".json");
        if(isFileExist(xmlDataFilePath + "/CurrencyData_" + merchantId + "_"+ Functions.getFormattedDate("yyMM")+ ".json"))
            return;

        logger.debug("prepare Sales Chart Data");
        //PartnerFunctions pPartner = new PartnerFunctions();


        logger.debug("SalesArrayList----->"+salesArrayList.size());




        MorrisDonutChartVO morrisDonutChartVO = null;
        if(salesArrayList.size()>0)
        {
            StringBuilder statusChartXml = new StringBuilder("<graph xaxisname='Months' yaxisname='Total Amount' caption='Monthwise sales for last 1 year' lineThickness='1' animation='1' showNames='1' alpha='100' showLimits='1' decimalPrecision='1' rotateNames='1' numDivLines='3' limitsDecimalPrecision='0' showValues='0'>");

            //BarChart
            BarChartVO barChartVO = new BarChartVO();          /// Add VO
            List<Dataset> datasetses = new ArrayList<Dataset>();
            List<Datas> morrisDatasets = new ArrayList<Datas>();

            List<String> labels= new ArrayList<String>();

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
                //System.out.println("currency selected...."+currency);
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



                    dataset.setLabel(currency);

                    Double data[] = new Double[12];
                    data[labels.indexOf(month)]=Double.valueOf(amount);

                    datasetMap.put(currency, dataset);
                    dataCurrencyMap.put(currency, data);
                    salesDonutCurrencyMap.put(currency,amount);


                }
            }
            morrisDonutChartVO = new MorrisDonutChartVO();

            List<Dataset> datasets = new ArrayList<Dataset>();
            Dataset datasetDonut = new Dataset();
            Datas morrisData = null;

            List<String> labelss= new ArrayList<String>();
            List<Double> transactionCountperCurrency= new ArrayList<Double>();

            List<String> backGroundColor = new ArrayList<String>();

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

                backGroundColor.add(colorPallet);


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
        createFile(xmlDataFilePath + "/CurrencyData_" + merchantId + "_"+ Functions.getFormattedDate("yyMM")+"_"+charttoken+".json", donutString);
    }

    private String prepareSalesChartData(int partnerId,String charttoken,StringBuilder currencyString, List<String> salesArrayList,String ColorPallet)
            throws SystemError, SQLException, ParseException
    {
        if(isFileExist(xmlDataFilePath + "/StatusData_" + partnerId + "_"+ Functions.getFormattedDate("yyMM")+ ".json"))
            return null;

        logger.debug("prepare Status Chart Data");
        //PartnerFunctions pPartner = new PartnerFunctions();



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
                    "  barColors: [\"" + ColorPallet +"\"],\n" +
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
        createFile(xmlDataFilePath + "/CurrencyData_" + partnerId + "_" + Functions.getFormattedDate("yyMM") + "_" + charttoken + ".json", currencyString);

        return morris;
    }

    private List<BigDecimal> totaldeclinedHashtable(String lastYearTime, int merchantId,String perCurrency, Connection connection) throws SystemError,SQLException
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
            query.append(" toid ='" + merchantId + "'");
            query.append(" and dtstamp >='" + lastYearTime + "'");
            query.append(" and currency='"+ perCurrency +"'");
            query.append(" and status in ('authfailed','failed')");
            //query.append(" and status ='failed'");
            /*if(i.hasNext())
                query.append(" UNION ");*/
        }

        StringBuffer countquery = null;

        BigDecimal declinedlValue = null;

        if(query.length()>0)
        {
            countquery = new StringBuffer("select sum(amount) as amount from ( " + query + ")as temp ");
            logger.debug("Countquery in totalDeclined -------->"+countquery);

            BigDecimal declined = null;

            Map<String, BigDecimal> authMap = new HashMap<String, BigDecimal>();
           /* Map<String, BigDecimal> chargebackMap = new HashMap<String,BigDecimal>();
            Map<String, BigDecimal> refundMap = new HashMap<String,BigDecimal>();
*/
            ResultSet rs = Database.executeQuery(countquery.toString(), connection);
            while (rs.next())
            {
                declined = BigDecimal.valueOf(rs.getDouble("amount"));

                authMap.put("declined", declined);
                //authMap.put("failed", declined);
            }

            Map.Entry<String,BigDecimal> declinedlValueEntry = authMap.entrySet().iterator().next();

            //Get Capture
            declinedlValue = (declinedlValueEntry.getValue());


        }
        else
        {
            declinedlValue = BigDecimal.valueOf(0.0);
        }
        return Arrays.asList(declinedlValue);
    }

    private List<BigDecimal> totalsetteledHashtable(String lastYearTime, int merchantId,String perCurrency, Connection connection) throws SystemError,SQLException
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
            query.append(" toid ='" + merchantId + "'");
            query.append(" and dtstamp >='" + lastYearTime + "'");
            query.append(" and currency='"+ perCurrency +"'");
            query.append(" and status ='settled'");
           /* if(i.hasNext())
                query.append(" UNION ");*/
        }

        StringBuffer countquery = null;

        BigDecimal setteledValue = null;

        if(query.length()>0)
        {
            countquery = new StringBuffer("select sum(amount) as amount from ( " + query + ")as temp ");
            logger.debug("Countquery in totalSetteled -------->"+countquery);

            BigDecimal declined = null;

            Map<String, BigDecimal> authMap = new HashMap<String, BigDecimal>();
           /* Map<String, BigDecimal> chargebackMap = new HashMap<String,BigDecimal>();
            Map<String, BigDecimal> refundMap = new HashMap<String,BigDecimal>();
*/
            ResultSet rs = Database.executeQuery(countquery.toString(), connection);
            while (rs.next())
            {
                declined = BigDecimal.valueOf(rs.getDouble("amount"));

                authMap.put("declined", declined);
                //authMap.put("failed", declined);
            }

            Map.Entry<String,BigDecimal> setteledValueEntry = authMap.entrySet().iterator().next();

            //Get Capture
            setteledValue = (setteledValueEntry.getValue());


        }
        else
        {
            setteledValue = BigDecimal.valueOf(0.0);
        }
        return Arrays.asList(setteledValue);
    }

    private BigDecimal totalSales(int merchantId, String lastYearTime, Connection con, String perCurrency) throws SystemError,SQLException
    {
        String tablename = "";
        String fields = "";
        int salesCount = 0;
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
            query.append(" and toid ='" + merchantId + "'");
            query.append(" and dtstamp >='" + lastYearTime + "'");
            query.append(" and currency='"+ perCurrency +"'");
            query.append(" UNION ");
            query.append(" select sum(amount) as total from " + tablename + " where ");
            query.append(" status in ('authsuccessful')");
            query.append(" and toid ='" + merchantId + "'");
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
            logger.debug("Countquery in TotalSales Chart-------->"+countquery);
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

    private List<BigDecimal> totalChargebackCaptureRefund(String lastYearTime, int merchantId, Connection connection, String perCurrency) throws SystemError,SQLException
    {
        String tablename = "";
        String fields = "";
        StringBuffer query = new StringBuffer();
        Set<String> gatewayTypeSet = gatewayTransactionTables();
        Iterator i = gatewayTypeSet.iterator();
        while(i.hasNext())
        {
            tablename = (String)i.next();
            fields = "sum(captureamount) as capture, sum(refundamount) as refund, sum(chargebackamount) as chargeback";
            query.append("select " + fields + " from " + tablename + " where");
            query.append(" toid ='" + merchantId + "'");
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
            logger.debug("Countquery in totalChargebackCaptureRefund -------->"+countquery);

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
            captureValue = (captureEntry.getValue());

            //Get Chargeback
            chargebackValue = (chargebackEntry.getValue());

            //Get Refund
            refundValue = (refundEntry.getValue());

        }
        else
        {
            captureValue = BigDecimal.valueOf(0.0);
            refundValue = BigDecimal.valueOf(0.0);
            chargebackValue = BigDecimal.valueOf(0.0);
        }
        return Arrays.asList(captureValue,refundValue,chargebackValue);
    }

    private int progressStatusTotal(int merchantId, HttpSession session,String lastYearTime,Connection con) throws SystemError,SQLException
    {
        String tablename = "";
        String fields = "";
        int count = 0;
        StringBuffer query = new StringBuffer();
        Set<String> gatewayTypeSet = gatewayTransactionTables();
        Iterator i = gatewayTypeSet.iterator();
        while(i.hasNext())
        {
            tablename = Database.getTableName((String) i.next());
            fields = "count('status') as count";
            query.append("select " + fields + " from " + tablename + " where");
            query.append(" toid ='" + merchantId + "'");
            query.append(" and dtstamp >='" + lastYearTime + "'");
            /*if(i.hasNext())
                query.append(" UNION ");*/
        }
        if(query.length()>0)
        {
            StringBuffer countquery = new StringBuffer("select SUM(count) as count from ( " + query + ")as temp");

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
    private List<BigDecimal> totalauthsuccessfulHashtable(String lastYearTime, int merchantId,String perCurrency, Connection connection) throws SystemError,SQLException
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
            query.append(" toid ='" + merchantId + "'");
            query.append(" and dtstamp >='" + lastYearTime + "'");
            query.append(" and currency='"+ perCurrency +"'");
            query.append(" and status ='authsuccessful'");
            /*if(i.hasNext())
                query.append(" UNION ");*/
        }

        StringBuffer countquery = null;

        BigDecimal authSuccessfulValue = null;

        if(query.length()>0)
        {
            countquery = new StringBuffer("select sum(amount) as amount from ( " + query + ")as temp ");
            logger.debug("Countquery in totalauthsuccessful -------->"+countquery);

            BigDecimal authSuccessful = null;

            Map<String, BigDecimal> authMap = new HashMap<String, BigDecimal>();
           /* Map<String, BigDecimal> chargebackMap = new HashMap<String,BigDecimal>();
            Map<String, BigDecimal> refundMap = new HashMap<String,BigDecimal>();
*/
            ResultSet rs = Database.executeQuery(countquery.toString(), connection);
            while (rs.next())
            {
                authSuccessful = BigDecimal.valueOf(rs.getDouble("amount"));

                authMap.put("authSuccessful", authSuccessful);
            }

            Map.Entry<String,BigDecimal> authSuccessfulEntry = authMap.entrySet().iterator().next();

            //Get Capture
            authSuccessfulValue = (authSuccessfulEntry.getValue());

        }
        else
        {
            authSuccessfulValue = BigDecimal.valueOf(0.0);
        }
        return Arrays.asList(authSuccessfulValue);
    }

    private Hashtable<String,Integer> progressStatusChart(HashMap<String,Integer> statusHashMap) throws SystemError,SQLException
    {
        logger.debug("prepare Status Chart Data");

        Hashtable<String,Integer> statushash = new Hashtable();

        if(statusHashMap.size()>0)
        {

            Set statusSet = statusHashMap.entrySet();
            Iterator statusIterator = statusSet.iterator();
            while (statusIterator.hasNext())
            {
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
                    logger.error("Could not close file handler" ,e);
                }
        }
    }

    private boolean isFileExist(String fileName)
    {
        File f = new File(fileName);
        return f.exists();
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
