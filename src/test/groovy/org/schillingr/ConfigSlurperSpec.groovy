package org.schillingr

import spock.lang.Specification
import spock.lang.Unroll

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class ConfigSlurperSpec extends Specification {
    @Unroll
    def "test config slurper attempt #attempts"() {
        setup:
        ConfigSlurper slurper = new ConfigSlurper('grid_firefox')
        GroovyClassLoader classLoader = new GroovyClassLoader(getClass().getClassLoader())
        String resource = 'GebConfig'

        when:
        Class configClass = classLoader.loadClass(resource)

        then:
        configClass

        when:
        ConfigObject config = slurper.parse(configClass)
        String driverClassName = config['driver'].getClass().getName()
        println("Driver class name: ${driverClassName}")

        then:
        !driverClassName.contains('determine')

        where:
        attempts << [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30]
    }

    def "test config slurper threaded"() {

        given:
        int numThreads = 30
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads)
        List<Future<String>> futureList = []

        when:
        numThreads.times {
            Future<String> future = executorService.submit(new Callable<String>() {
                @Override
                String call() throws Exception {
                    ConfigSlurper slurper = new ConfigSlurper('grid_firefox')
                    GroovyClassLoader classLoader = new GroovyClassLoader(getClass().getClassLoader())
                    String resource = 'GebConfig'
                    Class configClass = classLoader.loadClass(resource)
                    ConfigObject config = slurper.parse(configClass)
                    String driverClassName = config['driver'].getClass().getName()
                    println("${Thread.currentThread().getName()} - Driver class name: ${driverClassName}")
                    return driverClassName
                }
            })
            futureList.add(future)
        }


        then:
        for(Future<String> future : futureList) {
            assert !future.get().contains('determine')
        }

        cleanup:
        executorService.shutdown()
    }
}
