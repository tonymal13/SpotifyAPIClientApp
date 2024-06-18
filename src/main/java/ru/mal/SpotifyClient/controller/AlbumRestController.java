package ru.mal.SpotifyClient.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.mal.SpotifyClient.client.SpotifyAPIClient;
import ru.mal.SpotifyClient.dto.AlbumDTO;
import ru.mal.SpotifyClient.entity.Artist;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1")
public class AlbumRestController {

    private final SpotifyAPIClient spotifyAPIClient;

    public AlbumRestController(SpotifyAPIClient spotifyAPIClient) {
        this.spotifyAPIClient = spotifyAPIClient;
    }

    @GetMapping("/albums")
    public Mono<List<AlbumDTO>> getAlbums(@RequestParam("q") String query){

        return spotifyAPIClient. getAlbumsByTitle(query)
                .map(albumResponse -> albumResponse.getAlbums().getItems().stream()
                        .map(albumItem -> {
                            AlbumDTO dto = new AlbumDTO();
                            dto.setId(albumItem.getId());
                            dto.setName(albumItem.getName());
                            // Извлекаем только имена исполнителей
                            List<String> artistNames = albumItem.getArtists().stream()
                                    .map(Artist::getName) // Получаем имя исполнителя
                                    .collect(Collectors.toList());
                            dto.setArtists(artistNames);
                            dto.setImage(albumItem.getImages().get(2).getUrl());
                            return dto;
                        })
                        .collect(Collectors.toList()));
    }
}

