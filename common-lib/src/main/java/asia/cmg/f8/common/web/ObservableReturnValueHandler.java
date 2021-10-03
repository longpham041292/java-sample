package asia.cmg.f8.common.web;


import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.method.support.AsyncHandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import rx.Observable;

import static org.springframework.web.context.request.async.WebAsyncUtils.getAsyncManager;

/**
 * Created on 12/26/16.
 */
public class ObservableReturnValueHandler implements AsyncHandlerMethodReturnValueHandler {

    @Override
    public boolean isAsyncReturnValue(final Object returnValue, final MethodParameter returnType) {
        return returnValue != null && supportsReturnType(returnType);
    }

    @Override
    public boolean supportsReturnType(final MethodParameter returnType) {
        return Observable.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    @SuppressWarnings("PMD")
    public void handleReturnValue(final Object returnValue, final MethodParameter returnType, final ModelAndViewContainer mavContainer, final NativeWebRequest webRequest) throws Exception {
        if (returnValue == null) {
            mavContainer.setRequestHandled(true);
            return;
        }

        // TODO set time out for DeferredResult
        final Observable<?> observable = Observable.class.cast(returnValue);
        getAsyncManager(webRequest).startDeferredResultProcessing(new ObservableAdapter<>(observable), mavContainer);
    }

    public class ObservableAdapter<T> extends DeferredResult<T> {
        public ObservableAdapter(final Observable<T> observable) {
            observable.subscribe(this::setResult, this::setErrorResult);
        }
    }
}
