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

package org.screamingsandals.reposilite.webhooks

import com.reposilite.configuration.shared.api.Doc
import com.reposilite.configuration.shared.api.Min
import com.reposilite.configuration.shared.api.SharedSettings

@Doc(title = "Webhooks", description = "Webhooks settings")
data class WebhookSettings(
    @get:Doc(title = "Discord Webhoooks", description = "List of Discord webhooks")
    val discordWebhooks: List<DiscordWebhookSettings> = listOf(),
) : SharedSettings

@Doc(title = "Webhook", description = "Settings for a given webhook")
interface Webhook {
    val id: String

    val webhookUrl: String

    val artifactFilter: List<ArtifactFilter>
}

@Doc(title = "Artifact filter", description = "Settings for a given artifact filter")
data class ArtifactFilter(
    @get:Doc(title = "Repository name", description = "Name of the repository")
    val repositoryName: String = "",
    @get:Doc(title = "Group Regex", description = "Regex for the group of the artifact")
    val groupRegex: String = "",
    @get:Doc(title = "Id Regex", description = "Regex for the id of the artifact")
    val idRegex: String = "",
    @get:Doc(title = "Version Regex", description = "Regex for the version of the artifact")
    val versionRegex: String = "",
) : SharedSettings

@Doc(title = "Discord Webhook", description = "Settings for a Discord webhook")
data class DiscordWebhookSettings(
    @Min(1)
    @get:Doc(title = "Id", description = "Id of the webhook")
    override val id: String = "",
    @Min(1)
    @get:Doc(title = "URL", description = "URL of the webhook")
    override val webhookUrl: String = "",
    @get:Doc(title = "Artifact filter", description = "Filters artifacts for this webhook")
    override val artifactFilter: List<ArtifactFilter> = listOf(),

    @Min(1)
    @get:Doc(title = "Embed title", description = "Title of the embed")
    val embedTitle: String = "",
    @get:Doc(title = "Embed description", description = "Description of the embed")
    val embedDescription: String = "The version `\${version}` of `\${groupId}:\${artifactId}` has been published by \${username}.",
    @get:Doc(title = "Embed link", description = "Link of the embed")
    val embedLink: String = "",
    @get:Doc(title = "Embed footer text", description = "Footer text of the embed")
    val embedFooterText: String = "",
    @get:Doc(title = "Embed fields", description = "List of embed fields")
    val embedFields: List<EmbedSettings> = listOf(
        EmbedSettings("Gradle Groovy", """
        ```gradle
        implementation '$\{groupId}:$\{artifactId}:$\{version}'
        ```
        """.trimIndent()),
        EmbedSettings("Maven", """
        ```xml
        <dependency>
            <groupId>$\{groupId}</groupId>
            <artifactId>$\{artifactId}</artifactId>
            <version>$\{version}</version>
        </dependency>
        ```
        """.trimIndent())
    ),
    @get:Doc(title = "Embed color", description = "Hex color code of the embed left border")
    val embedColor: String = "49BE25",
) : Webhook

@Doc(title = "Embed field", description = "Settings for an embed field")
data class EmbedSettings(
    @get:Doc(title = "Field name", description = "Title of the field")
    val name: String = "",
    @get:Doc(title = "Field value", description = "Content of the field")
    val value: String = "",
    @get:Doc(title = "Inline", description = "Whether or not should the field be inline")
    val inline: Boolean = false,
    ) : SharedSettings