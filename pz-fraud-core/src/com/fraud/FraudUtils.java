package com.fraud;

import com.directi.pg.Database;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.fraud.at.ATTransactionStatus;
import com.fraud.fourstop.FourStopTransactionStatus;
import com.payment.PZTransactionStatus;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;

import java.io.PrintWriter;


/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 9/17/14
 * Time: 6:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class FraudUtils
{
    private final Logger logger = ESAPI.getLogger("FraudUtils");
    public String getTableNameFromAccountId(String accountId)
    {
        GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
        GatewayType gatewayType = GatewayTypeService.getGatewayType(account.getPgTypeId());
        String tableName = Database.getTableName(gatewayType.getGateway());
        return tableName;
    }
    public PZTransactionStatus getPZTransactionStatus(String status)
    {

        PZTransactionStatus pzTransactionStatus = null;

        if(PZTransactionStatus.AUTH_FAILED.toString().equals(status))
        {
            pzTransactionStatus = PZTransactionStatus.AUTH_FAILED;
        }
        else if (PZTransactionStatus.SETTLED.toString().equals(status))
        {
            pzTransactionStatus = PZTransactionStatus.SETTLED;
        }
        else if(PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(status))
        {
            pzTransactionStatus = PZTransactionStatus.CAPTURE_SUCCESS;
        }
        else if(PZTransactionStatus.CHARGEBACK.toString().equals(status))
        {
            pzTransactionStatus = PZTransactionStatus.CHARGEBACK;
        }
        else if(PZTransactionStatus.REVERSED.toString().equals(status))
        {
            pzTransactionStatus = PZTransactionStatus.REVERSED;
        }
        return  pzTransactionStatus;

    }
    public String getATStatus(PZTransactionStatus status)
    {
        String atStatus=null;
        switch(status)
        {
            /*case PZTransactionStatus.BEGUN:
                atStatus = ATTransactionStatus.PASS_VALIDATION.toString();
                break;

            case PZTransactionStatus.AUTH_STARTED:
                atStatus = ATTransactionStatus.PASS_VALIDATION.toString();
                break;

            case PZTransactionStatus.PROOF_REQUIRED:
                atStatus = ATTransactionStatus.PENDING.toString();
                break;

            case PZTransactionStatus.AUTH_SUCCESS:
                atStatus = ATTransactionStatus.APPROVED.toString();
                break;

            case PZTransactionStatus.AUTH_FAILED:
                atStatus = ATTransactionStatus.DECLINED_BY_BANK_GATWAY.toString();
                break;

            case PZTransactionStatus.AUTH_CANCELLED:
                atStatus = ATTransactionStatus.ABANDON.toString();
                break;

            case PZTransactionStatus.CAPTURE_STARTED:
                atStatus = ATTransactionStatus.PENDING.toString();
                break;

            case PZTransactionStatus.CAPTURE_SUCCESS:
                atStatus = ATTransactionStatus.APPROVED.toString();
                break;

            case PZTransactionStatus.CAPTURE_FAILED:
                atStatus = ATTransactionStatus.DECLINED_BY_BANK_GATWAY.toString();
                break;

            case PZTransactionStatus.SETTLED:
                atStatus = ATTransactionStatus.SETTLED.toString();
                break;

            case PZTransactionStatus.MARKED_FOR_REVERSAL:
                atStatus = ATTransactionStatus.REFUND.toString();
                break;

            case PZTransactionStatus.REVERSED:
                atStatus = ATTransactionStatus.REFUND.toString();
                break;

            case PZTransactionStatus.FAILED:
                atStatus = ATTransactionStatus.FAILED_VALIDATION.toString();
                break;

            case PZTransactionStatus.CHARGEBACK:
                atStatus = ATTransactionStatus.CHARGEBACK.toString();
                break;

            case PZTransactionStatus.RETRIEVAL_REQUEST:
                atStatus = ATTransactionStatus.UNDEFINED.toString();
                break;*/

            default:
                break;
        }
        return atStatus;
    }
    public String getFourStopStatus(PZTransactionStatus status)
    {
        String fourstopStatus = null;
        switch(status)
        {
            /*case PZTransactionStatus.BEGUN:
                fourstopStatus = FourStopTransactionStatus.PASS_VALIDATION.toString();
                break;

            case PZTransactionStatus.AUTH_STARTED:
                fourstopStatus = FourStopTransactionStatus.PASS_VALIDATION.toString();
                break;

            case PZTransactionStatus.PROOF_REQUIRED:
                fourstopStatus = FourStopTransactionStatus.PENDING.toString();
                break;

            case PZTransactionStatus.AUTH_SUCCESS:
                fourstopStatus = FourStopTransactionStatus.APPROVED.toString();
                break;

            case PZTransactionStatus.AUTH_FAILED:
                fourstopStatus = FourStopTransactionStatus.DECLINED_BY_BANK_GATWAY.toString();
                break;

            case PZTransactionStatus.AUTH_CANCELLED:
                fourstopStatus = FourStopTransactionStatus.ABANDON.toString();
                break;

            case PZTransactionStatus.CAPTURE_STARTED:
                fourstopStatus = FourStopTransactionStatus.PENDING.toString();
                break;

            case PZTransactionStatus.CAPTURE_SUCCESS:
                fourstopStatus = FourStopTransactionStatus.APPROVED.toString();
                break;

            case PZTransactionStatus.CAPTURE_FAILED:
                fourstopStatus = FourStopTransactionStatus.DECLINED_BY_BANK_GATWAY.toString();
                break;

            case PZTransactionStatus.SETTLED:
                fourstopStatus = FourStopTransactionStatus.SETTLED.toString();
                break;

            case PZTransactionStatus.MARKED_FOR_REVERSAL:
                fourstopStatus = FourStopTransactionStatus.REFUND.toString();
                break;

            case PZTransactionStatus.REVERSED:
                fourstopStatus = FourStopTransactionStatus.REFUND.toString();
                break;

            case PZTransactionStatus.FAILED:
                fourstopStatus = FourStopTransactionStatus.FAILED_VALIDATION.toString();
                break;

            case PZTransactionStatus.CHARGEBACK:
                fourstopStatus = FourStopTransactionStatus.CHARGEBACK.toString();
                break;

            case PZTransactionStatus.RETRIEVAL_REQUEST:
                fourstopStatus = FourStopTransactionStatus.UNDEFINED.toString();
                break;*/

            default:
                break;
        }
        return fourstopStatus;
    }
    public void writeResponseForNewTransaction(PrintWriter pWriter, String pzFraudTransId,String status, String statusMsg,String recommendation,String score)
    {
        pWriter.write(pzFraudTransId+ ":" +status + ":" +statusMsg+":"+recommendation+":"+score+":");
        //System.out.println(pzFraudTransId+ ":" +status + ":" +statusMsg+":"+recommendation+":"+score+":");
    }
    public void writeResponseForUpdateTransaction(PrintWriter pWriter,String pztransid, String status,String description)
    {
        pWriter.write(pztransid + ":" +status+":"+description);
        //System.out.println(pztransid + ":" +status+":"+description);
    }



}

