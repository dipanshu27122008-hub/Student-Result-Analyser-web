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
                    <p class="page-subtitle">Search, filter, manage student records, and inspect academic metrics</p>
                </div>
                <div style="display: flex; gap: 8px; flex-wrap: wrap;">
                    <button type="button" class="btn btn-primary" onclick="openAddModal()">
                        ➕ Add Student
                    </button>
                    <a href="${pageContext.request.contextPath}/demo-data?redirect=${pageContext.request.contextPath}/students" 
                       class="btn btn-outline" 
                       onclick="return confirm('Load the 35 benchmark demo students into the database?')">
                        ⚡ Load Demo Data
                    </a>
                    <a href="${pageContext.request.contextPath}/upload" class="btn btn-outline">
                        📥 Import File
                    </a>
                    <a href="${pageContext.request.contextPath}/pdf-report?filter=${currentFilter}" class="btn btn-outline" target="_blank">
                        📄 Export PDF
                    </a>
                </div>
            </div>

            <!-- Flash Success Message -->
            <c:if test="${not empty successMessage}">
                <div class="alert alert-success" style="margin-bottom: 20px;">
                    <span>✅</span>
                    <div>${successMessage}</div>
                </div>
            </c:if>

            <!-- Flash Error Message -->
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger" style="margin-bottom: 20px;">
                    <span>⚠️</span>
                    <div>${errorMessage}</div>
                </div>
            </c:if>

            <!-- Search & Filter Card -->
            <div class="table-card">
                <div class="table-toolbar">
                    <form action="${pageContext.request.contextPath}/students" method="get" class="search-form" style="width: 100%; justify-content: space-between; flex-wrap: wrap; gap: 12px;">
                        <div style="display: flex; gap: 10px; align-items: center; flex: 1; min-width: 280px; max-width: 500px;">
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
                                <th class="text-center" style="min-width: 170px;">Actions</th>
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
                                                <div style="display: inline-flex; gap: 4px; align-items: center;">
                                                    <a href="${pageContext.request.contextPath}/student-result?rollNo=${s.rollNo}" 
                                                       class="btn btn-sm btn-outline" style="padding: 3px 8px; font-size: 12px;" title="View Full Report Card">
                                                        👁️ View
                                                    </a>
                                                    <button type="button" class="btn btn-sm btn-outline" style="padding: 3px 8px; font-size: 12px;"
                                                            onclick="openEditModal(${s.studentId}, '${s.rollNo}', '${s.name}', ${s.javaMarks}, ${s.deMarks}, ${s.dsaMarks}, ${s.osMarks})" 
                                                            title="Edit Student Marks">
                                                        ✏️ Edit
                                                    </button>
                                                    <form action="${pageContext.request.contextPath}/delete-student" method="post" style="display: inline; margin: 0;"
                                                          onsubmit="return confirm('Are you sure you want to delete student ${s.name} (Roll: ${s.rollNo})?');">
                                                        <input type="hidden" name="studentId" value="${s.studentId}">
                                                        <button type="submit" class="btn btn-sm btn-outline" style="padding: 3px 8px; font-size: 12px; color: #e53e3e; border-color: #fca5a5;" title="Delete Student">
                                                            🗑️
                                                        </button>
                                                    </form>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="12" class="text-center" style="padding: 36px; color: var(--neutral-600);">
                                            <div style="font-size: 15px; font-weight: 600; margin-bottom: 6px;">No student records found!</div>
                                            <p style="font-size: 13px; margin-bottom: 16px;">You can add a student manually, load the benchmark demo dataset, or import an Excel/TXT file.</p>
                                            <div style="display: flex; gap: 10px; justify-content: center;">
                                                <button type="button" class="btn btn-primary btn-sm" onclick="openAddModal()">➕ Add Student</button>
                                                <a href="${pageContext.request.contextPath}/demo-data?redirect=${pageContext.request.contextPath}/students" class="btn btn-accent btn-sm">⚡ Load Demo Data</a>
                                                <a href="${pageContext.request.contextPath}/upload" class="btn btn-outline btn-sm">📥 Import File</a>
                                            </div>
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>

                <div style="padding: 12px 20px; background-color: var(--neutral-50); border-top: 1px solid var(--neutral-200); font-size: 13px; color: var(--neutral-600); display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 10px;">
                    <div>Showing <strong>${totalFound}</strong> student record(s). Passing criterion: minimum 40 marks in every subject.</div>
                    <div class="badge-db-status">🟢 ${dbStatus}</div>
                </div>
            </div>

        </main>
    </div>
