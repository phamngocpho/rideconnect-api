// users-management.js
class UserManagement {
    constructor() {
        this.currentPage = 0;
        this.pageSize = 10;
        this.totalPages = 0;
        this.totalElements = 0;
        this.currentSort = 'createdAt,desc';
        this.currentFilter = '';
        this.currentStatus = '';
        this.editingUserId = null;
        this.userToDelete = null;

        this.init();
    }

    init() {
        this.bindEvents();
        this.loadUsers();
    }

    bindEvents() {
        // Search functionality
        const searchInput = document.getElementById('searchInput');
        let searchTimeout;
        searchInput.addEventListener('input', (e) => {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(() => {
                this.currentFilter = e.target.value;
                this.currentPage = 0;
                this.loadUsers();
            }, 500);
        });

        // Status filter
        document.getElementById('statusFilter').addEventListener('change', (e) => {
            this.currentStatus = e.target.value;
            this.currentPage = 0;
            this.loadUsers();
        });

        // Sort options
        document.getElementById('sortSelect').addEventListener('change', (e) => {
            this.currentSort = e.target.value;
            this.currentPage = 0;
            this.loadUsers();
        });

        // Add user button
        document.getElementById('addUserBtn').addEventListener('click', () => {
            this.openUserModal();
        });

        // Modal events
        document.getElementById('cancelBtn').addEventListener('click', () => {
            this.closeUserModal();
        });

        document.getElementById('userForm').addEventListener('submit', (e) => {
            e.preventDefault();
            this.saveUser();
        });

        // Delete modal events
        document.getElementById('cancelDeleteBtn').addEventListener('click', () => {
            this.closeDeleteModal();
        });

        document.getElementById('confirmDeleteBtn').addEventListener('click', () => {
            this.deleteUser();
        });

        // Toast close
        document.getElementById('closeToast').addEventListener('click', () => {
            this.hideToast();
        });

        // Mobile pagination
        document.getElementById('prevPageMobile').addEventListener('click', () => {
            if (this.currentPage > 0) {
                this.currentPage--;
                this.loadUsers();
            }
        });

        document.getElementById('nextPageMobile').addEventListener('click', () => {
            if (this.currentPage < this.totalPages - 1) {
                this.currentPage++;
                this.loadUsers();
            }
        });

        // Close modals when clicking outside
        document.getElementById('userModal').addEventListener('click', (e) => {
            if (e.target.id === 'userModal') {
                this.closeUserModal();
            }
        });

        document.getElementById('deleteModal').addEventListener('click', (e) => {
            if (e.target.id === 'deleteModal') {
                this.closeDeleteModal();
            }
        });
    }

    async loadUsers() {
        try {
            this.showLoading();

            const params = new URLSearchParams({
                page: this.currentPage,
                size: this.pageSize
            });

            // Add sort parameters
            if (this.currentSort) {
                const [sortBy, sortDir] = this.currentSort.split(',');
                params.append('sortBy', sortBy);
                params.append('sortDir', sortDir);
            }

            const response = await fetch(`/api/admin/users?${params}`);

            if (!response.ok) {
                throw new Error('Failed to load users');
            }

            const data = await response.json();
            this.renderUsers(data);
            this.updatePagination(data);

        } catch (error) {
            console.error('Error loading users:', error);
            this.showToast('Lỗi khi tải danh sách người dùng', 'error');
            this.showEmptyState();
        }
    }

    renderUsers(data) {
        const tbody = document.getElementById('usersTableBody');
        const { content } = data;

        if (!content || content.length === 0) {
            this.showEmptyState();
            return;
        }

        this.hideLoading();
        this.hideEmptyState();

        // Filter users based on search and status
        let filteredUsers = content;

        if (this.currentFilter) {
            const filter = this.currentFilter.toLowerCase();
            filteredUsers = filteredUsers.filter(user =>
                user.email.toLowerCase().includes(filter) ||
                user.phoneNumber.includes(filter) ||
                user.fullName.toLowerCase().includes(filter)
            );
        }

        if (this.currentStatus) {
            filteredUsers = filteredUsers.filter(user => user.status === this.currentStatus);
        }

        tbody.innerHTML = filteredUsers.map(user => this.createUserRow(user)).join('');

        // Update total count
        document.getElementById('totalUsers').textContent = `Tổng: ${filteredUsers.length} người dùng`;
    }

