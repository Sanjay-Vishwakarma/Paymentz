package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.ChargeManager;
import com.manager.TerminalManager;
import com.manager.dao.ChargesDAO;
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
import java.util.List;

/**
 * Created by SurajT. on 11/4/2017.
 */
public class ManageMemberAccountCharges extends HttpServlet
{
    private static Logger logger = new Logger(PartnerMerchantSignUp.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        PartnerFunctions partner = new PartnerFunctions();
        Functions functions = new Functions();
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }
        TerminalManager terminalManager = new TerminalManager();
        StringBuilder validationBuffer = new StringBuilder();
        StringBuilder success1 = new StringBuilder();
        StringBuilder sbError = new StringBuilder();
        RequestDispatcher rd = request.getRequestDispatcher("/manageMemberAccountCharges.jsp?ctoken=" + user.getCSRFToken());
        String result = "";
        CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();

        try
        {
            String chargeIds = request.getParameter("chargeids");
            String chargeIdsArr[] = chargeIds.split("\\|");

            String memberId = request.getParameter("memberid");
            String pid =request.getParameter("memberid");
            String terminalid = request.getParameter("terminalid");
            String role= (String) session.getAttribute("role");
            String username= (String) session.getAttribute("username");
            int partnerId= (int) session.getAttribute("partnerId");
            String fromTerminal=request.getParameter("fromTerminal");
            String toTerminal=request.getParameter("toTerminal");
            String checked=request.getParameter("copyCharge");
            StringBuffer vb=new StringBuffer();
            String terminalId="";
            boolean isValid=true;
            String EOL = "<BR>";
            if(!functions.isValueNull(terminalid) && functions.isValueNull(toTerminal)){
                terminalId=toTerminal;
            }
            else {
                terminalId=terminalid;
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
                rd = request.getRequestDispatcher("/manageMemberAccountCharges.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
                return;
            }

            for(String id2 : list)
            {
                String id = id2.split("-")[0];
                String startDate = request.getParameter("fromdate_" + id);
                String endDate = request.getParameter("todate_" + id);

                if (functions.isValueNull(request.getParameter("fromdate_" + id)) && functions.isValueNull(request.getParameter("todate_" + id)))
                {
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
            }

            for (String id1 : list)
            {
                String id = id1.split("-")[0];
                String chargeId = request.getParameter("chargeId_" + id);
                String merchantRate = request.getParameter("merchantRate_" + id);
                String agentCommission = request.getParameter("agentCommission_" + id);
                String partnerCommission = request.getParameter("partnerCommission_" + id);
                String startDate = request.getParameter("fromdate_" + id);
                String endDate = request.getParameter("todate_" + id);
                String isInputRequired = request.getParameter("isinputrequired_" + id);
                String sequenceNumber = request.getParameter("sequenceNumber_" + id);

                TerminalVO terminalVO = terminalManager.getTerminalByTerminalId(terminalId);
                String timestampStartDate = commonFunctionUtil.convertDatepickerToTimestamp(startDate, "00:00:00");
                String timestampEndDate = commonFunctionUtil.convertDatepickerToTimestamp(endDate, "23:59:59");

                ChargeManager chargeManager = new ChargeManager();
                ChargeMasterVO chargeMasterVO = chargeManager.getBusinessChargeDetails(chargeId);

                ChargeVO chargeVO = new ChargeVO();

                chargeVO.setMemberid(memberId);
                chargeVO.setAccountId(terminalVO.getAccountId());
                chargeVO.setTerminalid(terminalVO.getTerminalId());
                chargeVO.setPaymode(terminalVO.getPaymodeId());
                chargeVO.setCardType(terminalVO.getCardTypeId());
                chargeVO.setMchargevalue(merchantRate);
                chargeVO.setAchargevalue(agentCommission);
                chargeVO.setPchargevalue(partnerCommission);
                chargeVO.setStartdate(timestampStartDate);
                chargeVO.setEnddate(timestampEndDate);
                chargeVO.setIsInputRequired(isInputRequired);
                chargeVO.setChargeid(chargeId);
                chargeVO.setCategory(chargeMasterVO.getCategory());
                chargeVO.setKeyword(chargeMasterVO.getKeyword());
                chargeVO.setSubKey(chargeMasterVO.getSubKeyword());
                chargeVO.setFrequency(chargeMasterVO.getFrequency());
                chargeVO.setChargetype(chargeMasterVO.getValueType());
                chargeVO.setSequencenum(sequenceNumber);
                chargeVO.setActionExecutorId(String.valueOf(partnerId));
                chargeVO.setActionExecutorName(role+" "+username);

                boolean accountAvailability = terminalManager.isValidTerminal(chargeVO.getMemberid(), terminalVO.getAccountId(), terminalVO.getTerminalId(), terminalVO.getPaymodeId(), terminalVO.getCardTypeId());

                if (accountAvailability)
                {
                    sbError.append("Invalid member terminal configuration");
                    request.setAttribute("sberror", sbError.toString());
                    rd.forward(request, response);
                    return;
                }

                boolean isChargeAppliedOnMerchantAccount = chargeManager.isChargeAppliedOnMerchantAccount(chargeVO);

                if (isChargeAppliedOnMerchantAccount)
                {
                    boolean checkAvailability = chargeManager.checkMemberSequenceNoAvailability(chargeVO);

                    if (checkAvailability)
                    {
                        ChargesDAO chargesDAO = new ChargesDAO();
                        result = chargesDAO.insertMemberAccountCharges(chargeVO);
                        if (result.equals("success"))
                        {
                            request.setAttribute("success1", "New charge mapped successfully on member account.");
                            continue;
                        }
                        else
                        {
                            request.setAttribute("sberror","New charge mapping failed on member account.");
                            continue;
                        }
                    }
                    else
                    {
                        request.setAttribute("sberror", "Sequence Number already used on merchant account." + " " + chargeVO.getSequencenum());
                        request.setAttribute("chargeids1", chargeIds);
                        rd = request.getRequestDispatcher("/manageMemberAccountCharges.jsp?ctoken=" + user.getCSRFToken());
                        rd.forward(request, response);
                        return;
                    }
                }
                else
                {
                    request.setAttribute("sberror", "Charge is already mapped on merchant account.");
                    //request.setAttribute("sberror", "New charge mapped successfully on member account.");
                    continue;
                }
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZDBViolationException:::::" , e);
            String error = "Internal error while adding new charge on member account.";
            request.setAttribute("sberror", error + e.getMessage());
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

        rd = request.getRequestDispatcher("/manageMemberAccountCharges.jsp?ctoken=" + user.getCSRFToken());
        rd.forward(request, response);
        return;
    }

    public void validation(StringBuilder validationBuffer, HttpServletRequest request)
    {

        String chargeIds = request.getParameter("chargeids");
        String chargeIdsArr[] = chargeIds.split("\\|");

        String memberId = request.getParameter("memberid");
        String terminalId = request.getParameter("terminalid");

        String EOL = "<BR>";

        List sequenceList = new ArrayList<>();

        String deleteids=request.getParameter("deletedId");
        String deletedId[]=deleteids.split(",");
        StringBuffer vb = new StringBuffer();
        boolean isValid = true;

        List<String> list=new ArrayList<>();
        for (String chargeid:chargeIdsArr){
            String id2=chargeid.split("-")[0];
            list.add(chargeid);
            for(String dl:deletedId){
                if(id2.equals(dl)){
                    list.remove(chargeid);
                }
            }
        }
        if(list==null || list.size()==0)
        {
            isValid=false;
            vb.append( "Charges Not Found.Please select the at least one charge" );
            validationBuffer.append(vb.toString() + EOL);
        }

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
                isValid = false;
                vb.append("Invalid Member Id."+EOL);
            }
            if (!ESAPI.validator().isValidInput("terminalid", terminalId, "Numbers", 5, false) || "0".equals(request.getParameter("terminalid")))
            {
                isValid = false;
                vb.append("Invalid Terminal Id."+EOL);
            }
            if (!ESAPI.validator().isValidInput("chargeId", chargeId, "Numbers", 50, false))
            {
                isValid = false;
                vb.append("Invalid Charge Name."+EOL);
            }
            if (!ESAPI.validator().isValidInput("merchantRate", merchantRate, "AmountStr", 20, false))
            {
                isValid = false;
                vb.append("Invalid Merchant Rate."+EOL);
            }
            if (!ESAPI.validator().isValidInput("agentCommission", agentCommission, "AmountStr", 20, false))
            {
                isValid = false;
                vb.append("Invalid Agent Commission."+EOL);
            }
            if (!ESAPI.validator().isValidInput("partnerCommission", partnerCommission, "AmountStr", 20, false))
            {
                isValid = false;
                vb.append("Invalid Partner Commission."+EOL);
            }
            if (!ESAPI.validator().isValidInput("startDate", startDate, "fromDate", 20, false))
            {
                isValid = false;
                vb.append("Invalid Start Date."+EOL);
            }
            if (!ESAPI.validator().isValidInput("endDate", endDate, "toDate", 20, false))
            {
                isValid = false;
                vb.append("Invalid End Date."+EOL);
            }
            if (!ESAPI.validator().isValidInput("sequenceNumber", sequenceNumber, "Numbers", 10, false))
            {
                isValid = false;
                vb.append("Invalid Sequence Number."+EOL);
            }
            if(sequenceList.contains(sequenceNumber))
            {
                isValid = false;
                vb.append("Sequence Number already used on merchant account."+EOL);
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
