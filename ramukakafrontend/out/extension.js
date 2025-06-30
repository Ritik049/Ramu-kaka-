"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || (function () {
    var ownKeys = function(o) {
        ownKeys = Object.getOwnPropertyNames || function (o) {
            var ar = [];
            for (var k in o) if (Object.prototype.hasOwnProperty.call(o, k)) ar[ar.length] = k;
            return ar;
        };
        return ownKeys(o);
    };
    return function (mod) {
        if (mod && mod.__esModule) return mod;
        var result = {};
        if (mod != null) for (var k = ownKeys(mod), i = 0; i < k.length; i++) if (k[i] !== "default") __createBinding(result, mod, k[i]);
        __setModuleDefault(result, mod);
        return result;
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
exports.activate = activate;
const vscode = __importStar(require("vscode"));
const fs = __importStar(require("fs"));
const path = __importStar(require("path"));
const REACT_PROJECT_PATH = "C:\\Users\\agraw\\OneDrive\\Desktop\\RamukakaExtension\\ramu-react-preview";
const APP_JSX_PATH = path.join(REACT_PROJECT_PATH, "src", "App.js");
function activate(context) {
    const provider = new RamuViewProvider(context);
    vscode.window.registerWebviewViewProvider('chatAssistantView', provider);
    vscode.commands.registerCommand('ramu.scanWorkspace', () => provider.scanWorkspace());
}
class RamuViewProvider {
    context;
    webviewView;
    currentRoomId = '';
    constructor(context) {
        this.context = context;
    }
    resolveWebviewView(webviewView) {
        this.webviewView = webviewView;
        const webPath = path.join(this.context.extensionPath, 'web');
        const indexPath = path.join(webPath, 'index.html');
        let html = fs.readFileSync(indexPath, 'utf8');
        html = html.replace(/(src=")(.*?)(")/g, (_match, p1, p2, p3) => {
            const uri = vscode.Uri.file(path.join(webPath, p2));
            return `${p1}${webviewView.webview.asWebviewUri(uri)}${p3}`;
        });
        webviewView.webview.options = {
            enableScripts: true,
            localResourceRoots: [vscode.Uri.file(webPath)]
        };
        webviewView.webview.html = html;
        webviewView.webview.onDidReceiveMessage(async (msg) => {
            if (msg.type === 'scanWorkspace') {
                this.currentRoomId = msg.roomId;
                await this.scanWorkspace();
            }
            else if (msg.type === 'viewFlow') {
                vscode.commands.executeCommand('ramu.viewFlow');
            }
            else if (msg.type === 'previewReactFlow') {
                const cleanCode = msg.code
                    .replace(/^```[a-z]*\s*/i, '') // remove starting ```jsx or ```js
                    .replace(/```$/, '') // remove trailing ```
                    .trim();
                // Step 1: Log start of write
                console.log("📝 Writing cleaned JSX code to App.jsx...");
                // Step 2: Render preview
                RamuFlowPanel.renderReactCode(cleanCode);
                // Step 3: Overwrite App.jsx
                fs.writeFileSync(APP_JSX_PATH, cleanCode, 'utf8');
                // Step 4: Confirm success
                console.log("✅ Successfully wrote code to:", APP_JSX_PATH);
                // Step 5: Launch the React project
                require('child_process').exec(`start cmd /k "cd ${REACT_PROJECT_PATH} && npm start"`);
            }
        });
    }
    async scanWorkspace() {
        if (!this.currentRoomId || !vscode.workspace.workspaceFolders) {
            this.webviewView?.webview.postMessage({
                type: 'systemMessage',
                text: `⚠️ Scan failed: No workspace is open.`
            });
            return;
        }
        const files = await vscode.workspace.findFiles('**/*', '**/{node_modules,.git}/**', 500);
        const fileList = [];
        for (const file of files) {
            try {
                const content = await vscode.workspace.fs.readFile(file);
                fileList.push({
                    filePath: vscode.workspace.asRelativePath(file),
                    content: Buffer.from(content).toString('utf8')
                });
            }
            catch { }
        }
        const payload = { room: this.currentRoomId, files: fileList };
        await fetch('http://localhost:8080/api/context', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        this.context.globalState.update('currentRoomId', this.currentRoomId);
        this.context.globalState.update('scannedFiles', fileList);
        this.webviewView?.webview.postMessage({
            type: 'systemMessage',
            text: `📁 Workspace scanned: ${fileList.length} files indexed. RAMU can now understand your project.`
        });
    }
}
class RamuFlowPanel {
    // Old version
    //   static renderReactCode(code: string) {
    //   const panel = vscode.window.createWebviewPanel(
    //     'ramuReactPreview',
    //     '📦 Flowchart Preview - React JSX',
    //     vscode.ViewColumn.Beside,
    //     { enableScripts: true }
    //   );
    //   const escaped = code.replace(/</g, '&lt;').replace(/>/g, '&gt;');
    //   panel.webview.html = `
    //     <!DOCTYPE html>
    //     <html>
    //     <head>
    //       <style>
    //         body { font-family: monospace; white-space: pre-wrap; background: #f4f4f4; padding: 20px; }
    //         code { color: #222; font-size: 13px; }
    //       </style>
    //     </head>
    //     <body>
    //       <h3>📘 React Flowchart Code</h3>
    //       <code>${escaped}</code>
    //     </body>
    //     </html>
    //   `;
    // }
    static renderReactCode(code) {
        const panel = vscode.window.createWebviewPanel('ramuReactPreview', '📦 Flowchart Preview - React JSX', vscode.ViewColumn.Beside, { enableScripts: true });
        const escaped = code.replace(/</g, '&lt;').replace(/>/g, '&gt;');
        panel.webview.html = `
    <!DOCTYPE html>
    <html lang="en">
    <head>
      <meta charset="UTF-8">
      <style>
        body {
          margin: 0;
          padding: 0;
          background: #f4f4f4;
          font-family: 'Courier New', Courier, monospace;
          color: #333;
        }
        .container {
          padding: 20px;
        }
        h3 {
          color: #000;
          margin-bottom: 10px;
        }
        pre {
          background: #1e1e1e;
          color: #d4d4d4;
          padding: 16px;
          border-radius: 8px;
          overflow-x: auto;
          font-size: 13px;
          line-height: 1.5;
        }
      </style>
    </head>
    <body>
      <div class="container">
        <h3>📘 React Flowchart Code</h3>
        <pre><code>${escaped}</code></pre>
      </div>
    </body>
    </html>
  `;
    }
}
//# sourceMappingURL=extension.js.map