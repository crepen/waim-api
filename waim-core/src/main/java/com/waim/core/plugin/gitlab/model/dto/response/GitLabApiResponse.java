package com.waim.core.plugin.gitlab.model.dto.response;

import lombok.Getter;

@Getter
public class GitLabApiResponse <T> {
    private boolean state;
    private String message;
    private T data;

    public GitLabApiResponse(boolean state , T data){
        this.state = state;
        this.data = data;
    }

    public GitLabApiResponse(boolean state , T data , String message){
        this.state = state;
        this.data = data;
        this.message = message;
    }

    public GitLabApiResponse(boolean state , String message ){
        this.state = state;
        this.message = message;
    }
}
