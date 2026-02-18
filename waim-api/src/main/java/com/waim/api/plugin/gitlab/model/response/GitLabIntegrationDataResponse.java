package com.waim.api.plugin.gitlab.model.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GitLabIntegrationDataResponse {
    @JsonProperty("last_pipeline")
    private Pipeline lastPipeline;

    @JsonProperty("last_commit")
    private Commit lastCommit;

    @Getter
    @Setter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Pipeline{
        @JsonProperty("state")
        private String state;

        @JsonProperty("created_at")
        private Long createdAt;
    }


    @Getter
    @Setter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Commit{
        @JsonProperty("id")
        private String id;

        @JsonProperty("created_at")
        private Long createdAt;

        private String message;

        @JsonProperty("web_url")
        private String webUrl;

    }
}
