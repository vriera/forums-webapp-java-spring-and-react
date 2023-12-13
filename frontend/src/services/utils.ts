
import { SearchProperties} from "../components/MainSearchPanel";

export function parseQueryParamsForHistory(q: SearchProperties) : string{
    let queries = "";
    if(q.query)
        queries = queries + `&query=${q.query}`
    if(q.order)
        queries = queries + `&order=${q.order}`
    if(q.filter)
        queries = queries + `&filter=${q.filter}`
    return queries
}