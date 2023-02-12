import com.directi.pg.*;
import com.manager.WhiteListManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User:Mahima Rai.
 * Date:07/04/18
 * Time: 1:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class UploadSingleDetails extends HttpServlet
{
    private static Logger log = new Logger(UploadSingleDetails.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        StringBuffer sf = new StringBuffer();
        StringBuilder sErrorMessage = new StringBuilder();
        StringBuilder sSuccessMessage = new StringBuilder();
        Functions functions = new Functions();
        WhiteListManager whiteListManager = new WhiteListManager();
        MerchantDAO merchantDAO = new MerchantDAO();
        Connection conn=null;
        Date date = null;

        boolean isRecordAvailable = false;
        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;
        String memberId = "";
        String accountId ="";
        String emailAddress = "";
        String firstSix = "";
        String lastFour = "";
        String cardHolderName="";
        String ipAddress="";
        String EOL = "<br>";

        accountId = req.getParameter("accountid");
        memberId = req.getParameter("toid");
        firstSix = req.getParameter("firstsix");
        lastFour = req.getParameter("lastfour");
        emailAddress = req.getParameter("emailaddr");
        cardHolderName=req.getParameter("name");
        ipAddress=req.getParameter("ipAddress");
        String expiryDate = req.getParameter("expiryDate");
        log.error("firstSix--->"+firstSix);
        log.error("lastFour--->"+lastFour);

        try
        {
            log.error("req.getParameter(\"action\"):::" + req.getParameter("action"));
            if("getdata".equals(req.getParameter("action")) && functions.isValueNull(memberId))
            {
                try
                {
                    String whitelistlevel=whiteListManager.getWhitelistingLevel(memberId);
                    if(!functions.isValueNull(whitelistlevel)){
                        if(functions.isValueNull(memberId)){
                            boolean isPresent=merchantDAO.isMemberExist(memberId);
                            if(!isPresent){
                                log.debug("Merchant does not exist in the system");
                                sErrorMessage.append("Merchant does not exist in the system" + EOL);
                            }
                        }
                    }
                    req.setAttribute("whitelistlevel",whitelistlevel);
                    req.setAttribute("sErrorMessage", sErrorMessage.toString());
                    RequestDispatcher rd = req.getRequestDispatcher("/uploadsingledetail.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }
                catch (Exception e)
                {
                    log.error("Catch Exception..",e);
                }
            }
            else
            {
                String whiteListingLevel = req.getParameter("whitelistlevel");
                log.error("whiteListingLevel:::" + whiteListingLevel);
                if (functions.isValueNull(emailAddress))
                {

                    if (!ESAPI.validator().isValidInput("emailaddr", emailAddress, "Email", 50, false))
                    {
                        log.debug("Invalid email address");
                        sErrorMessage.append("Invalid Email or Email should not be empty" + EOL);
                    }
                }
                if (functions.hasHTMLTags(req.getParameter("pgtypeid")))
                {
                    sErrorMessage.append("Invalid Gateway" + EOL);
                }
                if (functions.isValueNull(firstSix) && functions.isValueNull(lastFour))
                {

                    if (!ESAPI.validator().isValidInput("firstsix", firstSix, "Numbers", 50, false))
                    {
                        log.debug("Invalid firstsix");
                        sErrorMessage.append("Invalid card number firstsix or firstsix should not be empty" + EOL);
                    }
                    if (!ESAPI.validator().isValidInput("lastfour", lastFour, "Numbers", 50, false))
                    {
                        log.debug("Invalid lastfour");
                        sErrorMessage.append("Invalid card number lastfour or lastfour should not be empty" + EOL);
                    }
                }
                if (!ESAPI.validator().isValidInput("toid", memberId, "Numbers", 50, false))
                {
                    log.debug("Invalid memberid");
                    sErrorMessage.append("Invalid memberID or memberID should not be empty" + EOL);
                }
                if (functions.isValueNull(whiteListingLevel) && whiteListingLevel.equals("Account"))
                {
                    if (!ESAPI.validator().isValidInput("accountid", accountId, "Numbers", 50, false) || functions.hasHTMLTags(accountId))
                    {
                        log.debug("Invalid accountid");
                        sErrorMessage.append("Invalid accountID or accountID should not be empty" + EOL);
                    }
                }
                else {
                    if(!ESAPI.validator().isValidInput("accountid", accountId, "Numbers", 50, true) || functions.hasHTMLTags(accountId))
                    {
                        log.debug("Invalid accountid");
                        sErrorMessage.append("Invalid accountID or accountID should not be empty" + EOL);
                    }
                }
                if (functions.isValueNull(cardHolderName))
                {
                    if (!ESAPI.validator().isValidInput("name", cardHolderName, "Description", 50, false))
                    {
                        log.debug("Invalid Card Holder Name.");
                        sErrorMessage.append("Invalid Card Holder Name." + EOL);
                    }
                }
                if (functions.isValueNull(ipAddress))
                {
                    if (!ESAPI.validator().isValidInput("ipAddress", ipAddress, "IPAddressNew", 50, false))
                    {
                        log.debug("Invalid IP Address.");
                        sErrorMessage.append("Invalid IP Address." + EOL);
                    }
                }
                if (functions.isValueNull(expiryDate))
                {
                    String input = expiryDate;
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/yy");
                    simpleDateFormat.setLenient(false);
                    Date expiry = simpleDateFormat.parse(input);
                    boolean expired = expiry.before(new Date());
                    if (expired == true)
                    {
                        sErrorMessage.append("Invalid Expiry Date." + EOL);
                    }
                }
                if(functions.isValueNull(memberId)){
                    boolean isPresent=merchantDAO.isMemberExist(memberId);
                    if(!isPresent){
                        log.debug("Merchant does not exist in the system");
                        sErrorMessage.append("Merchant does not exist in the system" + EOL);
                    }
                }
                log.error("sErrorMessage.length()--->"+sErrorMessage.length());
                if (sErrorMessage.length() > 0)
                {
                    String redirectpage = "/uploadsingledetail.jsp?ctoken=" + user.getCSRFToken();
                    req.setAttribute("sErrorMessage", sErrorMessage.toString());
                    RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
                    rd.forward(req, res);
                    return;
                }

                MerchantDetailsVO merchantDetailsVO = merchantDAO.getMemberDetails(memberId);
                String level = merchantDetailsVO.getCardWhitelistLevel();
                String companyName = merchantDetailsVO.getCompany_name();
                String encryptExpiryDate = "";


                if (functions.isValueNull(expiryDate))
                {
                    encryptExpiryDate = PzEncryptor.hashExpiryDate(expiryDate, memberId);
                }

                //GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
                String gateway = "";
                //gateway=account.getGateway();

                if ((functions.isValueNull(firstSix) && functions.isValueNull(lastFour)) || (functions.isValueNull(emailAddress)))
                {
                    if ("Member".equals(level))
                    {
                        isRecordAvailable = whiteListManager.isRecordAvailableInSystem(memberId,firstSix, lastFour, emailAddress);
                    }
                    else if ("Group".equals(level))
                    {
                        if (functions.isValueNull(emailAddress))
                        {
                            isRecordAvailable = whiteListManager.isRecordAvailableOnOtherGroup(firstSix, lastFour, emailAddress, gateway, companyName);
                        }
                        else
                        {
                            isRecordAvailable = whiteListManager.isRecordAvailableOnOtherGroup(firstSix, lastFour, gateway, companyName);
                        }
                    }
                    else
                    {
                        if (functions.isValueNull(emailAddress))
                        {
                            isRecordAvailable = whiteListManager.isRecordAvailableForMember(memberId, accountId, firstSix, lastFour, emailAddress, cardHolderName, ipAddress, encryptExpiryDate);
                        }
                        else
                        {
                            isRecordAvailable = whiteListManager.isRecordAvailableForMember(memberId, accountId, firstSix, lastFour, cardHolderName, ipAddress, encryptExpiryDate);
                        }
                    }
                    if (!isRecordAvailable)
                    {
                        boolean isRecordInserted = whiteListManager.addCard(firstSix, lastFour, emailAddress, accountId, memberId, cardHolderName, ipAddress, encryptExpiryDate, actionExecutorId, actionExecutorName);
                        sSuccessMessage.append("Records Updated Successfully");
                        req.setAttribute("success", "Records updated");
                    }
                    else
                    {
                        sErrorMessage.append("Records Already Available");
                        req.setAttribute("error", "not update");
                    }
                }
                else
                {
                    sErrorMessage.append("Please select the Card Details OR Email Address" + EOL);
                }
            }
        }
        catch (Exception e)
        {
            log.error("Catch Exception..",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());

        String redirectpage = "/uploadsingledetail.jsp?ctoken=" + user.getCSRFToken();
        req.setAttribute("sErrorMessage", sErrorMessage.toString());
        req.setAttribute("sSuccessMessage", chargeBackMessage.toString());
        RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
        rd.forward(req, res);
        return;
    }
}