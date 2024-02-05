// EmptyQuestionCard.jsx
import Spinner from "./Spinner"
import React from "react";

const EmptyQuestionCard = () => {
    return (
        <div className="card shadowOnHover">
            <div className="d-flex card-body m-0">
                <div className="d-flex card-body align-items-center justify-content-center m-0">
                    <Spinner/>
                </div>
            </div>
        </div>
    );
};

export default EmptyQuestionCard;
