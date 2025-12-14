package com.example.ticketing.dto.admin;

import com.example.ticketing.entity.AiSuggestionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminUpdateAiSuggestionStatusRequest {

    @NotNull
    private AiSuggestionStatus status;
}
