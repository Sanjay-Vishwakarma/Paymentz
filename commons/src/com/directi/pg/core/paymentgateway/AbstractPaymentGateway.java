package com.directi.pg.core.paymentgateway;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.Mail;
import com.directi.pg.SystemError;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.handler.AbstractKitHandler;
import com.payment.Alweave.AlweavePaymentGateway;
import com.payment.CBCG.CBCGPaymentGateway;
import com.payment.CashflowsCaibo.CashFlowsCaiboPaymentGateway;
import com.payment.bitclear.BitClearPaymentGateway;
import com.payment.bnmquick.BnmQuickPaymentGateway;
import com.payment.continentPay.ContinentPayPaymentGateway;
import com.payment.Easebuzz.EaseBuzzPaymentGateway;
import com.payment.Ecommpay.EcommpayPaymentGateway;
import com.payment.Ecospend.EcospendPaymentGateway;
import com.payment.LetzPay.LetzPayPaymentGateway;
import com.payment.Oculus.OculusPaymentGateway;
import com.payment.PayEasyWorld.PayEasyWorldPaymentGateway;
import com.payment.Rubixpay.RubixpayPaymentGateway;
import com.payment.TWDTaiwan.TWDTaiwanPaymentGateway;
import com.payment.Triple000.Triple000PaymentGateway;
import com.payment.aamarpay.AamarPayPaymentGateway;
import com.payment.airpay.AirpayPaymentGateway;
import com.payment.apcofastpayv6.ApcoFastPayV6PaymentGateway;
import com.payment.apexpay.ApexPayPaymentGateway;
import com.payment.appletree.AppleTreeCellulantPaymentGateway;
import com.payment.asiancheckout.AsianCheckoutPaymentGateway;
import com.payment.bennupay.BennupayPaymentGateway;
import com.payment.cashfree.CashFreePaymentGateway;
import com.payment.cellulant.AtCellulantPaymentGateway;
import com.payment.cleveland.ClevelandPaymentGateway;
import com.payment.cybersource2.AtCyberSourcePaymentGateway;
import com.payment.doku.DokuPaymentGateway;
import com.payment.easypay.EasyPaymentGateway;
import com.payment.easypaymentz.EasyPaymentzPaymentGateway;
import com.payment.Gpay.GpayPaymentzGateway;
import com.payment.euroeximbank.EuroEximBankPaymentGateway;
import com.payment.fenige.FenigePaymentGateway;
import com.payment.flexepin.FlexepinPaymentGateway;
import com.payment.giftpay.GiftPayPaymentGateway;
import com.payment.globalgate.GlobalGatePaymentGateway;
import com.payment.globalpay.GlobalPayPaymentGateway;
import com.payment.hdfc.HDFCPaymentGateway;
import com.payment.imoneypay.IMoneyPayPaymentGateway;
import com.payment.infipay.InfiPayPaymentGateway;
import com.payment.ippopay.IppoPayPaymentGateway;
import com.payment.jpbanktransfer.JPBTPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.logicboxes.util.ApplicationProperties;
import com.logicboxes.util.Util;
import com.payment.DusPay.DusPayPaymentGateway;
import com.payment.DusPayCard.DusPayCardPaymentGateway;
import com.payment.EPaySolution.EPaySolutionGateway;
import com.payment.FlutterWave.FlutterWavePaymentGateway;
import com.payment.FrickBank.core.FrickBankPaymentGateWay;
import com.payment.KortaPay.KortaPayPaymentGateway;
import com.payment.acqra.AcqraPaymentGateway;
import com.payment.bhartiPay.BhartiPayPaymentGateway;
import com.payment.bitcoinpayget.BitcoinPaygatePaymentGateway;
import com.payment.cupUPI.UnionPayInternationalPaymentGateway;
import com.payment.curo.CuroPaymentGateway;
import com.payment.duspaydirectdebit.DusPayDDPaymentGateway;
import com.payment.ecomprocessing.ECPCPPaymentGateway;
import com.payment.ecomprocessing.ECPPaymentGateway;
import com.payment.emerchantpay.EMerchantPayPaymentGateway;
import com.payment.ezpaynow.EzPayNowPaymentGateway;
import com.payment.flwBarter.FlutterWaveBarterPaymentGateway;
import com.payment.ilixium.IlixiumPaymentGateway;
import com.payment.kcp.KCPPaymentGateway;
import com.payment.knox.KnoxPaymentGateway;
import com.payment.luqapay.JetonCardPaymentGateway;
import com.payment.lyra.LyraPaymentGateway;
import com.payment.nexi.NexiPaymentGateway;
import com.payment.OneRoadPayments.OneRoadPaymentGateway;
import com.payment.PBSAmex.PBSPaymentGatewayAmex;
import com.payment.PayMitco.core.PayMitcoPaymentGateway;
import com.payment.ReitumuBank.core.ReitumuBankMerchantPaymentGateway;
import com.payment.ReitumuBank.core.ReitumuBankSMSPaymentGateway;
import com.payment.STS.core.STSPaymentGateway;
import com.payment.Wirecardnew.WireCardNPaymentGateway;
import com.payment.allPay88.AllPay88PaymentGateway;
import com.payment.alliedwalled.core.AlliedWalletPaymentGateway;
import com.payment.apco.core.ApcoPaymentGateway;
import com.payment.apcoFastpay.ApcoFastpayPaymentGateway;
import com.payment.arenaplus.core.ArenaPlusPaymentGateway;
import com.payment.auxpay_payment.core.AuxPayPaymentGateway;
import com.payment.awepay.AwepayBundle.core.AwepayPaymentGateway;
import com.payment.b4payment.B4PaymentGateway;
import com.payment.bankone.BankonePaymentGateway;
import com.payment.beekash.BeekashPaymentGateway;
import com.payment.billdesk.BillDeskPaymentGateway;
import com.payment.borgun.core.BorgunPaymentGateway;
import com.payment.brd.BRDPaymentGateway;
import com.payment.cardinity.CardinityPaymentGateway;
import com.payment.cardpay.CardPayPaymentGateway;
import com.payment.cashflow.core.CashFlowPaymentGateway;
import com.payment.clearsettle.ClearSettleHPPGateway;
import com.payment.clearsettle.ClearSettlePaymentGateway;
import com.payment.clearsettle.ClearSettleVoucherPaymentGateway;
import com.payment.cybersource.CyberSourcePaymentGateway;
import com.payment.decta.core.DectaMerchantPaymentGateway;
import com.payment.decta.core.DectaSMSPaymentGateway;
import com.payment.dectaNew.DectaNewPaymentGateway;
import com.payment.dvg_payment.DVGPaymentGateway;
import com.payment.elegro.ElegroPaymentGateway;
import com.payment.emax_high_risk.core.EmaxHighRiskPaymentGateway;
import com.payment.emexpay.EmexpayPaymentGateway;
import com.payment.ems.core.EMSPaymentGateway;
import com.payment.epay.EpayPaymentGateway;
import com.payment.europay.core.EuroPayPaymentGateway;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.gold24.core.Gold24PaymentGateway;
import com.payment.iMerchantPay.iMerchantPaymentGateway;
import com.payment.icard.ICardPaymentGateway;
import com.payment.icici.INICICIPaymentGateway;
import com.payment.inpay.InPayPaymentGateway;
import com.payment.jeton.JetonPaymentGateway;
import com.payment.jeton.JetonVoucherPaymentGateway;
import com.payment.kotakbank.core.KotakAllPayCardPaymentGateway;
import com.payment.libill_payment.core.LibillPaymentGateway;
import com.payment.nestpay.NestPayPaymentGateway;
import com.payment.neteller.NetellerPaymentGateway;
import com.payment.ninja.NinjaWalletPaymentGateway;
import com.payment.npayon.NPayOnGateway;
import com.payment.octapay.OctapayPaymentGateway;
import com.payment.omnipay.OmnipayPaymentGateway;
import com.payment.boombill.BoomBillPaymentGateway;
import com.payment.infipay.InfiPayPaymentGateway;
import com.payment.onepay.OnePayPaymentGateway;
import com.payment.onlinepay.OnlinePayPaymentGateway;
import com.payment.opx.OPXPaymentGateway;
import com.payment.p4.gateway.P4CreditCardPaymentGateway;
import com.payment.p4.gateway.P4DirectDebitPaymentGateway;
import com.payment.p4.gateway.P4PaymentGateway;
import com.payment.paspx.PaspxPaymentGateway;
import com.payment.payFluid.PayFluidPaymentGateway;
import com.payment.payGateway.core.PaygatewayPaymentGateway;
import com.payment.paySafeCard.PaySafeCardPaymentGateway;
import com.payment.payVaultPro.PayVaultProPaymentGateway;
import com.payment.payaidpayments.PayaidPaymentGateway;
import com.payment.payboutique.PayBoutiquePaymentGateway;
import com.payment.payclub.PayClubPaymentGateway;
import com.payment.payeezy.PayeezyPaymentGateway;
import com.payment.payforasia.core.PayforasiaPaymentGateway;
import com.payment.payg.PayGPaymentGateway;
import com.payment.paygsmile.PayGSmilePaymentGateway;
import com.payment.payhost.PayHostPayGatePaymentGateway;
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
import com.payment.plmp.PLMPPaymentGateway;
import com.payment.powercash21.PowerCash21PaymentGateway;
import com.payment.procesosmc.ProcesosMCPaymentGateway;
import com.payment.processing.core.ProcessingPaymentGateway;
import com.payment.qikpayv2.QikPayV2PaymentGateway;
import com.payment.quickcard.QuickCardPaymentGateway;
import com.payment.quickpayments.QuickPaymentsGateway;
import com.payment.rave.RavePaymentGateway;
import com.payment.cajarural.CajaRuralPaymentGateway;
import com.payment.romcard.RomCardPaymentGateway;
import com.payment.rsp.RSPPaymentGateway;
import com.payment.sabadell.SabadellPaymentGateway;
import com.payment.safechargeV2.SafeChargeV2PaymentGateway;
import com.payment.safexpay.SafexPayPaymentGateway;
import com.payment.sbm.core.SBMPaymentGateway;
import com.payment.secureTrading.SecureTradingGateway;
import com.payment.skrill.SkrillPaymentGateway;
import com.payment.smartfastpay.SmartFastPayPaymentGateway;
import com.payment.sofort.IdealPaymentGateway;
import com.payment.sofort.SofortPaymentGateway;
import com.payment.swiffy.SwiffyPaymentGateway;
import com.payment.tapmio.TapMioPaymentGateway;
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
import com.payment.verve.VervePaymentGateway;
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
import com.payment.qikpay.QikpayPaymentGateway;
import com.payment.payneteasy.PayneteasyGateway;
import com.payment.boltpay.BoltPayPaymentGateway;
import com.payment.airtel.AirtelUgandaPaymentGateway;
import com.payment.mtn.MTNUgandaPaymentGateway;
import com.payment.transfr.TransfrPaymentGateway;
import com.payment.smartcode.SmartCodePayPaymentGateway;
import com.payment.alphapay.AlphapayPaymentGateway;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

