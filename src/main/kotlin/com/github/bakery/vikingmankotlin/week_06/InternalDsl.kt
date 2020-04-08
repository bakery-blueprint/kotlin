package com.github.bakery.vikingmankotlin.week_06

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import kotlin.properties.Delegates

class Http(private val restTemplate: RestTemplate) {
    private lateinit var url: String
    private lateinit var host: String
    private lateinit var path: String
    private var port: Int = 8080


    fun url(url: String) = this.apply { this.url = url }

    fun post(responseType: Class<out Any>, block: POST.() -> Unit) = restTemplate.postForEntity(
            url,
            //TODO: 아래 더미 HttpEntity 대신에 block 파라미터를 이용해 toHttpEntity()로 HttpEntity를 생성해보기.
            HttpEntity("body", HttpHeaders()),
            responseType
    )
 
    fun host(host: String) = this.apply { this.host = host }
    fun port(port: Int) = this.apply { this.port = port }
    fun path(path: String) = this.apply { this.path = path }

    operator fun invoke(block: Http.() -> Unit) = this.apply(block)

}

class POST : METHOD {
    private val header = HEADER()
    private val body = BODY()

    //TODO: this.body에 적용할 함수를 받아와 실행해주는 메서드를 구현해보기.
    fun body(block: BODY.() -> Unit){
        body.block()
    }

    override fun header(block: HEADER.() -> Unit) {
        header.block()
    }


    override fun toHttpEntity(): HttpEntity<Any> = HttpEntity(body.map, header.httpHeaders)
}

interface METHOD {
    fun header(block: HEADER.() -> Unit)
    fun toHttpEntity(): HttpEntity<Any>
}

class HEADER {
    val httpHeaders = HttpHeaders()

    //TODO: '+=' 연산자를 이용하도록 변경해보기.
    fun put(key: String, value: String) {
        httpHeaders[key] = value
    }

    //TODO: '+=' 연산자를 이용하도록 변경해보기.
    fun put(key: String, mediaType: MediaType) {
        httpHeaders[key] = mediaType.type
    }
}

operator fun HEADER.plusAssign(mediaType: MediaType) {
    this.httpHeaders[HttpHeaders.CONTENT_TYPE] = mediaType.type
}

operator fun HttpHeaders.plusAssign(mediaType: MediaType) {
    this[HttpHeaders.CONTENT_TYPE]?.add(mediaType.type)
}

class BODY {
    val map = mutableMapOf<String, Any>()

    //TODO: 중위 호출 to 로 변경해보기.
    fun put(key: String, value: Any) {
        map[key] = value
    }



}

fun main() {
    val result = Http(RestTemplate())
            .url("localhost:8080/test")
            .post(String::class.java) {
                header {
                    put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                }
                body {
                    put("key", "value")
                    put("what", "say")
                }
            }

    //TODO: 이 테스트코드가 동작할 수 있게 URL 클래스를 새로 만들고, Http 클래스를 변경해보기
    val http = Http(RestTemplate())

    val newResult = http {
                host("localhost")
                port(8080)
                path("/test")
            }
            .post(String::class.java) {
                header {
                    put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                }
                body {
                    "key" to "value"
                    "what" to "say"
                }
            }
}
