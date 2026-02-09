package com.appsdeveloperblog.webclient.demo.service;

import java.util.UUID;

import com.appsdeveloperblog.webclient.demo.presentation.model.AlbumRest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AlbumsClientService {
    Flux<AlbumRest> getAlbums(String jwt);
    Mono<AlbumRest> getAlbum(UUID id, String jwt);
    Mono<AlbumRest> createAlbum(Mono<AlbumRest> albumm, String jwt);
    Mono<AlbumRest> updateAlbum(UUID id, Mono<AlbumRest> album, String jwt);
    Mono<Void> deleteAlbum(UUID id, String jwt);
}
