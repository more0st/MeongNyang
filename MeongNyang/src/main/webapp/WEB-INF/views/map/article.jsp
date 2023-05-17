<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>spring</title>
<jsp:include page="/WEB-INF/views/layout/staticHeader.jsp" />
<style type="text/css">
.body-main {
	max-width: 700px;
	padding-top: 15px;
}

.img-box {
	max-width: 700px;
	padding: 5px;
	overflow-x: auto;
}

.img-box img {
	width: 350px;
	height: 300px;
	margin-right: 5px;
	flex: 0 0 auto;
	cursor: pointer;
}

/* 모달대화상자 */
.ui-widget-header { /* 타이틀바 */
	background: none;
	border: none;
	border-bottom: 1px solid #ccc;
	border-radius: 0;
}

.ui-dialog .ui-dialog-title {
	padding-top: 5px;
	padding-bottom: 5px;
}

.ui-widget-content { /* 내용 */
	/* border: none; */
	border-color: #ccc;
}

.photo-layout img {
	width: 570px;
	height: 450px;
}

.table-article tr>td {
	padding-left: 5px;
	padding-right: 5px;
}

.map_wrap {
	position: relative;
	width: 100%;
	height: 350px;
}

.kkotitle {
	font-weight: bold;
	display: block;
}

.hAddr {
	position: absolute;
	left: 10px;
	top: 10px;
	border-radius: 2px;
	background: #fff;
	background: rgba(255, 255, 255, 0.8);
	z-index: 1;
	padding: 5px;
}

#centerAddr {
	display: block;
	margin-top: 2px;
	font-weight: normal;
}

.bAddr {
	padding: 5px;
	text-overflow: ellipsis;
	overflow: hidden;
	white-space: nowrap;
}
</style>

