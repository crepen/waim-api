package com.waim.core.plugin.gitlab.service;

import com.fasterxml.jackson.databind.type.TypeFactory;
import com.waim.core.plugin.gitlab.model.dto.GitLabConnectOption;
import com.waim.core.plugin.gitlab.model.dto.response.GitLabApiResponse;
import com.waim.core.plugin.gitlab.model.error.GitLabApiUnknownException;
import com.waim.core.plugin.gitlab.model.error.GitLabConnException;
import com.waim.core.plugin.gitlab.model.error.GitLabPluginException;
import com.waim.core.plugin.gitlab.model.error.GitLabUnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.type.internal.ParameterizedTypeImpl;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpStatus;
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

    public <T> List<T> getDataList(String endpoint, GitLabConnectOption option, Class<T> responseClass) {
        ParameterizedTypeReference<List<T>> typeRef = ParameterizedTypeReference.forType(
                ResolvableType.forClassWithGenerics(List.class, responseClass).getType()
        );

        return this.webClient.get()
                .uri(endpoint)
                .header("PRIVATE-TOKEN", option.token())
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(typeRef);
                    } else {
                        return response.bodyToMono(Map.class)
                                .defaultIfEmpty(Map.of("message", "No error message provided"))
                                .flatMap(error -> {
                                    String msg = String.valueOf(Map.of("message", "Unknown GitLab API Error"));

                                    if(response.statusCode() == HttpStatus.UNAUTHORIZED){
                                        return Mono.error(new GitLabUnauthorizedException());
                                    }
                                    else{
                                        return Mono.error(new GitLabApiUnknownException(msg));
                                    }
                                });
                    }
                })
                .onErrorResume(e -> {
                    if (e instanceof GitLabPluginException) return Mono.error(e);
                    return Mono.error(new GitLabConnException(e));
                })
                .block();
    }

    public <T> T getData(String endpoint, GitLabConnectOption option, Class<T> responseClass) {
        return this.webClient.get()
                .uri(endpoint)
                .header("PRIVATE-TOKEN", option.token())
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(responseClass);
                    } else {
                        return response.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                                })
                                .defaultIfEmpty(Map.of("message", "No error message provided"))
                                .flatMap(error -> {
                                    String msg = String.valueOf(error.getOrDefault("message", "Unknown GitLab API Error"));

                                    if (response.statusCode() == HttpStatus.UNAUTHORIZED) {
                                        return Mono.error(new GitLabUnauthorizedException());
                                    } else {
                                        return Mono.error(new GitLabApiUnknownException(msg));
                                    }
                                });
                    }
                })
                .onErrorResume(e -> {
                    if (e instanceof GitLabPluginException) return Mono.error(e);
                    return Mono.error(new GitLabConnException(e));
                })
                .block();
    }

}
