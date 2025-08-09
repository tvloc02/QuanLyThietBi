// Báo cáo JavaScript - Tích hợp với dashboard.js
// Configuration
const REPORT_API_BASE = '/api/bao-cao';
const STATS_API_BASE = '/api/thong-ke';
const CHART_COLORS = {
    primary: '#1e40af',
    success: '#02ec53',
    warning: '#f59e0b',
    danger: '#ef4444',
    info: '#3b82f6',
    secondary: '#6b7280'
};

// Global chart instances
let maintenanceTrendChart = null;
let statusDistributionChart = null;
let equipmentCategoryChart = null;
let maintenanceCostChart = null;

// Initialize Report Dashboard
document.addEventListener('DOMContentLoaded', function() {
    initializeReport();
    setupReportEventListeners();
    loadReportData();
});

async function initializeReport() {
    try {
        showNotification('Đang tải báo cáo...', 'info');

        await Promise.all([
            loadSummaryData(),
            initializeCharts(),
            loadRecentRequests(),
            loadEquipmentStatus()
        ]);

        showNotification('Báo cáo đã được tải thành công!', 'success');
    } catch (error) {
        console.error('Error initializing report:', error);
        showNotification('Có lỗi khi tải báo cáo', 'error');
    }
}

// Load Summary Data
async function loadSummaryData() {
    try {
        const fromDate = document.getElementById('fromDate')?.value;
        const toDate = document.getElementById('toDate')?.value;

        const params = new URLSearchParams();
        if (fromDate) params.append('tuNgay', fromDate);
        if (toDate) params.append('denNgay', toDate);

        const [equipmentStats, maintenanceStats, alertStats] = await Promise.all([
            fetchAPI(`${STATS_API_BASE}/thiet-bi?${params}`),
            fetchAPI(`${STATS_API_BASE}/bao-tri?${params}`),
            fetchAPI(`${STATS_API_BASE}/canh-bao?${params}`)
        ]);

        // Update summary cards with real data
        if (equipmentStats) {
            updateSummaryCard('tongThietBi', equipmentStats.tongThietBi || 0);
            updateTrendIndicator('thietBiMoi', equipmentStats.thietBiMoi || 0, 'positive');
        }

        if (maintenanceStats) {
            updateSummaryCard('yeuCauHoanThanh', maintenanceStats.daHoanThanh || 0);
            updateSummaryCard('yeuCauDangXuLy', maintenanceStats.dangThucHien || 0);
            updateTrendIndicator('tyLeHoanThanh', maintenanceStats.tyLeHoanThanh || 0, 'positive');
        }

        if (alertStats) {
            updateSummaryCard('canhBaoNghiemTrong', alertStats.nghiemTrong || 0);
            updateTrendIndicator('canhBaoMoi', alertStats.moi || 0, 'negative');
        }

    } catch (error) {
        console.error('Error loading summary data:', error);
    }
}

// Update summary card value
function updateSummaryCard(elementId, value) {
    const element = document.querySelector(`[th\\:text*="${elementId}"]`) || document.getElementById(elementId);
    if (element) {
        element.textContent = typeof value === 'number' ? value.toLocaleString('vi-VN') : value;
    }
}

// Update trend indicator
function updateTrendIndicator(type, value, trend) {
    const indicators = document.querySelectorAll('.summary-change');
    indicators.forEach(indicator => {
        if (indicator.textContent.includes(type)) {
            indicator.className = `summary-change ${trend}`;
            const icon = indicator.querySelector('i');
            if (icon) {
                icon.className = trend === 'positive' ? 'fas fa-arrow-up' :
                    trend === 'negative' ? 'fas fa-arrow-down' : 'fas fa-minus';
            }
        }
    });
}

// Initialize Charts
async function initializeCharts() {
    try {
        await Promise.all([
            initMaintenanceTrendChart(),
            initStatusDistributionChart(),
            initEquipmentCategoryChart(),
            initMaintenanceCostChart()
        ]);
    } catch (error) {
        console.error('Error initializing charts:', error);
    }
}

