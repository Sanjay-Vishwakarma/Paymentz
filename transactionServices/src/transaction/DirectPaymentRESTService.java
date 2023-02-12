package transaction;

import com.sun.jersey.api.core.InjectParam;
import com.transaction.vo.restVO.RequestVO.RestPaymentRequest;
import com.transaction.vo.restVO.RequestVO.RestPaymentRequestVO;
import com.transaction.vo.restVO.ResponseVO.Response;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by Sneha on 2/29/2016.
 */
public interface DirectPaymentRESTService
{
    //for Normal Pre Auth and Sale Transaction + generating token with (registration="true") + recurring transaction (recurringType="INITIAL")
    @Path("/payments")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response processTransaction(@InjectParam RestPaymentRequest paymentRequest);

    @Path("/payments")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response processTransactionJSON( RestPaymentRequestVO paymentRequest);

    @Path("/sendSMSCode")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response processSendSmsCode(@InjectParam RestPaymentRequest paymentRequest);

    @Path("/sendSMSCode")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response processSendSmsCodeJSON( RestPaymentRequestVO paymentRequest);

    //for Refund, Capture, Cancel, Repeated with TrackingID and Inquire the transaction
   @Path("/payments/{id}")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response processCaptureCancelRefund(@PathParam("id") String id, @InjectParam RestPaymentRequest paymentRequest);

    //for Refund, Capture, Cancel, Repeated with TrackingID and Inquire the transaction
  @Path("/payments/{id}")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response processCaptureCancelRefundJSON(@PathParam("id") String id, RestPaymentRequestVO paymentRequestVO);

    //for Delete/In Active generated token, normal token transaction and token transaction with (recurring="INITIAL" and "REPEATED")
    @Path("/paywithtoken/{id}")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response processRegistrationTransaction(@PathParam("id") String id, @InjectParam RestPaymentRequest paymentRequest);

    @Path("/paywithtoken/{id}")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response processRegistrationTransactionJSON(@PathParam("id") String id,RestPaymentRequestVO paymentRequestVO);

    //for Generating Stand alone registration
    @Path("/registrations")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response processStandAloneTokenRegistration(@InjectParam RestPaymentRequest paymentRequest);

    @Path("/registrations")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response processStandAloneTokenRegistrationJSON(RestPaymentRequestVO paymentRequestVO);

    /*//merchant sign up
    @Path("/merchantSignup")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_FORM_URLENCODED})
    public Response processMerchantSignUp(@InjectParam RestPaymentRequest paymentRequest);

    //customer sign up
    @Path("/customerSignup")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_FORM_URLENCODED})
    public Response customerSignup(@InjectParam RestPaymentRequest paymentRequest);*/

    @Path("/getCardsAndAccounts")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response getCardsAndAccounts(@InjectParam RestPaymentRequest paymentRequest);

    @Path("/getCardsAndAccounts")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response getCardsAndAccountsJSON(RestPaymentRequestVO paymentRequestVO);

