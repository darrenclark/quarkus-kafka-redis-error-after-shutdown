import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import java.time.Duration;
import java.util.Random;

/**
 * Write data to Kafka to reproduce the bug in ErrorOccurredAfterShutdown.java
 */
@ApplicationScoped
public class KafkaPricesProducer {
    private final Random random = new Random();

    @Outgoing("prices-out")
    public Multi<Double> generate() {
        return Multi.createFrom().ticks().every(Duration.ofMillis(50))
                .flatMap(x -> Multi.createFrom().range(0, 100).map(y -> random.nextDouble()));
    }
}
