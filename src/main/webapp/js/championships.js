$(document).ready(function() {
    $.ajax({
        url: '/api/championships',
        context: document.body,
        success: function(data) {
            renderChampionships(data);
        },
        error: function(data) {
            $('#mainContent').html('<h1 class="pageTitle">Error</h1><p>Error loading championships</p>');
        }
    });
});

function renderChampionships(data) {
    $.each(data.standings, function(i, item) {
        renderChampionship(item);
    });
}

function renderChampionship(data) {
    var championshipDiv = $('<div></div>');
    championshipDiv.addClass('championshipDiv');
    championshipDiv.click(function() {
        window.location.href = data.link;
    });
    championshipDiv.append($('<img class="championshipLogo" src="images/chicagof1-logo-' + data.id + '.png"></img>'));
    championshipDiv.append($('<h3 class="championshipTitle">' + data.name + '</h3>'));
    championshipDiv.append($('<span class="championshipKey">Completion</span>'));
    championshipDiv.append($('<p class="championshipValue">' + renderCompletion(data) + '</p>'));
    championshipDiv.append($('<span class="championshipKey">Number of Participants</span>'));
    championshipDiv.append($('<p class="championshipValue">' + data.racerCount + '</p>'));
    $('#championshipList').append(championshipDiv);
}

function renderCompletion(data) {
    var completed = parseInt(data.completedSize);
    var size = parseInt(data.size);
    var percentage = Math.floor((completed / size) * 100);
    return completed + '/' + size + ' (' + percentage + '%)';
}
