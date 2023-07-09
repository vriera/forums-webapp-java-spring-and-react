
import { useState, useEffect } from "react";


export default function VotingOptions(props: {myVote : boolean | undefined, votes: number, userId : number, id : number, 
    vote: (userId: number, id: number, vote: boolean) => Promise<any> ,
    deleteVote: (userId: number, id: number ) => Promise<any>}) {
    const [myVote, setMyVote] = useState(props.myVote);
    const [votes, setVotes] = useState(props.votes);

    async function upVote() {
        await props.vote(props.userId, props.id, true);
        setMyVote(true);
        setVotes(votes + 1);
    }

    async function downVote() {
        await props.vote(props.userId, props.id, false);
        setMyVote(false);
        setVotes(votes - 1);
    }

    async function nullVote() {
        await props.deleteVote(props.userId, props.id);
        setMyVote(undefined);
        setVotes(votes - (myVote ? 1 : -1));
    }

    return (
        <div className="col-2 d-flex flex-column align-items-center">
            <div>
                {myVote === true && (
                    <button className="clickable btn b-0 p-0" onClick={nullVote}>
                        <img
                            src={require("../images/votes.png")}
                            width="30"
                            height="30"
                            alt="upvote"
                        />
                    </button>
                )}
                {(myVote == null ||
                    myVote === false) && (
                        <button className="clickable btn b-0 p-0" onClick={upVote}>
                            <img
                                src={require("../images/upvotep.png")}
                                width="30"
                                height="30"
                                alt="upvote"
                            />
                        </button>
                    )}
            </div>

            {/* Terminan boton de upvote, va texto numerico */}
            <div className="d-flex justify-content-center">
                <p className="h5">{votes}</p>
            </div>

            {/* Arrancan botones de downvote */}

            <div>
                {myVote === false && (
                    <button className="clickable btn b-0 p-0" onClick={nullVote}>
                        <img
                            src={require("../images/voted.png")}
                            width="30"
                            height="30"
                            alt="downvote"
                        />
                    </button>
                )}
                {(myVote === true ||
                    myVote == null) && (
                        <button className="clickable btn b-0 p-0" onClick={downVote}>
                            <img
                                src={require("../images/downvotep.png")}
                                width="30"
                                height="30"
                                alt="downvote"
                            />
                        </button>
                    )}
            </div>
        </div>
    );
}
