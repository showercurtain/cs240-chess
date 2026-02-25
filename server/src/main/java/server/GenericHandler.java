package server;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import dataaccess.AuthDAO;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.AuthData;
import org.jetbrains.annotations.NotNull;
import service.ServiceException;

import java.util.Map;

public class GenericHandler<Q, R> implements Handler {
    public interface EndpointHandler<Q, R> {
        R run(AuthData auth, Q request) throws ServiceException;
    }

    @FunctionalInterface
    public interface PublicServiceEndpoint<Q, R> extends EndpointHandler<Q, R> {
        @Override
        default R run(AuthData authData, Q request) throws ServiceException {
            return run(request);
        }
        R run(Q request) throws ServiceException;
    }

    @FunctionalInterface
    public interface AuthServiceEndpoint<Q, R> extends EndpointHandler<Q, R> {
    }

    @FunctionalInterface
    public interface EmptyAuthServiceEndpoint<R> extends EndpointHandler<Void, R> {
        @Override
        default R run(AuthData auth, Void request) throws ServiceException {
            return run(auth);
        }
        R run(AuthData auth) throws ServiceException;
    }

    @FunctionalInterface
    public interface EmptyServiceEndpoint<R> extends EndpointHandler<Void, R> {
        @Override
        default R run(AuthData auth, Void request) throws ServiceException {
            return run();
        }
        R run() throws ServiceException;
    }

    @FunctionalInterface
    public interface AuthVoidServiceEndpoint<Q> extends EndpointHandler<Q, Void> {
        @Override
        default Void run(AuthData auth, Q request) throws ServiceException {
            exec(auth, request);
            return null;
        }

        void exec(AuthData auth, Q request) throws ServiceException;
    }

    @FunctionalInterface
    public interface LogoutServiceEndpoint extends EndpointHandler<Void, Void> {
        @Override
        default Void run(AuthData auth, Void request) throws ServiceException {
            run(auth);
            return null;
        }
        void run(AuthData auth) throws ServiceException;
    }

    @FunctionalInterface
    public interface BlankEndpoint extends EndpointHandler<Void, Void> {
        @Override
        default Void run(AuthData auth, Void request) throws ServiceException {
            run();
            return null;
        }
        void run() throws ServiceException;
    }

    Class<Q> requestType;
    Class<R> responseType;
    boolean authenticated;
    
    EndpointHandler<Q, R> handler;

    Gson gson;
    AuthDAO authDAO;

    public GenericHandler(
            Gson gson,
            AuthDAO authDAO,
            Class<Q> requestType,
            Class<R> responseType,
            EndpointHandler<Q, R> handler,
            boolean authenticated
    ) {
        this.gson = gson;
        this.authDAO = authDAO;
        this.requestType = requestType;
        this.responseType = responseType;
        this.handler = handler;

        this.authenticated =
                (handler instanceof GenericHandler.AuthServiceEndpoint) ||
                (handler instanceof GenericHandler.AuthVoidServiceEndpoint) ||
                (handler instanceof GenericHandler.EmptyAuthServiceEndpoint) ||
                (handler instanceof LogoutServiceEndpoint) || authenticated;
    }

    @Override
    public void handle(@NotNull Context context) {
        context.contentType("application/json");
        try {
            AuthData auth = null;
            Q request = null;
            if (requestType != Void.class) {
                request = gson.fromJson(context.body(), requestType);
                if (request == null) {
                    throw new ServiceException("Bad Request").withError(400);
                }
            }
            if (authenticated) {
                String authToken = context.header("authorization");
                if (authToken == null) {
                    throw ServiceException.UNAUTHORIZED;
                }
                auth = authDAO.getAuth(authToken);
                if (auth == null) {
                    throw ServiceException.UNAUTHORIZED;
                }
            }
            R response = handler.run(auth, request);
            if (responseType != Void.class) {
                context.result(gson.toJson(response, responseType));
            }
        } catch (ServiceException e) {
            context.status(e.getHttpError());
            context.result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
        } catch (JsonParseException e) {
            context.status(400);
            context.result(gson.toJson(Map.of("message", "Error: Bad Request")));
        }
    }
}
