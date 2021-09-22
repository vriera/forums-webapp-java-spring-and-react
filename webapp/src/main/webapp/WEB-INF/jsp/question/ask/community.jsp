<%----%><!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<html>
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

	<title>AskAway | Elegir comunidad</title>


	<!-- Icons -->
	<%--    <link href="/assets/vendor/nucleo/css/nucleo-icons.css" rel="stylesheet">--%>
	<link href="https://use.fontawesome.com/releases/v5.0.6/css/all.css" rel="stylesheet">

	<!-- BLK• CSS -->
	<%--<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css" rel="stylesheet"
		  integrity="sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6" crossorigin="anonymous">--%>
	<link type="text/css" href="<c:url value="/resources/styles/argon-design-system.css"/>" rel="stylesheet">
	<link type="text/css" href="<c:url value="/resources/styles/general.css"/>" rel="stylesheet">
	<link type="text/css" href="<c:url value="/resources/styles/stepper.css"/>" rel="stylesheet">
	<link rel="icon" href="<c:url value="/resources/images/favicon.ico"/>">
</head>
<body>

<jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>

<div class="wrapper">
	<div class="section section-hero section-shaped">
		<div class="shape shape-style-1 shape-default shape-skew viewheight-90" >
			<span class="span-150"></span>
			<span class="span-50"></span>
			<span class="span-50"></span>
			<span class="span-75"></span>
			<span class="span-100"></span>
			<span class="span-75"></span>
			<span class="span-50"></span>
			<span class="span-100"></span>
			<span class="span-50"></span>
			<span class="span-100"></span>
		</div>
		<div class="container">
			<%--Tarjeta--%>
			<div class="white-pill">
				<div class="d-flex justify-content-center">
					<p class="h1 text-primary">ELEGÍ UNA COMUNIDAD</p>
				</div>
				<hr>
				<p class="h5 text-black">La comunidad correcta va a unir tus dudas con quienes pueden darte respuestas</p>
				<%--Badges de las comunidades--%>
				<div class="container-fluid">
					<c:forEach items="${communityList}" var="community">
						<a class="btn btn-outline-primary badge-pill badge-lg my-3" href="<c:url value="/question/ask/content?communityId=${community.id}"/>">${community.name}</a>
					</c:forEach>
				</div>
				<hr>
				<%--Stepper--%>
				<div class="stepper-wrapper">
					<div class="stepper-item active">
						<div class="step-counter">1</div>
						<div class="step-name">Comunidad</div>
					</div>
					<div class="stepper-item">
						<div class="step-counter">2</div>
						<div class="step-name">Pregunta</div>
					</div>
					<div class="stepper-item">
						<div class="step-counter">3</div>
						<div class="step-name">Contacto</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>


</body>
</html>