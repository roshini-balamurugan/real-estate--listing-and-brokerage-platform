// Properties Catalog & Filtering Logic

document.addEventListener('DOMContentLoaded', () => {
    initFiltersFromUrl();
    setupEventListeners();
    fetchAndRenderProperties();
});

function initFiltersFromUrl() {
    const params = new URLSearchParams(window.location.search);
    if (params.get('city')) document.getElementById('filterCity').value = params.get('city');
    if (params.get('listingType')) document.getElementById('filterListingType').value = params.get('listingType');
    if (params.get('propertyType')) document.getElementById('filterPropertyType').value = params.get('propertyType');
    if (params.get('minPrice')) document.getElementById('filterMinPrice').value = params.get('minPrice');
    if (params.get('maxPrice')) document.getElementById('filterMaxPrice').value = params.get('maxPrice');
}

function setupEventListeners() {
    const form = document.getElementById('filterForm');
    if (form) {
        form.addEventListener('submit', (e) => {
            e.preventDefault();
            fetchAndRenderProperties();
        });
    }

    const resetBtn = document.getElementById('resetFiltersBtn');
    if (resetBtn) {
        resetBtn.addEventListener('click', () => {
            document.getElementById('filterCity').value = '';
            document.getElementById('filterListingType').value = '';
            document.getElementById('filterPropertyType').value = '';
            document.getElementById('filterMinPrice').value = '';
            document.getElementById('filterMaxPrice').value = '';
            fetchAndRenderProperties();
        });
    }
}

async function fetchAndRenderProperties() {
    const grid = document.getElementById('propertiesGrid');
    const countEl = document.getElementById('resultsCount');
    if (!grid) return;

    grid.innerHTML = `
        <div class="col-12 text-center py-5">
            <div class="spinner-border text-primary" role="status"></div>
            <div class="text-muted mt-2">Searching properties...</div>
        </div>
    `;

    const city = document.getElementById('filterCity').value.trim();
    const listingType = document.getElementById('filterListingType').value;
    const propertyType = document.getElementById('filterPropertyType').value;
    const minPrice = document.getElementById('filterMinPrice').value;
    const maxPrice = document.getElementById('filterMaxPrice').value;

    const queryParams = new URLSearchParams();
    if (city) queryParams.append('city', city);
    if (listingType) queryParams.append('listingType', listingType);
    if (propertyType) queryParams.append('propertyType', propertyType);
    if (minPrice) queryParams.append('minPrice', minPrice);
    if (maxPrice) queryParams.append('maxPrice', maxPrice);

    try {
        const endpoint = `/api/properties${queryParams.toString() ? '?' + queryParams.toString() : ''}`;
        const properties = await apiFetch(endpoint);

        if (countEl) {
            countEl.textContent = `Found ${properties.length} ${properties.length === 1 ? 'property' : 'properties'}`;
        }

        if (!properties || properties.length === 0) {
            grid.innerHTML = `
                <div class="col-12 text-center py-5">
                    <div class="p-5 bg-white border rounded-3 shadow-sm">
                        <i class="bi bi-search fs-1 text-muted"></i>
                        <h5 class="fw-bold mt-3">No Properties Match Your Search</h5>
                        <p class="text-muted small">Try broadening your search criteria or resetting filters.</p>
                        <button class="btn btn-outline-primary btn-sm" onclick="document.getElementById('resetFiltersBtn').click()">Clear All Filters</button>
                    </div>
                </div>
            `;
            return;
        }

        grid.innerHTML = properties.map(p => renderPropertyCard(p)).join('');

    } catch (err) {
        console.error(err);
        grid.innerHTML = `
            <div class="col-12 text-center py-5">
                <div class="alert alert-danger d-inline-block">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i>Error loading properties. ${err.message}
                </div>
            </div>
        `;
    }
}

function renderPropertyCard(p) {
    const defaultImage = 'https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=800&q=80';
    const imgUrl = p.imageUrl && p.imageUrl.startsWith('http') ? p.imageUrl : defaultImage;
    const isSale = p.listingType === 'SALE';
    const isFav = p.isFavorite ? 'active' : '';

    return `
        <div class="col-md-6 col-lg-4">
            <div class="property-card shadow-sm">
                <div class="property-image-container">
                    <img src="${imgUrl}" alt="${p.title}" loading="lazy" onerror="this.src='${defaultImage}'">
                    <span class="property-badge-type ${isSale ? 'property-badge-sale' : 'property-badge-rent'}">
                        ${isSale ? 'For Sale' : 'For Rent'}
                    </span>
                    <button class="property-fav-btn ${isFav}" onclick="toggleFavorite(${p.id}, this)" title="Save property">
                        <i class="bi ${p.isFavorite ? 'bi-heart-fill' : 'bi-heart'}"></i>
                    </button>
                </div>
                <div class="property-card-body">
                    <div class="d-flex justify-content-between align-items-center mb-1">
                        <span class="badge bg-light text-dark border text-uppercase" style="font-size:0.7rem;">${p.propertyType}</span>
                        <div class="property-price">${formatCurrency(p.price, p.listingType)}</div>
                    </div>
                    <h5 class="fw-bold mb-1 text-truncate" title="${p.title}">
                        <a href="property-details.html?id=${p.id}" class="text-decoration-none text-dark">${p.title}</a>
                    </h5>
                    <p class="text-muted small mb-3">
                        <i class="bi bi-geo-alt me-1 text-danger"></i>${p.address ? p.address + ', ' : ''}${p.city}${p.state ? ', ' + p.state : ''}
                    </p>

                    <div class="property-specs">
                        ${p.bedrooms ? `<div class="spec-item"><i class="bi bi-door-open"></i>${p.bedrooms} Beds</div>` : ''}
                        ${p.bathrooms ? `<div class="spec-item"><i class="bi bi-droplet"></i>${p.bathrooms} Baths</div>` : ''}
                        ${p.areaSqFt ? `<div class="spec-item"><i class="bi bi-aspect-ratio"></i>${p.areaSqFt} sqft</div>` : ''}
                    </div>

                    <div class="d-grid mt-3">
                        <a href="property-details.html?id=${p.id}" class="btn btn-outline-primary btn-sm">
                            View Details <i class="bi bi-arrow-right ms-1"></i>
                        </a>
                    </div>
                </div>
            </div>
        </div>
    `;
}

async function toggleFavorite(propertyId, btn) {
    const token = getToken();
    if (!token) {
        showToast('Please sign in to save favorite properties.', 'warning');
        setTimeout(() => {
            window.location.href = 'login.html';
        }, 1200);
        return;
    }

    const icon = btn.querySelector('i');
    const isCurrentlyFav = btn.classList.contains('active');

    try {
        if (isCurrentlyFav) {
            await apiFetch(`/api/favorites/${propertyId}`, { method: 'DELETE' });
            btn.classList.remove('active');
            icon.className = 'bi bi-heart';
            showToast('Removed from favorites', 'info');
        } else {
            await apiFetch(`/api/favorites/${propertyId}`, { method: 'POST' });
            btn.classList.add('active');
            icon.className = 'bi bi-heart-fill';
            showToast('Saved to your favorites!', 'success');
        }
    } catch (err) {
        showToast(err.message || 'Error updating favorites', 'danger');
    }
}
