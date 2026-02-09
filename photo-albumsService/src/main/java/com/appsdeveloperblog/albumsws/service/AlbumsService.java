package com.appsdeveloperblog.albumsws.service;

import java.util.UUID;

import com.appsdeveloperblog.albumsws.presentation.AlbumRest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AlbumsService {
    Flux<AlbumRest> getAlbums(String userId);
	Mono<AlbumRest> createAlbum(Mono<AlbumRest> album);
    Mono<AlbumRest> getAlbum(UUID id, String userId);
    Mono<AlbumRest> updateAlbum(Mono<AlbumRest> album);
    Mono<Void> deleteAlbum(UUID id, String userId);
}
