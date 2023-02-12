package com.payment.exceptionHandler.operations;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 12/9/14
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class PZOperations
{
    public static final String DIRECTKIT_SALE = "Sale form Direct Kit";
    public static final String DIRECTKIT_STATUS = "Status form Direct Kit";
    public static final String DIRECTKIT_VOID = "Void form Direct Kit";
    public static final String DIRECTKIT_REFUND = "Refund form Direct Kit";
    public static final String DIRECTKIT_CAPTURE = "Capture form Direct Kit";

    public static final String STANDARDKIT_SALE = "Sale form Standard Kit";
    public static final String VT_SALE = "Sale form Virtual Terminal";

    public static final String TOKEN_SALE = "Sale Using Token";

    public static final String MERCHANT_REFUND = "Refund from Merchant Interface";
    public static final String ADMIN_REFUND = "Refund from Admin Interface";
    public static final String ADMIN_CHARGEBACK = "Chargeback from Admin Interface";
    public static final String PARTNER_CHARGEBACK = "Chargeback from Partner Interface";


    public static final String MERCHANT_PAYOUT = "Payout from Merchant Interface";

    //for Account Details tab under merchant
    public static final String TRANSACTION_SUMMARY = "Getting Transaction Summary";
    public static final String CHARGE_LIST ="Getting List of Charges";
    public static final String Wire_Reports = "Getting List of Wire reports";
    public static final String Account_summary ="Getting Account Summary Details";

    //member config Details
    public static  final String MEMBER_CONFIG = "Getting Member Configuration Details";

    //integration wise operations
    public static  final String PASPX_SALE = "Sale from PASPX Integration";
    public static  final String PASPX_REFUND = "Refund from PASPX Integration";

    public static  final String CASHFLOW_SALE = "Sale from CASHFLOW Integration";
    public static  final String CASHFLOW_REFUND = "Refund from CASHFLOW Integration";

    public static  final String PAYFORASIA_SALE = "Sale from PAYFORASIA Integration";
    public static  final String PAYFORASIA_REFUND = "Refund from PAYFORASIA Integration";
    public static  final String PAYFORASIA_INQUIRY = "Inquiry from PAYFORASIA Integration";

    public static  final String DVG_SALE = "Sale from DVG Integration";
    public static  final String DVG_REFUND = "Refund from DVG Integration";

    public static  final String STS_SALE = "Sale from STS Integration";

    public static  final String PAYGATEWAY_SALE = "Sale from Paygateway Integration";
    public static  final String PAYGATEWAY_REFUND = "Refund from Paygateway Refund";

    public static  final String QWIPI_SALE = "Sale from QWIPI Integration";
    public static  final String QWIPI_REFUND = "Refund from QWIPI Refund";
    public static  final String QWIPI_INQUIRY = "Inquiry from QWIPI Refund";

    public static  final String SAFECHARGE_SALE = "Sale from SafeCharge Integration";
    public static  final String SAFECHARGE_REFUND = "Refund from SafeCharge Integration";
    public static  final String SAFECHARGE_AUTH = "Authentication from SafeCharge Integration";
    public static  final String SAFECHARGE_VOID = "Void from SafeCharge Integration";
    public static  final String SAFECHARGE_CAPTURE = "Capture from SafeCharge Integration";

    public static  final String PAYWORLD_SALE = "Sale from PayWorld Integration";
    public static  final String PAYWORLD_REFUND = "Refund from PayWorld Integration";

    public static  final String BORGUN_SALE = "Sale from Borgun Integration";

    public static  final String PFS_SALE = "Sale from PFS Integration";
    public static  final String PFS_REFUND = "Refund from PFS Integration";
    public static  final String PFS_CONFIRM = "Confirm from PFS Integration";

    public static  final String CREDORAX_SALE = "Sale from CREDORAX Integration";
    public static  final String CREDORAX_AUTH = "Authentication from CREDORAX Integration";
    public static  final String CREDORAX_REFUND = "Refund from CREDORAX Integration";
    public static  final String CREDORAX_CAPTURE = "Capture from CREDORAX Integration";
    public static  final String CREDORAX_VOID = "Void from CREDORAX Integration";

    public static  final String WIRECARD_SALE = "Sale from WIRECARD Integration";
    public static  final String WIRECARD_AUTH = "Authentication from WIRECARD Integration";
    public static  final String WIRECARD_REFUND = "Refund from WIRECARD Integration";
    public static  final String WIRECARD_CAPTURE = "Capture from WIRECARD Integration";
    public static  final String WIRECARD_VOID = "Void from WIRECARD Integration";
    public static  final String WIRECARD_INQUIRY = "Inquiry from WIRECARD Integration";

    public static  final String GOLD24_SALE = "Sale from Gold24 Integration";
    public static  final String GOLD24_AUTH = "Authentication from Gold24 Integration";
    public static  final String GOLD24_REFUND = "Refund from Gold24 Integration";
    public static  final String GOLD24_CAPTURE = "Capture from Gold24 Integration";
    public static  final String GOLD24_VOID = "Void from Gold24 Integration";
    public static  final String GOLD24_INQUIRY = "Inquiry from Gold24 Integration";

    public static  final String PAYSAFECARD_AUTH = "Authentication from PaySafeCard Integration";
    public static  final String PAYSAFECARD_CAPTURE = "Capture from PaySafeCard Integration";

    public static  final String SOFORT_NOTIFICATION = "Notification for Sofort Integration";
    public static final String P4_NOTIFICATION = "Notification for P4 Integration";
    public static final String NETELLER_NOTIFICATION = "Notification for Neteller Integration";

    public static final String PROCESSING_CAPTURE_TRANSACTION="Capturing the transaction with processing gateway";
    public static  final String PROCESSING_SALE_TRANSACTION="Process Sale with Processing Gateway";
    public static final String PROCESSING_QUERY_TRANSACTION="Process Query with Processing Gateway";
    public static final String APPLICATION_MANAGER_UPLOAD="Uploading documents by the merchant for the application";
    public static final String MERCHANT_TOKEN="Merchant Token";


    public static final String ARENAPLUS_SALE="Sale from ArenaPlus Integration";
    public static final String ARENAPLUS_REFUND="Sale from ArenaPlus Refund";
    public static final String TRANSACTION_BORGUN_DETAILS=" Data from transaction_borgun_details";
    public static final String FRICKBANK_LAOD_ACCOUNT=" Loading gateway account details while of FrickBank";
    public static final String MYMONEDERO_LAOD_ACCOUNT=" Loading gateway account details while of MyMonedero";
    public static final String NMI_LAOD_ACCOUNT=" Loading gateway account details while of NMI";
    public static final String PAYDOLAR_LAOD_ACCOUNT=" Loading gateway account details while of PAY DOLLAR";
    public static final String PAYLINEVOUCHER_LAOD_ACCOUNT=" Loading gateway account details while of PAY LINE VOUCHER";
    public static final String SWIFFPAY_LAOD_ACCOUNT=" Loading gateway account details while of SWIFF PAY";
    public static final String UGS_LAOD_ACCOUNT=" Loading gateway account details while of UGS";
    public static final String BORGUN_MAKE_REQUEST="Exception while making request to Borgun";
    public static final String DIRECT_KIT_WEBSERVICE="RESTFul Web Service";
    public static final String RISK_RULE="Adding RISK Configuration";
    public static final String BUSINESS_RULE="Adding BUSINESS Configuration";
    public static final String RISKPROFILE="Exception During Riskprofile";
    public static final String BUSINESSPROFILE="Exception During Businessprofile";
    public static final String USERID="Exception while getting the User Profile";

    public static final String MANUAL_REBILL="Manual recurring billing";

}