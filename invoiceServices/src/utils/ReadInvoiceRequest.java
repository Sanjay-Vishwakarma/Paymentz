package utils;

import com.directi.pg.Functions;
import com.directi.pg.core.GatewayAccountService;
import com.invoice.vo.BillingAddress;
import com.invoice.vo.InvoiceVO;
import com.invoice.vo.Pagination;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.PaginationVO;
import com.merchant.vo.requestVOs.*;
import com.payment.validators.vo.CommonValidatorVO;
import vo.restVO.requestvo.InvoiceRequest;
import vo.restVO.requestvo.InvoiceRequestVO;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Sneha on 2/9/2017.
 */
public class ReadInvoiceRequest
{
    private static Functions functions = new Functions();

    public InvoiceVO readGenerateInvoiceRequest(InvoiceRequest request, HttpServletRequest httpServletRequest)
    {
        InvoiceVO invoiceVO = new InvoiceVO();
        //getting merchant authentication information
        invoiceVO = populateAuthenticationDetails(invoiceVO, request);
        if(functions.isValueNull(request.getPaymentBrand()))
        {
            invoiceVO.setPaymentBrand(request.getPaymentBrand());
            invoiceVO.setCardTypeId(GatewayAccountService.getCardId(request.getPaymentBrand()));
        }
        if(functions.isValueNull(request.getPaymentMode()))
        {
            invoiceVO.setPaymentMode(request.getPaymentMode());
            invoiceVO.setPaymodeid(GatewayAccountService.getPaymentId(request.getPaymentMode()));
        }

        //invoiceVO.setItemList(request.getItemList());
        invoiceVO.setAmount(request.getAmount());
        invoiceVO.setDescription(request.getMerchantInvoiceId());
        invoiceVO.setRedirecturl(request.getMerchantRedirectUrl());
        invoiceVO.setCurrency(request.getCurrency());
        invoiceVO.setOrderDescription(request.getMerchantOrderDescription());
        invoiceVO.setCustName(request.getCustomer().getGivenName());
        invoiceVO.setEmail(request.getCustomer().getEmail());
        invoiceVO.setCountry(request.getCustomer().getCountry());
        invoiceVO.setCity(request.getCustomer().getCity());
        invoiceVO.setZip(request.getCustomer().getPostcode());
        invoiceVO.setTelCc(request.getCustomer().getTelnocc());
        invoiceVO.setTelno(request.getCustomer().getMobile());
        invoiceVO.setStreet(request.getCustomer().getStreet());
        invoiceVO.setState(request.getBillingAddress().getState());
        invoiceVO.setMerchantIpAddress(functions.getIpAddress(httpServletRequest));
        invoiceVO.setCancelReason(request.getCancelReason());
        invoiceVO.setExpirationPeriod(request.getInvoiceExpirationPeriod());
        invoiceVO.setGST(request.getGst());


        return invoiceVO;
    }

