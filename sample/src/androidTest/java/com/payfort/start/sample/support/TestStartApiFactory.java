package com.payfort.start.sample.support;

import android.support.annotation.NonNull;

import com.payfort.start.web.StartApi;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Some test implementation of {@link StartApi}.
 */
public class TestStartApiFactory {

    public static StartApi newOfflineStartApi() {
        OfflineCall<?> offlineCall = new OfflineCall<>();
        return newStartApiProxy(offlineCall);
    }

    public static StartApi new400ErrorsStartApi() {
        Errors400Call<?> errorCall = new Errors400Call<>();
        return newStartApiProxy(errorCall);
    }

    @NonNull
    private static StartApi newStartApiProxy(Call<?> responseCall) {
        InvocationHandler invocationHandler = new StartApiInvocationHandler(responseCall);
        return (StartApi) Proxy.newProxyInstance(TestStartApiFactory.class.getClassLoader(), new Class[]{StartApi.class}, invocationHandler);
    }

    private static class StartApiInvocationHandler implements InvocationHandler {

        private final Object responses;

        private StartApiInvocationHandler(Object responses) {
            this.responses = responses;
        }

        @Override
        public Object invoke(Object object, Method method, Object[] objects) throws Throwable {
            return responses;
        }
    }

    private abstract static class BaseCall<T> implements Call<T> {

        @Override
        public boolean isExecuted() {
            return false;
        }

        @Override
        public void cancel() {
        }

        @Override
        public boolean isCanceled() {
            return false;
        }

        @SuppressWarnings("CloneDoesntCallSuperClone") // Immutable object.
        @Override
        public Call<T> clone() {
            return this;
        }

        @Override
        public Request request() {
            return new Request.Builder().url("http://localhost").build();
        }
    }

    private static class OfflineCall<T> extends BaseCall<T> {

        @Override
        public Response<T> execute() throws IOException {
            throw new IOException("Offline");
        }

        @Override
        public void enqueue(Callback<T> callback) {
            callback.onFailure(this, new IOException("Offline"));
        }
    }

    private static class Errors400Call<T> extends BaseCall<T> {

        @Override
        public Response<T> execute() throws IOException {
            return newErrorResponse();
        }

        @Override
        public void enqueue(Callback<T> callback) {
            callback.onResponse(this, newErrorResponse());
        }

        private Response<T> newErrorResponse() {
            String rawResponse = "{\"error\": \"Invalid request\"}";
            MediaType responseType = MediaType.parse("application/json");
            ResponseBody responseBody = ResponseBody.create(responseType, rawResponse);
            return Response.error(400, responseBody);
        }
    }
}
