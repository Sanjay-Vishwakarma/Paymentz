<%@ page language="java" %>
<%@ page import="com.directi.pg.Functions"%>
<%@ page import="com.directi.pg.Logger" %>
<%--<link rel="stylesheet" type="text/css" href="/merchant/style1/styles123.css">--%>
<%!
    private static Logger logger=new Logger("page.jsp");

%>
<%!Functions  functions=new Functions();%>
<%

    int noofpagesperblock = 10;


    int currentnoofrecords = Integer.parseInt(request.getParameter("numrecords"));

    int currentnoofrecordsperpage = Integer.parseInt(request.getParameter("numrows"));

    //int currentnoofrecordsperpage=10;


    if (currentnoofrecordsperpage == 0)

    {

        currentnoofrecordsperpage = 30;

    }


    int currentpageno = Integer.parseInt(request.getParameter("pageno"));

    String str = request.getParameter("str");

    String querystr = str;

    String linkpage = request.getParameter("page");


    String pageorderby = request.getParameter("orderby");

////////// for calculating no of blocks


    int noofblocks = 0;


    int blocksremainder = currentnoofrecords % (currentnoofrecordsperpage * noofpagesperblock);

    int blocksdivisor = (int) Math.floor(currentnoofrecords / (currentnoofrecordsperpage * noofpagesperblock));


    if (blocksremainder == 0)

    {

        noofblocks = blocksdivisor;

    }

    else

    {

        noofblocks = blocksdivisor + 1;

    }


    int currentblock = Integer.parseInt(request.getParameter("currentblock"));

////////// for calculating no of pages

    int totalnofopages = 0;

    int remainder = currentnoofrecords % currentnoofrecordsperpage;

    int divisor = (int) Math.floor(currentnoofrecords / currentnoofrecordsperpage);


    if (remainder == 0)

    {

        totalnofopages = divisor;

    }

    else

    {

        totalnofopages = divisor + 1;

    }

///////checking page no

////this code cshould always be above the code for checking current variable


    if (currentpageno > totalnofopages)

    {

        currentpageno = totalnofopages;

    }


    if (currentpageno < 1)

    {

        currentpageno = 1;

    }

//////////////chcking the currentblock variable


    int tempcurrentblock = currentpageno / noofpagesperblock;


    if (currentpageno % noofpagesperblock != 0)

    {

        currentblock = tempcurrentblock + 1;

    }

    else

    {

        currentblock = tempcurrentblock;

    }

//		String str="SName="+name+"&SState="+state+"&SCity="+city+"&SZip="+zip+"&SCountry="+country+"&SStatus="+commaseperatedownerstatus+"&SRec="+records+"&SOrderby="+commaseperatedorderby;

    String tempstr = "";


    int nextblock = currentblock + 1;

    int prevblock = currentblock - 1;


    if (prevblock == 0)

    {

        prevblock = 1;

    }


    if (nextblock == 0)

    {

        nextblock = 1;

    }


    int nextblockpageno = currentblock * noofpagesperblock + 1;

    int prevblockpageno = ((currentblock - 2) * noofpagesperblock) + 1;


    int tempprevcurrentblock = currentblock;

    int tempnextcurrentblock = currentblock;

/*

out.println("currentnoofrecords= "+currentnoofrecords);	

out.println("currentnoofrecordsperpage= "+currentnoofrecordsperpage);	

out.println("noofblocks= "+noofblocks);			

out.println("currentblock= "+currentblock);	

out.println("nextblock= "+nextblock);

out.println("prevblock= "+prevblock);

out.println("nextblockpageno= "+nextblockpageno);

out.println("prevblockpageno= "+prevblockpageno);

*/

//out.println("currentpageno= "+currentpageno);

//out.println(querystr+"<br><br>");

%>

<%

    StringBuffer input = new StringBuffer();
    String requestParameter[]=str.split("&");
    for(int i=0;i<requestParameter.length;i++)
    {
        if(functions.isValueNull(requestParameter[i]))
        {
            String value="";
            String requestValuePair[] = requestParameter[i].split("=");
            logger.debug("input values STRING::"+requestParameter[i]);
            if(requestValuePair.length>1)
                value =requestValuePair[1];
            input.append("<input type=\"hidden\" name="+requestValuePair[0]+" value=\""+value+"\">");
        }
    }
