package se.fk.rimfrost.adapter.identity.exception;

/**
 * Exception thrown by the identity adapter when the identity service returns an error
 * or an unexpected response.
 */
public class IdentityException extends Exception
{
   private final ErrorType errorType;

   /**
    * @param errorType the category of error
    * @param message   a description of the error
    */
   public IdentityException(ErrorType errorType, String message)
   {
      super(message);

      this.errorType = errorType;
   }

   /**
    * @param errorType the category of error
    * @param message   a description of the error
    * @param cause     the underlying exception
    */
   public IdentityException(ErrorType errorType, String message, Throwable cause)
   {
      super(message, cause);

      this.errorType = errorType;
   }

   /**
    * Returns the error type categorizing this exception.
    *
    * @return the error type
    */
   public ErrorType getErrorType()
   {
      return errorType;
   }

   /**
    * Categorizes the type of error returned by the identity service.
    */
   public enum ErrorType
   {
      NOT_FOUND, UNAUTHORIZED, BAD_REQUEST, SERVICE_UNAVAILABLE, UNEXPECTED_ERROR
   }
}
