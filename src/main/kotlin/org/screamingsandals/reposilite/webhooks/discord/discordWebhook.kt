/*
 * Copyright 2025 ScreamingSandals
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.screamingsandals.reposilite.webhooks.discord

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.screamingsandals.reposilite.webhooks.DeploymentInformation
import org.screamingsandals.reposilite.webhooks.DiscordWebhookSettings
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Instant

@OptIn(ExperimentalStdlibApi::class)
fun sendDiscordWebhook(settings: DiscordWebhookSettings, deployment: DeploymentInformation) {
    fun String.resolve(): String? = if (isEmpty()) null else deployment.resolvePlaceholders(this)

    val payload = Webhook(
        embeds = listOf(
            Embed(
                title = settings.embedTitle.resolve(),
                description = settings.embedDescription.resolve(),
                color = settings.embedColor.hexToInt(),
                url = settings.embedLink.resolve(),
                timestamp = Instant.now().toString(),
                footer = settings.embedFooterText.resolve()?.let { EmbedFooter(it) },
                fields = settings.embedFields.map {
                    EmbedField(
                        name = it.name.resolve() ?: "",
                        value = it.value.resolve() ?: "",
                        inline = it.inline,
                    )
                },
            )
        )
    )
    val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())
    val jsonPayload = objectMapper.writeValueAsString(payload)

    val client = HttpClient.newHttpClient()
    val request = HttpRequest.newBuilder()
        .uri(URI.create(settings.webhookUrl))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
        .build()

    val response = client.send(request, HttpResponse.BodyHandlers.ofString())

    if (response.statusCode() != 200 && response.statusCode() != 204) {
        throw Exception("Unexpected response: ${response.statusCode()}")
    }
}

data class Webhook(
    val embeds: List<Embed> = listOf(),
)

data class Embed(
    val title: String? = null,
    val description: String? = null,
    val url: String? = null,
    val timestamp: String? = null,
    val color: Int? = null,
    val footer: EmbedFooter? = null,
    val fields: List<EmbedField> = listOf(),
)

data class EmbedField(
    val name: String,
    val value: String,
    val inline: Boolean,
)

data class EmbedFooter(
    val text: String,
)