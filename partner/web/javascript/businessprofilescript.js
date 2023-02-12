/**
 * Created by admin on 1/30/2018.
 */
function selectEnumValue1(name,count)
{
    var hat1 = this.document.getElementsByName("selectBusinessProfileValue1_"+name+"_"+count)[0].selectedIndex;
    this.document.getElementsByName("businessProfileValue1_"+name+"_"+count)[0].value = this.document.getElementsByName("selectBusinessProfileValue1_"+name+"_"+count)[0].options[hat1].value;
}
function selectEnumValue2(name,count)
{
    var hat1 = this.document.getElementsByName("selectBusinessProfileValue2_"+name+"_"+count)[0].selectedIndex;
    this.document.getElementsByName("businessProfileValue2_"+name+"_"+count)[0].value = this.document.getElementsByName("selectBusinessProfileValue2_"+name+"_"+count)[0].options[hat1].value;
}
function selectedDropdown(name)
{
    var hat1 = this.document.getElementsByName(name)[0].selectedIndex ;
    var hatto1 = this.document.getElementsByName(name)[0].options[hat1].getAttribute("id");

    selectedOption[name.split("_")[1]]=hatto1;
    this.document.getElementsByName('businessProfileDesc_'+name.split("_")[1])[0].value=optionValue[option.indexOf(hatto1)].split("|")[1];
    // this.document.getElementsByName('businessProfileRuleType_'+name.split("_")[1])[0].value=optionValue[option.indexOf(hatto1)].split("|")[2];


    this.document.getElementsByName('Text|'+name)[0].value=optionValue[option.indexOf(hatto1)].split("|")[0];

    //This is for value 2
    var isapplicable = "businessProfileIsApplicable_" +name.split("_")[1];
    var value1 = "businessProfileValue1_" + name.split("_")[1];
    var value2 = "businessProfileValue2_" + name.split("_")[1];

    var subBody = document.getElementById("ruleOperation_"+name.split("_")[1]).getElementsByTagName("TBODY")[0];
    var row = document.createElement("tr");
    var rowH = document.createElement("tr");
    var lastRow = subBody.rows.length;
    for(var i=lastRow-1;i>0;i--)
    {
        subBody.deleteRow(i);
    }
    lastRow = subBody.rows.length;
    var iteration=1;

    document.getElementById("ruleOperationTR_"+name.split("_")[1]).innerHTML="<td colspan=\"5\" align=\"center\">"+optionValue[option.indexOf(hatto1)].split("|")[2]+"</td>"
    var row = subBody.insertRow(lastRow);
    var cellLeft0 = row.insertCell(0);
    var textNode0 = "Operation&nbsp;Type";
    cellLeft0.innerHTML=textNode0.replace(/!count!/g, iteration);
    cellLeft0.setAttribute("class","th0");
    cellLeft0.setAttribute("valign","middle");
    cellLeft0.setAttribute("align","center");

    var cellLeft = row.insertCell(1);
    var textNode = "Input&nbsp;Name";
    cellLeft.innerHTML=textNode.replace(/!count!/g, iteration);
    cellLeft.setAttribute("class","th0");
    cellLeft.setAttribute("valign","middle");
    cellLeft.setAttribute("align","center");

    var cellLeft2 = row.insertCell(2);
    var textNode2 = "Operator";
    cellLeft2.innerHTML=textNode2.replace(/!count!/g, iteration);
    cellLeft2.setAttribute("class","th0");
    cellLeft2.setAttribute("valign","middle");
    cellLeft2.setAttribute("align","center");

    if(optionValue[option.indexOf(hatto1)].split("|")[2]=="<%=RuleTypeEnum.FLAT_FILE.name()%>")
    {
        var cellLeft3 = row.insertCell(3);
        var textNode3 = "File Path";
        cellLeft3.innerHTML=textNode3.replace(/!count!/g, iteration);
        cellLeft3.setAttribute("class","th0");
        cellLeft3.setAttribute("valign","middle");
        cellLeft3.setAttribute("align","center");
        cellLeft3.setAttribute("colspan","2");
    }
    else if(optionValue[option.indexOf(hatto1)].split("|")[2]=="<%=RuleTypeEnum.REGULAR_EXPRESSION.name()%>")
    {
        cellLeft2.setAttribute("colspan","3");
    }
    else
    {
        var cellLeft3 = row.insertCell(3);
        var textNode3 = "Value1";
        cellLeft3.innerHTML = textNode3.replace(/!count!/g, iteration);
        cellLeft3.setAttribute("class", "th0");
        cellLeft3.setAttribute("valign", "middle");
        cellLeft3.setAttribute("align", "center");

        var cellLeft4 = row.insertCell(4);
        var textNode4 = "Value2";
        cellLeft4.innerHTML = textNode4.replace(/!count!/g, iteration);
        cellLeft4.setAttribute("class", "th0");
        cellLeft4.setAttribute("valign", "middle");
        cellLeft4.setAttribute("align", "center");
    }
    lastRow++;

    var value2Disable=""
    var value1Disable=""
    var value2Disabled=""
    var value1Disabled=""
    if(document.getElementById(isapplicable).checked != true)
    {
        value1Disable="readonly style=\"background-color:#EBEBE4\"";
        value2Disable="readonly style=\"background-color:#EBEBE4\"";
        value1Disabled="disabled style=\"background-color:#EBEBE4\"";
        value2Disabled="disabled style=\"background-color:#EBEBE4\"";
    }
    for(var i=0; i<optionValue[option.indexOf(hatto1)].split("|")[3].split(",").length;i++)
    {
        var singleOperation=optionValue[option.indexOf(hatto1)].split("|")[3].split(",")[i];
        var innerRow = subBody.insertRow(lastRow);

        var cellLeftInner10 = innerRow.insertCell(0);
        var textNodeInner10 = "<textarea type=\"text\" class=\"form-control\" name=\"operationType_"+name.split("_")[1]+"_!count!\" value=\""+singleOperation.split(":")[6]+"\" readonly <%--style='max-width:50px;max-height:100px'--%>>"+singleOperation.split(":")[6]+"</textarea>";
        cellLeftInner10.innerHTML=textNodeInner10.replace(/!count!/g, iteration);
        cellLeftInner10.setAttribute("class","tr0") ;
        cellLeftInner10.setAttribute("align","center");
        cellLeftInner10.setAttribute("valign","middle");

        var cellLeftInner1 = innerRow.insertCell(1);
        var textNodeInner1 = "<input type=\"text\" class=\"form-control\" name=\"ruleInputName_"+name.split("_")[1]+"_!count!\" value=\""+singleOperation.split(":")[2]+"\" readonly>";
        cellLeftInner1.innerHTML=textNodeInner1.replace(/!count!/g, iteration);
        cellLeftInner1.setAttribute("class","tr0") ;
        cellLeftInner1.setAttribute("align","center");
        cellLeftInner1.setAttribute("valign","middle");

        var cellLeftInner2 = innerRow.insertCell(2);
        var textNodeInner2 = "<input type=\"text\" class=\"form-control\" style=\"width:200px;\" name=\"ruleOperation_"+name.split("_")[1]+"_!count!\" value=\""+singleOperation.split(":")[1]+"\" readonly>";
        cellLeftInner2.innerHTML=textNodeInner2.replace(/!count!/g, iteration);
        cellLeftInner2.setAttribute("class","tr0") ;
        cellLeftInner2.setAttribute("align","center");
        cellLeftInner2.setAttribute("valign","middle");

        if(singleOperation.split(":")[1]!="<%=PZOperatorEnums.BETWEEN.name()%>")
        {
            value2Disable="readonly style=\"background-color:#EBEBE4\"";
            value2Disabled="disabled style=\"background-color:#EBEBE4\"";
        }

        if(optionValue[option.indexOf(hatto1)].split("|")[2]=="<%=RuleTypeEnum.FLAT_FILE.name()%>")
        {
            var cellLeftInner3 = innerRow.insertCell(3);
            var textNodeInner3 = "<input type=\"text\" class=\"form-control\" name=\"businessProfileValue1_" + name.split("_")[1] + "_!count!\" value=\"\" "+value1Disable+">";
            cellLeftInner3.innerHTML = textNodeInner3.replace(/!count!/g, iteration);
            cellLeftInner3.setAttribute("class", "tr0");
            cellLeftInner3.setAttribute("align", "center");
            cellLeftInner3.setAttribute("valign", "middle");
            cellLeftInner3.setAttribute("colspan", "2");
        }
        else if(optionValue[option.indexOf(hatto1)].split("|")[2]=="<%=RuleTypeEnum.REGULAR_EXPRESSION.name()%>")
        {
            cellLeftInner2.setAttribute("colspan","3");
        }
        else
        {
            var cellLeftInner3 = innerRow.insertCell(3);
            var inputType="text";
            var prefix1="";
            var prefix2="";
            if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
            {
                prefix1+="<select class=\"form-control\" name=\"selectBusinessProfileValue1_"+ name.split("_")[1] + "_!count!\" onchange=\"selectEnumValue1('"+name.split("_")[1]+"','!count!')\" "+value1Disabled+">";
                prefix1+="<option value=\"\" >Select Value</option>";
                var enumValues=singleOperation.split(":")[5];
                for(var enumValue=0;enumValue<enumValues.split("!").length;enumValue ++)
                {
                    prefix1+="<option value=\""+enumValues.split("!")[enumValue]+"\" >"+enumValues.split("!")[enumValue]+"</option>";
                }
                prefix1+="</select>";
                prefix2+="<select class=\"form-control\" name=\"selectBusinessProfileValue2_"+ name.split("_")[1] + "_!count!\" onchange=\"selectEnumValue2('"+name.split("_")[1]+"','!count!')\" "+value2Disabled+">";
                prefix2+="<option value=\"\" >Select Value</option>";
                var enumValues=singleOperation.split(":")[5];
                for(var enumValue=0;enumValue<enumValues.split("!").length;enumValue ++)
                {
                    prefix2+="<option value=\""+enumValues.split("!")[enumValue]+"\" >"+enumValues.split("!")[enumValue]+"</option>";
                }
                prefix2+="</select>"
                inputType="hidden";
            }
            var textNodeInner3 = ""+prefix1+"<input type=\""+inputType+"\" class=\"form-control\" name=\"businessProfileValue1_" + name.split("_")[1] + "_!count!\" value=\"\" "+value1Disable+">";
            cellLeftInner3.innerHTML = textNodeInner3.replace(/!count!/g, iteration);
            cellLeftInner3.setAttribute("class", "tr0");
            cellLeftInner3.setAttribute("align", "center");
            cellLeftInner3.setAttribute("valign", "middle");

            var cellLeftInner4 = innerRow.insertCell(4);
            var textNodeInner4 = ""+prefix2+"<input type=\""+inputType+"\" class=\"form-control\" name=\"businessProfileValue2_" + name.split("_")[1] + "_!count!\" value=\"\" "+value2Disable+">";
            cellLeftInner4.innerHTML = textNodeInner4.replace(/!count!/g, iteration);
            cellLeftInner4.setAttribute("class", "tr0");
            cellLeftInner4.setAttribute("align", "center");
            cellLeftInner4.setAttribute("valign", "middle");
        }
        if(singleOperation.split(":")[3]!="")
        {
            var innerRow2 = subBody.insertRow(++lastRow);
            var cellLeftInner21 = innerRow2.insertCell(0);
            var textNodeInner21 = "" + singleOperation.split(":")[3] + "";
            cellLeftInner21.innerHTML = textNodeInner21.replace(/!count!/g, iteration);
            cellLeftInner21.setAttribute("class", "tr0");
            cellLeftInner21.setAttribute("align", "center");
            cellLeftInner21.setAttribute("valign", "middle");
            cellLeftInner21.setAttribute("colspan", "5");
        }
        iteration++;
        lastRow++;
    }
}
function addRow(in_tbl_name)
{
    var i=0;
    var validationPass=[];
    var validationFail=false;
    var minimalCount=false;
    //alert("Inside Add Row");
    for(i=1;i<addedRow.length;i++)
    {
        //alert("Main Iteration:::"+i);
        if(currentRowStatus[i]=="P")
        {
            //alert("Inside P");
            var previous = i;
            var profileId = "businessProfileId_" + previous;
            var isApplicable = "businessProfileIsApplicable_" + previous;

            if($('select[name='+profileId+'] option:selected').val()!="")
            {
                for (var inner = 0; inner <optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[3].split(",").length; inner++)
                {
                    var singleOperation = optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[3].split(",")[inner];
                    var value1 = "businessProfileValue1_" + previous + "_" + (Number(inner) + 1);
                    var value2 = "businessProfileValue2_" + previous + "_" + (Number(inner) + 1);

                    if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.FLAT_FILE.name()%>")
                    {
                        if ((document.getElementsByName(value1) != null ) && ( (document.getElementById(isApplicable).checked == true && document.getElementsByName(value1)[0].value == "" )))
                        {
                            validationPass[i] = false;
                            break;
                        }
                        else
                        {
                            validationPass[i]=true;
                        }
                    }
                    else if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.REGULAR_EXPRESSION.name()%>")
                    {
                        validationPass[i]=true;
                    }
                    else
                    {
                        if ((document.getElementsByName(value1) != null || document.getElementsByName(value2) != null) && ( (document.getElementById(isApplicable).checked == true && document.getElementsByName(value1)[0].value == "" || ( $('select[name=' + profileId + '] option:selected').val() != "" && singleOperation.split(":")[1] == "BETWEEN" && document.getElementsByName(value2)[0].value == "" && document.getElementById(isApplicable).checked == true )) || $('select[name=' + profileId + '] option:selected').val() == ""))
                        {
                            validationPass[i] = false;
                            break;
                        }
                        else
                        {
                            validationPass[i] = true;
                        }
                    }
                }
            }
            else
            {
                validationPass[i]=false;
            }

            if(option.indexOf(selectedOption[i])>=0)
            {
                var index=option.indexOf(selectedOption[i]);
                optionStatus[index]="Y";
            }
        }
        else if(minimalCount==false || currentRowStatus[i]=="D")
        {
            if (minimalCount == false)
            {
                count = i;
                minimalCount = true;
            }
            if(selectedOption.indexOf(selectedOption[i])==selectedOption.lastIndexOf(selectedOption[i]))
            {
                var index = option.indexOf(selectedOption[i]);
                optionStatus[index] = "N";
                selectedOption[i] = "";
            }
        }
    }
    //For count if present or not if present then count ofd or last+1
    if(currentRowStatus[count]=="P")
    {
        if(currentRowStatus.indexOf("D")>=0)
        {
            count=currentRowStatus.indexOf("D");
        }
        else if(currentRowStatus.indexOf("D")==-1)
        {
            count=parseInt(currentRowStatus.lastIndexOf("P"))+1;
        }
    }
    var tbody = document.getElementById(in_tbl_name).getElementsByTagName("TBODY")[0];
    // create row
    var row = document.createElement("tr");
    row.setAttribute("id",count);
    // create table cell 1
    var td1 = document.createElement("td");
    var strHtml1 = "<center><select name=\"businessProfileId_!count!\" style=\"width:inherit;\" class=\"form-control\" onchange=\"selectedDropdown('businessProfileId_!count!')\"><option value=\"\">Select One Rule</option>" +
            "<%=businessRule%>" +
            "</select><input type=\"hidden\" name=\"Text|businessProfileId_!count!\" value=\"\"></center>";
    td1.innerHTML = strHtml1.replace(/!count!/g, count);

    var td2 = document.createElement("td");
    var strHtml2 = "<textarea type=\"text\" name=\"businessProfileDesc_!count!\" style=\"width:inherit;\" class=\"form-control\"  style=\"background-color:#EBEBE4;font-size: 12px;<%--min-height:50px;max-height: 150px;max-width: 100px--%>\" readonly/>";
    td2.innerHTML = strHtml2.replace(/!count!/g, count);

    var td3 = document.createElement("td");
    var strHtml3 = "<input type=\"checkbox\" name=\"businessProfileIsApplicable_!count!\" align=\"center\" id=\"businessProfileIsApplicable_!count!\" onclick=\"enable_text(this.checked,'!count!')\"  value=\"Y\"/>";
    td3.innerHTML = strHtml3.replace(/!count!/g, count);

    var td9 = document.createElement("td");
    /*var strHtml9 = "<input type=\"text\" name=\"businessProfileOperator_!count!\" class=\"txtbox\"  style=\"background-color:#EBEBE4\" readonly/>";*/
    var strHtml9 = "<div class=\"tableScroll\"><table id=\"ruleOperation_!count!\" width=\"100%\" class=\"table table-striped table-bordered table-green dataTable tableScroll\" style=\"margin-bottom: 0px\"><tr id=\"ruleOperationTR_!count!\" colspan=\"3\"></tr></table></div>";

    td9.innerHTML = strHtml9.replace(/!count!/g, count);

    var td7 = document.createElement("td");
    var strHtml7 = "<button type=\"button\" class=\"btn btn-default\" width=\"\" onclick=\"delRow('!count!',event)\"  name=\"delete_!count!\" value=\"!count!\" ><!--style=\"width:60px\"-->Delete</button>";
    td7.innerHTML = strHtml7.replace(/!count!/g, count);

    var td8 = document.createElement("td");
    var strHtml8 = "<button type=\"button\" class=\"btn btn-default\"  name=\"edit_!count!\" onclick=\"editRow('!count!')\" value=\"!count!\" ><!--style=\"width:60px\"-->Edit</button>";
    td8.innerHTML = strHtml8.replace(/!count!/g, count);

    td1.setAttribute("valign","middle");
    td1.setAttribute("align","center");
    td2.setAttribute("valign","middle");
    td2.setAttribute("align","center");
    td3.setAttribute("valign","middle");
    td3.setAttribute("align","center");

    td7.setAttribute("valign","middle");
    td7.setAttribute("align","center");
    td8.setAttribute("valign","middle");
    td8.setAttribute("align","center");
    td9.setAttribute("valign","middle");
    td9.setAttribute("align","center");
    td9.setAttribute("colspan","3");

    // append data to row
    row.appendChild(td1);
    row.appendChild(td2);
    row.appendChild(td3);
    row.appendChild(td9);
    row.appendChild(td7);
    row.appendChild(td8);

    //This is for removal of dropdown to make distinct select
    var j=0;
    for(j=1;j<validationPass.length;j++)
    {
        //alert("Inside After Check Main Iteration:::"+j);
        if(validationPass[j]==false)
        {
            /*//alert("Inside After Check Failed:::"+j);*/
            var previous = j;
            var profileId = "businessProfileId_" + previous;
            var isapplicable = "businessProfileIsApplicable_" + previous;

            document.getElementsByName(profileId)[0].disabled = false
            $(document.getElementsByName(profileId)[0]).css('background-color','#ffffff');
            document.getElementById(isapplicable).setAttribute("onclick","enable_text(this.checked,"+j+")");

            if($('select[name='+profileId+'] option:selected').val()!="")
            {
                for (var inner = 0; inner < optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[3].split(",").length; inner++)
                {
                    //alert("Inside After Check Sub Disable:::"+inner);
                    var singleOperation = optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[3].split(",")[inner];
                    var value1 = "businessProfileValue1_" + previous + "_" + (Number(inner) + 1);
                    var value2 = "businessProfileValue2_" + previous + "_" + (Number(inner) + 1);
                    var selectvalue1 = "selectBusinessProfileValue1_" + previous + "_" + (Number(inner) + 1);
                    var selectvalue2 = "selectBusinessProfileValue2_" + previous + "_" + (Number(inner) + 1);

                    if (document.getElementById(isapplicable).checked == true)
                    {
                        if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.FLAT_FILE.name()%>")
                        {
                            document.getElementsByName(value1)[0].readOnly = false
                            $(document.getElementsByName(value1)[0]).css('background-color', '#ffffff');
                        }
                        else if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.REGULAR_EXPRESSION.name()%>")
                        {

                        }
                        else
                        {
                            if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
                            {
                                document.getElementsByName(selectvalue1)[0].disabled = false;
                                $(document.getElementsByName(selectvalue1)[0]).css('background-color', '#ffffff');
                            }
                            document.getElementsByName(value1)[0].readOnly = false;
                            $(document.getElementsByName(value1)[0]).css('background-color', '#ffffff');
                            if (singleOperation.split(":")[1] == "BETWEEN")
                            {
                                if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
                                {
                                    document.getElementsByName(selectvalue2)[0].disabled = false;
                                    $(document.getElementsByName(selectvalue2)[0]).css('background-color', '#ffffff');
                                }
                                document.getElementsByName(value2)[0].readOnly = false;
                                $(document.getElementsByName(value2)[0]).css('background-color', '#ffffff');
                            }
                            else
                            {
                                if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
                                {
                                    document.getElementsByName(selectvalue2)[0].disabled = true;
                                    $(document.getElementsByName(selectvalue2)[0]).css('background-color', '#EBEBE4');
                                }
                                document.getElementsByName(value2)[0].readOnly = true;
                                $(document.getElementsByName(value2)[0]).css('background-color', '#EBEBE4');
                            }
                        }
                    }
                    else
                    {
                        if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.FLAT_FILE.name()%>")
                        {
                            document.getElementsByName(value1)[0].readOnly = true
                            $(document.getElementsByName(value1)[0]).css('background-color', '#EBEBE4');
                        }
                        else if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.REGULAR_EXPRESSION.name()%>")
                        {

                        }
                        else
                        {
                            if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
                            {
                                document.getElementsByName(selectvalue1)[0].disabled = true;
                                $(document.getElementsByName(selectvalue1)[0]).css('background-color', '#EBEBE4');
                                document.getElementsByName(selectvalue2)[0].disabled = true;
                                $(document.getElementsByName(selectvalue2)[0]).css('background-color', '#EBEBE4');
                            }
                            document.getElementsByName(value1)[0].readOnly = true;
                            $(document.getElementsByName(value1)[0]).css('background-color', '#EBEBE4');

                            document.getElementsByName(value2)[0].readOnly = true;
                            $(document.getElementsByName(value2)[0]).css('background-color', '#EBEBE4');
                        }
                    }
                }
            }
            validationFail=true;
            break;
        }
        else if(validationPass[j]=="" || validationPass[j]==null)
        {

        }
        else
        {
            var previous = j;
            var profileId = "businessProfileId_" + previous;
            var isapplicable = "businessProfileIsApplicable_" +previous;

            document.getElementsByName(profileId)[0].disabled = true;
            $(document.getElementsByName(profileId)[0]).css('background-color','#EBEBE4');
            document.getElementById(isapplicable).setAttribute("onclick","return false");

            for (var inner = 0; inner < optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[3].split(",").length; inner++)
            {
                //alert("Inside After Check Main Disable:::"+inner);
                var singleOperation = optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[3].split(",")[inner];
                var value1 = "businessProfileValue1_" + previous + "_" + (Number(inner) + 1);
                var value2 = "businessProfileValue2_" + previous + "_" + (Number(inner) + 1);
                var selectvalue1 = "selectBusinessProfileValue1_" + previous + "_" + (Number(inner) + 1);
                var selectvalue2 = "selectBusinessProfileValue2_" + previous + "_" + (Number(inner) + 1);

                if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.FLAT_FILE.name()%>")
                {
                    document.getElementsByName(value1)[0].readOnly = true
                    $(document.getElementsByName(value1)[0]).css('background-color', '#EBEBE4');
                }
                else if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.REGULAR_EXPRESSION.name()%>")
                {

                }
                else
                {
                    if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
                    {
                        document.getElementsByName(selectvalue1)[0].disabled = true;
                        $(document.getElementsByName(selectvalue1)[0]).css('background-color', '#EBEBE4');
                        document.getElementsByName(selectvalue2)[0].disabled = true;
                        $(document.getElementsByName(selectvalue2)[0]).css('background-color', '#EBEBE4');
                    }
                    document.getElementsByName(value1)[0].readOnly = true;
                    $(document.getElementsByName(value1)[0]).css('background-color', '#EBEBE4');

                    document.getElementsByName(value2)[0].readOnly = true;
                    $(document.getElementsByName(value2)[0]).css('background-color', '#EBEBE4');
                }

                document.getElementsByName(value2)[0].readOnly = true;
                $(document.getElementsByName(value2)[0]).css('background-color', '#EBEBE4');
                /*}*/
            }
        }
    }

    var k=0;
    for(k=1;k<optionStatus.length;k++)
    {
        if(optionStatus[k]=="Y")
        {
            row.getElementsByTagName("option")[k].style.display="none";//TODO temporary
        }
    }
    currentRowFlag=count;
    // append row to table
    if(validationFail==true )
    {
        alert("please fill the previous data");
    }
    else if(optionStatus.indexOf("N")==-1)
    {
        alert("All the rule has been configured");
    }
    else
    {
        tbody.appendChild(row);
        addedRow[count]="Y";
        currentRowStatus[count]="P"
        count = parseInt(count) + 1;
    }
}
function delRow(element,e)
{
    var current =  e.target;
    //here we will delete the line
    while ((current = current.parentElement) && current.tagName != "TR");
    current.parentElement.removeChild(current);

    currentRowStatus[element]="D";

    var profileId = "businessProfileId_" + currentRowFlag;
    var l=0;
    for(l=1;l<optionStatus.length;l++)
    {
        if(optionStatus[l]=="Y" )
        {
            if (option.indexOf(selectedOption[element]) == l)
            {
                (this.document.getElementsByName(profileId)[0].options[l]).style.display = "block";
            }
        }
    }
}
function setCountOfRows(element)
{
    this.document.getElementsByName("countOfRow")[0].value=count;
}
function editRow(element)   //updated
{
    var validationFail=false;
    var i=0;
    for (i = 1; i < addedRow.length; i++)
    {
        if (currentRowStatus[i] == "P")
        {
            var previous = i;
            var profileId = "businessProfileId_" + previous;
            var fileType = "fileType_" + previous;
            var isApplicable = "businessProfileIsApplicable_" + previous;

            if($('select[name='+profileId+'] option:selected').val()!="")
            {
                for (var inner = 0; inner <optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[3].split(",").length; inner++)
                {
                    //alert("Validation Check Iteration::::"+inner);
                    var singleOperation = optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[3].split(",")[inner];
                    var value1 = "businessProfileValue1_" + previous + "_" + (Number(inner) + 1);
                    var value2 = "businessProfileValue2_" + previous + "_" + (Number(inner) + 1);
                    var selectvalue1 = "selectBusinessProfileValue1_" + previous + "_" + (Number(inner) + 1);
                    var selectvalue2 = "selectBusinessProfileValue2_" + previous + "_" + (Number(inner) + 1);

                    if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.FLAT_FILE.name()%>")
                    {
                        //alert("Validation Check Is File Type");
                        if ((document.getElementsByName(value1) != null ) && ( (document.getElementById(isApplicable).checked == true && document.getElementsByName(value1)[0].value == "" )))
                        {
                            currentRowFlag = i;
                            validationFail = true;
                            break;
                        }
                        else
                        {
                            validationFail = false;
                        }
                    }
                    else if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.REGULAR_EXPRESSION.name()%>")
                    {
                        validationFail = false;
                    }
                    else
                    {
                        //alert("Validation Check Is Data Or Comparator");
                        if ((document.getElementsByName(value1) != null || document.getElementsByName(value2) != null) && ( (document.getElementById(isApplicable).checked == true && document.getElementsByName(value1)[0].value == "" || ( $('select[name=' + profileId + '] option:selected').val() != "" && singleOperation.split(":")[1] == "BETWEEN" && document.getElementsByName(value2)[0].value == "" && document.getElementById(isApplicable).checked == true )) || $('select[name=' + profileId + '] option:selected').val() == ""))
                        {
                            currentRowFlag = i;
                            validationFail = true;
                            break;
                        }
                        else
                        {
                            validationFail = false;
                        }
                    }
                }
            }
            else
            {
                currentRowFlag = i;
                validationFail = true;
                break;
            }

            if(validationFail==false)
            {
                document.getElementsByName(profileId)[0].disabled = true;
                $(document.getElementsByName(profileId)[0]).css('background-color', '#EBEBE4');
                document.getElementById(isApplicable).setAttribute("onclick", "return false");
                if ($('select[name='+profileId+'] option:selected').val()!="")
                {
                    for (var inner = 0; inner < optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[3].split(",").length; inner++)
                    {
                        //alert("Inside After Check Sub Disable:::"+inner);
                        var singleOperation = optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[3].split(",")[inner];
                        var value1 = "businessProfileValue1_" + previous + "_" + (Number(inner) + 1);
                        var value2 = "businessProfileValue2_" + previous + "_" + (Number(inner) + 1);
                        var selectvalue1 = "selectBusinessProfileValue1_" + previous + "_" + (Number(inner) + 1);
                        var selectvalue2 = "selectBusinessProfileValue2_" + previous + "_" + (Number(inner) + 1);

                        if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.FLAT_FILE.name()%>")
                        {
                            document.getElementsByName(value1)[0].readOnly = true
                            $(document.getElementsByName(value1)[0]).css('background-color', '#EBEBE4');
                        }
                        else if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.REGULAR_EXPRESSION.name()%>")
                        {

                        }
                        else
                        {
                            if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
                            {
                                document.getElementsByName(selectvalue1)[0].disabled = true;
                                $(document.getElementsByName(selectvalue1)[0]).css('background-color', '#EBEBE4');
                            }
                            document.getElementsByName(value1)[0].readOnly = true;
                            $(document.getElementsByName(value1)[0]).css('background-color', '#EBEBE4');
                            if (singleOperation.split(":")[1] == "BETWEEN")
                            {
                                document.getElementsByName(value2)[0].readOnly = true;
                                $(document.getElementsByName(value2)[0]).css('background-color', '#EBEBE4');
                                if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
                                {
                                    document.getElementsByName(selectvalue2)[0].disabled = true;
                                    $(document.getElementsByName(selectvalue2)[0]).css('background-color', '#EBEBE4');
                                }
                            }
                            else
                            {
                                if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
                                {
                                    document.getElementsByName(selectvalue2)[0].disabled = true;
                                    $(document.getElementsByName(selectvalue2)[0]).css('background-color', '#EBEBE4');
                                }
                                document.getElementsByName(value2)[0].readOnly = true;
                                $(document.getElementsByName(value2)[0]).css('background-color', '#EBEBE4');
                            }
                        }

                    }
                }
            }
            else
            {
                currentRowFlag = i;
                validationFail = true;

                document.getElementsByName(profileId)[0].disabled = false;
                $(document.getElementsByName(profileId)[0]).css('background-color', '#ffffff');
                document.getElementById(isApplicable).setAttribute("onclick", "enable_text(this.checked,"+previous+")");
                if ($('select[name='+profileId+'] option:selected').val()!="")
                {
                    for (var inner = 0; inner < optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[3].split(",").length; inner++)
                    {
                        //alert("Inside After Check Sub Disable:::"+inner);
                        var singleOperation = optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[3].split(",")[inner];
                        var value1 = "businessProfileValue1_" + previous + "_" + (Number(inner) + 1);
                        var value2 = "businessProfileValue2_" + previous + "_" + (Number(inner) + 1);
                        var selectvalue1 = "selectBusinessProfileValue1_" + previous + "_" + (Number(inner) + 1);
                        var selectvalue2 = "selectBusinessProfileValue2_" + previous + "_" + (Number(inner) + 1);

                        if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.FLAT_FILE.name()%>")
                        {
                            document.getElementsByName(value1)[0].readOnly = false
                            $(document.getElementsByName(value1)[0]).css('background-color', '#ffffff');
                        }
                        else if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.REGULAR_EXPRESSION.name()%>")
                        {

                        }
                        else
                        {
                            if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
                            {
                                document.getElementsByName(selectvalue1)[0].disabled = false;
                                $(document.getElementsByName(selectvalue1)[0]).css('background-color', '#ffffff');
                            }
                            document.getElementsByName(value1)[0].readOnly = false;
                            $(document.getElementsByName(value1)[0]).css('background-color', '#ffffff');
                            if (singleOperation.split(":")[1] == "BETWEEN")
                            {
                                document.getElementsByName(value2)[0].readOnly = false;
                                $(document.getElementsByName(value2)[0]).css('background-color', '#ffffff');
                                if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
                                {
                                    document.getElementsByName(selectvalue2)[0].disabled = false;
                                    $(document.getElementsByName(selectvalue2)[0]).css('background-color', '#ffffff');
                                }
                            }
                            else
                            {
                                if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
                                {
                                    document.getElementsByName(selectvalue2)[0].disabled = true;
                                    $(document.getElementsByName(selectvalue2)[0]).css('background-color', '#EBEBE4');
                                }
                                document.getElementsByName(value2)[0].readOnly = true;
                                $(document.getElementsByName(value2)[0]).css('background-color', '#EBEBE4');
                            }
                        }
                    }
                }

                break;
            }

            if(option.indexOf(selectedOption[i])>=0)
            {

                var index=option.indexOf(selectedOption[i]);

                optionStatus[index]="Y";
            }

        }
        else if(currentRowStatus[i]=="D")
        {

            var index=option.indexOf(selectedOption[i]);

            optionStatus[index]="N";

            selectedOption[i]="";

        }
    }
    if(validationFail==true)
    {
        alert("please fill in the previous data");
    }
    else
    {
        currentRowFlag=element;
        var profileId = "businessProfileId_" + element;
        var fileType = "fileType_" + element;
        var isapplicable = "businessProfileIsApplicable_" +element;
        document.getElementsByName(profileId)[0].disabled = false;
        $(document.getElementsByName(profileId)[0]).css('background-color', '#ffffff');
        document.getElementById(isapplicable).setAttribute("onclick","enable_text(this.checked,"+element+")");

        var l=0;
        for(l=1;l<optionStatus.length;l++)
        {
            if(optionStatus[l]=="Y" )
            {
                if(option.indexOf(selectedOption[element])==l)
                {
                    (this.document.getElementsByName(profileId)[0].options[l]).style.display="block";//TODO temporary
                    optionStatus[(option.indexOf(selectedOption[element]))]="N";
                }
                else
                {
                    (this.document.getElementsByName(profileId)[0].options[l]).style.display = "none";//TODO temporary
                }
            }
            else if(optionStatus[l]=="N")
            {
                (this.document.getElementsByName(profileId)[0].options[l]).style.display="block";//TODO temporary
            }
        }
        for (var inner = 0; inner < optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[3].split(",").length; inner++)
        {
            //alert("Inside After Check Sub Disable:::"+inner);
            var singleOperation = optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[3].split(",")[inner];
            var value1 = "businessProfileValue1_" + element + "_" + (Number(inner) + 1);
            var value2 = "businessProfileValue2_" + element + "_" + (Number(inner) + 1);
            var selectvalue1 = "selectBusinessProfileValue1_" + element + "_" + (Number(inner) + 1);
            var selectvalue2 = "selectBusinessProfileValue2_" + element + "_" + (Number(inner) + 1);
            if(document.getElementById(isapplicable).checked == true)
            {
                if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.FLAT_FILE.name()%>")
                {
                    document.getElementsByName(value1)[0].readOnly = false
                    $(document.getElementsByName(value1)[0]).css('background-color', '#ffffff');
                }
                else if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.REGULAR_EXPRESSION.name()%>")
                {

                }
                else
                {
                    if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
                    {
                        document.getElementsByName(selectvalue1)[0].disabled = false;
                        $(document.getElementsByName(selectvalue1)[0]).css('background-color', '#ffffff');
                        document.getElementsByName(selectvalue2)[0].disabled = false;
                        $(document.getElementsByName(selectvalue2)[0]).css('background-color', '#ffffff');
                    }
                    document.getElementsByName(value1)[0].readOnly = false;
                    $(document.getElementsByName(value1)[0]).css('background-color', '#ffffff');
                    if (singleOperation.split(":")[1] == "BETWEEN")
                    {
                        if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
                        {
                            document.getElementsByName(selectvalue2)[0].disabled = false;
                            $(document.getElementsByName(selectvalue2)[0]).css('background-color', '#ffffff');
                        }
                        document.getElementsByName(value2)[0].readOnly = false;
                        $(document.getElementsByName(value2)[0]).css('background-color', '#ffffff');
                    }
                    else
                    {
                        if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
                        {
                            document.getElementsByName(selectvalue2)[0].disabled = true;
                            $(document.getElementsByName(selectvalue2)[0]).css('background-color', '#EBEBE4');
                        }
                        document.getElementsByName(value2)[0].readOnly = true;
                        $(document.getElementsByName(value2)[0]).css('background-color', '#EBEBE4');
                    }
                }
            }
            else
            {
                if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.FLAT_FILE.name()%>")
                {
                    document.getElementsByName(value1)[0].readOnly = true
                    $(document.getElementsByName(value1)[0]).css('background-color', '#EBEBE4');
                }
                else if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.REGULAR_EXPRESSION.name()%>")
                {

                }
                else
                {
                    if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
                    {
                        document.getElementsByName(selectvalue1)[0].disabled = true;
                        $(document.getElementsByName(selectvalue1)[0]).css('background-color', '#EBEBE4');
                        document.getElementsByName(selectvalue2)[0].disabled = true;
                        $(document.getElementsByName(selectvalue2)[0]).css('background-color', '#EBEBE4');
                    }
                    document.getElementsByName(value1)[0].readOnly = true;
                    $(document.getElementsByName(value1)[0]).css('background-color', '#EBEBE4');

                    document.getElementsByName(value2)[0].readOnly = true;
                    $(document.getElementsByName(value2)[0]).css('background-color', '#EBEBE4');
                }
            }
        }

    }
}
function isNumberKey(evt)  //updated
{
    var charCode = (evt.which) ? evt.which : event.keyCode
    if (charCode > 31 && (charCode < 48 || charCode > 57))
        return false;

    return true;
}
function enable_text(status,name)
{
    var profileId = "businessProfileId_" + name;

    if ($('select[name=' + profileId + '] option:selected').val() != "")
    {
        for (var inner = 0; inner < optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[3].split(",").length; inner++)
        {
            //alert("Inside After Check Sub Disable:::"+inner);
            var singleOperation = optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[3].split(",")[inner];
            var value1 = "businessProfileValue1_" + name + "_" + (Number(inner) + 1);
            var value2 = "businessProfileValue2_" + name + "_" + (Number(inner) + 1);
            var selectvalue1 = "selectBusinessProfileValue1_" + name+ "_" + (Number(inner) + 1);
            var selectvalue2 = "selectBusinessProfileValue2_" + name + "_" + (Number(inner) + 1);

            if(status==true)
            {
                if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.FLAT_FILE.name()%>")
                {
                    document.getElementsByName(value1)[0].readOnly = false
                    $(document.getElementsByName(value1)[0]).css('background-color', '#ffffff');
                }
                else if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.REGULAR_EXPRESSION.name()%>")
                {

                }
                else
                {
                    if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
                    {
                        document.getElementsByName(selectvalue1)[0].disabled = false;
                        $(document.getElementsByName(selectvalue1)[0]).css('background-color', '#ffffff');
                    }

                    document.getElementsByName(value1)[0].readOnly = false;
                    $(document.getElementsByName(value1)[0]).css('background-color', '#ffffff');
                    if (singleOperation.split(":")[1] == "BETWEEN")
                    {
                        if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
                        {
                            document.getElementsByName(selectvalue2)[0].disabled = false;
                            $(document.getElementsByName(selectvalue2)[0]).css('background-color', '#ffffff');
                        }
                        document.getElementsByName(value2)[0].readOnly = false;
                        $(document.getElementsByName(value2)[0]).css('background-color', '#ffffff');
                    }
                    else
                    {
                        document.getElementsByName(value2)[0].readOnly = true;
                        $(document.getElementsByName(value2)[0]).css('background-color', '#EBEBE4');
                    }
                }
            }
            else
            {
                if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.FLAT_FILE.name()%>")
                {
                    document.getElementsByName(value1)[0].readOnly = true
                    $(document.getElementsByName(value1)[0]).css('background-color', '#EBEBE4');
                }
                else if (optionValue[option.indexOf($('select[name=' + profileId + '] option:selected').attr("id"))].split("|")[2] == "<%=RuleTypeEnum.REGULAR_EXPRESSION.name()%>")
                {

                }
                else
                {
                    if(singleOperation.split(":")[4]=="<%=DataType.ENUM.name()%>")
                    {
                        document.getElementsByName(selectvalue1)[0].disabled = true;
                        $(document.getElementsByName(selectvalue1)[0]).css('background-color', '#EBEBE4');
                        document.getElementsByName(selectvalue2)[0].disabled = true;
                        $(document.getElementsByName(selectvalue2)[0]).css('background-color', '#EBEBE4');
                    }
                    document.getElementsByName(value1)[0].readOnly = true;
                    $(document.getElementsByName(value1)[0]).css('background-color', '#EBEBE4');

                    document.getElementsByName(value2)[0].readOnly = true;
                    $(document.getElementsByName(value2)[0]).css('background-color', '#EBEBE4');
                }
            }
        }
    }
}
function cancel(ctoken) {
    document.myForm.action="/partner/net/BusinessProfileList?ctoken="+ctoken;
}
