package net.partner;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.core.GatewayAccountService;
import com.enums.BankApplicationStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.manager.*;
import com.manager.enums.ApplicationStatus;
import com.manager.vo.AgentManager;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TerminalVO;
import com.payment.MultipleMemberUtill;
import com.payment.exceptionHandler.PZDBViolationException;
import com.vo.applicationManagerVOs.ApplicationManagerVO;
import com.vo.applicationManagerVOs.BankApplicationMasterVO;
import com.vo.applicationManagerVOs.ConsolidatedApplicationVO;
import org.owasp.esapi.User;

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
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * User: Naushad
 * Date: 8/3/18
 * Time: 7:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetDetails extends HttpServlet
{
    private static MultipleMemberUtill multipleMemberUtill = new MultipleMemberUtill();
    Logger logger=new Logger(GetDetails.class.getName());
    PartnerFunctions partner=new PartnerFunctions();
    GatewayManager gatewayManager = new GatewayManager();
    AgentManager agentManager=new AgentManager();

    TerminalManager terminalManager = new TerminalManager();
    PartnerManager partnerManager = new PartnerManager();
    Functions functions = new Functions();
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException
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

/*
        if (!partner.isLoggedInPartner(session)){
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }
*/
        logger.debug("::::inside GetDetails:::");
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String method = request.getParameter("method").toString();
        String json = "";
        JsonObject jsonresponse = new JsonObject();

        if ("terminalList".equals(method))
        {
            json = getTerminalList(request, response);
        }
        else if ("partneridlist".equals(method))
        {
            json = getPartnerList(request, response);
        }
        else if ("getPartnersList".equals(method))
        {
            json = getPartnersList(request, response);
        }
        else if ("memberid".equals(method))
        {
            json = getMemberList(request, response);
        }
        else if ("memberlistnew".equals(method))
        {
            json = getMemberListNew(request, response);
        }
        else if ("currencyList".equals(method))
        {
            json = perCurrencyList(request, response);
        }
        else if ("riskrulename".equals(method))
        {
            json = riskRuleNameList(request, response);
        }

        else if ("gatewayList".equals(method))
        {
            json = getGatewayList(request, response);
        }

        else if ("accountList".equals(method))
        {
            json = getAccountList(request, response);
        }

        else if ("accountList_new".equals(method))
        {
            json = getAccountList_new(request, response);
        }

        else if ("memberList".equals(method))
        {
            json = getMemberLists(request, response);
        }
        else if ("memberList_new".equals(method))
        {
            json = getMemberLists_new(request, response);
        }
        else if ("issuingbankList".equals(method))
        {
            json = getIssuingBankList(request, response);
        }
        else if ("patnersmember".equals(method))
        {
            jsonresponse = getPartnersMember(request, response);
            response.getWriter().write(jsonresponse.toString());
        }
        else if ("getPartnersAccount".equals(method))
        {
            jsonresponse = getPartnersAccount(request, response);
            response.getWriter().write(jsonresponse.toString());
        }
        else if ("applicationmanager".equals(method))
        {
            jsonresponse = ApplicationManager(request, response);
            response.getWriter().write(jsonresponse.toString());
        }
        else if ("ApplicationManager".equals(method))
        {
            jsonresponse = NewMerchantApplicationManager(request, response);
            response.getWriter().write(jsonresponse.toString());
        }
        else if ("ApplicationManagerPDF".equals(method))
        {
            jsonresponse = ApplicationManagerPDF(request, response);
            response.getWriter().write(jsonresponse.toString());
        }
        else if ("AppManagConsol".equals(method))
        {
            jsonresponse = ApplicationManagerConsolidated(request, response);
            response.getWriter().write(jsonresponse.toString());
        }
        else if ("AppConsolhistory".equals(method))
        {
            jsonresponse = AppConsolhistory(request, response);
            response.getWriter().write(jsonresponse.toString());
        }
        else if ("ManageApplication".equals(method))
        {
            jsonresponse = ManageApplication(request, response);
            response.getWriter().write(jsonresponse.toString());
        }
        else if ("loadGatewayAccounts".equals(method))
        {
            jsonresponse = loadGatewayAccounts(request, response);
            response.getWriter().write(jsonresponse.toString());
        }else if ("loadMerchantsGatewayAccounts".equals(method))
        {
            jsonresponse = loadMerchantsGatewayAccounts(request, response);
            response.getWriter().write(jsonresponse.toString());
        }else if ("getTerminalByMemberId".equals(method))
        {
            jsonresponse = getTerminalByMemberId(request, response);
            response.getWriter().write(jsonresponse.toString());
        }
        else if ("agentList".equals(method))
        {
            json = getAgentNameList(request, response);
        }
        else if ("agentMemberList".equals(method))
        {
            json = getagentMemberList(request, response);
        }
        else if ("agentListPartner".equals(method))
        {
            json = getAgentNameListPartner(request, response);
        }
        else if("paymodeData".equals(method))
        {
            json= getPaymodeList(request,response);
        }
        else if ("cardtypeData".equals(method))
        {
            json= getCardtypeList(request,response);
        }
        response.getWriter().write(json);
    }

    private String getagentMemberList(HttpServletRequest request, HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q = term.toLowerCase();
        String agentid = request.getParameter("agentid");
        String flag = request.getParameter("flag");
        TreeMap<String, String> memberHash = new TreeMap<String, String>();
        if (functions.isValueNull(flag))
        {

            List<MerchantDetailsVO> merchantDetailsVOList=agentManager.getAgentMemberListFromMapped(agentid);
            for (MerchantDetailsVO mdv : merchantDetailsVOList)
            {
                memberHash.put(mdv.getMemberId(), mdv.getMemberId()+"-"+mdv.getCompany_name());
            }
        }
        else
        {
            memberHash = multipleMemberUtill.getAgentMemberDetails(agentid);
        }
        Map<String,String> filteredMemberList = filterMapByTerm(memberHash,q);

        String json = map2Json(filteredMemberList);


        return json;
    }

    public String getTerminalList(HttpServletRequest request, HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q = term.toLowerCase();
        String partnerId = request.getParameter("partnerid").toString();
        String memberId = request.getParameter("memberid").toString();
        String isActive = request.getParameter("isActive").toString();
        TreeMap<String, String> terminladetails = new TreeMap<>();
        List<TerminalVO> terminalVOList = null;
        if ("All".equals(isActive))
        {
            try
            {
                terminalVOList = terminalManager.getAllTerminalsByMemberId(partnerId, memberId);
            }
            catch (PZDBViolationException e)
            {
                logger.debug("PZDBViolationException::::" + e);
            }
        }
        else
        {
            try
            {
                terminalVOList = terminalManager.getTerminalsByMemberIdAndPartner(partnerId, memberId);
            }
            catch (PZDBViolationException e)
            {
                logger.debug("PZDBViolationException::::" + e);
            }
        }

        for (TerminalVO terminalVO1 : terminalVOList)
        {
            String key = terminalVO1.getTerminalId();
            String value = terminalVO1.getTerminalId() + "-" + terminalVO1.getPaymentTypeName() + "-" + terminalVO1.getCardType() + "-" + terminalVO1.getCurrency() + "-" + (terminalVO1.getIsActive().equals("Y") ? "Active" : "InActive");
            terminladetails.put(key, value);
        }


        Map<String, String> filteredMemberList = filterMapByTerm(terminladetails, q);

        String json = map2Json(filteredMemberList);

        return json;

    }


    public String getMemberList(HttpServletRequest request,HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q = term.toLowerCase();
        String partnerId = request.getParameter("partnerid").toString();
        TreeMap<String,String> memberiddetails =null;
        memberiddetails=partner.getPartnerMemberDetailsForUI(partnerId);
        Map<String,String> filteredMemberList = filterMapByTerm(memberiddetails,q);

        String json = map2Json(filteredMemberList);

        return json;

    }


    //MODIFY TO RETRIVE MEMBERS AS PER PARTNER OR SUPER PARTNER.
    public String getMemberListNew(HttpServletRequest request, HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q = term.toLowerCase();
        String partnerId = request.getParameter("partnerid").toString();
        String partnersession = request.getParameter("partnersession").toString();
        TreeMap<String, String> memberidDetails = null;
        boolean status = partner.isPartnerSuperpartnerMapped(partnerId, partnersession);
        if(functions.isValueNull(partnerId))
        {
            if (partnerId.equals(partnersession) || status == true)
            {
                memberidDetails = partner.getPartnerMemberDetailsForUI(partnerId);
            }
        }
        else
        {
            memberidDetails = partner.getSuperPartnerMemberDetailsForUI(partnersession);
        }

        Map<String,String> filteredMemberList = filterMapByTerm(memberidDetails,q);

        String json = map2Json(filteredMemberList);
        return json;

    }

    //TO RETRIVE PARTNER LIST UNDER ONE SUPER PARTNER
    public String getPartnerList(HttpServletRequest request, HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q = term.toLowerCase();
        String partnerId = request.getParameter("partnerid").toString();
        TreeMap<String, String> partneriddetails = null;
        partneriddetails = partner.getPartnerDetailsForUI(partnerId);
        Map<String, String> filteredMemberList = filterMapByTerm(partneriddetails, q);
        String json = map2Json(filteredMemberList);
        return json;

    }

    //TO RETRIVE PARTNER LIST UNDER ONE SUPER PARTNER
    public String getPartnersList(HttpServletRequest request, HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q = term.toLowerCase();
        String partnerId = request.getParameter("partnerid").toString();
        TreeMap<String, String> partneriddetails = null;
        partneriddetails = partner.getPartnerDetailsForMap(partnerId);
        Map<String, String> filteredMemberList = filterMapByTerm(partneriddetails, q);
        String json = map2Json(filteredMemberList);
        return json;

    }

    //TO RETRIEVE MEMBER ONCHANGE OF PARTNER
    public JsonObject getPartnersMember(HttpServletRequest request, HttpServletResponse response)
    {
        String partnerId = request.getParameter("partnerid").toString();
        LinkedHashMap<Integer, String> memberidDetails = null;
        String Roles = partner.getRoleofPartner(partnerId);
        if (Roles.equals("superpartner"))
        {
            memberidDetails = partner.getSuperPartnerMembersDetail(partnerId);
        }
        else
        {
            memberidDetails = partner.getPartnerMembersDetail(partnerId);
        }
        JsonArray data_json = new JsonArray();
        JsonObject json_response = new JsonObject();

        for (Integer mid : memberidDetails.keySet())
        {
            JsonObject json = new JsonObject();
            json.addProperty("value", mid);
            json.addProperty("text", "-" + memberidDetails.get(mid));
            data_json.add(json);
        }
        json_response.add("aaData", data_json);
        return json_response;
    }

    //TO RETRIEVE FRAUD ACCOUNT ID ONCHANGE VALUE OF PARTNER ID.
    public JsonObject getPartnersAccount(HttpServletRequest request, HttpServletResponse response)
    {
        String partnerId = request.getParameter("partnerid").toString();
        LinkedHashMap<Integer, String> accountDetails = new LinkedHashMap();
        Connection conn = null;
        try
        {
            conn= Database.getConnection();
            ResultSet rs = Database.executeQuery("SELECT am.fsaccountid,am.accountname FROM partner_fsaccounts_mapping AS pam JOIN fraudsystem_account_mapping AS am ON am.fsaccountid=pam.fsaccountid where pam.isActive='Y' and  pam.partnerid= '"+partnerId+ "'",conn);
            while (rs.next())
            {

                accountDetails.put(rs.getInt("fsaccountid"), rs.getString("fsaccountid") + " - " + rs.getString("accountname"));
            }
        }
        catch (Exception e)
        {
            logger.error("Exception"+e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        JsonArray data_json = new JsonArray();
        JsonObject json_response = new JsonObject();

        for (Integer accountid : accountDetails.keySet())
        {
            JsonObject json = new JsonObject();
            json.addProperty("value", accountid);
            json.addProperty("text", accountDetails.get(accountid));
            data_json.add(json);
        }
        json_response.add("aaData", data_json);
        return json_response;
    }

    //Retrieving account id as per partner id.
    public JsonObject loadGatewayAccounts(HttpServletRequest request, HttpServletResponse response)
    {
        TreeMap<Integer, String> gatewayaccounts = new TreeMap<Integer, String>();
        String partnerId = request.getParameter("partnerid").toString();
        PartnerManager partnerManager1 = new PartnerManager();
        gatewayaccounts = partnerManager1.loadGatewayAccounts(partnerId ,"");
        JsonArray data_json = new JsonArray();
        JsonObject json_response = new JsonObject();

        for(Integer accID:gatewayaccounts.keySet())
        {
            JsonObject json = new JsonObject();
            json.addProperty("value", accID);
            json.addProperty("text", accID + "-" + gatewayaccounts.get(accID));
            data_json.add(json);
        }
        json_response.add("aaData", data_json);
        return json_response;

    }

    //Retrieving account id as per merchant id.
    public JsonObject loadMerchantsGatewayAccounts(HttpServletRequest request, HttpServletResponse response)
    {
        TreeMap<Integer, String> gatewayaccounts = new TreeMap<Integer, String>();
        String partnerId = request.getParameter("partnerid").toString();
        String memberid = request.getParameter("memberid").toString();
        PartnerManager partnerManager1 = new PartnerManager();
        gatewayaccounts = partnerManager1.loadMerchantsGatewayAccounts(partnerId, memberid);
        JsonArray data_json = new JsonArray();
        JsonObject json_response = new JsonObject();

        for(Integer accID:gatewayaccounts.keySet())
        {
            JsonObject json = new JsonObject();
            json.addProperty("value", accID);
            json.addProperty("text", accID + "-" + gatewayaccounts.get(accID));
            data_json.add(json);
        }
        json_response.add("aaData", data_json);
        return json_response;

    }
    public JsonObject getTerminalByMemberId(HttpServletRequest request, HttpServletResponse response)
    {
        List<TerminalVO> gatewayaccounts = new ArrayList<>();
        String memberid = request.getParameter("memberid").toString();
        TerminalManager transactionManager = new TerminalManager();
        JsonObject json_response = new JsonObject();
        try
        {
            gatewayaccounts = transactionManager.getTerminalsByMerchantId(memberid);
            JsonArray data_json = new JsonArray();
            for(TerminalVO terminalVO:gatewayaccounts)
            {
                JsonObject json = new JsonObject();
                json.addProperty("value", terminalVO.getTerminalId());
                json.addProperty("text", terminalVO.getTerminalId() + "-"+terminalVO.getPaymentTypeName()+"-"+terminalVO.getCardType());
                data_json.add(json);
            }
            json_response.add("aaData", data_json);
            /*for (TerminalVO terminalVO1 : terminalVOList)
            {
                String key = terminalVO1.getTerminalId();
                String value = terminalVO1.getTerminalId() + "-" + terminalVO1.getPaymentTypeName() + "-" + terminalVO1.getCardType() + "-" + terminalVO1.getCurrency() + "-" + (terminalVO1.getIsActive().equals("Y") ? "Active" : "InActive");
                terminladetails.put(key, value);
            }
            Map<String, String> filteredMemberList = filterMapByTerm(terminladetails, q);
            String json = map2Json(filteredMemberList);
            return json;*/
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZDBViolationException---" + e);
        }
        return json_response;
    }

    //TO RETRIEVE MEMBERS FOR APPLICATION MANAGER.
    public JsonObject ApplicationManager(HttpServletRequest request, HttpServletResponse response){

        String partnerId = request.getParameter("partnerid");
        ApplicationManager applicationManager = new ApplicationManager();
        List<ApplicationManagerVO> applicationManagerVOList=applicationManager.getPartnersMerchantApplicationManagerVO(partnerId);
        JsonArray data_json = new JsonArray();
        JsonObject json_response = new JsonObject();

        for(ApplicationManagerVO applicationManagerVO : applicationManagerVOList)
        {
            JsonObject json = new JsonObject();
            json.addProperty("value",(applicationManagerVO.getMemberId().split("_"))[0]);
            json.addProperty("text",(applicationManagerVO.getMemberId().split("_"))[0]);
            data_json.add(json);
        }
        json_response.add("aaData", data_json);
        return json_response;
    }

    //TO RETRIEVE MEMBERS FOR MERCHANT APPLICATION PDF.
    public JsonObject ApplicationManagerPDF(HttpServletRequest request, HttpServletResponse response){

        String partnerId = request.getParameter("partnerid");
        ApplicationManagerVO applicationManagerVOParam = new ApplicationManagerVO();
        ApplicationManager applicationManager = new ApplicationManager();
        List<ApplicationManagerVO> applicationManagerVOList = applicationManager.getPartnerApplicationManagerVO(applicationManagerVOParam, partnerId);
        JsonArray data_json = new JsonArray();
        JsonObject json_response = new JsonObject();


        for(ApplicationManagerVO applicationManagerVO : applicationManagerVOList)
        {
            String memberid = "";
            if (ApplicationStatus.MODIFIED.name().equals(applicationManagerVO.getStatus()) || ApplicationStatus.VERIFIED.name().equals(applicationManagerVO.getStatus()))
            {
                JsonObject json = new JsonObject();
                json.addProperty("value", applicationManagerVO.getMemberId());
                json.addProperty("text", applicationManagerVO.getMemberId());
                data_json.add(json);
            }

        }
        json_response.add("aaData", data_json);
        return json_response;
    }

    //TO RETRIEVE NEW MEMBERS FOR APPLICATION MANAGER.
    public JsonObject NewMerchantApplicationManager(HttpServletRequest request, HttpServletResponse response){

        String partnerId = request.getParameter("partnerid");
        ApplicationManager applicationManager = new ApplicationManager();
        List<ApplicationManagerVO> applicationManagerVOList=applicationManager.getPartnersNewMerchantApplicationManagerVO(partnerId);
        JsonArray data_json = new JsonArray();
        JsonObject json_response = new JsonObject();

        for(ApplicationManagerVO applicationManagerVO : applicationManagerVOList)
        {
            JsonObject json = new JsonObject();
            json.addProperty("value",(applicationManagerVO.getMemberId().split("_"))[0]);
            json.addProperty("text",(applicationManagerVO.getMemberId().split("_"))[0]);
            data_json.add(json);
        }
        json_response.add("aaData", data_json);
        return json_response;
    }

    //TO RETRIEVE MEMBERS FOR APPLICATION MANAGER CONSOLIDATION.
    public JsonObject ApplicationManagerConsolidated(HttpServletRequest request, HttpServletResponse response){

        String partnerId = request.getParameter("partnerid");
        ApplicationManager applicationManager = new ApplicationManager();
        String orderBy= "member_id";
        String groupBy= "member_id";
        BankApplicationMasterVO bankApplicationMasterVOParam = new BankApplicationMasterVO();
        bankApplicationMasterVOParam.setStatus(BankApplicationStatus.VERIFIED.toString());
        Map<String, List<BankApplicationMasterVO>> bankApplicationMasterVOList=applicationManager.getPartnerBankApplicationMasterVOForMemberId(bankApplicationMasterVOParam, orderBy, groupBy, partnerId);
        JsonArray data_json = new JsonArray();
        JsonObject json_response = new JsonObject();

        for(String memberId : bankApplicationMasterVOList.keySet())
        {
            JsonObject json = new JsonObject();
            json.addProperty("value",memberId);
            json.addProperty("text",memberId);
            data_json.add(json);
        }
        json_response.add("aaData", data_json);
        return json_response;
    }

    public JsonObject AppConsolhistory(HttpServletRequest request, HttpServletResponse response){

        String partnerId = request.getParameter("partnerid");
        ApplicationManager applicationManager = new ApplicationManager();
        Map<String,ConsolidatedApplicationVO> consolidatedApplicationVOMapOption=applicationManager.getconsolidated_memberList_history(partnerId);
        JsonArray data_json = new JsonArray();
        JsonObject json_response = new JsonObject();

        for(Map.Entry<String,ConsolidatedApplicationVO> consolidatedApplicationVO : consolidatedApplicationVOMapOption.entrySet())
        {
            JsonObject json = new JsonObject();
            json.addProperty("value",consolidatedApplicationVO.getValue().getMemberid());
            json.addProperty("text",consolidatedApplicationVO.getValue().getMemberid());
            data_json.add(json);
        }
        json_response.add("aaData", data_json);
        return json_response;
    }

    //TO RETRIEVE MEMBERS FOR MANAGE APPLICATION .
    public JsonObject ManageApplication(HttpServletRequest request, HttpServletResponse response){

        String partnerId = request.getParameter("partnerid");
        ApplicationManager applicationManager = new ApplicationManager();
        Map<String,ConsolidatedApplicationVO> consolidatedApplicationVOMapOption=applicationManager.getconsolidated_memberList(partnerId);
        JsonArray data_json = new JsonArray();
        JsonObject json_response = new JsonObject();

        for(Map.Entry<String,ConsolidatedApplicationVO> consolidatedApplicationVO : consolidatedApplicationVOMapOption.entrySet())
        {
            JsonObject json = new JsonObject();
            json.addProperty("value",consolidatedApplicationVO.getValue().getMemberid());
            json.addProperty("text",consolidatedApplicationVO.getValue().getMemberid());
            data_json.add(json);
        }
        json_response.add("aaData", data_json);
        return json_response;
    }

    public String perCurrencyList(HttpServletRequest request, HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q = term.toLowerCase();
        String memberId = request.getParameter("memberid");
        String companyname = request.getParameter("companyname");

        TreeMap<String, String> currencyDetails = new TreeMap<>();
        List<String> currencyList = new ArrayList<>();
        try
        {
            currencyList = partner.perCurrency(memberId, companyname);
        }
        catch (SystemError e)
        {
            logger.debug("SystemError:::::" + e);
        }

        for (String curr : currencyList)

        {
            String key = curr;
            String value = curr;
            currencyDetails.put(key, value);
        }

        Map<String, String> filteredMemberList = filterMapByTerm(currencyDetails, q);

        String json = map2Json(filteredMemberList);

        return json;

    }

    public String riskRuleNameList(HttpServletRequest request, HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q = term.toLowerCase();
        String memberId = request.getParameter("memberid");
        String terminalid = request.getParameter("terminalid");

        TreeMap<String, String> ruleDetails = null;
        List<String> riskrulenames = new ArrayList<>();
        try
        {
            ruleDetails = partner.riskRuleList(memberId, terminalid);
        }
        catch (SystemError e)
        {
            logger.debug("SystemError:::::" + e);
        }

        Map<String, String> filteredMemberList = filterMapByTerm(ruleDetails, q);

        String json = map2Json(filteredMemberList);

        return json;

    }


    public String getGatewayList(HttpServletRequest request, HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q = term.toLowerCase();
        TreeMap<String, String> gatewayTypedetails = new TreeMap<>();
        String partnerid = request.getParameter("partnerid");
        try
        {
            gatewayTypedetails = gatewayManager.getGatewayTypePartner(partnerid);
        }
        catch (Exception e)
        {
            logger.error("Exception---" + e);
        }
        Map<String, String> filteredMemberList = filterMapByTerm(gatewayTypedetails, q);

        String json = map2Json(filteredMemberList);

        return json;

    }

    public String getAccountList(HttpServletRequest request, HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String partnerid = request.getParameter("partnerid");
        String memberid = request.getParameter("memberid");
        String gateway = request.getParameter("gateway");
        String q = term.toLowerCase();
        GatewayAccountService gatewayAccountService = new GatewayAccountService();
        TreeMap<String, String> accountdetails = new TreeMap<>();
        String gatewayid = "";
        if (functions.isValueNull(gateway))
        {
            String gatewayarr[] = gateway.split("-");
            gatewayid = gatewayarr[2];
            accountdetails = gatewayAccountService.getAccountsDetailsPartner(gatewayid, partnerid);
        }
        if (functions.isValueNull(memberid))
        {
            accountdetails = partnerManager.getAccountDetailsMemberid(memberid, partnerid);
        }
        if (functions.isEmptyOrNull(gatewayid) && functions.isValueNull(memberid))
        {
            accountdetails = partnerManager.getAccountDetailsMemberid(memberid, partnerid);
        }
        if (functions.isValueNull(gatewayid) && functions.isEmptyOrNull(memberid))
        {
            accountdetails = gatewayAccountService.getAccountsDetailsPartner(gatewayid, partnerid);
        }
        if (functions.isEmptyOrNull(gatewayid) && functions.isEmptyOrNull(memberid))
        {
            accountdetails = gatewayAccountService.getAccountsDetailsPartner(gatewayid, partnerid);
        }

        Map<String, String> filteredMemberList = filterMapByTerm(accountdetails, q);

        String json = map2Json(filteredMemberList);

        return json;

    }

    public String getAccountList_new(HttpServletRequest request, HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String partnerid = request.getParameter("partnerid");
        String memberid = request.getParameter("memberid");
        String gateway = request.getParameter("gateway");
        String q = term.toLowerCase();
        GatewayAccountService gatewayAccountService = new GatewayAccountService();
        TreeMap<String, String> accountdetails = new TreeMap<>();
        String gatewayid = "";
        if (functions.isValueNull(gateway))
        {
            String gatewayarr[] = gateway.split("-");
            gatewayid = gatewayarr[2];
            accountdetails = gatewayAccountService.getAccountsDetailsPartner(gatewayid, partnerid);
        }
        if (functions.isValueNull(memberid))
        {
            accountdetails = partnerManager.getAccountDetailsMemberid(memberid, partnerid);
        }
        if (functions.isEmptyOrNull(gatewayid) && functions.isValueNull(memberid))
        {
            accountdetails = partnerManager.getAccountDetailsMemberid(memberid, partnerid);
        }
        if (functions.isValueNull(gatewayid) && functions.isEmptyOrNull(memberid))
        {
            accountdetails = gatewayAccountService.getAccountsDetailsPartner(gatewayid, partnerid);
        }
        if (functions.isEmptyOrNull(gatewayid) && functions.isEmptyOrNull(memberid))
        {
            accountdetails = gatewayAccountService.getAccountsDetailsPartner(gatewayid, partnerid);
        }

        Map<String, String> filteredMemberList = filterMapByTerm(accountdetails, q);

        String json = map2Json(filteredMemberList);

        return json;

    }

    public String getMemberLists(HttpServletRequest request, HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String partnerid = request.getParameter("partnerid");
        String accountid = "";
        String q = term.toLowerCase();
        TreeMap<String, String> memberdetails = null;
        String gatewayid = "";
        if (functions.isValueNull(request.getParameter("gateway")))
        {
            String gateway[] = request.getParameter("gateway").split("-");
            gatewayid = gateway[2];
        }
        if (functions.isValueNull(request.getParameter("accountid")))
        {
            accountid = request.getParameter("accountid");
        }
        memberdetails = partnerManager.getAccountsDetailsMemberid(gatewayid, accountid, partnerid);
        Map<String, String> filteredMemberList = filterMapByTerm(memberdetails, q);
        String json = map2Json(filteredMemberList);
        return json;
    }

    public String getMemberLists_new(HttpServletRequest request, HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String partnerid = request.getParameter("partnerid");
        String pid = request.getParameter("pid");
        String accountid = "";
        String q = term.toLowerCase();
        TreeMap<String, String> memberdetails = null;
        String gatewayid = "";
        if (functions.isValueNull(request.getParameter("bank_name")))
        {
            String gateway[] = request.getParameter("bank_name").split("-");
            gatewayid = gateway[2];
        }
        if (functions.isValueNull(request.getParameter("acc_id")))
        {
            accountid = request.getParameter("acc_id");
        }
        if(functions.isValueNull(pid))
        {
            memberdetails = partnerManager.getAccountsDetailsMemberid(gatewayid, accountid, pid);
        }
        else
        {
            memberdetails = partnerManager.getAccountsDetailsMemberid_superpartner(gatewayid, accountid, partnerid);
        }
        Map<String, String> filteredMemberList = filterMapByTerm(memberdetails, q);
        String json = map2Json(filteredMemberList);
        return json;
    }

    public String getIssuingBankList(HttpServletRequest request, HttpServletResponse response)
    {
        logger.debug("inside getGatewayList in merchant::::");
        String term = request.getParameter("term");
        String q = term.toLowerCase();
        TreeMap<String, String> gatewayTypedetails = new TreeMap<>();
        String partnername = request.getParameter("partnername");
        try
        {
            gatewayTypedetails = gatewayManager.getIssuingBankForPartnert(partnername);
        }
        catch (Exception e)
        {
            logger.error("Exception---" + e);
        }
        Map<String, String> filteredMemberList = filterMapByTerm(gatewayTypedetails, q);

        String json = map2Json(filteredMemberList);

        return json;

    }

    public String getAgentNameList(HttpServletRequest request,HttpServletResponse response)
    {
        String partnerid= request.getParameter("partnerid").toString();
        String partnersession= request.getParameter("partnersession").toString();
        String term = request.getParameter("term");
        String q = term.toLowerCase();
        TreeMap<String, String> agentidDetails=null;
        boolean status= partner.isPartnerSuperpartnerMapped(partnerid,partnersession);
        if (functions.isValueNull(partnerid))
        {
            if (partnerid.equals(partnersession) || status== true)
            {
                agentidDetails= partner.getPartnerAgentDetailsForUI(partnerid);
            }
        }
        else
        {
            agentidDetails= partner.getSuperPartnerAgentDetailsForUI(partnersession);
        }

        Map<String,String> filteredAgentList = filterMapByTerm(agentidDetails,q);

        String json = map2Json(filteredAgentList);
        return json;
    }

    public List<String> filterListByTerm(List<String> list, String term) {

        List<String> matching = list.stream()
                .filter(e -> e.toLowerCase().startsWith(term))
                .collect(Collectors.toList());

        return matching;
    }

    public Map<String, String> filterMapByTerm(Map<String, String> map, String term) {

        Map<String, String> matching = map.entrySet().stream()
                .filter(e -> e.getKey().toLowerCase().startsWith(term))
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));

        return matching;
    }

    public String list2Json(List<String> list) {

        String json = null;
        ObjectMapper objectMapper = new ObjectMapper();

        try
        {
            json = objectMapper.writeValueAsString(list);
        }
        catch (JsonProcessingException e)
        {
            logger.error("JsonProcessingException---" + e);
        }
        return json;
    }

    public String map2Json(Map<String, String> map)
    {

        String json = null;
        ObjectMapper objectMapper = new ObjectMapper();

        try
        {
            json = objectMapper.writeValueAsString(map);
        }
        catch (JsonProcessingException e)
        {
            logger.error("JsonProcessingException---" + e);
        }
        return json;
    }


    public TreeMap<String, String> getAgentMemberDetails(String agentid)
    {
        TreeMap dataHash = new TreeMap();
        Connection conn = null;
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer();
            query.append("SELECT mam.mappingid,mam.memberid,mam.agentid,a.agentName AS agentname,m.company_name AS merchantname FROM merchant_agent_mapping AS mam JOIN agents AS a ON mam.agentid=a.agentid JOIN members AS m ON mam.memberid=m.memberid");
            if (functions.isValueNull(agentid))
            {
                query.append(" where mam.agentid='"+agentid+"'");
            }
            query.append(" ORDER BY mam.memberid ASC");
            PreparedStatement p = conn.prepareStatement(query.toString());
            ResultSet resultSet = p.executeQuery();
            while(resultSet.next())
            {
                dataHash.put(String.valueOf(resultSet.getInt("memberid")),String.valueOf(resultSet.getInt("memberid"))+"-"+ resultSet.getString("merchantname"));
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return dataHash;
    }
    public String getAgentNameListPartner(HttpServletRequest request,HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String partnerId = request.getParameter("partnerid").toString();
        String partnersession = request.getParameter("partnersession").toString();

        String q = term.toLowerCase();
        TreeMap<String, String> agentHash = null;
        boolean status = partner.isPartnerSuperpartnerMapped(partnerId, partnersession);
        try
        {
            if (functions.isValueNull(partnerId))
            {
                if (partnerId.equals(partnersession) || status == true)
                {
                    agentHash = partner.getAgentDetailsPartner(partnerId);
                }
            }
            else
            {
                agentHash = partner.getAgentDetailsPartnerSession(partnersession);
            }
        }catch(Exception e)
        {
            logger.error("Exception---" + e);
        }
        Map<String,String> filteredMemberList = filterMapByTerm(agentHash,q);

        String json = map2Json(filteredMemberList);
        return json;
    }
    public String getPaymodeList(HttpServletRequest request, HttpServletResponse response)
    {
        String term= request.getParameter("term");
        String q= term.toLowerCase();

        TreeMap<String,String> paymodedata= multipleMemberUtill.getPaymodeData();
        Map<String,String> filtermapdata= filterMapByTerm(paymodedata,q);
        String json= map2Json(filtermapdata);
        return json;
    }
    public String getCardtypeList(HttpServletRequest request, HttpServletResponse response)
    {
        String term= request.getParameter("term");
        String q= term.toLowerCase();

        TreeMap<String,String> cardtypedata= multipleMemberUtill.getCardTypeDetail();
        Map<String,String> filtermapcarddata= filterMapByTerm(cardtypedata,q);
        String json= map2Json(filtermapcarddata);
        return json;
    }
}
