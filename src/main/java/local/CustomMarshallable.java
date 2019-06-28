package local;

import net.openhft.chronicle.wire.Marshallable;
import net.openhft.chronicle.wire.ValueIn;
import net.openhft.chronicle.wire.WireIn;
import net.openhft.chronicle.wire.Wires;
import org.jetbrains.annotations.NotNull;

public interface CustomMarshallable extends Marshallable {

    /// Don't overwrite initialized fields
    @Override
    default void readMarshallable(@NotNull WireIn wire) {
        Wires.readMarshallable(this, wire, false);
    }

    /// Crash on unexpected fields
    @Override
    default void unexpectedField(Object event, ValueIn valueIn) {
        throw new IllegalArgumentException(
            "Unexpected field " + event + ", was " + valueIn.object());
    }
}
