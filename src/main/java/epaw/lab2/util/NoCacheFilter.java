package epaw.lab2.util;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Prevents the browser from caching any servlet/JSP response.
 * Useful during development with Jetty hot-reload so the browser
 * always fetches fresh HTML instead of serving stale cached pages.
 */
@WebFilter("/*")
public class NoCacheFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse httpResp = (HttpServletResponse) response;
        httpResp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        httpResp.setHeader("Pragma",        "no-cache");
        httpResp.setDateHeader("Expires",   0);

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() { }
}