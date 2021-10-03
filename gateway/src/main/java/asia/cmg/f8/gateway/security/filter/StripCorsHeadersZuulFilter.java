package asia.cmg.f8.gateway.security.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.http.HttpServletResponseWrapper;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * Created by tri.bui on 10/14/16.
 */
@Component
public class StripCorsHeadersZuulFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        final RequestContext context = RequestContext.getCurrentContext();
        final HttpServletResponse wrapper = new HttpServletResponseWrapper(context.getResponse()) {
            @Override
            public void addHeader(final String name, final String value) {
                if (name.toLowerCase(Locale.getDefault()).startsWith("access-control-allow")) {
                    return;
                }
                super.addHeader(name, value);
            }
        };
        context.setResponse(wrapper);
        return null;
    }
}