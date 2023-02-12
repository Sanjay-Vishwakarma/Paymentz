package com.payment.Mail;

import com.directi.pg.Mail;
import com.directi.pg.SystemError;
import com.logicboxes.util.ApplicationProperties;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Created by 123 on 10/26/2015.
 */
public class AsynchronousMailService
{
    //final static ResourceBundle rb = LoadProperties.getProperty("com.directi.pg.Settings");
    private final static String COREPOOL = ApplicationProperties.getProperty("CORE_POOL_SIZE");
    //private Session session;
    private static AsynchronousMailService asynchronousMailService = null;
    private static String MAXPOOL = ApplicationProperties.getProperty("MAX_POOL_SIZE");
    private static String KEEPALIVE = ApplicationProperties.getProperty("KEEP_ALIVE");
    public static ExecutorService executorService = new ThreadPoolExecutor(Integer.parseInt(COREPOOL),Integer.parseInt(MAXPOOL),Long.parseLong(KEEPALIVE),TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());

    public AsynchronousMailService()
    {
        //executorService = new ThreadPoolExecutor(Integer.parseInt(COREPOOL),Integer.parseInt(MAXPOOL),Long.parseLong(KEEPALIVE),TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
    }

    public static AsynchronousMailService getInstance()
    {
        if (asynchronousMailService != null)
        {
            return asynchronousMailService;
        }
        else
        {
            asynchronousMailService = new AsynchronousMailService();
        }
        return asynchronousMailService;
    }

    public static void sendAdminMail(String subject, String message) throws SystemError
    {
        MailService mailService = new MailService();
        HashMap adminMessage = new HashMap();
        adminMessage.put(MailPlaceHolder.MESSAGE, message);
        mailService.sendMail(MailEventEnum.ADMIN_MAIL, adminMessage);
    }

    public static void sendmail(String to, String from, String subject, String message) throws SystemError
    {
        Mail.sendmail(to, from, null, null, subject, message);
    }

    public static void main(String[] args)
    {
        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
        asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, "12343", "success", "", null);
    }

    public Future<String> sendEmail(MailEventEnum mailEventEnum, String trackingid, String status, String remark, String billingDescriptor)
    {
        return executorService.submit(new EmailCallable(mailEventEnum, trackingid, status, remark, billingDescriptor));
    }

    public Future<String> sendMerchantMonitoringAlert(MailEventEnum mailEventEnum, HashMap vMailContent)
    {
        return executorService.submit(new MerchantCallable(mailEventEnum, vMailContent));
    }

    public Future<String> sendMerchantSignup(MailEventEnum mailEventEnum, HashMap vMailContent)
    {
        return executorService.submit(new MerchantCallable(mailEventEnum, vMailContent));
    }

    public Future<String> MAFStatusChange(MailEventEnum mailEventEnum, HashMap vMailContent)
    {
        return executorService.submit(new MerchantCallable(mailEventEnum, vMailContent));
    }
    public String getDetailTable(Map<String, Map<String, String>> transDetail)
    {

        String style = "class=td11";
        String previousicicitransid = " ";
        String previousStyle = " bgcolor=\"\" color=\"#001963\" font-family=\"sans-serif\" size=\"2px\"";
        String currentStyle = " font bgcolor=\"\" color=\"#001963\" font-family=\"sans-serif\" font-size=\"2px\" ";

        Set<String> trackingids = transDetail.keySet();
        StringBuffer table = new StringBuffer();
        int i = 0;
        table.append("<table align=\"center\" cellspacing=\"1\"  border=\"1\" >");
        for (String trackingid : trackingids)
        {
            Map<String, String> hashMap = transDetail.get(trackingid);
            Set<String> columns = hashMap.keySet();
            if (i == 0)
            {
                table.append("<TR>");
                for (String column : columns)
                {
                    table.append("<TD valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\">");
                    table.append("<b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">" + column + "</font></p></b>");
                    table.append("</TD>");
                }
                table.append("</TR>");
            }
            i++;
            table.append("<TR>");
            if (!previousicicitransid.equals(transDetail.get(trackingid)))
            {
                String tempStyle = "";
                previousicicitransid = transDetail.get(trackingid).toString();
                tempStyle = previousStyle;
                previousStyle = currentStyle;
                currentStyle = tempStyle;
            }
            style = currentStyle;
            for (String column : columns)
            {
                table.append("<TD " + style + " >");
                table.append("<p align=\"center\" bgcolor=\"\" color=\"#001963\">" + hashMap.get(column) + "</p>");
                table.append("</TD>");
            }
            table.append("</TR>");
        }
        table.append("</table>");
        return table.toString();
    }

    public String getDetailTableForSingleTrans(LinkedHashMap<String, String> transDetail)
    {
        StringBuffer table = new StringBuffer();
        int i = 0;
        table.append("<table align=\"center\" cellspacing=\"1\"  border=\"1\" >");

        Set<String> columns = transDetail.keySet();
        if (i == 0)
        {
            table.append("<TR>");
            for (String column : columns)
            {
                table.append("<TD valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\">");
                table.append("<b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">" + column + "</font></p></b>");
                table.append("</TD>");
            }
            table.append("</TR>");
        }
        i++;
        table.append("<TR>");
        for (String column : columns)
        {
            table.append("<TD>");
            table.append("<p align=\"center\" bgcolor=\"\" color=\"#001963\" font-family=\"sans-serif\" font-size=\"2px\">" + transDetail.get(column) + "</p>");
            table.append("</TD>");
        }
        table.append("</TR>");

        table.append("</table>");
        return table.toString();
    }

    public class EmailCallable implements Callable<String>
    {
        public MailEventEnum mailEntityEnum;
        public HashMap<MailPlaceHolder, String> mailContent;
        public String trackingid;
        public String status;
        public String remark;
        public String billingDescriptor;

        private EmailCallable(MailEventEnum vMailEventEnum, String vTrackingId, String vStatus, String vRemark, String billingDescriptor)
        {
            this.mailEntityEnum = vMailEventEnum;
            //this.mailContent = vMailContent;
            this.trackingid = vTrackingId;
            this.status = vStatus;
            this.remark = vRemark;
            this.billingDescriptor = billingDescriptor;
        }

        public String call() throws Exception
        {
            SendTransactionEventMailUtil sendTransactionEventMailUtil = new SendTransactionEventMailUtil();

            sendTransactionEventMailUtil.sendTransactionEventMailNew(mailEntityEnum, trackingid, status, remark, billingDescriptor);

            return "Success";
        }
    }

    public class MerchantCallable implements Callable<String>
    {
        public MailEventEnum mailEntityEnum;
        public HashMap<MailPlaceHolder, String> mailContent;

        private MerchantCallable(MailEventEnum vMailEventEnum, HashMap mailContent)
        {
            this.mailEntityEnum = vMailEventEnum;
            this.mailContent = mailContent;
        }

        public String call() throws Exception
        {
            MailService mailService = new MailService();
            mailService.sendMail(mailEntityEnum, mailContent);
            return "Success";
        }
    }



}
