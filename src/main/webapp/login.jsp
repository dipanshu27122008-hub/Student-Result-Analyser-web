<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login &bull; Student Result Analysis System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="login-page">

<div class="login-card">
    <div class="login-header">
        <img src="${pageContext.request.contextPath}/${empty collegeSettings.logoPath ? 'images/college-logo.png' : collegeSettings.logoPath}"
             alt="Logo" class="login-logo"
             onerror="this.src='${pageContext.request.contextPath}/images/college-logo.png'"/>
        <h2 class="login-title">${empty collegeSettings.collegeName ? 'YOUR COLLEGE NAME' : collegeSettings.collegeName}</h2>
        <div class="login-sub">${empty collegeSettings.projectTitle ? 'Student Result Analysis System' : collegeSettings.projectTitle}</div>
    </div>

    <div class="login-body">
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">
                <span>⚠️</span>
                <div>${errorMessage}</div>
            </div>
        </c:if>

        <c:if test="${param.logout == 'true'}">
            <div class="alert alert-info">
                <span>ℹ️</span>
                <div>You have been successfully logged out.</div>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="post">
            <div class="form-group">
                <label for="username">Username</label>
                <input type="text" id="username" name="username" class="form-control"
                       placeholder="Enter username" value="${usernameVal}" required autofocus autocomplete="username">
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" class="form-control"
                       placeholder="Enter password" required autocomplete="current-password">
            </div>

            <button type="submit" class="btn btn-primary" style="width: 100%; justify-content: center; padding: 10px; margin-top: 10px;">
                Secure Sign In
            </button>
        </form>

        <div class="demo-credentials">
            <strong>Demo Administrator Credentials:</strong><br>
            Username: <code>admin</code> &bull; Password: <code>admin123</code>
        </div>
    </div>
</div>

</body>
</html>
