package practice;

import com.payment.exceptionHandler.PZTechnicalViolationException;
import org.codehaus.jettison.json.JSONException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.transaction.vo.restVO.RequestVO.Date;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Admin on 6/12/2020.
 */
public class EcommpayTest
{
        public static void main(String[] args) throws PZTechnicalViolationException
        {

//test();
        }

        public static void test() throws PZTechnicalViolationException
        {
                String url="https://api.accentpay.com/v2/payment/card/3ds_result";
                String accesskey="55e1a71afa4afa809528bf1b767897f6344af784af4f802a7ee171e5127cbbfe1ed1f5cdab65cd1f58a4d3b73e7e2af3f1326210f70f2514355768ca6f31ed79";
                String request="{\n" +
                        "\t\"general\": {\n" +
                        "\t\t\"payment_id\": \"173914\",\n" +
                        "\t\t\"project_id\": 15561,\n" +
                        "\t\t\"signature\": \"NIl2WHq/3RZhqLN+o+Um71JOYnKX819u3bdme5uHFRVBTLKzAt2lBjxE/7iGqpclsuHTVpoCdVN3bP0InGAtNw\\u003d\\u003d\"\n" +
                        "\t},\n" +
                        "\t\"md\": \"eyJwdXJjaGFzZV9vcGVyYXRpb25faWQiOjQwNTcxMDAwMDMyODcxLCJwcm9qZWN0X2lkIjoxNTU2MSwicGF5bWVudF9pZCI6IjE3MzkxNCIsInBsdXNfbWQiOiIifQ\\u003d\\u003d\",\n" +
                        "\t\"pares\": \"eJzNWWnP4kiS/iutno+o2jfgFvVK6fvG98E337cN2NjgX78G6prqmt3e0Wo1SMbpyIzIyLieTPtgF9c0Zaw0vl3Tj4OaDkOYp7+Vyeffz+E1vfwR7RFyh5L7T1EcEp9wGE0+ReQe+UQkaJbEu2wb79HfPw46MNPhxeZdJUcnjHKkfFAmW/eB3a5Vvg6Z0utQ9t0H8gf8B3qAvj6uc17jIuzGj0MYXyhR+8BhgsCIA/Tl8dCmV5H5gN8/5PmH4auAN/kAfefXb8/WsK7jXiYfKgPm98UiasWi6hLAqm081Kr+fICeIw5JOKYfKIzsYQxFfkOIP/HtnwR+gF70w/kpDrT9bZWNviY/QD/SDqvNrmkXPz62OHaAvj0d0vu579In1wH61j5A39U7h93X9bx+6H63zvqkHmz/4zCW7Y9qoX/iuz8R5AC96IdhDMfb8BEcoC+tQxxO0wcAgAIET8esS4iPnM5FaQbv37rc15BDGpcf8GrZ5/3FBZq8v5Zj0T5V/WfCAXqqAr0c+3GwyrxbJ7umv93bphs+/16M4/lPCJrn+Y8Z+6O/5tDTRBBMQuuAZCjzf/z+5koTscv6/xUbHXZ9V8ZhUy7huMaImo5Fn/z2TbdfibHNpyQEMln60yrqU4zg3acnBcYQYpUJ/VroDyv7O7P8rOx1CD8NRYg8J/hJ0MfBTLP0GRHpb44pfv79H/8yM5gyT4fx35n/69w/Svgqzw2bW/px38gmfbwRKJeFimJFVnGPpWWz3aHG569875EH6JvCX1bzdt0PJnoPdLcnJPCDdjP4G5mSvSTgqDM7x6CcpjSl61BdOsjad5dr3KAQCvVcZ+yItjD6ZeSEmyeH8mOatym3BNHRaOR7xHJnRiXnB86qiFHjLj7jcaIhp/Z8h2NlYmAti+qCd1bLtDlCQt7E7RH4DqcL5MUkfW3S3oYrFKtIVXc2/v1CECGLFTfwuNA5KsKwKezSYoHYLMNzzxH2pT0JsP0QNuymC9IrepLPrM9vd8jsDXwLCE2r3RM7ogGMB+tsO2FUOjDFTm6Hi2dTBFsR94oZjxNMbO8zjJNTqbiJUxXnYIfZUllSpshdzpF02UOdzVnu5Xos2TG29d5xyYmavUBk5iMDTy1J5HlW0+Dz5x9C6ItH5PTx9oBPwCQTjuG7RafXsczWWF6rlCqKXLDQNMXecjCLFMhFEyi6yt5mo1SE/YNUOICHChyIM2MEktyfxGKKNWCwCmWAObZZRQU1DxCHpQqVdl31zthAoXLNpUBvU9xJcmD2rixgfNMGW2pO5xhlc8sj4JMv3QLfPEcoUUQ0Za/PaOhpjchyS4ySVehxcOiRN9USZxEEjGsYDHtvvNDXCpF3mQhFxpWnOlmUFGMaEnpEJ7IapVK4z9hr1bbFx1rB18pdI0e3f9Lwn2hzflp1XoD21i+2qRqZYlSbkoXVVQC/1kZTKm975BKgJKYaw0wbL114dpYs12YjlXrbgC5Uy4Elzqklyq6H3IaD3Glc24EbSjX7mX+vQWHvHB14xDV+UEzgS/V6Z08+BSsVe1bp+CUL3NV8lWWZJVhMTs0NR7NFTjufUKKJS8pTbHFW7VU+J+n22s5ztnzqS1sX3hIjjDFYChgOALhIMTN49sugX31sMOi4a9F4vBEAuS17eoHrdHu02Fy/yanuHs+usBjujDui07McdsuzhxEMqUiAbRyRjcIfiw6xvPFChj5udffTo6A629vSPKilHN413Oxadb+Fsq2IGxyxzaFbGVvXtL8wqql68SgPeM7GcZMZSrbBJuYIdVXDlTCnbXa05vqlRDEhDz2Ckr6l6W4n8+w4M4yEsEbgLmfIOqvX1ou3PpPS0LRbPNzHoBON+6DVlCSPfOG+gfYTdMTyGDIJ/uhpQcX2DGyJZtfZ0qxJHKHeRgRm0r5lpjxaA3pzRvytd6SmSafRx8XLRAGTiNlIAD8hjzFgZ4I73acgt+IkZVIboBIK6Yxp76QdRhQgVykA+CrPBXPNK2qxqYbK8yuVsxxlxKvdzZPKxbNkBKI8BxRlOIIKeJ73CjgRwFZ5kFOAabPSvfLjtsbaqKw5EGNgVvIfeWRebL/xNFEnTRE/35K2WQJPK5RWmyKLbJ78kUVUJ1+chXmNOxOuKCqfuR442F69uBY+GdV5a0io1euJkCapXuD9zIDXWBsYAkQBcQYMTT3yFy2jRIFWRR5S6TKXLqAuSqlPBHM+lvvJ9IhHhN6Ht/5cFaxzRyg8nZxVHw++BZg0rLxWL/L6eqlFz0sRbxYx008K7N7ilhwimkCfuR18Wf+xUvGjRVZRhfyPOa/wKrnWnyHCHNJ92mLtjzqziVuiSPhmilpuWPOniVf9AtTJDV9bIlQ7v+sPPkQL4lmuRunoc4x5PrVNtfY1Tuui67i3jPp+jjFjZt720N82MhiQp0ClXrUiYXLDoygbglmeG08lHiwMAmiCUfntbQ8hx7XWEM9aI1iAVat1h0njPOWlFJhZSqWdWZy/+xrkrMeh33yNJVj89vMX28b8eP455+n5nfPAoK/3Y45EAb5nzgyh3z3R0fDogkGhWxxF+TrABZkjzE6j4RS2o+2SBZeodVXKXuaIbrFi4jdxvt3jjNAiiDk1hNdUR9RgRoEtNunFKdoqB3Ceqefelchs+yh6TN5KqQedoNw8+nfeoevHYC8WHpRZxCDLNUzH83y2TGm83zbBNeX1x7oWLSX3xcUAG0OwBK6FUsmgmEXpGd9JNd9PWwmqHTlLaGnOagtqwgLUzbAMO8nNcaFSWWvRp1PyEPjARKlTQ8nkYiD3NvHwGyJFS7ilbieg9OA61LriQhC02dwZnJVJ70KboIy0+kLIzjmuNjizIaWcHo/KTsazIkrBscMvzOIvVzvgVvg/QD+j6C9hlX/CKjh+h1WtgAOboiDmzvbKwvvejO5N8EtYDZf/J1g155nPv8Kq9qsUsyKUhL/AKaIywUN7HYjiWWuecPqE0uDxCmM7mDWk/8/dEqwlWayeMF9firrkyRleU4TlADjSwNiDZz+dy2ubBde6pQnY2PB1azxOKFnL9zOSbDoJx6TZL/jTLuQ4RC9Vwmpo1jJQ9OJCDQWwWQgCfV8iO4g3jECjyhU98eu2uvmiwM3VPMkPTuW2dUsF/hQb4V5PdgRHIx2WZeK1YKtNk0TterpcZLjQs+Dhs6cLcxaK+ta1E7O7LAjFejK705zA5Uw2689umTVy6Z7i4CqUO1oCKaUFu+SRT62odoKTCrg2MhZxyaCOZAwjy9xHRxSSVxJ6Su13nd2Mj/2SVCU6VrsOoJsA0xXZlBzsgYhRXFz4pvMv9zCUs9pWtKE8XUjdnuXwuu6Aby02+hlxw9tbs5grzhaos3FqXOnGVuXoCFIuUu11jptuZpEBBqD6tTQNFU2DEP8LLHF2yTDLftAxsAgDwkN5qopHxp938czmP8JSPq+wSkHP3GL+j8vpMw7mr3C3yq9oCloA2jNWrvbBHAom/N9BloK84I8xvB9i0V0hGjPeEMg88uMF4EV1+gadyaqJ0ja35K8wXsXtPL3gzNGoN5QiK6RpKyyRU0ITz/45D8I8D47rRfNZe68D/7U+7pdbA7tfjkw9BSj+FSYb47lV8NzHExbfcu+T7r7y9N+FUyhA3cc6rl1zdx3vWif/tM75zl2bb5aEAceX34w9BbL9uldV19wLZsF42f5IUQHLaRAjN85NgcVtWtNJfWEwE+v8Ck/lfw17FBlsOJIcCwEci/upN2KbpCO671x6jFZU85oM7Sipu4VG2mdjcpS9sYKDo13tdCeUj142x9HJEqgtdlVbpXTb3ekiul3i3SeKMhXEjvNoDnOmU3JwNVRZ6AL0xhuZXrFr5QNd56WBlCWb2JTcfitjJk5FBr9wlhCiJRpet1DvL6L2CO/AT3oexukF1WG93SLYWjDPFbfuexsLwtcKmCJ2DU3CYHfczDU1ubpI2bsFFW4rHM7CkLtB26zsdnu5P9fkNhcSIGrIGAEE1bT2uosM5OwMcCcLxWLwvenntyDZOcGRLNFBc2mEJgDG7ZP+PA8TasjoBsMuMHFEIZzJOw5viduwNV0SZ/4e7DH9M7X7y1fYM1g8QGwThqhJoTaewyEnEAd/KcMOBxjq+tPJynUWVlPB8PVkxbp1Yxs2O6n0l1PZXW3/jdC8CwwI39CjrqD2hp4fU0vkXuk6C0WsqYw4a5WIrvf7kVFR70mrXjRYZcBdq9iHV/3n6f73T4P05JKEJ7TbAm3pe0NnCkcOgo7TnL0v43Fk4AiG9DapCnQviT5zGY62LgjoRZEq+KplrjicBArUONucud3A6PNgmkfW2yCGvI/M0JV32Ba96nIuIDnE1qdcF9ECFzDdXfBFmEZhH/ZuMyfu6eIhirULcq59nGcUM29zEUB75LJ16OVxhW2d2JEnMylYtN+3tI7OeKNL5BLHGweDrxeUJB9HcVmaK3bndot1q0bOG8KOHAK/nPkEotpe7lXvxpxFqMY3vTzwfNrYexzY1AYep2Uv65yTH615AHwDN85lLyryOU2doDzxPUbdtd1u3GjzmEBV25TcWoX4ZeCFOzVloZL46TaQUJaA8PHradCoVk+B/etkwM5PyDI4dT0rrCXvF6cIlv/nU4ThHkVV1x8zFDNqujjRhsLnHNcyZlx+kUOvrQwLfIijMv8e00p9xTXPMqFz58uOPc9q0T5GtXQi3XMwCbq4YGx1DfPZvvFic6GtAYbNIQ5xSuQHpu/3wfiot1nqeAGn4bpKT1ygYxUiLpKC1DWR3Uooi3i5GONIxxQqMxLaPZMQcdUla2M7I1WxuzImmaqmpcXYY8tuc16W2pMH+SRB8J3sfF68C0FSc0OymzCV3OzIQPbuaXG6w2ZkrBi8j/jWR7dEdjydZ2V29rwTsK0FUJinboER7rhaVmM9njcbJjpdVAAirFW0i+lvKb6EkUvHwCRmuDzUhVAP+j0BmFbaZVBBY+mtFLumoO7bhzJi5H7pVKi6htsjIu5aG+En7qzkvh4IzPx8xfaXEgh9f7cGfXvf9v1N3OsDw+ujyPON+I8fS/4LjyZCjw\\u003d\\u003d\"\n" +
                        "}";
                String response= doPostHTTPSURLConnectionClient(url,request,accesskey);
                System.out.println("response-->"+response);
        }
    public static void sale()
    {
                    String key = "qwertyuiasdfghj12345t6y";
                    String saleurl = "https://api-developers.ecommpay.com/v2/payment/card/sale";
                    int payment_type = 1;
                    int amount = 500;
                    int id = 129;
                    int amount1 = 1;
                    int rate = 1000;
                    boolean fire_safety_act_indicator = true;
                    int price = 1000;
                    int tax = 8;
                    int year = 2023;
                    int month = 2;

                    String request = "{" +
                            " \"general\":" + "{" +
                            "\"project_id\":\"2345\"," +
                            "\"payment_id\":\"231\"," +



                    "\"card\":{" +
                            "\"pan\":\"2345\"," +
                            "\"year\":" + year + "," +
                            "\"month\":" + month + "," +
                            "\"card_holder\":\"abc\"}," +

                            "\"customer\":{" +
                            "\"id\":\"254\"," +
                            "\"country\":\"IND\"," +
                            "\"city\":\"Mumbai\"," +
                            "\"state\":\"MH\"," +
                            "\"phone\":\"08987786576\"," +
                            "\"home_phone\":\"09087786576\"," +
                            "\"work_phone\":\"09087786575\"," +
                            "\"day_of_birth\":\"2345\"," +
                            "\"birthplace\":\"30-08-2000\"," +
                            "\"first_name\":\"abc\"," +
                            "\"middle_name\":\"def\"," +
                            "\"last_name\":\"pqr\"," +
                            "\"email\":\"abc@gmail.com\"," +
                            "\"ip_address\":\"192.34.43.1\"," +
                            "\"district\":\"Thane\"," +
                            "\"street\":\"abcdef\"," +
                            "\"building\":\"13\"}," +

                            "\"avs_data\":{" +
                            "\"avs_post_code\":\"1\"," +
                            "\"avs_street_address\":\"2\"}," +

                            "\"payment\":{" +
                            "\"amount\":" + amount + "," +
                            "\"currency\":\"EUR 421\"}," +

                            "\"positions\":{" +
                            "\"price\":" + price + "," +
                            "\"position_description\":\"1\"," +
                            "\"tax\":" + tax + "}," +

                            "\"payments\":{" +
                            "\"payment_type\":" + payment_type + "," +
                            "\"amount\":" + amount + "}," +

                            "\"interface_type\":{" +
                            "\"id\":" + id + "}," +

                            "\"receipt_data\":{" +
                            "\"positions\":{" +
                            "\"amount\":" + amount1 + "}" +
                            "  }," +

                            "\"addendum\":{" +
                            "\"lodging\":{" +
                            "\"customer_service_toll_free_number\":\"2345\"," +
                            "\"check_in_date\":\"2031\"," +
                            "\"check_out_date\":\"12\"," +
                            "\"folio_number\":\"2345\"}" +
                            "}," +

                            "\"room\":{" +
                            "\"rate\":" + rate + "," +
                            "\"fire_safety_act_indicator\":" + fire_safety_act_indicator + "}," +

                            "  }" +
                            "}";

                    System.out.println("Request-->" + request);
                    String response = doPostHTTPSURLConnectionClient(saleurl, request);
                    System.out.println("response-->" + response);


/*
catch (PZTechnicalViolationException e)
{
        e.printStackTrace();
}
*/


    }

