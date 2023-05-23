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
	max-width: 90%;
	padding-top: 15px;
}

.img-box{
	width:80%;
	height: auto;
}

.img-box img{
	max-width:100%;
	display: block;
}

.table-article tr>td { padding-left: 5px; padding-right: 5px; }


.user-wrap {
	width: 100%;
	margin: 10px auto;
	position: relative;
}
.user-wrap img {
	width: 100%;
	vertical-align: middle;
}
.user-text {
	position: absolute;
	top: 45%;
	left: 50%;
	width: 100%;
	transform: translate( -50%, -50% );
	font-weight: bold;
    font-size: 15px;
    font-family: 'ypseo';
    text-align:center;
    color: white;
}


/* 댓글 */
.reply {
	clear: both; padding: 20px 0 10px;
}
.reply .bold {
	font-weight: 900;
}

.reply .form-header {
	padding-bottom: 7px;
}
.reply-form tr>td {
	padding: 2px 0 2px;
}
.reply-form textarea {
	width: 100%; height: 75px; resize: none;
	border-radius: 30px;
}
.reply-form button {
	padding: 8px 25px;
}

.reply .reply-info {
	padding-top: 25px; padding-bottom: 7px;
}
.reply .reply-info  .reply-count {
	color: tomato; font-weight: 900; 
}

.reply .reply-list tr>td {
	padding: 7px 5px;
}
.reply .reply-list .bold {
	font-weight: 600;
}

.reply .deleteReply, .reply .deleteReplyAnswer {
	cursor: pointer;
}
.reply .notifyReply {
	cursor: pointer;
}

.reply-list .list-header {
	border: 1px solid white; background: #ffedea;
	border-radius: 50px; 
	
}
.reply-list tr>td {
	padding-left: 7px; padding-right: 7px;
}

.reply-answer {
	display: none;
}
.reply-answer .answer-left {
	float: left; width: 5%;
}
.reply-answer .answer-right {
	float: left; width: 95%;
	border-radius: 30px;
}
.reply-answer .answer-list {
	border-top: 1px solid #cccccc; padding: 0 10px 7px;
}
.reply-answer .answer-form {
	clear: both; padding: 3px 10px 5px;
}
.reply-answer .answer-form textarea {
	width: 100%; height: 75px; resize: none;
	border-radius: 30px;
}
.reply-answer .answer-footer {
	clear: both; padding: 0 13px 10px 10px; text-align: right;
}

.answer-article {
	clear: both;
}
.answer-article .answer-article-header {
	clear: both; padding-top: 5px;
}
.answer-article .answer-article-body {
	clear:both; padding: 5px 5px; border-bottom: 1px solid #cccccc;
}

</style>
<script type="text/javascript">
<c:if test="${sessionScope.member.userId==dto.userId || sessionScope.member.userId=='admin'}">
	function deleteBoard() {
	    if(confirm("게시글을 삭제 하시 겠습니까 ? ")) {
		    let query = "num=${dto.photoNum}&page=${page}";
		    let url = "${pageContext.request.contextPath}/gallery/delete.do?" + query;
	    	location.href = url;
	    }
	}
</c:if>

//해당되는 객체가 숨겨져있는지 아닌지 확인하는 함수
const isHidden = ele => {
	const styles = window.getComputedStyle(ele); //인자로 넘겨 받은 요소의 모든 css 속성 값을 담은 객체 반환
	return styles.display === 'none' || styles.visibility === 'hidden'; //숨겨져있는지 아닌지 확인
	
};


function ajaxFun(url, method, query, dataType, fn) {
	$.ajax({
		type:method,	// 메소드(get, post, put, delete)
		url:url,		// 요청받을 서버주소 
		data:query,		// 서버에 전송할 파라미터 
		dataType:dataType,	// 서버에서 응답할 형식(json, xml, text)
		success:function(data){
			fn(data);
		},
		beforeSend:function(jqXHR){
			jqXHR.setRequestHeader("AJAX", true);	// 사용자 정의 헤더
		},
		error:function(jqXHR){
			if(jqXHR.status === 403) {
				login();
				return false;
			} else if(jqXHR.status === 400) {
				alert("요청 처리가 실패했습니다.");
				return false;
			}
			console.log(jqXHR.responseText);
		}
	});
}

//게시글 공감 여부 
$(function() {
	$(".btnSendBoardLike").click(function() {
		const $i = $(this).find('img');
		console.log($i.css("color"));
		let isNoLike = $i.css("color") == "rgb(0, 0, 0)";
		let msg = isNoLike ? "게시글에 공감하십니까 ?" : "게시글 공감을 취소하시겠습니까 ?"; 
		
		if(! confirm(msg)) {
			return false; 
		}
		
		let url = "${pageContext.request.contextPath}/gallery/insertBoardLike.do";
		let num = "${dto.photoNum}"; 
		let qs = "num=" + num + "&isNoLike=" + isNoLike;
		
		const fn = function(data) {
			let state = data.state;
			if(state === "true") {
				let img = "heart";
				let color = "black";
				if( isNoLike) {
					img = "heart_red";
					color = "blue";
				}
				$i.attr("src", "${pageContext.request.contextPath}/resource/images/"+img+".png");
				$i.css("color", color);
				
				let count = data.boardLikeCount;
				$("#boardLikeCount").text(count);
				
			} else if (state === "liked") {
				alert("좋아요는 한번만 가능합니다.");
			}
		};
		
		ajaxFun(url, "post", qs, "json", fn);
		
	});
});






