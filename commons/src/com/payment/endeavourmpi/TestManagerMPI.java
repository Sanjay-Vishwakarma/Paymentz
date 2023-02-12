package com.payment.endeavourmpi;

import com.directi.pg.SystemError;
import com.payment.Wirecardnew.WirecardUtills;
import com.payment.borgun.core.BorgunUtills;
import com.payment.borgun.core.InquiryTransaction;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.skrill.SkrillUtills;
import org.apache.commons.lang.StringUtils;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 11/13/2017.
 */
public class TestManagerMPI
{
    public static void main(String[] args) throws PZTechnicalViolationException
    {
        EndeavourMPIGateway endeavourMPIGateway=new EndeavourMPIGateway();
      /*  try
        {
            EnrollmentRequestVO enrollmentRequestVO=new EnrollmentRequestVO();

            *//*"mid=Test" +
                    "&name=Thomas Jefferson" +
                    "&pan=4018490000000013" +
                    "&expiry=3012" +
                    "&currency=978" +
                    "&amount=1234" +
                    "&desc=CD Collection" +
                    "&useragent=Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US)" +
                    "&accept=en-us" +
                    "&trackid=101803134322657";*//*

            enrollmentRequestVO.setMid("Test");
            enrollmentRequestVO.setName("Thomas Jefferson");
            enrollmentRequestVO.setPan("4018490000000013");
            enrollmentRequestVO.setExpiry("3012");
            enrollmentRequestVO.setCurrency("978");
            enrollmentRequestVO.setAmount("1234");
            enrollmentRequestVO.setDesc("CD Collection");
            enrollmentRequestVO.setUseragent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US)");
            enrollmentRequestVO.setAccept("en-us");
            enrollmentRequestVO.setTrackid("101803134322657");


            EnrollmentResponseVO enrollmentResponseVO= endeavourMPIGateway.processEnrollment(enrollmentRequestVO);
            System.out.println("request in manager:::::"+enrollmentResponseVO.getEnrollmentResponse());

*//*
            Map<String, String> responseMap = EndeavourMPIUtils.getQueryMap(enroll);

            System.out.println("result of Enrollment request is:::::"+responseMap.get("result"));
            System.out.println("Enrollment pareq value:::::"+responseMap.get("pareq"));

            String pareq=responseMap.get("pareq");
            String url=responseMap.get("url");
            System.out.println("URL is:::::"+url);
            String trackid=responseMap.get("tracktrackid");
            String url1 = java.net.URLDecoder.decode(url, "UTF-8");
            System.out.println(":::::url is:::::"+url1);*//*



        }
        catch (PZTechnicalViolationException e)
        {
            e.printStackTrace();
        }*/

        try{
            ParesDecodeRequestVO paresDecodeRequestVO=new ParesDecodeRequestVO();
         //   paresDecodeRequestVO.setMassageID("epg67144312-46f6-4294-b2e7-d5b5ab34d3a017");
            //paresDecodeRequestVO.setPareq("eJydVtmSosoWfa8I/6HC81hhMzpVUJzIBEQsEgtFAd8QUkAGB1CQrz+Jdld7O+7DvSd5YOdi75Vr5QAIf9dZ+nrF5yI+5B9d5gfdfcW5fwjiPPzorqxJb9T9WxSs6IyxvMT+5YxFAeGi8EL8GgcfXXwMWR7T9I4OejzHjHu8x3q9UeBzPY7fDUcMt915bL8rCl9ggYt7jWLIynrIsfyAHQ14njz7KUAk4/9gBepXl4x09iMvL0XB809QM0Tm3gTqZ1fI8FmTRX7QXgOBenQF6nfd16WNCqK6jgPRsBcJyrR6bgHWbVzGUBUGsbPUsM0PgWozhMArscjSzJCMQ7/So/f+6J0bC9QdF44tHcgOF8LNsBwvUM+IQObnTKbvJo6HI4H67gm4Ph5yTDKIue9YoH6LO3q5SP9nGxJuggqWIwplnD2JYth3hn7nOYG640JReuWlEF2B+hkJvne9igAACSZBtIK+qSwUYLITEzwaMXtPEbAfi3QritzvVSAND+e4jDKReeT8BgSqlaLUJc7b1Xn1CRz7XvrR3XlpgbuP/fDrcRx0qdZiu+qisIzDnEg741ey3/LioxuV5fGdoqqq+lFxPw7nkGKJZ4oeUyQhKOLwr+6jCgdavjuIguTlhJQMFzdeSfgRLqND8Pot779RWouWlaEWitQjtD2f4fNei9Ac02/lfcv6X9j+FHguvF4ReUxLtMA73K41fl0ttI8u0W6dvbzYHc5Z8RT/fwPg/IrTwxEHveKXzvucPlPLcYiL8t/I/yX9wbD20gsW+3Wz+GJKtlL4ohoO6xuw9vuwynBWkS3znClQ35ZJ/LxQ33P6SJTWwMzllW9vxlG4u+rSTDqal3N4U0yNGvUbZF+nq1X+5uTz3fxsKekJTC62vYxsfzbQonoKYGag/Zy/6E7n5Rib+UTasHai7a/AV51Y4yg76Yd9eKIdo66MW7gauEtkQa5aoiPHBElep5SbTI7h9C2issAyM/kN6h5haxLm7VLQp7QIhuPxsqQy7eNh5smA8IlvD2dOnx7LXuk9Igmfy3hHdiR5LyBNkzxLkkDwGYJKgyDUlJW6zpw9MGCYnKIkVscVDYG5mgAZ9pFZVJLpymvTVJVqZqxlZY5ApQJmpXReJIgmlj0pAlWp5T1AMDTWEPgWXMPjxllEaGFWSniv/lSqotlMjNTl1rfNijGXlmIjaN6ZpBoZnRdy7G8bCVobZ8Z6tpH6jeIjeLhngBpZARdw+t5IfHa9D5xZunXgUc+M69YCeFLRNdqDPiKbYC6vuM4LkmceQRujaVHtjrYYmia11IDZQ6trgYRZIZOvZHDXqSnV2nDtmnatZ6ewRpOVmqat04kMlo/qgyU1RrL6qcdawxlauNXkwaQrFSO7zizRiGs/3xxddt2gRVV1XtTHnMhKbRBP/gWxi2ZjG4yfrUtfTS4uOy6Rpu4QoFVpeVKX2paTTaVdFQB41QCyBGPzE4amJHVe9quxC1mvHn2ttBDsaD6VTauZqn0Hc5u3L1RvKuSFNUfp9Mk3kfEVXNa1NtEPyufWPtNvx8hkGAQv1g2vOy/saWpMizhTip1VJI06OkW544fNYBZfl3GS5TdP52fW1ojGB/Nr8DaNofPp7u0xXHonHckna4Ix5KQrPOSdF10d7GCqN8m2f0Xp2PYqTQYmgH86gw9nEEyUqQLGG91Q58M4tHCgBkqW2rqDmc7Lorgxg5NLLUol2EaLslkwVMkteU1Vy0uqm6eynOtyzpqD5IqN/RYoxXmE0Wc+2F/2icMmvAclO5r05QFP2KikCIoCTn2L22g7I9PV47w5Irmhvj5LnNFwuRs47iWv+nGT8c3ITvtMsdWlYdjf6docxSz53P55wh7I4/RR3yfy91m9f/TvvyXtC/L5d+UfNw3kPA");
            paresDecodeRequestVO.setTrackid("101803134322657");

            ParesDecodeResponseVO paresDecodeResponseVO=endeavourMPIGateway.processParesDecode(paresDecodeRequestVO);

           /* System.out.println("decoded pares value is:::::"+paresDecodeResponseVO.getPares());
            System.out.println("20 byte cavv:::::"+paresDecodeResponseVO.get_20BytesBinaryCAVV());
            System.out.println("20 byte xid:::::"+paresDecodeResponseVO.get_20BytesBinaryXID());*/
        }
        catch (Exception e){
           // e.printStackTrace();
        }
    }
}
