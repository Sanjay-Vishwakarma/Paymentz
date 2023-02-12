package net.partner;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.manager.enums.ActivityLogParameters;
import com.manager.vo.ActivityTrackerVOs;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
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
import java.util.*;

/**
 * Created by Sneha on 8/9/15.
 */
public class ActionGatewayAccounts extends HttpServlet
{
    private static Logger logger = new Logger(ActionGatewayAccounts.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doProcess(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doProcess(request, response);
    }

    public void doProcess(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        logger.debug("Inside ActionGatewayAccounts Partners---");
        HttpSession session     = req.getSession();
        User user               = (User) session.getAttribute("ESAPIUserSessionKey");

        PartnerFunctions partner = new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }

        Connection con                  = null;
        PreparedStatement pstmt         = null;
        PreparedStatement pstmtUpdate   = null;
        ResultSet rs                    = null;
        Hashtable hash                  = null;
        RequestDispatcher rd            = null;
        String accountid                = req.getParameter("accountid");
        String action                   = req.getParameter("submit");
        Functions functions             = new Functions();
        String modify                   = "";
        StringBuilder dataStringBuilder = new StringBuilder();
        String tableColumn  = "";
        String role         = "Admin";
        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
        ActivityTrackerVOs activityTrackerVOs           = new ActivityTrackerVOs();
        String actionExecutorId                         = (String) session.getAttribute("merchantid");
        String Login                                    = user.getAccountName();
        String onchangedValues = "";
        try
        {
            if(req.getParameter("onchangedvalue") != null ){
                onchangedValues = req.getParameter("onchangedvalue");
            }
            logger.error("onchangedValues >>>>>>>>> "+onchangedValues);

            if (action.equalsIgnoreCase("View") || action.equalsIgnoreCase("Edit"))
            {
                if (action.equalsIgnoreCase("View"))
                {
                    modify = "disabled";
                }

                con                 = Database.getConnection();
                StringBuffer qry    = new StringBuffer("select merchantid,aliasname,displayname,username,passwd,chargeback_path,isCVVrequired,shortname,site,path,3dSupportAccount,istest,isactive," +
                        "isMultipleRefund,PartialRefund,emiSupport,isForexMid,a.pgtypeid,t.currency,t.gateway,cardLimitCheck,cardAmountLimitCheckAcc,amountLimitCheckAcc,daily_card_limit," +
                        "weekly_card_limit,monthly_card_limit,daily_amount_limit,weekly_amount_limit,monthly_amount_limit,daily_card_amount_limit,weekly_card_amount_limit,monthly_card_amount_limit," +
                        "ismastercardsupported,is_recurring,addressValidation,isDynamicDescriptor,a.actionExecutorId,a.actionExecutorName,fromAccountId,fromMid,t.name,t.currency,a.threeDsVersion," +
                        "Daily_Account_Amount_Limit,Weekly_Account_Amount_Limit,Monthly_Account_Amount_Limit,daily_card_limit_check,weekly_card_limit_check,monthly_card_limit_check,daily_card_amount_limit_check," +
                        "weekly_card_amount_limit_check,monthly_card_amount_limit_check,daily_amount_range,daily_amount_range_check from gateway_accounts as a,gateway_type as t \n" +
                        "where a.pgtypeid=t.pgtypeid and accountid=?");
                pstmt = con.prepareStatement(qry.toString());
                pstmt.setString(1, accountid);
                // pstmt.setString(2,actionExecutorId);
                //pstmt.setString(3,actionExecutorName);
                rs                  = pstmt.executeQuery();
                hash                = Database.getHashFromResultSet(rs);
                Hashtable innerHash = (Hashtable) hash.get(1 + "");
                String pgtypeid     = (String) innerHash.get("pgtypeid");
                GatewayType gatewayType         = GatewayTypeService.getGatewayType(pgtypeid);
                String gateway_table_name       = gatewayType.getTableName();
                if (functions.isValueNull(gateway_table_name))
                {
                    PreparedStatement preparedStatementData = null;
                    ResultSet tableMetaDataRs               = null;
                    try
                    {
                        String tableDataQuery = "select * from " + gateway_table_name + " where accountid = ?";
                        preparedStatementData = con.prepareStatement(tableDataQuery);
                        preparedStatementData.setInt(1, Integer.parseInt(accountid));

                        tableMetaDataRs = preparedStatementData.executeQuery();
                        Integer count   = tableMetaDataRs.getMetaData().getColumnCount();

                        boolean containData = false;

                        while (tableMetaDataRs.next())
                        {
                            containData = true;
                            for (int i = 1; i <= count; i++)
                            {
                                String coulumnName      = tableMetaDataRs.getMetaData().getColumnName(i);
                                String columnNameLabel  = tableMetaDataRs.getMetaData().getColumnName(i);
                                int columnNameSize      = tableMetaDataRs.getMetaData().getColumnDisplaySize(i);
                                String columnType       = tableMetaDataRs.getMetaData().getColumnTypeName(i);
                                String temp             = tableMetaDataRs.getString(coulumnName);
                                if (!functions.isValueNull(temp))
                                {
                                    temp = "";
                                }
                                String isOptional="true";
                                columnNameLabel = columnNameLabel.substring(0, 1).toUpperCase() + columnNameLabel.substring(1);
                                if (tableMetaDataRs.getMetaData().isNullable(i) == 0)
                                {
                                    columnNameLabel = columnNameLabel.substring(0, 1).toUpperCase() + columnNameLabel.substring(1) + "*";
                                    isOptional="false";
                                }

                                dataStringBuilder.append("<tr>");
                                dataStringBuilder.append("<td align='center' valign='middle' style='color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;'>" + columnNameLabel + "</td>");
                                dataStringBuilder.append("<td align='center' colspan='2'>");
                                dataStringBuilder.append("<input type='text' name='" + coulumnName + "'  class='form-control' value='" + temp + "'" + modify + ">("+columnType+" - "+columnNameSize+")");
                                dataStringBuilder.append("<input type=\"hidden\" name=\""+coulumnName+"_isOptional\" value=\""+isOptional+"\">");
                                dataStringBuilder.append("<input type=\"hidden\" name=\""+coulumnName+"_size\" value=\""+columnNameSize+"\">");
                                dataStringBuilder.append("</td>");
                                dataStringBuilder.append("</tr>");
                                tableColumn += coulumnName + ",";
                            }
                        }

                        if (!containData)
                        {
                            tableDataQuery          = "select * from " + gateway_table_name + " where 1=1 limit 1";
                            preparedStatementData   = con.prepareStatement(tableDataQuery.toString());

                            tableMetaDataRs = preparedStatementData.executeQuery();
                            count           = tableMetaDataRs.getMetaData().getColumnCount();
                            while (tableMetaDataRs.next())
                            {
                                containData = true;
                                for (int i = 1; i <= count; i++)
                                {
                                    String coulumnName      = tableMetaDataRs.getMetaData().getColumnName(i);
                                    String columnNameLabel  = tableMetaDataRs.getMetaData().getColumnName(i);
                                    int columnNameSize      = tableMetaDataRs.getMetaData().getColumnDisplaySize(i);
                                    String columnType       = tableMetaDataRs.getMetaData().getColumnTypeName(i);
                                    String temp             = "";
                                    if (functions.isValueNull(tableMetaDataRs.getString(coulumnName)))
                                    {
                                        temp = tableMetaDataRs.getString(coulumnName);
                                    }
                                    columnNameLabel     = columnNameLabel.substring(0, 1).toUpperCase() + columnNameLabel.substring(1);
                                    String isOptional   = "true";
                                    if (tableMetaDataRs.getMetaData().isNullable(i) == 0)
                                    {
                                        columnNameLabel = columnNameLabel.substring(0, 1).toUpperCase() + columnNameLabel.substring(1) + "*";
                                        isOptional      = "false";
                                    }

                                    dataStringBuilder.append("<tr>");
                                    dataStringBuilder.append("<td align='center' valign='middle' style='color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;'>" + columnNameLabel + "</td>");
                                    dataStringBuilder.append("<td align='center' colspan='2'>");
                                    dataStringBuilder.append("<input type='text' name='" + coulumnName + "'  class='form-control' value='" + temp + "'" + modify + ">("+columnType+" - "+columnNameSize+")");
                                    dataStringBuilder.append("<input type=\"hidden\" name=\""+coulumnName+"_isOptional\" value=\""+isOptional+"\">");
                                    dataStringBuilder.append("<input type=\"hidden\" name=\""+coulumnName+"_size\" value=\""+columnNameSize+"\">");
                                    dataStringBuilder.append("</td>");
                                    dataStringBuilder.append("</tr>");
                                    tableColumn += coulumnName + ",";
                                }
                            }
                        }
                        if (tableColumn.length() > 0)
                        {
                            tableColumn = tableColumn.substring(0, tableColumn.length() - 1);
                        }
                    }
                    catch (Exception e)
                    {
                        logger.error("Exception while getting the gateway accounts details", e);
                    }
                    finally
                    {
                        Database.closeResultSet(tableMetaDataRs);
                        Database.closePreparedStatement(preparedStatementData);
                        Database.closeConnection(con);
                    }
                }

                req.setAttribute("gateway_table_name", gateway_table_name);
                req.setAttribute("table_data", dataStringBuilder.toString());
                req.setAttribute("table_column", tableColumn);
                req.setAttribute("pgtypeid", pgtypeid);
                req.setAttribute("hash", hash);
                req.setAttribute("accountid", accountid);
                req.setAttribute("action", action);
                rd = req.getRequestDispatcher("/actionGatewayAccounts.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
            }
            else if (action.equalsIgnoreCase("Update"))
            {
                StringBuffer statusMsg      = new StringBuffer();
                StringBuffer successMsg     = new StringBuffer();
                StringBuffer mid            = new StringBuffer(req.getParameter("mid"));
                StringBuffer pgtypeid       = new StringBuffer(req.getParameter("pgtypeid"));
                StringBuilder sb            = new StringBuilder();
                String tableName            = "";
                String columnName           = "";
                String subTablequery        = "";
                String oldDisplayName       = "";
                String displayName          = "";
                //StringBuffer isCVVrequired = new StringBuffer(req.getParameter("iscvvrequired"));
                String isCVVrequired = "Y";
                if (req.getParameterMap().containsKey("iscvvrequired"))
                {
                    isCVVrequired = req.getParameter("iscvvrequired");
                }
                StringBuffer mcc            = new StringBuffer(req.getParameter("mcc"));
                StringBuffer displayname    = new StringBuffer(req.getParameter("displayname"));
                StringBuffer username       = new StringBuffer(req.getParameter("username"));
                StringBuffer passwd         = new StringBuffer(req.getParameter("passwd"));
                StringBuffer shortName      = new StringBuffer(req.getParameter("shortname"));
                StringBuffer path           = new StringBuffer(req.getParameter("path"));
                StringBuffer chargeBackPath = new StringBuffer(req.getParameter("chargeBackPath"));
                StringBuffer fromAccountId  = new StringBuffer(req.getParameter("fromAccountId"));
                StringBuffer fromMid        = new StringBuffer(req.getParameter("fromMid"));
                String threeDsVersion       = req.getParameter("threeDsVersion");
                String is3dSupportAccount   = "N";
                String isTestAccount        = "N";
                String isActiveAccount      = "N";
                String isMultipleRefund     = "N";
                String PartialRefund        = "N";
                String emiSupport           = "N";
                String isForexMid           = "";
                int monthlyCardLimit        = 0;
                int dailyCardLimit          = 0;
                int weeklyCardLimit         = 0;

                Double dailyAmountLimit         = 0.00;
                Double monthlyAmountLimit       = 0.00;
                Double dailyCardAmountLimit     = 0.00;
                Double weeklyCardAmountLimit    = 0.00;
                Double monthlyCardAmountLimit   = 0.00;
                Double weeklyAmountLimit        = 0.00;
                String daily_amount_range_check= req.getParameter("daily_amount_range_check");
                String daily_amount_rangeValue="";
                String daily_amount_range=null;

                String DailyAccountAmountLimit      = "";
                String WeeklyAccountAmountLimit     = "";
                String MonthlyAccountAmountLimit    = "";
                String daily_card_limit_check       = "";
                String weekly_card_limit_check      = "";
                String monthly_card_limit_check         = "";
                String daily_card_amount_limit_check    = "";
                String weekly_card_amount_limit_check   = "";
                String monthly_card_amount_limit_check  = "";

                String cardLimitCheck           = req.getParameter("cardLimitCheck");
                String cardAmountlimitCheck     = req.getParameter("cardAmountLimitCheck");
                String amountLimitCheck         = req.getParameter("amountLimitCheck");
                int ismastercardsupported       = Integer.parseInt(req.getParameter("ismastercardsupported"));
                String isrecurring              = req.getParameter("isrecurring");
                String addressValidation        = req.getParameter("addressValidation");
                String isDynamicDescriptor      = req.getParameter("isDynamicDescriptor");
                String site = req.getParameter("site");

                if (req.getParameterMap().containsKey("is3dSupportAccount"))
                {
                    is3dSupportAccount = req.getParameter("is3dSupportAccount");
                }
                if (req.getParameterMap().containsKey("isTestAccount"))
                {
                    isTestAccount = req.getParameter("isTestAccount");
                }
                if (req.getParameterMap().containsKey("isActiveAccount"))
                {
                    isActiveAccount = req.getParameter("isActiveAccount");
                }
                if (req.getParameterMap().containsKey("isMultipleRefund"))
                {
                    isMultipleRefund = req.getParameter("isMultipleRefund");

                }
                if (req.getParameterMap().containsKey("PartialRefund"))
                {
                    PartialRefund = req.getParameter("PartialRefund");
                }
                if (req.getParameterMap().containsKey("emiSupport"))
                {
                    emiSupport = req.getParameter("emiSupport");

                }

                if (req.getParameterMap().containsKey("isForexMid"))
                {
                    isForexMid = req.getParameter("isForexMid");
                }
                con = Database.getConnection();

                //validation check
                if (!ESAPI.validator().isValidInput("mid", mid.toString(), "SafeString", 255, false) || functions.hasHTMLTags(mid.toString()))
                {
                    statusMsg.append("Invalid MerchantId<BR>");
                }
                if (!ESAPI.validator().isValidInput("pgtypeid", pgtypeid.toString(), "SafeString", 100, false) || functions.hasHTMLTags(pgtypeid.toString()))
                {
                    statusMsg.append("Invalid BankName<BR>");
                }
                if (!ESAPI.validator().isValidInput("mcc", mcc.toString(), "SafeString", 100, false) || functions.hasHTMLTags(mcc.toString()))
                {
                    statusMsg.append("Invalid MCC/Alias Name<BR>");
                }
                if (!(ESAPI.validator().isValidInput("site", site, "URL", 100, true)) || functions.hasHTMLTags(site))
                {
                    statusMsg.append("Invalid site<BR>");
                }
                if (!ESAPI.validator().isValidInput("displayname", displayname.toString(), "SafeString", 255, false) || functions.hasHTMLTags(displayname.toString()))
                {
                    statusMsg.append("Invalid Billing Descriptor<BR>");
                }
                if (!ESAPI.validator().isValidInput("username", username.toString(), "SafeString", 255, true) || functions.hasHTMLTags(username.toString()))
                {
                    statusMsg.append("Invalid User Name<BR>");
                }
                if (!ESAPI.validator().isValidInput("pwd", passwd.toString(), "SafeString", 255, true) || functions.hasHTMLTags(passwd.toString()))
                {
                    statusMsg.append("Invalid Password<BR>");
                }
                if (!ESAPI.validator().isValidInput("shortname", shortName.toString(), "SafeString", 255, true) || functions.hasHTMLTags(shortName.toString()))
                {
                    statusMsg.append("Invalid ShortName<BR>");
                }
                if (!ESAPI.validator().isValidInput("path", path.toString(), "SafeString", 1000, true) || functions.hasHTMLTags(path.toString()))
                {
                    statusMsg.append("Invalid Path<BR>");
                }
                if (!ESAPI.validator().isValidInput("chargeBackPath", chargeBackPath.toString(), "SafeString", 1000, true) || functions.hasHTMLTags(chargeBackPath.toString()))
                {
                    statusMsg.append("Invalid ChargeBack Path<BR>");
                }
                if (!(ESAPI.validator().isValidInput("dailycardlimit", (String) req.getParameter("dailycardlimit"), "Number", 10, true)))
                {
                    statusMsg.append("Invalid daily card limit .<BR>");
                }
                else
                {
                    if (req.getParameter("dailycardlimit").length() > 0)
                        dailyCardLimit = Integer.parseInt(req.getParameter("dailycardlimit"));
                }
                if (!(ESAPI.validator().isValidInput("weeklycardlimit", (String) req.getParameter("weeklycardlimit"), "Number", 10, true)))
                {
                    statusMsg.append("Invalid weekly card limit.<BR>");
                }
                else
                {
                    if (req.getParameter("weeklycardlimit").length() > 0)
                        weeklyCardLimit = Integer.parseInt(req.getParameter("weeklycardlimit"));
                }
                if (!(ESAPI.validator().isValidInput("monthlycardlimit", (String) req.getParameter("monthlycardlimit"), "Number", 10, true)))
                {
                    statusMsg.append("Invalid monthly card limit.<BR>");
                }
                else
                {
                    if (req.getParameter("monthlycardlimit").length() > 0)
                        monthlyCardLimit = Integer.parseInt(req.getParameter("monthlycardlimit"));
                }
                if (!(ESAPI.validator().isValidInput("dailyamountlimit", req.getParameter("dailyamountlimit"), "AmountStr", 10, true)))
                {
                    statusMsg.append("Invalid daily amount limit.<BR>");
                }
                else
                {
                    if (req.getParameter("dailyamountlimit").length() > 0)
                        dailyAmountLimit = Double.parseDouble(req.getParameter("dailyamountlimit"));
                }
                if (functions.isValueNull(req.getParameter("daily_amount_range")))
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
                    statusMsg.append("Invalid daily amount range.<BR>");
                }
                else if (!functions.isValueNull(dailyamount1) && daily_amount_rangeValue.contains("-") || !ESAPI.validator().isValidInput("dailyamount1", dailyamount1, "AmountStr", 10, true))
                {
                    statusMsg.append("Invalid daily amount range.<BR>");
                }
                else if (!functions.isValueNull(dailyamount2) && daily_amount_rangeValue.contains("-") || !ESAPI.validator().isValidInput("dailyamount2", dailyamount2, "AmountStr", 10, true))
                {
                    statusMsg.append("Invalid daily amount range.<BR>");
                }
                else if (!(ESAPI.validator().isValidInput("daily_amount_range",req.getParameter("daily_amount_range"),"Numbers", 20,true)))
                {
                    statusMsg.append("Invalid daily amount range.<BR>");
                }
                else if(functions.isValueNull(daily_amount_range_check) && "Y".equals(daily_amount_range_check) && !functions.isValueNull(daily_amount_rangeValue))
                {
                    statusMsg.append("Invalid daily amount range.<BR>");
                }
                else if(functions.isValueNull(dailyamount1) && functions.isValueNull(dailyamount2) && value1>value2 || dailyamount1.startsWith("0") || dailyamount2.startsWith("0"))
                {
                    statusMsg.append("Invalid daily amount range.<BR>");
                }
                else
                {
                    if (req.getParameter("daily_amount_range").length()>0)
                        daily_amount_range= req.getParameter("daily_amount_range");
                }

                if (!(ESAPI.validator().isValidInput("weeklyamountlimit", req.getParameter("weeklyamountlimit"), "AmountStr", 10, true)))
                {
                    statusMsg.append("Invalid weekly amount limit.<BR>");
                }
                else
                {
                    if (req.getParameter("weeklyamountlimit").length() > 0)
                        weeklyAmountLimit = Double.parseDouble(req.getParameter("weeklyamountlimit"));
                }
                if (!(ESAPI.validator().isValidInput("monthlyamountlimit", (String) req.getParameter("monthlyamountlimit"), "AmountStr", 10, true)))
                {
                    statusMsg.append("Invalid Monthly Amount limit.<BR>");
                }
                else
                {
                    if (req.getParameter("monthlyamountlimit").length() > 0)
                        monthlyAmountLimit = Double.parseDouble(req.getParameter("monthlyamountlimit"));
                }

                if (!(ESAPI.validator().isValidInput("dailycardamountlimit", (String) req.getParameter("dailycardamountlimit"), "AmountStr", 10, true)))
                {
                    statusMsg.append("Invalid daily card amount limit .<BR>");
                }
                else
                {
                    if (req.getParameter("dailycardamountlimit").length() > 0)
                        dailyCardAmountLimit = Double.parseDouble(req.getParameter("dailycardamountlimit"));
                }
                if (!(ESAPI.validator().isValidInput("weeklycardamountlimit", (String) req.getParameter("weeklycardamountlimit"), "AmountStr", 10, true)))
                {
                    statusMsg.append("Invalid weekly card amount limit.<BR>");
                }
                else
                {
                    if (req.getParameter("weeklycardamountlimit").length() > 0)
                        weeklyCardAmountLimit = Double.parseDouble(req.getParameter("weeklycardamountlimit"));
                }
                if (!(ESAPI.validator().isValidInput("monthlycardamountlimit", (String) req.getParameter("monthlycardamountlimit"), "AmountStr", 10, true)))
                {
                    statusMsg.append("Invalid monthly card amount limit.<BR>");
                }
                else
                {
                    if (req.getParameter("monthlycardamountlimit").length() > 0)
                        monthlyCardAmountLimit = Double.parseDouble(req.getParameter("monthlycardamountlimit"));
                }

                if (!(ESAPI.validator().isValidInput("Daily_Account_Amount_Limit",req.getParameter("Daily_Account_Amount_Limit"),"SafeString",1,false)))
                    statusMsg.append("Invalid Daily Amount Limit Flag.<BR>");
                else
                    DailyAccountAmountLimit = req.getParameter("Daily_Account_Amount_Limit");

                if (!(ESAPI.validator().isValidInput("Weekly_Account_Amount_Limit",req.getParameter("Weekly_Account_Amount_Limit"),"SafeString",1,false)))
                    statusMsg.append("Invalid Weekly Amount Limit Flag.<BR>");
                else
                    WeeklyAccountAmountLimit = req.getParameter("Weekly_Account_Amount_Limit");

                if (!(ESAPI.validator().isValidInput("Monthly_Account_Amount_Limit",req.getParameter("Monthly_Account_Amount_Limit"),"SafeString",1,false)))
                    statusMsg.append("Invalid Monthly Amount Limit Flag.<BR>");
                else
                    MonthlyAccountAmountLimit = req.getParameter("Monthly_Account_Amount_Limit");

                if (!(ESAPI.validator().isValidInput("daily_card_limit_check", req.getParameter("daily_card_limit_check"),"SafeString",1,false)))
                    statusMsg.append("Invalid Daily Card Limit Flag.<BR>");
                else
                    daily_card_limit_check = req.getParameter("daily_card_limit_check");

                if (!(ESAPI.validator().isValidInput("weekly_card_limit_check", req.getParameter("weekly_card_limit_check"),"SafeString",1,false)))
                    statusMsg.append("Invalid Weekly Card Limit Flag.<BR>");
                else
                    weekly_card_limit_check = req.getParameter("weekly_card_limit_check");

                if (!(ESAPI.validator().isValidInput("monthly_card_limit_check", req.getParameter("monthly_card_limit_check"),"SafeString",1,false)))
                    statusMsg.append("Invalid Monthly Card Limit Flag.<BR>");
                else
                    monthly_card_limit_check = req.getParameter("monthly_card_limit_check");

                if (!(ESAPI.validator().isValidInput("daily_card_amount_limit_check", req.getParameter("daily_card_amount_limit_check"),"SafeString",1,false)))
                    statusMsg.append("Invalid Daily Card Account Amount Limit Flag.<BR>");
                else
                    daily_card_amount_limit_check = req.getParameter("daily_card_amount_limit_check");

                if (!(ESAPI.validator().isValidInput("weekly_card_amount_limit_check", req.getParameter("weekly_card_amount_limit_check"),"SafeString",1,false)))
                    statusMsg.append("Invalid Weekly Card Account Amount Limit Flag.<BR>");
                else
                    weekly_card_amount_limit_check = req.getParameter("weekly_card_amount_limit_check");

                if (!(ESAPI.validator().isValidInput("monthly_card_amount_limit_check", req.getParameter("monthly_card_amount_limit_check"),"SafeString",1,false)))
                    statusMsg.append("Invalid Monthly Card Account Amount Limit Flag.<BR>");
                else
                    monthly_card_amount_limit_check = req.getParameter("monthly_card_amount_limit_check");

                if (!ESAPI.validator().isValidInput("fromAccountId", fromAccountId.toString(), "Number", 50, true))
                {
                    statusMsg.append("Invalid fromAccountId.<BR>");
                }
                if (!ESAPI.validator().isValidInput("fromMid", fromMid.toString(), "SafeString", 50, true) || functions.hasHTMLTags(fromMid.toString()))
                {
                    statusMsg.append("Invalid fromMid.<BR>");
                }
                if (!ESAPI.validator().isValidInput("gateway_table_name", (String) req.getParameter("gateway_table_name"), "SafeString", 50, true))
                {
                    statusMsg.append("Invalid gateway table name.<BR>");
                }
                else
                {
                    tableName = req.getParameter("gateway_table_name");
                }
                if (req.getParameter("columnnames") != null && req.getParameter("columnnames").length() > 0)
                {

                    if (!ESAPI.validator().isValidInput("columnnames", (String) req.getParameter("columnnames"), "SafeString", 500, true))
                    {
                        statusMsg.append("Invalid column names.<BR>");
                    }
                    else
                    {
                        columnName = req.getParameter("columnnames");
                    }
                    String[] columnNameArray = columnName.split(",");

                    for (int i = 0; i < columnNameArray.length; i++)
                    {
                        String tempColumn   = "";
                        tempColumn          = columnNameArray[i];
                        if (functions.isValueNull(tempColumn))
                        {
                            String isOptional   = req.getParameter(tempColumn+"_isOptional");
                            String columnSize   = req.getParameter(tempColumn+"_size");
                            if (functions.hasHTMLTags(req.getParameter(tempColumn)) || (!ESAPI.validator().isValidInput(tempColumn, req.getParameter(tempColumn), "SafeString", Integer.parseInt(columnSize), Boolean.parseBoolean(isOptional))))
                            {
                                logger.error("inside invalid");
                                statusMsg.append("Invalid "+ tempColumn +"<BR>");
                            }
                        }
                    }
                }
                if (statusMsg.length() > 0)
                {
                    req.setAttribute("message", statusMsg.toString());
                    rd  = req.getRequestDispatcher("/gatewayAccountInterface.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }

                //unique check while update
                /*StringBuffer isMidunique = new StringBuffer("select * from gateway_accounts where accountid not in(?) and merchantid=?");
                pstmt = con.prepareStatement(isMidunique.toString());
                pstmt.setString(1, accountid);
                pstmt.setString(2, mid.toString());
                rs = pstmt.executeQuery();
                if(rs.next())
                {
                    req.setAttribute("message","MerchantId Is Already Used,Please Enter Unique MerchantId");
                    rd = req.getRequestDispatcher("/gatewayAccountInterface.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(req,res);
                    return;
                }*/

                if (tableName.length() > 0 && !tableName.equalsIgnoreCase("null"))
                {
                    if (columnName != null && columnName.length() > 0)
                    {
                        Codec me                    = new MySQLCodec(MySQLCodec.Mode.STANDARD);
                        String tempId               = req.getParameter("id");
                        String tempAccountid        = req.getParameter("accountid");
                        String[] columnNameArray    = columnName.split(",");

                        if (tempId.equalsIgnoreCase("id") && tempAccountid.equalsIgnoreCase(accountid))
                        {

                            StringBuilder columnValuesSb    = new StringBuilder();
                            StringBuilder columnNamesSb     = new StringBuilder();

                            columnNamesSb.append("insert into " + tableName + " (accountid,");
                            columnValuesSb.append(" values ('");
                            columnValuesSb.append(accountid);
                            columnValuesSb.append("',");

                            for (int i = 2; i < columnNameArray.length; i++)
                            {
                                String tempColumn   = "";
                                String value        = "";
                                tempColumn          = columnNameArray[i];

                                if (req.getParameter(tempColumn) != null && req.getParameter(tempColumn).length() > 0)
                                {
                                    value = req.getParameter(tempColumn);

                                    if (value != null && value.length() > 0 && !value.equalsIgnoreCase("null"))
                                    {
                                        columnNamesSb.append("`" + tempColumn + "`");
                                        columnNamesSb.append(",");

                                        columnValuesSb.append("'");
                                        columnValuesSb.append(ESAPI.encoder().encodeForSQL(me,value));
                                        columnValuesSb.append("',");
                                    }
                                }
                            }


                            String tempColumn   = columnNamesSb.toString();
                            String tempValues   = columnValuesSb.toString();
                            tempColumn          = tempColumn.substring(0, tempColumn.length() - 1);
                            tempValues          = tempValues.substring(0, tempValues.length() - 1);

                            subTablequery = tempColumn + ")" + tempValues + ")";

                        }
                        else
                        {

                            sb.append(" update " + tableName + " set accountid = " + accountid + ",");
                            //System.out.println("sb query:::"+sb.toString());

                            for (int i = 0; i < columnNameArray.length; i++)
                            {
                                String tempColumn   = "";
                                String value        = "";
                                tempColumn          = columnNameArray[i];
                                if (req.getParameter(tempColumn) != null)
                                {
                                    if(!"id".equalsIgnoreCase(tempColumn))
                                    {
                                        value   = req.getParameter(tempColumn);
                                        if (value != null && !value.equalsIgnoreCase("null"))
                                        {
                                            sb.append("`" + tempColumn + "`='" + ESAPI.encoder().encodeForSQL(me,value) + "'");
                                            sb.append(",");
                                        }
                                    }
                                }
                            }

                            String temp     = sb.toString();
                            temp            = temp.substring(0, temp.length() - 1);
                            subTablequery   = temp + " where accountid = " + accountid;

                        }
                    }
                }


                //seems all correct - allow updating the table
                int i = 0;
                Connection connection   = null;
                connection              = Database.getConnection();
                try
                {
                    StringBuffer updateQry = new StringBuffer("update gateway_accounts set merchantid=? ,pgtypeid=? ,aliasname=? ,displayname=? ,username=? ,passwd=? ,chargeback_path=?,isCVVrequired=?," +
                            "shortname=?,path=?,3dSupportAccount=?,istest=?,isactive=?,isMultipleRefund=?,PartialRefund=?,emiSupport=?,isForexMid=?,cardLimitCheck=?,cardAmountLimitCheckAcc=?," +
                            "amountLimitCheckAcc=?,daily_card_limit=?,weekly_card_limit=?,monthly_card_limit=?,daily_card_amount_limit=?,weekly_card_amount_limit=?,monthly_card_amount_limit=?," +
                            "daily_amount_limit=?,weekly_amount_limit=?,monthly_amount_limit=?,ismastercardsupported=?,is_recurring=?,addressValidation=?,isDynamicDescriptor=?,site=?,fromAccountId=?," +
                            "fromMid=?,threeDsVersion=?,Daily_Account_Amount_Limit=?,Weekly_Account_Amount_Limit=?,Monthly_Account_Amount_Limit=?,daily_card_limit_check=?,weekly_card_limit_check=?," +
                            "monthly_card_limit_check=?,daily_card_amount_limit_check=?,weekly_card_amount_limit_check=?,monthly_card_amount_limit_check=?,daily_amount_range=?,daily_amount_range_check=? " +
                            " where accountid=?");
                    pstmtUpdate = connection.prepareStatement(updateQry.toString());
                    pstmtUpdate.setString(1, mid.toString());
                    pstmtUpdate.setString(2, pgtypeid.toString());
                    pstmtUpdate.setString(3, mcc.toString());
                    pstmtUpdate.setString(4, displayname.toString());
                    pstmtUpdate.setString(5, username.toString());
                    pstmtUpdate.setString(6, passwd.toString());
                    pstmtUpdate.setString(7, chargeBackPath.toString());
                    pstmtUpdate.setString(8, isCVVrequired);
                    pstmtUpdate.setString(9, shortName.toString());
                    pstmtUpdate.setString(10, path.toString());
                    pstmtUpdate.setString(11, is3dSupportAccount);
                    pstmtUpdate.setString(12, isTestAccount);
                    pstmtUpdate.setString(13, isActiveAccount);
                    pstmtUpdate.setString(14, isMultipleRefund);
                    pstmtUpdate.setString(15, PartialRefund);
                    pstmtUpdate.setString(16, emiSupport);
                    pstmtUpdate.setString(17, isForexMid);
                    pstmtUpdate.setString(18, cardLimitCheck);
                    pstmtUpdate.setString(19, cardAmountlimitCheck);
                    pstmtUpdate.setString(20, amountLimitCheck);
                    pstmtUpdate.setInt(21, dailyCardLimit);
                    pstmtUpdate.setInt(22, weeklyCardLimit);
                    pstmtUpdate.setInt(23, monthlyCardLimit);
                    pstmtUpdate.setDouble(24, dailyCardAmountLimit);
                    pstmtUpdate.setDouble(25, weeklyCardAmountLimit);
                    pstmtUpdate.setDouble(26, monthlyCardAmountLimit);
                    pstmtUpdate.setDouble(27, dailyAmountLimit);
                    pstmtUpdate.setDouble(28, weeklyAmountLimit);
                    pstmtUpdate.setDouble(29, monthlyAmountLimit);
                    pstmtUpdate.setInt(30, ismastercardsupported);
                    pstmtUpdate.setString(31, isrecurring);
                    pstmtUpdate.setString(32, addressValidation);
                    pstmtUpdate.setString(33, isDynamicDescriptor);
                    pstmtUpdate.setString(34, site);
                    pstmtUpdate.setString(35, fromAccountId.toString());
                    pstmtUpdate.setString(36, fromMid.toString());
                    pstmtUpdate.setString(37, threeDsVersion);
                    pstmtUpdate.setString(38, DailyAccountAmountLimit);
                    pstmtUpdate.setString(39, WeeklyAccountAmountLimit);
                    pstmtUpdate.setString(40, MonthlyAccountAmountLimit);
                    pstmtUpdate.setString(41, daily_card_limit_check);
                    pstmtUpdate.setString(42, weekly_card_limit_check);
                    pstmtUpdate.setString(43, monthly_card_limit_check);
                    pstmtUpdate.setString(44, daily_card_amount_limit_check);
                    pstmtUpdate.setString(45, weekly_card_amount_limit_check);
                    pstmtUpdate.setString(46, monthly_card_amount_limit_check);
                    pstmtUpdate.setString(47,daily_amount_range);
                    pstmtUpdate.setString(48,daily_amount_range_check);
                    pstmtUpdate.setString(49, accountid);

                    //   pstmtUpdate.setString(36,actionExecutorId);
                    // pstmtUpdate.setString(37,actionExecutorName);
                    logger.error("pstmtUpdate---->" + pstmtUpdate);
                    i = pstmtUpdate.executeUpdate();
                    if (i > 0)
                    {
                        successMsg.append("Bank Account Details Updated Successfully.");

                        int j = 0;
                        if (subTablequery.length() > 0)
                        {
                            PreparedStatement subTablePs    = con.prepareStatement(subTablequery);
                            j                               = subTablePs.executeUpdate();
                            logger.debug(subTablePs);
                            logger.debug(j);
                            if (j == 0)
                            {
                                statusMsg.append("<font class=\"textb\">" + "<center>" + "<b>" + "Error  Updating Second Table" + "</b>" + "</center>" + "</font>" + "<BR>");
                            }
                        }

                        if (!oldDisplayName.equalsIgnoreCase(displayName))
                        {
                            Connection subconnection    = null;
                            subconnection               = Database.getConnection();
                            try
                            {
                                String selectQry = "SELECT * FROM member_account_mapping WHERE accountid=? AND isActive='Y' AND isTest='N'";
                                PreparedStatement preparedStatement = subconnection.prepareStatement(selectQry);
                                preparedStatement.setString(1, accountid);
                                ResultSet resultSet = preparedStatement.executeQuery();
                                while (resultSet.next())
                                {
                                    String member           = rs.getString("memberid");
                                    String companyName      = "";
                                    String selectcompany    = "select company_name,activation from members where memberid=? ";
                                    PreparedStatement p1    = subconnection.prepareStatement(selectcompany);
                                    p1.setString(1, member);
                                    ResultSet rs1 = p1.executeQuery();
                                    String activation = "";
                                    if (rs1.next())
                                    {
                                        companyName = rs1.getString("company_name");
                                        activation  = rs1.getString("activation");
                                    }
                                    if (activation.equalsIgnoreCase("Y"))
                                    {
                                        HashMap membermailDetails       = new HashMap();
                                        LinkedHashMap discriptorDetails = new LinkedHashMap();

                                        discriptorDetails.put("Member ID", member);
                                        discriptorDetails.put("Company Name", companyName);
                                        discriptorDetails.put("Account ID", accountid);
                                        discriptorDetails.put("OLD Billing Discriptor", oldDisplayName);
                                        discriptorDetails.put("NEW Billing Discriptor", displayName);

                                        membermailDetails.put(MailPlaceHolder.TOID, member);
                                        membermailDetails.put(MailPlaceHolder.MULTIPALTRANSACTION, asynchronousMailService.getDetailTableForSingleTrans(discriptorDetails));
                                        asynchronousMailService.sendMerchantSignup(MailEventEnum.BILLING_DESCRIPTOR_CHANGE_INTIMATION, membermailDetails);
                                    }
                                }
                            }catch (Exception e){
                                e.getMessage();
                            }finally
                            {
                                Database.closeConnection(subconnection);
                            }
                        }

                        if(functions.isValueNull(onchangedValues)){
                            String remoteAddr   = Functions.getIpAddress(req);
                            int serverPort      = req.getServerPort();
                            String servletPath  = req.getServletPath();
                            String header       = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;

                            activityTrackerVOs.setInterface(ActivityLogParameters.PARTNER.toString());
                            activityTrackerVOs.setUser_name(Login + "-" + actionExecutorId);
                            activityTrackerVOs.setRole(partner.getUserRole(user));
                            activityTrackerVOs.setAction(ActivityLogParameters.EDIT.toString());
                            activityTrackerVOs.setModule_name(ActivityLogParameters.GATWAY_ACCOUNT_MASTER.toString());
                            activityTrackerVOs.setLable_values(onchangedValues);
                            activityTrackerVOs.setDescription(ActivityLogParameters.ACCOUNTID.toString() + "-" + accountid);
                            activityTrackerVOs.setIp(remoteAddr);
                            activityTrackerVOs.setHeader(header);
                            activityTrackerVOs.setPartnerId(actionExecutorId);
                            try
                            {
                                AsyncActivityTracker asyncActivityTracker = AsyncActivityTracker.getInstance();
                                asyncActivityTracker.asyncActivity(activityTrackerVOs);
                            }
                            catch (Exception e)
                            {
                                logger.error("Exception while AsyncActivityLog::::", e);
                            }
                        }



                        GatewayAccountService.loadGatewayAccounts();
                    }
                    else
                    {
                        statusMsg.append("Failed While Updating Bank Account Details.");
                    }
                }
                catch (Exception e)
                {
                    logger.error("Exception:::", e);
                    req.setAttribute("message", e.getMessage());
                    rd = req.getRequestDispatcher("/gatewayAccountInterface.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }
                finally
                {
                    Database.closePreparedStatement(pstmtUpdate);
                    Database.closeConnection(connection);
                }
                req.setAttribute("message", statusMsg.toString());
                req.setAttribute("success", successMsg.toString());
                req.setAttribute("pgtypeid", pgtypeid.toString());
                rd = req.getRequestDispatcher("/gatewayAccountInterface.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::" + systemError);
            req.setAttribute("message", systemError.getMessage());
            rd = req.getRequestDispatcher("/gatewayAccountInterface.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::" + e);
            req.setAttribute("message", e.getMessage());
            rd = req.getRequestDispatcher("/gatewayAccountInterface.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (Exception e)
        {
            logger.error("Exception:::", e);
            req.setAttribute("message", e.getMessage());
            rd = req.getRequestDispatcher("/gatewayAccountInterface.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
    }
}

