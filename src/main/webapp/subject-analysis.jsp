<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="activeNav" value="subject" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Subject Analysis &bull; Student Result Analysis System</title>
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
                    <h1 class="page-title">Subject-Wise Performance Analysis</h1>
                    <p class="page-subtitle">Detailed evaluation across the four primary curriculum subjects (out of 100 marks each)</p>
                </div>
                <div style="display: flex; gap: 10px;">
                    <a href="${pageContext.request.contextPath}/analysis" class="btn btn-outline">📈 Overall Analysis</a>
                    <a href="${pageContext.request.contextPath}/pdf-report" class="btn btn-primary" target="_blank">📄 Download PDF Report</a>
                </div>
            </div>

            <!-- Subject-wise Analysis Summary Cards -->
            <div class="kpi-grid">
                <c:forEach var="entry" items="${subjectMap}">
                    <div class="kpi-card ${entry.value.passPercentage >= 75 ? 'success' : (entry.value.passPercentage >= 50 ? 'info' : 'danger')}">
                        <div class="kpi-label">${entry.key}</div>
                        <div class="kpi-value">${entry.value.averageMarks}</div>
                        <div class="kpi-subtext">Avg Marks &bull; Pass Rate: <strong>${entry.value.passPercentage}%</strong></div>
                    </div>
                </c:forEach>
            </div>

            <!-- Subject Analysis Data Table -->
            <div class="table-card">
                <div class="table-toolbar">
                    <div class="table-title">Course Analytics Breakdown</div>
                    <span style="font-size: 13px; color: var(--neutral-600);">Pass criterion: &ge; 40 marks</span>
                </div>
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Subject Name</th>
                                <th class="text-center">Average Marks</th>
                                <th class="text-center">Highest Marks</th>
                                <th class="text-center">Lowest Marks</th>
                                <th class="text-center">Passed Students</th>
                                <th class="text-center">Failed Students</th>
                                <th class="text-center">Pass %</th>
                                <th class="text-center">Subject Health</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="entry" items="${subjectMap}">
                                <tr>
                                    <td class="fw-bold">${entry.key}</td>
                                    <td class="text-center fw-bold">${entry.value.averageMarks} / 100</td>
                                    <td class="text-center" style="color: #22543d; font-weight: 700;">${entry.value.highestMarks}</td>
                                    <td class="text-center" style="color: #742a2a; font-weight: 700;">${entry.value.lowestMarks}</td>
                                    <td class="text-center">
                                        <span class="badge badge-pass">${entry.value.passedStudents}</span>
                                    </td>
                                    <td class="text-center">
                                        <span class="badge ${entry.value.failedStudents > 0 ? 'badge-fail' : 'badge-pass'}">
                                            ${entry.value.failedStudents}
                                        </span>
                                    </td>
                                    <td class="text-center fw-bold">${entry.value.passPercentage}%</td>
                                    <td class="text-center">
                                        <c:choose>
                                            <c:when test="${entry.value.passPercentage >= 85}">
                                                <span class="badge badge-pass">Excellent</span>
                                            </c:when>
                                            <c:when test="${entry.value.passPercentage >= 70}">
                                                <span class="badge badge-pass">Good</span>
                                            </c:when>
                                            <c:when test="${entry.value.passPercentage >= 50}">
                                                <span class="badge badge-cat">Moderate</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-fail">Critical</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Diagnostic Insights / Remedial Suggestions -->
            <div class="chart-card">
                <div class="chart-card-header">
                    <div class="chart-card-title">💡 Pedagogical Insights &amp; Observations</div>
                </div>
                <div style="font-size: 13.5px; color: var(--neutral-700); line-height: 1.6;">
                    <ul style="padding-left: 20px;">
                        <li><strong>Hardest Subject:</strong> Identified by comparing lowest class average and highest backlog count.</li>
                        <li><strong>High Scoring Subject:</strong> Course exhibiting maximum proportion of distinction (&ge; 75) marks.</li>
                        <li><strong>Remedial Recommendation:</strong> Special doubt-clearing sessions should be scheduled for courses with pass percentage below 70%.</li>
                    </ul>
                </div>
            </div>

        </main>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>
