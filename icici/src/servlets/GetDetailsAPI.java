package servlets;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.manager.BankManager;
import com.manager.ProfileManagementManager;
import com.manager.TerminalManager;
import com.manager.dao.PartnerDAO;
import com.manager.dao.TransactionDAO;
import com.manager.vo.AgentManager;
import com.manager.vo.BankWireManagerVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TerminalVO;
import com.manager.vo.partnerMerchantVOs.TokenVo;
import com.payment.IPEntry;
import com.payment.MultipleMemberUtill;
import com.payment.exceptionHandler.PZDBViolationException;
import org.owasp.esapi.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Ajit on 4/12/2018.
 */


public class GetDetailsAPI extends HttpServlet
{
    private static MultipleMemberUtill multipleMemberUtill = new MultipleMemberUtill();
    private static PartnerDAO partnerDAO = new PartnerDAO();
    private static TerminalManager terminalManager = new TerminalManager();
    Logger logger=new Logger(GetDetailsAPI.class.getName());
    IPEntry ipEntry = new IPEntry();
    Functions functions = new Functions();
    AgentManager agentManager=new AgentManager();
    TransactionDAO transactionDAO = new TransactionDAO();
    ProfileManagementManager profileManagementManager=new ProfileManagementManager();
    JsonObject jsonresponse = new JsonObject();
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        doProcess(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        doProcess(request,response);
    }
    public void doProcess(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        HttpSession session = request.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String method = request.getParameter("method");
        String json="";

        if("memberid".equals(method))
        {
            json = getMemberList(request, response);
        }
        else if ("ActiveMemberid".equals(method))
        {
            json = getActiveMemberList(request, response);
        }
        else if ("terminalList".equals(method))
        {
            json = getTerminalList(request, response);
        }
        else if ("partnerid".equals(method))
        {
            json = getPartnerList(request, response);
        }
        else if ("usernamelist".equals(method))
        {
            try
            {
                json = getUsernameList(request, response);
            }
            catch (PZDBViolationException e)
            {
                logger.error("PZDBViolationException--->",e);
            }
        }
        else if ("partnerid1".equals(method))
        {
            json = getPartnerList1(request, response);
        }
        else if ("allpartnerid".equals(method))
        {
            json = getAllPartnerList(request, response);
        }
        else if ("pmemberList".equals(method))
        {
            json = getPartnerByMemberList(request, response);
        }
        else if ("gatewayList".equals(method))
        {
            json = getGatewayList(request, response);
        }
        else if ("gatewayList1".equals(method))
        {
            json = getGatewayList1(request, response);
        }
        else if ("accountList".equals(method))
        {
            json = getAccountList(request, response);
        }
        else if ("MembersAccountList".equals(method))
        {
            json = getMembersAccountList(request, response);
        }
        else if ("memberList".equals(method))
        {
            json = getMemberdetailList(request, response);
        }
    else if ("terminalDetailList".equals(method))
        {
            json = getMemberdetailListByMemberid(request, response);
        }
        else if ("rulename".equals(method))
        {
            json = getRuleNameList(request, response);
        }
        else if ("agentList".equals(method))
        {
            json = getAgentNameList(request, response);
        }
    else if ("agentAllList".equals(method))
        {
            json = getAllAgentNameList(request, response);
        }
    else if ("agentMemberList".equals(method))
        {
            json = getAgentMemberList(request, response);
        }
    else if ("agentMemberTerminalList".equals(method))
        {
            json = getAgentMemberTerminalList(request, response);
        }
        else if ("cardtypeList".equals(method))
        {
            json = getCardTypeList(request, response);
        }
        else if ("bankList".equals(method))
        {
            json = getIssuingBankList(request, response);
        }
    else if ("memberAccountList".equals(method))
        {
            json = getMemberAccountList(request, response);
        }
        else if ("bankWireList".equals(method))
        {
            json = bankWireList(request, response);
        }
        else if ("partnermember".equals(method))
        {
            json = getPartnerByMemberListPartner(request, response);
        }
        else if ("terminalmemberpartner".equals(method))
        {
            json = getTerminalListMemberPartner(request, response);
        }
        else if("paymodeData".equals(method))
        {
            json = getPaymodeById(request, response);
        }
        else if ("cardtypedetails".equals(method)){
            json = getCardTypeDetails(request,response);
        }
        else if ("bankwireidList".equals(method))
        {
            json= getBankwireIdList(request, response);
        }
        else if ("parent_bankwireIdList".equals(method))
        {
            json = getParent_BankWireIdList(request, response);
        }
        else if ("paymodeList".equals(method))
        {
            jsonresponse = getPaymodeidByGateway(request, response);
            response.getWriter().write(jsonresponse.toString());
        }
        else if ("gatewayname".equals(method))
        {
            json = getGatewayNameList(request,response);
        }

        response.getWriter().write(json);
    }

