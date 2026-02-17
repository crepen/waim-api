package com.waim.core.plugin.gitlab.service;

import com.waim.core.plugin.gitlab.model.dto.GitLabConnectOption;
import com.waim.core.plugin.gitlab.model.dto.response.GitLabApiResponse;
import com.waim.core.plugin.gitlab.model.error.GitLabConnException;
import com.waim.core.plugin.gitlab.model.error.GitLabPluginException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

@Slf4j
public class GitLabConnector {
    private final WebClient webClient;

    public GitLabConnector(String gitlabBaseUrl) {
        this.webClient = WebClient.builder().baseUrl(gitlabBaseUrl).build();
    }

    public <T> GitLabApiResponse<List<T>> getDataList(String endpoint, GitLabConnectOption option, Class<T> responseClass) {
        ParameterizedTypeReference<List<T>> typeRef = ParameterizedTypeReference.forType(
                new ParameterizedType() {
                    @Override public Type[] getActualTypeArguments() { return new Type[]{responseClass}; }
                    @Override public Type getRawType() { return List.class; }
                    @Override public Type getOwnerType() { return null; }
                }
        );

        return this.webClient.get()
                .uri(endpoint)
                .header("PRIVATE-TOKEN", option.token())
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(typeRef)
                                .map(data -> new GitLabApiResponse<>(true, data));
                    } else {
                        return response.bodyToMono(Map.class)
                                .defaultIfEmpty(Map.of("message", "No error message provided"))
                                .flatMap(error -> {
                                    String msg = String.valueOf(Map.of("message", "Unknown GitLab API Error"));
                                    return Mono.error(new GitLabPluginException(msg, response.statusCode().value()));
                                });
                    }
                })
                .onErrorResume(e -> {
                    if (e instanceof GitLabPluginException) return Mono.error(e);
                    return Mono.error(new GitLabConnException("GitLab Connection Failed: " + e.getMessage(), e));
                })
                .block();
    }

    public <T> GitLabApiResponse<T> getData(String endpoint, GitLabConnectOption option, Class<T> responseClass) {
        return this.webClient.get()
                .uri(endpoint)
                .header("PRIVATE-TOKEN", option.token())
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(responseClass)
                                .map(data -> new GitLabApiResponse<>(true, data));
                    } else {
                        return response.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                                .defaultIfEmpty(Map.of("message", "No error message provided"))
                                .flatMap(error -> {
                                    String msg = String.valueOf(error.getOrDefault("message", "Unknown GitLab API Error"));
                                    return Mono.error(new GitLabPluginException(msg, response.statusCode().value()));
                                });
                    }
                })
                .onErrorResume(e -> {
                    if (e instanceof GitLabPluginException) return Mono.error(e);
                    return Mono.error(new GitLabConnException("GitLab Connection Failed: " + e.getMessage(), e));
                })
                .block();
    }

}
