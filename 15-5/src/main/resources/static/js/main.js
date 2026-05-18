'use strict';

var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var fileInput = document.querySelector('#fileInput');
var messageArea = document.querySelector('#messageArea');
var nameInput = document.querySelector('#name');

var stompClient = null;
var username = null;

// Cuộn xuống dòng tin nhắn cuối cùng
function scrollToBottom() {
    messageArea.scrollTop = messageArea.scrollHeight;
}

// Gọi cuộn xuống lần đầu khi load trang để xem tin cũ
scrollToBottom();

function connect(event) {
    username = nameInput.value.trim();

    if (username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}

function onConnected() {
    // Đăng ký nhận tin nhắn từ /topic/public
    stompClient.subscribe('/topic/public', onMessageReceived);

    // Gửi thông báo có người tham gia
    stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    );
}

function onError(error) {
    alert('Không thể kết nối đến server WebSocket. Vui lòng thử lại sau.');
}

function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    var file = fileInput.files[0];

    if (file) {
        var formData = new FormData();
        formData.append("file", file);

        fetch('/upload', {
            method: 'POST',
            body: formData
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => { throw new Error(text) });
            }
            return response.json();
        })
        .then(data => {
            if (stompClient) {
                var chatMessage = {
                    sender: username,
                    content: messageContent,
                    type: 'CHAT',
                    fileUrl: data.fileUrl,
                    fileName: data.fileName
                };
                stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
                messageInput.value = '';
                fileInput.value = '';
            }
        })
        .catch(error => {
            alert('Lỗi upload: ' + error.message);
        });
    } else if (messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageContent,
            type: 'CHAT'
        };

        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    var messageElement = document.createElement('li');

    if (message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        messageElement.innerHTML = '<i><span>' + message.sender + ' đã tham gia phòng.</span></i>';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        messageElement.innerHTML = '<i><span>' + message.sender + ' đã rời khỏi phòng.</span></i>';
    } else {
        messageElement.classList.add('chat-message');
        var innerHtml = '<strong>' + message.sender + ': </strong>';
        if (message.content) {
            innerHtml += '<span>' + message.content + '</span>';
        }
        if (message.fileUrl) {
            innerHtml += '<div class="file-attachment"><a href="' + message.fileUrl + '" target="_blank" rel="noopener noreferrer">' + message.fileName + '</a></div>';
        }
        messageElement.innerHTML = innerHtml;
    }

    messageArea.appendChild(messageElement);
    scrollToBottom();
}

usernameForm.addEventListener('submit', connect, true);
messageForm.addEventListener('submit', sendMessage, true);
