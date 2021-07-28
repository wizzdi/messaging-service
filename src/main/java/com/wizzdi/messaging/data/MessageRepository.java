package com.wizzdi.messaging.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BaseclassRepository;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.messaging.model.*;
import com.wizzdi.messaging.request.MessageFilter;
import com.wizzdi.messaging.response.UnreadMessagesSummaryItem;
import org.eclipse.persistence.internal.jpa.querydef.CriteriaBuilderImpl;
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
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
		q.select(r).where(predicates.toArray(Predicate[]::new)).orderBy(cb.asc(r.get(Message_.creationDate)));
		TypedQuery<Message> query = em.createQuery(q);
		BasicRepository.addPagination(MessageFilter, query);
		return query.getResultList();

	}

	public <T extends Message> void addMessagePredicates(MessageFilter messageFilter, CriteriaBuilder cb, CommonAbstractCriteria q, From<?, T> r, List<Predicate> predicates, SecurityContextBase securityContext) {
		
		if(messageFilter.getBasicPropertiesFilter()!=null){
			BasicRepository.addBasicPropertiesFilter(messageFilter.getBasicPropertiesFilter(),cb,q,r,predicates);

		}
		if(messageFilter.getAddressedTo()!=null&&!messageFilter.getAddressedTo().isEmpty()){
			Set<String> ids=messageFilter.getAddressedTo().stream().map(f->f.getId()).collect(Collectors.toSet());

			Join<T, Chat> join=r.join(Message_.chat);
			Join<T,ChatUser> senderJoin=r.join(Message_.sender);
			Join<Chat,ChatToChatUser> linkJoin=join.join(Chat_.chatToChatUsers);
			Join<ChatToChatUser,ChatUser> chatUserJoin=linkJoin.join(ChatToChatUser_.chatUser);
			Predicate participatesInChat = chatUserJoin.get(ChatUser_.id).in(ids);
			Predicate notSender = cb.not(senderJoin.get(ChatUser_.id).in(ids));
			predicates.add(cb.and(participatesInChat, notSender));
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
		if(messageFilter.getUnreadBy()!=null&&!messageFilter.getUnreadBy().isEmpty()){

			Set<String> ids=messageFilter.getUnreadBy().stream().map(f->f.getId()).collect(Collectors.toSet());
			Predicate pred = cb.or();
			for (String id : ids) {
				Expression<String> userField = cb.function("jsonb_extract_path_text",
						String.class,
						r.get("other"),
						cb.literal(Message.CHATUSERS_FIELD),
						cb.literal(id));
				pred=cb.or(pred,userField.isNull());
			}
			predicates.add(pred);

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

	public List<UnreadMessagesSummaryItem> getMessageSummary(MessageFilter messageFilter, SecurityContextBase securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UnreadMessagesSummaryItem> q = cb.createQuery(UnreadMessagesSummaryItem.class);
		Root<Message> r = q.from(Message.class);
		List<Predicate> predicates = new ArrayList<>();
		addMessagePredicates(messageFilter, cb, q, r, predicates, securityContext);
		Join<Message,Chat> chatJoin=r.join(Message_.chat);
		q.select(cb.construct(UnreadMessagesSummaryItem.class,chatJoin.get(Chat_.id),cb.count(r)))
				.where(predicates.toArray(Predicate[]::new))
				.groupBy(chatJoin.get(Chat_.id))
				.orderBy(cb.asc(chatJoin.get(Chat_.id)));
		TypedQuery<UnreadMessagesSummaryItem> query = em.createQuery(q);
		return query.getResultList();
	}
}
