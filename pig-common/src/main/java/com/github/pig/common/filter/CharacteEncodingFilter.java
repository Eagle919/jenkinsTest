package com.github.pig.common.filter;


import com.github.pig.common.bean.helper.PlaceholderHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;

/**
 * @className CharacteEncodingFilter
 * @Author chenkang
 * @Date 2018/11/29 17:39
 * @Version 1.0
 */
@Component
public class CharacteEncodingFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CharacteEncodingFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        PlaceholderHelper.doNothing(() ->
            LOGGER.info(" {}  init ",this.getClass().getName())
        );

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain filterChain) throws IOException, ServletException {
        if (request.getCharacterEncoding() == null) {
            request.setCharacterEncoding("UTF-8");
        }
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        PlaceholderHelper.doNothing(() ->
            LOGGER.info(" {}  destroy ",this.getClass().getName())
        );
    }

}
