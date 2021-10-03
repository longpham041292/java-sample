package asia.cmg.f8.session.api;

/**
 * Created on 12/20/16.
 */
public interface SortFieldMapping {

    default String getSessionBurnedSortField() {
        return null;
    }

    default String getExpiredDateSortField() {
        return null;
    }

    default String getPriceSortField() {
        return null;
    }

    default String getCommissionSortField() {
        return null;
    }

    default String getFullNameSortField() {
        return null;
    }
    
    default String getUsernameSortField() {
        return null;
    }

    default String getCitySortField() {
        return null;
    }
    
    default String getCountrySortField() {
        return null;
    }
    
    default String getJoinDateSortField() {
        return null;
    }

    default String getDocumentStatusSortField() {
        return null;
    }
    
    default String getLevelSortField() {
        return null;
    }

    default String getPtRevenueSortField() {
        return null;
    }

    default String getF8RevenueSortField() {
        return null;
    }

    default String getActivatedSortField() {
        return null;
    }
    
}
