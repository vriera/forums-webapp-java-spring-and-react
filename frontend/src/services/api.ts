import axios from "axios";

export const api = axios.create({
  baseURL: `${process.env.PUBLIC_URL}/api`
});

api.interceptors.request.use(
  (config) =>{
    // console.log(config)
    if(window.localStorage.getItem("token")){
      if(config.headers){
          config.headers = {
            ...config.headers,
            Authorization:  window.localStorage.getItem("token"),
        }
      }else{
          config.headers = {
            Authorization:  window.localStorage.getItem("token"),
          }
      }
    } 
    return config;
   },
  (error) => {
    return Promise.reject(error);
  }

);

//Meme from the future
export const apiBaseURL = axios.create();

export const apiURLfromApi = axios.create();

apiURLfromApi.interceptors.request.use(
  (config) =>{
    // console.log(config)
    if(window.localStorage.getItem("token")){
      if(config.headers){
          config.headers = {
            ...config.headers,
            Authorization:  window.localStorage.getItem("token"),
        }
      }else{
          config.headers = {
            Authorization:  window.localStorage.getItem("token"),
          }
      }
    } 
    return config;
   },
  (error) => {
    return Promise.reject(error);
  }

);

export const updateToken = (token: string) => {
  window.localStorage.setItem("token", token);

};
  
export const removeToken = () => {
  window.localStorage.removeItem("token");
};



