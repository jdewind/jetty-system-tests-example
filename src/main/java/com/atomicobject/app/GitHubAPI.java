package com.atomicobject.app;

import java.util.List;

public interface GitHubAPI {
    List<String> followersFor(String gitHubHandle);
}
