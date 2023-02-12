package com.payment.qwipi.core;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.manager.vo.MerchantDetailsVO;
import com.payment.AbstractPaymentProcess;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.request.*;
import com.payment.response.*;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 2/16/13
 * Time: 11:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class QwipiGatewayProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(QwipiGatewayProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(QwipiGatewayProcess.class.getName());

    @Override
    public String getAdminEmailAddress()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getSpecificCreditPageTemplate()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Hashtable specificValidationAPI(Hashtable<String, String> parameter)
    {
        InputValidator inputValidator = new InputValidator();
        Hashtable error = new Hashtable();
        Hashtable<InputFields, String> hashTable =new Hashtable<InputFields, String>();

        hashTable.put(InputFields.BIRTHDATE, parameter.get("birthdate"));

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(hashTable,errorList,false);


        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :hashTable.keySet())
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    transactionLogger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error.put(inputFields.toString(),errorList.getError(inputFields.toString()).getMessage());
                }
            }
        }
        return error;
    }

    //@Override
    public String specificValidationAPI(HttpServletRequest request)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        Hashtable<InputFields, String> hashTable =new Hashtable<InputFields, String>();

        hashTable.put(InputFields.BIRTHDATE, request.getParameter("birthdate"));

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(hashTable,errorList,false);


        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :hashTable.keySet())
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    transactionLogger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }

    public Hashtable specificValidation(Map<String, String> parameter)
    {
        InputValidator inputValidator = new InputValidator();
        Hashtable error = new Hashtable();
        Hashtable<InputFields, String> hashTable =new Hashtable<InputFields, String>();

        hashTable.put(InputFields.BIRTHDATE, parameter.get("birthdate"));

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(hashTable,errorList,false);


        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :hashTable.keySet())
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    transactionLogger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error.put(inputFields.toString(),errorList.getError(inputFields.toString()).getMessage());
                }
            }
        }
        return error;
    }

    @Override
    public int insertTransactionDetails(Hashtable parameters) throws SystemError
    {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getRedirectPage(Hashtable parameters) throws SystemError
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public Hashtable<String, String> transaction(Map<String, String> transactionRequestParameters, Hashtable<String, Object> transactionAttributes)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PZTransactionResponse transaction(PZTransactionRequest refundRequest)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PZRefundResponse refund(PZRefundRequest refundRequest)
    {
        Connection conn = null;
        TransactionEntry transactionEntry = new TransactionEntry();
        BigDecimal bdConst = new BigDecimal("0.01");
        PZRefundResponse refundResponse = new PZRefundResponse();
        try
        {
            Integer trackingId = refundRequest.getTrackingId();
            Integer accntId = refundRequest.getAccountId();
            String refundAmount = refundRequest.getRefundAmount();
            String refundReason = refundRequest.getRefundReason();

            conn = Database.getConnection();

            QwipiRequestVO RequestDetail = null;
            QwipiResponseVO transRespDetails = null;
            QwipiTransDetailsVO TransDetail = new QwipiTransDetailsVO();
            GenericCardDetailsVO cardDetail = new GenericCardDetailsVO();
            QwipiAddressDetailsVO AddressDetail = new QwipiAddressDetailsVO();

            String transaction = "select T.*,M.company_name,M.contact_emails,M.currency,M.taxper,M.reversalcharge,T.fixamount from transaction_qwipi as T,members as M where T.trackingid=? and T.toid=M.memberid and status IN('settled','capturesuccess')";
            PreparedStatement transPreparedStatement = conn.prepareStatement(transaction);
            transPreparedStatement.setString(1, String.valueOf(trackingId));
            ResultSet rstransaction = transPreparedStatement.executeQuery();

            if (rstransaction.next())
            {

                BigDecimal refundamount = null;
                try
                {
                    refundamount = new BigDecimal(refundAmount);
                    if (!Functions.checkAccuracy(refundAmount, 2))
                    {
                        refundResponse.setStatus(PZResponseStatus.FAILED);
                        refundResponse.setResponseDesceiption("Refund Amount should be 2 decimal places accurate");
                    }
                }
                catch (NumberFormatException e)
                {
                    log.debug("Invalid Refund amount" + refundAmount + "-" + e.getMessage());
                    transactionLogger.debug("Invalid Refund amount" + refundAmount + "-" + e.getMessage());
                    refundResponse.setStatus(PZResponseStatus.FAILED);
                    refundResponse.setResponseDesceiption("Invalid Refund Amount");
                }


                String icicitransid = String.valueOf(trackingId);
                String transid = rstransaction.getString("transid");
                String accountId = String.valueOf(accntId);
                String fixamt = rstransaction.getString("fixamount");
                String qwipi_paymentordernumber = rstransaction.getString("qwipiPaymentOrderNumber");
                String rsdescription = rstransaction.getString("description");
                String captureamount = rstransaction.getString("amount");
                String transactionDate = rstransaction.getString("dtstamp");
                String icicimerchantid = rstransaction.getString("fromid");
                String toid = rstransaction.getString("toid");
                String cardholdername = rstransaction.getString("name");
                int chargeper = rstransaction.getInt("chargeper");
                int transactiontaxper = rstransaction.getInt("T.taxper");
                int currtaxper = rstransaction.getInt("M.taxper");
                GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
                String reversalCharges = rstransaction.getString("M.reversalcharge");
                String icicimerchanttype = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                String transstatus = rstransaction.getString("status");
                BigDecimal amt = new BigDecimal(captureamount);
                BigDecimal charge = new BigDecimal(chargeper);
                String currenttex = String.valueOf(currtaxper);


                AuditTrailVO auditTrailVO=new AuditTrailVO();
                auditTrailVO.setActionExecutorId(icicimerchantid);
                auditTrailVO.setActionExecutorName("Admin");

                int year = 0;
                if (transactionDate != null)
                {
                    long timeInSecs = Long.parseLong(transactionDate);
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(timeInSecs * 1000);
                    year = cal.get(Calendar.YEAR);
                }

                transactionEntry.newGenericRefundTransaction(icicitransid, new BigDecimal(refundAmount), accountId, null, transRespDetails,auditTrailVO);
                transactionEntry.closeConnection();

                TransDetail.setOperation("02");
                TransDetail.setPaymentOrderNo(qwipi_paymentordernumber);
                TransDetail.setBillNo(rsdescription);
                TransDetail.setAmount(captureamount);
                TransDetail.setRefundAmount(refundamount.toString());

                //Now Reverse transaction on the gateway

                    log.debug("callng processRefund");
                    transactionLogger.debug("callng processRefund");
                    AbstractPaymentGateway paymentGateway = AbstractPaymentGateway.getGateway(accountId);
                    RequestDetail = new QwipiRequestVO(cardDetail, AddressDetail, TransDetail);
                    transRespDetails = (QwipiResponseVO) paymentGateway.processRefund(icicitransid, RequestDetail);
                    log.debug("called processRefund" + transRespDetails.getBillNo() + transRespDetails.getPaymentOrderNo() + transRespDetails.getResultCode());
                    transactionLogger.debug("called processRefund" + transRespDetails.getBillNo() + transRespDetails.getPaymentOrderNo() + transRespDetails.getResultCode());



                Database db = new Database();
                if (transRespDetails != null && (transRespDetails.getResultCode()).equals("0"))
                {
                    Codec MY = new MySQLCodec(MySQLCodec.Mode.STANDARD);

                    StringBuffer sb = new StringBuffer();
                    sb.append("update transaction_qwipi set status='reversed'");

                    sb.append(",refundinfo='" + refundReason + "'");

                    sb.append(",refundamount='" + ESAPI.encoder().encodeForSQL(MY, transRespDetails.getRefundAmount()) + "'");
                    sb.append(",refundcode='" + ESAPI.encoder().encodeForSQL(MY, transRespDetails.getResultCode()) + "'");

                    sb.append(" where trackingid=" + ESAPI.encoder().encodeForSQL(MY, icicitransid) + " and status='markedforreversal'");

                    int rows = db.executeUpdate(sb.toString(), conn);
                    log.debug("No of Rows updated : " + rows + "<br>");
                    transactionLogger.debug("No of Rows updated : " + rows + "<br>");

                    if (rows == 1)
                    {

                        boolean refunded = true;
                        // preparing collections of refunds as per merchant
                        Hashtable refundDetails = new Hashtable();
                        refundDetails.put("icicitransid", icicitransid);
                        refundDetails.put("captureamount", captureamount);
                        refundDetails.put("refundamount", transRespDetails.getRefundAmount());
                        refundDetails.put("description", rsdescription);
                        refundDetails.put("accountid", accountId);
                        refundDetails.put("cardholdername", cardholdername);


                        // Start : Added for Action and Status Entry in Action History table

                        ActionEntry entry = new ActionEntry();
                        int actionEntry = entry.actionEntryForQwipi(icicitransid, transRespDetails.getRefundAmount(), ActionEntry.ACTION_REVERSAL_SUCCESSFUL, ActionEntry.STATUS_REVERSAL_SUCCESSFUL,null,transRespDetails,auditTrailVO);
                        entry.closeConnection();

                        // End : Added for Action and Status Entry in Action History table

                        refundResponse.setStatus(PZResponseStatus.SUCCESS);
                        refundResponse.setResponseDesceiption("Transaction has been successfully reversed");
                    }
                }
                else
                {
                    refundResponse.setStatus(PZResponseStatus.FAILED);
                    refundResponse.setResponseDesceiption("Transaction cannot be Reversed.");
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
            log.error("PzTechnical Exception while refunding transaction VIA Qwipi",e);
            transactionLogger.error("PzTechnical Exception while refunding transaction VIA Qwipi",e);

            PZExceptionHandler.handleTechicalCVEException(e, null, "refund");
            refundResponse.setStatus(PZResponseStatus.ERROR);
            refundResponse.setResponseDesceiption("Error during reversal of transaction " + e.getMessage());

        }
        catch (PZConstraintViolationException e)
        {
            log.error("PZConstraintViolationException while refunding transaction VIA Qwipi",e);
            transactionLogger.error("PZConstraintViolationException while refunding transaction VIA Qwipi",e);

            PZExceptionHandler.handleCVEException(e, null, "refund");
            refundResponse.setStatus(PZResponseStatus.ERROR);
            refundResponse.setResponseDesceiption("Error during reversal of transaction " + e.getMessage());
        }
        catch (PZDBViolationException e)
        {
            log.error("PZDBViolationException while refunding transaction VIA Qwipi",e);
            transactionLogger.error("PZDBViolationException while refunding transaction VIA Qwipi",e);

            PZExceptionHandler.handleDBCVEException(e, null, "refund");
            refundResponse.setStatus(PZResponseStatus.ERROR);
            refundResponse.setResponseDesceiption("Error during reversal of transaction " + e.getMessage());
        }
        catch (SQLException e)
        {
            log.error("SQLException while refunding transaction VIA Qwipi",e);
            transactionLogger.error("SQLException while refunding transaction VIA Qwipi",e);

            PZExceptionHandler.raiseAndHandleDBViolationException(QwipiGatewayProcess.class.getName(),"refund",null,"common","Technical Exception", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause(),null,"refund");
            refundResponse.setStatus(PZResponseStatus.ERROR);
            refundResponse.setResponseDesceiption("Error during reversal of transaction " + e.getMessage());

        }
        catch (SystemError systemError)
        {
            log.error("PZDBViolationException while refunding transaction VIA Qwipi",systemError);
            transactionLogger.error("PZDBViolationException while refunding transaction VIA Qwipi",systemError);

            PZExceptionHandler.raiseAndHandleDBViolationException(QwipiGatewayProcess.class.getName(), "refund", null, "common", "Technical Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause(), null, "refund");
            refundResponse.setStatus(PZResponseStatus.ERROR);
            refundResponse.setResponseDesceiption("Error during reversal of transaction " + systemError.getMessage());

        }


        finally
        {
            Database.closeConnection(conn);
        }

        return refundResponse;
    }

    @Override
    public PZCaptureResponse capture(PZCaptureRequest captureRequest)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PZCancelResponse cancel(PZCancelRequest cancelRequest)
    {
        return null;
    }

    @Override
    public PZChargebackResponse chargeback(PZChargebackRequest pzChargebackRequest)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PZStatusResponse status(PZStatusRequest pzStatusRequest)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<PZSettlementRecord> readSettlementFile(PZSettlementFile fileName) throws SystemError
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public PZInquiryResponse inquiry(PZInquiryRequest pzInquiryRequest)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<PZReconcilationResponce> reconcilationTransaction(List<PZReconcilationRequest> pzReconcilationRequests) throws SystemError
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /*@Override
    public int actionEntry(String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO requestVO,AuditTrailVO auditTrailVO)
    {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }*/

    @Override
    public String getSpecificVirtualTerminalJSP()
    {
        return "qwipispecificfields.jsp";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setTransactionVOExtension(CommRequestVO commRequestVO, CommonValidatorVO commonValidatorVO)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PZCancelResponse cancelCapture(PZCancelRequest cancelRequest)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /*@Override
    public void setTransactionVOExtension(CommonValidatorVO commonValidatorVO)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }*/

}