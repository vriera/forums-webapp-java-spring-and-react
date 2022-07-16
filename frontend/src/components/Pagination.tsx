
const Pagination = (props: {totalPages: number, currentPage: number, setCurrentPageCallback: any}) => {

    const pages = Array.from({length: props.totalPages}, (_, index) => index + 1);

    function nextPageCondition(): string{
        return (props.currentPage == props.totalPages || props.totalPages == 1)? "disabled" : ""
    }

    function previousPageCondition(): string{
        return (props.currentPage == 1)? "disabled" : ""
    }

    return (

    <nav aria-label="Page navigation example" className="d-flex justify-content-center">
        <ul className="pagination">

            {/* <!-- FLECHITA DE PREVIOUS; QUEDA DISABLED SI ESTOY EN = --> */}
            <li className="page-item">
                <button className={"page-link " + previousPageCondition} onClick={ () => props.setCurrentPageCallback(props.currentPage-1)}>
                    <i className="fa fa-angle-left"></i>
                </button>
            </li>

            {/* <!-- NUMERICOS --> */}
            {
            pages.map((page: number) =>
            <li key={page} className={"page-item " + ((page == props.currentPage)? "active" : "")} >
                <button className="page-link" onClick={() => props.setCurrentPageCallback(page)}>
                    {page}
                </button>
            </li>
            )
            }

            { /*<!-- FLECHITA DE NEXT --> */}                            
            <li className="page-item">
                <button className={"page-link " + nextPageCondition} onClick={ () => props.setCurrentPageCallback(props.currentPage+1)} aria-label="Next">
                    <i className="fa fa-angle-right"></i>
                </button>
            </li>
        
        </ul>
    </nav>
    );
}

export default Pagination