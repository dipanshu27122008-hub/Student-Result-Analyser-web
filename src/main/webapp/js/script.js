/**
 * Student Result Analysis System - Client-side scripts
 * Chart.js rendering and form helper interactions.
 */

document.addEventListener("DOMContentLoaded", function () {
    // File upload input file name display
    const fileInput = document.getElementById("fileInput");
    const fileChosenText = document.getElementById("fileChosenText");
    if (fileInput && fileChosenText) {
        fileInput.addEventListener("change", function () {
            if (this.files && this.files.length > 0) {
                const name = this.files[0].name;
                const sizeKb = Math.round(this.files[0].size / 1024);
                fileChosenText.innerHTML = `<strong>Selected:</strong> ${name} (${sizeKb} KB)`;
            } else {
                fileChosenText.textContent = "No file chosen";
            }
        });
    }

    // Auto-dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll(".alert-dismissible");
    alerts.forEach(function (alert) {
        setTimeout(function () {
            alert.style.transition = "opacity 0.5s ease";
            alert.style.opacity = "0";
            setTimeout(() => alert.remove(), 500);
        }, 5000);
    });
});

/**
 * Initializes Dashboard Charts using Chart.js
 */
function initDashboardCharts(data) {
    if (typeof Chart === "undefined") {
        console.warn("Chart.js library is not loaded.");
        return;
    }

    // Common Chart Defaults
    Chart.defaults.font.family = '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Arial, sans-serif';
    Chart.defaults.color = "#475569";

    // Chart 1: Pass vs Fail (Doughnut)
    const ctxPassFail = document.getElementById("chartPassFail");
    if (ctxPassFail) {
        new Chart(ctxPassFail, {
            type: "doughnut",
            data: {
                labels: ["Passed Students", "Failed Students"],
                datasets: [{
                    data: [data.passedCount, data.failedCount],
                    backgroundColor: ["#38a169", "#e53e3e"],
                    borderWidth: 2,
                    borderColor: "#ffffff"
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { position: "bottom" },
                    tooltip: {
                        callbacks: {
                            label: function (context) {
                                const total = data.passedCount + data.failedCount;
                                const pct = total > 0 ? ((context.parsed / total) * 100).toFixed(1) : 0;
                                return ` ${context.label}: ${context.parsed} (${pct}%)`;
                            }
                        }
                    }
                }
            }
        });
    }

    // Chart 2: Percentage Categories Distribution (Bar)
    const ctxCategory = document.getElementById("chartCategory");
    if (ctxCategory) {
        new Chart(ctxCategory, {
            type: "bar",
            data: {
                labels: ["75%+ (Distinction)", "65–74.99% (First)", "40–64.99% (Pass)", "Below 40% (Fail)"],
                datasets: [{
                    label: "Students",
                    data: [data.cat75Plus, data.cat65To74, data.cat40To64, data.catBelow40],
                    backgroundColor: ["#2b6cb0", "#3182ce", "#d69e2e", "#e53e3e"],
                    borderRadius: 6
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: { stepSize: 1 }
                    }
                }
            }
        });
    }

    // Chart 3: Average Marks by Subject (Bar)
    const ctxSubAvg = document.getElementById("chartSubjectAvg");
    if (ctxSubAvg) {
        new Chart(ctxSubAvg, {
            type: "bar",
            data: {
                labels: ["Java", "Digital Electronics", "DSA", "Operating System"],
                datasets: [{
                    label: "Class Average Marks (out of 100)",
                    data: [data.javaAvg, data.deAvg, data.dsaAvg, data.osAvg],
                    backgroundColor: "#1a365d",
                    borderRadius: 6
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        max: 100
                    }
                }
            }
        });
    }

    // Chart 4: Subject Pass Percentage (Bar)
    const ctxSubPass = document.getElementById("chartSubjectPass");
    if (ctxSubPass) {
        new Chart(ctxSubPass, {
            type: "bar",
            data: {
                labels: ["Java", "Digital Electronics", "DSA", "Operating System"],
                datasets: [{
                    label: "Subject Pass %",
                    data: [data.javaPassPct, data.dePassPct, data.dsaPassPct, data.osPassPct],
                    backgroundColor: "#38a169",
                    borderRadius: 6
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        max: 100,
                        ticks: {
                            callback: function (val) { return val + "%"; }
                        }
                    }
                }
            }
        });
    }
}
