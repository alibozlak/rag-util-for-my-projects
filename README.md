# rag-util-for-my-projects

A small Java library for the retrieval part of RAG (Retrieval-Augmented Generation).

It helps you to:

- check that a text is not too long for an embedding model (token count),
- create an embedding vector for a text with [Ollama](https://ollama.com).

## Requirements

- Java 21
- Maven
- Ollama, running on your machine or on a server
- The `tokenizer.json` file of your embedding model

## Installation

The library is not on Maven Central. First, install it to your local Maven repository.
Run this command in the folder of this library:

```bash
mvn install
```

Then add the dependency to the `pom.xml` of your project:

```xml
<dependency>
    <groupId>dev.bozlak</groupId>
    <artifactId>rag-util-for-my-projects</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## Setup

### 1. Pull the model in Ollama

```bash
ollama pull bge-m3:567m
```

### 2. Put the tokenizer file in your project

The library reads the tokenizer from `models/embedding/tokenizer.json`.

This path starts from the **working directory**, not from the classpath.
So put the file in the root folder of your project, next to `pom.xml`:

```
my-project/
├── pom.xml
├── models/
│   └── embedding/
│       └── tokenizer.json
└── src/
```

Download the tokenizer of bge-m3:

```bash
curl -L --create-dirs -o models/embedding/tokenizer.json https://huggingface.co/BAAI/bge-m3/resolve/main/tokenizer.json
```

> On Windows PowerShell, write `curl.exe` instead of `curl`.

> [!IMPORTANT]
> The tokenizer must be the tokenizer of the embedding model that you use.
> The library does not check this. If the tokenizer is from another model, the token count is wrong.
> Use one embedding model in one project.

## Supported models

| Enum value | Ollama name | Max tokens | Vector length | Quantization | Tokenizer |
|---|---|---|---|---|---|
| `BGE_M3_567M` (default) | `bge-m3:567m` | 8192 | 1024 | F16 | [tokenizer.json](https://huggingface.co/BAAI/bge-m3/resolve/main/tokenizer.json) |

## Usage

The library does not need Spring. But in a Spring or Spring Boot project, create the services as beans in one `@Configuration` class.
A bean is a singleton by default, so Spring creates each service only one time.

```java
import java.io.IOException;
import java.net.http.HttpClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;

import dev.bozlak.rag.retrieval.backends.ollama.EmbeddingVectorService;
import dev.bozlak.rag.retrieval.TokenService;
import dev.bozlak.rag.retrieval.tokenizers.huggingface.HuggingFaceTokenizerDefaultConfigForRetrieval;

@Configuration
public class RagUtilConfig {

    // Spring calls close() when the application stops. This frees the native memory of the tokenizer.
    @Bean
    public TokenService tokenService() throws IOException {
        return new HuggingFaceTokenService(HuggingFaceTokenizerDefaultConfigForRetrieval.huggingFaceTokenizer());
    }

    @Bean
    public EmbeddingVectorService embeddingVectorService() {
        return new OllamaEmbeddingVectorServiceImpl(this.httpClient(), this.gson());
    }

    @Bean
    public Gson gson() {
        return new Gson();
    }

    @Bean 
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }
}
```

Then inject the services into your own classes. Use the interfaces `TokenService` and `EmbeddingVectorService`.
So your code does not depend on the Hugging Face or Ollama classes.

```java
import java.io.IOException;

import org.springframework.stereotype.Service;

import dev.bozlak.rag.retrieval.TokenService;
import dev.bozlak.rag.retrieval.backends.EmbeddingVectorService;

@Service
public class MyRagService {

    private final TokenService tokenService;
    private final EmbeddingVectorService embeddingVectorService;

    public MyRagService(TokenService tokenService, EmbeddingVectorService embeddingVectorService) {
        this.tokenService = tokenService;
        this.embeddingVectorService = embeddingVectorService;
    }

