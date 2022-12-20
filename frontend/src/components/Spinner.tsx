import React, { useEffect } from "react";

const Spinner =() => {
    return (
        // Show loading spinner
        <div className="d-flex justify-content-center">
            <div className="spinner-border text-primary" role="status">
                <span className="visually-hidden">Loading...</span>
            </div>
        </div>
    )
}

export default Spinner