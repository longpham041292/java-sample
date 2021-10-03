package asia.cmg.f8.profile.domain.repository;

import java.util.List;

public interface QueryRepositoryCustom {

	// Filter PT info
	// process get PT filter By skills
	List<Object> getPTsByFilter(final String query);

}
