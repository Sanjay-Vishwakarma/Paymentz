/**
 * Created by admin on 6/4/2018.
 */
$(function () {
    $("#aid-act").autocomplete({
      source: function( request, response ) {
        $.ajax( {
            url: "/icici/servlet/GetDetailsAPI",
            dataType: "json",
            data: {
                ctoken: $('#ctoken').val(),
                method:"agentList",
                term: request.term
            },
            success: function( data ) {

                response($.map(data, function (value, key) {
                    return {
                        label: value,
                        value: key
                    };
                }));
            }
        } );
    },
    minLength: 0

});

    $("#aid-act-mid").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method:"agentMemberList",
                    flag:"aid-act-mid",
                    agentid: $('#aid-act').val(),
                    term: request.term

                },
                success: function( data ) {

                    response($.map(data, function (value, key) {
                        return {
                            label: value,
                            value: key
                        };
                    }));
                }
            } );
        },
        minLength: 0


    });

    $("#aid-mid-tid").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method:"agentMemberTerminalList",
                    agentid: $('#aid-act').val(),
                    memberid: $('#aid-act-mid').val(),
                    term: request.term


                },
                success: function( data ) {
                    console.log("data - "+data)

                    response($.map(data, function (value, key) {
                        return {
                            label: value,
                            value: key
                        };
                    }));


                }
            } );
        },
        minLength: 0


    });


});