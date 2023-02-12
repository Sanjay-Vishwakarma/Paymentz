 import com.directi.pg.*;
 import com.directi.pg.core.GatewayAccountService;
 import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;

 import com.directi.pg.core.valueObjects.GenericTransDetailsVO;

 import com.manager.vo.MerchantDetailsVO;
 import com.manager.vo.TerminalVO;
 import com.payment.AbstractPaymentProcess;

 import com.payment.PaymentProcessFactory;

 import com.payment.exceptionHandler.PZConstraintViolationException;
 import com.payment.exceptionHandler.PZExceptionHandler;


 import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;


 import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
 import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
 import com.payment.request.PZPayoutRequest;
 import com.payment.response.PZPayoutResponse;

 import com.payment.response.PZResponseStatus;
 import com.payment.validators.vo.CommonValidatorVO;
 import com.transaction.vo.restVO.ResponseVO.Result;

 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.text.DateFormat;
 import java.text.SimpleDateFormat;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.Hashtable;

/**
 * Created by Pramod Gotad on 1/21/2021.
 */
public class BulkPayoutProcess
{
    private static Logger logger                        = new Logger(BulkPayoutProcess.class.getName());
    private static TransactionLogger transactionLogger  = new TransactionLogger(BulkPayoutProcess.class.getName());

