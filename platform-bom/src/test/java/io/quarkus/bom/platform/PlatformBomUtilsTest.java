package io.quarkus.bom.platform;

import static org.assertj.core.api.Assertions.assertThat;

import io.quarkus.maven.dependency.ArtifactKey;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class PlatformBomUtilsTest {

    @Test
    public void groupIdThatIsPrefixOfAnotherGroupIdSortsFirst() {
        // com.fasterxml is a proper prefix of com.fasterxml.jackson.core; plain field-by-field
        // comparison ranks the shorter one first.
        var classmate = ArtifactKey.gact("com.fasterxml", "classmate", "", null);
        var jacksonAnnotations = ArtifactKey.gact("com.fasterxml.jackson.core", "jackson-annotations", "", null);

        assertSortsInOrder(List.of(jacksonAnnotations, classmate), List.of(classmate, jacksonAnnotations));
    }

    @Test
    public void artifactIdThatIsPrefixOfAnotherArtifactIdSortsFirst() {
        // netty-transport is a proper prefix of netty-transport-rxtx; same as the groupId case, one
        // field over.
        var plain = ArtifactKey.gact("io.netty", "netty-transport", "", "jar");
        var rxtx = ArtifactKey.gact("io.netty", "netty-transport-rxtx", "", "jar");

        assertSortsInOrder(List.of(rxtx, plain), List.of(plain, rxtx));
    }

    @Test
    public void noClassifierSortsAfterClassifiedSiblings() {
        var plain = ArtifactKey.gact("org.acme", "foo", "", "jar");
        var sources = ArtifactKey.gact("org.acme", "foo", "sources", "jar");
        var javadoc = ArtifactKey.gact("org.acme", "foo", "javadoc", "jar");

        assertSortsInOrder(List.of(plain, sources, javadoc), List.of(javadoc, sources, plain));
    }

    @Test
    public void unrelatedGroupIdsStayAlphabetical() {
        var a = ArtifactKey.gact("org.acme", "foo", "", "jar");
        var b = ArtifactKey.gact("org.other", "bar", "", "jar");

        assertSortsInOrder(List.of(a, b), List.of(a, b));
    }

    @Test
    public void differentTypesWithoutAnyClassifierKeepTypeOrder() {
        // no classifier ever appears for this groupId:artifactId, so the group must sort exactly
        // as before: by type, with the untyped (null) variant first.
        var untyped = ArtifactKey.gact("org.acme", "foo", "", null);
        var pomImport = ArtifactKey.gact("org.acme", "foo", "", "pom");
        var testJar = ArtifactKey.gact("org.acme", "foo", "", "test-jar");

        assertSortsInOrder(List.of(testJar, untyped, pomImport), List.of(untyped, pomImport, testJar));
    }

    private static void assertSortsInOrder(List<ArtifactKey> input, List<ArtifactKey> expected) {
        List<ArtifactKey> actual = new ArrayList<>(input);
        PlatformBomUtils.sortConstraints(actual);
        assertThat(actual).isEqualTo(expected);
    }
}
