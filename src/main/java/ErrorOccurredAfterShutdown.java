import io.quarkus.redis.datasource.RedisDataSource;
import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class ErrorOccurredAfterShutdown {
    @Inject
    RedisDataSource redisDataSource;

    @Incoming("prices")
    @Blocking()
    public void consumePrices(double price) {
        redisDataSource.value(Double.class).set("key", price);
    }
}
