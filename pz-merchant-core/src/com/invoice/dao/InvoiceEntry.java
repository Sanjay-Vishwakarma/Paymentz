package com.invoice.dao;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import java.nio.file.Path;
import java.nio.file.FileSystems;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.BarcodeFormat;
import com.invoice.vo.*;
import com.logicboxes.util.ApplicationProperties;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;
import com.manager.TerminalManager;
import com.manager.dao.MerchantDAO;
import com.manager.dao.PayoutDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.PartnerDetailsVO;
import com.manager.vo.TerminalVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.sms.AsynchronousSmsService;
import org.apache.commons.io.FileUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import java.awt.*;
import java.io.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Base64;
import java.util.Date;
import java.util.List;
//import PaymentClient.*;

public class InvoiceEntry
{

    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.Servlet");
    final static String PROXYHOST = RB.getString("PROXYHOST");
    final static String PROXYPORT = RB.getString("PROXYPORT");
    final static String PROXYSCHEME=RB.getString("PROXYSCHEME");
    final static String NEW ="new";
    final static String REGENERATE="regenerate";
    final static String CANCEL="cancel";
    private static final ResourceBundle countryName = LoadProperties.getProperty("com.directi.pg.countrycodenamepairlist");
    private static final ResourceBundle QRCODERB = LoadProperties.getProperty("com.directi.pg.QRCode");
    private final static String INVOICE_GENERATION_PATH = ApplicationProperties.getProperty("INVOICE_GENERATION_PATH");
    private final static String PARTNER_LOGO_PATH = ApplicationProperties.getProperty("PARTNER_LOGO_PATH");

    static Hashtable statushash = new Hashtable();
    static TreeMap sortedMap = new TreeMap();
    static Logger log = new Logger(InvoiceEntry.class.getName());
    static Connection conn = null;
    int merchantid = -9999;
    private TransactionLogger transactionLogger = new TransactionLogger(InvoiceEntry.class.getName());

    static
    {
        // sortedMap.put("generated", "GENERATED");
        sortedMap.put("mailsent", "MAIL SENT");
        sortedMap.put("cancelled", "CANCELLED");
        sortedMap.put("regenerated", "RE-GENERATED");
        sortedMap.put("processed", "PROCESSED");
        sortedMap.put("expired", "EXPIRED");

    }

    public InvoiceEntry(int id)
    {
        log.debug("Entering Constructor");
        this.merchantid = id;
        try
        {
            conn = Database.getConnection();
        }
        catch (Exception e)
        {
            log.error("Connection cannot be obtained in InvoiceEntry.java ",e);
        }
        finally{
            Database.closeConnection(conn);
        }
        log.debug("leaving Constructor");
    }

    //static Category cat = Category.getInstance(Merchants.class.getName());


    public InvoiceEntry()
    {
        try
        {
            conn = Database.getConnection();
        }
        catch (Exception e)
        {
            log.error("Connection cannot be obtained in InvoiceEntry.java ", e);
        }
        finally {
            Database.closeConnection(conn);
        }
    }

    public static void remindInvoice(String invoiceno)  throws SystemError
    {
        sendInvoiceMail(invoiceno);
    }

    public static void sendInvoiceMail(String invoiceno) throws SystemError
    {
        Date date = new Date();
        String strDate = date.toString();
        Functions functions = new Functions();
        InvoiceEntry invoiceEntry = new InvoiceEntry();
        String company = ApplicationProperties.getProperty("COMPANY");
        String supportUrl = ApplicationProperties.getProperty("COMPANY_SUPPORT_URL");
        String supportFromAddress = ApplicationProperties.getProperty("SUPPORT_FROM_ADDRESS");
        String liveUrl = ApplicationProperties.getProperty("COMPANY_LIVE_URL");
        Hashtable values = (new InvoiceEntry()).getInvoiceDetails(invoiceno);
        // Hashtable value = (new InvoiceEntry()).getInvoiceConfigurationDetail(invoiceno);
        InvoiceVO invoiceVO = new InvoiceVO();
        invoiceVO = invoiceEntry.getInvoiceConfigurationDetailsForGeneratedInvoice((String) values.get("memberid"));
        String lateFee = invoiceVO.getLatefee();

        String duedate = "";
        String invoiceduedate = "";


        try
        {

            if (functions.isValueNull(invoiceVO.getDuedate()))
            {
                duedate = invoiceVO.getDuedate();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Calendar c = Calendar.getInstance();
                c.setTime(new Date()); // Now use today date.
                c.add(Calendar.DATE, (Integer.parseInt(duedate))); // Adding 5 days
                invoiceduedate = sdf.format(c.getTime());

            }
            invoiceVO.setInvoiceduedate(invoiceduedate);


            if (functions.isValueNull(lateFee) && !lateFee.equals("0"))
            {
                double amount = Double.parseDouble(invoiceVO.getLatefee());
                DecimalFormat twoPlaces = new DecimalFormat("0.00");
                invoiceVO.setLatefee(twoPlaces.format(amount));
            }
            else
            {
                invoiceVO.setLatefee("0.00");
            }

            String paymentTerms = invoiceVO.getPaymentterms();
            String gst = invoiceVO.getGST();
            if (!functions.isValueNull(invoiceVO.getPaymentterms()))
            {
                paymentTerms = "";
            }

            if (!functions.isValueNull(invoiceVO.getGST()))
            {
                gst = "";
            }

            values.put("paymentterms", paymentTerms);
            values.put("COMPANY", company);
            values.put("SUPPORT_URL", supportUrl);
            values.put("LIVE_URL", liveUrl);
            values.put("latefee", invoiceVO.getLatefee());
            values.put("islatefee", invoiceVO.getIslatefee());
            values.put("gst", gst);
            values.put("duedate", invoiceVO.getInvoiceduedate());
            values.put("address", values.get("address"));
            values.put("TIME", strDate);
            values.put("PROXYSCHEME", PROXYSCHEME);
            values.put("PROXYHOST", PROXYHOST);
            values.put("PROXYPORT", PROXYPORT);
            values.putAll(getMemberDetails((String) values.get("memberid")));
            log.debug("values-----" + values);
            System.out.println("values-----" + values);

            if (functions.isValueNull((String) values.get("terminalid")))
            {
                values.put("TERMINALID", (String) values.get("terminalid"));
                values.put("MSG", "An Invoice Has Been Generated by <#companyname#> <br> The Website of <#companyname#> : <a target=\"_blank\" href=\"<#sitename#>\" ><#sitename#></a> ");
                values.put("MSG2", "<a href=\"<#PROXYSCHEME#>://<#PROXYHOST#>:<#PROXYPORT#>/transaction/PayInvoice?inv=<#invoiceno#>&t=<#terminal#>&ct=<#ctoken#>\" ><font color=\"#FFFFFF\" face=\"Verdana, Arial\" size=\"3\"><B>PAY NOW<B></font></a>");
                //  values.put("INVOICE_URL", "<a href=\"<#PROXYSCHEME#>://<#PROXYHOST#>:<#PROXYPORT#>/transaction/PayInvoiceController?invoiceno=<#invoiceno#>&terminalid=<#terminal#>&ctoken=<#ctoken#>\"</a>");
                //values.put("MSG3","<form method=POST action=\"<#PROXYSCHEME#>://<#PROXYHOST#>:<#PROXYPORT#>/icici/servlet/PayInvoiceController?invoiceno=<#invoiceno#>&ctoken=<#ctoken#>\" > <input type=submit align=center value=\"Pay Now\"></form>");
            }
            else
            {
                values.put("MSG", "An Invoice Has Been Generated by <#companyname#> <br> The Website of <#companyname#> : <a target=\"_blank\" href=\"<#sitename#>\" ><#sitename#></a> ");
                values.put("MSG2", "<a href=\"<#PROXYSCHEME#>://<#PROXYHOST#>:<#PROXYPORT#>/transaction/PayInvoice?inv=<#invoiceno#>&ct=<#ctoken#>\" ><font color=\"#FFFFFF\" face=\"Verdana, Arial\" size=\"3\"><B>PAY NOW<B></font></a>");
                //  values.put("INVOICE_URL", "<a href=\"<#PROXYSCHEME#>://<#PROXYHOST#>:<#PROXYPORT#>/transaction/PayInvoiceController?invoiceno=<#invoiceno#>&ctoken=<#ctoken#>\"</a>");

                //values.put("MSG3","<form method=POST action=\"<#PROXYSCHEME#>://<#PROXYHOST#>:<#PROXYPORT#>/icici/servlet/PayInvoiceController?invoiceno=<#invoiceno#>&ctoken=<#ctoken#>\" > <input type=submit align=center value=\"Pay Now\"></form>");
            }


            values.put("SIGNATURE", generateSignature((String) values.get("memberid")));
            values.put("langForInvoice", (String) values.get("langForInvoice"));


            String isCredential = "0";
            isCredential = (String) values.get("isCredential");

            sendHTMLMail(values, isCredential, NEW, null, invoiceno);
        }
        catch(Exception e){
            log.error("Error while generating Invoice::" +  e.getMessage());
        }


        int remindercounter=0;
        remindercounter = Integer.parseInt((String)values.get("remindercounter"));
        remindercounter++;
        String query="update invoice set remindercounter ="+remindercounter+" ,status='mailsent' where invoiceno ="+invoiceno+";";
        try
        {
            conn=Database.getConnection();
            PreparedStatement p=conn.prepareStatement(query);
            p.executeUpdate();

        }catch(Exception e)
        {
            log.error("Exception occured while inserting data",e);
        }finally
        {
            Database.closeConnection(conn);
        }
    }

    public static void sendInvoiceSMS(String invoiceno, HashMap map) throws SystemError
    {
        Date date = new Date();
        String strDate = date.toString();
        Functions functions = new Functions();
        InvoiceEntry invoiceEntry = new InvoiceEntry();
        AsynchronousSmsService smsService = new AsynchronousSmsService();


        String company = ApplicationProperties.getProperty("COMPANY");
        String supportUrl = ApplicationProperties.getProperty("COMPANY_SUPPORT_URL");
        String supportFromAddress = ApplicationProperties.getProperty("SUPPORT_FROM_ADDRESS");
        String liveUrl = ApplicationProperties.getProperty("COMPANY_LIVE_URL");
        HashMap values = (new InvoiceEntry()).getInvoiceDetail(invoiceno);
        InvoiceVO invoiceVO = new InvoiceVO();
        invoiceVO = invoiceEntry.getInvoiceConfigurationDetails((String) values.get("memberid"));
        values.put("paymentterms",invoiceVO.getPaymentterms());
        values.put("COMPANY", company);
        values.put("SUPPORT_URL", supportUrl);
        values.put("LIVE_URL", liveUrl);
        values.put("TIME", strDate);
        values.put("PROXYSCHEME",PROXYSCHEME);
        values.put("PROXYHOST",PROXYHOST);
        values.put("PROXYPORT", PROXYPORT);
        values.put("custname",map.get("custname"));
        values.put("orderdesc",map.get("orderdesc"));
        values.put("invoiceno",map.get("invoiceno"));
        values.put("currency",map.get("currency"));
        values.put("amount",map.get("amount"));
        values.put("phone",map.get("phone"));
        values.put("phonecc",map.get("phonecc"));
        values.put("orderid",map.get("orderid"));
        values.put("ctoken",map.get("ctoken").toString().substring(0,16));
        values.putAll(getMemberDetails((String) values.get("memberid")));
        log.debug("values-----"+values);

        if (functions.isValueNull((String) values.get("terminalid"))  )
        {
            values.put("TERMINALID", (String) values.get("terminalid"));
            values.put("MSG", "An Invoice Has Been Generated by <#companyname#> <br> The Website of <#companyname#> : <a target=\"_blank\" href=\"<#sitename#>\" ><#sitename#></a> ");
            values.put("MSG2", "<a href=\"<#PROXYSCHEME#>://<#PROXYHOST#>:<#PROXYPORT#>/transaction/PayInvoice?inv=<#invoiceno#>&t=<#terminal#>&ct=<#ctoken#>\" ><font color=\"#FFFFFF\" face=\"Verdana, Arial\" size=\"3\"><B>PAY NOW<B></font></a>");
            //values.put("MSG3","<form method=POST action=\"<#PROXYSCHEME#>://<#PROXYHOST#>:<#PROXYPORT#>/icici/servlet/PayInvoiceController?invoiceno=<#invoiceno#>&ctoken=<#ctoken#>\" > <input type=submit align=center value=\"Pay Now\"></form>");
        }
        else
        {
            values.put("MSG", "An Invoice Has Been Generated by <#companyname#> <br> The Website of <#companyname#> : <a target=\"_blank\" href=\"<#sitename#>\" ><#sitename#></a> ");
            values.put("MSG2", "<a href=\"<#PROXYSCHEME#>://<#PROXYHOST#>:<#PROXYPORT#>/transaction/PayInvoice?inv=<#invoiceno#>&t=&ct=<#ctoken#>\" ><font color=\"#FFFFFF\" face=\"Verdana, Arial\" size=\"3\"><B>PAY NOW<B></font></a>");
            //values.put("MSG3","<form method=POST action=\"<#PROXYSCHEME#>://<#PROXYHOST#>:<#PROXYPORT#>/icici/servlet/PayInvoiceController?invoiceno=<#invoiceno#>&ctoken=<#ctoken#>\" > <input type=submit align=center value=\"Pay Now\"></form>");
        }



        values.put("SIGNATURE",generateSignature((String)values.get("memberid")));


        String isCredential ="0";
        isCredential=(String)values.get("isCredential");

        smsService.sendSMS(MailEventEnum.GENERATING_INVOICE_BY_MERCHANT, values);


        int remindercounter=0;
        remindercounter = Integer.parseInt((String)values.get("remindercounter"));
        remindercounter++;
        String query="update invoice set remindercounter ="+remindercounter+" ,status='mailsent' where invoiceno ="+invoiceno+";";
        try
        {
            conn=Database.getConnection();
            PreparedStatement p=conn.prepareStatement(query);
            p.executeUpdate();

        }catch(Exception e)
        {
            log.error("Exception occured while inserting data",e);
        }finally
        {
            Database.closeConnection(conn);
        }
    }

    public static InvoiceVO remindInvoice(InvoiceVO invoiceVO)  throws SystemError
    {
        return sendInvoiceMail(invoiceVO);
    }

    public static InvoiceVO sendInvoiceMail(InvoiceVO invoiceVO) throws SystemError
    {
        Date date = new Date();
        String strDate = date.toString();
        Functions functions = new Functions();
        InvoiceEntry invoiceEntry = new InvoiceEntry();

        String company = ApplicationProperties.getProperty("COMPANY");
        String supportUrl = ApplicationProperties.getProperty("COMPANY_SUPPORT_URL");
        String supportFromAddress = ApplicationProperties.getProperty("SUPPORT_FROM_ADDRESS");
        String liveUrl = ApplicationProperties.getProperty("COMPANY_LIVE_URL");
        Hashtable values = (new InvoiceEntry()).getInvoiceDetails(invoiceVO.getInvoiceno());
        String lateFee = invoiceVO.getLatefee();
        String duedate="";
        String invoiceduedate="";
        log.debug("latefee in mail----"+invoiceVO.getLatefee());
        if (functions.isValueNull(lateFee) && !lateFee.equals("0"))
        {
            double amount = Double.parseDouble(invoiceVO.getLatefee());
            DecimalFormat twoPlaces = new DecimalFormat("0.00");
            invoiceVO.setLatefee(twoPlaces.format(amount));
        }
        else
        {
            invoiceVO.setLatefee("0.00");
        }
        if (functions.isValueNull(invoiceVO.getDuedate()))
        {
            duedate =invoiceVO.getDuedate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Calendar c = Calendar.getInstance();
            c.setTime(new Date()); // Now use today date.
            c.add(Calendar.DATE, (Integer.parseInt(duedate))); // Adding 5 days
            invoiceduedate = sdf.format(c.getTime());

        }
        invoiceVO.setInvoiceduedate(invoiceduedate);
        invoiceVO.setTerminalid((String) values.get("terminalid"));
        invoiceVO.setCtoken((String) values.get("ctoken"));

        values.put("COMPANY", company);
        values.put("SUPPORT_URL", supportUrl);
        values.put("LIVE_URL", liveUrl);
        values.put("TIME", strDate);
        values.put("gst",invoiceVO.getGST());
        values.put("duedate",invoiceVO.getInvoiceduedate());
        values.put("PROXYSCHEME", PROXYSCHEME);
        values.put("PROXYHOST", PROXYHOST);
        values.put("PROXYPORT", PROXYPORT);
        values.put("latefee",invoiceVO.getLatefee());
        values.put("islatefee",invoiceVO.getIslatefee());
        values.putAll(getMemberDetails((String) values.get("memberid")));


        if (functions.isValueNull((String) values.get("terminalid"))  )
        {
            values.put("TERMINALID", (String) values.get("terminalid"));
            values.put("MSG", "An Invoice Has Been Generated by <#companyname#> <br> The Website of <#companyname#> : <a target=\"_blank\" href=\"<#sitename#>\" ><#sitename#></a> ");
            values.put("MSG2", "<a href=\"<#PROXYSCHEME#>://<#PROXYHOST#>:<#PROXYPORT#>/transaction/PayInvoice?inv=<#invoiceno#>&t=<#terminal#>&ct=<#ctoken#>\" ><font color=\"#FFFFFF\" face=\"Verdana, Arial\" size=\"3\"><B>PAY NOW<B></font></a>");
            invoiceVO.setTransactionUrl(PROXYSCHEME + "://" + PROXYHOST + "/transaction/PayInvoice?inv=" + invoiceVO.getInvoiceno() + "&t=" + (String) values.get("terminalid") + "&ct=" + (String) values.get("ctoken"));
        }
        else
        {
            values.put("MSG", "An Invoice Has Been Generated by <#companyname#> <br> The Website of <#companyname#> : <a target=\"_blank\" href=\"<#sitename#>\" ><#sitename#></a> ");
            values.put("MSG2", "<a href=\"<#PROXYSCHEME#>://<#PROXYHOST#>:<#PROXYPORT#>/transaction/PayInvoice?inv=<#invoiceno#>&ct=<#ctoken#>\" ><font color=\"#FFFFFF\" face=\"Verdana, Arial\" size=\"3\"><B>PAY NOW<B></font></a>");
            invoiceVO.setTransactionUrl(PROXYSCHEME+ "://" +PROXYHOST+ "/transaction/PayInvoice?inv=" +invoiceVO.getInvoiceno()+ "&ct=" +(String) values.get("ctoken"));
        }

        values.put("SIGNATURE", generateSignature((String) values.get("memberid")));


        String isCredential ="0";
        //isCredential=(String)values.get("isCredential");

        if (invoiceVO.getIsemail().equals("Y"))
        {
            sendHTMLMail(values, isCredential, NEW, null, invoiceVO.getInvoiceno());

        }

        int remindercounter=0;
        remindercounter = Integer.parseInt((String)values.get("remindercounter"));
        remindercounter++;
        String query="update invoice set remindercounter ="+remindercounter+" ,status='mailsent' where invoiceno ="+invoiceVO.getInvoiceno()+";";
        try
        {
            conn=Database.getConnection();
            PreparedStatement p=conn.prepareStatement(query);
            p.executeUpdate();
            log.debug("mailsent query0--"+p);
            invoiceVO.setStatus("mailsent");
        }catch(Exception e)
        {
            log.error("Exception occured while inserting data",e);
        }finally
        {
            Database.closeConnection(conn);
        }
        return invoiceVO;
    }

