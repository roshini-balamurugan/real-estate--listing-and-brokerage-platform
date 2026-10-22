// Property Details Logic

let currentProperty = null;

document.addEventListener('DOMContentLoaded', () => {
    const params = new URLSearchParams(window.location.search);
    const id = params.get('id');

    if (!id) {
        showToast('No property specified', 'danger');
        setTimeout(() => {
            window.location.href = 'properties.html';
        }, 1500);
        return;
    }

    // Set min date to today for visit scheduling
    const today = new Date().toISOString().split('T')[0];
    const visitDateInput = document.getElementById('visitDate');
    if (visitDateInput) {
        visitDateInput.min = today;
        visitDateInput.value = today;
    }

    loadPropertyDetails(id);
    setupForms(id);
});

async function loadPropertyDetails(id) {
    const loadingEl = document.getElementById('detailsLoading');
    const containerEl = document.getElementById('detailsContainer');

    try {
        currentProperty = await apiFetch(`/api/properties/${id}`);

        if (loadingEl) loadingEl.classList.add('d-none');
        if (containerEl) containerEl.classList.remove('d-none');

        // Populate Details
        document.title = `${currentProperty.title} - RealEstateHub`;
        document.getElementById('breadcrumbTitle').textContent = currentProperty.title;
        document.getElementById('propTitle').textContent = currentProperty.title;
        document.getElementById('propAddress').innerHTML = `
            <i class="bi bi-geo-alt-fill text-danger me-1"></i>
            ${currentProperty.address ? currentProperty.address + ', ' : ''}${currentProperty.city}${currentProperty.state ? ', ' + currentProperty.state : ''}
        `;
        document.getElementById('propPrice').textContent = formatCurrency(currentProperty.price, currentProperty.listingType);

        // Badges
        document.getElementById('badgeListingType').textContent = currentProperty.listingType === 'SALE' ? 'FOR SALE' : 'FOR RENT';
        document.getElementById('badgeListingType').className = currentProperty.listingType === 'SALE' ? 'badge bg-primary' : 'badge bg-success';
        document.getElementById('badgePropertyType').textContent = currentProperty.propertyType;
        document.getElementById('badgeStatus').textContent = currentProperty.status;

        // Images
        const defaultImg = 'https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=1200&q=80';
        const mainImgUrl = currentProperty.imageUrl && currentProperty.imageUrl.startsWith('http') ? currentProperty.imageUrl : defaultImg;
        document.getElementById('mainImage').src = mainImgUrl;

        // Thumbnails
        const thumbContainer = document.getElementById('thumbnailContainer');
        if (thumbContainer) {
            let images = [mainImgUrl];
            if (currentProperty.additionalImages) {
                const extras = currentProperty.additionalImages.split(',').map(s => s.trim()).filter(s => s.startsWith('http'));
                images = images.concat(extras);
            }
            thumbContainer.innerHTML = images.slice(0, 3).map((img, idx) => `
                <div class="rounded-3 overflow-hidden shadow-sm" style="height: 140px; cursor:pointer;" onclick="document.getElementById('mainImage').src='${img}'">
                    <img src="${img}" class="w-100 h-100" style="object-fit:cover;" alt="Thumbnail ${idx + 1}">
                </div>
            `).join('');
        }

        // Specs
        document.getElementById('specBeds').textContent = currentProperty.bedrooms !== null ? currentProperty.bedrooms : 'N/A';
        document.getElementById('specBaths').textContent = currentProperty.bathrooms !== null ? currentProperty.bathrooms : 'N/A';
        document.getElementById('specArea').textContent = currentProperty.areaSqFt ? `${currentProperty.areaSqFt} sqft` : 'N/A';
        document.getElementById('specStatus').textContent = currentProperty.status;

        // Description
        document.getElementById('propDescription').textContent = currentProperty.description || 'No description provided for this listing.';

        // Favorite Button state
        updateFavButtonState(currentProperty.isFavorite);

        // Host / Agent Information
        const host = currentProperty.agent || currentProperty.owner;
        if (host) {
            document.getElementById('hostInitial').textContent = host.name ? host.name.charAt(0).toUpperCase() : 'H';
            document.getElementById('hostName').textContent = host.name;
            document.getElementById('hostAgency').textContent = host.agencyName || (host.role === 'ROLE_AGENT' ? 'Independent Broker' : 'Property Owner');

            const isAgent = host.role === 'ROLE_AGENT';
            const isVerified = host.agentVerified === true;

            const badgeContainer = document.getElementById('hostVerifiedBadge');
            if (isAgent) {
                badgeContainer.innerHTML = isVerified
                    ? `<span class="badge-verified"><i class="bi bi-patch-check-fill"></i> Verified Broker</span>`
                    : `<span class="badge-pending"><i class="bi bi-clock-history"></i> Verification Pending</span>`;
            } else {
                badgeContainer.innerHTML = `<span class="badge bg-light text-dark border">Property Owner</span>`;
            }

            if (host.licenseNumber) {
                document.getElementById('hostLicense').textContent = host.licenseNumber;
            } else {
                document.getElementById('hostLicenseRow').classList.add('d-none');
            }

            if (host.phone) {
                document.getElementById('hostPhone').textContent = host.phone;
            } else {
                document.getElementById('hostPhoneRow').classList.add('d-none');
            }
        }

    } catch (err) {
        console.error(err);
        if (loadingEl) {
            loadingEl.innerHTML = `
                <div class="text-center py-5">
                    <div class="alert alert-danger d-inline-block">
                        <i class="bi bi-exclamation-triangle-fill me-2"></i>Could not load property. ${err.message}
                    </div>
                    <div class="mt-3">
                        <a href="properties.html" class="btn btn-primary btn-sm">Return to Properties</a>
                    </div>
                </div>
            `;
        }
    }
}

