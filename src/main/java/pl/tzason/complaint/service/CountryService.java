package pl.tzason.complaint.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CountryService {

    private final RestTemplate restTemplate;

    @Value("${country.api.url}")
    private String countryApiUrl;

    @Cacheable(value = "countries", key = "#ipAddress")
    @CircuitBreaker(name = "countryService", fallbackMethod = "getDefaultCountry")
    @Retry(name = "countryService")
    public String getCountryByIp(String ipAddress) {
        log.info("Fetching country for IP: {}", ipAddress);
        String url = countryApiUrl + "/" + ipAddress;

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            String country = (String) response.getBody().get("country");
            log.info("Country found for IP {}: {}", ipAddress, country);
            return country;
        }

        log.warn("Could not determine country for IP: {}", ipAddress);
        return getDefaultCountry(ipAddress, new RuntimeException("Failed to get country"));
    }

    public String getDefaultCountry(String ipAddress, Throwable throwable) {
        log.warn("Using fallback for country determination. IP: {}, Error: {}", ipAddress, throwable.getMessage());
        return "UNKNOWN";
    }
}