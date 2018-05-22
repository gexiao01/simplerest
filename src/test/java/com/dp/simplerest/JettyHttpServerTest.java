package com.dp.simplerest;

import java.io.IOException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dp.simplerest.exception.RestException;

/**
 * demo of a simple http server,not in testsuite
 */
public class JettyHttpServerTest {
    public static void main(String[] args) throws RestException, IOException {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "classpath:application-context-test.xml");
        JettyHttpServer server = context.getBean(JettyHttpServer.class);

        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            context.close();
        }
    }
}
