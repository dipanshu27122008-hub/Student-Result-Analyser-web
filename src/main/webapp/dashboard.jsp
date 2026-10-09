<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="activeNav" value="dashboard" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard &bull; Student Result Analysis System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/includes/sidebar.jsp"/>

    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/includes/topbar.jsp"/>

        <main class="content-body">
            <div class="page-header">
                <div>
                    <h1 class="page-title">Executive Result Dashboard</h1>
                    <p class="page-subtitle">Batch performance overview, category distributions, and diagnostic metrics</p>
                </div>
                <div style="display: flex; gap: 8px; flex-wrap: wrap;">
                    <a href="${pageContext.request.contextPath}/students" class="btn btn-outline">➕ Add Student</a>
                    <a href="${pageContext.request.contextPath}/demo-data?redirect=${pageContext.request.contextPath}/dashboard" 
                       class="btn btn-outline" 
                       onclick="return confirm('Load the 35 benchmark demo students into the database?')">
                        ⚡ Load Demo Data
                    </a>
                    <a href="${pageContext.request.contextPath}/upload" class="btn btn-outline">📥 Import File</a>
                    <a href="${pageContext.request.contextPath}/pdf-report" class="btn btn-primary" target="_blank">📄 Generate PDF</a>
                </div>
            </div>

            <!-- Flash Success / Error Messages -->
            <c:if test="${not empty successMessage}">
                <div class="alert alert-success" style="margin-bottom: 20px;">
                    <span>✅</span>
                    <div>${successMessage}</div>
                </div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger" style="margin-bottom: 20px;">
                    <span>⚠️</span>
                    <div>${errorMessage}</div>
                </div>
            </c:if>

            <c:if test="${resultsCount == 0}">
                <div class="alert alert-info">
                    <span>💡</span>
                    <div>
                        <strong>No student records currently in database!</strong> Click below to load 35 realistic benchmark students or import an Excel/TXT file.
                        <div style="margin-top: 10px; display: flex; gap: 8px; flex-wrap: wrap;">
                            <a href="${pageContext.request.contextPath}/demo-data?redirect=${pageContext.request.contextPath}/dashboard" class="btn btn-sm btn-accent">⚡ Instant 35-Student Demo</a>
                            <a href="${pageContext.request.contextPath}/students" class="btn btn-sm btn-primary">➕ Add Student Manually</a>
                            <a href="${pageContext.request.contextPath}/upload" class="btn btn-sm btn-outline">📥 Go to Import Page</a>
                        </div>
                    </div>
                </div>
            </c:if>

            <!-- KPI Cards Grid -->
            <div class="kpi-grid">
                <div class="kpi-card primary">
                    <div class="kpi-label">Total Students</div>
                    <div class="kpi-value">${analysis.totalStudents}</div>
                    <div class="kpi-subtext">Registered candidates</div>
                </div>

                <div class="kpi-card success">
                    <div class="kpi-label">Passed Students</div>
                    <div class="kpi-value">${analysis.passedStudents}</div>
                    <div class="kpi-subtext">Every subject &ge; 40 marks</div>
                </div>

                <div class="kpi-card danger">
                    <div class="kpi-label">Failed Students</div>
                    <div class="kpi-value">${analysis.failedStudents}</div>
                    <div class="kpi-subtext">&ge; 1 subject &lt; 40 marks</div>
                </div>

                <div class="kpi-card info">
                    <div class="kpi-label">Pass Percentage</div>
                    <div class="kpi-value">${analysis.passPercentage}%</div>
                    <div class="kpi-subtext">Batch clearing rate</div>
                </div>
            </div>

            <!-- Mutually Exclusive Percentage Category Cards -->
            <div class="kpi-grid">
                <div class="kpi-card gold">
                    <div class="kpi-label">75% &amp; Above</div>
                    <div class="kpi-value">${analysis.category75Plus}</div>
                    <div class="kpi-subtext">Distinction category</div>
                </div>

                <div class="kpi-card info">
                    <div class="kpi-label">65% – 74.99%</div>
                    <div class="kpi-value">${analysis.category65To74}</div>
                    <div class="kpi-subtext">First class category</div>
                </div>

                <div class="kpi-card primary">
                    <div class="kpi-label">40% – 64.99%</div>
                    <div class="kpi-value">${analysis.category40To64}</div>
                    <div class="kpi-subtext">Pass class category</div>
                </div>

                <div class="kpi-card danger">
                    <div class="kpi-label">Below 40%</div>
                    <div class="kpi-value">${analysis.categoryBelow40}</div>
                    <div class="kpi-subtext">Under-performing category</div>
                </div>
            </div>

            <!-- Performer Highlights -->
            <div class="highlight-grid">
                <div class="performer-card top">
                    <div class="performer-icon">🏆</div>
                    <div class="performer-info">
                        <h4>Top Performer</h4>
                        <div class="performer-name">${empty analysis.topPerformerName ? 'N/A' : analysis.topPerformerName}</div>
                        <div class="performer-score">
                            Roll No: <strong>${empty analysis.topPerformerRoll ? 'N/A' : analysis.topPerformerRoll}</strong> &bull;
                            Score: <strong>${analysis.topPerformerPercentage}%</strong>
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
                            Score: <strong>${analysis.lowestPerformerPercentage}%</strong>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Charts Grid (Real dynamic values) -->
            <div class="charts-grid">
                <!-- Chart 1: Pass vs Fail -->
                <div class="chart-card">
                    <div class="chart-card-header">
                        <div>
                            <div class="chart-card-title">Overall Pass vs Fail Ratio</div>
                            <div class="chart-card-desc">Comparison of qualified vs backlogged students</div>
                        </div>
                    </div>
                    <div class="chart-canvas-wrapper">
                        <canvas id="chartPassFail"></canvas>
                    </div>
                </div>

                <!-- Chart 2: Category Distribution -->
                <div class="chart-card">
                    <div class="chart-card-header">
                        <div>
                            <div class="chart-card-title">Percentage Category Distribution</div>
                            <div class="chart-card-desc">Mutually exclusive performance brackets</div>
                        </div>
                    </div>
                    <div class="chart-canvas-wrapper">
                        <canvas id="chartCategory"></canvas>
                    </div>
                </div>

                <!-- Chart 3: Subject Averages -->
                <div class="chart-card">
                    <div class="chart-card-header">
                        <div>
                            <div class="chart-card-title">Average Marks by Subject</div>
                            <div class="chart-card-desc">Class mean score per course (out of 100)</div>
                        </div>
                    </div>
                    <div class="chart-canvas-wrapper">
                        <canvas id="chartSubjectAvg"></canvas>
                    </div>
                </div>

                <!-- Chart 4: Subject Pass Percentage -->
                <div class="chart-card">
                    <div class="chart-card-header">
                        <div>
                            <div class="chart-card-title">Subject Pass Percentage</div>
                            <div class="chart-card-desc">Pass rate per course (&ge; 40 threshold)</div>
                        </div>
                    </div>
                    <div class="chart-canvas-wrapper">
                        <canvas id="chartSubjectPass"></canvas>
                    </div>
                </div>
            </div>

            <!-- Overall Performance Summary Table -->
            <div class="table-card">
                <div class="table-toolbar">
                    <div class="table-title">Performance Diagnostic Benchmark</div>
                    <a href="${pageContext.request.contextPath}/students" class="btn btn-sm btn-outline">View All Students &rarr;</a>
                </div>
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Metric</th>
                                <th>Class Value</th>
                                <th>Benchmark Standard</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td class="fw-bold">Overall Batch Average</td>
                                <td>${analysis.overallAverage}%</td>
                                <td>&ge; 60.00%</td>
                                <td>
                                    <span class="badge ${analysis.overallAverage >= 60 ? 'badge-pass' : 'badge-fail'}">
                                        ${analysis.overallAverage >= 60 ? 'Satisfactory' : 'Needs Review'}
                                    </span>
                                </td>
                            </tr>
                            <tr>
                                <td class="fw-bold">Highest Percentage Recorded</td>
                                <td>${analysis.highestPercentage}%</td>
                                <td>Top Batch Score</td>
                                <td><span class="badge badge-pass">Peak</span></td>
                            </tr>
                            <tr>
                                <td class="fw-bold">Lowest Percentage Recorded</td>
                                <td>${analysis.lowestPercentage}%</td>
                                <td>Minimum Batch Score</td>
                                <td><span class="badge badge-fail">Lowest</span></td>
                            </tr>
                            <tr>
                                <td class="fw-bold">Batch Pass Percentage</td>
                                <td>${analysis.passPercentage}%</td>
                                <td>&ge; 70.00%</td>
                                <td>
                                    <span class="badge ${analysis.passPercentage >= 70 ? 'badge-pass' : 'badge-fail'}">
                                        ${analysis.passPercentage >= 70 ? 'Target Met' : 'Below Target'}
                                    </span>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div style="margin-top: 16px; padding: 12px 20px; background-color: var(--neutral-50); border-top: 1px solid var(--neutral-200); display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 10px; font-size: 13px; color: var(--neutral-600);">
                    <div>Academic Year: <strong>${collegeSettings.academicYear}</strong> &bull; Department of ${collegeSettings.departmentName}</div>
                    <div class="badge-db-status">🟢 Database: ${dbStatus}</div>
                </div>
            </div>

        </main>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/script.js"></script>
