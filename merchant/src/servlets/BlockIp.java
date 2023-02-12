import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.manager.BlacklistManager;
import com.manager.vo.BlacklistVO;
import com.manager.vo.PaginationVO;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sanjay Yadav on 28-Nov-18.
 */
public class BlockIp extends HttpServlet
{
    private static Logger log = new Logger(BlockIp.class.getName());

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
        PaginationVO paginationVO = new PaginationVO();

        if (!merchants.isLoggedIn(session))
        {
            log.debug("member is logout ");
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        String error = "";
        String sMessage = "";
        String ip = "";
        int count = 0;
        String memberId = "";
        String selectIpVersion = "";
        String ipStartRange = "";
        String ipEndRange = "";
        String EOL = "<br>";
        String ipv6Start = "";
        String ipv6End = "";
        String id = "";
        String AllIp = "";

        StringBuilder sErrorMessage = new StringBuilder();
        BlacklistManager blacklistManager = new BlacklistManager();
        StringBuilder sSuccessMessage = new StringBuilder();

        boolean isValid = true;
        Connection conn = null;
        List<BlacklistVO> listOfIp = null;
        ArrayList<BlacklistVO> listOfIpblock = null;
        String redirectpage = null;
        RequestDispatcher rd = req.getRequestDispatcher("/blockiplist.jsp?ctoken=" + user.getCSRFToken());
        StringBuilder chargeBackMessage = null;
        selectIpVersion = req.getParameter("selectIpVersion");
        boolean isIPv4 = false;
        boolean isIPv6 = false;

        paginationVO.setInputs("toid=" + memberId + "&ipstartRange=" + ipStartRange + "&selectIpVersion=" + selectIpVersion);
        paginationVO.setPageNo(Functions.convertStringtoInt(req.getParameter("SPageno"), 1));
        paginationVO.setPage(BlockIp.class.getName());
        paginationVO.setCurrentBlock(Functions.convertStringtoInt(req.getParameter("currentblock"), 1));
        paginationVO.setRecordsPerPage(Functions.convertStringtoInt(req.getParameter("SRecords"), 15));
        error = error + validateOptionalParameter(req);
        try
        {
            memberId = (String) session.getAttribute("merchantid");
            AllIp = req.getParameter("IPAddress");
            isValid = false;
            String msg = "";

            try
            {
                error = validateOptionalParameter(req);
                if (!error.equals(""))
                {
                    req.setAttribute("error", error);
                    rd.forward(req, res);
                    return;
                }
                if (functions.isValueNull(AllIp))
                {

                    isIPv4 = isIPv4(req.getParameter("IPAddress"));
                    isIPv6 = isIPv6(req.getParameter("IPAddress"));
                    if (functions.isValueNull(AllIp)&& (isIPv4 == false) && (isIPv6 == false)&& selectIpVersion.equals(""))
                    {
                        sErrorMessage.append("Invalid IPAddress");
                    }
                    else if (isIPv4 == true && isIPv6 == false && selectIpVersion.equals("IPv4") && functions.isValueNull(req.getParameter("IPAddress")))
                    {
                        selectIpVersion = "IPv4";
                        AllIp = req.getParameter("IPAddress");
                    }
                    else if (isIPv6 == true && isIPv4 == false && selectIpVersion.equals("IPv6") && functions.isValueNull(req.getParameter("IPAddress")))
                    {
                        selectIpVersion = "IPv6";
                        AllIp = req.getParameter("IPAddress");
                    }
                    else if (selectIpVersion.equals("") && !functions.isValueNull(AllIp))
                    {
                        listOfIp = blacklistManager.getBlockedip(memberId, AllIp, selectIpVersion, paginationVO);
                    }
                    else if (selectIpVersion.equals("")&& functions.isValueNull(AllIp))
                    {
                        listOfIp = blacklistManager.getBlockedip(memberId, AllIp, selectIpVersion, paginationVO);
                    }
                    else
                    {
                        sErrorMessage.append("Invalid IPAddress");
                        log.error("Invalid IPAddress");
                    }


                }
            }
            catch (Exception e)
            {
                log.error("Invalisd IPAddress" + e);
            }
            if (functions.isValueNull(selectIpVersion) || functions.isValueNull(memberId) || functions.isValueNull(AllIp))
            {
                listOfIp = blacklistManager.getBlockedAllIP(AllIp, memberId, selectIpVersion, paginationVO);
            }

            if (sErrorMessage.length() > 0)
            {
                redirectpage = "/blockiplist.jsp?ctoken=" + user.getCSRFToken();
                req.setAttribute("error", sErrorMessage.toString());
                rd = req.getRequestDispatcher(redirectpage);
                req.setAttribute("toid", memberId);
                rd.forward(req, res);
                return;
            }
        }

        catch (Exception e)

        {
            log.error("Error in searching of data from blacklist of Ipv4 and Ipv6", e);
        }

        finally

        {
            Database.closeConnection(conn);
        }

        chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());
        redirectpage = "/blockiplist.jsp?ctoken=" + user.getCSRFToken();
        req.setAttribute("msg", sMessage);
        req.setAttribute("listOfIp", listOfIp);
        req.setAttribute("selectIpVersion", selectIpVersion);
        req.setAttribute("paginationVO", paginationVO);
        req.setAttribute("error", error);
        rd = req.getRequestDispatcher(redirectpage);
        rd.forward(req, res);

    }

    private boolean isIPv4(String AllIp)
    {
        boolean status = false;
        int count = 1;
        char c;
        for (int i = 1; i < AllIp.length(); i++)
        {
            c = AllIp.charAt(i);
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

    private boolean isIPv6(String AllIp)
    {
        boolean status = false;
        int count = 1;
        char c;
        for (int i = 1; i < AllIp.length(); i++)
        {
            c = AllIp.charAt(i);
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
}
