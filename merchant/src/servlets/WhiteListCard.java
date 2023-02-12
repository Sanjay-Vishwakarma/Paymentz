import com.directi.pg.*;
import com.manager.dao.WhiteListDAO;
import com.manager.vo.PaginationVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.apache.commons.lang3.StringUtils;
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
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Mahima on 1/9/2016.
 */
public class WhiteListCard extends HttpServlet
{
    private static Logger log = new Logger(WhiteListCard.class.getName());

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
        String WhiteListCard_accountid_errormsg = StringUtils.isNotEmpty(rb1.getString("WhiteListCard_accountid_errormsg"))?rb1.getString("WhiteListCard_accountid_errormsg"): "Invalid accountID or accountID should not be empty";
        String WhiteListCard_memberid_errormsg = StringUtils.isNotEmpty(rb1.getString("WhiteListCard_memberid_errormsg"))?rb1.getString("WhiteListCard_memberid_errormsg"): "MemberID/AccountID NOT mapped.";
        String WhiteListCard_cardnumber_errormsg = StringUtils.isNotEmpty(rb1.getString("WhiteListCard_cardnumber_errormsg"))?rb1.getString("WhiteListCard_cardnumber_errormsg"): "Invalid accountID or accountID should not be empty";
        String WhiteListCard_last_errormsg = StringUtils.isNotEmpty(rb1.getString("WhiteListCard_last_errormsg"))?rb1.getString("WhiteListCard_last_errormsg"): "Invalid card number lastfour or lastfour should not be empty";
        String WhiteListCard_invalid_errormsg = StringUtils.isNotEmpty(rb1.getString("WhiteListCard_invalid_errormsg"))?rb1.getString("WhiteListCard_invalid_errormsg"): "Invalid Email";
        String WhiteListCard_cardholder_errormsg = StringUtils.isNotEmpty(rb1.getString("WhiteListCard_cardholder_errormsg"))?rb1.getString("WhiteListCard_cardholder_errormsg"): "Invalid Card Holder Name.";
        String WhiteListCard_ipaddress_errormsg = StringUtils.isNotEmpty(rb1.getString("WhiteListCard_ipaddress_errormsg"))?rb1.getString("WhiteListCard_ipaddress_errormsg"): "Invalid IP Address.";
        String WhiteListCard_expirydate_errormsg = StringUtils.isNotEmpty(rb1.getString("WhiteListCard_expirydate_errormsg"))?rb1.getString("WhiteListCard_expirydate_errormsg"): "Invalid Expiry Date.";
        String WhiteListCard_records_errormsg = StringUtils.isNotEmpty(rb1.getString("WhiteListCard_records_errormsg"))?rb1.getString("WhiteListCard_records_errormsg"): "Records Uploaded Successfully.";
        String WhiteListCard_already_errormsg = StringUtils.isNotEmpty(rb1.getString("WhiteListCard_already_errormsg"))?rb1.getString("WhiteListCard_already_errormsg"): "Records already Uploaded.";
        String WhiteListCard_deleted_errormsg = StringUtils.isNotEmpty(rb1.getString("WhiteListCard_deleted_errormsg"))?rb1.getString("WhiteListCard_deleted_errormsg"): "Records deleted successfully.";


        StringBuffer stringBuffer = new StringBuffer();
        WhiteListDAO whiteListDAO = new WhiteListDAO();
        PaginationVO paginationVO = new PaginationVO();
        List<WhitelistingDetailsVO> listOfCard = null;
        Connection conn = null;

