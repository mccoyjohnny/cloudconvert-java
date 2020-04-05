package com.cloudconvert.client;

import com.cloudconvert.client.api.key.ApiKeyProvider;
import com.cloudconvert.client.api.key.PropertiesFileApiKeyProvider;
import com.cloudconvert.client.api.url.ApiUrlProvider;
import com.cloudconvert.client.api.url.PropertiesFileApiUrlProvider;
import com.cloudconvert.client.http.AsyncCloseableHttpClientProvider;
import com.cloudconvert.client.mapper.ObjectMapperProvider;
import com.cloudconvert.dto.response.JobResponse;
import com.cloudconvert.dto.response.JobResponseData;
import com.cloudconvert.dto.response.OperationResponse;
import com.cloudconvert.dto.response.Pageable;
import com.cloudconvert.dto.response.TaskResponse;
import com.cloudconvert.dto.response.TaskResponseData;
import com.cloudconvert.dto.response.UserResponseData;
import com.cloudconvert.dto.response.WebhookResponse;
import com.cloudconvert.dto.response.WebhookResponseData;
import com.cloudconvert.dto.result.AsyncResult;
import com.cloudconvert.executor.AsyncRequestExecutor;
import com.cloudconvert.extractor.ResultExtractor;
import com.cloudconvert.resource.async.AsyncCaptureWebsitesResource;
import com.cloudconvert.resource.async.AsyncConvertFilesResource;
import com.cloudconvert.resource.async.AsyncCreateArchivesResource;
import com.cloudconvert.resource.async.AsyncExecuteCommandsResource;
import com.cloudconvert.resource.async.AsyncExportFilesResource;
import com.cloudconvert.resource.async.AsyncFilesResource;
import com.cloudconvert.resource.async.AsyncImportFilesResource;
import com.cloudconvert.resource.async.AsyncJobsResource;
import com.cloudconvert.resource.async.AsyncMergeFilesResource;
import com.cloudconvert.resource.async.AsyncOptimizeFilesResource;
import com.cloudconvert.resource.async.AsyncTasksResource;
import com.cloudconvert.resource.async.AsyncUsersResource;
import com.cloudconvert.resource.async.AsyncWebhookResource;

import java.io.IOException;
import java.io.InputStream;

public class AsyncCloudConvertClient extends AbstractCloudConvertClient<AsyncResult<Void>,
    AsyncResult<InputStream>, AsyncResult<TaskResponseData>, AsyncResult<Pageable<TaskResponse>>,
    AsyncResult<JobResponseData>, AsyncResult<Pageable<JobResponse>>, AsyncResult<WebhookResponseData>,
    AsyncResult<Pageable<WebhookResponse>>, AsyncResult<UserResponseData>, AsyncResult<Pageable<OperationResponse>>> {

    public AsyncCloudConvertClient() throws IOException {
        this(new PropertiesFileApiUrlProvider(), new PropertiesFileApiKeyProvider(), new ObjectMapperProvider());
    }

    public AsyncCloudConvertClient(
        final ApiUrlProvider apiUrlProvider, final ApiKeyProvider apiKeyProvider
    ) throws IOException {
        this(apiUrlProvider, apiKeyProvider, new ObjectMapperProvider());
    }

    public AsyncCloudConvertClient(
        final ApiUrlProvider apiUrlProvider, final ApiKeyProvider apiKeyProvider, final ObjectMapperProvider objectMapperProvider
    ) throws IOException {
        this(apiUrlProvider, apiKeyProvider, objectMapperProvider,
            new AsyncRequestExecutor(new ResultExtractor(objectMapperProvider), new AsyncCloseableHttpClientProvider())
        );
    }

    public AsyncCloudConvertClient(
        final ApiUrlProvider apiUrlProvider, final ApiKeyProvider apiKeyProvider,
        final ObjectMapperProvider objectMapperProvider, final AsyncRequestExecutor asyncRequestExecutor
    ) {
        this(apiUrlProvider, apiKeyProvider, objectMapperProvider, asyncRequestExecutor,
            new AsyncTasksResource(apiUrlProvider, apiKeyProvider, objectMapperProvider, asyncRequestExecutor,
                new AsyncConvertFilesResource(apiUrlProvider, apiKeyProvider, objectMapperProvider, asyncRequestExecutor),
                new AsyncOptimizeFilesResource(apiUrlProvider, apiKeyProvider, objectMapperProvider, asyncRequestExecutor),
                new AsyncCaptureWebsitesResource(apiUrlProvider, apiKeyProvider, objectMapperProvider, asyncRequestExecutor),
                new AsyncMergeFilesResource(apiUrlProvider, apiKeyProvider, objectMapperProvider, asyncRequestExecutor),
                new AsyncCreateArchivesResource(apiUrlProvider, apiKeyProvider, objectMapperProvider, asyncRequestExecutor),
                new AsyncExecuteCommandsResource(apiUrlProvider, apiKeyProvider, objectMapperProvider, asyncRequestExecutor)
            ),
            new AsyncJobsResource(apiUrlProvider, apiKeyProvider, objectMapperProvider, asyncRequestExecutor)
        );
    }

    public AsyncCloudConvertClient(
        final ApiUrlProvider apiUrlProvider, final ApiKeyProvider apiKeyProvider,
        final ObjectMapperProvider objectMapperProvider, final AsyncRequestExecutor asyncRequestExecutor,
        final AsyncTasksResource asyncTasksResource, final AsyncJobsResource asyncJobsResource
    ) {
        super(asyncTasksResource, asyncJobsResource,
            new AsyncImportFilesResource(apiUrlProvider, apiKeyProvider, objectMapperProvider, asyncRequestExecutor, asyncTasksResource),
            new AsyncExportFilesResource(apiUrlProvider, apiKeyProvider, objectMapperProvider, asyncRequestExecutor),
            new AsyncUsersResource(apiUrlProvider, apiKeyProvider, objectMapperProvider, asyncRequestExecutor),
            new AsyncWebhookResource(apiUrlProvider, apiKeyProvider, objectMapperProvider, asyncRequestExecutor),
            new AsyncFilesResource(apiUrlProvider, apiKeyProvider, objectMapperProvider, asyncRequestExecutor)
        );
    }
}