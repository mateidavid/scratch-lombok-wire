package local;

import static local.Util.newLinkedList;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@FieldDefaults(level = AccessLevel.PUBLIC)
@Getter
@Setter
@Accessors(fluent = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class DataSuperBuilder implements CustomMarshallable {

    //
    // no init
    //

    List<Long> aList;

    @Singular("aSingularListElement")
    List<Long> aSingularList;

    //
    // init, no final
    //

    List<Long> anInitList = newLinkedList(0L);

    @Singular("anInitSingularListElement")
    List<Long> anInitSingularList = newLinkedList(0L);

    @Default
    List<Long> anInitDefaultList = newLinkedList(0L);

    //
    // final
    //

    List<Long> aFinalList = newLinkedList(0L);

    @Singular("aFinalSingularListElement")
    List<Long> aFinalSingularList = newLinkedList(0L);

    @Default
    List<Long> aFinalDefaultList = newLinkedList(0L);
}
