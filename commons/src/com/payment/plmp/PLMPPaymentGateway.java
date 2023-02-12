package com.payment.plmp;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;

import java.util.ResourceBundle;

/**
 * Created by Admin on 4/11/2019.
 */
public class PLMPPaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionLogger = new TransactionLogger(PLMPPaymentGateway.class.getName());

    //private static final ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.romcard");

    public static final String GATEWAY_TYPE = "plmp";

    public PLMPPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    @Override
    public String getMaxWaitDays() {   return null;   }

    public static void main(String[] args)
    {

    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processAuthentication-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        //Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String merchantWalletAddress = gatewayAccount.getFRAUD_FTP_PATH();
        String walletAddress[]=null;

        try
        {
            transactionLogger.error(" --- in auth tracking id ----"+trackingID);
            transactionLogger.error(" --- in auth amount ----"+commTransactionDetailsVO.getAmount());
            transactionLogger.error(" --- in auth currency ----"+commTransactionDetailsVO.getCurrency());

            transactionLogger.error(" --- in auth wallet currency ----"+commTransactionDetailsVO.getWalletCurrency());
            transactionLogger.error(" --- in auth wallet currency ----"+commTransactionDetailsVO.getWalletAmount());

            if(functions.isValueNull(merchantWalletAddress))
            {
                if (merchantWalletAddress.contains(","))
                {
                    walletAddress = merchantWalletAddress.split(",");
                    for (String s : walletAddress)
                    {
                        String val[] = null;
                        if (s.contains("="))
                        {
                            val = s.split("=");
                            if (commTransactionDetailsVO.getWalletCurrency().equalsIgnoreCase(val[0]))
                            {
                                merchantWalletAddress = val[1];
                                break;
                            }
                            else
                            {
                                merchantWalletAddress = "";
                            }
                        }
                    }
                }
                else
                {
                    String val[] = null;
                    if (merchantWalletAddress.contains("="))
                    {
                        val = merchantWalletAddress.split("=");
                        if (commTransactionDetailsVO.getWalletCurrency().equalsIgnoreCase(val[0]))
                        {
                            merchantWalletAddress = val[1];
                        }
                        else
                        {
                            merchantWalletAddress = "";
                        }
                    }
                }
            }
            else
            {
                merchantWalletAddress="";
            }


/*            commResponseVO.setAmount(commTransactionDetailsVO.getWalletAmount());
            commResponseVO.setCurrency(commTransactionDetailsVO.getWalletCurrency());
            commResponseVO.setTmpl_Currency(commTransactionDetailsVO.getCurrency());
            commResponseVO.setTmpl_Amount(commTransactionDetailsVO.getAmount());*/

            transactionLogger.error("In auth merchantWalletAddress = "+merchantWalletAddress);

            if(functions.isValueNull(merchantWalletAddress))
            {
                transactionLogger.error("in if wallet Address");
                commResponseVO.setStatus("success");
                commResponseVO.setWalletId(merchantWalletAddress);
            }
            else
            {
                transactionLogger.error("in else NO wallet Address");
                commResponseVO.setStatus("failed");
            }

            
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception-----", e);
        }

        return commResponseVO;

    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processSale-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        Functions functions = new Functions();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String merchantWalletAddress = gatewayAccount.getFRAUD_FTP_PATH();
        String walletAddress[]=null;


        try
        {
            if(functions.isValueNull(merchantWalletAddress))
            {
                if (merchantWalletAddress.contains(","))
                {
                    walletAddress = merchantWalletAddress.split(",");
                    for (String s : walletAddress)
                    {
                        String val[] = null;
                        if (s.contains("="))
                        {
                            val = s.split("=");
                            if (commTransactionDetailsVO.getWalletCurrency().equalsIgnoreCase(val[0]))
                            {
                                merchantWalletAddress = val[1];
                                break;
                            }
                            else
                            {
                                merchantWalletAddress = "";
                            }
                        }
                    }
                }
                else
                {
                    String val[] = null;
                    if (merchantWalletAddress.contains("="))
                    {
                        val = merchantWalletAddress.split("=");
                        if (commTransactionDetailsVO.getWalletCurrency().equalsIgnoreCase(val[0]))
                        {
                            merchantWalletAddress = val[1];
                        }
                        else
                        {
                            merchantWalletAddress = "";
                        }
                    }
                }
            }else {
                merchantWalletAddress="";
            }
            transactionLogger.error(" --- in sale currency ----"+commTransactionDetailsVO.getWalletCurrency());
            transactionLogger.error(" --- in sale currency ----"+commTransactionDetailsVO.getWalletAmount());

            /*commResponseVO.setAmount(commTransactionDetailsVO.getWalletAmount());
            commResponseVO.setCurrency(commTransactionDetailsVO.getWalletCurrency());
            commResponseVO.setTmpl_Currency(commTransactionDetailsVO.getWalletAmount());
            commResponseVO.setTmpl_Amount(commTransactionDetailsVO.getWalletCurrency());*/
            commResponseVO.setStatus("success");
            commResponseVO.setWalletId(merchantWalletAddress);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception-----", e);
        }


        return commResponseVO;
    }




}
