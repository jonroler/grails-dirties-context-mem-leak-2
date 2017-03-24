package memleak

import grails.test.mixin.integration.Integration
import grails.util.Holder
import grails.util.Holders
import grails.validation.ConstrainedProperty
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Specification

@Integration
public class Leak1Spec extends Specification {
  static Logger logger = LoggerFactory.getLogger(Leak1Spec.class)

  @DirtiesContext
  def "dirty context 1"() {
    when:
    logger.error("Size of ConstrainedProperty.constraints.unique: ${ConstrainedProperty.constraints.unique.size()}")

    then:
    1 == 1
  }

  @DirtiesContext
  def "dirty context 2"() {
    when:
    logger.error("Size of ConstrainedProperty.constraints.unique: ${ConstrainedProperty.constraints.unique.size()}")

    then:
    1 == 1
  }

  @DirtiesContext
  def "dirty context 3"() {
    when:
    logger.error("Size of ConstrainedProperty.constraints.unique: ${ConstrainedProperty.constraints.unique.size()}")

    then:
    1 == 1
  }
}