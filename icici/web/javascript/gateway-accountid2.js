/**
 * Created by admin on 4/11/2017.
 */
$(function(){
    $('#bank2').on('change', function(){
        var val = $(this).val();
        var sub2 = $('#accountid2');
        if(val == '0')//gateway all
        {
            sub2.find('option').not(':first').hide();
            $('option', sub2).filter(function(){
                if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {

                    if (this.nodeName.toUpperCase() === 'OPTION') {
                        var span = $(this).parent();
                        var opt = this;

                        if($(this).parent().is('span')) {

                            $(span).replaceWith(opt);

                        }
                        if($(this).attr('value') != "0")
                        {
                            $(opt).wrap((navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') ? '<span>' : null);
                        }
                    }

                }

                if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                    if (this.nodeName.toUpperCase() === 'OPTION') {
                        var span = $(this).parent();
                        var opt = this;
                        $(opt).show();
                        if($(this).parent().is('span')) {

                            $(span).replaceWith(opt);
                        }
                    }
                } else {
                    $(this).show(); //all other browsers use standard .show()
                }
            });
        }
        else
        {
            sub2.find('option').not(':first').hide();

            $('option', sub2).filter(function(){

                if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {

                    if (this.nodeName.toUpperCase() === 'OPTION') {
                        var span = $(this).parent();
                        var opt = this;

                        if($(this).parent().is('span')) {

                            $(span).replaceWith(opt);

                        }
                        if($(this).attr('value') != "0")
                        {
                            $(opt).wrap((navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') ? '<span>' : null);
                        }

                    }

                }

                if($(this).attr('data-bank') == val){

                    if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') 													{
                        if (this.nodeName.toUpperCase() === 'OPTION') {
                            var span = $(this).parent();
                            var opt = this;
                            $(opt).show();
                            if($(this).parent().is('span')) {

                                $(span).replaceWith(opt);
                            }
                        }
                    } else {
                        $(this).show(); //all other browsers use standard .show()
                    }
                }


            });
        }
        sub2.val(0);
    });
});

$(function(){
    var val = $('#bank2').val();
    var sub2 = $('#accountid2');
    //alert("function---");
    if(val == '0')//gateway all
    {
        //alert("if---");
        sub2.find('option').not(':first').hide();
        $('option', sub2).filter(function(){

            if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {

                if (this.nodeName.toUpperCase() === 'OPTION') {
                    var span = $(this).parent();
                    var opt = this;

                    if($(this).parent().is('span')) {

                        $(span).replaceWith(opt);

                    }
                    if($(this).attr('value') != "0")
                    {
                        $(opt).wrap((navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') ? '<span>' : null);
                    }


                }

            }


            if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                if (this.nodeName.toUpperCase() === 'OPTION') {
                    var span = $(this).parent();
                    var opt = this;
                    $(opt).show();
                    if($(this).parent().is('span')) {

                        $(span).replaceWith(opt);
                    }
                }
            } else {
                $(this).show(); //all other browsers use standard .show()
            }

            //$(this).show();

        });
    }
    else
    {
        sub2.find('option').not(':first').hide();


        $('option', sub2).filter(function(){


            if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {

                if (this.nodeName.toUpperCase() === 'OPTION') {
                    var span = $(this).parent();
                    var opt = this;

                    if($(this).parent().is('span')) {

                        $(span).replaceWith(opt);

                    }
                    if($(this).attr('value') != "0")
                    {
                        $(opt).wrap((navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') ? '<span>' : null);
                    }


                }

            }



            if($(this).attr('data-bank') == val){

                if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') 													{
                    if (this.nodeName.toUpperCase() === 'OPTION') {
                        var span = $(this).parent();
                        var opt = this;
                        $(opt).show();
                        if($(this).parent().is('span')) {

                            $(span).replaceWith(opt);
                        }
                    }
                } else {
                    $(this).show(); //all other browsers use standard .show()
                }

                //$(this).show();
            }


        });
    }
    //sub2.val(0);
});