//import com.payment.wirecard.core.WireCardPaymentGateway;

//import com.payment.b4payment.B4PaymentGateway;

//import com.payment.atlas.core.AtlasPaymentPaymentGateway;


/**
 * Created by IntelliJ IDEA.
 * User: jignesh.r
 * Date: Mar 1, 2007
 * Time: 3:22:06 PM
 * To change this template use File | Settings | File Templates.
 */

public abstract class AbstractPaymentGateway
{
    private static Logger log = new Logger(AbstractPaymentGateway.class.getName());
    public AbstractKitHandler gatewayHandler;
    protected String accountId;

    public static AbstractPaymentGateway getGateway(String accountId) throws SystemError
    {
        String gatewayType = GatewayAccountService.getGatewayAccount(accountId).getGateway();

        switch (gatewayType)
        {
            case SBMPaymentGateway.GATEWAY_TYPE:
                return new SBMPaymentGateway(accountId);

            case DVGPaymentGateway.GATEWAY_TYPE:
                return new DVGPaymentGateway(accountId);

            case PfsPaymentGateway.GATEWAY_TYPE:
                return new PfsPaymentGateway(accountId);

            case AlliedWalletPaymentGateway.GATEWAY_TYPE:
                return new AlliedWalletPaymentGateway(accountId);

            case PayforasiaPaymentGateway.GATEWAY_TYPE:
                return new PayforasiaPaymentGateway(accountId);

            case PayClubPaymentGateway.GATEWAY_TYPE:
                return new PayClubPaymentGateway(accountId);

            case VisaNetPaymentGateway.GATEWAY_TYPE:
                return new VisaNetPaymentGateway(accountId);

            case ProcesosMCPaymentGateway.GATEWAY_TYPE:
                return new ProcesosMCPaymentGateway(accountId);

            case QpPaymentGateway.GATEWAY_TYPE:
                return new QpPaymentGateway(accountId);

            case BorgunPaymentGateway.GATEWAY_TYPE:
                return new BorgunPaymentGateway(accountId);

            case ICICIPaymentGateway.GATEWAY_TYPE:
                return new ICICIPaymentGateway(accountId);

            case PayVTPaymentGateway.GATEWAY_TYPE:
                return new PayVTPaymentGateway(accountId);

            case PayLineVoucherGateway.GATEWAY_TYPE:
                return new PayLineVoucherGateway(accountId);

            case PayDollarPaymentGateway.GATEWAY_TYPE:
                return new PayDollarPaymentGateway(accountId);

            case EcorePaymentGateway.GATEWAY_TYPE:
                return new EcorePaymentGateway(accountId);

            case Pay132PaymentGateway.GATEWAY_TYPE:
                return new Pay132PaymentGateway(accountId);

            case Gold24PaymentGateway.GATEWAY_TYPE:
                return new Gold24PaymentGateway(accountId);

            case PayVisionPaymentGateway.GATEWAY_TYPE:
                return new PayVisionPaymentGateway(accountId);

            case WSecPaymentGateway.GATEWAY_TYPE:
                return new WSecPaymentGateway(accountId);

            case MyMonederoPaymentGateway.GATEWAY_TYPE:
                return new MyMonederoPaymentGateway(accountId);

            case UGSPaymentGateway.GATEWAY_TYPE_UGS:
                return new UGSPaymentGateway(accountId);

            case UGSPaymentGateway.GATEWAY_TYPE_FORT:
                return new UGSPaymentGateway(accountId);

            case DdpPaymentGateway.GATEWAY_TYPE:
                return new DdpPaymentGateway(accountId);

            case PayWorldPaymentGateway.GATEWAY_TYPE:
                return new PayWorldPaymentGateway(accountId);

            case CredoraxPaymentGateway.GATEWAY_TYPE:
                return new CredoraxPaymentGateway(accountId);

            case SwiffpayPaymentGateway.GATEWAY_TYPE:
                return new SwiffpayPaymentGateway(accountId);

            case EuroPayPaymentGateway.GATEWAY_TYPE:
                return new EuroPayPaymentGateway(accountId);

            case ArenaPlusPaymentGateway.GATEWAY_TYPE:
                return new ArenaPlusPaymentGateway(accountId);

            case NMIPaymentGateway.GATEWAY_TYPE:
                return new NMIPaymentGateway(accountId);

            case PayWorldPaymentGateway.GATEWAY_TYPE_RITUMU:
                return new PayWorldPaymentGateway(accountId);

            case DeltaPaymentGateway.GATEWAY_TYPE:
                return new DeltaPaymentGateway(accountId);

            case FrickBankPaymentGateWay.GATEWAY_TYPE:
                return new FrickBankPaymentGateWay(accountId);

            case LpbPaymentGateway.GATEWAY_TYPE:
                return new LpbPaymentGateway(accountId);

            case SafeChargePaymentGateway.GATEWAY_TYPE:
                return new SafeChargePaymentGateway(accountId);

            case GuardianPaymentGateway.GATEWAY_TYPE:
                return new GuardianPaymentGateway(accountId);

            case LibertyPaymentGateway.GATEWAY_TYPE:
                return new LibertyPaymentGateway(accountId);

            case FluzznetworkPaymentGateway.GATEWAY_TYPE:
                return new FluzznetworkPaymentGateway(accountId);

            case PaygatewayPaymentGateway.GATEWAY_TYPE:
                return new PaygatewayPaymentGateway(accountId);

            case IPAYDNAPaymentGateway.GATEWAY_TYPE:
                return new IPAYDNAPaymentGateway(accountId);

            case CUPPaymentGateway.GATEWAY_TYPE:
                return new CUPPaymentGateway(accountId);

            case InPayPaymentGateway.GATEWAY_TYPE:
                return new InPayPaymentGateway(accountId);

            case CashFlowPaymentGateway.GATEWAY_TYPE:
                return new CashFlowPaymentGateway(accountId);

            case PaspxPaymentGateway.GATEWAY_TYPE:
                return new PaspxPaymentGateway(accountId);

            case STSPaymentGateway.GATEWAY_TYPE:
                return new STSPaymentGateway(accountId);

            case PaySafeCardPaymentGateway.GATEWAY_TYPE:
                return new PaySafeCardPaymentGateway(accountId);

            case SofortPaymentGateway.GATEWAY_TYPE:
                return new SofortPaymentGateway(accountId);

            case IdealPaymentGateway.GATEWAY_TYPE:
                return new IdealPaymentGateway(accountId);

            case EmaxHighRiskPaymentGateway.GATEWAY_TYPE:
                return new EmaxHighRiskPaymentGateway(accountId);

            case ReitumuBankSMSPaymentGateway.GATEWAY_TYPE:
                return new ReitumuBankSMSPaymentGateway(accountId);

            case ReitumuBankMerchantPaymentGateway.GATEWAY_TYPE:
                return new ReitumuBankMerchantPaymentGateway(accountId);

            case ProcessingPaymentGateway.GATEWAY_TYPE:
                return new ProcessingPaymentGateway(accountId);

            case TwoGatePayPaymentGateway.GATEWAY_TYPE:
                return new TwoGatePayPaymentGateway(accountId);

            case OPXPaymentGateway.GATEWAY_TYPE:
                return new OPXPaymentGateway(accountId);

            case PayOnGateway.GATEWAY_TYPE_PAYON:
                return new PayOnGateway(accountId);

            case PayOnGateway.GATEWAY_TYPE_CATELLA:
                return new PayOnGateway(accountId);

            case INICICIPaymentGateway.GATEWAY_TYPE:
                return new INICICIPaymentGateway(accountId);

            case AuxPayPaymentGateway.GATEWAY_TYPE:
                return new AuxPayPaymentGateway(accountId);

            case LibillPaymentGateway.GATEWAY_TYPE:
                return new LibillPaymentGateway(accountId);

            case PayHostPayGatePaymentGateway.GATEWAY_TYPE:
                return new PayHostPayGatePaymentGateway(accountId);

            case BeekashPaymentGateway.GATEWAY_TYPE:
                return new BeekashPaymentGateway(accountId);

            case P4CreditCardPaymentGateway.GATEWAY_TYPE:
                return new P4CreditCardPaymentGateway(accountId);

            case P4PaymentGateway.GATEWAY_TYPE:
                return new P4PaymentGateway(accountId);

            case P4DirectDebitPaymentGateway.GATEWAY_TYPE:
                return new P4DirectDebitPaymentGateway(accountId);

            case PaySecPaymentGateway.GATEWAY_TYPE:
                return new PaySecPaymentGateway(accountId);

            case PayMitcoPaymentGateway.GATEWAY_TYPE:
                return new PayMitcoPaymentGateway(accountId);

            case TrustsPayPaymentGateway.GATEWAY_TYPE:
                return new TrustsPayPaymentGateway(accountId);

            case PbsPaymentGateway.GATEWAY_TYPE:
                return new PbsPaymentGateway(accountId);

            case WonderlandPayPaymentGateway.GATEWAY_TYPE:
                return new WonderlandPayPaymentGateway(accountId);

            case ApcoPaymentGateway.GATEWAY_TYPE:
                return new ApcoPaymentGateway(accountId);
            case TrustPayPaymentGateway.GATEWAY_TYPE:
                return new TrustPayPaymentGateway(accountId);

            case ClearSettlePaymentGateway.GATEWAY_TYPE:
                return new ClearSettlePaymentGateway(accountId);

            case B4PaymentGateway.GATEWAY_TYPE:
                return new B4PaymentGateway(accountId);

            case NetellerPaymentGateway.GATEWAY_TYPE:
                return new NetellerPaymentGateway(accountId);

            case WireCardNPaymentGateway.GATEWAY_TYPE:
                return new WireCardNPaymentGateway(accountId);

            case KotakAllPayCardPaymentGateway.GATEWAY_TYPE:
                return new KotakAllPayCardPaymentGateway(accountId);

            case SkrillPaymentGateway.GATEWAY_TYPE:
                return new SkrillPaymentGateway(accountId);

            case PerfectMoneyPaymentGateway.GATEWAY_TYPE:
                return new PerfectMoneyPaymentGateway(accountId);

            case DectaSMSPaymentGateway.GATEWAY_TYPE:
                return new DectaSMSPaymentGateway(accountId);

            case DectaMerchantPaymentGateway.GATEWAY_TYPE:
                return new DectaMerchantPaymentGateway(accountId);

            case VoucherMoneyPaymentGateway.GATEWAY_TYPE:
                return new VoucherMoneyPaymentGateway(accountId);

            case PaySpacePaymentGateway.GATEWAY_TYPE:
                return new PaySpacePaymentGateway(accountId);

            case UPayGatePaymentGateway.GATEWAY_TYPE:
                return new UPayGatePaymentGateway(accountId);

            case BillDeskPaymentGateway.GATEWAY_TYPE:
                return new BillDeskPaymentGateway(accountId);

            case JetonPaymentGateway.GATEWAY_TYPE:
                return new JetonPaymentGateway(accountId);

            case JetonVoucherPaymentGateway.GATEWAY_TYPE:
                return new JetonVoucherPaymentGateway(accountId);

            case BankonePaymentGateway.GATEWAY_TYPE:
                return new BankonePaymentGateway(accountId);

            case ClearSettleVoucherPaymentGateway.GATEWAY_TYPE:
                return new ClearSettleVoucherPaymentGateway(accountId);

            case EpayPaymentGateway.GATEWAY_TYPE:
                return new EpayPaymentGateway(accountId);

            case RavePaymentGateway.GATEWAY_TYPE:
                return new RavePaymentGateway(accountId);

            case TrustlyPaymentGateway.GATEWAY_TYPE:
                return new TrustlyPaymentGateway(accountId);

            case GCPPaymentGateway.GATEWAY_TYPE:
                return new GCPPaymentGateway(accountId);

            case PayneticsGateway.GATEWAY_TYPE:
                return new PayneticsGateway(accountId);

            case EmexpayPaymentGateway.GATEWAY_TYPE:
                return new EmexpayPaymentGateway(accountId);

            case EMSPaymentGateway.GATEWAY_TYPE:
                return new EMSPaymentGateway(accountId);

            case UnicreditPaymentGateway.GATEWAY_TYPE:
                return new UnicreditPaymentGateway(accountId);

            case TransactiumPaymentGateway.GATEWAY_TYPE:
                return new TransactiumPaymentGateway(accountId);

            case CardinityPaymentGateway.GATEWAY_TYPE:
                return new CardinityPaymentGateway(accountId);

            case ApcoFastpayPaymentGateway.GATEWAY_TYPE_APC0_ALDRAPAY:
                return new ApcoFastpayPaymentGateway(accountId);

            case ApcoFastpayPaymentGateway.GATEWAY_TYPE_APCO_PURPLEPAY:
                return new ApcoFastpayPaymentGateway(accountId);

            case RSPPaymentGateway.GATEWAY_TYPE:
                return new RSPPaymentGateway(accountId);

            case SafexPayPaymentGateway.GATEWAY_TYPE:
                return new SafexPayPaymentGateway(accountId);

            case NPayOnGateway.GATEWAY_TYPE:
                return new NPayOnGateway(accountId);

            case EPaySolutionGateway.GATEWAY_TYPE:
                return new EPaySolutionGateway(accountId);

            case TojikaPaymentGateway.GATEWAY_TYPE:
                return new TojikaPaymentGateway(accountId);

            case iMerchantPaymentGateway.GATEWAY_TYPE:
                return new iMerchantPaymentGateway(accountId);

            case NestPayPaymentGateway.GATEWAY_TYPE:
                return new NestPayPaymentGateway(accountId);

            case SecureTradingGateway.GATEWAY_TYPE:
                return new SecureTradingGateway(accountId);

            case PayBoutiquePaymentGateway.GATEWAY_TYPE:
                return new PayBoutiquePaymentGateway(accountId);

            case ECPPaymentGateway.GATEWAY_TYPE:
                return new ECPPaymentGateway(accountId);

            case XPatePaymentGateway.GATEWAY_TYPE:
                return new XPatePaymentGateway(accountId);

            case DusPayDDPaymentGateway.GATEWAY_TYPE:
                return new DusPayDDPaymentGateway(accountId);

            case UnionPayInternationalPaymentGateway.GATEWAY_TYPE:
                return new UnionPayInternationalPaymentGateway(accountId);

            case FlutterWavePaymentGateway.GATEWAY_TYPE:
                return new FlutterWavePaymentGateway(accountId);

            case BitcoinPaygatePaymentGateway.GATEWAY_TYPE:
                return new BitcoinPaygatePaymentGateway(accountId);

            case PBSPaymentGatewayAmex.GATEWAY_TYPE:
                return new PBSPaymentGatewayAmex(accountId);

            case DusPayPaymentGateway.GATEWAY_TYPE:
                return new DusPayPaymentGateway(accountId);

            case ZotapayPaymentGateway.GATEWAY_TYPE:
                return new ZotapayPaymentGateway(accountId);

            case TransactiumPaymentGateway.GATEWAY_TYPE_TRX:
                return new TransactiumPaymentGateway(accountId);

            case SabadellPaymentGateway.GATEWAY_TYPE:
                return new SabadellPaymentGateway(accountId);

            case NPaySecPaymentGateway.GATEWAY_TYPE:
                return new NPaySecPaymentGateway(accountId);

            case ClearSettleHPPGateway.GATEWAY_TYPE:
                return new ClearSettleHPPGateway(accountId);

            case CardPayPaymentGateway.GATEWAY_TYPE:
                return new CardPayPaymentGateway(accountId);

            case ApcoFastpayPaymentGateway.GATEWAY_TYPE_APCO_RAVE:
                return new ApcoFastpayPaymentGateway(accountId);

            case ApcoFastpayPaymentGateway.GATEWAY_TYPE_APCO_FAST_PAY:
                return new ApcoFastpayPaymentGateway(accountId);

            case TransactiumPaymentGateway.GATEWAY_TYPE_TRANSACTIUM_1:
                return new TransactiumPaymentGateway(accountId);

            case WLPaymentGateway.GATEWAY_TYPE_AGNIPAY:
                return new WLPaymentGateway(accountId);

            case WLPaymentGateway.GATEWAY_TYPE_TRANSACTWORLD:
                return new WLPaymentGateway(accountId);

            case WLPaymentGateway.GATEWAY_TYPE_SHIMOTOMO:
                return new WLPaymentGateway(accountId);

            case WLPaymentGateway.GATEWAY_TYPE_SAMPLEPSP:
                return new WLPaymentGateway(accountId);

            case WLPaymentGateway.GATEWAY_TYPE_FIDOMS:
                return new WLPaymentGateway(accountId);

            case AwepayPaymentGateway.GATEWAY_TYPE:
                return new AwepayPaymentGateway(accountId);

            case AllPay88PaymentGateway.GATEWAY_TYPE:
                return new AllPay88PaymentGateway(accountId);

            case CyberSourcePaymentGateway.GATEWAY_TYPE:
                return new CyberSourcePaymentGateway(accountId);

            case KortaPayPaymentGateway.GATEWAY_TYPE:
                return new KortaPayPaymentGateway(accountId);

            case PayVaultProPaymentGateway.GATEWAY_TYPE:
                return new PayVaultProPaymentGateway(accountId);

            case RomCardPaymentGateway.GATEWAY_TYPE:
                return new RomCardPaymentGateway(accountId);

            case ElegroPaymentGateway.GATEWAY_TYPE:
                return new ElegroPaymentGateway(accountId);

            case OneRoadPaymentGateway.GATEWAY_TYPE:
                return new OneRoadPaymentGateway(accountId);

            case BRDPaymentGateway.GATEWAY_TYPE:
                return new BRDPaymentGateway(accountId);

            case QuickCardPaymentGateway.GATEWAY_TYPE:
                return new QuickCardPaymentGateway(accountId);

            case PLMPPaymentGateway.GATEWAY_TYPE:
                return new PLMPPaymentGateway(accountId);

            case ICardPaymentGateway.GATEWAY_TYPE:
                return new ICardPaymentGateway(accountId);

            case PaySendPaymentGateway.GATEWAY_TYPE:
                return new PaySendPaymentGateway(accountId);

            case PaySendWalletPaymentGateway.GATEWAY_TYPE:
                return new PaySendWalletPaymentGateway(accountId);

            case NexiPaymentGateway.GATEWAY_TYPE:
                return new NexiPaymentGateway(accountId);

            case DectaNewPaymentGateway.GATEWAY_TYPE:
                return new DectaNewPaymentGateway(accountId);

            case DusPayCardPaymentGateway.GATEWAY_TYPE:
                return new DusPayCardPaymentGateway(accountId);

            case NinjaWalletPaymentGateway.GATEWAY_TYPE:
                return new NinjaWalletPaymentGateway(accountId);

            case VoguePayPaymentGateway.GATEWAY_TYPE:
                return new VoguePayPaymentGateway(accountId);

            case OnlinePayPaymentGateway.GATEWAY_TYPE:
                return new OnlinePayPaymentGateway(accountId);

            case PayeezyPaymentGateway.GATEWAY_TYPE:
                return new PayeezyPaymentGateway(accountId);

            case PhoneixPaymentGateway.GATEWAY_TYPE:
                return new PhoneixPaymentGateway(accountId);

            case PayvisionV2PaymentGateway.GATEWAY_TYPE:
                return  new PayvisionV2PaymentGateway(accountId);

            case TrueVoPaymentGateway.GATEWAY_TYPE:
                return  new TrueVoPaymentGateway(accountId);

            case AcqraPaymentGateway.GATEWAY_TYPE:
                return  new AcqraPaymentGateway(accountId);

            case CuroPaymentGateway.GATEWAY_TYPE:
                return  new CuroPaymentGateway(accountId);

            case BhartiPayPaymentGateway.GATEWAY_TYPE:
                return  new BhartiPayPaymentGateway(accountId);

            case FlutterWaveBarterPaymentGateway.GATEWAY_TYPE:
                return  new FlutterWaveBarterPaymentGateway(accountId);

            case ZotaPayGateway.GATEWAY_TYPE:
                return  new ZotaPayGateway(accountId);

            case KCPPaymentGateway.GATEWAY_TYPE:
                return new KCPPaymentGateway(accountId);

            case EMerchantPayPaymentGateway.GATEWAY_TYPE :
                return new EMerchantPayPaymentGateway(accountId);

            case EzPayNowPaymentGateway.GATEWAY_TYPE:
                return  new EzPayNowPaymentGateway(accountId);

            case JetonCardPaymentGateway.GATEWAY_TYPE:
                return  new JetonCardPaymentGateway(accountId);

            case IlixiumPaymentGateway.GATEWAY_TYPE:
                return  new IlixiumPaymentGateway(accountId);

            case ECPCPPaymentGateway.GATEWAY_TYPE:
                return  new ECPCPPaymentGateway(accountId);

            case JPBTPaymentGateway.GATEWAY_TYPE:
                return new JPBTPaymentGateway(accountId);

            case TapMioPaymentGateway.GATEWAY_TYPE:
                return new TapMioPaymentGateway(accountId);

            case RubixpayPaymentGateway.GATEWAY_TYPE:
                return new RubixpayPaymentGateway(accountId);

            case PayonOppwaPaymentGateway.GATEWAY_TYPE:
                return new PayonOppwaPaymentGateway(accountId);

            case EcommpayPaymentGateway.GATEWAY_TYPE:
                return new EcommpayPaymentGateway(accountId);

            case TotalPayPaymentGateway.GATEWAY_TYPE:
                return new TotalPayPaymentGateway(accountId);

            case KnoxPaymentGateway.GATEWAY_TYPE:
                return new KnoxPaymentGateway(accountId);

            case CBCGPaymentGateway.GATEWAY_TYPE:
                return new CBCGPaymentGateway(accountId);

            case SafeChargeV2PaymentGateway.GATEWAY_TYPE:
                return new SafeChargeV2PaymentGateway(accountId);

            case VervePaymentGateway.GATEWAY_TYPE:
                return new VervePaymentGateway(accountId);

            case IppoPayPaymentGateway.GATEWAY_TYPE:
                return new IppoPayPaymentGateway(accountId);

            case Triple000PaymentGateway.GATEWAY_TYPE:
                return new Triple000PaymentGateway(accountId);

            case FenigePaymentGateway.GATEWAY_TYPE:
                return new FenigePaymentGateway(accountId);

            case AlweavePaymentGateway.GATEWAY_TYPE:
                return new AlweavePaymentGateway(accountId);

            case ZhiXinfuPaymentGateway.GATEWAY_TYPE:
                return new ZhiXinfuPaymentGateway(accountId);

            case AtCyberSourcePaymentGateway.GATEWAY_TYPE:
                return new AtCyberSourcePaymentGateway(accountId);

            case OctapayPaymentGateway.GATEWAY_TYPE:
                return new OctapayPaymentGateway(accountId);

            case PayFluidPaymentGateway.GATEWAY_TYPE:
                return new PayFluidPaymentGateway(accountId);

            case TWDTaiwanPaymentGateway.GATEWAY_TYPE:
                return new TWDTaiwanPaymentGateway(accountId);

            case GlobalPayPaymentGateway.GATEWAY_TYPE:
                return new GlobalPayPaymentGateway(accountId);

            case TrustPayOppwaPaymentGateway.GATEWAY_TYPE:
                return new TrustPayOppwaPaymentGateway(accountId);

            case AtCellulantPaymentGateway.GATEWAY_TYPE:
                return new AtCellulantPaymentGateway(accountId);

            case QikpayPaymentGateway.GATEWAY_TYPE:
                return new QikpayPaymentGateway(accountId);

            case LetzPayPaymentGateway.GATEWAY_TYPE:
                return new LetzPayPaymentGateway(accountId);

            case GiftPayPaymentGateway.GATEWAY_TYPE:
                return new GiftPayPaymentGateway(accountId);

            case QuickPaymentsGateway.GATEWAY_TYPE:
                return new QuickPaymentsGateway(accountId);

            case BennupayPaymentGateway.GATEWAY_TYPE:
                return new BennupayPaymentGateway(accountId);

            case PayneteasyGateway.GATEWAY_TYPE:
                return new PayneteasyGateway(accountId);

            case PayEasyWorldPaymentGateway.GATEWAY_TYPE:
                return new PayEasyWorldPaymentGateway(accountId);

            case DokuPaymentGateway.GATEWAY_TYPE:
                return new DokuPaymentGateway(accountId);

            case AsianCheckoutPaymentGateway.GATEWAY_TYPE:
                return new AsianCheckoutPaymentGateway(accountId);

            case CashFreePaymentGateway.GATEWAY_TYPE:
                return new CashFreePaymentGateway(accountId);

            case OculusPaymentGateway.GATEWAY_TYPE:
                return new OculusPaymentGateway(accountId);

            case EasyPaymentzPaymentGateway.GATEWAY_TYPE:
                return new EasyPaymentzPaymentGateway(accountId);

            case IMoneyPayPaymentGateway.GATEWAY_TYPE:
                return new IMoneyPayPaymentGateway(accountId);

            case GpayPaymentzGateway.GATEWAY_TYPE:
                return new GpayPaymentzGateway(accountId);

            case AppleTreeCellulantPaymentGateway.GATEWAY_TYPE:
                return new AppleTreeCellulantPaymentGateway(accountId);

            case GlobalGatePaymentGateway.GATEWAY_TYPE:
                return new GlobalGatePaymentGateway(accountId);

            case ApcoFastPayV6PaymentGateway.GATEWAY_TYPE:
                return new ApcoFastPayV6PaymentGateway(accountId);


            case PayUPaymentGateway.GATEWAY_TYPE:
                return new PayUPaymentGateway(accountId);

            case EcospendPaymentGateway.GATEWAY_TYPE:
                return new EcospendPaymentGateway(accountId);

            case WealthPayGateway.GATEWAY_TYPE:
            return new WealthPayGateway(accountId);

            case PayGPaymentGateway.GATEWAY_TYPE:
                return new PayGPaymentGateway(accountId);

            case ApexPayPaymentGateway.GATEWAY_TYPE:
                return new ApexPayPaymentGateway(accountId);

            case AirpayPaymentGateway.GATEWAY_TYPE:
                return new AirpayPaymentGateway(accountId);

            case ClevelandPaymentGateway.GATEWAY_TYPE:
                return new ClevelandPaymentGateway(accountId);

            case QikPayV2PaymentGateway.GATEWAY_TYPE:
                return new QikPayV2PaymentGateway(accountId);

            case CashFlowsCaiboPaymentGateway.GATEWAY_TYPE:
                return new CashFlowsCaiboPaymentGateway(accountId);

            case EasyPaymentGateway.GATEWAY_TYPE:
                return new EasyPaymentGateway(accountId);
            case TigerPayPaymentGateway.GATEWAY_TYPE:
                return new TigerPayPaymentGateway(accountId);

            case BoltPayPaymentGateway.GATEWAY_TYPE:
                return new BoltPayPaymentGateway(accountId);

            case CajaRuralPaymentGateway.GATEWAY_TYPE:
                return new CajaRuralPaymentGateway(accountId);

            case TigerGateWayPaymentGateway.GATEWAY_TYPE:
                return new TigerGateWayPaymentGateway(accountId);

            case PayaidPaymentGateway.GATEWAY_TYPE:
                return new PayaidPaymentGateway(accountId);

            case OnePayPaymentGateway.GATEWAY_TYPE:
                return new OnePayPaymentGateway(accountId);

            case AamarPayPaymentGateway.GATEWAY_TYPE:
                return new AamarPayPaymentGateway(accountId);

            case EuroEximBankPaymentGateway.GATEWAY_TYPE:
                return new EuroEximBankPaymentGateway(accountId);

            case OmnipayPaymentGateway.GATEWAY_TYPE:
                return new OmnipayPaymentGateway(accountId);

            case BoomBillPaymentGateway.GATEWAY_TYPE:
                return new BoomBillPaymentGateway(accountId);

            case LyraPaymentGateway.GATEWAY_TYPE:
                 return new LyraPaymentGateway(accountId);

            case PowerCash21PaymentGateway.GATEWAY_TYPE:
                return new PowerCash21PaymentGateway(accountId);

            case PayTMPaymentGateway.GATEWAY_TYPE:
                return new PayTMPaymentGateway(accountId);

            case AirtelUgandaPaymentGateway.GATEWAY_TYPE:
                return new AirtelUgandaPaymentGateway(accountId);

            case MTNUgandaPaymentGateway.GATEWAY_TYPE:
                return new MTNUgandaPaymentGateway(accountId);

            case HDFCPaymentGateway.GATEWAY_TYPE:
                return new HDFCPaymentGateway(accountId);

            case UBAMCPaymentGateway.GATEWAY_TYPE:
                return new UBAMCPaymentGateway(accountId);

            case EaseBuzzPaymentGateway.GATEWAY_TYPE:
                return new EaseBuzzPaymentGateway(accountId);

            case ContinentPayPaymentGateway.GATEWAY_TYPE:
                return new ContinentPayPaymentGateway(accountId);

            case TransfrPaymentGateway.GATEWAY_TYPE:
                return new TransfrPaymentGateway(accountId);

           case BitClearPaymentGateway.GATEWAY_TYPE:
                return new BitClearPaymentGateway(accountId);

            case XceptsPaymentGateway.GATEWAY_TYPE:
                return new XceptsPaymentGateway(accountId);

            case BnmQuickPaymentGateway.GATEWAY_TYPE:
                return new BnmQuickPaymentGateway(accountId);

            case FlexepinPaymentGateway.GATEWAY_TYPE:
                return new FlexepinPaymentGateway(accountId);

            case SmartCodePayPaymentGateway.GATEWAY_TYPE:
                return new SmartCodePayPaymentGateway(accountId);

            case SmartFastPayPaymentGateway.GATEWAY_TYPE:
                return new SmartFastPayPaymentGateway(accountId);

            case InfiPayPaymentGateway.GATEWAY_TYPE:
                return new InfiPayPaymentGateway(accountId);

            case PayGSmilePaymentGateway.GATEWAY_TYPE:
                return new PayGSmilePaymentGateway(accountId);

            case TRAXXPaymentGateway.GATEWAY_TYPE:
                return new TRAXXPaymentGateway(accountId);

            case SwiffyPaymentGateway.GATEWAY_TYPE:
                return new SwiffyPaymentGateway(accountId);

            case AlphapayPaymentGateway.GATEWAY_TYPE:
                return new AlphapayPaymentGateway(accountId);

            /*case UPIQRPaymentGateway.GATEWAY_TYPE:
                return new UPIQRPaymentGateway(accountId);*/

            default:
                throw new SystemError("Couldn't find a Valid Gateway");

        }

    }
//todo
    public static void autoCaptureCron() throws SystemError
    {
        log.info("Entering autoCapture Cron");
        List<GatewayAccount> allAccounts = getAccounts();
        captureTransactions(allAccounts);
    }

