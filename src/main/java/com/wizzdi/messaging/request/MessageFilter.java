package com.wizzdi.messaging.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.FilteringInformationHolder;
import com.flexicore.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessageFilter extends FilteringInformationHolder {

    private String contentLike;
    private String subjectLike;
    private Set<String> fromUsersIds=new HashSet<>();
    @JsonIgnore
    private List<User> fromUsers;
    private Set<String> toUsersIds=new HashSet<>();
    @JsonIgnore
    private List<User> toUsers;

    public String getContentLike() {
        return contentLike;
    }

    public <T extends MessageFilter> T setContentLike(String contentLike) {
        this.contentLike = contentLike;
        return (T) this;
    }

    public String getSubjectLike() {
        return subjectLike;
    }

    public <T extends MessageFilter> T setSubjectLike(String subjectLike) {
        this.subjectLike = subjectLike;
        return (T) this;
    }

    public Set<String> getFromUsersIds() {
        return fromUsersIds;
    }

    public <T extends MessageFilter> T setFromUsersIds(Set<String> fromUsersIds) {
        this.fromUsersIds = fromUsersIds;
        return (T) this;
    }

    @JsonIgnore
    public List<User> getFromUsers() {
        return fromUsers;
    }

    public <T extends MessageFilter> T setFromUsers(List<User> fromUsers) {
        this.fromUsers = fromUsers;
        return (T) this;
    }

    public Set<String> getToUsersIds() {
        return toUsersIds;
    }

    public <T extends MessageFilter> T setToUsersIds(Set<String> toUsersIds) {
        this.toUsersIds = toUsersIds;
        return (T) this;
    }

    @JsonIgnore
    public List<User> getToUsers() {
        return toUsers;
    }

    public <T extends MessageFilter> T setToUsers(List<User> toUsers) {
        this.toUsers = toUsers;
        return (T) this;
    }
}
