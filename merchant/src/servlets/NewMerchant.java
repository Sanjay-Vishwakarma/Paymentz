import com.directi.pg.*;
import com.invoice.dao.InvoiceEntry;
import com.logicboxes.util.ApplicationProperties;
import com.manager.PartnerManager;
import com.manager.vo.PartnerDefaultConfigVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.MultipleMemberUtill;
import javacryption.aes.AesCtr;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.AuthenticationAccountsException;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.ResourceBundle;

public class NewMerchant extends HttpServlet
{
    private static final ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.PartnerId");
    private static Logger Log = new Logger(NewMerchant.class.getName());
    Functions functions = new Functions();
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        Merchants merchants                     = new Merchants();
        HttpSession session                     = request.getSession();
        User user                               = null;
        MultipleMemberUtill multipleMemberUtill = new MultipleMemberUtill();
        String remoteAddr                       = Functions.getIpAddress(request);
        int serverPort                          = request.getServerPort();
        String servletPath                      = request.getServletPath();
        String userAgent                        = request.getHeader("User-Agent");
        String header                           = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath + ",User-Agent="+userAgent;
        String url                              = request.getHeader("referer");
        String sendEmailNotification            = "N";

        if (((String) request.getParameter("step")).equals("1") || ((String) request.getParameter("step")).equals("3"))
        {
            session = Functions.getNewSession(request);
            user    = (User)session.getAttribute("Anonymous");
            if(Functions.validateCSRFAnonymos(request.getParameter("ctoken"),user))
            {
                session.setAttribute("Anonymous",user);
            }
            else
            {     Log.debug("session out");
                response.sendRedirect("/merchant/sessionout.jsp");
                return;
            }
        }
        else if (((String) request.getParameter("step")).equals("2"))
        {
            if (!merchants.isLoggedIn(session))
            {   Log.debug("member is logout ");
                response.sendRedirect("/merchant/Logout.jsp");
                return;
            }
            user =  (User)session.getAttribute("ESAPIUserSessionKey");
            if(Functions.validateCSRF(request.getParameter("ctoken"),user))
            {
                session.setAttribute("ESAPIUserSessionKey",user);
            }
            else
            {
                response.sendRedirect("/merchant/Logout.jsp");
                return;
            }
        }
        Log.debug("successful");
        response.setContentType("text/html");
        PrintWriter out         = response.getWriter();
        String data             = request.getParameter("data");
        String redirectpage     = null;
        String errormsg         = "";
        String msg              = "F";
        int idx1, idx2;
        String EOL              = "<BR>";
        String passwd           = null;
        String conpasswd        = null;
        String tpasswd          = null;
        String contpasswd       = null;
        String etoken           = request.getParameter("emailtoken");
        boolean isUpdate        = false;

        if(request.getParameter("passwd")!=null )
        {
            passwd = AesCtr.decrypt(request.getParameter("passwd"), request.getParameter("ctoken"), 256);
            request.setAttribute("passwd",passwd);
        }

