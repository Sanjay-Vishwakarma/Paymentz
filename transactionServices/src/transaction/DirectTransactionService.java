package transaction;

import com.transaction.vo.*;


/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 5/28/15
 * Time: 3:48 PM
 * To change this template use File | Settings | File Templates.
 */
/*@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)*/
public interface DirectTransactionService
{
    /*@WebMethod(exclude = false)
    @WebResult(name="transactionResponse")*/
    public DirectTransactionResponse processTransaction(/*@WebParam(name = "transactionRequest")*/ DirectTransactionRequest directTransactionRequest);

   /* @WebMethod(exclude = false)
    @WebResult(name="refundResponse")*/
    public DirectRefundResponse processRefund(/*@WebParam(name = "refundRequest")*/ DirectRefundRequest directRefundRequest);

   /* @WebMethod(exclude = false)
    @WebResult(name="captureResponse")*/
    public DirectCaptureResponse processCapture(/*@WebParam(name = "captureRequest")*/ DirectCaptureRequest directCaptureRequest);

   /* @WebMethod(exclude = false)
    @WebResult(name="cancelResponse")*/
    public DirectCancelResponse processCancel(/*@WebParam(name = "cancelRequest")*/ DirectCancelRequest directCancelRequest);

    /*@WebMethod(exclude = false)
    @WebResult(name="inquiryResponse")*/
    public DirectInquiryResponse processInquiry(/*@WebParam(name = "inquiryRequest")*/ DirectInquiryRequest directInquiryRequest);
}
