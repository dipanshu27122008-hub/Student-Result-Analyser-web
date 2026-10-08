<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="activeNav" value="students" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Roster &bull; Student Result Analysis System</title>
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
                    <h1 class="page-title">Student Performance Roster</h1>
                    <p class="page-subtitle">Search, filter, and inspect comprehensive academic marks and results</p>
                </div>
                <div style="display: flex; gap: 10px;">
                    <a href="${pageContext.request.contextPath}/upload" class="btn btn-outline">📥 Import File</a>
                    <a href="${pageContext.request.contextPath}/pdf-report?filter=${currentFilter}" class="btn btn-primary" target="_blank">📄 Export Filtered PDF</a>
                </div>
            </div>

            <!-- Search & Filter Card -->
            <div class="table-card">
                <div class="table-toolbar">
                    <form action="${pageContext.request.contextPath}/students" method="get" class="search-form" style="width: 100%; justify-content: space-between;">
                        <div style="display: flex; gap: 10px; align-items: center; flex: 1; max-width: 500px;">
                            <input type="text" name="q" value="${currentQuery}" class="form-control"
                                   placeholder="Search by Roll No or Student Name..." style="flex: 1;">
                            <button type="submit" class="btn btn-primary btn-sm">🔍 Search</button>
                            <c:if test="${not empty currentQuery || currentFilter != 'ALL'}">
                                <a href="${pageContext.request.contextPath}/students" class="btn btn-outline btn-sm">Clear</a>
                            </c:if>
                        </div>

                        <div style="display: flex; gap: 10px; align-items: center;">
                            <label for="filterSelect" style="font-size: 13px; font-weight: 600; color: var(--neutral-700);">Category Filter:</label>
                            <select id="filterSelect" name="filter" class="form-select" onchange="this.form.submit()">
                                <option value="ALL" ${currentFilter == 'ALL' ? 'selected' : ''}>All Students</option>
                                <option value="PASS" ${currentFilter == 'PASS' ? 'selected' : ''}>PASS Only</option>
                                <option value="FAIL" ${currentFilter == 'FAIL' ? 'selected' : ''}>FAIL Only</option>
                                <option value="75%+" ${currentFilter == '75%+' ? 'selected' : ''}>75% &amp; Above (Distinction)</option>
                                <option value="65–74.99%" ${currentFilter == '65–74.99%' || currentFilter == '65-74.99%' ? 'selected' : ''}>65% – 74.99% (First Class)</option>
                                <option value="40–64.99%" ${currentFilter == '40–64.99%' || currentFilter == '40-64.99%' ? 'selected' : ''}>40% – 64.99% (Pass Class)</option>
                                <option value="Below 40%" ${currentFilter == 'Below 40%' ? 'selected' : ''}>Below 40% (Fail Class)</option>
                            </select>
                        </div>
                    </form>
                </div>

                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Roll No</th>
                                <th>Student Name</th>
                                <th class="text-center">Java</th>
                                <th class="text-center">DE</th>
                                <th class="text-center">DSA</th>
                                <th class="text-center">OS</th>
                                <th class="text-center">Total (400)</th>
                                <th class="text-center">Percentage</th>
                                <th class="text-center">Grade</th>
                                <th class="text-center">Category</th>
                                <th class="text-center">Result</th>
                                <th class="text-center">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty studentsList}">
                                    <c:forEach var="s" items="${studentsList}">
                                        <tr>
                                            <td class="fw-bold">${s.rollNo}</td>
                                            <td>${s.name}</td>
                                            <td class="text-center ${s.javaMarks < 40 ? 'fw-bold' : ''}" style="${s.javaMarks < 40 ? 'color: #e53e3e;' : ''}">
                                                ${s.javaMarks}
                                            </td>
                                            <td class="text-center ${s.deMarks < 40 ? 'fw-bold' : ''}" style="${s.deMarks < 40 ? 'color: #e53e3e;' : ''}">
                                                ${s.deMarks}
                                            </td>
                                            <td class="text-center ${s.dsaMarks < 40 ? 'fw-bold' : ''}" style="${s.dsaMarks < 40 ? 'color: #e53e3e;' : ''}">
                                                ${s.dsaMarks}
                                            </td>
                                            <td class="text-center ${s.osMarks < 40 ? 'fw-bold' : ''}" style="${s.osMarks < 40 ? 'color: #e53e3e;' : ''}">
                                                ${s.osMarks}
                                            </td>
                                            <td class="text-center fw-bold">${s.totalMarks}</td>
                                            <td class="text-center fw-bold">${s.percentage}%</td>
                                            <td class="text-center">
                                                <span class="badge badge-grade ${s.grade == 'A+' ? 'aplus' : (s.grade == 'A' ? 'a' : (s.grade == 'F' ? 'f' : ''))}">
                                                    ${s.grade}
                                                </span>
                                            </td>
                                            <td class="text-center">
                                                <span class="badge badge-cat">${s.category}</span>
                                            </td>
                                            <td class="text-center">
                                                <span class="badge ${s.result == 'PASS' ? 'badge-pass' : 'badge-fail'}">
                                                    ${s.result}
                                                </span>
                                            </td>
                                            <td class="text-center">
                                                <a href="${pageContext.request.contextPath}/student-result?rollNo=${s.rollNo}" class="btn btn-sm btn-outline" title="View Full Report Card">
                                                    👁️ View
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="12" class="text-center" style="padding: 30px; color: var(--neutral-600);">
                                            No students found matching your search or filter criteria.
                                            <div style="margin-top: 10px;">
                                                <a href="${pageContext.request.contextPath}/students" class="btn btn-sm btn-outline">Reset Filter</a>
                                                <a href="${pageContext.request.contextPath}/upload" class="btn btn-sm btn-primary">Import Data</a>
                                            </div>
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>

                <div style="padding: 12px 20px; background-color: var(--neutral-50); border-top: 1px solid var(--neutral-200); font-size: 13px; color: var(--neutral-600);">
                    Showing <strong>${totalFound}</strong> student record(s). Passing criterion: minimum 40 marks in every subject.
                </div>
            </div>

        </main>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>
