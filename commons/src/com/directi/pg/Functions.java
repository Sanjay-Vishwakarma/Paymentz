package com.directi.pg;

import com.directi.pg.core.CardUsageType;
import com.directi.pg.core.EuCountryCode;
import com.directi.pg.core.EuCountryCodeA2;
import com.directi.pg.core.EuCountryCodeA3;
import com.directi.pg.core.paymentgateway.*;
import com.directi.pg.encryption.blowfish.BlowfishEasy;
import com.google.gson.Gson;
import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import com.jcraft.jsch.*;
import com.logicboxes.util.ApplicationProperties;
import com.manager.BinVerificationManager;
import com.manager.TransactionManager;
import com.manager.vo.BinResponseVO;
import com.manager.vo.TransactionDetailsVO;
import com.manager.vo.merchantmonitoring.DateVO;
import com.manager.vo.morrisBarVOs.Data;
import com.payment.Alweave.AlweavePaymentGateway;
import com.payment.CBCG.CBCGPaymentGateway;
import com.payment.CashflowsCaibo.CashFlowsCaiboPaymentGateway;
import com.payment.alphapay.AlphapayPaymentGateway;
import com.payment.bitclear.BitClearPaymentGateway;
import com.payment.bnmquick.BnmQuickPaymentGateway;
import com.payment.cajarural.CajaRuralPaymentGateway;
import com.payment.common.core.CommResponseVO;
import com.payment.continentPay.ContinentPayPaymentGateway;
import com.payment.DusPay.DusPayPaymentGateway;
import com.payment.DusPayCard.DusPayCardPaymentGateway;
import com.payment.EPaySolution.EPaySolutionGateway;
import com.payment.Easebuzz.EaseBuzzPaymentGateway;
import com.payment.Ecommpay.EcommpayPaymentGateway;
import com.payment.FlutterWave.FlutterWavePaymentGateway;
import com.payment.FrickBank.core.FrickBankPaymentGateWay;
import com.payment.Gpay.GpayPaymentzGateway;
import com.payment.KortaPay.KortaPayPaymentGateway;
import com.payment.LetzPay.LetzPayPaymentGateway;
import com.payment.Mail.MailPlaceHolder;
import com.payment.Oculus.OculusPaymentGateway;
import com.payment.OneRoadPayments.OneRoadPaymentGateway;
import com.payment.PBSAmex.PBSPaymentGatewayAmex;
import com.payment.PayEasyWorld.PayEasyWorldPaymentGateway;
import com.payment.PayMitco.core.PayMitcoPaymentGateway;
import com.payment.ReitumuBank.core.ReitumuBankMerchantPaymentGateway;
import com.payment.ReitumuBank.core.ReitumuBankSMSPaymentGateway;
import com.payment.STS.core.STSPaymentGateway;
import com.payment.TWDTaiwan.TWDTaiwanPaymentGateway;
import com.payment.Triple000.Triple000PaymentGateway;
import com.payment.Wirecardnew.WireCardNPaymentGateway;
import com.payment.aamarpay.AamarPayPaymentGateway;
import com.payment.acqra.AcqraPaymentGateway;
import com.payment.airpay.AirpayPaymentGateway;
import com.payment.airtel.AirtelUgandaPaymentGateway;
import com.payment.flexepin.FlexepinPaymentGateway;
import com.payment.paygsmile.PayGSmilePaymentGateway;
import com.payment.smartfastpay.SmartFastPayPaymentGateway;
import com.payment.swiffy.SwiffyPaymentGateway;
import com.payment.transfr.TransfrPaymentGateway;
import com.payment.allPay88.AllPay88PaymentGateway;
import com.payment.alliedwalled.core.AlliedWalletPaymentGateway;
import com.payment.apco.core.ApcoPaymentGateway;
import com.payment.apcoFastpay.ApcoFastpayPaymentGateway;
import com.payment.apcofastpayv6.ApcoFastPayV6PaymentGateway;
import com.payment.apexpay.ApexPayPaymentGateway;
import com.payment.appletree.AppleTreeCellulantPaymentGateway;
import com.payment.arenaplus.core.ArenaPlusPaymentGateway;
import com.payment.asiancheckout.AsianCheckoutPaymentGateway;
import com.payment.auxpay_payment.core.AuxPayPaymentGateway;
import com.payment.awepay.AwepayBundle.core.AwepayPaymentGateway;
import com.payment.b4payment.B4PaymentGateway;
import com.payment.bankone.BankonePaymentGateway;
import com.payment.beekash.BeekashPaymentGateway;
import com.payment.bennupay.BennupayPaymentGateway;
import com.payment.bhartiPay.BhartiPayPaymentGateway;
import com.payment.billdesk.BillDeskPaymentGateway;
import com.payment.bitcoinpayget.BitcoinPaygatePaymentGateway;
import com.payment.boltpay.BoltPayPaymentGateway;
import com.payment.borgun.core.BorgunPaymentGateway;
import com.payment.brd.BRDPaymentGateway;
import com.payment.cardinity.CardinityPaymentGateway;
import com.payment.cardpay.CardPayPaymentGateway;
import com.payment.cashflow.core.CashFlowPaymentGateway;
import com.payment.cashfree.CashFreePaymentGateway;
import com.payment.cellulant.AtCellulantPaymentGateway;
import com.payment.clearsettle.ClearSettleHPPGateway;
import com.payment.clearsettle.ClearSettlePaymentGateway;
import com.payment.clearsettle.ClearSettleVoucherPaymentGateway;
import com.payment.cleveland.ClevelandPaymentGateway;
import com.payment.cupUPI.UnionPayInternationalPaymentGateway;
import com.payment.curo.CuroPaymentGateway;
import com.payment.cybersource.CyberSourcePaymentGateway;
import com.payment.cybersource2.AtCyberSourcePaymentGateway;
import com.payment.decta.core.DectaMerchantPaymentGateway;
import com.payment.decta.core.DectaSMSPaymentGateway;
import com.payment.dectaNew.DectaNewPaymentGateway;
import com.payment.doku.DokuPaymentGateway;
import com.payment.duspaydirectdebit.DusPayDDPaymentGateway;
import com.payment.dvg_payment.DVGPaymentGateway;
import com.payment.easypay.EasyPaymentGateway;
import com.payment.easypaymentz.EasyPaymentzPaymentGateway;
import com.payment.ecomprocessing.ECPCPPaymentGateway;
import com.payment.ecomprocessing.ECPPaymentGateway;
import com.payment.elegro.ElegroPaymentGateway;
import com.payment.emax_high_risk.core.EmaxHighRiskPaymentGateway;
import com.payment.emerchantpay.EMerchantPayPaymentGateway;
import com.payment.emexpay.EmexpayPaymentGateway;
import com.payment.ems.core.EMSPaymentGateway;
import com.payment.epay.EpayPaymentGateway;
import com.payment.euroeximbank.EuroEximBankPaymentGateway;
import com.payment.europay.core.EuroPayPaymentGateway;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.ezpaynow.EzPayNowPaymentGateway;
import com.payment.fenige.FenigePaymentGateway;
import com.payment.flwBarter.FlutterWaveBarterPaymentGateway;
import com.payment.giftpay.GiftPayPaymentGateway;
import com.payment.globalgate.GlobalGatePaymentGateway;
import com.payment.gold24.core.Gold24PaymentGateway;
import com.payment.hdfc.HDFCPaymentGateway;
import com.payment.iMerchantPay.iMerchantPaymentGateway;
import com.payment.icard.ICardPaymentGateway;
import com.payment.icici.INICICIPaymentGateway;
import com.payment.ilixium.IlixiumPaymentGateway;
import com.payment.imoneypay.IMoneyPayPaymentGateway;
import com.payment.jeton.JetonPaymentGateway;
import com.payment.jeton.JetonVoucherPaymentGateway;
import com.payment.jpbanktransfer.JPBTPaymentGateway;
import com.payment.kcp.KCPPaymentGateway;
import com.payment.knox.KnoxPaymentGateway;
import com.payment.kotakbank.core.KotakAllPayCardPaymentGateway;
import com.payment.libill_payment.core.LibillPaymentGateway;
import com.payment.luqapay.JetonCardPaymentGateway;
import com.payment.mtn.MTNUgandaPaymentGateway;
import com.payment.lyra.LyraPaymentGateway;
import com.payment.nestpay.NestPayPaymentGateway;
import com.payment.nexi.NexiPaymentGateway;
import com.payment.ninja.NinjaWalletPaymentGateway;
import com.payment.npayon.NPayOnGateway;
import com.payment.octapay.OctapayPaymentGateway;
import com.payment.omnipay.OmnipayPaymentGateway;
import com.payment.boombill.BoomBillPaymentGateway;
import com.payment.infipay.InfiPayPaymentGateway;
//import com.payment.fidypay.FidyPayPaymentGateway;
import com.payment.onlinepay.OnlinePayPaymentGateway;
import com.payment.opx.OPXPaymentGateway;
import com.payment.p4.gateway.P4CreditCardPaymentGateway;
import com.payment.p4.gateway.P4DirectDebitPaymentGateway;
import com.payment.p4.gateway.P4PaymentGateway;
import com.payment.paspx.PaspxPaymentGateway;
import com.payment.payGateway.core.PaygatewayPaymentGateway;
import com.payment.paySafeCard.PaySafeCardPaymentGateway;
import com.payment.payVaultPro.PayVaultProPaymentGateway;
import com.payment.payboutique.PayBoutiquePaymentGateway;
import com.payment.payclub.PayClubPaymentGateway;
import com.payment.payeezy.PayeezyPaymentGateway;
import com.payment.payforasia.core.PayforasiaPaymentGateway;
import com.payment.payg.PayGPaymentGateway;
import com.payment.payhost.PayHostPayGatePaymentGateway;
import com.payment.payneteasy.PayneteasyGateway;
import com.payment.paynetics.core.PayneticsGateway;
import com.payment.payon.core.PayOnGateway;
import com.payment.payonOppwa.PayonOppwaPaymentGateway;
import com.payment.paysec.NPaySecPaymentGateway;
import com.payment.paysec.PaySecPaymentGateway;
import com.payment.paysend.PaySendPaymentGateway;
import com.payment.paysend.PaySendWalletPaymentGateway;
import com.payment.payspace.PaySpacePaymentGateway;
import com.payment.paytm.PayTMPaymentGateway;
import com.payment.payu.PayUPaymentGateway;
import com.payment.payvision.core.PayVisionPaymentGateway;
import com.payment.payvisionV2.PayvisionV2PaymentGateway;
import com.payment.pbs.core.PbsPaymentGateway;
import com.payment.perfectmoney.PerfectMoneyPaymentGateway;
import com.payment.phoneix.PhoneixPaymentGateway;
import com.payment.powercash21.PowerCash21PaymentGateway;
import com.payment.procesosmc.ProcesosMCPaymentGateway;
import com.payment.processing.core.ProcessingPaymentGateway;
import com.payment.qikpay.QikpayPaymentGateway;
import com.payment.qikpayv2.QikPayV2PaymentGateway;
import com.payment.quickcard.QuickCardPaymentGateway;
import com.payment.quickpayments.QuickPaymentsGateway;
import com.payment.rave.RavePaymentGateway;
import com.payment.response.PZShortResponseStatus;
import com.payment.romcard.RomCardPaymentGateway;
import com.payment.rsp.RSPPaymentGateway;
import com.payment.sabadell.SabadellPaymentGateway;
import com.payment.safechargeV2.SafeChargeV2PaymentGateway;
import com.payment.safexpay.SafexPayPaymentGateway;
import com.payment.sbm.core.SBMPaymentGateway;
import com.payment.secureTrading.SecureTradingGateway;
import com.payment.skrill.SkrillPaymentGateway;
import com.payment.sofort.SofortPaymentGateway;
import com.payment.tigergateway.TigerGateWayPaymentGateway;
import com.payment.tigerpay.TigerPayPaymentGateway;
import com.payment.tojika.TojikaPaymentGateway;
import com.payment.totalPay.TotalPayPaymentGateway;
import com.payment.transactium.psp.ps.v1003.TransactiumPaymentGateway;
import com.payment.traxx.TRAXXPaymentGateway;
import com.payment.trueVo.TrueVoPaymentGateway;
import com.payment.trustPay.TrustPayOppwaPaymentGateway;
import com.payment.trustPay.TrustPayPaymentGateway;
import com.payment.trustly.TrustlyPaymentGateway;
import com.payment.twoGatePay.TwoGatePayPaymentGateway;
import com.payment.uPayGate.UPayGatePaymentGateway;
import com.payment.uba_mc.UBAMCPaymentGateway;
import com.payment.unicredit.UnicreditPaymentGateway;
import com.payment.validators.vo.CommonValidatorVO;
import com.payment.visaNet.VisaNetPaymentGateway;
import com.payment.voguePay.VoguePayPaymentGateway;
import com.payment.vouchermoney.VoucherMoneyPaymentGateway;
import com.payment.wealthpay.WealthPayGateway;
import com.payment.websecpay.core.WSecPaymentGateway;
import com.payment.whitelabel.WLPaymentGateway;
import com.payment.wonderland.core.WonderlandPayPaymentGateway;
import com.payment.xcepts.XceptsPaymentGateway;
import com.payment.xpate.XPatePaymentGateway;
import com.payment.zhixinfu.ZhiXinfuPaymentGateway;
import com.payment.zotapay.ZotapayPaymentGateway;
import com.payment.zotapaygateway.ZotaPayGateway;
import com.payment.smartcode.SmartCodePayPaymentGateway;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.MDC;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.crypto.CipherText;
import org.owasp.esapi.crypto.PlainText;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.UriBuilder;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.text.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Adler32;
import org.apache.commons.codec.binary.Base64;
import sun.misc.BASE64Decoder;

//import com.payment.APS.APSUtils;
//import com.payment.APS.ApsPaymentGateway;
//import com.payment.tapmio.TapMioPaymentGateway;

//import com.payment.wirecard.core.WireCardPaymentGateway;
//import com.payment.atlas.core.AtlasPaymentPaymentGateway;


public class Functions
{
    //checks the strting and returns null if blank
    final static String PASSWORD_ENCRYPTION_KEY = "4b99ab201b6c866a7a2sfggreggrbryukjkkjl8iiu2401f68d8a8ac2a89de31fdd0cd";
    static final private String ALPHABET = "98765432100123456789";
    final private Random rng = new SecureRandom();
    /**
     * It is used as the start tag in the replaceData function.
     */

    static String startTag = "<#";
    /**
     * It is the length of the start tag.
     */

    static int startTagLength = 2;
    /**
     * It is used as the end tag in the replaceData function.
     */

    static String endTag = "#>";
    /**
     * It is the length of the end tag.
     */

    static int endTagLength = 2;
    /**
     * Sole constructor.
     */

    static String startTag1 = "&lt;#";
    static int startTagLength1 = 5;
    static String endTag1 = "#&gt;";
    static int endTagLength1 = 5;
    private static Logger log = new Logger(Functions.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(Functions.class.getName());
    //  private static SystemAccessLogger accessLogger = new SystemAccessLogger(Functions.class.getName());
    private static final String HTML_PATTERN = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";
    private Pattern pattern = Pattern.compile(HTML_PATTERN);

    public boolean hasHTMLTags(String text)
    {
        boolean hasHTMLTags = false;
        if (isValueNull(text))
        {
            Matcher matcher = pattern.matcher(text);
            hasHTMLTags = matcher.find();
        }
        return hasHTMLTags;
    }

    public static boolean isSaleMerchant(String memberid)
    {
        Connection conn = null;

        try
        {
            conn = Database.getConnection();
            String query = "select isservice from members where memberid=" + memberid;
            ResultSet rs = Database.executeQuery(query, conn);
            if (rs.next())
            {
                if (rs.getString("isservice").equals("Y"))
                    return true;
                else return false;
            }
            else return false;

        }
        catch (SystemError se)
        {
            log.error("System Error Occured", se);
        }
        catch (SQLException se)
        {
            log.error("System Error Occured", se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return false;
    }

    public static String generateSignature(String memberid)
    {

        ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.template");
        String invoicetemplate = "Y";
        Connection conn = null;
        String query2 = "select invoicetemplate from members where memberid= ?";
        try
        {
            conn = Database.getConnection();

            PreparedStatement pstmnt = conn.prepareStatement(query2);
            pstmnt.setString(1, memberid);
            ResultSet x = pstmnt.executeQuery();
            x.next();
            invoicetemplate = x.getString("invoicetemplate");

        }
        catch (SQLException e)
        {
            log.error("Exception Occured", e);
            return RB.getString("DEFAULTSIGN");

        }
        catch (SystemError se)
        {
            log.error("Exception Occured", se);
            return RB.getString("DEFAULTSIGN");

        }
        finally
        {
            Database.closeConnection(conn);
        }
        if (invoicetemplate.equalsIgnoreCase("Y"))
        {
            return RB.getString("DEFAULTSIGN");
        }
        else
        {

            String signature = "";
            try
            {

                Hashtable details = Template.getMemberTemplateDetails(memberid);
                signature += "Customer Care <br>";
                if (details.get("PHONE1") != null)
                    signature += "<table border=0 ><tr><td><p><font size=\"2\" face=\"Verdana\">Phone No : </p></td><td><p><font size=\"2\" face=\"Verdana\">" + details.get("PHONE1") + "</p></td></tr>";
                if (details.get("PHONE2") != null)
                    signature += "<tr><td>&nbsp;</td><td><p><font size=\"2\" face=\"Verdana\">" + details.get("PHONE2") + "</p></td></tr>";

                String em = "";
                em = (String) details.get("EMAILS");
                if (em != null)
                {
                    String emails[] = em.split(",");
                    for (int count = 0; count < emails.length; count++)
                    {
                        if (count == 0)
                            signature += "<tr><td><p><font size=\"2\" face=\"Verdana\">Email Address : </p></td><td><p><font size=\"2\" face=\"Verdana\">" + emails[count] + "</p></td></tr>";
                        else
                            signature += "<tr><td>&nbsp;</td><td><p><font size=\"2\" face=\"Verdana\">" + emails[count] + "</p></td></tr>";
                    }

                }


            }
            catch (SystemError e)
            {
                log.error("Exception Occured", e);
            }
            return signature;
        }
    }

    public static String checkStringNull(String checkstr)
    {
        if (checkstr != null)
        {
            checkstr = checkstr.trim();

            if (checkstr.equals("null"))
            {
                checkstr = "";
            }

            if (checkstr.equals(""))
            {
                checkstr = null;
            }

        }
        return checkstr;
    }

    public static String checkValue(String checkstr)
    {
        if (checkstr != null)
        {
            checkstr = checkstr.trim();
            if (checkstr.equals("null") || checkstr.equals(""))
            {
                checkstr = "-";
            }
        }
        else
        {
            checkstr = "-";
        }
        return checkstr;
    }

    public static String checkTelFax(String cc, String no)
    {
        String str = "";
        if (cc != null && no != null)
        {
            str = cc + "-" + no;
        }
        else
        {
            str = "-";
        }
        return str;
    }


    //checks the strting and returns null if blank

    public static String[] checkArrayNull(String[] checkarr)
    {

        if (checkarr != null)
        {
            if (checkarr.length == 0) //if the user deselect the any option
            {
                checkarr = null;
            }
            else if (checkarr[0].equals(""))
            {
                checkarr = null;
            }
        }
        return checkarr;
    }

    //converts strtig to int if string is "" then returns default value passed to it

    public static int convertStringtoInt(String convertstr, int defaultval)
    {
        int val = defaultval;

        if (convertstr != null)
        {
            convertstr = convertstr.trim();

            if (!convertstr.equals("") && !convertstr.equals("null"))
            {
                try
                {
                    val = Integer.parseInt(convertstr);
                }
                catch (NumberFormatException nfe)
                {
                    val = defaultval;
                }
            }

        }
        return val;
    }

    //converts string array to commaseperated string

    public static String commaseperatedString(String[] arr)
    {
        String commastr = "";

        if (arr != null)
        {
            if (arr.length != 0) //if the user deselect the any option
            {
                for (int z = 0; z < arr.length; z++)
                {
                    if (z != arr.length - 1)
                    {
                        if (!arr[z].equals(""))
                        {
                            commastr = commastr + arr[z] + ",";
                        }
                    }
                    else
                    {

                        commastr = commastr + arr[z];

                    }
                }
            }
        }
        return commastr;
    }

    //converts mm/dd/yy to millisec

    public static String converttomillisec(String tempmm, String tempdd, String tempyy)
    {
        return converttomillisec(tempmm, tempdd, tempyy, "0", "0", "0");
    }

    public static String convertmd5Test(String value) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        String md5 = null;

        if (null == value) return null;

        try
        {

            //Create MessageDigest object for MD5
            MessageDigest digest = MessageDigest.getInstance("MD5");

            //Update input string in message digest
            digest.update(value.getBytes(), 0, value.length());

            //Converts message digest value in base 16 (hex)
            md5 = new BigInteger(1, digest.digest()).toString(16);

        }
        catch (NoSuchAlgorithmException e)
        {

            log.error("NoSuchAlgorithmException---->", e);
        }

        return md5;
    }

    public static String convertmd5(String value) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        String md5 = null;

        if (null == value) return null;


        try
        {

            log.debug("MD5 combination for qwipi---" + value);
            //Create MessageDigest object for MD5
            MessageDigest digest = MessageDigest.getInstance("MD5");

            md5 = Functions.getString(digest.digest(value.getBytes()));

        }
        catch (NoSuchAlgorithmException e)
        {
            log.error("NoSuchAlgorithmException while calculating MD5 for qwipi---", e);
        }


        return md5;
    }

    public static String converttomillisec(String tempmm, String tempdd, String tempyy, String temph, String tempm, String temps)
    {
        long tempdt = 0;
        String strdt = null;

        if (tempmm != null && tempdd != null && tempyy != null)
        {
            tempmm = tempmm.trim();
            tempdd = tempdd.trim();
            tempyy = tempyy.trim();

            if (tempmm.equals("") || tempdd.equals("") || tempyy.equals(""))
            {
                tempdt = 0;
                //		out.println("mm "+tempmm +" : " +"dd "+dd +" : " +"yy "+yy +"<br>" );
            }
            else
            {
                try
                {
                    //Date dt =new Date(Integer.parseInt(tempyy),Integer.parseInt(tempmm),Integer.parseInt(tempdd));
                    //tempdt=dt.getTime();

                    Calendar cal = Calendar.getInstance();
                    cal.set(Integer.parseInt(tempyy), Integer.parseInt(tempmm), Integer.parseInt(tempdd), Integer.parseInt(temph), Integer.parseInt(tempm), Integer.parseInt(temps));
                    tempdt = cal.getTime().getTime();

                    tempdt = tempdt / 1000;

                    strdt = "" + tempdt;
                }
                catch (NumberFormatException nfe)
                {
                    strdt = null;
                }
            }

        }

        return strdt;

    }

    //converts strtig array to int arr

    public static int[] convertStringarrtoIntarr(String convertstrarr[])
    {
        int val[] = null;

        if (convertstrarr != null)
        {
            if (convertstrarr.length != 0)
            {
                val = new int[convertstrarr.length];

                for (int i = 0; i < convertstrarr.length; i++)
                {
                    try
                    {
                        val[i] = Integer.parseInt(convertstrarr[i]);
                    }
                    catch (NumberFormatException nfe)
                    {
                        val = null;
                    }
                }
            }
        }
        return val;
    }

    /*
    public static  int[] convertStringarrtoIntarr(String convertstrarr[])
    {
        int val[]=null;

        if(convertstrarr!=null)
        {
            if(convertstrarr.length!=0)
            {
                val=new int[convertstrarr.length];

                for(int i=0;i<convertstrarr.length;i++)
                {
                    val[i]=Integer.parseInt(convertstrarr[i]);
                }
            }
        }
        return val;
    }
    */
    //converts commaseperated string to int array

    public static int[] convertCommaseperatedStringtoIntarr(String commastr)
    {
        int val[] = null;

        if (commastr != null)
        {
            commastr = commastr.trim();

            if (!commastr.equals(""))
            {
                //if(commastr.indexOf(",")!=-1)
                //{
                StringTokenizer stz = new StringTokenizer(commastr, ",");
                val = new int[stz.countTokens()];

                int pos = 0;
                while (stz.hasMoreTokens())
                {
                    String tempstr = stz.nextToken();

                    //	if(tempstr!=null && !tempstr.equals("null"))
                    //		{
                    try
                    {
                        val[pos] = Integer.parseInt(tempstr);
                        pos++;
                    }
                    catch (NumberFormatException nfe)
                    {
                        val[pos] = 0;
                        pos++;
                    }
                    //	}
                    //}
                }
            }
        }
        else
        {
            val = null;
        }

        return val;
    }

    public static String[] convertCommaseperatedStringtoStringarr(String commastr)
    {
        String val[] = null;

        if (commastr != null)
        {
            commastr = commastr.trim();

            if (!commastr.equals(""))
            {

                StringTokenizer stz = new StringTokenizer(commastr, ",");
                val = new String[stz.countTokens()];

                int pos = 0;
                while (stz.hasMoreTokens())
                {
                    String tempstr = stz.nextToken();

                    val[pos] = tempstr;
                    pos++;
                }

            }
        }
        else
        {
            val = null;
        }

        return val;
    }

    //returns a string of concated option tag

    public static String printoptions(int start, int end)
    {
        String str = "";

        for (int i = start; i <= end; i++)
        {
            str = str + "<option value=" + i + ">" + i + "</option>";
        }
        return str;
    }

    public static String monthoptions(int start, int end)
    {
        String str = "";
        String month[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        for (int i = start; i <= end; i++)
        {
            str = str + "<option value=" + i + ">" + month[i] + "</option>";
        }
        return str;
    }

    /*
    public static Calender getCalender(String timestamp)
    {
        java.util.GregorianCalendar cal=new java.util.GregorianCalendar();
        cal.setTime(new java.util.Date(Long.parseLong(timestamp+"000")));
        return cal;
    }
    */

    public static String NoRecordsFound()
    {
        String str = "";
        str = str + "<br><br><br>";
        str = str + "<table border=1  cellpadding=2 cellspacing=0 height=30% width=50% bordercolorlight=#000000 bordercolordark=#FFFFFF  align=center valign=center>";
        str = str + "<tr height=15>";
        str = str + "<td  bgcolor=#2379A5><b><font color=#FFFFFF size=+1 face=Verdana,Arial>Information</font></b></td>";
        str = str + "</tr>";
        str = str + "<tr><td align=center><font color=red size=2 face=Verdana,Arial>No Records Found</font></td></tr>";
        str = str + "</table>";
        str = str + "<br><br><br>";
        return str;
    }

    public static String[] convertStringToArray(String str)
    {
        String[] strarr = null;

        if (str != null)
        {
            str = str.trim();

            if (!str.equals(""))
            {
                strarr = new String[]{str};

            }
        }
        return strarr;
    }

    public static String convertStringtoDate(String str)
    {
        String tempdt = "";

        GregorianCalendar cal = new GregorianCalendar();
        try
        {
            cal.setTime(new Date(Long.parseLong(str + "000")));
            tempdt = "" + Integer.toString(cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DATE) + "/" + cal.get(Calendar.YEAR);
        }
        catch (NumberFormatException nfe)
        {
            tempdt = "-";
        }
        return tempdt;
    }


    public static String convertDtstamptoDate(String str)
    {
        String dt = null;

        try
        {
            long longdt = Long.parseLong(str);
            dt = DateFormat.getDateInstance().format(new Date(longdt * 1000));

        }
        catch (NumberFormatException ne)
        {
        }
        if (dt == null) dt = "";
        return dt;
    }

    public static String convertDtstampToDateTime(String str)
    {
        String dt = null;
        SimpleDateFormat dbFormatter = new SimpleDateFormat("d MMM yyyy HH:mm:ss");
        try
        {
            long longdt = Long.parseLong(str);
            Date date = new Date(longdt * 1000);
            dt = dbFormatter.format(date);

        }
        catch (NumberFormatException ne)
        {
            log.error("NumberFormatException====" + ne);
        }
        if (dt == null) dt = "";
        return dt;
    }

    public static String convertDtstampToDateTimeforTimezone(String str)
    {
        String dt = null;
        SimpleDateFormat dbFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            long longdt = Long.parseLong(str);
            Date date = new Date(longdt * 1000);
            dt = dbFormatter.format(date);

        }
        catch (NumberFormatException ne)
        {
            log.error("NumberFormatException====" + ne);
        }
        if (dt == null) dt = "";
        return dt;
    }

    public static String convertDtstampToDBFormat(String str)
    {
        String dt = null;
        SimpleDateFormat dbFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            log.debug("inside try::::");
            long longdt = Long.parseLong(str);
            Date date = new Date(longdt * 1000);
            dt = dbFormatter.format(date);
            log.debug("dt:::::" + dt);

        }
        catch (NumberFormatException ne)
        {
            log.error("NumberFormatException====" + ne);
        }
        if (dt == null) dt = "";
        log.debug("dt if:::::" + dt);
        return dt;
    }

    public static String getFormattedDate(String format)
    {
        TimeZone mauritiusTZ = TimeZone.getTimeZone("Indian/Mauritius");
        Calendar cal = Calendar.getInstance(mauritiusTZ);

        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date currentDate = cal.getTime();
        return dateFormat.format(currentDate);
    }