//댓글 리스트 및 페이징
$(function(){
	listPage(1);
});

function listPage(page) {
	let url = "${pageContext.request.contextPath}/gallery/listReply.do";
	let qs = "num=${dto.photoNum}&pageNo="+page;
	let selector = "#listReply";
	
	const fn = function(data) {
		$(selector).html(data);	
	}
	
	ajaxFun(url, "get", qs, "text", fn);
	// ajaxFun(url, "get", qs, "html", fn); // 가능
}

//댓글 등록
$(function(){
	$(".btnSendReply").click(function(){
		let num = "${dto.photoNum}";
		const $tb = $(this).closest("table");
		let content = $tb.find("textarea").val().trim();
		
		if(! content) {
			$tb.find("textarea").focus();
			return false;
		}
		content = encodeURIComponent(content);
		
		let url = "${pageContext.request.contextPath}/gallery/insertReply.do";
		let qs = "num="+num+"&content="+content+"&answer=0";
		
		const fn = function(data) {
			$tb.find("textarea").val("");
			
			let state = data.state;
			if(state === "true") {
				listPage(1);
			} else {
				alert("댓글을 추가하지 못했습니다.");
			}
		}
		
		ajaxFun(url, "post", qs, "json", fn);
		
	});
});

//댓글의 답글 리스트
function listReplyAnswer(answer) {
	let url = "${pageContext.request.contextPath}/gallery/listReplyAnswer.do";
	let qs = "answer="+answer;
	let selector = "#listReplyAnswer"+answer;
	
	const fn = function(data) {
		$(selector).html(data);
	};
	
	ajaxFun(url, "get", qs, "text", fn);
}

//댓글별 답글 개수
function countReplyAnswer(answer) {
	let url = "${pageContext.request.contextPath}/gallery/countReplyAnswer.do";
	let qs = "answer="+answer;
	
	const fn = function(data) {
		let count = data.count;
		let selector = "#answerCount"+answer;
		$(selector).html(count);
	};
	ajaxFun(url, "post", qs, "json", fn);
}

//답글 버튼(댓글별 답글 등록 폼 및 답글 리스트)
$(function(){
	$("#listReply").on("click", ".btnReplyAnswerLayout", function() {
		const $trAnswer = $(this).closest("tr").next();
		
		let isVisible = $trAnswer.is(":visible");
		let replyNum = $(this).attr("data-replyNum");
		
		if(isVisible) {
			$trAnswer.hide();
		} else {
			$trAnswer.show();
			
			// 답글 리스트
			listReplyAnswer(replyNum);
			
			// 답글 개수
			countReplyAnswer(replyNum);
		}
	});
});

//답글 등록 버튼
$(function(){
	$("#listReply").on("click", ".btnSendReplyAnswer", function(){
		let num = "${dto.photoNum}";
		let replyNum = $(this).attr("data-replyNum");
		const $td = $(this).closest("td");
		
		let content = $td.find("textarea").val().trim();
		if(! content) {
			$td.find("textarea").focus();
			return false;
		}
		content = encodeURIComponent(content);
		
		let url = "${pageContext.request.contextPath}/gallery/insertReply.do";
		let qs = "num="+num+"&content="+content+"&answer="+replyNum;
		
		const fn = function(data) {
			let state = data.state;
			
			$td.find("textarea").val("");
			
			if(state === "true") {
				listReplyAnswer(replyNum);
				countReplyAnswer(replyNum);
			}
		};
		
		ajaxFun(url, "post", qs, "json", fn);
	});
});

//댓글 삭제
$(function(){
	$("#listReply").on("click", ".deleteReply", function(){
		if(! confirm("게시글을 삭제하시겠습니까 ? ")) {
			return false;
		}
		
		let replyNum = $(this).attr("data-replyNum");
		let page = $(this).attr("data-pageNo");
		
		let url = "${pageContext.request.contextPath}/gallery/deleteReply.do";
		let qs = "replyNum="+replyNum;
		
		const fn = function(data) {
			listPage(page);
		};
		
		ajaxFun(url, "post", qs, "json", fn);
	});
});

//댓글의 답글 삭제
$(function(){
	$("#listReply").on("click", ".deleteReplyAnswer", function(){
		if(! confirm("게시글을 삭제하시겠습니까 ? ")) {
			return false;
		}
		
		let replyNum = $(this).attr("data-replyNum");
		let answer = $(this).attr("data-answer");
		
		let url = "${pageContext.request.contextPath}/gallery/deleteReply.do";
		let qs = "replyNum="+replyNum;
		
		const fn = function(data) {
			listReplyAnswer(answer);
			countReplyAnswer(answer);
		};
		
		ajaxFun(url, "post", qs, "json", fn);
	});
});


