package com.payment.emax_high_risk.core;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ValidationErrorList;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 2/5/15
 * Time: 7:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class EmaxHighRiskPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(EmaxHighRiskPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(EmaxHighRiskPaymentProcess.class.getName());

    public Hashtable specificValidationAPI(Hashtable<String, String> parameter)
    {
        InputValidator inputValidator = new InputValidator();
        Hashtable error = new Hashtable();
        Hashtable<InputFields, String> hashTable =new Hashtable<InputFields, String>();

        hashTable.put(InputFields.CARDHOLDERIP, parameter.get("cardholderipaddress"));

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
    public String specificValidationAPI(HttpServletRequest request)
    {
        log.debug("inside specific validation");
        transactionLogger.debug("inside specific validation");
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        Hashtable<InputFields, String> hashTable =new Hashtable<InputFields, String>();

        hashTable.put(InputFields.CARDHOLDERIP, request.getParameter("cardholderipaddress"));

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

        hashTable.put(InputFields.CARDHOLDERIP, parameter.get("cardholderipaddress"));

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
    public String getSpecificVirtualTerminalJSP()
    {
        return "emaxspecificfields.jsp";    //To change body of overridden methods use File | Settings | File Templates.
    }

    public int actionEntryExtension(int newDetailId, String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO commRequestVO) throws PZDBViolationException
    {
        log.debug("enter in Emax actionentery extension");
        transactionLogger.debug("enter in Emax actionentery extension");
        int i=0;
        Connection conn= null;

        EmaxResponseVO emaxResponseVO = null;

        String token = "";
        String stamp = "";
        String address_details = "";
        String card_holder = "";


        if(responseVO != null  && !responseVO.equals(""))
        {
            emaxResponseVO = (EmaxResponseVO) responseVO;
            token = emaxResponseVO.getToken();
            stamp = emaxResponseVO.getStamp();
            address_details = emaxResponseVO.getAddress_details();
            card_holder = emaxResponseVO.getCard_holder();

            log.debug("token---"+emaxResponseVO.getToken());
            log.debug("stamp---"+emaxResponseVO.getStamp());
            log.debug("address---"+emaxResponseVO.getAddress_details());
            log.debug("card---"+emaxResponseVO.getCard_holder());
        }
        try
        {
            conn=Database.getConnection();
            String sql="insert into transaction_emax_details(detailid,trackingid,token,stamp,address_details,card_holder) values (?,?,?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,newDetailId+"");
            pstmt.setString(2,trackingId+"");
            pstmt.setString(3,token);
            pstmt.setString(4,stamp);
            pstmt.setString(5,address_details);
            pstmt.setString(6,card_holder);

            i= pstmt.executeUpdate();
            log.debug("inseert emax---"+sql);
        }
        catch (SQLException se)
        {
            PZExceptionHandler.raiseDBViolationException(EmaxHighRiskPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(EmaxHighRiskPaymentProcess.class.getName(),"actionEntryExtension()",null,"common","Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return i;
    }


}
