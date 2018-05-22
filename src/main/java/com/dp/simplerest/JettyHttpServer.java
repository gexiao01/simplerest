package com.dp.simplerest;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.beans.factory.annotation.Autowired;

import com.dp.simplerest.binding.BindingMeta;
import com.dp.simplerest.jetty.SimpleHttpServlet;

public class JettyHttpServer {

	private Server server;
	private final InetSocketAddress host;

	@Autowired
	private BindingMeta meta;

	private Logger logger = Logger.getLogger(getClass());

	public JettyHttpServer(String hostname, int port) {
		host = new InetSocketAddress(hostname, port);
	}

	public void start() throws Exception {
		server = new Server(host);
		ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        context.addServlet(new ServletHolder(new SimpleHttpServlet(meta)), "/");
        HandlerCollection handlers = new HandlerCollection();
        handlers.setHandlers(new Handler[] { context, new DefaultHandler() });
        server.setHandler(handlers);
        server.start();
        server.join();
		logger.info("Start JettyHttpServer at " + host);
	}

	public void close() {
		logger.info("Close JettyHttpServer at " + host);
		if (server != null) {
			server.destroy();
		}
	}

	public InetSocketAddress getHost() {
		return host;
	}

}