    public InvoiceVO readGenerateInvoiceRequestJSON(InvoiceRequestVO request, HttpServletRequest httpServletRequest)
    {
        InvoiceVO invoiceVO = new InvoiceVO();
        //getting merchant authentication information
        invoiceVO = populateAuthenticationDetailsJSON(invoiceVO, request);
        if(functions.isValueNull(request.getPaymentBrand()))
        {
            invoiceVO.setPaymentBrand(request.getPaymentBrand());
            invoiceVO.setCardTypeId(GatewayAccountService.getCardId(request.getPaymentBrand()));
        }
        if(functions.isValueNull(request.getPaymentMode()))
        {
            invoiceVO.setPaymentMode(request.getPaymentMode());
            invoiceVO.setPaymodeid(GatewayAccountService.getPaymentId(request.getPaymentMode()));
        }

        //new item list
        invoiceVO.setProductList(request.getItemList());
        invoiceVO.setAmount(request.getAmount());
        invoiceVO.setDescription(request.getMerchantInvoiceId());
        invoiceVO.setRedirecturl(request.getMerchantRedirectUrl());
        invoiceVO.setCurrency(request.getCurrency());
        invoiceVO.setOrderDescription(request.getMerchantOrderDescription());

        if (request.getCustomer() != null)
        {
            if (functions.isValueNull(request.getCustomer().getGivenName()))
                invoiceVO.setCustName(request.getCustomer().getGivenName());
            if (functions.isValueNull(request.getCustomer().getEmail()))
                invoiceVO.setEmail(request.getCustomer().getEmail());
            if (functions.isValueNull(request.getCustomer().getCountry()))
                invoiceVO.setCountry(request.getCustomer().getCountry());
            if (functions.isValueNull(request.getCustomer().getCity()))
                invoiceVO.setCity(request.getCustomer().getCity());
            if (functions.isValueNull(request.getCustomer().getPostcode()))
                invoiceVO.setZip(request.getCustomer().getPostcode());
            if (functions.isValueNull(request.getCustomer().getTelnocc()))
                invoiceVO.setTelCc(request.getCustomer().getTelnocc());
            if (functions.isValueNull(request.getCustomer().getMobile()))
                invoiceVO.setTelno(request.getCustomer().getMobile());
            if (functions.isValueNull(request.getCustomer().getStreet()))
                invoiceVO.setStreet(request.getCustomer().getStreet());
        }

        if (request.getMerchant()!=null){
            if(functions.isValueNull(request.getMerchant().getLoginName()))
                invoiceVO.setUserName(request.getMerchant().getLoginName());
        }

        if (functions.isValueNull(request.getGst()))
            invoiceVO.setGST(request.getGst());
        //invoiceVO.setState(request.getCustomer());
        invoiceVO.setMerchantIpAddress(functions.getIpAddress(httpServletRequest));
        invoiceVO.setCancelReason(request.getCancelReason());
        invoiceVO.setExpirationPeriod(request.getInvoiceExpirationPeriod());
        invoiceVO.setGST(request.getGst());
        invoiceVO.setQuantityTotal(request.getQuantityTotal());
        //System.out.println("customer details list size----" + request.getCustomerDetailList().size());
        //invoiceVO.setCustomerDetailList(request.getCustomerDetailList());
        /*CustomerDetailList customerDetailList = new CustomerDetailList();
        customerDetailList.setCustomerName("xyz");
        customerDetailList.setCustomerEmail("");
        customerDetailList.setCustomerPhoneCC("91");
        customerDetailList.setCustomerPhone("9867493501");
        customerDetailList.setAmount("50.00");

        customerDetailList.setCustomerName("abc");
        customerDetailList.setCustomerEmail("");
        customerDetailList.setCustomerPhoneCC("91");
        customerDetailList.setCustomerPhone("9969019024");
        customerDetailList.setAmount("40.00");
        //request.setCustomerDetailList((List<CustomerDetailList>) customerDetailList);
        System.out.println("customer details list size----" + request.getCustomerDetailList().size());*/
        invoiceVO.setCustomerDetailList(request.getCustomerDetailList());

        return invoiceVO;
    }


    public InvoiceRequestVO readCancelInvoiceRequest(InvoiceRequest request)
    {
        InvoiceRequestVO invoiceRequestVO = new InvoiceRequestVO();
        invoiceRequestVO.setAuthentication(getAuthenticationVO(request.getAuthentication()));
        invoiceRequestVO.setMerchant(getMerchantVO(request.getMerchant()));
        invoiceRequestVO.setInvoiceId(request.getInvoiceId());
        invoiceRequestVO.setMerchantRedirectUrl(request.getMerchantRedirectUrl());
        invoiceRequestVO.setCancelReason(request.getCancelReason());
        return invoiceRequestVO;
    }


    public InvoiceVO readCancelInvoiceRequestJSON(InvoiceRequestVO request, HttpServletRequest httpServletRequest)
    {
        InvoiceVO invoiceVO = new InvoiceVO();
        invoiceVO = populateAuthenticationDetailsJSON(invoiceVO, request);
        if (request.getMerchant()!=null)
            invoiceVO.setUserName(request.getMerchant().getLoginName());
        invoiceVO.setInvoiceno(request.getInvoiceId());
        invoiceVO.setRedirecturl(request.getMerchantRedirectUrl());
        invoiceVO.setCancelReason(request.getCancelReason());
        return invoiceVO;
    }



