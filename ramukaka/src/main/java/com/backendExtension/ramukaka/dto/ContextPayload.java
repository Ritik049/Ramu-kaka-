package com.backendExtension.ramukaka.dto;

import com.backendExtension.ramukaka.controller.ContextController;
import lombok.Data;

import java.util.List;

@Data
public  class ContextPayload {
    private String room;
    private List<FileData> files;
}