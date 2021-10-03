package asia.cmg.f8.gateway.security.filter;

import asia.cmg.f8.gateway.config.GatewayProperties;
import asia.cmg.f8.gateway.security.utils.ForwardedHeader;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.constants.ZuulHeaders;
import com.netflix.zuul.context.RequestContext;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

/**
 * Created on 10/14/16.
 */
@Component
public class FreForwardSSLHeadersZuulFilter extends ZuulFilter {

	private static final Logger LOG = LoggerFactory.getLogger(FreForwardSSLHeadersZuulFilter.class);

	@Autowired
	private GatewayProperties gatewayProperties;

	@Override
	public String filterType() {
		return "pre";
	}

	@Override
	public int filterOrder() {
		return 6;
	}

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() {
		final RequestContext ctx = RequestContext.getCurrentContext();
		LOG.debug(String.format("Before filter ['%s': '%s', '%s': '%s']",
				ZuulHeaders.X_FORWARDED_PROTO.toLowerCase(Locale.getDefault()),
				ctx.getZuulRequestHeaders().get(ZuulHeaders.X_FORWARDED_PROTO.toLowerCase(Locale.getDefault())),
				"X-Forwarded-Port", ctx.getZuulRequestHeaders().get("x-forwarded-port")));

		ctx.addZuulRequestHeader(ZuulHeaders.X_FORWARDED_PROTO.toLowerCase(Locale.getDefault()), "https");
		if (gatewayProperties.getProtocol().equals("https")) {
			ctx.addZuulRequestHeader("X-Forwarded-Port", "443");
		}

		final String forwardHeader = ctx.getRequest().getHeader("Forwarded");
		if (StringUtils.isNotEmpty(forwardHeader)) {
			final ForwardedHeader forward = ForwardedHeader.from(forwardHeader);
			LOG.info("Forward header {}", forwardHeader);
			LOG.info("Set Protocol to {}", gatewayProperties.getProtocol());
			forward.setProto(gatewayProperties.getProtocol());
			ctx.addZuulRequestHeader("Forwarded", forward.toString());
		}

		LOG.debug(String.format("After filter ['%s': '%s']",
				ZuulHeaders.X_FORWARDED_PROTO.toLowerCase(Locale.getDefault()),
				ctx.getZuulRequestHeaders().get(ZuulHeaders.X_FORWARDED_PROTO.toLowerCase(Locale.getDefault()))));

		return null;
	}
}