/**
 * Created by admin on 31-08-2017.
 */
/**
 * Created by Admin on 6/16/2017.
 */
$(document).ready(function () {

    var counter = '';
    if($('#unitCounter').val() != 0)
    {
        counter = $('#unitCounter').val();
        counter++;
    }
    else{
        counter = 1;
    }



    $("#addrow1").on("click", function ()
    {

        var cols = "";
        //cols += '<div class="form3"><div class="form-group col-md-2 has-feedback"></div>';
        //cols += '<div class="form-group col-md-1 has-feedback"><input type="button" class="btn btn-default" name="delete" value="Delete" size="" ></div></div>';


        cols += '<div class="form3"><div class="form-group col-md-4 has-feedback"><input type="text" size="10" placeholder="Unit" class="form-control" name="defaultunit'+counter+'"></div>';
        cols += '<div class="form-group col-md-2 has-feedback"><input type="button" class="btn btn-default" name="delete" value="Delete" size=""></div></div>';

        $("#horizontal-form3").append(cols);

        $('#unitCounter').val(counter);

        counter++;
    });

    $("#horizontal-form3").on("click", 'input[name^="delete"]', function (event) {
        $(this).closest('.form3').remove();

    });
});




function isNumber(evt) {
    evt = (evt) ? evt : window.event;
    var charCode = (evt.which) ? evt.which : evt.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57 || charCode == 46)) {
        return false;
    }
    return true;
}



