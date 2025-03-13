package com.oblojmarcin.cvmaker.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class ChangePasswordRequest {
    private String token;
    private String newPassword;

}
