package com.dp.simplerest;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.springframework.beans.factory.annotation.Autowired;

import com.dp.simplerest.binding.BindingMeta;
import com.dp.simplerest.netty.SimpleHttpRequestHandler;

public class NettyHttpServer {

	private ServerBootstrap bootstrap;
	private final InetSocketAddress host;

	@Autowired
	private BindingMeta meta;

	private Logger logger = Logger.getLogger(getClass());

	public NettyHttpServer(String hostname, int port) {
		host = new InetSocketAddress(hostname, port);
	}

	public void start() throws IOException {
		bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory());

		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {

			@Override
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();

				pipeline.addLast("decoder", new HttpRequestDecoder());
				pipeline.addLast("encoder", new HttpResponseEncoder());
				pipeline.addLast("deflater", new HttpContentCompressor());

				pipeline.addLast("handler", new SimpleHttpRequestHandler(meta));
				return pipeline;
			}

		});
		bootstrap.bind(host);

		logger.info("Start NettyHttpServer at " + host);
	}

	public void close() {
		logger.info("Close NettyHttpServer at " + host);
		if (bootstrap != null) {
			bootstrap.shutdown();
			bootstrap.releaseExternalResources();
		}
	}

	public InetSocketAddress getHost() {
		return host;
	}

}
