package com.dp.simplerest.netty;

import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import com.dp.simplerest.binding.BindingMeta;
import com.dp.simplerest.util.JsonUtils;

public class SimpleHttpRequestHandler extends SimpleChannelUpstreamHandler {

    private Logger logger = Logger.getLogger(getClass());

    private final BindingMeta meta;

    public SimpleHttpRequestHandler(BindingMeta meta) {
        this.meta = meta;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {

        HttpRequest request = (HttpRequest) e.getMessage();
        String uri = request.getUri();
        logger.info("Rest Request:" + uri);

        if (!uri.contains(".")) { // a simple pattern filter
            Object ret = meta.invoke(request);
            writeResponse(e.getChannel(), ret != null ? ret : "done");
        }
    }

    private void writeResponse(Channel channel, Object obj) {

        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        response.setStatus(HttpResponseStatus.OK);

        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/json");

        String resMsg = JsonUtils.toJsonString(obj);
        response.setContent(ChannelBuffers.wrappedBuffer(resMsg.getBytes()));
        final ChannelFuture future = channel.write(response);
        future.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        logger.info("channel closed:" + e.getChannel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        logger.error("rest error", e.getCause());
        writeResponse(e.getChannel(), "ERROR: " + e.getCause().getMessage());
    }

}