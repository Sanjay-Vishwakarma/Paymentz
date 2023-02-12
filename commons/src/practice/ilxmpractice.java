/*

package practice;

import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.ilixium.IlixiumUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncodingException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.ResourceBundle;



public class ilxmpractice extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "ilixium";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.ilixium");
    private static TransactionLogger transactionLogger = new TransactionLogger(ilxmpractice.class.getName());
    private static Logger log = new Logger(ilxmpractice.class.getName());
    private static IlixiumUtils ilixiumUtils = new IlixiumUtils();

    public ilxmpractice(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processAuthentication(String trackingId, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("BillDeskPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }
    public GenericResponseVO sale(String trackingID,GenericRequestVO requestVO)
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO= new Comm3DResponseVO();
        Functions functions = new Functions();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String mID = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String username = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String termurl="";
        String notifyurl="";
        String saleurl="";
        String amount="";
        String Dateofbirth="";
        String firstname=addressDetailsVO.getFirstname();
        String lastname = addressDetailsVO.getLastname();
        String street = addressDetailsVO.getStreet();
        boolean isTest=gatewayAccount.isTest();
        if(functions.isValueNull(addressDetailsVO.getBirthdate()))
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateFormat1= new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("ddMMyyyy");
            try
            {
                if(addressDetailsVO.getBirthdate().contains("-"))
                {
                    Dateofbirth=dateFormat2.format(dateFormat1.parse(addressDetailsVO.getBirthdate());
                }
            else
            {
                Dateofbirth=dateFormat2.format(dateFormat1.parse(addressDetailsVO.getBirthdate()));
            }
            }  catch(ParseException e)
                {
                    transactionLogger.error("Parse Exception "+e);
                }
        }
if(isTest)
{
    saleurl= RB.getString("TEST_AUTH_URL");
}
        else
{
    saleurl-RB.getString("LIVE_AUTH_URL");
}

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}



*/
