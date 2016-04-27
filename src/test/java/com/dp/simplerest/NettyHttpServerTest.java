package com.dp.simplerest;

import java.io.IOException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dp.simplerest.NettyHttpServer;
import com.dp.simplerest.exception.RestException;

/**
 * demo of a simple http server,not in testsuite
 */
public class NettyHttpServerTest {
    public static void main(String[] args) throws RestException, IOException {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "classpath:application-context-test.xml");
        NettyHttpServer server = context.getBean(NettyHttpServer.class);

        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            context.close();
        }
    }
}
