package com.algaposts.post.api.controller;

import com.algaposts.post.api.dto.PostInput;
import com.algaposts.post.api.dto.PostOutput;
import com.algaposts.post.api.dto.PostSummaryOutput;
import com.algaposts.post.domain.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("v1/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostOutput> createPost(@RequestBody PostInput postInput) {
        var postOutput = postService.processPost(postInput);
        return ResponseEntity.status(HttpStatus.CREATED).body(postOutput);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostOutput> findPostById(@PathVariable("id") UUID id) {
        var post = postService.findPostById(id);
        return ResponseEntity.ok(post);
    }

    @GetMapping
    public ResponseEntity<Page<PostSummaryOutput>> findAllPosts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        var posts = postService.findAllPosts(page, size);
        return ResponseEntity.ok(posts);
    }
}
