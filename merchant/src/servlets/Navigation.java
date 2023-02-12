import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.enums.Module;
import com.manager.AppRequestManager;
import com.manager.ApplicationManager;
import com.manager.dao.PartnerDAO;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.validators.BankInputName;
import com.vo.applicationManagerVOs.AppValidationVO;
import com.vo.applicationManagerVOs.ApplicationManagerVO;
import com.vo.applicationManagerVOs.CompanyProfileVO;
import com.vo.applicationManagerVOs.NavigationVO;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Niket
 * Date: 1/21/15
 * Time: 9:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class Navigation extends HttpServlet
{
    private static Logger log = new Logger(PopulateApplication.class.getName());

    private static Logger logger = new Logger(Navigation.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        Functions functions=new Functions();
        Merchants merchants =new Merchants();

        boolean isOptional=true;
        StringBuffer sErrorMessage = new StringBuffer();
        HttpSession session = Functions.getNewSession(request);
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        String pagenoforiframe=(String)session.getAttribute("pageno");
        if(functions.isValueNull(pagenoforiframe)){
           /* RequestDispatcher rd = request.getRequestDispatcher("/servlet/PopulateApplication?ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
            return;*/
            ApplicationManagerVO applicationManagerVO=new ApplicationManagerVO();
            NavigationVO navigationVO = new NavigationVO();
            ApplicationManager applicationManager = new ApplicationManager();
            AppValidationVO appValidationVO = null;
            Map<Integer, Map<Boolean, Set<BankInputName>>> fullValidationForStep=new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();
            Map<Integer, Map<Boolean, Set<BankInputName>>> dependencyFullValidationForStep=new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();
            Map<Integer, Map<Boolean, Set<BankInputName>>> dependencyOtherFullValidationForStep=new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();
            Map<Boolean,Set<BankInputName>> dependencyPageViseValidation=new HashMap<Boolean, Set<BankInputName>>();
            Map<Boolean,Set<BankInputName>> dependencyOtherPageViseValidation=new HashMap<Boolean, Set<BankInputName>>();
            Map<Integer,Set<BankInputName>> otherValidation=new HashMap<Integer, Set<BankInputName>>();
            Set<BankInputName> otherValidationPageVise=new HashSet<BankInputName>();
            PartnerDAO partnerDAO = new PartnerDAO();
            //Request Dispatcher
            RequestDispatcher rdSuccessU = request.getRequestDispatcher("/appNavigation.jsp?MES=Success&ctoken="+user.getCSRFToken());
            RequestDispatcher rdSuccessV=request.getRequestDispatcher("/viewapplicationdetails.jsp?MES=Success&ctoken="+user.getCSRFToken());
            try
            {
                logger.debug("inside Navigation Application");
                applicationManagerVO.setMemberId(session.getAttribute("merchantid").toString());
                applicationManagerVO.setPartnerid(session.getAttribute("partnerid").toString());
                session.setAttribute("company", partnerDAO.getPartnerName((String)session.getAttribute("partnerid").toString()));
                appValidationVO=applicationManager.loadAllMerchantBankMapping(applicationManagerVO.getMemberId());
                if(functions.isValueNull(String.valueOf(session.getAttribute("applicationManagerVO"))))
                {
                    applicationManagerVO=(ApplicationManagerVO)session.getAttribute("applicationManagerVO");
                }
                else
                {
                    applicationManager.populateAppllicationData(applicationManagerVO);
                }
                applicationManager.setValidationForMember(applicationManagerVO.getMemberId(), navigationVO, fullValidationForStep, dependencyFullValidationForStep,dependencyPageViseValidation,otherValidation,dependencyOtherFullValidationForStep,otherValidationPageVise,dependencyOtherPageViseValidation,appValidationVO);

                //set in session value
                session.setAttribute("applicationManagerVO",applicationManagerVO);
                session.setAttribute("navigationVO",navigationVO);
                session.setAttribute("appValidationVO",appValidationVO);
                request.setAttribute("fullValidationForStep", fullValidationForStep);
                request.setAttribute("dependencyFullValidationForStep", dependencyFullValidationForStep);
                request.setAttribute("otherValidation", otherValidation);
                request.setAttribute("dependencyOtherFullValidationForStep", dependencyOtherFullValidationForStep);
                //pages in order how to be navigated
                    navigationVO.addStepAndPageName("companyprofile.jsp");
                    navigationVO.addStepAndPageName("ownershipprofile.jsp");
                    navigationVO.addStepAndPageName("businessprofile.jsp");
                    navigationVO.addStepAndPageName("bankapplication.jsp");
                    navigationVO.addStepAndPageName("cardholderprofile.jsp");
                    navigationVO.addStepAndPageName("upload.jsp");
                    //navigationVO.addStepAndPageName("reports.jsp");
            }
            catch (Exception e)
            {
                logger.error("Mail class exception::",e);
            }
        }

//Validation for step1
        String EOL = "<br>";
        String companyprofile_name1 =request.getParameter("merchantname");
        String companyprofile_street =request.getParameter("corporatestreet");
        String companyprofile_address_proof=request.getParameter("merchant_addressproof");
        String companyprofile_address_proof1=request.getParameter("corporate_addressproof");
        String companyprofile_im_address = request.getParameter("SkypeIMaddress");
        String companyprofile_business_name=request.getParameter("corporatename");
        String companyprofile_address_id1=request.getParameter("corporate_addressId");
        String companyprofile_billing_address=request.getParameter("corporateaddress");
        String companyprofile_zip1=request.getParameter("corporatezipcode");
        String companyprofile_registered_corporate=request.getParameter("registered_corporatename");
        String companyprofile_street2=request.getParameter("registered_directors_street");
        String companyprofile_registered_number=request.getParameter("EURegistrationNumber");
        String companyprofile_name_relationship=request.getParameter("contactname");
        String companyprofile_email2=request.getParameter("contactemailaddress");
        String companyprofile_designation=request.getParameter("contact_designation");
        String companyprofile_name_relationship1=request.getParameter("technicalcontactname");
        String companyprofile_email3=request.getParameter("technicalemailaddress");
        String companyprofile_designation4=request.getParameter("technical_designation");
        String companyprofile_name3=request.getParameter("billingcontactname");
        String companyprofile_email5=request.getParameter("billingemailaddress");

//Validation for step2
        String shareholderprofile2_address=request.getParameter("shareholderprofile2_address");
        String shareholderprofile2_street=request.getParameter("shareholderprofile2_street");
        String corporateshareholder1_Address=request.getParameter("corporateshareholder1_Address");
        String corporateshareholder1_Street=request.getParameter("corporateshareholder1_Street");
        String authorizedsignatoryprofile1_designation=request.getParameter("authorizedsignatoryprofile1_designation");
        String authorizedsignatoryprofile_emailaddress=request.getParameter("authorizedsignatoryprofile_emailaddress");
        String authorizedsignatoryprofile_address=request.getParameter("authorizedsignatoryprofile_address");
        String authorizedsignatoryprofile_street=request.getParameter("authorizedsignatoryprofile_street");

// Validation for step3
        String shoppingcart_address=request.getParameter("shoppingcart_address");
        String callcenter_address=request.getParameter("callcenter_address");
        String shoppingcart_email=request.getParameter("shoppingcart_email");
        String shoppingcart_company_name=request.getParameter("shoppingcart_company_name");
        String isacallcenterusedyes=request.getParameter("isacallcenterusedyes");
        String payment_address=request.getParameter("payment_address");
        String payment_company_name=request.getParameter("payment_company_name");
        String webhost_company_name=request.getParameter("webhost_company_name");
        String inhouselocation=request.getParameter("inhouselocation");
        String otherlocation=request.getParameter("otherlocation");
        String mainsuppliers=request.getParameter("mainsuppliers");
        String isafulfillmenthouseused=request.getParameter("isafulfillmenthouseused");
        String coolingoffperiod=request.getParameter("coolingoffperiod");
        String timeframe=request.getParameter("timeframe");
        String direct_debit_sepa=request.getParameter("direct_debit_sepa");
        String creditor_id=request.getParameter("creditor_id");
        String alternative_payments=request.getParameter("alternative_payments");
        String risk_management=request.getParameter("risk_management");
        String payment_engine=request.getParameter("payment_engine");
        String countries_blocked=request.getParameter("countries_blocked");
        String pricing_policies_website=request.getParameter("pricing_policies_website");


//Validation for step4
    //    BankProfileVO bankProfileVO= null;
        String bankinfo_bankaddress = request.getParameter("bankinfo_bankaddress");

        String salesvolume_6monthsago= request.getParameter("salesvolume_6monthsago");
        String salesvolume_5monthsago= request.getParameter("salesvolume_5monthsago");
        String salesvolume_4monthsago= request.getParameter("salesvolume_4monthsago");
        String salesvolume_3monthsago= request.getParameter("salesvolume_3monthsago");
        String salesvolume_2monthsago= request.getParameter("salesvolume_2monthsago");
        String salesvolume_lastmonth=request.getParameter("salesvolume_lastmonth");
        String salesvolume_12monthsago=request.getParameter("salesvolume_12monthsago");
        String salesvolume_year2=request.getParameter("salesvolume_year2");
        String salesvolume_year3=request.getParameter("salesvolume_year3");

        String numberoftransactions_6monthsago= request.getParameter("numberoftransactions_6monthsago");
        String numberoftransactions_5monthsago= request.getParameter("numberoftransactions_5monthsago");
        String numberoftransactions_4monthsago= request.getParameter("numberoftransactions_4monthsago");
        String numberoftransactions_3monthsago= request.getParameter("numberoftransactions_3monthsago");
        String numberoftransactions_2monthsago= request.getParameter("numberoftransactions_2monthsago");
        String numberoftransactions_lastmonth= request.getParameter("numberoftransactions_lastmonth");
        String numberoftransactions_12monthsago = request.getParameter("numberoftransactions_12monthsago");
        String numberoftransactions_year2= request.getParameter("numberoftransactions_year2");
        String numberoftransactions_year3= request.getParameter("numberoftransactions_year3");

        String chargebackvolume_6monthsago=request.getParameter("chargebackvolume_6monthsago");
        String chargebackvolume_5monthsago=request.getParameter("chargebackvolume_5monthsago");
        String chargebackvolume_4monthsago=request.getParameter("chargebackvolume_4monthsago");
        String chargebackvolume_3monthsago=request.getParameter("chargebackvolume_3monthsago");
        String chargebackvolume_2monthsago=request.getParameter("chargebackvolume_2monthsago");
        String chargebackvolume_lastmonth=request.getParameter("chargebackvolume_lastmonth");
        String chargebackvolume_12monthsago=request.getParameter("chargebackvolume_12monthsago");
        String chargebackvolume_year2=request.getParameter("chargebackvolume_year2");
        String chargebackvolume_year3=request.getParameter("chargebackvolume_year3");

        String numberofchargebacks_6monthsago= request.getParameter("numberofchargebacks_6monthsago");
        String numberofchargebacks_5monthsago= request.getParameter("numberofchargebacks_5monthsago");
        String numberofchargebacks_4monthsago= request.getParameter("numberofchargebacks_4monthsago");
        String numberofchargebacks_3monthsago= request.getParameter("numberofchargebacks_3monthsago");
        String numberofchargebacks_2monthsago= request.getParameter("numberofchargebacks_2monthsago");
        String numberofchargebacks_lastmonth= request.getParameter("numberofchargebacks_lastmonth");
        String numberofchargebacks_12monthsago= request.getParameter("numberofchargebacks_12monthsago");
        String numberofchargebacks_year2= request.getParameter("numberofchargebacks_year2");
        String numberofchargebacks_year3= request.getParameter("numberofchargebacks_year3");

        String refundsvolume_6monthsago=request.getParameter("refundsvolume_6monthsago");
        String refundsvolume_5monthsago=request.getParameter("refundsvolume_5monthsago");
        String refundsvolume_4monthsago=request.getParameter("refundsvolume_4monthsago");
        String refundsvolume_3monthsago=request.getParameter("refundsvolume_3monthsago");
        String refundsvolume_2monthsago=request.getParameter("refundsvolume_2monthsago");
        String refundsvolume_lastmonth=request.getParameter("refundsvolume_lastmonth");
        String refundsvolume_12monthsago=request.getParameter("refundsvolume_12monthsago");
        String refundsvolume_year2=request.getParameter("refundsvolume_year2");
        String refundsvolume_year3= request.getParameter("refundsvolume_year3");

        String numberofrefunds_6monthsago= request.getParameter("numberofrefunds_6monthsago");
        String numberofrefunds_5monthsago= request.getParameter("numberofrefunds_5monthsago");
        String numberofrefunds_4monthsago= request.getParameter("numberofrefunds_4monthsago");
        String numberofrefunds_3monthsago= request.getParameter("numberofrefunds_3monthsago");
        String numberofrefunds_2monthsago= request.getParameter("numberofrefunds_2monthsago");
        String numberofrefunds_lastmonth= request.getParameter("numberofrefunds_lastmonth");
        String numberofrefunds_12monthsago= request.getParameter("numberofrefunds_12monthsago");
        String numberofrefunds_year2= request.getParameter("numberofrefunds_year2");
        String numberofrefunds_year3= request.getParameter("numberofrefunds_year3");

          //Manager instance
        AppRequestManager appRequestManager = new AppRequestManager();
        ApplicationManager applicationManager = new ApplicationManager();
        //FileManager fileManager = new FileManager();
        //Vo instance
        ApplicationManagerVO applicationManagerVO=null;
        ApplicationManagerVO databaseApplicationManagerVO=null;
        NavigationVO navigationVO =null;
        AppValidationVO appValidationVO =null;
        CompanyProfileVO companyProfileVO = null;
        ErrorCodeVO errorCodeVO = null;
        ErrorCodeUtils errorCodeUtils=null;
        //Collection
        Map<Integer, Map<Boolean, Set<BankInputName>>> fullValidationForStep=new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();
        Map<Integer, Map<Boolean, Set<BankInputName>>> dependencyFullValidationForStep=new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();
        Map<Integer, Map<Boolean, Set<BankInputName>>> dependencyOtherFullValidationForStep=new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();
        Map<Boolean,Set<BankInputName>> dependencyPageViseValidation=new HashMap<Boolean, Set<BankInputName>>();
        Map<Boolean,Set<BankInputName>> dependencyOtherPageViseValidation=new HashMap<Boolean, Set<BankInputName>>();
        Map<Integer,Set<BankInputName>> otherValidation=new HashMap<Integer, Set<BankInputName>>();
        Set<BankInputName> otherValidationPageVise=new HashSet<BankInputName>();
        //ValidationErrorList instance
        ValidationErrorList validationErrorList = null;
        response.setHeader("X-Frame-Options", "ALLOWALL");
        session.setAttribute("X-Frame-Options", "ALLOWALL");
         RequestDispatcher rdSuccess = request.getRequestDispatcher("/appNavigation.jsp?MES=Success&ctoken="+user.getCSRFToken());
        RequestDispatcher rdError= request.getRequestDispatcher("/appNavigation.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        RequestDispatcher rdSubmitsucess= request.getRequestDispatcher("/appThankYou.jsp?&ctoken="+user.getCSRFToken());
        try
        {

            //getting applicationManagerVO & navigationVO
            applicationManagerVO=appRequestManager.getApplicationManagerVO(session);
            navigationVO=appRequestManager.getNavigationVO(session, Module.MERCHANT);
            appValidationVO=appRequestManager.getAppValidationVO(session);
            applicationManagerVO.setUser(Module.MERCHANT.name());

            databaseApplicationManagerVO=applicationManagerVO;


// Validation for step 1
            if (!ESAPI.validator().isValidInput("corporate_addressId", companyprofile_address_id1, "Description", 50, isOptional) || functions.hasHTMLTags(companyprofile_address_id1))
            {
                log.debug("Invalid corporate_addressId");
                sErrorMessage.append("corporate_addressId should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("merchantname", companyprofile_name1, "Description", 50, false) || functions.hasHTMLTags(companyprofile_name1))
            {
                log.debug("Invalid Name");
                sErrorMessage.append("Street should be in String"+ EOL);
            }
            if (!ESAPI.validator().isValidInput("merchant_addressproof", companyprofile_address_proof, "Description", 50, false) || functions.hasHTMLTags(companyprofile_address_proof))
            {
                log.debug("Invalid merchantaddressproof");
                sErrorMessage.append("Street should be in String" + EOL);
            }
            if (!ESAPI.validator().isValidInput("corporatestreet", companyprofile_street, "Description", 50, isOptional) || functions.hasHTMLTags(companyprofile_street))
            {
                log.debug("Invalid Street Name");
                sErrorMessage.append("Street should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("corporate_addressproof", companyprofile_address_proof1, "Description", 50, false) || functions.hasHTMLTags(companyprofile_address_proof1))
            {
                log.debug("Invalid corporate addressproof");
                sErrorMessage.append("corporatename should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("SkypeIMaddress", companyprofile_im_address, "Description", 50, isOptional) || functions.hasHTMLTags(companyprofile_im_address))
            {
                log.debug("Invalid SkypeIMaddress");
                sErrorMessage.append("SkypeIMaddress should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("corporatename", companyprofile_business_name, "Description", 50, false) || functions.hasHTMLTags(companyprofile_business_name))
            {
                log.debug("Invalid corporatename");
                sErrorMessage.append("Corporatename should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("corporateaddress", companyprofile_billing_address, "Description", 50, isOptional) || functions.hasHTMLTags(companyprofile_billing_address))
            {
                log.debug("Invalid name");
                sErrorMessage.append("Name should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("corporatezipcode", companyprofile_zip1, "Description", 50, isOptional) || functions.hasHTMLTags(companyprofile_zip1))
            {
                log.debug("Invalid zip code");
                sErrorMessage.append("zip code should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("registered_corporatename", companyprofile_registered_corporate, "Description", 50, isOptional) || functions.hasHTMLTags(companyprofile_registered_corporate))
            {
                log.debug("Invalid registered_corporatename");
                sErrorMessage.append("registered_corporatename should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("registered_directors_street", companyprofile_street2, "Description", 50, isOptional) || functions.hasHTMLTags(companyprofile_street2))
            {
                log.debug("Invalid registered_directors_street");
                sErrorMessage.append("registered_directors_street should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("EURegistrationNumber", companyprofile_registered_number, "Description", 50, isOptional) || functions.hasHTMLTags(companyprofile_registered_number))
            {
                log.debug("Invalid EURegistrationNumber");
                sErrorMessage.append("EURegistrationNumber should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("contactname", companyprofile_name_relationship, "Description", 50, isOptional) || functions.hasHTMLTags(companyprofile_name_relationship))
            {
                log.debug("Invalid name");
                sErrorMessage.append("Name should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("contactemailaddress", companyprofile_email2, "Description", 50, isOptional) || functions.hasHTMLTags(companyprofile_email2))
            {
                log.debug("Invalid email");
                sErrorMessage.append("Email should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("contact_designation", companyprofile_designation, "Description", 50, isOptional) || functions.hasHTMLTags(companyprofile_designation))
            {
                log.debug("Invalid contact_designation");
                sErrorMessage.append("contact_designation should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("technicalcontactname", companyprofile_name_relationship1, "Description", 50, isOptional) || functions.hasHTMLTags(companyprofile_name_relationship1))
            {
                log.debug("Invalid name");
                sErrorMessage.append("Name should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("technicalemailaddress", companyprofile_email3, "Description", 50, isOptional) || functions.hasHTMLTags(companyprofile_email3))
            {
                log.debug("Invalid email");
                sErrorMessage.append("Email should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("technical_designation", companyprofile_designation4, "Description", 50, isOptional) || functions.hasHTMLTags(companyprofile_designation4))
            {
                log.debug("Invalid technical_designation");
                sErrorMessage.append("Designation should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("billingcontactname", companyprofile_name3, "Description", 50, isOptional) || functions.hasHTMLTags(companyprofile_name3))
            {
                log.debug("Invalid Name");
                sErrorMessage.append("Name should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("billingemailaddress", companyprofile_email5, "Description", 50, isOptional) || functions.hasHTMLTags(companyprofile_email5))
            {
                log.debug("Invalid mail");
                sErrorMessage.append("Mail should be in String"+ EOL);
            }

//Validation for step 2

            if (!ESAPI.validator().isValidInput("shareholderprofile2_address", shareholderprofile2_address, "Description", 50, false) || functions.hasHTMLTags(shareholderprofile2_address))
            {
                log.debug("Invalid shareholderprofile2_address");
                sErrorMessage.append("shareholderprofile2_address should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("shareholderprofile2_street", shareholderprofile2_street, "Description", 50, false)|| functions.hasHTMLTags(shareholderprofile2_street))
            {
                log.debug("Invalid Streetname");
                sErrorMessage.append("Streetname should be in String" + EOL);
            }

            if (!ESAPI.validator().isValidInput("corporateshareholder1_Address", corporateshareholder1_Address, "Description", 50, false) || functions.hasHTMLTags(corporateshareholder1_Address))
            {
                log.debug("Invalid Address");
                sErrorMessage.append("Address should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("corporateshareholder1_Street", corporateshareholder1_Street, "Description", 50, false) || functions.hasHTMLTags(corporateshareholder1_Street))
            {
                log.debug("Invalid Streetname");
                sErrorMessage.append("Streetname should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("authorizedsignatoryprofile1_designation", authorizedsignatoryprofile1_designation, "Description", 50, isOptional) || functions.hasHTMLTags(authorizedsignatoryprofile1_designation))
            {
                log.debug("Invalid Designation");
                sErrorMessage.append("Designation should be in String"+ EOL);
            }
            if (!ESAPI.validator().isValidInput("authorizedsignatoryprofile_emailaddress", authorizedsignatoryprofile_emailaddress, "Description", 50, isOptional) || functions.hasHTMLTags(authorizedsignatoryprofile_emailaddress))
            {
                log.debug("Invalid Emailaddress");
                sErrorMessage.append("Emailaddress should be in String"+ EOL);
            }
            if (!ESAPI.validator().isValidInput("authorizedsignatoryprofile_address", authorizedsignatoryprofile_address, "Description", 50, false) || functions.hasHTMLTags(authorizedsignatoryprofile_address))
            {
                log.debug("Invalid Address");
                sErrorMessage.append("Address should be in String"+ EOL);
            }
            if (!ESAPI.validator().isValidInput("authorizedsignatoryprofile_street", authorizedsignatoryprofile_street, "Description", 50, false) || functions.hasHTMLTags(authorizedsignatoryprofile_street))
            {
                log.debug("Invalid authorizedsignatoryprofile_street");
                sErrorMessage.append("authorizedsignatoryprofile_street should be in String"+ EOL);
            }
//Validation for step 3
            if (!ESAPI.validator().isValidInput("shoppingcart_address", shoppingcart_address, "Description", 50, isOptional) || functions.hasHTMLTags(shoppingcart_address))
            {
                log.debug("Invalid address");
                sErrorMessage.append("Address should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("callcenter_address", callcenter_address, "Description", 50, isOptional) || functions.hasHTMLTags(callcenter_address))
            {
                log.debug("Invalid address");
                sErrorMessage.append("Address should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("shoppingcart_email", shoppingcart_email, "Description", 50, isOptional) || functions.hasHTMLTags(shoppingcart_email))
            {
                log.debug("Invalid shoppingcart email");
                sErrorMessage.append("Shoppingcart email should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("shoppingcart_company_name", shoppingcart_company_name, "Description", 50, isOptional) || functions.hasHTMLTags(shoppingcart_company_name))
            {
                log.debug("Invalid shoppingcart company name");
                sErrorMessage.append("Shoppingcart company name should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("isacallcenterusedyes", isacallcenterusedyes, "Description", 50, isOptional) || functions.hasHTMLTags(isacallcenterusedyes))
            {
                log.debug("Invalid isacallcenterusedyes");
                sErrorMessage.append("Isacallcenterusedyes should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("payment_address", payment_address, "Description", 50, isOptional) || functions.hasHTMLTags(payment_address))
            {
                log.debug("Invalid address");
                sErrorMessage.append("Address should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("payment_company_name", payment_company_name, "Description", 50, isOptional) || functions.hasHTMLTags(payment_company_name))
            {
                log.debug("Invalid Company name");
                sErrorMessage.append("Company name should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("webhost_company_name", webhost_company_name, "Description", 50, isOptional) || functions.hasHTMLTags(webhost_company_name))
            {
                log.debug("Invalid company name");
                sErrorMessage.append("Company name should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("inhouselocation", inhouselocation, "Description", 50, isOptional) || functions.hasHTMLTags(inhouselocation))
            {
                log.debug("Invalid inhouselocation");
                sErrorMessage.append("Inhouselocation should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("otherlocation", otherlocation, "Description", 50, isOptional) || functions.hasHTMLTags(otherlocation))
            {
                log.debug("Invalid otherlocation");
                sErrorMessage.append("otherlocation name should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("mainsuppliers", mainsuppliers, "Description", 50, isOptional) || functions.hasHTMLTags(mainsuppliers))
            {
                log.debug("Invalid mainsuppliers");
                sErrorMessage.append("mainsuppliers should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("isafulfillmenthouseused", isafulfillmenthouseused, "Description", 50, isOptional) || functions.hasHTMLTags(isafulfillmenthouseused))
            {
                log.debug("Invalid isafulfillmenthouseused");
                sErrorMessage.append("Isafulfillmenthouseused should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("coolingoffperiod", coolingoffperiod, "Description", 50, isOptional) || functions.hasHTMLTags(coolingoffperiod))
            {
                log.debug("Invalid coolingoffperiod");
                sErrorMessage.append("coolingoffperiod should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("timeframe", timeframe , "Description", 50, isOptional) || functions.hasHTMLTags(timeframe))
            {
                log.debug("Invalid timeframe");
                sErrorMessage.append("timeframe should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("direct_debit_sepa", direct_debit_sepa, "Description", 50, isOptional) || functions.hasHTMLTags(direct_debit_sepa))
            {
                log.debug("Invalid direct_debit_sepa");
                sErrorMessage.append("direct_debit_sepa should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("creditor_id", creditor_id, "Description", 50, isOptional) || functions.hasHTMLTags(creditor_id))
            {
                log.debug("Invalid creditor_id");
                sErrorMessage.append("creditor_id should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("alternative_payments", alternative_payments, "Description", 50, isOptional) || functions.hasHTMLTags(alternative_payments))
            {
                log.debug("Invalid alternative_payments");
                sErrorMessage.append("Alternative payments should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("risk_management", risk_management, "Description", 50, isOptional) || functions.hasHTMLTags(risk_management))
            {
                log.debug("Invalid risk_management");
                sErrorMessage.append("risk_management should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("payment_engine", payment_engine, "Description", 50, isOptional) || functions.hasHTMLTags(payment_engine))
            {
                log.debug("Invalid payment engine");
                sErrorMessage.append("payment engine should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("countries_blocked", countries_blocked, "Description", 50, isOptional) || functions.hasHTMLTags(countries_blocked))
            {
                log.debug("Invalid countries_blocked");
                sErrorMessage.append("countries_blocked name should be in String"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("pricing_policies_website", pricing_policies_website, "Description", 50, isOptional) || functions.hasHTMLTags(pricing_policies_website))
            {
                log.debug("Invalid pricing_policies_website");
                sErrorMessage.append("pricing_policies_website should be in String"+ EOL);
            }


//Validation for step 4
            if (!ESAPI.validator().isValidInput("bankinfo_bankaddress", bankinfo_bankaddress, "Description", 50, isOptional) || functions.hasHTMLTags(bankinfo_bankaddress))
            {
                log.debug("Invalid bank address");
                sErrorMessage.append("Bank address should be in String"+ EOL);
            }

            // validations for sales volume
            if (!ESAPI.validator().isValidInput("salesvolume_6monthsago", salesvolume_6monthsago, "Numbers", 50, isOptional) || functions.hasHTMLTags(salesvolume_6monthsago))
            {
                log.debug("Invalid salesvolume");
                sErrorMessage.append("Sales volume should be in Number"+ EOL);
            }
            if (!ESAPI.validator().isValidInput("salesvolume_5monthsago", salesvolume_5monthsago, "Numbers", 50, isOptional) || functions.hasHTMLTags(salesvolume_5monthsago))
            {
                log.debug("Invalid salesvolume");
                sErrorMessage.append("Sales volume should be in Number"+ EOL);
            }
            if (!ESAPI.validator().isValidInput("salesvolume_4monthsago", salesvolume_4monthsago, "Numbers", 50, isOptional) || functions.hasHTMLTags(salesvolume_4monthsago))
            {
                log.debug("Invalid salesvolume");
                sErrorMessage.append("Sales volume should be in Number"+ EOL);
            }
            if (!ESAPI.validator().isValidInput("salesvolume_3monthsago", salesvolume_3monthsago, "Numbers", 50, isOptional) || functions.hasHTMLTags(salesvolume_3monthsago))
            {
                log.debug("Invalid salesvolume");
                sErrorMessage.append("Sales volume should be in Number"+ EOL);
            }
            if (!ESAPI.validator().isValidInput("salesvolume_2monthsago", salesvolume_2monthsago, "Numbers", 50, isOptional) || functions.hasHTMLTags(salesvolume_2monthsago))
            {
                log.debug("Invalid salesvolume");
                sErrorMessage.append("Sales volume should be in Number"+ EOL);
            }
            if (!ESAPI.validator().isValidInput("salesvolume_lastmonth", salesvolume_lastmonth, "Numbers", 50, isOptional) || functions.hasHTMLTags(salesvolume_lastmonth))
            {
                log.debug("Invalid salesvolume");
                sErrorMessage.append("Sales volume should be in Number"+ EOL);
            }

            if (!ESAPI.validator().isValidInput("salesvolume_12monthsago", salesvolume_12monthsago, "Numbers", 50, isOptional) || functions.hasHTMLTags(salesvolume_12monthsago))
            {
                log.debug("Invalid salesvolume");
                sErrorMessage.append("Sales volume should be in Number" + EOL);
            }
            if (!ESAPI.validator().isValidInput("salesvolume_year2", salesvolume_year2, "Numbers", 50, isOptional) || functions.hasHTMLTags(salesvolume_year2))
            {
                log.debug("Invalid salesvolume");
                sErrorMessage.append("Sales volume should be in Number"+ EOL);
            }
            if (!ESAPI.validator().isValidInput("salesvolume_year3", salesvolume_year3, "Numbers", 50, isOptional) || functions.hasHTMLTags(salesvolume_year3))
            {
                 log.debug("Invalid salesvolume");
                 sErrorMessage.append("Sales volume should be in Number"+ EOL);
            }

            // validations for number of transactions
            if (!ESAPI.validator().isValidInput("numberoftransactions_6monthsago", numberoftransactions_6monthsago, "Numbers", 50, isOptional) || functions.hasHTMLTags(numberoftransactions_6monthsago))
            {
                log.debug("Invalid number of transactions");
                sErrorMessage.append("Number of transactions should be in number"+ EOL);
            }
            if (!ESAPI.validator().isValidInput("numberoftransactions_5monthsago", numberoftransactions_5monthsago, "Numbers", 50, isOptional) || functions.hasHTMLTags(numberoftransactions_5monthsago))
            {
                log.debug("Invalid number of transactions");
                sErrorMessage.append("Number of transactions should be in number"+ EOL);
            }
            if (!ESAPI.validator().isValidInput("numberoftransactions_4monthsago",numberoftransactions_4monthsago,"Numbers",50, isOptional) || functions.hasHTMLTags(numberoftransactions_4monthsago))
            {
                log.debug("Invalid number of transactions");
                sErrorMessage.append("Number of transactions should be in number"+ EOL);
            }
            if (!ESAPI.validator().isValidInput("numberoftransactions_3monthsago",numberoftransactions_3monthsago,"Numbers",50, isOptional) || functions.hasHTMLTags(numberoftransactions_3monthsago))
            {
                log.debug("Invalid number of transactions");
                sErrorMessage.append("Number of transactions should be in number"+ EOL);
            }
            if (!ESAPI.validator().isValidInput("numberoftransactions_2monthsago",numberoftransactions_2monthsago,"Numbers",50, isOptional) || functions.hasHTMLTags(numberoftransactions_2monthsago))
            {
                log.debug("Invalid number of transactions");
                sErrorMessage.append("Number of transactions should be in number"+ EOL);
            }
            if (!ESAPI.validator().isValidInput("numberoftransactions_lastmonth",numberoftransactions_lastmonth,"Numbers",50, isOptional) || functions.hasHTMLTags(numberoftransactions_lastmonth))
            {
                log.debug("Invalid number of transactions");
                sErrorMessage.append("Number of transactions should be in number"+ EOL);
            }
            if (!ESAPI.validator().isValidInput("numberoftransactions_12monthsago",numberoftransactions_12monthsago,"Numbers",50,isOptional) || functions.hasHTMLTags(numberoftransactions_12monthsago))
            {
                log.debug("Invalid number of transactions");
                sErrorMessage.append("Number of transactions should be in number"+ EOL);
            }
            if (!ESAPI.validator().isValidInput("numberoftransactions_year2",numberoftransactions_year2,"Numbers",50, isOptional) || functions.hasHTMLTags(numberoftransactions_year2))
            {
                log.debug("Invalid number of transactions");
                sErrorMessage.append("Number of transactions should be in number"+ EOL);
            }
            if (!ESAPI.validator().isValidInput("numberoftransactions_year3",numberoftransactions_year3,"Numbers",50, isOptional) || functions.hasHTMLTags(numberoftransactions_year3))
            {
                log.debug("Invalid number of transactions");
                sErrorMessage.append("Number of transactions should be in number"+ EOL);
            }

            //validations for chargeback volume
            if (!ESAPI.validator().isValidInput("chargebackvolume_6monthsago",chargebackvolume_6monthsago,"Numbers",50, isOptional) || functions.hasHTMLTags(chargebackvolume_6monthsago))
            {
                log.debug("Invalid chargebackvolume");
                sErrorMessage.append("Chargeback volume should be in number"+ EOL);
            }
            if (!ESAPI.validator().isValidInput("chargebackvolume_5monthsago",chargebackvolume_5monthsago,"Numbers",50, isOptional) || functions.hasHTMLTags(chargebackvolume_5monthsago))
            {
                log.debug("Invalid chargebackvolume");
                sErrorMessage.append("Chargebackvolume should be in number"+ EOL);
            }
            if (!ESAPI.validator().isValidInput("chargebackvolume_4monthsago",chargebackvolume_4monthsago,"Numbers",50, isOptional) || functions.hasHTMLTags(chargebackvolume_4monthsago))
            {
                log.debug("Invalid chargebackvolume");
                sErrorMessage.append("Chargebackvolume should be in number"+ EOL);
            }
            if (!ESAPI.validator().isValidInput("chargebackvolume_3monthsago",chargebackvolume_3monthsago,"Numbers", 50, isOptional) || functions.hasHTMLTags(chargebackvolume_3monthsago))
            {
                log.debug("Invalid chargebackvolume");
                sErrorMessage.append("Chargebackvolume should be in number"+ EOL);
            }
            if (!ESAPI.validator().isValidInput("chargebackvolume_2monthsago",chargebackvolume_2monthsago,"Numbers", 50, isOptional) || functions.hasHTMLTags(chargebackvolume_2monthsago))
            {
                log.debug("Invalid chargebackvolume");
                sErrorMessage.append("Chargebackvolume should be in number"+ EOL);
            }
            if (!ESAPI.validator().isValidInput("chargebackvolume_lastmonth",chargebackvolume_lastmonth,"Numbers",50, isOptional) || functions.hasHTMLTags(chargebackvolume_lastmonth))
            {
                log.debug("Invalid chargebackvolume");
                sErrorMessage.append("Chargebackvolume should be in number"+ EOL);
            }
            if (!ESAPI.validator().isValidInput("chargebackvolume_12monthsago",chargebackvolume_12monthsago,"Numbers",50, isOptional) || functions.hasHTMLTags(chargebackvolume_12monthsago))
            {
                log.debug("Invalid chargebackvolume");
                sErrorMessage.append("Chargebackvolume should be in number"+ EOL);
            }
            if (!ESAPI.validator().isValidInput("chargebackvolume_year2",chargebackvolume_year2,"Numbers",50,isOptional) || functions.hasHTMLTags(chargebackvolume_year2))
            {
                log.debug("Invalid chargebackvolume");
                sErrorMessage.append("Chargebackvolume should be in number"+ EOL);
            }
            if (!ESAPI.validator().isValidInput("chargebackvolume_year3",chargebackvolume_year3,"Numbers",50,isOptional) || functions.hasHTMLTags(chargebackvolume_year3))
            {
                log.debug("Invalid chargebackvolume");
                sErrorMessage.append("Chargebackvolume should be in number"+ EOL);
            }

            // validations for Number of chargebacks
            if(!ESAPI.validator().isValidInput("numberofchargebacks_6monthsago",numberofchargebacks_6monthsago,"Numbers",50,isOptional) || functions.hasHTMLTags(numberofchargebacks_6monthsago))
            {
                log.debug("Invalid numberofchargebacks");
                sErrorMessage.append("Number of chargebacks should be in number"+EOL);
            }
            if(!ESAPI.validator().isValidInput("numberofchargebacks_5monthsago",numberofchargebacks_5monthsago,"Numbers",50,isOptional) || functions.hasHTMLTags(numberofchargebacks_5monthsago))
            {
                log.debug("Invalid numberofchargebacks");
                sErrorMessage.append("Number of chargebacks should be in number"+EOL);
            }
            if(!ESAPI.validator().isValidInput("numberofchargebacks_4monthsago",numberofchargebacks_4monthsago,"Numbers",50,isOptional) || functions.hasHTMLTags(numberofchargebacks_4monthsago))
            {
                log.debug("Invalid numberofchargebacks");
                sErrorMessage.append("Number of chargebacks should be in number"+EOL);
            }
            if(!ESAPI.validator().isValidInput("numberofchargebacks_3monthsago",numberofchargebacks_3monthsago,"Numbers",50,isOptional) || functions.hasHTMLTags(numberofchargebacks_3monthsago))
            {
                log.debug("Invalid numberofchargebacks");
                sErrorMessage.append("Number of chargebacks should be in number"+EOL);
            }
            if(!ESAPI.validator().isValidInput("numberofchargebacks_2monthsago",numberofchargebacks_2monthsago,"Numbers",50,isOptional) || functions.hasHTMLTags(numberofchargebacks_2monthsago))
            {
                log.debug("Invalid numberofchargebacks");
                sErrorMessage.append("Number of chargebacks should be in number"+EOL);
            }
            if(!ESAPI.validator().isValidInput("numberofchargebacks_lastmonth",numberofchargebacks_lastmonth,"Numbers",50,isOptional) || functions.hasHTMLTags(numberofchargebacks_lastmonth))
            {
                log.debug("Invalid numberofchargebacks");
                sErrorMessage.append("Number of chargebacks should be in number"+EOL);
            }
            if(!ESAPI.validator().isValidInput("numberofchargebacks_12monthsago",numberofchargebacks_12monthsago,"Numbers",50,isOptional) || functions.hasHTMLTags(numberofchargebacks_12monthsago))
            {
                log.debug("Invalid numberofchargebacks");
                sErrorMessage.append("Number of chargebacks should be in number"+EOL);
            }
            if(!ESAPI.validator().isValidInput("numberofchargebacks_year2",numberofchargebacks_year2,"Numbers",50,isOptional) || functions.hasHTMLTags(numberofchargebacks_year2))
            {
                log.debug("Invalid numberofchargebacks");
                sErrorMessage.append("Number of chargebacks should be in number"+EOL);
            }
            if(!ESAPI.validator().isValidInput("numberofchargebacks_year3",numberofchargebacks_year3,"Numbers",50,isOptional) || functions.hasHTMLTags(numberofchargebacks_year3))
            {
                log.debug("Invalid numberofchargebacks");
                sErrorMessage.append("Number of chargebacks should be in number"+EOL);
            }

            // validations for refunds volume
            if(!ESAPI.validator().isValidInput("refundsvolume_6monthsago",refundsvolume_6monthsago,"Numbers",50,isOptional) || functions.hasHTMLTags(refundsvolume_6monthsago))
            {
                log.debug("Invalid refundsvolume");
                sErrorMessage.append("Refund volume should be in number"+EOL);
            }
            if(!ESAPI.validator().isValidInput("refundsvolume_5monthsago",refundsvolume_5monthsago,"Numbers",50,isOptional) || functions.hasHTMLTags(refundsvolume_5monthsago))
            {
                log.debug("Invalid refundsvolume");
                sErrorMessage.append("Refund volume should be in number"+EOL);
            }
            if(!ESAPI.validator().isValidInput("refundsvolume_4monthsago",refundsvolume_4monthsago,"Numbers",50,isOptional) || functions.hasHTMLTags(refundsvolume_4monthsago))
            {
                log.debug("Invalid refundsvolume");
                sErrorMessage.append("Refund volume should be in number"+EOL);
            }
            if(!ESAPI.validator().isValidInput("refundsvolume_3monthsago",refundsvolume_3monthsago,"Numbers",50,isOptional) || functions.hasHTMLTags(refundsvolume_3monthsago))
            {
                log.debug("Invalid refundsvolume");
                sErrorMessage.append("Refund volume should be in number"+EOL);
            }
            if(!ESAPI.validator().isValidInput("refundsvolume_2monthsago",refundsvolume_2monthsago,"Numbers",50,isOptional) || functions.hasHTMLTags(refundsvolume_2monthsago))
            {
                log.debug("Invalid refundsvolume");
                sErrorMessage.append("Refund volume should be in number"+EOL);
            }
            if(!ESAPI.validator().isValidInput("refundsvolume_lastmonth",refundsvolume_lastmonth,"Numbers",50,isOptional) || functions.hasHTMLTags(refundsvolume_lastmonth))
            {
                log.debug("Invalid refundsvolume");
                sErrorMessage.append("Refund volume should be in number"+EOL);
            }
            if(!ESAPI.validator().isValidInput("refundsvolume_12monthsago",refundsvolume_12monthsago,"Numbers",50,isOptional) || functions.hasHTMLTags(refundsvolume_12monthsago))
            {
                log.debug("Invalid refundsvolume");
                sErrorMessage.append("Refund volume should be in number"+EOL);
            }
            if(!ESAPI.validator().isValidInput("refundsvolume_year2",refundsvolume_year2,"Numbers",50,isOptional) || functions.hasHTMLTags(refundsvolume_year2))
            {
                log.debug("Invalid refundsvolume");
                sErrorMessage.append("Refund volume should be in number"+EOL);
            }
            if(!ESAPI.validator().isValidInput("refundsvolume_year3",refundsvolume_year3,"Numbers",50,isOptional) || functions.hasHTMLTags(refundsvolume_year3))
            {
                log.debug("Invalid refundsvolume");
                sErrorMessage.append("Refund volume should be in number"+EOL);
            }

            //  validations for number of refunds
            if(!ESAPI.validator().isValidInput("numberofrefunds_6monthsago",numberofrefunds_6monthsago,"Numbers",50,isOptional) || functions.hasHTMLTags(numberofrefunds_6monthsago))
            {
                log.debug("Invalid number of refunds");
                sErrorMessage.append("Number of refunds should be in number"+EOL);
            }
            if(!ESAPI.validator().isValidInput("numberofrefunds_5monthsago",numberofrefunds_5monthsago,"Numbers",50,isOptional) || functions.hasHTMLTags(numberofrefunds_5monthsago))
            {
                log.debug("Invalid number of refunds");
                sErrorMessage.append("Number of refunds should be in number"+EOL);
            }
            if(!ESAPI.validator().isValidInput("numberofrefunds_4monthsago",numberofrefunds_4monthsago,"Numbers",50,isOptional) || functions.hasHTMLTags(numberofrefunds_4monthsago))
            {
                log.debug("Invalid number of refunds");
                sErrorMessage.append("Number of refunds should be in number"+EOL);
            }
            if(!ESAPI.validator().isValidInput("numberofrefunds_3monthsago",numberofrefunds_3monthsago,"Numbers",50,isOptional) || functions.hasHTMLTags(numberofrefunds_3monthsago))
            {
                log.debug("Invalid number of refunds");
                sErrorMessage.append("Number of refunds should be in number"+EOL);
            }
            if(!ESAPI.validator().isValidInput("numberofrefunds_2monthsago",numberofrefunds_2monthsago,"Numbers",50,isOptional) || functions.hasHTMLTags(numberofrefunds_2monthsago))
            {
                log.debug("Invalid number of refunds");
                sErrorMessage.append("Number of refunds should be in number"+EOL);
            }
            if(!ESAPI.validator().isValidInput("numberofrefunds_lastmonth",numberofrefunds_lastmonth,"Numbers",50,isOptional) || functions.hasHTMLTags(numberofrefunds_lastmonth))
            {
                log.debug("Invalid number of refunds");
                sErrorMessage.append("Number of refunds should be in number"+EOL);
            }
            if(!ESAPI.validator().isValidInput("numberofrefunds_12monthsago",numberofrefunds_12monthsago,"Numbers",50,isOptional) || functions.hasHTMLTags(numberofrefunds_12monthsago))
            {
                log.debug("Invalid number of refunds");
                sErrorMessage.append("Number of refunds should be in number"+EOL);
            }
            if(!ESAPI.validator().isValidInput("numberofrefunds_year2",numberofrefunds_year2,"Numbers",50,isOptional) || functions.hasHTMLTags(numberofrefunds_year2))
            {
                log.debug("Invalid number of refunds");
                sErrorMessage.append("Number of refunds should be in number"+EOL);
            }
            if(!ESAPI.validator().isValidInput("numberofrefunds_year3",numberofrefunds_year3,"Numbers",50,isOptional) || functions.hasHTMLTags(numberofrefunds_year3))
            {
                log.debug("Invalid number of refunds");
                sErrorMessage.append("Number of refunds should be in number"+EOL);
            }

            System.out.println("save action here" + request.getParameter("save") +"::::::::;;"+ request.getAttribute("request"));

            if(functions.isValueNull(request.getParameter("next")) || functions.isValueNull(request.getParameter("previous")) || functions.isValueNull(request.getParameter("save")) || functions.isValueNull(request.getParameter("appsubmit")) || functions.isValueNull(request.getParameter("apptab")))
            {
                if(functions.isValueNull(request.getParameter("next")))
                {
                    navigationVO.setNextPageNO(Integer.valueOf(request.getParameter("next")));

                }
                else if(functions.isValueNull(request.getParameter("previous")))
                {

                    navigationVO.setPreviousPageNO(Integer.valueOf(request.getParameter("previous")));

                }
                else if(functions.isValueNull(request.getParameter("save")))
                {
                    logger.debug("currentPageNo::"+request.getParameter("save"));
                    navigationVO.setCurrentPageNO(Integer.valueOf(request.getParameter("save")));
                }
                else if(functions.isValueNull(request.getParameter("appsubmit")))
                {
                    //no page info to be added
                }
                else if(functions.isValueNull(request.getParameter("apptab")))//this is for side bar
                {
                    navigationVO.setCurrentPageNO(Integer.valueOf(request.getParameter("currentPage")));
                    logger.debug("currentPage::"+navigationVO.getCurrentPageNO());
                }
                else if(functions.isValueNull((String)session.getAttribute("pageno"))){
                    navigationVO.setCurrentPageNO(Integer.valueOf((String)session.getAttribute("pageno")));
                }

                validationErrorList=appRequestManager.validationForInterfaceOrApi(request,null,navigationVO,applicationManagerVO,false,false,appValidationVO);
                if(session.getAttribute("validationErrorList")!=null)
                {
                    validationErrorList=appRequestManager.compareValidationErrorList(request,(ValidationErrorList)session.getAttribute("validationErrorList"),validationErrorList);

                    session.setAttribute("validationErrorList",validationErrorList);
                }

                //This is to retain the database value for that profile.
                if(validationErrorList!=null && !validationErrorList.isEmpty())
                {
                    appRequestManager.retainCurrentPage(applicationManagerVO,databaseApplicationManagerVO,navigationVO,validationErrorList);
                }
                //after validation Save to the dedicated table
                if(functions.isValueNull(request.getParameter("save")))
                {
                    boolean savedCurrentPage=applicationManager.saveCurrentPage(navigationVO,applicationManagerVO,true,null);
                    applicationManagerVO.setNotificationMessage(savedCurrentPage,applicationManagerVO.getNotificationMessage()," Saved Successfully");
                }
                //this is for all profile to be submitted
                if(functions.isValueNull(request.getParameter("appsubmit")) && (validationErrorList==null || validationErrorList.isEmpty()))
                {
                    logger.debug(" inside save of all profile");
                    boolean submitAllProfile=applicationManager.submitAllProfile(applicationManagerVO,false,false);
                    applicationManagerVO.setNotificationMessage(submitAllProfile,applicationManagerVO.getNotificationMessage()," Submitted Successfully<br> Thank you for choosing us for processing your transactions, Our team will get back to you  once your application is approved.");

                    if(applicationManagerVO.getNotificationMessage()!=null)
                    {
                        rdSubmitsucess.forward(request,response);
                    }
                }
                else if(functions.isValueNull(request.getParameter("appsubmit")) && !validationErrorList.isEmpty())
                {
                    session.setAttribute("validationErrorList",validationErrorList);
                }

            }
            else if(functions.isValueNull(request.getParameter("cancel")))
            {

            }

            applicationManager.setValidationForMember(applicationManagerVO.getMemberId(), navigationVO, fullValidationForStep, dependencyFullValidationForStep,dependencyPageViseValidation,otherValidation,dependencyOtherFullValidationForStep,otherValidationPageVise,dependencyOtherPageViseValidation,appValidationVO);

            request.setAttribute("fullValidationForStep", fullValidationForStep);
            request.setAttribute("dependencyFullValidationForStep", dependencyFullValidationForStep);
            request.setAttribute("otherValidation", otherValidation);
            request.setAttribute("dependencyOtherFullValidationForStep", dependencyOtherFullValidationForStep);
            request.setAttribute("validationErrorList",validationErrorList);
            session.setAttribute("applicationManagerVO",databaseApplicationManagerVO);

            if(validationErrorList!=null && !validationErrorList.isEmpty())
            {
                rdError.forward(request,response);
            }
            else
            {
                rdSuccess.forward(request,response);
            }
        }
        catch(Exception e)
        {
            logger.error("Exception in Navigation Page",e);
        }

    }
}