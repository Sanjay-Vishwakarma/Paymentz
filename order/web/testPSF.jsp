<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.io.OutputStreamWriter" %>
<%@ page import="java.net.HttpURLConnection" %>
<%@ page import="java.net.URL" %>
<%@ page import="java.net.URLConnection" %>
<%@ page import="java.util.StringTokenizer" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.security.Key" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="java.util.UUID" %>
<%@ page import="sun.misc.BASE64Encoder" %>
<%@ page import="java.net.URLEncoder" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 1/9/13
  Time: 12:22 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Transaction Request</title>

</head>
<body>

<table width="760" height="100%" border="0" align="center" cellpadding="0" cellspacing="0">
    <tr>
        <td width="760" align="center" valign="middle">
            <img src="./processing.gif" alt="Processing" />
            <br><br>
            <div style="font: 12px verdana, arial, sans-serif; color: #515151;">
                <strong>Please wait while your transaction is being processed ......</strong>
            </div>
        </td>
    </tr>
</table>

<%

    String strUrl = "https://staging.prepaidfinancialservices.com/acqapi/Service.ashx";
    String messageId = UUID.randomUUID().toString();


    String data="<R>\n" +
            "<R1>C</R1>\n" +
            "<R2>Chandan022</R2>\n" +
            "<R3>10000</R3>\n" +
            "<R4>0</R4>\n" +
            "<R5>TestName</R5>\n" +
            "<R6>TestMiddleName</R6>\n" +
            "<R7>TestLastName</R7>\n" +
            "<R8>4th Floor</R8>\n" +
            "<R9>36 Carnaby Street</R9>\n" +
            "<R10>London</R10>\n" +
            "<R11>TX</R11>\n" +
            "<R12>W1F7DR</R12>\n" +
            "<R13>UK</R13>\n" +
            "<R14>123456789</R14>\n" +
            "<R15>VSA</R15>\n" +
            /*"<R16>4176661000001049</R16>\n" +
            "<R17>12</R17>\n" +
            "<R18>2017</R18>\n" +
            "<R19>550</R19>\n" +*/
            "<R16>4176661000001056</R16>\n" +
            "<R17>12</R17>\n" +
            "<R18>2017</R18>\n" +
            "<R19>499</R19>\n" +
            "<R20>826</R20>\n" +
            "<R21>0</R21>\n" +
            "<R22>FirstName</R22>\n" +
            "<R23>MiddleName</R23>\n" +
            "<R24>LastName</R24>\n" +
            "<R25>4th Floor</R25>\n" +
            "<R26>36 Carnaby Street</R26>\n" +
            "<R27>London</R27>\n" +
            "<R28>TX</R28>\n" +
            "<R29>W1F7DR</R29>\n" +
            "<R30>UK</R30>\n" +
            "<R31>12346789</R31>\n" +
            "<R32>tracking01</R32>\n" +
            "<R33>MARK</R33>\n" +
            "<R34></R34>\n" +
            "<R35>S</R35>\n" +
            "<R36>test@test.com</R36>\n" +
            "<R37>123.132.0.1</R37>\n" +
            "<R38>*/*</R38>\n" +
            "<R39>Mozilla/4.0 (compatible; Win32; WinHttp.WinHttpRequest.5)</R39>\n" +
            "<R40></R40>\n" +
            "<R41></R41>\n" +
            "<R42>https://integration.pz.com/order/redirect.jsp</R42>\n" +
            "<R43>2</R43>\n" +
            "<R44>2</R44>\n" +
            "<R45>pz01</R45>\n" +
            "</R>";

    String str ="Username=pzStagAPI&Password=kut183y$amf&APISignature=Register&MessageID="+messageId+"&Data="+URLEncoder.encode(data);