    public static InvoiceVO sendInvoiceMailRegenerate(String oldinvoiceno,InvoiceVO invoiceVO) throws SystemError
    {
        log.debug("Entering sendInvoiceMailRegenerate");
        Date date = new Date();
        String strDate = date.toString();
        String duedate="";
        String invoiceduedate="";
        Functions functions = new Functions();

        String company = ApplicationProperties.getProperty("COMPANY");
        String supportUrl = ApplicationProperties.getProperty("COMPANY_SUPPORT_URL");
        String supportFromAddress = ApplicationProperties.getProperty("SUPPORT_FROM_ADDRESS");
        String liveUrl = ApplicationProperties.getProperty("COMPANY_LIVE_URL");
        Hashtable values = (new InvoiceEntry()).getInvoiceDetails(invoiceVO.getInvoiceno());
        if (functions.isValueNull(invoiceVO.getDuedate()))
        {
            duedate =invoiceVO.getDuedate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Calendar c = Calendar.getInstance();
            c.setTime(new Date()); // Now use today date.
            c.add(Calendar.DATE, (Integer.parseInt(duedate))); // Adding 5 days
            invoiceduedate = sdf.format(c.getTime());
        }
        invoiceVO.setInvoiceduedate(invoiceduedate);
        values.put("COMPANY", company);
        values.put("SUPPORT_URL", supportUrl);
        values.put("LIVE_URL", liveUrl);
        values.put("TIME", strDate);
        values.put("islatefee", invoiceVO.getIslatefee());
        values.put("latefee", invoiceVO.getLatefee());
        values.put("gst",invoiceVO.getGST());
        values.put("duedate",invoiceVO.getInvoiceduedate());
        values.put("PROXYSCHEME",PROXYSCHEME);
        values.put("PROXYHOST",PROXYHOST);
        values.put("PROXYPORT",PROXYPORT);
        values.putAll(getMemberDetails((String) values.get("memberid")));
        values.put("MSG","This is a regenerated Invoice <BR> The Old Invoice No was "+oldinvoiceno+" and the new Invoice Number is "+invoiceVO.getInvoiceno()+"<BR> Kindly Note that the Previous Invoice has been voided so please do not Process It.  ");
        values.put("MSG2","<a href=\"<#PROXYSCHEME#>://<#PROXYHOST#>:<#PROXYPORT#>/transaction/PayInvoice?inv=<#invoiceno#>&ct=<#ctoken#>\" ><font color=\"#FFFFFF\" face=\"Verdana, Arial\" size=\"3\"><B>PAY NOW<B></font></a>");
        //values.put("MSG3","<form method=POST action=\"<#PROXYSCHEME#>://<#PROXYHOST#>:<#PROXYPORT#>/icici/servlet/PayInvoiceController?invoiceno=<#invoiceno#>&ctoken=<#ctoken#>\" > <input type=submit align=center value=\"Pay Now\"></form>");
        invoiceVO.setTransactionUrl(PROXYSCHEME + "://" + PROXYHOST + "/transaction/PayInvoice?inv=" + invoiceVO.getInvoiceno() + "&ct=" + (String) values.get("ctoken"));

        values.put("SIGNATURE",generateSignature((String) values.get("memberid")));

        sendHTMLMail(values, null, REGENERATE, oldinvoiceno, invoiceVO.getInvoiceno());

        int remindercounter=0;
        remindercounter = Integer.parseInt((String)values.get("remindercounter"));
        remindercounter++;
        String query="update invoice set remindercounter ="+remindercounter+" ,status='mailsent' where invoiceno ="+invoiceVO.getInvoiceno()+";";
        try
        {
            conn=Database.getConnection();
            PreparedStatement p=conn.prepareStatement(query);
            p.executeUpdate();
            log.debug("regenerate query----" + p);
        }
        catch(Exception e)
        {
            log.error("Exception occured while inserting data",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return invoiceVO;
    }

    private static void sendHTMLMail(Hashtable values,String isCredential,String action,String oldinvoiceno,String invoiceno ) throws SystemError
    {

        System.out.println("invoice no" + invoiceno);
        // default values need to pass in hashmap
        //MailService mailService=new MailService();
        AsynchronousMailService mailService = new AsynchronousMailService();
        List<ProductList> listProducts = (List<ProductList>) values.get("listOfProducts");
        Functions functions = new Functions();

        String lateFee = "";
        String duedate = String.valueOf(values.get("duedate"));

        double afterExpAmount = 0.0d;
        String afterExpAmount1 = "";

        log.debug("latefee----" + String.valueOf(values.get("latefee")));
        if (functions.isValueNull(String.valueOf(values.get("latefee"))) && values.get("islatefee").equals("Y"))
        {
            lateFee = String.valueOf(values.get("latefee"));
        }
        else
        {
            lateFee="00.00";
        }

        HashMap mailValue=new HashMap();
        mailValue.put(MailPlaceHolder.TOID,values.get("memberid"));
        mailValue.put(MailPlaceHolder.NAME,values.get("custname"));
        mailValue.put(MailPlaceHolder.DATE,values.get("TIME"));
        mailValue.put(MailPlaceHolder.INVOICENO,invoiceno);
        mailValue.put(MailPlaceHolder.ORDERID,values.get("orderid"));
        mailValue.put(MailPlaceHolder.DESC,values.get("orderdesc"));
        mailValue.put(MailPlaceHolder.AMOUNT,values.get("amount"));
        mailValue.put(MailPlaceHolder.CURRENCY,values.get("currency"));
        mailValue.put(MailPlaceHolder.CARDHOLDERNAME, values.get("custname"));
        mailValue.put(MailPlaceHolder.MERCHANTCOMPANYNAME,values.get("companyname"));
        mailValue.put(MailPlaceHolder.MCOMNAME,values.get("sitename"));
        mailValue.put(MailPlaceHolder.LATE_FEE, lateFee);
        mailValue.put(MailPlaceHolder.CustomerEmail, values.get("custemail"));
        mailValue.put(MailPlaceHolder.CTOKEN,values.get("ctoken").toString().substring(0, 16));
        mailValue.put(MailPlaceHolder.TERMINALID, values.get("TERMINALID"));
        mailValue.put(MailPlaceHolder.MerchantEmail, values.get("contact_emails"));
        mailValue.put(MailPlaceHolder.TERMINALID, values.get("TERMINALID"));
        mailValue.put(MailPlaceHolder.PAYMENTTERMS,values.get("paymentterms"));
        mailValue.put(MailPlaceHolder.DUE_DATE,values.get("duedate"));
        mailValue.put(MailPlaceHolder.LANG_FOR_INVOICE,values.get("langForInvoice"));



        if(values.get("TERMINALID")!=null)
        {
            mailValue.put(MailPlaceHolder.TERMINALID, values.get("TERMINALID"));
        }
        else
        {
            mailValue.put(MailPlaceHolder.TERMINALID,"");
        }
        if(functions.isValueNull(lateFee))
        {
            mailValue.put(MailPlaceHolder.LATE_FEE, values.get("latefee"));
            String amount = (String) values.get("amount");
            afterExpAmount = Double.parseDouble(amount) + Double.parseDouble(lateFee);

            afterExpAmount1 = String.valueOf(afterExpAmount);
            if (afterExpAmount != 0)
            {
                DecimalFormat twoPlaces = new DecimalFormat("0.00");
                afterExpAmount1 = twoPlaces.format(afterExpAmount);
            }
        }
        else
        {
            mailValue.put(MailPlaceHolder.LATE_FEE,"");
        }


        String mailMessage = null;
        log.debug(action + values);


            if(listProducts.size()>0)
            {
                StringBuffer mailBuffer = new StringBuffer();
                StringBuffer productBuffer = new StringBuffer();
               // mailBuffer.append("<br>");
               // mailBuffer.append("<center><b>Invoice Product List</b></center>");
                //mailBuffer.append("<br>");
                //productBuffer.append(functions.getTableHeaderforMail("Description", "Unit", "Amount", "Quantity", "Tax(%)", "Sub Total"));

                for (ProductList productList : listProducts)
                {

                  if (!functions.isValueNull(productList.getTax()))
                  {
                      productList.setTax("0");
                  }
                    if (!functions.isValueNull(productList.getProductUnit()))
                    {
                        productList.setProductUnit("");
                    }


                    productBuffer.append(functions.setTableDataforMail(productList.getProductDescription(), productList.getProductUnit(), productList.getProductAmount(), productList.getQuantity(), productList.getTax(), productList.getProductTotal(), "N"));

                }
               // productBuffer.append(functions.setTableDataforMail("", "","","", "Tax Amount"+"("+values.get("gst")+"%)",(String)values.get("taxamount"),"Y"));
                /*productBuffer.append(functions.setTableDataforMail("", "","","", "Grand Total",(String)values.get("amount"),"Y"));
                if (values.get("islatefee").equals("Y"))
                {
                    productBuffer.append(functions.setTableDataforMail("", "","","", " Amount After Due Date "+duedate+" Late Fee "+"("+values.get("currency")+ lateFee+")",afterExpAmount1,"Y"));
                }*/
               // productBuffer.append("</table>");
                mailBuffer.append(productBuffer);
               // mailBuffer.append("<br>");
                mailValue.put(MailPlaceHolder.MULTIPALTRANSACTION, mailBuffer.toString());
                mailValue.put(MailPlaceHolder.GST, values.get("gst"));
                mailValue.put(MailPlaceHolder.TAXAMOUNT, values.get("taxamount"));
                mailValue.put(MailPlaceHolder.TOTALAMOUNT, values.get("amount"));
                mailValue.put(MailPlaceHolder.DUEDATE, duedate);
                mailValue.put(MailPlaceHolder.LATEFEE, values.get("currency")+" "+lateFee);
                mailValue.put(MailPlaceHolder.AFTEREXPAMOUNT, afterExpAmount1);
                log.debug("mail buffer for invoice---" + mailBuffer);

            }
            else
            {
                mailValue.put(MailPlaceHolder.MULTIPALTRANSACTION, "");
            }
            Font f1 = FontFactory.getFont(FontFactory.TIMES_ROMAN,20);
            f1.setColor(Color.BLACK);

            Font f2 = FontFactory.getFont(FontFactory.TIMES_BOLD,15);
            f2.setColor(Color.WHITE);

            Font f3 = FontFactory.getFont(FontFactory.TIMES_BOLD,15);
            f3.setColor(Color.BLACK);
            //code for invoice PDF
            try
            {

                PayoutDAO payoutDAO=new PayoutDAO();
                HashMap partnerDetails=payoutDAO.getPartnerDetails(String.valueOf(values.get("memberid")));
                String partnerLogoName=(String)partnerDetails.get("logoName");
                com.lowagie.text.Document document = new com.lowagie.text.Document(PageSize.A3,40,40,40,40);
                SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String invoiceFileName=("Invoice"+invoiceno);
                List<ProductList> productListHashMap = (List<ProductList>) values.get("listOfProducts");
                invoiceFileName=invoiceFileName+".pdf";
                File filePath = new File(INVOICE_GENERATION_PATH + invoiceFileName);
                try
                {
                    PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));


                }
                catch ( DocumentException e)
                {
                    //e.printStackTrace();
                    log.debug("DocumentException :" +e);
                }
                document.open();
                Image partnerImageInstance = Image.getInstance(PARTNER_LOGO_PATH +partnerLogoName);
                partnerImageInstance.scaleAbsolute(150f, 150f);
                //partnerImageInstance.scaleAbsoluteHeight(40f);
                partnerImageInstance.scaleAbsoluteWidth(partnerImageInstance.getWidth());

                Table table = null;
                table = new Table(6);
                table.setWidth(100);
                table.setBorderColor(new Color(0, 0, 0));
                table.setPadding(1);

                Cell partnerNameCaptionCell=new Cell();
                partnerNameCaptionCell.setColspan(2);
                partnerNameCaptionCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                partnerNameCaptionCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
               /* partnerNameCaptionCell.setLeading(10);*/

                MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
                MerchantDAO merchantDAO = new MerchantDAO();
                merchantDetailsVO = merchantDAO.getMemberDetails(String.valueOf(values.get("memberid")));

                String country = countryName.getString(merchantDetailsVO.getCountry());
                Paragraph paragraph1 = new Paragraph(merchantDetailsVO.getCompany_name());
                Paragraph paragraph2 = new Paragraph(merchantDetailsVO.getAddress()+ " "+merchantDetailsVO.getCity());
                Paragraph paragraph3 = new Paragraph(merchantDetailsVO.getState() +" "+ country);
                partnerNameCaptionCell.add(paragraph1);
                partnerNameCaptionCell.add(paragraph2);
                partnerNameCaptionCell.add(paragraph3);

                Cell reportingDateCaptionCe11 = new Cell(partnerImageInstance);
                reportingDateCaptionCe11.setColspan(2);
                //reportingDateCaptionCe11.setBackgroundColor(Color.white.brighter());
                reportingDateCaptionCe11.setHorizontalAlignment(Element.ALIGN_CENTER);
                reportingDateCaptionCe11.setVerticalAlignment(Element.ALIGN_MIDDLE);

                Cell partnerLogoCell = new Cell(new Paragraph(("INVOICE:#"+invoiceno),f1));
                partnerLogoCell.setColspan(2);
                partnerLogoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                partnerLogoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

                table.addCell(partnerNameCaptionCell);
                table.addCell(reportingDateCaptionCe11);
                table.addCell(partnerLogoCell);

                Cell invoicedetails=new Cell("Order Details");
                invoicedetails.setColspan(3);
                invoicedetails.setHorizontalAlignment(Element.ALIGN_CENTER);
                invoicedetails.setVerticalAlignment(Element.ALIGN_CENTER);
                invoicedetails.setBackgroundColor(Color.gray.brighter());

                Cell customerdetails=new Cell("Customer Details");
                customerdetails.setColspan(3);
                customerdetails.setHorizontalAlignment(Element.ALIGN_CENTER);
                customerdetails.setVerticalAlignment(Element.ALIGN_CENTER);
                customerdetails.setBackgroundColor(Color.gray.brighter());

                table.addCell(invoicedetails);
                table.addCell(customerdetails);


                Cell partnerNameLabel=new Cell("Order ID:" +values.get("orderid"));
                String custName=values.get("custname")!=null? (String) values.get("custname") :"";
                Cell partnerNameValue=new Cell("Customer Name:"+custName);

                partnerNameLabel.setColspan(3);
                partnerNameValue.setColspan(3);

                table.addCell(partnerNameLabel);
                table.addCell(partnerNameValue);

                Cell memberIdLabel=new Cell("Order Date/Invoice Date: "+values.get("TIME"));
                Cell memberIdValue = new Cell("Customer Email:"+values.get("custemail"));

                memberIdLabel.setColspan(3);
                memberIdValue.setColspan(3);

                table.addCell(memberIdLabel);
                table.addCell(memberIdValue);

                String custaddress="";
                if (functions.isValueNull(String.valueOf(values.get("address"))))
                {
                    custaddress= String.valueOf(values.get("address"));
                }

                Cell terminalIdLabel=new Cell("Order Description: "+values.get("orderdesc"));
                Cell terminalValue=new Cell("Customer Address: "+custaddress);

                terminalIdLabel.setColspan(3);
                terminalValue.setColspan(3);

                table.addCell(terminalIdLabel);
                table.addCell(terminalValue);

                 if (listProducts.size()>0)
                 {
                     String taxamounts="";
                     if (functions.isValueNull(String.valueOf(values.get("gst"))))
                     {
                         taxamounts=String.valueOf(values.get("gst"));
                     }

                     String phonecc="";
                     if (functions.isValueNull(String.valueOf(values.get("phonecc"))))
                     {
                         phonecc=(String.valueOf(values.get("phonecc")));
                     }

                     Cell invoicegst = new Cell("GST/VAT: " + "" + taxamounts + "%");
                     Cell customerphone = new Cell("Customer TelNo:" +phonecc + "-" + values.get("phone"));

                     invoicegst.setColspan(3);
                     customerphone.setColspan(3);

                     table.addCell(invoicegst);
                     table.addCell(customerphone);
                 }
                else
                 {
                     String phonecc="";
                     if (functions.isValueNull(String.valueOf(values.get("phonecc"))))
                     {
                         phonecc=(String.valueOf(values.get("phonecc")));
                     }


                     Cell invoicegst = new Cell("GST/VAT:"+"");
                     Cell customerphone = new Cell("Customer TelNo:" + phonecc + "-" + values.get("phone"));

                     invoicegst.setColspan(3);
                     customerphone.setColspan(3);

                     table.addCell(invoicegst);
                     table.addCell(customerphone);

                 }



                if(listProducts.size()>0)
                {

                    String str = null;
                    byte[] utf8;

                    String cuurrency="";
                    Locale usLocale = Locale.US;
                    Locale ukLocale = Locale.UK;
                    Locale frLocale = Locale.FRANCE;
                    Locale jpLocale = Locale.JAPAN;
                    Locale cdLocale = Locale.CANADA;


                    Currency currency = Currency.getInstance(usLocale);
                    Currency currency1 = Currency.getInstance(ukLocale);
                    Currency currency2 = Currency.getInstance(frLocale);
                    Currency currency3 = Currency.getInstance(jpLocale);
                    Currency currency4 = Currency.getInstance(cdLocale);

                    if (values.get("currency").equals("USD"))
                    {
                        cuurrency =currency.getSymbol(usLocale);
                    }
                    if (values.get("currency").equals("GBP"))
                    {
                        cuurrency =currency1.getSymbol(ukLocale);
                    }
                    if (values.get("currency").equals("EUR"))
                    {
                        cuurrency =currency2.getSymbol(frLocale);
                    }
                    if (values.get("currency").equals("JPY"))
                    {
                        str = "\u00A5";
                        utf8 = str.getBytes("UTF-8");
                        cuurrency = new String(utf8, "UTF-8");
                    }
                    if (values.get("currency").equals("CAD"))
                    {
                        cuurrency =currency4.getSymbol(cdLocale);
                    }



                    Cell invoiceProductList = new Cell("Invoice Product List");
                    invoiceProductList.setColspan(6);
                    invoiceProductList.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceProductList.setBackgroundColor(Color.gray.brighter());
                    table.addCell(invoiceProductList);

                    Cell invoicedesclabel = new Cell("Product");
                    Cell invoiceunitlabel = new Cell("Unit");
                    Cell invoiceamountlabel = new Cell("Amount");
                    Cell invoicequantitylabel = new Cell("Quantity");
                    Cell invoicetaxlabel = new Cell("Tax(%)");
                    Cell invoicesublabel = new Cell("Sub Total");


                    invoicedesclabel.setBackgroundColor(Color.gray.brighter());
                    invoiceunitlabel.setBackgroundColor(Color.gray.brighter());
                    invoiceamountlabel.setBackgroundColor(Color.gray.brighter());
                    invoicequantitylabel.setBackgroundColor(Color.gray.brighter());
                    invoicetaxlabel.setBackgroundColor(Color.gray.brighter());
                    invoicesublabel.setBackgroundColor(Color.gray.brighter());

                    invoicedesclabel.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceunitlabel.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceamountlabel.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoicequantitylabel.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoicetaxlabel.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoicesublabel.setHorizontalAlignment(Element.ALIGN_CENTER);

                    table.addCell(invoicedesclabel);
                    table.addCell(invoiceunitlabel);
                    table.addCell(invoiceamountlabel);
                    table.addCell(invoicequantitylabel);
                    table.addCell(invoicetaxlabel);
                    table.addCell(invoicesublabel);


                    Cell invoiceDescvalue, invoiceUnitvalue, invoiceAmountvalue, invoiceQuanityvalue, invoiceTaxvalue, invoiceSubtotalvalue;

                    double addtax = 0;
                    int quantityy = 0;
                    double producttotals = 0;
                    String productfinal ="";
                    for (int i = 0; i < listProducts.size(); i++)
                    {

                        ProductList productList = listProducts.get(i);
                        invoiceDescvalue = new Cell(productList.getProductDescription());
                        String unit="";
                        if (functions.isValueNull(productList.getProductUnit()))
                        {
                            unit= productList.getProductUnit();
                        }
                        invoiceUnitvalue = new Cell(unit);
                        invoiceAmountvalue = new Cell(cuurrency+" "+productList.getProductAmount());
                        invoiceQuanityvalue = new Cell(productList.getQuantity());
                        invoiceTaxvalue = new Cell(productList.getTax());
                        invoiceSubtotalvalue = new Cell(cuurrency+" "+productList.getProductTotal());

                        String prodtotal=productList.getProductTotal();
                        addtax += Double.parseDouble(productList.getTax());
                        quantityy += Integer.parseInt(productList.getQuantity());
                        producttotals += Double.parseDouble(prodtotal);
                        productfinal = String.format("%.2f", producttotals);

                        invoiceDescvalue.setHorizontalAlignment(Element.ALIGN_CENTER);
                        invoiceUnitvalue.setHorizontalAlignment(Element.ALIGN_CENTER);
                        invoiceAmountvalue.setHorizontalAlignment(Element.ALIGN_CENTER);
                        invoiceQuanityvalue.setHorizontalAlignment(Element.ALIGN_CENTER);
                        invoiceTaxvalue.setHorizontalAlignment(Element.ALIGN_CENTER);
                        invoiceSubtotalvalue.setHorizontalAlignment(Element.ALIGN_CENTER);

                        table.addCell(invoiceDescvalue);
                        table.addCell(invoiceUnitvalue);
                        table.addCell(invoiceAmountvalue);
                        table.addCell(invoiceQuanityvalue);
                        table.addCell(invoiceTaxvalue);
                        table.addCell(invoiceSubtotalvalue);
                    }

                    Cell total=new Cell("Total");
                    total.setColspan(3);
                    total.setHorizontalAlignment(Element.ALIGN_CENTER);

                    Cell quantity = new Cell(String.valueOf(quantityy));
                    quantity.setColspan(1);
                    quantity.setBackgroundColor(Color.white.brighter());
                    quantity.setHorizontalAlignment(Element.ALIGN_CENTER);

                    Cell amount = new Cell(String.valueOf(addtax));
                    amount.setColspan(1);
                    amount.setHorizontalAlignment(Element.ALIGN_CENTER);

                    Cell tax = new Cell(String.valueOf(cuurrency+" "+productfinal));
                    tax.setColspan(1);
                    tax.setHorizontalAlignment(Element.ALIGN_CENTER);

                    table.addCell(total);
                    table.addCell(quantity);
                    table.addCell(amount);
                    table.addCell(tax);

                    Cell partnerCommissionAmountLabel = new Cell("Tax Amount");
                    Cell partnerCommissionAmountValue = new Cell(cuurrency+" "+String.valueOf(values.get("taxamount")));

                    partnerCommissionAmountLabel.setColspan(5);
                    partnerCommissionAmountLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
                    partnerCommissionAmountValue.setHorizontalAlignment(Element.ALIGN_CENTER);

                    table.addCell(partnerCommissionAmountLabel);
                    table.addCell(partnerCommissionAmountValue);
                }
                else
                {
                    Cell invoicedetail = new Cell("Invoice Details");
                    invoicedetail.setColspan(6);
                    invoicedetail.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoicedetail.setBackgroundColor(Color.gray.brighter());
                    table.addCell(invoicedetail);
                }


                String currencySymbol = null;
                String str = null;
                byte[] utf8;
                String cuurrency="";
                Locale usLocale = Locale.US;
                Locale ukLocale = Locale.UK;
                Locale frLocale = Locale.FRANCE;
                Locale jpLocale = Locale.JAPAN;
                Locale cdLocale = Locale.CANADA;

                Currency currency = Currency.getInstance(usLocale);
                Currency currency1 = Currency.getInstance(ukLocale);
                Currency currency2 = Currency.getInstance(frLocale);
                Currency currency3 = Currency.getInstance(jpLocale);
                Currency currency4 = Currency.getInstance(cdLocale);

                if (values.get("currency").equals("USD"))
                {
                    cuurrency =currency.getSymbol(usLocale);
                }
                if (values.get("currency").equals("GBP"))
                {
                    cuurrency =currency1.getSymbol(ukLocale);
                }
                if (values.get("currency").equals("EUR"))
                {
                    cuurrency =currency2.getSymbol(frLocale);
                }
                if (values.get("currency").equals("JPY"))
                {

                    str = "\u00A5";
                    utf8 = str.getBytes("UTF-8");
                    cuurrency = new String(utf8, "UTF-8");

                }
                if (values.get("currency").equals("CAD"))
                {
                    cuurrency =currency4.getSymbol(cdLocale);
                }


                Cell partnerChargesAmountLabel=new Cell("Total Amount");
                Cell partnerChargesAmountValue=new Cell(new Paragraph(cuurrency+" "+String.valueOf(values.get("amount")),f3));


                partnerChargesAmountLabel.setColspan(5);
                partnerChargesAmountValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                partnerChargesAmountLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(partnerChargesAmountLabel);
                table.addCell(partnerChargesAmountValue);


                if (values.get("islatefee").equals("Y"))
                {
                    Cell lateFeeLabel = new Cell("Amount After Due Date " + duedate + " Late Fee " + "(" + cuurrency+" "+ lateFee + ")");
                    Cell lateFeeValue = new Cell(new Paragraph(cuurrency+" "+ afterExpAmount1,f3));
                    lateFeeLabel.setColspan(5);

                    lateFeeValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                    lateFeeLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(lateFeeLabel);
                    table.addCell(lateFeeValue);
                }
                document.add(table);
                Paragraph paragraph = new Paragraph();
                paragraph.setAlignment(Element.ALIGN_CENTER);
                Chunk chunk=new Chunk("This is a computer generated invoice. No signature required.");
                paragraph.add(chunk);
                document.add(paragraph);


                MerchantDetailsVO merchantDetailsVO1 = new MerchantDetailsVO();
                MerchantDAO merchantDAO1 = new MerchantDAO();
                merchantDetailsVO1 = merchantDAO1.getMemberDetails(String.valueOf(values.get("memberid")));


                if (merchantDetailsVO1.getIsPoweredBy().equals("Y"))
                {
                    Image poweredByLogoValue = Image.getInstance(PARTNER_LOGO_PATH + "poweredby_new_logo.png");
                    poweredByLogoValue.setAlignment(Element.ALIGN_RIGHT);
                    document.add(poweredByLogoValue);
                }
                document.close();


            }
            catch (Exception ex)
            {
                log.error(ex);
            }
        if(action.equals(NEW))
        {
            mailValue.put(MailPlaceHolder.MSG," ");
            mailValue.put(MailPlaceHolder.INVOICE_URL,"");
            mailValue.put(MailPlaceHolder.ATTACHMENTFILENAME,"Invoice"+invoiceno+".pdf");
            mailValue.put(MailPlaceHolder.ATTACHMENTFILEPATH,INVOICE_GENERATION_PATH+"Invoice"+invoiceno+".pdf");
            mailService.sendMerchantMonitoringAlert(MailEventEnum.GENERATING_INVOICE_BY_MERCHANT, mailValue);

        }
        else if(action.equals(REGENERATE))
        {
            mailValue.put(MailPlaceHolder.MSG,values.get("MSG"));
            mailValue.put(MailPlaceHolder.ATTACHMENTFILENAME,"Invoice"+invoiceno+".pdf");
            mailValue.put(MailPlaceHolder.ATTACHMENTFILEPATH,INVOICE_GENERATION_PATH+"Invoice"+invoiceno+".pdf");
            mailService.sendMerchantMonitoringAlert(MailEventEnum.GENERATING_INVOICE_BY_MERCHANT,mailValue);
        }
        else if(action.equals(CANCEL))
        {
            mailValue.put(MailPlaceHolder.MSG,values.get("MSG"));
            mailService.sendMerchantMonitoringAlert(MailEventEnum.CANCELED_INVOICE,mailValue);
        }


    }

