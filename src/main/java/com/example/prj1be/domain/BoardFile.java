package com.example.prj1be.domain;

import lombok.Data;

@Data
public class BoardFile {
    private Integer id;
    private Integer boardId;
    private String name;
    private String url;
}
