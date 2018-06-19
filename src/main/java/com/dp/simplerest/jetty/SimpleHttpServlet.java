package com.dp.simplerest.jetty;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.dp.simplerest.binding.BindingMeta;
import com.dp.simplerest.exception.RestException;
import com.dp.simplerest.util.JsonUtils;

public class SimpleHttpServlet extends HttpServlet {

    /**
	 * 
	 */
    private static final long serialVersionUID = 8969384425966859160L;

    private Logger logger = Logger.getLogger(getClass());

    private final BindingMeta meta;

    public SimpleHttpServlet(BindingMeta meta) {
        this.meta = meta;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handle0(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        handle0(request, response);
    }

    private void handle0(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();
        logger.info("Rest Request:" + uri);

        if (!uri.contains(".")) { // a simple pattern filter
            Object ret;
            try {
                ret = meta.invoke(request);
            } catch (RestException e) {
                throw new ServletException(e);
            }
            response.setCharacterEncoding("utf-8");
            response.getWriter().write(JsonUtils.toJsonString(ret));
        }
    }
}
