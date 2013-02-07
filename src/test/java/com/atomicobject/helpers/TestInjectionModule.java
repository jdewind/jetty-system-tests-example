package com.atomicobject.helpers;

import com.atomicobject.app.GitHubAPI;
import com.atomicobject.app.modules.ApplicationInjectionModule;
import org.junit.Ignore;

@Ignore
public class TestInjectionModule extends ApplicationInjectionModule {
    @Override
    protected void configure() {
        super.configure();
    }

    @Override
    protected Class<? extends GitHubAPI> getGitHubAPIImpl() {
        return GitHubAPIStub.class;
    }
}
