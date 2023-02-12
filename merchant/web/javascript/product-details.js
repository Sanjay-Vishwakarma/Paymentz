/**
 * Created by Admin on 6/16/2017.
 */

$(document).ready(function ()
{
    var counter = '';

    if($('#productCounter').val() != 0)
    {
        counter = $('#productCounter').val();
        counter++;
    }
    else{
        counter = 1;
    }
    /*var div = '';
    var prodCount = $("#productList").val().size();
    alert("size----"+prodCount);

    if(prodCount == 0)
    {
        div = '<div class="widget-content padding"><div id="horizontal-form1"><div class="form-group col-md-4 has-feedback"><label>Product Description</label></div><div class="form-group col-md-2 has-feedback"><label>Product Amount</label></div><div class="form-group col-md-2 has-feedback"><label>Unit</label></div><div class="form-group col-md-3 has-feedback"><label>Tax</label></div></div></div>';
    }
    else
    {
        counter = prodCount;
        alert("count----"+counter);
    }*/

    $("#addrow").on("click", function ()
    {
        var dUnit = $("#unitList").val().replace("[","").replace("]","");
        var splitVals = dUnit.split(',');
        var el = '';
        var isSelected = '';
        //alert("---"+splitVals.length);

        el = '<option value="" selected size="10" placeholder="Unit" >Select Unit</option>';
        //el += '<option value="'+opt+'" selected size="10" placeholder="Unit" >'+opt+'</option>';
        for(var i = 0; i < splitVals.length; i++) {
            var opt = splitVals[i];
            if(splitVals == opt)
            {
                isSelected = 'selected';
            }
            el += '<option value="'+opt+'"  size="10" placeholder="Unit" "'+isSelected+'">'+opt+'</option>';
            //alert("lang dropdown---"+el)
        }


        //var div = '<div class="widget-content padding"><div id="horizontal-form1"><div class="form-group col-md-4 has-feedback"><label>Product Description</label></div><div class="form-group col-md-2 has-feedback"><label>Product Amount</label></div><div class="form-group col-md-2 has-feedback"><label>Unit</label></div><div class="form-group col-md-3 has-feedback"><label>Tax</label></div></div></div>';
        var div = '<div class="widget-content padding" id="topheader"><div id="horizontal-form1"><div class="form-group col-md-4 has-feedback"><label>Product Description</label></div><div class="form-group col-md-2 has-feedback"><label>Unit</label></div><div class="form-group col-md-3 has-feedback"><label>Tax</label></div></div></div>';


        var cols = "";
        cols += '<div class="form2"><div class="form-group col-md-4 has-feedback"><input type="text" size="10" placeholder="Order Description" class="form-control" name="productDescription' + counter + '" ></div>';
        //cols += '<div class="form-group col-md-2 has-feedback"><input type="text" size="10" placeholder="Order Amount" class="form-control" onkeypress="return isNumber(event)" name="productAmount' + counter + '"></div>';
        cols += '<div class="form-group col-md-2 has-feedback"><select id="unit" class="form-control" name="productunit'+counter + '" >'+el+'</select></div>';
        cols += '<div class="form-group col-md-3 has-feedback"><input type="text" maxlength="4" size="10" placeholder="Tax" class="form-control" name="tax' + counter + '"></div>';
        cols += '<div class="form-group col-md-1 has-feedback"><input type="button" class="btn btn-default" name="delete" value="Delete" size="" placeholder=""></div></div>';
        //cols += '<td><a class="deleteRow"> x </a></td>';
        //newRow.append(cols);

        if (counter == 1)
        {
            $("#horizontal-form1").append(div);
        }

        $("#horizontal-form1").append(cols);

        $('#productCounter').val(counter);
        counter++;
    });


    $("#horizontal-form1").on("click", 'input[name^="delete"]', function (event)
    {
        if(counter==2)
        {
            $('#topheader').remove();
        }

        $(this).closest('.form2').remove();

        counter--;
    });


    function isNumber(evt)
    {
        evt = (evt) ? evt : window.event;
        var charCode = (evt.which) ? evt.which : evt.keyCode;
        if (charCode > 31 && (charCode < 48 || charCode > 57 || charCode == 46))
        {
            return false;
        }
        return true;
    }


    function per(num, amount)
    {
        var per = num * amount / 100;
        return +parseFloat(per) + +num;
    }
});
