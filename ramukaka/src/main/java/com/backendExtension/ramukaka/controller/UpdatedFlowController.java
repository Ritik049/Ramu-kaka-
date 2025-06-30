package com.backendExtension.ramukaka.controller;

import com.backendExtension.ramukaka.service.SmartFlowChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.regex.*;

@RestController
@RequestMapping("/api/flow/updated")
public class UpdatedFlowController {


//    @PostMapping("/generate-react")
//    public String generateReactFlowComponent(@RequestBody Map<String, String> request) {
//        String graphText = request.get("graphText");
//        System.out.println("CONSOLE tEXT "+graphText);
//        if (graphText == null || (!graphText.contains("-->") && !graphText.contains("->"))) {
//            return "// Invalid graph definition";
//        }
//
//        // Extract nodes and links from Mermaid-style graph
//        Set<String> nodes = new LinkedHashSet<>();
//        List<String[]> edges = new ArrayList<>();
//
////        Pattern pattern = Pattern.compile("(\\w+)\\s*-+>\\s*(\\w+)(?:\\s*:\\s*['\"]?(.*?)['\"]?)?");
//        // Updated regex to extract node ID and label
//        Pattern pattern = Pattern.compile("(\\w+)\\s*(?:\\[(.*?)\\]|\\((.*?)\\)|\\{(.*?)\\})?\\s*-+>\\s*(\\w+)(?:\\s*:\\s*['\"]?(.*?)['\"]?)?");
//
//        Matcher matcher = pattern.matcher(graphText);
////        while (matcher.find()) {
////            String from = matcher.group(1);
////            String to = matcher.group(2);
////            String label = matcher.group(3) != null ? matcher.group(3) : "";
////            nodes.add(from);
////            nodes.add(to);
////            edges.add(new String[]{from, to, label});
////        }
//        while (matcher.find()) {
//            String from = matcher.group(1);
//            String fromLabel = matcher.group(2) != null ? matcher.group(2)
//                    : matcher.group(3) != null ? matcher.group(3)
//                    : matcher.group(4) != null ? matcher.group(4)
//                    : from;
//
//            String to = matcher.group(5);
//            String label = matcher.group(6) != null ? matcher.group(6) : "";
//
//            nodes.add(from + "::" + fromLabel); // store ID + label
//            nodes.add(to + "::" + to);          // fallback label is ID
//            edges.add(new String[]{from, to, label});
//        }
//
//        StringBuilder nodeJson = new StringBuilder("[");
//        for (String nodeWithLabel : nodes) {
//            String[] parts = nodeWithLabel.split("::");
//            nodeJson.append(String.format("{ key: \"%s\", text: \"%s\" },", parts[0], parts[1]));
//        }
//
//        if (!nodes.isEmpty()) nodeJson.setLength(nodeJson.length() - 1);
//        nodeJson.append("]");
//
//        StringBuilder linkJson = new StringBuilder("[");
//        for (String[] edge : edges) {
//            linkJson.append(String.format("{ from: \"%s\", to: \"%s\", text: \"%s\" },", edge[0], edge[1], edge[2]));
//        }
//        if (!edges.isEmpty()) linkJson.setLength(linkJson.length() - 1);
//        linkJson.append("]");
//
//        String template = """
//import React, { useEffect, useRef } from 'react';
//import * as go from 'gojs';
//import Box from '@mui/material/Box';
//import Typography from '@mui/material/Typography';
//import { css } from '@emotion/react';
//
//function App() {
//  const diagramRef = useRef(null);
//
//  useEffect(() => {
//    const $ = go.GraphObject.make;
//    if (!diagramRef.current || diagramRef.current.__DIAGRAM_INITIALIZED__) return;
//    diagramRef.current.__DIAGRAM_INITIALIZED__ = true;
//
//    const diagram = $(go.Diagram, diagramRef.current, {
//      initialAutoScale: go.Diagram.Uniform,
//      layout: $(go.LayeredDigraphLayout, { direction: 90 }),
//      isReadOnly: true,
//    });
//
//    diagram.nodeTemplate =
//      $(go.Node, "Auto",
//        $(go.Shape, "RoundedRectangle", { fill: "#e3f2fd", stroke: "#1e88e5", strokeWidth: 2 }),
//        $(go.TextBlock, { margin: 8, font: "bold 13px sans-serif", wrap: go.TextBlock.WrapFit, width: 140 },
//          new go.Binding("text", "text")
//        )
//      );
//
//    diagram.linkTemplate =
//      $(go.Link, { routing: go.Link.Orthogonal, corner: 5 },
//        $(go.Shape, { strokeWidth: 2 }),
//        $(go.Shape, { toArrow: "Standard" }),
//        $(go.TextBlock, { segmentOffset: new go.Point(0, -10), font: "10px sans-serif" },
//          new go.Binding("text", "text")
//        )
//      );
//
//    diagram.model = new go.GraphLinksModel(
//      NODE_JSON,
//      LINK_JSON
//    );
//  }, []);
//
//  return (
//    <Box sx={{ p: 3 }}>
//      <Typography variant="h6" gutterBottom>📈 Auto-Generated Flowchart</Typography>
//      <Box ref={diagramRef} sx={{ border: '1px solid #ccc', height: '600px', width: '100%' }} />
//    </Box>
//  );
//}
//
//export default App;
//""";
//
//        return template
//                .replace("NODE_JSON", nodeJson.toString())
//                .replace("LINK_JSON", linkJson.toString());
//    }

     @Autowired
    SmartFlowChartService smartFlowChartService;

    @PostMapping("/generate-react")
   public String generateReactFlowComponent(@RequestBody Map<String, String> request) {

        return smartFlowChartService.generateReactFlowCode(request.get("graphText"));

    }

}

