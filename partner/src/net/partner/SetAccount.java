package net.partner;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.manager.PartnerManager;
import com.manager.TerminalManager;
import com.manager.enums.ActivityLogParameters;
import com.manager.vo.ActivityTrackerVOs;
import com.payment.Enum.CardTypeEnum;
import com.payment.Enum.PaymentModeEnum;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.apache.commons.lang.StringUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;

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
import java.sql.ResultSetMetaData;
import java.util.*;

public class SetAccount extends HttpServlet
{
    private static Logger logger = new Logger(SetAccount.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException, NumberFormatException
    {
        HttpSession session             = req.getSession();
        User user                       = (User) session.getAttribute("ESAPIUserSessionKey");
        ResourceBundle rb1              = null;
        String language_property1       = (String) session.getAttribute("language_property");
        rb1                             = LoadProperties.getProperty(language_property1);
        String SetAccount_new_errormsg  = StringUtils.isNotEmpty(rb1.getString("SetAccount_new_errormsg")) ? rb1.getString("SetAccount_new_errormsg") : "New Terminal Added Successfully.";

        PartnerFunctions partner                = new PartnerFunctions();
        TerminalManager terminalManager         = new TerminalManager();
        PartnerManager partnerManager           = new PartnerManager();
        Functions functions                     = new Functions();
        ActivityTrackerVOs activityTrackerVOs   = new ActivityTrackerVOs();

        List<String> cardList       = new ArrayList();
        List<String> accountlist    = new ArrayList();
        List<String> paymodlist     = new ArrayList();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }
        String actionExecutorId =(String) session.getAttribute("merchantid");
        String partnerid        = "";
        if (functions.isValueNull(req.getParameter("partnerid")))
        {
            partnerid = req.getParameter("partnerid");
        }
        else
        {
            partnerid = (String) session.getAttribute("merchantid");
        }


        String mId  = req.getParameter("memberid");
        // String paymode = req.getParameter("paymodeid");
        String error        = "";
        String success      = "";
        Connection conn     = null;
        String memberid     = "";
        String cardtypeid   = "";
        String paymodeid    = "";
        String account_id   = "";
        String activityrole = "";
        /*String Roles        = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
        if(Roles.contains("partner")){
            activityrole    = ActivityLogParameters.PARTNER.toString();
        }
        else if(Roles.contains("childsuperpartner"))
        {
            activityrole    = ActivityLogParameters.CHILEDSUPERPARTNER.toString();
        }else if(Roles.contains("superpartner"))
        {
            activityrole    = ActivityLogParameters.SUPERPARTNER.toString();
        }else if(Roles.contains("subpartner"))
        {
            activityrole    = ActivityLogParameters.SUBPARTNER.toString();
        }*/

        String Login    = user.getAccountName();


        for (CardTypeEnum cardTypeEnum : CardTypeEnum.values())
        {
            if (functions.isValueNull(req.getParameter("cardtype_" + cardTypeEnum.getValue())))
            {
                cardList.add(req.getParameter("cardtype_" + cardTypeEnum.getValue()));
            }
        }

        for (PaymentModeEnum paymodEnum : PaymentModeEnum.values())
        {
            if (functions.isValueNull(req.getParameter("paymode_" + paymodEnum.getValue())))
            {
                paymodlist.add(req.getParameter("paymode_" + paymodEnum.getValue()));
            }
        }

        TreeMap<Integer, String> accountMap = partnerManager.loadGatewayAccounts(partnerid, "");

        for (Integer accID : accountMap.keySet())
        {
            if (functions.isValueNull(req.getParameter("accID_" + accID)))
            {
                accountlist.add(req.getParameter("accID_" + accID));
            }
        }

        String[] terminal_mainAll = null;

        String action1  = req.getParameter("action_create");
        if(functions.isValueNull(action1)){
            if(action1.equalsIgnoreCase("CREATE"))
            {
                PreparedStatement pstmnt = null;
                try
                {
                    conn = Database.getConnection();
                    error = validateMandatoryParameters(req, action1);
                    boolean flag = true;

                    if (error.equals(""))
                    {
                        memberid                    = req.getParameter("memberid");
                        String daily_amount_limit   = req.getParameter("daily_amount_limit");
                        String weekly_amount_limit  = req.getParameter("weekly_amount_limit");
                        String monthly_amount_limit = req.getParameter("monthly_amount_limit");

                        String daily_card_limit     = req.getParameter("daily_card_limit");
                        String weekly_card_limit    = req.getParameter("weekly_card_limit");
                        String monthly_card_limit   = req.getParameter("monthly_card_limit");

                        String daily_card_amount_limit      = req.getParameter("daily_card_amount_limit");
                        String weekly_card_amount_limit     = req.getParameter("weekly_card_amount_limit");
                        String monthly_card_amount_limit    = req.getParameter("monthly_card_amount_limit");

                        String daily_amount_limit_check = req.getParameter("daily_amount_limit_check");
                        String weekly_amount_limit_check = req.getParameter("weekly_amount_limit_check");
                        String monthly_amount_limit_check = req.getParameter("monthly_amount_limit_check");
                        String daily_card_limit_check = req.getParameter("daily_card_limit_check");
                        String weekly_card_limit_check = req.getParameter("weekly_card_limit_check");
                        String monthly_card_limit_check = req.getParameter("monthly_card_limit_check");
                        String daily_card_amount_limit_check = req.getParameter("daily_card_amount_limit_check");
                        String weekly_card_amount_limit_check = req.getParameter("weekly_card_amount_limit_check");
                        String monthly_card_amount_limit_check = req.getParameter("monthly_card_amount_limit_check");

                        String daily_avg_ticket     = req.getParameter("daily_avg_ticket");
                        String weekly_avg_ticket    = req.getParameter("weekly_avg_ticket");
                        String monthly_avg_ticket   = req.getParameter("monthly_avg_ticket");

                        String min_trans_amount     = req.getParameter("min_trans_amount");
                        String max_trans_amount     = req.getParameter("max_trans_amount");

                        String isActive     = req.getParameter("isActive");
                        String priority     = req.getParameter("priority");
                        //String isTest = req.getParameter("isTest");
                        String isPSTTerminal        = req.getParameter("isPSTTerminal");
                        String isTokenizationActive = req.getParameter("isTokenizationActive");
                        String riskRuleActivation   = req.getParameter("riskRuleActivation");
                        String minPayoutAmount      = req.getParameter("min_payout_amount");
                        String settlementCurrency   = req.getParameter("settlement_currency");
                        String bin_routing          = req.getParameter("bin_routing");
                        String isCardWhitelisted    = req.getParameter("isCardWhitelisted");
                        String isEmailWhitelisted   = req.getParameter("isEmailWhitelisted");
                        String emi_support          = req.getParameter("emi_support");
                        String whitelisting         = req.getParameter("whitelistdetails");
                        String cardLimitCheck       = req.getParameter("cardLimitCheck");
                        String cardAmountLimitCheck = req.getParameter("cardAmountLimitCheck");
                        String amountLimitCheck     = req.getParameter("amountLimitCheck");
                        String addressDetails       = req.getParameter("addressDetails");
                        String addressValidation    = req.getParameter("addressValidation");
                        String is_recurring         = req.getParameter("is_recurring");
                        String isRestrictedTicketActive = req.getParameter("isRestrictedTicketActive");
                        String isManualRecurring        = req.getParameter("isManualRecurring");
                        String cardDetailRequired       = req.getParameter("cardDetailRequired");
                        String payoutActivation         = req.getParameter("payoutActivation");
                        String autoRedirectRequest      = req.getParameter("autoRedirectRequest");
                        String currency_conversion      = req.getParameter("currency_conversion");
                        String conversion_currency      = req.getParameter("conversion_currency");
                        String isCardEncryptionEnable   = req.getParameter("isCardEncryptionEnable");
                        String payout_priority          = req.getParameter("payout_priority");
                        //      String role = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));

//                    if(!functions.isValueNull(role))
//                    {
//                        role="Partner";
//                    }
                        String role                 = (String) session.getAttribute("role");
                        String username             = (String) session.getAttribute("username");
                         actionExecutorId           = (String) session.getAttribute("merchantid");
                        String actionExecutorName   = role + "-" + username;

                        for (String cardsList : cardList)
                        {
                            cardtypeid = cardsList;

                            for (String paymodelist : paymodlist)
                            {
                                paymodeid = paymodelist;

                                for (String accountidlist : accountlist)
                                {
                                    account_id      = accountidlist;
                                    boolean flag1   = partnerManager.isChackForAccountId(partnerid, account_id);

                                    if (flag1 == true)
                                    {
                                        if (partnerManager.isTerminalUnique(memberid, account_id, paymodeid, cardtypeid))
                                        {
                                            if (cardtypeid.equals(String.valueOf(CardTypeEnum.MASTER_CARD_CARDTYPE.getValue())))
                                            {
                                                flag = partnerManager.isMasterCardSupported(account_id);
                                            }
                                            if (flag == true)
                                            {
                                                String query = "insert into  member_account_mapping(daily_amount_limit,monthly_amount_limit,daily_card_limit,weekly_card_limit,monthly_card_limit," +
                                                        "memberid,accountid,paymodeid,cardtypeid,daily_card_amount_limit,weekly_card_amount_limit,monthly_card_amount_limit,min_transaction_amount," +
                                                        "max_transaction_amount,isActive,priority,isPSTTerminal,weekly_amount_limit,daily_avg_ticket,weekly_avg_ticket,monthly_avg_ticket," +
                                                        "isTokenizationActive,riskruleactivation,min_payout_amount,settlement_currency,binRouting,isCardWhitelisted,isEmailWhitelisted,emi_support," +
                                                        "whitelisting_details,cardLimitCheck,cardAmountLimitCheck,amountLimitCheck,addressDetails,addressValidation,is_recurring," +
                                                        "isRestrictedTicketActive,isManualRecurring,cardDetailRequired,payoutActivation,autoRedirectRequest,currency_conversion,conversion_currency," +
                                                        "isCardEncryptionEnable,actionExecutorId,actionExecutorName,dtstamp,payout_priority,daily_amount_limit_check,weekly_amount_limit_check," +
                                                        "monthly_amount_limit_check,daily_card_limit_check,weekly_card_limit_check,monthly_card_limit_check,daily_card_amount_limit_check," +
                                                        "weekly_card_amount_limit_check,monthly_card_amount_limit_check) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?,?,?,?,?,?,?,?)";
                                                pstmnt      = conn.prepareStatement(query);

                                                pstmnt.setString(1, daily_amount_limit);
                                                pstmnt.setString(2, monthly_amount_limit);
                                                pstmnt.setString(3, daily_card_limit);
                                                pstmnt.setString(4, weekly_card_limit);
                                                pstmnt.setString(5, monthly_card_limit);
                                                pstmnt.setString(6, memberid);
                                                pstmnt.setString(7, account_id);
                                                pstmnt.setString(8, paymodeid);
                                                pstmnt.setString(9, cardtypeid);
                                                pstmnt.setString(10, daily_card_amount_limit);
                                                pstmnt.setString(11, weekly_card_amount_limit);
                                                pstmnt.setString(12, monthly_card_amount_limit);
                                                pstmnt.setString(13, min_trans_amount);
                                                pstmnt.setString(14, max_trans_amount);
                                                pstmnt.setString(15, isActive);
                                                pstmnt.setString(16, priority);
                                                // pstmnt.setString(17, isTest);
                                                pstmnt.setString(17, isPSTTerminal);
                                                pstmnt.setString(18, weekly_amount_limit);
                                                pstmnt.setString(19, daily_avg_ticket);
                                                pstmnt.setString(20, weekly_avg_ticket);
                                                pstmnt.setString(21, monthly_avg_ticket);
                                                pstmnt.setString(22, isTokenizationActive);
                                                pstmnt.setString(23, riskRuleActivation);
                                                pstmnt.setString(24, minPayoutAmount);
                                                pstmnt.setString(25, settlementCurrency);
                                                pstmnt.setString(26, bin_routing);
                                                pstmnt.setString(27, isCardWhitelisted);
                                                pstmnt.setString(28, isEmailWhitelisted);
                                                pstmnt.setString(29, emi_support);
                                                pstmnt.setString(30, whitelisting);
                                                pstmnt.setString(31, cardLimitCheck);
                                                pstmnt.setString(32, cardAmountLimitCheck);
                                                pstmnt.setString(33, amountLimitCheck);
                                                pstmnt.setString(34, addressDetails);
                                                pstmnt.setString(35, addressValidation);
                                                pstmnt.setString(36, is_recurring);
                                                pstmnt.setString(37, isRestrictedTicketActive);
                                                pstmnt.setString(38, isManualRecurring);
                                                pstmnt.setString(39, cardDetailRequired);
                                                pstmnt.setString(40, payoutActivation);
                                                pstmnt.setString(41, autoRedirectRequest);
                                                pstmnt.setString(42, currency_conversion);
                                                pstmnt.setString(43, conversion_currency);
                                                pstmnt.setString(44, isCardEncryptionEnable);
                                                pstmnt.setString(45, actionExecutorId);
                                                pstmnt.setString(46, actionExecutorName);
                                                pstmnt.setString(47, payout_priority);
                                                pstmnt.setString(48, daily_amount_limit_check);
                                                pstmnt.setString(49, weekly_amount_limit_check);
                                                pstmnt.setString(50, monthly_amount_limit_check);
                                                pstmnt.setString(51, daily_card_limit_check);
                                                pstmnt.setString(52, weekly_card_limit_check);
                                                pstmnt.setString(53, monthly_card_limit_check);
                                                pstmnt.setString(54, daily_card_amount_limit_check);
                                                pstmnt.setString(55, weekly_card_amount_limit_check);
                                                pstmnt.setString(56, monthly_card_amount_limit_check);


                                                int k = pstmnt.executeUpdate();

                                                logger.error("insert query for pstmnt ---->" + pstmnt);

                                                if (k > 0)
                                                {
                                                    success = SetAccount_new_errormsg;
                                                }
                                            }
                                            else
                                            {
                                                error = error + "New Mastercard Terminal could not be added.";
                                            }
                                        }
                                        else
                                        {
                                            success = "Same Terminal Configuration Already Available";
                                        }
                                    }
                                    else
                                    {
                                        error = error + "Invalid Partner ID and Account ID mapping for " + account_id;
                                    }
                                }
                            }
                        }
                    }
                    else
                    {
                        req.setAttribute("error", error);
                    }
                }
                catch (Exception e)
                {
                    logger.error("Exception---" + e);
                    logger.error("Exception while Creating Terminal", e);
                    error = error + "Terminal has not created,please check with your input values";
                    req.setAttribute("error", error);
                }
                finally
                {
                    Database.closePreparedStatement(pstmnt);
                    Database.closeConnection(conn);
                }
            }

        }
        else
        {
            if (req.getParameterValues("terminalid") != null)
            {
                terminal_mainAll = req.getParameterValues("terminalid");
                if (Functions.checkArrayNull(terminal_mainAll) == null)
                {
                    req.setAttribute("accountids", partnerManager.loadGatewayAccount(partnerid));
                    req.setAttribute("paymodeids", terminalManager.getPaymodeType());
                    req.setAttribute("cardtypeids", terminalManager.getCardTypeID());
                    req.setAttribute("memberid", mId);
                    req.setAttribute("cardtypeid", cardtypeid);
                    req.setAttribute("paymodeid", paymodeid);
                    req.setAttribute("error1", "Select at least one transaction.<BR>");
                    RequestDispatcher rd = req.getRequestDispatcher("/membermappingpreference.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                }
            }
            else
            {
                req.setAttribute("accountids", partnerManager.loadGatewayAccount(partnerid));
                req.setAttribute("paymodeids", terminalManager.getPaymodeType());
                req.setAttribute("cardtypeids", terminalManager.getCardTypeID());
                req.setAttribute("memberid", mId);
                req.setAttribute("cardtypeid", cardtypeid);
                req.setAttribute("paymodeid", paymodeid);
                req.setAttribute("error1", "Select at least one transaction.<BR>");
                RequestDispatcher rd = req.getRequestDispatcher("/membermappingpreference.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
            }

            if (terminal_mainAll != null)
            {
                for (int k = 0; k < terminal_mainAll.length; k++)
                {
                    String terminal_main    = terminal_mainAll[k];
                    String action           = "";

                    if (!ESAPI.validator().isValidInput("action ", req.getParameter("action_" + terminal_main), "SafeString", 100, false))
                    {
                        logger.debug("Select valid action");
                        error += "Invalid Action Selected";
                    }

                    else
                        action = req.getParameter("action_" + terminal_main);

                    if (action.equalsIgnoreCase("UPDATE_" + terminal_main))
                    {
                        PreparedStatement pstmnt = null;
                        try
                        {
                            conn                            = Database.getConnection();
                            memberid                        = req.getParameter("memberid_" + terminal_main);
                            String accountid                = req.getParameter("accountid_" + terminal_main);
                            String accountIdAfterFillter    = (accountid.replaceAll("\\-.*", ""));
                            boolean flag                    = true;
                            if (functions.isValueNull(accountIdAfterFillter))
                            {

                                flag = partnerManager.isChackForAccountId(partnerid, accountIdAfterFillter);

                               /* if (flag == false)
                                {
                                    res.sendRedirect("/partner/Logout.jsp");
                                    return;
                                }*/
                            }
                            String terminalid           = req.getParameter("terminalid_" + terminal_main);
                            String daily_amount_limit   = req.getParameter("daily_amount_limit_" + terminal_main);
                            String weekly_amount_limit  = req.getParameter("weekly_amount_limit_" + terminal_main);
                            String monthly_amount_limit = req.getParameter("monthly_amount_limit_" + terminal_main);

                            String daily_card_limit     = req.getParameter("daily_card_limit_" + terminal_main);
                            String weekly_card_limit    = req.getParameter("weekly_card_limit_" + terminal_main);
                            String monthly_card_limit   = req.getParameter("monthly_card_limit_" + terminal_main);

                            String daily_card_amount_limit      = req.getParameter("daily_card_amount_limit_" + terminal_main);
                            String weekly_card_amount_limit     = req.getParameter("weekly_card_amount_limit_" + terminal_main);
                            String monthly_card_amount_limit    = req.getParameter("monthly_card_amount_limit_" + terminal_main);

                            String daily_amount_limit_check_for_terminals = req.getParameter("daily_amount_limit_check_for_terminals_" + terminal_main);
                            String weekly_amount_limit_check_for_terminals = req.getParameter("weekly_amount_limit_check_for_terminals_" + terminal_main);
                            String monthly_amount_limit_check_for_terminals = req.getParameter("monthly_amount_limit_check_for_terminals_" + terminal_main);
                            String daily_card_limit_check_for_terminals = req.getParameter("daily_card_limit_check_for_terminals_" + terminal_main);
                            String weekly_card_limit_check_for_terminals = req.getParameter("weekly_card_limit_check_for_terminals_" + terminal_main);
                            String monthly_card_limit_check_for_terminals = req.getParameter("monthly_card_limit_check_for_terminals_" + terminal_main);
                            String daily_card_amount_limit_check_for_terminals = req.getParameter("daily_card_amount_limit_check_for_terminals_" + terminal_main);
                            String weekly_card_amount_limit_check_for_terminals = req.getParameter("weekly_card_amount_limit_check_for_terminals_" + terminal_main);
                            String monthly_card_amount_limit_check_for_terminals = req.getParameter("monthly_card_amount_limit_check_for_terminals_" + terminal_main);

                            String daily_avg_ticket     = req.getParameter("daily_avg_ticket_" + terminal_main);
                            String weekly_avg_ticket    = req.getParameter("weekly_avg_ticket_" + terminal_main);
                            String monthly_avg_ticket   = req.getParameter("monthly_avg_ticket_" + terminal_main);

                            String min_trans_amount     = req.getParameter("min_trans_amount_" + terminal_main);
                            String max_trans_amount     = req.getParameter("max_trans_amount_" + terminal_main);

                            String isActive     = req.getParameter("isActive_" + terminal_main);
                            String priority     = req.getParameter("priority_" + terminal_main);
                            // String isTest = req.getParameter("isTest");
                            String isPSTTerminal        = req.getParameter("isPSTTerminal_" + terminal_main);
                            String isTokenizationActive = req.getParameter("isTokenizationActive_" + terminal_main);
                            String addressDetails       = req.getParameter("addressDetails_" + terminal_main);
                            String addressValidation    = req.getParameter("addressValidation_" + terminal_main);
                            String riskRuleActivation   = req.getParameter("riskRuleActivation_" + terminal_main);
                            String minPayoutAmount      = req.getParameter("min_payout_amount_" + terminal_main);
                            String settlementCurrency   = req.getParameter("settlement_currency_" + terminal_main);
                            String bin_routing          = req.getParameter("bin_routing_" + terminal_main);
                            String isCardWhitelisted    = req.getParameter("isCardWhitelisted_" + terminal_main);
                            String isEmailWhitelisted   = req.getParameter("isEmailWhitelisted_" + terminal_main);
                            String emi_support          = req.getParameter("emi_support_" + terminal_main);
                            String whitelisting         = req.getParameter("whitelistdetails_" + terminal_main);
                            String cardLimitCheck       = req.getParameter("cardLimitCheck_" + terminal_main);
                            String cardAmountLimitCheck = req.getParameter("cardAmountLimitCheck_" + terminal_main);
                            String amountLimitCheck     = req.getParameter("amountLimitCheck_" + terminal_main);
                            String is_recurring         = req.getParameter("is_recurring_" + terminal_main);
                            String isRestrictedTicketActive = req.getParameter("isRestrictedTicketActive_" + terminal_main);
                            String isManualRecurring        = req.getParameter("isManualRecurring_" + terminal_main);
                            String cardDetailRequired       = req.getParameter("cardDetailRequired_" + terminal_main);
                            String payoutActivation         = req.getParameter("payoutActivation_" + terminal_main);
                            String autoRedirectRequest      = req.getParameter("autoRedirectRequest_" + terminal_main);
                            String currency_conversion      = req.getParameter("currency_conversion_" + terminal_main);
                            String conversion_currency      = req.getParameter("conversion_currency_" + terminal_main);
                            String isCardEncryptionEnable   = req.getParameter("isCardEncryptionEnable_" + terminal_main);
                            String onchangedValues          = req.getParameter("onchangedvalue_" + terminal_main);
                            String payout_priority          = req.getParameter("payout_priority_" + terminal_main);
                            logger.debug("payout_priority --->"+payout_priority);
                            //System.out.println("onchangedValues"+onchangedValues);


                            if (!ESAPI.validator().isValidInput("daily_amount_limit", req.getParameter("daily_amount_limit_" + terminal_main), "Numbers", 20, false))
                                error = "Invalid Daily Amount limit for " + terminal_main;

                            if (!ESAPI.validator().isValidInput("monthly_amount_limit", req.getParameter("monthly_amount_limit_" + terminal_main), "Numbers", 20, false))
                                error = error + "Invalid Monthly Amount limit for " + terminal_main;

                            if (!ESAPI.validator().isValidInput("weekly_amount_limit", req.getParameter("weekly_amount_limit_" + terminal_main), "Numbers", 100, false))
                                error = error + "Invalid Weekly Amount limit for " + terminal_main;

                            if (!ESAPI.validator().isValidInput("daily_card_limit", req.getParameter("daily_card_limit_" + terminal_main), "Numbers", 10, false))
                                error = error + "Invalid Daily Card limit for " + terminal_main;

                            if (!ESAPI.validator().isValidInput("weekly_card_limit", req.getParameter("weekly_card_limit_" + terminal_main), "Numbers", 10, false))
                                error = error + "Invalid Weekly Card limit for " + terminal_main;

                            if (!ESAPI.validator().isValidInput("monthly_card_limit", req.getParameter("monthly_card_limit_" + terminal_main), "Numbers", 10, false))
                                error = error + "Invalid Monthly card limit for " + terminal_main;

                            if (!ESAPI.validator().isValidInput("daily_card_amount_limit", req.getParameter("daily_card_amount_limit_" + terminal_main), "Numbers", 20, false))
                                error = error + "Invalid Daily Card Amount Limit for " + terminal_main;

                            if (!ESAPI.validator().isValidInput("weekly_card_amount_limit", req.getParameter("weekly_card_amount_limit_" + terminal_main), "Numbers", 20, false))
                                error = error + "Invalid Weekly Card Amount Limit for " + terminal_main;

                            if (!ESAPI.validator().isValidInput("monthly_card_amount_limit", req.getParameter("monthly_card_amount_limit_" + terminal_main), "Numbers", 20, false))
                                error = error + "Invalid Monthly Card Amount Limit for " + terminal_main;

                            if (!ESAPI.validator().isValidInput("min_trans_amount", req.getParameter("min_trans_amount_" + terminal_main), "Numbers", 10, false))
                                error = error + "Invalid Min Transaction Amount for " + terminal_main;

                            if (!ESAPI.validator().isValidInput("max_trans_amount", req.getParameter("max_trans_amount_" + terminal_main), "Numbers", 20, false))
                                error = error + "Invalid Max Transaction Amount for " + terminal_main;

                            if (!ESAPI.validator().isValidInput("isActive", req.getParameter("isActive_" + terminal_main), "SafeString", 1, false))
                                error = error + "Invalid Is Active for " + terminal_main;

                            if (!ESAPI.validator().isValidInput("priority", req.getParameter("priority_" + terminal_main), "Numbers", 1, false))
                                error = error + "Invalid Priority for " + terminal_main;

                        /*if (!ESAPI.validator().isValidInput("isTest", req.getParameter("isTest_" + terminal_main), "SafeString", 1, false))
                            error = error + "Invalid Is Test for " + terminal_main;*/

                            if (!ESAPI.validator().isValidInput("memberid", req.getParameter("memberid_" + terminal_main), "OnlyNumber", 10, false))
                                error = error + "Invalid Merchant Id for " + terminal_main;

                            if (!ESAPI.validator().isValidInput("accountid", req.getParameter("accountid_" + terminal_main), "Numbers", 10, false))
                                error = error + "Invalid AccountID for " + terminal_main;

                            if (!ESAPI.validator().isValidInput("terminalid", req.getParameter("terminalid_" + terminal_main), "Numbers", 6, false))
                                error = error + "Invalid Terminal ID for " + terminal_main;

                            if (!ESAPI.validator().isValidInput("daily_avg_ticket", req.getParameter("daily_avg_ticket_" + terminal_main), "AmountStr", 10, false))
                                error = error + "Invalid Daily Avg Ticket for " + terminal_main;

                            if (!ESAPI.validator().isValidInput("weekly_avg_ticket", req.getParameter("weekly_avg_ticket_" + terminal_main), "AmountStr", 10, false))
                                error = error + "Invalid Weekly Avg Ticket for " + terminal_main;

                            if (!ESAPI.validator().isValidInput("monthly_avg_ticket", req.getParameter("monthly_avg_ticket_" + terminal_main), "AmountStr", 10, false))
                                error = error + "Invalid Monthly Avg Ticket for " + terminal_main;

                            if (!ESAPI.validator().isValidInput("min_payout_amount", req.getParameter("min_payout_amount_" + terminal_main), "AmountStr", 20, false))
                                error = error + "Invalid Min Payout Amount for " + terminal_main;

                           if (!ESAPI.validator().isValidInput("processor_partnerid", req.getParameter("processor_partnerid_" + terminal_main), "Number", 20, true))
                                error = error + "Invalid Processor Partner Id for " + terminal_main;

                            if (error.equals(""))
                            {
                                String query    = "update member_account_mapping set daily_amount_limit=?,monthly_amount_limit=?,daily_card_limit=?,weekly_card_limit=?,monthly_card_limit=?," +
                                                    "daily_card_amount_limit=?,weekly_card_amount_limit=?,monthly_card_amount_limit=?,min_transaction_amount=?,max_transaction_amount=?,isActive=?," +
                                                    "priority=?,isPSTTerminal=?,weekly_amount_limit=?,daily_avg_ticket=?,weekly_avg_ticket=?,monthly_avg_ticket=?,isTokenizationActive=?,addressDetails=?," +
                                                    "addressValidation=?,riskruleactivation=?,min_payout_amount=?,settlement_currency=?,binRouting=?,isCardWhitelisted=?,isEmailWhitelisted=?,emi_support=?," +
                                                    "whitelisting_details=?,cardLimitCheck=?,cardAmountLimitCheck=?,amountLimitCheck=?,is_recurring=?,isRestrictedTicketActive=?,isManualRecurring=?,cardDetailRequired=?," +
                                                    "payoutActivation=?,autoRedirectRequest=?,currency_conversion=?,conversion_currency=?,isCardEncryptionEnable=?,payout_priority=?,daily_amount_limit_check=?," +
                                                    "weekly_amount_limit_check=?,monthly_amount_limit_check=?,daily_card_limit_check=?,weekly_card_limit_check=?,monthly_card_limit_check=?,daily_card_amount_limit_check=?," +
                                                    "weekly_card_amount_limit_check=?,monthly_card_amount_limit_check=? where memberid=? and accountid=? and terminalid=?";
                                pstmnt          = conn.prepareStatement(query);

                                pstmnt.setString(1, daily_amount_limit);
                                pstmnt.setString(2, monthly_amount_limit);
                                pstmnt.setString(3, daily_card_limit);
                                pstmnt.setString(4, weekly_card_limit);
                                pstmnt.setString(5, monthly_card_limit);
                                pstmnt.setString(6, daily_card_amount_limit);
                                pstmnt.setString(7, weekly_card_amount_limit);
                                pstmnt.setString(8, monthly_card_amount_limit);
                                pstmnt.setString(9, min_trans_amount);
                                pstmnt.setString(10, max_trans_amount);
                                pstmnt.setString(11, isActive);
                                pstmnt.setString(12, priority);
                                // pstmnt.setString(13, isTest);
                                pstmnt.setString(13, isPSTTerminal);
                                pstmnt.setString(14, weekly_amount_limit);
                                pstmnt.setString(15, daily_avg_ticket);
                                pstmnt.setString(16, weekly_avg_ticket);
                                pstmnt.setString(17, monthly_avg_ticket);
                                pstmnt.setString(18, isTokenizationActive);
                                pstmnt.setString(19, addressDetails);
                                pstmnt.setString(20, addressValidation);
                                pstmnt.setString(21, riskRuleActivation);
                                pstmnt.setString(22, minPayoutAmount);
                                pstmnt.setString(23, settlementCurrency);
                                pstmnt.setString(24, bin_routing);
                                pstmnt.setString(25, isCardWhitelisted);
                                pstmnt.setString(26, isEmailWhitelisted);
                                pstmnt.setString(27, emi_support);
                                pstmnt.setString(28, whitelisting);
                                pstmnt.setString(29, cardLimitCheck);
                                pstmnt.setString(30, cardAmountLimitCheck);
                                pstmnt.setString(31, amountLimitCheck);
                                pstmnt.setString(32, is_recurring);
                                pstmnt.setString(33, isRestrictedTicketActive);
                                pstmnt.setString(34, isManualRecurring);
                                pstmnt.setString(35, cardDetailRequired);
                                pstmnt.setString(36, payoutActivation);
                                pstmnt.setString(37, autoRedirectRequest);
                                pstmnt.setString(38, currency_conversion);
                                pstmnt.setString(39, conversion_currency);
                                pstmnt.setString(40, isCardEncryptionEnable);
                                pstmnt.setString(41, payout_priority);
                                pstmnt.setString(42, daily_amount_limit_check_for_terminals);
                                pstmnt.setString(43, weekly_amount_limit_check_for_terminals);
                                pstmnt.setString(44, monthly_amount_limit_check_for_terminals);
                                pstmnt.setString(45, daily_card_limit_check_for_terminals);
                                pstmnt.setString(46, weekly_card_limit_check_for_terminals);
                                pstmnt.setString(47, monthly_card_limit_check_for_terminals);
                                pstmnt.setString(48, daily_card_amount_limit_check_for_terminals);
                                pstmnt.setString(49, weekly_card_amount_limit_check_for_terminals);
                                pstmnt.setString(50, monthly_card_amount_limit_check_for_terminals);
                                pstmnt.setString(51, memberid);
                                pstmnt.setString(52, accountIdAfterFillter);
                                pstmnt.setString(53, terminalid);


                                pstmnt.executeUpdate();

                                success     = "Terminal Configuration Updated Successfully";
                                logger.debug("Creating Activity for edit Gatway account --->"+pstmnt);
                                String remoteAddr   = Functions.getIpAddress(req);
                                int serverPort      = req.getServerPort();
                                String servletPath  = req.getServletPath();
                                String header       = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;
                                if(functions.isValueNull(onchangedValues))
                                {
                                    activityTrackerVOs.setInterface(ActivityLogParameters.PARTNER.toString());
                                    activityTrackerVOs.setUser_name(Login + "-" + actionExecutorId);
                                    activityTrackerVOs.setRole(partner.getUserRole(user));
                                    activityTrackerVOs.setAction(ActivityLogParameters.EDIT.toString());
                                    activityTrackerVOs.setModule_name(ActivityLogParameters.MERCHANT_ACCOUNT_MAPPING.toString());
                                    activityTrackerVOs.setLable_values(onchangedValues);
                                    activityTrackerVOs.setDescription(ActivityLogParameters.MEMBERID.toString() + "-" + memberid + " " + ActivityLogParameters.TERMINALID.toString() + "-" + terminal_main);
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
                            }
                        }

                        catch (Exception e)
                        {
                            logger.error("Exception---" + e);
                            logger.error("Exception While Updating Terminal Configuration", e);
                            error = error + "Exception While Updating Terminal Configuration";
                            req.setAttribute("error", error);
                        }

                        finally
                        {
                            Database.closePreparedStatement(pstmnt);
                            Database.closeConnection(conn);
                        }
                    }
                    if (action.equalsIgnoreCase("DELETE_" + terminal_main))
                    {

                        if (!ESAPI.validator().isValidInput("memberid", req.getParameter("memberid_" + terminal_main), "OnlyNumber", 10, false))
                            error = error + "Invalid Merchant Id for " + terminal_main;

                        if (!ESAPI.validator().isValidInput("accountid", req.getParameter("accountid_" + terminal_main), "Numbers", 10, false))
                            error = error + "Invalid AccountID for " + terminal_main;

                        if (!ESAPI.validator().isValidInput("terminalid", req.getParameter("terminalid_" + terminal_main), "Numbers", 6, false))
                            error = error + "Invalid Terminal ID for " + terminal_main;

                        if (error.equals(""))
                        {
                            memberid            = req.getParameter("memberid_" + terminal_main);
                            String accountid    = req.getParameter("accountid_" + terminal_main);
                            String terminalid   = req.getParameter("terminalid_" + terminal_main);
                            PreparedStatement pstmnt    = null;
                            ResultSet rs                = null;
                            try
                            {
                                String accountIdAfterFillter    = (accountid.replaceAll("\\-.*", ""));
                                boolean flag                    = true;
                                if (functions.isValueNull(accountIdAfterFillter))
                                {
                                    flag = partnerManager.isChackForAccountId(partnerid, accountIdAfterFillter);
                                    if (flag == false)
                                    {
                                        res.sendRedirect("/partner/Logout.jsp");
                                        return;
                                    }
                                }
                                conn            = Database.getConnection();
                                Hashtable hash  = new Hashtable();

                                String query    = "select * from member_account_mapping where memberid=? and accountid=? and terminalid=?";
                                pstmnt          = conn.prepareStatement(query);
                                pstmnt.setString(1, memberid);
                                pstmnt.setString(2, accountIdAfterFillter);
                                pstmnt.setString(3, terminalid);
                                rs                              = pstmnt.executeQuery();
                                ResultSetMetaData rsMetaData    = rs.getMetaData();
                                int count                       = rsMetaData.getColumnCount();
                                int i = 0;
                                while (rs.next())
                                {
                                    for (i = 1; i <= count; i++)
                                    {
                                        if (rs.getString(i) != null)
                                        {

                                            hash.put(rsMetaData.getColumnLabel(i), rs.getString(i));

                                        }
                                    }
                                }
                                query   = "insert into  member_account_mapping_archive(memberid,accountid,paymodeid,cardtypeid,chargePercentage,fixApprovalCharge,fixDeclinedCharge,taxper,reversalcharge,withdrawalcharge,chargebackcharge,reservePercentage,fraudVerificationCharge,annualCharge,setupCharge,fxClearanceChargePercentage,monthlyGatewayCharge,monthlyAccountMntCharge,reportCharge,fraudulentCharge,autoRepresentationCharge,interchangePlusCharge,daily_amount_limit,monthly_amount_limit,daily_card_limit,weekly_card_limit,monthly_card_limit,daily_card_amount_limit,weekly_card_amount_limit,monthly_card_amount_limit,min_transaction_amount,max_transaction_amount,isActive,priority,isPSTTerminal,riskruleactivation,daily_avg_ticket,weekly_avg_ticket,monthly_avg_ticket,terminalid) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                                pstmnt  = conn.prepareStatement(query);

                                pstmnt.setString(1, (String) hash.get("memberid"));
                                pstmnt.setString(2, (String) hash.get("accountid"));
                                pstmnt.setString(3, (String) hash.get("paymodeid"));
                                pstmnt.setString(4, (String) hash.get("cardtypeid"));
                                pstmnt.setString(5, (String) hash.get("chargePercentage"));
                                pstmnt.setString(6, (String) hash.get("fixApprovalCharge"));
                                pstmnt.setString(7, (String) hash.get("fixDeclinedCharge"));
                                pstmnt.setString(8, (String) hash.get("taxper"));
                                pstmnt.setString(9, (String) hash.get("reversalcharge"));
                                pstmnt.setString(10, (String) hash.get("withdrawalcharge"));
                                pstmnt.setString(11, (String) hash.get("chargebackcharge"));
                                pstmnt.setString(12, (String) hash.get("reservePercentage"));
                                pstmnt.setString(13, (String) hash.get("fraudVerificationCharge"));
                                pstmnt.setString(14, (String) hash.get("annualCharge"));
                                pstmnt.setString(15, (String) hash.get("setupCharge"));
                                pstmnt.setString(16, (String) hash.get("fxClearanceChargePercentage"));
                                pstmnt.setString(17, (String) hash.get("monthlyGatewayCharge"));
                                pstmnt.setString(18, (String) hash.get("monthlyAccountMntCharge"));
                                pstmnt.setString(19, (String) hash.get("reportCharge"));
                                pstmnt.setString(20, (String) hash.get("fraudulentCharge"));
                                pstmnt.setString(21, (String) hash.get("autoRepresentationCharge"));
                                pstmnt.setString(22, (String) hash.get("interchangePlusCharge"));
                                pstmnt.setString(23, (String) hash.get("daily_amount_limit"));
                                pstmnt.setString(24, (String) hash.get("monthly_amount_limit"));
                                pstmnt.setString(25, (String) hash.get("daily_card_limit"));
                                pstmnt.setString(26, (String) hash.get("weekly_card_limit"));
                                pstmnt.setString(27, (String) hash.get("monthly_card_limit"));
                                pstmnt.setString(28, (String) hash.get("daily_card_amount_limit"));
                                pstmnt.setString(29, (String) hash.get("weekly_card_amount_limit"));
                                pstmnt.setString(30, (String) hash.get("monthly_card_amount_limit"));
                                pstmnt.setString(31, (String) hash.get("min_trans_amount"));
                                pstmnt.setString(32, (String) hash.get("max_trans_amount"));
                                pstmnt.setString(33, (String) hash.get("isActive"));
                                pstmnt.setString(34, (String) hash.get("priority"));
                                //pstmnt.setString(35, (String) hash.get("isTest"));
                                pstmnt.setString(35, (String) hash.get("isPSTTerminal"));
                                pstmnt.setString(36, (String) hash.get("riskruleactivation"));
                                pstmnt.setString(37, (String) hash.get("daily_avg_ticket"));
                                pstmnt.setString(38, (String) hash.get("weekly_avg_ticket"));
                                pstmnt.setString(39, (String) hash.get("monthly_avg_ticket"));
                                pstmnt.setString(40, (String) hash.get("terminalid"));

                                int countk  = pstmnt.executeUpdate();
                                if (countk > 0)
                                {
                                    query   = "delete from member_account_mapping  where memberid=? and accountid=? and terminalid=?";
                                    pstmnt  = conn.prepareStatement(query);

                                    pstmnt.setString(1, memberid);
                                    pstmnt.setString(2, accountid);
                                    pstmnt.setString(3, terminalid);

                                    int l = pstmnt.executeUpdate();
                                    if (l > 0)
                                    {
                                        success = "Terminal Configuration Has Removed Successfully for " + terminal_main;
                                    }
                                }
                                else
                                {
                                    error = error + "Terminal Configuration Could Not Removed for " + terminal_main;
                                }
                            }
                            catch (Exception e)
                            {
                                logger.error("Exception Has Occurred While Removing Terminal Configuration for " + terminal_main, e);
                                error = error + e.getMessage();
                            }
                            finally
                            {
                                Database.closeResultSet(rs);
                                Database.closePreparedStatement(pstmnt);
                                Database.closeConnection(conn);
                            }
                        }
                    }
                }
            }
        }

            req.setAttribute("accountids", partnerManager.loadGatewayAccount(partnerid));
            req.setAttribute("paymodeids", terminalManager.getPaymodeType());
            req.setAttribute("cardtypeids", terminalManager.getCardTypeID());
            req.setAttribute("success1", success);
            req.setAttribute("error", error);
            req.setAttribute("memberid", mId);
            req.setAttribute("cardtypeid", cardtypeid);
            req.setAttribute("paymodeid", paymodeid);
            logger.debug("forwarding to member preference");
            RequestDispatcher rd = req.getRequestDispatcher("/membermappingpreference.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
        }

        Hashtable loadGatewayAccounts ()
        {
            return GatewayAccountService.getMerchantDetails();
        }

        private String validateMandatoryParameters (HttpServletRequest req, String action)
        {
            InputValidator inputValidator               = new InputValidator();
            String error                                = "";
            String EOL                                  = "<BR>";
            List<InputFields> inputFieldsListOptional   = new ArrayList<InputFields>();

            if (action.equalsIgnoreCase("UPDATE"))
            {
                inputFieldsListOptional.add(InputFields.DAILY_AMT_LIMIT);
                inputFieldsListOptional.add(InputFields.MONTHLY_AMT_LIMIT);
                inputFieldsListOptional.add(InputFields.WEEKLY_AMT_LIMIT);
                inputFieldsListOptional.add(InputFields.DAILY_CARD_LIMIT);
                inputFieldsListOptional.add(InputFields.WEEKLY_CARD_LIMIT);
                inputFieldsListOptional.add(InputFields.MONTHLY_CARD_LIMIT);
                inputFieldsListOptional.add(InputFields.DAILY_CARD_AMT_LIMIT);
                inputFieldsListOptional.add(InputFields.WEEKLY_CARD_AMT_LIMIT);
                inputFieldsListOptional.add(InputFields.MONTHLY_CARD_AMT_LIMIT);
                inputFieldsListOptional.add(InputFields.MIN_TRANSE_AMT);
                inputFieldsListOptional.add(InputFields.MAX_TRANSE_AMT);
                inputFieldsListOptional.add(InputFields.IS_ACTIVE);
                inputFieldsListOptional.add(InputFields.PRIORITY);
                //inputFieldsListOptional.add(InputFields.IS_TEST);
                inputFieldsListOptional.add(InputFields.MEMBERID);
                inputFieldsListOptional.add(InputFields.ACCOUNTID_SMALL);
                inputFieldsListOptional.add(InputFields.DAILY_AVG_TICKET);
                inputFieldsListOptional.add(InputFields.WEEKLY_AVG_TICKET);
                inputFieldsListOptional.add(InputFields.MONTHLY_AVG_TICKET);
                inputFieldsListOptional.add(InputFields.MIN_PAYOUT_AMOUNT);
            }
            else if (action.equalsIgnoreCase("UPDATE2"))
            {
                inputFieldsListOptional.add(InputFields.MEMBERID);
                inputFieldsListOptional.add(InputFields.ACCOUNTID_SMALL);
                inputFieldsListOptional.add(InputFields.CHARGE_PER);
                inputFieldsListOptional.add(InputFields.FIX_APPROVE_CHARGE);
                inputFieldsListOptional.add(InputFields.FIX_DECLINE_CHARGE);
                inputFieldsListOptional.add(InputFields.TAX_PER);
                inputFieldsListOptional.add(InputFields.REVERSECHARGE);
                inputFieldsListOptional.add(InputFields.REVERSE_PERCENTAGE);
                inputFieldsListOptional.add(InputFields.WITHDRAWCHARGE);
                inputFieldsListOptional.add(InputFields.CHARGEBACKCHARGE);
                inputFieldsListOptional.add(InputFields.FRAUDE_VARIFICATION_CHARGE);
                inputFieldsListOptional.add(InputFields.ANNUAL_CHARGE);
                inputFieldsListOptional.add(InputFields.SETUP_CHARGE);
                inputFieldsListOptional.add(InputFields.FX_CLERANCE_CHARGE_PER);
                inputFieldsListOptional.add(InputFields.MONTHLY_GATEWAY_CHARGE);
                inputFieldsListOptional.add(InputFields.MONTHLY_ACC_MN_CHARGE);
                inputFieldsListOptional.add(InputFields.REPORT_CHARGE);
                inputFieldsListOptional.add(InputFields.FRAUDULENT_CHARGE);
                inputFieldsListOptional.add(InputFields.AUTO_REPRESENTATION_CHARGE);
                inputFieldsListOptional.add(InputFields.INTERCHANGE_PLUS_CHARGE);
            }
            else if (action.equalsIgnoreCase("DELETE"))
            {
                inputFieldsListOptional.add(InputFields.MEMBERID);
                inputFieldsListOptional.add(InputFields.ACCOUNTID_SMALL);
                inputFieldsListOptional.add(InputFields.TERMINALID);
            }
            else if (action.equalsIgnoreCase("action_create"))
            {
                inputFieldsListOptional.add(InputFields.DAILY_AMT_LIMIT);
                inputFieldsListOptional.add(InputFields.WEEKLY_AMT_LIMIT);
                inputFieldsListOptional.add(InputFields.MONTHLY_AMT_LIMIT);
                inputFieldsListOptional.add(InputFields.DAILY_CARD_LIMIT);
                inputFieldsListOptional.add(InputFields.WEEKLY_CARD_LIMIT);
                inputFieldsListOptional.add(InputFields.MONTHLY_CARD_LIMIT);
                inputFieldsListOptional.add(InputFields.DAILY_CARD_AMT_LIMIT);
                inputFieldsListOptional.add(InputFields.WEEKLY_CARD_AMT_LIMIT);
                inputFieldsListOptional.add(InputFields.MONTHLY_CARD_AMT_LIMIT);
                inputFieldsListOptional.add(InputFields.MIN_TRANSE_AMT);
                inputFieldsListOptional.add(InputFields.MAX_TRANSE_AMT);
                inputFieldsListOptional.add(InputFields.IS_ACTIVE);
                inputFieldsListOptional.add(InputFields.PRIORITY);
                //inputFieldsListOptional.add(InputFields.IS_TEST);
                inputFieldsListOptional.add(InputFields.MEMBERID);
                //inputFieldsListOptional.add(InputFields.ACCOUNTID_SMALL);
                inputFieldsListOptional.add(InputFields.DAILY_AVG_TICKET);
                inputFieldsListOptional.add(InputFields.WEEKLY_AVG_TICKET);
                inputFieldsListOptional.add(InputFields.MONTHLY_AVG_TICKET);
            }

            ValidationErrorList errorList = new ValidationErrorList();
            inputValidator.InputValidations(req, inputFieldsListOptional, errorList, false);

            if (!errorList.isEmpty())
            {
                for (InputFields inputFields : inputFieldsListOptional)
                {
                    if (errorList.getError(inputFields.toString()) != null)
                    {
                        logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                        error = error.concat(errorList.getError(inputFields.toString()).getMessage() + EOL);
                    }
                }
            }
            return error;
        }
    }