function loadData() {
    $.ajax({
        url: '/data/racers/' + racerId,
        context: document.body,
        success: function(data) {
            renderRacerStats(data);
        }
    });
}

function renderRacerStats(data) {
   var racerDiv = $('#racerDiv');
   var standingsDiv = $('#racerStandingsStatsDiv');
   var editionsDiv = $('#racerEditionsStatsDiv');
   var othersDiv = $('#racerOtherStatsDiv');
   addSpan(standingsDiv, "Current Position in Standings", data['currentStandingsPosition']);
   addSpan(standingsDiv, "Current Points in Standings", data['currentPoints']);
   addSpan(editionsDiv, "Number of Edition Wins", data['editionWinCount']);
   addSpan(editionsDiv, "Number of Single Race wins", data['raceWinCount']);
   addSpan(othersDiv, "Number of Videos Posted", data['videosCount']);
}

function addSpan(div, key, value) {
    div.append($('<span class="racerStatsKey">' + key + '</span>'));
    div.append($('<span class="racerStatsValue">' + value + '</span>'));
}

$(window).load(function() {
    loadData();
});