</head>
<body>

	<header>
		<jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
	</header>

	<main>
		<div class="container body-container">
			<div class="body-title" style="text-align: center;">
				<img
					src="${pageContext.request.contextPath}/resource/images/mappage.png"
					style="width: 250px;">
			</div>
			<div
				style="box-shadow: 0 0 15px 0 rgb(2 59 109/ 10%); border-radius: 30px; margin: 0 auto; width: 70%; margin-bottom: 50px;">
				<div class="body-main mx-auto">
					<table class="table table-border table-article">
						<thead>
							<tr>
								<td colspan="2" align="center">${dto.subject}</td>
							</tr>
						</thead>

						<tbody>
							<tr>
								<td width="50%">이름 : ${dto.userName}</td>
								<td align="right">${dto.reg_date} | 조회 ${dto.hitCount}</td>
							</tr>

							<tr>
								<td colspan="2" valign="top" height="200"><c:out value="${dto.content}" /></td>
							</tr>

							<tr style="border-bottom: none; text-align: center;">
								<td colspan="2" height="110">
									<div class="img-box">
										<c:forEach var="vo" items="${listFile}">
											<img
												src="${pageContext.request.contextPath}/uploads/map/${vo.imageFilename}"
												onclick="imageViewer('${pageContext.request.contextPath}/uploads/map/${vo.imageFilename}');">
										</c:forEach>
									</div>
								</td>
							</tr>



							<tr>
								<td style="text-align: center;">
									<div id="map" style="width: 100%; height: 350px;"></div>
								</td>
							</tr>

							<tr>
								<td colspan="2">이전글 : <c:if test="${not empty preReadDto}">
										<a
											href="${pageContext.request.contextPath}/map/article.do?${query}&num=${preReadDto.mapNum}">${preReadDto.subject}</a>
									</c:if>
								</td>
							</tr>
							<tr>
								<td colspan="2">다음글 : <c:if test="${not empty nextReadDto}">
										<a
											href="${pageContext.request.contextPath}/map/article.do?${query}&num=${nextReadDto.mapNum}">${nextReadDto.subject}</a>
									</c:if>
								</td>
							</tr>
							<tr>
								<td colspan="2" align="center" style="border-bottom: 20px;">
									<button type="button" class="btn btnSendBoardLike" title="좋아요">
										<i class="fas fa-thumbs-up"
											style="color:${isUserLike?'blue':'black'}"></i>&nbsp;&nbsp;<span
											id="boardLikeCount">${dto.boardLikeCount}</span>
									</button>
								</td>
							</tr>

						</tbody>
					</table>

					<table class="table">
						<tr>
							<td width="50%"><c:choose>
									<c:when test="${sessionScope.member.userId==dto.userId}">
										<button type="button" class="btn"
											onclick="location.href='${pageContext.request.contextPath}/map/update.do?num=${dto.mapNum}&page=${page}';">수정</button>
									</c:when>
									<c:otherwise>
										<button type="button" class="btn" disabled="disabled">수정</button>
									</c:otherwise>
								</c:choose> <c:choose>
									<c:when
										test="${sessionScope.member.userId==dto.userId || sessionScope.member.userId=='admin'}">
										<button type="button" class="btn" onclick="deleteBoard();">삭제</button>
									</c:when>
									<c:otherwise>
										<button type="button" class="btn" disabled="disabled">삭제</button>
									</c:otherwise>
								</c:choose></td>


							<td align="right">
								<button type="button" class="btn"
									onclick="location.href='${pageContext.request.contextPath}/map/list.do?${query}';">리스트</button>
							</td>
						</tr>
					</table>

				</div>
			</div>
		</div>


		<!-- 댓글 폼 -->
		<div class="body-container">

			<div class="reply">
				<form name="replyForm" method="post">
					<div class='form-header'>
						<span class="bold">댓글쓰기</span><span> - 타인을 비방하거나 개인정보를 유출하는
							글의 게시를 삼가해 주세요.</span>
					</div>

					<table class="table reply-form">
						<tr>
							<td><textarea class='form-control' name="content"
									style="height: 120px;"></textarea></td>
						</tr>
						<tr>
							<td align='right'>
								<button type='button' class='btn btnSendReply'>댓글 등록</button>
							</td>
						</tr>
					</table>
				</form>
				<div id="listReply">
					<c:forEach var="vo" items="${listReply}">
					        <!-- 댓글을 표시하는 HTML 코드 -->
					</c:forEach>
				</div>
				<div>
					<div class="page-navigation">${paging}</div>
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

	<jsp:include page="/WEB-INF/views/layout/staticFooter.jsp" />

	<script type="text/javascript"
		src="//dapi.kakao.com/v2/maps/sdk.js?appkey=01680d6fa50eecc6cbcc23d4888731c2&libraries=services,clusterer,drawing"></script>

	<script>
	var mapContainer = document.getElementById('map'), // 지도를 표시할 div 
	mapOption = { 
	    center: new kakao.maps.LatLng(${dto.addr}), // 지도의 중심좌표
	    level: 3 // 지도의 확대 레벨
	};
	
	var map = new kakao.maps.Map(mapContainer, mapOption); // 지도를 생성합니다
	
	//마커가 표시될 위치입니다 
	var markerPosition  = new kakao.maps.LatLng(${dto.addr}); 
	
	//마커를 생성합니다
	var marker = new kakao.maps.Marker({
	position: markerPosition
	});
	
	//마커가 지도 위에 표시되도록 설정합니다
	marker.setMap(map);


	//주소-좌표 변환 객체를 생성합니다
	var geocoder = new kakao.maps.services.Geocoder();

	var marker = new kakao.maps.Marker(), // 클릭한 위치를 표시할 마커입니다
	infowindow = new kakao.maps.InfoWindow({zindex:1}); // 클릭한 위치에 대한 주소를 표시할 인포윈도우입니다

	//현재 지도 중심좌표로 주소를 검색해서 지도 좌측 상단에 표시합니다
	searchAddrFromCoords(map.getCenter(), displayCenterInfo);

	//지도를 클릭했을 때 클릭 위치 좌표에 대한 주소정보를 표시하도록 이벤트를 등록합니다
	kakao.maps.event.addListener(map, 'click', function(mouseEvent) {
	searchDetailAddrFromCoords(mouseEvent.latLng, function(result, status) {
	    if (status === kakao.maps.services.Status.OK) {
	        var detailAddr = !!result[0].road_address ? '<div>도로명주소 : ' + result[0].road_address.address_name + '</div>' : '';
	        detailAddr += '<div>지번 주소 : ' + result[0].address.address_name + '</div>';
	        
	        var content = '<div class="bAddr">' +
	                        '<span class="title">상세 주소</span>' + 
	                        detailAddr + 
	                    '</div>';

	        // 마커를 클릭한 위치에 표시합니다 
	        
	        var markerPosition  = new kakao.maps.LatLng(${dto.addr}); 
		
			//마커를 생성합니다
			var marker = new kakao.maps.Marker({
			position: markerPosition
			});
		
			//마커가 지도 위에 표시되도록 설정합니다
			marker.setMap(map);


	        // 인포윈도우에 클릭한 위치에 대한 법정동 상세 주소정보를 표시합니다
	        infowindow.setContent(content);
	        infowindow.open(map, marker);
	    }   
	});
	});

	//중심 좌표나 확대 수준이 변경됐을 때 지도 중심 좌표에 대한 주소 정보를 표시하도록 이벤트를 등록합니다
	kakao.maps.event.addListener(map, 'idle', function() {
	searchAddrFromCoords(map.getCenter(), displayCenterInfo);
	});

	function searchAddrFromCoords(coords, callback) {
	// 좌표로 행정동 주소 정보를 요청합니다
	geocoder.coord2RegionCode(coords.getLng(), coords.getLat(), callback);         
	}

	function searchDetailAddrFromCoords(coords, callback) {
	// 좌표로 법정동 상세 주소 정보를 요청합니다
	geocoder.coord2Address(coords.getLng(), coords.getLat(), callback);
	}

	//지도 좌측상단에 지도 중심좌표에 대한 주소정보를 표출하는 함수입니다
	function displayCenterInfo(result, status) {
	if (status === kakao.maps.services.Status.OK) {
	    var infoDiv = document.getElementById('centerAddr');

	    for(var i = 0; i < result.length; i++) {
	        // 행정동의 region_type 값은 'H' 이므로
	        if (result[i].region_type === 'H') {
	            break;
	        }
	    }
	}    
	}
	
	

	
	// 아래 코드는 지도 위의 마커를 제거하는 코드입니다
	// marker.setMap(null);    
	</script>
	
	