function updateFavButtonState(isFav) {
    const btn = document.getElementById('detailsFavBtn');
    if (!btn) return;
    if (isFav) {
        btn.className = 'btn btn-danger w-100 fw-semibold mb-3';
        btn.innerHTML = '<i class="bi bi-heart-fill me-2"></i>Saved in Favorites';
    } else {
        btn.className = 'btn btn-outline-danger w-100 fw-semibold mb-3';
        btn.innerHTML = '<i class="bi bi-heart me-2"></i>Save to Favorites';
    }
}

async function toggleDetailsFavorite() {
    const token = getToken();
    if (!token) {
        showToast('Please sign in to save this property.', 'warning');
        setTimeout(() => {
            window.location.href = 'login.html';
        }, 1200);
        return;
    }

    if (!currentProperty) return;

    try {
        if (currentProperty.isFavorite) {
            await apiFetch(`/api/favorites/${currentProperty.id}`, { method: 'DELETE' });
            currentProperty.isFavorite = false;
            updateFavButtonState(false);
            showToast('Removed from favorites', 'info');
        } else {
            await apiFetch(`/api/favorites/${currentProperty.id}`, { method: 'POST' });
            currentProperty.isFavorite = true;
            updateFavButtonState(true);
            showToast('Added to your favorite properties!', 'success');
        }
    } catch (err) {
        showToast(err.message || 'Error updating favorite', 'danger');
    }
}

function setupForms(propertyId) {
    // Schedule Visit Form
    const visitForm = document.getElementById('scheduleVisitForm');
    if (visitForm) {
        visitForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const token = getToken();
            if (!token) {
                showToast('Please sign in to schedule a property tour.', 'warning');
                setTimeout(() => {
                    window.location.href = 'login.html';
                }, 1200);
                return;
            }

            const visitDate = document.getElementById('visitDate').value;
            const timeSlot = document.getElementById('visitTimeSlot').value;
            const notes = document.getElementById('visitNotes').value.trim();

            try {
                const payload = {
                    propertyId: parseInt(propertyId),
                    visitDate,
                    timeSlot,
                    notes
                };

                await apiFetch('/api/visits', {
                    method: 'POST',
                    body: JSON.stringify(payload)
                });

                // Close modal
                const modalEl = document.getElementById('scheduleVisitModal');
                const modal = bootstrap.Modal.getInstance(modalEl);
                if (modal) modal.hide();

                showToast('Visit request sent! Check your Buyer Dashboard for status updates.', 'success');
                visitForm.reset();
            } catch (err) {
                showToast(err.message || 'Failed to schedule visit', 'danger');
            }
        });
    }

    // Inquiry Form
    const inquiryForm = document.getElementById('detailsInquiryForm');
    if (inquiryForm) {
        inquiryForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const token = getToken();
            if (!token) {
                showToast('Please sign in to send an inquiry.', 'warning');
                setTimeout(() => {
                    window.location.href = 'login.html';
                }, 1200);
                return;
            }

            const subject = document.getElementById('inqSubject').value.trim();
            const phone = document.getElementById('inqPhone').value.trim();
            const message = document.getElementById('inqMessage').value.trim();

            try {
                const payload = {
                    propertyId: parseInt(propertyId),
                    subject,
                    phone,
                    message
                };

                await apiFetch('/api/inquiries', {
                    method: 'POST',
                    body: JSON.stringify(payload)
                });

                showToast('Inquiry delivered successfully to the host!', 'success');
                inquiryForm.reset();
            } catch (err) {
                showToast(err.message || 'Failed to send inquiry', 'danger');
            }
        });
    }
}
