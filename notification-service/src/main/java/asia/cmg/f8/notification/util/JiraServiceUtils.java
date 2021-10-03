package asia.cmg.f8.notification.util;

import asia.cmg.f8.notification.dto.JiraTicketRequest;
import asia.cmg.f8.notification.dto.ServicedeskRequest;
import asia.cmg.f8.notification.dto.ServicedeskRequestFieldExtent;
import asia.cmg.f8.notification.dto.ServicedeskRequestFieldValue;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Objects;

/**
 * Created on 12/29/16.
 */
public final class JiraServiceUtils {

    private static final int ACCOUNT_REQUEST = 179;

    private JiraServiceUtils() {
    }

    public static String base64ClientCredentials(final String userName,
                                                 final String password) {
        final String plainClientCredentials = String.format("%s:%s", userName, password);
        final String base64Credentials =
                new String(Base64.encodeBase64(plainClientCredentials.getBytes()));
        return String.format("Basic %s", base64Credentials);
    }

    public static ServicedeskRequest buildTicket(final JiraTicketRequest request,
                                                 final Integer servicedeskId,
                                                 final String annonymousUserRole,
                                                 final String ptRole,
                                                 final String euRole){
        if (!Objects.isNull(request.getTopic()) && request.getTopic().equals(ACCOUNT_REQUEST)) {
            return ServicedeskRequest.builder()
                    .servicedeskId(servicedeskId)
                    .requestTypeId(request.getTopic())
                    .requestFieldValues(ServicedeskRequestFieldValue.builder()
                    .summary(request.getSummary())
                    .userName(request.getUserName())
                    .email(request.getEmail())
                    .firstName(request.getName())
                    .lastName(request.getName())
                    .build()).build();

        } else {
            final Map<String, Object> role = new HashedMap();
            if (StringUtils.isEmpty(request.getRole())) {
                role.put("id", annonymousUserRole);
            } else if ("pt".equalsIgnoreCase(request.getRole())) {
                role.put("id", ptRole);
            } else if ("eu".equalsIgnoreCase(request.getRole())) {
                role.put("id", euRole);
            }
            return ServicedeskRequest.builder()
                    .servicedeskId(servicedeskId)
                    .requestTypeId(request.getTopic())
                    .requestFieldValues(ServicedeskRequestFieldExtent.builder()
                    .firstName(request.getName())
                    .lastName(request.getName())
                    .email(request.getEmail())
                    .role(role)
                    .source(request.getSource())
                    .reportedUserId(request.getReportedUserId())
                    .reportedPostId(request.getReportedPostId())
                    .description(request.getDescription())
                    .summary(request.getSummary())
                    .userName(request.getUserName())
                    .phoneNumber(request.getPhoneNumber()).build()).build();
        }
    }
}
