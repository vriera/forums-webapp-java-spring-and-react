package ar.edu.itba.paw.webapp.form;

public class PaginationForm {
    private int limit = 5;
    private int page = 1;

    PaginationForm(){}

    public int getPage() {
        if(page < 0){
            return 1;
        }
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

}
