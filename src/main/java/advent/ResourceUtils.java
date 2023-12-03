package advent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class ResourceUtils {
    public static BufferedReader resourceReader(Class<?> clz, String resourcePath) throws IOException {
        Objects.requireNonNull(clz);
        Objects.requireNonNull(resourcePath);

        InputStream inputStream = clz.getClassLoader().getResourceAsStream(resourcePath);
        Objects.requireNonNull(inputStream);

        return new BufferedReader(new InputStreamReader(inputStream));
    }
}
