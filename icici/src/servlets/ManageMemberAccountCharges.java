import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.ChargeManager;
import com.manager.TerminalManager;
import com.manager.dao.ChargesDAO;
import com.manager.dao.PayoutDAO;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.ChargeVO;
import com.manager.vo.TerminalVO;
import com.manager.vo.payoutVOs.ChargeMasterVO;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by Vishal on 4/25/2017.
 */
public class ManageMemberAccountCharges extends HttpServlet
{
    private static Logger logger = new Logger(ManageMemberAccountCharges.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        Functions functions = new Functions();

        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;

        TerminalManager terminalManager = new TerminalManager();
        StringBuilder sbError = new StringBuilder();
        StringBuilder validationBuffer = new StringBuilder();
        RequestDispatcher rd = request.getRequestDispatcher("/manageMemberAccountsCharges.jsp?ctoken=" + user.getCSRFToken());
        String status = "";
        CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();

        try
        {
            String memberId = request.getParameter("memberid");
            String terminalId = request.getParameter("terminalid");
            String checked=request.getParameter("copyCharge");
            String fromMember=request.getParameter("fromMember");
            String toMember=request.getParameter("toMember");
            System.out.println("Checked in Java---"+checked);
            System.out.println("Action:::"+request.getParameter("action"));
            if("getinfo".equalsIgnoreCase(request.getParameter("action")))
            {
                if (!ESAPI.validator().isValidInput("memberid", request.getParameter("toMember"), "Numbers", 10, false))
                {
                    request.setAttribute("sberror", validationBuffer.toString());
                }
                rd.forward(request, response);
                return;
            }
            String chargeIds = request.getParameter("chargeids");
            String chargeIdsArr[] = chargeIds.split("\\|");
            String terminalIds = request.getParameter("toTerminal");
            String terminalIdsArr[] = terminalIds.split("\\|");
            System.out.println("terminalIdsArr:::"+ Arrays.toString(terminalIdsArr));
            List<String> terminalList=new ArrayList<>();
            for(String terminal:terminalIdsArr){
                System.out.println("------------"+terminal);
                terminalList.add(terminal);
            }


            String deleteids=request.getParameter("deletedId");
            String deletedId[]=deleteids.split(",");
            List<String> list=new ArrayList<>();
            for (String chargeid:chargeIdsArr){
                String id=chargeid.split("-")[0];
                list.add(chargeid);
                for(String dl:deletedId){
                    if(id.equals(dl)){
                        list.remove(chargeid);
                    }
                }
            }
            validation(validationBuffer, request);
            if (validationBuffer.length() > 0)
            {
                request.setAttribute("sberror", validationBuffer.toString());
                logger.error("ErrorMSg---"+validationBuffer.toString());
                request.setAttribute("chargeids1", chargeIds);
                if(validationBuffer.toString().equals("Charges Not Found.Please select the at least one charge<BR>")){
                    request.setAttribute("chargeids1", null);
                }
                rd = request.getRequestDispatcher("/manageMemberAccountsCharges.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
                return;
            }

            for(String id2 : list)
            {
                String id = id2.split("-")[0];
                String startDate = request.getParameter("fromdate_" + id);
                String endDate = request.getParameter("todate_" + id);

                String message = commonFunctionUtil.newValidateDate(request.getParameter("fromdate_" + id), request.getParameter("todate_" + id), null, null);
                if (functions.isValueNull(message))
                {
                    request.setAttribute("sberror", message.toString());
                    request.setAttribute("chargeids1", chargeIds);
                    rd = request.getRequestDispatcher("/manageMemberAccountsCharges.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }
            }
            if(functions.isValueNull(terminalId))
            {
                for (String id1 : list)
                {
                    String id = id1.split("-")[0];
                    String chargeId = request.getParameter("chargeId_" + id);
                    String merchantRate = request.getParameter("merchantRate_" + id);
                    String agentCommission = request.getParameter("agentCommission_" + id);
                    String partnerCommission = request.getParameter("partnerCommission_" + id);
                    String sequenceNumber = request.getParameter("sequenceNumber_" + id);
                    String isInputRequired = request.getParameter("isinputrequired_" + id);
                    String startDate = request.getParameter("fromdate_" + id);
                    String endDate = request.getParameter("todate_" + id);
                    String negativebalance = request.getParameter("negativebalance_" + id);
                    if(!functions.isValueNull(negativebalance)){
                        negativebalance="N";
                    }

                    TerminalVO terminalVO = terminalManager.getTerminalByTerminalId(terminalId);
                    String timestampStartDate = commonFunctionUtil.convertDatepickerToTimestamp(startDate, "00:00:00");
                    String timestampEndDate = commonFunctionUtil.convertDatepickerToTimestamp(endDate, "23:59:59");

                    ChargeManager chargeManager = new ChargeManager();
                    ChargeMasterVO chargeMasterVO = chargeManager.getBusinessChargeDetails(chargeId);
                    ChargeVO chargeVO = new ChargeVO();
                    PayoutDAO payoutDAO = new PayoutDAO();
                    if (functions.isValueNull(chargeId))
                    {
                        if (chargeMasterVO != null && (chargeMasterVO.getKeyword().equals("DomesticTotal") || chargeMasterVO.getKeyword().equals("InternationalTotal")))
                        {
                            String country = payoutDAO.getGatewayCountryFromAccountId(terminalVO.getAccountId());
                            if (!functions.isValueNull(country))
                            {
                                request.setAttribute("sberror", "Gateway Country not found OR Please map the country on gateway.");
                                request.setAttribute("chargeids1", chargeIds);
                                rd = request.getRequestDispatcher("/manageMemberAccountsCharges.jsp?ctoken=" + user.getCSRFToken());
                                rd.forward(request, response);
                                return;
                            }
                        }
                    }

                    chargeVO.setMemberid(memberId);
                    chargeVO.setAccountId(terminalVO.getAccountId());
                    chargeVO.setTerminalid(terminalVO.getTerminalId());
                    chargeVO.setPaymode(terminalVO.getPaymodeId());
                    chargeVO.setCardType(terminalVO.getCardTypeId());
                    chargeVO.setChargeid(chargeId);
                    chargeVO.setIsInputRequired(isInputRequired);
                    chargeVO.setMchargevalue(merchantRate);
                    chargeVO.setAchargevalue(agentCommission);
                    chargeVO.setPchargevalue(partnerCommission);
                    chargeVO.setStartdate(timestampStartDate);
                    chargeVO.setEnddate(timestampEndDate);
                    chargeVO.setSequencenum(sequenceNumber);
                    chargeVO.setCategory(chargeMasterVO.getCategory());
                    chargeVO.setKeyword(chargeMasterVO.getKeyword());
                    chargeVO.setSubKey(chargeMasterVO.getSubKeyword());
                    chargeVO.setFrequency(chargeMasterVO.getFrequency());
                    chargeVO.setChargetype(chargeMasterVO.getValueType());
                    chargeVO.setActionExecutorId(actionExecutorId);
                    chargeVO.setActionExecutorName(actionExecutorName);
                    chargeVO.setNegativebalance(negativebalance);
                    boolean accountAvailability = terminalManager.isValidTerminal(chargeVO.getMemberid(), terminalVO.getAccountId(), terminalVO.getTerminalId(), terminalVO.getPaymodeId(), terminalVO.getCardTypeId());
                    if (accountAvailability)
                    {
                        sbError.append("Invalid member terminal configuration");
                        request.setAttribute("sberror", sbError.toString());
                        rd.forward(request, response);
                        return;
                    }

                    boolean isChargeAppliedOnMerchantAccount = chargeManager.isChargeAppliedOnMerchantAccount(chargeVO);
                    logger.error("isChargeAppliedOnMerchantAccount::::" + isChargeAppliedOnMerchantAccount);
                    if (!isChargeAppliedOnMerchantAccount)
                    {
                        chargeManager.deleteChargeDetails(chargeVO);
                    }
                    boolean checkAvailability = chargeManager.checkMemberSequenceNoAvailability(chargeVO);
                    if (checkAvailability)
                    {
                        ChargesDAO chargesDAO = new ChargesDAO();
                        status = chargesDAO.insertMemberAccountCharges(chargeVO);
                        if ("success".equals(status))
                        {
                            request.setAttribute("sberror", "New charge mapped successfully on member account.");
                            continue;
                        }
                        else
                        {
                            request.setAttribute("sberror", "New charge mapping failed on member account.");
                            continue;
                        }
                    }
                    else
                    {
                        request.setAttribute("sberror", "Sequence Number already used on merchant account." + " " + sequenceNumber);
                        request.setAttribute("chargeids1", chargeIds);
                        rd = request.getRequestDispatcher("/manageMemberAccountsCharges.jsp?ctoken=" + user.getCSRFToken());
                        rd.forward(request, response);
                        return;
                    }
                }
            }else if(functions.isValueNull(terminalIds)){
                System.out.println("terminalList::::"+terminalList.size()+"----"+terminalList);
                for(String terminalid:terminalList)
                {
                    System.out.println("terminalid:::"+terminalid);
                    if(functions.isValueNull(terminalid))
                    {
                        for (String id1 : list)
                        {
                            String id = id1.split("-")[0];
                            String chargeId = request.getParameter("chargeId_" + id);
                            String merchantRate = request.getParameter("merchantRate_" + id);
                            String agentCommission = request.getParameter("agentCommission_" + id);
                            String partnerCommission = request.getParameter("partnerCommission_" + id);
                            String sequenceNumber = request.getParameter("sequenceNumber_" + id);
                            String isInputRequired = request.getParameter("isinputrequired_" + id);
                            String startDate = request.getParameter("fromdate_" + id);
                            String endDate = request.getParameter("todate_" + id);
                            String negativebalance = request.getParameter("negativebalance_" + id);
                            if(!functions.isValueNull(negativebalance)){
                                negativebalance="N";
                            }
                            TerminalVO terminalVO = terminalManager.getTerminalByTerminalId(terminalid);
                            String timestampStartDate = commonFunctionUtil.convertDatepickerToTimestamp(startDate, "00:00:00");
                            String timestampEndDate = commonFunctionUtil.convertDatepickerToTimestamp(endDate, "23:59:59");

                            ChargeManager chargeManager = new ChargeManager();
                            ChargeMasterVO chargeMasterVO = chargeManager.getBusinessChargeDetails(chargeId);
                            ChargeVO chargeVO = new ChargeVO();
                            PayoutDAO payoutDAO = new PayoutDAO();
                            if (functions.isValueNull(chargeId))
                            {
                                if (chargeMasterVO != null && (chargeMasterVO.getKeyword().equals("DomesticTotal") || chargeMasterVO.getKeyword().equals("InternationalTotal")))
                                {
                                    String country = payoutDAO.getGatewayCountryFromAccountId(terminalVO.getAccountId());
                                    if (!functions.isValueNull(country))
                                    {
                                        request.setAttribute("sberror", "Gateway Country not found OR Please map the country on gateway.");
                                        request.setAttribute("chargeids1", chargeIds);
                                        rd = request.getRequestDispatcher("/manageMemberAccountsCharges.jsp?ctoken=" + user.getCSRFToken());
                                        rd.forward(request, response);
                                        return;
                                    }
                                }
                            }

                            chargeVO.setMemberid(toMember);
                            chargeVO.setAccountId(terminalVO.getAccountId());
                            chargeVO.setTerminalid(terminalVO.getTerminalId());
                            chargeVO.setPaymode(terminalVO.getPaymodeId());
                            chargeVO.setCardType(terminalVO.getCardTypeId());
                            chargeVO.setChargeid(chargeId);
                            chargeVO.setIsInputRequired(isInputRequired);
                            chargeVO.setMchargevalue(merchantRate);
                            chargeVO.setAchargevalue(agentCommission);
                            chargeVO.setPchargevalue(partnerCommission);
                            chargeVO.setStartdate(timestampStartDate);
                            chargeVO.setEnddate(timestampEndDate);
                            chargeVO.setSequencenum(sequenceNumber);
                            chargeVO.setCategory(chargeMasterVO.getCategory());
                            chargeVO.setKeyword(chargeMasterVO.getKeyword());
                            chargeVO.setSubKey(chargeMasterVO.getSubKeyword());
                            chargeVO.setFrequency(chargeMasterVO.getFrequency());
                            chargeVO.setChargetype(chargeMasterVO.getValueType());
                            chargeVO.setActionExecutorId(actionExecutorId);
                            chargeVO.setActionExecutorName(actionExecutorName);
                            chargeVO.setNegativebalance(negativebalance);
                            boolean accountAvailability = terminalManager.isValidTerminal(chargeVO.getMemberid(), terminalVO.getAccountId(), terminalVO.getTerminalId(), terminalVO.getPaymodeId(), terminalVO.getCardTypeId());
                            if (accountAvailability)
                            {
                                sbError.append("Invalid member terminal configuration");
                                request.setAttribute("sberror", sbError.toString());
                                rd.forward(request, response);
                                return;
                            }

                            boolean isChargeAppliedOnMerchantAccount = chargeManager.isChargeAppliedOnMerchantAccount(chargeVO);
                            logger.error("isChargeAppliedOnMerchantAccount::::" + isChargeAppliedOnMerchantAccount);
                            if (!isChargeAppliedOnMerchantAccount)
                            {
                                chargeManager.deleteChargeDetails(chargeVO);
                            }
                            boolean checkAvailability = chargeManager.checkMemberSequenceNoAvailability(chargeVO);
                            if (checkAvailability)
                            {
                                ChargesDAO chargesDAO = new ChargesDAO();
                                status = chargesDAO.insertMemberAccountCharges(chargeVO);
                                if ("success".equals(status))
                                {
                                    request.setAttribute("sberror", "New charge mapped successfully on member account.");
                                    continue;
                                }
                                else
                                {
                                    request.setAttribute("sberror", "New charge mapping failed on member account.");
                                    continue;
                                }
                            }
                            else
                            {
                                request.setAttribute("sberror", "Sequence Number already used on merchant account." + " " + sequenceNumber);
                                request.setAttribute("chargeids1", chargeIds);
                                rd = request.getRequestDispatcher("/manageMemberAccountsCharges.jsp?ctoken=" + user.getCSRFToken());
                                rd.forward(request, response);
                                return;
                            }
                        }
                    }
                }
            }
        }

        catch (PZDBViolationException e)
        {
            logger.error("PZDBViolationException::::" , e);
            sbError.append("Internal error while adding new charge on member account.");
            request.setAttribute("sberror", sbError.toString());
            rd.forward(request, response);
            return;
        }
        catch (Exception e)
        {
            logger.error("Exception::::" , e);
            sbError.append("Internal error while adding new charge on member account.");
            request.setAttribute("sberror", sbError.toString());
            rd.forward(request, response);
            return;
        }


        rd = request.getRequestDispatcher("/manageMemberAccountsCharges.jsp?ctoken=" + user.getCSRFToken());
        rd.forward(request, response);
        return;
    }

    public void validation(StringBuilder validationBuffer, HttpServletRequest request)
    {
        Functions functions=new Functions();
        String chargeIds = request.getParameter("chargeids");
        String chargeIdsArr[] = chargeIds.split("\\|");
        String deleteids=request.getParameter("deletedId");
        String deletedId[]=deleteids.split(",");

        String memberId = request.getParameter("memberid");
        String terminalId = request.getParameter("terminalid");
        StringBuffer vb=new StringBuffer();
        boolean isValid=true;
        String EOL = "<BR>";

        /*String toTerminal=request.getParameter("toTerminal");
        if(!functions.isValueNull(terminalId) && functions.isValueNull(toTerminal)){
            terminalId=toTerminal;
        }*/
        List<String> list=new ArrayList<>();
        for (String chargeid:chargeIdsArr){
            String id=chargeid.split("-")[0];
            list.add(chargeid);
            for(String dl:deletedId){
                if(id.equals(dl)){
                    list.remove(chargeid);
                }
            }
        }
        if(list==null || list.size()==0){
            isValid=false;
            vb.append( "Charges Not Found.Please select the at least one charge" );
            validationBuffer.append(vb.toString() + EOL);
        }

        List sequenceList = new ArrayList<>();
        for (String id1 : list)
        {
            String id = id1.split("-")[0];
            String chargeName = id1.split("-")[1];
            String chargeId = request.getParameter("chargeId_" + id);
            String merchantRate = request.getParameter("merchantRate_" + id);
            String agentCommission = request.getParameter("agentCommission_" + id);
            String partnerCommission = request.getParameter("partnerCommission_" + id);
            String startDate = request.getParameter("fromdate_" + id);
            String endDate = request.getParameter("todate_" + id);
            String isInputRequired = request.getParameter("isinputrequired_" + id);
            String sequenceNumber = request.getParameter("sequenceNumber_" + id);

            if (!ESAPI.validator().isValidInput("memberid", memberId, "Numbers", 20, false) || "0".equals(request.getParameter("memberid")))
            {
                isValid=false;
                vb.append( "Invalid Member Id." );
            }
            if(functions.isValueNull(request.getParameter("terminalid")))
            {
                if (!ESAPI.validator().isValidInput("terminalid", terminalId, "Numbers", 5, false) || "0".equals(request.getParameter("terminalid")))
                {
                    isValid = false;
                    vb.append("Invalid Terminal Id.");
                }
            }
            if (!ESAPI.validator().isValidInput("chargeId", chargeId, "Numbers", 50, false))
            {
                isValid=false;
                vb.append("Invalid Charge Name." );
            }
            if (!ESAPI.validator().isValidInput("merchantRate", merchantRate, "AmountStr", 20, false))
            {
                isValid=false;
                vb.append( "Invalid Member Value." );
            }
            if (!ESAPI.validator().isValidInput("agentCommission", agentCommission, "AmountStr", 20, false))
            {
                isValid=false;
                vb.append("Invalid Agent Value." );
            }
            if (!ESAPI.validator().isValidInput("partnerCommission", partnerCommission, "AmountStr", 20, false))
            {
                isValid=false;
                vb.append( "Invalid Partner Value." );
            }
            if (!ESAPI.validator().isValidInput("startDate", startDate, "fromDate", 20, false))
            {
                isValid=false;
                vb.append( "Invalid Start Date.");
            }
            if (!ESAPI.validator().isValidInput("endDate", endDate, "toDate", 20, false))
            {
                isValid=false;
                vb.append("Invalid End Date." );
            }
            if (!ESAPI.validator().isValidInput("sequenceNumber", sequenceNumber, "Numbers", 10, false))
            {
                isValid=false;
                vb.append( "Invalid Sequence Number" );
            }
            if(sequenceList.contains(sequenceNumber))
            {
                isValid = false;
                vb.append("Sequence Number already used on merchant account.");
            }
            else
            {
                sequenceList.add(sequenceNumber);
            }
            if (isValid == false)
            {
                validationBuffer.append(chargeName + "-" + vb.toString() + EOL);
            }
        }
    }
}