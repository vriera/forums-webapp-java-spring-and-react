import React from "react";
import '../resources/styles/argon-design-system.css';
import '../resources/styles/blk-design-system.css';
import '../resources/styles/general.css';
import '../resources/styles/stepper.css';


export default function QuestionCard(){ //despues hay que pasarle todas las comunidades y en cual estoy
    return(

        <div className="white-pill mt-5">
            <div className="card-body">
                <div className="d-flex justify-content-center">
                    <p className="h1 text-primary">
                        ¿Cuanto da 2+2?
                    </p>
                </div>
            </div>

            <div className="row">
                <div className="col">
                    <div className="d-flex flex-column justify-content-center ml-3">
                        <div className="justify-content-center">
                            <p><span className="badge badge-primary badge-pill">Matematica</span></p>
                        </div>
                        <div className="justify-content-center">
                            <p className="h6">Hecha por Natu</p>
                        </div>

                    </div>
                    <div className="col-12 text-wrap-ellipsis justify-content-center">
                        <p className="h5">No estoy segura</p>
                    </div>

                </div>
            </div>
        </div>
    )
}