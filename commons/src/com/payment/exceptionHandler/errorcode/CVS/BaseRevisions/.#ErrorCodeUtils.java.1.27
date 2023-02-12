package com.payment.exceptionHandler.errorcode;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;

import com.payment.europay.core.message.ErrorCode;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.*;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 4/14/15
 * Time: 5:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class ErrorCodeUtils
{
    private static Logger log = new Logger(ErrorCodeUtils.class.getName());
    public static HashMap<String,ErrorCodeVO> validationErrorCodes=null;
    public static HashMap<String,ErrorCodeVO>  systemErrorCodes=null;
    public static HashMap<String,ErrorCodeVO> successTransaction = null;
    public static HashMap<String,ErrorCodeVO> rejectedTransaction = null;
    public static HashMap<String,ErrorCodeVO> referenceValidation = null;
    public static HashMap<String,ErrorCodeVO> mafValidation = null;



    static
    {
        try
        {
            getErrorCodeAndDescription();
            //getPayonErrorCodeAndDescription();
        }
        catch (PZDBViolationException dbe)
        {
            log.error("Error while loading gateway accounts : " );
            PZExceptionHandler.handleDBCVEException(dbe, null, null);
        }
    }

    public ErrorCodeVO getErrorCode(ErrorName errorName )
    {
        return  validationErrorCodes.get(errorName.toString());
    }
    public ErrorCodeVO getSystemErrorCode(ErrorName errorName )
    {
        return  systemErrorCodes.get(errorName.toString());
    }
    public ErrorCodeVO getSuccessErrorCode(ErrorName errorName )
    {
        return  successTransaction.get(errorName.toString());
    }
    public ErrorCodeVO getRejectedErrorCode(ErrorName errorName)
    {
        return rejectedTransaction.get(errorName.toString());
    }
    public ErrorCodeVO getReferenceErrorCode(ErrorName errorName)
    {
        return referenceValidation.get(errorName.toString());
    }
    public ErrorCodeVO getMafErrorCode(ErrorName errorName)
    {
        return  mafValidation.get(errorName.toString());
    }

    /*public static void main(String[] args)
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_REFERENCE_TRACKINGID);
        System.out.println(errorCodeVO.getErrorCode()+"-"+errorCodeVO.getErrorDescription());
        System.out.println("error_code----"+errorCodeVO.getErrorCode());
    }*/


    public ErrorCodeVO getErrorCodeFromName(ErrorName errorName)
    {
        ErrorCodeVO errorCodeVO=new ErrorCodeVO();
        if(validationErrorCodes.containsKey(errorName.toString().trim()))
        {
            errorCodeVO = validationErrorCodes.get(errorName.toString().trim());
        }
        else if(systemErrorCodes.containsKey(errorName.toString().trim()))
        {
            errorCodeVO=systemErrorCodes.get(errorName.toString().trim());

        }
        else if(successTransaction.containsKey(errorName.toString().trim()))
        {
            errorCodeVO = successTransaction.get(errorName.toString().trim());
        }
        else if(rejectedTransaction.containsKey(errorName.toString().trim()))
        {
            errorCodeVO = rejectedTransaction.get(errorName.toString().trim());
        }
        else if(referenceValidation.containsKey(errorName.toString().trim()))
        {
            errorCodeVO = referenceValidation.get(errorName.toString().trim());
        }
        else if(mafValidation.containsKey(errorName.toString().trim()))
        {
            errorCodeVO = mafValidation.get(errorName.toString().trim());
        }
       return errorCodeVO;
    }


    public static void getErrorCodeAndDescription() throws PZDBViolationException
    {
        Connection connection = null;
        validationErrorCodes = new HashMap<String, ErrorCodeVO>();
        systemErrorCodes = new HashMap<String, ErrorCodeVO>();
        successTransaction = new HashMap<String, ErrorCodeVO>();
        rejectedTransaction = new HashMap<String, ErrorCodeVO>();
        referenceValidation = new HashMap<String, ErrorCodeVO>();
        mafValidation = new HashMap<String, ErrorCodeVO>();

        try
        {
            connection = Database.getConnection();
            String qry = "Select * from error_code where error_type=?";
            PreparedStatement preparedStatement = connection.prepareStatement(qry);
            preparedStatement.setString(1, String.valueOf(ErrorType.VALIDATION.toString()));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                errorCodeVO.setErrorType(ErrorType.VALIDATION);
                errorCodeVO.setErrorCode(resultSet.getString("error_code"));
                errorCodeVO.setErrorDescription(resultSet.getString("error_description"));
                errorCodeVO.setApiCode(resultSet.getString("api_code"));
                errorCodeVO.setApiDescription(resultSet.getString("api_description"));
                validationErrorCodes.put(resultSet.getString("error_name").trim().toUpperCase(), errorCodeVO);
            }
            resultSet=null;
            String qry1 = "Select * from error_code where error_type=?";
            preparedStatement = connection.prepareStatement(qry1);
            preparedStatement.setString(1, String.valueOf(ErrorType.SYSCHECK.toString()));
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                //errorCodeVO.setErrorName(ErrorName.valueOf(resultSet.getString("error_name")));
                errorCodeVO.setErrorType(ErrorType.SYSCHECK);
                errorCodeVO.setErrorCode(resultSet.getString("error_code"));
                errorCodeVO.setErrorDescription(resultSet.getString("error_description"));
                errorCodeVO.setApiCode(resultSet.getString("api_code"));
                errorCodeVO.setApiDescription(resultSet.getString("api_description"));
                systemErrorCodes.put(resultSet.getString("error_name").trim().toUpperCase(), errorCodeVO);

            }

            resultSet=null;
            String qry2 = "Select * from error_code where error_type=?";
            preparedStatement = connection.prepareStatement(qry2);
            preparedStatement.setString(1, String.valueOf(ErrorType.SUCCESSFUL_TRANSACTION.toString()));
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                errorCodeVO.setErrorType(ErrorType.SUCCESSFUL_TRANSACTION);
                errorCodeVO.setErrorCode(resultSet.getString("error_code"));
                errorCodeVO.setErrorDescription(resultSet.getString("error_description"));
                errorCodeVO.setApiCode(resultSet.getString("api_code"));
                errorCodeVO.setApiDescription(resultSet.getString("api_description"));
                successTransaction.put(resultSet.getString("error_name").trim().toUpperCase(), errorCodeVO);

            }

            resultSet=null;
            String qry3 = "Select * from error_code where error_type=?";
            preparedStatement = connection.prepareStatement(qry3);
            preparedStatement.setString(1, String.valueOf(ErrorType.REJECTED_TRANSACTION.toString()));
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                errorCodeVO.setErrorType(ErrorType.REJECTED_TRANSACTION);
                errorCodeVO.setErrorCode(resultSet.getString("error_code"));
                errorCodeVO.setErrorDescription(resultSet.getString("error_description"));
                errorCodeVO.setApiCode(resultSet.getString("api_code"));
                errorCodeVO.setApiDescription(resultSet.getString("api_description"));
                rejectedTransaction.put(resultSet.getString("error_name").trim().toUpperCase(), errorCodeVO);
            }

            resultSet=null;
            String qry4 = "Select * from error_code where error_type=?";
            preparedStatement = connection.prepareStatement(qry4);
            preparedStatement.setString(1, String.valueOf(ErrorType.REFERENCE_VALIDATION.toString()));
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                errorCodeVO.setErrorType(ErrorType.REFERENCE_VALIDATION);
                errorCodeVO.setErrorCode(resultSet.getString("error_code"));
                errorCodeVO.setErrorDescription(resultSet.getString("error_description"));
                errorCodeVO.setApiCode(resultSet.getString("api_code"));
                errorCodeVO.setApiDescription(resultSet.getString("api_description"));
                referenceValidation.put(resultSet.getString("error_name").trim().toUpperCase(), errorCodeVO);
            }

            resultSet=null;
            String qry5 = "Select * from error_code where error_type=?";
            preparedStatement = connection.prepareStatement(qry5);
            preparedStatement.setString(1, String.valueOf(ErrorType.MAF_VALIDATION.toString()));
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                errorCodeVO.setErrorType(ErrorType.MAF_VALIDATION);
                errorCodeVO.setErrorCode(resultSet.getString("error_code"));
                errorCodeVO.setErrorDescription(resultSet.getString("error_description"));
                errorCodeVO.setApiCode(resultSet.getString("api_code"));
                errorCodeVO.setApiDescription(resultSet.getString("api_description"));
                mafValidation.put(resultSet.getString("error_name").trim().toUpperCase(), errorCodeVO);
            }
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("ErrorCodeUtils","getValidationErrorCodeAndDescription()",null,"common","Error while fetch error codes", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException("ErrorCodeUtils", "getValidationErrorCodeAndDescription()", null, "common", "Error while fetch error codes", PZDBExceptionEnum.SQL_EXCEPTION, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
    }

    /*public static void getPayonErrorCodeAndDescription() throws PZDBViolationException
    {
        Connection connection = null;
        //payonErrorCodes = new HashMap<String, PayonResultCodeVO>();
        try
        {
            connection = Database.getConnection();
            String qry = "SELECT * FROM payon_result_code WHERE result_subtype =?";
            PreparedStatement preparedStatement = connection.prepareStatement(qry);
            preparedStatement.setString(1, String.valueOf(ResultSubType.REJECTION_BY_EXTERNAL_BANK_OR_SIMILARPAYMENT_SYSTEM.toString()));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                PayonResultCodeVO payonResultCodeVO = new PayonResultCodeVO();
                payonResultCodeVO.setResultSubType(ResultSubType.REJECTION_BY_EXTERNAL_BANK_OR_SIMILARPAYMENT_SYSTEM);
                payonResultCodeVO.setResultDescription(resultSet.getString("result_description"));
                payonResultCodeVO.setResultCode(resultSet.getString("result_code"));
                //payonErrorCodes.put(resultSet.getString("result_name").trim().toUpperCase(), payonResultCodeVO);

                log.debug("query for getPayonErrorCodeAndDescription---"+preparedStatement);
            }

            resultSet=null;

            String qry1 = "SELECT * FROM payon_result_code WHERE result_subtype =?";
            preparedStatement = connection.prepareStatement(qry1);
            preparedStatement.setString(1, String.valueOf(ResultSubType.SUCCESSFULLY_PROCESSED.toString()));
             resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                PayonResultCodeVO payonResultCodeVO = new PayonResultCodeVO();
                payonResultCodeVO.setResultSubType(ResultSubType.SUCCESSFULLY_PROCESSED);
                payonResultCodeVO.setResultDescription(resultSet.getString("result_description"));
                payonResultCodeVO.setResultCode(resultSet.getString("result_code"));
                //payonErrorCodes.put(resultSet.getString("result_name").trim().toUpperCase(), payonResultCodeVO);

                log.debug("query for getPayonErrorCodeAndDescription---"+preparedStatement);
            }

        }
        catch (SystemError e)
        {
            PZExceptionHandler.raiseDBViolationException("ErrorCodeUtils", "getPayonErrorCodeAndDescription()", null, "common", "Error while fatch error codes", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());

        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("ErrorCodeUtils","getPayonErrorCodeAndDescription()",null,"common","Error while fatch error codes", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
    }*/

    public String getSystemErrorCodeVOForDKIT(ErrorCodeListVO errorCodeListVO)
    {
        String error="";
        if(errorCodeListVO!=null)
        {
            for (ErrorCodeVO errorCodeVO:errorCodeListVO.getListOfError())
            {
                if(errorCodeVO.getErrorDescription()!=null)
                {
                   // error = error + errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ", " + errorCodeVO.getErrorReason();
                    error = error + errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription();
                }
                else
                {
                    error = error + errorCodeVO.getErrorReason();
                }
            }
        }
        else
        {
            error="Internal Error Occurred,Contact Support Team for more information";
        }
        return error;
    }
    public String getSystemErrorCodeVO(ErrorCodeListVO errorCodeListVO)
    {
        String error="";
        if(errorCodeListVO!=null)
        {
            for (ErrorCodeVO errorCodeVO:errorCodeListVO.getListOfError())
            {
                System.out.println("error code in utils---"+errorCodeVO.getErrorCode());
                if(errorCodeVO.getErrorDescription()!=null)
                {
                   // error = error + errorCodeVO.getErrorDescription() + "," + errorCodeVO.getErrorReason() + " <BR> ";
                    error = error + errorCodeVO.getErrorDescription()+ "<BR>";
                }
                else
                {
                    error = error + errorCodeVO.getErrorReason() + "<BR>";
                }
            }
        }
        else
        {
            error="Internal Error Occurred,Contact Support Team for more information";
        }
        return error;
    }

    public String getSystemErrorCodesVO(ErrorCodeListVO errorCodeListVO)
    {
        String errorCode="";
        if(errorCodeListVO!=null)
        {
            for (ErrorCodeVO errorCodeVO:errorCodeListVO.getListOfError())
            {
                System.out.println("error code in utils---"+errorCodeVO.getErrorCode());
                if(errorCodeVO.getErrorDescription()!=null)
                {
                    // error = error + errorCodeVO.getErrorDescription() + "," + errorCodeVO.getErrorReason() + " <BR> ";
                    errorCode = errorCode + errorCodeVO.getApiCode()+ "<BR>";
                }
                else
                {
                    errorCode = errorCode + errorCodeVO.getApiCode() + "<BR>";
                }
            }
        }
        else
        {
            errorCode="Internal Error Occurred,Contact Support Team for more information";
        }
        return errorCode;
    }

    public String getErrorNames(List<ErrorCodeVO> errorCodeListVO)
    {
        String error="";
        if(errorCodeListVO!=null)
        {
            for (ErrorCodeVO errorCodeVO:errorCodeListVO)
            {
                if(errorCodeVO.getErrorName()!=null)
                {
                    error = error + errorCodeVO.getErrorName().toString()+"<BR>";
                }

            }
        }
        else
        {
            error="Internal Error Occurred,Contact Support Team for more information";
        }
        return error;
    }

    public String getErrorName(ErrorCodeVO errorCodeVO)
    {
        String error="";
        if(errorCodeVO!=null)
        {

            if(errorCodeVO.getErrorName()!=null)
            {
                error = errorCodeVO.getErrorName().toString();
            }


        }

        return error;
    }


    public List<ErrorCodeVO> getErrorCode(ErrorType errorType) throws PZDBViolationException
    {
        PreparedStatement preparedStatement = null;
        Connection connection = null;
        List<ErrorCodeVO> errorCodeVOList = new ArrayList();
        ResultSet resultSet = null;
        try
        {
            connection = Database.getConnection();
            String qry2 = "select * from error_code where error_type = ?";
            preparedStatement = connection.prepareStatement(qry2);
            preparedStatement.setString(1, String.valueOf(errorType));
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                errorCodeVO.setApiCode(resultSet.getString("api_code"));
                errorCodeVO.setApiDescription(resultSet.getString("api_description"));
                errorCodeVO.setErrorCode(resultSet.getString("error_code"));
                errorCodeVO.setErrorDescription(resultSet.getString("error_description"));
                errorCodeVOList.add(errorCodeVO);
            }
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("ErrorCodeUtils","getErrorCodeForAPI()",null,"common","Error while fetch error codes", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (SystemError e)
        {
            PZExceptionHandler.raiseDBViolationException("ErrorCodeUtils","getErrorCodeForAPI()",null,"common","Error while fetch error codes", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return errorCodeVOList;
    }
}