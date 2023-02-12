package com.payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.paymentgateway.UGSPaymentGateway;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.directi.pg.core.valueObjects.UGSPayRequestVO;
import com.directi.pg.core.valueObjects.UGSPayResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.request.PZChargebackRequest;
import com.payment.request.PZInquiryRequest;
import com.payment.request.PZRefundRequest;
import com.payment.response.PZChargebackResponse;
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
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Mar 16, 2013
 * Time: 3:33:22 PM
 * To change this template use File | Settings | File Templates.
 */

public class UGSPayPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(UGSPayPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(UGSPayPaymentProcess.class.getName());
    Connection conn = null;
    BigDecimal bdConst = new BigDecimal("0.01");
    TransactionEntry transactionEntry = null;


    public PZRefundResponse refund(PZRefundRequest refundRequest)
    {
        PZRefundResponse refundResponse = new PZRefundResponse();
           try
           {
           Integer trackingId = refundRequest.getTrackingId();
           Integer accountId = refundRequest.getAccountId();
           String refundAmount = refundRequest.getRefundAmount();
           String refundReason = refundRequest.getRefundReason();
           boolean isFraud =  refundRequest.isFraud();
           boolean isAdmin =  refundRequest.isAdmin();
           conn = Database.getConnection();
           AuditTrailVO auditTrailVO=new AuditTrailVO();
           UGSPayRequestVO UGSPayRequestVO= new UGSPayRequestVO();
           GenericTransDetailsVO genericTransDetailsVO = new  GenericTransDetailsVO();
           UGSPayResponseVO UGSPayResponseVO = new UGSPayResponseVO();
           auditTrailVO.setActionExecutorId(UGSPayResponseVO.getMerchantId());
           auditTrailVO.setActionExecutorName("Customer");
           BigDecimal refundamount = null;
           try
           {
               refundamount = new BigDecimal(refundAmount);
               if (!Functions.checkAccuracy(String.valueOf(refundAmount), 2))
               {
                   refundResponse.setStatus(PZResponseStatus.FAILED);
                   refundResponse.setResponseDesceiption("Refund Amount should be 2 decimal places accurate");
                   return refundResponse;
               }
           }
           catch (NumberFormatException e)
           {
               log.debug("Invalid Refund amount" + refundAmount + "-" + e.getMessage());
               transactionLogger.debug("Invalid Refund amount" + refundAmount + "-" + e.getMessage());
               refundResponse.setStatus(PZResponseStatus.FAILED);
               refundResponse.setResponseDesceiption("Invalid Refund Amount");
               return refundResponse;
           }

           String transaction = "select trackingid,toid,fromid,description,captureamount,transid,transaction_common.accountid,status,timestamp,name,paymentid,members.contact_emails,members.company_name from transaction_common,members where status IN ('settled','capturesuccess') and transaction_common.toid=members.memberid and trackingid=?";
           PreparedStatement transPreparedStatement = conn.prepareStatement(transaction);
           transPreparedStatement.setString(1, String.valueOf(trackingId));
           ResultSet rstransaction = transPreparedStatement.executeQuery();
           if (rstransaction.next())
           {


               String trackingIdDB = rstransaction.getString("trackingid");
               String transid = rstransaction.getString("transid");
               String accountIdDB = rstransaction.getString("accountid");
               String rsdescription = rstransaction.getString("description");
               String captureamount = rstransaction.getString("captureamount");
               String transstatus = rstransaction.getString("status");
               BigDecimal amt = new BigDecimal(captureamount);
               String paymentid = rstransaction.getString("paymentid");

               GatewayAccount account = GatewayAccountService.getGatewayAccount(accountIdDB);


            if (Double.parseDouble(refundAmount) > Double.parseDouble(captureamount))
            {
                log.debug("Refund Amount " + refundamount + " is greater than capture amount transaction id is"+trackingId);
                transactionLogger.debug("Refund Amount " + refundamount + " is greater than capture amount transaction id is"+trackingId);
                refundResponse.setStatus(PZResponseStatus.FAILED);
                refundResponse.setResponseDesceiption("Refund Amount " + refundamount + " is greater than capture amount transaction id is"+trackingId);
                return refundResponse;
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

             UGSPayResponseVO.setResponseRemark(remark);


            //add charges and change status to markedforreverse
            transactionEntry = new TransactionEntry();
            transactionEntry.newGenericRefundTransaction(trackingIdDB,refundamount,accountIdDB,description,UGSPayResponseVO,auditTrailVO);
            log.debug("Added charges and changed the status to markedforreversal");
            transactionLogger.debug("Added charges and changed the status to markedforreversal");

            //Preparing to call gateway for refund
            UGSPayRequestVO.setUGSTransId(Integer.parseInt(paymentid));
            UGSPayRequestVO.setAmount(refundamount.toString());

            AbstractPaymentGateway paymentGateway = AbstractPaymentGateway.getGateway(accountIdDB);

            UGSPayResponseVO = (UGSPayResponseVO)paymentGateway.processRefund(trackingIdDB, UGSPayRequestVO);
            log.debug("Got Response from gateway");
            transactionLogger.debug("Got Response from gateway");

           if(UGSPayResponseVO!=null && UGSPayResponseVO.getTransactionStatus().equalsIgnoreCase("Authorized"))
           {

               Codec MY = new MySQLCodec(MySQLCodec.Mode.STANDARD);
               StringBuffer sb = new StringBuffer();
               sb.append("update transaction_common set status='reversed'");
               sb.append(",refundamount='"+ ESAPI.encoder().encodeForSQL(MY,refundamount.toString())+"'");
               sb.append(",paymentid='"+ESAPI.encoder().encodeForSQL(MY,UGSPayResponseVO.getTransactionId())+"'");
               sb.append(",refundinfo='"+ESAPI.encoder().encodeForSQL(MY,refundReason)+"'");
               sb.append(" where trackingid=" +ESAPI.encoder().encodeForSQL(MY,trackingIdDB)+ " and status = 'markedforreversal'");

               int rows = Database.executeUpdate(sb.toString(), conn);
               log.debug("No of Rows updated : " + rows + "<br>");
               transactionLogger.debug("No of Rows updated : " + rows + "<br>");

               auditTrailVO.setActionExecutorId(UGSPayResponseVO.getMerchantId());
               auditTrailVO.setActionExecutorName("Customer");

               ActionEntry entry = new ActionEntry();
               int actionEntry = entry.genericActionEntry(trackingIdDB,refundamount.toString(),ActionEntry.ACTION_REVERSAL_SUCCESSFUL,ActionEntry.STATUS_REVERSAL_SUCCESSFUL,null,account.getGateway(),UGSPayResponseVO,auditTrailVO);
               entry.closeConnection();

               //Send Mail

               refundResponse.setStatus(PZResponseStatus.SUCCESS);
               refundResponse.setResponseDesceiption("Transaction has been Reversed for TrackingID: "+trackingIdDB);
               refundResponse.setTrackingId(trackingIdDB);


           }
           else
           {
              log.debug("Error while Process refund Transaction. TrackingId:"+trackingId);
              transactionLogger.debug("Error while Process refund Transaction. TrackingId:"+trackingId);
              refundResponse.setStatus(PZResponseStatus.FAILED);
              refundResponse.setResponseDesceiption("Error while Process refund Transaction. TrackingId:"+trackingId);
              return refundResponse;
           }



       }
       else
        {
            refundResponse.setStatus(PZResponseStatus.FAILED);
            refundResponse.setResponseDesceiption("Transaction not found");
        }


        }
           catch (PZTechnicalViolationException e)
           {
               log.error("PZTechnicalException while refunding via UgsPay", e);
               transactionLogger.error("PZTechnicalException while refunding via UgsPay", e);

               PZExceptionHandler.handleTechicalCVEException(e, null, "refund");
               refundResponse.setStatus(PZResponseStatus.ERROR);
               refundResponse.setResponseDesceiption("Error fetching transaction status" + e.getMessage());
           }
           catch (PZConstraintViolationException e)
           {
               log.error("PZConstraintViolationException while refunding via UgsPay",e);
               transactionLogger.error("PZConstraintViolationException while refunding via UgsPay",e);

               PZExceptionHandler.handleCVEException(e,null,"refund");

               refundResponse.setStatus(PZResponseStatus.ERROR);
               refundResponse.setResponseDesceiption("Error fetching transaction status" + e.getMessage());
           }
           catch (PZDBViolationException e)
           {
               log.error("PZDBViolationException while refunding via UgsPay",e);
               transactionLogger.error("PZDBViolationException while refunding via UgsPay",e);

               PZExceptionHandler.handleDBCVEException(e, null, "refund");

               refundResponse.setStatus(PZResponseStatus.ERROR);
               refundResponse.setResponseDesceiption("Error fetching transaction status" + e.getMessage());
           }
           catch (SQLException e)
           {
               log.error("SQLException while refunding via UgsPay",e);
               transactionLogger.error("SQLException while refunding via UgsPay",e);

               PZExceptionHandler.raiseAndHandleDBViolationException(UGSPayPaymentProcess.class.getName(),"Inquiry()",null,"COmmon","DB Exception", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause(),null,"refund");

               refundResponse.setStatus(PZResponseStatus.ERROR);
               refundResponse.setResponseDesceiption("Error fetching transaction status" + e.getMessage());
           }
           catch (SystemError systemError)
           {
               log.error("SystemError while refunding via UgsPay",systemError);
               transactionLogger.error("SystemError while refunding via UgsPay",systemError);

               PZExceptionHandler.raiseAndHandleDBViolationException(UGSPayPaymentProcess.class.getName(),"Inquiry()",null,"COmmon","DB Exception",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause(),null,"refund");

               refundResponse.setStatus(PZResponseStatus.ERROR);
               refundResponse.setResponseDesceiption("Error fetching transaction status" + systemError.getMessage());
           }

           finally
        {
            Database.closeConnection(conn);
        }

        return refundResponse;
    }




    public PZChargebackResponse chargeback(PZChargebackRequest pzChargebackRequest)
    {
        PZChargebackResponse chargebackResponse = new PZChargebackResponse();
        Transaction trans = new Transaction();

               try
               {
               Integer trackingId = pzChargebackRequest.getTrackingId();
               Integer accountId = pzChargebackRequest.getAccountId();
               String cbamount =pzChargebackRequest.getCbAmount();
               String cbreason =pzChargebackRequest.getCbReason();
               boolean isAdmin =pzChargebackRequest.isAdmin();
               conn = Database.getConnection();
               transactionEntry = new TransactionEntry();
               UGSPayRequestVO UGSPayRequestVO= new UGSPayRequestVO();
               GenericTransDetailsVO genericTransDetailsVO = new  GenericTransDetailsVO();
               UGSPayResponseVO UGSPayResponseVO = new UGSPayResponseVO();


               if(cbreason.equals("") || cbreason==null)
                {
                    cbreason="N/A";
                }


               try
               {

                   if (cbamount!=null && !Functions.checkAccuracy(String.valueOf(cbamount), 2))
                   {
                       chargebackResponse.setStatus(PZResponseStatus.FAILED);
                       chargebackResponse.setResponseDesceiption("ChargeBack Amount should be 2 decimal places accurate");
                       return chargebackResponse;
                   }
               }
               catch (NumberFormatException e)
               {
                   log.debug("Invalid ChargeBack amount" + cbamount + "-" + e.getMessage());
                   transactionLogger.debug("Invalid ChargeBack amount" + cbamount + "-" + e.getMessage());
                   chargebackResponse.setStatus(PZResponseStatus.FAILED);
                   chargebackResponse.setResponseDesceiption("Invalid ChargeBack Amount");
                   return chargebackResponse;
               }

              String transaction = "select trackingid,toid,fromid,description,amount,captureamount-refundamount as cbamount,transid,transaction_common.accountid,status,timestamp,name,paymentid,members.contact_emails,members.company_name from transaction_common,members where transaction_common.toid=members.memberid and trackingid=?";
              PreparedStatement transPreparedStatement = conn.prepareStatement(transaction);
              transPreparedStatement.setString(1, String.valueOf(trackingId));
              ResultSet rstransaction = transPreparedStatement.executeQuery();
              if (rstransaction.next())
              {


                  String trackingIdDB = rstransaction.getString("trackingid");
                  String transid = rstransaction.getString("transid");
                  String accountIdDB = rstransaction.getString("accountid");
                  String rsdescription = rstransaction.getString("description");
                  String amount = rstransaction.getString("amount");
                  String chargebackamount = rstransaction.getString("cbamount");
                  String transstatus = rstransaction.getString("status");
                  String paymentid = rstransaction.getString("paymentid");

                  GatewayAccount account = GatewayAccountService.getGatewayAccount(accountIdDB);


               if (Double.parseDouble(cbamount) > Double.parseDouble(chargebackamount))
               {
                   log.debug("Chargeback  Amount " + cbamount + " is greater than capture amount - refund amount transaction id is"+trackingId);
                   transactionLogger.debug("Chargeback  Amount " + cbamount + " is greater than capture amount - refund amount transaction id is"+trackingId);
                   chargebackResponse.setStatus(PZResponseStatus.FAILED);
                   chargebackResponse.setResponseDesceiption("Chargeback Amount " + cbamount + " is greater than capture amount -refund amounttransaction id is"+trackingId);
                   return chargebackResponse;
               }

              String remark="";

               if(isAdmin)
               {
               remark="ChargeBack BY ADMIN "; //set in VO object

               }


               //Preparing to call gateway for inquery

               AbstractPaymentGateway paymentGateway = AbstractPaymentGateway.getGateway(accountIdDB);

               UGSPayResponseVO = (UGSPayResponseVO)paymentGateway.processQuery(trackingIdDB, UGSPayRequestVO);
               log.debug("Got Response from gateway");
               transactionLogger.debug("Got Response from gateway");

               AuditTrailVO auditTrailVO=pzChargebackRequest.getAuditTrailVO();

               if(UGSPayResponseVO!=null && UGSPayResponseVO.getResult().equalsIgnoreCase("exist"))
              {

              if(transstatus.equals("capturesuccess"))
              {
                int paymentTransId=0;
                paymentTransId = transactionEntry.newGenericCreditTransaction(trackingIdDB,new BigDecimal(amount),accountIdDB,null,auditTrailVO);
              }
              if(transstatus.equals("markedforreversal"))
              {

                String query="update transaction_common set status='reversed' where status='markedforreversal' and trackingid=?";
                PreparedStatement p1=conn.prepareStatement(query);
                p1.setString(1,trackingIdDB);
                int rows = p1.executeUpdate();

              }

              UGSPayResponseVO.setResponseRemark(remark);
              trans.genericProcessChargeback(trackingIdDB,null ,null,String.valueOf(cbamount),cbreason,null,UGSPayResponseVO,account,auditTrailVO);
              log.debug("Added charges and changed the status to chargeback");
              transactionLogger.debug("Added charges and changed the status to chargeback");
              chargebackResponse.setStatus(PZResponseStatus.SUCCESS);
              chargebackResponse.setResponseDesceiption(rsdescription);
              chargebackResponse.setTrackingId(trackingIdDB);


              }
              else
              {
                 log.debug("Transaction found but it is not in chargeback status at Bank end.. TrackingId:"+trackingId);
                 transactionLogger.debug("Transaction found but it is not in chargeback status at Bank end.. TrackingId:"+trackingId);
                 chargebackResponse.setStatus(PZResponseStatus.FAILED);
                 chargebackResponse.setResponseDesceiption("Transaction found but it is not in chargeback status at Bank end. TrackingId:"+trackingId);
                 return chargebackResponse;
              }


           }
            else
             {
                 chargebackResponse.setStatus(PZResponseStatus.FAILED);
                 chargebackResponse.setResponseDesceiption("Transaction not found");
             }



              }
               catch (PZTechnicalViolationException e)
               {
                   log.error("PZTechnicalException while chargeback via UgsPay", e);
                   transactionLogger.error("PZTechnicalException while chargeback via UgsPay", e);

                   PZExceptionHandler.handleTechicalCVEException(e, null, "chargeback");
                   chargebackResponse.setStatus(PZResponseStatus.ERROR);
                   chargebackResponse.setResponseDesceiption("Error fetching transaction status" + e.getMessage());
               }
               catch (PZConstraintViolationException e)
               {
                   log.error("PZConstraintViolationException while chargeback via UgsPay",e);
                   transactionLogger.error("PZConstraintViolationException while chargeback via UgsPay",e);

                   PZExceptionHandler.handleCVEException(e,null,"chargeback");

                   chargebackResponse.setStatus(PZResponseStatus.ERROR);
                   chargebackResponse.setResponseDesceiption("Error fetching transaction status" + e.getMessage());
               }
               catch (PZDBViolationException e)
               {
                   log.error("PZDBViolationException while chargeback via UgsPay",e);
                   transactionLogger.error("PZDBViolationException while chargeback via UgsPay",e);

                   PZExceptionHandler.handleDBCVEException(e,null,"chargeback");

                   chargebackResponse.setStatus(PZResponseStatus.ERROR);
                   chargebackResponse.setResponseDesceiption("Error fetching transaction status" + e.getMessage());
               }
               catch (PZGenericConstraintViolationException e)
               {
                   log.error("PZGenericConstraintViolationException while chargeback via UgsPay",e);
                   transactionLogger.error("PZGenericConstraintViolationException while chargeback via UgsPay",e);

                   PZExceptionHandler.handleGenericCVEException(e,null,"chargeback");

                   chargebackResponse.setStatus(PZResponseStatus.ERROR);
                   chargebackResponse.setResponseDesceiption("Error fetching transaction status" + e.getMessage());
               }
               catch (SQLException e)
               {
                   log.error("SQLException while chargeback via UgsPay",e);
                   transactionLogger.error("SQLException while chargeback via UgsPay",e);

                   PZExceptionHandler.raiseAndHandleDBViolationException(UGSPayPaymentProcess.class.getName(),"Inquiry()",null,"COmmon","DB Exception", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause(),null,"chargeback");

                   chargebackResponse.setStatus(PZResponseStatus.ERROR);
                   chargebackResponse.setResponseDesceiption("Error fetching transaction status" + e.getMessage());
               }
               catch (SystemError systemError)
               {
                   log.error("SystemError while chargeback via UgsPay",systemError);
                   transactionLogger.error("SystemError while chargeback via UgsPay",systemError);

                   PZExceptionHandler.raiseAndHandleDBViolationException(UGSPayPaymentProcess.class.getName(),"Inquiry()",null,"COmmon","DB Exception",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause(),null,"chargeback");

                   chargebackResponse.setStatus(PZResponseStatus.ERROR);
                   chargebackResponse.setResponseDesceiption("Error fetching transaction status" + systemError.getMessage());
               }


               finally
            {
                Database.closeConnection(conn);
            }

            return chargebackResponse;  //To change body of implemented methods use File | Settings | File Templates.
             //To change body of implemented methods use File | Settings | File Templates.
    }



    public PZInquiryResponse inquiry(PZInquiryRequest pzInquiryRequest)
    {
         log.debug("Inside UGSPAy Inquiry");
         transactionLogger.debug("Inside UGSPAy Inquiry");
        PZInquiryResponse inquiryResponse = new PZInquiryResponse();
        try
        {
        Integer trackingId = pzInquiryRequest.getTrackingId();
        Integer accountId = pzInquiryRequest.getAccountId();
        conn = Database.getConnection();

        UGSPayRequestVO ugsPayRequestVO= new UGSPayRequestVO();
        UGSPayResponseVO ugsPayResponseVO = null;


       String transaction = "select trackingid,paymentid,accountid,amount,currency,status,refundamount from transaction_common where trackingid=?";
       PreparedStatement transPreparedStatement = conn.prepareStatement(transaction);
       transPreparedStatement.setString(1, String.valueOf(trackingId));
       ResultSet rstransaction = transPreparedStatement.executeQuery();
       if (rstransaction.next())
       {

           String trackingIdDB = rstransaction.getString("trackingid");
           String accountIdDB = rstransaction.getString("accountid");

            log.debug("Setting Details====");
            transactionLogger.debug("Setting Details====");
            UGSPaymentGateway pg= new UGSPaymentGateway(accountIdDB);
            ugsPayResponseVO=(UGSPayResponseVO ) pg.processQuery(trackingIdDB,ugsPayRequestVO);



            if(ugsPayResponseVO!=null)
            {
                inquiryResponse.setStatus(PZResponseStatus.SUCCESS);
                inquiryResponse.setTrackingId(trackingIdDB);
                inquiryResponse.setResponseTransactionId(ugsPayResponseVO.getTransactionId());
                inquiryResponse.setResponseTransactionStatus(ugsPayResponseVO.getResult());
                inquiryResponse.setResponseCode(ugsPayResponseVO.getTransactionErrorcode());
                inquiryResponse.setResponseDescription(ugsPayResponseVO.getTransactionErrormessage());
                inquiryResponse.setResponseTime(ugsPayResponseVO.getResponseTime());
                inquiryResponse.setResponseName1("ErrorCode");
                inquiryResponse.setResponseValue1(ugsPayResponseVO.getErrorCode());
                inquiryResponse.setResponseName2("ErrorMessage");
                inquiryResponse.setResponseValue2(ugsPayResponseVO.getDescription());
            }
           else
           {
               log.debug("Error while Inquiry Transaction. TrackingId:"+trackingId);
               transactionLogger.debug("Error while Inquiry Transaction. TrackingId:"+trackingId);
               inquiryResponse.setStatus(PZResponseStatus.FAILED);
               inquiryResponse.setResponseDesceiption("Error while Inquiry Transaction. TrackingId:"+trackingId);
               return inquiryResponse;
           }


       }

       else
        {
            inquiryResponse.setStatus(PZResponseStatus.FAILED);
            inquiryResponse.setResponseDesceiption("Transaction not found");
        }



        }
        catch (PZTechnicalViolationException e)
        {
            log.error("PZTechnicalException while inquiry via UgsPay", e);
            transactionLogger.error("PZTechnicalException while inquiry via UgsPay", e);

            PZExceptionHandler.handleTechicalCVEException(e, null, "inquiry");
            inquiryResponse.setStatus(PZResponseStatus.ERROR);
            inquiryResponse.setResponseDesceiption("Error inquiry transaction status" + e.getMessage());
        }
        catch (PZConstraintViolationException e)
        {
            log.error("PZConstraintViolationException while inquiry via UgsPay",e);
            transactionLogger.error("PZConstraintViolationException while inquiry via UgsPay",e);

            PZExceptionHandler.handleCVEException(e,null,"inquiry");

            inquiryResponse.setStatus(PZResponseStatus.ERROR);
            inquiryResponse.setResponseDesceiption("Error inquiry transaction status" + e.getMessage());
        }
        catch (PZGenericConstraintViolationException e)
        {
            log.error("PZGenericConstraintViolationException while inquiry via UgsPay",e);
            transactionLogger.error("PZGenericConstraintViolationException while inquiry via UgsPay",e);

            PZExceptionHandler.handleGenericCVEException(e,null,"inquiry");

            inquiryResponse.setStatus(PZResponseStatus.ERROR);
            inquiryResponse.setResponseDesceiption("Error inquiry transaction status" + e.getMessage());
        }
        catch (SQLException e)
        {
            log.error("SQLException while inquiry via UgsPay",e);
            transactionLogger.error("SQLException while inquiry via UgsPay",e);

            PZExceptionHandler.raiseAndHandleDBViolationException(UGSPayPaymentProcess.class.getName(),"Inquiry()",null,"COmmon","DB Exception", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause(),null,"inquiry");

            inquiryResponse.setStatus(PZResponseStatus.ERROR);
            inquiryResponse.setResponseDesceiption("Error inquiry transaction status" + e.getMessage());
        }
        catch (SystemError systemError)
        {
            log.error("SystemError while inquiry via UgsPay",systemError);
            transactionLogger.error("SystemError while inquiry via UgsPay",systemError);

            PZExceptionHandler.raiseAndHandleDBViolationException(UGSPayPaymentProcess.class.getName(),"Inquiry()",null,"COmmon","DB Exception",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause(),null,"inquiry");

            inquiryResponse.setStatus(PZResponseStatus.ERROR);
            inquiryResponse.setResponseDesceiption("Error inquiry transaction status" + systemError.getMessage());
        }

        finally
        {
            Database.closeConnection(conn);
        }

        return inquiryResponse;
    }

}