package asia.cmg.f8.session.api;

import asia.cmg.f8.session.utils.ReportUtils;

/**
 * Created on 12/20/16.
 */

@SuppressWarnings("PMD")
public final class SortFieldConverter {

    private SortFieldConverter() {
        // empty
    }

    public static String getSortField(final String sortField, final SortFieldMapping mapping) {
        switch (sortField) {
            case ReportUtils.NAME:
                return mapping.getFullNameSortField();
            case "session_burned":
                return mapping.getSessionBurnedSortField();
            case "expired_date":
                return mapping.getExpiredDateSortField();
            case "price":
                return mapping.getPriceSortField();
            case "commission":
                return mapping.getCommissionSortField();
            case "username":
                return mapping.getUsernameSortField();
            case "userName":
                return mapping.getUsernameSortField();
            case "city":
                return mapping.getCitySortField();
            case "country":
                return mapping.getCountrySortField();
            case "join_date":
                return mapping.getJoinDateSortField();
            case "document_approval_status":
                return mapping.getJoinDateSortField();
            case "level":
                return mapping.getLevelSortField();
            case "pt_revenue":
                return mapping.getPtRevenueSortField();
            case "f8_revenue":
                return mapping.getF8RevenueSortField();
            case "activated":
                return mapping.getActivatedSortField();
            default:
                return mapping.getFullNameSortField();
        }
    }
}
