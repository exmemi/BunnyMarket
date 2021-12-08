<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:set var="now" value="<%=new java.util.Date()%>" />

<link rel="stylesheet" type="text/css" href="/css/chat_style.css">

<div class="inner">
	<div id="container" class="container">
		<input type="hidden" id="chatHistory" value="${chatHistory}">
		<div class="info">
			<div class="wrap">
				<img alt="profile" src="/image/profile-icon.png" class="profile-img">
				<div class="info-text">
					<span class="tit nick">${nick}</span> <span class="sm-tit">후기</span>
				</div>
				<div class="chatout-btn">
					<button type="button" class="btn-list" id="chatOutBtn">나가기</button>
				</div>
			</div>

			<div class="wrap link" onclick="location.href='../product/detail?idx=${dto.idx}'">
				<img alt="product-image" src="../../photo/${photo}" class="product-img">
				<div class="info-text">
					<div class="title-div">
						<div class="status" id="status">${dto.sellstatus}</div>
						<div class="title">${dto.title}</div>
					</div>
					<div class="price tit">
						<fmt:formatNumber type="number" value="${dto.price}" />원
					</div>
				</div>
			</div>
		</div>

		<div id="chating" class="chating">
			<c:forEach var="chat" items="${chatHistory}">
				<c:if test="${chat.sender == myId}">
					<div class='me'>
						<div class='time'>
							<fmt:parseDate var="dateString" value="${chat.time}" pattern="yyyy-MM-dd HH:mm:ss" />
							<fmt:formatDate value="${dateString}" pattern=" HH:mm" />
						</div>
						<div class='speech-bubble'>
							<p>${chat.msg }</p>
						</div>
					</div>
				</c:if>
				<c:if test="${chat.sender != myId}">
					<div class='other'>
						<div class='speech-bubble other-bubble'>
							<p>${chat.msg }</p>
						</div>
						<div class='time'>
							<fmt:parseDate var="dateString" value="${chat.time}" pattern="yyyy-MM-dd HH:mm:ss" />
							<fmt:formatDate value="${dateString}" pattern="HH:mm" />
						</div>
					</div>
				</c:if>
			</c:forEach>
		</div>

		<form:form modelAttribute="ChatDTO" name="msg-form" class="msgForm">
			<input type="hidden" name="chat_idx" id="chat_idx"
				value="${chat_idx}">
			<input type="hidden" name="sessionId" id="sessionId" value="${myId}">
			<input type="hidden" name="userName" id="userName" value="${nick}">
			<input type="hidden" name="roomNumber" id="roomNumber" value="${roomNumber}">
			<input type="hidden" name="idx" id="idx" value="${dto.idx}">
			<input type="hidden" name="seller" id="seller" value="${dto.id}">
			<textarea class="chat-box" name="msg" id="chatting"
				placeholder="메세지를 입력해주세요"></textarea>
			<div class="chat-btn-div">
				<button type="button" class="btn-add" onclick="first()" id="sendBtn">전송</button>
			</div>
		</form:form>

		<div class="btn-wrap">
			<button type="button" class="btn-default"
				onclick="location.href='../../product/detail?idx=${dto.idx}'">뒤로가기</button>
		</div>
	</div>
</div>
<script type="text/javascript">
//스크롤 항상 제일 밑으로
$(".chating").scrollTop($(".chating")[0].scrollHeight);

var ws;

function wsOpen() {
	ws = new WebSocket("ws://" + location.host + "/chating/"
			+ $("#roomNumber").val());
	wsEvt();
}

function wsEvt() {
	ws.onopen = function(data) {
		//소켓이 열리면 동작
	}

	ws.onmessage = function(data) {
		//메시지를 받으면 동작
		var msg = data.data;

		//채팅메세지가 존재하면,
		if (msg != null && msg.trim() != '') {
			var d = JSON.parse(msg);
			if (d.type == "getId") {
				var si = d.sessionId != null ? d.sessionId : "";
				if (si != '') {
					$("#sessionId").val(si);
				}
			} else if (d.type == "message") {
				if (d.sessionId == $("#sessionId").val()) {
					$("#chating").append(
					"<div class='me'><div class='time'><fmt:formatDate value="${now }" type="time" pattern=" hh:mm"/></div>"
					+"<div class='speech-bubble'><p>"+ d.msg + "</p></div></div>");
				} else {
					$("#chating").append(
					"<div class='other'><div class='speech-bubble other-bubble'><p>"+ d.msg
					+ "</p></div><div class='time'><fmt:formatDate value="${now }" type="time" pattern=" hh:mm"/></div></div>");
				}
				//스크롤 항상 제일 밑으로
				$(".chating").scrollTop($(".chating")[0].scrollHeight);
			} else {
				console.warn("unknown type!")
			}
		}
	}

	//엔터눌러도 보내지게
	document.addEventListener("keypress", function(e) {
		if (e.keyCode == 13) {
			if ($("#sendBtn").attr("onClick") == "first()") {
				first();
			} else {
				send();
			}
		}
	});
}

//소켓 열기
wsOpen();

//첫번째 메세지
function first() {
	console.log("first");
	//이벤트 변경
	$("#sendBtn").attr("onclick", "send()");
	var idx = $("#idx").val();
	var seller = $("#seller").val();
	var roomNumber = $("#roomNumber").val();
	var msg = $("#chatting").val();

	//방 만들기
	$.ajax({
		type : "post",
		url : "firstSend",
		data : {
			"idx" : idx,
			"seller" : seller,
			"roomNumber" : roomNumber,
			"msg" : msg
		},
		success : function(data) {
			$("#chat_idx").val(data.chat_idx);
		},
		error : function(data) {
			console.log("에러", data);
		}
	});

	var option = {
		type : "message",
		roomNumber : $("#roomNumber").val(),
		sessionId : $("#sessionId").val(),
		userName : $("#userName").val(),
		msg : $("#chatting").val()
	}
	ws.send(JSON.stringify(option))

	$('#chatting').val("");
}

//메세지 전송 버튼 눌렀을 때,
function send() {
	console.log("send");

	var data = {
		chat_idx : $("#chat_idx").val(),
		product_idx : $("#idx").val(),
		sender : $("#sessionId").val(),
		seller_id : $("#seller").val(),
		msg : $("#chatting").val()
	}

	//send 저장
	$.ajax({
		type : "post",
		url : "send",
		data : data,
		success : function() {
			console.log("send success");
		},
		error : function(data) {
			console.log("send 에러", data);
		}
	});

	var option = {
		type : "message",
		roomNumber : $("#roomNumber").val(),
		sessionId : $("#sessionId").val(),
		userName : $("#userName").val(),
		msg : $("#chatting").val()
	}
	ws.send(JSON.stringify(option))

	//채팅메세지 사라지게
	$('#chatting').val("");
}

//판매상태 폰트 색상
if ($("#status").text() == "판매완료") {
	$("#status").css("color", "#979593");
} else if ($("#status").text() == "예약중") {
	$("#status").css("color", "#ff7ab0");
} else if ($("#status").text() == "판매중") {
	$("#status").css("color", "#3088d4");
}

//채팅방 나가기
$("#chatOutBtn").click(function() {
	var result = confirm("정말 채팅방을 나가시겠습니까?\n나간 채팅방의 데이터는 복구되지 않습니다");
	if (result) {
		//채팅방 delete
		location.href = "deleteChat?chat_idx="
				+ $('#chat_idx').val() + "&id="
				+ $('#sessionId').val() + "&idx="
				+ $('#idx').val();
		console.log("delete");
	} else {
		return;
	}
});
</script>