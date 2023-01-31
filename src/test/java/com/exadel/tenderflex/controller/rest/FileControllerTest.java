package com.exadel.tenderflex.controller.rest;

import com.exadel.tenderflex.controller.utils.JwtTokenUtil;
import com.exadel.tenderflex.core.dto.input.FileDtoInput;
import com.exadel.tenderflex.core.dto.output.FileDtoOutput;
import com.exadel.tenderflex.repository.entity.enums.EFileType;
import com.exadel.tenderflex.service.JwtUserDetailsService;
import com.exadel.tenderflex.service.api.IFileManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.util.UUID;

@WebMvcTest(controllers = FileController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@AutoConfigureMockMvc
class FileControllerTest {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private IFileManager fileManager;
    @MockBean
    SecurityContext securityContext;
    // Beans for JwtFilter
    @MockBean
    private JwtTokenUtil tokenUtil;
    @MockBean
    private JwtUserDetailsService userDetailsService;

    // preconditions
    final Instant dtCreate = Instant.ofEpochMilli(1673532204657L);
    final Instant dtUpdate = Instant.ofEpochMilli(1673532532870L);
    final UUID id = UUID.fromString("58c635ab-8bac-4899-bc30-b0bb4524c28b");
    final String contentType = "application/pdf";
    final String fileName = "testFile";
    final String url = "http//localhost:8082";
    final String multipleError = "structured_error";

    @Test
    @WithMockUser(username = "contractor@gmail.com", password = "55ffg89", roles = {"CONTRACTOR"})
    void put() throws Exception {
        // preconditions
        final FileDtoInput dtoInput = getPreparedFileDtoInput();
        final FileDtoOutput dtoOutput = getPreparedFileDtoOutput();
        Mockito.when(fileManager.updateDto(dtoInput, id, dtUpdate.toEpochMilli())).thenReturn(dtoOutput);

        // assert
        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/file/" + id + "/version/" + dtUpdate.toEpochMilli())
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dtoInput)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.file_type").value(EFileType.AWARD_DECISION.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content_type").value(contentType))
                .andExpect(MockMvcResultMatchers.jsonPath("$.file_name").value(fileName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.url").value(url))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dt_create").value(dtCreate.toEpochMilli()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dt_update").value(dtUpdate.toEpochMilli()));

        //test
        Mockito.verify(fileManager).updateDto(dtoInput, id, dtUpdate.toEpochMilli());
    }

    @Test
    void validateFileDtoEmptyFileType() throws Exception {
        // preconditions
        final FileDtoInput dtoInput = FileDtoInput.builder()
                .fileType(null)
                .contentType(contentType)
                .fileName(fileName)
                .url(url)
                .build();
        final String errorMessage = "file type cannot be null";
        final String field = "fileType";

        // assert
        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/file/" + id + "/version/" + dtUpdate.toEpochMilli())
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dtoInput)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.logref").value(multipleError))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[*].field").value(field))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[*].message").value(errorMessage));

        //test
        Mockito.verify(fileManager, Mockito.times(0)).updateDto(dtoInput, id, dtUpdate.toEpochMilli());
    }

    @Test
    void validateFileDtoIncorrectFileType() throws Exception {
        // preconditions
        final FileDtoInput dtoInput = FileDtoInput.builder()
                .fileType("test")
                .contentType(contentType)
                .fileName(fileName)
                .url(url)
                .build();
        final String errorMessage = "file type does not match";
        final String field = "fileType";

        // assert
        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/file/" + id + "/version/" + dtUpdate.toEpochMilli())
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dtoInput)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.logref").value(multipleError))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[*].field").value(field))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[*].message").value(errorMessage));

        //test
        Mockito.verify(fileManager, Mockito.times(0)).updateDto(dtoInput, id, dtUpdate.toEpochMilli());
    }

    @Test
    void validateFileDtoEmptyContentType() throws Exception {
        // preconditions
        final FileDtoInput dtoInput = FileDtoInput.builder()
                .fileType(EFileType.AWARD_DECISION.name())
                .contentType(null)
                .fileName(fileName)
                .url(url)
                .build();
        final String errorMessage = "content type cannot be empty";
        final String field = "contentType";

        // assert
        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/file/" + id + "/version/" + dtUpdate.toEpochMilli())
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dtoInput)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.logref").value(multipleError))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[*].field").value(field))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[*].message").value(errorMessage));

        //test
        Mockito.verify(fileManager, Mockito.times(0)).updateDto(dtoInput, id, dtUpdate.toEpochMilli());
    }

    @Test
    void validateFileDtoBlankContentType() throws Exception {
        // preconditions
        final FileDtoInput dtoInput = FileDtoInput.builder()
                .fileType(EFileType.AWARD_DECISION.name())
                .contentType(" ")
                .fileName(fileName)
                .url(url)
                .build();
        final String errorMessage = "content type cannot be empty";
        final String field = "contentType";

        // assert
        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/file/" + id + "/version/" + dtUpdate.toEpochMilli())
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dtoInput)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.logref").value(multipleError))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[*].field").value(field))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[*].message").value(errorMessage));

        //test
        Mockito.verify(fileManager, Mockito.times(0)).updateDto(dtoInput, id, dtUpdate.toEpochMilli());
    }

    @Test
    void validateFileDtoEmptyFileName() throws Exception {
        // preconditions
        final FileDtoInput dtoInput = FileDtoInput.builder()
                .fileType(EFileType.AWARD_DECISION.name())
                .contentType(contentType)
                .fileName(null)
                .url(url)
                .build();
        final String errorMessage = "file name cannot be empty";
        final String field = "fileName";

        // assert
        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/file/" + id + "/version/" + dtUpdate.toEpochMilli())
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dtoInput)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.logref").value(multipleError))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[*].field").value(field))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[*].message").value(errorMessage));

        //test
        Mockito.verify(fileManager, Mockito.times(0)).updateDto(dtoInput, id, dtUpdate.toEpochMilli());
    }

    @Test
    void validateFileDtoBlankFileName() throws Exception {
        // preconditions
        final FileDtoInput dtoInput = FileDtoInput.builder()
                .fileType(EFileType.AWARD_DECISION.name())
                .contentType(contentType)
                .fileName(" ")
                .url(url)
                .build();
        final String errorMessage = "file name cannot be empty";
        final String field = "fileName";

        // assert
        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/file/" + id + "/version/" + dtUpdate.toEpochMilli())
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dtoInput)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.logref").value(multipleError))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[*].field").value(field))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[*].message").value(errorMessage));

        //test
        Mockito.verify(fileManager, Mockito.times(0)).updateDto(dtoInput, id, dtUpdate.toEpochMilli());
    }

    @Test
    void validateFileDtoEmptyUrl() throws Exception {
        // preconditions
        final FileDtoInput dtoInput = FileDtoInput.builder()
                .fileType(EFileType.AWARD_DECISION.name())
                .contentType(contentType)
                .fileName(fileName)
                .url(null)
                .build();
        final String errorMessage = "url cannot be empty";
        final String field = "url";

        // assert
        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/file/" + id + "/version/" + dtUpdate.toEpochMilli())
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dtoInput)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.logref").value(multipleError))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[*].field").value(field))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[*].message").value(errorMessage));

        //test
        Mockito.verify(fileManager, Mockito.times(0)).updateDto(dtoInput, id, dtUpdate.toEpochMilli());
    }

    @Test
    void validateFileDtoBlankUrl() throws Exception {
        // preconditions
        final FileDtoInput dtoInput = FileDtoInput.builder()
                .fileType(EFileType.AWARD_DECISION.name())
                .contentType(contentType)
                .fileName(fileName)
                .url(" ")
                .build();
        final String errorMessage = "url cannot be empty";
        final String field = "url";

        // assert
        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/file/" + id + "/version/" + dtUpdate.toEpochMilli())
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dtoInput)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.logref").value(multipleError))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[*].field").value(field))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[*].message").value(errorMessage));

        //test
        Mockito.verify(fileManager, Mockito.times(0)).updateDto(dtoInput, id, dtUpdate.toEpochMilli());
    }

    FileDtoInput getPreparedFileDtoInput() {
        return FileDtoInput.builder()
                .fileType(EFileType.AWARD_DECISION.name())
                .contentType(contentType)
                .fileName(fileName)
                .url(url)
                .build();
    }

    FileDtoOutput getPreparedFileDtoOutput() {
        return FileDtoOutput.builder()
                .id(id.toString())
                .fileType(EFileType.AWARD_DECISION.name())
                .contentType(contentType)
                .fileName(fileName)
                .url(url)
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate)
                .build();
    }
}