        public static void auth()
        {

                String authurl = "https://api-developers.ecommpay.com/v2/payment/card/auth";
                int project_id=1209;
                int year=2025;
                int month=10;
                int payment_type =1;
                int amount =500;
                int id =129;
                int amount1=1;
                int price=1000;
                int tax =8;
                int rate=1000;
                boolean fire_safety_act_indicator=true;
                String request ="{" + "" +
                        " \"general\":" + "{" +
                        "\"project_id\":"+project_id+"," +
                        "\"payment_id\":\"787\","+
                        "\"signature\":\"78\","+
                        "},"+

                        "\"card\":{"+
                        "\"pan\":\"87\","+
                        "\"year\":"+year+"," +
                        "\"month\":"+month+"," +
                        "\"card_holder\":\"abcdefghi\"},"+

                        "\"avs_data\":{" +
                        "\"avs_post_code\":\"9\"," +
                        "\"avs_street_address\":\"6\"}," +

                        "\"customer\":{" +
                        "\"id\":\"254\"," +
                        "\"country\":\"IND\"," +
                        "\"city\":\"Mumbai\"," +
                        "\"state\":\"MH\"," +
                        "\"phone\":\"08987786576\"," +
                        "\"home_phone\":\"09087786576\"," +
                        "\"work_phone\":\"09087786575\"," +
                        "\"day_of_birth\":\"2345\"," +
                        "\"birthplace\":\"30-08-2000\"," +
                        "\"first_name\":\"abc\"," +
                        "\"middle_name\":\"def\"," +
                        "\"last_name\":\"pqr\"," +
                        "\"email\":\"abc@gmail.com\"," +
                        "\"ip_address\":\"192.34.43.1\"," +
                        "\"district\":\"Thane\"," +
                        "\"street\":\"abcdef\"," +
                        "\"building\":\"13\"}," +

                        "\"payment\":{" +
                        "\"amount\":"+amount+"," +
                        "\"currency\":\"EUR 421\"}," +

                        "\"positions\":{" +
                        "\"price\":"+price+"," +
                        "\"position_description\":\"EUR 421\"," +
                        "\"tax\":"+tax+"}," +

                        "\"payments\":{" +
                        "\"payment_type\":" + payment_type + ","  +
                        "\"amount\":" + amount + "}," +

                        "\"receipt_data\":{" +
                        "\"positions\":{" +
                        "\"amount\":"+ amount1 + "}" +
                        "  }," +

                        "\"interface_type\":{" +
                        "\"id\":" + id + "}," +

                        "\"addendum\":{" +
                        "\"lodging\":{" +
                        "\"customer_service_toll_free_number\":\"2345\"," +
                        "\"check_in_date\":\"2031\"," +
                        "\"check_out_date\":\"12\"," +
                        "\"folio_number\":\"2345\"}" +
                        "}," +

                        "\"room\":{" +
                        "\"rate\":"+rate+"," +
                        "\"fire_safety_act_indicator\":"+fire_safety_act_indicator+"}," +

                        "  }" +
                        "}";
                System.out.println("Request-->" + request);
                String response = doPostHTTPSURLConnectionClient(authurl, request);
                System.out.println("response-->" + response);
        }
        public static void capture()
        {
                String captureurl = "https://api-developers.ecommpay.com/v2/payment/card/capture";
                int project_id=900;
                int id=4;
                String request = "{" + "" +
                        " \"general\":" + "{" +
                        "\"project_id\":"+project_id+"," +
                        "\"payment_id\":\"245\"," +
                        "\"signature\":\"345\"," +
                        "},"+

                        "\"interface_type\":{" +
                        "\"id\":" + id + "}," +


"}"+"}";
                System.out.println("Request-->" + request);
                String response = doPostHTTPSURLConnectionClient(captureurl, request);
                System.out.println("response-->" + response);

        }

