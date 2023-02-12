package com.directi.pg;

import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.payment.AbstractPaymentProcess;
import com.payment.PaymentProcessFactory;
import com.payment.arenaplus.core.ArenaPlusPaymentProcess;
import com.payment.borgun.core.BorgunPaymentProcess;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.request.PZSettlementFile;
import com.payment.response.PZSettlementRecord;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 12/13/13
 * Time: 3:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommonSettlement
{
    private static Logger log = new Logger(CommonSettlement.class.getName());

    public String processSettlement(String fileName, String fullFileName, String accountid)throws SystemError
    {

        PZSettlementFile PZSettlementFile = new PZSettlementFile();

        List<PZSettlementRecord> vTransactions = new ArrayList<PZSettlementRecord>();

        String str="";
        String val="";
        Connection con = null;
        TransactionEntry transactionEntry = null;
        String tableName="";

        AbstractPaymentProcess process = PaymentProcessFactory.getPaymentProcessInstance(null, Integer.parseInt(accountid));
        PZSettlementFile.setFilepath(fullFileName);
        PZSettlementFile.setAccountId(Integer.parseInt(accountid));
        try
        {
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountid);
            GatewayType gatewayType = GatewayTypeService.getGatewayType(gatewayAccount.getPgTypeId());
            tableName = Database.getTableNameForSettlement(gatewayType.getGateway());

            log.error("---Before readSettlementFile---");
            vTransactions = process.readSettlementFile(PZSettlementFile);
            log.error("---After readSettlementFile---");
        }
        catch (SystemError systemError)
        {
            log.error("SYSTEM ERROR",systemError);
            //new SystemError("Error while reading settlement file.Please check file format.");
            val=systemError.getMessage();
            return val;
        }
        if(vTransactions!=null && vTransactions.size()>0)
        {
            AuditTrailVO auditTrailVO = new AuditTrailVO();
            auditTrailVO.setActionExecutorId(String.valueOf("1"));
            auditTrailVO.setActionExecutorName("Admin");

            log.error("---Before process instanceof ArenaPlusPaymentProcess---");
            if(process instanceof ArenaPlusPaymentProcess)
            {
                log.error("---Inside process instanceof ArenaPlusPaymentProcess---");
                ArenaPlusPaymentProcess aProcess = new ArenaPlusPaymentProcess();
                val = aProcess.processSettlement(Integer.parseInt(accountid), vTransactions, process.getAdminEmailAddress(), auditTrailVO,tableName);
            }
            else if(process instanceof BorgunPaymentProcess)
            {
                log.error("---Inside process instanceof BorgunPaymentProcess---");
                BorgunPaymentProcess bProcess = new BorgunPaymentProcess();
                val = bProcess.processSettlement(Integer.parseInt(accountid), vTransactions, process.getAdminEmailAddress(), auditTrailVO,tableName);
            }
            else
            {
                log.error("---inside CommonPaymentProcess---");
                CommonPaymentProcess proc=new CommonPaymentProcess();
                val = proc.processSettlement(Integer.parseInt(accountid), vTransactions, process.getAdminEmailAddress(), auditTrailVO,tableName);
                log.error("---After processSettlement---");
                //log.error("val==="+val);
            }
        }
        else
        {
          val="Transactions not found in settlement file";
        }
        return val;
    }
}
