package com.payment;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.directi.pg.core.lpb.LpbPaymentProcess;
import com.directi.pg.core.paymentgateway.*;
import com.payment.Alweave.AlweavePaymentGateway;
import com.payment.Alweave.AlweavePaymentProcess;
import com.payment.Ecommpay.EcommpayPaymentGateway;
import com.payment.Ecommpay.EcommpayPaymentProcess;
import com.payment.FlutterWave.FlutterWavePaymentGateway;
import com.payment.FlutterWave.FlutterWavePaymentProcess;
import com.payment.FrickBank.core.FrickBankPaymentGateWay;
import com.payment.FrickBank.core.FrickBankPaymentProcess;
import com.payment.KortaPay.KortaPayPaymentGateway;
import com.payment.KortaPay.KortaPayPaymentProcess;
import com.payment.LetzPay.LetzPayPaymentGateway;
import com.payment.LetzPay.LetzPayPaymentProcess;
import com.payment.PayEasyWorld.PayEasyWorldPaymentGateway;
import com.payment.PayEasyWorld.PayEasyWorldPaymentProcess;
import com.payment.PayMitco.core.PayMitcoPaymentGateway;
import com.payment.PayMitco.core.PayMitcoPaymentProcess;
import com.payment.ReitumuBank.core.ReitumuBankMerchantPaymentGateway;
import com.payment.ReitumuBank.core.ReitumuBankMerchantPaymentProcess;
import com.payment.ReitumuBank.core.ReitumuBankPaymentProcess;
import com.payment.ReitumuBank.core.ReitumuBankSMSPaymentGateway;
import com.payment.Rubixpay.RubixpayPaymentGateway;
import com.payment.Rubixpay.RubixpayPaymentProcess;
import com.payment.STS.core.STSPaymentGateway;
import com.payment.STS.core.STSPaymentProcess;
import com.payment.TWDTaiwan.TWDTaiwanPaymentGateway;
import com.payment.TWDTaiwan.TWDTaiwanPaymentProccess;
import com.payment.Triple000.Triple000PaymentGateway;
import com.payment.Triple000.Triple000PaymentProcess;
import com.payment.Wirecardnew.WireCardNPaymentGateway;
import com.payment.Wirecardnew.WireCardPaymentProcess;
import com.payment.alliedwalled.core.AlliedPaymentProcess;
import com.payment.alliedwalled.core.AlliedWalletPaymentGateway;
import com.payment.apco.core.ApcoPayPaymentProcess;
import com.payment.apco.core.ApcoPaymentGateway;
import com.payment.apcoFastpay.ApcoFastpayPaymentGateway;
import com.payment.apcoFastpay.ApcoFastpayPaymentProcess;
import com.payment.arenaplus.core.ArenaPlusPaymentGateway;
import com.payment.arenaplus.core.ArenaPlusPaymentProcess;
import com.payment.asiancheckout.AsianCheckoutPaymentGateway;
import com.payment.asiancheckout.AsianCheckoutPaymentProcess;
import com.payment.auxpay_payment.core.AuxPayPaymentGateway;
import com.payment.auxpay_payment.core.AuxpayPaymentProcess;
import com.payment.awepay.AwepayBundle.core.AwepayPaymentGateway;
import com.payment.awepay.AwepayBundle.core.AwepayPaymentProcess;
import com.payment.b4payment.B4PaymentGateway;
import com.payment.b4payment.B4PaymentProcess;
import com.payment.bankone.BankonePaymentGateway;
import com.payment.bankone.BankonePaymentProcess;
import com.payment.beekash.BeekashPaymentGateway;
import com.payment.beekash.BeekashPaymentProcess;
import com.payment.bennupay.BennupayPaymentGateway;
import com.payment.bennupay.BennupayPaymentProcess;
import com.payment.bhartiPay.BhartiPayPaymentGateway;
import com.payment.bhartiPay.BhartiPayPaymentProcess;
import com.payment.billdesk.BillDeskPaymentGateway;
import com.payment.billdesk.BillDeskPaymentProcess;
import com.payment.bitcoinpayget.BitcoinPaygatePaymentGateway;
import com.payment.bitcoinpayget.BitcoinPaygatePaymentProcess;
import com.payment.borgun.core.BorgunPaymentGateway;
import com.payment.borgun.core.BorgunPaymentProcess;
import com.payment.cardinity.CardinityPaymentGateway;
import com.payment.cardinity.CardinityPaymentProcess;
import com.payment.cardpay.CardPayPaymentGateway;
import com.payment.cardpay.CardPayPaymentProcess;
import com.payment.cashflow.core.CashFlowPaymentGateway;
import com.payment.cashflow.core.CashFlowPaymentProcess;
import com.payment.cashfree.CashFreePaymentGateway;
import com.payment.cashfree.CashFreePaymentProcess;
import com.payment.clearsettle.ClearSettleHPPGateway;
import com.payment.clearsettle.ClearSettleHPPPaymentProcess;
import com.payment.clearsettle.ClearSettlePaymentGateway;
import com.payment.clearsettle.ClearSettlePaymentProcess;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.credorax.core.CredoraxPaymentProcess;
import com.payment.cup.core.CupPaymentProcess;
import com.payment.cupUPI.UnionPayInternationalPaymentGateway;
import com.payment.cupUPI.UnionPayInternationalPaymentProcess;
import com.payment.curo.CuroPaymentGateway;
import com.payment.curo.CuroPaymentProcess;
import com.payment.cybersource.CyberSourcePaymentGateway;
import com.payment.cybersource.CyberSourcePaymentProcess;
import com.payment.cybersource2.AtCyberSourcePaymentGateway;
import com.payment.cybersource2.AtCyberSourcePaymentProcess;
import com.payment.ddp.core.DdpPaymentProcess;
import com.payment.decta.core.DectaMerchantPaymentGateway;
import com.payment.decta.core.DectaPaymentProcess;
import com.payment.decta.core.DectaSMSPaymentGateway;
import com.payment.dectaNew.DectaNewPaymentGateway;
import com.payment.dectaNew.DectaNewPaymentProcess;
import com.payment.deltapay.core.DeltaPayPaymentProcess;
import com.payment.doku.DokuPaymentGateway;
import com.payment.doku.DokuPaymentProcess;
import com.payment.duspaydirectdebit.DusPayDDPaymentGateway;
import com.payment.duspaydirectdebit.DusPayDDPaymentProcess;
import com.payment.dvg_payment.DVGPaymentGateway;
import com.payment.dvg_payment.DVGPaymentProcess;
import com.payment.easypaymentz.EasyPaymentzPaymentGateway;
import com.payment.easypaymentz.EasyPaymentzPaymentProcess;
import com.payment.ecomprocessing.ECPPaymentGateway;
import com.payment.ecomprocessing.ECPPaymentProcess;
import com.payment.elegro.ElegroPaymentGateway;
import com.payment.elegro.ElegroPaymentProcess;
import com.payment.emax_high_risk.core.EmaxHighRiskPaymentGateway;
import com.payment.emax_high_risk.core.EmaxHighRiskPaymentProcess;
import com.payment.emerchantpay.EMerchantPayPaymentGateway;
import com.payment.emerchantpay.EMerchantPayPaymentProcess;
import com.payment.emexpay.EmexpayPaymentGateway;
import com.payment.emexpay.EmexpayPaymentProcess;
import com.payment.ems.core.EMSPaymentGateway;
import com.payment.ems.core.EMSPaymentProcess;
import com.payment.epay.EpayPaymentGateway;
import com.payment.epay.EpayPaymentProcess;
import com.payment.europay.core.EuroPayPaymentGateway;
import com.payment.europay.core.EuroPayPaymentProcess;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.fenige.FenigePaymentGateway;
import com.payment.fenige.FenigePaymentProcess;
import com.payment.flwBarter.FlutterWaveBarterPaymentGateway;
import com.payment.flwBarter.FlutterWaveBarterPaymentProcess;
import com.payment.giftpay.GiftPayPaymentGateway;
import com.payment.giftpay.GiftPayPaymentProcess;
import com.payment.globalpay.GlobalPayPaymentGateway;
import com.payment.globalpay.GlobalPayPaymentProcess;
import com.payment.gold24.core.Gold24PaymentGateway;
import com.payment.iMerchantPay.iMerchantPaymentGateway;
import com.payment.iMerchantPay.iMerchantPaymentProcess;
import com.payment.icard.ICardPaymentGateway;
import com.payment.icard.ICardPaymentProcess;
import com.payment.icici.INICICIPaymentGateway;
import com.payment.icici.INICICIPaymentProcess;
import com.payment.ilixium.IlixiumPaymentGateway;
import com.payment.ilixium.IlixiumPaymentProcess;
import com.payment.inpay.InPayPaymentGateway;
import com.payment.inpay.InPayPaymentProcess;
import com.payment.ippopay.IppoPayPaymentGateway;
import com.payment.ippopay.IppoPayPaymentProcess;
import com.payment.jeton.JetonPaymentGateway;
import com.payment.jeton.JetonPaymentProcess;
import com.payment.jpbanktransfer.JPBTPaymentGateway;
import com.payment.jpbanktransfer.JPBankPaymentProcess;
import com.payment.kotakbank.core.KotakAllPayCardPaymentGateway;
import com.payment.kotakbank.core.KotakPaymentProcess;
import com.payment.luqapay.JetonCardPaymentGateway;
import com.payment.luqapay.JetonCardPaymentProcess;
import com.payment.nestpay.NestPayPaymentGateway;
import com.payment.nestpay.NestPaymentProcess;
import com.payment.neteller.NetellerPaymentGateway;
import com.payment.neteller.response.NetellerPaymentProcess;
import com.payment.nexi.NexiPaymentGateway;
import com.payment.nexi.NexiPaymentProcess;
import com.payment.ninja.NinjaWalletPaymentGateway;
import com.payment.ninja.NinjaWalletPaymentProcess;
import com.payment.npayon.NPayOnGateway;
import com.payment.npayon.NPayOnPaymentProcess;
import com.payment.octapay.OctapayPaymentGateway;
import com.payment.octapay.OctapayPaymentProcess;
import com.payment.opx.OPXPaymentGateway;
import com.payment.opx.OPXPaymentProcess;
import com.payment.p4.gateway.P4CreditCardPaymentGateway;
import com.payment.p4.gateway.P4CreditCardPaymentProcess;
import com.payment.p4.gateway.P4PaymentGateway;
import com.payment.p4.gateway.P4PaymentProcess;
import com.payment.payFluid.PayFluidPaymentGateway;
import com.payment.payFluid.PayFluidPaymentProcess;
import com.payment.payGateway.core.PaygatewayPaymentGateway;
import com.payment.paySafeCard.PaySafeCardPaymentGateway;
import com.payment.paySafeCard.PaySafeCardPaymentProcess;
import com.payment.payVaultPro.PayVaultProPaymentGateway;
import com.payment.payVaultPro.PayVaultProPaymentProcess;
import com.payment.payboutique.PayBoutiquePaymentGateway;
import com.payment.payboutique.PayBoutiquePaymentProcess;
import com.payment.payclub.PayClubPaymentGateway;
import com.payment.payclub.PayClubPaymentProcess;
import com.payment.paydollar.core.PayDollarPaymentProcess;
import com.payment.payeezy.PayeezyPaymentGateway;
import com.payment.payeezy.PayeezyPaymentProcess;
import com.payment.payforasia.core.PayforasiaPaymentGateway;
import com.payment.payforasia.core.PayforasiaPaymentProcess;
import com.payment.paynetics.core.PayneticsGateway;
import com.payment.paynetics.core.PayneticsPaymentProcess;
import com.payment.payon.core.PayOnGateway;
import com.payment.payon.core.PayOnPaymentProcess;
import com.payment.payonOppwa.PayonOppwaPaymentGateway;
import com.payment.paysec.PaySecPaymentGateway;
import com.payment.paysec.PaysecPaymentProcess;
import com.payment.paysend.PaySendPaymentGateway;
import com.payment.paysend.PaySendPaymentProcess;
import com.payment.paysend.PaySendWalletPaymentGateway;
import com.payment.payspace.PaySpacePaymentGateway;
import com.payment.payspace.PaySpacePaymentProcess;
import com.payment.payvision.core.PayVisionPaymentGateway;
import com.payment.payvision.core.PayVisionPaymentProcess;
import com.payment.payvoucher.core.PayVoucherPaymentProcess;
import com.payment.pbs.core.PbsPaymentGateway;
import com.payment.pbs.core.PbsPaymentProcess;
import com.payment.perfectmoney.PerfectMoneyPaymentGateway;
import com.payment.perfectmoney.PerfectMoneyPaymentProcess;
import com.payment.pfs.core.PfsPaymentProcess;
import com.payment.phoneix.PhoneixPaymentGateway;
import com.payment.phoneix.PhoneixPaymentProcess;
import com.payment.procesosmc.ProcesosMCPaymentGateway;
import com.payment.procesosmc.ProcesosMCPaymentProcess;
import com.payment.processing.core.ProcessingPaymentGateway;
import com.payment.processing.core.ProcessingPaymentProcess;
import com.payment.qikpay.QikPayPaymentProcess;
import com.payment.qikpay.QikpayPaymentGateway;
import com.payment.quickpayments.QuickPaymentsGateway;
import com.payment.quickpayments.QuickPaymentsPaymentProcess;
import com.payment.qwipi.core.QwipiGatewayProcess;
import com.payment.rave.RavePaymentGateway;
import com.payment.rave.RavePaymentProcess;
import com.payment.romcard.RomCardPaymentGateway;
import com.payment.romcard.RomCardPaymentProcess;
import com.payment.rsp.RSPPaymentGateway;
import com.payment.rsp.RSPPaymentProcess;
import com.payment.sabadell.SabadellPaymentGateway;
import com.payment.sabadell.SabadellPaymentProcess;
import com.payment.safechargeV2.SafeChargeV2PaymentGateway;
import com.payment.safechargeV2.SafeChargeV2PaymentProcess;
import com.payment.safexpay.SafexPayPaymentGateway;
import com.payment.safexpay.SafexPayPaymentProcess;
import com.payment.sbm.core.SBMPaymentGateway;
import com.payment.sbm.core.SBMPaymentProcess;
import com.payment.secureTrading.SecureTradingGateway;
import com.payment.secureTrading.SecureTradingPaymentProcess;
import com.payment.skrill.SkrillPaymentGateway;
import com.payment.skrill.SkrillPaymentProcess;
import com.payment.sofort.IdealPaymentGateway;
import com.payment.sofort.IdealPaymentProcess;
import com.payment.sofort.SofortPaymentGateway;
import com.payment.sofort.SofortPaymentProcess;
import com.payment.tapmio.TapMioPaymentGateway;
import com.payment.tapmio.TapMioPaymentProcess;
import com.payment.transactium.psp.ps.v1003.TransactiumPaymentGateway;
import com.payment.transactium.psp.ps.v1003.TransactiumPaymentProcess;
import com.payment.trueVo.TrueVoPaymentGateway;
import com.payment.trustPay.TrustPayOppwaPaymentGateway;
import com.payment.trustly.TrustlyPaymentGateway;
import com.payment.trustly.TrustlyPaymentProcess;
import com.payment.trustspay.TrustsPayPaymentProcess;
import com.payment.uPayGate.UPayGatePaymentGateway;
import com.payment.uPayGate.UPayGatePaymentProcess;
import com.payment.unicredit.UnicreditPaymentGateway;
import com.payment.unicredit.UnicreditPaymentProcess;
import com.payment.verve.VervePaymentGateway;
import com.payment.verve.VervePaymentProcess;
import com.payment.visaNet.VisaNetPaymentGateway;
import com.payment.visaNet.VisaNetPaymentProcess;
import com.payment.vouchermoney.VoucherMoneyPaymentGateway;
import com.payment.vouchermoney.VoucherMoneyPaymentProcess;
import com.payment.websecpay.core.WSecPaymentGateway;
import com.payment.websecpay.core.WSecPaymentProcess;
import com.payment.whitelabel.WLPaymentGateway;
import com.payment.whitelabel.WLPaymentProcess;
import com.payment.wonderland.core.WonderlandPayPaymentGateway;
import com.payment.wonderland.core.WonderlandPayPaymentProcess;
import com.payment.xpate.XPatePaymentGateway;
import com.payment.xpate.XPatePaymentProcess;
import com.payment.zhixinfu.ZhiXinfuPaymentGateway;
import com.payment.zhixinfu.ZhiXinfuPaymentProcess;
import com.payment.zotapay.ZotaPayPaymentProcess;
import com.payment.zotapay.ZotapayPaymentGateway;
import com.payment.zotapaygateway.ZotaPayGateway;
import com.payment.zotapaygateway.ZotaPayProcess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 2/17/13
 * Time: 11:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class PaymentProcessFactory
{
    static Logger logger = new Logger(PaymentProcessFactory.class.getName());

    public static AbstractPaymentProcess getPaymentProcessInstance(Integer trackingId, Integer accountId)
    {
        if (accountId == null)
        {
            //String[] transactionTables = new String[]{"transaction_common", "transaction_ecore", "transaction_qwipi"};
            Connection con = null;
            try
            {
                con = Database.getConnection();
               /* for (String transactionTable : transactionTables)
                {*/
                    String sql = "select accountid from transaction_common where trackingid=?";
                    PreparedStatement p1 = con.prepareStatement(sql);
                    p1.setString(1, String.valueOf(trackingId));
                    ResultSet rs = p1.executeQuery();

                    if (rs.next())
                    {
                        accountId = rs.getInt("accountid");
                    }
                //}
            }
            catch (SQLException se)
            {
                logger.error("SQLException in PaymentProcessFactory",se);
                PZExceptionHandler.raiseAndHandleDBViolationException("PaymentProcessFactory.java","getPaymentProcessInstance()",null,"common","SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null,se.getMessage(),se.getCause(),null,null);
            }
            catch (SystemError e)
            {
                logger.error("Exception in PaymentProcessFactory",e);
                PZExceptionHandler.raiseAndHandleGenericViolationException("PaymentProcessFactory.java","getPaymentProcessInstance()",null,"common","SQLException Thrown:::",null,e.getMessage(),e.getCause(),null,null);
            }
            finally
            {
                Database.closeConnection(con);
            }
        }

        //ToDo - make switch in createPaymentProcess
        return createPaymentProcess(accountId);
    }

    public static AbstractPaymentProcess getPaymentProcessInstance(Integer accountId)
    {
        return createPaymentProcess(accountId);
    }

    public static AbstractPaymentProcess getPaymentProcessInstance(String pgTypeId)
    {
        return createPaymentProcess(pgTypeId);
    }

    private static AbstractPaymentProcess createPaymentProcess(Integer accountId)
    {
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
        GatewayType gatewayType = GatewayTypeService.getGatewayType(gatewayAccount.getPgTypeId());
        String gateway = gatewayType.getGateway();

        logger.debug("gateway-----"+gateway);
        switch (gateway)
        {
            case QpPaymentGateway.GATEWAY_TYPE:
                return new QwipiGatewayProcess();

            case MyMonederoPaymentGateway.GATEWAY_TYPE:
                return new MyMonederoPaymentProcess();

            case UGSPaymentGateway.GATEWAY_TYPE_UGS:
                return new UGSPayPaymentProcess();

            case UGSPaymentGateway.GATEWAY_TYPE_FORT:
                return new UGSPayPaymentProcess();

            case CUPPaymentGateway.GATEWAY_TYPE:
                return new CupPaymentProcess();

            case DdpPaymentGateway.GATEWAY_TYPE:
                return new DdpPaymentProcess();

            case PayVisionPaymentGateway.GATEWAY_TYPE:
                return new PayVisionPaymentProcess();

            case WSecPaymentGateway.GATEWAY_TYPE:
                return new WSecPaymentProcess();

            case PayOnGateway.GATEWAY_TYPE_PAYON:
                return new PayOnPaymentProcess();

            case PayOnGateway.GATEWAY_TYPE_CATELLA:
                return new PayOnPaymentProcess();

            case CredoraxPaymentGateway.GATEWAY_TYPE:
                return new CredoraxPaymentProcess();

            case EuroPayPaymentGateway.GATEWAY_TYPE:
                return new EuroPayPaymentProcess();

            case WireCardNPaymentGateway.GATEWAY_TYPE:
                return new WireCardPaymentProcess();

            case BorgunPaymentGateway.GATEWAY_TYPE:
                return  new BorgunPaymentProcess();

            case ArenaPlusPaymentGateway.GATEWAY_TYPE:
                return new ArenaPlusPaymentProcess();

            case PayDollarPaymentGateway.GATEWAY_TYPE:
                return new PayDollarPaymentProcess();

            case SwiffpayPaymentGateway.GATEWAY_TYPE:
                return new SwiffPayPaymentProcess();

            case PayLineVoucherGateway.GATEWAY_TYPE:
                return new PayVoucherPaymentProcess();

            case PayWorldPaymentGateway.GATEWAY_TYPE:
                return new PayWorldPaymentProcess();

            case NMIPaymentGateway.GATEWAY_TYPE:
                return new NMIPaymentProcess();

            case DeltaPaymentGateway.GATEWAY_TYPE:
                return new DeltaPayPaymentProcess();

            case FrickBankPaymentGateWay.GATEWAY_TYPE:
                return new FrickBankPaymentProcess();

            case SafeChargePaymentGateway.GATEWAY_TYPE:
                return new SafeChargePaymentProcess();

            case FluzznetworkPaymentGateway.GATEWAY_TYPE:
                return new FluzznetworkPaymentProcess();

            case IPAYDNAPaymentGateway.GATEWAY_TYPE:
                return new IPAYDNAPaymentProcess();

            case PaygatewayPaymentGateway.GATEWAY_TYPE:
                return new PaygatewayPaymentProcess();

            case Gold24PaymentGateway.GATEWAY_TYPE:
                return new Gold24PaymentProcess();

            case PayforasiaPaymentGateway.GATEWAY_TYPE:
                return new PayforasiaPaymentProcess();

            case GCPPaymentGateway.GATEWAY_TYPE:
                return new PayforasiaPaymentProcess();

            case AlliedWalletPaymentGateway.GATEWAY_TYPE:
                return new AlliedPaymentProcess();

            case PfsPaymentGateway.GATEWAY_TYPE:
                return new PfsPaymentProcess();

            case SBMPaymentGateway.GATEWAY_TYPE:
                return new SBMPaymentProcess();

            case DVGPaymentGateway.GATEWAY_TYPE:
                return new DVGPaymentProcess();

            case STSPaymentGateway.GATEWAY_TYPE:
                return new STSPaymentProcess();

            case EmaxHighRiskPaymentGateway.GATEWAY_TYPE:
                return new EmaxHighRiskPaymentProcess();

            case ReitumuBankSMSPaymentGateway.GATEWAY_TYPE:
                return new ReitumuBankPaymentProcess();

            case DectaSMSPaymentGateway.GATEWAY_TYPE:
                return new DectaPaymentProcess();

            case ReitumuBankMerchantPaymentGateway.GATEWAY_TYPE:
                return new ReitumuBankMerchantPaymentProcess();

            case PaySafeCardPaymentGateway.GATEWAY_TYPE:
                return new PaySafeCardPaymentProcess();

            case ProcessingPaymentGateway.GATEWAY_TYPE:
                return new ProcessingPaymentProcess();

            case SofortPaymentGateway.GATEWAY_TYPE:
                return new SofortPaymentProcess();

            case IdealPaymentGateway.GATEWAY_TYPE:
                return new IdealPaymentProcess();

            case OPXPaymentGateway.GATEWAY_TYPE:
                return new OPXPaymentProcess();

            case ProcesosMCPaymentGateway.GATEWAY_TYPE:
                return new ProcesosMCPaymentProcess();

            case VisaNetPaymentGateway.GATEWAY_TYPE:
                return new VisaNetPaymentProcess();

            case INICICIPaymentGateway.GATEWAY_TYPE:
                return new INICICIPaymentProcess();

            case AuxPayPaymentGateway.GATEWAY_TYPE:
                return new AuxpayPaymentProcess();

            case PayMitcoPaymentGateway.GATEWAY_TYPE:
                return new PayMitcoPaymentProcess();

            case InPayPaymentGateway.GATEWAY_TYPE:
                return new InPayPaymentProcess();

            case PaySecPaymentGateway.GATEWAY_TYPE:
                return new PaysecPaymentProcess();

            case P4PaymentGateway.GATEWAY_TYPE:
                return new P4PaymentProcess();

            case PbsPaymentGateway.GATEWAY_TYPE:
                return new PbsPaymentProcess();

            case TrustsPayPaymentGateway.GATEWAY_TYPE:
                return new TrustsPayPaymentProcess();

            case P4CreditCardPaymentGateway.GATEWAY_TYPE:
                return new P4CreditCardPaymentProcess();

            case CashFlowPaymentGateway.GATEWAY_TYPE:
                return new CashFlowPaymentProcess();

            case WonderlandPayPaymentGateway.GATEWAY_TYPE:
                return new WonderlandPayPaymentProcess();

            case ClearSettlePaymentGateway.GATEWAY_TYPE:
                return new ClearSettlePaymentProcess();

            case DusPayDDPaymentGateway.GATEWAY_TYPE:
                return new DusPayDDPaymentProcess();

            case B4PaymentGateway.GATEWAY_TYPE:
                return new B4PaymentProcess();

            case ApcoPaymentGateway.GATEWAY_TYPE:
                return new ApcoPayPaymentProcess();

            case KotakAllPayCardPaymentGateway.GATEWAY_TYPE:
                return new KotakPaymentProcess();

            case DectaMerchantPaymentGateway.GATEWAY_TYPE:
                return new DectaPaymentProcess();

            case PaySpacePaymentGateway.GATEWAY_TYPE:
                return new PaySpacePaymentProcess();

            case UPayGatePaymentGateway.GATEWAY_TYPE:
                return new UPayGatePaymentProcess();

            case PerfectMoneyPaymentGateway.GATEWAY_TYPE:
                return new PerfectMoneyPaymentProcess();

            case NetellerPaymentGateway.GATEWAY_TYPE:
                return new NetellerPaymentProcess();

            case SkrillPaymentGateway.GATEWAY_TYPE:
                return new SkrillPaymentProcess();

            case BillDeskPaymentGateway.GATEWAY_TYPE:
                return new BillDeskPaymentProcess();

            case JetonPaymentGateway.GATEWAY_TYPE:
                return new JetonPaymentProcess();

            case VoucherMoneyPaymentGateway.GATEWAY_TYPE:
                return new VoucherMoneyPaymentProcess();

            case BankonePaymentGateway.GATEWAY_TYPE:
                return new BankonePaymentProcess();

            case RavePaymentGateway.GATEWAY_TYPE:
                return new RavePaymentProcess();

            case EpayPaymentGateway.GATEWAY_TYPE:
                return new EpayPaymentProcess();

            case TrustlyPaymentGateway.GATEWAY_TYPE:
                return new TrustlyPaymentProcess();

            case PayneticsGateway.GATEWAY_TYPE:
                return new PayneticsPaymentProcess();

            case EmexpayPaymentGateway.GATEWAY_TYPE:
                return new EmexpayPaymentProcess();

            case EMSPaymentGateway.GATEWAY_TYPE:
                return new EMSPaymentProcess();

            case UnicreditPaymentGateway.GATEWAY_TYPE:
                return new UnicreditPaymentProcess();

            case TransactiumPaymentGateway.GATEWAY_TYPE:
                return new TransactiumPaymentProcess();

            case CardinityPaymentGateway.GATEWAY_TYPE:
                return new CardinityPaymentProcess();

            case LpbPaymentGateway.GATEWAY_TYPE:
                return new LpbPaymentProcess();

            case ApcoFastpayPaymentGateway.GATEWAY_TYPE_APC0_ALDRAPAY:
                return new ApcoFastpayPaymentProcess();

            case ApcoFastpayPaymentGateway.GATEWAY_TYPE_APCO_PURPLEPAY:
                return new ApcoFastpayPaymentProcess();

            case RSPPaymentGateway.GATEWAY_TYPE:
                return new RSPPaymentProcess();

            case NPayOnGateway.GATEWAY_TYPE:
                return new NPayOnPaymentProcess();

            case TrueVoPaymentGateway.GATEWAY_TYPE:
                return new NPayOnPaymentProcess();

            case iMerchantPaymentGateway.GATEWAY_TYPE:
                return  new iMerchantPaymentProcess();

            case NestPayPaymentGateway.GATEWAY_TYPE:
                return  new NestPaymentProcess();

            case ZotapayPaymentGateway.GATEWAY_TYPE:
                return  new ZotaPayPaymentProcess();

            case TransactiumPaymentGateway.GATEWAY_TYPE_TRX:
                return  new TransactiumPaymentProcess();

            case ClearSettleHPPGateway.GATEWAY_TYPE:
                return  new ClearSettleHPPPaymentProcess();

            case SabadellPaymentGateway.GATEWAY_TYPE:
                return  new SabadellPaymentProcess();

            case CardPayPaymentGateway.GATEWAY_TYPE:
                return  new CardPayPaymentProcess();

            case ApcoFastpayPaymentGateway.GATEWAY_TYPE_APCO_RAVE:
                return  new ApcoFastpayPaymentProcess();

            case ApcoFastpayPaymentGateway.GATEWAY_TYPE_APCO_FAST_PAY:
                return  new ApcoFastpayPaymentProcess();

            case TransactiumPaymentGateway.GATEWAY_TYPE_TRANSACTIUM_1:
                return  new TransactiumPaymentProcess();

            case WLPaymentGateway.GATEWAY_TYPE_AGNIPAY:
                return new WLPaymentProcess();

            case WLPaymentGateway.GATEWAY_TYPE_TRANSACTWORLD:
                return new WLPaymentProcess();

            case WLPaymentGateway.GATEWAY_TYPE_SHIMOTOMO:
                return new WLPaymentProcess();

            case WLPaymentGateway.GATEWAY_TYPE_SAMPLEPSP:
                return new WLPaymentProcess();

            case WLPaymentGateway.GATEWAY_TYPE_FIDOMS:
                return new WLPaymentProcess();

            case SecureTradingGateway.GATEWAY_TYPE:
                return new SecureTradingPaymentProcess();

            case XPatePaymentGateway.GATEWAY_TYPE:
                return new XPatePaymentProcess();

            case UnionPayInternationalPaymentGateway.GATEWAY_TYPE:
                return new UnionPayInternationalPaymentProcess();

             case ECPPaymentGateway.GATEWAY_TYPE:
                return new ECPPaymentProcess();

            case FlutterWavePaymentGateway.GATEWAY_TYPE:
                return new FlutterWavePaymentProcess();


            case AwepayPaymentGateway.GATEWAY_TYPE:
                return new AwepayPaymentProcess();

            case CyberSourcePaymentGateway.GATEWAY_TYPE:
                return new CyberSourcePaymentProcess();

            case PayVaultProPaymentGateway.GATEWAY_TYPE:
                return new PayVaultProPaymentProcess();

            case RomCardPaymentGateway.GATEWAY_TYPE:
                return new RomCardPaymentProcess();

            case KortaPayPaymentGateway.GATEWAY_TYPE:
                return new KortaPayPaymentProcess();

            case BitcoinPaygatePaymentGateway.GATEWAY_TYPE:
                return new BitcoinPaygatePaymentProcess();

            case PayBoutiquePaymentGateway.GATEWAY_TYPE:
                return new PayBoutiquePaymentProcess();

            case ElegroPaymentGateway.GATEWAY_TYPE:
                return new ElegroPaymentProcess();

            case BeekashPaymentGateway.GATEWAY_TYPE:
                return new BeekashPaymentProcess();

            case ICardPaymentGateway.GATEWAY_TYPE:
                return new ICardPaymentProcess();

            case PaySendPaymentGateway.GATEWAY_TYPE:
                return new PaySendPaymentProcess();

            case PaySendWalletPaymentGateway.GATEWAY_TYPE:
                return new PaySendPaymentProcess();

            case NexiPaymentGateway.GATEWAY_TYPE:
                return new NexiPaymentProcess();

            case DectaNewPaymentGateway.GATEWAY_TYPE:
                return new DectaNewPaymentProcess();

            case NinjaWalletPaymentGateway.GATEWAY_TYPE:
                return new NinjaWalletPaymentProcess();

            case PayeezyPaymentGateway.GATEWAY_TYPE:
                return new PayeezyPaymentProcess();

           /* case UnionPayInternationalPaymentGateway.GATEWAY_TYPE:
                return new UnicreditPaymentProcess();*/

            case PhoneixPaymentGateway.GATEWAY_TYPE:
                return new PhoneixPaymentProcess();

            case SafexPayPaymentGateway.GATEWAY_TYPE:
               return new SafexPayPaymentProcess();


            case CuroPaymentGateway.GATEWAY_TYPE:
                return new CuroPaymentProcess();

            case BhartiPayPaymentGateway.GATEWAY_TYPE:
                return new BhartiPayPaymentProcess();

            case FlutterWaveBarterPaymentGateway.GATEWAY_TYPE:
                return new FlutterWaveBarterPaymentProcess();

            case ZotaPayGateway.GATEWAY_TYPE:
                return new ZotaPayProcess();

            case EMerchantPayPaymentGateway.GATEWAY_TYPE:
                return new EMerchantPayPaymentProcess();

            case JetonCardPaymentGateway.GATEWAY_TYPE:
                return new JetonCardPaymentProcess();

            case IlixiumPaymentGateway.GATEWAY_TYPE:
                return new IlixiumPaymentProcess();

            case JPBTPaymentGateway.GATEWAY_TYPE:
                return new JPBankPaymentProcess();

            case TapMioPaymentGateway.GATEWAY_TYPE:
                return new TapMioPaymentProcess();

            case RubixpayPaymentGateway.GATEWAY_TYPE:
                return new RubixpayPaymentProcess();

            case PayonOppwaPaymentGateway.GATEWAY_TYPE:
                return new NPayOnPaymentProcess();

            case EcommpayPaymentGateway.GATEWAY_TYPE:
                return new EcommpayPaymentProcess();

            case SafeChargeV2PaymentGateway.GATEWAY_TYPE:
                return new SafeChargeV2PaymentProcess();

            case VervePaymentGateway.GATEWAY_TYPE:
                return new VervePaymentProcess();

            case IppoPayPaymentGateway.GATEWAY_TYPE:
                return new IppoPayPaymentProcess();
            case Triple000PaymentGateway.GATEWAY_TYPE:
                return new Triple000PaymentProcess();

            case FenigePaymentGateway.GATEWAY_TYPE:
                return new FenigePaymentProcess();

            case AlweavePaymentGateway.GATEWAY_TYPE:
                return new AlweavePaymentProcess();

            case ZhiXinfuPaymentGateway.GATEWAY_TYPE:
                return new ZhiXinfuPaymentProcess();

            case AtCyberSourcePaymentGateway.GATEWAY_TYPE:
                return new AtCyberSourcePaymentProcess();

            case OctapayPaymentGateway.GATEWAY_TYPE:
                return new OctapayPaymentProcess();

             case PayFluidPaymentGateway.GATEWAY_TYPE:
                return new PayFluidPaymentProcess();

             case PayClubPaymentGateway.GATEWAY_TYPE:
                return new PayClubPaymentProcess();



            case TWDTaiwanPaymentGateway.GATEWAY_TYPE:
                return new TWDTaiwanPaymentProccess();


            case GlobalPayPaymentGateway.GATEWAY_TYPE:
                return new GlobalPayPaymentProcess();

            case TrustPayOppwaPaymentGateway.GATEWAY_TYPE:
                return new NPayOnPaymentProcess();

            case QikpayPaymentGateway.GATEWAY_TYPE:
                return new QikPayPaymentProcess();

            case LetzPayPaymentGateway.GATEWAY_TYPE:
                return new LetzPayPaymentProcess();

            case GiftPayPaymentGateway.GATEWAY_TYPE:
                return new GiftPayPaymentProcess();

            case AsianCheckoutPaymentGateway.GATEWAY_TYPE:
                return new AsianCheckoutPaymentProcess();

            case CashFreePaymentGateway.GATEWAY_TYPE:
                return new CashFreePaymentProcess();

            case QuickPaymentsGateway.GATEWAY_TYPE:
                return new QuickPaymentsPaymentProcess();

            case BennupayPaymentGateway.GATEWAY_TYPE:
                return new BennupayPaymentProcess();

            case PayEasyWorldPaymentGateway.GATEWAY_TYPE:
                return new PayEasyWorldPaymentProcess();

            case DokuPaymentGateway.GATEWAY_TYPE:
                return new DokuPaymentProcess();

            case EasyPaymentzPaymentGateway.GATEWAY_TYPE:
                return new EasyPaymentzPaymentProcess();

            default:
                return new CommonPaymentProcess();

        }
    }

    private static AbstractPaymentProcess createPaymentProcess(String gateway)
    {
        if (QpPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new QwipiGatewayProcess();
        }
        else if (MyMonederoPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new MyMonederoPaymentProcess();
        }
        else if (UGSPaymentGateway.GATEWAY_TYPE_UGS.equalsIgnoreCase(gateway))
        {
            return new UGSPayPaymentProcess();
        }
        else if (UGSPaymentGateway.GATEWAY_TYPE_FORT.equalsIgnoreCase(gateway))
        {
            return new UGSPayPaymentProcess();
        }
        else if (CUPPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new CupPaymentProcess();
        }
        else if (DdpPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new DdpPaymentProcess();
        }
        else if (PayVisionPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new PayVisionPaymentProcess();
        }
        else if (WSecPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new WSecPaymentProcess();
        }
        else if (PayOnGateway.GATEWAY_TYPE_PAYON.equalsIgnoreCase(gateway))
        {
            return new PayOnPaymentProcess();
        }
        else if (PayOnGateway.GATEWAY_TYPE_CATELLA.equalsIgnoreCase(gateway))
        {
            return new PayOnPaymentProcess();
        }

        else if (CredoraxPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new CredoraxPaymentProcess();
        }
        else if (EuroPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new EuroPayPaymentProcess();
        }
        else if (WireCardNPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new WireCardPaymentProcess();
        }
        else if (BorgunPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new BorgunPaymentProcess();
        }
        else if (ArenaPlusPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new ArenaPlusPaymentProcess();
        }
        else if (PayDollarPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new PayDollarPaymentProcess();
        }
        else if (SwiffpayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new SwiffPayPaymentProcess();
        }
        else if (PayLineVoucherGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new PayVoucherPaymentProcess();
        }
        else if (PayWorldPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new PayWorldPaymentProcess();
        }
        else if (NMIPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new NMIPaymentProcess();
        }
        else if (DeltaPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new DeltaPayPaymentProcess();
        }
        else if (FrickBankPaymentGateWay.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new FrickBankPaymentProcess();
        }
        else if (SafeChargePaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new SafeChargePaymentProcess();
        }
        else if (FluzznetworkPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new FluzznetworkPaymentProcess();
        }
        else if (IPAYDNAPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new IPAYDNAPaymentProcess();
        }
        else if (PaygatewayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new PaygatewayPaymentProcess();
        }
        else if (Gold24PaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new Gold24PaymentProcess();
        }
        else if (PayforasiaPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new PayforasiaPaymentProcess();
        }else if (GCPPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new PayforasiaPaymentProcess();
        }
        else if (AlliedWalletPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new AlliedPaymentProcess();
        }
        else if (PfsPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new PfsPaymentProcess();
        }
        else if (SBMPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new SBMPaymentProcess();
        }
        else if (DVGPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new DVGPaymentProcess();
        }
        else if (STSPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new STSPaymentProcess();
        }
        else if (EmaxHighRiskPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new EmaxHighRiskPaymentProcess();
        }
        else if (ReitumuBankSMSPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new ReitumuBankPaymentProcess();
        }
        else if (ReitumuBankMerchantPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new ReitumuBankMerchantPaymentProcess();
        }
        else if (PaySafeCardPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new PaySafeCardPaymentProcess();
        }
        else if (ProcessingPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new ProcessingPaymentProcess();
        }
        else if (SofortPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new SofortPaymentProcess();
        }
        else if (IdealPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new IdealPaymentProcess();
        }
        else if (OPXPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new OPXPaymentProcess();
        }
        else if (ProcesosMCPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new ProcesosMCPaymentProcess();
        }
        else if (PayOnGateway.GATEWAY_TYPE_PAYON.equalsIgnoreCase(gateway))
        {
            return new PayOnPaymentProcess();
        }
        else if (PayOnGateway.GATEWAY_TYPE_CATELLA.equalsIgnoreCase(gateway))
        {
            return new PayOnPaymentProcess();
        }
        else if (VisaNetPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new VisaNetPaymentProcess();
        }
        else if (INICICIPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new INICICIPaymentProcess();
        }
        else if (AuxPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new AuxpayPaymentProcess();
        }
        else if (PayMitcoPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new PayMitcoPaymentProcess();
        }
        else if (InPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new InPayPaymentProcess();
        }
        else if (PaySecPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new PaysecPaymentProcess();
        }
        else if (P4PaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new P4PaymentProcess();
        }
        else if (PbsPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new PbsPaymentProcess();
        }
        else if (TrustsPayPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new TrustsPayPaymentProcess();
        }
        else if (P4CreditCardPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new P4CreditCardPaymentProcess();
        }
        else if (CashFlowPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new CashFlowPaymentProcess();
        }
        else if (WonderlandPayPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new WonderlandPayPaymentProcess();
        }
        else if (ClearSettlePaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new ClearSettlePaymentProcess();
        }
        else if (B4PaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new B4PaymentProcess();
        }
        else if (ApcoPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new ApcoPayPaymentProcess();
        }
        else if (KotakAllPayCardPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new KotakPaymentProcess();
        }
        else if (DectaMerchantPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new DectaPaymentProcess();
        }
        else if (DectaSMSPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new DectaPaymentProcess();
        }
        else if (PaySpacePaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new PaySpacePaymentProcess();
        }
        else if (PerfectMoneyPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new PerfectMoneyPaymentProcess();
        }
        else if (NetellerPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new NetellerPaymentProcess();
        }
        else if (SkrillPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new SkrillPaymentProcess();
        }
        else if (BillDeskPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new BillDeskPaymentProcess();
        }
        else if (JetonPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new JetonPaymentProcess();
        }
        else if (VoucherMoneyPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new VoucherMoneyPaymentProcess();
        }
        else if (BankonePaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new BankonePaymentProcess();
        }
        else if (RavePaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new RavePaymentProcess();
        }
        else if (EpayPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new EpayPaymentProcess();
        }
        else if (TrustlyPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new TrustlyPaymentProcess();
        }
        else if (PayneticsGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new PayneticsPaymentProcess();
        }
        else if(EmexpayPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new EmexpayPaymentProcess();
        }
        else if(EMSPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new EMSPaymentProcess();
        }
        else if(UnicreditPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new UnicreditPaymentProcess();
        }
        else if(TransactiumPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new TransactiumPaymentProcess();
        }
        else if(ClearSettleHPPGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new ClearSettleHPPPaymentProcess();
        }
        else if(DusPayDDPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new DusPayDDPaymentProcess();
        }
        else if (LpbPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new LpbPaymentProcess();
        }
        else if (ApcoFastpayPaymentGateway.GATEWAY_TYPE_APC0_ALDRAPAY.equals(gateway))
        {
            return new ApcoFastpayPaymentProcess();
        }
        else if (ApcoFastpayPaymentGateway.GATEWAY_TYPE_APCO_PURPLEPAY.equals(gateway))
        {
            return new ApcoFastpayPaymentProcess();
        }
        else if (RSPPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new RSPPaymentProcess();
        }
        else if (NPayOnGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new NPayOnPaymentProcess();
        }
        else if (NestPayPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new NestPaymentProcess();
        }
        else if (ZotapayPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new ZotaPayPaymentProcess();
        }
        else if (TransactiumPaymentGateway.GATEWAY_TYPE_TRX.equals(gateway))
        {
            return new TransactiumPaymentProcess();
        }
        else if (SabadellPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new SabadellPaymentProcess();
        }
        else if (SecureTradingGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new SecureTradingPaymentProcess();
        }
        else if (UnionPayInternationalPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new UnionPayInternationalPaymentProcess();
        }
        else if (ECPPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new ECPPaymentProcess();
        }
        else if (FlutterWavePaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new FlutterWavePaymentProcess();
        }
        else if (CardPayPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new CardPayPaymentProcess();

        } else if (TransactiumPaymentGateway.GATEWAY_TYPE_TRANSACTIUM_1.equals(gateway))
        {
            return new TransactiumPaymentProcess();
        }
        else if (WLPaymentGateway.GATEWAY_TYPE_AGNIPAY.equals(gateway))
        {
            return new WLPaymentProcess();
        }
        else if (WLPaymentGateway.GATEWAY_TYPE_TRANSACTWORLD.equals(gateway))
        {
            return new WLPaymentProcess();
        }
        else if (WLPaymentGateway.GATEWAY_TYPE_SHIMOTOMO.equals(gateway))
        {
            return new WLPaymentProcess();
        }
        else if (WLPaymentGateway.GATEWAY_TYPE_SAMPLEPSP.equals(gateway))
        {
            return new WLPaymentProcess();
        }
        else if (WLPaymentGateway.GATEWAY_TYPE_FIDOMS.equals(gateway))
        {
            return new WLPaymentProcess();
        }
        else if (AwepayPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new AwepayPaymentProcess();
        }
        else if (CyberSourcePaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new CyberSourcePaymentProcess();
        }
        else if (RomCardPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new RomCardPaymentProcess();
        }
        else if (ElegroPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new ElegroPaymentProcess();
        }
        else if (BeekashPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new BeekashPaymentProcess();
        }
        else if (PaySendPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new PaySendPaymentProcess();
        }
        else if (PaySendWalletPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new PaySendPaymentProcess();
        }
        else if (NexiPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new NexiPaymentProcess();
        }
        else if (DectaNewPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new DectaNewPaymentProcess();
        }
        else if(NinjaWalletPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new NinjaWalletPaymentProcess();
        }
        else if(PayeezyPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new PayeezyPaymentProcess();
        }
        else if(PhoneixPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new PhoneixPaymentProcess();
        }
        else if(SafexPayPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new SafexPayPaymentProcess();
        }
        else if(EMerchantPayPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new EMerchantPayPaymentProcess();
        }
        else if(LetzPayPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new LetzPayPaymentProcess();
        } 
        else if(VervePaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new VervePaymentProcess();
        }
        else if(QikpayPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new QikPayPaymentProcess();
        }
        else if(BhartiPayPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new BhartiPayPaymentProcess();
        }else if(TWDTaiwanPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new TWDTaiwanPaymentProccess();
        }else if(GlobalPayPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new GlobalPayPaymentProcess();
        }else if(TrustPayOppwaPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new NPayOnPaymentProcess();
        }else if(IppoPayPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new IppoPayPaymentProcess();
        }
        else if(AsianCheckoutPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new AsianCheckoutPaymentProcess();
        }
        else if(DokuPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new DokuPaymentProcess();
        }

        else if(CashFreePaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new CashFreePaymentProcess();
        }

        else if(EasyPaymentzPaymentGateway.GATEWAY_TYPE.equals(gateway))
        {
            return new EasyPaymentzPaymentProcess();
        }

        else
        {
            return new CommonPaymentProcess();
        }
    }
}