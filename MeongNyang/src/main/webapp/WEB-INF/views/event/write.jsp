<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>이벤트 :: 멍냥마켓</title>
<jsp:include page="/WEB-INF/views/layout/staticHeader.jsp"/>
<style type="text/css">
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
	
img-box {
	max-width: 100px;
	padding: 5px;
	box-sizing: border-box;
	display: flex;
	flex-direction: row;
	flex-wrap: nowrap;
	overflow-x: auto;
}
</style>

<script type="text/javascript">
function sendOk() {
    const f = document.boardForm;
	let str;
	
	let start_date=new Date(f.start_date.value);
	let end_date=new Date(f.end_date.value);
	let current_date=new Date(new Date().setHours(0, 0, 0, 0));
	let mode=f.mode.value;
	
    str = f.subject.value.trim();
    if(!str) {
        alert("제목을 입력하세요. ");
        f.subject.focus();
        return;
    }

    str = f.content.value.trim();
    if(!str) {
        alert("내용을 입력하세요. ");
        f.content.focus();
        return;
    }
    
    if(start_date>=end_date){
    	alert("종료일은 시작일보다 작을 수 없습니다.");
    	f.end_date.focus();
    	return;
    } else if(current_date>start_date && mode!='update'){
    	alert("현재보다 이전일을 시작일로 설정할 수 없습니다.");
    	f.start_date.focus();
    	return;
    }
    
    f.action = "${pageContext.request.contextPath}/event/${mode}_ok.do";
    f.submit();
}

<c:if test="${mode=='update'}">
function deleteFile(fileNum){
	if(!confirm("이미지를 삭제 하시겠습니까 ?")){
		return;
	}
	
	let query="eNum=${dto.eNum}&fileNum="+fileNum+"&page=${page}";
	let url = "${pageContext.request.contextPath}/event/deleteFile.do?" + query;
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
	    <div class="body-title">
			<h2><img src="${pageContext.request.contextPath}/resource/images/eventPage.png" style="width: 250px;"></h2>
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
						<td>작성자</td>
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
						<td>이벤트기간</td>
						<td>
							<p>
							 	<input type="date" name="start_date" value="${dto.start_date}"> ~ 
								<input type="date" name="end_date" value="${dto.end_date}">
								<c:if test="${mode=='update'}">
										종료 <input type="checkbox" name="enabled" value="0" ${dto.enabled == 0?'checked="checked"':''}>
								</c:if>
							</p>
						</td>
						
					</tr>
					<tr>
						<td>추첨인원</td>
						<td>
							<p>
							 	<input type="number" name="passCount" min="1" max="10" value="${dto.passCount}"> 
							</p>
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
									<c:forEach var="file" items="${listFile}">
										<img src="${pageContext.request.contextPath}/uploads/event/${file.imageFileName}"
											onclick="deleteFile('${file.fileNum}');" style="width: 15%;" >
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
							<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/event/list.do';">${mode=='update'?'수정취소':'등록취소'}</button>
								<input type="hidden" name="mode" value="${mode}">
							<c:if test="${mode=='update'}">
								<input type="hidden" name="eNum" value="${dto.eNum}">
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