    public InvoiceRequestVO readRegenerateInvoiceRequest(InvoiceRequest request)
    {
        InvoiceRequestVO invoiceRequestVO = new InvoiceRequestVO();
        invoiceRequestVO.setAuthentication(getAuthenticationVO(request.getAuthentication()));
        invoiceRequestVO.setMerchant(getMerchantVO(request.getMerchant()));
        invoiceRequestVO.setInvoiceId(request.getInvoiceId());
        invoiceRequestVO.setMerchantRedirectUrl(request.getMerchantRedirectUrl());
        invoiceRequestVO.setInvoiceExpirationPeriod(request.getInvoiceExpirationPeriod());
        invoiceRequestVO.setMerchantInvoiceId(request.getMerchantInvoiceId());

        return invoiceRequestVO;
    }

    public InvoiceVO readRegenerateInvoiceRequestJSON(InvoiceRequestVO request, HttpServletRequest httpServletRequest)
    {
        InvoiceVO invoiceVO = new InvoiceVO();
        invoiceVO = populateAuthenticationDetailsJSON(invoiceVO, request);
        if (request.getMerchant()!=null)
            invoiceVO.setUserName(request.getMerchant().getLoginName());
        invoiceVO.setInvoiceno(request.getInvoiceId());
        invoiceVO.setRedirecturl(request.getMerchantRedirectUrl());
        invoiceVO.setExpirationPeriod(request.getInvoiceExpirationPeriod());
        invoiceVO.setDescription(request.getMerchantInvoiceId());
        return invoiceVO;
    }

    public InvoiceRequestVO readRemindInvoiceRequest(InvoiceRequest request)
    {
        InvoiceRequestVO invoiceRequestVO = new InvoiceRequestVO();
        invoiceRequestVO.setAuthentication(getAuthenticationVO(request.getAuthentication()));
        invoiceRequestVO.setInvoiceId(request.getInvoiceId());
        invoiceRequestVO.setMerchantRedirectUrl(request.getMerchantRedirectUrl());
        return invoiceRequestVO;
    }

    public InvoiceVO readRemindInvoiceRequestJSON(InvoiceRequestVO request, HttpServletRequest httpServletRequest)
    {
        InvoiceVO invoiceVO = new InvoiceVO();
        invoiceVO = populateAuthenticationDetailsJSON(invoiceVO, request);
        invoiceVO.setInvoiceno(request.getInvoiceId());
        invoiceVO.setRedirecturl(request.getMerchantRedirectUrl());
        return invoiceVO;
    }


    public InvoiceRequestVO readInquiryInvoiceRequesttrans(InvoiceRequest request)
    {
        InvoiceRequestVO invoiceRequestVO = new InvoiceRequestVO();
        invoiceRequestVO.setAuthentication(getAuthenticationVO(request.getAuthentication()));
        invoiceRequestVO.setInvoiceId(request.getInvoiceId());
        invoiceRequestVO.setMerchantInvoiceId(request.getMerchantInvoiceId());
        return invoiceRequestVO;
    }

    public InvoiceVO readInquiryInvoiceRequesttransJSON(InvoiceRequestVO request, HttpServletRequest httpServletRequest)
    {
        InvoiceVO invoiceVO = new InvoiceVO();
        invoiceVO = populateAuthenticationDetailsJSON(invoiceVO, request);
        invoiceVO.setDescription(request.getMerchantInvoiceId());
        invoiceVO.setInvoiceno(request.getInvoiceId());
        return invoiceVO;
    }


