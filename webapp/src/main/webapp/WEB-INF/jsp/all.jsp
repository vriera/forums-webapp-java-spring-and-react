<%----%><!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>

<html>
<head>

	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	<meta charset="utf-8">
	<title>AskAway | Home</title>


	<!-- Icons -->
	<%--    <link href="/assets/vendor/nucleo/css/nucleo-icons.css" rel="stylesheet">--%>
	<link href="https://use.fontawesome.com/releases/v5.0.6/css/all.css" rel="stylesheet">

	<!-- BLK• CSS -->
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css" rel="stylesheet"
		  integrity="sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6" crossorigin="anonymous">
	<link type="text/css" href="<c:url value="/resources/styles/argon-design-system.css"/>" rel="stylesheet">
	<link type="text/css" href="<c:url value="/resources/styles/blk-design-system.css"/>" rel="stylesheet">
	<link type="text/css" href="<c:url value="/resources/styles/general.css"/>" rel="stylesheet">



</head>
<body>

<jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>

<div class="wrapper">
<div class="section section-hero section-shaped">
	<div class="shape shape-style-1 shape-default shape-skew">
		<span class="span-150 square1"></span>
		<span class="span-50 square2"></span>
		<span class="span-50 square3"></span>
		<span class="span-75 square4"></span>
		<span class="span-100 square5"></span>
		<span class="span-75 square6"></span>
		<span class="span-50 square7"></span>
		<span class="span-100 square3"></span>
		<span class="span-50 square2"></span>
		<span class="span-100 square4"></span>
	</div>

	<div>
		<div class="col-6 center">
			<%--TARJETA SUPERIOR--%>
			<div class="white-pill h-75 ">
				<div class="align-items-start d-flex justify-content-start my-3">
					<p class="h1 text-primary bold"><strong>AskAway</strong></p>
				</div>
				<%--BARRA DE BÚSQUEDAS--%>
				<div class="form-group mx-5">
					<form action="<c:url value="/all"/>" method="get">
						<div class="input-group">
							<input class="form-control rounded" type="search" name="query" id="query" placeholder="Buscá una pregunta acá">
							<input class="btn btn-primary" type="submit" value="Buscar">
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>

	<div class="d-flex flex-row-reverse justify-content-between align-items-end mx-3">
		<%--COMUNIDADES--%>
		<div class="white-pill mx-3 h-25 w-25 align-self-start">
			<div class="row d-flex justify-content-center">
				<p class="h1 text-primary">COMUNIDADES</p>
			</div>
			<hr>
			<%--Badges de las comunidades--%>
			<div class="container-fluid">
				<c:forEach items="${communityList}" var="community">
					<a class="btn btn-outline-primary badge-pill badge-lg my-3" href="<c:url value="/community?community_id=${community.id}"/>">${community.name}</a>
				</c:forEach>
			</div>
		</div>

		<%--PREGUNTAS--%>
		<div class="white-pill ml-30 w-50 align-self-start">
			<div class="row d-flex justify-content-center">
				<p class="h1 text-primary">PREGUNTAS</p>
			</div>
			<hr>
			<c:if test="${questionList.size() == 0}">
			<div class="row d-flex justify-content-center mb-5">
				<p class="h1 text-gray">No encontramos nada :(</p>
				<img class="w-25 h-25" src="<c:url value="/resources/images/empty.png"/>" alt="No hay nada para mostrar">
			</div>
			</c:if>
			<div class="overflow-auto">
				<c:forEach items="${questionList}" var="question">
					<div class="card p-3 m-3">
						<div class="row">
							<p class="h2 text-primary">${question.title}</p>
							<div class="col-12 text-wrap-ellipsis">
								<p class="h5">${question.body}</p>
							</div>
						</div>
					</div>
				</c:forEach>
			</div>
		</div>
	</div>


</div>
</div>


</body>
</html>