/*

    String data = "<R>\n" +
            "<R1>eNqdmElz6kgSgO9E8B8cPUe6WxI7HdgRpV0CCbQj3bShBS0ILSX49VOA/ex54+h+M7q4lM7KyqrM/CrR\n" +
            "Wo8vYUhrod9ewre1FNa1G4UvSfD6Gz87W0punhcbK2LlpepJCv7b23oP1LB+KDxGCxgKle7HIRW2Dly6\n" +
            "+9GtyLKCmor1K9LuwkudlMUb8Sf+53iNfbyihS5+7BbN29r1K1KQ35boWaFnjb0L1nl4Eei3IDy6bdas\n" +
            "sefrGvucuW/voxq53SfBm0ab0CTYxizARCOCQj1Np0YuyoYOXtfYXWMduE34NsbxBT4fz16I6V848Re+\n" +
            "XGMP+fp8NwfyskW2CRw9a+yraI1O6BIW/vVtOZ6vsR9v67A/l0WINND+fozX2Kd3Z7d4w788BD5F27xL\n" +
            "1/rhbd0k+VevVneviMUae8jXdeM2bf1mr7H30dp3u+4NAEACi81yBdyHqWpZDsOwEXsbod0+VNahn7zh\n" +
            "M+QU+vuYBbKovCRNnN9d/U/BGru7gj1C+rbWkqhAi13Clz7PChTIuGnOf2EYhPBPOPmzvEQYchjH8BWG\n" +
            "FII6if7123NWGAjFsfyfplFuURaJ72bJzW1QckhhE5fByw/fvjOjq3dLBKYy1B/I1B8+MS3+uEvwCTFD\n" +
            "NrHvjX7Z2a+s8rOzl9r9o45d4r7AT4be1mp4DO8ZEb4YqvD6279+pTroJArr5v9x5cONrxY+7Jlu1oZv\n" +
            "Ab9c5lO757amulzK6laz9asRkvOr8vox76m5xn74/r6xZxS/nNZT0cONG9gc7HiGXx2v0K+KkwQ6HpdM\n" +
            "sMClylMPlQAjvmtBxWgnx4cuvqExaaGCWKdlnarFs6RfR9JwEJvBvrT8kTo5mtnFVnf7PZ7goWa0/iRM\n" +
            "nI3Rc3sfG/tzJksBe+ATzGCNbLOrrmSL2/XOcp2Js1UPw4Hqkgwr2PptFRy57ZlKRxvZ1LZSTgfCsZ1g\n" +
            "DpuPSt0z7OPk2l2WWLWtS4bVvLpfJpOJHx2464Gxo8twgMU4e/SFHM6LE3SxiwSmMXethP1GVzl6YfDH\n" +
            "qRwEQCZjGO3Gi41duEWxSMaFDgNdArtDbpqsGTTDwUnkuZg8uuUB5/1bLbpe4Oeg0+JT24TLgF2QKc4W\n" +
            "inGS7BPVXIgdC29x9Pr6JaveI7MJr89IHGb4inYb9znSWi8N/UZ2ESEo+ZVyL0FSuNkLHeblyyOAl99f\n" +
            "dsbnPwClIcHnO1XmiKd++PvL9lVCvCqRvqa/7uKk/P2FejW0NfbzOo+FqfDSJEdUVwiYkiDQ1o2iQK1F\n" +
            "AAokiAQKaAKkFVvclI4Qd74MFIYlFQCdlNlK4MQBwmDIWKJMU+ppHWzJSDaHAzRVIplV6VrLnk+Bf5eS\n" +
            "oJRYQ479XD17OVsr41XjWebVHhsRGl99mtEkEjwsUhCK0ik7DQeONcNdyzlLqgKZyKZNRaFp0BjOQcYF\n" +
            "XibshKQVHYQsxHuJZq5S6vdyCmYyLbpIdn3KlH44+JBK/LKnbkB8+mTrIDN1dBFCCj6sCwxU9y6XdZKy\n" +
            "vO/8LuMZ6Mh3T7yJgCxxKbDfd6QzX3YkMHLnWUTm53ImqQZknha3DOxp+yCeXGsWexRJKoTU8zrw0Dk9\n" +
            "rEg6y3y1omaetYyMcVZ4uXlFUSGFFGVpdKriU8KtIE7eowDAjgLKEtz/PxxQ0Qa9MahOJwGRiHkVZ2Qw\n" +
            "FgiR3LPG3J/6dgq2bW2pK4aR1VGfe6ssOtinRk55LgrLCen2jcYhn+bTKU8sVrEbdXYuJWlICj5ssLmG\n" +
            "hfS2VVfSSqJH9AT6vr8zEGcCEJ+Ec8coc0s/40tlZuCFUCxQ/cW1sFCqnRkIeHAQwS3zpnHDXAG9Ma4S\n" +
            "KwRcdl1kkK1NLN0puCLVJJlMNCgatGZlaXtQRoa4H7vb4WAcefIh8MiOPBFjPqoOtDAT7HB3NqqOaDvr\n" +
            "oJ4DSRyd+kLCr2Y1FerFZEwCjRSXRUzbu04t+MOt4ocDPGS0GMZ+LM1pezuqumrKUQlvwqWpU+22RTRR\n" +
            "EJQ35e52qol+x9e5EpzceDZuMQkKNFAAWU5qOByE+jOHeFViQAqAREGOsihOAxw3YgSUlsqRjYm9z5mt\n" +
            "QciSwATZPZ72Qc0ciqSdg/jI7OHAHrO4o4PgYU2ZoktfMdSIU43EM5Uu7iniArpIabZYRJxcAfL3vFLx\n" +
            "lCQjyJbAOAqJlAQEPhwQlrZMZDLESym2Tvh8c2O+q146shkg6+KBwZeHzr5gwewo4IW4261Wrbn3UAV3\n" +
            "m3FDs/LJD9WLZhsNQ1D5NJzuyls/bqwpXcuTalbjmNXPrPM+6TrWOzK6AfWci6PSO7WXo9iNFnNEYX4y\n" +
            "OUeE6cjw5jGzvFaJM3V0KApXRdcdndo91TrONJnTl1W1ki7bPZG2p2h067FpgCvLS3QyHIp5fQLsK6++\n" +
            "AxiF0whgaf4FYN8eAQzpXwUYfbsX371QfQnlX+YVauenTCiR8AmrCJrKmL06XNbaVk97Y6JBwU0djaSH\n" +
            "A28iQkkVIAOeMKDB6apZ6t3K2clPPasD/R0DEs2qmT9RIiNfdQH1n2AbDiT9jjbmJuvyJ9p05YdM4uy/\n" +
            "BRsNnmBDltQp5J/+bGjS/ITTF4DdYeYXoGdSoDwt1hKli6KD0hlVbu4i9CFLpPA4AwDh1uTMG/L6FlhC\n" +
            "pOAMjJzlzydPPpMPR9DiRFByaNNXSMxUBFXB3YEp55OWy3PEzZh7bnkU1H2xa1gui89VN4dbl4PG8Tia\n" +
            "nezzQmLyq2BILc/ILujUUyJqzZVa7YaDQ76bR4tgSy83BUfMqVVx6dNlSU7zCcKSMc1HR9vacUUe5/ty\n" +
            "fovJriLr7nhbTWIm2hzkORNs5NaMhoO2ujXWHGx30JDnHlltFG1yTwwAuDS8kbP3/AkYqFASAHALbNEW\n" +
            "HAHY3lSJGJkklxZvrODjGtMNHEbq2GxRbsQBZ57QCWf+Iwuy9A4FicQf1uhIsUhSXSbCVZwcdNPZXgWc\n" +
            "XWjRlXWHA10vL5IOjg9YaBLD0cCKSJWJ0j1GWF6U2c6Y4/qCstyqSESIcuinq8O4Xx3oyogUctluDGw1\n" +
            "WWLmZkaxhpgomLE7uOMLfyV6MPEKgiB21Dwj53WgNAs5jRU/3ujJVlfqaHbEKi2TieFg69htNwXCRt9t\n" +
            "HIa7JGIF9Ll4qkabQ9zQ2jRKqzNbLHepU4nq4uheGHUUeLMZJmrdueWj1Ov14wIMBzlMUP3faEOcmmab\n" +
            "2JulXrHZdWxeTJ59NFS/BIEuRRBIwg8IiECk+/2IyUw13kgA5yit4jTBm9CPlDSAxEf/VTooub8pnq9d\n" +
            "AQ0+u4K/63IQBP6hz/nscn50DajL6Z9dDit2HoLCvaSGAz72ZRT+XkqZiaTbUNbB1brL0ocM/yFLyamk\n" +
            "1JB6+soxUDSNGyNLoH6sjDoGABl9HJ+9G7O/n8kDhBByunUv8NX1VyExHPwDJkyJlD4gIb1DQvPGKxzN\n" +
            "JhEIjh8RGQ4+YoKSFCAayICmyETZkJFCTcPL0etUhaEOF3s1b/YLZWdVlA7GaYHN0skKi7AK96TEVyh1\n" +
            "OLiMR8dkcbptruRVqZwTf7PF2XKCgUO3EV2IN6yOpRMmDaJT2JvnzNbCitnPdgYtTSNXOy4qXorMPSqT\n" +
            "jVsW4+6S8r0VUmbPJtN+SpZBJhbHDMMaLt3glUzfmsUJm8qhxlpXr0qU906BAFBPnzc7it3H3S4IN2xl\n" +
            "5lPR4oI4rh2Y5dV5mvgM8193+3e6w8GHNg0e2joJDIjCSWK374s9UkDCLkkuI1kohbIYMokNC1dBYGoM\n" +
            "/pK4qdF48xkxXzby8cwxzORy4z1yVko54x/62vMaQSXbLt4v8yYKnYzP2q1ywyt15I/LWQbD4aCHizQ7\n" +
            "Ar6qUJS1lNps+bZeYnJZLPzRIqm0W6PwEmmG3mI8h+6+4wTvwGx92SpX3j5SK8sYn50YdQybq8OvwLfF\n" +
            "jn3+bMJ+/JT6/JH1+Jr0+OB1//7x9UPYvwGZVDWC</R1>\n" +
            "<R2>Chandan008</R2>\n" +
            "<R3>Pz</R3>\n" +
            "</R>";

    //BASE64Encoder encoder = new BASE64Encoder();
    //data = encoder.encode(data.getBytes());

    String str ="Username=PzStagAPI&Password=kut183y$amf&APISignature=Confirm&MessageID="+messageId+"&Data="+URLEncoder.encode(data);
*/

    System.out.println("----"+str);
    URL url = new URL(strUrl);
    URLConnection connection = url.openConnection();

    if(connection instanceof HttpURLConnection)
    {
        ((HttpURLConnection)connection).setRequestMethod("POST");
    }

    connection.setUseCaches(false);
    connection.setDoInput(true);
    connection.setDoOutput(true);

    // Set request headers for content type and length
    connection.setRequestProperty("Content-type","application/x-www-form-urlencoded");
    connection.setRequestProperty("Content-length",String.valueOf(strUrl.length()));
    //connection.setRequestProperty("Username","PzStagAPI");
    //connection.setRequestProperty("Password","kut183y$amf");
    //connection.setRequestProperty("APISignature","MD5");
    //connection.setRequestProperty("Data",str);

    OutputStreamWriter outSW = new OutputStreamWriter(connection.getOutputStream());
    outSW.write(str);
    outSW.close();

    BufferedReader in = new BufferedReader(
            new InputStreamReader(
                    connection.getInputStream()));
    String strResponse="";
    String decodedString;
    while ((decodedString = in.readLine()) != null)
    {
        strResponse = strResponse + decodedString;
        out.println(strResponse);
        System.out.print("RESULT==============="+strResponse);
    }
    in.close();

%>

    </body></html>

