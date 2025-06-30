import React, { useEffect, useRef } from 'react';
import * as go from 'gojs';
import './App.css';

function App() {
  const diagramRef = useRef(null);

  useEffect(() => {
    const $ = go.GraphObject.make;
    if (!diagramRef.current || diagramRef.current.__DIAGRAM_INITIALIZED__) return;
    diagramRef.current.__DIAGRAM_INITIALIZED__ = true;

    const diagram = $(go.Diagram, diagramRef.current, {
      initialAutoScale: go.Diagram.Uniform,
      layout: $(go.LayeredDigraphLayout, { direction: 90 }),
      isReadOnly: true,
    });

    diagram.nodeTemplate = $(
      go.Node,
      'Auto',
      $(
        go.Shape,
        { name: 'SHAPE', fill: 'lightblue', strokeWidth: 2, stroke: '#333' },
        new go.Binding('figure', 'isDecision', d => d ? 'Diamond' : 'RoundedRectangle')
      ),
      $(
        go.TextBlock,
        { margin: 8, font: 'bold 12px sans-serif', stroke: '#000', wrap: go.TextBlock.WrapFit, width: 140 },
        new go.Binding('text', 'text')
      )
    );

    diagram.linkTemplate = $(
      go.Link,
      { routing: go.Link.Orthogonal, corner: 5 },
      $(go.Shape, { strokeWidth: 2 }),
      $(go.Shape, { toArrow: 'Standard' }),
      $(
        go.TextBlock,
        { segmentOffset: new go.Point(0, -10), font: '10px sans-serif', stroke: '#333' },
        new go.Binding('text', 'text')
      )
    );

    // Replace this with generated data
    const nodeDataArray = [
      { key: "A", text: "Client (React Frontend)", color: "#f9f" },
      { key: "B", text: "Email Content & Tone" },
      { key: "C", text: "API Endpoint (http://localhost:8080/api/email/generate)", isDecision: true },
      { key: "D", text: "Backend (Node.js/Express)", color: "#ccf" },
      { key: "E", text: "Email Reply Generation Logic", color: "#ffc" },
      { key: "F", text: "Generated Reply", color: "#ccf" },
      { key: "G", text: "Response to Frontend" },
      { key: "H", text: "Client (Displays Reply)", color: "#f9f" },
      { key: "I", text: "User" }
    ];
    const linkDataArray = [
      { from: "A", to: "B" },
      { from: "B", to: "C" },
      { from: "C", to: "D" },
      { from: "D", to: "E" },
      { from: "E", to: "F" },
      { from: "F", to: "C" },
      { from: "C", to: "G" },
      { from: "G", to: "H" },
      { from: "I", to: "H", text: "Copies" }
    ];

    diagram.model = new go.GraphLinksModel(nodeDataArray, linkDataArray);

    diagram.nodes.each(node => {
      if (node.data.color) {
        const shape = node.findObject('SHAPE');
        if (shape) shape.fill = node.data.color;
      }
    });
  }, []);

  return (
    <div className='App'>
      <h2>📈 Flowchart - Auto-Generated</h2>
      <div
        ref={diagramRef}
        style={{ width: '100%', height: '600px', border: '1px solid #ccc' }}
      />
    </div>
  );
}

export default App;