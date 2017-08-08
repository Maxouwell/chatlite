var stompClient = null;
var page = 0;

function connect() {
    var socket = new SockJS('/stomp');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/user/topic/message/error', function (message) {
        	addMessageError(JSON.parse(message.body));
        	scrollMessages();
        });
        stompClient.subscribe('/user/topic/messages/list', function (message) {
        	addMessagesTop(JSON.parse(message.body));
        	
        	if(page == 1) {
            	scrollMessages();
        	}
        });
        stompClient.subscribe('/topic/messages/flow', function (message) {
        	addMessageBottom(JSON.parse(message.body));
            scrollMessages();
        });
        stompClient.subscribe('/topic/users/list', function (message) {
        	updateUsers(JSON.parse(message.body));
        });
        stompClient.subscribe('/user/topic/users/list', function (message) {
        	updateUsers(JSON.parse(message.body));
        });
        
        requestMessagesList();
        requestUsersList();
    });
}

function requestUsersList() {
	if(page == -1) {
		return;
	}
	
	stompClient.send("/api/users/askList", {}, JSON.stringify({}));
}

function updateUsers(users) {
	$( "#usersList" ).empty();
	
	jQuery.each(users, function(index, user) {
		if(index != 0) {
			$("#usersList").append(', ');
		}
		
		$("#usersList").append(user);
	});
}

function requestMessagesList() {
	if(page == -1) {
		return;
	}
	
	stompClient.send("/api/messages/askList", {}, JSON.stringify({'page':page}));
    page = page + 1;
}

function sendMessage() {
    stompClient.send("/api/message/send", {}, JSON.stringify({'channel':'main', 'content': $("#message").val()}));
    $("#message").val('');
}

function scrollMessages() {
	$("#messagesScroll").animate({ scrollTop: $("#messagesScroll")[0].scrollHeight}, 500);
}

function addMessagesTop(messagesPages) {
	jQuery.each(messagesPages.content, function() {
	  $("#loadMore").after(createMessage(this, ''))
	});
	
	if(messagesPages.last) {
		page = -1;
		$( "#loadMore" ).remove();
	}
}

function addMessageBottom(message) {
    $("#messages").append(createMessage(message, ''));
}

function addMessageError(message) {
    $("#messages").append(createMessage(message, 'class="error"'));
}

function createMessage(message, trClass) {
	return "<tr " 
		+ trClass
		+ "><td>[" 
		+ message.date.dayOfMonth
		+ "/"
		+ message.date.monthValue
		+ "/"
		+ message.date.year
		+ " "
		+ message.date.hour
		+ ":"
		+ message.date.minute
		+ ":"
		+ message.date.second
		+ "] " 
		+ message.username 
		+ " > " 
		+ message.content 
		+ "</td></tr>";
}

$(function () {
    $("#sendForm").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#send" ).click(function() { sendMessage(); });
    $( "#loadMoreBtn" ).click(function() { requestMessagesList(); });
    connect();
});