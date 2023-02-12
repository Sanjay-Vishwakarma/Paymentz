package api;

import com.merchant.vo.requestVOs.MerchantRequest;
import com.merchant.vo.requestVOs.MerchantRequestVO;
import com.merchant.vo.requestVOs.MerchantServiceRequest;
import com.merchant.vo.requestVOs.MerchantServiceRequestVO;
import com.merchant.vo.responseVOs.MerchantResponseFlagsVO;
import com.merchant.vo.responseVOs.MerchantResponseVO;
import com.merchant.vo.responseVOs.MerchantServiceResponseVO;
import com.sun.jersey.api.core.InjectParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by Sneha on 9/1/2016.
 */
public interface MerchantService
{

    @Path("/merchantSignup")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public MerchantServiceResponseVO processMerchantSignUp(@InjectParam MerchantServiceRequest merchantServiceRequest);

    @Path("/merchantSignup")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public MerchantServiceResponseVO processMerchantSignUpJSON(MerchantServiceRequestVO merchantServiceRequestVO);

    @Path("/merchantLogin")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public MerchantServiceResponseVO processMerchantLogin(@InjectParam MerchantServiceRequest loginRequest);

    @Path("/merchantLogin")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public MerchantServiceResponseVO processMerchantLoginJSON(MerchantServiceRequestVO loginRequestVO);

    @Path("/merchantCurrencies")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public MerchantServiceResponseVO processGetMerchantCurrencies(@InjectParam MerchantServiceRequest merchantServiceRequestVO);

    @Path("/merchantCurrencies")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public MerchantServiceResponseVO processGetMerchantCurrenciesJSON(MerchantServiceRequestVO merchantServiceRequestVO);

