package com.theanh.dev.IAM_Service.Dtos.Requests.Admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSearchRequest {
    private String keyword;
    private int limit = 10;
    private int offset = 0;
}
