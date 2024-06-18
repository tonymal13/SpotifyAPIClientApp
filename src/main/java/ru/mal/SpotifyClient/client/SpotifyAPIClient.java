package ru.mal.SpotifyClient.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.mal.SpotifyClient.entity.AlbumResponseFromSpotifyApi;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.util.Base64;

@RequiredArgsConstructor
public class SpotifyAPIClient {

    private static final String client_id="your_spotify_client_id";
    private static final String client_secret="your_spotify_client_secret";

    public static Mono<String> getToken() {
        System.out.println(client_id);
        return webClient.post()
                .uri("https://accounts.spotify.com/api/token")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString((client_id + ":" + client_secret).getBytes()))
                .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
                .retrieve()
                .bodyToMono(String.class);
    }

    private static final WebClient webClient = WebClient.create();

    public static Mono<AlbumResponseFromSpotifyApi> getAlbumsByTitle(String query, String type) {
        String accessTokenPayloadString = getToken().block();
        JsonReader jsonReader = Json.createReader(new StringReader(accessTokenPayloadString));
        JsonObject tokenPayloadJson = jsonReader.readObject();
        jsonReader.close();

        String accessToken = tokenPayloadJson.getString("access_token");

        return webClient.get()
                .uri(String.format( "https://api.spotify.com/v1/search?q=%s&type=%s",query,type))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        return objectMapper.readValue(response, AlbumResponseFromSpotifyApi.class);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to parse response", e);
                    }
                });
    }

}
