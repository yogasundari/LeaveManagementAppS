package com.saveetha.LeaveManagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApprovalFlowUpdateDTO {

    @NotBlank
    private String name;

    @NotNull
    private String finalApproverId;

    @NotNull
    private Boolean active;
}