package asia.cmg.f8.common.util

import spock.lang.Specification

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

/**
 * Created on 12/14/16.
 */
class ZoneDateTimeUtilsSpec extends Specification {

    def 'convert local date time to second UTC'() {

        setup:
        def localDateTime = LocalDateTime.now(ZoneId.systemDefault())

        def ldtGmt = localDateTime.atZone(ZoneId.systemDefault())

        def ldtUtc = ldtGmt.withZoneSameInstant(ZoneId.of('Z')).toLocalDateTime()

        when:
        def epochSecondUtc = ZoneDateTimeUtils.convertToSecondUTC(localDateTime)

        then:
        epochSecondUtc == ldtUtc.toEpochSecond(ZoneOffset.UTC)
    }
}
