package com.backendExtension.ramukaka.controller;



import com.backendExtension.ramukaka.dto.FlowNode;
import com.backendExtension.ramukaka.dto.FlowPayload;
import com.backendExtension.ramukaka.dto.FlowRequest;
import com.backendExtension.ramukaka.dto.FlowStep;
import com.backendExtension.ramukaka.service.FlowBuilderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@RestController
//@RequestMapping("/api/flow")
//public class FlowController {
//    private final FlowBuilderService flowBuilder;
//
//    public FlowController(FlowBuilderService flowBuilder) {
//        this.flowBuilder = flowBuilder;
//    }
//
//    @PostMapping("/generate")
//    public List<FlowNode> generateFlow(@RequestBody FlowPayload payload) {
//        return flowBuilder.buildFlow(payload);
//    }
//}



import com.backendExtension.ramukaka.aiservice.AiService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;


//Not in Use----
@RestController
@RequestMapping("/api/flow")
public class FlowController {

    @Autowired
    private AiService aiService;

    @PostMapping("/generate")
    public Mono<List<FlowStep>> generateFlow(@RequestBody FlowRequest request) {
        if (request.files == null || request.files.isEmpty()) {
            return Mono.just(Collections.emptyList());
        }

        String fileSummary = request.files.stream()
                .limit(5)
                .map(f -> "File: " + f.get("filePath") + "\n" + f.get("content"))
                .collect(Collectors.joining("\n\n"));

        String prompt = "From the following code snippets, generate a high-level flowchart with key steps.\n"
                + "Respond with a numbered list of major steps (max 10) with brief labels.\n\n"
                + fileSummary;

        return aiService.askGemini(prompt)
                .map(response -> toFlowSteps(response, request));
    }

    private List<FlowStep> toFlowSteps(String response, FlowRequest request) {
        List<FlowStep> steps = new ArrayList<>();
        String[] lines = response.split("\n");

        int i = 0;
        for (String line : lines) {
            line = line.trim();
            if (line.matches("^\\d+\\..*")) {
                String label = line.replaceFirst("^\\d+\\.\\s*", "").trim();
                steps.add(makeStep(label, request, i));
                i++;
            }
        }
        return steps;
    }

    private FlowStep makeStep(String label, FlowRequest request, int index) {
        FlowStep step = new FlowStep();
        step.label = label;
        step.filePath = (request.files != null && !request.files.isEmpty())
                ? request.files.get(index % request.files.size()).get("filePath")
                : "System";
        return step;
    }
}

