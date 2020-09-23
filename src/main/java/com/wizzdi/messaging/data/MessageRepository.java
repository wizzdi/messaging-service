package com.wizzdi.messaging.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.model.User;
import com.flexicore.model.User_;
import com.flexicore.security.SecurityContext;
import com.wizzdi.messaging.model.Message;
import com.wizzdi.messaging.model.Message_;
import com.wizzdi.messaging.request.MessageFilter;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Extension
@PluginInfo(version = 1)
@Component
public class MessageRepository extends AbstractRepositoryPlugin {
    public List<Message> listAllMessages(
            MessageFilter filtering, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Message> q = cb.createQuery(Message.class);
        Root<Message> r = q.from(Message.class);
        List<Predicate> preds = new ArrayList<>();
        addMessagePredicates(preds, r, cb, filtering);
        QueryInformationHolder<Message> queryInformationHolder = new QueryInformationHolder<>(filtering, Message.class, securityContext);
        return getAllFiltered(queryInformationHolder, preds, cb, q, r);

    }

    public long countAllMessages(MessageFilter filtering, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<Message> r = q.from(Message.class);
        List<Predicate> preds = new ArrayList<>();
        addMessagePredicates(preds, r, cb, filtering);
        QueryInformationHolder<Message> queryInformationHolder = new QueryInformationHolder<>(filtering, Message.class, securityContext);
        return countAllFiltered(queryInformationHolder, preds, cb, q, r);
    }

    private void addMessagePredicates(List<Predicate> preds, Root<Message> r, CriteriaBuilder cb, MessageFilter filtering) {
        if(filtering.getFromUsers()!=null &&!filtering.getFromUsers().isEmpty()){
            Set<String> fromIds=filtering.getFromUsers().stream().map(f->f.getId()).collect(Collectors.toSet());
            Join<Message, User> join=r.join(Message_.fromUser);
            preds.add(join.get(User_.id).in(fromIds));
        }

        if(filtering.getToUsers()!=null &&!filtering.getToUsers().isEmpty()){
            Set<String> fromIds=filtering.getToUsers().stream().map(f->f.getId()).collect(Collectors.toSet());
            Join<Message, User> join=r.join(Message_.toUser);
            preds.add(join.get(User_.id).in(fromIds));
        }
        if(filtering.getContentLike()!=null){
            preds.add(cb.like(r.get(Message_.content),filtering.getContentLike()));
        }
        if(filtering.getSubjectLike()!=null){
            preds.add(cb.like(r.get(Message_.subject),filtering.getSubjectLike()));
        }
    }
}
