package com.payment.common.core;

import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.directi.pg.core.paymentgateway.*;
import com.directi.pg.core.valueObjects.*;
import com.payment.Ecospend.EcospendPaymentGateway;
import com.payment.Ecospend.EcospendRequestVo;
import com.payment.FrickBank.core.FrickBankPaymentGateWay;
import com.payment.FrickBank.core.FrickBankRequestVO;
import com.payment.Wirecardnew.WireCardRequestVO;
import com.payment.arenaplus.core.ArenaPlusPaymentGateway;
import com.payment.arenaplus.core.ArenaPlusRequestVO;
import com.payment.borgun.core.BorgunPaymentGateway;
import com.payment.borgun.core.BorgunRequestVO;
import com.payment.credorax.core.CredoraxRequestVO;
import com.payment.cupUPI.UnionPayInternationalPaymentGateway;
import com.payment.cupUPI.UnionPayInternationalRequestVO;
import com.payment.europay.core.EuroPayPaymentGateway;
import com.payment.europay.core.EuroPayRequestVO;
import com.payment.icici.INICICIPaymentGateway;
import com.payment.icici.INICICIRequestVO;
import com.payment.nestpay.NestPayPaymentGateway;
import com.payment.nestpay.NestPayRequestVO;
import com.payment.paynetics.core.PayneticsGateway;
import com.payment.paynetics.core.PayneticsRequestVO;
import com.payment.payon.core.PayOnGateway;
import com.payment.payon.core.PayOnRequestVO;
import com.payment.payvision.core.PayVisionPaymentGateway;
import com.payment.payvision.core.PayVisionRequestVO;
import com.payment.procesosmc.ProcesosMCPaymentGateway;
import com.payment.procesosmc.ProcesosMCRequestVO;
import com.payment.smartfastpay.SmartFastPayPaymentGateway;
import com.payment.tigergateway.TigerGateWayPaymentGateway;
import com.payment.tigerpay.TigerPayPaymentGateway;
import com.payment.visaNet.VisaNetPaymentGateway;
import com.payment.visaNet.VisaNetRequestVO;
import com.payment.wealthpay.WealthPayGateway;
import com.payment.wealthpay.WealthPayRequestVO;
import com.payment.websecpay.core.WSecPaymentGateway;
import com.payment.websecpay.core.WSecRequestVO;
import com.payment.wirecard.core.WireCardPaymentGateway;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 5/13/13
 * Time: 10:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class PaymentProcessRequestVOFactory
{
    public static CommRequestVO getRequestVOInstance(int accountId)
    {
        GatewayAccount gatewayAccount   = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
        GatewayType gatewayType         = GatewayTypeService.getGatewayType(gatewayAccount.getPgTypeId());
        String gateway                  = gatewayType.getGateway();

        switch (gateway)
        {
            case UGSPaymentGateway.GATEWAY_TYPE_FORT:
                return new UGSPayRequestVO();

            case UGSPaymentGateway.GATEWAY_TYPE_UGS:
                return new UGSPayRequestVO();

            case PayVisionPaymentGateway.GATEWAY_TYPE:
                return new PayVisionRequestVO();

            case WSecPaymentGateway.GATEWAY_TYPE:
                return new WSecRequestVO();

            case PayOnGateway.GATEWAY_TYPE_PAYON:
                return new PayOnRequestVO();

            case PayOnGateway.GATEWAY_TYPE_CATELLA:
                return new PayOnRequestVO();

            case CredoraxPaymentGateway.GATEWAY_TYPE:
                return new CredoraxRequestVO();

            case EuroPayPaymentGateway.GATEWAY_TYPE:
                return  new EuroPayRequestVO();

            case WireCardPaymentGateway.GATEWAY_TYPE:
                return  new WireCardRequestVO();

            case BorgunPaymentGateway.GATEWAY_TYPE:
                return  new BorgunRequestVO();

            case ArenaPlusPaymentGateway.GATEWAY_TYPE:
                return  new ArenaPlusRequestVO();

            case PayDollarPaymentGateway.GATEWAY_TYPE:
                return new PayDollarRequestVO();

            case SwiffpayPaymentGateway.GATEWAY_TYPE:
                return new SwiffpayRequestVO();

            case PayLineVoucherGateway.GATEWAY_TYPE:
                return new PayLineVoucherRequestVO();

            case FrickBankPaymentGateWay.GATEWAY_TYPE:
                return new FrickBankRequestVO();

            case SafeChargePaymentGateway.GATEWAY_TYPE:
                return new SafeChargeRequestVO();

            case ProcesosMCPaymentGateway.GATEWAY_TYPE:
                return new ProcesosMCRequestVO();

            case VisaNetPaymentGateway.GATEWAY_TYPE:
                return new VisaNetRequestVO();

            case INICICIPaymentGateway.GATEWAY_TYPE:
                return new INICICIRequestVO();

            case NestPayPaymentGateway.GATEWAY_TYPE:
                return new NestPayRequestVO();

            case UnionPayInternationalPaymentGateway.GATEWAY_TYPE:
                return new UnionPayInternationalRequestVO();

            case EcospendPaymentGateway.GATEWAY_TYPE:
                return new EcospendRequestVo();

            case PayneticsGateway.GATEWAY_TYPE:
                return new PayneticsRequestVO();

            case TigerPayPaymentGateway.GATEWAY_TYPE:
                return new TigerGatePayRequestVO();

            case TigerGateWayPaymentGateway.GATEWAY_TYPE:
                return new TigerGatePayRequestVO();

            case WealthPayGateway.GATEWAY_TYPE:
                return new WealthPayRequestVO();

            case SmartFastPayPaymentGateway.GATEWAY_TYPE:
                return new SmartFastPayRequestVO();

            default:
                return new CommRequestVO();
        }
    }
}
