<%----%><!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>

<html>
<head>

	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	<meta charset="utf-8">
	<title>AskAway | Todo</title>


	<!-- Icons -->
	<%--    <link href="/assets/vendor/nucleo/css/nucleo-icons.css" rel="stylesheet">--%>
	<link href="https://use.fontawesome.com/releases/v5.0.6/css/all.css" rel="stylesheet">

	<!-- BLK• CSS -->
	<%--<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css" rel="stylesheet"
		  integrity="sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6" crossorigin="anonymous">--%>
	<link type="text/css" href="<c:url value="/resources/styles/argon-design-system.css"/>" rel="stylesheet">
	<link type="text/css" href="<c:url value="/resources/styles/blk-design-system.css"/>" rel="stylesheet">
	<link type="text/css" href="<c:url value="/resources/styles/general.css"/>" rel="stylesheet">
	<link rel="icon" href="<c:url value="/resources/images/favicon.ico"/>">


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
		<%--TARJETA SUPERIOR--%>
		<div class="col-6 center">
			<div class="white-pill h-75 ">
				<div class="align-items-start d-flex justify-content-center my-3">
					<p class="h1 text-primary bold"><strong>AskAway</strong></p>
				</div>
				<%--BARRA DE BÚSQUEDAS--%>
				<div class="form-group mx-5">
					<form action="<c:url value="/community/view/all"/>" method="get">
						<div class="input-group">
							<input class="form-control rounded" type="search" name="query" id="query" placeholder="Buscá una pregunta acá">
							<input class="btn btn-primary" type="submit" value="Buscar">
						</div>
						<c:if test="${query != null}">
							<p class="h4">Resultados para: ${query}</p>
						</c:if>
					</form>
				</div>

			</div>
		</div>
	</div>

		<div class="row">
			<%--COMUNIDADES--%>
			<div class="col-3 ">
				<div class="white-pill mt-5 ml-3">
					<div class="card-body">
						<p class="h3 text-primary text-center">COMUNIDADES</p>
						<hr>
						<%--Badges de las comunidades--%>
						<div class="container-fluid">
							<c:forEach items="${communityList}" var="community">
								<a class="btn btn-outline-primary badge-pill badge-lg my-3" href="<c:url value="/community/view/${community.id}"/>">${community.name}</a>
							</c:forEach>
						</div>
					</div>
				</div>

			</div>

			<%--PREGUNTAS--%>
			<div class="col-6">
				<div class="white-pill mt-5">
					<div class="card-body">
						<p class="h2 text-primary text-center">PREGUNTAS</p>
						<hr>
						<c:if test="${questionList.size() == 0}">
							<p class="row h1 text-gray">No encontramos nada :(</p>
							<div class="d-flex justify-content-center">
								<img class="row w-25 h-25" src="<c:url value="/resources/images/empty.png"/>" alt="No hay nada para mostrar">
							</div>
						</c:if>
						<div class="overflow-auto">
							<c:forEach items="${questionList}" var="question">
								<a class="d-block" href="<c:url value="/question/view/${question.id}"/>">
								<div class="card p-3 m-3 shadow-sm--hover ">
										<div class="row">
											<div class="d-flex flex-column justify-content-start ml-3">
												<div class="h2 text-primary">${question.title}</div>
												<p><span class="badge badge-primary badge-pill">${question.community.name}</span></p>
											</div>
											<div class="col-12 text-wrap-ellipsis">
												<p class="h5">${question.body}</p>
											</div>
										</div>
								</div>
								</a>
							</c:forEach>
						</div>
					</div>
				</div>
			</div>

			<%--HACER PREGUNTA--%>
			<div class="col-3">
				<div class="white-pill mt-5 mr-3">
					<div class="card-body">
						<p class="h3 text-primary text-center">¿TENES DUDAS?</p>
						<hr>
						<p class="h5 my-3">Enviá una pregunta a nuestros distintos foros para que la comunidad la responda.</p>
						<a class="btn btn-primary" href="<c:url value="/question/ask/community"/>">Preguntar</a>
					</div>
				</div>
			</div>

		</div>


</div>
</div>


</body>
</html>
