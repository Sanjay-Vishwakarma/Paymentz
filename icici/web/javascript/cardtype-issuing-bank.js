/**
 * Created by Ajit on 6/26/2018.
 */
$(function () {
    $("#ctype").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method:"cardtypeList",
                    term: request.term
                },
                success: function( data ) {
                    //response( data );
                    const ordered = {};
                    Object.keys(data).sort().forEach(function(key) {
                        ordered[key] = data[key];
                    });

                    response($.map(ordered, function (value, key) {
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

    $("#ibank").autocomplete({
        source: function( request, response ) {
            $.ajax( {
                url: "/icici/servlet/GetDetailsAPI",
                dataType: "json",
                data: {
                    ctoken: $('#ctoken').val(),
                    method:"bankList",
                    term: request.term
                },
                success: function( data ) {
                    //response( data );
                    const ordered = {};
                    Object.keys(data).sort().forEach(function(key) {
                        ordered[key] = data[key];
                    });

                    response($.map(ordered, function (value, key) {
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