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
   var currentStandingsPosition = data['currentStandingsPosition'];
   addSpan(standingsDiv, "Position", currentStandingsPosition != 0 ? currentStandingsPosition : "-");
   addSpan(standingsDiv, "Points", currentStandingsPosition != 0 ? data['currentPoints'] : "-");
   addSpan(editionsDiv, "Edition Wins", data['editionWinCount']);
   addSpan(editionsDiv, "Single Race wins", data['raceWinCount']);
   addSpan(othersDiv, "Videos Posted", data.videosCount);
   addGraph("racerEditionsStatsDiv", data.editionPosHistogram);
}

function addSpan(div, key, value) {
    div.append($('<span class="racerStatsKey">' + key + '</span>'));
    div.append($('<span class="racerStatsValue">' + value + '</span>'));
}

function addGraph(divName, histogram) {
    var divWidth = $('#' + divName).width();

    var margin = {top: 20, right: 20, bottom: 30, left: 40},
        width = divWidth - margin.left - margin.right,
        height = 500 - margin.top - margin.bottom;

    var x = d3.scale.ordinal()
        .rangeRoundBands([0, width], .1, .3);

    var y = d3.scale.linear()
        .range([height, 0]);

    var xAxis = d3.svg.axis()
        .scale(x)
        .orient("bottom");

    var yAxis = d3.svg.axis()
        .scale(y)
        .orient("left");

    var svg = d3.select('#' + divName).append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
      .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    x.domain(histogram.map(function(e, i) { return i + 1; }));
    y.domain([0, d3.max(histogram)]);

    svg.append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(0," + height + ")")
        .call(xAxis)
      .append("text")
        .attr("x", width + 20)
        .style("text-anchor", "end")
        .text("Position");

    svg.append("g")
        .attr("class", "y axis")
        .call(yAxis)
      .append("text")
        .attr("transform", "rotate(-90)")
        .attr("y", 5)
        .attr("dy", "0.5em")
        .style("text-anchor", "end")
        .text("Frequency");

    svg.selectAll(".bar")
        .data(histogram)
      .enter().append("rect")
        .attr("class", "bar")
        .attr("x", function(d, i) {
            return x(i + 1);
         })
        .attr("width", x.rangeBand())
        .attr("y", function(d) { return y(d); })
        .attr("height", function(d) { return height - y(d); });
}

$(window).load(function() {
    loadData();
});
