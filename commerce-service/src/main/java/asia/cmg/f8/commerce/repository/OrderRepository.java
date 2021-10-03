package asia.cmg.f8.commerce.repository;

import asia.cmg.f8.commerce.dto.TopUpReportDto;
import asia.cmg.f8.commerce.entity.OrderEntity;
import asia.cmg.f8.commerce.entity.OrderSubscriptionEntryEntity;
import asia.cmg.f8.common.spec.order.OrderStatus;
import asia.cmg.f8.common.spec.order.PaymentStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
	
	final String GET_TOP_UP_REPORT_ALL_USER_QUERY
		= " SELECT "
		+ " NEW asia.cmg.f8.commerce.dto.TopUpReportDto("
		+ " orderEntity,"
		+ " userEntity"
		+ ")"		
		+ " FROM OrderEntity orderEntity"
		+ " INNER JOIN BasicUserEntity userEntity ON userEntity.uuid = orderEntity.userUuid "
	    + " WHERE orderEntity.createdDate BETWEEN :start AND :end "
	    + " AND ( orderEntity.type = 'CREDIT' OR  orderEntity.type = 'COUPON') "
	    + " AND  orderEntity.orderStatus = 'COMPLETED' ";
	final String SEARCH_KEYWORD = " AND (orderEntity.code LIKE %:keyword% OR userEntity.fullName LIKE %:keyword% OR userEntity.userName LIKE %:keyword% OR userEntity.email LIKE %:keyword% OR userEntity.userCode LIKE %:keyword% OR userEntity.phone LIKE %:keyword%)";

    @Query("select o.paymentStatus from OrderEntity o where o.uuid = ?1 and o.userUuid = ?2")
    Optional<PaymentStatus> findStatusOfOrder(final String uuid, final String userUuid);
    
    @Query("select o from OrderEntity o where o.uuid = ?1")
    Optional<OrderEntity> findOrderByUuid(final String orderUuid);
    
    @Query("select o from OrderEntity o where o.code = ?1")
    Optional<OrderEntity> findOrderByCode(final String code);

    @Query("select o from OrderEntity o where o.uuid = ?1 and o.userUuid = ?2")
    Optional<OrderEntity> findOrderByUserUuid(final String orderUuid, final String userUuid);

    @Query(
            value = "SELECT o.uuid, o.code, o.created_date, o.start_session_date, o.total_price, o.currency, oe.quantity, oe.expire_limit, su.full_name "
                    + "FROM orders o left join order_entries oe ON o.id = oe.order_id "
                    + "left join session_users su on o.pt_uuid = su.uuid "
                    + "where o.user_uuid = :userUuid and o.payment_status in :status order by o.created_date desc;", nativeQuery = true)
    List<Object[]> findOrdersByStatus(@Param("userUuid")  final String userUuid, @Param("status") final List<String> status);
    
    @Query("select o from OrderEntity o where o.userUuid = ?1 and o.ptUuid = ?2 "
            + "and o.createdDate > ?3 and o.orderStatus= ?4 and o.paymentStatus = ?5")
    List<OrderEntity> findNewOrdersByStatusAndDate(final String clientUuid, final String ptUuid,
            final LocalDateTime fromDate, final OrderStatus orderStatus,
            final PaymentStatus paymentStatus);

    @Query("select count(0) from OrderEntity o where o.userUuid = ?1 and o.ptUuid = ?2 "
            + "and o.createdDate > ?3 and o.paymentStatus = ?4")
    Integer findPendingOrderWithPt(final String clientUuid, final String ptUuid,
            final LocalDateTime fromDate, final PaymentStatus paymentStatus);
    
    @Modifying
    @Query("UPDATE OrderEntity o set o.orderStatus = :cancelOrderStatus, "
            + "o.paymentStatus = :cancelPaymentStatus WHERE o.userUuid = :userUuid "
            + "and o.ptUuid = :ptUuid and o.createdDate > :fromDate "
            + "and o.paymentStatus = :pendingStatus")
    int cancelPendingOrdersWithPt(@Param("userUuid") final String userUuid,
            @Param("ptUuid") final String ptUuid, @Param("fromDate") final LocalDateTime fromDate,
            @Param("pendingStatus") final PaymentStatus pendingStatus,
            @Param("cancelOrderStatus") final OrderStatus cancelOrderStatus,
            @Param("cancelPaymentStatus") final PaymentStatus cancelPaymentStatus);    
    
    @Query("select o from OrderEntity o where o.userUuid = ?1 and o.ptUuid = ?2 "
            + "and o.orderStatus= ?3 and o.paymentStatus = ?4")
    List<OrderEntity> findNewOrdersByStatus(final String clientUuid, final String ptUuid,
            final OrderStatus orderStatus, final PaymentStatus paymentStatus);
    
    @Query(value = "select os from OrderSubscriptionEntryEntity os where os.order_id = ?1", nativeQuery = true)
    List<OrderSubscriptionEntryEntity> findOrderSubscriptionEntriesByOrderId(final long orderId);
    
    
    @Query("select o.uuid from OrderEntity o where o.paymentStatus = ?1 and o.createdDate < ?2")
    List<String> findOrdersWithPaymentStatus(final PaymentStatus paymentStatus,
            final LocalDateTime toDate);
    
    @Modifying
	@Query(value = "UPDATE session_orders so SET so.expired_date = :expiredDate "
			+ "WHERE so.uuid = :uuid ", nativeQuery = true)
	void updateOrderExpiredDate(@Param("expiredDate") final LocalDateTime expiredDate,
			@Param("uuid") final String uuid);

	@Modifying
    @Query(
            value = "UPDATE contract_upload_info set contract_import=1 "
                    + "where contract_number in :contractNumbers", nativeQuery = true)
    void updateContractUploadStatus( @Param("contractNumbers") final List<String> contractNumbers);
	
	@Query(value = GET_TOP_UP_REPORT_ALL_USER_QUERY + SEARCH_KEYWORD)
	Page<TopUpReportDto> getTopUpReport( @Param("start")  LocalDateTime start, 
										  @Param("end")  LocalDateTime end,
										  @Param("keyword")  String keyword,
										Pageable pageable);
	@Query(value = "SELECT COUNT(orderEntity.id) FROM OrderEntity orderEntity "
		    + " WHERE ( orderEntity.type = 'CREDIT' OR  orderEntity.type = 'COUPON') "
		    + " AND orderEntity.paymentStatus IN :status "
		    + " AND orderEntity.userUuid = :uuid ")
	Integer checkSuccessOrdered(@Param("uuid") String uuid, @Param("status") List<PaymentStatus> status);
}
