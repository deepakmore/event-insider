package com.myproject.insider.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiMeta {

    public String code;
    public String status;
    public String message;
    public String requestId;
    public String responseId;
}