        if(request.getParameter("conpasswd")!=null )
        {
            conpasswd = AesCtr.decrypt(request.getParameter("conpasswd"), request.getParameter("ctoken"), 256);
            request.setAttribute("conpasswd",conpasswd);
        }
        if (((String) request.getParameter("step")).equals("1"))
        {
            Hashtable detailhash = new Hashtable();
            Log.debug("In newmerchant.java");
            try
            {
                boolean flag = true;

                if (!ESAPI.validator().isValidInput("username",(String) request.getParameter("username"),"username",50,false))
                {
                    Log.debug("in newmerchant.java.., Username or Email Address is not valid....");
                    errormsg = errormsg + "Username or Email Address entered is incorrect."+EOL;
                    detailhash.put("login", (String) request.getParameter("username"));
                    flag = false;
                }
                else
                    detailhash.put("login", (String) request.getParameter("username"));

                if ((!ESAPI.validator().isValidInput("passwd",passwd,"NewPassword",20,false)) || (!(passwd).equals(conpasswd)))
                {
                    flag = false;
                    Log.debug("wrong password");
                    //msg="X";
                    detailhash.put("passwd", passwd);
                    errormsg = errormsg + "Please enter valid Password/Confirm Password."+EOL;
                }
                else
                    detailhash.put("passwd", passwd);
                if (!ESAPI.validator().isValidInput("company_name ",(String) request.getParameter("company_name"),"companyName",100,false))
                {   Log.debug("enter velid compny name");
                    errormsg    = errormsg + "Please enter valid Organisation Name."+EOL;
                    flag        = false;
                    detailhash.put("company_name", (String) request.getParameter("company_name"));
                }
                else
                    detailhash.put("company_name", (String) request.getParameter("company_name"));
                if (!ESAPI.validator().isValidInput("contact_persons",(String) request.getParameter("contact_persons"),"contactName",100,false))
                {   Log.debug("enter velid Contect person name");
                    errormsg    = errormsg + "Please Enter valid Contact Person."+EOL;
                    flag        = false;
                    detailhash.put("contact_persons", (String) request.getParameter("contact_persons"));
                }
                else
                    detailhash.put("contact_persons", (String) request.getParameter("contact_persons"));
                if (!ESAPI.validator().isValidInput("contact_emails",(String) request.getParameter("contact_emails"),"Email",100,false))
                {
                    flag    = false;
                    msg     = "X";
                    Log.debug("Enter valid EmailID"+request.getParameter("contact_persons"));
                    errormsg = errormsg + "Please enter valid Contact emailaddress."+EOL;
                    detailhash.put("contact_emails", (String) request.getParameter("contact_emails"));
                }
                else
                {
                    detailhash.put("contact_emails", (String) request.getParameter("contact_emails"));
                }
                String country = "";
                if(functions.isValueNull(request.getParameter("country")))
                {
                    String [] splitValue    = request.getParameter("country").split("\\|");
                    country                 = splitValue[0];
                }
                Log.debug("country----"+request.getParameter("country")+"-----"+country);
                if (!ESAPI.validator().isValidInput("country",country,"StrictString",25,false))
                {
                    errormsg    = errormsg + "Please enter Valid Country Name."+EOL;
                    flag        = false;
                    detailhash.put("country", country);
                }
                else
                    detailhash.put("country", country);
                detailhash.put("phonecc", request.getParameter("phonecc"));

                String telno        = request.getParameter("telno");
                String phonecc      = request.getParameter("phonecc");
                String phoneNo      = phonecc+"-"+telno;

                if (!ESAPI.validator().isValidInput("phonecc",(String) phonecc,"SignupPhone",20,false))
                {
                    Log.debug("PLS enter valid phone cc ");
                    errormsg    = errormsg + "Please enter valid phone cc."+EOL;
                    flag        = false;
                }
                if (!ESAPI.validator().isValidInput("telno",(String) telno,"SignupPhone",20,false))
                {   Log.debug("PLS enter valid phone number ");
                    errormsg    = errormsg + "Please enter valid telephone number."+EOL;
                    flag        = false;
                    detailhash.put("telno", (String) phoneNo);
                }
                else
                {
                    detailhash.put("telno", (String) phoneNo);
                }
                if (!ESAPI.validator().isValidInput("sitename",(String) request.getParameter("sitename"),"URL",100,false) || functions.hasHTMLTags(request.getParameter("sitename")))
                {   Log.debug("PLS enter velid site ");
                    errormsg    = errormsg + "Site name is Invalid. Check example for enter VALID site name. "+EOL;
                    flag        = false;
                    detailhash.put("sitename", (String) request.getParameter("sitename"));
                }
                else
                    detailhash.put("sitename", (String) request.getParameter("sitename"));

                String mainContactcc        = request.getParameter("mainContactcc");
                String mainContact        = request.getParameter("maincontact_phone");
                String mainContact_number      = mainContactcc+"-"+mainContact;
                if (!ESAPI.validator().isValidInput("mainContactcc",(String) mainContactcc,"SignupPhone",20,false))
                {
                    Log.debug("PLS enter valid Contact Telephone Number cc ");
                    errormsg    = errormsg + "Please enter valid  Contact Telephone Number cc."+EOL;
                    flag        = false;
                }
                if (!ESAPI.validator().isValidInput("maincontact_phone ",(String) request.getParameter("maincontact_phone"),"Phone",16,false))
                {
                    errormsg    = errormsg + "Please enter valid Contact telephone number ."+EOL;
                    flag        = false;
                    detailhash.put("maincontact_phone",(String) mainContact_number);
                }
                else
                    detailhash.put("maincontact_phone",(String) mainContact_number);


                if (!ESAPI.validator().isValidInput("partnerid",(String) request.getParameter("partnerid"),"Numbers",5,false))
                {
                    errormsg    = errormsg + "Partner not found ."+EOL;
                    flag        = false;
                    detailhash.put("partnerid", (String) request.getParameter("partnerid"));
                }
                else
                    detailhash.put("partnerid", (String) request.getParameter("partnerid"));

                detailhash.put("emailtoken", (String) request.getParameter("emailtoken"));

                detailhash.put("company", (String)request.getParameter("company"));
                detailhash.put("consent",request.getParameter("consent"));
                detailhash.put("url", url);
                detailhash.put("ipAddress", remoteAddr);
                detailhash.put("httpheader", header);

                if(request.getParameter("sendEmailNotification") != null){
                    sendEmailNotification = request.getParameter("sendEmailNotification");
                }
                detailhash.put("sendEmailNotification", sendEmailNotification);

                if (flag == true)
                {
                    PartnerManager partnerManager                   = new PartnerManager();
                    PartnerDefaultConfigVO partnerDefaultConfigVO   = partnerManager.getPartnerDefaultConfig((String)detailhash.get("partnerid"));

                    if(partnerDefaultConfigVO == null){
                        Log.debug("partner default configuration not found");
                        redirectpage = "/signup.jsp?MES=" +msg+"&ctoken="+user.getCSRFToken();
                        request.setAttribute("details", detailhash);
                        request.setAttribute("error","partner default configuration not found");
                    }
                    else
                    {
                        Log.debug("Valid user data");
                        if (merchants.isMember((String) detailhash.get("login"))|| multipleMemberUtill.isUniqueChildMember((String) detailhash.get("login")))
                        {   Log.debug("redirect to NEWLOGIN");
                            redirectpage = "/newloginname.jsp?ctoken="+user.getCSRFToken();
                            session.setAttribute("newmember", detailhash);
                            request.setAttribute("sendEmailNotification", sendEmailNotification);
                            request.setAttribute("username", (String) detailhash.get("login"));
                        }
                        else
                        {

                            detailhash.put("actionExecutorId","-");
                            detailhash.put("actionExecutorName","merchant-"+detailhash.get("login"));

                            request.setAttribute("role", "merchant");
                            addMerchant(detailhash,partnerDefaultConfigVO);
                            request.setAttribute("username", (String) detailhash.get("login"));
                            redirectpage = "/thankyou.jsp";
                            Log.info("THANK YOU for signup  " + redirectpage);
                            isUpdate = true;
                        }
                    }
                }
                else
                {
                    Log.debug("ENTER VALID DATA");
                    redirectpage = "/signup.jsp?MES=" +msg+"&ctoken="+user.getCSRFToken();
                    request.setAttribute("details", detailhash);
                    request.setAttribute("error",errormsg);
                }
            }
            catch (Exception e)
            {
                Log.error("Leaving Merchants (Step 1) throwing Exception : " , e);
                if(e.getMessage().contains("Duplicate"))
                {
                    Log.debug("redirect to NEWLOGIN");
                    redirectpage = "/newloginname.jsp?ctoken="+user.getCSRFToken();
                    session.setAttribute("newmember", detailhash);
                    request.setAttribute("username", (String) detailhash.get("login"));
                }
                else
                {
                    out.println(Functions.NewShowConfirmation1("Error", "Internal System Error"));
                }
            }
            Log.debug("redirectpage  " + redirectpage);
        }
        //Step 2
        else if (((String) request.getParameter("step")).equals("2"))
        {
            Log.info("inside step 2");
            Hashtable detailhash = new Hashtable();
            try
            {
                String company_type         = "";
                String proprietor           = "";
                String proprietorAddress    = "";
                String proprietorPhNo       = "";
                String OrganisationRegNo    = "";
                String partnerNameAddress   = "";
                String directorsNameAddress = "";
                String pan                  = "";
                String directors            = "";
                String employees            = "";
                String potentialbusiness    = "";
                String registeredaddress    = "";
                String bussinessaddress     = "";
                String notifyemail          = "";
                String acdetails            = "";
                String bankName             = "";
                String Branch               = "";
                String AcType               = "";
                String AcNumber             = "";
                boolean flag                = true;
                try
                {
                    company_type = ESAPI.validator().getValidInput("comtype",request.getParameter("comtype"),"StrictString",20,false);
                }
                catch(ValidationException e)
                {
                    Log.error("Invalid input in compny type",e);
                    flag        = false;
                    errormsg    = errormsg + "<br><font face=\"arial\"  size=\"2\">Please select Company type.</font>";
                }
                Log.debug("In NewMerchant.java, in type = " + company_type + ".........");
                if (company_type.equals("Propritory"))
                {
                    Log.debug("In NewMerchant.java, in type = Propritory");
                    if (!ESAPI.validator().isValidInput("proprietor",(String) request.getParameter("proprietor"),"contactName",35,false))
                    {
                        errormsg    = errormsg + "<br><font face=\"arial\"  size=\"2\">Please enter valid Proprietor Name (Allowed only alphanumeric values)</font>";
                        flag        = false;
                        proprietor  = request.getParameter("proprietor");

                    }
                    else
                        proprietor = request.getParameter("proprietor");
                    if (!ESAPI.validator().isValidInput("resAddress",(String) request.getParameter("resAddress"),"Address",175,false))
                    {
                        errormsg            = errormsg + "<br><font face=\"arial\"  size=\"2\">Please enter valid Proprietor Residential address.</font>";
                        flag                = false;
                        proprietorAddress   = request.getParameter("resAddress");

                    }
                    else
                        proprietorAddress = request.getParameter("resAddress");
                    if (!ESAPI.validator().isValidInput("Res_Ph_num",(String) request.getParameter("Res_Ph_num"),"Phone",35,false))
                    {
                        errormsg        = errormsg + "<br><font face=\"arial\"  size=\"2\">Please enter valid Proprietor Residential phone number (Allowed only numeric values[0-9])</font>";
                        flag            = false;
                        proprietorPhNo  = request.getParameter("Res_Ph_num");

                    }
                    else
                        proprietorPhNo = request.getParameter("Res_Ph_num");
                }
                else if ( company_type.equals("Partnership"))
                {
                    Log.debug("In NewMerchant.java, in type = Partnership");
                    if (!ESAPI.validator().isValidInput("OrgRegNumber",(String) request.getParameter("OrgRegNumber"),"Address",35,false))
                    {
                        errormsg            = errormsg + "<br><font face=\"arial\"  size=\"2\">Please enter valid Organisation registration number</font>";
                        flag                = false;
                        OrganisationRegNo   = request.getParameter("OrgRegNumber");

                    }
                    else
                        OrganisationRegNo = request.getParameter("OrgRegNumber");
                    if (!ESAPI.validator().isValidInput("partners_data",(String) request.getParameter("partners_data"),"Address",175,false))
                    {
                        errormsg            = errormsg + "<br><font face=\"arial\" size=\"2\">Please enter valid Partner's data.</font>";
                        flag                = false;
                        partnerNameAddress  = request.getParameter("partners_data");

                    }
                    else
                        partnerNameAddress = request.getParameter("partners_data");
                }
                else if ( company_type.equals("Private"))
                {
                    Log.debug("In NewMerchant.java, in type = Private");
                    if (!ESAPI.validator().isValidInput("ComRegNumber",(String) request.getParameter("ComRegNumber"),"Address",35,false))
                    {
                        errormsg            = errormsg + "<br><font face=\"arial\"  size=\"2\">Please enter valid Company Registration number</font>";
                        flag                = false;
                        OrganisationRegNo   = request.getParameter("ComRegNumber");
                    }
                    else
                        OrganisationRegNo = request.getParameter("ComRegNumber");
                }
                else
                {
                    Log.debug("In NewMerchant.java, in type = else");
                    if (!ESAPI.validator().isValidInput("ComRegNumber",(String) request.getParameter("ComRegNumber"),"SafeString",35,false))
                    {
                        errormsg            = errormsg + "<br><font face=\"arial\"  size=\"2\">Please enter valid Company Registration number (Allowed only alphanumeric values)</font>";
                        flag                = false;
                        OrganisationRegNo   = request.getParameter("ComRegNumber");
                    }
                    else
                        OrganisationRegNo   = request.getParameter("ComRegNumber");
                }
                detailhash.put("company_type", company_type);
                detailhash.put("proprietor", proprietor);
                detailhash.put("proprietorAddress", proprietorAddress);
                detailhash.put("proprietorPhNo", proprietorPhNo);
                detailhash.put("OrganisationRegNo", OrganisationRegNo);
                detailhash.put("partnerNameAddress", partnerNameAddress);
                if (!ESAPI.validator().isValidInput("PAN",(String) request.getParameter("PAN"),"alphanum",35,true))
                {
                    errormsg    = errormsg + "<br><font face=\"arial\"  size=\"2\">Please enter valid Permanent Account number (Allowed only alphanumeric values)</font>";
                    flag        = false;
                    detailhash.put("pan", (String) request.getParameter("PAN"));
                }
                else
                    detailhash.put("pan", (String) request.getParameter("PAN"));

                if (!ESAPI.validator().isValidInput("Directors_data",(String) request.getParameter("Directors_data"),"Address",175,false))
                {
                    errormsg                = errormsg + "<br><font face=\"arial\"  size=\"2\">Please enter valid Directors data.</font>";
                    flag                    = false;
                    directorsNameAddress    = request.getParameter("Directors_data");
                    detailhash.put("directorsNameAddress", directorsNameAddress);
                }
                else
                {
                    directorsNameAddress = request.getParameter("Directors_data");
                    detailhash.put("directorsNameAddress", directorsNameAddress);
                }
                if (!ESAPI.validator().isValidInput("employees",(String) request.getParameter("employees"),"Address",175,false))
                {
                    errormsg    = errormsg + "<br><font face=\"arial\" size=\"2\">Please enter valid Employees values </font>";
                    flag        = false;
                    detailhash.put("employees", (String) request.getParameter("employees"));
                }
                else
                    detailhash.put("employees", (String) request.getParameter("employees"));

                if (!ESAPI.validator().isValidInput("potentialbusiness",(String) request.getParameter("potentialbusiness"),"Numbers",20,false))
                {
                    errormsg    = errormsg + "<br><font face=\"arial\"  size=\"2\">Please enter valid Potential Business (Allowed only numeric values)</font>";
                    flag        = false;
                    detailhash.put("potentialbusiness", (String) request.getParameter("potentialbusiness"));
                }
                else
                    detailhash.put("potentialbusiness", (String) request.getParameter("potentialbusiness"));

                if (!ESAPI.validator().isValidInput("registeredaddress",(String) request.getParameter("registeredaddress"),"Address",175,false))
                {
                    errormsg    = errormsg + "<br><font face=\"arial\"  size=\"2\">Please enter valid Registered address.</font>";
                    flag        = false;
                    detailhash.put("registeredaddress", (String) request.getParameter("registeredaddress"));
                }
                else
                    detailhash.put("registeredaddress", (String) request.getParameter("registeredaddress"));

                if (!ESAPI.validator().isValidInput("bussinessaddress",(String) request.getParameter("bussinessaddress"),"Address",175,false))
                {
                    errormsg    = errormsg + "<br><font face=\"arial\"  size=\"2\">Please enter valid Business address.</font>";
                    flag        = false;
                    detailhash.put("bussinessaddress", (String) request.getParameter("bussinessaddress"));
                }
                else
                    detailhash.put("bussinessaddress", (String) request.getParameter("bussinessaddress"));

                if (!ESAPI.validator().isValidInput("notifyemail",(String) request.getParameter("notifyemail"),"Email",35,false))
                {
                    errormsg    = errormsg + "<br><font face=\"arial\"  size=\"2\">Please enter valid Notify Email address.</font>";
                    flag        = false;
                    detailhash.put("notifyemail", (String) request.getParameter("notifyemail"));
                }
                else
                    detailhash.put("notifyemail", (String) request.getParameter("notifyemail"));

                String accdetails = null;
                if (!ESAPI.validator().isValidInput("bankname",(String) request.getParameter("bankname"),"alphanum",35,false))
                {
                    errormsg    = errormsg + "<br><font face=\"arial\"  size=\"2\">Please enter valid Bank name (Allowed only alphanumeric values) </font>";
                    flag        = false;
                    detailhash.put("bankname", (String) request.getParameter("bankname"));
                    accdetails  = (String) request.getParameter("bankname");
                }
                else
                {
                    detailhash.put("bankname", (String) request.getParameter("bankname"));
                    accdetails = (String) request.getParameter("bankname");
                }

                if (!ESAPI.validator().isValidInput("branch",(String) request.getParameter("branch"),"alphanum",35,false))
                {
                    errormsg    = errormsg + "<br><font face=\"arial\"  size=\"2\">Please enter valid Branch (Allowed only alphanumeric values)</font>";
                    flag        = false;
                    detailhash.put("branch", (String) request.getParameter("branch"));
                    accdetails  = accdetails + "," + (String) request.getParameter("branch");
                }
                else
                {
                    detailhash.put("branch", (String) request.getParameter("branch"));
                    accdetails = accdetails + "," + (String) request.getParameter("branch");
                }
                if (!Functions.isValidate((String) request.getParameter("acctype")))
                {
                    errormsg    = errormsg + "<br><font face=\"arial\" size=\"2\">Please enter valid Account type.</font>";
                    flag        = false;
                    detailhash.put("acctype", (String) request.getParameter("acctype"));
                    accdetails  = accdetails + "  " + (String) request.getParameter("acctype");
                }
                else
                {
                    detailhash.put("acctype", (String) request.getParameter("acctype"));
                    accdetails  = accdetails + "  " + (String) request.getParameter("acctype");
                }
                if (!ESAPI.validator().isValidInput("AcNumber",(String) request.getParameter("AcNumber"),"Numbers",35,false))
                {
                    errormsg    = errormsg + "<br><font face=\"arial\"  size=\"2\">Please enter valid Account number (Allowed only numeric values)</font>";
                    flag        = false;
                    detailhash.put("AcNumber", (String) request.getParameter("AcNumber"));
                    accdetails  = accdetails + "-" + (String) request.getParameter("AcNumber");
                }
                else
                {
                    detailhash.put("AcNumber", (String) request.getParameter("AcNumber"));
                    accdetails  = accdetails + "-" + (String) request.getParameter("AcNumber");
                }
                if (flag == true)
                {
                    Log.debug("Inserting Merchant");
                    detailhash.put("acdetails", accdetails);
                    int result = updateOrganProfile(detailhash, session,request);
                    if (result == 0)
                    {
                        msg = "Organization detail changes update failed.";
                    }
                    else{
                        msg = "Organization detail changes update successful.";}

                    request.setAttribute("MES",msg);
                    redirectpage            = "/servlet/OrganisationProfile";
                    RequestDispatcher rd    = request.getRequestDispatcher(redirectpage);
                    rd.forward(request, response);
                }
                else
                {
                    detailhash.put("acdetails", accdetails);
                    //System.out.println("in NewMerchant.java, in else");
                    Log.debug(errormsg);
                    msg = "X";
                    request.setAttribute("MES",msg);
                    request.setAttribute("error",errormsg);
                    request.setAttribute("details", detailhash);
                    redirectpage            = "/servlet/OrganisationProfile";
                    RequestDispatcher rd    = request.getRequestDispatcher(redirectpage);
                    rd.forward(request, response);
                    return;
                }
            }
            catch (SystemError se)
            {
                Log.error("Leaving Merchants (Step 2) throwing System Error : ",se);
                redirectpage = "/error.jsp";
            }
            catch (Exception e)
            {
                Log.error("Leaving Merchants (Step 2) throwing Exception : " ,e);
                redirectpage = "/error.jsp";
            }
            Log.debug("Leaving UpdateMerchant  " + detailhash);
            Log.debug("redirectpage  " + redirectpage);
        }
        else if (((String) request.getParameter("step")).equals("3"))
        {
            Log.info("inside step 3");
            Hashtable detailhash = (Hashtable) session.getAttribute("newmember");
            try
            {
                PartnerManager partnerManager                   = new PartnerManager();
                PartnerDefaultConfigVO partnerDefaultConfigVO   = partnerManager.getPartnerDefaultConfig((String)detailhash.get("partnerid"));

                if(request.getParameter("sendEmailNotification") != null){
                    sendEmailNotification = request.getParameter("sendEmailNotification");
                }
                detailhash.put("sendEmailNotification", sendEmailNotification);

                if(partnerDefaultConfigVO == null){
                    Log.debug("partner default configuration not found");
                    redirectpage    = "/signup.jsp?MES=" +msg+"&ctoken="+user.getCSRFToken();
                    request.setAttribute("details", detailhash);
                    request.setAttribute("error","partner default configuration not found");
                }

                if (!ESAPI.validator().isValidInput("username",(String) request.getParameter("username"),"alphanum",100,false)|| merchants.isMember((String) request.getParameter("username")) || multipleMemberUtill.isUniqueChildMember((String) request.getParameter("username")))
                {
                    request.setAttribute("username", (String) request.getParameter("username"));
                    errormsg        = errormsg + "Please Enter Valid Username.";
                    request.setAttribute("error",errormsg);
                    redirectpage    = "/newloginname.jsp";
                }
                else
                {


                    //inserting the record
                    detailhash.put("login", request.getParameter("username"));
                    request.setAttribute("role","merchant");
                    addMerchant(detailhash,partnerDefaultConfigVO);
                    Log.debug(request.getParameter("username"));
                    request.setAttribute("username", (String) detailhash.get("login"));
                    redirectpage    = "/thankyou.jsp";
                    isUpdate        = true;
                }
                Log.debug("redirectpage  " + redirectpage);
            }
            catch (SystemError se)
            {
                Log.error("Leaving Merchants (Step 3) throwing System Error : ",se);
                if(se.getMessage().contains("Duplicate"))
                {
                    Log.debug("redirect to NEWLOGIN");
                    redirectpage    = "/newloginname.jsp?ctoken="+user.getCSRFToken();
                    session.setAttribute("newmember", detailhash);
                    request.setAttribute("username", (String) detailhash.get("login"));
                }
                else
                {
                    out.println(Functions.NewShowConfirmation1("Error", "Username is not found"));
                }
            }
            catch (Exception e)
            {
                Log.error("Leaving Merchants (Step 3) throwing Exception : ",e);
                out.println(Functions.NewShowConfirmation1("Error", "Username is not found"));
            }
        }

