/**
 * 
 */
package asia.cmg.f8.commerce.constants;

import asia.cmg.f8.common.web.errorcode.ErrorCode;

/**
 * @author khoa.bui
 *
 */
public  class CommerceErrorConstantCode {

	public static final int COUPON_CODE_NOT_EXISTED = 191919;
	public static final int COUPON_CODE_REACH_LIMIT_INDIVIDUAL = 191920;
	public static final int COUPON_CODE_REACH_LIMIT_TOTAL = 191921;
	public static final int COUPON_CODE_INACTIVE = 191922;
	public static final int COUPON_CODE_VALID = 0;
	
	public static final ErrorCode COUPON_INVALID = new ErrorCode(5001, "INVALID", "Invalid");
	public static final ErrorCode COUPON_EXPIRED = new ErrorCode(5002, "EXPIRED", "Expired");
	public static final ErrorCode COUPON_INACTIVE = new ErrorCode(5003, "INACTIVE", "Inactive");
	public static final ErrorCode COUPON_USED = new ErrorCode(5004, "USED", "Used");
	public static final ErrorCode COUPON_NOT_FOUND = new ErrorCode(5005, "NOT_FOUND", "Not found");
	
	/**
     * Duplicated error codes
     */
    public static final CommerceErrorConstantCode COUPON_CODE_DUPLICATE = new CommerceErrorConstantCode(191923, "COUPON_CODE_DUPLICATE", "Coupon Code is duplicated");
	
	private static final long serialVersionUID = -3519702048844690464L;

    private final int code;
    private final String error;
    private final String detail;
	
    
   public CommerceErrorConstantCode(final int code,final String error,final String detail){
	   this.code = code;
       this.error = error;
       this.detail = detail;
   } 
   
   public int getCode() {
       return code;
   }

   public String getError() {
       return error;
   }

   public String getDetail() {
       return detail;
   }
   
   public CommerceErrorConstantCode withDetail(final String detail) {
       return new CommerceErrorConstantCode(this.code, this.error, detail);
   }

   public CommerceErrorConstantCode withError(final String error, final String detail) {
       return new CommerceErrorConstantCode(this.code, error, detail);
   }
   
}
