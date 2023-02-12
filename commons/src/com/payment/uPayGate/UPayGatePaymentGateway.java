package com.payment.uPayGate;

import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator;
import com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.payforasia.core.PayforasiaUtils;
import com.payment.uPayGate.Interface_pkg.*;
import org.apache.axis.AxisFault;

import javax.xml.bind.SchemaOutputResolver;
import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static junit.framework.Assert.assertNotNull;

/**
 * Created by admin on 7/26/2017.
 */
public class UPayGatePaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionLogger = new TransactionLogger(UPayGatePaymentGateway.class.getName());

    public static final String GATEWAY_TYPE = "UPayGate";

    public String getMaxWaitDays()
    {
        return "3.5";
    }
    public  UPayGatePaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.debug("Entering processSale of Allied Wallet...");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();

        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();

        try
        {
            validateForSale(trackingID,requestVO);

            String merchantID = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
            String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
            String password = GatewayAccountService.getGatewayAccount(accountId).getPassword();

            DirectAuthorizationRequest directAuthorizationRequest = new DirectAuthorizationRequest();
            directAuthorizationRequest.setAccountID(merchantID);
            directAuthorizationRequest.setPassword(password);
            directAuthorizationRequest.setOrderID(genericTransDetailsVO.getOrderId());
            directAuthorizationRequest.setCustomerIP(addressDetailsVO.getCardHolderIpAddress());
            directAuthorizationRequest.setAmount(Integer.valueOf(genericTransDetailsVO.getAmount().replace(".", "")));
            directAuthorizationRequest.setCurrency(genericTransDetailsVO.getCurrency());
            directAuthorizationRequest.setCustomerID(trackingID);
            directAuthorizationRequest.setCardHolderName(addressDetailsVO.getFirstname()+" "+addressDetailsVO.getLastname());
            directAuthorizationRequest.setCardHolderAddress(addressDetailsVO.getStreet());
            directAuthorizationRequest.setCardHolderZipcode(addressDetailsVO.getZipCode());
            directAuthorizationRequest.setCardHolderCity(addressDetailsVO.getCity());
            directAuthorizationRequest.setCardHolderState(addressDetailsVO.getState());
            directAuthorizationRequest.setCardHolderCountryCode(addressDetailsVO.getCountry());
            directAuthorizationRequest.setCardHolderPhone(addressDetailsVO.getPhone());
            directAuthorizationRequest.setCardHolderEmail(addressDetailsVO.getEmail());
            directAuthorizationRequest.setCardNumber(genericCardDetailsVO.getCardNum());
            directAuthorizationRequest.setCardSecurityCode(genericCardDetailsVO.getcVV());
            directAuthorizationRequest.setCardExpireMonth(genericCardDetailsVO.getExpMonth());
            directAuthorizationRequest.setCardExpireYear(genericCardDetailsVO.getExpYear());

            //transactionLogger.debug("request data...");

            InterfaceBindingStub interfaceBindingStub = new InterfaceBindingStub();

            interfaceBindingStub = (InterfaceBindingStub) new InterfaceLocator().getInterfacePort();

            assertNotNull("binding is null", interfaceBindingStub);
            interfaceBindingStub.setTimeout(60000);

            com.payment.uPayGate.Interface_pkg.AuthorizationResponse response = null;

            response = interfaceBindingStub.authorizeTransaction(directAuthorizationRequest);

            if(response!=null)
            {
                String status = "fail";

                if (response.getErrorCode().equals("000") && response.getStatus().equalsIgnoreCase("Authorized"))
                {
                    status = "success";
                }

                commResponseVO.setTransactionId(response.getTransactionID());
                commResponseVO.setTransactionStatus(status);
                commResponseVO.setErrorCode(response.getErrorCode());
                commResponseVO.setDescription(response.getErrorMessage());
                commResponseVO.setStatus(status);
                commResponseVO.setRemark(response.getErrorDescription());
                commResponseVO.setTransactionType("auth");
                commResponseVO.setDescriptor(descriptor);
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            }
        }

        catch (AxisFault e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("UPayGatePaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.AXISFAULT, null, e.getMessage(), e.getCause());
        }
        catch (ServiceException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("UPayGatePaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (RemoteException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("UPayGatePaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        return commResponseVO;

    }
    public GenericResponseVO processSale(String trackingID,GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionLogger.debug("Entering processSale of Allied Wallet...");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();

        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();

        try
        {
            validateForSale(trackingID,requestVO);

            String merchantID = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
            String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
            String password = GatewayAccountService.getGatewayAccount(accountId).getPassword();

            DirectAuthorizationRequest directAuthorizationRequest = new DirectAuthorizationRequest();
            directAuthorizationRequest.setAccountID(merchantID);
            directAuthorizationRequest.setPassword(password);
            directAuthorizationRequest.setOrderID(trackingID);
            directAuthorizationRequest.setCustomerIP(addressDetailsVO.getCardHolderIpAddress());
            directAuthorizationRequest.setAmount(Integer.valueOf(genericTransDetailsVO.getAmount().replace(".", "")));
            directAuthorizationRequest.setCurrency(genericTransDetailsVO.getCurrency());
            directAuthorizationRequest.setCustomerID(genericTransDetailsVO.getOrderId());
            directAuthorizationRequest.setCardHolderName(addressDetailsVO.getFirstname()+" "+addressDetailsVO.getLastname());
            directAuthorizationRequest.setCardHolderAddress(addressDetailsVO.getStreet());
            directAuthorizationRequest.setCardHolderZipcode(addressDetailsVO.getZipCode());
            directAuthorizationRequest.setCardHolderCity(addressDetailsVO.getCity());
            directAuthorizationRequest.setCardHolderState(addressDetailsVO.getState());
            directAuthorizationRequest.setCardHolderCountryCode(addressDetailsVO.getCountry());
            directAuthorizationRequest.setCardHolderPhone(addressDetailsVO.getPhone());
            directAuthorizationRequest.setCardHolderEmail(addressDetailsVO.getEmail());
            directAuthorizationRequest.setCardNumber(genericCardDetailsVO.getCardNum());
            directAuthorizationRequest.setCardSecurityCode(genericCardDetailsVO.getcVV());
            directAuthorizationRequest.setCardExpireMonth(genericCardDetailsVO.getExpMonth());
            directAuthorizationRequest.setCardExpireYear(genericCardDetailsVO.getExpYear());

            //transactionLogger.debug("request data...");

            InterfaceBindingStub interfaceBindingStub = new InterfaceBindingStub();

            interfaceBindingStub = (InterfaceBindingStub) new InterfaceLocator().getInterfacePort();

            assertNotNull("binding is null", interfaceBindingStub);
            interfaceBindingStub.setTimeout(60000);

            com.payment.uPayGate.Interface_pkg.AuthorizationResponse response = null;

            response = interfaceBindingStub.saleTransaction(directAuthorizationRequest);

            if(response!=null)
            {
                String status = "fail";

                transactionLogger.debug("Transaction Id======" + response.getTransactionID());
                transactionLogger.debug("Order Id======" + response.getOrderID());
                transactionLogger.debug("Status======" + response.getStatus());
                transactionLogger.debug("getError Code Id======" + response.getErrorCode());
                transactionLogger.debug("Get Error MSG Id======" + response.getErrorMessage());
                transactionLogger.debug("Descriptor=======" + descriptor);

                if (response.getErrorCode().equals("000") && response.getStatus().equalsIgnoreCase("Authorized"))
                {
                    status = "success";
                }

                commResponseVO.setTransactionId(response.getTransactionID());
                commResponseVO.setTransactionStatus(status);
                commResponseVO.setErrorCode(response.getErrorCode());
                commResponseVO.setDescription(response.getErrorMessage());
                commResponseVO.setStatus(status);
                commResponseVO.setRemark(response.getErrorDescription());
                commResponseVO.setTransactionType("sale");
                commResponseVO.setDescriptor(descriptor);
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            }
        }

        catch (AxisFault e)
        {
            transactionLogger.error("AxisFault in UPayGate---",e);
            PZExceptionHandler.raiseTechnicalViolationException("UPayGatePaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.AXISFAULT, null, e.getMessage(), e.getCause());
        }
        catch (ServiceException e)
        {
            transactionLogger.error("ServiceException in UPayGate---",e);
            PZExceptionHandler.raiseTechnicalViolationException("UPayGatePaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (RemoteException e)
        {
            transactionLogger.error("RemoteException in UPayGate---",e);
            PZExceptionHandler.raiseTechnicalViolationException("UPayGatePaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug("Enter in processRefund of UPayGate---");
        CommRequestVO reqVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = reqVO.getTransDetailsVO();
        CommResponseVO commResponseVO = new CommResponseVO();

        String merchantID = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
        String password = GatewayAccountService.getGatewayAccount(accountId).getPassword();

        try
        {
            InterfaceBindingStub interfaceBindingStub = new InterfaceBindingStub();

            interfaceBindingStub = (InterfaceBindingStub) new InterfaceLocator().getInterfacePort();

            assertNotNull("binding is null", interfaceBindingStub);
            interfaceBindingStub.setTimeout(60000);
            transactionLogger.debug("Response Transaction Id refund======" + commTransactionDetailsVO.getPreviousTransactionId());
            ReferralDetailedRequest referralDetailedRequest = new ReferralDetailedRequest();
            referralDetailedRequest.setAccountID(merchantID);
            referralDetailedRequest.setPassword(password);
            referralDetailedRequest.setTransactionID(commTransactionDetailsVO.getPreviousTransactionId());
            referralDetailedRequest.setAmount(Integer.valueOf(commTransactionDetailsVO.getAmount().replace(".", "")));
            com.payment.uPayGate.Interface_pkg.Response response = null;

            response = interfaceBindingStub.refundTransaction(referralDetailedRequest);

            if(response!=null)
            {
                transactionLogger.debug("Status======" + response.getStatus());
                transactionLogger.debug("getError Code Id======" + response.getErrorCode());
                String status = "fail";
                if(response.getErrorCode().equals("000") && response.getStatus().equalsIgnoreCase("Authorized"))
                {
                    status = "success";
                }
                transactionLogger.debug("Transaction Id======" + response.getTransactionID());

                transactionLogger.debug("Get Error MSG Id======" + response.getErrorMessage());
                transactionLogger.debug("Descriptor======="+descriptor);
                commResponseVO.setTransactionId(response.getTransactionID());
                commResponseVO.setTransactionStatus(status);
                commResponseVO.setErrorCode(response.getErrorCode());
                commResponseVO.setDescription(response.getErrorMessage());
                commResponseVO.setStatus(status);
                commResponseVO.setRemark(response.getErrorDescription());
                commResponseVO.setTransactionType("refund");
                commResponseVO.setDescriptor(descriptor);
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            }
        }
        catch (AxisFault e)
        {
            transactionLogger.error("AxisFault in UPayGate---",e);
            PZExceptionHandler.raiseTechnicalViolationException("UPayGatePaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.AXISFAULT, null, e.getMessage(), e.getCause());
        }
        catch (ServiceException e)
        {
            transactionLogger.error("ServiceException in UPayGate---",e);
            PZExceptionHandler.raiseTechnicalViolationException("UPayGatePaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (RemoteException e)
        {
            transactionLogger.error("RemoteException in UPayGate---",e);
            PZExceptionHandler.raiseTechnicalViolationException("UPayGatePaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, e.getMessage(), e.getCause());
        }

        return commResponseVO;
    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug("Enter in processVoid of UPayGate---");
        CommRequestVO reqVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = reqVO.getTransDetailsVO();
        CommResponseVO commResponseVO = new CommResponseVO();

        String merchantID = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
        String password = GatewayAccountService.getGatewayAccount(accountId).getPassword();

        try
        {
            InterfaceBindingStub interfaceBindingStub = new InterfaceBindingStub();

            interfaceBindingStub = (InterfaceBindingStub) new InterfaceLocator().getInterfacePort();

            assertNotNull("binding is null", interfaceBindingStub);
            interfaceBindingStub.setTimeout(60000);
            transactionLogger.debug("Response Transaction Id======" + commTransactionDetailsVO.getPreviousTransactionId());
            ReferralRequest referralRequest = new ReferralRequest();
            referralRequest.setAccountID(merchantID);
            referralRequest.setPassword(password);
            referralRequest.setTransactionID(commTransactionDetailsVO.getPreviousTransactionId());
            com.payment.uPayGate.Interface_pkg.Response response = null;

            response = interfaceBindingStub.cancelTransaction(referralRequest);

            if(response!=null)
            {
                String status = "fail";
                if(response.getErrorCode().equals("000") && response.getStatus().equalsIgnoreCase("Authorized"))
                {
                    status = "success";
                }
                transactionLogger.debug("Transaction Id======" + response.getTransactionID());
                transactionLogger.debug("Status======" + response.getStatus());
                transactionLogger.debug("getError Code Id======" + response.getErrorCode());
                transactionLogger.debug("Get Error MSG Id======" + response.getErrorMessage());
                transactionLogger.debug("Descriptor======="+descriptor);
                commResponseVO.setTransactionId(response.getTransactionID());
                commResponseVO.setTransactionStatus(status);
                commResponseVO.setErrorCode(response.getErrorCode());
                commResponseVO.setDescription(response.getErrorMessage());
                commResponseVO.setStatus(status);
                commResponseVO.setRemark(response.getErrorDescription());
                commResponseVO.setTransactionType("cancel");
                commResponseVO.setDescriptor(descriptor);
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            }
        }
        catch (AxisFault e)
        {
            transactionLogger.error("AxisFault in UPayGate---",e);
            PZExceptionHandler.raiseTechnicalViolationException("UPayGatePaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.AXISFAULT, null, e.getMessage(), e.getCause());
        }
        catch (ServiceException e)
        {
            transactionLogger.error("ServiceException in UPayGate---",e);
            PZExceptionHandler.raiseTechnicalViolationException("UPayGatePaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (RemoteException e)
        {
            transactionLogger.error("RemoteException in UPayGate---",e);
            PZExceptionHandler.raiseTechnicalViolationException("UPayGatePaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug("Enter in processCapture of UPayGate---");
        CommRequestVO reqVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = reqVO.getTransDetailsVO();
        CommResponseVO commResponseVO = new CommResponseVO();

        String merchantID = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
        String password = GatewayAccountService.getGatewayAccount(accountId).getPassword();

        try
        {
            InterfaceBindingStub interfaceBindingStub = new InterfaceBindingStub();

            interfaceBindingStub = (InterfaceBindingStub) new InterfaceLocator().getInterfacePort();

            assertNotNull("binding is null", interfaceBindingStub);
            interfaceBindingStub.setTimeout(60000);
            transactionLogger.debug("Response Transaction Id capture======" + commTransactionDetailsVO.getPreviousTransactionId());
            ReferralDetailedRequest referralDetailedRequest = new ReferralDetailedRequest();
            referralDetailedRequest.setAccountID(merchantID);
            referralDetailedRequest.setPassword(password);
            referralDetailedRequest.setTransactionID(commTransactionDetailsVO.getPreviousTransactionId());
            referralDetailedRequest.setAmount(Integer.valueOf(commTransactionDetailsVO.getAmount().replace(".", "")));
            com.payment.uPayGate.Interface_pkg.Response response = null;

            response = interfaceBindingStub.captureTransaction(referralDetailedRequest);

            if(response!=null)
            {
                String status = "fail";
                if(response.getErrorCode().equals("000") && response.getStatus().equalsIgnoreCase("Authorized"))
                {
                    status = "success";
                }
                transactionLogger.debug("Transaction Id======" + response.getTransactionID());
                transactionLogger.debug("Status======" + response.getStatus());
                transactionLogger.debug("getError Code Id======" + response.getErrorCode());
                transactionLogger.debug("Get Error MSG Id======" + response.getErrorMessage());
                transactionLogger.debug("Descriptor======="+descriptor);
                commResponseVO.setTransactionId(response.getTransactionID());
                commResponseVO.setTransactionStatus(status);
                commResponseVO.setErrorCode(response.getErrorCode());
                commResponseVO.setDescription(response.getErrorMessage());
                commResponseVO.setStatus(status);
                commResponseVO.setRemark(response.getErrorDescription());
                commResponseVO.setTransactionType("capture");
                commResponseVO.setDescriptor(descriptor);
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            }
        }
        catch (AxisFault e)
        {
            transactionLogger.error("AxisFault in UPayGate---",e);
            PZExceptionHandler.raiseTechnicalViolationException("UPayGatePaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.AXISFAULT, null, e.getMessage(), e.getCause());
        }
        catch (ServiceException e)
        {
            transactionLogger.error("ServiceException in UPayGate---",e);
            PZExceptionHandler.raiseTechnicalViolationException("UPayGatePaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (RemoteException e)
        {
            transactionLogger.error("RemoteException in UPayGate---",e);
            PZExceptionHandler.raiseTechnicalViolationException("UPayGatePaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, e.getMessage(), e.getCause());
        }

        return commResponseVO;
    }

    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug("Inside processInquiry :::");
        CommRequestVO reqVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO transactionDetailsVO = reqVO.getTransDetailsVO();
        CommResponseVO commResponseVO = new CommResponseVO();

        String merchantID = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String password = GatewayAccountService.getGatewayAccount(accountId).getPassword();

        try
        {
            InterfaceBindingStub interfaceBindingStub = new InterfaceBindingStub();

            interfaceBindingStub = (InterfaceBindingStub) new InterfaceLocator().getInterfacePort();

            assertNotNull("binding is null", interfaceBindingStub);
            interfaceBindingStub.setTimeout(60000);

            OrderStatusRequest request = new OrderStatusRequest();

            request.setAccountID(merchantID);
            request.setPassword(password);
            request.setOrderID(transactionDetailsVO.getOrderId());
            transactionLogger.debug("Response Transaction Id capture======");
            com.payment.uPayGate.Interface_pkg.OrderStatusResponse response = null;

            response = interfaceBindingStub.orderStatus(request);

            if(response!=null)
            {
                String status = "fail";

                if (response.getErrorCode().equals("000") && response.getResult().equalsIgnoreCase("exist"))
                {
                    status = "success";
                }
                commResponseVO.setTransactionId(response.getTransactionID());
                commResponseVO.setAuthCode(response.getTransactionID());
                commResponseVO.setTransactionStatus(status);
                commResponseVO.setStatus(status);
                commResponseVO.setBankTransactionDate(response.getTransactionDate());
                commResponseVO.setErrorCode(response.getErrorCode());
                commResponseVO.setDescription(response.getTransactionErrorMessage());
                commResponseVO.setRemark(response.getErrorMessage());
            }
        }
        catch (AxisFault e)
        {
            transactionLogger.error("AxisFault in UPayGate---",e);
            PZExceptionHandler.raiseTechnicalViolationException("UPayGatePaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.AXISFAULT, null, e.getMessage(), e.getCause());
        }
        catch (ServiceException e)
        {
            transactionLogger.error("ServiceException in UPayGate---",e);
            PZExceptionHandler.raiseTechnicalViolationException("UPayGatePaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (RemoteException e)
        {
            transactionLogger.error("RemoteException in UPayGate---",e);
            PZExceptionHandler.raiseTechnicalViolationException("UPayGatePaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    private void validateForSale(String trackingId, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        CommRequestVO commRequestVO= (CommRequestVO) requestVO;
        GenericTransDetailsVO genericTransDetailsVO=commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO=commRequestVO.getAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO=commRequestVO.getCardDetailsVO();


        if(genericTransDetailsVO != null)
        {
            if(genericTransDetailsVO.getAmount()==null || genericTransDetailsVO.getAmount().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(UPayGatePaymentGateway.class.getName(),"validateForSale()",null,"Common","Amount not provided", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Amount not provided for the transaction",new Throwable("Amount not provided for the transaction"));
            }
            if(genericTransDetailsVO.getCurrency()==null || genericTransDetailsVO.getCurrency().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(UPayGatePaymentGateway.class.getName(),"validateForSale()",null,"Common","Currency not provided", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Currency not provided for the transaction",new Throwable("Currency not provided for the transaction"));
            }
        }
        else
        {
            PZExceptionHandler.raiseConstraintViolationException(UPayGatePaymentGateway.class.getName(),"validateForSale()",null,"Common","Transaction Detail not provided", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Transaction detail not provided for the transaction",new Throwable("Transaction Detail not provided for the transaction"));
        }
        if(addressDetailsVO != null)
        {
            if(addressDetailsVO.getIp()==null || addressDetailsVO.getIp().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(UPayGatePaymentGateway.class.getName(),"validateForSale()",null,"Common","IpAddress not provided", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"IppAddress not provided for the transaction",new Throwable("IpAddress not provided for the transaction"));
            }
            if(addressDetailsVO.getFirstname()==null || addressDetailsVO.getFirstname().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(UPayGatePaymentGateway.class.getName(),"validateForSale()",null,"Common","FirstName not provided", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"FirtsName not provided for the transaction",new Throwable("FirstName not provided for the transaction"));
            }
            if(addressDetailsVO.getLastname()==null || addressDetailsVO.getLastname().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(UPayGatePaymentGateway.class.getName(),"validateForSale()",null,"Common","LastName not provided", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"LastName not provided for the transaction",new Throwable("LastName not provided for the transaction"));
            }

            GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
            String addressValidation = account.getAddressValidation();
            if (addressValidation.equalsIgnoreCase("Y"))
            {
                if (addressDetailsVO.getStreet() == null || addressDetailsVO.getStreet().equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException(UPayGatePaymentGateway.class.getName(), "validateForSale()", null, "Common", "Street not provided", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Street not provided for the transaction", new Throwable("Street not provided for the transaction"));
                }
                if (addressDetailsVO.getCity() == null || addressDetailsVO.getCity().equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException(UPayGatePaymentGateway.class.getName(), "validateForSale()", null, "Common", "City not provided", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "City not provided for the transaction", new Throwable("City not provided for the transaction"));
                }
                if (addressDetailsVO.getState() == null || addressDetailsVO.getState().equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException(UPayGatePaymentGateway.class.getName(), "validateForSale()", null, "Common", "State not provided", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "State not provided for the transaction", new Throwable("State not provided for the transaction"));
                }
                if (addressDetailsVO.getCountry() == null || addressDetailsVO.getCountry().equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException(UPayGatePaymentGateway.class.getName(), "validateForSale()", null, "Common", "Country not provided", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Country not provided for the transaction ", new Throwable("Country not provided for the transaction"));
                }
                if (addressDetailsVO.getZipCode() == null || addressDetailsVO.getZipCode().equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException(UPayGatePaymentGateway.class.getName(), "validateForSale()", null, "Common", "Postal Code not provided", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Postal Code not provided for the transaction ", new Throwable("Postal Code not provided for the transaction"));
                }
                if(addressDetailsVO.getPhone()==null || addressDetailsVO.getPhone().equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException(UPayGatePaymentGateway.class.getName(),"validateForSale()",null,"Common","Phone NO not provided", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Phone NO not provided for the transaction",new Throwable("Phone No not provided for the transaction"));
                }
            }
            if(addressDetailsVO.getEmail()==null || addressDetailsVO.getEmail().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(UPayGatePaymentGateway.class.getName(),"validateForSale()",null,"Common","Email not provided", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Email not provided for the transaction ",new Throwable("Email not provided for the transaction"));
            }
        }
        else
        {
            PZExceptionHandler.raiseConstraintViolationException(UPayGatePaymentGateway.class.getName(),"validateForSale()",null,"Common","Address Details not provided", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Address Details not provided for the transaction",new Throwable("Address Details not provided for the transaction"));
        }
        if(genericCardDetailsVO != null)
        {
            if(genericCardDetailsVO.getCardNum()==null || genericCardDetailsVO.getCardNum().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(UPayGatePaymentGateway.class.getName(),"validateForSale()",null,"Common","Card NO not provided", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Card NO not provided for the transaction",new Throwable("Card NO not provided for the transaction"));
            }
            if(genericCardDetailsVO.getExpMonth()==null || genericCardDetailsVO.getExpMonth().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(UPayGatePaymentGateway.class.getName(),"validateForSale()",null,"Common","Expiry Month not provided", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Expiry Month not provided for the transaction",new Throwable("Expiry Month not provided for the transaction"));
            }
            if(genericCardDetailsVO.getExpYear()==null || genericCardDetailsVO.getExpYear().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(UPayGatePaymentGateway.class.getName(),"validateForSale()",null,"Common","Expiry Year not provided", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Expiry Year not provided for the transaction",new Throwable("Expiry Year not provided for the transaction"));
            }
            if(genericCardDetailsVO.getcVV()==null || genericCardDetailsVO.getcVV().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(UPayGatePaymentGateway.class.getName(),"validateForSale()",null,"Common","CVV not provided", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"CVV not provided for the transaction",new Throwable("Amount not provided for the transaction"));
            }
        }
        else
        {
            PZExceptionHandler.raiseConstraintViolationException(UPayGatePaymentGateway.class.getName(),"validateForSale()",null,"Common","Card Details not provided", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Card Details not provided for the transaction",new Throwable("Card Details not provided for the transaction"));
        }

        if(trackingId==null || trackingId.equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(UPayGatePaymentGateway.class.getName(),"validateForSale()",null,"Common","Tracking Id not provided", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracking Id not provided for the transaction",new Throwable("Tracking Id not provided for the transaction"));
        }
    }

}