    public String getMemberList(HttpServletRequest request,HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q = term.toLowerCase();
        TreeMap<String, String> merchantHash = multipleMemberUtill.getMemberid();

        Map<String,String> filteredMemberList = filterMapByTerm(merchantHash,q);

        String json = map2Json(filteredMemberList);

        return json;
    }

    public String getActiveMemberList(HttpServletRequest request,HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q = term.toLowerCase();
        TreeMap<String, String> merchantHash = multipleMemberUtill.getActiveMemberid();

        Map<String,String> filteredMemberList = filterMapByTerm(merchantHash, q);

        String json = map2Json(filteredMemberList);


        return json;
    }

    public String getTerminalList(HttpServletRequest request,HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q = term.toLowerCase();
        String memberid = request.getParameter("memberid");
        TreeMap<String, String> merchantHash = multipleMemberUtill.getTerminalid(memberid);

        Map<String,String> filteredMemberList = filterMapByTerm(merchantHash,q);

        String json = map2Json(filteredMemberList);

        return json;
    }
    public String getTerminalListMemberPartner(HttpServletRequest request,HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q = term.toLowerCase();
        String memberid = request.getParameter("memberid");
        TreeMap<String, String> merchantHash = multipleMemberUtill.getTerminalidMember(memberid);

        Map<String,String> filteredMemberList = filterMapByTerm(merchantHash,q);

        String json = map2Json(filteredMemberList);

        return json;
    }
    public String getPaymodeById(HttpServletRequest request, HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q= term.toLowerCase();

        TreeMap<String, String> paymodemap= multipleMemberUtill.getPaymodeData();
        Map<String, String> filteredPaymodeData = filterMapByTerm(paymodemap, q);

        String json= map2Json(filteredPaymodeData);
        return  json;

    }
    public String getRuleNameList(HttpServletRequest request,HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q = term.toLowerCase();
        String memberid = request.getParameter("memberid");
        String terminalid = request.getParameter("terminalid");
        TreeMap<String, String> merchantHash = multipleMemberUtill.getRuleName(memberid, terminalid);

        Map<String,String> filteredMemberList = filterMapByTerm(merchantHash,q);

        String json = map2Json(filteredMemberList);

        return json;
    }

    public String getPartnerList(HttpServletRequest request,HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q = term.toLowerCase();
        TreeMap<String,String> partnerdetails =new TreeMap<>();
        List<TokenVo> partneriddetails=partnerDAO.getpartnerTokenDetail();

        for(TokenVo p:partneriddetails)
        {
            String key = p.getPartnerid();
            String value =p.getPartnerid()+"-"+ p.getPartnername();
            partnerdetails.put(key,value);
        }

        Map<String,String> filteredMemberList = filterMapByTerm(partnerdetails,q);

        String json = map2Json(filteredMemberList);

        return json;
    }

    public String getPartnerList1(HttpServletRequest request,HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String partneridwithname = request.getParameter("partneridwithname");
        logger.debug("partneridwithname inside Getdetails:::::" + partneridwithname);
        String q = term.toLowerCase();
        TreeMap<String,String> partnerdetails =new TreeMap<>();
        List<TokenVo> partneriddetails=partnerDAO.getAllPartnerid();
        if (functions.isValueNull(partneridwithname))
        {
            for(TokenVo p:partneriddetails)
            {
                partnerdetails.put(p.getPartnerid()+"-"+ p.getPartnername(),p.getPartnerid()+"-"+ p.getPartnername());
            }
        }
        else
        {
            for(TokenVo p:partneriddetails)
            {
                String key = p.getPartnerid();
                String value =p.getPartnerid()+"-"+ p.getPartnername();
                partnerdetails.put(key,value);
            }
        }
        Map<String,String> filteredMemberList = filterMapByTerm(partnerdetails,q);

        String json = map2Json(filteredMemberList);

        return json;
    }