    public InvoiceRequestVO readInquiryInvoiceRequest(InvoiceRequest request)
    {
        InvoiceRequestVO invoiceRequestVO = new InvoiceRequestVO();
        invoiceRequestVO.setAuthentication(getAuthenticationVO(request.getAuthentication()));
        invoiceRequestVO.setMerchantRedirectUrl(request.getMerchantRedirectUrl());
        invoiceRequestVO.setInvoiceId(request.getInvoiceId());
        invoiceRequestVO.setMerchantInvoiceId(request.getMerchantInvoiceId());
        return invoiceRequestVO;
    }
    public InvoiceVO readInquiryInvoiceRequestJSON(InvoiceRequestVO request, HttpServletRequest httpServletRequest)
    {
        InvoiceVO invoiceVO = new InvoiceVO();
        invoiceVO = populateAuthenticationDetailsJSON(invoiceVO, request);
        invoiceVO.setDescription(request.getMerchantInvoiceId());
        invoiceVO.setMemberid(request.getAuthentication().getMemberId());
        invoiceVO.setInvoiceno(request.getInvoiceId());
        return invoiceVO;
    }

    private InvoiceVO populateAuthenticationDetails(InvoiceVO invoiceVO,InvoiceRequest request)
    {
        invoiceVO.setMemberid(request.getAuthentication().getMemberId());
        invoiceVO.setChecksum(request.getAuthentication().getChecksum());
        if(functions.isValueNull(request.getAuthentication().getTerminalId()))
            invoiceVO.setTerminalid(request.getAuthentication().getTerminalId());

        return invoiceVO;
    }

    private InvoiceVO populateAuthenticationDetailsJSON(InvoiceVO invoiceVO,InvoiceRequestVO request)
    {
        invoiceVO.setMemberid(request.getAuthentication().getMemberId());
        invoiceVO.setChecksum(request.getAuthentication().getChecksum());
        if(functions.isValueNull(request.getAuthentication().getTerminalId()))
            invoiceVO.setTerminalid(request.getAuthentication().getTerminalId());

        return invoiceVO;
    }


    public InvoiceRequestVO getInvoiceRequest(InvoiceRequest request)
    {
        InvoiceRequestVO invoiceRequestVO = new InvoiceRequestVO();
        PaginationVO paginationVO = new PaginationVO();
        invoiceRequestVO.setAuthentication(getAuthenticationVO(request.getAuthentication()));
        invoiceRequestVO.setMerchant(getMerchantVO(request.getMerchant()));
        invoiceRequestVO.setCurrency(request.getCurrency());
        invoiceRequestVO.setMerchantInvoiceId(request.getMerchantInvoiceId());
        invoiceRequestVO.setPagination(getpaginationVO(request.getPagination()));
        return invoiceRequestVO;
    }
    public InvoiceVO getInvoiceRequestJSON(InvoiceRequestVO request)
    {
        InvoiceVO invoiceVO = new InvoiceVO();
        invoiceVO = populateAuthenticationDetailsJSON(invoiceVO, request);
        PaginationVO paginationVO = new PaginationVO();
        paginationVO.setPageNo(request.getPagination().getPageno());
        paginationVO.setRecordsPerPage(request.getPagination().getRecords());
        paginationVO.setStartdate(request.getPagination().getFromdate());
        paginationVO.setEnddate(request.getPagination().getTodate());
        paginationVO.setStart(request.getPagination().getStart());
        paginationVO.setEnd(request.getPagination().getEnd());
        if (request.getMerchant()!=null)
            invoiceVO.setUserName(request.getMerchant().getLoginName());
        invoiceVO.setInvoiceAction(request.getActionType());
        invoiceVO.setCurrency(request.getCurrency());
        invoiceVO.setPaginationVO(paginationVO);

        return invoiceVO;

    }


