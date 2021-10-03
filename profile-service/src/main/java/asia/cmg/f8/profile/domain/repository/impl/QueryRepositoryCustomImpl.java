package asia.cmg.f8.profile.domain.repository.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import asia.cmg.f8.profile.domain.repository.QueryRepositoryCustom;

@Repository
public class QueryRepositoryCustomImpl implements QueryRepositoryCustom{

	@PersistenceContext
	private EntityManager entityManager;


	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getPTsByFilter(final String queryStr) {
		// TODO Auto-generated method stub
		Query query = entityManager.createNativeQuery(queryStr);
		return query.getResultList();
	}
}