    public String getAllPartnerList(HttpServletRequest request,HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q = term.toLowerCase();
        TreeMap<String, String> allpartnerdetails=ipEntry.selectAllPartnerId();
        Map<String,String> filteredMemberList = filterMapByTerm(allpartnerdetails,q);

        String json = map2Json(filteredMemberList);

        return json;
    }

    public String getPartnerByMemberList(HttpServletRequest request,HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q = term.toLowerCase();
        String partnerid = request.getParameter("partnerid");
        TreeMap<String,String> partnerdetails =new TreeMap<>();
        List<TokenVo> partneriddetails=partnerDAO.getpartnerTokenDetailByMemberid(partnerid);

        for(TokenVo p:partneriddetails)
        {
            String key = p.getMemberid();
            String value =p.getMemberid()+"-"+ p.getCompanyname();
            partnerdetails.put(key,value);
        }

        Map<String,String> filteredMemberList = filterMapByTerm(partnerdetails,q);

        String json = map2Json(filteredMemberList);

        return json;
    }
    public String getPartnerByMemberListPartner(HttpServletRequest request,HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q = term.toLowerCase();
        String partnerid = request.getParameter("partnerid");
        TreeMap<String,String> partnerdetails =new TreeMap<>();
        List<TokenVo> partneriddetails=partnerDAO.getpartnerTokenDetailByMemberidPartner(partnerid);

        for(TokenVo p:partneriddetails)
        {
            String key = p.getMemberid();
            String value =p.getMemberid()+"-"+ p.getPartnername();
            partnerdetails.put(key,value);
        }

        Map<String,String> filteredMemberList = filterMapByTerm(partnerdetails,q);

        String json = map2Json(filteredMemberList);

        return json;
    }

    public String getGatewayList(HttpServletRequest request,HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q = term.toLowerCase();
        TreeMap<String,String> gatewayTypedetails =new TreeMap<>();
        TreeMap<String,GatewayType> gatewayTypeTreeMap = GatewayTypeService.getAllGatewayTypesMap1();
        for (Map.Entry<String,GatewayType> gateway : gatewayTypeTreeMap.entrySet())
        {
            String key = gateway.getKey();
            GatewayType gt = gateway.getValue();
            gatewayTypedetails.put(key, gt.getGateway().toUpperCase() + "-" + gt.getCurrency() + "-" + gt.getPgTypeId());
        }

        Map<String,String> filteredMemberList = filterMapByTerm(gatewayTypedetails,q);

        String json = map2Json(filteredMemberList);

        return json;
    }

    public String getGatewayList1(HttpServletRequest request,HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String gatetype = request.getParameter("gatetype");
        String q = term.toLowerCase();
        TreeMap<String,String> gatewayTypedetails =new TreeMap<>();
        if (functions.isValueNull(gatetype))
        {
            TreeMap<String,GatewayType> gatewayTypeTreeMap = GatewayTypeService.getAllGatewayTypesSortByGateway1();
            for (Map.Entry<String,GatewayType> gateway : gatewayTypeTreeMap.entrySet())
            {
                String key = gateway.getKey();
                GatewayType gt = gateway.getValue();
                gatewayTypedetails.put(key, gt.getGateway().toUpperCase() + "-" + gt.getCurrency() + "-" + gt.getPgTypeId());
            }
        }
        else
        {
            TreeMap<String,GatewayType> gatewayTypeTreeMap = GatewayTypeService.getAllGatewayTypesSortByGateway();
            for (Map.Entry<String,GatewayType> gateway : gatewayTypeTreeMap.entrySet())
            {
                String key = gateway.getKey();
                GatewayType gt = gateway.getValue();
                gatewayTypedetails.put(key, gt.getGateway().toUpperCase() + "-" + gt.getCurrency() + "-" + gt.getPgTypeId());
            }
        }

        Map<String,String> filteredMemberList = filterMapByTerm(gatewayTypedetails,q);

        String json = map2Json(filteredMemberList);

        return json;
    }

