import { useTranslation } from "react-i18next";
import Background from "../../components/Background";
import { useNavigate, useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { getUser } from "../../services/user";
import { Spinner } from "react-bootstrap";
import { User } from "../../models/UserTypes";
import UserTabs from "../../components/UserTabs";

const UserProfilePane = () => {
    const { t } = useTranslation();

    const navigate = useNavigate();
    const [user, setUser] = useState<User>(undefined as unknown as User);
    let { userId } = useParams();

    useEffect(() => {
        async function fetchUser() {                    

            
            try{
                let auxUser = await getUser(parseInt(userId as string))
                setUser(auxUser)
            }catch(error){
                setUser(null as unknown as User);
            }
            
        }
        fetchUser();
    }, [navigate, userId])

    return (
        <div className="white-pill mt-5">
            <div className="card-body overflow-hidden">
                <p className="h3 text-primary text-center">{t("user.profile")}</p>
                <UserTabs activeTab="profile"/>
                {user === null &&
                    // Nothing to show
                    <div className="mt-3">
                        <p className="row h1 text-gray">{t("user.noUser")}</p>
                        <div className="d-flex justify-content-center">
                            <img className="row w-25 h-25" src={require('../../images/empty.png')} alt="No hay nada para mostrar"/>
                        </div>
                    </div>
                }
                { user === undefined &&
                    <div className="d-flex justify-content-center mt-5">
                        <Spinner/>
                    </div>
                }
                { user &&
                <div>
                    <div className="text-center">
                        <img className="rounded-circle" alt="User profile icon" src={"https://avatars.dicebear.com/api/avataaars/"+user.email+".svg"} style={{height: "80px", width: "80px"}}/> 
                    </div>
                    <p className="h1 text-center text-primary">{user.username}</p>
                    <p className="h4 text-center">{t('email')}: {user.email}</p>
                    <div className="d-flex justify-content-center">
                        <p className="h4 text-center">{t("profile.karma")}{user.karma && user.karma.karma}</p>
                        {user.karma && user.karma.karma > 0 &&
                            <div className="h4 mr-2 text-success mb-0 mt-1 ml-3">
                                <i className="fas fa-arrow-alt-circle-up"></i>
                            </div>
                        }
                        {user.karma && user.karma.karma < 0 &&
                            <div className="h4 mr-2 text-warning mb-0 mt-1 ml-3">
                                <i className="fas fa-arrow-alt-circle-down">{user.karma.karma}</i>
                            </div>
                        }
                    </div>
                </div>
                }
            </div>
        </div>
    )
}

const UserProfilePage = () => {

    return (
        <div>
            {/* <Navbar changeToLogin={setOptionToLogin} changeToSignin={setOptionToSignin}/> */}
            <div className="wrapper">
                <div className="section section-hero section-shaped pt-3">
                    <Background/>

                    <div className="row">
                        <div className="col-3">
                            
                        </div>

                        {/* CENTER PANE*/}
                        <div className="col-6">
                            <UserProfilePane/>
                        </div> 

                        <div className="col-3">
                            
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default UserProfilePage;