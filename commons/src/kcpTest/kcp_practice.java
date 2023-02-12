/*
package kcpTest;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.common.core.Comm3DRequestVO;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommRequestVO;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.kcp.KCPPaymentGateway;

import java.util.ResourceBundle;

*/
/**
 * Created by Admin on 3/16/2020.
 *//*

public class kcp_practice extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE= "kcp";  //to select gateway
    private static TransactionLogger transactionLogger = new TransactionLogger(KCPPaymentGateway.class.getName());//transactionlogger is to print
    final static ResourceBundle RB = LoadProperties.getProperty(com.directi.pg.kcp); //related to properties and url
    public KCPPaymentGateway (String accountId)
    {
        this.accountId=accountId;
    }
    public GenericResponseVO processSale(String trackingID,GenericResponseVO requestVO)throws PZTechnicalViolationException// this sale method contains parameters for tracking id and genereicresponseVO
    {
        CommRequestVO commRequestVO =(CommRequestVO)requestVO;//requestVO is typecasted
        Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();//CONC
        Functions functions = new Functions();

    }
}
*/