    public static double convertCentsToDollars(String str)
    {
        double amount = 0;
        double intamount = 0;


        try
        {
            amount = Integer.parseInt(str);
            amount = amount / 100;
        }
        catch (NumberFormatException nfe)
        {

        }


        return amount;
    }

    public static String ShowMessage(String msg, String error)
    {
        String customerror = error;
        String soapex = "SOAP-ENV";
        int pos = 0;

        if (error.indexOf("Connection refused") != -1 || error.indexOf("Error opening socket") != -1)
        {
            msg = "Server Down";
            customerror = "Server is Down!";
        }
        else if (error.indexOf(soapex) != -1)
        {
            //	pos=error.indexOf(soapex);
            //	if(pos!=-1)
            //	customerror=error.substring(pos+soapex.length()+1);
        }


        String str = "";
        str = str + "<HTML><BODY>";
        str = str + "<br><br><br>";
        str = str + "<table align=\"center\" width=\"50%\" cellpadding=\"2\" cellspacing=\"2\">	<tr><td>";

        str = str + "<table border=\"0\"  bgcolor=\"#CCE0FF\" width=\"100%\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\">";

        str = str + "<tr height=15>";
        str = str + "<td  bgcolor=\"#007ACC\" colspan=\"3\" class=\"label\" >&nbsp;&nbsp;" + msg + "</td>";
        str = str + "</tr>";

        str = str + "<tr><td>&nbsp;</td></tr>";

        str = str + "<tr><td align=\"center\" class=\"textb\">" + customerror + "</td></tr>";
        str = str + "<tr><td>&nbsp;</td></tr>";
        str = str + "</table>";
        str = str + "</BODY></html>";

        //str=str+"</body></html>";
        return str;
    }

    public static String ShowMessageForAdmin(String msg, String error)
    {
        String customerror = error;
        String soapex = "SOAP-ENV";
        int pos = 0;

        if (error.indexOf("Connection refused") != -1 || error.indexOf("Error opening socket") != -1)
        {
            msg = "Server Down";
            customerror = "Server is Down!";
        }
        else if (error.indexOf(soapex) != -1)
        {
            //	pos=error.indexOf(soapex);
            //	if(pos!=-1)
            //	customerror=error.substring(pos+soapex.length()+1);
        }


        String page = "index.jsp";

        String str = "";


        /*str = str + "<jsp:include page="+page+" flush=\"true\">" ;
    str = str + "</jsp:include>";*/
        str = str + "<%@ page language=\"java\" contentType=\"text/html\" %>";
        str = str + "<%@ include file=" + page + " %>\n";
        str = str + "<HTML>\n<BODY>\n";

        str = str + "<br><br><br>\n";
        str = str + "<table align=\"center\" width=\"50%\" cellpadding=\"2\" cellspacing=\"2\">\n	<tr><td>";

        str = str + "<table border=\"0\"  bgcolor=\"#CCE0FF\" width=\"100%\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\">\n";

        str = str + "<tr height=15>\n";

        str = str + "<td  bgcolor=\"#007ACC\" colspan=\"3\" class=\"label\" >&nbsp;&nbsp;" + msg + "</td>\n";
        str = str + "</tr>\n";

        str = str + "<tr><td>&nbsp;</td></tr>\n";

        str = str + "<tr><td align=\"center\" class=\"textb\">" + customerror + "</td></tr>\n";
        str = str + "<tr><td>&nbsp;</td></tr>\n";
        str = str + "</table>\n";
        str = str + "</BODY>\n</html>\n";

        //str=str+"</body></html>";
        return str;
    }

    public static String ShowConfirmation(String msg, String stat)
    {
        String str = "";
        str = str + "<HTML><BODY>";
        str = str + "<br><br><br>";
        str = str + "<table align=\"center\" width=\"50%\" cellpadding=\"2\" cellspacing=\"2\">	<tr><td>";

        str = str + "<table border=\"0\"  bgcolor=\"#CCE0FF\" width=\"100%\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\">";

        str = str + "<tr height=15>";
        str = str + "<td  bgcolor=\"#007ACC\" colspan=\"3\" class=\"label\" >&nbsp;&nbsp;" + msg + "</td>";
        str = str + "</tr>";

        str = str + "<tr><td>&nbsp;</td></tr>";

        str = str + "<tr><td align=\"center\" class=\"textb\">" + stat + "</td></tr>";
        str = str + "<tr><td>&nbsp;</td></tr>";
        str = str + "</table>";
        str = str + "</BODY></html>";

        return str;
    }

    /*public static String NewShowConfirmation1(String msg, String stat)
    {
        String str = "";

        *//*str = str + "<div class=\"alert alert-danger nomargin\">" + stat + "</div>";*//*
        str = str + "<div class=\"alert alert-danger\" style=\"text-align: center;width:60%;margin-right:auto;margin-left:auto\" >" + stat + "</div>";
        return str;
    }*/
    public static String NewShowConfirmation1(String msg, String stat)
    {
        String str = "";

        str = str + "<div class=\"bg-info\" style=\"text-align:center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;" + stat + "</div>";
        return str;
    }

    public static String NewShowConfirmationAlert(String msg, String stat)
    {
        String str = "";

        str = str + "<div class=\"bg-alert\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;" + stat + "</div>";
        return str;
    }

    public static String NewShowConfirmation(String msg, String stat)
    {
        String str = "";
        str = str + "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">";
        str = str + "<HTML><BODY>";
        str = str + "<br><br><br>";
        str = str + "<table align=\"center\" width=\"80%\" cellpadding=\"2\" cellspacing=\"2\" ><tr><td>";

        str = str + "<table bgcolor=\"#ecf0f1\" width=\"100%\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\">";

        str = str + "<tr height=30>";
        str = str + "<td colspan=\"3\" bgcolor=\"#34495e\"  class=\"texthead\" align=\"center\"><font color=\"#FFFFFF\" size=\"2\" face=\"Open Sans,Helvetica Neue,Helvetica,Arial,Palatino Linotype', 'Book Antiqua, Palatino, serif\"> " + msg + "</font></td>";
        str = str + "</tr>";

        str = str + "<tr><td>&nbsp;</td></tr>";

        str = str + "<tr><td align=\"center\" class=\"textb\">" + stat + "</td></tr>";
        str = str + "<tr><td>&nbsp;</td></tr>";
        str = str + "</table> </tr></td> </table>";
        str = str + "</BODY></HTML>";

        return str;
    }

    public static String NewShowMessage(String msg, String stat)
    {
        String str = "";
        str = str + "<HTML><BODY>";
        str = str + "<br><br><br>";
        str = str + "<table align=\"center\" width=\"50%\" cellpadding=\"2\" cellspacing=\"2\" ><tr><td>";

        str = str + "<table bgcolor=\"#ecf0f1\" width=\"100%\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\">";

        str = str + "<tr height=30>";
        str = str + "<td colspan=\"3\" bgcolor=\"#34495e\"  class=\"texthead\" align=\"center\"><font color=\"#FFFFFF\" size=\"2\" face=\"Open Sans,Helvetica Neue,Helvetica,Arial,Palatino Linotype', 'Book Antiqua, Palatino, serif\"> " + msg + "</font></td>";
        str = str + "</tr>";

        str = str + "<tr><td>&nbsp;</td></tr>";

        str = str + "<tr><td align=\"center\" class=\"textb\">" + stat + "</td></tr>";
        str = str + "<tr><td>&nbsp;</td></tr>";
        str = str + "</table> </tr></td> </table>";
        str = str + "</BODY></html>";

        return str;
    }


    public static String dayoptions(int start, int end, String value)
    {
        String str = "";
        value = value.trim();

        int j = Integer.parseInt(value);

        for (int i = start; i <= end; i++)
        {
            if (i == j)
            {
                str = str + "<option value=" + i + " selected >" + i + "</option>";
            }
            else
            {
                str = str + "<option value=" + i + " >" + i + "</option>";
            }
        }
        return str;
    }

    public static String yearoptions(int start, int end, String value)
    {
        String str = "";
        value = value.trim();

        int j = Integer.parseInt(value);

        for (int i = start; i <= end; i++)
        {
            if (i == j)
            {
                str = str + "<option value=" + i + " selected >" + i + "</option>";
            }
            else
            {
                str = str + "<option value=" + i + " >" + i + "</option>";
            }
        }
        return str;
    }

    public static String monthoptions(int start, int end, String value)
    {
        String str = "";
        value = value.trim();

        int j = Integer.parseInt(value);

        for (int i = start; i <= end; i++)
        {
            if (i == j)
            {
                str = str + "<option value=" + i + " selected >" + i + "</option>";
            }
            else
            {
                str = str + "<option value=" + i + " >" + i + "</option>";
            }
        }
        return str;
    }


    public static String newmonthoptions(int start, int end, String value)
    {
        String str = "";
        value = value.trim();

        //String month[]={"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        String month[] = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

        String monthselected[] = new String[12];
        int j = Integer.parseInt(value);

        for (int k = 0; k < 12; k++)
        {
            if (k == j)
                monthselected[k] = "selected";
            else
                monthselected[k] = "";
        }

        for (int i = 0; i < 12; i++)
        {
            str = str + "<option value=" + i + " " + monthselected[i] + ">" + month[i] + "</option>";
        }
        return str;
    }

    public static String printDOB(String str)
    {
        String newstr = null;
        if (str != null && !str.equals("0"))
        {
            newstr = "";
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(new Date(Long.parseLong(str + "000")));
            newstr = newstr + "<select name=dd><option>dd</option>" + dayoptions(1, 31, Integer.toString(cal.get(Calendar.DATE))) + "</select>" + "<select  name=mm><option>mm</option>" + newmonthoptions(0, 11, Integer.toString(cal.get(Calendar.MONTH) + 1)) + "</select>" + "<select name=yy><option>yyyy</option>" + yearoptions(1920, 2001, Integer.toString(cal.get(Calendar.YEAR))) + "</select>";
        }
        else
        {
            newstr = "";
            newstr = newstr + "<select name=dd><option>dd</option>" + printoptions(1, 31) + "</select>" + "<select  name=mm><option>mm</option>" + monthoptions(0, 11) + "</select>" + "<select name=yy><option>yyyy</option>" + printoptions(1920, 2001) + "</select>";
        }
        return newstr;
    }

    public static String newPrintDOB(String str, String ddname, String mmname, String yyname)
    {
        String newstr = null;
        if (str != null && !str.equals("0"))
        {
            newstr = "";
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(new Date(Long.parseLong(str + "000")));
            newstr = newstr + "<select name=\"" + ddname + "\"><option>dd</option>" + dayoptions(1, 31, Integer.toString(cal.get(Calendar.DATE))) + "</select>" + "<select  name=\"" + mmname + "\"><option>mm</option>" + newmonthoptions(0, 11, Integer.toString(cal.get(Calendar.MONTH) + 1)) + "</select>" + "<select name=\"" + yyname + "\"><option>yyyy</option>" + yearoptions(1920, 2002, Integer.toString(cal.get(Calendar.YEAR))) + "</select>";
        }
        else
        {
            newstr = "";
            newstr = newstr + "<select name=\"" + ddname + "\"><option>dd</option>" + printoptions(1, 31) + "</select>" + "<select  name=\"" + mmname + "\"><option>mm</option>" + monthoptions(0, 11) + "</select>" + "<select name=\"" + yyname + "\"><option>yyyy</option>" + printoptions(1920, 2001) + "</select>";
        }
        return newstr;
    }

    public static String printcontactdetails(Hashtable orderdata, String contactid)
    {
        String str = "";

        int size = Integer.parseInt((String) orderdata.get("numrows"));
        if (size > 0)
        {
            Hashtable temphash = null;
            for (int pos = 1; pos <= size; pos++)
            {
                String id = Integer.toString(pos);
                temphash = (Hashtable) orderdata.get(id);
                str = str + "<option value=" + temphash.get("contactid") + ">" + temphash.get("name") + "</option>";
            }

        }
        return str;
    }

    public static String printInfocontactdetails(Hashtable orderdata)
    {
        String str = "";

        int size = Integer.parseInt((String) orderdata.get("numrows"));
        if (size > 0)
        {
            Hashtable temphash = null;

            for (int pos = 1; pos <= size; pos++)
            {
                String id = Integer.toString(pos);
                temphash = (Hashtable) orderdata.get(id);
                if (temphash.get("roid") != null)
                    str = str + "<option value=" + temphash.get("contactid") + ">" + temphash.get("name") + " - " + temphash.get("roid") + "</option>";
            }

        }
        return str;
    }


    public static String printAppServer(Hashtable orderdata, String value, String text)
    {
        String str = "";

        int size = Integer.parseInt((String) orderdata.get("numrows"));

        if (size > 0)
        {
            Hashtable temphash = null;
            for (int pos = 1; pos <= size; pos++)
            {
                String id = Integer.toString(pos);
                temphash = (Hashtable) orderdata.get(id);
                str = str + "<option value=" + temphash.get(value) + ">" + temphash.get(text) + "</option>";
            }

        }
        return str;
    }

    public static boolean isValidate(String detail)
    {

        if (parseData(detail) == null || !isValidSrt(detail))
            return false;
        else
            return true;
    }

    public static boolean isValidSrt(String detail)
    {

        if (detail.indexOf("'") != -1)
            return false;
        else
            return true;
    }

    //remove html tag function START

    public static String removeHTML(String htmlString)
    {

        // Remove HTML tag from java String
        String noHTMLString = htmlString;
        noHTMLString = noHTMLString.replaceAll("<html>", "");
        noHTMLString = noHTMLString.replaceAll("<head", "");
        noHTMLString = noHTMLString.replaceAll("<body", "");
        noHTMLString = noHTMLString.replaceAll("<form", "");
        noHTMLString = noHTMLString.replaceAll("<script", "");
        noHTMLString = noHTMLString.replaceAll("</html>", "");
        noHTMLString = noHTMLString.replaceAll("</head>", "");
        noHTMLString = noHTMLString.replaceAll("</body>", "");
        noHTMLString = noHTMLString.replaceAll("</script>", "");
        noHTMLString = noHTMLString.replaceAll("</form>", "");

        noHTMLString = noHTMLString.replaceAll("<HTML>", "");
        noHTMLString = noHTMLString.replaceAll("<HEAD", "");
        noHTMLString = noHTMLString.replaceAll("<BODY", "");
        noHTMLString = noHTMLString.replaceAll("<FORM", "");
        noHTMLString = noHTMLString.replaceAll("<SCRIPT", "");
        noHTMLString = noHTMLString.replaceAll("</HTML>", "");
        noHTMLString = noHTMLString.replaceAll("</HEAD>", "");
        noHTMLString = noHTMLString.replaceAll("</BODY>", "");
        noHTMLString = noHTMLString.replaceAll("</SCRIPT>", "");
        noHTMLString = noHTMLString.replaceAll("</FORM>", "");
        return noHTMLString;
    }

    //remove html tag function END

    public static boolean isValidHeader(String header)
    {
        String detail = header.toLowerCase();
        if (detail.indexOf("<script") != -1 && detail.indexOf("<form") != -1 && detail.indexOf("</form>") != -1 && detail.indexOf("</script>") != -1 && detail.indexOf("expression(") != -1 && detail.indexOf("javascript") != -1 && detail.indexOf("<style") != -1 && detail.indexOf("</style") != -1)
            return false;
        else
            return true;
    }

    public static boolean isValidSQL(String detail)
    {

        if (parseData(detail) == null)
            return true;

        if (detail.indexOf("'") != -1 || detail.indexOf(";") != -1 || detail.indexOf("\\") != -1)
            return false;
        else
            return true;
    }

    public static String ShowLandRush(String msg, String stat)
    {
        String str = "";

        str = str + "<table width=\"75%\" align=center border=1 cellpadding=2 cellspacing=0  bordercolorlight=#000000 bordercolordark=#FFFFFF>";
        str = str + "<tr>";
        str = str + "<td  bgcolor=\"#2379A5\" colspan=\"3\"><font color=\"#FFFFFF\" size=\"1\" face=\"Verdana, Arial\"><b>" + msg + "</b></font></td>";
        str = str + "</tr>";
        str = str + "<tr>";
        str = str + "<td>";

        str = str + "<br><br><font face=\"verdana,arial\" size=\"2\">" + stat + "</font><br><br><br>";
        str = str + "</td></tr>";
        str = str + "</table>";
        return str;
    }

    public static String countryoptions(String name, String defaultvalue, String defaulttext, String selectedcountry)
    {
        selectedcountry = checkStringNull(selectedcountry);

        StringBuffer str = new StringBuffer();
        str.append("<SELECT name=" + name + ">");

        if (defaultvalue != null && defaulttext != null)
            str.append("<OPTION value=\"" + defaultvalue + "\">" + defaulttext);

        String country[] = {"<OPTION value=AF>Afghanistan", "<OPTION value=AL>Albania", "<OPTION value=DZ>Algeria", "<OPTION value=AS>American Samoa", "<OPTION value=AD>Andorra", "<OPTION value=AO>Angola", "<OPTION value=AI>Anguilla", "<OPTION value=AQ>Antarctica", "<OPTION value=AG>Antigua And Barbuda", "<OPTION value=AR>Argentina<OPTION value=AM>Armenia", "", "<OPTION value=AW>Aruba", "<OPTION value=AU>Australia", "<OPTION value=AT>Austria", "<OPTION value=AZ>Azerbaijan", "<OPTION value=BS>Bahamas, The", "<OPTION value=BH>Bahrain", "<OPTION value=BD>Bangladesh", "<OPTION value=BB>Barbados", "<OPTION value=BY>Belarus", "<OPTION value=BE>Belgium", "<OPTION value=BZ>Belize", "<OPTION value=BJ>Benin", "<OPTION value=BM>Bermuda", "<OPTION value=BT>Bhutan", "<OPTION value=BO>Bolivia", "<OPTION value=BA>Bosnia and Herzegovina", "<OPTION value=BW>Botswana", "<OPTION value=BV>Bouvet Island", "<OPTION value=BR>Brazil", "<OPTION value=IO>British Indian Ocean Territory", "<OPTION value=BN>Brunei", "<OPTION value=BG>Bulgaria", "<OPTION value=BF>Burkina Faso", "<OPTION value=BI>Burundi", "<OPTION value=KH>Cambodia", "<OPTION value=CM>Cameroon", "<OPTION value=CA>Canada", "<OPTION value=CV>Cape Verde", "<OPTION value=KY>Cayman Islands", "<OPTION value=CF>Central African Republic", "<OPTION value=TD>Chad", "<OPTION value=CL>Chile", "<OPTION value=CN>China", "<OPTION value=CX>Christmas Island", "<OPTION value=CC>Cocos (Keeling) Islands", "<OPTION value=CO>Colombia", "<OPTION value=KM>Comoros", "<OPTION value=CG>Congo", "<OPTION value=CD>Congo, Democractic Republic static of the", "<OPTION value=CK>Cook Islands", "<OPTION value=CR>Costa Rica", "<OPTION value=CI>Cote D'Ivoire (Ivory Coast)", "<OPTION value=HR>Croatia (Hrvatska)", "<OPTION value=CU>Cuba", "<OPTION value=CY>Cyprus", "<OPTION value=CZ>Czech Republic", "<OPTION value=DK>Denmark", "<OPTION value=DJ>Djibouti", "<OPTION value=DM>Dominica", "<OPTION value=DO>Dominican Republic", "<OPTION value=TP>East Timor", "<OPTION value=EC>Ecuador", "<OPTION value=EG>Egypt", "<OPTION value=SV>El Salvador", "<OPTION value=GQ>Equatorial Guinea", "<OPTION value=ER>Eritrea", "<OPTION value=EE>Estonia", "<OPTION value=ET>Ethiopia", "<OPTION value=FK>Falkland Islands (Islas Malvinas)", "<OPTION value=FO>Faroe Islands", "<OPTION value=FJ>Fiji Islands", "<OPTION value=FI>Finland", "<OPTION value=FR>France", "<OPTION value=GF>French Guiana", "<OPTION value=PF>French Polynesia", "<OPTION value=TF>French Southern Territories", "<OPTION value=GA>Gabon", "<OPTION value=GM>Gambia, The", "<OPTION value=GE>Georgia", "<OPTION value=DE>Germany", "<OPTION value=GH>Ghana", "<OPTION value=GI>Gibraltar", "<OPTION value=GR>Greece", "<OPTION value=GL>Greenland", "<OPTION value=GD>Grenada", "<OPTION value=GP>Guadeloupe", "<OPTION value=GU>Guam", "<OPTION value=GT>Guatemala", "<OPTION value=GN>Guinea", "<OPTION value=GW>Guinea-Bissau", "<OPTION value=GY>Guyana", "<OPTION value=HT>Haiti", "<OPTION value=HM>Heard and McDonald Islands", "<OPTION value=HN>Honduras", "<OPTION value=HK>Hong Kong S.A.R.", "<OPTION value=HU>Hungary", "<OPTION value=IS>Iceland", "<OPTION value=IN>India", "<OPTION value=ID>Indonesia", "<OPTION value=IR>Iran", "<OPTION value=IQ>Iraq", "<OPTION value=IE>Ireland", "<OPTION value=IL>Israel", "<OPTION value=IT>Italy", "<OPTION value=JM>Jamaica", "<OPTION value=JP>Japan", "<OPTION value=JO>Jordan", "<OPTION value=KZ>Kazakhstan", "<OPTION value=KE>Kenya", "<OPTION value=KI>Kiribati", "<OPTION value=KR>Korea", "<OPTION value=KP>Korea, North", "<OPTION value=KW>Kuwait", "<OPTION value=KG>Kyrgyzstan", "<OPTION value=LA>Laos", "<OPTION value=LV>Latvia", "<OPTION value=LB>Lebanon", "<OPTION value=LS>Lesotho", "<OPTION value=LR>Liberia", "<OPTION value=LY>Libya", "<OPTION value=LI>Liechtenstein", "<OPTION value=LT>Lithuania", "<OPTION value=LU>Luxembourg", "<OPTION value=MO>Macau S.A.R.", "<OPTION value=MK>Macedonia, Former Yugoslav Republic static of", "<OPTION value=MG>Madagascar", "<OPTION value=MW>Malawi", "<OPTION value=MY>Malaysia", "<OPTION value=MV>Maldives", "<OPTION value=ML>Mali", "<OPTION value=MT>Malta", "<OPTION value=MH>Marshall Islands", "<OPTION value=MQ>Martinique", "<OPTION value=MR>Mauritania", "<OPTION value=MU>Mauritius", "<OPTION value=YT>Mayotte", "<OPTION value=MX>Mexico", "<OPTION value=FM>Micronesia", "<OPTION value=MD>Moldova", "<OPTION value=MC>Monaco", "<OPTION value=MN>Mongolia", "<OPTION value=MS>Montserrat", "<OPTION value=MA>Morocco", "<OPTION value=MZ>Mozambique", "<OPTION value=MM>Myanmar", "<OPTION value=NA>Namibia", "<OPTION value=NR>Nauru", "<OPTION value=NP>Nepal", "<OPTION value=AN>Netherlands Antilles", "<OPTION value=NL>Netherlands, The", "<OPTION value=NC>New Caledonia", "<OPTION value=NZ>New Zealand", "<OPTION value=NI>Nicaragua", "<OPTION value=NE>Niger", "<OPTION value=NG>Nigeria", "<OPTION value=NU>Niue", "<OPTION value=NF>Norfolk Island", "<OPTION value=MP>Northern Mariana Islands", "<OPTION value=NO>Norway", "<OPTION value=OM>Oman", "<OPTION value=PK>Pakistan", "<OPTION value=PW>Palau", "<OPTION value=PA>Panama", "<OPTION value=PG>Papua new Guinea", "<OPTION value=PY>Paraguay", "<OPTION value=PE>Peru", "<OPTION value=PH>Philippines", "<OPTION value=PN>Pitcairn Island", "<OPTION value=PL>Poland", "<OPTION value=PT>Portugal", "<OPTION value=PR>Puerto Rico", "<OPTION value=QA>Qatar", "<OPTION value=RE>Reunion", "<OPTION value=RO>Romania", "<OPTION value=RU>Russia", "<OPTION value=RW>Rwanda", "<OPTION value=SH>Saint Helena", "<OPTION value=KN>Saint Kitts And Nevis", "<OPTION value=LC>Saint Lucia", "<OPTION value=PM>Saint Pierre and Miquelon", "<OPTION value=VC>Saint Vincent And The Grenadines", "<OPTION value=WS>Samoa", "<OPTION value=SM>San Marino", "<OPTION value=ST>Sao Tome and Principe", "<OPTION value=SA>Saudi Arabia", "<OPTION value=SN>Senegal", "<OPTION value=SC>Seychelles", "<OPTION value=SL>Sierra Leone", "<OPTION value=SG>Singapore", "<OPTION value=SK>Slovakia", "<OPTION value=SI>Slovenia", "<OPTION value=SB>Solomon Islands", "<OPTION value=SO>Somalia", "<OPTION value=ZA>South Africa", "<OPTION value=GS>South Georgia And The South Sandwich Islands", "<OPTION value=ES>Spain", "<OPTION value=LK>Sri Lanka", "<OPTION value=SD>Sudan", "<OPTION value=SR>Suriname", "<OPTION value=SJ>Svalbard And Jan Mayen Islands", "<OPTION value=SZ>Swaziland", "<OPTION value=SE>Sweden", "<OPTION value=CH>Switzerland", "<OPTION value=SY>Syria", "<OPTION value=TW>Taiwan", "<OPTION value=TJ>Tajikistan", "<OPTION value=TZ>Tanzania", "<OPTION value=TH>Thailand", "<OPTION value=TG>Togo", "<OPTION value=TK>Tokelau", "<OPTION value=TO>Tonga", "<OPTION value=TT>Trinidad And Tobago", "<OPTION value=TN>Tunisia", "<OPTION value=TR>Turkey", "<OPTION value=TM>Turkmenistan", "<OPTION value=TC>Turks And Caicos Islands", "<OPTION value=TV>Tuvalu", "<OPTION value=UG>Uganda", "<OPTION value=UA>Ukraine", "<OPTION value=AE>United Arab Emirates", "<OPTION value=UK>United Kingdom", "<OPTION value=US>United States", "<OPTION value=UM>United States Minor Outlying Islands", "<OPTION value=UY>Uruguay", "<OPTION value=UZ>Uzbekistan", "<OPTION value=VU>Vanuatu", "<OPTION value=VA>Vatican City State (Holy See)", "<OPTION value=VE>Venezuela", "<OPTION value=VN>Vietnam", "<OPTION value=VG>Virgin Islands (British)", "<OPTION value=VI>Virgin Islands (US)", "<OPTION value=WF>Wallis And Futuna Islands", "<OPTION value=YE>Yemen", "<OPTION value=YU>Yugoslavia", "<OPTION value=ZM>Zambia", "<OPTION value=ZW>Zimbabwe</OPTION>"};
        for (int i = 0; i < country.length; i++)
        {
            //	System.out.print("..."+i+"..."+(country[i].indexOf("IN")>0));
            if (selectedcountry != null)
            {
                if (country[i].indexOf(selectedcountry + ">") > 0)
                {
                    StringBuffer sb = new StringBuffer(country[i]);
                    sb.insert(country[i].indexOf(">"), " selected");
                    //System.out.println(i+"    "+sb.toString());
                    str.append(sb.toString());
                }
                else
                    str.append(country[i]);
            }
            else
                str.append(country[i]);
        }
        str.append("</SELECT>");
        return str.toString();
    }

    public static String parseData(String str)
    {
// cat.info("In parseData");
        if (str == null || str.trim().length() == 0 || str.trim().equalsIgnoreCase("null"))
        {
// cat.info("Leaving parseData with null");
            return null;
        }
        else
        {
// cat.info("Leaving parseData with string");
            return str;
        }
    }

    public static String replaceTag(String data, Hashtable values)
    {
        // cat.info("In replaceData");
        StringBuffer sb = new StringBuffer();
        int startPos = 0;
        int startTagPos = 0;
        int endTagPos = 0;

        int length = 0;

        if (data != null)
            length = data.length();
        else
            return sb.toString();

        String value = null;
        // cat.info("Before While");
        while (startPos < length)
        {
            startTagPos = data.indexOf(startTag, startPos);
            if (startTagPos == -1)
            {
                sb.append(data.substring(startPos));
                startPos = length + 1;
            }
            else
            {
                sb.append(data.substring(startPos, startTagPos));
                endTagPos = data.indexOf(endTag, startTagPos);
                value = String.valueOf(values.get(data.substring(startTagPos + startTagLength, endTagPos)));

                if (value != null && !value.equalsIgnoreCase("null"))
                {
                    sb.append(value);
                }
                else
                {
                    sb.append("&nbsp;");
                }
                startPos = endTagPos + endTagLength;
            }
        }
        // cat.info("Leaving replaceData");
        return sb.toString();
    }

