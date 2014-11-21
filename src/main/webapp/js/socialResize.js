$(window).resize(function () {
    resizeSocial();
});

$(document).ready(function() {
    resizeSocial();
});

function resizeSocial() {
    var socialWidth = $('#socialContent').width();
    if (socialWidth > 1000) {
        var half = socialWidth / 2 - 4;
        setSocialDivSize(half);
    } else {
        setSocialDivSize(socialWidth);
    }
}

function setSocialDivSize(width) {
    $('#fbBoxDiv').css({'width': width + 'px'});
    $('#twitterDiv').css({'width': width + 'px'});
}