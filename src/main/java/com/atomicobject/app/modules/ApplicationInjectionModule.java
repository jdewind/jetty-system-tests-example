package com.atomicobject.app.modules;

import com.atomicobject.app.GitHubAPI;
import com.atomicobject.app.GitHubAPIImpl;
import com.google.inject.AbstractModule;

public class ApplicationInjectionModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(GitHubAPI.class).to(getGitHubAPIImpl());
    }

    protected Class<? extends GitHubAPI> getGitHubAPIImpl() {
        return GitHubAPIImpl.class;
    }
}