// Maintenance Trend Chart
async function initMaintenanceTrendChart() {
    const ctx = document.getElementById('maintenanceTrendChart');
    if (!ctx) return;

    try {
        const data = await fetchAPI(`${STATS_API_BASE}/xu-huong-bao-tri?days=30`);

        const chartData = data?.trend || generateSampleTrendData();

        maintenanceTrendChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: chartData.labels,
                datasets: [
                    {
                        label: 'Yêu cầu bảo trì',
                        data: chartData.requests,
                        borderColor: CHART_COLORS.primary,
                        backgroundColor: `${CHART_COLORS.primary}20`,
                        tension: 0.4,
                        fill: true
                    },
                    {
                        label: 'Hoàn thành',
                        data: chartData.completed,
                        borderColor: CHART_COLORS.success,
                        backgroundColor: `${CHART_COLORS.success}20`,
                        tension: 0.4,
                        fill: true
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'top',
                    },
                    tooltip: {
                        mode: 'index',
                        intersect: false,
                    }
                },
                scales: {
                    x: {
                        display: true,
                        title: {
                            display: true,
                            text: 'Thời gian'
                        }
                    },
                    y: {
                        display: true,
                        title: {
                            display: true,
                            text: 'Số lượng'
                        },
                        beginAtZero: true
                    }
                },
                interaction: {
                    mode: 'nearest',
                    axis: 'x',
                    intersect: false
                }
            }
        });
    } catch (error) {
        console.error('Error creating maintenance trend chart:', error);
        showChartError('maintenanceTrendChart');
    }
}

// Status Distribution Chart
async function initStatusDistributionChart() {
    const ctx = document.getElementById('statusDistributionChart');
    if (!ctx) return;

    try {
        const data = await fetchAPI(`${STATS_API_BASE}/phan-bo-trang-thai`);

        const chartData = data?.distribution || {
            labels: ['Hoạt động', 'Bảo trì', 'Hỏng', 'Ngừng sử dụng'],
            values: [150, 25, 8, 12]
        };

        statusDistributionChart = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: chartData.labels,
                datasets: [{
                    data: chartData.values,
                    backgroundColor: [
                        CHART_COLORS.success,
                        CHART_COLORS.warning,
                        CHART_COLORS.danger,
                        CHART_COLORS.secondary
                    ],
                    borderWidth: 2,
                    borderColor: '#ffffff'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            usePointStyle: true,
                            padding: 15
                        }
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                const total = context.dataset.data.reduce((a, b) => a + b, 0);
                                const percentage = ((context.parsed * 100) / total).toFixed(1);
                                return `${context.label}: ${context.parsed} (${percentage}%)`;
                            }
                        }
                    }
                }
            }
        });
    } catch (error) {
        console.error('Error creating status distribution chart:', error);
        showChartError('statusDistributionChart');
    }
}

// Equipment Category Chart
async function initEquipmentCategoryChart() {
    const ctx = document.getElementById('equipmentCategoryChart');
    if (!ctx) return;

    try {
        const data = await fetchAPI(`${STATS_API_BASE}/thiet-bi-theo-danh-muc`);

        const chartData = data?.categories || {
            labels: ['Thiết bị IT', 'Phòng học', 'Phòng lab', 'Văn phòng', 'Hệ thống điện'],
            values: [45, 38, 25, 18, 12]
        };

        equipmentCategoryChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: chartData.labels,
                datasets: [{
                    label: 'Số lượng thiết bị',
                    data: chartData.values,
                    backgroundColor: [
                        `${CHART_COLORS.primary}80`,
                        `${CHART_COLORS.success}80`,
                        `${CHART_COLORS.warning}80`,
                        `${CHART_COLORS.info}80`,
                        `${CHART_COLORS.danger}80`
                    ],
                    borderColor: [
                        CHART_COLORS.primary,
                        CHART_COLORS.success,
                        CHART_COLORS.warning,
                        CHART_COLORS.info,
                        CHART_COLORS.danger
                    ],
                    borderWidth: 2,
                    borderRadius: 6,
                    borderSkipped: false
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                return `${context.label}: ${context.parsed.y} thiết bị`;
                            }
                        }
                    }
                },
                scales: {
                    x: {
                        grid: {
                            display: false
                        }
                    },
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: '#f3f4f6'
                        }
                    }
                }
            }
        });
    } catch (error) {
        console.error('Error creating equipment category chart:', error);
        showChartError('equipmentCategoryChart');
    }
}

