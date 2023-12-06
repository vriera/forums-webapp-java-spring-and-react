package ar.edu.itba.paw.models.exceptions;

public class IllegalAnswersSearchArgumentException extends  ParametrizedIllegalArgumentException{
    public IllegalAnswersSearchArgumentException() {
        super("search.answer.illegal.argument");
    }

}