    public InvoiceRequestVO setInvoiceConfigRequest(InvoiceRequest request)
    {
        InvoiceRequestVO invoiceRequestVO = new InvoiceRequestVO();
        invoiceRequestVO.setAuthentication(getAuthenticationVO(request.getAuthentication()));
        if (functions.isValueNull(request.getCurrency()))
            invoiceRequestVO.setCurrency(request.getCurrency());
        if (functions.isValueNull(request.getInitial()))
            invoiceRequestVO.setInitial(request.getInitial());
        if (functions.isValueNull(request.getMerchantRedirectUrl()))
            invoiceRequestVO.setMerchantRedirectUrl(request.getMerchantRedirectUrl());
        if (functions.isValueNull(request.getInvoiceExpirationPeriod()))
            invoiceRequestVO.setInvoiceExpirationPeriod(request.getInvoiceExpirationPeriod());
        invoiceRequestVO.setActionType(request.getActionType());
        invoiceRequestVO.setGst(request.getGst());
        invoiceRequestVO.setIssms(request.getIssms());
        invoiceRequestVO.setIsemail(request.getIsemail());
        invoiceRequestVO.setIsapp(request.getIsapp());
        invoiceRequestVO.setPaymentterms(request.getPaymentterms());
        invoiceRequestVO.setDuedate(request.getDuedate());
        invoiceRequestVO.setLatefee(request.getLatefee());
        invoiceRequestVO.setIsduedate(request.getIsduedate());
        invoiceRequestVO.setIslatefee(request.getIslatefee());
        invoiceRequestVO.setUnit(request.getUnit());
        if (request.getUnitList() != null)
        invoiceRequestVO.setUnitList(request.getUnitList());
        if (request.getDefaultProductList() != null)
            invoiceRequestVO.setDefaultProductList(request.getDefaultProductList());
        if (request.getIsSplitInvoice() != null)
            invoiceRequestVO.setIsSplitInvoice(request.getIsSplitInvoice());

        return invoiceRequestVO;
    }
    public InvoiceVO setInvoiceConfigRequestJSON(InvoiceRequestVO request)
    {
        InvoiceVO invoiceVO = new InvoiceVO();
        invoiceVO = populateAuthenticationDetailsJSON(invoiceVO, request);
        if (functions.isValueNull(request.getCurrency()))
            invoiceVO.setCurrency(request.getCurrency());
        if (functions.isValueNull(request.getInitial()))
            invoiceVO.setInitial(request.getInitial());
        if (functions.isValueNull(request.getMerchantRedirectUrl()))
            invoiceVO.setRedirecturl(request.getMerchantRedirectUrl());
        if (functions.isValueNull(request.getInvoiceExpirationPeriod()))
            invoiceVO.setExpirationPeriod(request.getInvoiceExpirationPeriod());
        invoiceVO.setInvoiceAction(request.getActionType());
        invoiceVO.setGST(request.getGst());
        invoiceVO.setIssms(request.getIssms());
        invoiceVO.setIsemail(request.getIsemail());
        invoiceVO.setIsapp(request.getIsapp());
//        invoiceVO.setPaymentterms(request.getPaymentterms());
        invoiceVO.setDuedate(request.getDuedate());
        invoiceVO.setLatefee(request.getLatefee());
        invoiceVO.setIsduedate(request.getIsduedate());
        invoiceVO.setIslatefee(request.getIslatefee());
        invoiceVO.setUnit(request.getUnit());
        if (request.getUnitList() != null)
        invoiceVO.setDefaultunitList(request.getUnitList());
        if (request.getDefaultProductList() != null)
            invoiceVO.setDefaultProductList(request.getDefaultProductList());
        if (request.getIsSplitInvoice() != null)
            invoiceVO.setIsSplitInvoice(request.getIsSplitInvoice());


        return invoiceVO;
    }

    public InvoiceRequestVO getInvoiceConfigRequest(InvoiceRequest request)
    {
        InvoiceRequestVO invoiceRequestVO = new InvoiceRequestVO();
        invoiceRequestVO.setAuthentication(getAuthenticationVO(request.getAuthentication()));
        return invoiceRequestVO;
    }
    public  InvoiceVO getInvoiceConfigRequestJSON(InvoiceRequestVO request)

    {
        InvoiceVO invoiceVO = new InvoiceVO();
        invoiceVO =populateAuthenticationDetailsJSON(invoiceVO,request);

        return invoiceVO;
    }


