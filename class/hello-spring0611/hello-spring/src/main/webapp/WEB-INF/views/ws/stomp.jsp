<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<jsp:include page="/WEB-INF/views/common/header.jsp">
	<jsp:param value="Stomp" name="title"/>
</jsp:include>
<div class="input-group mb-3">
	<select id="stomp-url" class="form-select mr-1">
		<option value="">전송url</option>
		<option value="/topic/a">/topic/a</option>
		<option value="/app/a">/app/a</option>
	</select>
	<input type="text" id="message" class="form-control" placeholder="Message">
	<div class="input-group-append" style="padding: 0px;">
	    <button id="sendBtn" class="btn btn-outline-secondary" type="button">Send</button>
	</div>
</div>
<div>
    <ul class="list-group list-group-flush" id="data"></ul>
</div>
<!-- sockjs-client js 추가 -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.1.2/sockjs.js" integrity="sha512-3/5zbNJKTwZiPFIUPL9Q6woFGvOluvYq2/rJ+C4sZUTXKhVoY3e6mSTf5RJG01lYX3atqeslmWTsxCXb147x2w==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<!-- stomp.js 추가 -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.js" integrity="sha512-tL4PIUsPy+Rks1go4kQG8M8/ItpRMvKnbBjQm4d2DQnFwgcBYRRN00QdyQnWSCwNMsoY/MfJY8nHp2CzlNdtZA==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<!-- 구형브라우져에서 ES6 최신문법 사용하기 -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/babel-standalone/6.26.0/babel.js" integrity="sha512-pBSlhNUvB+td6sjW1zmR6L7c7kVWR4octUPl4tfHmzO63424nxta8aLmficEcAAswQmRqTiToi63AazDurj/Sg==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/babel-polyfill/7.12.1/polyfill.js" integrity="sha512-wixq/u8vbwoVM6yCmTHUNszWudaPpwf8pKxfG1NPUOBXTh1ntBx8sr/dJSbGTlZUqpcoPjaUmU1hlBB3oJlzFQ==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<script type="text/babel">
/**
 * sock.js
 * html5api websocket을 지원하지 않는 브라우져에서도 양방향 통신을 사용
 * 
 * - http로 최초연결시도후, websock이 사용가능한 경우 ws protocol로 upgrade.
 * - 구버젼 브라우져의 경우, xhr-stream/xhr-polling 중 적합한 방식으로 양방향 통신 사용.
 */
const ws = new SockJS(`http://\${location.host}${pageContext.request.contextPath}/stommmp`);
const stompClient = Stomp.over(ws);

const $data = $("#data");

//최초 연결
stompClient.connect({}, frame => {
	console.log("stomp connected : ", frame);

	//구독
	stompClient.subscribe("/topic/a", message => {
		console.log("message from /topic/a : ", message);
	});
	stompClient.subscribe("/bpp/a", message => {
		console.log("message from /bpp/a : ", message);
	});
});



const sendMessage = () => {
	const url = $("#stomp-url").val();
	if(url === ""){
		alert("전송 url을 선택하세요.");
		return;
	}

	const $message = $("#message");
	if($message.val()){
		stompClient.send(url, {}, $message.val());
		$message.val("").focus();
	}
};

$("#sendBtn").click(sendMessage);
$("#message").keyup(e => e.keyCode == 13 && sendMessage());



</script>
<jsp:include page="/WEB-INF/views/common/footer.jsp"></jsp:include>