    public static Hashtable getMemberDetails(String memberid) throws SystemError
    {
        Hashtable hash=new Hashtable();
        try{
            conn=Database.getConnection();
            String query="select login,company_name as companyname,sitename,contact_emails from members where memberid='"+memberid+"'";
            //log.debug(query);
            PreparedStatement pstmnt=conn.prepareStatement(query);
            ResultSet rs =pstmnt.executeQuery();


            ResultSetMetaData rsMetaData = rs.getMetaData();
            int count = rsMetaData.getColumnCount();


            while (rs.next())
            {

                for (int i = 1; i <= count; i++)
                {
                    if (rs.getString(i) != null)
                    {
                        hash.put(rsMetaData.getColumnLabel(i), rs.getString(i));

                    }
                }

            }




        }
        catch(Exception e)
        {
            log.error("Error Occured while Getting Merchant Details",e);
        }
        finally {
            Database.closeConnection(conn);
        }

        return hash;
    }

    public static void sendInvoiceMailRegenerate(String oldinvoiceno,String invoiceno) throws SystemError
    {
        log.debug("Entering sendInvoiceMailRegenerate");
        Date date = new Date();
        Functions functions = new Functions();
        String strDate = date.toString();
        InvoiceEntry invoiceEntry = new InvoiceEntry();

        String gst="";
        String company = ApplicationProperties.getProperty("COMPANY");
        String supportUrl = ApplicationProperties.getProperty("COMPANY_SUPPORT_URL");
        String supportFromAddress = ApplicationProperties.getProperty("SUPPORT_FROM_ADDRESS");
        String liveUrl = ApplicationProperties.getProperty("COMPANY_LIVE_URL");
        Hashtable values = (new InvoiceEntry()).getInvoiceDetails(invoiceno);
        InvoiceVO invoiceVO = new InvoiceVO();
        invoiceVO = invoiceEntry.getInvoiceConfigurationDetailsForGeneratedInvoice((String) values.get("memberid"));
        String lateFee = invoiceVO.getLatefee();
        String duedate="";
        String invoiceduedate="";
        if (functions.isValueNull(invoiceVO.getDuedate()))
        {
            duedate =invoiceVO.getDuedate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Calendar c = Calendar.getInstance();
            c.setTime(new Date()); // Now use today date.
            c.add(Calendar.DATE, (Integer.parseInt(duedate))); // Adding 5 days
            invoiceduedate = sdf.format(c.getTime());
        }
        invoiceVO.setInvoiceduedate(invoiceduedate);

        if (functions.isValueNull(lateFee) && !lateFee.equals("0"))
        {
            double amount = Double.parseDouble(invoiceVO.getLatefee());
            DecimalFormat twoPlaces = new DecimalFormat("0.00");
            invoiceVO.setLatefee(twoPlaces.format(amount));
        }
        else
        {
            invoiceVO.setLatefee("0.00");
        }

        if (!functions.isValueNull(invoiceVO.getGST()))
        {
            gst = "";
        }

        values.put("COMPANY", company);
        values.put("SUPPORT_URL", supportUrl);
        values.put("LIVE_URL", liveUrl);
        values.put("TIME", strDate);
        values.put("latefee",invoiceVO.getLatefee());
        values.put("islatefee",invoiceVO.getIslatefee());
        values.put("gst",gst);
        values.put("duedate",invoiceVO.getInvoiceduedate());
        values.put("address",values.get("address"));
        values.put("PROXYSCHEME",PROXYSCHEME);
        values.put("PROXYHOST",PROXYHOST);
        values.put("PROXYPORT",PROXYPORT);
        values.putAll(getMemberDetails((String) values.get("memberid")));
        values.put("MSG","This is a regenerated Invoice <BR> The Old Invoice No was "+oldinvoiceno+" and the new Invoice Number is "+invoiceno+"<BR> Kindly Note that the Previous Invoice has been voided so please do not Process It.  ");
        values.put("MSG2","<a href=\"<#PROXYSCHEME#>://<#PROXYHOST#>:<#PROXYPORT#>/transaction/PayInvoice?inv=<#invoiceno#>&ct=<#ctoken#>\" ><font color=\"#FFFFFF\" face=\"Verdana, Arial\" size=\"3\"><B>PAY NOW<B></font></a>");
        //values.put("MSG3","<form method=POST action=\"<#PROXYSCHEME#>://<#PROXYHOST#>:<#PROXYPORT#>/icici/servlet/PayInvoiceController?invoiceno=<#invoiceno#>&ctoken=<#ctoken#>\" > <input type=submit align=center value=\"Pay Now\"></form>");



        values.put("SIGNATURE",generateSignature((String)values.get("memberid")));

        sendHTMLMail(values, null, REGENERATE, oldinvoiceno, invoiceno);

        int remindercounter=0;
        remindercounter = Integer.parseInt((String)values.get("remindercounter"));
        remindercounter++;
        String query="update invoice set remindercounter ="+remindercounter+" ,status='mailsent' where invoiceno ="+invoiceno+";";
        try
        {
            conn=Database.getConnection();
            PreparedStatement p=conn.prepareStatement(query);
            p.executeUpdate();

        }catch(Exception e)
        {
            log.error("Exception occured while inserting data",e);
        }finally
        {
            Database.closeConnection(conn);
        }
    }

    public static void sendInvoiceMailCancel(String invoiceno , String cancleReason) throws SystemError
    {

        Date date = new Date();
        String strDate = date.toString();

        String company = ApplicationProperties.getProperty("COMPANY");
        String supportUrl = ApplicationProperties.getProperty("COMPANY_SUPPORT_URL");
        String supportFromAddress = ApplicationProperties.getProperty("SUPPORT_FROM_ADDRESS");
        String liveUrl = ApplicationProperties.getProperty("COMPANY_LIVE_URL");
        Hashtable values = (new InvoiceEntry()).getInvoiceDetails(invoiceno);
        InvoiceVO invoiceVO = new InvoiceVO();
        InvoiceEntry invoiceEntry = new InvoiceEntry();
        invoiceVO = invoiceEntry.getInvoiceConfigurationDetailsForGeneratedInvoice((String) values.get("memberid"));
        values.put("COMPANY", company);
        values.put("SUPPORT_URL", supportUrl);
        values.put("LIVE_URL", liveUrl);
        values.put("TIME", strDate);
        values.put("islatefee", invoiceVO.getIslatefee());
        values.put("latefee", invoiceVO.getLatefee());
        values.put("PROXYSCHEME", PROXYSCHEME);
        values.put("PROXYHOST",PROXYHOST);
        values.put("PROXYPORT",PROXYPORT);
        //values.put("MSG","Unfortunately, the following Invoice has been cancelled. <br>The reason for the cancellation was: "+cancleReason+"<br>Please find below the details of the transaction.");
        values.put("MSG",cancleReason);
        values.put("MSG2", "");
        //values.put("reason",cancleReason);

        values.put("SIGNATURE",generateSignature((String)values.get("memberid")));


        sendHTMLMail(values, null, CANCEL,null,invoiceno);




    }

    private static String getMailContents(File templateFile)
            throws IOException
    {
        FileReader fr = new FileReader(templateFile);
        BufferedReader br = new BufferedReader(fr);
        StringBuffer mailContents = new StringBuffer();
        String line = null;
        while ((line = br.readLine()) != null)
        {
            mailContents.append(line);
        }
        return mailContents.toString();
    }

    static String generateSignature(String memberid)
    {
        String signature="";
        try{

            Hashtable details=Template.getMemberTemplateDetails((String) memberid);
            if (details.get("PHONE1")!=null)
            {
                signature += "<table border=0 ><tr><td><p><font size=\"2\" face=\"Verdana\">Phone No : </p></td><td><p><font size=\"2\" face=\"Verdana\">" + details.get("PHONE1") + "</p></td></tr>";
            }
             if (details.get("PHONE2")!=null)
             {
                 signature += "<tr><td>&nbsp;</td><td><p><font size=\"2\" face=\"Verdana\">" + details.get("PHONE2") + "</p></td></tr>";
             }
            String em="";
            em=(String )details.get("EMAILS");
            if(em!=null){
                String emails[]=em.split(",");
                for(int count= 0;count<emails.length;count++)
                {
                    if(count ==0)
                    {
                        signature += "<tr><td><p><font size=\"2\" face=\"Verdana\">Email Address : </p></td><td><p><font size=\"2\" face=\"Verdana\">" + emails[count] + "</p></td></tr>";
                    } else{
                        signature+="<tr><td>&nbsp;</td><td><p><font size=\"2\" face=\"Verdana\">"+emails[count]+"</p></td></tr>";
                }}

            }


        }
        catch(SystemError e)
        {
            log.error("Exception Occured",e);
        }
        return signature;
    }

    public static void sendInvoiceMailForSplitInvoice(String invoiceno) throws SystemError
    {
        Date date = new Date();
        String strDate = date.toString();
        Functions functions = new Functions();
        InvoiceEntry invoiceEntry = new InvoiceEntry();


        String company = ApplicationProperties.getProperty("COMPANY");
        String supportUrl = ApplicationProperties.getProperty("COMPANY_SUPPORT_URL");
        String supportFromAddress = ApplicationProperties.getProperty("SUPPORT_FROM_ADDRESS");
        String liveUrl = ApplicationProperties.getProperty("COMPANY_LIVE_URL");
        Hashtable values = (new InvoiceEntry()).getSplitInvoiceDetails(invoiceno);
        // Hashtable value = (new InvoiceEntry()).getInvoiceConfigurationDetail(invoiceno);
        InvoiceVO invoiceVO = new InvoiceVO();
        invoiceVO = invoiceEntry.getInvoiceConfigurationDetails((String) values.get("memberid"));
        values.put("paymentterms",invoiceVO.getPaymentterms());
        values.put("COMPANY", company);
        values.put("SUPPORT_URL", supportUrl);
        values.put("LIVE_URL", liveUrl);
        values.put("latefee",invoiceVO.getLatefee());
        values.put("TIME", strDate);
        values.put("PROXYSCHEME",PROXYSCHEME);
        values.put("PROXYHOST",PROXYHOST);
        values.put("PROXYPORT", PROXYPORT);
        values.putAll(getMemberDetails((String) values.get("memberid")));
        log.debug("values-----"+values);

        if (functions.isValueNull((String) values.get("terminalid"))  )
        {
            values.put("TERMINALID", (String) values.get("terminalid"));
            values.put("MSG", "An Invoice Has Been Generated by <#companyname#> <br> The Website of <#companyname#> : <a target=\"_blank\" href=\"<#sitename#>\" ><#sitename#></a> ");
            values.put("MSG2", "<a href=\"<#PROXYSCHEME#>://<#PROXYHOST#>:<#PROXYPORT#>/transaction/PayInvoice?inv=<#invoiceno#>&t=<#terminal#>&ct=<#ctoken#>\" ><font color=\"#FFFFFF\" face=\"Verdana, Arial\" size=\"3\"><B>PAY NOW<B></font></a>");
            //  values.put("INVOICE_URL", "<a href=\"<#PROXYSCHEME#>://<#PROXYHOST#>:<#PROXYPORT#>/transaction/PayInvoiceController?invoiceno=<#invoiceno#>&terminalid=<#terminal#>&ctoken=<#ctoken#>\"</a>");
            //values.put("MSG3","<form method=POST action=\"<#PROXYSCHEME#>://<#PROXYHOST#>:<#PROXYPORT#>/icici/servlet/PayInvoiceController?invoiceno=<#invoiceno#>&ctoken=<#ctoken#>\" > <input type=submit align=center value=\"Pay Now\"></form>");
        }
        else
        {
            values.put("MSG", "An Invoice Has Been Generated by <#companyname#> <br> The Website of <#companyname#> : <a target=\"_blank\" href=\"<#sitename#>\" ><#sitename#></a> ");
            values.put("MSG2", "<a href=\"<#PROXYSCHEME#>://<#PROXYHOST#>:<#PROXYPORT#>/transaction/PayInvoice?inv=<#invoiceno#>&ct=<#ctoken#>\" ><font color=\"#FFFFFF\" face=\"Verdana, Arial\" size=\"3\"><B>PAY NOW<B></font></a>");
            //  values.put("INVOICE_URL", "<a href=\"<#PROXYSCHEME#>://<#PROXYHOST#>:<#PROXYPORT#>/transaction/PayInvoiceController?invoiceno=<#invoiceno#>&ctoken=<#ctoken#>\"</a>");

            //values.put("MSG3","<form method=POST action=\"<#PROXYSCHEME#>://<#PROXYHOST#>:<#PROXYPORT#>/icici/servlet/PayInvoiceController?invoiceno=<#invoiceno#>&ctoken=<#ctoken#>\" > <input type=submit align=center value=\"Pay Now\"></form>");
        }



        values.put("SIGNATURE",generateSignature((String)values.get("memberid")));


        String isCredential ="0";
        isCredential=(String)values.get("isCredential");

        sendHTMLMail(values, isCredential, NEW, null, invoiceno);


        int remindercounter=0;
        remindercounter = Integer.parseInt((String)values.get("remindercounter"));
        remindercounter++;
        String query="update invoice set remindercounter ="+remindercounter+" ,status='mailsent' where invoiceno ="+invoiceno+";";
        try
        {
            conn=Database.getConnection();
            PreparedStatement p=conn.prepareStatement(query);
            p.executeUpdate();

        }catch(Exception e)
        {
            log.error("Exception occured while inserting data",e);
        }finally
        {
            Database.closeConnection(conn);
        }
    }

    public Hashtable getStatusHash()
    {
        return statushash;
    }

    public SortedMap getSortedMap()
    {return sortedMap;


    }

    public void cancelInvoice(String invoiceno, String reason)
    {

        String query="update invoice set status='cancelled',cancelreason=? where invoiceno=?";

        try{
            //log.debug(query);
            conn=Database.getConnection();
            PreparedStatement ps= conn.prepareStatement(query);
            ps.setString(1,reason);
            ps.setString(2,invoiceno);
            ps.executeUpdate();
            ps.close();

            log.debug("update done");

        }catch(Exception e)
        {
            log.error("Error occured while Entering Canceling Transaction in data updation",e);
        }finally {

            Database.closeConnection(conn);
        }
        try{
            sendInvoiceMailCancel(invoiceno, reason);
        }
        catch(Exception e)
        {
            log.error("Error occured while Cancelling Transaction",e);

        }


    }

    public void cancelInvoiceForRESTAPI(String invoiceno, String reason)
    {

        String query="update invoice set status='cancelled',cancelreason=? where invoiceno=?";

        try{
            //log.debug(query);
            conn=Database.getConnection();
            PreparedStatement ps= conn.prepareStatement(query);
            ps.setString(1,reason);
            ps.setString(2,invoiceno);
            ps.executeUpdate();
            ps.close();

            log.debug("update done");

        }catch(Exception e)
        {
            log.error("Error occured while Entering Canceling Transaction in data updation",e);
        }finally {

            Database.closeConnection(conn);
        }
        try{
            sendInvoiceMailCancel(invoiceno, reason);
        }
        catch(Exception e)
        {
            log.error("Error occured while Cancelling Transaction",e);

        }


    }

    public void processInvoice(String invoiceno, int trackingid, String accountid)
    {

        String query="update invoice set status='processed',trackingid=?,accountid=? where invoiceno=?";

        try{

            conn=Database.getConnection();
            PreparedStatement ps= conn.prepareStatement(query);
            ps.setInt(1,trackingid);
            ps.setString(2,accountid);
            ps.setString(3, invoiceno);
            ps.executeUpdate();


        }catch(Exception e)
        {
            log.error("Error occured while Cancelling Transaction",e);
        }finally {
            Database.closeConnection(conn);
        }



    }

