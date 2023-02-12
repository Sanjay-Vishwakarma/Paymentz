package com.directi.pg;

import com.payment.AbstractPaymentProcess;
import com.payment.PaymentProcessFactory;
import com.payment.borgun.core.BorgunPaymentProcess;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.request.PZFileVO;
import com.payment.request.PZTC40Record;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 123 on 7/11/2016.
 */
public class CommonFraudulent
{
    private static Logger log = new Logger(CommonSettlement.class.getName());

    public String processFraudUpload(String fileName, String fullFileName, String accountid, String isRefund)throws SystemError
    {
        PZFileVO pzFileVO = new PZFileVO();

        List<PZTC40Record> vTransactions = new ArrayList<PZTC40Record>();

        AbstractPaymentProcess process = PaymentProcessFactory.getPaymentProcessInstance(null, Integer.parseInt(accountid));
        pzFileVO.setFilepath(fullFileName);
        pzFileVO.setFileName(fullFileName);
        pzFileVO.setAccountId(Integer.parseInt(accountid));

        vTransactions = process.readTC40file(pzFileVO);

        StringBuffer val = new StringBuffer();

        CommonPaymentProcess proc=new CommonPaymentProcess();

        if(vTransactions!=null && vTransactions.size()>0)
        {

           if (process instanceof BorgunPaymentProcess)
           {
               BorgunPaymentProcess bg =new BorgunPaymentProcess();
               val=bg.processFraudulentTransactions(Integer.parseInt(accountid),vTransactions,process.getAdminEmailAddress(),isRefund);

           }
           else
           {
                log.error("---inside CommonPaymentProcess---");
               val = proc.processFraudulentTransactions(Integer.parseInt(accountid), vTransactions, process.getAdminEmailAddress(), isRefund);
           }
        }
        else
        {
            val.append("Error while upload File/Records not found");
        }
        return val.toString();
    }
}