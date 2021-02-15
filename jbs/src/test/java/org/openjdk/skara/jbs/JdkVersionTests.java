/*
 * Copyright (c) 2020, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.openjdk.skara.jbs;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JdkVersionTests {
    private JdkVersion from(String raw) {
        return JdkVersion.parse(raw).orElseThrow();
    }

    @Test
    void jep223() {
        assertEquals(List.of("8"), from("8").components());
        assertEquals(List.of("9", "0", "4"), from("9.0.4").components());
        assertEquals(List.of("10", "0", "2"), from("10.0.2").components());
        assertEquals(List.of("11"), from("11").components());
        assertEquals(List.of("11", "0", "3"), from("11.0.3").components());
        assertEquals(List.of("12", "0", "2"), from("12.0.2").components());
    }

    @Test
    void jep322() {
        assertEquals(List.of("11", "0", "2", "0", "1"), from("11.0.2.0.1-oracle").components());
        assertEquals("oracle", from("11.0.2.0.1-oracle").opt().orElseThrow());
        assertEquals(List.of("11", "0", "3"), from("11.0.3-oracle").components());
        assertEquals("oracle", from("11.0.3-oracle").opt().orElseThrow());
    }

    @Test
    void legacy() {
        assertEquals(List.of("5.0", "45"), from("5.0u45").components());
        assertEquals(List.of("6", "201"), from("6u201").components());
        assertEquals(List.of("7", "40"), from("7u40").components());
        assertEquals(List.of("8", "211"), from("8u211").components());
        assertEquals(List.of("emb-8", "171"), from("emb-8u171").components());
        assertEquals(List.of("hs22", "4"), from("hs22.4").components());
        assertEquals(List.of("hs23"), from("hs23").components());
        assertEquals(List.of("openjdk7"), from("openjdk7").components());
        assertEquals(List.of("openjdk8"), from("openjdk8").components());
        assertEquals(List.of("openjdk8", "211"), from("openjdk8u211").components());
    }

    @Test
    void order() {
        assertEquals(0, from("5.0u45").compareTo(from("5.0u45")));
        assertEquals(0, from("11.0.3").compareTo(from("11.0.3")));
        assertEquals(0, from("11.0.2.0.1-oracle").compareTo(from("11.0.2.0.1-oracle")));

        assertEquals(1, from("6u201").compareTo(from("5.0u45")));
        assertEquals(-1, from("5.0u45").compareTo(from("6u201")));

        assertEquals(-1, from("11.0.2.0.1").compareTo(from("11.0.2.0.1-oracle")));
        assertEquals(1, from("11.0.2.0.1-oracle").compareTo(from("11.0.2.0.1")));

        assertEquals(-1, from("9.0.4").compareTo(from("10.0.2")));
        assertEquals(-1, from("11").compareTo(from("11.0.3")));
        assertEquals(-1, from("emb-8u171").compareTo(from("emb-8u175")));
        assertEquals(-1, from("emb-8u71").compareTo(from("emb-8u170")));
        assertEquals(-1, from("openjdk7").compareTo(from("openjdk7u42")));
        assertEquals(-1, from("hs22.4").compareTo(from("hs23")));
    }

    @Test
    void nonConforming() {
        assertEquals(Optional.empty(), JdkVersion.parse("bla"));
        assertEquals(Optional.empty(), JdkVersion.parse(""));
        assertEquals(Optional.empty(), JdkVersion.parse("12u-cpu"));
        assertEquals(Optional.empty(), JdkVersion.parse("13u-open"));
    }
}