        public static void cancel()
        {
                String cancelurl = "https://api-developers.ecommpay.com/v2/payment/card/cancel";
                int project_id=80;
                int id=2;
                int amount=5;
                String request = "{" + "" +
                        " \"general\":" + "{" +
                        "\"project_id\":"+project_id+"," +
                        "\"payment_id\":\"205\"," +
                        "\"signature\":\"315\"," +
                        "},"+

                        "\"interface_type\":{" +
                        "\"id\":" + id + "}," +

                        "\"receipt_data\":{" +
                        "\"positions\":{" +
                        "\"amount\":"+ amount + "}" +
                        "  }," +

                        "}"+"}";

                System.out.println("Request-->" + request);
                String response = doPostHTTPSURLConnectionClient(cancelurl, request);
                System.out.println("response-->" + response);

        }

        public static void refund()
        {
                String refundurl = "https://api-developers.ecommpay.com/v2/payment/card/refund";
                int project_id = 20;
                int id = 1;
                int amount1=1000;
                int amount = 5;
                int price=2;
                int tax=5;
                int payment_type=16;


                String request = "{" + "" +
                        " \"general\":" + "{" +
                        "\"project_id\":" + project_id + "," +
                        "\"payment_id\":\"295\"," +
                        "\"signature\":\"375\"," +
                        "}," +


                        "\"interface_type\":{" +
                        "\"id\":" + id + "}," +


                        "\"positions\":{" +
                        "\"price\":" + price + "," +
                        "\"position_description\":\"EUR 421\"," +
                        "\"tax\":" + tax + "}," +

                        "\"payments\":{" +
                        "\"payment_type\":" + payment_type + ","  +
                        "\"amount\":" + amount + "}," +

                        "\"payment\":{" +
                        "\"amount\":"+amount1+"," +
                        "\"description\":\"421\"}," +

                        "\"receipt_data\":{" +
                        "\"positions\":{" +
                        "\"amount\":"+ amount1 + "}" +
                        "  }," +
                        "}"+"}";

                System.out.println("Request-->" + request);
                String response = doPostHTTPSURLConnectionClient(refundurl, request);
                System.out.println("response-->" + response);

        }

