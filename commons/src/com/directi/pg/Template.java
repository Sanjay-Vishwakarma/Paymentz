package com.directi.pg;

import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.payment.AbstractPaymentProcess;
import com.payment.PaymentProcessFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;
//import PaymentClient.*;

public class Template
{
    //static Category cat = Category.getInstance(Merchants.class.getName());
    static Logger logger = new Logger(Template.class.getName());
    public static final int noofimages = 3;
    public static Hashtable defaulthash = null;
    public static Hashtable defaultlabelhash = null;
    static StringBuffer creditpage = new StringBuffer();
    static StringBuffer Payvtcreditpage = new StringBuffer();
    static StringBuffer Qwipicreditpage = new StringBuffer();
    static StringBuffer UGScreditpage = new StringBuffer();
    static StringBuffer Ecorecreditpage = new StringBuffer();
    static StringBuffer Gold24creditpage = new StringBuffer();
    static StringBuffer commonCreditpage = new StringBuffer();
    static StringBuffer ddpCreditpage = new StringBuffer();
    static StringBuffer errorpage = new StringBuffer();
    static StringBuffer confirmationpage = new StringBuffer();
    static StringBuffer qwipiconfirmationpage = new StringBuffer();
    static StringBuffer waitpage = new StringBuffer();
    static StringBuffer newwaitpage = new StringBuffer();
    static StringBuffer autoredirectpage = new StringBuffer();
    static StringBuffer proofpage = new StringBuffer();
    static StringBuffer paylinevoucher = new StringBuffer();
    static StringBuffer payinvoice = new StringBuffer();
    static StringBuffer payDollarCreditpage = new StringBuffer();
    static StringBuffer error = new StringBuffer();
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.template");

    public static String path = RB.getString("PATH");
    final static String imagepath = RB.getString("IMAGEPATH");

    //final static String HEADER = RB.getString("HEADER");
    final static String BGCOLOR = RB.getString("BGCOLOR");
    final static String TEXTCOLOR = RB.getString("TEXTCOLOR");
    final static String HIGHLIGHT_TEXT_COLOR = RB.getString("HIGHLIGHT_TEXT_COLOR");
    final static String BACKGROUND = RB.getString("BACKGROUND");


    final static String HEADER_LABEL = RB.getString("HEADER_LABEL");
    final static String BGCOLOR_LABEL = RB.getString("BGCOLOR_LABEL");
    final static String TEXTCOLOR_LABEL = RB.getString("TEXTCOLOR_LABEL");
    final static String HIGHLIGHT_TEXT_COLOR_LABEL = RB.getString("HIGHLIGHT_TEXT_COLOR_LABEL");
    final static String BACKGROUND_LABEL = RB.getString("BACKGROUND_LABEL");

    final static String MAILPATH = RB.getString("MAILPATH");

