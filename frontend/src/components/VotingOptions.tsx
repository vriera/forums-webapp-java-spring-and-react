
import { useState, useEffect } from "react";


export default function VotingOptions(props: {userVote : boolean | undefined, votes: number, userId : number, id : number, 
    vote: (userId: number, id: number, vote: boolean) => Promise<any> ,
    deleteVote: (userId: number, id: number ) => Promise<any>}) {
    const [userVote, setuserVote] = useState(props.userVote);
    const [votes, setVotes] = useState(props.votes);

    async function upVote() {
        let newVotes = userVote === false ? votes + 2 : votes + 1;
        await props.vote(props.userId, props.id, true);
        setuserVote(true);
        setVotes(newVotes);
    }

    async function downVote() {
        let newVotes = userVote === true ? votes - 2 : votes - 1;
        await props.vote(props.userId, props.id, false);
        setuserVote(false);
        setVotes(newVotes);
    }

    async function nullVote() {
        let newVotes = userVote ? votes - 1 : votes + 1;
        await props.deleteVote(props.userId, props.id);
        setuserVote(undefined);
        setVotes(newVotes);
    }

    return (
        <div className="col-2 d-flex flex-column align-items-center">
            <div>
                {userVote === true && (
                    <button className="clickable btn b-0 p-0" onClick={nullVote}>
                        <img
                            src={require("../images/votes.png")}
                            width="30"
                            height="30"
                            alt="upvote"
                        />
                    </button>
                )}
                {(userVote == null ||
                    userVote === false) && (
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
                {userVote === false && (
                    <button className="clickable btn b-0 p-0" onClick={nullVote}>
                        <img
                            src={require("../images/voted.png")}
                            width="30"
                            height="30"
                            alt="downvote"
                        />
                    </button>
                )}
                {(userVote === true ||
                    userVote == null) && (
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