// Maintenance Cost Chart
async function initMaintenanceCostChart() {
    const ctx = document.getElementById('maintenanceCostChart');
    if (!ctx) return;

    try {
        const data = await fetchAPI(`${STATS_API_BASE}/chi-phi-bao-tri?months=6`);

        const chartData = data?.costs || {
            labels: ['T2/2025', 'T3/2025', 'T4/2025', 'T5/2025', 'T6/2025', 'T7/2025'],
            values: [85, 92, 78, 115, 98, 125],
            budget: [100, 100, 100, 100, 100, 100]
        };

        maintenanceCostChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: chartData.labels,
                datasets: [
                    {
                        label: 'Chi phí thực tế (triệu VNĐ)',
                        data: chartData.values,
                        backgroundColor: `${CHART_COLORS.primary}80`,
                        borderColor: CHART_COLORS.primary,
                        borderWidth: 2,
                        borderRadius: 6
                    },
                    {
                        label: 'Ngân sách (triệu VNĐ)',
                        data: chartData.budget,
                        type: 'line',
                        borderColor: CHART_COLORS.danger,
                        backgroundColor: 'transparent',
                        borderWidth: 3,
                        pointBackgroundColor: CHART_COLORS.danger,
                        pointBorderColor: '#ffffff',
                        pointBorderWidth: 2,
                        pointRadius: 5,
                        tension: 0.4
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'top'
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                return `${context.dataset.label}: ${context.parsed.y} triệu VNĐ`;
                            }
                        }
                    }
                },
                scales: {
                    x: {
                        grid: {
                            display: false
                        }
                    },
                    y: {
                        beginAtZero: true,
                        title: {
                            display: true,
                            text: 'Chi phí (triệu VNĐ)'
                        },
                        grid: {
                            color: '#f3f4f6'
                        }
                    }
                }
            }
        });
    } catch (error) {
        console.error('Error creating maintenance cost chart:', error);
        showChartError('maintenanceCostChart');
    }
}

// Load Recent Requests Table
async function loadRecentRequests() {
    const tableBody = document.getElementById('recentRequestsTable');
    if (!tableBody) return;

    try {
        const data = await fetchAPI(`${REPORT_API_BASE}/yeu-cau-gan-day?limit=10`);

        if (data?.requests && data.requests.length > 0) {
            tableBody.innerHTML = data.requests.map(request => `
                <tr>
                    <td>
                        <strong>${request.maYeuCau}</strong>
                    </td>
                    <td>
                        <div class="equipment-info">
                            <div class="equipment-name">${request.tenThietBi}</div>
                            <small class="text-muted">${request.maThietBi}</small>
                        </div>
                    </td>
                    <td>
                        <span class="status-badge ${getStatusClass(request.trangThai)}">
                            ${getStatusText(request.trangThai)}
                        </span>
                    </td>
                    <td>
                        <div class="date-info">
                            <div>${formatDate(request.ngayTao)}</div>
                            <small class="text-muted">${formatTimeAgo(request.ngayTao)}</small>
                        </div>
                    </td>
                </tr>
            `).join('');
        } else {
            // Show sample data when API returns empty
            tableBody.innerHTML = generateSampleRequestsTable();
        }
    } catch (error) {
        console.error('Error loading recent requests:', error);
        tableBody.innerHTML = generateSampleRequestsTable();
    }
}

// Load Equipment Status Table
async function loadEquipmentStatus() {
    const tableBody = document.getElementById('equipmentStatusTable');
    if (!tableBody) return;

    try {
        const data = await fetchAPI(`${REPORT_API_BASE}/thiet-bi-can-chu-y?limit=10`);

        if (data?.equipment && data.equipment.length > 0) {
            tableBody.innerHTML = data.equipment.map(equipment => `
                <tr>
                    <td>
                        <strong>${equipment.maThietBi}</strong>
                    </td>
                    <td>
                        <div class="equipment-details">
                            <div class="equipment-name">${equipment.tenThietBi}</div>
                            <small class="text-muted">${equipment.viTriLapDat}</small>
                        </div>
                    </td>
                    <td>
                        <span class="status-badge ${getStatusClass(equipment.trangThai)}">
                            ${getStatusText(equipment.trangThai)}
                        </span>
                    </td>
                    <td>
                        <div class="action-buttons-small">
                            <button class="btn btn-sm btn-outline-primary" onclick="viewEquipment('${equipment.idThietBi}')" title="Xem chi tiết">
                                <i class="fas fa-eye"></i>
                            </button>
                            <button class="btn btn-sm btn-outline-warning" onclick="createMaintenanceRequest('${equipment.idThietBi}')" title="Tạo yêu cầu bảo trì">
                                <i class="fas fa-tools"></i>
                            </button>
                        </div>
                    </td>
                </tr>
            `).join('');
        } else {
            tableBody.innerHTML = generateSampleEquipmentTable();
        }
    } catch (error) {
        console.error('Error loading equipment status:', error);
        tableBody.innerHTML = generateSampleEquipmentTable();
    }
}

