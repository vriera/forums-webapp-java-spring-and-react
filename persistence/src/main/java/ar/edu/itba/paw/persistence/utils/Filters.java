package ar.edu.itba.paw.persistence.utils;

public enum Filters {
    NO_FILTER(0) ,
    HAS_ANSWERS(1) ,
    HAS_NO_ANSWERS(2) ,
    HAS_VERIFIED_ANSWER(3);
    private int value;
    Filters(int value){ this.value = value;}

    public int getValue(){
        return value;
    }
}
