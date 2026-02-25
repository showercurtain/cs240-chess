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

public class GenericHandler<Rq, Rs> implements Handler {
    public interface EndpointHandler<Rq, Rs> {
        Rs run(AuthData auth, Rq request) throws ServiceException;
    }

    @FunctionalInterface
    public interface PublicServiceEndpoint<Rq, Rs> extends EndpointHandler<Rq, Rs> {
        @Override
        default Rs run(AuthData authData, Rq request) throws ServiceException {
            return run(request);
        }
        Rs run(Rq request) throws ServiceException;
    }

    @FunctionalInterface
    public interface AuthServiceEndpoint<Rq, Rs> extends EndpointHandler<Rq, Rs> {
    }

    @FunctionalInterface
    public interface EmptyAuthServiceEndpoint<Rs> extends EndpointHandler<Void, Rs> {
        @Override
        default Rs run(AuthData auth, Void request) throws ServiceException {
            return run(auth);
        }
        Rs run(AuthData auth) throws ServiceException;
    }

    @FunctionalInterface
    public interface EmptyServiceEndpoint<Rs> extends EndpointHandler<Void, Rs> {
        @Override
        default Rs run(AuthData auth, Void request) throws ServiceException {
            return run();
        }
        Rs run() throws ServiceException;
    }

    @FunctionalInterface
    public interface AuthVoidServiceEndpoint<Rq> extends EndpointHandler<Rq, Void> {
        @Override
        default Void run(AuthData auth, Rq request) throws ServiceException {
            exec(auth, request);
            return null;
        }

        void exec(AuthData auth, Rq request) throws ServiceException;
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

    Class<Rq> requestType;
    Class<Rs> responseType;
    boolean authenticated;
    
    EndpointHandler<Rq, Rs> handler;

    Gson gson;
    AuthDAO authDAO;

    public GenericHandler(
            Gson gson,
            AuthDAO authDAO,
            Class<Rq> requestType,
            Class<Rs> responseType,
            EndpointHandler<Rq, Rs> handler,
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
            Rq request = null;
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
            Rs response = handler.run(auth, request);
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
