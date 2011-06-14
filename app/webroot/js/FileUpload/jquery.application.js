/*
 * jQuery File Upload Plugin JS Example 5.0.1
 * https://github.com/blueimp/jQuery-File-Upload
 *
 * Copyright 2010, Sebastian Tschan
 * https://blueimp.net
 *
 * Licensed under the MIT license:
 * http://creativecommons.org/licenses/MIT/
 */

/*jslint nomen: false */
/*global $ */

$(function () {

    // Initialize the jQuery File Upload widget:
    $('div[id^="fileupload_"]').fileupload();

    // Load existing files:
    $('div[id^="fileupload_"]').each(function(index,element){
        $.getJSON($('div',element).prop('title'), function (files) {
            var fu = $(element).data('fileupload');
            if(files != null){
                fu._adjustMaxNumberOfFiles(-files.length);
                fu._renderDownload(files)
                    .appendTo($('.files',element))
                    .fadeIn(function () {
                        // Fix for IE7 and lower:
                        $(this).show();
                });
            }
        });
    });

    // Open download dialogs via iframes,
    // to prevent aborting current uploads:
    $('div[id^="fileupload_"]').each(function(index,element){
        $('.files a:not([target^=_blank])',element).live('click', function (e) {
            e.preventDefault();
            $('<iframe style="display:none;"></iframe>')
                .prop('src', this.href)
                .appendTo('body');
        });
    });
});