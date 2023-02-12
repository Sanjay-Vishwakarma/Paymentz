package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.vo.PaginationVO;
import com.payment.IPEntry;
import com.payment.MultiplePartnerUtill;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Diksha on 18-May-20.
 */
@WebServlet(name = "PartnerIpWhitelistConfig")
public class PartnerIpWhitelistConfig extends HttpServlet
{
    static Logger log = new Logger(PartnerIpWhitelistConfig.class.getName());
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request,response);
    }
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        RequestDispatcher rd = req.getRequestDispatcher("/partnerIpWhitelist.jsp?ctoken=" + user.getCSRFToken());
        PartnerFunctions partnerFunctions = new PartnerFunctions();
        PaginationVO paginationVO=new PaginationVO();
        Functions functions = new Functions();
        IPEntry ipEntry = new IPEntry();
        if (!partnerFunctions.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/logout.jsp");
            return;
        }
        log.error("Inside PartnerIpWhitelistConfig---");
        String error="";
        String message="";
        String action = req.getParameter("action");
        String username=(String)session.getAttribute("username");
        String actionExecutorId=session.getAttribute("merchantid")+"";
        String pageNo=req.getParameter("SPageno")!=null?req.getParameter("SPageno"):"";
        String record=req.getParameter("SRecords")!=null?req.getParameter("SRecords"):"";
        String role= String.valueOf(user.getRoles());
        String actionExecutorName=role+"-"+username;
        log.error("username==="+username);
        log.error("role==="+role);
        Hashtable recordHash=null;
        Hashtable temphash=null;
        //error=error+validateOptionalParameters(req);
       /* if(functions.isValueNull(error))
        {
            req.setAttribute("error", error);
            rd.forward(req, res);
            return;
        }*/

        String error1="";
        error1 = error1+validateOptionalParameters(req);
        if(error1.length()>0)
        {
            req.setAttribute("error",error1);
            rd.forward(req,res);
            return;
        }

        String partnerid = (String) session.getAttribute("merchantid");
        String pid = req.getParameter("pid");

        if (!ESAPI.validator().isValidInput("pid", req.getParameter("pid"), "Numbers", 10, false))
        {
            req.setAttribute("error","Invalid Partner ID");
            rd = req.getRequestDispatcher("/partnerIpWhitelist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        try
        {
            if (functions.isValueNull(req.getParameter("pid")) && !partnerFunctions.isPartnerSuperpartnerMapped(req.getParameter("pid"), partnerid))
            {
                error = "Invalid Partner Mapping.";
                req.setAttribute("error", error);
                rd = req.getRequestDispatcher("/partnerIpWhitelist.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
        }
        catch (Exception e)
        {
            log.error("Exception---" + e);
        }
        int pageno = functions.convertStringtoInt(pageNo, 1);
        int records = functions.convertStringtoInt(record, 15);
        paginationVO.setPageNo(pageno);
        paginationVO.setRecordsPerPage(records);

        if ((functions.isValueNull(pid)) && (functions.isValueNull(action)))
        {
            if (action.equalsIgnoreCase("delete"))
            {
                ipEntry.deleteIPForPartner(pid, req.getParameter("ipAddress"));
                message = "Record Deletion Successful for Partner" + "<BR>";
                req.setAttribute("success", message);
            }

            if (action.equalsIgnoreCase("add"))
            {
                message=validateMandatoryParameters(req);
                log.error("partnerId----" + pid);
                String type = req.getParameter("type");
                boolean isIPv4 = isIPv4(req.getParameter("ipAddress"));
                boolean isIPv6 = isIPv6(req.getParameter("ipAddress"));
                String startIp="";
                boolean isDuplicateIpFound = false;
                boolean isLimit = false;
                recordHash = ipEntry.retrievIPForPartner(pid, paginationVO, actionExecutorId, actionExecutorName, isLimit);
                for (int pos = 1; pos <= recordHash.size() - 2; pos++)
                {
                    String id = Integer.toString(pos);
                    temphash = (Hashtable) recordHash.get(id);
                    startIp = (String) temphash.get("ipAddress");
                    if (startIp.equals(req.getParameter("ipAddress")))
                    {
                        isDuplicateIpFound = true;
                        break;
                    }
                }
                if (isIPv4 == true && isIPv6 == false && type.equals("IPv4"))
                {
                    type = req.getParameter("type");
                }
                else if (isIPv6 == true && isIPv4 == false && type.equals("IPv6"))
                {
                    type = req.getParameter("type");
                }
                else
                {
                    message = "Please enter valid " + type + " type of IP Address for selected Partner"+"<BR>";
                    req.setAttribute("message", message);
                }
                if (req.getParameter("ipAddress").equals(""))
                {
                    message = "Please enter valid IP Address for selected Partner"+"<BR>";
                    req.setAttribute("message", message);
                }
                if (!message.equals(""))
                {
                    req.setAttribute("message", message);
                }
                else if (isDuplicateIpFound)
                {
                    message = "IP Address already added for selected Partner"+"<BR>";
                    req.setAttribute("message", message);
                }
                else
                {
                    ipEntry.insertIPForPartner(pid, req.getParameter("ipAddress"), actionExecutorId, actionExecutorName, type);
                    message = "Record Insertion Successful for Partner"+"<BR>";
                    req.setAttribute("success", message);
                    }
                }

            }

            recordHash=ipEntry.retrievIPForPartner(pid,paginationVO,actionExecutorId,actionExecutorName,true);
            log.error("hash---"+recordHash);
            log.error("partnerId----" + pid);
            paginationVO.setInputs("pid="+pid);
            req.setAttribute("recordHash",recordHash);
            req.setAttribute("partnerid",pid);

        log.error("ending PartnerIpWhitelistConfig--------------------------");
        rd=req.getRequestDispatcher("/partnerIpWhitelist.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req,res);

        return;

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
        List<InputFields>  inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.PAGENO);
        inputFieldsListOptional.add(InputFields.RECORDS);
        //inputFieldsListOptional.add(InputFields.IPADDRESS);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional,errorList,true);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
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
}
