package uk.gov.justice.services.core.interceptor;

import static uk.gov.justice.services.core.interceptor.DefaultInterceptorContext.interceptorContextWithInput;

import uk.gov.justice.services.messaging.JsonEnvelope;

import java.util.Optional;
import java.util.function.Function;

public class DefaultInterceptorChainProcessor implements InterceptorChainProcessor {

    private final InterceptorCache interceptorCache;
    private final Function<JsonEnvelope, JsonEnvelope> dispatch;

    DefaultInterceptorChainProcessor(final InterceptorCache interceptorCache, final Function<JsonEnvelope, JsonEnvelope> dispatch) {
        this.interceptorCache = interceptorCache;
        this.dispatch = dispatch;
    }

    @Override
    public Optional<JsonEnvelope> process(final InterceptorContext interceptorContext) {
        return new DefaultInterceptorChain(interceptorCache.getInterceptors(), targetOf(dispatch))
                .processNext(interceptorContext)
                .outputEnvelope();
    }

    @Override
    @Deprecated
    public Optional<JsonEnvelope> process(final JsonEnvelope jsonEnvelope) {
        return process(interceptorContextWithInput(jsonEnvelope));
    }

    private Target targetOf(final Function<JsonEnvelope, JsonEnvelope> dispatch) {
        return interceptorContext -> interceptorContext.copyWithOutput(dispatch.apply(interceptorContext.inputEnvelope()));
    }
}