    //Button Checkout
    @Path("/checkout")
    @GET
    @Produces({MediaType.TEXT_HTML})
    @Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_FORM_URLENCODED})
    public String processCheckout();

    @Path("/paywithtoken")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_FORM_URLENCODED})
    public Response invalidRegistrationId(@InjectParam RestPaymentRequest paymentRequest);

    //new
    @Path("/getTransactionDetails")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response getTransactionDetails(@InjectParam RestPaymentRequest paymentRequest);

    //new
    @Path("/getTransactionDetails")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response getTransactionDetailsJSON(RestPaymentRequestVO paymentRequestVO);

    @Path("/authToken")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response generateAuthToken(@InjectParam RestPaymentRequest loginRequest);

    @Path("/authToken")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response generateAuthTokenJSON(RestPaymentRequestVO loginRequestvo);

    @Path("/partnerAuthToken")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response generatePartnerAuthToken(@InjectParam RestPaymentRequest loginRequest);

    @Path("/partnerAuthToken")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response generatePartnerAuthTokenJSON(RestPaymentRequestVO loginRequestvo);


    @Path("/payout")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response processPayout(@InjectParam RestPaymentRequest paymentRequest);

    @Path("/exchangerDeposit")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response processExchangerDeposit(RestPaymentRequestVO paymentRequest);

    @Path("/customerValidation")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response processValidateCustomer(RestPaymentRequestVO paymentRequest);

    @Path("/paymentUpdate")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response processPaymentUpdate(RestPaymentRequestVO paymentRequest);

    @Path("/customerValidateAndUpdate")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response processValidateandUpdateforExchanger(RestPaymentRequestVO paymentRequest);

    @Path("/payout")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response processPayoutJSON(RestPaymentRequestVO paymentRequestVO);

    @Path("/regenerateToken")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response getAuthToken(@InjectParam RestPaymentRequest loginRequest);

    @Path("/regenerateToken")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response getAuthTokenJSON(RestPaymentRequestVO loginRequestvo);

    @Path("/validateWalletDetails")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response processValidateWalletDetails(RestPaymentRequestVO paymentRequestVO);

    @Path("/checkConfirmation")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response processCheckConfirmationStatus(RestPaymentRequestVO paymentRequestVO);


    @Path("/QRCheckout")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response processQRCheckout(@InjectParam RestPaymentRequest paymentRequest);

    @Path("/QRCheckout")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response processQRCheckoutJSON( RestPaymentRequestVO paymentRequest);

    @Path("/QRPayments")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response processQRTransaction(@InjectParam RestPaymentRequest paymentRequest);

    @Path("/QRPayments")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response processQRTransactionJSON( RestPaymentRequestVO paymentRequest);


    @Path("/QRPayments/{id}")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response processQRConfirmation(@PathParam("id") String id, @InjectParam RestPaymentRequest paymentRequest);

    @Path("/QRPayments/{id}")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response processQRConfirmationJSON(@PathParam("id") String id,RestPaymentRequestVO paymentRequestVO);

    //for QR Inquiry
    @Path("/inquiryStatus/{id}")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response processQRInquiryStatus(@PathParam("id") String id, @InjectParam RestPaymentRequest paymentRequest);

    //for Refund, Capture, Cancel, Repeated with TrackingID and Inquire the transaction
    @Path("/inquiryStatus/{id}")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response processQRInquiryStatusJSON(@PathParam("id") String id, RestPaymentRequestVO paymentRequestVO);

    //for EMI
    @Path("/getInstallmentWithToken/{id}")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response processGetInstallmentWithToken(@PathParam("id") String id, @InjectParam RestPaymentRequest paymentRequest);

    //for EMI
    @Path("/getInstallmentWithToken/{id}")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response processGetInstallmentWithTokenJSON(@PathParam("id") String id, RestPaymentRequestVO paymentRequestVO);

    @Path("/getInstallmentWithToken")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_FORM_URLENCODED})
    public Response invalidRegistrationIdForInstallment(@InjectParam RestPaymentRequest paymentRequest);

    @Path("/initiateAuthentication")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response processInitiateJSON(RestPaymentRequestVO restPaymentRequestVO);

    @Path("/authenticate")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response processAuthenticateJSON(RestPaymentRequestVO restPaymentRequestVO);

    @Path("/getPaymentAndCardType")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response processGetPaymentAndCardType(@InjectParam RestPaymentRequest paymentRequest);

    @Path("/getPaymentAndCardType")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response processGetPaymentAndCardTypeJSON( RestPaymentRequestVO paymentRequest);

    @Path("/saveTransactionReceipt")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response processSaveTransactionReceipt(@InjectParam RestPaymentRequest paymentRequest);

    @Path("/saveTransactionReceipt")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public  Response processSaveTransactionReceiptJSON(RestPaymentRequestVO paymentRequestVO);

    @Path("/getTransactionList")   //endpoint of url
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML}) // o/p
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})               // i/p
    public Response processGetTransactionList(@InjectParam RestPaymentRequest paymentRequest);

    @Path("/getTransactionList")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response processGetTransactionListJSON(RestPaymentRequestVO paymentRequestVO);

    @Path("/customerCardWhitelisting")   //endpoint of url
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response processCustomerCardWhitelisting(@InjectParam RestPaymentRequest paymentRequest);

    @Path("/customerCardWhitelisting")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response processCustomerCardWhitelistingJSON(RestPaymentRequestVO paymentRequestVO);

    @Path("/getSalesReport")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response processGetDailySalesReport(@InjectParam RestPaymentRequest paymentRequest);

    @Path("/getSalesReport")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response processGetDailySalesReportJSON( RestPaymentRequestVO paymentRequest);

    @Path("/generateTransactionOTP")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response createOTP(@InjectParam RestPaymentRequest merchantRequest);

    @Path("/generateTransactionOTP")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response createOTPJSON(RestPaymentRequestVO merchantRequestVO);

    @Path("/verifyTransactionOTP")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response processVerifyOTP(@InjectParam RestPaymentRequest merchantRequest);

    @Path("/verifyTransactionOTP")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response processVerifyOTPJSON(RestPaymentRequestVO merchantRequestVO);



  //for payout balance  Inquiry
  @Path("/getPayoutBalance/{merchnatid}")
  @POST
  @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
  @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
  public Response getPayoutBalance(@PathParam("merchnatid") String id, @InjectParam RestPaymentRequest paymentRequest);

  //for payout balance  Inquiry
  @Path("/getPayoutBalance/{merchnatid}")
  @POST
  @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
  @Consumes({MediaType.APPLICATION_JSON})
  public Response getPayoutBalanceJSON(@PathParam("merchnatid") String id, RestPaymentRequestVO paymentRequestVO);

  //for updateUpiTransactionDetails
  @Path("/updateUpiTransactionDetails")
  @POST
  @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
  @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
  public Response updateUpiTransactionDetails( @InjectParam RestPaymentRequest paymentRequest);

  //for updateUpiTransactionDetailsJSON
  @Path("/updateUpiTransactionDetails")
  @POST
  @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
  @Consumes({MediaType.APPLICATION_JSON})
  public Response updateUpiTransactionDetailsJSON( RestPaymentRequestVO paymentRequestVO);


}