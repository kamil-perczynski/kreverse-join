package io.github.kamilperczynski.kreversejoin

import org.slf4j.LoggerFactory

abstract class Sl4fj {
    internal val log = LoggerFactory.getLogger(this.javaClass.enclosingClass!!)
}
