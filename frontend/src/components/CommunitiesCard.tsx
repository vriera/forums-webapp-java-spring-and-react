import React from "react";

import '../resources/styles/argon-design-system.css';
import '../resources/styles/blk-design-system.css';
import '../resources/styles/general.css';
import '../resources/styles/stepper.css';

export default function CommunitiesCard(){ //despues hay que pasarle todas las comunidades y en cual estoy
    return(

            <div className="white-pill mt-5 ml-3">
                <div className="card-body">
                    <p className="h3 text-primary">Comunities</p>
                    <hr></hr>
                    <div className="container-fluid">
                        <a className="btn btn-outline-primary badge-pill badge-lg my-3">Comunities all</a>
                        <a className="btn btn-light badge-pill badge-lg my-3">Matematica</a>
                    </div>
                </div>
            </div>
    )
}