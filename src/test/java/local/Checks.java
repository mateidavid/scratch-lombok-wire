package local;

import static local.Util.uncheckedCast;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import net.openhft.chronicle.wire.AbstractMarshallableCfg;
import net.openhft.chronicle.wire.WireIn;
import net.openhft.chronicle.wire.WireOut;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Checks extends AbstractMarshallableCfg {

    private static final Logger log = LoggerFactory.getLogger(Checks.class);

    public static class Check {

        private final String fieldName;
        private final String expectedClassSimpleName;
        private final Object expectedObject;

        Check(String fieldName, String expectedClassSimpleName, Object expectedObject) {
            this.fieldName = fieldName;
            this.expectedClassSimpleName = expectedClassSimpleName;
            this.expectedObject = expectedObject;
        }
    }

    private final List<Check> checkList = new ArrayList<>();

    @Override
    public void readMarshallable(@NotNull WireIn wire) {
        checkList.clear();

        while (wire.hasMore()) {
            List<Object> arg = uncheckedCast(wire.getValueIn().object());
            checkList.add(new Check(
                uncheckedCast(arg.get(0)),
                uncheckedCast(arg.get(1)),
                arg.size() > 2 ? arg.get(2) : null));
        }
    }

    @Override
    public void writeMarshallable(@NotNull WireOut wire) {
        List<Object> tmp = new ArrayList<>();
        checkList.forEach(check -> {
            tmp.add(check.fieldName);
            tmp.add(check.expectedClassSimpleName);
            tmp.add(check.expectedObject);
            wire.getValueOut().object(tmp);
            tmp.clear();
        });
    }

    void run(Object data) {
        checkList.forEach(check ->
            assertThatCode(
                () -> {
                    log.info("checking fieldName=[{}]", check.fieldName);

                    Field field = data.getClass().getField(check.fieldName);
                    Object object = field.get(data);

                    if (check.expectedClassSimpleName != null) {

                        assertThat(object)
                            .isNotNull();
                        assertThat(object.getClass().getSimpleName())
                            .isEqualTo(check.expectedClassSimpleName);

                        if (check.expectedObject != null) {

                            assertThat(object)
                                .isEqualTo(check.expectedObject);

                        }
                    } else {
                        assertThat(object)
                            .isNull();
                    }
                })
                .doesNotThrowAnyException());
    }
}
