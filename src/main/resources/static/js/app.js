var stompClient = null;
var page = 0;

function connect() {
    var socket = new SockJS('/stomp');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, 
    		function (frame) {
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
		    },
		    function (error) {
		        //Dans le doute on reload la page, pour avoir la page de login
		        location.reload();
		    },
    );
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
	  $("#conversation").prepend(createMessage(this, ''))
	});
	
	if(messagesPages.last) {
		page = -1;
		$( "#loadMoreBtn" ).remove();
	}
}

function addMessageBottom(message) {
    $("#conversation").append(createMessage(message, ''));
}

function addMessageError(message) {
    $("#conversation").append(createMessage(message, 'class="error"'));
}

function createMessage(message, trClass) {
	return "<li class='list-group-item " 
		+ trClass
		+ "'><span class='date'>[" 
		+ formatNumber(message.date.dayOfMonth)
		+ "/"
		+ formatNumber(message.date.monthValue)
		+ "/"
		+ message.date.year
		+ " "
		+ formatNumber(message.date.hour)
		+ ":"
		+ formatNumber(message.date.minute)
		+ ":"
		+ formatNumber(message.date.second)
		+ "]</span> <span class='username strong'>" 
		+ message.username 
		+ "</span> > " 
		+ linkify(message.content)
		+ "</li>";
}

function resize() {
	console.log("resize " + $( "#messagesScroll" ).height() );
	$( "#messagesScroll" ).height( $( window ).height() 
			- $( "#navbar" ).height() 
			- $( "#footer" ).height() 
			- 20);
	console.log("resize " + $( "#messagesScroll" ).height());
}

$(function () {
	$( window ).resize(function() {
		resize();
	});
	resize();
	
    $("#sendForm").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#send" ).click(function() { sendMessage(); });
    $( "#loadMoreBtn" ).click(function() { requestMessagesList(); });
    connect();
});

function formatNumber(number) {
	if(number < 10) {
		return '0' + number;
	} else {
		return number;
	}
}

/* See https://github.com/jmrware/LinkifyURL */
function linkify(text) {
	
	// added
	var img_pattern = /(https?:\/\/[\w\-\.]+\.[a-zA-Z]{2,3}(?:\/\S*)?(?:[\w])+\.(?:jpg|png|gif|jpeg))/ig;
	var img_replace = '<a href="$1" target="blank"><img src="$1" /></a>';
	text = text.replace(img_pattern, img_replace);
	// /added
	
    var url_pattern = /(\()((?:ht|f)tps?:\/\/[a-z0-9\-._~!$&'()*+,;=:\/?#[\]@%]+)(\))|(\[)((?:ht|f)tps?:\/\/[a-z0-9\-._~!$&'()*+,;=:\/?#[\]@%]+)(\])|(\{)((?:ht|f)tps?:\/\/[a-z0-9\-._~!$&'()*+,;=:\/?#[\]@%]+)(\})|(<|&(?:lt|#60|#x3c);)((?:ht|f)tps?:\/\/[a-z0-9\-._~!$&'()*+,;=:\/?#[\]@%]+)(>|&(?:gt|#62|#x3e);)|((?:^|[^=\s'"\]])\s*['"]?|[^=\s]\s+)(\b(?:ht|f)tps?:\/\/[a-z0-9\-._~!$'()*+,;=:\/?#[\]@%]+(?:(?!&(?:gt|#0*62|#x0*3e);|&(?:amp|apos|quot|#0*3[49]|#x0*2[27]);[.!&',:?;]?(?:[^a-z0-9\-._~!$&'()*+,;=:\/?#[\]@%]|$))&[a-z0-9\-._~!$'()*+,;=:\/?#[\]@%]*)*[a-z0-9\-_~$()*+=\/#[\]@%])/img;
    var url_replace = '$1$4$7$10$13<a href="$2$5$8$11$14" target="blank">$2$5$8$11$14</a>$3$6$9$12';
    return text.replace(url_pattern, url_replace);
}