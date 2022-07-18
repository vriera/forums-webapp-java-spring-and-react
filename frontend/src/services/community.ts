import { api} from "./api";



export async function createCommunity( name : string , description: string){
    if(!window.localStorage.getItem("userId")){
        return;
    }
    let id = window.localStorage.getItem("userId")
    const resp = api.post(`/community/${id}` ,
     { name , description}
     );
    console.log(resp); 
}

