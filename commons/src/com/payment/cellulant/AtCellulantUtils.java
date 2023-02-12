package com.payment.cellulant;

import com.directi.pg.Database;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.vo.ReserveField2VO;
import com.payment.common.core.*;
import com.payment.cybersource2.PKCS1EncodedKeySpec;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.Signature;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;

/**
 * Created by Admin on 11/27/2020.
 */
public class AtCellulantUtils
{
    private static TransactionLogger transactionLogger = new TransactionLogger(AtCellulantUtils.class.getName());
    private static HashMap<String,String> paymentBrandMap=new HashMap();
    private static HashMap<String,String> countryCodeHash=new HashMap<>();
    static {
        paymentBrandMap.put("ECOCASH","03");
        paymentBrandMap.put("TELECASH","04");

        countryCodeHash.put("ALA","AX");
        countryCodeHash.put("AFG","AF");
        countryCodeHash.put("ALB","AL");
        countryCodeHash.put("DZA","DZ");
        countryCodeHash.put("ASM","AS");
        countryCodeHash.put("AND","AD");
        countryCodeHash.put("AGO","AO");
        countryCodeHash.put("AIA","AI");
        countryCodeHash.put("ATA","AQ");
        countryCodeHash.put("ATG","AG");
        countryCodeHash.put("ARG","AR");
        countryCodeHash.put("ARM","AM");
        countryCodeHash.put("ABW","AW");
        countryCodeHash.put("AUS","AU");
        countryCodeHash.put("AUT","AT");
        countryCodeHash.put("AZE","AZ");
        countryCodeHash.put("BHS","BS");
        countryCodeHash.put("BHR","BH");
        countryCodeHash.put("BGD","BD");
        countryCodeHash.put("BRB","BB");
        countryCodeHash.put("BLR","BY");
        countryCodeHash.put("BEL","BE");
        countryCodeHash.put("BLZ","BZ");
        countryCodeHash.put("BEN","BJ");
        countryCodeHash.put("BMU","BM");
        countryCodeHash.put("BTN","BT");
        countryCodeHash.put("BOL","BO");
        countryCodeHash.put("BIH","BA");
        countryCodeHash.put("BWA","BW");
        countryCodeHash.put("BVT","BV");
        countryCodeHash.put("BRA","BR");
        countryCodeHash.put("IOT","IO");
        countryCodeHash.put("BRN","BN");
        countryCodeHash.put("BGR","BG");
        countryCodeHash.put("BFA","BF");
        countryCodeHash.put("BDI","BI");
        countryCodeHash.put("KHM","KH");
        countryCodeHash.put("CMR","CM");
        countryCodeHash.put("CAN","CA");
        countryCodeHash.put("CPV","CV");
        countryCodeHash.put("CYM","KY");
        countryCodeHash.put("CAF","CF");
        countryCodeHash.put("TCD","TD");
        countryCodeHash.put("CHL","CL");
        countryCodeHash.put("CHN","CN");
        countryCodeHash.put("CXR","CX");
        countryCodeHash.put("CCK","CC");
        countryCodeHash.put("COL","CO");
        countryCodeHash.put("COM","KM");
        countryCodeHash.put("COD","CD");
        countryCodeHash.put("COG","CG");
        countryCodeHash.put("COK","CK");
        countryCodeHash.put("CRI","CR");
        countryCodeHash.put("CIV","CI");
        countryCodeHash.put("HRV","HR");
        countryCodeHash.put("CUB","CU");
        countryCodeHash.put("CYP","CY");
        countryCodeHash.put("CZE","CZ");
        countryCodeHash.put("DNK","DK");
        countryCodeHash.put("DJI","DJ");
        countryCodeHash.put("DMA","DM");
        countryCodeHash.put("DOM","DO");
        countryCodeHash.put("ECU","EC");
        countryCodeHash.put("EGY","EG");
        countryCodeHash.put("SLV","SV");
        countryCodeHash.put("GNQ","GQ");
        countryCodeHash.put("ERI","ER");
        countryCodeHash.put("EST","EE");
        countryCodeHash.put("ETH","ET");
        countryCodeHash.put("FLK","FK");
        countryCodeHash.put("FRO","FO");
        countryCodeHash.put("FJI","FJ");
        countryCodeHash.put("FIN","FI");
        countryCodeHash.put("FRA","FR");
        countryCodeHash.put("GUF","GF");
        countryCodeHash.put("PYF","PF");
        countryCodeHash.put("ATF","TF");
        countryCodeHash.put("GAB","GA");
        countryCodeHash.put("GMB","GM");
        countryCodeHash.put("GEO","GE");
        countryCodeHash.put("DEU","DE");
        countryCodeHash.put("GHA","GH");
        countryCodeHash.put("GIB","GI");
        countryCodeHash.put("GRC","GR");
        countryCodeHash.put("GRL","GL");
        countryCodeHash.put("GRD","GD");
        countryCodeHash.put("GLP","GP");
        countryCodeHash.put("GUM","GU");
        countryCodeHash.put("GTM","GT");
        countryCodeHash.put("GIN","GN");
        countryCodeHash.put("GNB","GW");
        countryCodeHash.put("GUY","GY");
        countryCodeHash.put("HTI","HT");
        countryCodeHash.put("HMD","HM");
        countryCodeHash.put("HND","HN");
        countryCodeHash.put("HKG","HK");
        countryCodeHash.put("HUN","HU");
        countryCodeHash.put("ISL","IS");
        countryCodeHash.put("IND","IN");
        countryCodeHash.put("IDN","ID");
        countryCodeHash.put("IRN","IR");
        countryCodeHash.put("IRQ","IQ");
        countryCodeHash.put("IRL","IE");
        countryCodeHash.put("ISR","IL");
        countryCodeHash.put("ITA","IT");
        countryCodeHash.put("JAM","JM");
        countryCodeHash.put("JPN","JP");
        countryCodeHash.put("JOR","JO");
        countryCodeHash.put("KAZ","KZ");
        countryCodeHash.put("KEN","KE");
        countryCodeHash.put("KIR","KI");
        countryCodeHash.put("PRK","KP");
        countryCodeHash.put("KOR","KR");
        countryCodeHash.put("KWT","KW");
        countryCodeHash.put("KGZ","KG");
        countryCodeHash.put("LAO","LA");
        countryCodeHash.put("LVA","LV");
        countryCodeHash.put("LBN","LB");
        countryCodeHash.put("LSO","LS");
        countryCodeHash.put("LBR","LR");
        countryCodeHash.put("LBY","LY");
        countryCodeHash.put("LIE","LI");
        countryCodeHash.put("LTU","LT");
        countryCodeHash.put("LUX","LU");
        countryCodeHash.put("MAC","MO");
        countryCodeHash.put("MKD","MK");
        countryCodeHash.put("MDG","MG");
        countryCodeHash.put("MWI","MW");
        countryCodeHash.put("MYS","MY");
        countryCodeHash.put("MDV","MV");
        countryCodeHash.put("MLI","ML");
        countryCodeHash.put("MLT","MT");
        countryCodeHash.put("MHL","MH");
        countryCodeHash.put("MTQ","MQ");
        countryCodeHash.put("MRT","MR");
        countryCodeHash.put("MUS","MU");
        countryCodeHash.put("MYT","YT");
        countryCodeHash.put("MEX","MX");
        countryCodeHash.put("FSM","FM");
        countryCodeHash.put("MDA","MD");
        countryCodeHash.put("MCO","MC");
        countryCodeHash.put("MNG","MN");
        countryCodeHash.put("MSR","MS");
        countryCodeHash.put("MAR","MA");
        countryCodeHash.put("MOZ","MZ");
        countryCodeHash.put("MMR","MM");
        countryCodeHash.put("NAM","NA");
        countryCodeHash.put("NRU","NR");
        countryCodeHash.put("NPL","NP");
        countryCodeHash.put("NLD","NL");
        countryCodeHash.put("ANT","AN");
        countryCodeHash.put("NCL","NC");
        countryCodeHash.put("NZL","NZ");
        countryCodeHash.put("NIC","NI");
        countryCodeHash.put("NER","NE");
        countryCodeHash.put("NGA","NG");
        countryCodeHash.put("NIU","NU");
        countryCodeHash.put("NFK","NF");
        countryCodeHash.put("MNP","MP");
        countryCodeHash.put("NOR","NO");
        countryCodeHash.put("OMN","OM");
        countryCodeHash.put("PAK","PK");
        countryCodeHash.put("PLW","PW");
        countryCodeHash.put("PSE","PS");
        countryCodeHash.put("PAN","PA");
        countryCodeHash.put("PNG","PG");
        countryCodeHash.put("PRY","PY");
        countryCodeHash.put("PER","PE");
        countryCodeHash.put("PHL","PH");
        countryCodeHash.put("PCN","PN");
        countryCodeHash.put("POL","PL");
        countryCodeHash.put("PRT","PT");
        countryCodeHash.put("PRI","PR");
        countryCodeHash.put("QAT","QA");
        countryCodeHash.put("REU","RE");
        countryCodeHash.put("ROU","RO");
        countryCodeHash.put("RUS","RU");
        countryCodeHash.put("RWA","RW");
        countryCodeHash.put("SHN","SH");
        countryCodeHash.put("KNA","KN");
        countryCodeHash.put("LCA","LC");
        countryCodeHash.put("SPM","PM");
        countryCodeHash.put("VCT","VC");
        countryCodeHash.put("WSM","WS");
        countryCodeHash.put("SMR","SM");
        countryCodeHash.put("STP","ST");
        countryCodeHash.put("SAU","SA");
        countryCodeHash.put("SEN","SN");
        countryCodeHash.put("SCG","CS");
        countryCodeHash.put("SYC","SC");
        countryCodeHash.put("SLE","SL");
        countryCodeHash.put("SGP","SG");
        countryCodeHash.put("SVK","SK");
        countryCodeHash.put("SVN","SI");
        countryCodeHash.put("SLB","SB");
        countryCodeHash.put("SOM","SO");
        countryCodeHash.put("ZAF","ZA");
        countryCodeHash.put("SGS","GS");
        countryCodeHash.put("ESP","ES");
        countryCodeHash.put("LKA","LK");
        countryCodeHash.put("SDN","SD");
        countryCodeHash.put("SUR","SR");
        countryCodeHash.put("SJM","SJ");
        countryCodeHash.put("SWZ","SZ");
        countryCodeHash.put("SWE","SE");
        countryCodeHash.put("CHE","CH");
        countryCodeHash.put("SYR","SY");
        countryCodeHash.put("TWN","TW");
        countryCodeHash.put("TJK","TJ");
        countryCodeHash.put("TZA","TZ");
        countryCodeHash.put("THA","TH");
        countryCodeHash.put("TLS","TL");
        countryCodeHash.put("TGO","TG");
        countryCodeHash.put("TKL","TK");
        countryCodeHash.put("TON","TO");
        countryCodeHash.put("TTO","TT");
        countryCodeHash.put("TUN","TN");
        countryCodeHash.put("TUR","TR");
        countryCodeHash.put("TKM","TM");
        countryCodeHash.put("TCA","TC");
        countryCodeHash.put("TUV","TV");
        countryCodeHash.put("UGA","UG");
        countryCodeHash.put("UKR","UA");
        countryCodeHash.put("ARE","AE");
        countryCodeHash.put("GBR","GB");
        countryCodeHash.put("USA","US");
        countryCodeHash.put("UMI","UM");
        countryCodeHash.put("URY","UY");
        countryCodeHash.put("UZB","UZ");
        countryCodeHash.put("VUT","VU");
        countryCodeHash.put("VAT","VA");
        countryCodeHash.put("VEN","VE");
        countryCodeHash.put("VNM","VN");
        countryCodeHash.put("VGB","VG");
        countryCodeHash.put("VIR","VI");
        countryCodeHash.put("WLF","WF");
        countryCodeHash.put("ESH","EH");
        countryCodeHash.put("YEM","YE");
        countryCodeHash.put("ZMB","ZM");
        countryCodeHash.put("ZWE","ZW");
    }

