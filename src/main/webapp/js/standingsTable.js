var standingsData;
var ReadingType = {
    "POINTS": "points",
    "POSITION": "position",
    "KART": "kart"
}

function loadStandings() {
    $.ajax({
        url: '/data/standings',
        context: document.body,
        success: function(data) {
            standingsData = data;
            renderStandingsTable(data);
        }
    });
}

function renderStandingsTable(standings) {
    var table = $('<table id="standingsTable" class="table table table-striped table-hover table-bordered"></table>');
    var header = buildHeader(standings.editions);
    var body = buildBody(standings.racers, standings.editions, standings.data);
    table.append(header);
    table.append(body);
    $('#standings').html(table)
}

function buildHeader(editions) {
    var header = $("<thead></thead>");
    var row = $('<tr></tr>');
    row.append($("<th>Pos</th>"));
    row.append($('<th class="racerHeader">Racer</th>'));
    $.each(editions, function(i, item) {
        row.append($('<th class="edition">' + item + "</th>"));
    });
    row.append($("<th>Total</th>"));
    header.append(row);
    return header;
}

function colorClass(position) {
    var retValue = "";
    if(position == 1) {
        retValue = "first";
    } else if(position == 2) {
        retValue = "second";
    } else if(position == 3) {
        retValue = "third";
    }
    return retValue;
}

function buildBody(racers, editions, data) {
    var body = $("<tbody></tbody>");
    $.each(racers, function(i, racer) {
        var row = $("<tr></tr>");
        row.append($('<td class="position">' + (i + 1) + '</td>'));
        row.append($('<td class="racer">' + racer + '</td>'));
        $.each(data[i], function(j, positionAndPoints) {
            var position = positionAndPoints.position;
            var pointsTotalClass = '';
            var reading;
            if(j == data[i].length - 1) {
                pointsTotalClass = "pointsTotal";
                reading = positionAndPoints["points"];
            } else {
                reading = positionAndPoints[displayedProperty];
            }
            var tdClass = colorClass(position)
            var pointsToDisplay = renderReading(reading, position, displayedProperty);
            row.append($('<td class="points ' + tdClass + ' ' + pointsTotalClass + '">' + pointsToDisplay + "</td>"));
        });
        body.append(row);
    });
    return body;
}

function renderReading(reading, position, displayedProperty) {
    switch(displayedProperty) {
        case ReadingType.POINTS:
            return (position > 0 ? reading : "");
        case ReadingType.POSITION:
        case ReadingType.KART:
            return reading > 0 ? reading : "";
    }
}

$(window).load(function() {
    loadStandings();

    $('#showPoints').click(function() {
        displayedProperty = ReadingType.POINTS;
        setSelectedButton('showPoints');
        renderStandingsTable(standingsData);
    });

    $('#showPosition').click(function() {
        displayedProperty = ReadingType.POSITION;
        setSelectedButton('showPosition');
        renderStandingsTable(standingsData);
    });

    $('#showKart').click(function() {
        displayedProperty = ReadingType.KART;
        setSelectedButton('showKart');
        renderStandingsTable(standingsData);
    });
});

var buttonIds = ["showPoints", "showPosition", "showKart"];

function setSelectedButton(id) {
    $.each(buttonIds, function(i, elem) {
        $('#' + elem).removeClass('btn-selected').addClass('btn-unselected');
    });
    $('#' + id).removeClass('btn-unselected').addClass('btn-selected');
}