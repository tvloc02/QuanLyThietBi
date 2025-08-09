// Configuration
const API_BASE = '/api';
const REFRESH_INTERVAL = 5 * 60 * 1000; // 5 minutes

// Initialize Dashboard
document.addEventListener('DOMContentLoaded', function() {
    initializeDashboard();
    setupEventListeners();
    setupKeyboardShortcuts();
    startAutoRefresh();
});

async function initializeDashboard() {
    try {
        showNotification('Đang tải dashboard...', 'info');

        await Promise.all([
            loadDashboardData(),
            loadRecentActivities(),
            loadSystemStatus()
        ]);

        showNotification('Dashboard đã được tải thành công!', 'success');
    } catch (error) {
        console.error('Error initializing dashboard:', error);
        showNotification('Có lỗi khi tải dashboard', 'error');
    }
}

async function loadDashboardData() {
    try {
        // Load equipment statistics
        const deviceStats = await fetchAPI('/thong-ke/thiet-bi');
        if (deviceStats) {
            updateStatCard('tongThietBi', deviceStats.tongThietBi || 0);
        }

        // Load maintenance statistics
        const maintenanceStats = await fetchAPI('/thong-ke/bao-tri');
        if (maintenanceStats) {
            updateStatCard('tongYeuCauBaoTri', maintenanceStats.tongYeuCau || 0);
        }

        // Update system status in welcome section
        updateSystemStatusBadge();

    } catch (error) {
        console.error('Error loading dashboard data:', error);
    }
}

async function loadRecentActivities() {
    try {
        const activities = await fetchAPI('/hoat-dong-gan-day');
        const container = document.getElementById('recentActivities');

        if (activities && activities.length > 0) {
            container.innerHTML = activities.slice(0, 6).map(activity => `
                    <div class="activity-item">
                        <div class="activity-icon" style="background: ${getActivityColor(activity.type)}">
                            <i class="fas ${getActivityIcon(activity.type)}"></i>
                        </div>
                        <div class="activity-content">
                            <div class="activity-title">${activity.title}</div>
                            <div class="activity-time">${formatTime(activity.timestamp)}</div>
                        </div>
                    </div>
                `).join('');
        } else {
            container.innerHTML = `
                    <div class="activity-item">
                        <div class="activity-icon" style="background: var(--success-green)">
                            <i class="fas fa-check"></i>
                        </div>
                        <div class="activity-content">
                            <div class="activity-title">Hoàn thành bảo trì thiết bị #TB001</div>
                            <div class="activity-time">5 phút trước</div>
                        </div>
                    </div>
                    <div class="activity-item">
                        <div class="activity-icon" style="background: var(--warning-orange)">
                            <i class="fas fa-exclamation"></i>
                        </div>
                        <div class="activity-content">
                            <div class="activity-title">Cảnh báo thiết bị cần kiểm tra</div>
                            <div class="activity-time">15 phút trước</div>
                        </div>
                    </div>
                    <div class="activity-item">
                        <div class="activity-icon" style="background: var(--primary-blue)">
                            <i class="fas fa-user-plus"></i>
                        </div>
                        <div class="activity-content">
                            <div class="activity-title">Thêm người dùng mới</div>
                            <div class="activity-time">30 phút trước</div>
                        </div>
                    </div>
                `;
        }
    } catch (error) {
        console.error('Error loading activities:', error);
        document.getElementById('recentActivities').innerHTML = '<p class="text-muted text-center">Không thể tải hoạt động gần đây</p>';
    }
}