<script>
    // Initialize Dashboard Charts with dynamic server data
    document.addEventListener("DOMContentLoaded", function() {
        const dashboardData = {
            passedCount: ${chartPassCount != null ? chartPassCount : 0},
            failedCount: ${chartFailCount != null ? chartFailCount : 0},
            cat75Plus: ${cat75Plus != null ? cat75Plus : 0},
            cat65To74: ${cat65To74 != null ? cat65To74 : 0},
            cat40To64: ${cat40To64 != null ? cat40To64 : 0},
            catBelow40: ${catBelow40 != null ? catBelow40 : 0},
            javaAvg: ${javaAvg != null ? javaAvg : 0.0},
            deAvg: ${deAvg != null ? deAvg : 0.0},
            dsaAvg: ${dsaAvg != null ? dsaAvg : 0.0},
            osAvg: ${osAvg != null ? osAvg : 0.0},
            javaPassPct: ${javaPassPct != null ? javaPassPct : 0.0},
            dePassPct: ${dePassPct != null ? dePassPct : 0.0},
            dsaPassPct: ${dsaPassPct != null ? dsaPassPct : 0.0},
            osPassPct: ${osPassPct != null ? osPassPct : 0.0}
        };
        initDashboardCharts(dashboardData);
    });
</script>
</body>
</html>
