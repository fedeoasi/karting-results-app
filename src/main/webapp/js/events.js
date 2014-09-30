var dataTable;
$(document).ready(function() {
  dataTable = $('#events').dataTable( {
    //"sDom": 'T<"clear">lfrtip',
    "bProcessing": true,
    "bStateSave": true,
    "bSort" : false,
    "sAjaxSource": 'api/events',
    "sAjaxDataProp": 'events',
    "aoColumns": [
        { "mData": "link", "sTitle": "Name", "sClass": "movieTitle", "sType": "html" },
        { "mData": "date", "sTitle": "Date" },
        { "mData": "startTime", "sTitle": "Start Time", "sWidth": "150px", "sClass": "eventTime" },
        { "mData": "endTime", "sTitle": "End Time", "sWidth": "150px", "sClass": "eventTime" },
        { "mData": "location", "sTitle": "Location" }
    ]
  });
});