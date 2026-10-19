package com.realestate.backend.dto;

public class DashboardStatsDTO {

    private long totalUsers;
    private long totalBuyers;
    private long totalSellers;
    private long totalAgents;
    private long pendingAgentVerifications;

    private long totalProperties;
    private long propertiesForSale;
    private long propertiesForRent;
    private long activeListings;

    private long totalVisits;
    private long totalInquiries;

    public DashboardStatsDTO() {
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalBuyers() {
        return totalBuyers;
    }

    public void setTotalBuyers(long totalBuyers) {
        this.totalBuyers = totalBuyers;
    }

    public long getTotalSellers() {
        return totalSellers;
    }

    public void setTotalSellers(long totalSellers) {
        this.totalSellers = totalSellers;
    }

    public long getTotalAgents() {
        return totalAgents;
    }

    public void setTotalAgents(long totalAgents) {
        this.totalAgents = totalAgents;
    }

    public long getPendingAgentVerifications() {
        return pendingAgentVerifications;
    }

    public void setPendingAgentVerifications(long pendingAgentVerifications) {
        this.pendingAgentVerifications = pendingAgentVerifications;
    }

    public long getTotalProperties() {
        return totalProperties;
    }

    public void setTotalProperties(long totalProperties) {
        this.totalProperties = totalProperties;
    }

    public long getPropertiesForSale() {
        return propertiesForSale;
    }

    public void setPropertiesForSale(long propertiesForSale) {
        this.propertiesForSale = propertiesForSale;
    }

    public long getPropertiesForRent() {
        return propertiesForRent;
    }

    public void setPropertiesForRent(long propertiesForRent) {
        this.propertiesForRent = propertiesForRent;
    }

    public long getActiveListings() {
        return activeListings;
    }

    public void setActiveListings(long activeListings) {
        this.activeListings = activeListings;
    }

    public long getTotalVisits() {
        return totalVisits;
    }

    public void setTotalVisits(long totalVisits) {
        this.totalVisits = totalVisits;
    }

    public long getTotalInquiries() {
        return totalInquiries;
    }

    public void setTotalInquiries(long totalInquiries) {
        this.totalInquiries = totalInquiries;
    }
}
