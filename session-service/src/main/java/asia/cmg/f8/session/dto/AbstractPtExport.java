package asia.cmg.f8.session.dto;

import org.immutables.value.Value;

import javax.annotation.Nullable;


/**
 * Created on 1/11/17.
 */
@Value.Immutable
@Value.Style(
        typeImmutable = "*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC)
public interface AbstractPtExport {

    @Nullable
    String getUuid();

    @Nullable
    String getName();

    @Nullable
    String getUsername();

    @Nullable
    String getAvatar();

    @Nullable
    String getCity();

    @Nullable
    String getCountry();

    @Nullable
    Boolean isActivated();

    @Nullable
    String getJoinDate();

    @Nullable
    String getDocumentStatus();

    @Nullable
    String getLevel();

    @Nullable
    Double getPtRevenue();

    @Nullable
    Double getF8Revenue();

    @Nullable
    String getDisplayPtRevenue();

    @Nullable
    String getDisplayF8Revenue();

    @Nullable
    String getEmail();
    
    @Nullable
    String getJoinTime();
    
    @Nullable
    String getStaffCode();
}
