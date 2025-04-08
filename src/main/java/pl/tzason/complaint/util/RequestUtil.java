package pl.tzason.complaint.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestUtil {

    private static final String[] IP_HEADERS = {
            "X-Forwarded-For",
            "REMOTE_ADDR",
            "X-Real-IP"
    };

    public static String getClientIp(HttpServletRequest request) {
        String ip = null;

        for (String header : IP_HEADERS) {
            ip = request.getHeader(header);
            if (StringUtils.hasText(ip) && !ip.equalsIgnoreCase("unknown")) {
                // if multiple address, get first
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                System.out.println("Found IP in header " + header);
                break;
            }
        }

        // When IP not found in headers
        if (!StringUtils.hasText(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }
}