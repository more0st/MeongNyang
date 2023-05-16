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

.table-form td { padding: 7px 0; }
.table-form p { line-height: 200%; }
.table-form tr:first-child { border-top: 2px solid white; }
.table-form tr > td:first-child { width: 110px; text-align: center; background: white; font-weight: 700; color : tomato;}
.table-form tr > td:nth-child(2) { padding-left: 10px; }

.table-form input[type=text], .table-form input[type=file], .table-form textarea {
	width: 96%; }
	
.img-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, 65px);
	grid-gap: 5px;
}

.img-grid .item {
    object-fit: cover; /* 가로세로 비율은 유지하면서 컨테이너에 꽉 차도록 설정 */
    width: 65px;
    height: 65px;
	cursor: pointer;
}

.img-box {
	max-width: 600px;

	box-sizing: border-box;
	display: flex; /* 자손요소를 flexbox로 변경 */
	flex-direction: row; /* 정방향 수평나열 */
	flex-wrap: nowrap;
	overflow-x: auto;
}
.img-box img {
	width: 65px; height: 65px;
	margin-right: 5px;
	flex: 0 0 auto;
	cursor: pointer;
}
</style>

<script type="text/javascript">
function sendOk() {
    const f = document.photoForm;
	let str;
	
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

    str = f.price.value.trim();
    if(!str) {
        alert("내용을 입력하세요. ");
        f.price.focus();
        return;
    }
	
    f.action = "${pageContext.request.contextPath}/market/${mode}_ok.do";
    f.submit();
}

$(function(){
	var sel_files = [];
	
	$("body").on("click", ".table-form .img-add", function(event){
		$("form[name=photoForm] input[name=selectFile]").trigger("click"); 
	});
	
	$("form[name=photoForm] input[name=selectFile]").change(function(){
		if(! this.files) {
			let dt = new DataTransfer();
			for(let file of sel_files) {
				dt.items.add(file);
			}
			document.photoForm.selectFile.files = dt.files;
			
	    	return false;
	    }
	    
        for(let file of this.files) {
        	sel_files.push(file);
        	
            const reader = new FileReader();
			const $img = $("<img>", {class:"item img-item"});
			$img.attr("data-filename", file.name);
            reader.onload = e => {
            	$img.attr("src", e.target.result);
            };
			reader.readAsDataURL(file);
            
            $(".img-grid").append($img);
        }
		
		let dt = new DataTransfer();
		for(let file of sel_files) {
			dt.items.add(file);
		}
		document.photoForm.selectFile.files = dt.files;		
	});
	
	$("body").on("click", ".table-form .img-item", function(event) {
		if(! confirm("선택한 파일을 삭제 하시겠습니까 ?")) {
			return false;
		}
		
		let filename = $(this).attr("data-filename");
		
	    for(let i = 0; i < sel_files.length; i++) {
	    	if(filename === sel_files[i].name){
	    		sel_files.splice(i, 1);
	    		break;
			}
	    }
	
		let dt = new DataTransfer();
		for(let file of sel_files) {
			dt.items.add(file);
		}
		document.photoForm.selectFile.files = dt.files;
		
		$(this).remove();
	});
});

<c:if test="${mode=='update'}">
function deleteFile(fileNum) {
	if(! confirm("이미지를 삭제 하시겠습니까 ?")) {
		return;
	}
	
	let query = "marketNum=${dto.marketNum}&fileNum=" + fileNum + "&page=${page}";
	let url = "${pageContext.request.contextPath}/market/deleteFile.do?" + query;
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
			<h2> 내 물건 팔기 </h2>
	    </div>
			<form name="photoForm" method="post" enctype="multipart/form-data">
				<table class="table table-border table-form">
					<tr> 
						<td>글&nbsp;제목</td>
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
						<td>￦가격</td>
						<td> 
							<input type="text" name="price" class="form-control">
						</td>
					</tr>
					
					<tr> 
						<td>희망&nbsp;거래&nbsp;장소</td>
						<td> 
							<input type="text" name="addr" class="form-control">
						</td>
					</tr>
					
					<tr> 
						<td valign="top">내&nbsp;&nbsp;&nbsp;&nbsp;용</td>
						<td> 
							<textarea name="content" class="form-control">${dto.content}</textarea>
						</td>
					</tr>
					
					<tr> 
						<td>이미지</td>
						<td>
							<div class="img-grid"><img class="item img-add" src="${pageContext.request.contextPath}/resource/images/add_photo.png"></div>
							<input type="file" name="selectFile" accept="image/*" multiple="multiple" style="display: none;" class="form-control">
						</td>
					</tr>
					
					<c:if test="${mode=='update'}">
						<tr>
							<td>등록이미지</td>
							<td> 
								<div class="img-box">
									<c:forEach var="vo" items="${listFile}">
										<img src="${pageContext.request.contextPath}/uploads/market/${vo.imageFilename}"
											onclick="deleteFile('${vo.fileNum}');">
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
							<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/market/list.do';">${mode=='update'?'수정취소':'등록취소'}</button>
							<c:if test="${mode=='update'}">
								<input type="hidden" name="marketNum" value="${dto.marketNum}">
								<input type="hidden" name="page" value="${page}">
							</c:if>
						</td>
					</tr>
				</table>
		
			</form>
	    </div>
</main>

<footer>
    <jsp:include page="/WEB-INF/views/layout/footer.jsp"></jsp:include>
</footer>

<jsp:include page="/WEB-INF/views/layout/staticFooter.jsp"/>
</body>
</html>