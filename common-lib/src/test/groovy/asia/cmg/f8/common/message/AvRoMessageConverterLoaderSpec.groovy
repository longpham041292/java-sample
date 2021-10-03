package asia.cmg.f8.common.message

import spock.lang.Specification

/**
 * Created on 11/17/16.
 */
class AvRoMessageConverterLoaderSpec extends Specification {

    def 'load avro message in classpath'() {
        setup:
        def loader = new AvRoMessageConverterLoader()

        when:
        def converter = loader.load('ChangeUserInfoEvent.avsc')

        then:
        converter.isPresent()
    }
}