// Generate sample data functions
function generateSampleRequestsTable() {
    return `
        <tr>
            <td><strong>YC001</strong></td>
            <td>
                <div class="equipment-info">
                    <div class="equipment-name">Máy chiếu phòng 101</div>
                    <small class="text-muted">PRJ001</small>
                </div>
            </td>
            <td><span class="status-badge warning">Đang xử lý</span></td>
            <td>
                <div class="date-info">
                    <div>30/07/2025</div>
                    <small class="text-muted">2 giờ trước</small>
                </div>
            </td>
        </tr>
        <tr>
            <td><strong>YC002</strong></td>
            <td>
                <div class="equipment-info">
                    <div class="equipment-name">Máy tính phòng 102</div>
                    <small class="text-muted">PC002</small>
                </div>
            </td>
            <td><span class="status-badge success">Hoàn thành</span></td>
            <td>
                <div class="date-info">
                    <div>29/07/2025</div>
                    <small class="text-muted">1 ngày trước</small>
                </div>
            </td>
        </tr>
        <tr>
            <td><strong>YC003</strong></td>
            <td>
                <div class="equipment-info">
                    <div class="equipment-name">Điều hòa phòng 103</div>
                    <small class="text-muted">AC003</small>
                </div>
            </td>
            <td><span class="status-badge danger">Khẩn cấp</span></td>
            <td>
                <div class="date-info">
                    <div>30/07/2025</div>
                    <small class="text-muted">30 phút trước</small>
                </div>
            </td>
        </tr>
    `;
}

function generateSampleEquipmentTable() {
    return `
        <tr>
            <td><strong>AC001</strong></td>
            <td>
                <div class="equipment-details">
                    <div class="equipment-name">Điều hòa phòng 101</div>
                    <small class="text-muted">Phòng học 101</small>
                </div>
            </td>
            <td><span class="status-badge warning">Cần bảo trì</span></td>
            <td>
                <div class="action-buttons-small">
                    <button class="btn btn-sm btn-outline-primary" onclick="viewEquipment('1')" title="Xem chi tiết">
                        <i class="fas fa-eye"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-warning" onclick="createMaintenanceRequest('1')" title="Tạo yêu cầu bảo trì">
                        <i class="fas fa-tools"></i>
                    </button>
                </div>
            </td>
        </tr>
        <tr>
            <td><strong>PRJ002</strong></td>
            <td>
                <div class="equipment-details">
                    <div class="equipment-name">Máy chiếu phòng 205</div>
                    <small class="text-muted">Phòng học 205</small>
                </div>
            </td>
            <td><span class="status-badge danger">Hỏng</span></td>
            <td>
                <div class="action-buttons-small">
                    <button class="btn btn-sm btn-outline-primary" onclick="viewEquipment('2')" title="Xem chi tiết">
                        <i class="fas fa-eye"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger" onclick="createMaintenanceRequest('2')" title="Sửa chữa khẩn cấp">
                        <i class="fas fa-exclamation-triangle"></i>
                    </button>
                </div>
            </td>
        </tr>
        <tr>
            <td><strong>PC005</strong></td>
            <td>
                <div class="equipment-details">
                    <div class="equipment-name">Máy tính văn phòng</div>
                    <small class="text-muted">Phòng giáo vụ</small>
                </div>
            </td>
            <td><span class="status-badge info">Kiểm tra định kỳ</span></td>
            <td>
                <div class="action-buttons-small">
                    <button class="btn btn-sm btn-outline-primary" onclick="viewEquipment('5')" title="Xem chi tiết">
                        <i class="fas fa-eye"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-info" onclick="scheduleInspection('5')" title="Lên lịch kiểm tra">
                        <i class="fas fa-calendar-check"></i>
                    </button>
                </div>
            </td>
        </tr>
    `;
}

function generateSampleTrendData() {
    const labels = [];
    const requests = [];
    const completed = [];

    for (let i = 29; i >= 0; i--) {
        const date = new Date();
        date.setDate(date.getDate() - i);
        labels.push(date.toLocaleDateString('vi-VN', { month: 'short', day: 'numeric' }));

        // Generate random but realistic data
        const dailyRequests = Math.floor(Math.random() * 10) + 2;
        const dailyCompleted = Math.floor(dailyRequests * (0.7 + Math.random() * 0.3));

        requests.push(dailyRequests);
        completed.push(dailyCompleted);
    }

    return { labels, requests, completed };
}

