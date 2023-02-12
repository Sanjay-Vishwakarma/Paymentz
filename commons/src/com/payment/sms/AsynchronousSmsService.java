package com.payment.sms;

import com.logicboxes.util.ApplicationProperties;
import com.payment.Mail.MailEventEnum;

import java.util.HashMap;
import java.util.concurrent.*;

/**
 * Created by sandip on 2/26/2017.
 */
public class AsynchronousSmsService
{
    private final static String COREPOOL = ApplicationProperties.getProperty("CORE_POOL_SIZE");
    private static String MAXPOOL = ApplicationProperties.getProperty("MAX_POOL_SIZE");
    private static String KEEPALIVE = ApplicationProperties.getProperty("KEEP_ALIVE");
    public static ExecutorService executorService = new ThreadPoolExecutor(Integer.parseInt(COREPOOL), Integer.parseInt(MAXPOOL), Long.parseLong(KEEPALIVE), TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    private static AsynchronousSmsService asynchronousSmsService = null;
    public AsynchronousSmsService()
    {

    }

    public static AsynchronousSmsService getInstance()
    {
        if (asynchronousSmsService != null)
        {
            return asynchronousSmsService;
        }
        else
        {
            asynchronousSmsService = new AsynchronousSmsService();
        }
        return asynchronousSmsService;
    }

    public Future<String> sendSMS(MailEventEnum smsEventEnum, String trackingId, String status, String remark, String billingDescriptor)
    {
        return executorService.submit(new EmailCallable(smsEventEnum, trackingId, status, remark, billingDescriptor));
    }

    public Future<String> sendSMS(MailEventEnum smsEventEnum, HashMap map)
    {
        return executorService.submit(new SMSCallable(smsEventEnum, map));
    }

    public class EmailCallable implements Callable<String>
    {
        public MailEventEnum smsEntityEnum;
        public String trackingId;
        public String status;
        public String remark;
        public String billingDescriptor;

        private EmailCallable(MailEventEnum smsEventEnum, String trackingId, String status, String remark, String billingDescriptor)
        {
            this.smsEntityEnum = smsEventEnum;
            this.trackingId = trackingId;
            this.status = status;
            this.remark = remark;
            this.billingDescriptor = billingDescriptor;
        }

        public String call() throws Exception
        {
            TransactionSMSEventUtil smsEventUtil = new TransactionSMSEventUtil();
            smsEventUtil.sendTransactionEventSMS(smsEntityEnum, trackingId, status, billingDescriptor);
            return "Success";
        }
    }

    public class SMSCallable implements Callable<String>
    {
        public MailEventEnum smsEntityEnum;
        public String telno;
        public String data;
        public HashMap map;

        private SMSCallable(MailEventEnum smsEventEnum, HashMap map)
        {
            this.smsEntityEnum = smsEventEnum;
            //this.telno = (String) map.get("phone");
            //this.data = (String) map.get("phone");
            this.map = map;

        }

        public String call() throws Exception
        {
            TransactionSMSEventUtil smsEventUtil = new TransactionSMSEventUtil();
            smsEventUtil.sendNonTransactionEventSMS(smsEntityEnum, map);
            return "Success";
        }
    }
}
