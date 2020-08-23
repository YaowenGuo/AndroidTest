package com.example.test_retrofit.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;


public final class ResponseStatusInterceptor implements Interceptor {
    public static final String CHECK_HTTP_RESPONSE_CODE = "Check-Http-Response-Code: true";
    private static final String CHECK_HEADER = "Check-Http-Response-Code";
    private static final Gson gson = new Gson();
    private final Charset UTF8 = StandardCharsets.UTF_8;
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String value = request.headers().get(CHECK_HEADER);
        request = request.newBuilder().removeHeader(CHECK_HEADER).build();
        Response response =  chain.proceed(request);
        if ("true".equals(value)) {
            if (!response.isSuccessful()) {
                throw new ApiException(response.code(), response.message());
            }
            // Make sure the content is json.
            String contentType = response.headers().get("Content-Type");
            if (contentType != null && response.body() != null && contentType.contains("application/json")) {
//                JsonReader jsonReader = gson.newJsonReader(response.body().charStream());
                BaseRsp<Void> status = gson.fromJson(bodyContent(response.body()),new TypeToken<BaseRsp<Void>>() {}.getType());
                if (status != null && (status.getCode() != 1)) {
                    throw new ApiException(status.getCode(), status.getMsg() == null ? "" : status.getMsg());
                }
            }
        }
        return response;
    }


    protected String bodyContent(ResponseBody responseBody) throws IOException {
        if (responseBody == null) return null;

        long contentLength = responseBody.contentLength();
        BufferedSource source = responseBody.source();
        source.request(contentLength); // Buffer the entire body.
        Buffer buffer = source.getBuffer();

//        MediaType contentType = responseBody.contentType();
        if (contentLength != 0) {
            return buffer.clone().readString(UTF8);
        }
        return null;
    }
}
