package com.appsdeveloperblog.webclient.demo.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.appsdeveloperblog.webclient.demo.presentation.model.AlbumRest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AlbumsClientServiceImpl implements AlbumsClientService {
    private final WebClient webClient;

    public AlbumsClientServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Flux<AlbumRest> getAlbums(String jwt) {
        return webClient.get()
                .uri("/albums")
                .header("Authorization",jwt)
                .retrieve()
                .bodyToFlux(AlbumRest.class);
    }

    @Override
    public Mono<AlbumRest> getAlbum(UUID id, String jwt) {
        return webClient.get()
                .uri("/albums/{id}", id)
                .header("Authorization",jwt)
                .retrieve()
                .bodyToMono(AlbumRest.class);
    }

    @Override
    public Mono<AlbumRest> createAlbum(Mono<AlbumRest> album, String jwt) {
        return webClient.post()
                .uri("/albums")
                .body(album, AlbumRest.class)
                .header("Authorization",jwt)
                .retrieve()
                .bodyToMono(AlbumRest.class);
    }

    @Override
    public Mono<AlbumRest> updateAlbum(UUID id, Mono<AlbumRest> album, String jwt) {
        return album.flatMap(albumRest -> {
            albumRest.setId(id);
            return webClient.put()
                    .uri("/albums/{id}", id)
                    .body(Mono.just(albumRest), AlbumRest.class)
                    .header("Authorization",jwt)
                    .retrieve()
                    .bodyToMono(AlbumRest.class);
        });
    }

    @Override
    public Mono<Void> deleteAlbum(UUID id, String jwt) {
        return webClient.delete()
                .uri("/albums/{id}", id)
                .header("Authorization",jwt)
                .retrieve()
                .bodyToMono(Void.class);
    }
}



