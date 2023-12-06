package ar.edu.itba.paw.models.exceptions;

public class IllegalAnswersSearchArgumentException extends  IllegalArgumentException{
    public IllegalAnswersSearchArgumentException() {
        super("Arguments must be only one from: questionId or ownerId");
    }

}
