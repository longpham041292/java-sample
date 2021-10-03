package asia.cmg.f8.gateway.security.auth

import asia.cmg.f8.gateway.security.api.UserDetail
import spock.lang.Specification

/**
 * Created on 10/25/16.
 */
class TokenAuthenticationSpec extends Specification {

    def 'auth is not authenticated if user is unactivated'() {

        setup:
        def activated = false
        def detail = Mock(UserDetail)

        detail.isActivated() >> activated

        def auth = new TokenAuthentication('uuid', 1, detail)

        expect:
        auth.authenticated == activated
    }
}
