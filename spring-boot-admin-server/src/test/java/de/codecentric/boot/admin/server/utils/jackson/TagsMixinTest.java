/*
 * Copyright 2014-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.codecentric.boot.admin.server.utils.jackson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import de.codecentric.boot.admin.server.domain.values.Tags;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class TagsMixinTest {

	private final ObjectMapper objectMapper;

	private JacksonTester<Tags> jsonTester;

	protected TagsMixinTest() {
		AdminServerModule adminServerModule = new AdminServerModule(new String[] { ".*password$" });
		JavaTimeModule javaTimeModule = new JavaTimeModule();
		objectMapper = Jackson2ObjectMapperBuilder.json().modules(adminServerModule, javaTimeModule).build();
	}

	@BeforeEach
	void setup() {
		JacksonTester.initFields(this, objectMapper);
	}

	@Test
	void verifyDeserialize() throws JSONException, JsonProcessingException {
		String json = new JSONObject().put("env", "test").put("foo", "bar").toString();

		Tags tags = objectMapper.readValue(json, Tags.class);
		assertThat(tags).isNotNull();
		assertThat(tags.getValues()).containsOnly(entry("env", "test"), entry("foo", "bar"));
	}

	@Test
	void verifySerialize() throws IOException {
		Map<String, Object> data = new HashMap<>();
		data.put("env", "test");
		data.put("foo", "bar");
		Tags tags = Tags.from(data);

		JsonContent<Tags> jsonContent = jsonTester.write(tags);
		assertThat(jsonContent).extractingJsonPathMapValue("$").containsOnly(entry("env", "test"), entry("foo", "bar"));
	}

}
