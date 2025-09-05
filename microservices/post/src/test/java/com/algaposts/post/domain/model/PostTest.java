package com.algaposts.post.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PostTest {

    @Test
    void deveInicializarPostComBuilder() {

        UUID id = UUID.randomUUID();
        String title = "Título do Post";
        String body = "Conteúdo do post";
        String author = "Autor";
        Integer wordCount = 5;
        BigDecimal calculatedValue = BigDecimal.valueOf(25.75);

        Post post = Post.builder()
                .id(id)
                .title(title)
                .body(body)
                .author(author)
                .wordCount(wordCount)
                .calculatedValue(calculatedValue)
                .build();

        assertNotNull(post);
        assertEquals(id, post.getId());
        assertEquals(title, post.getTitle());
        assertEquals(body, post.getBody());
        assertEquals(author, post.getAuthor());
        assertEquals(wordCount, post.getWordCount());
        assertEquals(calculatedValue, post.getCalculatedValue());
    }

    @Test
    void deveInicializarPostComConstrutorVazio() {

        Post post = new Post();

        assertNotNull(post);
        assertNull(post.getId());
        assertNull(post.getTitle());
        assertNull(post.getBody());
        assertNull(post.getAuthor());
        assertNull(post.getWordCount());
        assertNull(post.getCalculatedValue());
    }

    @Test
    void deveInicializarPostComConstrutorCompleto() {

        UUID id = UUID.randomUUID();
        String title = "Título do Post";
        String body = "Conteúdo do post";
        String author = "Autor";
        Integer wordCount = 5;
        BigDecimal calculatedValue = BigDecimal.valueOf(25.75);

        Post post = new Post(id, title, body, author, wordCount, calculatedValue);

        assertNotNull(post);
        assertEquals(id, post.getId());
        assertEquals(title, post.getTitle());
        assertEquals(body, post.getBody());
        assertEquals(author, post.getAuthor());
        assertEquals(wordCount, post.getWordCount());
        assertEquals(calculatedValue, post.getCalculatedValue());
    }

    @Test
    void devePermitirModificacaoDePropriedades() {

        Post post = new Post();
        UUID id = UUID.randomUUID();
        String title = "Novo Título";
        String body = "Novo Conteúdo";
        String author = "Novo Autor";
        Integer wordCount = 10;
        BigDecimal calculatedValue = BigDecimal.valueOf(50.00);

        post.setId(id);
        post.setTitle(title);
        post.setBody(body);
        post.setAuthor(author);
        post.setWordCount(wordCount);
        post.setCalculatedValue(calculatedValue);

        assertEquals(id, post.getId());
        assertEquals(title, post.getTitle());
        assertEquals(body, post.getBody());
        assertEquals(author, post.getAuthor());
        assertEquals(wordCount, post.getWordCount());
        assertEquals(calculatedValue, post.getCalculatedValue());
    }

    @Test
    void equals_DeveRetornarTrueParaPostsComMesmoId() {

        UUID id = UUID.randomUUID();
        Post post1 = Post.builder()
                .id(id)
                .title("Título 1")
                .build();

        Post post2 = Post.builder()
                .id(id)
                .title("Título 2")
                .build();

        assertEquals(post1, post2);
        assertEquals(post1.hashCode(), post2.hashCode());
    }

    @Test
    void equals_DeveRetornarFalseParaPostsComIdsDiferentes() {

        Post post1 = Post.builder()
                .id(UUID.randomUUID())
                .title("Título")
                .build();

        Post post2 = Post.builder()
                .id(UUID.randomUUID())
                .title("Título")
                .build();

        assertNotEquals(post1, post2);
    }

    @Test
    void equals_DeveRetornarFalseParaNull() {

        Post post = Post.builder()
                .id(UUID.randomUUID())
                .build();

        assertNotEquals(null, post);
    }

    @Test
    void toString_DeveConterInformacoesDoPost() {
        // Given
        UUID id = UUID.randomUUID();
        Post post = Post.builder()
                .id(id)
                .title("Título")
                .author("Autor")
                .build();

        String toString = post.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("id=" + id));
        assertTrue(toString.contains("title=Título"));
        assertTrue(toString.contains("author=Autor"));
    }

    @Test
    void hashCode_DeveSerConsistenteComEquals() {

        UUID id = UUID.randomUUID();
        Post post1 = Post.builder().id(id).build();
        Post post2 = Post.builder().id(id).build();

        assertEquals(post1, post2);
        assertEquals(post1.hashCode(), post2.hashCode());
    }
}
