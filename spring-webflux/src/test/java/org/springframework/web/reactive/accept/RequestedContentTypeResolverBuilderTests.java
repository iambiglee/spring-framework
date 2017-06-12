/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.web.reactive.accept;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link RequestedContentTypeResolverBuilder}.
 * @author Rossen Stoyanchev
 */
public class RequestedContentTypeResolverBuilderTests {

	@Test
	public void defaultSettings() throws Exception {

		RequestedContentTypeResolver resolver = new RequestedContentTypeResolverBuilder().build();
		ServerWebExchange exchange = MockServerHttpRequest.get("/flower").accept(MediaType.IMAGE_GIF).toExchange();
		List<MediaType> mediaTypes = resolver.resolveMediaTypes(exchange);

		assertEquals(Collections.singletonList(MediaType.IMAGE_GIF), mediaTypes);
	}

	@Test
	public void parameterResolver() throws Exception {

		RequestedContentTypeResolverBuilder builder = new RequestedContentTypeResolverBuilder();
		builder.parameterResolver().mediaType("json", MediaType.APPLICATION_JSON);
		RequestedContentTypeResolver resolver = builder.build();

		ServerWebExchange exchange = MockServerHttpRequest.get("/flower?format=json").toExchange();
		List<MediaType> mediaTypes = resolver.resolveMediaTypes(exchange);

		assertEquals(Collections.singletonList(MediaType.APPLICATION_JSON), mediaTypes);
	}

	@Test
	public void parameterResolverWithCustomParamName() throws Exception {

		RequestedContentTypeResolverBuilder builder = new RequestedContentTypeResolverBuilder();
		builder.parameterResolver().mediaType("json", MediaType.APPLICATION_JSON).parameterName("s");
		RequestedContentTypeResolver resolver = builder.build();

		ServerWebExchange exchange = MockServerHttpRequest.get("/flower?s=json").toExchange();
		List<MediaType> mediaTypes = resolver.resolveMediaTypes(exchange);

		assertEquals(Collections.singletonList(MediaType.APPLICATION_JSON), mediaTypes);
	}

	@Test // SPR-10513
	public void fixedResolver() throws Exception {

		RequestedContentTypeResolverBuilder builder = new RequestedContentTypeResolverBuilder();
		builder.fixedResolver(MediaType.APPLICATION_JSON);
		RequestedContentTypeResolver resolver = builder.build();

		ServerWebExchange exchange = MockServerHttpRequest.get("/").accept(MediaType.ALL).toExchange();
		List<MediaType> mediaTypes = resolver.resolveMediaTypes(exchange);

		assertEquals(Collections.singletonList(MediaType.APPLICATION_JSON), mediaTypes);
	}

	@Test // SPR-12286
	public void resolver() throws Exception {

		RequestedContentTypeResolverBuilder builder = new RequestedContentTypeResolverBuilder();
		builder.resolver(new FixedContentTypeResolver(MediaType.APPLICATION_JSON));
		RequestedContentTypeResolver resolver = builder.build();

		ServerWebExchange exchange = MockServerHttpRequest.get("/").toExchange();
		List<MediaType> mediaTypes = resolver.resolveMediaTypes(exchange);
		assertEquals(Collections.singletonList(MediaType.APPLICATION_JSON), mediaTypes);

		exchange = MockServerHttpRequest.get("/").accept(MediaType.ALL).toExchange();
		mediaTypes = resolver.resolveMediaTypes(exchange);
		assertEquals(Collections.singletonList(MediaType.APPLICATION_JSON), mediaTypes);
	}

}