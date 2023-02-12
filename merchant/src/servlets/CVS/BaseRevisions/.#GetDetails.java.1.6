import com.directi.pg.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.manager.GatewayManager;
import com.manager.TerminalManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.TerminalVO;
import com.payment.exceptionHandler.PZDBViolationException;
import org.owasp.esapi.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created with IntelliJ IDEA.
 * User: Ajit
 * Date: 11/4/18
 * Time: 3:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetDetails extends HttpServlet
{
    Logger logger=new Logger(GetDetails.class.getName());
    private static TerminalManager terminalManager = new TerminalManager();
    MerchantDAO merchantDAO = new MerchantDAO();
    GatewayManager gatewayManager = new GatewayManager();
    Functions functions = new Functions();
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
        String json = "";

        if("currencyList".equals(method))
        {
            json = getCurrencyList(request, response);
        }
        else if ("terminalList".equals(method))
        {
            json = getTerminalList(request,response);
        }
        else if ("accountList".equals(method))
        {
            json = getAccountList(request,response);
        }
        else if ("issuingbankList".equals(method))
        {
            json = getIssuingBankList(request, response);
        }
        response.getWriter().write(json);
    }

    public String getCurrencyList(HttpServletRequest request,HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q = term.toLowerCase();
        String merchantid = request.getParameter("merchantid");
        String role = request.getParameter("role").toString();
        TreeMap<String,String> terminladetails =new TreeMap<>();
        List<TerminalVO> terminalVOList = null;

        try
        {
            terminalVOList = terminalManager.getCurrencyList(merchantid, role);
        }
        catch (PZDBViolationException e)
        {
            logger.debug("PZDBViolationException::::"+e);
        }

        for(TerminalVO terminalVO1:terminalVOList)
        {
            String key = terminalVO1.getCurrency();
            String value = terminalVO1.getCurrency();
            terminladetails.put(key,value);
        }

        Map<String,String> filteredMemberList = filterMapByTerm(terminladetails,q);

        String json = map2Json(filteredMemberList);

        return json;

    }

    public String getTerminalList(HttpServletRequest request,HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q = term.toLowerCase();
        String merchantid = request.getParameter("merchantid");
        String currency = request.getParameter("currency");
        TreeMap<String,String> terminladetails =new TreeMap<>();
        List<TerminalVO> terminalVOList = null;

            try
            {
                terminalVOList = terminalManager.getMemberandTerminalList(merchantid, currency);
            }
            catch (PZDBViolationException e)
            {
                logger.debug("PZDBViolationException::::"+e);
            }

        for(TerminalVO terminalVO1:terminalVOList)
        {
            String key = terminalVO1.getTerminalId();
            String value = terminalVO1.getTerminalId()+"-"+terminalVO1.getPaymentTypeName()+"-"+terminalVO1.getCardType()+"-" + terminalVO1.getCurrency() + "-" + (terminalVO1.getIsActive().equals("Y") ? "Active" : "InActive") ;
            terminladetails.put(key,value);
        }

        Map<String,String> filteredMemberList = filterMapByTerm(terminladetails,q);

        String json = map2Json(filteredMemberList);

        return json;

    }

    public String getAccountList(HttpServletRequest request,HttpServletResponse response)
    {
        String term = request.getParameter("term");
        String q = term.toLowerCase();
        String merchantid = request.getParameter("merchantid");
        TreeMap<String,String> accountdetails =new TreeMap<>();
        accountdetails = merchantDAO.getAccountsListOnMemberid(merchantid);
        Map<String,String> filteredMemberList = filterMapByTerm(accountdetails,q);

        String json = map2Json(filteredMemberList);

        return json;

    }
    public String getIssuingBankList(HttpServletRequest request,HttpServletResponse response)
    {
        logger.debug("inside getGatewayList in merchant::::");
        String term = request.getParameter("term");
        String q = term.toLowerCase();
        TreeMap<String,String> gatewayTypedetails =new TreeMap<>();
        String merchantid = request.getParameter("merchantid");
        try
        {
            gatewayTypedetails = gatewayManager.getIssuingBankForMerchant(merchantid);
        }
        catch (Exception e)
        {
            logger.error("Exception in getGatewayList in merchant::::", e);
        }
        Map<String,String> filteredMemberList = filterMapByTerm(gatewayTypedetails,q);

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
            logger.error("JsonProcessingException in list2Json in merchant::::", e);
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
            logger.error("JsonProcessingException in map2Json in merchant::::", e);
        }
        return json;
    }
}
