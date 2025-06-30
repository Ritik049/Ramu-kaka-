package com.backendExtension.ramukaka.service;

import com.backendExtension.ramukaka.aiservice.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class SmartFlowChartService {

   // StringBuilder prompt = new StringBuilder();
    @Autowired
    AiService aiService;

    public String generateReactFlowCode(String flowData) {
        String prompt = buildPrompt(flowData); // Build the full prompt with flow data

        Mono<String> reactCodeMono = aiService.askGemini(prompt); // Send the prompt to Gemini

        return reactCodeMono.block(); // Block to retrieve the actual response synchronously
    }


    public String buildPrompt(String flowData)
    {
        StringBuilder prompt = new StringBuilder();

        prompt.append("You're a ReactJS developer using GoJS to render flowcharts. ");
        prompt.append("Use the React template shown below exactly as-is. ");
        prompt.append("Replace only the `nodeDataArray` and `linkDataArray` content based on the provided flow structure. ");
        prompt.append("Return ONLY a complete `App.jsx` component—no commentary or explanation.\n\n");

        prompt.append("Here is the flowData:\n");
        prompt.append(flowData).append("\n\n");

        prompt.append("Use this JSX template:\n");

        prompt.append("``jsx\n");
        prompt.append("import React, { useEffect, useRef } from 'react';\n");
        prompt.append("import * as go from 'gojs';\n");
        prompt.append("import './App.css';\n\n");

        prompt.append("function App() {\n");
        prompt.append("  const diagramRef = useRef(null);\n\n");

        prompt.append("  useEffect(() => {\n");
        prompt.append("    const $ = go.GraphObject.make;\n");
        prompt.append("    if (!diagramRef.current || diagramRef.current.__DIAGRAM_INITIALIZED__) return;\n");
        prompt.append("    diagramRef.current.__DIAGRAM_INITIALIZED__ = true;\n\n");

        prompt.append("    const diagram = $(go.Diagram, diagramRef.current, {\n");
        prompt.append("      initialAutoScale: go.Diagram.Uniform,\n");
        prompt.append("      layout: $(go.LayeredDigraphLayout, { direction: 90 }),\n");
        prompt.append("      isReadOnly: true,\n");
        prompt.append("    });\n\n");

        prompt.append("    diagram.nodeTemplate = $(\n");
        prompt.append("      go.Node,\n");
        prompt.append("      'Auto',\n");
        prompt.append("      $(\n");
        prompt.append("        go.Shape,\n");
        prompt.append("        { name: 'SHAPE', fill: 'lightblue', strokeWidth: 2, stroke: '#333' },\n");
        prompt.append("        new go.Binding('figure', 'isDecision', d => d ? 'Diamond' : 'RoundedRectangle')\n");
        prompt.append("      ),\n");
        prompt.append("      $(\n");
        prompt.append("        go.TextBlock,\n");
        prompt.append("        { margin: 8, font: 'bold 12px sans-serif', stroke: '#000', wrap: go.TextBlock.WrapFit, width: 140 },\n");
        prompt.append("        new go.Binding('text', 'text')\n");
        prompt.append("      )\n");
        prompt.append("    );\n\n");

        prompt.append("    diagram.linkTemplate = $(\n");
        prompt.append("      go.Link,\n");
        prompt.append("      { routing: go.Link.Orthogonal, corner: 5 },\n");
        prompt.append("      $(go.Shape, { strokeWidth: 2 }),\n");
        prompt.append("      $(go.Shape, { toArrow: 'Standard' }),\n");
        prompt.append("      $(\n");
        prompt.append("        go.TextBlock,\n");
        prompt.append("        { segmentOffset: new go.Point(0, -10), font: '10px sans-serif', stroke: '#333' },\n");
        prompt.append("        new go.Binding('text', 'text')\n");
        prompt.append("      )\n");
        prompt.append("    );\n\n");

        prompt.append("    // Replace this with generated data\n");
        prompt.append("    const nodeDataArray = [...];\n");
        prompt.append("    const linkDataArray = [...];\n\n");

        prompt.append("    diagram.model = new go.GraphLinksModel(nodeDataArray, linkDataArray);\n\n");

        prompt.append("    diagram.nodes.each(node => {\n");
        prompt.append("      if (node.data.color) {\n");
        prompt.append("        const shape = node.findObject('SHAPE');\n");
        prompt.append("        if (shape) shape.fill = node.data.color;\n");
        prompt.append("      }\n");
        prompt.append("    });\n");

        prompt.append("  }, []);\n\n");

        prompt.append("  return (\n");
        prompt.append("    <div className='App'>\n");
        prompt.append("      <h2>📈 Flowchart - Auto-Generated</h2>\n");
        prompt.append("      <div\n");
        prompt.append("        ref={diagramRef}\n");
        prompt.append("        style={{ width: '100%', height: '600px', border: '1px solid #ccc' }}\n");
        prompt.append("      />\n");
        prompt.append("    </div>\n");
        prompt.append("  );\n");
        prompt.append("}\n\n");

        prompt.append("export default App;\n");
        prompt.append("```\n");


        return prompt.toString();
    }



}