// Event Listeners
function setupReportEventListeners() {
    // Date range change
    const fromDate = document.getElementById('fromDate');
    const toDate = document.getElementById('toDate');

    if (fromDate) fromDate.addEventListener('change', debounce(updateReport, 1000));
    if (toDate) toDate.addEventListener('change', debounce(updateReport, 1000));

    // Chart period change
    const trendPeriod = document.getElementById('trendPeriod');
    if (trendPeriod) trendPeriod.addEventListener('change', updateTrendChart);

    // Keyboard shortcuts
    document.addEventListener('keydown', function(e) {
        if (e.ctrlKey || e.metaKey) {
            switch(e.key) {
                case 'e':
                    e.preventDefault();
                    exportReport('excel');
                    break;
                case 'p':
                    e.preventDefault();
                    printReport();
                    break;
                case 'r':
                    e.preventDefault();
                    updateReport();
                    break;
            }
        }
    });
}

// Update functions
async function updateReport() {
    showNotification('Đang cập nhật báo cáo...', 'info');

    try {
        await Promise.all([
            loadSummaryData(),
            updateAllCharts(),
            loadRecentRequests(),
            loadEquipmentStatus()
        ]);

        showNotification('Báo cáo đã được cập nhật!', 'success');
    } catch (error) {
        console.error('Error updating report:', error);
        showNotification('Có lỗi khi cập nhật báo cáo', 'error');
    }
}

async function updateTrendChart() {
    if (!maintenanceTrendChart) return;

    const period = document.getElementById('trendPeriod')?.value || 30;

    try {
        const data = await fetchAPI(`${STATS_API_BASE}/xu-huong-bao-tri?days=${period}`);
        const chartData = data?.trend || generateSampleTrendData();

        maintenanceTrendChart.data.labels = chartData.labels;
        maintenanceTrendChart.data.datasets[0].data = chartData.requests;
        maintenanceTrendChart.data.datasets[1].data = chartData.completed;
        maintenanceTrendChart.update();

    } catch (error) {
        console.error('Error updating trend chart:', error);
    }
}

async function updateAllCharts() {
    const charts = [
        { chart: maintenanceTrendChart, init: initMaintenanceTrendChart },
        { chart: statusDistributionChart, init: initStatusDistributionChart },
        { chart: equipmentCategoryChart, init: initEquipmentCategoryChart },
        { chart: maintenanceCostChart, init: initMaintenanceCostChart }
    ];

    for (const { chart, init } of charts) {
        if (chart) {
            chart.destroy();
        }
        await init();
    }
}

// Export functions
async function exportReport(format) {
    try {
        showNotification(`Đang xuất báo cáo ${format.toUpperCase()}...`, 'info');

        const fromDate = document.getElementById('fromDate')?.value;
        const toDate = document.getElementById('toDate')?.value;

        const params = new URLSearchParams({
            format: format,
            type: 'tong-hop'
        });

        if (fromDate) params.append('tuNgay', fromDate);
        if (toDate) params.append('denNgay', toDate);

        const response = await fetch(`${REPORT_API_BASE}/xuat?${params}`, {
            method: 'GET',
            headers: {
                'Accept': format === 'excel' ? 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' : 'application/pdf'
            }
        });

        if (!response.ok) {
            throw new Error(`Export failed: ${response.status}`);
        }

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `bao-cao-tong-hop-${new Date().toISOString().split('T')[0]}.${format === 'excel' ? 'xlsx' : 'pdf'}`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);

        showNotification(`Báo cáo ${format.toUpperCase()} đã được tải xuống!`, 'success');

    } catch (error) {
        console.error('Error exporting report:', error);
        showNotification(`Có lỗi khi xuất báo cáo ${format.toUpperCase()}`, 'error');
    }
}

function printReport() {
    const originalTitle = document.title;
    document.title = `Báo cáo tổng hợp - ${new Date().toLocaleDateString('vi-VN')}`;

    // Add print-specific styles
    const printStyles = `
        <style media="print">
            @page { margin: 1cm; }
            .no-print { display: none !important; }
            .chart-body { height: 300px !important; }
        </style>
    `;

    document.head.insertAdjacentHTML('beforeend', printStyles);

    window.print();

    document.title = originalTitle;
}

// Action functions
function viewEquipment(equipmentId) {
    window.open(`/thiet-bi/chi-tiet/${equipmentId}`, '_blank');
}

function createMaintenanceRequest(equipmentId) {
    window.location.href = `/bao-tri/tao-yeu-cau?thietBiId=${equipmentId}`;
}

