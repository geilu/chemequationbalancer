const { app, BrowserWindow, ipcMain, screen } = require('electron');
const path = require('node:path');

const isDev = process.env.NODE_ENV === 'development';

function createWindow() {
    const win = new BrowserWindow({
        width: 800,
        height: 270,
        useContentSize: true,
        resizable: false,
        webPreferences: {
            preload: path.join(__dirname, 'preload.js'),
            nodeIntegration: false,
            contextIsolation: true
        }
    });

    if (isDev) {
        win.loadURL('http://localhost:3000');
        win.webContents.openDevTools();
        win.setResizable(true);
    } else {
        win.loadFile(path.join(__dirname, 'gui/dist/index.html'));
    }
}

ipcMain.on('resize-window', (event, width, height) => {
    const win = BrowserWindow.fromWebContents(event.sender);
    if (!win) return;

    const { workAreaSize } = screen.getPrimaryDisplay();
    const maxLimit = workAreaSize.height - 50;
    const newHeight = Math.min(height, maxLimit);
    const finalHeight = Math.max(newHeight, 270);

    const [currentWidth] = win.getContentSize();
    win.setContentSize(currentWidth, finalHeight);
})

app.whenReady().then(createWindow);