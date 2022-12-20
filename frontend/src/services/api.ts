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



export type PaginationInfo = {
  current:number,
  total:number,
  prev?:number,
  next?:number,
  first?:number,
  uri: string
}

export const noContentPagination : PaginationInfo = {
  current: 1,
  total: 0,
  uri: ""
}

export function getPaginationInfo(link : string , currentPage: number){
  console.log("inside of pagination info");
  if(!link){
    return {
      current:0,
      total:0,
      uri : ""
    }
  }
 let links = link.split(",");
 //console.log(links);
 let lastPage = new URL(links.filter(x => /rel="last"/.test(x))[0].trim().slice(1).split('>')[0]).searchParams.get("page");
 let url =new URL(links[0].trim().slice(1).split('>')[0]);
 
 let pageInfo : PaginationInfo = { 
  current: currentPage,
  total: parseInt(lastPage as string),
  uri: url.origin + url.pathname
};
let prevLink = links.filter(x => /rel="prev"/.test(x))[0]?.trim().slice(1).split('>')[0];
let nextLink = links.filter(x => /rel="next"/.test(x))[0]?.trim().slice(1).split('>')[0];
let firstLink = links.filter(x => /rel="first"/.test(x))[0]?.trim().slice(1).split('>')[0];

if (prevLink){
  let prevPage =  new URL(prevLink).searchParams.get("page");
  if(prevPage) pageInfo.prev = parseInt(prevPage);
}
if(nextLink){
  let nextPage = new URL(nextLink).searchParams.get("page");
  if(nextPage) pageInfo.next = parseInt(nextPage);
}
if(firstLink){
  let firstPage = new URL(firstLink).searchParams.get("page");
  if(firstPage) pageInfo.first = parseInt(firstPage);
}
return pageInfo;
}