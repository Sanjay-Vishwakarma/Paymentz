/**
 * Created by Admin on 6/16/2017.
 */

$(document).ready(function () {



    var counter = '';

    if($('#productCounter').val() != 0)
    {
        counter = $('#productCounter').val();
        counter++;
    }
    else{
        counter = 1;
    }

    $("#addrow").on("click", function ()
    {
        var dUnit = $("#unitList").val().replace("[","").replace("]","");
        var splitVals = dUnit.split(',');

        var el = '';

        el = '<option value="" selected size="10" placeholder="Unit" >Select Unit</option>';

        for(var i = 0; i < splitVals.length; i++) {
            var opt = splitVals[i];

            el += '<option value="'+opt+'" size="10" placeholder="Unit">'+opt+'</option>';
            //alert("lang dropdown---"+el)
        }

        var div = '<div class="widget-content padding" id="topheader"><div id="horizontal-form1">' +
                '<div class="form-group col-md-2 has-feedback"><label>Product&nbsp;Description</label></div>' +
                '<div class="form-group col-md-2 has-feedback"><label>Unit</label></div>' +
                '<div class="form-group col-md-2 has-feedback"><label>Product&nbsp;Amount</label></div>' +
                '<div class="form-group col-md-2 has-feedback"><label>Quantity</label></div>' +
                '<div class="form-group col-md-1 has-feedback"><label>Tax(%)</label></div>' +
                '<div class="form-group col-md-2 has-feedback"><label>Total</label></div>' +
                '</div></div>';

        //var newRow = $("<div class=form-group col-md-3 has-feedback>");
        var cols = "";
        cols += '<div class="form1"><div class="form-group col-md-2 has-feedback"><input type="text" size="10" placeholder="Product Description" class="form-control" name="product'+counter+'" ></div>';
        cols += '<div class="form-group col-md-2 has-feedback"><select id="unit" class="form-control" name="productunit'+counter + '" >'+el+'</select></div>';
        cols += '<div class="form-group col-md-2 has-feedback"><input type="text" size="10"  placeholder="Product Amount" class="form-control" onkeyup="checkDec(this,event);" name="price'+counter+'"></div>';
        cols += '<div class="form-group col-md-2 has-feedback"><input type="text" size="10" placeholder="Quantity" class="form-control" onkeypress="return isNumber(event)" name="qty'+counter+'"></div>';
        cols += '<div class="form-group col-md-1 has-feedback"><input type="text" size="10" maxlength="4" placeholder="Tax" class="form-control" onkeyup="checkDec(this,event);" name="tax'+counter+'"></div>';
        cols += '<div class="form-group col-md-2 has-feedback"><input type="text" readonly="readonly"  size="10" placeholder="Total" class="form-control" name="linetotal'+counter+'"></div>';
        cols += '<div class="form-group col-md-1 has-feedback"><input type="button" class="btn btn-default" name="delete" value="Delete" size="" placeholder=""></div></div>';
        //cols += '<td><a class="deleteRow"> x </a></td>';
        //newRow.append(cols);

        var amt = '<div class="invoice-address" style="float: right;width: 100%;margin-bottom: 10px;">' +
                '<table style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;border: 0px">' +
                '<tbody>' +
                '<tr>' +
                '<label class="thcheckboxnew" id="dProList">'+
                '<input type="checkbox" id="thcheckboxnew" name="addtax">&nbsp;&nbsp;&nbsp;Apply GST/VAT on Total Amount?</label>'+
                '</tr>'+
                '<tr><td width="200">Tax Amount :</td><td class="text-right"><input type="text" style="margin-left: -42px;width: 84%;" class="form-control" id="tax" class="tax" name="taxamount" readonly="readonly" size="10" placeholder="0.00"></td></tr>' +
                '<tr><td>&nbsp;</td></tr><tr><td>Grand Total:</td><td class="text-right"><input type="text" class="form-control" style="margin-left: -42px;width: 84%;" id="grandtotal" class="grandtotal" name="grandtotal" readonly="readonly" size="10" placeholder="Grand Total"></td></tr>' +
                '</tbody></table></div>';

        if(counter==1)
        {
            $("#horizontal-form1").append(div);
        }

        $("#horizontal-form1").append(cols);

        if($('#productCounter').val() == 0 || counter==1)
        {
            $("#horizontal-form2").append(amt);
        }
        $('#productCounter').val(counter);
        counter++;


    });



    //alert("grandtotal---"+$('#grandtotal').val())


    $("#horizontal-form1").on("change", 'input[name^="price"], input[name^="qty"], input[name^="tax"]', function (event) {

        calculateRow($(this).closest('.form1'));
        calculateGrandTotal(counter);
    });

    $("#horizontal-form1").on("click", 'input[name^="delete"]', function (event) {
        //$(this).closest('.invoice-address').remove();
        if(counter==2)
        {
            $('.invoice-address').remove();
            $('#topheader').remove();
        }

        $(this).closest('.form1').remove();
        counter--;
        calculateGrandTotal(counter);
    });
});