    createUserRow(user) {
        const statusBadge = this.getStatusBadge(user.status);
        const roleBadge = this.getRoleBadge(user.role);
        const formattedDate = this.formatDate(user.createdAt);
        const avatar = user.avatarUrl || this.getDefaultAvatar(user.fullName);

        return `
            <tr class="hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors">
                <td class="px-6 py-4 whitespace-nowrap">
                    <div class="flex items-center">
                        <div class="flex-shrink-0 h-10 w-10">
                            <img class="h-10 w-10 rounded-full object-cover" 
                                 src="${avatar}" 
                                 alt="${user.fullName}"
                                 onerror="this.src='${this.getDefaultAvatar(user.fullName)}'">
                        </div>
                        <div class="ml-4">
                            <div class="text-sm font-medium text-gray-900 dark:text-white">
                                ${user.fullName}
                            </div>
                            <div class="text-sm text-gray-500 dark:text-gray-400">
                                ${roleBadge}
                            </div>
                        </div>
                    </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                    <div class="text-sm text-gray-900 dark:text-white">${user.email}</div>
                    <div class="text-sm text-gray-500 dark:text-gray-400">${user.phoneNumber}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                    ${statusBadge}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-400">
                    ${formattedDate}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                    <div class="flex items-center justify-end space-x-2">
                        <button onclick="userManagement.viewUser('${user.id}')" 
                                class="text-blue-600 hover:text-blue-900 dark:text-blue-400 dark:hover:text-blue-300 transition-colors"
                                title="Xem chi tiết">
                            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"></path>
                            </svg>
                        </button>
                        <button onclick="userManagement.editUser('${user.id}')" 
                                class="text-indigo-600 hover:text-indigo-900 dark:text-indigo-400 dark:hover:text-indigo-300 transition-colors"
                                title="Chỉnh sửa">
                            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
                            </svg>
                        </button>
                        <button onclick="userManagement.confirmDelete('${user.id}', '${user.fullName}')" 
                                class="text-red-600 hover:text-red-900 dark:text-red-400 dark:hover:text-red-300 transition-colors"
                                title="Xóa">
                            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
                            </svg>
                        </button>
                    </div>
                </td>
            </tr>
        `;
    }