</div>

<!-- =========================================================================
     MODAL 1: ADD NEW STUDENT (MANUAL ENTRY)
     ========================================================================= -->
<div class="modal-overlay" id="addStudentModal">
    <div class="modal-container">
        <div class="modal-header">
            <div class="modal-title">➕ Add New Student Record</div>
            <button type="button" class="modal-close-btn" onclick="closeAddModal()">&times;</button>
        </div>
        <form action="${pageContext.request.contextPath}/add-student" method="post" id="addStudentForm">
            <div class="modal-body">
                <div class="form-grid-2">
                    <div class="form-group">
                        <label class="form-label" for="addRollNo">Roll Number *</label>
                        <input type="text" id="addRollNo" name="rollNo" class="form-control" placeholder="e.g. 136" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="addName">Student Name *</label>
                        <input type="text" id="addName" name="name" class="form-control" placeholder="e.g. Rohan Verma" required>
                    </div>
                </div>

                <div style="font-size: 13px; font-weight: 700; color: var(--neutral-700); margin: 16px 0 8px 0; border-top: 1px solid var(--neutral-200); padding-top: 12px;">
                    Subject Marks (Out of 100 each):
                </div>

                <div class="form-grid-2">
                    <div class="form-group">
                        <label class="form-label" for="addJava">Java Marks (0–100) *</label>
                        <input type="number" id="addJava" name="java" class="form-control" min="0" max="100" step="any" placeholder="0 - 100" required oninput="calculateAddPreview()">
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="addDe">Digital Electronics (0–100) *</label>
                        <input type="number" id="addDe" name="de" class="form-control" min="0" max="100" step="any" placeholder="0 - 100" required oninput="calculateAddPreview()">
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="addDsa">DSA Marks (0–100) *</label>
                        <input type="number" id="addDsa" name="dsa" class="form-control" min="0" max="100" step="any" placeholder="0 - 100" required oninput="calculateAddPreview()">
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="addOs">Operating System (0–100) *</label>
                        <input type="number" id="addOs" name="os" class="form-control" min="0" max="100" step="any" placeholder="0 - 100" required oninput="calculateAddPreview()">
                    </div>
                </div>

                <!-- Real-time Interactive Calculation Preview Box -->
                <div class="live-calc-box">
                    <div class="live-calc-title">
                        <span>⚡ Live Calculated Result Preview</span>
                        <span id="addPreviewStatusNote" style="font-size: 11px; font-weight: normal; color: var(--neutral-600);"></span>
                    </div>
                    <div class="live-calc-grid">
                        <div class="live-calc-item">
                            <div class="live-calc-label">Total Marks</div>
                            <div class="live-calc-val" id="addPrevTotal">0 / 400</div>
                        </div>
                        <div class="live-calc-item">
                            <div class="live-calc-label">Percentage</div>
                            <div class="live-calc-val" id="addPrevPct">0.00%</div>
                        </div>
                        <div class="live-calc-item">
                            <div class="live-calc-label">Grade</div>
                            <div class="live-calc-val" id="addPrevGrade">-</div>
                        </div>
                        <div class="live-calc-item">
                            <div class="live-calc-label">Result</div>
                            <div class="live-calc-val" id="addPrevResult">-</div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline" onclick="closeAddModal()">Cancel</button>
                <button type="submit" class="btn btn-primary">💾 Save Student Record</button>
            </div>
        </form>
    </div>
</div>

<!-- =========================================================================
     MODAL 2: EDIT STUDENT RECORD
     ========================================================================= -->
