import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.manager.TerminalManager;
import com.manager.enums.ActivityLogParameters;
import com.manager.vo.ActivityTrackerVOs;
import com.manager.vo.TerminalVO;
import com.payment.Enum.CardTypeEnum;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

public class SetAccount extends HttpServlet
{
    private static Logger logger = new Logger(SetAccount.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
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

        String error = "";
        String success = "";

        Connection conn = null;
        TerminalManager terminalManager = new TerminalManager();
        GatewayAccountService gatewayAccountService = new GatewayAccountService();
        TerminalVO terminalVO = new TerminalVO();
        Functions functions = new Functions();
        ActivityTrackerVOs activityTrackerVOs = new ActivityTrackerVOs();

        String Login=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");


        String[] terminal_mainAll = null;
        String action1 = req.getParameter("action_create");
        if (functions.isValueNull(action1))
        {
            if (action1.equalsIgnoreCase("CREATE"))
            {
                try
                {
                    error = validateMandatoryParameters(req, action1);
                    error = validateOptionalParameters(req, action1);
                    List<String> cardList = new ArrayList();
                    List<String> paymodlist = new ArrayList();
                    if (error.equals(""))
                    {
                        String pgtypeid = "";
                        if (functions.isValueNull(req.getParameter("pgtypeid")))
                        {
                            String str[] = req.getParameter("pgtypeid").split("-");
                            pgtypeid = str[2];
                        }
                        else
                        {
                            error = error + "Invalid Gateway OR AccountID. Please select a proper Gateway and AccountID.";
                        }
                        String accountid = "";
                        if (functions.isValueNull(pgtypeid))
                        {
                            if (gatewayAccountService.isGatewayAccountIDMapped(pgtypeid, req.getParameter("accountid")))
                            {
                                accountid = req.getParameter("accountid");
                            }
                            else
                            {
                                error = error + "AccountID doesn't exist.";
                            }
                        }
                        String memberid = "";
                        boolean flag1 = false;
                        memberid = req.getParameter("memberid");
                        flag1 = functions.isMember(memberid);
                        if (functions.isValueNull(memberid) && (flag1 == true))
                        {
                            memberid = req.getParameter("memberid");
                        }
                        else
                        {
                            memberid = "";
                            error = error + "MemberID doesn't exist.";
                        }
                        String cardtypeid = "";
                        String paymodeid = "";
                        String paymodeidNew = req.getParameter("paymodeid");
                        String cardtypeidNew = req.getParameter("cardtypeid");
                        String[] elements_paymodeid = paymodeidNew.split(",");
                        String[] elements_cardtypeid = cardtypeidNew.split(",");
                        cardList = Arrays.asList(elements_cardtypeid);
                        paymodlist = Arrays.asList(elements_paymodeid);
                        System.out.println("cardList" + cardList.size());
                        System.out.println("paymodlist" + paymodlist.size());
                        for (String cardsList : cardList)
                        {
                            cardtypeid = cardsList;

                            for (String paymodelist : paymodlist)
                            {
                                paymodeid = paymodelist;

                                if (terminalManager.isTerminalUnique(memberid, accountid, paymodeid, cardtypeid))
                                {
                                    String daily_amount_limit = req.getParameter("daily_amount_limit");
                                    String monthly_amount_limit = req.getParameter("monthly_amount_limit");


                                    String daily_card_limit = req.getParameter("daily_card_limit");
                                    String weekly_card_limit = req.getParameter("weekly_card_limit");
                                    String monthly_card_limit = req.getParameter("monthly_card_limit");

                                    String daily_card_amount_limit = req.getParameter("daily_card_amount_limit");
                                    String weekly_card_amount_limit = req.getParameter("weekly_card_amount_limit");
                                    String monthly_card_amount_limit = req.getParameter("monthly_card_amount_limit");

                                    String daily_avg_ticket = req.getParameter("daily_avg_ticket");
                                    String weekly_avg_ticket = req.getParameter("weekly_avg_ticket");
                                    String monthly_avg_ticket = req.getParameter("monthly_avg_ticket");

                                    String min_trans_amount = req.getParameter("min_trans_amount");
                                    String max_trans_amount = req.getParameter("max_trans_amount");
                                    String isActive = req.getParameter("isActive");
                                    String priority = req.getParameter("priority");
                                    String payout_priority = req.getParameter("payout_priority");
                                    String isTest = req.getParameter("isTest");
                                    String weekly_amount_limit = req.getParameter("weekly_amount_limit");
                                    String isRecurring = req.getParameter("is_recurring");
                                    String isRestrictedTicketActive = req.getParameter("isRestrictedTicketActive");
                                    String isTokenizationActive = req.getParameter("isTokenizationActive");
                                    String isManualRecurring = req.getParameter("isManualRecurring");
                                    String addressDetails = req.getParameter("addressDetails");
                                    String daily_amount_limit_check = req.getParameter("daily_amount_limit_check");
                                    String weekly_amount_limit_check = req.getParameter("weekly_amount_limit_check");
                                    String monthly_amount_limit_check = req.getParameter("monthly_amount_limit_check");
                                    String daily_card_limit_check = req.getParameter("daily_card_limit_check");
                                    String weekly_card_limit_check = req.getParameter("weekly_card_limit_check");
                                    String monthly_card_limit_check = req.getParameter("monthly_card_limit_check");
                                    String daily_card_amount_limit_check = req.getParameter("daily_card_amount_limit_check");
                                    String weekly_card_amount_limit_check = req.getParameter("weekly_card_amount_limit_check");
                                    String monthly_card_amount_limit_check = req.getParameter("monthly_card_amount_limit_check");
                                    String addressValidation = req.getParameter("addressValidation");
                                    String cardDetailRequired = req.getParameter("cardDetailRequired");
                                    String isPSTTerminal = req.getParameter("isPSTTerminal");
                                    String isCardEncryptionEnable = req.getParameter("isCardEncryptionEnable");
                                    String riskRuleActivation = req.getParameter("riskruleactivation");
                                    String settlementCurrency = req.getParameter("settlementcurrency");
                                    String minPayoutAmount = req.getParameter("min_payout_amount");
                                    String payoutActivation = req.getParameter("payoutactivation");
                                    String autoRedirectRequest = req.getParameter("autoRedirectRequest");
                                    String isCardWhitelisted = req.getParameter("isCardWhitelisted");
                                    String isEmailWhitelisted = req.getParameter("isEmailWhitelisted");
                                    String currencyConversion = req.getParameter("currency_conversion");
                                    String conversionCurrency = req.getParameter("conversion_currency");
                                    String bin_routing = req.getParameter("bin_routing");
                                    String emi_support = req.getParameter("emi_support");
                                    String whitelisting = req.getParameter("whitelistdetails");
                                    String cardLimitCheck = req.getParameter("cardLimitCheck");
                                    String cardAmountLimitCheck = req.getParameter("cardAmountLimitCheck");
                                    String amountLimitCheck = req.getParameter("amountLimitCheck");
                                    String processor_partnerid = req.getParameter("processor_partnerid");


                                    actionExecutorId = (String) session.getAttribute("merchantid");
                                    String username = (String) session.getAttribute("username");
                                    String role = "Admin";
                                    String actionExecutorName = role + "-" + username;


                                    terminalVO.setMemberId(memberid);
                                    terminalVO.setAccountId(accountid);
                                    terminalVO.setDaily_card_limit(daily_card_limit);
                                    terminalVO.setWeekly_card_limit(weekly_card_limit);
                                    terminalVO.setMonthly_card_limit(monthly_card_limit);
                                    terminalVO.setDaily_amount_limit(daily_amount_limit);
                                    terminalVO.setWeekly_amount_limit(weekly_amount_limit);
                                    terminalVO.setMonthly_amount_limit(monthly_amount_limit);
                                    terminalVO.setDaily_card_amount_limit(daily_card_amount_limit);
                                    terminalVO.setWeekly_card_amount_limit(weekly_card_amount_limit);
                                    terminalVO.setMonthly_card_amount_limit(monthly_card_amount_limit);
                                    terminalVO.setDaily_avg_ticket(daily_avg_ticket);
                                    terminalVO.setWeekly_avg_ticket(weekly_avg_ticket);
                                    terminalVO.setMonthly_avg_ticket(monthly_avg_ticket);
                                    terminalVO.setMin_trans_amount(min_trans_amount);
                                    terminalVO.setMax_trans_amount(max_trans_amount);
                                    terminalVO.setIsActive(isActive);
                                    terminalVO.setPriority(priority);
                                    terminalVO.setIsTest(isTest);
                                    terminalVO.setIsRecurring(isRecurring);
                                    terminalVO.setIsRestrictedTicketActive(isRestrictedTicketActive);
                                    terminalVO.setIsTokenizationActive(isTokenizationActive);
                                    terminalVO.setIsManualRecurring(isManualRecurring);
                                    terminalVO.setAddressDetails(addressDetails);
                                    terminalVO.setDaily_amount_limit_check(daily_amount_limit_check);
                                    terminalVO.setWeekly_amount_limit_check(weekly_amount_limit_check);
                                    terminalVO.setMonthly_amount_limit_check(monthly_amount_limit_check);
                                    terminalVO.setDaily_card_limit_check(daily_card_limit_check);
                                    terminalVO.setWeekly_card_limit_check(weekly_card_limit_check);
                                    terminalVO.setMonthly_card_limit_check(monthly_card_limit_check);
                                    terminalVO.setDaily_card_amount_limit_check(daily_card_amount_limit_check);
                                    terminalVO.setWeekly_card_amount_limit_check(weekly_card_amount_limit_check);
                                    terminalVO.setMonthly_card_amount_limit_check(monthly_card_amount_limit_check);
                                    terminalVO.setAddressValidation(addressValidation);
                                    terminalVO.setCardDetailRequired(cardDetailRequired);
                                    terminalVO.setIsCardEncryptionEnable(isCardEncryptionEnable);
                                    terminalVO.setRiskRuleActivation(riskRuleActivation);
                                    terminalVO.setIsPSTTerminal(isPSTTerminal);
                                    terminalVO.setPaymodeId(paymodeid);
                                    terminalVO.setCardTypeId(cardtypeid);
                                    terminalVO.setSettlementCurrency(settlementCurrency);
                                    terminalVO.setMinPayoutAmount(minPayoutAmount);
                                    terminalVO.setPayoutActivation(payoutActivation);
                                    terminalVO.setAutoRedirectRequest(autoRedirectRequest);
                                    terminalVO.setIsCardWhitelisted(isCardWhitelisted);
                                    terminalVO.setIsEmailWhitelisted(isEmailWhitelisted);
                                    terminalVO.setCurrencyConversion(currencyConversion);
                                    terminalVO.setConversionCurrency(conversionCurrency);
                                    terminalVO.setIsbin_routing(bin_routing);
                                    terminalVO.setIsEmi_support(emi_support);
                                    terminalVO.setWhitelisting(whitelisting);
                                    terminalVO.setCardLimitCheckTerminalLevel(cardLimitCheck);
                                    terminalVO.setCardAmountLimitCheckTerminalLevel(cardAmountLimitCheck);
                                    terminalVO.setAmountLimitCheckTerminalLevel(amountLimitCheck);
                                    terminalVO.setActionExecutorId(actionExecutorId);
                                    terminalVO.setActionExecutorName(actionExecutorName);
                                    terminalVO.setProcessor_partnerid(processor_partnerid);
                                    terminalVO.setPayout_priority(payout_priority);
                                    boolean flag = true;
                                    if (cardtypeid.equals(String.valueOf(CardTypeEnum.MASTER_CARD_CARDTYPE.getValue())))
                                    {
                                        flag = terminalManager.isMasterCardSupported(accountid);
                                    }
                                    if (flag == true)
                                    {
                                        success = terminalManager.masterCardTerminalConfiguration(terminalVO);
                                    }
                                    else
                                    {
                                        error = error + "New Mastercard Terminal could not be added.";
                                    }
                                }
                                else
                                {
                                    success = "Same Terminal Configuration Already Available For " + accountid;
                                }
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    logger.error("Exception while adding new terminal", e);
                    error = error + "Could Not Added New Terminal.";
                    req.setAttribute("error", error);
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
                    req.setAttribute("accountids", loadGatewayAccounts());
                    req.setAttribute("paymodeids", terminalManager.loadPaymodeids());
                    req.setAttribute("cardtypeids", terminalManager.loadcardtypeids());
                    req.setAttribute("error1", "Select at least one transaction.<BR>");
                    RequestDispatcher rd = req.getRequestDispatcher("/membermappingpreference.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                }
            }
            else
            {
                req.setAttribute("accountids", loadGatewayAccounts());
                req.setAttribute("paymodeids", terminalManager.loadPaymodeids());
                req.setAttribute("cardtypeids", terminalManager.loadcardtypeids());
                req.setAttribute("error1", "Select at least one transaction.<BR>");
                RequestDispatcher rd = req.getRequestDispatcher("/membermappingpreference.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
            }

            if (terminal_mainAll != null)
            {
                for (int k = 0; k < terminal_mainAll.length; k++)
                {
                    String terminal_main = terminal_mainAll[k];
                    String action = "";

                    if (!ESAPI.validator().isValidInput("action ", req.getParameter("action_" + terminal_main), "SafeString", 100, false))
                    {
                        logger.debug("Select valid action");
                        error += "Invalid Action Selected";
                    }
                    else
                        action = req.getParameter("action_" + terminal_main);
                    if (action.equalsIgnoreCase("UPDATE_" + terminal_main))
                    {

                        String memberid = req.getParameter("memberid_" + terminal_main);
                        String accountid = req.getParameter("accountid_" + terminal_main);
                        String terminalId = req.getParameter("terminalid_" + terminal_main);

                        String daily_card_limit = req.getParameter("daily_card_limit_" + terminal_main);
                        String weekly_card_limit = req.getParameter("weekly_card_limit_" + terminal_main);
                        String monthly_card_limit = req.getParameter("monthly_card_limit_" + terminal_main);

                        String daily_amount_limit = req.getParameter("daily_amount_limit_" + terminal_main);
                        String weekly_amount_limit = req.getParameter("weekly_amount_limit_" + terminal_main);
                        String monthly_amount_limit = req.getParameter("monthly_amount_limit_" + terminal_main);

                        String daily_card_amount_limit = req.getParameter("daily_card_amount_limit_" + terminal_main);
                        String weekly_card_amount_limit = req.getParameter("weekly_card_amount_limit_" + terminal_main);
                        String monthly_card_amount_limit = req.getParameter("monthly_card_amount_limit_" + terminal_main);

                        String daily_avg_ticket = req.getParameter("daily_avg_ticket_" + terminal_main);
                        String weekly_avg_ticket = req.getParameter("weekly_avg_ticket_" + terminal_main);
                        String monthly_avg_ticket = req.getParameter("monthly_avg_ticket_" + terminal_main);

                        String min_trans_amount = req.getParameter("min_trans_amount_" + terminal_main);
                        String max_trans_amount = req.getParameter("max_trans_amount_" + terminal_main);
                        String isActive = req.getParameter("isActive_" + terminal_main);

                        String priority = req.getParameter("priority_" + terminal_main);
                        String payout_priority = req.getParameter("payout_priority_" + terminal_main);
                        String isTest = req.getParameter("isTest_" + terminal_main);
                        String isPSTTerminal = req.getParameter("isPSTTerminal_" + terminal_main);

                        String isRecurring = req.getParameter("is_recurring_" + terminal_main);
                        String isRestrictedTicketActive = req.getParameter("isRestrictedTicketActive_" + terminal_main);
                        String isTokenizationActive = req.getParameter("isTokenizationActive_" + terminal_main);
                        String isManualRecurring = req.getParameter("isManualRecurring_" + terminal_main);
                        String addressDetails = req.getParameter("addressDetails_" + terminal_main);
                        String daily_amount_limit_check = req.getParameter("daily_amount_limit_check_" + terminal_main);
                        String weekly_amount_limit_check = req.getParameter("weekly_amount_limit_check_" + terminal_main);
                        String monthly_amount_limit_check = req.getParameter("monthly_amount_limit_check_" + terminal_main);
                        String daily_card_limit_check = req.getParameter("daily_card_limit_check_" + terminal_main);
                        String weekly_card_limit_check = req.getParameter("weekly_card_limit_check_" + terminal_main);
                        String monthly_card_limit_check = req.getParameter("monthly_card_limit_check_" + terminal_main);
                        String daily_card_amount_limit_check = req.getParameter("daily_card_amount_limit_check_" + terminal_main);
                        String weekly_card_amount_limit_check = req.getParameter("weekly_card_amount_limit_check_" + terminal_main);
                        String monthly_card_amount_limit_check = req.getParameter("monthly_card_amount_limit_check_" + terminal_main);
                        String addressValidation = req.getParameter("addressValidation_" + terminal_main);
                        String cardDetailRequired = req.getParameter("cardDetailRequired_" + terminal_main);
                        String isCardEncryptionEnable = req.getParameter("isCardEncryptionEnable_" + terminal_main);
                        String riskRuleActivation = req.getParameter("riskruleactivation_" + terminal_main);
                        String settlementCurrency = req.getParameter("settlementcurrency_" + terminal_main);
                        String minPayoutAmount = req.getParameter("min_payout_amount_" + terminal_main);
                        String payoutActivation = req.getParameter("payoutactivation_" + terminal_main);
                        String autoRedirectRequest = req.getParameter("autoRedirectRequest_" + terminal_main);
                        String isCardWhitelisted = req.getParameter("isCardWhitelisted_" + terminal_main);
                        String isEmailWhitelisted = req.getParameter("isEmailWhitelisted_" + terminal_main);
                        String currencyConversion = req.getParameter("currency_conversion_" + terminal_main);
                        String conversionCurrency = req.getParameter("conversion_currency_" + terminal_main);
                        String bin_routing = req.getParameter("bin_routing_" + terminal_main);
                        String emi_support = req.getParameter("emi_support_" + terminal_main);
                        String whitelisting = req.getParameter("whitelistdetails_" + terminal_main);
                        String cardLimitCheck = req.getParameter("cardLimitCheck_" + terminal_main);
                        String cardAmountLimitCheck = req.getParameter("cardAmountLimitCheck_" + terminal_main);
                        String amountLimitCheck = req.getParameter("amountLimitCheck_" + terminal_main);
                        String processor_partnerid = req.getParameter("processor_partnerid_" + terminal_main);
                        String onchangedValues = req.getParameter("onchangedvalue_" + terminal_main);


                    /*error = validateMandatoryParameters(req, action);
                    error = validateOptionalParameters(req, action);*/

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

                        if (!ESAPI.validator().isValidInput("priority", req.getParameter("priority_" + terminal_main), "Numbers", 3, false))
                            error = error + "Invalid Priority for " + terminal_main;

                        if (!ESAPI.validator().isValidInput("isTest", req.getParameter("isTest_" + terminal_main), "SafeString", 1, false))
                            error = error + "Invalid Is Test for " + terminal_main;

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

                        //inputFieldsListOptional.add(InputFields.ADDRESS_DETAILS);
                        //.add(InputFields.ADDRESS_VALIDATION);
                        //inputFieldsListOptional.add(InputFields.CARD_DETAIL_REQUIRED);

                        if (error.equals(""))
                        {
                            terminalVO.setMemberId(memberid);
                            terminalVO.setAccountId(accountid);
                            terminalVO.setTerminalId(terminalId);
                            terminalVO.setDaily_card_limit(daily_card_limit);
                            terminalVO.setWeekly_card_limit(weekly_card_limit);
                            terminalVO.setMonthly_card_limit(monthly_card_limit);
                            terminalVO.setDaily_amount_limit(daily_amount_limit);
                            terminalVO.setWeekly_amount_limit(weekly_amount_limit);
                            terminalVO.setMonthly_amount_limit(monthly_amount_limit);
                            terminalVO.setDaily_card_amount_limit(daily_card_amount_limit);
                            terminalVO.setWeekly_card_amount_limit(weekly_card_amount_limit);
                            terminalVO.setMonthly_card_amount_limit(monthly_card_amount_limit);
                            terminalVO.setDaily_avg_ticket(daily_avg_ticket);
                            terminalVO.setWeekly_avg_ticket(weekly_avg_ticket);
                            terminalVO.setMonthly_avg_ticket(monthly_avg_ticket);
                            terminalVO.setMin_trans_amount(min_trans_amount);
                            terminalVO.setMax_trans_amount(max_trans_amount);
                            terminalVO.setIsActive(isActive);
                            terminalVO.setPriority(priority);
                            terminalVO.setIsTest(isTest);
                            terminalVO.setIsRecurring(isRecurring);
                            terminalVO.setIsRestrictedTicketActive(isRestrictedTicketActive);
                            terminalVO.setIsTokenizationActive(isTokenizationActive);
                            terminalVO.setIsManualRecurring(isManualRecurring);
                            terminalVO.setAddressDetails(addressDetails);
                            terminalVO.setDaily_amount_limit_check(daily_amount_limit_check);
                            terminalVO.setWeekly_amount_limit_check(weekly_amount_limit_check);
                            terminalVO.setMonthly_amount_limit_check(monthly_amount_limit_check);
                            terminalVO.setDaily_card_limit_check(daily_card_limit_check);
                            terminalVO.setWeekly_card_limit_check(weekly_card_limit_check);
                            terminalVO.setMonthly_card_limit_check(monthly_card_limit_check);
                            terminalVO.setDaily_card_amount_limit_check(daily_card_amount_limit_check);
                            terminalVO.setWeekly_card_amount_limit_check(weekly_card_amount_limit_check);
                            terminalVO.setMonthly_card_amount_limit_check(monthly_card_amount_limit_check);
                            terminalVO.setAddressValidation(addressValidation);
                            terminalVO.setCardDetailRequired(cardDetailRequired);
                            terminalVO.setIsCardEncryptionEnable(isCardEncryptionEnable);
                            terminalVO.setRiskRuleActivation(riskRuleActivation);
                            terminalVO.setIsPSTTerminal(isPSTTerminal);
                            terminalVO.setSettlementCurrency(settlementCurrency);
                            terminalVO.setMinPayoutAmount(minPayoutAmount);
                            terminalVO.setPayoutActivation(payoutActivation);
                            terminalVO.setAutoRedirectRequest(autoRedirectRequest);
                            terminalVO.setIsCardWhitelisted(isCardWhitelisted);
                            terminalVO.setIsEmailWhitelisted(isEmailWhitelisted);
                            terminalVO.setCurrencyConversion(currencyConversion);
                            terminalVO.setConversionCurrency(conversionCurrency);
                            terminalVO.setIsbin_routing(bin_routing);
                            terminalVO.setIsEmi_support(emi_support);
                            terminalVO.setWhitelisting(whitelisting);
                            terminalVO.setCardLimitCheckTerminalLevel(cardLimitCheck);
                            terminalVO.setCardAmountLimitCheckTerminalLevel(cardAmountLimitCheck);
                            terminalVO.setAmountLimitCheckTerminalLevel(amountLimitCheck);
                            terminalVO.setProcessor_partnerid(processor_partnerid);
                            terminalVO.setPayout_priority(payout_priority);

                            logger.debug("Creating Activity for edit Gatway account");
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
                                activityTrackerVOs.setModule_name(ActivityLogParameters.COMMERCIALS_LIMITS.toString());
                                activityTrackerVOs.setLable_values(onchangedValues);
                                activityTrackerVOs.setDescription(ActivityLogParameters.MEMBERID.toString() + "-" + memberid+" "+ActivityLogParameters.TERMINALID.toString() + "-" + terminal_main);
                                activityTrackerVOs.setIp(remoteAddr);
                                activityTrackerVOs.setHeader(header);
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


                            try
                            {
                                success = terminalManager.updateTerminalConfiguration(terminalVO);

                            }
                            catch (SystemError e)
                            {

                                logger.error("SystemError while updating terminal configuration", e);
                                error = error + "Could Not Update Terminal Configuration for " + terminalId;
                                req.setAttribute("error", error);
                            }
                            catch (SQLException e)
                            {
                                logger.error("SQLException while updating terminal configuration", e);
                                error = error + "Could Not Update Terminal Configuration for " + terminalId;
                                req.setAttribute("error", error);
                            }
                        }
                    }
                    if (action.equalsIgnoreCase("DELETE_" + terminal_main))
                    {
                        try
                        {
                            if (!ESAPI.validator().isValidInput("memberid", req.getParameter("memberid_" + terminal_main), "OnlyNumber", 10, false))
                                error = error + "Invalid Merchant Id for " + terminal_main;

                            if (!ESAPI.validator().isValidInput("accountid", req.getParameter("accountid_" + terminal_main), "Numbers", 10, false))
                                error = error + "Invalid AccountID for " + terminal_main;

                            if (!ESAPI.validator().isValidInput("terminalid", req.getParameter("terminalid_" + terminal_main), "Numbers", 6, false))
                                error = error + "Invalid Terminal ID for " + terminal_main;

                            if (error.equals(""))
                            {
                                String memberid = req.getParameter("memberid_" + terminal_main);
                                String accountid = req.getParameter("accountid_" + terminal_main);
                                String terminalid = req.getParameter("terminalid_" + terminal_main);

                                terminalVO.setMemberId(memberid);
                                terminalVO.setAccountId(accountid);
                                terminalVO.setTerminalId(terminalid);

                                success = success + terminalManager.deleteTerminalConfiguration(terminalVO);

                            }
                        }
                        catch (Exception e)
                        {
                            logger.error("An error has occured while removing terminal configuration", e);
                        }
                        finally
                        {
                            Database.closeConnection(conn);
                        }
                    }
                }
            }
        }


        req.setAttribute("accountids", loadGatewayAccounts());
        req.setAttribute("paymodeids", terminalManager.loadPaymodeids());
        req.setAttribute("cardtypeids", terminalManager.loadcardtypeids());
        req.setAttribute("success1", success);
        req.setAttribute("error1", error);
        RequestDispatcher rd = req.getRequestDispatcher("/membermappingpreference.jsp?ctoken=" + user.getCSRFToken());
        rd.forward(req, res);
    }

    Hashtable loadGatewayAccounts()
    {
        return GatewayAccountService.getMerchantDetails();
    }

    private String validateMandatoryParameters(HttpServletRequest req, String action)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

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
            inputFieldsListOptional.add(InputFields.IS_TEST);
            inputFieldsListOptional.add(InputFields.ADDRESS_DETAILS);
            inputFieldsListOptional.add(InputFields.ADDRESS_VALIDATION);
            inputFieldsListOptional.add(InputFields.MEMBERID);
            inputFieldsListOptional.add(InputFields.ACCOUNTID_SMALL);
            inputFieldsListOptional.add(InputFields.TERMINALID);
            inputFieldsListOptional.add(InputFields.CARD_DETAIL_REQUIRED);
            inputFieldsListOptional.add(InputFields.DAILY_AVG_TICKET);
            inputFieldsListOptional.add(InputFields.WEEKLY_AVG_TICKET);
            inputFieldsListOptional.add(InputFields.MONTHLY_AVG_TICKET);
            inputFieldsListOptional.add(InputFields.MIN_PAYOUT_AMOUNT);
            inputFieldsListOptional.add(InputFields.PROCESSOR_PARTNERID);
            inputFieldsListOptional.add(InputFields.DAILY_AMOUNT_LIMIT_CHECK);
            inputFieldsListOptional.add(InputFields.WEEKLY_AMOUNT_LIMIT_CHECK);
            inputFieldsListOptional.add(InputFields.MONTHLY_AMOUNT_LIMIT_CHECK);
            inputFieldsListOptional.add(InputFields.DAILY_CARD_LIMIT_CHECK);
            inputFieldsListOptional.add(InputFields.WEEKLY_CARD_LIMIT_CHECK);
            inputFieldsListOptional.add(InputFields.MONTHLY_CARD_LIMIT_CHECK);
            inputFieldsListOptional.add(InputFields.DAILY_CARD_AMOUNT_LIMIT_CHECK);
            inputFieldsListOptional.add(InputFields.WEEKLY_CARD_AMOUNT_LIMIT_CHECK);
            inputFieldsListOptional.add(InputFields.MONTHLY_CARD_AMOUNT_LIMIT_CHECK);

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
            inputFieldsListOptional.add(InputFields.PROCESSOR_PARTNERID);
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
            inputFieldsListOptional.add(InputFields.IS_TEST);
            inputFieldsListOptional.add(InputFields.ADDRESS_DETAILS);
            inputFieldsListOptional.add(InputFields.ADDRESS_VALIDATION);
            inputFieldsListOptional.add(InputFields.MEMBERID);
            inputFieldsListOptional.add(InputFields.ACCOUNTID_SMALL);
            inputFieldsListOptional.add(InputFields.DAILY_AVG_TICKET);
            inputFieldsListOptional.add(InputFields.WEEKLY_AVG_TICKET);
            inputFieldsListOptional.add(InputFields.MONTHLY_AVG_TICKET);
            inputFieldsListOptional.add(InputFields.MIN_PAYOUT_AMOUNT);
            inputFieldsListOptional.add(InputFields.PROCESSOR_PARTNERID);
            inputFieldsListOptional.add(InputFields.DAILY_AMOUNT_LIMIT_CHECK);
            inputFieldsListOptional.add(InputFields.WEEKLY_AMOUNT_LIMIT_CHECK);
            inputFieldsListOptional.add(InputFields.MONTHLY_AMOUNT_LIMIT_CHECK);
            inputFieldsListOptional.add(InputFields.DAILY_CARD_LIMIT_CHECK);
            inputFieldsListOptional.add(InputFields.WEEKLY_CARD_LIMIT_CHECK);
            inputFieldsListOptional.add(InputFields.MONTHLY_CARD_LIMIT_CHECK);
            inputFieldsListOptional.add(InputFields.DAILY_CARD_AMOUNT_LIMIT_CHECK);
            inputFieldsListOptional.add(InputFields.WEEKLY_CARD_AMOUNT_LIMIT_CHECK);
            inputFieldsListOptional.add(InputFields.MONTHLY_CARD_AMOUNT_LIMIT_CHECK);
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
                    error = error + errorList.getError(inputFields.toString()).getMessage() + EOL;
                }
            }
        }
        return error;
    }

    private String validateOptionalParameters(HttpServletRequest req, String action)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        if (action.equalsIgnoreCase("UPDATE"))
        {
            inputFieldsListOptional.add(InputFields.PROCESSOR_PARTNERID);
        }
        else if (action.equalsIgnoreCase("UPDATE2"))
        {
            inputFieldsListOptional.add(InputFields.PROCESSOR_PARTNERID);
        }
        else if (action.equalsIgnoreCase("action_create"))
        {
            inputFieldsListOptional.add(InputFields.PROCESSOR_PARTNERID);
        }

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req, inputFieldsListOptional, errorList, true);

        if (!errorList.isEmpty())
        {
            for (InputFields inputFields : inputFieldsListOptional)
            {
                if (errorList.getError(inputFields.toString()) != null)
                {
                    logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error + errorList.getError(inputFields.toString()).getMessage() + EOL;
                }
            }
        }
        return error;
    }
}