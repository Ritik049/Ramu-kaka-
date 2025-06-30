package com.backendExtension.ramukaka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


//Not in Use---
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlowNode {
    private String type;    // start, action, decision, end
    private String label;
    private String filePath;
}
