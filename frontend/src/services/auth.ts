import { api , updateToken , removeToken} from "./api";
import {updateUserInfo} from './user'


export async function loginUser(email: string, password: string) {
    const response = await api.post("/login", { email, password });
    if(response.status  == 200){
        console.log(response.headers.Authorization || response.headers.authorization);
        updateToken(response.headers.Authorization || response.headers.authorization);
        if(response.data.user_url)
            updateUserInfo(new URL(response.data.user_url).pathname);
    }
    return response;
}

export function logout(): void{
    removeToken();
    window.localStorage.removeItem(
        "userId" 
    )
}

export async function registerUser(email: string, password: string, username: string) {
    const response = await api.post("/register", { email, password, username });
    return response;
}

const AuthService = {
    loginUser,
    registerUser
}

export default AuthService;
