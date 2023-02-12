package transaction;

import com.transaction.vo.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


/**
 * Created with IntelliJ IDEA.
 * User: NIKET
 * Date: 6/8/15
 * Time: 4:16 PM
 * To change this template use File | Settings | File Templates.
 */
public interface DirectTransactionRESTService
{
    @Path("/transaction")
    @POST
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public DirectTransactionResponse processTransaction(DirectTransactionRequest directTransactionRequest);

    @Path("/refund")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public DirectRefundResponse processRefund(DirectRefundRequest directRefundRequest);

    @Path("/capture")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public DirectCaptureResponse processCapture(DirectCaptureRequest directCaptureRequest);

    @Path("/cancel")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public DirectCancelResponse processCancel(DirectCancelRequest directCancelRequest);

    @Path("/status")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public DirectInquiryResponse processStatus(DirectInquiryRequest directStatusRequest);
}
