import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.manager.enums.ActivityLogParameters;
import com.manager.vo.ActivityTrackerVOs;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.validators.AbstractGatewayAccountInputValidator;
import com.payment.validators.GatewayAccountInputValidatorFactory;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;


/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 3/9/14
 * Time: 4:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class editGatewayAccountsDetails extends HttpServlet
{
    static Logger log = new Logger(editGatewayAccountsDetails.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        boolean flag = true;
        String EOL = "<BR>";
        String errormsg = "<center><font class=\"text\" face=\"arial\"><b>"+"Following information are incorrect:-"+EOL+"</b></font></center>";
        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
        ActivityTrackerVOs activityTrackerVOs = new ActivityTrackerVOs();
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        String pgtypeid = "";
        String accountid = "";
        String merchantid = "";
        String aliasName = "";
        String displayName = "";
        Integer isMasterCardSupported = 0;
        String site ="";
        String shortname = "";
        String path ="";
        String username = "";
        String passwd = "";
        String changeback_path = "";
        String isCVVrequired ="";
        String DailyAccountAmountLimit = "";
        String WeeklyAccountAmountLimit = "";
        String MonthlyAccountAmountLimit = "";
        String daily_card_limit_check = "";
        String weekly_card_limit_check = "";
        String monthly_card_limit_check = "";
        String daily_card_amount_limit_check = "";
        String weekly_card_amount_limit_check = "";
        String monthly_card_amount_limit_check = "";
        Double monthly_card_limit =0.0;
        Double daily_amount_limit = 0.0;
        String daily_amount_range= "";
        String  daily_amount_rangeValue="";
        String daily_amount_range_check= req.getParameter("daily_amount_range_check");
        Double monthly_amount_limit= 0.0;
        Double daily_card_limit =0.0;
        Double weekly_card_limit = 0.0;
        Double min_transaction_amount = 0.0;
        Double max_transaction_amount = 0.0;
        Double daily_card_amount_limit = 0.0;
        Double weekly_card_amount_limit = 0.0;
        Double monthly_card_amount_limit = 0.0;
        StringBuilder sb = new StringBuilder();
        String oldDisplayName="";
        String tableName = "";
        String tableData = "";
        String action;
        String columnName = "";
        String subTablequery ="";
        String isTest="";
        String isActive="";
        String emiSupport="";
        String isMultipleRefund="";
        String PartialRefund="";
        String partnerid="";
        String agentid="";
        Double weeklyAmountLimit = 0.0;
        String isRecurring = "";
        String addressValidation = "";
        String isDynamicDescriptor = "";
        String is3dSupportAccount = "";
        String isForexMid="";
        String fromAccountId="";
        String fromMid="";
        String cardLimitCheck=req.getParameter("cardLimitCheck");
        String cardAmountLimitCheck=req.getParameter("cardAmountLimitCheck");
        String amountLimitCheck=req.getParameter("amountLimitCheck");
        String threeDsVersion=req.getParameter("threeDsVersion");
        String Login=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String onchangedValues = req.getParameter("onchangedvalue");
        Functions functions=new Functions();

        try
        {
           if(!ESAPI.validator().isValidInput("pgtypeid",(String) req.getParameter("pgtypeid"),"Numbers",10,false))
            {
                log.debug("Invalid gateway type.");
                errormsg = "<center><font class=\"textb\"><b>"+errormsg + "Invalid gateway type." + EOL +"</b></font></center>";
                flag = false;
            }
            else
            {
                pgtypeid = req.getParameter("pgtypeid");
            }

            if(!ESAPI.validator().isValidInput("accountid",(String) req.getParameter("accountid"),"Numbers",20,false))
            {
                log.debug("Invalid accountid.");
                errormsg = "<center><font class=\"textb\"><b>"+errormsg + "Invalid accountid." + EOL +"</b></font></center>";
                flag = false;
            }
            else
            {
                accountid = req.getParameter("accountid");
            }

            if(!ESAPI.validator().isValidInput("merchantid",(String) req.getParameter("merchantid"),"SafeString",50,false))
            {
                log.debug("Invalid merchantid.");
                errormsg = "<center><font class=\"textb\"><b>"+errormsg + "Invalid merchantid." + EOL +"</b></font></center>";
                flag = false;
            }
            else
            {
                merchantid = req.getParameter("merchantid");
            }
            if(req.getParameter("columnnames") != null && req.getParameter("columnnames").length() > 0)
            {

                if(!ESAPI.validator().isValidInput("columnnames",(String) req.getParameter("columnnames"),"SafeString",500,false))
                {
                    log.debug("Invalid column names.");
                    errormsg = "<center><font class=\"textb\"><b>"+errormsg + "Invalid column names." + EOL +"</b></font></center>";
                    flag = false;
                }
                else
                {
                    columnName = req.getParameter("columnnames");
                }
                String[] columnNameArray = columnName.split(",");

                for (int i = 0; i < columnNameArray.length; i++)
                {
                    String tempColumn = "";
                    tempColumn = columnNameArray[i];
                    if (functions.isValueNull(tempColumn))
                    {
                        String isOptional=req.getParameter(tempColumn+"_isOptional");
                        String columnSize=req.getParameter(tempColumn+"_size");
                        if (functions.hasHTMLTags(req.getParameter(tempColumn)) || (!ESAPI.validator().isValidInput(tempColumn, req.getParameter(tempColumn), "SafeString", Integer.parseInt(columnSize), Boolean.parseBoolean(isOptional))))
                        {
                            log.error("inside invalid");
                            errormsg = "<center><font class=\"text\" face=\"arial\"><b>" + errormsg + "Invalid "+ tempColumn + EOL + "</b></font></center>";
                            flag = false;
                        }
                    }
                }
            }
            if(!ESAPI.validator().isValidInput("gateway_table_name",(String) req.getParameter("gateway_table_name"),"SafeString",50,true))
            {
                log.debug("Invalid gateway table name.");
                errormsg = "<center><font class=\"textb\"><b>"+errormsg + "Invalid gateway table name." + EOL +"</b></font></center>";
                flag = false;
            }
            else
            {
                tableName = req.getParameter("gateway_table_name");
            }
            if(!ESAPI.validator().isValidInput("action",(String) req.getParameter("action"),"SafeString",15,false) )
            {
                log.debug("Invalid action.");
                errormsg = "<center><font class=\"textb\"><b>"+errormsg + "Invalid action." + EOL +"</b></font></center>";
                flag = false;
            }
            else
            {
                action = req.getParameter("action");
            }

            if(!(ESAPI.validator().isValidInput("aliasname",(String) req.getParameter("aliasname"),"SafeString",100,false)) || functions.hasHTMLTags((String) req.getParameter("aliasname")))
            {
                log.debug("Invalid aliasname.");
                errormsg = "<center><font class=\"textb\"><b>"+errormsg + "Invalid aliasname." + EOL +"</b></font></center>";
                flag = false;
            }
            else
            {
                aliasName = req.getParameter("aliasname");
            }

            if (!(ESAPI.validator().isValidInput("displayname",(String) req.getParameter("displayname"),"SafeString",255,false)) || functions.hasHTMLTags((String) req.getParameter("displayname")))
            {
                log.debug("Invalid displayname.");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid displayname."+EOL +"</b></font></center>";
                flag = false;
            }
            else
            {
                displayName = req.getParameter("displayname");
            }

            if (!ESAPI.validator().isValidInput("0ld_displayname",(String) req.getParameter("0ld_displayname"),"SafeString",50,false) || functions.hasHTMLTags((String) req.getParameter("0ld_displayname")))
            {
                log.debug("Invalid displayname");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Old Display Name. is empty"+EOL +"</b></font></center>";
                flag = false;
            }
            else
            {
                oldDisplayName = req.getParameter("0ld_displayname");
            }

            if (!(ESAPI.validator().isValidInput("ismastercardsupported",req.getParameter("ismastercardsupported"),"Numbers",1,false) && req.getParameter("ismastercardsupported").length() > 0))
            {   log.debug("Invalid ismastercardsupported.");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid ismastercardsupported. "+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                isMasterCardSupported = Integer.parseInt(req.getParameter("ismastercardsupported"));
            }
            if (!(ESAPI.validator().isValidInput("shortname",(String) req.getParameter("shortname"),"SafeString",100,true)) || functions.hasHTMLTags((String) req.getParameter("shortname")))
            {
                log.debug("Invalid shortname.");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid shortname."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                shortname = req.getParameter("shortname");
            }

            if (!(ESAPI.validator().isValidInput("site",(String) req.getParameter("site"),"SafeString",100,true))  || functions.hasHTMLTags((String) req.getParameter("site")))
            {
                log.debug("Invalid site.");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid site."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                site = req.getParameter("site");
            }

            if (!(ESAPI.validator().isValidInput("path",(String) req.getParameter("path"),"SafeString",1000,true)) || functions.hasHTMLTags((String) req.getParameter("path")))
            {
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "InValid path."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                path = req.getParameter("path");
            }

            if (!(ESAPI.validator().isValidInput("username",(String) req.getParameter("username"),"SafeString",50,true)) || functions.hasHTMLTags((String) req.getParameter("username")))
            {
                log.debug("Invalid username.");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid username."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                username = req.getParameter("username");
            }

            if (!(ESAPI.validator().isValidInput("passwd",(String) req.getParameter("passwd"),"SafeString",255,true)) || functions.hasHTMLTags((String) req.getParameter("passwd")))
            {
                log.debug("Invalid password.");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid password."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                passwd = req.getParameter("passwd");
            }


            if (!(ESAPI.validator().isValidInput("chargeback_path",(String) req.getParameter("chargeback_path"),"SafeString",100,true)) || functions.hasHTMLTags((String) req.getParameter("chargeback_path")))
            {
                log.debug("Invalid chargeback path.");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid chargeback path."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                changeback_path = req.getParameter("chargeback_path");
            }


            if (!(ESAPI.validator().isValidInput("isCVVrequired",(String) req.getParameter("isCVVrequired"),"SafeString",1,false)))
            {
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid isCVVrequired."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                isCVVrequired = req.getParameter("isCVVrequired");
            }

            if (!(ESAPI.validator().isValidInput("Daily_Account_Amount_Limit",req.getParameter("Daily_Account_Amount_Limit"),"SafeString",1,false))) {
                errormsg = "<center><font class =\"textb\"><b>"+ errormsg + "Invalid Daily Amount Limit Flag." + EOL+"</b></fomt></center>";
                flag = false;
            }else {
                DailyAccountAmountLimit = req.getParameter("Daily_Account_Amount_Limit");
            }

            if (!(ESAPI.validator().isValidInput("Weekly_Account_Amount_Limit",req.getParameter("Weekly_Account_Amount_Limit"),"SafeString",1,false))) {
                errormsg = "<center><font class =\"textb\"><b>"+ errormsg + "Invalid Weekly Amount Limit Flag." + EOL+"</b></fomt></center>";
                flag = false;
            }else {
                WeeklyAccountAmountLimit = req.getParameter("Weekly_Account_Amount_Limit");
            }

            if (!(ESAPI.validator().isValidInput("Monthly_Account_Amount_Limit",req.getParameter("Monthly_Account_Amount_Limit"),"SafeString",1,false))) {
                errormsg = "<center><font class =\"textb\"><b>"+ errormsg + "Invalid Monthly Amount Limit Flag." + EOL+"</b></fomt></center>";
                flag = false;
            }else {
                MonthlyAccountAmountLimit = req.getParameter("Monthly_Account_Amount_Limit");
            }

            if (!(ESAPI.validator().isValidInput("daily_card_limit_check", req.getParameter("daily_card_limit_check"),"SafeString",1,false))){
                errormsg = "<center><font class =\"textb\"><b>"+ errormsg + "Invalid Daily Card Limit Flag." + EOL+"</b></fomt></center>";
                flag = false;
            }else{
                daily_card_limit_check = req.getParameter("daily_card_limit_check");
            }

            if (!(ESAPI.validator().isValidInput("weekly_card_limit_check", req.getParameter("weekly_card_limit_check"),"SafeString",1,false))){
                errormsg = "<center><font class =\"textb\"><b>"+ errormsg + "Invalid Weekly Card Limit Flag." + EOL+"</b></fomt></center>";
                flag = false;
            }else{
                weekly_card_limit_check = req.getParameter("weekly_card_limit_check");
            }

            if (!(ESAPI.validator().isValidInput("monthly_card_limit_check", req.getParameter("monthly_card_limit_check"),"SafeString",1,false))){
                errormsg = "<center><font class =\"textb\"><b>"+ errormsg + "Invalid Monthly Card Limit Flag." + EOL+"</b></fomt></center>";
                flag = false;
            }else{
                monthly_card_limit_check = req.getParameter("monthly_card_limit_check");
            }

            if (!(ESAPI.validator().isValidInput("daily_card_amount_limit_check", req.getParameter("daily_card_amount_limit_check"),"SafeString",1,false))){
                errormsg = "<center><font class =\"textb\"><b>"+ errormsg + "Invalid Daily Card Amount Limit Flag." + EOL+"</b></fomt></center>";
                flag = false;
            }else{
                daily_card_amount_limit_check = req.getParameter("daily_card_amount_limit_check");
            }

            if (!(ESAPI.validator().isValidInput("weekly_card_amount_limit_check", req.getParameter("weekly_card_amount_limit_check"),"SafeString",1,false))){
                errormsg = "<center><font class =\"textb\"><b>"+ errormsg + "Invalid Weekly Card Amount Limit Flag." + EOL+"</b></fomt></center>";
                flag = false;
            }else{
                weekly_card_amount_limit_check = req.getParameter("weekly_card_amount_limit_check");
            }

            if (!(ESAPI.validator().isValidInput("monthly_card_amount_limit_check", req.getParameter("monthly_card_amount_limit_check"),"SafeString",1,false))){
                errormsg = "<center><font class =\"textb\"><b>"+ errormsg + "Invalid Monthly Card Amount Limit Flag." + EOL+"</b></fomt></center>";
                flag = false;
            }else{
                monthly_card_amount_limit_check = req.getParameter("monthly_card_amount_limit_check");
            }

            if (!(ESAPI.validator().isValidInput("monthly_card_limit",(String) req.getParameter("monthly_card_limit"),"Numbers",10,false)))
            {
                log.debug("Invalid monthly card limit.");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid monthly card limit."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                monthly_card_limit = Double.parseDouble(req.getParameter("monthly_card_limit"));
            }

            if (!(ESAPI.validator().isValidInput("weekly_amount_limit",req.getParameter("weekly_amount_limit"),"AmountStr",15,false)))
            {
                log.debug("Invalid weekly amount Limit.");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid weekly amount limit."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                weeklyAmountLimit = Double.parseDouble(req.getParameter("weekly_amount_limit"));
            }
            if (!(ESAPI.validator().isValidInput("daily_amount_limit",req.getParameter("daily_amount_limit"),"AmountStr",15,false)))
            {
                log.debug("Invalid daily amount limit.");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid daily amount limit."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                daily_amount_limit = Double.parseDouble(req.getParameter("daily_amount_limit"));
            }
            if(functions.isValueNull(req.getParameter("daily_amount_range")))
                daily_amount_rangeValue= req.getParameter("daily_amount_range");

            String [] array= new String[5];
            if (functions.isValueNull(daily_amount_rangeValue) && daily_amount_rangeValue.contains("-"))
            {
                array=daily_amount_rangeValue.split("-");
            }
            String dailyamount1= array[0];
            String dailyamount2= array[1];
            Double value1= Double.valueOf(dailyamount1);
            Double value2= Double.valueOf(dailyamount2);

            if (functions.isValueNull(daily_amount_rangeValue) && !daily_amount_rangeValue.contains("-"))
            {
                log.error("Invalid daily amount range.");
                errormsg+= "<center><font class=\"text\" face=\"arial\"><b>Invalid daily amount range.</b></font></center>" +EOL;
                flag= false;
            }
            else if (!functions.isValueNull(dailyamount1) && daily_amount_rangeValue.contains("-") || !ESAPI.validator().isValidInput("dailyamount1", dailyamount1, "AmountStr", 10, true))
            {
                errormsg+= "<center><font class=\"text\" face=\"arial\"><b>Invalid daily amount range.</b></font></center>" +EOL;
                flag= false;
            }
            else if (!functions.isValueNull(dailyamount2) && daily_amount_rangeValue.contains("-") || !ESAPI.validator().isValidInput("dailyamount2", dailyamount2, "AmountStr", 10, true))
            {
                errormsg+= "<center><font class=\"text\" face=\"arial\"><b>Invalid daily amount range.</b></font></center>" +EOL;
                flag= false;
            }
            else if (!(ESAPI.validator().isValidInput("daily_amount_range",req.getParameter("daily_amount_range"),"Numbers", 20,true))  )
            {
                errormsg+= "<center><font class=\"text\" face=\"arial\"><b>Invalid daily amount range.</b></font></center>" +EOL;
                flag= false;
            }
            else if(functions.isValueNull(daily_amount_range_check) && "Y".equals(daily_amount_range_check) && !functions.isValueNull(daily_amount_rangeValue))
            {
                errormsg+= "<center><font class=\"text\" face=\"arial\"><b>Invalid daily amount range.</b></font></center>" +EOL;
                flag= false;
            }
            else if(functions.isValueNull(dailyamount1) && functions.isValueNull(dailyamount2) && value1>value2 || dailyamount1.startsWith("0") || dailyamount2.startsWith("0"))
            {
                errormsg+= "<center><font class=\"text\" face=\"arial\"><b>Invalid daily amount range.</b></font></center>" +EOL;
                flag= false;
            }
            else
            {
                if (req.getParameter("daily_amount_range").length()>0)
                    daily_amount_range= req.getParameter("daily_amount_range");
            }
            if (!(ESAPI.validator().isValidInput("monthly_amount_limit",(String) req.getParameter("monthly_amount_limit"),"AmountStr",15,false)))
            {
                log.debug("Invalid monthly amount limit.");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid monthly amount limit."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                monthly_amount_limit = Double.parseDouble(req.getParameter("monthly_amount_limit"));
            }


            if (!(ESAPI.validator().isValidInput("daily_card_limit",(String) req.getParameter("daily_card_limit"),"Numbers",10,false) ))
            {
                log.debug("Invalid daily card limit.");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid daily card limit."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                daily_card_limit = Double.parseDouble(req.getParameter("daily_card_limit"));
            }


            if (!(ESAPI.validator().isValidInput("weekly_card_limit",(String) req.getParameter("weekly_card_limit"),"Numbers",10,false)))
            {
                log.debug("Invalid weekly card limit.");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid weekly card limit."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                weekly_card_limit = Double.parseDouble(req.getParameter("weekly_card_limit"));
            }


            if (!(ESAPI.validator().isValidInput("min_transaction_amount",(String) req.getParameter("min_transaction_amount"),"AmountStr",10,false)))
            {
                log.debug("Invalid min_transaction_amount.");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid min_transaction_amount."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                min_transaction_amount = Double.parseDouble(req.getParameter("min_transaction_amount"));
            }

            if (!ESAPI.validator().isValidInput("max_transaction_amount",(String) req.getParameter("max_transaction_amount"),"AmountStr",10,false))
            {
                log.debug("Invalid max_transaction_amount.");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid max_transaction_amount."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                max_transaction_amount = Double.parseDouble(req.getParameter("max_transaction_amount"));
            }

            if (!(ESAPI.validator().isValidInput("daily_card_amount_limit",(String) req.getParameter("daily_card_amount_limit"),"AmountStr",20,false)))
            {
                log.debug("Invalid daily card amount limit ");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid daily card amount limit."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                daily_card_amount_limit = Double.parseDouble(req.getParameter("daily_card_amount_limit"));
            }

            if (!(ESAPI.validator().isValidInput("weekly_card_amount_limit",(String) req.getParameter("weekly_card_amount_limit"),"AmountStr",20,false)))
            {
                log.debug("Invalid weekly card amount limit");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid weekly card amount limit."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                weekly_card_amount_limit = Double.parseDouble(req.getParameter("weekly_card_amount_limit"));
            }

            if (!(ESAPI.validator().isValidInput("monthly_card_amount_limit",(String) req.getParameter("monthly_card_amount_limit"),"AmountStr",20,false)))
            {
                log.debug("Invalid monthly card amount limit ");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid monthly card amount limit."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                monthly_card_amount_limit = Double.parseDouble(req.getParameter("monthly_card_amount_limit"));
            }

            if (!(ESAPI.validator().isValidInput("istest",(String) req.getParameter("istest"),"SafeString",1,false) ))
            {
                log.debug("Invalid istest.");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid isTest."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                isTest = req.getParameter("istest");
            }

            if (!(ESAPI.validator().isValidInput("isactive",(String) req.getParameter("isactive"),"SafeString",1,false) ))
            {
                log.debug("Invalid isactive ");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid isactive."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                isActive = req.getParameter("isactive");
            }
            if (!(ESAPI.validator().isValidInput("emiSupport",(String) req.getParameter("emiSupport"),"SafeString",1,false) ))
            {
                log.debug("Invalid emiSupport ");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid emiSupport."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                emiSupport = req.getParameter("emiSupport");
            }
            if (!(ESAPI.validator().isValidInput("isMultipleRefund",(String)req.getParameter("isMultipleRefund"),"SafeString",1,false) ))
            {
                log.debug("Invalid isMultipleRefund ");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid isMultipleRefund."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                isMultipleRefund = req.getParameter("isMultipleRefund");
            }
            if (!(ESAPI.validator().isValidInput("PartialRefund",(String)req.getParameter("PartialRefund"),"SafeString",1,false) ))
            {
                log.debug("Invalid PartialRefund ");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid PartialRefund."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                PartialRefund = req.getParameter("PartialRefund");
            }


            /*if (!ESAPI.validator().isValidInput("agentid",req.getParameter("agentid"),"Numbers",10,false) )
            {
                log.debug("Invalid agentid ");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid agentid."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                agentid = req.getParameter("agentid");

            }
            if (!ESAPI.validator().isValidInput("partnerid",req.getParameter("partnerid"),"Numbers",10,false) )
            {
                log.debug("Invalid partnerid ");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid partnerid."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                partnerid = req.getParameter("partnerid");
            }*/

            if (!(ESAPI.validator().isValidInput("isrecurring",(String) req.getParameter("isrecurring"),"SafeString",1,false) ))
            {
                log.debug("Invalid isrecurring ");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid isRecurring."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                isRecurring = req.getParameter("isrecurring");
            }
            if(!ESAPI.validator().isValidInput("addressValidation",(String) req.getParameter("addressValidation"),"SafeString",1,false))
            {
                log.debug("Invalid address details");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid address details."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                addressValidation = req.getParameter("addressValidation");
            }

            if(!ESAPI.validator().isValidInput("isDynamicDescriptor",(String) req.getParameter("isDynamicDescriptor"),"SafeString",1,false))
            {
                log.debug("Invalid isDynamicDescriptor");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid isDynamicDescriptor."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                isDynamicDescriptor = req.getParameter("isDynamicDescriptor");
            }

            if(!ESAPI.validator().isValidInput("3dSupportAccount",(String) req.getParameter("3dSupportAccount"),"SafeString",1,false))
            {
                log.debug("Invalid 3dSupportAccount");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid 3dSupportAccount."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                is3dSupportAccount = req.getParameter("3dSupportAccount");
            }
            if(!ESAPI.validator().isValidInput("isForexMid",(String) req.getParameter("isForexMid"),"SafeString",1,false))
            {
                log.debug("Invalid FX Mid");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid FX Mid."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                isForexMid = req.getParameter("isForexMid");
            }
            if (!(ESAPI.validator().isValidInput("fromAccountId",(String) req.getParameter("fromAccountId"),"Numbers",50,true)))
            {
                log.debug("Invalid From Account ID.");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid fromAccountId."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                fromAccountId = req.getParameter("fromAccountId");
            }

            if (!(ESAPI.validator().isValidInput("fromMid",(String) req.getParameter("fromMid"),"SafeString",50,true)))
            {
                log.debug("Invalid From MID.");
                errormsg = "<center><font class=\"textb\"><b>"+ errormsg + "Invalid fromMid."+EOL+"</b></font></center>";
                flag = false;
            }
            else
            {
                fromMid = req.getParameter("fromMid");
            }

            if(tableName.length() > 0 && !tableName.equalsIgnoreCase("null"))
            {
                if(columnName != null && columnName.length() > 0)
                {
                    String tempId = req.getParameter("id");
                    String tempAccountid = req.getParameter("accountid");
                    String[] columnNameArray  = columnName.split(",");

                    if(tempId.equalsIgnoreCase("id") && tempAccountid.equalsIgnoreCase(accountid))
                    {

                        StringBuilder columnValuesSb = new StringBuilder();
                        StringBuilder columnNamesSb = new StringBuilder();

                        columnNamesSb.append("insert into " + tableName + " (accountid,");
                        columnValuesSb.append(" values ('");
                        columnValuesSb.append(accountid);
                        columnValuesSb.append("',");

                        for(int i=2;i<columnNameArray.length;i++)
                        {
                            String tempColumn  = "";
                            String value = "";
                            tempColumn = columnNameArray[i];

                            if(req.getParameter(tempColumn) != null && req.getParameter(tempColumn).length() > 0)
                            {
                                value = req.getParameter(tempColumn);

                                if(value != null && value.length() > 0  && !value.equalsIgnoreCase("null"))
                                {
                                    columnNamesSb.append("`"+tempColumn+"`");
                                    columnNamesSb.append(",");

                                    columnValuesSb.append("'");
                                    columnValuesSb.append(ESAPI.encoder().encodeForSQL(me,value));
                                    columnValuesSb.append("',");
                                }
                            }
                        }


                        String tempColumn = columnNamesSb.toString();
                        String tempValues = columnValuesSb.toString();
                        tempColumn = tempColumn.substring(0,tempColumn.length()-1);
                        tempValues = tempValues.substring(0,tempValues.length()-1);

                        subTablequery = tempColumn + ")" + tempValues + ")";

                    }
                    else
                    {

                        sb.append(" update " + tableName + " set accountid = " + accountid + ",");

                        for(int i=0;i<columnNameArray.length;i++)
                        {
                            String tempColumn  = "";
                            String value = "";
                            tempColumn = columnNameArray[i];
                            if(req.getParameter(tempColumn) != null)
                            {
                                value = req.getParameter(tempColumn);
                                if(!"id".equalsIgnoreCase(tempColumn))
                                {
                                    if (value != null && !value.equalsIgnoreCase("null"))
                                    {
                                        sb.append("`" + tempColumn + "`='" + ESAPI.encoder().encodeForSQL(me,value) + "'");
                                        sb.append(",");
                                    }
                                }
                            }
                        }

                        String temp = sb.toString();
                        temp = temp.substring(0,temp.length()-1);
                        subTablequery = temp + " where accountid = " + accountid ;

                    }
                    log.error("subTablequery--->"+subTablequery);
                }
            }
        }
        catch (Exception e)
        {
            log.error("Exception :::::" + e.getMessage(),e);
            errormsg += "<center><font class=\"textb\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            flag = false;
        }

        AbstractGatewayAccountInputValidator abstractGatewayAccountInputValidator= GatewayAccountInputValidatorFactory.getRequestVOInstance(pgtypeid);
        String errorMsg=abstractGatewayAccountInputValidator.gatewaySpecificAccountValidation(req);
        if(functions.isValueNull(errorMsg))
        {
            errormsg=errormsg+errorMsg;
            flag=false;
        }
       /* if(flag)
        {
            GatewayAccountDAO gatewayAccountDAO=new GatewayAccountDAO();
            try
            {
                boolean isAvailable=gatewayAccountDAO.checkGatewayAccountAvailability(merchantid,pgtypeid,aliasName);
                if(isAvailable)
                {
                    errormsg = errormsg + "<font class=\"textb\" >" + "<center>" + "<b>" + "Gateway Accounts with same merchantId, pgtypeId, aliasName already exists" + "</b>" + "</center>" + "</font>" + "<BR>";
                    flag = false;
                }
            }
            catch (SQLException se){
                errormsg=errormsg+"<font class=\"textb\" >"+"<center>"+"<b>"+"Internal Error While Processing"+"</b>"+"</center>"+"</font>"+"<BR>";
                flag=false;
            }
            catch (SystemError se){
                errormsg=errormsg+"<font class=\"textb\" >"+"<center>"+"<b>"+"Internal Error While Processing"+"</b>"+"</center>"+"</font>"+"<BR>";
                flag=false;
            }
        }*/

        if(flag)
        {
            Connection conn = null;
            PreparedStatement ps = null;
            PreparedStatement preparedStatement =  null;
            PreparedStatement p1 = null;
            ResultSet rs = null;
            ResultSet rs1 = null;


            String query = "UPDATE gateway_accounts SET aliasname=?,displayname=?,ismastercardsupported=?,shortname=?,site=?,path=?,username=?,passwd=?,chargeback_path=?,isCVVrequired=?," +
                    "monthly_card_limit=?,daily_amount_limit=?,monthly_amount_limit=?,daily_card_limit=?,weekly_card_limit=?,min_transaction_amount=?,max_transaction_amount=?," +
                    "daily_card_amount_limit=?,weekly_card_amount_limit=?,monthly_card_amount_limit=?,istest=?,isactive=?,emiSupport=?,isMultipleRefund=?,PartialRefund=?,weekly_amount_limit=?," +
                    "is_recurring=?,addressValidation=?,isDynamicDescriptor=?,3dSupportAccount=?,isForexMid=?,cardLimitCheck=?,cardAmountLimitCheckAcc=?,amountLimitCheckAcc=?,fromAccountId=?," +
                    "fromMid=?,threeDsVersion=?,Daily_Account_Amount_Limit=?,Weekly_Account_Amount_Limit=?,Monthly_Account_Amount_Limit=?,daily_card_limit_check=?,weekly_card_limit_check=?," +
                    "monthly_card_limit_check=?,daily_card_amount_limit_check=?,weekly_card_amount_limit_check=?,monthly_card_amount_limit_check=?,daily_amount_range=?,daily_amount_range_check=? " +
                    "WHERE pgtypeid=? and accountid=?";
            try
            {
                conn = Database.getConnection();
                ps = conn.prepareStatement(query);
                ps.setString(1,aliasName);
                ps.setString(2,displayName);
                ps.setInt(3, isMasterCardSupported);
                ps.setString(4, shortname);
                ps.setString(5,site);
                ps.setString(6,path);
                ps.setString(7,username);
                ps.setString(8,passwd);
                ps.setString(9,changeback_path);
                ps.setString(10,isCVVrequired);
                ps.setDouble(11, monthly_card_limit);
                ps.setDouble(12,daily_amount_limit);
                ps.setDouble(13,monthly_amount_limit);
                ps.setDouble(14,daily_card_limit);
                ps.setDouble(15,weekly_card_limit);
                ps.setDouble(16,min_transaction_amount);
                ps.setDouble(17,max_transaction_amount);
                ps.setDouble(18,daily_card_amount_limit);
                ps.setDouble(19,weekly_card_amount_limit);
                ps.setDouble(20,monthly_card_amount_limit);
                ps.setString(21, isTest);
                ps.setString(22, isActive);
                ps.setString(23,emiSupport);
                ps.setString(24,isMultipleRefund);
                ps.setString(25,PartialRefund);
                ps.setDouble(26, weeklyAmountLimit);
                ps.setString(27, isRecurring);
                ps.setString(28, addressValidation);
                ps.setString(29,isDynamicDescriptor);
                ps.setString(30,is3dSupportAccount);
                ps.setString(31,isForexMid);
                ps.setString(32,cardLimitCheck);
                ps.setString(33,cardAmountLimitCheck);
                ps.setString(34,amountLimitCheck);
                ps.setString(35,fromAccountId);
                ps.setString(36,fromMid);
                ps.setString(37,threeDsVersion);
                ps.setString(38,DailyAccountAmountLimit);
                ps.setString(39,WeeklyAccountAmountLimit);
                ps.setString(40,MonthlyAccountAmountLimit);
                ps.setString(41,daily_card_limit_check);
                ps.setString(42,weekly_card_limit_check);
                ps.setString(43,monthly_card_limit_check);
                ps.setString(44,daily_card_amount_limit_check);
                ps.setString(45,weekly_card_amount_limit_check);
                ps.setString(46,monthly_card_amount_limit_check);
                ps.setString(47,daily_amount_range);
                ps.setString(48,daily_amount_range_check);
                ps.setString(49,pgtypeid);
                ps.setString(50,accountid);
                log.error("Query is :" + ps);
                log.debug("line 707");
                int i =ps.executeUpdate();
                if(i!=0)
                {
                    errormsg ="<font class=\"textb\" >"+"<center>"+"<b>"+"Record Updated Successfully"+"</b>"+"</center>"+"</font>"+"<BR>";
                    log.debug("Query-----------------"+subTablequery.toString());
                    int j = 0;
                    //if(columnName)
                    log.debug("Creating Activity for edit Gatway account");
                    String remoteAddr = Functions.getIpAddress(req);
                    int serverPort = req.getServerPort();
                    String servletPath = req.getServletPath();
                    String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;
                    if(functions.isValueNull(onchangedValues))
                    {
                        activityTrackerVOs.setInterface(ActivityLogParameters.ADMIN.toString());
                        activityTrackerVOs.setUser_name(Login + "-" + actionExecutorId);
                        activityTrackerVOs.setRole(ActivityLogParameters.ADMIN.toString());
                        activityTrackerVOs.setAction(ActivityLogParameters.EDIT.toString());
                        activityTrackerVOs.setModule_name(ActivityLogParameters.GATWAY_ACCOUNT_MASTER.toString());
                        activityTrackerVOs.setLable_values(onchangedValues);
                        activityTrackerVOs.setDescription(ActivityLogParameters.ACCOUNTID.toString() + "-" + accountid);
                        activityTrackerVOs.setIp(remoteAddr);
                        activityTrackerVOs.setHeader(header);
                        try
                        {
                            AsyncActivityTracker asyncActivityTracker = AsyncActivityTracker.getInstance();
                            asyncActivityTracker.asyncActivity(activityTrackerVOs);
                        }
                        catch (Exception e)
                        {
                            log.error("Exception while AsyncActivityLog::::", e);
                        }
                    }
                    if(subTablequery.length() > 0 )
                    {
                        PreparedStatement subTablePs = conn.prepareStatement(subTablequery);
                        //subTablePs.setString(1,accountid);
                        j = subTablePs.executeUpdate();

                            //errormsg ="<font class=\"textb\">"+"<center>"+"<b>"+"Error  Updating Second Table"+"</b>"+"</center>"+"</font>"+"<BR>";
                    }

                    if(!oldDisplayName.equalsIgnoreCase(displayName))
                    {
                        //prepare mail for superAdmin,Partner and partner's merchant.
                        String selectQry="SELECT * FROM member_account_mapping WHERE accountid=? AND isActive='Y' AND isTest='N'";
                        preparedStatement=conn.prepareStatement(selectQry);
                        preparedStatement.setString(1,accountid);
                        rs=preparedStatement.executeQuery();
                        while(rs.next())
                        {
                            String member=rs.getString("memberid");
                            String companyName="";
                            String selectcompany="select company_name,activation from members where memberid=? ";
                            p1=conn.prepareStatement(selectcompany);
                            p1.setString(1,member);
                            rs1=p1.executeQuery();
                            String activation="";
                            if(rs1.next())
                            {
                                companyName=rs1.getString("company_name");
                                activation= rs1.getString("activation");
                            }
                            if(activation.equalsIgnoreCase("Y"))
                            {
                                HashMap membermailDetails=new HashMap();
                                LinkedHashMap discriptorDetails=new LinkedHashMap();

                                discriptorDetails.put("Member ID",member);
                                discriptorDetails.put("Company Name",companyName);
                                discriptorDetails.put("Account ID",accountid);
                                discriptorDetails.put("OLD Billing Discriptor",oldDisplayName);
                                discriptorDetails.put("NEW Billing Discriptor",displayName);

                                membermailDetails.put(MailPlaceHolder.TOID,member);
                                //membermailDetails.put(MailPlaceHolder.MULTIPALTRANSACTION, mailService.getDetailTableForSingleTrans(discriptorDetails));
                                membermailDetails.put(MailPlaceHolder.MULTIPALTRANSACTION, asynchronousMailService.getDetailTableForSingleTrans(discriptorDetails));
                                asynchronousMailService.sendMerchantSignup(MailEventEnum.BILLING_DESCRIPTOR_CHANGE_INTIMATION, membermailDetails);
                            }
                        }
                    }
                }
                else
                {
                    errormsg ="<font class=\"textb\" >"+"<center>"+"<b>Update Failed"+"</b>"+"</center>"+"</font>"+"<BR><BR>";
                }
            }
            catch (SystemError systemError)
            {
                log.error("Sql Exception :::::",systemError);
                errormsg += "<center><font class=\"textb\"><b>"+ errormsg + systemError.getMessage() + EOL +"</b></font></center>";
            }
            catch (SQLException e)
            {
                log.error("Exception while updating  : ",e);
                errormsg += "<center><font class=\"textb\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            }
            finally
            {
                Database.closeResultSet(rs);
                Database.closeResultSet(rs1);
                Database.closePreparedStatement(ps);
                Database.closePreparedStatement(preparedStatement);
                Database.closePreparedStatement(p1);
                Database.closeConnection(conn);
            }

            try
            {
                GatewayAccountService.loadGatewayAccounts();
            }
            catch (Exception e)
            {
                log.error("Exception while getting GatewayAccountsDetails ",e);
            }

            req.setAttribute("message",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/gatewayAccountInterface.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        else
        {
            req.setAttribute("message",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/viewGatewayAccountDetails?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
    }
}