    @Path("/generateOtp")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_FORM_URLENCODED})
    public MerchantServiceResponseVO processGenerateOTP(@InjectParam MerchantServiceRequestVO merchantServiceRequestVO);

    @Path("/changePassword")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public MerchantServiceResponseVO processChangePassword(@InjectParam MerchantServiceRequest merchantServiceRequest);

    @Path("/changePassword")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public MerchantServiceResponseVO processChangePasswordJSON(MerchantServiceRequestVO merchantServiceRequestVO);

    @Path("/forgetPassword")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public MerchantServiceResponseVO processForgetPassword(@InjectParam MerchantServiceRequest merchantServiceRequest);

    @Path("/forgetPassword")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public MerchantServiceResponseVO processForgetPasswordJSON(MerchantServiceRequestVO merchantServiceRequestVO);


    @Path("/generateAppOtp")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public MerchantServiceResponseVO processGenerateOTPTwoStep(@InjectParam MerchantServiceRequest merchantServiceRequest);

    @Path("/generateAppOtp")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public MerchantServiceResponseVO processGenerateOTPTwoStepJSON(MerchantServiceRequestVO merchantServiceRequestVO);


    @Path("/verifyAppOtp")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public MerchantServiceResponseVO ProcessVerifyGenerateAppOTP(@InjectParam MerchantServiceRequest merchantServiceRequestVO);

    @Path("/verifyAppOtp")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public MerchantServiceResponseVO ProcessVerifyGenerateAppOTPJSON(MerchantServiceRequestVO merchantServiceRequestVO);

    @Path("/authToken")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public MerchantServiceResponseVO generateAuthToken(@InjectParam MerchantServiceRequest loginRequest);

    @Path("/authToken")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public MerchantServiceResponseVO generateAuthTokenJSON(MerchantServiceRequestVO loginRequestvo);

    @Path("/customerSignup")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public MerchantServiceResponseVO processCustomerSignUp(@InjectParam MerchantServiceRequest merchantServiceRequest);

    @Path("/customerSignup")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public MerchantServiceResponseVO processCustomerSignUpJSON(MerchantServiceRequestVO merchantServiceRequestVO);

    @Path("/getAddress")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public MerchantServiceResponseVO getaddress(MerchantServiceRequestVO merchantServiceRequest);

    @Path("/updateAddress")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public MerchantServiceResponseVO updateAddress(MerchantServiceRequestVO merchantServiceRequest);

    @Path("/authFail")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_FORM_URLENCODED})
    public MerchantServiceResponseVO failResponse(@InjectParam MerchantServiceRequestVO merchantServiceRequestVO);

    @Path("/regenerateToken")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public MerchantServiceResponseVO getAuthToken(@InjectParam MerchantServiceRequest loginRequest);

    @Path("/regenerateToken")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public MerchantServiceResponseVO getAuthTokenJSON(MerchantServiceRequestVO loginRequestvo);

    @Path("/resendEmail")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public MerchantServiceResponseVO processemailVerification(  MerchantServiceRequestVO verifyRequest);

    @Path("/merchantLogout")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public MerchantServiceResponseVO processMerchantLogout(@InjectParam MerchantServiceRequest merchantServiceRequest);

    @Path("/merchantLogout")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public MerchantServiceResponseVO processMerchantLogoutJSON( MerchantServiceRequestVO verifyRequest);

    @Path("/sendReceiptEmail")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public MerchantServiceResponseVO processSendReceiptEmail(@InjectParam MerchantServiceRequest merchantServiceRequest);

    @Path("/sendReceiptEmail")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public MerchantServiceResponseVO processSendReceiptEmailJSON(MerchantServiceRequestVO merchantServiceRequestVO);

    @Path("/sendReceiptSms")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public MerchantServiceResponseVO processSendReceiptSms(@InjectParam MerchantServiceRequest merchantServiceRequest);

    @Path("/sendReceiptSms")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public MerchantServiceResponseVO processSendReceiptSmsJSON(MerchantServiceRequestVO merchantServiceRequestVO);


    @Path("/createOTP")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public MerchantResponseVO createOTP(@InjectParam MerchantRequest merchantRequest);

    @Path("/createOTP")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public MerchantResponseVO createOTPJSON(MerchantRequestVO merchantRequestVO);

    @Path("/verifyOtp")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public MerchantResponseVO processVerifyOTP(@InjectParam MerchantRequest merchantRequest);

    @Path("/verifyOtp")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public MerchantResponseVO processVerifyOTPJSON(MerchantRequestVO merchantRequestVO);

    @Path("/createLoginMerchantOTP")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public MerchantResponseVO createLoginMerchantOTP(@InjectParam MerchantRequest merchantRequest);

    @Path("/createLoginMerchantOTP")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public MerchantResponseVO createLoginMerchantOTPJSON(MerchantRequestVO merchantRequestVO);


    @Path("/verifyLoginMerchantOtp")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public MerchantResponseVO processVerifyLoginMerchantOTP(@InjectParam MerchantRequest merchantRequest);

    @Path("/verifyLoginMerchantOtp")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public MerchantResponseVO processVerifyLoginMerchantOTPJSON(MerchantRequestVO merchantRequestVO);

    @Path("/getMerchantFlags")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public MerchantResponseFlagsVO getMerchantFlag(@InjectParam MerchantRequest merchantRequest);

    @Path("/getMerchantFlags")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public MerchantResponseFlagsVO getMerchantFlagJSON(MerchantRequestVO merchantRequestVO);

    @Path("/getMerchantTheme")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public MerchantResponseVO getMerchantTheme(@InjectParam MerchantRequest merchantRequest);

    @Path("/getMerchantTheme")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public MerchantResponseVO getMerchantThemeJSON(MerchantRequestVO merchantRequestVO);



//todo  getMemberAllTerminalFlags


    @Path("/getMemberAllTerminalFlags")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public MerchantResponseVO getMemberAllTerminalFlags(@InjectParam MerchantRequest merchantRequest);

    @Path("/getMemberAllTerminalFlags")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public MerchantResponseVO getMemberAllTerminalFlagsJSON(MerchantRequestVO merchantRequestVO);
}