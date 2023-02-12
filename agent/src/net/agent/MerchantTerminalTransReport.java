package net.agent;

import com.directi.pg.*;
import com.manager.TerminalManager;
import com.manager.vo.ChartVolumeVO;
import com.manager.vo.TerminalVO;
import com.manager.vo.morrisBarVOs.Data;
import com.manager.vo.morrisBarVOs.MorrisBarChartVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
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
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Sneha on 10/31/2015.
 */
public class MerchantTerminalTransReport extends HttpServlet
{
    private static Logger log = new Logger(MerchantTerminalTransReport.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }
    public void doService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        log.debug("Entering in MerchantTransSummaryList");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        AgentFunctions agent=new AgentFunctions();
        if (!agent.isLoggedInAgent(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/agent/logout.jsp");
            return;
        }

        Calendar cal= Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date= null;

        //RequestDispatcher rd = req.getRequestDispatcher("/merchantTerminalTransReport.jsp?ctoken=" + user.getCSRFToken());
        RequestDispatcher rdError=req.getRequestDispatcher("/merchantTerminalTransReport.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        RequestDispatcher rdSuccess= req.getRequestDispatcher("/merchantTerminalTransReport.jsp?Success=YES&ctoken="+user.getCSRFToken());

        StringBuffer error = new StringBuffer();
        Functions functions = new Functions();

        String oError = validateOptionalParameters(req);

        error.append(oError);

        String toid = req.getParameter("toid");
        String terminalid = req.getParameter("terminalid");

        String fromdate=req.getParameter("fromdate");
        String todate=req.getParameter("todate");
        String currency = req.getParameter("currency");
        String errorMsg = "";

        if(functions.isFutureDateComparisonWithFromAndToDate(fromdate, todate, "dd/MM/yyyy"))
        {
            req.setAttribute("catchError","Invalid From & To date");
            rdError.forward(req, res);
            return;
        }
        if(functions.isValueNull(error.toString()))
        {
            errorMsg=errorMsg+" Invalid From & To date";
            req.setAttribute("catchError",errorMsg);
            rdError.forward(req, res);
            return;
        }

        if (toid.equalsIgnoreCase("all"))
        {
            log.debug("enter valid toid");
            errorMsg= errorMsg+"Enter valid Merchant ID. It should not be empty.<br>";
            req.setAttribute("currencyError", errorMsg);
            rdError.forward(req, res);
            return;
        }

        if ( terminalid == null || terminalid.equals("null") || terminalid.equals(""))
        {
            log.debug("enter valid terminal id");
            errorMsg= errorMsg+"Enter valid Terminal ID. It should not be empty.<br>";
            req.setAttribute("currencyError", errorMsg);
            rdError.forward(req, res);
            return;
        }

        if (functions.isEmptyOrNull(currency))
        {
            log.debug("enter valid currency");
            errorMsg= errorMsg+"Enter valid Currency. It should not be empty.<br>";
            req.setAttribute("currencyError", errorMsg);
            rdError.forward(req, res);
            return;
        }

        ChartVolumeVO chartVolumeVO = null;
        Hashtable statushash = null;
        TerminalManager terminalManager = new TerminalManager();
        Hashtable<String,Hashtable> stringHashtable = new Hashtable<String, Hashtable>();

        try
        {
            //from date
            date=sdf.parse(fromdate);
            cal.setTime(date);
            String fdate=String.valueOf(cal.get(Calendar.DATE));
            String fmonth=String.valueOf(cal.get(Calendar.MONTH));
            String fyear=String.valueOf(cal.get(Calendar.YEAR));

            //to Date
            date=sdf.parse(todate);
            cal.setTime(date);
            String tdate=String.valueOf(cal.get(Calendar.DATE));
            String tmonth=String.valueOf(cal.get(Calendar.MONTH));
            String tyear=String.valueOf(cal.get(Calendar.YEAR));

            log.debug("From date dd::" + fdate + " MM;;" + fmonth + " YY::" + fyear + " To date dd::" + tdate + " MM::" + tmonth + " YY::" + tyear);

            String dfromdate=fdate+"/"+(Integer.parseInt(fmonth) + 1)+"/"+fyear;
            String dtodate=tdate+"/"+(Integer.parseInt(tmonth) + 1)+"/"+tyear;
            String fromDate=fyear+"-"+(Integer.parseInt(fmonth) + 1)+"-"+fdate;
            String toDate=tyear+"-"+(Integer.parseInt(tmonth) + 1)+"-"+tdate;

            //System.out.println("dfromdate---->"+dfromdate+"---dtodate---"+dtodate+"---fromDate---"+fromDate+"---toDate---"+toDate);

            //conversion to dtstamp
            String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
            String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");

            if(functions.isValueNull(terminalid))
            {
                //TerminalVO terminalVO = terminalManager.getTerminalByTerminalId(terminalid);
                TerminalVO terminalVO = new TerminalVO();
                terminalVO.setTerminalId(terminalid);
                terminalVO.setMemberId(toid);
                statushash = getMerchantTerminalReport(terminalVO,fdtstamp,tdtstamp,currency);
                chartVolumeVO = prepareAllChartsData(terminalVO,fdtstamp,tdtstamp,currency);
                int records = Integer.parseInt ((String)statushash.get("records"));
                if(records>0)
                {
                    stringHashtable.put(terminalid,statushash);
                }
            }
            else
            {
                List<TerminalVO> terminalVOs = terminalManager.getTerminalsByMerchantId(toid);
                for (TerminalVO terminalVO : terminalVOs)
                {
                    terminalid = terminalVO.getTerminalId();
                    statushash = getMerchantTerminalReport(terminalVO, fdtstamp, tdtstamp,currency);
                    int records = Integer.parseInt ((String)statushash.get("records"));
                    if(records>0)
                    {
                        stringHashtable.put(terminalid,statushash);
                    }
                }
            }
            req.setAttribute("statushash",stringHashtable);

            req.setAttribute("fdate", fdate);
            req.setAttribute("tdate", tdate);
            req.setAttribute("fromdate", fromDate);
            req.setAttribute("todate", toDate);
            req.setAttribute("dfromdate", dfromdate);
            req.setAttribute("dtodate", dtodate);
            req.setAttribute("toid",toid);
            req.setAttribute("chartVolumeVO",chartVolumeVO);
            rdSuccess.forward(req,res);
            return;
        }
        catch (SQLException e)
        {
            log.error("SQLException while accessing data",e);
            error.append("Error while accessing data");
        }
        catch (SystemError systemError)
        {
            log.error("SystemError while accessing data",systemError);
            error.append("Error while accessing data");
        }
        catch (PZDBViolationException e)
        {
            log.error("PZDBViolationException while accessing data",e);
            error.append("Error while accessing data");
        }
        catch (ParseException e)
        {
            log.error("ParseException while accessing data",e);
            error.append("Error while accessing data");
        }
        req.setAttribute("error",error.toString());
        rdSuccess.forward(req,res);
    }

    public Hashtable getMerchantTerminalReport(TerminalVO terminalVO,String fromDate,String toDate, String currency) throws SystemError,SQLException,PZDBViolationException
    {
        log.debug("Entering into getMerchantTerminalReport for fetching reports...");
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        StringBuffer query = new StringBuffer();
        Functions functions = new Functions();

        //conn = Database.getConnection();
        conn = Database.getRDBConnection();
        //qwipi
        query.append("SELECT date_format(from_unixtime(dtstamp),'%b-%y') as month,STATUS,COUNT(*) AS COUNT,SUM(amount) AS amount FROM transaction_qwipi WHERE toid='" +terminalVO.getMemberId()+"' AND dtstamp >='"+fromDate+"' AND dtstamp <='"+toDate+"'");

        if (functions.isValueNull(terminalVO.getTerminalId()))
        {
            query.append(" AND terminalid = '"+terminalVO.getTerminalId()+"'");
        }
        /*query.append("SELECT STATUS,COUNT(*) AS COUNT,SUM(amount) AS amount FROM transaction_qwipi WHERE toid='" +terminalVO.getMemberId()+"'");
        query.append(" AND dtstamp >= "+fromDate);
        query.append(" AND dtstamp <= "+toDate);
        query.append(" AND terminalid = "+terminalVO.getTerminalId());*/
        query.append(" and currency = '"+currency+"'");
        query.append(" group by status ");
        query.append(" UNION ");

        //ecore
        query.append("SELECT date_format(from_unixtime(dtstamp),'%b-%y') as month,STATUS,COUNT(*) AS COUNT,SUM(amount) AS amount FROM transaction_ecore WHERE toid='" +terminalVO.getMemberId()+ "' AND dtstamp >= '"+fromDate + "' AND dtstamp <= '"+toDate +"'");
        if (functions.isValueNull(terminalVO.getTerminalId()))
        {
            query.append(" AND terminalid = '" + terminalVO.getTerminalId() + "'");
        }
        /*query.append(" AND accountid= '"+terminalVO.getAccountId()+"' AND paymodeid= '"+terminalVO.getPaymodeId()+ "' AND cardtypeid='" + terminalVO.getCardTypeId()+"'");
        query.append();
        query.append();*/
        query.append(" and currency = '"+currency+"'");
        query.append(" group by status ");
        query.append(" UNION ");

        //common
        query.append("SELECT date_format(from_unixtime(dtstamp),'%b-%y') as month,STATUS,COUNT(*) AS COUNT,SUM(amount) AS amount FROM transaction_common WHERE toid='" +terminalVO.getMemberId()+ "'  AND dtstamp >= '"+fromDate +"'AND dtstamp <= '"+toDate+"'");
        if (functions.isValueNull(terminalVO.getTerminalId()))
        {
            query.append(" AND terminalid = '"+terminalVO.getTerminalId()+"'");
        }
        /*query.append(" AND accountid= '"+terminalVO.getAccountId()+"' AND paymodeid= '"+terminalVO.getPaymodeId()+ "' AND cardtypeid='"+terminalVO.getCardTypeId()+"' AND terminalid= '"+terminalVO.getTerminalId()+"'");
        query.append(" AND dtstamp >= "+fromDate);
        query.append(" AND dtstamp <= "+toDate);*/
        query.append(" and currency = '"+currency+"'");
        query.append(" group by status ");

        StringBuffer reportquery = new StringBuffer("select month,status, SUM(count) as count,SUM(amount) as amount from ( " + query + ")as temp group by status");
        StringBuffer countquery = new StringBuffer("select SUM(count) as count,SUM(amount) as grandtotal from ( " + query + ")as temp");
        log.debug("Report QRY-------"+reportquery);
        log.debug("Count QRY----------"+countquery);


        Hashtable statusreport= new Hashtable();
        try
        {
            statusreport=Database.getHashFromResultSet(Database.executeQuery(reportquery.toString(), conn));
            rs = Database.executeQuery(countquery.toString(),conn);

            int total = 0;
            String totalamount="";
            if (rs.next())
            {
                total = rs.getInt("count");
                totalamount=rs.getString("grandtotal");
            }
            if(totalamount!=null)
            {
                statusreport.put("grandtotal",totalamount);
                statusreport.put("totalrecords", "" + total);
            }
            else
            {
                statusreport.put("grandtotal","00");
                statusreport.put("totalrecords", "00");
            }
            statusreport.put("records", "0");

            if (total > 0)
                statusreport.put("records", "" + (statusreport.size() - 2));


        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return statusreport;
    }

    //ChartVolumeVO chartVolumeVO = null;

    public ChartVolumeVO prepareAllChartsData(TerminalVO terminalVO,String fdtstamp,String tdtstamp, String currency)
    {
        ChartVolumeVO chartVolumeVO = new ChartVolumeVO();

        String chartType = "salesChart";
        //Prepare Sales Chart Data
        chartVolumeVO = prepareMerchantTerminalGraph(terminalVO, chartType, fdtstamp, tdtstamp, chartVolumeVO, currency);

        chartType ="refundChart";
        //Prepare Refund Chart Data
        chartVolumeVO = prepareMerchantTerminalGraph(terminalVO, chartType, fdtstamp, tdtstamp, chartVolumeVO, currency);

        chartType ="chargebackChart";
        //Prepare Chargeback Chart Data
        chartVolumeVO = prepareMerchantTerminalGraph(terminalVO, chartType, fdtstamp, tdtstamp, chartVolumeVO, currency);

        return chartVolumeVO;
    }

    public ChartVolumeVO prepareMerchantTerminalGraph(TerminalVO terminalVO, String chartType, String fdtstamp, String tdtstamp, ChartVolumeVO chartVolumeVO, String currency )
    {
        log.debug("Preparing column chart for "+chartType);
        //PartnerFunctions partner=new PartnerFunctions();
        String fields;
        String capturefields;
        String refundfields;
        String chargebackfields;
        StringBuffer query = new StringBuffer();
        StringBuilder chartString = new StringBuilder();
        MorrisBarChartVO morrisBarChartVO = null;

        //System.out.println("fdtstamp----"+fdtstamp+"----tdtstamp------"+tdtstamp+"---Currency---"+currency);

        List<String> backGroundColor = new ArrayList<String>();

        Data datasets = null;

        Connection conn = null;
        ResultSet rs = null;
        try
        {   Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);

            conn= Database.getRDBConnection();
            fields = "date_format(from_unixtime(dtstamp),'%b-%y') as month,sum(amount) as amount,max(dtstamp) as dtstamp";
            capturefields = "date_format(from_unixtime(dtstamp),'%b-%y') as month,sum(captureamount) as amount,max(dtstamp) as dtstamp";
            refundfields = "date_format(from_unixtime(dtstamp),'%b-%y') as month,sum(refundamount) as amount,max(dtstamp) as dtstamp";
            chargebackfields = "date_format(from_unixtime(dtstamp),'%b-%y') as month,sum(chargebackamount) as amount,max(dtstamp) as dtstamp";

            //qwipi
            //query.append("select " + fields + " from transaction_qwipi wherne");

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
            query.append(" toid = '"+ESAPI.encoder().encodeForSQL(me,terminalVO.getMemberId())+"'");
            query.append(" AND terminalid = '"+ESAPI.encoder().encodeForSQL(me,terminalVO.getTerminalId())+"'");
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

                query.append(" toid = '"+ESAPI.encoder().encodeForSQL(me,terminalVO.getMemberId())+"'");
                query.append(" AND terminalid = '"+ESAPI.encoder().encodeForSQL(me,terminalVO.getTerminalId())+"'");
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
            //query.append("select " + fields + " from transaction_ecore where");
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
            query.append(" toid = '"+ESAPI.encoder().encodeForSQL(me,terminalVO.getMemberId())+"'");
            query.append(" AND terminalid = '"+ESAPI.encoder().encodeForSQL(me,terminalVO.getTerminalId())+"'");
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

                query.append(" toid = '"+ESAPI.encoder().encodeForSQL(me,terminalVO.getMemberId())+"'");
                query.append(" AND terminalid = '"+ESAPI.encoder().encodeForSQL(me,terminalVO.getTerminalId())+"'");
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
            //query.append("select " + fields + " from transaction_common where");
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
            query.append(" toid = '"+ESAPI.encoder().encodeForSQL(me,terminalVO.getMemberId())+"'");
            query.append(" AND terminalid = '"+ESAPI.encoder().encodeForSQL(me,terminalVO.getTerminalId())+"'");
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
            //For Authsuccessful
            if(chartType.equals("salesChart"))
            {
                query.append(" UNION ");
                query.append("select " + fields + " from transaction_common where");

                query.append(" toid = '"+ESAPI.encoder().encodeForSQL(me,terminalVO.getMemberId())+"'");
                query.append(" AND terminalid = '"+ESAPI.encoder().encodeForSQL(me,terminalVO.getTerminalId())+"'");
                query.append(" AND dtstamp>= '"+ESAPI.encoder().encodeForSQL(me,fdtstamp)+"'");
                query.append(" AND dtstamp<= '"+ESAPI.encoder().encodeForSQL(me,tdtstamp)+"'");
                query.append(" AND currency= '"+ESAPI.encoder().encodeForSQL(me,currency)+"'");
                if(chartType.equals("salesChart")){
                    query.append(" and status='authsuccessful'");
                }
                query.append(" group by month");
            }

            //System.out.println("Inside if condition"+query);
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
                //System.out.println("Inside else condition");
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

            //System.out.println("ChartString ------->"+chartString.toString());
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

    private String validateOptionalParameters(HttpServletRequest req)
    {
        String error = "";
        String EOL = "<BR>";
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.MEMBERID);
        inputFieldsListOptional.add(InputFields.CURRENCY);
        inputFieldsListOptional.add(InputFields.FDATE);
        inputFieldsListOptional.add(InputFields.TDATE);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req, inputFieldsListOptional,errorList,true);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }
}
