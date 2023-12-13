package ar.edu.itba.paw.services.utils;

public final class PaginationUtils {
    private PaginationUtils(){}
    public static final int PAGE_SIZE = 10;

    public static long getPagesFromTotal(int total){
        return (total % PAGE_SIZE == 0) ? total / PAGE_SIZE : (total / PAGE_SIZE) + 1;
    }
    public static long getPagesFromTotal(long total){
        return (total % PAGE_SIZE == 0) ? total / PAGE_SIZE : (total / PAGE_SIZE) + 1;
    }
}
