package net.hollowcube.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;

public class TestJsonUtil {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static Stream<Arguments> mergeTestData() {
        return Stream.of(
                test("null right", """
                        {
                            "a": 1
                        }""", null, """
                        {
                            "a": 1
                        }"""),
                test("null left", null, """
                        {
                            "a": 1
                        }""", """
                        {
                            "a": 1
                        }"""),
                test("merge top level objects", """
                        {
                            "a": 1,
                            "b": 1
                        }""", """
                        {
                            "a": 2,
                            "c": 1
                        }""", """
                        {
                            "a": 2,
                            "b": 1,
                            "c": 1
                        }"""),
                test("merge sub objects", """
                        {
                            "obj": {
                                "a": 1,
                                "b": 1
                            }
                        }""", """
                        {
                            "obj": {
                                "a": 2,
                                "c": 1
                            }
                        }""", """
                        {
                            "obj": {
                                "a": 2,
                                "b": 1,
                                "c": 1
                            }
                        }"""),
                // Note the extra "a" in the result because there is no type field to merge on
                test("merge array simple", """
                        [
                            "a",
                            "b"
                        ]""", """
                        [
                            "a",
                            "c"
                        ]""", """
                        [
                            "a",
                            "b",
                            "a",
                            "c"
                        ]"""),
                test("merge array complex", """
                        [
                            {
                                "type": "a",
                                "a": 1
                            },
                            {
                                "type": "b",
                                "a": 1
                            }
                        ]""", """
                        [
                            {
                                "type": "a",
                                "a": 2,
                                "b": 1
                            }
                        ]""", """
                        [
                            {
                                "type": "a",
                                "a": 2,
                                "b": 1
                            },
                            {
                                "type": "b",
                                "a": 1
                            }
                        ]""")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("mergeTestData")
    public void testMergeNullRight(String name, String leftStr, String rightStr, String mergedStr) {
        // Parse and re stringify result to ensure formatting is the same. We just want to compare content with a readable error.
        JsonElement left = null, right = null, merged = JsonParser.parseString(mergedStr);
        if (leftStr != null) left = JsonParser.parseString(leftStr);
        if (rightStr != null) right = JsonParser.parseString(rightStr);

        JsonElement result = JsonUtil.merge(left, right);
        String resultStr = gson.toJson(result);

        assertThat(resultStr).isEqualTo(gson.toJson(merged));
    }

    private static Arguments test(String name, @Language("JSON") String left, @Language("JSON") String right, @Language("JSON") String merged) {
        return Arguments.of(name, left, right, merged);
    }


}
