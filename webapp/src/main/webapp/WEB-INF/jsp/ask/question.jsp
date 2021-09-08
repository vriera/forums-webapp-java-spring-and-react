<%----%><!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<html>
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

	<title>AskAway | Crear pregunta</title>


	<!-- Icons -->
	<%--    <link href="/assets/vendor/nucleo/css/nucleo-icons.css" rel="stylesheet">--%>
	<link href="https://use.fontawesome.com/releases/v5.0.6/css/all.css" rel="stylesheet">

	<!-- BLK• CSS -->
	<<%--link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css" rel="stylesheet"
		  integrity="sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6" crossorigin="anonymous">--%>
	<link type="text/css" href="<c:url value="/resources/styles/argon-design-system.css"/>" rel="stylesheet">
	<link type="text/css" href="<c:url value="/resources/styles/general.css"/>" rel="stylesheet">
	<link type="text/css" href="<c:url value="/resources/styles/stepper.css"/>" rel="stylesheet">
</head>
<body>

<jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>

<div class="wrapper">
	<div class="section section-hero section-shaped">
		<div class="shape shape-style-1 shape-default shape-skew viewheight-90">
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


		<div class="row">
			<%--OTRAS COMUNIDADES--%>
			<div class="col-3">
				<div class="white-pill mt-5 ml-3">
					<div class="card-body">
						<p class="h3 text-primary">OTRAS COMUNIDADES</p>
						<hr>
						<%--BADGES--%>
						<div class="container-fluid">
							<a class="btn btn-light badge-pill badge-lg my-3"
							   href="<c:url value="/community/view?communityId=${community.id}"/>">${community.name}</a>
							<c:forEach items="${communityList}" var="community">
								<a class="btn btn-outline-primary badge-pill badge-lg my-3"
								   href="<c:url value="/community/view?communityId=${community.id}"/>">${community.name}</a>
							</c:forEach>
						</div>
					</div>
				</div>
			</div>

			<%--PREGUNTA PRINCIPAL--%>
			<div class="col-6">
				<div class="white-pill mt-5">
					<div class="card-body">
						<div class="d-flex justify-content-center">
							<p class="h1 text-primary">${question.title}</p>
						</div>
						<div>
							<p class="h5">${question.body}</p>
						</div>
						<%--<div class=" p-3 m-3">
                            <div class="row">
                                <div class="d-flex justify-content-center">
                                    <p class="h1 text-primary">${question.title}</p>
                                </div>
                                <div class="col-12 text-wrap-ellipsis">
                                    <p class="h5">${question.body}</p>
                                </div>
                            </div>
                        </div>--%>
						<hr>
						<div class="d-flex justify-content-center">
							<p class="h3 text-primary">RESPUESTAS</p>
						</div>
						<c:if test="${answerList.size() == 0}">
							<p class="row h3 text-gray mx-3">No encontramos nada :(</p>
							<div class="d-flex justify-content-center">
								<img class="row w-25 h-25" src="<c:url value="/resources/images/empty.png"/>"
									 alt="No hay nada para mostrar">
							</div>
						</c:if>
						<div class="overflow-auto">
							<c:forEach items="${answerList}" var="answer">
								<div class="card p-3 m-3 shadow-sm--hover">
									<div class="row">
										<div class="col-12 text-wrap-ellipsis">
											<p class="h5">${answer.body}</p>
										</div>
									</div>
								</div>
							</c:forEach>
						</div>
					</div>
				</div>
			</div>

			<%--HACER PREGUNTA--%>
			<div class="col-3">
				<div class="white-pill mt-5 mr-3">
					<div class="card-body">
						<c:url value="/question/${question.id}" var="postPath"/>
						<form:form method="POST" modelAttribute="AnswersForm" action="${postPath}">
							<div class="form-group">
								<form:label path="body">Tu Respuesta</form:label>
								<form:textarea path="body" class="form-control" id="body" rows="3"></form:textarea>
							</div>
							<div>
								<div class="mt-3 d-flex justify-content-center">
									<p class="h4 text-primary">Contacto</p>
								</div>
								<div class="row d-flex justify-content-center">
									<form:label path="name" for="name">Nombre</form:label>
									<form:input path="name" class="form-control" id="name"></form:input>
									<div/>
									<div class="row d-flex justify-content-center mt-3">
										<form:label path="email" for="email">Email</form:label>
										<form:input path="email" class="form-control" id="email"></form:input>
									</div>
								</div>
							</div>
						</form:form>
						<div class="d-flex justify-content-center mb-3 mt-3">
							<button type="submit" class="btn btn-primary">Enviar</button>
						</div>
					</div>
				</div>
			</div>
		</div>

	</div>


</div>



</body>
</html>