    public static List<GatewayAccount> getAccounts()
    {
        Hashtable<String, GatewayAccount> merchants = GatewayAccountService.getMerchants();
        List<GatewayAccount> allAccounts = new ArrayList<GatewayAccount>();
        for (Map.Entry merchant : merchants.entrySet())
        {
            GatewayAccount account = (GatewayAccount) merchant.getValue();
            allAccounts.add(account);
        }
        return allAccounts;
    }

    private static void captureTransactions(List<GatewayAccount> accounts) throws SystemError
    {


        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            for (GatewayAccount account : accounts)
            {
                String accountId = String.valueOf(account.getAccountId());
                String gateway = String.valueOf(account.getGateway());
                AbstractPaymentGateway pg = getGateway(accountId);
                String selectStuckTransactionsQuery = "select * from transaction_icicicredit where status = 'capturestarted' and accountid = " + accountId;
                log.debug("Select Stuck Transactiosn Query :" + selectStuckTransactionsQuery);
                ResultSet rs = Database.executeQuery(selectStuckTransactionsQuery, conn);
                while (rs.next())
                {
                    try
                    {
                        String trackingId = rs.getString("icicitransid");
                        String captureAmount = rs.getString("captureamount");
                        String amount = rs.getString("amount");


                        Hashtable statusDetails = pg.getStatus(trackingId);
                        log.debug("Status details from gateway " + statusDetails);
                        String status = (String) statusDetails.get("status");

                        if ("captured".equalsIgnoreCase(status))
                        {
                            if (amount.equalsIgnoreCase(captureAmount))
                            {
                                log.info("Captured on gateway with the details we had sent update details in DB");
                                String captureId = (String) statusDetails.get("id");
                                String captureCode = (String) statusDetails.get("code");
                                String captureRRN = (String) statusDetails.get("receiptno");
                                String captureQsiResponseCode = (String) statusDetails.get("qsiresponsecode");
                                String captureQsiResponseDesc = (String) statusDetails.get("qsiresponsedesc");
                                Hashtable captureDetails = pg.processResultHashforCapture(captureId, captureCode, captureRRN, captureQsiResponseCode, captureQsiResponseDesc);
                                updateCaptureDetails(trackingId, captureDetails, conn);


                            }
                        }
                        else if ("approved".equalsIgnoreCase(status))
                        {
                            log.info("Since transaction not captured capture it now");

                            String authId = rs.getString("authid");
                            String authcode = rs.getString("authcode");
                            String authRRN = rs.getString("authreceiptno");
                            Hashtable captureDetails = pg.processCapture(trackingId, captureAmount, authId, authcode, authRRN);
                            updateCaptureDetails(trackingId, captureDetails, conn);


                        }
                        else
                        {
                            Mail.sendAdminMail("Error while autoCapture Cron", " Status in DB is 'capturestarted' while status in " + gateway + " is " + status + " Tracking Id :" + trackingId);
                        }
                    }
                    catch (SQLException e)
                    {
                        Mail.sendAdminMail("Error while autoCapture Cron", " Stack Trace " + Util.getStackTrace(e));
                    }
                    catch (SystemError se)
                    {
                        Mail.sendAdminMail("Error while autoCapture Cron", " Stack Trace " + Util.getStackTrace(se));
                    }

                }
            }


        }
        catch (SQLException e)
        {
            throw new SystemError("SQLException " + Util.getStackTrace(e));
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }

