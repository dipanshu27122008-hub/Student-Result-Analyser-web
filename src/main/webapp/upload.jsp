<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="activeNav" value="upload" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Import Data &bull; Student Result Analysis System</title>
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
                    <h1 class="page-title">Import Student Marks</h1>
                    <p class="page-subtitle">Batch ingestion supporting Microsoft Excel (.xlsx) and Comma-Separated Text (.txt) formats</p>
                </div>
                <a href="${pageContext.request.contextPath}/import-history" class="btn btn-outline">
                    🕒 View Import History
                </a>
            </div>

            <!-- Global Error / Warning Messages -->
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger">
                    <span>⚠️</span>
                    <div>${errorMessage}</div>
                </div>
            </c:if>

            <!-- Import Result Summary Card (Shown after upload) -->
            <c:if test="${not empty importResult}">
                <div class="table-card" style="margin-bottom: 24px;">
                    <div class="table-toolbar" style="background-color: ${importResult.status == 'Success' ? '#f0fff4' : (importResult.status == 'Partial Success' ? '#fffaf0' : '#fff5f5')};">
                        <div>
                            <div class="table-title">Import Processing Summary: ${importResult.fileName}</div>
                            <div style="font-size: 13px; color: var(--neutral-600); margin-top: 2px;">
                                Status: <strong>${importResult.status}</strong> &bull; Format: <strong>${importResult.fileType}</strong>
                            </div>
                        </div>
                        <div>
                            <a href="${pageContext.request.contextPath}/students" class="btn btn-sm btn-primary">View Student Roster &rarr;</a>
                            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-sm btn-outline">Go to Dashboard</a>
                        </div>
                    </div>

                    <div style="padding: 20px;">
                        <div class="kpi-grid" style="margin-bottom: 16px;">
                            <div class="kpi-card primary">
                                <div class="kpi-label">Total Rows Scanned</div>
                                <div class="kpi-value">${importResult.totalRows}</div>
                            </div>
                            <div class="kpi-card success">
                                <div class="kpi-label">Successfully Saved</div>
                                <div class="kpi-value">${importResult.importedRows}</div>
                            </div>
                            <div class="kpi-card danger">
                                <div class="kpi-label">Rejected Records</div>
                                <div class="kpi-value">${importResult.rejectedRows}</div>
                            </div>
                        </div>

                        <!-- Rejected Rows Diagnostics List -->
                        <c:if test="${not empty importResult.errorMessages}">
                            <div class="diagnostic-box">
                                <h5>⚠️ Import Warnings &amp; Rejected Row Details (${importResult.errorMessages.size()} items)</h5>
                                <p style="font-size: 13px; color: #742a2a; margin-bottom: 10px;">
                                    The following rows were rejected to maintain data integrity. Existing records were preserved:
                                </p>
                                <ul style="padding-left: 20px; font-size: 13px; color: #742a2a;">
                                    <c:forEach var="err" items="${importResult.errorMessages}">
                                        <li style="margin-bottom: 4px;">${err}</li>
                                    </c:forEach>
                                </ul>
                            </div>
                        </c:if>
                    </div>
                </div>
            </c:if>

            <!-- File Upload Card -->
            <div class="table-card">
                <div class="table-toolbar">
                    <div class="table-title">Upload Marks File</div>
                </div>
                <div style="padding: 24px;">
                    <form action="${pageContext.request.contextPath}/upload" method="post" enctype="multipart/form-data">
                        <div class="upload-box" onclick="document.getElementById('fileInput').click();">
                            <div class="upload-icon">📁</div>
                            <h3 style="font-size: 16px; font-weight: 700; color: var(--neutral-800); margin-bottom: 4px;">
                                Click to select file (.xlsx or .txt)
                            </h3>
                            <p style="font-size: 13px; color: var(--neutral-600); margin-bottom: 12px;">
                                Supports Microsoft Excel 2007+ (.xlsx) and Comma-Separated Values (.txt)
                            </p>
                            <input type="file" id="fileInput" name="file" accept=".xlsx, .txt" style="display: none;" required>
                            <span class="btn btn-outline btn-sm" id="selectBtn">Choose File</span>
                            <div id="fileChosenText" style="margin-top: 10px; font-size: 13px; color: var(--neutral-700);">No file chosen</div>
                        </div>

                        <div style="margin-top: 24px; display: flex; justify-content: flex-end; gap: 12px;">
                            <button type="reset" class="btn btn-outline">Reset</button>
                            <button type="submit" class="btn btn-primary" style="padding: 10px 24px;">
                                🚀 Import Data
                            </button>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Expected Formats Guidance Grid -->
            <div class="highlight-grid">
                <!-- Excel Guidance -->
                <div class="chart-card">
                    <div class="chart-card-header">
                        <div class="chart-card-title">📗 Expected Excel (.xlsx) Format</div>
                    </div>
                    <p style="font-size: 13px; color: var(--neutral-600); margin-bottom: 12px;">
                        First row must contain header columns. Each subject mark is out of 100.
                    </p>
                    <div class="table-responsive">
                        <table class="data-table" style="font-size: 12px;">
                            <thead>
                                <tr>
                                    <th>RollNo</th>
                                    <th>Name</th>
                                    <th>Java</th>
                                    <th>DE</th>
                                    <th>DSA</th>
                                    <th>OS</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td>101</td>
                                    <td>Rahul Kumar</td>
                                    <td>82</td>
                                    <td>76</td>
                                    <td>71</td>
                                    <td>88</td>
                                </tr>
                                <tr>
                                    <td>102</td>
                                    <td>Amit Sharma</td>
                                    <td>65</td>
                                    <td>72</td>
                                    <td>68</td>
                                    <td>70</td>
                                </tr>
                                <tr>
                                    <td>103</td>
                                    <td>Priya Singh</td>
                                    <td>91</td>
                                    <td>85</td>
                                    <td>89</td>
                                    <td>94</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- TXT Guidance -->
                <div class="chart-card">
                    <div class="chart-card-header">
                        <div class="chart-card-title">📄 Expected Text (.txt) Format</div>
                    </div>
                    <p style="font-size: 13px; color: var(--neutral-600); margin-bottom: 12px;">
                        Comma-separated values with header on line 1:
                    </p>
                    <pre style="background: #f8fafc; padding: 14px; border-radius: var(--radius-sm); border: 1px solid var(--neutral-300); font-size: 12.5px; font-family: monospace; overflow-x: auto;">
RollNo,Name,Java,DE,DSA,OS
101,Rahul Kumar,82,76,71,88
102,Amit Sharma,65,72,68,70
103,Priya Singh,91,85,89,94</pre>
                    <div style="margin-top: 12px; font-size: 12px; color: var(--neutral-600);">
                        &bull; Duplicate Roll Numbers are automatically rejected to preserve records.<br>
                        &bull; Marks must be numeric values between 0 and 100.
                    </div>
                </div>
            </div>

        </main>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>
