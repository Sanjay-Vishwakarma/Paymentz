import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.manager.vo.PaginationVO;
import com.payment.IPEntry;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.apache.commons.lang3.StringUtils;
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
import java.util.ResourceBundle;

/*Created by IntelliJ IDEA.
        User: sanjeet
        Date: 11/28/2018
        Time: 5:01 PM
        To change this template use File | Settings | File Templates.*/


public class IpWhiteList extends HttpServlet
{
    private static Logger logger = new Logger(IpWhiteList.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        Merchants merchants = new Merchants();
        Functions functions = new Functions();
        ResourceBundle rb1 = null;
        String language_property1 = (String)session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String IpWhiteList_please_errormsg = StringUtils.isNotEmpty(rb1.getString("IpWhiteList_please_errormsg"))?rb1.getString("IpWhiteList_please_errormsg"): "Please provide Member ID.";
        String IpWhiteList_enter_errormsg = StringUtils.isNotEmpty(rb1.getString("IpWhiteList_enter_errormsg"))?rb1.getString("IpWhiteList_enter_errormsg"): "Please enter valid";
        String IpWhiteList_type_errormsg = StringUtils.isNotEmpty(rb1.getString("IpWhiteList_type_errormsg"))?rb1.getString("IpWhiteList_type_errormsg"): "type of IP Address";
        String IpWhiteList_select_errormsg = StringUtils.isNotEmpty(rb1.getString("IpWhiteList_select_errormsg"))?rb1.getString("IpWhiteList_select_errormsg"): "Please select IP Type";
        String IpWhiteList_already_errormsg = StringUtils.isNotEmpty(rb1.getString("IpWhiteList_already_errormsg"))?rb1.getString("IpWhiteList_already_errormsg"): "Record Already Uploaded";
        String IpWhiteList_uploaded_errormsg = StringUtils.isNotEmpty(rb1.getString("IpWhiteList_uploaded_errormsg"))?rb1.getString("IpWhiteList_uploaded_errormsg"): "Record Uploaded Successfully.";
        String IpWhiteList_deleted_errormsg = StringUtils.isNotEmpty(rb1.getString("IpWhiteList_deleted_errormsg"))?rb1.getString("IpWhiteList_deleted_errormsg"): "Record Deleted Successfully";
        PaginationVO paginationVO = new PaginationVO();
        String memberId = (String) session.getAttribute("merchantid");
        String type = req.getParameter("type");
        String ipAddress = req.getParameter("ipAddress");
        String action = req.getParameter("upload");
        String delete = req.getParameter("delete");
        String[] ids=req.getParameterValues("id");
        StringBuilder sErrorMessage = new StringBuilder();
        IPEntry ipEntry = new IPEntry();
        String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;

        if (!merchants.isLoggedIn(session))
        {
            logger.debug("member is logout ");
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        RequestDispatcher rd = req.getRequestDispatcher("/ipwhitelist.jsp?ctoken=" + user.getCSRFToken());
        Hashtable memberHash = null;
        Hashtable recordHash = null;
        Hashtable tempHash = null;
        String msg = "";
        String startIp = "";
        String error = "";
        String EOL = "<BR>";
        int pageno = 1;
        int records = 15;

        boolean isIPv4 = false;
        boolean isIPv6 = false;

        error = error + validateOptionalParameter(req);
        if (error.length() > 0)
        {
            req.setAttribute("error", error);
            rd.forward(req, res);
            return;
        }
        if (!functions.isValueNull(memberId))
        {
            sErrorMessage.append(IpWhiteList_please_errormsg);
            req.setAttribute("error", sErrorMessage.toString());

            rd.forward(req, res);
            return;
        }
        try
        {
            pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("SPageno", req.getParameter("SPageno"), "Numbers", 5, true), 1);
            records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("SRecords", req.getParameter("SRecords"), "Numbers", 5, true), 15);
        }
        catch (ValidationException e)
        {
            logger.error("Invalid page no or records", e);
            pageno = 1;
            records = 15;
        }
        paginationVO.setInputs("memberid=" + memberId + "&type=" + type + "&ipAddress=" + ipAddress);
        paginationVO.setPageNo(pageno);
        paginationVO.setRecordsPerPage(records);

        try
        {

                if(functions.isValueNull(ipAddress))
                {
                    error=validateOptionalParameter(req);
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
                        sErrorMessage.append(IpWhiteList_enter_errormsg+ type + IpWhiteList_type_errormsg);
                        req.setAttribute("error", sErrorMessage.toString());
                        rd.forward(req,res);
                        return;
                    }
                }
                memberHash = ipEntry.retrievIPForMerchant1(memberId, ipAddress,type,paginationVO,actionExecutorId,actionExecutorName,true);
                if("upload".equalsIgnoreCase(action))
                {
                    if (!functions.isValueNull(ipAddress))
                    {
                        sErrorMessage.append(IpWhiteList_enter_errormsg + type + IpWhiteList_type_errormsg);
                        req.setAttribute("error", sErrorMessage.toString());
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
                        sErrorMessage.append(IpWhiteList_select_errormsg);
                        req.setAttribute("error", sErrorMessage.toString());
                        rd.forward(req, res);
                        return;
                    }
                    else if (isDuplicateIpFound)
                    {
                        msg = IpWhiteList_already_errormsg;
                        req.setAttribute("msg", msg);
                        rd.forward(req, res);
                        return;
                    }
                    else
                    {
                        ipEntry.insertIPForMerchant(memberId, req.getParameter("ipAddress"),actionExecutorId,actionExecutorName,type);
                        msg =IpWhiteList_uploaded_errormsg;
                        req.setAttribute("msg", msg);
                    }
                    memberHash = ipEntry.retrievIPForMerchant1(memberId, ipAddress, type, paginationVO,actionExecutorId,actionExecutorName, true);
                }

                if("delete".equalsIgnoreCase(delete))
                {
                    for(String id : ids)
                    {
                        ipEntry.deleteIPForMerchant1(id);
                    }
                    msg = IpWhiteList_deleted_errormsg;
                    memberHash = ipEntry.retrievIPForMerchant1(memberId, "",type,paginationVO,actionExecutorId,actionExecutorName,true);
                }
        }

        catch (Exception e)
        {
            logger.debug("Exception::::" + e);
        }
        req.setAttribute("recordHash", memberHash);
        req.setAttribute("memberId", memberId);
        req.setAttribute("paginationVO", paginationVO);
        req.setAttribute("error", error);
        req.setAttribute("error",sErrorMessage.toString());
        req.setAttribute("msg", msg);
        rd.forward(req, res);
    }

    private boolean isIPv4(String ipAddress)
    {
        boolean status = false;
        int count = 1;
        char c;
        for (int i = 1; i < ipAddress.length(); i++)
        {
            c = ipAddress.charAt(i);
            if (c == '.')
            {
                count++;
            }
        }
        if (count == 4)
        {
            status = true;
        }
        return status;
    }

    private boolean isIPv6(String ipAddress)
    {
        boolean status = false;
        int count = 1;
        char c;
        for (int i = 1; i < ipAddress.length(); i++)
        {
            c = ipAddress.charAt(i);
            if (c == ':')
            {
                count++;
            }
        }
        if (count == 8)
        {
            status = true;
        }
        return status;
    }

    private String validateOptionalParameter(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.MEMBERID);
        inputFieldsListOptional.add(InputFields.IPADDRESS);
        inputFieldsListOptional.add(InputFields.PAGENO);
        inputFieldsListOptional.add(InputFields.RECORDS);

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