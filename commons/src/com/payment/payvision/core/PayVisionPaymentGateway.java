package com.payment.payvision.core;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.payvision.core.type.*;
import com.payment.payvision.core.type.Void;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 4/27/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class PayVisionPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "PayVision";
    public static final int ECOMMERECE = 1;
    public static final int MOTO = 2;
    public static final int RECURRING = 4;
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.payvision");
    private static Logger log = new Logger(PayVisionPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PayVisionPaymentGateway.class.getName());

    public PayVisionPaymentGateway() {}

    public PayVisionPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public static void main(String[] args)
    {
        PayVisionResponseVO payVisionResponseVO = new PayVisionResponseVO();
        String status = "";
        try
        {

            int memberId = 142967;
            // int memberId =142971;
            String memberGuId = "D2949DD1-C8D2-4C2F-9985-5F3DDDE2362D";
            // String memberGuId = "a8ba7862-642b-4586-9c8f-185c74831a4f";
            String amount = "100.00";
            String trackingMemberCode = "96716";
            String cardNumber = "5200000000000106";
            //String cardNumber ="4907639999990022";
            String cardHolder = "uday";
            // int cardExpiryMonth = 12;
            int cardExpiryMonth =04;
            Short cardExpiryYear = 2022;
            String cardCVV ="578";
            int currencyId = 840;
            int countryId=356;
            int merchantAccountType = ECOMMERECE;

            boolean isTest = true;

            CheckEnrollment checkEnrollment = new CheckEnrollment();
            checkEnrollment.setMemberid(memberId);
            checkEnrollment.setMemberGuid(memberGuId);
            checkEnrollment.setAmount(amount);
            checkEnrollment.setTrackingMemberCode(trackingMemberCode);
            checkEnrollment.setCardNumber(cardNumber);
            checkEnrollment.setCardholder(cardHolder);
            checkEnrollment.setCardExpiryMonth(cardExpiryMonth);
            checkEnrollment.setCardExpiryYear(cardExpiryYear);
            checkEnrollment.setCurrencyId(currencyId);

            /*PaymentUsingIntegratedMPI paymentUsingIntegratedMPI= new PaymentUsingIntegratedMPI();
            paymentUsingIntegratedMPI.setMemberId(memberId);
            paymentUsingIntegratedMPI.setMemberGuid(memberGuId);
            paymentUsingIntegratedMPI.setCountryId(356);
            paymentUsingIntegratedMPI.setEnrollmentId(669523);
            paymentUsingIntegratedMPI.setEnrollmentTrackingMemberCode("Transaction-999656");
            paymentUsingIntegratedMPI.setCardCvv(cardCVV);
            paymentUsingIntegratedMPI.setMerchantAccountType(merchantAccountType);
            paymentUsingIntegratedMPI.setTrackingMemberCode("Transaction-752121");
            paymentUsingIntegratedMPI.setPayerAuthenticationResponse("eNqdWNmS6riyfSeCf6jo80h32wYDpoMiQh7wADZ4xrx5wgMe8Gz89VdA1a66++w4Z9/LC1I6tZRSZi6ltNbC0vdp1Xeb0t+sRb+q7MB/i7z3P0IyzPC5ROFMIpGEz6PEEv1jsz4Cxa+eCs/WsvP5QnNDn/Kbc0fYx8mQJUlG4UL1DrVbv6yiPNtgf6N/T9fIZxdOVLqhndWbte0WJC9tsOkMny+WxBr5EKxTv+Tpjedf7Cap18iru0a+Rh6bR6uCZveRt7EZIXdiGdOuIqqY/UGOw0TVAC4N4H2NPDTWnl37mymKLtHFdP6G4f+g2D8onPEpX98ecCDNG4iNoTiKrpHvojXcodLP3PuGwOGnH72139/yzIcacH0/2mvky7qbnW3Q7z8MXUBsKF1rp826jtLvVq0eVmHLNfKUr6varptqA9bIR2vt2m27Cau4kZP4whMLB8hg2xoya5sVeex4uNqnytp3ow2KQaPg/3MUSIK8jOow3cxeOl+CNfIwBXm6dLNWoyCDk5X+W58mGXRkWNe3fxCk67q/u9nfeRkg0GAUQVcIVPCqKPjXH69Rvsdnl/z/NIyyszyLXDuJBruGwSH6dZh7bz9s+xWMpjyQMERhqL8g1F8uhmd/PSToDJtDTOTXoN9W9juz/GxsWdl/VaGNPSb4CWizVvyL/4gI/01X+Pc//vU72UFHgV/V/x9TPs34jvCJZ9hJ42/ElJVSvToKndzj6D3f7Q95bNJpd8TfP8e9NNfID9s/Fvby4rfdeikqqkHkleNt8+qwp3d2WejSBOmFalGziHeh79dZenGPeqmxQWxQBN9SaAykGTo382Wbe5bXzlP9EI5HZmYVoXTOV4XHIqfend23J1aLOZFXIy2QL5amMXP01OHV1JJSzDDmgxsLd0WtkixF0wN3WyzUDrXHo36ggpQAoSz0Dstke9ddebNzdHMFiXcMEieWCl5SE3TRAc5P1dAWINSMFejDUkH91XV1M7iVm6Xj0fzInUhd4LHWsFipVianC+qc/e2qYTS3lRHy7De3bIuJkUxjcjylFJu5Te7nZs+14lK7GwUei/7BH49AOEmlGTmcDKUSbuiE7LP4KBYtLxhcK6fFsN+XPVpIsDMXp8HAEHeABO/v36LqwzM7//7yxGmOrmi7tl8ttXFi360lGzIEJb1TdulFmZ280X6avz0dWP75dtC/PgBKhYKvPpWnkE9d/8+3/bsI+SqH+qr2fgij/M836l1X18jP8zwnpvyyji4wryBhijxPmwNFgUoNQMeTIOApoPIdLVvCLj/zYetKQGa2pAy6c8zsRXBlAaYzZChShiH2tAb2ZCAZ4xEcKpLMKrdNoudi4D6kJMjFrS6FbqrcnHRbydNV7ZjG3ZrqAWzfXZpRRRI8EamuE8Rrch2PzuYctc3zTVTkjgks2pBlmga1fj5JKM9JmBWRtKwBf9uhvUgzdzF2eykGc4kWbCi7v2RyPx59SkWO6KkBCC+bLA0khibKaEd1T3Se6ZSjzSatKBOPlT9kHNOdpYclzoyHSGwMrI8Vacy3FfGM1DomlriplIiK3jEvxD3T9bR1Eq62OQ8diiRlTOw5DThwn54oorZlvqMoiWMSgT5NMic17tArJA+TjgyuRXiN2FWHkg8vAHCggEyAx/fxiAp2sMeAgpl5WCSkRZiQ3pTHBPK41Rcu7lox2DeVqawYRlImfeqskuBkXWsp5tjAz2ek3dcqC21a4DiHLVehHbRWKkaxT/JuVyMLFfHpfaOsxJVIT+hZ57ruQYc844Hwyt9aRl6Y2g0l5LmOZny2LMejsOKXcnEwPB71TgIYEgcPa+YO6J1+F7e8xyb3ZdJtKwOJDzIqixVJRjO1E3RaNZO4OckTXThO7f14NA0c6eQ5ZEtesSkXFCean/OWf7jpRYs1rXlSbp4oTK59JqKPtOWr5WxKApUUiCykrUOrZNxpKLjxCPUZNexCNxQXtLWfFG2Bs1TEGR1haFSzb4p9JUNS3uWH4Vph/YGrUtm72uF82iBix9PwaCbzWdWNR772iiFOERkQAyBSHUuZFKsClp0wPAxL+bINsaPLGo2OSSLPeMnDn9ZJSc4USZ9PwjOyxyNrukXPGvCeaDLObANZVwJW0SPHkNuwp7AStIFc75EAu9p8xz3iSkFjkgy6bQ70Cx+JkYeh4xFmqkQkkT6ai6F5RRe7gflV9tKBxQBJE04MSpxaq0S8+YVHM+FwWK0a4+jADG5305reSlfXV0rV0msGo1Lcxw/50E9rE6craVbMKxQx+7l5O0Ztu3UujKZ3WsqGQe5cm/IitJPlYjxCuNnsFmDGWeoGh5mnlYLdqMuZolBFsO3JtTlSzfmMRwu6XBUrsdwfsbi5BpOhR3APlYkyuOpninl/Edh3vvoVgVEoDQksTr8R2C+3oPPp3yUwengk3yNRXRHGX+JkSuvGjC+S3Yusgs6Qp9v7mU0ay+xpZ4rV0LnxWSXp8ciZCZ2o8B0DXmRAg+tdNZUHyu2cXvutBrQPGhDprZK4MznQ01XrUf+b2MYjUXtQGzNImvRFbZr8Qyay1n8kNhq8iA0iKXjHvezZ0aTxRU7fCOxBZm4GeiYG8guxEilNEM4wnGHmpjakPohE8s89AF23N1hjgFYPnskHMsp0wZn4eefJV/ChkLRYAeQsXPS9w+YKJFXePgCcdUnT5lhs0BeOnV945Zgd6i2bhLeiXXR7m+30y2Uyv1q3pcikd14XG46RbNAq10hQ6zu1OoxHp/SwCJbeniZ2GYstqFVW9jGRk3g6g7Sk4+nkYpkHNkvD9JgvhpBsC7JqL8NqFjLB7iQtGG8nNUYwHjXFUJsLsD90urRwyGInq7NHYADAxv5Azj/ix2M6mRIB6PbAEiz+zAPLweWAkUiSMDl91T2PMU1Hu0CZGg2MjdBjjSvc4cR9RkESP0hBJNEnGh3IJkkqRMTfhdlJM877O49ul2pw38LySNPyUtTA5UkWqsiwNDADUmGC+IhgphMk1nnKsn1GmXaRRbBW1H4+OvTH0QGPjEAmiWanI6sZgRi7ObXVhUhG9MPJnpbcHevBzMkwDDtQi4RcVJ5cL6U4lN1wp0V7Ta6C+QUp1ETCxqP92WpaHPA77bA7M2wZCQXQFsK1mOxOYU2reBAXt21GHOJzISjLi10yysRz5nNEUNtbwwWx02uXJRiP0i6C+T/QuoAbRhNZO0Irtsl9apQGt30WVL9FAm0MSSDyP0lAAALdHydMYijhTgQoS6kFq/LOjH6GpA5ELvi31IHB/Yvk+V4V0OCrKvhPVQ4kgf9S53xVOT+qBljl9K8qZyu0DiSFR0qNR1zoStD9vRgzM1GzOkkDd/Mhi58y9IcsJnFRrjrqZSvLdIKhD4wkguo5M6wYQMdo0/DmDMzxsSdPIuw6VjMfCb66/y5JjEf/hSYMkRQ/SUL8IAnVma5QOJqERHD59Mh49OkTGKQAsoEEaIqM5B0ZyBTulxenVWSGOpXWalEfl/LBLCgNTOMMmcezFRIgBeqIkStTynhUTieXaHkddnfyLhfnKzdYwpyYIeDU7gS7Q+uthsQzJvaCq98bt8RS/YI5zg86LeKBrV6WBScGxhGmyc7Os2lbxlxv+pTRbyO8x8ncS4TskiBIzcY7WPjTQ728Irjkq1vz7hSR/FEpYKDT4tfJDn33ebbz/ICsjBQXTNYLw+rcJWlxwyOXYf7tbP+V7nj0qU2Dp7ZGAr2D7iSR4dfJHsgg2hIkm5DbTvQlwWciq8tsGRJTrXNlZMd67Szm2IKopcuNZZhZOXAOOc/FlHFPfeU4Na+QTRseibQO/HPCJc1eHtBCmbjTfJ508I7Ud8s4uQCuKKCX1Zja7bmmIhApz5buZBkV6lDLnEgavrOcLjr72LK8c2L2rmTmK+cYKIWpT2/nEFYMu/uZW4FfJjvydW1Cflylvi5Zz9ek54PX4/3j+0PY/wAWXzU0");
            // paymentUsingIntegratedMPI.setPayerAuthenticationResponse("eNqdWFuTqkgSfjfC/9Ax++jMAIqKE3ZHFHdQUO7KGze5yEUuAvLrt9Tu0z1nOnbPri9WJVlfZVVmfpmw1qMqCGgt8K5V8LaWgrp2wuAl9l9/86fGNM00LMi3xxuXzH0LR397W++BGtQPhcdo2QVCqXtRQAVXuyOc/WTI0zSncLF+hdptUNVxkb9hf6J/TtfIxxRuVHmRkzdva8crSUF+w6YzfL5YEmvkXbDOgkqg3/zg5FzTZo08p2vkc+X+eh/V0Ow+9t9sWjSlrG+MgU3dc28Hhl/KlnqQafC6Ru4aa99pgrcpii7RxXT+guF/odhfKNzxIV9f7nAgK64Qe46i6Br5KlnDC6qC3Lu9ETh89GO2DvpLkQdQAx7vx3iNfBp3cfI39O8/qHqXrvXD27qJs69Gre5GYcs18pCv68ZprvXbcY28j9ae07ZvAAASWGyaKeA+TFTLshmGDdlhAg/7UFkHXvyGzqFR8P+xCqRhUcVNlN1N/btgjdxNQR4efVtrcZjDzargpc/SHPoxaprLXwjSdd2f3ezPogoRaDCKoCsEKvh1HP7rt+eqwBfyU/E/LaOcvMhjz0njwWlgbEhBExX+yw/bvoPR1TsShqgM9QeE+sPD8PyPuwSdYXOIiXwP+uVkv7LLz8ZWtfNHHTnYfYOfgN7WanAK7hERvBiq8Prbv34lOeg4DOrm/zHlw4yvCB94ppNeg7fzgfGjdH+ZZA7X+cKurFexoRJIUxKvH+uemmvkh+3vB3t68cttPRXdLmqbCUMsQG9PDhjGXwNT8XcrXBxQtbpudIVddpqqmDdnURn4dI44OHGexpxoDhfS9ZLFAUlJkhuPiou+ajp2s92u9oq9GFTKTfzDJPOqSbQwDrQw0ZE4kWJN7VmvvRXkZUoPYopowTHpVzZYsi3AaCEdjxRKmlH74haSjnrsooke5zZJJYftXEDi+aFVK5E+hXtzOGNzeRfJBkr7jn657DyEYqVwmOY9Q2CzfDwSmVAKBSvxHE4UpYvKL7dLtEUcjqhdVWFtbiilxNHiKxVHXqEusrzSd8ROq/b8SThIVLqvDk7e0+PRFRAK5udY3Yg53JIWOqbSvVJb4gWxCHVKCKRp704WqqaX5g3RfN0vmvD19UtUvXtmE9yenjjM0RXtNM5zpF3dJPAa2YEMQcmvlFP5ce6kL3SQFS8PB1a/v+yMzweA0qDgc04VGaRTL/j9ZfsqQb4qoL6mv+6iuPj9hXo1tDXy8z6PjamgauITzCvIl5Ig0NZAUaDWQtAJJAgFCmhCRytHcVPYQtR6MlAYllRAZyfMVgJnDmAGQ0YSZZpST+tgS4ayOR7BpRLJrArHIno+Ad5dSoJCYg058jL14mZsrUxXjWuZt+PUCOH45tGMJpHggUh1nSid0/N4ZFtz1LHsi6QqHRMeaVNRaBo0hn2QUYGXsWNM0ooOArZDe4lmblLi9XIC5jItOlB2e8qUfjz6kEo80VMDEJ82HXWQmrqkoB3VPdAFplP3Dpe2kkLcT36X8Uxny3dL3JkAkbgEHN9PpDNfTiQwcutaWOplciqpRsc8EbdM19PHg3h2rHnkUiSpYFLP68CF9/RAkXSW+Yqipq5FhLBU525m3qBXSCEBMhmey+gcc6sOJe9eAGBHAYUA9+fjERVu4IwBJTPzsVjMyigl/amAieSeNRYe7h0TsL3WlrpiGFmd9Jm7SsPD8dzICc+FQTEjnb7ROGjTAsd5bLmKnLA9ZlKcBKTgdQ2y0JCA3l7VlbSS6Ak96zzP2xmQZ3wQnYVLyygLS7+ghDI30FzIl9V4FNXCUil3pi+g/kEEQ+riUcPcAL0xbhIr+Fx6W6YdW5tIslNQRapJMp5pnWjQmpUm14MyMcT91NmOR9PQlQ++S7bkGZvyYQn5ZC4cg93FKFvs2loH9eJL4uTc5xJ6M0tcqJezKQk0UiTyiD7uWjXnD0PJj0dowGhRF3mRtKCP20nZljhHxbzZEaZOXbfXclsrkJQ3xW4411i/4+tM8c9ONJ9eEakTaKAAspjV3XgU6M8Y4lWJAQkAEtVxlEVxGuC4CSPAsFRObITtPc68GpgsCYyf3v15PKipTZG0fRAfkT0eHacsauvAf6ApOCz6iqGGnGrErqm0UU9hFWhDpdkiIXZ2hI6/x5WKJiQZdmwBjJMQS7GPoeMRZmlELJMBWkiRdUYXm4H5Lnvp8MgAWRcPDEoc2mOF+POTgObibrdaXc29CzO43UwbmpXPXqBW2tFoGIzK8ADfFUM/bSycruVZOa9RxOrn1mUfty3rnhjd6PSMi8LCPV+rk9hOlovxCOFns0uImbbcDS4zz2oVu1Anm6JQVXScyfm6p662jccLulqVK6na7rHkeg4nQ4/gPqoQVXg2bIp5fRLYV776jsAolIYElmRfCOzbK+gC+lcJjB7uyXdPVE+C8Ze6udp6CRNIZPckq7AzlSl7s7n0erR62p1iDXRuYmskLB3uTOwkFRYM8CQDGpxvmqXeUS52du5ZHejvNCDRrJp6MyU0slXrU38ntvFI0u/UxgyyLn9Sm678kEnc8T8SGw2exAaRVLzjn/ZsaNL8JKcvBHYnMy8HPZMA5YlYS5QuijYMZ5i5mQOpDyKRwuMOQNdtTc4coNWDbwmhgjJdaBM/3zz5DD4UkhYngoKDh7512FyFpCo4O4BzHmk5PIcNxsJ1ipOg7vNdw3JpdCnbRbeFfY9xOk3m5+NlKTHZTTCkK8/IDmjVcyxqzY1a7cajQ7ZbhEt/SxObnMMW1Cqv+oQoSDybQVoy8GxyOlo7Ls+ibF8shohsS7JuT8NqFjHh5iAvGH8jX80Qlv1yaKwF2O46Q164ZLlRtNk9MADgkmAg5+/x4zMdbFoA6LbgKB4FWwBHF1dCRiZJwuKNVfcoY7qBdqE6Na8wNiKfM8/whlPvEQVpcicFiUQfaHSoWCSpErFwE2cH3bS3NwFll1p4Y53xSNeLStLB6UEWmsRwNLBCUmXCZI9glhumR3vKcX1OWU6Zx2IHY+in0mHcSwcsGaFCEteNgaxmBGJu5hRriLGCGLuDM634G9aDmZtjGLajFim5qH2lWcpJpHjRRo+3ulKH8xNSaqmMjUdb+3htcSBs9N3GZrgqFkugL8RzOdkcoobW8DApL2xO7BK7FNXlyakYdeK78zkiau3lyoeJ2+unJRiPsi6G+T/Qhoib5jU+bgi9ZNPb1KxMnn00VL9EAm0CSSAOPkhABCLd7ydMaqrRRgIoR2klpwnujH6EpAEkPvxH6sDg/iZ5vnYFNPjsCv5TlwNJ4L/0OZ9dzo+uAXY5/bPLYcXWhaRwT6nxiI88Gbq/lxJmJunHTtbBzbrLkocM/SFLSFxS6o562soxnWgaAyNLoH7sDDsG0DH6NLq4A7O/38mDCLuO0617gq9uv0oS49F/oQlTIqUPkpDeSUJzpysUriYhEZw+PDIeffgEBimAbCADmiJjZUOGCoUH1cltVYWhDtVxtWj2S2VnlZQOpkmOzJPZCgmREnWl2FModTyqppNTvDwPmxt5U0r7zA9HcU7MEHBoN6LToQ2rI8mMSfzwHPTmJT1qQcns5zuDlvDQ0U7LkpdCcw/TZOMU+bStEr63Asrs2RjvcbLwUzE/pQjScMkGLWV6aJZnBJcDjbVubhkr750CBjo9eVZ26LuP2i4IA7IyM1y0OD+KartLs/KCxx7D/KO2f6c7Hn1o0+ChrZPA6KA7SWT4PtlDBcQsQXIpyXZSIIsBEx+73FEgMTUGX8VOYjTuYo4tiEY+XTiGmVUD75LzQsoY79DXrtsIKnltoz2RNWFgp3x63SoDWqoTb1rM0y4Yj/pumaQnwJcl9LKWUJstf60JRC7ypTdZxqU2NAovkWbgLqeLztm3nOAemK0nW8XK3YdqaRnTix3BjmFzs/kV+DbZkc/XJuTHq9TnS9bjY9Lje9f9+8fX72D/BhfPNLg=");
*/
         /*   boolean isTest = true;
            int memberId=142967;
            String memberGuId="D2949DD1-C8D2-4C2F-9985-5F3DDDE2362D";
            String trackingMemberCode="54148_3DS";
            String transactionDate="/Date(1516439503000)/";
            Inquiry inquiry= new Inquiry();
            inquiry.setMemberId(memberId);
            inquiry.setMemberGuid(memberGuId);
            inquiry.setTrackingMemberCode(trackingMemberCode);
            inquiry.setTransactionDate(transactionDate);*/
/*

            System.setProperty("jsse.enableSNIExtension", "false");
            System.setProperty("https.protocols", "TLSv1.2");
            ClientConfig config = new DefaultClientConfig();
            config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
            Client client = Client.create(config);
            WebResource services = null;
*/

            //services=client.resource(getTestBaseURI());

            // String jsonNotificationResponse = PayVisionPaymentGateway.toJson(checkEnrollment);

            //      System.out.println("jsonNotificationResponse----"+jsonNotificationResponse);

            //   String enrollmentResponse=services.path("GatewayV2").path("ThreeDSecureService.svc").path("json").path("CheckEnrollment").type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(String.class,checkEnrollment);

            //     String enrollmentResponse= PayVisionUtils.doPostHTTPSURLConnectionClient("https://testprocessor.payvisionservices.com/GatewayV2/ThreeDSecureService.svc/json/CheckEnrollment", jsonNotificationResponse);

            //      System.out.println("enrollmentResponse-----"+enrollmentResponse);

          /*  System.out.println("inquiyRequest-----"+"memberid:-"+memberId+"--memberGuid:-"+memberGuId+"--TrackinMembercode:-"+trackingMemberCode+"--TransactionDate:-");

            if (isTest)
            {
                System.out.println("----inside test-----");
                service = client.resource(getTestBaseURI());
            }
            else
            {
                System.out.println("----inside live-----");
                service = client.resource(getLiveBaseURI());
            }

            String response = service.path("GatewayV2").path("BasicOperationsService.svc").path("json").path("RetrieveTransactionResult").type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(String.class,inquiry);*/
            //  String response = service.path("GatewayV2").path("ThreeDSecureService.svc").path("json").path("CheckEnrollment").type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(String.class, checkEnrollment);
            // String response = service.path("GatewayV2").path("ThreeDSecureService.svc").path("json").path("PaymentUsingIntegratedMPI").type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(String.class, paymentUsingIntegratedMPI);
            //   String  response = service.path("GatewayV2").path("BasicOperationsService.svc").path("json").path("Payment").type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(String.class, payment);

            // String response = service.path("GatewayV2").path("BasicOperationsService.svc").path("json").path("Authorize").type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(String.class, authorize);
            //   String response = service.path("GatewayV2").path("BasicOperationsService.svc").path("json").path("Capture").type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(String.class, capture);
            //   String response = service.path("GatewayV2").path("BasicOperationsService.svc").path("json").path("Void").type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(String.class, objVoid);
            //  String  response = service.path("GatewayV2").path("BasicOperationsService.svc").path("json").path("Refund").type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(String.class, refund);
            // String   response = service.path("GatewayV2").path("BasicOperationsService.svc").path("json").path("CardFundTransfer").type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(String.class, payment);
            //   String response = service.path("GatewayV2").path("BasicOperationsService.svc").path("json").path("Payment").type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(String.class, payment);

            // System.out.println("Response-------" + response);
/*
            JSONObject jsonObject = new JSONObject(response);
            Hashtable hashtable= new Hashtable();
d
            JSONArray slideContent = new JSONArray(jsonObject.getJSONObject("CheckEnrollmentResult").getString("Cdc"));
            System.out.println("slideContent.length()---"+slideContent.length());
            for (int j = 0; j < slideContent.length(); j++)
            {
                org.codehaus.jettison.json.JSONObject jsonProductObject = slideContent.getJSONObject(j);
                String name = jsonProductObject.getString("Name");
                String val = jsonProductObject.getString("Items");

                JSONArray jsonArray= new JSONArray(val);
                System.out.println("ItemsLength------"+jsonArray.length());
                for(int k=0; k<jsonArray.length();k++)
                {
                    JSONObject js = jsonArray.getJSONObject(k);
                    String key = js.getString("Key");
                    String value = js.getString("Value");
                    System.out.println("key----" + key);
                    System.out.println("value----" + value);
                    hashtable.put(key, value);
                }
            }

            String MerchantPluginResult=hashtable.get("MerchantPluginResult").toString();
            System.out.println("MerchantPluginResult-----"+MerchantPluginResult);





            if ((!jsonObject.getJSONObject("CheckEnrollmentResult").has("Error")) && jsonObject.getJSONObject("CheckEnrollmentResult").getString("Result").equals("0"))
            {
                status = "success";
                payVisionResponseVO.setStatus(status);
              //  String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
            //    payVisionResponseVO.setDescriptor(descriptor);
            }
            else
            {
                status = "fail";
                payVisionResponseVO.setStatus(status);
            }

          //  payVisionResponseVO.setTransactionStatus(status);
        //    payVisionResponseVO.setTransactionId(jsonObject.getJSONObject("PaymentResult").getString("TransactionId"));
        //    payVisionResponseVO.setAmount(amount);
            payVisionResponseVO.setErrorCode(jsonObject.getJSONObject("CheckEnrollmentResult").getString("Result"));
            payVisionResponseVO.setDescription(jsonObject.getJSONObject("CheckEnrollmentResult").getString("Message"));
            payVisionResponseVO.setTransactionGuid(jsonObject.getJSONObject("CheckEnrollmentResult").getString("EnrollmentId"));
            payVisionResponseVO.setTrackingMemberCode(jsonObject.getJSONObject("CheckEnrollmentResult").getString("TrackingMemberCode"));
            payVisionResponseVO.setPaReq(jsonObject.getJSONObject("CheckEnrollmentResult").getString("PaymentAuthenticationRequest"));
            payVisionResponseVO.setBankTransactionDate(jsonObject.getJSONObject("CheckEnrollmentResult").getString("DateTime"));
            payVisionResponseVO.setTransactionType("sale");

            System.out.println(payVisionResponseVO.getErrorCode());
            System.out.println(payVisionResponseVO.getDescription());
            System.out.println(payVisionResponseVO.getTransactionGuid());
            System.out.println(payVisionResponseVO.getTrackingMemberCode());
            System.out.println(payVisionResponseVO.getPaReq());
            System.out.println(payVisionResponseVO.getBankTransactionDate());
            System.out.println(payVisionResponseVO.getErrorCode());*/



        }catch (Exception e){
            //e.printStackTrace();
        }


    }

    public static long getMilliSecond(String timeStamp){
        Timestamp t= Timestamp.valueOf(timeStamp);
        long milisecond=t.getTime();
        transactionLogger.debug("milisecond-----" + milisecond);
        return milisecond;
    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("-----inside processSale-----");
        PayVisionResponseVO payVisionResponseVO = new PayVisionResponseVO();
        Functions functions= new Functions();
        int countryId=0;


        try
        {
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            boolean isTest = gatewayAccount.isTest();
            String is3dSupported = gatewayAccount.get_3DSupportAccount();
            String isForexMid = gatewayAccount.getForexMid();
            transactionLogger.error("IsForexMid value SALE---for "+trackingID+ "----"+isForexMid);
            String displayName = "";
            String termUrl = "";

            PayVisionRequestVO payVisionRequestVO = (PayVisionRequestVO) requestVO;
            CommMerchantVO commMerchantAccountVO = payVisionRequestVO.getCommMerchantVO();
            CommTransactionDetailsVO transDetailsVO = payVisionRequestVO.getTransDetailsVO();

            CommCardDetailsVO cardDetailsVO = payVisionRequestVO.getCardDetailsVO();
            CommAddressDetailsVO addressDetailsVO = payVisionRequestVO.getAddressDetailsVO();

            transactionLogger.error("host url----for "+trackingID+ "----"+commMerchantAccountVO.getHostUrl());
            if (functions.isValueNull(commMerchantAccountVO.getHostUrl()))
            {
                termUrl = "https://"+commMerchantAccountVO.getHostUrl()+"/transaction/PVFrontEndServlet?referenceNo=";
                transactionLogger.error("from host url----for "+trackingID+ "----"+termUrl);
            }
            else
            {
                termUrl = RB.getString("Term_url");
                transactionLogger.error("from RB----"+termUrl);
            }

            String amount = transDetailsVO.getAmount();
            int memberId = Integer.parseInt(commMerchantAccountVO.getMerchantId());
            String memberGuId = commMerchantAccountVO.getMerchantUsername();
            if(functions.isValueNull(addressDetailsVO.getCountry()))
            {
                if (functions.isValueNull(CountryCodeISO3166.getNumericCountryCode(addressDetailsVO.getCountry())))
                {
                    countryId = Integer.parseInt(CountryCodeISO3166.getNumericCountryCode(addressDetailsVO.getCountry()));
                }
            }
            int currencyId = Integer.parseInt(CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency()));
            String trackingMemberCode = payVisionRequestVO.getTrackingMemberCode();
            String cardNumber = cardDetailsVO.getCardNum();
            String cardHolder = addressDetailsVO.getFirstname() + " " + addressDetailsVO.getLastname();

            String cardExpiryMonth = cardDetailsVO.getExpMonth();
            Short cardExpiryYear = Short.parseShort(cardDetailsVO.getExpYear());
            String cardCVV = cardDetailsVO.getcVV();


            String status = "";
            String response = "";
            String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

            if(functions.isValueNull(gatewayAccount.getDisplayName().trim()) && gatewayAccount.getDisplayName().equals("*"))
            {
                displayName="";
            }
            else if (functions.isValueNull(gatewayAccount.getDisplayName().trim()) && gatewayAccount.getDisplayName().contains("*"))
            {
                displayName = StringUtils.substringBefore(gatewayAccount.getDisplayName().trim(), "*")+"*";
            }
            else
            {
                displayName = gatewayAccount.getDisplayName().trim()+"*";
            }

            String currency="";
            String tmpl_amount="";
            String tmpl_currency="";
            if (functions.isValueNull(transDetailsVO.getCurrency()))
            {
                currency=transDetailsVO.getCurrency();
            }
            if (functions.isValueNull(addressDetailsVO.getTmpl_amount()))
            {
                tmpl_amount=addressDetailsVO.getTmpl_amount();
            }
            if (functions.isValueNull(addressDetailsVO.getTmpl_currency()))
            {
                tmpl_currency=addressDetailsVO.getTmpl_currency();
            }

            String merchantOrganizationName = displayName +  commMerchantAccountVO.getMerchantOrganizationName();
            String partnerSupportContactNumber = commMerchantAccountVO.getPartnerSupportContactNumber();

            int merchantAccountType = ECOMMERECE;

            if (gatewayAccount.getIsDynamicDescriptor().equalsIgnoreCase("Y"))
            {
                if (!merchantOrganizationName.equals("") && merchantOrganizationName.length()!=0 && merchantOrganizationName.length() > 25)
                {
                    merchantOrganizationName = merchantOrganizationName.substring(0,25);
                }
                if (!partnerSupportContactNumber.equals("") && partnerSupportContactNumber.length()!=0 && partnerSupportContactNumber.length() > 13)
                {
                    partnerSupportContactNumber = partnerSupportContactNumber.substring(0,13);
                }
                descriptor = merchantOrganizationName + "|" + partnerSupportContactNumber;
            }

            if ("Y".equals(is3dSupported) || "O".equals(is3dSupported))
            {
                CheckEnrollment checkEnrollment = new CheckEnrollment();
                checkEnrollment.setMemberid(memberId);
                checkEnrollment.setMemberGuid(memberGuId);
                checkEnrollment.setAmount(amount);
                checkEnrollment.setTrackingMemberCode(trackingID+"_S");
                checkEnrollment.setCardNumber(cardNumber);
                checkEnrollment.setCardholder(cardHolder);
                checkEnrollment.setCardExpiryMonth(Integer.parseInt(cardExpiryMonth));
                checkEnrollment.setCardExpiryYear(cardExpiryYear);
                checkEnrollment.setCurrencyId(currencyId);

                CheckEnrollment checkEnrollmentLogs = new CheckEnrollment();
                checkEnrollmentLogs.setMemberid(memberId);
                checkEnrollmentLogs.setMemberGuid(memberGuId);
                checkEnrollmentLogs.setAmount(amount);
                checkEnrollmentLogs.setTrackingMemberCode(trackingID+"_S");
                checkEnrollmentLogs.setCardNumber(functions.maskingPan(cardNumber));
                checkEnrollmentLogs.setCardholder(cardHolder);
                transactionLogger.error("-----exp month-----for "+trackingID+ "----" +functions.maskingNumber(cardDetailsVO.getExpMonth()) + "exp year "+ functions.maskingNumber(cardDetailsVO.getExpYear()));
                //checkEnrollmentLogs.setCardExpiryMonth(Integer.parseInt(functions.maskingNumber(cardExpiryMonth)));
                //checkEnrollmentLogs.setCardExpiryYear(Integer.parseInt(functions.maskingNumber(cardDetailsVO.getExpYear())));
                checkEnrollmentLogs.setCurrencyId(currencyId);

                String checkEnrollmentRequest = PayVisionUtils.toJson(checkEnrollment);
                String checkEnrollmentRequestLogs = PayVisionUtils.toJson(checkEnrollmentLogs);


                transactionLogger.error("-----checkEnrollment request-----for "+trackingID+ "----" +checkEnrollmentRequestLogs);

                String enrollmentResponse="";
                if(isTest){
                    transactionLogger.error("----inside Test-----");
                    enrollmentResponse= PayVisionUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_ENROLLMENT_URL"), checkEnrollmentRequest);

                }else{
                    transactionLogger.error("----inside Live-----");
                    enrollmentResponse= PayVisionUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_ENROLLMENT_URL"), checkEnrollmentRequest);
                }
                transactionLogger.error("-----checkEnrollment Response----for "+trackingID+ "----" +enrollmentResponse);

                if(functions.isValueNull(enrollmentResponse) && enrollmentResponse.contains("{"))
                {
                    JSONObject jsonObject = new JSONObject(enrollmentResponse);
                    Hashtable hashtable = new Hashtable();

                    JSONArray slideContent = new JSONArray(jsonObject.getJSONObject("CheckEnrollmentResult").getString("Cdc"));

                    for (int j = 0; j < slideContent.length(); j++)
                    {
                        org.codehaus.jettison.json.JSONObject jsonProductObject = slideContent.getJSONObject(j);
                        String name = jsonProductObject.getString("Name");
                        String Items = jsonProductObject.getString("Items");

                        JSONArray jsonArray = new JSONArray(Items);
                        for (int k = 0; k < jsonArray.length(); k++)
                        {
                            JSONObject js = jsonArray.getJSONObject(k);
                            String key = js.getString("Key");
                            String value = js.getString("Value");
                            hashtable.put(key, value);
                        }
                    }
                    if (functions.isValueNull((String) hashtable.get("MerchantPluginResult")))
                    {

                        String MerchantPluginResult = hashtable.get("MerchantPluginResult").toString();
                        transactionLogger.error("MerchantPluginResult-----" + MerchantPluginResult);
                        int enrollmentId = Integer.parseInt(jsonObject.getJSONObject("CheckEnrollmentResult").getString("EnrollmentId"));
                        String enrollmentTrackingMemberCode = jsonObject.getJSONObject("CheckEnrollmentResult").getString("TrackingMemberCode");


                        if (("Y".equals(MerchantPluginResult) && jsonObject.getJSONObject("CheckEnrollmentResult").getString("Result").equals("0")))
                        {
                            status = "pending3DConfirmation";
                            payVisionResponseVO.setStatus(status);
                            payVisionResponseVO.setUrlFor3DRedirect(jsonObject.getJSONObject("CheckEnrollmentResult").getString("IssuerUrl"));
                            payVisionResponseVO.setPaReq(jsonObject.getJSONObject("CheckEnrollmentResult").getString("PaymentAuthenticationRequest"));
                            payVisionResponseVO.setMd("");
                            payVisionResponseVO.setTerURL(termUrl + trackingID + "-" + PzEncryptor.encryptCVV(cardCVV));
                            payVisionResponseVO.setAmount(amount);
                            payVisionResponseVO.setRemark(jsonObject.getJSONObject("CheckEnrollmentResult").getString("Message"));
                            payVisionResponseVO.setErrorCode(jsonObject.getJSONObject("CheckEnrollmentResult").getString("Result"));
                            payVisionResponseVO.setDescription(jsonObject.getJSONObject("CheckEnrollmentResult").getString("Message"));
                            payVisionResponseVO.setEnrollmentId(enrollmentId);
                            payVisionResponseVO.setTrackingMemberCode(enrollmentTrackingMemberCode);
                            payVisionResponseVO.setResponseTime(jsonObject.getJSONObject("CheckEnrollmentResult").getString("DateTime"));
                            payVisionResponseVO.setTransactionType(PZProcessType.SALE.toString());
                        }
                        else if ("N".equals(MerchantPluginResult) && !"O".equalsIgnoreCase(is3dSupported))
                        {
                            PaymentUsingIntegratedMPI paymentUsingIntegratedMPI = new PaymentUsingIntegratedMPI();

                            String dateOfBirth = "1990-01-30";
                            String firstSix = Functions.getFirstSix(cardNumber);
                            String lastFour = Functions.getLastFour(cardNumber);

                            paymentUsingIntegratedMPI.setMemberId(memberId);
                            paymentUsingIntegratedMPI.setMemberGuid(memberGuId);
                            paymentUsingIntegratedMPI.setCountryId(countryId);
                            paymentUsingIntegratedMPI.setEnrollmentId(enrollmentId);
                            paymentUsingIntegratedMPI.setEnrollmentTrackingMemberCode(enrollmentTrackingMemberCode);
                            paymentUsingIntegratedMPI.setCardCvv(cardCVV);
                            paymentUsingIntegratedMPI.setMerchantAccountType(merchantAccountType);
                            paymentUsingIntegratedMPI.setTrackingMemberCode(trackingID + "_S");
                            paymentUsingIntegratedMPI.setPayerAuthenticationResponse("");
                            paymentUsingIntegratedMPI.setDbaName(merchantOrganizationName);
                            paymentUsingIntegratedMPI.setDbaCity(partnerSupportContactNumber);

                            PaymentUsingIntegratedMPI paymentUsingIntegratedMPILogs = new PaymentUsingIntegratedMPI();
                            paymentUsingIntegratedMPILogs.setMemberId(memberId);
                            paymentUsingIntegratedMPILogs.setMemberGuid(memberGuId);
                            paymentUsingIntegratedMPILogs.setCountryId(countryId);
                            paymentUsingIntegratedMPILogs.setEnrollmentId(enrollmentId);
                            paymentUsingIntegratedMPILogs.setEnrollmentTrackingMemberCode(enrollmentTrackingMemberCode);
                            paymentUsingIntegratedMPILogs.setCardCvv(functions.maskingNumber(cardCVV));
                            paymentUsingIntegratedMPILogs.setMerchantAccountType(merchantAccountType);
                            paymentUsingIntegratedMPILogs.setTrackingMemberCode(trackingID + "_S");
                            paymentUsingIntegratedMPILogs.setPayerAuthenticationResponse("");
                            paymentUsingIntegratedMPILogs.setDbaName(merchantOrganizationName);
                            paymentUsingIntegratedMPILogs.setDbaCity(partnerSupportContactNumber);


                            if(isForexMid.equalsIgnoreCase("Y"))
                            {
                                Recipient recipient = new Recipient();
                                AdditionalInfo additionalInfo = new AdditionalInfo();
                                recipient.setDateOfBirth(dateOfBirth);
                                recipient.setAccountNumber(firstSix + lastFour);
                                if(functions.isValueNull(commMerchantAccountVO.getZipCode()))
                                    recipient.setPostalCode(commMerchantAccountVO.getZipCode().trim());
                                recipient.setSurname(commMerchantAccountVO.getLastName());

                                additionalInfo.setRecipient(recipient);
                                paymentUsingIntegratedMPI.setAdditionalInfo(additionalInfo);
                                paymentUsingIntegratedMPILogs.setAdditionalInfo(additionalInfo);
                            }


                            String paymentUsingIntegratedMPIRequest = PayVisionUtils.toJson(paymentUsingIntegratedMPI);
                            String paymentUsingIntegratedMPIRequestLogs = PayVisionUtils.toJson(paymentUsingIntegratedMPILogs);


                            transactionLogger.error("-----paymentUsingIntegratedMPI Request-----for " + trackingID + "----" + paymentUsingIntegratedMPIRequestLogs);

                            String paymentUsingIntegratedMPIResponse = "";
                            if (isTest)
                            {
                                transactionLogger.error("----inside Test-----");
                                paymentUsingIntegratedMPIResponse = PayVisionUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_PaymentUsingIntegratedMPI_URL"), paymentUsingIntegratedMPIRequest);

                            }
                            else
                            {
                                transactionLogger.error("----inside Live-----");
                                paymentUsingIntegratedMPIResponse = PayVisionUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_PaymentUsingIntegratedMPI_URL"), paymentUsingIntegratedMPIRequest);
                            }
                            transactionLogger.error("-----PaymentUsingIntegratedMPI Response----for "+trackingID+ "----" + paymentUsingIntegratedMPIResponse);

                            if (functions.isValueNull(paymentUsingIntegratedMPIResponse))
                            {
                                String bankCode = "";
                                String bankApprovalCode = "";
                                String bankMsg = "";
                                jsonObject = new JSONObject(paymentUsingIntegratedMPIResponse);
                                slideContent = new JSONArray(jsonObject.getJSONObject("PaymentUsingIntegratedMPIResult").getString("Cdc"));

                                JSONObject jObj = null;
                                for (int j = 0; j < slideContent.length(); j++)
                                {
                                    jObj = slideContent.getJSONObject(0);
                                }

                                JSONArray itemContent = new JSONArray(jObj.getString("Items"));
                                JSONObject itemObj = null;
                                Map bankResMap = new HashMap<>();
                                for (int j = 0; j < itemContent.length(); j++)
                                {
                                    itemObj = itemContent.getJSONObject(j);
                                    bankResMap.put(itemObj.getString("Key"), itemObj.getString("Value"));

                                }
                                if (bankResMap != null)
                                {
                                    if (bankResMap.get("BankCode") != null && bankResMap.get("BankMessage") != null)
                                    {
                                        bankCode = (String) bankResMap.get("BankCode");
                                        bankApprovalCode = (String) bankResMap.get("BankApprovalCode");
                                        bankMsg = (String) bankResMap.get("BankMessage");
                                    }
                                    else if (bankResMap.get("ErrorCode") != null && bankResMap.get("ErrorMessage") != null)
                                    {
                                        bankCode = (String) bankResMap.get("ErrorCode");
                                        bankMsg = (String) bankResMap.get("ErrorMessage");
                                    }
                                }
                                if(!functions.isValueNull(bankMsg)){
                                    bankMsg=jsonObject.getJSONObject("PaymentUsingIntegratedMPIResult").getString("Message");
                                }

                                if ((!jsonObject.getJSONObject("PaymentUsingIntegratedMPIResult").has("Error")) && jsonObject.getJSONObject("PaymentUsingIntegratedMPIResult").getString("Result").equals("0"))
                                {
                                    status = "success";
                                    payVisionResponseVO.setStatus(status);
                                    //String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                                    payVisionResponseVO.setDescriptor(descriptor);
                                }
                                else
                                {
                                    status = "fail";
                                    payVisionResponseVO.setStatus(status);
                                }

                                payVisionResponseVO.setTransactionStatus(status);
                                payVisionResponseVO.setRemark(jsonObject.getJSONObject("PaymentUsingIntegratedMPIResult").getString("Message"));
                                payVisionResponseVO.setTransactionId(jsonObject.getJSONObject("PaymentUsingIntegratedMPIResult").getString("TransactionId"));
                                payVisionResponseVO.setAmount(amount);
                                payVisionResponseVO.setErrorCode(bankCode);
                                payVisionResponseVO.setAuthCode(bankApprovalCode);
                                payVisionResponseVO.setDescription(bankMsg);
                                payVisionResponseVO.setTransactionGuid(jsonObject.getJSONObject("PaymentUsingIntegratedMPIResult").getString("TransactionGuid"));
                                payVisionResponseVO.setTrackingMemberCode(jsonObject.getJSONObject("PaymentUsingIntegratedMPIResult").getString("TrackingMemberCode"));
                                payVisionResponseVO.setResponseTime(jsonObject.getJSONObject("PaymentUsingIntegratedMPIResult").getString("TransactionDateTime"));
                                payVisionResponseVO.setTransactionType(PZProcessType.SALE.toString());
                            }
                        }else if("U".equals(MerchantPluginResult) && !"O".equalsIgnoreCase(is3dSupported)){
                            Payment payment = new Payment();

                            payment.setMemberId(memberId);
                            payment.setMemberGuid(memberGuId);
                            payment.setCountryId(countryId);
                            payment.setAmount(amount);
                            payment.setCurrencyId(currencyId);
                            payment.setTrackingMemberCode(trackingID + "_S");
                            payment.setCardNumber(cardNumber);
                            payment.setCardholder(cardHolder);
                            payment.setCardExpiryMonth(Integer.valueOf(cardExpiryMonth));
                            payment.setCardExpiryYear(cardExpiryYear);
                            payment.setCardCvv(cardCVV);
                            payment.setMerchantAccountType(merchantAccountType);
                            payment.setDbaCity(merchantOrganizationName);
                            payment.setDbaName(partnerSupportContactNumber);

                            Payment paymentLogs = new Payment();

                            paymentLogs.setMemberId(memberId);
                            paymentLogs.setMemberGuid(memberGuId);
                            paymentLogs.setCountryId(countryId);
                            paymentLogs.setAmount(amount);
                            paymentLogs.setCurrencyId(currencyId);
                            paymentLogs.setTrackingMemberCode(trackingID + "_S");
                            paymentLogs.setCardNumber(functions.maskingPan(cardNumber));
                            paymentLogs.setCardholder(cardHolder);
                            transactionLogger.error("-----exp month-----for "+trackingID+ "----" +functions.maskingNumber(cardDetailsVO.getExpMonth()) + "exp year "+ functions.maskingNumber(cardDetailsVO.getExpYear()));
                            paymentLogs.setCardCvv(functions.maskingNumber(cardCVV));
                            paymentLogs.setMerchantAccountType(merchantAccountType);
                            paymentLogs.setDbaCity(merchantOrganizationName);
                            paymentLogs.setDbaName(partnerSupportContactNumber);

                            if(is3dSupported.equalsIgnoreCase("Y"))
                            {
                                Recipient recipient = new Recipient();
                                AdditionalInfo additionalInfo = new AdditionalInfo();

                                String firstSix = Functions.getFirstSix(cardNumber);
                                String lastFour = Functions.getLastFour(cardNumber);
                                String dateOfBirth = "1990-01-30";

                                recipient.setDateOfBirth(dateOfBirth);
                                recipient.setAccountNumber(firstSix+lastFour);
                                if(functions.isValueNull(commMerchantAccountVO.getZipCode()))
                                    recipient.setPostalCode(commMerchantAccountVO.getZipCode().trim());
                                recipient.setSurname(commMerchantAccountVO.getLastName());

                                additionalInfo.setRecipient(recipient);
                                payment.setAdditionalInfo(additionalInfo);
                                paymentLogs.setAdditionalInfo(additionalInfo);
                            }

                            String paymentRequest = PayVisionUtils.toJson(payment);
                            String paymentRequestLogs = PayVisionUtils.toJson(paymentLogs);


                            transactionLogger.error("-----payment Request-----for "+trackingID+ "----" + paymentRequestLogs);

                            String paymentResponse = "";
                            if (isTest)
                            {
                                transactionLogger.error("----inside Test-----");
                                paymentResponse = PayVisionUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_SALE_URL"), paymentRequest);

                            }
                            else
                            {
                                transactionLogger.error("----inside Live-----");
                                paymentResponse = PayVisionUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_SALE_URL"), paymentRequest);
                            }
                            transactionLogger.error("-----Payment Response----for "+trackingID+ "----"+ paymentResponse);

                            if (functions.isValueNull(paymentResponse))
                            {

                                String bankCode="";
                                String bankApprovalCode="";
                                String bankMsg="";
                                 jsonObject = new JSONObject(paymentResponse);
                                 slideContent = new JSONArray(jsonObject.getJSONObject("PaymentResult").getString("Cdc"));

                                JSONObject jObj = null;
                                for (int j = 0; j < slideContent.length(); j++)
                                {
                                    jObj = slideContent.getJSONObject(0);
                                }

                                JSONArray itemContent = new JSONArray(jObj.getString("Items"));
                                JSONObject itemObj = null;
                                Map bankResMap = new HashMap<>();
                                for (int j = 0; j< itemContent.length(); j++)
                                {
                                    itemObj = itemContent.getJSONObject(j);
                                    bankResMap.put(itemObj.getString("Key"), itemObj.getString("Value"));

                                }
                                if(bankResMap!=null){
                                    if(bankResMap.get("BankCode")!=null && bankResMap.get("BankMessage")!=null){
                                        bankCode=(String)bankResMap.get("BankCode");
                                        bankApprovalCode = (String) bankResMap.get("BankApprovalCode");
                                        bankMsg=(String)bankResMap.get("BankMessage");
                                    }else if(bankResMap.get("ErrorCode")!=null && bankResMap.get("ErrorMessage")!=null){
                                        bankCode=(String)bankResMap.get("ErrorCode");
                                        bankMsg=(String) bankResMap.get("ErrorMessage");
                                    }
                                }
                                if(!functions.isValueNull(bankMsg)){
                                    bankMsg=jsonObject.getJSONObject("PaymentResult").getString("Message");
                                }

                                if ((!jsonObject.getJSONObject("PaymentResult").has("Error")) && jsonObject.getJSONObject("PaymentResult").getString("Result").equals("0"))
                                {
                                    status = "success";
                                    payVisionResponseVO.setStatus(status);
                                    //descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                                    payVisionResponseVO.setDescriptor(descriptor);
                                }
                                else
                                {
                                    status = "fail";
                                    payVisionResponseVO.setStatus(status);
                                }

                                payVisionResponseVO.setTransactionStatus(status);
                                payVisionResponseVO.setRemark(jsonObject.getJSONObject("PaymentResult").getString("Message"));
                                payVisionResponseVO.setTransactionId(jsonObject.getJSONObject("PaymentResult").getString("TransactionId"));
                                payVisionResponseVO.setAmount(amount);
                                payVisionResponseVO.setErrorCode(bankCode);
                                payVisionResponseVO.setAuthCode(bankApprovalCode);
                                payVisionResponseVO.setDescription(bankMsg);
                                payVisionResponseVO.setTransactionGuid(jsonObject.getJSONObject("PaymentResult").getString("TransactionGuid"));
                                payVisionResponseVO.setTrackingMemberCode(jsonObject.getJSONObject("PaymentResult").getString("TrackingMemberCode"));
                                payVisionResponseVO.setResponseTime(jsonObject.getJSONObject("PaymentResult").getString("TransactionDateTime"));
                                payVisionResponseVO.setTransactionType(PZProcessType.SALE.toString());

                            }
                            payVisionResponseVO.setCurrency(currency);
                            payVisionResponseVO.setTmpl_Amount(tmpl_amount);
                            payVisionResponseVO.setTmpl_Currency(tmpl_currency);
                        }
                        else
                        {
                            payVisionResponseVO.setStatus("fail");
                            payVisionResponseVO.setTransactionStatus(status);
                            payVisionResponseVO.setDescription("Transaction not allowed");
                            payVisionResponseVO.setRemark("Transaction not allowed");
                            payVisionResponseVO.setTransactionType(PZProcessType.SALE.toString());
                        }
                    }
                    else{
                        String bankCode="";
                        String bankMsg="";
                        if(functions.isValueNull((String)hashtable.get("ErrorCode")) || functions.isValueNull((String)hashtable.get("ErrorMessage"))){
                            bankCode=(String)hashtable.get("ErrorCode");
                            bankMsg=(String)hashtable.get("ErrorMessage");
                        }
                        if(!functions.isValueNull(bankMsg)){
                            bankMsg=jsonObject.getJSONObject("CheckEnrollmentResult").getString("Message");
                        }
                        payVisionResponseVO.setStatus("fail");
                        payVisionResponseVO.setTransactionStatus(status);
                        payVisionResponseVO.setRemark(jsonObject.getJSONObject("CheckEnrollmentResult").getString("Message"));
                        payVisionResponseVO.setAmount(amount);
                        payVisionResponseVO.setErrorCode(bankCode);
                        payVisionResponseVO.setDescription(bankMsg);
                        payVisionResponseVO.setTrackingMemberCode(jsonObject.getJSONObject("CheckEnrollmentResult").getString("TrackingMemberCode"));
                        payVisionResponseVO.setResponseTime(jsonObject.getJSONObject("CheckEnrollmentResult").getString("DateTime"));
                        payVisionResponseVO.setTransactionType(PZProcessType.SALE.toString());
                    }
                }
                else
                {
                    payVisionResponseVO.setStatus("fail");
                    payVisionResponseVO.setTransactionStatus(status);
                    payVisionResponseVO.setDescription("Transaction Failed");
                    payVisionResponseVO.setRemark("Transaction Failed");
                    payVisionResponseVO.setTransactionType(PZProcessType.SALE.toString());
                }
            }
            else
            {
                Payment payment = new Payment();

                payment.setMemberId(memberId);
                payment.setMemberGuid(memberGuId);
                payment.setCountryId(countryId);
                payment.setAmount(amount);
                payment.setCurrencyId(currencyId);
                payment.setTrackingMemberCode(trackingID + "_S");
                payment.setCardNumber(cardNumber);
                payment.setCardholder(cardHolder);
                payment.setCardExpiryMonth(Integer.valueOf(cardExpiryMonth));
                payment.setCardExpiryYear(cardExpiryYear);
                payment.setCardCvv(cardCVV);
                payment.setMerchantAccountType(merchantAccountType);
                payment.setDbaCity(merchantOrganizationName);
                payment.setDbaName(partnerSupportContactNumber);


                Payment paymentLogs = new Payment();

                paymentLogs.setMemberId(memberId);
                paymentLogs.setMemberGuid(memberGuId);
                paymentLogs.setCountryId(countryId);
                paymentLogs.setAmount(amount);
                paymentLogs.setCurrencyId(currencyId);
                paymentLogs.setTrackingMemberCode(trackingID + "_S");
                paymentLogs.setCardNumber(functions.maskingPan(cardNumber));
                paymentLogs.setCardholder(cardHolder);
                transactionLogger.error("-----exp month-----for "+trackingID+ "----" +functions.maskingNumber(cardDetailsVO.getExpMonth()) + "exp year "+ functions.maskingNumber(cardDetailsVO.getExpYear()));
                //paymentLogs.setCardExpiryMonth(Integer.valueOf(functions.maskingNumber(cardExpiryMonth)));
                //paymentLogs.setCardExpiryYear(Integer.parseInt(functions.maskingNumber(cardDetailsVO.getExpYear())));
                paymentLogs.setCardCvv(functions.maskingNumber(cardCVV));
                paymentLogs.setMerchantAccountType(merchantAccountType);
                paymentLogs.setDbaCity(merchantOrganizationName);
                paymentLogs.setDbaName(partnerSupportContactNumber);

                if(is3dSupported.equalsIgnoreCase("Y"))
                {
                    Recipient recipient = new Recipient();
                    AdditionalInfo additionalInfo = new AdditionalInfo();

                    String firstSix = Functions.getFirstSix(cardNumber);
                    String lastFour = Functions.getLastFour(cardNumber);
                    String dateOfBirth = "1990-01-30";

                    recipient.setDateOfBirth(dateOfBirth);
                    recipient.setAccountNumber(firstSix+lastFour);
                    if(functions.isValueNull(commMerchantAccountVO.getZipCode()))
                        recipient.setPostalCode(commMerchantAccountVO.getZipCode().trim());
                    recipient.setSurname(commMerchantAccountVO.getLastName());

                    additionalInfo.setRecipient(recipient);
                    payment.setAdditionalInfo(additionalInfo);
                    paymentLogs.setAdditionalInfo(additionalInfo);
                }

                String paymentRequest = PayVisionUtils.toJson(payment);
                String paymentRequestLogs = PayVisionUtils.toJson(paymentLogs);


                transactionLogger.error("-----payment Request-----for "+trackingID+ "----" + paymentRequestLogs);

                String paymentResponse = "";
                if (isTest)
                {
                    transactionLogger.error("----inside Test-----");
                    paymentResponse = PayVisionUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_SALE_URL"), paymentRequest);

                }
                else
                {
                    transactionLogger.error("----inside Live-----");
                    paymentResponse = PayVisionUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_SALE_URL"), paymentRequest);
                }
                transactionLogger.error("-----Payment Response----for "+trackingID+ "----" + paymentResponse);

                if (functions.isValueNull(paymentResponse))
                {
                    String bankCode="";
                    String bankApprovalCode="";
                    String bankMsg="";
                    JSONObject jsonObject = new JSONObject(paymentResponse);
                    JSONArray slideContent = new JSONArray(jsonObject.getJSONObject("PaymentResult").getString("Cdc"));

                    JSONObject jObj = null;
                    for (int j = 0; j < slideContent.length(); j++)
                    {
                        jObj = slideContent.getJSONObject(0);
                    }

                    JSONArray itemContent = new JSONArray(jObj.getString("Items"));
                    JSONObject itemObj = null;
                    Map bankResMap = new HashMap<>();
                    for (int j = 0; j< itemContent.length(); j++)
                    {
                        itemObj = itemContent.getJSONObject(j);
                        bankResMap.put(itemObj.getString("Key"), itemObj.getString("Value"));

                    }
                    if(bankResMap!=null){
                        if(bankResMap.get("BankCode")!=null && bankResMap.get("BankMessage")!=null){
                            bankCode=(String)bankResMap.get("BankCode");
                            bankApprovalCode = (String) bankResMap.get("BankApprovalCode");
                            bankMsg=(String)bankResMap.get("BankMessage");
                        }else if(bankResMap.get("ErrorCode")!=null && bankResMap.get("ErrorMessage")!=null){
                            bankCode=(String)bankResMap.get("ErrorCode");
                            bankMsg=(String) bankResMap.get("ErrorMessage");
                        }
                    }
                    if(!functions.isValueNull(bankMsg)){
                        bankMsg=jsonObject.getJSONObject("PaymentResult").getString("Message");
                    }

                    if ((!jsonObject.getJSONObject("PaymentResult").has("Error")) && jsonObject.getJSONObject("PaymentResult").getString("Result").equals("0"))
                    {
                        status = "success";
                        payVisionResponseVO.setStatus(status);
                        //descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        payVisionResponseVO.setDescriptor(descriptor);
                    }
                    else
                    {
                        status = "fail";
                        payVisionResponseVO.setStatus(status);
                    }

                    payVisionResponseVO.setTransactionStatus(status);
                    payVisionResponseVO.setRemark(jsonObject.getJSONObject("PaymentResult").getString("Message"));
                    payVisionResponseVO.setTransactionId(jsonObject.getJSONObject("PaymentResult").getString("TransactionId"));
                    payVisionResponseVO.setAmount(amount);
                    payVisionResponseVO.setErrorCode(bankCode);
                    payVisionResponseVO.setAuthCode(bankApprovalCode);
                    payVisionResponseVO.setDescription(bankMsg);
                    payVisionResponseVO.setTransactionGuid(jsonObject.getJSONObject("PaymentResult").getString("TransactionGuid"));
                    payVisionResponseVO.setTrackingMemberCode(jsonObject.getJSONObject("PaymentResult").getString("TrackingMemberCode"));
                    payVisionResponseVO.setResponseTime(jsonObject.getJSONObject("PaymentResult").getString("TransactionDateTime"));
                    payVisionResponseVO.setTransactionType(PZProcessType.SALE.toString());

                }
            }
            payVisionResponseVO.setCurrency(currency);
            payVisionResponseVO.setTmpl_Amount(tmpl_amount);
            payVisionResponseVO.setTmpl_Currency(tmpl_currency);
        }
        catch (Exception e){
            log.error("Exception:::::",e);
            PZExceptionHandler.raiseTechnicalViolationException(PayVisionPaymentGateway.class.getName(), "processSale()", null, "common", "Exception while placing  Sale transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return payVisionResponseVO;
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("-----inside processAuthentication-----");
        PayVisionResponseVO payVisionResponseVO = new PayVisionResponseVO();
        Functions functions= new Functions();
        int countryId=0;
        String displayName = "";
        try
        {
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            boolean isTest = gatewayAccount.isTest();
            String is3dSupported = gatewayAccount.get_3DSupportAccount();
            String isForexMid = gatewayAccount.getForexMid();
            String termUrl = "";

            PayVisionRequestVO payVisionRequestVO = (PayVisionRequestVO) requestVO;
            CommMerchantVO commMerchantAccountVO = payVisionRequestVO.getCommMerchantVO();
            CommTransactionDetailsVO transDetailsVO = payVisionRequestVO.getTransDetailsVO();
            CommCardDetailsVO cardDetailsVO = payVisionRequestVO.getCardDetailsVO();
            CommAddressDetailsVO addressDetailsVO = payVisionRequestVO.getAddressDetailsVO();

            String amount = transDetailsVO.getAmount();
            int memberId = Integer.parseInt(commMerchantAccountVO.getMerchantId());
            String memberGuId = commMerchantAccountVO.getMerchantUsername();
            if(functions.isValueNull(addressDetailsVO.getCountry()))
            {
                if (functions.isValueNull(CountryCodeISO3166.getNumericCountryCode(addressDetailsVO.getCountry())))
                {
                    countryId = Integer.parseInt(CountryCodeISO3166.getNumericCountryCode(addressDetailsVO.getCountry()));
                }
            }
            int currencyId = Integer.parseInt(CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency()));
            String trackingMemberCode = "Transaction-" + transDetailsVO.getOrderDesc() + "-" + trackingID;
            String cardNumber = cardDetailsVO.getCardNum();
            String cardHolder = addressDetailsVO.getFirstname() + " " + addressDetailsVO.getLastname();
            String cardExpiryMonth = cardDetailsVO.getExpMonth();
            Short cardExpiryYear = Short.parseShort(cardDetailsVO.getExpYear());
            String cardCVV = cardDetailsVO.getcVV();
            String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

            String currency="";
            String tmpl_amount="";
            String tmpl_currency="";
            if (functions.isValueNull(transDetailsVO.getCurrency()))
            {
                currency=transDetailsVO.getCurrency();
            }
            if (functions.isValueNull(addressDetailsVO.getTmpl_amount()))
            {
                tmpl_amount=addressDetailsVO.getTmpl_amount();
            }
            if (functions.isValueNull(addressDetailsVO.getTmpl_currency()))
            {
                tmpl_currency=addressDetailsVO.getTmpl_currency();
            }

            if (functions.isValueNull(commMerchantAccountVO.getHostUrl()))
            {
                termUrl = "https://"+commMerchantAccountVO.getHostUrl()+"/transaction/PVFrontEndServlet?referenceNo=";
                transactionLogger.error("from host url----" + termUrl);
            }
            else
            {
                termUrl = RB.getString("Term_url");
                transactionLogger.error("from resource bundle----"+termUrl);
            }

            if (functions.isValueNull(gatewayAccount.getDisplayName()) && gatewayAccount.getDisplayName().contains("*"))
            {
                displayName = StringUtils.substringBefore(gatewayAccount.getDisplayName(), "*");
            }
            else
            {
                displayName = gatewayAccount.getDisplayName();
            }

            String merchantOrganizationName = displayName + "*" + commMerchantAccountVO.getMerchantOrganizationName();
            String partnerSupportContactNumber = commMerchantAccountVO.getPartnerSupportContactNumber();


            if (gatewayAccount.getIsDynamicDescriptor().equalsIgnoreCase("Y"))
            {
                if (!merchantOrganizationName.equals("") && merchantOrganizationName.length()!=0 && merchantOrganizationName.length() > 25)
                {
                    merchantOrganizationName = merchantOrganizationName.substring(0,25);
                }
                if (!partnerSupportContactNumber.equals("") && partnerSupportContactNumber.length()!=0 && partnerSupportContactNumber.length() > 13)
                {
                    partnerSupportContactNumber = partnerSupportContactNumber.substring(0,13);
                }
                descriptor = merchantOrganizationName + "|" + partnerSupportContactNumber;
            }



            String status = "";
            String response = "";
            int merchantAccountType = ECOMMERECE;

            if ("Y".equals(is3dSupported))
            {
                CheckEnrollment checkEnrollment = new CheckEnrollment();
                checkEnrollment.setMemberid(memberId);
                checkEnrollment.setMemberGuid(memberGuId);
                checkEnrollment.setAmount(amount);
                checkEnrollment.setTrackingMemberCode(trackingID + "_A");
                checkEnrollment.setCardNumber(cardNumber);
                checkEnrollment.setCardholder(cardHolder);
                checkEnrollment.setCardExpiryMonth(Integer.parseInt(cardExpiryMonth));
                checkEnrollment.setCardExpiryYear(cardExpiryYear);
                checkEnrollment.setCurrencyId(currencyId);

                CheckEnrollment checkEnrollmentLogs = new CheckEnrollment();
                checkEnrollmentLogs.setMemberid(memberId);
                checkEnrollmentLogs.setMemberGuid(memberGuId);
                checkEnrollmentLogs.setAmount(amount);
                checkEnrollmentLogs.setTrackingMemberCode(trackingID+"_S");
                checkEnrollmentLogs.setCardNumber(functions.maskingPan(cardNumber));
                checkEnrollmentLogs.setCardholder(cardHolder);
                transactionLogger.error("-----exp month-----for "+trackingID+ "----" +functions.maskingNumber(cardDetailsVO.getExpMonth()) + "exp year "+ functions.maskingNumber(cardDetailsVO.getExpYear()));
                //checkEnrollmentLogs.setCardExpiryMonth(Integer.parseInt(functions.maskingNumber(cardExpiryMonth)));
                //checkEnrollmentLogs.setCardExpiryYear(Integer.parseInt(functions.maskingNumber(cardDetailsVO.getExpYear())));
                checkEnrollmentLogs.setCurrencyId(currencyId);

                String checkEnrollmentRequest = PayVisionUtils.toJson(checkEnrollment);
                String checkEnrollmentRequestLogs = PayVisionUtils.toJson(checkEnrollmentLogs);

                transactionLogger.error("-----checkEnrollment request-----for "+trackingID+ "----" +checkEnrollmentRequestLogs);

                String enrollmentResponse = "";
                if (isTest)
                {
                    transactionLogger.error("----inside Test-----");
                    enrollmentResponse = PayVisionUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_ENROLLMENT_URL"), checkEnrollmentRequest);

                }
                else
                {
                    transactionLogger.error("----inside Live-----");
                    enrollmentResponse = PayVisionUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_ENROLLMENT_URL"), checkEnrollmentRequest);
                }
                transactionLogger.error("-----checkEnrollment Response----" + enrollmentResponse);

                if (functions.isValueNull(enrollmentResponse))
                {
                    JSONObject jsonObject = new JSONObject(enrollmentResponse);
                    Hashtable hashtable = new Hashtable();

                    JSONArray slideContent = new JSONArray(jsonObject.getJSONObject("CheckEnrollmentResult").getString("Cdc"));

                    for (int j = 0; j < slideContent.length(); j++)
                    {
                        org.codehaus.jettison.json.JSONObject jsonProductObject = slideContent.getJSONObject(j);
                        String name = jsonProductObject.getString("Name");
                        String Items = jsonProductObject.getString("Items");

                        JSONArray jsonArray = new JSONArray(Items);
                        for (int k = 0; k < jsonArray.length(); k++)
                        {
                            JSONObject js = jsonArray.getJSONObject(k);
                            String key = js.getString("Key");
                            String value = js.getString("Value");

                            hashtable.put(key, value);
                        }
                    }
                    if (functions.isValueNull((String) hashtable.get("MerchantPluginResult")))
                    {
                        String MerchantPluginResult = hashtable.get("MerchantPluginResult").toString();
                        transactionLogger.error("MerchantPluginResult-----" + MerchantPluginResult);
                        int enrollmentId = Integer.parseInt(jsonObject.getJSONObject("CheckEnrollmentResult").getString("EnrollmentId"));
                        String enrollmentTrackingMemberCode = jsonObject.getJSONObject("CheckEnrollmentResult").getString("TrackingMemberCode");


                        if (("Y".equals(MerchantPluginResult) && jsonObject.getJSONObject("CheckEnrollmentResult").getString("Result").equals("0")))
                        {
                            status = "pending3DConfirmation";
                            payVisionResponseVO.setStatus(status);
                            payVisionResponseVO.setUrlFor3DRedirect(jsonObject.getJSONObject("CheckEnrollmentResult").getString("IssuerUrl"));
                            payVisionResponseVO.setPaReq(jsonObject.getJSONObject("CheckEnrollmentResult").getString("PaymentAuthenticationRequest"));
                            payVisionResponseVO.setMd("");
                            payVisionResponseVO.setTerURL(termUrl + trackingID + "-" + PzEncryptor.encryptCVV(cardCVV));
                            payVisionResponseVO.setAmount(amount);
                            payVisionResponseVO.setRemark(jsonObject.getJSONObject("CheckEnrollmentResult").getString("Message"));
                            payVisionResponseVO.setErrorCode(jsonObject.getJSONObject("CheckEnrollmentResult").getString("Result"));
                            payVisionResponseVO.setDescription(jsonObject.getJSONObject("CheckEnrollmentResult").getString("Message"));
                            payVisionResponseVO.setEnrollmentId(Integer.parseInt(jsonObject.getJSONObject("CheckEnrollmentResult").getString("EnrollmentId")));
                            payVisionResponseVO.setTrackingMemberCode(jsonObject.getJSONObject("CheckEnrollmentResult").getString("TrackingMemberCode"));
                            payVisionResponseVO.setResponseTime(jsonObject.getJSONObject("CheckEnrollmentResult").getString("DateTime"));
                            payVisionResponseVO.setTransactionType(PZProcessType.AUTH.toString());
                        }
                        else if (("N".equals(MerchantPluginResult) && !"O".equalsIgnoreCase(is3dSupported) ))
                        {
                            AuthorizeUsingIntegratedMPI authorizeUsingIntegratedMPI = new AuthorizeUsingIntegratedMPI();

                            authorizeUsingIntegratedMPI.setMemberId(memberId);
                            authorizeUsingIntegratedMPI.setMemberGuid(memberGuId);
                            authorizeUsingIntegratedMPI.setCountryId(countryId);
                            authorizeUsingIntegratedMPI.setEnrollmentId(enrollmentId);
                            authorizeUsingIntegratedMPI.setEnrollmentTrackingMemberCode(enrollmentTrackingMemberCode);
                            authorizeUsingIntegratedMPI.setCardCvv(cardCVV);
                            authorizeUsingIntegratedMPI.setMerchantAccountType(merchantAccountType);
                            authorizeUsingIntegratedMPI.setTrackingMemberCode(trackingID + "_A");
                            authorizeUsingIntegratedMPI.setPayerAuthenticationResponse("");
                            authorizeUsingIntegratedMPI.setDbaName(merchantOrganizationName);
                            authorizeUsingIntegratedMPI.setDbaCity(partnerSupportContactNumber);

                            if(isForexMid.equalsIgnoreCase("Y"))
                            {
                                Recipient recipient = new Recipient();
                                AdditionalInfo additionalInfo = new AdditionalInfo();

                                String firstSix = Functions.getFirstSix(cardNumber);
                                String lastFour = Functions.getLastFour(cardNumber);

                                String dateOfBirth = "1990-01-30";

                                recipient.setDateOfBirth(dateOfBirth);
                                recipient.setAccountNumber(firstSix+lastFour);
                                if(functions.isValueNull(commMerchantAccountVO.getZipCode()))
                                    recipient.setPostalCode(commMerchantAccountVO.getZipCode().trim());
                                recipient.setSurname(commMerchantAccountVO.getLastName());

                                additionalInfo.setRecipient(recipient);
                                authorizeUsingIntegratedMPI.setAdditionalInfo(additionalInfo);
                            }

                            String authorizeUsingIntegratedMPIRequest = PayVisionUtils.toJson(authorizeUsingIntegratedMPI);

                            transactionLogger.error("-----authorizeUsingIntegratedMPI Request-----:" + authorizeUsingIntegratedMPIRequest);

                            String authorizeUsingIntegratedMPIResponse = "";
                            if (isTest)
                            {
                                transactionLogger.error("----inside Test-----");
                                authorizeUsingIntegratedMPIResponse = PayVisionUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_AuthorizeUsingIntegratedMPI_URL"), authorizeUsingIntegratedMPIRequest);
                            }
                            else
                            {
                                transactionLogger.error("----inside Live-----");
                                authorizeUsingIntegratedMPIResponse = PayVisionUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_AuthorizeUsingIntegratedMPI_URL"), authorizeUsingIntegratedMPIRequest);
                            }
                            transactionLogger.error("-----authorizeUsingIntegratedMPI Response----" + authorizeUsingIntegratedMPIResponse);

                            if (functions.isValueNull(authorizeUsingIntegratedMPIResponse))
                            {
                                String bankCode = "";
                                String bankApprovalCode = "";
                                String bankMsg = "";
                                jsonObject = new JSONObject(authorizeUsingIntegratedMPIResponse);
                                slideContent = new JSONArray(jsonObject.getJSONObject("AuthorizeUsingIntegratedMPIResult").getString("Cdc"));

                                JSONObject jObj = null;
                                for (int j = 0; j < slideContent.length(); j++)
                                {
                                    jObj = slideContent.getJSONObject(0);
                                }
                                JSONArray itemContent = new JSONArray(jObj.getString("Items"));
                                JSONObject itemObj = null;
                                Map bankResMap = new HashMap<>();
                                for (int j = 0; j < itemContent.length(); j++)
                                {
                                    itemObj = itemContent.getJSONObject(j);
                                    bankResMap.put(itemObj.getString("Key"), itemObj.getString("Value"));

                                }
                                if (bankResMap != null)
                                {
                                    if (bankResMap.get("BankCode") != null && bankResMap.get("BankMessage") != null)
                                    {
                                        bankCode = (String) bankResMap.get("BankCode");
                                        bankApprovalCode = (String) bankResMap.get("BankApprovalCode");
                                        bankMsg = (String) bankResMap.get("BankMessage");
                                    }
                                    else if (bankResMap.get("ErrorCode") != null && bankResMap.get("ErrorMessage") != null)
                                    {
                                        bankCode = (String) bankResMap.get("ErrorCode");
                                        bankMsg = (String) bankResMap.get("ErrorMessage");
                                    }
                                }
                                if(!functions.isValueNull(bankMsg)){
                                    bankMsg=jsonObject.getJSONObject("AuthorizeUsingIntegratedMPIResult").getString("Message");
                                }

                                if ((!jsonObject.getJSONObject("AuthorizeUsingIntegratedMPIResult").has("Error")) && jsonObject.getJSONObject("AuthorizeUsingIntegratedMPIResult").getString("Result").equals("0"))
                                {
                                    status = "success";
                                    payVisionResponseVO.setStatus(status);
                                    // descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                                    payVisionResponseVO.setDescriptor(descriptor);
                                }
                                else
                                {
                                    status = "fail";
                                    payVisionResponseVO.setStatus(status);
                                }
                                payVisionResponseVO.setTransactionStatus(status);
                                payVisionResponseVO.setRemark(jsonObject.getJSONObject("AuthorizeUsingIntegratedMPIResult").getString("Message"));
                                payVisionResponseVO.setTransactionId(jsonObject.getJSONObject("AuthorizeUsingIntegratedMPIResult").getString("TransactionId"));
                                payVisionResponseVO.setAmount(amount);
                                payVisionResponseVO.setErrorCode(bankCode);
                                payVisionResponseVO.setAuthCode(bankApprovalCode);
                                payVisionResponseVO.setDescription(bankMsg);
                                payVisionResponseVO.setTransactionGuid(jsonObject.getJSONObject("AuthorizeUsingIntegratedMPIResult").getString("TransactionGuid"));
                                payVisionResponseVO.setTrackingMemberCode(jsonObject.getJSONObject("AuthorizeUsingIntegratedMPIResult").getString("TrackingMemberCode"));
                                payVisionResponseVO.setResponseTime(jsonObject.getJSONObject("AuthorizeUsingIntegratedMPIResult").getString("TransactionDateTime"));
                                payVisionResponseVO.setTransactionType(PZProcessType.AUTH.toString());
                            }
                        }else if("U".equals(MerchantPluginResult) && !"O".equalsIgnoreCase(is3dSupported) ){
                            Authorize authorize = new Authorize();

                            authorize.setMemberId(memberId);
                            authorize.setMemberGuid(memberGuId);
                            authorize.setCountryId(countryId);
                            authorize.setAmount(amount);
                            authorize.setCurrencyId(currencyId);
                            authorize.setTrackingMemberCode(trackingID + "_A");
                            authorize.setCardNumber(cardNumber);
                            authorize.setCardExpiryMonth(Integer.valueOf(cardExpiryMonth));
                            authorize.setCardExpiryYear(cardExpiryYear);
                            authorize.setCardCvv(cardCVV);
                            authorize.setCardholder(cardHolder);
                            authorize.setMerchantAccountType(merchantAccountType);
                            authorize.setDbaName(merchantOrganizationName);
                            authorize.setDbaCity(partnerSupportContactNumber);

                            Authorize authorizeLog = new Authorize();

                            authorizeLog.setMemberId(memberId);
                            authorizeLog.setMemberGuid(memberGuId);
                            authorizeLog.setCountryId(countryId);
                            authorizeLog.setAmount(amount);
                            authorizeLog.setCurrencyId(currencyId);
                            authorizeLog.setTrackingMemberCode(trackingID + "_A");
                            authorizeLog.setCardNumber(functions.maskingPan(cardNumber));
                            transactionLogger.error("-----exp month-----for "+trackingID+ "----" +functions.maskingNumber(cardDetailsVO.getExpMonth()) + "exp year "+ functions.maskingNumber(cardDetailsVO.getExpYear()));
                            //authorizeLog.setCardExpiryMonth(Integer.valueOf(functions.maskingNumber(cardExpiryMonth)));
                            //authorizeLog.setCardExpiryYear(Integer.parseInt(functions.maskingNumber(cardDetailsVO.getExpYear())));
                            authorizeLog.setCardCvv(functions.maskingNumber(cardCVV));
                            authorizeLog.setCardholder(cardHolder);
                            authorizeLog.setMerchantAccountType(merchantAccountType);
                            authorizeLog.setDbaName(merchantOrganizationName);
                            authorizeLog.setDbaCity(partnerSupportContactNumber);

                            if(isForexMid.equalsIgnoreCase("Y"))
                            {
                                Recipient recipient = new Recipient();
                                AdditionalInfo additionalInfo = new AdditionalInfo();

                                String firstSix = Functions.getFirstSix(cardNumber);
                                String lastFour = Functions.getLastFour(cardNumber);
                                String dateOfBirth = "1990-01-30";

                                recipient.setDateOfBirth(dateOfBirth);
                                recipient.setAccountNumber(firstSix+lastFour);
                                if(functions.isValueNull(commMerchantAccountVO.getZipCode()))
                                    recipient.setPostalCode(commMerchantAccountVO.getZipCode().trim());
                                recipient.setSurname(commMerchantAccountVO.getLastName());

                                additionalInfo.setRecipient(recipient);
                                authorize.setAdditionalInfo(additionalInfo);
                                authorizeLog.setAdditionalInfo(additionalInfo);
                            }

                            String authorizeRequest = PayVisionUtils.toJson(authorize);
                            String authorizeRequestLog = PayVisionUtils.toJson(authorizeLog);

                            transactionLogger.error("-----authorize Request-----for "+trackingID+ "----" + authorizeRequestLog);

                            String authorizeResponse = "";
                            if (isTest)
                            {
                                transactionLogger.error("----inside Test-----");
                                authorizeResponse = PayVisionUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_AUTH_URL"), authorizeRequest);

                            }
                            else
                            {
                                transactionLogger.error("----inside Live-----");
                                authorizeResponse = PayVisionUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_AUTH_URL"), authorizeRequest);
                            }
                            transactionLogger.error("-----authorize Response----for "+trackingID+ "----" + authorizeResponse);

                            if (functions.isValueNull(authorizeResponse))
                            {
                                String bankCode="";
                                String bankApprovalCode="";
                                String bankMsg="";
                                 jsonObject = new JSONObject(authorizeResponse);
                                 slideContent = new JSONArray(jsonObject.getJSONObject("AuthorizeResult").getString("Cdc"));

                                JSONObject jObj = null;
                                for (int j = 0; j < slideContent.length(); j++)
                                {
                                    jObj = slideContent.getJSONObject(0);
                                }


                                JSONArray itemContent = new JSONArray(jObj.getString("Items"));
                                JSONObject itemObj = null;
                                Map bankResMap = new HashMap<>();
                                for (int j = 0; j< itemContent.length(); j++)
                                {
                                    itemObj = itemContent.getJSONObject(j);
                                    bankResMap.put(itemObj.getString("Key"), itemObj.getString("Value"));

                                }
                                if(bankResMap!=null){
                                    if(bankResMap.get("BankCode")!=null && bankResMap.get("BankMessage")!=null){
                                        bankCode=(String)bankResMap.get("BankCode");
                                        bankApprovalCode = (String) bankResMap.get("BankApprovalCode");
                                        bankMsg=(String)bankResMap.get("BankMessage");
                                    }else if(bankResMap.get("ErrorCode")!=null && bankResMap.get("ErrorMessage")!=null){
                                        bankCode=(String)bankResMap.get("ErrorCode");
                                        bankMsg=(String) bankResMap.get("ErrorMessage");
                                    }
                                }
                                if(!functions.isValueNull(bankMsg)){
                                    bankMsg=jsonObject.getJSONObject("AuthorizeResult").getString("Message");
                                }

                                if ((!jsonObject.getJSONObject("AuthorizeResult").has("Error")) && jsonObject.getJSONObject("AuthorizeResult").getString("Result").equals("0"))
                                {
                                    status = "success";
                                    payVisionResponseVO.setStatus(status);
                                    //String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                                    payVisionResponseVO.setDescriptor(descriptor);
                                }
                                else
                                {
                                    status = "fail";
                                    payVisionResponseVO.setStatus(status);
                                }

                                payVisionResponseVO.setTransactionStatus(status);
                                payVisionResponseVO.setRemark(jsonObject.getJSONObject("AuthorizeResult").getString("Message"));
                                payVisionResponseVO.setTransactionId(jsonObject.getJSONObject("AuthorizeResult").getString("TransactionId"));
                                payVisionResponseVO.setAmount(amount);
                                payVisionResponseVO.setErrorCode(bankCode);
                                payVisionResponseVO.setAuthCode(bankApprovalCode);
                                payVisionResponseVO.setDescription(bankMsg);
                                payVisionResponseVO.setTransactionGuid(jsonObject.getJSONObject("AuthorizeResult").getString("TransactionGuid"));
                                payVisionResponseVO.setTrackingMemberCode(jsonObject.getJSONObject("AuthorizeResult").getString("TrackingMemberCode"));
                                payVisionResponseVO.setResponseTime(jsonObject.getJSONObject("AuthorizeResult").getString("TransactionDateTime"));
                                payVisionResponseVO.setTransactionType(PZProcessType.AUTH.toString());

                            }
                            payVisionResponseVO.setCurrency(currency);
                            payVisionResponseVO.setTmpl_Amount(tmpl_amount);
                            payVisionResponseVO.setTmpl_Currency(tmpl_currency);
                        }else {
                            payVisionResponseVO.setStatus("fail");
                            payVisionResponseVO.setTransactionStatus(status);
                            payVisionResponseVO.setDescription("Transaction not allowed");
                            payVisionResponseVO.setRemark("Transaction not allowed");
                            payVisionResponseVO.setTransactionType(PZProcessType.AUTH.toString());
                        }
                    }
                    else
                    {
                        String bankCode = "";
                        String bankMsg = "";
                        if (functions.isValueNull((String) hashtable.get("ErrorCode")) || functions.isValueNull((String) hashtable.get("ErrorMessage")))
                        {
                            bankCode = (String) hashtable.get("ErrorCode");
                            bankMsg = (String) hashtable.get("ErrorMessage");
                        }
                        if(!functions.isValueNull(bankMsg)){
                            bankMsg=jsonObject.getJSONObject("CheckEnrollmentResult").getString("Message");
                        }
                        payVisionResponseVO.setStatus("fail");
                        payVisionResponseVO.setTransactionStatus(status);
                        payVisionResponseVO.setRemark(jsonObject.getJSONObject("CheckEnrollmentResult").getString("Message"));
                        payVisionResponseVO.setAmount(amount);
                        payVisionResponseVO.setErrorCode(bankCode);
                        payVisionResponseVO.setDescription(bankMsg);
                        payVisionResponseVO.setTrackingMemberCode(jsonObject.getJSONObject("CheckEnrollmentResult").getString("TrackingMemberCode"));
                        payVisionResponseVO.setResponseTime(jsonObject.getJSONObject("CheckEnrollmentResult").getString("DateTime"));
                        payVisionResponseVO.setTransactionType(PZProcessType.AUTH.toString());
                    }
                }
            }
            else
            {
                Authorize authorize = new Authorize();

                authorize.setMemberId(memberId);
                authorize.setMemberGuid(memberGuId);
                authorize.setCountryId(countryId);
                authorize.setAmount(amount);
                authorize.setCurrencyId(currencyId);
                authorize.setTrackingMemberCode(trackingID + "_A");
                authorize.setCardNumber(cardNumber);
                authorize.setCardExpiryMonth(Integer.valueOf(cardExpiryMonth));
                authorize.setCardExpiryYear(cardExpiryYear);
                authorize.setCardCvv(cardCVV);
                authorize.setCardholder(cardHolder);
                authorize.setMerchantAccountType(merchantAccountType);
                authorize.setDbaName(merchantOrganizationName);
                authorize.setDbaCity(partnerSupportContactNumber);

                Authorize authorizeLog = new Authorize();

                authorizeLog.setMemberId(memberId);
                authorizeLog.setMemberGuid(memberGuId);
                authorizeLog.setCountryId(countryId);
                authorizeLog.setAmount(amount);
                authorizeLog.setCurrencyId(currencyId);
                authorizeLog.setTrackingMemberCode(trackingID + "_A");
                authorizeLog.setCardNumber(functions.maskingPan(cardNumber));
                transactionLogger.error("-----exp month-----for "+trackingID+ "----" +functions.maskingNumber(cardDetailsVO.getExpMonth()) + "exp year "+ functions.maskingNumber(cardDetailsVO.getExpYear()));
                //authorizeLog.setCardExpiryMonth(Integer.valueOf(functions.maskingNumber(cardExpiryMonth)));
                //authorizeLog.setCardExpiryYear(Integer.parseInt(functions.maskingNumber(cardDetailsVO.getExpYear())));
                authorizeLog.setCardCvv(functions.maskingNumber(cardCVV));
                authorizeLog.setCardholder(cardHolder);
                authorizeLog.setMerchantAccountType(merchantAccountType);
                authorizeLog.setDbaName(merchantOrganizationName);
                authorizeLog.setDbaCity(partnerSupportContactNumber);


                if(isForexMid.equalsIgnoreCase("Y"))
                {
                    Recipient recipient = new Recipient();
                    AdditionalInfo additionalInfo = new AdditionalInfo();

                    String firstSix = Functions.getFirstSix(cardNumber);
                    String lastFour = Functions.getLastFour(cardNumber);
                    String dateOfBirth = "1990-01-30";

                    recipient.setDateOfBirth(dateOfBirth);
                    recipient.setAccountNumber(firstSix+lastFour);
                    if(functions.isValueNull(commMerchantAccountVO.getZipCode()))
                        recipient.setPostalCode(commMerchantAccountVO.getZipCode().trim());
                    recipient.setSurname(commMerchantAccountVO.getLastName());

                    additionalInfo.setRecipient(recipient);
                    authorize.setAdditionalInfo(additionalInfo);
                    authorizeLog.setAdditionalInfo(additionalInfo);
                }

                String authorizeRequest = PayVisionUtils.toJson(authorize);
                String authorizeRequestLog = PayVisionUtils.toJson(authorizeLog);


                transactionLogger.error("-----authorize Request-----for "+trackingID+ "----" + authorizeRequestLog);

                String authorizeResponse = "";
                if (isTest)
                {
                    transactionLogger.error("----inside Test-----");
                    authorizeResponse = PayVisionUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_AUTH_URL"), authorizeRequest);

                }
                else
                {
                    transactionLogger.error("----inside Live-----");
                    authorizeResponse = PayVisionUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_AUTH_URL"), authorizeRequest);
                }
                transactionLogger.error("-----authorize Response----" + authorizeResponse);

                if (functions.isValueNull(authorizeResponse))
                {
                    String bankCode="";
                    String bankApprovalCode="";
                    String bankMsg="";
                    JSONObject jsonObject = new JSONObject(authorizeResponse);
                    JSONArray slideContent = new JSONArray(jsonObject.getJSONObject("AuthorizeResult").getString("Cdc"));

                    JSONObject jObj = null;
                    for (int j = 0; j < slideContent.length(); j++)
                    {
                        jObj = slideContent.getJSONObject(0);
                    }


                    JSONArray itemContent = new JSONArray(jObj.getString("Items"));
                    JSONObject itemObj = null;
                    Map bankResMap = new HashMap<>();
                    for (int j = 0; j< itemContent.length(); j++)
                    {
                        itemObj = itemContent.getJSONObject(j);
                        bankResMap.put(itemObj.getString("Key"), itemObj.getString("Value"));

                    }
                    if(bankResMap!=null){
                        if(bankResMap.get("BankCode")!=null && bankResMap.get("BankMessage")!=null){
                            bankCode=(String)bankResMap.get("BankCode");
                            bankApprovalCode = (String) bankResMap.get("BankApprovalCode");
                            bankMsg=(String)bankResMap.get("BankMessage");
                        }else if(bankResMap.get("ErrorCode")!=null && bankResMap.get("ErrorMessage")!=null){
                            bankCode=(String)bankResMap.get("ErrorCode");
                            bankMsg=(String) bankResMap.get("ErrorMessage");
                        }
                    }
                    if(!functions.isValueNull(bankMsg)){
                        bankMsg=jsonObject.getJSONObject("AuthorizeResult").getString("Message");
                    }

                    if ((!jsonObject.getJSONObject("AuthorizeResult").has("Error")) && jsonObject.getJSONObject("AuthorizeResult").getString("Result").equals("0"))
                    {
                        status = "success";
                        payVisionResponseVO.setStatus(status);
                        //String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        payVisionResponseVO.setDescriptor(descriptor);
                    }
                    else
                    {
                        status = "fail";
                        payVisionResponseVO.setStatus(status);
                    }

                    payVisionResponseVO.setTransactionStatus(status);
                    payVisionResponseVO.setRemark(jsonObject.getJSONObject("AuthorizeResult").getString("Message"));
                    payVisionResponseVO.setTransactionId(jsonObject.getJSONObject("AuthorizeResult").getString("TransactionId"));
                    payVisionResponseVO.setAmount(amount);
                    payVisionResponseVO.setErrorCode(bankCode);
                    payVisionResponseVO.setAuthCode(bankApprovalCode);
                    payVisionResponseVO.setDescription(bankMsg);
                    payVisionResponseVO.setTransactionGuid(jsonObject.getJSONObject("AuthorizeResult").getString("TransactionGuid"));
                    payVisionResponseVO.setTrackingMemberCode(jsonObject.getJSONObject("AuthorizeResult").getString("TrackingMemberCode"));
                    payVisionResponseVO.setResponseTime(jsonObject.getJSONObject("AuthorizeResult").getString("TransactionDateTime"));
                    payVisionResponseVO.setTransactionType(PZProcessType.AUTH.toString());

                }
            }
            payVisionResponseVO.setCurrency(currency);
            payVisionResponseVO.setTmpl_Amount(tmpl_amount);
            payVisionResponseVO.setTmpl_Currency(tmpl_currency);
        }
        catch (Exception e){
            transactionLogger.error("Exception:::::"+e);
            PZExceptionHandler.raiseTechnicalViolationException(PayVisionPaymentGateway.class.getName(), "processAuthentication()", null, "common", "Exception while placing  Auth transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return payVisionResponseVO;
    }

    @Override
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("-----inside processVoid-----");
        PayVisionResponseVO payVisionResponseVO = new PayVisionResponseVO();
        Functions functions=new Functions();
        try
        {
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            boolean isTest = gatewayAccount.isTest();

            PayVisionRequestVO payVisionRequestVO = (PayVisionRequestVO) requestVO;
            CommMerchantVO commMerchantAccountVO = payVisionRequestVO.getCommMerchantVO();
            CommTransactionDetailsVO transDetailsVO = payVisionRequestVO.getTransDetailsVO();
            int memberId = Integer.parseInt(commMerchantAccountVO.getMerchantId());
            String memberGuId = commMerchantAccountVO.getMerchantUsername();
            String trackingMemberCode = payVisionRequestVO.getTrackingMemberCode();
            long transactionId = Long.parseLong(transDetailsVO.getPreviousTransactionId());
            String transactionGuId = payVisionRequestVO.getTransactionGuid();

            String status = "fail";
            String response = "";

            Void objVoid = new Void();
            objVoid.setMemberId(memberId);
            objVoid.setMemberGuid(memberGuId);
            objVoid.setTransactionId(transactionId);
            objVoid.setTransactionGuid(transactionGuId);
            objVoid.setTrackingMemberCode(trackingID + "_V");

            String voidRequest = PayVisionUtils.toJson(objVoid);

            transactionLogger.error("-----void Request-----for "+trackingID+ "----" + voidRequest);

            String voidResponse = "";
            if (isTest)
            {
                transactionLogger.error("----inside Test-----");
                voidResponse = PayVisionUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_VOID_URL"), voidRequest);

            }
            else
            {
                transactionLogger.error("----inside Live-----");
                voidResponse = PayVisionUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_VOID_URL"), voidRequest);
            }
            transactionLogger.error("-----void Response----for "+trackingID+ "----" + voidResponse);

            if (functions.isValueNull(voidResponse))
            {
                String bankCode="";
                String bankMsg="";
                JSONObject jsonObject = new JSONObject(voidResponse);
                JSONArray slideContent = new JSONArray(jsonObject.getJSONObject("VoidResult").getString("Cdc"));

                JSONObject jObj = null;
                for (int j = 0; j < slideContent.length(); j++)
                {
                    jObj = slideContent.getJSONObject(0);
                }

                JSONArray itemContent = new JSONArray(jObj.getString("Items"));
                JSONObject itemObj = null;
                Map bankResMap = new HashMap<>();
                for (int j = 0; j< itemContent.length(); j++)
                {
                    itemObj = itemContent.getJSONObject(j);
                    bankResMap.put(itemObj.getString("Key"), itemObj.getString("Value"));

                }
                if(bankResMap!=null){
                    if(bankResMap.get("BankCode")!=null && bankResMap.get("BankMessage")!=null){
                        bankCode=(String)bankResMap.get("BankCode");
                        bankMsg=(String)bankResMap.get("BankMessage");
                    }else if(bankResMap.get("ErrorCode")!=null && bankResMap.get("ErrorMessage")!=null){
                        bankCode=(String)bankResMap.get("ErrorCode");
                        bankMsg=(String) bankResMap.get("ErrorMessage");
                    }
                }
                if(!functions.isValueNull(bankMsg)){
                    bankMsg=jsonObject.getJSONObject("VoidResult").getString("Message");
                }
                if ((!jsonObject.getJSONObject("VoidResult").has("Error")) && jsonObject.getJSONObject("VoidResult").getString("Result").equals("0"))
                {
                    status = "success";
                    payVisionResponseVO.setStatus(status);
                    String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    payVisionResponseVO.setDescriptor(descriptor);
                }
                else
                {
                    payVisionResponseVO.setDescription("Transaction Failed");
                    payVisionResponseVO.setStatus("fail");
                }
                payVisionResponseVO.setTransactionId(jsonObject.getJSONObject("VoidResult").getString("TransactionId"));
                payVisionResponseVO.setRemark(jsonObject.getJSONObject("VoidResult").getString("Message"));
                payVisionResponseVO.setErrorCode(bankCode);
                payVisionResponseVO.setDescription(bankMsg);
                payVisionResponseVO.setTransactionGuid(jsonObject.getJSONObject("VoidResult").getString("TransactionGuid"));
                payVisionResponseVO.setTrackingMemberCode(jsonObject.getJSONObject("VoidResult").getString("TrackingMemberCode"));
                payVisionResponseVO.setResponseTime(jsonObject.getJSONObject("VoidResult").getString("TransactionDateTime"));
                payVisionResponseVO.setTransactionType(PZProcessType.CANCEL.toString());
            }
        }
        catch (Exception e){
            log.error("Exception:::::for "+trackingID+ "----" ,e);
            PZExceptionHandler.raiseTechnicalViolationException(PayVisionPaymentGateway.class.getName(), "processVoid()", null, "common", "Exception while placing  cancel transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return payVisionResponseVO;
    }

    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("-----inside processRefund-----");
        PayVisionResponseVO payVisionResponseVO = new PayVisionResponseVO();
        Functions functions= new Functions();

        try
        {
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            boolean isTest = gatewayAccount.isTest();

            PayVisionRequestVO payVisionRequestVO = (PayVisionRequestVO) requestVO;
            CommMerchantVO commMerchantAccountVO = payVisionRequestVO.getCommMerchantVO();
            CommTransactionDetailsVO transDetailsVO = payVisionRequestVO.getTransDetailsVO();
            CommAddressDetailsVO addressDetailsVO=payVisionRequestVO.getAddressDetailsVO();

            String amount = transDetailsVO.getAmount();
            int memberId = Integer.parseInt(commMerchantAccountVO.getMerchantId());
            String memberGuId = commMerchantAccountVO.getMerchantUsername();

            int currencyId = Integer.parseInt(CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency()));
            long transactionId = Long.parseLong(transDetailsVO.getPreviousTransactionId());
            String currency="";
            String tmpl_amount="";
            String tmpl_currency="";
            if (functions.isValueNull(transDetailsVO.getCurrency()))
            {
                currency=transDetailsVO.getCurrency();
            }
            if (functions.isValueNull(addressDetailsVO.getTmpl_amount()))
            {
                tmpl_amount=addressDetailsVO.getTmpl_amount();
            }
            if (functions.isValueNull(addressDetailsVO.getTmpl_currency()))
            {
                tmpl_currency=addressDetailsVO.getTmpl_currency();
            }

            String transactionGuId = payVisionRequestVO.getTransactionGuid();
            String trackingMemberCode = payVisionRequestVO.getTrackingMemberCode();
            String count=payVisionRequestVO.getCount();

            String status = "";
            String response = "";

            Refund refund = new Refund();
            refund.setMemberId(memberId);
            refund.setMemberGuid(memberGuId);
            refund.setTransactionId(transactionId);
            refund.setTransactionGuid(transactionGuId);
            refund.setAmount(amount);
            refund.setCurrencyId(currencyId);
            refund.setTrackingMemberCode(trackingID +"_R"+count);

            String refundRequest = PayVisionUtils.toJson(refund);

            transactionLogger.error("-----refund Request-----:" + transDetailsVO.getPreviousTransactionId());
            transactionLogger.error("-----refund Request-----for "+trackingID+ "----"  + refundRequest);

            String refundResponse = "";
            if (isTest)
            {
                transactionLogger.error("----inside Test-----");
                refundResponse = PayVisionUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_REFUND_URL"), refundRequest);

            }
            else
            {
                transactionLogger.error("----inside Live-----");
                refundResponse = PayVisionUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_REFUND_URL"), refundRequest);
            }
            transactionLogger.error("-----refund Response----for "+trackingID+ "----" + refundResponse);

            if (functions.isValueNull(refundResponse))
            {
                String bankCode="";
                String bankMsg="";
                JSONObject jsonObject = new JSONObject(refundResponse);
                JSONArray slideContent = new JSONArray(jsonObject.getJSONObject("RefundResult").getString("Cdc"));

                JSONObject jObj = null;
                for (int j = 0; j < slideContent.length(); j++)
                {
                    jObj = slideContent.getJSONObject(0);
                }

                JSONArray itemContent = new JSONArray(jObj.getString("Items"));
                JSONObject itemObj = null;
                Map bankResMap = new HashMap<>();
                for (int j = 0; j< itemContent.length(); j++)
                {
                    itemObj = itemContent.getJSONObject(j);
                    bankResMap.put(itemObj.getString("Key"), itemObj.getString("Value"));

                }
                if(bankResMap!=null){
                    if(bankResMap.get("BankCode")!=null && bankResMap.get("BankMessage")!=null){
                        bankCode=(String)bankResMap.get("BankCode");
                        bankMsg=(String)bankResMap.get("BankMessage");
                    }else if(bankResMap.get("ErrorCode")!=null && bankResMap.get("ErrorMessage")!=null){
                        bankCode=(String)bankResMap.get("ErrorCode");
                        bankMsg=(String) bankResMap.get("ErrorMessage");
                    }
                }

                if(!functions.isValueNull(bankMsg)){
                    bankMsg= jsonObject.getJSONObject("RefundResult").getString("Message");
                }

                if (jsonObject.getJSONObject("RefundResult").getString("Result").equals("0"))
                {
                    status = "success";
                    payVisionResponseVO.setStatus(status);
                    String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    payVisionResponseVO.setDescriptor(descriptor);
                }
                else
                {
                    status = "fail";
                    payVisionResponseVO.setDescription("Transaction Failed");
                    payVisionResponseVO.setStatus(status);
                }

                payVisionResponseVO.setTransactionStatus(status);
                payVisionResponseVO.setRemark(jsonObject.getJSONObject("RefundResult").getString("Message"));
                payVisionResponseVO.setTransactionId(jsonObject.getJSONObject("RefundResult").getString("TransactionId"));
                payVisionResponseVO.setAmount(amount);
                payVisionResponseVO.setErrorCode(bankCode);
                payVisionResponseVO.setDescription(bankMsg);
                payVisionResponseVO.setTransactionGuid(jsonObject.getJSONObject("RefundResult").getString("TransactionGuid"));
                payVisionResponseVO.setTrackingMemberCode(jsonObject.getJSONObject("RefundResult").getString("TrackingMemberCode"));
                payVisionResponseVO.setResponseTime(jsonObject.getJSONObject("RefundResult").getString("TransactionDateTime"));
                payVisionResponseVO.setTransactionType(PZProcessType.REFUND.toString());
                payVisionResponseVO.setCurrency(currency);
                payVisionResponseVO.setTmpl_Amount(tmpl_amount);
                payVisionResponseVO.setTmpl_Currency(tmpl_currency);
            }
        }
        catch(Exception e){
            log.error("Exception:::::for "+trackingID+ "----",e);
            PZExceptionHandler.raiseTechnicalViolationException(PayVisionPaymentGateway.class.getName(), "processRefund()", null, "common", "Exception while placing  Refund transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return payVisionResponseVO;
    }

    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("-----inside processCapture-----");
        PayVisionResponseVO payVisionResponseVO = new PayVisionResponseVO();
        Functions functions= new Functions();
        try
        {
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            boolean isTest = gatewayAccount.isTest();


            PayVisionRequestVO payVisionRequestVO = (PayVisionRequestVO) requestVO;
            CommMerchantVO commMerchantAccountVO = payVisionRequestVO.getCommMerchantVO();
            CommTransactionDetailsVO transDetailsVO = payVisionRequestVO.getTransDetailsVO();

            String amount = transDetailsVO.getAmount();
            int memberId = Integer.parseInt(commMerchantAccountVO.getMerchantId());
            String memberGuId = commMerchantAccountVO.getMerchantUsername();
            int currencyId = Integer.parseInt(CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency()));
            String trackingMemberCode = payVisionRequestVO.getTrackingMemberCode();
            long transactionId = Long.parseLong(transDetailsVO.getPreviousTransactionId());
            String transactionGuId = payVisionRequestVO.getTransactionGuid();

            String status = "fail";
            String response = "";

            Capture capture = new Capture();
            capture.setMemberId(memberId);
            capture.setMemberGuid(memberGuId);
            capture.setTransactionId(transactionId);
            capture.setTransactionGuid(transactionGuId);
            capture.setAmount(amount);
            capture.setCurrencyId(currencyId);
            capture.setTrackingMemberCode(trackingID + "_C");

            String captureRequest = PayVisionUtils.toJson(capture);

            transactionLogger.error("-----capture Request-----for "+trackingID+ "----" + captureRequest);

            String captureResponse = "";
            if (isTest)
            {
                transactionLogger.error("----inside Test-----");
                captureResponse = PayVisionUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_CAPTURE_URL"), captureRequest);

            }
            else
            {
                transactionLogger.error("----inside Live-----");
                captureResponse = PayVisionUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_CAPTURE_URL"), captureRequest);
            }
            transactionLogger.error("-----capture Response----for "+trackingID+ "----" + captureResponse);

            if (functions.isValueNull(captureResponse))
            {
                String bankCode="";
                String bankMsg="";
                JSONObject jsonObject = new JSONObject(captureResponse);
                JSONArray slideContent = new JSONArray(jsonObject.getJSONObject("CaptureResult").getString("Cdc"));

                JSONObject jObj = null;
                for (int j = 0; j < slideContent.length(); j++)
                {
                    jObj = slideContent.getJSONObject(0);
                }

                JSONArray itemContent = new JSONArray(jObj.getString("Items"));
                JSONObject itemObj = null;
                Map bankResMap = new HashMap<>();
                for (int j = 0; j < itemContent.length(); j++)
                {
                    itemObj = itemContent.getJSONObject(j);
                    bankResMap.put(itemObj.getString("Key"), itemObj.getString("Value"));

                }
                if (bankResMap != null)
                {
                    if (bankResMap.get("BankCode") != null && bankResMap.get("BankMessage") != null)
                    {
                        bankCode = (String) bankResMap.get("BankCode");
                        bankMsg = (String) bankResMap.get("BankMessage");
                    }
                    else if (bankResMap.get("ErrorCode") != null && bankResMap.get("ErrorMessage") != null)
                    {
                        bankCode = (String) bankResMap.get("ErrorCode");
                        bankMsg = (String) bankResMap.get("ErrorMessage");
                    }
                }
                if(!functions.isValueNull(bankMsg)){
                    bankMsg= jsonObject.getJSONObject("CaptureResult").getString("Message");
                }

                if ((!jsonObject.getJSONObject("CaptureResult").has("Error")) && jsonObject.getJSONObject("CaptureResult").getString("Result").equals("0"))
                {
                    status = "success";
                    payVisionResponseVO.setStatus(status);
                    String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    payVisionResponseVO.setDescriptor(descriptor);
                }
                else
                {
                    payVisionResponseVO.setStatus("fail");
                }
                payVisionResponseVO.setTransactionId(jsonObject.getJSONObject("CaptureResult").getString("TransactionId"));
                payVisionResponseVO.setRemark(jsonObject.getJSONObject("CaptureResult").getString("Message"));
                payVisionResponseVO.setErrorCode(bankCode);
                payVisionResponseVO.setDescription(bankMsg);
                payVisionResponseVO.setTransactionGuid(jsonObject.getJSONObject("CaptureResult").getString("TransactionGuid"));
                payVisionResponseVO.setTrackingMemberCode(jsonObject.getJSONObject("CaptureResult").getString("TrackingMemberCode"));
                payVisionResponseVO.setResponseTime(jsonObject.getJSONObject("CaptureResult").getString("TransactionDateTime"));
                payVisionResponseVO.setTransactionType(PZProcessType.CAPTURE.toString());
            }
        }
        catch (Exception e){
            log.error("Exception:::::for "+trackingID+ "----",e);
            PZExceptionHandler.raiseTechnicalViolationException(PayVisionPaymentGateway.class.getName(), "processCapture()", null, "common", "Exception while placing  capture transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return payVisionResponseVO;
    }

    public GenericResponseVO processPayout(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processPayout-----");
        PayVisionResponseVO payVisionResponseVO = new PayVisionResponseVO();
        Functions functions= new Functions();
        int countryId=0;
        try
        {
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            boolean isTest = gatewayAccount.isTest();

            PayVisionRequestVO payVisionRequestVO = (PayVisionRequestVO) requestVO;
            CommMerchantVO commMerchantAccountVO = payVisionRequestVO.getCommMerchantVO();
            CommTransactionDetailsVO transDetailsVO = payVisionRequestVO.getTransDetailsVO();
            CommAddressDetailsVO addressDetailsVO = payVisionRequestVO.getAddressDetailsVO();
            CommCardDetailsVO cardDetailsVO = payVisionRequestVO.getCardDetailsVO();

            String amount = transDetailsVO.getAmount();
            int memberId = Integer.parseInt(commMerchantAccountVO.getMerchantId());
            String memberGuId = commMerchantAccountVO.getMerchantUsername();
            if (functions.isValueNull(CountryCodeISO3166.getNumericCountryCode(addressDetailsVO.getCountry())))
            {
                countryId = Integer.parseInt(CountryCodeISO3166.getNumericCountryCode(addressDetailsVO.getCountry()));
            }

            int currencyId = Integer.parseInt(CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency()));
            String trackingMemberCode = payVisionRequestVO.getTrackingMemberCode();

            String cardNumber = cardDetailsVO.getCardNum();
            String cardExpiryMonth = cardDetailsVO.getExpMonth();
            Short cardExpiryYear = Short.parseShort(cardDetailsVO.getExpYear());
            String cardCVV = cardDetailsVO.getcVV();

            String firstSix = Functions.getFirstSix(cardNumber);
            String lastFour = Functions.getLastFour(cardNumber);

            String status = "";
            String response = "";

            int merchantAccountType = ECOMMERECE;

            Payment payment = new Payment();
            payment.setMemberId(memberId);
            payment.setMemberGuid(memberGuId);
            payment.setCountryId(countryId);
            payment.setAmount(amount);
            payment.setCurrencyId(currencyId);
            payment.setTrackingMemberCode(trackingID);
            payment.setCardNumber(cardNumber);
            payment.setCardExpiryMonth(Integer.valueOf(cardExpiryMonth));
            payment.setCardExpiryYear(cardExpiryYear);
            payment.setCardCvv(cardCVV);
            payment.setMerchantAccountType(merchantAccountType);

            Payment paymentLogs = new Payment();
            paymentLogs.setMemberId(memberId);
            paymentLogs.setMemberGuid(memberGuId);
            paymentLogs.setCountryId(countryId);
            paymentLogs.setAmount(amount);
            paymentLogs.setCurrencyId(currencyId);
            paymentLogs.setTrackingMemberCode(trackingID);
            paymentLogs.setCardNumber(functions.maskingPan(cardNumber));
            transactionLogger.error("-----exp month-----for "+trackingID+ "----" +functions.maskingNumber(cardDetailsVO.getExpMonth()) + "exp year "+ functions.maskingNumber(cardDetailsVO.getExpYear()));
            //paymentLogs.setCardExpiryMonth(Integer.valueOf(cardExpiryMonth));
            //paymentLogs.setCardExpiryYear(cardExpiryYear);
            paymentLogs.setCardCvv(functions.maskingNumber(cardCVV));
            paymentLogs.setMerchantAccountType(merchantAccountType);

            String payoutRequest = PayVisionUtils.toJson(payment);
            String payoutRequestLog = PayVisionUtils.toJson(paymentLogs);


            transactionLogger.error("-----payout Request-----for "+trackingID+ "----" + payoutRequestLog);

            String payoutResponse = "";
            if (isTest)
            {
                transactionLogger.error("----inside Test-----");
                payoutResponse = PayVisionUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_PAYOUT_URL"), payoutRequest);

            }
            else
            {
                transactionLogger.error("----inside Live-----");
                payoutResponse = PayVisionUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_PAYOUT_URL"), payoutRequest);
            }
            transactionLogger.error("-----payout Response----for "+trackingID+ "----" + payoutResponse);

            if (functions.isValueNull(payoutResponse))
            {
                String bankCode="";
                String bankMsg="";
                JSONObject jsonObject = new JSONObject(payoutResponse);
                JSONArray slideContent = new JSONArray(jsonObject.getJSONObject("CardFundTransferResult").getString("Cdc"));

                JSONObject jObj = null;
                for (int j = 0; j < slideContent.length(); j++)
                {
                    jObj = slideContent.getJSONObject(0);
                }

                JSONArray itemContent = new JSONArray(jObj.getString("Items"));
                JSONObject itemObj = null;
                Map bankResMap = new HashMap<>();
                for (int j = 0; j< itemContent.length(); j++)
                {
                    itemObj = itemContent.getJSONObject(j);
                    bankResMap.put(itemObj.getString("Key"), itemObj.getString("Value"));

                }
                if(bankResMap!=null){
                    if(bankResMap.get("BankCode")!=null && bankResMap.get("BankMessage")!=null){
                        bankCode=(String)bankResMap.get("BankCode");
                        bankMsg=(String)bankResMap.get("BankMessage");
                    }else if(bankResMap.get("ErrorCode")!=null && bankResMap.get("ErrorMessage")!=null){
                        bankCode=(String)bankResMap.get("ErrorCode");
                        bankMsg=(String) bankResMap.get("ErrorMessage");
                    }
                }

                if(!functions.isValueNull(bankMsg)){
                    bankMsg=jsonObject.getJSONObject("CardFundTransferResult").getString("Message");
                }

                status = "fail";
                if ((!jsonObject.getJSONObject("CardFundTransferResult").has("Error")) && jsonObject.getJSONObject("CardFundTransferResult").getString("Result").equals("0"))
                {
                    status = "success";
                    payVisionResponseVO.setStatus(status);
                    String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    payVisionResponseVO.setDescriptor(descriptor);
                }
                else
                {
                    status = "fail";
                    payVisionResponseVO.setDescription("Transaction Failed");
                    payVisionResponseVO.setStatus(status);
                }
                payVisionResponseVO.setTransactionStatus(status);
                payVisionResponseVO.setRemark(jsonObject.getJSONObject("CardFundTransferResult").getString("Message"));
                payVisionResponseVO.setTransactionId(jsonObject.getJSONObject("CardFundTransferResult").getString("TransactionId"));
                payVisionResponseVO.setAmount(amount);
                payVisionResponseVO.setErrorCode(bankCode);
                payVisionResponseVO.setDescription(bankMsg);
                payVisionResponseVO.setTransactionGuid(jsonObject.getJSONObject("CardFundTransferResult").getString("TransactionGuid"));
                payVisionResponseVO.setTrackingMemberCode(jsonObject.getJSONObject("CardFundTransferResult").getString("TrackingMemberCode"));
                payVisionResponseVO.setResponseTime(jsonObject.getJSONObject("CardFundTransferResult").getString("TransactionDateTime"));
                payVisionResponseVO.setTransactionType(PZProcessType.PAYOUT.toString());
            }
        }
        catch (JSONException e){
            log.error("Exception:::::",e);
            PZExceptionHandler.raiseTechnicalViolationException(PayVisionPaymentGateway.class.getName(), "processPayout()", null, "common", "Exception while placing Payout transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return payVisionResponseVO;
    }

    @Override
    public GenericResponseVO processQuery(String trackingID,GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.error("-----inside processQuery-----");
        PayVisionResponseVO payVisionResponseVO = new PayVisionResponseVO();
        Functions functions= new Functions();

        try
        {
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            boolean isTest = gatewayAccount.isTest();


            CommRequestVO commRequestVO = (CommRequestVO) requestVO;
            CommMerchantVO commMerchantAccountVO = commRequestVO.getCommMerchantVO();
            CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();

            String transactionDate = "/Date(" + getMilliSecond(commTransactionDetailsVO.getResponsetime()) + ")/";
            int memberId = Integer.parseInt(gatewayAccount.getMerchantId());
            String memberGuId = gatewayAccount.getFRAUD_FTP_USERNAME();
            String trackingMemberCode = trackingID;
            String status = "fail";
            String response = "";
            String amount= commTransactionDetailsVO.getAmount();
            String currency=commTransactionDetailsVO.getCurrency();

            Inquiry inquiry = new Inquiry();
            inquiry.setMemberId(memberId);
            inquiry.setMemberGuid(memberGuId);
            inquiry.setTrackingMemberCode(trackingMemberCode);
            inquiry.setTransactionDate(transactionDate);
            String inquiryRequest = PayVisionUtils.toJson(inquiry);

            transactionLogger.error("-----inquiry Request-----for "+trackingID+ "----" + inquiryRequest);

            String inquiryResponse = "";
            if (isTest)
            {
                inquiryResponse = PayVisionUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_QUERY_URL"), inquiryRequest);

            }
            else
            {
                inquiryResponse = PayVisionUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_QUERY_URL"), inquiryRequest);
            }
            transactionLogger.error("-----inquiry Response----for "+trackingID+ "----" + inquiryResponse);
            if (functions.isValueNull(inquiryResponse))
            {
                String bankCode="";
                String bankMsg="";
                JSONObject jsonObject = new JSONObject(inquiryResponse);
                JSONArray slideContent = new JSONArray(jsonObject.getJSONObject("RetrieveTransactionResultResult").getString("Cdc"));

                if(slideContent.length()!=0)
                {
                    JSONObject jObj = null;
                    for (int j = 0; j < slideContent.length(); j++)
                    {
                        jObj = slideContent.getJSONObject(0);
                    }

                    JSONArray itemContent = new JSONArray(jObj.getString("Items"));
                    JSONObject itemObj = null;
                    Map bankResMap = new HashMap<>();
                    for (int j = 0; j < itemContent.length(); j++)
                    {
                        itemObj = itemContent.getJSONObject(j);
                        bankResMap.put(itemObj.getString("Key"), itemObj.getString("Value"));

                    }
                    if (bankResMap != null)
                    {
                        if (bankResMap.get("BankCode") != null && bankResMap.get("BankMessage") != null)
                        {
                            bankCode = (String) bankResMap.get("BankCode");
                            bankMsg = (String) bankResMap.get("BankMessage");
                        }
                        else if (bankResMap.get("ErrorCode") != null && bankResMap.get("ErrorMessage") != null)
                        {
                            bankCode = (String) bankResMap.get("ErrorCode");
                            bankMsg = (String) bankResMap.get("ErrorMessage");
                        }
                    }

                    if(!functions.isValueNull(bankMsg)){
                        bankMsg=jsonObject.getJSONObject("RetrieveTransactionResultResult").getString("Message");
                    }

                    if ((!jsonObject.getJSONObject("RetrieveTransactionResultResult").has("Error")) && jsonObject.getJSONObject("RetrieveTransactionResultResult").getString("Result").equals("0"))
                    {
                        status = "success";
                        payVisionResponseVO.setStatus(status);
                        String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        payVisionResponseVO.setDescriptor(descriptor);
                    }
                    else
                    {
                        payVisionResponseVO.setStatus("fail");
                    }
                    payVisionResponseVO.setRemark(jsonObject.getJSONObject("RetrieveTransactionResultResult").getString("Message"));
                    payVisionResponseVO.setTransactionId(jsonObject.getJSONObject("RetrieveTransactionResultResult").getString("TransactionId"));
                    payVisionResponseVO.setErrorCode(bankCode);
                    payVisionResponseVO.setDescription(bankMsg);
                    payVisionResponseVO.setTransactionGuid(jsonObject.getJSONObject("RetrieveTransactionResultResult").getString("TransactionGuid"));
                    payVisionResponseVO.setTrackingMemberCode(jsonObject.getJSONObject("RetrieveTransactionResultResult").getString("TrackingMemberCode"));
                    payVisionResponseVO.setResponseTime(jsonObject.getJSONObject("RetrieveTransactionResultResult").getString("TransactionDateTime"));
                    payVisionResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                    payVisionResponseVO.setTransactionStatus(bankMsg);
                    payVisionResponseVO.setBankTransactionDate(jsonObject.getJSONObject("RetrieveTransactionResultResult").getString("TransactionDateTime"));
                    payVisionResponseVO.setMerchantId(String.valueOf(memberId));
                    payVisionResponseVO.setAuthCode(bankCode);
                    payVisionResponseVO.setAmount(amount);
                    payVisionResponseVO.setCurrency(currency);
                }else
                {
                    payVisionResponseVO.setStatus("notFound");
                    payVisionResponseVO.setDescription(jsonObject.getJSONObject("RetrieveTransactionResultResult").getString("Message"));
                    payVisionResponseVO.setErrorCode(jsonObject.getJSONObject("RetrieveTransactionResultResult").getString("Result"));
                    payVisionResponseVO.setRemark(jsonObject.getJSONObject("RetrieveTransactionResultResult").getString("Message"));
                    payVisionResponseVO.setTransactionId(jsonObject.getJSONObject("RetrieveTransactionResultResult").getString("TransactionId"));
                    payVisionResponseVO.setTransactionGuid(jsonObject.getJSONObject("RetrieveTransactionResultResult").getString("TransactionGuid"));
                    payVisionResponseVO.setTrackingMemberCode(jsonObject.getJSONObject("RetrieveTransactionResultResult").getString("TrackingMemberCode"));
                    payVisionResponseVO.setResponseTime(jsonObject.getJSONObject("RetrieveTransactionResultResult").getString("TransactionDateTime"));
                    payVisionResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                    payVisionResponseVO.setBankTransactionDate(jsonObject.getJSONObject("RetrieveTransactionResultResult").getString("TransactionDateTime"));
                    payVisionResponseVO.setTransactionStatus(jsonObject.getJSONObject("RetrieveTransactionResultResult").getString("Message"));
                    payVisionResponseVO.setMerchantId(String.valueOf(memberId));
                    payVisionResponseVO.setAuthCode(jsonObject.getJSONObject("RetrieveTransactionResultResult").getString("Result"));
                    payVisionResponseVO.setAmount(amount);
                    payVisionResponseVO.setCurrency(currency);
                }
            }
        }
        catch (Exception e){
            log.error("Exception:::::",e);
            PZExceptionHandler.raiseTechnicalViolationException(PayVisionPaymentGateway.class.getName(), "processQuery", null, "common", "Exception while placing  capture transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return payVisionResponseVO;
    }

    public GenericResponseVO process3DSaleConfirmation(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException
    {
        transactionLogger.error("-----Entering into  process3DSaleConfirmation-----");

        PayVisionResponseVO payVisionResponseVO = new PayVisionResponseVO();
        Functions functions = new Functions();
        int countryId = 0;
        String displayName = "";
        String paRes = "";
        try
        {
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            boolean isTest = gatewayAccount.isTest();
            String isForexMid = gatewayAccount.getForexMid();
            transactionLogger.error("IsForexMid value 3D SALE---"+isForexMid);
            PayVisionRequestVO payVisionRequestVO = (PayVisionRequestVO) requestVO;
            CommTransactionDetailsVO transDetailsVO = payVisionRequestVO.getTransDetailsVO();
            CommAddressDetailsVO addressDetailsVO = payVisionRequestVO.getAddressDetailsVO();
            CommCardDetailsVO commCardDetailsVO = payVisionRequestVO.getCardDetailsVO();
            CommMerchantVO commMerchantVO = payVisionRequestVO.getCommMerchantVO();

            int memberId = Integer.parseInt(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
            String memberGuId = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
            if (functions.isValueNull(CountryCodeISO3166.getNumericCountryCode(addressDetailsVO.getCountry())))
            {
                countryId = Integer.parseInt(CountryCodeISO3166.getNumericCountryCode(addressDetailsVO.getCountry()));
            }
            String cvv = commCardDetailsVO.getcVV();
            transactionLogger.debug("num inside porcess3DSale----for "+trackingID+ "----" + functions.maskingNumber(cvv));


            String trackingMemberCode = "Transaction-" + trackingID;
            String amount = transDetailsVO.getAmount();

            int merchantAccountType = ECOMMERECE;
            if (functions.isValueNull(payVisionRequestVO.getPaRes()))
            {
                paRes = payVisionRequestVO.getPaRes();
            }
            else
            {
                paRes = "";
            }
            int enrollmentId = payVisionRequestVO.getEnrollmentId();
            String enrollmentTrackingMemberCode = payVisionRequestVO.getEnrollmentTrackingMemberCode();


            String status = "";
            String response = "";
            String currency="";
            String tmpl_amount="";
            String tmpl_currency="";
            if (functions.isValueNull(transDetailsVO.getCurrency()))
            {
                currency=transDetailsVO.getCurrency();
            }
            if (functions.isValueNull(addressDetailsVO.getTmpl_amount()))
            {
                tmpl_amount=addressDetailsVO.getTmpl_amount();
            }
            if (functions.isValueNull(addressDetailsVO.getTmpl_currency()))
            {
                tmpl_currency=addressDetailsVO.getTmpl_currency();
            }

            String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

            if (functions.isValueNull(gatewayAccount.getDisplayName()) && gatewayAccount.getDisplayName().contains("*"))
            {
                displayName = StringUtils.substringBefore(gatewayAccount.getDisplayName(), "*");
            }
            else
            {
                displayName = gatewayAccount.getDisplayName();
            }

            String merchantOrganizationName = displayName + "*" + payVisionRequestVO.getCommMerchantVO().getMerchantOrganizationName();
            String partnerSupportContactNumber = payVisionRequestVO.getCommMerchantVO().getPartnerSupportContactNumber();

            if (gatewayAccount.getIsDynamicDescriptor().equalsIgnoreCase("Y"))
            {
                if (!merchantOrganizationName.equals("") && merchantOrganizationName.length() != 0 && merchantOrganizationName.length() > 25)
                {
                    merchantOrganizationName = merchantOrganizationName.substring(0, 25);
                }
                if (!partnerSupportContactNumber.equals("") && partnerSupportContactNumber.length() != 0 && partnerSupportContactNumber.length() > 13)
                {
                    partnerSupportContactNumber = partnerSupportContactNumber.substring(0, 13);
                }
                descriptor = merchantOrganizationName + "|" + partnerSupportContactNumber;
            }

            PaymentUsingIntegratedMPI paymentUsingIntegratedMPI = new PaymentUsingIntegratedMPI();

            paymentUsingIntegratedMPI.setMemberId(memberId);
            paymentUsingIntegratedMPI.setMemberGuid(memberGuId);
            paymentUsingIntegratedMPI.setCountryId(countryId);
            paymentUsingIntegratedMPI.setCardCvv(cvv);
            paymentUsingIntegratedMPI.setEnrollmentId(enrollmentId);
            paymentUsingIntegratedMPI.setEnrollmentTrackingMemberCode(enrollmentTrackingMemberCode);
            paymentUsingIntegratedMPI.setMerchantAccountType(merchantAccountType);
            paymentUsingIntegratedMPI.setTrackingMemberCode(trackingID + "_3DS");
            paymentUsingIntegratedMPI.setPayerAuthenticationResponse(paRes);
            paymentUsingIntegratedMPI.setDbaName(merchantOrganizationName);
            paymentUsingIntegratedMPI.setDbaCity(partnerSupportContactNumber);

            PaymentUsingIntegratedMPI paymentUsingIntegratedMPILogs = new PaymentUsingIntegratedMPI();
            paymentUsingIntegratedMPILogs.setMemberId(memberId);
            paymentUsingIntegratedMPILogs.setMemberGuid(memberGuId);
            paymentUsingIntegratedMPILogs.setCountryId(countryId);
            paymentUsingIntegratedMPILogs.setCardCvv(functions.maskingNumber(cvv));
            paymentUsingIntegratedMPILogs.setEnrollmentId(enrollmentId);
            paymentUsingIntegratedMPILogs.setEnrollmentTrackingMemberCode(enrollmentTrackingMemberCode);
            paymentUsingIntegratedMPILogs.setMerchantAccountType(merchantAccountType);
            paymentUsingIntegratedMPILogs.setTrackingMemberCode(trackingID + "_3DS");
            paymentUsingIntegratedMPILogs.setPayerAuthenticationResponse(paRes);
            paymentUsingIntegratedMPILogs.setDbaName(merchantOrganizationName);
            paymentUsingIntegratedMPILogs.setDbaCity(partnerSupportContactNumber);


            if(isForexMid.equalsIgnoreCase("Y"))
            {
                AdditionalInfo additionalInfo = new AdditionalInfo();
                Recipient recipient = new Recipient();

                String dateOfBirth = "1990-01-30";
                String firstSix = Functions.getFirstSix(commCardDetailsVO.getCardNum());
                String lastFour = Functions.getLastFour(commCardDetailsVO.getCardNum());

                recipient.setDateOfBirth(dateOfBirth);
                recipient.setAccountNumber(firstSix + lastFour);

                recipient.setPostalCode(commMerchantVO.getZipCode());
                recipient.setSurname(commMerchantVO.getLastName());

                additionalInfo.setRecipient(recipient);
                paymentUsingIntegratedMPI.setAdditionalInfo(additionalInfo);
                paymentUsingIntegratedMPILogs.setAdditionalInfo(additionalInfo);

            }

            String paymentUsingIntegratedMPIRequest = PayVisionUtils.toJson(paymentUsingIntegratedMPI);
            String paymentUsingIntegratedMPIRequestLogs = PayVisionUtils.toJson(paymentUsingIntegratedMPILogs);

            transactionLogger.error("-----paymentUsingIntegratedMPI Request-----for "+trackingID+ "----" + paymentUsingIntegratedMPIRequestLogs);

            String paymentUsingIntegratedMPIResponse = "";
            if (isTest)
            {
                transactionLogger.error("----inside Test-----");
                paymentUsingIntegratedMPIResponse = PayVisionUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_PaymentUsingIntegratedMPI_URL"), paymentUsingIntegratedMPIRequest);
            }
            else
            {
                transactionLogger.error("----inside Live-----");
                paymentUsingIntegratedMPIResponse = PayVisionUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_PaymentUsingIntegratedMPI_URL"), paymentUsingIntegratedMPIRequest);
            }
            transactionLogger.error("-----PaymentUsingIntegratedMPI Response----for "+trackingID+ "----" + paymentUsingIntegratedMPIResponse);

            if (functions.isValueNull(paymentUsingIntegratedMPIResponse))
            {

                String bankCode="";
                String bankApprovalCode="";
                String bankMsg="";

                JSONObject jsonObject = new JSONObject(paymentUsingIntegratedMPIResponse);
                if(jsonObject.has("PaymentUsingIntegratedMPIResult"))
                {

                    if(jsonObject.getJSONObject("PaymentUsingIntegratedMPIResult").has("Cdc"))
                    {
                        JSONArray slideContent = new JSONArray(jsonObject.getJSONObject("PaymentUsingIntegratedMPIResult").getString("Cdc"));
                        if(slideContent.length()>0)
                        {
                            JSONObject jObj = null;
                            for (int j = 0; j < slideContent.length(); j++)
                            {
                                jObj = slideContent.getJSONObject(0);
                            }

                                JSONArray itemContent = new JSONArray(jObj.getString("Items"));
                                JSONObject itemObj = null;
                                Map bankResMap = new HashMap<>();
                                for (int j = 0; j < itemContent.length(); j++)
                                {
                                    itemObj = itemContent.getJSONObject(j);
                                    bankResMap.put(itemObj.getString("Key"), itemObj.getString("Value"));

                                }
                                if (bankResMap != null)
                                {
                                    if (bankResMap.get("BankCode") != null && bankResMap.get("BankMessage") != null)
                                    {
                                        bankCode = (String) bankResMap.get("BankCode");
                                        bankApprovalCode = (String) bankResMap.get("BankApprovalCode");
                                        bankMsg = (String) bankResMap.get("BankMessage");
                                    }
                                    else if (bankResMap.get("ErrorCode") != null && bankResMap.get("ErrorMessage") != null)
                                    {
                                        bankCode = (String) bankResMap.get("ErrorCode");
                                        bankMsg = (String) bankResMap.get("ErrorMessage");
                                    }
                                }

                                if (!functions.isValueNull(bankMsg))
                                {
                                    bankMsg = jsonObject.getJSONObject("PaymentUsingIntegratedMPIResult").getString("Message");
                                }
                                if ((!jsonObject.getJSONObject("PaymentUsingIntegratedMPIResult").has("Error")) && jsonObject.getJSONObject("PaymentUsingIntegratedMPIResult").getString("Result").equals("0"))
                                {
                                    status = "success";
                                    payVisionResponseVO.setStatus(status);
                                    //String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                                    payVisionResponseVO.setDescriptor(descriptor);
                                }
                                else
                                {
                                    status = "fail";
                                    payVisionResponseVO.setStatus(status);
                                }
                                payVisionResponseVO.setTransactionStatus(status);
                                payVisionResponseVO.setTransactionId(jsonObject.getJSONObject("PaymentUsingIntegratedMPIResult").getString("TransactionId"));
                                payVisionResponseVO.setAmount(amount);
                                payVisionResponseVO.setRemark(jsonObject.getJSONObject("PaymentUsingIntegratedMPIResult").getString("Message"));
                                payVisionResponseVO.setErrorCode(bankCode);
                                payVisionResponseVO.setAuthCode(bankApprovalCode);
                                payVisionResponseVO.setDescription(bankMsg);
                                payVisionResponseVO.setTransactionGuid(jsonObject.getJSONObject("PaymentUsingIntegratedMPIResult").getString("TransactionGuid"));
                                payVisionResponseVO.setTrackingMemberCode(jsonObject.getJSONObject("PaymentUsingIntegratedMPIResult").getString("TrackingMemberCode"));
                                payVisionResponseVO.setResponseTime(jsonObject.getJSONObject("PaymentUsingIntegratedMPIResult").getString("TransactionDateTime"));
                                payVisionResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());

                        }
                        else
                        {
                            transactionLogger.error("Inside Slidelength=0");
                            status = "fail";
                            payVisionResponseVO.setStatus(status);
                            if(jsonObject.getJSONObject("PaymentUsingIntegratedMPIResult").has("Message"))
                            {
                                payVisionResponseVO.setRemark(jsonObject.getJSONObject("PaymentUsingIntegratedMPIResult").getString("Message"));
                                payVisionResponseVO.setDescription(jsonObject.getJSONObject("PaymentUsingIntegratedMPIResult").getString("Message"));
                            }
                            if(jsonObject.getJSONObject("PaymentUsingIntegratedMPIResult").has("Result"))
                                payVisionResponseVO.setErrorCode(jsonObject.getJSONObject("PaymentUsingIntegratedMPIResult").getString("Result"));

                        }
                    }
                    else
                    {
                        transactionLogger.error("Inside no CDC");
                        status = "fail";
                        payVisionResponseVO.setStatus(status);
                    }
                }
                else
                {
                    transactionLogger.error("Inside No Json Found");
                    status = "fail";
                    payVisionResponseVO.setStatus(status);
                }
            }
           payVisionResponseVO.setCurrency(currency);
           payVisionResponseVO.setTmpl_Amount(tmpl_amount);
           payVisionResponseVO.setTmpl_Currency(tmpl_currency);
        }
        catch (JSONException e)
        {
            log.error("Exception:::::for "+trackingID+ "----",e);
            PZExceptionHandler.raiseTechnicalViolationException(PayVisionPaymentGateway.class.getName(), "process3DSaleConfirmation()", null, "common", "Exception while placing Payout transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return payVisionResponseVO;
    }

    public GenericResponseVO process3DAuthConfirmation(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException
    {
        transactionLogger.error("-----Entering into  process3DAuthConfirmation-----");

        PayVisionResponseVO payVisionResponseVO = new PayVisionResponseVO();
        Functions functions= new Functions();
        int countryId=0;
        String displayName = "";
        String paRes="";
        try
        {
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            boolean isTest = gatewayAccount.isTest();
            String isForexMid = gatewayAccount.getForexMid();

            PayVisionRequestVO payVisionRequestVO = (PayVisionRequestVO) requestVO;
            CommCardDetailsVO commCardDetailsVO = payVisionRequestVO.getCardDetailsVO();
            CommTransactionDetailsVO transDetailsVO = payVisionRequestVO.getTransDetailsVO();
            CommMerchantVO commMerchantVO = payVisionRequestVO.getCommMerchantVO();
            CommAddressDetailsVO addressDetailsVO = payVisionRequestVO.getAddressDetailsVO();

            int memberId = Integer.parseInt(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
            String memberGuId = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();


            if (functions.isValueNull(CountryCodeISO3166.getNumericCountryCode(addressDetailsVO.getCountry())))
            {
                countryId = Integer.parseInt(CountryCodeISO3166.getNumericCountryCode(addressDetailsVO.getCountry()));
            }
            int currencyId = Integer.parseInt(CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency()));
            String trackingMemberCode = "Auth-" + trackingID;
            String amount = transDetailsVO.getAmount();
            String cvv = commCardDetailsVO.getcVV();
            transactionLogger.debug("num inside porcess3DAuth----" + functions.maskingNumber(cvv));

            String currency="";
            String tmpl_amount="";
            String tmpl_currency="";
            if (functions.isValueNull(transDetailsVO.getCurrency()))
            {
                currency=transDetailsVO.getCurrency();
            }
            if (functions.isValueNull(addressDetailsVO.getTmpl_amount()))
            {
                tmpl_amount=addressDetailsVO.getTmpl_amount();
            }
            if (functions.isValueNull(addressDetailsVO.getTmpl_currency()))
            {
                tmpl_currency=addressDetailsVO.getTmpl_currency();
            }

            int merchantAccountType = ECOMMERECE;
            if (functions.isValueNull(payVisionRequestVO.getPaRes()))
            {
                paRes = payVisionRequestVO.getPaRes();
            }
            else
            {
                paRes = "";
            }
            int enrollmentId = payVisionRequestVO.getEnrollmentId();
            String enrollmentTrackingMemberCode = payVisionRequestVO.getEnrollmentTrackingMemberCode();

            String status = "";
            String response = "";

            String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

            if (functions.isValueNull(gatewayAccount.getDisplayName()) && gatewayAccount.getDisplayName().contains("*"))
            {
                displayName = StringUtils.substringBefore(gatewayAccount.getDisplayName(), "*");
            }
            else
            {
                displayName = gatewayAccount.getDisplayName();
            }

            String merchantOrganizationName = displayName + "*" + payVisionRequestVO.getCommMerchantVO().getMerchantOrganizationName();
            String partnerSupportContactNumber = payVisionRequestVO.getCommMerchantVO().getPartnerSupportContactNumber();

            if (gatewayAccount.getIsDynamicDescriptor().equalsIgnoreCase("Y"))
            {
                if (!merchantOrganizationName.equals("") && merchantOrganizationName.length() != 0 && merchantOrganizationName.length() > 25)
                {
                    merchantOrganizationName = merchantOrganizationName.substring(0, 25);
                }
                if (!partnerSupportContactNumber.equals("") && partnerSupportContactNumber.length() != 0 && partnerSupportContactNumber.length() > 13)
                {
                    partnerSupportContactNumber = partnerSupportContactNumber.substring(0, 13);
                }
                descriptor = merchantOrganizationName + "|" + partnerSupportContactNumber;
            }

            AuthorizeUsingIntegratedMPI authorizeUsingIntegratedMPI = new AuthorizeUsingIntegratedMPI();
            authorizeUsingIntegratedMPI.setMemberId(memberId);
            authorizeUsingIntegratedMPI.setMemberGuid(memberGuId);
            authorizeUsingIntegratedMPI.setCountryId(countryId);
            authorizeUsingIntegratedMPI.setCardCvv(cvv);
            authorizeUsingIntegratedMPI.setEnrollmentId(enrollmentId);
            authorizeUsingIntegratedMPI.setEnrollmentTrackingMemberCode(enrollmentTrackingMemberCode);
            authorizeUsingIntegratedMPI.setMerchantAccountType(merchantAccountType);
            authorizeUsingIntegratedMPI.setTrackingMemberCode(trackingID + "_3DA");
            authorizeUsingIntegratedMPI.setPayerAuthenticationResponse(paRes);
            authorizeUsingIntegratedMPI.setDbaName(merchantOrganizationName);
            authorizeUsingIntegratedMPI.setDbaCity(partnerSupportContactNumber);

            AuthorizeUsingIntegratedMPI authorizeUsingIntegratedMPILog = new AuthorizeUsingIntegratedMPI();
            authorizeUsingIntegratedMPILog.setMemberId(memberId);
            authorizeUsingIntegratedMPILog.setMemberGuid(memberGuId);
            authorizeUsingIntegratedMPILog.setCountryId(countryId);
            authorizeUsingIntegratedMPILog.setCardCvv(functions.maskingNumber(cvv));
            authorizeUsingIntegratedMPILog.setEnrollmentId(enrollmentId);
            authorizeUsingIntegratedMPILog.setEnrollmentTrackingMemberCode(enrollmentTrackingMemberCode);
            authorizeUsingIntegratedMPILog.setMerchantAccountType(merchantAccountType);
            authorizeUsingIntegratedMPILog.setTrackingMemberCode(trackingID + "_3DA");
            authorizeUsingIntegratedMPILog.setPayerAuthenticationResponse(paRes);
            authorizeUsingIntegratedMPILog.setDbaName(merchantOrganizationName);
            authorizeUsingIntegratedMPILog.setDbaCity(partnerSupportContactNumber);

            if(isForexMid.equalsIgnoreCase("Y"))
            {
                AdditionalInfo additionalInfo = new AdditionalInfo();
                Recipient recipient = new Recipient();

                String dateOfBirth = "1990-01-30";
                String firstSix = Functions.getFirstSix(commCardDetailsVO.getCardNum());
                String lastFour = Functions.getLastFour(commCardDetailsVO.getCardNum());

                recipient.setDateOfBirth(dateOfBirth);
                recipient.setAccountNumber(firstSix + lastFour);
                recipient.setPostalCode(commMerchantVO.getZipCode());
                recipient.setSurname(commMerchantVO.getLastName());

                additionalInfo.setRecipient(recipient);
                authorizeUsingIntegratedMPI.setAdditionalInfo(additionalInfo);
                authorizeUsingIntegratedMPILog.setAdditionalInfo(additionalInfo);
            }

            String authorizeUsingIntegratedMPIRequest = PayVisionUtils.toJson(authorizeUsingIntegratedMPI);
            String authorizeUsingIntegratedMPIRequestLog = PayVisionUtils.toJson(authorizeUsingIntegratedMPILog);


            transactionLogger.error("-----authorizeUsingIntegratedMPI Request-----for "+trackingID+ "----" + authorizeUsingIntegratedMPIRequestLog);

            String authorizeUsingIntegratedMPIResponse = "";
            if (isTest)
            {
                transactionLogger.error("----inside Test-----");
                authorizeUsingIntegratedMPIResponse = PayVisionUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_AuthorizeUsingIntegratedMPI_URL"), authorizeUsingIntegratedMPIRequest);

            }
            else
            {
                transactionLogger.error("----inside Live-----");
                authorizeUsingIntegratedMPIResponse = PayVisionUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_AuthorizeUsingIntegratedMPI_URL"), authorizeUsingIntegratedMPIRequest);
            }
            transactionLogger.error("-----authorizeUsingIntegratedMPI Response----for "+trackingID+ "----" + authorizeUsingIntegratedMPIResponse);

            if (functions.isValueNull(authorizeUsingIntegratedMPIResponse))
            {

                String bankCode="";
                String bankApprovalCode="";
                String bankMsg="";
                JSONObject jsonObject = new JSONObject(authorizeUsingIntegratedMPIResponse);
                JSONArray slideContent = new JSONArray(jsonObject.getJSONObject("AuthorizeUsingIntegratedMPIResult").getString("Cdc"));

                JSONObject jObj = null;
                for (int j = 0; j < slideContent.length(); j++)
                {
                    jObj = slideContent.getJSONObject(0);
                }

                JSONArray itemContent = new JSONArray(jObj.getString("Items"));
                JSONObject itemObj = null;
                Map bankResMap = new HashMap<>();
                for (int j = 0; j< itemContent.length(); j++)
                {
                    itemObj = itemContent.getJSONObject(j);
                    bankResMap.put(itemObj.getString("Key"), itemObj.getString("Value"));

                }
                if(bankResMap!=null){
                    if(bankResMap.get("BankCode")!=null && bankResMap.get("BankMessage")!=null){
                        bankCode=(String)bankResMap.get("BankCode");
                        bankApprovalCode = (String) bankResMap.get("BankApprovalCode");
                        bankMsg=(String)bankResMap.get("BankMessage");
                    }else if(bankResMap.get("ErrorCode")!=null && bankResMap.get("ErrorMessage")!=null){
                        bankCode=(String)bankResMap.get("ErrorCode");
                        bankMsg=(String) bankResMap.get("ErrorMessage");
                    }
                }

                if(!functions.isValueNull(bankMsg)){
                    bankMsg=jsonObject.getJSONObject("AuthorizeUsingIntegratedMPIResult").getString("Message");
                }

                if ((!jsonObject.getJSONObject("AuthorizeUsingIntegratedMPIResult").has("Error")) && jsonObject.getJSONObject("AuthorizeUsingIntegratedMPIResult").getString("Result").equals("0"))
                {
                    status = "success";
                    payVisionResponseVO.setStatus(status);
                    //String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    payVisionResponseVO.setDescriptor(descriptor);
                }
                else
                {
                    status = "fail";
                    payVisionResponseVO.setStatus(status);
                }
                payVisionResponseVO.setTransactionStatus(status);
                payVisionResponseVO.setTransactionId(jsonObject.getJSONObject("AuthorizeUsingIntegratedMPIResult").getString("TransactionId"));
                payVisionResponseVO.setAmount(amount);
                payVisionResponseVO.setRemark(jsonObject.getJSONObject("AuthorizeUsingIntegratedMPIResult").getString("Message"));
                payVisionResponseVO.setErrorCode(bankCode);
                payVisionResponseVO.setAuthCode(bankApprovalCode);
                payVisionResponseVO.setDescription(bankMsg);
                payVisionResponseVO.setTransactionGuid(jsonObject.getJSONObject("AuthorizeUsingIntegratedMPIResult").getString("TransactionGuid"));
                payVisionResponseVO.setTrackingMemberCode(jsonObject.getJSONObject("AuthorizeUsingIntegratedMPIResult").getString("TrackingMemberCode"));
                payVisionResponseVO.setResponseTime(jsonObject.getJSONObject("AuthorizeUsingIntegratedMPIResult").getString("TransactionDateTime"));
                payVisionResponseVO.setTransactionType(PZProcessType.THREE_D_AUTH.toString());
            }
            payVisionResponseVO.setCurrency(currency);
            payVisionResponseVO.setTmpl_Amount(tmpl_amount);
            payVisionResponseVO.setTmpl_Currency(tmpl_currency);
        }
        catch (JSONException e){
            log.error("Exception:::::for "+trackingID+ "----",e);
            PZExceptionHandler.raiseTechnicalViolationException(PayVisionPaymentGateway.class.getName(), "process3DAuthConfirmation()", null, "common", "Exception while placing Payout transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return payVisionResponseVO;
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        PZGenericConstraint genConstraint = new PZGenericConstraint("AlliedWalletPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }

    @Override
    public String getMaxWaitDays()
    {
        return "5";
    }
}