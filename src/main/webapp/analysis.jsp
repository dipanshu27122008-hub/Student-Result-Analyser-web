<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="activeNav" value="analysis" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Overall Analysis &bull; Student Result Analysis System</title>
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
                    <h1 class="page-title">Overall Batch Performance Analysis</h1>
                    <p class="page-subtitle">Exhaustive statistical aggregation, distribution brackets, and institutional benchmarks</p>
                </div>
                <div style="display: flex; gap: 10px;">
                    <a href="${pageContext.request.contextPath}/subject-analysis" class="btn btn-outline">📚 Subject Analysis</a>
                    <a href="${pageContext.request.contextPath}/pdf-report" class="btn btn-primary" target="_blank">📄 Download PDF Report</a>
                </div>
            </div>

            <!-- 1. Overall Student Statistics -->
            <div class="table-card">
                <div class="table-toolbar">
                    <div class="table-title">1. Student Population &amp; Qualification Statistics</div>
                </div>
                <div style="padding: 20px;">
                    <div class="kpi-grid">
                        <div class="kpi-card primary">
                            <div class="kpi-label">Total Students</div>
                            <div class="kpi-value">${analysis.totalStudents}</div>
                            <div class="kpi-subtext">Evaluated cohort</div>
                        </div>

                        <div class="kpi-card success">
                            <div class="kpi-label">Passed Students</div>
                            <div class="kpi-value">${analysis.passedStudents}</div>
                            <div class="kpi-subtext">All courses &ge; 40 marks</div>
                        </div>

                        <div class="kpi-card danger">
                            <div class="kpi-label">Failed Students</div>
                            <div class="kpi-value">${analysis.failedStudents}</div>
                            <div class="kpi-subtext">&ge; 1 course backlogged</div>
                        </div>

                        <div class="kpi-card info">
                            <div class="kpi-label">Batch Pass Rate</div>
                            <div class="kpi-value">${analysis.passPercentage}%</div>
                            <div class="kpi-subtext">Clearance efficiency</div>
                        </div>

                        <div class="kpi-card danger">
                            <div class="kpi-label">Batch Fail Rate</div>
                            <div class="kpi-value">${analysis.failPercentage}%</div>
                            <div class="kpi-subtext">Backlog proportion</div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 2. Percentage Distribution Categories Table -->
            <div class="table-card">
                <div class="table-toolbar">
                    <div class="table-title">2. Mutually Exclusive Percentage Categories</div>
                </div>
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Category Classification</th>
                                <th>Percentage Range</th>
                                <th class="text-center">Count</th>
                                <th class="text-center">Category Share</th>
                                <th>Academic Distinction</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td class="fw-bold">Category 1: Distinction</td>
                                <td>75% and above (75%+)</td>
                                <td class="text-center fw-bold" style="color: #2b6cb0;">${analysis.category75Plus}</td>
                                <td class="text-center">
                                    <c:choose>
                                        <c:when test="${analysis.totalStudents > 0}">
                                            ${String.format("%.2f", (analysis.category75Plus * 100.0) / analysis.totalStudents)}%
                                        </c:when>
                                        <c:otherwise>0%</c:otherwise>
                                    </c:choose>
                                </td>
                                <td><span class="badge aplus">Distinction / First Class with Honours</span></td>
                            </tr>
                            <tr>
                                <td class="fw-bold">Category 2: First Class</td>
                                <td>65% to 74.99%</td>
                                <td class="text-center fw-bold" style="color: #3182ce;">${analysis.category65To74}</td>
                                <td class="text-center">
                                    <c:choose>
                                        <c:when test="${analysis.totalStudents > 0}">
                                            ${String.format("%.2f", (analysis.category65To74 * 100.0) / analysis.totalStudents)}%
                                        </c:when>
                                        <c:otherwise>0%</c:otherwise>
                                    </c:choose>
                                </td>
                                <td><span class="badge a">First Class</span></td>
                            </tr>
                            <tr>
                                <td class="fw-bold">Category 3: Second / Pass Class</td>
                                <td>40% to 64.99%</td>
                                <td class="text-center fw-bold" style="color: #d69e2e;">${analysis.category40To64}</td>
                                <td class="text-center">
                                    <c:choose>
                                        <c:when test="${analysis.totalStudents > 0}">
                                            ${String.format("%.2f", (analysis.category40To64 * 100.0) / analysis.totalStudents)}%
                                        </c:when>
                                        <c:otherwise>0%</c:otherwise>
                                    </c:choose>
                                </td>
                                <td><span class="badge badge-grade">Pass Class</span></td>
                            </tr>
                            <tr>
                                <td class="fw-bold">Category 4: Remedial Class</td>
                                <td>Below 40%</td>
                                <td class="text-center fw-bold" style="color: #e53e3e;">${analysis.categoryBelow40}</td>
                                <td class="text-center">
                                    <c:choose>
                                        <c:when test="${analysis.totalStudents > 0}">
                                            ${String.format("%.2f", (analysis.categoryBelow40 * 100.0) / analysis.totalStudents)}%
                                        </c:when>
                                        <c:otherwise>0%</c:otherwise>
                                    </c:choose>
                                </td>
                                <td><span class="badge badge-fail">Fail Class (Needs Remedial Coaching)</span></td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- 3. Overall Performance & Performers -->
            <div class="table-card">
                <div class="table-toolbar">
                    <div class="table-title">3. Performance Extreme Bounds &amp; Class Averages</div>
                </div>
                <div style="padding: 20px;">
                    <div class="highlight-grid" style="margin-bottom: 20px;">
                        <div class="performer-card top">
                            <div class="performer-icon">🥇</div>
                            <div class="performer-info">
                                <h4>Top Performer</h4>
                                <div class="performer-name">${empty analysis.topPerformerName ? 'N/A' : analysis.topPerformerName}</div>
                                <div class="performer-score">
                                    Roll No: <strong>${empty analysis.topPerformerRoll ? 'N/A' : analysis.topPerformerRoll}</strong> &bull;
                                    Percentage: <strong>${analysis.topPerformerPercentage}%</strong>
                                </div>
                            </div>
                        </div>

                        <div class="performer-card lowest">
                            <div class="performer-icon">⚠️</div>
                            <div class="performer-info">
                                <h4>Lowest Performer</h4>
                                <div class="performer-name">${empty analysis.lowestPerformerName ? 'N/A' : analysis.lowestPerformerName}</div>
                                <div class="performer-score">
                                    Roll No: <strong>${empty analysis.lowestPerformerRoll ? 'N/A' : analysis.lowestPerformerRoll}</strong> &bull;
                                    Percentage: <strong>${analysis.lowestPerformerPercentage}%</strong>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Metric Parameter</th>
                                    <th>Calculated Value</th>
                                    <th>Description</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td class="fw-bold">Overall Batch Average</td>
                                    <td class="fw-bold">${analysis.overallAverage}%</td>
                                    <td>Cumulative mean percentage across all enrolled students</td>
                                </tr>
                                <tr>
                                    <td class="fw-bold">Highest Percentage</td>
                                    <td class="fw-bold" style="color: #22543d;">${analysis.highestPercentage}%</td>
                                    <td>Maximum percentage achieved in this examination batch</td>
                                </tr>
                                <tr>
                                    <td class="fw-bold">Lowest Percentage</td>
                                    <td class="fw-bold" style="color: #742a2a;">${analysis.lowestPercentage}%</td>
                                    <td>Minimum percentage recorded across all candidate scores</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

        </main>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>
