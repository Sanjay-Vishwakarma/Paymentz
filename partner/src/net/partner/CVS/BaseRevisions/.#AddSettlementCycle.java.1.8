package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.manager.DateManager;
import com.manager.PartnerManager;
import com.manager.SettlementManager;
import com.manager.dao.BankDao;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.BankWireManagerVO;
import com.manager.vo.PartnerDetailsVO;
import com.manager.vo.payoutVOs.SettlementCycleVO;
import com.payment.exceptionHandler.PZDBViolationException;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Sandip on 2/12/2018.
 */
public class AddSettlementCycle extends HttpServlet
{
    private static Logger logger = new Logger(AllocationUser.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doProcess(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doProcess(request, response);
    }

    public void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        PartnerFunctions partner = new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            logger.debug("member is logout ");
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        RequestDispatcher rd = null;
        StringBuffer errorMsg = new StringBuffer();
        String EOL = "<BR>";

        SettlementManager settlementManager = new SettlementManager();
        Functions functions = new Functions();
        PartnerFunctions partnerFunctions=new PartnerFunctions();

        /*read the settlement cycle parameters*/
        String accountId = request.getParameter("accountid");
        String action = request.getParameter("action");

        /* getting info for superpartner*/
        String partnerId=null;
        String roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
        if(roles.contains("superpartner")){
            partnerId=request.getParameter("partnerId");
        }else {
            partnerId = (String) session.getAttribute("merchantid");
        }

        logger.debug("action:" + action);
        logger.debug("accountId:" + accountId);
        logger.debug("partnerId:" + partnerId);

        if ("next".equalsIgnoreCase(action))
        {
            String serverStartDate = "";
            String serverEndDate = "";
            String bankStartDate = "";
            String bankEndDate = "";

            String startDate = request.getParameter("startdate");
            String startTime = request.getParameter("starttime");
            String endDate = request.getParameter("enddate");
            String endTime = request.getParameter("endtime");
            String isTransactionFileAvailable = request.getParameter("istransactionfileavailable");

            logger.debug("startDate:" + startDate);
            logger.debug("startTime:" + startTime);
            logger.debug("endDate:" + endDate);
            logger.debug("endTime:" + endTime);
            logger.debug("partnerId:" + partnerId);
            logger.debug("isTransactionFileAvailable:" + isTransactionFileAvailable);

            /*input validation step*/
            if (!ESAPI.validator().isValidInput("startdate", startDate, "fromDate", 20, false))
            {
                errorMsg.append("Invalid Start Date" + EOL);
            }
            if (!ESAPI.validator().isValidInput("starttime", startTime, "time", 8, false))
            {
                errorMsg.append("Invalid Start Time" + EOL);
            }
            if (!ESAPI.validator().isValidInput("enddate", endDate, "fromDate", 20, false))
            {
                errorMsg.append("Invalid End Date" + EOL);
            }
            if (!ESAPI.validator().isValidInput("endtime", endTime, "time", 8, false))
            {
                errorMsg.append("Invalid End Time" + EOL);
            }
            if (!ESAPI.validator().isValidInput("accountid", accountId, "Numbers", 10, false))
            {
                errorMsg.append("Invalid AccountId" + EOL);
            }
            if (!ESAPI.validator().isValidInput("partnerId", partnerId, "Numbers", 10, false))
            {
                errorMsg.append("Please Provide Partner Id" + EOL);
            }
            if (errorMsg.length() > 0)
            {
                request.setAttribute("sberror", errorMsg.toString());
                rd = request.getRequestDispatcher("/addSettlementCycle.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
                return;
            }

            try
            {

            /*added constraints to check,is there any pending settlement cycle*/
               /* boolean pendingSettlementCycle = settlementManager.checkForPendingSettlementCycle(accountId, partnerId);
                if (pendingSettlementCycle)
                {
                    request.setAttribute("sberror", "Please complete previous pending settlement cycle");
                    rd = request.getRequestDispatcher("/addSettlementCycle.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }*/

            /*convert the input date into database format*/
                CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();
                bankStartDate = commonFunctionUtil.convertDatepickerToTimestamp(request.getParameter("startdate"), startTime);
                bankEndDate = commonFunctionUtil.convertDatepickerToTimestamp(request.getParameter("enddate"), endTime);


            /*added constraints to verify settlement cycle date range*/
                boolean isInvalidSettlementDate = settlementManager.checkSettlementDateRange(accountId, partnerId, bankStartDate, bankEndDate);
                if (isInvalidSettlementDate)
                {
                    request.setAttribute("sberror", "Invalid settlement date,Please adjust settlement date and try");
                    rd = request.getRequestDispatcher("/addSettlementCycle.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }

              /*find out the time diff between bank server and pz server and prepare server start date and end date*/
                GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                GatewayType gatewayType = GatewayTypeService.getGatewayType(gatewayAccount.getPgTypeId());

                String timeDifferenceNormal = gatewayType.getTime_difference_normal();
                String timeAdjustment = gatewayType.getTimeAdjustment();

                logger.debug(gatewayType.getName() + " timeDifferenceNormal:::::::" + timeDifferenceNormal);
                logger.debug(gatewayType.getName() + " timeAdjustment:::::::" + timeAdjustment);

                String adjustmentAction = "";

                if (functions.isValueNull(timeAdjustment))
                {
                    adjustmentAction = timeAdjustment;
                }

                String timeArr[] = timeDifferenceNormal.split(":");

                int hour = Integer.parseInt(timeArr[0]);
                int min = Integer.parseInt(timeArr[1]);
                int sec = Integer.parseInt(timeArr[2]);

                DateManager dateManager = new DateManager();
                serverStartDate = dateManager.modifyDate(adjustmentAction, bankStartDate, hour, min, sec);
                serverEndDate = dateManager.modifyDate(adjustmentAction, bankEndDate, hour, min, sec);

                PartnerManager partnerManager = new PartnerManager();
                PartnerDetailsVO partnerDetailsVO = null;
                String partnerName = "";

                try
                {
                    partnerDetailsVO = partnerManager.getPartnerDetails(partnerId);
                }
                catch (PZDBViolationException pz)
                {
                    logger.error("PZDBViolationException:::::" + pz);
                    request.setAttribute("sberror", "Internal error occured while processing your request");
                    rd = request.getRequestDispatcher("/addSettlementCycle.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }

                if (partnerDetailsVO != null)
                {
                    partnerName = partnerDetailsVO.getCompanyName();
                }
                else
                {
                    request.setAttribute("sberror", "Partner details not founds");
                    rd = request.getRequestDispatcher("/addSettlementCycle.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }

                /*added constraints to check the settlement cycle date range with transaction table*/
                String previousStartDate = "";
                String previousEndDate = "";
                boolean isFirstSettlementCycle = settlementManager.isFirstSettlementCycle(accountId, partnerId);
                if (isFirstSettlementCycle)
                {
                    /*get the first transaction date on account of psp*/
                    previousStartDate = settlementManager.getFirstTransactionDate(accountId, partnerName);
                    previousEndDate = bankStartDate;//start date minus 1 sec

                    /*added constraints to check the pending transactions*/
                    boolean isPendingTransactions = settlementManager.checkForPendingTransactions(accountId, partnerId, previousStartDate, previousEndDate);
                    if (isPendingTransactions)
                    {
                        request.setAttribute("sberror", "Invalid settlement date,there are some previous pending transactions");
                        rd = request.getRequestDispatcher("/addSettlementCycle.jsp?ctoken=" + user.getCSRFToken());
                        rd.forward(request, response);
                        return;
                    }
                }
                else
                {
                    /*
                       Get previous settlement cycle date range end date minus one sec
                       and current settlement cycle start minus on sec date range
                    */
                    SettlementCycleVO recentSettlementCycleVO = settlementManager.getRecentSettlementInfo(accountId, partnerId);
                    previousStartDate = recentSettlementCycleVO.getEndDate();
                    previousEndDate = bankStartDate;//start date minus 1 sec

                   /*added constraints to check the pending transactions*/
                    boolean checkForCoverTransactions = settlementManager.checkForCoverTransactions(accountId, partnerId, previousStartDate, previousEndDate);
                    if (checkForCoverTransactions)
                    {
                        request.setAttribute("sberror", "Invalid settlement date,there are some previous pending transactions");
                        rd = request.getRequestDispatcher("/addSettlementCycle.jsp?ctoken=" + user.getCSRFToken());
                        rd.forward(request, response);
                        return;
                    }
                }

                /*adding settlement cycle entry into database*/
                int settlementCycleId = settlementManager.addSettlementCycleStep1(accountId, bankStartDate, bankEndDate, partnerId, isTransactionFileAvailable);
                if (settlementCycleId > 0)
                {
                    /*create settlement cycle vo and store the input values*/
                    SettlementCycleVO settlementCycleVO = new SettlementCycleVO();
                    settlementCycleVO.setSettlementCycleId(settlementCycleId);
                    settlementCycleVO.setAccountId(accountId);
                    settlementCycleVO.setStartDate(bankStartDate);
                    settlementCycleVO.setStartTime(startTime);
                    settlementCycleVO.setEndDate(bankEndDate);
                    settlementCycleVO.setEndTime(endTime);
                    settlementCycleVO.setPartnerId(partnerId);
                    settlementCycleVO.setIsTransactionFileAvailable(isTransactionFileAvailable);

                    /*create bank wire manager vo and store the input values*/
                    BankWireManagerVO bankWireManagerVO = new BankWireManagerVO();
                    bankWireManagerVO.setAccountId(accountId);
                    bankWireManagerVO.setBank_start_date(bankStartDate);
                    bankWireManagerVO.setBank_end_date(bankEndDate);
                    bankWireManagerVO.setServer_start_date(serverStartDate);
                    bankWireManagerVO.setServer_end_date(serverEndDate);

                    session.setAttribute("settlementCycleId", String.valueOf(settlementCycleId));
                    session.setAttribute("settlementCycleVO", settlementCycleVO);
                    session.setAttribute("bankWireManagerVO", bankWireManagerVO);

                    rd = request.getRequestDispatcher("/addBankWire.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }
                else
                {
                    logger.debug("Internal error while adding settlement cycle into system");
                    request.setAttribute("sberror", "Internal error while processing your request,please contact to  support team");
                    rd = request.getRequestDispatcher("/addSettlementCycle.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }
            }
            catch (Exception e)
            {
                logger.error("Exception:::::" + e);
                request.setAttribute("sberror", "Internal error while processing your request,please contact to  support team");
                rd = request.getRequestDispatcher("/addSettlementCycle.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
                return;
            }
        }
        else if ("cancel".equalsIgnoreCase(action))
        {
            /*remove the settlementCycleId,bankWireManagerVO,settlementCycleVO from session and database */
            String settlementCycleId = (String) session.getAttribute("settlementCycleId");
            if (functions.isValueNull(settlementCycleId))
            {
                settlementManager.removeSettlementCycle(settlementCycleId);
            }

            session.removeAttribute("settlementCycleId");
            session.removeAttribute("bankWireManagerVO");
            session.removeAttribute("settlementCycleVO");

            request.setAttribute("message", "Your current settlement process has been cancelled");
            rd = request.getRequestDispatcher("/listSettlementCycle.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        else if ("continue".equalsIgnoreCase(action))
        {
            String settlementCycleId = request.getParameter("settlementcycleid");
            errorMsg = new StringBuffer();

            if (!ESAPI.validator().isValidInput("settlementCycleId", settlementCycleId, "Numbers", 10, false))
            {
                errorMsg.append("Invalid Settlement ID" + EOL);
            }
            if (errorMsg.length() > 0)
            {
                request.setAttribute("message", errorMsg.toString());
                rd = request.getRequestDispatcher("/listSettlementCycle.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
                return;
            }

            try
            {
                SettlementCycleVO settlementCycleVO = settlementManager.getSettlementCycleInfo(settlementCycleId, "Initiated");
                if (settlementCycleVO != null)
                {
                    accountId = settlementCycleVO.getAccountId();
                    String bankStartDate = settlementCycleVO.getStartDate();
                    String bankEndDate = settlementCycleVO.getEndDate();

                    GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                    GatewayType gatewayType = GatewayTypeService.getGatewayType(gatewayAccount.getPgTypeId());

                    String timeDifferenceNormal = gatewayType.getTime_difference_normal();
                    String timeAdjustment = gatewayType.getTimeAdjustment();

                    String adjustmentAction = "";

                    if (functions.isValueNull(timeAdjustment))
                    {
                        adjustmentAction = timeAdjustment;
                    }

                    String timeArr[] = timeDifferenceNormal.split(":");

                    int hour = Integer.parseInt(timeArr[0]);
                    int min = Integer.parseInt(timeArr[1]);
                    int sec = Integer.parseInt(timeArr[2]);

                    DateManager dateManager = new DateManager();
                    String serverStartDate = dateManager.modifyDate(adjustmentAction, bankStartDate, hour, min, sec);
                    String serverEndDate = dateManager.modifyDate(adjustmentAction, bankEndDate, hour, min, sec);

                    /*create settlement cycle vo and store the input values*/
                    SettlementCycleVO settlementCycleVONew = new SettlementCycleVO();
                    settlementCycleVONew.setSettlementCycleId(Integer.parseInt(settlementCycleId));
                    settlementCycleVONew.setAccountId(accountId);
                    settlementCycleVONew.setStartDate(bankStartDate);
                    settlementCycleVONew.setEndDate(bankEndDate);
                    settlementCycleVONew.setPartnerId(settlementCycleVO.getPartnerId());
                    settlementCycleVONew.setIsTransactionFileAvailable(settlementCycleVO.getIsTransactionFileAvailable());

                   /*create bank wire manager vo and store the input values*/
                    BankWireManagerVO bankWireManagerVO = new BankWireManagerVO();
                    bankWireManagerVO.setAccountId(accountId);
                    bankWireManagerVO.setServer_start_date(serverStartDate);
                    bankWireManagerVO.setServer_end_date(serverEndDate);
                    bankWireManagerVO.setBank_start_date(bankStartDate);
                    bankWireManagerVO.setBank_end_date(bankEndDate);

                    session.setAttribute("settlementCycleId", String.valueOf(settlementCycleId));
                    session.setAttribute("settlementCycleVO", settlementCycleVO);
                    session.setAttribute("bankWireManagerVO", bankWireManagerVO);

                    rd = request.getRequestDispatcher("/addBankWire.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }
                else
                {
                    request.setAttribute("message", "Settlement cycle not found,please contact to  support team");
                    rd = request.getRequestDispatcher("/listSettlementCycle.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }
            }
            catch (Exception e)
            {
                request.setAttribute("message", "Internal error while processing your request,please contact to  support team");
                rd = request.getRequestDispatcher("/listSettlementCycle.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
                return;
            }

        }
        else if ("getinfo".equalsIgnoreCase(action))
        {
            if (!ESAPI.validator().isValidInput("accountid", accountId, "Numbers", 10, false))
            {
                errorMsg.append("Invalid AccountId" + EOL);
            }
            if (!ESAPI.validator().isValidInput("partnerId", partnerId, "Numbers", 10, false))
            {
                errorMsg.append("Invalid PartnerId" + EOL);
            }
            if (errorMsg.length() > 0)
            {
                request.setAttribute("sberror", errorMsg.toString());
                rd = request.getRequestDispatcher("/addSettlementCycle.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
                return;
            }

            PartnerManager partnerManager = new PartnerManager();
            PartnerDetailsVO partnerDetailsVO = null;
            String partnerName = "";
            try
            {
                partnerDetailsVO = partnerManager.getPartnerDetails(partnerId);
            }
            catch (PZDBViolationException pz)
            {
                logger.error("PZDBViolationException:::::" + pz);
                request.setAttribute("sberror", "Internal error occured while processing your request");
                rd = request.getRequestDispatcher("/addSettlementCycle.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
                return;
            }

            if (partnerDetailsVO != null)
            {
                partnerName = partnerDetailsVO.getCompanyName();
            }
            else
            {
                request.setAttribute("sberror", "Partner details not founds");
                rd = request.getRequestDispatcher("/addSettlementCycle.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
                return;
            }

            //Get previous settlement cycle date range end date minus one sec
            CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();
            BankDao bankDao=new BankDao();
            BankWireManagerVO bankWireManagerVO=bankDao.getPreviousWireDetails(accountId);
            //SettlementCycleVO recentSettlementCycleVO = settlementManager.getRecentSettlementInfo(accountId, partnerId);
            if (bankWireManagerVO != null)
            {
                String previousStartDate = bankWireManagerVO.getBank_end_date();
                SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try
                {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(targetFormat.parse(previousStartDate));
                    cal.add(Calendar.SECOND, 1);
                    previousStartDate = targetFormat.format(cal.getTime());
                }
                catch (ParseException e)
                {
                    logger.error("ParseException:::::" + e);
                    request.setAttribute("sberror", "Internal error while processing your request,please contact with  team");
                    rd = request.getRequestDispatcher("/addSettlementCycle.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }
                request.setAttribute("nextstarttime", previousStartDate.split(" ")[1]);
                request.setAttribute("nextstartdate", commonFunctionUtil.convertTimestampToDatepicker(previousStartDate));
            }
            else
            {
                SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String firstTransactionDate = settlementManager.getFirstTransactionDate(accountId, partnerName);
                if (functions.isValueNull(firstTransactionDate))
                {
                    try
                    {
                        firstTransactionDate = targetFormat.format(targetFormat.parse(firstTransactionDate));
                    }
                    catch (ParseException e)
                    {
                        logger.error("ParseException:::::" + e);
                        request.setAttribute("sberror", "Internal error while processing your request,please contact with  team");
                        rd = request.getRequestDispatcher("/addSettlementCycle.jsp?ctoken=" + user.getCSRFToken());
                        rd.forward(request, response);
                        return;
                    }
                    request.setAttribute("nextstarttime", firstTransactionDate.split(" ")[1]);
                    request.setAttribute("nextstartdate", commonFunctionUtil.convertTimestampToDatepicker(firstTransactionDate));
                }

            }
            rd = request.getRequestDispatcher("/addSettlementCycle.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        else
        {
            request.setAttribute("message", "Internal error while processing your request,please contact to  support team");
            rd = request.getRequestDispatcher("/listSettlementCycle.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
    }
}
