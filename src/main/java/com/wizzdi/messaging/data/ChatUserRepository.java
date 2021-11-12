package com.wizzdi.messaging.data;

import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.SoftDeleteOption;
import com.wizzdi.messaging.model.ChatUser;
import com.wizzdi.messaging.model.ChatUser_;
import com.wizzdi.messaging.request.ChatUserFilter;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@Extension
public class ChatUserRepository implements Plugin {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SecuredBasicRepository securedBasicRepository;


    public List<ChatUser> listAllChatUsers(ChatUserFilter ChatUserFilter, SecurityContextBase securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ChatUser> q = cb.createQuery(ChatUser.class);
        Root<ChatUser> r = q.from(ChatUser.class);
        List<Predicate> predicates = new ArrayList<>();
        addChatUserPredicates(ChatUserFilter, cb, q, r, predicates, securityContext);
        q.select(r).where(predicates.toArray(Predicate[]::new));
        TypedQuery<ChatUser> query = em.createQuery(q);
        BasicRepository.addPagination(ChatUserFilter, query);
        return query.getResultList();

    }

    public <T extends ChatUser> void addChatUserPredicates(ChatUserFilter chatUserFilter, CriteriaBuilder cb, CommonAbstractCriteria q, From<?, T> r, List<Predicate> predicates, SecurityContextBase securityContext) {

        securedBasicRepository.addSecuredBasicPredicates(chatUserFilter.getBasicPropertiesFilter(),cb,q,r,predicates,securityContext);


    }

    public long countAllChatUsers(ChatUserFilter ChatUserFilter, SecurityContextBase securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<ChatUser> r = q.from(ChatUser.class);
        List<Predicate> predicates = new ArrayList<>();
        addChatUserPredicates(ChatUserFilter, cb, q, r, predicates, securityContext);
        q.select(cb.count(r)).where(predicates.toArray(Predicate[]::new));
        TypedQuery<Long> query = em.createQuery(q);
        return query.getSingleResult();

    }

    @Transactional
    public void merge(Object o) {
        em.merge(o);
    }

    @Transactional
    public void massMerge(List<Object> list) {
        for (Object o : list) {
            em.merge(o);
        }
    }

    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
        return securedBasicRepository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
        return securedBasicRepository.getByIdOrNull(id, c, securityContext);
    }

    public <T extends Baseclass> List<T> findByIds(Class<T> c, Set<String> requested) {
        return securedBasicRepository.findByIds(c, requested);
    }

    public <T extends ChatUser> T getChatUserByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
        return securedBasicRepository.getByIdOrNull(id, c, ChatUser_.security, securityContext);
    }
}
