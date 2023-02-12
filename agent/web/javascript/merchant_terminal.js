$(function(){
    $('#mid').on('change', function()
    {
        var val = $(this).val();//member
        var sub = $('#tid');
        if(val == '')//accountid all(0)
        {
            $('#tid').find('option').show();
        }
        else
        {
            sub.find('option').not(':first').hide();
            $('option', sub).filter(function()
            {
                if($(this).attr('data-group') == val)
                {
                    $(this).show();
                }
            });
        }
        sub.val(0);
    });
});

$(function(){

    //var val = $(this).val();//member
    var val = $('#mid').val();//member
    var sub = $('#tid');

    if(val == '')//accountid all(0)
    {
        $('#tid').find('option').show();
    }
    else
    {
        sub.find('option').not(':first').hide();
        $('option', sub).filter(function()
        {
            if($(this).attr('data-group') == val)
            {
                $(this).show();
            }
        });
    }
});