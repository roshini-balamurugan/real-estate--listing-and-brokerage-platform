// Authentication & Global Header Manager

document.addEventListener('DOMContentLoaded', async () => {
    if (getToken()) {
        await refreshCurrentUser();
    }
    renderGlobalNavbar();
    if (getToken()) {
        loadHeaderNotifications();
    }
});

async function refreshCurrentUser() {
    if (getToken()) {
        try {
            const user = await apiFetch('/api/auth/me');
            if (user) {
                setCurrentUser(user);
                return user;
            }
        } catch (e) {
            console.warn('Could not refresh user info', e);
        }
    }
    return getCurrentUser();
}

function renderGlobalNavbar() {
    const navPlaceholder = document.getElementById('navbarPlaceholder');
    if (!navPlaceholder) return;

    const user = getCurrentUser();
    const token = getToken();

    let authLinksHtml = '';
    let dashboardLink = 'dashboard-buyer.html';

    if (user) {
        if (user.role === 'ROLE_ADMIN') {
            dashboardLink = 'dashboard-admin.html';
        } else if (user.role === 'ROLE_SELLER' || user.role === 'ROLE_AGENT') {
            dashboardLink = 'dashboard-seller.html';
        } else {
            dashboardLink = 'dashboard-buyer.html';
        }

        const roleBadge = user.role.replace('ROLE_', '');

        authLinksHtml = `
            <!-- Notifications Dropdown -->
            <li class="nav-item dropdown me-2">
                <a class="nav-link notification-bell" href="#" id="notifDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                    <i class="bi bi-bell-fill fs-5"></i>
                    <span class="notification-count d-none" id="navUnreadCount">0</span>
                </a>
                <ul class="dropdown-menu dropdown-menu-end dropdown-notifications shadow" aria-labelledby="notifDropdown" id="navNotifList">
                    <li class="dropdown-header d-flex justify-content-between align-items-center">
                        <span class="fw-bold">Notifications</span>
                        <a href="javascript:void(0)" class="text-primary small text-decoration-none" onclick="markAllNotificationsRead()">Mark all read</a>
                    </li>
                    <li><hr class="dropdown-divider my-1"></li>
                    <div id="navNotifItems">
                        <li class="p-3 text-center text-muted small">No notifications</li>
                    </div>
                </ul>
            </li>

            <!-- User Menu -->
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle d-flex align-items-center gap-2" href="#" id="userDropdown" role="button" data-bs-toggle="dropdown">
                    <div class="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center" style="width:32px; height:32px; font-weight:600;">
                        ${user.name ? user.name.charAt(0).toUpperCase() : 'U'}
                    </div>
                    <span>${user.name}</span>
                    <span class="badge bg-secondary" style="font-size:0.65rem;">${roleBadge}</span>
                </a>
                <ul class="dropdown-menu dropdown-menu-end shadow" aria-labelledby="userDropdown">
                    <li><h6 class="dropdown-header">${user.email}</h6></li>
                    <li><a class="dropdown-item" href="${dashboardLink}"><i class="bi bi-speedometer2 me-2"></i>My Dashboard</a></li>
                    <li><hr class="dropdown-divider"></li>
                    <li><a class="dropdown-item text-danger" href="javascript:void(0)" onclick="handleLogout()"><i class="bi bi-box-arrow-right me-2"></i>Logout</a></li>
                </ul>
            </li>
        `;
    } else {
        authLinksHtml = `
            <li class="nav-item">
                <a class="nav-link" href="login.html"><i class="bi bi-box-arrow-in-right me-1"></i>Sign In</a>
            </li>
            <li class="nav-item ms-lg-2">
                <a class="btn btn-primary btn-sm px-3 rounded-pill" href="register.html">Get Started</a>
            </li>
        `;
    }

    navPlaceholder.innerHTML = `
        <nav class="navbar navbar-expand-lg navbar-dark navbar-custom">
            <div class="container">
                <a class="navbar-brand" href="index.html">
                    <i class="bi bi-buildings-fill fs-3 text-info"></i>
                    <span>RealEstate<span class="text-info">Hub</span></span>
                </a>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="collapse navbar-collapse" id="mainNav">
                    <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                        <li class="nav-item"><a class="nav-link" href="index.html">Home</a></li>
                        <li class="nav-item"><a class="nav-link" href="properties.html">Browse Properties</a></li>
                        <li class="nav-item"><a class="nav-link" href="properties.html?listingType=SALE">For Sale</a></li>
                        <li class="nav-item"><a class="nav-link" href="properties.html?listingType=RENT">For Rent</a></li>
                    </ul>
                    <ul class="navbar-nav ms-auto align-items-center">
                        ${authLinksHtml}
                    </ul>
                </div>
            </div>
        </nav>
    `;
}

async function loadHeaderNotifications() {
    try {
        const notifData = await apiFetch('/api/notifications');
        const countData = await apiFetch('/api/notifications/unread-count');
        
        const countEl = document.getElementById('navUnreadCount');
        const itemsEl = document.getElementById('navNotifItems');

        if (countEl && countData && countData.unreadCount > 0) {
            countEl.textContent = countData.unreadCount;
            countEl.classList.remove('d-none');
        } else if (countEl) {
            countEl.classList.add('d-none');
        }

        if (itemsEl && notifData && notifData.length > 0) {
            itemsEl.innerHTML = notifData.slice(0, 6).map(n => `
                <div class="notification-item ${n.read ? '' : 'unread'}" onclick="markNotificationRead(${n.id})">
                    <div class="d-flex justify-content-between align-items-start">
                        <strong class="d-block text-truncate" style="max-width: 220px;">${n.title}</strong>
                        <small class="text-muted" style="font-size:0.7rem;">${formatDate(n.createdAt)}</small>
                    </div>
                    <div class="text-muted small text-truncate" style="max-width: 280px;">${n.message}</div>
                </div>
            `).join('');
        }
    } catch (e) {
        console.warn('Could not load header notifications:', e);
    }
}

async function markNotificationRead(id) {
    try {
        await apiFetch(`/api/notifications/${id}/read`, { method: 'PATCH' });
        loadHeaderNotifications();
    } catch (e) {
        console.error(e);
    }
}

async function markAllNotificationsRead() {
    try {
        await apiFetch('/api/notifications/read-all', { method: 'PATCH' });
        showToast('All notifications marked as read', 'success');
        loadHeaderNotifications();
    } catch (e) {
        console.error(e);
    }
}

function handleLogout() {
    clearSession();
    showToast('You have been signed out.', 'info');
    setTimeout(() => {
        window.location.href = 'index.html';
    }, 400);
}

function requireAuth(allowedRoles = []) {
    const token = getToken();
    const user = getCurrentUser();

    if (!token || !user) {
        window.location.href = 'login.html';
        return null;
    }

    if (allowedRoles.length > 0 && !allowedRoles.includes(user.role)) {
        showToast('Unauthorized access to this portal.', 'danger');
        setTimeout(() => {
            window.location.href = 'index.html';
        }, 1200);
        return null;
    }

    return user;
}
