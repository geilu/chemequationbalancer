import { useEffect } from 'react';

export default function useAutoResize(ref) {
    useEffect(() => {
        if (!globalThis.electronAPI) {
            console.warn("Electron API not found, auto-resize disabled");
            return;
        }

        const observer = new ResizeObserver((entries) => {
            for (let entry of entries) {
                const height = entry.contentRect.height;
                const width = entry.contentRect.width;

                globalThis.electronAPI.resizeWindow(width, Math.ceil(height + 100));
            }
        });

        if (ref.current) {
            observer.observe(ref.current);
        }

        return () => observer.disconnect();
    }, [ref]);
}