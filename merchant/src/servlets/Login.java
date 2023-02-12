import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.manager.MerchantModuleManager;
import com.manager.dao.MerchantDAO;
import com.manager.OTPValidationManager;
import com.manager.dao.PartnerDAO;
import com.manager.enums.ActivityLogParameters;
import com.manager.vo.ActivityTrackerVOs;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.PartnerDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.AuthenticationLoginException;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Hashtable;

public class Login extends HttpServlet
{
    private static Logger log = new Logger(Login.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        HttpSession session = Functions.getNewSession(request);
        PartnerDAO partnerDAO = new PartnerDAO();
        User user =  (User)session.getAttribute("Anonymous");
        String logo=(String)session.getAttribute("logo");
        String icon=(String)session.getAttribute("icon");
        String company=(String)session.getAttribute("company");
        String partnerid=(String)session.getAttribute("partnerid");
        log.error("PARTNER ID FROM LOGIN "+partnerid);
        PartnerDetailsVO partnerDetailsVOMain = null;
        Functions functions= new Functions();

        String hostname=(String)session.getAttribute("hostname");
        String defaulttheme=request.getParameter("defaultTheme");
        String currenttheme=request.getParameter("currentTheme");
        String Sessioncolor=(String)session.getAttribute("colorPallet");
        String lanuage_property=request.getParameter("languageproperty");


        if(Functions.validateCSRFAnonymos(request.getParameter("ctoken"),user))
        {

            session.setAttribute("Anonymous",user);

        }
        else
        {     log.debug("session out");
            response.sendRedirect("/merchant/sessionout.jsp");
            ESAPI.httpUtilities().setNoCacheHeaders( response );


            return;
        }

        log.debug("successful");


        String username=null;
        String password=null;
        String isVirtual = "null";

        /** Decrypts the password **/

        if(request.getParameter("password")!=null )
        {
            password = request.getParameter("password");
            /*password = AesCtr.decrypt(request.getParameter("password"), request.getParameter("ctoken"), 256);*/
            request.setAttribute("password",password);


        }


        try
        {
            username = ESAPI.validator().getValidInput("username", request.getParameter("username"), "Login", 100, false);
        }
        catch(ValidationException e)
        {
            log.error("Given Value is not valid ",e);
            //response.sendRedirect("/merchant/index.jsp?action=F&ctoken="+user.getCSRFToken());
            RequestDispatcher rd = request.getRequestDispatcher("/index.jsp?action=E&ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            ESAPI.httpUtilities().setNoCacheHeaders( response );
            return;
        }
        try
        {
            password =ESAPI.validator().getValidInput("password",password,"Password",25,false);
        }
        catch(ValidationException e)
        {
            log.error("Given Value is not valid ",e);
            //response.sendRedirect("/merchant/index.jsp?action=F&ctoken="+user.getCSRFToken());
            RequestDispatcher rd = request.getRequestDispatcher("/index.jsp?action=E&ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            ESAPI.httpUtilities().setNoCacheHeaders( response );
            return;
        }

        try
        {
            isVirtual =ESAPI.validator().getValidInput("isVirtual",request.getParameter("isVirtual"),"SafeString",1,true);
            //System.out.println("is virtual---"+isVirtual);
        }
        catch(ValidationException e)
        {
            log.error("Given Value is not valid ",e);
            //response.sendRedirect("/merchant/index.jsp?action=F&ctoken="+user.getCSRFToken());
            RequestDispatcher rd = request.getRequestDispatcher("/virtualLogin.jsp?action=E&ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            ESAPI.httpUtilities().setNoCacheHeaders( response );
            return;
        }


        String redirectpage = null;
        Member member = null;

        response.setContentType("text/html");

        PrintWriter out = response.getWriter();



        String temp = (String) session.getAttribute("valid");
        ActivityTrackerVOs activityTrackerVOs = new ActivityTrackerVOs();
        String Login=username;
        //String actionExecutorId=(String)session.getAttribute("merchantid");
        if (temp == null)
        {
            log.debug("not a valid user...");
            redirectpage = "/index.jsp?action=F&ctoken="+user.getCSRFToken();
            RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
            rd.forward(request, response);
            ESAPI.httpUtilities().setNoCacheHeaders( response );
            return;
        }
        else
            log.debug("valid user."+username);

        log.debug("login page partnerid"+partnerid);
        try
        {

            /* log.debug(member.authenticate);

     if (member.authenticate.equals("true") || member.authenticate.equals("forgot"))
     {
         int merchantid = member.memberid;


         log.debug("Memeber activation detail :"+member.activation);
         log.debug("Memeber authenticate detail :"+member.authenticate);*/
            ESAPI.httpUtilities().setCurrentHTTP(request, response);
            session.setAttribute("language_property", lanuage_property);
            Merchants merchants=new Merchants();
            session.setAttribute("language_property", lanuage_property);
            String userId = "";
            try
            {
                session.invalidate();

                member = merchants.authenticate(username, partnerid,request);
                log.debug("get member user:::::"+member.getMemberUser());

                if(member.getMemberUser()!=null)
                {
                    request.setAttribute("role","submerchant");
                    userId = String.valueOf(member.getMemberUser().getUserId());
                }
                user = ESAPI.authenticator().login(request, response);

                try
                {
                    log.error("Merchant login successful ");
                    String remoteAddr = Functions.getIpAddress(request);
                    log.error("remoteAddr::: " + remoteAddr);
                    String ipcountry= functions.getIPCountryLong(remoteAddr);
                    log.error("ipcountry in success case :: " + ipcountry);
                    int serverPort = request.getServerPort();
                    String servletPath = request.getServletPath();
                    String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;
                    activityTrackerVOs.setInterface(ActivityLogParameters.MERCHANT.toString());
                    activityTrackerVOs.setUser_name(Login + "-" + member.memberid);
                    log.error("UserName set from Login.java----- "+activityTrackerVOs.getUser_name());
                    if(functions.isValueNull(String.valueOf(request.getAttribute("role"))) && (String.valueOf(request.getAttribute("role"))).equalsIgnoreCase("submerchant"))
                    {
                        activityTrackerVOs.setRole(ActivityLogParameters.SUBMERCHANT.toString());
                    }
                    else
                    {
                        activityTrackerVOs.setRole(ActivityLogParameters.MERCHANT.toString());
                    }
                    activityTrackerVOs.setAction(ActivityLogParameters.LOGIN.toString());
                    activityTrackerVOs.setModule_name(ActivityLogParameters.LOGIN_MERCHANT.toString());
                    activityTrackerVOs.setLable_values("Username=" + username+" ,Ip address="+remoteAddr+",Ip Country= "+ipcountry);
                    if (functions.isValueNull(member.activation) && "N".equalsIgnoreCase(member.activation))
                        activityTrackerVOs.setDescription(ActivityLogParameters.LOGIN_DISABLE.toString() + "-" + username);
                    else
                        activityTrackerVOs.setDescription(ActivityLogParameters.LOGIN_SUCCESS.toString() + "-" + username);
                    activityTrackerVOs.setIp(remoteAddr);
                    activityTrackerVOs.setHeader(header);
                    activityTrackerVOs.setPartnerId(partnerid);
                    try
                    {
                        AsyncActivityTracker asyncActivityTracker = AsyncActivityTracker.getInstance();
                        asyncActivityTracker.asyncActivity(activityTrackerVOs);
                    }
                    catch (Exception ex)
                    {
                        log.error("Exception while AsyncActivityLog::::", ex);
                    }
                }

                catch (Exception ex)
                {
                    log.error("Merchant login throwing Authentication Exception ", ex);
                }
            }
            catch (Exception e)
            {

                session = request.getSession(true);

                session.setAttribute("valid", "true");

                session.setAttribute("username",username);
                session.setAttribute("Anonymous",user);
                session.setAttribute("logo",logo);
                session.setAttribute("icon",icon);
                session.setAttribute("company",company);
                session.setAttribute("hostname",hostname);
                session.setAttribute("partnerid",partnerid);
                session.setAttribute("currenttheme",currenttheme);
                session.setAttribute("defaulttheme",defaulttheme);
                session.setAttribute("colorPallet",Sessioncolor);
                session.setAttribute("language_property", lanuage_property);
                session.setAttribute("favicon", request.getParameter("favicon"));

                log.error("error in catch login",e);
                String mes = "";
                if (e.getMessage().toLowerCase().contains("disabled"))
                {
                    mes = "D";
                    redirectpage = "/index.jsp?action=" + mes + "&ctoken=" + user.getCSRFToken();
                    RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
                    rd.forward(request, response);
                    ESAPI.httpUtilities().setNoCacheHeaders( response );
                    return;
                }
                else if (e.getMessage().toLowerCase().contains("locked"))
                {
                    mes = "L";
                    redirectpage = "/index.jsp?action=" + mes + "&ctoken=" + user.getCSRFToken();
                    RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
                    rd.forward(request, response);
                    ESAPI.httpUtilities().setNoCacheHeaders( response );
                    return;
                }
                else if(e.getMessage().toLowerCase().contains("denied"))
                {
                    mes="AD";
                    redirectpage = "/index.jsp?action=" + mes + "&ctoken=" + user.getCSRFToken();
                    RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
                    rd.forward(request, response);
                    ESAPI.httpUtilities().setNoCacheHeaders( response );
                    return;
                }
                else if(e.getMessage().toLowerCase().contains("whitelisted"))
                {
                    mes="IP";
                    redirectpage = "/index.jsp?action=" + mes + "&ctoken=" + user.getCSRFToken();
                    RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
                    rd.forward(request, response);
                    ESAPI.httpUtilities().setNoCacheHeaders( response );
                    return;
                }
                else if(e.getMessage().toLowerCase().contains("unauthorized"))
                {
                    mes="A";
                    redirectpage = "/index.jsp?action=" + mes + "&ctoken=" + user.getCSRFToken();
                    RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
                    rd.forward(request, response);
                    ESAPI.httpUtilities().setNoCacheHeaders( response );
                    return;
                }
                else if (member.partnerid != null && !member.partnerid.equalsIgnoreCase(partnerid))
                {
                    redirectpage = "/index.jsp?action=F&ctoken=" + user.getCSRFToken();
                    RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
                    rd.forward(request, response);
                    ESAPI.httpUtilities().setNoCacheHeaders( response );
                    return;
                }
                else
                {
                    try
                    {
                        log.error("Merchant login failed ");
                         activityTrackerVOs= new ActivityTrackerVOs();
                        log.error("REQUEST Fail case :: " + request);

                        String remoteAddr = Functions.getIpAddress(request);
                        String ipcountry= functions.getIPCountryLong(remoteAddr);
                        log.error("ipcountry in fail case :: " + ipcountry);
                        int serverPort = request.getServerPort();
                        String servletPath = request.getServletPath();
                        String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;
                        activityTrackerVOs.setInterface(ActivityLogParameters.MERCHANT.toString());
                        activityTrackerVOs.setUser_name(Login + "-" + member.memberid);
                        log.error("UserName set from Login.java----- "+activityTrackerVOs.getUser_name());

                        if(functions.isValueNull(String.valueOf(request.getAttribute("role"))) && (String.valueOf(request.getAttribute("role"))).equalsIgnoreCase("submerchant"))
                        {
                            activityTrackerVOs.setRole(ActivityLogParameters.SUBMERCHANT.toString());

                        }
                        else
                        {
                            activityTrackerVOs.setRole(ActivityLogParameters.MERCHANT.toString());

                        }                        activityTrackerVOs.setAction(ActivityLogParameters.LOGIN.toString());
                        activityTrackerVOs.setModule_name(ActivityLogParameters.LOGIN_MERCHANT.toString());
                        activityTrackerVOs.setLable_values("Username= " + username+" ,Ip address="+remoteAddr+" ,Ip Country="+ipcountry);
                        activityTrackerVOs.setDescription(ActivityLogParameters.LOGIN_FAIL.toString() + "-" + username);
                        activityTrackerVOs.setIp(remoteAddr);
                        activityTrackerVOs.setHeader(header);
                        activityTrackerVOs.setPartnerId(partnerid);
                        try
                        {
                            AsyncActivityTracker asyncActivityTracker = AsyncActivityTracker.getInstance();
                            asyncActivityTracker.asyncActivity(activityTrackerVOs);
                        }
                        catch (Exception ex)
                        {
                            log.error("Exception while AsyncActivityLog::::", e);
                        }
                    }

                    catch (Exception ex)
                    {
                        log.error("Merchant login throwing Authentication Exception ", e);
                    }
                    throw new AuthenticationLoginException("LOGIN FAILED", "DUE TO INTERNAL ERRORS");
                }

            }
            int merchantid = member.memberid;
            String merchantId = String.valueOf(merchantid);
            //String uId = "";

            if(!request.getAttribute("role").equals("submerchant"))
            {
                userId = merchantId;
            }

            session = request.getSession();

            log.debug("user is valid display dashboard");


            if (member.activation.equalsIgnoreCase("Y") || member.activation.equalsIgnoreCase("T"))
            {
                redirectpage = "/servlet/DashBoard?ctoken=" + ESAPI.httpUtilities().getCSRFToken();

                MerchantModuleManager merchantModuleManager=new MerchantModuleManager();
                session.setAttribute("username", username);
                session.setAttribute("password", password);
                session.setAttribute("activation", member.activation);
                session.setAttribute("userid", userId);
                session.setAttribute("merchantid", merchantId);
                session.setAttribute("partnerid",member.partnerid);
                session.setAttribute("transactionentry", new TransactionEntry(merchantid));
                session.setAttribute("memberObj", member);
                session.setAttribute("partnerid",partnerid);
                session.setAttribute("logo",logo);
                session.setAttribute("icon",icon);
                session.setAttribute("company", company);
                session.setAttribute("moduleset",merchantModuleManager.getMerchantAccessModuleSet(userId));
                session.setAttribute("terminallist", merchants.getMappingAccount(userId, (String) request.getAttribute("role")));
                session.setAttribute("paybylinkterminallist",merchants.getPaybyLinkTerminals(userId, (String) request.getAttribute("role")));
                session.setAttribute("MerchantModuleAccessVO",merchants.getMerchantModulesAccessDetail(merchantId));
                session.setAttribute("currenttheme",currenttheme);
                session.setAttribute("defaulttheme",defaulttheme);
                log.debug("member.currentTheme from login:::" + member.currentTheme);
                log.debug("member.defaultTheme from login:::"+member.defaultTheme);
                session.setAttribute("favicon", request.getParameter("favicon"));

                session.setAttribute("isappmanageractivate",member.isApplicationManagerAccess);
                session.setAttribute("isspeedoptionactivate",member.isSpeedOptionAccess);
                session.setAttribute("iscardregistrationallowed",member.isCardRegistrationAllowed);
                session.setAttribute("is_rest_whitelisted",member.is_rest_whitelisted);
                session.setAttribute("isRecurring",member.isRecurringModuleAllowed);
                session.setAttribute("role",request.getAttribute("role"));
                session.setAttribute("colorPallet",Sessioncolor);
                session.setAttribute("multiCurrencySupport",member.multiCurrencySupport);
                session.setAttribute("language_property", lanuage_property);

                String accountId = member.getAccountId();
                log.info("user is Authenticated successful display dashboard1");
                if (accountId != null)
                {
                    GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
                    String currency = account.getCurrency();
                    String bank = account.getGateway().toUpperCase();
                    String displayName = account.getDisplayName();
                    session.setAttribute("currency", currency);
                    session.setAttribute("bank", bank);
                    session.setAttribute("displayname", displayName);
                }
                else
                {
                    session.setAttribute("currency", "USD");
                    session.setAttribute("bank", "SBM");
                    session.setAttribute("displayname", "order.pz.com");
                }
                log.info("valid user");

            }
            else
            {
                session = request.getSession(true);

                session.setAttribute("valid", "true");

                session.setAttribute("Anonymous",user);
                session.setAttribute("logo",logo);
                session.setAttribute("icon",icon);
                session.setAttribute("company",company);
                session.setAttribute("hostname",hostname);
                session.setAttribute("partnerid",partnerid);
                session.setAttribute("currenttheme",currenttheme);
                session.setAttribute("defaulttheme",defaulttheme);
                session.setAttribute("colorPallet",Sessioncolor);
                session.setAttribute("language_property", lanuage_property);
                session.setAttribute("favicon", request.getParameter("favicon"));


                log.debug("user not authenticated");
                request.setAttribute("action", "D");
                redirectpage = "/index.jsp?action=D&ctoken=" + user.getCSRFToken();


                log.debug("Merchant index page---"+redirectpage);
                RequestDispatcher rd=request.getRequestDispatcher(redirectpage);
                rd.forward(request,response);
                ESAPI.httpUtilities().setNoCacheHeaders( response );

            }

            if (isVirtual == null || !isVirtual.equals("T"))
            {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, -91);
                session.setAttribute("language_property", lanuage_property);
                if (user.getLastPasswordChangeTime().compareTo(cal.getTime()) == -1)
                {
                    log.debug("redirecting to change password page");
                    redirectpage = "/chngpwd.jsp?MES=UP";
                }

                if (member.authenticate.equals("forgot"))
                {
                    log.debug("redirecting to change password page");
                    redirectpage = "/chngpwd.jsp?MES=FP";
                }
            }


            log.debug("Merchant redirect page---"+redirectpage);
            RequestDispatcher rd = null;
            Hashtable hash = null;
            hash = merchants.getMemberDetails(merchantId);

            System.out.println("request.setAttribute(\"role\",\"submerchant\");"+request.getAttribute("role"));
            System.out.println("userid ++++"+userId);

            if(hash != null && hash.get("merchant_verify_otp").equals("Y"))
            {
                String phoneno = "";
                if(request.getAttribute("role") == "submerchant")
                {
                    phoneno = partnerDAO.getMerchantUserContactNumber(userId);
                }
                else
                {
                    phoneno = partnerDAO.getMemberContactNumber(merchantId);

                }
                //String phoneno =member.telno;
                if(functions.isValueNull(phoneno))
                {
                    phoneno = phoneno.replaceFirst("^0+(?!$)", "");
                    phoneno = phoneno.replace("-", "").replace("+", "");
                }
                log.error("phoneno++++" + phoneno);
                request.setAttribute("memberid", member.memberid);
                request.setAttribute("membername", member.login);
                request.setAttribute("mobileno",phoneno);
                request.setAttribute("securekey",member.secureKey);
                request.setAttribute("partnerid",member.partnerid);
                OTPValidationManager otp = new OTPValidationManager();
                CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
                GenericAddressDetailsVO genericAddressDetailsVO = new GenericAddressDetailsVO();
                MerchantDetailsVO genericMerchantDetailsVO = new MerchantDetailsVO();

                genericAddressDetailsVO.setPhone(phoneno);
                genericAddressDetailsVO.setEmail("");
                genericMerchantDetailsVO.setMemberId(merchantId);
                if(request.getAttribute("role") == "submerchant")
                {
                    genericMerchantDetailsVO.setTransactionID(userId + "_MU");
                    request.setAttribute("memberTransactionId",userId + "_MU");
                }
                else
                {
                    genericMerchantDetailsVO.setTransactionID(merchantId + "_M");
                    request.setAttribute("memberTransactionId",merchantid +"_M");
                }

                commonValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
                commonValidatorVO.setMerchantDetailsVO(genericMerchantDetailsVO);

                String otpGenerate ="";

                if(functions.isValueNull(phoneno))
                {
                    otpGenerate = otp.insertOtpForLoginMerchant(commonValidatorVO);
                }

                if(functions.isValueNull(otpGenerate) && otpGenerate.equals("success"))
                {
                    request.setAttribute("OtpMessage", "OTP generated Successfully");
                }
                else  if(functions.isValueNull(otpGenerate) && otpGenerate.equals("failed"))
                {
                    request.setAttribute("OtpMessage","OTP generated Failed.Please Contact Support");
                }
                else  if(functions.isValueNull(otpGenerate) && otpGenerate.equals("limitexceed"))
                {
                    request.setAttribute("OtpMessage","OTP Limit Exceed.Please retry login after an hour");
                }
                else if(functions.isValueNull(phoneno))
                {
                    request.setAttribute("OtpMessage","Kindly update Contact Number");
                }
                else {
                    request.setAttribute("OtpMessage","");
                }
                redirectpage = "/otpVerification.jsp";
                rd = request.getRequestDispatcher(redirectpage);
            rd.forward(request,response);
            ESAPI.httpUtilities().setNoCacheHeaders( response );
                return;
            }
            else
            {

                rd = request.getRequestDispatcher(redirectpage);
                rd.forward(request, response);
        }
            ESAPI.httpUtilities().setNoCacheHeaders( response );

        }
        catch(Exception e)
        {
            log.error(" User not authenticated redirect to login page",e);

            try
            {
                session.invalidate();

                session = request.getSession();

                session.setAttribute("Anonymous",user);


                if(isVirtual!=null && isVirtual.equals("T"))
                {
                    redirectpage = "/virtualLogin.jsp?action=F&ctoken="+user.getCSRFToken();
                }
                else
                {
                    redirectpage = "/index.jsp?action=F&ctoken="+user.getCSRFToken();
                }

                session.setAttribute("partnerid",partnerid);
                session.setAttribute("logo",logo);
                session.setAttribute("icon",icon);
                session.setAttribute("company",company);
                session.setAttribute("defaulttheme",defaulttheme);
                session.setAttribute("currenttheme", currenttheme);
                session.setAttribute("colorPallet", Sessioncolor);
                session.setAttribute("language_property", lanuage_property);
                session.setAttribute("favicon", request.getParameter("favicon"));

                RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
                rd.forward(request, response);
                ESAPI.httpUtilities().setNoCacheHeaders( response );

            }
            catch(Exception ee)
            {
                log.error(" exception redirecting ::",ee);
            }
        }
    }
}