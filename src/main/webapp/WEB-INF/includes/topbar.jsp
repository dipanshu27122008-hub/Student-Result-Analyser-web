<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<header class="topbar">
    <div class="topbar-institution">
        <div>
            <div class="college-title">${empty collegeSettings.collegeName ? 'YOUR COLLEGE NAME' : collegeSettings.collegeName}</div>
            <div class="college-dept">Department of ${empty collegeSettings.departmentName ? 'YOUR DEPARTMENT' : collegeSettings.departmentName} &bull; ${empty collegeSettings.courseName ? 'BCA' : collegeSettings.courseName} (${empty collegeSettings.semester ? 'Semester' : collegeSettings.semester})</div>
        </div>
    </div>
    <div class="topbar-right">
        <span class="badge-academic-year">AY ${empty collegeSettings.academicYear ? '2026-27' : collegeSettings.academicYear}</span>
        <a href="${pageContext.request.contextPath}/pdf-report" class="btn btn-sm btn-primary" target="_blank">
            📄 Download Report
        </a>
    </div>
</header>
