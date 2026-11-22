package com.atelier_floral.backend.dto.auth;

import com.atelier_floral.backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenValidationResponse {
    private boolean valid;
    private String username;
    private User user;
}
