import com.directi.pg.Admin;
import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.vo.PaginationVO;
import com.payment.IPEntry;

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
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 3/31/14
 * Time: 2:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class IpWhitelistConfig extends HttpServlet
{
    static Logger log = new Logger(IpWhitelistConfig.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        Functions functions= new Functions();
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        String error = "";
        error=error+validateOptionalParameters(req);
        String message = "";
        IPEntry ipEntry = new IPEntry();
        String action = req.getParameter("action");
        String memberId = req.getParameter("merchantid");
        String partnerId = req.getParameter("partnerid");
        String agentId = req.getParameter("agentid");
        String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;
        IpWhitelistConfig ipconf= new IpWhitelistConfig();

        Hashtable memberHash = null;
        Hashtable partnerHash = null;
        Hashtable agentHash =null;
        Hashtable recordHash = null;
        Hashtable tempHash = null;
        int pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        int records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);
        PaginationVO paginationVO=new PaginationVO();
        paginationVO.setPageNo(pageno);
        paginationVO.setRecordsPerPage(records);
        if(req.getParameter("group1")!=null && !req.getParameter("group1").equals(""))
        {
            String group = req.getParameter("group1");
            if(group.equalsIgnoreCase("mId"))
            {
                if(!functions.isValueNull(memberId))
                {
                    message = "<font class=\"text\" face=\"arial\">"+"<center>"+"<b>"+"Please select Member from Dropdown"+"</b>"+"</center>"+"</font>"+"<BR><BR>";
                    req.setAttribute("message",message);
                    log.debug(message);
                    RequestDispatcher rd = req.getRequestDispatcher("/ipwhitelistconfig.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }
                else if(functions.isValueNull(memberId) && (!ESAPI.validator().isValidInput("merchantid",memberId,"Numbers",10,false)))
                {
                    message = "<font class=\"text\" face=\"arial\">"+"<center>"+"<b>"+"Invalid Merchant Id"+"</b>"+"</center>"+"</font>"+"<BR><BR>";
                    req.setAttribute("message",message);
                    RequestDispatcher rd= req.getRequestDispatcher("/ipwhitelistconfig.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(req,res);
                    return;
                }
                else if(functions.isValueNull(memberId) && !ipconf.isMerchantIdPresent(memberId))
                {
                    message = "<font class=\"text\" face=\"arial\">"+"<center>"+"<b>"+"Invalid Merchant Id"+"</b>"+"</center>"+"</font>"+"<BR><BR>";
                    req.setAttribute("message",message);
                    RequestDispatcher rd= req.getRequestDispatcher("/ipwhitelistconfig.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(req,res);
                    return;
                }
                else
                {
                    memberId=req.getParameter("merchantid");
                }
            }
            else if(group.equalsIgnoreCase("pId"))
            {
                log.debug("=======inside radio select as partner");
                if(!functions.isValueNull(partnerId))
                {
                    message = "<font class=\"text\" face=\"arial\">"+"<center>"+"<b>"+"Please select Partner from Dropdown"+"</b>"+"</center>"+"</font>"+"<BR><BR>";
                    req.setAttribute("message",message);
                    log.debug(message);
                    RequestDispatcher rd = req.getRequestDispatcher("/ipwhitelistconfig.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }
                else if(functions.isValueNull(partnerId) && !ESAPI.validator().isValidInput("partnerid",partnerId,"Numbers",10,false))
                {
                    message = "<font class=\"text\" face=\"arial\">"+"<center>"+"<b>"+"Invalid Partner Id"+"</b>"+"</center>"+"</font>"+"<BR><BR>";
                    req.setAttribute("message",message);
                    RequestDispatcher rd= req.getRequestDispatcher("/ipwhitelistconfig.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(req,res);
                    return;
                }
                else if(functions.isValueNull(partnerId) && !ipconf.isPartnerIdPresent(partnerId))
                {
                    message = "<font class=\"text\" face=\"arial\">"+"<center>"+"<b>"+"Invalid Partner Id"+"</b>"+"</center>"+"</font>"+"<BR><BR>";
                    req.setAttribute("message",message);
                    RequestDispatcher rd= req.getRequestDispatcher("/ipwhitelistconfig.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(req,res);
                    return;
                }
                else
                {
                    log.debug("======= partner as 1"+partnerId);
                    partnerId = req.getParameter("partnerid");
                }
            }
            else if(group.equalsIgnoreCase("aId"))
            {
                if(!functions.isValueNull(agentId))
                {
                    message = "<font class=\"text\" face=\"arial\">"+"<center>"+"<b>"+"Please select Agent from Dropdown"+"</b>"+"</center>"+"</font>"+"<BR><BR>";
                    req.setAttribute("message",message);
                    log.debug(message);
                    RequestDispatcher rd = req.getRequestDispatcher("/ipwhitelistconfig.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }
                else if(functions.isValueNull(agentId) && (!ESAPI.validator().isValidInput("agentid",agentId,"Numbers",10,false)))
                {
                    message = "<font class=\"text\" face=\"arial\">"+"<center>"+"<b>"+"Invalid Agent Id"+"</b>"+"</center>"+"</font>"+"<BR><BR>";
                    req.setAttribute("message",message);
                    RequestDispatcher rd= req.getRequestDispatcher("/ipwhitelistconfig.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(req,res);
                    return;
                }
               else if(functions.isValueNull(agentId) && !ipconf.isAgentIdPresent(agentId))
                {
                    message = "<font class=\"text\" face=\"arial\">"+"<center>"+"<b>"+"Invalid Agent Id"+"</b>"+"</center>"+"</font>"+"<BR><BR>";
                    req.setAttribute("message",message);
                    RequestDispatcher rd= req.getRequestDispatcher("/ipwhitelistconfig.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(req,res);
                    return;
                }
                else
                {
                    agentId=req.getParameter("agentid");
                }
            }
        }

        if(memberId!=null && !memberId.equals("") && !memberId.equals("null"))
        {
            if(action!=null && !action.equals(""))
            {
                if(action.equalsIgnoreCase("delete"))
                {
                    message = validateMandatoryParameters(req);
                    if(!message.equals(""))
                    {
                        message = "<font class=\"text\" face=\"arial\">"+"<center>"+"<b>"+"Please enter IP Address for  selected Merchant"+"</b>"+"</center>"+"</font>"+"<BR><BR>";
                        req.setAttribute("message",message);
                    }
                    else
                    {
                        ipEntry.deleteIPForMerchant(memberId,req.getParameter("ipAddress"));
                        message = "<font class=\"text\" face=\"arial\">"+"<center>"+"<b>"+"Record Deletion Successful for Merchant"+"</b>"+"</center>"+"</font>"+"<BR><BR>";
                        req.setAttribute("message",message);
                    }
                }
                if(action.equalsIgnoreCase("add"))
                {
                    message = validateMandatoryParameters(req);
                    String type=req.getParameter("type_"+memberId);
                    boolean isIPv4=isIPv4(req.getParameter("ipAddress"));
                    boolean isIPv6=isIPv6(req.getParameter("ipAddress"));

                    String startIp="",endIp;
                    boolean isDuplicateIpFound=false;
                    recordHash=ipEntry.retrievIPForMerchant(memberId,paginationVO,actionExecutorId,actionExecutorName,false);
                    for(int pos=1;pos<=recordHash.size()-2;pos++)
                    {
                        String id=Integer.toString(pos);
                        tempHash=(Hashtable)recordHash.get(id);
                        startIp=(String)tempHash.get("ipAddress");
                        if(startIp.equals(req.getParameter("ipAddress"))){
                            isDuplicateIpFound=true;
                            break;
                        }
                    }
                    if(isIPv4==true && isIPv6==false && type.equals("IPv4"))
                    {
                        type=req.getParameter("type_"+memberId);
                    }
                    else  if(isIPv6==true && isIPv4==false && type.equals("IPv6"))
                    {
                        type=req.getParameter("type_"+memberId);
                    }
                    else
                    {
                        message = "<font class=\"text\" face=\"arial\">"+"<center>"+"<b>"+"Please enter valid "+type+" type of IP Address for selected Merchant"+"</b>"+"</center>"+"</font>"+"<BR><BR>";
                        req.setAttribute("message",message);
                    }
                    if(req.getParameter("ipAddress").equals(""))
                    {
                        message = "<font class=\"text\" face=\"arial\">"+"<center>"+"<b>"+"Please enter valid IP Address for selected Merchant"+"</b>"+"</center>"+"</font>"+"<BR><BR>";
                        req.setAttribute("message",message);
                    }
                    if(!message.equals(""))
                    {
                        req.setAttribute("message",message);
                        message="";
                    }
                    else if(isDuplicateIpFound)
                    {
                        message = "<font class=\"text\" face=\"arial\">"+"<center>"+"<b>"+"IP Address already added for selected Merchant"+"</b>"+"</center>"+"</font>"+"<BR><BR>";
                        req.setAttribute("message",message);
                    }
                    else
                    {
                        ipEntry.insertIPForMerchant(memberId,req.getParameter("ipAddress"),actionExecutorId,actionExecutorName,type);
                        message = "<font class=\"text\" face=\"arial\">"+"<center>"+"<b>"+"Record Insertion Successful for Merchant"+"</b>" + "</center>"+"</font>" + "<BR><BR>";
                        req.setAttribute("message",message);
                    }
                }
            }
            memberHash = ipEntry.retrievIPForMerchant(memberId,paginationVO,actionExecutorId,actionExecutorName,true);
            log.debug(memberHash);
            req.setAttribute("recordHash", memberHash);
            req.setAttribute("merchantid",memberId);
            req.setAttribute("paginationVO", paginationVO);
            req.setAttribute("group1","mId");
        }
        else if(partnerId != null && !partnerId.equals("") && !partnerId.equals("null"))
        {
            if(action!=null && !action.equals(""))
            {
                if(action.equalsIgnoreCase("delete"))
                {
                    message = validateMandatoryParameters(req);
                    if(!message.equals(""))
                    {
                        message = "<font class=\"text\" face=\"arial\">"+"<center>"+"<b>"+"Please enter IP Address for  selected Partner"+"</b>"+"</center>"+"</font>"+"<BR><BR>";
                        req.setAttribute("message",message);
                        message="";
                    }
                    else
                    {
                        ipEntry.deleteIPForPartner(partnerId,req.getParameter("ipAddress"));
                        message = "<font class=\"text\" face=\"arial\">"+"<center>"+"<b>"+"Record Deletion Successful for Partner"+"</b>"+"</center>"+"</font>"+"<BR><BR>";
                        req.setAttribute("message",message);
                    }
                }
                if(action.equalsIgnoreCase("add"))
                {
                    message = validateMandatoryParameters(req);
                    String type=req.getParameter("type_"+partnerId);
                    boolean isIPv4=isIPv4(req.getParameter("ipAddress"));
                    boolean isIPv6=isIPv6(req.getParameter("ipAddress"));
                    String startIp,endIp;
                    boolean isDuplicateIpFound=false;
                    boolean isLimit=false;
                    partnerHash=ipEntry.retrievIPForPartner(partnerId, paginationVO,actionExecutorId,actionExecutorName,isLimit);
                    for(int pos=1;pos<=partnerHash.size()-2;pos++)
                    {
                        String id=Integer.toString(pos);
                        tempHash=(Hashtable)partnerHash.get(id);
                        startIp=(String)tempHash.get("ipAddress");
                        if(startIp.equals(req.getParameter("ipAddress"))) /*&& endIp.equals(req.getParameter("endIp"))*/
                        {
                            isDuplicateIpFound=true;
                            break;
                        }
                    }
                    if(isIPv4==true && isIPv6==false && type.equals("IPv4"))
                    {
                        type=req.getParameter("type_"+partnerId);
                    }
                    else  if(isIPv6==true && isIPv4==false && type.equals("IPv6"))
                    {
                        type=req.getParameter("type_"+partnerId);
                    }
                    else
                    {
                        message = "<font class=\"text\" face=\"arial\">"+"<center>"+"<b>"+"Please enter valid "+type+" type of IP Address for selected Partner"+"</b>"+"</center>"+"</font>"+"<BR><BR>";
                        req.setAttribute("message",message);
                    }
                    if(req.getParameter("ipAddress").equals(""))
                    {
                        message = "<font class=\"text\" face=\"arial\">"+"<center>"+"<b>"+"Please enter valid IP Address for selected Partner"+"</b>"+"</center>"+"</font>"+"<BR><BR>";
                        req.setAttribute("message",message);
                    }
                    if(!message.equals(""))
                    {
                        req.setAttribute("message",message);
                        message="";
                    }
                    else if(isDuplicateIpFound)
                    {
                        message = "<font class=\"text\" face=\"arial\">"+"<center>"+"<b>"+"IP Address already added for selected Partner"+"</b>"+"</center>"+"</font>"+"<BR><BR>";
                        req.setAttribute("message",message);
                    }
                    else
                    {
                        ipEntry.insertIPForPartner(partnerId,req.getParameter("ipAddress"),actionExecutorId,actionExecutorName,type);
                        message = "<font class=\"text\" face=\"arial\">"+"<center>"+"<b>"+"Record Insertion Successful for Partner"+"</b>"+"</center>"+"</font>"+"<BR><BR>";
                        req.setAttribute("message",message);
                    }
                }
            }
            partnerHash = ipEntry.retrievIPForPartner(partnerId,paginationVO,actionExecutorId,actionExecutorName,true);
            paginationVO.setInputs("partnerid="+partnerId);
            req.setAttribute("recordHash",partnerHash);
            req.setAttribute("partnerid",partnerId);

            req.setAttribute("group1","pId");
        }
        else if(agentId != null && !agentId.equals("") && !agentId.equals("null"))
        {
            if(action!=null && !action.equals(""))
            {
                if(action.equalsIgnoreCase("delete"))
                {
                    message = validateMandatoryParameters(req);
                    if(!message.equals(""))
                    {
                        message = "<font class=\"text\" face=\"arial\">"+"<center>"+"<b>"+"Please enter IP Address for  selected agent"+"</b>"+"</center>"+"</font>"+"<BR><BR>";
                        req.setAttribute("message",message);
                        message="";
                    }
                    else
                    {
                        ipEntry.deleteIPForAgent(agentId,req.getParameter("ipAddress"));
                        message = "<font class=\"text\" face=\"arial\">"+"<center>"+"<b>"+"Record Deletion Successful for agent"+"</b>"+"</center>"+"</font>"+"<BR><BR>";
                        req.setAttribute("message",message);
                    }
                }
                if(action.equalsIgnoreCase("add"))
                {
                    message = validateMandatoryParameters(req);
                    String type=req.getParameter("type_"+agentId);
                    boolean isIPv4=isIPv4(req.getParameter("ipAddress"));
                    boolean isIPv6=isIPv6(req.getParameter("ipAddress"));
                    String startIp,endIp;
                    boolean isDuplicateIpFound=false;
                    agentHash=ipEntry.retrievIPForAgent(agentId, paginationVO,false);
                    for(int pos=1;pos<=agentHash.size()-2;pos++)
                    {
                        String id=Integer.toString(pos);
                        tempHash=(Hashtable)agentHash.get(id);
                        startIp=(String)tempHash.get("ipAddress");
                        if(startIp.equals(req.getParameter("ipAddress"))) /*&& endIp.equals(req.getParameter("endIp"))*/
                        {
                            isDuplicateIpFound=true;
                            break;
                        }
                    }
                    if(isIPv4==true && isIPv6==false && type.equals("IPv4"))
                    {
                        type=req.getParameter("type_"+agentId);
                    }
                    else  if(isIPv6==true && isIPv4==false && type.equals("IPv6"))
                    {
                        type=req.getParameter("type_"+agentId);
                    }
                    else
                    {
                        message = "<font class=\"text\" face=\"arial\">"+"<center>"+"<b>"+"Please enter valid "+type+" type of IP Address for selected Agent"+"</b>"+"</center>"+"</font>"+"<BR><BR>";
                        req.setAttribute("message",message);
                    }
                    if(req.getParameter("ipAddress").equals(""))
                    {
                        message = "<font class=\"text\" face=\"arial\">"+"<center>"+"<b>"+"Please enter valid IP Address for selected Agent"+"</b>"+"</center>"+"</font>"+"<BR><BR>";
                        req.setAttribute("message",message);
                    }
                    if(!message.equals(""))
                    {
                        req.setAttribute("message",message);
                        message="";
                    }
                    else if(isDuplicateIpFound)
                    {
                        message = "<font class=\"text\" face=\"arial\">"+"<center>"+"<b>"+"IP Address already added for selected Agent"+"</b>"+"</center>"+"</font>"+"<BR><BR>";
                        req.setAttribute("message",message);
                    }
                    else
                    {
                        ipEntry.insertIPForAgent(agentId,req.getParameter("ipAddress"),actionExecutorId,actionExecutorName,type);
                        message = "<font class=\"text\" face=\"arial\">"+"<center>"+"<b>"+"Record Insertion Successful for Agent"+"</b>"+"</center>"+"</font>"+"<BR><BR>";
                        req.setAttribute("message",message);
                    }
                }
            }
            agentHash = ipEntry.retrievIPForAgent(agentId, paginationVO,true);
            paginationVO.setInputs("agentid"+agentId);
            req.setAttribute("recordHash",agentHash);
            req.setAttribute("agentid",agentId);
            req.setAttribute("group1","aId");
        }
        RequestDispatcher rd = req.getRequestDispatcher("/ipwhitelistconfig.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }

    private String validateMandatoryParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields>  inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.IPADDRESS);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListMandatory,errorList,false);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListMandatory)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }

    private String validateOptionalParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields>  inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListMandatory,errorList,true);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListMandatory)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }
    private boolean isIPv4(String ipAddress)
    {
        boolean status=false;
        int count=1;
        char c;
        for (int i=1;i<ipAddress.length();i++)
        {
            c=ipAddress.charAt(i);
            if(c=='.')
            {
                count++;
            }
        }
        if(count==4)
        {
            status=true;
        }
        return  status;
    }
    private boolean isIPv6(String ipAddress)
    {
        boolean status=false;
        int count=1;
        char c;
        for (int i=1;i<ipAddress.length();i++)
        {
            c=ipAddress.charAt(i);
            if(c==':')
            {
                count++;
            }
        }
        if(count==8)
        {
            status=true;
        }
        return  status;
    }

    public boolean isMerchantIdPresent(String memberId)
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String status = null;

        try
        {
            String query;
            con = Database.getRDBConnection();
            query = "SELECT 'X' FROM members WHERE memberid=?";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                status = rs.getString(1);
                if (status.equals("X"))
                {
                    return true;
                }
            }
        }
        catch (Exception e)
        {
            log.error("Exception in isMemberUser method: ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return false;
    }

    public boolean isPartnerIdPresent(String partnerId)
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String status = null;

        try
        {
            String query;
            con = Database.getRDBConnection();
            query = "SELECT 'X' FROM partners WHERE partnerId=?";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, partnerId);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                status = rs.getString(1);
                if (status.equals("X"))
                {
                    return true;
                }
            }
        }
        catch (Exception e)
        {
            log.error("Exception in isPartnerrUser method: ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return false;
    }

    public boolean isAgentIdPresent(String agentId)
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String status = null;

        try
        {
            String query;
            con = Database.getRDBConnection();
            query = "SELECT 'X' FROM agents WHERE agentId=?";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, agentId);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                status = rs.getString(1);
                if (status.equals("X"))
                {
                    return true;
                }
            }
        }
        catch (Exception e)
        {
            log.error("Exception in isAgentUser method: ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return false;
    }
}
