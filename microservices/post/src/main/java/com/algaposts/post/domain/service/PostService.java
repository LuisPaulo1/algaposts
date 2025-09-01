package com.algaposts.post.domain.service;

import com.algaposts.post.api.dto.PostInput;
import com.algaposts.post.api.dto.PostSummaryOutput;
import com.algaposts.post.mapper.PostMapper;
import com.algaposts.post.api.dto.PostOutput;
import com.algaposts.post.api.dto.TextProcessorData;
import com.algaposts.post.domain.model.Post;
import com.algaposts.post.domain.repository.PostRepository;
import com.algaposts.post.infrastructure.rabbitmq.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final EventPublisher eventPublisher;
    private final PostMapper postMapper;

    @Transactional
    public PostOutput processPost(PostInput postInput) {
        log.info("Criando post: {}", postInput);
        var post = postMapper.toEntity(postInput);
        post = save(post);
        eventPublisher.publishPostCreated(post);
        return postMapper.toOutput(post);
    }

    public Post save(Post post) {
        log.info("Salvando post: {}", post);
        return postRepository.save(post);
    }

    @Transactional
    public void updatePostWithProcessedData(TextProcessorData textProcessorData) {
        log.info("Atualizando post com dados processados: {}", textProcessorData);
        var post = postRepository.findById(textProcessorData.getPostId());
        post.ifPresent(p -> {
            p.setWordCount(textProcessorData.getWordCount());
            p.setCalculatedValue(textProcessorData.getCalculatedValue());
            save(p);
        });
    }

    public PostOutput findPostById(UUID id) {
        log.info("Buscando post pelo id: {}", id);
        var post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return postMapper.toOutput(post);
    }

    public Page<PostSummaryOutput> findAllPosts(int page, int size) {
        log.info("Buscando todos os posts - página: {}, tamanho: {}", page, size);
        var pageable = PageRequest.of(page, size);
        Page<Post> postsPage = postRepository.findAll(pageable);
        return postsPage.map(postMapper::toSummaryOutput);
    }
}
