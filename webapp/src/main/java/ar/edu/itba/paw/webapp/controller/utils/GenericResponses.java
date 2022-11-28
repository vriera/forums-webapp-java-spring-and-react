package ar.edu.itba.paw.webapp.controller.utils;

import ar.edu.itba.paw.webapp.dto.SuccessDto;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

public class GenericResponses {

    public static Response notAuthorized(){
        return Response.status(Response.Status.UNAUTHORIZED).entity(
                new GenericEntity<SuccessDto>(SuccessDto.boolToSuccessDto(false , "Not authorized on behalf of this id") ){}
            ).build();
        }

    public static Response cantAccess(){
        return Response.status(Response.Status.UNAUTHORIZED).entity(
                new GenericEntity<SuccessDto>(SuccessDto.boolToSuccessDto(false , "Can't access this content") ){}
        ).build();
    }


    public static Response serverError(){
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                new GenericEntity<SuccessDto>(SuccessDto.boolToSuccessDto(false , "Internal Server Error") ){}
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

    public static Response badRequest(String message){
        return Response.status(Response.Status.BAD_REQUEST).entity(
                new GenericEntity<SuccessDto>(SuccessDto.boolToSuccessDto(false , message) ){}
        ).build();
    }

    public static Response notFound(){
        return Response.status(Response.Status.NOT_FOUND).entity(
                new GenericEntity<SuccessDto>(SuccessDto.boolToSuccessDto(false , "Not found") ){}
        ).build();
    }






}
