package snmaddula.components.logger;

import static org.springframework.util.StringUtils.hasText;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StopWatch;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author snmaddula
 *
 */
@Slf4j
@Setter
@ConfigurationProperties("app.log")
@SuppressWarnings({ "unchecked", "rawtypes" })
class AppLoggingConfig {

	private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
	private static final Object ANY_PATH = "/*";
	private Set<String> exludedPaths;

	@Bean
	public FilterRegistrationBean<? extends Filter> executionTimeLogger() {
		return new FilterRegistrationBean() {
			{
				setUrlPatterns(Collections.singleton(ANY_PATH));
				setOrder(OrderedFilter.REQUEST_WRAPPER_FILTER_MAX_ORDER);
				setFilter(new OncePerRequestFilter() {

					@Override
					protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
							FilterChain filterChain) throws ServletException, IOException {
						final StopWatch watch = new StopWatch();
						try {
							watch.start();
							filterChain.doFilter(request, response);
							watch.stop();
						} finally {
							log.info("Request {} completed in {} ms", getUrlWithMethodAndQuery(request),
									watch.getTotalTimeMillis());
						}
					}

					@Override
					protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
						return exludedPaths.stream().anyMatch(p -> PATH_MATCHER.match(p, request.getServletPath()));
					}
				});
			}
		};
	}

	private String getUrlWithMethodAndQuery(HttpServletRequest req) {
		return req.getMethod() + " : " + req.getRequestURI()
				+ (hasText(req.getQueryString()) ? "?" + req.getQueryString() : "");
	}

}