    private static void updateCaptureDetails(String trackingId, Hashtable details, Connection conn) throws SystemError
    {
        log.info("Updating Capture Details");
        String captureId = (String) details.get("captureid");
        String captureCode = (String) details.get("capturecode");
        String captureRRN = (String) details.get("capturereceiptno");
        String captureQsiResponseCode = (String) details.get("captureqsiresponsecode");
        String captureQsiResponseDesc = (String) details.get("captureqsiresponsedesc");
        String query = "";
        if (("0").equals(captureQsiResponseCode))
        {
            log.debug("Transaction captured successfully for trackingid--" + trackingId);
            query = "update transaction_icicicredit set captureqsiresponsecode='" + captureQsiResponseCode + "' ,captureqsiresponsedesc='" + captureQsiResponseDesc + "' ,captureId='" + captureId + "',captureCode='" + captureCode + "',capturereceiptno='" + captureRRN + "',status='capturesuccess',captureresult='Capture Done using Cron.' where icicitransid=" + trackingId + " and status in ('proofrequired','authsuccessful','capturestarted')";
        }
        else
        {
            log.debug("Error while capturing for trackingid--" + trackingId);
            query = "update transaction_icicicredit set captureqsiresponsecode='" + captureQsiResponseCode + "' ,captureqsiresponsedesc='" + captureQsiResponseDesc + "' ,captureId='" + captureId + "',captureCode='" + captureCode + "',capturereceiptno='" + captureRRN + "' where icicitransid=" + trackingId + " and status in ('proofrequired','authsuccessful','capturestarted')";
        }
        Database.executeUpdate(query, conn);
    }

