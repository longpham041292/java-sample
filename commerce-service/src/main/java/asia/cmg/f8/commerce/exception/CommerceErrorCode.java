package asia.cmg.f8.commerce.exception;

import asia.cmg.f8.common.web.errorcode.ErrorCode;

public final class CommerceErrorCode {

    
    public static final ErrorCode PRODUCT_EXIST = new ErrorCode(4005, "PRODUCT_IS_EXISTED",
            "Product with request level is existed");
    public static final ErrorCode SUBSCRIBE_TYPE_EXIST = new ErrorCode(4005, "SUBSCRIBE_TYPE_IS_EXISTED",
            "Subscribe type with request level is existed");
    public static final ErrorCode ACTIVE_CONTRACT_EXIST = new ErrorCode(4006,
            "ACTIVE_CONTRACT_IS_EXISTED", "Active contract existed with current PT");
    public static final ErrorCode PROCESS_PAYMENT_ERROR = new ErrorCode(4007,
            "PROCESS_PAYMENT_ERROR", "Error when process payment for order");
    public static final ErrorCode PENDING_ORDER_ERROR = new ErrorCode(4008, "PENDING_ORDER_ERROR",
            "An order is in pending status");
    public static final ErrorCode COMMERCE_INTERNAL_ERROR = new ErrorCode(5002,
            "COMMERCE_INTERNAL_ERROR", "An internal error happen in the commerce service");
    
  private CommerceErrorCode(){
	  
  }  
}
