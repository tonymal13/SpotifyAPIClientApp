package ru.mal.SpotifyClient.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.mal.SpotifyClient.dto.AlbumDTO;
import ru.mal.SpotifyClient.entity.Artist;

import java.util.List;
import java.util.stream.Collectors;

import static ru.mal.SpotifyClient.client.SpotifyAPIClient.getAlbumsByTitle;

@RestController
public class AlbumRestController {
    @GetMapping("/api/albums")
    public Mono<List<AlbumDTO>> getAlbums(@RequestParam("q") String query,@RequestParam("type") String  type) {

        return getAlbumsByTitle(query, type)
                .map(albumResponse -> albumResponse.getAlbum().getItems().stream()
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

