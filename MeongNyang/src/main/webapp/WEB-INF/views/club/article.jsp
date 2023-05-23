<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>멍냥모임</title>
<jsp:include page="/WEB-INF/views/layout/staticHeader.jsp"/>

<style type="text/css">
.fa-thumbs-up, i {font-size: 30px;}

.body-main {
	max-width: 700px;
	padding-top: 15px;
}

/* 모달대화상자 */
.ui-widget-header { /* 타이틀바 */
	background: none;
	border: none;
	border-bottom: 1px solid #ccc;
	border-radius: 0;
}
.ui-dialog .ui-dialog-title {
	padding-top: 5px; padding-bottom: 5px; 
}
.ui-widget-content { /* 내용 */
   /* border: none; */
   border-color: #ccc; 
}

.table-article tr>td { padding-left: 5px; padding-right: 5px; }

.img-box {
	max-width: 700px;
	padding: 5px;
	box-sizing: border-box;
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

.photo-layout img { width: 570px; height: 450px; }

.bold { font-weight: bold;}


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



</style>

<script type="text/javascript">
<c:if test="${sessionScope.member.userId==dto.userId || sessionScope.member.userId=='admin'}">
	function deleteBoard() {
	    if(confirm("모임을 삭제 하시 겠습니까 ? ")) {
		    let query = "num=${dto.clubNum}&${query}";
		    let url = "${pageContext.request.contextPath}/club/delete.do?" + query;
	    	location.href = url;
	    }
	}
</c:if>

function join(){
	if(confirm("가입 하시겠습니까 ? ")){
		location.href='${pageContext.request.contextPath}/club/signUp.do?num=${dto.clubNum}';
	}
}

function bye(){
	if(confirm("탈퇴 하시겠습니까 ? ")){
		location.href='${pageContext.request.contextPath}/club/byebye.do?num=${dto.clubNum}&val=${val}';
	}
}

function listOrMy(){
	
	let val = "${val}";
    if(val === "") val = false;
	
	if(val){
		location.href='${pageContext.request.contextPath}/club/my.do?${query}';
	}else {
		location.href='${pageContext.request.contextPath}/club/list.do?${query}';
		
	}

}


//해당되는 객체가 숨겨져있는지 아닌지 확인하는 함수
const isHidden = ele => {
	const styles = window.getComputedStyle(ele); //인자로 넘겨 받은 요소의 모든 css 속성 값을 담은 객체 반환
	return styles.display === 'none' || styles.visibility === 'hidden'; //숨겨져있는지 아닌지 확인
	
};


$(function(){
	$(".showMember").click(function(){
		$(".popup-dialog").dialog({
			title:"현재 가입한 멤버"
		});
	});
});

function imageViewer(img) {
	const viewer = $(".photo-layout");
	let s="<img src='"+img+"'>";
	viewer.html(s);
	
	$(".dialog-photo").dialog({
		title:"이미지",
		width: 600,
		height: 530,
		modal: true
	});
}

</script>




<script type="text/javascript">

function login(){
	location.href = " ${pageContext.request.contextPath}/member/login.do ";
}


//좋아요 기능 구현
function ajaxFun(url,method,query,dataType,fn){//3.
	$.ajax({
		type : method,	//메소드(요청방식)-> (get, post, put, delete)
		url : url,		//요청 받을 서버 주소
		data : query,	//서버에 전송할 파라미터
		dataType : dataType,	//서버에서 응답하는 형식(json, xml, text)
		success : function(data){ //성공 , data = json
			fn(data);
		},
		beforeSend : function(jqXHR){ //서버전송 전
			jqXHR.setRequestHeader("AJAX",true); // 사용자 정의 헤더
		},
		error : function(jqXHR){
			if(jqXHR.status === 403){
				login();
				return false;
			}else if(jqXHR.status === 400){
				alert("요청 처리가 실패 했습니다.");
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
		
		let url = "${pageContext.request.contextPath}/club/insertBoardLike.do";
		let num = "${dto.clubNum}"; 
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

function listPage(page){
	let url = "${pageContext.request.contextPath}/club/listReply.do";
	let qs = "num=${dto.clubNum}&pageNo="+page;
	let selector = "#listReply";
	
	const fn = function(data){
		$(selector).html(data);
	}
	
	ajaxFun(url, "get", qs, "text", fn);
		//ajaxFun(url,"get"qs,"html",fn); //가능
}
	
	
//댓글 등록
$(function(){
	$(".btnSendReply").click(function(){
		let num = "${dto.clubNum}";
		const $tb = $(this).closest("table");
		let content = $tb.find("textarea").val().trim();
		
		if(! content){
			$tb.find("textarea").focus();
			return false;
		}
		
		content = encodeURIComponent(content);
		
		let url = "$(pageContext.request.contextPath)/club/insertReply.do";
		let qs = "num="+num+"&content="+content+"&answer=0";
		
		const fn = function(data){
			$tb.find("textarea").val("");
			let state = data.state;
			if(state === "true"){
				listPage(1);
			}else {
				alert("댓글을 추가하지 못했습니다.");
			}
		}
		
		ajaxFun(url,"post",qs,"json",fn);
		
	});
});


//댓글의 답글 리스트
function listReplyAnswer(answer){
	let url = "${pageContext.request.contextPath}/club/listReplyAnswer.do";
	let qs = "answer="+answer;
	let selector = "#listReplyAnswer"+answer;
	
	const fn =  function(data){
		$(selector).html(data);
	};
	
	ajaxFun(url,"get",qs,"text",fn);
	
}

//댓글별 답글 개수
function countReplyAnswer(answer){
	let url = "${pageContext.request.contextPath}/club/countReplyAnswer.do";
	let qs = "answer="+answer;
	
	const fn = function(data){
		let count = data.count;
		let selector = "#answerCount"+answer;
		$(selector).html(count);
	};
		
	ajaxFun(url,"post",qs,"json",fn);
}



//답글 버튼(댓글별 답글 등록 폼 및 답글 리스트)
$(function(){
	$("#listReply").on("click",".btnReplyAnswerLayout",function(){
		
		const $trAnswer = $(this).closest("tr").next();
		
		let isVisible = $trAnswer.is(":visible");
		let replyNum = $(this).attr("data-replyNum");
		
		if(isVisible){
			$trAnswer.hide();
		}else{
			$trAnswer.show();//답글 창이 열릴때
			
			//답글 리스트
			listReplyAnswer(replyNum);
			
			//답글 개수
			countReplyAnswer(replyNum);
			
		}
		
		
	});
});


//답글 등록 버튼
$(function(){
	$("#listReply").on("click",".btnSendReplyAnswer",function(){
		
		let num = "${dto.clubNum}";
		let replyNum = $(this).attr("data-replyNum");
		const $td = $(this).closest("td");
		
		let content = $td.find("textarea").val().trim();
		if(! content){
			$td.find("textarea").focus();
			return false;
		}
		
		content = encodeURIComponent(content);
		
		let url = "${pageContext.request.contextPath}/club/insertReply.do";
		let qs = "num="+num+"&content="+content+"&answer="+replyNum;
		

		const fn =  function(data){
			let state = data.state;
			
			$td.find("textarea").val("");
			
			if(state === "true"){
				listReplyAnswer(replyNum);
				countReplyAnswer(replyNum);
				
			}
		};
		
		ajaxFun(url,"post",qs,"json",fn);
		
	});
});

//댓글 삭제
$(function(){
	$("#listReply").on("click",".deleteReply",function(){
		if(! confirm("게시글을 삭제 하시겠습니까?")){
			return false;
		}
		
		let replyNum = $(this).attr("data-replyNum");
		let page = $(this).attr("data-pageNo");
		
		let url = "${pageContext.request.contextPath}/club/deleteReply.do";
		let qs = "replyNum="+replyNum;
		
		const fn = function(data){
			listPage(page);
		};
		
		ajaxFun(url,"post",qs,"json",fn);
		
	});
});


//댓글의 답글 삭제
$(function(){
	$("#listReply").on("click",".deleteReplyAnswer",function(){
		if(! confirm("게시글을 삭제 하시겠습니까?")){
			return false;
		}
		
		let replyNum = $(this).attr("data-replyNum");
		let answer = $(this).attr("data-answer");
		
		let url = "${pageContext.request.contextPath}/club/deleteReply.do";
		let qs = "replyNum="+replyNum;
		
		const fn = function(data){
			listReplyAnswer(answer);
			countReplyAnswer(answer);
		};
		
		ajaxFun(url,"post",qs,"json",fn);
		
	});
});


</script>

</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
</header>
	
<main>
	<div class="container body-container" >
	    <div class="body-title" style="text-align: center;">
			<a href="${pageContext.request.contextPath}/club/list.do';">
				<img src="${pageContext.request.contextPath}/resource/images/clubPage.png" style="width: 250px;">
			</a>
	    </div>
	    <div style="box-shadow: 0 0 15px 0 rgb(2 59 109 / 10%);border-radius: 30px; margin: 0 auto ; width: 70%; margin-bottom: 50px;">
	    <div class="body-main mx-auto">
			<table class="table table-border table-article">
				<thead>
					<tr style="border-top: 2px solid #eee; border-bottom: 2px solid #eee;">
						<td colspan="2" align="center">
							<h3>${dto.subject} </h3>
						</td>
					</tr>
				</thead>
				
				<tbody>
					<tr>
						<td><span class="bold">모임명</span> : ${dto.clubName }</td>
						<td align="right" >
							${dto.reg_date} | 조회수 ${dto.hitCount} 
						</td>
					</tr>
					<tr>
						<td width="50%">
							<span class="bold">모임장</span> : ${dto.userName}
						</td>
						
					</tr>
					
						<tr>
						<td width="50%">
							<span class="bold">인원</span> : ${memberCount}/${dto.maxMember }
						</td>
						
					</tr>
					
					<tr>
						<td colspan="2" valign="top" height="200">
							${dto.content}
						</td>
					</tr>
					
					
					<tr style="border-bottom: none;">
						<td colspan="2" height="110">
							<div class="img-box">
								<c:forEach var="vo" items="${listFile}">
									<img src="${pageContext.request.contextPath}/uploads/club/${vo.imageFilename}"
										onclick="imageViewer('${pageContext.request.contextPath}/uploads/club/${vo.imageFilename}');">
								</c:forEach>
							</div>
						</td>	
					</tr>
					
				
					
					
					<tr>
						<td colspan="2">
							<span class="bold">이전글 : </span>
							<c:if test="${not empty preReadDto}">
								<a href="${pageContext.request.contextPath}/club/article.do?${query}&num=${preReadDto.clubNum}">${preReadDto.subject}</a>
							</c:if>
							<c:if test="${empty preReadDto}">
								<span>이전글이 없습니다.</span>
							</c:if>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<span class="bold">다음글 : </span>
							<c:if test="${not empty nextReadDto}">
								<a href="${pageContext.request.contextPath}/club/article.do?${query}&num=${nextReadDto.clubNum}">${nextReadDto.subject}</a>
							</c:if>
							<c:if test="${empty nextReadDto}">
								<span>다음글이 없습니다.</span>
							</c:if>
						</td>
					</tr>
					
					
				
				</tbody>
			</table>
			
			<table class="table">
				<tr>
					<td width="30%">
						<c:choose>
							<c:when test="${sessionScope.member.userId==dto.userId}">
								<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/club/update.do?num=${dto.clubNum}&page=${page}';">수정</button>
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
						<c:choose>
				    		<c:when test="${ result }">
								<button type="button" class="btn showMember" >멤버보기</button>
				    		</c:when>
				    	</c:choose>
						
						<!--가입하기버튼 정원수초과하면 비활성화 -> 현재수>=정원수, 멤버일 경우 비활성화->멤버리스트가 있으면(not empty)
						  -->
						<c:choose>
				    		<c:when test="${ memberCount < dto.maxMember && !result }">
				    			<button type="button" class="btn" onclick="join();">가입하기</button>
				    		</c:when>
				    	</c:choose>
							<!-- 탈퇴하기 버튼 가입한멤버이면(리더빼고) 활성화 해야함 -->
							
						<c:choose>
				    		<c:when test="${ result && status != 1 }">
				    			<button type="button" class="btn" onclick="bye();">탈퇴하기</button>
				    		</c:when>
				    	</c:choose>
				    	
					</td>
					<td align="right">
						<button type="button" class="btn" onclick="listOrMy();">리스트</button>
					</td>
					
					<td style="width: 10%">
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
			</table>
			
				<input type="hidden" name="num" value="${dto.clubNum }">
				<input type="hidden" name="val" value="${val}">
				
					
					<!-- 멤버리스트 -->
					<div class=" popup-dialog" style="display: none;">
							<c:forEach var="list" items="${list }">
								<c:if test="${list.status=='1' }">
									<p style="font-size: 15px;"><i class="fa-solid fa-crown" style="color: #f2eb1c; font-size: 15px;"></i>&nbsp;&nbsp;${list.userName }</p>
								</c:if>
							</c:forEach>
							<c:forEach var="list" items="${list }">
								<c:if test="${list.status=='0' }">
									<p style="font-size: 15px;"><i class="fa-solid fa-user" style="color: #fd855d; font-size: 15px;"></i>&nbsp;&nbsp;${list.userName }</p>
								</c:if>
							</c:forEach>
					</div>

	    </div>
	    </div>
	</div>
	
	
	<!-- 댓글 쓰기폼 -->
	<c:choose>
		<c:when test="${ result }">
			<div class="body-container">
				<div class="reply">
					<form name="replyForm" method="post">
						<div class='form-header'>
							<span class="bold">댓글쓰기</span><span> - 타인을 비방하거나 개인정보를 유출하는 글의 게시를 삼가해 주세요.</span>
						</div>
						
						<table class="table reply-form">
							<tr>
								<td>
									<textarea class='form-control' name="content" style="height: 130px;"></textarea>
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
		</c:when>
		<c:otherwise>
			<div class="body-container">
				<div class="reply">
					<form name="replyForm" method="post">
						<div class='form-header'>
							<span class="bold">댓글쓰기</span><span> - 타인을 비방하거나 개인정보를 유출하는 글의 게시를 삼가해 주세요.</span>
						</div>
						
						<table class="table reply-form">
							<tr>
								<td>
									<textarea class='form-control' name="content" style="height: 130px;" disabled="disabled">댓글쓰기 기능은 모임멤버만 참여 할 수 있습니다.</textarea>
								</td>
							</tr>
							<tr>
							   <td align='right'>
									<button type='button' class='btn btnSendReply' disabled="disabled">댓글 등록</button>
								</td>
							 </tr>
						</table>
					</form>
					
				</div>
			</div>
		</c:otherwise>
	</c:choose>

	
</main>

<footer>
    <jsp:include page="/WEB-INF/views/layout/footer.jsp"></jsp:include>
</footer>

<div class="dialog-photo">
      <div class="photo-layout"></div>
</div>

<jsp:include page="/WEB-INF/views/layout/staticFooter.jsp"/>
</body>
</html>