</script>

</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
</header>
	
<main>
	<div class="container body-container">
	    <div class="body-title">
			<h2>
			<a href="${pageContext.request.contextPath}/gallery/list.do">
			<img src="${pageContext.request.contextPath}/resource/images/gallerypage2.png" style="width: 280px;" >
			</a>
			</h2>
	    </div>
	    <div style="box-shadow: 0 0 15px 0 rgb(2 59 109 / 10%);border-radius: 30px; margin: 0 auto ; width: 90%; margin-bottom: 50px;">
	    <div class="body-main mx-auto">
			<table class="table table-border table-article">
				<thead>
					<tr style="border-top: 2px solid #eee; border-bottom: 2px solid #eee;">
						<td colspan="2" align="center">
							<h3>${dto.subject}</h3>
						</td>
					</tr>
				</thead>
				
				<tbody>
					<tr>
						<td width="50%">
							이름 : ${dto.userName}
						</td>
						<td align="right" style="border-bottom: 2px;">
							${dto.reg_date} | 조회 ${dto.hitCount}
						</td>
					</tr>
					
					<tr style="border-bottom: none;">
						<td colspan="2" style="padding-bottom: 0;">
							<div class="img-box">
								<c:forEach var="vo" items="${listFile}">
								<div>
									<img src="${pageContext.request.contextPath}/uploads/gallery/${vo.imageFilename}">
								</div>
								</c:forEach>
							</div>
						</td>
					</tr>
					
					<tr>
						<td colspan="2" valign="top" style="margin-bottom:100px;">
							${dto.content}
						</td>
					</tr>
					
					<tr>
						<td colspan="2" align="center" style="border-bottom: 20px;">
							<!-- 
							<button type="button" class="btn btnSendBoardLike" title="좋아요"><i class="fas fa-thumbs-up" style="color:${isUserLike?'blue':'black'}"></i>&nbsp;&nbsp;<span id="boardLikeCount">${dto.boardLikeCount}</span></button>
							 -->
							<button type="button" class="btn btnSendBoardLike" title="좋아요" style="background: white">
								<div class="user-wrap">
									<div class="user-image">
								    <c:choose>
										<c:when test="${isUserLike == true}">
												<img src="${pageContext.request.contextPath}/resource/images/heart_red.png" style="width: 40px; color:blue;" >										
										</c:when>
										<c:otherwise>
												<img src="${pageContext.request.contextPath}/resource/images/heart.png" style="width: 40px; color:black;" >										
										</c:otherwise>
									</c:choose>
								    </div>
								    <div class="user-text" id="boardLikeCount">
								        <p>${dto.boardLikeCount}</p>
								    </div>
								    
								</div>
							</button>
						</td>
					</tr>
					
					<tr>
						<td colspan="2">
							이전글 :
							<c:if test="${not empty preReadDto}">
								<a href="${pageContext.request.contextPath}/gallery/article.do?${query}&num=${preReadDto.photoNum}">${preReadDto.subject}</a>
							</c:if>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							다음글 :
							<c:if test="${not empty nextReadDto}">
								<a href="${pageContext.request.contextPath}/gallery/article.do?${query}&num=${nextReadDto.photoNum}">${nextReadDto.subject}</a>
							</c:if>
						</td>
					</tr>
				</tbody>
			</table>
			
			<table class="table">
				<tr>
					<td width="50%">
						<c:choose>
							<c:when test="${sessionScope.member.userId==dto.userId}">
								<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/gallery/update.do?num=${dto.photoNum}&page=${page}';">수정</button>
							</c:when>
							<c:otherwise>
								<button type="button" class="btn" disabled="disabled">수정</button>
							</c:otherwise>
						</c:choose>
				    	
						<c:choose>
				    		<c:when test="${sessionScope.member.userId==dto.userId || sessionScope.member.userId=='admin'}">
				    			<button type="button" class="btn" onclick="deleteBoard();">삭제</button>
				    		</c:when>
				    		<c:otherwise>
				    			<button type="button" class="btn" disabled="disabled">삭제</button>
				    		</c:otherwise>
				    	</c:choose>
					</td>
					<td align="right">
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/gallery/list.do?${query}';">리스트</button>
					</td>
				</tr>
			</table>

					<div class="reply">
						<form name="replyForm" method="post">
							<div class='form-header'>
								<span class="bold">댓글쓰기</span><span> - 타인을 비방하거나 개인정보를
									유출하는 글의 게시를 삼가해 주세요.</span>
							</div>

							<table class="table reply-form">
								<tr>
									<td><textarea class='form-control' name="content"></textarea>
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

<jsp:include page="/WEB-INF/views/layout/staticFooter.jsp"/>
</body>
</html>