    public String getAccountList(HttpServletRequest request,HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String accountid = request.getParameter("accountid");
        String q = term.toLowerCase();
        GatewayAccountService gatewayAccountService=new GatewayAccountService();
        String gatewayid = "";
        if (functions.isValueNull(accountid))
        {
            if (functions.isValueNull(request.getParameter("gatewayid")))
            {
                gatewayid = request.getParameter("gatewayid");
            }
            else if (functions.isValueNull(request.getParameter("gatewayid1")))
            {
                String gateway[] = request.getParameter("gatewayid1").split(" ");
                gatewayid = gateway[2];
            }
            else
            {
            }
        }
        else
        {
            if (functions.isValueNull(request.getParameter("gatewayid")))
            {
                gatewayid = request.getParameter("gatewayid");
            }
            else if (functions.isValueNull(request.getParameter("gatewayid1")))
            {
                String gateway[] = request.getParameter("gatewayid1").split("-");
                gatewayid = gateway[2];
            }
            else
            {
            }
        }
        TreeMap<String,String> accountdetails =new TreeMap<>();
        TreeMap<Integer, GatewayAccount> accountList = GatewayAccountService.getAccountsDetails(gatewayid);
        for (Map.Entry<Integer,GatewayAccount> accid : accountList.entrySet())
        {
            GatewayAccount acc = accid.getValue();
            accountdetails.put(String.valueOf(accid.getKey()), acc.getAccountId()+"-"+ acc.getMerchantId()+"-"+acc.getCurrency());
        }

        Map<String,String> filteredMemberList = filterMapByTerm(accountdetails,q);

        String json = map2Json(filteredMemberList);

        return json;
    }

    public String getMembersAccountList(HttpServletRequest request,HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String memberid = request.getParameter("memberid");
        String q = term.toLowerCase();
        System.out.println(memberid);

        TreeMap<String,String> accountdetails =new TreeMap<>();
        Connection conn = null;
        ResultSet rs=null;
        try
        {
            conn = Database.getRDBConnection();
            rs = Database.executeQuery("select g.accountid,g.merchantid,g.pgtypeid,t.currency,t.name from gateway_accounts as g JOIN gateway_type as t ON t.pgtypeid=g.pgtypeid " +
                    "JOIN gateway_account_partner_mapping as gapm ON g.accountid=gapm.accountid and gapm.isActive='Y' and g.accountid IN " +
                    "(SELECT accountid FROM member_account_mapping WHERE memberid="+memberid+") order by g.accountid asc ", conn);
            System.out.println("rs" + rs);
            while (rs.next())
            {
                accountdetails.put(String.valueOf(rs.getInt("accountid")), rs.getInt("accountid") + "-" + rs.getString("merchantid") + "-" + rs.getString("currency"));
            }
        }
        catch (Exception e)
        {
            e.getMessage();
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }

        Map<String,String> filteredMemberList = filterMapByTerm(accountdetails,q);

        String json = map2Json(filteredMemberList);

        return json;
    }

    public String getMemberdetailList(HttpServletRequest request,HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q = term.toLowerCase();
        String gatewayid = "";
        String accountid = "";
        if (functions.isValueNull(request.getParameter("gatewayid")))
        {
            gatewayid = request.getParameter("gatewayid");
        }
        else if (functions.isValueNull(request.getParameter("gatewayid1")))
        {
            String gateway[] = request.getParameter("gatewayid1").split("-");
            gatewayid = gateway[2];
        }
        else
        {
        }
        if (functions.isValueNull(request.getParameter("accountid")))
        {
            accountid = request.getParameter("accountid");
        }
        else if (functions.isValueNull(request.getParameter("accountid1")))
        {
            accountid = request.getParameter("accountid1");
        }
        else
        {

        }
        TreeMap<String,String> memberdetails =new TreeMap<>();
        List<TerminalVO> terminalList = null;
        try
        {
            terminalList = terminalManager.getAllMappedTerminals1(gatewayid, accountid, null);
        }
        catch (Exception e)
        {
            logger.error("Exception--->", e);
        }
        for(TerminalVO m : terminalList)
        {
            String key = m.getMemberId();
            String value = m.getMemberId()+"-"+m.getAccountId()+"-"+m.getCompany_name();
            memberdetails.put(key, value);
        }

        Map<String,String> filteredMemberList = filterMapByTerm(memberdetails,q);

        String json = map2Json(filteredMemberList);

        return json;
    }

