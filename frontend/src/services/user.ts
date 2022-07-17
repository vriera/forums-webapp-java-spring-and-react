import { api , apiBaseURL ,  apiURLfromApi,} from './api' 

export async function updateUserInfo(userURI : string){
    let response = await  apiURLfromApi.get(userURI);
    let user  = response.data;
    console.log(window.localStorage.getItem("token"));
    console.log(user);
   
}

async function getUser(id: number){
    const response = await api.get(`/users/${id}`);
    if(response.status == 200){
        return response.data;
    }
}

export default {
    updateUserInfo,
    getUser
}