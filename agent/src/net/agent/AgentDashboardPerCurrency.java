package net.agent;
import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.logicboxes.util.ApplicationProperties;
import com.logicboxes.util.Util;
import com.manager.vo.chartVOs.donutchart.Datasets;
import com.manager.vo.morrisBarVOs.Data;
import com.manager.vo.morrisBarVOs.MorrisBarChartVO;
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
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: saurabh	
 * Date: 2/20/14
 * Time: 3:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class AgentDashboardPerCurrency extends HttpServlet
{
    private static Logger logger = new Logger(AgentDashboard.class.getName());

    private static String xmlDataFilePath = ApplicationProperties.getProperty("AGENT_XML_DATA_FILE_PATH");
    //private static String[] colorPallet = new String[]{"AFD8F8", "F6BD0F", "8BBA00", "FF8E46", "008E8E", "D64646", "8E468E", "588526", "B3AA00", "008ED6", "9D080D", "A186BE", "78F04A","AFD8F8", "F6BD0F", "8BBA00", "FF8E46", "008E8E", "D64646", "8E468E", "588526", "B3AA00", "008ED6", "9D080D", "A186BE", "78F04A","AFD8F8", "F6BD0F", "8BBA00", "FF8E46", "008E8E", "D64646", "8E468E", "588526", "B3AA00", "008ED6", "9D080D", "A186BE", "78F04A","AFD8F8", "F6BD0F", "8BBA00", "FF8E46", "008E8E", "D64646", "8E468E", "588526", "B3AA00", "008ED6", "9D080D", "A186BE", "78F04A","AFD8F8", "F6BD0F", "8BBA00", "FF8E46", "008E8E", "D64646", "8E468E", "588526", "B3AA00", "008ED6", "9D080D", "A186BE", "78F04A","AFD8F8", "F6BD0F", "8BBA00", "FF8E46", "008E8E", "D64646", "8E468E", "588526", "B3AA00", "008ED6", "9D080D", "A186BE", "78F04A"};
    private static String[] colorPallet = new String[] {"#68c39f", "#edce8c", "#b4c0c0", "#e8a451","#e8e85a", "#4a525f", "#99e6ff", "#b399ff", "#6fc51b", "#ff99e6", "#ff9999", "#3b76c7", "#d689c0", "#b93a71", "#990000", "#54ce28", "#68c39f", "#edce8c", "#4A2CC9", "#C92C5C"};

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        AgentFunctions agent=new AgentFunctions();
        if (!agent.isLoggedInAgent(session))
        {
            res.sendRedirect("/agent/logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        String perCurrency = req.getParameter("currency");
        //System.out.println("CurrencyParameterPerCurrency--------->"+perCurrency);

        Connection conn = null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            int agentId = Integer.parseInt((String) session.getAttribute("merchantid"));

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -365);
            //adjust to 6 months before current month with 0 hrs and min
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1, 0, 0, 0);
            String lastYearTime = String.valueOf(cal.getTimeInMillis() / 1000);

            String charttoken = Functions.getFormattedDate("yyMMddHHmmss");
            session.setAttribute("charttoken", charttoken);

            StringBuffer statusChartString = new StringBuffer();
            StringBuffer salesChartString = new StringBuffer();
            StringBuffer refundChartString = new StringBuffer();
            StringBuffer chargeChartString = new StringBuffer();
            StringBuffer fraudChartString = new StringBuffer();

            prepareStatusChartData(agentId, lastYearTime, conn, session, statusChartString,perCurrency);
            prepareSalesChartData(agentId, lastYearTime, conn, session, salesChartString,perCurrency);
            prepareRefundChartData(agentId, lastYearTime, conn, session, refundChartString,perCurrency);
            prepareChargeBackChartData(agentId, lastYearTime, conn, session, chargeChartString,perCurrency);
            prepareFraudChartData(agentId, lastYearTime, conn, session,fraudChartString,perCurrency);

            req.setAttribute("statusChart",statusChartString);
            req.setAttribute("salesChart",salesChartString);
            req.setAttribute("refundChart",refundChartString);
            req.setAttribute("chargebackChart",chargeChartString);
            req.setAttribute("fraudChart",fraudChartString);

            BigDecimal salesCount = totalSales(agentId, lastYearTime, conn,perCurrency);

            List<BigDecimal> totalHashtable = totalChargebackCaptureRefund(agentId, lastYearTime, conn,perCurrency);

            req.setAttribute("totalHashtable",totalHashtable);
            req.setAttribute("countsales",salesCount);

            /*List<String> currencyList = perCurrency(agentId,lastYearTime,conn);
            System.out.println("CurrencyList Here--------->"+currencyList);
            session.setAttribute("currencyList",currencyList);*/

            RequestDispatcher rd = req.getRequestDispatcher("/agentDashboardPerCurrency.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (SystemError systemError)
        {
            logger.error("Error while getting connection :" + Util.getStackTrace(systemError));
            out.print(Functions.NewShowConfirmation1("Error", "Error while generating report"));
            return;
        }
        catch (SQLException e)
        {
            logger.error("SQLException In AgentDashboardPerCurrency ",e);
        }
        finally
        {
            if (conn != null)
            {
                try
                {
                    conn.close();
                }
                catch (SQLException e)
                {
                    logger.error("Error while clossing connection :" + e.getMessage());
                }
            }
        }
    }


    private void prepareStatusChartData(int agentId, String lastYearTime, Connection conn, HttpSession session,StringBuffer chartString,String perCurrency) throws SystemError, SQLException
    {
        logger.debug("prepare Status Chart Data for agent");

        AgentFunctions pAgent = new AgentFunctions();

        Functions functions = new Functions();
        String sMemberList = null;
        sMemberList = pAgent.getAgentMemberList(String.valueOf(agentId));
        if(!functions.isValueNull(sMemberList))
        {
            sMemberList = "10000"; // This is to get rid of sql error. Random number 10000
        }
        StringBuffer countquery=null;

        StringBuffer query = new StringBuffer();

        String tablename = "";
        String fields = "";
        Set<String> gatewayTypeSet = gatewayTransactionTables();
        Iterator i = gatewayTypeSet.iterator();
        while(i.hasNext())
        {
            tablename = (String)i.next();
            fields = "status,count(*) as count";
            query.append("select " + fields + " from " + tablename + " where");
            query.append(" toid IN(" + sMemberList + ")");
            query.append(" and dtstamp >='" + lastYearTime + "'");
            query.append(" and currency='"+ perCurrency +"'");
            query.append(" group by status ");
            if(i.hasNext())
            {
                query.append(" UNION ");
            }
        }
        logger.debug("===query=="+query.toString());

        countquery = new StringBuffer("select status, SUM(count) as count from ( " + query + ")as temp  group by status order by count");
        logger.debug("===countquery=="+countquery.toString());

        String temp = countquery.toString();

        ResultSet rs = Database.executeQuery(temp, conn);

        MorrisDonutChartVO morrisDonut = new MorrisDonutChartVO();
        List<Datas> datasetses = new ArrayList<Datas>();

        int colourCount = 0;

        List<String> backgroundColor= new ArrayList<String>();

        Datas dataset = null;
        String status = "";
        double count = 0.0;
        String element="donut-example";

        while (rs.next())
        {
            dataset = new Datas();
            status = rs.getString("status");
            count = rs.getDouble("count");

            dataset.setValue(count);
            dataset.setLabel(status);
            datasetses.add(dataset);

            backgroundColor.add(colorPallet[colourCount]);
            colourCount++;
        }

        if(datasetses.size()>0)
        {
            morrisDonut.setDatas(datasetses);
            morrisDonut.setElement(element);
            morrisDonut.setColors(backgroundColor);
        }
        else
        {
            morrisDonut = new MorrisDonutChartVO();

            dataset = new Datas();

            dataset.setValue(0.0);
            dataset.setLabel("No data to display");
            datasetses.add(dataset);

            morrisDonut.setDatas(datasetses);
            morrisDonut.setElement(element);
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
            jaxbMarshaller.marshal(morrisDonut, stringWriter);
            logger.debug("XMLtoJSON BEFORE in status:::" + stringWriter.toString());
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

        //String filename = xmlDataFilePath + "/StatusData_" + agentId + "_"+ charttoken + ".xml";
        //createFile(filename, statusChartXml);
    }

    private void prepareSalesChartData(int agentId, String lastYearTime, Connection conn, HttpSession session,StringBuffer chartString,String perCurrency) throws SystemError, SQLException
    {
        logger.debug("prepare Sales Chart Data");
        AgentFunctions pAgent = new AgentFunctions();
        Functions functions = new Functions();
        String sMemberList = null;
        sMemberList = pAgent.getAgentMemberList(String.valueOf(agentId));
        if(!functions.isValueNull(sMemberList))
        {
            sMemberList = "10000"; // This is to get rid of sql error. Random number 10000
        }
        StringBuffer countquery=null;
        StringBuffer query = new StringBuffer();
        String tablename = "";
        String fields = "";
        Set<String> gatewayTypeSet = gatewayTransactionTables();
        Iterator i = gatewayTypeSet.iterator();
        while(i.hasNext())
        {
            tablename = (String)i.next();

            fields = "date_format(from_unixtime(dtstamp),'%b-%y') as month,sum(captureamount) as amount,max(dtstamp) as dtstamp ";
            query.append("select " + fields + " from " + tablename + " where");
            query.append(" toid IN(" + sMemberList + ")");
            query.append(" and dtstamp >='" + lastYearTime + "'");
            query.append(" and currency='"+ perCurrency +"'");
            query.append(" and status IN ('capturesuccess','settled') "); //('capturesuccess','settled','chargeback','reversed')
            query.append(" group by month ");
            query.append(" UNION ");
            query.append(" select date_format(from_unixtime(dtstamp),'%b-%y') as month,sum(amount) as amount,max(dtstamp) as dtstamp from " + tablename + " where");
            query.append(" toid IN(" + sMemberList + ")");
            query.append(" and dtstamp >='" + lastYearTime + "'");
            query.append(" and currency='"+ perCurrency +"'");
            query.append(" and status IN ('authsuccessful') ");
            query.append(" group by month ");
            if(i.hasNext())
                query.append(" UNION ");
        }
        countquery = new StringBuffer("select month, SUM(amount) as amount,max(dtstamp) as dt from ( " + query + ")as temp group by month order by dt");
        logger.debug(countquery);

        String temp = countquery.toString();
        ResultSet rs = Database.executeQuery(temp, conn);

        MorrisBarChartVO morrisBarChartVO = new MorrisBarChartVO();
        List<Data> datasets = new ArrayList<Data>();
        Data data = null;

        List<String> backgroundColor= new ArrayList<String>();
        String month = "";
        double amount = 0.0;
        String element = "sales";
        String[] arrayAmount = {"amount"};

        int colourCount = 0;
        while (rs.next())
        {
            data = new Data();
            month = rs.getString("month");
            amount = rs.getDouble("amount");

            data.setAmount(amount);
            data.setMonth(month);
            datasets.add(data);

            backgroundColor.add("#abb7b7");
            //colourCount++;
        }

        if(datasets.size()>0)
        {
            morrisBarChartVO.setElement(element);
            morrisBarChartVO.setData(datasets);
            morrisBarChartVO.setXkey("month");
            morrisBarChartVO.setYkeys(arrayAmount);
            morrisBarChartVO.setLabels(arrayAmount);
            morrisBarChartVO.setBarColors(backgroundColor);
        }
        else
        {
            //System.out.println("Inside else condition");

            morrisBarChartVO = new MorrisBarChartVO();

            data = new Data();
            data.setMonth("No data to display");
            data.setAmount(0.0);
            datasets.add(data);

            morrisBarChartVO.setElement(element);
            morrisBarChartVO.setData(datasets);
            morrisBarChartVO.setXkey("month");
            morrisBarChartVO.setYkeys(arrayAmount);
            morrisBarChartVO.setLabels(arrayAmount);
        }

        StringWriter stringWriter = new StringWriter();
        String jsonString = null;
        try
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(MorrisBarChartVO.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
            jaxbMarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
            jaxbMarshaller.marshal(morrisBarChartVO, stringWriter);
            logger.debug("XMLtoJSON BEFORE in sales:::" + stringWriter.toString());
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
        //String filename = xmlDataFilePath + "/SalesData_" + agentId + "_"+ charttoken + ".xml";
        //createFile(filename, statusChartXml);
    }

    private void prepareRefundChartData(int agentId, String lastYearTime, Connection conn, HttpSession session,StringBuffer chartString,String perCurrency) throws SystemError, SQLException
    {
        logger.debug("prepare refund Chart Data");
        AgentFunctions pAgent = new AgentFunctions();
        Functions functions = new Functions();
        String sMemberList = null;
        sMemberList = pAgent.getAgentMemberList(String.valueOf(agentId));
        if(!functions.isValueNull(sMemberList))
        {
            sMemberList = "10000"; // This is to get rid of sql error. Random number 10000
        }
        StringBuffer countquery=null;
        StringBuffer query = new StringBuffer();
        String tablename = "";
        String fields = "";
        Set<String> gatewayTypeSet = gatewayTransactionTables();
        Iterator i = gatewayTypeSet.iterator();
        while(i.hasNext())
        {
            tablename = (String)i.next();

            fields = "date_format(from_unixtime(dtstamp),'%b-%y') as month,sum(refundamount) as amount,max(dtstamp) as dtstamp ";
            query.append("select " + fields + " from " + tablename + " where");
            query.append(" toid IN(" + sMemberList + ")");
            query.append(" and dtstamp >='" + lastYearTime + "'");
            query.append(" and currency='"+ perCurrency +"'");
            query.append(" and status = 'reversed'");
            query.append(" group by month ");
            if(i.hasNext())
                query.append(" UNION ");
        }
        countquery = new StringBuffer("select month, SUM(amount) as amount,max(dtstamp) as dt from ( " + query + ")as temp  group by month order by dt");
        String temp = countquery.toString();
        logger.debug(countquery);
        ResultSet rs = Database.executeQuery(temp, conn);

        MorrisBarChartVO morrisBarChartVO = new MorrisBarChartVO();
        List<Data> datasets = new ArrayList<Data>();
        Data data = null;

        List<String> backgroundColor= new ArrayList<String>();
        String month = "";
        double amount = 0.0;
        String element = "refund";
        String[] arrayAmount = {"amount"};

        int colourCount = 0;
        while (rs.next())
        {
            data = new Data();
            month = rs.getString("month");
            amount = rs.getDouble("amount");

            data.setAmount(amount);
            data.setMonth(month);
            datasets.add(data);

            backgroundColor.add("#68c39f");
            //colourCount++;
        }

        if(datasets.size()>0)
        {
            morrisBarChartVO.setElement(element);
            morrisBarChartVO.setData(datasets);
            morrisBarChartVO.setXkey("month");
            morrisBarChartVO.setYkeys(arrayAmount);
            morrisBarChartVO.setLabels(arrayAmount);
            morrisBarChartVO.setBarColors(backgroundColor);
        }
        else
        {
            //System.out.println("Inside else condition");

            morrisBarChartVO = new MorrisBarChartVO();

            data = new Data();
            data.setMonth("No data to display");
            data.setAmount(0.0);
            datasets.add(data);

            morrisBarChartVO.setElement(element);
            morrisBarChartVO.setData(datasets);
            morrisBarChartVO.setXkey("month");
            morrisBarChartVO.setYkeys(arrayAmount);
            morrisBarChartVO.setLabels(arrayAmount);
        }


        StringWriter stringWriter = new StringWriter();
        String jsonString = null;
        try
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(MorrisBarChartVO.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
            jaxbMarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
            jaxbMarshaller.marshal(morrisBarChartVO, stringWriter);
            logger.debug("XMLtoJSON BEFORE in refund:::" + stringWriter.toString());
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
        //String filename = xmlDataFilePath + "/RefundData_" + agentId + "_"+ charttoken + ".xml";
        //createFile(filename, statusChartXml);
    }

    private void prepareChargeBackChartData(int agentId, String lastYearTime, Connection conn, HttpSession session,StringBuffer chartString,String perCurrency) throws SystemError, SQLException
    {
        logger.debug("prepare Chargeback Chart Data");
        AgentFunctions pAgent = new AgentFunctions();
        Functions functions = new Functions();
        String sMemberList = null;
        sMemberList = pAgent.getAgentMemberList(String.valueOf(agentId));
        if(!functions.isValueNull(sMemberList))
        {
            sMemberList = "10000"; // This is to get rid of sql error. Random number 10000
        }
        StringBuffer countquery=null;
        StringBuffer query = new StringBuffer();
        String tablename = "";
        String fields = "";

        Set<String> gatewayTypeSet = gatewayTransactionTables();
        Iterator i = gatewayTypeSet.iterator();;

        while(i.hasNext())
        {
            tablename = (String)i.next();

            fields = "date_format(from_unixtime(dtstamp),'%b-%y') as month,sum(chargebackamount) as amount,max(dtstamp) as dtstamp";
            query.append("select " + fields + " from " + tablename + " where");
            query.append(" toid IN(" + sMemberList + ")");
            query.append(" and dtstamp >='" + lastYearTime + "'");
            query.append(" and currency='"+ perCurrency +"'");
            query.append(" and status ='chargeback'");
            query.append(" group by month ");
            if(i.hasNext())
                query.append(" UNION ");
        }
        countquery = new StringBuffer("select month, SUM(amount) as amount,max(dtstamp) as dt from ( " + query + ")as temp  group by month order by dt");

        String temp = countquery.toString();
        logger.debug(countquery);
        ResultSet rs = Database.executeQuery(temp, conn);

        MorrisBarChartVO morrisBarChartVO = new MorrisBarChartVO();
        List<Data> datasets = new ArrayList<Data>();
        Data data = null;

        List<String> backgroundColor= new ArrayList<String>();
        String month = "";
        double amount = 0.0;
        String element = "chargeback";
        String[] arrayAmount = {"amount"};

        int colourCount = 0;
        while (rs.next())
        {
            data = new Data();
            month = rs.getString("month");
            amount = rs.getDouble("amount");

            data.setAmount(amount);
            data.setMonth(month);
            datasets.add(data);

            backgroundColor.add("#edce8c");
            //colourCount++;
        }

        if(datasets.size()>0)
        {
            morrisBarChartVO.setElement(element);
            morrisBarChartVO.setData(datasets);
            morrisBarChartVO.setXkey("month");
            morrisBarChartVO.setYkeys(arrayAmount);
            morrisBarChartVO.setLabels(arrayAmount);
            morrisBarChartVO.setBarColors(backgroundColor);
        }
        else
        {
            //System.out.println("Inside else condition");

            morrisBarChartVO = new MorrisBarChartVO();

            data = new Data();
            data.setMonth("No data to display");
            data.setAmount(0.0);
            datasets.add(data);

            morrisBarChartVO.setElement(element);
            morrisBarChartVO.setData(datasets);
            morrisBarChartVO.setXkey("month");
            morrisBarChartVO.setYkeys(arrayAmount);
            morrisBarChartVO.setLabels(arrayAmount);
        }


        StringWriter stringWriter = new StringWriter();
        String jsonString = null;
        try
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(MorrisBarChartVO.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
            jaxbMarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
            jaxbMarshaller.marshal(morrisBarChartVO, stringWriter);
            logger.debug("XMLtoJSON BEFORE in chargeback:::" + stringWriter.toString());
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
        //createFile(filename, statusChartXml);
    }

    private void prepareFraudChartData(int agentId, String lastYearTime, Connection conn, HttpSession session,StringBuffer chartString,String perCurrency) throws SystemError, SQLException
    {
        logger.debug("prepare fraud Chart Data");
        AgentFunctions pAgent = new AgentFunctions();
        Functions functions = new Functions();
        String sMemberList = null;
        sMemberList = pAgent.getAgentMemberList(String.valueOf(agentId));
        if(!functions.isValueNull(sMemberList))
        {
            sMemberList = "10000"; // This is to get rid of sql error. Random number 10000
        }
        StringBuffer countquery=null;
        StringBuffer query = new StringBuffer();
        String tablename = "";
        String fields = "";
        Set<String> gatewayTypeSet = gatewayTransactionTables();
        Iterator i = gatewayTypeSet.iterator();
        while(i.hasNext())
        {
            tablename = (String)i.next();

            fields = "date_format(from_unixtime(dtstamp),'%b-%y') as month,sum(captureamount) as amount,max(dtstamp) as dtstamp ";
            query.append("select " + fields + " from " + tablename + " where");
            query.append(" toid IN(" + sMemberList + ")");
            query.append(" and dtstamp >='" + lastYearTime + "'");
            query.append(" and currency='"+ perCurrency +"'");
            query.append(" and status IN ('capturesuccess','settled','reversed')");
            query.append(" group by month ");
            query.append(" UNION ");
            query.append(" select date_format(from_unixtime(dtstamp),'%b-%y') as month,sum(chargebackamount) as amount,max(dtstamp) as dtstamp from " + tablename + " where");
            query.append(" toid IN(" + sMemberList + ")");
            query.append(" and dtstamp >='" + lastYearTime + "'");
            query.append(" and currency='"+ perCurrency +"'");
            query.append(" and status IN ('chargeback') ");
            query.append(" group by month ");
            if(i.hasNext())
                query.append(" UNION ");
        }

        countquery = new StringBuffer("select month, SUM(amount) as amount,sum(dtstamp) as dt  from ( " + query + ")as temp  group by month order by dt");
        String temp = countquery.toString();
        ResultSet rs = Database.executeQuery(temp, conn);

        MorrisBarChartVO morrisBarChartVO = new MorrisBarChartVO();
        List<Data> datasets = new ArrayList<Data>();
        Data data = null;

        List<String> backgroundColor= new ArrayList<String>();
        String month = "";
        double amount = 0.0;
        String element = "fraud";
        String[] arrayAmount = {"amount"};

        int colourCount = 0;
        while (rs.next())
        {
            data = new Data();
            month = rs.getString("month");
            amount = rs.getDouble("amount");

            data.setAmount(amount);
            data.setMonth(month);
            datasets.add(data);

            backgroundColor.add("#4a525f");
            //colourCount++;
        }

        if(datasets.size()>0)
        {
            morrisBarChartVO.setElement(element);
            morrisBarChartVO.setData(datasets);
            morrisBarChartVO.setXkey("month");
            morrisBarChartVO.setYkeys(arrayAmount);
            morrisBarChartVO.setLabels(arrayAmount);
            morrisBarChartVO.setBarColors(backgroundColor);
        }
        else
        {
            //System.out.println("Inside else condition");

            morrisBarChartVO = new MorrisBarChartVO();

            data = new Data();
            data.setMonth("No data to display");
            data.setAmount(0.0);
            datasets.add(data);

            morrisBarChartVO.setElement(element);
            morrisBarChartVO.setData(datasets);
            morrisBarChartVO.setXkey("month");
            morrisBarChartVO.setYkeys(arrayAmount);
            morrisBarChartVO.setLabels(arrayAmount);
        }


        StringWriter stringWriter = new StringWriter();
        String jsonString = null;
        try
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(MorrisBarChartVO.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
            jaxbMarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
            jaxbMarshaller.marshal(morrisBarChartVO, stringWriter);
            logger.debug("XMLtoJSON BEFORE in fraud:::" + stringWriter.toString());
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
        //createFile(filename, statusChartXml);

    }

    private BigDecimal totalSales(int agentId, String lastYearTime, Connection con,String perCurrency) throws SystemError,SQLException
    {
        String tablename = "";
        String fields = "";
        int salesCount = 0;
        String status = "";
        AgentFunctions pAgent = new AgentFunctions();
        Functions functions = new Functions();
        String sMemberList = null;
        sMemberList = pAgent.getAgentMemberList(String.valueOf(agentId));
        if(!functions.isValueNull(sMemberList))
        {
            sMemberList = "10000"; // This is to get rid of sql error. Random number 10000
        }
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
            query.append(" and toid IN(" + sMemberList + ")");
            query.append(" and dtstamp >='" + lastYearTime + "'");
            query.append(" and currency='"+ perCurrency +"'");
            query.append(" UNION ");
            query.append(" select sum(amount) as total from " + tablename + " where ");
            query.append(" status in ('authsuccessful')");
            query.append(" and toid IN(" + sMemberList + ")");
            query.append(" and dtstamp >='" + lastYearTime + "' ");
            query.append(" and currency='"+ perCurrency +"'");

            if(i.hasNext())
                query.append(" UNION ");
        }

        long number = 0;
        BigDecimal amount = null;
        StringBuffer countquery = null;
        if(query.length()>0)
        {
            countquery = new StringBuffer("select SUM(total) as total from ( " + query + ")as temp ");
            logger.debug("Countquery in TotalSales Chart-------->"+countquery);
            ResultSet rs = Database.executeQuery(countquery.toString(), con);
            if(rs.next())
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

    private List<BigDecimal> totalChargebackCaptureRefund(int agentId, String lastYearTime, Connection connection,String perCurrency) throws SystemError,SQLException
    {
        String tablename = "";
        String fields = "";
        AgentFunctions pAgent = new AgentFunctions();
        Functions functions = new Functions();
        String sMemberList = null;
        sMemberList = pAgent.getAgentMemberList(String.valueOf(agentId));
        if(!functions.isValueNull(sMemberList))
        {
            sMemberList = "10000"; // This is to get rid of sql error. Random number 10000
        }
        StringBuffer query = new StringBuffer();
        Set<String> gatewayTypeSet = gatewayTransactionTables();
        Iterator i = gatewayTypeSet.iterator();
        while(i.hasNext())
        {
            tablename = (String)i.next();
            fields = "sum(captureamount) as capture, sum(refundamount) as refund, sum(chargebackamount) as chargeback";
            query.append("select " + fields + " from " + tablename + " where");
            query.append(" toid IN(" + sMemberList + ")");
            query.append(" and dtstamp >='" + lastYearTime + "'");
            query.append(" and currency='"+ perCurrency +"'");
            if(i.hasNext())
                query.append(" UNION ");
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

    public Set<String> gatewayTransactionTables()
    {
        Set<String> gatewayTypeSet = new HashSet();
        gatewayTypeSet.add("transaction_common");
        /*gatewayTypeSet.add("transaction_qwipi");
        gatewayTypeSet.add("transaction_ecore");*/

        return gatewayTypeSet;
    }

    final static public void main(String[] arguments)
    {

    }
}

