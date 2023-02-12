import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.manager.PartnerManager;
import com.manager.TokenManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.PartnerDetailsVO;
import com.manager.vo.TokenRequestVO;
import com.manager.vo.TokenResponseVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.Mail.MailService;
import com.payment.common.core.CommCardDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;

//import org.joda.time.format.DateTimeFormat;
//import org.joda.time.format.DateTimeFormatter;
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.time.LocalDate;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: sandip
 * Date: 4/13/15
 * Time: 01:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class NewCardRegistration extends HttpServlet
{
    private static Logger logger = new Logger(NewCardRegistration.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        logger.debug("GET request received");
        doProcess(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        logger.debug("POST request received");
        doProcess(request, response);
    }

    public void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        Merchants merchants = new Merchants();
        HttpSession session = request.getSession();

        if (!merchants.isLoggedIn(session))
        {
            response.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        session.setAttribute("submit", "Register Card");

        TokenRequestVO tokenRequestVO = new TokenRequestVO();
        CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();
        Functions functions = new Functions();
        TokenManager tokenManager = new TokenManager();

        RequestDispatcher rd = request.getRequestDispatcher("/newcardregistration.jsp?ctoken=" + user.getCSRFToken());


        String toId = request.getParameter("toid");
        if (!ESAPI.validator().isValidInput("toid", toId, "Numbers", 20, false))
        {
            request.setAttribute("error", "Invalid User");
            rd.forward(request, response);
            return;
        }

        String cNum = "";
        String expiryMonth = "";
        String expiryYear = "";
        String cvv = "";
        String cardholderFirstName = "";
        String cardholderLastName = "";
        String language = "";
        String birthDate = "";
        String street = "";
        String city = "";
        String country = "";
        String state = "";
        String zip = "";
        String telNoCc = "";
        String telNo = "";
        String cardholderEmail = "";
        String cardholderId = "";
        String partnerId = "";
        String generatedBy ="";

        try
        {

            MerchantDAO merchantDAO = new MerchantDAO();
            MerchantDetailsVO merchantDetailsVO = null;
            try
            {
                merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                generatedBy=merchantDetailsVO.getLogin();
            }
            catch (PZDBViolationException e)
            {
                logger.error("PZDBViolationException::",e);
                request.setAttribute("error", "Internal error while processing your request");
                rd.forward(request, response);
                return;
            }

            PartnerManager partnerManager=new PartnerManager();
            PartnerDetailsVO partnerDetailsVO=null;

            try
            {
                partnerDetailsVO=partnerManager.getPartnerDetails(merchantDetailsVO.getPartnerId());
            }
            catch (Exception e)
            {
                logger.error("Exception::",e);
                request.setAttribute("error", "Internal error while processing your request");
                rd.forward(request, response);
                return;
            }

            if ("N".equals(partnerDetailsVO.getIsMerchantRequiredForCardRegistration()))
            {
                request.setAttribute("error", "Unable to register card on your account-(Card registration at PSP level)");
                logger.error("Unable to register card on your account-(Card registration is at PSP level)");
                rd.forward(request, response);
                return;

            }



            if ("N".equals(merchantDetailsVO.getIsTokenizationAllowed()))
            {
                request.setAttribute("error", "Card registration has been not activated to your account");
                rd.forward(request, response);
                return;
            }

            String errorMsg = "";
            TokenResponseVO tokenResponseVO = null;
            errorMsg = tokenManager.cardRegistrationDirectAPIMandatoryValidation(request);
            if (functions.isValueNull(errorMsg))
            {
                request.setAttribute("error", errorMsg);
                rd.forward(request, response);
                return;
            }
            else
            {
                cNum = request.getParameter("cardnumber");
                expiryMonth = request.getParameter("expirymonth");
                expiryYear = request.getParameter("expiryyear");
                cvv = request.getParameter("cvv");
                cardholderFirstName = request.getParameter("firstname");
                cardholderLastName = request.getParameter("lastname");
                partnerId = request.getParameter("partnerid");
                cardholderEmail = request.getParameter("emailaddr");
            }

            if ("N".equals(merchantDetailsVO.getIsAddressDetailsRequired()))
            {
                errorMsg = tokenManager.cardRegistrationDirectAPIConditionalValidation(request);
                if (functions.isValueNull(errorMsg))
                {
                    request.setAttribute("error", errorMsg);
                    rd.forward(request, response);
                    return;
                }
                else
                {
                    country = request.getParameter("countrycode");
                    city = request.getParameter("city");
                    state = request.getParameter("state");
                    street = request.getParameter("street");
                    zip = request.getParameter("zip");
                    telNoCc = request.getParameter("telnocc");
                    telNo = request.getParameter("telno");

                }
            }
            errorMsg = tokenManager.cardRegistrationDirectAPIOptionalValidation(request);
            if (functions.isValueNull(errorMsg))
            {
                request.setAttribute("error", errorMsg);
                rd.forward(request, response);
                return;
            }
            else
            {
                language = request.getParameter("language");
                cardholderId = request.getParameter("cardholderid");
            }

            errorMsg = tokenManager.cardRegistrationBackOfficeBirthDatePickerValidation(request);
            if (functions.isValueNull(errorMsg))
            {
                request.setAttribute("error", errorMsg);
                rd.forward(request, response);
                return;
            }
            else
            {
                birthDate = request.getParameter("bdate");

                if(functions.isValueNull(birthDate))
                {
                    String str[] = birthDate.split("/");
                    String day = str[0];
                    String month = str[1];
                    String year = str[2];
                    birthDate = year + month + day;
                    //System.out.println("inside======"+birthDate);
                }

            }
            if(functions.isValueNull(cardholderId) && !tokenManager.isCardholderRegistered(toId,cardholderId))
            {
                /*if(!tokenManager.isCardholderRegistered(toId,cardholderId))
                {*/
                    request.setAttribute("error", "Requested cardholder not found");
                    rd.forward(request, response);
                    return;

            }
            if (tokenManager.isNewCard(toId, cNum))
            {
                GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();

                commCardDetailsVO.setCardNum(cNum);
                commCardDetailsVO.setExpMonth(expiryMonth);
                commCardDetailsVO.setExpYear(expiryYear);
                commCardDetailsVO.setcVV(cvv);

                addressDetailsVO.setFirstname(cardholderFirstName);
                addressDetailsVO.setLastname(cardholderLastName);
                addressDetailsVO.setLanguage(language);
                addressDetailsVO.setBirthdate(birthDate);
                addressDetailsVO.setStreet(street);
                addressDetailsVO.setCity(city);
                addressDetailsVO.setState(state);
                addressDetailsVO.setZipCode(zip);
                addressDetailsVO.setCountry(country);
                addressDetailsVO.setTelnocc(telNoCc);
                addressDetailsVO.setPhone(telNo);
                addressDetailsVO.setEmail(cardholderEmail);

                tokenRequestVO.setMemberId(toId);
                tokenRequestVO.setPartnerId(partnerId);
                tokenRequestVO.setAddressDetailsVO(addressDetailsVO);
                tokenRequestVO.setCommCardDetailsVO(commCardDetailsVO);
                tokenRequestVO.setCardholderId(cardholderId);
                tokenRequestVO.setGeneratedBy(generatedBy);

                tokenResponseVO = tokenManager.createTokenNew(tokenRequestVO);

                if ("success".equals(tokenResponseVO.getStatus()))
                {
                    request.setAttribute("error", "Card has been successfully tokenized");
                    rd.forward(request, response);
                }
                else
                {
                    request.setAttribute("error", "Card tokenizing process has been failed");
                    rd.forward(request, response);
                }

                MailService mailService=new MailService();
                AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
                HashMap hashMap=new HashMap();

                hashMap.put(MailPlaceHolder.NAME, tokenRequestVO.getCommCardDetailsVO().getCardHolderFirstName());
                hashMap.put(MailPlaceHolder.CARDHOLDERNAME,cardholderFirstName+" "+cardholderLastName);
                hashMap.put(MailPlaceHolder.CustomerEmail, cardholderEmail);
                hashMap.put(MailPlaceHolder.MULTIPALTRANSACTION,mailService.getDetailTableForCardDetails(tokenResponseVO.getTokenDetailsVO()));
                hashMap.put(MailPlaceHolder.TOID, merchantDetailsVO.getMemberId());
                asynchronousMailService.sendMerchantSignup(MailEventEnum.MERCHANTS_LEVEL_CARD_REGISTRATION, hashMap);

            }
            else
            {
                request.setAttribute("error", "Card already tokenized");
                rd.forward(request, response);
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZDBViolationException::",e);
            request.setAttribute("error", "Internal error while processing your request");
            rd.forward(request, response);
        }
    }
}
