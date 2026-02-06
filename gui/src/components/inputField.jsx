import {useState} from "react";

export default function InputField() {
    const [inputValue, setInputValue] = useState('');

    return (
        <>
            <input
                value={inputValue}
                type="text"
                className="element-field"
                onChange={(e) => setInputValue(e.target.value)}
                style={{ width: `${Math.max(3, (inputValue.length+3))}ch` }}/>
        </>
    )
}