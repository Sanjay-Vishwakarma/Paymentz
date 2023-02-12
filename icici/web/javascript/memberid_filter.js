/**
 * Created by Admin on 12/26/2016.
 */


$(function(){
    $('#groups').on('change', function(){
        var val = $(this).val();
        var sub = $('#sub_groups');
        var val2 = $('#currency').val();
        var val3 = $('#sub_groups').val();
        var member = $('#member_groups');
        var curr = $('#currency');

      /*  alert('gateway change');*/
       // alert("curr---"+curr.val(0))
       // alert("member---"+member.val(0))
        if(val == '--All--')//gateway all
        {
            if(val2 == '--All--')//currency all
            {
                //$('#sub_groups').find('option').show();
                if(val3 == '0')//accountid all(0)
                {
                    $('#sub_groups').find('option').show();
                    $('#member_groups').find('option').show();
                }
                else
                {

                    member.find('option').not(':first').hide();
                    $('option', member).filter(function(){
                        if($(this).attr('data-accid') == val3){
                            $(this).show();
                        }
                    });
                }
            }
            else
            {
                if(val3 == '0')
                {
                    //$('#sub_groups').find('option').show();

                    sub.find('option').not(':first').hide();
                    $('option', sub).filter(function(){
                        if($(this).attr('data-curr') == val2){
                            //alert()
                            $(this).show();
                        }
                    });
                    member.find('option').not(':first').hide();
                    $('option', member).filter(function(){
                        if($(this).attr('data-curr') == val2){
                            //alert()
                            $(this).show();
                        }
                    });
                }
                else
                {
                    $('#sub_groups').find('option').show();
                    member.find('option').not(':first').hide();
                    $('option', member).filter(function(){
                        if($(this).attr('data-curr') == val2 && $(this).attr('data-accid') == val3){
                            //alert()
                            $(this).show();
                        }
                    });
                }
            }
        }
        else
        {


            if(val2 == '--All--')//currency all and Accountid all
            {
                sub.find('option').not(':first').hide();
                $('option', sub).filter(function(){
                    if($(this).attr('data-group') == val){
                        $(this).show();
                    }
                });
                member.find('option').not(':first').hide();
                $('option', member).filter(function(){
                    if($(this).attr('data-group') == val){
                        $(this).show();
                    }
                });
            }
            else {

                if(val2 == '--All--' && val3 != '0')
                {
                    sub.find('option').not(':first').hide();
                    $('option', sub).filter(function(){
                        if($(this).attr('data-group') == val && $(this).attr('data-accid') == val3){
                            $(this).show();
                        }
                    });
                    member.find('option').not(':first').hide();
                    $('option', member).filter(function(){
                        if($(this).attr('data-group') == val && $(this).attr('data-accid') == val3){
                            $(this).show();
                        }
                    });
                }

                else if(val2 != '--All--' && val3 == '0')
                {
                    sub.find('option').not(':first').hide();
                    $('option', sub).filter(function(){
                        if($(this).attr('data-group') == val && $(this).attr('data-curr') == val2){
                            $(this).show();
                        }
                    });
                    member.find('option').not(':first').hide();
                    $('option', member).filter(function(){
                        if($(this).attr('data-group') == val && $(this).attr('data-curr') == val2){
                            $(this).show();
                        }
                    });
                }
                else
                {

                    sub.find('option').not(':first').hide();
                    $('option', sub).filter(function ()
                    {
                        if ($(this).attr('data-group') == val && $(this).attr('data-curr') == val2)
                        {
                            $(this).show();
                        }
                    });
                    member.find('option').not(':first').hide();
                    $('option', member).filter(function ()
                    {
                        if ($(this).attr('data-group') == val && $(this).attr('data-curr') == val2 && $(this).attr('data-accid') == val3)
                        {
                            $(this).show();
                        }
                    });

                }
            }

        }
        sub.val(0);
        member.val(0);
        //curr.val(0);
    });
});


