package net.agent;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.logicboxes.util.ApplicationProperties;
import com.logicboxes.util.Util;
import com.manager.vo.ChartVolumeVO;
import com.manager.vo.morrisBarVOs.Data;
import com.manager.vo.morrisBarVOs.MorrisBarChartVO;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 5/5/14
 * Time: 6:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class TransReport
{
    private static Logger log = new Logger(TransReport.class.getName());
    private static String AGENT_XML_DATA_FILE_PATH = ApplicationProperties.getProperty("AGENT_XML_DATA_FILE_PATH");
    private static String[] colorPallet = new String[]{"AFD8F8", "F6BD0F", "8BBA00", "FF8E46", "008E8E", "D64646", "8E468E", "588526", "B3AA00", "008ED6", "9D080D", "A186BE", "78F04A","AFD8F8", "F6BD0F", "8BBA00", "FF8E46", "008E8E", "D64646", "8E468E", "588526", "B3AA00", "008ED6", "9D080D", "A186BE", "78F04A","AFD8F8", "F6BD0F", "8BBA00", "FF8E46", "008E8E", "D64646", "8E468E", "588526", "B3AA00", "008ED6", "9D080D", "A186BE", "78F04A","AFD8F8", "F6BD0F", "8BBA00", "FF8E46", "008E8E", "D64646", "8E468E", "588526", "B3AA00", "008ED6", "9D080D", "A186BE", "78F04A"};

    ChartVolumeVO chartVolumeVO = null;

    public ChartVolumeVO prepareAllChartsData(String memberId,String agentid,String fdtstamp,String tdtstamp,String currency)
    {

        log.debug("Fdtstamp--->"+fdtstamp+"----Tdtstamp---"+tdtstamp+"---agentid---"+agentid+"---toid---"+memberId+"---currency---"+currency);
        chartVolumeVO = new ChartVolumeVO();

        String chartType = "salesChart";
        //Prepare Sales Chart Data
        chartVolumeVO = prepareColumnChartData(memberId,agentid,chartType,fdtstamp,tdtstamp,chartVolumeVO,currency);

        chartType ="refundChart";
        //Prepare Refund Chart Data
        chartVolumeVO = prepareColumnChartData(memberId,agentid,chartType,fdtstamp,tdtstamp,chartVolumeVO,currency);

        chartType ="chargebackChart";
        //Prepare Chargeback Chart Data
        chartVolumeVO = prepareColumnChartData(memberId,agentid,chartType,fdtstamp,tdtstamp,chartVolumeVO,currency);

        return chartVolumeVO;
    }

    public ChartVolumeVO prepareColumnChartData(String memberId,String agentid, String chartType,String fdtstamp,String tdtstamp,ChartVolumeVO chartVolumeVO, String currency)
    {

        log.debug("Preparing column chart for "+chartType);
        AgentFunctions agent=new AgentFunctions();
        Functions functions = new Functions();
        String fields;
        String capturefields;
        String refundfields;
        String chargebackfields;
        StringBuffer query = new StringBuffer();
        StringBuilder chartString = new StringBuilder();
        MorrisBarChartVO morrisBarChartVO = null;

        List<String> backGroundColor = new ArrayList<String>();
        Data datasets = null;

        Connection conn = null;
        ResultSet rs = null;
        try
        {   Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
            //conn= Database.getConnection();
            conn= Database.getRDBConnection();
            fields = "date_format(from_unixtime(dtstamp),'%b-%y') as month,sum(amount) as amount,max(dtstamp) as dtstamp";
            capturefields = "date_format(from_unixtime(dtstamp),'%b-%y') as month,sum(captureamount) as amount,max(dtstamp) as dtstamp";
            refundfields = "date_format(from_unixtime(dtstamp),'%b-%y') as month,sum(refundamount) as amount,max(dtstamp) as dtstamp";
            chargebackfields = "date_format(from_unixtime(dtstamp),'%b-%y') as month,sum(chargebackamount) as amount,max(dtstamp) as dtstamp";

            //qwipi
            if(chartType.equals("salesChart"))
            {
                query.append("select " + capturefields + " from transaction_qwipi where");
            }
            else if(chartType.equals("refundChart"))
            {
                query.append("select " + refundfields + " from transaction_qwipi where");
            }
            else if(chartType.equals("chargebackChart"))
            {
                query.append("select " + chargebackfields + " from transaction_qwipi where");
            }
            if(memberId.equalsIgnoreCase("all"))
            {
                String allMemberid=agent.getAgentMemberList(agentid);
                query.append(" toid IN ("+ allMemberid+")");
            }
            else
            {
                query.append(" toid ='"+ESAPI.encoder().encodeForSQL(me,memberId)+"'");
            }
            query.append(" AND dtstamp>= '"+ESAPI.encoder().encodeForSQL(me,fdtstamp)+"'");
            query.append(" AND dtstamp<= '"+ESAPI.encoder().encodeForSQL(me,tdtstamp)+"'");
            query.append(" AND currency= '"+ESAPI.encoder().encodeForSQL(me,currency)+"'");
            if(chartType.equals("salesChart")){
                query.append(" and status in ('capturesuccess','settled')");
            }
            else if(chartType.equals("refundChart")){
                query.append(" and status ='reversed'");
            }
            else if(chartType.equals("chargebackChart")){
                query.append(" and status ='chargeback'");
            }
            query.append(" group by month");
            query.append(" UNION ");
            //For Authsuccessful
            if(chartType.equals("salesChart"))
            {
                query.append("select " + fields + " from transaction_qwipi where");

                if(memberId.equalsIgnoreCase("all"))
                {
                    String allMemberid=agent.getAgentMemberList(agentid);
                    query.append(" toid IN ("+ allMemberid+")");
                }
                else
                {
                    query.append(" toid ='"+ESAPI.encoder().encodeForSQL(me,memberId)+"'");
                }
                query.append(" AND dtstamp>= '"+ESAPI.encoder().encodeForSQL(me,fdtstamp)+"'");
                query.append(" AND dtstamp<= '"+ESAPI.encoder().encodeForSQL(me,tdtstamp)+"'");
                query.append(" AND currency= '"+ESAPI.encoder().encodeForSQL(me,currency)+"'");
                if(chartType.equals("salesChart")){
                    query.append(" and status='authsuccessful'");
                }
                query.append(" group by month");
                query.append(" UNION ");
            }

            //ecore
            if(chartType.equals("salesChart"))
            {
                query.append("select " + capturefields + " from transaction_ecore where");
            }
            else if(chartType.equals("refundChart"))
            {
                query.append("select " + refundfields + " from transaction_ecore where");
            }
            else if(chartType.equals("chargebackChart"))
            {
                query.append("select " + chargebackfields + " from transaction_ecore where");
            }
            if(memberId.equalsIgnoreCase("all"))
            {
                String allMemberid=agent.getAgentMemberList(agentid);
                query.append(" toid IN ("+ allMemberid+")");
            }
            else
            {
                query.append(" toid ='"+ESAPI.encoder().encodeForSQL(me,memberId)+"'");
            }
            query.append(" AND dtstamp>= '"+ESAPI.encoder().encodeForSQL(me,fdtstamp)+"'");
            query.append(" AND dtstamp<= '"+ESAPI.encoder().encodeForSQL(me,tdtstamp)+"'");
            query.append(" AND currency= '"+ESAPI.encoder().encodeForSQL(me,currency)+"'");
            if(chartType.equals("salesChart")){
                query.append(" and status in ('capturesuccess','settled')");
            }
            else if(chartType.equals("refundChart")){
                query.append(" and status ='reversed'");
            }
            else if(chartType.equals("chargebackChart")){
                query.append(" and status ='chargeback'");
            }
            query.append(" group by month");
            query.append(" UNION ");
            //For Authsuccessful
            if(chartType.equals("salesChart"))
            {
                query.append("select " + fields + " from transaction_ecore where");

                if(memberId.equalsIgnoreCase("all"))
                {
                    String allMemberid=agent.getAgentMemberList(agentid);
                    query.append(" toid IN ("+ allMemberid+")");
                }
                else
                {
                    query.append(" toid ='"+ESAPI.encoder().encodeForSQL(me,memberId)+"'");
                }
                query.append(" AND dtstamp>= '"+ESAPI.encoder().encodeForSQL(me,fdtstamp)+"'");
                query.append(" AND dtstamp<= '"+ESAPI.encoder().encodeForSQL(me,tdtstamp)+"'");
                query.append(" AND currency= '"+ESAPI.encoder().encodeForSQL(me,currency)+"'");
                if(chartType.equals("salesChart")){
                    query.append(" and status='authsuccessful'");
                }
                query.append(" group by month");
                query.append(" UNION ");
            }

            //common
            if(chartType.equals("salesChart"))
            {
                query.append("select " + capturefields + " from transaction_common where");
            }
            else if(chartType.equals("refundChart"))
            {
                query.append("select " + refundfields + " from transaction_common where");
            }
            else if(chartType.equals("chargebackChart"))
            {
                query.append("select " + chargebackfields + " from transaction_common where");
            }
            if(memberId.equalsIgnoreCase("all"))
            {
                String allMemberid=agent.getAgentMemberList(agentid);
                query.append(" toid IN ("+ allMemberid+")");
            }
            else
            {
                query.append(" toid ='"+ESAPI.encoder().encodeForSQL(me,memberId)+"'");
            }
            query.append(" AND dtstamp>= '"+ESAPI.encoder().encodeForSQL(me,fdtstamp)+"'");
            query.append(" AND dtstamp<= '"+ESAPI.encoder().encodeForSQL(me,tdtstamp)+"'");
            query.append(" AND currency= '"+ESAPI.encoder().encodeForSQL(me,currency)+"'");
            if(chartType.equals("salesChart"))
            {
                query.append(" and status in ('capturesuccess','settled')");
            }
            else if(chartType.equals("refundChart")){
                query.append(" and status ='reversed'");
            }
            else if(chartType.equals("chargebackChart")){
                query.append(" and status ='chargeback'");
            }
            query.append(" group by month");

            //For Authsuccessful
            if(chartType.equals("salesChart"))
            {
                query.append(" UNION ");
                query.append("select " + fields + " from transaction_common where");

                if(memberId.equalsIgnoreCase("all"))
                {
                    String allMemberid=agent.getAgentMemberList(agentid);
                    query.append(" toid IN ("+ allMemberid+")");
                }
                else
                {
                    query.append(" toid ='"+ESAPI.encoder().encodeForSQL(me,memberId)+"'");
                }
                query.append(" AND dtstamp>= '"+ESAPI.encoder().encodeForSQL(me,fdtstamp)+"'");
                query.append(" AND dtstamp<= '"+ESAPI.encoder().encodeForSQL(me,tdtstamp)+"'");
                query.append(" AND currency= '"+ESAPI.encoder().encodeForSQL(me,currency)+"'");
                if(chartType.equals("salesChart")){
                    query.append(" and status='authsuccessful'");
                }
                query.append(" group by month");
            }

            StringBuffer countquery = new StringBuffer("select month, SUM(amount) as amount,max(dtstamp) as dt from ( "+ query +")as temp group by month order by dt");
            log.debug("Count query in AgentTransReport------->"+countquery);
            rs = Database.executeQuery(countquery.toString(), conn);

            List<Data> data = new ArrayList<Data>();

            String month = "";
            double doubleAmount = 0.0;

            while (rs.next())
            {
                month = rs.getString("month");
                doubleAmount = rs.getDouble("amount");

                datasets = new Data();
                datasets.setMonth(month);
                datasets.setAmount(doubleAmount);
                data.add(datasets);

            }

            String backGroundColors = "";
            if(chartType.equals("salesChart"))
            {
                //backGroundColors = "#68c39f";
                backGroundColor.add("#68c39f");
            }
            else if(chartType.equals("refundChart"))
            {
                //backGroundColors = "#edce8c";
                backGroundColor.add("#edce8c");
            }
            else if(chartType.equals("chargebackChart"))
            {
                //backGroundColors = "#Abb7b7";
                backGroundColor.add("#Abb7b7");
            }
            //String element = "salesChart";

            if(data.size()>0)
            {
                String[] amount = {"amount"};

                morrisBarChartVO = new MorrisBarChartVO();

                morrisBarChartVO.setElement(chartType);
                morrisBarChartVO.setData(data);
                morrisBarChartVO.setXkey("month");
                morrisBarChartVO.setYkeys(amount);
                morrisBarChartVO.setLabels(amount);
                morrisBarChartVO.setBarColors(backGroundColor);
            }
            else
            {
                //System.out.println("Inside first else condition");
                data = new ArrayList<Data>();
                String[] amount = {"amount"};
                morrisBarChartVO = new MorrisBarChartVO();

                datasets = new Data();
                datasets.setMonth("No data to display");
                datasets.setAmount(0.0);
                data.add(datasets);

                morrisBarChartVO.setElement(chartType);
                morrisBarChartVO.setData(data);
                morrisBarChartVO.setXkey("month");
                morrisBarChartVO.setYkeys(amount);
                morrisBarChartVO.setLabels(amount);
            }

            StringWriter stringWriterBarChart = new StringWriter();

            try
            {
                JAXBContext jaxbContextBar = JAXBContext.newInstance(MorrisBarChartVO.class);
                Marshaller jaxbMarshallerBar = jaxbContextBar.createMarshaller();

                // output Donut
                jaxbMarshallerBar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                jaxbMarshallerBar.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
                jaxbMarshallerBar.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
                jaxbMarshallerBar.marshal(morrisBarChartVO, stringWriterBarChart);

                if(chartType.equals("salesChart"))
                {
                    chartVolumeVO.setSalesChart(stringWriterBarChart.toString());
                }
                else if(chartType.equals("refundChart"))
                {
                    chartVolumeVO.setRefundChart(stringWriterBarChart.toString());
                }
                else if(chartType.equals("chargebackChart"))
                {
                    chartVolumeVO.setChargebackChart(stringWriterBarChart.toString());
                }

                //System.out.println("Morris char in TransReport---->" + stringWriterBarChart.toString());
                log.debug("Morris char in TransReport---->" + stringWriterBarChart.toString());
            }
            catch (PropertyException e)
            {
                log.error("Property Exception while converting to json", e);
            }
            catch (JAXBException e)
            {
                log.error("JAXB Exception while converting to json", e);
            }

            chartString.append(stringWriterBarChart.toString());

            //System.out.println("ChartString ------->"+chartString.toString());

        }
        catch(Exception e)
        {
            log.error("Error in column chart data creation",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }

        return chartVolumeVO;
    }

    public static void prepareAllChartsDataWithTerminal(String memberId,String terminalid, String fromdate,String todate,String agentid,String dFromDate,String dToDate,String token)
    {
        String chartType = "Sales";
        //Prepare Sales Chart Data
        prepareColumnChartDataWithTerminal(memberId, terminalid, fromdate, todate, agentid, chartType, dFromDate, dToDate, token);

        chartType ="Refund";
        //Prepare Refund Chart Data
        prepareColumnChartDataWithTerminal(memberId, terminalid, fromdate, todate, agentid, chartType, dFromDate, dToDate, token);

        chartType ="Chargeback";
        //Prepare Chargeback Chart Data
        prepareColumnChartDataWithTerminal(memberId,terminalid,fromdate,todate,agentid,chartType,dFromDate,dToDate,token);

        /*chartType ="Fraud";
        //Prepare Fraud Chart Data
        prepareColumnChartData(memberId,tdtstamp,fdtstamp,cardType,chartType);*/
    }

    public static void prepareColumnChartDataWithTerminal(String memberId,String terminalid,String fromDate,String toDate, String agentid, String chartType,String dFromDate,String dToDate,String token){

        log.debug("Preparing column chart for "+chartType);
        AgentFunctions agent=new AgentFunctions();
        String fields;
        String joindata = null;
        String allMembers = "";
        StringBuffer query = new StringBuffer();
        Connection conn = null;
        ResultSet rs = null;
        Functions functions = new Functions();
        try
        {   Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
            //conn= Database.getConnection();
            conn= Database.getRDBConnection();
            fields = "date_format(from_unixtime(t.dtstamp),'%b-%y') as month,sum(amount) as amount";
            joindata = "JOIN member_account_mapping AS m WHERE t.toid=m.memberid AND t.accountid=m.accountid AND t.paymodeid=m.paymodeid AND t.cardtypeid=m.cardtypeid ";

            //qwipi
            query.append("select " + fields + " from transaction_qwipi AS t "+joindata);
            if(functions.isValueNull(memberId))
            {
                query.append(" AND toid ='"+memberId+"'");
            }
            else
            {
                allMembers = agent.getAgentMemberList(agentid);
                query.append(" AND toid IN("+allMembers+")");
            }
            if(functions.isValueNull(terminalid))
            {
                query.append(" AND m.terminalid ='"+terminalid+"'");
            }
            query.append(" AND FROM_UNIXTIME(t.dtstamp) >= CONCAT('"+ESAPI.encoder().encodeForSQL(me,fromDate)+"',' 00:00:00')");
            query.append(" AND FROM_UNIXTIME(t.dtstamp) <= CONCAT('"+ESAPI.encoder().encodeForSQL(me,toDate)+"',' 23:59:59')");
            if(chartType.equals("Sales")){
                query.append(" and status in ('capturesuccess','settled','authsuccessful')");
            }
            else if(chartType.equals("Refund")){
                query.append(" and status ='reversed'");
            }
            else if(chartType.equals("Chargeback")){
                query.append(" and status ='chargeback'");
            }
            query.append(" group by month");
            query.append(" UNION ");

            //ecore
            query.append("select " + fields + " from transaction_ecore AS t "+joindata);
            if(functions.isValueNull(memberId))
            {
                query.append(" AND toid ='"+memberId+"'");
            }
            else
            {
                allMembers = agent.getAgentMemberList(agentid);
                query.append(" AND toid IN("+allMembers+")");
            }
            if(functions.isValueNull(terminalid))
            {

                query.append(" AND m.terminalid ='"+terminalid+"'");
            }

            query.append(" AND FROM_UNIXTIME(t.dtstamp) >= CONCAT('"+ESAPI.encoder().encodeForSQL(me,fromDate)+"',' 00:00:00')");
            query.append(" AND FROM_UNIXTIME(t.dtstamp) <= CONCAT('"+ESAPI.encoder().encodeForSQL(me,toDate)+"',' 23:59:59')");
            if(chartType.equals("Sales")){
                query.append(" and status in ('capturesuccess','settled','authsuccessful')");
            }
            else if(chartType.equals("Refund")){
                query.append(" and status ='reversed'");
            }
            else if(chartType.equals("Chargeback")){
                query.append(" and status ='chargeback'");
            }
            query.append(" group by month");
            query.append(" UNION ");

            //common
            query.append("select " + fields + " from transaction_common  AS t "+joindata);
            if(functions.isValueNull(memberId))
            {
                query.append(" AND toid ='"+memberId+"'");
            }
            else
            {
                allMembers = agent.getAgentMemberList(agentid);
                query.append(" AND toid IN("+allMembers+")");
            }
            if(functions.isValueNull(terminalid))
            {
                query.append(" AND m.terminalid ='"+terminalid+"'");
            }
            query.append(" AND FROM_UNIXTIME(t.dtstamp) >= CONCAT('"+ESAPI.encoder().encodeForSQL(me,fromDate)+"',' 00:00:00')");
            query.append(" AND FROM_UNIXTIME(t.dtstamp) <= CONCAT('"+ESAPI.encoder().encodeForSQL(me,toDate)+"',' 23:59:59')");
            if(chartType.equals("Sales")){
                query.append(" and status in ('capturesuccess','settled','authsuccessful')");
            }
            else if(chartType.equals("Refund")){
                query.append(" and status ='reversed'");
            }
            else if(chartType.equals("Chargeback")){
                query.append(" and status ='chargeback'");
            }
            query.append(" group by month");
            StringBuffer countquery = new StringBuffer("select month, SUM(amount) as amount from ( "+ query +")as temp group by month");
            //log.debug("===countquery for chart=="+countquery.toString());
            rs = Database.executeQuery(countquery.toString(), conn);

            StringBuilder chartData = new StringBuilder("<graph bgcolor='F2F2F2' showCanvasBg='0' borderColor='B8B7B7' showBorder='1' caption='FromDate: "+dFromDate+"-- TODate: "+dToDate+"' xAxisName='Month' yAxisName='Amount in USD' " +
                    " decimalPrecision='0' formatNumberScale='0' showValues='0' >");

            int colourCount = 0;
            Map<Date, String> treeMap = new TreeMap<Date, String>();
            SimpleDateFormat formatter = new SimpleDateFormat("MMM-yy");
            while (rs.next())
            {
                //chartData.append("<set name='" + rs.getString("month") + "' value='" + rs.getString("amount") + "' color='" + colorPallet[colourCount] + "' />");
                treeMap.put(formatter.parse(rs.getString("month")),"<set name='" + rs.getString("month") + "' value='" + rs.getString("amount") + "' color='" + colorPallet[colourCount] + "' />");
                colourCount++;
            }
            for(String data : treeMap.values()){
                chartData.append(data);
            }
            chartData.append("</graph>");
            if(functions.isValueNull(terminalid))
            {
                createFile(AGENT_XML_DATA_FILE_PATH + "/Agent" + chartType + "-" + token + "-" + terminalid + "-Data.xml", chartData);
            }
            else{
                createFile(AGENT_XML_DATA_FILE_PATH + "/Agent" + chartType + "-" + token + "-Data.xml", chartData);
            }
            log.debug(chartData);
        }
        catch(Exception e){
            log.error("Error in column chart data creation",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
    }

    private static void createFile(String fileName, StringBuilder statusChartXml)
    {
        File f = new File(fileName);

        FileWriter fw = null;
        try
        {
            log.debug(" Creating xml data file ");
            fw = new FileWriter(f);
            fw.write(statusChartXml.toString());
            fw.flush();

        }
        catch (IOException e)
        {
            log.error(" Error In Creating XML Data File ",e);
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
                    log.error("Could Not Close File Handler" + Util.getStackTrace(e));
                }
        }
    }
}