    public static void autoCancelCron() throws SystemError
    {
        log.info("Entering autoCancel Cron ");
        List<GatewayAccount> allAccounts = getAccounts();
        cancelTransactions(allAccounts);
    }

    private static void cancelTransactions(List<GatewayAccount> accounts) throws SystemError
    {
        Connection conn = null;
        //String fromAddress = ApplicationProperties.getProperty("COMPANY_FROM_ADDRESS");
        String adminEmail = ApplicationProperties.getProperty("COMPANY_ADMIN_EMAIL");
        try
        {
            conn = Database.getConnection();
            for (GatewayAccount account : accounts)
            {
                String accountId = String.valueOf(account.getAccountId());
                String gateway              = String.valueOf(account.getGatewayName());
                AbstractPaymentGateway pg = getGateway(accountId);
                //(unix_timestamp(now())-dtstamp)/(24*60*60)
                //String selectStuckTransactionsQuery = "select * from transaction_icicicredit T,members M where T.toid=M.memberid and T.status in ('authsuccessful','proofrequired') and (TO_DAYS(now())-TO_DAYS(from_unixtime(T.dtstamp))) >" + pg.getMaxWaitDays() + " and T.accountid = " + accountId;
                String selectStuckTransactionsQuery = "select * from transaction_icicicredit T,members M where T.toid=M.memberid and T.status in ('authsuccessful','proofrequired') and (unix_timestamp(now())-T.dtstamp)/(24*60*60) >" + pg.getMaxWaitDays() + " and T.accountid = " + accountId;
                log.debug("Select Stuck Transactiosn Query :" + selectStuckTransactionsQuery);
                ResultSet rs = Database.executeQuery(selectStuckTransactionsQuery, conn);
                while (rs.next())
                {
                    try
                    {
                        String trackingId = rs.getString("icicitransid");
                        String amount = rs.getString("amount");
                        String description = rs.getString("description");
                        String status = rs.getString("status");
                        String notifyEmail = rs.getString("notifyemail");

                        cancelTransactionOnGateway(trackingId, accountId, conn);

                        StringBuffer mailBody = new StringBuffer();
                        mailBody.append("<html><body>Following Transaction was on status " + status + " and has been CANCELLED as it was not CAPTURED at the end of " + pg.getMaxWaitDays() + " days<br><br>");
                        mailBody.append("<table><tr><td>TrackingId</td><td>Amount</td><td>Description</td></tr>");
                        mailBody.append("<tr><td>" + trackingId + "</td><td>" + amount + "</td><td>" + description + "</td></tr>");
                        mailBody.append("</table></body></html>");
                        //Mail.sendHtmlMail(notifyEmail, fromAddress, null, adminEmail, " Transactions Cancelled ", mailBody.toString());
                    }
                    catch (SQLException e)
                    {
                        Mail.sendAdminMail("Error while autoCancel Cron", " Stack Trace " + Util.getStackTrace(e));
                    }
                    catch (SystemError se)
                    {
                        Mail.sendAdminMail("Error while autoCancel Cron", " Stack Trace " + Util.getStackTrace(se));
                    }
                }
            }
        }
        catch (SQLException e)
        {
            throw new SystemError("SQLException " + Util.getStackTrace(e));
        }
        finally
        {
            Database.closeConnection(conn);
        }

    }