    public float[] createEmbeddingVector(String sentence) throws IOException, InterruptedException {
        // 1. Check the token count first. The embedding service does not do it.
        tokenService.validateTokenCount(sentence);

        // 2. Create the embedding vector.
        return embeddingVectorService.getGeneratedEmbeddingVector(sentence);
    }
}
```

There are two steps:

1. Check the token count with `validateTokenCount`.
   **The embedding service does not check it for you.**
   If the text is too long, you get a `TokenCountTooMuchException`.
2. Create the vector with `getGeneratedEmbeddingVector`.

Some tips:

- Do not create the services with `new` in your methods. The tokenizer reads a big file
  (about 17 MB for bge-m3) and uses native memory. Create it only one time, as a bean.
- Spring calls `close()` of `HuggingFaceTokenService` when the application stops. You do not need to close it yourself.
- If the tokenizer file is not found, the application does not start (`TokenizerJsonFileNotFoundException`).
  So you see the problem early.
- Without Spring, create the services one time with `new` when your application starts.
  Call `close()` of `HuggingFaceTokenService` when the application stops.
- Without an `EmbeddingModel` parameter, the methods use the default model (`BGE_M3_567M`).
- Without a URL parameter, the embedding service uses `http://localhost:11434/api/embed`.

### Another model or another Ollama URL

For example, in `MyRagService`:

```java
EmbeddingModel model = EmbeddingModel.BGE_M3_567M;
String url = "http://192.168.1.20:11434/api/embed";

tokenService.validateTokenCount(model, sentence);
float[] vector = embeddingVectorService.getGeneratedEmbeddingVector(model, sentence, url);
```

The URL must be full. Only a path, like `/api/embed`, does not work.

### Tokenizer in another path

Give the path in the bean method of `RagUtilConfig`:

```java
@Bean
public HuggingFaceTokenService huggingFaceTokenService() throws IOException {
    return new HuggingFaceTokenService(
            HuggingFaceTokenizerDefaultConfigForRetrieval.huggingFaceTokenizer("config/bge-m3/tokenizer.json"));
}
```

A relative path also starts from the working directory.

## Token limit

The limit is the max tokens of the model minus a safety margin of 500 tokens
(`TokenizerDefaultConfig.TOKEN_SAFETY_MARGIN`).

For `bge-m3:567m`, the limit is 8192 - 500 = **7692 tokens**.

Ollama does not cut long text, because the library sends `truncate: false`.
Too long text gives an error. So check the token count before you send the text.
If a text is too long, split it into smaller parts.

## Errors

| Exception | When | What to do |
|---|---|---|
| `TokenCountTooMuchException` | The text has too many tokens. | Split the text into smaller parts. |
| `TokenizerJsonFileNotFoundException` | There is no tokenizer file in the path. | Check the file and the working directory. See [Running your project](#running-your-project). |
| `EmbeddingVectorDidNotGenerateException` | The Ollama status code is not 200, or the answer has no vector. | Read the message. It has the status code and the body of the Ollama answer. For example, the model is not pulled. |
| `IOException` | There is a network problem, Ollama is not running, or there is no answer in 100 seconds. | Check that Ollama is running and that the URL is correct. |
| `IllegalArgumentException` | The URL is only a path, like `/api/embed`. | Give the full URL. |

## Running your project

The tokenizer path starts from the working directory.
So run the Maven commands in the folder that has your `pom.xml`:

| Command | Working directory |
|---|---|
| `mvn exec:java` | The folder where you run `mvn` |
| `mvn spring-boot:run` | The folder of `pom.xml` |
| `mvn test` | The folder of `pom.xml` |

If you start your application in another way (for example with `java -jar`), start it in the root folder of your project too.

> [!NOTE]
> **Multi-module projects:** The tests of each module run in the folder of that module.
> So the library looks for `models/embedding/tokenizer.json` inside the module folder.

### Logging

The tokenizer library (DJL) uses SLF4J. If you see `No SLF4J providers were found`, nothing is broken.
To see the logs, add an SLF4J provider to your project, for example `slf4j-simple`.

## Development

Build the library and run the tests:

```bash
mvn test
```

The tests do not need Ollama or a real tokenizer.
They use a fake Ollama server and a small test tokenizer (`src/test/resources/tokenizer/test-tokenizer.json`).

### Packages

| Package | What is in it |
|---|---|
| `dev.bozlak.rag.retrieval` | `EmbeddingModel`, `TokenService` and the exceptions |
| `dev.bozlak.rag.retrieval.tokenizers.huggingface` | `HuggingFaceTokenService` and the tokenizer config |
| `dev.bozlak.rag.retrieval.backends` | The `EmbeddingVectorService` interface |
| `dev.bozlak.rag.retrieval.backends.ollama` | The Ollama implementation |
| `dev.bozlak.rag.backends.ollama` | The Ollama base URL |
