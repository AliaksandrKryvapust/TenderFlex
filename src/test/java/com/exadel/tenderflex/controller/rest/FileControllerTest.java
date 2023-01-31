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