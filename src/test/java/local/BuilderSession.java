package local;

import static local.Util.uncheckedCast;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.openhft.chronicle.wire.AbstractMarshallableCfg;
import net.openhft.chronicle.wire.WireIn;
import net.openhft.chronicle.wire.WireOut;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuilderSession extends AbstractMarshallableCfg {

    private static final Logger log = LoggerFactory.getLogger(BuilderSession.class);

    public static class Call {

        final String methodName;
        final Object argument;

        Call(String methodName, Object argument) {
            this.methodName = methodName;
            this.argument = argument;
        }
    }

    private final List<Call> callList = new ArrayList<>();

    @Override
    public void readMarshallable(@NotNull WireIn wire) {
        callList.clear();

        while (wire.hasMore()) {
            List<Object> arg = uncheckedCast(wire.getValueIn().object());
            callList.add(new Call(uncheckedCast(arg.get(0)), arg.get(1)));
        }
    }

    @Override
    public void writeMarshallable(@NotNull WireOut wire) {
        List<Object> tmp = new ArrayList<>();
        callList.forEach(call -> {
            tmp.add(call.methodName);
            tmp.add(call.argument);
            wire.getValueOut().object(tmp);
            tmp.clear();
        });
    }

    void run(Object builder) {
        callList.forEach(call ->
            assertThatCode(
                () -> {
                    log.info("applying methodName=[{}] argumentClass=[{}]",
                        call.methodName, call.argument.getClass().getName());

                    Method method = Arrays.stream(builder.getClass().getDeclaredMethods())
                        .filter(m ->
                            m.getName().equals(call.methodName) &&
                                m.getParameterCount() == 1 &&
                                m.getParameterTypes()[0].isAssignableFrom(call.argument.getClass()))
                        .findFirst()
                        .get();
                    method.invoke(builder, call.argument);
                })
                .doesNotThrowAnyException());
    }
}