    public static void cancelTransactionOnGateway(String trackingId, String accountId, Connection conn)
            throws SystemError
    {
        AbstractPaymentGateway pg = AbstractPaymentGateway.getGateway(accountId);
        String gateway = pg.getGateway();
        Hashtable statusDetails = pg.getStatus(trackingId);
        log.debug("Status details" + statusDetails);
        String status = (String) statusDetails.get("status");
        if ("voided".equalsIgnoreCase(status))
        {
            log.info("VOIDED on SBM so update details ");

            String cancellationId = (String) statusDetails.get("id");
            String cancellationCode = (String) statusDetails.get("code");
            String cancellationRRN = (String) statusDetails.get("receiptno");
            String cancellationQsiResponseCode = (String) statusDetails.get("qsiresponsecode");
            String cancellationQsiResponseDesc = (String) statusDetails.get("qsiresponsedesc");
            Hashtable cancellationDetails = pg.processResultHashForCancellation(cancellationId, cancellationCode, cancellationRRN, cancellationQsiResponseCode, cancellationQsiResponseDesc);
            updateCancelDetails(trackingId, cancellationDetails, conn);
        }
        else if ("approved".equalsIgnoreCase(status))
        {
            log.info("Approved on " + gateway + "so VOID Auth ");
            Hashtable cancellationDetails = pg.processVoidAuth(trackingId);
            updateCancelDetails(trackingId, cancellationDetails, conn);
        }
        else if ("captured".equalsIgnoreCase(status))
        {
            log.info("Captured on " + gateway + " so pls update details in db ");
            String captureId = (String) statusDetails.get("id");
            String captureCode = (String) statusDetails.get("code");
            String captureRRN = (String) statusDetails.get("receiptno");
            String captureQsiResponseCode = (String) statusDetails.get("qsiresponsecode");
            String captureQsiResponseDesc = (String) statusDetails.get("qsiresponsedesc");
            Hashtable captureDetails = pg.processResultHashforCapture(captureId, captureCode, captureRRN, captureQsiResponseCode, captureQsiResponseDesc);
            // updating capture details in db required since we need the captureid while making a void capture call.
            updateCaptureDetails(trackingId, captureDetails, conn);


            log.info("Now since the detials have been updated we can safely call void capture");
            Hashtable cancellationDetails = pg.processVoidCapture(trackingId);
            updateCancelDetails(trackingId, cancellationDetails, conn);
        }
        else
        {
            throw new SystemError(" Status in DB is 'authsuccessful' while status in " + gateway + " is " + status + " Tracking Id :" + trackingId);
        }
    }