    public static String replaceTagForCustomTemplate(String data, HashMap<MailPlaceHolder, String> values)
    {
        StringBuffer sb = new StringBuffer();
        int startPos = 0;
        int startTagPos = 0;
        int endTagPos = 0;

        int length = 0;

        if (data != null)
            length = data.length();
        else
            return sb.toString();

        String value = null;
        while (startPos < length)
        {
            startTagPos = data.indexOf(startTag1, startPos);
            if (startTagPos == -1)
            {
                sb.append(data.substring(startPos));
                startPos = length + 1;
            }
            else
            {
                sb.append(data.substring(startPos, startTagPos));
                endTagPos = data.indexOf(endTag1, startTagPos);

                value = String.valueOf(values.get(MailPlaceHolder.valueOf(data.substring(startTagPos + startTagLength1, endTagPos).trim()
                )));
                if (value != null && !value.equalsIgnoreCase("null"))
                {
                    sb.append(value);
                }
                else
                {
                    sb.append("&nbsp;");
                }
                startPos = endTagPos + endTagLength1;
            }
        }
        return sb.toString();
    }

    public static Hashtable getAdditionalFields(String data)
    {
        StringBuffer sb = new StringBuffer();
        Hashtable edditionalFields = new Hashtable();
        int startPos = 0;
        int startTagPos = 0;
        int endTagPos = 0;

        String startJSTag = "<JS#";
        String endJSTag = "#JS>";

        String startFieldTag = "<FL#";
        String endFieldTag = "#FL>";

        int length = 0;

        if (data != null)
            length = data.length();
        else
            return edditionalFields;

        String value = null;

        while (startPos < length)
        {
            startTagPos = data.indexOf(startJSTag, startPos);
            if (startTagPos == -1)
            {
                sb.append(data.substring(startPos));
                startPos = length + 1;
            }
            else
            {
                sb.append(data.substring(startPos, startTagPos));
                endTagPos = data.indexOf(endJSTag, startTagPos);
                value = String.valueOf(data.substring(startTagPos + 4, endTagPos));

                if (value != null && !value.equalsIgnoreCase("null"))
                {
                    edditionalFields.put("ADDITIONALJS", value.toString());
                }

                startPos = endTagPos + 4;
            }
        }
        startPos = 0;
        endTagPos = 0;
        startTagPos = 0;
        value = null;
        while (startPos < length)
        {
            startTagPos = data.indexOf(startFieldTag, startPos);
            if (startTagPos == -1)
            {
                sb.append(data.substring(startPos));
                startPos = length + 1;
            }
            else
            {
                sb.append(data.substring(startPos, startTagPos));
                endTagPos = data.indexOf(endFieldTag, startTagPos);
                value = String.valueOf(data.substring(startTagPos + 4, endTagPos));

                if (value != null && !value.equalsIgnoreCase("null"))
                {
                    edditionalFields.put("ADDITIONALFIELDS", value.toString());
                }

                startPos = endTagPos + 4;
            }
        }

        return edditionalFields;
    }

    public static String replaceTag1(String data, HashMap<MailPlaceHolder, String> vPlaceHolder)
    {
        // cat.info("In replaceData");
        StringBuffer sb = new StringBuffer();
        int startPos = 0;
        int startTagPos = 0;
        int endTagPos = 0;

        int length = 0;

        if (data != null)
            length = data.length();
        else
            return sb.toString();


        String value = null;
        // cat.info("Before While");
        while (startPos < length)
        {
            startTagPos = data.indexOf(startTag, startPos);
            if (startTagPos == -1)
            {
                sb.append(data.substring(startPos));
                startPos = length + 1;
            }
            else
            {
                sb.append(data.substring(startPos, startTagPos));
                endTagPos = data.indexOf(endTag, startTagPos);

                value = String.valueOf(vPlaceHolder.get(MailPlaceHolder.valueOf(data.substring(startTagPos + startTagLength, endTagPos).trim())));
                //log.debug("replaceTag1---"+data.substring(startTagPos+startTagLength,endTagPos)+" : "+value);
                if (value != null && !value.equalsIgnoreCase("null"))
                {
                    sb.append(value);
                }
                else
                {
                    sb.append("&nbsp;");
                }
                startPos = endTagPos + endTagLength;
            }
        }
        // cat.info("Leaving replaceData");
        return sb.toString();
    }

    public static String replaceData(String data, String searchString, String replaceString)
    {
        StringBuffer sb = new StringBuffer();
        String firstPart = "";
        String secondPart = "";

        int startPos = 0;
        int startTagPos = 0;
        int length = data.length();
        int searchLength = searchString.length();
        int replaceLength = searchString.length();


        while (startPos < length)
        {

            startTagPos = data.indexOf(searchString, startPos);


            if (startTagPos != -1)
            {
                firstPart = data.substring(0, startTagPos);
                secondPart = data.substring(startTagPos + searchLength);

                data = firstPart + replaceString + secondPart;

                startPos = startTagPos + replaceLength;
                length = data.length();
            }
            else
            {
                break;
            }

        }//while ends
        return data;
    }

    public static String getDigitsOnly(String s)
    {
        StringBuffer digitsOnly = new StringBuffer();
        char c;
        for (int i = 0; i < s.length(); i++)
        {
            c = s.charAt(i);
            if (Character.isDigit(c))
            {
                digitsOnly.append(c);
            }
        }
        return digitsOnly.toString();
    }

    public static String getFormat(String request, String replace, String replaceto)
    {
        int startfrom = 0;
        int pos = 0;

        StringBuffer buf = new StringBuffer(request);

        while ((pos = request.indexOf(replace, startfrom)) != -1)
        {
            startfrom = pos + replace.length();
            buf.replace(pos, startfrom, replaceto);
            startfrom = pos + replaceto.length();
            request = buf.toString();
        }

        return buf.toString();
    }

    public static Hashtable getDetailedHashFromResultSet(ResultSet rs) throws SystemError
    {
        if (rs == null)
        {
            throw new SystemError("Result Set is empty");
        }
        Hashtable outerHash = null;
        try
        {
            outerHash = new Hashtable();

            while (rs.next())
            {
                String name = rs.getString(1);
                String value = rs.getString(2);
                if (name != null && value != null)
                {
                    outerHash.put(name, value);
                }

            }
        }
        catch (Exception e)
        {
            log.error(" Error -> ", e);
            throw new SystemError(e.toString());
        }
        return outerHash;
    }

    public static final String getStackTrace(Throwable t)
    {
        try
        {
            StringWriter str = new StringWriter();
            PrintWriter pw = new PrintWriter(str);
            t.printStackTrace(pw);
            return str.toString();
        }
        catch (Exception e)
        {
            return e.getMessage();
        }
    }

    private static boolean verifyCheckSum(String memberid, String description, String checksum, String key)
    {
        String str = memberid + "|" + description + "|" + key;

        Adler32 adl = new Adler32();
        adl.update(str.getBytes());
        long adler = adl.getValue();

        return ("" + adler).equals(checksum);
    }

    private static boolean verifyMD5ChecksumV1(String memberid, String description, String checksum, String key) throws NoSuchAlgorithmException
    {
        String str = memberid + "|" + description + "|" + key;
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        return generatedCheckSum.equals(checksum);
    }

    public static boolean verifyChecksumV1(String memberid, String description, String checksum, String key, String algorithm) throws NoSuchAlgorithmException
    {

        if ("Adler32".equals(algorithm))
        {
            return verifyCheckSum(memberid, description, checksum, key);
        }
        else
        {
            return verifyMD5ChecksumV1(memberid, description, checksum, key);
        }
    }


    private static String generateChecksum(String description, String status, String key)
    {
        String str = description + "|" + status + "|" + key;


        Adler32 adl = new Adler32();
        adl.update(str.getBytes());
        return String.valueOf(adl.getValue());
    }

    private static String generateMD5ChecksumV1(String description, String status, String key) throws NoSuchAlgorithmException
    {
        String str = description + "|" + status + "|" + key;
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        return generatedCheckSum;
    }

    public static String generateChecksumV1(String description, String status, String key, String algorithm) throws NoSuchAlgorithmException
    {
        if ("Adler32".equals(algorithm))
        {
            return generateChecksum(description, status, key);
        }
        else
        {
            return generateMD5ChecksumV1(description, status, key);
        }
    }


    private static boolean verifyCheckSum(String memberid, String description, String captureamount, String checksum, String key)
    {
        String str = memberid + "|" + description + "|" + captureamount + "|" + key;

        Adler32 adl = new Adler32();
        adl.update(str.getBytes());
        long adler = adl.getValue();

        return ("" + adler).equals(checksum);
    }

    private static boolean verifyMD5ChecksumV2(String memberid, String description, String captureamount, String checksum, String key) throws NoSuchAlgorithmException
    {
        String str = memberid + "|" + description + "|" + captureamount + "|" + key;
        log.debug("key :" + key);
        log.debug("checksum string :" + str);

        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        log.debug("Checksum verify    : " + checksum + " = " + generatedCheckSum);
        return generatedCheckSum.equals(checksum);
    }

    public static boolean verifyChecksumV2(String memberid, String description, String captureamount, String checksum, String key, String algorithm) throws NoSuchAlgorithmException
    {


        if ("Adler32".equals(algorithm))
        {
            return verifyCheckSum(memberid, description, captureamount, checksum, key);
        }
        else
        {
            return verifyMD5ChecksumV2(memberid, description, captureamount, checksum, key);
        }
    }

    private static String generateChecksum(String memberid, String description, String captureamount, String key)
    {
        String str = memberid + "|" + description + "|" + captureamount + "|" + key;


        Adler32 adl = new Adler32();
        adl.update(str.getBytes());
        return String.valueOf(adl.getValue());
    }

    private static String generateMD5ChecksumV2(String memberid, String description, String captureamount, String key) throws NoSuchAlgorithmException
    {
        String str = memberid + "|" + description + "|" + captureamount + "|" + key;
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        return generatedCheckSum;

    }

    public static String generateChecksumManualRebill(String toid, String trackingid, String amount, String description, String key) throws NoSuchAlgorithmException
    {
        return generateMD5ChecksumManualRebill(toid, trackingid, amount, description, key);
    }

    public static String generateMD5ChecksumManualRebill(String toid, String trackingid, String amount, String description, String key) throws NoSuchAlgorithmException
    {
        String str = toid + "|" + trackingid + "|" + amount + "|" + description + "|" + key;
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        return generatedCheckSum;
    }

    public static String generateChecksumV2(String memberid, String description, String captureamount, String key, String algorithm) throws NoSuchAlgorithmException
    {
        if ("Adler32".equals(algorithm))
        {
            return generateChecksum(memberid, description, captureamount, key);
        }
        else
        {
            return generateMD5ChecksumV2(memberid, description, captureamount, key);
        }

    }

    public static String generateChecksumV4(String toid, String totype, String amount, String description, String redirecturl, String key, String algorithm) throws NoSuchAlgorithmException
    {
        if ("Adler32".equals(algorithm))
        {
            return generateChecksumVV(toid, totype, amount, description, redirecturl, key, algorithm);
        }
        else
        {
            return generateMD5ChecksumVV(toid, totype, amount, description, redirecturl, key, algorithm);
        }

    }

    public static String generateChecksumV5(String toid, String amount, String description, String redirecturl, String key, String algorithm) throws NoSuchAlgorithmException
    {
        if ("Adler32".equals(algorithm))
        {
            return generateChecksumVV(toid, amount, description, redirecturl, key, algorithm);
        }
        else
        {
            return generateMD5ChecksumVV(toid, amount, description, redirecturl, key, algorithm);
        }

    }

    public static String generateChecksumV4InvoiceGen(String toid, String amount, String orderid, String orderdescription, String emailaddr, String redirecturl, String key, String algorithm) throws NoSuchAlgorithmException
    {
        if ("Adler32".equals(algorithm))
        {
            return generateChecksumInvoiceGen(toid, amount, orderid, orderdescription, emailaddr, redirecturl, key, algorithm);
        }
        else
        {
            return generateMD5ChecksumInvoiceGen(toid, amount, orderid, orderdescription, emailaddr, redirecturl, key, algorithm);
        }
    }

    public static String generateChecksumDirectKit(String toid, String totype, String amount, String description, String redirecturl, String cardNumber, String key, String algorithm) throws NoSuchAlgorithmException
    {
        return generateMD5ChecksumDirectKit(toid, totype, amount, description, redirecturl, cardNumber, key, algorithm);
    }

    public static String generateChecksumSTDFlow(String toid, String amount, String description, String redirecturl, String cardNumber, String key, String algorithm) throws NoSuchAlgorithmException
    {
        return generateMD5ChecksumSTDFlow(toid, amount, description, redirecturl, cardNumber, key, algorithm);
    }

    private static String generateChecksumVV(String toid, String totype, String amount, String description, String redirecturl, String key, String algorithm)
    {
        String str = toid + "|" + totype + "|" + amount + "|" + description + "|" + redirecturl + "|" + key;

        Adler32 adl = new Adler32();
        adl.update(str.getBytes());
        return String.valueOf(adl.getValue());
    }

    private static String generateChecksumVV(String toid, String amount, String description, String redirecturl, String key, String algorithm)
    {
        String str = toid + "|" + amount + "|" + description + "|" + redirecturl + "|" + key;

        Adler32 adl = new Adler32();
        adl.update(str.getBytes());
        return String.valueOf(adl.getValue());
    }

    private static String generateChecksumInvoiceGen(String toid, String amount, String orderid, String orderdescription, String emailaddr, String redirecturl, String key, String algorithm)
    {
        String str = toid + "|" + amount + "|" + orderid + "|" + orderdescription + "|" + emailaddr + "|" + redirecturl + "|" + key;

        Adler32 adl = new Adler32();
        adl.update(str.getBytes());
        return String.valueOf(adl.getValue());
    }

    public static String generateMD5ChecksumVV(String toid, String totype, String amount, String description, String redirecturl, String key, String algorithm) throws NoSuchAlgorithmException
    {
        String str = toid + "|" + totype + "|" + amount + "|" + description + "|" + redirecturl + "|" + key;
        log.debug("str----" + str);
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        log.debug("generatedCheckSum----" + generatedCheckSum);
        return generatedCheckSum;
    }

    private static String generateMD5ChecksumVV(String toid, String amount, String description, String redirecturl, String key, String algorithm) throws NoSuchAlgorithmException
    {
        String str = toid + "|" + amount + "|" + description + "|" + redirecturl + "|" + key;
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        return generatedCheckSum;
    }

