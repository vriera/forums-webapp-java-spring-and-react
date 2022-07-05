import React from 'react';
import '../../resources/styles/argon-design-system.css';
import '../../resources/styles/blk-design-system.css';
import '../../resources/styles/general.css';
import '../../resources/styles/stepper.css';
import './ask.css'
import '../../components/CommunitiesCard'
import CommunitiesCard from "../../components/CommunitiesCard";
import QuestionCard from "../../components/QuestionCard";
import Background from "../../components/Background";
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
                <Background />
                <div className="float-parent-element">
                    <div className="float-child-element">
                        < CommunitiesCard />
                    </div>
                    <div className="float-child-element2">
                        < QuestionCard />
                    </div>
                    <div className="float-child-element3">
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
