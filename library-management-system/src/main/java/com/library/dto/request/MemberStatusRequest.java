package com.library.dto.request;

import com.library.entity.enums.MemberStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberStatusRequest {

    @NotNull(message = "Status cannot be null")
    private MemberStatus status;
}
