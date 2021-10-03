package asia.cmg.f8.session.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import asia.cmg.f8.session.entity.credit.BasicWalletEntity;

public interface BasicWalletRepository extends JpaRepository<BasicWalletEntity, Long> {

	BasicWalletEntity findByOwnerUuid(final String ownerUuid);
}
