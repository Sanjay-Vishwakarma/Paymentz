package com.payment.payGateway.core;

import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.payment.common.core.CommCardDetailsVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoap12Stub;
import com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoapStub;
import com.payment.payGateway.core.message.paygateway.process.TTransacResult;
import org.apache.axis.AxisFault;

import java.rmi.RemoteException;

import static junit.framework.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh Dani
 * Date: 12/18/13
 * Time: 7:07 PM
 * To change this template use File | Settings | File Templates.
 */


public class TestClass
{
    private static Logger logger=new Logger(TestClass.class.getName());
    public static void testTransaction()
    {
        CommTransactionDetailsVO  transactionDetailsVO=new CommTransactionDetailsVO();
        CommCardDetailsVO cardDetailsVO = new CommCardDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();

        String accountid = "pz_paygateway";
        String password = "HF624BNv";
        String sha = "1f48fd27e7b1d6615751c9f212bc3e9c7a2a650bde15d9a0b01a6706977fab49";
        String action_type = "payment";
        String account_gateway = "1";
        transactionDetailsVO.setOrderId("34587");
        addressDetailsVO.setEmail("jinesh.d@pz.com");
        addressDetailsVO.setFirstname("Jinesh");
        addressDetailsVO.setLastname("Dani");
        addressDetailsVO.setStreet("Ricardo Street");
        addressDetailsVO.setCity("San Francisco");
        addressDetailsVO.setZipCode("94115");
        addressDetailsVO.setState("CA");
        addressDetailsVO.setCountry("US");
        addressDetailsVO.setPhone("64-4844-865");
        transactionDetailsVO.setAmount("10.00");
        transactionDetailsVO.setCurrency("USD");
        cardDetailsVO.setCardType("visa");
        cardDetailsVO.setCardNum("44443333222211111");
        cardDetailsVO.setExpMonth("06");
        cardDetailsVO.setExpYear("2014");
        cardDetailsVO.setcVV("123");
        addressDetailsVO.setIp("122.169.97.70");

        com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoapStub binding;
        try {
            binding = (com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoapStub)
                    new com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceLocator().getPayGateway_ServiceSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            logger.error("ServiceException--->",jre.getLinkedCause());
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        //assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        com.payment.payGateway.core.message.paygateway.process.TTransacResult value = null;
        try
        {
            value = binding.processTx(accountid, password, sha, action_type, account_gateway, genericTransDetailsVO.getOrderId(), addressDetailsVO.getEmail(), addressDetailsVO.getLastname(), addressDetailsVO.getFirstname(), addressDetailsVO.getStreet(), addressDetailsVO.getCity(), addressDetailsVO.getZipCode(), addressDetailsVO.getState(), addressDetailsVO.getCountry(), addressDetailsVO.getPhone(), "", "", "", "", "", "", "", "", "", genericTransDetailsVO.getAmount(), genericTransDetailsVO.getCurrency(), cardDetailsVO.getCardType(), cardDetailsVO.getCardNum(), cardDetailsVO.getExpMonth(), cardDetailsVO.getExpYear(), cardDetailsVO.getcVV(), addressDetailsVO.getIp(), "", "", "", "", "");
            //System.out.println("req :"+ value);
        }
        catch (RemoteException e)
        {
            logger.error("RemoteException--->",e);  //To change body of catch statement use File | Settings | File Templates.
        }
        // TBD - validate results
    }

    public static GenericResponseVO processRefund() throws SystemError
    {
        //System.out.println("Process Refund");


        com.payment.payGateway.core.message.paygateway.process.TTransacResult res = null;
        //CommTransactionDetailsVO commTransactionDetailsVO=requestVO.getTransDetailsVO();
        CommResponseVO commResponseVO = new CommResponseVO();
        try
        {

            String amount = "20.00";
            String trans_id="T81312271643193";
            String accountId = "pz_paygateway";
            String password = "HF624BNv";
            String sha = "ee82712ed978d97a3035c2eb039e598734b4cd85b73e93f5405ed7c6705552a2";
            PayGateway_ServiceSoap12Stub binding = new PayGateway_ServiceSoap12Stub();
            try {
                binding = (com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoap12Stub)
                        new com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceLocator().getPayGateway_ServiceSoap12();
            }
            catch (javax.xml.rpc.ServiceException jre) {
                logger.error("ServiceException---->",jre.getLinkedCause());
                throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
            }
            assertNotNull("binding is null", binding);

            // Time out after a minute
            binding.setTimeout(60000);
            res = binding.processRefund(accountId, password, sha, trans_id, amount, "", "", "", "", "");
            /*System.out.println(res.getErrorsDescriptionCodes());
            System.out.println(res.getResp_action_type());
            System.out.println(res.getResp_trans_amount());
            System.out.println(res.getResp_sha());
            System.out.println(res.getResp_trans_description_status());
            System.out.println(res.getResp_trans_id());
            System.out.println(res.getResp_trans_status());*/
            commResponseVO.setTransactionId(res.getResp_trans_id());
            commResponseVO.setMerchantId(res.getResp_trans_merchant_id());
            commResponseVO.setAmount(res.getResp_trans_amount());
            commResponseVO.setTransactionType(res.getResp_action_type());
            commResponseVO.setErrorCode(res.getResp_trans_status());
            commResponseVO.setDescription(res.getResp_trans_detailled_status());
        }
        catch (RemoteException e)
        {
            logger.error("RemoteException---->", e);
        }
        return commResponseVO;
    }
    public static void main(String[] args) throws Exception
    {
        testTransaction();
        processRefund();
    }
}