    public String getMemberdetailListByMemberid(HttpServletRequest request,HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String flag = request.getParameter("flag");
        String q = term.toLowerCase();
        String gatewayid = "";
        String accountid = "";
        String memberid = "";
        if (functions.isValueNull(request.getParameter("gatewayid")))
        {
            gatewayid = request.getParameter("gatewayid");
        }
        else if (functions.isValueNull(request.getParameter("gatewayid1")))
        {
            String gateway[] = request.getParameter("gatewayid1").split("-");
            gatewayid = gateway[2];
        }
        else
        {
        }
        if (functions.isValueNull(request.getParameter("accountid")))
        {
            accountid = request.getParameter("accountid");
        }
        else if (functions.isValueNull(request.getParameter("accountid1")))
        {
            accountid = request.getParameter("accountid1");
        }
        else
        {
        }
        if (functions.isValueNull(request.getParameter("memberid")))
        {
            memberid = request.getParameter("memberid");
        }
        else if (functions.isValueNull(request.getParameter("memberid1")))
        {
            memberid = request.getParameter("memberid1");
        }
        else
        {
        }

        TreeMap<String,String> memberdetails =new TreeMap<>();
        List<TerminalVO> terminalList = null;
        try
        {
            terminalList = terminalManager.getAllMappedTerminals1(gatewayid, accountid,memberid);
        }
        catch (Exception e)
        {
            logger.error("Exception--->", e);
        }
        if (flag.equals("true"))
        {
            logger.debug("inside if:::"+flag);
            System.out.println("inside if:::"+flag);
            for(TerminalVO m : terminalList)
            {
                String key = m.getTerminalId()+"-"+m.getPaymodeId()+"-"+m.getCardTypeId();
                //String value = m.getTerminalId()+"-"+m.getPaymodeId()+"-"+m.getCardTypeId();
                String value = m.getTerminalId()+"-"+GatewayAccountService.getPaymentMode(m.getPaymodeId())+"-"+GatewayAccountService.getCardType(m.getCardTypeId());
                memberdetails.put(key, value);
            }
        }
        else if (flag.equals("false"))
        {
            logger.debug("inside else if:::"+flag);
            System.out.println("inside else if:::"+flag);
            for(TerminalVO m : terminalList)
            {
                String key = m.getTerminalId();
                String value = m.getTerminalId()+"-"+GatewayAccountService.getPaymentMode(m.getPaymodeId())+"-"+GatewayAccountService.getCardType(m.getCardTypeId());
                memberdetails.put(key, value);
            }
        }
        else
        {
            logger.debug("inside else:::"+flag);
            System.out.println("inside else:::"+flag);
            for (TerminalVO m:terminalList)
            {
                String key = m.getTerminalId();
                String value = m.getTerminalId();
                memberdetails.put(key, value);
            }
        }

        Map<String,String> filteredMemberList = filterMapByTerm(memberdetails,q);

        String json = map2Json(filteredMemberList);

        return json;
    }

    public String getAgentNameList(HttpServletRequest request,HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q = term.toLowerCase();
        TreeMap<String, String> agentHash = multipleMemberUtill.getAgentDetails();

        Map<String,String> filteredMemberList = filterMapByTerm(agentHash,q);

        String json = map2Json(filteredMemberList);

        return json;
    }

    public String getAllAgentNameList(HttpServletRequest request,HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q = term.toLowerCase();
        TreeMap<String, String> allagentdetails=ipEntry.selectAllAgentNameList();

        Map<String,String> filteredMemberList = filterMapByTerm(allagentdetails,q);

        String json = map2Json(filteredMemberList);

        return json;
    }

