package io.github.sefiraat.slimetinker;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.junit.jupiter.api.Test;

class DustOptionsRangeTest {

    private static final Pattern DUST_OPTIONS_PATTERN =
        Pattern.compile("new\\s+(?:Particle\\.)?DustOptions\\s*\\([^;]+,\\s*([0-9]+(?:\\.[0-9]+)?)[fFdD]?\\s*\\)");

    @Test
    void paperApiAllowsSizeFourAndRejectsSizeFive() {
        assertDoesNotThrow(() -> new Particle.DustOptions(Color.RED, 4.0f));
        assertThrows(IllegalArgumentException.class, () -> new Particle.DustOptions(Color.RED, 5.0f));
    }

    @Test
    void allDustOptionsInSourcesStayWithinPaperBounds() throws IOException {
        Path srcMain = Path.of("src/main/java");
        try (Stream<Path> paths = Files.walk(srcMain)) {
            for (Path file : paths.filter(p -> p.toString().endsWith(".java")).toList()) {
                String content = Files.readString(file);
                Matcher matcher = DUST_OPTIONS_PATTERN.matcher(content);
                while (matcher.find()) {
                    float size = Float.parseFloat(matcher.group(1));
                    assertTrue(
                        size >= 0.01f && size <= 4.0f,
                        () -> "Invalid DustOptions size " + size + " in " + file + ": " + matcher.group(0)
                    );
                }
            }
        }
    }
}
