package net.partner;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.logicboxes.util.ApplicationProperties;
import com.logicboxes.util.Util;
import com.manager.vo.ChartVolumeVO;
import com.manager.vo.morrisBarVOs.Data;
import com.manager.vo.morrisBarVOs.MorrisBarChartVO;
import com.manager.vo.morrisChartVOs.morrisDonut.MorrisDonutChartVO;
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
    private static String PARTNER_XML_DATA_FILE_PATH = ApplicationProperties.getProperty("PARTNER_XML_DATA_FILE_PATH");

    private static String[] colorPallet = new String[] {"#68c39f", "#edce8c", "#Abb7b7", "#4a525f", "#b4c0c0", "#e5d493", "#4a525f", "#7bC0AA", "#b4c0c0", "#e5d493", "#4a525f", "#7bC0AA", "#b4c0c0", "#e5d493", "#4a525f", "#b4c0c0","#b4c0c0", "#e5d493", "#4a525f", "#7bC0AA", "#b4c0c0", "#e5d493", "#4a525f", "#7bC0AA", "#b4c0c0", "#e5d493", "#4a525f", "#7bC0AA", "rgba(180,192,192,0.1)", "rgba(229,212,147,0.1)", "rgba(74,82,95,0.1)", "rgba(123,192,170,0.1)", "rgba(11,98,164,0.1)", "rgba(11,98,164,0.1)", "rgba(11,98,164,0.1)", "rgba(11,98,164,0.1)", "rgba(11,98,164,0.7)", "rgba(11,98,164,0.7)", "rgba(11,98,164,0.7)","rgba(11,98,164,0.7)", "rgba(11,98,164,0.7)", "rgba(11,98,164,0.7)", "rgba(11,98,164,0.7)", "rgba(11,98,164,0.7)", "rgba(11,98,164,0.7)", "rgba(11,98,164,0.7)", "rgba(11,98,164,0.7)", "rgba(11,98,164,0.7)", "rgba(11,98,164,0.7)", "rgba(11,98,164,0.7)", "rgba(11,98,164,0.7)", "rgba(11,98,164,0.7)"};

    ChartVolumeVO chartVolumeVO = null;

    public ChartVolumeVO prepareAllChartsData(String memberId,String partnerid,String fdtstamp,String tdtstamp,String token, String currency,String accountid)
    {
        chartVolumeVO = new ChartVolumeVO();

        String chartType = "salesChart";
        //Prepare Sales Chart Data
        chartVolumeVO = prepareColumnChartData(memberId,partnerid,chartType,fdtstamp,tdtstamp,token,chartVolumeVO,currency,accountid);

        chartType ="refundChart";
        //Prepare Refund Chart Data
        chartVolumeVO = prepareColumnChartData(memberId,partnerid,chartType,fdtstamp,tdtstamp,token,chartVolumeVO,currency,accountid);

        chartType ="chargebackChart";
        //Prepare Chargeback Chart Data
        chartVolumeVO = prepareColumnChartData(memberId,partnerid,chartType,fdtstamp,tdtstamp,token,chartVolumeVO,currency,accountid);

        return chartVolumeVO;
    }

    public ChartVolumeVO prepareColumnChartData(String memberId, String partnerid, String chartType,String fdtstamp,String tdtstamp,String token,ChartVolumeVO chartVolumeVO, String currency,String accountid)
    {
        log.debug("Preparing column chart for "+chartType);
        PartnerFunctions partner=new PartnerFunctions();
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

            conn= Database.getRDBConnection();
            capturefields = "date_format(from_unixtime(dtstamp),'%b-%y') as month,sum(captureamount) as amount,max(dtstamp) as dtstamp";
            refundfields = "date_format(from_unixtime(dtstamp),'%b-%y') as month,sum(captureamount) as amount,max(dtstamp) as dtstamp";
            chargebackfields = "date_format(from_unixtime(dtstamp),'%b-%y') as month,sum(captureamount) as amount,max(dtstamp) as dtstamp";
            fields = "date_format(from_unixtime(dtstamp),'%b-%y') as month,sum(captureamount) as amount,max(dtstamp) as dtstamp";

            /*//qwipi
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
            *//*if(memberId.equalsIgnoreCase("all"))
            {
                String allMemberid=partner.getPartnerMemberRS(partnerid);
                query.append(" toid IN ("+ allMemberid+")");
            }
            else
            {
                query.append(" toid IN ("+ESAPI.encoder().encodeForSQL(me,memberId)+")");
            }*//*
            query.append(" toid ="+ESAPI.encoder().encodeForSQL(me,memberId));
            query.append(" AND dtstamp>= '"+ESAPI.encoder().encodeForSQL(me,fdtstamp)+"'");
            query.append(" AND dtstamp<= '"+ESAPI.encoder().encodeForSQL(me,tdtstamp)+"'");
            //query.append(" AND currency= '"+ESAPI.encoder().encodeForSQL(me,currency)+"'");
            if (partner.isEmptyOrNull(accountid))
            {
                String getAccountIds = partner.getAccountIds(memberId);
                query.append(" AND accountid IN ("+getAccountIds+")");
            }
            else
            {
                query.append(" AND accountid IN ('"+ESAPI.encoder().encodeForSQL(me,accountid)+"')");
            }
            if (partner.isEmptyOrNull(currency))
            {
                String allcurrency = partner.allCurrency(memberId);
                query.append(" AND currency IN ("+allcurrency+")");
            }
            else
            {
                query.append(" AND currency IN ('"+ESAPI.encoder().encodeForSQL(me,currency)+"')");
            }
            if(chartType.equals("salesChart")){
                query.append(" and status in ('capturesuccess','setVfr5vftg!2tled','authsuccessful')");
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

                *//*if(memberId.equalsIgnoreCase("all"))
                {
                    String allMemberid=partner.getPartnerMemberRS(partnerid);
                    query.append(" toid IN ("+ allMemberid+")");
                }
                else
                {
                    query.append(" toid ='"+ESAPI.encoder().encodeForSQL(me,memberId)+"'");
                }*//*
                query.append(" toid ="+ESAPI.encoder().encodeForSQL(me,memberId));
                query.append(" AND dtstamp>= '"+ESAPI.encoder().encodeForSQL(me,fdtstamp)+"'");
                query.append(" AND dtstamp<= '"+ESAPI.encoder().encodeForSQL(me,tdtstamp)+"'");
               // query.append(" AND currency= '"+ESAPI.encoder().encodeForSQL(me,currency)+"'");
                if (partner.isEmptyOrNull(accountid))
                {
                    String getAccountIds = partner.getAccountIds(memberId);
                    query.append(" AND accountid IN ("+getAccountIds+")");
                }
                else
                {
                    query.append(" AND accountid IN ('"+ESAPI.encoder().encodeForSQL(me,accountid)+"')");
                }
                if (partner.isEmptyOrNull(currency))
                {
                    String allcurrency = partner.allCurrency(memberId);
                    query.append(" AND currency IN ("+allcurrency+")");
                }
                else
                {
                    query.append(" AND currency IN ('"+ESAPI.encoder().encodeForSQL(me,currency)+"')");
                }
                if(chartType.equals("salesChart")){
                    query.append(" and status='authsuccessful'");
                }
                query.append(" group by month");
                query.append(" UNION ");
            }*/

            //ecore
         /*   if(chartType.equals("salesChart"))
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
            *//*if(memberId.equalsIgnoreCase("all"))
            {
                String allMemberid=partner.getPartnerMemberRS(partnerid);
                query.append(" toid IN ("+allMemberid+")");
            }
            else
            {
                query.append(" toid IN ("+ESAPI.encoder().encodeForSQL(me,memberId)+")");
            }*//*
            query.append(" toid ="+ESAPI.encoder().encodeForSQL(me,memberId));
            query.append(" AND dtstamp>= '"+ESAPI.encoder().encodeForSQL(me,fdtstamp)+"'");
            query.append(" AND dtstamp<= '"+ESAPI.encoder().encodeForSQL(me,tdtstamp)+"'");
            //query.append(" AND currency= '"+ESAPI.encoder().encodeForSQL(me,currency)+"'");
            if (partner.isEmptyOrNull(accountid))
            {
                String getAccountIds = partner.getAccountIds(memberId);
                query.append(" AND accountid IN ("+getAccountIds+")");
            }
            else
            {
                query.append(" AND accountid IN ('"+ESAPI.encoder().encodeForSQL(me,accountid)+"')");
            }
            if (partner.isEmptyOrNull(currency))
            {
                String allcurrency = partner.allCurrency(memberId);
                query.append(" AND currency IN ("+allcurrency+")");
            }
            else
            {
                query.append(" AND currency IN ('"+ESAPI.encoder().encodeForSQL(me,currency)+"')");
            }
            if(chartType.equals("salesChart")){
                query.append(" and status in ('capturesuccess','settled','authsuccessful')");
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

                *//*if(memberId.equalsIgnoreCase("all"))
                {
                    String allMemberid=partner.getPartnerMemberRS(partnerid);
                    query.append(" toid IN ("+allMemberid+")");
                }
                else
                {
                    query.append(" toid ='"+ESAPI.encoder().encodeForSQL(me,memberId)+"'");
                }*//*
                query.append(" toid ="+ESAPI.encoder().encodeForSQL(me,memberId));
                query.append(" AND dtstamp>= '"+ESAPI.encoder().encodeForSQL(me,fdtstamp)+"'");
                query.append(" AND dtstamp<= '"+ESAPI.encoder().encodeForSQL(me,tdtstamp)+"'");
                //query.append(" AND currency= '"+ESAPI.encoder().encodeForSQL(me,currency)+"'");
                if (partner.isEmptyOrNull(accountid))
                {
                    String getAccountIds = partner.getAccountIds(memberId);
                    query.append(" AND accountid IN ("+getAccountIds+")");
                }
                else
                {
                    query.append(" AND accountid IN ('"+ESAPI.encoder().encodeForSQL(me,accountid)+"')");
                }
                if (partner.isEmptyOrNull(currency))
                {
                    String allcurrency = partner.allCurrency(memberId);
                    query.append(" AND currency IN ("+allcurrency+")");
                }
                else
                {
                    query.append(" AND currency IN ('"+ESAPI.encoder().encodeForSQL(me,currency)+"')");
                }
                if(chartType.equals("salesChart")){
                    query.append(" and status='authsuccessful'");
                }
                query.append(" group by month");
                query.append(" UNION ");
            }*/

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
            /*if(memberId.equalsIgnoreCase("all"))
            {
                String allMemberid=partner.getPartnerMemberRS(partnerid);
                query.append(" toid IN ("+allMemberid+")");
            }
            else
            {
                query.append(" toid IN ("+ESAPI.encoder().encodeForSQL(me,memberId)+")");
            }*/
            query.append(" toid ="+ESAPI.encoder().encodeForSQL(me,memberId));
            query.append(" AND dtstamp>= '"+ESAPI.encoder().encodeForSQL(me,fdtstamp)+"'");
            query.append(" AND dtstamp<= '"+ESAPI.encoder().encodeForSQL(me,tdtstamp)+"'");
           // query.append(" AND currency= '"+ESAPI.encoder().encodeForSQL(me,currency)+"'");
            if (partner.isEmptyOrNull(accountid))
            {
                String getAccountIds = partner.getAccountIds(memberId);
                query.append(" AND accountid IN ("+getAccountIds+")");
            }
            else
            {
                query.append(" AND accountid IN ('"+ESAPI.encoder().encodeForSQL(me,accountid)+"')");
            }
            if (partner.isEmptyOrNull(currency))
            {
                String allcurrency = partner.allCurrency(memberId);
                query.append(" AND currency IN ("+allcurrency+")");
            }
            else
            {
                query.append(" AND currency IN ('"+ESAPI.encoder().encodeForSQL(me,currency)+"')");
            }
            if(chartType.equals("salesChart")){
                query.append(" and status in ('capturesuccess','settled','authsuccessful')");
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

                /*if(memberId.equalsIgnoreCase("all"))
                {
                    String allMemberid=partner.getPartnerMemberRS(partnerid);
                    query.append(" toid IN ("+allMemberid+")");
                }
                else
                {
                    query.append(" toid ='"+ESAPI.encoder().encodeForSQL(me,memberId)+"'");
                }*/
                query.append(" toid ="+ESAPI.encoder().encodeForSQL(me,memberId));
                query.append(" AND dtstamp>= '"+ESAPI.encoder().encodeForSQL(me,fdtstamp)+"'");
                query.append(" AND dtstamp<= '"+ESAPI.encoder().encodeForSQL(me,tdtstamp)+"'");
               // query.append(" AND currency= '"+ESAPI.encoder().encodeForSQL(me,currency)+"'");
                if (partner.isEmptyOrNull(accountid))
                {
                    String getAccountIds = partner.getAccountIds(memberId);
                    query.append(" AND accountid IN ("+getAccountIds+")");
                }
                else
                {
                    query.append(" AND accountid IN ('"+ESAPI.encoder().encodeForSQL(me,accountid)+"')");
                }
                if (partner.isEmptyOrNull(currency))
                {
                    String allcurrency = partner.allCurrency(memberId);
                    query.append(" AND currency IN ("+allcurrency+")");
                }
                else
                {
                    query.append(" AND currency IN ('"+ESAPI.encoder().encodeForSQL(me,currency)+"')");
                }
                if(chartType.equals("salesChart")){
                    query.append(" and status='authsuccessful'");
                }
                query.append(" group by month");
            }

            StringBuffer countquery = new StringBuffer("select month, SUM(amount) as amount,max(dtstamp) as dt from ( " + query + ")as temp group by month order by dt");
            //if(countquery.length()>0)

                log.debug("===countquery in Partner Merchant Volume==" + countquery.toString());

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

                log.debug("Morris char in TransReport---->" + stringWriterBarChart.toString());
                //chartString = stringWriterBarChart.toString();
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

            //chartData.append("</graph>");
            createFile(PARTNER_XML_DATA_FILE_PATH + "/Partner_" + Functions.getFormattedDate("yyMM") + "_" + token + ".json",chartString);
        }
        catch(Exception e){
            log.error("Error in column chart data creation",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }

        return chartVolumeVO;
    }

    private static void createFile(String fileName, StringBuilder statusChartXml)
    {
        File f = new File(fileName);

        FileWriter fw = null;
        try
        {
            log.debug(" Creating xml data file " );
            fw = new FileWriter(f);
            fw.write(statusChartXml.toString());
            fw.flush();

        }
        catch (IOException e)
        {
            log.error(" Error in creating xml data file ",e);
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
                    log.error("Could not close file handler" + Util.getStackTrace(e));
                }
        }
    }
}