    private static void updateCancelDetails(String trackingId, Hashtable details, Connection conn) throws SystemError
    {
        log.info("Updating Cancel Details");
        String cancellationId = (String) details.get("cancellationid");
        String cancellationCode = (String) details.get("cancellationcode");
        String cancellationRRN = (String) details.get("cancellationreceiptno");
        String cancellationQsiResponseCode = (String) details.get("cancellationqsiresponsecode");
        String cancellationQsiResponseDesc = (String) details.get("cancellationqsiresponsedesc");
        String query = "";

        if (("0").equals(cancellationQsiResponseCode))
        {
            log.debug("Transaction cancelled successfully for trackingid--" + trackingId);
            query = "update transaction_icicicredit set status='authcancelled' where icicitransid=" + trackingId + " and status  in ('proofrequired','authsuccessful','capturesuccess')";
        }
        else

        {
            log.debug("Error while cancelling trackingid--" + trackingId);
            query = null;
        }
        if (query != null)
        {
            Database.executeUpdate(query, conn);
        }
    }

    public Hashtable processAuthentication(String trackingID, String transAmount, Hashtable cardDetailHash,
                                           Hashtable billingAddrHash, Hashtable shippingAddrHash, Hashtable MPIDataHash,
                                           String ipaddress) throws SystemError
    {
        return gatewayHandler.processAuthentication(trackingID, transAmount, cardDetailHash, billingAddrHash, shippingAddrHash, MPIDataHash, ipaddress, accountId);
    }

