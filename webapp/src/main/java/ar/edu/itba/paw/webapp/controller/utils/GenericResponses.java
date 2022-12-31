package ar.edu.itba.paw.webapp.controller.utils;

import ar.edu.itba.paw.webapp.controller.dto.SuccessDto;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

public  class GenericResponses {

    private GenericResponses(){

    }

    public static Response notAuthorized(){
        return Response.status(Response.Status.UNAUTHORIZED).entity(
                new GenericEntity<SuccessDto>(SuccessDto.boolToSuccessDto(false , "Not authorized on behalf of this id") ){}
            ).build();
        }

    public static Response notAuthorized(String code){
        return Response.status(Response.Status.UNAUTHORIZED).entity(
                new GenericEntity<SuccessDto>(SuccessDto.boolToSuccessDto(false , code ) ){}
        ).build();
    }
    public static Response notAuthorized(String code , String message){
        return Response.status(Response.Status.UNAUTHORIZED).entity(
                new GenericEntity<SuccessDto>(SuccessDto.boolToSuccessDto(false , code , message) ){}
        ).build();
    }

    public static Response cantAccess(){
        return Response.status(Response.Status.FORBIDDEN).entity(
                new GenericEntity<SuccessDto>(SuccessDto.boolToSuccessDto(false , "cannot.access.content","Can't access this content") ){}
        ).build();
    }
    public static Response cantAccess(String code, String message){
        return Response.status(Response.Status.FORBIDDEN).entity(
                new GenericEntity<SuccessDto>(SuccessDto.boolToSuccessDto(false , code , message) ){}
        ).build();
    }

    public static Response notAModerator(){
        return cantAccess("not.a.moderator" , "Must be logged in as a moderator for the community");
    }
    public static Response serverError(){
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                new GenericEntity<SuccessDto>(SuccessDto.boolToSuccessDto(false , "internal.error","Internal Server Error") ){}
        ).build();
    }
    public static Response serverError(String code , String message){
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                new GenericEntity<SuccessDto>(SuccessDto.boolToSuccessDto(false , code , message) ){}
        ).build();
    }


    public static Response success(){
        return Response.ok(
                new GenericEntity<SuccessDto>(SuccessDto.boolToSuccessDto(true , null) ){}
        ).build();
    }

    public static Response badRequest(){
        return Response.status(Response.Status.BAD_REQUEST).entity(
                new GenericEntity<SuccessDto>(SuccessDto.boolToSuccessDto(false , "Bad request") ){}
        ).build();
    }

    public static Response badRequest(String code , String message ){
        return Response.status(Response.Status.BAD_REQUEST).entity(
                new GenericEntity<SuccessDto>(SuccessDto.boolToSuccessDto(false , code , message) ){}
        ).build();
    }

    public static Response notFound(){
        return Response.status(Response.Status.NOT_FOUND).entity(
                new GenericEntity<SuccessDto>(SuccessDto.boolToSuccessDto(false , "Not found") ){}
        ).build();
    }


    public static Response conflict(String code , String message){
        return Response.status(Response.Status.CONFLICT).entity(
                new GenericEntity<SuccessDto>(SuccessDto.boolToSuccessDto(false , code , message) ){}
        ).build();
    }





}
