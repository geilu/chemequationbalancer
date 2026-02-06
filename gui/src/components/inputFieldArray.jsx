import {useState, Fragment} from 'react';
import InputField from './inputField.jsx';

let nextId = 1;

export default function InputFieldArray() {
    const [fields, setFields] = useState([0]);

    const addField = () => {
        if (fields.length === 10) return;
        setFields([
            ...fields,
            nextId++
        ])
    }

    const removeField = () => {
        if (fields.length === 1) return;
        setFields(fields.slice(0, -1));
    }

    return (
            <div className="element-window">
                <div className="add-remove-buttons">
                    <button className="btn add-field" onClick={addField}>+</button>
                    <button className="btn remove-field" onClick={removeField}>-</button>
                </div>

                <hr className="element-button-divider"/>

                <div className="field-grid">
                    {fields.map((id, i) => (
                        <Fragment key={id}>
                            {i > 0 && <span className="plus-symbol">+</span>}
                            <InputField />
                        </Fragment>
                    ))}
                </div>
            </div>
    )
}