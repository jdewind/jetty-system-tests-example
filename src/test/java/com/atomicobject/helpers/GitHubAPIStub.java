package com.atomicobject.helpers;

import com.atomicobject.app.GitHubAPI;
import com.google.common.collect.Lists;

import java.util.List;

public class GitHubAPIStub implements GitHubAPI {
    @Override
    public List<String> followersFor(String gitHubHandle) {
        return Lists.newArrayList("follower 1", "follower 2");
    }
}
