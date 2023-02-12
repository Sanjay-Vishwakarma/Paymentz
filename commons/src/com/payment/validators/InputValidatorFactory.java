package com.payment.validators;

import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.directi.pg.core.paymentgateway.CUPPaymentGateway;
import com.directi.pg.core.paymentgateway.PfsPaymentGateway;
import com.directi.pg.core.paymentgateway.QwipiPaymentGateway;
import com.payment.CashflowsCaibo.CashFlowsCaiboInputValidator;
import com.payment.CashflowsCaibo.CashFlowsCaiboPaymentGateway;
import com.payment.DusPayCard.DusPayCardPaymentGateway;
import com.payment.DusPayCard.DuspaycardInputValidator;
import com.payment.EPaySolution.EPaySolnInputValidator;
import com.payment.EPaySolution.EPaySolutionGateway;
import com.payment.Easebuzz.EaseBuzzPaymentGateway;
import com.payment.Easebuzz.EasebuzzInputValidator;
import com.payment.Ecommpay.EcommpayInputValidator;
import com.payment.Ecommpay.EcommpayPaymentGateway;
import com.payment.Gpay.GpayInputValidator;
import com.payment.Gpay.GpayPaymentzGateway;
import com.payment.LetzPay.LetzPayInputValidator;
import com.payment.LetzPay.LetzPayPaymentGateway;
import com.payment.Oculus.OculusInputValidator;
import com.payment.Oculus.OculusPaymentGateway;
import com.payment.PayEasyWorld.PayEasyWorldInputValidator;
import com.payment.PayEasyWorld.PayEasyWorldPaymentGateway;
import com.payment.PayMitco.core.PayMitcoInputValidator;
import com.payment.PayMitco.core.PayMitcoPaymentGateway;
import com.payment.ReitumuBank.core.ReitumuBankGatewayInputValidator;
import com.payment.ReitumuBank.core.ReitumuBankMerchantPaymentGateway;
import com.payment.ReitumuBank.core.ReitumuBankSMSPaymentGateway;
import com.payment.STS.core.STSInputValidator;
import com.payment.STS.core.STSPaymentGateway;
import com.payment.TWDTaiwan.TWDTaiwanInputValidator;
import com.payment.TWDTaiwan.TWDTaiwanPaymentGateway;
import com.payment.Triple000.Triple000InputValidator;
import com.payment.Triple000.Triple000PaymentGateway;
import com.payment.Wirecardnew.WireCardNPaymentGateway;
import com.payment.Wirecardnew.WirecardNinputValidator;
import com.payment.aamarpay.AamarPayInputValidator;
import com.payment.aamarpay.AamarPayPaymentGateway;
import com.payment.acqra.AcqraInputValidator;
import com.payment.acqra.AcqraPaymentGateway;
import com.payment.airpay.AirPayInputValidator;
import com.payment.airpay.AirpayPaymentGateway;
import com.payment.alphapay.AlphapayInputValidator;
import com.payment.alphapay.AlphapayPaymentGateway;
import com.payment.apco.core.ApcoPayInputValidator;
import com.payment.apco.core.ApcoPaymentGateway;
import com.payment.apcofastpayv6.ApcoFastPayV6InputValidator;
import com.payment.apcofastpayv6.ApcoFastPayV6PaymentGateway;
import com.payment.apexpay.ApexPayInputValidator;
import com.payment.apexpay.ApexPayPaymentGateway;
import com.payment.appletree.AppleTreeCellulantPaymentGateway;
import com.payment.appletree.AppletreeInputValidator;
import com.payment.auxpay_payment.core.AuxPayInputValidator;
import com.payment.auxpay_payment.core.AuxPayPaymentGateway;
import com.payment.b4payment.B4InputValidator;
import com.payment.b4payment.B4PaymentGateway;
import com.payment.bennupay.BennupayInputValidator;
import com.payment.bennupay.BennupayPaymentGateway;
import com.payment.billdesk.BillDeskPaymentGateway;
import com.payment.billdesk.BilldeskInputValidator;
import com.payment.bitclear.BitClearInputValidator;
import com.payment.bitclear.BitClearPaymentGateway;
import com.payment.bnmquick.BnmQuickInputValidator;
import com.payment.bnmquick.BnmQuickPaymentGateway;
import com.payment.boltpay.BoltPayPaymentGateway;
import com.payment.boltpay.BoltpayInputValidator;
import com.payment.cajarural.CajaRuralInputValidator;
import com.payment.cajarural.CajaRuralPaymentGateway;
import com.payment.cardpay.CardPayInputValidator;
import com.payment.cardpay.CardPayPaymentGateway;
import com.payment.cellulant.AtCellulantInputValidator;
import com.payment.cellulant.AtCellulantPaymentGateway;
import com.payment.clearsettle.ClearSettleInputValidator;
import com.payment.clearsettle.ClearSettlePaymentGateway;
import com.payment.cleveland.ClevelandInputValidator;
import com.payment.cleveland.ClevelandPaymentGateway;
import com.payment.continentPay.ContinentPayInputValidator;
import com.payment.continentPay.ContinentPayPaymentGateway;
import com.payment.cup.core.CupInputValidator;
import com.payment.curo.CuroInputValidator;
import com.payment.curo.CuroPaymentGateway;
import com.payment.cybersource2.AtCyberSourcePaymentGateway;
import com.payment.cybersource2.AtCybersourceInputValidator;
import com.payment.doku.DokuPaymentGateway;
import com.payment.doku.DokuPaymentInputValidator;
import com.payment.dvg_payment.DVGInputValidator;
import com.payment.dvg_payment.DVGPaymentGateway;
import com.payment.easypay.EasyPayInputValidator;
import com.payment.easypay.EasyPaymentGateway;
import com.payment.easypaymentz.EasyPaymentzInputValidator;
import com.payment.easypaymentz.EasyPaymentzPaymentGateway;
import com.payment.ecomprocessing.ECPPaymentGateway;
import com.payment.ecomprocessing.EcpInputValidator;
import com.payment.emax_high_risk.core.EmaxHighRiskPaymentGateway;
import com.payment.emax_high_risk.core.EmaxInputValidator;
import com.payment.emerchantpay.EMerchantPayInputValidator;
import com.payment.emerchantpay.EMerchantPayPaymentGateway;
import com.payment.epay.EpayInputValidator;
import com.payment.epay.EpayPaymentGateway;
import com.payment.euroeximbank.EuroEximBankInputVaidator;
import com.payment.euroeximbank.EuroEximBankPaymentGateway;
import com.payment.ezpaynow.EzPayNowInputValidator;
import com.payment.ezpaynow.EzPayNowPaymentGateway;
import com.payment.flexepin.FlexepinInputValidator;
import com.payment.flexepin.FlexepinPaymentGateway;
import com.payment.globalgate.GlobalGateInputValidator;
import com.payment.globalgate.GlobalGatePaymentGateway;
import com.payment.hdfc.HDFCPaymentGateway;
import com.payment.hdfc.HDFCPaymentGatewayInputValidator;
import com.payment.iMerchantPay.iMerchantInputValidator;
import com.payment.iMerchantPay.iMerchantPaymentGateway;
import com.payment.icici.INICICIInputValidator;
import com.payment.icici.INICICIPaymentGateway;
import com.payment.ilixium.IlixiumInputValidator;
import com.payment.ilixium.IlixiumPaymentGateway;
import com.payment.imoneypay.IMoneyPayInputValidator;
import com.payment.imoneypay.IMoneyPayPaymentGateway;
import com.payment.infipay.InfiPayInputValidator;
import com.payment.infipay.InfiPayPaymentGateway;
import com.payment.inpay.InPayInputValidator;
import com.payment.inpay.InPayPaymentGateway;
import com.payment.jeton.JetonInputValidator;
import com.payment.jeton.JetonPaymentGateway;
import com.payment.jeton.JetonVoucherPaymentGateway;
import com.payment.jeton.JetonWalletInputValidator;
import com.payment.kcp.KCPInputValidator;
import com.payment.kcp.KCPPaymentGateway;
import com.payment.libill_payment.core.LibillInputValidator;
import com.payment.libill_payment.core.LibillPaymentGateway;
import com.payment.luqapay.JetonCardInputValidator;
import com.payment.luqapay.JetonCardPaymentGateway;
import com.payment.lyra.LyraPaymentGateway;
import com.payment.neteller.NetellerInputValidator;
import com.payment.neteller.NetellerPaymentGateway;
import com.payment.octapay.OctapayInputValidator;
import com.payment.octapay.OctapayPaymentGateway;
import com.payment.omnipay.OmnipayInputValidator;
import com.payment.omnipay.OmnipayPaymentGateway;
import com.payment.boombill.BoomBillInputValidator;
import com.payment.boombill.BoomBillPaymentGateway;
import com.payment.onepay.OnePayInputValidator;
import com.payment.onepay.OnePayPaymentGateway;
import com.payment.p4.gateway.P4CreditCardPaymentGateway;
import com.payment.p4.gateway.P4DirectDebitPaymentGateway;
import com.payment.p4.gateway.P4PaymentGateway;
import com.payment.p4.validator.P4CreditCardInputValidator;
import com.payment.p4.validator.P4DirectDebitInputValidator;
import com.payment.p4.validator.P4InputValidator;
import com.payment.payGateway.core.PaygatewayInputValidator;
import com.payment.payGateway.core.PaygatewayPaymentGateway;
import com.payment.paySafeCard.PaySafeCardInputValidator;
import com.payment.paySafeCard.PaySafeCardPaymentGateway;
import com.payment.payaidpayments.PayaidInputValidator;
import com.payment.payaidpayments.PayaidPaymentGateway;
import com.payment.payforasia.core.PayforAsiaInputValidator;
import com.payment.payforasia.core.PayforasiaPaymentGateway;
import com.payment.payg.PayGInputValidator;
import com.payment.payg.PayGPaymentGateway;
import com.payment.paygsmile.PayGSmilePaymentGateway;
import com.payment.paygsmile.PaygSmileInputValidator;
import com.payment.payneteasy.PayneteasyGateway;
import com.payment.payneteasy.PayneteasyInputValidator;
import com.payment.payonOppwa.PayOnOppwaInputValidator;
import com.payment.payonOppwa.PayonOppwaPaymentGateway;
import com.payment.paytm.PayTMInputValidator;
import com.payment.paytm.PayTMPaymentGateway;
import com.payment.payu.PayUInputValidator;
import com.payment.payu.PayUPaymentGateway;
import com.payment.payvision.core.PayVisionPaymentGateway;
import com.payment.payvision.core.PayvisionInputValidator;
import com.payment.pbs.core.PbsInputValidator;
import com.payment.pbs.core.PbsPaymentGateway;
import com.payment.perfectmoney.PerfectMoneyInputValidator;
import com.payment.perfectmoney.PerfectMoneyPaymentGateway;
import com.payment.pfs.core.PFSInputValidator;
import com.payment.powercash21.PowerCash21InputValidator;
import com.payment.powercash21.PowerCash21PaymentGateway;
import com.payment.procesosmc.ProcesosMCInputValidator;
import com.payment.procesosmc.ProcesosMCPaymentGateway;
import com.payment.qikpay.QikpayInputValidator;
import com.payment.qikpay.QikpayPaymentGateway;
import com.payment.qikpayv2.QikPayV2InputValidator;
import com.payment.qikpayv2.QikPayV2PaymentGateway;
import com.payment.quickpayments.QuickPaymentsGateway;
import com.payment.quickpayments.QuickPaymentsInputValidator;
import com.payment.qwipi.core.QwipiInputValidator;
import com.payment.safechargeV2.SafeChargeV2InputValidator;
import com.payment.safechargeV2.SafeChargeV2PaymentGateway;
import com.payment.safexpay.SafexPayPaymentGateway;
import com.payment.safexpay.SafexpayInputValidator;
import com.payment.sbm.core.SBMInputValidator;
import com.payment.sbm.core.SBMPaymentGateway;
import com.payment.secureTrading.SecureTradingGateway;
import com.payment.secureTrading.SecureTradingInputValidator;
import com.payment.skrill.SkrillInputValidator;
import com.payment.skrill.SkrillPaymentGateway;
import com.payment.smartfastpay.SmartFastPayInputValidator;
import com.payment.smartfastpay.SmartFastPayPaymentGateway;
import com.payment.sofort.IdealInputValidator;
import com.payment.sofort.IdealPaymentGateway;
import com.payment.sofort.SofortInputValidator;
import com.payment.sofort.SofortPaymentGateway;
import com.payment.swiffy.SwiffyInputValidator;
import com.payment.swiffy.SwiffyPaymentGateway;
import com.payment.tigergateway.TigerGateWayInputValidator;
import com.payment.tigergateway.TigerGateWayPaymentGateway;
import com.payment.tigerpay.TigerPayInputValidator;
import com.payment.tigerpay.TigerPayPaymentGateway;
import com.payment.totalPay.TotalPayInputValidator;
import com.payment.totalPay.TotalPayPaymentGateway;
import com.payment.traxx.TRAXXInputValidator;
import com.payment.traxx.TRAXXPaymentGateway;
import com.payment.trustly.TrustlyInputValidator;
import com.payment.trustly.TrustlyPaymentGateway;
import com.payment.uPayGate.UPayGateInputValidator;
import com.payment.uPayGate.UPayGatePaymentGateway;
import com.payment.uba_mc.UBAMCInputValidator;
import com.payment.uba_mc.UBAMCPaymentGateway;
import com.payment.verve.VervePayInputValidator;
import com.payment.verve.VervePaymentGateway;
import com.payment.visaNet.VisaNetInputValidator;
import com.payment.visaNet.VisaNetPaymentGateway;
import com.payment.vouchermoney.VoucherMoneyInputValidator;
import com.payment.vouchermoney.VoucherMoneyPaymentGateway;
import com.payment.wealthpay.WealthPayGateway;
import com.payment.wealthpay.WealthPayInputValidator;
import com.payment.xcepts.XceptsInputValidator;
import com.payment.xcepts.XceptsPaymentGateway;
import com.payment.xpate.XPatePaymentGateway;
import com.payment.xpate.XpateInputValidator;
import com.payment.zhixinfu.ZhiXinfuInputValidator;
import com.payment.zhixinfu.ZhiXinfuPaymentGateway;
import com.payment.airtel.AirtelUgandaPaymentGateway;
import com.payment.airtel.AirtelUgandaInputValidator;
import com.payment.mtn.MTNUgandaPaymentGateway;
import com.payment.mtn.MTNUgandaInputValidator;
import com.payment.smartcode.SmartCodePayInputValidator;
import com.payment.smartcode.SmartCodePayPaymentGateway;