$(function(){
    $('#currency').on('change', function(){
        var val = $(this).val();
        var sub = $('#sub_groups');
        var val2 = $('#groups').val();
        var val3 = $('#sub_groups').val();
        var member = $('#member_groups');
        var curr = $('#currency');

        if(val == '--All--')//currency all
        {
            if(val2 == '--All--')//gateway all
            {
                //$('#sub_groups').find('option').show();
                if(val3 == '0')
                {

                    $('#sub_groups').find('option').show();
                    $('#member_groups').find('option').show();
                }
                else
                {
                    member.find('option').not(':first').hide();
                    $('option', member).filter(function(){
                        if($(this).attr('data-accid') == val3){
                            $(this).show();
                        }
                    });
                }
            }
            else
            {
                if(val3 == '0')
                {
                    sub.find('option').not(':first').hide();
                    $('option', sub).filter(function(){
                        if($(this).attr('data-group') == val2){
                            $(this).show();
                        }
                    });
                    member.find('option').not(':first').hide();
                    $('option', member).filter(function(){
                        if($(this).attr('data-group') == val2){
                            $(this).show();
                        }
                    });
                }
                else
                {
                    sub.find('option').not(':first').hide();
                    $('option', sub).filter(function(){
                        if($(this).attr('data-group') == val2){
                            $(this).show();
                        }
                    });
                    member.find('option').not(':first').hide();
                    $('option', member).filter(function(){
                        if($(this).attr('data-group') == val2 && $(this).attr('data-accid') == val3){
                            $(this).show();
                        }
                    });
                }
            }
        }
        else {
            if(val2 == '--All--' && val3=='0')
            {
                sub.find('option').not(':first').hide();
                $('option', sub).filter(function(){
                    if($(this).attr('data-curr') == val){
                        $(this).show();
                    }
                });
                member.find('option').not(':first').hide();
                $('option', member).filter(function(){
                    if($(this).attr('data-curr') == val){
                        $(this).show();
                    }
                });
            }
            else
            {
                if(val2 == '--All--' && val3!='0')
                {
                    sub.find('option').not(':first').hide();
                    $('option', sub).filter(function(){
                        if($(this).attr('data-curr') == val){
                            $(this).show();
                        }
                    });
                    member.find('option').not(':first').hide();
                    $('option', member).filter(function(){
                        if($(this).attr('data-curr') == val && $(this).attr('data-accid') == val3){
                            $(this).show();
                        }
                    });
                }
                else if(val2 != '--All--' && val3=='0')
                {
                    sub.find('option').not(':first').hide();
                    $('option', sub).filter(function(){
                        if($(this).attr('data-curr') == val && $(this).attr('data-group') == val2){
                            $(this).show();
                        }
                    });
                    member.find('option').not(':first').hide();
                    $('option', member).filter(function(){
                        if($(this).attr('data-curr') == val && $(this).attr('data-group') == val2){
                            $(this).show();
                        }
                    });
                }
                else
                {
                    sub.find('option').not(':first').hide();
                    $('option', sub).filter(function(){
                        if($(this).attr('data-curr') == val && $(this).attr('data-group') == val2){
                            $(this).show();
                        }
                    });
                    member.find('option').not(':first').hide();
                    $('option', member).filter(function(){
                        if($(this).attr('data-curr') == val && $(this).attr('data-group') == val2 && $(this).attr('data-accid') == val3){
                            $(this).show();
                        }
                    });
                }

            }

        }
        sub.val(0);
        member.val(0);
        //curr.val(0);
    });

});

//new code
$(function(){
    $('#sub_groups').on('change', function(){

        var val = $(this).val();
        var sub = $('#sub_groups');
        var val2 = $('#groups').val();
        var val3 = $('#currency').val();
        var member = $('#member_groups');

        if(val == '0')//accountid all
        {
            if(val2 == '--All--')
            {
                if(val3 == '--All--')
                {
                    $('#member_groups').find('option').show();
                }
                else
                {
                    member.find('option').not(':first').hide();
                    $('option', member).filter(function(){
                        if($(this).attr('data-curr') == val3){
                            $(this).show();
                        }
                    });
                }
            }
            else
            {
                if(val3 == '--All--')
                {
                    member.find('option').not(':first').hide();
                    $('option', member).filter(function(){
                        if($(this).attr('data-group') == val2){
                            $(this).show();
                        }
                    });
                }
                else
                {
                    sub.find('option').not(':first').hide();
                    $('option', sub).filter(function(){
                        if($(this).attr('data-group') == val2 && $(this).attr('data-curr') == val3){
                            $(this).show();
                        }
                    });
                    member.find('option').not(':first').hide();
                    $('option', member).filter(function(){
                        if($(this).attr('data-group') == val2 && $(this).attr('data-curr') == val3){
                            $(this).show();
                        }
                    });
                }
            }
        }
        else
        {
            if(val2 == '--All--' && val3 == '--All--')//gateway and currency all
            {
                member.find('option').not(':first').hide();
                $('option', member).filter(function(){
                    if($(this).attr('data-accid') == val){
                        $(this).show();
                    }
                });
            }
            else if(val2 == '--All--' && val3 != '--All--')//gateway all and currency selected
            {
                sub.find('option').not(':first').hide();
                $('option', sub).filter(function(){
                    if($(this).attr('data-curr') == val3){
                        $(this).show();
                    }
                });
                member.find('option').not(':first').hide();
                $('option', member).filter(function(){
                    if($(this).attr('data-curr') == val3 && $(this).attr('data-accid') == val){
                        $(this).show();
                    }
                });
            }
            else if(val2 != '--All--' && val3 == '--All--')//gateway selected and currency all
            {
                sub.find('option').not(':first').hide();
                $('option', sub).filter(function(){
                    if($(this).attr('data-group') == val2){
                        $(this).show();
                    }
                });
                member.find('option').not(':first').hide();
                $('option', member).filter(function(){
                    if($(this).attr('data-group') == val2 && $(this).attr('data-accid') == val){
                        $(this).show();
                    }
                });
            }
            else
            {
                sub.find('option').not(':first').hide();
                $('option', sub).filter(function(){
                    if($(this).attr('data-group') == val2 && $(this).attr('data-curr') == val3){
                        $(this).show();
                    }
                });
                member.find('option').not(':first').hide();
                $('option', member).filter(function(){
                    if($(this).attr('data-group') == val2 && $(this).attr('data-curr') == val3 && $(this).attr('data-accid') == val){
                        $(this).show();
                    }
                });
            }

        }
    });

});



