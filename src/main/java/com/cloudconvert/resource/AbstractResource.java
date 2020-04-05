package com.cloudconvert.resource;

import com.cloudconvert.client.api.key.ApiKeyProvider;
import com.cloudconvert.client.api.url.ApiUrlProvider;
import com.cloudconvert.client.mapper.ObjectMapperProvider;
import com.cloudconvert.dto.request.Request;
import com.cloudconvert.dto.response.JobResponse;
import com.cloudconvert.dto.response.JobResponseData;
import com.cloudconvert.dto.response.OperationResponse;
import com.cloudconvert.dto.response.Pageable;
import com.cloudconvert.dto.response.TaskResponse;
import com.cloudconvert.dto.response.TaskResponseData;
import com.cloudconvert.dto.response.UserResponseData;
import com.cloudconvert.dto.response.WebhookResponse;
import com.cloudconvert.dto.response.WebhookResponseData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class AbstractResource implements Closeable {

    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_USER_AGENT = "User-Agent";

    public static final String V2 = "v2";
    public static final String BEARER = "Bearer";
    public static final String VALUE_USER_AGENT = "cloudconvert-java/v2 (https://github.com/cloudconvert/cloudconvert-java)";

    public static final TypeReference<Void> VOID_TYPE_REFERENCE = new TypeReference<Void>() {};
    public static final TypeReference<InputStream> INPUT_STREAM_TYPE_REFERENCE = new TypeReference<InputStream>() {};
    public static final TypeReference<Map<String, Object>> MAP_STRING_TO_OBJECT_TYPE_REFERENCE = new TypeReference<Map<String, Object>>() {};

    public static final TypeReference<TaskResponseData> TASK_RESPONSE_DATA_TYPE_REFERENCE = new TypeReference<TaskResponseData>() {};
    public static final TypeReference<JobResponseData> JOB_RESPONSE_DATA_TYPE_REFERENCE = new TypeReference<JobResponseData>() {};
    public static final TypeReference<UserResponseData> USER_RESPONSE_DATA_TYPE_REFERENCE = new TypeReference<UserResponseData>() {};
    public static final TypeReference<WebhookResponseData> WEBHOOKS_RESPONSE_DATA_TYPE_REFERENCE = new TypeReference<WebhookResponseData>() {};

    public static final TypeReference<Pageable<OperationResponse>> OPERATION_RESPONSE_PAGEABLE_TYPE_REFERENCE = new TypeReference<Pageable<OperationResponse>>() {};
    public static final TypeReference<Pageable<TaskResponse>> TASK_RESPONSE_PAGEABLE_TYPE_REFERENCE = new TypeReference<Pageable<TaskResponse>>() {};
    public static final TypeReference<Pageable<JobResponse>> JOB_RESPONSE_PAGEABLE_TYPE_REFERENCE = new TypeReference<Pageable<JobResponse>>() {};
    public static final TypeReference<Pageable<WebhookResponse>> WEBHOOKS_RESPONSE_PAGEABLE_TYPE_REFERENCE = new TypeReference<Pageable<WebhookResponse>>() {};

    private final ApiUrlProvider apiUrlProvider;
    private final ApiKeyProvider apiKeyProvider;
    private final ObjectMapperProvider objectMapperProvider;

    private final Map<Class<? extends HttpRequestBase>, Supplier<RequestBuilder>> requestBuilderProviders;

    public AbstractResource(
        final ApiUrlProvider apiUrlProvider, final ApiKeyProvider apiKeyProvider, final ObjectMapperProvider objectMapperProvider
    ) {
        this.apiUrlProvider = apiUrlProvider;
        this.apiKeyProvider = apiKeyProvider;
        this.objectMapperProvider = objectMapperProvider;

        this.requestBuilderProviders = ImmutableMap.<Class<? extends HttpRequestBase>, Supplier<RequestBuilder>>builder()
            .put(HttpGet.class, RequestBuilder::get).put(HttpPost.class, RequestBuilder::post)
            .put(HttpPut.class, RequestBuilder::put).put(HttpDelete.class, RequestBuilder::delete)
            .put(HttpHead.class, RequestBuilder::head).put(HttpOptions.class, RequestBuilder::options)
            .put(HttpPatch.class, RequestBuilder::patch).put(HttpTrace.class, RequestBuilder::trace).build();
    }

    protected URI getUri(
        final List<String> pathSegments
    ) throws URISyntaxException {
        return getUri(pathSegments, ImmutableList.of());
    }

    protected URI getUri(
        final List<String> pathSegments, final List<NameValuePair> nameValuePairs
    ) throws URISyntaxException {
        final List<String> v2PathSegments = ImmutableList.<String>builder().add(V2).addAll(pathSegments).build();

        return new URIBuilder(apiUrlProvider.provide()).setPathSegments(v2PathSegments).setParameters(nameValuePairs).build();
    }

    protected HttpEntity getHttpEntity(
        final Request request
    ) throws JsonProcessingException {
        return new ByteArrayEntity(objectMapperProvider.provide().writeValueAsBytes(request), ContentType.APPLICATION_JSON);
    }

    protected HttpUriRequest getHttpUriRequest(
        final Class<? extends HttpRequestBase> httpRequestBaseClass, final URI uri
    ) {
        return getHttpUriRequest(httpRequestBaseClass, uri, null);
    }

    protected HttpUriRequest getHttpUriRequest(
        final Class<? extends HttpRequestBase> httpRequestBaseClass, final URI uri, @Nullable final HttpEntity httpEntity
    ) {
        return requestBuilderProviders.get(httpRequestBaseClass).get().setUri(uri).setEntity(httpEntity)
            .setHeader(HEADER_USER_AGENT, VALUE_USER_AGENT).setHeader(HEADER_AUTHORIZATION, BEARER + " " + apiKeyProvider.provide()).build();
    }
}