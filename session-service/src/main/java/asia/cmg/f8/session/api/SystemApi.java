package asia.cmg.f8.session.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.session.dto.PackageInfoResponse;
import asia.cmg.f8.session.entity.SessionPackageEntity;
import asia.cmg.f8.session.service.SessionService;

/**
 * Created on 1/4/17.
 */
@RestController
public class SystemApi {
    private final SessionService sessionService;

    public SystemApi(final SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @RequestMapping(value = "/system/packages", method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getSessionPackageById(
            @RequestParam("package_id") final String packageUuid) {

        final Optional<SessionPackageEntity> sessionPackageOptional =
                sessionService.getPackageByUuid(packageUuid);

        if (!sessionPackageOptional.isPresent()) {
            return new ResponseEntity<>(
                    ErrorCode.REQUEST_INVALID.withDetail("Could not found session package " + packageUuid),
                    HttpStatus.BAD_REQUEST);
        }
        final SessionPackageEntity packageEntity = sessionPackageOptional.get();

        return new ResponseEntity<>(PackageInfoResponse
                .builder()
                .packageUuid(packageEntity.getUuid())
                .numberOfBurned(packageEntity.getNumOfBurned())
                .numberOfSession(packageEntity.getNumOfSessions())
                .build(), HttpStatus.OK);
    }
}