    static
    {
        //This contains all the paremeters modifiable by merchant
        defaulthash = new Hashtable();
        //defaulthash.put("HEADER", HEADER);
        defaulthash.put("BGCOLOR", BGCOLOR);
        defaulthash.put("TEXTCOLOR", TEXTCOLOR);
        //   defaulthash.put("FONTFACE","arial,verdana,helvetica");
        defaulthash.put("HIGHLIGHT_TEXT_COLOR", HIGHLIGHT_TEXT_COLOR);
        defaulthash.put("BACKGROUND", BACKGROUND);

        //This contains labels for all parameter
        defaultlabelhash = new Hashtable();
        defaultlabelhash.put("HEADER", HEADER_LABEL);
        defaultlabelhash.put("BGCOLOR", BGCOLOR_LABEL);
        defaultlabelhash.put("TEXTCOLOR", TEXTCOLOR_LABEL);
        //  defaultlabelhash.put("FONTFACE","Text Font");
        defaultlabelhash.put("HIGHLIGHT_TEXT_COLOR", HIGHLIGHT_TEXT_COLOR_LABEL);
        defaultlabelhash.put("BACKGROUND", BACKGROUND_LABEL);

        try
        {
            FileInputStream fin = new FileInputStream(path + "creditpage.template");
            BufferedReader br = new BufferedReader(new InputStreamReader(fin));


            String temp = null;

            while ((temp = br.readLine()) != null)
                creditpage.append(temp + "\r\n");

            fin.close();

//            fin = new FileInputStream(path + "payvt_creditpage.template");
//            br = new BufferedReader(new InputStreamReader(fin));
//
//            temp = null;
//
//            while ((temp = br.readLine()) != null)
//                Payvtcreditpage.append(temp + "\r\n");

            fin.close();

            fin = new FileInputStream(path + "qwipi_creditpage.template");
            logger.debug(path + "qwipi_creditpage.template");
            br = new BufferedReader(new InputStreamReader(fin));

            temp = null;

            while ((temp = br.readLine()) != null)
                Qwipicreditpage.append(temp + "\r\n");

            fin.close();

            fin = new FileInputStream(path + "ugs_creditpage.template");
            logger.debug(path + "ugs_creditpage.template");
            br = new BufferedReader(new InputStreamReader(fin));

            temp = null;

            while ((temp = br.readLine()) != null)
                UGScreditpage.append(temp + "\r\n");

            fin.close();

            fin = new FileInputStream(path + "ecore_creditpage.template");
            logger.debug(path + "ecore_creditpage.template");
            br = new BufferedReader(new InputStreamReader(fin));

            temp = null;

            while ((temp = br.readLine()) != null)
                Ecorecreditpage.append(temp + "\r\n");

            fin.close();

            fin = new FileInputStream(path + "error.template");
            br = new BufferedReader(new InputStreamReader(fin));

            temp = null;

            while ((temp = br.readLine()) != null)
                error.append(temp + "\r\n");

            fin.close();


//            fin = new FileInputStream(path + "gold24_creditpage.template");
//            logger.debug(path + "gold24_creditpage.template");
//            br = new BufferedReader(new InputStreamReader(fin));
//
//            temp = null;
//
//            while ((temp = br.readLine()) != null)
//                Gold24creditpage.append(temp + "\r\n");

            fin.close();

            fin = new FileInputStream(path + "common_creditpage.template");
            logger.debug(path + "common_creditpage.template");
            br = new BufferedReader(new InputStreamReader(fin));

            temp = null;

            while ((temp = br.readLine()) != null)
                commonCreditpage.append(temp + "\r\n");

            fin.close();

            fin = new FileInputStream(path + "ddp_creditpage.template");
            logger.debug(path + "ddp_creditpage.template");
            br = new BufferedReader(new InputStreamReader(fin));

            temp = null;

            while ((temp = br.readLine()) != null)
                ddpCreditpage.append(temp + "\r\n");

            fin.close();


            fin = new FileInputStream(path + "paydollar_creditpage.template");
            br = new BufferedReader(new InputStreamReader(fin));

            temp = null;

            while ((temp = br.readLine()) != null)
                payDollarCreditpage.append(temp + "\r\n");

            fin.close();

            fin = new FileInputStream(path + "payline_creditpage.template");
            br = new BufferedReader(new InputStreamReader(fin));

            temp = null;

            while ((temp = br.readLine()) != null)
                paylinevoucher.append(temp + "\r\n");

            fin.close();

            fin = new FileInputStream(path + "payinvoice.template");
            br = new BufferedReader(new InputStreamReader(fin));

            temp = null;

            while ((temp = br.readLine()) != null)
                payinvoice.append(temp + "\r\n");

            fin.close();

            fin = new FileInputStream(path + "errorpage.template");
            br = new BufferedReader(new InputStreamReader(fin));

            temp = null;

            while ((temp = br.readLine()) != null)
                errorpage.append(temp + "\r\n");

            fin.close();

            fin = new FileInputStream(path + "confirmationpage.template");
            logger.debug(path + "confirmationpage.template");
            br = new BufferedReader(new InputStreamReader(fin));
            temp = null;

            while ((temp = br.readLine()) != null)
                confirmationpage.append(temp + "\r\n");

            fin.close();

            fin = new FileInputStream(path + "qwipiconfirmationpage.template");
            logger.debug(path + "qwipiconfirmationpage.template");
            br = new BufferedReader(new InputStreamReader(fin));
            temp = null;

            while ((temp = br.readLine()) != null)
                qwipiconfirmationpage.append(temp + "\r\n");

            fin.close();

            fin = new FileInputStream(path + "waitpage.template");
            logger.debug(path + "waitpage.template");
            br = new BufferedReader(new InputStreamReader(fin));
            temp = null;

            while ((temp = br.readLine()) != null)
                waitpage.append(temp + "\r\n");

            fin.close();

            fin = new FileInputStream(path + "newwaitpage.template");
            logger.debug(path + "newwaitpage.template");
            br = new BufferedReader(new InputStreamReader(fin));
            temp = null;

            while ((temp = br.readLine()) != null)
                newwaitpage.append(temp + "\r\n");

            fin.close();

            fin = new FileInputStream(path + "autoredirectpage.template");
            br = new BufferedReader(new InputStreamReader(fin));
            temp = null;

            while ((temp = br.readLine()) != null)
                autoredirectpage.append(temp + "\r\n");

            fin.close();

            fin = new FileInputStream(path + "authorization.display");
            br = new BufferedReader(new InputStreamReader(fin));
            temp = null;

            while ((temp = br.readLine()) != null)
                proofpage.append(temp + "\r\n");

            fin.close();

        }
        catch (Exception x)
        {
            logger.error("Exception ", x);
        }
    }

    public static String getValue(String key) throws Exception
    {
        return (String) defaulthash.get(key);
    }

