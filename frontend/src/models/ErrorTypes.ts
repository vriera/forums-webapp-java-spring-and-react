export class ApiError extends Error{
    constructor(code: number, message: string){
        super(message);
        this.code = code;
        this.message = message;
    }
    code: number;
    message: string;
}

export class BadRequestError extends ApiError{
    constructor(message: string){
        super(400, message);
    }
}

export class UnauthorizedError extends ApiError{
    constructor(message: string){
        super(401, message);
    }
}

export class InternalServerError extends ApiError{
    constructor(message: string){
        super(500, message);
    }
}

export class ForbiddenError extends ApiError{
  constructor(message: string){
      super(403, message);
  }
}

export class NotFoundError extends ApiError{
  constructor(message: string){
      super(404, message);
  }
}

// Map of HTTP status codes to error classes
export const apiErrors = new Map([
    [400, BadRequestError],
    [401, UnauthorizedError],
    [403, ForbiddenError],
    [404, NotFoundError],
    [500, InternalServerError],

]);




