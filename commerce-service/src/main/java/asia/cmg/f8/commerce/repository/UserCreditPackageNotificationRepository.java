package asia.cmg.f8.commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import asia.cmg.f8.commerce.entity.credit.UserCreditPackageEntity;
import asia.cmg.f8.commerce.entity.credit.UserCreditPackagesNotificationsEntity;

public interface UserCreditPackageNotificationRepository
		extends JpaRepository<UserCreditPackagesNotificationsEntity, Long> {

	List<UserCreditPackagesNotificationsEntity> findByUserCreditPackage(UserCreditPackageEntity userCreditPackage);
}
