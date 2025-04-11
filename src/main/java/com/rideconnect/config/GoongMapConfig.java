package com.rideconnect.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@Getter
public class GoongMapConfig {

    @Value("${goong.api.key}")
    private String apiKey;

    @Value("${goong.api.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Get directions from origin to destination
     *
     * @param originLat origin latitude
     * @param originLng origin longitude
     * @param destLat destination latitude
     * @param destLng destination longitude
     * @return response from Goong API
     */
    public String getDirections(double originLat, double originLng, double destLat, double destLng) {
        String url = String.format("%s/Direction?origin=%f,%f&destination=%f,%f&vehicle=car&api_key=%s",
                baseUrl, originLat, originLng, destLat, destLng, apiKey);

        return restTemplate.getForObject(url, String.class);
    }

    /**
     * Geocode an address to coordinates
     *
     * @param address address to geocode
     * @return response from Goong API
     */
    public String geocode(String address) {
        String url = String.format("%s/Geocode?address=%s&api_key=%s",
                baseUrl, address, apiKey);

        return restTemplate.getForObject(url, String.class);
    }

    /**
     * Reverse geocode coordinates to address
     *
     * @param lat latitude
     * @param lng longitude
     * @return response from Goong API
     */
    public String reverseGeocode(double lat, double lng) {
        String url = String.format("%s/Geocode?latlng=%f,%f&api_key=%s",
                baseUrl, lat, lng, apiKey);

        return restTemplate.getForObject(url, String.class);
    }
}
