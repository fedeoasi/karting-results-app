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
        { "mData": "date", "sTitle": "Date"},
        { "mData": "startTime", "sTitle": "Start Time"},
        { "mData": "endTime", "sTitle": "End Time"},
        { "mData": "location", "sTitle": "Location"}
    ]
  });
});