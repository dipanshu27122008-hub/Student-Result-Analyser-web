<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<aside class="sidebar">
    <div class="sidebar-header">
        <img src="${pageContext.request.contextPath}/${empty collegeSettings.logoPath ? 'images/college-logo.png' : collegeSettings.logoPath}" 
             alt="Logo" class="sidebar-logo" 
             onerror="this.src='${pageContext.request.contextPath}/images/college-logo.png'"/>
        <div>
            <div class="sidebar-brand-title">RESULT PORTAL</div>
            <div class="sidebar-brand-sub">${empty collegeSettings.courseName ? 'BCA' : collegeSettings.courseName} Analysis</div>
        </div>
    </div>

    <nav class="sidebar-nav">
        <div class="nav-section-title">Main Menu</div>
        <a href="${pageContext.request.contextPath}/dashboard" class="nav-item ${activeNav == 'dashboard' ? 'active' : ''}">
            <span class="nav-icon">📊</span>
            <span>Dashboard</span>
        </a>
        <a href="${pageContext.request.contextPath}/upload" class="nav-item ${activeNav == 'upload' ? 'active' : ''}">
            <span class="nav-icon">📥</span>
            <span>Import Data</span>
        </a>
        <a href="${pageContext.request.contextPath}/students" class="nav-item ${activeNav == 'students' ? 'active' : ''}">
            <span class="nav-icon">👥</span>
            <span>Students</span>
        </a>
        <a href="${pageContext.request.contextPath}/student-result" class="nav-item ${activeNav == 'result' ? 'active' : ''}">
            <span class="nav-icon">🎓</span>
            <span>Individual Result</span>
        </a>

        <div class="nav-section-title">Analytics & Reports</div>
        <a href="${pageContext.request.contextPath}/analysis" class="nav-item ${activeNav == 'analysis' ? 'active' : ''}">
            <span class="nav-icon">📈</span>
            <span>Overall Analysis</span>
        </a>
        <a href="${pageContext.request.contextPath}/subject-analysis" class="nav-item ${activeNav == 'subject' ? 'active' : ''}">
            <span class="nav-icon">📚</span>
            <span>Subject Analysis</span>
        </a>
        <a href="${pageContext.request.contextPath}/import-history" class="nav-item ${activeNav == 'history' ? 'active' : ''}">
            <span class="nav-icon">🕒</span>
            <span>Import History</span>
        </a>
        <a href="${pageContext.request.contextPath}/pdf-report" class="nav-item ${activeNav == 'pdf' ? 'active' : ''}" target="_blank">
            <span class="nav-icon">📄</span>
            <span>Generate PDF</span>
        </a>

        <div class="nav-section-title">Administration</div>
        <a href="${pageContext.request.contextPath}/settings" class="nav-item ${activeNav == 'settings' ? 'active' : ''}">
            <span class="nav-icon">⚙️</span>
            <span>College Settings</span>
        </a>
    </nav>

    <div class="sidebar-footer">
        <div class="user-badge">
            <div class="user-info">
                <div class="user-name">${empty sessionScope.username ? 'Administrator' : sessionScope.username}</div>
                <div class="user-role">${empty sessionScope.role ? 'Admin' : sessionScope.role}</div>
            </div>
            <a href="${pageContext.request.contextPath}/logout" class="logout-link" title="Logout">Logout</a>
        </div>
    </div>
</aside>