        RequestDispatcher rd    = request.getRequestDispatcher(redirectpage);
        rd.forward(request, response);
    }
    public int updateOrganProfile(Hashtable details, HttpSession session,HttpServletRequest request) throws SystemError
    {
        Merchants merchants = new Merchants();
        int rs              = merchants.updateOrganProfile(details, session);
        Member mem          = merchants.authenticate((String) session.getAttribute("username"),(String) session.getAttribute("partnerid"),request);
        return rs;
    }
    public void addMerchant(Hashtable details,PartnerDefaultConfigVO partnerDefaultConfigVO) throws SystemError
    {
        Merchants merchants             = new Merchants();
        Member mem                      = null;
        String sendEmailNotification    = "N";
        try
        {
            User user   = ESAPI.authenticator().createUser((String) details.get("login"), (String) details.get("passwd"), "merchant");
            mem         = merchants.addMerchant_new(user.getAccountId(), details, partnerDefaultConfigVO);
        }
        catch (Exception e)
        {
            Log.error("Add user throwing Authentication Exception ", e);
            if(e instanceof AuthenticationAccountsException)
            {
                String message  = ((AuthenticationAccountsException)e).getLogMessage();
                if(message.contains("Duplicate"))
                {
                    throw new SystemError("Error: " + message);
                }
            }
            try{
                merchants.DeleteBoth((String) details.get("login"));
            }
            catch(Exception e1)
            {
                Log.error("Exception while deletion of Details::",e1);
            }
            throw new SystemError("Error: " + e.getMessage());
        }
        if(details.get("sendEmailNotification") != null){
            sendEmailNotification   =(String) details.get("sendEmailNotification");
        }
        String liveUrl                                  = ApplicationProperties.getProperty("COMPANY_LIVE_URL");
        InvoiceEntry invoiceEntry                       = new InvoiceEntry();
        if(sendEmailNotification.equalsIgnoreCase("Y")){
            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
            HashMap merchantSignupMail                      = new HashMap();

            merchantSignupMail.put(MailPlaceHolder.USERNAME, details.get("login"));
            merchantSignupMail.put(MailPlaceHolder.NAME, details.get("contact_persons"));
            merchantSignupMail.put(MailPlaceHolder.TOID, String.valueOf(mem.memberid));
            merchantSignupMail.put(MailPlaceHolder.CTOKEN, details.get("emailtoken"));
            merchantSignupMail.put(MailPlaceHolder.PARTNER_URL, liveUrl);
            merchantSignupMail.put(MailPlaceHolder.PARTNERID, details.get("partnerid"));
            merchantSignupMail.put(MailPlaceHolder.FROM_TYPE,details.get("company"));

            asynchronousMailService.sendMerchantSignup(MailEventEnum.PARTNERS_MERCHANT_REGISTRATION, merchantSignupMail);
        }

        invoiceEntry.insertInvoiceConfigDetails(String.valueOf(mem.memberid));
    }
}