    public String getAgentMemberList(HttpServletRequest request,HttpServletResponse response)
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
    public String getMemberAccountList(HttpServletRequest request,HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q = term.toLowerCase();
        String memberId = request.getParameter("memberid");
        String agentId=request.getParameter("agentid");
        TreeMap<String, String> memberHash = new TreeMap<String, String>();

        if (functions.isValueNull(memberId) && functions.isValueNull(agentId))
        {
            List<MerchantDetailsVO> merchantDetailsVOList=agentManager.getAgentMemberAccountListFromMapped(memberId, agentId);
            for (MerchantDetailsVO mdv : merchantDetailsVOList)
            {
                memberHash.put(mdv.getAccountId(), mdv.getAccountId()+"-"+ mdv.getMemberId());
            }
        }
        else
        {
            memberHash = multipleMemberUtill.getMemberAccountDetails(agentId);
        }
        Map<String,String> filteredMemberList = filterMapByTerm(memberHash,q);
        String json = map2Json(filteredMemberList);
        return json;
    }
    public String bankWireList(HttpServletRequest request,HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q = term.toLowerCase();
        String accountId = request.getParameter("accountid");
        BankManager bankManager=new BankManager();
        TreeMap<String, BankWireManagerVO> bankWiresMap=null;
        TreeMap<String, String> memberHash = new TreeMap<String, String>();

        bankWiresMap = bankManager.getBankWiresForAgent(accountId);
        for (String bankId : bankWiresMap.keySet())
        {
            BankWireManagerVO bankWireManagerVO = bankWiresMap.get(bankId);
            String accountId1 =bankWireManagerVO.getAccountId();
            String MID = bankWireManagerVO.getMid();
            String value = bankId + "-" + accountId1 + "-" + MID;
            memberHash.put(bankId, value);
        }

        Map<String,String> filteredMemberList = filterMapByTerm(memberHash,q);
        String json = map2Json(filteredMemberList);
        return json;
    }

    public String getAgentMemberTerminalList(HttpServletRequest request,HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q = term.toLowerCase();
        String agentid = request.getParameter("agentid");
        String memberid = request.getParameter("memberid");
        TreeMap<String, String> memberHash = new TreeMap<String, String>();
        LinkedHashMap<String,TerminalVO> terminalVOTreeMap=terminalManager.getTerminalMap();
        for (String terminal : terminalVOTreeMap.keySet())
        {
            TerminalVO terminalVO = terminalVOTreeMap.get(terminal);
            String payMode = "";
            String cardType = "";
            if(terminalVO.getPaymodeId()!=null)
            {
                payMode = GatewayAccountService.getPaymentMode(terminalVO.getPaymodeId());
                cardType = GatewayAccountService.getCardType(terminalVO.getCardTypeId());
            }

            String value = terminalVO.getMemberId()+"-"+terminalVO.getTerminalId();
            if (functions.isEmptyOrNull(memberid))
            {
                memberHash.put(terminal, value+"-"+payMode+"-"+cardType);
            }
            else
            {
                if (terminalVO.getMemberId().equals(memberid))
                {
                    memberHash.put(terminalVO.getTerminalId(), value+"-"+payMode+"-"+cardType);
                }
            }
        }
        Map<String,String> filteredMemberList = filterMapByTerm(memberHash,q);

        String json = map2Json(filteredMemberList);

        return json;
    }

    public String getUsernameList(HttpServletRequest request,HttpServletResponse response) throws PZDBViolationException
    {
        String term = request.getParameter("term");
        String role = request.getParameter("role");
        System.out.println("role"+role);

        String q = term.toLowerCase();
        TreeMap<String, String> getUsernameList = profileManagementManager.getUsernameList(role);

        Map<String,String> filteredMemberList = filterMapByTerm(getUsernameList, q);

        String json = map2Json(filteredMemberList);


        return json;
    }


    public String getCardTypeList(HttpServletRequest request,HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q = term.toLowerCase();
        TreeMap<String, String> treeMap = transactionDAO.getCardTypeLists();
        Map<String,String> filteredMemberList = filterMapByTerm(treeMap, q);

        String json = map2Json(filteredMemberList);

        return json;
    }