<div class="modal-overlay" id="editStudentModal">
    <div class="modal-container">
        <div class="modal-header">
            <div class="modal-title">✏️ Edit Student Marks &amp; Details</div>
            <button type="button" class="modal-close-btn" onclick="closeEditModal()">&times;</button>
        </div>
        <form action="${pageContext.request.contextPath}/edit-student" method="post" id="editStudentForm">
            <input type="hidden" id="editStudentId" name="studentId">
            <div class="modal-body">
                <div class="form-grid-2">
                    <div class="form-group">
                        <label class="form-label" for="editRollNo">Roll Number (Permanent)</label>
                        <input type="text" id="editRollNo" class="form-control" readonly style="background-color: var(--neutral-100); font-weight: 600;">
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="editName">Student Name *</label>
                        <input type="text" id="editName" name="name" class="form-control" required>
                    </div>
                </div>

                <div style="font-size: 13px; font-weight: 700; color: var(--neutral-700); margin: 16px 0 8px 0; border-top: 1px solid var(--neutral-200); padding-top: 12px;">
                    Subject Marks (Out of 100 each):
                </div>

                <div class="form-grid-2">
                    <div class="form-group">
                        <label class="form-label" for="editJava">Java Marks (0–100) *</label>
                        <input type="number" id="editJava" name="java" class="form-control" min="0" max="100" step="any" required oninput="calculateEditPreview()">
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="editDe">Digital Electronics (0–100) *</label>
                        <input type="number" id="editDe" name="de" class="form-control" min="0" max="100" step="any" required oninput="calculateEditPreview()">
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="editDsa">DSA Marks (0–100) *</label>
                        <input type="number" id="editDsa" name="dsa" class="form-control" min="0" max="100" step="any" required oninput="calculateEditPreview()">
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="editOs">Operating System (0–100) *</label>
                        <input type="number" id="editOs" name="os" class="form-control" min="0" max="100" step="any" required oninput="calculateEditPreview()">
                    </div>
                </div>

                <!-- Real-time Interactive Calculation Preview Box for Edit -->
                <div class="live-calc-box">
                    <div class="live-calc-title">
                        <span>⚡ Updated Result Preview</span>
                        <span id="editPreviewStatusNote" style="font-size: 11px; font-weight: normal; color: var(--neutral-600);"></span>
                    </div>
                    <div class="live-calc-grid">
                        <div class="live-calc-item">
                            <div class="live-calc-label">Total Marks</div>
                            <div class="live-calc-val" id="editPrevTotal">0 / 400</div>
                        </div>
                        <div class="live-calc-item">
                            <div class="live-calc-label">Percentage</div>
                            <div class="live-calc-val" id="editPrevPct">0.00%</div>
                        </div>
                        <div class="live-calc-item">
                            <div class="live-calc-label">Grade</div>
                            <div class="live-calc-val" id="editPrevGrade">-</div>
                        </div>
                        <div class="live-calc-item">
                            <div class="live-calc-label">Result</div>
                            <div class="live-calc-val" id="editPrevResult">-</div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline" onclick="closeEditModal()">Cancel</button>
                <button type="submit" class="btn btn-primary">💾 Update Student Record</button>
            </div>
        </form>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/script.js"></script>

