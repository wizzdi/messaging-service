package com.wizzdi.messaging.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BaseclassRepository;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.messaging.model.*;
import com.wizzdi.messaging.request.MessageFilter;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Extension
public class MessageRepository implements Plugin {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private BaseclassRepository baseclassRepository;
	@Autowired
	private BasicRepository basicRepository;


	public List<Message> listAllMessages(MessageFilter MessageFilter, SecurityContextBase securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Message> q = cb.createQuery(Message.class);
		Root<Message> r = q.from(Message.class);
		List<Predicate> predicates = new ArrayList<>();
		addMessagePredicates(MessageFilter, cb, q, r, predicates, securityContext);
		q.select(r).where(predicates.toArray(Predicate[]::new));
		TypedQuery<Message> query = em.createQuery(q);
		BasicRepository.addPagination(MessageFilter, query);
		return query.getResultList();

	}

	public <T extends Message> void addMessagePredicates(MessageFilter messageFilter, CriteriaBuilder cb, CommonAbstractCriteria q, From<?, T> r, List<Predicate> predicates, SecurityContextBase securityContext) {
		
		if(messageFilter.getBasicPropertiesFilter()!=null){
			BasicRepository.addBasicPropertiesFilter(messageFilter.getBasicPropertiesFilter(),cb,q,r,predicates);

		}
		if(messageFilter.getChats()!=null&&!messageFilter.getChats().isEmpty()){
			Set<String> ids=messageFilter.getChats().stream().map(f->f.getId()).collect(Collectors.toSet());
			Join<T, Chat> join=r.join(Message_.chat);
			predicates.add(join.get(Chat_.id).in(ids));
		}

		if(messageFilter.getSenders()!=null&&!messageFilter.getSenders().isEmpty()){
			Set<String> ids=messageFilter.getSenders().stream().map(f->f.getId()).collect(Collectors.toSet());
			Join<T, ChatUser> join=r.join(Message_.sender);
			predicates.add(join.get(ChatUser_.id).in(ids));
		}


	}

	public long countAllMessages(MessageFilter MessageFilter, SecurityContextBase securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<Message> r = q.from(Message.class);
		List<Predicate> predicates = new ArrayList<>();
		addMessagePredicates(MessageFilter, cb, q, r, predicates, securityContext);
		q.select(cb.count(r)).where(predicates.toArray(Predicate[]::new));
		TypedQuery<Long> query = em.createQuery(q);
		return query.getSingleResult();

	}

	@Transactional
	public void merge(Object base) {
		basicRepository.merge(base);
	}

	@Transactional
	public void massMerge(List<?> toMerge) {
		basicRepository.massMerge(toMerge);
	}

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
		return baseclassRepository.listByIds(c, ids, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return baseclassRepository.listByIds(c, ids, baseclassAttribute, securityContext);
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
		return baseclassRepository.getByIdOrNull(id, c, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return baseclassRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <T extends Baseclass> List<T> findByIds(Class<T> c, Set<String> requested) {
		return baseclassRepository.findByIds(c, requested);
	}

	public <T> T findByIdOrNull(Class<T> type, String id) {
		return baseclassRepository.findByIdOrNull(type, id);
	}
}
