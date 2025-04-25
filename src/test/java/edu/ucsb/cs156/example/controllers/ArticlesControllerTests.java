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


@WebMvcTest(controllers = ArticlesController.class)
@Import(TestConfig.class)
public class ArticlesControllerTests extends ControllerTestCase {

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

}