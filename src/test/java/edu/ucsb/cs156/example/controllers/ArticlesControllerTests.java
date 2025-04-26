package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.Articles;
import edu.ucsb.cs156.example.repositories.ArticlesRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDateTime;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.ArgumentCaptor;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;




@WebMvcTest(controllers = ArticlesController.class)
@Import(TestConfig.class)
public class ArticlesControllerTests extends ControllerTestCase {
        @Autowired
        private ObjectMapper objectMapper;
        
        @MockBean
        ArticlesRepository articlesRepository;

        @MockBean
        UserRepository userRepository;

        // Authorization tests for /api/articles/all

        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/articles/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/articles/all"))
                                .andExpect(status().is(200)); // logged
        }

        // Authorization tests for /api/articles/post

        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/articles/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/articles/post"))
                                .andExpect(status().is(403)); // only admins can post
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_articles() throws Exception {

                // arrange
                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                Articles article1 = Articles.builder()
                                .title("Article 1")
                                .url("https://example.com/1")
                                .explanation("Explanation 1")
                                .email("user1@example.com")
                                .dateAdded(ldt1)
                                .build();

                LocalDateTime ldt2 = LocalDateTime.parse("2022-03-11T00:00:00");

                Articles article2 = Articles.builder()
                                .title("Article 2")
                                .url("https://example.com/2")
                                .explanation("Explanation 2")
                                .email("user2@example.com")
                                .dateAdded(ldt2)
                                .build();

                ArrayList<Articles> expectedArticles = new ArrayList<>();
                expectedArticles.addAll(Arrays.asList(article1, article2));

                when(articlesRepository.findAll()).thenReturn(expectedArticles);

                // act
                MvcResult response = mockMvc.perform(get("/api/articles/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(articlesRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedArticles);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_article() throws Exception {
                // arrange

                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                Articles article1 = Articles.builder()
                                .title("Test Article")
                                .url("https://example.com/test")
                                .explanation("Test Explanation")
                                .email("test@example.com")
                                .dateAdded(ldt1)
                                .build();

                when(articlesRepository.save(any(Articles.class))).thenReturn(article1);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/articles/post?title=Test Article&url=https://example.com/test&explanation=Test Explanation&email=test@example.com&dateAdded=2022-01-03T00:00:00")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(articlesRepository, times(1)).save(any(Articles.class));
                String expectedJson = mapper.writeValueAsString(article1);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

    @WithMockUser(roles = { "ADMIN", "USER" })
@Test
public void test_post_with_specific_values() throws Exception {
    // arrange
    LocalDateTime ldt = LocalDateTime.parse("2022-01-03T00:00:00");
    
    Articles expectedArticle = Articles.builder()
            .title("Specific Title")
            .url("https://specific-url.com")
            .explanation("Specific explanation")
            .email("specific@example.com")
            .dateAdded(ldt)
            .build();
    
    when(articlesRepository.save(any(Articles.class))).thenReturn(expectedArticle);
    
    // act
    MvcResult response = mockMvc.perform(
            post("/api/articles/post?title=Specific Title&url=https://specific-url.com&explanation=Specific explanation&email=specific@example.com&dateAdded=2022-01-03T00:00:00")
                    .with(csrf()))
            .andExpect(status().isOk()).andReturn();
    
    // assert
    String expectedJson = mapper.writeValueAsString(expectedArticle);
    String responseString = response.getResponse().getContentAsString();
    assertEquals(expectedJson, responseString);
}
    
    @WithMockUser(roles = { "ADMIN", "USER" })
@Test
public void test_edge_case_behavior() throws Exception {
    // Testing with URL containing special characters that need encoding
    String specialUrl = "https://example.com/path?param=value&second=true";
    
    LocalDateTime ldt = LocalDateTime.parse("2022-01-03T00:00:00");
    
    Articles article = Articles.builder()
            .title("Special URL Test")
            .url(specialUrl)
            .explanation("Testing URL with query parameters")
            .email("test@example.com")
            .dateAdded(ldt)
            .build();
    
    when(articlesRepository.save(any(Articles.class))).thenReturn(article);
    
    MvcResult response = mockMvc.perform(
            post("/api/articles/post")
                    .param("title", "Special URL Test")
                    .param("url", specialUrl)
                    .param("explanation", "Testing URL with query parameters")
                    .param("email", "test@example.com")
                    .param("dateAdded", "2022-01-03T00:00:00")
                    .with(csrf()))
            .andExpect(status().isOk()).andReturn();
    
    verify(articlesRepository, times(1)).save(any(Articles.class));
}

@WithMockUser(roles = { "ADMIN", "USER" })
@Test
public void test_all_properties_are_set_correctly() throws Exception {
    // Arrange
    LocalDateTime ldt = LocalDateTime.parse("2022-01-03T00:00:00");
    
    // Use ArgumentCaptor to capture the actual object passed to save()
    org.mockito.ArgumentCaptor<Articles> articlesCaptor = org.mockito.ArgumentCaptor.forClass(Articles.class);
    
    // Return the same object that was passed to save
    when(articlesRepository.save(any(Articles.class))).thenAnswer(invocation -> invocation.getArgument(0));
    
    // Act
    mockMvc.perform(
            post("/api/articles/post")
                    .param("title", "Test Title")
                    .param("url", "https://example.com")
                    .param("explanation", "Test Explanation")
                    .param("email", "test@example.com")
                    .param("dateAdded", "2022-01-03T00:00:00")
                    .with(csrf()))
            .andExpect(status().isOk());
    
    // Assert - verify that all properties were set correctly
    verify(articlesRepository).save(articlesCaptor.capture());
    Articles savedArticle = articlesCaptor.getValue();
    
    // These assertions will fail if any of the setters are removed
    assertEquals("Test Title", savedArticle.getTitle());
    assertEquals("https://example.com", savedArticle.getUrl());
    assertEquals("Test Explanation", savedArticle.getExplanation());
    assertEquals("test@example.com", savedArticle.getEmail());
    assertEquals(ldt, savedArticle.getDateAdded());
}

    @Test
@WithMockUser(roles = "USER")
public void getById_whenExists_returnsJson() throws Exception {
    // arrange
    Long id = 123L;
    Articles article = new Articles();
    article.setId(id);
    article.setTitle("Test Title");
    article.setUrl("http://example.com");
    article.setExplanation("An explanation");
    article.setEmail("me@example.com");
    article.setDateAdded(LocalDateTime.now());   // ← supply a LocalDateTime

    when(articlesRepository.findById(eq(id)))
        .thenReturn(Optional.of(article));

    // act + assert
    mockMvc.perform(get("/api/articles")
            .param("id", id.toString())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.title").value("Test Title"))
        .andExpect(jsonPath("$.url").value("http://example.com"))
        .andExpect(jsonPath("$.explanation").value("An explanation"))
        .andExpect(jsonPath("$.email").value("me@example.com"))
        .andExpect(jsonPath("$.dateAdded").exists());

    verify(articlesRepository, times(1)).findById(eq(id));
}

    //
    // 2) NOT FOUND CASE → returns 404 + { "message": "id {id} not found" }
    //
@Test
@WithMockUser(roles = "USER")
public void getById_whenNotExists_returns404() throws Exception {
    Long id = 7L;
    when(articlesRepository.findById(eq(id)))
        .thenReturn(Optional.empty());

    mockMvc.perform(get("/api/articles")
            .param("id", id.toString())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());  // ✅ Only check 404 status!

    verify(articlesRepository, times(1)).findById(eq(id));
}


@Test
@WithMockUser(roles = { "ADMIN" })
public void test_updateArticle_success() throws Exception {
    // arrange
    Articles originalArticle = Articles.builder()
            .title("Original Title")
            .url("https://original-url.com")
            .explanation("Original explanation")
            .email("original@example.com")
            .dateAdded(LocalDateTime.parse("2025-04-24T12:00:00"))
            .build();
    originalArticle.setId(123L);

    Articles incomingUpdate = Articles.builder()
            .title("Updated Title")
            .url("https://updated-url.com")
            .explanation("Updated explanation")
            .email("updated@example.com")
            .dateAdded(LocalDateTime.parse("2025-04-25T15:00:00"))
            .build();

    Articles expectedUpdated = Articles.builder()
            .title("Updated Title")
            .url("https://updated-url.com")
            .explanation("Updated explanation")
            .email("updated@example.com")
            .dateAdded(LocalDateTime.parse("2025-04-25T15:00:00"))
            .build();
    expectedUpdated.setId(123L);

    when(articlesRepository.findById(123L)).thenReturn(Optional.of(originalArticle));
    when(articlesRepository.save(any(Articles.class))).thenReturn(expectedUpdated);

    // act
    MvcResult response = mockMvc.perform(put("/api/articles?id=123")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(incomingUpdate)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Updated Title"))
            .andExpect(jsonPath("$.url").value("https://updated-url.com"))
            .andExpect(jsonPath("$.explanation").value("Updated explanation"))
            .andExpect(jsonPath("$.email").value("updated@example.com"))
            .andExpect(jsonPath("$.dateAdded").exists())
            .andReturn();

    // verify database save
    ArgumentCaptor<Articles> articlesCaptor = ArgumentCaptor.forClass(Articles.class);
    verify(articlesRepository).save(articlesCaptor.capture());
    Articles savedArticle = articlesCaptor.getValue();

    assertEquals("Updated Title", savedArticle.getTitle());
    assertEquals("https://updated-url.com", savedArticle.getUrl());
    assertEquals("Updated explanation", savedArticle.getExplanation());
    assertEquals("updated@example.com", savedArticle.getEmail());
    assertEquals(LocalDateTime.parse("2025-04-25T15:00:00"), savedArticle.getDateAdded());
}


@Test
@WithMockUser(roles = { "ADMIN" })
public void test_updateArticle_notFound() throws Exception {
    // arrange
    Articles incomingUpdate = Articles.builder()
            .title("Updated Title")
            .url("https://updated-url.com")
            .explanation("Updated explanation")
            .email("updated@example.com")
            .dateAdded(LocalDateTime.parse("2025-04-25T15:00:00"))
            .build();

    when(articlesRepository.findById(123L)).thenReturn(Optional.empty());

    // act + assert
    mockMvc.perform(put("/api/articles?id=123")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(incomingUpdate)))
            .andExpect(status().isNotFound());

    verify(articlesRepository, times(1)).findById(123L);
    verify(articlesRepository, times(0)).save(any(Articles.class)); // no save if not found
}



}