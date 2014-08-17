$(window).resize(function () {
    resizeEditions();
});

$(document).ready(function() {
    resizeEditions();
});

function resizeEditions() {
    $(".editions").css({ 'width' : "100%"});
    var editionsWidth = $('.editions').width();
    var editionWidth = $('.editionDiv').width();
    var editionsPerRow = Math.floor(editionsWidth / 250);
    var newEditionWidth = Math.floor((editionsWidth - 4 * editionsPerRow) / editionsPerRow);
    $('.editionDiv').css({ width: newEditionWidth });
}

