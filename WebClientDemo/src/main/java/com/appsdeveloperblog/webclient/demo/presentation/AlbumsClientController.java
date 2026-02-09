package com.appsdeveloperblog.webclient.demo.presentation;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appsdeveloperblog.webclient.demo.presentation.model.AlbumRest;
import com.appsdeveloperblog.webclient.demo.service.AlbumsClientServiceImpl;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/albums-client/albums")
public class AlbumsClientController {
    private final AlbumsClientServiceImpl albumsClientService;

    public AlbumsClientController(AlbumsClientServiceImpl albumsClientService) {
        this.albumsClientService = albumsClientService;
    }

    @GetMapping
    public Flux<AlbumRest> getAlbums(@RequestHeader(name = "Authorization") String jwt) {
        return albumsClientService.getAlbums(jwt);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<AlbumRest>> getAlbum(@PathVariable UUID id,
            @RequestHeader(name = "Authorization") String jwt) {
        return albumsClientService.getAlbum(id, jwt)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<AlbumRest>> createAlbum(@Valid @RequestBody Mono<AlbumRest> album,
            @RequestHeader(name = "Authorization") String jwt) {
        return albumsClientService.createAlbum(album, jwt)
                .map(albumRest -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .location(URI.create("/albums-client/" + albumRest.getId()))
                        .body(albumRest));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<AlbumRest>> updateAlbum(@PathVariable UUID id,
            @Valid @RequestBody Mono<AlbumRest> album,
            @RequestHeader(name = "Authorization") String jwt) {
        return albumsClientService.updateAlbum(id, album, jwt)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAlbum(@PathVariable UUID id,
            @RequestHeader(name = "Authorization") String jwt) {
        return albumsClientService.deleteAlbum(id, jwt)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}