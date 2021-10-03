package asia.cmg.f8.common.dto;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import asia.cmg.f8.common.web.errorcode.ErrorCode;

public class ApiRespListObject<T> implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private ErrorCode status;
	private List<T> data = Collections.emptyList();
		
	public ErrorCode getStatus() {
		return status;
	}
	public void setStatus(ErrorCode status) {
		this.status = status;
	}
	public List<T> getData() {
		return data;
	}
	public void setData(List<T> data) {
		this.data = data;
	}
}
