import './App.css'
import InputFieldArray from "./components/inputFieldArray.jsx";
import EnterButton from "./components/enterButton.jsx";
import { useRef } from 'react';
import useAutoResize from "./hooks/useAutoResize.js";

function App() {
    const contentRef = useRef(null);
    useAutoResize(contentRef);


    return (
        <div ref={contentRef} className="main-container">
            <div className="equation-container">
                <InputFieldArray />

                <div className="equals-sign">=</div>

                <InputFieldArray />
            </div>
            <EnterButton />
        </div>
    )
}

export default App
