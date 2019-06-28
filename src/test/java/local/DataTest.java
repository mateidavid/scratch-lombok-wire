package local;

import static java.util.Objects.requireNonNull;

import net.openhft.chronicle.wire.Marshallable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DataTest {

    private static final Logger log = LoggerFactory.getLogger(DataTest.class);

    @ParameterizedTest
    @CsvSource({

        //
        // DataBuilder
        //

        // wire default value
        //
        ""
            + "'!local.DataBuilder {}', "
            + "'', "
            + "'!Checks {"
            + "  [ aList,               !!null              ],"
            + "  [ aSingularList,       !!null              ],"
            + "  [ anInitList,          LinkedList, [ 0 ]   ],"
            + "  [ anInitSingularList,  LinkedList, [ 0 ]   ],"
            + "  [ anInitDefaultList,   LinkedList, [ 0 ]   ],"
            + "  [ aFinalList,          LinkedList, [ 0 ]   ],"
            + "  [ aFinalSingularList,  LinkedList, [ 0 ]   ],"
            + "  [ aFinalDefaultList,   LinkedList, [ 0 ]   ],"
            + " }'",

        // wire singletons
        //
        // (1) non-initialized lists: initialized to ArrayList
        //
        // (2) initialized lists: wire maintains initialization container (LinkedList)
        //
        ""
            + "'!local.DataBuilder { "
            + "  aList:                 [ 1 ],"
            + "  aSingularList:         [ 1 ],"
            + "  anInitList:            [ 1 ],"
            + "  anInitSingularList:    [ 1 ],"
            + "  anInitDefaultList:     [ 1 ],"
            + "  aFinalList:            [ 1 ],"
            + "  aFinalSingularList:    [ 1 ],"
            + "  aFinalDefaultList:     [ 1 ],"
            + " }', "
            + "'', "
            + "'!Checks {"
            + "  [ aList,               ArrayList,  [ 1 ] ],"   // (1)
            + "  [ aSingularList,       ArrayList,  [ 1 ] ],"   // (1)
            + "  [ anInitList,          LinkedList, [ 1 ] ],"   // (2)
            + "  [ anInitSingularList,  LinkedList, [ 1 ] ],"   // (2)
            + "  [ anInitDefaultList,   LinkedList, [ 1 ] ],"   // (2)
            + "  [ aFinalList,          LinkedList, [ 1 ] ],"   // (2)
            + "  [ aFinalSingularList,  LinkedList, [ 1 ] ],"   // (2)
            + "  [ aFinalDefaultList,   LinkedList, [ 1 ] ],"   // (2)
            + " }'",

        // empty builder() session
        //
        // (3) @Singular lists: initialized to EmptyList
        //
        // (4) non-@Singular non-@Default initialized lists: initialized to null
        //
        ""
            + "'!type local.DataBuilder', "
            + "'!BuilderSession {}', "
            + "'!Checks {"
            + "  [ aList,               !!null              ],"
            + "  [ aSingularList,       EmptyList,  []      ]," // (3)
            + "  [ anInitList,          !!null              ]," // (4)
            + "  [ anInitSingularList,  EmptyList,  []      ]," // (3)
            + "  [ anInitDefaultList,   LinkedList, [ 0 ]   ],"
            + "  [ aFinalList,          !!null              ]," // (4)
            + "  [ aFinalSingularList,  EmptyList,  []      ]," // (3)
            + "  [ aFinalDefaultList,   LinkedList, [ 0 ]   ],"
            + " }'",

        // wire default + empty builder() session
        //
        // (5/6) @Singular lists holding singleton LinkedList/ArrayList: cast to SingletonList
        //
        ""
            + "'!local.DataBuilder {}', "
            + "'!BuilderSession {}', "
            + "'!Checks {"
            + "  [ aList,               !!null                  ],"
            + "  [ aSingularList,       EmptyList,      []      ]," // (3)
            + "  [ anInitList,          LinkedList,     [ 0 ]   ],"
            + "  [ anInitSingularList,  SingletonList,  [ 0 ]   ]," // (5)
            + "  [ anInitDefaultList,   LinkedList,     [ 0 ]   ],"
            + "  [ aFinalList,          LinkedList,     [ 0 ]   ],"
            + "  [ aFinalSingularList,  SingletonList,  [ 0 ]   ]," // (5)
            + "  [ aFinalDefaultList,   LinkedList,     [ 0 ]   ],"
            + " }'",

        // wire singletons + empty builder() session
        //
        ""
            + "'!local.DataBuilder { "
            + "  aList:                 [ 1 ],"
            + "  aSingularList:         [ 1 ],"
            + "  anInitList:            [ 1 ],"
            + "  anInitSingularList:    [ 1 ],"
            + "  anInitDefaultList:     [ 1 ],"
            + "  aFinalList:            [ 1 ],"
            + "  aFinalSingularList:    [ 1 ],"
            + "  aFinalDefaultList:     [ 1 ],"
            + " }', "
            + "'!BuilderSession {}', "
            + "'!Checks {"
            + "  [ aList,               ArrayList,      [ 1 ]   ],"
            + "  [ aSingularList,       SingletonList,  [ 1 ]   ]," // (6)
            + "  [ anInitList,          LinkedList,     [ 1 ]   ],"
            + "  [ anInitSingularList,  SingletonList,  [ 1 ]   ]," // (5)
            + "  [ anInitDefaultList,   LinkedList,     [ 1 ]   ],"
            + "  [ aFinalList,          LinkedList,     [ 1 ]   ],"
            + "  [ aFinalSingularList,  SingletonList,  [ 1 ]   ]," // (5)
            + "  [ aFinalDefaultList,   LinkedList,     [ 1 ]   ],"
            + " }'",

        // wire default + empty builder() session
        //
        // (7) non-@Singular lists used in builder session retain type (ArrayList fro Wire)
        //
        // (8) for init @Singular lists, the build session continues with existing element
        //
        // (9) @Singular lists are cast into UnmodifiableRandomAccessList
        //
        ""
            + "'!local.DataBuilder {}', "
            + "'!BuilderSession {"
            + "  [ aList,               [ 1, 2 ]    ],"
            + "  [ aSingularList,       [ 1, 2 ]    ],"
            + "  [ anInitList,          [ 1, 2 ]    ],"
            + "  [ anInitSingularList,  [ 1, 2 ]    ],"
            + "  [ anInitDefaultList,   [ 1, 2 ]    ],"
            + "  [ aFinalList,          [ 1, 2 ]    ],"
            + "  [ aFinalSingularList,  [ 1, 2 ]    ],"
            + "  [ aFinalDefaultList,   [ 1, 2 ]    ],"
            + " }', "
            + "'!Checks {"
            + "  [ aList,               ArrayList,                      [ 1, 2 ]    ]," // (7)
            + "  [ aSingularList,       UnmodifiableRandomAccessList,   [ 1, 2 ]    ]," // (9)
            + "  [ anInitList,          ArrayList,                      [ 1, 2 ]    ]," // (7)
            + "  [ anInitSingularList,  UnmodifiableRandomAccessList,   [ 0, 1, 2 ] ]," // (8,9)
            + "  [ anInitDefaultList,   ArrayList,                      [ 1, 2 ]    ]," // (7)
            + "  [ aFinalList,          ArrayList,                      [ 1, 2 ]    ]," // (7)
            + "  [ aFinalSingularList,  UnmodifiableRandomAccessList,   [ 0, 1, 2 ] ]," // (8,9)
            + "  [ aFinalDefaultList,   ArrayList,                      [ 1, 2 ]    ]," // (7)
            + " }'",

        //
        // DataSuperBuilder
        //

        // wire default value
        //
        ""
            + "'!local.DataSuperBuilder {}', "
            + "'', "
            + "'!Checks {"
            + "  [ aList,               !!null              ],"
            + "  [ aSingularList,       !!null              ],"
            + "  [ anInitList,          LinkedList, [ 0 ]   ],"
            + "  [ anInitSingularList,  LinkedList, [ 0 ]   ],"
            + "  [ anInitDefaultList,   LinkedList, [ 0 ]   ],"
            + "  [ aFinalList,          LinkedList, [ 0 ]   ],"
            + "  [ aFinalSingularList,  LinkedList, [ 0 ]   ],"
            + "  [ aFinalDefaultList,   LinkedList, [ 0 ]   ],"
            + " }'",

        // wire singletons
        //
        ""
            + "'!local.DataSuperBuilder { "
            + "  aList:                 [ 1 ],"
            + "  aSingularList:         [ 1 ],"
            + "  anInitList:            [ 1 ],"
            + "  anInitSingularList:    [ 1 ],"
            + "  anInitDefaultList:     [ 1 ],"
            + "  aFinalList:            [ 1 ],"
            + "  aFinalSingularList:    [ 1 ],"
            + "  aFinalDefaultList:     [ 1 ],"
            + " }', "
            + "'', "
            + "'!Checks {"
            + "  [ aList,               ArrayList,  [ 1 ] ],"   // (1)
            + "  [ aSingularList,       ArrayList,  [ 1 ] ],"   // (1)
            + "  [ anInitList,          LinkedList, [ 1 ] ],"   // (2)
            + "  [ anInitSingularList,  LinkedList, [ 1 ] ],"   // (2)
            + "  [ anInitDefaultList,   LinkedList, [ 1 ] ],"   // (2)
            + "  [ aFinalList,          LinkedList, [ 1 ] ],"   // (2)
            + "  [ aFinalSingularList,  LinkedList, [ 1 ] ],"   // (2)
            + "  [ aFinalDefaultList,   LinkedList, [ 1 ] ],"   // (2)
            + " }'",

        // empty builder() session
        //
        ""
            + "'!type local.DataSuperBuilder', "
            + "'!BuilderSession {}', "
            + "'!Checks {"
            + "  [ aList,               !!null              ],"
            + "  [ aSingularList,       EmptyList,  []      ]," // (3)
            + "  [ anInitList,          !!null              ]," // (4)
            + "  [ anInitSingularList,  EmptyList,  []      ]," // (3)
            + "  [ anInitDefaultList,   LinkedList, [ 0 ]   ],"
            + "  [ aFinalList,          !!null              ]," // (4)
            + "  [ aFinalSingularList,  EmptyList,  []      ]," // (3)
            + "  [ aFinalDefaultList,   LinkedList, [ 0 ]   ],"
            + " }'",

        // wire default + empty builder() session
        //
        ""
            + "'!local.DataSuperBuilder {}', "
            + "'!BuilderSession {}', "
            + "'!Checks {"
            + "  [ aList,               !!null                  ],"
            + "  [ aSingularList,       EmptyList,      []      ]," // (3)
            + "  [ anInitList,          LinkedList,     [ 0 ]   ],"
            + "  [ anInitSingularList,  SingletonList,  [ 0 ]   ]," // (5)
            + "  [ anInitDefaultList,   LinkedList,     [ 0 ]   ],"
            + "  [ aFinalList,          LinkedList,     [ 0 ]   ],"
            + "  [ aFinalSingularList,  SingletonList,  [ 0 ]   ]," // (5)
            + "  [ aFinalDefaultList,   LinkedList,     [ 0 ]   ],"
            + " }'",

        // wire singletons + empty builder() session
        //
        ""
            + "'!local.DataSuperBuilder { "
            + "  aList:                 [ 1 ],"
            + "  aSingularList:         [ 1 ],"
            + "  anInitList:            [ 1 ],"
            + "  anInitSingularList:    [ 1 ],"
            + "  anInitDefaultList:     [ 1 ],"
            + "  aFinalList:            [ 1 ],"
            + "  aFinalSingularList:    [ 1 ],"
            + "  aFinalDefaultList:     [ 1 ],"
            + " }', "
            + "'!BuilderSession {}', "
            + "'!Checks {"
            + "  [ aList,               ArrayList,      [ 1 ]   ],"
            + "  [ aSingularList,       SingletonList,  [ 1 ]   ]," // (6)
            + "  [ anInitList,          LinkedList,     [ 1 ]   ],"
            + "  [ anInitSingularList,  SingletonList,  [ 1 ]   ]," // (5)
            + "  [ anInitDefaultList,   LinkedList,     [ 1 ]   ],"
            + "  [ aFinalList,          LinkedList,     [ 1 ]   ],"
            + "  [ aFinalSingularList,  SingletonList,  [ 1 ]   ]," // (5)
            + "  [ aFinalDefaultList,   LinkedList,     [ 1 ]   ],"
            + " }'",

        // wire default + empty builder() session
        //
        // builder session does not work with SuperBuilder, no methods found
        //
        ""
            + "'!local.DataSuperBuilder {}', "
            + "'!BuilderSession {"
//            + "  [ aList,               [ 1, 2 ]    ],"
//            + "  [ aSingularList,       [ 1, 2 ]    ],"
//            + "  [ anInitList,          [ 1, 2 ]    ],"
//            + "  [ anInitSingularList,  [ 1, 2 ]    ],"
//            + "  [ anInitDefaultList,   [ 1, 2 ]    ],"
//            + "  [ aFinalList,          [ 1, 2 ]    ],"
//            + "  [ aFinalSingularList,  [ 1, 2 ]    ],"
//            + "  [ aFinalDefaultList,   [ 1, 2 ]    ],"
            + " }', "
            + "'!Checks {"
//            + "  [ aList,               ArrayList,                      [ 1, 2 ]    ],"
//            + "  [ aSingularList,       UnmodifiableRandomAccessList,   [ 1, 2 ]    ],"
//            + "  [ anInitList,          ArrayList,                      [ 1, 2 ]    ],"
//            + "  [ anInitSingularList,  UnmodifiableRandomAccessList,   [ 0, 1, 2 ] ],"
//            + "  [ anInitDefaultList,   ArrayList,                      [ 1, 2 ]    ],"
//            + "  [ aFinalList,          ArrayList,                      [ 1, 2 ]    ],"
//            + "  [ aFinalSingularList,  UnmodifiableRandomAccessList,   [ 0, 1, 2 ] ],"
//            + "  [ aFinalDefaultList,   ArrayList,                      [ 1, 2 ]    ],"
            + " }'",

    })
    void test(String dataSource, String builderSessionSource, String checksSource)
        throws Exception {

        Object origData = !dataSource.isEmpty()
            ? requireNonNull(Marshallable.fromString(dataSource))
            : null;
        BuilderSession builderSession = !builderSessionSource.isEmpty()
            ? requireNonNull(Marshallable.fromString(BuilderSession.class, builderSessionSource))
            : null;
        Checks checks = requireNonNull(Marshallable.fromString(Checks.class, checksSource));

        Object data;
        if (builderSession != null) {
            Object builder = origData.getClass() != Class.class
                ? origData.getClass().getMethod("toBuilder").invoke(origData)
                : ((Class<?>) origData).getMethod("builder").invoke(null);
            builderSession.run(builder);
            data = builder.getClass().getMethod("build").invoke(builder);
        } else {
            data = origData;
        }

        log.info("data checks:\n{}{}", data, checks);
        checks.run(data);
    }
}
