package com.algaposts.post.mapper;

import com.algaposts.post.api.dto.PostInput;
import com.algaposts.post.api.dto.PostOutput;
import com.algaposts.post.api.dto.PostSummaryOutput;
import com.algaposts.post.domain.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PostMapperTest {

    private PostMapper postMapper;
    private PostInput postInput;
    private Post post;
    private UUID postId;

    @BeforeEach
    void setUp() {
        postMapper = new PostMapper();
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
    }

    @Test
    void toEntity_DeveConverterPostInputParaPost() {

        Post result = postMapper.toEntity(postInput);

        assertNotNull(result);
        assertNotNull(result.getId()); // UUID gerado automaticamente
        assertEquals(postInput.getTitle(), result.getTitle());
        assertEquals(postInput.getBody(), result.getBody());
        assertEquals(postInput.getAuthor(), result.getAuthor());
        assertNull(result.getWordCount()); // Ainda não processado
        assertNull(result.getCalculatedValue()); // Ainda não processado
    }

    @Test
    void toOutput_DeveConverterPostParaPostOutput() {

        PostOutput result = postMapper.toOutput(post);

        assertNotNull(result);
        assertEquals(post.getId(), result.getId());
        assertEquals(post.getTitle(), result.getTitle());
        assertEquals(post.getBody(), result.getBody());
        assertEquals(post.getAuthor(), result.getAuthor());
        assertEquals(post.getWordCount(), result.getWordCount());
        assertEquals(post.getCalculatedValue(), result.getCalculatedValue());
    }

    @Test
    void toSummaryOutput_DeveConverterPostParaPostSummaryOutput() {

        PostSummaryOutput result = postMapper.toSummaryOutput(post);

        assertNotNull(result);
        assertEquals(post.getId(), result.getId());
        assertEquals(post.getTitle(), result.getTitle());
        assertEquals(post.getBody(), result.getSummary()); // Body curto mantém conteúdo completo
        assertEquals(post.getAuthor(), result.getAuthor());
    }

    @Test
    void toSummaryOutput_DeveTruncarTextoLongo() {

        String longBody = "a".repeat(400); // Texto com 400 caracteres

        Post postWithLongBody = Post.builder()
                .id(postId)
                .title("Título")
                .body(longBody)
                .author("Autor")
                .build();

        PostSummaryOutput result = postMapper.toSummaryOutput(postWithLongBody);

        assertNotNull(result);
        assertTrue(result.getSummary().length() <= 353); // 350 chars + "..."
        assertTrue(result.getSummary().endsWith("..."));
        assertTrue(result.getSummary().length() < longBody.length());
    }

    @Test
    void toSummaryOutput_DeveTruncarNoUltimoEspaco() {
        // Texto com mais de 350 caracteres para garantir truncamento
        String bodyWithSpaces = "Este é um texto muito longo que precisa ser truncado no último " +
                "espaço encontrado antes do limite de caracteres para manter a integridade das palavras e " +
                "não cortar palavras no meio do seu conteúdo textual que pode ser muito extenso e detalhado " +
                "para caber no resumo com mais de trezentos e cinquenta caracteres de conteúdo e ainda mais " +
                "texto adicional para garantir que ultrapasse o limite estabelecido pelo algoritmo de truncamento" +
                " do PostMapper";

        Post postWithSpaces = Post.builder()
                .id(postId)
                .title("Título")
                .body(bodyWithSpaces)
                .author("Autor")
                .build();

        PostSummaryOutput result = postMapper.toSummaryOutput(postWithSpaces);

        assertNotNull(result);
        assertTrue(result.getSummary().endsWith("..."));
        assertTrue(result.getSummary().length() < bodyWithSpaces.length());
        String summaryWithoutEllipsis = result.getSummary().substring(0, result.getSummary().length() - 3);
        assertFalse(summaryWithoutEllipsis.endsWith(" "));
    }

    @Test
    void toSummaryOutput_DeveRetornarStringVaziaParaBodyNulo() {

        Post postWithNullBody = Post.builder()
                .id(postId)
                .title("Título")
                .body(null)
                .author("Autor")
                .build();

        PostSummaryOutput result = postMapper.toSummaryOutput(postWithNullBody);

        assertNotNull(result);
        assertEquals("", result.getSummary());
        assertEquals(postId, result.getId());
        assertEquals("Título", result.getTitle());
        assertEquals("Autor", result.getAuthor());
    }

    @Test
    void toSummaryOutput_DeveRetornarStringVaziaParaBodyVazio() {

        Post postWithEmptyBody = Post.builder()
                .id(postId)
                .title("Título")
                .body("")
                .author("Autor")
                .build();

        PostSummaryOutput result = postMapper.toSummaryOutput(postWithEmptyBody);

        assertNotNull(result);
        assertEquals("", result.getSummary());
    }

    @Test
    void toSummaryOutput_DeveRetornarStringVaziaParaBodyComApenasEspacos() {

        Post postWithWhitespaceBody = Post.builder()
                .id(postId)
                .title("Título")
                .body("   ")
                .author("Autor")
                .build();

        PostSummaryOutput result = postMapper.toSummaryOutput(postWithWhitespaceBody);

        assertNotNull(result);
        assertEquals("", result.getSummary());
    }

    @Test
    void toSummaryOutput_DeveManterTextoExatamenteCom350Caracteres() {

        String exactly350Chars = "a".repeat(350);

        Post postWith350Chars = Post.builder()
                .id(postId)
                .title("Título")
                .body(exactly350Chars)
                .author("Autor")
                .build();

        PostSummaryOutput result = postMapper.toSummaryOutput(postWith350Chars);

        assertNotNull(result);
        assertEquals(exactly350Chars, result.getSummary());
        assertFalse(result.getSummary().endsWith("..."));
    }

    @Test
    void toSummaryOutput_DeveTruncarTextoComExatamente351Caracteres() {

        String exactly351Chars = "a".repeat(351);

        Post postWith351Chars = Post.builder()
                .id(postId)
                .title("Título")
                .body(exactly351Chars)
                .author("Autor")
                .build();

        PostSummaryOutput result = postMapper.toSummaryOutput(postWith351Chars);

        assertNotNull(result);
        assertTrue(result.getSummary().endsWith("..."));
        assertEquals(353, result.getSummary().length());
        String expectedContent = "a".repeat(350) + "...";
        assertEquals(expectedContent, result.getSummary());
        assertTrue(result.getSummary().startsWith("a".repeat(350)));
    }

    @Test
    void toSummaryOutput_DeveTruncarSemEspacoQuandoNaoHaEspacoDisponivel() {

        String longTextWithoutSpaces = "a".repeat(400);

        Post postWithoutSpaces = Post.builder()
                .id(postId)
                .title("Título")
                .body(longTextWithoutSpaces)
                .author("Autor")
                .build();

        PostSummaryOutput result = postMapper.toSummaryOutput(postWithoutSpaces);

        assertNotNull(result);
        assertTrue(result.getSummary().endsWith("..."));
        assertEquals(353, result.getSummary().length());
        String expectedPrefix = longTextWithoutSpaces.substring(0, 350);
        assertEquals(expectedPrefix + "...", result.getSummary());
    }
}
