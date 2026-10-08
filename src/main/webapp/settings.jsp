<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="activeNav" value="settings" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>College Settings &bull; Student Result Analysis System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/includes/sidebar.jsp"/>

    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/includes/topbar.jsp"/>

        <main class="content-body">
            <div class="page-header">
                <div>
                    <h1 class="page-title">Institution &amp; Project Settings</h1>
                    <p class="page-subtitle">Configure college branding, department, course, and official report headers</p>
                </div>
            </div>

            <!-- Feedback Alerts -->
            <c:if test="${not empty successMessage}">
                <div class="alert alert-success alert-dismissible">
                    <span>✅</span>
                    <div>${successMessage}</div>
                </div>
            </c:if>

            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger alert-dismissible">
                    <span>⚠️</span>
                    <div>${errorMessage}</div>
                </div>
            </c:if>

            <c:if test="${not empty warningMessage}">
                <div class="alert alert-warning alert-dismissible">
                    <span>⚠️</span>
                    <div>${warningMessage}</div>
                </div>
            </c:if>

            <div class="table-card">
                <div class="table-toolbar">
                    <div class="table-title">Academic Institutional Metadata</div>
                </div>

                <div style="padding: 24px;">
                    <form action="${pageContext.request.contextPath}/settings" method="post" enctype="multipart/form-data">
                        
                        <!-- Logo Configuration Row -->
                        <div style="display: flex; align-items: center; gap: 24px; padding-bottom: 24px; margin-bottom: 24px; border-bottom: 1px solid var(--neutral-200);">
                            <img src="${pageContext.request.contextPath}/${empty collegeSettings.logoPath ? 'images/college-logo.png' : collegeSettings.logoPath}" 
                                 alt="College Logo" 
                                 style="width: 90px; height: 90px; border-radius: 50%; object-fit: cover; border: 2px solid var(--neutral-300); padding: 4px; background: white;"
                                 onerror="this.src='${pageContext.request.contextPath}/images/college-logo.png'"/>
                            <div style="flex: 1;">
                                <label style="font-size: 14px; font-weight: 700; color: var(--neutral-800); display: block; margin-bottom: 4px;">
                                    Institutional Seal / Emblem Logo
                                </label>
                                <p style="font-size: 13px; color: var(--neutral-600); margin-bottom: 8px;">
                                    Upload a square PNG or JPG image (recommended size: 250x250 px). Used on the dashboard and generated PDF reports.
                                </p>
                                <input type="file" name="logoFile" accept=".png, .jpg, .jpeg" class="form-control" style="max-width: 380px;">
                            </div>
                        </div>

                        <!-- Metadata Fields Grid -->
                        <div class="settings-grid">
                            <div class="form-group">
                                <label for="collegeName">College / University Name *</label>
                                <input type="text" id="collegeName" name="collegeName" class="form-control"
                                       value="${collegeSettings.collegeName}" required>
                                <small style="color: var(--neutral-600); font-size: 11px;">Example: ABC Institute of Technology &amp; Management</small>
                            </div>

                            <div class="form-group">
                                <label for="departmentName">Department Name *</label>
                                <input type="text" id="departmentName" name="departmentName" class="form-control"
                                       value="${collegeSettings.departmentName}" required>
                                <small style="color: var(--neutral-600); font-size: 11px;">Example: Computer Science &amp; Applications</small>
                            </div>

                            <div class="form-group">
                                <label for="courseName">Course / Degree Program *</label>
                                <input type="text" id="courseName" name="courseName" class="form-control"
                                       value="${collegeSettings.courseName}" required>
                                <small style="color: var(--neutral-600); font-size: 11px;">Example: BCA / B.Tech CSE / B.Sc IT</small>
                            </div>

                            <div class="form-group">
                                <label for="semester">Semester / Term *</label>
                                <input type="text" id="semester" name="semester" class="form-control"
                                       value="${collegeSettings.semester}" required>
                                <small style="color: var(--neutral-600); font-size: 11px;">Example: Semester IV / 4th Sem</small>
                            </div>

                            <div class="form-group">
                                <label for="academicYear">Academic Year *</label>
                                <input type="text" id="academicYear" name="academicYear" class="form-control"
                                       value="${collegeSettings.academicYear}" required>
                                <small style="color: var(--neutral-600); font-size: 11px;">Example: 2026-27</small>
                            </div>

                            <div class="form-group">
                                <label for="projectTitle">Project Title / Header *</label>
                                <input type="text" id="projectTitle" name="projectTitle" class="form-control"
                                       value="${collegeSettings.projectTitle}" required>
                                <small style="color: var(--neutral-600); font-size: 11px;">Header displayed on reports and scorecards</small>
                            </div>
                        </div>

                        <div style="margin-top: 24px; padding-top: 16px; border-top: 1px solid var(--neutral-200); display: flex; justify-content: flex-end; gap: 12px;">
                            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-outline">Cancel</a>
                            <button type="submit" class="btn btn-primary" style="padding: 10px 24px;">
                                💾 Save Settings
                            </button>
                        </div>
                    </form>
                </div>
            </div>

        </main>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>
