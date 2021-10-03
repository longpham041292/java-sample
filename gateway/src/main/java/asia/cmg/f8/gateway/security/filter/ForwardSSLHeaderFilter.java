package asia.cmg.f8.gateway.security.filter;

import asia.cmg.f8.gateway.config.GatewayProperties;
import asia.cmg.f8.gateway.security.utils.ForwardedHeader;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

/**
 * Created on 12/1/16.
 */
@Component
public class ForwardSSLHeaderFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(ForwardSSLHeaderFilter.class);

    @Autowired
    private GatewayProperties gatewayProperties;
    
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        //Init
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        LOG.debug("ForwardSSLHeaderFilter processing ...");
        final HttpServletRequest req = (HttpServletRequest) request;
        final CustomHttpServletRequest customRequest = new CustomHttpServletRequest(req);

        final String forwardHeader = req.getHeader("Forwarded");
        if (StringUtils.isNotEmpty(forwardHeader)) {
            final ForwardedHeader forward = ForwardedHeader.from(forwardHeader);
            LOG.debug("Forward header {}", forwardHeader);
            forward.setProto(gatewayProperties.getProtocol());
            customRequest.putHeader("Forwarded", forward.toString());
        }

        chain.doFilter(customRequest, response);

    }

    @Override
    public void destroy() {
        //Destroy
    }
}
