package com.payment.twoGatePay;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.google.gson.Gson;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.twoGatePay.Interface_pkg.*;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 3/30/15
 * Time: 3:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class TwoGatePayPaymentGateway extends AbstractPaymentGateway
{
    private static Logger log = new Logger(TwoGatePayPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(TwoGatePayPaymentGateway.class.getName());

    public static final String GATEWAY_TYPE = "twogatepay";
    public String getMaxWaitDays()
    {
        return "3.5";
    }
    public  TwoGatePayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
       /* throw new PZGenericConstraintViolationException("This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::");*/
        PZExceptionHandler.raiseGenericViolationException(TwoGatePayPaymentGateway.class.getName(),"processAuthentication()",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team",null,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team",new Throwable("This Functionality is not supported by processing gateway. Please contact your Tech. support Team"));
        return null;
    }

    public GenericResponseVO processSale(String trackingID,GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        log.debug("Entering processSale of TwoGatePay...");
        transactionLogger.debug("Entering processSale of TwoGatePay...");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        Functions functions=new Functions();
        SaleRequest saleRequest = new SaleRequest();
        SaleRequest saleRequestLog = new SaleRequest();
        SaleResponse saleResponse = new SaleResponse();

        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commRequestVO.getAddressDetailsVO();

        String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

        saleRequest.setLoginid(Integer.parseInt(GatewayAccountService.getGatewayAccount(accountId).getMerchantId()));
        saleRequest.setPassword(GatewayAccountService.getGatewayAccount(accountId).getPassword());

        saleRequest.setPurchaseid(trackingID);
        saleRequest.setBuyerip(genericAddressDetailsVO.getIp());
        saleRequest.setAmount(Integer.parseInt(getAmount(genericTransDetailsVO.getAmount())));
        saleRequest.setCurrency(genericTransDetailsVO.getCurrency());

        saleRequest.setCardnumber(genericCardDetailsVO.getCardNum());
        saleRequest.setCvv(genericCardDetailsVO.getcVV());
        saleRequest.setExpirymonth(genericCardDetailsVO.getExpMonth());//04
        saleRequest.setExpiryyear(genericCardDetailsVO.getExpYear());//2015

        saleRequest.setBuyername(genericAddressDetailsVO.getFirstname());
        saleRequest.setBuyersurname(genericAddressDetailsVO.getLastname());
        saleRequest.setBuyeraddress(genericAddressDetailsVO.getStreet());
        saleRequest.setBuyerzipcode(genericAddressDetailsVO.getZipCode());
        saleRequest.setBuyercity(genericAddressDetailsVO.getCity());
        saleRequest.setBuyerstate(genericAddressDetailsVO.getState());
        saleRequest.setBuyercountrycode(genericAddressDetailsVO.getCountry());
        saleRequest.setBuyerphone(genericAddressDetailsVO.getPhone());
        saleRequest.setBuyeremail(genericAddressDetailsVO.getEmail());
        saleRequest.setBuyerdateofbirth("");
        saleRequest.setBuyeridverification("");
        saleRequest.setCustomfield1("");
        saleRequest.setCustomfield2("");
        saleRequest.setCustomfield3("");
        saleRequest.setCustomfield4("");

        saleRequestLog.setLoginid(Integer.parseInt(GatewayAccountService.getGatewayAccount(accountId).getMerchantId()));
        saleRequestLog.setPassword(GatewayAccountService.getGatewayAccount(accountId).getPassword());

        saleRequestLog.setPurchaseid(trackingID);
        saleRequestLog.setBuyerip(genericAddressDetailsVO.getIp());
        saleRequestLog.setAmount(Integer.parseInt(getAmount(genericTransDetailsVO.getAmount())));
        saleRequestLog.setCurrency(genericTransDetailsVO.getCurrency());

        saleRequestLog.setCardnumber(functions.maskingPan(genericCardDetailsVO.getCardNum()));
        saleRequestLog.setCvv(functions.maskingNumber(genericCardDetailsVO.getcVV()));
        saleRequestLog.setExpirymonth(functions.maskingNumber(genericCardDetailsVO.getExpMonth()));//04
        saleRequestLog.setExpiryyear(functions.maskingNumber(genericCardDetailsVO.getExpYear()));//2015

        saleRequestLog.setBuyername(genericAddressDetailsVO.getFirstname());
        saleRequestLog.setBuyersurname(genericAddressDetailsVO.getLastname());
        saleRequestLog.setBuyeraddress(genericAddressDetailsVO.getStreet());
        saleRequestLog.setBuyerzipcode(genericAddressDetailsVO.getZipCode());
        saleRequestLog.setBuyercity(genericAddressDetailsVO.getCity());
        saleRequestLog.setBuyerstate(genericAddressDetailsVO.getState());
        saleRequestLog.setBuyercountrycode(genericAddressDetailsVO.getCountry());
        saleRequestLog.setBuyerphone(genericAddressDetailsVO.getPhone());
        saleRequestLog.setBuyeremail(genericAddressDetailsVO.getEmail());
        saleRequestLog.setBuyerdateofbirth("");
        saleRequestLog.setBuyeridverification("");
        saleRequestLog.setCustomfield1("");
        saleRequestLog.setCustomfield2("");
        saleRequestLog.setCustomfield3("");
        saleRequestLog.setCustomfield4("");

        Gson gson=new Gson();
        transactionLogger.error("request--" + trackingID + "--" + gson.toJson(saleRequestLog));

        com.payment.twoGatePay.Interface_pkg.InterfaceBindingStub binding = null;
        try
        {
            binding = (com.payment.twoGatePay.Interface_pkg.InterfaceBindingStub)
                    new com.payment.twoGatePay.Interface_pkg._interfaceLocator().getInterfacePort();

            saleResponse = binding.sale(saleRequest);

            transactionLogger.error("response----"+trackingID+"--"+saleResponse);

            String status = "fail";
            if(!saleResponse.equals("") && saleResponse!=null)
            {
                if(saleResponse.getTransactionstatus().equalsIgnoreCase("success"))
                {
                    status = "success";
                }
                commResponseVO.setTransactionId(String.valueOf(saleResponse.getTransactionid()));
                commResponseVO.setTransactionStatus(saleResponse.getTransactionstatus());
                commResponseVO.setErrorCode(saleResponse.getTransactionerrorcode());
                commResponseVO.setTransactionType("sale");
                commResponseVO.setDescriptor(descriptor);
                commResponseVO.setStatus(status);
                commResponseVO.setDescription(saleResponse.getAcsrequestmessage());
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            }
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            PZExceptionHandler.raiseTechnicalViolationException("TwoGatePayPaymentGateway.java","processSale()",null,"common","Service Exception Thrown:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION,null,jre.getMessage(),jre.getCause());
        }
        catch (RemoteException re)
        {
            PZExceptionHandler.raiseTechnicalViolationException("TwoGatePayPaymentGateway.java","processSale()",null,"common","Remote Exception Thrown:::", PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null,re.getMessage(),re.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        log.debug("Entering processRefund of TwoGatePay...");
        transactionLogger.debug("Entering processRefund of TwoGatePay...");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();

        CommTransactionDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

        ReversalRequest reversalRequest = new ReversalRequest();
        ReversalResponse reversalResponse = new ReversalResponse();

        reversalRequest.setLoginid(Integer.parseInt(GatewayAccountService.getGatewayAccount(accountId).getMerchantId()));
        reversalRequest.setPassword(GatewayAccountService.getGatewayAccount(accountId).getPassword());
        reversalRequest.setTransactionid(genericTransDetailsVO.getPreviousTransactionId());
        reversalRequest.setAmount(Integer.parseInt(getAmount(genericTransDetailsVO.getAmount())));
        transactionLogger.debug("request----"+reversalRequest);

        com.payment.twoGatePay.Interface_pkg.InterfaceBindingStub binding = null;
        try
        {
            binding = (com.payment.twoGatePay.Interface_pkg.InterfaceBindingStub)
                    new com.payment.twoGatePay.Interface_pkg._interfaceLocator().getInterfacePort();

            reversalResponse = binding.reversal(reversalRequest);
            transactionLogger.debug("response----"+reversalResponse);

            String status = "fail";
            if(!reversalResponse.equals("") && reversalResponse!=null)
            {
                if(reversalResponse.getTransactionstatus().equalsIgnoreCase("success"))
                {
                    status = "success";
                }
                commResponseVO.setTransactionId(String.valueOf(reversalResponse.getTransactionid()));
                commResponseVO.setTransactionStatus(reversalResponse.getTransactionstatus());
                commResponseVO.setErrorCode(reversalResponse.getTransactionerrorcode());
                commResponseVO.setTransactionType("refund");
                commResponseVO.setDescriptor(descriptor);
                commResponseVO.setStatus(status);
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            }
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            PZExceptionHandler.raiseTechnicalViolationException("TwoGatePayPaymentGateway.java","processRefund()",null,"common","Service Exception Thrown:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION,null,jre.getMessage(),jre.getCause());
        }
        catch (RemoteException re)
        {
           PZExceptionHandler.raiseTechnicalViolationException("TwoGatePayPaymentGateway.java","processRefund()",null,"common","Remote Exception Thrown:::", PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null,re.getMessage(),re.getCause());
        }

        return commResponseVO;
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW));
        PZGenericConstraint genConstraint = new PZGenericConstraint("TwoGatePayPaymentGateway","processRebilling",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }

    public String getAmount(String amount)
    {
        Double dObj2 = Double.valueOf(amount);
        dObj2= dObj2 * 100;
        Integer newAmount = dObj2.intValue();

        return newAmount.toString();
    }
}