    public String getIssuingBankList(HttpServletRequest request,HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q = term.toLowerCase();
        TreeMap<String, String> treeMap =  transactionDAO.getIssuing_BankList();
        Map<String,String> filteredMemberList = filterMapByTerm(treeMap,q);

        String json = map2Json(filteredMemberList);

        return json;
    }

    public List<String> filterListByTerm(List<String> list, String term) {

        List<String> matching = list.stream()
                .filter(e -> e.toLowerCase().startsWith(term))
                .collect(Collectors.toList());

        return matching;
    }

    public Map<String,String> filterMapByTerm(Map<String,String> map, String term) {

        Map<String,String> matching = map.entrySet().stream()
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
            logger.error("JsonProcessingException--->", e);
        }
        return json;
    }

    public String map2Json(Map<String,String> map) {

        String json = null;
        ObjectMapper objectMapper = new ObjectMapper();

        try
        {
            json = objectMapper.writeValueAsString(map);
        }
        catch (JsonProcessingException e)
        {
            logger.error("JsonProcessingException--->", e);
        }
        return json;
    }

   public JsonObject getPaymodeidByGateway(HttpServletRequest request,HttpServletResponse response)
    {
        String pgtypeid ="";
        String gatewaylist[] = request.getParameter("gateway").split("-");
        pgtypeid = gatewaylist[2];
        System.out.println(pgtypeid);
        GatewayAccountService gatewayAccountService = new GatewayAccountService();
        TreeMap<String, String> Details = null;
        Details = gatewayAccountService.getPaymodeIdGateway(pgtypeid);

        JsonArray data_json = new JsonArray();
        JsonObject json_response = new JsonObject();

        for (String id : Details.keySet())
        {
            JsonObject json = new JsonObject();
            json.addProperty("value", id);
            json.addProperty("text", "-" + Details.get(id));
            data_json.add(json);
        }

        TreeMap<String, String> Details_card = null;
        Details_card = gatewayAccountService.getCardtypeIdGateway(pgtypeid);

        JsonArray data_json_card = new JsonArray();

        for (String id : Details_card.keySet())
        {
            JsonObject json = new JsonObject();
            json.addProperty("value", id);
            json.addProperty("text", "-" + Details_card.get(id));
            data_json_card.add(json);
        }

        json_response.add("aaData", data_json);
        json_response.add("bbData", data_json_card);
        return json_response;
    }

    private String getCardTypeDetails(HttpServletRequest request, HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q = term.toLowerCase();

        TreeMap<String,String> CardTypeDetails = multipleMemberUtill.getCardTypeDetail();
        Map<String,String> filterCardTypeList = filterMapByTerm(CardTypeDetails,q);
        String json = map2Json(filterCardTypeList);
        return json;
    }

    private String getBankwireIdList(HttpServletRequest request, HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q= term.toLowerCase();
        GatewayAccountService gatewayAccountService=new GatewayAccountService();


        TreeMap<String,String> bankwirelist= gatewayAccountService.getBankWireIdList();
        Map<String,String> filterbankwirelist= filterMapByTerm(bankwirelist, q);
        String json= map2Json(filterbankwirelist);
        return json;
    }

    public String getParent_BankWireIdList(HttpServletRequest request, HttpServletResponse response) {
        String accountid = request.getParameter("accountid");
        String gatewayid = request.getParameter("gatewayid");
        String term= request.getParameter("term");
        String q=term.toLowerCase();
        GatewayAccountService gatewayAccountService=new GatewayAccountService();
        TreeMap<String, String> utilgettemplateIdList;
        utilgettemplateIdList = gatewayAccountService.getParent_BankWireIdList(accountid, gatewayid);

        Map<String,String> filteredMembersortedMap = filterMapByTerm(utilgettemplateIdList,q);
        String json = map2Json(filteredMembersortedMap);
        return json;
    }

    private String getGatewayNameList(HttpServletRequest request, HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q = term.toLowerCase();

        TreeMap<String,String> gatewayNameList = multipleMemberUtill.getGatewayNameList();
        Map<String,String> filterGatewayNameList = filterMapByTerm(gatewayNameList,q);
        String json = map2Json(filterGatewayNameList);
        return json;
    }

}