    public static String generateMD5Checksum(String emailId) throws NoSuchAlgorithmException
    {
        //String str = toid + "|" + totype + "|" + amount + "|" + description + "|" + redirecturl + "|" + key;
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(emailId.getBytes()));
        return generatedCheckSum;
    }

    private static String generateMD5ChecksumInvoiceGen(String toid, String amount, String orderid, String orderdescription, String emailaddr, String redirecturl, String key, String algorithm) throws NoSuchAlgorithmException
    {
        String str = toid + "|" + amount + "|" + orderid + "|" + orderdescription + "|" + emailaddr + "|" + redirecturl + "|" + key;
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        return generatedCheckSum;
    }

    public static String generateMD5ChecksumDirectKit(String toid, String totype, String amount, String description, String redirecturl, String cardNumber, String key, String algorithm) throws NoSuchAlgorithmException
    {
        String str = toid + "|" + totype + "|" + amount + "|" + description + "|" + redirecturl + "|" + cardNumber + "|" + key;
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        return generatedCheckSum;
    }

    public static String generateMD5ChecksumSTDFlow(String toid, String amount, String description, String redirecturl, String cardNumber, String key, String algorithm) throws NoSuchAlgorithmException
    {
        String str = toid + "|" + amount + "|" + description + "|" + redirecturl + "|" + cardNumber + "|" + key;
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        return generatedCheckSum;
    }


    /**
     * This is to generate MD5 Checksum fpr API Based
     *
     * @param
     * @param key
     * @return
     * @throws java.security.NoSuchAlgorithmException
     */
    private static String generateChecksum(String description, String status, String chargeamount, String key, int random)
    {
        String str = description + "|" + status + "|" + chargeamount + "|" + key + "|" + random;

        Adler32 adl = new Adler32();
        adl.update(str.getBytes());
        return String.valueOf(adl.getValue());
    }

    //Used in TransactionStatusServlet to include random number and charge amount
    private static String generateMD5ChecksumV3(String description, String status, String chargeamount, String key, int random) throws NoSuchAlgorithmException
    {
        String str = description + "|" + status + "|" + chargeamount + "|" + key + "|" + random;
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        return generatedCheckSum;
    }

    public static String generateChecksumV3(String description, String status, String chargeamount, String key, int random, String algorithm) throws NoSuchAlgorithmException
    {
        if ("Adler32".equals(algorithm))
        {
            return generateChecksum(description, status, chargeamount, key, random);
        }
        else
        {
            return generateMD5ChecksumV3(description, status, chargeamount, key, random);
        }
    }

    public static boolean checkAccuracy(String conv, int decimalplace)
    {
        boolean isValidDecimalPart = false;
        if (conv != null && conv.indexOf(".") > -1)
        {
            String decimalPart = conv.substring(conv.indexOf(".") + 1);
            if (decimalPart.length() == decimalplace)
            {
                isValidDecimalPart = true;
            }
            else
            {
                isValidDecimalPart = false;
            }
        }
        else
        {
            isValidDecimalPart = false;
        }
        return isValidDecimalPart;
    }

    /* //This pair of varify checksum and generate checksum is being used in communication with SFNB

   public static boolean verifyCheckSum(String inputString ,String key,String checksum)
       {
           String str = inputString + "|" + key ;

           Adler32 adl = new Adler32();
           adl.update(str.getBytes());
           long adler = adl.getValue();

           return ("" + adler).equals(checksum);
       }

       public static String generateChecksum(String inputString, String key)
       {
           String str = inputString + "|" + key ;

           System.out.println("In generateChecksum with values "+str);

           Adler32 adl = new Adler32();
           adl.update(str.getBytes());
           return String.valueOf(adl.getValue());

       }
    */

    private static String getString(byte buf[])
    {
        StringBuffer sb = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++)
        {
            int h = (buf[i] & 0xf0) >> 4;
            int l = (buf[i] & 0x0f);
            sb.append(new Character((char) ((h > 9) ? 'a' + h - 10 : '0' + h)));
            sb.append(new Character((char) ((l > 9) ? 'a' + l - 10 : '0' + l)));
        }
        return sb.toString();
    }

    public static Vector getVectorFromArray(Object[] array)
    {
        Vector vector = new Vector();

        if (array == null)
        {
            return vector;
        }

        for (int i = 0; i < array.length; i++)
        {
            vector.add(array[i]);
        }
        return vector;
    }

    public boolean isMember(String memberid)
    {
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            String selquery = "select memberid from members Where memberid=? ";
            PreparedStatement pstmt = conn.prepareStatement(selquery);
            pstmt.setString(1, memberid);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                return true;
            }
        }
        catch (SystemError se)
        {
            log.error(" SystemError in isMember method: ", se);
        }
        catch (SQLException e)
        {
            log.error("Exception in isMember method: ", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return false;
    }

    public static String isValidCard(String ccNum, String cardType) throws NumberFormatException
    {
        //ccNum.trim();
        int length = ccNum.trim().length();
        String error = "";
        String type = "";
        if (cardType.equals("1") && !ccNum.startsWith("4") && (length != 13 || length != 16 || length != 19))
        {
            error = "Invalid Visa Card";
        }
        else if (cardType.equals("2") && !((ccNum.startsWith("51") || ccNum.startsWith("52") || ccNum.startsWith("53") || ccNum.startsWith("54") || ccNum.startsWith("55") || ccNum.startsWith("22") || ccNum.startsWith("23") || ccNum.startsWith("24") || ccNum.startsWith("25") || ccNum.startsWith("26") || ccNum.startsWith("27") && length != 16)
                || (ccNum.startsWith("50") || ccNum.startsWith("58") || ccNum.startsWith("57") || ccNum.startsWith("63") || ccNum.startsWith("67") || ccNum.startsWith("56") && (length != 16 || length != 19))))
        {
            error = "Invalid Master/Maestro Card";
        }
        else if (cardType.equals("3") && !(ccNum.startsWith("300") || ccNum.startsWith("301") || ccNum.startsWith("302") || ccNum.startsWith("303") || ccNum.startsWith("304")
                || ccNum.startsWith("305") || ccNum.startsWith("36") || ccNum.startsWith("54") || ccNum.startsWith("55")) && (length != 14 || length != 16))
        {
            error = "Invalid DINERS Card";
        }
        else if (cardType.equals("4") && !(ccNum.startsWith("34") || ccNum.startsWith("37")) && length != 15)
        {
            error = "Invalid AMEX Card";
        }
        else if (cardType.equals("5") && !(ccNum.startsWith("6011") || ccNum.startsWith("622") || ccNum.startsWith("644") || ccNum.startsWith("645") || ccNum.startsWith("646") || ccNum.startsWith("647") || ccNum.startsWith("648") || ccNum.startsWith("649") || ccNum.startsWith("65")) && (length != 16 || length != 19))
        {
            error = "Invalid DISC Card";
        }
        else if (cardType.equals("16") && !(ccNum.startsWith("35")) && (length != 16 || length != 19))
        {
            error = "Invalid JCB Card";
        }
        else if (cardType.equals("37") && !(ccNum.startsWith("5018") || ccNum.startsWith("5020") || ccNum.startsWith("5038") || ccNum.startsWith("5893") || ccNum.startsWith("6304") || ccNum.startsWith("6759") || ccNum.startsWith("6761") || ccNum.startsWith("6762") || ccNum.startsWith("6763") || ccNum.startsWith("6709") || ccNum.startsWith("6799") || ccNum.startsWith("6705") || ccNum.startsWith("5612")) && (length != 16 || length != 19))
        {
            error = "Invalid MAESTRO Card";
        }
        else if (cardType.equals("38") && !((ccNum.startsWith("637") || ccNum.startsWith("638") || ccNum.startsWith("639")) && length == 16))
        {
            error = "Invalid INSTAPAYMENT Card";
        }
        else if (cardType.equals("23") && !((ccNum.startsWith("6521") || ccNum.startsWith("6522") || ccNum.startsWith("60") || ccNum.startsWith("3536") || ccNum.startsWith("3538") || ccNum.startsWith("5028") || ccNum.startsWith("5085") || ccNum.startsWith("5086") || ccNum.startsWith("5087") || ccNum.startsWith("5088") || ccNum.startsWith("5089") || ccNum.startsWith("6061") || ccNum.startsWith("6062") || ccNum.startsWith("6063") || ccNum.startsWith("6064") || ccNum.startsWith("6065") || ccNum.startsWith("6066") || ccNum.startsWith("6067") || ccNum.startsWith("6068") || ccNum.startsWith("6069") || ccNum.startsWith("6070") || ccNum.startsWith("6071") || ccNum.startsWith("6072") || ccNum.startsWith("6073") || ccNum.startsWith("6074") || ccNum.startsWith("6075") || ccNum.startsWith("6076") || ccNum.startsWith("6077") || ccNum.startsWith("6078") || ccNum.startsWith("6079") || ccNum.startsWith("6080") || ccNum.startsWith("6081") || ccNum.startsWith("6082") || ccNum.startsWith("6083") || ccNum.startsWith("6084") || ccNum.startsWith("6085") || ccNum.startsWith("6086") || ccNum.startsWith("6087") || ccNum.startsWith("6088") || ccNum.startsWith("6089") || ccNum.startsWith("6273") || ccNum.startsWith("6521") || ccNum.startsWith("6522") || ccNum.startsWith("6523") || ccNum.startsWith("6524") || ccNum.startsWith("6525") || ccNum.startsWith("6528") || ccNum.startsWith("6950") || ccNum.startsWith("8172") || ccNum.startsWith("8201") || ccNum.startsWith("8888") || ccNum.startsWith("9999")) && length == 16))
        {
            error = "Invalid RUPAY Card";
        }
        return error;
    }

    public static String getCardTypeFromCardNumber(String ccNum) throws NumberFormatException
    {
        int length = ccNum.trim().length();
        String cardtype = "";
        String type = "";

        if (ccNum.startsWith("4") && (length == 13 || length == 16 || length == 19))
        {
            cardtype = "VISA";
            type = "1";
        }
        else if ((ccNum.startsWith("51") || ccNum.startsWith("52") || ccNum.startsWith("53") || ccNum.startsWith("54") || ccNum.startsWith("55") || ccNum.startsWith("22") || ccNum.startsWith("23") || ccNum.startsWith("24") || ccNum.startsWith("25") || ccNum.startsWith("26") || ccNum.startsWith("27") && length == 16))
        {
            cardtype = "MC";
            type = "2";
        }
        else if ((ccNum.startsWith("300") || ccNum.startsWith("301") || ccNum.startsWith("302") || ccNum.startsWith("303") || ccNum.startsWith("304")
                || ccNum.startsWith("305") || ccNum.startsWith("36") || ccNum.startsWith("54") || ccNum.startsWith("55")) && (length == 14 || length == 16))
        {
            cardtype = "DINERS";
            type = "3";
        }
        else if ((ccNum.startsWith("34") || ccNum.startsWith("37")) && length == 15)
        {
            cardtype = "AMEX";
            type = "4";
        }
        else if ((ccNum.startsWith("6011") || ccNum.startsWith("622") || ccNum.startsWith("644") || ccNum.startsWith(" 645") || ccNum.startsWith("646") || ccNum.startsWith("647") || ccNum.startsWith("648") || ccNum.startsWith("649") || ccNum.startsWith("65")) && (length == 16 || length == 19))
        {
            cardtype = "DISC";
            type = "5";
        }
        else if ((ccNum.startsWith("35")) && (length == 16 || length == 19))
        {
            cardtype = "JCB";
            type = "16";
        }
        else if ((ccNum.startsWith("5018") || ccNum.startsWith("5020") || ccNum.startsWith("5038") || ccNum.startsWith("5893") || ccNum.startsWith("6304") || ccNum.startsWith("6759") || ccNum.startsWith("6761") || ccNum.startsWith("6762") || ccNum.startsWith("6763") || ccNum.startsWith("6709") || ccNum.startsWith("6799")) && (length == 16 || length == 19))
        {
            cardtype = "MAESTRO";
            type = "37";
        }
        else if (((ccNum.startsWith("637") || ccNum.startsWith("638") || ccNum.startsWith("639")) && length == 16))
        {
            cardtype = "INSTAPAYMENT";
            type = "38";
        }
        return type;
    }

    private static boolean IsMatch(String s, String pattern)
    {
        try
        {
            Pattern patt = Pattern.compile(pattern);
            Matcher matcher = patt.matcher(s);
            return matcher.matches();
        }
        catch (RuntimeException e)
        {
            return false;
        }
    }

    public static boolean isValid(String cardNumber)
    {
        if (cardNumber == null || cardNumber.equals(""))
        {
            return false;
        }
        if (!testCard(cardNumber))
        {


            String digitsOnly = getDigitsOnly(cardNumber);
            int sum = 0;
            int digit = 0;
            int addend = 0;
            boolean timesTwo = false;

            for (int i = digitsOnly.length() - 1; i >= 0; i--)
            {
                digit = Integer.parseInt(digitsOnly.substring(i, i + 1));
                if (timesTwo)
                {
                    addend = digit * 2;
                    if (addend > 9)
                    {
                        addend -= 9;
                    }
                }
                else
                {
                    addend = digit;
                }
                sum += addend;
                timesTwo = !timesTwo;
            }

            int modulus = sum % 10;
            return modulus == 0;
        }
        else
        {
            return true;
        }


    }

    private static boolean testCard(String cardNumber)
    {
        ArrayList<String> testCards = new ArrayList<String>();
        testCards.add("4515151515151515");
        testCards.add("4515151515151510");
        testCards.add("4000000000000000");
        testCards.add("4000100020004400");
        testCards.add("4000100020004401");
        testCards.add("4000100020004402");
        testCards.add("4000100020004403");
        testCards.add("4000100020004404");
        testCards.add("4000100020004405");
        testCards.add("4000100020004406");
        testCards.add("4000100020004407");
        testCards.add("4000100020004408");
        testCards.add("4000100020004409");
        testCards.add("4111111111111111");
        testCards.add("5111111111111111");
        testCards.add("4111111111111110");
        testCards.add("4000000000000001");
        testCards.add("4012888888881881");
        testCards.add("4222000002020001");
        testCards.add("4222000002020002");

        return testCards.contains(cardNumber);
    }

    /*public static String getCardType(String ccNum) throws NumberFormatException
    {
        ccNum.trim();
        int bankCode = 0;
        int length = 0;

        try
        {
            if(!ccNum.equals(""))
            {
                bankCode = Integer.parseInt(ccNum.substring(0, 4));
                length = ccNum.length();
            }
        }
        catch (NumberFormatException e)
        {
            return null;
        }

        String type = null;

        if (bankCode >= 4000 && bankCode < 5000 && (length == 13 || length == 16))
            type = "VISA"; //Visa
        else if (((bankCode >= 5100 && bankCode < 5600) || (bankCode >= 2200 && bankCode < 2800)) && length == 16)
            type = "MC";   //Master Card
        else if (bankCode >= 3000 && bankCode < 3060 && length == 14)
            type = "DINER";    //Diners Club
        else if (bankCode >= 3400 && bankCode < 3500 && length == 15)
            type = "AMEX"; //American Express
        else if (bankCode >= 3600 && bankCode < 3700 && length == 14)
            type = "DINER";    //Diners Club
        else if (bankCode >= 3700 && bankCode < 3800 && length == 15)
            type = "AMEX"; //American Express
        else if (bankCode >= 3800 && bankCode < 3890 && length == 14)
            type = "DINER";   // Diners Club
        else if (bankCode == 6011 && length == 16)
            type = "DISC";   // Discover/Novus
        ccNum = null;
        return type;

    }
*/
    public static String getCardType(String ccNum) throws NumberFormatException
    {
        int length = ccNum.trim().length();
        String cardType = "";
        String type = "";
        if ((ccNum.startsWith("4")) && (length == 13 || length == 16 || length == 19))
        {
            cardType = "VISA";
        }
        else if ((ccNum.startsWith("51") || ccNum.startsWith("52") || ccNum.startsWith("53") || ccNum.startsWith("54") || ccNum.startsWith("55") || ccNum.startsWith("22") || ccNum.startsWith("23") || ccNum.startsWith("24") || ccNum.startsWith("25") || ccNum.startsWith("26") || ccNum.startsWith("27")) && length == 16)
        {
            cardType = "MC";
        }
        else if ((ccNum.startsWith("300") || ccNum.startsWith("301") || ccNum.startsWith("302") || ccNum.startsWith("303") || ccNum.startsWith("304")
                || ccNum.startsWith("305") || ccNum.startsWith("36") /*|| ccNum.startsWith("54") || ccNum.startsWith("55")*/) && (length == 14 || length == 16))
        {
            cardType = "DINER";
        }
        else if ((ccNum.startsWith("34") || ccNum.startsWith("37")) && length == 15)
        {
            cardType = "AMEX";
        }
        else if ((ccNum.startsWith("6521") || ccNum.startsWith("6522") || ccNum.startsWith("3536") || ccNum.startsWith("3538") || ccNum.startsWith("5028") || ccNum.startsWith("5085") || ccNum.startsWith("5086") || ccNum.startsWith("5087") || ccNum.startsWith("5088") || ccNum.startsWith("5089") || ccNum.startsWith("6061") || ccNum.startsWith("6062") || ccNum.startsWith("6063") || ccNum.startsWith("6064") || ccNum.startsWith("6065") || ccNum.startsWith("6066") || ccNum.startsWith("6067") || ccNum.startsWith("6068") || ccNum.startsWith("6069") || ccNum.startsWith("6070") || ccNum.startsWith("6071") || ccNum.startsWith("6072") || ccNum.startsWith("6073") || ccNum.startsWith("6074") || ccNum.startsWith("6075") || ccNum.startsWith("6076") || ccNum.startsWith("6077") || ccNum.startsWith("6078") || ccNum.startsWith("6079") || ccNum.startsWith("6080") || ccNum.startsWith("6081") || ccNum.startsWith("6082") || ccNum.startsWith("6083") || ccNum.startsWith("6084") || ccNum.startsWith("6085") || ccNum.startsWith("6086") || ccNum.startsWith("6087") || ccNum.startsWith("6088") || ccNum.startsWith("6089") || ccNum.startsWith("6273") || ccNum.startsWith("6521") || ccNum.startsWith("6522") || ccNum.startsWith("6523") || ccNum.startsWith("6524") || ccNum.startsWith("6525") || ccNum.startsWith("6528") || ccNum.startsWith("6950") || ccNum.startsWith("8172") || ccNum.startsWith("8201") || ccNum.startsWith("8888") || ccNum.startsWith("9999")) && length == 16)
        {
            cardType = "RUPAY";
        }
        else if ((ccNum.startsWith("6011") || ccNum.startsWith("622") || ccNum.startsWith("644") || ccNum.startsWith("645") || ccNum.startsWith("646") || ccNum.startsWith("647") || ccNum.startsWith("648") || ccNum.startsWith("649") || ccNum.startsWith("65")) && (length == 16 || length == 19))
        {
            cardType = "DISC";
        }
        else if ((ccNum.startsWith("35")) && (length == 16 || length == 19))
        {
            cardType = "JCB";
        }
        else if ((ccNum.startsWith("5018") || ccNum.startsWith("5020") || ccNum.startsWith("5038") || ccNum.startsWith("5893") || ccNum.startsWith("6304") || ccNum.startsWith("6759") || ccNum.startsWith("6761") || ccNum.startsWith("6762") || ccNum.startsWith("6763") || ccNum.startsWith("6709") || ccNum.startsWith("6799") || ccNum.startsWith("6705") || ccNum.startsWith("5612")) && (length == 16 || length == 19))
        {
            cardType = "MAESTRO";
        }
        else if ((ccNum.startsWith("637") || ccNum.startsWith("638") || ccNum.startsWith("639")) && length == 16)
        {
            cardType = "INSTAPAYMENT";
        }
        return cardType;
    }

    public static String[] getStringArrayFromDelimitedString(String str, String delimiter)
    {

        if (Functions.parseData(str) == null)
        {
            return null;
        }
        StringTokenizer strtokeni = new StringTokenizer(str, delimiter);
        String strarr[] = new String[strtokeni.countTokens()];
        for (int i = 0; strtokeni.hasMoreTokens(); i++)
        {
            strarr[i] = strtokeni.nextToken();
        }
        return strarr;
    }

    public static String encryptString(String sPlainText)
    {
        String b64str = "";

        try
        {
            CipherText ct = ESAPI.encryptor().encrypt(new PlainText(sPlainText));
            byte[] serializedCiphertext = ct.asPortableSerializedByteArray();
            b64str = ESAPI.encoder().encodeForBase64(serializedCiphertext, false);
        }
        catch (Exception e)
        {
            log.error("Encryption exception  thrown while encrypting data", e);
        }
        return b64str;
        // return getBlowfishEasy().encryptString(sPlainText);
    }

    public static String decryptString(String sCipherText)
    {

        PlainText plaintext = new PlainText("");

        try
        {
            byte[] serializedCiphertext = ESAPI.encoder().decodeFromBase64(sCipherText);
            CipherText restoredCipherText = CipherText.fromPortableSerializedBytes(serializedCiphertext);
            plaintext = ESAPI.encryptor().decrypt(restoredCipherText);
            //log.debug("____+Plain text+____"+plaintext.toString());
        }
        catch (Exception e)
        {
            log.error("Exception thrown while decrypting data", e);
        }
        return plaintext.toString();
        //return getBlowfishEasy().decryptString(sCipherText);
    }

    public static BlowfishEasy getBlowfishEasy()
    {
        return new BlowfishEasy(PASSWORD_ENCRYPTION_KEY.toCharArray());
    }

    public static boolean validateCSRF(String ctoken, User user)
    {
        boolean isValid = true;


        if (user == null || user.getCSRFToken() == null || user.getCSRFToken().equals(""))
        {
            log.debug("UnAuthorized member is logout 1");
            return false;
        }
        if (!user.getCSRFToken().equals(ctoken))
        {
            log.debug("UnAuthorized member is logout 2");
            return false;
        }
        try
        {
            user.resetCSRFToken();

        }
        catch (Exception e)
        {

        }

        return isValid;

    }

    public static boolean validateAPIToken(String ctoken, User user)
    {
        boolean isValid = true;


        if (user == null || user.getCSRFToken() == null || user.getCSRFToken().equals(""))
        {
            log.debug("UnAuthorized member is logout 1");
            return false;
        }
        if (!user.getCSRFToken().equals(ctoken))
        {
            log.debug("UnAuthorized member is logout 2");
            return false;
        }
        log.debug("csrf token valid---" + isValid + "--token---" + user.getCSRFToken());
        return isValid;

    }

    public static boolean validateAdminCSRF(String ctoken, User user)
    {
        boolean isValid = true;


        if (user == null || user.getCSRFToken() == null || user.getCSRFToken().equals(""))
        {
            log.debug("UnAuthorized member is logout 1");
            return false;
        }
        if (!user.getCSRFToken().equals(ctoken))
        {
            log.debug("UnAuthorized member is logout 2");
            return false;
        }
        try
        {
            user.resetCSRFToken();


        }
        catch (Exception e)
        {

        }

        return isValid;

    }

    public static boolean validateTransactionCSRF(String ctoken, HttpSession session)
    {
        boolean isValid = true;
        log.debug("Inside validateTransactionCSRF");

        if (session == null)
        {
            log.debug("UnAuthorized member is logout 1");
            return false;
        }
        if (!session.getAttribute("ctoken").equals(ctoken))
        {
            log.debug("UnAuthorized member is logout 2");
            return false;
        }

        return isValid;

    }

    public static boolean validateSKitTransactionCSRF(String ctoken, HttpSession session)
    {
        boolean isValid = true;
        log.debug("Inside validateSKitTransactionCSRF");
        if (session == null)
        {
            log.debug("UnAuthorized member is logout 1");
            return false;
        }
        log.debug("Inside validateSKitTransactionCSRF" + session.getAttribute("ctoken"));
        if (!session.getAttribute("ctoken").equals(ctoken))
        {
            log.debug("UnAuthorized member is logout 2");
            return false;
        }
        log.debug("Inside validateSKitTransactionCSRF" + isValid);
        return isValid;

    }

    public static boolean validate3DTransactionCSRF(String ctoken, HttpSession session)
    {
        boolean isValid = true;
        log.debug("Inside validate3DTransactionCSRF");
        if (session == null)
        {
            log.debug("UnAuthorized member is logout 1");
            return false;
        }
        log.debug("Inside validate3DTransactionCSRF" + session.getAttribute("ctoken"));
        if (session.getAttribute("ctoken") == null)
        {
            log.debug("Valid Request 1st Attempt");
            return true;
        }
        if (!session.getAttribute("ctoken").equals(ctoken))
        {
            log.debug("UnAuthorized member more than 1 attempt");
            return false;
        }
        log.debug("Inside validate3DTransactionCSRF" + isValid);

        return isValid;

    }

    public static boolean validateVirtualCSRF(String ctoken, User user)
    {
        boolean isValid = true;


        if (user == null || user.getCSRFToken() == null || user.getCSRFToken().equals(""))
        {
            log.debug("UnAuthorized member is logout 1");
            return false;
        }
        if (!user.getCSRFToken().equals(ctoken))
        {
            log.debug("UnAuthorized member is logout 2");
            return false;
        }
        try
        {
            //user.resetCSRFToken();    //commented as resetting CSRF giving problem with Virtual terminal
            // other option is to remove CSRF check for Virtual Terminal


        }
        catch (Exception e)
        {

        }

        return isValid;

    }

    public static HttpSession getNewSession(HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        HashMap<String, Object> attributes = new HashMap<String, Object>();
        // copy/save all attributes
        Enumeration<String> enames = session.getAttributeNames();
        while (enames.hasMoreElements())
        {
            String name = enames.nextElement();
            if (!name.equals("JSESSIONID"))
            {
                attributes.put(name, session.getAttribute(name));
                //log.debug("Attribute=="+name+" Value=="+session.getAttribute(name));
                if (null != session.getAttribute("userid"))
                {
                    MDC.put("id", session.getAttribute("userid"));
                }
            }

        }
        // invalidate the session
        session.invalidate();
        // create a new session
        session = request.getSession(true);
        // "restore" the session values
        for (Map.Entry<String, Object> et : attributes.entrySet())
        {
            session.setAttribute(et.getKey(), et.getValue());

        }


        return session;

    }

    public static void setCookie(HttpServletRequest request)
    {

        //Added for Setting secure cookies

        Cookie[] cookies = request.getCookies();
        if (cookies != null)
        {
            for (int i = 0; i < cookies.length; i++)
            {
                Cookie cookie = cookies[i];
                if (cookie != null)
                {
                    // ESAPI.securityConfiguration().getHttpSessionIdName() returns JSESSIONID by default configuration
                    if (ESAPI.securityConfiguration().getHttpSessionIdName().equals(cookie.getName()))
                    {

                        ESAPI.httpUtilities().addCookie(cookie);

                    }
                }
            }
        }

    }

    public static boolean validateCSRFAnonymos(String ctoken, User user)
    {
        boolean isValid = true;

        if (user == null || user.getCSRFToken() == null || user.getCSRFToken().equals(""))
        {
            log.debug("validateCSRFAnonymos UnAuthorized member is logout 1");
            return false;
        }
        if (!user.getCSRFToken().equals(ctoken))
        {


            log.debug("validateCSRFAnonymos UnAuthorized member is logout 2" + user.getCSRFToken() + "====" + ctoken);
            return false;
        }

        return isValid;

    }

    public static boolean validateCSECSRF(String ctoken, User user)
    {
        boolean isValid = true;


        if (user == null || user.getCSRFToken() == null || user.getCSRFToken().equals(""))
        {
            log.debug("UnAuthorized CSE is logout 1");
            return false;
        }
        if (!user.getCSRFToken().equals(ctoken))
        {
            log.debug("UnAuthorized CSE is logout 2");
            return false;
        }
        try
        {
            user.resetCSRFToken();

        }
        catch (Exception e)
        {
            log.error("Error while validating csrfToken ", e);
        }

        return isValid;

    }

    public static String comboval(String check)
    {
        String str = "";
        if (check != null && check.equals("Y"))
        {
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "</option>";
            str = str + "<option value=N>N</option>";
        }
        else
        {
            str = str + "<option value=Y>Y</option>";
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "</option>";
        }


        return str;

    }

    public static String comboPersonalInfo(String check)
    {
        String str = "";
        if (check != null && check.equals("Y"))
        {
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "</option>";
            str = str + "<option value=E>E</option>";
            str = str + "<option value=M>M</option>";
            str = str + "<option value=N>N</option>";
        }
        else if (check != null && check.equals("E"))
        {
            str = str + "<option value=Y>Y</option>";
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "</option>";
            str = str + "<option value=M>M</option>";
            str = str + "<option value=N>N</option>";
        }
        else if (check != null && check.equals("M"))
        {
            str = str + "<option value=Y>Y</option>";
            str = str + "<option value=E>E</option>";
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "</option>";
            str = str + "<option value=N>N</option>";
        }
        else
        {
            str = str + "<option value=Y>Y</option>";
            str = str + "<option value=E>E</option>";
            str = str + "<option value=M>M</option>";
            str = str + "<option value=" + check + " selected=\"selected\"> N </option>";
        }
        return str;
    }

    public static String comboval3D(String check)
    {
        String str = "";
        if (check != null && check.equals("3D"))
        {
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "</option>";
            str = str + "<option value=Non-3D>Non-3D</option>";
            str = str + "<option value=Both>Both</option>";
            str = str + "<option value=N>N</option>";
        }
        else if (check != null && check.equals("Non-3D"))
        {
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "</option>";
            str = str + "<option value=3D>3D</option>";
            str = str + "<option value=Both>Both</option>";
            str = str + "<option value=N>N</option>";
        }
        else if (check != null && check.equals("Both"))
        {
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "</option>";
            str = str + "<option value=Non-3D>Non-3D</option>";
            str = str + "<option value=3D>3D</option>";
            str = str + "<option value=N>N</option>";
        }
        else
        {
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "</option>";
            str = str + "<option value=Non-3D>Non-3D</option>";
            str = str + "<option value=3D>3D</option>";
            str = str + "<option value=Both>Both</option>";
        }

        return str;

    }

    public static String comboval7(String check)
    {
        String str = "";
        if (check != null && check.equals("Y"))
        {
            str = str + "<option value=N>Disable</option>";
            str = str + "<option value=" + check + " selected=\"selected\">Enable</option>";
        }
        else
        {
            str = str + "<option value=Y>Enable</option>";
            str = str + "<option value=" + check + " selected=\"selected\">Disable</option>";
        }
        return str;
    }


    public static String comboval6(String check)
    {
        String str = "";
        if (check != null && check.equals("Y"))
        {
            str = str + "<option value=N>Optional</option>";
            str = str + "<option value=" + check + " selected=\"selected\">Mandatory</option>";
        }
        else
        {
            str = str + "<option value=Y>Mandatory</option>";
            str = str + "<option value=" + check + " selected=\"selected\">Optional</option>";
        }
        return str;
    }

    public static String comboval8(String check)
    {
        String str = "";
        if (check != null && check.equals("Y"))
        {
            str = str + "<option value=N>Inactive</option>";
            str = str + "<option value=" + check + " selected=\"selected\">Active</option>";
        }
        else
        {
            str = str + "<option value=Y>Active</option>";
            str = str + "<option value=" + check + " selected=\"selected\">Inactive</option>";
        }
        return str;
    }

    public static String comboval5(String check)
    {
        String str = "";
        if (check != null && check.equals("Y"))
        {
            str = str + "<option value=N>Hidden</option>";
            str = str + "<option value=" + check + " selected=\"selected\">View</option>";
        }
        else
        {
            str = str + "<option value=Y>View</option>";
            str = str + "<option value=" + check + " selected=\"selected\">Hidden</option>";
        }
        return str;
    }

    public static String comboval1(String check)
    {
        String str = "";
        if (check != null && check.equals("Y"))
        {
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "</option>";
            str = str + "<option value=N>N</option>";
            str = str + "<option value=O>O</option>";
            str = str + "<option value=R>R</option>";
        }
        else if (check != null && check.equals("N"))
        {
            str = str + "<option value=Y>Y</option>";
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "</option>";
            str = str + "<option value=O>O</option>";
            str = str + "<option value=R>R</option>";
        }
        else if (check != null && check.equals("O"))
        {
            str = str + "<option value=Y>Y</option>";
            str = str + "<option value=N>N</option>";
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "</option>";
            str = str + "<option value=R>R</option>";
        }
        else
        {
            str = str + "<option value=Y>Y</option>";
            str = str + "<option value=N>N</option>";
            str = str + "<option value=O>O</option>";
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "</option>";
        }


        return str;

    }

    public static String comboval3DsVersion(String check)
    {
        String str = "";
        if (check != null && check.equals("3Dsv2=1"))
        {
            str = str + "<option value='" + check + "' selected=\"selected\">3Ds v1</option>";
            str = str + "<option value='3Dsv2'>3Ds v2</option>";
        }
        else if (check != null && check.equals("3Dsv2"))
        {
            str = str + "<option value='3Dsv1'>3Ds v1</option>";
            str = str + "<option value='" + check + "' selected=\"selected\">3Ds v2</option>";
        }
        else
        {
            str = str + "<option value='3Dsv1'>3Ds v1</option>";
            str = str + "<option value='3Dsv2'>3Ds v2</option>";
        }


        return str;

    }

    public static String comboval2(String check)
    {
        String str = "";
        if (check != null && check.equals("N"))
        {
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "</option>";
            str = str + "<option value=Y>Y</option>";
            str = str + "<option value=V>V</option>";
        }
        else if (check != null && check.equals("Y"))
        {
            str = str + "<option value=N>N</option>";
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "</option>";
            str = str + "<option value=V>V</option>";
        }
        else
        {
            str = str + "<option value=N>N</option>";
            str = str + "<option value=Y>Y</option>";
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "</option>";
        }


        return str;

    }

    public static String comboval3(String check)
    {
        String str = "";
        if (check != null && check.equals("N"))
        {
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "</option>";
            str = str + "<option value=terminal_Level>Terminal Level</option>";
            str = str + "<option value=account_Level>Account Level</option>";
            str = str + "<option value=account_member_Level>Account Member Level</option>";
        }
        else if (check != null && check.equals("terminal_Level"))
        {
            str = str + "<option value=N>N</option>";
            str = str + "<option value=" + check + " selected=\"selected\">Terminal Level</option>";
            str = str + "<option value=account_Level>Account Level</option>";
            str = str + "<option value=account_member_Level>Account Member Level</option>";
        }
        else if (check != null && check.equals("account_Level"))
        {
            str = str + "<option value=N>N</option>";
            str = str + "<option value=terminal_Level>Terminal Level</option>";
            str = str + "<option value=" + check + " selected=\"selected\">Account Level</option>";
            str = str + "<option value=account_member_Level>Account Member Level</option>";
        }
        else
        {
            str = str + "<option value=N>N</option>";
            str = str + "<option value=terminal_Level>Terminal Level</option>";
            str = str + "<option value=account_Level>Account Level</option>";
            str = str + "<option value=" + check + " selected=\"selected\">Account Member Level</option>";
        }
        return str;
    }

    public static String comboval4(String check)
    {
        String str = "";
        if (check != null && check.equals("N"))
        {
            str = str + "<option value=" + check + " selected=\"selected\">N</option>";
            str = str + "<option value=merchant_level_charges>Merchant Level Charges</option>";
            str = str + "<option value=submerchant_level_charges>Submerchant Level Charges</option>";
            str = str + "<option value=split>Split</option>";
        }
        else if (check != null && check.equals("merchant_level_charges"))
        {
            str = str + "<option value=N>N</option>";
            str = str + "<option value=" + check + " selected=\"selected\">Merchant Level Charges</option>";
            str = str + "<option value=submerchant_level_charges>Submerchant Level Charges</option>";
            str = str + "<option value=split>Split</option>";
        }
        else if (check != null && check.equals("submerchant_level_charges"))
        {
            str = str + "<option value=N>N</option>";
            str = str + "<option value=merchant_level_charges>Merchant Level Charges</option>";
            str = str + "<option value=" + check + " selected=\"selected\">Submerchant Level Charges</option>";
            str = str + "<option value=split>Split</option>";
        }
        else
        {
            str = str + "<option value=N>N</option>";
            str = str + "<option value=merchant_level_charges>Merchant Level Charges</option>";
            str = str + "<option value=submerchant_level_charges>Submerchant Level Charges</option>";
            str = str + "<option value=" + check + " selected=\"selected\">Split</option>";
        }
        return str;
    }

    public static String comboval9(String check)
    {
        String str = "";
        if (check != null && check.equals("N"))
        {
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "</option>";
            str = str + "<option value=Bin>Bin</option>";
            str = str + "<option value=Card>Card</option>";
            str = str + "<option value=Bin_Country>Bin Country</option>";
        }
        else if (check != null && check.equals("Bin"))
        {
            str = str + "<option value=N>N</option>";
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "</option>";
            str = str + "<option value=Card>Card</option>";
            str = str + "<option value=Bin_Country>Bin Country</option>";
        }
        else if (check != null && check.equals("Card"))
        {
            str = str + "<option value=N>N</option>";
            str = str + "<option value=Bin>Bin</option>";
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "</option>";
            str = str + "<option value=Bin_Country>Bin Country</option>";
        }
        else
        {
            str = str + "<option value=N>N</option>";
            str = str + "<option value=Bin>Bin</option>";
            str = str + "<option value=Card>Card</option>";
            str = str + "<option value=" + check + " selected=\"selected\">Bin Country</option>";
        }
        return str;
    }

    public static String combovalLanguage(String check)
    {
        String str = "";
        if (check != null && check.equals("RO"))
        {
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "-Romanian" + "</option>";
            str = str + "<option value=JA>JA-Japanese</option>";
            str = str + "<option value=BG>BG-Bulgarian</option>";
            str = str + "<option value=EN>EN-English</option>";
        }
        else if (check != null && check.equals("JA"))
        {
            str = str + "<option value=RO>RO-Romanian</option>";
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "-Japanese" + "</option>";
            str = str + "<option value=BG>BG-Bulgarian</option>";
            str = str + "<option value=EN>EN-English</option>";
        }
        else if (check != null && check.equals("BG"))
        {
            str = str + "<option value=RO>RO-Romanian</option>";
            str = str + "<option value=JA>JA-Japanese</option>";
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "-Bulgarian" + "</option>";
            str = str + "<option value=EN>EN-English</option>";
        }
        else
        {
            str = str + "<option value=RO>RO-Romanian</option>";
            str = str + "<option value=JA>JA-Japanese</option>";
            str = str + "<option value=BG>BG-Bulgarian</option>";
            str = str + "<option value=" + check + " selected=\"selected\">EN-English</option>";
        }

        return str;
    }

    public static String combovalLang(String check)
    {
        String str = "";
        if (check != null && check.equals("RO"))
        {
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "-Romanian" + "</option>";
            str = str + "<option value=JA>JA-Japanese</option>";
            str = str + "<option value=BG>BG-Bulgarian</option>";
            str = str + "<option value=DU>DU-Dutch</option>";
            str = str + "<option value=FR>FR-French</option>";
            str = str + "<option value=EN>EN-English</option>";
        }
        else if (check != null && check.equals("JA"))
        {
            str = str + "<option value=RO>RO-Romanian</option>";
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "-Japanese" + "</option>";
            str = str + "<option value=BG>BG-Bulgarian</option>";
            str = str + "<option value=DU>DU-Dutch</option>";
            str = str + "<option value=FR>FR-French</option>";
            str = str + "<option value=EN>EN-English</option>";
        }
        else if (check != null && check.equals("BG"))
        {
            str = str + "<option value=RO>RO-Romanian</option>";
            str = str + "<option value=JA>JA-Japanese</option>";
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "-Bulgarian" + "</option>";
            str = str + "<option value=DU>DU-Dutch</option>";
            str = str + "<option value=FR>FR-French</option>";
            str = str + "<option value=EN>EN-English</option>";
        }
        else if (check != null && check.equals("DU"))
        {
            str = str + "<option value=RO>RO-Romanian</option>";
            str = str + "<option value=JA>JA-Japanese</option>";
            str = str + "<option value=BG>BG-Bulgarian</option>";
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "-Dutch" + "</option>";
            str = str + "<option value=FR>FR-French</option>";
            str = str + "<option value=EN>EN-English</option>";
        }
        else if (check != null && check.equals("FR"))
        {
            str = str + "<option value=RO>RO-Romanian</option>";
            str = str + "<option value=JA>JA-Japanese</option>";
            str = str + "<option value=BG>BG-Bulgarian</option>";
            str = str + "<option value=DU>DU-Dutch</option>";
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "-French" + "</option>";
            str = str + "<option value=EN>EN-English</option>";
        }
        else
        {
            str = str + "<option value=RO>RO-Romanian</option>";
            str = str + "<option value=JA>JA-Japanese</option>";
            str = str + "<option value=BG>BG-Bulgarian</option>";
            str = str + "<option value=DU>DU-Dutch</option>";
            str = str + "<option value=FR>FR-French</option>";
            str = str + "<option value=" + check + " selected=\"selected\">EN-English</option>";
        }

        return str;
    }

    public static String combovalSystem(String check)
    {
        String str = "";
        if (check != null && check.equals("System"))
        {
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "</option>";
            str = str + "<option value=Member>Member</option>";
            str = str + "<option value=N>N</option>";
        }
        else if (check != null && check.equals("Member"))
        {
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "</option>";
            str = str + "<option value=System>System</option>";
            str = str + "<option value=N>N</option>";
        }
        else
        {
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "</option>";
            str = str + "<option value=System>System</option>";
            str = str + "<option value=Member>Member</option>";
        }

        return str;
    }

    public static String comboCardLimit(String check)
    {
        String str="";
        if (check!= null && check.equals("account_Level"))
        {
            str = str + "<option value=" + check + " selected=\"selected\">Account Level</option>";
            str = str + "<option value=mid_Level>MID Level</option>";
            str = str + "<option value=N>N</option>";
        }
        else if (check!= null && check.equals("mid_Level"))
        {
            str = str + "<option value=" + check + " selected=\"selected\">MID Level</option>";
            str = str + "<option value=account_Level>Account Level</option>";
            str = str + "<option value=N>N</option>";
        }
        else
        {
            str = str + "<option value=" + check + " selected=\"selected\">N</option>";
            str = str + "<option value=mid_Level>MID Level</option>";
            str = str + "<option value=account_Level>Account Level</option>";
        }
        return str;
    }

    public static String combo3DSupport(String check)
    {
        String str="";
        if (check!= null && check.equals("Y"))
        {
            str = str + "<option value=" + check + " selected=\"selected\">Y</option>";
            str = str + "<option value=O>O</option>";
            str = str + "<option value=R>R</option>";
            str = str + "<option value=N>N</option>";
        }
        else if (check!= null && check.equals("O"))
        {
            str = str + "<option value=" + check + " selected=\"selected\">O</option>";
            str = str + "<option value=R>R</option>";
            str = str + "<option value=Y>Y</option>";
            str = str + "<option value=N>N</option>";
        }
        else if (check!= null && check.equals("R"))
        {
            str = str + "<option value=" + check + " selected=\"selected\">R</option>";
            str = str + "<option value=Y>Y</option>";
            str = str + "<option value=O>O</option>";
            str = str + "<option value=N>N</option>";
        }
        else
        {
            str = str + "<option value=" + check + " selected=\"selected\">N</option>";
            str = str + "<option value=Y>Y</option>";
            str = str + "<option value=O>O</option>";
            str = str + "<option value=R>R</option>";
        }
        return str;
    }

    public static String getIpAddress(final HttpServletRequest request)
    {
        // Fetch Ipaddress from X Headers if behind firewall
        String ipAddress = request.getHeader("X-Forwarded-For");
        log.debug("Forwarded Header IP: " + ipAddress);

        //log.debug("==========X-Forwarded-Host====" + request.getHeader("X-Forwarded-Host"));

        if (ipAddress == null)
        {
            ipAddress = request.getRemoteAddr();
            log.debug("IP from Request Object: " + ipAddress);
        }

        if (ipAddress.contains(","))
        {
            log.debug("IP with Comma::: " + ipAddress);
            String sIp[] = ipAddress.split(",");
            ipAddress = sIp[0].trim();
        }
        log.debug("Final IP from Header::: " + ipAddress);

        return ipAddress;


    }

    public static String getForwardedHost(final HttpServletRequest request)
    {
        // Fetch Ipaddress from X Headers if behind firewall
        String host = request.getHeader("X-Forwarded-Host");

        log.debug("==========X-Forwarded-Host====" + request.getHeader("X-Forwarded-Host"));
        if (host == null)
        {
            host = request.getHeader("X-Forwarded-Server");
            log.debug("Server: " + host);
        }

        return host;
    }

    public static boolean checkAPIGateways(String fromtype)
    {
        ArrayList<String> directAPIGateways = new ArrayList<String>();
        directAPIGateways.add(Gold24PaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayVisionPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(WSecPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(UGSPaymentGateway.GATEWAY_TYPE_FORT);
        directAPIGateways.add(UGSPaymentGateway.GATEWAY_TYPE_UGS);
        directAPIGateways.add(SwiffpayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayOnGateway.GATEWAY_TYPE_PAYON);
        directAPIGateways.add(PayOnGateway.GATEWAY_TYPE_CATELLA);
        directAPIGateways.add(NMIPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayWorldPaymentGateway.GATEWAY_TYPE_RITUMU);
        directAPIGateways.add(PayWorldPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(DeltaPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(FrickBankPaymentGateWay.GATEWAY_TYPE);
        directAPIGateways.add(SafeChargePaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(BorgunPaymentGateway.GATEWAY_TYPE);
        // directAPIGateways.add(WireCardPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(EuroPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PaygatewayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(FluzznetworkPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ArenaPlusPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(AlliedWalletPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(SBMPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(LpbPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayforasiaPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(DVGPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PfsPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(CashFlowPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PaspxPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(STSPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(CredoraxPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(EmaxHighRiskPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ProcessingPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(TwoGatePayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(OPXPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(VisaNetPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayDollarPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ProcesosMCPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(INICICIPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayHostPayGatePaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(AuxPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(LibillPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(P4CreditCardPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(P4PaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(P4DirectDebitPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PaySecPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(BeekashPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayMitcoPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(TrustsPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PbsPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(WonderlandPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ApcoPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(TrustPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ClearSettlePaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(B4PaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(WireCardNPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(KotakAllPayCardPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(SkrillPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(DectaMerchantPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PerfectMoneyPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PerfectMoneyPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(VoucherMoneyPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PaySpacePaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(UPayGatePaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(QpPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(JetonPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(JetonVoucherPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(BankonePaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ClearSettleVoucherPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(EpayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(RavePaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(TrustlyPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(GCPPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayneticsGateway.GATEWAY_TYPE);
        directAPIGateways.add(EmexpayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(BillDeskPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayneticsGateway.GATEWAY_TYPE);
        directAPIGateways.add(EMSPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(TransactiumPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(CardinityPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ApcoFastpayPaymentGateway.GATEWAY_TYPE_APC0_ALDRAPAY);
        directAPIGateways.add(ApcoFastpayPaymentGateway.GATEWAY_TYPE_APCO_PURPLEPAY);
        directAPIGateways.add(ApcoFastpayPaymentGateway.GATEWAY_TYPE_APCO_RAVE);
        directAPIGateways.add(ApcoFastpayPaymentGateway.GATEWAY_TYPE_APCO_FAST_PAY);
        directAPIGateways.add(RSPPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(SecureTradingGateway.GATEWAY_TYPE);
        directAPIGateways.add(ECPPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(DusPayDDPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(UnionPayInternationalPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(FlutterWavePaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(BitcoinPaygatePaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PBSPaymentGatewayAmex.GATEWAY_TYPE);
        directAPIGateways.add(PayClubPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(TojikaPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(KortaPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(DusPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ClearSettleHPPGateway.GATEWAY_TYPE);
        directAPIGateways.add(SafexPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(NPayOnGateway.GATEWAY_TYPE);
        directAPIGateways.add(EPaySolutionGateway.GATEWAY_TYPE);
        directAPIGateways.add(iMerchantPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(NestPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ZotapayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(TransactiumPaymentGateway.GATEWAY_TYPE_TRX);
        directAPIGateways.add(SabadellPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(NPaySecPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(CardPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(TransactiumPaymentGateway.GATEWAY_TYPE_TRANSACTIUM_1);
        directAPIGateways.add(WLPaymentGateway.GATEWAY_TYPE_AGNIPAY);
        directAPIGateways.add(WLPaymentGateway.GATEWAY_TYPE_TRANSACTWORLD);
        directAPIGateways.add(WLPaymentGateway.GATEWAY_TYPE_SHIMOTOMO);
        directAPIGateways.add(WLPaymentGateway.GATEWAY_TYPE_SAMPLEPSP);
        directAPIGateways.add(WLPaymentGateway.GATEWAY_TYPE_FIDOMS);
        directAPIGateways.add(AwepayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(AllPay88PaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(CyberSourcePaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayVaultProPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(RomCardPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ElegroPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(BRDPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(QuickCardPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(OneRoadPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ICardPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PaySendPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(DectaNewPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(NinjaWalletPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(NexiPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(DusPayCardPaymentGateway.GATEWAY_TYPE);
        //directAPIGateways.add(ReitumuBankMerchantPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PaySendWalletPaymentGateway.GATEWAY_TYPE);
        //directAPIGateways.add(ApsPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(VoguePayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(OnlinePayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayeezyPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PhoneixPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayvisionV2PaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayBoutiquePaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(TrueVoPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(XPatePaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(AcqraPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(CuroPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(BhartiPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(FlutterWaveBarterPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ZotaPayGateway.GATEWAY_TYPE);
        directAPIGateways.add(KCPPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(EMerchantPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(EzPayNowPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(JetonCardPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(IlixiumPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ECPCPPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(JPBTPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayonOppwaPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(EcommpayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(TotalPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(KnoxPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(CBCGPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(SafeChargeV2PaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(Triple000PaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(FenigePaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ZhiXinfuPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(AtCyberSourcePaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(OctapayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(TWDTaiwanPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(AtCellulantPaymentGateway.GATEWAY_TYPE);
        // directAPIGateways.add(TapMioPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(TrustPayOppwaPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(QikpayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(GiftPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(QuickPaymentsGateway.GATEWAY_TYPE);
        directAPIGateways.add(LetzPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(BennupayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayneteasyGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayEasyWorldPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(DokuPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(OculusPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(EasyPaymentzPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(IMoneyPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(AsianCheckoutPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(WealthPayGateway.GATEWAY_TYPE);
        directAPIGateways.add(AppleTreeCellulantPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(GlobalGatePaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ApcoFastPayV6PaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayUPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(GpayPaymentzGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayGPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ApexPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(AirpayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ClevelandPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(QikPayV2PaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(CashFlowsCaiboPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(EasyPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(TigerPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(BoltPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(CajaRuralPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(TigerGateWayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(EuroEximBankPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(AamarPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(OmnipayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(BoomBillPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PowerCash21PaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayTMPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(AirtelUgandaPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(MTNUgandaPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(HDFCPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(LyraPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(UBAMCPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(EaseBuzzPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ContinentPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(TransfrPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(BitClearPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(XceptsPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(BnmQuickPaymentGateway.GATEWAY_TYPE);
 //       directAPIGateways.add(FidyPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(FlexepinPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(SmartCodePayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(SmartFastPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(InfiPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(TRAXXPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayGSmilePaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(AlphapayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(SwiffyPaymentGateway.GATEWAY_TYPE);

        return directAPIGateways.contains(fromtype);
    }

    public static boolean checkInquiryAPIGateways(String fromtype)
    {

        ArrayList<String> directAPIGateways = new ArrayList<String>();
        directAPIGateways.add(Gold24PaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayVisionPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(WSecPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(UGSPaymentGateway.GATEWAY_TYPE_FORT);
        directAPIGateways.add(UGSPaymentGateway.GATEWAY_TYPE_UGS);
        directAPIGateways.add(SwiffpayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayOnGateway.GATEWAY_TYPE_PAYON);
        directAPIGateways.add(PayOnGateway.GATEWAY_TYPE_CATELLA);
        directAPIGateways.add(NMIPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayWorldPaymentGateway.GATEWAY_TYPE_RITUMU);
        directAPIGateways.add(PayWorldPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(DeltaPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(FrickBankPaymentGateWay.GATEWAY_TYPE);
        directAPIGateways.add(SafeChargePaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(BorgunPaymentGateway.GATEWAY_TYPE);
        //  directAPIGateways.add(WireCardPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(EuroPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PaygatewayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(FluzznetworkPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ArenaPlusPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(AlliedWalletPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(SBMPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(LpbPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayforasiaPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(DVGPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PfsPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(CashFlowPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PaspxPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(STSPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(CredoraxPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(EmaxHighRiskPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ProcessingPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(TwoGatePayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(OPXPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(VisaNetPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayDollarPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ProcesosMCPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(INICICIPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayHostPayGatePaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(AuxPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(LibillPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(P4CreditCardPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(P4PaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(P4DirectDebitPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PaySecPaymentGateway.GATEWAY_TYPE);

        directAPIGateways.add(BeekashPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayMitcoPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(TrustsPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PbsPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(SofortPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(WonderlandPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ApcoPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(TrustPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ClearSettlePaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(B4PaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(WireCardNPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(KotakAllPayCardPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(SkrillPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(DectaMerchantPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PerfectMoneyPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(VoucherMoneyPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PaySpacePaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(UPayGatePaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(QpPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(JetonPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(JetonVoucherPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PaySafeCardPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(BankonePaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ClearSettleVoucherPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(EpayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(RavePaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(TrustlyPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(GCPPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayneticsGateway.GATEWAY_TYPE);
        directAPIGateways.add(EmexpayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(EMSPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(TransactiumPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(CardinityPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ApcoFastpayPaymentGateway.GATEWAY_TYPE_APC0_ALDRAPAY);
        directAPIGateways.add(ApcoFastpayPaymentGateway.GATEWAY_TYPE_APCO_PURPLEPAY);
        directAPIGateways.add(ApcoFastpayPaymentGateway.GATEWAY_TYPE_APCO_RAVE);
        directAPIGateways.add(ApcoFastpayPaymentGateway.GATEWAY_TYPE_APCO_FAST_PAY);
        directAPIGateways.add(RSPPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(SecureTradingGateway.GATEWAY_TYPE);
        directAPIGateways.add(ECPPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(DusPayDDPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(UnionPayInternationalPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(FlutterWavePaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(BitcoinPaygatePaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PBSPaymentGatewayAmex.GATEWAY_TYPE);
        directAPIGateways.add(PayClubPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(TojikaPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(KortaPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(DusPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ClearSettleHPPGateway.GATEWAY_TYPE);
        directAPIGateways.add(SafexPayPaymentGateway.GATEWAY_TYPE);

        directAPIGateways.add(NPayOnGateway.GATEWAY_TYPE);
        directAPIGateways.add(EPaySolutionGateway.GATEWAY_TYPE);
        directAPIGateways.add(iMerchantPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(NestPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ZotapayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(TransactiumPaymentGateway.GATEWAY_TYPE_TRX);
        directAPIGateways.add(SabadellPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(NPaySecPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(CardPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(TransactiumPaymentGateway.GATEWAY_TYPE_TRANSACTIUM_1);
        directAPIGateways.add(WLPaymentGateway.GATEWAY_TYPE_AGNIPAY);
        directAPIGateways.add(WLPaymentGateway.GATEWAY_TYPE_TRANSACTWORLD);
        directAPIGateways.add(WLPaymentGateway.GATEWAY_TYPE_SHIMOTOMO);
        directAPIGateways.add(WLPaymentGateway.GATEWAY_TYPE_SAMPLEPSP);
        directAPIGateways.add(WLPaymentGateway.GATEWAY_TYPE_FIDOMS);
        directAPIGateways.add(AwepayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(AllPay88PaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(CyberSourcePaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayVaultProPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(RomCardPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ElegroPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(BRDPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(QuickCardPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(OneRoadPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ICardPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PaySendPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(NexiPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(DusPayCardPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(DectaNewPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PaySendWalletPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(NinjaWalletPaymentGateway.GATEWAY_TYPE);
        //directAPIGateways.add(ApsPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(VoguePayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(OnlinePayPaymentGateway.GATEWAY_TYPE);
        //directAPIGateways.add(ReitumuBankMerchantPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayeezyPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PhoneixPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayvisionV2PaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayBoutiquePaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(TrueVoPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(XPatePaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(AcqraPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(CuroPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(BhartiPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(FlutterWaveBarterPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ZotaPayGateway.GATEWAY_TYPE);
        directAPIGateways.add(KCPPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(EMerchantPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(EzPayNowPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(JetonCardPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(IlixiumPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ECPCPPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(JPBTPaymentGateway.GATEWAY_TYPE);
        //directAPIGateways.add(TapMioPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayonOppwaPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(EcommpayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(CBCGPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(SafeChargeV2PaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(Triple000PaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(FenigePaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ZhiXinfuPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(AlweavePaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(AtCyberSourcePaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(OctapayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(TWDTaiwanPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(TrustPayOppwaPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(AtCellulantPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(QikpayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(LetzPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(GiftPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(QuickPaymentsGateway.GATEWAY_TYPE);
        directAPIGateways.add(BennupayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayEasyWorldPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(OculusPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(EasyPaymentzPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(IMoneyPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(AsianCheckoutPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(AppleTreeCellulantPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(GlobalGatePaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayUPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ApcoFastPayV6PaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(GpayPaymentzGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayGPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ApexPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(AirpayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ClevelandPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(QikPayV2PaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(CashFlowsCaiboPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(EasyPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(TigerPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(BoltPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(CajaRuralPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(TigerGateWayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(EuroEximBankPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(AamarPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(OmnipayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(BoomBillPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PowerCash21PaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayTMPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(EaseBuzzPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(AirtelUgandaPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(MTNUgandaPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(HDFCPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(ContinentPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(TransfrPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(BitClearPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(XceptsPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(BnmQuickPaymentGateway.GATEWAY_TYPE);
    //    directAPIGateways.add(FidyPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(FlexepinPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(SmartCodePayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(SmartFastPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(InfiPayPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(TRAXXPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(PayGSmilePaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(SwiffyPaymentGateway.GATEWAY_TYPE);
        directAPIGateways.add(AlphapayPaymentGateway.GATEWAY_TYPE);

        return directAPIGateways.contains(fromtype);
    }

    public static boolean checkCommonProcessGateways(String fromtype)
    {
        transactionLogger.error("Error fromtype----" + fromtype);
        ArrayList<String> commProcessGateways = new ArrayList<String>();

        commProcessGateways.add(Gold24PaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayVisionPaymentGateway.GATEWAY_TYPE);
        //commProcessGateways.add(Pay132PaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(DdpPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayWorldPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(CredoraxPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(UGSPaymentGateway.GATEWAY_TYPE_FORT);
        commProcessGateways.add(UGSPaymentGateway.GATEWAY_TYPE_UGS);
        commProcessGateways.add(SwiffpayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayOnGateway.GATEWAY_TYPE_PAYON);
        commProcessGateways.add(PayOnGateway.GATEWAY_TYPE_CATELLA);
        commProcessGateways.add(WSecPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(EuroPayPaymentGateway.GATEWAY_TYPE);
        //  commProcessGateways.add(WireCardPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(BorgunPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(NMIPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayWorldPaymentGateway.GATEWAY_TYPE_RITUMU);
        commProcessGateways.add(DeltaPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(FrickBankPaymentGateWay.GATEWAY_TYPE);
        commProcessGateways.add(LpbPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(SafeChargePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(GuardianPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(LibertyPaymentGateway.GATEWAY_TYPE);
        //commProcessGateways.add(IPAYDNAPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(FluzznetworkPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PaygatewayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ArenaPlusPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(AlliedWalletPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayforasiaPaymentGateway.GATEWAY_TYPE);
        //commProcessGateways.add(AtlasPaymentPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(DVGPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(SBMPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PfsPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(CashFlowPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PaspxPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(STSPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(EmaxHighRiskPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ReitumuBankSMSPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ReitumuBankMerchantPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ProcessingPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(TwoGatePayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(OPXPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(VisaNetPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayDollarPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ProcesosMCPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(INICICIPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayHostPayGatePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(AuxPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(LibillPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(BeekashPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(P4CreditCardPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(P4PaymentGateway.GATEWAY_TYPE);
        //commProcessGateways.add(P4DirectDebitPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PaySecPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayMitcoPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(TrustsPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PbsPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(WonderlandPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ApcoPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(TrustPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ClearSettlePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(B4PaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(WireCardNPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(KotakAllPayCardPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(SkrillPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(DectaSMSPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(DectaMerchantPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PerfectMoneyPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(VoucherMoneyPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PaySpacePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(UPayGatePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(BillDeskPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(QpPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(JetonPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(JetonVoucherPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(BankonePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ClearSettleVoucherPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(EpayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(RavePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(TrustlyPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(GCPPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayneticsGateway.GATEWAY_TYPE);
        commProcessGateways.add(EmexpayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(EMSPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(TransactiumPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(CardinityPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ApcoFastpayPaymentGateway.GATEWAY_TYPE_APC0_ALDRAPAY);
        commProcessGateways.add(ApcoFastpayPaymentGateway.GATEWAY_TYPE_APCO_PURPLEPAY);
        commProcessGateways.add(ApcoFastpayPaymentGateway.GATEWAY_TYPE_APCO_RAVE);
        commProcessGateways.add(ApcoFastpayPaymentGateway.GATEWAY_TYPE_APCO_FAST_PAY);
        commProcessGateways.add(RSPPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(SecureTradingGateway.GATEWAY_TYPE);
        commProcessGateways.add(ECPPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(DusPayDDPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(UnionPayInternationalPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(FlutterWavePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(BitcoinPaygatePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PBSPaymentGatewayAmex.GATEWAY_TYPE);
        commProcessGateways.add(PayClubPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(TojikaPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(KortaPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(DusPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ClearSettleHPPGateway.GATEWAY_TYPE);
        commProcessGateways.add(SafexPayPaymentGateway.GATEWAY_TYPE);

        commProcessGateways.add(NPayOnGateway.GATEWAY_TYPE);
        commProcessGateways.add(EPaySolutionGateway.GATEWAY_TYPE);
        commProcessGateways.add(iMerchantPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(NestPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ZotapayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(TransactiumPaymentGateway.GATEWAY_TYPE_TRX);
        commProcessGateways.add(SabadellPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(NPaySecPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(CardPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(TransactiumPaymentGateway.GATEWAY_TYPE_TRANSACTIUM_1);
        commProcessGateways.add(WLPaymentGateway.GATEWAY_TYPE_AGNIPAY);
        commProcessGateways.add(WLPaymentGateway.GATEWAY_TYPE_TRANSACTWORLD);
        commProcessGateways.add(WLPaymentGateway.GATEWAY_TYPE_SHIMOTOMO);
        commProcessGateways.add(WLPaymentGateway.GATEWAY_TYPE_SAMPLEPSP);
        commProcessGateways.add(WLPaymentGateway.GATEWAY_TYPE_FIDOMS);
        commProcessGateways.add(AwepayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(AllPay88PaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(CyberSourcePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayVaultProPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(RomCardPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ElegroPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(BRDPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(QuickCardPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(OneRoadPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ICardPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PaySendPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(NexiPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(DectaNewPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(DusPayCardPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PaySendWalletPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(NinjaWalletPaymentGateway.GATEWAY_TYPE);
        //commProcessGateways.add(ApsPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(VoguePayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(OnlinePayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayeezyPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PhoneixPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayvisionV2PaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayBoutiquePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(TrueVoPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(XPatePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(AcqraPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(CuroPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(BhartiPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(FlutterWaveBarterPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ZotaPayGateway.GATEWAY_TYPE);
        commProcessGateways.add(KCPPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(EMerchantPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(JetonCardPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(IlixiumPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ECPCPPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(JPBTPaymentGateway.GATEWAY_TYPE);
        // commProcessGateways.add(TapMioPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayonOppwaPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(EcommpayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(TotalPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(KnoxPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(CBCGPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(SafeChargeV2PaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(Triple000PaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(FenigePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ZhiXinfuPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(AtCyberSourcePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(OctapayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(TWDTaiwanPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(TrustPayOppwaPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(AtCellulantPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(QikpayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(LetzPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(GiftPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(QuickPaymentsGateway.GATEWAY_TYPE);
        commProcessGateways.add(BennupayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayneteasyGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayEasyWorldPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(OculusPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(AsianCheckoutPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(EasyPaymentzPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(IMoneyPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(WealthPayGateway.GATEWAY_TYPE);
        commProcessGateways.add(AppleTreeCellulantPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(GlobalGatePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayUPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(CashFreePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ApcoFastPayV6PaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(GpayPaymentzGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayGPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ApexPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(AirpayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ClevelandPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(QikPayV2PaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(CashFlowsCaiboPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(EasyPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(TigerPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(BoltPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(CajaRuralPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(TigerGateWayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(EuroEximBankPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(AamarPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(OmnipayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(BoomBillPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PowerCash21PaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayTMPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(AirtelUgandaPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(MTNUgandaPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(HDFCPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(LyraPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(UBAMCPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(EaseBuzzPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ContinentPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(TransfrPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(BitClearPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(XceptsPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(BnmQuickPaymentGateway.GATEWAY_TYPE);
    //    commProcessGateways.add(FidyPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(FlexepinPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(SmartCodePayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(SmartFastPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(InfiPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(TRAXXPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayGSmilePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(SwiffyPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(AlphapayPaymentGateway.GATEWAY_TYPE);

        return commProcessGateways.contains(fromtype);
    }

    public static boolean checkProcessGateways(String fromtype)
    {
        ArrayList<String> commProcessGateways = new ArrayList<String>();
        commProcessGateways.add(QwipiPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(EcorePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(Gold24PaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayVisionPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(Pay132PaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(DdpPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayWorldPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(CredoraxPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(UGSPaymentGateway.GATEWAY_TYPE_FORT);
        commProcessGateways.add(UGSPaymentGateway.GATEWAY_TYPE_UGS);
        commProcessGateways.add(SwiffpayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayOnGateway.GATEWAY_TYPE_PAYON);
        commProcessGateways.add(PayOnGateway.GATEWAY_TYPE_CATELLA);
        commProcessGateways.add(WSecPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(EuroPayPaymentGateway.GATEWAY_TYPE);
        // commProcessGateways.add(WireCardPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(BorgunPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(NMIPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayWorldPaymentGateway.GATEWAY_TYPE_RITUMU);
        commProcessGateways.add(DeltaPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(FrickBankPaymentGateWay.GATEWAY_TYPE);
        commProcessGateways.add(SafeChargePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(GuardianPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(LibertyPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(IPAYDNAPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(FluzznetworkPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PaygatewayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ArenaPlusPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(AlliedWalletPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayforasiaPaymentGateway.GATEWAY_TYPE);
        //commProcessGateways.add(AtlasPaymentPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(DVGPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(SBMPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PfsPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(CashFlowPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PaspxPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(STSPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(EmaxHighRiskPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ReitumuBankSMSPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ReitumuBankMerchantPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ProcessingPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(TwoGatePayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(OPXPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(VisaNetPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(LpbPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayDollarPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ProcesosMCPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(INICICIPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayHostPayGatePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(AuxPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(LibillPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(BeekashPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayMitcoPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(P4CreditCardPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(P4PaymentGateway.GATEWAY_TYPE);
        //commProcessGateways.add(P4DirectDebitPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PaySecPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(TrustsPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PbsPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(WonderlandPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ApcoPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(TrustPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ClearSettlePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(B4PaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(WireCardNPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(KotakAllPayCardPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(SkrillPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(DectaSMSPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(DectaMerchantPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PerfectMoneyPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(VoucherMoneyPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PaySpacePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(UPayGatePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(BillDeskPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(QpPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(JetonPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(JetonVoucherPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(BankonePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ClearSettleVoucherPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(EpayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(RavePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(TrustlyPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(GCPPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayneticsGateway.GATEWAY_TYPE);
        commProcessGateways.add(EmexpayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(EMSPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(UnicreditPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(TransactiumPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(CardinityPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ApcoFastpayPaymentGateway.GATEWAY_TYPE_APC0_ALDRAPAY);
        commProcessGateways.add(ApcoFastpayPaymentGateway.GATEWAY_TYPE_APCO_PURPLEPAY);
        commProcessGateways.add(ApcoFastpayPaymentGateway.GATEWAY_TYPE_APCO_RAVE);
        commProcessGateways.add(ApcoFastpayPaymentGateway.GATEWAY_TYPE_APCO_FAST_PAY);
        commProcessGateways.add(RSPPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(SecureTradingGateway.GATEWAY_TYPE);
        commProcessGateways.add(ECPPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(DusPayDDPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(UnionPayInternationalPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(FlutterWavePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(BitcoinPaygatePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PBSPaymentGatewayAmex.GATEWAY_TYPE);
        commProcessGateways.add(PayClubPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(TojikaPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(KortaPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(DusPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ClearSettleHPPGateway.GATEWAY_TYPE);
        commProcessGateways.add(SafexPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(NPayOnGateway.GATEWAY_TYPE);
        commProcessGateways.add(EPaySolutionGateway.GATEWAY_TYPE);
        commProcessGateways.add(iMerchantPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(NestPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ZotapayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(TransactiumPaymentGateway.GATEWAY_TYPE_TRX);
        commProcessGateways.add(SabadellPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(NPaySecPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(CardPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(TransactiumPaymentGateway.GATEWAY_TYPE_TRANSACTIUM_1);
        commProcessGateways.add(WLPaymentGateway.GATEWAY_TYPE_AGNIPAY);
        commProcessGateways.add(WLPaymentGateway.GATEWAY_TYPE_TRANSACTWORLD);
        commProcessGateways.add(WLPaymentGateway.GATEWAY_TYPE_SHIMOTOMO);
        commProcessGateways.add(WLPaymentGateway.GATEWAY_TYPE_SAMPLEPSP);
        commProcessGateways.add(WLPaymentGateway.GATEWAY_TYPE_FIDOMS);
        commProcessGateways.add(AwepayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(AllPay88PaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(CyberSourcePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayVaultProPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(RomCardPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ElegroPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(BRDPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(QuickCardPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(OneRoadPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ICardPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PaySendPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(NexiPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(DectaNewPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(DusPayCardPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PaySendWalletPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(NinjaWalletPaymentGateway.GATEWAY_TYPE);
        //commProcessGateways.add(ApsPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(VoguePayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(OnlinePayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayeezyPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PhoneixPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayvisionV2PaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayBoutiquePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(TrueVoPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(XPatePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(AcqraPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(BhartiPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(FlutterWaveBarterPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ZotaPayGateway.GATEWAY_TYPE);
        commProcessGateways.add(EMerchantPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(JetonCardPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(IlixiumPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ECPCPPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(JPBTPaymentGateway.GATEWAY_TYPE);
        // commProcessGateways.add(TapMioPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayonOppwaPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(EcommpayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(TotalPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(KnoxPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(SafeChargeV2PaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(Triple000PaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(FenigePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ZhiXinfuPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(AtCyberSourcePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(OctapayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(TWDTaiwanPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(TrustPayOppwaPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(AtCellulantPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(QikpayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(LetzPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(GiftPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(QuickPaymentsGateway.GATEWAY_TYPE);
        commProcessGateways.add(BennupayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayneteasyGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayEasyWorldPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(OculusPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(EasyPaymentzPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(IMoneyPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(AsianCheckoutPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(AppleTreeCellulantPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(GlobalGatePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayUPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ApcoFastPayV6PaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(GpayPaymentzGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayGPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ApexPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(AirpayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ClevelandPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(QikPayV2PaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(CashFlowsCaiboPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(EasyPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(TigerPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(BoltPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(CajaRuralPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(TigerGateWayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(EuroEximBankPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(AamarPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(OmnipayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(BoomBillPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PowerCash21PaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayTMPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(AirtelUgandaPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(MTNUgandaPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(HDFCPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(LyraPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(UBAMCPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(EaseBuzzPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(ContinentPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(TransfrPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(BitClearPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(XceptsPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(BnmQuickPaymentGateway.GATEWAY_TYPE);
     //   commProcessGateways.add(FidyPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(FlexepinPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(SmartCodePayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(SmartFastPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(InfiPayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(TRAXXPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(PayGSmilePaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(AlphapayPaymentGateway.GATEWAY_TYPE);
        commProcessGateways.add(SwiffyPaymentGateway.GATEWAY_TYPE);

        return commProcessGateways.contains(fromtype);
    }

    public static String getFirstSix(String ccpan)
    {

        String firstSix = "";

        int len = ccpan.length();
        if (len == 19)
        {
            firstSix = ccpan.substring(ccpan.length() - 19, ccpan.length() - 13);

        }
        else if (len == 16)
        {
            firstSix = ccpan.substring(ccpan.length() - 16, ccpan.length() - 10);

        }
        else if (len == 15)
        {
            firstSix = ccpan.substring(ccpan.length() - 15, ccpan.length() - 9);

        }
        else if (len == 14)
        {
            firstSix = ccpan.substring(ccpan.length() - 14, ccpan.length() - 8);

        }
        else if (len == 13)
        {
            firstSix = ccpan.substring(ccpan.length() - 13, ccpan.length() - 7);

        }
        else if (len == 12)
        {
            firstSix = ccpan.substring(ccpan.length() - 12, ccpan.length() - 6);

        }
        return firstSix;
    }

    public static String getLastFour(String ccpan)
    {
        String lastfour = "";
        int len = ccpan.length();
        if (len == 19)
        {

            lastfour = ccpan.substring(ccpan.length() - 4);
        }

        if (len == 16)
        {

            lastfour = ccpan.substring(ccpan.length() - 4);
        }
        else if (len == 15)
        {

            lastfour = ccpan.substring(ccpan.length() - 4);
        }
        else if (len == 14)
        {

            lastfour = ccpan.substring(ccpan.length() - 4);
        }
        else if (len == 13)
        {

            lastfour = ccpan.substring(ccpan.length() - 4);
        }
        else if (len == 12)
        {

            lastfour = ccpan.substring(ccpan.length() - 4);
        }
        return lastfour;
    }

    public static long DATEDIFF(String date1, String date2)
    {
        long MILLISECS_PER_DAY = 24 * 60 * 60 * 1000;
        long days = 0l;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // "dd/MM/yyyy HH:mm:ss");
        Date dateIni = null;
        Date dateFin = null;
        try
        {
            dateIni = format.parse(date1);
            dateFin = format.parse(date2);
            days = (dateFin.getTime() - dateIni.getTime()) / MILLISECS_PER_DAY;
        }
        catch (Exception e)
        {
            log.error("Exception---->", e);
        }
        return days;
    }

    public static boolean isValidDate(String date)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        sdf.setLenient(false);

        try
        {

            //if not valid, it will throw ParseException
            Date d = sdf.parse(date);
        }
        catch (ParseException e)
        {
            log.error("ParseException---->", e);
            return false;
        }
        return true;
    }

    public static boolean isValidDateNew(String date)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        sdf.setLenient(false);
        Functions functions = new Functions();
        try
        {
            if (functions.isValueNull(date))
            {
                //if not valid, it will throw ParseException
                Date d = sdf.parse(date);
            }
        }
        catch (ParseException e)
        {
            log.error("ParseException---->", e);
            return false;
        }
        return true;
    }

    public static boolean isValidDatePicker(String date)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
        sdf.setLenient(false);
        Functions functions = new Functions();
        try
        {
            if (functions.isValueNull(date))
            {
                //if not valid, it will throw ParseException
                Date d = sdf.parse(date);
            }
        }
        catch (ParseException e)
        {
            log.error("ParseException---->", e);
            return false;
        }
        return true;
    }

    public static long getDiffBetweenTwoCalander(Calendar cal1, Calendar cal2)
    {
        //log.debug("Call1::"+cal1.getTime().toString()+" cal1 in milli::"+cal1.getTimeInMillis()+" cal2::"+cal2.getTime().toString()+" call2 in milli::"+cal2.getTimeInMillis());
        long timeDifferenceMillis;
        if (cal1.getTimeInMillis() >= cal2.getTimeInMillis())
        {
            timeDifferenceMillis = cal1.getTimeInMillis() - cal2.getTimeInMillis();
        }
        else
        {
            timeDifferenceMillis = cal2.getTimeInMillis() - cal1.getTimeInMillis();
        }

        //log.debug("difftimemilis::"+timeDifferenceMillis);

        long timeInMinutes = TimeUnit.MILLISECONDS.toSeconds(timeDifferenceMillis);
        //log.debug("timedifference::"+timeInMinutes);
        return timeInMinutes;
    }

    public static String convert2Decimal(String strAmount) //sandip
    {
        BigDecimal amount = new BigDecimal(strAmount);
        amount = amount.setScale(2, BigDecimal.ROUND_DOWN); //as amount value in database was round down by mysql while inserting in php page
        return amount.toString();

    }

    public static String convert2Decimal(Double amount)
    {
        BigDecimal value = new BigDecimal(amount);
        String strAmount = String.valueOf(value.setScale(2, BigDecimal.ROUND_DOWN));
        return strAmount;
    }

    public static String convertDateDBFormat(Date date)//sandip
    {
        String value = null;
        SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            value = dbDateFormat.format(date);
        }
        catch (Exception e)
        {
            log.error("Date Parsing Exception::::::" + e.getMessage());
        }
        return value;
    }

    public static String round(double value, int places)
    {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_DOWN);
        return bd.toString();
    }

    public static double roundDBL(double value, int places)
    {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_DOWN);
        return bd.doubleValue();
    }

    public static String combovalForSplitPayment(String value)
    {
        String str = "";
        if (value != null && value.equals("Terminal"))
        {
            str = str + "<option value=" + value + " selected=\"selected\">" + value + "</option>";
            str = str + "<option value=\"Merchant\">Merchant</option>";
        }
        else
        {
            str = str + "<option value=\"Terminal\">Terminal</option>";
            str = str + "<option value=" + value + " selected=\"selected\">" + value + "</option>";
        }
        return str;
    }

    /*public static String combovalForCardWhitelistLevel(String value)
    {
        String str = "";
        if (value != null && value.equals("Member"))
        {
            str = str + "<option value=" + value + " selected=\"selected\">" + value + "</option>";
            str = str + "<option value=\"System\">System</option>";
        }
        else
        {
            str = str + "<option value=\"Member\">Member</option>";
            str = str + "<option value=" + value + " selected=\"selected\">" + value + "</option>";
        }
        return str;
    }*/
    public static String combovalForCardWhitelistLevel(String value)
    {
        String str = "";
        if (value != null && value.equals("Member"))
        {
            str = str + "<option value=" + value + " selected=\"selected\">" + value + "</option>";
            str = str + "<option value=\"Account\">Account</option>";
            str = str + "<option value=\"Group\">Group</option>";
        }
        else if (value != null && value.equals("Account"))
        {
            str = str + "<option value=" + value + " selected=\"selected\">" + value + "</option>";
            str = str + "<option value=\"Member\">Member</option>";
            str = str + "<option value=\"Group\">Group</option>";

        }
        else
        {
            str = str + "<option value=" + value + " selected=\"selected\">" + value + "</option>";
            str = str + "<option value=\"Member\">Member</option>";
            str = str + "<option value=\"Account\">Account</option>";

        }
        return str;
    }

    public static String combovalForRecurring(String value)
    {
        String str = "";
        if (value != null && value.equals("MANUAL"))
        {
            str = str + "<option value=" + value + " selected=\"selected\">" + value + "</option>";
            str = str + "<option value=\"AUTOMATIC\">AUTOMATIC</option>";
        }
        else
        {
            str = str + "<option value=\"MANUAL\">MANUAL</option>";
            str = str + "<option value=" + value + " selected=\"selected\">" + value + "</option>";
        }
        return str;
    }

    private static URI getBaseURI()
    {

        return UriBuilder.fromUri("https://service.pz.com").build();

    }

    public static String roundOff(String amount)
    {
        double roundOff = Double.parseDouble(amount);
        double amt = Math.round(roundOff * 100.0) / 100.0;
        DecimalFormat df = new DecimalFormat("0.00##");
        amount = df.format(amt);
        return amount;
    }

    public String getIPCountryShort(String ipAddress)
    {
        String countryShort = "";
        try
        {
            IP2Location loc = new IP2Location();
            String binPath = ApplicationProperties.getProperty("BINPATH");
            String licensePath = ApplicationProperties.getProperty("LICENSEPATH");
            loc.IPDatabasePath = binPath;

            loc.IPLicensePath = licensePath;
            IPResult rec = loc.IPQuery(ipAddress);
            countryShort = rec.getCountryShort();
            if ("OK".equals(rec.getStatus()))
            {
                countryShort = rec.getCountryShort();
            }

        }
        catch (IOException e)
        {
            transactionLogger.error("IO Exception for getting Country from IP---", e);
        }
        if (!isValueNull(countryShort))
            countryShort = "";
        return countryShort;
    }

    public String getIPCountryLong(String ipAddress)
    {
        String countryShort = "";
        try
        {
            IP2Location loc = new IP2Location();
            String binPath = ApplicationProperties.getProperty("BINPATH");
            String licensePath = ApplicationProperties.getProperty("LICENSEPATH");
            loc.IPDatabasePath = binPath;

            loc.IPLicensePath = licensePath;
            IPResult rec = loc.IPQuery(ipAddress);
            countryShort = rec.getCountryShort();
            if ("OK".equals(rec.getStatus()))
            {
                countryShort = rec.getCountryLong();
            }

        }
        catch (IOException e)
        {
            transactionLogger.error("IO Exception for getting Country from IP---", e);
        }
        return countryShort;
    }

    public String getIFrameHeaderValue(String refererHeader, String wlDomain)
    {
        transactionLogger.debug("inside getIFrameHeaderValue---" + wlDomain + "---" + refererHeader);
        String headerVal = "";
        if (this.isValueNull(refererHeader) && this.isValueNull(wlDomain))
        {
            if (wlDomain.contains(","))
            {
                Boolean isWhitelistedDomin = false;
                String allowedDomain = "";
                List<String> arrList = Arrays.asList(wlDomain.split(","));
                transactionLogger.debug("arrList from getIFrameHeaderValuea---" + arrList);
                for (String domain : arrList)
                {
                    if (refererHeader.contains(domain))
                    {
                        allowedDomain = domain;
                        isWhitelistedDomin = true;
                        transactionLogger.debug("allowedDomain from getIFrameHeaderValuea---" + allowedDomain);
                        break;
                    }
                }
                if (isWhitelistedDomin)
                {
                    //headerVal = "ALLOW-FROM "+allowedDomain;
                    transactionLogger.debug("X-Frame-Options from isWhitelistedDomin---" + headerVal);
                }
                else
                {
                    headerVal = "SAMEORIGIN";
                }
            }
            else if (refererHeader.contains(wlDomain))
            {
                //headerVal = "ALLOW-FROM "+wlDomain;

                transactionLogger.debug("X-Frame-Options from contains---" + headerVal);
            }
            else
            {
                headerVal = "SAMEORIGIN";
            }

        }
        return headerVal;
    }

    public boolean isDoubleValue(String value)
    {
        boolean result = false;
        String decimalPattern = "([0-9]*)\\.([0-9]*)";
        result = Pattern.matches(decimalPattern, value);
        return result;
    }

    public String splitGatewaySet(String gatewaySet)
    {
        String gatewayID = "";
        if (isValueNull(gatewaySet))
        {
            String aGatewaySet[] = gatewaySet.split("-");
            if (aGatewaySet.length == 3)
            {
                gatewayID = aGatewaySet[2];
            }
        }
        return gatewayID;
    }

    /**
     * This is to generate MD5 Checksum fpr API Based
     *
     * @param toid
     * @param key
     * @return
     * @throws java.security.NoSuchAlgorithmException
     */
    public String generateMD5ChecksumAPIBased(String toid, String key, String random) throws NoSuchAlgorithmException
    {
        String str = toid + "|" + key + "|" + random;
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        return generatedCheckSum;
    }

    public Hashtable getHashValue(String str)
    {
        String str1 = str.substring(1, str.length() - 1);
        String res[] = str1.split(",");
        Hashtable responseHash = new Hashtable();
        for (int i = 0; i < res.length; i++)
        {
            String field = res[i];
            String temp[] = field.split("=");
            if (temp.length >= 2)
            {
                responseHash.put(temp[0].trim(), temp[1].trim());
            }
            else
            {
                responseHash.put(temp[0].trim(), "");
            }
        }
        return responseHash;
    }

    public String validateDate(String newStartD, String newEndD, String previousStartD, String previousEndD)
    {

        String message = null;
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
            Date newStartDate = sdf.parse(newStartD);
            Date newEndDate = sdf.parse(newEndD);
            if (!newStartDate.before(newEndDate))
            {
                return message = "Invalid Date, End Date should not be less then Start Date.";
            }
            if (previousStartD != null && previousEndD != null)
            {
                //new start date-1 = previous end date;
                Date previousStartDate = sdf.parse(previousStartD);
                Date dateBefore = new Date(newStartDate.getTime() - 1 * 24 * 3600 * 1000);
                if (!dateBefore.after(previousStartDate))
                {
                    return message = "Invalid Date, New Start Date Should not be grater then Previous version Start Date. ";
                }
            }
        }
        catch (ParseException e)
        {
            log.error("Date Parsing Exception", e);
        }

        return null;
    }

    public boolean isValueNull(String str)
    {
        if (str != null && !str.equals("null") && !str.equals(""))
        {
            return true;
        }
        return false;
    }

    public boolean isEmptyOrNull(String str)
    {
        if (str == null || str.equals("null") || str.trim().equals(""))
        {
            return true;
        }
        return false;
    }

    public boolean isNumericVal(String data)
    {
        // return data.matches("\\d+");
        return data.matches("^.*\\d$");
    }

    public boolean isNumericVal1(String data) // Used For cardType And PaymentType
    {
        // return data.matches("\\d+");
        return data.matches("^\\S(\\d)*$");
    }

    public boolean isFutureDateComparisonWithCurrentDate(String date, String dateFormate)
    {
        boolean isFutureDate = false;
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormate);
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        Date passedDate = null;
        try
        {
            passedDate = sdf.parse(date);
            if (passedDate.after(currentDate))
            {
                isFutureDate = true;
            }
        }
        catch (ParseException e)
        {
            isFutureDate = false;
        }
        return isFutureDate;
    }

    public boolean isFutureExpMonthYear(String expMonth, String expYear)
    {

        boolean result = false;
        int expMonth1 = Integer.parseInt(expMonth);
        int expYear1 = Integer.parseInt(expYear);

        LocalDateTime now = LocalDateTime.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();

        if (expYear1 == currentYear && expMonth1 >= currentMonth)
        {
            result = true;
        }
        else if (expYear1 > currentYear)
        {
            result = true;
        }
        else
        {
            result = false;
        }
        return result;
    }

    public boolean isFutureDateComparisonWithFromAndToDate(String fromDate, String toDate, String dateFormate)
    {
        boolean isFutureDate = false;
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormate);

        Date passedFromDate = null;
        Date passedToDate = null;
        try
        {
            passedFromDate = sdf.parse(fromDate);
            passedToDate = sdf.parse(toDate);
            if (passedFromDate.after(passedToDate))
            {
                isFutureDate = true;
            }
        }
        catch (ParseException e)
        {
            isFutureDate = false;
        }
        return isFutureDate;
    }

    public boolean isFutureDate(String date, String dateFormat)
    {
        boolean isFutureDate = false;
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        Date passedDate = null;
        try
        {
            passedDate = sdf.parse(date);
            if (passedDate.after(currentDate))
            {
                isFutureDate = true;
            }
        }
        catch (ParseException e)
        {
            isFutureDate = false;
        }
        return isFutureDate;
    }

    public boolean isRandomDate(String date, String dateFormat)
    {
        boolean isRandomDate = true;
        try
        {
            DateFormat df = new SimpleDateFormat(dateFormat);
            df.setLenient(false);
            df.parse(date);
            isRandomDate = false;
        }
        catch (ParseException e)
        {
            isRandomDate = true;
        }
        return isRandomDate;
    }

    public StringBuffer getTableHeaderforMail(String s1, String s2, String s3, String s4, String s5, String s6)
    {
        StringBuffer sHeader = new StringBuffer();

        sHeader.append("<table align=\"center\" cellspacing=\"1\" style=\"width:100%;\" border=\"1\" width=\"400\">");
        sHeader.append("<TR id=\"multipletable\">");
        sHeader.append("<TD valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\">");
        sHeader.append("<b><p align=\"center\"><font color=\"#ffffff\" family=\"Palatino Linotype', 'Book Antiqua, Palatino, serif\" size=\"4px\">" + s1 + "</font></p></b>");
        sHeader.append("</TD>");

        sHeader.append("<TD valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\">");
        sHeader.append("<b><p align=\"center\"><font color=\"#ffffff\" family=\"Palatino Linotype', 'Book Antiqua, Palatino, serif\" size=\"4px\">" + s2 + "</font></p></b>");
        sHeader.append("</TD>");

        sHeader.append("<TD valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\">");
        sHeader.append("<b><p align=\"center\"><font color=\"#ffffff\" family=\"Palatino Linotype', 'Book Antiqua, Palatino, serif\" size=\"4px\">" + s3 + "</font></p></b>");
        sHeader.append("</TD>");

        sHeader.append("<TD valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\">");
        sHeader.append("<b><p align=\"center\"><font color=\"#ffffff\" family=\"Palatino Linotype', 'Book Antiqua, Palatino, serif\" size=\"4px\">" + s4 + "</font></p></b>");
        sHeader.append("</TD>");

        sHeader.append("<TD valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\">");
        sHeader.append("<b><p align=\"center\"><font color=\"#ffffff\" family=\"Palatino Linotype', 'Book Antiqua, Palatino, serif\" size=\"4px\">" + s5 + "</font></p></b>");
        sHeader.append("</TD>");

        sHeader.append("<TD valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\">");
        sHeader.append("<b><p align=\"center\"><font color=\"#ffffff\" family=\"Palatino Linotype', 'Book Antiqua, Palatino, serif\" size=\"4px\">" + s6 + "</font></p></b>");
        sHeader.append("</TD>");

        sHeader.append("</TR>");

        return sHeader;
    }

    public StringBuffer setTableDataforMail(String s1, String s2, String s3, String s4, String s5, String s6, String cValue)
    {
        StringBuffer nTransaction = new StringBuffer();

        String colspan = "";
        String align = "center";
        if (cValue.equalsIgnoreCase("Y"))
        {
            colspan = "colspan=5";
            align = "right";
        }

        nTransaction.append("<TR>");
        if (cValue.equalsIgnoreCase("N"))
        {
            nTransaction.append("<TD " + colspan + ">");
            nTransaction.append("<p align=\"center\" bgcolor=\"\" color=\"#797979\" font-family=\"Helvetica, Arial, Palatino Linotype', 'Book Antiqua, Palatino, serif\" font-size=\"2px\">" + s1 + "</p>");
            nTransaction.append("</TD>");
            nTransaction.append("<TD " + colspan + ">");
            nTransaction.append("<p align=\"center\" bgcolor=\"\" color=\"#797979\" font-family=\"Helvetica, Arial, Palatino Linotype', 'Book Antiqua, Palatino, serif;\" font-size=\"2px\">" + s2 + "</p>");
            nTransaction.append("</TD>");
            nTransaction.append("<TD " + colspan + ">");
            nTransaction.append("<p align=\"center\" bgcolor=\"\" color=\"#797979\" font-family=\"Helvetica, Arial, Palatino Linotype', 'Book Antiqua, Palatino, serif;\" font-size=\"2px\">" + s3 + "</p>");
            nTransaction.append("</TD>");
            nTransaction.append("<TD " + colspan + ">");
            nTransaction.append("<p align=\"center\" bgcolor=\"\" color=\"#797979\" font-family=\"Helvetica, Arial, Palatino Linotype', 'Book Antiqua, Palatino, serif;\" font-size=\"2px\">" + s4 + "</p>");
            nTransaction.append("</TD>");
        }
        nTransaction.append("<TD " + colspan + ">");
        nTransaction.append("<p align=\"center\" bgcolor=\"\" color=\"#797979\" font-family=\"Helvetica, Arial, Palatino Linotype', 'Book Antiqua, Palatino, serif;\" font-size=\"2px\">" + s5 + "</p>");
        nTransaction.append("</TD>");
        nTransaction.append("<TD " + colspan + ">");
        nTransaction.append("<p align=\"center\" bgcolor=\"\" color=\"#797979\" font-family=\"Helvetica, Arial, Palatino Linotype', 'Book Antiqua, Palatino, serif;\" font-size=\"2px\">" + s6 + "</p>");
        nTransaction.append("</TD>");
        nTransaction.append("</TR>");


        return nTransaction;
    }

    /*public BinResponseVO getBinDetails(String firstSix)
    {

        ClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
        Client client = Client.create(config);
        WebResource service = client.resource(getBaseURI());

        BinRequestVO binRequestVO = new BinRequestVO();
        BinResponseVO binResponseVO = new BinResponseVO();

        binRequestVO.setUserid("6");
        binRequestVO.setChecksum("702d60ac4863427b451dc5ab81d8f00b");
        binRequestVO.setRandom("12345");
        binRequestVO.setFirstsix(firstSix);
        *//*String request = ""
                + "firstsix="+firstSix;*//*

        //binResponseVO = service.path("binServices").path("api").path("v1").path("authToken").type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(BinResponseVO.class,binRequestVO);
        binResponseVO = service.path("binServices").path("api").path("v1").path("bindetails").type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(BinResponseVO.class,binRequestVO);
        //System.out.println("binresponse-----"+service.path("binServices").path("api").path("v1").path("bindetails").type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).header("AuthToken", binResponseVO.getAuthToken()).post(String.class, binRequestVO));

        //ToDo - calculate usage type and trans type
        //Usage type
        if (isValueNull(binResponseVO.getCardcategory()))
            binResponseVO.setUsagetype((String) CardUsageType.usageType.get(binResponseVO.getCardcategory()));

        //TranType



        return binResponseVO;
    }*/

    public BinResponseVO getBinDetails(String firstSix) throws PZDBViolationException
    {
        BinVerificationManager binVerificationManager = new BinVerificationManager();
        BinResponseVO binResponseVO = binVerificationManager.getBinDetailsFromFirstSix(firstSix);
        Functions functions = new Functions();
        if (functions.isValueNull(binResponseVO.getCardcategory()))
        {
            binResponseVO.setUsagetype((String) CardUsageType.usageType.get(binResponseVO.getCardcategory()));
        }
        return binResponseVO;
    }


    /*public BinResponseVO getBinDetails(String firstSix, String merchantCountry)
    {
        ClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
        Client client = Client.create(config);
        WebResource service = client.resource(getBaseURI());

        BinRequestVO binRequestVO = new BinRequestVO();
        BinResponseVO binResponseVO = new BinResponseVO();

        binRequestVO.setUserid("6");
        binRequestVO.setChecksum("4bd635afcfaeb3a50490b2ecee6de6a7");
        binRequestVO.setRandom("12345");
        binRequestVO.setFirstsix(firstSix);
        *//*String request = ""
                + "firstsix="+firstSix;*//*

        //binResponseVO = service.path("binServices").path("api").path("v1").path("authToken").type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(BinResponseVO.class,binRequestVO);
        binResponseVO = service.path("binServices").path("api").path("v1").path("bindetails").type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(BinResponseVO.class,binRequestVO);
        //ToDo - calculate usage type and trans type
        //Usage type4
        binResponseVO.setUsagetype((String) CardUsageType.usageType.get(binResponseVO.getCardcategory()));

        Set cName2 = EuCountryCodeA2.euCountryCodeA2.keySet();
        Set cName3 = EuCountryCodeA3.euCountryCodeA3.keySet();
        Set cName = EuCountryCode.euCountryCode;


        //TranType
        if (cName2.contains(binResponseVO.getCountrycodeA2()) || cName3.contains(binResponseVO.getCountrycodeA3()) || cName.contains(binResponseVO.getCountryname()))
        {
            if (binResponseVO.getCountrycodeA2().equals(merchantCountry) || binResponseVO.getCountrycodeA3().equals(merchantCountry) || binResponseVO.getCountryname().equals(merchantCountry))
            {
                binResponseVO.setTranstype("INTRA EURO");
            }
        }
        else if (merchantCountry.equals(binResponseVO.getCountrycodeA2()) || merchantCountry.equals(binResponseVO.getCountrycodeA3()) || merchantCountry.equals(binResponseVO.getCountryname()))
        {
            binResponseVO.setTranstype("DOMESTIC");
        }
        else
        {
            binResponseVO.setTranstype("INTERNATIONAL");
        }


        return binResponseVO;
    }
*/

    public BinResponseVO getBinDetails(String firstSix, String pgTypeCountry) throws PZDBViolationException
    {
        BinVerificationManager binVerificationManager = new BinVerificationManager();
        BinResponseVO binResponseVO = binVerificationManager.getBinDetailsFromFirstSix(firstSix);
        Functions functions = new Functions();
        if (functions.isValueNull(binResponseVO.getCardcategory()))
        {
            binResponseVO.setUsagetype((String) CardUsageType.usageType.get(binResponseVO.getCardcategory()));
        }
        Set cName2 = EuCountryCodeA2.euCountryCodeA2.keySet();
        Set cName3 = EuCountryCodeA3.euCountryCodeA3.keySet();
        Set cName = EuCountryCode.euCountryCode;

        //TranType
        /*if (cName2.contains(binResponseVO.getCountrycodeA2()) || cName3.contains(binResponseVO.getCountrycodeA3()) || cName.contains(binResponseVO.getCountryname()))
        {
            if (binResponseVO.getCountrycodeA2().equals(merchantCountry) || binResponseVO.getCountrycodeA3().equals(merchantCountry) || binResponseVO.getCountryname().equals(merchantCountry))
            {
                binResponseVO.setTranstype("INTRA EURO");
            }
        }
        else if (merchantCountry.equals(binResponseVO.getCountrycodeA2()) || merchantCountry.equals(binResponseVO.getCountrycodeA3()) || merchantCountry.equals(binResponseVO.getCountryname()))
        {
            binResponseVO.setTranstype("DOMESTIC");
        }
        else
        {
            binResponseVO.setTranstype("INTERNATIONAL");
        }*/
        if (functions.isValueNull(pgTypeCountry))
        {
            if (((cName2.contains(binResponseVO.getCountrycodeA2()) || cName3.contains(binResponseVO.getCountrycodeA3())) && cName3.contains(pgTypeCountry)) || binResponseVO.getCountrycodeA3().equalsIgnoreCase(pgTypeCountry))
            {
                binResponseVO.setTranstype("DOMESTIC");
            }
            else
            {
                binResponseVO.setTranstype("INTERNATIONAL");
            }
        }
        else
        {
            binResponseVO.setTranstype("INTERNATIONAL");
        }
        return binResponseVO;
    }

    public String getGatewayName(String gatewayString)
    {
        String gatewayName = "";
        Functions functions = new Functions();
        if (functions.isValueNull(gatewayString))
        {
            String aGatewaySet[] = gatewayString.split("-");
            if (aGatewaySet.length == 2)
            {
                gatewayName = aGatewaySet[0];
            }
        }
        return gatewayName;
    }

    public String getCurrency(String gatewayString)
    {
        String currency = "";
        Functions functions = new Functions();
        if (functions.isValueNull(gatewayString))
        {
            String aGatewaySet[] = gatewayString.split("-");
            if (aGatewaySet.length == 2)
            {
                currency = aGatewaySet[1];
            }
        }
        return currency;
    }

    public String getAutoGeneratedOrderId(String memberid)
    {
        Connection connection = null;
        PreparedStatement p = null;
        ResultSet rs = null;

        String orderId = "";
        try
        {
            connection = Database.getConnection();
            //connection=Database.getRDBConnection();
            String query = "SELECT order_seq(" + memberid + ") as orderid";
            p = connection.prepareStatement(query);
            rs = p.executeQuery();

            if (rs.next())
            {
                orderId = rs.getString("orderid");
            }
            log.debug("order query----" + p);

        }
        catch (SQLException e)
        {
            log.error("SQLException", e);
        }
        catch (SystemError e)
        {
            log.error("SystemError", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p);
            Database.closeConnection(connection);
        }
        return orderId;
    }

    public String getNameMasking(String name)
    {
        String maskingname = "";
        int len = name.length();
        boolean isSpace = false;
        for (int i = 0; i < len; i++)
        {
            if (name.charAt(i) == ' ')
            {
                isSpace = true;
            }
        }
        if (!isSpace)
        {
            maskingname = "";
            for (int i = 0; i < len; i++)
            {
                if (i < len - 2)
                {
                    maskingname += "x";
                }
                else
                {
                    maskingname += name.charAt(i);
                }
            }
        }
        if (len < 4)
        {
            maskingname = "";
            for (int i = 0; i < len; i++)
            {
                if (i < len - 1)
                {
                    maskingname += "x";
                }
                else
                {
                    maskingname += name.charAt(i);
                }
            }
        }
        if (isSpace)
        {
            maskingname = "";
            for (int i = 0; i < len; i++)
            {

                if (i > name.indexOf(' ') && i < len - 2)
                {
                    maskingname += "x";
                }
                else if (i < name.indexOf(' ') - 2 && i < name.indexOf(' '))
                {
                    maskingname += "x";
                }
                else
                {
                    maskingname += name.charAt(i);
                }
            }
        }
        return maskingname;
    }

    public String getEmailMasking(String email)
    {
        String maskingemail = "";
        for (int i = 0; i < email.length(); i++)
        {
            if (i > 1 && i < email.indexOf("@"))
            {
                if (email.charAt(i) != '.' && email.charAt(i) != '-' && email.charAt(i) != '_')
                {
                    maskingemail += "x";
                }
                else
                {
                    maskingemail += email.charAt(i);
                }
            }
            else
            {
                maskingemail += email.charAt(i);
            }
        }
        return maskingemail;
    }

    public String getPhoneNumMasking(String phoneNum)
    {
        String maskingphoneNum = "";
        for (int i = 0; i < phoneNum.length(); i++)
        {
            if (i < (phoneNum.length() - 3))
            {
                maskingphoneNum += "x";
            }
            else
            {
                maskingphoneNum += phoneNum.charAt(i);
            }
        }
        return maskingphoneNum;
    }

    public String maskVpaAddress(String vpaAddress)
    {
        String maskingVpa = "";
        for (int i = 0; i < vpaAddress.length(); i++)
        {
            if (i > 2 && i < vpaAddress.indexOf("@") && vpaAddress.contains("@"))
            {
                if (vpaAddress.charAt(i) != '.' || vpaAddress.charAt(i) != '-' || vpaAddress.charAt(i) != '_')
                {
                    maskingVpa += "x";
                }
                else
                {
                    maskingVpa += vpaAddress.charAt(i);
                }
            }
            else
            {
                maskingVpa += vpaAddress.charAt(i);
            }
        }
//        log.error("maskingVpa========>"+maskingVpa);
        return maskingVpa;
    }

    private static String hmacSha(String KEY, String VALUE, String SHA_TYPE)
    {
        try
        {

            SecretKeySpec signingKey = new SecretKeySpec(KEY.getBytes("UTF-8"), SHA_TYPE);
            Mac mac = Mac.getInstance(SHA_TYPE);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(VALUE.getBytes("UTF-8"));

            byte[] hexArray = {
                    (byte) '0', (byte) '1', (byte) '2', (byte) '3',
                    (byte) '4', (byte) '5', (byte) '6', (byte) '7',
                    (byte) '8', (byte) '9', (byte) 'a', (byte) 'b',
                    (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f'
            };
            byte[] hexChars = new byte[rawHmac.length * 2];
            for (int j = 0; j < rawHmac.length; j++)
            {
                int v = rawHmac[j] & 0xFF;
                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];
            }
            return new String(hexChars);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    public static String pack(String hex)
    {
        String input = hex.length() % 2 == 0 ? hex : hex + "0";
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < input.length(); i += 2)
        {
            String str = input.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }

    String randomUUID(int length, int spacing)
    {
        StringBuilder sb = new StringBuilder();
        int spacer = 0;
        while (length > 0)
        {
            if (spacer == spacing)
            {
                //sb.append(spacerChar);
                spacer = 0;
            }
            length--;
            spacer++;
            sb.append(randomChar());
        }
        return sb.toString();
    }

    char randomChar()
    {
        return ALPHABET.charAt(rng.nextInt(ALPHABET.length()));
    }

    public void setMerchantNotification(TransactionDetailsVO transactionDetailsVO, String trackingid, String status, String resMessage)
    {
        HttpPost post           = null;
        JSONObject jo           = new JSONObject();
        ErrorCodeVO errorCodeVO         = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils   = new ErrorCodeUtils();
        String errorCode            = "";
        String errorDesc            = "";
        String transactionStatus    = "";
        Transaction transaction     = new Transaction();
        String checkSum             = "";
        DateFormat dateFormat       = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date                   = new Date();
        TransactionManager transactionManager = new TransactionManager();
        // putting data to JSONObject
        try
        {
            transactionLogger.error("MerchantNotificationUrl >>>>>>>>>>> " + trackingid + " " + transactionDetailsVO.getMerchantNotificationUrl());
            transactionLogger.error("TransactionNotificationUrl >>>>>>>>>>> " + trackingid + " " + transactionDetailsVO.getNotificationUrl());
            //Success Transactions
            if (status.equalsIgnoreCase(PZShortResponseStatus.SUCCESS.name()) || status.equalsIgnoreCase(PZShortResponseStatus.authsuccessful.name()) || status.equalsIgnoreCase(PZShortResponseStatus.capturesuccess.name()) || status.equalsIgnoreCase(PZShortResponseStatus.setteled.name()) || status.equalsIgnoreCase(PZShortResponseStatus.settled.name()) || status.equalsIgnoreCase(PZShortResponseStatus.markedforreversal.name()) || status.equalsIgnoreCase(PZShortResponseStatus.reversed.name()) || status.equalsIgnoreCase(PZShortResponseStatus.chargeback.name()) || status.equalsIgnoreCase(PZShortResponseStatus.payoutsuccessful.name()) || status.equalsIgnoreCase(PZShortResponseStatus.success.name()))
            {
                //errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_RECORD_FOUND);
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.TRANSACTION_SUCCEED);
                errorCode = errorCodeVO.getApiCode();
                errorDesc = errorCodeVO.getApiDescription();
                transactionStatus = PZShortResponseStatus.SUCCESS.toString();
            }
            //Failed Transactions
            else if (status.equalsIgnoreCase(PZShortResponseStatus.FAILED.name()) || status.equalsIgnoreCase(PZShortResponseStatus.authfailed.name()) || status.equalsIgnoreCase(PZShortResponseStatus.capturefailed.name()) || status.equalsIgnoreCase(PZShortResponseStatus.payoutfailed.name()) || status.equalsIgnoreCase(PZShortResponseStatus.failed.name()))
            {
                //errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_RECORD_FOUND);
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.TRANSACTION_REJECTED);
                errorCode = errorCodeVO.getApiCode();
                errorDesc = errorCodeVO.getApiDescription();
                transactionStatus = PZShortResponseStatus.FAILED.toString();
            }
            //Pending Transaction
            else if (status.equalsIgnoreCase(PZShortResponseStatus.begun.name()) || status.equalsIgnoreCase(PZShortResponseStatus.PARTIAL_SUCCESS.name()) || status.equalsIgnoreCase(PZShortResponseStatus.authstarted.name()) || status.equalsIgnoreCase(PZShortResponseStatus.authcancelled.name()) || status.equalsIgnoreCase(PZShortResponseStatus.capturestarted.name()) || status.equalsIgnoreCase(PZShortResponseStatus.payoutstarted.name()))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SUCCESSFULL_PENDING_TRANSACTION);
                errorCode = errorCodeVO.getApiCode();
                errorDesc = errorCodeVO.getApiDescription();
                transactionStatus = PZShortResponseStatus.begun.toString();
            }
            //Record Not Found
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.REJECTED_RECORD_NOT_FOUND);
                errorCode = errorCodeVO.getApiCode();
                errorDesc = errorCodeVO.getApiDescription();
                transactionStatus = PZShortResponseStatus.FAILED.toString();
            }

            try
            {
                String amount = transactionDetailsVO.getAmount();
                if ("JPY".equalsIgnoreCase(transactionDetailsVO.getCurrency()))
                {
                    amount = Functions.getJPYAmount(transactionDetailsVO.getAmount());
                }
                checkSum = Checksum.generateChecksumForStandardKit(trackingid, transactionDetailsVO.getDescription(), amount, transactionStatus, transactionDetailsVO.getSecretKey());
            }
            catch (NoSuchAlgorithmException e)
            {
                transactionLogger.error("NoSuchAlgorithmException while checksum in sending notification---", e);
            }

            jo.put("paymentId", trackingid);
            jo.put("status", status);
            jo.put("transactionStatus", transactionStatus);
            jo.put("paymentBrand", transaction.getPaymentBrandForRest(transactionDetailsVO.getCardTypeId()));
            jo.put("paymentMode", transaction.getPaymentModeForRest(transactionDetailsVO.getPaymodeId()));
            jo.put("firstName", transactionDetailsVO.getFirstName());
            jo.put("lastName", transactionDetailsVO.getLastName());
            jo.put("amount", transactionDetailsVO.getAmount());
            jo.put("currency", transactionDetailsVO.getCurrency());
            jo.put("descriptor", transactionDetailsVO.getBillingDesc());
            jo.put("merchantTransactionId", transactionDetailsVO.getDescription());
            jo.put("remark", resMessage);
            jo.put("tmpl_amount", transactionDetailsVO.getTemplateamount());
            jo.put("tmpl_currency", transactionDetailsVO.getTemplatecurrency());
            jo.put("eci", transactionDetailsVO.getEci());
            jo.put("checksum", checkSum);

            Map m = new LinkedHashMap();
            m.put("code", errorCode);
            m.put("description", errorDesc);
            if (isValueNull(transactionDetailsVO.getBankCode()) && isValueNull(transactionDetailsVO.getBankDescription()))
            {
                m.put("bankCode", transactionDetailsVO.getBankCode());
                m.put("bankDescription", transactionDetailsVO.getBankDescription());
            }
            jo.put("result", m);

            m = new LinkedHashMap();
            m.put("email", transactionDetailsVO.getEmailaddr());
            if (isValueNull(transactionDetailsVO.getCustomerId()))
                m.put("id", transactionDetailsVO.getCustomerId());
            else
                m.put("id", "");
            jo.put("customer", m);
            Map cardDetails = new LinkedHashMap();
            if (isValueNull(transactionDetailsVO.getCcnum()) && isValueNull(transactionDetailsVO.getExpdate()))
            {
                String firstSix = transactionDetailsVO.getCcnum().substring(0, 6);
                String lastFour = transactionDetailsVO.getCcnum().substring((transactionDetailsVO.getCcnum().length() - 4), transactionDetailsVO.getCcnum().length());
                cardDetails.put("bin", firstSix);
                cardDetails.put("lastFourDigits", lastFour);
                cardDetails.put("last4Digits", lastFour);
                String expDate = transactionDetailsVO.getExpdate();

                String expMonth = "";
                String expYear = "";

                if (expDate.contains("/"))
                {
                    String edate[] = expDate.split("/");
                    if (edate.length == 2)
                    {
                        expMonth = edate[0];
                        expYear = edate[1];
                    }
                }
                cardDetails.put("expiryMonth", expMonth);
                cardDetails.put("expiryYear", expYear);
                jo.put("card", cardDetails);
            }
            else
            {
                cardDetails.put("bin", "");
                cardDetails.put("lastFourDigits", "");
                cardDetails.put("last4Digits", "");
                cardDetails.put("expiryMonth", "");
                cardDetails.put("expiryYear", "");
                jo.put("card", cardDetails);
            }
            jo.put("timestamp", String.valueOf(dateFormat.format(date)));
            jo.put("descriptor", transactionDetailsVO.getBillingDesc());
            if (isValueNull(transactionDetailsVO.getEci()))
            {
                jo.put("eci", transactionDetailsVO.getEci());
            }
            else
            {
                jo.put("eci", "");
            }
            if (isValueNull(transactionDetailsVO.getBankReferenceId()))
            {
                jo.put("bankReferenceId", transactionDetailsVO.getBankReferenceId());
            }
            else
            {
                jo.put("bankReferenceId", "");
            }
            if (isValueNull(transactionDetailsVO.getTerminalId()))
            {
                jo.put("terminalId", transactionDetailsVO.getTerminalId());
            }
            else
            {
                jo.put("terminalId", "");
            }

            Gson gson = new Gson();
            CommResponseVO commResponseVO   = new CommResponseVO();
            AuditTrailVO auditTrailVO       = new AuditTrailVO();
            StringEntity postingString      = new StringEntity(jo.toString());//gson.tojson() converts your pojo to json
            int code                        =0;

            if (isValueNull(transactionDetailsVO.getNotificationUrl())){
                HttpClient httpClient   = HttpClientBuilder.create().build();
                post                    = new HttpPost(transactionDetailsVO.getNotificationUrl());
                transactionLogger.error("postingString Functions---" + postingString);
                post.setHeader("Content-type", "application/json");
                post.setEntity(postingString);
                HttpResponse response   = httpClient.execute(post);
                code                    = response.getStatusLine().getStatusCode();
                transactionLogger.error("setMerchantNotification sending Notification JSON for Recon ---for---> " + trackingid + "---" + jo.toString());
                transactionLogger.error("response --------> " + trackingid + " --- " + response.toString() + "  Response Staus Code:: " + code);
            }

            if(isValueNull(transactionDetailsVO.getMerchantNotificationUrl())){
                HttpClient httpClient2      = HttpClientBuilder.create().build();
                post                        = new HttpPost(transactionDetailsVO.getMerchantNotificationUrl());
                post.setHeader("Content-type", "application/json");
                post.setEntity(postingString);
                HttpResponse responseMerchant   = httpClient2.execute(post);
                int codeMerchant                = responseMerchant.getStatusLine().getStatusCode();
                transactionLogger.error("MerchantURL Notification JSON for Recon ----> "+trackingid+" ---> "+jo.toString() +" Response Code Merchant >> "+codeMerchant);
            }

            commResponseVO.setTransactionStatus(transactionStatus);
            commResponseVO.setRemark(status + " Response code: "+code);
            commResponseVO.setDescription(status);
            commResponseVO.setCurrency(transactionDetailsVO.getCurrency());
            commResponseVO.setAmount(transactionDetailsVO.getAmount());
            commResponseVO.setTmpl_Currency(transactionDetailsVO.getTemplatecurrency());
            commResponseVO.setTmpl_Amount(transactionDetailsVO.getTemplateamount());
            commResponseVO.setDescriptor(transactionDetailsVO.getBillingDesc());
            commResponseVO.setIpaddress(transactionDetailsVO.getIpAddress());

            auditTrailVO.setActionExecutorName(transactionDetailsVO.getActionExecutorName());
            auditTrailVO.setActionExecutorId(transactionDetailsVO.getToid());
            transactionLogger.error("response Functions setMerchantNotification---for "+trackingid+" --- " + "  Response Staus code:: "+code);
            transactionManager.updateNotificationStatusCode(trackingid,String.valueOf(code),commResponseVO,auditTrailVO);
            //System.out.println(jo);
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException while notification---", e);
        }
        catch (IOException e)
        {
            transactionLogger.error("IOException while notification---", e);
            //PZExceptionHandler.raiseTechnicalViolationException("TransactionUtility.java", "setMerchantNotification()", null, "transaction", "UnsupportedEncodingException:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
        }finally{
            if(post!=null)
                post.releaseConnection();
        }

    }

    public void setTokenNotification(CommonValidatorVO commonValidatorVO, String registrationStatus, String registrationToken)
    {
        HttpPost post = null;
        transactionLogger.error("Inside setTokenNotification");
        JSONObject jo = new JSONObject();
        Functions functions = new Functions();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String timeStamp = String.valueOf(dateFormat.format(date));
        String checkSum = null;
        String respStatus = "N";
        String firstName = "";
        String lastName = "";
        String token = "";
        String status = "";
        String customerId = "";
        if (functions.isValueNull(registrationStatus))
        {
            status = registrationStatus;
        }

        if (functions.isValueNull(registrationToken))
            token = registrationToken;
        else
            token = "";
        if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getFirstname()))
            firstName = commonValidatorVO.getAddressDetailsVO().getFirstname();
        else
            firstName = "";
        if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getLastname()))
            lastName = commonValidatorVO.getAddressDetailsVO().getLastname();
        else
            lastName = "";
        if (functions.isValueNull(commonValidatorVO.getCustomerId()))
            customerId = commonValidatorVO.getCustomerId();
        try
        {
            if (functions.isValueNull(commonValidatorVO.getErrorName()))
            {
                errorCodeVO = errorCodeUtils.getRejectedErrorCode(ErrorName.valueOf(commonValidatorVO.getErrorName()));
                if (errorCodeVO == null)
                {
                    errorCodeVO = errorCodeUtils.getSystemErrorCode(ErrorName.valueOf(commonValidatorVO.getErrorName()));
                }
            }
            else if (functions.isValueNull(status) && status.contains("success"))
            {
                respStatus = "Y";
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_TOKEN_GENERATION);
            }
            else
            {
                respStatus = "N";
                if (functions.isValueNull(commonValidatorVO.getErrorMsg()))
                {
                    errorCodeVO.setApiDescription(commonValidatorVO.getErrorMsg());
                }
                else
                {
                    errorCodeVO = errorCodeUtils.getRejectedErrorCode(ErrorName.TOKEN_CREATION_FAILED);
                }
            }
            try
            {
                checkSum = Checksum.generateChecksumForCardRegistration(getValue(commonValidatorVO.getMerchantDetailsVO().getMemberId()), getValue(commonValidatorVO.getMerchantDetailsVO().getKey()), registrationStatus);
            }
            catch (NoSuchAlgorithmException e)
            {
                respStatus = "N";
            }

            jo.put("resultCode", getValue(errorCodeVO.getApiCode()));
            jo.put("resultDescription", getValue(errorCodeVO.getApiDescription()));
            jo.put("timestamp", timeStamp);
            jo.put("firstName", firstName);
            jo.put("lastName", lastName);
            jo.put("checksum", checkSum);
            jo.put("status", respStatus);
            jo.put("registrationId", token);
            if (null != commonValidatorVO.getCardDetailsVO() && functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
            {
                String cardBin = commonValidatorVO.getCardDetailsVO().getCardNum().substring(0, 6);
                String cardLast4Digits = commonValidatorVO.getCardDetailsVO().getCardNum().substring((commonValidatorVO.getCardDetailsVO().getCardNum().length() - 4), commonValidatorVO.getCardDetailsVO().getCardNum().length());
                jo.put("firstSix", cardBin);
                jo.put("lastFour", cardLast4Digits);
            }
            jo.put("cardType", commonValidatorVO.getCardType());
            jo.put("customerId", customerId);
            Gson gson = new Gson();
            HttpClient httpClient = HttpClientBuilder.create().build();
            post = new HttpPost(commonValidatorVO.getTransDetailsVO().getNotificationUrl());
            StringEntity postingString = new StringEntity(jo.toString());//gson.tojson() converts your pojo to json
            transactionLogger.error("Functions setTokenNotification postingString---" + postingString);
            transactionLogger.error("---Notification---" + jo.toString());
            post.setHeader("Content-type", "application/json");
            post.setEntity(postingString);
            HttpResponse response = httpClient.execute(post);
            transactionLogger.error("Functions setTokenNotification response---" + response.toString());
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException while notification---", e);
        }
        catch (IOException e)
        {
            transactionLogger.error("IOException while notification---", e);
        }
        finally
        {
            if (post != null)
                post.releaseConnection();
        }
    }

    public String getValue(String value)
    {
        Functions functions = new Functions();
        if (functions.isValueNull(value))
            return value;
        else
            return "";
    }

    public String getActivation(String memberid)
    {
        log.debug("inside getActivation:::");
        String activation = "";
        PreparedStatement pstmt1 = null;
        Connection cn = null;
        ResultSet rs = null;
        try
        {
            cn = Database.getConnection();
            StringBuilder query1 = new StringBuilder("SELECT activation FROM members where memberid = ?");
            pstmt1 = cn.prepareStatement(query1.toString());
            pstmt1.setString(1, memberid);
            rs = pstmt1.executeQuery();
            while (rs.next())
            {
                activation = rs.getString("activation");
            }
        }
        catch (Exception e)
        {
            log.debug("Exception:::::" + e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt1);
            Database.closeConnection(cn);
        }
        return activation;
    }

    public String getPartnerActivation(String partnerid)
    {
        log.debug("inside getPartnerActivation:::");
        String activation = "";
        PreparedStatement pstmt1 = null;
        Connection cn = null;
        ResultSet rs = null;
        try
        {
            cn = Database.getConnection();
            StringBuilder query1 = new StringBuilder("SELECT activation FROM partner_default_configuration WHERE partnerid = ?");
            pstmt1 = cn.prepareStatement(query1.toString());
            if (partnerid != null)
            {
                pstmt1.setString(1, partnerid);
            }
            else
            {
                pstmt1.setString(1, "1");
            }
            log.debug("pstmt SELECT activation::::::" + pstmt1);
            rs = pstmt1.executeQuery();
            while (rs.next())
            {
                activation = rs.getString("activation");
            }
        }
        catch (Exception e)
        {
            log.debug("Exception:::::" + e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt1);
            Database.closeConnection(cn);
        }
        return activation;
    }

    public String convertDecimalToInt(String str)
    {
        String num = "";
        DecimalFormat decimalFormat = new DecimalFormat("#");
        try
        {
            int number = decimalFormat.parse(str).intValue();
            num = String.valueOf(number);
        }
        catch (ParseException e)
        {
            log.debug(num + " is not a valid number.");
        }
        return num;

    }

    public String printNumber(Locale locale, String amount)
    {
        double dbl = Double.parseDouble(amount);
        NumberFormat formatter = NumberFormat.getNumberInstance(locale);
        String number = formatter.format(dbl);
        return number;
    }

    public static String convertDateTimeToTimeZone(String date, String timezone) throws ParseException
    {
        String sDate1 = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = sdf1.parse(date);
        TimeZone zone = TimeZone.getTimeZone(timezone);
        sdf.setTimeZone(zone);
        String sDate = sdf.format(date1); // Convert to String first
        String datetimeArray[] = sDate.split(" ");
        String mm = String.valueOf(Integer.parseInt(datetimeArray[0].split("-")[1]) - 1);
        String dtstmp = Functions.converttomillisec(mm, datetimeArray[0].split("-")[2], datetimeArray[0].split("-")[0], datetimeArray[1].split(":")[0], datetimeArray[1].split(":")[1], datetimeArray[1].split(":")[2]);
        sDate1 = Functions.convertDtstampToDateTime(dtstmp);
        return sDate1;
    }

    public static String convertDateTimeToTimeZone1(String date, String timezone) throws ParseException
    {
        String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        ZoneId fromTimeZone = ZoneId.of(timezone);  //Source timezone
        ZoneId toTimeZone = ZoneId.of("Asia/Kolkata");    //Target timezone
        LocalDateTime fromZoneDate = LocalDateTime.parse(date, formatter);
        //Zoned date time at source timezone
        ZonedDateTime fromTime = fromZoneDate.atZone(fromTimeZone);

        //Zoned date time at target timezone
        ZonedDateTime toTimeIST = fromTime.withZoneSameInstant(toTimeZone);
        String strtoTimeIST = formatter.format(toTimeIST);
        return strtoTimeIST;
    }

    public static boolean checkInstallment(String startDate, String endDate) throws ParseException
    {
        boolean isAvailable = false;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date1 = dateFormat.parse(startDate);
        Date date2 = dateFormat.parse(endDate);
        Timestamp sDate = new Timestamp(date1.getTime());
        Timestamp eDate = new Timestamp(date2.getTime());
        Timestamp today = new Timestamp(new Date().getTime());
        if (sDate.before(today) && eDate.before(today) || sDate.after(today) && eDate.after(today))
        {
            isAvailable = true;
        }
        return isAvailable;
    }

    public boolean isPasswordForgotWithInHour(String forgotDate)
    {
        boolean isPasswordForgotWithInHour = false;
        try
        {
            long diffHours = 1;
            forgotDate = Functions.convertDtstampToDBFormat(forgotDate);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            Date d1 = sdf.parse(forgotDate);
            System.out.println(d1);
            Date d2 = sdf.parse(currentDate);
            long diff = d2.getTime() - d1.getTime();
            diffHours = diff / (60 * 60 * 1000);

            System.out.println("diffHours--->" + diffHours);
            if (diffHours == 0)
                isPasswordForgotWithInHour = true;
        }
        catch (ParseException e)
        {
            log.error("ParseException e-->", e);
        }
        return isPasswordForgotWithInHour;
    }

   /* public static String ssl_encrypt()
    {
        String enc = "";
        try
        {
            String deskey = "3d5a5d89b4a6c2f934086ddc5fce7ee22f8988d634572e8e51071febad2aeb3e";

            String iv = "eb3caab09451fcd2";
            byte[] key = deskey.getBytes();

            Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            //Cipher cipher = Cipher.getInstance("AES-256-CBC");
            IvParameterSpec aaa = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "DESede"), aaa);
            enc = java.util.Base64.getEncoder().encodeToString(cipher.doFinal("".getBytes()));

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return enc;
    }*/

    public static void main(String[] args) throws Exception
    {
//        System.out.println(ssl_encrypt());
        /* String originalString = "my home town is gujarat its a nice place to visit in india people should have to visit once ----------";

        //System.out.println("Original String to encrypt - " + originalString);
       // String encryptedString = encrypt(originalString);
       // System.out.println("Encrypted String - " + encryptedString);
       // String decryptedString = decrypt(encryptedString);
       // System.out.println("After decryption - " + decryptedString);

        HashSet<String> map = new HashSet<String>();

         map.add("jeet");
         map.add("jeet");
         map.add("pawan");
         map.add("nagendra");
         map.add("gupta");

         for(String s :map)
         {
             String key = s.toString();

            // System.out.println("key is----------"+key);
           //  System.out.println("value is --------"+value);
         }
       // System.out.println("\n");
       // System.out.println("\n");
         HashMap<String,String> map1 = new HashMap<String,String>();
         map1.put("1","jeet");
         map1.put("1","jeet");
         map1.put("2","jeet");
         map1.put("3","gupta");
         map1.put("4","kalwati");
         map1.put("5","");
         map1.put("6","");

        // print hashmap values

         for (HashMap.Entry<String,String> m : map1.entrySet())
         {
             String key = m.getKey();
             String Value = m.getValue();

             //System.out.println("key is ----------->"+key.toString());
             //System.out.println("value is ----------->"+Value.toString());
         }
      // map1.forEach((key, value) -> System.out.println("key is =->"+value)); //enhanced for each iteration with lambda expression---------------->

        double amount;
        double newCurr;
        double defaultCurrAmt=100.00;

        Scanner sc = new Scanner(System.in);
       // System.out.println("enter amount to be parsed ----->");
        amount = sc.nextDouble();

        newCurr = amount * defaultCurrAmt;

       // System.out.println("newCurrency is -----------> "+newCurr);*/

    }

    public static HashMap<String, String> getTimeZone()
    {
        HashMap<String, String> timezoneHash = new HashMap<>();
        timezoneHash.put("Etc/GMT+12|(GMT-12:00)", "(GMT-12:00) International Date Line West");
        timezoneHash.put("Pacific/Midway|(GMT-11:00)", "(GMT-11:00) Midway Island, Samoa");
        timezoneHash.put("Pacific/Honolulu|(GMT-10:00)", "(GMT-10:00) Hawaii");
        timezoneHash.put("US/Alaska|(GMT-09:00)", "(GMT-09:00) Alaska");
        timezoneHash.put("America/Los_Angeles|(GMT-08:00)", "(GMT-08:00) Pacific Time (US & Canada)");
        timezoneHash.put("America/Tijuana|(GMT-08:00)", "(GMT-08:00) Tijuana, Baja California");
        timezoneHash.put("US/Arizona|(GMT-07:00)", "(GMT-07:00) Arizona");
        timezoneHash.put("America/Chihuahua|(GMT-07:00)", "(GMT-07:00) Chihuahua, La Paz, Mazatlan");
        timezoneHash.put("US/Mountain|(GMT-07:00)", "(GMT-07:00) Mountain Time (US & Canada)");
        timezoneHash.put("America/Managua|(GMT-06:00)", "(GMT-06:00) Central America");
        timezoneHash.put("US/Central|(GMT-06:00)", "(GMT-06:00) Central Time (US & Canada)");
        timezoneHash.put("America/Mexico_City|(GMT-06:00)", "(GMT-06:00) Guadalajara, Mexico City, Monterrey");
        timezoneHash.put("Canada/Saskatchewan|(GMT-06:00)", "(GMT-06:00) Saskatchewan");
        timezoneHash.put("America/Bogota|(GMT-05:00)", "(GMT-05:00) Bogota, Lima, Quito, Rio Branco");
        timezoneHash.put("US/Eastern|(GMT-05:00)", "(GMT-05:00) Eastern Time (US & Canada)");
        timezoneHash.put("US/East-Indiana|(GMT-05:00)", "(GMT-05:00) Indiana (East)");
        timezoneHash.put("Canada/Atlantic|(GMT-04:00)", "(GMT-04:00) Atlantic Time (Canada)");
        timezoneHash.put("America/Caracas|(GMT-04:00)", "(GMT-04:00) Caracas, La Paz");
        timezoneHash.put("America/Manaus|(GMT-04:00)", "(GMT-04:00) Manaus");
        timezoneHash.put("America/Santiago|(GMT-04:00)", "(GMT-04:00) Santiago");
        timezoneHash.put("Canada/Newfoundland|(GMT-03:30)", "(GMT-03:30) Newfoundland");
        timezoneHash.put("America/Sao_Paulo|(GMT-03:00)", "(GMT-03:00) Brasilia");
        timezoneHash.put("America/Argentina/Buenos_Aires|(GMT-03:00)", "(GMT-03:00) Buenos Aires, Georgetown");
        timezoneHash.put("America/Godthab|(GMT-03:00)", "(GMT-03:00) Greenland");
        timezoneHash.put("America/Montevideo|(GMT-03:00)", "(GMT-03:00) Montevideo");
        timezoneHash.put("America/Noronha|(GMT-02:00)", "(GMT-02:00) Mid-Atlantic");
        timezoneHash.put("Atlantic/Cape_Verde|(GMT-01:00)", "(GMT-01:00) Cape Verde Is.");
        timezoneHash.put("Atlantic/Azores|(GMT-01:00)", "(GMT-01:00) Azores");
        timezoneHash.put("Africa/Casablanca|(GMT+00:00)", "(GMT+00:00) Casablanca, Monrovia, Reykjavik");
        timezoneHash.put("Etc/Greenwich|(GMT+00:00)", "(GMT+00:00) Greenwich Mean Time : Dublin, Edinburgh, Lisbon, London");
        timezoneHash.put("Europe/Amsterdam|(GMT+01:00)", "(GMT+01:00) Amsterdam, Berlin, Bern, Rome, Stockholm, Vienna");
        timezoneHash.put("Europe/Belgrade|(GMT+01:00)", "(GMT+01:00) Belgrade, Bratislava, Budapest, Ljubljana, Prague");
        timezoneHash.put("Europe/Brussels|(GMT+01:00)", "(GMT+01:00) Brussels, Copenhagen, Madrid, Paris");
        timezoneHash.put("Europe/Sarajevo|(GMT+01:00)", "(GMT+01:00) Sarajevo, Skopje, Warsaw, Zagreb");
        timezoneHash.put("Africa/Lagos|(GMT+01:00)", "(GMT+01:00) West Central Africa");
        timezoneHash.put("Asia/Amman|(GMT+02:00)", "(GMT+02:00) Amman");
        timezoneHash.put("Europe/Athens|(GMT+02:00)", "(GMT+02:00) Athens, Bucharest, Istanbul");
        timezoneHash.put("Asia/Beirut|(GMT+02:00)", "(GMT+02:00) Beirut");
        timezoneHash.put("Africa/Cairo|(GMT+02:00)", "(GMT+02:00) Cairo");
        timezoneHash.put("Africa/Harare|(GMT+02:00)", "(GMT+02:00) Harare, Pretoria");
        timezoneHash.put("Europe/Helsinki|(GMT+02:00)", "(GMT+02:00) Helsinki, Kyiv, Riga, Sofia, Tallinn, Vilnius");
        timezoneHash.put("Asia/Jerusalem|(GMT+02:00)", "(GMT+02:00) Jerusalem");
        timezoneHash.put("Europe/Minsk|(GMT+02:00)", "(GMT+02:00) Minsk");
        timezoneHash.put("Africa/Windhoek|(GMT+02:00)", "(GMT+02:00) Windhoek");
        timezoneHash.put("Asia/Kuwait|(GMT+03:00)", "(GMT+03:00) Kuwait, Riyadh, Baghdad");
        timezoneHash.put("Europe/Moscow|(GMT+03:00)", "(GMT+03:00) Moscow, St. Petersburg, Volgograd");
        timezoneHash.put("Africa/Nairobi|(GMT+03:00)", "(GMT+03:00) Nairobi");
        timezoneHash.put("Asia/Tbilisi|(GMT+03:00)", "(GMT+03:00) Tbilisi");
        timezoneHash.put("Asia/Tehran|(GMT+03:30)", "(GMT+03:30) Tehran");
        timezoneHash.put("Asia/Muscat|(GMT+04:00)", "(GMT+04:00) Abu Dhabi, Muscat");
        timezoneHash.put("Asia/Baku|(GMT+04:00)", "(GMT+04:00) Baku");
        timezoneHash.put("Asia/Yerevan|(GMT+04:00)", "(GMT+04:00) Yerevan");
        timezoneHash.put("Asia/Kabul|(GMT+04:30)", "(GMT+04:30) Kabul");
        timezoneHash.put("Asia/Yekaterinburg|(GMT+05:00)", "(GMT+05:00) Yekaterinburg");
        timezoneHash.put("Asia/Karachi|(GMT+05:00)", "(GMT+05:00) Islamabad, Karachi, Tashkent");
        timezoneHash.put("Asia/Calcutta|(GMT+05:30)", "(GMT+05:30) Chennai, Kolkata, Mumbai, New Delhi");
        timezoneHash.put("Asia/Katmandu|(GMT+05:45)", "(GMT+05:45) Kathmandu");
        timezoneHash.put("Asia/Almaty|(GMT+06:00)", "(GMT+06:00) Almaty, Novosibirsk");
        timezoneHash.put("Asia/Dhaka|(GMT+06:00)", "(GMT+06:00) Astana, Dhaka");
        timezoneHash.put("Asia/Rangoon|(GMT+06:30)", "(GMT+06:30) Yangon (Rangoon)");
        timezoneHash.put("Asia/Bangkok|(GMT+07:00)", "(GMT+07:00) Bangkok, Hanoi, Jakarta");
        timezoneHash.put("Asia/Krasnoyarsk|(GMT+07:00)", "(GMT+07:00) Krasnoyarsk");
        timezoneHash.put("Asia/Hong_Kong|(GMT+08:00)", "(GMT+08:00) Beijing, Chongqing, Hong Kong, Urumqi");
        timezoneHash.put("Asia/Kuala_Lumpur|(GMT+08:00)", "(GMT+08:00) Kuala Lumpur, Singapore");
        timezoneHash.put("Asia/Irkutsk|(GMT+08:00)", "(GMT+08:00) Irkutsk, Ulaan Bataar");
        timezoneHash.put("Australia/Perth|(GMT+08:00)", "(GMT+08:00) Perth");
        timezoneHash.put("Asia/Taipei|(GMT+08:00)", "(GMT+08:00) Taipei");
        timezoneHash.put("Asia/Tokyo|(GMT+09:00)", "(GMT+09:00) Osaka, Sapporo, Tokyo");
        timezoneHash.put("Asia/Seoul|(GMT+09:00)", "(GMT+09:00) Seoul");
        timezoneHash.put("Asia/Yakutsk|GMT+09:00)", "(GMT+09:00) Yakutsk");
        timezoneHash.put("Australia/Adelaide|(GMT+09:30)", "(GMT+09:30) Adelaide");
        timezoneHash.put("Australia/Darwin|(GMT+09:30)", "(GMT+09:30) Darwin");
        timezoneHash.put("Australia/Brisbane|(GMT+10:00)", "(GMT+10:00) Brisbane");
        timezoneHash.put("Australia/Canberra|(GMT+10:00)", "(GMT+10:00) Canberra, Melbourne, Sydney");
        timezoneHash.put("Australia/Hobart|(GMT+10:00)", "(GMT+10:00) Hobart");
        timezoneHash.put("Pacific/Guam|(GMT+10:00)", "(GMT+10:00) Guam, Port Moresby");
        timezoneHash.put("Asia/Vladivostok|(GMT+10:00)", "(GMT+10:00) Vladivostok");
        timezoneHash.put("Asia/Magadan|(GMT+11:00)", "(GMT+11:00) Magadan, Solomon Is., New Caledonia");
        timezoneHash.put("Pacific/Auckland|(GMT+12:00)", "(GMT+12:00) Auckland, Wellington");
        timezoneHash.put("Pacific/Fiji|(GMT+12:00)", "(GMT+12:00) Fiji, Kamchatka, Marshall Is.");
        timezoneHash.put("Pacific/Tongatapu|(GMT+13:00)", "(GMT+13:00) Nuku'alofa");
        return timezoneHash;
    }

    public void sendFile(String fileName, String user, String file)
    {
        String SFTPHOST = ApplicationProperties.getProperty("SFTP_HOST");//"103.95.12.129";
        int SFTPPORT = 22;
        String SFTPUSER = ApplicationProperties.getProperty("SFTP_USER");//"root";
        String SFTPPASS = ApplicationProperties.getProperty("SFTP_PASSWORD");//"ewq!dsa@prod7788";
        String SFTPWORKINGDIR = "/home/" + user + ApplicationProperties.getProperty("DESTINATION_EXPORT_SFTP_FILE_PATH");
        //String LOCALPATH = "D:/transactions";

        Session session = null;
        com.jcraft.jsch.Channel channel = null;
        ChannelSftp channelSftp = null;
        log.error("preparing the host information for sftp.");

        try
        {
            JSch jsch = new JSch();
            session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
            session.setPassword(SFTPPASS);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            log.error("Host connected.");
            channel = session.openChannel("sftp");
            channel.connect();
            log.error("sftp channel opened and connected.");
            channelSftp = (ChannelSftp) channel;
            channelSftp.cd(SFTPWORKINGDIR);
            /*File f = new File(fileName);
            channelSftp.put(new FileInputStream(f), f.getName());*/
            recursiveFolderUpload(fileName, SFTPWORKINGDIR, channelSftp);
            //download("/home" + "/" + user + "/" + file, LOCALPATH, channelSftp);
            log.error("File transfered successfully to host.");
        }
        catch (Exception ex)
        {
            log.error("Exception found while tranfer the response." + ex);
        }
        finally
        {
            channelSftp.exit();
            log.error("sftp Channel exited.");
            channel.disconnect();
            log.error("Channel disconnected.");
            session.disconnect();
            log.error("Host Session disconnected.");
        }
    }

    private static void recursiveFolderUpload(String sourcePath, String destinationPath, ChannelSftp channelSftp) throws SftpException, FileNotFoundException
    {
        log.error("sourcePath------------>" + sourcePath);
        log.error("destinationPath------------>" + destinationPath);
        File sourceFile = new File(sourcePath);
        if (sourceFile.isFile())
        {

            // copy if it is a file
            channelSftp.cd(destinationPath);
            if (!sourceFile.getName().startsWith("."))
                channelSftp.put(new FileInputStream(sourceFile), sourceFile.getName(), ChannelSftp.OVERWRITE);

        }
        else
        {

            log.error("inside else " + sourceFile.getName());
            File[] files = sourceFile.listFiles();

            if (files != null && !sourceFile.getName().startsWith("."))
            {

                channelSftp.cd(destinationPath);
                SftpATTRS attrs = null;

                // check if the directory is already existing
                try
                {
                    attrs = channelSftp.stat(destinationPath /*+ "/" + sourceFile.getName()*/);
                }
                catch (Exception e)
                {
                    log.error("Creating dir " + sourceFile.getName());
                    //channelSftp.mkdir(sourceFile.getName());
                }

                // else create a directory
                /*if (attrs != null) {
                    System.out.println("Directory exists IsDir=" + attrs.isDir());
                } else {
                    System.out.println("Creating dir " + sourceFile.getName());
                    channelSftp.mkdir(sourceFile.getName());
                }*/

                for (File f : files)
                {
                    recursiveFolderUpload(f.getAbsolutePath(), destinationPath /*+ "/" + sourceFile.getName()*/, channelSftp);
                }

            }
        }
    }

    public void download(String fileName, String localDir, ChannelSftp sftpChannel)
    {

        byte[] buffer = new byte[1024];
        BufferedInputStream bis;
        try
        {
            // Change to output directory
            String cdDir = fileName.substring(0, fileName.lastIndexOf("/") + 1);
            sftpChannel.cd(cdDir);

            File file = new File(fileName);
            bis = new BufferedInputStream(sftpChannel.get(file.getName()));

            File newFile = new File(localDir + "/" + file.getName());

            // Download file
            OutputStream os = new FileOutputStream(newFile);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            int readCount;
            while ((readCount = bis.read(buffer)) > 0)
            {
                bos.write(buffer, 0, readCount);
            }
            bis.close();
            bos.close();
            /*System.out.println("File downloaded successfully - "+ file.getAbsolutePath());*/

        }
        catch (Exception e)
        {
            log.error("Exception---->", e);
        }
    }


    public String getTransactionType(String type)
    {
        String payMode = "";
        if ("sale".equalsIgnoreCase(type) || "PURCHASE".equalsIgnoreCase(type) || "PAYMENT".equalsIgnoreCase(type) || "BILL PAYMENT".equalsIgnoreCase(type))
            payMode = "'capturesuccess','reversed','settled'";
        if ("refund".equalsIgnoreCase(type) || "CREDIT".equalsIgnoreCase(type) || "ORIG CREDIT".equalsIgnoreCase(type) || "RETURN".equalsIgnoreCase(type))
            payMode = "'reversed','settled'";
        if ("authorization".equalsIgnoreCase(type) || "AUTH".equalsIgnoreCase(type))
            payMode = "'authsuccessful','capturesuccess','reversed','settled'";
        return payMode;
    }

    public static long getInvoiceExpiredTime(String generateDate, String expiredPeriod) throws ParseException
    {
        String year = "";
        String month = "";
        String day = "";
        boolean isExpired = false;
        Date date = null;
        if (generateDate.contains(" "))
        {
            String d = generateDate.split(" ")[0];
            if (d.contains("-"))
            {
                String d1[] = d.split("-");
                year = d1[0];
                month = d1[1];
                day = d1[2];
            }
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
        cal.set(Calendar.MONTH, Integer.parseInt(month) - 1);
        cal.set(Calendar.YEAR, Integer.parseInt(year));
        Date firstDate = cal.getTime();
        long days = Long.parseLong(expiredPeriod) * 1000 * 60 * 60 * 24;
        long expiryTime = (firstDate.getTime() + days) / 1000;
        System.out.println("---->" + expiryTime);

        return expiryTime;
    }

    public HashMap<String, Object> getFingerPrintMap(String fingerPrint)
    {
        HashMap<String, Object> fingerPrintMap = new HashMap<>();
        try
        {
            if (isValueNull(fingerPrint))
            {
                JSONArray jsonArray = new JSONArray(fingerPrint);
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String key = jsonObject.getString("key");
                    Object object = jsonObject.get("value");
                    fingerPrintMap.put(key, object);

                }
            }

        }
        catch (JSONException e)
        {
            log.error("JSONException---->", e);
        }
        return fingerPrintMap;
    }

    public String maskingPan(String str)
    {
        String mask = "";
        if (str != null && !(str.isEmpty()) && (str.length() >= 10))
        {
            mask += str.substring(0, 6);
            for (int i = 7; i <= str.length() - 4; i++)
                mask += "*";
            mask += str.substring(str.length() - 4);
        }
        return mask;
    }

    public String maskingNumber(String str)
    {
        String mask = "";
        if (str != null)
        {
            for (int i = 1; i <= str.length(); i++)
                mask += "*";
        }
        return mask;
    }

    public String maskingExpiry(String str)
    {
        String mask = "";
        if (str != null && str.contains("/"))
        {
            String[] s = str.split("/");
            if (s.length == 2)
            {
                String first = s[0];
                String last = s[1];
                mask = maskingNumber(first) + "/" + maskingNumber(last);
            }
        }
        return mask;
    }

    public String maskingFirstName(String str)
    {
        String mask = "";
        if (str.length() < 2)
        {
            mask = str;
        }
        else
        {
            mask += str.substring(0, 2);
            for (int i = 3; i <= str.length(); i++)
                mask += "*";
            mask += str.substring(str.length());
        }
        return mask;
    }

    public String maskingLastName(String str)
    {
        String mask = "";

        if (str != null && !(str.isEmpty()))
        {
            if (str.length() < 2)
            {
                mask = str;
            }
            else
            {
                for (int i = 0; i <= str.length() - 2; i++)
                    mask += "*";
                mask += str.substring(str.length() - 2);
            }
        }
        return mask;
    }

    public String getRoleofPartner(String partnerid)
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String v_roll = "";
        try
        {
            con = Database.getRDBConnection();
            String qry = "select roles from user u , partners p where u.login=p.login and p.partnerId=?";
            pstmt = con.prepareStatement(qry);
            pstmt.setString(1, partnerid);
            log.debug("partner query::::" + pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                v_roll = v_roll + "," + rs.getString("roles");
            }
        }
        catch (Exception e)
        {
            log.error("error", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return v_roll;
    }

    public boolean isDomainWhitelisted(String refererHeader, String wlDomain)
    {
        transactionLogger.error("inside getIFrameHeaderValue---" + wlDomain + "---" + refererHeader);
        Boolean isWhitelistedDomin = false;
        if (this.isValueNull(refererHeader) && this.isValueNull(wlDomain))
        {
            if (wlDomain.contains(","))
            {
                String allowedDomain = "";
                List<String> arrList = Arrays.asList(wlDomain.split(","));
                transactionLogger.error("arrList from getIFrameHeaderValuea---" + arrList);
                for (String domain : arrList)
                {
                    if (refererHeader.contains(domain))
                    {
                        allowedDomain = domain;
                        isWhitelistedDomin = true;
                        transactionLogger.error("allowedDomain from getIFrameHeaderValuea---" + allowedDomain);
                        break;
                    }
                }
            }
            else if (refererHeader.contains(wlDomain))
            {
                isWhitelistedDomin = true;
            }

        }
        return isWhitelistedDomin;
    }

    public boolean isValidHexCodeColor(String hexcodecolor)
    {
        String regex_colorhexcode = "^[A-Za-z]+|#[A-Fa-f0-9]{6}|[A-Fa-f0-9]{3}$";
        if (Pattern.matches(regex_colorhexcode, hexcodecolor) || hexcodecolor.isEmpty())
            return true;
        return false;
    }

    public String getTimestamp()
    {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = formatter.format(date);
        return strDate;
    }

    public boolean isValidTimeZone(String inDate)
    {
        transactionLogger.info("Time zone date-----" + inDate);
        SimpleDateFormat dateFormat = new SimpleDateFormat("X");
        dateFormat.setLenient(false);
        try
        {
            dateFormat.parse(inDate.trim());
        }
        catch (ParseException pe)
        {
            return false;
        }
        return true;
    }

    public String getVpaAddressPrefix(String vpaAddress)
    {
        return vpaAddress.split("@")[0];
    }

    public static String encodeFileToBase64Binary(String filePath)
    {
        String encodedfile = "";
        try
        {
            File file = new File(filePath); //change path of image according to you
            if (file.exists())
            {
                FileInputStream fis = new FileInputStream(file);
                byte byteArray[] = new byte[(int) file.length()];
                fis.read(byteArray);
                encodedfile = Base64.encodeBase64String(byteArray);
            }
        }
        catch (FileNotFoundException e)
        {
            log.error("encodeFileToBase64Binary", e);
        }
        catch (IOException e)
        {
            log.error("encodeFileToBase64Binary", e);
        }

        return encodedfile;
    }

    public static boolean isJSONValid(String test)
    {
        try
        {
            new JSONObject(test);
        }
        catch (JSONException ex)
        {
            return false;
        }
        return true;
    }

    public String getStartDtStamp(DateVO dateVO)
    {
        Functions functions= new Functions();
        String datestartdtstmp ="";
        if (functions.isValueNull(dateVO.getStartDate()) &&  dateVO.getStartDate().split(" ").length >0)
        {
            String datetimeArray2[] = dateVO.getStartDate().split(" ");
            String mm2 = String.valueOf(Integer.parseInt(datetimeArray2[0].split("-")[1]) - 1);
            datestartdtstmp = Functions.converttomillisec(mm2,datetimeArray2[0].split("-")[2],datetimeArray2[0].split("-")[0],datetimeArray2[1].split(":")[0],datetimeArray2[1].split(":")[1],datetimeArray2[1].split(":")[2]);
        }
        return datestartdtstmp;
    }

    public String getEndDtStamp(DateVO dateVO)
    {
        Functions functions= new Functions();
        String dateenddtstmp="";
        if (functions.isValueNull(dateVO.getEndDate()) && dateVO.getEndDate().split(" ").length>0)
        {
            String datetimeArray3[] = dateVO.getEndDate().split(" ");
            String mm3 = String.valueOf(Integer.parseInt(datetimeArray3[0].split("-")[1]) - 1);
            dateenddtstmp = Functions.converttomillisec(mm3,datetimeArray3[0].split("-")[2],datetimeArray3[0].split("-")[0],datetimeArray3[1].split(":")[0],datetimeArray3[1].split(":")[1],datetimeArray3[1].split(":")[2]);
        }
        return dateenddtstmp;
    }

    public String getLastWeekStartdtStamp(DateVO lastweek)
    {
        String datetimeArray[] = lastweek.getStartDate().split(" ");
        String mm = String.valueOf(Integer.parseInt(datetimeArray[0].split("-")[1]) - 1);
        String lastweekstartdtstmp = Functions.converttomillisec(mm, datetimeArray[0].split("-")[2], datetimeArray[0].split("-")[0], datetimeArray[1].split(":")[0], datetimeArray[1].split(":")[1], datetimeArray[1].split(":")[2]);
        return lastweekstartdtstmp;
    }

    public String getLastWeekEnddtStamp(DateVO lastweek)
    {
        String datetimeArray1[] = lastweek.getEndDate().split(" ");
        String mm1 = String.valueOf(Integer.parseInt(datetimeArray1[0].split("-")[1]) - 1);
        String lastweekenddtstmp = Functions.converttomillisec(mm1, datetimeArray1[0].split("-")[2], datetimeArray1[0].split("-")[0], datetimeArray1[1].split(":")[0], datetimeArray1[1].split(":")[1], datetimeArray1[1].split(":")[2]);
        return lastweekenddtstmp;
    }

    public static String getJPYAmount(String amount)
    {
        double amt = Double.parseDouble(amount);
        double roundOff = Math.round(amt);
        int value = (int) roundOff;
        amount = String.valueOf(value);
        return amount.toString();
    }

    public HashMap payoutBalance(String memberid)
    {
        HashMap<String,String> balanceMap=new HashMap();
        try
        {

            Calendar calendar1 = Calendar.getInstance();
            calendar1.add(Calendar.DATE, 0);
            calendar1.set(Calendar.HOUR_OF_DAY, 0);
            calendar1.set(Calendar.MINUTE, 0);
            calendar1.set(Calendar.SECOND, 0);

            Calendar calendar2 = Calendar.getInstance();
            calendar2.add(Calendar.DATE, 0);
            calendar2.set(Calendar.HOUR_OF_DAY, 23);
            calendar2.set(Calendar.MINUTE, 59);
            calendar2.set(Calendar.SECOND, 59);

            Calendar calendar3 = Calendar.getInstance();
            calendar3.add(Calendar.DATE, -1);
            calendar3.set(Calendar.HOUR_OF_DAY, 0);
            calendar3.set(Calendar.MINUTE, 0);
            calendar3.set(Calendar.SECOND, 0);

            Calendar calendar4 = Calendar.getInstance();
            calendar4.add(Calendar.DATE, -1);
            calendar4.set(Calendar.HOUR_OF_DAY, 23);
            calendar4.set(Calendar.MINUTE, 59);
            calendar4.set(Calendar.SECOND, 59);

            long todaysStartTimeInSecs = calendar3.getTimeInMillis() / 1000;
            long endStartTimeInSecs = calendar4.getTimeInMillis() / 1000;

            long todaysStartTime = calendar1.getTimeInMillis() / 1000;
            long endStartTime = calendar2.getTimeInMillis() / 1000;

            String capt = getPayoutBalanceAmount(memberid);
            String payoutAmount = getPayoutAmount(todaysStartTime, endStartTime, memberid);
            log.error("capt= " + capt + " payoutAmout = " + payoutAmount);
            String currentPayoutBalance= String.valueOf(Double.parseDouble(capt)-Double.parseDouble(payoutAmount));
            balanceMap.put("totalPayoutBalance",capt);
            balanceMap.put("currentPayoutBalance",currentPayoutBalance);
        }
        catch (Exception e)
        {
            log.error("Exception:::::", e);

        }
     return balanceMap;
    }

    public String getPayoutBalanceAmount( String memeberId)
    {
        String payOutBalance    = "-9999.00";
        PreparedStatement psmt  = null;
        ResultSet resultSet     = null;
        Connection conn         = null;
        try
        {
            conn = Database.getRDBConnection();
            // double   percentage = getPayoutBalancePercetage(memeberId);
            // StringBuffer query  = new StringBuffer("SELECT SUM(tc.captureamount *0.6) FROM transaction_common tc WHERE toid= ?  AND tc.status  IN ('capturesuccess') AND tc.dtstamp > ? AND tc.dtstamp < ?");
            StringBuffer query = new StringBuffer("SELECT totalPayoutAmount FROM merchant_configuration WHERE memberid= ? ");
            psmt = conn.prepareStatement(query.toString());
            psmt.setString(1, memeberId);

            resultSet = psmt.executeQuery();
            log.error("getPayoutBalanceAmount Query " + psmt);

            while (resultSet.next())
            {
                payOutBalance = String.valueOf(resultSet.getDouble(1));
            }

        }
        catch (Exception e)
        {
            log.error("Exception:::::", e);
            Functions.NewShowConfirmation1("Error", "Internal System Error while getting PayoutBalance");
        }
        finally
        {
            Database.closeResultSet(resultSet);
            Database.closeConnection(conn);
        }
        return payOutBalance;
    }

    public String getPayoutAmount(long todaysStartTimeInSecs, long endStartTimeInSecs, String memeberId)
    {
        String payOutBalance = "0.00";
        PreparedStatement psmt = null;
        ResultSet resultSet = null;
        Connection conn = null;
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("SELECT SUM(tc.payoutamount) as payoutamount FROM transaction_common tc WHERE toid= ?  AND tc.status  IN ('payoutsuccessful') AND tc.dtstamp > ? AND tc.dtstamp < ?");
            psmt = conn.prepareStatement(query.toString());
            psmt.setString(1, memeberId);
            psmt.setString(2, String.valueOf(todaysStartTimeInSecs));
            psmt.setString(3, String.valueOf(endStartTimeInSecs));
            resultSet = psmt.executeQuery();
            log.debug("getPayoutAmount Query " + psmt);

            while (resultSet.next())
            {
                payOutBalance = String.valueOf(resultSet.getDouble("payoutamount"));
            }

        }
        catch (Exception e)
        {
            log.error("Exception:::::", e);
            Functions.NewShowConfirmation1("Error", "Internal System Error while getting PayoutBalance");
        }
        finally
        {
            Database.closeResultSet(resultSet);
            Database.closeConnection(conn);
        }
        return payOutBalance;
    }
    public void generateKey(String merchantid)
    {

        String pwdData      = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int len             = pwdData.length();
        Date date           = new Date();
        Random rand         = new Random(date.getTime());
        StringBuilder key   = new StringBuilder();
        int index = -1;
        for (int i = 0; i < 32; i++)
        {
            index = rand.nextInt(len);
            key.append(pwdData.substring(index, index + 1));
        }


        StringBuilder updQuery = new StringBuilder("update members");
        updQuery.append(" set clkey = ?  where memberid= ? " );
        Connection conn = null;
        PreparedStatement pstmt= null;

        try
        {
            conn = Database.getConnection();
            pstmt= conn.prepareStatement(updQuery.toString());
            pstmt.setString(1,key.toString());
            pstmt.setString(2,merchantid);
            pstmt.executeUpdate();
        }
        catch (SystemError se)
        {
            log.error("System  Error:::::", se);
            //System.out.println(se.toString());
        }
        catch (Exception e)
        {
            log.error("Error!", e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }

    }

    public  static String decodeStringToImage(String value,String  path)
    {

        BASE64Decoder decoder   = new BASE64Decoder();
        byte[] imgBytes         = new byte[0];
        try
        {
            imgBytes = decoder.decodeBuffer(value);
            BufferedImage bufImg    = ImageIO.read(new ByteArrayInputStream(imgBytes));
            File imgOutFile         = new File(path);
            ImageIO.write(bufImg, "png", imgOutFile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return path;

    }


    public static String getAmountLimitCheckDropDown(String check)
    {
        String str = "";
        if (check != null && check.equals("N"))
        {
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "</option>";
            str = str + "<option value=terminal_Level>Terminal Level</option>";
            str = str + "<option value=account_Level>Account Level</option>";
            str = str + "<option value=account_member_Level>Account Member Level</option>";
            str = str + "<option value=terminal_Level_based_on_cardtype>Terminal Level Based On Cardtype</option>";
        }
        else if (check != null && check.equals("terminal_Level"))
        {
            str = str + "<option value=N>N</option>";
            str = str + "<option value=" + check + " selected=\"selected\">Terminal Level</option>";
            str = str + "<option value=account_Level>Account Level</option>";
            str = str + "<option value=account_member_Level>Account Member Level</option>";
            str = str + "<option value=terminal_Level_based_on_cardtype>Terminal Level Based On Cardtype</option>";
        }
        else if (check != null && check.equals("account_Level"))
        {
            str = str + "<option value=N>N</option>";
            str = str + "<option value=terminal_Level>Terminal Level</option>";
            str = str + "<option value=" + check + " selected=\"selected\">Account Level</option>";
            str = str + "<option value=account_member_Level>Account Member Level</option>";
            str = str + "<option value=terminal_Level_based_on_cardtype>Terminal Level Based On Cardtype</option>";
        }else if(check != null && check.equals("terminal_Level_based_on_cardtype"))
        {
            str = str + "<option value=N>N</option>";
            str = str + "<option value=terminal_Level>Terminal Level</option>";
            str = str + "<option value=account_Level>Account Level</option>";
            str = str + "<option value=account_member_Level>Account Member Level</option>";
            str = str + "<option value=" + check + " selected=\"selected\">Terminal Level Based On Cardtype</option>";
        }else
        {
            str = str + "<option value=N>N</option>";
            str = str + "<option value=terminal_Level>Terminal Level</option>";
            str = str + "<option value=account_Level>Account Level</option>";

            str = str + "<option value=" + check + " selected=\"selected\">Account Member Level</option>";
            str = str + "<option value=terminal_Level_based_on_cardtype>Terminal Level Based On Cardtype</option>";
        }
        return str;
    }


}