function scheduleInspection(equipmentId) {
    window.location.href = `/bao-tri/kiem-tra-dinh-ky?thietBiId=${equipmentId}`;
}

// Utility functions
function getStatusClass(status) {
    const statusMap = {
        'HOAT_DONG': 'success',
        'BAO_TRI': 'warning',
        'HONG': 'danger',
        'NGUNG_SU_DUNG': 'secondary',
        'CHO_DUYET': 'info',
        'DANG_XU_LY': 'warning',
        'HOAN_THANH': 'success',
        'KHAN_CAP': 'danger'
    };
    return statusMap[status] || 'secondary';
}

function getStatusText(status) {
    const statusMap = {
        'HOAT_DONG': 'Hoạt động',
        'BAO_TRI': 'Bảo trì',
        'HONG': 'Hỏng',
        'NGUNG_SU_DUNG': 'Ngừng sử dụng',
        'CHO_DUYET': 'Chờ duyệt',
        'DANG_XU_LY': 'Đang xử lý',
        'HOAN_THANH': 'Hoàn thành',
        'KHAN_CAP': 'Khẩn cấp'
    };
    return statusMap[status] || status;
}

function formatDate(dateString) {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('vi-VN');
}

function formatTimeAgo(dateString) {
    if (!dateString) return '';

    const now = new Date();
    const date = new Date(dateString);
    const diffMs = now - date;
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMins / 60);
    const diffDays = Math.floor(diffHours / 24);

    if (diffMins < 60) return `${diffMins} phút trước`;
    if (diffHours < 24) return `${diffHours} giờ trước`;
    if (diffDays < 30) return `${diffDays} ngày trước`;
    return formatDate(dateString);
}

function showChartError(chartId) {
    const canvas = document.getElementById(chartId);
    if (canvas) {
        const container = canvas.parentElement;
        container.innerHTML = `
            <div class="chart-loading">
                <i class="fas fa-exclamation-triangle text-warning"></i>
                <p>Không thể tải biểu đồ</p>
            </div>
        `;
    }
}

function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Load report data on page load
async function loadReportData() {
    try {
        // Load additional data that's not covered by initialization
        await loadPerformanceMetrics();
    } catch (error) {
        console.error('Error loading additional report data:', error);
    }
}

async function loadPerformanceMetrics() {
    try {
        const metrics = await fetchAPI(`${STATS_API_BASE}/chi-so-hieu-suat`);

        if (metrics) {
            // Update OEE average
            const oeeElement = document.querySelector('[th\\:text*="oeeAverage"]');
            if (oeeElement) {
                oeeElement.textContent = `${metrics.oeeAverage || 85.2}%`;
            }

            // Update MTBF average
            const mtbfElement = document.querySelector('[th\\:text*="mtbfAverage"]');
            if (mtbfElement) {
                mtbfElement.textContent = `${metrics.mtbfAverage || 320}h`;
            }

            // Update MTTR average
            const mttrElement = document.querySelector('[th\\:text*="mttrAverage"]');
            if (mttrElement) {
                mttrElement.textContent = `${metrics.mttrAverage || 4.2}h`;
            }

            // Update cost total
            const costElement = document.querySelector('[th\\:text*="costTotal"]');
            if (costElement) {
                costElement.textContent = `${metrics.costTotal || '125M'} VNĐ`;
            }
        }
    } catch (error) {
        console.error('Error loading performance metrics:', error);
    }
}

// Auto-refresh functionality
let autoRefreshInterval;

function startAutoRefresh(intervalMinutes = 5) {
    stopAutoRefresh();
    autoRefreshInterval = setInterval(() => {
        updateReport();
    }, intervalMinutes * 60 * 1000);
}

function stopAutoRefresh() {
    if (autoRefreshInterval) {
        clearInterval(autoRefreshInterval);
        autoRefreshInterval = null;
    }
}

// Start auto-refresh on page load
document.addEventListener('DOMContentLoaded', function() {
    startAutoRefresh(5); // Refresh every 5 minutes
});

// Stop auto-refresh when page is hidden
document.addEventListener('visibilitychange', function() {
    if (document.hidden) {
        stopAutoRefresh();
    } else {
        startAutoRefresh(5);
    }
});

// Export global functions for HTML onclick handlers
window.updateReport = updateReport;
window.updateTrendChart = updateTrendChart;
window.exportReport = exportReport;
window.printReport = printReport;
window.viewEquipment = viewEquipment;
window.createMaintenanceRequest = createMaintenanceRequest;
window.scheduleInspection = scheduleInspection;