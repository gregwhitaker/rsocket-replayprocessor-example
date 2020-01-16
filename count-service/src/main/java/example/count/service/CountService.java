package example.count.service;

import io.rsocket.AbstractRSocket;
import io.rsocket.ConnectionSetupPayload;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.SocketAcceptor;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;

import java.math.BigInteger;
import java.time.Duration;

public class CountService {
    private static final Logger LOG = LoggerFactory.getLogger(CountService.class);

    public static void main(String... args) throws Exception {
        final ReplayProcessor<Integer> replayProcessor = ReplayProcessor.create();
        final FluxSink<Integer> rpSink = replayProcessor.sink(FluxSink.OverflowStrategy.DROP);

        RSocketFactory.receive()
                .frameDecoder(PayloadDecoder.DEFAULT)
                .acceptor(new SocketAcceptor() {
                    @Override
                    public Mono<RSocket> accept(ConnectionSetupPayload setup, RSocket sendingSocket) {
                        return Mono.just(new AbstractRSocket() {
                            @Override
                            public Flux<Payload> requestStream(Payload payload) {
                                return replayProcessor.map(i -> DefaultPayload.create(BigInteger.valueOf(i).toByteArray()));
                            }
                        });
                    }
                })
                .transport(TcpServerTransport.create(7000))
                .start()
                .block();

        LOG.info("RSocket server started on port: 7000");

        Flux.range(1, 100)
                .delayElements(Duration.ofSeconds(1))
                .doOnComplete(rpSink::complete)
                .subscribe(rpSink::next);

        Thread.currentThread().join();
    }
}
