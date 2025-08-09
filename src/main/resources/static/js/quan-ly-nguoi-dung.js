// quan-ly-nguoi-dung.js

class UserManagement {
    constructor() {
        this.users = [];
        this.roles = [];
        this.teams = [];
        this.currentPage = 1;
        this.pageSize = 10;
        this.totalUsers = 0;
        this.selectedUsers = new Set();
        this.sortField = '';
        this.sortDirection = 'asc';
        this.filters = {
            search: '',
            role: '',
            status: ''
        };

        this.init();
    }

    async init() {
        await this.loadInitialData();
        this.setupEventListeners();
        this.loadUsers();
    }

    async loadInitialData() {
        try {
            // Load roles
            const rolesResponse = await fetch('/api/vai-tro');
            if (rolesResponse.ok) {
                const rolesData = await rolesResponse.json();
                this.roles = rolesData.content || rolesData;
            }

            // Load teams
            const teamsResponse = await fetch('/api/doi-bao-tri');
            if (teamsResponse.ok) {
                const teamsData = await teamsResponse.json();
                this.teams = teamsData.content || teamsData;
            }

            this.populateRoleSelect();
            this.populateTeamSelect();
            this.updateStats();
        } catch (error) {
            console.error('Error loading initial data:', error);
        }
    }

    setupEventListeners() {
        // Search input
        const searchInput = document.getElementById('searchInput');
        searchInput.addEventListener('input', this.debounce(() => {
            this.filters.search = searchInput.value;
            this.currentPage = 1;
            this.loadUsers();
        }, 300));

        // Filter selects
        document.getElementById('roleFilter').addEventListener('change', (e) => {
            this.filters.role = e.target.value;
            this.currentPage = 1;
            this.loadUsers();
        });

        document.getElementById('statusFilter').addEventListener('change', (e) => {
            this.filters.status = e.target.value;
            this.currentPage = 1;
            this.loadUsers();
        });

        // Select all checkbox
        document.getElementById('selectAll').addEventListener('change', (e) => {
            this.toggleSelectAll(e.target.checked);
        });

        // User form
        document.getElementById('userForm').addEventListener('submit', (e) => {
            e.preventDefault();
            this.saveUser();
        });
    }