        public static void payout()
        {
                String payouturl = "https://api-developers.ecommpay.com/v2/payment/card/payout";
                int project_id = 1209;
                int year = 2025;
                int month = 10;
                int payment_type = 1;
                int amount = 500;
                int id = 129;
                int amount1 = 1;
                int price = 1000;
                int tax = 8;
                int rate = 1000;
                boolean fire_safety_act_indicator = true;

                String request = "{" + "" +
                        " \"general\":" + "{" +
                        "\"project_id\":" + project_id + "," +
                        "\"payment_id\":\"295\"," +
                        "\"signature\":\"375\"," +
                        "}," +

                        "\"card\":{" +
                        "\"pan\":\"87\"," +
                        "}," +

                        "\"interface_type\":{" +
                        "\"id\":" + id + "}," +

                        "\"avs_data\":{" +
                        "\"avs_post_code\":\"9\"," +
                        "\"avs_street_address\":\"6\"}," +

                        "\"customer\":{" +
                        "\"id\":\"254\"," +
                        "\"country\":\"IND\"," +
                        "\"city\":\"Mumbai\"," +
                        "\"state\":\"MH\"," +
                        "\"phone\":\"08987786576\"," +
                        "\"home_phone\":\"09087786576\"," +
                        "\"work_phone\":\"09087786575\"," +
                        "\"day_of_birth\":\"2345\"," +
                        "\"birthplace\":\"30-08-2000\"," +
                        "\"first_name\":\"abc\"," +
                        "\"middle_name\":\"def\"," +
                        "\"last_name\":\"pqr\"," +
                        "\"email\":\"abc@gmail.com\"," +
                        "\"ip_address\":\"192.34.43.1\"," +
                        "\"district\":\"Thane\"," +
                        "\"street\":\"abcdef\"," +
                        "\"building\":\"13\"}," +

                        "\"payment\":{" +
                        "\"amount\":" + amount + "," +
                        "\"currency\":\"EUR 421\"}," +

                        "\"receipt_data\":{" +
                        "\"positions\":{" +
                        "\"amount\":"+ amount1 + "}" +
                        "  }," +

                        "}"+"}";
                System.out.println("Request-->" + request);
                String response = doPostHTTPSURLConnectionClient(payouturl, request);
                System.out.println("response-->" + response);

        }

