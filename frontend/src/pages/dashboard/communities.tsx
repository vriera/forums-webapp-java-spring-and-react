import { useState } from "react";
import Background from "../../components/Background";
import CommunitiesCard from "../../components/CommunitiesCard";
import DashboardCommunitiesPane from "../../components/DashboardCommunitiesPane";
import DashboardPane from "../../components/DashboardPane";
import { Community } from "../../models/CommunityTypes";
import { User, Notification } from "../../models/UserTypes";


const DashboardCommunitiesPage = (props: {user: User}) => {
    const [selectedCommunity, setSelectedCommunity] = useState(null as unknown as Community)
    const [moderatedCommunities] = useState(null as unknown as Community[])
    const [currentModeratedCommunityPage, setCurrentModeratedCommunityPage] = useState(1)
    const [moderatedCommunityPages] = useState(null as unknown as number)
    const [option, setOption] = useState('communities') 
    let auxNotification: Notification = {
        requests: 1,
        invites: 2,
        total: 3
    }
    return (
        <div>
        {/* <Navbar changeToLogin={setOptionToLogin} changeToSignin={setOptionToSignin}/> */}
        <div className="wrapper">
            <div className="section section-hero section-shaped pt-3">
                <Background/>

                <div className="row">
                    {/* COMMUNITIES SIDE PANE*/}
                    <div className="col-3">
                        <DashboardPane user={props.user} notifications={auxNotification} option={"communities"} optionCallback={setOption}/>
                    </div>

                    {/* CENTER PANE*/}
                    <div className="col-6">
                        {selectedCommunity &&
                            <DashboardCommunitiesPane selectedCommunity={selectedCommunity}/>
                        }
                    </div> 

                    {/* ASK QUESTION */}
                    <div className="col-3">
                        {moderatedCommunities && 
                            <CommunitiesCard 
                            communities={moderatedCommunities} selectedCommunity={selectedCommunity} selectedCommunityCallback={setSelectedCommunity} 
                            currentPage={currentModeratedCommunityPage} totalPages={moderatedCommunityPages/* FIXME: levantar de la API */} currentPageCallback={setCurrentModeratedCommunityPage}/>
                        }
                    </div>
                </div>
            </div>
        </div>
    </div>
    )
}

export default DashboardCommunitiesPage;