async function loadSystemStatus() {
    try {
        const status = await fetchAPI('/he-thong/trang-thai');
        const container = document.getElementById('systemStatus');

        if (status && Object.keys(status).length > 0) {
            container.innerHTML = Object.entries(status).map(([key, value]) => `
                    <div class="status-item">
                        <div class="d-flex align-items-center">
                            <span class="status-indicator ${getStatusClass(value.status)}"></span>
                            <span>${value.name}</span>
                        </div>
                        <span class="badge bg-${getStatusBadgeClass(value.status)}">${getStatusText(value.status)}</span>
                    </div>
                `).join('');
        } else {
            container.innerHTML = `
                    <div class="status-item">
                        <div class="d-flex align-items-center">
                            <span class="status-indicator status-online"></span>
                            <span>Database Server</span>
                        </div>
                        <span class="badge bg-success">Online</span>
                    </div>
                    <div class="status-item">
                        <div class="d-flex align-items-center">
                            <span class="status-indicator status-online"></span>
                            <span>Application Server</span>
                        </div>
                        <span class="badge bg-success">Online</span>
                    </div>
                    <div class="status-item">
                        <div class="d-flex align-items-center">
                            <span class="status-indicator status-warning"></span>
                            <span>Backup Service</span>
                        </div>
                        <span class="badge bg-warning">Warning</span>
                    </div>
                    <div class="status-item">
                        <div class="d-flex align-items-center">
                            <span class="status-indicator status-online"></span>
                            <span>Email Service</span>
                        </div>
                        <span class="badge bg-success">Online</span>
                    </div>
                `;
        }

        updateOverallSystemStatus(status);
    } catch (error) {
        console.error('Error loading system status:', error);
        document.getElementById('systemStatus').innerHTML = '<p class="text-muted text-center">Không thể tải trạng thái hệ thống</p>';
    }
}

// Event Listeners
function setupEventListeners() {
    document.querySelectorAll('.action-btn').forEach(btn => {
        btn.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-2px)';
        });
        btn.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
        });
    });
}

function setupKeyboardShortcuts() {
    document.addEventListener('keydown', function(e) {
        if (e.ctrlKey || e.metaKey) {
            switch(e.key) {
                case '1':
                    e.preventDefault();
                    window.location.href = '/bao-tri/tao-yeu-cau';
                    break;
                case '2':
                    e.preventDefault();
                    window.location.href = '/thiet-bi/them-moi';
                    break;
                case '3':
                    e.preventDefault();
                    window.location.href = '/nguoi-dung/them-moi';
                    break;
                case 'r':
                    e.preventDefault();
                    refreshDashboard();
                    break;
                case 'k':
                    e.preventDefault();
                    toggleSearch();
                    break;
            }
        }
        if (e.key === 'Escape') {
            closeAllModals();
        }
    });
}

function startAutoRefresh() {
    setInterval(() => {
        loadDashboardData();
        checkForNewAlerts();
    }, REFRESH_INTERVAL);
}

