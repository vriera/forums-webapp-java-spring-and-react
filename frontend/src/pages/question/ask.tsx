import React from 'react';
import '../../resources/styles/argon-design-system.css';
import '../../resources/styles/blk-design-system.css';
import '../../resources/styles/general.css';
import '../../resources/styles/stepper.css';
import './ask.css'

const Header = () => {
    return (
        <div className="card-header">
            <span>Ask page</span>
        </div>
    );
}

const Questions = () => {
    return(
        <div className="wrapper">
            <div className="section section-hero section-shaped">
                <div className="shape shape-style-1 shape-default shape-skew viewheight-90">
                    <span className="span-150 square1"></span>
                    <span className="span-50 square2"></span>
                    <span className="span-50 square3"></span>
                    <span className="span-75 square4"></span>
                    <span className="span-100 square5"></span>
                    <span className="span-75 square6"></span>
                    <span className="span-50 square7"></span>
                    <span className="span-100 square3"></span>
                    <span className="span-50 square2"></span>
                    <span className="span-100 square4"></span>
                </div>
                <div className="float-parent-element">
                    <div className="float-child-element">
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
                    </div>
                    <div className="float-child-element2">
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
                    </div>
                    <div className="responder">
                            <div className="white-pill mt-5">
                                <div className="card-body">
                                    <p className="h3 text-primary">respuesta</p>
                                    <hr></hr>
                                    <div className="form-group">
                                        <form>
                                            <input type="text">

                                            </input>
                                        </form>
                                    </div>
                                    <div className="d-flex justify-content-center mb-3 mt-3">
                                        <button type="submit" className="btn btn-primary">
                                            Enviar
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
    )
}


const AskPage = () => {
    return (
        <React.Fragment>
            <Header />
            <Questions />
        </React.Fragment>

    );
};


export default AskPage;