    // Integration APIs

    public Hashtable processCapture(String trackingID, String captureAmount, String authId, String authCode,
                                    String authRRN) throws SystemError
    {
        return gatewayHandler.processCapture(trackingID, captureAmount, authId, authCode, authRRN, accountId);
    }

    public Hashtable processRefund(String trackingID, String refundAmount, String captureId, String captureCode,
                                   String captureRRN) throws SystemError
    {
        return gatewayHandler.processRefund(trackingID, refundAmount, captureId, captureCode, captureRRN, accountId);
    }

    public Hashtable getStatus(String trackingID) throws SystemError
    {
        return gatewayHandler.getStatus(trackingID, accountId);
    }

    public Hashtable processVoidAuth(String trackingID) throws SystemError
    {
        return gatewayHandler.processVoidAuth(trackingID, accountId);
    }

    public Hashtable processVoidCapture(String trackingID) throws SystemError
    {
        return gatewayHandler.processVoidCapture(trackingID, accountId);
    }

    public Hashtable processVerifyEnrollment(String trackingID, String transAmount, Hashtable cardDetailHash, Hashtable billingAddrHash, String accountID) throws SystemError
    {
        return gatewayHandler.processVerifyEnrollment(trackingID, transAmount, cardDetailHash, billingAddrHash, accountID);
    }

    public Hashtable processPayerAuthentication(String trackingID, String PARes, String paymentId, String accountID) throws SystemError
    {
        return gatewayHandler.processPayerAuthentication(trackingID, PARes, paymentId, accountID);
    }

    public Hashtable processResultHashForCancellation(String cancellationId, String cancellationCode, String cancellationReceiptNo, String cancellationQsiResponseCode, String cancellationQsiResponseDesc)
    {
        return gatewayHandler.processResultHashForCancellation(cancellationId, cancellationCode, cancellationReceiptNo, cancellationQsiResponseCode, cancellationQsiResponseDesc);
    }

    private Hashtable processResultHashforCapture(String captureId, String captureCode, String captureRRN, String captureQsiResponseCode, String captureQsiResponseDesc)
    {
        return gatewayHandler.processResultHashforCapture(captureId, captureCode, captureRRN, captureQsiResponseCode, captureQsiResponseDesc);
    }

    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        return null;
    }

    public GenericResponseVO processInitialSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        return null;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        return null;
    }

    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        return null;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException, PZGenericConstraintViolationException
    {
        return null;
    }

    public GenericResponseVO processCommon3DSaleConfirmation(String trackingID,GenericRequestVO requestVO)throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException,PZGenericConstraintViolationException
    {
        return null;
    }

    public GenericResponseVO processCommon3DAuthConfirmation(String trackingID,GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException,PZGenericConstraintViolationException
    {
        return null;
    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        return null;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        return null;
    }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        return null;
    }

    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZGenericConstraintViolationException
    {
        return null;
    }
    public GenericResponseVO processPayoutInquiry(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZGenericConstraintViolationException
    {
        return null;
    }


    public GenericResponseVO processInitiateAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException, PZGenericConstraintViolationException
    {
        return null;
    }

    public GenericResponseVO processAuthenticate(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException, PZGenericConstraintViolationException
    {
        return null;
    }

    /*public GenericResponseVO getOptionalCodeList(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        return null;
    }*/

    public String getDisplayName()
    {
        return GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
    }

    public String getMerchantId()
    {
        return GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
    }

    public String getCurrency()
    {
        return GatewayAccountService.getGatewayAccount(accountId).getCurrency();
    }

    public String getGateway()
    {
        return GatewayAccountService.getGatewayAccount(accountId).getGateway();
    }

    public boolean isMasterCardSupported()
    {
        return GatewayAccountService.getGatewayAccount(accountId).isMasterCardSupported();
    }

    abstract public String getMaxWaitDays();

/*
    public void processStuckAuthTransactions() throws SystemError
    {

        List<GatewayAccount> accounts = getAccounts();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            for (GatewayAccount account : accounts)
            {
                String accountId = String.valueOf(account.getAccountId());
                String gateway = String.valueOf(account.getGatewayName());
                AbstractPaymentGateway pg = getGateway(accountId);
                String selectStuckTransactionsQuery = "select * from transaction_icicicredit where status = 'authstarted' and  (unix_timestamp(now())-dtstamp >= 1800 ) accountid = " + accountId;
                log.debug("Select Stuck Transactiosn Query :" + selectStuckTransactionsQuery);
                ResultSet rs = Database.executeQuery(selectStuckTransactionsQuery, conn);
                while (rs.next())
                {
                    try
                    {
                        String trackingId = rs.getString("icicitransid");
                        Hashtable statusDetails = pg.getStatus(trackingId);
                        log.debug("Status details" + statusDetails);
                        String status = (String) statusDetails.get("status");
                        if ("voided".equalsIgnoreCase(status))
                        {
                            log.info("VOIDED on "+gateway +"  so update details in db");
                            //cancelTransaction();
                        }
                        else if ("approved".equalsIgnoreCase(status))
                        {
                            log.info("Approved on " + gateway + "so VOID Auth ");
                            Hashtable cancellationDetails = pg.processVoidAuth(trackingId);
                            updateCancelDetails(trackingId, cancellationDetails, conn);
                        }
                        else if ("captured".equalsIgnoreCase(status))
                        {
                            log.info("Captured on " + gateway + " so pls update details in db ");
                            String captureId = (String) statusDetails.get("id");
                            String captureCode = (String) statusDetails.get("code");
                            String captureRRN = (String) statusDetails.get("ceiptno");
                            String captureQsiResponseCode = (String) statusDetails.get("qsiresponsecode");
                            String captureQsiResponseDesc = (String) statusDetails.get("qsiresponsedesc");
                            Hashtable captureDetails = AbstractKitHandler.processResultHashforCapture(captureId, captureCode, captureRRN, captureQsiResponseCode, captureQsiResponseDesc);
                            // updating capture details in db required since we need the captureid while making a void capture call.
                            updateCaptureDetails(trackingId, captureDetails, conn);


                            log.info("Now since the detials have been updated we can safely call void capture");
                            Hashtable cancellationDetails = pg.processVoidCapture(trackingId);
                            updateCancelDetails(trackingId,cancellationDetails, conn);
                        }
                        else
                        {
                            Mail.sendAdminMail("Error while autoCancel", " Status in DB is 'authsuccessful' while status in " + gateway + " is " + status + " Tracking Id :" + trackingId);
                        }
                    }
                    catch (SQLException e)
                    {
                        Mail.sendAdminMail("Error while autoCancel Cron", " Stack Trace " + Util.getStackTrace(e));
                    }
                    catch (SystemError se)
                    {
                        Mail.sendAdminMail("Error while autoCancel Cron", " Stack Trace " + Util.getStackTrace(se));
                    }
                }
            }
        }
        catch (SQLException e)
        {
            throw new SystemError("SQLException " + Util.getStackTrace(e));
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }*/

    public GenericResponseVO processCaptureVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        return null;
    }
    public GenericResponseVO processUpdateRecurring(GenericRequestVO requestVO,String rbid)
    {
        return null;
    }
    public GenericResponseVO processDeleteRecurring(GenericRequestVO requestVO,String rbid) throws PZTechnicalViolationException
    {
        return null;
    }
    public GenericResponseVO processActivateDeactivateRecurring(GenericRequestVO requestVO,String rbid) throws PZTechnicalViolationException
    {
        return null;
    }
    public GenericResponseVO processRebilling(String trackingId,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        return null;
    }
    public GenericResponseVO processPayout(String trackingId,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        return null;
    }
    public GenericResponseVO processPaybyLink (String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        return null;
    }
}