package com.payment.b4payment;

import com.directi.pg.*;
import com.payment.b4payment.vos.TransactionRequest;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
/**
 * Created by Admin on 8/2/2017.
 */
public class B4PaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(B4PaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(B4PaymentProcess.class.getName());

    public String getSpecificVirtualTerminalJSP()
    {
        return "b4SepaExpress.jsp";    //To change body of overridden methods use File | Settings | File Templates.
    }

    public int actionEntryExtension(int newDetailId, String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO requestVO) throws PZDBViolationException
    {
        log.debug("Entering ActionEntry for B4PaymentProcess Details");
        transactionLogger.debug("Entering ActionEntry for B4PaymentProcess Details");

        String recipient_bic="";
        String recipient_iban="";
        String recipient_mandateId="";

        Functions functions = new Functions();

        TransactionRequest transactionRequest = null;
        int results = 0;
        //int newDetailId = 0;

        if (requestVO != null){
            if(requestVO.getCardDetailsVO()!=null){
                recipient_bic=functions.isValueNull(requestVO.getCardDetailsVO().getBIC())?requestVO.getCardDetailsVO().getBIC():"";
                recipient_iban=functions.isValueNull(requestVO.getCardDetailsVO().getIBAN())?requestVO.getCardDetailsVO().getIBAN():"";
                recipient_mandateId=functions.isValueNull(requestVO.getCardDetailsVO().getMandateId())?requestVO.getCardDetailsVO().getMandateId():"";
                log.debug("BIC IBAN MANDATEID:::::"+recipient_bic+"-----"+recipient_iban+"----"+recipient_mandateId);
                transactionLogger.debug("BIC IBAN MANDATEID:::::"+recipient_bic+"-----"+recipient_iban+"----"+recipient_mandateId);
            }
        }

        Connection conn = null;
        int k=0;
        try
        {
            String sql = "insert into transaction_b4sepaexpress_details(trackingId,detailId,IBAN,BIC,MandateId) values (?,?,?,?,?)";
            conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, trackingId);
            pstmt.setInt(2, newDetailId);
            pstmt.setString(3, recipient_iban);
            pstmt.setString(4, recipient_bic);
            pstmt.setString(5, recipient_mandateId);

            k = pstmt.executeUpdate();
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(B4PaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(B4PaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return k;
    }
}