    public InvoiceRequestVO getOrderIdRequest(InvoiceRequest request)
    {
        InvoiceRequestVO invoiceRequestVO = new InvoiceRequestVO();
        invoiceRequestVO.setAuthentication(getAuthenticationVO(request.getAuthentication()));

        return invoiceRequestVO;
    }

    public InvoiceVO getOrderIdRequestJSON(InvoiceRequestVO request)
    {
        InvoiceVO invoiceVO = new InvoiceVO();
        invoiceVO = populateAuthenticationDetailsJSON(invoiceVO, request);
        return invoiceVO;
    }
    public CommonValidatorVO readRequestForMerchantLogin(MerchantServiceRequest request)
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        merchantDetailsVO.setLogin(request.getMerchant().getLoginName());
        merchantDetailsVO.setPassword(request.getMerchant().getNewPassword());
        commonValidatorVO.setParetnerId(request.getAuthentication().getPartnerId());
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        return commonValidatorVO;
    }

    public CommonValidatorVO readRequestForMerchantLoginJSON(MerchantServiceRequestVO request)
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        merchantDetailsVO.setLogin(request.getMerchant().getLoginName());
        merchantDetailsVO.setPassword(request.getMerchant().getNewPassword());
        merchantDetailsVO.setKey(request.getAuthentication().getsKey());
        commonValidatorVO.setParetnerId(request.getAuthentication().getPartnerId());
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        return commonValidatorVO;
    }


    public MerchantServiceRequestVO getMerchantServiceRequestVO(MerchantServiceRequest merchantServiceRequest)
    {
        MerchantServiceRequestVO merchantServiceRequestVO = new MerchantServiceRequestVO();

        merchantServiceRequestVO.setAuthentication(getAuthenticationVO(merchantServiceRequest.getAuthentication()));
        merchantServiceRequestVO.setCustomer(getCustomerVO(merchantServiceRequest.getCustomer()));
        merchantServiceRequestVO.setMerchant(getMerchantVO(merchantServiceRequest.getMerchant()));

        return merchantServiceRequestVO;
    }

    public InvoiceRequestVO getInvoiceRequestVO(InvoiceRequest request)
    {
        InvoiceRequestVO invoiceRequestVO = new InvoiceRequestVO();
        InvoiceVO invoiceVO = new InvoiceVO();
        invoiceRequestVO.setAuthentication(getAuthenticationVO(request.getAuthentication()));
        invoiceRequestVO.setCustomer(getCustomerVO(request.getCustomer()));
        invoiceRequestVO.setMerchant(getMerchantVO(request.getMerchant()));
        invoiceRequestVO.setBillingAddress(getBillingVO(request.getBillingAddress()));
        invoiceRequestVO.setPagination(getpaginationVO(request.getPagination()));
        invoiceRequestVO.setAmount(request.getAmount());
        invoiceRequestVO.setMerchantTransactionId(request.getMerchantTransactionId());
        invoiceRequestVO.setMerchantRedirectUrl(request.getMerchantRedirectUrl());
        invoiceRequestVO.setCurrency(request.getCurrency());
        invoiceRequestVO.setInvoiceExpirationPeriod(request.getInvoiceExpirationPeriod());
        invoiceRequestVO.setItemList(request.getItemList());
        if(functions.isValueNull(request.getPaymentBrand()))
        {
            invoiceRequestVO.setPaymentBrand(request.getPaymentBrand());
            invoiceVO.setCardTypeId(GatewayAccountService.getCardId(request.getPaymentBrand()));
        }
        if(functions.isValueNull(request.getPaymentMode()))
        {
            invoiceRequestVO.setPaymentMode(request.getPaymentMode());
            invoiceVO.setPaymodeid(GatewayAccountService.getPaymentId(request.getPaymentMode()));
        }
        invoiceRequestVO.setGst(request.getGst());
        invoiceRequestVO.setQuantityTotal(request.getQuantityTotal());
        invoiceRequestVO.setMerchantInvoiceId(request.getMerchantInvoiceId());
        invoiceRequestVO.setCustomerDetailList(request.getCustomerDetailList());
        invoiceRequestVO.setMerchantOrderDescription(request.getMerchantOrderDescription());

        return invoiceRequestVO;
    }

    private MerchantVO getMerchantVO(Merchant merchant)
    {

        MerchantVO merchantVO = new MerchantVO();

        merchantVO.setLoginName(merchant.getLoginName());
        merchantVO.setNewPassword(merchant.getNewPassword());
        merchantVO.setConPassword(merchant.getConPassword());
        merchantVO.setBirthDate(merchant.getBirthDate());
        merchantVO.setEmail(merchant.getEmail());
        merchantVO.setCompanyName(merchant.getCompanyName());
        merchantVO.setContactName(merchant.getContactName());
        merchantVO.setGivenName(merchant.getGivenName());
        merchantVO.setCountry(merchant.getCountry());
        merchantVO.setMobilecountrycode(merchant.getMobilecountrycode());
        merchantVO.setOtp(merchant.getOtp());
        merchantVO.setTelcc(merchant.getTelcc());
        merchantVO.setWebsite(merchant.getWebsite());
        merchantVO.setSurname(merchant.getSurname());
        merchantVO.setPostcode(merchant.getPostcode());
        merchantVO.setSex(merchant.getSex());

        return merchantVO;
    }

    private CustomerVO getCustomerVO(Customer customer)
    {
        CustomerVO customerVO = new CustomerVO();

        customerVO.setEmail(customer.getEmail());
        customerVO.setGivenName(customer.getGivenName());
        customerVO.setEmail(customer.getEmail());
        customerVO.setCountry(customer.getCountry());
        customerVO.setCity(customer.getCity());
        customerVO.setBirthDate(customer.getBirthDate());
        customerVO.setStreet(customer.getStreet());
        customerVO.setTelnocc(customer.getTelnocc());
        customerVO.setMobile(customer.getMobile());
        customerVO.setPostcode(customer.getPostcode());


        return customerVO;
    }
    private BillingAddressVO getBillingVO(BillingAddress billingAddress)

    {
        BillingAddressVO billingAddressVo = new BillingAddressVO();
        billingAddressVo.setState(billingAddress.getState());
        return billingAddressVo;
    }

    private Pagination getpaginationVO(Pagination pagination)

    {
        Pagination paginationVo = new Pagination();
        paginationVo.setStart(pagination.getStart());
        paginationVo.setEnd(pagination.getEnd());
        paginationVo.setFromdate(pagination.getFromdate());
        paginationVo.setTodate(pagination.getTodate());
        paginationVo.setPageno(pagination.getPageno());
        paginationVo.setRecords(pagination.getRecords());

        return paginationVo;
    }



    private AuthenticationVO getAuthenticationVO(Authentication authentication)
    {
        AuthenticationVO authenticationVO = new AuthenticationVO();
        authenticationVO.setPartnerId(authentication.getPartnerId());
        authenticationVO.setChecksum(authentication.getChecksum());
        authenticationVO.setTerminalId(authentication.getTerminalId());
        authenticationVO.setMemberId(authentication.getMemberId());
        authenticationVO.setPassword(authentication.getPassword());
        authenticationVO.setsKey(authentication.getsKey());

        return authenticationVO;
    }


    public MerchantServiceRequestVO getNewAuthTokenRequest(MerchantServiceRequest request)
    {
        MerchantServiceRequestVO requestVO = new MerchantServiceRequestVO();
        AuthenticationVO authenticationVO = new AuthenticationVO();
        requestVO.setAuthToken(request.getAuthToken());
        authenticationVO.setPartnerId(request.getAuthentication().getPartnerId());
        requestVO.setAuthentication(authenticationVO);

        return requestVO;
    }

    public CommonValidatorVO readRequestForgetNewAuthToken(MerchantServiceRequestVO requestVO)
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        commonValidatorVO.setAuthToken(requestVO.getAuthToken());
        commonValidatorVO.setParetnerId(requestVO.getAuthentication().getPartnerId());

        return commonValidatorVO;
    }




}
