package com.backendExtension.ramukaka.service;

import com.backendExtension.ramukaka.dto.FileData;
import com.backendExtension.ramukaka.dto.FlowNode;
import com.backendExtension.ramukaka.dto.FlowPayload;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


//Not in Use*****
@Service
public class FlowBuilderService {

    public List<FlowNode> buildFlow(FlowPayload payload) {
        List<FlowNode> flow = new ArrayList<>();
        flow.add(new FlowNode("start", "🟢 Project Start", "ALL"));

        for (FileData file : payload.getFiles()) {
            String path = file.getFilePath();
            String content = file.getContent();

            if (!(path.endsWith(".java") || path.endsWith(".ts") || path.endsWith(".js"))) continue;

            flow.add(new FlowNode("action", "📄 " + path, path));

            if (content.contains("main(")) {
                flow.add(new FlowNode("action", "▶️ Entry point detected", path));
            }
            if (content.contains("if") || content.contains("switch")) {
                flow.add(new FlowNode("decision", "🔀 Conditional logic", path));
            }
            if (content.contains("for") || content.contains("while")) {
                flow.add(new FlowNode("loop", "🔁 Loop detected", path));
            }
        }

        flow.add(new FlowNode("end", "🏁 Project Ends", "ALL"));
        return flow;
    }
}
