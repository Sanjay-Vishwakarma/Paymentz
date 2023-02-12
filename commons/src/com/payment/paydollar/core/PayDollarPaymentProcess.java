package com.payment.paydollar.core;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.paymentgateway.CredoraxPaymentGateway;
import com.directi.pg.core.paymentgateway.PayDollarPaymentGateway;
import com.directi.pg.core.valueObjects.PayDollarRequestVO;
import com.directi.pg.core.valueObjects.PayDollarResponseVO;
import com.directi.pg.core.valueObjects.UGSPayResponseVO;
import com.payment.common.core.*;
import com.payment.credorax.core.CredoraxRequestVO;
import com.payment.credorax.core.CredoraxResponseVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.request.*;
import com.payment.response.PZCancelResponse;
import com.payment.response.PZInquiryResponse;
import com.payment.response.PZRefundResponse;
import com.payment.response.PZResponseStatus;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 5/17/13
 * Time: 10:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class PayDollarPaymentProcess extends CommonPaymentProcess
{

    private static Logger log = new Logger(PayDollarPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger= new TransactionLogger(PayDollarPaymentProcess.class.getName());
    TransactionEntry transactionEntry = null;

    
    /*public PZRefundResponse refund(PZRefundRequest refundRequest)
    {
        String trackingid=refundRequest.getTrackingId()+"";

        PZRefundResponse resp=new PZRefundResponse();
        
        boolean isFraud =  refundRequest.isFraud();
        boolean isAdmin =  refundRequest.isAdmin();
        String refundReason = refundRequest.getRefundReason();

        if(trackingid==null || trackingid.equals(""))
        {
            resp.setResponseDesceiption("FAILED due to Invalid Tracking ID");
            resp.setStatus(PZResponseStatus.FAILED);
        }

        Double refundAmount= refundRequest.getRefundAmount();
        
        String transid="",payref="",accountid="",amount="";
        String query="select paymentid,accountid,captureamount,transid from transaction_common where trackingid="+trackingid+" and status='capturesuccess'";
        PayDollarResponseVO payDollarResponseVO=null;
        Connection conn=null;

        BigDecimal refundamount = null;

        try
        {
            refundamount = new BigDecimal(refundAmount);
            if (!Functions.checkAccuracy(String.valueOf(refundAmount), 2))
            {   
                resp.setStatus(PZResponseStatus.FAILED);
                resp.setResponseDesceiption("Refund Amount should be 2 decimal places accurate");
                return resp;
            }
        }
        catch (NumberFormatException e)
        {
            log.debug("Invalid Refund amount" + refundAmount + "-" + e.getMessage());
            resp.setStatus(PZResponseStatus.FAILED);
            resp.setResponseDesceiption("Invalid Refund Amount");
            return resp;
        }
        
        
        try
        {
            conn=Database.getConnection();
            PreparedStatement ps=conn.prepareStatement(query);
            ResultSet rs=ps.executeQuery();
            if(rs.next())
            {
                payref = rs.getString("paymentid");
                accountid=rs.getString("accountid");
                amount=rs.getString("captureamount");
                transid = rs.getString("transid");

                GatewayAccount account = GatewayAccountService.getGatewayAccount(accountid);

                if (refundAmount > Double.parseDouble(amount))
                {
                    log.debug("Refund Amount " + refundAmount + " is greater than capture amount transaction id is"+trackingid);
                    resp.setTrackingId(trackingid);
                    resp.setStatus(PZResponseStatus.FAILED);
                    resp.setResponseDesceiption("Refund Amount " + refundAmount + " is greater than capture amount transaction id is"+trackingid);
                    return resp;
                }
                String remark;
                String description;
                if(isFraud)
                {
                    if(isAdmin)
                    {
                        remark="Refund BY ADMIN (FRAUD Transaction)"; //set in VO object
                        description = "Refund of " + transid +"  (Fraudulent Transaction)";
                    }
                    else
                    {
                        remark="Refund BY MERCHANT (FRAUD Transaction)"; //set in VO object
                        description = "Refund of " + transid +"  (Fraudulent Transaction)";
                    }

                }
                else
                {
                    if(isAdmin)
                    {
                        remark="Refund BY ADMIN "; //set in VO object
                        description = "Refund of " + transid +"  ";
                    }
                    else
                    {
                        remark="Refund BY MERCHANT "; //set in VO object
                        description = "Refund of " + transid +"  ";
                    }

                }

                transactionEntry = new TransactionEntry();
                transactionEntry.newGenericRefundTransaction(trackingid,refundamount,accountid,description,payDollarResponseVO);
                log.debug("Added charges and changed the status to markedforreversal");

                PayDollarRequestVO pdreq=new PayDollarRequestVO();
                CommTransactionDetailsVO cmtrans=new CommTransactionDetailsVO();

                cmtrans.setAmount(refundamount+"");
                pdreq.setTransDetailsVO(cmtrans);
                pdreq.setPayRef(payref);



                AbstractPaymentGateway paymentGateway = AbstractPaymentGateway.getGateway(accountid);

                payDollarResponseVO = (PayDollarResponseVO)paymentGateway.processRefund(trackingid, pdreq);



                if(payDollarResponseVO!=null && payDollarResponseVO.getTransactionStatus().equalsIgnoreCase("Authorized"))
                {

                    Codec MY = new MySQLCodec(MySQLCodec.Mode.STANDARD);
                    StringBuffer sb = new StringBuffer();
                    sb.append("update transaction_common set status='reversed'");
                    sb.append(",refundamount='"+ ESAPI.encoder().encodeForSQL(MY,refundamount.toString())+"'");
                    sb.append(",paymentid='"+ESAPI.encoder().encodeForSQL(MY,payDollarResponseVO.getTransactionId())+"'");
                    sb.append(",refundinfo='"+ESAPI.encoder().encodeForSQL(MY,refundReason)+"'");
                    sb.append(" where trackingid=" +ESAPI.encoder().encodeForSQL(MY,trackingid)+ " and status = 'markedforreversal'");

                    int rows = Database.executeUpdate(sb.toString(), conn);
                    log.debug("No of Rows updated : " + rows + "<br>");

                   actionEntry(trackingid,refundamount.toString(),ActionEntry.ACTION_REVERSAL_SUCCESSFUL,ActionEntry.STATUS_REVERSAL_SUCCESSFUL,payDollarResponseVO,null);


                    resp.setStatus(PZResponseStatus.SUCCESS);
                    resp.setResponseDesceiption("Transaction has been Reversed for TrackingID: "+trackingid);
                    resp.setTrackingId(trackingid);

                }

            }
            else
            {
                log.debug("Cannot Find Transaction to reverse transaction id is"+trackingid);
                resp.setTrackingId(trackingid);
                resp.setStatus(PZResponseStatus.FAILED);
                resp.setResponseDesceiption("Cannot find Valid Transaction To Reverse with Trackingid = "+trackingid);
                return resp;
            }
            
            
        }
        catch(SystemError se)
        {
            log.error("ERROR OCCURED",se);
            resp.setTrackingId(trackingid);
            resp.setStatus(PZResponseStatus.FAILED);
            resp.setResponseDesceiption("Exception Occured while trying to Reverse the transaction with Trackingid = "+trackingid);
            return resp;
        }
        catch(SQLException se)
        {
            log.error("ERROR OCCURED",se);
            resp.setTrackingId(trackingid);
            resp.setStatus(PZResponseStatus.FAILED);
            resp.setResponseDesceiption("Exception Occured while trying to Reverse the transaction with Trackingid = "+trackingid);
            return resp;
        }
        finally {
            
                
            Database.closeConnection(conn);
        }
        
            

        
        
        
        return resp;
    }
*/
    /*public PZCancelResponse cancel(PZCancelRequest cancelRequest)
    {
        Connection conn = null;

        PZCancelResponse cancelResponse = new PZCancelResponse();
        try
        {   conn=Database.getConnection();
            Integer trackingId = cancelRequest.getTrackingId();
            Integer accntId = cancelRequest.getAccountId();
            cancelResponse.setTrackingId(trackingId+"");

            PayDollarPaymentGateway pg =(PayDollarPaymentGateway) AbstractPaymentGateway.getGateway(String.valueOf(accntId));

            GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(accntId));


            PayDollarRequestVO paydrreq=new PayDollarRequestVO();
            PayDollarResponseVO paydrresp=null;
            
            
            String transaction_details = "select paymentid,amount from transaction_common where trackingid=? and status='authsuccessful'";
            PreparedStatement transDetailsprepstmnt = conn.prepareStatement(transaction_details);
            transDetailsprepstmnt.setInt(1, trackingId);
            ResultSet rsTransDetails = transDetailsprepstmnt.executeQuery();
            if (rsTransDetails.next())
            {
                String cancelStartedQuery = "update transaction_common set status = 'cancelstarted' where  status='authsuccessful' and trackingid = ?";
                PreparedStatement pstmt = conn.prepareStatement(cancelStartedQuery);
                pstmt.setInt(1, trackingId);
                int result = pstmt.executeUpdate();
                if (result == 1)
                {

                    String amount = rsTransDetails.getString("amount");
                    String payref=rsTransDetails.getString("paymentid");
                    paydrreq.setPayRef(payref);

                    int actionEntry = actionEntry(String.valueOf(trackingId), String.valueOf(amount), ActionEntry.ACTION_CANCEL_STARTED, ActionEntry.STATUS_CANCEL_STARTED, null, null);

                    try
                    {
                        log.debug("callng processCapture");
                        paydrresp = (PayDollarResponseVO) pg.processVoid(String.valueOf(trackingId), paydrreq);
                        log.debug("called processvoid" + paydrresp.getErrorCode() + paydrresp.getDescription() + paydrresp.getTransactionId());
                    }
                    catch (SystemError e)
                    {
                        log.error("system error", e);
                        cancelResponse.setStatus(PZResponseStatus.ERROR);
                        cancelResponse.setResponseDesceiption("Error during cancellation of transaction : " + trackingId + " ; " + e.getMessage());

                    }
                    Database db = new Database();
                    if (paydrresp != null && (paydrresp.getStatus()).equalsIgnoreCase("Voided"))
                    {
                        Codec MY = new MySQLCodec(MySQLCodec.Mode.STANDARD);

                        StringBuffer sb = new StringBuffer();
                        sb.append("update transaction_common set status='authcancelled'");
                        sb.append(" where trackingid=" + ESAPI.encoder().encodeForSQL(MY, trackingId+"") + " and status='cancelstarted'");
                        int rows = db.executeUpdate(sb.toString(), conn);
                        log.debug("No of Rows updated :" + rows + "<br>");

                        if (rows == 1)
                        {

                            // Start : Added for Action and Status Entry in Action History table

                            actionEntry(trackingId+"", String.valueOf(amount), ActionEntry.ACTION_CANCEL_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_CANCLLED, paydrresp,paydrreq);

                            // End : Added for Action and Status Entry in Action History table

                            cancelResponse.setStatus(PZResponseStatus.SUCCESS);
                            cancelResponse.setResponseDesceiption("Transaction has been successfully cancelled");
                        }
                    }
                    else
                    {
                        cancelResponse.setStatus(PZResponseStatus.FAILED);
                        cancelResponse.setResponseDesceiption("Transaction cannot be Captured.");
                    }


                }
                else
                {
                    cancelResponse.setStatus(PZResponseStatus.ERROR);
                    cancelResponse.setResponseDesceiption("Error initiating cancellation for transaction");
                    return cancelResponse;
                }


            }
            else{
                cancelResponse.setStatus(PZResponseStatus.ERROR);
                cancelResponse.setResponseDesceiption("Could Not Find Transaction To Cancel with Tracking id "+trackingId);
                return cancelResponse;
            }
            
            
        }
        catch(SQLException sqle)
        {
            log.error("Exception Occured",sqle);
        }
        catch (SystemError se)
        {
            log.error("Error Occured",se);
            
        }
        finally {
            Database.closeConnection(conn);
        }
        return cancelResponse;
    }

*/
    public void setRefundVOParamsextension(CommRequestVO requestVO, PZRefundRequest refundRequest) throws PZDBViolationException
    {
        setParams(requestVO, refundRequest);
        
    }

    public void setCaptureVOParamsExtension(CommRequestVO requestVO, PZCaptureRequest captureRequest) throws PZDBViolationException
    {
        setParams(requestVO, captureRequest);

    }
    public void setInquiryVOParamsExtension(CommRequestVO requestVO, PZInquiryRequest captureRequest) throws PZDBViolationException
    {
        setParams(requestVO, captureRequest);

    }
    


    public void setCancelVOParamsExtension(CommRequestVO requestVO, PZCancelRequest cancelRequest) throws PZDBViolationException
    {
        setParams(requestVO, cancelRequest);
    }

    public void setParams(CommRequestVO requestVO,PZRequest request) throws PZDBViolationException
    {


        PayDollarRequestVO payDollarRequestVO = (PayDollarRequestVO) requestVO;
        int trackingid=request.getTrackingId();

        String payref="";
        String query="select paymentid from transaction_common where trackingid="+trackingid+"";

        Connection con=null;
        try{

            con=Database.getConnection();
            ResultSet rs=Database.executeQuery(query,con);
            if(rs.next())
            {
                payref = rs.getString("paymentid");
            }
            else {
                throw new SystemError("INVALID TRACKING ID") ;
            }

        }

        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(PayDollarPaymentProcess.class.getName(),"setParams()",null,"common","DB Exception",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(PayDollarPaymentProcess.class.getName(), "setParams()", null, "common", "DB Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }

        payDollarRequestVO.setPayRef(payref);
        
        
    }

    

    public int actionEntryExtension(int newDetailId, String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO requestVO) throws PZDBViolationException
    {

        log.debug("Entering ActionEntry for PayDollar Details");
        transactionLogger.debug("Entering ActionEntry for PayDollar Details");

        String prc="",src="",ord="0",holder="",successcode="0",payref="0",currcode="0",authid="";
        int results=0;
        if (responseVO != null)
        {
            PayDollarResponseVO payDollarResponseVO = (PayDollarResponseVO) responseVO;
            prc=payDollarResponseVO.getPrimaryResponseCode();
            src=payDollarResponseVO.getSecondResponseCode();
            if(payDollarResponseVO.getBankOrderId()!=null&&!payDollarResponseVO.getBankOrderId().equals(""))
            ord = payDollarResponseVO.getBankOrderId();
            
            holder = payDollarResponseVO.getHolder();
            successcode = payDollarResponseVO.getSuccessCode();
            payref=payDollarResponseVO.getPayRef();
            currcode=payDollarResponseVO.getCurrencyCode();
            authid=payDollarResponseVO.getAuthId();

            
            
        }




        Connection cn = null;
        try
        {
            
            cn = Database.getConnection();
            String sql = "insert into transaction_paydollar_details(detailid,prc,src,ord,holder,successcode,payref,currcode,authid) values (?,?,?,?,?,?,?,?,?)";

            PreparedStatement pstmt = cn.prepareStatement(sql);
            pstmt.setInt(1, newDetailId);
            pstmt.setString(2, prc);
            pstmt.setString(3,src);
            pstmt.setString(4,ord);
            pstmt.setString(5,holder);
            pstmt.setString(6,successcode);
            pstmt.setString(7,payref);
            pstmt.setString(8,currcode);
            pstmt.setString(9,authid);




            results = pstmt.executeUpdate();


        }
        catch (SQLException se)
        {
            PZExceptionHandler.raiseDBViolationException(PayDollarPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(PayDollarPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(cn);
        }


        return results;
    }
   

    public PZInquiryResponse inquiry(PZInquiryRequest pzInquiryRequest)
    {


        Connection conn = null;

        log.debug("INSIDE PAYDOLLAR PROCESS, INQUIRY");
        transactionLogger.debug("INSIDE PAYDOLLAR PROCESS, INQUIRY");
        PZInquiryResponse cancelResponse = new PZInquiryResponse();
        try
        {
            Integer trackingId = pzInquiryRequest.getTrackingId();
            Integer accntId = pzInquiryRequest.getAccountId();
            
            String ip = pzInquiryRequest.getIpAddress();
            

            AbstractPaymentGateway pg = AbstractPaymentGateway.getGateway(String.valueOf(accntId));
            String currency = pg.getCurrency();
            GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(accntId));
            String merchantId = account.getMerchantId();
            String alias = account.getAliasName();
            String username = account.getFRAUD_FTP_USERNAME();
            String password = account.getFRAUD_FTP_PASSWORD();
            String displayname = account.getDisplayName();


            conn = Database.getConnection();



            
            log.debug("QUERYING DATABASE");
            transactionLogger.debug("QUERYING DATABASE");

            String transaction_details = "select * from transaction_common where trackingid=?";
            PreparedStatement transDetailsprepstmnt = conn.prepareStatement(transaction_details);
            transDetailsprepstmnt.setInt(1, trackingId);
            ResultSet rsTransDetails = transDetailsprepstmnt.executeQuery();
            if (rsTransDetails.next())
            {

                log.debug("GOT FROM DATABASE");
                transactionLogger.debug("GOT FROM DATABASE");
                String amount = rsTransDetails.getString("amount");
                String paymentid=rsTransDetails.getString("paymentid");

                log.debug("SETTING VO");
                transactionLogger.debug("SETTING VO");

                CommMerchantVO merchantAccountVO = new CommMerchantVO();
                merchantAccountVO.setMerchantId(merchantId);
                merchantAccountVO.setPassword(password);
                merchantAccountVO.setMerchantUsername(username);
                merchantAccountVO.setDisplayName(displayname);
                merchantAccountVO.setAliasName(alias);

                CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
                transDetailsVO.setAmount(String.valueOf(amount));
                transDetailsVO.setCurrency(currency);
                transDetailsVO.setOrderId(String.valueOf(trackingId));
                transDetailsVO.setPreviousTransactionId(String.valueOf(paymentid));

                CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
                addressDetailsVO.setIp(ip);

                CommRequestVO commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(accntId);

                commRequestVO.setCommMerchantVO(merchantAccountVO);
                commRequestVO.setTransDetailsVO(transDetailsVO);
                commRequestVO.setAddressDetailsVO(addressDetailsVO);





                    setInquiryVOParamsExtension(commRequestVO, pzInquiryRequest);




                log.debug("CALLING GATEWAY");
                transactionLogger.debug("CALLING GATEWAY");
                PayDollarResponseVO paydrresp=(PayDollarResponseVO )pg.processQuery(trackingId+"", commRequestVO );
                
                log.debug("GATEWAY IS CALLED");
                transactionLogger.debug("GATEWAY IS CALLED");
                if(paydrresp !=null)
                {

                    log.debug("PAYDOLAR RESPONSE IS NOT NULL");
                    transactionLogger.debug("PAYDOLAR RESPONSE IS NOT NULL");
                    cancelResponse.setTrackingId(trackingId+"");
                    cancelResponse.setResponseTransactionId(paydrresp.getPayRef());
                    cancelResponse.setResponseTransactionStatus(paydrresp.getGatewayStatus());
                    cancelResponse.setResponseCode(paydrresp.getPrimaryResponseCode()+":"+paydrresp.getSecondResponseCode());
                    cancelResponse.setResponseDescription(paydrresp.getErrMsg());
                    cancelResponse.setResponseDescriptor(paydrresp.getBankOrderId() );

                    
                    
                    
                }
                else
                {
                    cancelResponse.setStatus(PZResponseStatus.ERROR);
                    cancelResponse.setResponseDesceiption("Error Connecting to Gateway");
                }
                
                
                    

            }
            else
            {
                cancelResponse.setStatus(PZResponseStatus.ERROR);
                cancelResponse.setResponseDesceiption("Error Finding transaction");
            }
        

            
        }
        catch (PZConstraintViolationException e)
        {
            log.error("PZConstraintViolationException while inquiring via PayDollar",e);
            transactionLogger.error("PZConstraintViolationException while inquiring via PayDollar",e);
            PZExceptionHandler.handleCVEException(e,null,"Inquiring");

            cancelResponse.setStatus(PZResponseStatus.ERROR);
            cancelResponse.setResponseDesceiption("Error Finding transaction");
        }
        catch (PZDBViolationException e)
        {
            log.error("PZDBViolationException while inquiring via PayDollar",e);
            transactionLogger.error("PZDBViolationException while inquiring via PayDollar",e);
            PZExceptionHandler.handleDBCVEException(e,null,"Inquiring");

            cancelResponse.setStatus(PZResponseStatus.ERROR);
            cancelResponse.setResponseDesceiption("Error Finding transaction");
        }
        catch (PZGenericConstraintViolationException e)
        {
            log.error("PZGenericConstraintViolationException while inquiring via PayDollar",e);
            transactionLogger.error("PZGenericConstraintViolationException while inquiring via PayDollar",e);
            PZExceptionHandler.handleGenericCVEException(e,null,"Inquiring");

            cancelResponse.setStatus(PZResponseStatus.ERROR);
            cancelResponse.setResponseDesceiption("Error Finding transaction");
        }
        catch (SQLException e)
        {
            log.error("SQL Exception while inquiring via PayDollar",e);
            transactionLogger.error("SQL Exception while inquiring via PayDollar",e);
            PZExceptionHandler.raiseAndHandleDBViolationException(PayDollarPaymentProcess.class.getName(), "inquiry()", null, "common", "SQL Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause(), null, "Inquiring");

            cancelResponse.setStatus(PZResponseStatus.ERROR);
            cancelResponse.setResponseDesceiption("Error Finding transaction");
        }
        catch (SystemError systemError)
        {
            log.error("SQL Exception while inquiring via PayDollar",systemError);
            transactionLogger.error("SQL Exception while inquiring via PayDollar",systemError);
            PZExceptionHandler.raiseAndHandleDBViolationException(PayDollarPaymentProcess.class.getName(),"inquiry()",null,"common","SQL Exception",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause(),null,"Inquiring");

            cancelResponse.setStatus(PZResponseStatus.ERROR);
            cancelResponse.setResponseDesceiption("Error Finding transaction");
        }

        finally
        {
            Database.closeConnection(conn);
        }






     return cancelResponse;
    }


}
