package com.directi.pg.core.paymentgateway;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.*;
import com.payment.common.core.*;


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
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.ipaydna.core.message.*;

import com.payment.ipaydna.core.message.aquarius.gatedna.process.acquirer.WebservicesaleCfcSoapBindingStub;
import com.payment.ipaydna.core.message.aquarius.gatedna.process.acquirer.WebservicesaleServiceLocator;
import com.payment.ipaydna.core.message.coldfusion.xml.rpc.CFCInvocationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;

import java.io.IOException;
import java.io.StringReader;

import java.rmi.RemoteException;
import java.util.Hashtable;


/*
*
 * Created by IntelliJ IDEA.
 * User: saurabh.b
 * Date: 12/7/13
 * Time: 4:29 PM
 * To change this template use File | Settings | File Templates.
*/
public  class IPAYDNAPaymentGateway extends AbstractPaymentGateway
{
    private static Logger log = new Logger(IPAYDNAPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(IPAYDNAPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE = "iPayDNA";


    public IPAYDNAPaymentGateway(String accountId)

    {
        this.accountId = accountId;
    }



    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
      //  wddxPacket res;
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        IPayDNAResponseVO iPayDNAresVO=new IPayDNAResponseVO();
        java.lang.String response = null;
        Hashtable hashResponse=null;

        try
        {
            validateForSale(trackingID, requestVO);
            GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();

            CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();

            CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
            com.payment.ipaydna.core.message.aquarius.gatedna.process.acquirer.WebservicesaleCfcSoapBindingStub binding;

                binding = (com.payment.ipaydna.core.message.aquarius.gatedna.process.acquirer.WebservicesaleCfcSoapBindingStub)new com.payment.ipaydna.core.message.aquarius.gatedna.process.acquirer.WebservicesaleServiceLocator().getWebservicesaleCfc();



            // Time out after a minute
            binding.setTimeout(60000);


                String name =addressDetailsVO.getFirstname()+" "+addressDetailsVO.getLastname();
                response=binding.payment(GatewayAccountService.getGatewayAccount(accountId).getMerchantId(),trackingID,transDetailsVO.getOrderDesc(),transDetailsVO.getCurrency(),transDetailsVO.getAmount(),"0.00","0.00","0.00",name,cardDetailsVO.getCardNum(),Functions.getCardType(cardDetailsVO.getCardNum()),cardDetailsVO.getcVV(),cardDetailsVO.getExpMonth(),cardDetailsVO.getExpYear(),"0","0","",addressDetailsVO.getFirstname(),addressDetailsVO.getLastname(),"",addressDetailsVO.getStreet(),addressDetailsVO.getCity(),addressDetailsVO.getState(),addressDetailsVO.getZipCode(),addressDetailsVO.getCountry(),addressDetailsVO.getEmail(),addressDetailsVO.getPhone(),"","","","","","","",addressDetailsVO.getIp());

                log.debug(response);
                transactionLogger.debug(response);

            hashResponse= trick(response);
            if(response!=null)
            {

                iPayDNAresVO.setINVOICENO((String) hashResponse.get("INVOICENO"));   //    transaction_ipaydna_details
                iPayDNAresVO.setErrorCode((String)hashResponse.get("ERRORCODE"));
                iPayDNAresVO.setDescription((String)hashResponse.get("ERRORMESSAGE")) ;
                iPayDNAresVO.setCurrencyText((String)hashResponse.get("CURRENCYTEXT"));
                iPayDNAresVO.setAuthorizationCode((String)hashResponse.get("AUTHORIZATIONCODE"));      // transaction_ipaydna_details
                iPayDNAresVO.setMerchantOrderId((String)hashResponse.get("ORDERDESCRIPTION"));
                iPayDNAresVO.setAmount((String)hashResponse.get("AMOUNT"));
                iPayDNAresVO.setTransactionType((String)hashResponse.get("TRANSACTIONTYPETEXT"));
                iPayDNAresVO.setBatchID((String)hashResponse.get("BATCHID"));                        //     transaction_ipaydna_details
                iPayDNAresVO.setDescriptor((String)hashResponse.get("RESPONSECODE"));
                iPayDNAresVO.setAVSResponse((String)hashResponse.get("AVSRESPONSE"));           //    transaction_ipaydna_details
                iPayDNAresVO.setMerchantId((String)hashResponse.get("CUSTOMERPAYMENTPAGETEXT"));
                iPayDNAresVO.setTransactionStatus((String)hashResponse.get("ERRORMESSAGE"));
                iPayDNAresVO.setTransactionId((String)hashResponse.get("ORDERREFERENCE"));
                iPayDNAresVO.setResponseTime((String)hashResponse.get("TRANSACTIONDATE"));
                if(((String)hashResponse.get("TRANSACTIONSTATUSTEXT")).equalsIgnoreCase("DECLINED"))
                {
                    iPayDNAresVO.setStatus("fail");
                }
                else
                {
                    iPayDNAresVO.setStatus("success");
                }
            }
            else
            {
                iPayDNAresVO.setINVOICENO((String) hashResponse.get("INVOICENO"));
                iPayDNAresVO.setErrorCode((String)hashResponse.get("ERRORCODE"));
                iPayDNAresVO.setDescription("Connection with Bank couldn't be established") ;
                iPayDNAresVO.setCurrencyText(transDetailsVO.getCurrency());
                iPayDNAresVO.setMerchantOrderId(trackingID);
                iPayDNAresVO.setAmount(transDetailsVO.getAmount());
                iPayDNAresVO.setTransactionType("SALE");
                iPayDNAresVO.setMerchantId(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
                iPayDNAresVO.setTransactionStatus("Connection with Bank couldn't be established");
                iPayDNAresVO.setStatus("fail");
            }
        }
        catch (ServiceException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(IPAYDNAPaymentGateway.class.getName(),"processSale()",null,"common","Technical Exception while placing transaction via IPAYDNA", PZTechnicalExceptionEnum.SERVICE_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (CFCInvocationException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(IPAYDNAPaymentGateway.class.getName(),"processSale()",null,"common","Technical Exception while placing transaction via IPAYDNA", PZTechnicalExceptionEnum.INVOCATION_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (RemoteException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(IPAYDNAPaymentGateway.class.getName(),"processSale()",null,"common","Technical Exception while placing transaction via IPAYDNA", PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        return iPayDNAresVO;
    }




    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        IPayDNAResponseVO iPayDNAresVO=new IPayDNAResponseVO();
        java.lang.String refundresponse = null;
        Hashtable hashResponse=null;

        try
        {
            validateForRefund(trackingID, requestVO);
            CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
            GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();

            com.payment.ipaydna.core.message.ipayRefund.aquarius.gatedna.process.acquirer.WebservicerefundCfcSoapBindingStub binding;

                binding = (com.payment.ipaydna.core.message.ipayRefund.aquarius.gatedna.process.acquirer.WebservicerefundCfcSoapBindingStub)
                        new com.payment.ipaydna.core.message.ipayRefund.aquarius.gatedna.process.acquirer.WebservicerefundServiceLocator().getWebservicerefundCfc();




            // Time out after a minute
            binding.setTimeout(60000);



                log.debug(transDetailsVO.getPreviousTransactionId());
                refundresponse = binding.refund(GatewayAccountService.getGatewayAccount(accountId).getMerchantId(),trackingID,transDetailsVO.getPreviousTransactionId(),"     ",addressDetailsVO.getIp());
                log.debug(refundresponse);



            hashResponse= trick(refundresponse);
            if(refundresponse!=null)
            {
                iPayDNAresVO.setMerchantId((String)hashResponse.get("CUSTOMERPAYMENTPAGETEXT"));
                iPayDNAresVO.setTransactionId((String)hashResponse.get("ORDERREFERENCE"));
                iPayDNAresVO.setMerchantOrderId((String)hashResponse.get("ORDERDESCRIPTION"));
                iPayDNAresVO.setReferralOrderReference((String)hashResponse.get("REFERRALORDERREFERENCE")); //
                iPayDNAresVO.setResponseTime((String)hashResponse.get("TRANSACTIONDATE"));
                iPayDNAresVO.setTransactionType((String)hashResponse.get("TRANSACTIONTYPETEXT"));
                iPayDNAresVO.setAuthorizationCode((String)hashResponse.get("AUTHORIZATIONCODE"));
                iPayDNAresVO.setDescriptor((String)hashResponse.get("RESPONSECODE"));
                iPayDNAresVO.setINVOICENO((String)hashResponse.get("ORDERID"));
                iPayDNAresVO.setBatchID((String)hashResponse.get("BATCHID"));
                iPayDNAresVO.setErrorCode((String)hashResponse.get("ERRORCODE"));
                iPayDNAresVO.setDescription((String)hashResponse.get("ERRORMESSAGE")) ;
                if(((String)hashResponse.get("TRANSACTIONSTATUSTEXT")).equalsIgnoreCase("DECLINED"))
                {
                    iPayDNAresVO.setStatus("fail");
                }
                else
                {
                    iPayDNAresVO.setStatus("success");
                }
            }

            else
            {
                iPayDNAresVO.setINVOICENO((String) hashResponse.get("INVOICENO"));
                iPayDNAresVO.setErrorCode((String)hashResponse.get("ERRORCODE"));
                iPayDNAresVO.setDescription("Connection with Bank couldn't be established") ;
                iPayDNAresVO.setMerchantOrderId(trackingID);
                iPayDNAresVO.setTransactionType("REFUND");
                iPayDNAresVO.setMerchantId(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
                iPayDNAresVO.setStatus("fail");

            }


        }
        catch (ServiceException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(IPAYDNAPaymentGateway.class.getName(),"processRefund()",null,"common","Technical Exception while Refunding the transaction via IPAY DNA",PZTechnicalExceptionEnum.SERVICE_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (com.payment.ipaydna.core.message.ipayRefund.coldfusion.xml.rpc.CFCInvocationException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(IPAYDNAPaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception while Refunding the transaction via IPAY DNA", PZTechnicalExceptionEnum.INVOCATION_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (RemoteException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(IPAYDNAPaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception while Refunding the transaction via IPAY DNA", PZTechnicalExceptionEnum.INVOCATION_EXCEPTION,null, e.getMessage(), e.getCause());
        }

        return iPayDNAresVO;
    }

    @Override
    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
       // CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        IPayDNAResponseVO iPayDNAresVO=new IPayDNAResponseVO();
        java.lang.String queryresponse = null;
        Hashtable hashResponse=null;

        try
        {

            validateForQuery(trackingID, requestVO);
            com.payment.ipaydna.core.message.ipayInquiry.aquarius.gatedna.process.acquirer.TransactionQueryCfcSoapBindingStub binding;

                binding = (com.payment.ipaydna.core.message.ipayInquiry.aquarius.gatedna.process.acquirer.TransactionQueryCfcSoapBindingStub)
                        new com.payment.ipaydna.core.message.ipayInquiry.aquarius.gatedna.process.acquirer.TransactionQueryServiceLocator().getTransactionQueryCfc();




            // Time out after a minute
            binding.setTimeout(60000);




                queryresponse = binding.getRecord(GatewayAccountService.getGatewayAccount(accountId).getMerchantId(),"",trackingID);

            // TBD - validate results
            //System.out.println();
            hashResponse= trick(queryresponse);
            if(queryresponse!=null)
            {
                iPayDNAresVO.setAmount((String)hashResponse.get("AMOUNT"));
                iPayDNAresVO.setAuthorizationCode((String)hashResponse.get("AUTHORIZATIONCODE"));
                iPayDNAresVO.setCurrencyText((String)hashResponse.get("CURRENCYTEXT"));
                iPayDNAresVO.setMerchantId((String)hashResponse.get("CUSTOMERPAYMENTPAGETEXT"));
                iPayDNAresVO.setErrorCode((String)hashResponse.get("ERRORCODE"));
                iPayDNAresVO.setDescription((String)hashResponse.get("ERRORMESSAGE")) ;
                iPayDNAresVO.setMerchantOrderId((String)hashResponse.get("ORDERDESCRIPTION"));
                iPayDNAresVO.setTransactionId((String)hashResponse.get("ORDERREFERENCE"));
                iPayDNAresVO.setDescriptor((String)hashResponse.get("RESPONSECODE"));
                iPayDNAresVO.setSettlementDate((String)hashResponse.get("SETTLEMENTDATE"));
                iPayDNAresVO.setSettlementStatusText((String)hashResponse.get("SETTLEMENTSTATUSTEXT"));
                iPayDNAresVO.setResponseTime((String)hashResponse.get("TRANSACTIONDATE"));
                iPayDNAresVO.setTransactionType((String)hashResponse.get("TRANSACTIONTYPETEXT"));

                 if(((String)hashResponse.get("TRANSACTIONSTATUSTEXT")).equalsIgnoreCase("DECLINED"))
                {
                    iPayDNAresVO.setStatus("fail");
                }
                else
                {
                    iPayDNAresVO.setStatus("success");
                }
            }

            else
            {

                iPayDNAresVO.setDescription("Connection with Bank couldn't be established") ;
                iPayDNAresVO.setMerchantOrderId(trackingID);
                iPayDNAresVO.setTransactionType("REFUND");
                iPayDNAresVO.setMerchantId(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
                iPayDNAresVO.setStatus("fail");

            }


        }
        catch (ServiceException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(IPAYDNAPaymentGateway.class.getName(),"processQuery()",null,"common","Technical Exception while Querying transaction via IPay DNA",PZTechnicalExceptionEnum.SERVICE_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (com.payment.ipaydna.core.message.ipayInquiry.coldfusion.xml.rpc.CFCInvocationException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(IPAYDNAPaymentGateway.class.getName(), "processQuery()", null, "common", "Technical Exception while Querying transaction via IPay DNA", PZTechnicalExceptionEnum.INVOCATION_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (RemoteException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(IPAYDNAPaymentGateway.class.getName(), "processQuery()", null, "common", "Technical Exception while Querying transaction via IPay DNA", PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        return iPayDNAresVO;
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW));
        PZGenericConstraint genConstraint = new PZGenericConstraint("IPAYDNAPaymentGateway","processRebilling",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }

    private WebservicesaleCfcSoapBindingStub getCommBindingStub() throws SystemError
    {
        WebservicesaleCfcSoapBindingStub webservicesaleCfcSoapBindingStub;

        try
        {
            webservicesaleCfcSoapBindingStub = (WebservicesaleCfcSoapBindingStub) new WebservicesaleServiceLocator().getWebservicesaleCfc();
            return webservicesaleCfcSoapBindingStub;
        }
        catch (ServiceException e)
        {
            throw new SystemError("Unable to get binding stub for Comm");
        }

    }


    public Hashtable trick(String xml) throws PZTechnicalViolationException

    {


        Hashtable res=new Hashtable();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        DocumentBuilder db = null;

        Document document=null;

        try

        {

            db = dbf.newDocumentBuilder();

            document = db.parse(new InputSource(new StringReader(xml)));

        }

        catch (ParserConfigurationException e)

        {
            PZExceptionHandler.raiseTechnicalViolationException(IPAYDNAPaymentGateway.class.getName(),"trick()",null,"common","Parse Configuration Exception while parsing response of IPAY DNA",PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION,null,e.getMessage(),e.getCause());
        }

        catch (SAXException e)

        {
            PZExceptionHandler.raiseTechnicalViolationException(IPAYDNAPaymentGateway.class.getName(),"trick()",null,"common","SAX Exception while parsing response of IPAY DNA",PZTechnicalExceptionEnum.SAXEXCEPTION,null,e.getMessage(),e.getCause());
        }

        catch (IOException e)

        {
            PZExceptionHandler.raiseTechnicalViolationException(IPAYDNAPaymentGateway.class.getName(),"trick()",null,"common","IO Exception while parsing response of IPAY DNA",PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
        }

        NodeList nodeList = document.getElementsByTagName("var");

        //NodeList nodeStrList = document.getElementsByTagName("string");

        Node nNode = null;

        for(int x=0,size= nodeList.getLength(); x<size; x++)
        {
            nNode = nodeList.item(x);

            log.debug(nNode.getAttributes().getNamedItem("name").getNodeValue());

            NodeList vChildNodeList = nNode.getChildNodes();

            Node vChildNode = vChildNodeList.item(0);

            Node vSubChildNode = vChildNode.getFirstChild();
            String strvalue=null;
            if(vSubChildNode==null)
            {
                strvalue=" ";
            }
            else
            {
                strvalue=vSubChildNode.getNodeValue();
            }
            res.put(nNode.getAttributes().getNamedItem("name").getNodeValue(),strvalue);
        }
        return res;

    }
      //VALIDATION METHODS

    private void validateForSale(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if(trackingID ==null || trackingID.equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TRACKINGID);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(IPAYDNAPaymentGateway.class.getName(),"validateForSale()",null,"common","Tracking Id not provided while placing transaction via IPayDNA", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Tracking Id not provided while placing transaction via IPayDNA",new Throwable("Tracking Id not provided while placing transaction via IPayDNA"));
        }

        if(requestVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(IPAYDNAPaymentGateway.class.getName(),"validateForSale()",null,"common","Request  not provided while placing transaction via IPayDNA", PZConstraintExceptionEnum.VO_MISSING,errorCodeListVO,"Request  not provided while placing transaction via IPayDNA",new Throwable("Request  not provided while placing transaction via IPayDNA"));
        }

        CommRequestVO  commRequestVO  =    (CommRequestVO)requestVO;
        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        if(transDetailsVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(IPAYDNAPaymentGateway.class.getName(),"validateForSale()",null,"common","TransactionDetails  not provided while placing transaction via IPayDNA", PZConstraintExceptionEnum.VO_MISSING,errorCodeListVO,"TransactionDetails  not provided while placing transaction via IPayDNA",new Throwable("TransactionDetails  not provided while placing transaction via IPayDNA"));
        }
        if(transDetailsVO.getAmount() == null || transDetailsVO.getAmount().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_AMOUNT);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(IPAYDNAPaymentGateway.class.getName(),"validateForSale()",null,"common","Amount not provided while placing transaction via IPayDNA", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Amount not provided while placing transaction via IPayDNA",new Throwable("Amount not provided while placing transaction via IPayDNA"));
        }
        if(transDetailsVO.getCurrency() == null || transDetailsVO.getCurrency().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CURRENCY);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(IPAYDNAPaymentGateway.class.getName(),"validateForSale()",null,"common","Currency not provided while placing transaction via IPayDNA", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Currency not provided while placing transaction via IPayDNA",new Throwable("Currency not provided while placing transaction via IPayDNA"));
        }

        CommAddressDetailsVO addressDetailsVO= commRequestVO.getAddressDetailsVO();

        if(addressDetailsVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(IPAYDNAPaymentGateway.class.getName(),"validateForSale()",null,"common","AddressDetails  not provided while placing transaction via IPayDNA", PZConstraintExceptionEnum.VO_MISSING,errorCodeListVO,"AddressDetails  not provided while placing transaction via IPayDNA",new Throwable("AddressDetails  not provided while placing transaction via IPayDNA"));
        }
        //User Details
        if(addressDetailsVO.getFirstname()==null|| addressDetailsVO.getFirstname().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_FIRSTNAME);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(IPAYDNAPaymentGateway.class.getName(),"validateForSale()",null,"common","First Name not provided while placing transaction via IPayDNA", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"First Name not provided while placing transaction via IPayDNA",new Throwable("First Name not provided while placing transaction via IPayDNA"));

        }
        if(addressDetailsVO.getLastname()==null|| addressDetailsVO.getLastname().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LASTNAME);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(IPAYDNAPaymentGateway.class.getName(),"validateForSale()",null,"common","Last Name not provided while placing transaction via IPayDNA", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Last Name not provided while placing transaction via IPayDNA",new Throwable("Last Name not provided while placing transaction via IPayDNA"));

        }



        if(addressDetailsVO.getIp()==null || addressDetailsVO.getIp().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_IPADDRESS);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(IPAYDNAPaymentGateway.class.getName(),"validateForSale()",null,"common","IP Address not provided while placing transaction via IPayDNA", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"IP Address not provided while placing transaction via IPayDNA",new Throwable("IP Address not provided while placing transaction via IPayDNA"));
        }

        //Address Details
        if(addressDetailsVO.getStreet()==null || addressDetailsVO.getStreet().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STREET);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(IPAYDNAPaymentGateway.class.getName(),"validateForSale()",null,"common","Street not provided while placing transaction via IPayDNA", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Street not provided while placing transaction via IPayDNA",new Throwable("Street not provided while placing transaction via IPayDNA"));
        }

        if(addressDetailsVO.getCity()==null || addressDetailsVO.getCity().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CITY);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(IPAYDNAPaymentGateway.class.getName(),"validateForSale()",null,"common","City not provided while placing transaction via IPayDNA", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"City not provided while placing transaction via IPayDNA",new Throwable("City not provided while placing transaction via IPayDNA"));
        }
        if(addressDetailsVO.getCountry()==null || addressDetailsVO.getCountry().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_COUNTRY_CODE);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(IPAYDNAPaymentGateway.class.getName(),"validateForSale()",null,"common","Country not provided while placing transaction via IPayDNA", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Country not provided while placing transaction via IPayDNA",new Throwable("Country not provided while placing transaction via IPayDNA"));
        }

        if(addressDetailsVO.getState()==null || addressDetailsVO.getState().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STATE);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(IPAYDNAPaymentGateway.class.getName(),"validateForSale()",null,"common","State not provided while placing transaction via IPayDNA", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"State not provided while placing transaction via IPayDNA",new Throwable("State not provided while placing transaction via IPayDNA"));
        }

        if(addressDetailsVO.getZipCode()==null || addressDetailsVO.getZipCode().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ZIP);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(IPAYDNAPaymentGateway.class.getName(),"validateForSale()",null,"common","Zip Code not provided while placing transaction via IPayDNA", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Zip Code not provided while placing transaction via IPayDNA",new Throwable("Zip Code not provided while placing transaction via IPayDNA"));
        }


        GenericCardDetailsVO cardDetailsVO= commRequestVO.getCardDetailsVO();

        if(cardDetailsVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(IPAYDNAPaymentGateway.class.getName(),"validateForSale()",null,"common","CardDetails  not provided while placing transaction via IPayDNA", PZConstraintExceptionEnum.VO_MISSING,errorCodeListVO,"CardDetails  not provided while placing transaction via IPayDNA",new Throwable("CardDetails  not provided while placing transaction via IPayDNA"));
        }
        //Card Details

        if(cardDetailsVO.getCardNum()==null || cardDetailsVO.getCardNum().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(IPAYDNAPaymentGateway.class.getName(),"validateForSale()",null,"common","Card NO not provided while placing transaction via IPayDNA", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Card NO not provided while placing transaction via IPayDNA",new Throwable("Card NO not provided while placing transaction via IPayDNA"));
        }

        String ccnum = cardDetailsVO.getCardNum();

        if(ccnum!=null && !Functions.isValid(ccnum))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(IPAYDNAPaymentGateway.class.getName(),"validateForSale()",null,"common","Card NO  provided is invalid while placing transaction via IPayDNA", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Card NO provided is invalid while placing transaction via IPayDNA",new Throwable("Card NO provided is invalid while placing transaction via IPayDNA"));
        }

        if(cardDetailsVO.getcVV()==null || cardDetailsVO.getcVV().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CVV);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(IPAYDNAPaymentGateway.class.getName(),"validateForSale()",null,"common","CVV not provided while placing transaction via IPayDNA", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"CVV not provided while placing transaction via IPayDNA",new Throwable("CVV not provided while placing transaction via IPayDNA"));
        }
        if(cardDetailsVO.getExpMonth()==null || cardDetailsVO.getExpMonth().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EXP_MONTH);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(IPAYDNAPaymentGateway.class.getName(),"validateForSale()",null,"common","Expiry Month not provided while placing transaction via IPayDNA", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Expiry Month not provided while placing transaction via IPayDNA",new Throwable("Expiry Month not provided while placing transaction via IPayDNA"));
        }
        if(cardDetailsVO.getExpYear()==null || cardDetailsVO.getExpYear().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EXP_YEAR);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(IPAYDNAPaymentGateway.class.getName(),"validateForSale()",null,"common","Expiry Year not provided while placing transaction via IPayDNA", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Expiry Year not provided while placing transaction via IPayDNA",new Throwable("Expiry Year not provided while placing transaction via IPayDNA"));
        }


    }
    private void validateForRefund(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        if(trackingID ==null || trackingID.equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(IPAYDNAPaymentGateway.class.getName(),"validateForRefund()",null,"common","Tracking Id is not provided while refunding the transaction via IPAYDNA",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracking Id is not provided while refunding the transaction via IPAYDNA",new Throwable("Tracking Id is not provided while refunding the transaction via IPAYDNA"));
        }

        if(requestVO ==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(IPAYDNAPaymentGateway.class.getName(),"validateForRefund()",null,"common","Request  is not provided while refunding the transaction via IPAYDNA",PZConstraintExceptionEnum.VO_MISSING,null,"Request Vo is not provided while refunding the transaction via IPAYDNA",new Throwable("Request  is not provided while refunding the transaction via IPAYDNA"));
        }
        if(GatewayAccountService.getGatewayAccount(accountId).getMerchantId() == null || GatewayAccountService.getGatewayAccount(accountId).getMerchantId().equals(""))
        {
            PZExceptionHandler.raiseTechnicalViolationException(IPAYDNAPaymentGateway.class.getName(), "validateForRefund()", null, "common", "Merchant Id is not Configured while refunding the transaction via IPAYDNA", PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null, "Merchant Id is not Configured while refunding the transaction via IPAYDNA", new Throwable("Merchant Id is not Configured while refunding the transaction via IPAYDNA"));
        }

        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        if(transDetailsVO.getPreviousTransactionId() == null || transDetailsVO.getPreviousTransactionId().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(IPAYDNAPaymentGateway.class.getName(),"validateForRefund()",null,"common","Previous Transaction Id is not provided while refunding the transaction via IPAYDNA",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Previous Transaction Id is not provided while refunding the transaction via IPAYDNA",new Throwable("Previous Transaction Id is not provided while refunding the transaction via IPAYDNA"));
        }

        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        if(addressDetailsVO.getIp()==null || addressDetailsVO.getIp().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(IPAYDNAPaymentGateway.class.getName(),"validateForRefund()",null,"common","Ip Address is not provided while refunding the transaction via IPAYDNA",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"IP Address is not provided while refunding the transaction via IPAYDNA",new Throwable("IP Address is not provided while refunding the transaction via IPAYDNA"));
        }
    }

    private void validateForQuery(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {

        if(trackingID ==null || trackingID.equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(IPAYDNAPaymentGateway.class.getName(),"validateForQuery()",null,"common","Tracking Id not provided while Queriying transaction via IPAY DNA",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracking Id not provided while Queriying transaction via IPAY DNA",new Throwable("Tracking Id not provided while Queriying transaction via IPAY DNA"));
        }

        if(requestVO ==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(IPAYDNAPaymentGateway.class.getName(),"validateForQuery()",null,"common","Request  not provided while Queriying transaction via IPAY DNA",PZConstraintExceptionEnum.VO_MISSING,null,"Request  not provided while Queriying transaction via IPAY DNA",new Throwable("Request  not provided while Queriying transaction via IPAY DNA"));
        }
        if(GatewayAccountService.getGatewayAccount(accountId).getMerchantId() == null || GatewayAccountService.getGatewayAccount(accountId).getMerchantId().equals(""))
        {
            PZExceptionHandler.raiseTechnicalViolationException(IPAYDNAPaymentGateway.class.getName(), "validateForQuery()", null, "common", "Merchant Id not Configured while Queriying transaction via IPAY DNA", PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null, "Merchant Id not Configured while Queriying transaction via IPAY DNA", new Throwable("Nerchant Id not Configured while Queriying transaction via IPAY DNA"));
        }

    }


    @Override
    public String getMaxWaitDays()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