<script>
    // Helper to calculate Grade based on project specifications
    function calculateGrade(pct) {
        if (pct >= 90) return 'A+';
        if (pct >= 80) return 'A';
        if (pct >= 70) return 'B+';
        if (pct >= 60) return 'B';
        if (pct >= 50) return 'C';
        if (pct >= 40) return 'D';
        return 'F';
    }

    // Modal Controls for Add Student
    function openAddModal() {
        document.getElementById('addStudentForm').reset();
        calculateAddPreview();
        document.getElementById('addStudentModal').classList.add('active');
        document.getElementById('addRollNo').focus();
    }

    function closeAddModal() {
        document.getElementById('addStudentModal').classList.remove('active');
    }

    function calculateAddPreview() {
        var j = parseFloat(document.getElementById('addJava').value) || 0;
        var de = parseFloat(document.getElementById('addDe').value) || 0;
        var dsa = parseFloat(document.getElementById('addDsa').value) || 0;
        var os = parseFloat(document.getElementById('addOs').value) || 0;

        var total = j + de + dsa + os;
        var pct = total / 4.0;
        var grade = calculateGrade(pct);

        var isPass = (j >= 40 && de >= 40 && dsa >= 40 && os >= 40);

        document.getElementById('addPrevTotal').innerText = total.toFixed(1) + ' / 400';
        document.getElementById('addPrevPct').innerText = pct.toFixed(2) + '%';
        document.getElementById('addPrevGrade').innerText = grade;

        var resElem = document.getElementById('addPrevResult');
        var noteElem = document.getElementById('addPreviewStatusNote');

        if (isPass) {
            resElem.innerHTML = '<span style="color: #2e7d32;">PASS</span>';
            noteElem.innerText = 'All subjects >= 40 marks';
        } else {
            resElem.innerHTML = '<span style="color: #c62828;">FAIL</span>';
            var failed = [];
            if (j < 40) failed.push('Java (' + j + ')');
            if (de < 40) failed.push('DE (' + de + ')');
            if (dsa < 40) failed.push('DSA (' + dsa + ')');
            if (os < 40) failed.push('OS (' + os + ')');
            noteElem.innerText = 'Failed in: ' + failed.join(', ');
        }
    }

    // Modal Controls for Edit Student
    function openEditModal(id, rollNo, name, java, de, dsa, os) {
        document.getElementById('editStudentId').value = id;
        document.getElementById('editRollNo').value = rollNo;
        document.getElementById('editName').value = name;
        document.getElementById('editJava').value = java;
        document.getElementById('editDe').value = de;
        document.getElementById('editDsa').value = dsa;
        document.getElementById('editOs').value = os;

        calculateEditPreview();
        document.getElementById('editStudentModal').classList.add('active');
        document.getElementById('editName').focus();
    }

    function closeEditModal() {
        document.getElementById('editStudentModal').classList.remove('active');
    }

    function calculateEditPreview() {
        var j = parseFloat(document.getElementById('editJava').value) || 0;
        var de = parseFloat(document.getElementById('editDe').value) || 0;
        var dsa = parseFloat(document.getElementById('editDsa').value) || 0;
        var os = parseFloat(document.getElementById('editOs').value) || 0;

        var total = j + de + dsa + os;
        var pct = total / 4.0;
        var grade = calculateGrade(pct);

        var isPass = (j >= 40 && de >= 40 && dsa >= 40 && os >= 40);

        document.getElementById('editPrevTotal').innerText = total.toFixed(1) + ' / 400';
        document.getElementById('editPrevPct').innerText = pct.toFixed(2) + '%';
        document.getElementById('editPrevGrade').innerText = grade;

        var resElem = document.getElementById('editPrevResult');
        var noteElem = document.getElementById('editPreviewStatusNote');

        if (isPass) {
            resElem.innerHTML = '<span style="color: #2e7d32;">PASS</span>';
            noteElem.innerText = 'All subjects >= 40 marks';
        } else {
            resElem.innerHTML = '<span style="color: #c62828;">FAIL</span>';
            var failed = [];
            if (j < 40) failed.push('Java (' + j + ')');
            if (de < 40) failed.push('DE (' + de + ')');
            if (dsa < 40) failed.push('DSA (' + dsa + ')');
            if (os < 40) failed.push('OS (' + os + ')');
            noteElem.innerText = 'Failed in: ' + failed.join(', ');
        }
    }

    // Close modals on escape key or clicking outside
    window.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            closeAddModal();
            closeEditModal();
        }
    });

    document.querySelectorAll('.modal-overlay').forEach(function(overlay) {
        overlay.addEventListener('click', function(e) {
            if (e.target === overlay) {
                overlay.classList.remove('active');
            }
        });
    });
</script>
</body>
</html>
