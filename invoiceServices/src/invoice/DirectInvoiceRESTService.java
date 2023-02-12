package invoice;

import com.merchant.vo.requestVOs.MerchantServiceRequest;
import com.merchant.vo.requestVOs.MerchantServiceRequestVO;
import com.merchant.vo.responseVOs.MerchantServiceResponseVO;
import com.sun.jersey.api.core.InjectParam;
import vo.restVO.requestvo.InvoiceRequest;
import vo.restVO.requestvo.InvoiceRequestVO;
import vo.restVO.resposnevo.Response;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by Sneha on 2/9/2017.
 */
public interface DirectInvoiceRESTService
{
    @Path("/generate")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response generate(@InjectParam InvoiceRequest paymentRequest);

    @Path("/generate")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes(MediaType.APPLICATION_JSON)
    public Response generateJSON(InvoiceRequestVO paymentRequest);


    @Path("/authToken")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public MerchantServiceResponseVO generateAuthToken(@InjectParam MerchantServiceRequest loginRequest);

    @Path("/authToken")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes(MediaType.APPLICATION_JSON)
    public MerchantServiceResponseVO generateAuthTokenJSON( MerchantServiceRequestVO loginRequest);

    @Path("/cancel")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response cancel(@InjectParam InvoiceRequest paymentRequest);

    @Path("/cancel")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response cancelJSON( InvoiceRequestVO paymentRequest);


    @Path("/regenerate")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response regenerate(@InjectParam InvoiceRequest paymentRequest);

    @Path("/regenerate")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response regenerateJSON( InvoiceRequestVO paymentRequest);

    @Path("/remind")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response remind(@InjectParam InvoiceRequest paymentRequest);

    @Path("/remind")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response remindJSON( InvoiceRequestVO paymentRequest);

    @Path("/inquiry")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response inquiry(@InjectParam InvoiceRequest paymentRequest);

    @Path("/inquiry")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response inquiryJSON( InvoiceRequestVO paymentRequest);

    @Path("/invoiceTransInquiry")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response invoiceTransInquiry(@InjectParam InvoiceRequest paymentRequest);


    @Path("/invoiceTransInquiry")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response invoiceTransInquiryJSON( InvoiceRequestVO paymentRequest);

    @Path("/getInvoice")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response getInvoice(@InjectParam InvoiceRequest paymentRequest);

    @Path("/getInvoice")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response getInvoiceJSON( InvoiceRequestVO paymentRequest);

    @Path("/getInvoiceDetails")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response getInvoiceDetails(@InjectParam InvoiceRequest paymentRequest);

    @Path("/getInvoiceDetails")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response getInvoiceDetailsJSON( InvoiceRequestVO paymentRequest);

    @Path("/updateInvoiceConfig")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response updateInvoiceConfigDetails(@InjectParam InvoiceRequest paymentRequest);

    @Path("/updateInvoiceConfig")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response updateInvoiceConfigDetailsJSON( InvoiceRequestVO paymentRequest);



    @Path("/getInvoiceConfig")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response getInvoiceConfigDetails(@InjectParam InvoiceRequest paymentRequest);


    @Path("/getInvoiceConfig")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response getInvoiceConfigDetailsJSON( InvoiceRequestVO paymentRequest);

    @Path("/getOrderID")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response getOrderID(@InjectParam InvoiceRequest paymentRequest);


    @Path("/getOrderID")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response getOrderIDJSON(InvoiceRequestVO paymentRequest);

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


   /* @Path("/demo")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_FORM_URLENCODED})
    public String test(@InjectParam InvoiceRequest paymentRequest);*/



}