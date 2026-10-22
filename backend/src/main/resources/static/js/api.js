// API Client & Utility Functions

const API_CONFIG = {
    // Determine backend base URL: relative if served from backend, absolute if live-server/file
    baseUrl: (window.location.port === '8080') ? '' : 'http://localhost:8080',
    tokenKey: 'realestate_jwt_token',
    userKey: 'realestate_user_info'
};

// Storage Helpers
function getToken() {
    return localStorage.getItem(API_CONFIG.tokenKey);
}

function setToken(token) {
    localStorage.setItem(API_CONFIG.tokenKey, token);
}

function removeToken() {
    localStorage.removeItem(API_CONFIG.tokenKey);
}

function getCurrentUser() {
    const raw = localStorage.getItem(API_CONFIG.userKey);
    try {
        return raw ? JSON.parse(raw) : null;
    } catch (e) {
        return null;
    }
}

function setCurrentUser(user) {
    localStorage.setItem(API_CONFIG.userKey, JSON.stringify(user));
}

function clearSession() {
    localStorage.removeItem(API_CONFIG.tokenKey);
    localStorage.removeItem(API_CONFIG.userKey);
}

// Core API Fetch wrapper
async function apiFetch(endpoint, options = {}) {
    const url = `${API_CONFIG.baseUrl}${endpoint}`;
    const token = getToken();

    const headers = {
        'Content-Type': 'application/json',
        ...(options.headers || {})
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const config = {
        ...options,
        headers
    };

    try {
        const response = await fetch(url, config);
        
        if (response.status === 401) {
            // Unauthorized - clear token and redirect if on private dashboard
            clearSession();
            if (window.location.pathname.includes('dashboard')) {
                window.location.href = 'login.html';
                return null;
            }
        }

        const contentType = response.headers.get('content-type');
        let data = null;
        if (contentType && contentType.includes('application/json')) {
            data = await response.json();
        } else {
            data = await response.text();
        }

        if (!response.ok) {
            const errorMsg = (data && data.error) ? data.error : (data && data.message) ? data.message : 'Request failed';
            throw new Error(errorMsg);
        }

        return data;
    } catch (err) {
        console.error('API Error:', err);
        throw err;
    }
}

// Toast Notifications
function showToast(message, type = 'info') {
    let container = document.getElementById('toastContainer');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toastContainer';
        document.body.appendChild(container);
    }

    const bgClass = type === 'success' ? 'bg-success text-white' :
                    type === 'error' || type === 'danger' ? 'bg-danger text-white' :
                    type === 'warning' ? 'bg-warning text-dark' : 'bg-primary text-white';

    const toastEl = document.createElement('div');
    toastEl.className = `toast align-items-center ${bgClass} border-0 show shadow mb-2`;
    toastEl.setAttribute('role', 'alert');
    toastEl.style.minWidth = '260px';
    toastEl.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">
                <i class="bi ${type === 'success' ? 'bi-check-circle-fill' : type === 'error' ? 'bi-exclamation-triangle-fill' : 'bi-info-circle-fill'} me-2"></i>
                ${message}
            </div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
        </div>
    `;

    container.appendChild(toastEl);
    setTimeout(() => {
        toastEl.remove();
    }, 4500);
}

// Formatting helpers
function formatCurrency(amount, listingType) {
    if (amount === undefined || amount === null) return '$0';
    const formatted = new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD', maximumFractionDigits: 0 }).format(amount);
    return listingType === 'RENT' ? `${formatted}/mo` : formatted;
}

function formatDate(dateStr) {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
}