    public void regenerateInvoice(String invoiceno)
    {

        String query="update invoice set status='regenerated' where invoiceno="+invoiceno;

        try{

            conn=Database.getConnection();
            PreparedStatement ps= conn.prepareStatement(query);
            ps.executeUpdate();

            log.debug("regenerate invoice query-----" + ps);
        }catch(Exception e)
        {
            log.error("Error occured while Regenerating Invoice",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }

    public void insertInvoiceProduct(List<ProductList> listofProduct,int invoiceno)
    {
        try
        {
            conn = Database.getConnection();
            Functions functions = new Functions();

            for(ProductList productList : listofProduct)
            {
                if (functions.isValueNull(productList.getProductDescription()))
                {
                    String query = "insert into invoice_product_details(invoiceno,description,amount,quantity,subtotal,tax,unit) values (?,?,?,?,?,?,?)";
                    PreparedStatement p = conn.prepareStatement(query);
                    p.setInt(1, invoiceno);
                    p.setString(2, productList.getProductDescription());
                    p.setString(3, productList.getProductAmount());
                    p.setString(4, productList.getQuantity());
                    p.setString(5, productList.getProductTotal());
                    p.setString(6, productList.getTax());
                    p.setString(7, productList.getProductUnit());
                    p.executeUpdate();
                    log.debug("inserted  invoice_prod_detail successfully---" + p);
                }
            }
        }
        catch(Exception e)
        {
            log.error("Exception occur while insert data", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }

    public void insertDefaultInvoiceProduct(List<DefaultProductList> listofProduct,int memberid)
    {
        try
        {
            conn = Database.getConnection();

            for(DefaultProductList defaultProductList : listofProduct)
            {
                String query = "insert into default_productlist(memberid,productdesc,productamount,unit,tax) values (?,?,?,?,?)";
                PreparedStatement p = conn.prepareStatement(query);
                p.setInt(1, memberid);
                p.setString(2, defaultProductList.getProductDescription());
                p.setString(3, defaultProductList.getProductAmount());
                p.setString(4, defaultProductList.getUnit());
                p.setString(5, defaultProductList.getTax());
                p.executeUpdate();
                log.debug("inserted  invoice_prod_detail successfully---"+p);
            }
        }
        catch(Exception e)
        {
            log.error("Exception occur while insert data", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }

    public void insertUnit(List<UnitList> unitLists,int memberid)
    {
        try
        {
            conn = Database.getConnection();
            for(UnitList unitList : unitLists)
            {
                String query = "insert into product_unit(memberid,unit) values (?,?)";
                PreparedStatement p = conn.prepareStatement(query);
                p.setInt(1, memberid);
                p.setString(2, unitList.getDefaultunit());

                p.executeUpdate();
                log.debug("insert into product_unit successfully---"+p);
            }
        }
        catch(Exception e)
        {
            log.error("Exception occur while insert data", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }

    public void deleteUnit(String memberId)
    {
        try
        {
            conn = Database.getConnection();
            String query = "delete from product_unit where memberid = ?";
            PreparedStatement p = conn.prepareStatement(query);
            p.setString(1,memberId);

            p.executeUpdate();
            log.debug("delete query----"+p);
        }
        catch (SystemError e)
        {
            log.error("SystemError----",e);
        }
        catch (SQLException e)
        {
            log.error("SQLException----",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }
    //new

    public void deleteDefaultProductList(String memberId)
    {
        try
        {
            conn = Database.getConnection();
            String query = "delete from default_productlist where memberid = ?";
            PreparedStatement p = conn.prepareStatement(query);
            p.setString(1,memberId);

            p.executeUpdate();
            log.debug("delete product query----"+p);
        }
        catch (SystemError e)
        {
            log.error("SystemError----",e);
        }
        catch (SQLException e)
        {
            log.error("SQLException----",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }


    //new
   /* public List<InvoiceVO> TransactionDetailsForAPI(InvoiceVO invoiceVO)
    {
        log.debug("Entering listInvoices");

        Functions functions = new Functions();

        List<InvoiceVO> invoiceVOList = new ArrayList<InvoiceVO>();

        StringBuffer query = new StringBuffer();

        try
        {
            Connection conn=Database.getRDBConnection();
            query.append("SELECT COUNT(*) AS COUNT ,SUM(amount)AS AMOUNT ,SUM(captureamount)AS CAMOUNT,SUM(refundamount)AS RAMOUNT,SUM(chargebackamount) AS CBAMOUNT,STATUS,toid,currency FROM transaction_common WHERE ");

            if(functions.isValueNull(invoiceVO.getMemberid()));
            {
                query.append("toid ='" + invoiceVO.getMemberid() + "'");
            }
            if (functions.isValueNull(invoiceVO.getCurrency()))
            {
                query.append(" and currency = '" + invoiceVO.getCurrency() + "'");
            }

            query.append(" GROUP BY currency,STATUS ");


            log.debug("query..." + query);

            log.debug("Fatch records from transaction ");
            ResultSet queryRs = Database.executeQuery(query.toString(),conn);
            while (queryRs.next())
            {
                InvoiceVO invoiceVO1 = new InvoiceVO();


                invoiceVO1.setMemberid(queryRs.getString("toid"));
                invoiceVO1.setAmount(queryRs.getString("AMOUNT"));
                invoiceVO1.setCamount(queryRs.getString("CAMOUNT"));
                invoiceVO1.setRamount(queryRs.getString("RAMOUNT"));
                invoiceVO1.setCbamount(queryRs.getString("CBAMOUNT"));

                invoiceVO1.setCurrency(queryRs.getString("currency"));
                invoiceVO1.setTransactionStatus(queryRs.getString("STATUS"));
                invoiceVO1.setCount(queryRs.getString("count"));

                invoiceVOList.add(invoiceVO1);
            }
        }
        catch (SystemError e)
        {
            log.error("SystemError Exception leaving listTransactions" ,e);
        }
        catch (SQLException se)
        {
            log.error("SQL Exception leaving listTransactions" ,se);
        }
        finally
        {
            Database.closeConnection(conn);
        }

        log.debug("Leaving listTransactions  "+query.toString());
        return invoiceVOList;
    }*/

    public Hashtable insertInvoice(Hashtable hash,Boolean reg)
    {
        String oldinvoiceno="";
        Hashtable resulthash=new Hashtable();
        HashMap map = new HashMap();
        int invoiceno=0;

        int parentInvoiceNo = insertParentInvoice(hash);


        try{
            conn= Database.getConnection();
            String query="insert into invoice(parentinvoiceno,memberid,amount,orderid,orderdescription,customeremail,status,ctoken,dtstamp,redirecturl,currency,country,city,state,zip,street,telnocc,telno,custname,paymodeid,isCredential,username,pwd,question,answer,merchantIpAddress,terminalid,expirationPeriod,raisedBy,taxamount,langForInvoice) values(?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            log.debug(hash.get("memberid")+" "+hash.get("amount")+" "+hash.get("orderid")+" "+hash.get("orderdesc")+" "+hash.get("paymodeid")+" ");
            log.debug(hash.get("custemail")+" generated "+hash.get("ctoken")+" "+hash.get("accountid")+" "+hash.get("custname"));
            PreparedStatement p=conn.prepareStatement(query);
            p.setInt(1, parentInvoiceNo);
            p.setString(2,(String)hash.get("memberid"));
            p.setString(3, (String) hash.get("amount"));
            p.setString(4, (String) hash.get("orderid"));
            p.setString(5,(String)hash.get("orderdesc"));
            p.setString(6,(String)hash.get("custemail"));
            p.setString(7,"generated");
            p.setString(8,(String)hash.get("ctoken"));
            p.setString(9,(String)hash.get("redirecturl"));
            p.setString(10,(String)hash.get("currency"));
            p.setString(11,(String)hash.get("country"));
            p.setString(12,(String)hash.get("city"));
            p.setString(13,(String)hash.get("state"));
            p.setString(14,(String)hash.get("zipcode"));
            p.setString(15,(String)hash.get("address"));
            p.setString(16,(String)hash.get("phonecc"));
            p.setString(17,(String)hash.get("phone"));
            p.setString(18,(String)hash.get("custname"));
            p.setString(19,(String)hash.get("paymodeid"));
            p.setString(20,(String)hash.get("isCredential"));
            p.setString(21,(String)hash.get("username"));
            p.setString(22,(String)hash.get("password"));
            p.setString(23,(String)hash.get("question"));
            p.setString(24,(String)hash.get("answer"));
            p.setString(25,(String)hash.get("ipaddress"));
            p.setString(26,(String)hash.get("terminalid"));
            p.setString(27,(String)hash.get("expirationPeriod"));
            p.setString(28, (String) hash.get("useraccname"));
            p.setString(29, (String) hash.get("taxamount"));
            p.setString(30, (String) hash.get("langForInvoice"));
            int num = p.executeUpdate();
            if (num == 1)
            {
                ResultSet rs = p.getGeneratedKeys();

                if(rs!=null)
                {
                    while(rs.next())
                    {
                        invoiceno = rs.getInt(1);
                        log.debug("invoiceno:::::::::::::;"+invoiceno);
                    }
                }
            }
            log.debug("inserted  invoice successfully---"+p);

        }
        catch(Exception e)
        {
            log.error("Exception occur while insert data", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }


        resulthash.put("invoiceno",invoiceno);
        InvoiceVO invoiceVO = new InvoiceVO();
        invoiceVO= (InvoiceVO) hash.get("invoicevo");

        List<ProductList> listProducts = (List<ProductList>) hash.get("listofproducts");

        if (listProducts != null)
        {
            if (listProducts.size() > 0)
            {
                insertInvoiceProduct(listProducts, invoiceno);
            }
        }

        resulthash.put("invoiceno",invoiceno);

        //resulthash.put("trackingid",trackingid);


        try
        {

            if (invoiceVO.getIsemail().equals("Y"))
            {
                if (reg)
                {
                    sendInvoiceMailRegenerate(oldinvoiceno + "", invoiceno + "");
                }
                else
                {
                    System.out.println("inside this");
                    sendInvoiceMail(invoiceno + "");
                }
            }


        }
        catch(Exception e)
        {
            log.error("Exception in Sending Mail",e);
        }
        try
        {
            if (invoiceVO.getSmsactivation().equals("Y"))
            {
                if (invoiceVO.getIssms().equals("Y"))
                {

                    map.put("custname",hash.get("custname"));
                    map.put("orderdesc",hash.get("orderdesc"));
                    map.put("invoiceno",resulthash.get("invoiceno"));
                    map.put("currency",hash.get("currency"));
                    map.put("amount",hash.get("amount"));
                    map.put("phonecc",hash.get("phonecc"));
                    map.put("phone",hash.get("phone"));
                    map.put("orderid", hash.get("orderid"));
                    map.put("ctoken", hash.get("ctoken"));
                    sendInvoiceSMS(invoiceno + "", map);
                    // smsService.sendSMS(MailEventEnum.GENERATING_INVOICE_BY_MERCHANT, map);



                }
            }
        }
        catch (Exception se)

        {
            log.error("Exception in Sending SMS",se);
        }

        log.debug("Leaving insertinvoice");

        return resulthash;

    }

    public int insertParentInvoice(Hashtable hash)
    {
        int invoiceno=0;//trackingid=0;

        try
        {
            conn= Database.getConnection();
            String query="INSERT INTO parent_invoice(amount,memberid,currency,redirecturl,orderid,orderdescription,dtstamp,taxamount,raisedby) VALUES (?,?,?,?,?,?,unix_timestamp(now()),?,?)";
            log.debug(hash.get("memberid")+" "+hash.get("amount")+" "+hash.get("orderid")+" "+hash.get("orderdesc")+" ");
            PreparedStatement p=conn.prepareStatement(query);
            p.setString(1, (String) hash.get("amount"));
            p.setString(2,(String)hash.get("memberid"));
            p.setString(3,(String)hash.get("currency"));
            p.setString(4,(String)hash.get("redirecturl"));
            p.setString(5, (String) hash.get("orderid"));
            p.setString(6,(String)hash.get("orderdesc"));
            p.setString(7, (String) hash.get("taxamount"));
            p.setString(8, (String) hash.get("useraccname"));

            int num = p.executeUpdate();
            if (num == 1)
            {
                ResultSet rs = p.getGeneratedKeys();

                if(rs!=null)
                {
                    while(rs.next())
                    {

                        invoiceno = rs.getInt(1);
                    }
                }
            }
            log.debug("inserted parent invoice successfully---"+p);

        }
        catch(Exception e)
        {
            log.error("Exception occur while insert data", e);

        }
        finally
        {
            Database.closeConnection(conn);
        }


        return invoiceno;

    }

    public HashMap getInvoiceDetail(String invoiceno)
    {
        Functions functions = new Functions();
        log.debug("Entering getInvoiceDetails");
        HashMap hash=new HashMap();
        List<ProductList> listOfProducts = new ArrayList<ProductList>();
        String query = "select custname,invoiceno,trackingid,memberid,timestamp,amount,orderid,orderdescription as orderdesc,customeremail as custemail,remindercounter,status,ctoken,accountid,currency,redirecturl,country,city,state,zip as zipcode,street as address,telnocc as phonecc,telno as phone,cancelreason,paymodeid,isCredential,username,pwd,question,answer,terminalid,expirationPeriod,raisedBy,timestamp,dtstamp from invoice where invoiceno="+invoiceno;

        String productQuery = "select description,amount,quantity,subtotal from invoice_product_details where invoiceno="+invoiceno;

        try{
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();

            ResultSet rs = Database.executeQuery(query, conn);
            ResultSet rs1 = Database.executeQuery(productQuery, conn);

            ResultSetMetaData rsMetaData = rs.getMetaData();
            int count = rsMetaData.getColumnCount();

            int i = 0;
            while (rs.next())
            {

                for (i = 1; i <= count; i++)
                {
                    if (rs.getString(i) != null)
                    {

                        hash.put(rsMetaData.getColumnLabel(i), rs.getString(i));
                        if (null == rs.getString("terminalid") ||  "null".equals(rs.getString("terminalid")))
                        {
                            hash.put("terminalid", "");
                        }
                        else
                        {
                            hash.put("terminalid", rs.getString("terminalid").trim());
                        }
                    }
                }

                hash.put("date",rs.getString("timestamp").substring(0,rs.getString("timestamp").indexOf(" ")));
                hash.put("time",rs.getString("timestamp").substring(rs.getString("timestamp").indexOf(" "),rs.getString("timestamp").length()-1));

            }

            while (rs1.next())
            {

                ProductList productList = new ProductList();

                productList.setProductDescription(rs1.getString("description"));
                productList.setProductAmount(rs1.getString("amount"));
                productList.setQuantity(rs1.getString("quantity"));
                productList.setProductTotal(rs1.getString("subtotal"));

                listOfProducts.add(productList);
            }
            hash.put("listOfProducts",listOfProducts);
            log.debug("invoice query---" + query);
            log.debug("invoice product query---"+productQuery);

        }catch (Exception e)
        {
            log.error("Error occured while getting Invoice Details",e);
        }finally
        {
            Database.closeConnection(conn);
        }
        try{
            //conn=Database.getConnection();
            conn=Database.getRDBConnection();
            String trackingid= (String)hash.get("trackingid");
            String accountid= (String) hash.get("accountid");
            String transStatus="";
            if(trackingid!=null)
            {
                String tablename=Database.getTableName(GatewayAccountService.getGatewayAccount(accountid).getGateway());
                String fieldname= tablename.equalsIgnoreCase("transaction_icicicredit")?"icicitransid":"trackingid";
                ResultSet rs =conn.prepareStatement("select status from "+tablename+" where "+fieldname+"="+trackingid).executeQuery();
                rs.next();
                transStatus = rs.getString("status");
                hash.put("transStatus",transStatus);
            }
        }
        catch(Exception e)
        {
            log.error("DB Exception whille fatching Invoice---", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return hash;
    }

    public Hashtable getInvoiceDetails(String invoiceno)
    {
        Functions functions = new Functions();
        log.debug("Entering getInvoiceDetails");
        Hashtable hash=new Hashtable();
        List<ProductList> listOfProducts = new ArrayList<ProductList>();
        String query = "select custname,invoiceno,trackingid,memberid,timestamp,amount,orderid,orderdescription as orderdesc,customeremail as custemail,remindercounter,status,ctoken,accountid,currency,redirecturl,country,city,state,zip as zipcode,street as address,telnocc as phonecc,telno as phone,cancelreason,paymodeid,isCredential,username,pwd,question,answer,terminalid,expirationPeriod,raisedBy,timestamp,dtstamp,taxamount,langForInvoice from invoice where invoiceno="+invoiceno;

        String productQuery = "select description,amount,quantity,subtotal, unit, tax from invoice_product_details where invoiceno="+invoiceno;

        try{
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();

            ResultSet rs = Database.executeQuery(query, conn);
            ResultSet rs1 = Database.executeQuery(productQuery, conn);

            ResultSetMetaData rsMetaData = rs.getMetaData();
            int count = rsMetaData.getColumnCount();

            int i = 0;
            while (rs.next())
            {

                for (i = 1; i <= count; i++)
                {
                    if (rs.getString(i) != null)
                    {

                        hash.put(rsMetaData.getColumnLabel(i), rs.getString(i));
                        if (null == rs.getString("terminalid") ||  "null".equals(rs.getString("terminalid")))
                        {
                            hash.put("terminalid", "");
                        }
                        else
                        {
                            hash.put("terminalid", rs.getString("terminalid").trim());
                        }
                    }
                }

                hash.put("date",rs.getString("timestamp").substring(0,rs.getString("timestamp").indexOf(" ")));
                hash.put("time",rs.getString("timestamp").substring(rs.getString("timestamp").indexOf(" "),rs.getString("timestamp").length()-1));

            }

            while (rs1.next())
            {

                ProductList productList = new ProductList();

                productList.setProductDescription(rs1.getString("description"));
                productList.setProductAmount(rs1.getString("amount"));
                productList.setQuantity(rs1.getString("quantity"));
                productList.setProductTotal(rs1.getString("subtotal"));
                productList.setProductUnit(rs1.getString("unit"));
                productList.setTax(rs1.getString("tax"));

                listOfProducts.add(productList);
            }
            hash.put("listOfProducts",listOfProducts);
            log.debug("invoice query---" + query);
            log.debug("invoice product query---"+productQuery);

        }catch (Exception e)
        {
            log.error("Error occured while getting Invoice Details",e);
        }finally
        {
            Database.closeConnection(conn);
        }
        try{
            //conn=Database.getConnection();
            conn=Database.getRDBConnection();
            String trackingid= (String)hash.get("trackingid");
            String accountid= (String) hash.get("accountid");
            String transStatus="";
            if(trackingid!=null)
            {
                String tablename=Database.getTableName(GatewayAccountService.getGatewayAccount(accountid).getGateway());
                String fieldname= tablename.equalsIgnoreCase("transaction_icicicredit")?"icicitransid":"trackingid";
                ResultSet rs =conn.prepareStatement("select status from "+tablename+" where "+fieldname+"="+trackingid).executeQuery();
                rs.next();
                transStatus = rs.getString("status");
                hash.put("transStatus",transStatus);
            }
        }
        catch(Exception e)
        {
            log.error("DB Exception whille fatching Invoice---", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return hash;
    }

    public Hashtable getInvoiceDetailsForController(String invoiceno)
    {
        log.debug("Entering getInvoiceDetails");
        Hashtable hash=new Hashtable();
        String query = "select invoiceno,memberid as toid,timestamp,amount,orderid as description,orderdescription,customeremail as TMPL_emailaddr,remindercounter,status as invoicestatus,ctoken,redirecturl,currency,country as TMPL_COUNTRY,city as TMPL_city,state as TMPL_state,zip as TMPL_zip,street as TMPL_street,telnocc as TMPL_telnocc,telno as TMPL_telno,paymodeid,merchantIpAddress as ipaddr,terminalid as terminalid,expirationPeriod from invoice where invoiceno="+invoiceno;
        String transquery="";

        try
        {
            conn = Database.getConnection();
            ResultSet rs=Database.executeQuery(query, conn);
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int count = rsMetaData.getColumnCount();

            int i = 0;
            while (rs.next())
            {
                for (i = 1; i <= count; i++)
                {
                    if (rs.getString(i) != null)
                    {
                        hash.put(rsMetaData.getColumnLabel(i), rs.getString(i));
                    }
                }
                hash.put("date",rs.getString("timestamp").substring(0,rs.getString("timestamp").indexOf(" ")));
                hash.put("time",rs.getString("timestamp").substring(rs.getString("timestamp").indexOf(" "),rs.getString("timestamp").length()-1));
                hash.put("timestamp",rs.getString("timestamp"));
            }

            String q2="SELECT partnerName,logoName FROM partners WHERE partnerid=(SELECT partnerId FROM members WHERE memberid=?)";
            PreparedStatement p1=conn.prepareStatement(q2);
            p1.setString(1, (String) hash.get("toid"));
            ResultSet resultSet=p1.executeQuery();
            if(resultSet.next())
            {
                hash.put("totype",resultSet.getString("partnerName"));
                hash.put("logoName",resultSet.getString("logoName"));
            }

        }
        catch (Exception e)
        {
            log.error("Error occured while getting Invoice Details",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return hash;
    }

    public void getInvoiceDetailsForController(InvoiceVO invoiceVO,String invoiceno)
    {
        log.debug("Entering getInvoiceDetails");
        Functions functions = new Functions();
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        //String query = "select invoiceno,memberid as toid,timestamp,amount,orderid as description,orderdescription,customeremail as TMPL_emailaddr,remindercounter,status as invoicestatus,ctoken,redirecturl,currency,country as TMPL_COUNTRY,city as TMPL_city,state as TMPL_state,zip as TMPL_zip,street as TMPL_street,telnocc as TMPL_telnocc,telno as TMPL_telno,paymodeid,merchantIpAddress as ipaddr,terminalid as terminalid,expirationPeriod from invoice where invoiceno="+invoiceno;
        String query = "select * from invoice where invoiceno=?";


        PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();

        MerchantDAO merchantDAO = new MerchantDAO();
        try
        {
            conn = Database.getConnection();
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1,invoiceno);
            rs=preparedStatement.executeQuery();

            if (rs.next())
            {
                invoiceVO.setInvoiceno(rs.getString("invoiceno"));
                invoiceVO.setMemberid(rs.getString("memberid"));
                invoiceVO.setAmount(rs.getString("amount"));
                invoiceVO.setTimestamp(rs.getString("timestamp"));
                invoiceVO.setDate(rs.getString("timestamp").substring(0,rs.getString("timestamp").indexOf(" ")));
                invoiceVO.setTime(rs.getString("timestamp").substring(rs.getString("timestamp").indexOf(" "),rs.getString("timestamp").length()-1));
                invoiceVO.setDescription(rs.getString("orderid"));
                invoiceVO.setOrderDescription(rs.getString("orderdescription"));
                invoiceVO.setEmail(rs.getString("customeremail"));
                invoiceVO.setReminderCounter(rs.getString("remindercounter"));
                invoiceVO.setStatus(rs.getString("status"));
                invoiceVO.setCtoken(rs.getString("ctoken"));
                invoiceVO.setRedirecturl(rs.getString("redirecturl"));
                invoiceVO.setCurrency(rs.getString("currency"));
                invoiceVO.setCountry(rs.getString("country"));
                invoiceVO.setCity(rs.getString("city"));
                invoiceVO.setState(rs.getString("state"));
                invoiceVO.setStreet(rs.getString("street"));
                invoiceVO.setZip(rs.getString("zip"));
                invoiceVO.setTelCc(rs.getString("telnocc"));
                invoiceVO.setTelno(rs.getString("telno"));
                invoiceVO.setPaymodeid(rs.getString("paymodeid"));
                invoiceVO.setTerminalid(rs.getString("terminalid"));
                invoiceVO.setMerchantIpAddress(rs.getString("merchantIpAddress"));
                invoiceVO.setRemark(rs.getString("remark"));
                invoiceVO.setExpirationPeriod(rs.getString("expirationPeriod"));
                invoiceVO.setTrackingId(rs.getString("trackingid"));
            }

            if (functions.isValueNull(invoiceVO.getTrackingId()))
            {
                String query1 = "select status from transaction_common where trackingid=?";


                    PreparedStatement preparedStatement1 = conn.prepareStatement(query1);
                    preparedStatement1.setString(1, invoiceVO.getTrackingId());
                    ResultSet rs1 = preparedStatement1.executeQuery();

                    if (rs1.next())
                    {
                        invoiceVO.setTransactionStatus(rs1.getString("status"));
                    }

            }

            String q2="SELECT partnerName,logoName,checkoutInvoice FROM partners WHERE partnerid=(SELECT partnerId FROM members WHERE memberid=?)";
            PreparedStatement p1=conn.prepareStatement(q2);
            p1.setString(1,invoiceVO.getMemberid());
            ResultSet resultSet=p1.executeQuery();
            if(resultSet.next())
            {
                partnerDetailsVO.setPartnerName(resultSet.getString("partnerName"));
                partnerDetailsVO.setLogoName(resultSet.getString("logoName"));
                partnerDetailsVO.setCheckoutInvoice(resultSet.getString("checkoutInvoice"));
            }
            merchantDetailsVO = merchantDAO.getMemberDetails(invoiceVO.getMemberid());
            invoiceVO.setPartnerDetailsVO(partnerDetailsVO);
            invoiceVO.setMerchantDetailsVO(merchantDetailsVO);
        }
        catch (Exception e)
        {
            log.error("Error occured while getting Invoice Details",e);
        }
        finally
        {
            Database.closeConnection(conn);
            Database.closePreparedStatement(preparedStatement);
            Database.closeResultSet(rs);
        }

    }

    public void getPaymodeCardfromTerminal(String terminalid,InvoiceVO invoiceVO)
    {
        Connection conn = null;
        PreparedStatement pstmt1=null;
        ResultSet rs = null;
        MerchantTerminalVo merchantTerminalVo = new MerchantTerminalVo();
        try
        {
            String query = "select paymodeid,cardtypeid from member_account_mapping where terminalid=?";
            conn = Database.getConnection();
            pstmt1= conn.prepareStatement(query);
            pstmt1.setString(1,terminalid);
            rs = pstmt1.executeQuery();
            if(rs.next())
            {
                merchantTerminalVo.setPaymodeId(rs.getString("paymodeid"));
                merchantTerminalVo.setCardType(rs.getString("cardtypeid"));
            }
            invoiceVO.setMerchantTerminalVo(merchantTerminalVo);
        }
        catch (Exception e)
        {
            log.error("Error occured while getting Invoice Details",e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt1);
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }

    }

    public Hashtable getChecksumDetails(String toid)
    {

        Hashtable hash=new Hashtable();

        try{
            String query1= "select clkey ,icici,checksumalgo as algorithm from members where memberid=?";
            conn = Database.getConnection();
            PreparedStatement pstmt1= conn.prepareStatement(query1);
            pstmt1.setString(1,toid);
            ResultSet rs1 = pstmt1.executeQuery();

            ResultSetMetaData rsMetaData = rs1.getMetaData();
            int count = rsMetaData.getColumnCount();

            int i = 0;
            while (rs1.next())
            {
                for (i = 1; i <= count; i++)
                {
                    if (rs1.getString(i) != null)
                    {
                        hash.put(rsMetaData.getColumnLabel(i), rs1.getString(i));
                    }
                }
            }
        }catch (Exception e)
        {
            log.error("Error occured while getting Invoice Details",e);
        }finally
        {
            Database.closeConnection(conn);
        }

        return hash;
    }

    public Hashtable listInvoices(String memberid,String orderid,String orderdesc, String tdtstamp, String fdtstamp, String trackingid,String invoiceno, String status, int records, int pageno,Set<String > gatewaytypeSet) throws SystemError
    {
        log.debug("Entering listInvoices");

        Functions functions = new Functions();

        if(!Functions.isValidSQL(invoiceno) || !Functions.isValidSQL(orderdesc) ||!Functions.isValidSQL(trackingid)  || !Functions.isValidSQL(status))
        {
            throw new SystemError("In valid data received");

        }

        // log.debug("trackingid===1===//=in transactionENTRY"+trackingid);
        Hashtable hash = null;

        String fields = "";

        StringBuffer query = new StringBuffer();
        //Encoding for SQL Injection check

//        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
//        status = ESAPI.encoder().encodeForSQL(me,status);
//        fdtstamp= ESAPI.encoder().encodeForSQL(me,fdtstamp);
//        tdtstamp=ESAPI.encoder().encodeForSQL(me,tdtstamp);
//        orderid=ESAPI.encoder().encodeForSQL(me, orderid);
//        orderdesc=ESAPI.encoder().encodeForSQL(me,orderdesc);
//        invoiceno=ESAPI.encoder().encodeForSQL(me,invoiceno);
//        trackingid=ESAPI.encoder().encodeForSQL(me,trackingid);

        int start = 0; // start index
        int end = 0; // end index
        start = (pageno - 1) * records;
        end = records;

        fields = "invoiceno,memberid,trackingid,status,orderid,orderdescription,amount,dtstamp,accountid,expirationPeriod,raisedBy,timestamp";

        TerminalVO terminalVO = new TerminalVO();
        TerminalManager terminalManager = new TerminalManager();
        try
        {
            Connection conn=Database.getConnection();

            query.append("select " + fields + " from invoice"  + " where");

            if (functions.isValueNull(fdtstamp)){
                query.append(" dtstamp >= ").append( fdtstamp);}
            if (functions.isValueNull(tdtstamp)){
                query.append(" and dtstamp <= ").append( tdtstamp);}
            if(functions.isValueNull(memberid)){
                query.append(" and memberid ='").append( memberid).append( "'");}
            if (functions.isValueNull(status))
            {
                if (status.equalsIgnoreCase("expired")){
                    query.append(" and status in ('generated','mailsent') and DATE_ADD(FROM_UNIXTIME(dtstamp),INTERVAL expirationPeriod HOUR)<NOW()");}
                else if (status.equalsIgnoreCase("mailsent") || status.equalsIgnoreCase("generated")){
                    query.append(" and status in ('"+status+"') and DATE_ADD(FROM_UNIXTIME(dtstamp),INTERVAL expirationPeriod HOUR)>NOW()");}
                else{
                    query.append(" and status='" + status + "'");}
            }
            if (functions.isValueNull(orderid)){
                query.append(" and orderid ='" + orderid + "'");}
            if (functions.isValueNull(trackingid)){
                query.append(" and trackingid='" + trackingid + "'");}
            if (functions.isValueNull(orderdesc)){
                query.append(" and orderdescription='" + orderdesc + "'");}
            if (functions.isValueNull(invoiceno)){
                query.append(" and invoiceno=").append( invoiceno);}
                /*if (functions.isValueNull(terminalid))
                    query.append(" and accountid=" + terminalVO.getAccountId()+" and paymodeid=" +terminalVO.getPaymodeId()+"and cardtype=" +terminalVO.getCardType());*/



            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");

            query.append(" order by dtstamp DESC");

            query.append(" limit " + start + "," + end);

            log.debug("query admin..."+query);

            log.debug("Fatch records from transaction ");


            //log.debug("Count query =="+countquery.toString());

            hash = Database.getHashFromResultSetForInvoiceEntry(Database.executeQuery(query.toString(), conn));
            //log.debug("hash===1===//=in transactionENTRY"+hash);
            ResultSet rs = Database.executeQuery(countquery.toString(), conn);

            int totalrecords = 0;
            if (rs.next())
            {
                totalrecords = rs.getInt(1);
            }

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
            {
                hash.put("records", "" + (hash.size() - 2));
            }
        }
        catch (SQLException se)
        {   log.error("SQL Exception leaving listTransactions" ,se);
            throw new SystemError(se.toString());
        }finally
        {
            Database.closeConnection(conn);
        }

        log.debug("Leaving listTransactions  "+query.toString());
        return hash;
    }

    public Hashtable listInvoicesForMembers(String memberid,String orderid,String orderdesc, String tdtstamp, String fdtstamp, String trackingid,String invoiceno, String status, TerminalVO terminalVO,String terminalid, int records, int pageno,Set<String > gatewaytypeSet, String role, String useraccname) throws SystemError
    {
        log.debug("Entering listInvoices");

        Functions functions = new Functions();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = new Date();

        if(!Functions.isValidSQL(invoiceno) || !Functions.isValidSQL(orderdesc) ||!Functions.isValidSQL(trackingid)  || !Functions.isValidSQL(status))
        {
            throw new SystemError("In valid data received");

        }

        // log.debug("trackingid===1===//=in transactionENTRY"+trackingid);
        Hashtable hash = null;

        String fields = "";

        StringBuilder query = new StringBuilder();
        //Encoding for SQL Injection check

        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        status = ESAPI.encoder().encodeForSQL(me,status);
        fdtstamp= ESAPI.encoder().encodeForSQL(me,fdtstamp);
        tdtstamp=ESAPI.encoder().encodeForSQL(me,tdtstamp);
        orderid=ESAPI.encoder().encodeForHTML(orderid);
        orderdesc=ESAPI.encoder().encodeForSQL(me,orderdesc);
        invoiceno=ESAPI.encoder().encodeForSQL(me,invoiceno);
        trackingid=ESAPI.encoder().encodeForSQL(me, trackingid);

        //terminalid=ESAPI.encoder().encodeForSQL(me, terminalid);

        int start = 0; // start index
        int end = 0; // end index
        start = (pageno - 1) * records;
        end = records;

        fields = "invoiceno,memberid,trackingid,status,orderid,orderdescription,amount,currency,dtstamp,timestamp,accountid,expirationPeriod,paymodeid,raisedBy";

        TerminalManager terminalManager = new TerminalManager();
        StringBuffer countquery = new StringBuffer();
        try
        {
            //Connection conn=Database.getConnection();
            Connection conn=Database.getRDBConnection();

            query.append("select " + fields + " from invoice"  + " where");
            countquery.append("select count(*) from invoice where ");

            if(role.equalsIgnoreCase("submerchant"))
            {
                //query.append(" memberid ='" + memberid + " and raisedBy='"+useraccname+"'");
                query.append(" memberid = ");
                query.append(memberid);
                query.append(" and raisedBy= '");
                query.append(useraccname+"'");
                countquery.append(" memberid ='" + memberid + "' and raisedBy='"+useraccname+"'");
            }
            else
            {
                query.append(" memberid ='" + memberid + "'");
                countquery.append(" memberid ='" + memberid + "'");
            }

            if (functions.isValueNull(tdtstamp))
            {
                query.append(" and dtstamp <= ").append( tdtstamp);
                countquery.append(" and dtstamp <= ").append( tdtstamp);
            }
            if (functions.isValueNull(fdtstamp))
            {
                query.append(" and dtstamp >= ").append( fdtstamp);
                countquery.append(" and dtstamp >= ").append( fdtstamp);
            }


            /*if(functions.isValueNull(memberid))
            {
                query.append(" and memberid ='" + memberid + "'");
                countquery.append(" and memberid ='" + memberid + "'");
            }*/
            if (functions.isValueNull(status))
            {
                if (status.equalsIgnoreCase("expired"))
                {
                    query.append(" and status in ('generated','mailsent') and DATE_ADD(FROM_UNIXTIME(dtstamp),INTERVAL expirationPeriod HOUR)<NOW()");
                    countquery.append(" and status in ('generated','mailsent') and DATE_ADD(FROM_UNIXTIME(dtstamp),INTERVAL expirationPeriod HOUR)<NOW()");
                }
                else if (status.equalsIgnoreCase("mailsent") || status.equalsIgnoreCase("mailsent"))
                {
                    query.append(" and status in ('"+status+"') and DATE_ADD(FROM_UNIXTIME(dtstamp),INTERVAL expirationPeriod HOUR)>NOW()");
                    countquery.append(" and status in ('"+status+"') and DATE_ADD(FROM_UNIXTIME(dtstamp),INTERVAL expirationPeriod HOUR)>NOW()");
                }
                else
                {
                    query.append(" and status='" + status + "'");
                    countquery.append(" and status='" + status + "'");
                }
            }
            if (functions.isValueNull(orderid))
            {
                query.append(" and orderid ='" + orderid + "'");
                countquery.append(" and orderid ='" + orderid + "'");
            }
            if (functions.isValueNull(trackingid))
            {
                query.append(" and trackingid='" + trackingid + "'");
                countquery.append(" and trackingid='" + trackingid + "'");
                log.debug("TrackingId for Invoice----"+trackingid);
            }
            if (functions.isValueNull(orderdesc))
            {
                query.append(" and orderdescription='" + orderdesc + "'");
                countquery.append(" and orderdescription='" + orderdesc + "'");
            }
            if (functions.isValueNull(invoiceno))
            {
                query.append(" and invoiceno=" ).append( invoiceno);
                countquery.append(" and invoiceno=").append( invoiceno);
            }
            /*if (functions.isValueNull(terminalid))
            {
                query.append(" and accountid=" + terminalVO.getAccountId() + " and paymodeid=" + terminalVO.getPaymodeId());
                countquery.append(" and accountid=" + terminalVO.getAccountId() + " and paymodeid=" + terminalVO.getPaymodeId());
            }*/
            log.debug("query..." + query);



            //query.append(" order by dtstamp ,trackingid ASC");
            query.append(" order by dtstamp desc");

            query.append(" limit " + start + "," + end);



            log.debug("Fatch records from transaction ");


            log.debug("Count query =="+countquery.toString());
            log.debug(" query =="+query);

            hash = Database.getHashFromResultSetForInvoiceEntry(Database.executeQuery(query.toString(), conn));
            //log.debug("hash===1===//=in transactionENTRY"+hash);
            ResultSet rs = Database.executeQuery(countquery.toString(), conn);

            int totalrecords = 0;
            if (rs.next())
            {
                totalrecords = rs.getInt(1);
            }

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
            {
                hash.put("records", "" + (hash.size() - 2));
            }
        }
        catch (SQLException se)
        {   log.error("SQL Exception leaving listTransactions" ,se);
            throw new SystemError(se.toString());
        }finally
        {
            Database.closeConnection(conn);
        }

        log.debug("Leaving listTransactions  "+query.toString());
        return hash;
    }

    public String getTransactionstatus(String orderid)
    {
        Connection conn = null;
        String status="";
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            PreparedStatement pstmt=null;
            String query=("SELECT status FROM transaction_common WHERE description=?");
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, orderid);
            ResultSet res = pstmt.executeQuery();


            if(res.next())
            {
                status = res.getString("status");
            }

        }
        catch (Exception e)
        {
            log.error("Exception", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return status ;


    }

    public List<InvoiceVO> listInvoicesForAPI(InvoiceVO invoiceVO, String fdtstamp, String tdtstamp)
    {
        log.debug("Entering listInvoices");

        Functions functions = new Functions();
        List<ProductList> listOfProducts = new ArrayList<ProductList>();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar=Calendar.getInstance();


        List<InvoiceVO> invoiceVOList = new ArrayList<InvoiceVO>();

        StringBuffer query = new StringBuffer();

        int start = 0; // start index
        int end = 0; // end index
        if (functions.isValueNull(String.valueOf(invoiceVO.getPaginationVO().getPageNo())) && invoiceVO.getPaginationVO().getPageNo() != 0)
        {
            start = (invoiceVO.getPaginationVO().getPageNo() - 1) * invoiceVO.getPaginationVO().getRecordsPerPage();
        }
        else
        {
            start = invoiceVO.getPaginationVO().getPageNo();
        }

        if (invoiceVO.getPaginationVO().getRecordsPerPage() == 0)
        {
            end = 15;
        }
        else
        {
            end = invoiceVO.getPaginationVO().getRecordsPerPage();
        }
        try
        {
            conn=Database.getRDBConnection();
            query.append("SELECT i.invoiceno,i.trackingid,i.memberid,i.amount,i.custname,i.ctoken,i.currency,i.cancelreason,i.redirecturl,i.orderid,i.orderdescription,i.country,i.city,i.state,i.zip,i.telnocc,i.telno,i.street,i.customeremail,i.status,i.accountid,i.raisedBy,i.expirationPeriod,i.timestamp,i.dtstamp,i.terminalid,t.status as transactionstatus,t.refundamount FROM invoice AS i LEFT JOIN transaction_common AS t ON i.trackingid=t.trackingid WHERE ");

            if(functions.isValueNull(invoiceVO.getMemberid()))
            {
                query.append(" i.memberid ='" + invoiceVO.getMemberid() + "'");
            }
            if (functions.isValueNull(invoiceVO.getUserName()))
            {
                query.append(" and raisedBy ='"+invoiceVO.getUserName()+"'");
            }
            if (functions.isValueNull(fdtstamp))
            {
                query.append(" and i.dtstamp >= ").append( fdtstamp);

            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" and i.dtstamp <= ").append( tdtstamp);

            }
            query.append(" order by i.invoiceno DESC");
            log.debug("query..."+query);

            //  query.append(" order by m ASC");

            query.append(" limit " + start + "," + end);

            log.debug("Fatch records from transaction ");
            ResultSet queryRs = Database.executeQuery(query.toString(),conn);
            while (queryRs.next())
            {
                InvoiceVO invoiceVO1 = new InvoiceVO();

                invoiceVO1.setInvoiceno(queryRs.getString("invoiceno"));
                invoiceVO1.setTrackingId(queryRs.getString("trackingid"));
                invoiceVO1.setMemberid(queryRs.getString("memberid"));
                invoiceVO1.setAmount(queryRs.getString("amount"));
                invoiceVO1.setRefundAmount(queryRs.getString("refundamount"));
                invoiceVO1.setCurrency(queryRs.getString("currency"));
                invoiceVO1.setRedirecturl(queryRs.getString("redirecturl"));
                invoiceVO1.setDescription(queryRs.getString("orderid"));
                invoiceVO1.setOrderDescription(queryRs.getString("orderdescription"));
                invoiceVO1.setCountry(queryRs.getString("country"));
                invoiceVO1.setCity(queryRs.getString("city"));
                invoiceVO1.setState(queryRs.getString("state"));
                invoiceVO1.setZip(queryRs.getString("zip"));
                invoiceVO1.setTelCc(queryRs.getString("telnocc"));
                invoiceVO1.setTelno(queryRs.getString("telno"));
                invoiceVO1.setStreet(queryRs.getString("street"));
                invoiceVO1.setEmail(queryRs.getString("customeremail"));
                if (queryRs.getString("status").equals("mailsent"))
                {
                    int expirationPeriod=queryRs.getInt("expirationPeriod");
                    String createdDate=queryRs.getString("timestamp");
                    calendar.setTime(simpleDateFormat.parse(createdDate));
                    calendar.add(Calendar.DATE, expirationPeriod);
                    Date expiredDate = calendar.getTime();
                    if (new Date().after(expiredDate)){
                        invoiceVO1.setStatus("expired");
                    }
                    else
                    {
                        invoiceVO1.setStatus(queryRs.getString("status"));
                    }
                }
                else
                {
                    invoiceVO1.setStatus(queryRs.getString("status"));
                }
                invoiceVO1.setTransactionStatus(queryRs.getString("transactionstatus"));
                invoiceVO1.setAccountid(queryRs.getString("accountid"));
                invoiceVO1.setTimestamp(queryRs.getString("timestamp"));
                //invoiceVO1.setReminderCounter(queryRs.getString("remindercounter"));
                invoiceVO1.setRaisedby(queryRs.getString("raisedBy"));
                invoiceVO1.setDate(queryRs.getString("timestamp"));
                invoiceVO1.setCustName(queryRs.getString("custname"));
                //new

                invoiceVO1.setExpirationPeriod(queryRs.getString("expirationPeriod"));
                invoiceVO1.setCancelReason(queryRs.getString("cancelreason"));
                invoiceVO1.setDtstamp(queryRs.getString("dtstamp"));
                invoiceVO1.setTerminalid(queryRs.getString("terminalid"));
                invoiceVO1.setCtoken(queryRs.getString("ctoken"));

                if (functions.isValueNull(invoiceVO1.getTerminalid()) && !invoiceVO1.getTerminalid().equalsIgnoreCase("0"))
                {
                    invoiceVO1.setTransactionUrl(PROXYSCHEME + "://" + PROXYHOST +  "/transaction/PayInvoice?inv=" + invoiceVO1.getInvoiceno() +"&t=" + invoiceVO1.getTerminalid() + "&ct=" + invoiceVO1.getCtoken());
                }
                else
                {
                    invoiceVO1.setTransactionUrl(PROXYSCHEME+ "://" +PROXYHOST+  "/transaction/PayInvoice?inv=" +invoiceVO1.getInvoiceno()+ "&ct=" +invoiceVO1.getCtoken());
                }

                //  System.out.println("invoice--->"+invoiceVO1.getTransactionStatus());
                listOfProducts = getItemList(invoiceVO1.getInvoiceno());
                invoiceVO1.setProductList(listOfProducts);
                invoiceVOList.add(invoiceVO1);


            }
        }
        catch (SystemError e)
        {
            log.error("SystemError Exception leaving listTransactions" ,e);
        }
        catch (SQLException se)
        {
            log.error("SQL Exception leaving listTransactions" ,se);
        }
        catch (ParseException e)
        {
            log.error("Parse Exception leaving listTransactions" ,e);
        }
        finally
        {
            Database.closeConnection(conn);
        }

        log.debug("Leaving listTransactions  "+query.toString());
        return invoiceVOList;
    }

    //NEW
    public List<InvoiceVO> InvoiceDetailsForAPI(InvoiceVO invoiceVO,String fdtstamp, String tdtstamp)
    {
        log.debug("Entering listInvoices");

        Functions functions = new Functions();

        List<InvoiceVO> invoiceVOList = new ArrayList<InvoiceVO>();

        StringBuffer query = new StringBuffer();

        try
        {
            conn=Database.getRDBConnection();
            query.append("SELECT COUNT(*) as count ,SUM(i.`amount`)AS AMOUNT,t.`status` AS STATUS , i.`currency` AS CURRENCY, i.memberid,i.timestamp,i.dtstamp FROM invoice AS i LEFT JOIN transaction_common AS t ON i.trackingid=t.trackingid WHERE ");

            if(functions.isValueNull(invoiceVO.getMemberid()))
            {
                query.append("i.memberid ='" + invoiceVO.getMemberid() + "'");
            }
            if (functions.isValueNull(fdtstamp))
            {
                query.append(" and i.dtstamp >= ").append( fdtstamp);

            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" and i.dtstamp <= ").append( tdtstamp);

            }
            if (functions.isValueNull(invoiceVO.getCurrency()))
            {
                query.append(" and i.currency = '" + invoiceVO.getCurrency() + "'");
            }

            query.append(" GROUP BY i.currency,t.status ");


            log.debug("query..." + query);

            log.debug("Fatch records from transaction ");
            ResultSet queryRs = Database.executeQuery(query.toString(),conn);
            while (queryRs.next())
            {
                InvoiceVO invoiceVO1 = new InvoiceVO();


                invoiceVO1.setMemberid(queryRs.getString("memberid"));
                invoiceVO1.setAmount(queryRs.getString("AMOUNT"));
                invoiceVO1.setCurrency(queryRs.getString("CURRENCY"));
                invoiceVO1.setTransactionStatus(queryRs.getString("STATUS"));
                invoiceVO1.setCount(queryRs.getString("count"));
                invoiceVO1.setDtstamp(queryRs.getString("dtstamp"));
                invoiceVO1.setDate(queryRs.getString("timestamp"));

                invoiceVOList.add(invoiceVO1);
            }
        }
        catch (SystemError e)
        {
            log.error("SystemError Exception leaving listTransactions" ,e);
        }
        catch (SQLException se)
        {
            log.error("SQL Exception leaving listTransactions" ,se);
        }
        finally
        {
            Database.closeConnection(conn);
        }

        log.debug("Leaving listTransactions  "+query.toString());
        return invoiceVOList;
    }

    public Hashtable getGatewayHash(String memberid,String accountid)
    {
        Hashtable gatewayhash = new Hashtable();
        PreparedStatement pstmt2=null;
        ResultSet rs2=null;

        if(accountid==null || accountid.equals("") || accountid.equals("null"))
        {
            try
            {
                //conn=Database.getConnection();
                conn=Database.getRDBConnection();
                String query= "select accountid from member_account_mapping where memberid=?";
                pstmt2= conn.prepareStatement(query);
                pstmt2.setString(1,memberid);
                rs2 = pstmt2.executeQuery();
                while (rs2.next())
                {

                    String gateway = GatewayAccountService.getGatewayAccount(rs2.getString("accountid")).getGateway();
                    String displayname = GatewayAccountService.getGatewayAccount(rs2.getString("accountid")).getDisplayName();
                    gatewayhash.put(gateway,displayname);
                }
                log.debug("query00----"+pstmt2);
            }
            catch (Exception se)
            {
                log.error("SQLException is occure",se);

            }
            finally{
                Database.closeResultSet(rs2);
                Database.closePreparedStatement(pstmt2);
                Database.closeConnection(conn);
            }
        }
        else
        {
            String gateway = GatewayAccountService.getGatewayAccount(accountid).getGateway();
            String displayname = GatewayAccountService.getGatewayAccount(accountid).getDisplayName();
            gatewayhash.put(gateway,displayname);
        }


        return gatewayhash;
    }

    public  Hashtable getPayModeList(String memeberId)
    {
        Hashtable paymodelist = new Hashtable();
        try
        {
            conn=Database.getRDBConnection();
            String query2= "select distinct paymodeid from member_account_mapping where memberid=?";
            PreparedStatement pstmt2= conn.prepareStatement(query2);
            pstmt2.setString(1,memeberId);
            ResultSet rs2 = pstmt2.executeQuery();
            while (rs2.next())
            {
                String paymodeid = rs2.getString("paymodeid");
                paymodelist.put(paymodeid,GatewayAccountService.getPaymentTypes(paymodeid));


            }

            log.debug("paymodelist----"+pstmt2);
        }
        catch(Exception e)
        {
            log.error("Exception occur",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return paymodelist;

    }

    public  Hashtable getCardTypeList(String memeberId)
    {
        Hashtable cardtypelist = new Hashtable();
        try
        {
            conn=Database.getConnection();

            String query3= "select distinct cardtypeid from member_account_mapping where memberid=?";
            PreparedStatement pstmt3= conn.prepareStatement(query3);
            pstmt3.setString(1,memeberId);
            ResultSet rs3 = pstmt3.executeQuery();
            while (rs3.next())
            {
                String cardtypeid =  rs3.getString("cardtypeid");
                if("0"!=cardtypeid)
                {
                    cardtypelist.put(cardtypeid, GatewayAccountService.getCardType(cardtypeid));
                }


            }

        }
        catch(Exception e)
        {
            log.error("Exception occur",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }


        return cardtypelist;

    }

    public  Hashtable getCardTypeList(String memeberId, String paymentid )
    {
        Hashtable cardtypelist = new Hashtable();
        try
        {
            conn=Database.getConnection();

            String query3= "select distinct cardtypeid from member_account_mapping where memberid=? and paymodeid=?";
            PreparedStatement pstmt3= conn.prepareStatement(query3);
            pstmt3.setString(1,memeberId);
            pstmt3.setString(2,paymentid);
            ResultSet rs3 = pstmt3.executeQuery();
            while (rs3.next())
            {
                String cardtypeid =  rs3.getString("cardtypeid");
                if("0"!=cardtypeid)
                {
                    cardtypelist.put(cardtypeid, GatewayAccountService.getCardType(cardtypeid));
                }


            }

        }
        catch(Exception e)
        {
            log.error("Exception occur",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }


        return cardtypelist;

    }

    public InvoiceVO getInvoiceDetails(InvoiceVO invoiceVO)
    {
        log.debug("Entering getInvoiceDetails");
        Functions functions = new Functions();
        StringBuilder query = new StringBuilder("select custname,invoiceno,trackingid,memberid,timestamp,amount,orderid,orderdescription as orderdesc,customeremail as custemail,remindercounter,status,ctoken,accountid,currency,redirecturl,country,city,state,zip as zipcode,street as address,telnocc as phonecc,telno as phone,cancelreason,paymodeid,isCredential,username,pwd,question,answer,terminalid,raisedby from invoice where memberid=").append(invoiceVO.getMemberid());
        if(functions.isValueNull(invoiceVO.getInvoiceno()))
        {
            query.append(" and invoiceno ='"+invoiceVO.getInvoiceno()+ "'");
        }
        if(functions.isValueNull(invoiceVO.getDescription()))
        {
            query.append(" and orderid ='"+invoiceVO.getDescription()+ "'");
        }
        try
        {
            log.debug("qry in InvoiceEntry-->"+query.toString());
            conn = Database.getConnection();
            ResultSet rs=Database.executeQuery(query.toString(), conn);
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int count = rsMetaData.getColumnCount();
            int i = 0;
            while (rs.next())
            {
                invoiceVO.setDate(rs.getString("timestamp").substring(0,rs.getString("timestamp").indexOf(" ")));
                invoiceVO.setTime(rs.getString("timestamp").substring(rs.getString("timestamp").indexOf(" "), rs.getString("timestamp").length() - 1));
                invoiceVO.setInvoiceno(rs.getString("invoiceno"));
                invoiceVO.setStatus(rs.getString("status"));
                invoiceVO.setAmount(rs.getString("amount"));
                invoiceVO.setAccountid(rs.getString("accountid"));
                invoiceVO.setCurrency(rs.getString("currency"));
                invoiceVO.setDescription(rs.getString("orderid"));
                invoiceVO.setOrderDescription(rs.getString("orderdesc"));
                invoiceVO.setCountry(rs.getString("country"));
                invoiceVO.setState(rs.getString("state"));
                invoiceVO.setCity(rs.getString("city"));
                invoiceVO.setZip(rs.getString("zipcode"));
                invoiceVO.setTelno(rs.getString("phone"));
                invoiceVO.setTelCc(rs.getString("phonecc"));
                invoiceVO.setStreet(rs.getString("address"));
                invoiceVO.setEmail(rs.getString("custemail"));
                invoiceVO.setCustName(rs.getString("custname"));
                invoiceVO.setIsCredential(rs.getString("isCredential"));
                invoiceVO.setUserName(rs.getString("username"));
                invoiceVO.setPwd(rs.getString("pwd"));
                invoiceVO.setQuestion(rs.getString("question"));
                invoiceVO.setAnswer(rs.getString("answer"));
                invoiceVO.setTerminalid(rs.getString("terminalid"));
                invoiceVO.setRaisedby(rs.getString("raisedby"));
                invoiceVO.setTrackingId(rs.getString("trackingid"));
                invoiceVO.setAccountid(rs.getString("accountid"));
                invoiceVO.setTimestamp(rs.getString("timestamp"));
                //invoiceVO.setIsapp(rs.getString("isApp"));
                //invoiceVO.setPaymentterms(rs.getString("paymentterms"));
                //invoiceVO.setSmsactivation(rs.getString("smsactivation"));
                //invoiceVO.setIsduedate(rs.getString("isduedate"));
                //invoiceVO.setIslatefee(rs.getString("islatefee"));
                //invoiceVO.setDuedate(rs.getString("duedate"));
                //invoiceVO.setLatefee(rs.getString("latefee"));
            }
        }
        catch(Exception e)
        {
            log.error("Exception::::",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return invoiceVO;
    }

    public InvoiceVO getInvoiceDetailsByInvoiceid(String memberId, String invoiceId, InvoiceVO invoiceVO) throws PZDBViolationException
    {
        log.debug("Entering getInvoiceDetails");
        StringBuffer query = new StringBuffer();
        try
        {
            log.debug("qry in InvoiceEntry-->"+query.toString());
            conn = Database.getConnection();
            query.append("select custname,invoiceno,trackingid,memberid,timestamp,amount,orderid,orderdescription as orderdesc,customeremail as custemail,remindercounter,status,ctoken,accountid,currency,redirecturl,country,city,state,zip as zipcode,street as address,telnocc as phonecc,telno as phone,cancelreason,paymodeid,isCredential,username,pwd,question,answer,terminalid,raisedby,expirationPeriod from invoice where memberid=? AND invoiceno=?");
            PreparedStatement pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1,memberId);
            pstmt.setString(2, invoiceId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                invoiceVO.setDate(rs.getString("timestamp").substring(0,rs.getString("timestamp").indexOf(" ")));
                invoiceVO.setTime(rs.getString("timestamp").substring(rs.getString("timestamp").indexOf(" "), rs.getString("timestamp").length() - 1));
                invoiceVO.setInvoiceno(rs.getString("invoiceno"));
                invoiceVO.setStatus(rs.getString("status"));
                invoiceVO.setAmount(rs.getString("amount"));
                invoiceVO.setAccountid(rs.getString("accountid"));
                invoiceVO.setCurrency(rs.getString("currency"));
                invoiceVO.setDescription(rs.getString("orderid"));
                invoiceVO.setOrderDescription(rs.getString("orderdesc"));
                invoiceVO.setCountry(rs.getString("country"));
                invoiceVO.setState(rs.getString("state"));
                invoiceVO.setCity(rs.getString("city"));
                invoiceVO.setZip(rs.getString("zipcode"));
                invoiceVO.setTelno(rs.getString("phone"));
                invoiceVO.setTelCc(rs.getString("phonecc"));
                invoiceVO.setStreet(rs.getString("address"));
                invoiceVO.setEmail(rs.getString("custemail"));
                invoiceVO.setCustName(rs.getString("custname"));
                invoiceVO.setIsCredential(rs.getString("isCredential"));
                invoiceVO.setUserName(rs.getString("username"));
                invoiceVO.setPwd(rs.getString("pwd"));
                invoiceVO.setQuestion(rs.getString("question"));
                invoiceVO.setAnswer(rs.getString("answer"));
                invoiceVO.setTerminalid(rs.getString("terminalid"));
                invoiceVO.setRaisedby(rs.getString("raisedby"));
                invoiceVO.setTrackingId(rs.getString("trackingid"));
                invoiceVO.setAccountid(rs.getString("accountid"));
                invoiceVO.setExpirationPeriod(rs.getString("expirationPeriod"));
                invoiceVO.setTimestamp(rs.getString("timestamp"));
            }
            else
            {
                invoiceVO.setStatus("Record not found");
            }
            log.debug("invoice query----"+pstmt);
        }
        catch(Exception e)
        {
            log.error("Leaving Invoice throwing System Exception as System Error :::: ",e);
            PZExceptionHandler.raiseDBViolationException(InvoiceEntry.class.getName(), "getInvoiceDetails()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return invoiceVO;
    }

    public InvoiceVO insertInvoice(InvoiceVO invoiceVO, Boolean reg) throws PZDBViolationException
    {
        log.debug("Entering insertInvoice--");
        HashMap map = new HashMap();
        String oldinvoiceno = "";
        InvoiceEntry invoiceEntry = new InvoiceEntry();
        Functions functions = new Functions();
        ErrorCodeListVO errorCodeListVO = null;
        ErrorCodeVO errorCodeVO = null;

        transactionLogger.debug("2"+ invoiceVO.getMemberid());
        transactionLogger.debug("3"+ invoiceVO.getAmount());
        transactionLogger.debug("4"+ invoiceVO.getDescription());
        transactionLogger.debug("5"+ invoiceVO.getEmail());
        transactionLogger.debug("6"+ invoiceVO.getRedirecturl());
        transactionLogger.debug("7"+ invoiceVO.getCurrency());
        transactionLogger.debug("8"+ invoiceVO.getOrderDescription());
        transactionLogger.debug("9"+ invoiceVO.getCountry());
        transactionLogger.debug("10"+ invoiceVO.getCity());
        transactionLogger.debug("11"+ invoiceVO.getZip());
        transactionLogger.debug("12"+ invoiceVO.getTelCc());
        transactionLogger.debug("13"+ invoiceVO.getTelno());
        transactionLogger.debug("14"+ invoiceVO.getStreet());
        transactionLogger.debug("15"+ invoiceVO.getState());
        transactionLogger.debug("16"+ invoiceVO.getCustName());
        transactionLogger.debug("17"+ invoiceVO.getPaymodeid());
        transactionLogger.debug("18"+ invoiceVO.getIsCredential());
        transactionLogger.debug("19"+ invoiceVO.getUserName());
        transactionLogger.debug("20"+ invoiceVO.getPwd());
        transactionLogger.debug("21"+ invoiceVO.getQuestion());
        transactionLogger.debug("22"+ invoiceVO.getAnswer());
        transactionLogger.debug("23"+ invoiceVO.getMerchantIpAddress());
        transactionLogger.debug("24"+ invoiceVO.getTerminalid());
        transactionLogger.debug("25"+ invoiceVO.getExpirationPeriod());
        transactionLogger.debug("26"+ invoiceVO.getRaisedby());
        transactionLogger.debug("27"+ invoiceVO.getGST());
        transactionLogger.debug("28"+ invoiceVO.getCtoken());

        int parentInvoiceNo = insertParentInvoice(invoiceVO);

        invoiceVO = getInvoiceConfigurationDetail(invoiceVO);
        List<CustomerDetailList> customerDetailLists = invoiceVO.getCustomerDetailList();
        try
        {
        if (invoiceVO.getIsSplitInvoice().equalsIgnoreCase("Y") && customerDetailLists != null)
        {

                if (customerDetailLists.size() > 0)
                {
                    invoiceVO = insertSplitInvoice(parentInvoiceNo, invoiceVO, customerDetailLists);
                    //invoiceVO = invoiceEntry.getSplitInvoiceDetailsForResponse(invoiceVO);

                }

        }
        else if(invoiceVO.getIsSplitInvoice().equalsIgnoreCase("N") && customerDetailLists != null)
        {
            errorCodeListVO = getErrorVO(ErrorName.SYS_SPLIT_INVOICE_NOT_ALLOWED);
            invoiceVO.setErrorCodeListVO(errorCodeListVO);
            return invoiceVO;
        }
        else
        {
            conn = Database.getConnection();
            String query = "insert into invoice(invoiceno,parentinvoiceno,memberid,amount,orderid,customeremail,redirecturl,currency,orderdescription,country,city,zip,telnocc,telno,street,state,custname,paymodeid,isCredential,username,pwd,question,answer,merchantIpAddress,terminalid,expirationPeriod,raisedby,taxamount,ctoken,dtstamp) values(NULL ,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()))";
            PreparedStatement p = conn.prepareStatement(query);
            p.setInt(1, parentInvoiceNo);
            p.setString(2, invoiceVO.getMemberid());
            p.setString(3, invoiceVO.getAmount());
            p.setString(4, invoiceVO.getDescription());
            p.setString(5, invoiceVO.getEmail());
            p.setString(6, invoiceVO.getRedirecturl());
            p.setString(7, invoiceVO.getCurrency());
            p.setString(8, invoiceVO.getOrderDescription());
            p.setString(9, invoiceVO.getCountry());
            p.setString(10, invoiceVO.getCity());
            p.setString(11, invoiceVO.getZip());
            p.setString(12, invoiceVO.getTelCc());
            p.setString(13, invoiceVO.getTelno());
            p.setString(14, invoiceVO.getStreet());
            p.setString(15, invoiceVO.getState());
            p.setString(16, invoiceVO.getCustName());
            p.setString(17, invoiceVO.getPaymodeid());
            p.setString(18, invoiceVO.getIsCredential());
            p.setString(19, invoiceVO.getUserName());
            p.setString(20, invoiceVO.getPwd());
            p.setString(21, invoiceVO.getQuestion());
            p.setString(22, invoiceVO.getAnswer());
            p.setString(23, invoiceVO.getMerchantIpAddress());
            p.setString(24, invoiceVO.getTerminalid());
            p.setString(25, invoiceVO.getExpirationPeriod());
            p.setString(26, invoiceVO.getRaisedby());
            p.setString(27, invoiceVO.getGST());
            p.setString(28, invoiceVO.getCtoken());
            log.debug("insert invoice qry--->" + p);
            int num = p.executeUpdate();
            if (num == 1)
            {
                ResultSet rs = p.getGeneratedKeys();
                if (rs != null)
                {
                    while (rs.next())
                    {
                        invoiceVO.setInvoiceno(String.valueOf(rs.getInt(1)));
                    }
                }
            }

            invoiceVO = invoiceEntry.getInvoiceConfigurationDetail(invoiceVO);

            List<ProductList> listProducts = invoiceVO.getProductList();

            if (listProducts != null)
            {
                if (listProducts.size() > 0)
                {
                    insertInvoiceProduct(listProducts, Integer.parseInt(invoiceVO.getInvoiceno()));
                }
            }

            String telnocc = invoiceVO.getTelCc();


            invoiceVO.setTelCc(telnocc);
            //for email
            if (invoiceVO.getIsemail().equals("Y"))
            {
                if (reg)
                {
                    invoiceVO = sendInvoiceMailRegenerate(oldinvoiceno + "", invoiceVO);
                }
                else
                {
                    invoiceVO = sendInvoiceMail(invoiceVO);
                }
            }
            //for sms
            if (invoiceVO.getSmsactivation().equals("Y"))
            {
                if (invoiceVO.getIssms().equals("Y"))
                {
                    AsynchronousSmsService smsService = new AsynchronousSmsService();
                    String liveUrl = ApplicationProperties.getProperty("COMPANY_LIVE_URL");
                    map.put("custname", invoiceVO.getCustName());
                    map.put("orderdesc", invoiceVO.getOrderDescription());
                    map.put("invoiceno", invoiceVO.getInvoiceno());
                    map.put("currency", invoiceVO.getCurrency());
                    map.put("amount", invoiceVO.getAmount());
                    map.put("phone", invoiceVO.getTelno());
                    map.put("phonecc", invoiceVO.getTelCc());
                    map.put("orderid", invoiceVO.getDescription());
                    map.put("ctoken", invoiceVO.getCtoken());
                    if (!functions.isValueNull(invoiceVO.getTerminalid()))
                    {
                        invoiceVO.setTerminalid("");
                    }
                    map.put("terminalid", invoiceVO.getTerminalid());
                    map.put("companyname", invoiceVO.getMerchantDetailsVO().getCompany_name());
                    map.put("LIVE_URL", liveUrl);
                    smsService.sendSMS(MailEventEnum.GENERATING_INVOICE_BY_MERCHANT, map);


                }

            }
            if(invoiceVO.getPartnerDetailsVO()!=null)
            {
                PartnerDetailsVO partnerDetailsVO = invoiceVO.getPartnerDetailsVO();
                if(functions.isValueNull(invoiceVO.getTerminalid()))
                    invoiceVO.setTransactionUrl("https://"+partnerDetailsVO.getHostUrl()+"/transaction/PayInvoice?inv=" + invoiceVO.getInvoiceno() + "&t=" +invoiceVO.getTerminalid() + "&ct=" + invoiceVO.getCtoken());
                else
                    invoiceVO.setTransactionUrl("https://"+partnerDetailsVO.getHostUrl()+"/transaction/PayInvoice?inv=" + invoiceVO.getInvoiceno() + "&ct=" + invoiceVO.getCtoken());

                try
                {
                    String text = invoiceVO.getTransactionUrl();
                    transactionLogger.error("text---->"+text);
                    String filePath = QRCODERB.getString("PATH") + "InvoiceQR_" + invoiceVO.getInvoiceno() + ".png";
                    QRCodeWriter qrCodeWriter = new QRCodeWriter();
                    BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);

                    Path path = FileSystems.getDefault().getPath(filePath);
                    MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

                    File file=new File(filePath);
                    byte[] fileContent=FileUtils.readFileToByteArray(file);
                    String invoiceQRCode= Base64.getEncoder().encodeToString(fileContent);
                    invoiceVO.setTransactionUrlQRCode(invoiceQRCode);
                }
                catch (WriterException e)
                {
                    transactionLogger.error("WriterException---->",e);
                }
                catch (IOException e)
                {
                    transactionLogger.error("IOException---->", e);
                }
                catch (Exception e)
                {
                    transactionLogger.error("Exception---->", e);
                }
            }

            /*}

            catch (SystemError e)
            {
                log.error("Leaving Incoice throwing System Exception as System Error :::: ", e);
                PZExceptionHandler.raiseDBViolationException(InvoiceEntry.class.getName(), "insertInvoice()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
            }
            catch (SQLException e)
            {
                log.error("Leaving Incoice throwing System Exception as System Error :::: ", e);
                PZExceptionHandler.raiseDBViolationException(InvoiceEntry.class.getName(), "insertInvoice()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
            }
            finally
            {
                Database.closeConnection(conn);
            }*/

        }

       /* if(reg)
        {
            log.debug("old invoice no.---->" + invoiceVO.getInvoiceno());
            oldinvoiceno = invoiceVO.getInvoiceno();

        conn = Database.getConnection();
            String query = "insert into invoice(invoiceno,parentinvoiceno,memberid,amount,orderid,customeremail,redirecturl,currency,orderdescription,country,city,zip,telnocc,telno,street,state,custname,paymodeid,isCredential,username,pwd,question,answer,merchantIpAddress,terminalid,expirationPeriod,raisedby,taxamount,ctoken,dtstamp) values(NULL ,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()))";
            PreparedStatement p = conn.prepareStatement(query);
            p.setInt(1, parentInvoiceNo);
            p.setString(2, invoiceVO.getMemberid());
            p.setString(3, invoiceVO.getAmount());
            p.setString(4, invoiceVO.getDescription());
            p.setString(5, invoiceVO.getEmail());
            p.setString(6, invoiceVO.getRedirecturl());
            p.setString(7, invoiceVO.getCurrency());
            p.setString(8, invoiceVO.getOrderDescription());
            p.setString(9, invoiceVO.getCountry());
            p.setString(10, invoiceVO.getCity());
            p.setString(11, invoiceVO.getZip());
            p.setString(12, invoiceVO.getTelCc());
            p.setString(13, invoiceVO.getTelno());
            p.setString(14, invoiceVO.getStreet());
            p.setString(15, invoiceVO.getState());
            p.setString(16, invoiceVO.getCustName());
            p.setString(17, invoiceVO.getPaymodeid());
            p.setString(18, invoiceVO.getIsCredential());
            p.setString(19, invoiceVO.getUserName());
            p.setString(20, invoiceVO.getPwd());
            p.setString(21, invoiceVO.getQuestion());
            p.setString(22, invoiceVO.getAnswer());
            p.setString(23, invoiceVO.getMerchantIpAddress());
            p.setString(24, invoiceVO.getTerminalid());
            p.setString(25, invoiceVO.getExpirationPeriod());
            p.setString(26, invoiceVO.getRaisedby());
            p.setString(27, invoiceVO.getGST());
            p.setString(28, invoiceVO.getCtoken());
            log.debug("insert invoice qry--->" + p);
            int num = p.executeUpdate();
            if (num == 1)
            {
                ResultSet rs = p.getGeneratedKeys();
                if (rs != null)
                {
                    while (rs.next())
                    {
                        invoiceVO.setInvoiceno(String.valueOf(rs.getInt(1)));
                        log.debug("getGeneratedKeys in while loop 3338 ::::::::::::::::"+rs.getInt(1));

                    }
                }
            }

            invoiceVO = invoiceEntry.getInvoiceConfigurationDetail(invoiceVO);

            List<ProductList> listProducts = invoiceVO.getProductList();

            if (listProducts != null)
            {
                if (listProducts.size() > 0)
                {
                    insertInvoiceProduct(listProducts, Integer.parseInt(invoiceVO.getInvoiceno()));
                }
            }

            String telnocc = invoiceVO.getTelCc();


            invoiceVO.setTelCc(telnocc);
            //for email
            if (invoiceVO.getIsemail().equals("Y"))
            {
                if (reg)
                {
                    invoiceVO = sendInvoiceMailRegenerate(oldinvoiceno + "", invoiceVO);
                }
                else
                {
                    invoiceVO = sendInvoiceMail(invoiceVO);
                }
            }
            //for sms
            if (invoiceVO.getSmsactivation().equals("Y"))
            {
                if (invoiceVO.getIssms().equals("Y"))
                {
                    AsynchronousSmsService smsService = new AsynchronousSmsService();
                    String liveUrl = ApplicationProperties.getProperty("COMPANY_LIVE_URL");
                    map.put("custname", invoiceVO.getCustName());
                    map.put("orderdesc", invoiceVO.getOrderDescription());
                    map.put("invoiceno", invoiceVO.getInvoiceno());
                    map.put("currency", invoiceVO.getCurrency());
                    map.put("amount", invoiceVO.getAmount());
                    map.put("phone", invoiceVO.getTelno());
                    map.put("phonecc", invoiceVO.getTelCc());
                    map.put("orderid", invoiceVO.getDescription());
                    map.put("ctoken", invoiceVO.getCtoken());
                    if (!functions.isValueNull(invoiceVO.getTerminalid()))
                    {
                        invoiceVO.setTerminalid("");
                    }
                    map.put("terminalid", invoiceVO.getTerminalid());
                    map.put("companyname", invoiceVO.getMerchantDetailsVO().getCompany_name());
                    map.put("LIVE_URL", liveUrl);
                    smsService.sendSMS(MailEventEnum.GENERATING_INVOICE_BY_MERCHANT, map);


                }

            }
        }*/
    }

            catch (SystemError e)
            {
                log.error("Leaving Incoice throwing System Exception as System Error :::: ", e);
                PZExceptionHandler.raiseDBViolationException(InvoiceEntry.class.getName(), "insertInvoice()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
            }
            catch (SQLException e)
            {
                log.error("Leaving Incoice throwing System Exception as System Error :::: ", e);
                PZExceptionHandler.raiseDBViolationException(InvoiceEntry.class.getName(), "insertInvoice()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
            }
            finally
            {
                Database.closeConnection(conn);
            }

        return invoiceVO;
    }

    public int insertParentInvoice(InvoiceVO invoiceVO) throws PZDBViolationException
    {
        log.debug("Entering insertInvoice--");
        int invoiceno = 0;
        try
        {
            conn = Database.getConnection();
            String query="INSERT INTO parent_invoice(amount,memberid,currency,redirecturl,orderid,orderdescription,dtstamp,taxamount,raisedby) VALUES (?,?,?,?,?,?,unix_timestamp(now()),?,?)";
            PreparedStatement p = conn.prepareStatement(query);
            p.setString(1, invoiceVO.getAmount());
            p.setString(2, invoiceVO.getMemberid());
            p.setString(3, invoiceVO.getCurrency());
            p.setString(4, invoiceVO.getRedirecturl());
            p.setString(5, invoiceVO.getDescription());
            p.setString(6, invoiceVO.getOrderDescription());
            p.setString(7, invoiceVO.getGST());
            p.setString(8, invoiceVO.getRaisedby());
            log.debug("insert parent invoice qry--->" + p);
            int num = p.executeUpdate();
            if (num == 1)
            {
                ResultSet rs = p.getGeneratedKeys();
                if (rs != null)
                {
                    while (rs.next())
                    {
                        invoiceno = rs.getInt(1);
                    }
                }
            }
        }

        catch (SQLException e)
        {
            log.error("Leaving Incoice throwing System Exception as System Error :::: ",e);
            PZExceptionHandler.raiseDBViolationException(InvoiceEntry.class.getName(), "insertInvoice()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError e)
        {
            log.error("Leaving Incoice throwing System Exception as System Error :::: ",e);
            PZExceptionHandler.raiseDBViolationException(InvoiceEntry.class.getName(), "insertInvoice()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return invoiceno;
    }

    public boolean isOrderUnique(String orderId)
    {
        boolean isOrderUnique = true;
        Connection con = null;
        PreparedStatement p = null;
        ResultSet rs=null;

        try
        {
            //con = Database.getConnection();
            con=Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("SELECT orderid FROM invoice WHERE orderid=?");
            p = con.prepareStatement(qry.toString());
            p.setString(1, orderId);
            rs = p.executeQuery();
            if(rs.next())
            {
                isOrderUnique = false;
            }
        }
        catch (SQLException e)
        {
            //e.printStackTrace();
            log.debug("SQLException :" +e);
        }
        catch (SystemError systemError)
        {
            //systemError.printStackTrace();
            log.debug("SystemError :" + systemError);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p);
            Database.closeConnection(con);
        }

        return isOrderUnique;
    }

    public InvoiceVO getInvoiceConfigurationDetails(String memberid)
    {
        InvoiceVO invoiceVO = null;
        MerchantDetailsVO merchantDetailsVO = null;
        Connection connection = null;
        PreparedStatement p=null;
        ResultSet rs=null;
        try
        {    connection=Database.getRDBConnection();
            //connection = Database.getConnection();
            //  String query = "SELECT * FROM invoice_config WHERE memberid= ?";
            String query = "SELECT i.memberid,i.initial,i.redirecturl,i.expirationPeriod,i.currency,i.terminalid,i.city,i.state,i.country,i.telnocc,i.GST,i.isSMS,i.isEmail,i.isApp,i.paymentterms,m.smsactivation,i.isduedate,i.islatefee,i.duedate,i.latefee,i.loaddefaultproductlist,i.GST,i.isSplitInvoice,i.unit,m.customersmsactivation as smsactivation, m.clkey,m.checksumalgo,m.autoSelectTerminal FROM invoice_config AS i JOIN members AS m ON i.memberid= m.memberid WHERE i.memberid=?";
            //query.append("SELECT DISTINCT ic.*, mam.paymodeid, mam.cardtypeid FROM invoice_config AS ic, member_account_mapping AS mam WHERE ic.memberid="+memberid+" AND ic.memberid=mam.memberid ");
            p = connection.prepareStatement(String.valueOf(query));
            p.setString(1, memberid);
            rs=p.executeQuery();

            if (rs.next())
            {
                invoiceVO = new InvoiceVO();
                merchantDetailsVO = new MerchantDetailsVO();
                invoiceVO.setMemberid(rs.getString("memberid"));
                invoiceVO.setInitial(rs.getString("initial"));
                invoiceVO.setRedirecturl(rs.getString("redirecturl"));
                invoiceVO.setExpirationPeriod(rs.getString("expirationPeriod"));
                invoiceVO.setCurrency(rs.getString("currency"));
                invoiceVO.setTerminalid(rs.getString("terminalid"));
                invoiceVO.setCity(rs.getString("city"));
                invoiceVO.setState(rs.getString("state"));
                invoiceVO.setCountry(rs.getString("country"));
                invoiceVO.setTelCc(rs.getString("telnocc"));
                invoiceVO.setGST(rs.getString("GST"));
                invoiceVO.setIssms(rs.getString("isSMS"));
                invoiceVO.setIsemail(rs.getString("isEmail"));
                invoiceVO.setIsapp(rs.getString("isApp"));
                invoiceVO.setPaymentterms(rs.getString("paymentterms"));
                invoiceVO.setSmsactivation(rs.getString("smsactivation"));
                invoiceVO.setIsduedate(rs.getString("isduedate"));
                invoiceVO.setIslatefee(rs.getString("islatefee"));
                invoiceVO.setDuedate(rs.getString("duedate"));
                invoiceVO.setLatefee(rs.getString("latefee"));
                invoiceVO.setLoadDefaultProductList(rs.getString("loaddefaultproductlist"));
                invoiceVO.setGST(rs.getString("GST"));
                invoiceVO.setIsSplitInvoice(rs.getString("isSplitInvoice"));
                invoiceVO.setUnit(rs.getString("unit"));
                invoiceVO.setStatus("success");
                //invoiceVO.setStatus("defaultLanguage");
                merchantDetailsVO.setKey("clkey");
                merchantDetailsVO.setChecksumAlgo("checksumalgo");
                merchantDetailsVO.setAutoSelectTerminal("autoSelectTerminal");
                invoiceVO.setMerchantDetailsVO(merchantDetailsVO);
            }
            log.debug("query-----"+p);

            List<UnitList> unitList = getUnitListForAPI(memberid);
            if (unitList != null)
            {
                if (unitList.size() > 0)
                {
                    invoiceVO.setDefaultunitList(unitList);
                }
            }

            List<DefaultProductList> defaultProductList = getDefaultProductList(memberid);
            if (defaultProductList != null)
            {
                if (defaultProductList.size() > 0)
                {
                    invoiceVO.setDefaultProductList(defaultProductList);
                }
            }
        }
        catch (SQLException e)
        {
            log.error("SQLException----", e);
        }
        catch (SystemError e)
        {
            log.error("SystemError----", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p);
            Database.closeConnection(connection);
        }
        return invoiceVO;
    }

    public InvoiceVO getInvoiceConfigurationDetailsForGeneratedInvoice(String memberid)
    {
        InvoiceVO invoiceVO = null;
        Connection connection = null;
        try
        {
            connection = Database.getConnection();
            //  String query = "SELECT * FROM invoice_config WHERE memberid= ?";
            String query = "SELECT i.*,m.customersmsactivation as smsactivation FROM invoice_config AS i JOIN members AS m ON i.memberid= m.memberid WHERE i.memberid=?";
            //query.append("SELECT DISTINCT ic.*, mam.paymodeid, mam.cardtypeid FROM invoice_config AS ic, member_account_mapping AS mam WHERE ic.memberid="+memberid+" AND ic.memberid=mam.memberid ");
            PreparedStatement p = connection.prepareStatement(String.valueOf(query));
            p.setString(1, memberid);

            ResultSet rs=p.executeQuery();

            if (rs.next())
            {
                invoiceVO = new InvoiceVO();
                invoiceVO.setMemberid(rs.getString("memberid"));
                invoiceVO.setInitial(rs.getString("initial"));
                invoiceVO.setRedirecturl(rs.getString("redirecturl"));
                invoiceVO.setExpirationPeriod(rs.getString("expirationPeriod"));
                invoiceVO.setCurrency(rs.getString("currency"));
                invoiceVO.setTerminalid(rs.getString("terminalid"));
                //invoiceVO.setCity(rs.getString("city"));
                //invoiceVO.setState(rs.getString("state"));
                //invoiceVO.setCountry(rs.getString("country"));
                //invoiceVO.setTelCc(rs.getString("telnocc"));
                invoiceVO.setGST(rs.getString("GST"));
                invoiceVO.setIssms(rs.getString("isSMS"));
                invoiceVO.setIsemail(rs.getString("isEmail"));
                invoiceVO.setIsapp(rs.getString("isApp"));
                invoiceVO.setPaymentterms(rs.getString("paymentterms"));
                invoiceVO.setSmsactivation(rs.getString("smsactivation"));
                invoiceVO.setIsduedate(rs.getString("isduedate"));
                invoiceVO.setIslatefee(rs.getString("islatefee"));
                invoiceVO.setDuedate(rs.getString("duedate"));
                invoiceVO.setLatefee(rs.getString("latefee"));
                invoiceVO.setLoadDefaultProductList(rs.getString("loaddefaultproductlist"));
                invoiceVO.setGST(rs.getString("GST"));
                invoiceVO.setIsSplitInvoice(rs.getString("isSplitInvoice"));
                invoiceVO.setUnit(rs.getString("unit"));
                invoiceVO.setStatus("success");
            }
            log.debug("query-----"+p);

            List<UnitList> unitList = getUnitListForAPI(memberid);
            if (unitList != null)
            {
                if (unitList.size() > 0)
                {
                    invoiceVO.setDefaultunitList(unitList);
                }
            }

            List<DefaultProductList> defaultProductList = getDefaultProductList(memberid);
            if (defaultProductList != null)
            {
                if (defaultProductList.size() > 0)
                {
                    invoiceVO.setDefaultProductList(defaultProductList);
                }
            }

        }
        catch (SQLException e)
        {
            log.error("SQLException----", e);
        }
        catch (SystemError e)
        {
            log.error("SystemError----", e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return invoiceVO;
    }

    public InvoiceVO getInvoiceConfigurationDetails(InvoiceVO invoiceVO)
    {
        //InvoiceVO invoiceVO = null;
        Connection connection = null;
        PreparedStatement p=null;
        ResultSet rs=null;
        try
        {
            //connection = Database.getConnection();
            connection=Database.getRDBConnection();
            //  String query = "SELECT * FROM invoice_config WHERE memberid= ?";
            String query = "SELECT i.*,m.customersmsactivation as smsactivation FROM invoice_config AS i JOIN members AS m ON i.memberid= m.memberid WHERE i.memberid=?";
            //query.append("SELECT DISTINCT ic.*, mam.paymodeid, mam.cardtypeid FROM invoice_config AS ic, member_account_mapping AS mam WHERE ic.memberid="+memberid+" AND ic.memberid=mam.memberid ");
            p = connection.prepareStatement(String.valueOf(query));
            p.setString(1, invoiceVO.getMemberid());

            rs=p.executeQuery();

            if (rs.next())
            {
                //invoiceVO = new InvoiceVO();
                invoiceVO.setMemberid(rs.getString("memberid"));
                invoiceVO.setInitial(rs.getString("initial"));
                invoiceVO.setRedirecturl(rs.getString("redirecturl"));
                invoiceVO.setExpirationPeriod(rs.getString("expirationPeriod"));
                invoiceVO.setCurrency(rs.getString("currency"));
                invoiceVO.setTerminalid(rs.getString("terminalid"));
                invoiceVO.setCity(rs.getString("city"));
                invoiceVO.setState(rs.getString("state"));
                invoiceVO.setCountry(rs.getString("country"));
                invoiceVO.setTelCc(rs.getString("telnocc"));
                invoiceVO.setGST(rs.getString("GST"));
                invoiceVO.setIssms(rs.getString("isSMS"));
                invoiceVO.setIsemail(rs.getString("isEmail"));
                invoiceVO.setIsapp(rs.getString("isApp"));
                invoiceVO.setPaymentterms(rs.getString("paymentterms"));
                invoiceVO.setSmsactivation(rs.getString("smsactivation"));
                invoiceVO.setIsduedate(rs.getString("isduedate"));
                invoiceVO.setIslatefee(rs.getString("islatefee"));
                invoiceVO.setDuedate(rs.getString("duedate"));
                invoiceVO.setLatefee(rs.getString("latefee"));
                invoiceVO.setUnit(rs.getString("unit"));
                invoiceVO.setStatus("success");
            }
            log.debug("query-----"+p);

        }
        catch (SQLException e)
        {
            log.error("SQLException----",e);
        }
        catch (SystemError e)
        {
            log.error("SystemError----",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p);
            Database.closeConnection(connection);
        }
        return invoiceVO;
    }

    public InvoiceVO getInvoiceConfigurationDetailsForTransaction(InvoiceVO invoiceVO)
    {
        //InvoiceVO invoiceVO = null;
        Connection connection = null;
        PreparedStatement p = null;
        ResultSet rs = null;
        try
        {
            connection = Database.getConnection();
            //  String query = "SELECT * FROM invoice_config WHERE memberid= ?";
            String query = "SELECT i.*,m.customersmsactivation as smsactivation FROM invoice_config AS i JOIN members AS m ON i.memberid= m.memberid WHERE i.memberid=?";
            //query.append("SELECT DISTINCT ic.*, mam.paymodeid, mam.cardtypeid FROM invoice_config AS ic, member_account_mapping AS mam WHERE ic.memberid="+memberid+" AND ic.memberid=mam.memberid ");
            p = connection.prepareStatement(String.valueOf(query));
            p.setString(1, invoiceVO.getMemberid());

            rs=p.executeQuery();

            if (rs.next())
            {
                //invoiceVO = new InvoiceVO();
                invoiceVO.setMemberid(rs.getString("memberid"));
                invoiceVO.setInitial(rs.getString("initial"));
                invoiceVO.setRedirecturl(rs.getString("redirecturl"));
                invoiceVO.setExpirationPeriod(rs.getString("expirationPeriod"));
                //invoiceVO.setCurrency(rs.getString("currency"));
                invoiceVO.setTerminalid(rs.getString("terminalid"));
                //invoiceVO.setCity(rs.getString("city"));
                //invoiceVO.setState(rs.getString("state"));
                //invoiceVO.setCountry(rs.getString("country"));
                //invoiceVO.setTelCc(rs.getString("telnocc"));
                invoiceVO.setGST(rs.getString("GST"));
                invoiceVO.setIssms(rs.getString("isSMS"));
                invoiceVO.setIsemail(rs.getString("isEmail"));
                invoiceVO.setIsapp(rs.getString("isApp"));
                invoiceVO.setPaymentterms(rs.getString("paymentterms"));
                invoiceVO.setSmsactivation(rs.getString("smsactivation"));
                invoiceVO.setIsduedate(rs.getString("isduedate"));
                invoiceVO.setIslatefee(rs.getString("islatefee"));
                invoiceVO.setDuedate(rs.getString("duedate"));
                invoiceVO.setLatefee(rs.getString("latefee"));
                invoiceVO.setStatus("success");
            }
            log.debug("query-----"+p);

        }
        catch (SQLException e)
        {
            log.error("SQLException----",e);
        }
        catch (SystemError e)
        {
            log.error("SystemError----",e);
        }
        finally
        {
            Database.closePreparedStatement(p);
            Database.closeResultSet(rs);
            Database.closeConnection(connection);
        }
        return invoiceVO;
    }


    public InvoiceVO getInvoiceConfigurationDetail(InvoiceVO invoiceVO)
    {
        //InvoiceVO invoiceVO = null;
        Connection connection = null;
        try
        {
            connection = Database.getConnection();
            //  String query = "SELECT * FROM invoice_config WHERE memberid= ?";
            String query = "SELECT i.*,m.customersmsactivation as smsactivation FROM invoice_config AS i JOIN members AS m ON i.memberid= m.memberid WHERE i.memberid=?";
            //query.append("SELECT DISTINCT ic.*, mam.paymodeid, mam.cardtypeid FROM invoice_config AS ic, member_account_mapping AS mam WHERE ic.memberid="+memberid+" AND ic.memberid=mam.memberid ");
            PreparedStatement p = connection.prepareStatement(String.valueOf(query));
            p.setString(1, invoiceVO.getMemberid());

            ResultSet rs=p.executeQuery();

            if (rs.next())
            {
                //invoiceVO = new InvoiceVO();
                invoiceVO.setMemberid(rs.getString("memberid"));


                invoiceVO.setIssms(rs.getString("isSMS"));
                invoiceVO.setIsemail(rs.getString("isEmail"));
                invoiceVO.setIsapp(rs.getString("isApp"));
                invoiceVO.setPaymentterms(rs.getString("paymentterms"));
                invoiceVO.setSmsactivation(rs.getString("smsactivation"));
                invoiceVO.setIsduedate(rs.getString("isduedate"));
                invoiceVO.setIslatefee(rs.getString("islatefee"));
                invoiceVO.setDuedate(rs.getString("duedate"));
                invoiceVO.setLatefee(rs.getString("latefee"));
                invoiceVO.setIsSplitInvoice(rs.getString("isSplitInvoice"));
                invoiceVO.setGST(rs.getString("GST"));
                invoiceVO.setStatus("success");
            }
            log.debug("query-----"+p);

        }
        catch (SQLException e)
        {
            log.error("SQLException----",e);
        }
        catch (SystemError e)
        {
            log.error("SystemError----",e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return invoiceVO;
    }



    //unitlist

    public List<ProductList> getItemList(String invoiceid)
    {
        List<ProductList> productList = new ArrayList<ProductList>();
        Connection connection = null;
        try
        {
            connection = Database.getConnection();
            String query = "SELECT * FROM invoice_product_details WHERE invoiceno= ?";
            PreparedStatement p = connection.prepareStatement(String.valueOf(query));
            p.setString(1, invoiceid);

            ResultSet rs=p.executeQuery();

            while (rs.next())
            {
                ProductList list = new ProductList();
                list.setProductAmount(rs.getString("amount"));
                list.setProductDescription(rs.getString("description"));
                list.setQuantity(rs.getString("quantity"));
                list.setProductTotal(rs.getString("subtotal"));
                list.setProductUnit(rs.getString("unit"));
                list.setTax(rs.getString("tax"));
                productList.add(list);


            }
            log.debug("query-----"+p);

        }
        catch (SQLException e)
        {
            log.error("SQLException----",e);
        }
        catch (SystemError e)
        {
            log.error("SystemError----",e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return productList;
    }

    public List<String> getUnitList(String memberid)
    {
        List<String> unitList = new ArrayList<String>();
        Connection connection = null;
        PreparedStatement p = null;
        ResultSet rs=null;
        try
        {
            //connection = Database.getConnection();
            connection=Database.getRDBConnection();
            String query = "SELECT unit FROM product_unit WHERE memberid= ?";
            p = connection.prepareStatement(String.valueOf(query));
            p.setString(1, memberid);

            rs=p.executeQuery();

            while (rs.next())
            {
                String unit = "";
                unit = rs.getString("unit");
                unitList.add(unit);
            }
            log.debug("query-----"+p);
        }
        catch (SQLException e)
        {
            log.error("SQLException----",e);
        }
        catch (SystemError e)
        {
            log.error("SystemError----",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p);
            Database.closeConnection(connection);

        }
        return unitList;
    }

    public List<UnitList> getUnitListForAPI(String memberid)
    {
        List<UnitList> unitList = new ArrayList<UnitList>();
        Connection connection = null;
        PreparedStatement p=null;
        ResultSet rs=null;
        try
        {
            connection = Database.getConnection();
            String query = "SELECT unit FROM product_unit WHERE memberid= ?";
            p = connection.prepareStatement(String.valueOf(query));
            p.setString(1, memberid);

            rs=p.executeQuery();

            while (rs.next())
            {
                UnitList listOfUnit = new UnitList();
                listOfUnit.setDefaultunit(rs.getString("unit"));
                unitList.add(listOfUnit);
            }
            log.debug("query-----"+p);
        }
        catch (SQLException e)
        {
            log.error("SQLException----",e);
        }
        catch (SystemError e)
        {
            log.error("SystemError----",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p);
            Database.closeConnection(connection);
        }
        return unitList;
    }

    public List<DefaultProductList> getDefaultProductList(String memberId)
    {
        List<DefaultProductList> defaultProductList = new ArrayList<DefaultProductList>();
        PreparedStatement p=null;
        ResultSet rs=null;
        try
        {
           // conn = Database.getConnection();
            conn=Database.getRDBConnection();
            String query = "SELECT productdesc,productamount,unit,tax FROM default_productlist WHERE memberid= ?";
            p = conn.prepareStatement(query);
            p.setString(1,memberId);

            rs = p.executeQuery();

            while(rs.next())
            {
                DefaultProductList productList = new DefaultProductList();
                productList.setProductDescription(rs.getString("productdesc"));
                productList.setProductAmount(rs.getString("productamount"));
                productList.setUnit(rs.getString("unit"));
                productList.setTax(rs.getString("tax"));

                defaultProductList.add(productList);
            }
        }
        catch (SystemError e)
        {
            log.error("SystemError----",e);
        }
        catch (SQLException e)
        {
            log.error("SQLException----",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p);
            Database.closeConnection(conn);
        }
        return defaultProductList;
    }

    public boolean updateInvoiceConfigurationDetails(InvoiceVO invoiceVO)
    {
        Connection connection = null;
        Functions functions = new Functions();
        boolean status = false;
        try
        {
            connection = Database.getConnection();
            StringBuilder query = new StringBuilder("update invoice_config set memberid = '" + invoiceVO.getMemberid()+"'");

            if (functions.isValueNull(invoiceVO.getInitial()))
            {
                query.append(" ,initial = '" + invoiceVO.getInitial()+"'");
            }
            if (functions.isValueNull(invoiceVO.getRedirecturl()))
            {
                query.append(" ,redirecturl = '"+invoiceVO.getRedirecturl()+"'");
            }
            if (functions.isValueNull(invoiceVO.getExpirationPeriod()))
            {
                query.append(" ,expirationPeriod = ").append(invoiceVO.getExpirationPeriod());
            }
            if (functions.isValueNull(invoiceVO.getCurrency()))
            {
                query.append(" ,currency = '" + invoiceVO.getCurrency() + "'");
            }
            query.append(" ,terminalid = '"+invoiceVO.getTerminalid()+"'");

            if (functions.isValueNull(invoiceVO.getCity()))
            {
                query.append(" ,city = '"+invoiceVO.getCity()+"'");
            }
            else
            {
                query.append(" ,city = ''");
            }
            if (functions.isValueNull(invoiceVO.getState()))
            {
                query.append(" ,state = '"+invoiceVO.getState()+"'");
            }
            else
            {
                query.append(" ,state = ''");
            }

            query.append(" ,country = '"+invoiceVO.getCountry()+"'");

            query.append(" ,telnocc = '"+invoiceVO.getTelCc()+"'");

            if (functions.isValueNull(invoiceVO.getGST()))
            {
                query.append(" ,GST = '"+invoiceVO.getGST()+"'");
            }
            else
            {
                query.append(" ,GST = '0'");
            }

            if (functions.isValueNull(invoiceVO.getIssms()))
            {
                query.append(" ,isSMS = '" + invoiceVO.getIssms() + "'");
            }

            if (functions.isValueNull(invoiceVO.getIsemail()))
            {
                query.append(" ,isEmail = '" + invoiceVO.getIsemail() + "'");
            }

            if (functions.isValueNull(invoiceVO.getPaymentterms()))
            {
                query.append(" ,paymentterms = '" + invoiceVO.getPaymentterms() + "'");
            }

            if (functions.isValueNull(invoiceVO.getIsduedate()))
            {
                query.append(" ,isduedate = '" + invoiceVO.getIsduedate() + "'");
            }
            if (functions.isValueNull(invoiceVO.getDuedate()))
            {
                query.append(" ,duedate = '" + invoiceVO.getDuedate() + "'");
            }

            if (functions.isValueNull(invoiceVO.getIslatefee()))
            {
                query.append(" ,islatefee = '" + invoiceVO.getIslatefee() + "'");
            }
            if (functions.isValueNull(invoiceVO.getIsSplitInvoice()))
            {
                query.append(" ,isSplitInvoice = '" + invoiceVO.getIsSplitInvoice() + "'");
            }
            if (functions.isValueNull(invoiceVO.getLoadDefaultProductList()))
            {
                query.append(" ,loaddefaultproductlist = '" + invoiceVO.getLoadDefaultProductList() + "'");
            }
            if (functions.isValueNull(invoiceVO.getLatefee()))
            {
                query.append(" ,latefee = '" + invoiceVO.getLatefee() + "'");
            }
            else
            {
                invoiceVO.setLatefee("0");
            }
            query.append(" ,unit = '"+invoiceVO.getUnit()+"'");
            query.append(" where memberid = ").append( invoiceVO.getMemberid());

            List<DefaultProductList> listProducts = invoiceVO.getDefaultProductList();

            if (listProducts != null)
            {
                if (listProducts.size() > 0)
                {
                    List<DefaultProductList> defaultProductList = getDefaultProductList(invoiceVO.getMemberid());

                    if (defaultProductList != null)
                    {
                        if (defaultProductList.size() > 0)
                        {
                            deleteDefaultProductList(invoiceVO.getMemberid());
                        }

                    }
                    insertDefaultInvoiceProduct(listProducts, Integer.parseInt(invoiceVO.getMemberid()));
                }
                else
                {
                    deleteDefaultProductList(invoiceVO.getMemberid());
                }

            }
            else
            {
                deleteDefaultProductList(invoiceVO.getMemberid());
            }

            List<UnitList> unitList = invoiceVO.getDefaultunitList();
            if (unitList != null)
            {
                if (unitList.size() > 0)
                {
                    List<String> defaultUnitList = getUnitList(invoiceVO.getMemberid());
                    if (defaultUnitList.size() > 0)
                    {
                        deleteUnit(invoiceVO.getMemberid());
                    }
                    insertUnit(unitList, Integer.parseInt(invoiceVO.getMemberid()));

                }
                if (unitList.size() == 0)
                {
                    deleteUnit(invoiceVO.getMemberid());
                }
            }


            PreparedStatement preparedStatement =  connection.prepareStatement(query.toString());
            int i = preparedStatement.executeUpdate();


            if (i>0)
            {
                status = true;
            }
            log.debug("update query----"+preparedStatement);
        }
        catch (SQLException e)
        {
            log.error("SQLException----", e);
        }
        catch (SystemError e)
        {
            log.error("SystemError----", e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return status;
    }

    public void insertInvoiceConfigDetails(String memberId)
    {
        log.debug("inside insert invoice details---");
        Connection connection = null;
        try
        {
            connection = Database.getConnection();
            String query = "INSERT INTO invoice_config (`memberid`, `expirationPeriod`) VALUES (?, ?)";
            PreparedStatement p=connection.prepareStatement(query);
            p.setString(1,memberId );
            p.setString(2,"10");
            int num = p.executeUpdate();
            log.debug("query----"+p);
        }

        catch (SQLException e)
        {
            log.error("SQLException----",e);
        }
        catch (SystemError e)
        {
            log.error("SystemError",e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
    }

    public String getOrderId(String memberid)
    {
        Connection connection = null;
        String orderId = "";
        try
        {
            connection = Database.getConnection();
            String query = "SELECT order_seq(" + memberid + ") as orderid";
            PreparedStatement p = connection.prepareStatement(query);
            ResultSet rs=p.executeQuery();

            if (rs.next())
            {
                orderId = rs.getString("orderid");
            }
            log.debug("order query----"+p);

        }
        catch (SQLException e)
        {
            log.error("SQLException",e);
        }
        catch (SystemError e)
        {
            log.error("SystemError",e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return orderId;
    }

    public  List<String> getCurrencyList(String memberID)
    {
        List<String> currencyList = new ArrayList<String>();
        Connection connection = null;
        PreparedStatement p=null;
        ResultSet rs=null;
        try
        {
            //connection = Database.getConnection();
            connection=Database.getRDBConnection();
            String query = "SELECT DISTINCT gt.currency FROM gateway_type AS gt, gateway_accounts AS ga, member_account_mapping AS mam WHERE mam.memberid = ? AND mam.accountid = ga.accountid AND ga.pgtypeid = gt.pgtypeid ORDER BY currency ASC";
            p = connection.prepareStatement(query);
            p.setString(1, memberID);
            rs = p.executeQuery();

            while (rs.next())
            {
                currencyList.add(rs.getString("currency"));
            }
        }
        catch (SystemError e)
        {
            log.error("SQLException",e);
        }
        catch (SQLException e)
        {
            log.error("SQLException",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p);
            Database.closeConnection(connection);
        }
        return currencyList;
    }

    public boolean checkOrderUniqueness(String orderId)
    {
        Connection connection = null;
        boolean uniqueOrder = false;
        try
        {
            connection = Database.getConnection();
            String query = "select orderid from invoice where orderid like '%" + orderId + "%'";
            PreparedStatement p = connection.prepareStatement(query);
            ResultSet rs = p.executeQuery();

            while (rs.next())
            {
                uniqueOrder = true;
            }
            log.debug("order unique query----" + p);
        }
        catch (SystemError e)
        {
            log.error("SystemError----",e);
        }
        catch (SQLException e)
        {
            log.error("SQLException----",e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return uniqueOrder;
    }

    public boolean getMerchantCurrency(String memberID, String currency)
    {
        Connection connection = null;
        boolean isCurrency = false;
        try
        {
            connection = Database.getConnection();
            //String query = "SELECT DISTINCT currency FROM member_account_mapping AS mam, gateway_accounts AS ga, gateway_type AS gt WHERE currency = ? AND mam.memberid = ? AND mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid";
            String query = "select currency from invoice_config where currency = ? and memberid = ?";
            PreparedStatement p = connection.prepareStatement(query);
            p.setString(1,currency);
            p.setString(2,memberID);
            ResultSet rs = p.executeQuery();

            while (rs.next())
            {
                isCurrency = true;
            }
        }
        catch (SQLException e)
        {
            log.error("SQLException----", e);
        }
        catch (SystemError e)
        {
            log.error("SystemError----", e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return isCurrency;
    }

    public TreeMap<String,String> getPaymodeCardtype(String terminalid)
    {
        Connection connection = null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        TreeMap<String, String> map = new TreeMap<String, String>();
        try
        {
            //connection = Database.getConnection();
            connection=Database.getRDBConnection();
            String query = "select paymodeid, cardtypeid from member_account_mapping where terminalid=?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, terminalid);
            rs = preparedStatement.executeQuery();
            while (rs.next())
            {
                map.put(terminalid, GatewayAccountService.getCardType(rs.getString("cardtypeid")) + "-" + GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
            }
        }
        catch (SQLException e)
        {
            log.error("SQLException----", e);
        }
        catch (SystemError e)
        {
            log.error("SystemError----", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return map;
    }

    public void insertSplitInvoice(List<CustomerDetailList> listOfCustomerDetails, int invoiceno, InvoiceVO invoiceVO)
    {
        Connection conn = null;
        String childInvoiceno = "";
        String orderid = "";
        try
        {
            int i=1;
            for (CustomerDetailList customerDetailList : listOfCustomerDetails)
            {
                childInvoiceno = invoiceno + "_" +i;
                orderid = invoiceVO.getDescription() + "_" + i;

                conn = Database.getConnection();
                String query = "INSERT INTO invoice_customer_details(parentinvoiceno,childinvoiceno,amount,orderid,telnocc,telno,customeremail,status,customername,ctoken,dtstamp) VALUES(?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()))";
                PreparedStatement p = conn.prepareStatement(query);
                p.setInt(1, invoiceno);
                p.setString(2, childInvoiceno);
                p.setString(3, customerDetailList.getAmount());
                p.setString(4, orderid);
                p.setString(5, customerDetailList.getCustomerPhoneCC());
                p.setString(6,customerDetailList.getCustomerPhone());
                p.setString(7,customerDetailList.getCustomerEmail());
                p.setString(8,"generated");
                p.setString(9, customerDetailList.getCustomerName());
                p.setString(10, invoiceVO.getCtoken());
                p.executeUpdate();
                log.debug("invoice insert query----"+p);

                try
                {
                    sendInvoiceMailForSplitInvoice(childInvoiceno + "");
                }
                catch(Exception e)
                {
                    log.error("Exception in Sending Mail",e);
                }
                i++;
            }

        }
        catch (SQLException e)
        {
            log.error("SQLException---",e);
        }
        catch (SystemError e)
        {
            log.error("SystemError---",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }

    public Hashtable getSplitInvoiceDetails(String invoiceno)
    {
        Functions functions = new Functions();
        log.debug("Entering getInvoiceDetails");
        Hashtable hash=new Hashtable();

        String query = "SELECT ic.customername,ic.parentinvoiceno,ic.childinvoiceno,ic.trackingid,i.memberid,ic.timestamp,ic.amount,ic.orderid,ic.orderdescription AS orderdesc,ic.customeremail AS custemail,i.remindercounter,ic.status,i.ctoken,ic.accountid,ic.currency,i.redirecturl,ic.country,ic.city,ic.state,ic.zip AS zipcode,ic.street AS address,ic.telnocc AS phonecc,ic.telno AS phone,ic.cancelreason,ic.terminalid,i.expirationPeriod,i.taxamount,i.raisedBy,ic.timestamp,i.dtstamp FROM invoice AS i, invoice_customer_details AS ic WHERE ic.childinvoiceno='" +invoiceno+"' AND ic.parentinvoiceno=i.invoiceno";



        try{
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();

            ResultSet rs = Database.executeQuery(query, conn);


            ResultSetMetaData rsMetaData = rs.getMetaData();
            int count = rsMetaData.getColumnCount();

            int i = 0;
            while (rs.next())
            {

                for (i = 1; i <= count; i++)
                {
                    if (rs.getString(i) != null)
                    {

                        hash.put(rsMetaData.getColumnLabel(i), rs.getString(i));
                        if (null == rs.getString("terminalid") ||  "null".equals(rs.getString("terminalid")))
                        {
                            hash.put("terminalid", "");
                        }
                        else
                        {
                            hash.put("terminalid", rs.getString("terminalid").trim());
                        }
                    }
                }

                hash.put("date",rs.getString("timestamp").substring(0,rs.getString("timestamp").indexOf(" ")));
                hash.put("time",rs.getString("timestamp").substring(rs.getString("timestamp").indexOf(" "),rs.getString("timestamp").length()-1));
                hash.put("listOfProducts",getProductDetails(rs.getString("parentinvoiceno")));

                log.debug("hash for split invoice-----"+hash);

            }


            log.debug("invoice query---" + query);


        }catch (Exception e)
        {
            log.error("Error occured while getting Invoice Details",e);
        }finally
        {
            Database.closeConnection(conn);
        }
        try{
            //conn=Database.getConnection();
            conn=Database.getRDBConnection();
            String trackingid= (String)hash.get("trackingid");
            String accountid= (String) hash.get("accountid");
            String transStatus="";
            if(trackingid!=null)
            {
                String tablename=Database.getTableName(GatewayAccountService.getGatewayAccount(accountid).getGateway());
                String fieldname= tablename.equalsIgnoreCase("transaction_icicicredit")?"icicitransid":"trackingid";
                ResultSet rs =conn.prepareStatement("select status from "+tablename+" where "+fieldname+"="+trackingid).executeQuery();
                rs.next();
                transStatus = rs.getString("status");
                hash.put("transStatus",transStatus);
            }
        }
        catch(Exception e)
        {
            log.error("DB Exception whille fatching Invoice---", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return hash;
    }

    public Hashtable getProductDetails(String invoiceno)
    {
        Hashtable hash = new Hashtable();
        List<ProductList> listOfProducts = new ArrayList<ProductList>();
        String productQuery = "select description,amount,quantity,subtotal from invoice_product_details where invoiceno="+invoiceno;
        try
        {
            conn = Database.getRDBConnection();
            ResultSet rs1 = Database.executeQuery(productQuery, conn);

            while (rs1.next())
            {

                ProductList productList = new ProductList();

                productList.setProductDescription(rs1.getString("description"));
                productList.setProductAmount(rs1.getString("amount"));
                productList.setQuantity(rs1.getString("quantity"));
                productList.setProductTotal(rs1.getString("subtotal"));

                listOfProducts.add(productList);
            }
            hash.put("listOfProducts",listOfProducts);
            log.debug("invoice product query---"+productQuery);

        }
        catch (SQLException e)
        {

        }
        catch (SystemError e)
        {

        }
        finally
        {
            Database.closeConnection(conn);
        }
        return hash;
    }

    public InvoiceVO insertSplitInvoice(int parentInvoiceId, InvoiceVO invoiceVO, List<CustomerDetailList> listOfCustomerDetails) throws SystemError, SQLException
    {
        Connection conn = null;
        conn = Database.getConnection();
        HashMap map = new HashMap();
        Functions functions = new Functions();
        int invoiceId = 0;
        String orderId = "";
        List<CustomerDetailList> customerList = new ArrayList<CustomerDetailList>();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();


        //int i = 1;
        //for (CustomerDetailList customerDetailList : listOfCustomerDetails)

        try
        {
            int listCust = listOfCustomerDetails.size();
            for (int i = 0; i < listCust ; i++)
            {
                orderId = invoiceVO.getDescription() + "_" + i;
                CustomerDetailList customerDetailList = listOfCustomerDetails.get(i);
                String query = "insert into invoice(invoiceno,parentinvoiceno,memberid,amount,orderid,customeremail,redirecturl,currency,orderdescription,country,city,zip,telnocc,telno,street,state,custname,paymodeid,isCredential,username,pwd,question,answer,merchantIpAddress,terminalid,expirationPeriod,raisedby,taxamount,ctoken,dtstamp) values(NULL ,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()))";
                PreparedStatement p = conn.prepareStatement(query);
                p.setInt(1, parentInvoiceId);
                p.setString(2, invoiceVO.getMemberid());
                p.setString(3, customerDetailList.getAmount());
                p.setString(4, orderId);
                p.setString(5, customerDetailList.getCustomerEmail());
                p.setString(6, invoiceVO.getRedirecturl());
                p.setString(7, invoiceVO.getCurrency());
                p.setString(8, invoiceVO.getOrderDescription());
                p.setString(9, invoiceVO.getCountry());
                p.setString(10, invoiceVO.getCity());
                p.setString(11, invoiceVO.getZip());
                p.setString(12, customerDetailList.getCustomerPhoneCC());
                p.setString(13, customerDetailList.getCustomerPhone());
                p.setString(14, invoiceVO.getStreet());
                p.setString(15, invoiceVO.getState());
                p.setString(16, customerDetailList.getCustomerName());
                p.setString(17, invoiceVO.getPaymodeid());
                p.setString(18, invoiceVO.getIsCredential());
                p.setString(19, invoiceVO.getUserName());
                p.setString(20, invoiceVO.getPwd());
                p.setString(21, invoiceVO.getQuestion());
                p.setString(22, invoiceVO.getAnswer());
                p.setString(23, invoiceVO.getMerchantIpAddress());
                p.setString(24, invoiceVO.getTerminalid());
                p.setString(25, invoiceVO.getExpirationPeriod());
                p.setString(26, invoiceVO.getRaisedby());
                p.setString(27, invoiceVO.getGST());
                p.setString(28, invoiceVO.getCtoken());
                log.debug("insert invoice qry--->" + p);
                int num = p.executeUpdate();
                if (num == 1)
                {
                    ResultSet rs = p.getGeneratedKeys();
                    if (rs != null)
                    {
                        while (rs.next())
                        {
                            invoiceId = rs.getInt(1);
                            invoiceVO.setInvoiceno(String.valueOf(invoiceId));
                            customerDetailList.setInvoicenumber(String.valueOf(invoiceId));

                        }
                    }
                }

                String telnocc = invoiceVO.getTelCc();


                invoiceVO.setTelCc(telnocc);
                //for email
                if (invoiceVO.getIsemail().equals("Y"))
                {
                    invoiceVO = sendInvoiceMail(invoiceVO);
                }
                if(invoiceVO.getPartnerDetailsVO()!=null)
                {
                    PartnerDetailsVO partnerDetailsVO = invoiceVO.getPartnerDetailsVO();

                    if(functions.isValueNull(invoiceVO.getTerminalid()))
                        invoiceVO.setTransactionUrl("https://"+partnerDetailsVO.getHostUrl()+"/transaction/PayInvoice?inv=" + invoiceVO.getInvoiceno() + "&t=" +invoiceVO.getTerminalid() + "&ct=" + invoiceVO.getCtoken());
                    else
                        invoiceVO.setTransactionUrl("https://"+partnerDetailsVO.getHostUrl()+"/transaction/PayInvoice?inv=" + invoiceVO.getInvoiceno() + "&ct=" + invoiceVO.getCtoken());


                    try
                    {
                        String text = invoiceVO.getTransactionUrl();
                        transactionLogger.error("text---->"+text);
                        String filePath = QRCODERB.getString("PATH") + "InvoiceQR_" + invoiceVO.getInvoiceno() + ".png";
                        QRCodeWriter qrCodeWriter = new QRCodeWriter();
                        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);

                        Path path = FileSystems.getDefault().getPath(filePath);
                        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

                        File file=new File(filePath);
                        byte[] fileContent=FileUtils.readFileToByteArray(file);
                        String invoiceQRCode= Base64.getEncoder().encodeToString(fileContent);
                        invoiceVO.setTransactionUrlQRCode(invoiceQRCode);
                    }
                    catch (WriterException e)
                    {
                        transactionLogger.error("WriterException---->",e);
                    }
                    catch (IOException e)
                    {
                        transactionLogger.error("IOException---->", e);
                    }
                    catch (Exception e)
                    {
                        transactionLogger.error("Exception---->", e);
                    }
                }
                customerDetailList.setTransactionUrl(invoiceVO.getTransactionUrl());
                customerDetailList.setQR_Code(invoiceVO.getTransactionUrlQRCode());
                customerDetailList.setMerchantInvoiceId(orderId);
                customerList.add(customerDetailList);

                invoiceVO.setCustomerDetailList(customerList);
                //for sms
                invoiceVO = getInvoiceConfigurationDetail(invoiceVO);

                List<ProductList> listProducts = invoiceVO.getProductList();

                if (listProducts != null)
                {
                    if (listProducts.size() > 0)
                    {
                        insertInvoiceProduct(listProducts, Integer.parseInt(invoiceVO.getInvoiceno()));
                    }
                }

                invoiceVO.setTelCc(telnocc);
                //for email
                if (invoiceVO.getIsemail().equals("Y"))
                {
                    invoiceVO = sendInvoiceMail(invoiceVO);
                }
            }
            //for sms
            if (invoiceVO.getSmsactivation().equals("Y"))
            {
                if (invoiceVO.getIssms().equals("Y"))
                {
                    AsynchronousSmsService smsService = new AsynchronousSmsService();
                    String liveUrl = ApplicationProperties.getProperty("COMPANY_LIVE_URL");
                    map.put("custname", invoiceVO.getCustName());
                    map.put("orderdesc", invoiceVO.getOrderDescription());
                    map.put("invoiceno", invoiceVO.getInvoiceno());
                    map.put("currency", invoiceVO.getCurrency());
                    map.put("amount", invoiceVO.getAmount());
                    map.put("phone", invoiceVO.getTelno());
                    map.put("phonecc", invoiceVO.getTelCc());
                    map.put("orderid", invoiceVO.getDescription());
                    map.put("ctoken", invoiceVO.getCtoken());
                    if (!functions.isValueNull(invoiceVO.getTerminalid()))
                    {
                        invoiceVO.setTerminalid("");
                    }
                    map.put("terminalid", invoiceVO.getTerminalid());
                    map.put("companyname", invoiceVO.getMerchantDetailsVO().getCompany_name());
                    map.put("LIVE_URL", liveUrl);
                    smsService.sendSMS(MailEventEnum.GENERATING_INVOICE_BY_MERCHANT, map);


                }
            }
           /* if(invoiceVO.getPartnerDetailsVO()!=null)
            {
                PartnerDetailsVO partnerDetailsVO = invoiceVO.getPartnerDetailsVO();
                if(functions.isValueNull(invoiceVO.getTerminalid()))
                    invoiceVO.setTransactionUrl("https://"+partnerDetailsVO.getHostUrl()+"/transaction/PayInvoice?inv=" + invoiceVO.getInvoiceno() + "&t=" +invoiceVO.getTerminalid() + "&ct=" + invoiceVO.getCtoken());
                else
                    invoiceVO.setTransactionUrl("https://"+partnerDetailsVO.getHostUrl()+"/transaction/PayInvoice?inv=" + invoiceVO.getInvoiceno() + "&ct=" + invoiceVO.getCtoken());
            }*/

        }
        catch (SQLException e)
        {
            log.error("DB Exception whille fatching Invoice---", e);
        }
        catch (SystemError ex)
        {
            log.error("Error occured while insert Split Invoice----", ex);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return invoiceVO;
    }

    private ErrorCodeListVO getErrorVO(ErrorName errorName)
    {
        ErrorCodeVO errorCodeVO=new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO =new ErrorCodeListVO();
        log.debug("error in helper---------->"+errorName.toString());
        errorCodeVO = errorCodeUtils.getErrorCodeFromName(errorName);
        errorCodeListVO.addListOfError(errorCodeVO);
        return errorCodeListVO;
    }

}