/*import com.payment.CBCG.CBCGInputValidator;
import com.payment.CBCG.CBCGPaymentGateway;*/

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 7/25/14
 * Time: 4:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class InputValidatorFactory
{

    public static AbstractInputValidator getInputValidatorInstance(Integer accountId)
    {
        return getRequestVOInstance(accountId);
    }

    public static CommonInputValidator  getRequestVOInstance(int accountId)
    {

        GatewayAccount gatewayAccount   = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
        GatewayType gatewayType         = GatewayTypeService.getGatewayType(gatewayAccount.getPgTypeId());
        String gateway                  = gatewayType.getGateway();
        switch (gateway)
        {
            case QwipiPaymentGateway.GATEWAY_TYPE:
                return new QwipiInputValidator();

            case PayforasiaPaymentGateway.GATEWAY_TYPE:
                return new PayforAsiaInputValidator();

            case PaygatewayPaymentGateway.GATEWAY_TYPE:
                return new PaygatewayInputValidator();

            case InPayPaymentGateway.GATEWAY_TYPE:
                return new InPayInputValidator();

            case CUPPaymentGateway.GATEWAY_TYPE:
                return new CupInputValidator();

            case PfsPaymentGateway.GATEWAY_TYPE:
                return new PFSInputValidator();

            case SofortPaymentGateway.GATEWAY_TYPE:
                return new SofortInputValidator();

            case IdealPaymentGateway.GATEWAY_TYPE:
                return new IdealInputValidator();

            case ReitumuBankSMSPaymentGateway.GATEWAY_TYPE:
                return new ReitumuBankGatewayInputValidator();

            case ReitumuBankMerchantPaymentGateway.GATEWAY_TYPE:
                return new ReitumuBankGatewayInputValidator();

            case PaySafeCardPaymentGateway.GATEWAY_TYPE:
                return new PaySafeCardInputValidator();

            case DVGPaymentGateway.GATEWAY_TYPE:
                return new DVGInputValidator();

            case EmaxHighRiskPaymentGateway.GATEWAY_TYPE:
                return new EmaxInputValidator();

            case STSPaymentGateway.GATEWAY_TYPE:
                return new STSInputValidator();

            case ProcesosMCPaymentGateway.GATEWAY_TYPE:
                return new ProcesosMCInputValidator();

            case VisaNetPaymentGateway.GATEWAY_TYPE:
                return new VisaNetInputValidator();

            case INICICIPaymentGateway.GATEWAY_TYPE:
                return new INICICIInputValidator();

            case P4PaymentGateway.GATEWAY_TYPE:
                return new P4InputValidator();

            case P4DirectDebitPaymentGateway.GATEWAY_TYPE:
                return new P4DirectDebitInputValidator();

            case LibillPaymentGateway.GATEWAY_TYPE:
                return new LibillInputValidator();

            case AuxPayPaymentGateway.GATEWAY_TYPE:
                return new AuxPayInputValidator();

            case PayMitcoPaymentGateway.GATEWAY_TYPE:
                return new PayMitcoInputValidator();

            case P4CreditCardPaymentGateway.GATEWAY_TYPE:
                return new P4CreditCardInputValidator();

            case PbsPaymentGateway.GATEWAY_TYPE:
                return new PbsInputValidator();

            case PayVisionPaymentGateway.GATEWAY_TYPE:
                return new PayvisionInputValidator();

            case ClearSettlePaymentGateway.GATEWAY_TYPE:
                return new ClearSettleInputValidator();

            case B4PaymentGateway.GATEWAY_TYPE:
                return new B4InputValidator();

            case SBMPaymentGateway.GATEWAY_TYPE:
                return new SBMInputValidator();

            case WireCardNPaymentGateway.GATEWAY_TYPE:
                return new WirecardNinputValidator();

            case ApcoPaymentGateway.GATEWAY_TYPE:
                return new ApcoPayInputValidator();

            case NetellerPaymentGateway.GATEWAY_TYPE:
                return new NetellerInputValidator();

            case UPayGatePaymentGateway.GATEWAY_TYPE:
                return new UPayGateInputValidator();

            case SkrillPaymentGateway.GATEWAY_TYPE:
                return new SkrillInputValidator();

            case JetonPaymentGateway.GATEWAY_TYPE:
                return new JetonWalletInputValidator();

            case JetonVoucherPaymentGateway.GATEWAY_TYPE:
                return new JetonInputValidator();

            case PerfectMoneyPaymentGateway.GATEWAY_TYPE:
                return new PerfectMoneyInputValidator();

            case VoucherMoneyPaymentGateway.GATEWAY_TYPE:
                return new VoucherMoneyInputValidator();

            case EpayPaymentGateway.GATEWAY_TYPE:
                return new EpayInputValidator();

            case TrustlyPaymentGateway.GATEWAY_TYPE:
                return new TrustlyInputValidator();

            case iMerchantPaymentGateway.GATEWAY_TYPE:
                return new iMerchantInputValidator();

            case CardPayPaymentGateway.GATEWAY_TYPE:
                return new CardPayInputValidator();

            case EPaySolutionGateway.GATEWAY_TYPE:
                return new EPaySolnInputValidator();

            case DusPayCardPaymentGateway.GATEWAY_TYPE:
                return new DuspaycardInputValidator();

            case AcqraPaymentGateway.GATEWAY_TYPE:
                return new AcqraInputValidator();

            case CuroPaymentGateway.GATEWAY_TYPE:
                return new CuroInputValidator();

            case SafexPayPaymentGateway.GATEWAY_TYPE:
                return new SafexpayInputValidator();

            case ECPPaymentGateway.GATEWAY_TYPE:
                return new EcpInputValidator();

            case BillDeskPaymentGateway.GATEWAY_TYPE:
                return new BilldeskInputValidator();

            case EMerchantPayPaymentGateway.GATEWAY_TYPE:
                return new EMerchantPayInputValidator();

            case EzPayNowPaymentGateway.GATEWAY_TYPE:
                return new EzPayNowInputValidator();

            case KCPPaymentGateway.GATEWAY_TYPE:
                return new KCPInputValidator();

            case SecureTradingGateway.GATEWAY_TYPE:
                return new SecureTradingInputValidator();

            case JetonCardPaymentGateway.GATEWAY_TYPE:
                return new JetonCardInputValidator();

            case IlixiumPaymentGateway.GATEWAY_TYPE:
                return new IlixiumInputValidator();

            case XPatePaymentGateway.GATEWAY_TYPE:
                return new XpateInputValidator();

            case EcommpayPaymentGateway.GATEWAY_TYPE:
                return new EcommpayInputValidator();

            case TotalPayPaymentGateway.GATEWAY_TYPE:
                return new TotalPayInputValidator();

            case PayonOppwaPaymentGateway.GATEWAY_TYPE:
                return new PayOnOppwaInputValidator();

            case SafeChargeV2PaymentGateway.GATEWAY_TYPE:
                return new SafeChargeV2InputValidator();

            case Triple000PaymentGateway.GATEWAY_TYPE:
                return new Triple000InputValidator();

            case ZhiXinfuPaymentGateway.GATEWAY_TYPE:
                return new ZhiXinfuInputValidator();

            case OctapayPaymentGateway.GATEWAY_TYPE:
                return new OctapayInputValidator();

            case AtCyberSourcePaymentGateway.GATEWAY_TYPE:
                return new AtCybersourceInputValidator();

            case TWDTaiwanPaymentGateway.GATEWAY_TYPE:
                return new TWDTaiwanInputValidator();

            case AtCellulantPaymentGateway.GATEWAY_TYPE:
                return new AtCellulantInputValidator();

            case QikpayPaymentGateway.GATEWAY_TYPE:
                return new QikpayInputValidator();

            case LetzPayPaymentGateway.GATEWAY_TYPE:
                return new LetzPayInputValidator();

            case VervePaymentGateway.GATEWAY_TYPE:
                return new VervePayInputValidator();

            case BennupayPaymentGateway.GATEWAY_TYPE:
                return new BennupayInputValidator();

           /* case KnoxPaymentGateway.GATEWAY_TYPE:
                return new KnoxInputValidator();*/

            /*case CBCGPaymentGateway.GATEWAY_TYPE:
                return new CBCGInputValidator();*/

           /* case ZotaPayGateway.GATEWAY_TYPE:
                return new ZotaPayInputValidator();*/

            case QuickPaymentsGateway.GATEWAY_TYPE:
                return new QuickPaymentsInputValidator();

            case PayneteasyGateway.GATEWAY_TYPE:
                return new PayneteasyInputValidator();

            case PayEasyWorldPaymentGateway.GATEWAY_TYPE:
                return new PayEasyWorldInputValidator();

            case EasyPaymentzPaymentGateway.GATEWAY_TYPE:
                return new EasyPaymentzInputValidator();

            case DokuPaymentGateway.GATEWAY_TYPE:
                return new DokuPaymentInputValidator();

            case OculusPaymentGateway.GATEWAY_TYPE:
                return new OculusInputValidator();

            case IMoneyPayPaymentGateway.GATEWAY_TYPE:
                return new IMoneyPayInputValidator();

            case WealthPayGateway.GATEWAY_TYPE:
                return new WealthPayInputValidator();

            case GlobalGatePaymentGateway.GATEWAY_TYPE:
                return new GlobalGateInputValidator();

            case AppleTreeCellulantPaymentGateway.GATEWAY_TYPE:
                return new AppletreeInputValidator();

            case PayUPaymentGateway.GATEWAY_TYPE:
                return new PayUInputValidator();

            case PayGPaymentGateway.GATEWAY_TYPE:
                return new PayGInputValidator();

            case ApexPayPaymentGateway.GATEWAY_TYPE:
                return new ApexPayInputValidator();

            case AirpayPaymentGateway.GATEWAY_TYPE:
                return new AirPayInputValidator();

            case ApcoFastPayV6PaymentGateway.GATEWAY_TYPE:
                return new ApcoFastPayV6InputValidator();

            case ClevelandPaymentGateway.GATEWAY_TYPE:
                return new ClevelandInputValidator();

            case QikPayV2PaymentGateway.GATEWAY_TYPE:
                return new QikPayV2InputValidator();

            case CashFlowsCaiboPaymentGateway.GATEWAY_TYPE:
                return new CashFlowsCaiboInputValidator();

            case EasyPaymentGateway.GATEWAY_TYPE:
                return new EasyPayInputValidator();

            case TigerPayPaymentGateway.GATEWAY_TYPE:
                return new TigerPayInputValidator();

            case BoltPayPaymentGateway.GATEWAY_TYPE:
                return new BoltpayInputValidator();

            case CajaRuralPaymentGateway.GATEWAY_TYPE:
                return new CajaRuralInputValidator();

            case TigerGateWayPaymentGateway.GATEWAY_TYPE:
                return new TigerGateWayInputValidator();

            case EuroEximBankPaymentGateway.GATEWAY_TYPE:
                return new EuroEximBankInputVaidator();

            case PayaidPaymentGateway.GATEWAY_TYPE:
                return new PayaidInputValidator();

            case OnePayPaymentGateway.GATEWAY_TYPE:
                return new OnePayInputValidator();

            case AamarPayPaymentGateway.GATEWAY_TYPE:
                return new AamarPayInputValidator();

            case OmnipayPaymentGateway.GATEWAY_TYPE:
                return new OmnipayInputValidator();

            case BoomBillPaymentGateway.GATEWAY_TYPE:
                return new BoomBillInputValidator();

            case PowerCash21PaymentGateway.GATEWAY_TYPE:
                return new PowerCash21InputValidator();

            case PayTMPaymentGateway.GATEWAY_TYPE:
                return new PayTMInputValidator();

            case LyraPaymentGateway.GATEWAY_TYPE:
                return new PayTMInputValidator();

            case AirtelUgandaPaymentGateway.GATEWAY_TYPE:
                return new AirtelUgandaInputValidator();

            case MTNUgandaPaymentGateway.GATEWAY_TYPE:
                return new MTNUgandaInputValidator();

            case HDFCPaymentGateway.GATEWAY_TYPE:
                return new HDFCPaymentGatewayInputValidator();

            case UBAMCPaymentGateway.GATEWAY_TYPE:
                return new UBAMCInputValidator();

            case EaseBuzzPaymentGateway.GATEWAY_TYPE:
                return new EasebuzzInputValidator();

            case GpayPaymentzGateway.GATEWAY_TYPE:
                return new GpayInputValidator();

            case ContinentPayPaymentGateway.GATEWAY_TYPE:
                return new ContinentPayInputValidator();

            case BitClearPaymentGateway.GATEWAY_TYPE:
                return new BitClearInputValidator();

            case XceptsPaymentGateway.GATEWAY_TYPE:
                return new XceptsInputValidator();

            case BnmQuickPaymentGateway.GATEWAY_TYPE:
                return new BnmQuickInputValidator();

            case FlexepinPaymentGateway.GATEWAY_TYPE:
                return new FlexepinInputValidator();

            case SmartCodePayPaymentGateway.GATEWAY_TYPE:
                return new SmartCodePayInputValidator();

            case SmartFastPayPaymentGateway.GATEWAY_TYPE:
                return new SmartFastPayInputValidator();

            case InfiPayPaymentGateway.GATEWAY_TYPE:
                return new InfiPayInputValidator();

            case PayGSmilePaymentGateway.GATEWAY_TYPE:
                return new PaygSmileInputValidator();

            case TRAXXPaymentGateway.GATEWAY_TYPE:
                return new TRAXXInputValidator();

            case SwiffyPaymentGateway.GATEWAY_TYPE:
                return new SwiffyInputValidator();

            case AlphapayPaymentGateway.GATEWAY_TYPE:
                return new AlphapayInputValidator();

            default:
                return new CommonInputValidator();
        }
    }}