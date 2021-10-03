package asia.cmg.f8.common.dto;

import java.io.Serializable;
import asia.cmg.f8.common.web.errorcode.ErrorCode;

public class ApiRespObject<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private ErrorCode status;
	private T data = null;
	
	public ErrorCode getStatus() {
		return status;
	}
	public void setStatus(ErrorCode status) {
		this.status = status;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}	
}
