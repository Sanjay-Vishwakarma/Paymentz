$(function(){

    $('#bank').on('change', function(){
        var val = $(this).val();
        var sub = $('#accountid');
        var val2 = $('#currency').val();


        if(val == '--All--' || val == "") {
            if(val2 == '--All--') {


                sub.find('option').not(':first').hide();


                $('option', sub).filter(function(){

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
            else {
                sub.find('option').not(':first').hide();


                $('option', sub).filter(function(){


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




                    if($(this).attr('data-curr') == val2){

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
                    }


                });
            }
        }
        else {

            if(val2 == '--All--') {


                sub.find('option').not(':first').hide();

                $('option', sub).filter(function(){


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
                    }



                });
            }
            else {
                sub.find('option').not(':first').hide();



                $('option', sub).filter(function(){


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



                    if($(this).attr('data-bank') == val && $(this).attr('data-curr') == val2){

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
                    }



                });
            }

        }
        sub.val(0);
    });
});


$(function(){

    $('#currency').on('change', function(){
        var val = $(this).val();
        var sub = $('#accountid');
        var val2 = $('#bank').val();
        if(val == '--All--') {
            if(val2 == '--All--'  || val2 == "")
            {
                sub.find('option').not(':first').hide();
                //$('#accountid').find('option').show();
                $('option', sub).filter(function()
                {
                    if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                        if (this.nodeName.toUpperCase() === 'OPTION')
                        {
                            var span = $(this).parent();
                            var opt = this;
                            $(opt).show();
                            if($(this).parent().is('span'))
                            {
                                //$(opt).show();
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
            else {
                sub.find('option').not(':first').hide();

                $('option', sub).filter(function(){

                    if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                        if (this.nodeName.toUpperCase() === 'OPTION')
                        {
                            var span = $(this).parent();
                            var opt = this;
                            //$(opt).show();
                            if($(this).parent().is('span'))
                            {
                                $(span).replaceWith(opt);
                            }
                            if($(this).attr('value') != "0")
                            {
                                $(opt).wrap((navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') ? '<span>' : null);
                            }
                        }
                    }


                    if($(this).attr('data-bank') == val2){

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
                    }

                });



            }
        }
        else {

            if(val2 == '--All--'  || val2 == "") {
                sub.find('option').not(':first').hide();

                $('option', sub).filter(function(){

                    if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                        if (this.nodeName.toUpperCase() === 'OPTION')
                        {
                            var span = $(this).parent();
                            var opt = this;
                            if($(this).parent().is('span'))
                            {
                                //$(opt).show();
                                $(span).replaceWith(opt);
                            }
                            if($(this).attr('value') != "0")
                            {
                                $(opt).wrap((navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') ? '<span>' : null);
                            }
                        }
                    }


                    if($(this).attr('data-curr') == val){
                        if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                            if (this.nodeName.toUpperCase() === 'OPTION') {
                                var span = $(this).parent();
                                var opt = this;
                                $(opt).show();
                                if($(this).parent().is('span')) {
                                    //$(opt).show();
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
            else {
                sub.find('option').not(':first').hide();

                $('option', sub).filter(function(){

                    if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                        if (this.nodeName.toUpperCase() === 'OPTION')
                        {
                            var span = $(this).parent();
                            var opt = this;
                            if($(this).parent().is('span'))
                            {
                                //$(opt).show();
                                $(span).replaceWith(opt);
                            }
                            if($(this).attr('value') != "0")
                            {
                                $(opt).wrap((navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') ? '<span>' : null);
                            }
                        }
                    }


                    if($(this).attr('data-curr') == val && $(this).attr('data-bank') == val2){
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
                    }
                });
            }

        }
        sub.val(0);
    });
});


$(function(){


    var val = $('#bank').val();
    var sub = $('#accountid');
    var val2 = $('#currency').val();
    var subval = $('#accountid').val();


        if(val == '--All--' || val == "") {
            if(val2 == '--All--') {


                sub.find('option').not(':first').hide();


                $('option', sub).filter(function(){

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
            else {
                sub.find('option').not(':first').hide();


                $('option', sub).filter(function(){


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




                    if($(this).attr('data-curr') == val2){

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
                    }


                });
            }
        }
        else {

            if(val2 == '--All--') {


                sub.find('option').not(':first').hide();

                $('option', sub).filter(function(){


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
                    }



                });
            }
            else {
                sub.find('option').not(':first').hide();

                $('option', sub).filter(function(){


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



                    if($(this).attr('data-bank') == val && $(this).attr('data-curr') == val2){

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
                    }



                });
            }

        }
        //sub.val(0);

});