//NEW ONLOAD FUNCTION

$(function(){

    var val = $('#groups').val();//bank
    var sub = $('#sub_groups');
    var val2 = $('#currency').val();//currency
    var val3 = $('#sub_groups').val();//accid
    var member = $('#member_groups');
    var curr = $('#currency');
    var bank = $('#groups');

    //var subval = $('#sub_groups');


    if(val == '--All--')//gateway all
    {
        if(val2 == '--All--')//currency all
        {
            //$('#sub_groups').find('option').show();
            if(val3 == '0')//accountid all(0)
            {
                $('#sub_groups').find('option').show();
                $('#member_groups').find('option').show();
            }
            else
            {

                member.find('option').not(':first').hide();
                $('option', member).filter(function(){
                    if($(this).attr('data-accid') == val3){
                        $(this).show();
                    }
                });
            }
        }
        else
        {
            if(val3 == '0')
            {
                $('#sub_groups').find('option').show();
                member.find('option').not(':first').hide();
                $('option', member).filter(function(){
                    if($(this).attr('data-curr') == val2){
                        //alert()
                        $(this).show();
                    }
                });
            }
            else
            {
                sub.find('option').not(':first').hide();
                $('option', sub).filter(function(){
                    if($(this).attr('data-curr') == val2){
                        //alert()
                        $(this).show();
                    }
                });
                member.find('option').not(':first').hide();
                $('option', member).filter(function(){
                    if($(this).attr('data-curr') == val2 && $(this).attr('data-accid') == val3){
                        //alert()
                        $(this).show();
                    }
                });
            }
        }
    }
    else
    {

        if(val2 == '--All--' && val3 == '0')//currency all and Accountid all
        {
            sub.find('option').not(':first').hide();
            $('option', sub).filter(function(){
                if($(this).attr('data-group') == val){
                    $(this).show();
                }
            });
            member.find('option').not(':first').hide();
            $('option', member).filter(function(){
                if($(this).attr('data-group') == val){
                    $(this).show();
                }
            });
        }
        else {

            if(val2 == '--All--' && val3 != '0')
            {
                sub.find('option').not(':first').hide();
                $('option', sub).filter(function(){
                    if($(this).attr('data-group') == val){
                        $(this).show();
                    }
                });
                member.find('option').not(':first').hide();
                $('option', member).filter(function(){
                    if($(this).attr('data-group') == val && $(this).attr('data-accid') == val3){
                        $(this).show();
                    }
                });
            }
            else if(val2 != '--All--' && val3 == '0')
            {
                sub.find('option').not(':first').hide();
                $('option', sub).filter(function(){
                    if($(this).attr('data-group') == val && $(this).attr('data-curr') == val2){
                        $(this).show();
                    }
                });
                member.find('option').not(':first').hide();
                $('option', member).filter(function(){
                    if($(this).attr('data-group') == val && $(this).attr('data-curr') == val2){
                        $(this).show();
                    }
                });
            }
            else
            {
                sub.find('option').not(':first').hide();
                $('option', sub).filter(function(){
                    if($(this).attr('data-group') == val && $(this).attr('data-curr') == val2 ){
                        $(this).show();
                    }
                });
                member.find('option').not(':first').hide();
                $('option', member).filter(function(){
                    if($(this).attr('data-group') == val && $(this).attr('data-curr') == val2 && $(this).attr('data-accid') == val3){
                        $(this).show();
                    }
                });
            }
        }

    }


});