    public static String getPath() throws Exception
    {
        return path;
    }

    public static void setPath(String newpath) throws Exception
    {
        path = newpath;
    }

    public static String getPayInvoicePage(String merchantid, Hashtable otherdetails) throws SystemError
    {
        logger.debug("inside getPayInvoicePage ");

        //Adding the request parameter
        Hashtable detailhash = new Hashtable();
        detailhash.put("FONTFACE", "arial,verdana,helvetica");

        //Adding the default settings
        detailhash.putAll(defaulthash);
        //Adding the meberspecific settings
        if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("Y"))
            detailhash.putAll(getMemberTemplateDetails(merchantid));
        else if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("N"))
            detailhash.putAll(defaulthash);

        Template template=new Template();
        /*detailhash.putAll(otherdetails);

        detailhash.put("BACKGROUND", replaceBackgroundImageTag((String) detailhash.get("BACKGROUND"), detailhash));
        detailhash.putAll(template.getMerchantImageConfig(merchantid));
        String viewpage = Functions.replaceTag(payinvoice.toString(), detailhash);*/

        detailhash.putAll(otherdetails);

        detailhash.put("HEADER", replaceImageTag((String) detailhash.get("HEADER"), detailhash));
        detailhash.put("BACKGROUND", replaceBackgroundImageTag((String) detailhash.get("BACKGROUND"), detailhash));

        String viewpage = Functions.replaceTag(payinvoice.toString(), detailhash);
        viewpage = Functions.replaceTag(viewpage, detailhash);

        return viewpage;


    }

    public static String getPaylineCreditPage(String merchantid, Hashtable otherdetails) throws SystemError
    {
        logger.debug("inside getCreditPage ");

        //Adding the request parameter
        Hashtable detailhash = new Hashtable();
        detailhash.put("FONTFACE", "arial,verdana,helvetica");
        Template template=new Template();
        //Adding the default settings
        detailhash.putAll(defaulthash);
        //Adding the meberspecific settings
        if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("Y"))
            detailhash.putAll(getMemberTemplateDetails(merchantid));
        else if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("N"))
            detailhash.putAll(defaulthash);

        detailhash.putAll(otherdetails);

        detailhash.put("BACKGROUND", replaceBackgroundImageTag((String) detailhash.get("BACKGROUND"), detailhash));
        detailhash.putAll(template.getMerchantImageConfig(merchantid));
        String viewpage = Functions.replaceTag(paylinevoucher.toString(), detailhash);

        return viewpage;
    }

    public static String getQwipiCreditPage(String merchantid, Hashtable otherdetails) throws SystemError
    {
        logger.debug("inside getCreditPage ");

        //Adding the request parameter
        Hashtable detailhash = new Hashtable();
        detailhash.put("FONTFACE", "arial,verdana,helvetica");

        //Adding the default settings
        detailhash.putAll(defaulthash);
        //Adding the meberspecific settings
        if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("Y"))
            detailhash.putAll(getMemberTemplateDetails(merchantid));
        else if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("N"))
            detailhash.putAll(defaulthash);
        Template template=new Template();
        detailhash.putAll(otherdetails);

        detailhash.put("BACKGROUND", replaceBackgroundImageTag((String) detailhash.get("BACKGROUND"), detailhash));
        detailhash.putAll(template.getMerchantImageConfig(merchantid));
        String viewpage = Functions.replaceTag(Qwipicreditpage.toString(), detailhash);

        return viewpage;
    }

    public static String getUGSCreditPage(String merchantid, Hashtable otherdetails) throws SystemError
    {
        logger.debug("inside getCreditPage ");
         Template template=new Template();
        //Adding the request parameter
        Hashtable detailhash = new Hashtable();
        detailhash.put("FONTFACE", "arial,verdana,helvetica");

        //Adding the default settings
        detailhash.putAll(defaulthash);
        //Adding the meberspecific settings
        if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("Y"))
            detailhash.putAll(getMemberTemplateDetails(merchantid));
        else if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("N"))
            detailhash.putAll(defaulthash);

        detailhash.putAll(otherdetails);

        detailhash.put("BACKGROUND", replaceBackgroundImageTag((String) detailhash.get("BACKGROUND"), detailhash));
        detailhash.putAll(template.getMerchantImageConfig(merchantid));
        String viewpage = Functions.replaceTag(UGScreditpage.toString(), detailhash);

        return viewpage;
    }

    public static String getEcoreCreditPage(String merchantid, Hashtable otherdetails) throws SystemError
    {
        logger.debug("inside getCreditPage ");


        Template template=new Template();

        //Adding the request parameter
        Hashtable detailhash = new Hashtable();
        detailhash.put("FONTFACE", "arial,verdana,helvetica");

        //Adding the default settings
        detailhash.putAll(defaulthash);
        //Adding the meberspecific settings
        if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("Y"))
            detailhash.putAll(getMemberTemplateDetails(merchantid));
        else if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("N"))
            detailhash.putAll(defaulthash);

        detailhash.putAll(otherdetails);
        detailhash.put("BACKGROUND", replaceBackgroundImageTag((String) detailhash.get("BACKGROUND"), detailhash));
        detailhash.putAll(template.getMerchantImageConfig(merchantid));
        String viewpage = Functions.replaceTag(Ecorecreditpage.toString(), detailhash);

        return viewpage;
    }

    public static String getGold24CreditPage(String merchantid, Hashtable otherdetails) throws SystemError
    {
        logger.debug("inside getCreditPage ");
        //Adding the request parameter
        Hashtable detailhash = new Hashtable();
        detailhash.put("FONTFACE", "arial,verdana,helvetica");
        Template template=new Template();
        detailhash.putAll(template.getMerchantImageConfig(merchantid));
        //Adding the default settings
        detailhash.putAll(defaulthash);
        //Adding the meberspecific settings
        if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("Y"))
            detailhash.putAll(getMemberTemplateDetails(merchantid));
        else if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("N"))
            detailhash.putAll(defaulthash);

        detailhash.putAll(otherdetails);

        detailhash.put("BACKGROUND", replaceBackgroundImageTag((String) detailhash.get("BACKGROUND"), detailhash));
        detailhash.putAll(template.getMerchantImageConfig(merchantid));
        String viewpage = Functions.replaceTag(Gold24creditpage.toString(), detailhash);

        return viewpage;
    }

    public static String getCommonCreditPage(String merchantid, Hashtable otherdetails) throws SystemError
    {
        logger.debug("inside getCreditPage ");
        //Adding the request parameter
        Hashtable detailhash = new Hashtable();
        detailhash.put("FONTFACE", "arial,verdana,helvetica");
        Template template=new Template();

        //Adding the default settings
        detailhash.putAll(defaulthash);
        //Adding the meberspecific settings
        if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("Y"))
            detailhash.putAll(getMemberTemplateDetails(merchantid));
        else if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("N"))
            detailhash.putAll(defaulthash);

        detailhash.putAll(otherdetails);

        detailhash.put("BACKGROUND", replaceBackgroundImageTag((String) detailhash.get("BACKGROUND"), detailhash));
        detailhash.putAll(template.getMerchantImageConfig(merchantid));

        Hashtable extrafields=getSprcificTemplate((String) otherdetails.get("accountid"));
        if(extrafields!=null)
        {
            if(extrafields.get("ADDITIONALJS")!=null)
            {
                detailhash.put("ADDITIONALJS",extrafields.get("ADDITIONALJS"));
            }
            else
            {
                detailhash.put("ADDITIONALJS"," ");
            }

            if(extrafields.get("ADDITIONALFIELDS")!=null)
            {
                detailhash.put("ADDITIONALFIELDS",extrafields.get("ADDITIONALFIELDS"));
            }
            else
            {
                detailhash.put("ADDITIONALFIELDS"," ");
            }
        }
        else
        {
            detailhash.put("ADDITIONALJS","&nbsp;");
            detailhash.put("ADDITIONALFIELDS","&nbsp;");
        }

        String viewpage = Functions.replaceTag(commonCreditpage.toString(), detailhash);

        return viewpage;
    }

    public static String getDdpCreditPage(String merchantid, Hashtable otherdetails) throws SystemError
    {
        logger.debug("inside getCreditPage ");
        Template template=new Template();

        //Adding the request parameter
        Hashtable detailhash = new Hashtable();
        detailhash.put("FONTFACE", "arial,verdana,helvetica");

        //Adding the default settings
        detailhash.putAll(defaulthash);
        //Adding the meberspecific settings
        if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("Y"))
            detailhash.putAll(getMemberTemplateDetails(merchantid));
        else if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("N"))
            detailhash.putAll(defaulthash);

        detailhash.putAll(otherdetails);

        detailhash.put("BACKGROUND", replaceBackgroundImageTag((String) detailhash.get("BACKGROUND"), detailhash));
        detailhash.putAll(template.getMerchantImageConfig(merchantid));
        String viewpage = Functions.replaceTag(ddpCreditpage.toString(), detailhash);

        return viewpage;
    }

    public static String getPayvtCreditPage(String merchantid, Hashtable otherdetails) throws SystemError
    {
        logger.debug("inside getCreditPage ");
        Template template=new Template();
        //Adding the request parameter
        Hashtable detailhash = new Hashtable();
        detailhash.put("FONTFACE", "arial,verdana,helvetica");

        //Adding the default settings
        detailhash.putAll(defaulthash);
        //Adding the meberspecific settings
        if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("Y"))
            detailhash.putAll(getMemberTemplateDetails(merchantid));
        else if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("N"))
            detailhash.putAll(defaulthash);

        detailhash.putAll(otherdetails);


        detailhash.put("BACKGROUND", replaceBackgroundImageTag((String) detailhash.get("BACKGROUND"), detailhash));
        detailhash.putAll(template.getMerchantImageConfig(merchantid));
        String viewpage = Functions.replaceTag(Payvtcreditpage.toString(), detailhash);

        return viewpage;
    }

    public static String getPayDollarCreditPage(String merchantid, Hashtable otherdetails) throws SystemError
    {
        logger.debug("inside getCreditPage ");

        //Adding the request parameter
        Hashtable detailhash = new Hashtable();
        detailhash.put("FONTFACE", "arial,verdana,helvetica");
         Template template=new Template();
        //Adding the default settings
        detailhash.putAll(defaulthash);
        //Adding the meberspecific settings
        if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("Y"))
            detailhash.putAll(getMemberTemplateDetails(merchantid));
        else if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("N"))
            detailhash.putAll(defaulthash);

        detailhash.putAll(otherdetails);

        detailhash.put("BACKGROUND", replaceBackgroundImageTag((String) detailhash.get("BACKGROUND"), detailhash));
        detailhash.putAll(template.getMerchantImageConfig(merchantid));
        String viewpage = Functions.replaceTag(payDollarCreditpage.toString(), detailhash);

        return viewpage;
    }


    public static String getCreditPage(String merchantid, Hashtable otherdetails) throws SystemError
    {
        logger.debug("inside getCreditPage ");
        Template template=new Template();
        //Adding the request parameter
        Hashtable detailhash = new Hashtable();
        detailhash.put("FONTFACE", "arial,verdana,helvetica");

        //Adding the default settings
        detailhash.putAll(defaulthash);
        //Adding the meberspecific settings
        if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("Y"))
            detailhash.putAll(getMemberTemplateDetails(merchantid));
        else if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("N"))
            detailhash.putAll(defaulthash);

        detailhash.putAll(otherdetails);

        detailhash.put("BACKGROUND", replaceBackgroundImageTag((String) detailhash.get("BACKGROUND"), detailhash));
        detailhash.putAll(template.getMerchantImageConfig(merchantid));
        String viewpage = Functions.replaceTag(creditpage.toString(), detailhash);

        return viewpage;
    }

    public static String getConfirmationPage(String accountId, Hashtable otherdetails) throws SystemError
    {
        logger.debug("inside getConfirmationPage");

        Template template=new Template();
        //Adding the request parameter
        Hashtable detailhash = new Hashtable();
        detailhash.put("FONTFACE", "arial,verdana,helvetica");

        //Adding the default settings
        detailhash.putAll(defaulthash);
        //Adding the meberspecific settings
        if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("Y"))
            detailhash.putAll(getMemberTemplateDetails(accountId));
        else if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("N"))
            detailhash.putAll(defaulthash);

        detailhash.putAll(otherdetails);
        if(otherdetails.get("DISPLAYNAME").equals(""))
        {
        AbstractPaymentGateway paymentGateway = AbstractPaymentGateway.getGateway(accountId);
        String displayName = paymentGateway.getDisplayName();

        detailhash.put("DISPLAYNAME", displayName);
        }
        else
        {

            detailhash.put("DISPLAYNAME",otherdetails.get("DISPLAYNAME"));
        }

        detailhash.put("BACKGROUND", replaceBackgroundImageTag((String) detailhash.get("BACKGROUND"), detailhash));
        detailhash.putAll(template.getMerchantImageConfig(accountId));
        String viewpage = Functions.replaceTag(confirmationpage.toString(), detailhash);

        return viewpage;
    }
    public static String getConfirmationPage(String memberid, Hashtable otherdetails,String accountId) throws SystemError
    {
        logger.debug("inside getConfirmationPage");
        Template template=new Template();
        //Adding the request parameter
        Hashtable detailhash = new Hashtable();
        detailhash.put("FONTFACE", "arial,verdana,helvetica");

        //Adding the default settings
        detailhash.putAll(defaulthash);
        //Adding the meberspecific settings
        if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("Y"))
            detailhash.putAll(getMemberTemplateDetails(memberid));
        else if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("N"))
            detailhash.putAll(defaulthash);

        detailhash.putAll(otherdetails);

        AbstractPaymentGateway paymentGateway = AbstractPaymentGateway.getGateway(accountId);
        String displayName = paymentGateway.getDisplayName();

        detailhash.put("DISPLAYNAME", displayName);

        detailhash.put("BACKGROUND", replaceBackgroundImageTag((String) detailhash.get("BACKGROUND"), detailhash));
        detailhash.putAll(template.getMerchantImageConfig(memberid));
        String viewpage = Functions.replaceTag(confirmationpage.toString(), detailhash);

        return viewpage;
    }

    public static String getQwipiConfirmationPage(String memberid, Hashtable otherdetails,String accountId) throws SystemError
    {
        logger.debug("inside getConfirmationPage");
        //Adding the request parameter
        Hashtable detailhash = new Hashtable();
        detailhash.put("FONTFACE", "arial,verdana,helvetica");

        //Adding the default settings
        detailhash.putAll(defaulthash);
        //Adding the meberspecific settings
        if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("Y"))
            detailhash.putAll(getMemberTemplateDetails(memberid));
        else if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("N"))
            detailhash.putAll(defaulthash);

        detailhash.putAll(otherdetails);
        Template template=new Template();
        detailhash.putAll(template.getMerchantImageConfig(memberid));

        detailhash.put("DISPLAYNAME", otherdetails.get("displayname"));

        detailhash.put("BACKGROUND", replaceBackgroundImageTag((String) detailhash.get("BACKGROUND"), detailhash));
        detailhash.putAll(template.getMerchantImageConfig(memberid));
        String viewpage = Functions.replaceTag(qwipiconfirmationpage.toString(), detailhash);

        return viewpage;
    }

    public static String getErrorPage(String merchantid, Hashtable otherdetails) throws SystemError
    {
        logger.debug("inside getErrorPage");

        Template template=new Template();


        //Adding the request parameter
        Hashtable detailhash = new Hashtable();
        detailhash.put("FONTFACE", "arial,verdana,helvetica");

        //Adding the default settings
        detailhash.putAll(defaulthash);
        //Adding the meberspecific settings
        if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("Y"))
            detailhash.putAll(getMemberTemplateDetails(merchantid));
        else if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("N"))
            detailhash.putAll(defaulthash);

        detailhash.putAll(otherdetails);

        detailhash.put("BACKGROUND", replaceBackgroundImageTag((String) detailhash.get("BACKGROUND"), detailhash));
        detailhash.putAll(template.getMerchantImageConfig(merchantid));
        String viewpage = Functions.replaceTag(errorpage.toString(), detailhash);

        return viewpage;
    }

    public static String getError(String merchantid, Hashtable otherdetails) throws SystemError
    {
        logger.debug("inside getError");
        //Adding the request parameter
        Hashtable detailhash = new Hashtable();
        detailhash.put("FONTFACE", "arial,verdana,helvetica");
        Template template=new Template();

        //Adding the default settings
        detailhash.putAll(defaulthash);
        //Adding the meberspecific settings
        if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("Y"))
            detailhash.putAll(getMemberTemplateDetails(merchantid));
        else if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("N"))
            detailhash.putAll(defaulthash);

        detailhash.putAll(otherdetails);

        detailhash.put("BACKGROUND", replaceBackgroundImageTag((String) detailhash.get("BACKGROUND"), detailhash));
        detailhash.putAll(template.getMerchantImageConfig(merchantid));
        String viewpage = Functions.replaceTag(error.toString(), detailhash);

        return viewpage;
    }


    public static String getWaitPage(String merchantid, Hashtable otherdetails) throws SystemError
    {
        logger.debug("inside getWaitPage");
        //Adding the request parameter
        Hashtable detailhash = new Hashtable();
        detailhash.put("FONTFACE", "arial,verdana,helvetica");
        Template template=new Template();

        //Adding the default settings
        detailhash.putAll(defaulthash);
        //Adding the meberspecific settings
        if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("Y"))
            detailhash.putAll(getMemberTemplateDetails(merchantid));
        else if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("N"))
            detailhash.putAll(defaulthash);

        detailhash.putAll(otherdetails);

        detailhash.put("BACKGROUND", replaceBackgroundImageTag((String) detailhash.get("BACKGROUND"), detailhash));
        detailhash.putAll(template.getMerchantImageConfig(merchantid));
        String viewpage = Functions.replaceTag(waitpage.toString(), detailhash);

        return viewpage;
    }

    public static String getNewWaitPage() throws SystemError
    {
        String viewpage = newwaitpage.toString();

        return viewpage;
    }


    public static String getAutoRedirectPage(String merchantid, Hashtable otherdetails) throws SystemError
    {
        logger.debug("inside getAutoRedirectPage");


        String viewpage = Functions.replaceTag(autoredirectpage.toString(), otherdetails);

        return viewpage;
    }

    public static String getProofPage(String merchantid, Hashtable otherdetails) throws SystemError
    {
        logger.debug("inside getProofPage ");
        //Adding the request parameter
        Hashtable detailhash = new Hashtable();
        detailhash.put("FONTFACE", "arial,verdana,helvetica");

        //Adding the default settings
        detailhash.putAll(defaulthash);
        //Adding the meberspecific settings
        if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("Y"))
            detailhash.putAll(getMemberTemplateDetails(merchantid));
        else if (otherdetails.get("TEMPLATE") != null && (otherdetails.get("TEMPLATE")).equals("N"))
            detailhash.putAll(defaulthash);

        detailhash.putAll(otherdetails);

        String proofPage = (String) detailhash.get("PROOFPAGE");

        logger.debug(" Proof page for " + merchantid + " " + proofPage);
        if (Functions.parseData(proofPage) == null)
            proofPage = proofpage.toString();  //If merchant has not set it use default;

        /*ICICIPaymentGateway icici = new ICICIPaymentGateway(merchantid);
        String displayName=icici.getDisplayName();

        detailhash.put("DISPLAYNAME",displayName);*/
        Template template = new Template();
        detailhash.putAll(template.getMerchantImageConfig(merchantid));

        detailhash.put("BACKGROUND", replaceBackgroundImageTag((String) detailhash.get("BACKGROUND"), detailhash));

        String viewpage = Functions.replaceTag(proofPage, detailhash);

        return viewpage;
    }

    public static Hashtable getMemberTemplateDetails(String merchantid) throws SystemError
    {
        logger.debug("Entering getMemberTemplatDetails");
        Hashtable detailhash = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        try
        {
            //con = Database.getConnection();
            con=Database.getRDBConnection();
            String query = "select name,value from template_preferences where memberid= ? ";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, merchantid);
            detailhash = Functions.getDetailedHashFromResultSet(pstmt.executeQuery());
        }
        catch (Exception e)
        {
            logger.error("Leaving Merchants throwing SQL Exception as System Error : ", e);
            throw new SystemError("Error : " + e.getMessage());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }

        //logger.debug(" Got detailed hash " + detailhash);

        if (detailhash != null)
            return detailhash;
        else
            return new Hashtable();
    }

    public static void ChangeTemplate(Hashtable details, String merchantid) throws SystemError
    {
        logger.debug("entering inside ChangeTemplate");
        Hashtable merchantTemplateHash = getMemberTemplateDetails(merchantid);
        StringBuffer query;
        Enumeration detailsEnum = details.keys();
        Connection con = Database.getConnection();
        PreparedStatement p1;

        try
        {
            while (detailsEnum.hasMoreElements())
            {
                String name = (String) detailsEnum.nextElement();

                if (merchantTemplateHash != null)
                {


                    if (merchantTemplateHash.get(name) != null)
                    {
                        query = new StringBuffer("update template_preferences set ");
                        query.append("value=?  ");
                        query.append("where memberid =? and name=?");

                        p1 = con.prepareStatement(query.toString());
                        p1.setString(1, (String) details.get(name));
                        p1.setString(2, merchantid);
                        p1.setString(3, name);
                    }
                    else
                    {
                        query = new StringBuffer("insert into template_preferences(memberid,name,value) values (?,?,?)");

                        p1 = con.prepareStatement(query.toString());
                        p1.setString(1, merchantid);
                        p1.setString(2, name);
                        p1.setObject(3, details.get(name));
                    }
                }
                else
                {
                    query = new StringBuffer("insert into template_preferences(memberid,name,value) values ( ?,?,?)");

                    p1 = con.prepareStatement(query.toString());
                    p1.setString(1, merchantid);
                    p1.setString(2, name);
                    p1.setObject(3, details.get(name));
                }

                p1.executeUpdate();
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException occure", se);
        }
        catch (Exception e)
        {
            logger.error("Exception ::::", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally {
            Database.closeConnection(con);
        }
        logger.debug("leaving ChangeTemplate");
    }

    public static void removeFile(String name, String merchantid) throws SystemError
    {
        logger.debug("entering removeFile");
        int i = 0;
        Connection con=null;
        try
        {
            con = Database.getConnection();
            String s1 = "delete from template_preferences where memberid=? and name=?";
            PreparedStatement p1 = con.prepareStatement(s1);
            p1.setString(1, merchantid);
            p1.setString(2, name);
            i = p1.executeUpdate();
        }
        catch (Exception e)
        {
            logger.error("Exception ", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(con);
        }
        logger.debug("leaving removeFile deleting " + i + " file");
    }

    public static String getResellerLogo(String resellerid, String url)
    {
        logger.debug("resellerid " + resellerid);
        logger.debug("logo " + resellerid);

        StringBuffer sbuf = new StringBuffer();
        try
        {
            HttpURLConnection connection = (HttpURLConnection) (new URL(url)).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.connect();

            String inputLine = null;

            //out.print("resellerid="+resellerid);
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            //in = new BufferedReader(new InputStreamReader(viceurl.openStream()));
            while ((inputLine = in.readLine()) != null)
            {
                if (inputLine != null)
                    sbuf.append(inputLine);

            }

        }
        catch (Exception ex)
        {
            logger.error("Exception ", ex);
        }

        return sbuf.toString();
    }

    public static String replaceImageTag(String data, Hashtable values)
    {
        logger.debug("  Header " + data);
        Hashtable newvalues = new Hashtable();

        for (int i = 1; i <= noofimages; i++)
        {
            if (values.get("IMAGE" + i) != null)
            {
                newvalues.put("IMAGE" + i, "<img border=\"0\" src=\"" + imagepath + values.get("IMAGE" + i) + "\">");

                //values.remove("IMAGE"+i);
            }
        }
        //newvalues.putAll(values);
        logger.debug("  replaceImageTag newvalues ");
        return Functions.replaceTag(data, newvalues);
    }

    public static String replaceBackgroundImageTag(String data, Hashtable values)
    {

        Hashtable newvalues = new Hashtable();

        for (int i = 1; i <= noofimages; i++)
        {
            if (values.get("IMAGE" + i) != null)
            {
                newvalues.put("IMAGE" + i, imagepath + values.get("IMAGE" + i));
                //values.remove("IMAGE"+i);
            }
        }
        //newvalues.putAll(values);
        logger.debug("  replaceImageTag newvalues ");
        return Functions.replaceTag(data, newvalues);
    }

    public Hashtable getMerchantImageConfig(String memberid)
    {
        Connection con=null;
        Hashtable logoDetails=new Hashtable();
        String merchantImageName="";
        String partnerImageName="";
        String partnerid="";
        String partnerName="";
        String isMerchantLogoCheck="";
        String isPoweredbyLogo="";
        try
        {
            con =Database.getConnection();
            String merchantQry="SELECT ismerchantlogo,merchantlogoname,isPoweredBy,partnerId FROM members WHERE memberid=?";
            PreparedStatement preparedStatement=con.prepareStatement(merchantQry);
            preparedStatement.setString(1,memberid);
            ResultSet rs=preparedStatement.executeQuery();
            if(rs.next())
            {
                merchantImageName=rs.getString("merchantlogoname");
                isMerchantLogoCheck=rs.getString("ismerchantlogo");
                isPoweredbyLogo=rs.getString("isPoweredBy");
                partnerid=rs.getString("partnerId");
            }
            if(partnerid!=null)
            {
                String partnerQuery="SELECT partnerName, logoName FROM partners WHERE partnerId=?";
                PreparedStatement preparedStatement1=con.prepareStatement(partnerQuery);
                preparedStatement1.setString(1,partnerid);
                ResultSet resultSet=preparedStatement1.executeQuery();
                if(resultSet.next())
                {
                    partnerImageName=resultSet.getString("logoName");
                    partnerName=resultSet.getString("partnerName");
                }
            }

            if(isPoweredbyLogo.equals("Y"))
            {
               logoDetails.put("LOGO","<p align=\"left\"><a href=\"http://www.pz.com\"><IMG border=0 height=40 src=\"/icici/images/logo2.jpg\" width=105></a></p>");
            }
            else
            {
                logoDetails.put("LOGO","<p align=\"left\"></p>");
            }

            if(isMerchantLogoCheck.equals("Y") && merchantImageName!=null && !merchantImageName.equals(""))
            {
                logoDetails.put("HEADER","<img src=/merchant/images/"+merchantImageName+" > <br> "+partnerName+" Payment Gateway.");
            }
            else
            {
                logoDetails.put("HEADER","<img src=/merchant/images/"+partnerImageName+" > <br> "+partnerName+" Payment Gateway.");
            }

            logoDetails.put("PARTNERLOGO", "<img border=\"0\" height=40 width=105 src=\"/merchant/images/"+partnerImageName+"\" >");
        }
        catch (SystemError systemError)
        {
            logger.error("ERROR",systemError);
        }
        catch (SQLException e)
        {
            logger.error("ERROR",e);  //To change body of catch statement use File | Settings | File Templates.
        }
        finally
        {
            Database.closeConnection(con);
        }
        return logoDetails;
    }

    public static Hashtable getSprcificTemplate(String accountid)
    {
        String fileName="";
        Hashtable additionalFields=new Hashtable();
        AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(accountid));
        fileName=paymentProcess.getSpecificCreditPageTemplate();
        if(fileName!=null)
        {
            try
            {
                StringBuilder additionalTemplateContaint=new StringBuilder();
                FileInputStream fin = new FileInputStream(path +fileName);
                BufferedReader br = new BufferedReader(new InputStreamReader(fin));

                String temp = null;

                while ((temp = br.readLine()) != null)
                {
                    additionalTemplateContaint.append(temp + "\r\n");
                }
                fin.close();

                additionalFields=Functions.getAdditionalFields(additionalTemplateContaint.toString());

            }
            catch (FileNotFoundException e)
            {
                logger.error("File not found",e);
            }
            catch (IOException e)
            {
                logger.error("IO Exception",e);
            }

        }
        return additionalFields;
    }
}