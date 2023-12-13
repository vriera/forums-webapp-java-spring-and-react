import axios, { AxiosRequestConfig } from "axios";

export const api = axios.create({
  baseURL: `${process.env.PUBLIC_URL}/api`,
});

function configureAxios(config: AxiosRequestConfig): AxiosRequestConfig {
  if (window.localStorage.getItem("token")) {
    if (config.headers) {
      config.headers = {
        ...config.headers,
        Authorization: window.localStorage.getItem("token"),
      };
    } else {
      config.headers = {
        Authorization: window.localStorage.getItem("token"),
      };
    }
  }
  return config;
}
api.interceptors.request.use(
  (config) => {
    return configureAxios(config);
  },
  (error) => {
    return Promise.reject(error);
  }
);

export const apiWithoutBaseUrl = axios.create();

apiWithoutBaseUrl.interceptors.request.use(
  (config) => {
    // TODO: Disables development CORS. Remove this in production.
    // config.url = config.url?.replace(`localhost:8080`, `localhost:3000`);
    return configureAxios(config);
  },
  (error) => {
    return Promise.reject(error);
  }
);

export type PaginationInfo = {
  current: number;
  total: number;
  prev?: number;
  next?: number;
  first?: number;
  uri: string;
};

export const noContentPagination: PaginationInfo = {
  current: 1,
  total: 0,
  uri: "",
};

export function getPaginationInfo(link: string, currentPage: number) {
  if (!link) {
    return {
      current: 0,
      total: 0,
      uri: "",
    };
  }
  let links = link.split(",");
  let lastPage = new URL(
    links
      .filter((x) => /rel="last"/.test(x))[0]
      .trim()
      .slice(1)
      .split(">")[0]
  ).searchParams.get("page");
  let url = new URL(links[0].trim().slice(1).split(">")[0]);

  let pageInfo: PaginationInfo = {
    current: currentPage,
    total: parseInt(lastPage as string),
    uri: url.origin + url.pathname,
  };
  let prevLink = links
    .filter((x) => /rel="prev"/.test(x))[0]
    ?.trim()
    .slice(1)
    .split(">")[0];
  let nextLink = links
    .filter((x) => /rel="next"/.test(x))[0]
    ?.trim()
    .slice(1)
    .split(">")[0];
  let firstLink = links
    .filter((x) => /rel="first"/.test(x))[0]
    ?.trim()
    .slice(1)
    .split(">")[0];

  if (prevLink) {
    let prevPage = new URL(prevLink).searchParams.get("page");
    if (prevPage) pageInfo.prev = parseInt(prevPage);
  }
  if (nextLink) {
    let nextPage = new URL(nextLink).searchParams.get("page");
    if (nextPage) pageInfo.next = parseInt(nextPage);
  }
  if (firstLink) {
    let firstPage = new URL(firstLink).searchParams.get("page");
    if (firstPage) pageInfo.first = parseInt(firstPage);
  }
  return pageInfo;
}