        public static String getSignature(List list,String privateKey)
        {
                Collections.sort(list);

                StringBuffer sb= new StringBuffer();
                for(int i=0;i<list.size();i++)
                {
                        sb.append(list.get(i));
                        if(i<list.size()-1)
                        {
                                sb.append("|");
                        }
                }

                System.out.println("sb ===" + sb.toString());
                String signature = HmacSHA512(sb.toString(), privateKey);
                System.out.println("signature ="+signature);

                return signature;
        }

                public static String doPostHTTPSURLConnectionClient(String url, String request)
        {
                String result = "";
                PostMethod post = new PostMethod(url);
                try
                {
                        HttpClient httpClient = new HttpClient();
                        post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
                        post.setRequestBody(request);
                        httpClient.executeMethod(post);
                        String response = new String(post.getResponseBody());
                        result = response;
                }
                catch (HttpException e)
                {
                        System.out.println("HttpException --->" + e);
                }
                catch (IOException e)
                {
                        System.out.println("IOException --->" + e);
                }
                finally
                {
                        post.releaseConnection();
                }

                return result;
        }
        public static String encryptData(String message, String _encryptionKey)
        {
                try
                {
                        final byte[] digestOfPassword = _encryptionKey.getBytes("utf-8");
                        final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);

                        final SecretKey key = new SecretKeySpec( keyBytes , "DESede");
                        final Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
                        cipher.init(Cipher.ENCRYPT_MODE, key);
                        final byte[] plainTextBytes = message.getBytes("utf-8");
                        final byte[] cipherText = cipher.doFinal(plainTextBytes);
                        return Base64.getEncoder().encodeToString(cipherText);
                }
                catch (Exception e)
                {
                        e.printStackTrace();
                        return "";
                }
        }
        public static String HmacSHA512(String message,String key)
        {
                MessageDigest md = null;
                try
                {
                        Mac sha512_HMAC = Mac.getInstance("HmacSHA512");
                        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
                        sha512_HMAC.init(secret_key);


                        byte raw[] = sha512_HMAC.doFinal(message.getBytes(StandardCharsets.UTF_8));



                        String hex = bytesToHex(raw);
                        System.out.println("=====hex2 String=="+hex);
                        return hex; //step 6
                }
                catch(Exception e)
                {
                        System.out.println("exception "+ e);
                        return null;
                }

        }
        private static String bytesToHex(byte[] hashInBytes) {

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < hashInBytes.length; i++) {
                        sb.append(Integer.toString((hashInBytes[i] & 0xff) + 0x100, 16).substring(1));
                }
                return sb.toString();

        }

        public static String doPostHTTPSURLConnectionClient(String strURL,String req,String accesskey) throws PZTechnicalViolationException

        {
                String result = "";
                PostMethod post = new PostMethod(strURL);
                try
                {    HttpClient httpClient = new HttpClient();

                        post.addRequestHeader("Content-Type", "application/json");
                        post.addRequestHeader("Authorization", "Bearer "+accesskey);
                        post.setRequestBody(req);
                        httpClient.executeMethod(post);

                        String response = new String(post.getResponseBody());
                        result= response;
                }
                catch (HttpException he){
                        he.printStackTrace();
                }
                catch (IOException io){
                        io.printStackTrace();
                }
                finally
                {
                        post.releaseConnection();
                }
                return result;
        }

}

