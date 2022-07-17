import React from "react";

import '../resources/styles/argon-design-system.css';
import '../resources/styles/blk-design-system.css';
import '../resources/styles/general.css';
import '../resources/styles/stepper.css';

interface arg{
    communities : Array<String>
    thisCommunity: String
}
export default function CommunitiesCard(props: {title: string, communities: Array<String>, thisCommunity: String}){ //despues hay que pasarle todas las comunidades y en cual estoy
    return(

            <div className="white-pill mt-5 mx-3">
                <div className="card-body">
                    <p className="h3 text-primary">{props.title}</p>
                    <hr></hr>
                    <div className="container-fluid">
                        {props.communities.map( community => <a className="btn btn-outline-primary badge-pill badge-lg my-3">{community}</a> )}
                        <a className="btn btn-light badge-pill badge-lg my-3">{props.thisCommunity}</a>
                    </div>
                </div>
            </div>
    )
}