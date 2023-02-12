package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.core.GatewayAccountService;
import com.manager.dao.GatewayAccountDAO;
import com.payment.exceptionHandler.PZDBViolationException;
import org.apache.commons.lang.StringUtils;
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
import java.util.ResourceBundle;


/**
 * Created by Sneha on 2/9/15.
 */
public class ManageGatewayAccounts extends HttpServlet
{
    static Logger log = new Logger(ManageGatewayAccounts.class.getName());
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

        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }

        RequestDispatcher rd = req.getRequestDispatcher("/manageGatewayAccount.jsp?ctoken=" + user.getCSRFToken());
        ResourceBundle rb1 = null;
        String language_property1 = (String)session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String ManageGatewayAccounts_MerchantId_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageGatewayAccounts_MerchantId_errormsg"))?rb1.getString("ManageGatewayAccounts_MerchantId_errormsg"): "Invalid MerchantId<BR>";
        String ManageGatewayAccounts_bankname_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageGatewayAccounts_bankname_errormsg"))?rb1.getString("ManageGatewayAccounts_bankname_errormsg"): "Invalid BankName<BR>";
        String ManageGatewayAccounts_mcc_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageGatewayAccounts_mcc_errormsg"))?rb1.getString("ManageGatewayAccounts_mcc_errormsg"): "Invalid MCC/Alias Name<BR>";
        String ManageGatewayAccounts_billing_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageGatewayAccounts_billing_errormsg"))?rb1.getString("ManageGatewayAccounts_billing_errormsg"): "Invalid Billing Descriptor<BR>";
        String ManageGatewayAccounts_username_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageGatewayAccounts_username_errormsg"))?rb1.getString("ManageGatewayAccounts_username_errormsg"): "Invalid UserName<BR>";
        String ManageGatewayAccounts_password_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageGatewayAccounts_password_errormsg"))?rb1.getString("ManageGatewayAccounts_password_errormsg"): "Invalid Password<BR>";
        String ManageGatewayAccounts_site_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageGatewayAccounts_site_errormsg"))?rb1.getString("ManageGatewayAccounts_site_errormsg"): "Invalid site<BR>";
        String ManageGatewayAccounts_chargeback_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageGatewayAccounts_chargeback_errormsg"))?rb1.getString("ManageGatewayAccounts_chargeback_errormsg"): "Invalid chargeback path.<BR>";
        String ManageGatewayAccounts_support_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageGatewayAccounts_support_errormsg"))?rb1.getString("ManageGatewayAccounts_support_errormsg"): "Invalid is3dSupportAccount<BR>";
        String ManageGatewayAccounts_test_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageGatewayAccounts_test_errormsg"))?rb1.getString("ManageGatewayAccounts_test_errormsg"): "Invalid isTestAccount<BR>";
        String ManageGatewayAccounts_isactive_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageGatewayAccounts_isactive_errormsg"))?rb1.getString("ManageGatewayAccounts_isactive_errormsg"): "Invalid isActiveAccount<BR>";
        String ManageGatewayAccounts_ismultiple_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageGatewayAccounts_ismultiple_errormsg"))?rb1.getString("ManageGatewayAccounts_ismultiple_errormsg"): "Invalid isMultipleRefund<BR>";
        String ManageGatewayAccounts_partial_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageGatewayAccounts_partial_errormsg"))?rb1.getString("ManageGatewayAccounts_partial_errormsg"): "Invalid PartialRefund <BR>";
        String ManageGatewayAccounts_emisupport_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageGatewayAccounts_emisupport_errormsg"))?rb1.getString("ManageGatewayAccounts_emisupport_errormsg"): "Invalid emiSupport<BR>";
        String ManageGatewayAccounts_daily_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageGatewayAccounts_daily_errormsg"))?rb1.getString("ManageGatewayAccounts_daily_errormsg"): "Invalid daily card limit .<BR>";
        String ManageGatewayAccounts_weekly_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageGatewayAccounts_weekly_errormsg"))?rb1.getString("ManageGatewayAccounts_weekly_errormsg"): "Invalid weekly card limit.<BR>";
        String ManageGatewayAccounts_monthly_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageGatewayAccounts_monthly_errormsg"))?rb1.getString("ManageGatewayAccounts_monthly_errormsg"): "Invalid monthly card limit.<BR>";
        String ManageGatewayAccounts_daily1_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageGatewayAccounts_daily1_errormsg"))?rb1.getString("ManageGatewayAccounts_daily1_errormsg"): "Invalid daily amount limit.<BR>";
        String ManageGatewayAccounts_weekly1_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageGatewayAccounts_weekly1_errormsg"))?rb1.getString("ManageGatewayAccounts_weekly1_errormsg"): "Invalid weekly amount limit.<BR>";
        String ManageGatewayAccounts_monthly1_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageGatewayAccounts_monthly1_errormsg"))?rb1.getString("ManageGatewayAccounts_monthly1_errormsg"): "Invalid Monthly Amount limit.<BR>";
        String ManageGatewayAccounts_limit_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageGatewayAccounts_limit_errormsg"))?rb1.getString("ManageGatewayAccounts_limit_errormsg"): "Invalid daily card amount limit .<BR>";
        String ManageGatewayAccounts_card_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageGatewayAccounts_card_errormsg"))?rb1.getString("ManageGatewayAccounts_card_errormsg"): "Invalid weekly card amount limit.<BR>";
        String ManageGatewayAccounts_card1_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageGatewayAccounts_card1_errormsg"))?rb1.getString("ManageGatewayAccounts_card1_errormsg"): "Invalid monthly card amount limit.<BR>";
        String ManageGatewayAccounts_column_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageGatewayAccounts_column_errormsg"))?rb1.getString("ManageGatewayAccounts_column_errormsg"): "Invalid column names.<BR>";
        String ManageGatewayAccounts_gateway_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageGatewayAccounts_gateway_errormsg"))?rb1.getString("ManageGatewayAccounts_gateway_errormsg"): "Invalid gateway table name.<BR>";
        String ManageGatewayAccounts_added_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageGatewayAccounts_added_errormsg"))?rb1.getString("ManageGatewayAccounts_added_errormsg"): "Bank Account Added Successfully";
        String ManageGatewayAccounts_failed_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageGatewayAccounts_failed_errormsg"))?rb1.getString("ManageGatewayAccounts_failed_errormsg"): "Bank Account Adding Failed";

        GatewayAccountDAO accountDAO = new GatewayAccountDAO();
        Functions functions=new Functions();
        StringBuffer msg = new StringBuffer();
        StringBuffer successMsg = new StringBuffer();
        List<String> gatewayList = new ArrayList();
        String partnerid = req.getParameter("partnerid");
        String pgtypeidNew = req.getParameter("bankid");
        String[] elements_pgtypeid = pgtypeidNew.split(",");
        gatewayList = Arrays.asList(elements_pgtypeid);

        String tableName = "";
        String columnName = "";
        boolean flag = true;
        String mid = req.getParameter("mid");
        String isCVVRequired ="Y";
        String mcc = req.getParameter("mcc");
        String displayName = req.getParameter("displayName");
        String username = req.getParameter("username");
        String pwd = req.getParameter("pwd");
        String shortName = req.getParameter("shortname");
        String path = req.getParameter("path");
        String chargeBackPath=req.getParameter("chargeBackPath");
        //System.out.println("chargeBackPath-----------"+chargeBackPath);
        String is3dSupportAccount = req.getParameter("is3dSupportAccount");
        String threeDsVersion = req.getParameter("threeDsVersion");
        //System.out.println("is3dSupportAccount");
        String isTestAccount = req.getParameter("isTestAccount");
        String isActiveAccount = req.getParameter("isActiveAccount");
        String isMultipleRefund = req.getParameter("isMultipleRefund");
        String PartialRefund = req.getParameter("PartialRefund");
        String emiSupport = req.getParameter("emiSupport");
        String isSubmitted =  req.getParameter("isSubmitted");
        String site =  req.getParameter("site");
        int monthlyCardLimit =0;
        int dailyCardLimit =0;
        int weeklyCardLimit =0;
        int isMasterCardSupported = 1;

        Double dailyAmountLimit = 0.00;
        Double monthlyAmountLimit = 0.00;
        Double dailyCardAmountLimit = 0.00;
        Double weeklyCardAmountLimit = 0.00;
        Double monthlyCardAmountLimit = 0.00;
        Double weeklyAmountLimit = 0.00;

        String cardLimitCheck=req.getParameter("cardLimitCheck");
        String cardAmountlimitCheck=req.getParameter("cardAmountLimitCheck");
        String amountLimitCheck=req.getParameter("amountLimitCheck");
        isMasterCardSupported=Integer.parseInt(req.getParameter("ismastercardsupported"));
        String isrecurring=req.getParameter("isrecurring");
        String addressValidation=req.getParameter("addressValidation");
        String isDynamicDescriptor=req.getParameter("isDynamicDescriptor");
        String isForexMid=req.getParameter("isForexMid");
        String role=(String)session.getAttribute("role");
        String username1=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username1;
        String fromAccountId=req.getParameter("fromAccountId");
        String fromMid=req.getParameter("fromMid");

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
        String daily_amount_range=null;

            if (!ESAPI.validator().isValidInput("mid", mid, "SafeString", 255, false)|| functions.hasHTMLTags(mid))
            {
                msg.append(ManageGatewayAccounts_MerchantId_errormsg);
                flag = false;
            }
            /*if (!ESAPI.validator().isValidInput("pgtypeid", pgtypeid, "Numbers", 10, false))
            {
                msg.append(ManageGatewayAccounts_bankname_errormsg);
                flag = false;
            }*/
            if (!ESAPI.validator().isValidInput("mcc", mcc, "SafeString", 100, false)|| functions.hasHTMLTags(mcc))
            {
                msg.append(ManageGatewayAccounts_mcc_errormsg);
                flag = false;
            }
            if (!ESAPI.validator().isValidInput("displayname", displayName, "SafeString", 255, false) || functions.hasHTMLTags(displayName) )
            {
                msg.append(ManageGatewayAccounts_billing_errormsg);
                flag = false;
            }
            if (!ESAPI.validator().isValidInput("username", username, "SafeString", 255, true) || functions.hasHTMLTags(username) )
            {
                msg.append(ManageGatewayAccounts_username_errormsg);
                flag = false;
            }
            if (!ESAPI.validator().isValidInput("pwd", pwd, "SafeString", 255, true) || functions.hasHTMLTags(pwd))
            {
                msg.append(ManageGatewayAccounts_password_errormsg);
                flag = false;
            }

            if (!(ESAPI.validator().isValidInput("site", site, "URL", 100, true)) || functions.hasHTMLTags(site))
            {
                log.debug("Invalid site.");
                msg.append(ManageGatewayAccounts_site_errormsg);
                flag = false;
            }
            if (!(ESAPI.validator().isValidInput("chargeBackPath", chargeBackPath, "SafeString", 100, true)) || functions.hasHTMLTags(chargeBackPath))
            {
                log.debug("Invalid chargeback path.");
                msg.append(ManageGatewayAccounts_chargeback_errormsg);
                flag = false;
            }
        if (!(ESAPI.validator().isValidInput("path", path, "SafeString", 100, true)) || functions.hasHTMLTags(path))
        {
            log.debug("Invalid path.");
            msg.append("Invalid path <BR>");
            flag = false;
        }
        if (!(ESAPI.validator().isValidInput("shortname", req.getParameter("shortname"), "SafeString", 100, true)) || functions.hasHTMLTags(req.getParameter("shortname")))
        {
            log.debug("Invalid Short Name.");
            msg.append("Invalid Short Name <BR>");
            flag = false;
        }
        /*if (!(ESAPI.validator().isValidInput("chargeBackPath", (String) req.getParameter("chargeBackPath"), "SafeString", 100, true)))
        {
            log.debug("Invalid chargeback path.");
            msg.append("Invalid chargeback path<BR>");
            flag = false;
        }
        else
        {
            chargeBackPath = req.getParameter("chargeBackPath");
        }*/
            if (!ESAPI.validator().isValidInput("is3dSupportAccount", is3dSupportAccount, "SafeString", 10, true))
            {
                msg.append(ManageGatewayAccounts_support_errormsg);
                flag = false;
            }

            if (!ESAPI.validator().isValidInput("isTestAccount", isTestAccount, "SafeString", 10, true))
            {
                msg.append(ManageGatewayAccounts_test_errormsg);
                flag = false;
            }
            if (!ESAPI.validator().isValidInput("isActiveAccount", isActiveAccount, "SafeString", 10, true))
            {
                msg.append(ManageGatewayAccounts_isactive_errormsg);
                flag = false;
            }
            if (!ESAPI.validator().isValidInput("isMultipleRefund", isMultipleRefund, "SafeString", 10, true))
            {
                msg.append(ManageGatewayAccounts_ismultiple_errormsg);
                flag = false;
            }
            if (!ESAPI.validator().isValidInput("PartialRefund", PartialRefund, "SafeString", 10, true))
            {
                msg.append(ManageGatewayAccounts_partial_errormsg);
                flag = false;
            }
            if (!ESAPI.validator().isValidInput("emiSupport", emiSupport, "SafeString", 10, true))
            {
                msg.append(ManageGatewayAccounts_emisupport_errormsg);
                flag = false;
            }
            if (!(ESAPI.validator().isValidInput("dailycardlimit", (String) req.getParameter("dailycardlimit"), "Number", 10, true)))
            {
                log.debug("Invalid daily card limit ");
                msg.append(ManageGatewayAccounts_daily_errormsg);
                flag = false;
            }
            else
            {
                if (req.getParameter("dailycardlimit").length() > 0)
                    dailyCardLimit = Integer.parseInt(req.getParameter("dailycardlimit"));
            }
            if (!(ESAPI.validator().isValidInput("weeklycardlimit", (String) req.getParameter("weeklycardlimit"), "Number", 10, true)))
            {
                log.debug("Invalid weekly card limit");
                msg.append(ManageGatewayAccounts_weekly_errormsg);
                flag = false;
            }
            else
            {
                if (req.getParameter("weeklycardlimit").length() > 0)
                    weeklyCardLimit = Integer.parseInt(req.getParameter("weeklycardlimit"));
            }
            if (!(ESAPI.validator().isValidInput("monthlycardlimit", (String) req.getParameter("monthlycardlimit"), "Number", 10, true)))
            {
                log.debug("Invalid monthly card limit ");
                msg.append(ManageGatewayAccounts_monthly_errormsg);
                flag = false;
            }
            else
            {
                if (req.getParameter("monthlycardlimit").length() > 0)
                    monthlyCardLimit = Integer.parseInt(req.getParameter("monthlycardlimit"));
            }
            if (!(ESAPI.validator().isValidInput("dailyamountlimit", req.getParameter("dailyamountlimit"), "AmountStr", 10, true)))
            {
                log.debug("Invalid daily amount limit.");
                msg.append(ManageGatewayAccounts_daily1_errormsg);
                flag = false;
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
                log.error("Invalid daily amount range.");
                msg.append("Invalid daily amount range.<BR>");
                flag= false;
            }
            else if (!functions.isValueNull(dailyamount1) && daily_amount_rangeValue.contains("-") || !ESAPI.validator().isValidInput("dailyamount1", dailyamount1, "AmountStr", 10, true))
            {
                msg.append("Invalid daily amount range.<BR>");
                flag= false;
            }
            else if (!functions.isValueNull(dailyamount2) && daily_amount_rangeValue.contains("-") || !ESAPI.validator().isValidInput("dailyamount2", dailyamount2, "AmountStr", 10, true))
            {
                msg.append("Invalid daily amount range.<BR>");
                flag= false;
            }
            else if (!(ESAPI.validator().isValidInput("daily_amount_range",req.getParameter("daily_amount_range"),"Numbers", 20,true)))
            {
                 msg.append("Invalid daily amount range.<BR>");
                flag= false;
            }
            else if(functions.isValueNull(daily_amount_range_check) && "Y".equals(daily_amount_range_check) && !functions.isValueNull(daily_amount_rangeValue))
            {
                msg.append("Invalid daily amount range.<BR>");
                flag= false;
            }
            else if(functions.isValueNull(dailyamount1) && functions.isValueNull(dailyamount2) && value1>value2 || dailyamount1.startsWith("0") || dailyamount2.startsWith("0"))
            {
                msg.append("Invalid daily amount range.<BR>");
                flag= false;
            }
            else
            {
                 if (req.getParameter("daily_amount_range").length()>0)
                    daily_amount_range= req.getParameter("daily_amount_range");
            }
            if (!(ESAPI.validator().isValidInput("weeklyamountlimit", req.getParameter("weeklyamountlimit"), "AmountStr", 10, true)))
            {
                log.debug("Invalid weekly amount limit ");
                msg.append(ManageGatewayAccounts_weekly1_errormsg);
                flag = false;
            }
            else
            {
                if (req.getParameter("weeklyamountlimit").length() > 0)
                    weeklyAmountLimit = Double.parseDouble(req.getParameter("weeklyamountlimit"));
            }
            if (!(ESAPI.validator().isValidInput("monthlyamountlimit", (String) req.getParameter("monthlyamountlimit"), "AmountStr", 10, true)))
            {
                log.debug("Invalid monthly amount limit");
                msg.append(ManageGatewayAccounts_monthly1_errormsg);
                flag = false;
            }
            else
            {
                if (req.getParameter("monthlyamountlimit").length() > 0)
                    monthlyAmountLimit = Double.parseDouble(req.getParameter("monthlyamountlimit"));
            }

            if (!(ESAPI.validator().isValidInput("dailycardamountlimit", (String) req.getParameter("dailycardamountlimit"), "AmountStr", 10, true)))
            {
                log.debug("Invalid daily card amount limit");
                msg.append(ManageGatewayAccounts_limit_errormsg);
                flag = false;
            }
            else
            {
                if (req.getParameter("dailycardamountlimit").length() > 0)
                    dailyCardAmountLimit = Double.parseDouble(req.getParameter("dailycardamountlimit"));
            }
            if (!(ESAPI.validator().isValidInput("weeklycardamountlimit", (String) req.getParameter("weeklycardamountlimit"), "AmountStr", 10, true)))
            {
                log.debug("Invalid weekly card amount limit.");
                msg.append(ManageGatewayAccounts_card_errormsg);
                flag = false;
            }
            else
            {
                if (req.getParameter("weeklycardamountlimit").length() > 0)
                    weeklyCardAmountLimit = Double.parseDouble(req.getParameter("weeklycardamountlimit"));
            }
            if (!(ESAPI.validator().isValidInput("monthlycardamountlimit", (String) req.getParameter("monthlycardamountlimit"), "AmountStr", 10, true)))
            {
                log.debug("Invalid monthly card amount limit.");
                msg.append(ManageGatewayAccounts_card1_errormsg);
                flag = false;
            }
            else
            {
                if (req.getParameter("monthlycardamountlimit").length() > 0)
                    monthlyCardAmountLimit = Double.parseDouble(req.getParameter("monthlycardamountlimit"));
            }

            if ("true".equals(isSubmitted))
            {
                if (req.getParameter("columnnames") != null && req.getParameter("columnnames").length() > 0)
                {

                    if (!ESAPI.validator().isValidInput("columnnames", (String) req.getParameter("columnnames"), "SafeString", 500, false))
                    {
                        log.debug("Invalid column names.");
                        msg.append(ManageGatewayAccounts_column_errormsg);
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
                                log.debug("Invalid column names.");
                                msg.append("Invalid "+ tempColumn + "<BR>");
                                flag = false;
                            }
                        }
                    }
                }
                if (!ESAPI.validator().isValidInput("gatewaytablename", (String) req.getParameter("gatewaytablename"), "SafeString", 50, true))
                {
                    log.debug("Invalid gateway table name.");
                    msg.append(ManageGatewayAccounts_gateway_errormsg);
                    flag = false;
                }
                else
                {
                    tableName = req.getParameter("gatewaytablename");
                }
            }

            if (!ESAPI.validator().isValidInput("fromAccountId", fromAccountId, "Numbers", 50, true))
            {
                msg.append("Invalid fromAccountId.<BR>");
                flag = false;
            }
            if (!ESAPI.validator().isValidInput("fromMid", fromMid, "SafeString", 50, true) || functions.hasHTMLTags(fromMid))
            {
                msg.append("Invalid fromMid.<BR>");
                flag = false;
            }
            if (msg.length() > 0)
            {
                req.setAttribute("msg", msg.toString());
                rd.forward(req, res);
                return;
            }
            if (req.getParameterMap().containsKey("iscvvrequired"))
            {
                isCVVRequired = req.getParameter("iscvvrequired");
            }
            try
            {

                String status = "";
                Double minTransactionAmount = 0.00;
                Double maxTransactionAmount = 0.00;

                for (String pgtypeid : gatewayList)
                {

                    boolean isMidUnique = accountDAO.isMIDUnique(mid, pgtypeid, mcc);
                    if (!isMidUnique)
                    {
                        req.setAttribute("msg", "Gateway Accounts with same merchantId, pgtypeId, aliasName already exists");
                        rd.forward(req, res);
                        return;
                    }

                    if (flag)
                    {
                        status = accountDAO.createBankAccount(mid, pgtypeid, isCVVRequired, mcc, displayName, isMasterCardSupported, shortName, site, path, is3dSupportAccount, username, pwd, chargeBackPath, monthlyCardLimit, dailyAmountLimit, monthlyAmountLimit, dailyCardLimit, weeklyCardLimit, minTransactionAmount, maxTransactionAmount, dailyCardAmountLimit, weeklyCardAmountLimit, monthlyCardAmountLimit, isTestAccount, isActiveAccount, isMultipleRefund, PartialRefund, emiSupport, partnerid, "1", weeklyAmountLimit, isrecurring, cardLimitCheck, cardAmountlimitCheck, amountLimitCheck, addressValidation, isDynamicDescriptor, isForexMid, actionExecutorId, actionExecutorName, fromAccountId, fromMid, threeDsVersion, tableName, columnName,daily_card_limit_check, weekly_card_limit_check,monthly_card_limit_check,Daily_Account_Amount_Limit,Weekly_Account_Amount_Limit,Monthly_Account_Amount_Limit,daily_card_amount_limit_check,weekly_card_amount_limit_check,monthly_card_amount_limit_check,req,daily_amount_range,daily_amount_range_check);
                    }
                    else
                    {
                        msg.append(status);
                    }

                }
                if ("success".equals(status))
                {
                    successMsg.append(ManageGatewayAccounts_added_errormsg);
                    GatewayAccountService.loadGatewayAccounts();
                }
                else
                {
                    msg.append(ManageGatewayAccounts_failed_errormsg);
                }
            }
            catch (PZDBViolationException e)
            {
                msg.append("Exception While Adding Bank Account::");
                msg.append(e);
                log.debug("Exception While Adding Bank Account::" + e);
            }
            catch (Exception e)
            {
                msg.append("Exception While Loading Bank Account After Adding New Bank Account::");
                msg.append(e);
                log.debug("Exception While Loading Bank Account After Adding New Bank Account::" + e);
            }

        req.setAttribute("msg",msg.toString());
        req.setAttribute("success",successMsg.toString());
        rd = req.getRequestDispatcher("/gatewayAccountInterface.jsp?ctoken=" + user.getCSRFToken());
        rd.forward(req,res);
    }
}
