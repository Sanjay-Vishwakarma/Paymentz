package api;



import com.vo.requestVOs.ApplicationManagerAuthenticationRequest;
import com.vo.responseVOs.ApplicationManagerResponse;
import com.sun.jersey.api.core.InjectParam;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.vo.requestVOs.ApplicationManagerAuthentication;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by NIKET on 2/4/2016.
 */


public interface ApplicationManagerApiService
{

    @Path("/submitMAF")
    @POST
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public ApplicationManagerResponse   submitMerchantApplicationForm(@InjectParam ApplicationManagerAuthenticationRequest authenticationRequest);


    @Path("/submitMAF")
    @POST
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_JSON)
    public ApplicationManagerResponse submitMerchantApplicationFormJSON( ApplicationManagerAuthentication authentication);


    @Path("/submitPrevet")
    @POST
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public ApplicationManagerResponse submitMerchantApplicationForSpeedProcess(@InjectParam ApplicationManagerAuthenticationRequest authenticationRequest);


    @Path("/submitPrevet")
    @POST
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_JSON)
    public ApplicationManagerResponse submitMerchantApplicationForSpeedProcessJSON( ApplicationManagerAuthentication requestApplicationManagerAuthentication);



    @Path("/uploadKycDocuments")
    @POST
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ApplicationManagerResponse uploadKycDocuments(FormDataMultiPart form);

    @Path("/MAFInquiry")
    @POST
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public ApplicationManagerResponse getApplicationManagerDetails(@InjectParam ApplicationManagerAuthenticationRequest requestApplicationManagerAuthentication);

    @Path("/MAFInquiry")
    @POST
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_JSON)
    public ApplicationManagerResponse getApplicationManagerDetailsJSON( ApplicationManagerAuthentication requestApplicationManagerAuthentication);

    @Path("/merchantLogin")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public ApplicationManagerResponse processMerchantLogin( @InjectParam ApplicationManagerAuthenticationRequest loginRequest);

    @Path("/merchantLogin")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public ApplicationManagerResponse processMerchantLoginJSON( ApplicationManagerAuthentication loginRequest);


    @Path("/signup")
    @POST
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public ApplicationManagerResponse merchantsignupJSON( ApplicationManagerAuthentication requestApplicationManagerAuthentication);


    @Path("/signup")
    @POST
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public ApplicationManagerResponse merchantsignup(@InjectParam ApplicationManagerAuthenticationRequest requestApplicationManagerAuthentication);


    @Path("/verify")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public ApplicationManagerResponse processVerification( @InjectParam ApplicationManagerAuthenticationRequest verifyRequest);




}