    debounce(func, wait) {
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

    async loadUsers() {
        try {
            this.showLoading();

            const params = new URLSearchParams({
                page: this.currentPage - 1,
                size: this.pageSize,
                sort: this.sortField ? `${this.sortField},${this.sortDirection}` : 'ngayCapNhat,desc'
            });

            // Add search filter
            if (this.filters.search) {
                params.append('search', this.filters.search);
            }

            const response = await fetch(`/api/nguoi-dung?${params}`);
            if (response.ok) {
                const data = await response.json();
                this.users = data.content || [];
                this.totalUsers = data.totalElements || 0;
                this.renderUsersTable();
                this.renderPagination(data.totalPages || 1);
            } else {
                throw new Error('Failed to load users');
            }
        } catch (error) {
            console.error('Error loading users:', error);
            this.showError('Không thể tải danh sách người dùng');
        }
    }

    showLoading() {
        const tbody = document.getElementById('usersTableBody');
        tbody.innerHTML = `
            <tr class="loading-row">
                <td colspan="10" class="loading-cell">
                    <div class="loading-spinner">
                        <i class="bi bi-arrow-clockwise spin"></i>
                        Đang tải dữ liệu...
                    </div>
                </td>
            </tr>
        `;
    }

    renderUsersTable() {
        const tbody = document.getElementById('usersTableBody');

        if (this.users.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="10" class="empty-state">
                        <i class="bi bi-inbox"></i>
                        <h3>Không có dữ liệu</h3>
                        <p>Không tìm thấy người dùng nào phù hợp với bộ lọc.</p>
                    </td>
                </tr>
            `;
            return;
        }

        tbody.innerHTML = this.users.map(user => `
            <tr ${this.selectedUsers.has(user.idNguoiDung) ? 'class="selected"' : ''}>
                <td>
                    <input type="checkbox" 
                           value="${user.idNguoiDung}" 
                           ${this.selectedUsers.has(user.idNguoiDung) ? 'checked' : ''}
                           onchange="userManager.toggleUserSelection(${user.idNguoiDung}, this.checked)">
                </td>
                <td>
                    <div class="user-info">
                        <div class="user-avatar">
                            ${this.getInitials(user.hoVaTen)}
                        </div>
                        <div class="user-details">
                            <div class="user-name">${user.hoVaTen}</div>
                            <div class="user-username">@${user.tenDangNhap}</div>
                        </div>
                    </div>
                </td>
                <td>${user.hoVaTen}</td>
                <td>${user.email || '-'}</td>
                <td>${user.soDienThoai || '-'}</td>
                <td>${this.renderRoles(user.vaiTroSet)}</td>
                <td>${user.tenDoiBaoTri || '-'}</td>
                <td>${this.formatDate(user.lanDangNhapCuoi)}</td>
                <td>${this.renderStatus(user)}</td>
                <td>
                    <div class="action-buttons">
                        <button class="btn-action btn-view" title="Xem chi tiết" 
                                onclick="userManager.viewUser(${user.idNguoiDung})">
                            <i class="bi bi-eye"></i>
                        </button>
                        <button class="btn-action btn-edit" title="Chỉnh sửa" 
                                onclick="userManager.editUser(${user.idNguoiDung})">
                            <i class="bi bi-pencil"></i>
                        </button>
                        <button class="btn-action ${user.taiKhoanKhongBiKhoa ? 'btn-lock' : 'btn-view'}" 
                                title="${user.taiKhoanKhongBiKhoa ? 'Khóa tài khoản' : 'Mở khóa tài khoản'}"
                                onclick="userManager.toggleUserLock(${user.idNguoiDung}, ${user.taiKhoanKhongBiKhoa})">
                            <i class="bi bi-${user.taiKhoanKhongBiKhoa ? 'lock' : 'unlock'}"></i>
                        </button>
                        <button class="btn-action btn-delete" title="Xóa" 
                                onclick="userManager.deleteUser(${user.idNguoiDung}, '${user.hoVaTen}')">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `).join('');
    }

    getInitials(fullName) {
        if (!fullName) return 'ND';
        const names = fullName.trim().split(' ');
        if (names.length === 1) return names[0].charAt(0).toUpperCase();
        return (names[0].charAt(0) + names[names.length - 1].charAt(0)).toUpperCase();
    }

    renderRoles(roles) {
        if (!roles || roles.length === 0) return '-';
        return roles.map(role => {
            const badgeClass = this.getRoleBadgeClass(role.tenVaiTro);
            return `<span class="role-badge ${badgeClass}">${role.tenVaiTro}</span>`;
        }).join(' ');
    }

    getRoleBadgeClass(roleName) {
        const roleMap = {
            'QUAN_TRI_VIEN': 'admin',
            'TRUONG_PHONG_CSVC': 'manager',
            'KY_THUAT_VIEN': 'technician'
        };
        return roleMap[roleName] || 'user';
    }

    renderStatus(user) {
        if (!user.trangThaiHoatDong) {
            return '<span class="status-badge inactive"><i class="bi bi-x-circle"></i> Ngừng hoạt động</span>';
        }
        if (!user.taiKhoanKhongBiKhoa) {
            return '<span class="status-badge locked"><i class="bi bi-lock"></i> Bị khóa</span>';
        }
        return '<span class="status-badge active"><i class="bi bi-check-circle"></i> Hoạt động</span>';
    }

    formatDate(dateString) {
        if (!dateString) return '-';
        const date = new Date(dateString);
        return date.toLocaleDateString('vi-VN') + ' ' + date.toLocaleTimeString('vi-VN', {
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    renderPagination(totalPages) {
        const paginationNumbers = document.getElementById('pageNumbers');
        const startItem = (this.currentPage - 1) * this.pageSize + 1;
        const endItem = Math.min(this.currentPage * this.pageSize, this.totalUsers);

        // Update pagination info
        document.getElementById('startItem').textContent = startItem;
        document.getElementById('endItem').textContent = endItem;
        document.getElementById('totalItems').textContent = this.totalUsers;

        // Update prev/next buttons
        document.getElementById('prevBtn').disabled = this.currentPage === 1;
        document.getElementById('nextBtn').disabled = this.currentPage === totalPages;

        // Generate page numbers
        let pageNumbers = '';
        const maxVisiblePages = 5;
        let startPage = Math.max(1, this.currentPage - Math.floor(maxVisiblePages / 2));
        let endPage = Math.min(totalPages, startPage + maxVisiblePages - 1);

        if (endPage - startPage < maxVisiblePages - 1) {
            startPage = Math.max(1, endPage - maxVisiblePages + 1);
        }

        for (let i = startPage; i <= endPage; i++) {
            pageNumbers += `
                <button class="page-number ${i === this.currentPage ? 'active' : ''}" 
                        onclick="userManager.goToPage(${i})">
                    ${i}
                </button>
            `;
        }

        paginationNumbers.innerHTML = pageNumbers;
    }

    populateRoleSelect() {
        const roleSelect = document.getElementById('vaiTro');
        roleSelect.innerHTML = '<option value="">Chọn vai trò</option>' +
            this.roles.map(role =>
                `<option value="${role.idVaiTro}">${role.tenVaiTro}</option>`
            ).join('');
    }

    populateTeamSelect() {
        const teamSelect = document.getElementById('doiBaoTri');
        teamSelect.innerHTML = '<option value="">Chọn đội bảo trì</option>' +
            this.teams.map(team =>
                `<option value="${team.idDoiBaoTri}">${team.tenDoi}</option>`
            ).join('');
    }

    async updateStats() {
        try {
            // Update stats from current data or make separate API calls
            // For now, we'll use placeholder data
            document.getElementById('totalUsers').textContent = this.totalUsers;

            // You can implement separate API calls for more accurate stats
            // const statsResponse = await fetch('/api/nguoi-dung/thong-ke');
        } catch (error) {
            console.error('Error updating stats:', error);
        }
    }

    // Event handlers
    goToPage(page) {
        this.currentPage = page;
        this.loadUsers();
    }

    changePage(direction) {
        const newPage = this.currentPage + direction;
        if (newPage >= 1) {
            this.goToPage(newPage);
        }
    }

    sortTable(field) {
        if (this.sortField === field) {
            this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
        } else {
            this.sortField = field;
            this.sortDirection = 'asc';
        }

        // Update sort icons
        document.querySelectorAll('.sort-icon').forEach(icon => {
            icon.parentElement.classList.remove('sort-asc', 'sort-desc');
        });

        const currentHeader = document.querySelector(`th[onclick="sortTable('${field}')"]`);
        currentHeader.classList.add(`sort-${this.sortDirection}`);

        this.loadUsers();
    }

    toggleSelectAll(checked) {
        this.selectedUsers.clear();
        if (checked) {
            this.users.forEach(user => this.selectedUsers.add(user.idNguoiDung));
        }

        document.querySelectorAll('tbody input[type="checkbox"]').forEach(checkbox => {
            checkbox.checked = checked;
        });

        this.renderUsersTable();
    }

    toggleUserSelection(userId, checked) {
        if (checked) {
            this.selectedUsers.add(userId);
        } else {
            this.selectedUsers.delete(userId);
        }

        document.getElementById('selectAll').checked =
            this.selectedUsers.size === this.users.length && this.users.length > 0;
    }

    resetFilters() {
        this.filters = { search: '', role: '', status: '' };
        document.getElementById('searchInput').value = '';
        document.getElementById('roleFilter').value = '';
        document.getElementById('statusFilter').value = '';
        this.currentPage = 1;
        this.loadUsers();
    }

    refreshTable() {
        this.loadUsers();
    }

    // Modal operations
    openAddUserModal() {
        document.getElementById('modalTitle').textContent = 'Thêm người dùng mới';
        document.getElementById('userId').value = '';
        document.getElementById('userForm').reset();
        document.getElementById('passwordSection').style.display = 'block';
        document.getElementById('matKhau').required = true;
        document.getElementById('xacNhanMatKhau').required = true;
        document.getElementById('userModal').classList.add('show');
    }

    closeUserModal() {
        document.getElementById('userModal').classList.remove('show');
    }

    async viewUser(userId) {
        try {
            const response = await fetch(`/api/nguoi-dung/${userId}`);
            if (response.ok) {
                const user = await response.json();
                this.showUserDetails(user);
            }
        } catch (error) {
            console.error('Error loading user details:', error);
            this.showError('Không thể tải thông tin người dùng');
        }
    }

    showUserDetails(user) {
        // Populate user details modal
        document.getElementById('detailTenDangNhap').textContent = user.tenDangNhap;
        document.getElementById('detailHoVaTen').textContent = user.hoVaTen;
        document.getElementById('detailEmail').textContent = user.email || '-';
        document.getElementById('detailSoDienThoai').textContent = user.soDienThoai || '-';
        document.getElementById('detailDiaChi').textContent = user.diaChi || '-';
        document.getElementById('detailNgayTao').textContent = this.formatDate(user.ngayTao);
        document.getElementById('detailLanDangNhapCuoi').textContent = this.formatDate(user.lanDangNhapCuoi);
        document.getElementById('detailSoLanDangNhapThatBai').textContent = user.soLanDangNhapThatBai || '0';

        // Show roles
        const rolesList = document.getElementById('userRolesList');
        if (user.vaiTroSet && user.vaiTroSet.length > 0) {
            rolesList.innerHTML = user.vaiTroSet.map(role => `
                <div class="role-item">
                    <span class="role-badge ${this.getRoleBadgeClass(role.tenVaiTro)}">${role.tenVaiTro}</span>
                    <p class="role-description">${role.moTa || 'Không có mô tả'}</p>
                </div>
            `).join('');
        } else {
            rolesList.innerHTML = '<p class="text-muted">Chưa được phân quyền</p>';
        }

        // Show team info
        const teamInfo = document.getElementById('userTeamInfo');
        if (user.tenDoiBaoTri) {
            teamInfo.innerHTML = `
                <div class="team-item">
                    <h5>${user.tenDoiBaoTri}</h5>
                    <p class="text-muted">Thành viên đội bảo trì</p>
                </div>
            `;
        } else {
            teamInfo.innerHTML = '<p class="text-muted">Chưa được phân công đội</p>';
        }

        document.getElementById('userDetailsModal').classList.add('show');
    }

    closeUserDetailsModal() {
        document.getElementById('userDetailsModal').classList.remove('show');
    }

    editUserFromDetails() {
        this.closeUserDetailsModal();
        const userId = document.getElementById('detailTenDangNhap').textContent;
        const user = this.users.find(u => u.tenDangNhap === userId);
        if (user) {
            this.editUser(user.idNguoiDung);
        }
    }

    async editUser(userId) {
        try {
            const response = await fetch(`/api/nguoi-dung/${userId}`);
            if (response.ok) {
                const user = await response.json();
                this.populateEditForm(user);
                document.getElementById('modalTitle').textContent = 'Chỉnh sửa người dùng';
                document.getElementById('passwordSection').style.display = 'none';
                document.getElementById('matKhau').required = false;
                document.getElementById('xacNhanMatKhau').required = false;
                document.getElementById('userModal').classList.add('show');
            }
        } catch (error) {
            console.error('Error loading user for edit:', error);
            this.showError('Không thể tải thông tin người dùng');
        }
    }

    populateEditForm(user) {
        document.getElementById('userId').value = user.idNguoiDung;
        document.getElementById('tenDangNhap').value = user.tenDangNhap;
        document.getElementById('hoVaTen').value = user.hoVaTen;
        document.getElementById('email').value = user.email || '';
        document.getElementById('soDienThoai').value = user.soDienThoai || '';
        document.getElementById('diaChi').value = user.diaChi || '';
        document.getElementById('trangThaiHoatDong').checked = user.trangThaiHoatDong;

        // Set role if exists
        if (user.vaiTroSet && user.vaiTroSet.length > 0) {
            document.getElementById('vaiTro').value = user.vaiTroSet[0].idVaiTro;
        }

        // Set team if exists
        if (user.idDoiBaoTri) {
            document.getElementById('doiBaoTri').value = user.idDoiBaoTri;
        }
    }

    async saveUser() {
        try {
            const formData = new FormData(document.getElementById('userForm'));
            const userData = Object.fromEntries(formData);

            // Validate passwords if creating new user
            const isEdit = userData.idNguoiDung;
            if (!isEdit) {
                if (userData.matKhau !== userData.xacNhanMatKhau) {
                    this.showError('Mật khẩu và xác nhận mật khẩu không khớp');
                    return;
                }
                if (userData.matKhau.length < 6) {
                    this.showError('Mật khẩu phải có ít nhất 6 ký tự');
                    return;
                }
            }

            // Convert checkbox value
            userData.trangThaiHoatDong = userData.trangThaiHoatDong === 'on';

            // Remove confirm password field
            delete userData.xacNhanMatKhau;

            const url = isEdit ? `/api/nguoi-dung/${userData.idNguoiDung}` : '/api/nguoi-dung';
            const method = isEdit ? 'PUT' : 'POST';

            const response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(userData)
            });

            if (response.ok) {
                this.showSuccess(isEdit ? 'Cập nhật người dùng thành công' : 'Thêm người dùng thành công');
                this.closeUserModal();
                this.loadUsers();
                this.updateStats();
            } else {
                const error = await response.text();
                this.showError(error);
            }
        } catch (error) {
            console.error('Error saving user:', error);
            this.showError('Có lỗi xảy ra khi lưu thông tin người dùng');
        }
    }

    deleteUser(userId, userName) {
        document.getElementById('deleteUserInfo').innerHTML =
            `Bạn có chắc chắn muốn xóa người dùng <strong>${userName}</strong>?<br>
             Thao tác này không thể hoàn tác.`;

        document.getElementById('deleteModal').setAttribute('data-user-id', userId);
        document.getElementById('deleteModal').classList.add('show');

        // Check for dependencies
        this.checkUserDependencies(userId);
    }

    async checkUserDependencies(userId) {
        try {
            // Check if user has any related data
            const warnings = [];

            // You can implement API calls to check dependencies
            // For now, we'll show general warnings
            const user = this.users.find(u => u.idNguoiDung === userId);
            if (user) {
                if (user.vaiTroSet && user.vaiTroSet.length > 0) {
                    warnings.push('Người dùng này có vai trò được phân quyền');
                }
                if (user.tenDoiBaoTri) {
                    warnings.push('Người dùng này thuộc đội bảo trì');
                }
            }

            const warningsDiv = document.getElementById('deleteWarnings');
            if (warnings.length > 0) {
                warningsDiv.innerHTML = `
                    <h5><i class="bi bi-exclamation-triangle"></i> Cảnh báo:</h5>
                    <ul>
                        ${warnings.map(w => `<li>${w}</li>`).join('')}
                    </ul>
                `;
                warningsDiv.style.display = 'block';
            } else {
                warningsDiv.style.display = 'none';
            }
        } catch (error) {
            console.error('Error checking dependencies:', error);
        }
    }

    closeDeleteModal() {
        document.getElementById('deleteModal').classList.remove('show');
    }

    async confirmDelete() {
        try {
            const userId = document.getElementById('deleteModal').getAttribute('data-user-id');
            const response = await fetch(`/api/nguoi-dung/${userId}`, {
                method: 'DELETE'
            });

            if (response.ok) {
                this.showSuccess('Xóa người dùng thành công');
                this.closeDeleteModal();
                this.loadUsers();
                this.updateStats();
            } else {
                const error = await response.text();
                this.showError(error);
            }
        } catch (error) {
            console.error('Error deleting user:', error);
            this.showError('Có lỗi xảy ra khi xóa người dùng');
        }
    }

    async toggleUserLock(userId, isCurrentlyUnlocked) {
        try {
            const action = isCurrentlyUnlocked ? 'khoa-tai-khoan' : 'mo-khoa-tai-khoan';
            const response = await fetch(`/api/nguoi-dung/${userId}/${action}`, {
                method: 'POST'
            });

            if (response.ok) {
                const message = isCurrentlyUnlocked ? 'Khóa tài khoản thành công' : 'Mở khóa tài khoản thành công';
                this.showSuccess(message);
                this.loadUsers();
            } else {
                const error = await response.text();
                this.showError(error);
            }
        } catch (error) {
            console.error('Error toggling user lock:', error);
            this.showError('Có lỗi xảy ra khi thay đổi trạng thái tài khoản');
        }
    }

    // Bulk operations
    closeBulkActionsModal() {
        document.getElementById('bulkActionsModal').classList.remove('show');
    }

    async bulkActivate() {
        if (this.selectedUsers.size === 0) return;

        try {
            const promises = Array.from(this.selectedUsers).map(userId =>
                fetch(`/api/nguoi-dung/${userId}/trang-thai?trangThaiHoatDong=true`, {
                    method: 'PUT'
                })
            );

            await Promise.all(promises);
            this.showSuccess(`Kích hoạt ${this.selectedUsers.size} tài khoản thành công`);
            this.selectedUsers.clear();
            this.closeBulkActionsModal();
            this.loadUsers();
        } catch (error) {
            console.error('Error bulk activating users:', error);
            this.showError('Có lỗi xảy ra khi kích hoạt tài khoản');
        }
    }

    async bulkDeactivate() {
        if (this.selectedUsers.size === 0) return;

        try {
            const promises = Array.from(this.selectedUsers).map(userId =>
                fetch(`/api/nguoi-dung/${userId}/trang-thai?trangThaiHoatDong=false`, {
                    method: 'PUT'
                })
            );

            await Promise.all(promises);
            this.showSuccess(`Vô hiệu hóa ${this.selectedUsers.size} tài khoản thành công`);
            this.selectedUsers.clear();
            this.closeBulkActionsModal();
            this.loadUsers();
        } catch (error) {
            console.error('Error bulk deactivating users:', error);
            this.showError('Có lỗi xảy ra khi vô hiệu hóa tài khoản');
        }
    }

    async bulkDelete() {
        if (this.selectedUsers.size === 0) return;

        if (!confirm(`Bạn có chắc chắn muốn xóa ${this.selectedUsers.size} người dùng đã chọn?`)) {
            return;
        }

        try {
            const promises = Array.from(this.selectedUsers).map(userId =>
                fetch(`/api/nguoi-dung/${userId}`, { method: 'DELETE' })
            );

            await Promise.all(promises);
            this.showSuccess(`Xóa ${this.selectedUsers.size} người dùng thành công`);
            this.selectedUsers.clear();
            this.closeBulkActionsModal();
            this.loadUsers();
            this.updateStats();
        } catch (error) {
            console.error('Error bulk deleting users:', error);
            this.showError('Có lỗi xảy ra khi xóa người dùng');
        }
    }

    async exportUsers() {
        try {
            const response = await fetch('/api/nguoi-dung/export', {
                method: 'GET'
            });

            if (response.ok) {
                const blob = await response.blob();
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = `danh-sach-nguoi-dung-${new Date().toISOString().split('T')[0]}.xlsx`;
                document.body.appendChild(a);
                a.click();
                window.URL.revokeObjectURL(url);
                document.body.removeChild(a);

                this.showSuccess('Xuất file Excel thành công');
            } else {
                this.showError('Không thể xuất file Excel');
            }
        } catch (error) {
            console.error('Error exporting users:', error);
            this.showError('Có lỗi xảy ra khi xuất file');
        }
    }

    // Utility methods
    showSuccess(message) {
        this.showNotification(message, 'success');
    }

    showError(message) {
        this.showNotification(message, 'error');
    }

    showNotification(message, type) {
        // Create notification element
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        notification.innerHTML = `
            <div class="notification-content">
                <i class="bi bi-${type === 'success' ? 'check-circle' : 'exclamation-triangle'}"></i>
                <span>${message}</span>
                <button class="notification-close" onclick="this.parentElement.parentElement.remove()">
                    <i class="bi bi-x"></i>
                </button>
            </div>
        `;

        // Add to page
        document.body.appendChild(notification);

        // Auto remove after 5 seconds
        setTimeout(() => {
            if (notification.parentElement) {
                notification.remove();
            }
        }, 5000);
    }
}

// Global functions for HTML onclick events
function openAddUserModal() {
    userManager.openAddUserModal();
}

function closeUserModal() {
    userManager.closeUserModal();
}

function closeDeleteModal() {
    userManager.closeDeleteModal();
}

function confirmDelete() {
    userManager.confirmDelete();
}

function closeUserDetailsModal() {
    userManager.closeUserDetailsModal();
}

function editUserFromDetails() {
    userManager.editUserFromDetails();
}

function closeBulkActionsModal() {
    userManager.closeBulkActionsModal();
}

function bulkActivate() {
    userManager.bulkActivate();
}

function bulkDeactivate() {
    userManager.bulkDeactivate();
}

function bulkAssignRole() {
    // Implementation for bulk role assignment
    alert('Chức năng đang phát triển');
}

function bulkDelete() {
    userManager.bulkDelete();
}

function resetFilters() {
    userManager.resetFilters();
}

function refreshTable() {
    userManager.refreshTable();
}

function exportUsers() {
    userManager.exportUsers();
}

function sortTable(field) {
    userManager.sortTable(field);
}

function changePage(direction) {
    userManager.changePage(direction);
}

function toggleSelectAll() {
    const checkbox = document.getElementById('selectAll');
    userManager.toggleSelectAll(checkbox.checked);
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    window.userManager = new UserManagement();
});

// Add notification styles
const notificationStyles = `
<style>
.notification {
    position: fixed;
    top: 20px;
    right: 20px;
    z-index: 10000;
    min-width: 300px;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    animation: slideInRight 0.3s ease;
}

.notification-success {
    background: #10b981;
    color: white;
}

.notification-error {
    background: #ef4444;
    color: white;
}

.notification-content {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 16px;
}

.notification-close {
    background: none;
    border: none;
    color: inherit;
    cursor: pointer;
    margin-left: auto;
    opacity: 0.8;
}

.notification-close:hover {
    opacity: 1;
}

@keyframes slideInRight {
    from {
        transform: translateX(100%);
        opacity: 0;
    }
    to {
        transform: translateX(0);
        opacity: 1;
    }
}
</style>
`;

document.head.insertAdjacentHTML('beforeend', notificationStyles);