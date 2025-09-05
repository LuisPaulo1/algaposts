package com.algaposts.post.api.controller;

import com.algaposts.post.api.dto.PostInput;
import com.algaposts.post.api.dto.PostOutput;
import com.algaposts.post.api.dto.PostSummaryOutput;
import com.algaposts.post.domain.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    private PostInput postInput;
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
    void createPost_DeveRetornarPostCriadoComSucesso() throws Exception {

        when(postService.processPost(any(PostInput.class))).thenReturn(postOutput);

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postInput)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(postId.toString()))
                .andExpect(jsonPath("$.title").value("Título do Post"))
                .andExpect(jsonPath("$.body").value("Conteúdo do post"))
                .andExpect(jsonPath("$.author").value("Autor"))
                .andExpect(jsonPath("$.wordCount").value(3))
                .andExpect(jsonPath("$.calculatedValue").value(10.50));
    }

    @Test
    void findPostById_DeveRetornarPostExistente() throws Exception {

        when(postService.findPostById(postId)).thenReturn(postOutput);

        mockMvc.perform(get("/api/posts/{id}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postId.toString()))
                .andExpect(jsonPath("$.title").value("Título do Post"))
                .andExpect(jsonPath("$.body").value("Conteúdo do post"))
                .andExpect(jsonPath("$.author").value("Autor"))
                .andExpect(jsonPath("$.wordCount").value(3))
                .andExpect(jsonPath("$.calculatedValue").value(10.50));
    }

    @Test
    void findPostById_DeveRetornar404QuandoPostNaoExistir() throws Exception {

        when(postService.findPostById(postId))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/posts/{id}", postId))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllPosts_DeveRetornarPaginaComParametrosPadrao() throws Exception {

        PostSummaryOutput postSummaryOutput = PostSummaryOutput.builder()
                .id(postId)
                .title("Título do Post")
                .summary("Conteúdo do post")
                .author("Autor")
                .build();

        Page<PostSummaryOutput> page = new PageImpl<>(
                List.of(postSummaryOutput),
                PageRequest.of(0, 10),
                1
        );

        when(postService.findAllPosts(0, 10)).thenReturn(page);

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(postId.toString()))
                .andExpect(jsonPath("$.content[0].title").value("Título do Post"))
                .andExpect(jsonPath("$.content[0].author").value("Autor"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    void findAllPosts_DeveRetornarPaginaComParametrosCustomizados() throws Exception {

        PostSummaryOutput postSummaryOutput = PostSummaryOutput.builder()
                .id(postId)
                .title("Título do Post")
                .summary("Conteúdo do post")
                .author("Autor")
                .build();

        Page<PostSummaryOutput> page = new PageImpl<>(
                List.of(postSummaryOutput),
                PageRequest.of(1, 5),
                10
        );

        when(postService.findAllPosts(1, 5)).thenReturn(page);

        mockMvc.perform(get("/api/posts")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(postId.toString()))
                .andExpect(jsonPath("$.totalElements").value(10))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.number").value(1));
    }

    @Test
    void findAllPosts_DeveRetornarPaginaVazia() throws Exception {

        Page<PostSummaryOutput> emptyPage = new PageImpl<>(
                List.of(),
                PageRequest.of(0, 10),
                0
        );

        when(postService.findAllPosts(0, 10)).thenReturn(emptyPage);

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void createPost_DeveRetornar400QuandoDadosInvalidos() throws Exception {

        PostInput postInputInvalido = PostInput.builder()
                .title("") // título vazio - inválido
                .body("Conteúdo do post")
                .author("Autor")
                .build();

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postInputInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPost_DeveRetornar400QuandoBodyVazio() throws Exception {

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPost_DeveRetornar400QuandoContentTypeInvalido() throws Exception {

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("texto simples"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void findPostById_DeveRetornar400QuandoIdInvalido() throws Exception {

        mockMvc.perform(get("/api/posts/{id}", "id-invalido"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllPosts_DeveUsarParametrosPaginacaoCustomizados() throws Exception {

        Page<PostSummaryOutput> page = new PageImpl<>(
                List.of(),
                PageRequest.of(2, 20),
                0
        );

        when(postService.findAllPosts(2, 20)).thenReturn(page);

        mockMvc.perform(get("/api/posts")
                        .param("page", "2")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(2))
                .andExpect(jsonPath("$.size").value(20));
    }
}
