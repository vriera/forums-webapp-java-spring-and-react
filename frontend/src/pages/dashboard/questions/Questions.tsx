import { useEffect, useState } from "react";
import AskQuestionPane from "../../../components/AskQuestionPane";
import Background from "../../../components/Background";
import DashboardPane from "../../../components/DashboardPane";
import DashboardQuestionPane from "../../../components/DashboardQuestionPane";
import { User } from "../../../models/UserTypes";
import { getUserFromApi } from "../../../services/user";
import { useNavigate } from "react-router-dom";


const DashboardQuestionsPage = () => {
    const navigate = useNavigate();
    const [user, setUser] = useState<User>();

    useEffect(() => {
        async function fetchUser() {
            const userId = parseInt(window.localStorage.getItem("userId") as string);
            
            try{
                let auxUser = await getUserFromApi(userId)
                setUser(auxUser)
            }catch{
                // TODO: Implement error page
                navigate("/error")            
            }
        }
        fetchUser();
    }, [navigate])

    
    return (
        <div>
            {/* <Navbar changeToLogin={setOptionToLogin} changeToSignin={setOptionToSignin}/> */}
            <div className="wrapper">
                <div className="section section-hero section-shaped pt-3">
                    <Background/>

                    <div className="row">
                        {/* COMMUNITIES SIDE PANE*/}
                        <div className="col-3">
                            {user &&
                                <DashboardPane user={user} option={"questions"} />
                            }                           
                        </div>

                        {/* CENTER PANE*/}
                        <div className="col-6">
                            <DashboardQuestionPane/>

                        </div> 

                        {/* ASK QUESTION */}
                        <div className="col-3">
                            <AskQuestionPane/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default DashboardQuestionsPage;
