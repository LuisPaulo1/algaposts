package com.algaposts.post.domain.service;

import com.algaposts.post.api.dto.PostInput;
import com.algaposts.post.api.dto.PostOutput;
import com.algaposts.post.api.dto.PostSummaryOutput;
import com.algaposts.post.api.dto.TextProcessorData;
import com.algaposts.post.domain.model.Post;
import com.algaposts.post.domain.repository.PostRepository;
import com.algaposts.post.infrastructure.rabbitmq.EventPublisher;
import com.algaposts.post.mapper.PostMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostService postService;

    private PostInput postInput;
    private Post post;
    private PostOutput postOutput;
    private UUID postId;

    @BeforeEach
    void setUp() {
        postId = UUID.randomUUID();

        postInput = PostInput.builder()
                .title("Título do Post")
                .body("Conteúdo do post")
                .author("Autor")
                .build();

        post = Post.builder()
                .id(postId)
                .title("Título do Post")
                .body("Conteúdo do post")
                .author("Autor")
                .wordCount(3)
                .calculatedValue(BigDecimal.valueOf(10.50))
                .build();

        postOutput = PostOutput.builder()
                .id(postId)
                .title("Título do Post")
                .body("Conteúdo do post")
                .author("Autor")
                .wordCount(3)
                .calculatedValue(BigDecimal.valueOf(10.50))
                .build();
    }

    @Test
    void processPost_DeveProcessarPostComSucesso() {

        when(postMapper.toEntity(postInput)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.toOutput(post)).thenReturn(postOutput);

        PostOutput result = postService.processPost(postInput);

        assertNotNull(result);
        assertEquals(postOutput.getId(), result.getId());
        assertEquals(postOutput.getTitle(), result.getTitle());
        assertEquals(postOutput.getAuthor(), result.getAuthor());

        verify(postMapper).toEntity(postInput);
        verify(postRepository).save(post);
        verify(eventPublisher).publishPostCreated(post);
        verify(postMapper).toOutput(post);
    }

    @Test
    void save_DeveSalvarPostComSucesso() {

        when(postRepository.save(post)).thenReturn(post);

        Post result = postService.save(post);

        assertNotNull(result);
        assertEquals(post.getId(), result.getId());
        verify(postRepository).save(post);
    }

    @Test
    void updatePostWithProcessedData_DeveAtualizarPostExistente() {

        TextProcessorData textProcessorData = TextProcessorData.builder()
                .postId(postId)
                .wordCount(5)
                .calculatedValue(BigDecimal.valueOf(15.75))
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        postService.updatePostWithProcessedData(textProcessorData);

        verify(postRepository).findById(postId);
        verify(postRepository).save(post);
        assertEquals(5, post.getWordCount());
        assertEquals(BigDecimal.valueOf(15.75), post.getCalculatedValue());
    }

    @Test
    void updatePostWithProcessedData_NaoDeveAtualizarPostInexistente() {

        TextProcessorData textProcessorData = TextProcessorData.builder()
                .postId(postId)
                .wordCount(5)
                .calculatedValue(BigDecimal.valueOf(15.75))
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        postService.updatePostWithProcessedData(textProcessorData);

        verify(postRepository).findById(postId);
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void findPostById_DeveRetornarPostExistente() {

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postMapper.toOutput(post)).thenReturn(postOutput);

        PostOutput result = postService.findPostById(postId);

        assertNotNull(result);
        assertEquals(postOutput.getId(), result.getId());
        verify(postRepository).findById(postId);
        verify(postMapper).toOutput(post);
    }

    @Test
    void findPostById_DeveLancarExcecaoQuandoPostNaoExistir() {

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> postService.findPostById(postId)
        );

        assertEquals(404, exception.getStatusCode().value());
        verify(postRepository).findById(postId);
        verify(postMapper, never()).toOutput(any(Post.class));
    }

    @Test
    void findAllPosts_DeveRetornarPaginaDePostsSummary() {

        int page = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(page, size);

        List<Post> posts = List.of(post);
        Page<Post> postsPage = new PageImpl<>(posts, pageRequest, 1);

        PostSummaryOutput postSummaryOutput = PostSummaryOutput.builder()
                .id(postId)
                .title("Título do Post")
                .summary("Conteúdo do post")
                .author("Autor")
                .build();

        when(postRepository.findAll(pageRequest)).thenReturn(postsPage);
        when(postMapper.toSummaryOutput(post)).thenReturn(postSummaryOutput);

        Page<PostSummaryOutput> result = postService.findAllPosts(page, size);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(postSummaryOutput.getId(), result.getContent().get(0).getId());

        verify(postRepository).findAll(pageRequest);
        verify(postMapper).toSummaryOutput(post);
    }

    @Test
    void findAllPosts_DeveRetornarPaginaVazia() {

        int page = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Post> emptyPage = new PageImpl<>(List.of(), pageRequest, 0);

        when(postRepository.findAll(pageRequest)).thenReturn(emptyPage);

        Page<PostSummaryOutput> result = postService.findAllPosts(page, size);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());

        verify(postRepository).findAll(pageRequest);
        verify(postMapper, never()).toSummaryOutput(any(Post.class));
    }
}