// Utility Functions
async function fetchAPI(endpoint) {
    try {
        const response = await fetch(API_BASE + endpoint, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            credentials: 'same-origin'
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error(`Error fetching ${endpoint}:`, error);
        return null;
    }
}

function updateStatCard(elementId, value) {
    const element = document.getElementById(elementId);
    if (element) {
        if (typeof value === 'number') {
            element.textContent = value.toLocaleString('vi-VN');
        } else {
            element.textContent = value;
        }
    }
}

function getActivityColor(type) {
    const colors = {
        'success': 'var(--success-green)',
        'warning': 'var(--warning-orange)',
        'info': 'var(--primary-blue)',
        'error': 'var(--danger-red)',
        'maintenance': 'var(--warning-orange)',
        'completion': 'var(--success-green)',
        'user': 'var(--primary-blue)',
        'alert': 'var(--danger-red)'
    };
    return colors[type] || 'var(--gray-600)';
}

function getActivityIcon(type) {
    const icons = {
        'success': 'fa-check',
        'warning': 'fa-exclamation',
        'info': 'fa-info',
        'error': 'fa-times',
        'maintenance': 'fa-tools',
        'completion': 'fa-check-circle',
        'user': 'fa-user',
        'alert': 'fa-exclamation-triangle'
    };
    return icons[type] || 'fa-info';
}

function getStatusClass(status) {
    const classes = {
        'online': 'status-online',
        'warning': 'status-warning',
        'offline': 'status-offline',
        'active': 'status-online',
        'inactive': 'status-offline'
    };
    return classes[status.toLowerCase()] || 'status-offline';
}

function getStatusBadgeClass(status) {
    const classes = {
        'online': 'success',
        'warning': 'warning',
        'offline': 'danger',
        'active': 'success',
        'inactive': 'secondary'
    };
    return classes[status.toLowerCase()] || 'secondary';
}

function getStatusText(status) {
    const texts = {
        'online': 'Online',
        'warning': 'Cảnh báo',
        'offline': 'Offline',
        'active': 'Hoạt động',
        'inactive': 'Không hoạt động'
    };
    return texts[status.toLowerCase()] || status;
}

function formatTime(timestamp) {
    if (!timestamp) return 'Vừa xong';

    const now = new Date();
    const time = new Date(timestamp);
    const diff = Math.floor((now - time) / 1000);

    if (diff < 60) return `${diff} giây trước`;
    if (diff < 3600) return `${Math.floor(diff / 60)} phút trước`;
    if (diff < 86400) return `${Math.floor(diff / 3600)} giờ trước`;
    return `${Math.floor(diff / 86400)} ngày trước`;
}

function showNotification(message, type = 'info', duration = 5000) {
    const container = document.getElementById('notificationContainer');
    const notification = document.createElement('div');
    notification.className = `alert alert-${type === 'error' ? 'danger' : type} alert-dismissible fade show`;
    notification.style.cssText = 'min-width: 300px; margin-bottom: 10px;';
    notification.innerHTML = `
           <div class="d-flex align-items-center">
               <i class="fas ${getNotificationIcon(type)} me-2"></i>
               <span>${message}</span>
           </div>
           <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
       `;

    container.appendChild(notification);

    setTimeout(() => {
        if (notification.parentNode) {
            notification.remove();
        }
    }, duration);
}

function getNotificationIcon(type) {
    const icons = {
        'success': 'fa-check-circle',
        'error': 'fa-exclamation-circle',
        'warning': 'fa-exclamation-triangle',
        'info': 'fa-info-circle'
    };
    return icons[type] || 'fa-info-circle';
}

function updateSystemStatusBadge() {
    const badge = document.querySelector('.status-badge');

    const isHealthy = true;

    if (isHealthy) {
        badge.querySelector('.status-indicator').className = 'status-indicator status-online';
        badge.querySelector('span:last-child').textContent = 'Hệ thống hoạt động bình thường';
    } else {
        badge.querySelector('.status-indicator').className = 'status-indicator status-warning';
        badge.querySelector('span:last-child').textContent = 'Hệ thống có cảnh báo';
    }
}

function updateOverallSystemStatus(statusData) {
    const badge = document.getElementById('systemOverallStatus');
    if (!statusData || Object.keys(statusData).length === 0) return;

    const statuses = Object.values(statusData).map(item => item.status);
    const hasOffline = statuses.includes('offline');
    const hasWarning = statuses.includes('warning');

    if (hasOffline) {
        badge.className = 'badge bg-danger';
        badge.textContent = 'Có sự cố';
    } else if (hasWarning) {
        badge.className = 'badge bg-warning';
        badge.textContent = 'Có cảnh báo';
    } else {
        badge.className = 'badge bg-success';
        badge.textContent = 'Hoạt động tốt';
    }
}

// Dashboard Actions
async function refreshDashboard() {
    showNotification('Đang làm mới dashboard...', 'info');

    try {
        await Promise.all([
            loadDashboardData(),
            loadRecentActivities(),
            loadSystemStatus()
        ]);

        showNotification('Dashboard đã được làm mới!', 'success');
    } catch (error) {
        console.error('Error refreshing dashboard:', error);
        showNotification('Có lỗi khi làm mới dashboard', 'error');
    }
}

async function refreshActivities() {
    const button = event.target.closest('button');
    const originalHTML = button.innerHTML;
    button.innerHTML = '<i class="fas fa-sync-alt fa-spin"></i>';
    button.disabled = true;

    try {
        await loadRecentActivities();
        showNotification('Hoạt động đã được cập nhật', 'success');
    } catch (error) {
        showNotification('Không thể cập nhật hoạt động', 'error');
    } finally {
        button.innerHTML = originalHTML;
        button.disabled = false;
    }
}

async function exportData() {
    try {
        showNotification('Đang xuất dữ liệu...', 'info');

        const data = await fetchAPI('/dashboard/xuat');
        if (data) {
            const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `dashboard-export-${new Date().toISOString().split('T')[0]}.json`;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            URL.revokeObjectURL(url);

            showNotification('Dữ liệu đã được xuất thành công', 'success');
        } else {
            throw new Error('No data received');
        }
    } catch (error) {
        console.error('Error exporting data:', error);
        showNotification('Có lỗi khi xuất dữ liệu', 'error');
    }
}

function toggleFullscreen() {
    if (!document.fullscreenElement) {
        document.documentElement.requestFullscreen().catch(err => {
            console.error('Error attempting to enable fullscreen:', err);
        });
    } else {
        document.exitFullscreen();
    }
}

function toggleSearch() {
    const searchModal = new bootstrap.Modal(document.getElementById('searchModal') || createSearchModal());
    searchModal.show();
}

function createSearchModal() {
    const modal = document.createElement('div');
    modal.className = 'modal fade';
    modal.id = 'searchModal';
    modal.innerHTML = `
           <div class="modal-dialog">
               <div class="modal-content">
                   <div class="modal-header">
                       <h5 class="modal-title">Tìm kiếm nhanh</h5>
                       <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                   </div>
                   <div class="modal-body">
                       <input type="text" class="form-control" placeholder="Nhập từ khóa tìm kiếm..." id="searchInput">
                       <div id="searchResults" class="mt-3"></div>
                   </div>
               </div>
           </div>
       `;
    document.body.appendChild(modal);
    return modal;
}

function closeAllModals() {
    document.querySelectorAll('.modal.show').forEach(modal => {
        const modalInstance = bootstrap.Modal.getInstance(modal);
        if (modalInstance) {
            modalInstance.hide();
        }
    });
}

async function checkForNewAlerts() {
    try {
        const newAlerts = await fetchAPI('/canh-bao-loi/moi');
        if (newAlerts && newAlerts.length > 0) {
            newAlerts.forEach(alert => {
                showNotification(`Cảnh báo mới: ${alert.tieuDe}`, 'warning', 10000);
            });

            const alertCount = document.querySelector('[data-stat="tongCanhBao"] .stat-number');
            if (alertCount) {
                const currentCount = parseInt(alertCount.textContent) || 0;
                alertCount.textContent = currentCount + newAlerts.length;
            }
        }
    } catch (error) {
        console.error('Error checking for new alerts:', error);
    }
}

// Performance monitoring
function monitorPerformance() {
    if ('performance' in window) {
        window.addEventListener('load', function() {
            const loadTime = performance.timing.loadEventEnd - performance.timing.navigationStart;
            console.log(`Dashboard loaded in ${loadTime}ms`);

            if (loadTime > 3000) {
                showNotification('Dashboard đang tải chậm. Vui lòng kiểm tra kết nối mạng.', 'warning');
            }
        });
    }
}

monitorPerformance();

// Service Worker registration
if ('serviceWorker' in navigator) {
    navigator.serviceWorker.register('/sw.js').then(function(registration) {
        console.log('ServiceWorker registration successful');
    }).catch(function(err) {
        console.log('ServiceWorker registration failed: ', err);
    });
}

// Print functionality
function printDashboard() {
    window.print();
}

// Add print shortcut
document.addEventListener('keydown', function(e) {
    if ((e.ctrlKey || e.metaKey) && e.key === 'p') {
        e.preventDefault();
        printDashboard();
    }
});

// Dark mode toggle (optional)
function toggleDarkMode() {
    document.body.classList.toggle('dark-mode');
    localStorage.setItem('darkMode', document.body.classList.contains('dark-mode'));
}

// Load dark mode preference
if (localStorage.getItem('darkMode') === 'true') {
    document.body.classList.add('dark-mode');
}

// Error boundary
window.addEventListener('error', function(e) {
    console.error('Dashboard error:', e.error);
    showNotification('Đã xảy ra lỗi không mong muốn. Trang sẽ được làm mới.', 'error');
    setTimeout(() => {
        location.reload();
    }, 3000);
});

// Unhandled promise rejection
window.addEventListener('unhandledrejection', function(e) {
    console.error('Unhandled promise rejection:', e.reason);
    showNotification('Có lỗi xảy ra khi tải dữ liệu.', 'error');
});