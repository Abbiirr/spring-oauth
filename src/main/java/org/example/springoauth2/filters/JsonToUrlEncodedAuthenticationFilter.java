package org.example.springoauth2.filters;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class JsonToUrlEncodedAuthenticationFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if ("/oauth/token".equals(request.getRequestURI())
                && request.getContentType() != null
                && request.getContentType().contains("application/json")) {

            // Parse JSON payload
            Map<String, Object> jsonMap = objectMapper.readValue(
                    request.getInputStream(), new TypeReference<Map<String, Object>>() {});

            // Convert JSON map to URL-encoded parameters
            StringBuilder urlEncodedParams = new StringBuilder();
            for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
                if (urlEncodedParams.length() > 0) {
                    urlEncodedParams.append("&");
                }
                urlEncodedParams.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.name()))
                        .append("=")
                        .append(URLEncoder.encode(String.valueOf(entry.getValue()), StandardCharsets.UTF_8.name()));
            }

            // Wrap the original request to override getInputStream() and content type
            HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(request) {
                @Override
                public String getContentType() {
                    return "application/x-www-form-urlencoded";
                }

                @Override
                public ServletInputStream getInputStream() throws IOException {
                    final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                            urlEncodedParams.toString().getBytes(StandardCharsets.UTF_8));

                    return new ServletInputStream() {
                        @Override
                        public int read() throws IOException {
                            return byteArrayInputStream.read();
                        }
                        @Override
                        public boolean isFinished() {
                            return byteArrayInputStream.available() == 0;
                        }
                        @Override
                        public boolean isReady() {
                            return true;
                        }
                        @Override
                        public void setReadListener(ReadListener readListener) {
                            // No-op for this example
                        }
                    };
                }

                @Override
                public int getContentLength() {
                    return urlEncodedParams.toString().getBytes(StandardCharsets.UTF_8).length;
                }
            };

            filterChain.doFilter(wrappedRequest, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
