import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.dao.GatewayAccountDAO;
import com.payment.validators.AbstractGatewayAccountInputValidator;
import com.payment.validators.GatewayAccountInputValidatorFactory;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 1/24/14
 * Time: 1:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class addGatewayAccounts extends HttpServlet
{
    static Logger logger = new Logger(addGatewayAccounts.class.getName());
    boolean flag = true;
    StringBuilder sb = new StringBuilder();
    String token = "";
    String result = "";

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
            logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        boolean flag = true;
        String EOL = "<BR>";
        String errormsg = "<center><font class=\"text\" face=\"arial\"><b>" + "Following information are incorrect:-" + EOL + "</b></font></center>";

        //String pgtypeid = "";
        String pgtypeidNew="";
        List<String> gatewayList = new ArrayList();
        String merchantid = "";
        String aliasName = "";
        String displayName = "";
        Integer isMasterCardSupported = 0;
        String site = "";
        String shortname = "";
        String path = "";
        //String username = "";
        String passwd = "";
        String changeback_path = "";
        String isCVVrequired = "";
        Double monthly_card_limit = 0.0;
        Double daily_amount_limit = 0.0;
        Double monthly_amount_limit = 0.0;
        Double daily_card_limit = 0.0;
        Double weekly_card_limit = 0.0;
        Double min_transaction_amount = 0.0;
        Double max_transaction_amount = 0.0;
        Double daily_card_amount_limit = 0.0;
        Double weekly_card_amount_limit = 0.0;
        Double monthly_card_amount_limit = 0.0;
        String daily_amount_range=null;
        String tableName = "";
        String columnName = "";
        String isTest = "";
        String isActive = "";
        String emiSupport="";
        String isMultipleRefund = "";
        String PartialRefund ="";
        String addressValidation="";
        String agentid = "";
        String partnerid = "";
        Double weekly_amount_limit = 0.0;
        String isRecurring = "";
        String isDynamicDescriptor = "";
        String is3dSupportAccount = "";
        String fromAccountId = "";
        String fromMid = "";
        String cardLimitCheck=req.getParameter("cardLimitCheck");
        String cardAmountlimitCheck=req.getParameter("cardAmountLimitCheck");
        String amountLimitCheck=req.getParameter("amountLimitCheck");
        String threeDsVersion=req.getParameter("threeDsVersion");
        String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;
        pgtypeidNew = req.getParameter("bankid");

        String daily_card_limit_check = req.getParameter("daily_card_limit_check");
        String weekly_card_limit_check = req.getParameter("weekly_card_limit_check");
        String monthly_card_limit_check = req.getParameter("monthly_card_limit_check");
        String Daily_Account_Amount_Limit = req.getParameter("daily_amount_limit_check");
        String Weekly_Account_Amount_Limit = req.getParameter("weekly_amount_limit_check");
        String Monthly_Account_Amount_Limit = req.getParameter("monthly_amount_limit_check");
        String daily_card_amount_limit_check= req.getParameter("daily_card_amount_limit_check");
        String weekly_card_amount_limit_check=req.getParameter("weekly_card_amount_limit_check");
        String monthly_card_amount_limit_check=req.getParameter("monthly_card_amount_limit_check");
        String daily_amount_range_check= req.getParameter("daily_amount_range_check");
        String daily_amount_rangeValue="";
        Functions functions= new Functions();

        String[] elements_pgtypeid = pgtypeidNew.split(",");
        gatewayList = Arrays.asList(elements_pgtypeid);

        token = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
        String isSubmitted = req.getParameter("isSubmitted");
        try
        {
            if ("true".equals(isSubmitted))
            {
                if (req.getParameter("columnnames") != null && req.getParameter("columnnames").length() > 0)
                {

                    if (!ESAPI.validator().isValidInput("columnnames", (String) req.getParameter("columnnames"), "SafeString", 500, false) || functions.hasHTMLTags((String) req.getParameter("columnnames")))
                    {
                        logger.debug("Invalid column names.");
                        errormsg = "<center><font class=\"text\" face=\"arial\"><b>" + errormsg + "Invalid column names." + EOL + "</b></font></center>";
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
                        if (functions.isValueNull(tempColumn) && !tempColumn.equalsIgnoreCase("id") && !tempColumn.equalsIgnoreCase("accountid"))
                        {
                            String isOptional=req.getParameter(tempColumn+"_isOptional");
                            String columnSize=req.getParameter(tempColumn+"_size");
                            if (functions.hasHTMLTags(req.getParameter(tempColumn)) || (!ESAPI.validator().isValidInput(tempColumn, req.getParameter(tempColumn), "SafeString", Integer.parseInt(columnSize), Boolean.parseBoolean(isOptional))))
                            {
                                logger.debug("Invalid column names.");
                                errormsg = "<center><font class=\"text\" face=\"arial\"><b>" + errormsg + "Invalid "+ tempColumn + EOL + "</b></font></center>";
                                flag = false;
                            }
                        }
                    }
                }
                if (!ESAPI.validator().isValidInput("gatewaytablename", (String) req.getParameter("gatewaytablename"), "SafeString", 50, true))
                {
                    logger.debug("Invalid gateway table name.");
                    errormsg = "<center><font class=\"text\" face=\"arial\"><b>" + errormsg + "Invalid gateway table name." + EOL + "</b></font></center>";
                    flag = false;
                }
                else
                {
                    tableName = req.getParameter("gatewaytablename");
                }
                /*if (!ESAPI.validator().isValidInput("pgtypeid", (String) req.getParameter("pgtypeid"), "Numbers", 10, false))
                {
                    logger.debug("Invalid gateway type.");
                    errormsg = "<center><font class=\"text\" face=\"arial\"><b>" + errormsg + "Invalid gateway type." + EOL + "</b></font></center>";
                    flag = false;
                }
                else
                {
                    *//*pgtypeidNew = req.getParameter("pgtypeid");*//*
                    pgtypeidNew1 = pgtypeid;
                }*/
                if (!(ESAPI.validator().isValidInput("merchantid", (String) req.getParameter("merchantid"), "merchantid", 50, false) && req.getParameter("merchantid").length() > 0))
                {
                    logger.debug("Invalid merchantid.");
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>Invalid merchantid.</b></font></center>" + EOL;
                    flag = false;
                }
                else
                {
                    merchantid = req.getParameter("merchantid");
                }
                if (!(ESAPI.validator().isValidInput("aliasname", (String) req.getParameter("aliasname"), "SafeString", 100, false) && req.getParameter("aliasname").length() > 0) || functions.hasHTMLTags((String) req.getParameter("aliasname")))
                {
                    logger.debug("Invalid aliasname.");
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>Invalid aliasname.</b></font></center>" + EOL;
                    flag = false;
                }
                else
                {
                    aliasName = req.getParameter("aliasname");
                }
                if (!(ESAPI.validator().isValidInput("displayname", (String) req.getParameter("displayname"), "SafeString", 255, false) && req.getParameter("displayname").length() > 0) || functions.hasHTMLTags((String) req.getParameter("displayname")))
                {
                    logger.debug("Invalid  displayname.");
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>Invalid  displayname.</b></font></center>" + EOL;
                    flag = false;
                }
                else
                {
                    displayName = req.getParameter("displayname");
                }
                if (!(ESAPI.validator().isValidInput("ismastercardsupported", req.getParameter("ismastercardsupported"), "Numbers", 1, true) && req.getParameter("ismastercardsupported").length() > 0))
                {
                    logger.debug("Invalid ismastercardsupported.");
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>Invalid ismastercardsupported. </b></font></center>" + EOL;
                    flag = false;
                }
                else
                {
                    String temp = req.getParameter("ismastercardsupported");
                    isMasterCardSupported = Integer.parseInt(temp);
                }
                if (!(ESAPI.validator().isValidInput("shortname", (String) req.getParameter("shortname"), "SafeString", 40, true)) || functions.hasHTMLTags((String) req.getParameter("shortname")))
                {
                    logger.debug("Invalid shortname ");
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>Invalid shortname." + EOL + "</b></font></center>" + EOL;
                    flag = false;
                }
                else
                {
                    shortname = req.getParameter("shortname");
                }
                if (!(ESAPI.validator().isValidInput("site", (String) req.getParameter("site"), "SafeString", 100, true)) || functions.hasHTMLTags((String) req.getParameter("site")))
                {
                    logger.debug("Invalid site");
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>Invalid site.</b></font></center>" + EOL;
                    flag = false;
                }
                else
                {
                    site = req.getParameter("site");
                }
                if (!(ESAPI.validator().isValidInput("path", (String) req.getParameter("path"), "SafeString", 1000, true)) || functions.hasHTMLTags((String) req.getParameter("path")))
                {
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>Invalid path." + EOL + "</b></font></center>" + EOL;
                    flag = false;
                }
                else
                {
                    path = req.getParameter("path");
                }
                if (!(ESAPI.validator().isValidInput("username", (String) req.getParameter("username"), "SafeString", 50, true)) || functions.hasHTMLTags((String) req.getParameter("username")))
                {
                    logger.debug("Invalid username.");
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>Invalid username.</b></font></center>" + EOL;
                    flag = false;
                }
                else
                {
                    username = req.getParameter("username");
                }
                if (!(ESAPI.validator().isValidInput("passwd", (String) req.getParameter("passwd"), "SafeString", 255, true)) || functions.hasHTMLTags((String) req.getParameter("passwd")))
                {
                    logger.debug("Invalid password.");
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>Invalid password.</b></font></center>" + EOL;
                    flag = false;
                }
                else
                {
                    passwd = req.getParameter("passwd");
                }
                if (!(ESAPI.validator().isValidInput("chargebackpath", (String) req.getParameter("chargebackpath"), "SafeString", 100, true)) || functions.hasHTMLTags((String) req.getParameter("chargebackpath")))
                {
                    logger.debug("Invalid chargeback path.");
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>Invalid chargeback path.</b></font></center>" + EOL;
                    flag = false;
                }
                else
                {
                    changeback_path = req.getParameter("chargebackpath");
                }
                if (!(ESAPI.validator().isValidInput("iscvvrequired", (String) req.getParameter("iscvvrequired"), "SafeString", 1, true)))
                {
                    logger.debug("Invalid isCVVRequired ");
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>Invalid isCVVRequired.</b></font></center>" + EOL;
                    flag = false;
                }
                else
                {
                    isCVVrequired = req.getParameter("iscvvrequired");
                }
                if (!(ESAPI.validator().isValidInput("monthlycardlimit", (String) req.getParameter("monthlycardlimit"), "AmountStr", 10, true)))
                {
                    logger.debug("Invalid monthly card limit ");
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>Invalid monthly card limit.</b></font></center>" + EOL;
                    flag = false;
                }
                else
                {
                    if (req.getParameter("monthlycardlimit").length() > 0)
                        monthly_card_limit = Double.parseDouble(req.getParameter("monthlycardlimit"));
                }
                if (!(ESAPI.validator().isValidInput("dailyamountlimit", req.getParameter("dailyamountlimit"), "AmountStr", 10, true)))
                {
                    logger.debug("Invalid daily amount limit.");
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>Invalid daily amount limit.</b></font></center>" + EOL;
                    flag = false;
                }
                else
                {
                    if (req.getParameter("dailyamountlimit").length() > 0)
                        daily_amount_limit = Double.parseDouble(req.getParameter("dailyamountlimit"));
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
                    logger.error("Invalid daily amount range.");
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
                if (!(ESAPI.validator().isValidInput("weeklyamountlimit", req.getParameter("weeklyamountlimit"), "AmountStr", 10, true)))
                {
                    logger.debug("Invalid weekly amount limit ");
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>Invalid weekly amount limit.</b></font></center>" + EOL;
                    flag = false;
                }
                else
                {
                    if (req.getParameter("weeklyamountlimit").length() > 0)
                        weekly_amount_limit = Double.parseDouble(req.getParameter("weeklyamountlimit"));
                }
                if (!(ESAPI.validator().isValidInput("monthlyamountlimit", (String) req.getParameter("monthlyamountlimit"), "AmountStr", 10, true)))
                {
                    logger.debug("Invalid monthly amount limit");
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>Invalid Monthly Amount limit.</b></font></center>" + EOL;
                    flag = false;
                }
                else
                {
                    if (req.getParameter("monthlyamountlimit").length() > 0)
                        monthly_amount_limit = Double.parseDouble(req.getParameter("monthlyamountlimit"));
                }
                if (!(ESAPI.validator().isValidInput("dailycardlimit", (String) req.getParameter("dailycardlimit"), "AmountStr", 10, true)))
                {
                    logger.debug("Invalid daily card limit ");
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>Invalid daily card limit .</b></font></center>" + EOL;
                    flag = false;
                }
                else
                {
                    if (req.getParameter("dailycardlimit").length() > 0)
                        daily_card_limit = Double.parseDouble(req.getParameter("dailycardlimit"));
                }
                if (!(ESAPI.validator().isValidInput("weeklycardlimit", (String) req.getParameter("weeklycardlimit"), "AmountStr", 10, true)))
                {
                    logger.debug("Invalid weekly card limit");
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>Invalid weekly card limit.</b></font></center>" + EOL;
                    flag = false;
                }
                else
                {
                    if (req.getParameter("weeklycardlimit").length() > 0)
                        weekly_card_limit = Double.parseDouble(req.getParameter("weeklycardlimit"));
                }
                if (!(ESAPI.validator().isValidInput("mintransactionamount", (String) req.getParameter("mintransactionamount"), "AmountStr", 10, true)))
                {
                    logger.debug("Invalid min transaction amount.");
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>Invalid min transaction amount.</b></font></center>" + EOL;
                    flag = false;
                }
                else
                {
                    if (req.getParameter("mintransactionamount").length() > 0)
                        min_transaction_amount = Double.parseDouble(req.getParameter("mintransactionamount"));
                }

                if (!(ESAPI.validator().isValidInput("maxtransactionamount", (String) req.getParameter("maxtransactionamount"), "AmountStr", 10, true)))
                {
                    logger.debug("Invalid max transaction amount ");
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>Invalid max transaction amount .</b></font></center>" + EOL;
                    flag = false;
                }
                else
                {
                    if (req.getParameter("maxtransactionamount").length() > 0)
                        max_transaction_amount = Double.parseDouble(req.getParameter("maxtransactionamount"));
                }
                if (!(ESAPI.validator().isValidInput("dailycardamountlimit", (String) req.getParameter("dailycardamountlimit"), "AmountStr", 10, true)))
                {
                    logger.debug("Invalid daily card amount limit");
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>Invalid daily card amount limit .</b></font></center>" + EOL;
                    flag = false;
                }
                else
                {
                    if (req.getParameter("dailycardamountlimit").length() > 0)
                        daily_card_amount_limit = Double.parseDouble(req.getParameter("dailycardamountlimit"));
                }
                if (!(ESAPI.validator().isValidInput("weeklycardamountlimit", (String) req.getParameter("weeklycardamountlimit"), "AmountStr", 10, true)))
                {
                    logger.debug("Invalid weekly card amount limit.");
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>Invalid weekly card amount limit.</b></font></center>" + EOL;
                    flag = false;
                }
                else
                {
                    if (req.getParameter("weeklycardamountlimit").length() > 0)
                        weekly_card_amount_limit = Double.parseDouble(req.getParameter("weeklycardamountlimit"));
                }
                if (!(ESAPI.validator().isValidInput("monthlycardamountlimit", (String) req.getParameter("monthlycardamountlimit"), "AmountStr", 10, true)))
                {
                    logger.debug("Invalid monthly card amount limit.");
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>Invalid monthly card amount limit.</b></font></center>" + EOL;
                    flag = false;
                }
                else
                {
                    if (req.getParameter("monthlycardamountlimit").length() > 0)
                        monthly_card_amount_limit = Double.parseDouble(req.getParameter("monthlycardamountlimit"));
                }
                if (!ESAPI.validator().isValidInput("istest", (String) req.getParameter("istest"), "SafeString", 1, false) && req.getParameter("istest").length() > 0)
                {
                    logger.debug("Invalid istest account ");
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>Invalid istest option.</b></font></center>" + EOL;
                    flag = false;
                }
                else
                {
                    isTest = req.getParameter("istest");
                }
                if (!ESAPI.validator().isValidInput("isactive", (String) req.getParameter("isactive"), "SafeString", 1, false) && req.getParameter("isactive").length() > 0)
                {
                    logger.debug("Invalid isactive account ");
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>Invalid isactive option.</b></font></center>" + EOL;
                    flag = false;
                }
                else
                {
                    isActive = req.getParameter("isactive");
                }
                if (!ESAPI.validator().isValidInput("emiSupport", (String) req.getParameter("emiSupport"), "SafeString", 1, false) && req.getParameter("emiSupport").length() > 0)
                {
                    logger.debug("Invalid emiSupport ");
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>Invalid emiSupport.</b></font></center>" + EOL;
                    flag = false;
                }
                else
                {
                    emiSupport = req.getParameter("emiSupport");
                    logger.debug("Invalid emiSupport "+emiSupport);
                }
                if (!ESAPI.validator().isValidInput("isMultipleRefund", (String) req.getParameter("isMultipleRefund"), "SafeString", 1, false) && req.getParameter("isMultipleRefund").length() > 0)
                {
                    logger.debug("Invalid isMultipleRefund ");
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>Invalid isMultipleRefund.</b></font></center>" + EOL;
                    flag = false;
                }
                else
                {
                    isMultipleRefund = req.getParameter("isMultipleRefund");

                }

                if (!ESAPI.validator().isValidInput("PartialRefund", (String) req.getParameter("PartialRefund"), "SafeString", 1, false) && req.getParameter("PartialRefund").length() > 0)
                {
                    logger.debug("Invalid PartialRefund ");
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>Invalid PartialRefund.</b></font></center>" + EOL;
                    flag = false;
                }
                else
                {
                    PartialRefund = req.getParameter("PartialRefund");
                }
                if (!ESAPI.validator().isValidInput("addressValidation", (String) req.getParameter("addressValidation"), "SafeString", 1, false) && req.getParameter("addressValidation").length() > 0)
                {
                    logger.debug("Invalid Address Validation ");
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>Invalid Address Validation.</b></font></center>" + EOL;
                    flag = false;
                }
                else
                {
                    System.out.println("inside else part of valid address");
                    addressValidation = req.getParameter("addressValidation");
                }
                if (!ESAPI.validator().isValidInput("agentid", req.getParameter("agentid"), "Numbers", 10, false))
                {
                    logger.debug("Invalid agentid ");
                    errormsg = "<center><font class=\"textb\"><b>" + errormsg + "Invalid agentid ." + EOL + "</b></font></center>";
                    flag = false;
                }
                else
                {
                    agentid = req.getParameter("agentid");

                }
                if (!ESAPI.validator().isValidInput("partnerid", req.getParameter("partnerid"), "Numbers", 10, false))
                {
                    logger.debug("Invalid partnerid");
                    errormsg = "<center><font class=\"textb\"><b>" + errormsg + "Invalid partnerid." + EOL + "</b></font></center>";
                    flag = false;
                }
                else
                {
                    partnerid = req.getParameter("partnerid");
                }
                if (!ESAPI.validator().isValidInput("isrecurring", (String) req.getParameter("isrecurring"), "SafeString", 1, false) && req.getParameter("isrecurring").length() > 0)
                {
                    logger.debug("Invalid isRecurring.");
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>Invalid isRecurring.</b></font></center>" + EOL;
                    flag = false;
                }
                else
                {
                    isRecurring = req.getParameter("isrecurring");
                }

                if (!ESAPI.validator().isValidInput("isDynamicDescriptor", (String) req.getParameter("isDynamicDescriptor"), "SafeString", 1, false) && req.getParameter("isDynamicDescriptor").length() > 0)
                {
                    logger.debug("Invalid isDynamicDescriptor.");
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>Invalid isDynamicDescriptor.</b></font></center>" + EOL;
                    flag = false;
                }
                else
                {
                    isDynamicDescriptor = req.getParameter("isDynamicDescriptor");
                }
                if (!ESAPI.validator().isValidInput("3dSupportAccount", (String) req.getParameter("3dSupportAccount"), "SafeString", 1, false) && req.getParameter("3dSupportAccount").length() > 0)
                {
                    logger.debug("Invalid  3dSupportAccount.");
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>Invalid 3dSupportAccount.</b></font></center>" + EOL;
                    flag = false;
                }
                else
                {
                    is3dSupportAccount = req.getParameter("3dSupportAccount");
                }

                if (!ESAPI.validator().isValidInput("fromAccountId", req.getParameter("fromAccountId"), "Numbers", 50, true))
                {
                    logger.debug("Invalid fromAccountId");
                    errormsg = "<center><font class=\"textb\"><b>" + errormsg + "Invalid fromAccountId." + EOL + "</b></font></center>";
                    flag = false;
                }
                else
                {
                    fromAccountId = req.getParameter("fromAccountId");
                }

                if (!ESAPI.validator().isValidInput("fromMid", req.getParameter("fromMid"), "SafeString", 50, true)|| functions.hasHTMLTags((String) req.getParameter("fromMid")))
                {
                    logger.debug("Invalid fromMid");
                    errormsg = "<center><font class=\"textb\"><b>" + errormsg + "Invalid fromMid." + EOL + "</b></font></center>";
                    flag = false;
                }
                else
                {
                    fromMid = req.getParameter("fromMid");
                }
                /*if (!(ESAPI.validator().isValidInput("cardLimitCheck", (String) req.getParameter("cardLimitCheck"), "SafeString", 1, false)&& req.getParameter("cardLimitCheck").length() > 0))
                {
                    logger.debug("Invalid cardLimitCheck ");
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>Invalid CardLimitCheck.</b></font></center>" + EOL;
                    flag = false;

                }
                else
                {
                    cardLimitCheck = req.getParameter("cardLimitCheck");
                    System.out.println("LimitCheck:::"+cardLimitCheck);
                }*/

                for (String pgtypeid : gatewayList)
                {

                    AbstractGatewayAccountInputValidator abstractGatewayAccountInputValidator = GatewayAccountInputValidatorFactory.getRequestVOInstance(pgtypeid);
                    String errorMsg = abstractGatewayAccountInputValidator.gatewaySpecificAccountValidation(req);//dynamic binding.
                    if (functions.isValueNull(errorMsg))
                    {
                        errormsg = errormsg + errorMsg;
                        flag = false;
                    }

                    if (flag)
                    {

                        GatewayAccountDAO gatewayAccountDAO = new GatewayAccountDAO();
                        result = gatewayAccountDAO.addGatewayAccount(merchantid, pgtypeid, aliasName, displayName, isMasterCardSupported, shortname, site, path, username, passwd, changeback_path, isCVVrequired,
                                monthly_card_limit, daily_amount_limit, monthly_amount_limit, daily_card_limit, weekly_card_limit, min_transaction_amount, max_transaction_amount, daily_card_amount_limit,
                                weekly_card_amount_limit, monthly_card_amount_limit, tableName, columnName, req, isTest, isActive, isMultipleRefund, PartialRefund, emiSupport, partnerid, agentid, weekly_amount_limit, isRecurring, isDynamicDescriptor, is3dSupportAccount, cardLimitCheck, cardAmountlimitCheck, amountLimitCheck, actionExecutorId, actionExecutorName, fromAccountId, fromMid, threeDsVersion,daily_card_limit_check,weekly_card_limit_check,monthly_card_limit_check,Daily_Account_Amount_Limit,Weekly_Account_Amount_Limit,Monthly_Account_Amount_Limit,daily_card_amount_limit_check,weekly_card_amount_limit_check,monthly_card_amount_limit_check,addressValidation,
                                daily_amount_range,daily_amount_range_check);
                    }
                    else
                    {
                        result = errormsg;
                    }
                }
            }
        }
        catch (Exception e)
        {
            logger.error("Exception :::::" + e.getStackTrace());
            result = e.toString();
            flag = false;
        }
        req.setAttribute("message", result.toString());
        RequestDispatcher rd = req.getRequestDispatcher("/addNewGatewayAccount.jsp?message=" + result.toString() + "&ctoken=" + user.getCSRFToken());
        rd.forward(req, res);
    }


}