    public static String doPostHTTPSURLConnectionClient(String strURL, String request,String authentication) throws PZTechnicalViolationException
    {
        String result = "";
        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(strURL);
        try
        {
            post.addRequestHeader("Signature", authentication);
            post.addRequestHeader("Content-Type", "application/json");
            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result = response;
        }
        catch (HttpException he)
        {
            transactionLogger.error("CyberSource2Utils:: HttpException-----", he);
            PZExceptionHandler.raiseTechnicalViolationException(AtCellulantUtils.class.getName(), "doPostHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.error("CyberSource2Utils:: IOException-----", io);
            PZExceptionHandler.raiseTechnicalViolationException(AtCellulantUtils.class.getName(), "doPostHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }
    public static String doPostHTTPSURLConnectionClient(String strURL, String request) throws PZTechnicalViolationException
    {
        String result = "";
        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(strURL);
        try
        {
            post.addRequestHeader("Content-Type", "application/json");
            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result = response;
        }
        catch (HttpException he)
        {
            transactionLogger.error("AtCellulantUtils:: HttpException-----", he);
            PZExceptionHandler.raiseTechnicalViolationException(AtCellulantUtils.class.getName(), "doPostHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.error("AtCellulantUtils:: IOException-----", io);
            PZExceptionHandler.raiseTechnicalViolationException(AtCellulantUtils.class.getName(), "doPostHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }
    public static String getSignature(String plainText,String privatePath) {
        BufferedReader br=null;
        try {
            transactionLogger.error("plainText---->"+plainText);
            File file = new File(privatePath);

            br = new BufferedReader(new FileReader(file));

            String privateKey = "", st;

            while ((st = br.readLine()) != null) {
                privateKey += st;
            }
            String realPK = privateKey.replaceAll("-----BEGIN RSA PRIVATE KEY-----", "")
                    .replaceAll("-----END RSA PRIVATE KEY-----", "")
                    .replaceAll("\\r\\n|\\r|\\n", "");

            byte[] keyBytes = Base64.getDecoder().decode(realPK);
            PKCS1EncodedKeySpec spec = new PKCS1EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(kf.generatePrivate(spec.getKeySpec()));
            signature.update(plainText.getBytes("UTF-8"));
            byte[] signedBytes = signature.sign();

            return Base64.getEncoder().encodeToString(signedBytes);
        } catch (Exception ex) {
            transactionLogger.error("Exception--->", ex);
        }finally
        {
            try
            {

                if(br!=null)
                    br.close();
            }
            catch (IOException e)
            {
                transactionLogger.error("IOException--->",e);
            }

        }
        return "";
    }
    public CommRequestVO getRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO = null;
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommMerchantVO merchantAccountVO = new CommMerchantVO();
        ReserveField2VO reserveField2VO = null;

        GatewayAccount account = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());

        addressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        addressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        addressDetailsVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        addressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
        addressDetailsVO.setIp(commonValidatorVO.getAddressDetailsVO().getIp());
        addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setState(commonValidatorVO.getAddressDetailsVO().getState());
        addressDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
        addressDetailsVO.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
        addressDetailsVO.setCustomerid( commonValidatorVO.getCustomerId());
        transactionLogger.debug("---1. customer id---"+addressDetailsVO.getCustomerid());

        //addressDetailsVO.setTmpl_currency("ACH");
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());
        transDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());
        transDetailsVO.setCardType(commonValidatorVO.getCardType());

        if(commonValidatorVO.getReserveField2VO()!=null)
        {
            reserveField2VO = new ReserveField2VO();
            reserveField2VO.setAccountType(commonValidatorVO.getReserveField2VO().getAccountType());
            reserveField2VO.setRoutingNumber(commonValidatorVO.getReserveField2VO().getRoutingNumber());
            reserveField2VO.setAccountNumber(commonValidatorVO.getReserveField2VO().getAccountNumber());
            reserveField2VO.setCheckNumber(commonValidatorVO.getReserveField2VO().getCheckNumber());
            reserveField2VO.setBankName(commonValidatorVO.getReserveField2VO().getBankName());
            reserveField2VO.setBankAddress(commonValidatorVO.getReserveField2VO().getBankAddress());
            reserveField2VO.setBankCity(commonValidatorVO.getReserveField2VO().getBankCity());
            reserveField2VO.setBankState(commonValidatorVO.getReserveField2VO().getBankState());
            reserveField2VO.setBankZipcode(commonValidatorVO.getReserveField2VO().getBankZipcode());
        }



        merchantAccountVO.setDisplayName(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        merchantAccountVO.setAliasName(commonValidatorVO.getMerchantDetailsVO().getSiteName());
        merchantAccountVO.setMerchantOrganizationName(commonValidatorVO.getMerchantDetailsVO().getCompany_name());
        merchantAccountVO.setPartnerSupportContactNumber(commonValidatorVO.getMerchantDetailsVO().getPartnerSupportContactNumber());
        merchantAccountVO.setSitename(commonValidatorVO.getMerchantDetailsVO().getSiteName());
        merchantAccountVO.setMerchantSupportNumber(commonValidatorVO.getMerchantDetailsVO().getTelNo());


        commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));

        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCommMerchantVO(merchantAccountVO);
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setReserveField2VO(reserveField2VO);

        return commRequestVO;
    }
    public static String getPaymentBrand(String cardType)
    {
        return paymentBrandMap.get(cardType);
    }

    public static String insertTransactionDetails(String trackingId,String accountNumber,String payerClientCode)
    {
        Connection con= null;
        String detailId="";
        try
        {
            con = Database.getConnection();
            String query = "INSERT INTO transaction_atcellulant_details (`trackingid`,`accountNumber`,`payerClientCode`) VALUES (?,?,?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1,trackingId);
            ps.setString(2,accountNumber);
            ps.setString(3,payerClientCode);
            transactionLogger.error("insertTransactionDetails---->"+ps);
            int i=ps.executeUpdate();
            if (i == 1)
            {
                ResultSet rs = ps.getGeneratedKeys();

                if (rs != null)
                {
                    while (rs.next())
                    {
                        detailId = String.valueOf(rs.getInt(1));
                    }
                }
            }

        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError->",systemError);
        }

        catch (SQLException e)
        {
            transactionLogger.error("SQLException->", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return detailId;
    }
    public static boolean updateTransactionDetails(String detailId,String checkoutRequestID,String chargeRequestID,String amountPaid)
    {
        Connection con= null;
        boolean isUpdate=false;
        try
        {
            con = Database.getConnection();
            String query = "update transaction_atcellulant_details set checkoutRequestID=?,chargeRequestID=?,amountPaid=? where detailid=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1,checkoutRequestID);
            ps.setString(2,chargeRequestID);
            ps.setString(3,amountPaid);
            ps.setString(4,detailId);
            transactionLogger.error("updateTransactionDetails---->"+ps);
            int i=ps.executeUpdate();
            if(i>0)
            {
                isUpdate=true;
            }

        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError->",systemError);
        }

        catch (SQLException e)
        {
            transactionLogger.error("SQLException->", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return isUpdate;
    }
    public static String getCountryTwoDigit(String countryCode)
    {
        return countryCodeHash.get(countryCode);
    }
}
