package transaction;

import com.sun.jersey.api.core.InjectParam;
import com.transaction.vo.restVO.RequestVO.RestPaymentRequest;
import com.transaction.vo.restVO.RequestVO.RestPaymentRequestVO;
import com.transaction.vo.restVO.ResponseVO.FraudDefender;
import com.transaction.vo.restVO.ResponseVO.Response;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by Admin on 4/16/2020.
 */
public interface DirectFrauDefenderRestService
{
    @Path("/query")   //endpoint of url
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML}) // o/p
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})               // i/p
    public FraudDefender queryFraudefender(@InjectParam RestPaymentRequest paymentRequest);


    @Path("/query")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public FraudDefender queryFraudefenderJSON(RestPaymentRequestVO paymentRequestVO);

    @Path("/refund")   //endpoint of url
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML}) // o/p
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})               // i/p
    public FraudDefender refundFraudefender(@InjectParam RestPaymentRequest paymentRequest);


    @Path("/refund")
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON})
    public FraudDefender refundFraudefenderJSON(RestPaymentRequestVO paymentRequestVO);

}