function calculateRow(row) {

    var price = +row.find('input[name^="price"]').val();
    var taxTotal = 0;
    var qty = +row.find('input[name^="qty"]').val();
    var tax = +row.find('input[name^="tax"]').val();
    var total = (price * qty).toFixed(2);
    var taxCal = ((total*tax)/100).toFixed(2);
    //var tLineTotal = 0;
    var tLineTotal = eval(total) + eval(taxCal);

    row.find('input[name^="linetotal"]').val(tLineTotal.toFixed(2));
}

function calculateGrandTotal(count) {

    var gst = $("#gstcb").val();
    var grandTotal = 0;
    var gstTotal = 0;

    $(".form1").find('input[name^="linetotal"]').each(function () {
        grandTotal += +$(this).val();
    });
    $("#grandtotal").val(grandTotal.toFixed(2));
    $("#amount").val(grandTotal.toFixed(2));
    $("#tax").val("0.00");

    if(count>1)
    {
        document.getElementById("amount").readOnly = true;
    }
    else
    {
        document.getElementById("amount").readOnly = false;
    }

    //$("input[name='addtax']").next('.iCheck-helper').on("click", function() {
    $("#thcheckboxnew").on("click", function (){
        //alert("inside checked---"+gst)
        if($('input[name^="addtax"]').is(":checked") && gst>0)
        {
            var per = (grandTotal*gst)/100;
            gstTotal =grandTotal+per;

            $("#grandtotal").val(gstTotal.toFixed(2));
            $("#amount").val(gstTotal.toFixed(2));
            $("#tax").val(per.toFixed(2));
            document.getElementById("amount").readOnly = true;
        }
        else {
            $("#grandtotal").val(grandTotal.toFixed(2));
            $("#amount").val(grandTotal.toFixed(2));
            $("#tax").val("0.00");

            document.getElementById("amount").readOnly = true;
        }
    });

    /*if(gst!="0.00")
    {
        var per = (grandTotal*gst)/100;
        gstTotal +=grandTotal+per;

        $("#grandtotal").val(gstTotal.toFixed(2));
        $("#amount").val(gstTotal.toFixed(2));
        $("#tax").val(per.toFixed(2));
        document.getElementById("amount").readOnly = true;
    }
    else
    {
        $("#grandtotal").val(grandTotal.toFixed(2));
        $("#amount").val(grandTotal.toFixed(2));
        $("#tax").val("0.00");

        document.getElementById("amount").readOnly = true;
    }*/
}

function isNumber(evt) {
    evt = (evt) ? evt : window.event;
    var charCode = (evt.which) ? evt.which : evt.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57 || charCode == 46)) {
        return false;
    }
    return true;
}

function checkDec(el,evt){
    evt = (evt) ? evt : window.event;
    var charCode = (evt.which) ? evt.which : evt.keyCode;

    var ex = /^[0-9]+\.?[0-9]*$/;
    if(ex.test(el.value)==false){
        //el.value = el.value.substring(0,el.value.length - 1);
        el.value = "";
    }

    if (charCode > 31 && (charCode < 48 || charCode > 57))
    {
        evt.preventDefault();
    }

    return true;
}

function isNumberKey(evt)
{
    var charCode = (evt.which) ? evt.which : evt.keyCode;
    if (charCode != 46 && charCode > 31
            && (charCode < 48 || charCode > 57))
        return false;

    return true;
}

function replaceAmountwithGrandTotal() {


    var grandTotal = document.getElementById("grandtotal").value;

    if(grandTotal)
    {
        document.getElementById("amount").value = grandTotal;
        document.getElementById("amount").readOnly = true;
    }

}

function per(num, amount){
    var per = num*amount/100;
    return +parseFloat(per) + +num;
}