    getStatusBadge(status) {
        const statusConfig = {
            'ACTIVE': { text: 'Hoạt động', class: 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-300' },
            'INACTIVE': { text: 'Không hoạt động', class: 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-300' },
            'SUSPENDED': { text: 'Tạm khóa', class: 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-300' }
        };

        const config = statusConfig[status] || statusConfig['INACTIVE'];
        return `<span class="inline-flex px-2 py-1 text-xs font-semibold rounded-full ${config.class}">${config.text}</span>`;
    }

    getRoleBadge(role) {
        const roleConfig = {
            'ROLE_ADMIN': { text: 'Quản trị viên', class: 'bg-purple-100 text-purple-800 dark:bg-purple-900 dark:text-purple-300' },
            'ROLE_DRIVER': { text: 'Tài xế', class: 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-300' },
            'ROLE_CUSTOMER': { text: 'Khách hàng', class: 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-300' }
        };

        const config = roleConfig[role] || roleConfig['ROLE_CUSTOMER'];
        return `<span class="inline-flex px-2 py-1 text-xs font-semibold rounded-full ${config.class}">${config.text}</span>`;
    }

    formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString('vi-VN', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    getDefaultAvatar(fullName) {
        const initials = fullName.split(' ').map(name => name.charAt(0)).join('').toUpperCase();
        return `https://ui-avatars.com/api/?name=${encodeURIComponent(initials)}&background=3B82F6&color=fff&size=40`;
    }

    updatePagination(data) {
        this.totalPages = data.totalPages;
        this.totalElements = data.totalElements;

        // Update record info
        const fromRecord = this.currentPage * this.pageSize + 1;
        const toRecord = Math.min((this.currentPage + 1) * this.pageSize, this.totalElements);

        document.getElementById('fromRecord').textContent = fromRecord;
        document.getElementById('toRecord').textContent = toRecord;
        document.getElementById('totalRecords').textContent = this.totalElements;

        // Update mobile pagination buttons
        const prevMobile = document.getElementById('prevPageMobile');
        const nextMobile = document.getElementById('nextPageMobile');

        prevMobile.disabled = this.currentPage === 0;
        nextMobile.disabled = this.currentPage >= this.totalPages - 1;

        // Generate pagination buttons
        this.generatePaginationButtons();
    }

    generatePaginationButtons() {
        const pagination = document.getElementById('pagination');
        pagination.innerHTML = '';

        // Previous button
        const prevBtn = this.createPaginationButton('‹', this.currentPage - 1, this.currentPage === 0);
        pagination.appendChild(prevBtn);

        // Page numbers
        const startPage = Math.max(0, this.currentPage - 2);
        const endPage = Math.min(this.totalPages - 1, this.currentPage + 2);

        for (let i = startPage; i <= endPage; i++) {
            const pageBtn = this.createPaginationButton(i + 1, i, false, i === this.currentPage);
            pagination.appendChild(pageBtn);
        }

        // Next button
        const nextBtn = this.createPaginationButton('›', this.currentPage + 1, this.currentPage >= this.totalPages - 1);
        pagination.appendChild(nextBtn);
    }

    createPaginationButton(text, page, disabled, active = false) {
        const button = document.createElement('button');
        button.textContent = text;
        button.disabled = disabled;

        let classes = 'relative inline-flex items-center px-4 py-2 border text-sm font-medium transition-colors ';

        if (active) {
            classes += 'z-10 bg-blue-50 border-blue-500 text-blue-600 dark:bg-blue-900 dark:border-blue-400 dark:text-blue-300';
        } else if (disabled) {
            classes += 'bg-gray-100 border-gray-300 text-gray-400 cursor-not-allowed dark:bg-gray-700 dark:border-gray-600 dark:text-gray-500';
        } else {
            classes += 'bg-white border-gray-300 text-gray-500 hover:bg-gray-50 dark:bg-gray-800 dark:border-gray-600 dark:text-gray-400 dark:hover:bg-gray-700';
        }

        button.className = classes;

        if (!disabled && !active) {
            button.addEventListener('click', () => {
                this.currentPage = page;
                this.loadUsers();
            });
        }

        return button;
    }

    openUserModal(user = null) {
        this.editingUserId = user ? user.id : null;
        const modal = document.getElementById('userModal');
        const title = document.getElementById('modalTitle');
        const form = document.getElementById('userForm');
        const passwordField = document.getElementById('passwordField');

        if (user) {
            title.textContent = 'Chỉnh sửa người dùng';
            document.getElementById('fullName').value = user.fullName;
            document.getElementById('email').value = user.email;
            document.getElementById('phoneNumber').value = user.phoneNumber;
            document.getElementById('status').value = user.status;
            passwordField.style.display = 'none';
            document.getElementById('password').required = false;
        } else {
            title.textContent = 'Thêm người dùng mới';
            form.reset();
            passwordField.style.display = 'block';
            document.getElementById('password').required = true;
        }

        modal.classList.remove('hidden');
        document.body.style.overflow = 'hidden';
    }

    closeUserModal() {
        const modal = document.getElementById('userModal');
        modal.classList.add('hidden');
        document.body.style.overflow = 'auto';
        this.editingUserId = null;
    }

    async saveUser() {
        try {
            const formData = {
                fullName: document.getElementById('fullName').value,
                email: document.getElementById('email').value,
                phoneNumber: document.getElementById('phoneNumber').value,
                status: document.getElementById('status').value
            };

            if (!this.editingUserId) {
                formData.password = document.getElementById('password').value;
                formData.role = 'ROLE_CUSTOMER'; // Default role
            }

            const url = this.editingUserId
                ? `/api/admin/users/${this.editingUserId}`
                : '/api/admin/users';

            const method = this.editingUserId ? 'PUT' : 'POST';

            const response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData)
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Failed to save user');
            }

            this.closeUserModal();
            this.loadUsers();
            this.showToast(
                this.editingUserId ? 'Cập nhật người dùng thành công' : 'Thêm người dùng thành công',
                'success'
            );

        } catch (error) {
            console.error('Error saving user:', error);
            this.showToast(error.message || 'Lỗi khi lưu thông tin người dùng', 'error');
        }
    }

    async viewUser(userId) {
        try {
            const response = await fetch(`/api/admin/users/${userId}`);
            if (!response.ok) {
                throw new Error('Failed to load user details');
            }

            const user = await response.json();
            this.openUserModal(user);

            // Make form read-only for viewing
            const form = document.getElementById('userForm');
            const inputs = form.querySelectorAll('input, select');
            inputs.forEach(input => input.disabled = true);

            // Hide save button, show only close
            const saveBtn = form.querySelector('button[type="submit"]');
            saveBtn.style.display = 'none';

            document.getElementById('modalTitle').textContent = 'Chi tiết người dùng';

        } catch (error) {
            console.error('Error loading user details:', error);
            this.showToast('Lỗi khi tải thông tin người dùng', 'error');
        }
    }

    async editUser(userId) {
        try {
            const response = await fetch(`/api/admin/users/${userId}`);
            if (!response.ok) {
                throw new Error('Failed to load user details');
            }

            const user = await response.json();
            this.openUserModal(user);

        } catch (error) {
            console.error('Error loading user details:', error);
            this.showToast('Lỗi khi tải thông tin người dùng', 'error');
        }
    }

    confirmDelete(userId, userName) {
        this.userToDelete = { id: userId, name: userName };
        const modal = document.getElementById('deleteModal');
        modal.classList.remove('hidden');
        document.body.style.overflow = 'hidden';
    }

    closeDeleteModal() {
        const modal = document.getElementById('deleteModal');
        modal.classList.add('hidden');
        document.body.style.overflow = 'auto';
        this.userToDelete = null;
    }

    async deleteUser() {
        if (!this.userToDelete) return;

        try {
            const response = await fetch(`/api/admin/users/${this.userToDelete.id}`, {
                method: 'DELETE'
            });

            if (!response.ok) {
                throw new Error('Failed to delete user');
            }

            this.closeDeleteModal();
            this.loadUsers();
            this.showToast('Xóa người dùng thành công', 'success');

        } catch (error) {
            console.error('Error deleting user:', error);
            this.showToast('Lỗi khi xóa người dùng', 'error');
        }
    }

    showToast(message, type = 'info') {
        const toast = document.getElementById('toast');
        const toastMessage = document.getElementById('toastMessage');
        const toastIcon = document.getElementById('toastIcon');

        toastMessage.textContent = message;

        // Set icon based on type
        const icons = {
            success: '<svg class="h-5 w-5 text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path></svg>',
            error: '<svg class="h-5 w-5 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>',
            info: '<svg class="h-5 w-5 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>'
        };

        toastIcon.innerHTML = icons[type] || icons.info;

        toast.classList.remove('hidden');

        // Auto hide after 5 seconds
        setTimeout(() => {
            this.hideToast();
        }, 5000);
    }

    hideToast() {
        const toast = document.getElementById('toast');
        toast.classList.add('hidden');
    }

    showLoading() {
        document.getElementById('loadingState').classList.remove('hidden');
        document.getElementById('tableContent').classList.add('hidden');
        document.getElementById('emptyState').classList.add('hidden');
    }

    hideLoading() {
        document.getElementById('loadingState').classList.add('hidden');
        document.getElementById('tableContent').classList.remove('hidden');
    }

    showEmptyState() {
        document.getElementById('loadingState').classList.add('hidden');
        document.getElementById('tableContent').classList.add('hidden');
        document.getElementById('emptyState').classList.remove('hidden');
    }

    hideEmptyState() {
        document.getElementById('emptyState').classList.add('hidden');
    }
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    window.userManagement = new UserManagement();
});

// Keyboard shortcuts
document.addEventListener('keydown', (e) => {
    // ESC to close modals
    if (e.key === 'Escape') {
        const userModal = document.getElementById('userModal');
        const deleteModal = document.getElementById('deleteModal');

        if (!userModal.classList.contains('hidden')) {
            window.userManagement.closeUserModal();
        }
        if (!deleteModal.classList.contains('hidden')) {
            window.userManagement.closeDeleteModal();
        }
    }

    // Ctrl+N to add new user
    if (e.ctrlKey && e.key === 'n') {
        e.preventDefault();
        window.userManagement.openUserModal();
    }
});