%>
<%--<form action="<%=linkpage%>?<%=querystr%>&currentblock=<%=currentblock%>" method=post>--%>
<%--<div class="reporttable" style="width:50%">--%>
    <table align=right valign=top style="width:auto" id="page_navigate">
        <tr>

            <td style="padding:3px " class="navigate_class">
                <form action="<%=linkpage%>" method=post>
                    <%       //this is for first button
                        if (currentpageno > 1 && currentblock > 1)

                        {
                            out.println(input.toString());
                            if(functions.isValueNull(pageorderby))
                            {
                                out.println("<input type=\"hidden\" name=\"SOrderby\" value="+pageorderby+">");
                            }
                            out.println("<input type=\"hidden\" name=\"currentblock\" value=\"1\">");
//                            out.println("<button type=\"submit\" class=\"goto\" name=\"SPageno\" value=\"1\"><img src=\"/merchant/images/first.png\" ></button>");
                            out.println("<button type=\"submit\" id=\"firstid\" class=\"goto\" name=\"SPageno\" value=\"1\">First</button>");
                        }
                        else
                        {
//                            out.println("<button type=\"button\" class=\"goto\" name=\"SPageno\" value=\"1\"><img src=\"/merchant/images/first1.gif\" ></button>");
                            out.println("<button type=\"button\" id=\"firstid\" class=\"goto_disabled\" name=\"SPageno\" value=\"1\">First</button>");
                        }

                    %>
                </form>
            </td>

            <td style="padding:6px " class="navigate_class">
                <form action="<%=linkpage%>" method=post>
                    <%
                        if (currentblock > 1)

                        {
                            out.println(input.toString());
                            if(functions.isValueNull(pageorderby))
                            {
                                out.println("<input type=\"hidden\" name=\"SOrderby\" value="+pageorderby+">");
                            }

                            out.println("<input type=\"hidden\" name=\"currentblock\" value="+prevblock+">");
//                            out.println("<button type=\"submit\" class=\"goto\" name=\"SPageno\" value="+prevblockpageno+"><img src=\"/merchant/images/previous.png\" ></button>");
                            out.println("<button type=\"submit\" class=\"goto\" name=\"SPageno\" value="+prevblockpageno+">Previous</button>");



                        }
                        else
                        {
//                            out.println("<button type=\"button\" class=\"goto\" name=\"SPageno\" value=\"1\"><img src=\"/merchant/images/previous1.gif\" ></button>");
                            out.println("<button type=\"button\" class=\"goto_disabled\" name=\"SPageno\" value=\"1\">Previous</button>");
                        }
                    %>
                </form>
            </td>
            <td align="center">
                <form action="<%=linkpage%>" method=post>
                    <%
                        out.println(input.toString());
                        if(functions.isValueNull(pageorderby))
                        {
                            out.println("<input type=\"hidden\" name=\"SOrderby\" value="+pageorderby+">");
                        }
                        out.println("<input type=\"hidden\" name=\"currentblock\" value="+prevblock+">");

                        for (int i = 1; i <= noofpagesperblock; i++)
                        {

                            int temppageno = ((currentblock - 1) * noofpagesperblock) + i;



                            if (temppageno <= totalnofopages)

                            {


                                if (temppageno == currentpageno)

                                {

                                    out.println("<input type=\"submit\" class=\"goto\" name=\"SPageno\" value="+temppageno+" disabled>");

                                }

                                else

                                {

                                    tempstr = "";

                                    out.println("<input type=\"submit\" class=\"notgoto\" name=\"SPageno\" value="+temppageno+">");

                                }

                            }

                        }

                    %>
                </form>
            </td>
            <td align="right" style="padding:6px " class="navigate_class">
                <form action="<%=linkpage%>" method=post>
                    <%
                        if (currentblock < noofblocks)

                        {

                            //out.println(" | ");

                            tempstr = "";

                            out.println(input.toString());
                            if(functions.isValueNull(pageorderby))
                            {
                                out.println("<input type=\"hidden\" name=\"SOrderby\" value="+pageorderby+">");
                            }
                            out.println("<input type=\"hidden\" name=\"currentblock\" value="+nextblock+">");
//                            out.println("<button type=\"submit\" class=\"goto\" name=\"SPageno\" value="+nextblockpageno+"><img src=\"/merchant/images/next.png\" ></button>");
                            out.println("<button type=\"submit\" class=\"goto\" name=\"SPageno\" value="+nextblockpageno+">Next</button>");

                        }
                        else
                        {
//                            out.println("<button type=\"button\" class=\"goto\" name=\"SPageno\" value="+nextblockpageno+"><img src=\"/merchant/images/next1.gif\" ></button>");
                            out.println("<button type=\"button\" class=\"goto_disabled\" name=\"SPageno\" value="+nextblockpageno+">Next</button>");
                        }
                    %>
                </form>
            </td>
            <td align="right" style="padding:3px " class="navigate_class">
                <form action="<%=linkpage%>" method=post>
                    <%

                        if (currentpageno != totalnofopages && currentblock != noofblocks)

                        {

                            out.println(input.toString());
                            if(functions.isValueNull(pageorderby))
                            {
                                out.println("<input type=\"hidden\" name=\"SOrderby\" value="+pageorderby+">");
                            }
                            out.println("<input type=\"hidden\" name=\"currentblock\" value="+noofblocks+">");
//                            out.println("<button type=\"submit\" class=\"goto\" name=\"SPageno\" value="+totalnofopages+"><img src=\"/merchant/images/last.png\" ></button>");
                            out.println("<button type=\"submit\" class=\"goto\" name=\"SPageno\" value="+totalnofopages+">Last</button>");

                        }
                        else
                        {
//                            out.println("<button type=\"button\" class=\"goto\" name=\"SPageno\" value="+nextblockpageno+"><img src=\"/merchant/images/last1.gif\" ></button>");
                            out.println("<button type=\"button\" class=\"goto_disabled\" name=\"SPageno\" value="+nextblockpageno+">Last</button>");
                        }
                    %>
                </form>
            </td>
        </tr>
        <tr>
            <td colspan="5" align="center">
                <form action="<%=linkpage%>" method=post>
                    <span class="textb" style="font-family:Open Sans,Helvetica Neue,Helvetica,Arial,sans-serif; font-size: 12px;">Page No:</span>&nbsp;
                    <%
                        out.println(input.toString());
                        if(functions.isValueNull(pageorderby))
                        {
                            out.println("<input type=\"hidden\" name=\"SOrderby\" value="+pageorderby+">");
                        }
                        out.println("<input type=\"hidden\" name=\"currentblock\" value="+currentblock+">");
                    %>
                    <input type="text" name="SPageno" size=2 maxlength=4 class="form-control" style="width: 55px;display: inline-block; height: 27px; border-radius: 4px;">

                    &nbsp;&nbsp;

                    <input type="submit" value="Go To" class="goto">

                </form>

            </td>
        </tr>


    </table>
<%--</div>--%>


