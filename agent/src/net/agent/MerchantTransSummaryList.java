package net.agent;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
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
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: Saurabh
 * Date: 3/4/14
 * Time: 6:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchantTransSummaryList extends HttpServlet
{
    private static Logger log = new Logger(MerchantTransSummaryList.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        log.debug("Entering in MerchantTransSummaryList");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        AgentFunctions agent=new AgentFunctions();
        Functions functions = new Functions();
        if (!agent.isLoggedInAgent(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/agent/logout.jsp");
            return;
        }

        String toid=null;
        StringBuffer error= new StringBuffer();
        String fromdate=null;
        String todate=null;

        RequestDispatcher rdError=req.getRequestDispatcher("/merchanttranssummarylist.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        RequestDispatcher rdSuccess= req.getRequestDispatcher("/merchanttranssummarylist.jsp?Success=YES&ctoken="+user.getCSRFToken());

        Calendar cal= Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date= null;

        String oError = validateOptionalParameters(req);
        error.append(oError);

        log.debug("From ::" + req.getParameter("fromdate") + " Todate::" + req.getParameter("todate"));
        fromdate=req.getParameter("fromdate");
        todate= req.getParameter("todate");

        String currency = req.getParameter("currency");
        log.debug("Currency in MerchantTransSummaryList----->"+currency);

        int merchantId = Integer.parseInt((String) session.getAttribute("merchantid"));

        String fdtstamp = "";
        String tdtstamp = "";
        String errorMsg = "";

        String agentid=(String)session.getAttribute("merchantid");
        Hashtable statushash=null;

        if (!ESAPI.validator().isValidInput("toid ",req.getParameter("toid"),"SafeString",10,true))
        {   log.debug("Invalid toid");
            error.append("Invalid toid");
        }
        else
        {
            toid= req.getParameter("toid");
            req.setAttribute("toid",toid);
        }

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
        if (functions.isEmptyOrNull(currency))
        {
            log.debug("enter valid currency");
            errorMsg= errorMsg+"Enter valid Currency. It should not be empty.<br>";
            req.setAttribute("currencyError", errorMsg);
        }


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

            log.debug("From date dd::"+fdate+" MM;;"+fmonth+" YY::"+fyear+" To date dd::"+tdate+" MM::"+tmonth+" YY::"+tyear);

            //conversion to dtstamp
            fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
            tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");

            statushash=getReport(toid, agentid, fdtstamp, tdtstamp, currency);
        }
        catch (SQLException systemError)
        {
            log.error("SQLException while collection status Report",systemError);
            error.append("Internal error while accessing the data");
        }
        catch (SystemError systemError)
        {
            log.error("SystemError while collection status Report",systemError);
            error.append("Internal error while accessing the data");
        }
        catch (ParseException e)
        {
            log.error("Invalid date format", e);
        }
        req.setAttribute("fdtstamp",fdtstamp);
        req.setAttribute("tdtstamp",tdtstamp);
        req.setAttribute("toid",toid);
        req.setAttribute("status_report",statushash);
        req.setAttribute("error",error.toString());
        rdSuccess.forward(req, res);
    }

    public Hashtable getReport(String toid,String agentid,String fdtstamp,String tdtstamp, String currency) throws SystemError,SQLException
    {
        Connection conn = null;
        ResultSet rs = null;
        StringBuffer query = new StringBuffer();
        AgentFunctions agent=new AgentFunctions();
        Functions functions = new Functions();
        //conn = Database.getConnection();
        conn = Database.getRDBConnection();
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        //qwipi
       /* query.append("select STATUS,COUNT(*) AS COUNT,SUM(amount) AS amount from transaction_qwipi where ");
        if(toid.equalsIgnoreCase("all"))
        {
            String allMemberid=agent.getAgentMemberList(agentid);
            query.append(" toid IN ("+ allMemberid+")");
        }
        else
        {
            query.append(" toid ='"+ESAPI.encoder().encodeForSQL(me,toid)+"'");
        }
        query.append(" AND dtstamp>= '"+ESAPI.encoder().encodeForSQL(me,fdtstamp)+"'");
        query.append(" AND dtstamp<= '"+ESAPI.encoder().encodeForSQL(me,tdtstamp)+"'");
        query.append(" AND currency= '"+ESAPI.encoder().encodeForSQL(me,currency)+"'");
        query.append(" group by status ");
        query.append(" UNION ALL ");

        //ecore
        query.append("select STATUS,COUNT(*) AS COUNT,SUM(amount) AS amount from transaction_ecore where ");
        if(toid.equalsIgnoreCase("all"))
        {
            String allMemberid=agent.getAgentMemberList(agentid);
            query.append(" toid IN ("+ allMemberid+")");
        }
        else
        {
            query.append(" toid ='"+ESAPI.encoder().encodeForSQL(me,toid)+"'");
        }
        query.append(" AND dtstamp>= '"+ESAPI.encoder().encodeForSQL(me,fdtstamp)+"'");
        query.append(" AND dtstamp<= '"+ESAPI.encoder().encodeForSQL(me,tdtstamp)+"'");
        query.append(" AND currency= '"+ESAPI.encoder().encodeForSQL(me,currency)+"'");
        query.append(" group by status ");
        query.append(" UNION ALL ");*/

        //common
        query.append("select STATUS,COUNT(*) AS COUNT,SUM(amount) AS amount from transaction_common where ");
        if(toid.equalsIgnoreCase("all"))
        {
            String allMemberid=agent.getAgentMemberList(agentid);
            query.append(" toid IN ("+ allMemberid+")");
        }
        else
        {
            query.append(" toid ='"+ESAPI.encoder().encodeForSQL(me,toid)+"'");
        }
        query.append(" AND dtstamp>= '"+ESAPI.encoder().encodeForSQL(me,fdtstamp)+"'");
        query.append(" AND dtstamp<= '"+ESAPI.encoder().encodeForSQL(me,tdtstamp)+"'");
        query.append(" AND currency= '"+ESAPI.encoder().encodeForSQL(me,currency)+"'");
        query.append(" group by status ");

        StringBuffer reportquery = new StringBuffer("select status, SUM(count) as count,SUM(amount) as amount from ( " + query + ")as temp group by status");
        StringBuffer countquery = new StringBuffer("select SUM(count) as count,SUM(amount) as grandtotal from ( " + query + ")as temp");

        log.debug("========i=="+reportquery.toString());
        log.debug("========j=="+countquery);

        Hashtable statusreport= new Hashtable();
        try
        {
            statusreport=Database.getHashFromResultSet(Database.executeQuery(reportquery.toString(), conn));
            rs = Database.executeQuery(countquery.toString(), conn);

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
        catch (SQLException e)
        {
            log.error("sql error while execute status report",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        log.debug(statusreport);
        return statusreport;

    }
    private String validateOptionalParameters(HttpServletRequest req)
    {
        //System.out.println("Inside Validation method::::::::::::::");
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


