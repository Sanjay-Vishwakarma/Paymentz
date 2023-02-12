package com.payment.p4.gateway;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.vo.RecurringBillingVO;
import com.payment.Enum.PZProcessType;
import com.payment.PZTransactionStatus;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.p4.vos.p4MainVo.Request;
import com.payment.p4.vos.p4MainVo.Response;
import com.payment.p4.vos.queryBlock.Query;
import com.payment.p4.vos.queryBlock.identificationsBlock.Identifications;
//import com.payment.p4.vos.queryBlock.identificationsBlock.identification1Block.Identification1;
import com.payment.p4.vos.queryBlock.resultSetBlock.ResultSet;
import com.payment.p4.vos.queryBlock.resultSetBlock.resultBlock.Result;
import com.payment.p4.vos.transactionBlock.authorizationBlock.Authorization;
import com.payment.p4.vos.transactionBlock.eventsBlock.Events;
import com.payment.p4.vos.transactionBlock.eventsBlock.eventBlock.Event;
import com.payment.p4.vos.transactionBlock.loginBlock.Login;
import com.payment.p4.vos.queryBlock.periodBlock.Period;
import com.payment.p4.vos.queryBlock.scopeBlock.Scope;
import com.payment.p4.vos.queryBlock.typesBlock.Types;
import com.payment.p4.vos.queryBlock.typesBlock.typeBlock.Type;
import com.payment.p4.vos.transactionBlock.Transaction;
import com.payment.p4.vos.transactionBlock.customerBlock.Customer;
import com.payment.p4.vos.transactionBlock.customerBlock.accountBlock.Account;
import com.payment.p4.vos.transactionBlock.customerBlock.accountBlock.expiryBlock.Expiry;
import com.payment.p4.vos.transactionBlock.customerBlock.addressBlock.Address;
import com.payment.p4.vos.transactionBlock.customerBlock.contactBlock.Contact;
import com.payment.p4.vos.transactionBlock.customerBlock.nameBlock.Name;
import com.payment.p4.vos.transactionBlock.customerBlock.signatureBlock.Signature;
import com.payment.p4.vos.transactionBlock.frontEndBlock.Frontend;
import com.payment.p4.vos.transactionBlock.identificationBlock.Identification;
import com.payment.p4.vos.transactionBlock.loginBlock.Login;
import com.payment.p4.vos.transactionBlock.mandateBlock.Mandate;
import com.payment.p4.vos.transactionBlock.paymentBlock.Payment;
import com.payment.p4.vos.transactionBlock.paymentBlock.clearingBlock.Clearing;
import com.payment.p4.vos.transactionBlock.paymentBlock.presentationBlock.Presentation;
import com.payment.p4.vos.transactionBlock.processingBlock.Processing;
import com.payment.validators.vo.CommonValidatorVO;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by admin on 10/3/2015.
 */
public class P4Utils
{
    private static Logger logger= new Logger(P4Utils.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(P4Utils.class.getName());

    private Functions functions = new Functions();

    private final String FRONTENDURL="FrontEndURL";
    private final String ERORRURL="ErrorURL";
    private final String BACKENDURL="BackEndURL";

    /**
     * Get Request for Online Bank Transfer from system
     * @param commonValidatorVO
     * @return
     */
    public CommRequestVO getRequestForOnlineBankTransfer(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO = new CommRequestVO();

        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();
        CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();

        commMerchantVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount()); //Amount * 100 according to the docs
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());

        commAddressDetailsVO.setLanguage(commonValidatorVO.getAddressDetailsVO().getLanguage());

        commAddressDetailsVO.setLanguage(commonValidatorVO.getAddressDetailsVO().getLanguage());

        commAddressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        commAddressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        commAddressDetailsVO.setBirthdate(commonValidatorVO.getAddressDetailsVO().getBirthdate());
        commAddressDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
        commAddressDetailsVO.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
        commAddressDetailsVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
        commAddressDetailsVO.setState(commonValidatorVO.getAddressDetailsVO().getState());
        commAddressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        commAddressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        commAddressDetailsVO.setIp(commonValidatorVO.getAddressDetailsVO().getIp());
        commAddressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());

        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);
        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);

        return commRequestVO;
    }

    /**
     * Get sepa Request from system Request
     * @param commonValidatorVO
     * @return
     */
    public CommRequestVO getRequestForSEPATransfer(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO = new CommRequestVO();

        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();
        CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();
        CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();

        commMerchantVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount()); //Amount * 100 according to the docs
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());

        commAddressDetailsVO.setLanguage(commonValidatorVO.getAddressDetailsVO().getLanguage());

        commAddressDetailsVO.setLanguage(commonValidatorVO.getAddressDetailsVO().getLanguage());

        commAddressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        commAddressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        commAddressDetailsVO.setBirthdate(commonValidatorVO.getAddressDetailsVO().getBirthdate());
        commAddressDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
        commAddressDetailsVO.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
        commAddressDetailsVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
        commAddressDetailsVO.setState(commonValidatorVO.getAddressDetailsVO().getState());
        commAddressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        commAddressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        commAddressDetailsVO.setIp(commonValidatorVO.getAddressDetailsVO().getIp());
        commAddressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());

        commCardDetailsVO.setMandateId(commonValidatorVO.getCardDetailsVO().getMandateId());
        commCardDetailsVO.setBIC(commonValidatorVO.getCardDetailsVO().getBIC());
        commCardDetailsVO.setIBAN(commonValidatorVO.getCardDetailsVO().getIBAN());

        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);
        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
        commRequestVO.setCardDetailsVO(commCardDetailsVO);

        return commRequestVO;
    }



    public Response sendRequestForOnlineBankTransfer(Request request)
    {
        ClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
        Client client = Client.create(config);
        WebResource service = client.resource(getBaseURI());

        Response response = service.path("api").path("payment").type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).post(Response.class, request);

        return response;
    }

    public Response sendRequestForOnlineBankTransferQuery(Request request)
    {
        ClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
        Client client = Client.create(config);
        WebResource service = client.resource(getBaseURI());

        Response response = service.path("api").path("query").type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).post(Response.class, request);

        return response;
    }

    public Request getDirectDebitRequestForSepaAuthentication(CommRequestVO commRequestVO,String trackingId,String paymentCode,String accountId)
    {

        GatewayAccount gatewayAccount = getGatewayAccount(accountId);

        Request request = new Request();

        Transaction transaction = new Transaction();
        Login login = new Login();
        Identification identification = new Identification();

        List<Payment> payments = new ArrayList<Payment>();
        Payment payment = new Payment();
        Presentation presentation = new Presentation();
        Customer customer = new Customer();
        Signature signature = new Signature();

        Account account = new Account();
        Name name = new Name();
        Address address = new Address();
        Contact contact = new Contact();

        login.setUser(gatewayAccount.getFRAUD_FTP_USERNAME());
        login.setPassword(gatewayAccount.getFRAUD_FTP_PASSWORD());
        login.setProjectID(gatewayAccount.getMerchantId());

        identification.setTransactionID(trackingId);
        identification.setShopperID(trackingId);
        identification.setInvoiceID(trackingId);

        payment.setCode(paymentCode);

        presentation.setAmount(commRequestVO.getTransDetailsVO().getAmount());
        presentation.setCurrency(commRequestVO.getTransDetailsVO().getCurrency());
        presentation.setUsage("Demopayment");

        payment.setPresentation(presentation);

        account.setBank("370400");
        account.setNumber("5320130");
        account.setCountry(commRequestVO.getAddressDetailsVO().getCountry());
        account.setBankName("WorldBank");
        account.setBIC("MARKDEF1100");
        account.setIBAN("DE23100000001234567890");

        name.setGiven(commRequestVO.getAddressDetailsVO().getFirstname());
        name.setFamily(commRequestVO.getAddressDetailsVO().getLastname());
        name.setSex("M");

        address.setStreet(commRequestVO.getAddressDetailsVO().getStreet());
        address.setZip(commRequestVO.getAddressDetailsVO().getZipCode());
        address.setCity(commRequestVO.getAddressDetailsVO().getCity());
        address.setCountry(commRequestVO.getAddressDetailsVO().getCountry());

        contact.setEmail(commRequestVO.getAddressDetailsVO().getEmail());

        signature.setEmpty("empty");

        customer.setAccount(account);
        customer.setName(name);
        customer.setAddress(address);
        customer.setContact(contact);
        customer.setSignature(signature);

        transaction.setMode("CONNECTOR_TEST");
        transaction.setResponse("XML");

        transaction.setLogin(login);
        transaction.setIdentification(identification);
        payments.add(payment);
        transaction.setPayment(payments);
        transaction.setCustomer(customer);

        request.setVersion("1.0");

        request.setTransaction(transaction);
        return request;
    }

    public Response sendRequestForDirectDebitTransfer(Request request)
    {
        ClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
        Client client = Client.create(config);
        WebResource service = client.resource(getBaseURI());

        Response response = service.path("api").path("payment").type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).post(Response.class, request);

        return response;
    }

    public Request getDirectDebitRequestForSepaMandate(CommRequestVO commRequestVO,String trackingId,String paymentCode,String accountId)
    {
        String connectorMode="CONNECTOR_TEST";
        String recurrence="";

        RecurringBillingVO recurringBillingVO=new RecurringBillingVO();

        if(commRequestVO.getRecurringBillingVO()!=null)
        {
            recurringBillingVO=commRequestVO.getRecurringBillingVO();
        }


        GatewayAccount gatewayAccount = getGatewayAccount(accountId);

        Request request = new Request();

        Transaction transaction = new Transaction();
        Login login = new Login();
        Identification identification = new Identification();

        List<Payment> payments = new ArrayList<Payment>();
        Payment payment = new Payment();
        Presentation presentation = new Presentation();
        Customer customer = new Customer();
        Signature signature = new Signature();
        Mandate mandate = new Mandate();
        Authorization authorization = new Authorization();
        Frontend frontend = new Frontend();

        Account account = new Account();
        Name name = new Name();
        Address address = new Address();
        Contact contact = new Contact();

        login.setUser(gatewayAccount.getFRAUD_FTP_USERNAME());
        login.setPassword(gatewayAccount.getFRAUD_FTP_PASSWORD());
        login.setProjectID(gatewayAccount.getMerchantId());

        identification.setTransactionID(trackingId);
        identification.setShopperID(trackingId);
        identification.setInvoiceID(trackingId);

        payment.setCode(paymentCode);

        account.setHolder(commRequestVO.getAddressDetailsVO().getFirstname() + " " + commRequestVO.getAddressDetailsVO().getLastname());

        account.setBIC(commRequestVO.getCardDetailsVO().getBIC());
        account.setIBAN(commRequestVO.getCardDetailsVO().getIBAN());

        name.setGiven(commRequestVO.getAddressDetailsVO().getFirstname());
        name.setFamily(commRequestVO.getAddressDetailsVO().getLastname());
        presentation.setUsage(commRequestVO.getTransDetailsVO().getOrderDesc());
        //name.setSex("M");
        //payment.setPresentation(presentation);

        address.setStreet(commRequestVO.getAddressDetailsVO().getStreet());
        address.setZip(commRequestVO.getAddressDetailsVO().getZipCode());
        address.setCity(commRequestVO.getAddressDetailsVO().getCity());
        address.setCountry(commRequestVO.getAddressDetailsVO().getCountry());
        address.setState(commRequestVO.getAddressDetailsVO().getState());

        contact.setEmail(commRequestVO.getAddressDetailsVO().getEmail());

        customer.setAccount(account);
        customer.setName(name);
        customer.setAddress(address);
        customer.setContact(contact);

        frontend.setInternalResponseUrl(getPropertyValue(BACKENDURL)+"?transactionId="+trackingId);

        mandate.setType("core");

        if("Y".equalsIgnoreCase(gatewayAccount.getIsRecurring()))
        {
            recurrence="recurring";
            recurringBillingVO.setRecurring("Y");
        }
        else
        {
            recurrence="onetime";
            recurringBillingVO.setRecurring("N");
        }

        mandate.setRecurrence(recurrence);

        authorization.setType("ARBITRARY");

        if(gatewayAccount.isTest())
        {
            connectorMode="CONNECTOR_TEST";
        }
        else
        {
            connectorMode="LIVE";
        }

        transaction.setMode(connectorMode);
        transaction.setResponse("XML");

        transaction.setLogin(login);
        transaction.setIdentification(identification);
        payments.add(payment);
        transaction.setPayment(payments);
        transaction.setCustomer(customer);
        transaction.setMandate(mandate);
        transaction.setAuthorization(authorization);
        transaction.setFrontend(frontend);

        request.setVersion("1.0");

        request.setTransaction(transaction);

        commRequestVO.setRecurringBillingVO(recurringBillingVO);

        return request;
    }

    public Request getDirectDebitRequestForSepaSale(CommRequestVO commRequestVO,String trackingId,String paymentCode,String accountId)
    {
        String connectorMode="CONNECTOR_TEST";

        GatewayAccount gatewayAccount = getGatewayAccount(accountId);

        Request request = new Request();

        Transaction transaction = new Transaction();
        Login login = new Login();
        Identification identification = new Identification();

        List<Payment> payments = new ArrayList<Payment>();
        Payment payment = new Payment();
        Presentation presentation = new Presentation();
        Frontend frontend = new Frontend();
        Customer customer = new Customer();
        Signature signature = new Signature();

        Account account = new Account();
        Name name = new Name();
        Address address = new Address();
        Contact contact = new Contact();

        login.setUser(gatewayAccount.getFRAUD_FTP_USERNAME());
        login.setPassword(gatewayAccount.getFRAUD_FTP_PASSWORD());
        login.setProjectID(gatewayAccount.getMerchantId());

        identification.setTransactionID(trackingId);
        identification.setShopperID(trackingId);
        identification.setInvoiceID(trackingId);

        payment.setCode(paymentCode);

        presentation.setAmount(commRequestVO.getTransDetailsVO().getAmount());
        presentation.setCurrency(commRequestVO.getTransDetailsVO().getCurrency());
        presentation.setUsage(commRequestVO.getTransDetailsVO().getOrderDesc());

        payment.setPresentation(presentation);

        frontend.setInternalResponseUrl(getPropertyValue(BACKENDURL)+"?transactionId="+trackingId);

        customer.setRegistration(commRequestVO.getCardDetailsVO().getMandateId());

        if(gatewayAccount.isTest())
        {
            connectorMode="CONNECTOR_TEST";
        }
        else
        {
            connectorMode="LIVE";
        }

        transaction.setMode(connectorMode);
        transaction.setResponse("XML");

        transaction.setLogin(login);
        transaction.setIdentification(identification);
        payments.add(payment);
        transaction.setPayment(payments);
        transaction.setCustomer(customer);
        transaction.setFrontend(frontend);

        request.setVersion("1.0");

        request.setTransaction(transaction);
        return request;
    }

    //Request formation for DirectDebit SEPA Capture
    public Request getDirectDebitRequestForSepaCapture(CommRequestVO commRequestVO,String trackingId,String paymentCode,String accountId)
    {

        GatewayAccount gatewayAccount = getGatewayAccount(accountId);

        String connectorMode="CONNECTOR_TEST";

        Request request = new Request();

        Transaction transaction = new Transaction();
        Login login = new Login();
        Identification identification = new Identification();

        List<Payment> payments = new ArrayList<Payment>();
        Payment payment = new Payment();
        Presentation presentation = new Presentation();

        login.setUser(gatewayAccount.getFRAUD_FTP_USERNAME());
        login.setPassword(gatewayAccount.getFRAUD_FTP_PASSWORD());
        login.setProjectID(gatewayAccount.getMerchantId());

        identification.setReferenceID(commRequestVO.getTransDetailsVO().getPreviousTransactionId());

        payment.setCode(paymentCode);

        presentation.setAmount(commRequestVO.getTransDetailsVO().getAmount());
        presentation.setCurrency(commRequestVO.getTransDetailsVO().getCurrency());
        presentation.setUsage("Demopayment");

        payment.setPresentation(presentation);

        if(gatewayAccount.isTest())
        {
            connectorMode="CONNECTOR_TEST";
        }
        else
        {
            connectorMode="LIVE";
        }

        transaction.setMode(connectorMode);
        transaction.setResponse("XML");

        transaction.setLogin(login);
        transaction.setIdentification(identification);
        payments.add(payment);
        transaction.setPayment(payments);

        request.setVersion("1.0");

        request.setTransaction(transaction);
        return request;
    }

    //Request formation for DirectDebit SEPA Reversal
    public Request getDirectDebitRequestForSepaReversal(CommRequestVO commRequestVO,String trackingId,String paymentCode,String accountId)
    {

        String connectorMode = "CONNECTOR_TEST";
        GatewayAccount gatewayAccount = getGatewayAccount(accountId);

        Request request = new Request();

        Transaction transaction = new Transaction();
        Login login = new Login();
        Identification identification = new Identification();

        List<Payment> payments = new ArrayList<Payment>();
        Payment payment = new Payment();
        Presentation presentation = new Presentation();
        Customer customer = new Customer();
        Signature signature = new Signature();

        login.setUser(gatewayAccount.getFRAUD_FTP_USERNAME());
        login.setPassword(gatewayAccount.getFRAUD_FTP_PASSWORD());
        login.setProjectID(gatewayAccount.getMerchantId());

        identification.setTransactionID(trackingId);
        identification.setReferenceID(commRequestVO.getTransDetailsVO().getPreviousTransactionId());
        identification.setShopperID("");
        identification.setInvoiceID("");

        payment.setCode(paymentCode);

        presentation.setAmount(commRequestVO.getTransDetailsVO().getAmount());
        presentation.setCurrency(commRequestVO.getTransDetailsVO().getCurrency());
        presentation.setUsage("Demopayment");

        payment.setPresentation(presentation);

        if(gatewayAccount.isTest())
        {
            connectorMode="CONNECTOR_TEST";
        }
        else
        {
            connectorMode="LIVE";
        }

        transaction.setMode(connectorMode);
        transaction.setResponse("XML");

        transaction.setLogin(login);
        transaction.setIdentification(identification);
        payments.add(payment);
        transaction.setPayment(payments);

        request.setVersion("1.0");

        request.setTransaction(transaction);
        return request;
    }

    //Request formation for DirectDebit SEPA Refund
    public Request getDirectDebitRequestForSepaRefund(CommRequestVO commRequestVO,String trackingId,String paymentCode,String accountId)
    {
        GatewayAccount gatewayAccount = getGatewayAccount(accountId);

        String connectorMode="CONNECTOR_TEST";

        Request request = new Request();

        Transaction transaction = new Transaction();
        Login login = new Login();
        Identification identification = new Identification();
        List<Payment> payments = new ArrayList<Payment>();

        Payment payment = new Payment();
        Presentation presentation = new Presentation();

        login.setUser(gatewayAccount.getFRAUD_FTP_USERNAME());
        login.setPassword(gatewayAccount.getFRAUD_FTP_PASSWORD());
        login.setProjectID(gatewayAccount.getMerchantId());

        identification.setTransactionID(trackingId);
        identification.setReferenceID(commRequestVO.getTransDetailsVO().getPreviousTransactionId());
        identification.setShopperID(commRequestVO.getTransDetailsVO().getPreviousTransactionId());
        identification.setInvoiceID(commRequestVO.getTransDetailsVO().getPreviousTransactionId());

        payment.setCode(paymentCode);

        presentation.setAmount(commRequestVO.getTransDetailsVO().getAmount());
        presentation.setCurrency(commRequestVO.getTransDetailsVO().getCurrency());
        presentation.setUsage(commRequestVO.getTransDetailsVO().getOrderDesc());

        payment.setPresentation(presentation);

        if(gatewayAccount.isTest())
        {
            connectorMode="CONNECTOR_TEST";
        }
        else
        {
            connectorMode="LIVE";
        }

        transaction.setMode(connectorMode);
        transaction.setResponse("XML");

        transaction.setLogin(login);
        transaction.setIdentification(identification);
        payments.add(payment);
        transaction.setPayment(payments);

        request.setVersion("1.0");

        request.setTransaction(transaction);
        return request;
    }

    public Request getOnlineBankTransferRequestForAuthentication(CommRequestVO commRequestVO,String trackingId,String paymentCode, String accountid)
    {
        GatewayAccount gatewayAccount = getGatewayAccount(accountid);
        Request request = new Request();
        Transaction transaction = new Transaction();

        Login login = new Login();
        Identification identification = new Identification();
        Frontend frontend = new Frontend();
        List<Payment> payments =new ArrayList<Payment>();
        Payment payment = new Payment();
        Customer customer=new Customer();

        //Payment Sub Instance
        Presentation presentation = new Presentation();

        //Customer Sub instance
        Account account=new Account();
        Name name=new Name();
        Address address = new Address();
        Contact contact = new Contact();
        Signature signature = new Signature();

        transaction.setResponse("SYNC");
        transaction.setMode("CONNECTOR_TEST");

        login.setUser(gatewayAccount.getFRAUD_FTP_USERNAME());
        login.setPassword(gatewayAccount.getFRAUD_FTP_PASSWORD());
        login.setProjectID(gatewayAccount.getMerchantId());

        identification.setTransactionID(trackingId);
        //identification.setShopperID("12345");
        //identification.setInvoiceID("1");

        frontend.setResponseUrl("http://example.com");
        frontend.setResponseErrorUrl("http://example.com");

        payment.setCode(paymentCode);

        //Value is set for presentation
        presentation.setAmount(commRequestVO.getTransDetailsVO().getAmount());
        presentation.setCurrency(commRequestVO.getTransDetailsVO().getCurrency());
        presentation.setUsage("Demo Payment");

        payment.setPresentation(presentation);

        //account value is set
        /*account.setBank("37040044");
        account.setNumber("5320130");
        account.setCountry("DE");*/

        account.setIBAN("DE23100000001234567890");
        account.setBIC("MARKDEF1100");
        account.setBankName("Worldbank");
        account.setCountry(commRequestVO.getAddressDetailsVO().getCountry());


        // value is set for Name
        name.setFamily(commRequestVO.getAddressDetailsVO().getFirstname());
        name.setGiven(commRequestVO.getAddressDetailsVO().getLastname());
        name.setSex("M");
        //value is set for address
        address.setCity(commRequestVO.getAddressDetailsVO().getCity());
        address.setCountry(commRequestVO.getAddressDetailsVO().getCountry());
        //address.setState("");
        address.setStreet(commRequestVO.getAddressDetailsVO().getStreet());
        address.setZip(commRequestVO.getAddressDetailsVO().getZipCode());
        //value is set for contact
        contact.setEmail(commRequestVO.getAddressDetailsVO().getEmail());
        //value is set for signature
        signature.setEmpty("empty");


        customer.setAccount(account);
        customer.setName(name);
        customer.setAddress(address);
        customer.setContact(contact);
        customer.setSignature(signature);

        transaction.setLogin(login);
        transaction.setIdentification(identification);
        transaction.setFrontend(frontend);
        payments.add(payment);
        transaction.setPayment(payments);
        transaction.setCustomer(customer);

        request.setTransaction(transaction);
        request.setVersion("1.0");
        return request;
    }

    public Request getOnlineBankTransferRequestForPreAuth(CommRequestVO commRequestVO,String trackingId,String paymentCode,String accountId,String brand)
    {
        String connectorMode="CONNECTOR_TEST";

        GatewayAccount gatewayAccount = getGatewayAccount(accountId);
        Request request = new Request();

        Transaction transaction = new Transaction();

        Login login = new Login();
        List<Payment> payments=new ArrayList<Payment>();
        Payment payment = new Payment();
        Identification identification = new Identification();
        Customer customer = new Customer();
        Frontend frontend = new Frontend();

        //Payment
        Presentation presentation = new Presentation();

        //Customer
        Account account = new Account();

        login.setUser(gatewayAccount.getFRAUD_FTP_USERNAME());
        login.setPassword(gatewayAccount.getFRAUD_FTP_PASSWORD());
        login.setProjectID(gatewayAccount.getMerchantId());

        identification.setTransactionID(trackingId);
        identification.setShopperID(trackingId);
        identification.setInvoiceID(trackingId);
        identification.setShortID(trackingId);

        presentation.setAmount(commRequestVO.getTransDetailsVO().getAmount());
        presentation.setCurrency(commRequestVO.getTransDetailsVO().getCurrency());
        presentation.setUsage(commRequestVO.getTransDetailsVO().getOrderDesc());

        payment.setCode(paymentCode);
        payment.setPresentation(presentation);

        //account.setNumber("12345676");
        //account.setBank("88888888");
        account.setBrand(brand);

        Name name = new Name();

        name.setGiven(commRequestVO.getAddressDetailsVO().getFirstname());
        name.setFamily(commRequestVO.getAddressDetailsVO().getLastname());
        name.setBirthdate(commRequestVO.getAddressDetailsVO().getBirthdate());
        name.setSex("M");

        Address address = new Address();

        address.setStreet(commRequestVO.getAddressDetailsVO().getStreet());
        address.setZip(commRequestVO.getAddressDetailsVO().getZipCode());
        address.setCity(commRequestVO.getAddressDetailsVO().getCity());
        address.setState(commRequestVO.getAddressDetailsVO().getState());
        address.setCountry(commRequestVO.getAddressDetailsVO().getCountry());

        Contact contact = new Contact();

        contact.setEmail(commRequestVO.getAddressDetailsVO().getEmail());
        contact.setIp(commRequestVO.getAddressDetailsVO().getIp());
        contact.setPhone(commRequestVO.getAddressDetailsVO().getPhone());

        customer.setAccount(account);
        //customer.setName(name);
        //customer.setAddress(address);
        //customer.setContact(contact);

        frontend.setResponseUrl(getPropertyValue(FRONTENDURL)+"?transactionId="+trackingId);//"http://localhost:8081/transaction/P4FrontEndServlet?transactionId="+trackingId
        frontend.setResponseErrorUrl(getPropertyValue(ERORRURL)+"?transactionId="+trackingId);//"http://localhost:8081/transaction/P4FrontEndServlet?transactionId=" +trackingId
        frontend.setInternalResponseUrl(getPropertyValue(BACKENDURL)+"?transactionId="+trackingId);//"http://localhost:8081/transaction/P4BackEndServlet?transactionId="+trackingId
        //frontend.setLanguage(commRequestVO.getAddressDetailsVO().getLanguage());
        //frontend.setSessionID("localhost:8081/transaction/P4BackEndServlet");

        if(gatewayAccount.isTest())
        {
            connectorMode="CONNECTOR_TEST";
        }
        else
        {
            connectorMode="LIVE";
        }

        transaction.setMode(connectorMode);
        transaction.setResponse("ASYNC");

        transaction.setLogin(login);
        payments.add(payment);
        transaction.setPayment(payments);
        transaction.setIdentification(identification);
        transaction.setCustomer(customer);
        transaction.setFrontend(frontend);

        request.setVersion("1.0");
        request.setTransaction(transaction);
        return request;
    }

    private String getPropertyValue(String key)
    {
        ResourceBundle RB1=LoadProperties.getProperty("com.directi.pg.p4Online");
        return RB1.getString(key);
    }

    public Request getOnlineBankTransferRequestForRefund(CommRequestVO commRequestVO,String trackingId,String paymentCode, String accountId)
    {

        String connectorMode ="";

        GatewayAccount gatewayAccount = getGatewayAccount(accountId);
        Request request = new Request();

        Transaction transaction = new Transaction();
        Login login = new Login();
        Identification identification = new Identification();
        List<Payment> payments = new ArrayList<Payment>();
        Payment payment = new Payment();

        //Payment
        Presentation presentation = new Presentation();

        login.setUser(gatewayAccount.getFRAUD_FTP_USERNAME());
        login.setPassword(gatewayAccount.getFRAUD_FTP_PASSWORD());
        login.setProjectID(gatewayAccount.getMerchantId());

        identification.setReferenceID(commRequestVO.getTransDetailsVO().getPreviousTransactionId());

        presentation.setAmount(commRequestVO.getTransDetailsVO().getAmount());
        presentation.setCurrency(commRequestVO.getTransDetailsVO().getCurrency());
        presentation.setUsage(commRequestVO.getTransDetailsVO().getOrderDesc());

        payment.setCode(paymentCode);
        payment.setPresentation(presentation);

        if(gatewayAccount.isTest())
        {
            connectorMode="CONNECTOR_TEST";
        }
        else
        {
            connectorMode="LIVE";
        }

        transaction.setMode(connectorMode);
        transaction.setResponse("SYNC");//TODO CHANGE TO ASYNCH if not working

        transaction.setLogin(login);
        transaction.setIdentification(identification);
        payments.add(payment);
        transaction.setPayment(payments);

        request.setVersion("1.0");
        request.setTransaction(transaction);
        return request;
    }

    /*public Request getOnlineBankTransferRequestForDirectSubmit(CommRequestVO commRequestVO,String trackingId,String paymentCode)
    {
        Request request = new Request();

        Transaction transaction = new Transaction();
        Login login = new Login();
        Identification identification = new Identification();
        Frontend frontend = new Frontend();
        List<Payment> payments=new ArrayList<Payment>();
        Payment payment = new Payment();

        Customer customer = new Customer();
        Name name = new Name();
        Address address = new Address();
        Contact contact = new Contact();


        //Payment
        Presentation presentation = new Presentation();

        login.setUser("pz");
        login.setPassword("default");
        login.setProjectID("221");

        identification.setTransactionID("12345");
        identification.setShopperID("12345");
        identification.setInvoiceID("1");

        frontend.setResponseUrl("http://example.com");
        frontend.setResponseErrorUrl("http://example.com/error.php?error=~ERRORCODE~");

        payment.setCode("OT.SU");

        presentation.setAmount("1.99");
        presentation.setCurrency("EUR");
        presentation.setUsage("Demopayment");

        payment.setPresentation(presentation);

        name.setGiven("Max");
        name.setFamily("Musterman");
        name.setSex("M");


        address.setStreet("Musterweg 1");
        address.setZip("12345");
        address.setCity("Musterstadt");
        address.setCountry("DE");


        contact.setEmail("user@provider.de");

        customer.setAddress(address);
        customer.setName(name);
        customer.setContact(contact);

        transaction.setMode("CONNECTOR_TEST");
        transaction.setResponse("ASYNC");

        transaction.setLogin(login);
        transaction.setIdentification(identification);
        transaction.setFrontend(frontend);
        payments.add(payment);
        transaction.setPayment(payments);
        transaction.setCustomer(customer);

        request.setVersion("1.0");
        request.setTransaction(transaction);
        return request;
    }*/

    /*public CommRequestVO getCreditCardRequestForPreAuth(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO = new CommRequestVO();

        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();
        CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();
        CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();

        commMerchantVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount()); //Amount * 100 according to the docs
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());

        commAddressDetailsVO.setLanguage(commonValidatorVO.getAddressDetailsVO().getLanguage());

        commAddressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        commAddressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        commAddressDetailsVO.setBirthdate(commonValidatorVO.getAddressDetailsVO().getBirthdate());
        commAddressDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
        commAddressDetailsVO.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
        commAddressDetailsVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
        commAddressDetailsVO.setState(commonValidatorVO.getAddressDetailsVO().getState());
        commAddressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        commAddressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        commAddressDetailsVO.setIp(commonValidatorVO.getAddressDetailsVO().getIp());
        commAddressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());

        commCardDetailsVO.setCardHolderName(commonValidatorVO.getCardDetailsVO().getCardHolderName());
        commCardDetailsVO.setCardNum(commonValidatorVO.getCardDetailsVO().getCardNum());
        commCardDetailsVO.setcVV(commonValidatorVO.getCardDetailsVO().getcVV());
        commCardDetailsVO.setCardType(commonValidatorVO.getCardDetailsVO().getCardType());
        commCardDetailsVO.setExpMonth(commonValidatorVO.getCardDetailsVO().getExpMonth());
        commCardDetailsVO.setExpYear(commonValidatorVO.getCardDetailsVO().getExpYear());

        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);
        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
        commRequestVO.setCardDetailsVO(commCardDetailsVO);

        return commRequestVO;
    }*/

    public Response sendRequestForCreditCard(Request request) throws PZTechnicalViolationException
    {
        logger.debug("Sending Request for Credit Card");
        transactionLogger.debug("Sending Request for Credit Card");

        Response response=null;
        try
        {
            ClientConfig config = new DefaultClientConfig();
            config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
            Client client = Client.create(config);
            WebResource service = client.resource(getBaseURI());

            response = service.path("api").path("payment").type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).post(Response.class, request);

        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(P4Utils.class.getName(), "sendRequestForCreditCard()", null, "Common", "Exception while creating connection with bank", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON, null, e.getMessage(), e.getCause());
        }
        return response;
    }

    public Response sendRequestForQuery(Request request) throws PZTechnicalViolationException
    {
        logger.debug("Sending Request for Credit Card");
        transactionLogger.debug("Sending Request for Credit Card");

        Response response=null;
        try
        {
            ClientConfig config = new DefaultClientConfig();

            Client client = Client.create(config);
            WebResource service = client.resource(getBaseURI());

            response = service.path("api").path("query").type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).post(Response.class, request);
        }
        catch(Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(P4Utils.class.getName(), "sendRequestForQuery()", null, "Common", "Exception while creating connection with bank", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON, null, e.getMessage(), e.getCause());
        }

        return response;
    }

    public String sendTestingRequestForCreditCard(Request request)
    {
        logger.debug("Sending Request for Credit Card");
        transactionLogger.debug("Sending Request for Credit Card");

        ClientConfig config = new DefaultClientConfig();

        Client client = Client.create(config);
        WebResource service = client.resource(getBaseURI());

        String response = service.path("api").path("payment").type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).post(String.class, request);

        return response;
    }

    public Request getCreditCardRequestForAuthentication(CommRequestVO commRequestVO,String trackingId,String paymentCode,String accountId)
    {
        logger.debug("Entering processAuth of P4Utils...");
        transactionLogger.debug("Entering processAuth of P4Utils...");
        GatewayAccount gatewayAccount = getGatewayAccount(accountId);

        String connectorMode="";
        Request request = new Request();

        Transaction transaction = new Transaction();
        Login login = new Login();
        Identification identification = new Identification();

        List<Payment> payments = new ArrayList<Payment>();
        Payment payment = new Payment();
        Presentation presentation = new Presentation();

        Customer customer = new Customer();
        Account account = new Account();
        Expiry expiry = new Expiry();
        Name name = new Name();
        Address address = new Address();
        Contact contact = new Contact();



        login.setUser(gatewayAccount.getFRAUD_FTP_USERNAME());
        login.setPassword(gatewayAccount.getFRAUD_FTP_PASSWORD());
        login.setProjectID(gatewayAccount.getMerchantId());

        identification.setTransactionID(trackingId);
        //identification.setShopperID("12345");
        //identification.setInvoiceID("1");

        payment.setCode(paymentCode);

        presentation.setAmount(commRequestVO.getTransDetailsVO().getAmount());
        presentation.setCurrency(commRequestVO.getTransDetailsVO().getCurrency());
        presentation.setUsage(commRequestVO.getTransDetailsVO().getOrderId());

        payment.setPresentation(presentation);

        account.setType("CC");
        account.setHolder(commRequestVO.getAddressDetailsVO().getFirstname() + " " + commRequestVO.getAddressDetailsVO().getLastname());
        account.setNumber(commRequestVO.getCardDetailsVO().getCardNum());
        //account.setBrand("VISA");

        expiry.setMonth(commRequestVO.getCardDetailsVO().getExpMonth());
        expiry.setYear(commRequestVO.getCardDetailsVO().getExpYear());

        account.setExpiry(expiry);

        account.setVerification(commRequestVO.getCardDetailsVO().getcVV());

        name.setGiven(commRequestVO.getAddressDetailsVO().getFirstname());
        name.setFamily(commRequestVO.getAddressDetailsVO().getLastname());
        //name.setSex("M");

        address.setStreet(commRequestVO.getAddressDetailsVO().getStreet());
        address.setZip(commRequestVO.getAddressDetailsVO().getZipCode());
        address.setCity(commRequestVO.getAddressDetailsVO().getCity());
        address.setCountry(commRequestVO.getAddressDetailsVO().getCountry());

        contact.setEmail(commRequestVO.getAddressDetailsVO().getEmail());

        customer.setAccount(account);
        customer.setName(name);
        customer.setAddress(address);
        customer.setAccount(account);

        if(gatewayAccount.isTest())
        {
            connectorMode="TEST";
        }
        else
        {
            connectorMode="LIVE";
        }

        transaction.setMode(connectorMode);
        transaction.setResponse("SYNC");

        transaction.setLogin(login);
        transaction.setIdentification(identification);
        payments.add(payment);
        transaction.setPayment(payments);
        transaction.setCustomer(customer);

        request.setVersion("1.0");

        request.setTransaction(transaction);
        return request;
    }

    public Request getCreditCardRequestForSale(CommRequestVO commRequestVO,String trackingId,String paymentCode,String accountId)
    {
        GatewayAccount gatewayAccount = getGatewayAccount(accountId);

        String connectorMode="";

        Request request = new Request();

        Transaction transaction = new Transaction();
        Login login = new Login();
        Identification identification = new Identification();
        Frontend frontend = new Frontend();
        List<Payment> payments = new ArrayList<Payment>();
        Payment payment = new Payment();
        Customer customer = new Customer();

        Presentation presentation = new Presentation();
        Name name = new Name();
        Address address = new Address();
        Contact contact = new Contact();
        Account account = new Account();
        Expiry expiry = new Expiry();

        login.setUser(gatewayAccount.getFRAUD_FTP_USERNAME());
        login.setPassword(gatewayAccount.getFRAUD_FTP_PASSWORD());
        login.setProjectID(gatewayAccount.getMerchantId());

        identification.setTransactionID(trackingId);
        //identification.setShopperID("12345");
        //identification.setInvoiceID("1");

        frontend.setResponseUrl("http://example.com");
        frontend.setResponseErrorUrl("http://example.com");

        payment.setCode(paymentCode);

        presentation.setAmount(commRequestVO.getTransDetailsVO().getAmount());
        presentation.setCurrency(commRequestVO.getTransDetailsVO().getCurrency());
        presentation.setUsage(commRequestVO.getTransDetailsVO().getOrderId());

        payment.setPresentation(presentation);

        account.setType("CC");
        account.setHolder(commRequestVO.getAddressDetailsVO().getFirstname() + " " + commRequestVO.getAddressDetailsVO().getLastname());
        account.setNumber(commRequestVO.getCardDetailsVO().getCardNum());
        //account.setBrand("VISA");

        expiry.setMonth(commRequestVO.getCardDetailsVO().getExpMonth());
        expiry.setYear(commRequestVO.getCardDetailsVO().getExpYear());

        account.setExpiry(expiry);

        account.setVerification(commRequestVO.getCardDetailsVO().getcVV());

        name.setGiven(commRequestVO.getAddressDetailsVO().getFirstname());
        name.setFamily(commRequestVO.getAddressDetailsVO().getLastname());
        //name.setSex("M");

        address.setStreet(commRequestVO.getAddressDetailsVO().getStreet());
        address.setZip(commRequestVO.getAddressDetailsVO().getZipCode());
        address.setCity(commRequestVO.getAddressDetailsVO().getCity());
        address.setCountry(commRequestVO.getAddressDetailsVO().getCountry());

        contact.setEmail(commRequestVO.getAddressDetailsVO().getEmail());

        customer.setAccount(account);
        customer.setName(name);
        customer.setAddress(address);
        customer.setContact(contact);

        if(gatewayAccount.isTest())
        {
            connectorMode="TEST";
        }
        else
        {
            connectorMode="LIVE";
        }

        transaction.setMode(connectorMode);
        transaction.setResponse("SYNC");

        transaction.setLogin(login);
        transaction.setIdentification(identification);
        //transaction.setFrontend(frontend);
        payments.add(payment);
        /*Payment payment1=new Payment();
        payment1.setCode("CC.CP");
        payment1.setPresentation(payment.getPresentation());
        payments.add(payment1);*/
        transaction.setPayment(payments);
        transaction.setCustomer(customer);

        request.setTransaction(transaction);

        request.setVersion("1.0");

        return request;
    }

    public Request getCreditCardRequestForRefund(CommRequestVO commRequestVO,String trackingId,String paymentCode,String accountId)
    {
        String connectorMode="";

        GatewayAccount gatewayAccount = getGatewayAccount(accountId);
        Request request = new Request();

        Transaction transaction = new Transaction();
        Login login = new Login();
        Identification identification = new Identification();
        List<Payment> payments = new ArrayList<Payment>();
        Payment payment = new Payment();

        Presentation presentation = new Presentation();

        login.setUser(gatewayAccount.getFRAUD_FTP_USERNAME());
        login.setPassword(gatewayAccount.getFRAUD_FTP_PASSWORD());
        login.setProjectID(gatewayAccount.getMerchantId());

        identification.setReferenceID(commRequestVO.getTransDetailsVO().getPreviousTransactionId());//ID=Capture

        payment.setCode(paymentCode);

        presentation.setAmount(commRequestVO.getTransDetailsVO().getAmount());
        presentation.setCurrency(commRequestVO.getTransDetailsVO().getCurrency());
        presentation.setUsage(commRequestVO.getTransDetailsVO().getOrderDesc());

        payment.setPresentation(presentation);

        if(gatewayAccount.isTest())
        {
            connectorMode="TEST";
        }
        else
        {
            connectorMode="LIVE";
        }

        transaction.setMode(connectorMode);
        transaction.setResponse("SYNC");

        transaction.setLogin(login);
        transaction.setIdentification(identification);
        payments.add(payment);
        transaction.setPayment(payments);

        request.setTransaction(transaction);

        request.setVersion("1.0");

        return request;
    }

    public Request getCreditCardRequestForCapture(CommRequestVO commRequestVO,String trackingId,String paymentCode,String accountId)
    {
        String  connectorMode="";

        GatewayAccount gatewayAccount = getGatewayAccount(accountId);
        Request request = new Request();

        Transaction transaction = new Transaction();
        Login login = new Login();
        Identification identification = new Identification();
        List<Payment> payments = new ArrayList<Payment>();
        Payment payment = new Payment();

        Presentation presentation = new Presentation();

        login.setUser(gatewayAccount.getFRAUD_FTP_USERNAME());
        login.setPassword(gatewayAccount.getFRAUD_FTP_PASSWORD());
        login.setProjectID(gatewayAccount.getMerchantId());


        identification.setReferenceID(commRequestVO.getTransDetailsVO().getPreviousTransactionId());    //ID=Auth

        payment.setCode(paymentCode);

        presentation.setAmount(commRequestVO.getTransDetailsVO().getAmount());
        presentation.setCurrency(commRequestVO.getTransDetailsVO().getCurrency());
        presentation.setUsage(commRequestVO.getTransDetailsVO().getOrderDesc());

        payment.setPresentation(presentation);

        if(gatewayAccount.isTest())
        {
            connectorMode="TEST";
        }
        else
        {
            connectorMode="LIVE";
        }

        transaction.setMode(connectorMode);
        transaction.setResponse("SYNC");

        transaction.setLogin(login);
        transaction.setIdentification(identification);
        payments.add(payment);
        transaction.setPayment(payments);

        request.setVersion("1.0");

        request.setTransaction(transaction);

        return request;
    }

    public Request getCreditCardRequestForVoid(CommRequestVO commRequestVO,String trackingId,String paymentCode,String accountId)//
    {
        String connectorMode="";

        GatewayAccount gatewayAccount = getGatewayAccount(accountId);
        Request request = new Request();

        Transaction transaction = new Transaction();
        Login login = new Login();
        Identification identification = new Identification();
        List<Payment> payments=new ArrayList<Payment>();
        Payment payment = new Payment();

        Presentation presentation = new Presentation();

        login.setUser(gatewayAccount.getFRAUD_FTP_USERNAME());
        login.setPassword(gatewayAccount.getFRAUD_FTP_PASSWORD());
        login.setProjectID(gatewayAccount.getMerchantId());

        identification.setReferenceID(commRequestVO.getTransDetailsVO().getPreviousTransactionId());//ID=Capture

        payment.setCode(paymentCode);

        presentation.setAmount(commRequestVO.getTransDetailsVO().getAmount());
        presentation.setCurrency(commRequestVO.getTransDetailsVO().getCurrency());
        presentation.setUsage(commRequestVO.getTransDetailsVO().getOrderDesc());

        payment.setPresentation(presentation);

        if(gatewayAccount.isTest())
        {
            connectorMode="TEST";
        }
        else
        {
            connectorMode="LIVE";
        }

        transaction.setMode(connectorMode);
        transaction.setResponse("SYNC");

        transaction.setLogin(login);
        transaction.setIdentification(identification);
        payments.add(payment);
        transaction.setPayment(payments);

        request.setVersion("1.0");

        request.setTransaction(transaction);

        return  request;
    }

    public Request getCreditCardRequestForQuery(CommRequestVO commRequestVO,String trackingId,String paymentCode,String accountId)
    {
        GatewayAccount gatewayAccount = getGatewayAccount(accountId);
        Request request = new Request();

        Query query = new Query();
        Login login = new Login();
        Scope scope = new Scope();
        Period period = new Period();
        Types types = new Types();
        List<Type> typeList = new ArrayList<Type>();
        Type type=new Type();
        Identifications identifications = new Identifications();
        Identification identification=new Identification();
        List<Identification> identificationList = new ArrayList<Identification>();

        login.setUser(gatewayAccount.getFRAUD_FTP_USERNAME());
        login.setPassword(gatewayAccount.getFRAUD_FTP_PASSWORD());

        scope.setEntity("project");
        scope.setEntityID("221");

        /*period.setStrict("false");
        period.setFrom("2015-09-23T10:00:00");
        period.setTo("2015-11-24T11:00:00");*/

        type.setCode(paymentCode);
        typeList.add(type);
        types.setType(typeList);

        identification.setUniqueID(commRequestVO.getTransDetailsVO().getPreviousTransactionId());
        identification.setTransactionID(trackingId);
        identificationList.add(identification);
        identifications.setIdentification(identificationList);

        //query.setMode("CONNECTOR_TEST");

        // query.setResponse("XML");
        query.setLogin(login);
        query.setScope(scope);
        //query.setPeriod(period);
        //query.setTypes(types);
        query.setIdentifications(identifications);

        request.setVersion("1.0");

        request.setQuery(query);

        return request;
    }

    public Request getOnlineBankRequestForQuery(CommRequestVO commRequestVO,String trackingId,String uniqueId,String accountId)
    {
        //GatewayAccount gatewayAccount = getGatewayAccount(accountId);
        Request request = new Request();

        Query query = new Query();
        Login login = new Login();
        Scope scope = new Scope();
        Period period = new Period();
        Types types = new Types();
        List<Type> typeList = new ArrayList<Type>();
        Type type=new Type();
        Identifications identifications = new Identifications();
        Identification identification=new Identification();
        List<Identification> identificationList = new ArrayList<Identification>();

        login.setUser("pz");
        login.setPassword("default");

        scope.setEntity("project");
        scope.setEntityID("221");

        period.setStrict("false");
        period.setFrom("2015-09-23T10:00:00");
        period.setTo("2015-11-24T11:00:00");


        identification.setUniqueID(uniqueId);
        identificationList.add(identification);
        identifications.setIdentification(identificationList);

        //query.setMode("CONNECTOR_TEST");

        // query.setResponse("XML");
        query.setLogin(login);
        query.setScope(scope);
        //query.setPeriod(period);
        //query.setTypes(types);
        query.setIdentifications(identifications);

        request.setVersion("1.0");

        request.setQuery(query);

        return request;
    }

    public P4ResponseVO convertP4CreditCardResponseToPZForAuthorization(Response response)
    {
        P4ResponseVO p4ResponseVO = new P4ResponseVO();
        SimpleDateFormat responseTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat responseTimePZ = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try
        {
            if (response.getTransaction() != null)
            {

                Transaction transaction = response.getTransaction();
                if (transaction.getPayment() != null)
                {
                    Payment payment = transaction.getPayment().get(0);
                    if (payment.getClearing() != null)
                    {
                        Clearing clearing = payment.getClearing();
                        p4ResponseVO.setAmount(clearing.getAmount());
                    }
                    if(transaction.getIdentification()!=null)
                    {
                        Identification identification=transaction.getIdentification();
                        p4ResponseVO.setShortID(identification.getShortID());
                        p4ResponseVO.setTransactionId(identification.getUniqueID());
                    }
                    if (transaction.getProcessing() != null)
                    {
                        Processing processing = transaction.getProcessing();
                        p4ResponseVO.setResponseTime(responseTimePZ.format(responseTime.parse(processing.getTimestamp())));
                        p4ResponseVO.setStatus("ACK".equalsIgnoreCase(processing.getResult()) ? "success" : "fail");
                        p4ResponseVO.setDescription(processing.getReason() + "(" + processing.getReturn() + ")");
                        p4ResponseVO.setRemark(processing.getStatus() + "(" + processing.getState() + ")");
                    }
                }
            }
        }
        catch (ParseException e)
        {
            logger.error("Parsing exception while parsing response time from the bank.",e);
        }
        return p4ResponseVO;
    }

    public P4ResponseVO convertP4CreditCardResponseToPZForSale(Response response)
    {
        P4ResponseVO p4ResponseVO = new P4ResponseVO();
        SimpleDateFormat responseTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat responseTimePZ = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        try
        {
            if (response.getTransaction() != null)
            {

                Transaction transaction = response.getTransaction();
                if (transaction.getPayment() != null)
                {
                    Payment payment = transaction.getPayment().get(0);
                    if (payment.getClearing() != null)
                    {
                        Clearing clearing = payment.getClearing();

                        p4ResponseVO.setAmount(clearing.getAmount());
                    }
                }
                if (transaction.getIdentification() != null)
                {
                    Identification identification = transaction.getIdentification();

                    p4ResponseVO.setTransactionId(identification.getUniqueID());
                    p4ResponseVO.setShortID(identification.getShortID());
                }
                if (transaction.getProcessing() != null)
                {
                    Processing processing = transaction.getProcessing();

                    p4ResponseVO.setResponseTime(responseTimePZ.format(responseTime.parse(processing.getTimestamp())));
                    p4ResponseVO.setStatus("ACK".equalsIgnoreCase(processing.getResult()) ? "success" : "fail");
                    p4ResponseVO.setDescription(processing.getReason() /*+ "(" + processing.getReturn() + ")"*/);
                    p4ResponseVO.setRemark(processing.getStatus()+"("+processing.getState()+")");
                }
            }
        }
        catch (ParseException e)
        {
            logger.error("Parsing exception while parsing response time from the bank.",e);
        }
        return p4ResponseVO;
    }

    public P4ResponseVO convertP4CreditCardResponseToPZForCapture(Response response)
    {
        logger.debug("Entering convertP4CreditCardResponseToPZForCapture");
        transactionLogger.debug("Entering convertP4CreditCardResponseToPZForCapture");


        P4ResponseVO p4ResponseVO = new P4ResponseVO();
        SimpleDateFormat responseTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat responseTimePZ = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        try
        {
            if (response.getTransaction() != null)
            {
                logger.debug("reponse----" +response);
                transactionLogger.debug("response----" + response);

                Transaction transaction = response.getTransaction();
                if (transaction.getPayment() != null)
                {
                    Payment payment = transaction.getPayment().get(0);
                    if (payment.getClearing() != null)
                    {
                        Clearing clearing = payment.getClearing();

                        p4ResponseVO.setAmount(clearing.getAmount());
                    }
                }
                if (transaction.getIdentification() != null)
                {
                    Identification identification = transaction.getIdentification();

                    p4ResponseVO.setTransactionId(identification.getUniqueID());
                    p4ResponseVO.setShortID(identification.getShortID());
                }
                if (transaction.getProcessing() != null)
                {
                    Processing processing = transaction.getProcessing();

                    p4ResponseVO.setResponseTime(responseTimePZ.format(responseTime.parse(processing.getTimestamp())));
                    p4ResponseVO.setStatus("ACK".equalsIgnoreCase(processing.getResult()) ? "success" : "fail");
                    p4ResponseVO.setRemark(processing.getReason() + "(" + processing.getReturn() + ")");
                    p4ResponseVO.setDescription(processing.getStatus() + "(" + processing.getState() + ")");
                }
            }
        }
        catch (ParseException e)
        {
            logger.error("Parsing exception while parsing response time from the bank.",e);
        }
        return p4ResponseVO;
    }

    public P4ResponseVO convertP4CreditCardResponseToPZForVoid(Response response)
    {
        logger.debug("Entering convertP4CreditCardResponseToPZForVoid");
        transactionLogger.debug("Entering convertP4CreditCardResponseToPZForVoid");


        P4ResponseVO p4ResponseVO = new P4ResponseVO();
        SimpleDateFormat responseTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat responseTimePZ = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        try
        {
            if (response.getTransaction() != null)
            {
                logger.debug("reponse----" +response);
                transactionLogger.debug("response----" + response);

                Transaction transaction = response.getTransaction();
                if (transaction.getPayment() != null)
                {
                    Payment payment = transaction.getPayment().get(0);
                    if (payment.getClearing() != null)
                    {
                        Clearing clearing = payment.getClearing();

                        p4ResponseVO.setAmount(clearing.getAmount());
                    }
                }
                if (transaction.getIdentification() != null)
                {
                    Identification identification = transaction.getIdentification();

                    p4ResponseVO.setTransactionId(identification.getUniqueID());
                    p4ResponseVO.setShortID(identification.getShortID());
                }
                if (transaction.getProcessing() != null)
                {
                    Processing processing = transaction.getProcessing();

                    p4ResponseVO.setResponseTime(responseTimePZ.format(responseTime.parse(processing.getTimestamp())));
                    p4ResponseVO.setStatus("ACK".equalsIgnoreCase(processing.getResult()) ? "success" : "fail");
                    p4ResponseVO.setRemark(processing.getReason() + "(" + processing.getReturn() + ")");
                    p4ResponseVO.setDescription(processing.getStatus() + "(" + processing.getState() + ")");
                }
            }
        }
        catch (ParseException e)
        {
            logger.error("Parsing exception while parsing response time from the bank.",e);
        }
        return p4ResponseVO;
    }

    public P4ResponseVO convertP4CreditCardResponseToPZForQuery(Response response)
    {
        logger.debug("Entering convertP4CreditCardResponseToPZForQuery");
        transactionLogger.debug("Entering convertP4CreditCardResponseToPZForQuery");


        P4ResponseVO p4ResponseVO = new P4ResponseVO();
        SimpleDateFormat responseTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat responseTimePZ = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        CommResponseVO commResponseVO=new CommResponseVO();
        PZTransactionStatus pzTransactionStatus =null;
        String status="";
        String displayName="";


            if(response!=null)
            {
                if (response.getQuery() != null)
                {
                    Query query = response.getQuery();
                    if (query.getResultSet() != null)
                    {
                        ResultSet resultSet = query.getResultSet();
                        if (resultSet.getResult() != null)
                        {
                            Result result = resultSet.getResult();
                            if (result.getTransaction() != null)
                            {
                                Transaction transaction = result.getTransaction();
                                if (transaction.getProcessing() != null)
                                {
                                    Processing processing = transaction.getProcessing();


                                    commResponseVO.setTransactionStatus(processing.getStatus());
                                    commResponseVO.setRemark("NEW(" + processing.getStatus() + ")");
                                    commResponseVO.setDescriptor(displayName);
                                    commResponseVO.setTransactionType(PZProcessType.SALE.name());
                                    if ("TRANSMITTED".equalsIgnoreCase(processing.getStatus()))
                                    {

                                        status = "success";
                                        pzTransactionStatus= PZTransactionStatus.CAPTURE_SUCCESS;
                                    }
                                    else
                                    {
                                        status = "fail";
                                        pzTransactionStatus=PZTransactionStatus.CAPTURE_FAILED;
                                    }
                                }
                                if (transaction.getEvents() != null)
                                {
                                    Events events = transaction.getEvents();
                                    if (events.getEvent() != null && events.getEvent().size() > 0 && events.getEvent().get(0) != null)
                                    {
                                        Event event = events.getEvent().get(0);
                                        if (event.getIdentification() != null)
                                        {
                                            Identification identification = event.getIdentification();
                                            commResponseVO.setTransactionId(identification.getUniqueID());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        return p4ResponseVO;
    }

    public P4ResponseVO convertP4SEPAResponseToSystemResponse(Response response)
    {
        P4ResponseVO p4ResponseVO = new P4ResponseVO();
        SimpleDateFormat responseTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat responseTimePZ = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        try
        {
            if (response.getTransaction() != null)
            {

                Transaction transaction = response.getTransaction();
                if (transaction.getPayment() != null)
                {
                    Payment payment = transaction.getPayment().get(0);
                    if (payment.getClearing() != null)
                    {
                        Clearing clearing = payment.getClearing();

                        p4ResponseVO.setAmount(clearing.getAmount());
                    }
                }
                if (transaction.getIdentification() != null)
                {
                    Identification identification = transaction.getIdentification();

                    p4ResponseVO.setTransactionId(identification.getUniqueID());
                    p4ResponseVO.setShortID(identification.getShortID());
                }
                if (transaction.getProcessing() != null)
                {
                    Processing processing = transaction.getProcessing();

                    p4ResponseVO.setResponseTime(responseTimePZ.format(responseTime.parse(processing.getTimestamp())));
                    p4ResponseVO.setStatus("ACK".equalsIgnoreCase(processing.getResult()) ? "success" : "fail");
                    p4ResponseVO.setRemark(processing.getReason() + "(" + processing.getReturn() + ")");
                    p4ResponseVO.setDescription(processing.getStatus()+"("+processing.getState()+")");
                }
            }
        }
        catch (ParseException e)
        {
            logger.error("Parsing exception while parsing response time from the bank.",e);
        }
        return p4ResponseVO;
    }

    public P4ResponseVO convertP4SEPARefundResponseToSystemResponse(Response response)
    {
        logger.debug("Entering convertP4CreditCardResponseToPZForQuery");
        transactionLogger.debug("Entering convertP4CreditCardResponseToPZForQuery");

        P4ResponseVO p4ResponseVO = new P4ResponseVO();

        SimpleDateFormat responseTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat responseTimePZ = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        try
        {
            if(response.getTransaction()!= null)
            {
                Transaction transaction = response.getTransaction();

                if(transaction.getFrontend()!=null)
                {
                    Frontend frontend = transaction.getFrontend();

                    p4ResponseVO.setRevokeMandateURL(frontend.getMandateRevokeUrl());
                    p4ResponseVO.setMandateURL(frontend.getMandateShowUrl());
                }
                if(transaction.getIdentification()!=null)
                {
                    Identification identification = transaction.getIdentification();

                    p4ResponseVO.setTransactionId(identification.getTransactionID());
                    p4ResponseVO.setShortID(identification.getShortID());
                    p4ResponseVO.setInvoiceID(identification.getInvoiceID());
                    p4ResponseVO.setUniqueID(identification.getUniqueID());
                    p4ResponseVO.setShopperID(identification.getShopperID());
                }
                if(transaction.getProcessing() != null)
                {
                    Processing processing = transaction.getProcessing();

                    p4ResponseVO.setResponseTime(responseTimePZ.format(responseTime.parse(processing.getTimestamp())));
                    p4ResponseVO.setStatus("ACK".equalsIgnoreCase(processing.getResult()) ? "success" : "fail");
                    p4ResponseVO.setRemark(processing.getReason() + "(" + processing.getReturn() + ")");
                    p4ResponseVO.setDescription(processing.getStatus()+"("+processing.getState()+")");
                }
            }
        }
        catch (ParseException e)
        {
            logger.error("Parsing exception while parsing response time from the bank.",e);
        }
        return p4ResponseVO;
    }

    //Gateway Account details for credit card
    public GatewayAccount getGatewayAccount(String accountId)
    {
       return GatewayAccountService.getGatewayAccount(accountId);
    }

    public  URI getBaseURI()
    {

        return UriBuilder.fromUri("https://pi-demo.ywlpg.net").build();

    }

}
