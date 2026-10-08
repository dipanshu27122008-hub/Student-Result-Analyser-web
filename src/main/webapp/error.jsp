<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Application Error &bull; Student Result Analysis System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body style="background-color: var(--neutral-50); display: flex; align-items: center; justify-content: center; min-height: 100vh; padding: 20px;">

<div class="scorecard" style="max-width: 550px; text-align: center; padding: 40px 30px;">
    <div style="font-size: 56px; margin-bottom: 16px;">⚠️</div>
    <h2 style="font-size: 22px; font-weight: 800; color: var(--primary); margin-bottom: 8px;">
        An Unexpected Issue Occurred
    </h2>
    <p style="font-size: 14px; color: var(--neutral-600); margin-bottom: 24px;">
        <c:choose>
            <c:when test="${not empty pageContext.exception}">
                ${pageContext.exception.message}
            </c:when>
            <c:otherwise>
                The requested resource could not be found or a processing error occurred. Please verify your input or check the database connection configuration in <code>db.properties</code>.
            </c:otherwise>
        </c:choose>
    </p>

    <div style="display: flex; gap: 12px; justify-content: center;">
        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary">
            🏠 Return to Dashboard
        </a>
        <a href="${pageContext.request.contextPath}/login" class="btn btn-outline">
            🔐 Go to Login
        </a>
    </div>
</div>

</body>
</html>
