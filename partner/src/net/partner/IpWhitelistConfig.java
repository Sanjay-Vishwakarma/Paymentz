package net.partner;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.vo.PaginationVO;
import com.payment.IPEntry;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Vivek on 11/13/2018.
 */
public class IpWhitelistConfig extends HttpServlet
{
    private static Logger logger = new Logger(IpWhitelistConfig.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = Functions.getNewSession(req);
        String partnerId = session.getAttribute("partnerId").toString();
        PartnerFunctions partnerFunctions = new PartnerFunctions();
        PaginationVO paginationVO=new PaginationVO();
        Functions functions = new Functions();
        IPEntry ipEntry = new IPEntry();

        if (!partnerFunctions.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/logout.jsp");
            return;
        }

        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        RequestDispatcher rd = req.getRequestDispatcher("/ipwhitelistconfig.jsp?ctoken=" + user.getCSRFToken());

        String action = req.getParameter("upload");
        String memberId = req.getParameter("memberid");
        String pid = req.getParameter("pid");
        String type = req.getParameter("type");
        String ipAddress=req.getParameter("ipAddress");
        String delete=req.getParameter("delete");
        String[] ids=req.getParameterValues("id");
        String role=(String)session.getAttribute("role");
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;

        Hashtable memberHash = null;
        Hashtable recordHash = null;
        Hashtable tempHash = null;
        String msg = "";
        String startIp = "";
        String error = "";
        String EOL = "<BR>";
        int pageno=1;
        int records=15;

        boolean isIPv4=false;
        boolean isIPv6=false;

        error = error+validateOptionalParameter(req);
        if(error.length()>0)
        {
            req.setAttribute("error",error);
            rd.forward(req,res);
            return;
        }


        try
        {
            pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("SPageno",req.getParameter("SPageno"),"Numbers",5,true), 1);
            records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("SRecords",req.getParameter("SRecords"),"Numbers",5,true), 15);
        }
        catch(ValidationException e)
        {
            logger.error("Invalid page no or records",e);
            pageno = 1;
            records = 15;
        }

        paginationVO.setInputs("memberid="+memberId+"&type="+type+"&ipAddress="+ipAddress+"&pid="+pid);
        paginationVO.setPageNo(pageno);
        paginationVO.setRecordsPerPage(records);

        try
        {
            error=validateOptionalParameter(req);
            if (!ESAPI.validator().isValidInput("pid", pid, "Numbers", 10, true))
            {
                error = "Invalid PartnerId" + EOL;
            }
            if (!ESAPI.validator().isValidInput("memberid", memberId, "Numbers", 20, false))
            {
                error = "Invalid MemberID or MemberID should not be empty" + EOL;
            }
            else
            {

                try
                {
                    if (functions.isValueNull(pid) && !partnerFunctions.isPartnerMemberMapped(memberId, pid))
                    {
                        error = "Partner ID/Member ID NOT mapped." + EOL;
                    }
                    else if (!functions.isValueNull(req.getParameter("pid")) && !partnerFunctions.isPartnerSuperpartnerMembersMapped(memberId, partnerId))
                    {
                        error = "Partner ID/Member ID NOT mapped." + EOL;
                    }
                }
                catch (Exception e)
                {
                    logger.error("Exception---" + e);
                }
            }

            if(!error.equals(""))
                    {
                        req.setAttribute("error",error);
                        rd.forward(req,res);
                        return;
                    }


                    isIPv4 = isIPv4(req.getParameter("ipAddress"));
                    isIPv6 = isIPv6(req.getParameter("ipAddress"));

                    if (isIPv4 == true && isIPv6 == false && type.equals("IPv4") && functions.isValueNull(req.getParameter("ipAddress")))
                    {
                        type = req.getParameter("type");
                    }
                    else if (isIPv6 == true && isIPv4 == false && type.equals("IPv6") && functions.isValueNull(req.getParameter("ipAddress")))
                    {
                        type = req.getParameter("type");
                    }
                    else if(type.equals(""))
                    {
                        memberHash = ipEntry.retrievIPForMerchant1(memberId, ipAddress,type,paginationVO,actionExecutorId,actionExecutorName,true);
                    }
                    else
                    {
                        error = "Please enter valid " + type + " type of IP Address";
                        req.setAttribute("error", error);
                        rd.forward(req,res);
                        return;
                    }

                memberHash = ipEntry.retrievIPForMerchant1(memberId, ipAddress,type,paginationVO,actionExecutorId,actionExecutorName,true);
                if("upload".equalsIgnoreCase(action))
                {
                    if (!functions.isValueNull(ipAddress))
                    {
                        error = "Please select IP Type";
                        req.setAttribute("error", error);
                        req.setAttribute("msg", msg);
                        rd.forward(req,res);
                        return;
                    }
                    boolean isDuplicateIpFound = false;
                    recordHash = ipEntry.retrievIPForMerchant1(memberId, req.getParameter("ipAddress"), type, paginationVO,actionExecutorId,actionExecutorName,false);
                    for (int pos = 1; pos <= recordHash.size() - 2; pos++)
                    {
                        String id = Integer.toString(pos);
                        tempHash = (Hashtable) recordHash.get(id);
                        startIp = (String) tempHash.get("ipAddress");
                        if (startIp.equals(req.getParameter("ipAddress")))
                        {
                            isDuplicateIpFound = true;
                            break;
                        }
                    }
                    if (!functions.isValueNull(type) || type.equals(""))
                    {
                        error = "Please select IP Type";
                        req.setAttribute("error", error);
                        req.setAttribute("msg", msg);
                        rd.forward(req, res);
                        return;
                    }
                    else if (isDuplicateIpFound)
                    {
                        msg = "Record Already Uploaded";
                        req.setAttribute("msg", msg);
                        rd.forward(req, res);
                        return;
                    }
                    else
                    {
                        ipEntry.insertIPForMerchant(memberId, req.getParameter("ipAddress"),actionExecutorId,actionExecutorName,type);
                        msg = "Record Uploaded Successfully.";
                        req.setAttribute("msg", msg);
                    }
                    memberHash = ipEntry.retrievIPForMerchant1(memberId, ipAddress, type, paginationVO,actionExecutorId,actionExecutorName,true);
                }

                if("delete".equalsIgnoreCase(delete))
                {
                    for(String id : ids)
                    {
                        ipEntry.deleteIPForMerchant1(id);
                    }
                    msg = "Record Deleted Successfully";
                    memberHash = ipEntry.retrievIPForMerchant1(memberId, "",type,paginationVO,actionExecutorId,actionExecutorName,true);
                }
        }
        catch (Exception e)
        {
            logger.debug("Exception::::" + e);
        }
        req.setAttribute("recordHash", memberHash);
        req.setAttribute("memberId",memberId);
        req.setAttribute("paginationVO", paginationVO);
        req.setAttribute("error", error);
        req.setAttribute("msg", msg);
        rd.forward(req, res);
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

    private String validateOptionalParameter(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        //inputFieldsListOptional.add(InputFields.MEMBERID);
        inputFieldsListOptional.add(InputFields.IPADDRESS);
        inputFieldsListOptional.add(InputFields.PAGENO);
        inputFieldsListOptional.add(InputFields.RECORDS);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional,errorList,true);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }
}

