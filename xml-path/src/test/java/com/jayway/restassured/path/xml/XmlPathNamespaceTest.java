/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jayway.restassured.path.xml;

import org.junit.Test;

import static com.jayway.restassured.path.xml.config.XmlPathConfig.xmlPathConfig;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertThat;

public class XmlPathNamespaceTest {

    @Test public void
    xml_path_supports_namespaces_when_declared_correctly() {
        // Given
        String xml = "<foo xmlns:ns=\"http://localhost/\">\n" +
                "      <bar>sudo </bar>\n" +
                "      <ns:bar>make me a sandwich!</ns:bar>\n" +
                "    </foo>";

        // When
        XmlPath xmlPath = new XmlPath(xml).using(xmlPathConfig().declaredNamespace("ns", "http://localhost/"));

        // Then
        assertThat(xmlPath.getString("foo.bar.text()"), equalTo("sudo make me a sandwich!"));
        assertThat(xmlPath.getString(":foo.:bar.text()"), equalTo("sudo "));
        assertThat(xmlPath.getString(":foo.ns:bar.text()"), equalTo("make me a sandwich!"));
    }

    @Test public void
    xml_path_doesnt_support_namespaces_when_not_declared() {
        // Given
        String xml = "<foo xmlns:ns=\"http://localhost/\">\n" +
                "      <bar>sudo </bar>\n" +
                "      <ns:bar>make me a sandwich!</ns:bar>\n" +
                "    </foo>";

        // When
        XmlPath xmlPath = new XmlPath(xml);

        // Then
        assertThat(xmlPath.getString("foo.bar.text()"), equalTo("sudo make me a sandwich!"));
        assertThat(xmlPath.getString(":foo.:bar.text()"), equalTo("sudo "));
        assertThat(xmlPath.getString(":foo.ns:bar.text()"), equalTo(""));
    }

    @Test public void
    xml_path_supports_declared_namespaces() {
        // Given
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<x:response xmlns:x=\"http://something.com/test\" note=\"something\">\n" +
                "    <x:container cont_id=\"some_id\">\n" +
                "        <x:item id=\"i_1\">\n" +
                "            <x:name>first</x:name>\n" +
                "        </x:item>\n" +
                "        <x:item id=\"i_2\">\n" +
                "            <x:name>second</x:name>\n" +
                "        </x:item>\n" +
                "        <x:item id=\"i_3\">\n" +
                "            <x:name>third</x:name>\n" +
                "        </x:item>\n" +
                "        <item id=\"i_4\">\n" +
                "            <name>fourth</name>\n" +
                "        </item>\n" +
                "    </x:container>\n" +
                "</x:response>";

        // When
        XmlPath xmlPath = new XmlPath(xml).using(xmlPathConfig().declaredNamespace("x", "http://something.com/test"));

        // Then
        assertThat(xmlPath.getString("x:response.'x:container'.'x:item'[0].x:name"), equalTo("first"));
        assertThat(xmlPath.getString("response.container.':item'[0].name"), equalTo("fourth"));
        assertThat(xmlPath.getString("x:response.'x:container'.'x:item'[3].x:name"), isEmptyOrNullString());
        assertThat(xmlPath.getString("'x:response'.'x:container'.'x:item'[3].x:name"), isEmptyOrNullString());
    }

    @Test public void
    xml_path_supports_declared_namespaces_without_manual_escaping() {
        // Given
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<x:response xmlns:x=\"http://something.com/test\" note=\"something\">\n" +
                "    <x:container cont_id=\"some_id\">\n" +
                "        <x:item id=\"i_1\">\n" +
                "            <x:name>first</x:name>\n" +
                "        </x:item>\n" +
                "        <x:item id=\"i_2\">\n" +
                "            <x:name>second</x:name>\n" +
                "        </x:item>\n" +
                "        <x:item id=\"i_3\">\n" +
                "            <x:name>third</x:name>\n" +
                "        </x:item>\n" +
                "        <item id=\"i_4\">\n" +
                "            <name>fourth</name>\n" +
                "        </item>\n" +
                "    </x:container>\n" +
                "</x:response>";

        // When
        XmlPath xmlPath = new XmlPath(xml).using(xmlPathConfig().declaredNamespace("x", "http://something.com/test"));

        // Then
        assertThat(xmlPath.getString("x:response.x:container.x:item[0].x:name"), equalTo("first"));
        assertThat(xmlPath.getString("response.container.:item[0].name"), equalTo("fourth"));
        assertThat(xmlPath.getString("x:response.x:container.x:item[3].x:name"), isEmptyOrNullString());
        assertThat(xmlPath.getString("'x:response'.x:container.x:item[3].x:name"), isEmptyOrNullString());
    }
}
