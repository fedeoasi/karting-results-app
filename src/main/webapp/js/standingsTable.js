function loadStandings() {
    $.ajax({
        url: '/data/standings',
        context: document.body,
        success: function(data) {
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
    $('#standings').append(table)
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
            var points = positionAndPoints.points;
            var tdClass = colorClass(position)
            var pointsToDisplay = (position > 0 ? points : "")
            row.append($('<td class="points ' + tdClass + '">' + pointsToDisplay + "</td>"));
        });
        body.append(row);
    });
    return body;
}

$(window).load(function() {
    loadStandings();
});