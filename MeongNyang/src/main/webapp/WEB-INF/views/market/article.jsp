<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>spring</title>
<jsp:include page="/WEB-INF/views/layout/staticHeader.jsp"/>
<style type="text/css">
.body-main {
	max-width: 700px;
	padding-top: 15px;
}

.table-article tr>td { padding-left: 5px; padding-right: 5px; }

.img-box {
	max-width: 700px;
	padding: 5px;
	box-sizing: border-box;
	border: 1px solid #ccc;
	display: flex; /* 자손요소를 flexbox로 변경 */
	flex-direction: row; /* 정방향 수평나열 */
	flex-wrap: nowrap;
	overflow-x: auto;
}
.img-box img {
	width: 100px; height: 100px;
	margin-right: 5px;
	flex: 0 0 auto;
	cursor: pointer;
}

.reply { clear: both; padding: 20px 0 10px; }
.reply .bold { font-weight: 600; }

.reply .form-header { padding-bottom: 7px; }
.reply-form  tr>td { padding: 2px 0 2px; }
.reply-form textarea { width: 100%; height: 75px; }
.reply-form button { padding: 8px 25px; }

.reply .reply-info { padding-top: 25px; padding-bottom: 7px; }
.reply .reply-info  .reply-count { color: #3EA9CD; font-weight: 700; }

.reply .reply-list tr>td { padding: 7px 5px; }
.reply .reply-list .bold { font-weight: 600; }

.reply .deleteReply, .reply .deleteReplyAnswer { cursor: pointer; }
.reply .notifyReply { cursor: pointer; }

.reply-list .list-header { border: 1px solid #ccc; background: #f8f8f8; }
.reply-list tr>td { padding-left: 7px; padding-right: 7px; }

.reply-answer { display: none; }
.reply-answer .answer-left { float: left; width: 5%; }
.reply-answer .answer-right { float: left; width: 95%; }
.reply-answer .answer-list { border-top: 1px solid #ccc; padding: 0 10px 7px; }
.reply-answer .answer-form { clear: both; padding: 3px 10px 5px; }
.reply-answer .answer-form textarea { width: 100%; height: 75px; }
.reply-answer .answer-footer { clear: both; padding: 0 13px 10px 10px; text-align: right; }

.answer-article { clear: both; }
.answer-article .answer-article-header { clear: both; padding-top: 5px; }
.answer-article .answer-article-body { clear:both; padding: 5px 5px; border-bottom: 1px solid #ccc; }

.photo-layout img { width: 570px; height: 450px; }
</style>
<script type="text/javascript" src="https://code.jquery.com/jquery-1.12.4.min.js" ></script>
    <!-- iamport.payment.js -->
<script type="text/javascript" src="https://cdn.iamport.kr/js/iamport.payment-1.2.0.js"></script>
<script type="text/javascript">

function login() {
	loaction.href = "${pageContext.request.contextPath}/member/login.do";
}

function ajaxFun(url, method, query, dataType, fn) {
	$.ajax({
		type:method,			//메소드(get, post, put, delete)
		url:url,				// 요청받을 서버 주소
		data:query,				// 서버에 전송할 파라미터
		dataType:dataType,		// 서버에서 응답할 형식(json, xml, text)
		success:function(data){
			fn(data);
		},
		beforeSend:function(jqXHR){
			jqXHR.setRequestHeader("AJAX",true); // 사용자 정의 헤더
		},
		error:function(jqXHR){
			if(jqXHR.status === 403){
				login();
				return false;
			}else if(jqXHR.status===400){
				alert("요청 처리가 실패 했습니다.");
				return false;
			}
			console.log(jqXHR.responseText);
		}
	});
}

// 게시글 공감 여부
$(function () {
	$(".btnSendBoardLike").click(function () {
		const $i = $(this).find("i");
		let isNoLike = $i.css("color") == "rgb(0, 0, 0)";
		let msg = isNoLike ? "게시글에 공감하십니까?" : "게시글 공감을 취소하시겠습니까 ?";
		
		if(! confirm(msg)){
			return false;
		}
		
		let url = "${pageContext.request.contextPath}/market/insertBoardLike.do";
		let marketNum = "${dto.marketNum}";
		let qs = "marketNum="+marketNum+"&isNoLike="+isNoLike;
		
		const fn = function(data){
			let state = data.state;
			if(state === "true"){
				let color = "black";
				if( isNoLike ){
					color = "blue";
				}
				$i.css("color", color);
				
				let count = data.boardLikeCount;
				$("#boardLikeCount").text(count);
			}else if(state === "liked"){
				alert("좋아요는 한번만 가능합니다.");
			}
		};
		
		ajaxFun(url, "post", qs, "json", fn);
	});
});

<c:if test="${sessionScope.member.userId==dto.sellerId || sessionScope.member.userId=='admin'}">
	function deleteBoard() {
	    if(confirm("게시글을 삭제 하시 겠습니까 ? ")) {
		    let query = "marketNum=${dto.marketNum}&${query}";
		    let url = "${pageContext.request.contextPath}/market/delete.do?" + query;
	    	location.href = url;
	    }
	}
</c:if>

function modal() {
	const viewer = $(".buymodal");
	let userId = "${dto.sellerId}";
	let sessionId = "${sessionScope.member.userId}";
	let s;
	if(userId === sessionId){
		s ="<form name='buyForm' metod='post'>"
		s += "<table class='table table-border table-form'>"
		s += "<tr>"
		s += "<td>"
		s += "구매자 : "
		s += "</td>"
		s += "<td>"
		s += "<input type='text' name='buyerId'><br>"
		s += "</td>"
		s += "</tr>"
		s += "<tr>"
		s += "<td>"
		s +="<button type='button' class='btn' onclick='buy_ok();'>판매확정</button>"
		s += "</td>"
		s += "<td>"
		s += "</table>"
		s +="<form>"
	}else{
		s="<button type='button' class='btn' onclick='requestPay();'>결재하기</button>";
	}
	viewer.html(s);
	
	$(".dialog-modal").dialog({
		title: "결재",
		modal: true
	});
};

function imageViewer(img) {
	const viewer = $(".photo-layout");
	let s="<img src='"+img+"'>";
	viewer.html(s);
	
	$(".dialog-photo").dialog({
		title:"판매물품",
		width: 600,
		height: 530,
		modal: true
	});
}

$(function () {
	listPage(1);
});

function listPage(page) {
	let url = "${pageContext.request.contextPath}/market/listReply.do";
	let qs = "marketNum=${dto.marketNum}&pageNo="+page;
	let selector = "#listReply";
	
	const fn = function (data) {
		$(selector).html(data);
	}
	
	ajaxFun(url, "get", qs, "text", fn);
	//ajaxFun(url, "get", qs, "html", fn); //가능
	
}

$(function () {
	$(".btnSendReply").click(function name() {
		let marketNum = "${dto.marketNum}";
		const $tb = $(this).closest("table");
		let content = $tb.find("textarea").val().trim();
		
		if(! content){
			$tb.find("textarea").focus();
			return false;
		}
		content = encodeURIComponent(content);
		
		let url = "${pageContext.request.contextPath}/market/insertReply.do";
		let qs = "marketNum="+marketNum+"&content="+content+"&rereplynum=0";
		
		const fn = function (data) {
		 	$tb.find("textarea").val("");
			let state = data.state;
			if(state === "true"){
				listPage(1);
			}else{
				alert("댓글을 추가하지 못했습니다.");
			} 
		}
		
		ajaxFun(url, "post", qs, "json", fn);
	});
});

function buy_ok() {
	const f = document.buyForm;
	buyerId = f.buyerId.value.trim();
	marketNum = ${dto.marketNum};
	location.href = "${pageContext.request.contextPath}/market/buy_ok.do?buyerId="+buyerId+"&marketNum="+marketNum;
}

var IMP = window.IMP; 
IMP.init("imp85702804"); 

function requestPay() {
    IMP.request_pay({
        pg : 'html5_inicis.INIBillTst',
        pay_method : 'card',
        merchant_uid: '${sessionScope.member.userName}', 
        name : '${dto.subject}',
        amount : '${dto.price}',
        buyer_addr : '${dto.addr}',
    }, function (rsp) { // callback
        if(rsp.success){
      	  console.log(rsp);
        }else{
      	  console.log(rsp);
        }
    });
}

</script>

</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
</header>
	
<main>
	<div class="container body-container">
	    <div class="body-title">
			<h2> 글보기 </h2>
	    </div>
	    <div style="box-shadow: 0 0 15px 0 rgb(2 59 109 / 10%);border-radius: 30px; margin: 0 auto ; width: 70%; margin-bottom: 50px;">
	    <div class="body-main mx-auto">
			<table class="table table-border table-article">
				<thead>
					<tr>
						<td colspan="2" align="center">
							${dto.subject}
						</td>
					</tr>
					<tr>
						<td width="50%">
							가격 : ${dto.price}
						</td>
						<td align="right">
							거래지 : ${dto.addr}
						</td>
					</tr>
				</thead>
				
				<tbody>
					<tr>
						<td width="50%">
							이름 : ${dto.sellerId}
						</td>
						<td align="right">
							${dto.reg_date} | 조회 ${dto.hitCount}
						</td>
					</tr>
					
					<tr>
						<td colspan="2" valign="top" height="200">
							${dto.content}
						</td>
					</tr>
					
					<tr>
						<td colspan="2" align="center" style="border-bottom: 20px;">
							<button type="button" class="btn btnSendBoardLike" title="찜"> <i class="fas fa-thumbs-up" style="color:${isUserLike?'blue':'black'}"></i>&nbsp;&nbsp;<span id="boardLikeCount">${dto.zzimCount }</span> </button>
						</td>
					</tr>
					
					<tr style="border-bottom: none;">
						<td colspan="2" height="110">
							<div class="img-box">
								<c:forEach var="vo" items="${listFile}">
									<img src="${pageContext.request.contextPath}/uploads/market/${vo.imageFilename}" onclick="imageViewer('${pageContext.request.contextPath}/uploads/market/${vo.imageFilename}');">
								</c:forEach>
							</div>
						</td>	
					</tr>
					<tr>
						<td colspan="2">
							이전글 :
							<c:if test="${not empty preReadDto}">
								<a href="${pageContext.request.contextPath}/market/article.do?page=${page}&marketNum=${preReadDto.marketNum}">${preReadDto.subject}</a>
							</c:if>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							다음글 :
							<c:if test="${not empty nextReadDto}">
								<a href="${pageContext.request.contextPath}/market/article.do?page=${page}&marketNum=${nextReadDto.marketNum}">${nextReadDto.subject}</a>
							</c:if>
						</td>
					</tr>
				</tbody>
			</table>
			
			<table class="table">
				<tr>
					<td width="50%">
						<c:choose>
							<c:when test="${sessionScope.member.userId==dto.sellerId}">
								<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/market/update.do?marketNum=${dto.marketNum}&page=${page}';">수정</button>
							</c:when>
							<c:otherwise>
								<button type="button" class="btn" disabled="disabled">수정</button>
							</c:otherwise>
						</c:choose>
				    	
						<c:choose>
				    		<c:when test="${sessionScope.member.userId==dto.sellerId || sessionScope.member.userId=='admin'}">
				    			<button type="button" class="btn" onclick="deleteBoard();">삭제</button>
				    		</c:when>
				    		<c:otherwise>
				    			<button type="button" class="btn" disabled="disabled">삭제</button>
				    		</c:otherwise>
				    	</c:choose>
					</td>
					<td align="right">
					<c:choose>
						<c:when test="${sessionScope.member.userId==dto.sellerId}">
							<button type="button" class="btn" onclick="modal();">판매시작</button>
						</c:when>
						<c:when test="${dto.state == 0}">
							<button type="button" class="btn" disabled="disabled">판매완료</button>
						</c:when>
						<c:otherwise>
							<button type="button" class="btn" onclick="modal();">카드결재</button>
						</c:otherwise>
					</c:choose>	
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/market/list.do?page=${page}';">리스트</button>
					</td>
				</tr>
			</table>
			
			<div class="reply">
				<form name="replyForm" method="post">
					<div class='form-header'>
						<span class="bold">댓글쓰기</span><span> - 타인을 비방하거나 개인정보를 유출하는 글의 게시를 삼가해 주세요.</span>
					</div>
					
					<table class="table reply-form">
						<tr>
							<td>
								<textarea class='form-control' name="content"></textarea>
							</td>
						</tr>
						<tr>
						   <td align='right'>
								<button type='button' class='btn btnSendReply'>댓글 등록</button>
							</td>
						 </tr>
					</table>
				</form>
				
				<div id="listReply"></div>
			</div>

	    </div>
	    </div>
	</div>
</main>

<footer>
    <jsp:include page="/WEB-INF/views/layout/footer.jsp"></jsp:include>
</footer>

<div class="dialog-photo">
      <div class="photo-layout"></div>
</div>

<div class="dialog-modal">
      <div class="buymodal"></div>
</div>

<jsp:include page="/WEB-INF/views/layout/staticFooter.jsp"/>
</body>
</html>