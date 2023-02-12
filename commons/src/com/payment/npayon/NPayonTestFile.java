package com.payment.npayon;

import com.payment.clearsettle.ClearSettleResponseVO;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.List;

/**
 * Created by admin on 6/20/2018.
 */
public class NPayonTestFile
{
    public static void main(String[] args) throws Exception
    {
        //ClearSettleResponseVO responseVO = new ClearSettleResponseVO();
        NPayOnResponseVO nPayOnResponseVO = new NPayOnResponseVO();
        //String request = "{\"id\":\"8a82944a6403c85e01641c5f39ee0eaa\",\"paymentType\":\"DB\",\"paymentBrand\":\"VISA\",\"result\":{\"code\":\"200.100.501\",\"description\":\"invalid or missing customer\"},\"card\":{\"bin\":\"411111\",\"last4Digits\":\"1111\",\"holder\":\"Jane Jones\",\"expiryMonth\":\"10\",\"expiryYear\":\"2018\"},\"buildNumber\":\"70aa1aa78614cae59c9114048ae01cdd4b98e25f@2018-06-19 10:17:49 +0000\",\"timestamp\":\"2018-06-20 08:46:00+0000\",\"ndc\":\"8a82941763da0a720163de4f11b1174f_639baa9437144cfba9701711162755b2\"}";
        String request = "{\"id\":\"8a82944a6424257201642bd395df3b57\",\"paymentType\":\"PA\",\"paymentBrand\":\"MASTER\",\"merchantTransactionId\":\"64872\",\"result\":{\"code\":\"000.200.000\",\"description\":\"transaction pending\"},\"card\":{\"bin\":\"521234\",\"last4Digits\":\"1234\",\"holder\":\"null\",\"expiryMonth\":\"12\",\"expiryYear\":\"2020\"},\"customer\":{\"givenName\":\"Sandip\",\"surname\":\"Kolekar\",\"email\":\"sandip.k@pz.com\",\"ip\":\"127.0.0.1\"},\"billing\":{\"street1\":\"Malad\",\"city\":\"Mumbai\",\"state\":\"MH\",\"postcode\":\"400064\",\"country\":\"IN\"},\n" +
                "\n" +
                "\"redirect\":{\"url\":\"https://test.ppipe.net/connectors/demo/simulator.link?ndcid=8a82941763da0a720163de4f11b1174f_c9f2ee7bb7234377b43c187defa7d68d&REMOTEADDRESS=10.2.20.202\",\"parameters\":[{\"name\":\"PaReq\",\"value\":\"IT8ubu+5z4YupUCOEHKsbiPep8UzIAcPKJEjpwGlzD8#KioqKioqKioqKioqMTIzNCMxMDIuMDAgVVNEIw\"},{\"name\":\"MD\",\"value\":\"8a82944a6424257201642bd396353b5f\"},{\"name\":\"connector\",\"value\":\"THREEDSECURE\"},{\"name\":\"TermUrl\",\"value\":\"https://test.ppipe.net/connectors/asyncresponse_simulator;jsessionid=FD122A0F9DC5F5E571E75A8197858479.sbg-vm-con02?asyncsource=THREEDSECURE&ndcid=8a82941763da0a720163de4f11b1174f_c9f2ee7bb7234377b43c187defa7d68d\"}]},\"risk\":{\"score\":\"0\"},\"buildNumber\":\"70aa1aa78614cae59c9114048ae01cdd4b98e25f@2018-06-19 10:17:49 +0000\",\"timestamp\":\"2018-06-23 08:47:24+0000\",\"ndc\":\"8a82941763da0a720163de4f11b1174f_c9f2ee7bb7234377b43c187defa7d68d\"}";
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        nPayOnResponseVO = objectMapper.readValue(request, NPayOnResponseVO.class);
        //System.out.println("nPayOnResponseVO::::" + nPayOnResponseVO.getResult().description);
        System.out.println("nPayOnResponseVO::::" + nPayOnResponseVO.getRedirect().getUrl());
        List<Parameter> parameters = nPayOnResponseVO.getRedirect().getParameters();
        for (Parameter parameter : parameters)
        {
            System.out.println("============================");
            System.out.println("Name:" + parameter.getName());
            System.out.println("Value:" + parameter.getValue());
            System.out.println("============================");
        }




    }
}