<script type="text/javascript">
// 댓글 좋아요
function login() {
	location.href = "${pageContext.request.contextPath}/member/login.do";
}

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

// 게시글 공감 여부 
$(function() {
	$(".btnSendBoardLike").click(function() {
		const $i = $(this).find("i");
		let isNoLike = $i.css("color") == "rgb(0, 0, 0)";
		let msg = isNoLike ? "게시글에 공감하십니까 ?" : "게시글 공감을 취소하시겠습니까 ?"; 
		
		if(! confirm(msg)) {
			return false; 
		}
		
		let url = "${pageContext.request.contextPath}/map/insertBoardLike.do";
		let num = "${dto.mapNum}"; 
		let qs = "num=" + num + "&isNoLike=" + isNoLike;
		
		const fn = function(data) {
			let state = data.state;
			if(state === "true") {
				let color = "black";
				if( isNoLike) {
					color = "blue";
				}
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

// 댓글 리스트 및 페이징
$(function() {
	listPage(1);
});

function listPage(page) {
	let url = "${pageContext.request.contextPath}/map/listReply.do";
	let qs = "mapNum=${dto.mapNum}&pageNo="+page;
	let selector = "#listReply";
	
	const fn = function(data) {
		$(selector).html(data);	
	}
	
	ajaxFun(url, "get", qs, "text", fn);
	//ajaxFun(url, "get", qs, "html", fn);	// 가능

}


// 댓글 등록 
$(function() {
	$(".btnSendReply").click(function() {
		let mapNum = "${dto.mapNum}";
		const $td = $(this).closest("table");
		let content = $td.find("textarea").val().trim();
		
		if(! content) {
			$td.find("textarea").focus(); 
			return false;
		}
		content = encodeURIComponent(content);
		
		let url = "${pageContext.request.contextPath}/map/insertReply.do";
		let qs = "num="+mapNum+"&content="+content+"&originalReplyNum=0";
		
		const fn = function(data) {
			$td.find("textarea").val("");
			
			let state = data.state;
			if(state === "true"){
				listPage(1);
			} else {
				alert("댓글을 추가하지 못했습니다.");
			}
		}
		
		ajaxFun(url,"post",qs,"json",fn);
		
	});
});


//답글 등록 버튼
$(function () {
	$("#listReply").on("click", ".btnSendReplyAnswer", function () {
		let num = "${dto.mapNum}";
		let replyNum = $(this).attr("data-replyNum");
		const $td = $(this).closest("td");
		
		let content = $td.find("textarea").val().trim();
		if(! content) {
			$td.find("textarea").focus();
			return false;
		}
		content = encodeURIComponent(content);
		
		let url = "${pageContext.request.contextPath}/bbs/insertReply.do";
		let qs = "mapNum=" +num+ "&content=" +content+ "&originalReplyNum=" +replyNum;
		
		const fn = function (data) {
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





<c:if test="${sessionScope.member.userId==dto.userId || sessionScope.member.userId=='admin'}">
	function deleteBoard() {
	    if(confirm("게시글을 삭제 하시 겠습니까 ? ")) {
		    let query = "mapNum=${dto.mapNum}&${query}";
		    let url = "${pageContext.request.contextPath}/map/delete.do?" + query;
	    	location.href = url;
	    }
	}
</c:if>

</script>
</body>
</html>