        if (!merchants.isLoggedIn(session))
        {
            log.debug("member is logout ");
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;

        String memberid = (String) session.getAttribute("merchantid");
        String action = req.getParameter("upload");
        String msg ="";
        String accountID = req.getParameter("accountid"); // Added new column.
        String firstSix = req.getParameter("firstsix");
        String lastFour = req.getParameter("lastfour");
        String emailAddress = req.getParameter("emailAddr");
        String name = req.getParameter("name");
        String ipAddress = req.getParameter("ipAddress");
        String expiryDate = req.getParameter("expiryDate"); //Added new column.
        String encryptExpiryDate = "";
        String delete = req.getParameter("delete");
        String error = "";
        String EOL = "<br>";
        boolean isRecordAvailable = false;
        String isTemp = "";
        boolean isValid = true;
        if ("upload".equalsIgnoreCase(action))
            isValid = false;
        try
        {
            error = error + validateOptionalParameters(req);
            if (functions.isValueNull(error))
            {
                req.setAttribute("error", error);
                RequestDispatcher rd = req.getRequestDispatcher("/whitelist.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
            }

           /* if (!ESAPI.validator().isValidInput("accountid", accountID, "Numbers", 50, isValid))
            {
                log.debug("Invalid AccountId");
                stringBuffer.append(WhiteListCard_accountid_errormsg + EOL);
            }
            else if (functions.isValueNull(memberid) && functions.isValueNull(accountID))
            {
                boolean valid = whiteListDAO.getMemberidAccountid(memberid, accountID);
                if (valid == false)
                {
                    stringBuffer.append(WhiteListCard_memberid_errormsg);
                }
            }*/

            if (!ESAPI.validator().isValidInput("firstsix", firstSix, "FirstSixcc", 6, isValid))
            {
                log.debug("Invalid firstsix");
                stringBuffer.append(WhiteListCard_cardnumber_errormsg + EOL);
            }
            if (!ESAPI.validator().isValidInput("lastfour", lastFour, "LastFourcc", 4, isValid))
            {
                log.debug("Invalid lastfour");
                stringBuffer.append(WhiteListCard_last_errormsg + EOL);
            }
            if (functions.isValueNull(emailAddress))
            {
                if (!ESAPI.validator().isValidInput("emailaddr", emailAddress, "Email", 50, isValid))
                {
                    log.debug("Invalid email address");
                    stringBuffer.append(WhiteListCard_invalid_errormsg + EOL);
                }
            }
            if (functions.isValueNull(name))
            {
                if (!ESAPI.validator().isValidInput("name", name, "SafeString", 50, false) || functions.hasHTMLTags(name))
                {
                    log.debug("Invalid Card Holder Name.");
                    stringBuffer.append(WhiteListCard_cardholder_errormsg + EOL);
                }
            }
            if (functions.isValueNull(ipAddress))
            {
                if (!ESAPI.validator().isValidInput("ipAddress", ipAddress, "Numbers", 50, false))
                {
                    log.debug("Invalid IP Address.");
                    stringBuffer.append(WhiteListCard_ipaddress_errormsg + EOL);
                }
            }
            if (functions.isValueNull(expiryDate))
            {
                try
                {
                    String input = expiryDate;
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/yy");
                    simpleDateFormat.setLenient(false);
                    Date expiry = simpleDateFormat.parse(input);
                    boolean expired = expiry.before(new Date());
                    if (expired == true)
                    {
                        stringBuffer.append(WhiteListCard_expirydate_errormsg + EOL);
                    }
                    encryptExpiryDate = PzEncryptor.hashExpiryDate(expiryDate, memberid);

                }
                catch (Exception e)
                {
                    log.error("Exception In WhiteListCard ::::::::: ");
                    stringBuffer.append(WhiteListCard_expirydate_errormsg + EOL);
                }
            }


            if (stringBuffer.length() > 0)
            {
                req.setAttribute("error", stringBuffer.toString());
                RequestDispatcher rd = req.getRequestDispatcher("/whitelist.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
                return;
            }

            paginationVO.setPageNo(Functions.convertStringtoInt(req.getParameter("SPageno"), 1));
            paginationVO.setPage(WhiteListCard.class.getName());
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(req.getParameter("currentblock"), 1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(req.getParameter("SRecords"), 15));
            paginationVO.setInputs("&firstsix=" + firstSix + "&lastfour=" + lastFour + "&emailAddr=" + emailAddress + "&name=" + name + "&ipAddress=" + ipAddress + "&accountid=" + accountID);

            listOfCard = whiteListDAO.getWhiteListCardForMerchant(firstSix, lastFour, emailAddress, name, ipAddress, memberid, paginationVO,accountID);
            if ("upload".equalsIgnoreCase(action))
            {
                if (functions.isValueNull(emailAddress))
                {
                    isTemp = whiteListDAO.isRecordAvailableForPerMember(memberid, accountID, firstSix, lastFour, emailAddress, "", "", "");
                }
                else
                {
                    isTemp = whiteListDAO.isRecordAvailableForPerMember(memberid, accountID, firstSix, lastFour, "", "", "");
                }
                if (!functions.isValueNull(isTemp))
                {
                    whiteListDAO.addCard(firstSix, lastFour, emailAddress, accountID, memberid, name, ipAddress, encryptExpiryDate,actionExecutorId,actionExecutorName);
                    msg= WhiteListCard_records_errormsg;
                }
                else if("Y".equalsIgnoreCase(isTemp))
                {
                    whiteListDAO.updateCard(firstSix, lastFour, emailAddress, accountID, memberid, name, ipAddress, encryptExpiryDate,actionExecutorId,actionExecutorName);
                    msg= WhiteListCard_records_errormsg;
                }
                else
                {
                    msg = WhiteListCard_already_errormsg;
                }
                listOfCard = whiteListDAO.getWhiteListCardForMerchant(firstSix, lastFour, emailAddress, name, ipAddress, memberid, paginationVO,accountID);
            }
            String[] ids = req.getParameterValues("id");
            if (functions.isValueNull(delete))
            {
                for (String id : ids)
                {
                    whiteListDAO.updateCardEmailEntry(id);
                }
                msg = WhiteListCard_deleted_errormsg;
                listOfCard = whiteListDAO.getWhiteListCardForMerchant(firstSix, lastFour, emailAddress, name, ipAddress, memberid, paginationVO,accountID);
            }
        }
        catch (PZDBViolationException e)
        {
            log.debug("Exception::::" + e);
        }
        finally
        {
            Database.closeConnection(conn);
        }

        req.setAttribute("error", error);
        req.setAttribute("error", stringBuffer.toString());
        req.setAttribute("msg", msg);
        req.setAttribute("merchantid", memberid);
        req.setAttribute("listofcard", listOfCard);
        req.setAttribute("paginationVO", paginationVO);
        RequestDispatcher rd = req.getRequestDispatcher("/whitelist.jsp?ctoken=" + user.getCSRFToken());
        rd.forward(req, res);
        return;
    }

    private String validateOptionalParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
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
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error + errorList.getError(inputFields.toString()).getMessage() + EOL;
                }
            }
        }
        return error;
    }
}
