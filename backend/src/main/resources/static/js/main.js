// Homepage Logic

document.addEventListener('DOMContentLoaded', () => {
    initHeroSearch();
    loadFeaturedProperties();
});

function initHeroSearch() {
    const form = document.getElementById('heroSearchForm');
    if (!form) return;

    form.addEventListener('submit', (e) => {
        e.preventDefault();
        const city = document.getElementById('searchCity').value.trim();
        const listingType = document.getElementById('searchListingType').value;
        const propertyType = document.getElementById('searchPropertyType').value;

        const params = new URLSearchParams();
        if (city) params.append('city', city);
        if (listingType) params.append('listingType', listingType);
        if (propertyType) params.append('propertyType', propertyType);

        window.location.href = `properties.html?${params.toString()}`;
    });
}

async function loadFeaturedProperties() {
    const container = document.getElementById('featuredPropertiesContainer');
    if (!container) return;

    try {
        const properties = await apiFetch('/api/properties');
        
        if (!properties || properties.length === 0) {
            container.innerHTML = `
                <div class="col-12 text-center py-5">
                    <p class="text-muted">No properties found at the moment.</p>
                </div>
            `;
            return;
        }

        // Display top 6 properties
        const featured = properties.slice(0, 6);
        container.innerHTML = featured.map(p => renderPropertyCard(p)).join('');

    } catch (err) {
        console.error('Error loading featured properties:', err);
        container.innerHTML = `
            <div class="col-12 text-center py-5">
                <div class="alert alert-warning d-inline-block">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i>Unable to load properties. Ensure the backend server is running.
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
        <div class="col-lg-4 col-md-6">
            <div class="property-card shadow-sm">
                <div class="property-image-container">
                    <img src="${imgUrl}" alt="${p.title}" loading="lazy" onerror="this.src='${defaultImage}'">
                    <span class="property-badge-type ${isSale ? 'property-badge-sale' : 'property-badge-rent'}">
                        ${isSale ? 'For Sale' : 'For Rent'}
                    </span>
                    <button class="property-fav-btn ${isFav}" onclick="toggleFavorite(${p.id}, this)" title="Add to favorites">
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
