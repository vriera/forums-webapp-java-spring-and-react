package ar.edu.itba.paw.webapp.form;

public class PaginationForm {
    int limit = 5;
    int page = 1;

    PaginationForm(){}

    public int getPage() {
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