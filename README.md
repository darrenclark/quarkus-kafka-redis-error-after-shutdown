# kafka-redis-error-after-shutdown

⚠️ Not a bug, just found `quarkus.reactive-messaging.kafka.enable-graceful-shutdown-in-dev-and-test-mode=true`

---

Bug reproduction for crashes when shutting down a Quarkus SmallRye Message kafka consumer that uses Redis.

(Quarkus version 3.8.2)

## Important files

- `ErrorOccurredAfterShutdown.java` - reproduces the issue
- `KafkaPricesProducer.java` - only exists to send data to Kafka to trigger the bug 

## Reproduction steps

1. Clone down this repo
2. Run the app - `quarkus dev`
3. Wait until `SRMSG18256: Initialize record store for topic-partition 'prices-0' at position -1.` log messages shows up
4. Shutdown the app (Press Ctrl-C), and some stack traces should appear: 

```
2024-03-17 15:39:43,858 ERROR [io.sma.rea.mes.provider] (vert.x-worker-thread-1) SRMSG00200: The method ErrorOccurredAfterShutdown#consumePrices has thrown an exception [Error Occurred After Shutdown]: java.util.concurrent.CompletionException: CONNECTION_CLOSED
	at io.smallrye.mutiny.operators.uni.UniBlockingAwait.await(UniBlockingAwait.java:79)
	at io.smallrye.mutiny.groups.UniAwait.atMost(UniAwait.java:65)
	at io.quarkus.redis.runtime.datasource.BlockingStringCommandsImpl.set(BlockingStringCommandsImpl.java:121)
	at ErrorOccurredAfterShutdown.consumePrices(ErrorOccurredAfterShutdown.java:15)
	at ErrorOccurredAfterShutdown_ClientProxy.consumePrices(Unknown Source)
	at ErrorOccurredAfterShutdown_SmallRyeMessagingInvoker_consumePrices_e9bbe60388feedb2288a354be22161a38c1e7c05.invoke(Unknown Source)
	at io.smallrye.reactive.messaging.providers.AbstractMediator.lambda$invokeBlocking$15(AbstractMediator.java:190)
	at io.smallrye.context.impl.wrappers.SlowContextualSupplier.get(SlowContextualSupplier.java:21)
	at io.smallrye.mutiny.operators.uni.builders.UniCreateFromDeferredSupplier.subscribe(UniCreateFromDeferredSupplier.java:25)
	at io.smallrye.mutiny.operators.AbstractUni.subscribe(AbstractUni.java:36)
	at io.smallrye.mutiny.operators.uni.builders.UniCreateFromDeferredSupplier.subscribe(UniCreateFromDeferredSupplier.java:36)
	at io.smallrye.mutiny.operators.AbstractUni.subscribe(AbstractUni.java:36)
	at io.smallrye.mutiny.groups.UniSubscribe.withSubscriber(UniSubscribe.java:51)
	at io.smallrye.mutiny.groups.UniSubscribe.with(UniSubscribe.java:110)
	at io.smallrye.mutiny.groups.UniSubscribe.with(UniSubscribe.java:88)
	at io.vertx.mutiny.core.Context$1.handle(Context.java:172)
	at io.vertx.mutiny.core.Context$1.handle(Context.java:170)
	at io.vertx.core.impl.ContextImpl.lambda$executeBlocking$1(ContextImpl.java:190)
	at io.vertx.core.impl.ContextInternal.dispatch(ContextInternal.java:276)
	at io.vertx.core.impl.ContextImpl.lambda$internalExecuteBlocking$2(ContextImpl.java:209)
	at io.vertx.core.impl.TaskQueue.run(TaskQueue.java:76)
	at org.jboss.threads.ContextHandler$1.runWith(ContextHandler.java:18)
	at org.jboss.threads.EnhancedQueueExecutor$Task.run(EnhancedQueueExecutor.java:2513)
	at org.jboss.threads.EnhancedQueueExecutor$ThreadBody.run(EnhancedQueueExecutor.java:1538)
	at org.jboss.threads.DelegatingRunnable.run(DelegatingRunnable.java:29)
	at org.jboss.threads.ThreadLocalResettingRunnable.run(ThreadLocalResettingRunnable.java:29)
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
	at java.base/java.lang.Thread.run(Thread.java:1583)
Caused by: CONNECTION_CLOSED


2024-03-17 15:39:43,860 ERROR [io.sma.rea.mes.kafka] (vert.x-eventloop-thread-1) SRMSG18203: A message sent to channel `prices` has been nacked, fail-stop [Error Occurred After Shutdown]
2024-03-17 15:39:43,860 WARN  [io.sma.rea.mes.kafka] (vert.x-eventloop-thread-1) SRMSG18228: A failure has been reported for Kafka topics '[prices]': java.util.concurrent.CompletionException: CONNECTION_CLOSED
	at io.smallrye.mutiny.operators.uni.UniBlockingAwait.await(UniBlockingAwait.java:79)
	at io.smallrye.mutiny.groups.UniAwait.atMost(UniAwait.java:65)
	at io.quarkus.redis.runtime.datasource.BlockingStringCommandsImpl.set(BlockingStringCommandsImpl.java:121)
	at ErrorOccurredAfterShutdown.consumePrices(ErrorOccurredAfterShutdown.java:15)
	at ErrorOccurredAfterShutdown_ClientProxy.consumePrices(Unknown Source)
	at ErrorOccurredAfterShutdown_SmallRyeMessagingInvoker_consumePrices_e9bbe60388feedb2288a354be22161a38c1e7c05.invoke(Unknown Source)
	at io.smallrye.reactive.messaging.providers.AbstractMediator.lambda$invokeBlocking$15(AbstractMediator.java:190)
	at io.smallrye.context.impl.wrappers.SlowContextualSupplier.get(SlowContextualSupplier.java:21)
	at io.smallrye.mutiny.operators.uni.builders.UniCreateFromDeferredSupplier.subscribe(UniCreateFromDeferredSupplier.java:25)
	at io.smallrye.mutiny.operators.AbstractUni.subscribe(AbstractUni.java:36)
	at io.smallrye.mutiny.operators.uni.builders.UniCreateFromDeferredSupplier.subscribe(UniCreateFromDeferredSupplier.java:36)
	at io.smallrye.mutiny.operators.AbstractUni.subscribe(AbstractUni.java:36)
	at io.smallrye.mutiny.groups.UniSubscribe.withSubscriber(UniSubscribe.java:51)
	at io.smallrye.mutiny.groups.UniSubscribe.with(UniSubscribe.java:110)
	at io.smallrye.mutiny.groups.UniSubscribe.with(UniSubscribe.java:88)
	at io.vertx.mutiny.core.Context$1.handle(Context.java:172)
	at io.vertx.mutiny.core.Context$1.handle(Context.java:170)
	at io.vertx.core.impl.ContextImpl.lambda$executeBlocking$1(ContextImpl.java:190)
	at io.vertx.core.impl.ContextInternal.dispatch(ContextInternal.java:276)
	at io.vertx.core.impl.ContextImpl.lambda$internalExecuteBlocking$2(ContextImpl.java:209)
	at io.vertx.core.impl.TaskQueue.run(TaskQueue.java:76)
	at org.jboss.threads.ContextHandler$1.runWith(ContextHandler.java:18)
	at org.jboss.threads.EnhancedQueueExecutor$Task.run(EnhancedQueueExecutor.java:2513)
	at org.jboss.threads.EnhancedQueueExecutor$ThreadBody.run(EnhancedQueueExecutor.java:1538)
	at org.jboss.threads.DelegatingRunnable.run(DelegatingRunnable.java:29)
	at org.jboss.threads.ThreadLocalResettingRunnable.run(ThreadLocalResettingRunnable.java:29)
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
	at java.base/java.lang.Thread.run(Thread.java:1583)
Caused by: CONNECTION_CLOSED
```