    public String bulkPayoutProcessStart(Hashtable hashtable)
    {
        logger.error("::: Inside BulkPayoutProcess method :::");

        StringBuffer success        = new StringBuffer();

        Connection connection   = null;
        ResultSet resultSet     = null;
        PreparedStatement ps    = null;
        String trackingid       = null;
        String amount           = null;
        String accountid        = null;
        String toid             = null;
        String merchantId       = "";
        String terminalId       = "";
        String bankname         = "";
        String bankaccount      = "";
        String ifsc             = "";
        String discription      = "";
        String transferType     = "";
        String status           = "";
        String filename         = "";
        String notificationUrl              = "";
        ActionEntry entry                   = new ActionEntry();
        int actionEntry                     = 0;
        int bulkPayoutCounter               = 0;
        String orderIdStr                   = "";
        int    orderId                      = 0;
        HashMap<String,Integer> responseHM  = new HashMap<String,Integer>();
        TerminalVO terminalVO               = null;
        Functions functions                 = new Functions();
        LimitChecker limitChecker           = new LimitChecker();
        CommonValidatorVO commonValidatorVO = null;
        ErrorCodeListVO errorCodeListVO     = new ErrorCodeListVO();
        MerchantDetailsVO merchantDetailsVO = null;
        String EOL                          = "<br>";
        boolean limtCheckFlag               = false;
        PZPayoutResponse pzPayoutResponse   = null;
        int pendingPayoutCounter            = 0;
        GenericTransDetailsVO  genericTransDetailsVO = null;
        int pocessCount                              = 0;
        int failedCount                              = 0;
        int successCount                              = 0;
        boolean isTerminalFlag                       = true;
        double successAmount    = 0.00;
        double failedAmount     = 0.00;
        double pendingAmount    = 0.00;


        try
        {
            if(hashtable.containsKey("orderIdStr")){
                orderIdStr = (String) hashtable.get("orderIdStr");
            }

            if(!functions.isValueNull(orderIdStr)){
              return  success.append("Please Select At one Payout "+EOL).toString();
            }

            merchantId = (String) hashtable.get("memberId");

            if(!functions.isValueNull(merchantId)){
                return  success.append("MemberId Not Found "+EOL).toString();
            }

            terminalVO                               = getMemberAccount(merchantId);
            transactionLogger.error("accountid---->"+terminalVO.getAccountId());
            transactionLogger.error("terminalId--->"+terminalVO.getTerminalId());


            if(!functions.isValueNull(terminalVO.getAccountId()) || !functions.isValueNull(terminalVO.getTerminalId())){
                success.append("<b>Error : </b>Account/Terminal is not mapped "+EOL);
            }

            if (functions.isValueNull(terminalVO.getPayoutActivation())
                    && "N".equalsIgnoreCase(terminalVO.getPayoutActivation()))
            {
               success.append("Terminal Id:- "+terminalVO.getTerminalId() + " provided by you for payout is not active for your account. Please check your Technical specification."+EOL);

            }
            if(success.length() > 0){
                return success.toString() ;
            }
            transactionLogger.error("BulkPayoutProcess merchantId -----------" + merchantId);
            merchantDetailsVO   = getMemberDetails(merchantId);

            connection          = Database.getConnection();

            String wCond        = "FROM bulk_payout_upload  WHERE STATUS in  ('UPLOADED') AND id in("+orderIdStr+")";
            String selectQuery  = "SELECT * " + wCond;
            String cQuery       = "SELECT count(*) " + wCond;
            transactionLogger.error("selectQuery BulkPayoutProcess-----------" + selectQuery);

            ResultSet rset = Database.executeQuery(cQuery.toString(), connection);
            if(rset.next()){
                bulkPayoutCounter = rset.getInt(1);
            }

            ps          = connection.prepareStatement(selectQuery);
            resultSet   = ps.executeQuery();

            transactionLogger.error("Select Query BulkPayoutProcess---" + ps+ "BulkPayout Counter---"+bulkPayoutCounter);

            while (resultSet.next())
            {
                DateFormat dateFormat   = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date               = new Date();
                merchantId      = resultSet.getString("merchantId");
                terminalId      = resultSet.getString("terminalId");
                bankname        = resultSet.getString("bankname");
                bankaccount     = resultSet.getString("bankaccount");
                ifsc            = resultSet.getString("ifsc");
                discription     = resultSet.getString("discription");
                amount          = resultSet.getString("amount");
                transferType    = resultSet.getString("transferType");
                status          = resultSet.getString("status");
                filename        = resultSet.getString("filename");
                orderId         = resultSet.getInt("id");

                commonValidatorVO       = new CommonValidatorVO();
                genericTransDetailsVO   = new GenericTransDetailsVO();

                genericTransDetailsVO.setAmount(amount);
                merchantDetailsVO.setMemberId(merchantId);

                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                commonValidatorVO.setErrorCodeListVO(errorCodeListVO);

                /*if("Y".equalsIgnoreCase(merchantDetailsVO.getPayout_amount_limit_check())){
                    try
                    {
                        limitChecker.checkPayoutAmountLimitMemberLevel(commonValidatorVO);
                    } catch (PZConstraintViolationException e)
                    {
                        transactionLogger.error("PZ PZConstraintViolationException exception while Payout", e);
                        checkPayoutLimitException(success,e.getPzConstraint().getErrorCodeListVO(),discription);
                        limtCheckFlag = true;
                        pendingPayoutCounter = 1 + pendingPayoutCounter;
                    }
                }*/


                logger.info("limtCheckFlag-------> "+limtCheckFlag);
                if(!limtCheckFlag)
                {

                    if (functions.isValueNull(amount))
                    {
                        try
                        {
                            amount = String.format("%.2f", Double.parseDouble(amount));
                            /*double  minPayoutAmount = Double.parseDouble(terminalVO.getMinPayoutAmount());

                            if(Double.parseDouble(amount) > minPayoutAmount  ){
                                transactionLogger.error("Payout amount is greater than minimum payout amount ")
                            }*/

                        }
                        catch (NumberFormatException e)
                        {
                            logger.debug("BulkPayoutProcess Amount NumberFormatException---");
                        }
                    }

                    transactionLogger.error("merchantId---" + merchantId);
                    transactionLogger.error("terminalId---" + terminalVO.getTerminalId());
                    transactionLogger.error("AccountId---" + terminalVO.getAccountId());
                    transactionLogger.error("bankname---" + bankname);
                    transactionLogger.error("bankaccount---" + bankaccount);
                    transactionLogger.error("ifsc---" + ifsc);
                    transactionLogger.error("discription---" + discription);
                    transactionLogger.error("amount---" + amount);
                    transactionLogger.error("status---" + status);
                    transactionLogger.error("transferType---" + transferType);
                    transactionLogger.error("orderId---" + orderId);
                    transactionLogger.error("filename---" + filename);

                    AuditTrailVO auditTrailVO = new AuditTrailVO();

                    auditTrailVO.setActionExecutorId(toid);
                    auditTrailVO.setActionExecutorName("BulkPayoutProcess");

                    PZPayoutRequest pzPayoutRequest         = new PZPayoutRequest();
                    pzPayoutResponse                        = new PZPayoutResponse();
                    AbstractPaymentGateway paymentGateway   = null;
                    String bulkpayoutstatus                 = "";

                    AbstractPaymentProcess paymentProcess   = null;
                    paymentProcess                          = PaymentProcessFactory.getPaymentProcessInstance(0, Integer.parseInt(terminalVO.getAccountId()));

                    pzPayoutRequest.setOrderId(discription);
                    pzPayoutRequest.setPayoutAmount(amount);
                    pzPayoutRequest.setOrderDescription(discription);
                    pzPayoutRequest.setNotificationUrl("");
                    pzPayoutRequest.setMemberId(Integer.parseInt(merchantId));

                    pzPayoutRequest.setTerminalId(terminalVO.getTerminalId());
                    pzPayoutRequest.setAccountId(Integer.parseInt(terminalVO.getAccountId()));
                    pzPayoutRequest.setBankAccountNo(bankaccount);
                    pzPayoutRequest.setCustomerBankAccountNumber(bankaccount);
                    pzPayoutRequest.setCustomerBankAccountName(bankname);
                    pzPayoutRequest.setBankIfsc(ifsc);
                    pzPayoutRequest.setCustomerBankCode(ifsc);
                    pzPayoutRequest.setBankTransferType(transferType);

                    pzPayoutRequest.setAuditTrailVO(auditTrailVO);

                    transactionLogger.error("PayoutProcess Start---> " + orderId);
                    pzPayoutResponse = paymentProcess.payout(pzPayoutRequest);
                    transactionLogger.error("PayoutProcess End---> " + orderId);

                   String payoutStatus      = "";
                   boolean isStatusChnged   = false;

                    if(pzPayoutResponse != null)
                    {
                        if (pzPayoutResponse.getStatus() != null){
                            payoutStatus    = String.valueOf(pzPayoutResponse.getStatus());
                        }
                        transactionLogger.error("payoutStatus---"+payoutStatus);

                        if (pzPayoutResponse.getTrackingId() != null){
                            trackingid  = (pzPayoutResponse.getTrackingId());
                        }
                        transactionLogger.error("trackingid---"+trackingid);

                        if(("pending").equalsIgnoreCase(payoutStatus))
                        {
                            bulkpayoutstatus    = "PROCESSED";
                            pocessCount         = 1 + pocessCount;
                            pendingAmount        = pendingAmount + Double.parseDouble(amount);
                            updateTrackingid(trackingid,bulkpayoutstatus, orderId);

                            //Auth Success
                            isStatusChnged = true;
                        }

                        else if(("success").equalsIgnoreCase(String.valueOf(pzPayoutResponse.getStatus()))
                                || ("SUCCESS").equalsIgnoreCase(String.valueOf(payoutStatus)) || ("PAYOUTSUCCESSFUL").equalsIgnoreCase(String.valueOf(payoutStatus)) )
                        {
                            String payoutstatus = "SUCCESS";
                            isStatusChnged      = true;
                            successCount         = 1 + successCount;
                            successAmount        = successAmount + Double.parseDouble(amount);
                            updateTrackingid(trackingid,payoutstatus, orderId);
                        }
                        else if(("fail").equalsIgnoreCase(String.valueOf(pzPayoutResponse.getStatus()))
                                || ("FAILED").equalsIgnoreCase(String.valueOf(payoutStatus)) || ("PAYOUTFAILED").equalsIgnoreCase(String.valueOf(payoutStatus)))
                        {
                            String payoutstatus = "FAILED";
                            isStatusChnged      = true;
                            failedCount         = 1 + failedCount;
                            failedAmount        = failedAmount + Double.parseDouble(amount);
                            updateTrackingid(trackingid,payoutstatus, orderId);
                        }
                        else{
                            bulkpayoutstatus    = "invalid bank response";
                            pocessCount         = 1 + pocessCount;
                            updateTrackingid(trackingid,bulkpayoutstatus, orderId);
                            pendingAmount        = pendingAmount + Double.parseDouble(amount);

                            isStatusChnged = true;
                            transactionLogger.error("inside bulkpayoutprocess else condition-->"+bulkpayoutstatus);
                        }
                        transactionLogger.error("isStatusChnged-->"+isStatusChnged);

                    }
                }
                limtCheckFlag = false;
                //Sending Notification on NotificationURL
            }

            transactionLogger.error("BulkPayoutCounter------------------>"+bulkPayoutCounter);
        }
        catch (SQLException e)
        {

            logger.error("SQLException",e);
            PZExceptionHandler.raiseAndHandleDBViolationException(BulkPayoutProcess.class.getName(), "bulkPayoutProcessStart()", null, "common", "DB Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause(), toid, "SafexpayAuthStartedAutomaticCron");
        }
        catch (SystemError systemError)
        {
            success.append("<b>Error : </b>"+systemError+"<br>");
            logger.error("SystemError", systemError);
            success.append("<b>Error : </b>"+systemError+"<br>");
            PZExceptionHandler.raiseAndHandleDBViolationException(BulkPayoutProcess.class.getName(), "bulkPayoutProcessStart()", null, "common", "DB Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause(), toid, "safexpayAuthStartedAutomaticCron");
        }
        catch (Exception e){

            logger.error("Exception--->",e);
        }
        finally
        {
           Database.closeConnection(connection);
           Database.closeResultSet(resultSet);
        }

        DateFormat dateFormat   = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date               = new Date();
        success.append("<table align=\"center\" cellspacing=\"1\"  border=\"1\" >");
        success.append("<b>Date and Time : </b>"+String.valueOf(dateFormat.format(date))+"<br>");
        success.append("<br>");
        success.append(getTableHeader(successCount,successAmount,failedCount,failedAmount,pocessCount,pendingAmount));
        success.append("</table>");
       // success.append("<b>Total Payout Transaction : </b>"+bulkPayoutCounter+"<br>");
        //success.append("<br>");
        //success.append("<b>Payout In Processing : </b>"+pocessCount+"<br>");
        //success.append("<b>Payout Failed : </b>"+failedCount+"<br>");
       // success.append("<b>Payout Pending : </b>"+pendingPayoutCounter+"<br>");

        //Auth/Capture started transaction table

        transactionLogger.error("total count---" + (pocessCount + failedCount));

      /*  AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
        asynchronousMailService.sendEmail(MailEventEnum.ADMIN_AUTHSTARTED_CRON_REPORT, "", "", success.toString(), null);*/
        return success.toString();

    }

    public boolean updateTrackingid(String trackingid, String status,int id)
    {
        boolean isUpdated       = false;
        Connection connection   = null;
        PreparedStatement preparedStatement = null;
        try
        {
            connection            = Database.getConnection();
            String updateQuery1   = "UPDATE bulk_payout_upload SET trackingid=?,status=? WHERE id=?";
            preparedStatement     = connection.prepareStatement(updateQuery1);
            preparedStatement.setString(1, trackingid);
            preparedStatement.setString(2, status);
            preparedStatement.setInt(3, id);
            int i =  preparedStatement.executeUpdate();
            if(i > 0){
                isUpdated =  true;
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException---", se);
        }
        catch (SystemError s)
        {
            logger.error("SQLException---", s);
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }

        return isUpdated;
    }


    public TerminalVO getMemberAccount(String memberid){

        transactionLogger.error("in side  updateTransaction----------->");
        Connection con                          = null;
        PreparedStatement psUpdateTransaction   = null;
        ResultSet resultSet                     = null;
        TerminalVO terminalVO                   = null;
        try
        {

            con             = Database.getConnection();

            String select           = "SELECT accountid,terminalid,payoutActivation,min_payout_amount FROM member_account_mapping WHERE memberid=? AND payoutActivation='Y' AND paymodeid=19 ";
            psUpdateTransaction     = con.prepareStatement(select.toString());
            psUpdateTransaction.setString(1,memberid);
            transactionLogger.error("getMemberAccount Bulkpayout common query----"+psUpdateTransaction);
            resultSet   = psUpdateTransaction.executeQuery();

            terminalVO  = new TerminalVO();

            if (resultSet.next())
            {
                terminalVO.setAccountId(resultSet.getString("accountid"));
                terminalVO.setTerminalId(resultSet.getString("terminalid"));
                terminalVO.setPayoutActivation(resultSet.getString("payoutActivation"));
                terminalVO.setMinPayoutAmount(resultSet.getString("min_payout_amount"));
            }
        }

        catch (SQLException e)
        {
            transactionLogger.error("getMemberAccount SQLException----",e);

        }
        catch (Exception e)
        {
            transactionLogger.error("getMemberAccount Exception----",e);
        }
        finally
        {
            Database.closePreparedStatement(psUpdateTransaction);
            Database.closeConnection(con);
        }
        return terminalVO;

    }
    public MerchantDetailsVO getMemberDetails(String toid){
        MerchantDetailsVO merchantDetailsVO     = null;
        Connection con                          = null;
        PreparedStatement preparedStatement     = null;
        ResultSet resultSet                     = null;
        try{
            con             = Database.getConnection();

            String query    = "SELECT mc.memberid, mc.payout_amount_limit_check FROM merchant_configuration AS mc WHERE mc.memberid=?";

            preparedStatement       = con.prepareStatement(query.toString());
            preparedStatement.setString(1,toid);
            resultSet       =  preparedStatement.executeQuery();
            transactionLogger.error("getMemberDetails Bulkpayout common query----"+preparedStatement);
            if(resultSet.next()){
                merchantDetailsVO  = new MerchantDetailsVO();

                merchantDetailsVO.setPayout_amount_limit_check(resultSet.getString("payout_amount_limit_check"));
                merchantDetailsVO.setMemberId(resultSet.getString("memberid"));
            }

        }catch (SQLException e)
        {
            transactionLogger.error("getMemberDetails SQLException ----",e);
        }
        catch (Exception e)
        {
            transactionLogger.error("getMemberDetails Exception ----",e);
        }finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return merchantDetailsVO;
    }
    private void checkPayoutLimitException(StringBuffer success , ErrorCodeListVO errorCodeListVO,String orderDescription){
        Result result   = null;
        String EOL      = "<BR>";

        if (errorCodeListVO != null && errorCodeListVO.getListOfError() != null)
        {
           for (ErrorCodeVO errorCodeVO : errorCodeListVO.getListOfError())
            {
                success.append(errorCodeVO.getErrorReason() + " Description ::: " + orderDescription+EOL);
                logger.debug("checkPayoutLimitException  code---" + errorCodeVO.getApiCode() + "-" + errorCodeVO.getApiDescription());
            }
        }
    }

    /*public boolean updateRemarkById(String remark,String orderIdStr)
    {
        boolean isUpdated       = false;
        Connection connection   = null;
        PreparedStatement preparedStatement = null;
        try
        {
            connection            = Database.getConnection();
            String updateQuery1   = "UPDATE bulk_payout_upload SET remark=? WHERE  id in("+orderIdStr+")";
            preparedStatement     = connection.prepareStatement(updateQuery1);
            preparedStatement.setString(1, remark);
            logger.error("updateRemarkById---"+preparedStatement);
            int i =  preparedStatement.executeUpdate();
            if(i > 0){
                isUpdated =  true;
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException---", se);
        }
        catch (SystemError s)
        {
            logger.error("SQLException---", s);
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }

        return isUpdated;
    }*/


    public String  deleteUploadedPayOut(String orderIdStr,String memberid){
        Connection con                          = null;
        PreparedStatement preparedStatement     = null;
        int resultSet                           = 0;
        String resultString                     = "";
        try{
            con             = Database.getConnection();

            String query    = "delete  FROM bulk_payout_upload  WHERE id IN ("+orderIdStr+") and merchantid=?";
            preparedStatement       = con.prepareStatement(query.toString());
            preparedStatement.setString(1,memberid);
            resultSet       =  preparedStatement.executeUpdate();
            if(resultSet > 0){
                resultString = "Payout excluded From Uploaded List";
            }

        }catch (SQLException e)
        {
            transactionLogger.error("getMemberDetails SQLException ----",e);
        }
        catch (Exception e)
        {
            transactionLogger.error("getMemberDetails Exception ----",e);
        }finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return resultString;
    }

    public StringBuffer getTableHeader(int successCount,double successAmount,int failedCount,double failedAmount,int pendingCount,double pendingAmount)
    {
        StringBuffer sHeader = new StringBuffer();

        //sHeader.append("<table align=\"center\" cellspacing=\"1\"  border=\"1\" >");
        //1st TR
        sHeader.append("<TR>");
        sHeader.append("<TD valign=\"middle\" align=\"center\" /*bgcolor=\"#0f8c93\"*/>");
        sHeader.append("<b><p align=\"center\"><font /*color=\"#FFFFFF\"*/ family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\"></font></p></b>");
        sHeader.append("</TD>");


        sHeader.append("<TD valign=\"middle\" align=\"center\" /*bgcolor=\"#0f8c93\"*/>");
        sHeader.append("<b><p align=\"center\"><font /*color=\"#FFFFFF\"*/ family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Total Count</font></p></b>");
        sHeader.append("</TD>");

        sHeader.append("<TD valign=\"middle\" align=\"center\" /*bgcolor=\"#0f8c93\"*/>");
        sHeader.append("<b><p align=\"center\"><font /*color=\"#FFFFFF\"*/ family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Total Amount</font></p></b>");
        sHeader.append("</TD>");


        //2nd TR

        sHeader.append("<TR>");
        sHeader.append("<TD valign=\"middle\" align=\"center\" /*bgcolor=\"#0f8c93\"*/>");
        sHeader.append("<b><p align=\"center\"><font /*color=\"#FFFFFF\"*/ family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Success Transaction</font></p></b>");
        sHeader.append("</TD>");


        sHeader.append("<TD>");
        sHeader.append("<p align=\"center\" bgcolor=\"\" /*color=\"#001963\"*/ font-family=\"sans-serif\" font-size=\"2px\">"+successCount+"</p>");
        sHeader.append("</TD>");

        sHeader.append("<TD>");
        sHeader.append("<p align=\"center\" bgcolor=\"\" /*color=\"#001963\"*/ font-family=\"sans-serif\" font-size=\"2px\">"+successAmount+"</p>");
        sHeader.append("</TD>");

      /*  sHeader.append("<TD valign=\"middle\" align=\"center\" bgcolor=\"#0f8c93\">");
        sHeader.append("<b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Failed Transaction Amount/font></p></b>");
        sHeader.append("</TD>");

        sHeader.append("<TD>");
        sHeader.append("<p align=\"center\" bgcolor=\"\" color=\"#001963\" font-family=\"sans-serif\" font-size=\"2px\">"+failedAmount+"</p>");
        sHeader.append("</TD>");*/

        sHeader.append("</TR>");

        //3rd Tr

        sHeader.append("<TR>");
        sHeader.append("<TD valign=\"middle\" align=\"center\" /*bgcolor=\"#0f8c93\"*/>");
        sHeader.append("<b><p align=\"center\"><font /*color=\"#FFFFFF\"*/ family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Failed Transaction</font></p></b>");
        sHeader.append("</TD>");


        sHeader.append("<TD>");
        sHeader.append("<p align=\"center\" bgcolor=\"\" /*color=\"#001963\"*/ font-family=\"sans-serif\" font-size=\"2px\">"+failedCount+"</p>");
        sHeader.append("</TD>");

        sHeader.append("<TD>");
        sHeader.append("<p align=\"center\" bgcolor=\"\" /*color=\"#001963\"*/ font-family=\"sans-serif\" font-size=\"2px\">"+failedAmount+"</p>");
        sHeader.append("</TD>");

        /*sHeader.append("<TD>");
        sHeader.append("<p align=\"center\" bgcolor=\"\" color=\"#001963\" font-family=\"sans-serif\" font-size=\"2px\">"+pendingAmount+"</p>");
        sHeader.append("</TD>");*/

        sHeader.append("</TR>");

        //4th Tr

        sHeader.append("<TR>");
        sHeader.append("<TD valign=\"middle\" align=\"center\" /*bgcolor=\"#0f8c93\"*/>");
        sHeader.append("<b><p align=\"center\"><font /*color=\"#FFFFFF\"*/ family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Pending Transaction</font></p></b>");
        sHeader.append("</TD>");


        sHeader.append("<TD>");
        sHeader.append("<p align=\"center\" bgcolor=\"\" /*color=\"#001963\"*/ font-family=\"sans-serif\" font-size=\"2px\">"+pendingCount+"</p>");
        sHeader.append("</TD>");

        sHeader.append("<TD>");
        sHeader.append("<p align=\"center\" bgcolor=\"\" /*color=\"#001963\"*/ font-family=\"sans-serif\" font-size=\"2px\">"+pendingAmount+"</p>");
        sHeader.append("</TD>");

       /* sHeader.append("<TD>");
        sHeader.append("<p align=\"center\" bgcolor=\"\" color=\"#001963\" font-family=\"sans-serif\" font-size=\"2px\">"+pocessCount+"</p>");
        sHeader.append("</TD>");*/

        sHeader.append("</TR>");

       // sHeader.append("</TABLE>");
        return sHeader;
    }

}
