<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="activeNav" value="history" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Import History &bull; Student Result Analysis System</title>
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
                    <h1 class="page-title">File Ingestion Audit History</h1>
                    <p class="page-subtitle">Historical audit trail of all student data files ingested into MySQL</p>
                </div>
                <a href="${pageContext.request.contextPath}/upload" class="btn btn-primary">
                    📥 Upload New File
                </a>
            </div>

            <div class="table-card">
                <div class="table-toolbar">
                    <div class="table-title">Import Operations Log</div>
                </div>
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th># ID</th>
                                <th>File Name</th>
                                <th class="text-center">Format</th>
                                <th class="text-center">Total Rows</th>
                                <th class="text-center">Imported</th>
                                <th class="text-center">Rejected</th>
                                <th class="text-center">Status</th>
                                <th>Import Timestamp</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty historyList}">
                                    <c:forEach var="h" items="${historyList}">
                                        <tr>
                                            <td class="fw-bold">#${h.id}</td>
                                            <td>
                                                <strong>${h.fileName}</strong>
                                            </td>
                                            <td class="text-center">
                                                <span class="badge badge-grade">${h.fileType}</span>
                                            </td>
                                            <td class="text-center fw-bold">${h.totalRecords}</td>
                                            <td class="text-center" style="color: #22543d; font-weight: 700;">
                                                ${h.successfulRecords}
                                            </td>
                                            <td class="text-center" style="color: ${h.rejectedRecords > 0 ? '#742a2a' : 'inherit'}; font-weight: 700;">
                                                ${h.rejectedRecords}
                                            </td>
                                            <td class="text-center">
                                                <span class="badge ${h.status == 'Success' ? 'badge-pass' : (h.status == 'Partial Success' ? 'badge-cat' : 'badge-fail')}">
                                                    ${h.status}
                                                </span>
                                            </td>
                                            <td>
                                                <fmt:formatDate value="${h.importedAt}" pattern="dd-MM-yyyy hh:mm:ss a"/>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="8" class="text-center" style="padding: 30px; color: var(--neutral-600);">
                                            No import history recorded yet. Ingest your first Excel or TXT dataset to populate this log.
                                            <div style="margin-top: 10px;">
                                                <a href="${pageContext.request.contextPath}/upload" class="btn btn-sm btn-primary">Import File Now</a>
                                            </div>
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>

        </main>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>
