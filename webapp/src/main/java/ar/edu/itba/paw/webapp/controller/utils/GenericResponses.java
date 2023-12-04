package ar.edu.itba.paw.webapp.controller.utils;

import ar.edu.itba.paw.webapp.dto.errors.ErrorDto;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

public final class GenericResponses {

    private GenericResponses(){
        throw new UnsupportedOperationException();
    }

    public static String USERNAME_ALREADY_EXISTS = "username.already.exists";
    public static String EMAIL_ALREADY_EXISTS = "email.already.exists";
    public static String INCORRECT_CURRENT_PASSWORD = "incorrect.current.password";

    public static Response notAuthorized(){
        return Response.status(Response.Status.UNAUTHORIZED).entity(
                new GenericEntity<ErrorDto>(ErrorDto.boolToErrorDto(false , "Not authorized on behalf of this id") ){}
            ).build();
        }

    public static Response notAuthorized(String code){
        return Response.status(Response.Status.UNAUTHORIZED).entity(
                new GenericEntity<ErrorDto>(ErrorDto.boolToErrorDto(false , code ) ){}
        ).build();
    }
    public static Response notAuthorized(String code , String message){
        return Response.status(Response.Status.UNAUTHORIZED).entity(
                new GenericEntity<ErrorDto>(ErrorDto.boolToErrorDto(false , code , message) ){}
        ).build();
    }

    public static Response cantAccess(){
        return Response.status(Response.Status.FORBIDDEN).entity(
                new GenericEntity<ErrorDto>(ErrorDto.boolToErrorDto(false , "cannot.access.content","Can't access this content") ){}
        ).build();
    }
    public static Response cantAccess(String code, String message){
        return Response.status(Response.Status.FORBIDDEN).entity(
                new GenericEntity<ErrorDto>(ErrorDto.boolToErrorDto(false , code , message) ){}
        ).build();
    }

    public static Response notAModerator(){
        return cantAccess("not.a.moderator" , "Must be logged in as a moderator for the community");
    }
    public static Response serverError(){
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                new GenericEntity<ErrorDto>(ErrorDto.boolToErrorDto(false , "internal.error","Internal Server Error") ){}
        ).build();
    }
    public static Response serverError(String code , String message){
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                new GenericEntity<ErrorDto>(ErrorDto.boolToErrorDto(false , code , message) ){}
        ).build();
    }


    public static Response success(){
        return Response.ok(
                new GenericEntity<ErrorDto>(ErrorDto.boolToErrorDto(true , null) ){}
        ).build();
    }

    public static Response badRequest(){
        return Response.status(Response.Status.BAD_REQUEST).entity(
                new GenericEntity<ErrorDto>(ErrorDto.boolToErrorDto(false , "Bad request") ){}
        ).build();
    }

    public static Response badRequest(String code , String message ){
        return Response.status(Response.Status.BAD_REQUEST).entity(
                new GenericEntity<ErrorDto>(ErrorDto.boolToErrorDto(false , code , message) ){}
        ).build();
    }

    public static Response notFound(){
        return Response.status(Response.Status.NOT_FOUND).entity(
                new GenericEntity<ErrorDto>(ErrorDto.boolToErrorDto(false , "Not found") ){}
        ).build();
    }


    public static Response conflict(String code , String message){
        return Response.status(Response.Status.CONFLICT).entity(
                new GenericEntity<ErrorDto>(ErrorDto.boolToErrorDto(false , code , message) ){}
        ).build();
    }





}
