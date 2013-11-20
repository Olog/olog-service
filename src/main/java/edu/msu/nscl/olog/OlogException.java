/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */

package edu.msu.nscl.olog;

import javax.ws.rs.core.Response;

/**
 * Olog Exception that creates the matching HTTP Response.
 *
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */
public class OlogException extends Exception {

    private Response.Status status;

    /**
     * Creates a new CFException with the specified HTTP return code for this request,
     * detail message and cause.
     *
     * @param status HTTP return code
     * @param message
     * @param cause
     */
    public OlogException(Response.Status status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    /**
     * Creates a new CFException with the specified HTTP return code for this request.
     * and detail message.
     *
     * @param status HTTP return code
     * @param message
     */
    public OlogException(Response.Status status, String message) {
        super(message);
        this.status = status;
    }

    private String responseMessage() {
        String msg = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"" +
                " \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">" +
                "<html><head><title>Olog - Error report</title></head>" +
                "<body><h1>HTTP Status " + this.status.getStatusCode() + " - " + this.status.getReasonPhrase() + "</h1><hr/>" +
                "<p><b>type</b> Status report</p>" +
                "<p><b>message</b></p>" +
                "<p><b>description</b>" + getMessage() + "</p>";
        if (this.getCause() != null) {
            msg = msg + "<p><b>caused by:</b></p><p>" + this.getCause().getMessage() + "</p>";
        }
        return msg + "<hr/><h3>Olog</h3></body></html>";
    }

    /**
     * Returns a HTTP Response object for this exception.
     * @return HTTP response
     */
    public Response toResponse() {
        return Response.status(status)
                    .entity(responseMessage())
                    .build();
    }

    /**
     * Returns the HTTP Response status code for this exception.
     * @return HTTP response
     */
    public Response.Status getResponseStatusString() {
        return status;
    }

    /**
     * Returns the HTTP Response status code for this exception.
     * @return HTTP response
     */
    public int getResponseStatusCode() {
        return status.getStatusCode();
    }
}
