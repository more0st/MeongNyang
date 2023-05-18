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

.img-box img {
	width: 37px; height: 37px;
	margin-right: 5px;
	flex: 0 0 auto;
	cursor: pointer;
}

.body-main {
	max-width: 700px;
	padding-top: 15px;
}

.table-form td { padding: 7px 0; }
.table-form p { line-height: 200%; }
.table-form tr:first-child { border-top: 2px solid white; }
.table-form tr > td:first-child { width: 110px; text-align: center; background: white; font-weight: 700; color : tomato;}
.table-form tr > td:nth-child(2) { padding-left: 10px; }

.table-form input[type=text], .table-form input[type=file], .table-form textarea {
	width: 96%; }
</style>

<script type="text/javascript">
function sendOk() {
    const f = document.boardForm;
	let str;
	
    str = f.subject.value.trim();
    if(!str) {
        alert("제목을 입력하세요. ");
        f.subject.focus();
        return;
    }

    
    str = f.clubName.value.trim();
    if(!str) {
        alert("모임명을 입력하세요. ");
        f.clubName.focus();
        return;
    }
    
    str = f.content.value.trim();
    if(!str) {
        alert("내용을 입력하세요. ");
        f.content.focus();
        return;
    }
    

    str = f.maxMember.value.trim();
    if(!str) {
        alert("인원수를 입력하세요. ");
        f.maxMember.focus();
        return;
    }
    
    if( !/^\d+$/.test(str) ) {
        alert("숫자만 가능합니다. ");
        f.maxMember.focus();
        return;
    }

    
    let mode = "${mode}";
    let cnt = "${dto.nowMember}";
    if(cnt == "") cnt = "0";

    
    if(mode=="update" && parseInt(str) < parseInt(cnt) ){
    	alert("현재 정원수 보다 적습니다. ");
        f.maxMember.focus();
        return;
    }

    f.action = "${pageContext.request.contextPath}/club/${mode}_ok.do";
    f.submit();
}
    
    
<c:if test="${mode=='update'}">
	function deleteFile(fileNum) {
		if(! confirm("이미지를 삭제 하시겠습니까 ?")) {
			return;
		}
		
		let query = "num=${dto.clubNum}&fileNum=" + fileNum + "&page=${page}";
		let url = "${pageContext.request.contextPath}/club/deleteFile.do?" + query;
		location.href = url;
}
</c:if>

</script>
</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
</header>
	
<main>

	<div class="container body-container">
	    <div class="body-title" style="text-align: center;">
			<a href="${pageContext.request.contextPath}/club/list.do';">
				<img src="${pageContext.request.contextPath}/resource/images/clubPage.png" style="width: 250px;">
			</a>
	    </div>
	    <div style="box-shadow: 0 0 15px 0 rgb(2 59 109 / 10%);border-radius: 30px; margin: 0 auto ; width: 70%;">
	    <div class="body-main mx-auto">
			<form name="boardForm" method="post" enctype="multipart/form-data">
				<table class="table table-border table-form">
					<tr> 
						<td>제&nbsp;&nbsp;&nbsp;&nbsp;목</td>
						<td> 
							<input type="text" name="subject" maxlength="100" class="form-control" value="${dto.subject}">
						</td>
					</tr>
					
					<tr> 
						<td>모임명</td>
						<td> 
							<input type="text" name="clubName" maxlength="100" class="form-control" value="${dto.clubName }">
						</td>
					</tr>
					
					<tr> 
						<td>모임장</td>
						<td> 
							<p>${sessionScope.member.userName}</p>
						</td>
					</tr>
					
					<tr> 
						<td valign="top">내&nbsp;&nbsp;&nbsp;&nbsp;용</td>
						<td> 
							<textarea name="content" class="form-control">${dto.content}</textarea>
						</td>
					</tr>
					<tr> 
						<td>모임 정원수</td>
						<td> 
							<input type="text" name="maxMember" maxlength="100" class="form-control" value="${dto.maxMember }">
						</td>
					</tr>
					
					
					
					<tr> 
						<td>이미지</td>
						<td> 
							<input type="file" name="selectFile" accept="image/*" multiple="multiple" class="form-control">
						</td>
					</tr>
					
					<c:if test="${mode=='update'}">
						<tr>
							<td>등록이미지</td>
							<td> 
								<div class="img-box">
									<c:forEach var="vo" items="${listFile}">
										<img src="${pageContext.request.contextPath}/uploads/club/${vo.imageFilename}"
											onclick="deleteFile('${vo.fileNum}');" >
									</c:forEach>
								</div>
							</td>
						</tr>
					</c:if>
					
				</table>
					
				<table class="table">
					<tr> 
						<td align="center">
							<button type="button" class="btn" onclick="sendOk();">${mode=='update'?'수정완료':'등록하기'}</button>
							<button type="reset" class="btn">다시입력</button>
							<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/club/list.do';">${mode=='update'?'수정취소':'등록취소'}</button>
							<c:if test="${mode=='update'}">
								<input type="hidden" name="clubNum" value="${dto.clubNum}">
								<input type="hidden" name="page" value="${page}">
							</c:if>
						</td>
					</tr>
				</table>
		
			</form>

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