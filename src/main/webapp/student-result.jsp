<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="activeNav" value="result" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Individual Result &bull; Student Result Analysis System</title>
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
                    <h1 class="page-title">Individual Academic Scorecard</h1>
                    <p class="page-subtitle">Detailed subject performance, grade evaluation, and failure diagnostics</p>
                </div>
                <div style="display: flex; gap: 10px;">
                    <a href="${pageContext.request.contextPath}/students" class="btn btn-outline">&larr; Back to Roster</a>
                    <button onclick="window.print()" class="btn btn-primary">🖨️ Print Scorecard</button>
                </div>
            </div>

            <!-- Student Selection Bar -->
            <div class="table-card" style="margin-bottom: 24px;">
                <div class="table-toolbar">
                    <form action="${pageContext.request.contextPath}/student-result" method="get" class="search-form" style="width: 100%; justify-content: space-between;">
                        <div style="display: flex; gap: 10px; align-items: center; flex: 1;">
                            <label for="selectStudent" style="font-size: 13px; font-weight: 600; color: var(--neutral-700); white-space: nowrap;">
                                Select Student:
                            </label>
                            <select id="selectStudent" name="rollNo" class="form-select" onchange="this.form.submit()" style="max-width: 400px;">
                                <option value="">-- Choose from Roster --</option>
                                <c:forEach var="st" items="${allStudents}">
                                    <option value="${st.rollNo}" ${studentResult != null && studentResult.rollNo == st.rollNo ? 'selected' : ''}>
                                        ${st.rollNo} - ${st.name} (${st.result})
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <div style="display: flex; gap: 8px;">
                            <input type="text" name="rollNo" placeholder="Enter Roll No..." class="form-control" style="width: 160px;" value="${param.rollNo}">
                            <button type="submit" class="btn btn-primary btn-sm">Find</button>
                        </div>
                    </form>
                </div>
            </div>

            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger">
                    <span>⚠️</span>
                    <div>${errorMessage}</div>
                </div>
            </c:if>

            <c:choose>
                <c:when test="${not empty studentResult}">
                    <!-- Printable / Displayable Scorecard -->
                    <div class="scorecard">
                        <div class="scorecard-header">
                            <div class="scorecard-college">${empty collegeSettings.collegeName ? 'YOUR COLLEGE NAME' : collegeSettings.collegeName}</div>
                            <div class="scorecard-subtitle">
                                Department of ${empty collegeSettings.departmentName ? 'YOUR DEPARTMENT' : collegeSettings.departmentName} &bull;
                                ${empty collegeSettings.courseName ? 'BCA' : collegeSettings.courseName} (${empty collegeSettings.semester ? 'Semester' : collegeSettings.semester})
                            </div>
                            <div style="margin-top: 10px; font-size: 14px; font-weight: 700; letter-spacing: 1px; color: #feebc8;">
                                OFFICIAL STUDENT MARKS STATEMENT &bull; AY ${empty collegeSettings.academicYear ? '2026-27' : collegeSettings.academicYear}
                            </div>
                        </div>

                        <div class="scorecard-body">
                            <!-- Student Meta Info -->
                            <div class="student-meta-grid">
                                <div class="meta-field">
                                    <label>Roll Number</label>
                                    <div class="value">${studentResult.rollNo}</div>
                                </div>
                                <div class="meta-field">
                                    <label>Student Name</label>
                                    <div class="value">${studentResult.name}</div>
                                </div>
                                <div class="meta-field">
                                    <label>Evaluation Outcome</label>
                                    <div class="value">
                                        <span class="badge ${studentResult.result == 'PASS' ? 'badge-pass' : 'badge-fail'}" style="font-size: 14px; padding: 4px 12px;">
                                            ${studentResult.result}
                                        </span>
                                    </div>
                                </div>
                                <div class="meta-field">
                                    <label>Overall Grade &amp; Category</label>
                                    <div class="value">
                                        <span class="badge badge-grade ${studentResult.grade == 'A+' ? 'aplus' : (studentResult.grade == 'A' ? 'a' : (studentResult.grade == 'F' ? 'f' : ''))}" style="font-size: 14px;">
                                            Grade ${studentResult.grade}
                                        </span>
                                        <span style="font-size: 13px; color: var(--neutral-600); font-weight: 500;">
                                            (${studentResult.category})
                                        </span>
                                    </div>
                                </div>
                            </div>

                            <!-- Failure Diagnostic Box (Only shown if student failed) -->
                            <c:if test="${studentResult.result == 'FAIL'}">
                                <div class="diagnostic-box">
                                    <h5>⚠️ Diagnostic Failure Notice</h5>
                                    <p style="font-size: 13px; color: #742a2a; margin-bottom: 6px;">
                                        Even if the overall percentage is passing, a student fails if any subject score is under 40 marks.
                                    </p>
                                    <strong>Subjects Causing Failure:</strong>
                                    <ul style="padding-left: 20px; font-size: 13px; color: #742a2a; margin-top: 4px;">
                                        <c:forEach var="failedSub" items="${studentResult.failedSubjects}">
                                            <li><strong>${failedSub}</strong> &mdash; Below minimum threshold of 40</li>
                                        </c:forEach>
                                    </ul>
                                </div>
                            </c:if>

                            <!-- Subject Marks Breakdown Table -->
                            <table class="marks-table">
                                <thead>
                                    <tr>
                                        <th>Subject Name</th>
                                        <th class="text-center">Maximum Marks</th>
                                        <th class="text-center">Passing Marks</th>
                                        <th class="text-center">Marks Obtained</th>
                                        <th class="text-center">Subject Status</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td>Java Programming</td>
                                        <td class="text-center">100</td>
                                        <td class="text-center">40</td>
                                        <td class="text-center fw-bold ${studentResult.javaMarks < 40 ? 'text-danger' : ''}">
                                            ${studentResult.javaMarks}
                                        </td>
                                        <td class="text-center">
                                            <span class="badge ${studentResult.javaMarks >= 40 ? 'badge-pass' : 'badge-fail'}">
                                                ${studentResult.javaMarks >= 40 ? 'Pass' : 'Backlog'}
                                            </span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>Digital Electronics (DE)</td>
                                        <td class="text-center">100</td>
                                        <td class="text-center">40</td>
                                        <td class="text-center fw-bold ${studentResult.deMarks < 40 ? 'text-danger' : ''}">
                                            ${studentResult.deMarks}
                                        </td>
                                        <td class="text-center">
                                            <span class="badge ${studentResult.deMarks >= 40 ? 'badge-pass' : 'badge-fail'}">
                                                ${studentResult.deMarks >= 40 ? 'Pass' : 'Backlog'}
                                            </span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>Data Structures &amp; Algorithms (DSA)</td>
                                        <td class="text-center">100</td>
                                        <td class="text-center">40</td>
                                        <td class="text-center fw-bold ${studentResult.dsaMarks < 40 ? 'text-danger' : ''}">
                                            ${studentResult.dsaMarks}
                                        </td>
                                        <td class="text-center">
                                            <span class="badge ${studentResult.dsaMarks >= 40 ? 'badge-pass' : 'badge-fail'}">
                                                ${studentResult.dsaMarks >= 40 ? 'Pass' : 'Backlog'}
                                            </span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>Operating System (OS)</td>
                                        <td class="text-center">100</td>
                                        <td class="text-center">40</td>
                                        <td class="text-center fw-bold ${studentResult.osMarks < 40 ? 'text-danger' : ''}">
                                            ${studentResult.osMarks}
                                        </td>
                                        <td class="text-center">
                                            <span class="badge ${studentResult.osMarks >= 40 ? 'badge-pass' : 'badge-fail'}">
                                                ${studentResult.osMarks >= 40 ? 'Pass' : 'Backlog'}
                                            </span>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>

                            <!-- Score Summary Grid -->
                            <div class="scorecard-summary">
                                <div>
                                    <div style="font-size: 11px; text-transform: uppercase; color: var(--neutral-600); font-weight: 700;">Grand Total</div>
                                    <div style="font-size: 20px; font-weight: 800; color: var(--neutral-900);">${studentResult.totalMarks} / 400</div>
                                </div>
                                <div>
                                    <div style="font-size: 11px; text-transform: uppercase; color: var(--neutral-600); font-weight: 700;">Percentage</div>
                                    <div style="font-size: 20px; font-weight: 800; color: var(--neutral-900);">${studentResult.percentage}%</div>
                                </div>
                                <div>
                                    <div style="font-size: 11px; text-transform: uppercase; color: var(--neutral-600); font-weight: 700;">Highest Subject</div>
                                    <div style="font-size: 14px; font-weight: 700; color: var(--primary);">${studentResult.highestSubject}</div>
                                </div>
                                <div>
                                    <div style="font-size: 11px; text-transform: uppercase; color: var(--neutral-600); font-weight: 700;">Lowest Subject</div>
                                    <div style="font-size: 14px; font-weight: 700; color: #c53030;">${studentResult.lowestSubject}</div>
                                </div>
                            </div>

                            <div style="font-size: 12px; color: var(--neutral-600); text-align: center; border-top: 1px dashed var(--neutral-300); padding-top: 16px;">
                                Mean Subject Marks: <strong>${studentResult.averageSubjectMarks}</strong> &bull;
                                Generated on <%= new java.text.SimpleDateFormat("dd-MM-yyyy hh:mm a").format(new java.util.Date()) %> &bull;
                                Student Result Analysis System
                            </div>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="alert alert-info">
                        <span>ℹ️</span>
                        <div>No student record selected. Please select a student from the dropdown or search above.</div>
                    </div>
                </c:otherwise>
            </c:choose>

        </main>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>
